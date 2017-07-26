package com.google.android.gms.common.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.internal.zzbfm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageManager {
    private static final Object zzaFR = new Object();
    private static HashSet<Uri> zzaFS = new HashSet();
    private static ImageManager zzaFT;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService zzaFU = Executors.newFixedThreadPool(4);
    private final zza zzaFV = null;
    private final zzbfm zzaFW = new zzbfm();
    private final Map<zza, ImageReceiver> zzaFX = new HashMap();
    private final Map<Uri, ImageReceiver> zzaFY = new HashMap();
    private final Map<Uri, Long> zzaFZ = new HashMap();

    @KeepName
    final class ImageReceiver extends ResultReceiver {
        private final Uri mUri;
        private final ArrayList<zza> zzaGa = new ArrayList();
        private /* synthetic */ ImageManager zzaGb;

        ImageReceiver(ImageManager imageManager, Uri uri) {
            this.zzaGb = imageManager;
            super(new Handler(Looper.getMainLooper()));
            this.mUri = uri;
        }

        public final void onReceiveResult(int i, Bundle bundle) {
            this.zzaGb.zzaFU.execute(new zzb(this.zzaGb, this.mUri, (ParcelFileDescriptor) bundle.getParcelable("com.google.android.gms.extra.fileDescriptor")));
        }

        public final void zzb(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzcz("ImageReceiver.addImageRequest() must be called in the main thread");
            this.zzaGa.add(com_google_android_gms_common_images_zza);
        }

        public final void zzc(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzcz("ImageReceiver.removeImageRequest() must be called in the main thread");
            this.zzaGa.remove(com_google_android_gms_common_images_zza);
        }

        public final void zzqV() {
            Intent intent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
            intent.putExtra("com.google.android.gms.extras.uri", this.mUri);
            intent.putExtra("com.google.android.gms.extras.resultReceiver", this);
            intent.putExtra("com.google.android.gms.extras.priority", 3);
            this.zzaGb.mContext.sendBroadcast(intent);
        }
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(Uri uri, Drawable drawable, boolean z);
    }

    final class zzb implements Runnable {
        private final Uri mUri;
        private /* synthetic */ ImageManager zzaGb;
        private final ParcelFileDescriptor zzaGc;

        public zzb(ImageManager imageManager, Uri uri, ParcelFileDescriptor parcelFileDescriptor) {
            this.zzaGb = imageManager;
            this.mUri = uri;
            this.zzaGc = parcelFileDescriptor;
        }

        public final void run() {
            String str = "LoadBitmapFromDiskRunnable can't be executed in the main thread";
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                String valueOf = String.valueOf(Thread.currentThread());
                String valueOf2 = String.valueOf(Looper.getMainLooper().getThread());
                Log.e("Asserts", new StringBuilder((String.valueOf(valueOf).length() + 56) + String.valueOf(valueOf2).length()).append("checkNotMainThread: current thread ").append(valueOf).append(" IS the main thread ").append(valueOf2).append("!").toString());
                throw new IllegalStateException(str);
            }
            boolean z = false;
            Bitmap bitmap = null;
            if (this.zzaGc != null) {
                try {
                    bitmap = BitmapFactory.decodeFileDescriptor(this.zzaGc.getFileDescriptor());
                } catch (Throwable e) {
                    String valueOf3 = String.valueOf(this.mUri);
                    Log.e("ImageManager", new StringBuilder(String.valueOf(valueOf3).length() + 34).append("OOM while loading bitmap for uri: ").append(valueOf3).toString(), e);
                    z = true;
                }
                try {
                    this.zzaGc.close();
                } catch (Throwable e2) {
                    Log.e("ImageManager", "closed failed", e2);
                }
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            this.zzaGb.mHandler.post(new zzd(this.zzaGb, this.mUri, bitmap, z, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e3) {
                String valueOf4 = String.valueOf(this.mUri);
                Log.w("ImageManager", new StringBuilder(String.valueOf(valueOf4).length() + 32).append("Latch interrupted while posting ").append(valueOf4).toString());
            }
        }
    }

    final class zzc implements Runnable {
        private /* synthetic */ ImageManager zzaGb;
        private final zza zzaGd;

        public zzc(ImageManager imageManager, zza com_google_android_gms_common_images_zza) {
            this.zzaGb = imageManager;
            this.zzaGd = com_google_android_gms_common_images_zza;
        }

        public final void run() {
            com.google.android.gms.common.internal.zzc.zzcz("LoadImageRunnable must be executed on the main thread");
            ImageReceiver imageReceiver = (ImageReceiver) this.zzaGb.zzaFX.get(this.zzaGd);
            if (imageReceiver != null) {
                this.zzaGb.zzaFX.remove(this.zzaGd);
                imageReceiver.zzc(this.zzaGd);
            }
            zzb com_google_android_gms_common_images_zzb = this.zzaGd.zzaGf;
            if (com_google_android_gms_common_images_zzb.uri == null) {
                this.zzaGd.zza(this.zzaGb.mContext, this.zzaGb.zzaFW, true);
                return;
            }
            Bitmap zza = this.zzaGb.zza(com_google_android_gms_common_images_zzb);
            if (zza != null) {
                this.zzaGd.zza(this.zzaGb.mContext, zza, true);
                return;
            }
            Long l = (Long) this.zzaGb.zzaFZ.get(com_google_android_gms_common_images_zzb.uri);
            if (l != null) {
                if (SystemClock.elapsedRealtime() - l.longValue() < 3600000) {
                    this.zzaGd.zza(this.zzaGb.mContext, this.zzaGb.zzaFW, true);
                    return;
                }
                this.zzaGb.zzaFZ.remove(com_google_android_gms_common_images_zzb.uri);
            }
            this.zzaGd.zza(this.zzaGb.mContext, this.zzaGb.zzaFW);
            imageReceiver = (ImageReceiver) this.zzaGb.zzaFY.get(com_google_android_gms_common_images_zzb.uri);
            if (imageReceiver == null) {
                imageReceiver = new ImageReceiver(this.zzaGb, com_google_android_gms_common_images_zzb.uri);
                this.zzaGb.zzaFY.put(com_google_android_gms_common_images_zzb.uri, imageReceiver);
            }
            imageReceiver.zzb(this.zzaGd);
            if (!(this.zzaGd instanceof zzd)) {
                this.zzaGb.zzaFX.put(this.zzaGd, imageReceiver);
            }
            synchronized (ImageManager.zzaFR) {
                if (!ImageManager.zzaFS.contains(com_google_android_gms_common_images_zzb.uri)) {
                    ImageManager.zzaFS.add(com_google_android_gms_common_images_zzb.uri);
                    imageReceiver.zzqV();
                }
            }
        }
    }

    final class zzd implements Runnable {
        private final Bitmap mBitmap;
        private final Uri mUri;
        private /* synthetic */ ImageManager zzaGb;
        private boolean zzaGe;
        private final CountDownLatch zztJ;

        public zzd(ImageManager imageManager, Uri uri, Bitmap bitmap, boolean z, CountDownLatch countDownLatch) {
            this.zzaGb = imageManager;
            this.mUri = uri;
            this.mBitmap = bitmap;
            this.zzaGe = z;
            this.zztJ = countDownLatch;
        }

        public final void run() {
            com.google.android.gms.common.internal.zzc.zzcz("OnBitmapLoadedRunnable must be executed in the main thread");
            boolean z = this.mBitmap != null;
            if (this.zzaGb.zzaFV != null) {
                if (this.zzaGe) {
                    this.zzaGb.zzaFV.evictAll();
                    System.gc();
                    this.zzaGe = false;
                    this.zzaGb.mHandler.post(this);
                    return;
                } else if (z) {
                    this.zzaGb.zzaFV.put(new zzb(this.mUri), this.mBitmap);
                }
            }
            ImageReceiver imageReceiver = (ImageReceiver) this.zzaGb.zzaFY.remove(this.mUri);
            if (imageReceiver != null) {
                ArrayList zza = imageReceiver.zzaGa;
                int size = zza.size();
                for (int i = 0; i < size; i++) {
                    zza com_google_android_gms_common_images_zza = (zza) zza.get(i);
                    if (z) {
                        com_google_android_gms_common_images_zza.zza(this.zzaGb.mContext, this.mBitmap, false);
                    } else {
                        this.zzaGb.zzaFZ.put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
                        com_google_android_gms_common_images_zza.zza(this.zzaGb.mContext, this.zzaGb.zzaFW, false);
                    }
                    if (!(com_google_android_gms_common_images_zza instanceof zzd)) {
                        this.zzaGb.zzaFX.remove(com_google_android_gms_common_images_zza);
                    }
                }
            }
            this.zztJ.countDown();
            synchronized (ImageManager.zzaFR) {
                ImageManager.zzaFS.remove(this.mUri);
            }
        }
    }

    static final class zza extends LruCache<zzb, Bitmap> {
        protected final /* synthetic */ void entryRemoved(boolean z, Object obj, Object obj2, Object obj3) {
            super.entryRemoved(z, (zzb) obj, (Bitmap) obj2, (Bitmap) obj3);
        }

        protected final /* synthetic */ int sizeOf(Object obj, Object obj2) {
            Bitmap bitmap = (Bitmap) obj2;
            return bitmap.getHeight() * bitmap.getRowBytes();
        }
    }

    private ImageManager(Context context, boolean z) {
        this.mContext = context.getApplicationContext();
    }

    public static ImageManager create(Context context) {
        if (zzaFT == null) {
            zzaFT = new ImageManager(context, false);
        }
        return zzaFT;
    }

    private final Bitmap zza(zzb com_google_android_gms_common_images_zzb) {
        return this.zzaFV == null ? null : (Bitmap) this.zzaFV.get(com_google_android_gms_common_images_zzb);
    }

    private final void zza(zza com_google_android_gms_common_images_zza) {
        com.google.android.gms.common.internal.zzc.zzcz("ImageManager.loadImage() must be called in the main thread");
        new zzc(this, com_google_android_gms_common_images_zza).run();
    }

    public final void loadImage(ImageView imageView, int i) {
        zza(new zzc(imageView, i));
    }

    public final void loadImage(ImageView imageView, Uri uri) {
        zza(new zzc(imageView, uri));
    }

    public final void loadImage(ImageView imageView, Uri uri, int i) {
        zza com_google_android_gms_common_images_zzc = new zzc(imageView, uri);
        com_google_android_gms_common_images_zzc.zzaGh = i;
        zza(com_google_android_gms_common_images_zzc);
    }

    public final void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri) {
        zza(new zzd(onImageLoadedListener, uri));
    }

    public final void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri, int i) {
        zza com_google_android_gms_common_images_zzd = new zzd(onImageLoadedListener, uri);
        com_google_android_gms_common_images_zzd.zzaGh = i;
        zza(com_google_android_gms_common_images_zzd);
    }
}
