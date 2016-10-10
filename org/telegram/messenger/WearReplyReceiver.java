package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

public class WearReplyReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    ApplicationLoader.postInitApplication();
    paramContext = RemoteInput.getResultsFromIntent(paramIntent);
    if (paramContext == null) {}
    long l;
    int i;
    do
    {
      do
      {
        return;
        paramContext = paramContext.getCharSequence("extra_voice_reply");
      } while ((paramContext == null) || (paramContext.length() == 0));
      l = paramIntent.getLongExtra("dialog_id", 0L);
      i = paramIntent.getIntExtra("max_id", 0);
    } while ((l == 0L) || (i == 0));
    SendMessagesHelper.getInstance().sendMessage(paramContext.toString(), l, null, null, true, null, null, null);
    MessagesController.getInstance().markDialogAsRead(l, i, i, 0, true, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/WearReplyReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */