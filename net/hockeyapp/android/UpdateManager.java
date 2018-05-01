package net.hockeyapp.android;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask.Status;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.Date;
import net.hockeyapp.android.tasks.CheckUpdateTask;
import net.hockeyapp.android.tasks.CheckUpdateTaskWithUI;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.Util;

public class UpdateManager
{
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
      if ((paramUpdateManagerListener == null) || (new Date().compareTo(paramUpdateManagerListener) <= 0)) {
        break label33;
      }
    }
    label33:
    for (bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean dialogShown(WeakReference<Activity> paramWeakReference)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      bool2 = bool1;
      if (paramWeakReference != null)
      {
        bool2 = bool1;
        if (paramWeakReference.getFragmentManager().findFragmentByTag("hockey_update_dialog") != null) {
          bool2 = true;
        }
      }
    }
    return bool2;
  }
  
  protected static boolean installedFromMarket(WeakReference<? extends Context> paramWeakReference)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    paramWeakReference = (Context)paramWeakReference.get();
    bool3 = bool2;
    if (paramWeakReference != null) {
      bool4 = bool1;
    }
    try
    {
      paramWeakReference = paramWeakReference.getPackageManager().getInstallerPackageName(paramWeakReference.getPackageName());
      bool3 = bool2;
      bool4 = bool1;
      if (!TextUtils.isEmpty(paramWeakReference))
      {
        bool1 = true;
        bool2 = true;
        bool3 = bool2;
        bool4 = bool1;
        if (Build.VERSION.SDK_INT >= 24)
        {
          bool4 = bool1;
          if (!TextUtils.equals(paramWeakReference, "com.google.android.packageinstaller"))
          {
            bool3 = bool2;
            bool4 = bool1;
            if (!TextUtils.equals(paramWeakReference, "com.android.packageinstaller")) {}
          }
          else
          {
            bool3 = false;
          }
        }
        bool4 = bool3;
        bool2 = TextUtils.equals(paramWeakReference, "adb");
        if (bool2) {
          bool3 = false;
        }
      }
    }
    catch (Throwable paramWeakReference)
    {
      for (;;)
      {
        bool3 = bool4;
      }
    }
    return bool3;
  }
  
  public static void register(Activity paramActivity, String paramString)
  {
    register(paramActivity, paramString, true);
  }
  
  public static void register(Activity paramActivity, String paramString1, String paramString2, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    paramString2 = Util.sanitizeAppIdentifier(paramString2);
    Constants.loadFromContext(paramActivity);
    paramActivity = new WeakReference(paramActivity);
    if (dialogShown(paramActivity)) {}
    for (;;)
    {
      return;
      if ((!checkExpiryDate(paramActivity, paramUpdateManagerListener)) && (((paramUpdateManagerListener != null) && (paramUpdateManagerListener.canUpdateInMarket())) || (!installedFromMarket(paramActivity)))) {
        startUpdateTask(paramActivity, paramString1, paramString2, paramUpdateManagerListener, paramBoolean);
      }
    }
  }
  
  public static void register(Activity paramActivity, String paramString, UpdateManagerListener paramUpdateManagerListener, boolean paramBoolean)
  {
    register(paramActivity, "https://sdk.hockeyapp.net/", paramString, paramUpdateManagerListener, paramBoolean);
  }
  
  public static void register(Activity paramActivity, String paramString, boolean paramBoolean)
  {
    register(paramActivity, paramString, null, paramBoolean);
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
    }
    for (;;)
    {
      return;
      updateTask.attach(paramWeakReference);
    }
  }
  
  public static void unregister()
  {
    if (updateTask != null)
    {
      updateTask.cancel(true);
      updateTask.detach();
      updateTask = null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/UpdateManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */