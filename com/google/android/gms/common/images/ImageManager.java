package com.google.android.gms.common.images;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.google.android.gms.internal.zzacd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageManager {
    private static final Object zzaEf = new Object();
    private static HashSet<Uri> zzaEg = new HashSet();
    private static ImageManager zzaEh;
    private static ImageManager zzaEi;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService zzaEj = Executors.newFixedThreadPool(4);
    private final zza zzaEk;
    private final zzacd zzaEl;
    private final Map<zza, ImageReceiver> zzaEm;
    private final Map<Uri, ImageReceiver> zzaEn;
    private final Map<Uri, Long> zzaEo;

    @KeepName
    private final class ImageReceiver extends ResultReceiver {
        private final Uri mUri;
        private final ArrayList<zza> zzaEp = new ArrayList();
        final /* synthetic */ ImageManager zzaEq;

        ImageReceiver(ImageManager imageManager, Uri uri) {
            this.zzaEq = imageManager;
            super(new Handler(Looper.getMainLooper()));
            this.mUri = uri;
        }

        public void onReceiveResult(int i, Bundle bundle) {
            this.zzaEq.zzaEj.execute(new zzb(this.zzaEq, this.mUri, (ParcelFileDescriptor) bundle.getParcelable("com.google.android.gms.extra.fileDescriptor")));
        }

        public void zzb(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzdj("ImageReceiver.addImageRequest() must be called in the main thread");
            this.zzaEp.add(com_google_android_gms_common_images_zza);
        }

        public void zzc(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzdj("ImageReceiver.removeImageRequest() must be called in the main thread");
            this.zzaEp.remove(com_google_android_gms_common_images_zza);
        }

        public void zzxr() {
            Intent intent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
            intent.putExtra("com.google.android.gms.extras.uri", this.mUri);
            intent.putExtra("com.google.android.gms.extras.resultReceiver", this);
            intent.putExtra("com.google.android.gms.extras.priority", 3);
            this.zzaEq.mContext.sendBroadcast(intent);
        }
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(Uri uri, Drawable drawable, boolean z);
    }

    private final class zzb implements Runnable {
        private final Uri mUri;
        final /* synthetic */ ImageManager zzaEq;
        private final ParcelFileDescriptor zzaEr;

        public zzb(ImageManager imageManager, Uri uri, ParcelFileDescriptor parcelFileDescriptor) {
            this.zzaEq = imageManager;
            this.mUri = uri;
            this.zzaEr = parcelFileDescriptor;
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzdk("LoadBitmapFromDiskRunnable can't be executed in the main thread");
            boolean z = false;
            Bitmap bitmap = null;
            if (this.zzaEr != null) {
                try {
                    bitmap = BitmapFactory.decodeFileDescriptor(this.zzaEr.getFileDescriptor());
                } catch (Throwable e) {
                    String valueOf = String.valueOf(this.mUri);
                    Log.e("ImageManager", new StringBuilder(String.valueOf(valueOf).length() + 34).append("OOM while loading bitmap for uri: ").append(valueOf).toString(), e);
                    z = true;
                }
                try {
                    this.zzaEr.close();
                } catch (Throwable e2) {
                    Log.e("ImageManager", "closed failed", e2);
                }
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            this.zzaEq.mHandler.post(new zze(this.zzaEq, this.mUri, bitmap, z, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e3) {
                String valueOf2 = String.valueOf(this.mUri);
                Log.w("ImageManager", new StringBuilder(String.valueOf(valueOf2).length() + 32).append("Latch interrupted while posting ").append(valueOf2).toString());
            }
        }
    }

    private final class zzc implements Runnable {
        final /* synthetic */ ImageManager zzaEq;
        private final zza zzaEs;

        public zzc(ImageManager imageManager, zza com_google_android_gms_common_images_zza) {
            this.zzaEq = imageManager;
            this.zzaEs = com_google_android_gms_common_images_zza;
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzdj("LoadImageRunnable must be executed on the main thread");
            ImageReceiver imageReceiver = (ImageReceiver) this.zzaEq.zzaEm.get(this.zzaEs);
            if (imageReceiver != null) {
                this.zzaEq.zzaEm.remove(this.zzaEs);
                imageReceiver.zzc(this.zzaEs);
            }
            zza com_google_android_gms_common_images_zza_zza = this.zzaEs.zzaEu;
            if (com_google_android_gms_common_images_zza_zza.uri == null) {
                this.zzaEs.zza(this.zzaEq.mContext, this.zzaEq.zzaEl, true);
                return;
            }
            Bitmap zza = this.zzaEq.zza(com_google_android_gms_common_images_zza_zza);
            if (zza != null) {
                this.zzaEs.zza(this.zzaEq.mContext, zza, true);
                return;
            }
            Long l = (Long) this.zzaEq.zzaEo.get(com_google_android_gms_common_images_zza_zza.uri);
            if (l != null) {
                if (SystemClock.elapsedRealtime() - l.longValue() < 3600000) {
                    this.zzaEs.zza(this.zzaEq.mContext, this.zzaEq.zzaEl, true);
                    return;
                }
                this.zzaEq.zzaEo.remove(com_google_android_gms_common_images_zza_zza.uri);
            }
            this.zzaEs.zza(this.zzaEq.mContext, this.zzaEq.zzaEl);
            imageReceiver = (ImageReceiver) this.zzaEq.zzaEn.get(com_google_android_gms_common_images_zza_zza.uri);
            if (imageReceiver == null) {
                imageReceiver = new ImageReceiver(this.zzaEq, com_google_android_gms_common_images_zza_zza.uri);
                this.zzaEq.zzaEn.put(com_google_android_gms_common_images_zza_zza.uri, imageReceiver);
            }
            imageReceiver.zzb(this.zzaEs);
            if (!(this.zzaEs instanceof com.google.android.gms.common.images.zza.zzc)) {
                this.zzaEq.zzaEm.put(this.zzaEs, imageReceiver);
            }
            synchronized (ImageManager.zzaEf) {
                if (!ImageManager.zzaEg.contains(com_google_android_gms_common_images_zza_zza.uri)) {
                    ImageManager.zzaEg.add(com_google_android_gms_common_images_zza_zza.uri);
                    imageReceiver.zzxr();
                }
            }
        }
    }

    private static final class zzd implements ComponentCallbacks2 {
        private final zza zzaEk;

        public zzd(zza com_google_android_gms_common_images_ImageManager_zza) {
            this.zzaEk = com_google_android_gms_common_images_ImageManager_zza;
        }

        public void onConfigurationChanged(Configuration configuration) {
        }

        public void onLowMemory() {
            this.zzaEk.evictAll();
        }

        public void onTrimMemory(int i) {
            if (i >= 60) {
                this.zzaEk.evictAll();
            } else if (i >= 20) {
                this.zzaEk.trimToSize(this.zzaEk.size() / 2);
            }
        }
    }

    private final class zze implements Runnable {
        private final Bitmap mBitmap;
        private final Uri mUri;
        final /* synthetic */ ImageManager zzaEq;
        private boolean zzaEt;
        private final CountDownLatch zztj;

        public zze(ImageManager imageManager, Uri uri, Bitmap bitmap, boolean z, CountDownLatch countDownLatch) {
            this.zzaEq = imageManager;
            this.mUri = uri;
            this.mBitmap = bitmap;
            this.zzaEt = z;
            this.zztj = countDownLatch;
        }

        private void zza(ImageReceiver imageReceiver, boolean z) {
            ArrayList zza = imageReceiver.zzaEp;
            int size = zza.size();
            for (int i = 0; i < size; i++) {
                zza com_google_android_gms_common_images_zza = (zza) zza.get(i);
                if (z) {
                    com_google_android_gms_common_images_zza.zza(this.zzaEq.mContext, this.mBitmap, false);
                } else {
                    this.zzaEq.zzaEo.put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
                    com_google_android_gms_common_images_zza.zza(this.zzaEq.mContext, this.zzaEq.zzaEl, false);
                }
                if (!(com_google_android_gms_common_images_zza instanceof com.google.android.gms.common.images.zza.zzc)) {
                    this.zzaEq.zzaEm.remove(com_google_android_gms_common_images_zza);
                }
            }
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzdj("OnBitmapLoadedRunnable must be executed in the main thread");
            boolean z = this.mBitmap != null;
            if (this.zzaEq.zzaEk != null) {
                if (this.zzaEt) {
                    this.zzaEq.zzaEk.evictAll();
                    System.gc();
                    this.zzaEt = false;
                    this.zzaEq.mHandler.post(this);
                    return;
                } else if (z) {
                    this.zzaEq.zzaEk.put(new zza(this.mUri), this.mBitmap);
                }
            }
            ImageReceiver imageReceiver = (ImageReceiver) this.zzaEq.zzaEn.remove(this.mUri);
            if (imageReceiver != null) {
                zza(imageReceiver, z);
            }
            this.zztj.countDown();
            synchronized (ImageManager.zzaEf) {
                ImageManager.zzaEg.remove(this.mUri);
            }
        }
    }

    private static final class zza extends LruCache<zza, Bitmap> {
        public zza(Context context) {
            super(zzaR(context));
        }

        private static int zzaR(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            return (int) (((float) ((((context.getApplicationInfo().flags & 1048576) != 0 ? 1 : null) != null ? activityManager.getLargeMemoryClass() : activityManager.getMemoryClass()) * 1048576)) * 0.33f);
        }

        protected /* synthetic */ void entryRemoved(boolean z, Object obj, Object obj2, Object obj3) {
            zza(z, (zza) obj, (Bitmap) obj2, (Bitmap) obj3);
        }

        protected /* synthetic */ int sizeOf(Object obj, Object obj2) {
            return zza((zza) obj, (Bitmap) obj2);
        }

        protected int zza(zza com_google_android_gms_common_images_zza_zza, Bitmap bitmap) {
            return bitmap.getHeight() * bitmap.getRowBytes();
        }

        protected void zza(boolean z, zza com_google_android_gms_common_images_zza_zza, Bitmap bitmap, Bitmap bitmap2) {
            super.entryRemoved(z, com_google_android_gms_common_images_zza_zza, bitmap, bitmap2);
        }
    }

    private ImageManager(Context context, boolean z) {
        this.mContext = context.getApplicationContext();
        if (z) {
            this.zzaEk = new zza(this.mContext);
            this.mContext.registerComponentCallbacks(new zzd(this.zzaEk));
        } else {
            this.zzaEk = null;
        }
        this.zzaEl = new zzacd();
        this.zzaEm = new HashMap();
        this.zzaEn = new HashMap();
        this.zzaEo = new HashMap();
    }

    public static ImageManager create(Context context) {
        return zzg(context, false);
    }

    private Bitmap zza(zza com_google_android_gms_common_images_zza_zza) {
        return this.zzaEk == null ? null : (Bitmap) this.zzaEk.get(com_google_android_gms_common_images_zza_zza);
    }

    public static ImageManager zzg(Context context, boolean z) {
        if (z) {
            if (zzaEi == null) {
                zzaEi = new ImageManager(context, true);
            }
            return zzaEi;
        }
        if (zzaEh == null) {
            zzaEh = new ImageManager(context, false);
        }
        return zzaEh;
    }

    public void loadImage(ImageView imageView, int i) {
        zza(new com.google.android.gms.common.images.zza.zzb(imageView, i));
    }

    public void loadImage(ImageView imageView, Uri uri) {
        zza(new com.google.android.gms.common.images.zza.zzb(imageView, uri));
    }

    public void loadImage(ImageView imageView, Uri uri, int i) {
        zza com_google_android_gms_common_images_zza_zzb = new com.google.android.gms.common.images.zza.zzb(imageView, uri);
        com_google_android_gms_common_images_zza_zzb.zzcO(i);
        zza(com_google_android_gms_common_images_zza_zzb);
    }

    public void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri) {
        zza(new com.google.android.gms.common.images.zza.zzc(onImageLoadedListener, uri));
    }

    public void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri, int i) {
        zza com_google_android_gms_common_images_zza_zzc = new com.google.android.gms.common.images.zza.zzc(onImageLoadedListener, uri);
        com_google_android_gms_common_images_zza_zzc.zzcO(i);
        zza(com_google_android_gms_common_images_zza_zzc);
    }

    public void zza(zza com_google_android_gms_common_images_zza) {
        com.google.android.gms.common.internal.zzc.zzdj("ImageManager.loadImage() must be called in the main thread");
        new zzc(this, com_google_android_gms_common_images_zza).run();
    }
}
