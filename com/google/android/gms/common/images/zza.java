package com.google.android.gms.common.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.internal.zzrt;
import com.google.android.gms.internal.zzru;
import com.google.android.gms.internal.zzrv;
import java.lang.ref.WeakReference;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public abstract class zza {
    final zza At;
    protected int Au = 0;
    protected int Av = 0;
    protected boolean Aw = false;
    private boolean Ax = true;
    private boolean Ay = false;
    private boolean Az = true;

    static final class zza {
        public final Uri uri;

        public zza(Uri uri) {
            this.uri = uri;
        }

        public boolean equals(Object obj) {
            return !(obj instanceof zza) ? false : this == obj ? true : zzab.equal(((zza) obj).uri, this.uri);
        }

        public int hashCode() {
            return zzab.hashCode(this.uri);
        }
    }

    public static final class zzb extends zza {
        private WeakReference<ImageView> AA;

        public zzb(ImageView imageView, int i) {
            super(null, i);
            com.google.android.gms.common.internal.zzc.zzu(imageView);
            this.AA = new WeakReference(imageView);
        }

        public zzb(ImageView imageView, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.zzc.zzu(imageView);
            this.AA = new WeakReference(imageView);
        }

        private void zza(ImageView imageView, Drawable drawable, boolean z, boolean z2, boolean z3) {
            Object obj = (z2 || z3) ? null : 1;
            if (obj != null && (imageView instanceof zzru)) {
                int zzatp = ((zzru) imageView).zzatp();
                if (this.Av != 0 && zzatp == this.Av) {
                    return;
                }
            }
            boolean zzc = zzc(z, z2);
            Drawable zza = zzc ? zza(imageView.getDrawable(), drawable) : drawable;
            imageView.setImageDrawable(zza);
            if (imageView instanceof zzru) {
                zzru com_google_android_gms_internal_zzru = (zzru) imageView;
                com_google_android_gms_internal_zzru.zzq(z3 ? this.At.uri : null);
                com_google_android_gms_internal_zzru.zzgj(obj != null ? this.Av : 0);
            }
            if (zzc) {
                ((zzrt) zza).startTransition(Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zzb)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            ImageView imageView = (ImageView) this.AA.get();
            ImageView imageView2 = (ImageView) ((zzb) obj).AA.get();
            boolean z = (imageView2 == null || imageView == null || !zzab.equal(imageView2, imageView)) ? false : true;
            return z;
        }

        public int hashCode() {
            return 0;
        }

        protected void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
            ImageView imageView = (ImageView) this.AA.get();
            if (imageView != null) {
                zza(imageView, drawable, z, z2, z3);
            }
        }
    }

    public static final class zzc extends zza {
        private WeakReference<OnImageLoadedListener> AB;

        public zzc(OnImageLoadedListener onImageLoadedListener, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.zzc.zzu(onImageLoadedListener);
            this.AB = new WeakReference(onImageLoadedListener);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zzc)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            zzc com_google_android_gms_common_images_zza_zzc = (zzc) obj;
            OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.AB.get();
            OnImageLoadedListener onImageLoadedListener2 = (OnImageLoadedListener) com_google_android_gms_common_images_zza_zzc.AB.get();
            boolean z = onImageLoadedListener2 != null && onImageLoadedListener != null && zzab.equal(onImageLoadedListener2, onImageLoadedListener) && zzab.equal(com_google_android_gms_common_images_zza_zzc.At, this.At);
            return z;
        }

        public int hashCode() {
            return zzab.hashCode(this.At);
        }

        protected void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
            if (!z2) {
                OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.AB.get();
                if (onImageLoadedListener != null) {
                    onImageLoadedListener.onImageLoaded(this.At.uri, drawable, z3);
                }
            }
        }
    }

    public zza(Uri uri, int i) {
        this.At = new zza(uri);
        this.Av = i;
    }

    private Drawable zza(Context context, zzrv com_google_android_gms_internal_zzrv, int i) {
        return context.getResources().getDrawable(i);
    }

    protected zzrt zza(Drawable drawable, Drawable drawable2) {
        if (drawable == null) {
            drawable = null;
        } else if (drawable instanceof zzrt) {
            drawable = ((zzrt) drawable).zzatn();
        }
        return new zzrt(drawable, drawable2);
    }

    void zza(Context context, Bitmap bitmap, boolean z) {
        com.google.android.gms.common.internal.zzc.zzu(bitmap);
        zza(new BitmapDrawable(context.getResources(), bitmap), z, false, true);
    }

    void zza(Context context, zzrv com_google_android_gms_internal_zzrv) {
        if (this.Az) {
            zza(null, false, true, false);
        }
    }

    void zza(Context context, zzrv com_google_android_gms_internal_zzrv, boolean z) {
        Drawable drawable = null;
        if (this.Av != 0) {
            drawable = zza(context, com_google_android_gms_internal_zzrv, this.Av);
        }
        zza(drawable, z, false, false);
    }

    protected abstract void zza(Drawable drawable, boolean z, boolean z2, boolean z3);

    protected boolean zzc(boolean z, boolean z2) {
        return (!this.Ax || z2 || z) ? false : true;
    }

    public void zzgh(int i) {
        this.Av = i;
    }
}
