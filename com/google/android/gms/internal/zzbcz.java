package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class zzbcz
  extends Handler
{
  zzbcz(zzbcx paramzzbcx, Looper paramLooper)
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
      return;
    case 1: 
      ((zzbcy)paramMessage.obj).zzc(this.zzaEa);
      return;
    }
    throw ((RuntimeException)paramMessage.obj);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */