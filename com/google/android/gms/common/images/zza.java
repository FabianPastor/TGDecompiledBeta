package com.google.android.gms.common.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzacb;
import com.google.android.gms.internal.zzacc;
import com.google.android.gms.internal.zzacd;
import java.lang.ref.WeakReference;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public abstract class zza {
    private boolean zzaEA = true;
    final zza zzaEu;
    protected int zzaEv = 0;
    protected int zzaEw = 0;
    protected boolean zzaEx = false;
    private boolean zzaEy = true;
    private boolean zzaEz = false;

    static final class zza {
        public final Uri uri;

        public zza(Uri uri) {
            this.uri = uri;
        }

        public boolean equals(Object obj) {
            return !(obj instanceof zza) ? false : this == obj ? true : zzaa.equal(((zza) obj).uri, this.uri);
        }

        public int hashCode() {
            return zzaa.hashCode(this.uri);
        }
    }

    public static final class zzb extends zza {
        private WeakReference<ImageView> zzaEB;

        public zzb(ImageView imageView, int i) {
            super(null, i);
            com.google.android.gms.common.internal.zzc.zzt(imageView);
            this.zzaEB = new WeakReference(imageView);
        }

        public zzb(ImageView imageView, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.zzc.zzt(imageView);
            this.zzaEB = new WeakReference(imageView);
        }

        private void zza(ImageView imageView, Drawable drawable, boolean z, boolean z2, boolean z3) {
            Object obj = (z2 || z3) ? null : 1;
            if (obj != null && (imageView instanceof zzacc)) {
                int zzxu = ((zzacc) imageView).zzxu();
                if (this.zzaEw != 0 && zzxu == this.zzaEw) {
                    return;
                }
            }
            boolean zzc = zzc(z, z2);
            Drawable zza = zzc ? zza(imageView.getDrawable(), drawable) : drawable;
            imageView.setImageDrawable(zza);
            if (imageView instanceof zzacc) {
                zzacc com_google_android_gms_internal_zzacc = (zzacc) imageView;
                com_google_android_gms_internal_zzacc.zzr(z3 ? this.zzaEu.uri : null);
                com_google_android_gms_internal_zzacc.zzcQ(obj != null ? this.zzaEw : 0);
            }
            if (zzc) {
                ((zzacb) zza).startTransition(Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zzb)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            ImageView imageView = (ImageView) this.zzaEB.get();
            ImageView imageView2 = (ImageView) ((zzb) obj).zzaEB.get();
            boolean z = (imageView2 == null || imageView == null || !zzaa.equal(imageView2, imageView)) ? false : true;
            return z;
        }

        public int hashCode() {
            return 0;
        }

        protected void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
            ImageView imageView = (ImageView) this.zzaEB.get();
            if (imageView != null) {
                zza(imageView, drawable, z, z2, z3);
            }
        }
    }

    public static final class zzc extends zza {
        private WeakReference<OnImageLoadedListener> zzaEC;

        public zzc(OnImageLoadedListener onImageLoadedListener, Uri uri) {
            super(uri, 0);
            com.google.android.gms.common.internal.zzc.zzt(onImageLoadedListener);
            this.zzaEC = new WeakReference(onImageLoadedListener);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zzc)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            zzc com_google_android_gms_common_images_zza_zzc = (zzc) obj;
            OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.zzaEC.get();
            OnImageLoadedListener onImageLoadedListener2 = (OnImageLoadedListener) com_google_android_gms_common_images_zza_zzc.zzaEC.get();
            boolean z = onImageLoadedListener2 != null && onImageLoadedListener != null && zzaa.equal(onImageLoadedListener2, onImageLoadedListener) && zzaa.equal(com_google_android_gms_common_images_zza_zzc.zzaEu, this.zzaEu);
            return z;
        }

        public int hashCode() {
            return zzaa.hashCode(this.zzaEu);
        }

        protected void zza(Drawable drawable, boolean z, boolean z2, boolean z3) {
            if (!z2) {
                OnImageLoadedListener onImageLoadedListener = (OnImageLoadedListener) this.zzaEC.get();
                if (onImageLoadedListener != null) {
                    onImageLoadedListener.onImageLoaded(this.zzaEu.uri, drawable, z3);
                }
            }
        }
    }

    public zza(Uri uri, int i) {
        this.zzaEu = new zza(uri);
        this.zzaEw = i;
    }

    private Drawable zza(Context context, zzacd com_google_android_gms_internal_zzacd, int i) {
        return context.getResources().getDrawable(i);
    }

    protected zzacb zza(Drawable drawable, Drawable drawable2) {
        if (drawable == null) {
            drawable = null;
        } else if (drawable instanceof zzacb) {
            drawable = ((zzacb) drawable).zzxs();
        }
        return new zzacb(drawable, drawable2);
    }

    void zza(Context context, Bitmap bitmap, boolean z) {
        com.google.android.gms.common.internal.zzc.zzt(bitmap);
        zza(new BitmapDrawable(context.getResources(), bitmap), z, false, true);
    }

    void zza(Context context, zzacd com_google_android_gms_internal_zzacd) {
        if (this.zzaEA) {
            zza(null, false, true, false);
        }
    }

    void zza(Context context, zzacd com_google_android_gms_internal_zzacd, boolean z) {
        Drawable drawable = null;
        if (this.zzaEw != 0) {
            drawable = zza(context, com_google_android_gms_internal_zzacd, this.zzaEw);
        }
        zza(drawable, z, false, false);
    }

    protected abstract void zza(Drawable drawable, boolean z, boolean z2, boolean z3);

    protected boolean zzc(boolean z, boolean z2) {
        return (!this.zzaEy || z2 || z) ? false : true;
    }

    public void zzcO(int i) {
        this.zzaEw = i;
    }
}
