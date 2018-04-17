package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.UriUtil;
import org.telegram.messenger.exoplayer2.util.Util;

public class SsManifest {
    public static final int UNSET_LOOKAHEAD = -1;
    public final long durationUs;
    public final long dvrWindowLengthUs;
    public final boolean isLive;
    public final int lookAheadCount;
    public final int majorVersion;
    public final int minorVersion;
    public final ProtectionElement protectionElement;
    public final StreamElement[] streamElements;

    public static class ProtectionElement {
        public final byte[] data;
        public final UUID uuid;

        public ProtectionElement(UUID uuid, byte[] data) {
            this.uuid = uuid;
            this.data = data;
        }
    }

    public static class StreamElement {
        private static final String URL_PLACEHOLDER_BITRATE_1 = "{bitrate}";
        private static final String URL_PLACEHOLDER_BITRATE_2 = "{Bitrate}";
        private static final String URL_PLACEHOLDER_START_TIME_1 = "{start time}";
        private static final String URL_PLACEHOLDER_START_TIME_2 = "{start_time}";
        private final String baseUri;
        public final int chunkCount;
        private final List<Long> chunkStartTimes;
        private final long[] chunkStartTimesUs;
        private final String chunkTemplate;
        public final int displayHeight;
        public final int displayWidth;
        public final Format[] formats;
        public final String language;
        private final long lastChunkDurationUs;
        public final int maxHeight;
        public final int maxWidth;
        public final String name;
        public final String subType;
        public final long timescale;
        public final int type;

        public StreamElement(String baseUri, String chunkTemplate, int type, String subType, long timescale, String name, int maxWidth, int maxHeight, int displayWidth, int displayHeight, String language, Format[] formats, List<Long> chunkStartTimes, long lastChunkDuration) {
            long j = timescale;
            this(baseUri, chunkTemplate, type, subType, j, name, maxWidth, maxHeight, displayWidth, displayHeight, language, formats, chunkStartTimes, Util.scaleLargeTimestamps(chunkStartTimes, C0539C.MICROS_PER_SECOND, j), Util.scaleLargeTimestamp(lastChunkDuration, C0539C.MICROS_PER_SECOND, j));
        }

        private StreamElement(String baseUri, String chunkTemplate, int type, String subType, long timescale, String name, int maxWidth, int maxHeight, int displayWidth, int displayHeight, String language, Format[] formats, List<Long> chunkStartTimes, long[] chunkStartTimesUs, long lastChunkDurationUs) {
            this.baseUri = baseUri;
            this.chunkTemplate = chunkTemplate;
            this.type = type;
            this.subType = subType;
            this.timescale = timescale;
            this.name = name;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.displayWidth = displayWidth;
            this.displayHeight = displayHeight;
            this.language = language;
            this.formats = formats;
            this.chunkStartTimes = chunkStartTimes;
            this.chunkStartTimesUs = chunkStartTimesUs;
            this.lastChunkDurationUs = lastChunkDurationUs;
            this.chunkCount = chunkStartTimes.size();
        }

        public StreamElement copy(Format[] formats) {
            String str = this.baseUri;
            String str2 = this.chunkTemplate;
            int i = this.type;
            String str3 = this.subType;
            long j = this.timescale;
            String str4 = this.name;
            int i2 = this.maxWidth;
            int i3 = this.maxHeight;
            int i4 = this.displayWidth;
            int i5 = this.displayHeight;
            String str5 = this.language;
            List list = this.chunkStartTimes;
            List list2 = list;
            return new StreamElement(str, str2, i, str3, j, str4, i2, i3, i4, i5, str5, formats, list2, this.chunkStartTimesUs, this.lastChunkDurationUs);
        }

        public int getChunkIndex(long timeUs) {
            return Util.binarySearchFloor(this.chunkStartTimesUs, timeUs, true, true);
        }

        public long getStartTimeUs(int chunkIndex) {
            return this.chunkStartTimesUs[chunkIndex];
        }

        public long getChunkDurationUs(int chunkIndex) {
            return chunkIndex == this.chunkCount + -1 ? this.lastChunkDurationUs : this.chunkStartTimesUs[chunkIndex + 1] - this.chunkStartTimesUs[chunkIndex];
        }

        public Uri buildRequestUri(int track, int chunkIndex) {
            boolean z = false;
            Assertions.checkState(this.formats != null);
            Assertions.checkState(this.chunkStartTimes != null);
            if (chunkIndex < this.chunkStartTimes.size()) {
                z = true;
            }
            Assertions.checkState(z);
            String bitrateString = Integer.toString(this.formats[track].bitrate);
            String startTimeString = ((Long) this.chunkStartTimes.get(chunkIndex)).toString();
            return UriUtil.resolveToUri(this.baseUri, this.chunkTemplate.replace(URL_PLACEHOLDER_BITRATE_1, bitrateString).replace(URL_PLACEHOLDER_BITRATE_2, bitrateString).replace(URL_PLACEHOLDER_START_TIME_1, startTimeString).replace(URL_PLACEHOLDER_START_TIME_2, startTimeString));
        }
    }

    public SsManifest(int majorVersion, int minorVersion, long timescale, long duration, long dvrWindowLength, int lookAheadCount, boolean isLive, ProtectionElement protectionElement, StreamElement[] streamElements) {
        int i = (duration > 0 ? 1 : (duration == 0 ? 0 : -1));
        long j = C0539C.TIME_UNSET;
        long scaleLargeTimestamp = i == 0 ? C0539C.TIME_UNSET : Util.scaleLargeTimestamp(duration, C0539C.MICROS_PER_SECOND, timescale);
        if (dvrWindowLength != 0) {
            j = Util.scaleLargeTimestamp(dvrWindowLength, C0539C.MICROS_PER_SECOND, timescale);
        }
        this(majorVersion, minorVersion, scaleLargeTimestamp, j, lookAheadCount, isLive, protectionElement, streamElements);
    }

    private SsManifest(int majorVersion, int minorVersion, long durationUs, long dvrWindowLengthUs, int lookAheadCount, boolean isLive, ProtectionElement protectionElement, StreamElement[] streamElements) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.durationUs = durationUs;
        this.dvrWindowLengthUs = dvrWindowLengthUs;
        this.lookAheadCount = lookAheadCount;
        this.isLive = isLive;
        this.protectionElement = protectionElement;
        this.streamElements = streamElements;
    }

    public final SsManifest copy(List<TrackKey> trackKeys) {
        SsManifest ssManifest = this;
        LinkedList<TrackKey> sortedKeys = new LinkedList(trackKeys);
        Collections.sort(sortedKeys);
        List<StreamElement> copiedStreamElements = new ArrayList();
        List<Format> copiedFormats = new ArrayList();
        StreamElement currentStreamElement = null;
        for (int i = 0; i < sortedKeys.size(); i++) {
            TrackKey key = (TrackKey) sortedKeys.get(i);
            StreamElement streamElement = ssManifest.streamElements[key.streamElementIndex];
            if (!(streamElement == currentStreamElement || currentStreamElement == null)) {
                copiedStreamElements.add(currentStreamElement.copy((Format[]) copiedFormats.toArray(new Format[0])));
                copiedFormats.clear();
            }
            currentStreamElement = streamElement;
            copiedFormats.add(streamElement.formats[key.trackIndex]);
        }
        if (currentStreamElement != null) {
            copiedStreamElements.add(currentStreamElement.copy((Format[]) copiedFormats.toArray(new Format[0])));
        }
        StreamElement[] copiedStreamElementsArray = (StreamElement[]) copiedStreamElements.toArray(new StreamElement[0]);
        return new SsManifest(ssManifest.majorVersion, ssManifest.minorVersion, ssManifest.durationUs, ssManifest.dvrWindowLengthUs, ssManifest.lookAheadCount, ssManifest.isLive, ssManifest.protectionElement, copiedStreamElementsArray);
    }
}
