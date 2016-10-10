package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtil
{
  private SharedPreferences mFeedbackTokenPrefs;
  private SharedPreferences.Editor mFeedbackTokenPrefsEditor;
  private SharedPreferences mNameEmailSubjectPrefs;
  private SharedPreferences.Editor mNameEmailSubjectPrefsEditor;
  
  public static PrefsUtil getInstance()
  {
    return PrefsUtilHolder.INSTANCE;
  }
  
  public String getFeedbackTokenFromPrefs(Context paramContext)
  {
    if (paramContext == null) {}
    do
    {
      return null;
      this.mFeedbackTokenPrefs = paramContext.getSharedPreferences("net.hockeyapp.android.prefs_feedback_token", 0);
    } while (this.mFeedbackTokenPrefs == null);
    return this.mFeedbackTokenPrefs.getString("net.hockeyapp.android.prefs_key_feedback_token", null);
  }
  
  public String getNameEmailFromPrefs(Context paramContext)
  {
    if (paramContext == null) {}
    do
    {
      return null;
      this.mNameEmailSubjectPrefs = paramContext.getSharedPreferences("net.hockeyapp.android.prefs_name_email", 0);
    } while (this.mNameEmailSubjectPrefs == null);
    return this.mNameEmailSubjectPrefs.getString("net.hockeyapp.android.prefs_key_name_email", null);
  }
  
  public void saveFeedbackTokenToPrefs(Context paramContext, String paramString)
  {
    if (paramContext != null)
    {
      this.mFeedbackTokenPrefs = paramContext.getSharedPreferences("net.hockeyapp.android.prefs_feedback_token", 0);
      if (this.mFeedbackTokenPrefs != null)
      {
        this.mFeedbackTokenPrefsEditor = this.mFeedbackTokenPrefs.edit();
        this.mFeedbackTokenPrefsEditor.putString("net.hockeyapp.android.prefs_key_feedback_token", paramString);
        this.mFeedbackTokenPrefsEditor.apply();
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
        this.mNameEmailSubjectPrefsEditor = this.mNameEmailSubjectPrefs.edit();
        if ((paramString1 != null) && (paramString2 != null) && (paramString3 != null)) {
          break label71;
        }
        this.mNameEmailSubjectPrefsEditor.putString("net.hockeyapp.android.prefs_key_name_email", null);
      }
    }
    for (;;)
    {
      this.mNameEmailSubjectPrefsEditor.apply();
      return;
      label71:
      this.mNameEmailSubjectPrefsEditor.putString("net.hockeyapp.android.prefs_key_name_email", String.format("%s|%s|%s", new Object[] { paramString1, paramString2, paramString3 }));
    }
  }
  
  private static class PrefsUtilHolder
  {
    public static final PrefsUtil INSTANCE = new PrefsUtil(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/PrefsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */