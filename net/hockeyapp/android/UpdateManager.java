package net.hockeyapp.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask.Status;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.Date;
import net.hockeyapp.android.tasks.CheckUpdateTask;
import net.hockeyapp.android.tasks.CheckUpdateTaskWithUI;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.Util;

public class UpdateManager
{
  public static final String INSTALLER_ADB = "adb";
  private static UpdateManagerListener lastListener = null;
  private static CheckUpdateTask updateTask = null;
  
  private static boolean checkExpiryDate(WeakReference<Activity> paramWeakReference, UpdateManagerListener paramUpdateManagerListener)
  {
    boolean bool1 = false;
    boolean bool2 = checkExpiryDateForBackground(paramUpdateManagerListener);
    if (bool2) {
      bool1 = paramUpdateManagerListener.onBuildExpired();
    }
    if ((bool2) && (bool1)) {
      startExpiryInfoIntent(paramWeakReference);
    }
    return bool2;
  }
  
  private static boolean checkExpiryDateForBackground(UpdateManagerListener paramUpdateManagerListener)
  {
    boolean bool = false;
    if (paramUpdateManagerListener != null)
    {
      paramUpdateManagerListener = paramUpdateManagerListener.getExpiryDate();
      if ((paramUpdateManagerListener != null) && (new Date().compareTo(paramUpdateManagerListener) > 0)) {
        bool = true;
      }
    }
    else
    {
      return bool;
    }
    return false;
  }
  
  @TargetApi(11)
  private static boolean dialogShown(WeakReference<Activity> paramWeakReference)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      bool1 = bool2;
      if (paramWeakReference != null)
      {
        bool1 = bool2;
        if (paramWeakReference.getFragmentManager().findFragmentByTag("hockey_update_dialog") != null) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public static UpdateManagerListener getLastListener()
  {
    return lastListener;
  }
  
  private static boolean installedFromMarket(WeakReference<? extends Context> paramWeakReference)
  {
    boolean bool = false;
    paramWeakReference = (Context)paramWeakReference.get();
    if (paramWeakReference != null) {}
    try
    {
      paramWeakReference = paramWeakReference.getPackageManager().getInstallerPackageName(paramWeakReference.getPackageName());
      if (TextUtils.isEmpty(paramWeakReference))
      {
        if (paramWeakReference != null)
        {
          bool = TextUtils.equals(paramWeakReference, "adb");
          if (bool) {}
        }
      }
      else
      {
        bool = true;
        return bool;
      }
      return false;
    }
    catch (Throwable paramWeakReference) {}
    return false;
  }
  
  public static void register(Activity paramActivity)
  {
    String str = Util.getAppIdentifier(paramActivity);
    if (TextUtils.isEmpty(str)) {
      throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
    }
    register(paramActivity, str);
  }
  
  public static void register(Activity paramActivity, String paramString)
  {
    register(paramActivity, paramString, true);
  }
  
  public static void register(Activity paramActivity, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener)
  {
    register(paramActivity, paramString1, paramString2, paramUpdateManagerListener, true);
  }
  
  public static void register(Activity paramActivity, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    paramString2 = Util.sanitizeAppIdentifier(paramString2);
    lastListener = paramUpdateManagerListener;
    paramActivity = new WeakReference(paramActivity);
    if ((Util.fragmentsSupported().booleanValue()) && (dialogShown(paramActivity))) {}
    while ((checkExpiryDate(paramActivity, paramUpdateManagerListener)) || (((paramUpdateManagerListener == null) || (!paramUpdateManagerListener.canUpdateInMarket())) && (installedFromMarket(paramActivity)))) {
      return;
    }
    startUpdateTask(paramActivity, paramString1, paramString2, paramUpdateManagerListener, paramBoolean);
  }
  
  public static void register(Activity paramActivity, String paramString, UpdateManagerListener paramUpdateManagerListener)
  {
    register(paramActivity, "https://sdk.hockeyapp.net/", paramString, paramUpdateManagerListener, true);
  }
  
  public static void register(Activity paramActivity, String paramString, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    register(paramActivity, "https://sdk.hockeyapp.net/", paramString, paramUpdateManagerListener, paramBoolean);
  }
  
  public static void register(Activity paramActivity, String paramString, boolean paramBoolean)
  {
    register(paramActivity, paramString, null, paramBoolean);
  }
  
  public static void registerForBackground(Context paramContext, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener)
  {
    paramString2 = Util.sanitizeAppIdentifier(paramString2);
    lastListener = paramUpdateManagerListener;
    paramContext = new WeakReference(paramContext);
    if ((!checkExpiryDateForBackground(paramUpdateManagerListener)) && (((paramUpdateManagerListener != null) && (paramUpdateManagerListener.canUpdateInMarket())) || (!installedFromMarket(paramContext)))) {
      startUpdateTaskForBackground(paramContext, paramString1, paramString2, paramUpdateManagerListener);
    }
  }
  
  public static void registerForBackground(Context paramContext, String paramString, UpdateManagerListener paramUpdateManagerListener)
  {
    registerForBackground(paramContext, "https://sdk.hockeyapp.net/", paramString, paramUpdateManagerListener);
  }
  
  private static void startExpiryInfoIntent(WeakReference<Activity> paramWeakReference)
  {
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      if (paramWeakReference != null)
      {
        paramWeakReference.finish();
        Intent localIntent = new Intent(paramWeakReference, ExpiryInfoActivity.class);
        localIntent.addFlags(335544320);
        paramWeakReference.startActivity(localIntent);
      }
    }
  }
  
  private static void startUpdateTask(WeakReference<Activity> paramWeakReference, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    if ((updateTask == null) || (updateTask.getStatus() == AsyncTask.Status.FINISHED))
    {
      updateTask = new CheckUpdateTaskWithUI(paramWeakReference, paramString1, paramString2, paramUpdateManagerListener, paramBoolean);
      AsyncTaskUtils.execute(updateTask);
      return;
    }
    updateTask.attach(paramWeakReference);
  }
  
  private static void startUpdateTaskForBackground(WeakReference<Context> paramWeakReference, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener)
  {
    if ((updateTask == null) || (updateTask.getStatus() == AsyncTask.Status.FINISHED))
    {
      updateTask = new CheckUpdateTask(paramWeakReference, paramString1, paramString2, paramUpdateManagerListener);
      AsyncTaskUtils.execute(updateTask);
      return;
    }
    updateTask.attach(paramWeakReference);
  }
  
  public static void unregister()
  {
    if (updateTask != null)
    {
      updateTask.cancel(true);
      updateTask.detach();
      updateTask = null;
    }
    lastListener = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */