package com.google.android.gms.common.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.internal.zzsj;
import com.google.android.gms.internal.zzsk;
import com.google.android.gms.internal.zzsl;
import java.lang.ref.WeakReference;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public abstract class zza {
    final zza CD;
    protected int CE = 0;
    protected int CF = 0;
    protected boolean CG = false;
    private boolean CH = true;
    private boolean CI = false;
    private boolean CJ = true;

    static final class zza {
        public final Uri uri;

        public zza(Uri uri) {
            this.uri = uri;
        }

        public boolean equals(Object obj) {
            return !(obj instanceof zza) ? false : this == obj ? true : zzz.equal(((zza) obj).uri, this.uri);
        }

        public int hashCode() {
            return zzz.hashCode(this.uri);
        }
    }

    public static final class zzb extends zza {
        private WeakReference<ImageView> CK;

        public zzb(ImageView imageView, int i) {
            super(null, i);
            com.google.android.gms.common.internal.zzc.zzu(imageView);
            this.CK = new WeakReference(imageView);
        }

        public zzb(ImageView imageView, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.zzc.zzu(imageView);
            this.CK = new WeakReference(imageView);
        }

        private void zza(ImageView imageView, Drawable drawable, boolean z, boolean z2, boolean z3) {
            Object obj = (z2 || z3) ? null : 1;
            if (obj != null && (imageView instanceof zzsk)) {
                int zzauy = ((zzsk) imageView).zzauy();
                if (this.CF != 0 && zzauy == this.CF) {
                    return;
                }
            }
            boolean zzc = zzc(z, z2);
            Drawable zza = zzc ? zza(imageView.getDrawable(), drawable) : drawable;
            imageView.setImageDrawable(zza);
            if (imageView instanceof zzsk) {
                zzsk com_google_android_gms_internal_zzsk = (zzsk) imageView;
                com_google_android_gms_internal_zzsk.zzr(z3 ? this.CD.uri : null);
                com_google_android_gms_internal_zzsk.zzgi(obj != null ? this.CF : 0);
            }
            if (zzc) {
                ((zzsj) zza).startTransition(Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zzb)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            ImageView imageView = (ImageView) this.CK.get();
            ImageView imageView2 = (ImageView) ((zzb) obj).CK.get();
            boolean z = (imageView2 == null || imageView == null || !zzz.equal(imageView2, imageView)) ? false : true;
            return z;
        }

        public int hashCode() {
            return 0;
        }

        protected void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
            ImageView imageView = (ImageView) this.CK.get();
            if (imageView != null) {
                zza(imageView, drawable, z, z2, z3);
            }
        }
    }

    public static final class zzc extends zza {
        private WeakReference<OnImageLoadedListener> CL;

        public zzc(OnImageLoadedListener onImageLoadedListener, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.zzc.zzu(onImageLoadedListener);
            this.CL = new WeakReference(onImageLoadedListener);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zzc)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            zzc com_google_android_gms_common_images_zza_zzc = (zzc) obj;
            OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.CL.get();
            OnImageLoadedListener onImageLoadedListener2 = (OnImageLoadedListener) com_google_android_gms_common_images_zza_zzc.CL.get();
            boolean z = onImageLoadedListener2 != null && onImageLoadedListener != null && zzz.equal(onImageLoadedListener2, onImageLoadedListener) && zzz.equal(com_google_android_gms_common_images_zza_zzc.CD, this.CD);
            return z;
        }

        public int hashCode() {
            return zzz.hashCode(this.CD);
        }

        protected void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
            if (!z2) {
                OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.CL.get();
                if (onImageLoadedListener != null) {
                    onImageLoadedListener.onImageLoaded(this.CD.uri, drawable, z3);
                }
            }
        }
    }

    public zza(Uri uri, int i) {
        this.CD = new zza(uri);
        this.CF = i;
    }

    private Drawable zza(Context context, zzsl com_google_android_gms_internal_zzsl, int i) {
        return context.getResources().getDrawable(i);
    }

    protected zzsj zza(Drawable drawable, Drawable drawable2) {
        if (drawable == null) {
            drawable = null;
        } else if (drawable instanceof zzsj) {
            drawable = ((zzsj) drawable).zzauw();
        }
        return new zzsj(drawable, drawable2);
    }

    void zza(Context context, Bitmap bitmap, boolean z) {
        com.google.android.gms.common.internal.zzc.zzu(bitmap);
        zza(new BitmapDrawable(context.getResources(), bitmap), z, false, true);
    }

    void zza(Context context, zzsl com_google_android_gms_internal_zzsl) {
        if (this.CJ) {
            zza(null, false, true, false);
        }
    }

    void zza(Context context, zzsl com_google_android_gms_internal_zzsl, boolean z) {
        Drawable drawable = null;
        if (this.CF != 0) {
            drawable = zza(context, com_google_android_gms_internal_zzsl, this.CF);
        }
        zza(drawable, z, false, false);
    }

    protected abstract void zza(Drawable drawable, boolean z, boolean z2, boolean z3);

    protected boolean zzc(boolean z, boolean z2) {
        return (!this.CH || z2 || z) ? false : true;
    }

    public void zzgg(int i) {
        this.CF = i;
    }
}
