package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import org.telegram.PhoneFormat.PhoneFormat;

public class CallReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.intent.action.PHONE_STATE"))
    {
      paramContext = paramIntent.getStringExtra("state");
      if (TelephonyManager.EXTRA_STATE_RINGING.equals(paramContext))
      {
        paramContext = paramIntent.getStringExtra("incoming_number");
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveCall, new Object[] { PhoneFormat.stripExceptNumbers(paramContext) });
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/CallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */