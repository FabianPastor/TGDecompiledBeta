package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;

public class NotificationRepeat
  extends IntentService
{
  public NotificationRepeat()
  {
    super("NotificationRepeat");
  }
  
  protected void onHandleIntent(Intent paramIntent)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationsController.getInstance().repeatNotificationMaybe();
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NotificationRepeat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */