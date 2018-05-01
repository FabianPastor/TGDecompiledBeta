package com.google.android.gms.common.images;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.gms.common.internal.zzc;
import com.google.android.gms.internal.zzbfm;

public abstract class zza
{
  final zzb zzaGf;
  private int zzaGg = 0;
  protected int zzaGh = 0;
  private boolean zzaGi = false;
  private boolean zzaGj = true;
  private boolean zzaGk = false;
  private boolean zzaGl = true;
  
  public zza(Uri paramUri, int paramInt)
  {
    this.zzaGf = new zzb(paramUri);
    this.zzaGh = paramInt;
  }
  
  final void zza(Context paramContext, Bitmap paramBitmap, boolean paramBoolean)
  {
    zzc.zzr(paramBitmap);
    zza(new BitmapDrawable(paramContext.getResources(), paramBitmap), paramBoolean, false, true);
  }
  
  final void zza(Context paramContext, zzbfm paramzzbfm)
  {
    if (this.zzaGl) {
      zza(null, false, true, false);
    }
  }
  
  final void zza(Context paramContext, zzbfm paramzzbfm, boolean paramBoolean)
  {
    paramzzbfm = null;
    if (this.zzaGh != 0)
    {
      int i = this.zzaGh;
      paramzzbfm = paramContext.getResources().getDrawable(i);
    }
    zza(paramzzbfm, paramBoolean, false, false);
  }
  
  protected abstract void zza(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  protected final boolean zzc(boolean paramBoolean1, boolean paramBoolean2)
  {
    return (this.zzaGj) && (!paramBoolean2) && (!paramBoolean1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/images/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */