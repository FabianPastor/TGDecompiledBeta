package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

public class CustomTabsClient
{
  private final ICustomTabsService mService;
  private final ComponentName mServiceComponentName;
  
  CustomTabsClient(ICustomTabsService paramICustomTabsService, ComponentName paramComponentName)
  {
    this.mService = paramICustomTabsService;
    this.mServiceComponentName = paramComponentName;
  }
  
  public static boolean bindCustomTabsService(Context paramContext, String paramString, CustomTabsServiceConnection paramCustomTabsServiceConnection)
  {
    Intent localIntent = new Intent("android.support.customtabs.action.CustomTabsService");
    if (!TextUtils.isEmpty(paramString)) {
      localIntent.setPackage(paramString);
    }
    return paramContext.bindService(localIntent, paramCustomTabsServiceConnection, 33);
  }
  
  public Bundle extraCommand(String paramString, Bundle paramBundle)
  {
    try
    {
      paramString = this.mService.extraCommand(paramString, paramBundle);
      return paramString;
    }
    catch (RemoteException paramString) {}
    return null;
  }
  
  public CustomTabsSession newSession(final CustomTabsCallback paramCustomTabsCallback)
  {
    paramCustomTabsCallback = new ICustomTabsCallback.Stub()
    {
      public void extraCallback(String paramAnonymousString, Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback != null) {
          paramCustomTabsCallback.extraCallback(paramAnonymousString, paramAnonymousBundle);
        }
      }
      
      public void onNavigationEvent(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramCustomTabsCallback != null) {
          paramCustomTabsCallback.onNavigationEvent(paramAnonymousInt, paramAnonymousBundle);
        }
      }
    };
    try
    {
      boolean bool = this.mService.newSession(paramCustomTabsCallback);
      if (!bool) {
        return null;
      }
    }
    catch (RemoteException paramCustomTabsCallback)
    {
      return null;
    }
    return new CustomTabsSession(this.mService, paramCustomTabsCallback, this.mServiceComponentName);
  }
  
  public boolean warmup(long paramLong)
  {
    try
    {
      boolean bool = this.mService.warmup(paramLong);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */