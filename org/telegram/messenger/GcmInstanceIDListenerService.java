package org.telegram.messenger;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GcmInstanceIDListenerService
  extends FirebaseInstanceIdService
{
  public static void sendRegistrationToServer(String paramString)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SharedConfig.pushString = this.val$token;
        for (final int i = 0; i < 3; i++)
        {
          UserConfig localUserConfig = UserConfig.getInstance(i);
          localUserConfig.registeredForPush = false;
          localUserConfig.saveConfig(false);
          if (localUserConfig.getClientUserId() != 0) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance(i).registerForPush(GcmInstanceIDListenerService.2.this.val$token);
              }
            });
          }
        }
      }
    });
  }
  
  public void onTokenRefresh()
  {
    try
    {
      String str = FirebaseInstanceId.getInstance().getToken();
      Runnable local1 = new org/telegram/messenger/GcmInstanceIDListenerService$1;
      local1.<init>(this, str);
      AndroidUtilities.runOnUIThread(local1);
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        FileLog.e(localThrowable);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GcmInstanceIDListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */