package com.google.android.gms.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class InstanceIDListenerService
  extends zzb
{
  static void zza(Context paramContext, zzo paramzzo)
  {
    paramzzo.zzavj();
    paramzzo = new Intent("com.google.android.gms.iid.InstanceID");
    paramzzo.putExtra("CMD", "RST");
    paramzzo.setClassName(paramContext, "com.google.android.gms.gcm.GcmReceiver");
    paramContext.sendBroadcast(paramzzo);
  }
  
  public void handleIntent(Intent paramIntent)
  {
    if (!"com.google.android.gms.iid.InstanceID".equals(paramIntent.getAction())) {}
    String str1;
    String str2;
    do
    {
      do
      {
        return;
        Object localObject = null;
        str1 = paramIntent.getStringExtra("subtype");
        if (str1 != null)
        {
          localObject = new Bundle();
          ((Bundle)localObject).putString("subtype", str1);
        }
        localObject = InstanceID.getInstance(this, (Bundle)localObject);
        str2 = paramIntent.getStringExtra("CMD");
        if (Log.isLoggable("InstanceID", 3)) {
          Log.d("InstanceID", String.valueOf(str1).length() + 34 + String.valueOf(str2).length() + "Service command. subtype:" + str1 + " command:" + str2);
        }
        if ("gcm.googleapis.com/refresh".equals(paramIntent.getStringExtra("from")))
        {
          InstanceID.zzavg().zzia(str1);
          onTokenRefresh();
          return;
        }
        if ("RST".equals(str2))
        {
          ((InstanceID)localObject).zzavf();
          onTokenRefresh();
          return;
        }
        if (!"RST_FULL".equals(str2)) {
          break;
        }
      } while (InstanceID.zzavg().isEmpty());
      InstanceID.zzavg().zzavj();
      onTokenRefresh();
      return;
    } while (!"SYNC".equals(str2));
    InstanceID.zzavg().zzia(str1);
    onTokenRefresh();
  }
  
  public void onTokenRefresh() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/InstanceIDListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */