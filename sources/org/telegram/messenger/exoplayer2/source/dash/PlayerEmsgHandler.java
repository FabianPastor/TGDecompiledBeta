package org.telegram.messenger.exoplayer2.source.dash;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
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

public final class PlayerEmsgHandler implements Callback {
    private static final int EMSG_MANIFEST_EXPIRED = 2;
    private static final int EMSG_MEDIA_PRESENTATION_ENDED = 1;
    private final Allocator allocator;
    private final EventMessageDecoder decoder = new EventMessageDecoder();
    private boolean dynamicMediaPresentationEnded;
    private long expiredManifestPublishTimeUs;
    private final Handler handler = new Handler(this);
    private boolean isWaitingForManifestRefresh;
    private long lastLoadedChunkEndTimeBeforeRefreshUs = C0542C.TIME_UNSET;
    private long lastLoadedChunkEndTimeUs = C0542C.TIME_UNSET;
    private DashManifest manifest;
    private final TreeMap<Long, Long> manifestPublishTimeToExpiryTimeUs = new TreeMap();
    private final PlayerEmsgCallback playerEmsgCallback;
    private boolean released;

    private static final class ManifestExpiryEventInfo {
        public final long eventTimeUs;
        public final long manifestPublishTimeMsInEmsg;

        public ManifestExpiryEventInfo(long j, long j2) {
            this.eventTimeUs = j;
            this.manifestPublishTimeMsInEmsg = j2;
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

        public int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException {
            return this.sampleQueue.sampleData(extractorInput, i, z);
        }

        public void sampleData(ParsableByteArray parsableByteArray, int i) {
            this.sampleQueue.sampleData(parsableByteArray, i);
        }

        public void sampleMetadata(long j, int i, int i2, int i3, CryptoData cryptoData) {
            this.sampleQueue.sampleMetadata(j, i, i2, i3, cryptoData);
            parseAndDiscardSamples();
        }

        public boolean maybeRefreshManifestBeforeLoadingNextChunk(long j) {
            return PlayerEmsgHandler.this.maybeRefreshManifestBeforeLoadingNextChunk(j);
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
                MetadataInputBuffer dequeueSample = dequeueSample();
                if (dequeueSample != null) {
                    long j = dequeueSample.timeUs;
                    EventMessage eventMessage = (EventMessage) PlayerEmsgHandler.this.decoder.decode(dequeueSample).get(0);
                    if (PlayerEmsgHandler.isPlayerEmsgEvent(eventMessage.schemeIdUri, eventMessage.value)) {
                        parsePlayerEmsgEvent(j, eventMessage);
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

        private void parsePlayerEmsgEvent(long j, EventMessage eventMessage) {
            long access$100 = PlayerEmsgHandler.getManifestPublishTimeMsInEmsg(eventMessage);
            if (access$100 != C0542C.TIME_UNSET) {
                if (PlayerEmsgHandler.isMessageSignalingMediaPresentationEnded(eventMessage) != null) {
                    onMediaPresentationEndedMessageEncountered();
                } else {
                    onManifestExpiredMessageEncountered(j, access$100);
                }
            }
        }

        private void onMediaPresentationEndedMessageEncountered() {
            PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(1));
        }

        private void onManifestExpiredMessageEncountered(long j, long j2) {
            PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(2, new ManifestExpiryEventInfo(j, j2)));
        }
    }

    public PlayerEmsgHandler(DashManifest dashManifest, PlayerEmsgCallback playerEmsgCallback, Allocator allocator) {
        this.manifest = dashManifest;
        this.playerEmsgCallback = playerEmsgCallback;
        this.allocator = allocator;
    }

    public void updateManifest(DashManifest dashManifest) {
        this.isWaitingForManifestRefresh = false;
        this.expiredManifestPublishTimeUs = C0542C.TIME_UNSET;
        this.manifest = dashManifest;
        removePreviouslyExpiredManifestPublishTimeValues();
    }

    boolean maybeRefreshManifestBeforeLoadingNextChunk(long j) {
        if (!this.manifest.dynamic) {
            return false;
        }
        boolean z = true;
        if (this.isWaitingForManifestRefresh) {
            return true;
        }
        if (!this.dynamicMediaPresentationEnded) {
            Entry ceilingExpiryEntryForPublishTime = ceilingExpiryEntryForPublishTime(this.manifest.publishTimeMs);
            if (ceilingExpiryEntryForPublishTime == null || ((Long) ceilingExpiryEntryForPublishTime.getValue()).longValue() >= j) {
                z = false;
            } else {
                this.expiredManifestPublishTimeUs = ((Long) ceilingExpiryEntryForPublishTime.getKey()).longValue();
                notifyManifestPublishTimeExpired();
            }
        }
        if (z) {
            maybeNotifyDashManifestRefreshNeeded();
        }
        return z;
    }

    boolean maybeRefreshManifestOnLoadingError(Chunk chunk) {
        if (!this.manifest.dynamic) {
            return false;
        }
        if (this.isWaitingForManifestRefresh) {
            return true;
        }
        chunk = (this.lastLoadedChunkEndTimeUs == C0542C.TIME_UNSET || this.lastLoadedChunkEndTimeUs >= chunk.startTimeUs) ? null : 1;
        if (chunk == null) {
            return false;
        }
        maybeNotifyDashManifestRefreshNeeded();
        return true;
    }

    void onChunkLoadCompleted(Chunk chunk) {
        if (this.lastLoadedChunkEndTimeUs != C0542C.TIME_UNSET || chunk.endTimeUs > this.lastLoadedChunkEndTimeUs) {
            this.lastLoadedChunkEndTimeUs = chunk.endTimeUs;
        }
    }

    public static boolean isPlayerEmsgEvent(String str, String str2) {
        return ("urn:mpeg:dash:event:2012".equals(str) == null || ("1".equals(str2) == null && "2".equals(str2) == null && "3".equals(str2) == null)) ? null : true;
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
                ManifestExpiryEventInfo manifestExpiryEventInfo = (ManifestExpiryEventInfo) message.obj;
                handleManifestExpiredMessage(manifestExpiryEventInfo.eventTimeUs, manifestExpiryEventInfo.manifestPublishTimeMsInEmsg);
                return true;
            default:
                return null;
        }
    }

    private void handleManifestExpiredMessage(long j, long j2) {
        if (!this.manifestPublishTimeToExpiryTimeUs.containsKey(Long.valueOf(j2))) {
            this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(j2), Long.valueOf(j));
        } else if (((Long) this.manifestPublishTimeToExpiryTimeUs.get(Long.valueOf(j2))).longValue() > j) {
            this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(j2), Long.valueOf(j));
        }
    }

    private void handleMediaPresentationEndedMessageEncountered() {
        this.dynamicMediaPresentationEnded = true;
        notifySourceMediaPresentationEnded();
    }

    private Entry<Long, Long> ceilingExpiryEntryForPublishTime(long j) {
        if (this.manifestPublishTimeToExpiryTimeUs.isEmpty()) {
            return 0;
        }
        return this.manifestPublishTimeToExpiryTimeUs.ceilingEntry(Long.valueOf(j));
    }

    private void removePreviouslyExpiredManifestPublishTimeValues() {
        Iterator it = this.manifestPublishTimeToExpiryTimeUs.entrySet().iterator();
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
        if (this.lastLoadedChunkEndTimeBeforeRefreshUs == C0542C.TIME_UNSET || this.lastLoadedChunkEndTimeBeforeRefreshUs != this.lastLoadedChunkEndTimeUs) {
            this.isWaitingForManifestRefresh = true;
            this.lastLoadedChunkEndTimeBeforeRefreshUs = this.lastLoadedChunkEndTimeUs;
            this.playerEmsgCallback.onDashManifestRefreshRequested();
        }
    }

    private static long getManifestPublishTimeMsInEmsg(org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = new java.lang.String;	 Catch:{ ParserException -> 0x000c }
        r2 = r2.messageData;	 Catch:{ ParserException -> 0x000c }
        r0.<init>(r2);	 Catch:{ ParserException -> 0x000c }
        r0 = org.telegram.messenger.exoplayer2.util.Util.parseXsDateTime(r0);	 Catch:{ ParserException -> 0x000c }
        return r0;
    L_0x000c:
        r0 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.dash.PlayerEmsgHandler.getManifestPublishTimeMsInEmsg(org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage):long");
    }

    private static boolean isMessageSignalingMediaPresentationEnded(EventMessage eventMessage) {
        return (eventMessage.presentationTimeUs == 0 && eventMessage.durationMs == 0) ? true : null;
    }
}
