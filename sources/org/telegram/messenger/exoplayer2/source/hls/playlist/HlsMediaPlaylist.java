package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0539C;
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

        public Segment(String uri, long byterangeOffset, long byterangeLength) {
            this(uri, 0, -1, C0539C.TIME_UNSET, null, null, byterangeOffset, byterangeLength);
        }

        public Segment(String url, long durationUs, int relativeDiscontinuitySequence, long relativeStartTimeUs, String fullSegmentEncryptionKeyUri, String encryptionIV, long byterangeOffset, long byterangeLength) {
            this.url = url;
            this.durationUs = durationUs;
            this.relativeDiscontinuitySequence = relativeDiscontinuitySequence;
            this.relativeStartTimeUs = relativeStartTimeUs;
            this.fullSegmentEncryptionKeyUri = fullSegmentEncryptionKeyUri;
            this.encryptionIV = encryptionIV;
            this.byterangeOffset = byterangeOffset;
            this.byterangeLength = byterangeLength;
        }

        public int compareTo(Long relativeStartTimeUs) {
            if (this.relativeStartTimeUs > relativeStartTimeUs.longValue()) {
                return 1;
            }
            return this.relativeStartTimeUs < relativeStartTimeUs.longValue() ? -1 : 0;
        }
    }

    public HlsMediaPlaylist(int playlistType, String baseUri, List<String> tags, long startOffsetUs, long startTimeUs, boolean hasDiscontinuitySequence, int discontinuitySequence, int mediaSequence, int version, long targetDurationUs, boolean hasIndependentSegmentsTag, boolean hasEndTag, boolean hasProgramDateTime, DrmInitData drmInitData, Segment initializationSegment, List<Segment> segments) {
        long j;
        super(baseUri, tags);
        this.playlistType = playlistType;
        this.startTimeUs = startTimeUs;
        this.hasDiscontinuitySequence = hasDiscontinuitySequence;
        this.discontinuitySequence = discontinuitySequence;
        this.mediaSequence = mediaSequence;
        this.version = version;
        this.targetDurationUs = targetDurationUs;
        this.hasIndependentSegmentsTag = hasIndependentSegmentsTag;
        this.hasEndTag = hasEndTag;
        this.hasProgramDateTime = hasProgramDateTime;
        this.drmInitData = drmInitData;
        this.initializationSegment = initializationSegment;
        this.segments = Collections.unmodifiableList(segments);
        if (segments.isEmpty()) {
            j = 0;
            r0.durationUs = 0;
        } else {
            Segment last = (Segment) segments.get(segments.size() - 1);
            r0.durationUs = last.relativeStartTimeUs + last.durationUs;
            j = 0;
        }
        long j2 = C0539C.TIME_UNSET;
        if (startOffsetUs != C0539C.TIME_UNSET) {
            j2 = startOffsetUs >= j ? startOffsetUs : r0.durationUs + startOffsetUs;
        }
        r0.startOffsetUs = j2;
    }

    public boolean isNewerThan(HlsMediaPlaylist other) {
        boolean z = true;
        if (other != null) {
            if (this.mediaSequence <= other.mediaSequence) {
                if (this.mediaSequence < other.mediaSequence) {
                    return false;
                }
                int segmentCount = this.segments.size();
                int otherSegmentCount = other.segments.size();
                if (segmentCount <= otherSegmentCount) {
                    if (segmentCount != otherSegmentCount || !this.hasEndTag || other.hasEndTag) {
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

    public HlsMediaPlaylist copyWith(long startTimeUs, int discontinuitySequence) {
        int i = this.playlistType;
        String str = this.baseUri;
        List list = this.tags;
        long j = this.startOffsetUs;
        int i2 = this.mediaSequence;
        int i3 = this.version;
        long j2 = this.targetDurationUs;
        boolean z = this.hasIndependentSegmentsTag;
        boolean z2 = this.hasEndTag;
        boolean z3 = this.hasProgramDateTime;
        DrmInitData drmInitData = this.drmInitData;
        DrmInitData drmInitData2 = drmInitData;
        boolean z4 = z3;
        boolean z5 = z2;
        return new HlsMediaPlaylist(i, str, list, j, startTimeUs, true, discontinuitySequence, i2, i3, j2, z, z5, z4, drmInitData2, this.initializationSegment, this.segments);
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
