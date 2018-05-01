package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.iid.InstanceID;

public class GcmRegistrationIntentService
  extends IntentService
{
  public GcmRegistrationIntentService()
  {
    super("GcmRegistrationIntentService");
  }
  
  private void sendRegistrationToServer(final String paramString)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SharedConfig.pushString = paramString;
        final int i = 0;
        while (i < 3)
        {
          UserConfig localUserConfig = UserConfig.getInstance(i);
          localUserConfig.registeredForPush = false;
          localUserConfig.saveConfig(false);
          if (localUserConfig.getClientUserId() != 0) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance(i).registerForPush(GcmRegistrationIntentService.3.this.val$token);
              }
            });
          }
          i += 1;
        }
      }
    });
  }
  
  protected void onHandleIntent(Intent paramIntent)
  {
    try
    {
      final String str = InstanceID.getInstance(this).getToken(getString(NUM), "GCM", null);
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("GCM Registration Token: " + str);
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          ApplicationLoader.postInitApplication();
          GcmRegistrationIntentService.this.sendRegistrationToServer(str);
        }
      });
      return;
    }
    catch (Exception localException)
    {
      final int i;
      do
      {
        do
        {
          FileLog.e(localException);
        } while (paramIntent == null);
        i = paramIntent.getIntExtra("failCount", 0);
      } while (i >= 60);
      paramIntent = new Runnable()
      {
        public void run()
        {
          try
          {
            Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
            localIntent.putExtra("failCount", i + 1);
            try
            {
              GcmRegistrationIntentService.this.startService(localIntent);
              return;
            }
            catch (Throwable localThrowable)
            {
              FileLog.e(localThrowable);
              return;
            }
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      };
      if (i >= 20) {}
    }
    for (long l = 10000L;; l = 1800000L)
    {
      AndroidUtilities.runOnUIThread(paramIntent, l);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GcmRegistrationIntentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */