package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.File;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader
  extends Application
{
  @SuppressLint({"StaticFieldLeak"})
  public static volatile Context applicationContext;
  public static volatile Handler applicationHandler;
  private static volatile boolean applicationInited = false;
  public static volatile boolean isScreenOn = false;
  public static volatile boolean mainInterfacePaused = true;
  public static volatile boolean mainInterfacePausedStageQueue = true;
  public static volatile long mainInterfacePausedStageQueueTime;
  
  private boolean checkPlayServices()
  {
    for (boolean bool = true;; bool = false)
    {
      try
      {
        int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (i != 0) {
          continue;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
      return bool;
    }
  }
  
  public static File getFilesDirFixed()
  {
    int i = 0;
    File localFile1;
    if (i < 10)
    {
      localFile1 = applicationContext.getFilesDir();
      if (localFile1 == null) {}
    }
    for (;;)
    {
      return localFile1;
      i++;
      break;
      try
      {
        ApplicationInfo localApplicationInfo = applicationContext.getApplicationInfo();
        localFile1 = new java/io/File;
        localFile1.<init>(localApplicationInfo.dataDir, "files");
        localFile1.mkdirs();
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        File localFile2 = new File("/data/data/org.telegram.messenger/files");
      }
    }
  }
  
  private void initPlayServices()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (ApplicationLoader.this.checkPlayServices())
        {
          String str = SharedConfig.pushString;
          if (!TextUtils.isEmpty(str)) {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("GCM regId = " + str);
            }
          }
        }
        label90:
        for (;;)
        {
          Utilities.globalQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              try
              {
                String str = FirebaseInstanceId.getInstance().getToken();
                if (!TextUtils.isEmpty(str)) {
                  GcmInstanceIDListenerService.sendRegistrationToServer(str);
                }
                return;
              }
              catch (Throwable localThrowable)
              {
                for (;;)
                {
                  FileLog.e(localThrowable);
                }
              }
            }
          });
          for (;;)
          {
            return;
            if (!BuildVars.LOGS_ENABLED) {
              break label90;
            }
            FileLog.d("GCM Registration not found.");
            break;
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("No valid Google Play Services APK found.");
            }
          }
        }
      }
    }, 1000L);
  }
  
  public static void postInitApplication()
  {
    if (applicationInited) {}
    for (;;)
    {
      return;
      applicationInited = true;
      try
      {
        LocaleController.getInstance();
      }
      catch (Exception localException2)
      {
        try
        {
          IntentFilter localIntentFilter = new android/content/IntentFilter;
          localIntentFilter.<init>("android.intent.action.SCREEN_ON");
          localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
          localObject = new org/telegram/messenger/ScreenReceiver;
          ((ScreenReceiver)localObject).<init>();
          applicationContext.registerReceiver((BroadcastReceiver)localObject, localIntentFilter);
        }
        catch (Exception localException2)
        {
          try
          {
            for (;;)
            {
              Object localObject;
              isScreenOn = ((PowerManager)applicationContext.getSystemService("power")).isScreenOn();
              if (BuildVars.LOGS_ENABLED)
              {
                localObject = new java/lang/StringBuilder;
                ((StringBuilder)localObject).<init>();
                FileLog.d("screen state = " + isScreenOn);
              }
              SharedConfig.loadConfig();
              for (i = 0; i < 3; i++)
              {
                UserConfig.getInstance(i).loadConfig();
                MessagesController.getInstance(i);
                ConnectionsManager.getInstance(i);
                localObject = UserConfig.getInstance(i).getCurrentUser();
                if (localObject != null)
                {
                  MessagesController.getInstance(i).putUser((TLRPC.User)localObject, true);
                  MessagesController.getInstance(i).getBlockedUsers(true);
                  SendMessagesHelper.getInstance(i).checkUnsentMessages();
                }
              }
              localException1 = localException1;
              localException1.printStackTrace();
              continue;
              localException2 = localException2;
              localException2.printStackTrace();
            }
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              FileLog.e(localException3);
            }
            ((ApplicationLoader)applicationContext).initPlayServices();
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("app initied");
            }
            MediaController.getInstance();
            for (int i = 0; i < 3; i++)
            {
              ContactsController.getInstance(i).checkAppAccount();
              DownloadController.getInstance(i);
            }
            WearDataLayerListenerService.updateWatchConnectionState();
          }
        }
      }
    }
  }
  
  public static void startPushService()
  {
    if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {}
    for (;;)
    {
      try
      {
        Context localContext = applicationContext;
        Intent localIntent = new android/content/Intent;
        localIntent.<init>(applicationContext, NotificationsService.class);
        localContext.startService(localIntent);
        return;
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
        continue;
      }
      stopPushService();
    }
  }
  
  public static void stopPushService()
  {
    applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
    PendingIntent localPendingIntent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
    ((AlarmManager)applicationContext.getSystemService("alarm")).cancel(localPendingIntent);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    try
    {
      LocaleController.getInstance().onDeviceConfigurationChange(paramConfiguration);
      AndroidUtilities.checkDisplaySize(applicationContext, paramConfiguration);
      return;
    }
    catch (Exception paramConfiguration)
    {
      for (;;)
      {
        paramConfiguration.printStackTrace();
      }
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    applicationContext = getApplicationContext();
    NativeLoader.initNativeLibs(applicationContext);
    ConnectionsManager.native_setJava(false);
    new ForegroundDetector(this);
    applicationHandler = new Handler(applicationContext.getMainLooper());
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run() {}
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ApplicationLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */