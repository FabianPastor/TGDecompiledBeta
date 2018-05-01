package com.google.android.gms.internal;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;

final class zzbfk
  extends Drawable.ConstantState
{
  int mChangingConfigurations;
  int zzaGD;
  
  zzbfk(zzbfk paramzzbfk)
  {
    if (paramzzbfk != null)
    {
      this.mChangingConfigurations = paramzzbfk.mChangingConfigurations;
      this.zzaGD = paramzzbfk.zzaGD;
    }
  }
  
  public final int getChangingConfigurations()
  {
    return this.mChangingConfigurations;
  }
  
  public final Drawable newDrawable()
  {
    return new zzbfg(this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbfk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */