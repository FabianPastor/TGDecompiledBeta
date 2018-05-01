package org.telegram.messenger.support.customtabsclient.shared;

import android.content.ComponentName;
import java.lang.ref.WeakReference;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;

public class ServiceConnection
  extends CustomTabsServiceConnection
{
  private WeakReference<ServiceConnectionCallback> mConnectionCallback;
  
  public ServiceConnection(ServiceConnectionCallback paramServiceConnectionCallback)
  {
    this.mConnectionCallback = new WeakReference(paramServiceConnectionCallback);
  }
  
  public void onCustomTabsServiceConnected(ComponentName paramComponentName, CustomTabsClient paramCustomTabsClient)
  {
    paramComponentName = (ServiceConnectionCallback)this.mConnectionCallback.get();
    if (paramComponentName != null) {
      paramComponentName.onServiceConnected(paramCustomTabsClient);
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    paramComponentName = (ServiceConnectionCallback)this.mConnectionCallback.get();
    if (paramComponentName != null) {
      paramComponentName.onServiceDisconnected();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabsclient/shared/ServiceConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */