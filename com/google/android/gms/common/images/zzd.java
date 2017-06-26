package com.google.android.gms.common.images;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzc;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public final class zzd extends zza {
    private WeakReference<OnImageLoadedListener> zzaGn;

    public zzd(OnImageLoadedListener onImageLoadedListener, Uri uri) {
        super(uri, 0);
        zzc.zzr(onImageLoadedListener);
        this.zzaGn = new WeakReference(onImageLoadedListener);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof zzd)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        zzd com_google_android_gms_common_images_zzd = (zzd) obj;
        OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.zzaGn.get();
        OnImageLoadedListener onImageLoadedListener2 = (OnImageLoadedListener) com_google_android_gms_common_images_zzd.zzaGn.get();
        return onImageLoadedListener2 != null && onImageLoadedListener != null && zzbe.equal(onImageLoadedListener2, onImageLoadedListener) && zzbe.equal(com_google_android_gms_common_images_zzd.zzaGf, this.zzaGf);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzaGf});
    }

    protected final void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
        if (!z2) {
            OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.zzaGn.get();
            if (onImageLoadedListener != null) {
                onImageLoadedListener.onImageLoaded(this.zzaGf.uri, drawable, z3);
            }
        }
    }
}
