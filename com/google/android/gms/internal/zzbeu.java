package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

final class zzbeu
  extends Handler
{
  public zzbeu(zzbes paramzzbes, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      int i = paramMessage.what;
      Log.e("TransformedResultImpl", 70 + "TransformationResultHandler received unknown message type: " + i);
      return;
    case 0: 
      PendingResult localPendingResult1 = (PendingResult)paramMessage.obj;
      paramMessage = zzbes.zzf(this.zzaFi);
      if (localPendingResult1 == null) {}
      for (;;)
      {
        try
        {
          zzbes.zza(zzbes.zzg(this.zzaFi), new Status(13, "Transform returned null"));
          return;
        }
        finally {}
        if ((localPendingResult2 instanceof zzbeh)) {
          zzbes.zza(zzbes.zzg(this.zzaFi), ((zzbeh)localPendingResult2).getStatus());
        } else {
          zzbes.zzg(this.zzaFi).zza(localPendingResult2);
        }
      }
    }
    RuntimeException localRuntimeException = (RuntimeException)paramMessage.obj;
    paramMessage = String.valueOf(localRuntimeException.getMessage());
    if (paramMessage.length() != 0) {}
    for (paramMessage = "Runtime exception on the transformation worker thread: ".concat(paramMessage);; paramMessage = new String("Runtime exception on the transformation worker thread: "))
    {
      Log.e("TransformedResultImpl", paramMessage);
      throw localRuntimeException;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbeu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */