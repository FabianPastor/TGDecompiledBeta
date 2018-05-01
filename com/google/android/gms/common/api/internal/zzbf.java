package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbf
  extends Handler
{
  zzbf(zzbd paramzzbd, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      int i = paramMessage.what;
      Log.w("GACStateManager", 31 + "Unknown message id: " + i);
    case 1: 
      for (;;)
      {
        return;
        ((zzbe)paramMessage.obj).zzc(this.zzjh);
      }
    }
    throw ((RuntimeException)paramMessage.obj);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */