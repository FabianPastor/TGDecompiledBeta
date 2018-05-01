package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.common.internal.zzbq;

public final class zzcjm
{
  final Context mContext;
  
  public zzcjm(Context paramContext)
  {
    zzbq.checkNotNull(paramContext);
    paramContext = paramContext.getApplicationContext();
    zzbq.checkNotNull(paramContext);
    this.mContext = paramContext;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcjm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */