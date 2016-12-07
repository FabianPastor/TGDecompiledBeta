package com.google.android.gms.common.images;

import android.annotation.TargetApi;
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
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.internal.zzsl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageManager {
    private static final Object Co = new Object();
    private static HashSet<Uri> Cp = new HashSet();
    private static ImageManager Cq;
    private static ImageManager Cr;
    private final ExecutorService Cs = Executors.newFixedThreadPool(4);
    private final zzb Ct;
    private final zzsl Cu;
    private final Map<zza, ImageReceiver> Cv;
    private final Map<Uri, ImageReceiver> Cw;
    private final Map<Uri, Long> Cx;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @KeepName
    private final class ImageReceiver extends ResultReceiver {
        private final ArrayList<zza> Cy = new ArrayList();
        final /* synthetic */ ImageManager Cz;
        private final Uri mUri;

        ImageReceiver(ImageManager imageManager, Uri uri) {
            this.Cz = imageManager;
            super(new Handler(Looper.getMainLooper()));
            this.mUri = uri;
        }

        public void onReceiveResult(int i, Bundle bundle) {
            this.Cz.Cs.execute(new zzc(this.Cz, this.mUri, (ParcelFileDescriptor) bundle.getParcelable("com.google.android.gms.extra.fileDescriptor")));
        }

        public void zzauv() {
            Intent intent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
            intent.putExtra("com.google.android.gms.extras.uri", this.mUri);
            intent.putExtra("com.google.android.gms.extras.resultReceiver", this);
            intent.putExtra("com.google.android.gms.extras.priority", 3);
            this.Cz.mContext.sendBroadcast(intent);
        }

        public void zzb(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzhs("ImageReceiver.addImageRequest() must be called in the main thread");
            this.Cy.add(com_google_android_gms_common_images_zza);
        }

        public void zzc(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzhs("ImageReceiver.removeImageRequest() must be called in the main thread");
            this.Cy.remove(com_google_android_gms_common_images_zza);
        }
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(Uri uri, Drawable drawable, boolean z);
    }

    @TargetApi(11)
    private static final class zza {
        static int zza(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    private final class zzc implements Runnable {
        private final ParcelFileDescriptor CA;
        final /* synthetic */ ImageManager Cz;
        private final Uri mUri;

        public zzc(ImageManager imageManager, Uri uri, ParcelFileDescriptor parcelFileDescriptor) {
            this.Cz = imageManager;
            this.mUri = uri;
            this.CA = parcelFileDescriptor;
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzht("LoadBitmapFromDiskRunnable can't be executed in the main thread");
            boolean z = false;
            Bitmap bitmap = null;
            if (this.CA != null) {
                try {
                    bitmap = BitmapFactory.decodeFileDescriptor(this.CA.getFileDescriptor());
                } catch (Throwable e) {
                    String valueOf = String.valueOf(this.mUri);
                    Log.e("ImageManager", new StringBuilder(String.valueOf(valueOf).length() + 34).append("OOM while loading bitmap for uri: ").append(valueOf).toString(), e);
                    z = true;
                }
                try {
                    this.CA.close();
                } catch (Throwable e2) {
                    Log.e("ImageManager", "closed failed", e2);
                }
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            this.Cz.mHandler.post(new zzf(this.Cz, this.mUri, bitmap, z, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e3) {
                String valueOf2 = String.valueOf(this.mUri);
                Log.w("ImageManager", new StringBuilder(String.valueOf(valueOf2).length() + 32).append("Latch interrupted while posting ").append(valueOf2).toString());
            }
        }
    }

    private final class zzd implements Runnable {
        private final zza CB;
        final /* synthetic */ ImageManager Cz;

        public zzd(ImageManager imageManager, zza com_google_android_gms_common_images_zza) {
            this.Cz = imageManager;
            this.CB = com_google_android_gms_common_images_zza;
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzhs("LoadImageRunnable must be executed on the main thread");
            ImageReceiver imageReceiver = (ImageReceiver) this.Cz.Cv.get(this.CB);
            if (imageReceiver != null) {
                this.Cz.Cv.remove(this.CB);
                imageReceiver.zzc(this.CB);
            }
            zza com_google_android_gms_common_images_zza_zza = this.CB.CD;
            if (com_google_android_gms_common_images_zza_zza.uri == null) {
                this.CB.zza(this.Cz.mContext, this.Cz.Cu, true);
                return;
            }
            Bitmap zza = this.Cz.zza(com_google_android_gms_common_images_zza_zza);
            if (zza != null) {
                this.CB.zza(this.Cz.mContext, zza, true);
                return;
            }
            Long l = (Long) this.Cz.Cx.get(com_google_android_gms_common_images_zza_zza.uri);
            if (l != null) {
                if (SystemClock.elapsedRealtime() - l.longValue() < 3600000) {
                    this.CB.zza(this.Cz.mContext, this.Cz.Cu, true);
                    return;
                }
                this.Cz.Cx.remove(com_google_android_gms_common_images_zza_zza.uri);
            }
            this.CB.zza(this.Cz.mContext, this.Cz.Cu);
            imageReceiver = (ImageReceiver) this.Cz.Cw.get(com_google_android_gms_common_images_zza_zza.uri);
            if (imageReceiver == null) {
                imageReceiver = new ImageReceiver(this.Cz, com_google_android_gms_common_images_zza_zza.uri);
                this.Cz.Cw.put(com_google_android_gms_common_images_zza_zza.uri, imageReceiver);
            }
            imageReceiver.zzb(this.CB);
            if (!(this.CB instanceof com.google.android.gms.common.images.zza.zzc)) {
                this.Cz.Cv.put(this.CB, imageReceiver);
            }
            synchronized (ImageManager.Co) {
                if (!ImageManager.Cp.contains(com_google_android_gms_common_images_zza_zza.uri)) {
                    ImageManager.Cp.add(com_google_android_gms_common_images_zza_zza.uri);
                    imageReceiver.zzauv();
                }
            }
        }
    }

    @TargetApi(14)
    private static final class zze implements ComponentCallbacks2 {
        private final zzb Ct;

        public zze(zzb com_google_android_gms_common_images_ImageManager_zzb) {
            this.Ct = com_google_android_gms_common_images_ImageManager_zzb;
        }

        public void onConfigurationChanged(Configuration configuration) {
        }

        public void onLowMemory() {
            this.Ct.evictAll();
        }

        public void onTrimMemory(int i) {
            if (i >= 60) {
                this.Ct.evictAll();
            } else if (i >= 20) {
                this.Ct.trimToSize(this.Ct.size() / 2);
            }
        }
    }

    private final class zzf implements Runnable {
        private boolean CC;
        final /* synthetic */ ImageManager Cz;
        private final Bitmap mBitmap;
        private final Uri mUri;
        private final CountDownLatch zzank;

        public zzf(ImageManager imageManager, Uri uri, Bitmap bitmap, boolean z, CountDownLatch countDownLatch) {
            this.Cz = imageManager;
            this.mUri = uri;
            this.mBitmap = bitmap;
            this.CC = z;
            this.zzank = countDownLatch;
        }

        private void zza(ImageReceiver imageReceiver, boolean z) {
            ArrayList zza = imageReceiver.Cy;
            int size = zza.size();
            for (int i = 0; i < size; i++) {
                zza com_google_android_gms_common_images_zza = (zza) zza.get(i);
                if (z) {
                    com_google_android_gms_common_images_zza.zza(this.Cz.mContext, this.mBitmap, false);
                } else {
                    this.Cz.Cx.put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
                    com_google_android_gms_common_images_zza.zza(this.Cz.mContext, this.Cz.Cu, false);
                }
                if (!(com_google_android_gms_common_images_zza instanceof com.google.android.gms.common.images.zza.zzc)) {
                    this.Cz.Cv.remove(com_google_android_gms_common_images_zza);
                }
            }
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzhs("OnBitmapLoadedRunnable must be executed in the main thread");
            boolean z = this.mBitmap != null;
            if (this.Cz.Ct != null) {
                if (this.CC) {
                    this.Cz.Ct.evictAll();
                    System.gc();
                    this.CC = false;
                    this.Cz.mHandler.post(this);
                    return;
                } else if (z) {
                    this.Cz.Ct.put(new zza(this.mUri), this.mBitmap);
                }
            }
            ImageReceiver imageReceiver = (ImageReceiver) this.Cz.Cw.remove(this.mUri);
            if (imageReceiver != null) {
                zza(imageReceiver, z);
            }
            this.zzank.countDown();
            synchronized (ImageManager.Co) {
                ImageManager.Cp.remove(this.mUri);
            }
        }
    }

    private static final class zzb extends LruCache<zza, Bitmap> {
        public zzb(Context context) {
            super(zzbz(context));
        }

        @TargetApi(11)
        private static int zzbz(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            int memoryClass = (((context.getApplicationInfo().flags & 1048576) != 0 ? 1 : null) == null || !zzs.zzayn()) ? activityManager.getMemoryClass() : zza.zza(activityManager);
            return (int) (((float) (memoryClass * 1048576)) * 0.33f);
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
            this.Ct = new zzb(this.mContext);
            if (zzs.zzayq()) {
                zzaut();
            }
        } else {
            this.Ct = null;
        }
        this.Cu = new zzsl();
        this.Cv = new HashMap();
        this.Cw = new HashMap();
        this.Cx = new HashMap();
    }

    public static ImageManager create(Context context) {
        return zzg(context, false);
    }

    private Bitmap zza(zza com_google_android_gms_common_images_zza_zza) {
        return this.Ct == null ? null : (Bitmap) this.Ct.get(com_google_android_gms_common_images_zza_zza);
    }

    @TargetApi(14)
    private void zzaut() {
        this.mContext.registerComponentCallbacks(new zze(this.Ct));
    }

    public static ImageManager zzg(Context context, boolean z) {
        if (z) {
            if (Cr == null) {
                Cr = new ImageManager(context, true);
            }
            return Cr;
        }
        if (Cq == null) {
            Cq = new ImageManager(context, false);
        }
        return Cq;
    }

    public void loadImage(ImageView imageView, int i) {
        zza(new com.google.android.gms.common.images.zza.zzb(imageView, i));
    }

    public void loadImage(ImageView imageView, Uri uri) {
        zza(new com.google.android.gms.common.images.zza.zzb(imageView, uri));
    }

    public void loadImage(ImageView imageView, Uri uri, int i) {
        zza com_google_android_gms_common_images_zza_zzb = new com.google.android.gms.common.images.zza.zzb(imageView, uri);
        com_google_android_gms_common_images_zza_zzb.zzgg(i);
        zza(com_google_android_gms_common_images_zza_zzb);
    }

    public void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri) {
        zza(new com.google.android.gms.common.images.zza.zzc(onImageLoadedListener, uri));
    }

    public void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri, int i) {
        zza com_google_android_gms_common_images_zza_zzc = new com.google.android.gms.common.images.zza.zzc(onImageLoadedListener, uri);
        com_google_android_gms_common_images_zza_zzc.zzgg(i);
        zza(com_google_android_gms_common_images_zza_zzc);
    }

    public void zza(zza com_google_android_gms_common_images_zza) {
        com.google.android.gms.common.internal.zzc.zzhs("ImageManager.loadImage() must be called in the main thread");
        new zzd(this, com_google_android_gms_common_images_zza).run();
    }
}
