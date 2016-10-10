package org.telegram.messenger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.File;
import java.io.RandomAccessFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader
  extends Application
{
  public static volatile Context applicationContext;
  public static volatile Handler applicationHandler;
  private static volatile boolean applicationInited = false;
  private static Drawable cachedWallpaper;
  private static boolean isCustomTheme;
  public static volatile boolean isScreenOn = false;
  public static volatile boolean mainInterfacePaused = true;
  private static int selectedColor;
  private static int serviceMessageColor;
  private static int serviceSelectedMessageColor;
  private static final Object sync = new Object();
  
  private static void calcBackgroundColor()
  {
    int[] arrayOfInt = AndroidUtilities.calcDrawableColor(cachedWallpaper);
    serviceMessageColor = arrayOfInt[0];
    serviceSelectedMessageColor = arrayOfInt[1];
    applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("serviceMessageColor", serviceMessageColor).putInt("serviceSelectedMessageColor", serviceSelectedMessageColor).commit();
  }
  
  private boolean checkPlayServices()
  {
    try
    {
      int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
      return i == 0;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return true;
  }
  
  private static void convertConfig()
  {
    SharedPreferences localSharedPreferences = applicationContext.getSharedPreferences("dataconfig", 0);
    SerializedData localSerializedData;
    boolean bool;
    if (localSharedPreferences.contains("currentDatacenterId"))
    {
      localSerializedData = new SerializedData(32768);
      localSerializedData.writeInt32(2);
      if (localSharedPreferences.getInt("datacenterSetId", 0) == 0) {
        break label251;
      }
      bool = true;
    }
    for (;;)
    {
      localSerializedData.writeBool(bool);
      localSerializedData.writeBool(true);
      localSerializedData.writeInt32(localSharedPreferences.getInt("currentDatacenterId", 0));
      localSerializedData.writeInt32(localSharedPreferences.getInt("timeDifference", 0));
      localSerializedData.writeInt32(localSharedPreferences.getInt("lastDcUpdateTime", 0));
      localSerializedData.writeInt64(localSharedPreferences.getLong("pushSessionId", 0L));
      localSerializedData.writeBool(false);
      localSerializedData.writeInt32(0);
      try
      {
        localObject1 = localSharedPreferences.getString("datacenters", null);
        if (localObject1 != null)
        {
          localObject1 = Base64.decode((String)localObject1, 0);
          if (localObject1 != null)
          {
            localObject2 = new SerializedData((byte[])localObject1);
            localSerializedData.writeInt32(((SerializedData)localObject2).readInt32(false));
            localSerializedData.writeBytes((byte[])localObject1, 4, localObject1.length - 4);
            ((SerializedData)localObject2).cleanup();
          }
        }
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            Object localObject1 = new RandomAccessFile(new File(getFilesDirFixed(), "tgnet.dat"), "rws");
            Object localObject2 = localSerializedData.toByteArray();
            ((RandomAccessFile)localObject1).writeInt(Integer.reverseBytes(localObject2.length));
            ((RandomAccessFile)localObject1).write((byte[])localObject2);
            ((RandomAccessFile)localObject1).close();
            localSerializedData.cleanup();
            localSharedPreferences.edit().clear().commit();
            return;
            label251:
            bool = false;
            break;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException2);
          }
        }
      }
    }
  }
  
  public static Drawable getCachedWallpaper()
  {
    synchronized (sync)
    {
      Drawable localDrawable = cachedWallpaper;
      return localDrawable;
    }
  }
  
  public static File getFilesDirFixed()
  {
    int i = 0;
    File localFile;
    while (i < 10)
    {
      localFile = applicationContext.getFilesDir();
      if (localFile != null) {
        return localFile;
      }
      i += 1;
    }
    try
    {
      localFile = new File(applicationContext.getApplicationInfo().dataDir, "files");
      localFile.mkdirs();
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return new File("/data/data/org.telegram.messenger/files");
  }
  
  public static int getSelectedColor()
  {
    return selectedColor;
  }
  
  public static int getServiceMessageColor()
  {
    return serviceMessageColor;
  }
  
  public static int getServiceSelectedMessageColor()
  {
    return serviceSelectedMessageColor;
  }
  
  private void initPlayServices()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (ApplicationLoader.this.checkPlayServices())
        {
          if ((UserConfig.pushString != null) && (UserConfig.pushString.length() != 0)) {
            FileLog.d("tmessages", "GCM regId = " + UserConfig.pushString);
          }
          for (;;)
          {
            Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
            ApplicationLoader.this.startService(localIntent);
            return;
            FileLog.d("tmessages", "GCM Registration not found.");
          }
        }
        FileLog.d("tmessages", "No valid Google Play Services APK found.");
      }
    }, 1000L);
  }
  
  public static boolean isCustomTheme()
  {
    return isCustomTheme;
  }
  
  public static void loadWallpaper()
  {
    if (cachedWallpaper != null) {
      return;
    }
    Utilities.searchQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: invokestatic 24	org/telegram/messenger/ApplicationLoader:access$000	()Ljava/lang/Object;
        //   3: astore 4
        //   5: aload 4
        //   7: monitorenter
        //   8: iconst_0
        //   9: istore_2
        //   10: iload_2
        //   11: istore_1
        //   12: getstatic 28	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   15: ldc 30
        //   17: iconst_0
        //   18: invokevirtual 36	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   21: astore 5
        //   23: iload_2
        //   24: istore_1
        //   25: aload 5
        //   27: ldc 38
        //   29: ldc 39
        //   31: invokeinterface 45 3 0
        //   36: istore_3
        //   37: iload_2
        //   38: istore_1
        //   39: aload 5
        //   41: ldc 47
        //   43: iconst_0
        //   44: invokeinterface 45 3 0
        //   49: istore_2
        //   50: iload_2
        //   51: istore_1
        //   52: aload 5
        //   54: ldc 49
        //   56: iconst_0
        //   57: invokeinterface 45 3 0
        //   62: invokestatic 53	org/telegram/messenger/ApplicationLoader:access$102	(I)I
        //   65: pop
        //   66: iload_2
        //   67: istore_1
        //   68: aload 5
        //   70: ldc 55
        //   72: iconst_0
        //   73: invokeinterface 45 3 0
        //   78: invokestatic 58	org/telegram/messenger/ApplicationLoader:access$202	(I)I
        //   81: pop
        //   82: iload_2
        //   83: istore_1
        //   84: iload_2
        //   85: ifne +35 -> 120
        //   88: iload_3
        //   89: ldc 39
        //   91: if_icmpne +80 -> 171
        //   94: iload_2
        //   95: istore_1
        //   96: getstatic 28	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   99: invokevirtual 62	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   102: ldc 63
        //   104: invokevirtual 69	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
        //   107: invokestatic 73	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   110: pop
        //   111: iload_2
        //   112: istore_1
        //   113: iconst_0
        //   114: invokestatic 77	org/telegram/messenger/ApplicationLoader:access$402	(Z)Z
        //   117: pop
        //   118: iload_2
        //   119: istore_1
        //   120: invokestatic 81	org/telegram/messenger/ApplicationLoader:access$300	()Landroid/graphics/drawable/Drawable;
        //   123: ifnonnull +24 -> 147
        //   126: iload_1
        //   127: istore_2
        //   128: iload_1
        //   129: ifne +6 -> 135
        //   132: ldc 82
        //   134: istore_2
        //   135: new 84	android/graphics/drawable/ColorDrawable
        //   138: dup
        //   139: iload_2
        //   140: invokespecial 87	android/graphics/drawable/ColorDrawable:<init>	(I)V
        //   143: invokestatic 73	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   146: pop
        //   147: invokestatic 91	org/telegram/messenger/ApplicationLoader:access$100	()I
        //   150: ifne +6 -> 156
        //   153: invokestatic 94	org/telegram/messenger/ApplicationLoader:access$500	()V
        //   156: new 13	org/telegram/messenger/ApplicationLoader$1$1
        //   159: dup
        //   160: aload_0
        //   161: invokespecial 97	org/telegram/messenger/ApplicationLoader$1$1:<init>	(Lorg/telegram/messenger/ApplicationLoader$1;)V
        //   164: invokestatic 103	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   167: aload 4
        //   169: monitorexit
        //   170: return
        //   171: iload_2
        //   172: istore_1
        //   173: new 105	java/io/File
        //   176: dup
        //   177: invokestatic 109	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
        //   180: ldc 111
        //   182: invokespecial 114	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   185: astore 5
        //   187: iload_2
        //   188: istore_1
        //   189: aload 5
        //   191: invokevirtual 118	java/io/File:exists	()Z
        //   194: ifeq +29 -> 223
        //   197: iload_2
        //   198: istore_1
        //   199: aload 5
        //   201: invokevirtual 122	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   204: invokestatic 128	android/graphics/drawable/Drawable:createFromPath	(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
        //   207: invokestatic 73	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   210: pop
        //   211: iload_2
        //   212: istore_1
        //   213: iconst_1
        //   214: invokestatic 77	org/telegram/messenger/ApplicationLoader:access$402	(Z)Z
        //   217: pop
        //   218: iload_2
        //   219: istore_1
        //   220: goto -100 -> 120
        //   223: iload_2
        //   224: istore_1
        //   225: getstatic 28	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   228: invokevirtual 62	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   231: ldc 63
        //   233: invokevirtual 69	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
        //   236: invokestatic 73	org/telegram/messenger/ApplicationLoader:access$302	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   239: pop
        //   240: iload_2
        //   241: istore_1
        //   242: iconst_0
        //   243: invokestatic 77	org/telegram/messenger/ApplicationLoader:access$402	(Z)Z
        //   246: pop
        //   247: iload_2
        //   248: istore_1
        //   249: goto -129 -> 120
        //   252: astore 5
        //   254: aload 4
        //   256: monitorexit
        //   257: aload 5
        //   259: athrow
        //   260: astore 5
        //   262: goto -142 -> 120
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	265	0	this	1
        //   11	238	1	i	int
        //   9	239	2	j	int
        //   36	56	3	k	int
        //   3	252	4	localObject1	Object
        //   21	179	5	localObject2	Object
        //   252	6	5	localObject3	Object
        //   260	1	5	localThrowable	Throwable
        // Exception table:
        //   from	to	target	type
        //   12	23	252	finally
        //   25	37	252	finally
        //   39	50	252	finally
        //   52	66	252	finally
        //   68	82	252	finally
        //   96	111	252	finally
        //   113	118	252	finally
        //   120	126	252	finally
        //   135	147	252	finally
        //   147	156	252	finally
        //   156	170	252	finally
        //   173	187	252	finally
        //   189	197	252	finally
        //   199	211	252	finally
        //   213	218	252	finally
        //   225	240	252	finally
        //   242	247	252	finally
        //   254	257	252	finally
        //   12	23	260	java/lang/Throwable
        //   25	37	260	java/lang/Throwable
        //   39	50	260	java/lang/Throwable
        //   52	66	260	java/lang/Throwable
        //   68	82	260	java/lang/Throwable
        //   96	111	260	java/lang/Throwable
        //   113	118	260	java/lang/Throwable
        //   173	187	260	java/lang/Throwable
        //   189	197	260	java/lang/Throwable
        //   199	211	260	java/lang/Throwable
        //   213	218	260	java/lang/Throwable
        //   225	240	260	java/lang/Throwable
        //   242	247	260	java/lang/Throwable
      }
    });
  }
  
  public static void postInitApplication()
  {
    if (applicationInited) {
      return;
    }
    applicationInited = true;
    convertConfig();
    try
    {
      LocaleController.getInstance();
    }
    catch (Exception localException3)
    {
      try
      {
        localObject1 = new IntentFilter("android.intent.action.SCREEN_ON");
        ((IntentFilter)localObject1).addAction("android.intent.action.SCREEN_OFF");
        localObject2 = new ScreenReceiver();
        applicationContext.registerReceiver((BroadcastReceiver)localObject2, (IntentFilter)localObject1);
      }
      catch (Exception localException3)
      {
        try
        {
          isScreenOn = ((PowerManager)applicationContext.getSystemService("power")).isScreenOn();
          FileLog.e("tmessages", "screen state = " + isScreenOn);
          UserConfig.loadConfig();
          str2 = getFilesDirFixed().toString();
        }
        catch (Exception localException3)
        {
          try
          {
            for (;;)
            {
              String str2;
              localObject4 = LocaleController.getLocaleStringIso639();
              localObject3 = Build.MANUFACTURER + Build.MODEL;
              Object localObject1 = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
              localObject2 = ((PackageInfo)localObject1).versionName + " (" + ((PackageInfo)localObject1).versionCode + ")";
              localObject1 = "SDK " + Build.VERSION.SDK_INT;
              Object localObject5 = localObject4;
              if (((String)localObject4).trim().length() == 0) {
                localObject5 = "en";
              }
              localObject4 = localObject3;
              if (((String)localObject3).trim().length() == 0) {
                localObject4 = "Android unknown";
              }
              localObject3 = localObject2;
              if (((String)localObject2).trim().length() == 0) {
                localObject3 = "App version unknown";
              }
              localObject2 = localObject1;
              if (((String)localObject1).trim().length() == 0) {
                localObject2 = "SDK Unknown";
              }
              boolean bool = applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushConnection", true);
              MessagesController.getInstance();
              ConnectionsManager.getInstance().init(BuildVars.BUILD_VERSION, 57, BuildVars.APP_ID, (String)localObject4, (String)localObject2, (String)localObject3, (String)localObject5, str2, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), bool);
              if (UserConfig.getCurrentUser() != null)
              {
                MessagesController.getInstance().putUser(UserConfig.getCurrentUser(), true);
                ConnectionsManager.getInstance().applyCountryPortNumber(UserConfig.getCurrentUser().phone);
                MessagesController.getInstance().getBlockedUsers(true);
                SendMessagesHelper.getInstance().checkUnsentMessages();
              }
              ((ApplicationLoader)applicationContext).initPlayServices();
              FileLog.e("tmessages", "app initied");
              ContactsController.getInstance().checkAppAccount();
              MediaController.getInstance();
              return;
              localException1 = localException1;
              localException1.printStackTrace();
              continue;
              localException2 = localException2;
              localException2.printStackTrace();
            }
            localException3 = localException3;
            FileLog.e("tmessages", localException3);
          }
          catch (Exception localException4)
          {
            for (;;)
            {
              Object localObject4 = "en";
              Object localObject3 = "Android unknown";
              Object localObject2 = "App version unknown";
              String str1 = "SDK " + Build.VERSION.SDK_INT;
            }
          }
        }
      }
    }
  }
  
  public static void reloadWallpaper()
  {
    cachedWallpaper = null;
    serviceMessageColor = 0;
    applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("serviceMessageColor").commit();
    loadWallpaper();
  }
  
  public static void startPushService()
  {
    if (applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true))
    {
      applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
      return;
    }
    stopPushService();
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
      paramConfiguration.printStackTrace();
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    applicationContext = getApplicationContext();
    NativeLoader.initNativeLibs(applicationContext);
    if ((Build.VERSION.SDK_INT == 14) || (Build.VERSION.SDK_INT == 15)) {}
    for (boolean bool = true;; bool = false)
    {
      ConnectionsManager.native_setJava(bool);
      new ForegroundDetector(this);
      applicationHandler = new Handler(applicationContext.getMainLooper());
      startPushService();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ApplicationLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */