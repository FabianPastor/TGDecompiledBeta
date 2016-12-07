package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.util.List;
import org.telegram.messenger.exoplayer2.C;

public final class HlsMediaPlaylist extends HlsPlaylist {
    public static final String ENCRYPTION_METHOD_AES_128 = "AES-128";
    public static final String ENCRYPTION_METHOD_NONE = "NONE";
    public final long durationUs;
    public final Segment initializationSegment;
    public final boolean live;
    public final int mediaSequence;
    public final List<Segment> segments;
    public final int targetDurationSecs;
    public final int version;

    public static final class Segment implements Comparable<Long> {
        public final long byterangeLength;
        public final long byterangeOffset;
        public final int discontinuitySequenceNumber;
        public final double durationSecs;
        public final String encryptionIV;
        public final String encryptionKeyUri;
        public final boolean isEncrypted;
        public final long startTimeUs;
        public final String url;

        public Segment(String uri, long byterangeOffset, long byterangeLength) {
            this(uri, 0.0d, -1, C.TIME_UNSET, false, null, null, byterangeOffset, byterangeLength);
        }

        public Segment(String uri, double durationSecs, int discontinuitySequenceNumber, long startTimeUs, boolean isEncrypted, String encryptionKeyUri, String encryptionIV, long byterangeOffset, long byterangeLength) {
            this.url = uri;
            this.durationSecs = durationSecs;
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
    }

    public HlsMediaPlaylist(String baseUri, int mediaSequence, int targetDurationSecs, int version, boolean live, Segment initializationSegment, List<Segment> segments) {
        super(baseUri, 1);
        this.mediaSequence = mediaSequence;
        this.targetDurationSecs = targetDurationSecs;
        this.version = version;
        this.live = live;
        this.initializationSegment = initializationSegment;
        this.segments = segments;
        if (segments.isEmpty()) {
            this.durationUs = 0;
            return;
        }
        Segment last = (Segment) segments.get(segments.size() - 1);
        this.durationUs = last.startTimeUs + ((long) (last.durationSecs * 1000000.0d));
    }
}
