package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.gms.common.internal.zzbo;

final class zzbdx
  extends Handler
{
  public zzbdx(zzbdw paramzzbdw, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    boolean bool = true;
    if (paramMessage.what == 1) {}
    for (;;)
    {
      zzbo.zzaf(bool);
      this.zzaEO.zzb((zzbdz)paramMessage.obj);
      return;
      bool = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */