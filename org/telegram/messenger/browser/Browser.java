package org.telegram.messenger.browser;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.CustomTabsCopyReceiver;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.ShareBroadcastReceiver;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsIntent;
import org.telegram.messenger.support.customtabs.CustomTabsIntent.Builder;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;

public class Browser
{
  private static WeakReference<Activity> currentCustomTabsActivity;
  private static CustomTabsClient customTabsClient;
  private static WeakReference<CustomTabsSession> customTabsCurrentSession;
  private static String customTabsPackageToBind;
  private static CustomTabsServiceConnection customTabsServiceConnection;
  private static CustomTabsSession customTabsSession;
  
  public static void bindCustomTabsService(Activity paramActivity)
  {
    Object localObject = null;
    if (currentCustomTabsActivity == null)
    {
      if ((localObject != null) && (localObject != paramActivity)) {
        unbindCustomTabsService((Activity)localObject);
      }
      if (customTabsClient == null) {
        break label41;
      }
    }
    for (;;)
    {
      return;
      localObject = (Activity)currentCustomTabsActivity.get();
      break;
      label41:
      currentCustomTabsActivity = new WeakReference(paramActivity);
      try
      {
        if (TextUtils.isEmpty(customTabsPackageToBind))
        {
          customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(paramActivity);
          if (customTabsPackageToBind == null) {}
        }
        else
        {
          localObject = new org/telegram/messenger/support/customtabsclient/shared/ServiceConnection;
          ServiceConnectionCallback local1 = new org/telegram/messenger/browser/Browser$1;
          local1.<init>();
          ((ServiceConnection)localObject).<init>(local1);
          customTabsServiceConnection = (CustomTabsServiceConnection)localObject;
          if (!CustomTabsClient.bindCustomTabsService(paramActivity, customTabsPackageToBind, customTabsServiceConnection)) {
            customTabsServiceConnection = null;
          }
        }
      }
      catch (Exception paramActivity)
      {
        FileLog.e(paramActivity);
      }
    }
  }
  
  private static CustomTabsSession getCurrentSession()
  {
    if (customTabsCurrentSession == null) {}
    for (CustomTabsSession localCustomTabsSession = null;; localCustomTabsSession = (CustomTabsSession)customTabsCurrentSession.get()) {
      return localCustomTabsSession;
    }
  }
  
  private static CustomTabsSession getSession()
  {
    if (customTabsClient == null) {
      customTabsSession = null;
    }
    for (;;)
    {
      return customTabsSession;
      if (customTabsSession == null)
      {
        customTabsSession = customTabsClient.newSession(new NavigationCallback(null));
        setCurrentSession(customTabsSession);
      }
    }
  }
  
  public static boolean isInternalUri(Uri paramUri, boolean[] paramArrayOfBoolean)
  {
    boolean bool = true;
    String str = paramUri.getHost();
    if (str != null)
    {
      str = str.toLowerCase();
      if (!"tg".equals(paramUri.getScheme())) {
        break label36;
      }
    }
    for (;;)
    {
      return bool;
      str = "";
      break;
      label36:
      if ("telegram.dog".equals(str))
      {
        paramUri = paramUri.getPath();
        if ((paramUri != null) && (paramUri.length() > 1))
        {
          paramUri = paramUri.substring(1).toLowerCase();
          if ((!paramUri.startsWith("blog")) && (!paramUri.equals("iv")) && (!paramUri.startsWith("faq")) && (!paramUri.equals("apps"))) {
            continue;
          }
          if (paramArrayOfBoolean != null) {
            paramArrayOfBoolean[0] = true;
          }
          bool = false;
        }
      }
      else if (("telegram.me".equals(str)) || ("t.me".equals(str)) || ("telesco.pe".equals(str)))
      {
        paramUri = paramUri.getPath();
        if ((paramUri != null) && (paramUri.length() > 1))
        {
          if (!paramUri.substring(1).toLowerCase().equals("iv")) {
            continue;
          }
          if (paramArrayOfBoolean != null) {
            paramArrayOfBoolean[0] = true;
          }
          bool = false;
          continue;
        }
      }
      bool = false;
    }
  }
  
  public static boolean isInternalUrl(String paramString, boolean[] paramArrayOfBoolean)
  {
    return isInternalUri(Uri.parse(paramString), paramArrayOfBoolean);
  }
  
  public static void openUrl(Context paramContext, Uri paramUri)
  {
    openUrl(paramContext, paramUri, true);
  }
  
  public static void openUrl(Context paramContext, Uri paramUri, boolean paramBoolean)
  {
    openUrl(paramContext, paramUri, paramBoolean, true);
  }
  
  public static void openUrl(Context paramContext, Uri paramUri, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramContext == null) || (paramUri == null)) {}
    for (;;)
    {
      return;
      int i = UserConfig.selectedAccount;
      boolean[] arrayOfBoolean = new boolean[1];
      arrayOfBoolean[0] = false;
      boolean bool = isInternalUri(paramUri, arrayOfBoolean);
      Object localObject4;
      Object localObject5;
      Object localObject6;
      if (paramBoolean2) {
        try
        {
          if ((paramUri.getHost().toLowerCase().equals("telegra.ph")) || (paramUri.toString().toLowerCase().contains("telegram.org/faq")))
          {
            AlertDialog[] arrayOfAlertDialog = new AlertDialog[1];
            localObject4 = new org/telegram/ui/ActionBar/AlertDialog;
            ((AlertDialog)localObject4).<init>(paramContext, 1);
            arrayOfAlertDialog[0] = localObject4;
            localObject5 = new org/telegram/tgnet/TLRPC$TL_messages_getWebPagePreview;
            ((TLRPC.TL_messages_getWebPagePreview)localObject5).<init>();
            ((TLRPC.TL_messages_getWebPagePreview)localObject5).message = paramUri.toString();
            localObject6 = ConnectionsManager.getInstance(UserConfig.selectedAccount);
            localObject4 = new org/telegram/messenger/browser/Browser$2;
            ((2)localObject4).<init>(arrayOfAlertDialog, i, paramUri, paramContext, paramBoolean1);
            i = ((ConnectionsManager)localObject6).sendRequest((TLObject)localObject5, (RequestDelegate)localObject4);
            localObject4 = new org/telegram/messenger/browser/Browser$3;
            ((3)localObject4).<init>(arrayOfAlertDialog, i);
            AndroidUtilities.runOnUIThread((Runnable)localObject4, 1000L);
          }
        }
        catch (Exception localException1) {}
      } else {
        try
        {
          Object localObject1;
          if (paramUri.getScheme() != null) {
            localObject1 = paramUri.getScheme().toLowerCase();
          }
          for (;;)
          {
            if ((paramBoolean1) && (SharedConfig.customTabs) && (!bool))
            {
              paramBoolean1 = ((String)localObject1).equals("tel");
              if (!paramBoolean1)
              {
                localObject6 = null;
                localObject5 = null;
                localObject4 = localObject5;
                try
                {
                  localObject1 = new android/content/Intent;
                  localObject4 = localObject5;
                  ((Intent)localObject1).<init>("android.intent.action.VIEW", Uri.parse("http://www.google.com"));
                  localObject4 = localObject5;
                  List localList = paramContext.getPackageManager().queryIntentActivities((Intent)localObject1, 0);
                  localObject1 = localObject6;
                  if (localList != null)
                  {
                    localObject4 = localObject5;
                    localObject1 = localObject6;
                    if (!localList.isEmpty())
                    {
                      localObject4 = localObject5;
                      localObject5 = new String[localList.size()];
                      for (i = 0;; i++)
                      {
                        localObject4 = localObject5;
                        localObject1 = localObject5;
                        if (i >= localList.size()) {
                          break;
                        }
                        localObject4 = localObject5;
                        localObject5[i] = ((ResolveInfo)localList.get(i)).activityInfo.packageName;
                        localObject4 = localObject5;
                        if (BuildVars.LOGS_ENABLED)
                        {
                          localObject4 = localObject5;
                          localObject1 = new java/lang/StringBuilder;
                          localObject4 = localObject5;
                          ((StringBuilder)localObject1).<init>();
                          localObject4 = localObject5;
                          FileLog.d("default browser name = " + localObject5[i]);
                        }
                      }
                      localObject1 = "";
                    }
                  }
                }
                catch (Exception localException2)
                {
                  Object localObject2 = localObject4;
                  localObject5 = null;
                  localObject4 = localObject5;
                  for (;;)
                  {
                    Object localObject3;
                    try
                    {
                      localObject6 = new android/content/Intent;
                      localObject4 = localObject5;
                      ((Intent)localObject6).<init>("android.intent.action.VIEW", paramUri);
                      localObject4 = localObject5;
                      localObject5 = paramContext.getPackageManager().queryIntentActivities((Intent)localObject6, 0);
                      int k;
                      if (localObject2 != null)
                      {
                        i = 0;
                        localObject4 = localObject5;
                        if (i < ((List)localObject5).size()) {
                          for (int j = 0;; j++)
                          {
                            k = i;
                            localObject4 = localObject5;
                            if (j < localObject2.length)
                            {
                              localObject4 = localObject5;
                              if (localObject2[j].equals(((ResolveInfo)((List)localObject5).get(i)).activityInfo.packageName))
                              {
                                localObject4 = localObject5;
                                ((List)localObject5).remove(i);
                                k = i - 1;
                              }
                            }
                            else
                            {
                              i = k + 1;
                              break;
                            }
                          }
                        }
                      }
                      else
                      {
                        for (i = 0;; i = k + 1)
                        {
                          localObject4 = localObject5;
                          if (i >= ((List)localObject5).size()) {
                            break;
                          }
                          localObject4 = localObject5;
                          if (!((ResolveInfo)((List)localObject5).get(i)).activityInfo.packageName.toLowerCase().contains("browser"))
                          {
                            k = i;
                            localObject4 = localObject5;
                            if (!((ResolveInfo)((List)localObject5).get(i)).activityInfo.packageName.toLowerCase().contains("chrome")) {}
                          }
                          else
                          {
                            localObject4 = localObject5;
                            ((List)localObject5).remove(i);
                            k = i - 1;
                          }
                        }
                      }
                      localObject4 = localObject5;
                      localObject2 = localObject5;
                      if (BuildVars.LOGS_ENABLED) {
                        for (i = 0;; i++)
                        {
                          localObject4 = localObject5;
                          localObject2 = localObject5;
                          if (i >= ((List)localObject5).size()) {
                            break;
                          }
                          localObject4 = localObject5;
                          localObject2 = new java/lang/StringBuilder;
                          localObject4 = localObject5;
                          ((StringBuilder)localObject2).<init>();
                          localObject4 = localObject5;
                          FileLog.d("device has " + ((ResolveInfo)((List)localObject5).get(i)).activityInfo.packageName + " to open " + paramUri.toString());
                        }
                      }
                      if (localObject3 == null) {
                        break label840;
                      }
                    }
                    catch (Exception localException3)
                    {
                      localObject3 = localObject4;
                      if (arrayOfBoolean[0] != 0) {
                        break label840;
                      }
                    }
                  }
                  if (((List)localObject3).isEmpty())
                  {
                    label840:
                    localObject3 = new android/content/Intent;
                    ((Intent)localObject3).<init>(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                    ((Intent)localObject3).setAction("android.intent.action.SEND");
                    localObject4 = ApplicationLoader.applicationContext;
                    localObject5 = new android/content/Intent;
                    ((Intent)localObject5).<init>(ApplicationLoader.applicationContext, CustomTabsCopyReceiver.class);
                    localObject5 = PendingIntent.getBroadcast((Context)localObject4, 0, (Intent)localObject5, 134217728);
                    localObject4 = new org/telegram/messenger/support/customtabs/CustomTabsIntent$Builder;
                    ((CustomTabsIntent.Builder)localObject4).<init>(getSession());
                    ((CustomTabsIntent.Builder)localObject4).addMenuItem(LocaleController.getString("CopyLink", NUM), (PendingIntent)localObject5);
                    ((CustomTabsIntent.Builder)localObject4).setToolbarColor(Theme.getColor("actionBarDefault"));
                    ((CustomTabsIntent.Builder)localObject4).setShowTitle(true);
                    ((CustomTabsIntent.Builder)localObject4).setActionButton(BitmapFactory.decodeResource(paramContext.getResources(), NUM), LocaleController.getString("ShareFile", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, (Intent)localObject3, 0), false);
                    localObject3 = ((CustomTabsIntent.Builder)localObject4).build();
                    ((CustomTabsIntent)localObject3).setUseNewTask();
                    ((CustomTabsIntent)localObject3).launchUrl(paramContext, paramUri);
                  }
                }
              }
            }
          }
        }
        catch (Exception localException4)
        {
          FileLog.e(localException4);
          try
          {
            Intent localIntent = new android/content/Intent;
            localIntent.<init>("android.intent.action.VIEW", paramUri);
            if (bool)
            {
              paramUri = new android/content/ComponentName;
              paramUri.<init>(paramContext.getPackageName(), LaunchActivity.class.getName());
              localIntent.setComponent(paramUri);
            }
            localIntent.putExtra("create_new_tab", true);
            localIntent.putExtra("com.android.browser.application_id", paramContext.getPackageName());
            paramContext.startActivity(localIntent);
          }
          catch (Exception paramContext)
          {
            FileLog.e(paramContext);
          }
        }
      }
    }
  }
  
  public static void openUrl(Context paramContext, String paramString)
  {
    if (paramString == null) {}
    for (;;)
    {
      return;
      openUrl(paramContext, Uri.parse(paramString), true);
    }
  }
  
  public static void openUrl(Context paramContext, String paramString, boolean paramBoolean)
  {
    if ((paramContext == null) || (paramString == null)) {}
    for (;;)
    {
      return;
      openUrl(paramContext, Uri.parse(paramString), paramBoolean);
    }
  }
  
  private static void setCurrentSession(CustomTabsSession paramCustomTabsSession)
  {
    customTabsCurrentSession = new WeakReference(paramCustomTabsSession);
  }
  
  public static void unbindCustomTabsService(Activity paramActivity)
  {
    if (customTabsServiceConnection == null) {}
    for (;;)
    {
      return;
      Activity localActivity;
      if (currentCustomTabsActivity == null)
      {
        localActivity = null;
        if (localActivity == paramActivity) {
          currentCustomTabsActivity.clear();
        }
      }
      try
      {
        paramActivity.unbindService(customTabsServiceConnection);
        customTabsClient = null;
        customTabsSession = null;
        continue;
        localActivity = (Activity)currentCustomTabsActivity.get();
      }
      catch (Exception paramActivity)
      {
        for (;;) {}
      }
    }
  }
  
  private static class NavigationCallback
    extends CustomTabsCallback
  {
    public void onNavigationEvent(int paramInt, Bundle paramBundle) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/browser/Browser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */