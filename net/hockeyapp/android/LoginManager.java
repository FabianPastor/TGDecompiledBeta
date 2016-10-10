package net.hockeyapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class LoginManager
{
  static final String LOGIN_EXIT_KEY = "net.hockeyapp.android.EXIT";
  public static final int LOGIN_MODE_ANONYMOUS = 0;
  public static final int LOGIN_MODE_EMAIL_ONLY = 1;
  public static final int LOGIN_MODE_EMAIL_PASSWORD = 2;
  public static final int LOGIN_MODE_VALIDATE = 3;
  private static String identifier = null;
  static LoginManagerListener listener;
  static Class<?> mainActivity;
  private static int mode;
  private static String secret = null;
  private static String urlString = null;
  private static Handler validateHandler = null;
  
  private static String getURLString(int paramInt)
  {
    String str = "";
    if (paramInt == 2) {
      str = "authorize";
    }
    for (;;)
    {
      return urlString + "api/3/apps/" + identifier + "/identity/" + str;
      if (paramInt == 1) {
        str = "check";
      } else if (paramInt == 3) {
        str = "validate";
      }
    }
  }
  
  public static void register(Context paramContext, String paramString, int paramInt)
  {
    String str = Util.getAppIdentifier(paramContext);
    if (TextUtils.isEmpty(str)) {
      throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
    }
    register(paramContext, str, paramString, paramInt, (Class)null);
  }
  
  public static void register(Context paramContext, String paramString1, String paramString2, int paramInt, Class<?> paramClass)
  {
    register(paramContext, paramString1, paramString2, "https://sdk.hockeyapp.net/", paramInt, paramClass);
  }
  
  public static void register(Context paramContext, String paramString1, String paramString2, int paramInt, LoginManagerListener paramLoginManagerListener)
  {
    listener = paramLoginManagerListener;
    register(paramContext, paramString1, paramString2, paramInt, (Class)null);
  }
  
  public static void register(Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt, Class<?> paramClass)
  {
    if (paramContext != null)
    {
      identifier = Util.sanitizeAppIdentifier(paramString1);
      secret = paramString2;
      urlString = paramString3;
      mode = paramInt;
      mainActivity = paramClass;
      if (validateHandler == null) {
        validateHandler = new LoginHandler(paramContext);
      }
      Constants.loadFromContext(paramContext);
    }
  }
  
  private static void startLoginActivity(Context paramContext)
  {
    Intent localIntent = new Intent();
    boolean bool;
    if (mode == 3)
    {
      bool = true;
      if (!Boolean.valueOf(bool).booleanValue()) {
        break label84;
      }
    }
    label84:
    for (int i = 2;; i = mode)
    {
      localIntent.setFlags(1342177280);
      localIntent.setClass(paramContext, LoginActivity.class);
      localIntent.putExtra("url", getURLString(i));
      localIntent.putExtra("mode", i);
      localIntent.putExtra("secret", secret);
      paramContext.startActivity(localIntent);
      return;
      bool = false;
      break;
    }
  }
  
  public static void verifyLogin(Activity paramActivity, Intent paramIntent)
  {
    if ((paramIntent != null) && (paramIntent.getBooleanExtra("net.hockeyapp.android.EXIT", false))) {
      paramActivity.finish();
    }
    Object localObject;
    label151:
    label196:
    label201:
    label207:
    do
    {
      do
      {
        return;
      } while ((paramActivity == null) || (mode == 0));
      localObject = paramActivity.getSharedPreferences("net.hockeyapp.android.login", 0);
      if (((SharedPreferences)localObject).getInt("mode", -1) != mode)
      {
        HockeyLog.verbose("HockeyAuth", "Mode has changed, require re-auth.");
        ((SharedPreferences)localObject).edit().remove("auid").remove("iuid").putInt("mode", mode).apply();
      }
      paramIntent = ((SharedPreferences)localObject).getString("auid", null);
      localObject = ((SharedPreferences)localObject).getString("iuid", null);
      int i;
      int j;
      if ((paramIntent == null) && (localObject == null))
      {
        i = 1;
        if ((paramIntent != null) || ((mode != 2) && (mode != 3))) {
          break label196;
        }
        j = 1;
        if ((localObject != null) || (mode != 1)) {
          break label201;
        }
      }
      for (int k = 1;; k = 0)
      {
        if ((i == 0) && (j == 0) && (k == 0)) {
          break label207;
        }
        HockeyLog.verbose("HockeyAuth", "Not authenticated or correct ID missing, re-authenticate.");
        startLoginActivity(paramActivity);
        return;
        i = 0;
        break;
        j = 0;
        break label151;
      }
    } while (mode != 3);
    HockeyLog.verbose("HockeyAuth", "LOGIN_MODE_VALIDATE, Validate the user's info!");
    HashMap localHashMap = new HashMap();
    if (paramIntent != null)
    {
      localHashMap.put("type", "auid");
      localHashMap.put("id", paramIntent);
    }
    for (;;)
    {
      paramActivity = new LoginTask(paramActivity, validateHandler, getURLString(3), 3, localHashMap);
      paramActivity.setShowProgressDialog(false);
      AsyncTaskUtils.execute(paramActivity);
      return;
      if (localObject != null)
      {
        localHashMap.put("type", "iuid");
        localHashMap.put("id", localObject);
      }
    }
  }
  
  private static class LoginHandler
    extends Handler
  {
    private final WeakReference<Context> mWeakContext;
    
    public LoginHandler(Context paramContext)
    {
      this.mWeakContext = new WeakReference(paramContext);
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool = paramMessage.getData().getBoolean("success");
      paramMessage = (Context)this.mWeakContext.get();
      if (paramMessage == null) {
        return;
      }
      if (!bool)
      {
        LoginManager.startLoginActivity(paramMessage);
        return;
      }
      HockeyLog.verbose("HockeyAuth", "We authenticated or verified successfully");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/LoginManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */