package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.dash.DashSegmentIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase;
import org.telegram.messenger.exoplayer2.source.dash.manifest.SegmentBase.SingleSegmentBase;

public abstract class Representation {
    public static final long REVISION_ID_DEFAULT = -1;
    private final String cacheKey;
    public final String contentId;
    public final Format format;
    private final RangedUri initializationUri;
    public final long presentationTimeOffsetUs;
    public final long revisionId;

    public static class MultiSegmentRepresentation extends Representation implements DashSegmentIndex {
        private final MultiSegmentBase segmentBase;

        public MultiSegmentRepresentation(String contentId, long revisionId, Format format, MultiSegmentBase segmentBase, String customCacheKey) {
            super(contentId, revisionId, format, segmentBase, customCacheKey);
            this.segmentBase = segmentBase;
        }

        public RangedUri getIndexUri() {
            return null;
        }

        public DashSegmentIndex getIndex() {
            return this;
        }

        public RangedUri getSegmentUrl(int segmentIndex) {
            return this.segmentBase.getSegmentUrl(this, segmentIndex);
        }

        public int getSegmentNum(long timeUs, long periodDurationUs) {
            return this.segmentBase.getSegmentNum(timeUs, periodDurationUs);
        }

        public long getTimeUs(int segmentIndex) {
            return this.segmentBase.getSegmentTimeUs(segmentIndex);
        }

        public long getDurationUs(int segmentIndex, long periodDurationUs) {
            return this.segmentBase.getSegmentDurationUs(segmentIndex, periodDurationUs);
        }

        public int getFirstSegmentNum() {
            return this.segmentBase.getFirstSegmentNum();
        }

        public int getLastSegmentNum(long periodDurationUs) {
            return this.segmentBase.getLastSegmentNum(periodDurationUs);
        }

        public boolean isExplicit() {
            return this.segmentBase.isExplicit();
        }
    }

    public static class SingleSegmentRepresentation extends Representation {
        public final long contentLength;
        private final RangedUri indexUri;
        private final SingleSegmentIndex segmentIndex;
        public final Uri uri;

        public static SingleSegmentRepresentation newInstance(String contentId, long revisionId, Format format, String uri, long initializationStart, long initializationEnd, long indexStart, long indexEnd, String customCacheKey, long contentLength) {
            return new SingleSegmentRepresentation(contentId, revisionId, format, new SingleSegmentBase(new RangedUri(uri, null, initializationStart, 1 + (initializationEnd - initializationStart)), 1, 0, uri, indexStart, (indexEnd - indexStart) + 1), customCacheKey, contentLength);
        }

        public SingleSegmentRepresentation(String contentId, long revisionId, Format format, SingleSegmentBase segmentBase, String customCacheKey, long contentLength) {
            super(contentId, revisionId, format, segmentBase, customCacheKey);
            this.uri = Uri.parse(segmentBase.uri);
            this.indexUri = segmentBase.getIndex();
            this.contentLength = contentLength;
            this.segmentIndex = this.indexUri != null ? null : new SingleSegmentIndex(new RangedUri(segmentBase.uri, null, 0, contentLength));
        }

        public RangedUri getIndexUri() {
            return this.indexUri;
        }

        public DashSegmentIndex getIndex() {
            return this.segmentIndex;
        }
    }

    public abstract DashSegmentIndex getIndex();

    public abstract RangedUri getIndexUri();

    public static Representation newInstance(String contentId, long revisionId, Format format, SegmentBase segmentBase) {
        return newInstance(contentId, revisionId, format, segmentBase, null);
    }

    public static Representation newInstance(String contentId, long revisionId, Format format, SegmentBase segmentBase, String customCacheKey) {
        if (segmentBase instanceof SingleSegmentBase) {
            return new SingleSegmentRepresentation(contentId, revisionId, format, (SingleSegmentBase) segmentBase, customCacheKey, -1);
        } else if (segmentBase instanceof MultiSegmentBase) {
            return new MultiSegmentRepresentation(contentId, revisionId, format, (MultiSegmentBase) segmentBase, customCacheKey);
        } else {
            throw new IllegalArgumentException("segmentBase must be of type SingleSegmentBase or MultiSegmentBase");
        }
    }

    private Representation(String contentId, long revisionId, Format format, SegmentBase segmentBase, String customCacheKey) {
        this.contentId = contentId;
        this.revisionId = revisionId;
        this.format = format;
        if (customCacheKey == null) {
            customCacheKey = contentId + "." + format.id + "." + revisionId;
        }
        this.cacheKey = customCacheKey;
        this.initializationUri = segmentBase.getInitialization(this);
        this.presentationTimeOffsetUs = segmentBase.getPresentationTimeOffsetUs();
    }

    public RangedUri getInitializationUri() {
        return this.initializationUri;
    }

    public String getCacheKey() {
        return this.cacheKey;
    }
}
