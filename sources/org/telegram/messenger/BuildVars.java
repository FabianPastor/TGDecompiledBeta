package org.telegram.messenger;

import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import com.android.billingclient.api.ProductDetails;
import java.util.Iterator;

public class BuildVars {
    public static String APPCENTER_HASH = "var_-67c9-48d2-b5d0-4761f1c1a8f3";
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";
    public static int APP_ID = 4;
    public static int BUILD_VERSION = 2794;
    public static String BUILD_VERSION_STRING = "9.0.0";
    public static boolean CHECK_UPDATES = true;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean DEBUG_VERSION = true;
    public static String GOOGLE_AUTH_CLIENT_ID = "760348033671-81kmi3pi84p11ub8hp9a1funsv0rn2p9.apps.googleusercontent.com";
    public static String HUAWEI_APP_ID = "NUM";
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
        return DEBUG_VERSION || isStandaloneApp() || isBetaApp() || isHuaweiStoreApp() || hasDirectCurrency();
    }

    private static boolean hasDirectCurrency() {
        ProductDetails productDetails;
        if (BillingController.getInstance().isReady() && (productDetails = BillingController.PREMIUM_PRODUCT_DETAILS) != null) {
            for (ProductDetails.SubscriptionOfferDetails pricingPhases : productDetails.getSubscriptionOfferDetails()) {
                Iterator<ProductDetails.PricingPhase> it = pricingPhases.getPricingPhases().getPricingPhaseList().iterator();
                while (true) {
                    if (it.hasNext()) {
                        ProductDetails.PricingPhase next = it.next();
                        Iterator<String> it2 = MessagesController.getInstance(UserConfig.selectedAccount).directPaymentsCurrency.iterator();
                        while (true) {
                            if (it2.hasNext()) {
                                if (ObjectsCompat$$ExternalSyntheticBackport0.m(next.getPriceCurrencyCode(), it2.next())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
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

    public static boolean isHuaweiStoreApp() {
        return ApplicationLoader.isHuaweiStoreBuild();
    }
}
