package org.telegram.messenger.exoplayer2.source.dash;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessageDecoder;
import org.telegram.messenger.exoplayer2.source.SampleQueue;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class PlayerEmsgHandler implements Callback {
    private static final int EMSG_MANIFEST_EXPIRED = 2;
    private static final int EMSG_MEDIA_PRESENTATION_ENDED = 1;
    private final Allocator allocator;
    private final EventMessageDecoder decoder = new EventMessageDecoder();
    private boolean dynamicMediaPresentationEnded;
    private long expiredManifestPublishTimeUs;
    private final Handler handler = new Handler(this);
    private boolean isWaitingForManifestRefresh;
    private long lastLoadedChunkEndTimeBeforeRefreshUs = C.TIME_UNSET;
    private long lastLoadedChunkEndTimeUs = C.TIME_UNSET;
    private DashManifest manifest;
    private final TreeMap<Long, Long> manifestPublishTimeToExpiryTimeUs = new TreeMap();
    private final PlayerEmsgCallback playerEmsgCallback;
    private boolean released;

    private static final class ManifestExpiryEventInfo {
        public final long eventTimeUs;
        public final long manifestPublishTimeMsInEmsg;

        public ManifestExpiryEventInfo(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
            this.eventTimeUs = eventTimeUs;
            this.manifestPublishTimeMsInEmsg = manifestPublishTimeMsInEmsg;
        }
    }

    public interface PlayerEmsgCallback {
        void onDashLiveMediaPresentationEndSignalEncountered();

        void onDashManifestPublishTimeExpired(long j);

        void onDashManifestRefreshRequested();
    }

    public final class PlayerTrackEmsgHandler implements TrackOutput {
        private final MetadataInputBuffer buffer = new MetadataInputBuffer();
        private final FormatHolder formatHolder = new FormatHolder();
        private final SampleQueue sampleQueue;

        PlayerTrackEmsgHandler(SampleQueue sampleQueue) {
            this.sampleQueue = sampleQueue;
        }

        public void format(Format format) {
            this.sampleQueue.format(format);
        }

        public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
            return this.sampleQueue.sampleData(input, length, allowEndOfInput);
        }

        public void sampleData(ParsableByteArray data, int length) {
            this.sampleQueue.sampleData(data, length);
        }

        public void sampleMetadata(long timeUs, int flags, int size, int offset, CryptoData encryptionData) {
            this.sampleQueue.sampleMetadata(timeUs, flags, size, offset, encryptionData);
            parseAndDiscardSamples();
        }

        public boolean maybeRefreshManifestBeforeLoadingNextChunk(long presentationPositionUs) {
            return PlayerEmsgHandler.this.maybeRefreshManifestBeforeLoadingNextChunk(presentationPositionUs);
        }

        public void onChunkLoadCompleted(Chunk chunk) {
            PlayerEmsgHandler.this.onChunkLoadCompleted(chunk);
        }

        public boolean maybeRefreshManifestOnLoadingError(Chunk chunk) {
            return PlayerEmsgHandler.this.maybeRefreshManifestOnLoadingError(chunk);
        }

        public void release() {
            this.sampleQueue.reset();
        }

        private void parseAndDiscardSamples() {
            while (this.sampleQueue.hasNextSample()) {
                MetadataInputBuffer inputBuffer = dequeueSample();
                if (inputBuffer != null) {
                    long eventTimeUs = inputBuffer.timeUs;
                    EventMessage eventMessage = (EventMessage) PlayerEmsgHandler.this.decoder.decode(inputBuffer).get(0);
                    if (PlayerEmsgHandler.isPlayerEmsgEvent(eventMessage.schemeIdUri, eventMessage.value)) {
                        parsePlayerEmsgEvent(eventTimeUs, eventMessage);
                    }
                }
            }
            this.sampleQueue.discardToRead();
        }

        private MetadataInputBuffer dequeueSample() {
            this.buffer.clear();
            if (this.sampleQueue.read(this.formatHolder, this.buffer, false, false, 0) != -4) {
                return null;
            }
            this.buffer.flip();
            return this.buffer;
        }

        private void parsePlayerEmsgEvent(long eventTimeUs, EventMessage eventMessage) {
            long manifestPublishTimeMsInEmsg = PlayerEmsgHandler.getManifestPublishTimeMsInEmsg(eventMessage);
            if (manifestPublishTimeMsInEmsg != C.TIME_UNSET) {
                if (PlayerEmsgHandler.isMessageSignalingMediaPresentationEnded(eventMessage)) {
                    onMediaPresentationEndedMessageEncountered();
                } else {
                    onManifestExpiredMessageEncountered(eventTimeUs, manifestPublishTimeMsInEmsg);
                }
            }
        }

        private void onMediaPresentationEndedMessageEncountered() {
            PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(1));
        }

        private void onManifestExpiredMessageEncountered(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
            PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(2, new ManifestExpiryEventInfo(eventTimeUs, manifestPublishTimeMsInEmsg)));
        }
    }

    public PlayerEmsgHandler(DashManifest manifest, PlayerEmsgCallback playerEmsgCallback, Allocator allocator) {
        this.manifest = manifest;
        this.playerEmsgCallback = playerEmsgCallback;
        this.allocator = allocator;
    }

    public void updateManifest(DashManifest newManifest) {
        this.isWaitingForManifestRefresh = false;
        this.expiredManifestPublishTimeUs = C.TIME_UNSET;
        this.manifest = newManifest;
        removePreviouslyExpiredManifestPublishTimeValues();
    }

    boolean maybeRefreshManifestBeforeLoadingNextChunk(long presentationPositionUs) {
        if (!this.manifest.dynamic) {
            return false;
        }
        if (this.isWaitingForManifestRefresh) {
            return true;
        }
        boolean manifestRefreshNeeded = false;
        if (this.dynamicMediaPresentationEnded) {
            manifestRefreshNeeded = true;
        } else {
            Entry<Long, Long> expiredEntry = ceilingExpiryEntryForPublishTime(this.manifest.publishTimeMs);
            if (expiredEntry != null && ((Long) expiredEntry.getValue()).longValue() < presentationPositionUs) {
                this.expiredManifestPublishTimeUs = ((Long) expiredEntry.getKey()).longValue();
                notifyManifestPublishTimeExpired();
                manifestRefreshNeeded = true;
            }
        }
        if (!manifestRefreshNeeded) {
            return manifestRefreshNeeded;
        }
        maybeNotifyDashManifestRefreshNeeded();
        return manifestRefreshNeeded;
    }

    boolean maybeRefreshManifestOnLoadingError(Chunk chunk) {
        if (!this.manifest.dynamic) {
            return false;
        }
        if (this.isWaitingForManifestRefresh) {
            return true;
        }
        boolean isAfterForwardSeek;
        if (this.lastLoadedChunkEndTimeUs == C.TIME_UNSET || this.lastLoadedChunkEndTimeUs >= chunk.startTimeUs) {
            isAfterForwardSeek = false;
        } else {
            isAfterForwardSeek = true;
        }
        if (!isAfterForwardSeek) {
            return false;
        }
        maybeNotifyDashManifestRefreshNeeded();
        return true;
    }

    void onChunkLoadCompleted(Chunk chunk) {
        if (this.lastLoadedChunkEndTimeUs != C.TIME_UNSET || chunk.endTimeUs > this.lastLoadedChunkEndTimeUs) {
            this.lastLoadedChunkEndTimeUs = chunk.endTimeUs;
        }
    }

    public static boolean isPlayerEmsgEvent(String schemeIdUri, String value) {
        return "urn:mpeg:dash:event:2012".equals(schemeIdUri) && ("1".equals(value) || "2".equals(value) || "3".equals(value));
    }

    public PlayerTrackEmsgHandler newPlayerTrackEmsgHandler() {
        return new PlayerTrackEmsgHandler(new SampleQueue(this.allocator));
    }

    public void release() {
        this.released = true;
        this.handler.removeCallbacksAndMessages(null);
    }

    public boolean handleMessage(Message message) {
        if (this.released) {
            return true;
        }
        switch (message.what) {
            case 1:
                handleMediaPresentationEndedMessageEncountered();
                return true;
            case 2:
                ManifestExpiryEventInfo messageObj = message.obj;
                handleManifestExpiredMessage(messageObj.eventTimeUs, messageObj.manifestPublishTimeMsInEmsg);
                return true;
            default:
                return false;
        }
    }

    private void handleManifestExpiredMessage(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
        if (!this.manifestPublishTimeToExpiryTimeUs.containsKey(Long.valueOf(manifestPublishTimeMsInEmsg))) {
            this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(manifestPublishTimeMsInEmsg), Long.valueOf(eventTimeUs));
        } else if (((Long) this.manifestPublishTimeToExpiryTimeUs.get(Long.valueOf(manifestPublishTimeMsInEmsg))).longValue() > eventTimeUs) {
            this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(manifestPublishTimeMsInEmsg), Long.valueOf(eventTimeUs));
        }
    }

    private void handleMediaPresentationEndedMessageEncountered() {
        this.dynamicMediaPresentationEnded = true;
        notifySourceMediaPresentationEnded();
    }

    private Entry<Long, Long> ceilingExpiryEntryForPublishTime(long publishTimeMs) {
        if (this.manifestPublishTimeToExpiryTimeUs.isEmpty()) {
            return null;
        }
        return this.manifestPublishTimeToExpiryTimeUs.ceilingEntry(Long.valueOf(publishTimeMs));
    }

    private void removePreviouslyExpiredManifestPublishTimeValues() {
        Iterator<Entry<Long, Long>> it = this.manifestPublishTimeToExpiryTimeUs.entrySet().iterator();
        while (it.hasNext()) {
            if (((Long) ((Entry) it.next()).getKey()).longValue() < this.manifest.publishTimeMs) {
                it.remove();
            }
        }
    }

    private void notifyManifestPublishTimeExpired() {
        this.playerEmsgCallback.onDashManifestPublishTimeExpired(this.expiredManifestPublishTimeUs);
    }

    private void notifySourceMediaPresentationEnded() {
        this.playerEmsgCallback.onDashLiveMediaPresentationEndSignalEncountered();
    }

    private void maybeNotifyDashManifestRefreshNeeded() {
        if (this.lastLoadedChunkEndTimeBeforeRefreshUs == C.TIME_UNSET || this.lastLoadedChunkEndTimeBeforeRefreshUs != this.lastLoadedChunkEndTimeUs) {
            this.isWaitingForManifestRefresh = true;
            this.lastLoadedChunkEndTimeBeforeRefreshUs = this.lastLoadedChunkEndTimeUs;
            this.playerEmsgCallback.onDashManifestRefreshRequested();
        }
    }

    private static long getManifestPublishTimeMsInEmsg(EventMessage eventMessage) {
        try {
            return Util.parseXsDateTime(new String(eventMessage.messageData));
        } catch (ParserException e) {
            return C.TIME_UNSET;
        }
    }

    private static boolean isMessageSignalingMediaPresentationEnded(EventMessage eventMessage) {
        return eventMessage.presentationTimeUs == 0 && eventMessage.durationMs == 0;
    }
}
