package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.tgnet.ConnectionsManager;

public class ScreenReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.intent.action.SCREEN_OFF"))
    {
      FileLog.e("tmessages", "screen off");
      ConnectionsManager.getInstance().setAppPaused(true, true);
      ApplicationLoader.isScreenOn = false;
    }
    for (;;)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenStateChanged, new Object[0]);
      return;
      if (paramIntent.getAction().equals("android.intent.action.SCREEN_ON"))
      {
        FileLog.e("tmessages", "screen on");
        ConnectionsManager.getInstance().setAppPaused(false, true);
        ApplicationLoader.isScreenOn = true;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ScreenReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */