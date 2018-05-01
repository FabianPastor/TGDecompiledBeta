package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.dash.DashSegmentIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.SingleSegmentBase;

public abstract class Representation {
    public static final long REVISION_ID_DEFAULT = -1;
    public final String baseUrl;
    public final String contentId;
    public final Format format;
    public final List<Descriptor> inbandEventStreams;
    private final RangedUri initializationUri;
    public final long presentationTimeOffsetUs;
    public final long revisionId;

    public static class MultiSegmentRepresentation extends Representation implements DashSegmentIndex {
        private final MultiSegmentBase segmentBase;

        public String getCacheKey() {
            return null;
        }

        public DashSegmentIndex getIndex() {
            return this;
        }

        public RangedUri getIndexUri() {
            return null;
        }

        public MultiSegmentRepresentation(String str, long j, Format format, String str2, MultiSegmentBase multiSegmentBase, List<Descriptor> list) {
            super(str, j, format, str2, multiSegmentBase, list);
            this.segmentBase = multiSegmentBase;
        }

        public RangedUri getSegmentUrl(int i) {
            return this.segmentBase.getSegmentUrl(this, i);
        }

        public int getSegmentNum(long j, long j2) {
            return this.segmentBase.getSegmentNum(j, j2);
        }

        public long getTimeUs(int i) {
            return this.segmentBase.getSegmentTimeUs(i);
        }

        public long getDurationUs(int i, long j) {
            return this.segmentBase.getSegmentDurationUs(i, j);
        }

        public int getFirstSegmentNum() {
            return this.segmentBase.getFirstSegmentNum();
        }

        public int getSegmentCount(long j) {
            return this.segmentBase.getSegmentCount(j);
        }

        public boolean isExplicit() {
            return this.segmentBase.isExplicit();
        }
    }

    public static class SingleSegmentRepresentation extends Representation {
        private final String cacheKey;
        public final long contentLength;
        private final RangedUri indexUri;
        private final SingleSegmentIndex segmentIndex;
        public final Uri uri;

        public static SingleSegmentRepresentation newInstance(String str, long j, Format format, String str2, long j2, long j3, long j4, long j5, List<Descriptor> list, String str3, long j6) {
            return new SingleSegmentRepresentation(str, j, format, str2, new SingleSegmentBase(new RangedUri(null, j2, (j3 - j2) + 1), 1, 0, j4, (j5 - j4) + 1), list, str3, j6);
        }

        public SingleSegmentRepresentation(String str, long j, Format format, String str2, SingleSegmentBase singleSegmentBase, List<Descriptor> list, String str3, long j2) {
            String str4;
            String str5 = str;
            super(str5, j, format, str2, singleSegmentBase, list);
            this.uri = Uri.parse(str2);
            this.indexUri = singleSegmentBase.getIndex();
            SingleSegmentIndex singleSegmentIndex = null;
            if (str3 != null) {
                str4 = str3;
            } else if (str5 != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str5);
                stringBuilder.append(".");
                stringBuilder.append(format.id);
                stringBuilder.append(".");
                stringBuilder.append(j);
                str4 = stringBuilder.toString();
            } else {
                str4 = null;
            }
            r9.cacheKey = str4;
            long j3 = j2;
            r9.contentLength = j3;
            if (r9.indexUri == null) {
                singleSegmentIndex = new SingleSegmentIndex(new RangedUri(null, 0, j3));
            }
            r9.segmentIndex = singleSegmentIndex;
        }

        public RangedUri getIndexUri() {
            return this.indexUri;
        }

        public DashSegmentIndex getIndex() {
            return this.segmentIndex;
        }

        public String getCacheKey() {
            return this.cacheKey;
        }
    }

    public abstract String getCacheKey();

    public abstract DashSegmentIndex getIndex();

    public abstract RangedUri getIndexUri();

    public static Representation newInstance(String str, long j, Format format, String str2, SegmentBase segmentBase) {
        return newInstance(str, j, format, str2, segmentBase, null);
    }

    public static Representation newInstance(String str, long j, Format format, String str2, SegmentBase segmentBase, List<Descriptor> list) {
        return newInstance(str, j, format, str2, segmentBase, list, null);
    }

    public static Representation newInstance(String str, long j, Format format, String str2, SegmentBase segmentBase, List<Descriptor> list, String str3) {
        SegmentBase segmentBase2 = segmentBase;
        if (segmentBase2 instanceof SingleSegmentBase) {
            return new SingleSegmentRepresentation(str, j, format, str2, (SingleSegmentBase) segmentBase2, list, str3, -1);
        } else if (segmentBase2 instanceof MultiSegmentBase) {
            return new MultiSegmentRepresentation(str, j, format, str2, (MultiSegmentBase) segmentBase2, list);
        } else {
            throw new IllegalArgumentException("segmentBase must be of type SingleSegmentBase or MultiSegmentBase");
        }
    }

    private Representation(String str, long j, Format format, String str2, SegmentBase segmentBase, List<Descriptor> list) {
        this.contentId = str;
        this.revisionId = j;
        this.format = format;
        this.baseUrl = str2;
        if (list == null) {
            str = Collections.emptyList();
        } else {
            str = Collections.unmodifiableList(list);
        }
        this.inbandEventStreams = str;
        this.initializationUri = segmentBase.getInitialization(this);
        this.presentationTimeOffsetUs = segmentBase.getPresentationTimeOffsetUs();
    }

    public RangedUri getInitializationUri() {
        return this.initializationUri;
    }
}
