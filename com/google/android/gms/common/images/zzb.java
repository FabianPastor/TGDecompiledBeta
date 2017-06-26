package com.google.android.gms.common.images;

import android.net.Uri;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

final class zzb {
    public final Uri uri;

    public zzb(Uri uri) {
        this.uri = uri;
    }

    public final boolean equals(Object obj) {
        return !(obj instanceof zzb) ? false : this == obj ? true : zzbe.equal(((zzb) obj).uri, this.uri);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.uri});
    }
}
