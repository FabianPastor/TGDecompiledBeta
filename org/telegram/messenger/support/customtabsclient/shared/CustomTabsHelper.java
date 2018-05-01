package org.telegram.messenger.support.customtabsclient.shared;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomTabsHelper
{
  private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
  static final String BETA_PACKAGE = "com.chrome.beta";
  static final String DEV_PACKAGE = "com.chrome.dev";
  private static final String EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE";
  static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
  static final String STABLE_PACKAGE = "com.android.chrome";
  private static final String TAG = "CustomTabsHelper";
  private static String sPackageNameToUse;
  
  public static void addKeepAliveExtra(Context paramContext, Intent paramIntent)
  {
    paramIntent.putExtra("android.support.customtabs.extra.KEEP_ALIVE", new Intent().setClassName(paramContext.getPackageName(), KeepAliveService.class.getCanonicalName()));
  }
  
  public static String getPackageNameToUse(Context paramContext)
  {
    if (sPackageNameToUse != null)
    {
      paramContext = sPackageNameToUse;
      return paramContext;
    }
    PackageManager localPackageManager = paramContext.getPackageManager();
    Intent localIntent1 = new Intent("android.intent.action.VIEW", Uri.parse("http://www.example.com"));
    Object localObject1 = localPackageManager.resolveActivity(localIntent1, 0);
    String str = null;
    if (localObject1 != null) {
      str = ((ResolveInfo)localObject1).activityInfo.packageName;
    }
    Object localObject2 = localPackageManager.queryIntentActivities(localIntent1, 0);
    localObject1 = new ArrayList();
    localObject2 = ((List)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject2).next();
      Intent localIntent2 = new Intent();
      localIntent2.setAction("android.support.customtabs.action.CustomTabsService");
      localIntent2.setPackage(localResolveInfo.activityInfo.packageName);
      if (localPackageManager.resolveService(localIntent2, 0) != null) {
        ((List)localObject1).add(localResolveInfo.activityInfo.packageName);
      }
    }
    if (((List)localObject1).isEmpty()) {
      sPackageNameToUse = null;
    }
    for (;;)
    {
      paramContext = sPackageNameToUse;
      break;
      if (((List)localObject1).size() == 1) {
        sPackageNameToUse = (String)((List)localObject1).get(0);
      } else if ((!TextUtils.isEmpty(str)) && (!hasSpecializedHandlerIntents(paramContext, localIntent1)) && (((List)localObject1).contains(str))) {
        sPackageNameToUse = str;
      } else if (((List)localObject1).contains("com.android.chrome")) {
        sPackageNameToUse = "com.android.chrome";
      } else if (((List)localObject1).contains("com.chrome.beta")) {
        sPackageNameToUse = "com.chrome.beta";
      } else if (((List)localObject1).contains("com.chrome.dev")) {
        sPackageNameToUse = "com.chrome.dev";
      } else if (((List)localObject1).contains("com.google.android.apps.chrome")) {
        sPackageNameToUse = "com.google.android.apps.chrome";
      }
    }
  }
  
  public static String[] getPackages()
  {
    return new String[] { "", "com.android.chrome", "com.chrome.beta", "com.chrome.dev", "com.google.android.apps.chrome" };
  }
  
  private static boolean hasSpecializedHandlerIntents(Context paramContext, Intent paramIntent)
  {
    bool1 = false;
    for (;;)
    {
      try
      {
        paramContext = paramContext.getPackageManager().queryIntentActivities(paramIntent, 64);
        bool2 = bool1;
        if (paramContext != null)
        {
          if (paramContext.size() != 0) {
            continue;
          }
          bool2 = bool1;
        }
      }
      catch (RuntimeException paramContext)
      {
        ResolveInfo localResolveInfo;
        Log.e("CustomTabsHelper", "Runtime exception while getting specialized handlers");
        boolean bool2 = bool1;
        continue;
      }
      return bool2;
      paramContext = paramContext.iterator();
      bool2 = bool1;
      if (paramContext.hasNext())
      {
        localResolveInfo = (ResolveInfo)paramContext.next();
        paramIntent = localResolveInfo.filter;
        if ((paramIntent != null) && (paramIntent.countDataAuthorities() != 0) && (paramIntent.countDataPaths() != 0))
        {
          paramIntent = localResolveInfo.activityInfo;
          if (paramIntent != null) {
            bool2 = true;
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/customtabsclient/shared/CustomTabsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */