package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;

public final class HlsMediaPlaylist extends HlsPlaylist {
    public static final int PLAYLIST_TYPE_EVENT = 2;
    public static final int PLAYLIST_TYPE_UNKNOWN = 0;
    public static final int PLAYLIST_TYPE_VOD = 1;
    public final int discontinuitySequence;
    public final DrmInitData drmInitData;
    public final long durationUs;
    public final boolean hasDiscontinuitySequence;
    public final boolean hasEndTag;
    public final boolean hasIndependentSegmentsTag;
    public final boolean hasProgramDateTime;
    public final Segment initializationSegment;
    public final int mediaSequence;
    public final int playlistType;
    public final List<Segment> segments;
    public final long startOffsetUs;
    public final long startTimeUs;
    public final long targetDurationUs;
    public final int version;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlaylistType {
    }

    public static final class Segment implements Comparable<Long> {
        public final long byterangeLength;
        public final long byterangeOffset;
        public final long durationUs;
        public final String encryptionIV;
        public final String fullSegmentEncryptionKeyUri;
        public final int relativeDiscontinuitySequence;
        public final long relativeStartTimeUs;
        public final String url;

        public Segment(String str, long j, long j2) {
            this(str, 0, -1, C0542C.TIME_UNSET, null, null, j, j2);
        }

        public Segment(String str, long j, int i, long j2, String str2, String str3, long j3, long j4) {
            this.url = str;
            this.durationUs = j;
            this.relativeDiscontinuitySequence = i;
            this.relativeStartTimeUs = j2;
            this.fullSegmentEncryptionKeyUri = str2;
            this.encryptionIV = str3;
            this.byterangeOffset = j3;
            this.byterangeLength = j4;
        }

        public int compareTo(Long l) {
            if (this.relativeStartTimeUs > l.longValue()) {
                return 1;
            }
            return this.relativeStartTimeUs < l.longValue() ? -1 : null;
        }
    }

    public HlsMediaPlaylist(int i, String str, List<String> list, long j, long j2, boolean z, int i2, int i3, int i4, long j3, boolean z2, boolean z3, boolean z4, DrmInitData drmInitData, Segment segment, List<Segment> list2) {
        long j4;
        super(str, list);
        this.playlistType = i;
        this.startTimeUs = j2;
        this.hasDiscontinuitySequence = z;
        this.discontinuitySequence = i2;
        this.mediaSequence = i3;
        this.version = i4;
        this.targetDurationUs = j3;
        this.hasIndependentSegmentsTag = z2;
        this.hasEndTag = z3;
        this.hasProgramDateTime = z4;
        this.drmInitData = drmInitData;
        this.initializationSegment = segment;
        this.segments = Collections.unmodifiableList(list2);
        if (list2.isEmpty()) {
            r0.durationUs = 0;
        } else {
            Segment segment2 = (Segment) list2.get(list2.size() - 1);
            r0.durationUs = segment2.relativeStartTimeUs + segment2.durationUs;
        }
        if (j == C0542C.TIME_UNSET) {
            j4 = C0542C.TIME_UNSET;
        } else if (j >= 0) {
            j4 = j;
        } else {
            j4 = r0.durationUs + j;
        }
        r0.startOffsetUs = j4;
    }

    public boolean isNewerThan(HlsMediaPlaylist hlsMediaPlaylist) {
        boolean z = true;
        if (hlsMediaPlaylist != null) {
            if (this.mediaSequence <= hlsMediaPlaylist.mediaSequence) {
                if (this.mediaSequence < hlsMediaPlaylist.mediaSequence) {
                    return false;
                }
                int size = this.segments.size();
                int size2 = hlsMediaPlaylist.segments.size();
                if (size <= size2) {
                    if (size != size2 || !this.hasEndTag || hlsMediaPlaylist.hasEndTag != null) {
                        z = false;
                    }
                }
                return z;
            }
        }
        return true;
    }

    public long getEndTimeUs() {
        return this.startTimeUs + this.durationUs;
    }

    public HlsMediaPlaylist copyWith(long j, int i) {
        int i2 = this.playlistType;
        String str = this.baseUri;
        List list = this.tags;
        long j2 = this.startOffsetUs;
        int i3 = this.mediaSequence;
        int i4 = this.version;
        long j3 = this.targetDurationUs;
        boolean z = this.hasIndependentSegmentsTag;
        boolean z2 = this.hasEndTag;
        boolean z3 = this.hasProgramDateTime;
        DrmInitData drmInitData = this.drmInitData;
        DrmInitData drmInitData2 = drmInitData;
        boolean z4 = z3;
        boolean z5 = z2;
        return new HlsMediaPlaylist(i2, str, list, j2, j, true, i, i3, i4, j3, z, z5, z4, drmInitData2, this.initializationSegment, this.segments);
    }

    public HlsMediaPlaylist copyWithEndTag() {
        if (this.hasEndTag) {
            return r0;
        }
        int i = r0.playlistType;
        String str = r0.baseUri;
        List list = r0.tags;
        long j = r0.startOffsetUs;
        long j2 = r0.startTimeUs;
        boolean z = r0.hasDiscontinuitySequence;
        int i2 = r0.discontinuitySequence;
        int i3 = r0.mediaSequence;
        int i4 = r0.version;
        long j3 = r0.targetDurationUs;
        boolean z2 = r0.hasIndependentSegmentsTag;
        long j4 = j3;
        boolean z3 = r0.hasProgramDateTime;
        boolean z4 = z3;
        return new HlsMediaPlaylist(i, str, list, j, j2, z, i2, i3, i4, j4, z2, true, z4, r0.drmInitData, r0.initializationSegment, r0.segments);
    }
}
