package com.google.android.gms.common.api.internal;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

final class zzcj
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
      Object localObject = (PendingResult)paramMessage.obj;
      paramMessage = zzch.zzf(this.zzml);
      if (localObject == null) {}
      for (;;)
      {
        try
        {
          zzch localzzch = zzch.zzg(this.zzml);
          localObject = new com/google/android/gms/common/api/Status;
          ((Status)localObject).<init>(13, "Transform returned null");
          zzch.zza(localzzch, (Status)localObject);
          break;
        }
        finally {}
        if ((localPendingResult instanceof zzbx)) {
          zzch.zza(zzch.zzg(this.zzml), ((zzbx)localPendingResult).getStatus());
        } else {
          zzch.zzg(this.zzml).zza(localPendingResult);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */