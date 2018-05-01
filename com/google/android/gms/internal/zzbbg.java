package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public final class zzbbg<R extends Result>
  extends Handler
{
  public zzbbg()
  {
    this(Looper.getMainLooper());
  }
  
  public zzbbg(Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      int i = paramMessage.what;
      Log.wtf("BasePendingResult", 45 + "Don't know how to handle message: " + i, new Exception());
      return;
    case 1: 
      Object localObject = (Pair)paramMessage.obj;
      paramMessage = (ResultCallback)((Pair)localObject).first;
      localObject = (Result)((Pair)localObject).second;
      try
      {
        paramMessage.onResult((Result)localObject);
        return;
      }
      catch (RuntimeException paramMessage)
      {
        zzbbe.zzc((Result)localObject);
        throw paramMessage;
      }
    }
    ((zzbbe)paramMessage.obj).zzs(Status.zzaBp);
  }
  
  public final void zza(ResultCallback<? super R> paramResultCallback, R paramR)
  {
    sendMessage(obtainMessage(1, new Pair(paramResultCallback, paramR)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */