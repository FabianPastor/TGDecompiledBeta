package org.telegram.messenger.support.customtabsclient.shared;

import org.telegram.messenger.support.customtabs.CustomTabsClient;

public abstract interface ServiceConnectionCallback
{
  public abstract void onServiceConnected(CustomTabsClient paramCustomTabsClient);
  
  public abstract void onServiceDisconnected();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabsclient/shared/ServiceConnectionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */