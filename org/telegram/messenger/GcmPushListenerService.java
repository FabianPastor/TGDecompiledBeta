package org.telegram.messenger;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;

public class GcmPushListenerService
  extends GcmListenerService
{
  public static final int NOTIFICATION_ID = 1;
  
  public void onMessageReceived(String paramString, final Bundle paramBundle)
  {
    FileLog.d("tmessages", "GCM received bundle: " + paramBundle + " from: " + paramString);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        
        try
        {
          if ("DC_UPDATE".equals(paramBundle.getString("loc_key")))
          {
            Object localObject = new JSONObject(paramBundle.getString("custom"));
            int i = ((JSONObject)localObject).getInt("dc");
            localObject = ((JSONObject)localObject).getString("addr").split(":");
            if (localObject.length != 2) {
              return;
            }
            String str = localObject[0];
            int j = Integer.parseInt(localObject[1]);
            ConnectionsManager.getInstance().applyDatacenterAddress(i, str, j);
          }
          ConnectionsManager.onInternalPushReceived();
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        }
        ConnectionsManager.getInstance().resumeNetworkMaybe();
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GcmPushListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */