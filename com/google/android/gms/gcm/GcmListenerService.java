package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

public abstract class GcmListenerService
  extends Service
{
  private int aeC;
  private int aeD = 0;
  private final Object zzakd = new Object();
  
  static void zzac(Bundle paramBundle)
  {
    paramBundle = paramBundle.keySet().iterator();
    while (paramBundle.hasNext())
    {
      String str = (String)paramBundle.next();
      if ((str != null) && (str.startsWith("google.c."))) {
        paramBundle.remove();
      }
    }
  }
  
  private void zzbnx()
  {
    synchronized (this.zzakd)
    {
      this.aeD -= 1;
      if (this.aeD == 0) {
        zztk(this.aeC);
      }
      return;
    }
  }
  
  @TargetApi(11)
  private void zzl(final Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable()
      {
        public void run()
        {
          GcmListenerService.zza(GcmListenerService.this, paramIntent);
        }
      });
      return;
    }
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        GcmListenerService.zza(GcmListenerService.this, paramIntent);
        return null;
      }
    }.execute(new Void[0]);
  }
  
  private void zzm(Intent paramIntent)
  {
    for (;;)
    {
      int i;
      try
      {
        str1 = paramIntent.getAction();
        i = -1;
        switch (str1.hashCode())
        {
        case 366519424: 
          str1 = String.valueOf(paramIntent.getAction());
          if (str1.length() == 0) {
            break label97;
          }
          str1 = "Unknown intent action: ".concat(str1);
          Log.d("GcmListenerService", str1);
          zzbnx();
          return;
        }
      }
      finally
      {
        String str1;
        GcmReceiver.completeWakefulIntent(paramIntent);
      }
      if (str1.equals("com.google.android.c2dm.intent.RECEIVE"))
      {
        i = 0;
        break label110;
        zzn(paramIntent);
        continue;
        label97:
        String str2 = new String("Unknown intent action: ");
        continue;
      }
      label110:
      switch (i)
      {
      }
    }
  }
  
  private void zzn(Intent paramIntent)
  {
    String str2 = paramIntent.getStringExtra("message_type");
    String str1 = str2;
    if (str2 == null) {
      str1 = "gcm";
    }
    int i = -1;
    switch (str1.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        paramIntent = String.valueOf(str1);
        if (paramIntent.length() == 0) {}
        break;
      }
      break;
    }
    for (paramIntent = "Received message with unknown type: ".concat(paramIntent);; paramIntent = new String("Received message with unknown type: "))
    {
      Log.w("GcmListenerService", paramIntent);
      return;
      if (!str1.equals("gcm")) {
        break;
      }
      i = 0;
      break;
      if (!str1.equals("deleted_messages")) {
        break;
      }
      i = 1;
      break;
      if (!str1.equals("send_event")) {
        break;
      }
      i = 2;
      break;
      if (!str1.equals("send_error")) {
        break;
      }
      i = 3;
      break;
      zzo(paramIntent);
      return;
      onDeletedMessages();
      return;
      onMessageSent(paramIntent.getStringExtra("google.message_id"));
      return;
      onSendError(zzp(paramIntent), paramIntent.getStringExtra("error"));
      return;
    }
  }
  
  private void zzo(Intent paramIntent)
  {
    paramIntent = paramIntent.getExtras();
    paramIntent.remove("message_type");
    paramIntent.remove("android.support.content.wakelockid");
    if (zza.zzad(paramIntent))
    {
      if (!zza.zzdd(this))
      {
        zza.zzdc(this).zzaf(paramIntent);
        return;
      }
      zza.zzae(paramIntent);
    }
    String str = paramIntent.getString("from");
    paramIntent.remove("from");
    zzac(paramIntent);
    onMessageReceived(str, paramIntent);
  }
  
  private String zzp(Intent paramIntent)
  {
    String str2 = paramIntent.getStringExtra("google.message_id");
    String str1 = str2;
    if (str2 == null) {
      str1 = paramIntent.getStringExtra("message_id");
    }
    return str1;
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onDeletedMessages() {}
  
  public void onMessageReceived(String paramString, Bundle paramBundle) {}
  
  public void onMessageSent(String paramString) {}
  
  public void onSendError(String paramString1, String paramString2) {}
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    synchronized (this.zzakd)
    {
      this.aeC = paramInt2;
      this.aeD += 1;
      if (paramIntent == null)
      {
        zzbnx();
        return 2;
      }
    }
    zzl(paramIntent);
    return 3;
  }
  
  boolean zztk(int paramInt)
  {
    return stopSelfResult(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */