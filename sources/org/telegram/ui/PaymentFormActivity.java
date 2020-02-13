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
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
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
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.PaymentFormActivity;

public class PaymentFormActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
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
    /* access modifiers changed from: private */
    public TLRPC.TL_inputPaymentCredentialsAndroidPay androidPayCredentials;
    private String androidPayPublicKey;
    private TLRPC.User botUser;
    private TextInfoPrivacyCell[] bottomCell;
    private FrameLayout bottomLayout;
    /* access modifiers changed from: private */
    public boolean canceled;
    /* access modifiers changed from: private */
    public String cardName;
    private TextCheckCell checkCell1;
    private EditTextSettingsCell codeFieldCell;
    /* access modifiers changed from: private */
    public HashMap<String, String> codesMap;
    /* access modifiers changed from: private */
    public ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private String countryName;
    private String currentBotName;
    private String currentItemName;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    /* access modifiers changed from: private */
    public int currentStep;
    /* access modifiers changed from: private */
    public PaymentFormActivityDelegate delegate;
    /* access modifiers changed from: private */
    public TextDetailSettingsCell[] detailSettingsCell;
    private ArrayList<View> dividers;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public boolean donePressed;
    /* access modifiers changed from: private */
    public int emailCodeLength;
    private GoogleApiClient googleApiClient;
    private HeaderCell[] headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnCardChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnPhoneChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputFields;
    /* access modifiers changed from: private */
    public boolean isWebView;
    private LinearLayout linearLayout2;
    private boolean loadingPasswordInfo;
    private MessageObject messageObject;
    private boolean need_card_country;
    /* access modifiers changed from: private */
    public boolean need_card_name;
    private boolean need_card_postcode;
    /* access modifiers changed from: private */
    public PaymentFormActivity passwordFragment;
    private boolean passwordOk;
    /* access modifiers changed from: private */
    public TextView payTextView;
    /* access modifiers changed from: private */
    public TLRPC.TL_payments_paymentForm paymentForm;
    private PaymentInfoCell paymentInfoCell;
    /* access modifiers changed from: private */
    public String paymentJson;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public ContextProgressView progressViewButton;
    /* access modifiers changed from: private */
    public RadioCell[] radioCells;
    /* access modifiers changed from: private */
    public TLRPC.TL_payments_validatedRequestedInfo requestedInfo;
    /* access modifiers changed from: private */
    public boolean saveCardInfo;
    private boolean saveShippingInfo;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell[] settingsCell;
    /* access modifiers changed from: private */
    public TLRPC.TL_shippingOption shippingOption;
    private Runnable shortPollRunnable;
    /* access modifiers changed from: private */
    public boolean shouldNavigateBack;
    private String stripeApiKey;
    private boolean swipeBackEnabled;
    private TextView textView;
    private String totalPriceDecimal;
    private TLRPC.TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    /* access modifiers changed from: private */
    public String webViewUrl;
    /* access modifiers changed from: private */
    public boolean webviewLoading;

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    private void initAndroidPay(Context context) {
    }

    static /* synthetic */ boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$null$17(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$34(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new Runnable(str, str2) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.TelegramWebviewProxy.this.lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(String str, String str2) {
            if (PaymentFormActivity.this.getParentActivity() != null && str.equals("payment_form_submit")) {
                try {
                    JSONObject jSONObject = new JSONObject(str2);
                    String unused = PaymentFormActivity.this.paymentJson = jSONObject.getJSONObject("credentials").toString();
                    String unused2 = PaymentFormActivity.this.cardName = jSONObject.getString("title");
                } catch (Throwable th) {
                    String unused3 = PaymentFormActivity.this.paymentJson = str2;
                    FileLog.e(th);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }

        public void onClick(View view) {
            PaymentFormActivity.this.presentFragment(new TwoStepVerificationActivity(0));
        }
    }

    public PaymentFormActivity(MessageObject messageObject2, TLRPC.TL_payments_paymentReceipt tL_payments_paymentReceipt) {
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList<>();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        this.currentStep = 5;
        this.paymentForm = new TLRPC.TL_payments_paymentForm();
        TLRPC.TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
        tL_payments_paymentForm.bot_id = tL_payments_paymentReceipt.bot_id;
        tL_payments_paymentForm.invoice = tL_payments_paymentReceipt.invoice;
        tL_payments_paymentForm.provider_id = tL_payments_paymentReceipt.provider_id;
        tL_payments_paymentForm.users = tL_payments_paymentReceipt.users;
        this.shippingOption = tL_payments_paymentReceipt.shipping;
        this.messageObject = messageObject2;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_payments_paymentReceipt.bot_id));
        TLRPC.User user = this.botUser;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject2.messageOwner.media.title;
        if (tL_payments_paymentReceipt.info != null) {
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            this.validateRequest.info = tL_payments_paymentReceipt.info;
        }
        this.cardName = tL_payments_paymentReceipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject2) {
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList<>();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        TLRPC.TL_invoice tL_invoice = tL_payments_paymentForm.invoice;
        int i = 0;
        if (!tL_invoice.shipping_address_requested && !tL_invoice.email_requested && !tL_invoice.name_requested && !tL_invoice.phone_requested) {
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
        init(tL_payments_paymentForm, messageObject2, i, (TLRPC.TL_payments_validatedRequestedInfo) null, (TLRPC.TL_shippingOption) null, (String) null, (String) null, (TLRPC.TL_payments_validateRequestedInfo) null, false, (TLRPC.TL_inputPaymentCredentialsAndroidPay) null);
    }

    private PaymentFormActivity(TLRPC.TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject2, int i, TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption tL_shippingOption, String str, String str2, TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo, boolean z, TLRPC.TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.swipeBackEnabled = true;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList<>();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.settingsCell = new TextSettingsCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.emailCodeLength = 6;
        init(tL_payments_paymentForm, messageObject2, i, tL_payments_validatedRequestedInfo, tL_shippingOption, str, str2, tL_payments_validateRequestedInfo, z, tL_inputPaymentCredentialsAndroidPay);
    }

    private void setCurrentPassword(TLRPC.TL_account_password tL_account_password) {
        if (!tL_account_password.has_password) {
            this.currentPassword = tL_account_password;
            TLRPC.TL_account_password tL_account_password2 = this.currentPassword;
            if (tL_account_password2 != null) {
                this.waitingForEmail = !TextUtils.isEmpty(tL_account_password2.email_unconfirmed_pattern);
            }
            updatePasswordFields();
        } else if (getParentActivity() != null) {
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    private void init(TLRPC.TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject2, int i, TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption tL_shippingOption, String str, String str2, TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo, boolean z, TLRPC.TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
        this.currentStep = i;
        this.paymentJson = str;
        this.androidPayCredentials = tL_inputPaymentCredentialsAndroidPay;
        this.requestedInfo = tL_payments_validatedRequestedInfo;
        this.paymentForm = tL_payments_paymentForm;
        this.shippingOption = tL_shippingOption;
        this.messageObject = messageObject2;
        this.saveCardInfo = z;
        boolean z2 = true;
        this.isWebView = !"stripe".equals(this.paymentForm.native_provider);
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_payments_paymentForm.bot_id));
        TLRPC.User user = this.botUser;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject2.messageOwner.media.title;
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
            TLRPC.TL_paymentSavedCredentialsCard tL_paymentSavedCredentialsCard = tL_payments_paymentForm.saved_credentials;
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
        if (Build.VERSION.SDK_INT >= 23) {
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
        GoogleApiClient googleApiClient2 = this.googleApiClient;
        if (googleApiClient2 != null) {
            googleApiClient2.connect();
        }
    }

    public void onPause() {
        GoogleApiClient googleApiClient2 = this.googleApiClient;
        if (googleApiClient2 != null) {
            googleApiClient2.disconnect();
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(11:267|268|269|(3:270|271|(1:273))|278|(2:279|280)|285|286|287|288|289) */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0f9c, code lost:
        if (r6.email_requested == false) goto L_0x0f8d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0324, code lost:
        if (r7.email_requested == false) goto L_0x0315;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:288:0x097c */
    /* JADX WARNING: Missing exception handler attribute for start block: B:311:0x0abf */
    /* JADX WARNING: Missing exception handler attribute for start block: B:316:0x0aca */
    /* JADX WARNING: Missing exception handler attribute for start block: B:321:0x0ad5 */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x1315  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x13c7  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x13ef  */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x141b  */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x141d  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x1448  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x147f  */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r29) {
        /*
            r28 = this;
            r1 = r28
            r2 = r29
            int r0 = r1.currentStep
            r3 = 6
            r4 = 5
            r5 = 4
            r6 = 3
            r7 = 2
            r8 = 1
            if (r0 != 0) goto L_0x001e
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626184(0x7f0e08c8, float:1.8879597E38)
            java.lang.String r10 = "PaymentShippingInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x001e:
            if (r0 != r8) goto L_0x0030
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626185(0x7f0e08c9, float:1.88796E38)
            java.lang.String r10 = "PaymentShippingMethod"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x0030:
            if (r0 != r7) goto L_0x0042
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626144(0x7f0e08a0, float:1.8879516E38)
            java.lang.String r10 = "PaymentCardInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x0042:
            if (r0 != r6) goto L_0x0054
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626144(0x7f0e08a0, float:1.8879516E38)
            java.lang.String r10 = "PaymentCardInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x0054:
            if (r0 != r5) goto L_0x008d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.test
            if (r0 == 0) goto L_0x007e
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Test "
            r9.append(r10)
            r10 = 2131626151(0x7f0e08a7, float:1.887953E38)
            java.lang.String r11 = "PaymentCheckout"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626151(0x7f0e08a7, float:1.887953E38)
            java.lang.String r10 = "PaymentCheckout"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x008d:
            if (r0 != r4) goto L_0x00c6
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.test
            if (r0 == 0) goto L_0x00b7
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Test "
            r9.append(r10)
            r10 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            java.lang.String r11 = "PaymentReceipt"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x00b7:
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            java.lang.String r10 = "PaymentReceipt"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x00c6:
            if (r0 != r3) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626166(0x7f0e08b6, float:1.887956E38)
            java.lang.String r10 = "PaymentPassword"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
        L_0x00d6:
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131165430(0x7var_f6, float:1.7945077E38)
            r0.setBackButtonImage(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r0.setAllowOverlayTitle(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            org.telegram.ui.PaymentFormActivity$1 r9 = new org.telegram.ui.PaymentFormActivity$1
            r9.<init>()
            r0.setActionBarMenuOnItemClick(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            int r9 = r1.currentStep
            r10 = -1
            if (r9 == 0) goto L_0x0102
            if (r9 == r8) goto L_0x0102
            if (r9 == r7) goto L_0x0102
            if (r9 == r6) goto L_0x0102
            if (r9 == r5) goto L_0x0102
            if (r9 != r3) goto L_0x013d
        L_0x0102:
            r9 = 2131165459(0x7var_, float:1.7945136E38)
            r11 = 1113587712(0x42600000, float:56.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItemWithWidth(r8, r9, r11)
            r1.doneItem = r0
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r2, r8)
            r1.progressView = r0
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressView
            r9 = 0
            r0.setAlpha(r9)
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressView
            r9 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r9)
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressView
            r0.setScaleY(r9)
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressView
            r0.setVisibility(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            org.telegram.ui.Components.ContextProgressView r9 = r1.progressView
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11)
            r0.addView(r9, r11)
        L_0x013d:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r2)
            r1.fragmentView = r0
            android.view.View r0 = r1.fragmentView
            r9 = r0
            android.widget.FrameLayout r9 = (android.widget.FrameLayout) r9
            java.lang.String r11 = "windowBackgroundGray"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r0.setBackgroundColor(r11)
            android.widget.ScrollView r0 = new android.widget.ScrollView
            r0.<init>(r2)
            r1.scrollView = r0
            android.widget.ScrollView r0 = r1.scrollView
            r0.setFillViewport(r8)
            android.widget.ScrollView r0 = r1.scrollView
            java.lang.String r11 = "actionBarDefault"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor((android.widget.ScrollView) r0, (int) r11)
            android.widget.ScrollView r0 = r1.scrollView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r14 = 0
            r15 = 0
            r16 = 0
            int r4 = r1.currentStep
            if (r4 != r5) goto L_0x017e
            r4 = 1111490560(0x42400000, float:48.0)
            r17 = 1111490560(0x42400000, float:48.0)
            goto L_0x0181
        L_0x017e:
            r4 = 0
            r17 = 0
        L_0x0181:
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r9.addView(r0, r4)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r2)
            r1.linearLayout2 = r0
            android.widget.LinearLayout r0 = r1.linearLayout2
            r0.setOrientation(r8)
            android.widget.ScrollView r0 = r1.scrollView
            android.widget.LinearLayout r4 = r1.linearLayout2
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            r12 = -2
            r11.<init>(r10, r12)
            r0.addView(r4, r11)
            int r0 = r1.currentStep
            java.lang.String r13 = "windowBackgroundWhiteBlackText"
            java.lang.String r14 = "windowBackgroundGrayShadow"
            java.lang.String r15 = "windowBackgroundWhite"
            r5 = 0
            if (r0 != 0) goto L_0x0942
            java.util.HashMap r9 = new java.util.HashMap
            r9.<init>()
            java.util.HashMap r11 = new java.util.HashMap
            r11.<init>()
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0216 }
            java.io.InputStreamReader r4 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0216 }
            android.content.res.Resources r18 = r29.getResources()     // Catch:{ Exception -> 0x0216 }
            android.content.res.AssetManager r3 = r18.getAssets()     // Catch:{ Exception -> 0x0216 }
            java.lang.String r10 = "countries.txt"
            java.io.InputStream r3 = r3.open(r10)     // Catch:{ Exception -> 0x0216 }
            r4.<init>(r3)     // Catch:{ Exception -> 0x0216 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x0216 }
        L_0x01d1:
            java.lang.String r3 = r0.readLine()     // Catch:{ Exception -> 0x0216 }
            if (r3 == 0) goto L_0x0212
            java.lang.String r4 = ";"
            java.lang.String[] r3 = r3.split(r4)     // Catch:{ Exception -> 0x0216 }
            java.util.ArrayList<java.lang.String> r4 = r1.countriesArray     // Catch:{ Exception -> 0x0216 }
            r10 = r3[r7]     // Catch:{ Exception -> 0x0216 }
            r4.add(r5, r10)     // Catch:{ Exception -> 0x0216 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.countriesMap     // Catch:{ Exception -> 0x0216 }
            r10 = r3[r7]     // Catch:{ Exception -> 0x0216 }
            r12 = r3[r5]     // Catch:{ Exception -> 0x0216 }
            r4.put(r10, r12)     // Catch:{ Exception -> 0x0216 }
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.codesMap     // Catch:{ Exception -> 0x0216 }
            r10 = r3[r5]     // Catch:{ Exception -> 0x0216 }
            r12 = r3[r7]     // Catch:{ Exception -> 0x0216 }
            r4.put(r10, r12)     // Catch:{ Exception -> 0x0216 }
            r4 = r3[r8]     // Catch:{ Exception -> 0x0216 }
            r10 = r3[r7]     // Catch:{ Exception -> 0x0216 }
            r11.put(r4, r10)     // Catch:{ Exception -> 0x0216 }
            int r4 = r3.length     // Catch:{ Exception -> 0x0216 }
            if (r4 <= r6) goto L_0x0209
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0216 }
            r10 = r3[r5]     // Catch:{ Exception -> 0x0216 }
            r12 = r3[r6]     // Catch:{ Exception -> 0x0216 }
            r4.put(r10, r12)     // Catch:{ Exception -> 0x0216 }
        L_0x0209:
            r4 = r3[r8]     // Catch:{ Exception -> 0x0216 }
            r3 = r3[r7]     // Catch:{ Exception -> 0x0216 }
            r9.put(r4, r3)     // Catch:{ Exception -> 0x0216 }
            r12 = -2
            goto L_0x01d1
        L_0x0212:
            r0.close()     // Catch:{ Exception -> 0x0216 }
            goto L_0x021a
        L_0x0216:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021a:
            java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
            org.telegram.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r3 = org.telegram.ui.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
            java.util.Collections.sort(r0, r3)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r1.inputFields = r0
            r0 = 0
        L_0x0228:
            r3 = 10
            if (r0 >= r3) goto L_0x0776
            if (r0 != 0) goto L_0x0265
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            r4 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.String r10 = "PaymentShippingAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r5]
            r10 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r10)
            r3.addView(r4, r7)
            r3 = 8
            r10 = -1
            goto L_0x02b7
        L_0x0265:
            r3 = 6
            r10 = -2
            r12 = -1
            if (r0 != r3) goto L_0x02b4
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r4 = new org.telegram.ui.Cells.ShadowSectionCell
            r4.<init>(r2)
            r3[r5] = r4
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r1.sectionCell
            r4 = r4[r5]
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r10)
            r3.addView(r4, r7)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            r4 = 2131626188(0x7f0e08cc, float:1.8879605E38)
            java.lang.String r7 = "PaymentShippingReceiver"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r8]
            r7 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7)
            r3.addView(r4, r12)
            goto L_0x02b5
        L_0x02b4:
            r10 = -1
        L_0x02b5:
            r3 = 8
        L_0x02b7:
            if (r0 != r3) goto L_0x02d5
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r2)
            r3.setOrientation(r5)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r7 = 50
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7)
            r4.addView(r3, r7)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            goto L_0x0345
        L_0x02d5:
            r3 = 9
            if (r0 != r3) goto L_0x02e6
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r4 = 8
            r3 = r3[r4]
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            goto L_0x0345
        L_0x02e6:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r7 = 50
            r10 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7)
            r4.addView(r3, r7)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            r4 = 5
            if (r0 == r4) goto L_0x0307
            r4 = 9
            if (r0 == r4) goto L_0x0307
            r4 = 1
            goto L_0x0308
        L_0x0307:
            r4 = 0
        L_0x0308:
            if (r4 == 0) goto L_0x0327
            r7 = 7
            if (r0 != r7) goto L_0x0317
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r7 = r7.invoice
            boolean r7 = r7.phone_requested
            if (r7 != 0) goto L_0x0317
        L_0x0315:
            r4 = 0
            goto L_0x0327
        L_0x0317:
            r7 = 6
            if (r0 != r7) goto L_0x0327
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r7 = r7.invoice
            boolean r10 = r7.phone_requested
            if (r10 != 0) goto L_0x0327
            boolean r7 = r7.email_requested
            if (r7 != 0) goto L_0x0327
            goto L_0x0315
        L_0x0327:
            if (r4 == 0) goto L_0x0345
            org.telegram.ui.PaymentFormActivity$2 r4 = new org.telegram.ui.PaymentFormActivity$2
            r4.<init>(r2)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r4.setBackgroundColor(r7)
            java.util.ArrayList<android.view.View> r7 = r1.dividers
            r7.add(r4)
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            r10 = 83
            r12 = -1
            r7.<init>(r12, r8, r10)
            r3.addView(r4, r7)
        L_0x0345:
            r4 = 9
            if (r0 != r4) goto L_0x0353
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            org.telegram.ui.Components.HintEditText r7 = new org.telegram.ui.Components.HintEditText
            r7.<init>(r2)
            r4[r0] = r7
            goto L_0x035c
        L_0x0353:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r7 = new org.telegram.ui.Components.EditTextBoldCursor
            r7.<init>(r2)
            r4[r0] = r7
        L_0x035c:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            r4.setTag(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r8, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.String r7 = "windowBackgroundWhiteHintText"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setHintTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 0
            r4.setBackgroundDrawable(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setCursorColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setCursorSize(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r7)
            r4 = 4
            if (r0 != r4) goto L_0x03c8
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$uDw-faglZkNLuTGLVBNZMQdnOLU r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$uDw-faglZkNLuTGLVBNZMQdnOLU
            r7.<init>()
            r4.setOnTouchListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.setInputType(r5)
        L_0x03c8:
            r4 = 9
            if (r0 == r4) goto L_0x03e6
            r4 = 8
            if (r0 != r4) goto L_0x03d1
            goto L_0x03e6
        L_0x03d1:
            r4 = 7
            if (r0 != r4) goto L_0x03dc
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.setInputType(r8)
            goto L_0x03ed
        L_0x03dc:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 16385(0x4001, float:2.296E-41)
            r4.setInputType(r7)
            goto L_0x03ed
        L_0x03e6:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.setInputType(r6)
        L_0x03ed:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r7)
            switch(r0) {
                case 0: goto L_0x050d;
                case 1: goto L_0x04e9;
                case 2: goto L_0x04c5;
                case 3: goto L_0x04a1;
                case 4: goto L_0x0467;
                case 5: goto L_0x0442;
                case 6: goto L_0x041f;
                case 7: goto L_0x03fc;
                default: goto L_0x03fa;
            }
        L_0x03fa:
            goto L_0x0530
        L_0x03fc:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626183(0x7f0e08c7, float:1.8879595E38)
            java.lang.String r10 = "PaymentShippingEmailPlaceholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            java.lang.String r4 = r4.email
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setText(r4)
            goto L_0x0530
        L_0x041f:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626186(0x7f0e08ca, float:1.8879601E38)
            java.lang.String r10 = "PaymentShippingName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            java.lang.String r4 = r4.name
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setText(r4)
            goto L_0x0530
        L_0x0442:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626192(0x7f0e08d0, float:1.8879613E38)
            java.lang.String r10 = "PaymentShippingZipPlaceholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r4 = r4.post_code
            r7.setText(r4)
            goto L_0x0530
        L_0x0467:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626182(0x7f0e08c6, float:1.8879593E38)
            java.lang.String r10 = "PaymentShippingCountry"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x0530
            java.lang.String r4 = r4.country_iso2
            java.lang.Object r4 = r11.get(r4)
            java.lang.String r4 = (java.lang.String) r4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            java.lang.String r7 = r7.country_iso2
            r1.countryName = r7
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            if (r4 == 0) goto L_0x049a
            goto L_0x049c
        L_0x049a:
            java.lang.String r4 = r1.countryName
        L_0x049c:
            r7.setText(r4)
            goto L_0x0530
        L_0x04a1:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626191(0x7f0e08cf, float:1.8879611E38)
            java.lang.String r10 = "PaymentShippingStatePlaceholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r4 = r4.state
            r7.setText(r4)
            goto L_0x0530
        L_0x04c5:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r10 = "PaymentShippingCityPlaceholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r4 = r4.city
            r7.setText(r4)
            goto L_0x0530
        L_0x04e9:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626180(0x7f0e08c4, float:1.8879589E38)
            java.lang.String r10 = "PaymentShippingAddress2Placeholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r4 = r4.street_line2
            r7.setText(r4)
            goto L_0x0530
        L_0x050d:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 2131626179(0x7f0e08c3, float:1.8879587E38)
            java.lang.String r10 = "PaymentShippingAddress1Placeholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r4.setHint(r7)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x0530
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r4 = r4.street_line1
            r7.setText(r4)
        L_0x0530:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r7 = r4[r0]
            r4 = r4[r0]
            int r4 = r4.length()
            r7.setSelection(r4)
            r4 = 8
            if (r0 != r4) goto L_0x05b9
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r2)
            r1.textView = r4
            android.widget.TextView r4 = r1.textView
            java.lang.String r7 = "+"
            r4.setText(r7)
            android.widget.TextView r4 = r1.textView
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setTextColor(r7)
            android.widget.TextView r4 = r1.textView
            r7 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r8, r7)
            android.widget.TextView r4 = r1.textView
            r20 = -2
            r21 = -2
            r22 = 1101529088(0x41a80000, float:21.0)
            r23 = 1094713344(0x41400000, float:12.0)
            r24 = 0
            r25 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25)
            r3.addView(r4, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 1092616192(0x41200000, float:10.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setPadding(r7, r5, r5, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 19
            r4.setGravity(r7)
            android.text.InputFilter[] r4 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r7 = new android.text.InputFilter$LengthFilter
            r10 = 5
            r7.<init>(r10)
            r4[r5] = r7
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setFilters(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r20 = 55
            r22 = 0
            r24 = 1101529088(0x41a80000, float:21.0)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25)
            r3.addView(r4, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$3 r4 = new org.telegram.ui.PaymentFormActivity$3
            r4.<init>()
            r3.addTextChangedListener(r4)
            goto L_0x0625
        L_0x05b9:
            r4 = 9
            if (r0 != r4) goto L_0x05f1
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.setPadding(r5, r5, r5, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 19
            r4.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r20 = -1
            r21 = -2
            r22 = 0
            r23 = 1094713344(0x41400000, float:12.0)
            r24 = 1101529088(0x41a80000, float:21.0)
            r25 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25)
            r3.addView(r4, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$4 r4 = new org.telegram.ui.PaymentFormActivity$4
            r4.<init>()
            r3.addTextChangedListener(r4)
            goto L_0x0625
        L_0x05f1:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setPadding(r5, r5, r5, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x0608
            r7 = 5
            goto L_0x0609
        L_0x0608:
            r7 = 3
        L_0x0609:
            r4.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r20 = -1
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 51
            r23 = 1101529088(0x41a80000, float:21.0)
            r24 = 1094713344(0x41400000, float:12.0)
            r25 = 1101529088(0x41a80000, float:21.0)
            r26 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3.addView(r4, r7)
        L_0x0625:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$waFpzGxIzLdyjPlQN2X3YYqAdDY r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$waFpzGxIzLdyjPlQN2X3YYqAdDY
            r4.<init>()
            r3.setOnEditorActionListener(r4)
            r3 = 9
            if (r0 != r3) goto L_0x0770
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            boolean r4 = r3.email_to_provider
            if (r4 != 0) goto L_0x065c
            boolean r3 = r3.phone_to_provider
            if (r3 == 0) goto L_0x0642
            goto L_0x065c
        L_0x0642:
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r4 = new org.telegram.ui.Cells.ShadowSectionCell
            r4.<init>(r2)
            r3[r8] = r4
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r1.sectionCell
            r4 = r4[r8]
            r7 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r7)
            r3.addView(r4, r12)
            goto L_0x0703
        L_0x065c:
            r3 = 0
            r4 = 0
        L_0x065e:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r7.users
            int r7 = r7.size()
            if (r3 >= r7) goto L_0x067e
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r7.users
            java.lang.Object r7 = r7.get(r3)
            org.telegram.tgnet.TLRPC$User r7 = (org.telegram.tgnet.TLRPC.User) r7
            int r10 = r7.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r12 = r1.paymentForm
            int r12 = r12.provider_id
            if (r10 != r12) goto L_0x067b
            r4 = r7
        L_0x067b:
            int r3 = r3 + 1
            goto L_0x065e
        L_0x067e:
            if (r4 == 0) goto L_0x0689
            java.lang.String r3 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r4)
            goto L_0x068b
        L_0x0689:
            java.lang.String r3 = ""
        L_0x068b:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r7.<init>(r2)
            r4[r8] = r7
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r7 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r7, (java.lang.String) r14)
            r4.setBackgroundDrawable(r10)
            android.widget.LinearLayout r4 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r8]
            r10 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r10)
            r4.addView(r7, r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r6 = r4.email_to_provider
            if (r6 == 0) goto L_0x06d2
            boolean r4 = r4.phone_to_provider
            if (r4 == 0) goto L_0x06d2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r6 = 2131626174(0x7f0e08be, float:1.8879577E38)
            java.lang.Object[] r7 = new java.lang.Object[r8]
            r7[r5] = r3
            java.lang.String r3 = "PaymentPhoneEmailToProvider"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r7)
            r4.setText(r3)
            goto L_0x0703
        L_0x06d2:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.email_to_provider
            if (r4 == 0) goto L_0x06ef
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r6 = 2131626162(0x7f0e08b2, float:1.8879552E38)
            java.lang.Object[] r7 = new java.lang.Object[r8]
            r7[r5] = r3
            java.lang.String r3 = "PaymentEmailToProvider"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r7)
            r4.setText(r3)
            goto L_0x0703
        L_0x06ef:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r6 = 2131626175(0x7f0e08bf, float:1.8879579E38)
            java.lang.Object[] r7 = new java.lang.Object[r8]
            r7[r5] = r3
            java.lang.String r3 = "PaymentPhoneToProvider"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r7)
            r4.setText(r3)
        L_0x0703:
            org.telegram.ui.Cells.TextCheckCell r3 = new org.telegram.ui.Cells.TextCheckCell
            r3.<init>(r2)
            r1.checkCell1 = r3
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r3.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            r4 = 2131626189(0x7f0e08cd, float:1.8879607E38)
            java.lang.String r6 = "PaymentShippingSave"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            boolean r6 = r1.saveShippingInfo
            r3.setTextAndCheck(r4, r6, r5)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r4 = r1.checkCell1
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r10)
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$ja6Gof-oEgJFLBPAYcrRraILPks r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$ja6Gof-oEgJFLBPAYcrRraILPks
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r5]
            r4 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r14)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r5]
            r4 = 2131626190(0x7f0e08ce, float:1.887961E38)
            java.lang.String r6 = "PaymentShippingSaveInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r5]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r10)
        L_0x0770:
            int r0 = r0 + 1
            r6 = 3
            r7 = 2
            goto L_0x0228
        L_0x0776:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x078f
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 6
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r2 = 8
            r0.setVisibility(r2)
            goto L_0x0791
        L_0x078f:
            r2 = 8
        L_0x0791:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x07a6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
        L_0x07a6:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x07be
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 7
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r2 = 8
            r0.setVisibility(r2)
        L_0x07be:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r2 = r0.phone_requested
            if (r2 == 0) goto L_0x07d3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 9
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x07fe
        L_0x07d3:
            boolean r2 = r0.email_requested
            if (r2 == 0) goto L_0x07e3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 7
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x07fe
        L_0x07e3:
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x07f3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 6
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x07fe
        L_0x07f3:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 5
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
        L_0x07fe:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r2 = r0[r8]
            if (r2 == 0) goto L_0x081f
            r0 = r0[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r3 = r2.name_requested
            if (r3 != 0) goto L_0x081a
            boolean r3 = r2.phone_requested
            if (r3 != 0) goto L_0x081a
            boolean r2 = r2.email_requested
            if (r2 == 0) goto L_0x0817
            goto L_0x081a
        L_0x0817:
            r2 = 8
            goto L_0x081b
        L_0x081a:
            r2 = 0
        L_0x081b:
            r0.setVisibility(r2)
            goto L_0x083f
        L_0x081f:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r2 = r0[r8]
            if (r2 == 0) goto L_0x083f
            r0 = r0[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r3 = r2.name_requested
            if (r3 != 0) goto L_0x083b
            boolean r3 = r2.phone_requested
            if (r3 != 0) goto L_0x083b
            boolean r2 = r2.email_requested
            if (r2 == 0) goto L_0x0838
            goto L_0x083b
        L_0x0838:
            r2 = 8
            goto L_0x083c
        L_0x083b:
            r2 = 0
        L_0x083c:
            r0.setVisibility(r2)
        L_0x083f:
            org.telegram.ui.Cells.HeaderCell[] r0 = r1.headerCell
            r0 = r0[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r3 = r2.name_requested
            if (r3 != 0) goto L_0x0857
            boolean r3 = r2.phone_requested
            if (r3 != 0) goto L_0x0857
            boolean r2 = r2.email_requested
            if (r2 == 0) goto L_0x0854
            goto L_0x0857
        L_0x0854:
            r2 = 8
            goto L_0x0858
        L_0x0857:
            r2 = 0
        L_0x0858:
            r0.setVisibility(r2)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x08c5
            org.telegram.ui.Cells.HeaderCell[] r0 = r1.headerCell
            r0 = r0[r5]
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r0 = r0[r5]
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r5]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r8]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 2
            r0 = r0[r3]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 3
            r0 = r0[r3]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 4
            r0 = r0[r3]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 5
            r0 = r0[r3]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
        L_0x08c5:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08dd
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08dd
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r1.fillNumber(r0)
            goto L_0x08e1
        L_0x08dd:
            r2 = 0
            r1.fillNumber(r2)
        L_0x08e1:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 8
            r0 = r0[r2]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x18c4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r0.invoice
            boolean r2 = r2.phone_requested
            if (r2 == 0) goto L_0x18c4
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x0901
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x18c4
        L_0x0901:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0917 }
            java.lang.String r2 = "phone"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x0917 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0917 }
            if (r0 == 0) goto L_0x091b
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0917 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x0917 }
            r11 = r0
            goto L_0x091c
        L_0x0917:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x091b:
            r11 = 0
        L_0x091c:
            if (r11 == 0) goto L_0x18c4
            java.lang.Object r0 = r9.get(r11)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x18c4
            java.util.ArrayList<java.lang.String> r2 = r1.countriesArray
            int r2 = r2.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x18c4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r1.inputFields
            r3 = 8
            r2 = r2[r3]
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r1.countriesMap
            java.lang.Object r0 = r3.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2.setText(r0)
            goto L_0x18c4
        L_0x0942:
            r3 = 2
            if (r0 != r3) goto L_0x0e80
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0983
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x097f }
            java.lang.String r0 = r0.data     // Catch:{ Exception -> 0x097f }
            r3.<init>(r0)     // Catch:{ Exception -> 0x097f }
            java.lang.String r0 = "android_pay_public_key"
            java.lang.String r0 = r3.getString(r0)     // Catch:{ Exception -> 0x0961 }
            boolean r4 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0961 }
            if (r4 != 0) goto L_0x0964
            r1.androidPayPublicKey = r0     // Catch:{ Exception -> 0x0961 }
            goto L_0x0964
        L_0x0961:
            r4 = 0
            r1.androidPayPublicKey = r4     // Catch:{ Exception -> 0x097f }
        L_0x0964:
            java.lang.String r0 = "android_pay_bgcolor"
            int r0 = r3.getInt(r0)     // Catch:{ Exception -> 0x0970 }
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = r0 | r4
            r1.androidPayBackgroundColor = r0     // Catch:{ Exception -> 0x0970 }
            goto L_0x0973
        L_0x0970:
            r4 = -1
            r1.androidPayBackgroundColor = r4     // Catch:{ Exception -> 0x097f }
        L_0x0973:
            java.lang.String r0 = "android_pay_inverse"
            boolean r0 = r3.getBoolean(r0)     // Catch:{ Exception -> 0x097c }
            r1.androidPayBlackTheme = r0     // Catch:{ Exception -> 0x097c }
            goto L_0x0983
        L_0x097c:
            r1.androidPayBlackTheme = r5     // Catch:{ Exception -> 0x097f }
            goto L_0x0983
        L_0x097f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0983:
            boolean r0 = r1.isWebView
            if (r0 == 0) goto L_0x0aa9
            java.lang.String r0 = r1.androidPayPublicKey
            if (r0 == 0) goto L_0x098e
            r28.initAndroidPay(r29)
        L_0x098e:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r2)
            r1.androidPayContainer = r0
            android.widget.FrameLayout r0 = r1.androidPayContainer
            r3 = 4000(0xfa0, float:5.605E-42)
            r0.setId(r3)
            android.widget.FrameLayout r0 = r1.androidPayContainer
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r3)
            android.widget.FrameLayout r0 = r1.androidPayContainer
            r3 = 8
            r0.setVisibility(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            android.widget.FrameLayout r3 = r1.androidPayContainer
            r4 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r4)
            r1.webviewLoading = r8
            r1.showEditDoneProgress(r8, r8)
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressView
            r0.setVisibility(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            r0.setEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            android.view.View r0 = r0.getContentView()
            r3 = 4
            r0.setVisibility(r3)
            org.telegram.ui.PaymentFormActivity$5 r0 = new org.telegram.ui.PaymentFormActivity$5
            r0.<init>(r2)
            r1.webView = r0
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setJavaScriptEnabled(r8)
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r8)
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r0 < r3) goto L_0x0a04
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setMixedContentMode(r5)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r3 = r1.webView
            r0.setAcceptThirdPartyCookies(r3, r8)
        L_0x0a04:
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 17
            if (r0 < r3) goto L_0x0a17
            android.webkit.WebView r0 = r1.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r3 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r4 = 0
            r3.<init>()
            java.lang.String r4 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r3, r4)
        L_0x0a17:
            android.webkit.WebView r0 = r1.webView
            org.telegram.ui.PaymentFormActivity$6 r3 = new org.telegram.ui.PaymentFormActivity$6
            r3.<init>()
            r0.setWebViewClient(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            android.webkit.WebView r3 = r1.webView
            r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r0.addView(r3, r4)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r2)
            r4 = 2
            r0[r4] = r3
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            r3 = r3[r4]
            r4 = -2
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
            r0.<init>(r2)
            r1.checkCell1 = r0
            org.telegram.ui.Cells.TextCheckCell r0 = r1.checkCell1
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextCheckCell r0 = r1.checkCell1
            r3 = 2131626147(0x7f0e08a3, float:1.8879522E38)
            java.lang.String r4 = "PaymentCardSavePaymentInformation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r1.saveCardInfo
            r0.setTextAndCheck(r3, r4, r5)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.TextCheckCell r0 = r1.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$55th3Bl6h3UNIH_JJ7d61ZMtWHE r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$55th3Bl6h3UNIH_JJ7d61ZMtWHE
            r3.<init>()
            r0.setOnClickListener(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r2)
            r0[r5] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r0 = r0[r5]
            r3 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r14)
            r0.setBackgroundDrawable(r2)
            r28.updateSavePaymentField()
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r1.bottomCell
            r2 = r2[r5]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
            goto L_0x18c4
        L_0x0aa9:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0ae9
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0ae5 }
            java.lang.String r0 = r0.data     // Catch:{ Exception -> 0x0ae5 }
            r3.<init>(r0)     // Catch:{ Exception -> 0x0ae5 }
            java.lang.String r0 = "need_country"
            boolean r0 = r3.getBoolean(r0)     // Catch:{ Exception -> 0x0abf }
            r1.need_card_country = r0     // Catch:{ Exception -> 0x0abf }
            goto L_0x0ac1
        L_0x0abf:
            r1.need_card_country = r5     // Catch:{ Exception -> 0x0ae5 }
        L_0x0ac1:
            java.lang.String r0 = "need_zip"
            boolean r0 = r3.getBoolean(r0)     // Catch:{ Exception -> 0x0aca }
            r1.need_card_postcode = r0     // Catch:{ Exception -> 0x0aca }
            goto L_0x0acc
        L_0x0aca:
            r1.need_card_postcode = r5     // Catch:{ Exception -> 0x0ae5 }
        L_0x0acc:
            java.lang.String r0 = "need_cardholder_name"
            boolean r0 = r3.getBoolean(r0)     // Catch:{ Exception -> 0x0ad5 }
            r1.need_card_name = r0     // Catch:{ Exception -> 0x0ad5 }
            goto L_0x0ad7
        L_0x0ad5:
            r1.need_card_name = r5     // Catch:{ Exception -> 0x0ae5 }
        L_0x0ad7:
            java.lang.String r0 = "publishable_key"
            java.lang.String r0 = r3.getString(r0)     // Catch:{ Exception -> 0x0ae0 }
            r1.stripeApiKey = r0     // Catch:{ Exception -> 0x0ae0 }
            goto L_0x0ae9
        L_0x0ae0:
            java.lang.String r0 = ""
            r1.stripeApiKey = r0     // Catch:{ Exception -> 0x0ae5 }
            goto L_0x0ae9
        L_0x0ae5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0ae9:
            r28.initAndroidPay(r29)
            r3 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x0af2:
            if (r0 >= r3) goto L_0x0e4a
            if (r0 != 0) goto L_0x0b2a
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            r4 = 2131626150(0x7f0e08a6, float:1.8879528E38)
            java.lang.String r6 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r5]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
            goto L_0x0b60
        L_0x0b2a:
            r3 = 4
            if (r0 != r3) goto L_0x0b60
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            r4 = 2131626141(0x7f0e089d, float:1.887951E38)
            java.lang.String r6 = "PaymentBillingAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r8]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
        L_0x0b60:
            r3 = 3
            if (r0 == r3) goto L_0x0b6f
            r3 = 5
            if (r0 == r3) goto L_0x0b6f
            r3 = 4
            if (r0 != r3) goto L_0x0b6d
            boolean r3 = r1.need_card_postcode
            if (r3 == 0) goto L_0x0b6f
        L_0x0b6d:
            r3 = 1
            goto L_0x0b70
        L_0x0b6f:
            r3 = 0
        L_0x0b70:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r2)
            android.widget.LinearLayout r6 = r1.linearLayout2
            r7 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r6.addView(r4, r7)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r4.setBackgroundColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r7 = new org.telegram.ui.Components.EditTextBoldCursor
            r7.<init>(r2)
            r6[r0] = r7
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            r6.setTag(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 1098907648(0x41800000, float:16.0)
            r6.setTextSize(r8, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            java.lang.String r7 = "windowBackgroundWhiteHintText"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setHintTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6.setTextColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 0
            r6.setBackgroundDrawable(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6.setCursorColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setCursorSize(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 1069547520(0x3fCLASSNAME, float:1.5)
            r6.setCursorWidth(r7)
            r6 = 3
            if (r0 != r6) goto L_0x0CLASSNAME
            android.text.InputFilter[] r7 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r9 = new android.text.InputFilter$LengthFilter
            r9.<init>(r6)
            r7[r5] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r6.setFilters(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 130(0x82, float:1.82E-43)
            r6.setInputType(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            android.graphics.Typeface r7 = android.graphics.Typeface.DEFAULT
            r6.setTypeface(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            android.text.method.PasswordTransformationMethod r7 = android.text.method.PasswordTransformationMethod.getInstance()
            r6.setTransformationMethod(r7)
            goto L_0x0c5c
        L_0x0CLASSNAME:
            if (r0 != 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 3
            r6.setInputType(r7)
            goto L_0x0c5c
        L_0x0CLASSNAME:
            r6 = 4
            if (r0 != r6) goto L_0x0c3a
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$Ueinsx-MGryTGh-0YI94FAwwnHM r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$Ueinsx-MGryTGh-0YI94FAwwnHM
            r7.<init>()
            r6.setOnTouchListener(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r6.setInputType(r5)
            goto L_0x0c5c
        L_0x0c3a:
            if (r0 != r8) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 16386(0x4002, float:2.2962E-41)
            r6.setInputType(r7)
            goto L_0x0c5c
        L_0x0CLASSNAME:
            r6 = 2
            if (r0 != r6) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 4097(0x1001, float:5.741E-42)
            r6.setInputType(r7)
            goto L_0x0c5c
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 16385(0x4001, float:2.296E-41)
            r6.setInputType(r7)
        L_0x0c5c:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 268435461(0x10000005, float:2.5243564E-29)
            r6.setImeOptions(r7)
            if (r0 == 0) goto L_0x0ccc
            if (r0 == r8) goto L_0x0cbb
            r6 = 2
            if (r0 == r6) goto L_0x0caa
            r6 = 3
            if (r0 == r6) goto L_0x0CLASSNAME
            r6 = 4
            if (r0 == r6) goto L_0x0CLASSNAME
            r6 = 5
            if (r0 == r6) goto L_0x0CLASSNAME
            goto L_0x0cdc
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 2131626192(0x7f0e08d0, float:1.8879613E38)
            java.lang.String r9 = "PaymentShippingZipPlaceholder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setHint(r7)
            goto L_0x0cdc
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 2131626182(0x7f0e08c6, float:1.8879593E38)
            java.lang.String r9 = "PaymentShippingCountry"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setHint(r7)
            goto L_0x0cdc
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 2131626142(0x7f0e089e, float:1.8879512E38)
            java.lang.String r9 = "PaymentCardCvv"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setHint(r7)
            goto L_0x0cdc
        L_0x0caa:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 2131626145(0x7f0e08a1, float:1.8879518E38)
            java.lang.String r9 = "PaymentCardName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setHint(r7)
            goto L_0x0cdc
        L_0x0cbb:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 2131626143(0x7f0e089f, float:1.8879514E38)
            java.lang.String r9 = "PaymentCardExpireDate"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setHint(r7)
            goto L_0x0cdc
        L_0x0ccc:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 2131626146(0x7f0e08a2, float:1.887952E38)
            java.lang.String r9 = "PaymentCardNumber"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setHint(r7)
        L_0x0cdc:
            if (r0 != 0) goto L_0x0ceb
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$7 r7 = new org.telegram.ui.PaymentFormActivity$7
            r7.<init>()
            r6.addTextChangedListener(r7)
            goto L_0x0cf9
        L_0x0ceb:
            if (r0 != r8) goto L_0x0cf9
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$8 r7 = new org.telegram.ui.PaymentFormActivity$8
            r7.<init>()
            r6.addTextChangedListener(r7)
        L_0x0cf9:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setPadding(r5, r5, r5, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x0d10
            r7 = 5
            goto L_0x0d11
        L_0x0d10:
            r7 = 3
        L_0x0d11:
            r6.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            r21 = -1
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 51
            r24 = 1101529088(0x41a80000, float:21.0)
            r25 = 1094713344(0x41400000, float:12.0)
            r26 = 1101529088(0x41a80000, float:21.0)
            r27 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r4.addView(r6, r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r1.inputFields
            r6 = r6[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$kGOIockVRhCZimnBQEOXCbrCCls r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$kGOIockVRhCZimnBQEOXCbrCCls
            r7.<init>()
            r6.setOnEditorActionListener(r7)
            r6 = 3
            if (r0 != r6) goto L_0x0d56
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r7 = new org.telegram.ui.Cells.ShadowSectionCell
            r7.<init>(r2)
            r6[r5] = r7
            android.widget.LinearLayout r6 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r7 = r1.sectionCell
            r7 = r7[r5]
            r9 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r6.addView(r7, r11)
            goto L_0x0e0a
        L_0x0d56:
            r6 = 5
            r9 = -2
            r10 = -1
            if (r0 != r6) goto L_0x0dd3
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r7 = new org.telegram.ui.Cells.ShadowSectionCell
            r7.<init>(r2)
            r11 = 2
            r6[r11] = r7
            android.widget.LinearLayout r6 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r7 = r1.sectionCell
            r7 = r7[r11]
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r6.addView(r7, r11)
            org.telegram.ui.Cells.TextCheckCell r6 = new org.telegram.ui.Cells.TextCheckCell
            r6.<init>(r2)
            r1.checkCell1 = r6
            org.telegram.ui.Cells.TextCheckCell r6 = r1.checkCell1
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r6.setBackgroundDrawable(r7)
            org.telegram.ui.Cells.TextCheckCell r6 = r1.checkCell1
            r7 = 2131626147(0x7f0e08a3, float:1.8879522E38)
            java.lang.String r9 = "PaymentCardSavePaymentInformation"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            boolean r9 = r1.saveCardInfo
            r6.setTextAndCheck(r7, r9, r5)
            android.widget.LinearLayout r6 = r1.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r7 = r1.checkCell1
            r9 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r6.addView(r7, r11)
            org.telegram.ui.Cells.TextCheckCell r6 = r1.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$7nFCu4nozpZFMiRn4XlVrELG-z4 r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$7nFCu4nozpZFMiRn4XlVrELG-z4
            r7.<init>()
            r6.setOnClickListener(r7)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r7.<init>(r2)
            r6[r5] = r7
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r1.bottomCell
            r6 = r6[r5]
            r7 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r7, (java.lang.String) r14)
            r6.setBackgroundDrawable(r9)
            r28.updateSavePaymentField()
            android.widget.LinearLayout r6 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r5]
            r9 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r6.addView(r7, r11)
            goto L_0x0e0a
        L_0x0dd3:
            if (r0 != 0) goto L_0x0e0a
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r2)
            r1.androidPayContainer = r6
            android.widget.FrameLayout r6 = r1.androidPayContainer
            r7 = 4000(0xfa0, float:5.605E-42)
            r6.setId(r7)
            android.widget.FrameLayout r6 = r1.androidPayContainer
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r6.setBackgroundDrawable(r7)
            android.widget.FrameLayout r6 = r1.androidPayContainer
            r7 = 8
            r6.setVisibility(r7)
            android.widget.FrameLayout r6 = r1.androidPayContainer
            r21 = -2
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 21
            r24 = 0
            r25 = 0
            r26 = 1082130432(0x40800000, float:4.0)
            r27 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r4.addView(r6, r7)
        L_0x0e0a:
            if (r3 == 0) goto L_0x0e28
            org.telegram.ui.PaymentFormActivity$9 r3 = new org.telegram.ui.PaymentFormActivity$9
            r3.<init>(r2)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r1.dividers
            r6.add(r3)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r7 = 83
            r9 = -1
            r6.<init>(r9, r8, r7)
            r4.addView(r3, r6)
        L_0x0e28:
            r3 = 4
            if (r0 != r3) goto L_0x0e33
            boolean r3 = r1.need_card_country
            if (r3 == 0) goto L_0x0e30
            goto L_0x0e33
        L_0x0e30:
            r3 = 8
            goto L_0x0e42
        L_0x0e33:
            r3 = 5
            if (r0 != r3) goto L_0x0e3a
            boolean r3 = r1.need_card_postcode
            if (r3 == 0) goto L_0x0e30
        L_0x0e3a:
            r3 = 2
            if (r0 != r3) goto L_0x0e45
            boolean r3 = r1.need_card_name
            if (r3 != 0) goto L_0x0e45
            goto L_0x0e30
        L_0x0e42:
            r4.setVisibility(r3)
        L_0x0e45:
            int r0 = r0 + 1
            r3 = 6
            goto L_0x0af2
        L_0x0e4a:
            boolean r0 = r1.need_card_country
            if (r0 != 0) goto L_0x0e62
            boolean r0 = r1.need_card_postcode
            if (r0 != 0) goto L_0x0e62
            org.telegram.ui.Cells.HeaderCell[] r0 = r1.headerCell
            r0 = r0[r8]
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r0 = r0[r5]
            r0.setVisibility(r2)
        L_0x0e62:
            boolean r0 = r1.need_card_postcode
            if (r0 == 0) goto L_0x0e73
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 5
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x18c4
        L_0x0e73:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 3
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x18c4
        L_0x0e80:
            if (r0 != r8) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r1.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r3 = new org.telegram.ui.Cells.RadioCell[r0]
            r1.radioCells = r3
            r3 = 0
        L_0x0e8f:
            if (r3 >= r0) goto L_0x0efa
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r4 = r1.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r4 = r4.shipping_options
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_shippingOption r4 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r4
            org.telegram.ui.Cells.RadioCell[] r6 = r1.radioCells
            org.telegram.ui.Cells.RadioCell r7 = new org.telegram.ui.Cells.RadioCell
            r7.<init>(r2)
            r6[r3] = r7
            org.telegram.ui.Cells.RadioCell[] r6 = r1.radioCells
            r6 = r6[r3]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
            r6.setTag(r7)
            org.telegram.ui.Cells.RadioCell[] r6 = r1.radioCells
            r6 = r6[r3]
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r6.setBackgroundDrawable(r7)
            org.telegram.ui.Cells.RadioCell[] r6 = r1.radioCells
            r6 = r6[r3]
            r7 = 2
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r7 = r4.prices
            java.lang.String r7 = r1.getTotalPriceString(r7)
            r9[r5] = r7
            java.lang.String r4 = r4.title
            r9[r8] = r4
            java.lang.String r4 = "%s - %s"
            java.lang.String r4 = java.lang.String.format(r4, r9)
            if (r3 != 0) goto L_0x0ed7
            r7 = 1
            goto L_0x0ed8
        L_0x0ed7:
            r7 = 0
        L_0x0ed8:
            int r9 = r0 + -1
            if (r3 == r9) goto L_0x0ede
            r9 = 1
            goto L_0x0edf
        L_0x0ede:
            r9 = 0
        L_0x0edf:
            r6.setText(r4, r7, r9)
            org.telegram.ui.Cells.RadioCell[] r4 = r1.radioCells
            r4 = r4[r3]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$vEFqYoe1G6TRzc-6QuSxLqi0_ls r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$vEFqYoe1G6TRzc-6QuSxLqi0_ls
            r6.<init>()
            r4.setOnClickListener(r6)
            android.widget.LinearLayout r4 = r1.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r6 = r1.radioCells
            r6 = r6[r3]
            r4.addView(r6)
            int r3 = r3 + 1
            goto L_0x0e8f
        L_0x0efa:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r2)
            r0[r5] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r0 = r0[r5]
            r3 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r14)
            r0.setBackgroundDrawable(r2)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r1.bottomCell
            r2 = r2[r5]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
            goto L_0x18c4
        L_0x0var_:
            r3 = 3
            if (r0 != r3) goto L_0x1161
            r3 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x0f2b:
            if (r0 >= r3) goto L_0x18c4
            if (r0 != 0) goto L_0x0var_
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            r4 = 2131626150(0x7f0e08a6, float:1.8879528E38)
            java.lang.String r6 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r5]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
            goto L_0x0var_
        L_0x0var_:
            r7 = -1
        L_0x0var_:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r6 = 50
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r4.addView(r3, r6)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            if (r0 == r8) goto L_0x0f7f
            r4 = 1
            goto L_0x0var_
        L_0x0f7f:
            r4 = 0
        L_0x0var_:
            if (r4 == 0) goto L_0x0f9f
            r6 = 7
            if (r0 != r6) goto L_0x0f8f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x0f8f
        L_0x0f8d:
            r4 = 0
            goto L_0x0f9f
        L_0x0f8f:
            r6 = 6
            if (r0 != r6) goto L_0x0f9f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r7 = r6.phone_requested
            if (r7 != 0) goto L_0x0f9f
            boolean r6 = r6.email_requested
            if (r6 != 0) goto L_0x0f9f
            goto L_0x0f8d
        L_0x0f9f:
            if (r4 == 0) goto L_0x0fbd
            org.telegram.ui.PaymentFormActivity$10 r4 = new org.telegram.ui.PaymentFormActivity$10
            r4.<init>(r2)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r4.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r1.dividers
            r6.add(r4)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r7 = 83
            r9 = -1
            r6.<init>(r9, r8, r7)
            r3.addView(r4, r6)
        L_0x0fbd:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r2)
            r4[r0] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r4.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r8, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r6)
            if (r0 != 0) goto L_0x102f
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$NaAxpZmEom2J3d4n5KPbjk_hflw r6 = org.telegram.ui.$$Lambda$PaymentFormActivity$NaAxpZmEom2J3d4n5KPbjk_hflw.INSTANCE
            r4.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.setInputType(r5)
            goto L_0x1041
        L_0x102f:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 129(0x81, float:1.81E-43)
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r6)
        L_0x1041:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r6)
            if (r0 == 0) goto L_0x1068
            if (r0 == r8) goto L_0x1050
            goto L_0x1075
        L_0x1050:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 2131625498(0x7f0e061a, float:1.8878206E38)
            java.lang.String r7 = "LoginPassword"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.requestFocus()
            goto L_0x1075
        L_0x1068:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r6 = r6.saved_credentials
            java.lang.String r6 = r6.title
            r4.setText(r6)
        L_0x1075:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r5, r5, r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x108c
            r6 = 5
            goto L_0x108d
        L_0x108c:
            r6 = 3
        L_0x108d:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r21 = -1
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 51
            r24 = 1101529088(0x41a80000, float:21.0)
            r25 = 1094713344(0x41400000, float:12.0)
            r26 = 1101529088(0x41a80000, float:21.0)
            r27 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r3.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$7nDt_ueaNatOk32R3AVkL3FoSzw r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$7nDt_ueaNatOk32R3AVkL3FoSzw
            r4.<init>()
            r3.setOnEditorActionListener(r4)
            if (r0 != r8) goto L_0x115c
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r5]
            r4 = 2131626159(0x7f0e08af, float:1.8879546E38)
            java.lang.Object[] r6 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r7 = r7.saved_credentials
            java.lang.String r7 = r7.title
            r6[r5] = r7
            java.lang.String r7 = "PaymentConfirmationMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r7, r4, r6)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r5]
            r4 = 2131165409(0x7var_e1, float:1.7945034E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r14)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r5]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r4 = new org.telegram.ui.Cells.TextSettingsCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r5]
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r3.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r5]
            r4 = 2131626160(0x7f0e08b0, float:1.8879548E38)
            java.lang.String r6 = "PaymentConfirmationNewCard"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4, r5)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r1.settingsCell
            r4 = r4[r5]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r5]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$hOneDfBvow-ay5NfpKknN53qGXA r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$hOneDfBvow-ay5NfpKknN53qGXA
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r8]
            r4 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r14)
            r3.setBackgroundDrawable(r6)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
        L_0x115c:
            int r0 = r0 + 1
            r3 = 2
            goto L_0x0f2b
        L_0x1161:
            r3 = 4
            if (r0 == r3) goto L_0x14c2
            r3 = 5
            if (r0 != r3) goto L_0x1169
            goto L_0x14c2
        L_0x1169:
            r3 = 6
            if (r0 != r3) goto L_0x18c4
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r2)
            r1.codeFieldCell = r0
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r1.codeFieldCell
            r3 = 2131626133(0x7f0e0895, float:1.8879494E38)
            java.lang.String r4 = "PasswordCode"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = ""
            r0.setTextAndHint(r4, r3, r5)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r1.codeFieldCell
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r1.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r3 = 3
            r0.setInputType(r3)
            r3 = 6
            r0.setImeOptions(r3)
            org.telegram.ui.-$$Lambda$PaymentFormActivity$zabhB89XpHUfevzEww0brDV8Dzs r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$zabhB89XpHUfevzEww0brDV8Dzs
            r3.<init>()
            r0.setOnEditorActionListener(r3)
            org.telegram.ui.PaymentFormActivity$15 r3 = new org.telegram.ui.PaymentFormActivity$15
            r3.<init>()
            r0.addTextChangedListener(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r3 = r1.codeFieldCell
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r2)
            r4 = 2
            r0[r4] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r0 = r0[r4]
            r3 = 2131165409(0x7var_e1, float:1.7945034E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r14)
            r0.setBackgroundDrawable(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r4]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r3 = new org.telegram.ui.Cells.TextSettingsCell
            r3.<init>(r2)
            r0[r8] = r3
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            r0.setTag(r13)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setTextColor(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            r3 = 2131626426(0x7f0e09ba, float:1.8880088E38)
            java.lang.String r4 = "ResendCode"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3, r8)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r8]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$I7CPk7usQoOQw119fB6a-FgtbsU r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$I7CPk7usQoOQw119fB6a-FgtbsU
            r3.<init>()
            r0.setOnClickListener(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r3 = new org.telegram.ui.Cells.TextSettingsCell
            r3.<init>(r2)
            r0[r5] = r3
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r5]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r5]
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            r0.setTag(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r5]
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r5]
            r3 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r4 = "AbortPassword"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3, r5)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r5]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r5]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$u3SvDLQ9ZpBSRMUM2jg2lBOMsbk r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$u3SvDLQ9ZpBSRMUM2jg2lBOMsbk
            r3.<init>()
            r0.setOnClickListener(r3)
            r3 = 3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x128c:
            if (r0 >= r3) goto L_0x14bd
            if (r0 != 0) goto L_0x12c4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r5]
            r4 = 2131626173(0x7f0e08bd, float:1.8879575E38)
            java.lang.String r6 = "PaymentPasswordTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r5]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
            goto L_0x12fb
        L_0x12c4:
            r3 = 2
            if (r0 != r3) goto L_0x12fb
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            r4 = 2131626169(0x7f0e08b9, float:1.8879567E38)
            java.lang.String r6 = "PaymentPasswordEmailTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r8]
            r6 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r3.addView(r4, r9)
            goto L_0x12fc
        L_0x12fb:
            r7 = -1
        L_0x12fc:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r6 = 50
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r6)
            r4.addView(r3, r6)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r3.setBackgroundColor(r4)
            if (r0 != 0) goto L_0x1331
            org.telegram.ui.PaymentFormActivity$16 r4 = new org.telegram.ui.PaymentFormActivity$16
            r4.<init>(r2)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r4.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r1.dividers
            r6.add(r4)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r7 = 83
            r9 = -1
            r6.<init>(r9, r8, r7)
            r3.addView(r4, r6)
        L_0x1331:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r2)
            r4[r0] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r4.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r8, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r7 = 0
            r4.setBackgroundDrawable(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r6)
            if (r0 == 0) goto L_0x13a9
            if (r0 != r8) goto L_0x1395
            goto L_0x13a9
        L_0x1395:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 33
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r6)
            goto L_0x13c5
        L_0x13a9:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 129(0x81, float:1.81E-43)
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r6)
        L_0x13c5:
            if (r0 == 0) goto L_0x13ef
            if (r0 == r8) goto L_0x13de
            r4 = 2
            if (r0 == r4) goto L_0x13cd
            goto L_0x1406
        L_0x13cd:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 2131626167(0x7f0e08b7, float:1.8879563E38)
            java.lang.String r9 = "PaymentPasswordEmail"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x1406
        L_0x13de:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            java.lang.String r9 = "PaymentPasswordReEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x1406
        L_0x13ef:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 2131626170(0x7f0e08ba, float:1.8879569E38)
            java.lang.String r9 = "PaymentPasswordEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.requestFocus()
        L_0x1406:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r5, r5, r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x141d
            r6 = 5
            goto L_0x141e
        L_0x141d:
            r6 = 3
        L_0x141e:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r21 = -1
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 51
            r24 = 1101529088(0x41a80000, float:21.0)
            r25 = 1094713344(0x41400000, float:12.0)
            r26 = 1101529088(0x41a80000, float:21.0)
            r27 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r3.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$wikSuuvfxhb2U7Hw9X3oaWcgGS8 r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$wikSuuvfxhb2U7Hw9X3oaWcgGS8
            r4.<init>()
            r3.setOnEditorActionListener(r4)
            if (r0 != r8) goto L_0x147f
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r5] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r5]
            r4 = 2131626171(0x7f0e08bb, float:1.887957E38)
            java.lang.String r6 = "PaymentPasswordInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r5]
            r4 = 2131165409(0x7var_e1, float:1.7945034E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r14)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r3.addView(r4, r10)
            goto L_0x14b8
        L_0x147f:
            r3 = 2
            if (r0 != r3) goto L_0x14b8
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r8]
            r4 = 2131626168(0x7f0e08b8, float:1.8879565E38)
            java.lang.String r6 = "PaymentPasswordEmailInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r8]
            r4 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r14)
            r3.setBackgroundDrawable(r6)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r3.addView(r4, r10)
        L_0x14b8:
            int r0 = r0 + 1
            r3 = 3
            goto L_0x128c
        L_0x14bd:
            r28.updatePasswordFields()
            goto L_0x18c4
        L_0x14c2:
            r7 = 0
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r2)
            r1.paymentInfoCell = r0
            org.telegram.ui.Cells.PaymentInfoCell r0 = r1.paymentInfoCell
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Cells.PaymentInfoCell r0 = r1.paymentInfoCell
            org.telegram.messenger.MessageObject r3 = r1.messageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r3
            java.lang.String r4 = r1.currentBotName
            r0.setInvoice(r3, r4)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r3 = r1.paymentInfoCell
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r10)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r2)
            r0[r5] = r3
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            r3 = r3[r5]
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r10)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r3.prices
            r0.<init>(r3)
            org.telegram.tgnet.TLRPC$TL_shippingOption r3 = r1.shippingOption
            if (r3 == 0) goto L_0x1519
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r3.prices
            r0.addAll(r3)
        L_0x1519:
            java.lang.String r3 = r1.getTotalPriceString(r0)
            r4 = 0
        L_0x151e:
            int r6 = r0.size()
            if (r4 >= r6) goto L_0x1555
            java.lang.Object r6 = r0.get(r4)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r6 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r6
            org.telegram.ui.Cells.TextPriceCell r10 = new org.telegram.ui.Cells.TextPriceCell
            r10.<init>(r2)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r10.setBackgroundColor(r11)
            java.lang.String r11 = r6.label
            org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
            long r7 = r6.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            java.lang.String r6 = r6.currency
            java.lang.String r6 = r12.formatCurrencyString(r7, r6)
            r10.setTextAndValue(r11, r6, r5)
            android.widget.LinearLayout r6 = r1.linearLayout2
            r6.addView(r10)
            int r4 = r4 + 1
            r7 = 0
            r8 = 1
            goto L_0x151e
        L_0x1555:
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r4)
            r4 = 2131626198(0x7f0e08d6, float:1.8879625E38)
            java.lang.String r6 = "PaymentTransactionTotal"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r6 = 1
            r0.setTextAndValue(r4, r3, r6)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r4.addView(r0)
            org.telegram.ui.PaymentFormActivity$11 r0 = new org.telegram.ui.PaymentFormActivity$11
            r0.<init>(r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r4)
            java.util.ArrayList<android.view.View> r4 = r1.dividers
            r4.add(r0)
            android.widget.LinearLayout r4 = r1.linearLayout2
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r7 = 83
            r8 = -1
            r10 = 1
            r6.<init>(r8, r10, r7)
            r4.addView(r0, r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r4 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r4.<init>(r2)
            r0[r5] = r4
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r5]
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r5]
            java.lang.String r4 = r1.cardName
            r6 = 2131626153(0x7f0e08a9, float:1.8879534E38)
            java.lang.String r7 = "PaymentCheckoutMethod"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r0.setTextAndValue(r4, r6, r10)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r4 = r1.detailSettingsCell
            r4 = r4[r5]
            r0.addView(r4)
            int r0 = r1.currentStep
            r4 = 4
            if (r0 != r4) goto L_0x15d2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r5]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$-_v7hZyhHE9vxsissEj7icTtuGM r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$-_v7hZyhHE9vxsissEj7icTtuGM
            r4.<init>()
            r0.setOnClickListener(r4)
        L_0x15d2:
            r0 = 0
            r4 = 0
        L_0x15d4:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r6.users
            int r6 = r6.size()
            if (r0 >= r6) goto L_0x15f4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r6.users
            java.lang.Object r6 = r6.get(r0)
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC.User) r6
            int r7 = r6.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r8 = r1.paymentForm
            int r8 = r8.provider_id
            if (r7 != r8) goto L_0x15f1
            r4 = r6
        L_0x15f1:
            int r0 = r0 + 1
            goto L_0x15d4
        L_0x15f4:
            if (r4 == 0) goto L_0x162d
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r6 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r6.<init>(r2)
            r7 = 1
            r0[r7] = r6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r7)
            r0.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            java.lang.String r6 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r6, r4)
            r6 = 2131626157(0x7f0e08ad, float:1.8879542E38)
            java.lang.String r8 = "PaymentCheckoutProvider"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r0.setTextAndValue(r4, r6, r7)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r7]
            r0.addView(r6)
            goto L_0x162f
        L_0x162d:
            java.lang.String r4 = ""
        L_0x162f:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            if (r0 == 0) goto L_0x1784
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            org.telegram.tgnet.TLRPC$TL_postAddress r0 = r0.shipping_address
            if (r0 == 0) goto L_0x168e
            r6 = 6
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.String r6 = r0.street_line1
            r7[r5] = r6
            java.lang.String r6 = r0.street_line2
            r8 = 1
            r7[r8] = r6
            java.lang.String r6 = r0.city
            r8 = 2
            r7[r8] = r6
            java.lang.String r6 = r0.state
            r8 = 3
            r7[r8] = r6
            java.lang.String r6 = r0.country_iso2
            r8 = 4
            r7[r8] = r6
            java.lang.String r0 = r0.post_code
            r6 = 5
            r7[r6] = r0
            java.lang.String r0 = "%s %s, %s, %s, %s, %s"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r7.<init>(r2)
            r8 = 2
            r6[r8] = r7
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r8]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r6.setBackgroundColor(r7)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r8]
            r7 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.String r10 = "PaymentShippingAddress"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r10 = 1
            r6.setTextAndValue(r0, r7, r10)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r8]
            r0.addView(r6)
        L_0x168e:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.name
            if (r0 == 0) goto L_0x16cb
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r6 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r6.<init>(r2)
            r7 = 3
            r0[r7] = r6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r6 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.info
            java.lang.String r6 = r6.name
            r8 = 2131626154(0x7f0e08aa, float:1.8879536E38)
            java.lang.String r10 = "PaymentCheckoutName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r10 = 1
            r0.setTextAndValue(r6, r8, r10)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r7]
            r0.addView(r6)
        L_0x16cb:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.phone
            if (r0 == 0) goto L_0x1711
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r6 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r6.<init>(r2)
            r7 = 4
            r0[r7] = r6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            org.telegram.PhoneFormat.PhoneFormat r6 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r7 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.info
            java.lang.String r7 = r7.phone
            java.lang.String r6 = r6.format(r7)
            r7 = 2131626156(0x7f0e08ac, float:1.887954E38)
            java.lang.String r8 = "PaymentCheckoutPhoneNumber"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r8 = 1
            r0.setTextAndValue(r6, r7, r8)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r7 = 4
            r6 = r6[r7]
            r0.addView(r6)
        L_0x1711:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.email
            if (r0 == 0) goto L_0x174e
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r6 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r6.<init>(r2)
            r7 = 5
            r0[r7] = r6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r6 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.info
            java.lang.String r6 = r6.email
            r8 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            java.lang.String r10 = "PaymentCheckoutEmail"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r10 = 1
            r0.setTextAndValue(r6, r8, r10)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r7]
            r0.addView(r6)
        L_0x174e:
            org.telegram.tgnet.TLRPC$TL_shippingOption r0 = r1.shippingOption
            if (r0 == 0) goto L_0x1784
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r6 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r6.<init>(r2)
            r7 = 6
            r0[r7] = r6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setBackgroundColor(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            org.telegram.tgnet.TLRPC$TL_shippingOption r6 = r1.shippingOption
            java.lang.String r6 = r6.title
            r8 = 2131626158(0x7f0e08ae, float:1.8879544E38)
            java.lang.String r10 = "PaymentCheckoutShippingMethod"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r0.setTextAndValue(r6, r8, r5)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r6 = r1.detailSettingsCell
            r6 = r6[r7]
            r0.addView(r6)
        L_0x1784:
            int r0 = r1.currentStep
            r6 = 4
            if (r0 != r6) goto L_0x189d
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r2)
            r1.bottomLayout = r0
            android.widget.FrameLayout r0 = r1.bottomLayout
            r6 = 1
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r0.setBackgroundDrawable(r7)
            android.widget.FrameLayout r0 = r1.bottomLayout
            r6 = 48
            r7 = 80
            r8 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r6, (int) r7)
            r9.addView(r0, r6)
            android.widget.FrameLayout r0 = r1.bottomLayout
            org.telegram.ui.-$$Lambda$PaymentFormActivity$UCCxWFA5zv52WCmPKPbwcSfjXPs r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$UCCxWFA5zv52WCmPKPbwcSfjXPs
            r6.<init>(r4, r3)
            r0.setOnClickListener(r6)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r2)
            r1.payTextView = r0
            android.widget.TextView r0 = r1.payTextView
            java.lang.String r4 = "windowBackgroundWhiteBlueText6"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setTextColor(r4)
            android.widget.TextView r0 = r1.payTextView
            r4 = 2131626155(0x7f0e08ab, float:1.8879538E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]
            r7[r5] = r3
            java.lang.String r3 = "PaymentCheckoutPay"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r7)
            r0.setText(r3)
            android.widget.TextView r0 = r1.payTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r6, r3)
            android.widget.TextView r0 = r1.payTextView
            r3 = 17
            r0.setGravity(r3)
            android.widget.TextView r0 = r1.payTextView
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r0.setTypeface(r3)
            android.widget.FrameLayout r0 = r1.bottomLayout
            android.widget.TextView r3 = r1.payTextView
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r0.addView(r3, r4)
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r2, r5)
            r1.progressViewButton = r0
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressViewButton
            r3 = 4
            r0.setVisibility(r3)
            android.widget.FrameLayout r0 = r1.bottomLayout
            org.telegram.ui.Components.ContextProgressView r3 = r1.progressViewButton
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r0.addView(r3, r4)
            android.view.View r0 = new android.view.View
            r0.<init>(r2)
            r3 = 2131165427(0x7var_f3, float:1.794507E38)
            r0.setBackgroundResource(r3)
            r19 = -1
            r20 = 1077936128(0x40400000, float:3.0)
            r21 = 83
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r9.addView(r0, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            r0.setEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            android.view.View r0 = r0.getContentView()
            r3 = 4
            r0.setVisibility(r3)
            org.telegram.ui.PaymentFormActivity$13 r0 = new org.telegram.ui.PaymentFormActivity$13
            r0.<init>(r2)
            r1.webView = r0
            android.webkit.WebView r0 = r1.webView
            r3 = -1
            r0.setBackgroundColor(r3)
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r3 = 1
            r0.setJavaScriptEnabled(r3)
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r3)
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r0 < r4) goto L_0x1880
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setMixedContentMode(r5)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r4 = r1.webView
            r0.setAcceptThirdPartyCookies(r4, r3)
        L_0x1880:
            android.webkit.WebView r0 = r1.webView
            org.telegram.ui.PaymentFormActivity$14 r3 = new org.telegram.ui.PaymentFormActivity$14
            r3.<init>()
            r0.setWebViewClient(r3)
            android.webkit.WebView r0 = r1.webView
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r9.addView(r0, r3)
            android.webkit.WebView r0 = r1.webView
            r3 = 8
            r0.setVisibility(r3)
        L_0x189d:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r2)
            r4 = 1
            r0[r4] = r3
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r0 = r0[r4]
            r3 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r14)
            r0.setBackgroundDrawable(r2)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r1.sectionCell
            r2 = r2[r4]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
        L_0x18c4:
            android.view.View r0 = r1.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ boolean lambda$createView$1$PaymentFormActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(String str, String str2) {
                    PaymentFormActivity.this.lambda$null$0$PaymentFormActivity(str, str2);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$0$PaymentFormActivity(String str, String str2) {
        this.inputFields[4].setText(str);
        this.countryName = str2;
    }

    public /* synthetic */ boolean lambda$createView$2$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int intValue = ((Integer) textView2.getTag()).intValue();
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
        this.saveShippingInfo = !this.saveShippingInfo;
        this.checkCell1.setChecked(this.saveShippingInfo);
    }

    public /* synthetic */ void lambda$createView$4$PaymentFormActivity(View view) {
        this.saveCardInfo = !this.saveCardInfo;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    public /* synthetic */ boolean lambda$createView$6$PaymentFormActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(String str, String str2) {
                    PaymentFormActivity.this.lambda$null$5$PaymentFormActivity(str, str2);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$5$PaymentFormActivity(String str, String str2) {
        this.inputFields[4].setText(str);
    }

    public /* synthetic */ boolean lambda$createView$7$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int intValue = ((Integer) textView2.getTag()).intValue();
            while (true) {
                intValue++;
                if (intValue < this.inputFields.length) {
                    if (intValue == 4) {
                        intValue++;
                    }
                    if (((View) this.inputFields[intValue].getParent()).getVisibility() == 0) {
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

    public /* synthetic */ void lambda$createView$8$PaymentFormActivity(View view) {
        this.saveCardInfo = !this.saveCardInfo;
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

    public /* synthetic */ boolean lambda$createView$11$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
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
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsAndroidPay) null);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
            }

            public void onFragmentDestroyed() {
            }

            public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
                PaymentFormActivity.this.paymentForm.saved_credentials = null;
                String unused = PaymentFormActivity.this.paymentJson = str;
                boolean unused2 = PaymentFormActivity.this.saveCardInfo = z;
                String unused3 = PaymentFormActivity.this.cardName = str2;
                TLRPC.TL_inputPaymentCredentialsAndroidPay unused4 = PaymentFormActivity.this.androidPayCredentials = tL_inputPaymentCredentialsAndroidPay;
                PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", NUM), true);
                return false;
            }
        });
        presentFragment(paymentFormActivity);
    }

    public /* synthetic */ void lambda$createView$15$PaymentFormActivity(String str, String str2, View view) {
        TLRPC.User user = this.botUser;
        if (user == null || user.verified) {
            showPayAlert(str2);
            return;
        }
        String str3 = "payment_warning_" + this.botUser.id;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        if (!notificationsSettings.getBoolean(str3, false)) {
            notificationsSettings.edit().putBoolean(str3, true).commit();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentWarning", NUM));
            builder.setMessage(LocaleController.formatString("PaymentWarningText", NUM, this.currentBotName, str));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(str2) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    PaymentFormActivity.this.lambda$null$14$PaymentFormActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return;
        }
        showPayAlert(str2);
    }

    public /* synthetic */ void lambda$null$14$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
        showPayAlert(str);
    }

    public /* synthetic */ boolean lambda$createView$16$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    public /* synthetic */ void lambda$createView$18$PaymentFormActivity(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), $$Lambda$PaymentFormActivity$kpdlJs1QwZ9_5EeFfvuu3eWN4uE.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$createView$20$PaymentFormActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(string);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", NUM));
        builder.setPositiveButton(LocaleController.getString("Disable", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.lambda$null$19$PaymentFormActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView2 = (TextView) create.getButton(-1);
        if (textView2 != null) {
            textView2.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    public /* synthetic */ void lambda$null$19$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    public /* synthetic */ boolean lambda$createView$21$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        } else if (i != 5) {
            return false;
        } else {
            int intValue = ((Integer) textView2.getTag()).intValue();
            if (intValue == 0) {
                this.inputFields[1].requestFocus();
                return false;
            } else if (intValue != 1) {
                return false;
            } else {
                this.inputFields[2].requestFocus();
                return false;
            }
        }
    }

    private void updatePasswordFields() {
        if (this.currentStep == 6 && this.bottomCell[2] != null) {
            int i = 0;
            this.doneItem.setVisibility(0);
            if (this.currentPassword == null) {
                showEditDoneProgress(true, true);
                this.bottomCell[2].setVisibility(8);
                this.settingsCell[0].setVisibility(8);
                this.settingsCell[1].setVisibility(8);
                this.codeFieldCell.setVisibility(8);
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (int i2 = 0; i2 < 3; i2++) {
                    ((View) this.inputFields[i2].getParent()).setVisibility(8);
                }
                while (i < this.dividers.size()) {
                    this.dividers.get(i).setVisibility(8);
                    i++;
                }
                return;
            }
            showEditDoneProgress(true, false);
            if (this.waitingForEmail) {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
                Object[] objArr = new Object[1];
                String str = this.currentPassword.email_unconfirmed_pattern;
                if (str == null) {
                    str = "";
                }
                objArr[0] = str;
                textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                this.bottomCell[2].setVisibility(0);
                this.settingsCell[0].setVisibility(0);
                this.settingsCell[1].setVisibility(0);
                this.codeFieldCell.setVisibility(0);
                this.bottomCell[1].setText("");
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (int i3 = 0; i3 < 3; i3++) {
                    ((View) this.inputFields[i3].getParent()).setVisibility(8);
                }
                while (i < this.dividers.size()) {
                    this.dividers.get(i).setVisibility(8);
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
            for (int i4 = 0; i4 < 3; i4++) {
                ((View) this.inputFields[i4].getParent()).setVisibility(0);
            }
            for (int i5 = 0; i5 < this.dividers.size(); i5++) {
                this.dividers.get(i5).setVisibility(0);
            }
        }
    }

    private void loadPasswordInfo() {
        if (!this.loadingPasswordInfo) {
            this.loadingPasswordInfo = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.lambda$loadPasswordInfo$24$PaymentFormActivity(tLObject, tL_error);
                }
            }, 10);
        }
    }

    public /* synthetic */ void lambda$loadPasswordInfo$24$PaymentFormActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$23$PaymentFormActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$23$PaymentFormActivity(TLRPC.TL_error tL_error, TLObject tLObject) {
        this.loadingPasswordInfo = false;
        if (tL_error == null) {
            this.currentPassword = (TLRPC.TL_account_password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(this.currentPassword, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            TLRPC.TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
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
                this.shortPollRunnable = new Runnable() {
                    public final void run() {
                        PaymentFormActivity.this.lambda$null$22$PaymentFormActivity();
                    }
                };
                AndroidUtilities.runOnUIThread(this.shortPollRunnable, 5000);
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
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void showPayAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", NUM));
        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", NUM, str, this.currentBotName, this.currentItemName));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.lambda$showPayAlert$25$PaymentFormActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$showPayAlert$25$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        setDonePressed(true);
        sendData();
    }

    private String getTotalPriceString(ArrayList<TLRPC.TL_labeledPrice> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += arrayList.get(i).amount;
        }
        return LocaleController.getInstance().formatCurrencyString(j, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TLRPC.TL_labeledPrice> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += arrayList.get(i).amount;
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
        WebView webView2 = this.webView;
        if (webView2 != null) {
            try {
                ViewParent parent = webView2.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(this.webView);
                }
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                this.webViewUrl = null;
                this.webView.destroy();
                this.webView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            if ((this.currentStep == 2 || this.currentStep == 6) && Build.VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(8192);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        super.onFragmentDestroy();
        this.canceled = true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            WebView webView2 = this.webView;
            if (webView2 == null) {
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
                webView2.loadUrl(str);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetTwoStepPassword) {
            TLRPC.TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
            tL_payments_paymentForm.password_missing = false;
            tL_payments_paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (i == NotificationCenter.didRemoveTwoStepPassword) {
            TLRPC.TL_payments_paymentForm tL_payments_paymentForm2 = this.paymentForm;
            tL_payments_paymentForm2.password_missing = true;
            tL_payments_paymentForm2.can_save_credentials = false;
            updateSavePaymentField();
        } else if (i == NotificationCenter.paymentFinished) {
            removeSelfFromStack();
        }
    }

    private void showAndroidPay() {
        WalletFragmentStyle walletFragmentStyle;
        PaymentMethodTokenizationParameters paymentMethodTokenizationParameters;
        if (getParentActivity() != null && this.androidPayContainer != null) {
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
            TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                arrayList.addAll(tL_shippingOption.prices);
            }
            this.totalPriceDecimal = getTotalPriceDecimalString(arrayList);
            if (this.androidPayPublicKey != null) {
                PaymentMethodTokenizationParameters.Builder newBuilder2 = PaymentMethodTokenizationParameters.newBuilder();
                newBuilder2.setPaymentMethodTokenizationType(2);
                newBuilder2.addParameter("publicKey", this.androidPayPublicKey);
                paymentMethodTokenizationParameters = newBuilder2.build();
            } else {
                PaymentMethodTokenizationParameters.Builder newBuilder3 = PaymentMethodTokenizationParameters.newBuilder();
                newBuilder3.setPaymentMethodTokenizationType(1);
                newBuilder3.addParameter("gateway", "stripe");
                newBuilder3.addParameter("stripe:publishableKey", this.stripeApiKey);
                newBuilder3.addParameter("stripe:version", "3.5.0");
                paymentMethodTokenizationParameters = newBuilder3.build();
            }
            MaskedWalletRequest.Builder newBuilder4 = MaskedWalletRequest.newBuilder();
            newBuilder4.setPaymentMethodTokenizationParameters(paymentMethodTokenizationParameters);
            newBuilder4.setEstimatedTotalPrice(this.totalPriceDecimal);
            newBuilder4.setCurrencyCode(this.paymentForm.invoice.currency);
            MaskedWalletRequest build = newBuilder4.build();
            WalletFragmentInitParams.Builder newBuilder5 = WalletFragmentInitParams.newBuilder();
            newBuilder5.setMaskedWalletRequest(build);
            newBuilder5.setMaskedWalletRequestCode(1000);
            newInstance.initialize(newBuilder5.build());
            this.androidPayContainer.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[]{0.0f, 1.0f})});
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
                TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
                if (tL_shippingOption != null) {
                    arrayList.addAll(tL_shippingOption.prices);
                }
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    TLRPC.TL_labeledPrice tL_labeledPrice = (TLRPC.TL_labeledPrice) arrayList.get(i3);
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
                        this.androidPayCredentials = new TLRPC.TL_inputPaymentCredentialsAndroidPay();
                        this.androidPayCredentials.payment_token = new TLRPC.TL_dataJSON();
                        this.androidPayCredentials.payment_token.data = token;
                        this.androidPayCredentials.google_transaction_id = fullWallet.getGoogleTransactionId();
                        String[] paymentDescriptions = fullWallet.getPaymentDescriptions();
                        if (paymentDescriptions.length > 0) {
                            this.cardName = paymentDescriptions[0];
                        } else {
                            this.cardName = "Android Pay";
                        }
                    } else {
                        Token parseToken = TokenParser.parseToken(token);
                        this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{parseToken.getType(), parseToken.getId()});
                        Card card = parseToken.getCard();
                        this.cardName = card.getType() + " *" + card.getLast4();
                    }
                    goToNextStep();
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                } catch (JSONException unused) {
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                }
            } else {
                showEditDoneProgress(true, false);
                setDonePressed(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void goToNextStep() {
        boolean z;
        int i;
        int i2;
        int i3 = this.currentStep;
        if (i3 == 0) {
            TLRPC.TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
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
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i2, this.requestedInfo, (TLRPC.TL_shippingOption) null, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (i3 == 1) {
            if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (i3 == 2) {
            TLRPC.TL_payments_paymentForm tL_payments_paymentForm2 = this.paymentForm;
            if (!tL_payments_paymentForm2.password_missing || !(z = this.saveCardInfo)) {
                PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
                if (paymentFormActivityDelegate != null) {
                    paymentFormActivityDelegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials);
                    finishFragment();
                    return;
                }
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
                return;
            }
            this.passwordFragment = new PaymentFormActivity(tL_payments_paymentForm2, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, z, this.androidPayCredentials);
            this.passwordFragment.setCurrentPassword(this.currentPassword);
            this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
                    if (PaymentFormActivity.this.delegate != null) {
                        PaymentFormActivity.this.delegate.didSelectNewCard(str, str2, z, tL_inputPaymentCredentialsAndroidPay);
                    }
                    if (PaymentFormActivity.this.isWebView) {
                        PaymentFormActivity.this.removeSelfFromStack();
                    }
                    return PaymentFormActivity.this.delegate != null;
                }

                public void onFragmentDestroyed() {
                    PaymentFormActivity unused = PaymentFormActivity.this.passwordFragment = null;
                }

                public void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                    TLRPC.TL_account_password unused = PaymentFormActivity.this.currentPassword = tL_account_password;
                }
            });
            presentFragment(this.passwordFragment, this.isWebView);
        } else if (i3 == 3) {
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.passwordOk ? 4 : 2, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), !this.passwordOk);
        } else if (i3 == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (i3 != 6) {
        } else {
            if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials)) {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), true);
            } else {
                finishFragment();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSavePaymentField() {
        WebView webView2;
        if (this.bottomCell[0] != null && this.sectionCell[2] != null) {
            TLRPC.TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
            if ((tL_payments_paymentForm.password_missing || tL_payments_paymentForm.can_save_credentials) && ((webView2 = this.webView) == null || (webView2 != null && !this.webviewLoading))) {
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
                        int i = indexOf + length;
                        int i2 = lastIndexOf + length;
                        this.bottomCell[0].getTextView().setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spannableStringBuilder.replace(i2, i2 + 1, "");
                        spannableStringBuilder.replace(i, i + 1, "");
                        spannableStringBuilder.setSpan(new LinkSpan(), i, i2 - 1, 33);
                    }
                }
                this.checkCell1.setEnabled(true);
                this.bottomCell[0].setText(spannableStringBuilder);
                this.checkCell1.setVisibility(0);
                this.bottomCell[0].setVisibility(0);
                ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
                shadowSectionCellArr[2].setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCellArr[2].getContext(), NUM, "windowBackgroundGrayShadow"));
                return;
            }
            this.checkCell1.setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            ShadowSectionCell[] shadowSectionCellArr2 = this.sectionCell;
            shadowSectionCellArr2[2].setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCellArr2[2].getContext(), NUM, "windowBackgroundGrayShadow"));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036 A[Catch:{ Exception -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045 A[Catch:{ Exception -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"HardwareIds"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fillNumber(java.lang.String r8) {
        /*
            r7 = this;
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x009f }
            java.lang.String r1 = "phone"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ Exception -> 0x009f }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x009f }
            r1 = 1
            if (r8 != 0) goto L_0x0019
            int r2 = r0.getSimState()     // Catch:{ Exception -> 0x009f }
            if (r2 == r1) goto L_0x00a3
            int r2 = r0.getPhoneType()     // Catch:{ Exception -> 0x009f }
            if (r2 == 0) goto L_0x00a3
        L_0x0019:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x009f }
            r3 = 23
            r4 = 0
            if (r2 < r3) goto L_0x002f
            android.app.Activity r2 = r7.getParentActivity()     // Catch:{ Exception -> 0x009f }
            java.lang.String r3 = "android.permission.READ_PHONE_STATE"
            int r2 = r2.checkSelfPermission(r3)     // Catch:{ Exception -> 0x009f }
            if (r2 != 0) goto L_0x002d
            goto L_0x002f
        L_0x002d:
            r2 = 0
            goto L_0x0030
        L_0x002f:
            r2 = 1
        L_0x0030:
            if (r8 != 0) goto L_0x0034
            if (r2 == 0) goto L_0x00a3
        L_0x0034:
            if (r8 != 0) goto L_0x003e
            java.lang.String r8 = r0.getLine1Number()     // Catch:{ Exception -> 0x009f }
            java.lang.String r8 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r8)     // Catch:{ Exception -> 0x009f }
        L_0x003e:
            r0 = 0
            boolean r2 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x009f }
            if (r2 != 0) goto L_0x00a3
            int r2 = r8.length()     // Catch:{ Exception -> 0x009f }
            r3 = 4
            if (r2 <= r3) goto L_0x0084
        L_0x004c:
            r2 = 8
            if (r3 < r1) goto L_0x006f
            java.lang.String r5 = r8.substring(r4, r3)     // Catch:{ Exception -> 0x009f }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r7.codesMap     // Catch:{ Exception -> 0x009f }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x009f }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x009f }
            if (r6 == 0) goto L_0x006c
            java.lang.String r0 = r8.substring(r3)     // Catch:{ Exception -> 0x009f }
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields     // Catch:{ Exception -> 0x009f }
            r3 = r3[r2]     // Catch:{ Exception -> 0x009f }
            r3.setText(r5)     // Catch:{ Exception -> 0x009f }
            r3 = r0
            r0 = 1
            goto L_0x0071
        L_0x006c:
            int r3 = r3 + -1
            goto L_0x004c
        L_0x006f:
            r3 = r0
            r0 = 0
        L_0x0071:
            if (r0 != 0) goto L_0x0083
            java.lang.String r0 = r8.substring(r1)     // Catch:{ Exception -> 0x009f }
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields     // Catch:{ Exception -> 0x009f }
            r2 = r3[r2]     // Catch:{ Exception -> 0x009f }
            java.lang.String r8 = r8.substring(r4, r1)     // Catch:{ Exception -> 0x009f }
            r2.setText(r8)     // Catch:{ Exception -> 0x009f }
            goto L_0x0084
        L_0x0083:
            r0 = r3
        L_0x0084:
            if (r0 == 0) goto L_0x00a3
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r7.inputFields     // Catch:{ Exception -> 0x009f }
            r1 = 9
            r8 = r8[r1]     // Catch:{ Exception -> 0x009f }
            r8.setText(r0)     // Catch:{ Exception -> 0x009f }
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r7.inputFields     // Catch:{ Exception -> 0x009f }
            r8 = r8[r1]     // Catch:{ Exception -> 0x009f }
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields     // Catch:{ Exception -> 0x009f }
            r0 = r0[r1]     // Catch:{ Exception -> 0x009f }
            int r0 = r0.length()     // Catch:{ Exception -> 0x009f }
            r8.setSelection(r0)     // Catch:{ Exception -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x00a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.fillNumber(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void sendSavePassword(boolean z) {
        String str;
        String str2;
        if (z || this.codeFieldCell.getVisibility() != 0) {
            TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
            if (z) {
                this.doneItem.setVisibility(0);
                tL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
                TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.flags = 2;
                tL_account_passwordInputSettings.email = "";
                tL_account_updatePasswordSettings.password = new TLRPC.TL_inputCheckPasswordEmpty();
                str2 = null;
                str = null;
            } else {
                String obj = this.inputFields[0].getText().toString();
                if (TextUtils.isEmpty(obj)) {
                    shakeField(0);
                    return;
                } else if (!obj.equals(this.inputFields[1].getText().toString())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    shakeField(1);
                    return;
                } else {
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
                    tL_account_updatePasswordSettings.password = new TLRPC.TL_inputCheckPasswordEmpty();
                    tL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
                    TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings2 = tL_account_updatePasswordSettings.new_settings;
                    tL_account_passwordInputSettings2.flags |= 1;
                    tL_account_passwordInputSettings2.hint = "";
                    tL_account_passwordInputSettings2.new_algo = this.currentPassword.new_algo;
                    if (obj2.length() > 0) {
                        TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings3 = tL_account_updatePasswordSettings.new_settings;
                        tL_account_passwordInputSettings3.flags = 2 | tL_account_passwordInputSettings3.flags;
                        tL_account_passwordInputSettings3.email = obj2.trim();
                    }
                    str = obj;
                    str2 = obj2;
                }
            }
            showEditDoneProgress(true, true);
            Utilities.globalQueue.postRunnable(new Runnable(z, str2, str, tL_account_updatePasswordSettings) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendSavePassword$33$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        String text = this.codeFieldCell.getText();
        if (text.length() == 0) {
            shakeView(this.codeFieldCell);
            return;
        }
        showEditDoneProgress(true, true);
        TLRPC.TL_account_confirmPasswordEmail tL_account_confirmPasswordEmail = new TLRPC.TL_account_confirmPasswordEmail();
        tL_account_confirmPasswordEmail.code = text;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_confirmPasswordEmail, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$sendSavePassword$27$PaymentFormActivity(tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$sendSavePassword$27$PaymentFormActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$26$PaymentFormActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$26$PaymentFormActivity(TLRPC.TL_error tL_error) {
        String str;
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
        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tL_error.text);
        }
    }

    public /* synthetic */ void lambda$null$32$PaymentFormActivity(boolean z, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, z, tLObject, str) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$31$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$sendSavePassword$33$PaymentFormActivity(boolean z, String str, String str2, TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        $$Lambda$PaymentFormActivity$X_NgFbW2mNzLbpFBedySWqq2_bA r0 = new RequestDelegate(z, str) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$null$32$PaymentFormActivity(this.f$1, this.f$2, tLObject, tL_error);
            }
        };
        if (!z) {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str2);
            TLRPC.PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.new_algo;
            if (passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                tL_account_updatePasswordSettings.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo);
                if (tL_account_updatePasswordSettings.new_settings.new_password_hash == null) {
                    TLRPC.TL_error tL_error = new TLRPC.TL_error();
                    tL_error.text = "ALGO_INVALID";
                    r0.run((TLObject) null, tL_error);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, r0, 10);
                return;
            }
            TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
            tL_error2.text = "PASSWORD_HASH_INVALID";
            r0.run((TLObject) null, tL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, r0, 10);
    }

    public /* synthetic */ void lambda$null$31$PaymentFormActivity(TLRPC.TL_error tL_error, boolean z, TLObject tLObject, String str) {
        String str2;
        if (tL_error == null || !"SRP_ID_INVALID".equals(tL_error.text)) {
            showEditDoneProgress(true, false);
            if (z) {
                TLRPC.TL_account_password tL_account_password = this.currentPassword;
                tL_account_password.has_password = false;
                tL_account_password.current_algo = null;
                this.delegate.currentPasswordUpdated(tL_account_password);
                finishFragment();
            } else if (tL_error != null || !(tLObject instanceof TLRPC.TL_boolTrue)) {
                if (tL_error == null) {
                    return;
                }
                if (tL_error.text.equals("EMAIL_UNCONFIRMED") || tL_error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    this.emailCodeLength = Utilities.parseInt(tL_error.text).intValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(str) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PaymentFormActivity.this.lambda$null$30$PaymentFormActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    Dialog showDialog = showDialog(builder.create());
                    if (showDialog != null) {
                        showDialog.setCanceledOnTouchOutside(false);
                        showDialog.setCancelable(false);
                    }
                } else if (tL_error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt(tL_error.text).intValue();
                    if (intValue < 60) {
                        str2 = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), tL_error.text);
                }
            } else if (getParentActivity() != null) {
                goToNextStep();
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(z) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.lambda$null$29$PaymentFormActivity(this.f$1, tLObject, tL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$null$29$PaymentFormActivity(boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, z) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$28$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$28$PaymentFormActivity(TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            this.currentPassword = (TLRPC.TL_account_password) tLObject;
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            sendSavePassword(z);
        }
    }

    public /* synthetic */ void lambda$null$30$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
        this.waitingForEmail = true;
        this.currentPassword.email_unconfirmed_pattern = str;
        updatePasswordFields();
    }

    /* access modifiers changed from: private */
    public boolean sendCardData() {
        Integer num;
        Integer num2;
        String[] split = this.inputFields[1].getText().toString().split("/");
        if (split.length == 2) {
            Integer parseInt = Utilities.parseInt(split[0]);
            num = Utilities.parseInt(split[1]);
            num2 = parseInt;
        } else {
            num2 = null;
            num = null;
        }
        Card card = new Card(this.inputFields[0].getText().toString(), num2, num, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), (String) null, (String) null, (String) null, (String) null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), (String) null);
        this.cardName = card.getType() + " *" + card.getLast4();
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
        } else if (!this.need_card_postcode || this.inputFields[5].length() != 0) {
            showEditDoneProgress(true, true);
            try {
                new Stripe(this.stripeApiKey).createToken(card, new TokenCallback() {
                    public void onSuccess(Token token) {
                        if (!PaymentFormActivity.this.canceled) {
                            String unused = PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    PaymentFormActivity.AnonymousClass18.this.lambda$onSuccess$0$PaymentFormActivity$18();
                                }
                            });
                        }
                    }

                    public /* synthetic */ void lambda$onSuccess$0$PaymentFormActivity$18() {
                        PaymentFormActivity.this.goToNextStep();
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        PaymentFormActivity.this.setDonePressed(false);
                    }

                    public void onError(Exception exc) {
                        if (!PaymentFormActivity.this.canceled) {
                            PaymentFormActivity.this.showEditDoneProgress(true, false);
                            PaymentFormActivity.this.setDonePressed(false);
                            if ((exc instanceof APIConnectionException) || (exc instanceof APIException)) {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", NUM));
                            } else {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, exc.getMessage());
                            }
                        }
                    }
                });
                return true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return true;
            }
        } else {
            shakeField(5);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void sendForm() {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = this.validateRequest;
            tL_payments_validateRequestedInfo.save = this.saveShippingInfo;
            tL_payments_validateRequestedInfo.msg_id = this.messageObject.getId();
            this.validateRequest.info = new TLRPC.TL_paymentRequestedInfo();
            if (this.paymentForm.invoice.name_requested) {
                this.validateRequest.info.name = this.inputFields[6].getText().toString();
                this.validateRequest.info.flags |= 1;
            }
            if (this.paymentForm.invoice.phone_requested) {
                this.validateRequest.info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
                TLRPC.TL_paymentRequestedInfo tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags = tL_paymentRequestedInfo.flags | 2;
            }
            if (this.paymentForm.invoice.email_requested) {
                this.validateRequest.info.email = this.inputFields[7].getText().toString().trim();
                this.validateRequest.info.flags |= 4;
            }
            if (this.paymentForm.invoice.shipping_address_requested) {
                this.validateRequest.info.shipping_address = new TLRPC.TL_postAddress();
                this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
                this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
                this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
                this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
                TLRPC.TL_postAddress tL_postAddress = this.validateRequest.info.shipping_address;
                String str = this.countryName;
                if (str == null) {
                    str = "";
                }
                tL_postAddress.country_iso2 = str;
                this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
                this.validateRequest.info.flags |= 8;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate(this.validateRequest) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.lambda$sendForm$37$PaymentFormActivity(this.f$1, tLObject, tL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$sendForm$37$PaymentFormActivity(TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error) {
        if (tLObject2 instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject2) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$35$PaymentFormActivity(this.f$1);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$36$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$35$PaymentFormActivity(TLObject tLObject) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) tLObject;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC.TL_payments_clearSavedInfo tL_payments_clearSavedInfo = new TLRPC.TL_payments_clearSavedInfo();
            tL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_clearSavedInfo, $$Lambda$PaymentFormActivity$sO9JvHKi_UoC8_6WynH4daVgdY.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$36$PaymentFormActivity(org.telegram.tgnet.TLRPC.TL_error r12, org.telegram.tgnet.TLObject r13) {
        /*
            r11 = this;
            r0 = 0
            r11.setDonePressed(r0)
            r1 = 1
            r11.showEditDoneProgress(r1, r0)
            if (r12 == 0) goto L_0x00a7
            java.lang.String r2 = r12.text
            r3 = -1
            int r4 = r2.hashCode()
            r5 = 3
            r6 = 5
            r7 = 2
            r8 = 4
            r9 = 7
            r10 = 6
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
            }
        L_0x001a:
            goto L_0x0076
        L_0x001b:
            java.lang.String r4 = "REQ_INFO_EMAIL_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 2
            goto L_0x0077
        L_0x0025:
            java.lang.String r4 = "ADDRESS_STREET_LINE2_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 8
            goto L_0x0077
        L_0x0030:
            java.lang.String r4 = "REQ_INFO_PHONE_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 1
            goto L_0x0077
        L_0x003a:
            java.lang.String r4 = "ADDRESS_STATE_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 6
            goto L_0x0077
        L_0x0044:
            java.lang.String r4 = "ADDRESS_POSTCODE_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 5
            goto L_0x0077
        L_0x004e:
            java.lang.String r4 = "REQ_INFO_NAME_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 0
            goto L_0x0077
        L_0x0058:
            java.lang.String r4 = "ADDRESS_COUNTRY_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 3
            goto L_0x0077
        L_0x0062:
            java.lang.String r4 = "ADDRESS_STREET_LINE1_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 7
            goto L_0x0077
        L_0x006c:
            java.lang.String r4 = "ADDRESS_CITY_INVALID"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x0076
            r2 = 4
            goto L_0x0077
        L_0x0076:
            r2 = -1
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
            }
        L_0x007a:
            int r1 = r11.currentAccount
            java.lang.Object[] r0 = new java.lang.Object[r0]
            org.telegram.ui.Components.AlertsCreator.processError(r1, r12, r11, r13, r0)
            goto L_0x00a7
        L_0x0082:
            r11.shakeField(r1)
            goto L_0x00a7
        L_0x0086:
            r11.shakeField(r0)
            goto L_0x00a7
        L_0x008a:
            r11.shakeField(r5)
            goto L_0x00a7
        L_0x008e:
            r11.shakeField(r6)
            goto L_0x00a7
        L_0x0092:
            r11.shakeField(r7)
            goto L_0x00a7
        L_0x0096:
            r11.shakeField(r8)
            goto L_0x00a7
        L_0x009a:
            r11.shakeField(r9)
            goto L_0x00a7
        L_0x009e:
            r12 = 9
            r11.shakeField(r12)
            goto L_0x00a7
        L_0x00a4:
            r11.shakeField(r10)
        L_0x00a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.lambda$null$36$PaymentFormActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject):void");
    }

    private TLRPC.TL_paymentRequestedInfo getRequestInfo() {
        TLRPC.TL_paymentRequestedInfo tL_paymentRequestedInfo = new TLRPC.TL_paymentRequestedInfo();
        if (this.paymentForm.invoice.name_requested) {
            tL_paymentRequestedInfo.name = this.inputFields[6].getText().toString();
            tL_paymentRequestedInfo.flags |= 1;
        }
        if (this.paymentForm.invoice.phone_requested) {
            tL_paymentRequestedInfo.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
            tL_paymentRequestedInfo.flags = tL_paymentRequestedInfo.flags | 2;
        }
        if (this.paymentForm.invoice.email_requested) {
            tL_paymentRequestedInfo.email = this.inputFields[7].getText().toString().trim();
            tL_paymentRequestedInfo.flags |= 4;
        }
        if (this.paymentForm.invoice.shipping_address_requested) {
            tL_paymentRequestedInfo.shipping_address = new TLRPC.TL_postAddress();
            tL_paymentRequestedInfo.shipping_address.street_line1 = this.inputFields[0].getText().toString();
            tL_paymentRequestedInfo.shipping_address.street_line2 = this.inputFields[1].getText().toString();
            tL_paymentRequestedInfo.shipping_address.city = this.inputFields[2].getText().toString();
            tL_paymentRequestedInfo.shipping_address.state = this.inputFields[3].getText().toString();
            TLRPC.TL_postAddress tL_postAddress = tL_paymentRequestedInfo.shipping_address;
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
        String str;
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm = new TLRPC.TL_payments_sendPaymentForm();
            tL_payments_sendPaymentForm.msg_id = this.messageObject.getId();
            if (UserConfig.getInstance(this.currentAccount).tmpPassword == null || this.paymentForm.saved_credentials == null) {
                TLRPC.TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay = this.androidPayCredentials;
                if (tL_inputPaymentCredentialsAndroidPay != null) {
                    tL_payments_sendPaymentForm.credentials = tL_inputPaymentCredentialsAndroidPay;
                } else {
                    tL_payments_sendPaymentForm.credentials = new TLRPC.TL_inputPaymentCredentials();
                    TLRPC.InputPaymentCredentials inputPaymentCredentials = tL_payments_sendPaymentForm.credentials;
                    inputPaymentCredentials.save = this.saveCardInfo;
                    inputPaymentCredentials.data = new TLRPC.TL_dataJSON();
                    tL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
                }
            } else {
                tL_payments_sendPaymentForm.credentials = new TLRPC.TL_inputPaymentCredentialsSaved();
                TLRPC.InputPaymentCredentials inputPaymentCredentials2 = tL_payments_sendPaymentForm.credentials;
                inputPaymentCredentials2.id = this.paymentForm.saved_credentials.id;
                inputPaymentCredentials2.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            }
            TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo = this.requestedInfo;
            if (!(tL_payments_validatedRequestedInfo == null || (str = tL_payments_validatedRequestedInfo.id) == null)) {
                tL_payments_sendPaymentForm.requested_info_id = str;
                tL_payments_sendPaymentForm.flags = 1 | tL_payments_sendPaymentForm.flags;
            }
            TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                tL_payments_sendPaymentForm.shipping_option_id = tL_shippingOption.id;
                tL_payments_sendPaymentForm.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendPaymentForm, new RequestDelegate(tL_payments_sendPaymentForm) {
                private final /* synthetic */ TLRPC.TL_payments_sendPaymentForm f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.lambda$sendData$40$PaymentFormActivity(this.f$1, tLObject, tL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$sendData$40$PaymentFormActivity(TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, tL_payments_sendPaymentForm) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.TL_payments_sendPaymentForm f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$39$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        } else if (tLObject instanceof TLRPC.TL_payments_paymentResult) {
            MessagesController.getInstance(this.currentAccount).processUpdates(((TLRPC.TL_payments_paymentResult) tLObject).updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PaymentFormActivity.this.goToNextStep();
                }
            });
        } else if (tLObject instanceof TLRPC.TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$38$PaymentFormActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$38$PaymentFormActivity(TLObject tLObject) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
        setDonePressed(false);
        this.webviewLoading = true;
        showEditDoneProgress(true, true);
        ContextProgressView contextProgressView = this.progressView;
        if (contextProgressView != null) {
            contextProgressView.setVisibility(0);
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setEnabled(false);
            this.doneItem.getContentView().setVisibility(4);
        }
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.setVisibility(0);
            WebView webView3 = this.webView;
            String str = ((TLRPC.TL_payments_paymentVerificationNeeded) tLObject).url;
            this.webViewUrl = str;
            webView3.loadUrl(str);
        }
    }

    public /* synthetic */ void lambda$null$39$PaymentFormActivity(TLRPC.TL_error tL_error, TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
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

    /* access modifiers changed from: private */
    public void setDonePressed(boolean z) {
        this.donePressed = z;
        this.swipeBackEnabled = !z;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.getBackButton().setEnabled(!this.donePressed);
        }
        TextDetailSettingsCell[] textDetailSettingsCellArr = this.detailSettingsCell;
        if (textDetailSettingsCellArr[0] != null) {
            textDetailSettingsCellArr[0].setEnabled(!this.donePressed);
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
    }

    /* access modifiers changed from: private */
    public void checkPassword() {
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
            TLRPC.TL_account_getPassword tL_account_getPassword = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPassword, new RequestDelegate(obj, tL_account_getPassword) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ TLRPC.TL_account_getPassword f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PaymentFormActivity.this.lambda$checkPassword$45$PaymentFormActivity(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$checkPassword$45$PaymentFormActivity(String str, TLRPC.TL_account_getPassword tL_account_getPassword, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, str, tL_account_getPassword) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ TLRPC.TL_account_getPassword f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$44$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$44$PaymentFormActivity(TLRPC.TL_error tL_error, TLObject tLObject, String str, TLRPC.TL_account_getPassword tL_account_getPassword) {
        if (tL_error == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (!tL_account_password.has_password) {
                this.passwordOk = false;
                goToNextStep();
            } else {
                Utilities.globalQueue.postRunnable(new Runnable(tL_account_password, AndroidUtilities.getStringBytes(str)) {
                    private final /* synthetic */ TLRPC.TL_account_password f$1;
                    private final /* synthetic */ byte[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PaymentFormActivity.this.lambda$null$43$PaymentFormActivity(this.f$1, this.f$2);
                    }
                });
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_getPassword, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    public /* synthetic */ void lambda$null$43$PaymentFormActivity(TLRPC.TL_account_password tL_account_password, byte[] bArr) {
        TLRPC.PasswordKdfAlgo passwordKdfAlgo = tL_account_password.current_algo;
        byte[] x = passwordKdfAlgo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo) : null;
        TLRPC.TL_account_getTmpPassword tL_account_getTmpPassword = new TLRPC.TL_account_getTmpPassword();
        tL_account_getTmpPassword.period = 1800;
        $$Lambda$PaymentFormActivity$uhyZXSlwS_E2QJObfK5gOziOlac r1 = new RequestDelegate(tL_account_getTmpPassword) {
            private final /* synthetic */ TLRPC.TL_account_getTmpPassword f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PaymentFormActivity.this.lambda$null$42$PaymentFormActivity(this.f$1, tLObject, tL_error);
            }
        };
        TLRPC.PasswordKdfAlgo passwordKdfAlgo2 = tL_account_password.current_algo;
        if (passwordKdfAlgo2 instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            tL_account_getTmpPassword.password = SRPHelper.startCheck(x, tL_account_password.srp_id, tL_account_password.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo2);
            if (tL_account_getTmpPassword.password == null) {
                TLRPC.TL_error tL_error = new TLRPC.TL_error();
                tL_error.text = "ALGO_INVALID";
                r1.run((TLObject) null, tL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getTmpPassword, r1, 10);
            return;
        }
        TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
        tL_error2.text = "PASSWORD_HASH_INVALID";
        r1.run((TLObject) null, tL_error2);
    }

    public /* synthetic */ void lambda$null$42$PaymentFormActivity(TLRPC.TL_account_getTmpPassword tL_account_getTmpPassword, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tL_error, tL_account_getTmpPassword) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLRPC.TL_account_getTmpPassword f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$41$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$41$PaymentFormActivity(TLObject tLObject, TLRPC.TL_error tL_error, TLRPC.TL_account_getTmpPassword tL_account_getTmpPassword) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (tLObject != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TLRPC.TL_account_tmpPassword) tLObject;
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

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z2;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else if (this.webView != null) {
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        if (!z3) {
                            PaymentFormActivity.this.progressView.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        AnimatorSet unused = PaymentFormActivity.this.doneItemAnimation = null;
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
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[]{1.0f})});
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        if (!z3) {
                            PaymentFormActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.payTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator)) {
                        AnimatorSet unused = PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public boolean onBackPressed() {
        if (!this.shouldNavigateBack) {
            return !this.donePressed;
        }
        this.webView.loadUrl(this.webViewUrl);
        this.shouldNavigateBack = false;
        return false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        if (this.inputFields != null) {
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[i].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                i++;
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        }
        if (this.radioCells != null) {
            int i2 = 0;
            while (true) {
                RadioCell[] radioCellArr = this.radioCells;
                if (i2 >= radioCellArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(radioCellArr[i2], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.radioCells[i2], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription((View) this.radioCells[i2], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.radioCells[i2], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
                arrayList.add(new ThemeDescription((View) this.radioCells[i2], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
                i2++;
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        }
        int i3 = 0;
        while (true) {
            HeaderCell[] headerCellArr = this.headerCell;
            if (i3 >= headerCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(headerCellArr[i3], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.headerCell[i3], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            i3++;
        }
        int i4 = 0;
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (i4 >= shadowSectionCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(shadowSectionCellArr[i4], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            i4++;
        }
        int i5 = 0;
        while (true) {
            TextInfoPrivacyCell[] textInfoPrivacyCellArr = this.bottomCell;
            if (i5 >= textInfoPrivacyCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textInfoPrivacyCellArr[i5], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.bottomCell[i5], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription((View) this.bottomCell[i5], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
            i5++;
        }
        for (int i6 = 0; i6 < this.dividers.size(); i6++) {
            arrayList.add(new ThemeDescription(this.dividers.get(i6), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        }
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.codeFieldCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.codeFieldCell, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        int i7 = 0;
        while (true) {
            TextSettingsCell[] textSettingsCellArr = this.settingsCell;
            if (i7 >= textSettingsCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textSettingsCellArr[i7], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.settingsCell[i7], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription((View) this.settingsCell[i7], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            i7++;
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText6"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        int i8 = 1;
        while (true) {
            TextDetailSettingsCell[] textDetailSettingsCellArr = this.detailSettingsCell;
            if (i8 < textDetailSettingsCellArr.length) {
                arrayList.add(new ThemeDescription(textDetailSettingsCellArr[i8], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription((View) this.detailSettingsCell[i8], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.detailSettingsCell[i8], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
                i8++;
            } else {
                arrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription((View) this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
                arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
        }
    }
}
