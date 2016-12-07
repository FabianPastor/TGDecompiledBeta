package org.telegram.messenger.exoplayer.dash.mpd;

import android.net.Uri;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.UriUtil;

public final class RangedUri {
    private final String baseUri;
    private int hashCode;
    public final long length;
    private final String referenceUri;
    public final long start;

    public RangedUri(String baseUri, String referenceUri, long start, long length) {
        boolean z = (baseUri == null && referenceUri == null) ? false : true;
        Assertions.checkArgument(z);
        this.baseUri = baseUri;
        this.referenceUri = referenceUri;
        this.start = start;
        this.length = length;
    }

    public Uri getUri() {
        return UriUtil.resolveToUri(this.baseUri, this.referenceUri);
    }

    public String getUriString() {
        return UriUtil.resolve(this.baseUri, this.referenceUri);
    }

    public RangedUri attemptMerge(RangedUri other) {
        RangedUri rangedUri = null;
        long j = -1;
        if (other != null && getUriString().equals(other.getUriString())) {
            String str;
            String str2;
            long j2;
            if (this.length != -1 && this.start + this.length == other.start) {
                str = this.baseUri;
                str2 = this.referenceUri;
                j2 = this.start;
                if (other.length != -1) {
                    j = this.length + other.length;
                }
                rangedUri = new RangedUri(str, str2, j2, j);
            } else if (other.length != -1 && other.start + other.length == this.start) {
                str = this.baseUri;
                str2 = this.referenceUri;
                j2 = other.start;
                if (this.length != -1) {
                    j = other.length + this.length;
                }
                rangedUri = new RangedUri(str, str2, j2, j);
            }
        }
        return rangedUri;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = ((((((int) this.start) + 527) * 31) + ((int) this.length)) * 31) + getUriString().hashCode();
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RangedUri other = (RangedUri) obj;
        if (this.start == other.start && this.length == other.length && getUriString().equals(other.getUriString())) {
            return true;
        }
        return false;
    }
}
