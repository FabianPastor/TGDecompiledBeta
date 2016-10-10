package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import org.telegram.PhoneFormat.PhoneFormat;

public class CallReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    ((TelephonyManager)paramContext.getSystemService("phone")).listen(new PhoneStateListener()
    {
      public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
      {
        super.onCallStateChanged(paramAnonymousInt, paramAnonymousString);
        if ((paramAnonymousInt == 1) && (paramAnonymousString != null) && (paramAnonymousString.length() > 0)) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveCall, new Object[] { PhoneFormat.stripExceptNumbers(paramAnonymousString) });
        }
      }
    }, 32);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/CallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */