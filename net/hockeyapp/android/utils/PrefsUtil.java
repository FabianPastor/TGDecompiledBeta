package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtil
{
  private SharedPreferences mFeedbackTokenPrefs;
  private SharedPreferences mNameEmailSubjectPrefs;
  
  public static PrefsUtil getInstance()
  {
    return PrefsUtilHolder.INSTANCE;
  }
  
  public void saveFeedbackTokenToPrefs(Context paramContext, String paramString)
  {
    if (paramContext != null)
    {
      this.mFeedbackTokenPrefs = paramContext.getSharedPreferences("net.hockeyapp.android.prefs_feedback_token", 0);
      if (this.mFeedbackTokenPrefs != null)
      {
        paramContext = this.mFeedbackTokenPrefs.edit();
        paramContext.putString("net.hockeyapp.android.prefs_key_feedback_token", paramString);
        paramContext.apply();
      }
    }
  }
  
  public void saveNameEmailSubjectToPrefs(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    if (paramContext != null)
    {
      this.mNameEmailSubjectPrefs = paramContext.getSharedPreferences("net.hockeyapp.android.prefs_name_email", 0);
      if (this.mNameEmailSubjectPrefs != null)
      {
        paramContext = this.mNameEmailSubjectPrefs.edit();
        if ((paramString1 != null) && (paramString2 != null) && (paramString3 != null)) {
          break label62;
        }
        paramContext.putString("net.hockeyapp.android.prefs_key_name_email", null);
      }
    }
    for (;;)
    {
      paramContext.apply();
      return;
      label62:
      paramContext.putString("net.hockeyapp.android.prefs_key_name_email", String.format("%s|%s|%s", new Object[] { paramString1, paramString2, paramString3 }));
    }
  }
  
  private static class PrefsUtilHolder
  {
    static final PrefsUtil INSTANCE = new PrefsUtil(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/PrefsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */