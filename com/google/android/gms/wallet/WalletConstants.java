package com.google.android.gms.wallet;

import android.accounts.Account;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class WalletConstants {
    public static final String ACTION_ENABLE_WALLET_OPTIMIZATION = "com.google.android.gms.wallet.ENABLE_WALLET_OPTIMIZATION";
    public static final int ENVIRONMENT_PRODUCTION = 1;
    @Deprecated
    public static final int ENVIRONMENT_SANDBOX = 0;
    @Deprecated
    public static final int ENVIRONMENT_STRICT_SANDBOX = 2;
    public static final int ENVIRONMENT_TEST = 3;
    public static final int ERROR_CODE_AUTHENTICATION_FAILURE = 411;
    public static final int ERROR_CODE_BUYER_ACCOUNT_ERROR = 409;
    public static final int ERROR_CODE_DEVELOPER_ERROR = 10;
    public static final int ERROR_CODE_INTERNAL_ERROR = 8;
    public static final int ERROR_CODE_INVALID_PARAMETERS = 404;
    public static final int ERROR_CODE_INVALID_TRANSACTION = 410;
    public static final int ERROR_CODE_MERCHANT_ACCOUNT_ERROR = 405;
    public static final int ERROR_CODE_SERVICE_UNAVAILABLE = 402;
    @Deprecated
    public static final int ERROR_CODE_SPENDING_LIMIT_EXCEEDED = 406;
    public static final int ERROR_CODE_UNKNOWN = 413;
    public static final int ERROR_CODE_UNSUPPORTED_API_VERSION = 412;
    public static final String EXTRA_ERROR_CODE = "com.google.android.gms.wallet.EXTRA_ERROR_CODE";
    public static final String EXTRA_FULL_WALLET = "com.google.android.gms.wallet.EXTRA_FULL_WALLET";
    public static final String EXTRA_IS_NEW_USER = "com.google.android.gms.wallet.EXTRA_IS_NEW_USER";
    public static final String EXTRA_IS_READY_TO_PAY = "com.google.android.gms.wallet.EXTRA_IS_READY_TO_PAY";
    public static final String EXTRA_IS_USER_PREAUTHORIZED = "com.google.android.gm.wallet.EXTRA_IS_USER_PREAUTHORIZED";
    public static final String EXTRA_MASKED_WALLET = "com.google.android.gms.wallet.EXTRA_MASKED_WALLET";
    public static final String EXTRA_MASKED_WALLET_REQUEST = "com.google.android.gms.wallet.EXTRA_MASKED_WALLET_REQUEST";
    public static final String EXTRA_WEB_PAYMENT_DATA = "com.google.android.gms.wallet.EXTRA_WEB_PAYMENT_DATA";
    public static final String METADATA_TAG_WALLET_API_ENABLED = "com.google.android.gms.wallet.api.enabled";
    public static final int RESULT_ERROR = 1;
    public static final int THEME_DARK = 0;
    @Deprecated
    public static final int THEME_HOLO_DARK = 0;
    @Deprecated
    public static final int THEME_HOLO_LIGHT = 1;
    public static final int THEME_LIGHT = 1;
    private static Account zzbPV = new Account("ACCOUNT_NO_WALLET", "com.google");

    @Retention(RetentionPolicy.SOURCE)
    public @interface CardNetwork {
        public static final int AMEX = 1;
        public static final int DISCOVER = 2;
        public static final int INTERAC = 6;
        public static final int JCB = 3;
        public static final int MASTERCARD = 4;
        public static final int OTHER = 1000;
        public static final int VISA = 5;
    }

    private WalletConstants() {
    }
}
