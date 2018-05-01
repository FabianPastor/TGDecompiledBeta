package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    boolean bool = false;
    if (!paramIntent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
    {
      bool = paramIntent.getAction().equals("android.provider.Telephony.NEW_OUTGOING_SMS");
      if (!bool) {}
    }
    else
    {
      if (AndroidUtilities.isWaitingForSms()) {
        break label35;
      }
    }
    for (;;)
    {
      return;
      label35:
      paramContext = paramIntent.getExtras();
      if (paramContext != null) {
        try
        {
          paramIntent = (Object[])paramContext.get("pdus");
          SmsMessage[] arrayOfSmsMessage = new SmsMessage[paramIntent.length];
          paramContext = "";
          for (int i = 0; i < arrayOfSmsMessage.length; i++)
          {
            arrayOfSmsMessage[i] = SmsMessage.createFromPdu((byte[])paramIntent[i]);
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            paramContext = paramContext + arrayOfSmsMessage[i].getMessageBody();
          }
          if (!bool) {
            try
            {
              paramIntent = Pattern.compile("[0-9]+").matcher(paramContext);
              if ((paramIntent.find()) && (paramIntent.group(0).length() >= 3))
              {
                paramContext = new org/telegram/messenger/SmsListener$1;
                paramContext.<init>(this, paramIntent);
                AndroidUtilities.runOnUIThread(paramContext);
              }
            }
            catch (Throwable paramContext)
            {
              FileLog.e(paramContext);
            }
          }
        }
        catch (Throwable paramContext)
        {
          FileLog.e(paramContext);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/SmsListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */