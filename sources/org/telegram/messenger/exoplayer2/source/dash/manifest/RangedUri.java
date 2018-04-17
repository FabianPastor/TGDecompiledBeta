package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class RangedUri {
    private int hashCode;
    public final long length;
    private final String referenceUri;
    public final long start;

    public RangedUri(String referenceUri, long start, long length) {
        this.referenceUri = referenceUri == null ? TtmlNode.ANONYMOUS_REGION_ID : referenceUri;
        this.start = start;
        this.length = length;
    }

    public Uri resolveUri(String baseUri) {
        return UriUtil.resolveToUri(baseUri, this.referenceUri);
    }

    public String resolveUriString(String baseUri) {
        return UriUtil.resolve(baseUri, this.referenceUri);
    }

    public RangedUri attemptMerge(RangedUri other, String baseUri) {
        String resolvedUri = resolveUriString(baseUri);
        if (other != null) {
            if (resolvedUri.equals(other.resolveUriString(baseUri))) {
                if (this.length != -1 && this.start + this.length == other.start) {
                    return new RangedUri(resolvedUri, this.start, other.length == -1 ? -1 : this.length + other.length);
                } else if (other.length == -1 || other.start + other.length != this.start) {
                    return null;
                } else {
                    return new RangedUri(resolvedUri, other.start, this.length == -1 ? -1 : other.length + this.length);
                }
            }
        }
        return null;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (31 * ((31 * ((31 * 17) + ((int) this.start))) + ((int) this.length))) + this.referenceUri.hashCode();
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                RangedUri other = (RangedUri) obj;
                if (this.start != other.start || this.length != other.length || !this.referenceUri.equals(other.referenceUri)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }
}
