package org.telegram.messenger.support.customtabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.BundleCompat;
import android.util.Log;

public class CustomTabsSessionToken
{
  private static final String TAG = "CustomTabsSessionToken";
  private final CustomTabsCallback mCallback;
  private final ICustomTabsCallback mCallbackBinder;
  
  CustomTabsSessionToken(ICustomTabsCallback paramICustomTabsCallback)
  {
    this.mCallbackBinder = paramICustomTabsCallback;
    this.mCallback = new CustomTabsCallback()
    {
      public void extraCallback(String paramAnonymousString, Bundle paramAnonymousBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.extraCallback(paramAnonymousString, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          for (;;)
          {
            Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
          }
        }
      }
      
      public void onMessageChannelReady(Bundle paramAnonymousBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onMessageChannelReady(paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousBundle)
        {
          for (;;)
          {
            Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
          }
        }
      }
      
      public void onNavigationEvent(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(paramAnonymousInt, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousBundle)
        {
          for (;;)
          {
            Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
          }
        }
      }
      
      public void onPostMessage(String paramAnonymousString, Bundle paramAnonymousBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onPostMessage(paramAnonymousString, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousString)
        {
          for (;;)
          {
            Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
          }
        }
      }
    };
  }
  
  public static CustomTabsSessionToken createDummySessionTokenForTesting()
  {
    return new CustomTabsSessionToken(new DummyCallback());
  }
  
  public static CustomTabsSessionToken getSessionTokenFromIntent(Intent paramIntent)
  {
    paramIntent = BundleCompat.getBinder(paramIntent.getExtras(), "android.support.customtabs.extra.SESSION");
    if (paramIntent == null) {}
    for (paramIntent = null;; paramIntent = new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(paramIntent))) {
      return paramIntent;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CustomTabsSessionToken)) {}
    for (boolean bool = false;; bool = ((CustomTabsSessionToken)paramObject).getCallbackBinder().equals(this.mCallbackBinder.asBinder())) {
      return bool;
    }
  }
  
  public CustomTabsCallback getCallback()
  {
    return this.mCallback;
  }
  
  IBinder getCallbackBinder()
  {
    return this.mCallbackBinder.asBinder();
  }
  
  public int hashCode()
  {
    return getCallbackBinder().hashCode();
  }
  
  public boolean isAssociatedWith(CustomTabsSession paramCustomTabsSession)
  {
    return paramCustomTabsSession.getBinder().equals(this.mCallbackBinder);
  }
  
  static class DummyCallback
    extends ICustomTabsCallback.Stub
  {
    public IBinder asBinder()
    {
      return this;
    }
    
    public void extraCallback(String paramString, Bundle paramBundle) {}
    
    public void onMessageChannelReady(Bundle paramBundle) {}
    
    public void onNavigationEvent(int paramInt, Bundle paramBundle) {}
    
    public void onPostMessage(String paramString, Bundle paramBundle) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsSessionToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */