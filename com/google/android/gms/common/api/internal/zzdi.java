package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

final class zzdi
  extends Handler
{
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
      paramMessage = zzdg.zzf(this.zzfvf);
      if (localPendingResult1 == null) {}
      for (;;)
      {
        try
        {
          zzdg.zza(zzdg.zzg(this.zzfvf), new Status(13, "Transform returned null"));
          return;
        }
        finally {}
        if ((localPendingResult2 instanceof zzct)) {
          zzdg.zza(zzdg.zzg(this.zzfvf), ((zzct)localPendingResult2).getStatus());
        } else {
          zzdg.zzg(this.zzfvf).zza(localPendingResult2);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */