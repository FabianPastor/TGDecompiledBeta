package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import org.telegram.messenger.ApplicationLoader;

public class JNIUtilities
{
  @TargetApi(23)
  public static String getCurrentNetworkInterfaceName()
  {
    String str = null;
    Object localObject = (ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity");
    Network localNetwork = ((ConnectivityManager)localObject).getActiveNetwork();
    if (localNetwork == null) {}
    for (;;)
    {
      return str;
      localObject = ((ConnectivityManager)localObject).getLinkProperties(localNetwork);
      if (localObject != null) {
        str = ((LinkProperties)localObject).getInterfaceName();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/JNIUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */