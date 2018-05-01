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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageManager
{
  private static final Object zzaFR = new Object();
  private static HashSet<Uri> zzaFS = new HashSet();
  private static ImageManager zzaFT;
  private final Context mContext;
  private final Handler mHandler;
  private final ExecutorService zzaFU;
  private final zza zzaFV;
  private final zzbfm zzaFW;
  private final Map<zza, ImageReceiver> zzaFX;
  private final Map<Uri, ImageReceiver> zzaFY;
  private final Map<Uri, Long> zzaFZ;
  
  private ImageManager(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mHandler = new Handler(Looper.getMainLooper());
    this.zzaFU = Executors.newFixedThreadPool(4);
    this.zzaFV = null;
    this.zzaFW = new zzbfm();
    this.zzaFX = new HashMap();
    this.zzaFY = new HashMap();
    this.zzaFZ = new HashMap();
  }
  
  public static ImageManager create(Context paramContext)
  {
    if (zzaFT == null) {
      zzaFT = new ImageManager(paramContext, false);
    }
    return zzaFT;
  }
  
  private final Bitmap zza(zzb paramzzb)
  {
    if (this.zzaFV == null) {
      return null;
    }
    return (Bitmap)this.zzaFV.get(paramzzb);
  }
  
  private final void zza(zza paramzza)
  {
    com.google.android.gms.common.internal.zzc.zzcz("ImageManager.loadImage() must be called in the main thread");
    new zzc(paramzza).run();
  }
  
  public final void loadImage(ImageView paramImageView, int paramInt)
  {
    zza(new zzc(paramImageView, paramInt));
  }
  
  public final void loadImage(ImageView paramImageView, Uri paramUri)
  {
    zza(new zzc(paramImageView, paramUri));
  }
  
  public final void loadImage(ImageView paramImageView, Uri paramUri, int paramInt)
  {
    paramImageView = new zzc(paramImageView, paramUri);
    paramImageView.zzaGh = paramInt;
    zza(paramImageView);
  }
  
  public final void loadImage(OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri)
  {
    zza(new zzd(paramOnImageLoadedListener, paramUri));
  }
  
  public final void loadImage(OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri, int paramInt)
  {
    paramOnImageLoadedListener = new zzd(paramOnImageLoadedListener, paramUri);
    paramOnImageLoadedListener.zzaGh = paramInt;
    zza(paramOnImageLoadedListener);
  }
  
  @KeepName
  final class ImageReceiver
    extends ResultReceiver
  {
    private final Uri mUri;
    private final ArrayList<zza> zzaGa;
    
    ImageReceiver(Uri paramUri)
    {
      super();
      this.mUri = paramUri;
      this.zzaGa = new ArrayList();
    }
    
    public final void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      paramBundle = (ParcelFileDescriptor)paramBundle.getParcelable("com.google.android.gms.extra.fileDescriptor");
      ImageManager.zzf(ImageManager.this).execute(new ImageManager.zzb(ImageManager.this, this.mUri, paramBundle));
    }
    
    public final void zzb(zza paramzza)
    {
      com.google.android.gms.common.internal.zzc.zzcz("ImageReceiver.addImageRequest() must be called in the main thread");
      this.zzaGa.add(paramzza);
    }
    
    public final void zzc(zza paramzza)
    {
      com.google.android.gms.common.internal.zzc.zzcz("ImageReceiver.removeImageRequest() must be called in the main thread");
      this.zzaGa.remove(paramzza);
    }
    
    public final void zzqV()
    {
      Intent localIntent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
      localIntent.putExtra("com.google.android.gms.extras.uri", this.mUri);
      localIntent.putExtra("com.google.android.gms.extras.resultReceiver", this);
      localIntent.putExtra("com.google.android.gms.extras.priority", 3);
      ImageManager.zzb(ImageManager.this).sendBroadcast(localIntent);
    }
  }
  
  public static abstract interface OnImageLoadedListener
  {
    public abstract void onImageLoaded(Uri paramUri, Drawable paramDrawable, boolean paramBoolean);
  }
  
  static final class zza
    extends LruCache<zzb, Bitmap>
  {}
  
  final class zzb
    implements Runnable
  {
    private final Uri mUri;
    private final ParcelFileDescriptor zzaGc;
    
    public zzb(Uri paramUri, ParcelFileDescriptor paramParcelFileDescriptor)
    {
      this.mUri = paramUri;
      this.zzaGc = paramParcelFileDescriptor;
    }
    
    public final void run()
    {
      if (Looper.getMainLooper().getThread() == Thread.currentThread())
      {
        localObject1 = String.valueOf(Thread.currentThread());
        localObject3 = String.valueOf(Looper.getMainLooper().getThread());
        Log.e("Asserts", String.valueOf(localObject1).length() + 56 + String.valueOf(localObject3).length() + "checkNotMainThread: current thread " + (String)localObject1 + " IS the main thread " + (String)localObject3 + "!");
        throw new IllegalStateException("LoadBitmapFromDiskRunnable can't be executed in the main thread");
      }
      boolean bool1 = false;
      boolean bool2 = false;
      Object localObject1 = null;
      Object localObject3 = null;
      if (this.zzaGc != null) {}
      try
      {
        localObject1 = BitmapFactory.decodeFileDescriptor(this.zzaGc.getFileDescriptor());
        bool1 = bool2;
        String str2;
        Object localObject2;
        String str1;
        return;
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        try
        {
          for (;;)
          {
            this.zzaGc.close();
            localObject3 = new CountDownLatch(1);
            ImageManager.zzg(ImageManager.this).post(new ImageManager.zzd(ImageManager.this, this.mUri, (Bitmap)localObject1, bool1, (CountDownLatch)localObject3));
            try
            {
              ((CountDownLatch)localObject3).await();
              return;
            }
            catch (InterruptedException localInterruptedException)
            {
              str1 = String.valueOf(this.mUri);
              Log.w("ImageManager", String.valueOf(str1).length() + 32 + "Latch interrupted while posting " + str1);
            }
            localOutOfMemoryError = localOutOfMemoryError;
            str2 = String.valueOf(this.mUri);
            Log.e("ImageManager", String.valueOf(str2).length() + 34 + "OOM while loading bitmap for uri: " + str2, localOutOfMemoryError);
            bool1 = true;
            localObject2 = localObject3;
          }
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            Log.e("ImageManager", "closed failed", localIOException);
          }
        }
      }
    }
  }
  
  final class zzc
    implements Runnable
  {
    private final zza zzaGd;
    
    public zzc(zza paramzza)
    {
      this.zzaGd = paramzza;
    }
    
    public final void run()
    {
      com.google.android.gms.common.internal.zzc.zzcz("LoadImageRunnable must be executed on the main thread");
      Object localObject1 = (ImageManager.ImageReceiver)ImageManager.zza(ImageManager.this).get(this.zzaGd);
      if (localObject1 != null)
      {
        ImageManager.zza(ImageManager.this).remove(this.zzaGd);
        ((ImageManager.ImageReceiver)localObject1).zzc(this.zzaGd);
      }
      zzb localzzb = this.zzaGd.zzaGf;
      if (localzzb.uri == null)
      {
        this.zzaGd.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this), true);
        return;
      }
      localObject1 = ImageManager.zza(ImageManager.this, localzzb);
      if (localObject1 != null)
      {
        this.zzaGd.zza(ImageManager.zzb(ImageManager.this), (Bitmap)localObject1, true);
        return;
      }
      localObject1 = (Long)ImageManager.zzd(ImageManager.this).get(localzzb.uri);
      if (localObject1 != null)
      {
        if (SystemClock.elapsedRealtime() - ((Long)localObject1).longValue() < 3600000L)
        {
          this.zzaGd.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this), true);
          return;
        }
        ImageManager.zzd(ImageManager.this).remove(localzzb.uri);
      }
      this.zzaGd.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this));
      ??? = (ImageManager.ImageReceiver)ImageManager.zze(ImageManager.this).get(localzzb.uri);
      localObject1 = ???;
      if (??? == null)
      {
        localObject1 = new ImageManager.ImageReceiver(ImageManager.this, localzzb.uri);
        ImageManager.zze(ImageManager.this).put(localzzb.uri, localObject1);
      }
      ((ImageManager.ImageReceiver)localObject1).zzb(this.zzaGd);
      if (!(this.zzaGd instanceof zzd)) {
        ImageManager.zza(ImageManager.this).put(this.zzaGd, localObject1);
      }
      synchronized (ImageManager.zzoG())
      {
        if (!ImageManager.zzqU().contains(localzzb.uri))
        {
          ImageManager.zzqU().add(localzzb.uri);
          ((ImageManager.ImageReceiver)localObject1).zzqV();
        }
        return;
      }
    }
  }
  
  final class zzd
    implements Runnable
  {
    private final Bitmap mBitmap;
    private final Uri mUri;
    private boolean zzaGe;
    private final CountDownLatch zztJ;
    
    public zzd(Uri paramUri, Bitmap paramBitmap, boolean paramBoolean, CountDownLatch paramCountDownLatch)
    {
      this.mUri = paramUri;
      this.mBitmap = paramBitmap;
      this.zzaGe = paramBoolean;
      this.zztJ = paramCountDownLatch;
    }
    
    public final void run()
    {
      com.google.android.gms.common.internal.zzc.zzcz("OnBitmapLoadedRunnable must be executed in the main thread");
      int i;
      if (this.mBitmap != null) {
        i = 1;
      }
      while (ImageManager.zzh(ImageManager.this) != null) {
        if (this.zzaGe)
        {
          ImageManager.zzh(ImageManager.this).evictAll();
          System.gc();
          this.zzaGe = false;
          ImageManager.zzg(ImageManager.this).post(this);
          return;
          i = 0;
        }
        else if (i != 0)
        {
          ImageManager.zzh(ImageManager.this).put(new zzb(this.mUri), this.mBitmap);
        }
      }
      ??? = (ImageManager.ImageReceiver)ImageManager.zze(ImageManager.this).remove(this.mUri);
      if (??? != null)
      {
        ??? = ImageManager.ImageReceiver.zza((ImageManager.ImageReceiver)???);
        int k = ((ArrayList)???).size();
        int j = 0;
        if (j < k)
        {
          zza localzza = (zza)((ArrayList)???).get(j);
          if (i != 0) {
            localzza.zza(ImageManager.zzb(ImageManager.this), this.mBitmap, false);
          }
          for (;;)
          {
            if (!(localzza instanceof zzd)) {
              ImageManager.zza(ImageManager.this).remove(localzza);
            }
            j += 1;
            break;
            ImageManager.zzd(ImageManager.this).put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
            localzza.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this), false);
          }
        }
      }
      this.zztJ.countDown();
      synchronized (ImageManager.zzoG())
      {
        ImageManager.zzqU().remove(this.mUri);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/ImageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */