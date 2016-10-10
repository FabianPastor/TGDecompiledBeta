package com.google.android.gms.common.images;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import com.google.android.gms.common.internal.zzc;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.internal.zzrv;
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
  private static final Object Ae = new Object();
  private static HashSet<Uri> Af = new HashSet();
  private static ImageManager Ag;
  private static ImageManager Ah;
  private final ExecutorService Ai;
  private final zzb Aj;
  private final zzrv Ak;
  private final Map<zza, ImageReceiver> Al;
  private final Map<Uri, ImageReceiver> Am;
  private final Map<Uri, Long> An;
  private final Context mContext;
  private final Handler mHandler;
  
  private ImageManager(Context paramContext, boolean paramBoolean)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mHandler = new Handler(Looper.getMainLooper());
    this.Ai = Executors.newFixedThreadPool(4);
    if (paramBoolean)
    {
      this.Aj = new zzb(this.mContext);
      if (zzs.zzaxn()) {
        zzatk();
      }
    }
    for (;;)
    {
      this.Ak = new zzrv();
      this.Al = new HashMap();
      this.Am = new HashMap();
      this.An = new HashMap();
      return;
      this.Aj = null;
    }
  }
  
  public static ImageManager create(Context paramContext)
  {
    return zzg(paramContext, false);
  }
  
  private Bitmap zza(zza.zza paramzza)
  {
    if (this.Aj == null) {
      return null;
    }
    return (Bitmap)this.Aj.get(paramzza);
  }
  
  @TargetApi(14)
  private void zzatk()
  {
    this.mContext.registerComponentCallbacks(new zze(this.Aj));
  }
  
  public static ImageManager zzg(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (Ah == null) {
        Ah = new ImageManager(paramContext, true);
      }
      return Ah;
    }
    if (Ag == null) {
      Ag = new ImageManager(paramContext, false);
    }
    return Ag;
  }
  
  public void loadImage(ImageView paramImageView, int paramInt)
  {
    zza(new zza.zzb(paramImageView, paramInt));
  }
  
  public void loadImage(ImageView paramImageView, Uri paramUri)
  {
    zza(new zza.zzb(paramImageView, paramUri));
  }
  
  public void loadImage(ImageView paramImageView, Uri paramUri, int paramInt)
  {
    paramImageView = new zza.zzb(paramImageView, paramUri);
    paramImageView.zzgh(paramInt);
    zza(paramImageView);
  }
  
  public void loadImage(OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri)
  {
    zza(new zza.zzc(paramOnImageLoadedListener, paramUri));
  }
  
  public void loadImage(OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri, int paramInt)
  {
    paramOnImageLoadedListener = new zza.zzc(paramOnImageLoadedListener, paramUri);
    paramOnImageLoadedListener.zzgh(paramInt);
    zza(paramOnImageLoadedListener);
  }
  
  public void zza(zza paramzza)
  {
    zzc.zzhq("ImageManager.loadImage() must be called in the main thread");
    new zzd(paramzza).run();
  }
  
  @KeepName
  private final class ImageReceiver
    extends ResultReceiver
  {
    private final ArrayList<zza> Ao;
    private final Uri mUri;
    
    ImageReceiver(Uri paramUri)
    {
      super();
      this.mUri = paramUri;
      this.Ao = new ArrayList();
    }
    
    public void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      paramBundle = (ParcelFileDescriptor)paramBundle.getParcelable("com.google.android.gms.extra.fileDescriptor");
      ImageManager.zzf(ImageManager.this).execute(new ImageManager.zzc(ImageManager.this, this.mUri, paramBundle));
    }
    
    public void zzatm()
    {
      Intent localIntent = new Intent("com.google.android.gms.common.images.LOAD_IMAGE");
      localIntent.putExtra("com.google.android.gms.extras.uri", this.mUri);
      localIntent.putExtra("com.google.android.gms.extras.resultReceiver", this);
      localIntent.putExtra("com.google.android.gms.extras.priority", 3);
      ImageManager.zzb(ImageManager.this).sendBroadcast(localIntent);
    }
    
    public void zzb(zza paramzza)
    {
      zzc.zzhq("ImageReceiver.addImageRequest() must be called in the main thread");
      this.Ao.add(paramzza);
    }
    
    public void zzc(zza paramzza)
    {
      zzc.zzhq("ImageReceiver.removeImageRequest() must be called in the main thread");
      this.Ao.remove(paramzza);
    }
  }
  
  public static abstract interface OnImageLoadedListener
  {
    public abstract void onImageLoaded(Uri paramUri, Drawable paramDrawable, boolean paramBoolean);
  }
  
  @TargetApi(11)
  private static final class zza
  {
    static int zza(ActivityManager paramActivityManager)
    {
      return paramActivityManager.getLargeMemoryClass();
    }
  }
  
  private static final class zzb
    extends LruCache<zza.zza, Bitmap>
  {
    public zzb(Context paramContext)
    {
      super();
    }
    
    @TargetApi(11)
    private static int zzcc(Context paramContext)
    {
      ActivityManager localActivityManager = (ActivityManager)paramContext.getSystemService("activity");
      if ((paramContext.getApplicationInfo().flags & 0x100000) != 0)
      {
        i = 1;
        if ((i == 0) || (!zzs.zzaxk())) {
          break label55;
        }
      }
      label55:
      for (int i = ImageManager.zza.zza(localActivityManager);; i = localActivityManager.getMemoryClass())
      {
        return (int)(i * 1048576 * 0.33F);
        i = 0;
        break;
      }
    }
    
    protected int zza(zza.zza paramzza, Bitmap paramBitmap)
    {
      return paramBitmap.getHeight() * paramBitmap.getRowBytes();
    }
    
    protected void zza(boolean paramBoolean, zza.zza paramzza, Bitmap paramBitmap1, Bitmap paramBitmap2)
    {
      super.entryRemoved(paramBoolean, paramzza, paramBitmap1, paramBitmap2);
    }
  }
  
  private final class zzc
    implements Runnable
  {
    private final ParcelFileDescriptor Aq;
    private final Uri mUri;
    
    public zzc(Uri paramUri, ParcelFileDescriptor paramParcelFileDescriptor)
    {
      this.mUri = paramUri;
      this.Aq = paramParcelFileDescriptor;
    }
    
    public void run()
    {
      zzc.zzhr("LoadBitmapFromDiskRunnable can't be executed in the main thread");
      boolean bool1 = false;
      boolean bool2 = false;
      Bitmap localBitmap = null;
      CountDownLatch localCountDownLatch = null;
      if (this.Aq != null) {}
      try
      {
        localBitmap = BitmapFactory.decodeFileDescriptor(this.Aq.getFileDescriptor());
        bool1 = bool2;
        String str2;
        Object localObject;
        String str1;
        return;
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        try
        {
          for (;;)
          {
            this.Aq.close();
            localCountDownLatch = new CountDownLatch(1);
            ImageManager.zzg(ImageManager.this).post(new ImageManager.zzf(ImageManager.this, this.mUri, localBitmap, bool1, localCountDownLatch));
            try
            {
              localCountDownLatch.await();
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
            localObject = localCountDownLatch;
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
  
  private final class zzd
    implements Runnable
  {
    private final zza Ar;
    
    public zzd(zza paramzza)
    {
      this.Ar = paramzza;
    }
    
    public void run()
    {
      zzc.zzhq("LoadImageRunnable must be executed on the main thread");
      Object localObject1 = (ImageManager.ImageReceiver)ImageManager.zza(ImageManager.this).get(this.Ar);
      if (localObject1 != null)
      {
        ImageManager.zza(ImageManager.this).remove(this.Ar);
        ((ImageManager.ImageReceiver)localObject1).zzc(this.Ar);
      }
      zza.zza localzza = this.Ar.At;
      if (localzza.uri == null)
      {
        this.Ar.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this), true);
        return;
      }
      localObject1 = ImageManager.zza(ImageManager.this, localzza);
      if (localObject1 != null)
      {
        this.Ar.zza(ImageManager.zzb(ImageManager.this), (Bitmap)localObject1, true);
        return;
      }
      localObject1 = (Long)ImageManager.zzd(ImageManager.this).get(localzza.uri);
      if (localObject1 != null)
      {
        if (SystemClock.elapsedRealtime() - ((Long)localObject1).longValue() < 3600000L)
        {
          this.Ar.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this), true);
          return;
        }
        ImageManager.zzd(ImageManager.this).remove(localzza.uri);
      }
      this.Ar.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this));
      ??? = (ImageManager.ImageReceiver)ImageManager.zze(ImageManager.this).get(localzza.uri);
      localObject1 = ???;
      if (??? == null)
      {
        localObject1 = new ImageManager.ImageReceiver(ImageManager.this, localzza.uri);
        ImageManager.zze(ImageManager.this).put(localzza.uri, localObject1);
      }
      ((ImageManager.ImageReceiver)localObject1).zzb(this.Ar);
      if (!(this.Ar instanceof zza.zzc)) {
        ImageManager.zza(ImageManager.this).put(this.Ar, localObject1);
      }
      synchronized (ImageManager.zzaoj())
      {
        if (!ImageManager.zzatl().contains(localzza.uri))
        {
          ImageManager.zzatl().add(localzza.uri);
          ((ImageManager.ImageReceiver)localObject1).zzatm();
        }
        return;
      }
    }
  }
  
  @TargetApi(14)
  private static final class zze
    implements ComponentCallbacks2
  {
    private final ImageManager.zzb Aj;
    
    public zze(ImageManager.zzb paramzzb)
    {
      this.Aj = paramzzb;
    }
    
    public void onConfigurationChanged(Configuration paramConfiguration) {}
    
    public void onLowMemory()
    {
      this.Aj.evictAll();
    }
    
    public void onTrimMemory(int paramInt)
    {
      if (paramInt >= 60) {
        this.Aj.evictAll();
      }
      while (paramInt < 20) {
        return;
      }
      this.Aj.trimToSize(this.Aj.size() / 2);
    }
  }
  
  private final class zzf
    implements Runnable
  {
    private boolean As;
    private final Bitmap mBitmap;
    private final Uri mUri;
    private final CountDownLatch zzamx;
    
    public zzf(Uri paramUri, Bitmap paramBitmap, boolean paramBoolean, CountDownLatch paramCountDownLatch)
    {
      this.mUri = paramUri;
      this.mBitmap = paramBitmap;
      this.As = paramBoolean;
      this.zzamx = paramCountDownLatch;
    }
    
    private void zza(ImageManager.ImageReceiver paramImageReceiver, boolean paramBoolean)
    {
      paramImageReceiver = ImageManager.ImageReceiver.zza(paramImageReceiver);
      int j = paramImageReceiver.size();
      int i = 0;
      if (i < j)
      {
        zza localzza = (zza)paramImageReceiver.get(i);
        if (paramBoolean) {
          localzza.zza(ImageManager.zzb(ImageManager.this), this.mBitmap, false);
        }
        for (;;)
        {
          if (!(localzza instanceof zza.zzc)) {
            ImageManager.zza(ImageManager.this).remove(localzza);
          }
          i += 1;
          break;
          ImageManager.zzd(ImageManager.this).put(this.mUri, Long.valueOf(SystemClock.elapsedRealtime()));
          localzza.zza(ImageManager.zzb(ImageManager.this), ImageManager.zzc(ImageManager.this), false);
        }
      }
    }
    
    public void run()
    {
      zzc.zzhq("OnBitmapLoadedRunnable must be executed in the main thread");
      boolean bool;
      if (this.mBitmap != null) {
        bool = true;
      }
      while (ImageManager.zzh(ImageManager.this) != null) {
        if (this.As)
        {
          ImageManager.zzh(ImageManager.this).evictAll();
          System.gc();
          this.As = false;
          ImageManager.zzg(ImageManager.this).post(this);
          return;
          bool = false;
        }
        else if (bool)
        {
          ImageManager.zzh(ImageManager.this).put(new zza.zza(this.mUri), this.mBitmap);
        }
      }
      ??? = (ImageManager.ImageReceiver)ImageManager.zze(ImageManager.this).remove(this.mUri);
      if (??? != null) {
        zza((ImageManager.ImageReceiver)???, bool);
      }
      this.zzamx.countDown();
      synchronized (ImageManager.zzaoj())
      {
        ImageManager.zzatl().remove(this.mUri);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/ImageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */