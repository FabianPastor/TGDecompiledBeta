package com.google.android.gms.common.images;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzc;
import com.google.android.gms.internal.zzrt;
import com.google.android.gms.internal.zzru;
import com.google.android.gms.internal.zzrv;
import java.lang.ref.WeakReference;

public abstract class zza
{
  final zza At;
  protected int Au = 0;
  protected int Av = 0;
  protected boolean Aw = false;
  private boolean Ax = true;
  private boolean Ay = false;
  private boolean Az = true;
  
  public zza(Uri paramUri, int paramInt)
  {
    this.At = new zza(paramUri);
    this.Av = paramInt;
  }
  
  private Drawable zza(Context paramContext, zzrv paramzzrv, int paramInt)
  {
    return paramContext.getResources().getDrawable(paramInt);
  }
  
  protected zzrt zza(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    if (paramDrawable1 != null)
    {
      localDrawable = paramDrawable1;
      if (!(paramDrawable1 instanceof zzrt)) {}
    }
    for (Drawable localDrawable = ((zzrt)paramDrawable1).zzatn();; localDrawable = null) {
      return new zzrt(localDrawable, paramDrawable2);
    }
  }
  
  void zza(Context paramContext, Bitmap paramBitmap, boolean paramBoolean)
  {
    zzc.zzu(paramBitmap);
    zza(new BitmapDrawable(paramContext.getResources(), paramBitmap), paramBoolean, false, true);
  }
  
  void zza(Context paramContext, zzrv paramzzrv)
  {
    if (this.Az) {
      zza(null, false, true, false);
    }
  }
  
  void zza(Context paramContext, zzrv paramzzrv, boolean paramBoolean)
  {
    Drawable localDrawable = null;
    if (this.Av != 0) {
      localDrawable = zza(paramContext, paramzzrv, this.Av);
    }
    zza(localDrawable, paramBoolean, false, false);
  }
  
  protected abstract void zza(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  protected boolean zzc(boolean paramBoolean1, boolean paramBoolean2)
  {
    return (this.Ax) && (!paramBoolean2) && (!paramBoolean1);
  }
  
  public void zzgh(int paramInt)
  {
    this.Av = paramInt;
  }
  
  static final class zza
  {
    public final Uri uri;
    
    public zza(Uri paramUri)
    {
      this.uri = paramUri;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof zza)) {
        return false;
      }
      if (this == paramObject) {
        return true;
      }
      return zzab.equal(((zza)paramObject).uri, this.uri);
    }
    
    public int hashCode()
    {
      return zzab.hashCode(new Object[] { this.uri });
    }
  }
  
  public static final class zzb
    extends zza
  {
    private WeakReference<ImageView> AA;
    
    public zzb(ImageView paramImageView, int paramInt)
    {
      super(paramInt);
      zzc.zzu(paramImageView);
      this.AA = new WeakReference(paramImageView);
    }
    
    public zzb(ImageView paramImageView, Uri paramUri)
    {
      super(0);
      zzc.zzu(paramImageView);
      this.AA = new WeakReference(paramImageView);
    }
    
    private void zza(ImageView paramImageView, Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      if ((!paramBoolean2) && (!paramBoolean3)) {}
      for (int i = 1; (i != 0) && ((paramImageView instanceof zzru)); i = 0)
      {
        int j = ((zzru)paramImageView).zzatp();
        if ((this.Av == 0) || (j != this.Av)) {
          break;
        }
        return;
      }
      paramBoolean1 = zzc(paramBoolean1, paramBoolean2);
      if (paramBoolean1) {
        paramDrawable = zza(paramImageView.getDrawable(), paramDrawable);
      }
      for (;;)
      {
        paramImageView.setImageDrawable(paramDrawable);
        zzru localzzru;
        if ((paramImageView instanceof zzru))
        {
          localzzru = (zzru)paramImageView;
          if (!paramBoolean3) {
            break label149;
          }
          paramImageView = this.At.uri;
          label110:
          localzzru.zzq(paramImageView);
          if (i == 0) {
            break label154;
          }
        }
        label149:
        label154:
        for (i = this.Av;; i = 0)
        {
          localzzru.zzgj(i);
          if (!paramBoolean1) {
            break;
          }
          ((zzrt)paramDrawable).startTransition(250);
          return;
          paramImageView = null;
          break label110;
        }
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof zzb)) {
        return false;
      }
      if (this == paramObject) {
        return true;
      }
      Object localObject = (zzb)paramObject;
      paramObject = (ImageView)this.AA.get();
      localObject = (ImageView)((zzb)localObject).AA.get();
      if ((localObject != null) && (paramObject != null) && (zzab.equal(localObject, paramObject))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int hashCode()
    {
      return 0;
    }
    
    protected void zza(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      ImageView localImageView = (ImageView)this.AA.get();
      if (localImageView != null) {
        zza(localImageView, paramDrawable, paramBoolean1, paramBoolean2, paramBoolean3);
      }
    }
  }
  
  public static final class zzc
    extends zza
  {
    private WeakReference<ImageManager.OnImageLoadedListener> AB;
    
    public zzc(ImageManager.OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri)
    {
      super(0);
      zzc.zzu(paramOnImageLoadedListener);
      this.AB = new WeakReference(paramOnImageLoadedListener);
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof zzc)) {
        return false;
      }
      if (this == paramObject) {
        return true;
      }
      paramObject = (zzc)paramObject;
      ImageManager.OnImageLoadedListener localOnImageLoadedListener1 = (ImageManager.OnImageLoadedListener)this.AB.get();
      ImageManager.OnImageLoadedListener localOnImageLoadedListener2 = (ImageManager.OnImageLoadedListener)((zzc)paramObject).AB.get();
      if ((localOnImageLoadedListener2 != null) && (localOnImageLoadedListener1 != null) && (zzab.equal(localOnImageLoadedListener2, localOnImageLoadedListener1)) && (zzab.equal(((zzc)paramObject).At, this.At))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int hashCode()
    {
      return zzab.hashCode(new Object[] { this.At });
    }
    
    protected void zza(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      if (!paramBoolean2)
      {
        ImageManager.OnImageLoadedListener localOnImageLoadedListener = (ImageManager.OnImageLoadedListener)this.AB.get();
        if (localOnImageLoadedListener != null) {
          localOnImageLoadedListener.onImageLoaded(this.At.uri, paramDrawable, paramBoolean3);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */