package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public abstract class PostMessageServiceConnection
  implements ServiceConnection
{
  private final Object mLock = new Object();
  private IPostMessageService mService;
  private final ICustomTabsCallback mSessionBinder;
  
  public PostMessageServiceConnection(CustomTabsSessionToken paramCustomTabsSessionToken)
  {
    this.mSessionBinder = ICustomTabsCallback.Stub.asInterface(paramCustomTabsSessionToken.getCallbackBinder());
  }
  
  public boolean bindSessionToPostMessageService(Context paramContext, String paramString)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName(paramString, PostMessageService.class.getName());
    return paramContext.bindService(localIntent, this, 1);
  }
  
  public final boolean notifyMessageChannelReady(Bundle paramBundle)
  {
    boolean bool = false;
    if (this.mService == null) {}
    for (;;)
    {
      return bool;
      synchronized (this.mLock)
      {
        try
        {
          this.mService.onMessageChannelReady(this.mSessionBinder, paramBundle);
          bool = true;
        }
        catch (RemoteException paramBundle) {}
      }
    }
  }
  
  public void onPostMessageServiceConnected() {}
  
  public void onPostMessageServiceDisconnected() {}
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.mService = IPostMessageService.Stub.asInterface(paramIBinder);
    onPostMessageServiceConnected();
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    this.mService = null;
    onPostMessageServiceDisconnected();
  }
  
  public final boolean postMessage(String paramString, Bundle paramBundle)
  {
    boolean bool = false;
    if (this.mService == null) {}
    for (;;)
    {
      return bool;
      synchronized (this.mLock)
      {
        try
        {
          this.mService.onPostMessage(this.mSessionBinder, paramString, paramBundle);
          bool = true;
        }
        catch (RemoteException paramString) {}
      }
    }
  }
  
  public void unbindFromContext(Context paramContext)
  {
    paramContext.unbindService(this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/PostMessageServiceConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */