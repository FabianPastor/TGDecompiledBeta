package com.google.android.gms.common.images;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzc;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public final class zzd
  extends zza
{
  private WeakReference<ImageManager.OnImageLoadedListener> zzaGn;
  
  public zzd(ImageManager.OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri)
  {
    super(paramUri, 0);
    zzc.zzr(paramOnImageLoadedListener);
    this.zzaGn = new WeakReference(paramOnImageLoadedListener);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof zzd)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    paramObject = (zzd)paramObject;
    ImageManager.OnImageLoadedListener localOnImageLoadedListener1 = (ImageManager.OnImageLoadedListener)this.zzaGn.get();
    ImageManager.OnImageLoadedListener localOnImageLoadedListener2 = (ImageManager.OnImageLoadedListener)((zzd)paramObject).zzaGn.get();
    return (localOnImageLoadedListener2 != null) && (localOnImageLoadedListener1 != null) && (zzbe.equal(localOnImageLoadedListener2, localOnImageLoadedListener1)) && (zzbe.equal(((zzd)paramObject).zzaGf, this.zzaGf));
  }
  
  public final int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.zzaGf });
  }
  
  protected final void zza(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (!paramBoolean2)
    {
      ImageManager.OnImageLoadedListener localOnImageLoadedListener = (ImageManager.OnImageLoadedListener)this.zzaGn.get();
      if (localOnImageLoadedListener != null) {
        localOnImageLoadedListener.onImageLoaded(this.zzaGf.uri, paramDrawable, paramBoolean3);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */