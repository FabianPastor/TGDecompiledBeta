package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0542C;
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

        public ProtectionElement(UUID uuid, byte[] bArr) {
            this.uuid = uuid;
            this.data = bArr;
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

        public StreamElement(String str, String str2, int i, String str3, long j, String str4, int i2, int i3, int i4, int i5, String str5, Format[] formatArr, List<Long> list, long j2) {
            long j3 = j;
            this(str, str2, i, str3, j3, str4, i2, i3, i4, i5, str5, formatArr, list, Util.scaleLargeTimestamps(list, C0542C.MICROS_PER_SECOND, j3), Util.scaleLargeTimestamp(j2, C0542C.MICROS_PER_SECOND, j3));
        }

        private StreamElement(String str, String str2, int i, String str3, long j, String str4, int i2, int i3, int i4, int i5, String str5, Format[] formatArr, List<Long> list, long[] jArr, long j2) {
            this.baseUri = str;
            this.chunkTemplate = str2;
            this.type = i;
            this.subType = str3;
            this.timescale = j;
            this.name = str4;
            this.maxWidth = i2;
            this.maxHeight = i3;
            this.displayWidth = i4;
            this.displayHeight = i5;
            this.language = str5;
            this.formats = formatArr;
            this.chunkStartTimes = list;
            this.chunkStartTimesUs = jArr;
            this.lastChunkDurationUs = j2;
            this.chunkCount = list.size();
        }

        public StreamElement copy(Format[] formatArr) {
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
            return new StreamElement(str, str2, i, str3, j, str4, i2, i3, i4, i5, str5, formatArr, list2, this.chunkStartTimesUs, this.lastChunkDurationUs);
        }

        public int getChunkIndex(long j) {
            return Util.binarySearchFloor(this.chunkStartTimesUs, j, true, true);
        }

        public long getStartTimeUs(int i) {
            return this.chunkStartTimesUs[i];
        }

        public long getChunkDurationUs(int i) {
            return i == this.chunkCount + -1 ? this.lastChunkDurationUs : this.chunkStartTimesUs[i + 1] - this.chunkStartTimesUs[i];
        }

        public Uri buildRequestUri(int i, int i2) {
            boolean z = false;
            Assertions.checkState(this.formats != null);
            Assertions.checkState(this.chunkStartTimes != null);
            if (i2 < this.chunkStartTimes.size()) {
                z = true;
            }
            Assertions.checkState(z);
            i = Integer.toString(this.formats[i].bitrate);
            i2 = ((Long) this.chunkStartTimes.get(i2)).toString();
            return UriUtil.resolveToUri(this.baseUri, this.chunkTemplate.replace(URL_PLACEHOLDER_BITRATE_1, i).replace(URL_PLACEHOLDER_BITRATE_2, i).replace(URL_PLACEHOLDER_START_TIME_1, i2).replace(URL_PLACEHOLDER_START_TIME_2, i2));
        }
    }

    public SsManifest(int i, int i2, long j, long j2, long j3, int i3, boolean z, ProtectionElement protectionElement, StreamElement[] streamElementArr) {
        int i4 = (j2 > 0 ? 1 : (j2 == 0 ? 0 : -1));
        long j4 = C0542C.TIME_UNSET;
        long scaleLargeTimestamp = i4 == 0 ? C0542C.TIME_UNSET : Util.scaleLargeTimestamp(j2, C0542C.MICROS_PER_SECOND, j);
        if (j3 != 0) {
            j4 = Util.scaleLargeTimestamp(j3, C0542C.MICROS_PER_SECOND, j);
        }
        this(i, i2, scaleLargeTimestamp, j4, i3, z, protectionElement, streamElementArr);
    }

    private SsManifest(int i, int i2, long j, long j2, int i3, boolean z, ProtectionElement protectionElement, StreamElement[] streamElementArr) {
        this.majorVersion = i;
        this.minorVersion = i2;
        this.durationUs = j;
        this.dvrWindowLengthUs = j2;
        this.lookAheadCount = i3;
        this.isLive = z;
        this.protectionElement = protectionElement;
        this.streamElements = streamElementArr;
    }

    public final SsManifest copy(List<TrackKey> list) {
        LinkedList linkedList = new LinkedList(list);
        Collections.sort(linkedList);
        list = new ArrayList();
        List arrayList = new ArrayList();
        StreamElement streamElement = null;
        int i = 0;
        while (i < linkedList.size()) {
            TrackKey trackKey = (TrackKey) linkedList.get(i);
            StreamElement streamElement2 = this.streamElements[trackKey.streamElementIndex];
            if (!(streamElement2 == streamElement || streamElement == null)) {
                list.add(streamElement.copy((Format[]) arrayList.toArray(new Format[0])));
                arrayList.clear();
            }
            arrayList.add(streamElement2.formats[trackKey.trackIndex]);
            i++;
            streamElement = streamElement2;
        }
        if (streamElement != null) {
            list.add(streamElement.copy((Format[]) arrayList.toArray(new Format[0])));
        }
        return new SsManifest(this.majorVersion, this.minorVersion, this.durationUs, this.dvrWindowLengthUs, this.lookAheadCount, this.isLive, this.protectionElement, (StreamElement[]) list.toArray(new StreamElement[0]));
    }
}
