package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtil {
    private SharedPreferences mFeedbackTokenPrefs;
    private SharedPreferences mNameEmailSubjectPrefs;

    private static class PrefsUtilHolder {
        static final PrefsUtil INSTANCE = new PrefsUtil();
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
                Editor editor = this.mFeedbackTokenPrefs.edit();
                editor.putString("net.hockeyapp.android.prefs_key_feedback_token", token);
                editor.apply();
            }
        }
    }

    public void saveNameEmailSubjectToPrefs(Context context, String name, String email, String subject) {
        if (context != null) {
            this.mNameEmailSubjectPrefs = context.getSharedPreferences("net.hockeyapp.android.prefs_name_email", 0);
            if (this.mNameEmailSubjectPrefs != null) {
                Editor editor = this.mNameEmailSubjectPrefs.edit();
                if (name == null || email == null || subject == null) {
                    editor.putString("net.hockeyapp.android.prefs_key_name_email", null);
                } else {
                    editor.putString("net.hockeyapp.android.prefs_key_name_email", String.format("%s|%s|%s", new Object[]{name, email, subject}));
                }
                editor.apply();
            }
        }
    }
}
