package org.telegram.messenger;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
import org.telegram.tgnet.TLRPC.User;

public class GoogleVoiceClientService
  extends SearchActionVerificationClientService
{
  public boolean performAction(final Intent paramIntent, boolean paramBoolean, Bundle paramBundle)
  {
    if (paramBoolean)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          try
          {
            ApplicationLoader.postInitApplication();
            String str1 = paramIntent.getStringExtra("android.intent.extra.TEXT");
            String str2 = paramIntent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
            if ((str1 != null) && (str1.length() > 0))
            {
              int i = Integer.parseInt(paramIntent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
              TLRPC.User localUser2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
              TLRPC.User localUser1 = localUser2;
              if (localUser2 == null)
              {
                localUser2 = MessagesStorage.getInstance().getUserSync(i);
                localUser1 = localUser2;
                if (localUser2 != null)
                {
                  MessagesController.getInstance().putUser(localUser2, true);
                  localUser1 = localUser2;
                }
              }
              if (localUser1 != null)
              {
                ContactsController.getInstance().markAsContacted(str2);
                SendMessagesHelper.getInstance().sendMessage(str1, localUser1.id, null, null, true, null, null, null);
              }
            }
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
      return true;
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GoogleVoiceClientService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */