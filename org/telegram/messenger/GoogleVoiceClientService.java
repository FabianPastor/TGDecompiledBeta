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
    if (paramBoolean) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          try
          {
            int i = UserConfig.selectedAccount;
            ApplicationLoader.postInitApplication();
            String str1 = paramIntent.getStringExtra("android.intent.extra.TEXT");
            String str2 = paramIntent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
            if ((str1 != null) && (str1.length() > 0))
            {
              int j = Integer.parseInt(paramIntent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
              TLRPC.User localUser1 = MessagesController.getInstance(i).getUser(Integer.valueOf(j));
              TLRPC.User localUser2 = localUser1;
              if (localUser1 == null)
              {
                localUser1 = MessagesStorage.getInstance(i).getUserSync(j);
                localUser2 = localUser1;
                if (localUser1 != null)
                {
                  MessagesController.getInstance(i).putUser(localUser1, true);
                  localUser2 = localUser1;
                }
              }
              if (localUser2 != null)
              {
                ContactsController.getInstance(i).markAsContacted(str2);
                SendMessagesHelper.getInstance(i).sendMessage(str1, localUser2.id, null, null, true, null, null, null);
              }
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/GoogleVoiceClientService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */