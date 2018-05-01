package org.telegram.messenger.support.customtabs;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RemoteViews;
import java.util.List;

public final class CustomTabsSession
{
  private static final String TAG = "CustomTabsSession";
  private final ICustomTabsCallback mCallback;
  private final ComponentName mComponentName;
  private final Object mLock = new Object();
  private final ICustomTabsService mService;
  
  CustomTabsSession(ICustomTabsService paramICustomTabsService, ICustomTabsCallback paramICustomTabsCallback, ComponentName paramComponentName)
  {
    this.mService = paramICustomTabsService;
    this.mCallback = paramICustomTabsCallback;
    this.mComponentName = paramComponentName;
  }
  
  public static CustomTabsSession createDummySessionForTesting(ComponentName paramComponentName)
  {
    return new CustomTabsSession(null, new CustomTabsSessionToken.DummyCallback(), paramComponentName);
  }
  
  IBinder getBinder()
  {
    return this.mCallback.asBinder();
  }
  
  ComponentName getComponentName()
  {
    return this.mComponentName;
  }
  
  public boolean mayLaunchUrl(Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
  {
    try
    {
      bool = this.mService.mayLaunchUrl(this.mCallback, paramUri, paramBundle, paramList);
      return bool;
    }
    catch (RemoteException paramUri)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
  
  public int postMessage(String paramString, Bundle paramBundle)
  {
    synchronized (this.mLock)
    {
      try
      {
        i = this.mService.postMessage(this.mCallback, paramString, paramBundle);
      }
      catch (RemoteException paramString)
      {
        for (;;)
        {
          int i = -2;
        }
      }
      return i;
    }
  }
  
  public boolean requestPostMessageChannel(Uri paramUri)
  {
    try
    {
      bool = this.mService.requestPostMessageChannel(this.mCallback, paramUri);
      return bool;
    }
    catch (RemoteException paramUri)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
  
  public boolean setActionButton(Bitmap paramBitmap, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    paramBitmap = new Bundle();
    paramBitmap.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle);
    try
    {
      bool = this.mService.updateVisuals(this.mCallback, paramBitmap);
      return bool;
    }
    catch (RemoteException paramBitmap)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
  
  public boolean setSecondaryToolbarViews(RemoteViews paramRemoteViews, int[] paramArrayOfInt, PendingIntent paramPendingIntent)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS", paramRemoteViews);
    localBundle.putIntArray("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS", paramArrayOfInt);
    localBundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT", paramPendingIntent);
    try
    {
      bool = this.mService.updateVisuals(this.mCallback, localBundle);
      return bool;
    }
    catch (RemoteException paramRemoteViews)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
  
  @Deprecated
  public boolean setToolbarItem(int paramInt, Bitmap paramBitmap, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("android.support.customtabs.customaction.ID", paramInt);
    localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    paramBitmap = new Bundle();
    paramBitmap.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle);
    try
    {
      bool = this.mService.updateVisuals(this.mCallback, paramBitmap);
      return bool;
    }
    catch (RemoteException paramBitmap)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */