package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;

public final class HlsMediaPlaylist extends HlsPlaylist {
    public final long durationUs;
    public final boolean hasEndTag;
    public final Segment initializationSegment;
    public final int mediaSequence;
    public final List<Segment> segments;
    public final int version;

    public static final class Segment implements Comparable<Long> {
        public final long byterangeLength;
        public final long byterangeOffset;
        public final int discontinuitySequenceNumber;
        public final long durationUs;
        public final String encryptionIV;
        public final String encryptionKeyUri;
        public final boolean isEncrypted;
        public final long startTimeUs;
        public final String url;

        public Segment(String uri, long byterangeOffset, long byterangeLength) {
            this(uri, 0, -1, C.TIME_UNSET, false, null, null, byterangeOffset, byterangeLength);
        }

        public Segment(String uri, long durationUs, int discontinuitySequenceNumber, long startTimeUs, boolean isEncrypted, String encryptionKeyUri, String encryptionIV, long byterangeOffset, long byterangeLength) {
            this.url = uri;
            this.durationUs = durationUs;
            this.discontinuitySequenceNumber = discontinuitySequenceNumber;
            this.startTimeUs = startTimeUs;
            this.isEncrypted = isEncrypted;
            this.encryptionKeyUri = encryptionKeyUri;
            this.encryptionIV = encryptionIV;
            this.byterangeOffset = byterangeOffset;
            this.byterangeLength = byterangeLength;
        }

        public int compareTo(Long startTimeUs) {
            if (this.startTimeUs > startTimeUs.longValue()) {
                return 1;
            }
            return this.startTimeUs < startTimeUs.longValue() ? -1 : 0;
        }

        public Segment copyWithStartTimeUs(long startTimeUs) {
            return new Segment(this.url, this.durationUs, this.discontinuitySequenceNumber, startTimeUs, this.isEncrypted, this.encryptionKeyUri, this.encryptionIV, this.byterangeOffset, this.byterangeLength);
        }
    }

    public HlsMediaPlaylist(String baseUri, int mediaSequence, int version, boolean hasEndTag, Segment initializationSegment, List<Segment> segments) {
        super(baseUri, 1);
        this.mediaSequence = mediaSequence;
        this.version = version;
        this.hasEndTag = hasEndTag;
        this.initializationSegment = initializationSegment;
        this.segments = Collections.unmodifiableList(segments);
        if (segments.isEmpty()) {
            this.durationUs = 0;
            return;
        }
        Segment last = (Segment) segments.get(segments.size() - 1);
        this.durationUs = (last.startTimeUs + last.durationUs) - ((Segment) segments.get(0)).startTimeUs;
    }

    public long getStartTimeUs() {
        return this.segments.isEmpty() ? 0 : ((Segment) this.segments.get(0)).startTimeUs;
    }

    public long getEndTimeUs() {
        return getStartTimeUs() + this.durationUs;
    }

    public HlsMediaPlaylist copyWithStartTimeUs(long newStartTimeUs) {
        long startTimeOffsetUs = newStartTimeUs - getStartTimeUs();
        int segmentsSize = this.segments.size();
        List<Segment> newSegments = new ArrayList(segmentsSize);
        for (int i = 0; i < segmentsSize; i++) {
            Segment segment = (Segment) this.segments.get(i);
            newSegments.add(segment.copyWithStartTimeUs(segment.startTimeUs + startTimeOffsetUs));
        }
        return copyWithSegments(newSegments);
    }

    public HlsMediaPlaylist copyWithSegments(List<Segment> segments) {
        return new HlsMediaPlaylist(this.baseUri, this.mediaSequence, this.version, this.hasEndTag, this.initializationSegment, segments);
    }
}
