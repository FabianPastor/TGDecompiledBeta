package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  
  public static boolean connectAndInitialize(Context paramContext, String paramString)
  {
    boolean bool1 = false;
    if (paramString == null) {}
    for (;;)
    {
      return bool1;
      Context localContext = paramContext.getApplicationContext();
      paramContext = new CustomTabsServiceConnection()
      {
        public final void onCustomTabsServiceConnected(ComponentName paramAnonymousComponentName, CustomTabsClient paramAnonymousCustomTabsClient)
        {
          paramAnonymousCustomTabsClient.warmup(0L);
          this.val$applicationContext.unbindService(this);
        }
        
        public final void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
      };
      try
      {
        boolean bool2 = bindCustomTabsService(localContext, paramString, paramContext);
        bool1 = bool2;
      }
      catch (SecurityException paramContext) {}
    }
  }
  
  public static String getPackageName(Context paramContext, List<String> paramList)
  {
    return getPackageName(paramContext, paramList, false);
  }
  
  public static String getPackageName(Context paramContext, List<String> paramList, boolean paramBoolean)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    if (paramList == null)
    {
      paramContext = new ArrayList();
      Object localObject1 = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
      Object localObject2 = paramContext;
      if (!paramBoolean)
      {
        localObject1 = localPackageManager.resolveActivity((Intent)localObject1, 0);
        localObject2 = paramContext;
        if (localObject1 != null)
        {
          localObject1 = ((ResolveInfo)localObject1).activityInfo.packageName;
          localObject2 = new ArrayList(paramContext.size() + 1);
          ((List)localObject2).add(localObject1);
          if (paramList != null) {
            ((List)localObject2).addAll(paramList);
          }
        }
      }
      paramList = new Intent("android.support.customtabs.action.CustomTabsService");
      localObject2 = ((List)localObject2).iterator();
      do
      {
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        paramContext = (String)((Iterator)localObject2).next();
        paramList.setPackage(paramContext);
      } while (localPackageManager.resolveService(paramList, 0) == null);
    }
    for (;;)
    {
      return paramContext;
      paramContext = paramList;
      break;
      paramContext = null;
    }
  }
  
  public Bundle extraCommand(String paramString, Bundle paramBundle)
  {
    try
    {
      paramString = this.mService.extraCommand(paramString, paramBundle);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        paramString = null;
      }
    }
  }
  
  public CustomTabsSession newSession(final CustomTabsCallback paramCustomTabsCallback)
  {
    localObject = null;
    paramCustomTabsCallback = new ICustomTabsCallback.Stub()
    {
      private Handler mHandler = new Handler(Looper.getMainLooper());
      
      public void extraCallback(final String paramAnonymousString, final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {}
        for (;;)
        {
          return;
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              CustomTabsClient.2.this.val$callback.extraCallback(paramAnonymousString, paramAnonymousBundle);
            }
          });
        }
      }
      
      public void onMessageChannelReady(final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {}
        for (;;)
        {
          return;
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              CustomTabsClient.2.this.val$callback.onMessageChannelReady(paramAnonymousBundle);
            }
          });
        }
      }
      
      public void onNavigationEvent(final int paramAnonymousInt, final Bundle paramAnonymousBundle)
      {
        if (paramCustomTabsCallback == null) {}
        for (;;)
        {
          return;
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              CustomTabsClient.2.this.val$callback.onNavigationEvent(paramAnonymousInt, paramAnonymousBundle);
            }
          });
        }
      }
      
      public void onPostMessage(final String paramAnonymousString, final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {}
        for (;;)
        {
          return;
          this.mHandler.post(new Runnable()
          {
            public void run()
            {
              CustomTabsClient.2.this.val$callback.onPostMessage(paramAnonymousString, paramAnonymousBundle);
            }
          });
        }
      }
    };
    try
    {
      boolean bool = this.mService.newSession(paramCustomTabsCallback);
      if (bool) {
        break label37;
      }
      paramCustomTabsCallback = (CustomTabsCallback)localObject;
    }
    catch (RemoteException paramCustomTabsCallback)
    {
      for (;;)
      {
        paramCustomTabsCallback = (CustomTabsCallback)localObject;
        continue;
        paramCustomTabsCallback = new CustomTabsSession(this.mService, paramCustomTabsCallback, this.mServiceComponentName);
      }
    }
    return paramCustomTabsCallback;
  }
  
  public boolean warmup(long paramLong)
  {
    try
    {
      bool = this.mService.warmup(paramLong);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabs/CustomTabsClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */