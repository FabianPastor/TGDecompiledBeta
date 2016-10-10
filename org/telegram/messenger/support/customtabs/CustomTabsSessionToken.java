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
      public void onNavigationEvent(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(paramAnonymousInt, paramAnonymousBundle);
          return;
        }
        catch (RemoteException paramAnonymousBundle)
        {
          Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
        }
      }
    };
  }
  
  public static CustomTabsSessionToken getSessionTokenFromIntent(Intent paramIntent)
  {
    paramIntent = BundleCompat.getBinder(paramIntent.getExtras(), "android.support.customtabs.extra.SESSION");
    if (paramIntent == null) {
      return null;
    }
    return new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(paramIntent));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CustomTabsSessionToken)) {
      return false;
    }
    return ((CustomTabsSessionToken)paramObject).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsSessionToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */