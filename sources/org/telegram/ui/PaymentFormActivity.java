package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.fragment.WalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.TokenParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPaymentCredentials;
import org.telegram.tgnet.TLRPC.PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC.TL_account_confirmPasswordEmail;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_resendPasswordEmail;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_dataJSON;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputCheckPasswordEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentials;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentialsAndroidPay;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentialsSaved;
import org.telegram.tgnet.TLRPC.TL_invoice;
import org.telegram.tgnet.TLRPC.TL_labeledPrice;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC.TL_paymentRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_paymentSavedCredentialsCard;
import org.telegram.tgnet.TLRPC.TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC.TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC.TL_payments_paymentResult;
import org.telegram.tgnet.TLRPC.TL_payments_paymentVerificationNeeded;
import org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_payments_validatedRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_postAddress;
import org.telegram.tgnet.TLRPC.TL_shippingOption;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PaymentInfoCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextPriceCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;

public class PaymentFormActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int FIELDS_COUNT_ADDRESS = 10;
    private static final int FIELDS_COUNT_CARD = 6;
    private static final int FIELDS_COUNT_PASSWORD = 3;
    private static final int FIELDS_COUNT_SAVEDCARD = 2;
    private static final int FIELD_CARD = 0;
    private static final int FIELD_CARDNAME = 2;
    private static final int FIELD_CARD_COUNTRY = 4;
    private static final int FIELD_CARD_POSTCODE = 5;
    private static final int FIELD_CITY = 2;
    private static final int FIELD_COUNTRY = 4;
    private static final int FIELD_CVV = 3;
    private static final int FIELD_EMAIL = 7;
    private static final int FIELD_ENTERPASSWORD = 0;
    private static final int FIELD_ENTERPASSWORDEMAIL = 2;
    private static final int FIELD_EXPIRE_DATE = 1;
    private static final int FIELD_NAME = 6;
    private static final int FIELD_PHONE = 9;
    private static final int FIELD_PHONECODE = 8;
    private static final int FIELD_POSTCODE = 5;
    private static final int FIELD_REENTERPASSWORD = 1;
    private static final int FIELD_SAVEDCARD = 0;
    private static final int FIELD_SAVEDPASSWORD = 1;
    private static final int FIELD_STATE = 3;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int done_button = 1;
    private static final int fragment_container_id = 4000;
    private int androidPayBackgroundColor;
    private boolean androidPayBlackTheme;
    private FrameLayout androidPayContainer;
    private TL_inputPaymentCredentialsAndroidPay androidPayCredentials;
    private String androidPayPublicKey;
    private User botUser;
    private TextInfoPrivacyCell[] bottomCell;
    private FrameLayout bottomLayout;
    private boolean canceled;
    private String cardName;
    private TextCheckCell checkCell1;
    private EditTextSettingsCell codeFieldCell;
    private HashMap<String, String> codesMap;
    private ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private String countryName;
    private String currentBotName;
    private String currentItemName;
    private TL_account_password currentPassword;
    private int currentStep;
    private PaymentFormActivityDelegate delegate;
    private TextDetailSettingsCell[] detailSettingsCell;
    private ArrayList<View> dividers;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private int emailCodeLength;
    private GoogleApiClient googleApiClient;
    private HeaderCell[] headerCell;
    private boolean ignoreOnCardChange;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private EditTextBoldCursor[] inputFields;
    private boolean isWebView;
    private LinearLayout linearLayout2;
    private boolean loadingPasswordInfo;
    private MessageObject messageObject;
    private boolean need_card_country;
    private boolean need_card_name;
    private boolean need_card_postcode;
    private PaymentFormActivity passwordFragment;
    private boolean passwordOk;
    private TextView payTextView;
    private TL_payments_paymentForm paymentForm;
    private PaymentInfoCell paymentInfoCell;
    private String paymentJson;
    private HashMap<String, String> phoneFormatMap;
    private ContextProgressView progressView;
    private ContextProgressView progressViewButton;
    private RadioCell[] radioCells;
    private TL_payments_validatedRequestedInfo requestedInfo;
    private boolean saveCardInfo;
    private boolean saveShippingInfo;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell[] settingsCell;
    private TL_shippingOption shippingOption;
    private Runnable shortPollRunnable;
    private boolean shouldNavigateBack;
    private String stripeApiKey;
    private TextView textView;
    private String totalPriceDecimal;
    private TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    private String webViewUrl;
    private boolean webviewLoading;

    public class LinkSpan extends ClickableSpan {
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }

        public void onClick(View view) {
            PaymentFormActivity.this.presentFragment(new TwoStepVerificationActivity(0));
        }
    }

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TL_account_password tL_account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        /* synthetic */ TelegramWebviewProxy(PaymentFormActivity paymentFormActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$TelegramWebviewProxy$GYkvPF7FkeOF4Jpek1fag25gp2E(this, str, str2));
        }

        public /* synthetic */ void lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(String str, String str2) {
            if (PaymentFormActivity.this.getParentActivity() != null && str.equals("payment_form_submit")) {
                try {
                    JSONObject jSONObject = new JSONObject(str2);
                    PaymentFormActivity.this.paymentJson = jSONObject.getJSONObject("credentials").toString();
                    PaymentFormActivity.this.cardName = jSONObject.getString("title");
                } catch (Throwable th) {
                    PaymentFormActivity.this.paymentJson = str2;
                    FileLog.e(th);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    private void initAndroidPay(Context context) {
    }

    static /* synthetic */ void lambda$null$17(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$34(TLObject tLObject, TL_error tL_error) {
    }

    public PaymentFormActivity(MessageObject messageObject, TL_payments_paymentReceipt tL_payments_paymentReceipt) {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        this.currentStep = 5;
        this.paymentForm = new TL_payments_paymentForm();
        TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
        tL_payments_paymentForm.bot_id = tL_payments_paymentReceipt.bot_id;
        tL_payments_paymentForm.invoice = tL_payments_paymentReceipt.invoice;
        tL_payments_paymentForm.provider_id = tL_payments_paymentReceipt.provider_id;
        tL_payments_paymentForm.users = tL_payments_paymentReceipt.users;
        this.shippingOption = tL_payments_paymentReceipt.shipping;
        this.messageObject = messageObject;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_payments_paymentReceipt.bot_id));
        User user = this.botUser;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject.messageOwner.media.title;
        if (tL_payments_paymentReceipt.info != null) {
            this.validateRequest = new TL_payments_validateRequestedInfo();
            this.validateRequest.info = tL_payments_paymentReceipt.info;
        }
        this.cardName = tL_payments_paymentReceipt.credentials_title;
    }

    public PaymentFormActivity(TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject) {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        TL_invoice tL_invoice = tL_payments_paymentForm.invoice;
        int i = 0;
        if (!(tL_invoice.shipping_address_requested || tL_invoice.email_requested || tL_invoice.name_requested || tL_invoice.phone_requested)) {
            if (tL_payments_paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i = 2;
            }
        }
        init(tL_payments_paymentForm, messageObject, i, null, null, null, null, null, false, null);
    }

    private PaymentFormActivity(TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject, int i, TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo, TL_shippingOption tL_shippingOption, String str, String str2, TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        init(tL_payments_paymentForm, messageObject, i, tL_payments_validatedRequestedInfo, tL_shippingOption, str, str2, tL_payments_validateRequestedInfo, z, tL_inputPaymentCredentialsAndroidPay);
    }

    private void setCurrentPassword(TL_account_password tL_account_password) {
        if (!tL_account_password.has_password) {
            this.currentPassword = tL_account_password;
            tL_account_password = this.currentPassword;
            if (tL_account_password != null) {
                this.waitingForEmail = TextUtils.isEmpty(tL_account_password.email_unconfirmed_pattern) ^ 1;
            }
            updatePasswordFields();
        } else if (getParentActivity() != null) {
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    private void init(TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject, int i, TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo, TL_shippingOption tL_shippingOption, String str, String str2, TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
        this.currentStep = i;
        this.paymentJson = str;
        this.androidPayCredentials = tL_inputPaymentCredentialsAndroidPay;
        this.requestedInfo = tL_payments_validatedRequestedInfo;
        this.paymentForm = tL_payments_paymentForm;
        this.shippingOption = tL_shippingOption;
        this.messageObject = messageObject;
        this.saveCardInfo = z;
        boolean equals = "stripe".equals(this.paymentForm.native_provider);
        boolean z2 = true;
        this.isWebView = equals ^ 1;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_payments_paymentForm.bot_id));
        User user = this.botUser;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject.messageOwner.media.title;
        this.validateRequest = tL_payments_validateRequestedInfo;
        this.saveShippingInfo = true;
        if (z) {
            this.saveCardInfo = z;
        } else {
            if (this.paymentForm.saved_credentials == null) {
                z2 = false;
            }
            this.saveCardInfo = z2;
        }
        if (str2 == null) {
            TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard = tL_payments_paymentForm.saved_credentials;
            if (tL_paymentSavedCredentialsCard != null) {
                this.cardName = tL_paymentSavedCredentialsCard.title;
                return;
            }
            return;
        }
        this.cardName = str2;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (VERSION.SDK_INT >= 23) {
            try {
                if ((this.currentStep == 2 || this.currentStep == 6) && !this.paymentForm.invoice.test) {
                    getParentActivity().getWindow().setFlags(8192, 8192);
                } else if (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(8192);
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        GoogleApiClient googleApiClient = this.googleApiClient;
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    public void onPause() {
        GoogleApiClient googleApiClient = this.googleApiClient;
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:258:0x0919  */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x130c  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x13e5  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x13bd  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x1413  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x1411  */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x1475  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x143e  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:315:0x0ad0 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:319:0x0adb */
    /* JADX WARNING: Missing exception handler attribute for start block: B:307:0x0aba */
    /* JADX WARNING: Missing exception handler attribute for start block: B:285:0x0977 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:311:0x0ac5 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Can't wrap try/catch for region: R(11:267|268|269|270|(1:272)|277|278|283|284|285|286) */
    /* JADX WARNING: Can't wrap try/catch for region: R(18:303|304|305|306|307|308|309|310|311|312|313|314|315|316|317|318|319|320) */
    /* JADX WARNING: Missing block: B:79:0x0320, code skipped:
            if (r7.email_requested == false) goto L_0x0311;
     */
    /* JADX WARNING: Missing block: B:321:0x0ae0, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:322:0x0ae1, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:448:0x0var_, code skipped:
            if (r6.email_requested == false) goto L_0x0var_;
     */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public android.view.View createView(android.content.Context r29) {
        /*
        r28 = this;
        r1 = r28;
        r2 = r29;
        r0 = r1.currentStep;
        r3 = 6;
        r4 = 5;
        r5 = 4;
        r6 = 3;
        r7 = 2;
        r8 = 1;
        if (r0 != 0) goto L_0x001e;
    L_0x000e:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e0839 float:1.8879307E38 double:1.0531631966E-314;
        r10 = "PaymentShippingInfo";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x001e:
        if (r0 != r8) goto L_0x0030;
    L_0x0020:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e083a float:1.887931E38 double:1.053163197E-314;
        r10 = "PaymentShippingMethod";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x0030:
        if (r0 != r7) goto L_0x0042;
    L_0x0032:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e0811 float:1.8879226E38 double:1.053163177E-314;
        r10 = "PaymentCardInfo";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x0042:
        if (r0 != r6) goto L_0x0054;
    L_0x0044:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e0811 float:1.8879226E38 double:1.053163177E-314;
        r10 = "PaymentCardInfo";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x0054:
        if (r0 != r5) goto L_0x008d;
    L_0x0056:
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r0 = r0.test;
        if (r0 == 0) goto L_0x007e;
    L_0x005e:
        r0 = r1.actionBar;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Test ";
        r9.append(r10);
        r10 = NUM; // 0x7f0e0818 float:1.887924E38 double:1.0531631803E-314;
        r11 = "PaymentCheckout";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r9.append(r10);
        r9 = r9.toString();
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x007e:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e0818 float:1.887924E38 double:1.0531631803E-314;
        r10 = "PaymentCheckout";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x008d:
        if (r0 != r4) goto L_0x00c6;
    L_0x008f:
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r0 = r0.test;
        if (r0 == 0) goto L_0x00b7;
    L_0x0097:
        r0 = r1.actionBar;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Test ";
        r9.append(r10);
        r10 = NUM; // 0x7f0e0832 float:1.8879293E38 double:1.053163193E-314;
        r11 = "PaymentReceipt";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r9.append(r10);
        r9 = r9.toString();
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x00b7:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e0832 float:1.8879293E38 double:1.053163193E-314;
        r10 = "PaymentReceipt";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x00d6;
    L_0x00c6:
        if (r0 != r3) goto L_0x00d6;
    L_0x00c8:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7f0e0827 float:1.887927E38 double:1.0531631877E-314;
        r10 = "PaymentPassword";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
    L_0x00d6:
        r0 = r1.actionBar;
        r9 = NUM; // 0x7var_e9 float:1.794505E38 double:1.052935618E-314;
        r0.setBackButtonImage(r9);
        r0 = r1.actionBar;
        r0.setAllowOverlayTitle(r8);
        r0 = r1.actionBar;
        r9 = new org.telegram.ui.PaymentFormActivity$1;
        r9.<init>();
        r0.setActionBarMenuOnItemClick(r9);
        r0 = r1.actionBar;
        r0 = r0.createMenu();
        r9 = r1.currentStep;
        r10 = -1;
        if (r9 == 0) goto L_0x0102;
    L_0x00f8:
        if (r9 == r8) goto L_0x0102;
    L_0x00fa:
        if (r9 == r7) goto L_0x0102;
    L_0x00fc:
        if (r9 == r6) goto L_0x0102;
    L_0x00fe:
        if (r9 == r5) goto L_0x0102;
    L_0x0100:
        if (r9 != r3) goto L_0x013d;
    L_0x0102:
        r9 = NUM; // 0x7var_ float:1.7945111E38 double:1.052935633E-314;
        r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r0 = r0.addItemWithWidth(r8, r9, r11);
        r1.doneItem = r0;
        r0 = new org.telegram.ui.Components.ContextProgressView;
        r0.<init>(r2, r8);
        r1.progressView = r0;
        r0 = r1.progressView;
        r9 = 0;
        r0.setAlpha(r9);
        r0 = r1.progressView;
        r9 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r0.setScaleX(r9);
        r0 = r1.progressView;
        r0.setScaleY(r9);
        r0 = r1.progressView;
        r0.setVisibility(r5);
        r0 = r1.doneItem;
        r9 = r1.progressView;
        r11 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11);
        r0.addView(r9, r11);
    L_0x013d:
        r0 = new android.widget.FrameLayout;
        r0.<init>(r2);
        r1.fragmentView = r0;
        r0 = r1.fragmentView;
        r9 = r0;
        r9 = (android.widget.FrameLayout) r9;
        r11 = "windowBackgroundGray";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r0.setBackgroundColor(r11);
        r0 = new android.widget.ScrollView;
        r0.<init>(r2);
        r1.scrollView = r0;
        r0 = r1.scrollView;
        r0.setFillViewport(r8);
        r0 = r1.scrollView;
        r11 = "actionBarDefault";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor(r0, r11);
        r0 = r1.scrollView;
        r11 = -1;
        r12 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r13 = 51;
        r14 = 0;
        r15 = 0;
        r16 = 0;
        r4 = r1.currentStep;
        if (r4 != r5) goto L_0x017d;
    L_0x0178:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r17 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        goto L_0x0180;
    L_0x017d:
        r4 = 0;
        r17 = 0;
    L_0x0180:
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17);
        r9.addView(r0, r4);
        r0 = new android.widget.LinearLayout;
        r0.<init>(r2);
        r1.linearLayout2 = r0;
        r0 = r1.linearLayout2;
        r0.setOrientation(r8);
        r0 = r1.scrollView;
        r4 = r1.linearLayout2;
        r11 = new android.widget.FrameLayout$LayoutParams;
        r12 = -2;
        r11.<init>(r10, r12);
        r0.addView(r4, r11);
        r0 = r1.currentStep;
        r13 = "windowBackgroundWhiteBlackText";
        r14 = "windowBackgroundGrayShadow";
        r15 = "windowBackgroundWhite";
        r5 = 0;
        if (r0 != 0) goto L_0x093d;
    L_0x01ab:
        r9 = new java.util.HashMap;
        r9.<init>();
        r11 = new java.util.HashMap;
        r11.<init>();
        r0 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0212 }
        r4 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x0212 }
        r18 = r29.getResources();	 Catch:{ Exception -> 0x0212 }
        r3 = r18.getAssets();	 Catch:{ Exception -> 0x0212 }
        r10 = "countries.txt";
        r3 = r3.open(r10);	 Catch:{ Exception -> 0x0212 }
        r4.<init>(r3);	 Catch:{ Exception -> 0x0212 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x0212 }
    L_0x01cd:
        r3 = r0.readLine();	 Catch:{ Exception -> 0x0212 }
        if (r3 == 0) goto L_0x020e;
    L_0x01d3:
        r4 = ";";
        r3 = r3.split(r4);	 Catch:{ Exception -> 0x0212 }
        r4 = r1.countriesArray;	 Catch:{ Exception -> 0x0212 }
        r10 = r3[r7];	 Catch:{ Exception -> 0x0212 }
        r4.add(r5, r10);	 Catch:{ Exception -> 0x0212 }
        r4 = r1.countriesMap;	 Catch:{ Exception -> 0x0212 }
        r10 = r3[r7];	 Catch:{ Exception -> 0x0212 }
        r12 = r3[r5];	 Catch:{ Exception -> 0x0212 }
        r4.put(r10, r12);	 Catch:{ Exception -> 0x0212 }
        r4 = r1.codesMap;	 Catch:{ Exception -> 0x0212 }
        r10 = r3[r5];	 Catch:{ Exception -> 0x0212 }
        r12 = r3[r7];	 Catch:{ Exception -> 0x0212 }
        r4.put(r10, r12);	 Catch:{ Exception -> 0x0212 }
        r4 = r3[r8];	 Catch:{ Exception -> 0x0212 }
        r10 = r3[r7];	 Catch:{ Exception -> 0x0212 }
        r11.put(r4, r10);	 Catch:{ Exception -> 0x0212 }
        r4 = r3.length;	 Catch:{ Exception -> 0x0212 }
        if (r4 <= r6) goto L_0x0205;
    L_0x01fc:
        r4 = r1.phoneFormatMap;	 Catch:{ Exception -> 0x0212 }
        r10 = r3[r5];	 Catch:{ Exception -> 0x0212 }
        r12 = r3[r6];	 Catch:{ Exception -> 0x0212 }
        r4.put(r10, r12);	 Catch:{ Exception -> 0x0212 }
    L_0x0205:
        r4 = r3[r8];	 Catch:{ Exception -> 0x0212 }
        r3 = r3[r7];	 Catch:{ Exception -> 0x0212 }
        r9.put(r4, r3);	 Catch:{ Exception -> 0x0212 }
        r12 = -2;
        goto L_0x01cd;
    L_0x020e:
        r0.close();	 Catch:{ Exception -> 0x0212 }
        goto L_0x0216;
    L_0x0212:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0216:
        r0 = r1.countriesArray;
        r3 = org.telegram.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE;
        java.util.Collections.sort(r0, r3);
        r0 = 10;
        r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0];
        r1.inputFields = r0;
        r0 = 0;
    L_0x0224:
        r3 = 10;
        if (r0 >= r3) goto L_0x0771;
    L_0x0228:
        if (r0 != 0) goto L_0x0261;
    L_0x022a:
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e0833 float:1.8879295E38 double:1.0531631937E-314;
        r10 = "PaymentShippingAddress";
        r4 = org.telegram.messenger.LocaleController.getString(r10, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r5];
        r10 = -2;
        r12 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r10);
        r3.addView(r4, r7);
        r3 = 8;
        r10 = -1;
        goto L_0x02b3;
    L_0x0261:
        r3 = 6;
        r10 = -2;
        r12 = -1;
        if (r0 != r3) goto L_0x02b0;
    L_0x0266:
        r3 = r1.sectionCell;
        r4 = new org.telegram.ui.Cells.ShadowSectionCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.linearLayout2;
        r4 = r1.sectionCell;
        r4 = r4[r5];
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r10);
        r3.addView(r4, r7);
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r8] = r4;
        r3 = r1.headerCell;
        r3 = r3[r8];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r8];
        r4 = NUM; // 0x7f0e083d float:1.8879315E38 double:1.0531631986E-314;
        r7 = "PaymentShippingReceiver";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r8];
        r7 = -2;
        r10 = -1;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7);
        r3.addView(r4, r12);
        goto L_0x02b1;
    L_0x02b0:
        r10 = -1;
    L_0x02b1:
        r3 = 8;
    L_0x02b3:
        if (r0 != r3) goto L_0x02d1;
    L_0x02b5:
        r3 = new android.widget.LinearLayout;
        r3.<init>(r2);
        r3.setOrientation(r5);
        r4 = r1.linearLayout2;
        r7 = 50;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7);
        r4.addView(r3, r7);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        goto L_0x0341;
    L_0x02d1:
        r3 = 9;
        if (r0 != r3) goto L_0x02e2;
    L_0x02d5:
        r3 = r1.inputFields;
        r4 = 8;
        r3 = r3[r4];
        r3 = r3.getParent();
        r3 = (android.view.ViewGroup) r3;
        goto L_0x0341;
    L_0x02e2:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r2);
        r4 = r1.linearLayout2;
        r7 = 50;
        r10 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7);
        r4.addView(r3, r7);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r4 = 5;
        if (r0 == r4) goto L_0x0303;
    L_0x02fd:
        r4 = 9;
        if (r0 == r4) goto L_0x0303;
    L_0x0301:
        r4 = 1;
        goto L_0x0304;
    L_0x0303:
        r4 = 0;
    L_0x0304:
        if (r4 == 0) goto L_0x0323;
    L_0x0306:
        r7 = 7;
        if (r0 != r7) goto L_0x0313;
    L_0x0309:
        r7 = r1.paymentForm;
        r7 = r7.invoice;
        r7 = r7.phone_requested;
        if (r7 != 0) goto L_0x0313;
    L_0x0311:
        r4 = 0;
        goto L_0x0323;
    L_0x0313:
        r7 = 6;
        if (r0 != r7) goto L_0x0323;
    L_0x0316:
        r7 = r1.paymentForm;
        r7 = r7.invoice;
        r10 = r7.phone_requested;
        if (r10 != 0) goto L_0x0323;
    L_0x031e:
        r7 = r7.email_requested;
        if (r7 != 0) goto L_0x0323;
    L_0x0322:
        goto L_0x0311;
    L_0x0323:
        if (r4 == 0) goto L_0x0341;
    L_0x0325:
        r4 = new org.telegram.ui.PaymentFormActivity$2;
        r4.<init>(r2);
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r4.setBackgroundColor(r7);
        r7 = r1.dividers;
        r7.add(r4);
        r7 = new android.widget.FrameLayout$LayoutParams;
        r10 = 83;
        r12 = -1;
        r7.<init>(r12, r8, r10);
        r3.addView(r4, r7);
    L_0x0341:
        r4 = 9;
        if (r0 != r4) goto L_0x034f;
    L_0x0345:
        r4 = r1.inputFields;
        r7 = new org.telegram.ui.Components.HintEditText;
        r7.<init>(r2);
        r4[r0] = r7;
        goto L_0x0358;
    L_0x034f:
        r4 = r1.inputFields;
        r7 = new org.telegram.ui.Components.EditTextBoldCursor;
        r7.<init>(r2);
        r4[r0] = r7;
    L_0x0358:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = java.lang.Integer.valueOf(r0);
        r4.setTag(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r8, r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = "windowBackgroundWhiteHintText";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r4.setHintTextColor(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setTextColor(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = 0;
        r4.setBackgroundDrawable(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setCursorColor(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4.setCursorSize(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r7);
        r4 = 4;
        if (r0 != r4) goto L_0x03c3;
    L_0x03b0:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$uDw-faglZkNLuTGLVBNZMQdnOLU;
        r7.<init>(r1);
        r4.setOnTouchListener(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.setInputType(r5);
    L_0x03c3:
        r4 = 9;
        if (r0 == r4) goto L_0x03e1;
    L_0x03c7:
        r4 = 8;
        if (r0 != r4) goto L_0x03cc;
    L_0x03cb:
        goto L_0x03e1;
    L_0x03cc:
        r4 = 7;
        if (r0 != r4) goto L_0x03d7;
    L_0x03cf:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.setInputType(r8);
        goto L_0x03e8;
    L_0x03d7:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r4.setInputType(r7);
        goto L_0x03e8;
    L_0x03e1:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.setInputType(r6);
    L_0x03e8:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r7);
        switch(r0) {
            case 0: goto L_0x0508;
            case 1: goto L_0x04e4;
            case 2: goto L_0x04c0;
            case 3: goto L_0x049c;
            case 4: goto L_0x0462;
            case 5: goto L_0x043d;
            case 6: goto L_0x041a;
            case 7: goto L_0x03f7;
            default: goto L_0x03f5;
        };
    L_0x03f5:
        goto L_0x052b;
    L_0x03f7:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0838 float:1.8879305E38 double:1.053163196E-314;
        r10 = "PaymentShippingEmailPlaceholder";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x040d:
        r4 = r4.email;
        if (r4 == 0) goto L_0x052b;
    L_0x0411:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r7.setText(r4);
        goto L_0x052b;
    L_0x041a:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e083b float:1.8879311E38 double:1.0531631976E-314;
        r10 = "PaymentShippingName";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x0430:
        r4 = r4.name;
        if (r4 == 0) goto L_0x052b;
    L_0x0434:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r7.setText(r4);
        goto L_0x052b;
    L_0x043d:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0841 float:1.8879323E38 double:1.0531632006E-314;
        r10 = "PaymentShippingZipPlaceholder";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x0453:
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x052b;
    L_0x0457:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r4 = r4.post_code;
        r7.setText(r4);
        goto L_0x052b;
    L_0x0462:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0837 float:1.8879303E38 double:1.0531631957E-314;
        r10 = "PaymentShippingCountry";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x0478:
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x052b;
    L_0x047c:
        r4 = r4.country_iso2;
        r4 = r11.get(r4);
        r4 = (java.lang.String) r4;
        r7 = r1.paymentForm;
        r7 = r7.saved_info;
        r7 = r7.shipping_address;
        r7 = r7.country_iso2;
        r1.countryName = r7;
        r7 = r1.inputFields;
        r7 = r7[r0];
        if (r4 == 0) goto L_0x0495;
    L_0x0494:
        goto L_0x0497;
    L_0x0495:
        r4 = r1.countryName;
    L_0x0497:
        r7.setText(r4);
        goto L_0x052b;
    L_0x049c:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0840 float:1.8879321E38 double:1.0531632E-314;
        r10 = "PaymentShippingStatePlaceholder";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x04b2:
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x052b;
    L_0x04b6:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r4 = r4.state;
        r7.setText(r4);
        goto L_0x052b;
    L_0x04c0:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0836 float:1.88793E38 double:1.053163195E-314;
        r10 = "PaymentShippingCityPlaceholder";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x04d6:
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x052b;
    L_0x04da:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r4 = r4.city;
        r7.setText(r4);
        goto L_0x052b;
    L_0x04e4:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0835 float:1.8879299E38 double:1.0531631947E-314;
        r10 = "PaymentShippingAddress2Placeholder";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x04fa:
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x052b;
    L_0x04fe:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r4 = r4.street_line2;
        r7.setText(r4);
        goto L_0x052b;
    L_0x0508:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x7f0e0834 float:1.8879297E38 double:1.053163194E-314;
        r10 = "PaymentShippingAddress1Placeholder";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r4.setHint(r7);
        r4 = r1.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x052b;
    L_0x051e:
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x052b;
    L_0x0522:
        r7 = r1.inputFields;
        r7 = r7[r0];
        r4 = r4.street_line1;
        r7.setText(r4);
    L_0x052b:
        r4 = r1.inputFields;
        r7 = r4[r0];
        r4 = r4[r0];
        r4 = r4.length();
        r7.setSelection(r4);
        r4 = 8;
        if (r0 != r4) goto L_0x05b4;
    L_0x053c:
        r4 = new android.widget.TextView;
        r4.<init>(r2);
        r1.textView = r4;
        r4 = r1.textView;
        r7 = "+";
        r4.setText(r7);
        r4 = r1.textView;
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setTextColor(r7);
        r4 = r1.textView;
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r8, r7);
        r4 = r1.textView;
        r20 = -2;
        r21 = -2;
        r22 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r23 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r24 = 0;
        r25 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25);
        r3.addView(r4, r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4.setPadding(r7, r5, r5, r5);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = 19;
        r4.setGravity(r7);
        r4 = new android.text.InputFilter[r8];
        r7 = new android.text.InputFilter$LengthFilter;
        r10 = 5;
        r7.<init>(r10);
        r4[r5] = r7;
        r7 = r1.inputFields;
        r7 = r7[r0];
        r7.setFilters(r4);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r20 = 55;
        r22 = 0;
        r24 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25);
        r3.addView(r4, r7);
        r3 = r1.inputFields;
        r3 = r3[r0];
        r4 = new org.telegram.ui.PaymentFormActivity$3;
        r4.<init>();
        r3.addTextChangedListener(r4);
        goto L_0x0620;
    L_0x05b4:
        r4 = 9;
        if (r0 != r4) goto L_0x05ec;
    L_0x05b8:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.setPadding(r5, r5, r5, r5);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = 19;
        r4.setGravity(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r20 = -1;
        r21 = -2;
        r22 = 0;
        r23 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r24 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r25 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25);
        r3.addView(r4, r7);
        r3 = r1.inputFields;
        r3 = r3[r0];
        r4 = new org.telegram.ui.PaymentFormActivity$4;
        r4.<init>();
        r3.addTextChangedListener(r4);
        goto L_0x0620;
    L_0x05ec:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4.setPadding(r5, r5, r5, r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = org.telegram.messenger.LocaleController.isRTL;
        if (r7 == 0) goto L_0x0603;
    L_0x0601:
        r7 = 5;
        goto L_0x0604;
    L_0x0603:
        r7 = 3;
    L_0x0604:
        r4.setGravity(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r20 = -1;
        r21 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r22 = 51;
        r23 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r24 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r25 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r26 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26);
        r3.addView(r4, r7);
    L_0x0620:
        r3 = r1.inputFields;
        r3 = r3[r0];
        r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$waFpzGxIzLdyjPlQN2X3YYqAdDY;
        r4.<init>(r1);
        r3.setOnEditorActionListener(r4);
        r3 = 9;
        if (r0 != r3) goto L_0x076b;
    L_0x0630:
        r3 = r1.paymentForm;
        r3 = r3.invoice;
        r4 = r3.email_to_provider;
        if (r4 != 0) goto L_0x0657;
    L_0x0638:
        r3 = r3.phone_to_provider;
        if (r3 == 0) goto L_0x063d;
    L_0x063c:
        goto L_0x0657;
    L_0x063d:
        r3 = r1.sectionCell;
        r4 = new org.telegram.ui.Cells.ShadowSectionCell;
        r4.<init>(r2);
        r3[r8] = r4;
        r3 = r1.linearLayout2;
        r4 = r1.sectionCell;
        r4 = r4[r8];
        r7 = -2;
        r10 = -1;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7);
        r3.addView(r4, r12);
        goto L_0x06fe;
    L_0x0657:
        r3 = 0;
        r4 = 0;
    L_0x0659:
        r7 = r1.paymentForm;
        r7 = r7.users;
        r7 = r7.size();
        if (r3 >= r7) goto L_0x0679;
    L_0x0663:
        r7 = r1.paymentForm;
        r7 = r7.users;
        r7 = r7.get(r3);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
        r10 = r7.id;
        r12 = r1.paymentForm;
        r12 = r12.provider_id;
        if (r10 != r12) goto L_0x0676;
    L_0x0675:
        r4 = r7;
    L_0x0676:
        r3 = r3 + 1;
        goto L_0x0659;
    L_0x0679:
        if (r4 == 0) goto L_0x0684;
    L_0x067b:
        r3 = r4.first_name;
        r4 = r4.last_name;
        r3 = org.telegram.messenger.ContactsController.formatName(r3, r4);
        goto L_0x0686;
    L_0x0684:
        r3 = "";
    L_0x0686:
        r4 = r1.bottomCell;
        r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r7.<init>(r2);
        r4[r8] = r7;
        r4 = r1.bottomCell;
        r4 = r4[r8];
        r7 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r7, r14);
        r4.setBackgroundDrawable(r10);
        r4 = r1.linearLayout2;
        r7 = r1.bottomCell;
        r7 = r7[r8];
        r10 = -2;
        r12 = -1;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r10);
        r4.addView(r7, r6);
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r6 = r4.email_to_provider;
        if (r6 == 0) goto L_0x06cd;
    L_0x06b4:
        r4 = r4.phone_to_provider;
        if (r4 == 0) goto L_0x06cd;
    L_0x06b8:
        r4 = r1.bottomCell;
        r4 = r4[r8];
        r6 = NUM; // 0x7f0e082f float:1.8879287E38 double:1.0531631917E-314;
        r7 = new java.lang.Object[r8];
        r7[r5] = r3;
        r3 = "PaymentPhoneEmailToProvider";
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r7);
        r4.setText(r3);
        goto L_0x06fe;
    L_0x06cd:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 == 0) goto L_0x06ea;
    L_0x06d5:
        r4 = r1.bottomCell;
        r4 = r4[r8];
        r6 = NUM; // 0x7f0e0823 float:1.8879262E38 double:1.053163186E-314;
        r7 = new java.lang.Object[r8];
        r7[r5] = r3;
        r3 = "PaymentEmailToProvider";
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r7);
        r4.setText(r3);
        goto L_0x06fe;
    L_0x06ea:
        r4 = r1.bottomCell;
        r4 = r4[r8];
        r6 = NUM; // 0x7f0e0830 float:1.8879289E38 double:1.053163192E-314;
        r7 = new java.lang.Object[r8];
        r7[r5] = r3;
        r3 = "PaymentPhoneToProvider";
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r7);
        r4.setText(r3);
    L_0x06fe:
        r3 = new org.telegram.ui.Cells.TextCheckCell;
        r3.<init>(r2);
        r1.checkCell1 = r3;
        r3 = r1.checkCell1;
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r3.setBackgroundDrawable(r4);
        r3 = r1.checkCell1;
        r4 = NUM; // 0x7f0e083e float:1.8879317E38 double:1.053163199E-314;
        r6 = "PaymentShippingSave";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r6 = r1.saveShippingInfo;
        r3.setTextAndCheck(r4, r6, r5);
        r3 = r1.linearLayout2;
        r4 = r1.checkCell1;
        r6 = -2;
        r7 = -1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r10);
        r3 = r1.checkCell1;
        r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$ja6Gof-oEgJFLBPAYcrRraILPks;
        r4.<init>(r1);
        r3.setOnClickListener(r4);
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r4, r14);
        r3.setBackgroundDrawable(r6);
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e083f float:1.887932E38 double:1.0531631996E-314;
        r6 = "PaymentShippingSaveInfo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.bottomCell;
        r4 = r4[r5];
        r6 = -2;
        r7 = -1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r10);
    L_0x076b:
        r0 = r0 + 1;
        r6 = 3;
        r7 = 2;
        goto L_0x0224;
    L_0x0771:
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r0 = r0.name_requested;
        if (r0 != 0) goto L_0x078a;
    L_0x0779:
        r0 = r1.inputFields;
        r2 = 6;
        r0 = r0[r2];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r2 = 8;
        r0.setVisibility(r2);
        goto L_0x078c;
    L_0x078a:
        r2 = 8;
    L_0x078c:
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r0 = r0.phone_requested;
        if (r0 != 0) goto L_0x07a1;
    L_0x0794:
        r0 = r1.inputFields;
        r0 = r0[r2];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
    L_0x07a1:
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r0 = r0.email_requested;
        if (r0 != 0) goto L_0x07b9;
    L_0x07a9:
        r0 = r1.inputFields;
        r2 = 7;
        r0 = r0[r2];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r2 = 8;
        r0.setVisibility(r2);
    L_0x07b9:
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r2 = r0.phone_requested;
        if (r2 == 0) goto L_0x07ce;
    L_0x07c1:
        r0 = r1.inputFields;
        r2 = 9;
        r0 = r0[r2];
        r2 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r0.setImeOptions(r2);
        goto L_0x07f9;
    L_0x07ce:
        r2 = r0.email_requested;
        if (r2 == 0) goto L_0x07de;
    L_0x07d2:
        r0 = r1.inputFields;
        r2 = 7;
        r0 = r0[r2];
        r2 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r0.setImeOptions(r2);
        goto L_0x07f9;
    L_0x07de:
        r0 = r0.name_requested;
        if (r0 == 0) goto L_0x07ee;
    L_0x07e2:
        r0 = r1.inputFields;
        r2 = 6;
        r0 = r0[r2];
        r2 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r0.setImeOptions(r2);
        goto L_0x07f9;
    L_0x07ee:
        r0 = r1.inputFields;
        r2 = 5;
        r0 = r0[r2];
        r2 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r0.setImeOptions(r2);
    L_0x07f9:
        r0 = r1.sectionCell;
        r2 = r0[r8];
        if (r2 == 0) goto L_0x081a;
    L_0x07ff:
        r0 = r0[r8];
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r3 = r2.name_requested;
        if (r3 != 0) goto L_0x0815;
    L_0x0809:
        r3 = r2.phone_requested;
        if (r3 != 0) goto L_0x0815;
    L_0x080d:
        r2 = r2.email_requested;
        if (r2 == 0) goto L_0x0812;
    L_0x0811:
        goto L_0x0815;
    L_0x0812:
        r2 = 8;
        goto L_0x0816;
    L_0x0815:
        r2 = 0;
    L_0x0816:
        r0.setVisibility(r2);
        goto L_0x083a;
    L_0x081a:
        r0 = r1.bottomCell;
        r2 = r0[r8];
        if (r2 == 0) goto L_0x083a;
    L_0x0820:
        r0 = r0[r8];
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r3 = r2.name_requested;
        if (r3 != 0) goto L_0x0836;
    L_0x082a:
        r3 = r2.phone_requested;
        if (r3 != 0) goto L_0x0836;
    L_0x082e:
        r2 = r2.email_requested;
        if (r2 == 0) goto L_0x0833;
    L_0x0832:
        goto L_0x0836;
    L_0x0833:
        r2 = 8;
        goto L_0x0837;
    L_0x0836:
        r2 = 0;
    L_0x0837:
        r0.setVisibility(r2);
    L_0x083a:
        r0 = r1.headerCell;
        r0 = r0[r8];
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r3 = r2.name_requested;
        if (r3 != 0) goto L_0x0852;
    L_0x0846:
        r3 = r2.phone_requested;
        if (r3 != 0) goto L_0x0852;
    L_0x084a:
        r2 = r2.email_requested;
        if (r2 == 0) goto L_0x084f;
    L_0x084e:
        goto L_0x0852;
    L_0x084f:
        r2 = 8;
        goto L_0x0853;
    L_0x0852:
        r2 = 0;
    L_0x0853:
        r0.setVisibility(r2);
        r0 = r1.paymentForm;
        r0 = r0.invoice;
        r0 = r0.shipping_address_requested;
        if (r0 != 0) goto L_0x08c0;
    L_0x085e:
        r0 = r1.headerCell;
        r0 = r0[r5];
        r2 = 8;
        r0.setVisibility(r2);
        r0 = r1.sectionCell;
        r0 = r0[r5];
        r0.setVisibility(r2);
        r0 = r1.inputFields;
        r0 = r0[r5];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
        r0 = r1.inputFields;
        r0 = r0[r8];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
        r0 = r1.inputFields;
        r3 = 2;
        r0 = r0[r3];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
        r0 = r1.inputFields;
        r3 = 3;
        r0 = r0[r3];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
        r0 = r1.inputFields;
        r3 = 4;
        r0 = r0[r3];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
        r0 = r1.inputFields;
        r3 = 5;
        r0 = r0[r3];
        r0 = r0.getParent();
        r0 = (android.view.ViewGroup) r0;
        r0.setVisibility(r2);
    L_0x08c0:
        r0 = r1.paymentForm;
        r0 = r0.saved_info;
        if (r0 == 0) goto L_0x08d8;
    L_0x08c6:
        r0 = r0.phone;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x08d8;
    L_0x08ce:
        r0 = r1.paymentForm;
        r0 = r0.saved_info;
        r0 = r0.phone;
        r1.fillNumber(r0);
        goto L_0x08dc;
    L_0x08d8:
        r2 = 0;
        r1.fillNumber(r2);
    L_0x08dc:
        r0 = r1.inputFields;
        r2 = 8;
        r0 = r0[r2];
        r0 = r0.length();
        if (r0 != 0) goto L_0x18b9;
    L_0x08e8:
        r0 = r1.paymentForm;
        r2 = r0.invoice;
        r2 = r2.phone_requested;
        if (r2 == 0) goto L_0x18b9;
    L_0x08f0:
        r0 = r0.saved_info;
        if (r0 == 0) goto L_0x08fc;
    L_0x08f4:
        r0 = r0.phone;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x18b9;
    L_0x08fc:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0912 }
        r2 = "phone";
        r0 = r0.getSystemService(r2);	 Catch:{ Exception -> 0x0912 }
        r0 = (android.telephony.TelephonyManager) r0;	 Catch:{ Exception -> 0x0912 }
        if (r0 == 0) goto L_0x0916;
    L_0x0908:
        r0 = r0.getSimCountryIso();	 Catch:{ Exception -> 0x0912 }
        r0 = r0.toUpperCase();	 Catch:{ Exception -> 0x0912 }
        r11 = r0;
        goto L_0x0917;
    L_0x0912:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0916:
        r11 = 0;
    L_0x0917:
        if (r11 == 0) goto L_0x18b9;
    L_0x0919:
        r0 = r9.get(r11);
        r0 = (java.lang.String) r0;
        if (r0 == 0) goto L_0x18b9;
    L_0x0921:
        r2 = r1.countriesArray;
        r2 = r2.indexOf(r0);
        r3 = -1;
        if (r2 == r3) goto L_0x18b9;
    L_0x092a:
        r2 = r1.inputFields;
        r3 = 8;
        r2 = r2[r3];
        r3 = r1.countriesMap;
        r0 = r3.get(r0);
        r0 = (java.lang.CharSequence) r0;
        r2.setText(r0);
        goto L_0x18b9;
    L_0x093d:
        r3 = 2;
        if (r0 != r3) goto L_0x0e7a;
    L_0x0940:
        r0 = r1.paymentForm;
        r0 = r0.native_params;
        if (r0 == 0) goto L_0x097e;
    L_0x0946:
        r3 = new org.json.JSONObject;	 Catch:{ Exception -> 0x097a }
        r0 = r0.data;	 Catch:{ Exception -> 0x097a }
        r3.<init>(r0);	 Catch:{ Exception -> 0x097a }
        r0 = "android_pay_public_key";
        r0 = r3.getString(r0);	 Catch:{ Exception -> 0x095c }
        r4 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x095c }
        if (r4 != 0) goto L_0x095f;
    L_0x0959:
        r1.androidPayPublicKey = r0;	 Catch:{ Exception -> 0x095c }
        goto L_0x095f;
    L_0x095c:
        r4 = 0;
        r1.androidPayPublicKey = r4;	 Catch:{ Exception -> 0x097a }
    L_0x095f:
        r0 = "android_pay_bgcolor";
        r0 = r3.getInt(r0);	 Catch:{ Exception -> 0x096b }
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r0 = r0 | r4;
        r1.androidPayBackgroundColor = r0;	 Catch:{ Exception -> 0x096b }
        goto L_0x096e;
    L_0x096b:
        r4 = -1;
        r1.androidPayBackgroundColor = r4;	 Catch:{ Exception -> 0x097a }
    L_0x096e:
        r0 = "android_pay_inverse";
        r0 = r3.getBoolean(r0);	 Catch:{ Exception -> 0x0977 }
        r1.androidPayBlackTheme = r0;	 Catch:{ Exception -> 0x0977 }
        goto L_0x097e;
    L_0x0977:
        r1.androidPayBlackTheme = r5;	 Catch:{ Exception -> 0x097a }
        goto L_0x097e;
    L_0x097a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x097e:
        r0 = r1.isWebView;
        if (r0 == 0) goto L_0x0aa4;
    L_0x0982:
        r0 = r1.androidPayPublicKey;
        if (r0 == 0) goto L_0x0989;
    L_0x0986:
        r28.initAndroidPay(r29);
    L_0x0989:
        r0 = new android.widget.FrameLayout;
        r0.<init>(r2);
        r1.androidPayContainer = r0;
        r0 = r1.androidPayContainer;
        r3 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r0.setId(r3);
        r0 = r1.androidPayContainer;
        r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r0.setBackgroundDrawable(r3);
        r0 = r1.androidPayContainer;
        r3 = 8;
        r0.setVisibility(r3);
        r0 = r1.linearLayout2;
        r3 = r1.androidPayContainer;
        r4 = 50;
        r6 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r4);
        r1.webviewLoading = r8;
        r1.showEditDoneProgress(r8, r8);
        r0 = r1.progressView;
        r0.setVisibility(r5);
        r0 = r1.doneItem;
        r0.setEnabled(r5);
        r0 = r1.doneItem;
        r0 = r0.getContentView();
        r3 = 4;
        r0.setVisibility(r3);
        r0 = new org.telegram.ui.PaymentFormActivity$5;
        r0.<init>(r2);
        r1.webView = r0;
        r0 = r1.webView;
        r0 = r0.getSettings();
        r0.setJavaScriptEnabled(r8);
        r0 = r1.webView;
        r0 = r0.getSettings();
        r0.setDomStorageEnabled(r8);
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r0 < r3) goto L_0x09ff;
    L_0x09ed:
        r0 = r1.webView;
        r0 = r0.getSettings();
        r0.setMixedContentMode(r5);
        r0 = android.webkit.CookieManager.getInstance();
        r3 = r1.webView;
        r0.setAcceptThirdPartyCookies(r3, r8);
    L_0x09ff:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 17;
        if (r0 < r3) goto L_0x0a12;
    L_0x0a05:
        r0 = r1.webView;
        r3 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy;
        r4 = 0;
        r3.<init>(r1, r4);
        r4 = "TelegramWebviewProxy";
        r0.addJavascriptInterface(r3, r4);
    L_0x0a12:
        r0 = r1.webView;
        r3 = new org.telegram.ui.PaymentFormActivity$6;
        r3.<init>();
        r0.setWebViewClient(r3);
        r0 = r1.linearLayout2;
        r3 = r1.webView;
        r4 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4);
        r0.addView(r3, r4);
        r0 = r1.sectionCell;
        r3 = new org.telegram.ui.Cells.ShadowSectionCell;
        r3.<init>(r2);
        r4 = 2;
        r0[r4] = r3;
        r0 = r1.linearLayout2;
        r3 = r1.sectionCell;
        r3 = r3[r4];
        r4 = -2;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r7);
        r0 = new org.telegram.ui.Cells.TextCheckCell;
        r0.<init>(r2);
        r1.checkCell1 = r0;
        r0 = r1.checkCell1;
        r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r0.setBackgroundDrawable(r3);
        r0 = r1.checkCell1;
        r3 = NUM; // 0x7f0e0814 float:1.8879232E38 double:1.0531631784E-314;
        r4 = "PaymentCardSavePaymentInformation";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = r1.saveCardInfo;
        r0.setTextAndCheck(r3, r4, r5);
        r0 = r1.linearLayout2;
        r3 = r1.checkCell1;
        r4 = -2;
        r6 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r7);
        r0 = r1.checkCell1;
        r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$55th3Bl6h3UNIH_JJ7d61ZMtWHE;
        r3.<init>(r1);
        r0.setOnClickListener(r3);
        r0 = r1.bottomCell;
        r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r3.<init>(r2);
        r0[r5] = r3;
        r0 = r1.bottomCell;
        r0 = r0[r5];
        r3 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r14);
        r0.setBackgroundDrawable(r2);
        r28.updateSavePaymentField();
        r0 = r1.linearLayout2;
        r2 = r1.bottomCell;
        r2 = r2[r5];
        r3 = -2;
        r4 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3);
        r0.addView(r2, r3);
        goto L_0x18b9;
    L_0x0aa4:
        r0 = r1.paymentForm;
        r0 = r0.native_params;
        if (r0 == 0) goto L_0x0ae4;
    L_0x0aaa:
        r3 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0ae0 }
        r0 = r0.data;	 Catch:{ Exception -> 0x0ae0 }
        r3.<init>(r0);	 Catch:{ Exception -> 0x0ae0 }
        r0 = "need_country";
        r0 = r3.getBoolean(r0);	 Catch:{ Exception -> 0x0aba }
        r1.need_card_country = r0;	 Catch:{ Exception -> 0x0aba }
        goto L_0x0abc;
    L_0x0aba:
        r1.need_card_country = r5;	 Catch:{ Exception -> 0x0ae0 }
    L_0x0abc:
        r0 = "need_zip";
        r0 = r3.getBoolean(r0);	 Catch:{ Exception -> 0x0ac5 }
        r1.need_card_postcode = r0;	 Catch:{ Exception -> 0x0ac5 }
        goto L_0x0ac7;
    L_0x0ac5:
        r1.need_card_postcode = r5;	 Catch:{ Exception -> 0x0ae0 }
    L_0x0ac7:
        r0 = "need_cardholder_name";
        r0 = r3.getBoolean(r0);	 Catch:{ Exception -> 0x0ad0 }
        r1.need_card_name = r0;	 Catch:{ Exception -> 0x0ad0 }
        goto L_0x0ad2;
    L_0x0ad0:
        r1.need_card_name = r5;	 Catch:{ Exception -> 0x0ae0 }
    L_0x0ad2:
        r0 = "publishable_key";
        r0 = r3.getString(r0);	 Catch:{ Exception -> 0x0adb }
        r1.stripeApiKey = r0;	 Catch:{ Exception -> 0x0adb }
        goto L_0x0ae4;
    L_0x0adb:
        r0 = "";
        r1.stripeApiKey = r0;	 Catch:{ Exception -> 0x0ae0 }
        goto L_0x0ae4;
    L_0x0ae0:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0ae4:
        r28.initAndroidPay(r29);
        r3 = 6;
        r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3];
        r1.inputFields = r0;
        r0 = 0;
    L_0x0aed:
        if (r0 >= r3) goto L_0x0e44;
    L_0x0aef:
        if (r0 != 0) goto L_0x0b25;
    L_0x0af1:
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e0817 float:1.8879238E38 double:1.05316318E-314;
        r6 = "PaymentCardTitle";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r5];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
        goto L_0x0b5b;
    L_0x0b25:
        r3 = 4;
        if (r0 != r3) goto L_0x0b5b;
    L_0x0b28:
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r8] = r4;
        r3 = r1.headerCell;
        r3 = r3[r8];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r8];
        r4 = NUM; // 0x7f0e080e float:1.887922E38 double:1.0531631754E-314;
        r6 = "PaymentBillingAddress";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r8];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
    L_0x0b5b:
        r3 = 3;
        if (r0 == r3) goto L_0x0b6a;
    L_0x0b5e:
        r3 = 5;
        if (r0 == r3) goto L_0x0b6a;
    L_0x0b61:
        r3 = 4;
        if (r0 != r3) goto L_0x0b68;
    L_0x0b64:
        r3 = r1.need_card_postcode;
        if (r3 == 0) goto L_0x0b6a;
    L_0x0b68:
        r3 = 1;
        goto L_0x0b6b;
    L_0x0b6a:
        r3 = 0;
    L_0x0b6b:
        r4 = new android.widget.FrameLayout;
        r4.<init>(r2);
        r6 = r1.linearLayout2;
        r7 = 50;
        r9 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7);
        r6.addView(r4, r7);
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r4.setBackgroundColor(r6);
        r6 = r1.inputFields;
        r7 = new org.telegram.ui.Components.EditTextBoldCursor;
        r7.<init>(r2);
        r6[r0] = r7;
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = java.lang.Integer.valueOf(r0);
        r6.setTag(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6.setTextSize(r8, r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = "windowBackgroundWhiteHintText";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r6.setHintTextColor(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r6.setTextColor(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = 0;
        r6.setBackgroundDrawable(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r6.setCursorColor(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6.setCursorSize(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r6.setCursorWidth(r7);
        r6 = 3;
        if (r0 != r6) goto L_0x0CLASSNAME;
    L_0x0be4:
        r7 = new android.text.InputFilter[r8];
        r9 = new android.text.InputFilter$LengthFilter;
        r9.<init>(r6);
        r7[r5] = r9;
        r6 = r1.inputFields;
        r6 = r6[r0];
        r6.setFilters(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        r6.setInputType(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = android.graphics.Typeface.DEFAULT;
        r6.setTypeface(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = android.text.method.PasswordTransformationMethod.getInstance();
        r6.setTransformationMethod(r7);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r0 != 0) goto L_0x0c1d;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = 3;
        r6.setInputType(r7);
        goto L_0x0CLASSNAME;
    L_0x0c1d:
        r6 = 4;
        if (r0 != r6) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$Ueinsx-MGryTGh-0YI94FAwwnHM;
        r7.<init>(r1);
        r6.setOnTouchListener(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r6.setInputType(r5);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r0 != r8) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = 16386; // 0x4002 float:2.2962E-41 double:8.096E-320;
        r6.setInputType(r7);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = 2;
        if (r0 != r6) goto L_0x0c4d;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = 4097; // 0x1001 float:5.741E-42 double:2.024E-320;
        r6.setInputType(r7);
        goto L_0x0CLASSNAME;
    L_0x0c4d:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r6.setInputType(r7);
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r6.setImeOptions(r7);
        if (r0 == 0) goto L_0x0cc6;
    L_0x0CLASSNAME:
        if (r0 == r8) goto L_0x0cb5;
    L_0x0CLASSNAME:
        r6 = 2;
        if (r0 == r6) goto L_0x0ca4;
    L_0x0CLASSNAME:
        r6 = 3;
        if (r0 == r6) goto L_0x0CLASSNAME;
    L_0x0c6a:
        r6 = 4;
        if (r0 == r6) goto L_0x0CLASSNAME;
    L_0x0c6d:
        r6 = 5;
        if (r0 == r6) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        goto L_0x0cd6;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x7f0e0841 float:1.8879323E38 double:1.0531632006E-314;
        r9 = "PaymentShippingZipPlaceholder";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r6.setHint(r7);
        goto L_0x0cd6;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x7f0e0837 float:1.8879303E38 double:1.0531631957E-314;
        r9 = "PaymentShippingCountry";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r6.setHint(r7);
        goto L_0x0cd6;
    L_0x0CLASSNAME:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x7f0e080f float:1.8879222E38 double:1.053163176E-314;
        r9 = "PaymentCardCvv";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r6.setHint(r7);
        goto L_0x0cd6;
    L_0x0ca4:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x7f0e0812 float:1.8879228E38 double:1.0531631774E-314;
        r9 = "PaymentCardName";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r6.setHint(r7);
        goto L_0x0cd6;
    L_0x0cb5:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x7f0e0810 float:1.8879224E38 double:1.0531631764E-314;
        r9 = "PaymentCardExpireDate";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r6.setHint(r7);
        goto L_0x0cd6;
    L_0x0cc6:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x7f0e0813 float:1.887923E38 double:1.053163178E-314;
        r9 = "PaymentCardNumber";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r6.setHint(r7);
    L_0x0cd6:
        if (r0 != 0) goto L_0x0ce5;
    L_0x0cd8:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = new org.telegram.ui.PaymentFormActivity$7;
        r7.<init>();
        r6.addTextChangedListener(r7);
        goto L_0x0cf3;
    L_0x0ce5:
        if (r0 != r8) goto L_0x0cf3;
    L_0x0ce7:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = new org.telegram.ui.PaymentFormActivity$8;
        r7.<init>();
        r6.addTextChangedListener(r7);
    L_0x0cf3:
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6.setPadding(r5, r5, r5, r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = org.telegram.messenger.LocaleController.isRTL;
        if (r7 == 0) goto L_0x0d0a;
    L_0x0d08:
        r7 = 5;
        goto L_0x0d0b;
    L_0x0d0a:
        r7 = 3;
    L_0x0d0b:
        r6.setGravity(r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r21 = -1;
        r22 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r27 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r4.addView(r6, r7);
        r6 = r1.inputFields;
        r6 = r6[r0];
        r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$kGOIockVRhCZimnBQEOXCbrCCls;
        r7.<init>(r1);
        r6.setOnEditorActionListener(r7);
        r6 = 3;
        if (r0 != r6) goto L_0x0d50;
    L_0x0d36:
        r6 = r1.sectionCell;
        r7 = new org.telegram.ui.Cells.ShadowSectionCell;
        r7.<init>(r2);
        r6[r5] = r7;
        r6 = r1.linearLayout2;
        r7 = r1.sectionCell;
        r7 = r7[r5];
        r9 = -2;
        r10 = -1;
        r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9);
        r6.addView(r7, r11);
        goto L_0x0e04;
    L_0x0d50:
        r6 = 5;
        r9 = -2;
        r10 = -1;
        if (r0 != r6) goto L_0x0dcd;
    L_0x0d55:
        r6 = r1.sectionCell;
        r7 = new org.telegram.ui.Cells.ShadowSectionCell;
        r7.<init>(r2);
        r11 = 2;
        r6[r11] = r7;
        r6 = r1.linearLayout2;
        r7 = r1.sectionCell;
        r7 = r7[r11];
        r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9);
        r6.addView(r7, r11);
        r6 = new org.telegram.ui.Cells.TextCheckCell;
        r6.<init>(r2);
        r1.checkCell1 = r6;
        r6 = r1.checkCell1;
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r6.setBackgroundDrawable(r7);
        r6 = r1.checkCell1;
        r7 = NUM; // 0x7f0e0814 float:1.8879232E38 double:1.0531631784E-314;
        r9 = "PaymentCardSavePaymentInformation";
        r7 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r9 = r1.saveCardInfo;
        r6.setTextAndCheck(r7, r9, r5);
        r6 = r1.linearLayout2;
        r7 = r1.checkCell1;
        r9 = -2;
        r10 = -1;
        r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9);
        r6.addView(r7, r11);
        r6 = r1.checkCell1;
        r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$7nFCu4nozpZFMiRn4XlVrELG-z4;
        r7.<init>(r1);
        r6.setOnClickListener(r7);
        r6 = r1.bottomCell;
        r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r7.<init>(r2);
        r6[r5] = r7;
        r6 = r1.bottomCell;
        r6 = r6[r5];
        r7 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r7, r14);
        r6.setBackgroundDrawable(r9);
        r28.updateSavePaymentField();
        r6 = r1.linearLayout2;
        r7 = r1.bottomCell;
        r7 = r7[r5];
        r9 = -2;
        r10 = -1;
        r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9);
        r6.addView(r7, r11);
        goto L_0x0e04;
    L_0x0dcd:
        if (r0 != 0) goto L_0x0e04;
    L_0x0dcf:
        r6 = new android.widget.FrameLayout;
        r6.<init>(r2);
        r1.androidPayContainer = r6;
        r6 = r1.androidPayContainer;
        r7 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r6.setId(r7);
        r6 = r1.androidPayContainer;
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r6.setBackgroundDrawable(r7);
        r6 = r1.androidPayContainer;
        r7 = 8;
        r6.setVisibility(r7);
        r6 = r1.androidPayContainer;
        r21 = -2;
        r22 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r23 = 21;
        r24 = 0;
        r25 = 0;
        r26 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r27 = 0;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r4.addView(r6, r7);
    L_0x0e04:
        if (r3 == 0) goto L_0x0e22;
    L_0x0e06:
        r3 = new org.telegram.ui.PaymentFormActivity$9;
        r3.<init>(r2);
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r6);
        r6 = r1.dividers;
        r6.add(r3);
        r6 = new android.widget.FrameLayout$LayoutParams;
        r7 = 83;
        r9 = -1;
        r6.<init>(r9, r8, r7);
        r4.addView(r3, r6);
    L_0x0e22:
        r3 = 4;
        if (r0 != r3) goto L_0x0e2d;
    L_0x0e25:
        r3 = r1.need_card_country;
        if (r3 == 0) goto L_0x0e2a;
    L_0x0e29:
        goto L_0x0e2d;
    L_0x0e2a:
        r3 = 8;
        goto L_0x0e3c;
    L_0x0e2d:
        r3 = 5;
        if (r0 != r3) goto L_0x0e34;
    L_0x0e30:
        r3 = r1.need_card_postcode;
        if (r3 == 0) goto L_0x0e2a;
    L_0x0e34:
        r3 = 2;
        if (r0 != r3) goto L_0x0e3f;
    L_0x0e37:
        r3 = r1.need_card_name;
        if (r3 != 0) goto L_0x0e3f;
    L_0x0e3b:
        goto L_0x0e2a;
    L_0x0e3c:
        r4.setVisibility(r3);
    L_0x0e3f:
        r0 = r0 + 1;
        r3 = 6;
        goto L_0x0aed;
    L_0x0e44:
        r0 = r1.need_card_country;
        if (r0 != 0) goto L_0x0e5c;
    L_0x0e48:
        r0 = r1.need_card_postcode;
        if (r0 != 0) goto L_0x0e5c;
    L_0x0e4c:
        r0 = r1.headerCell;
        r0 = r0[r8];
        r2 = 8;
        r0.setVisibility(r2);
        r0 = r1.sectionCell;
        r0 = r0[r5];
        r0.setVisibility(r2);
    L_0x0e5c:
        r0 = r1.need_card_postcode;
        if (r0 == 0) goto L_0x0e6d;
    L_0x0e60:
        r0 = r1.inputFields;
        r2 = 5;
        r0 = r0[r2];
        r2 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r0.setImeOptions(r2);
        goto L_0x18b9;
    L_0x0e6d:
        r0 = r1.inputFields;
        r2 = 3;
        r0 = r0[r2];
        r2 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r0.setImeOptions(r2);
        goto L_0x18b9;
    L_0x0e7a:
        if (r0 != r8) goto L_0x0f1c;
    L_0x0e7c:
        r0 = r1.requestedInfo;
        r0 = r0.shipping_options;
        r0 = r0.size();
        r3 = new org.telegram.ui.Cells.RadioCell[r0];
        r1.radioCells = r3;
        r3 = 0;
    L_0x0e89:
        if (r3 >= r0) goto L_0x0ef4;
    L_0x0e8b:
        r4 = r1.requestedInfo;
        r4 = r4.shipping_options;
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r4;
        r6 = r1.radioCells;
        r7 = new org.telegram.ui.Cells.RadioCell;
        r7.<init>(r2);
        r6[r3] = r7;
        r6 = r1.radioCells;
        r6 = r6[r3];
        r7 = java.lang.Integer.valueOf(r3);
        r6.setTag(r7);
        r6 = r1.radioCells;
        r6 = r6[r3];
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r6.setBackgroundDrawable(r7);
        r6 = r1.radioCells;
        r6 = r6[r3];
        r7 = 2;
        r9 = new java.lang.Object[r7];
        r7 = r4.prices;
        r7 = r1.getTotalPriceString(r7);
        r9[r5] = r7;
        r4 = r4.title;
        r9[r8] = r4;
        r4 = "%s - %s";
        r4 = java.lang.String.format(r4, r9);
        if (r3 != 0) goto L_0x0ed1;
    L_0x0ecf:
        r7 = 1;
        goto L_0x0ed2;
    L_0x0ed1:
        r7 = 0;
    L_0x0ed2:
        r9 = r0 + -1;
        if (r3 == r9) goto L_0x0ed8;
    L_0x0ed6:
        r9 = 1;
        goto L_0x0ed9;
    L_0x0ed8:
        r9 = 0;
    L_0x0ed9:
        r6.setText(r4, r7, r9);
        r4 = r1.radioCells;
        r4 = r4[r3];
        r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$vEFqYoe1G6TRzc-6QuSxLqi0_ls;
        r6.<init>(r1);
        r4.setOnClickListener(r6);
        r4 = r1.linearLayout2;
        r6 = r1.radioCells;
        r6 = r6[r3];
        r4.addView(r6);
        r3 = r3 + 1;
        goto L_0x0e89;
    L_0x0ef4:
        r0 = r1.bottomCell;
        r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r3.<init>(r2);
        r0[r5] = r3;
        r0 = r1.bottomCell;
        r0 = r0[r5];
        r3 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r14);
        r0.setBackgroundDrawable(r2);
        r0 = r1.linearLayout2;
        r2 = r1.bottomCell;
        r2 = r2[r5];
        r3 = -2;
        r4 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3);
        r0.addView(r2, r3);
        goto L_0x18b9;
    L_0x0f1c:
        r3 = 3;
        if (r0 != r3) goto L_0x115a;
    L_0x0f1f:
        r3 = 2;
        r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3];
        r1.inputFields = r0;
        r0 = 0;
    L_0x0var_:
        if (r0 >= r3) goto L_0x18b9;
    L_0x0var_:
        if (r0 != 0) goto L_0x0f5d;
    L_0x0var_:
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e0817 float:1.8879238E38 double:1.05316318E-314;
        r6 = "PaymentCardTitle";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r5];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
        goto L_0x0f5e;
    L_0x0f5d:
        r7 = -1;
    L_0x0f5e:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r2);
        r4 = r1.linearLayout2;
        r6 = 50;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r4.addView(r3, r6);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        if (r0 == r8) goto L_0x0var_;
    L_0x0var_:
        r4 = 1;
        goto L_0x0f7a;
    L_0x0var_:
        r4 = 0;
    L_0x0f7a:
        if (r4 == 0) goto L_0x0var_;
    L_0x0f7c:
        r6 = 7;
        if (r0 != r6) goto L_0x0var_;
    L_0x0f7f:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.phone_requested;
        if (r6 != 0) goto L_0x0var_;
    L_0x0var_:
        r4 = 0;
        goto L_0x0var_;
    L_0x0var_:
        r6 = 6;
        if (r0 != r6) goto L_0x0var_;
    L_0x0f8c:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r7 = r6.phone_requested;
        if (r7 != 0) goto L_0x0var_;
    L_0x0var_:
        r6 = r6.email_requested;
        if (r6 != 0) goto L_0x0var_;
    L_0x0var_:
        goto L_0x0var_;
    L_0x0var_:
        if (r4 == 0) goto L_0x0fb7;
    L_0x0f9b:
        r4 = new org.telegram.ui.PaymentFormActivity$10;
        r4.<init>(r2);
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r4.setBackgroundColor(r6);
        r6 = r1.dividers;
        r6.add(r4);
        r6 = new android.widget.FrameLayout$LayoutParams;
        r7 = 83;
        r9 = -1;
        r6.<init>(r9, r8, r7);
        r3.addView(r4, r6);
    L_0x0fb7:
        r4 = r1.inputFields;
        r6 = new org.telegram.ui.Components.EditTextBoldCursor;
        r6.<init>(r2);
        r4[r0] = r6;
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = java.lang.Integer.valueOf(r0);
        r4.setTag(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r8, r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = "windowBackgroundWhiteHintText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r4.setHintTextColor(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setTextColor(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = 0;
        r4.setBackgroundDrawable(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setCursorColor(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setCursorSize(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r6);
        if (r0 != 0) goto L_0x1028;
    L_0x1017:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.ui.-$$Lambda$PaymentFormActivity$NaAxpZmEom2J3d4n5KPbjk_hflw.INSTANCE;
        r4.setOnTouchListener(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.setInputType(r5);
        goto L_0x103a;
    L_0x1028:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r4.setInputType(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r6);
    L_0x103a:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r6);
        if (r0 == 0) goto L_0x1061;
    L_0x1046:
        if (r0 == r8) goto L_0x1049;
    L_0x1048:
        goto L_0x106e;
    L_0x1049:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x7f0e05b3 float:1.8877997E38 double:1.0531628775E-314;
        r7 = "LoginPassword";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r4.setHint(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.requestFocus();
        goto L_0x106e;
    L_0x1061:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = r1.paymentForm;
        r6 = r6.saved_credentials;
        r6 = r6.title;
        r4.setText(r6);
    L_0x106e:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setPadding(r5, r5, r5, r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x1085;
    L_0x1083:
        r6 = 5;
        goto L_0x1086;
    L_0x1085:
        r6 = 3;
    L_0x1086:
        r4.setGravity(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r21 = -1;
        r22 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r27 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r3.addView(r4, r6);
        r3 = r1.inputFields;
        r3 = r3[r0];
        r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$7nDt_ueaNatOk32R3AVkL3FoSzw;
        r4.<init>(r1);
        r3.setOnEditorActionListener(r4);
        if (r0 != r8) goto L_0x1155;
    L_0x10b0:
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e0820 float:1.8879256E38 double:1.0531631843E-314;
        r6 = new java.lang.Object[r8];
        r7 = r1.paymentForm;
        r7 = r7.saved_credentials;
        r7 = r7.title;
        r6[r5] = r7;
        r7 = "PaymentConfirmationMessage";
        r4 = org.telegram.messenger.LocaleController.formatString(r7, r4, r6);
        r3.setText(r4);
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7var_d6 float:1.7945012E38 double:1.052935609E-314;
        r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r4, r14);
        r3.setBackgroundDrawable(r4);
        r3 = r1.linearLayout2;
        r4 = r1.bottomCell;
        r4 = r4[r5];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
        r3 = r1.settingsCell;
        r4 = new org.telegram.ui.Cells.TextSettingsCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.settingsCell;
        r3 = r3[r5];
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r3.setBackgroundDrawable(r4);
        r3 = r1.settingsCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e0821 float:1.8879258E38 double:1.053163185E-314;
        r6 = "PaymentConfirmationNewCard";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4, r5);
        r3 = r1.linearLayout2;
        r4 = r1.settingsCell;
        r4 = r4[r5];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
        r3 = r1.settingsCell;
        r3 = r3[r5];
        r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$hOneDfBvow-ay5NfpKknN53qGXA;
        r4.<init>(r1);
        r3.setOnClickListener(r4);
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r8] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r8];
        r4 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r4, r14);
        r3.setBackgroundDrawable(r6);
        r3 = r1.linearLayout2;
        r4 = r1.bottomCell;
        r4 = r4[r8];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
    L_0x1155:
        r0 = r0 + 1;
        r3 = 2;
        goto L_0x0var_;
    L_0x115a:
        r3 = 4;
        if (r0 == r3) goto L_0x14b8;
    L_0x115d:
        r3 = 5;
        if (r0 != r3) goto L_0x1162;
    L_0x1160:
        goto L_0x14b8;
    L_0x1162:
        r3 = 6;
        if (r0 != r3) goto L_0x18b9;
    L_0x1165:
        r0 = new org.telegram.ui.Cells.EditTextSettingsCell;
        r0.<init>(r2);
        r1.codeFieldCell = r0;
        r0 = r1.codeFieldCell;
        r3 = NUM; // 0x7f0e0807 float:1.8879206E38 double:1.053163172E-314;
        r4 = "PasswordCode";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = "";
        r0.setTextAndHint(r4, r3, r5);
        r0 = r1.codeFieldCell;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r3);
        r0 = r1.codeFieldCell;
        r0 = r0.getTextView();
        r3 = 3;
        r0.setInputType(r3);
        r3 = 6;
        r0.setImeOptions(r3);
        r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$zabhB89XpHUfevzEww0brDV8Dzs;
        r3.<init>(r1);
        r0.setOnEditorActionListener(r3);
        r3 = new org.telegram.ui.PaymentFormActivity$15;
        r3.<init>();
        r0.addTextChangedListener(r3);
        r0 = r1.linearLayout2;
        r3 = r1.codeFieldCell;
        r4 = -2;
        r6 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r7);
        r0 = r1.bottomCell;
        r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r3.<init>(r2);
        r4 = 2;
        r0[r4] = r3;
        r0 = r1.bottomCell;
        r0 = r0[r4];
        r3 = NUM; // 0x7var_d6 float:1.7945012E38 double:1.052935609E-314;
        r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r14);
        r0.setBackgroundDrawable(r3);
        r0 = r1.linearLayout2;
        r3 = r1.bottomCell;
        r3 = r3[r4];
        r4 = -2;
        r6 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r7);
        r0 = r1.settingsCell;
        r3 = new org.telegram.ui.Cells.TextSettingsCell;
        r3.<init>(r2);
        r0[r8] = r3;
        r0 = r1.settingsCell;
        r0 = r0[r8];
        r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r0.setBackgroundDrawable(r3);
        r0 = r1.settingsCell;
        r0 = r0[r8];
        r0.setTag(r13);
        r0 = r1.settingsCell;
        r0 = r0[r8];
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r0.setTextColor(r3);
        r0 = r1.settingsCell;
        r0 = r0[r8];
        r3 = NUM; // 0x7f0e0911 float:1.8879745E38 double:1.0531633034E-314;
        r4 = "ResendCode";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.setText(r3, r8);
        r0 = r1.linearLayout2;
        r3 = r1.settingsCell;
        r3 = r3[r8];
        r4 = -2;
        r6 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r7);
        r0 = r1.settingsCell;
        r0 = r0[r8];
        r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$I7CPk7usQoOQw119fB6a-FgtbsU;
        r3.<init>(r1);
        r0.setOnClickListener(r3);
        r0 = r1.settingsCell;
        r3 = new org.telegram.ui.Cells.TextSettingsCell;
        r3.<init>(r2);
        r0[r5] = r3;
        r0 = r1.settingsCell;
        r0 = r0[r5];
        r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r0.setBackgroundDrawable(r3);
        r0 = r1.settingsCell;
        r0 = r0[r5];
        r3 = "windowBackgroundWhiteRedText3";
        r0.setTag(r3);
        r0 = r1.settingsCell;
        r0 = r0[r5];
        r3 = "windowBackgroundWhiteRedText3";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.setTextColor(r3);
        r0 = r1.settingsCell;
        r0 = r0[r5];
        r3 = NUM; // 0x7f0e0002 float:1.8875042E38 double:1.0531621576E-314;
        r4 = "AbortPassword";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.setText(r3, r5);
        r0 = r1.linearLayout2;
        r3 = r1.settingsCell;
        r3 = r3[r5];
        r4 = -2;
        r6 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r7);
        r0 = r1.settingsCell;
        r0 = r0[r5];
        r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$u3SvDLQ9ZpBSRMUM2jg2lBOMsbk;
        r3.<init>(r1);
        r0.setOnClickListener(r3);
        r3 = 3;
        r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3];
        r1.inputFields = r0;
        r0 = 0;
    L_0x1283:
        if (r0 >= r3) goto L_0x14b3;
    L_0x1285:
        if (r0 != 0) goto L_0x12bb;
    L_0x1287:
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e082e float:1.8879285E38 double:1.053163191E-314;
        r6 = "PaymentPasswordTitle";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r5];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
        goto L_0x12f2;
    L_0x12bb:
        r3 = 2;
        if (r0 != r3) goto L_0x12f2;
    L_0x12be:
        r3 = r1.headerCell;
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r4.<init>(r2);
        r3[r8] = r4;
        r3 = r1.headerCell;
        r3 = r3[r8];
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        r3 = r1.headerCell;
        r3 = r3[r8];
        r4 = NUM; // 0x7f0e082a float:1.8879277E38 double:1.053163189E-314;
        r6 = "PaymentPasswordEmailTitle";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.linearLayout2;
        r4 = r1.headerCell;
        r4 = r4[r8];
        r6 = -2;
        r7 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r3.addView(r4, r9);
        goto L_0x12f3;
    L_0x12f2:
        r7 = -1;
    L_0x12f3:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r2);
        r4 = r1.linearLayout2;
        r6 = 50;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6);
        r4.addView(r3, r6);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r3.setBackgroundColor(r4);
        if (r0 != 0) goto L_0x1328;
    L_0x130c:
        r4 = new org.telegram.ui.PaymentFormActivity$16;
        r4.<init>(r2);
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r4.setBackgroundColor(r6);
        r6 = r1.dividers;
        r6.add(r4);
        r6 = new android.widget.FrameLayout$LayoutParams;
        r7 = 83;
        r9 = -1;
        r6.<init>(r9, r8, r7);
        r3.addView(r4, r6);
    L_0x1328:
        r4 = r1.inputFields;
        r6 = new org.telegram.ui.Components.EditTextBoldCursor;
        r6.<init>(r2);
        r4[r0] = r6;
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = java.lang.Integer.valueOf(r0);
        r4.setTag(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r8, r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = "windowBackgroundWhiteHintText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r4.setHintTextColor(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setTextColor(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r7 = 0;
        r4.setBackgroundDrawable(r7);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r4.setCursorColor(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setCursorSize(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r6);
        if (r0 == 0) goto L_0x139f;
    L_0x1388:
        if (r0 != r8) goto L_0x138b;
    L_0x138a:
        goto L_0x139f;
    L_0x138b:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = 33;
        r4.setInputType(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r6);
        goto L_0x13bb;
    L_0x139f:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r4.setInputType(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r6);
    L_0x13bb:
        if (r0 == 0) goto L_0x13e5;
    L_0x13bd:
        if (r0 == r8) goto L_0x13d4;
    L_0x13bf:
        r4 = 2;
        if (r0 == r4) goto L_0x13c3;
    L_0x13c2:
        goto L_0x13fc;
    L_0x13c3:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x7f0e0828 float:1.8879273E38 double:1.053163188E-314;
        r9 = "PaymentPasswordEmail";
        r6 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r4.setHint(r6);
        goto L_0x13fc;
    L_0x13d4:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x7f0e082d float:1.8879283E38 double:1.0531631907E-314;
        r9 = "PaymentPasswordReEnter";
        r6 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r4.setHint(r6);
        goto L_0x13fc;
    L_0x13e5:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x7f0e082b float:1.8879279E38 double:1.0531631897E-314;
        r9 = "PaymentPasswordEnter";
        r6 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r4.setHint(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r4.requestFocus();
    L_0x13fc:
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setPadding(r5, r5, r5, r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x1413;
    L_0x1411:
        r6 = 5;
        goto L_0x1414;
    L_0x1413:
        r6 = 3;
    L_0x1414:
        r4.setGravity(r6);
        r4 = r1.inputFields;
        r4 = r4[r0];
        r21 = -1;
        r22 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r27 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r3.addView(r4, r6);
        r3 = r1.inputFields;
        r3 = r3[r0];
        r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$wikSuuvfxhb2U7Hw9X3oaWcgGS8;
        r4.<init>(r1);
        r3.setOnEditorActionListener(r4);
        if (r0 != r8) goto L_0x1475;
    L_0x143e:
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r5] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f0e082c float:1.887928E38 double:1.05316319E-314;
        r6 = "PaymentPasswordInfo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7var_d6 float:1.7945012E38 double:1.052935609E-314;
        r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r4, r14);
        r3.setBackgroundDrawable(r4);
        r3 = r1.linearLayout2;
        r4 = r1.bottomCell;
        r4 = r4[r5];
        r6 = -2;
        r9 = -1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6);
        r3.addView(r4, r10);
        goto L_0x14ae;
    L_0x1475:
        r3 = 2;
        if (r0 != r3) goto L_0x14ae;
    L_0x1478:
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r8] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r8];
        r4 = NUM; // 0x7f0e0829 float:1.8879275E38 double:1.0531631887E-314;
        r6 = "PaymentPasswordEmailInfo";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r3.setText(r4);
        r3 = r1.bottomCell;
        r3 = r3[r8];
        r4 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r4, r14);
        r3.setBackgroundDrawable(r6);
        r3 = r1.linearLayout2;
        r4 = r1.bottomCell;
        r4 = r4[r8];
        r6 = -2;
        r9 = -1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6);
        r3.addView(r4, r10);
    L_0x14ae:
        r0 = r0 + 1;
        r3 = 3;
        goto L_0x1283;
    L_0x14b3:
        r28.updatePasswordFields();
        goto L_0x18b9;
    L_0x14b8:
        r7 = 0;
        r0 = new org.telegram.ui.Cells.PaymentInfoCell;
        r0.<init>(r2);
        r1.paymentInfoCell = r0;
        r0 = r1.paymentInfoCell;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r3);
        r0 = r1.paymentInfoCell;
        r3 = r1.messageObject;
        r3 = r3.messageOwner;
        r3 = r3.media;
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r3;
        r4 = r1.currentBotName;
        r0.setInvoice(r3, r4);
        r0 = r1.linearLayout2;
        r3 = r1.paymentInfoCell;
        r4 = -2;
        r6 = -1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r10);
        r0 = r1.sectionCell;
        r3 = new org.telegram.ui.Cells.ShadowSectionCell;
        r3.<init>(r2);
        r0[r5] = r3;
        r0 = r1.linearLayout2;
        r3 = r1.sectionCell;
        r3 = r3[r5];
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4);
        r0.addView(r3, r10);
        r0 = new java.util.ArrayList;
        r3 = r1.paymentForm;
        r3 = r3.invoice;
        r3 = r3.prices;
        r0.<init>(r3);
        r3 = r1.shippingOption;
        if (r3 == 0) goto L_0x150f;
    L_0x150a:
        r3 = r3.prices;
        r0.addAll(r3);
    L_0x150f:
        r3 = r1.getTotalPriceString(r0);
        r4 = 0;
    L_0x1514:
        r6 = r0.size();
        if (r4 >= r6) goto L_0x154b;
    L_0x151a:
        r6 = r0.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r6;
        r10 = new org.telegram.ui.Cells.TextPriceCell;
        r10.<init>(r2);
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r10.setBackgroundColor(r11);
        r11 = r6.label;
        r12 = org.telegram.messenger.LocaleController.getInstance();
        r7 = r6.amount;
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.currency;
        r6 = r12.formatCurrencyString(r7, r6);
        r10.setTextAndValue(r11, r6, r5);
        r6 = r1.linearLayout2;
        r6.addView(r10);
        r4 = r4 + 1;
        r7 = 0;
        r8 = 1;
        goto L_0x1514;
    L_0x154b:
        r0 = new org.telegram.ui.Cells.TextPriceCell;
        r0.<init>(r2);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r4);
        r4 = NUM; // 0x7f0e0847 float:1.8879335E38 double:1.0531632036E-314;
        r6 = "PaymentTransactionTotal";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r6 = 1;
        r0.setTextAndValue(r4, r3, r6);
        r4 = r1.linearLayout2;
        r4.addView(r0);
        r0 = new org.telegram.ui.PaymentFormActivity$11;
        r0.<init>(r2);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r4);
        r4 = r1.dividers;
        r4.add(r0);
        r4 = r1.linearLayout2;
        r6 = new android.widget.FrameLayout$LayoutParams;
        r7 = 83;
        r8 = -1;
        r10 = 1;
        r6.<init>(r8, r10, r7);
        r4.addView(r0, r6);
        r0 = r1.detailSettingsCell;
        r4 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r4.<init>(r2);
        r0[r5] = r4;
        r0 = r1.detailSettingsCell;
        r0 = r0[r5];
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10);
        r0.setBackgroundDrawable(r4);
        r0 = r1.detailSettingsCell;
        r0 = r0[r5];
        r4 = r1.cardName;
        r6 = NUM; // 0x7f0e081a float:1.8879244E38 double:1.0531631813E-314;
        r7 = "PaymentCheckoutMethod";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r0.setTextAndValue(r4, r6, r10);
        r0 = r1.linearLayout2;
        r4 = r1.detailSettingsCell;
        r4 = r4[r5];
        r0.addView(r4);
        r0 = r1.currentStep;
        r4 = 4;
        if (r0 != r4) goto L_0x15c8;
    L_0x15bc:
        r0 = r1.detailSettingsCell;
        r0 = r0[r5];
        r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$-_v7hZyhHE9vxsissEj7icTtuGM;
        r4.<init>(r1);
        r0.setOnClickListener(r4);
    L_0x15c8:
        r0 = 0;
        r4 = 0;
    L_0x15ca:
        r6 = r1.paymentForm;
        r6 = r6.users;
        r6 = r6.size();
        if (r0 >= r6) goto L_0x15ea;
    L_0x15d4:
        r6 = r1.paymentForm;
        r6 = r6.users;
        r6 = r6.get(r0);
        r6 = (org.telegram.tgnet.TLRPC.User) r6;
        r7 = r6.id;
        r8 = r1.paymentForm;
        r8 = r8.provider_id;
        if (r7 != r8) goto L_0x15e7;
    L_0x15e6:
        r4 = r6;
    L_0x15e7:
        r0 = r0 + 1;
        goto L_0x15ca;
    L_0x15ea:
        if (r4 == 0) goto L_0x1623;
    L_0x15ec:
        r0 = r1.detailSettingsCell;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r6.<init>(r2);
        r7 = 1;
        r0[r7] = r6;
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r7);
        r0.setBackgroundDrawable(r6);
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = r4.first_name;
        r4 = r4.last_name;
        r4 = org.telegram.messenger.ContactsController.formatName(r6, r4);
        r6 = NUM; // 0x7f0e081e float:1.8879252E38 double:1.0531631833E-314;
        r8 = "PaymentCheckoutProvider";
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r0.setTextAndValue(r4, r6, r7);
        r0 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r6 = r6[r7];
        r0.addView(r6);
        goto L_0x1625;
    L_0x1623:
        r4 = "";
    L_0x1625:
        r0 = r1.validateRequest;
        if (r0 == 0) goto L_0x177a;
    L_0x1629:
        r0 = r0.info;
        r0 = r0.shipping_address;
        if (r0 == 0) goto L_0x1684;
    L_0x162f:
        r6 = 6;
        r7 = new java.lang.Object[r6];
        r6 = r0.street_line1;
        r7[r5] = r6;
        r6 = r0.street_line2;
        r8 = 1;
        r7[r8] = r6;
        r6 = r0.city;
        r8 = 2;
        r7[r8] = r6;
        r6 = r0.state;
        r8 = 3;
        r7[r8] = r6;
        r6 = r0.country_iso2;
        r8 = 4;
        r7[r8] = r6;
        r0 = r0.post_code;
        r6 = 5;
        r7[r6] = r0;
        r0 = "%s %s, %s, %s, %s, %s";
        r0 = java.lang.String.format(r0, r7);
        r6 = r1.detailSettingsCell;
        r7 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r7.<init>(r2);
        r8 = 2;
        r6[r8] = r7;
        r6 = r1.detailSettingsCell;
        r6 = r6[r8];
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r6.setBackgroundColor(r7);
        r6 = r1.detailSettingsCell;
        r6 = r6[r8];
        r7 = NUM; // 0x7f0e0833 float:1.8879295E38 double:1.0531631937E-314;
        r10 = "PaymentShippingAddress";
        r7 = org.telegram.messenger.LocaleController.getString(r10, r7);
        r10 = 1;
        r6.setTextAndValue(r0, r7, r10);
        r0 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r6 = r6[r8];
        r0.addView(r6);
    L_0x1684:
        r0 = r1.validateRequest;
        r0 = r0.info;
        r0 = r0.name;
        if (r0 == 0) goto L_0x16c1;
    L_0x168c:
        r0 = r1.detailSettingsCell;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r6.<init>(r2);
        r7 = 3;
        r0[r7] = r6;
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r6);
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = r1.validateRequest;
        r6 = r6.info;
        r6 = r6.name;
        r8 = NUM; // 0x7f0e081b float:1.8879246E38 double:1.053163182E-314;
        r10 = "PaymentCheckoutName";
        r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
        r10 = 1;
        r0.setTextAndValue(r6, r8, r10);
        r0 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r6 = r6[r7];
        r0.addView(r6);
    L_0x16c1:
        r0 = r1.validateRequest;
        r0 = r0.info;
        r0 = r0.phone;
        if (r0 == 0) goto L_0x1707;
    L_0x16c9:
        r0 = r1.detailSettingsCell;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r6.<init>(r2);
        r7 = 4;
        r0[r7] = r6;
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r6);
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.phone;
        r6 = r6.format(r7);
        r7 = NUM; // 0x7f0e081d float:1.887925E38 double:1.053163183E-314;
        r8 = "PaymentCheckoutPhoneNumber";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r8 = 1;
        r0.setTextAndValue(r6, r7, r8);
        r0 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r7 = 4;
        r6 = r6[r7];
        r0.addView(r6);
    L_0x1707:
        r0 = r1.validateRequest;
        r0 = r0.info;
        r0 = r0.email;
        if (r0 == 0) goto L_0x1744;
    L_0x170f:
        r0 = r1.detailSettingsCell;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r6.<init>(r2);
        r7 = 5;
        r0[r7] = r6;
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r6);
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = r1.validateRequest;
        r6 = r6.info;
        r6 = r6.email;
        r8 = NUM; // 0x7f0e0819 float:1.8879242E38 double:1.053163181E-314;
        r10 = "PaymentCheckoutEmail";
        r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
        r10 = 1;
        r0.setTextAndValue(r6, r8, r10);
        r0 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r6 = r6[r7];
        r0.addView(r6);
    L_0x1744:
        r0 = r1.shippingOption;
        if (r0 == 0) goto L_0x177a;
    L_0x1748:
        r0 = r1.detailSettingsCell;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r6.<init>(r2);
        r7 = 6;
        r0[r7] = r6;
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r0.setBackgroundColor(r6);
        r0 = r1.detailSettingsCell;
        r0 = r0[r7];
        r6 = r1.shippingOption;
        r6 = r6.title;
        r8 = NUM; // 0x7f0e081f float:1.8879254E38 double:1.053163184E-314;
        r10 = "PaymentCheckoutShippingMethod";
        r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
        r0.setTextAndValue(r6, r8, r5);
        r0 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r6 = r6[r7];
        r0.addView(r6);
    L_0x177a:
        r0 = r1.currentStep;
        r6 = 4;
        if (r0 != r6) goto L_0x1892;
    L_0x177f:
        r0 = new android.widget.FrameLayout;
        r0.<init>(r2);
        r1.bottomLayout = r0;
        r0 = r1.bottomLayout;
        r6 = 1;
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6);
        r0.setBackgroundDrawable(r7);
        r0 = r1.bottomLayout;
        r6 = 48;
        r7 = 80;
        r8 = -1;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r6, r7);
        r9.addView(r0, r6);
        r0 = r1.bottomLayout;
        r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$UCCxWFA5zv52WCmPKPbwcSfjXPs;
        r6.<init>(r1, r4, r3);
        r0.setOnClickListener(r6);
        r0 = new android.widget.TextView;
        r0.<init>(r2);
        r1.payTextView = r0;
        r0 = r1.payTextView;
        r4 = "windowBackgroundWhiteBlueText6";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0.setTextColor(r4);
        r0 = r1.payTextView;
        r4 = NUM; // 0x7f0e081c float:1.8879248E38 double:1.0531631823E-314;
        r6 = 1;
        r7 = new java.lang.Object[r6];
        r7[r5] = r3;
        r3 = "PaymentCheckoutPay";
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r7);
        r0.setText(r3);
        r0 = r1.payTextView;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0.setTextSize(r6, r3);
        r0 = r1.payTextView;
        r3 = 17;
        r0.setGravity(r3);
        r0 = r1.payTextView;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r0.setTypeface(r3);
        r0 = r1.bottomLayout;
        r3 = r1.payTextView;
        r4 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4);
        r0.addView(r3, r4);
        r0 = new org.telegram.ui.Components.ContextProgressView;
        r0.<init>(r2, r5);
        r1.progressViewButton = r0;
        r0 = r1.progressViewButton;
        r3 = 4;
        r0.setVisibility(r3);
        r0 = r1.bottomLayout;
        r3 = r1.progressViewButton;
        r4 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4);
        r0.addView(r3, r4);
        r0 = new android.view.View;
        r0.<init>(r2);
        r3 = NUM; // 0x7var_e7 float:1.7945046E38 double:1.052935617E-314;
        r0.setBackgroundResource(r3);
        r19 = -1;
        r20 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r21 = 83;
        r22 = 0;
        r23 = 0;
        r24 = 0;
        r25 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r9.addView(r0, r3);
        r0 = r1.doneItem;
        r0.setEnabled(r5);
        r0 = r1.doneItem;
        r0 = r0.getContentView();
        r3 = 4;
        r0.setVisibility(r3);
        r0 = new org.telegram.ui.PaymentFormActivity$13;
        r0.<init>(r2);
        r1.webView = r0;
        r0 = r1.webView;
        r3 = -1;
        r0.setBackgroundColor(r3);
        r0 = r1.webView;
        r0 = r0.getSettings();
        r3 = 1;
        r0.setJavaScriptEnabled(r3);
        r0 = r1.webView;
        r0 = r0.getSettings();
        r0.setDomStorageEnabled(r3);
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r0 < r4) goto L_0x1875;
    L_0x1863:
        r0 = r1.webView;
        r0 = r0.getSettings();
        r0.setMixedContentMode(r5);
        r0 = android.webkit.CookieManager.getInstance();
        r4 = r1.webView;
        r0.setAcceptThirdPartyCookies(r4, r3);
    L_0x1875:
        r0 = r1.webView;
        r3 = new org.telegram.ui.PaymentFormActivity$14;
        r3.<init>();
        r0.setWebViewClient(r3);
        r0 = r1.webView;
        r3 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r4 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3);
        r9.addView(r0, r3);
        r0 = r1.webView;
        r3 = 8;
        r0.setVisibility(r3);
    L_0x1892:
        r0 = r1.sectionCell;
        r3 = new org.telegram.ui.Cells.ShadowSectionCell;
        r3.<init>(r2);
        r4 = 1;
        r0[r4] = r3;
        r0 = r1.sectionCell;
        r0 = r0[r4];
        r3 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r14);
        r0.setBackgroundDrawable(r2);
        r0 = r1.linearLayout2;
        r2 = r1.sectionCell;
        r2 = r2[r4];
        r3 = -2;
        r4 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3);
        r0.addView(r2, r3);
    L_0x18b9:
        r0 = r1.fragmentView;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ boolean lambda$createView$1$PaymentFormActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$PaymentFormActivity$YL0G4SiCRRdBSsARre0gVqa3im4(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$0$PaymentFormActivity(String str, String str2) {
        this.inputFields[4].setText(str);
        this.countryName = str2;
    }

    public /* synthetic */ boolean lambda$createView$2$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int intValue = ((Integer) textView.getTag()).intValue();
            while (true) {
                intValue++;
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (intValue < editTextBoldCursorArr.length) {
                    if (intValue != 4 && ((View) editTextBoldCursorArr[intValue].getParent()).getVisibility() == 0) {
                        this.inputFields[intValue].requestFocus();
                        break;
                    }
                } else {
                    break;
                }
            }
            return true;
        } else if (i != 6) {
            return false;
        } else {
            this.doneItem.performClick();
            return true;
        }
    }

    public /* synthetic */ void lambda$createView$3$PaymentFormActivity(View view) {
        this.saveShippingInfo ^= 1;
        this.checkCell1.setChecked(this.saveShippingInfo);
    }

    public /* synthetic */ void lambda$createView$4$PaymentFormActivity(View view) {
        this.saveCardInfo ^= 1;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    public /* synthetic */ boolean lambda$createView$6$PaymentFormActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$PaymentFormActivity$c3rgfJXwF9E12z1XSDEQQZO6iNM(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$5$PaymentFormActivity(String str, String str2) {
        this.inputFields[4].setText(str);
    }

    public /* synthetic */ boolean lambda$createView$7$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int intValue = ((Integer) textView.getTag()).intValue();
            do {
                intValue++;
                if (intValue >= this.inputFields.length) {
                    break;
                } else if (intValue == 4) {
                    intValue++;
                }
            } while (((View) this.inputFields[intValue].getParent()).getVisibility() != 0);
            this.inputFields[intValue].requestFocus();
            return true;
        } else if (i != 6) {
            return false;
        } else {
            this.doneItem.performClick();
            return true;
        }
    }

    public /* synthetic */ void lambda$createView$8$PaymentFormActivity(View view) {
        this.saveCardInfo ^= 1;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    public /* synthetic */ void lambda$createView$9$PaymentFormActivity(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        int i = 0;
        while (true) {
            RadioCell[] radioCellArr = this.radioCells;
            if (i < radioCellArr.length) {
                radioCellArr[i].setChecked(intValue == i, true);
                i++;
            } else {
                return;
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$11$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$12$PaymentFormActivity(View view) {
        this.passwordOk = false;
        goToNextStep();
    }

    public /* synthetic */ void lambda$createView$13$PaymentFormActivity(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo, null);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public void currentPasswordUpdated(TL_account_password tL_account_password) {
            }

            public void onFragmentDestroyed() {
            }

            public boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
                PaymentFormActivity.this.paymentForm.saved_credentials = null;
                PaymentFormActivity.this.paymentJson = str;
                PaymentFormActivity.this.saveCardInfo = z;
                PaymentFormActivity.this.cardName = str2;
                PaymentFormActivity.this.androidPayCredentials = tL_inputPaymentCredentialsAndroidPay;
                PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", NUM), true);
                return false;
            }
        });
        presentFragment(paymentFormActivity);
    }

    public /* synthetic */ void lambda$createView$15$PaymentFormActivity(String str, String str2, View view) {
        User user = this.botUser;
        if (user == null || user.verified) {
            showPayAlert(str2);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("payment_warning_");
        stringBuilder.append(this.botUser.id);
        String stringBuilder2 = stringBuilder.toString();
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        if (notificationsSettings.getBoolean(stringBuilder2, false)) {
            showPayAlert(str2);
            return;
        }
        notificationsSettings.edit().putBoolean(stringBuilder2, true).commit();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentWarning", NUM));
        builder.setMessage(LocaleController.formatString("PaymentWarningText", NUM, this.currentBotName, str));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PaymentFormActivity$MREN73dZdmfuVvIGQi8sgTjprGE(this, str2));
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$14$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
        showPayAlert(str);
    }

    public /* synthetic */ boolean lambda$createView$16$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    public /* synthetic */ void lambda$createView$18$PaymentFormActivity(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resendPasswordEmail(), -$$Lambda$PaymentFormActivity$kpdlJs1QwZ9_5EeFfvuu3eWN4uE.INSTANCE);
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$createView$20$PaymentFormActivity(View view) {
        Builder builder = new Builder(getParentActivity());
        CharSequence string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string);
            stringBuilder.append("\n\n");
            stringBuilder.append(LocaleController.getString("TurnPasswordOffPassport", NUM));
            string = stringBuilder.toString();
        }
        builder.setMessage(string);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PaymentFormActivity$UsXOIe_Tc2OBHUXrtn6gfLKTiRQ(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$19$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    public /* synthetic */ boolean lambda$createView$21$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        }
        if (i == 5) {
            int intValue = ((Integer) textView.getTag()).intValue();
            if (intValue == 0) {
                this.inputFields[1].requestFocus();
            } else if (intValue == 1) {
                this.inputFields[2].requestFocus();
            }
        }
        return false;
    }

    private void updatePasswordFields() {
        if (this.currentStep == 6 && this.bottomCell[2] != null) {
            int i = 0;
            this.doneItem.setVisibility(0);
            int i2;
            if (this.currentPassword == null) {
                showEditDoneProgress(true, true);
                this.bottomCell[2].setVisibility(8);
                this.settingsCell[0].setVisibility(8);
                this.settingsCell[1].setVisibility(8);
                this.codeFieldCell.setVisibility(8);
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (i2 = 0; i2 < 3; i2++) {
                    ((View) this.inputFields[i2].getParent()).setVisibility(8);
                }
                while (i < this.dividers.size()) {
                    ((View) this.dividers.get(i)).setVisibility(8);
                    i++;
                }
                return;
            }
            showEditDoneProgress(true, false);
            if (this.waitingForEmail) {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
                Object[] objArr = new Object[1];
                String str = this.currentPassword.email_unconfirmed_pattern;
                String str2 = "";
                if (str == null) {
                    str = str2;
                }
                objArr[0] = str;
                textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                this.bottomCell[2].setVisibility(0);
                this.settingsCell[0].setVisibility(0);
                this.settingsCell[1].setVisibility(0);
                this.codeFieldCell.setVisibility(0);
                this.bottomCell[1].setText(str2);
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (i2 = 0; i2 < 3; i2++) {
                    ((View) this.inputFields[i2].getParent()).setVisibility(8);
                }
                while (i < this.dividers.size()) {
                    ((View) this.dividers.get(i)).setVisibility(8);
                    i++;
                }
                return;
            }
            this.bottomCell[2].setVisibility(8);
            this.settingsCell[0].setVisibility(8);
            this.settingsCell[1].setVisibility(8);
            this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", NUM));
            this.codeFieldCell.setVisibility(8);
            this.headerCell[0].setVisibility(0);
            this.headerCell[1].setVisibility(0);
            this.bottomCell[0].setVisibility(0);
            for (i2 = 0; i2 < 3; i2++) {
                ((View) this.inputFields[i2].getParent()).setVisibility(0);
            }
            for (i2 = 0; i2 < this.dividers.size(); i2++) {
                ((View) this.dividers.get(i2)).setVisibility(0);
            }
        }
    }

    private void loadPasswordInfo() {
        if (!this.loadingPasswordInfo) {
            this.loadingPasswordInfo = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new -$$Lambda$PaymentFormActivity$yExXYmmuCbLQYgqTn41BdR-HyfE(this), 10);
        }
    }

    public /* synthetic */ void lambda$loadPasswordInfo$24$PaymentFormActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$27udhTKOJAFbo7x-UeiB3WlNSgQ(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$23$PaymentFormActivity(TL_error tL_error, TLObject tLObject) {
        this.loadingPasswordInfo = false;
        if (tL_error == null) {
            this.currentPassword = (TL_account_password) tLObject;
            if (TwoStepVerificationActivity.canHandleCurrentPassword(this.currentPassword, false)) {
                TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
                if (tL_payments_paymentForm != null && this.currentPassword.has_password) {
                    tL_payments_paymentForm.password_missing = false;
                    tL_payments_paymentForm.can_save_credentials = true;
                    updateSavePaymentField();
                }
                TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
                PaymentFormActivity paymentFormActivity = this.passwordFragment;
                if (paymentFormActivity != null) {
                    paymentFormActivity.setCurrentPassword(this.currentPassword);
                }
                if (!this.currentPassword.has_password && this.shortPollRunnable == null) {
                    this.shortPollRunnable = new -$$Lambda$PaymentFormActivity$8V0l-YKYFLVQJz5pV-MyAIHFF4o(this);
                    AndroidUtilities.runOnUIThread(this.shortPollRunnable, 5000);
                }
            } else {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            }
        }
    }

    public /* synthetic */ void lambda$null$22$PaymentFormActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo();
            this.shortPollRunnable = null;
        }
    }

    private void showAlertWithText(String str, String str2) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void showPayAlert(String str) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", NUM));
        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", NUM, str, this.currentBotName, this.currentItemName));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PaymentFormActivity$zNiawUMZQLp4e5nGq2pzsvlUWzE(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$showPayAlert$25$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        setDonePressed(true);
        sendData();
    }

    private String getTotalPriceString(ArrayList<TL_labeledPrice> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += ((TL_labeledPrice) arrayList.get(i)).amount;
        }
        return LocaleController.getInstance().formatCurrencyString(j, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TL_labeledPrice> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += ((TL_labeledPrice) arrayList.get(i)).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(j, this.paymentForm.invoice.currency, false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentFinished);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
        if (paymentFormActivityDelegate != null) {
            paymentFormActivityDelegate.onFragmentDestroyed();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
        }
        WebView webView = this.webView;
        if (webView != null) {
            try {
                ViewParent parent = webView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(this.webView);
                }
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                this.webViewUrl = null;
                this.webView.destroy();
                this.webView = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            if ((this.currentStep == 2 || this.currentStep == 6) && VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(8192);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        super.onFragmentDestroy();
        this.canceled = true;
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            WebView webView = this.webView;
            if (webView == null) {
                int i = this.currentStep;
                if (i == 2) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                } else if (i == 3) {
                    this.inputFields[1].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[1]);
                } else if (i == 6 && !this.waitingForEmail) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
            } else if (this.currentStep != 4) {
                String str = this.paymentForm.url;
                this.webViewUrl = str;
                webView.loadUrl(str);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TL_payments_paymentForm tL_payments_paymentForm;
        if (i == NotificationCenter.didSetTwoStepPassword) {
            tL_payments_paymentForm = this.paymentForm;
            tL_payments_paymentForm.password_missing = false;
            tL_payments_paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (i == NotificationCenter.didRemoveTwoStepPassword) {
            tL_payments_paymentForm = this.paymentForm;
            tL_payments_paymentForm.password_missing = true;
            tL_payments_paymentForm.can_save_credentials = false;
            updateSavePaymentField();
        } else if (i == NotificationCenter.paymentFinished) {
            removeSelfFromStack();
        }
    }

    private void showAndroidPay() {
        if (getParentActivity() != null && this.androidPayContainer != null) {
            WalletFragmentStyle walletFragmentStyle;
            PaymentMethodTokenizationParameters build;
            WalletFragmentOptions.Builder newBuilder = WalletFragmentOptions.newBuilder();
            newBuilder.setEnvironment(this.paymentForm.invoice.test ? 3 : 1);
            newBuilder.setMode(1);
            int i = 6;
            if (this.androidPayPublicKey != null) {
                this.androidPayContainer.setBackgroundColor(this.androidPayBackgroundColor);
                walletFragmentStyle = new WalletFragmentStyle();
                walletFragmentStyle.setBuyButtonText(5);
                if (!this.androidPayBlackTheme) {
                    i = 4;
                }
                walletFragmentStyle.setBuyButtonAppearance(i);
                walletFragmentStyle.setBuyButtonWidth(-1);
            } else {
                walletFragmentStyle = new WalletFragmentStyle();
                walletFragmentStyle.setBuyButtonText(6);
                walletFragmentStyle.setBuyButtonAppearance(6);
                walletFragmentStyle.setBuyButtonWidth(-2);
            }
            newBuilder.setFragmentStyle(walletFragmentStyle);
            WalletFragment newInstance = WalletFragment.newInstance(newBuilder.build());
            FragmentTransaction beginTransaction = getParentActivity().getFragmentManager().beginTransaction();
            beginTransaction.replace(4000, newInstance);
            beginTransaction.commit();
            ArrayList arrayList = new ArrayList(this.paymentForm.invoice.prices);
            TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                arrayList.addAll(tL_shippingOption.prices);
            }
            this.totalPriceDecimal = getTotalPriceDecimalString(arrayList);
            PaymentMethodTokenizationParameters.Builder newBuilder2;
            if (this.androidPayPublicKey != null) {
                newBuilder2 = PaymentMethodTokenizationParameters.newBuilder();
                newBuilder2.setPaymentMethodTokenizationType(2);
                newBuilder2.addParameter("publicKey", this.androidPayPublicKey);
                build = newBuilder2.build();
            } else {
                newBuilder2 = PaymentMethodTokenizationParameters.newBuilder();
                newBuilder2.setPaymentMethodTokenizationType(1);
                newBuilder2.addParameter("gateway", "stripe");
                newBuilder2.addParameter("stripe:publishableKey", this.stripeApiKey);
                newBuilder2.addParameter("stripe:version", "3.5.0");
                build = newBuilder2.build();
            }
            MaskedWalletRequest.Builder newBuilder3 = MaskedWalletRequest.newBuilder();
            newBuilder3.setPaymentMethodTokenizationParameters(build);
            newBuilder3.setEstimatedTotalPrice(this.totalPriceDecimal);
            newBuilder3.setCurrencyCode(this.paymentForm.invoice.currency);
            MaskedWalletRequest build2 = newBuilder3.build();
            WalletFragmentInitParams.Builder newBuilder4 = WalletFragmentInitParams.newBuilder();
            newBuilder4.setMaskedWalletRequest(build2);
            newBuilder4.setMaskedWalletRequestCode(1000);
            newInstance.initialize(newBuilder4.build());
            this.androidPayContainer.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 1000) {
            if (i2 == -1) {
                showEditDoneProgress(true, true);
                setDonePressed(true);
                MaskedWallet maskedWallet = (MaskedWallet) intent.getParcelableExtra("com.google.android.gms.wallet.EXTRA_MASKED_WALLET");
                Cart.Builder newBuilder = Cart.newBuilder();
                newBuilder.setCurrencyCode(this.paymentForm.invoice.currency);
                newBuilder.setTotalPrice(this.totalPriceDecimal);
                ArrayList arrayList = new ArrayList(this.paymentForm.invoice.prices);
                TL_shippingOption tL_shippingOption = this.shippingOption;
                if (tL_shippingOption != null) {
                    arrayList.addAll(tL_shippingOption.prices);
                }
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    TL_labeledPrice tL_labeledPrice = (TL_labeledPrice) arrayList.get(i3);
                    String formatCurrencyDecimalString = LocaleController.getInstance().formatCurrencyDecimalString(tL_labeledPrice.amount, this.paymentForm.invoice.currency, false);
                    LineItem.Builder newBuilder2 = LineItem.newBuilder();
                    newBuilder2.setCurrencyCode(this.paymentForm.invoice.currency);
                    newBuilder2.setQuantity("1");
                    newBuilder2.setDescription(tL_labeledPrice.label);
                    newBuilder2.setTotalPrice(formatCurrencyDecimalString);
                    newBuilder2.setUnitPrice(formatCurrencyDecimalString);
                    newBuilder.addLineItem(newBuilder2.build());
                }
                FullWalletRequest.Builder newBuilder3 = FullWalletRequest.newBuilder();
                newBuilder3.setCart(newBuilder.build());
                newBuilder3.setGoogleTransactionId(maskedWallet.getGoogleTransactionId());
                Wallet.Payments.loadFullWallet(this.googleApiClient, newBuilder3.build(), 1001);
                return;
            }
            showEditDoneProgress(true, false);
            setDonePressed(false);
        } else if (i != 1001) {
        } else {
            if (i2 == -1) {
                FullWallet fullWallet = (FullWallet) intent.getParcelableExtra("com.google.android.gms.wallet.EXTRA_FULL_WALLET");
                String token = fullWallet.getPaymentMethodToken().getToken();
                try {
                    if (this.androidPayPublicKey != null) {
                        this.androidPayCredentials = new TL_inputPaymentCredentialsAndroidPay();
                        this.androidPayCredentials.payment_token = new TL_dataJSON();
                        this.androidPayCredentials.payment_token.data = token;
                        this.androidPayCredentials.google_transaction_id = fullWallet.getGoogleTransactionId();
                        String[] paymentDescriptions = fullWallet.getPaymentDescriptions();
                        if (paymentDescriptions.length > 0) {
                            this.cardName = paymentDescriptions[0];
                        } else {
                            this.cardName = "Android Pay";
                        }
                    } else {
                        this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{r9.getType(), TokenParser.parseToken(token).getId()});
                        Card card = r9.getCard();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(card.getType());
                        stringBuilder.append(" *");
                        stringBuilder.append(card.getLast4());
                        this.cardName = stringBuilder.toString();
                    }
                    goToNextStep();
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                    return;
                } catch (JSONException unused) {
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                    return;
                }
            }
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    private void goToNextStep() {
        int i = this.currentStep;
        int i2;
        if (i == 0) {
            TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
            if (tL_payments_paymentForm.invoice.flexible) {
                i2 = 1;
            } else if (tL_payments_paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i2 = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i2 = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i2, this.requestedInfo, null, null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (i == 1) {
            if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i2 = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i2 = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i2, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (i == 2) {
            TL_payments_paymentForm tL_payments_paymentForm2 = this.paymentForm;
            if (tL_payments_paymentForm2.password_missing) {
                boolean z = this.saveCardInfo;
                if (z) {
                    this.passwordFragment = new PaymentFormActivity(tL_payments_paymentForm2, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, z, this.androidPayCredentials);
                    this.passwordFragment.setCurrentPassword(this.currentPassword);
                    this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                        public boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
                            if (PaymentFormActivity.this.delegate != null) {
                                PaymentFormActivity.this.delegate.didSelectNewCard(str, str2, z, tL_inputPaymentCredentialsAndroidPay);
                            }
                            if (PaymentFormActivity.this.isWebView) {
                                PaymentFormActivity.this.removeSelfFromStack();
                            }
                            return PaymentFormActivity.this.delegate != null;
                        }

                        public void onFragmentDestroyed() {
                            PaymentFormActivity.this.passwordFragment = null;
                        }

                        public void currentPasswordUpdated(TL_account_password tL_account_password) {
                            PaymentFormActivity.this.currentPassword = tL_account_password;
                        }
                    });
                    presentFragment(this.passwordFragment, this.isWebView);
                    return;
                }
            }
            PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
            if (paymentFormActivityDelegate != null) {
                paymentFormActivityDelegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials);
                finishFragment();
                return;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (i == 3) {
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.passwordOk ? 4 : 2, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.passwordOk ^ 1);
        } else if (i == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (i != 6) {
        } else {
            if (this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials)) {
                finishFragment();
            } else {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), true);
            }
        }
    }

    private void updateSavePaymentField() {
        if (this.bottomCell[0] != null && this.sectionCell[2] != null) {
            ShadowSectionCell[] shadowSectionCellArr;
            TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
            String str = "windowBackgroundGrayShadow";
            if (tL_payments_paymentForm.password_missing || tL_payments_paymentForm.can_save_credentials) {
                WebView webView = this.webView;
                if (webView == null || !(webView == null || this.webviewLoading)) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", NUM));
                    if (this.paymentForm.password_missing) {
                        loadPasswordInfo();
                        spannableStringBuilder.append("\n");
                        int length = spannableStringBuilder.length();
                        String string = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", NUM);
                        int indexOf = string.indexOf(42);
                        int lastIndexOf = string.lastIndexOf(42);
                        spannableStringBuilder.append(string);
                        if (!(indexOf == -1 || lastIndexOf == -1)) {
                            indexOf += length;
                            lastIndexOf += length;
                            this.bottomCell[0].getTextView().setMovementMethod(new LinkMovementMethodMy());
                            string = "";
                            spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, string);
                            spannableStringBuilder.replace(indexOf, indexOf + 1, string);
                            spannableStringBuilder.setSpan(new LinkSpan(), indexOf, lastIndexOf - 1, 33);
                        }
                    }
                    this.checkCell1.setEnabled(true);
                    this.bottomCell[0].setText(spannableStringBuilder);
                    this.checkCell1.setVisibility(0);
                    this.bottomCell[0].setVisibility(0);
                    shadowSectionCellArr = this.sectionCell;
                    shadowSectionCellArr[2].setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCellArr[2].getContext(), NUM, str));
                    return;
                }
            }
            this.checkCell1.setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            shadowSectionCellArr = this.sectionCell;
            shadowSectionCellArr[2].setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCellArr[2].getContext(), NUM, str));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036 A:{Catch:{ Exception -> 0x009f }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045 A:{Catch:{ Exception -> 0x009f }} */
    @android.annotation.SuppressLint({"HardwareIds"})
    public void fillNumber(java.lang.String r8) {
        /*
        r7 = this;
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x009f }
        r1 = "phone";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x009f }
        r0 = (android.telephony.TelephonyManager) r0;	 Catch:{ Exception -> 0x009f }
        r1 = 1;
        if (r8 != 0) goto L_0x0019;
    L_0x000d:
        r2 = r0.getSimState();	 Catch:{ Exception -> 0x009f }
        if (r2 == r1) goto L_0x00a3;
    L_0x0013:
        r2 = r0.getPhoneType();	 Catch:{ Exception -> 0x009f }
        if (r2 == 0) goto L_0x00a3;
    L_0x0019:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x009f }
        r3 = 23;
        r4 = 0;
        if (r2 < r3) goto L_0x002f;
    L_0x0020:
        r2 = r7.getParentActivity();	 Catch:{ Exception -> 0x009f }
        r3 = "android.permission.READ_PHONE_STATE";
        r2 = r2.checkSelfPermission(r3);	 Catch:{ Exception -> 0x009f }
        if (r2 != 0) goto L_0x002d;
    L_0x002c:
        goto L_0x002f;
    L_0x002d:
        r2 = 0;
        goto L_0x0030;
    L_0x002f:
        r2 = 1;
    L_0x0030:
        if (r8 != 0) goto L_0x0034;
    L_0x0032:
        if (r2 == 0) goto L_0x00a3;
    L_0x0034:
        if (r8 != 0) goto L_0x003e;
    L_0x0036:
        r8 = r0.getLine1Number();	 Catch:{ Exception -> 0x009f }
        r8 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r8);	 Catch:{ Exception -> 0x009f }
    L_0x003e:
        r0 = 0;
        r2 = android.text.TextUtils.isEmpty(r8);	 Catch:{ Exception -> 0x009f }
        if (r2 != 0) goto L_0x00a3;
    L_0x0045:
        r2 = r8.length();	 Catch:{ Exception -> 0x009f }
        r3 = 4;
        if (r2 <= r3) goto L_0x0084;
    L_0x004c:
        r2 = 8;
        if (r3 < r1) goto L_0x006f;
    L_0x0050:
        r5 = r8.substring(r4, r3);	 Catch:{ Exception -> 0x009f }
        r6 = r7.codesMap;	 Catch:{ Exception -> 0x009f }
        r6 = r6.get(r5);	 Catch:{ Exception -> 0x009f }
        r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x009f }
        if (r6 == 0) goto L_0x006c;
    L_0x005e:
        r0 = r8.substring(r3);	 Catch:{ Exception -> 0x009f }
        r3 = r7.inputFields;	 Catch:{ Exception -> 0x009f }
        r3 = r3[r2];	 Catch:{ Exception -> 0x009f }
        r3.setText(r5);	 Catch:{ Exception -> 0x009f }
        r3 = r0;
        r0 = 1;
        goto L_0x0071;
    L_0x006c:
        r3 = r3 + -1;
        goto L_0x004c;
    L_0x006f:
        r3 = r0;
        r0 = 0;
    L_0x0071:
        if (r0 != 0) goto L_0x0083;
    L_0x0073:
        r0 = r8.substring(r1);	 Catch:{ Exception -> 0x009f }
        r3 = r7.inputFields;	 Catch:{ Exception -> 0x009f }
        r2 = r3[r2];	 Catch:{ Exception -> 0x009f }
        r8 = r8.substring(r4, r1);	 Catch:{ Exception -> 0x009f }
        r2.setText(r8);	 Catch:{ Exception -> 0x009f }
        goto L_0x0084;
    L_0x0083:
        r0 = r3;
    L_0x0084:
        if (r0 == 0) goto L_0x00a3;
    L_0x0086:
        r8 = r7.inputFields;	 Catch:{ Exception -> 0x009f }
        r1 = 9;
        r8 = r8[r1];	 Catch:{ Exception -> 0x009f }
        r8.setText(r0);	 Catch:{ Exception -> 0x009f }
        r8 = r7.inputFields;	 Catch:{ Exception -> 0x009f }
        r8 = r8[r1];	 Catch:{ Exception -> 0x009f }
        r0 = r7.inputFields;	 Catch:{ Exception -> 0x009f }
        r0 = r0[r1];	 Catch:{ Exception -> 0x009f }
        r0 = r0.length();	 Catch:{ Exception -> 0x009f }
        r8.setSelection(r0);	 Catch:{ Exception -> 0x009f }
        goto L_0x00a3;
    L_0x009f:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x00a3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.fillNumber(java.lang.String):void");
    }

    private void sendSavePassword(boolean z) {
        if (z || this.codeFieldCell.getVisibility() != 0) {
            String str;
            String str2;
            TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TL_account_updatePasswordSettings();
            String str3 = "";
            if (z) {
                this.doneItem.setVisibility(0);
                tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
                TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.flags = 2;
                tL_account_passwordInputSettings.email = str3;
                tL_account_updatePasswordSettings.password = new TL_inputCheckPasswordEmpty();
                str = null;
                str2 = str;
            } else {
                String obj = this.inputFields[0].getText().toString();
                if (TextUtils.isEmpty(obj)) {
                    shakeField(0);
                    return;
                } else if (obj.equals(this.inputFields[1].getText().toString())) {
                    String obj2 = this.inputFields[2].getText().toString();
                    if (obj2.length() < 3) {
                        shakeField(2);
                        return;
                    }
                    int lastIndexOf = obj2.lastIndexOf(46);
                    int lastIndexOf2 = obj2.lastIndexOf(64);
                    if (lastIndexOf2 < 0 || lastIndexOf < lastIndexOf2) {
                        shakeField(2);
                        return;
                    }
                    tL_account_updatePasswordSettings.password = new TL_inputCheckPasswordEmpty();
                    tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
                    TL_account_passwordInputSettings tL_account_passwordInputSettings2 = tL_account_updatePasswordSettings.new_settings;
                    tL_account_passwordInputSettings2.flags |= 1;
                    tL_account_passwordInputSettings2.hint = str3;
                    tL_account_passwordInputSettings2.new_algo = this.currentPassword.new_algo;
                    if (obj2.length() > 0) {
                        TL_account_passwordInputSettings tL_account_passwordInputSettings3 = tL_account_updatePasswordSettings.new_settings;
                        tL_account_passwordInputSettings3.flags = 2 | tL_account_passwordInputSettings3.flags;
                        tL_account_passwordInputSettings3.email = obj2.trim();
                    }
                    str2 = obj;
                    str = obj2;
                } else {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    shakeField(1);
                    return;
                }
            }
            showEditDoneProgress(true, true);
            Utilities.globalQueue.postRunnable(new -$$Lambda$PaymentFormActivity$v6Le3V4A5uKjdxXgwOCsgIj66wA(this, z, str, str2, tL_account_updatePasswordSettings));
        } else {
            String text = this.codeFieldCell.getText();
            if (text.length() == 0) {
                shakeView(this.codeFieldCell);
                return;
            }
            showEditDoneProgress(true, true);
            TL_account_confirmPasswordEmail tL_account_confirmPasswordEmail = new TL_account_confirmPasswordEmail();
            tL_account_confirmPasswordEmail.code = text;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_confirmPasswordEmail, new -$$Lambda$PaymentFormActivity$YjpXWVCXfyYjoMEGmUOokMuT9II(this), 10);
        }
    }

    public /* synthetic */ void lambda$sendSavePassword$27$PaymentFormActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$rp_a840DeP90rN9bDu7luNsoJKE(this, tL_error));
    }

    public /* synthetic */ void lambda$null$26$PaymentFormActivity(TL_error tL_error) {
        showEditDoneProgress(true, false);
        if (tL_error == null) {
            if (getParentActivity() != null) {
                Runnable runnable = this.shortPollRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.shortPollRunnable = null;
                }
                goToNextStep();
            }
        } else if (tL_error.text.startsWith("CODE_INVALID")) {
            shakeView(this.codeFieldCell);
            this.codeFieldCell.setText("", false);
        } else {
            String str = "AppName";
            if (tL_error.text.startsWith("FLOOD_WAIT")) {
                String formatPluralString;
                int intValue = Utilities.parseInt(tL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
            } else {
                showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
            }
        }
    }

    public /* synthetic */ void lambda$null$32$PaymentFormActivity(boolean z, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$sxgdxN6G7g4EkMcZxwoY42jxlj4(this, tL_error, z, tLObject, str));
    }

    public /* synthetic */ void lambda$sendSavePassword$33$PaymentFormActivity(boolean z, String str, String str2, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        -$$Lambda$PaymentFormActivity$X_NgFbW2mNzLbpFBedySWqq2_bA -__lambda_paymentformactivity_x_ngfbw2mnzlbpfbedyswqq2_ba = new -$$Lambda$PaymentFormActivity$X_NgFbW2mNzLbpFBedySWqq2_bA(this, z, str);
        if (z) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, -__lambda_paymentformactivity_x_ngfbw2mnzlbpfbedyswqq2_ba, 10);
            return;
        }
        byte[] stringBytes = AndroidUtilities.getStringBytes(str2);
        PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.new_algo;
        TL_error tL_error;
        if (passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo;
            tL_account_updatePasswordSettings.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow);
            if (tL_account_updatePasswordSettings.new_settings.new_password_hash == null) {
                tL_error = new TL_error();
                tL_error.text = "ALGO_INVALID";
                -__lambda_paymentformactivity_x_ngfbw2mnzlbpfbedyswqq2_ba.run(null, tL_error);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, -__lambda_paymentformactivity_x_ngfbw2mnzlbpfbedyswqq2_ba, 10);
            return;
        }
        tL_error = new TL_error();
        tL_error.text = "PASSWORD_HASH_INVALID";
        -__lambda_paymentformactivity_x_ngfbw2mnzlbpfbedyswqq2_ba.run(null, tL_error);
    }

    public /* synthetic */ void lambda$null$31$PaymentFormActivity(TL_error tL_error, boolean z, TLObject tLObject, String str) {
        if (tL_error != null) {
            if ("SRP_ID_INVALID".equals(tL_error.text)) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new -$$Lambda$PaymentFormActivity$tcLMevZOApYaPGxte7o40Zbxk2c(this, z), 8);
                return;
            }
        }
        showEditDoneProgress(true, false);
        if (z) {
            TL_account_password tL_account_password = this.currentPassword;
            tL_account_password.has_password = false;
            tL_account_password.current_algo = null;
            this.delegate.currentPasswordUpdated(tL_account_password);
            finishFragment();
        } else if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
            if (getParentActivity() != null) {
                goToNextStep();
            }
        } else if (tL_error != null) {
            if (tL_error.text.equals("EMAIL_UNCONFIRMED") || tL_error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                this.emailCodeLength = Utilities.parseInt(tL_error.text).intValue();
                Builder builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PaymentFormActivity$9D-zlo6Zt3tzxdsZk8Ai17yN6Vs(this, str));
                builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                Dialog showDialog = showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                }
            } else {
                str = "AppName";
                if (tL_error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString(str, NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                    String formatPluralString;
                    int intValue = Utilities.parseInt(tL_error.text).intValue();
                    if (intValue < 60) {
                        formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
                } else {
                    showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$29$PaymentFormActivity(boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$O97PWkThOP8HBp4dlwmRsHFZ4Cg(this, tL_error, tLObject, z));
    }

    public /* synthetic */ void lambda$null$28$PaymentFormActivity(TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            this.currentPassword = (TL_account_password) tLObject;
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            sendSavePassword(z);
        }
    }

    public /* synthetic */ void lambda$null$30$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
        this.waitingForEmail = true;
        this.currentPassword.email_unconfirmed_pattern = str;
        updatePasswordFields();
    }

    private boolean sendCardData() {
        Integer parseInt;
        Integer num;
        String[] split = this.inputFields[1].getText().toString().split("/");
        if (split.length == 2) {
            Integer parseInt2 = Utilities.parseInt(split[0]);
            parseInt = Utilities.parseInt(split[1]);
            num = parseInt2;
        } else {
            num = null;
            parseInt = num;
        }
        Card card = new Card(this.inputFields[0].getText().toString(), num, parseInt, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), null, null, null, null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), null);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(card.getType());
        stringBuilder.append(" *");
        stringBuilder.append(card.getLast4());
        this.cardName = stringBuilder.toString();
        if (!card.validateNumber()) {
            shakeField(0);
            return false;
        } else if (!card.validateExpMonth() || !card.validateExpYear() || !card.validateExpiryDate()) {
            shakeField(1);
            return false;
        } else if (this.need_card_name && this.inputFields[2].length() == 0) {
            shakeField(2);
            return false;
        } else if (!card.validateCVC()) {
            shakeField(3);
            return false;
        } else if (this.need_card_country && this.inputFields[4].length() == 0) {
            shakeField(4);
            return false;
        } else if (this.need_card_postcode && this.inputFields[5].length() == 0) {
            shakeField(5);
            return false;
        } else {
            showEditDoneProgress(true, true);
            try {
                new Stripe(this.stripeApiKey).createToken(card, new TokenCallback() {
                    public void onSuccess(Token token) {
                        if (!PaymentFormActivity.this.canceled) {
                            PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$18$QrVnMuLxiMtm_DJ7v5npJu9dxvA(this));
                        }
                    }

                    public /* synthetic */ void lambda$onSuccess$0$PaymentFormActivity$18() {
                        PaymentFormActivity.this.goToNextStep();
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        PaymentFormActivity.this.setDonePressed(false);
                    }

                    public void onError(Exception exception) {
                        if (!PaymentFormActivity.this.canceled) {
                            PaymentFormActivity.this.showEditDoneProgress(true, false);
                            PaymentFormActivity.this.setDonePressed(false);
                            if ((exception instanceof APIConnectionException) || (exception instanceof APIException)) {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", NUM));
                            } else {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, exception.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
            return true;
        }
    }

    private void sendForm() {
        if (!this.canceled) {
            TL_paymentRequestedInfo tL_paymentRequestedInfo;
            showEditDoneProgress(true, true);
            this.validateRequest = new TL_payments_validateRequestedInfo();
            TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = this.validateRequest;
            tL_payments_validateRequestedInfo.save = this.saveShippingInfo;
            tL_payments_validateRequestedInfo.msg_id = this.messageObject.getId();
            this.validateRequest.info = new TL_paymentRequestedInfo();
            if (this.paymentForm.invoice.name_requested) {
                this.validateRequest.info.name = this.inputFields[6].getText().toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 1;
            }
            if (this.paymentForm.invoice.phone_requested) {
                tL_paymentRequestedInfo = this.validateRequest.info;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(this.inputFields[8].getText().toString());
                stringBuilder.append(this.inputFields[9].getText().toString());
                tL_paymentRequestedInfo.phone = stringBuilder.toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 2;
            }
            if (this.paymentForm.invoice.email_requested) {
                this.validateRequest.info.email = this.inputFields[7].getText().toString().trim();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 4;
            }
            if (this.paymentForm.invoice.shipping_address_requested) {
                this.validateRequest.info.shipping_address = new TL_postAddress();
                this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
                this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
                this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
                this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
                TL_postAddress tL_postAddress = this.validateRequest.info.shipping_address;
                String str = this.countryName;
                if (str == null) {
                    str = "";
                }
                tL_postAddress.country_iso2 = str;
                this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
                TL_paymentRequestedInfo tL_paymentRequestedInfo2 = this.validateRequest.info;
                tL_paymentRequestedInfo2.flags |= 8;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new -$$Lambda$PaymentFormActivity$UHlY3SoIRXNL32kFvxEl3vmTkBw(this, this.validateRequest), 2);
        }
    }

    public /* synthetic */ void lambda$sendForm$37$PaymentFormActivity(TLObject tLObject, TLObject tLObject2, TL_error tL_error) {
        if (tLObject2 instanceof TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$7Bb2ya9NV9z4zmrHXo2U4Oeqe_A(this, tLObject2));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$dqb1yiZBfWsIMg61J_sdvcJToMY(this, tL_error, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$35$PaymentFormActivity(TLObject tLObject) {
        this.requestedInfo = (TL_payments_validatedRequestedInfo) tLObject;
        if (!(this.paymentForm.saved_info == null || this.saveShippingInfo)) {
            TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TL_payments_clearSavedInfo();
            tL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_clearSavedInfo, -$$Lambda$PaymentFormActivity$sO9JvHK-i_UoC8_6WynH4daVgdY.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    public /* synthetic */ void lambda$null$36$PaymentFormActivity(org.telegram.tgnet.TLRPC.TL_error r12, org.telegram.tgnet.TLObject r13) {
        /*
        r11 = this;
        r0 = 0;
        r11.setDonePressed(r0);
        r1 = 1;
        r11.showEditDoneProgress(r1, r0);
        if (r12 == 0) goto L_0x00a7;
    L_0x000a:
        r2 = r12.text;
        r3 = -1;
        r4 = r2.hashCode();
        r5 = 3;
        r6 = 5;
        r7 = 2;
        r8 = 4;
        r9 = 7;
        r10 = 6;
        switch(r4) {
            case -2092780146: goto L_0x006c;
            case -1623547228: goto L_0x0062;
            case -1224177757: goto L_0x0058;
            case -1031752045: goto L_0x004e;
            case -274035920: goto L_0x0044;
            case 417441502: goto L_0x003a;
            case 708423542: goto L_0x0030;
            case 863965605: goto L_0x0025;
            case 889106340: goto L_0x001b;
            default: goto L_0x001a;
        };
    L_0x001a:
        goto L_0x0076;
    L_0x001b:
        r4 = "REQ_INFO_EMAIL_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x0023:
        r2 = 2;
        goto L_0x0077;
    L_0x0025:
        r4 = "ADDRESS_STREET_LINE2_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x002d:
        r2 = 8;
        goto L_0x0077;
    L_0x0030:
        r4 = "REQ_INFO_PHONE_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x0038:
        r2 = 1;
        goto L_0x0077;
    L_0x003a:
        r4 = "ADDRESS_STATE_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x0042:
        r2 = 6;
        goto L_0x0077;
    L_0x0044:
        r4 = "ADDRESS_POSTCODE_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x004c:
        r2 = 5;
        goto L_0x0077;
    L_0x004e:
        r4 = "REQ_INFO_NAME_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x0056:
        r2 = 0;
        goto L_0x0077;
    L_0x0058:
        r4 = "ADDRESS_COUNTRY_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x0060:
        r2 = 3;
        goto L_0x0077;
    L_0x0062:
        r4 = "ADDRESS_STREET_LINE1_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x006a:
        r2 = 7;
        goto L_0x0077;
    L_0x006c:
        r4 = "ADDRESS_CITY_INVALID";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0076;
    L_0x0074:
        r2 = 4;
        goto L_0x0077;
    L_0x0076:
        r2 = -1;
    L_0x0077:
        switch(r2) {
            case 0: goto L_0x00a4;
            case 1: goto L_0x009e;
            case 2: goto L_0x009a;
            case 3: goto L_0x0096;
            case 4: goto L_0x0092;
            case 5: goto L_0x008e;
            case 6: goto L_0x008a;
            case 7: goto L_0x0086;
            case 8: goto L_0x0082;
            default: goto L_0x007a;
        };
    L_0x007a:
        r1 = r11.currentAccount;
        r0 = new java.lang.Object[r0];
        org.telegram.ui.Components.AlertsCreator.processError(r1, r12, r11, r13, r0);
        goto L_0x00a7;
    L_0x0082:
        r11.shakeField(r1);
        goto L_0x00a7;
    L_0x0086:
        r11.shakeField(r0);
        goto L_0x00a7;
    L_0x008a:
        r11.shakeField(r5);
        goto L_0x00a7;
    L_0x008e:
        r11.shakeField(r6);
        goto L_0x00a7;
    L_0x0092:
        r11.shakeField(r7);
        goto L_0x00a7;
    L_0x0096:
        r11.shakeField(r8);
        goto L_0x00a7;
    L_0x009a:
        r11.shakeField(r9);
        goto L_0x00a7;
    L_0x009e:
        r12 = 9;
        r11.shakeField(r12);
        goto L_0x00a7;
    L_0x00a4:
        r11.shakeField(r10);
    L_0x00a7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.lambda$null$36$PaymentFormActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject):void");
    }

    private TL_paymentRequestedInfo getRequestInfo() {
        TL_paymentRequestedInfo tL_paymentRequestedInfo = new TL_paymentRequestedInfo();
        if (this.paymentForm.invoice.name_requested) {
            tL_paymentRequestedInfo.name = this.inputFields[6].getText().toString();
            tL_paymentRequestedInfo.flags |= 1;
        }
        if (this.paymentForm.invoice.phone_requested) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(this.inputFields[8].getText().toString());
            stringBuilder.append(this.inputFields[9].getText().toString());
            tL_paymentRequestedInfo.phone = stringBuilder.toString();
            tL_paymentRequestedInfo.flags |= 2;
        }
        if (this.paymentForm.invoice.email_requested) {
            tL_paymentRequestedInfo.email = this.inputFields[7].getText().toString().trim();
            tL_paymentRequestedInfo.flags |= 4;
        }
        if (this.paymentForm.invoice.shipping_address_requested) {
            tL_paymentRequestedInfo.shipping_address = new TL_postAddress();
            tL_paymentRequestedInfo.shipping_address.street_line1 = this.inputFields[0].getText().toString();
            tL_paymentRequestedInfo.shipping_address.street_line2 = this.inputFields[1].getText().toString();
            tL_paymentRequestedInfo.shipping_address.city = this.inputFields[2].getText().toString();
            tL_paymentRequestedInfo.shipping_address.state = this.inputFields[3].getText().toString();
            TL_postAddress tL_postAddress = tL_paymentRequestedInfo.shipping_address;
            String str = this.countryName;
            if (str == null) {
                str = "";
            }
            tL_postAddress.country_iso2 = str;
            tL_paymentRequestedInfo.shipping_address.post_code = this.inputFields[5].getText().toString();
            tL_paymentRequestedInfo.flags |= 8;
        }
        return tL_paymentRequestedInfo;
    }

    private void sendData() {
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            TL_payments_sendPaymentForm tL_payments_sendPaymentForm = new TL_payments_sendPaymentForm();
            tL_payments_sendPaymentForm.msg_id = this.messageObject.getId();
            InputPaymentCredentials inputPaymentCredentials;
            if (UserConfig.getInstance(this.currentAccount).tmpPassword == null || this.paymentForm.saved_credentials == null) {
                TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay = this.androidPayCredentials;
                if (tL_inputPaymentCredentialsAndroidPay != null) {
                    tL_payments_sendPaymentForm.credentials = tL_inputPaymentCredentialsAndroidPay;
                } else {
                    tL_payments_sendPaymentForm.credentials = new TL_inputPaymentCredentials();
                    inputPaymentCredentials = tL_payments_sendPaymentForm.credentials;
                    inputPaymentCredentials.save = this.saveCardInfo;
                    inputPaymentCredentials.data = new TL_dataJSON();
                    tL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
                }
            } else {
                tL_payments_sendPaymentForm.credentials = new TL_inputPaymentCredentialsSaved();
                inputPaymentCredentials = tL_payments_sendPaymentForm.credentials;
                inputPaymentCredentials.id = this.paymentForm.saved_credentials.id;
                inputPaymentCredentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            }
            TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo = this.requestedInfo;
            if (tL_payments_validatedRequestedInfo != null) {
                String str = tL_payments_validatedRequestedInfo.id;
                if (str != null) {
                    tL_payments_sendPaymentForm.requested_info_id = str;
                    tL_payments_sendPaymentForm.flags = 1 | tL_payments_sendPaymentForm.flags;
                }
            }
            TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                tL_payments_sendPaymentForm.shipping_option_id = tL_shippingOption.id;
                tL_payments_sendPaymentForm.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendPaymentForm, new -$$Lambda$PaymentFormActivity$GlqRsRVH4sCdpkPSqoUCnb5fPtQ(this, tL_payments_sendPaymentForm), 2);
        }
    }

    public /* synthetic */ void lambda$sendData$40$PaymentFormActivity(TL_payments_sendPaymentForm tL_payments_sendPaymentForm, TLObject tLObject, TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$K6xCsGeMxqWhMaKUIG_1aUlRg70(this, tL_error, tL_payments_sendPaymentForm));
        } else if (tLObject instanceof TL_payments_paymentResult) {
            MessagesController.getInstance(this.currentAccount).processUpdates(((TL_payments_paymentResult) tLObject).updates, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$bzw4OVkpOzFWzvKyIdhX50nRu7A(this));
        } else if (tLObject instanceof TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$KLyhSsRtirfczI3lfwAKAEvcTeg(this, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$38$PaymentFormActivity(TLObject tLObject) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
        setDonePressed(false);
        this.webView.setVisibility(0);
        this.webviewLoading = true;
        showEditDoneProgress(true, true);
        this.progressView.setVisibility(0);
        this.doneItem.setEnabled(false);
        this.doneItem.getContentView().setVisibility(4);
        WebView webView = this.webView;
        String str = ((TL_payments_paymentVerificationNeeded) tLObject).url;
        this.webViewUrl = str;
        webView.loadUrl(str);
    }

    public /* synthetic */ void lambda$null$39$PaymentFormActivity(TL_error tL_error, TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_payments_sendPaymentForm, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
    }

    private void shakeField(int i) {
        shakeView(this.inputFields[i]);
    }

    private void shakeView(View view) {
        Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(view, 2.0f, 0);
    }

    private void setDonePressed(boolean z) {
        this.donePressed = z;
        this.swipeBackEnabled = z ^ 1;
        this.actionBar.getBackButton().setEnabled(this.donePressed ^ 1);
        TextDetailSettingsCell[] textDetailSettingsCellArr = this.detailSettingsCell;
        if (textDetailSettingsCellArr[0] != null) {
            textDetailSettingsCellArr[0].setEnabled(this.donePressed ^ 1);
        }
    }

    private void checkPassword() {
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
            UserConfig.getInstance(this.currentAccount).tmpPassword = null;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
            sendData();
        } else if (this.inputFields[1].length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
        } else {
            String obj = this.inputFields[1].getText().toString();
            showEditDoneProgress(true, true);
            setDonePressed(true);
            TL_account_getPassword tL_account_getPassword = new TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPassword, new -$$Lambda$PaymentFormActivity$tY62nItwlzRcBMKPnqAwPnsARG0(this, obj, tL_account_getPassword), 2);
        }
    }

    public /* synthetic */ void lambda$checkPassword$45$PaymentFormActivity(String str, TL_account_getPassword tL_account_getPassword, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$TcVVqZdUtRanjkIhF2Xby-5O01Q(this, tL_error, tLObject, str, tL_account_getPassword));
    }

    public /* synthetic */ void lambda$null$44$PaymentFormActivity(TL_error tL_error, TLObject tLObject, String str, TL_account_getPassword tL_account_getPassword) {
        if (tL_error == null) {
            TL_account_password tL_account_password = (TL_account_password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (tL_account_password.has_password) {
                Utilities.globalQueue.postRunnable(new -$$Lambda$PaymentFormActivity$k9SEAszMH3-nDlyEt9fLhgGlMAA(this, tL_account_password, AndroidUtilities.getStringBytes(str)));
            } else {
                this.passwordOk = false;
                goToNextStep();
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_getPassword, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    public /* synthetic */ void lambda$null$43$PaymentFormActivity(TL_account_password tL_account_password, byte[] bArr) {
        PasswordKdfAlgo passwordKdfAlgo = tL_account_password.current_algo;
        bArr = passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo) : null;
        TL_account_getTmpPassword tL_account_getTmpPassword = new TL_account_getTmpPassword();
        tL_account_getTmpPassword.period = 1800;
        -$$Lambda$PaymentFormActivity$uhyZXSlwS_E2QJObfK5gOziOlac -__lambda_paymentformactivity_uhyzxslws_e2qjobfk5goziolac = new -$$Lambda$PaymentFormActivity$uhyZXSlwS_E2QJObfK5gOziOlac(this, tL_account_getTmpPassword);
        PasswordKdfAlgo passwordKdfAlgo2 = tL_account_password.current_algo;
        TL_error tL_error;
        if (passwordKdfAlgo2 instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            tL_account_getTmpPassword.password = SRPHelper.startCheck(bArr, tL_account_password.srp_id, tL_account_password.srp_B, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo2);
            if (tL_account_getTmpPassword.password == null) {
                tL_error = new TL_error();
                tL_error.text = "ALGO_INVALID";
                -__lambda_paymentformactivity_uhyzxslws_e2qjobfk5goziolac.run(null, tL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getTmpPassword, -__lambda_paymentformactivity_uhyzxslws_e2qjobfk5goziolac, 10);
        } else {
            tL_error = new TL_error();
            tL_error.text = "PASSWORD_HASH_INVALID";
            -__lambda_paymentformactivity_uhyzxslws_e2qjobfk5goziolac.run(null, tL_error);
        }
    }

    public /* synthetic */ void lambda$null$42$PaymentFormActivity(TL_account_getTmpPassword tL_account_getTmpPassword, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PaymentFormActivity$WEnYGXUuB-UBrzR-7FlX-BgJs2A(this, tLObject, tL_error, tL_account_getTmpPassword));
    }

    public /* synthetic */ void lambda$null$41$PaymentFormActivity(TLObject tLObject, TL_error tL_error, TL_account_getTmpPassword tL_account_getTmpPassword) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (tLObject != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TL_account_tmpPassword) tLObject;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            goToNextStep();
        } else if (tL_error.text.equals("PASSWORD_HASH_INVALID")) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
            this.inputFields[1].setText("");
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_getTmpPassword, new Object[0]);
        }
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z2;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        String str = "alpha";
        String str2 = "scaleY";
        String str3 = "scaleX";
        AnimatorSet animatorSet2;
        Animator[] animatorArr;
        if (z && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                animatorSet2 = this.doneItemAnimation;
                Animator[] animatorArr2 = new Animator[6];
                animatorArr2[0] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str3, new float[]{0.1f});
                animatorArr2[1] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str2, new float[]{0.1f});
                animatorArr2[2] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str, new float[]{0.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{1.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr2);
            } else if (this.webView != null) {
                animatorSet2 = this.doneItemAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{0.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                animatorSet2 = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        if (z3) {
                            PaymentFormActivity.this.doneItem.getContentView().setVisibility(4);
                        } else {
                            PaymentFormActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (this.payTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                animatorSet2 = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.payTextView, str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.payTextView, str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.payTextView, str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressViewButton, str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressViewButton, str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressViewButton, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                animatorSet2 = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressViewButton, str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressViewButton, str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressViewButton, str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.payTextView, str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.payTextView, str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.payTextView, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        if (z3) {
                            PaymentFormActivity.this.payTextView.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.progressViewButton.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public boolean onBackPressed() {
        if (!this.shouldNavigateBack) {
            return this.donePressed ^ 1;
        }
        this.webView.loadUrl(this.webViewUrl);
        this.shouldNavigateBack = false;
        return false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        int i;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, "contextProgressOuter2"));
        if (this.inputFields != null) {
            i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                i++;
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        }
        String str = "radioButton";
        String str2 = "textView";
        if (this.radioCells != null) {
            i = 0;
            while (true) {
                RadioCell[] radioCellArr = this.radioCells;
                if (i >= radioCellArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(radioCellArr[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(this.radioCells[i], 0, new Class[]{RadioCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{str}, null, null, null, "radioBackground"));
                arrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{str}, null, null, null, "radioBackgroundChecked"));
                i++;
            }
        } else {
            arrayList.add(new ThemeDescription(null, 0, new Class[]{RadioCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{str}, null, null, null, "radioBackground"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{str}, null, null, null, "radioBackgroundChecked"));
        }
        i = 0;
        while (true) {
            HeaderCell[] headerCellArr = this.headerCell;
            if (i >= headerCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(headerCellArr[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.headerCell[i], 0, new Class[]{HeaderCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlueHeader"));
            i++;
        }
        i = 0;
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (i >= shadowSectionCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(shadowSectionCellArr[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            i++;
        }
        i = 0;
        while (true) {
            TextInfoPrivacyCell[] textInfoPrivacyCellArr = this.bottomCell;
            if (i >= textInfoPrivacyCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textInfoPrivacyCellArr[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.bottomCell[i], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription(this.bottomCell[i], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteLinkText"));
            i++;
        }
        for (i = 0; i < this.dividers.size(); i++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        }
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        View view = this.codeFieldCell;
        View view2 = view;
        arrayList.add(new ThemeDescription(view2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
        view = this.checkCell1;
        Class[] clsArr = new Class[]{TextCheckCell.class};
        String[] strArr = new String[1];
        strArr[0] = "checkBox";
        arrayList.add(new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        i = 0;
        while (true) {
            TextSettingsCell[] textSettingsCellArr = this.settingsCell;
            if (i >= textSettingsCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textSettingsCellArr[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.settingsCell[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.settingsCell[i], 0, new Class[]{TextSettingsCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
            i++;
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText6"));
        view = this.linearLayout2;
        view2 = view;
        arrayList.add(new ThemeDescription(view2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
        view = this.linearLayout2;
        int i2 = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{TextPriceCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        arrayList.add(new ThemeDescription(view, i2, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteGrayText2"));
        view = this.linearLayout2;
        View view3 = view;
        arrayList.add(new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        i = 1;
        while (true) {
            TextDetailSettingsCell[] textDetailSettingsCellArr = this.detailSettingsCell;
            if (i < textDetailSettingsCellArr.length) {
                arrayList.add(new ThemeDescription(textDetailSettingsCellArr[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.detailSettingsCell[i], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.detailSettingsCell[i], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{r7}, null, null, null, "windowBackgroundWhiteGrayText2"));
                i++;
            } else {
                arrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
                arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
        }
    }
}
