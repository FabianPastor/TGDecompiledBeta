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
import com.google.android.gms.internal.zzrv;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageManager {
    private static final Object Ae = new Object();
    private static HashSet<Uri> Af = new HashSet();
    private static ImageManager Ag;
    private static ImageManager Ah;
    private final ExecutorService Ai = Executors.newFixedThreadPool(4);
    private final zzb Aj;
    private final zzrv Ak;
    private final Map<zza, ImageReceiver> Al;
    private final Map<Uri, ImageReceiver> Am;
    private final Map<Uri, Long> An;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @KeepName
    private final class ImageReceiver extends ResultReceiver {
        private final ArrayList<zza> Ao = new ArrayList();
        final /* synthetic */ ImageManager Ap;
        private final Uri mUri;

        ImageReceiver(ImageManager imageManager, Uri uri) {
            this.Ap = imageManager;
            super(new Handler(Looper.getMainLooper()));
            this.mUri = uri;
        }

        public void onReceiveResult(int i, Bundle bundle) {
            this.Ap.Ai.execute(new zzc(this.Ap, this.mUri, (ParcelFileDescriptor) bundle.getParcelable("com.google.android.gms.extra.fileDescriptor")));
        }

        public void zzatm() {
            Intent intent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
            intent.putExtra("com.google.android.gms.extras.uri", this.mUri);
            intent.putExtra("com.google.android.gms.extras.resultReceiver", this);
            intent.putExtra("com.google.android.gms.extras.priority", 3);
            this.Ap.mContext.sendBroadcast(intent);
        }

        public void zzb(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzhq("ImageReceiver.addImageRequest() must be called in the main thread");
            this.Ao.add(com_google_android_gms_common_images_zza);
        }

        public void zzc(zza com_google_android_gms_common_images_zza) {
            com.google.android.gms.common.internal.zzc.zzhq("ImageReceiver.removeImageRequest() must be called in the main thread");
            this.Ao.remove(com_google_android_gms_common_images_zza);
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
        final /* synthetic */ ImageManager Ap;
        private final ParcelFileDescriptor Aq;
        private final Uri mUri;

        public zzc(ImageManager imageManager, Uri uri, ParcelFileDescriptor parcelFileDescriptor) {
            this.Ap = imageManager;
            this.mUri = uri;
            this.Aq = parcelFileDescriptor;
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzhr("LoadBitmapFromDiskRunnable can't be executed in the main thread");
            boolean z = false;
            Bitmap bitmap = null;
            if (this.Aq != null) {
                try {
                    bitmap = BitmapFactory.decodeFileDescriptor(this.Aq.getFileDescriptor());
                } catch (Throwable e) {
                    String valueOf = String.valueOf(this.mUri);
                    Log.e("ImageManager", new StringBuilder(String.valueOf(valueOf).length() + 34).append("OOM while loading bitmap for uri: ").append(valueOf).toString(), e);
                    z = true;
                }
                try {
                    this.Aq.close();
                } catch (Throwable e2) {
                    Log.e("ImageManager", "closed failed", e2);
                }
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            this.Ap.mHandler.post(new zzf(this.Ap, this.mUri, bitmap, z, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e3) {
                String valueOf2 = String.valueOf(this.mUri);
                Log.w("ImageManager", new StringBuilder(String.valueOf(valueOf2).length() + 32).append("Latch interrupted while posting ").append(valueOf2).toString());
            }
        }
    }

    private final class zzd implements Runnable {
        final /* synthetic */ ImageManager Ap;
        private final zza Ar;

        public zzd(ImageManager imageManager, zza com_google_android_gms_common_images_zza) {
            this.Ap = imageManager;
            this.Ar = com_google_android_gms_common_images_zza;
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzhq("LoadImageRunnable must be executed on the main thread");
            ImageReceiver imageReceiver = (ImageReceiver) this.Ap.Al.get(this.Ar);
            if (imageReceiver != null) {
                this.Ap.Al.remove(this.Ar);
                imageReceiver.zzc(this.Ar);
            }
            zza com_google_android_gms_common_images_zza_zza = this.Ar.At;
            if (com_google_android_gms_common_images_zza_zza.uri == null) {
                this.Ar.zza(this.Ap.mContext, this.Ap.Ak, true);
                return;
            }
            Bitmap zza = this.Ap.zza(com_google_android_gms_common_images_zza_zza);
            if (zza != null) {
                this.Ar.zza(this.Ap.mContext, zza, true);
                return;
            }
            Long l = (Long) this.Ap.An.get(com_google_android_gms_common_images_zza_zza.uri);
            if (l != null) {
                if (SystemClock.elapsedRealtime() - l.longValue() < 3600000) {
                    this.Ar.zza(this.Ap.mContext, this.Ap.Ak, true);
                    return;
                }
                this.Ap.An.remove(com_google_android_gms_common_images_zza_zza.uri);
            }
            this.Ar.zza(this.Ap.mContext, this.Ap.Ak);
            imageReceiver = (ImageReceiver) this.Ap.Am.get(com_google_android_gms_common_images_zza_zza.uri);
            if (imageReceiver == null) {
                imageReceiver = new ImageReceiver(this.Ap, com_google_android_gms_common_images_zza_zza.uri);
                this.Ap.Am.put(com_google_android_gms_common_images_zza_zza.uri, imageReceiver);
            }
            imageReceiver.zzb(this.Ar);
            if (!(this.Ar instanceof com.google.android.gms.common.images.zza.zzc)) {
                this.Ap.Al.put(this.Ar, imageReceiver);
            }
            synchronized (ImageManager.Ae) {
                if (!ImageManager.Af.contains(com_google_android_gms_common_images_zza_zza.uri)) {
                    ImageManager.Af.add(com_google_android_gms_common_images_zza_zza.uri);
                    imageReceiver.zzatm();
                }
            }
        }
    }

    @TargetApi(14)
    private static final class zze implements ComponentCallbacks2 {
        private final zzb Aj;

        public zze(zzb com_google_android_gms_common_images_ImageManager_zzb) {
            this.Aj = com_google_android_gms_common_images_ImageManager_zzb;
        }

        public void onConfigurationChanged(Configuration configuration) {
        }

        public void onLowMemory() {
            this.Aj.evictAll();
        }

        public void onTrimMemory(int i) {
            if (i >= 60) {
                this.Aj.evictAll();
            } else if (i >= 20) {
                this.Aj.trimToSize(this.Aj.size() / 2);
            }
        }
    }

    private final class zzf implements Runnable {
        final /* synthetic */ ImageManager Ap;
        private boolean As;
        private final Bitmap mBitmap;
        private final Uri mUri;
        private final CountDownLatch zzamx;

        public zzf(ImageManager imageManager, Uri uri, Bitmap bitmap, boolean z, CountDownLatch countDownLatch) {
            this.Ap = imageManager;
            this.mUri = uri;
            this.mBitmap = bitmap;
            this.As = z;
            this.zzamx = countDownLatch;
        }

        private void zza(ImageReceiver imageReceiver, boolean z) {
            ArrayList zza = imageReceiver.Ao;
            int size = zza.size();
            for (int i = 0; i < size; i++) {
                zza com_google_android_gms_common_images_zza = (zza) zza.get(i);
                if (z) {
                    com_google_android_gms_common_images_zza.zza(this.Ap.mContext, this.mBitmap, false);
                } else {
                    this.Ap.An.put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
                    com_google_android_gms_common_images_zza.zza(this.Ap.mContext, this.Ap.Ak, false);
                }
                if (!(com_google_android_gms_common_images_zza instanceof com.google.android.gms.common.images.zza.zzc)) {
                    this.Ap.Al.remove(com_google_android_gms_common_images_zza);
                }
            }
        }

        public void run() {
            com.google.android.gms.common.internal.zzc.zzhq("OnBitmapLoadedRunnable must be executed in the main thread");
            boolean z = this.mBitmap != null;
            if (this.Ap.Aj != null) {
                if (this.As) {
                    this.Ap.Aj.evictAll();
                    System.gc();
                    this.As = false;
                    this.Ap.mHandler.post(this);
                    return;
                } else if (z) {
                    this.Ap.Aj.put(new zza(this.mUri), this.mBitmap);
                }
            }
            ImageReceiver imageReceiver = (ImageReceiver) this.Ap.Am.remove(this.mUri);
            if (imageReceiver != null) {
                zza(imageReceiver, z);
            }
            this.zzamx.countDown();
            synchronized (ImageManager.Ae) {
                ImageManager.Af.remove(this.mUri);
            }
        }
    }

    private static final class zzb extends LruCache<zza, Bitmap> {
        public zzb(Context context) {
            super(zzcc(context));
        }

        @TargetApi(11)
        private static int zzcc(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            int memoryClass = (((context.getApplicationInfo().flags & 1048576) != 0 ? 1 : null) == null || !zzs.zzaxk()) ? activityManager.getMemoryClass() : zza.zza(activityManager);
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
            this.Aj = new zzb(this.mContext);
            if (zzs.zzaxn()) {
                zzatk();
            }
        } else {
            this.Aj = null;
        }
        this.Ak = new zzrv();
        this.Al = new HashMap();
        this.Am = new HashMap();
        this.An = new HashMap();
    }

    public static ImageManager create(Context context) {
        return zzg(context, false);
    }

    private Bitmap zza(zza com_google_android_gms_common_images_zza_zza) {
        return this.Aj == null ? null : (Bitmap) this.Aj.get(com_google_android_gms_common_images_zza_zza);
    }

    @TargetApi(14)
    private void zzatk() {
        this.mContext.registerComponentCallbacks(new zze(this.Aj));
    }

    public static ImageManager zzg(Context context, boolean z) {
        if (z) {
            if (Ah == null) {
                Ah = new ImageManager(context, true);
            }
            return Ah;
        }
        if (Ag == null) {
            Ag = new ImageManager(context, false);
        }
        return Ag;
    }

    public void loadImage(ImageView imageView, int i) {
        zza(new com.google.android.gms.common.images.zza.zzb(imageView, i));
    }

    public void loadImage(ImageView imageView, Uri uri) {
        zza(new com.google.android.gms.common.images.zza.zzb(imageView, uri));
    }

    public void loadImage(ImageView imageView, Uri uri, int i) {
        zza com_google_android_gms_common_images_zza_zzb = new com.google.android.gms.common.images.zza.zzb(imageView, uri);
        com_google_android_gms_common_images_zza_zzb.zzgh(i);
        zza(com_google_android_gms_common_images_zza_zzb);
    }

    public void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri) {
        zza(new com.google.android.gms.common.images.zza.zzc(onImageLoadedListener, uri));
    }

    public void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri, int i) {
        zza com_google_android_gms_common_images_zza_zzc = new com.google.android.gms.common.images.zza.zzc(onImageLoadedListener, uri);
        com_google_android_gms_common_images_zza_zzc.zzgh(i);
        zza(com_google_android_gms_common_images_zza_zzc);
    }

    public void zza(zza com_google_android_gms_common_images_zza) {
        com.google.android.gms.common.internal.zzc.zzhq("ImageManager.loadImage() must be called in the main thread");
        new zzd(this, com_google_android_gms_common_images_zza).run();
    }
}
