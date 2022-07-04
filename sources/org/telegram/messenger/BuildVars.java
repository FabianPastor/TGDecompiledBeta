package org.telegram.messenger;

import android.content.SharedPreferences;
import android.os.Build;

public class BuildVars {
    public static String APPCENTER_HASH = "var_-67c9-48d2-b5d0-4761f1c1a8f3";
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";
    public static int APP_ID = 4;
    public static int BUILD_VERSION = 2701;
    public static String BUILD_VERSION_STRING = "8.8.2";
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static boolean IS_BILLING_UNAVAILABLE = false;
    public static boolean LOGS_ENABLED = true;
    public static boolean NO_SCOPED_STORAGE = (Build.VERSION.SDK_INT <= 29);
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
    public static String SMS_HASH = (isStandaloneApp() ? "w0lkcmTZkKh" : DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static boolean USE_CLOUD_STRINGS = true;
    private static Boolean betaApp;
    private static Boolean standaloneApp;

    static {
        boolean z = true;
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
            boolean z2 = DEBUG_VERSION;
            if (!z2 && !sharedPreferences.getBoolean("logsEnabled", z2)) {
                z = false;
            }
            LOGS_ENABLED = z;
        }
    }

    public static boolean useInvoiceBilling() {
        return DEBUG_VERSION || isStandaloneApp() || isBetaApp();
    }

    public static boolean isStandaloneApp() {
        if (standaloneApp == null) {
            standaloneApp = Boolean.valueOf(ApplicationLoader.applicationContext != null && "org.telegram.messenger.web".equals(ApplicationLoader.applicationContext.getPackageName()));
        }
        return standaloneApp.booleanValue();
    }

    public static boolean isBetaApp() {
        if (betaApp == null) {
            betaApp = Boolean.valueOf(ApplicationLoader.applicationContext != null && "org.telegram.messenger.beta".equals(ApplicationLoader.applicationContext.getPackageName()));
        }
        return betaApp.booleanValue();
    }
}
