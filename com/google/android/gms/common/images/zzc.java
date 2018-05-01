package com.google.android.gms.common.images;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.internal.zzbfg;
import com.google.android.gms.internal.zzbfl;
import java.lang.ref.WeakReference;

public final class zzc
  extends zza
{
  private WeakReference<ImageView> zzaGm;
  
  public zzc(ImageView paramImageView, int paramInt)
  {
    super(null, paramInt);
    com.google.android.gms.common.internal.zzc.zzr(paramImageView);
    this.zzaGm = new WeakReference(paramImageView);
  }
  
  public zzc(ImageView paramImageView, Uri paramUri)
  {
    super(paramUri, 0);
    com.google.android.gms.common.internal.zzc.zzr(paramImageView);
    this.zzaGm = new WeakReference(paramImageView);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzc)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    Object localObject = (zzc)paramObject;
    paramObject = (ImageView)this.zzaGm.get();
    localObject = (ImageView)((zzc)localObject).zzaGm.get();
    return (localObject != null) && (paramObject != null) && (zzbe.equal(localObject, paramObject));
  }
  
  public final int hashCode()
  {
    return 0;
  }
  
  protected final void zza(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    Object localObject2 = null;
    ImageView localImageView = (ImageView)this.zzaGm.get();
    int i;
    Object localObject3;
    Object localObject1;
    if (localImageView != null)
    {
      if ((paramBoolean2) || (paramBoolean3)) {
        break label206;
      }
      i = 1;
      if ((i != 0) && ((localImageView instanceof zzbfl)))
      {
        int j = ((zzbfl)localImageView).zzqY();
        if ((this.zzaGh != 0) && (j == this.zzaGh)) {}
      }
      else
      {
        paramBoolean1 = zzc(paramBoolean1, paramBoolean2);
        if (!paramBoolean1) {
          break label224;
        }
        localObject3 = localImageView.getDrawable();
        if (localObject3 == null) {
          break label218;
        }
        localObject1 = localObject3;
        if ((localObject3 instanceof zzbfg)) {
          localObject1 = ((zzbfg)localObject3).zzqW();
        }
        label116:
        paramDrawable = new zzbfg((Drawable)localObject1, paramDrawable);
      }
    }
    label206:
    label212:
    label218:
    label224:
    for (;;)
    {
      localImageView.setImageDrawable(paramDrawable);
      if ((localImageView instanceof zzbfl))
      {
        localObject3 = (zzbfl)localImageView;
        localObject1 = localObject2;
        if (paramBoolean3) {
          localObject1 = this.zzaGf.uri;
        }
        ((zzbfl)localObject3).zzo((Uri)localObject1);
        if (i == 0) {
          break label212;
        }
      }
      for (i = this.zzaGh;; i = 0)
      {
        ((zzbfl)localObject3).zzax(i);
        if (paramBoolean1) {
          ((zzbfg)paramDrawable).startTransition(250);
        }
        return;
        i = 0;
        break;
      }
      localObject1 = null;
      break label116;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */