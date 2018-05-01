package com.google.android.gms.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.iid.zzb;
import java.util.Iterator;
import java.util.Set;

public class GcmListenerService
  extends zzb
{
  static void zzq(Bundle paramBundle)
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
  
  public void handleIntent(Intent paramIntent)
  {
    if (!"com.google.android.c2dm.intent.RECEIVE".equals(paramIntent.getAction()))
    {
      paramIntent = String.valueOf(paramIntent.getAction());
      if (paramIntent.length() != 0) {}
      for (paramIntent = "Unknown intent action: ".concat(paramIntent);; paramIntent = new String("Unknown intent action: "))
      {
        Log.w("GcmListenerService", paramIntent);
        return;
      }
    }
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
      paramIntent = paramIntent.getExtras();
      paramIntent.remove("message_type");
      paramIntent.remove("android.support.content.wakelockid");
      if (("1".equals(zza.zze(paramIntent, "gcm.n.e"))) || (zza.zze(paramIntent, "gcm.n.icon") != null)) {
        i = 1;
      }
      while (i != 0) {
        if (!zza.zzdk(this))
        {
          zza.zzdj(this).zzs(paramIntent);
          return;
          i = 0;
        }
        else
        {
          zza.zzr(paramIntent);
        }
      }
      str1 = paramIntent.getString("from");
      paramIntent.remove("from");
      zzq(paramIntent);
      onMessageReceived(str1, paramIntent);
      return;
      onDeletedMessages();
      return;
      onMessageSent(paramIntent.getStringExtra("google.message_id"));
      return;
      str2 = paramIntent.getStringExtra("google.message_id");
      str1 = str2;
      if (str2 == null) {
        str1 = paramIntent.getStringExtra("message_id");
      }
      onSendError(str1, paramIntent.getStringExtra("error"));
      return;
    }
  }
  
  public void onDeletedMessages() {}
  
  public void onMessageReceived(String paramString, Bundle paramBundle) {}
  
  public void onMessageSent(String paramString) {}
  
  public void onSendError(String paramString1, String paramString2) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */