package com.google.firebase.iid;

import android.content.Intent;
import android.util.Log;
import java.util.Queue;

public class FirebaseInstanceIdService
  extends zzb
{
  public void onTokenRefresh() {}
  
  protected final Intent zzf(Intent paramIntent)
  {
    return (Intent)zzz.zzta().zzbrx.poll();
  }
  
  public final void zzh(Intent paramIntent)
  {
    if ("com.google.firebase.iid.TOKEN_REFRESH".equals(paramIntent.getAction())) {
      onTokenRefresh();
    }
    for (;;)
    {
      return;
      String str = paramIntent.getStringExtra("CMD");
      if (str != null)
      {
        if (Log.isLoggable("FirebaseInstanceId", 3))
        {
          paramIntent = String.valueOf(paramIntent.getExtras());
          Log.d("FirebaseInstanceId", String.valueOf(str).length() + 21 + String.valueOf(paramIntent).length() + "Received command: " + str + " - " + paramIntent);
        }
        if (("RST".equals(str)) || ("RST_FULL".equals(str))) {
          FirebaseInstanceId.getInstance().zzsk();
        } else if ("SYNC".equals(str)) {
          FirebaseInstanceId.getInstance().zzsl();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/FirebaseInstanceIdService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */