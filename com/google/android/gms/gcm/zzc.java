package com.google.android.gms.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.concurrent.BlockingQueue;

final class zzc
  extends Handler
{
  zzc(GoogleCloudMessaging paramGoogleCloudMessaging, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    if ((paramMessage == null) || (!(paramMessage.obj instanceof Intent))) {
      Log.w("GCM", "Dropping invalid message");
    }
    paramMessage = (Intent)paramMessage.obj;
    if ("com.google.android.c2dm.intent.REGISTRATION".equals(paramMessage.getAction())) {
      GoogleCloudMessaging.zza(this.zzbfU).add(paramMessage);
    }
    while (GoogleCloudMessaging.zza(this.zzbfU, paramMessage)) {
      return;
    }
    paramMessage.setPackage(GoogleCloudMessaging.zzb(this.zzbfU).getPackageName());
    GoogleCloudMessaging.zzb(this.zzbfU).sendBroadcast(paramMessage);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */