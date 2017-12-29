package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtil {
    private SharedPreferences mFeedbackTokenPrefs;
    private Editor mFeedbackTokenPrefsEditor;
    private SharedPreferences mNameEmailSubjectPrefs;
    private Editor mNameEmailSubjectPrefsEditor;

    private static class PrefsUtilHolder {
        public static final PrefsUtil INSTANCE = new PrefsUtil();
    }

    private PrefsUtil() {
    }

    public static PrefsUtil getInstance() {
        return PrefsUtilHolder.INSTANCE;
    }

    public void saveFeedbackTokenToPrefs(Context context, String token) {
        if (context != null) {
            this.mFeedbackTokenPrefs = context.getSharedPreferences("net.hockeyapp.android.prefs_feedback_token", 0);
            if (this.mFeedbackTokenPrefs != null) {
                this.mFeedbackTokenPrefsEditor = this.mFeedbackTokenPrefs.edit();
                this.mFeedbackTokenPrefsEditor.putString("net.hockeyapp.android.prefs_key_feedback_token", token);
                this.mFeedbackTokenPrefsEditor.apply();
            }
        }
    }

    public String getFeedbackTokenFromPrefs(Context context) {
        if (context == null) {
            return null;
        }
        this.mFeedbackTokenPrefs = context.getSharedPreferences("net.hockeyapp.android.prefs_feedback_token", 0);
        if (this.mFeedbackTokenPrefs != null) {
            return this.mFeedbackTokenPrefs.getString("net.hockeyapp.android.prefs_key_feedback_token", null);
        }
        return null;
    }

    public void saveNameEmailSubjectToPrefs(Context context, String name, String email, String subject) {
        if (context != null) {
            this.mNameEmailSubjectPrefs = context.getSharedPreferences("net.hockeyapp.android.prefs_name_email", 0);
            if (this.mNameEmailSubjectPrefs != null) {
                this.mNameEmailSubjectPrefsEditor = this.mNameEmailSubjectPrefs.edit();
                if (name == null || email == null || subject == null) {
                    this.mNameEmailSubjectPrefsEditor.putString("net.hockeyapp.android.prefs_key_name_email", null);
                } else {
                    this.mNameEmailSubjectPrefsEditor.putString("net.hockeyapp.android.prefs_key_name_email", String.format("%s|%s|%s", new Object[]{name, email, subject}));
                }
                this.mNameEmailSubjectPrefsEditor.apply();
            }
        }
    }

    public String getNameEmailFromPrefs(Context context) {
        if (context == null) {
            return null;
        }
        this.mNameEmailSubjectPrefs = context.getSharedPreferences("net.hockeyapp.android.prefs_name_email", 0);
        if (this.mNameEmailSubjectPrefs != null) {
            return this.mNameEmailSubjectPrefs.getString("net.hockeyapp.android.prefs_key_name_email", null);
        }
        return null;
    }
}
