package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class RangedUri {
    private int hashCode;
    public final long length;
    private final String referenceUri;
    public final long start;

    public RangedUri(String str, long j, long j2) {
        if (str == null) {
            str = TtmlNode.ANONYMOUS_REGION_ID;
        }
        this.referenceUri = str;
        this.start = j;
        this.length = j2;
    }

    public Uri resolveUri(String str) {
        return UriUtil.resolveToUri(str, this.referenceUri);
    }

    public String resolveUriString(String str) {
        return UriUtil.resolve(str, this.referenceUri);
    }

    public RangedUri attemptMerge(RangedUri rangedUri, String str) {
        String resolveUriString = resolveUriString(str);
        if (rangedUri != null) {
            if (resolveUriString.equals(rangedUri.resolveUriString(str)) != null) {
                long j = -1;
                long j2;
                if (this.length != -1 && this.start + this.length == rangedUri.start) {
                    j2 = this.start;
                    if (rangedUri.length != -1) {
                        j = this.length + rangedUri.length;
                    }
                    return new RangedUri(resolveUriString, j2, j);
                } else if (rangedUri.length == -1 || rangedUri.start + rangedUri.length != this.start) {
                    return null;
                } else {
                    j2 = rangedUri.start;
                    if (this.length != -1) {
                        j = rangedUri.length + this.length;
                    }
                    return new RangedUri(resolveUriString, j2, j);
                }
            }
        }
        return null;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (31 * (((527 + ((int) this.start)) * 31) + ((int) this.length))) + this.referenceUri.hashCode();
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
                RangedUri rangedUri = (RangedUri) obj;
                if (this.start != rangedUri.start || this.length != rangedUri.length || this.referenceUri.equals(rangedUri.referenceUri) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }
}
