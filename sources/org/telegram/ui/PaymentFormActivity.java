package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import org.telegram.PhoneFormat.PhoneFormat;
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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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
import org.telegram.tgnet.TLRPC.TL_labeledPrice;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC.TL_paymentRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC.TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC.TL_payments_paymentResult;
import org.telegram.tgnet.TLRPC.TL_payments_paymentVerficationNeeded;
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
    private String stripeApiKey;
    private TextView textView;
    private String totalPriceDecimal;
    private TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    private boolean webviewLoading;

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TL_account_password tL_account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    public class LinkSpan extends ClickableSpan {
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            PaymentFormActivity.this.presentFragment(new TwoStepVerificationActivity(0));
        }
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        /* synthetic */ TelegramWebviewProxy(PaymentFormActivity x0, AnonymousClass1 x1) {
            this();
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$TelegramWebviewProxy$$Lambda$0(this, eventName, eventData));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(String eventName, String eventData) {
            if (PaymentFormActivity.this.getParentActivity() != null && eventName.equals("payment_form_submit")) {
                try {
                    JSONObject jsonObject = new JSONObject(eventData);
                    PaymentFormActivity.this.paymentJson = jsonObject.getJSONObject("credentials").toString();
                    PaymentFormActivity.this.cardName = jsonObject.getString("title");
                } catch (Throwable e) {
                    PaymentFormActivity.this.paymentJson = eventData;
                    FileLog.e(e);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    /* Access modifiers changed, original: final|bridge|synthetic */
    public final /* bridge */ /* synthetic */ void bridge$lambda$0$PaymentFormActivity() {
        goToNextStep();
    }

    public PaymentFormActivity(MessageObject message, TL_payments_paymentReceipt receipt) {
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
        this.paymentForm.bot_id = receipt.bot_id;
        this.paymentForm.invoice = receipt.invoice;
        this.paymentForm.provider_id = receipt.provider_id;
        this.paymentForm.users = receipt.users;
        this.shippingOption = receipt.shipping;
        this.messageObject = message;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(receipt.bot_id));
        if (this.botUser != null) {
            this.currentBotName = this.botUser.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = message.messageOwner.media.title;
        if (receipt.info != null) {
            this.validateRequest = new TL_payments_validateRequestedInfo();
            this.validateRequest.info = receipt.info;
        }
        this.cardName = receipt.credentials_title;
    }

    public PaymentFormActivity(TL_payments_paymentForm form, MessageObject message) {
        int step;
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
        if (form.invoice.shipping_address_requested || form.invoice.email_requested || form.invoice.name_requested || form.invoice.phone_requested) {
            step = 0;
        } else if (form.saved_credentials != null) {
            if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
            }
            if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                step = 4;
            } else {
                step = 3;
            }
        } else {
            step = 2;
        }
        init(form, message, step, null, null, null, null, null, false, null);
    }

    private PaymentFormActivity(TL_payments_paymentForm form, MessageObject message, int step, TL_payments_validatedRequestedInfo validatedRequestedInfo, TL_shippingOption shipping, String tokenJson, String card, TL_payments_validateRequestedInfo request, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
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
        init(form, message, step, validatedRequestedInfo, shipping, tokenJson, card, request, saveCard, androidPay);
    }

    private void setCurrentPassword(TL_account_password password) {
        if (!password.has_password) {
            this.currentPassword = password;
            if (this.currentPassword != null) {
                this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            }
            updatePasswordFields();
        } else if (getParentActivity() != null) {
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    private void init(TL_payments_paymentForm form, MessageObject message, int step, TL_payments_validatedRequestedInfo validatedRequestedInfo, TL_shippingOption shipping, String tokenJson, String card, TL_payments_validateRequestedInfo request, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
        boolean z = true;
        this.currentStep = step;
        this.paymentJson = tokenJson;
        this.androidPayCredentials = androidPay;
        this.requestedInfo = validatedRequestedInfo;
        this.paymentForm = form;
        this.shippingOption = shipping;
        this.messageObject = message;
        this.saveCardInfo = saveCard;
        this.isWebView = !"stripe".equals(this.paymentForm.native_provider);
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(form.bot_id));
        if (this.botUser != null) {
            this.currentBotName = this.botUser.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = message.messageOwner.media.title;
        this.validateRequest = request;
        this.saveShippingInfo = true;
        if (saveCard) {
            this.saveCardInfo = saveCard;
        } else {
            if (this.paymentForm.saved_credentials == null) {
                z = false;
            }
            this.saveCardInfo = z;
        }
        if (card != null) {
            this.cardName = card;
        } else if (form.saved_credentials != null) {
            this.cardName = form.saved_credentials.title;
        }
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
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (this.googleApiClient != null) {
            this.googleApiClient.connect();
        }
    }

    public void onPause() {
        if (this.googleApiClient != null) {
            this.googleApiClient.disconnect();
        }
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:380:0x1182, code skipped:
            r48.need_card_name = false;
     */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public android.view.View createView(android.content.Context r49) {
        /*
        r48 = this;
        r0 = r48;
        r4 = r0.currentStep;
        if (r4 != 0) goto L_0x0433;
    L_0x0006:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentShippingInfo";
        r6 = NUM; // 0x7f0CLASSNAMEe float:1.8612985E38 double:1.0530983224E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
    L_0x0017:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = NUM; // 0x7var_b float:1.7944892E38 double:1.0529355796E-314;
        r4.setBackButtonImage(r5);
        r0 = r48;
        r4 = r0.actionBar;
        r5 = 1;
        r4.setAllowOverlayTitle(r5);
        r0 = r48;
        r4 = r0.actionBar;
        r5 = new org.telegram.ui.PaymentFormActivity$1;
        r0 = r48;
        r5.<init>();
        r4.setActionBarMenuOnItemClick(r5);
        r0 = r48;
        r4 = r0.actionBar;
        r33 = r4.createMenu();
        r0 = r48;
        r4 = r0.currentStep;
        if (r4 == 0) goto L_0x0068;
    L_0x0045:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 1;
        if (r4 == r5) goto L_0x0068;
    L_0x004c:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 2;
        if (r4 == r5) goto L_0x0068;
    L_0x0053:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 3;
        if (r4 == r5) goto L_0x0068;
    L_0x005a:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 == r5) goto L_0x0068;
    L_0x0061:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 6;
        if (r4 != r5) goto L_0x00be;
    L_0x0068:
        r4 = 1;
        r5 = NUM; // 0x7var_b9 float:1.7944953E38 double:1.0529355944E-314;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r33;
        r4 = r0.addItemWithWidth(r4, r5, r6);
        r0 = r48;
        r0.doneItem = r4;
        r4 = new org.telegram.ui.Components.ContextProgressView;
        r5 = 1;
        r0 = r49;
        r4.<init>(r0, r5);
        r0 = r48;
        r0.progressView = r4;
        r0 = r48;
        r4 = r0.progressView;
        r5 = 0;
        r4.setAlpha(r5);
        r0 = r48;
        r4 = r0.progressView;
        r5 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r4.setScaleX(r5);
        r0 = r48;
        r4 = r0.progressView;
        r5 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r4.setScaleY(r5);
        r0 = r48;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.doneItem;
        r0 = r48;
        r5 = r0.progressView;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
    L_0x00be:
        r4 = new android.widget.FrameLayout;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.fragmentView = r4;
        r0 = r48;
        r0 = r0.fragmentView;
        r27 = r0;
        r27 = (android.widget.FrameLayout) r27;
        r0 = r48;
        r4 = r0.fragmentView;
        r5 = "windowBackgroundGray";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = new android.widget.ScrollView;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.scrollView = r4;
        r0 = r48;
        r4 = r0.scrollView;
        r5 = 1;
        r4.setFillViewport(r5);
        r0 = r48;
        r4 = r0.scrollView;
        r5 = "actionBarDefault";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor(r4, r5);
        r0 = r48;
        r0 = r0.scrollView;
        r46 = r0;
        r4 = -1;
        r5 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = 51;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r0 = r48;
        r10 = r0.currentStep;
        r47 = 4;
        r0 = r47;
        if (r10 != r0) goto L_0x0531;
    L_0x0118:
        r10 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x011a:
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r27;
        r1 = r46;
        r0.addView(r1, r4);
        r4 = new android.widget.LinearLayout;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.linearLayout2 = r4;
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = 1;
        r4.setOrientation(r5);
        r0 = r48;
        r4 = r0.scrollView;
        r0 = r48;
        r5 = r0.linearLayout2;
        r6 = new android.widget.FrameLayout$LayoutParams;
        r7 = -1;
        r8 = -2;
        r6.<init>(r7, r8);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.currentStep;
        if (r4 != 0) goto L_0x0ce7;
    L_0x0150:
        r31 = new java.util.HashMap;
        r31.<init>();
        r22 = new java.util.HashMap;
        r22.<init>();
        r39 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x01d1 }
        r4 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x01d1 }
        r5 = r49.getResources();	 Catch:{ Exception -> 0x01d1 }
        r5 = r5.getAssets();	 Catch:{ Exception -> 0x01d1 }
        r6 = "countries.txt";
        r5 = r5.open(r6);	 Catch:{ Exception -> 0x01d1 }
        r4.<init>(r5);	 Catch:{ Exception -> 0x01d1 }
        r0 = r39;
        r0.<init>(r4);	 Catch:{ Exception -> 0x01d1 }
    L_0x0175:
        r32 = r39.readLine();	 Catch:{ Exception -> 0x01d1 }
        if (r32 == 0) goto L_0x0534;
    L_0x017b:
        r4 = ";";
        r0 = r32;
        r15 = r0.split(r4);	 Catch:{ Exception -> 0x01d1 }
        r0 = r48;
        r4 = r0.countriesArray;	 Catch:{ Exception -> 0x01d1 }
        r5 = 0;
        r6 = 2;
        r6 = r15[r6];	 Catch:{ Exception -> 0x01d1 }
        r4.add(r5, r6);	 Catch:{ Exception -> 0x01d1 }
        r0 = r48;
        r4 = r0.countriesMap;	 Catch:{ Exception -> 0x01d1 }
        r5 = 2;
        r5 = r15[r5];	 Catch:{ Exception -> 0x01d1 }
        r6 = 0;
        r6 = r15[r6];	 Catch:{ Exception -> 0x01d1 }
        r4.put(r5, r6);	 Catch:{ Exception -> 0x01d1 }
        r0 = r48;
        r4 = r0.codesMap;	 Catch:{ Exception -> 0x01d1 }
        r5 = 0;
        r5 = r15[r5];	 Catch:{ Exception -> 0x01d1 }
        r6 = 2;
        r6 = r15[r6];	 Catch:{ Exception -> 0x01d1 }
        r4.put(r5, r6);	 Catch:{ Exception -> 0x01d1 }
        r4 = 1;
        r4 = r15[r4];	 Catch:{ Exception -> 0x01d1 }
        r5 = 2;
        r5 = r15[r5];	 Catch:{ Exception -> 0x01d1 }
        r0 = r22;
        r0.put(r4, r5);	 Catch:{ Exception -> 0x01d1 }
        r4 = r15.length;	 Catch:{ Exception -> 0x01d1 }
        r5 = 3;
        if (r4 <= r5) goto L_0x01c5;
    L_0x01b8:
        r0 = r48;
        r4 = r0.phoneFormatMap;	 Catch:{ Exception -> 0x01d1 }
        r5 = 0;
        r5 = r15[r5];	 Catch:{ Exception -> 0x01d1 }
        r6 = 3;
        r6 = r15[r6];	 Catch:{ Exception -> 0x01d1 }
        r4.put(r5, r6);	 Catch:{ Exception -> 0x01d1 }
    L_0x01c5:
        r4 = 1;
        r4 = r15[r4];	 Catch:{ Exception -> 0x01d1 }
        r5 = 2;
        r5 = r15[r5];	 Catch:{ Exception -> 0x01d1 }
        r0 = r31;
        r0.put(r4, r5);	 Catch:{ Exception -> 0x01d1 }
        goto L_0x0175;
    L_0x01d1:
        r25 = move-exception;
        org.telegram.messenger.FileLog.e(r25);
    L_0x01d5:
        r0 = r48;
        r4 = r0.countriesArray;
        r5 = org.telegram.ui.PaymentFormActivity$$Lambda$0.$instance;
        java.util.Collections.sort(r4, r5);
        r4 = 10;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r48;
        r0.inputFields = r4;
        r11 = 0;
    L_0x01e7:
        r4 = 10;
        if (r11 >= r4) goto L_0x0a62;
    L_0x01eb:
        if (r11 != 0) goto L_0x0539;
    L_0x01ed:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentShippingAddress";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612973E38 double:1.0530983194E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x0234:
        r4 = 8;
        if (r11 != r4) goto L_0x05a7;
    L_0x0238:
        r18 = new android.widget.LinearLayout;
        r0 = r18;
        r1 = r49;
        r0.<init>(r1);
        r4 = r18;
        r4 = (android.widget.LinearLayout) r4;
        r5 = 0;
        r4.setOrientation(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 50;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
    L_0x0265:
        r4 = 9;
        if (r11 != r4) goto L_0x0647;
    L_0x0269:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.HintEditText;
        r0 = r49;
        r5.<init>(r0);
        r4[r11] = r5;
    L_0x0276:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        r4 = 4;
        if (r11 != r4) goto L_0x0300;
    L_0x02e6:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$1;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnTouchListener(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setInputType(r5);
    L_0x0300:
        r4 = 9;
        if (r11 == r4) goto L_0x0308;
    L_0x0304:
        r4 = 8;
        if (r11 != r4) goto L_0x0656;
    L_0x0308:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 3;
        r4.setInputType(r5);
    L_0x0312:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r5);
        switch(r11) {
            case 0: goto L_0x06e2;
            case 1: goto L_0x071c;
            case 2: goto L_0x0756;
            case 3: goto L_0x0790;
            case 4: goto L_0x07ca;
            case 5: goto L_0x0825;
            case 6: goto L_0x0672;
            case 7: goto L_0x06aa;
            default: goto L_0x0321;
        };
    L_0x0321:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.inputFields;
        r5 = r5[r11];
        r5 = r5.length();
        r4.setSelection(r5);
        r4 = 8;
        if (r11 != r4) goto L_0x085f;
    L_0x0338:
        r4 = new android.widget.TextView;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.textView = r4;
        r0 = r48;
        r4 = r0.textView;
        r5 = "+";
        r4.setText(r5);
        r0 = r48;
        r4 = r0.textView;
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.textView;
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r48;
        r10 = r0.textView;
        r4 = -2;
        r5 = -2;
        r6 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = 0;
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5, r6, r7, r8, r9);
        r0 = r18;
        r0.addView(r10, r4);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r4.setPadding(r5, r6, r7, r8);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 19;
        r4.setGravity(r5);
        r4 = 1;
        r0 = new android.text.InputFilter[r4];
        r29 = r0;
        r4 = 0;
        r5 = new android.text.InputFilter$LengthFilter;
        r6 = 5;
        r5.<init>(r6);
        r29[r4] = r5;
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r29;
        r4.setFilters(r0);
        r0 = r48;
        r4 = r0.inputFields;
        r10 = r4[r11];
        r4 = 55;
        r5 = -2;
        r6 = 0;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5, r6, r7, r8, r9);
        r0 = r18;
        r0.addView(r10, r4);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$3;
        r0 = r48;
        r5.<init>();
        r4.addTextChangedListener(r5);
    L_0x03da:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$2;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 9;
        if (r11 != r4) goto L_0x09f3;
    L_0x03ee:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 != 0) goto L_0x0402;
    L_0x03f8:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_to_provider;
        if (r4 == 0) goto L_0x0a3e;
    L_0x0402:
        r38 = 0;
        r17 = 0;
    L_0x0406:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r4 = r4.size();
        r0 = r17;
        if (r0 >= r4) goto L_0x08e7;
    L_0x0414:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r0 = r17;
        r44 = r4.get(r0);
        r44 = (org.telegram.tgnet.TLRPC.User) r44;
        r0 = r44;
        r4 = r0.id;
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.provider_id;
        if (r4 != r5) goto L_0x0430;
    L_0x042e:
        r38 = r44;
    L_0x0430:
        r17 = r17 + 1;
        goto L_0x0406;
    L_0x0433:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 1;
        if (r4 != r5) goto L_0x044d;
    L_0x043a:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentShippingMethod";
        r6 = NUM; // 0x7f0CLASSNAMEf float:1.8612987E38 double:1.053098323E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x044d:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 2;
        if (r4 != r5) goto L_0x0467;
    L_0x0454:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentCardInfo";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612904E38 double:1.0530983026E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0467:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 3;
        if (r4 != r5) goto L_0x0481;
    L_0x046e:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentCardInfo";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612904E38 double:1.0530983026E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0481:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 != r5) goto L_0x04cc;
    L_0x0488:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.test;
        if (r4 == 0) goto L_0x04b9;
    L_0x0492:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Test ";
        r5 = r5.append(r6);
        r6 = "PaymentCheckout";
        r7 = NUM; // 0x7f0CLASSNAMEd float:1.8612918E38 double:1.053098306E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x04b9:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentCheckout";
        r6 = NUM; // 0x7f0CLASSNAMEd float:1.8612918E38 double:1.053098306E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x04cc:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 5;
        if (r4 != r5) goto L_0x0517;
    L_0x04d3:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.test;
        if (r4 == 0) goto L_0x0504;
    L_0x04dd:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Test ";
        r5 = r5.append(r6);
        r6 = "PaymentReceipt";
        r7 = NUM; // 0x7f0CLASSNAME float:1.861297E38 double:1.053098319E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0504:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentReceipt";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861297E38 double:1.053098319E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0517:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 6;
        if (r4 != r5) goto L_0x0017;
    L_0x051e:
        r0 = r48;
        r4 = r0.actionBar;
        r5 = "PaymentPassword";
        r6 = NUM; // 0x7f0CLASSNAMEc float:1.8612948E38 double:1.0530983135E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0531:
        r10 = 0;
        goto L_0x011a;
    L_0x0534:
        r39.close();	 Catch:{ Exception -> 0x01d1 }
        goto L_0x01d5;
    L_0x0539:
        r4 = 6;
        if (r11 != r4) goto L_0x0234;
    L_0x053c:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentShippingReceiver";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612993E38 double:1.0530983243E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0234;
    L_0x05a7:
        r4 = 9;
        if (r11 != r4) goto L_0x05bb;
    L_0x05ab:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 8;
        r4 = r4[r5];
        r18 = r4.getParent();
        r18 = (android.view.ViewGroup) r18;
        goto L_0x0265;
    L_0x05bb:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r49;
        r0.<init>(r1);
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 50;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        r4 = 5;
        if (r11 == r4) goto L_0x062c;
    L_0x05e3:
        r4 = 9;
        if (r11 == r4) goto L_0x062c;
    L_0x05e7:
        r13 = 1;
    L_0x05e8:
        if (r13 == 0) goto L_0x05f8;
    L_0x05ea:
        r4 = 7;
        if (r11 != r4) goto L_0x062e;
    L_0x05ed:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x062e;
    L_0x05f7:
        r13 = 0;
    L_0x05f8:
        if (r13 == 0) goto L_0x0265;
    L_0x05fa:
        r24 = new org.telegram.ui.PaymentFormActivity$2;
        r0 = r24;
        r1 = r48;
        r2 = r49;
        r0.<init>(r2);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r0 = r48;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
        goto L_0x0265;
    L_0x062c:
        r13 = 0;
        goto L_0x05e8;
    L_0x062e:
        r4 = 6;
        if (r11 != r4) goto L_0x05f8;
    L_0x0631:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x05f8;
    L_0x063b:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 != 0) goto L_0x05f8;
    L_0x0645:
        r13 = 0;
        goto L_0x05f8;
    L_0x0647:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r49;
        r5.<init>(r0);
        r4[r11] = r5;
        goto L_0x0276;
    L_0x0656:
        r4 = 7;
        if (r11 != r4) goto L_0x0665;
    L_0x0659:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r4.setInputType(r5);
        goto L_0x0312;
    L_0x0665:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r4.setInputType(r5);
        goto L_0x0312;
    L_0x0672:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingName";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612989E38 double:1.0530983233E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x068d:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.name;
        if (r4 == 0) goto L_0x0321;
    L_0x0697:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.name;
        r4.setText(r5);
        goto L_0x0321;
    L_0x06aa:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingEmailPlaceholder";
        r6 = NUM; // 0x7f0CLASSNAMEd float:1.8612983E38 double:1.053098322E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x06c5:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.email;
        if (r4 == 0) goto L_0x0321;
    L_0x06cf:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.email;
        r4.setText(r5);
        goto L_0x0321;
    L_0x06e2:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingAddress1Placeholder";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612975E38 double:1.05309832E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x06fd:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0321;
    L_0x0707:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.street_line1;
        r4.setText(r5);
        goto L_0x0321;
    L_0x071c:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingAddress2Placeholder";
        r6 = NUM; // 0x7f0CLASSNAMEa float:1.8612977E38 double:1.0530983204E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x0737:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0321;
    L_0x0741:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.street_line2;
        r4.setText(r5);
        goto L_0x0321;
    L_0x0756:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingCityPlaceholder";
        r6 = NUM; // 0x7f0CLASSNAMEb float:1.8612979E38 double:1.053098321E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x0771:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0321;
    L_0x077b:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.city;
        r4.setText(r5);
        goto L_0x0321;
    L_0x0790:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingStatePlaceholder";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612999E38 double:1.053098326E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x07ab:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0321;
    L_0x07b5:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.state;
        r4.setText(r5);
        goto L_0x0321;
    L_0x07ca:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingCountry";
        r6 = NUM; // 0x7f0CLASSNAMEc float:1.861298E38 double:1.0530983214E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x07e5:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0321;
    L_0x07ef:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        r4 = r4.country_iso2;
        r0 = r22;
        r45 = r0.get(r4);
        r45 = (java.lang.String) r45;
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        r4 = r4.country_iso2;
        r0 = r48;
        r0.countryName = r4;
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        if (r45 == 0) goto L_0x081e;
    L_0x0817:
        r0 = r45;
        r4.setText(r0);
        goto L_0x0321;
    L_0x081e:
        r0 = r48;
        r0 = r0.countryName;
        r45 = r0;
        goto L_0x0817;
    L_0x0825:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingZipPlaceholder";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8613E38 double:1.0530983263E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0321;
    L_0x0840:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0321;
    L_0x084a:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.post_code;
        r4.setText(r5);
        goto L_0x0321;
    L_0x085f:
        r4 = 9;
        if (r11 != r4) goto L_0x08a5;
    L_0x0863:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r4.setPadding(r5, r6, r7, r8);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 19;
        r4.setGravity(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r10 = r4[r11];
        r4 = -1;
        r5 = -2;
        r6 = 0;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r9 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5, r6, r7, r8, r9);
        r0 = r18;
        r0.addView(r10, r4);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$4;
        r0 = r48;
        r5.<init>();
        r4.addTextChangedListener(r5);
        goto L_0x03da;
    L_0x08a5:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x08e5;
    L_0x08c1:
        r4 = 5;
    L_0x08c2:
        r5.setGravity(r4);
        r0 = r48;
        r4 = r0.inputFields;
        r46 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r10 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r46;
        r0.addView(r1, r4);
        goto L_0x03da;
    L_0x08e5:
        r4 = 3;
        goto L_0x08c2;
    L_0x08e7:
        if (r38 == 0) goto L_0x09f7;
    L_0x08e9:
        r0 = r38;
        r4 = r0.first_name;
        r0 = r38;
        r5 = r0.last_name;
        r37 = org.telegram.messenger.ContactsController.formatName(r4, r5);
    L_0x08f5:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 == 0) goto L_0x09fc;
    L_0x0937:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_to_provider;
        if (r4 == 0) goto L_0x09fc;
    L_0x0941:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPhoneEmailToProvider";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612964E38 double:1.0530983174E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r37;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
    L_0x095b:
        r4 = new org.telegram.ui.Cells.TextCheckCell;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.checkCell1 = r4;
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = "PaymentShippingSave";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612995E38 double:1.053098325E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r0 = r48;
        r6 = r0.saveShippingInfo;
        r7 = 0;
        r4.setTextAndCheck(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.checkCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$3;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentShippingSaveInfo";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612997E38 double:1.0530983253E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x09f3:
        r11 = r11 + 1;
        goto L_0x01e7;
    L_0x09f7:
        r37 = "";
        goto L_0x08f5;
    L_0x09fc:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 == 0) goto L_0x0a22;
    L_0x0a06:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentEmailToProvider";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861294E38 double:1.0530983115E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r37;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        goto L_0x095b;
    L_0x0a22:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPhoneToProvider";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612966E38 double:1.053098318E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r37;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        goto L_0x095b;
    L_0x0a3e:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x095b;
    L_0x0a62:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0a7e;
    L_0x0a6c:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 6;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0a7e:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0a9b;
    L_0x0a88:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 8;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0a9b:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 != 0) goto L_0x0ab7;
    L_0x0aa5:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 7;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0ab7:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0ac1:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 9;
        r4 = r4[r5];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
    L_0x0acf:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 1;
        r4 = r4[r5];
        if (r4 == 0) goto L_0x0c9e;
    L_0x0ad8:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 1;
        r5 = r4[r5];
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0afd;
    L_0x0ae9:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0afd;
    L_0x0af3:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0c9a;
    L_0x0afd:
        r4 = 0;
    L_0x0afe:
        r5.setVisibility(r4);
    L_0x0b01:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r5 = r4[r5];
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0b26;
    L_0x0b12:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0b26;
    L_0x0b1c:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0cd5;
    L_0x0b26:
        r4 = 0;
    L_0x0b27:
        r5.setVisibility(r4);
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.shipping_address_requested;
        if (r4 != 0) goto L_0x0bb8;
    L_0x0b34:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 0;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 1;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 2;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 3;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 4;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 5;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0bb8:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0cd9;
    L_0x0bc0:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.phone;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0cd9;
    L_0x0bce:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.phone;
        r0 = r48;
        r0.fillNumber(r4);
    L_0x0bdb:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 8;
        r4 = r4[r5];
        r4 = r4.length();
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x0be9:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0bf3:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0bfb:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.phone;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r21 = 0;
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0ce1 }
        r5 = "phone";
        r42 = r4.getSystemService(r5);	 Catch:{ Exception -> 0x0ce1 }
        r42 = (android.telephony.TelephonyManager) r42;	 Catch:{ Exception -> 0x0ce1 }
        if (r42 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = r42.getSimCountryIso();	 Catch:{ Exception -> 0x0ce1 }
        r21 = r4.toUpperCase();	 Catch:{ Exception -> 0x0ce1 }
    L_0x0CLASSNAME:
        if (r21 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r31;
        r1 = r21;
        r23 = r0.get(r1);
        r23 = (java.lang.String) r23;
        if (r23 == 0) goto L_0x0CLASSNAME;
    L_0x0c2e:
        r0 = r48;
        r4 = r0.countriesArray;
        r0 = r23;
        r28 = r4.indexOf(r0);
        r4 = -1;
        r0 = r28;
        if (r0 == r4) goto L_0x0CLASSNAME;
    L_0x0c3d:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 8;
        r5 = r4[r5];
        r0 = r48;
        r4 = r0.countriesMap;
        r0 = r23;
        r4 = r4.get(r0);
        r4 = (java.lang.CharSequence) r4;
        r5.setText(r4);
    L_0x0CLASSNAME:
        r0 = r48;
        r4 = r0.fragmentView;
        return r4;
    L_0x0CLASSNAME:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 7;
        r4 = r4[r5];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0acf;
    L_0x0CLASSNAME:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 == 0) goto L_0x0c8b;
    L_0x0c7c:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 6;
        r4 = r4[r5];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0acf;
    L_0x0c8b:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 5;
        r4 = r4[r5];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0acf;
    L_0x0c9a:
        r4 = 8;
        goto L_0x0afe;
    L_0x0c9e:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        if (r4 == 0) goto L_0x0b01;
    L_0x0ca7:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r5 = r4[r5];
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0ccc;
    L_0x0cb8:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0ccc;
    L_0x0cc2:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0cd2;
    L_0x0ccc:
        r4 = 0;
    L_0x0ccd:
        r5.setVisibility(r4);
        goto L_0x0b01;
    L_0x0cd2:
        r4 = 8;
        goto L_0x0ccd;
    L_0x0cd5:
        r4 = 8;
        goto L_0x0b27;
    L_0x0cd9:
        r4 = 0;
        r0 = r48;
        r0.fillNumber(r4);
        goto L_0x0bdb;
    L_0x0ce1:
        r25 = move-exception;
        org.telegram.messenger.FileLog.e(r25);
        goto L_0x0CLASSNAME;
    L_0x0ce7:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 2;
        if (r4 != r5) goto L_0x1410;
    L_0x0cee:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.native_params;
        if (r4 == 0) goto L_0x0d35;
    L_0x0cf6:
        r30 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0ed7 }
        r0 = r48;
        r4 = r0.paymentForm;	 Catch:{ Exception -> 0x0ed7 }
        r4 = r4.native_params;	 Catch:{ Exception -> 0x0ed7 }
        r4 = r4.data;	 Catch:{ Exception -> 0x0ed7 }
        r0 = r30;
        r0.<init>(r4);	 Catch:{ Exception -> 0x0ed7 }
        r4 = "android_pay_public_key";
        r0 = r30;
        r14 = r0.getString(r4);	 Catch:{ Exception -> 0x0ecf }
        r4 = android.text.TextUtils.isEmpty(r14);	 Catch:{ Exception -> 0x0ecf }
        if (r4 != 0) goto L_0x0d18;
    L_0x0d14:
        r0 = r48;
        r0.androidPayPublicKey = r14;	 Catch:{ Exception -> 0x0ecf }
    L_0x0d18:
        r4 = "android_pay_bgcolor";
        r0 = r30;
        r4 = r0.getInt(r4);	 Catch:{ Exception -> 0x0edd }
        r5 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r5;
        r0 = r48;
        r0.androidPayBackgroundColor = r4;	 Catch:{ Exception -> 0x0edd }
    L_0x0d28:
        r4 = "android_pay_inverse";
        r0 = r30;
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x0ee5 }
        r0 = r48;
        r0.androidPayBlackTheme = r4;	 Catch:{ Exception -> 0x0ee5 }
    L_0x0d35:
        r0 = r48;
        r4 = r0.isWebView;
        if (r4 == 0) goto L_0x0eed;
    L_0x0d3b:
        r0 = r48;
        r4 = r0.androidPayPublicKey;
        if (r4 == 0) goto L_0x0d44;
    L_0x0d41:
        r48.initAndroidPay(r49);
    L_0x0d44:
        r4 = new android.widget.FrameLayout;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.androidPayContainer = r4;
        r0 = r48;
        r4 = r0.androidPayContainer;
        r5 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r4.setId(r5);
        r0 = r48;
        r4 = r0.androidPayContainer;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.androidPayContainer;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.androidPayContainer;
        r6 = -1;
        r7 = 50;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = 1;
        r0 = r48;
        r0.webviewLoading = r4;
        r4 = 1;
        r5 = 1;
        r0 = r48;
        r0.showEditDoneProgress(r4, r5);
        r0 = r48;
        r4 = r0.progressView;
        r5 = 0;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.doneItem;
        r5 = 0;
        r4.setEnabled(r5);
        r0 = r48;
        r4 = r0.doneItem;
        r4 = r4.getImageView();
        r5 = 4;
        r4.setVisibility(r5);
        r4 = new org.telegram.ui.PaymentFormActivity$5;
        r0 = r48;
        r1 = r49;
        r4.<init>(r1);
        r0 = r48;
        r0.webView = r4;
        r0 = r48;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setJavaScriptEnabled(r5);
        r0 = r48;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setDomStorageEnabled(r5);
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x0dec;
    L_0x0dd2:
        r0 = r48;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 0;
        r4.setMixedContentMode(r5);
        r19 = android.webkit.CookieManager.getInstance();
        r0 = r48;
        r4 = r0.webView;
        r5 = 1;
        r0 = r19;
        r0.setAcceptThirdPartyCookies(r4, r5);
    L_0x0dec:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 17;
        if (r4 < r5) goto L_0x0e04;
    L_0x0df2:
        r0 = r48;
        r4 = r0.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy;
        r6 = 0;
        r0 = r48;
        r5.<init>(r0, r6);
        r6 = "TelegramWebviewProxy";
        r4.addJavascriptInterface(r5, r6);
    L_0x0e04:
        r0 = r48;
        r4 = r0.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$6;
        r0 = r48;
        r5.<init>();
        r4.setWebViewClient(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.webView;
        r6 = -1;
        r7 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 2;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextCheckCell;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.checkCell1 = r4;
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = "PaymentCardSavePaymentInformation";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861291E38 double:1.053098304E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r0 = r48;
        r6 = r0.saveCardInfo;
        r7 = 0;
        r4.setTextAndCheck(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.checkCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$4;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r48.updateSavePaymentField();
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0CLASSNAME;
    L_0x0ecf:
        r25 = move-exception;
        r4 = 0;
        r0 = r48;
        r0.androidPayPublicKey = r4;	 Catch:{ Exception -> 0x0ed7 }
        goto L_0x0d18;
    L_0x0ed7:
        r25 = move-exception;
        org.telegram.messenger.FileLog.e(r25);
        goto L_0x0d35;
    L_0x0edd:
        r25 = move-exception;
        r4 = -1;
        r0 = r48;
        r0.androidPayBackgroundColor = r4;	 Catch:{ Exception -> 0x0ed7 }
        goto L_0x0d28;
    L_0x0ee5:
        r25 = move-exception;
        r4 = 0;
        r0 = r48;
        r0.androidPayBlackTheme = r4;	 Catch:{ Exception -> 0x0ed7 }
        goto L_0x0d35;
    L_0x0eed:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.native_params;
        if (r4 == 0) goto L_0x0var_;
    L_0x0ef5:
        r30 = new org.json.JSONObject;	 Catch:{ Exception -> 0x1173 }
        r0 = r48;
        r4 = r0.paymentForm;	 Catch:{ Exception -> 0x1173 }
        r4 = r4.native_params;	 Catch:{ Exception -> 0x1173 }
        r4 = r4.data;	 Catch:{ Exception -> 0x1173 }
        r0 = r30;
        r0.<init>(r4);	 Catch:{ Exception -> 0x1173 }
        r4 = "need_country";
        r0 = r30;
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x116b }
        r0 = r48;
        r0.need_card_country = r4;	 Catch:{ Exception -> 0x116b }
    L_0x0var_:
        r4 = "need_zip";
        r0 = r30;
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x1179 }
        r0 = r48;
        r0.need_card_postcode = r4;	 Catch:{ Exception -> 0x1179 }
    L_0x0f1e:
        r4 = "need_cardholder_name";
        r0 = r30;
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x1181 }
        r0 = r48;
        r0.need_card_name = r4;	 Catch:{ Exception -> 0x1181 }
    L_0x0f2b:
        r4 = "publishable_key";
        r0 = r30;
        r4 = r0.getString(r4);	 Catch:{ Exception -> 0x1189 }
        r0 = r48;
        r0.stripeApiKey = r4;	 Catch:{ Exception -> 0x1189 }
    L_0x0var_:
        r48.initAndroidPay(r49);
        r4 = 6;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r48;
        r0.inputFields = r4;
        r11 = 0;
    L_0x0var_:
        r4 = 6;
        if (r11 >= r4) goto L_0x13c8;
    L_0x0var_:
        if (r11 != 0) goto L_0x1193;
    L_0x0var_:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentCardTitle";
        r6 = NUM; // 0x7f0CLASSNAMEc float:1.8612916E38 double:1.0530983056E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x0f8f:
        r4 = 3;
        if (r11 == r4) goto L_0x11df;
    L_0x0var_:
        r4 = 5;
        if (r11 == r4) goto L_0x11df;
    L_0x0var_:
        r4 = 4;
        if (r11 != r4) goto L_0x0f9e;
    L_0x0var_:
        r0 = r48;
        r4 = r0.need_card_postcode;
        if (r4 == 0) goto L_0x11df;
    L_0x0f9e:
        r13 = 1;
    L_0x0f9f:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r49;
        r0.<init>(r1);
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 50;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        r34 = 0;
        r0 = r48;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r49;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        r4 = 3;
        if (r11 != r4) goto L_0x11e2;
    L_0x1043:
        r4 = 1;
        r0 = new android.text.InputFilter[r4];
        r29 = r0;
        r4 = 0;
        r5 = new android.text.InputFilter$LengthFilter;
        r6 = 3;
        r5.<init>(r6);
        r29[r4] = r5;
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r29;
        r4.setFilters(r0);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        r4.setInputType(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.text.method.PasswordTransformationMethod.getInstance();
        r4.setTransformationMethod(r5);
    L_0x107f:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r5);
        switch(r11) {
            case 0: goto L_0x123c;
            case 1: goto L_0x1266;
            case 2: goto L_0x127b;
            case 3: goto L_0x1251;
            case 4: goto L_0x12a5;
            case 5: goto L_0x1290;
            default: goto L_0x108e;
        };
    L_0x108e:
        if (r11 != 0) goto L_0x12ba;
    L_0x1090:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$7;
        r0 = r48;
        r5.<init>();
        r4.addTextChangedListener(r5);
    L_0x10a0:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x12cf;
    L_0x10bc:
        r4 = 5;
    L_0x10bd:
        r5.setGravity(r4);
        r0 = r48;
        r4 = r0.inputFields;
        r46 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r10 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r46;
        r0.addView(r1, r4);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$6;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 3;
        if (r11 != r4) goto L_0x12d2;
    L_0x10f1:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x1113:
        if (r13 == 0) goto L_0x1145;
    L_0x1115:
        r24 = new org.telegram.ui.PaymentFormActivity$9;
        r0 = r24;
        r1 = r48;
        r2 = r49;
        r0.<init>(r2);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r0 = r48;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
    L_0x1145:
        r4 = 4;
        if (r11 != r4) goto L_0x114e;
    L_0x1148:
        r0 = r48;
        r4 = r0.need_card_country;
        if (r4 == 0) goto L_0x1160;
    L_0x114e:
        r4 = 5;
        if (r11 != r4) goto L_0x1157;
    L_0x1151:
        r0 = r48;
        r4 = r0.need_card_postcode;
        if (r4 == 0) goto L_0x1160;
    L_0x1157:
        r4 = 2;
        if (r11 != r4) goto L_0x1167;
    L_0x115a:
        r0 = r48;
        r4 = r0.need_card_name;
        if (r4 != 0) goto L_0x1167;
    L_0x1160:
        r4 = 8;
        r0 = r18;
        r0.setVisibility(r4);
    L_0x1167:
        r11 = r11 + 1;
        goto L_0x0var_;
    L_0x116b:
        r25 = move-exception;
        r4 = 0;
        r0 = r48;
        r0.need_card_country = r4;	 Catch:{ Exception -> 0x1173 }
        goto L_0x0var_;
    L_0x1173:
        r25 = move-exception;
        org.telegram.messenger.FileLog.e(r25);
        goto L_0x0var_;
    L_0x1179:
        r25 = move-exception;
        r4 = 0;
        r0 = r48;
        r0.need_card_postcode = r4;	 Catch:{ Exception -> 0x1173 }
        goto L_0x0f1e;
    L_0x1181:
        r25 = move-exception;
        r4 = 0;
        r0 = r48;
        r0.need_card_name = r4;	 Catch:{ Exception -> 0x1173 }
        goto L_0x0f2b;
    L_0x1189:
        r25 = move-exception;
        r4 = "";
        r0 = r48;
        r0.stripeApiKey = r4;	 Catch:{ Exception -> 0x1173 }
        goto L_0x0var_;
    L_0x1193:
        r4 = 4;
        if (r11 != r4) goto L_0x0f8f;
    L_0x1196:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentBillingAddress";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612898E38 double:1.053098301E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0f8f;
    L_0x11df:
        r13 = 0;
        goto L_0x0f9f;
    L_0x11e2:
        if (r11 != 0) goto L_0x11f0;
    L_0x11e4:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 3;
        r4.setInputType(r5);
        goto L_0x107f;
    L_0x11f0:
        r4 = 4;
        if (r11 != r4) goto L_0x120f;
    L_0x11f3:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$5;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnTouchListener(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setInputType(r5);
        goto L_0x107f;
    L_0x120f:
        r4 = 1;
        if (r11 != r4) goto L_0x121f;
    L_0x1212:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 16386; // 0x4002 float:2.2962E-41 double:8.096E-320;
        r4.setInputType(r5);
        goto L_0x107f;
    L_0x121f:
        r4 = 2;
        if (r11 != r4) goto L_0x122f;
    L_0x1222:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 4097; // 0x1001 float:5.741E-42 double:2.024E-320;
        r4.setInputType(r5);
        goto L_0x107f;
    L_0x122f:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r4.setInputType(r5);
        goto L_0x107f;
    L_0x123c:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardNumber";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612908E38 double:1.0530983036E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x108e;
    L_0x1251:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardCvv";
        r6 = NUM; // 0x7f0CLASSNAME float:1.86129E38 double:1.0530983016E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x108e;
    L_0x1266:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardExpireDate";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612902E38 double:1.053098302E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x108e;
    L_0x127b:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardName";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612906E38 double:1.053098303E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x108e;
    L_0x1290:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingZipPlaceholder";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8613E38 double:1.0530983263E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x108e;
    L_0x12a5:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingCountry";
        r6 = NUM; // 0x7f0CLASSNAMEc float:1.861298E38 double:1.0530983214E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x108e;
    L_0x12ba:
        r4 = 1;
        if (r11 != r4) goto L_0x10a0;
    L_0x12bd:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$8;
        r0 = r48;
        r5.<init>();
        r4.addTextChangedListener(r5);
        goto L_0x10a0;
    L_0x12cf:
        r4 = 3;
        goto L_0x10bd;
    L_0x12d2:
        r4 = 5;
        if (r11 != r4) goto L_0x1380;
    L_0x12d5:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 2;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextCheckCell;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.checkCell1 = r4;
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = "PaymentCardSavePaymentInformation";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861291E38 double:1.053098304E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r0 = r48;
        r6 = r0.saveCardInfo;
        r7 = 0;
        r4.setTextAndCheck(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.checkCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.checkCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$7;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r48.updateSavePaymentField();
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x1113;
    L_0x1380:
        if (r11 != 0) goto L_0x1113;
    L_0x1382:
        r4 = new android.widget.FrameLayout;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.androidPayContainer = r4;
        r0 = r48;
        r4 = r0.androidPayContainer;
        r5 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r4.setId(r5);
        r0 = r48;
        r4 = r0.androidPayContainer;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.androidPayContainer;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r0 = r0.androidPayContainer;
        r46 = r0;
        r4 = -2;
        r5 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = 21;
        r7 = 0;
        r8 = 0;
        r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r10 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r46;
        r0.addView(r1, r4);
        goto L_0x1113;
    L_0x13c8:
        r0 = r48;
        r4 = r0.need_card_country;
        if (r4 != 0) goto L_0x13ec;
    L_0x13ce:
        r0 = r48;
        r4 = r0.need_card_postcode;
        if (r4 != 0) goto L_0x13ec;
    L_0x13d4:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
    L_0x13ec:
        r0 = r48;
        r4 = r0.need_card_postcode;
        if (r4 == 0) goto L_0x1401;
    L_0x13f2:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 5;
        r4 = r4[r5];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0CLASSNAME;
    L_0x1401:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = 3;
        r4 = r4[r5];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0CLASSNAME;
    L_0x1410:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 1;
        if (r4 != r5) goto L_0x14f2;
    L_0x1417:
        r0 = r48;
        r4 = r0.requestedInfo;
        r4 = r4.shipping_options;
        r20 = r4.size();
        r0 = r20;
        r4 = new org.telegram.ui.Cells.RadioCell[r0];
        r0 = r48;
        r0.radioCells = r4;
        r11 = 0;
    L_0x142a:
        r0 = r20;
        if (r11 >= r0) goto L_0x14b8;
    L_0x142e:
        r0 = r48;
        r4 = r0.requestedInfo;
        r4 = r4.shipping_options;
        r41 = r4.get(r11);
        r41 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r41;
        r0 = r48;
        r4 = r0.radioCells;
        r5 = new org.telegram.ui.Cells.RadioCell;
        r0 = r49;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r48;
        r4 = r0.radioCells;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.radioCells;
        r4 = r4[r11];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.radioCells;
        r6 = r4[r11];
        r4 = "%s - %s";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r7 = 0;
        r0 = r41;
        r8 = r0.prices;
        r0 = r48;
        r8 = r0.getTotalPriceString(r8);
        r5[r7] = r8;
        r7 = 1;
        r0 = r41;
        r8 = r0.title;
        r5[r7] = r8;
        r7 = java.lang.String.format(r4, r5);
        if (r11 != 0) goto L_0x14b3;
    L_0x1488:
        r4 = 1;
        r5 = r4;
    L_0x148a:
        r4 = r20 + -1;
        if (r11 == r4) goto L_0x14b6;
    L_0x148e:
        r4 = 1;
    L_0x148f:
        r6.setText(r7, r5, r4);
        r0 = r48;
        r4 = r0.radioCells;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$8;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.radioCells;
        r5 = r5[r11];
        r4.addView(r5);
        r11 = r11 + 1;
        goto L_0x142a;
    L_0x14b3:
        r4 = 0;
        r5 = r4;
        goto L_0x148a;
    L_0x14b6:
        r4 = 0;
        goto L_0x148f;
    L_0x14b8:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0CLASSNAME;
    L_0x14f2:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 3;
        if (r4 != r5) goto L_0x17ff;
    L_0x14f9:
        r4 = 2;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r48;
        r0.inputFields = r4;
        r11 = 0;
    L_0x1501:
        r4 = 2;
        if (r11 >= r4) goto L_0x0CLASSNAME;
    L_0x1504:
        if (r11 != 0) goto L_0x154d;
    L_0x1506:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentCardTitle";
        r6 = NUM; // 0x7f0CLASSNAMEc float:1.8612916E38 double:1.0530983056E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x154d:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r49;
        r0.<init>(r1);
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 50;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        r4 = 1;
        if (r11 == r4) goto L_0x1796;
    L_0x1575:
        r13 = 1;
    L_0x1576:
        if (r13 == 0) goto L_0x1586;
    L_0x1578:
        r4 = 7;
        if (r11 != r4) goto L_0x1799;
    L_0x157b:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x1799;
    L_0x1585:
        r13 = 0;
    L_0x1586:
        if (r13 == 0) goto L_0x15b8;
    L_0x1588:
        r24 = new org.telegram.ui.PaymentFormActivity$10;
        r0 = r24;
        r1 = r48;
        r2 = r49;
        r0.<init>(r2);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r0 = r48;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
    L_0x15b8:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r49;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        if (r11 != 0) goto L_0x17b3;
    L_0x1634:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = org.telegram.ui.PaymentFormActivity$$Lambda$9.$instance;
        r4.setOnTouchListener(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setInputType(r5);
    L_0x1649:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        switch(r11) {
            case 0: goto L_0x17cb;
            case 1: goto L_0x17de;
            default: goto L_0x1658;
        };
    L_0x1658:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x17fc;
    L_0x1674:
        r4 = 5;
    L_0x1675:
        r5.setGravity(r4);
        r0 = r48;
        r4 = r0.inputFields;
        r46 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r10 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r46;
        r0.addView(r1, r4);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$10;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 1;
        if (r11 != r4) goto L_0x1792;
    L_0x16a9:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentConfirmationMessage";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612934E38 double:1.05309831E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r0 = r48;
        r9 = r0.paymentForm;
        r9 = r9.saved_credentials;
        r9 = r9.title;
        r7[r8] = r9;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_b float:1.794486E38 double:1.0529355717E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentConfirmationNewCard";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612936E38 double:1.0530983105E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 0;
        r4.setText(r5, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.settingsCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$11;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x1792:
        r11 = r11 + 1;
        goto L_0x1501;
    L_0x1796:
        r13 = 0;
        goto L_0x1576;
    L_0x1799:
        r4 = 6;
        if (r11 != r4) goto L_0x1586;
    L_0x179c:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x1586;
    L_0x17a6:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 != 0) goto L_0x1586;
    L_0x17b0:
        r13 = 0;
        goto L_0x1586;
    L_0x17b3:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r4.setInputType(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r5);
        goto L_0x1649;
    L_0x17cb:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.saved_credentials;
        r5 = r5.title;
        r4.setText(r5);
        goto L_0x1658;
    L_0x17de:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "LoginPassword";
        r6 = NUM; // 0x7f0CLASSNAMEfc float:1.861178E38 double:1.053098029E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r4.requestFocus();
        goto L_0x1658;
    L_0x17fc:
        r4 = 3;
        goto L_0x1675;
    L_0x17ff:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 == r5) goto L_0x180d;
    L_0x1806:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 5;
        if (r4 != r5) goto L_0x1dd2;
    L_0x180d:
        r4 = new org.telegram.ui.Cells.PaymentInfoCell;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.paymentInfoCell = r4;
        r0 = r48;
        r4 = r0.paymentInfoCell;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r5 = r0.paymentInfoCell;
        r0 = r48;
        r4 = r0.messageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r4;
        r0 = r48;
        r6 = r0.currentBotName;
        r5.setInvoice(r4, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.paymentInfoCell;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r16 = new java.util.ArrayList;
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.prices;
        r0 = r16;
        r0.<init>(r4);
        r0 = r48;
        r4 = r0.shippingOption;
        if (r4 == 0) goto L_0x188e;
    L_0x1883:
        r0 = r48;
        r4 = r0.shippingOption;
        r4 = r4.prices;
        r0 = r16;
        r0.addAll(r4);
    L_0x188e:
        r0 = r48;
        r1 = r16;
        r43 = r0.getTotalPriceString(r1);
        r11 = 0;
    L_0x1897:
        r4 = r16.size();
        if (r11 >= r4) goto L_0x18e4;
    L_0x189d:
        r0 = r16;
        r35 = r0.get(r11);
        r35 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r35;
        r36 = new org.telegram.ui.Cells.TextPriceCell;
        r0 = r36;
        r1 = r49;
        r0.<init>(r1);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r36;
        r0.setBackgroundColor(r4);
        r0 = r35;
        r4 = r0.label;
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r0 = r35;
        r6 = r0.amount;
        r0 = r48;
        r8 = r0.paymentForm;
        r8 = r8.invoice;
        r8 = r8.currency;
        r5 = r5.formatCurrencyString(r6, r8);
        r6 = 0;
        r0 = r36;
        r0.setTextAndValue(r4, r5, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r36;
        r4.addView(r0);
        r11 = r11 + 1;
        goto L_0x1897;
    L_0x18e4:
        r36 = new org.telegram.ui.Cells.TextPriceCell;
        r0 = r36;
        r1 = r49;
        r0.<init>(r1);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r36;
        r0.setBackgroundColor(r4);
        r4 = "PaymentTransactionTotal";
        r5 = NUM; // 0x7f0CLASSNAMEc float:1.8613013E38 double:1.0530983293E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r5 = 1;
        r0 = r36;
        r1 = r43;
        r0.setTextAndValue(r4, r1, r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r36;
        r4.addView(r0);
        r24 = new org.telegram.ui.PaymentFormActivity$11;
        r0 = r24;
        r1 = r48;
        r2 = r49;
        r0.<init>(r2);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r0 = r48;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = new android.widget.FrameLayout$LayoutParams;
        r6 = -1;
        r7 = 1;
        r8 = 83;
        r5.<init>(r6, r7, r8);
        r0 = r24;
        r4.addView(r0, r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r4 = r4[r5];
        r0 = r48;
        r5 = r0.cardName;
        r6 = "PaymentCheckoutMethod";
        r7 = NUM; // 0x7f0CLASSNAMEf float:1.8612922E38 double:1.053098307E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 0;
        r5 = r5[r6];
        r4.addView(r5);
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 != r5) goto L_0x19a2;
    L_0x1991:
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$12;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
    L_0x19a2:
        r38 = 0;
        r11 = 0;
    L_0x19a5:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r4 = r4.size();
        if (r11 >= r4) goto L_0x19ce;
    L_0x19b1:
        r0 = r48;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r44 = r4.get(r11);
        r44 = (org.telegram.tgnet.TLRPC.User) r44;
        r0 = r44;
        r4 = r0.id;
        r0 = r48;
        r5 = r0.paymentForm;
        r5 = r5.provider_id;
        if (r4 != r5) goto L_0x19cb;
    L_0x19c9:
        r38 = r44;
    L_0x19cb:
        r11 = r11 + 1;
        goto L_0x19a5;
    L_0x19ce:
        if (r38 == 0) goto L_0x1dcd;
    L_0x19d0:
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 1;
        r4 = r4[r5];
        r0 = r38;
        r5 = r0.first_name;
        r0 = r38;
        r6 = r0.last_name;
        r37 = org.telegram.messenger.ContactsController.formatName(r5, r6);
        r5 = "PaymentCheckoutProvider";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861293E38 double:1.053098309E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 1;
        r0 = r37;
        r4.setTextAndValue(r0, r5, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 1;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1a1e:
        r0 = r48;
        r4 = r0.validateRequest;
        if (r4 == 0) goto L_0x1c1a;
    L_0x1a24:
        r0 = r48;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x1ac8;
    L_0x1a2e:
        r4 = "%s %s, %s, %s, %s, %s";
        r5 = 6;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r0 = r48;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.street_line1;
        r5[r6] = r7;
        r6 = 1;
        r0 = r48;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.street_line2;
        r5[r6] = r7;
        r6 = 2;
        r0 = r48;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.city;
        r5[r6] = r7;
        r6 = 3;
        r0 = r48;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.state;
        r5[r6] = r7;
        r6 = 4;
        r0 = r48;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.country_iso2;
        r5[r6] = r7;
        r6 = 5;
        r0 = r48;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.post_code;
        r5[r6] = r7;
        r12 = java.lang.String.format(r4, r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 2;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 2;
        r4 = r4[r5];
        r5 = "PaymentShippingAddress";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612973E38 double:1.0530983194E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 1;
        r4.setTextAndValue(r12, r5, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 2;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1ac8:
        r0 = r48;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.name;
        if (r4 == 0) goto L_0x1b1c;
    L_0x1ad2:
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 3;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 3;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 3;
        r4 = r4[r5];
        r0 = r48;
        r5 = r0.validateRequest;
        r5 = r5.info;
        r5 = r5.name;
        r6 = "PaymentCheckoutName";
        r7 = NUM; // 0x7f0CLASSNAME float:1.8612924E38 double:1.0530983075E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 3;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1b1c:
        r0 = r48;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.phone;
        if (r4 == 0) goto L_0x1b78;
    L_0x1b26:
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 4;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 4;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 4;
        r4 = r4[r5];
        r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r0 = r48;
        r6 = r0.validateRequest;
        r6 = r6.info;
        r6 = r6.phone;
        r5 = r5.format(r6);
        r6 = "PaymentCheckoutPhoneNumber";
        r7 = NUM; // 0x7f0CLASSNAME float:1.8612928E38 double:1.0530983085E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 4;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1b78:
        r0 = r48;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.email;
        if (r4 == 0) goto L_0x1bcc;
    L_0x1b82:
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 5;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 5;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 5;
        r4 = r4[r5];
        r0 = r48;
        r5 = r0.validateRequest;
        r5 = r5.info;
        r5 = r5.email;
        r6 = "PaymentCheckoutEmail";
        r7 = NUM; // 0x7f0CLASSNAMEe float:1.861292E38 double:1.0530983066E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 5;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1bcc:
        r0 = r48;
        r4 = r0.shippingOption;
        if (r4 == 0) goto L_0x1c1a;
    L_0x1bd2:
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 6;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 6;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.detailSettingsCell;
        r5 = 6;
        r4 = r4[r5];
        r0 = r48;
        r5 = r0.shippingOption;
        r5 = r5.title;
        r6 = "PaymentCheckoutShippingMethod";
        r7 = NUM; // 0x7f0CLASSNAME float:1.8612932E38 double:1.0530983095E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 0;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.detailSettingsCell;
        r6 = 6;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1c1a:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 != r5) goto L_0x1d93;
    L_0x1CLASSNAME:
        r4 = new android.widget.FrameLayout;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.bottomLayout = r4;
        r0 = r48;
        r4 = r0.bottomLayout;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.bottomLayout;
        r5 = -1;
        r6 = 48;
        r7 = 80;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7);
        r0 = r27;
        r0.addView(r4, r5);
        r0 = r48;
        r4 = r0.bottomLayout;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$13;
        r0 = r48;
        r1 = r37;
        r2 = r43;
        r5.<init>(r0, r1, r2);
        r4.setOnClickListener(r5);
        r4 = new android.widget.TextView;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.payTextView = r4;
        r0 = r48;
        r4 = r0.payTextView;
        r5 = "windowBackgroundWhiteBlueText6";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.payTextView;
        r5 = "PaymentCheckoutPay";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612926E38 double:1.053098308E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r43;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.payTextView;
        r5 = 1;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4.setTextSize(r5, r6);
        r0 = r48;
        r4 = r0.payTextView;
        r5 = 17;
        r4.setGravity(r5);
        r0 = r48;
        r4 = r0.payTextView;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r4.setTypeface(r5);
        r0 = r48;
        r4 = r0.bottomLayout;
        r0 = r48;
        r5 = r0.payTextView;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Components.ContextProgressView;
        r5 = 0;
        r0 = r49;
        r4.<init>(r0, r5);
        r0 = r48;
        r0.progressViewButton = r4;
        r0 = r48;
        r4 = r0.progressViewButton;
        r5 = 4;
        r4.setVisibility(r5);
        r0 = r48;
        r4 = r0.bottomLayout;
        r0 = r48;
        r5 = r0.progressViewButton;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r40 = new android.view.View;
        r0 = r40;
        r1 = r49;
        r0.<init>(r1);
        r4 = NUM; // 0x7var_ float:1.7944888E38 double:1.0529355786E-314;
        r0 = r40;
        r0.setBackgroundResource(r4);
        r4 = -1;
        r5 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = 83;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r27;
        r1 = r40;
        r0.addView(r1, r4);
        r0 = r48;
        r4 = r0.doneItem;
        r5 = 0;
        r4.setEnabled(r5);
        r0 = r48;
        r4 = r0.doneItem;
        r4 = r4.getImageView();
        r5 = 4;
        r4.setVisibility(r5);
        r4 = new org.telegram.ui.PaymentFormActivity$13;
        r0 = r48;
        r1 = r49;
        r4.<init>(r1);
        r0 = r48;
        r0.webView = r4;
        r0 = r48;
        r4 = r0.webView;
        r5 = -1;
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setJavaScriptEnabled(r5);
        r0 = r48;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setDomStorageEnabled(r5);
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x1d6c;
    L_0x1d52:
        r0 = r48;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 0;
        r4.setMixedContentMode(r5);
        r19 = android.webkit.CookieManager.getInstance();
        r0 = r48;
        r4 = r0.webView;
        r5 = 1;
        r0 = r19;
        r0.setAcceptThirdPartyCookies(r4, r5);
    L_0x1d6c:
        r0 = r48;
        r4 = r0.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$14;
        r0 = r48;
        r5.<init>();
        r4.setWebViewClient(r5);
        r0 = r48;
        r4 = r0.webView;
        r5 = -1;
        r6 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6);
        r0 = r27;
        r0.addView(r4, r5);
        r0 = r48;
        r4 = r0.webView;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x1d93:
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.sectionCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.sectionCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0CLASSNAME;
    L_0x1dcd:
        r37 = "";
        goto L_0x1a1e;
    L_0x1dd2:
        r0 = r48;
        r4 = r0.currentStep;
        r5 = 6;
        if (r4 != r5) goto L_0x0CLASSNAME;
    L_0x1dd9:
        r4 = new org.telegram.ui.Cells.EditTextSettingsCell;
        r0 = r49;
        r4.<init>(r0);
        r0 = r48;
        r0.codeFieldCell = r4;
        r0 = r48;
        r4 = r0.codeFieldCell;
        r5 = "";
        r6 = "PasswordCode";
        r7 = NUM; // 0x7f0CLASSNAMEc float:1.8612883E38 double:1.0530982977E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 0;
        r4.setTextAndHint(r5, r6, r7);
        r0 = r48;
        r4 = r0.codeFieldCell;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.codeFieldCell;
        r26 = r4.getTextView();
        r4 = 3;
        r0 = r26;
        r0.setInputType(r4);
        r4 = 6;
        r0 = r26;
        r0.setImeOptions(r4);
        r4 = new org.telegram.ui.PaymentFormActivity$$Lambda$14;
        r0 = r48;
        r4.<init>(r0);
        r0 = r26;
        r0.setOnEditorActionListener(r4);
        r4 = new org.telegram.ui.PaymentFormActivity$15;
        r0 = r48;
        r4.<init>();
        r0 = r26;
        r0.addTextChangedListener(r4);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.codeFieldCell;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 2;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_b float:1.794486E38 double:1.0529355717E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 2;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhiteBlackText";
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "ResendCode";
        r6 = NUM; // 0x7f0CLASSNAMEfe float:1.8613342E38 double:1.0530984093E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 1;
        r4.setText(r5, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.settingsCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$15;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextSettingsCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhiteRedText3";
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhiteRedText3";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "AbortPassword";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8609196E38 double:1.0530973994E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 0;
        r4.setText(r5, r6);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.settingsCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r48;
        r4 = r0.settingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$16;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r4 = 3;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r48;
        r0.inputFields = r4;
        r11 = 0;
    L_0x1f6e:
        r4 = 3;
        if (r11 >= r4) goto L_0x2257;
    L_0x1var_:
        if (r11 != 0) goto L_0x2156;
    L_0x1var_:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentPasswordTitle";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612962E38 double:1.053098317E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x1fba:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r49;
        r0.<init>(r1);
        r0 = r48;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 50;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        if (r11 != 0) goto L_0x2011;
    L_0x1fe1:
        r24 = new org.telegram.ui.PaymentFormActivity$16;
        r0 = r24;
        r1 = r48;
        r2 = r49;
        r0.<init>(r2);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r0 = r48;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
    L_0x2011:
        r0 = r48;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r49;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        if (r11 == 0) goto L_0x2090;
    L_0x208d:
        r4 = 1;
        if (r11 != r4) goto L_0x21a2;
    L_0x2090:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r4.setInputType(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r5);
    L_0x20b2:
        switch(r11) {
            case 0: goto L_0x21bb;
            case 1: goto L_0x21d9;
            case 2: goto L_0x21ee;
            default: goto L_0x20b5;
        };
    L_0x20b5:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r48;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x2203;
    L_0x20d1:
        r4 = 5;
    L_0x20d2:
        r5.setGravity(r4);
        r0 = r48;
        r4 = r0.inputFields;
        r46 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r10 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r46;
        r0.addView(r1, r4);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$17;
        r0 = r48;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 1;
        if (r11 != r4) goto L_0x2206;
    L_0x2106:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentPasswordInfo";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612958E38 double:1.053098316E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_b float:1.794486E38 double:1.0529355717E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x2152:
        r11 = r11 + 1;
        goto L_0x1f6e;
    L_0x2156:
        r4 = 2;
        if (r11 != r4) goto L_0x1fba;
    L_0x2159:
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r48;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPasswordEmailTitle";
        r6 = NUM; // 0x7f0CLASSNAMEf float:1.8612954E38 double:1.053098315E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.headerCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x1fba;
    L_0x21a2:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 33;
        r4.setInputType(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x20b2;
    L_0x21bb:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentPasswordEnter";
        r6 = NUM; // 0x7f0CLASSNAME float:1.8612956E38 double:1.0530983154E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r4.requestFocus();
        goto L_0x20b5;
    L_0x21d9:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentPasswordReEnter";
        r6 = NUM; // 0x7f0CLASSNAME float:1.861296E38 double:1.0530983164E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x20b5;
    L_0x21ee:
        r0 = r48;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentPasswordEmail";
        r6 = NUM; // 0x7f0CLASSNAMEd float:1.861295E38 double:1.053098314E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x20b5;
    L_0x2203:
        r4 = 3;
        goto L_0x20d2;
    L_0x2206:
        r4 = 2;
        if (r11 != r4) goto L_0x2152;
    L_0x2209:
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r49;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPasswordEmailInfo";
        r6 = NUM; // 0x7f0CLASSNAMEe float:1.8612952E38 double:1.0530983145E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r48;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r49;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r48;
        r4 = r0.linearLayout2;
        r0 = r48;
        r5 = r0.bottomCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x2152;
    L_0x2257:
        r48.updatePasswordFields();
        goto L_0x0CLASSNAME;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$1$PaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$Lambda$47(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$PaymentFormActivity(String name, String shortName) {
        this.inputFields[4].setText(name);
        this.countryName = shortName;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$2$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            while (num + 1 < this.inputFields.length) {
                num++;
                if (num != 4 && ((View) this.inputFields[num].getParent()).getVisibility() == 0) {
                    this.inputFields[num].requestFocus();
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$3$PaymentFormActivity(View v) {
        this.saveShippingInfo = !this.saveShippingInfo;
        this.checkCell1.setChecked(this.saveShippingInfo);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$4$PaymentFormActivity(View v) {
        this.saveCardInfo = !this.saveCardInfo;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$6$PaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$Lambda$46(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$5$PaymentFormActivity(String name, String shortName) {
        this.inputFields[4].setText(name);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$7$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            while (num + 1 < this.inputFields.length) {
                num++;
                if (num == 4) {
                    num++;
                }
                if (((View) this.inputFields[num].getParent()).getVisibility() == 0) {
                    this.inputFields[num].requestFocus();
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$8$PaymentFormActivity(View v) {
        this.saveCardInfo = !this.saveCardInfo;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$9$PaymentFormActivity(View v) {
        int num = ((Integer) v.getTag()).intValue();
        int a1 = 0;
        while (a1 < this.radioCells.length) {
            this.radioCells[a1].setChecked(num == a1, true);
            a1++;
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$11$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$12$PaymentFormActivity(View v) {
        this.passwordOk = false;
        goToNextStep();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$13$PaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo, null);
        activity.setDelegate(new PaymentFormActivityDelegate() {
            public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
                PaymentFormActivity.this.paymentForm.saved_credentials = null;
                PaymentFormActivity.this.paymentJson = tokenJson;
                PaymentFormActivity.this.saveCardInfo = saveCard;
                PaymentFormActivity.this.cardName = card;
                PaymentFormActivity.this.androidPayCredentials = androidPay;
                PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", NUM), true);
                return false;
            }

            public void onFragmentDestroyed() {
            }

            public void currentPasswordUpdated(TL_account_password password) {
            }
        });
        presentFragment(activity);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$15$PaymentFormActivity(String providerName, String totalPrice, View v) {
        if (this.botUser == null || this.botUser.verified) {
            showPayAlert(totalPrice);
            return;
        }
        String botKey = "payment_warning_" + this.botUser.id;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        if (preferences.getBoolean(botKey, false)) {
            showPayAlert(totalPrice);
            return;
        }
        preferences.edit().putBoolean(botKey, true).commit();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentWarning", NUM));
        builder.setMessage(LocaleController.formatString("PaymentWarningText", NUM, this.currentBotName, providerName));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$Lambda$45(this, totalPrice));
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$14$PaymentFormActivity(String totalPrice, DialogInterface dialogInterface, int i) {
        showPayAlert(totalPrice);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$16$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$18$PaymentFormActivity(View v) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resendPasswordEmail(), PaymentFormActivity$$Lambda$44.$instance);
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        showDialog(builder.create());
    }

    static final /* synthetic */ void lambda$null$17$PaymentFormActivity(TLObject response, TL_error error) {
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$20$PaymentFormActivity(View v) {
        Builder builder = new Builder(getParentActivity());
        String text = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(text);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$Lambda$43(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$19$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$21$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        }
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            if (num == 0) {
                this.inputFields[1].requestFocus();
            } else if (num == 1) {
                this.inputFields[2].requestFocus();
            }
        }
        return false;
    }

    private void updatePasswordFields() {
        if (this.currentStep == 6 && this.bottomCell[2] != null) {
            this.doneItem.setVisibility(0);
            int a;
            if (this.currentPassword == null) {
                showEditDoneProgress(true, true);
                this.bottomCell[2].setVisibility(8);
                this.settingsCell[0].setVisibility(8);
                this.settingsCell[1].setVisibility(8);
                this.codeFieldCell.setVisibility(8);
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (a = 0; a < 3; a++) {
                    ((View) this.inputFields[a].getParent()).setVisibility(8);
                }
                for (a = 0; a < this.dividers.size(); a++) {
                    ((View) this.dividers.get(a)).setVisibility(8);
                }
                return;
            }
            showEditDoneProgress(true, false);
            if (this.waitingForEmail) {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
                String str = "EmailPasswordConfirmText2";
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textInfoPrivacyCell.setText(LocaleController.formatString(str, NUM, objArr));
                this.bottomCell[2].setVisibility(0);
                this.settingsCell[0].setVisibility(0);
                this.settingsCell[1].setVisibility(0);
                this.codeFieldCell.setVisibility(0);
                this.bottomCell[1].setText("");
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (a = 0; a < 3; a++) {
                    ((View) this.inputFields[a].getParent()).setVisibility(8);
                }
                for (a = 0; a < this.dividers.size(); a++) {
                    ((View) this.dividers.get(a)).setVisibility(8);
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
            for (a = 0; a < 3; a++) {
                ((View) this.inputFields[a].getParent()).setVisibility(0);
            }
            for (a = 0; a < this.dividers.size(); a++) {
                ((View) this.dividers.get(a)).setVisibility(0);
            }
        }
    }

    private void loadPasswordInfo() {
        if (!this.loadingPasswordInfo) {
            this.loadingPasswordInfo = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new PaymentFormActivity$$Lambda$18(this), 10);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadPasswordInfo$24$PaymentFormActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$41(this, error, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$23$PaymentFormActivity(TL_error error, TLObject response) {
        this.loadingPasswordInfo = false;
        if (error == null) {
            this.currentPassword = (TL_account_password) response;
            if (TwoStepVerificationActivity.canHandleCurrentPassword(this.currentPassword, false)) {
                if (this.paymentForm != null && this.currentPassword.has_password) {
                    this.paymentForm.password_missing = false;
                    this.paymentForm.can_save_credentials = true;
                    updateSavePaymentField();
                }
                TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
                if (this.passwordFragment != null) {
                    this.passwordFragment.setCurrentPassword(this.currentPassword);
                }
                if (!this.currentPassword.has_password && this.shortPollRunnable == null) {
                    this.shortPollRunnable = new PaymentFormActivity$$Lambda$42(this);
                    AndroidUtilities.runOnUIThread(this.shortPollRunnable, 5000);
                    return;
                }
                return;
            }
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$22$PaymentFormActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo();
            this.shortPollRunnable = null;
        }
    }

    private void showAlertWithText(String title, String text) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void showPayAlert(String totalPrice) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", NUM));
        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", NUM, totalPrice, this.currentBotName, this.currentItemName));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$Lambda$19(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showPayAlert$25$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        setDonePressed(true);
        sendData();
    }

    private void initAndroidPay(Context context) {
    }

    private String getTotalPriceString(ArrayList<TL_labeledPrice> prices) {
        long amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount += ((TL_labeledPrice) prices.get(a)).amount;
        }
        return LocaleController.getInstance().formatCurrencyString(amount, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TL_labeledPrice> prices) {
        long amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount += ((TL_labeledPrice) prices.get(a)).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(amount, this.paymentForm.invoice.currency, false);
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
        if (this.delegate != null) {
            this.delegate.onFragmentDestroyed();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
        }
        if (this.webView != null) {
            try {
                ViewParent parent = this.webView.getParent();
                if (parent != null) {
                    ((FrameLayout) parent).removeView(this.webView);
                }
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
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
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            if (this.webView != null) {
                if (this.currentStep != 4) {
                    this.webView.loadUrl(this.paymentForm.url);
                }
            } else if (this.currentStep == 2) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            } else if (this.currentStep == 3) {
                this.inputFields[1].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[1]);
            } else if (this.currentStep == 6 && !this.waitingForEmail) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            this.paymentForm.password_missing = false;
            this.paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (id == NotificationCenter.didRemoveTwoStepPassword) {
            this.paymentForm.password_missing = true;
            this.paymentForm.can_save_credentials = false;
            updateSavePaymentField();
        } else if (id == NotificationCenter.paymentFinished) {
            removeSelfFromStack();
        }
    }

    private void showAndroidPay() {
        if (getParentActivity() != null && this.androidPayContainer != null) {
            WalletFragmentStyle walletFragmentStyle;
            PaymentMethodTokenizationParameters parameters;
            WalletFragmentOptions.Builder optionsBuilder = WalletFragmentOptions.newBuilder();
            optionsBuilder.setEnvironment(this.paymentForm.invoice.test ? 3 : 1);
            optionsBuilder.setMode(1);
            if (this.androidPayPublicKey != null) {
                this.androidPayContainer.setBackgroundColor(this.androidPayBackgroundColor);
                walletFragmentStyle = new WalletFragmentStyle().setBuyButtonText(5).setBuyButtonAppearance(this.androidPayBlackTheme ? 6 : 4).setBuyButtonWidth(-1);
            } else {
                walletFragmentStyle = new WalletFragmentStyle().setBuyButtonText(6).setBuyButtonAppearance(6).setBuyButtonWidth(-2);
            }
            optionsBuilder.setFragmentStyle(walletFragmentStyle);
            WalletFragment walletFragment = WalletFragment.newInstance(optionsBuilder.build());
            FragmentTransaction fragmentTransaction = getParentActivity().getFragmentManager().beginTransaction();
            fragmentTransaction.replace(4000, walletFragment);
            fragmentTransaction.commit();
            ArrayList<TL_labeledPrice> arrayList = new ArrayList(this.paymentForm.invoice.prices);
            if (this.shippingOption != null) {
                arrayList.addAll(this.shippingOption.prices);
            }
            this.totalPriceDecimal = getTotalPriceDecimalString(arrayList);
            if (this.androidPayPublicKey != null) {
                parameters = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(2).addParameter("publicKey", this.androidPayPublicKey).build();
            } else {
                parameters = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(1).addParameter("gateway", "stripe").addParameter("stripe:publishableKey", this.stripeApiKey).addParameter("stripe:version", "3.5.0").build();
            }
            walletFragment.initialize(WalletFragmentInitParams.newBuilder().setMaskedWalletRequest(MaskedWalletRequest.newBuilder().setPaymentMethodTokenizationParameters(parameters).setEstimatedTotalPrice(this.totalPriceDecimal).setCurrencyCode(this.paymentForm.invoice.currency).build()).setMaskedWalletRequestCode(1000).build());
            this.androidPayContainer.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[]{0.0f, 1.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == -1) {
                showEditDoneProgress(true, true);
                setDonePressed(true);
                MaskedWallet maskedWallet = (MaskedWallet) data.getParcelableExtra("com.google.android.gms.wallet.EXTRA_MASKED_WALLET");
                Cart.Builder cardBuilder = Cart.newBuilder().setCurrencyCode(this.paymentForm.invoice.currency).setTotalPrice(this.totalPriceDecimal);
                ArrayList<TL_labeledPrice> arrayList = new ArrayList(this.paymentForm.invoice.prices);
                if (this.shippingOption != null) {
                    arrayList.addAll(this.shippingOption.prices);
                }
                for (int a = 0; a < arrayList.size(); a++) {
                    TL_labeledPrice price = (TL_labeledPrice) arrayList.get(a);
                    String amount = LocaleController.getInstance().formatCurrencyDecimalString(price.amount, this.paymentForm.invoice.currency, false);
                    cardBuilder.addLineItem(LineItem.newBuilder().setCurrencyCode(this.paymentForm.invoice.currency).setQuantity("1").setDescription(price.label).setTotalPrice(amount).setUnitPrice(amount).build());
                }
                Wallet.Payments.loadFullWallet(this.googleApiClient, FullWalletRequest.newBuilder().setCart(cardBuilder.build()).setGoogleTransactionId(maskedWallet.getGoogleTransactionId()).build(), 1001);
                return;
            }
            showEditDoneProgress(true, false);
            setDonePressed(false);
        } else if (requestCode != 1001) {
        } else {
            if (resultCode == -1) {
                FullWallet fullWallet = (FullWallet) data.getParcelableExtra("com.google.android.gms.wallet.EXTRA_FULL_WALLET");
                String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
                try {
                    if (this.androidPayPublicKey != null) {
                        this.androidPayCredentials = new TL_inputPaymentCredentialsAndroidPay();
                        this.androidPayCredentials.payment_token = new TL_dataJSON();
                        this.androidPayCredentials.payment_token.data = tokenJSON;
                        this.androidPayCredentials.google_transaction_id = fullWallet.getGoogleTransactionId();
                        String[] descriptions = fullWallet.getPaymentDescriptions();
                        if (descriptions.length > 0) {
                            this.cardName = descriptions[0];
                        } else {
                            this.cardName = "Android Pay";
                        }
                    } else {
                        this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), TokenParser.parseToken(tokenJSON).getId()});
                        Card card = token.getCard();
                        this.cardName = card.getType() + " *" + card.getLast4();
                    }
                    goToNextStep();
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                    return;
                } catch (JSONException e) {
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
        int nextStep;
        if (this.currentStep == 0) {
            if (this.paymentForm.invoice.flexible) {
                nextStep = 1;
            } else if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                    nextStep = 4;
                } else {
                    nextStep = 3;
                }
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, null, null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (this.currentStep == 1) {
            if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                    nextStep = 4;
                } else {
                    nextStep = 3;
                }
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        } else if (this.currentStep == 2) {
            if (this.paymentForm.password_missing && this.saveCardInfo) {
                this.passwordFragment = new PaymentFormActivity(this.paymentForm, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials);
                this.passwordFragment.setCurrentPassword(this.currentPassword);
                this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                    public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
                        if (PaymentFormActivity.this.delegate != null) {
                            PaymentFormActivity.this.delegate.didSelectNewCard(tokenJson, card, saveCard, androidPay);
                        }
                        if (PaymentFormActivity.this.isWebView) {
                            PaymentFormActivity.this.removeSelfFromStack();
                        }
                        return PaymentFormActivity.this.delegate != null;
                    }

                    public void onFragmentDestroyed() {
                        PaymentFormActivity.this.passwordFragment = null;
                    }

                    public void currentPasswordUpdated(TL_account_password password) {
                        PaymentFormActivity.this.currentPassword = password;
                    }
                });
                presentFragment(this.passwordFragment, this.isWebView);
            } else if (this.delegate != null) {
                this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials);
                finishFragment();
            } else {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
            }
        } else if (this.currentStep == 3) {
            if (this.passwordOk) {
                nextStep = 4;
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), !this.passwordOk);
        } else if (this.currentStep == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (this.currentStep != 6) {
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
            if ((this.paymentForm.password_missing || this.paymentForm.can_save_credentials) && (this.webView == null || !(this.webView == null || this.webviewLoading))) {
                SpannableStringBuilder text = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", NUM));
                if (this.paymentForm.password_missing) {
                    loadPasswordInfo();
                    text.append("\n");
                    int len = text.length();
                    String str2 = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", NUM);
                    int index1 = str2.indexOf(42);
                    int index2 = str2.lastIndexOf(42);
                    text.append(str2);
                    if (!(index1 == -1 || index2 == -1)) {
                        index1 += len;
                        index2 += len;
                        this.bottomCell[0].getTextView().setMovementMethod(new LinkMovementMethodMy());
                        text.replace(index2, index2 + 1, "");
                        text.replace(index1, index1 + 1, "");
                        text.setSpan(new LinkSpan(), index1, index2 - 1, 33);
                    }
                }
                this.checkCell1.setEnabled(true);
                this.bottomCell[0].setText(text);
                this.checkCell1.setVisibility(0);
                this.bottomCell[0].setVisibility(0);
                this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), NUM, "windowBackgroundGrayShadow"));
                return;
            }
            this.checkCell1.setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), NUM, "windowBackgroundGrayShadow"));
        }
    }

    @SuppressLint({"HardwareIds"})
    public void fillNumber(String number) {
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            boolean allowCall = true;
            if (number != null || (tm.getSimState() != 1 && tm.getPhoneType() != 0)) {
                if (VERSION.SDK_INT >= 23) {
                    if (getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                        allowCall = true;
                    } else {
                        allowCall = false;
                    }
                }
                if (number != null || allowCall) {
                    if (number == null) {
                        number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                    }
                    String textToSet = null;
                    boolean ok = false;
                    if (!TextUtils.isEmpty(number)) {
                        if (number.length() > 4) {
                            for (int a = 4; a >= 1; a--) {
                                String sub = number.substring(0, a);
                                if (((String) this.codesMap.get(sub)) != null) {
                                    ok = true;
                                    textToSet = number.substring(a, number.length());
                                    this.inputFields[8].setText(sub);
                                    break;
                                }
                            }
                            if (!ok) {
                                textToSet = number.substring(1, number.length());
                                this.inputFields[8].setText(number.substring(0, 1));
                            }
                        }
                        if (textToSet != null) {
                            this.inputFields[9].setText(textToSet);
                            this.inputFields[9].setSelection(this.inputFields[9].length());
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void sendSavePassword(boolean clear) {
        if (clear || this.codeFieldCell.getVisibility() != 0) {
            String email;
            String firstPassword;
            TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
            if (clear) {
                this.doneItem.setVisibility(0);
                email = null;
                firstPassword = null;
                req.new_settings = new TL_account_passwordInputSettings();
                req.new_settings.flags = 2;
                req.new_settings.email = "";
                req.password = new TL_inputCheckPasswordEmpty();
            } else {
                firstPassword = this.inputFields[0].getText().toString();
                if (TextUtils.isEmpty(firstPassword)) {
                    shakeField(0);
                    return;
                } else if (firstPassword.equals(this.inputFields[1].getText().toString())) {
                    email = this.inputFields[2].getText().toString();
                    if (email.length() < 3) {
                        shakeField(2);
                        return;
                    }
                    int dot = email.lastIndexOf(46);
                    int dog = email.lastIndexOf(64);
                    if (dot < 0 || dog < 0 || dot < dog) {
                        shakeField(2);
                        return;
                    }
                    req.password = new TL_inputCheckPasswordEmpty();
                    req.new_settings = new TL_account_passwordInputSettings();
                    TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                    tL_account_passwordInputSettings.flags |= 1;
                    req.new_settings.hint = "";
                    req.new_settings.new_algo = this.currentPassword.new_algo;
                    if (email.length() > 0) {
                        tL_account_passwordInputSettings = req.new_settings;
                        tL_account_passwordInputSettings.flags |= 2;
                        req.new_settings.email = email.trim();
                    }
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
            Utilities.globalQueue.postRunnable(new PaymentFormActivity$$Lambda$21(this, clear, email, firstPassword, req));
            return;
        }
        String code = this.codeFieldCell.getText();
        if (code.length() == 0) {
            shakeView(this.codeFieldCell);
            return;
        }
        showEditDoneProgress(true, true);
        TL_account_confirmPasswordEmail req2 = new TL_account_confirmPasswordEmail();
        req2.code = code;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new PaymentFormActivity$$Lambda$20(this), 10);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendSavePassword$27$PaymentFormActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$40(this, error));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$26$PaymentFormActivity(TL_error error) {
        showEditDoneProgress(true, false);
        if (error == null) {
            if (getParentActivity() != null) {
                if (this.shortPollRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
                    this.shortPollRunnable = null;
                }
                goToNextStep();
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            shakeView(this.codeFieldCell);
            this.codeFieldCell.setText("", false);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            String timeString;
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$32$PaymentFormActivity(boolean clear, String email, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$36(this, error, clear, response, email));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendSavePassword$33$PaymentFormActivity(boolean clear, String email, String firstPassword, TL_account_updatePasswordSettings req) {
        RequestDelegate requestDelegate = new PaymentFormActivity$$Lambda$35(this, clear, email);
        if (clear) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        byte[] newPasswordBytes = AndroidUtilities.getStringBytes(firstPassword);
        TL_error error;
        if (this.currentPassword.new_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = this.currentPassword.new_algo;
            req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo);
            if (req.new_settings.new_password_hash == null) {
                error = new TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run(null, error);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        error = new TL_error();
        error.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$31$PaymentFormActivity(TL_error error, boolean clear, TLObject response, String email) {
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            showEditDoneProgress(true, false);
            if (clear) {
                this.currentPassword.has_password = false;
                this.currentPassword.current_algo = null;
                this.delegate.currentPasswordUpdated(this.currentPassword);
                finishFragment();
                return;
            } else if (error == null && (response instanceof TL_boolTrue)) {
                if (getParentActivity() != null) {
                    goToNextStep();
                    return;
                }
                return;
            } else if (error == null) {
                return;
            } else {
                if (error.text.equals("EMAIL_UNCONFIRMED") || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    this.emailCodeLength = Utilities.parseInt(error.text).intValue();
                    Builder builder = new Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$Lambda$38(this, email));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    Dialog dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else if (error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                    return;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                    return;
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
                    return;
                }
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new PaymentFormActivity$$Lambda$37(this, clear), 8);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$29$PaymentFormActivity(boolean clear, TLObject response2, TL_error error2) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$39(this, error2, response2, clear));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$28$PaymentFormActivity(TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            this.currentPassword = (TL_account_password) response2;
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            sendSavePassword(clear);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$30$PaymentFormActivity(String email, DialogInterface dialogInterface, int i) {
        this.waitingForEmail = true;
        this.currentPassword.email_unconfirmed_pattern = email;
        updatePasswordFields();
    }

    private boolean sendCardData() {
        Integer month;
        Integer year;
        String[] args = this.inputFields[1].getText().toString().split("/");
        if (args.length == 2) {
            month = Utilities.parseInt(args[0]);
            year = Utilities.parseInt(args[1]);
        } else {
            month = null;
            year = null;
        }
        Card card = new Card(this.inputFields[0].getText().toString(), month, year, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), null, null, null, null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), null);
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
                            AndroidUtilities.runOnUIThread(new PaymentFormActivity$18$$Lambda$0(this));
                        }
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$onSuccess$0$PaymentFormActivity$18() {
                        PaymentFormActivity.this.goToNextStep();
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        PaymentFormActivity.this.setDonePressed(false);
                    }

                    public void onError(Exception error) {
                        if (!PaymentFormActivity.this.canceled) {
                            PaymentFormActivity.this.showEditDoneProgress(true, false);
                            PaymentFormActivity.this.setDonePressed(false);
                            if ((error instanceof APIConnectionException) || (error instanceof APIException)) {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", NUM));
                            } else {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
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
            this.validateRequest.save = this.saveShippingInfo;
            this.validateRequest.msg_id = this.messageObject.getId();
            this.validateRequest.info = new TL_paymentRequestedInfo();
            if (this.paymentForm.invoice.name_requested) {
                this.validateRequest.info.name = this.inputFields[6].getText().toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 1;
            }
            if (this.paymentForm.invoice.phone_requested) {
                this.validateRequest.info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
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
                this.validateRequest.info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : "";
                this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 8;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new PaymentFormActivity$$Lambda$22(this, this.validateRequest), 2);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendForm$37$PaymentFormActivity(TLObject req, TLObject response, TL_error error) {
        if (response instanceof TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$32(this, response));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$33(this, error, req));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$35$PaymentFormActivity(TLObject response) {
        this.requestedInfo = (TL_payments_validatedRequestedInfo) response;
        if (!(this.paymentForm.saved_info == null || this.saveShippingInfo)) {
            TL_payments_clearSavedInfo req1 = new TL_payments_clearSavedInfo();
            req1.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, PaymentFormActivity$$Lambda$34.$instance);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    static final /* synthetic */ void lambda$null$34$PaymentFormActivity(TLObject response1, TL_error error1) {
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$36$PaymentFormActivity(TL_error error, TLObject req) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (error != null) {
            String str = error.text;
            boolean z = true;
            switch (str.hashCode()) {
                case -2092780146:
                    if (str.equals("ADDRESS_CITY_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case -1623547228:
                    if (str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case -1224177757:
                    if (str.equals("ADDRESS_COUNTRY_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case -1031752045:
                    if (str.equals("REQ_INFO_NAME_INVALID")) {
                        z = false;
                        break;
                    }
                    break;
                case -274035920:
                    if (str.equals("ADDRESS_POSTCODE_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 417441502:
                    if (str.equals("ADDRESS_STATE_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 708423542:
                    if (str.equals("REQ_INFO_PHONE_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 863965605:
                    if (str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 889106340:
                    if (str.equals("REQ_INFO_EMAIL_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    shakeField(6);
                    return;
                case true:
                    shakeField(9);
                    return;
                case true:
                    shakeField(7);
                    return;
                case true:
                    shakeField(4);
                    return;
                case true:
                    shakeField(2);
                    return;
                case true:
                    shakeField(5);
                    return;
                case true:
                    shakeField(3);
                    return;
                case true:
                    shakeField(0);
                    return;
                case true:
                    shakeField(1);
                    return;
                default:
                    AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
                    return;
            }
        }
    }

    private TL_paymentRequestedInfo getRequestInfo() {
        TL_paymentRequestedInfo info = new TL_paymentRequestedInfo();
        if (this.paymentForm.invoice.name_requested) {
            info.name = this.inputFields[6].getText().toString();
            info.flags |= 1;
        }
        if (this.paymentForm.invoice.phone_requested) {
            info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
            info.flags |= 2;
        }
        if (this.paymentForm.invoice.email_requested) {
            info.email = this.inputFields[7].getText().toString().trim();
            info.flags |= 4;
        }
        if (this.paymentForm.invoice.shipping_address_requested) {
            info.shipping_address = new TL_postAddress();
            info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
            info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
            info.shipping_address.city = this.inputFields[2].getText().toString();
            info.shipping_address.state = this.inputFields[3].getText().toString();
            info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : "";
            info.shipping_address.post_code = this.inputFields[5].getText().toString();
            info.flags |= 8;
        }
        return info;
    }

    private void sendData() {
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            TL_payments_sendPaymentForm req = new TL_payments_sendPaymentForm();
            req.msg_id = this.messageObject.getId();
            if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && this.paymentForm.saved_credentials != null) {
                req.credentials = new TL_inputPaymentCredentialsSaved();
                req.credentials.id = this.paymentForm.saved_credentials.id;
                req.credentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            } else if (this.androidPayCredentials != null) {
                req.credentials = this.androidPayCredentials;
            } else {
                req.credentials = new TL_inputPaymentCredentials();
                req.credentials.save = this.saveCardInfo;
                req.credentials.data = new TL_dataJSON();
                req.credentials.data.data = this.paymentJson;
            }
            if (!(this.requestedInfo == null || this.requestedInfo.id == null)) {
                req.requested_info_id = this.requestedInfo.id;
                req.flags |= 1;
            }
            if (this.shippingOption != null) {
                req.shipping_option_id = this.shippingOption.id;
                req.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$Lambda$23(this, req), 2);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendData$40$PaymentFormActivity(TL_payments_sendPaymentForm req, TLObject response, TL_error error) {
        if (response == null) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$31(this, error, req));
        } else if (response instanceof TL_payments_paymentResult) {
            MessagesController.getInstance(this.currentAccount).processUpdates(((TL_payments_paymentResult) response).updates, false);
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$29(this));
        } else if (response instanceof TL_payments_paymentVerficationNeeded) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$30(this, response));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$38$PaymentFormActivity(TLObject response) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
        setDonePressed(false);
        this.webView.setVisibility(0);
        this.webviewLoading = true;
        showEditDoneProgress(true, true);
        this.progressView.setVisibility(0);
        this.doneItem.setEnabled(false);
        this.doneItem.getImageView().setVisibility(4);
        this.webView.loadUrl(((TL_payments_paymentVerficationNeeded) response).url);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$39$PaymentFormActivity(TL_error error, TL_payments_sendPaymentForm req) {
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
    }

    private void shakeField(int field) {
        shakeView(this.inputFields[field]);
    }

    private void shakeView(View view) {
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(view, 2.0f, 0);
    }

    private void setDonePressed(boolean value) {
        boolean z;
        boolean z2 = true;
        this.donePressed = value;
        this.swipeBackEnabled = !value;
        View backButton = this.actionBar.getBackButton();
        if (this.donePressed) {
            z = false;
        } else {
            z = true;
        }
        backButton.setEnabled(z);
        if (this.detailSettingsCell[0] != null) {
            TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[0];
            if (this.donePressed) {
                z2 = false;
            }
            textDetailSettingsCell.setEnabled(z2);
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
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
        } else {
            String password = this.inputFields[1].getText().toString();
            showEditDoneProgress(true, true);
            setDonePressed(true);
            TL_account_getPassword req = new TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$Lambda$24(this, password, req), 2);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkPassword$45$PaymentFormActivity(String password, TL_account_getPassword req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$25(this, error, response, password, req));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$44$PaymentFormActivity(TL_error error, TLObject response, String password, TL_account_getPassword req) {
        if (error == null) {
            TL_account_password currentPassword = (TL_account_password) response;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            } else if (currentPassword.has_password) {
                Utilities.globalQueue.postRunnable(new PaymentFormActivity$$Lambda$26(this, currentPassword, AndroidUtilities.getStringBytes(password)));
                return;
            } else {
                this.passwordOk = false;
                goToNextStep();
                return;
            }
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        showEditDoneProgress(true, false);
        setDonePressed(false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$43$PaymentFormActivity(TL_account_password currentPassword, byte[] passwordBytes) {
        byte[] x_bytes;
        if (currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(passwordBytes, currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        TL_account_getTmpPassword req1 = new TL_account_getTmpPassword();
        req1.period = 1800;
        RequestDelegate requestDelegate = new PaymentFormActivity$$Lambda$27(this, req1);
        TL_error error2;
        if (currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req1.password = SRPHelper.startCheck(x_bytes, currentPassword.srp_id, currentPassword.srp_B, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword.current_algo);
            if (req1.password == null) {
                error2 = new TL_error();
                error2.text = "ALGO_INVALID";
                requestDelegate.run(null, error2);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, requestDelegate, 10);
            return;
        }
        error2 = new TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error2);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$42$PaymentFormActivity(TL_account_getTmpPassword req1, TLObject response1, TL_error error1) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$28(this, response1, error1, req1));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$41$PaymentFormActivity(TLObject response1, TL_error error1, TL_account_getTmpPassword req1) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (response1 != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TL_account_tmpPassword) response1;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            goToNextStep();
        } else if (error1.text.equals("PASSWORD_HASH_INVALID")) {
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
            this.inputFields[1].setText("");
        } else {
            AlertsCreator.processError(this.currentAccount, error1, this, req1, new Object[0]);
        }
    }

    private void showEditDoneProgress(boolean animateDoneItem, final boolean show) {
        if (this.doneItemAnimation != null) {
            this.doneItemAnimation.cancel();
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (animateDoneItem && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (show) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else if (this.webView != null) {
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.doneItem.getImageView().setVisibility(0);
                this.doneItem.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (show) {
                            PaymentFormActivity.this.doneItem.getImageView().setVisibility(4);
                        } else {
                            PaymentFormActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (this.payTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (show) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (show) {
                            PaymentFormActivity.this.payTextView.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.progressViewButton.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public boolean onBackPressed() {
        return !this.donePressed;
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        ArrayList<ThemeDescription> arrayList = new ArrayList();
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
            for (a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        }
        if (this.radioCells != null) {
            for (a = 0; a < this.radioCells.length; a++) {
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(this.radioCells[a], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackground"));
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked"));
            }
        } else {
            arrayList.add(new ThemeDescription(null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackground"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked"));
        }
        for (a = 0; a < this.headerCell.length; a++) {
            arrayList.add(new ThemeDescription(this.headerCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.headerCell[a], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        }
        for (View themeDescription : this.sectionCell) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        }
        for (a = 0; a < this.bottomCell.length; a++) {
            arrayList.add(new ThemeDescription(this.bottomCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.bottomCell[a], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription(this.bottomCell[a], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteLinkText"));
        }
        for (a = 0; a < this.dividers.size(); a++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        }
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.codeFieldCell, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        for (a = 0; a < this.settingsCell.length; a++) {
            arrayList.add(new ThemeDescription(this.settingsCell[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.settingsCell[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.settingsCell[a], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText6"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        for (a = 1; a < this.detailSettingsCell.length; a++) {
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        }
        arrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
