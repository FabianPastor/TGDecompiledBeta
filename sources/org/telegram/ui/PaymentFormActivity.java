package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.TokenParser;
import j$.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
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
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity;

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
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private static final int done_button = 1;
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
    private FrameLayout googlePayButton;
    private FrameLayout googlePayContainer;
    private String googlePayCountryCode;
    /* access modifiers changed from: private */
    public TLRPC.TL_inputPaymentCredentialsGooglePay googlePayCredentials;
    /* access modifiers changed from: private */
    public JSONObject googlePayParameters;
    /* access modifiers changed from: private */
    public String googlePayPublicKey;
    private HeaderCell[] headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnCardChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnPhoneChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private boolean initGooglePay;
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
    private BaseFragment parentFragment;
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
    private TLRPC.TL_payments_paymentReceipt paymentReceipt;
    private PaymentsClient paymentsClient;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap;
    private ArrayList<TLRPC.TL_labeledPrice> prices;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public ContextProgressView progressViewButton;
    /* access modifiers changed from: private */
    public String providerApiKey;
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
    private boolean swipeBackEnabled;
    private TextView textView;
    /* access modifiers changed from: private */
    public Long tipAmount;
    private LinearLayout tipLayout;
    private TextPriceCell totalCell;
    private String[] totalPrice;
    private String totalPriceDecimal;
    /* access modifiers changed from: private */
    public TLRPC.TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    /* access modifiers changed from: private */
    public String webViewUrl;
    /* access modifiers changed from: private */
    public boolean webviewLoading;

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password);

        void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo);

        boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay);

        void onFragmentDestroyed();

        /* renamed from: org.telegram.ui.PaymentFormActivity$PaymentFormActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$didSelectNewCard(PaymentFormActivityDelegate _this, String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay) {
                return false;
            }

            public static void $default$didSelectNewAddress(PaymentFormActivityDelegate _this, TLRPC.TL_payments_validateRequestedInfo validateRequested) {
            }

            public static void $default$onFragmentDestroyed(PaymentFormActivityDelegate _this) {
            }

            public static void $default$currentPasswordUpdated(PaymentFormActivityDelegate _this, TLRPC.TL_account_password password) {
            }
        }
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$TelegramWebviewProxy$$ExternalSyntheticLambda0(this, eventName, eventData));
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-PaymentFormActivity$TelegramWebviewProxy  reason: not valid java name */
        public /* synthetic */ void m3522x74var_d2(String eventName, String eventData) {
            if (PaymentFormActivity.this.getParentActivity() != null && eventName.equals("payment_form_submit")) {
                try {
                    JSONObject jsonObject = new JSONObject(eventData);
                    String unused = PaymentFormActivity.this.paymentJson = jsonObject.getJSONObject("credentials").toString();
                    String unused2 = PaymentFormActivity.this.cardName = jsonObject.getString("title");
                } catch (Throwable e) {
                    String unused3 = PaymentFormActivity.this.paymentJson = eventData;
                    FileLog.e(e);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            PaymentFormActivity.this.presentFragment(new TwoStepVerificationSetupActivity(6, PaymentFormActivity.this.currentPassword));
        }
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentReceipt receipt) {
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
        TLRPC.TL_payments_paymentForm tL_payments_paymentForm = new TLRPC.TL_payments_paymentForm();
        this.paymentForm = tL_payments_paymentForm;
        this.paymentReceipt = receipt;
        tL_payments_paymentForm.bot_id = receipt.bot_id;
        this.paymentForm.invoice = receipt.invoice;
        this.paymentForm.provider_id = receipt.provider_id;
        this.paymentForm.users = receipt.users;
        this.shippingOption = receipt.shipping;
        if (receipt.tip_amount != 0) {
            this.tipAmount = Long.valueOf(receipt.tip_amount);
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(receipt.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = receipt.title;
        if (receipt.info != null) {
            TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = new TLRPC.TL_payments_validateRequestedInfo();
            this.validateRequest = tL_payments_validateRequestedInfo;
            tL_payments_validateRequestedInfo.peer = getMessagesController().getInputPeer(receipt.bot_id);
            this.validateRequest.info = receipt.info;
        }
        this.cardName = receipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, BaseFragment parentFragment2) {
        int step;
        TLRPC.TL_payments_paymentForm tL_payments_paymentForm = form;
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
        if (tL_payments_paymentForm.invoice.shipping_address_requested || tL_payments_paymentForm.invoice.email_requested || tL_payments_paymentForm.invoice.name_requested || tL_payments_paymentForm.invoice.phone_requested) {
            step = 0;
        } else if (tL_payments_paymentForm.saved_credentials != null) {
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
        init(form, message, step, (TLRPC.TL_payments_validatedRequestedInfo) null, (TLRPC.TL_shippingOption) null, (Long) null, (String) null, (String) null, (TLRPC.TL_payments_validateRequestedInfo) null, false, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, parentFragment2);
    }

    private PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, int step, TLRPC.TL_payments_validatedRequestedInfo validatedRequestedInfo, TLRPC.TL_shippingOption shipping, Long tips, String tokenJson, String card, TLRPC.TL_payments_validateRequestedInfo request, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, BaseFragment parent) {
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
        init(form, message, step, validatedRequestedInfo, shipping, tips, tokenJson, card, request, saveCard, googlePay, parent);
    }

    private void setCurrentPassword(TLRPC.TL_account_password password) {
        if (!password.has_password) {
            this.currentPassword = password;
            this.waitingForEmail = !TextUtils.isEmpty(password.email_unconfirmed_pattern);
            updatePasswordFields();
        } else if (getParentActivity() != null) {
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    private void init(TLRPC.TL_payments_paymentForm form, MessageObject message, int step, TLRPC.TL_payments_validatedRequestedInfo validatedRequestedInfo, TLRPC.TL_shippingOption shipping, Long tips, String tokenJson, String card, TLRPC.TL_payments_validateRequestedInfo request, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, BaseFragment parent) {
        TLRPC.TL_payments_paymentForm tL_payments_paymentForm = form;
        MessageObject messageObject2 = message;
        String str = card;
        boolean z = saveCard;
        this.currentStep = step;
        this.parentFragment = parent;
        this.paymentJson = tokenJson;
        this.googlePayCredentials = googlePay;
        this.requestedInfo = validatedRequestedInfo;
        this.paymentForm = tL_payments_paymentForm;
        this.shippingOption = shipping;
        this.tipAmount = tips;
        this.messageObject = messageObject2;
        this.saveCardInfo = z;
        this.isWebView = !"stripe".equals(tL_payments_paymentForm.native_provider) && !"smartglocal".equals(this.paymentForm.native_provider);
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(tL_payments_paymentForm.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject2.messageOwner.media.title;
        this.validateRequest = request;
        this.saveShippingInfo = true;
        if (z || this.currentStep == 4) {
            this.saveCardInfo = z;
        } else {
            this.saveCardInfo = this.paymentForm.saved_credentials != null;
        }
        if (str != null) {
            this.cardName = str;
        } else if (tL_payments_paymentForm.saved_credentials != null) {
            this.cardName = tL_payments_paymentForm.saved_credentials.title;
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int i = this.currentStep;
                if ((i == 2 || i == 6) && !this.paymentForm.invoice.test) {
                    getParentActivity().getWindow().setFlags(8192, 8192);
                } else if (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(8192);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:198:0x0827  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0838  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0842  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0857  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x087c  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x08b0  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x08d3  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x091a  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x091d  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x0929  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x099f  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x09a9  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x09e0 A[Catch:{ Exception -> 0x09ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0257  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r37) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            int r0 = r7.currentStep
            r9 = 6
            r10 = 5
            r11 = 4
            r12 = 3
            r13 = 2
            r14 = 1
            if (r0 != 0) goto L_0x001e
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r2 = "PaymentShippingInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x001e:
            if (r0 != r14) goto L_0x0030
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627081(0x7f0e0CLASSNAME, float:1.8881416E38)
            java.lang.String r2 = "PaymentShippingMethod"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0030:
            if (r0 != r13) goto L_0x0042
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627038(0x7f0e0c1e, float:1.888133E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0042:
            if (r0 != r12) goto L_0x0054
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627038(0x7f0e0c1e, float:1.888133E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0054:
            if (r0 != r11) goto L_0x008d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.test
            if (r0 == 0) goto L_0x007e
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Test "
            r1.append(r2)
            r2 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r3 = "PaymentCheckout"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.String r2 = "PaymentCheckout"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x008d:
            if (r0 != r10) goto L_0x00c6
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.test
            if (r0 == 0) goto L_0x00b7
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Test "
            r1.append(r2)
            r2 = 2131627073(0x7f0e0CLASSNAME, float:1.88814E38)
            java.lang.String r3 = "PaymentReceipt"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x00b7:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627073(0x7f0e0CLASSNAME, float:1.88814E38)
            java.lang.String r2 = "PaymentReceipt"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x00c6:
            if (r0 != r9) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627062(0x7f0e0CLASSNAME, float:1.8881378E38)
            java.lang.String r2 = "PaymentPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x00d6:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131165485(0x7var_d, float:1.7945188E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r0.setAllowOverlayTitle(r14)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.PaymentFormActivity$1 r1 = new org.telegram.ui.PaymentFormActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r15 = r0.createMenu()
            int r0 = r7.currentStep
            r6 = -1
            if (r0 == 0) goto L_0x0102
            if (r0 == r14) goto L_0x0102
            if (r0 == r13) goto L_0x0102
            if (r0 == r12) goto L_0x0102
            if (r0 == r11) goto L_0x0102
            if (r0 != r9) goto L_0x0144
        L_0x0102:
            r0 = 2131165515(0x7var_b, float:1.794525E38)
            r1 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.String r3 = "Done"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r15.addItemWithWidth(r14, r0, r1, r2)
            r7.doneItem = r0
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r8, r14)
            r7.progressView = r0
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r0.setScaleY(r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r0.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            org.telegram.ui.Components.ContextProgressView r1 = r7.progressView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r2)
            r0.addView(r1, r2)
        L_0x0144:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.fragmentView = r0
            android.view.View r0 = r7.fragmentView
            r5 = r0
            android.widget.FrameLayout r5 = (android.widget.FrameLayout) r5
            android.view.View r0 = r7.fragmentView
            java.lang.String r1 = "windowBackgroundGray"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.ScrollView r0 = new android.widget.ScrollView
            r0.<init>(r8)
            r7.scrollView = r0
            r0.setFillViewport(r14)
            android.widget.ScrollView r0 = r7.scrollView
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor((android.widget.ScrollView) r0, (int) r1)
            android.widget.ScrollView r0 = r7.scrollView
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            int r1 = r7.currentStep
            if (r1 != r11) goto L_0x0187
            r1 = 1111490560(0x42400000, float:48.0)
            r22 = 1111490560(0x42400000, float:48.0)
            goto L_0x018a
        L_0x0187:
            r1 = 0
            r22 = 0
        L_0x018a:
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r5.addView(r0, r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.linearLayout2 = r0
            r0.setOrientation(r14)
            android.widget.LinearLayout r0 = r7.linearLayout2
            r4 = 0
            r0.setClipChildren(r4)
            android.widget.ScrollView r0 = r7.scrollView
            android.widget.LinearLayout r1 = r7.linearLayout2
            android.widget.FrameLayout$LayoutParams r2 = new android.widget.FrameLayout$LayoutParams
            r3 = -2
            r2.<init>(r6, r3)
            r0.addView(r1, r2)
            int r0 = r7.currentStep
            java.lang.String r11 = "windowBackgroundWhiteBlackText"
            java.lang.String r10 = "windowBackgroundGrayShadow"
            java.lang.String r23 = "windowBackgroundWhite"
            if (r0 != 0) goto L_0x0a15
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r24 = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r25 = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x023d }
            java.io.InputStreamReader r2 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x023d }
            android.content.res.Resources r26 = r37.getResources()     // Catch:{ Exception -> 0x023d }
            android.content.res.AssetManager r1 = r26.getAssets()     // Catch:{ Exception -> 0x023d }
            java.lang.String r9 = "countries.txt"
            java.io.InputStream r1 = r1.open(r9)     // Catch:{ Exception -> 0x023d }
            r2.<init>(r1)     // Catch:{ Exception -> 0x023d }
            r0.<init>(r2)     // Catch:{ Exception -> 0x023d }
        L_0x01de:
            java.lang.String r1 = r0.readLine()     // Catch:{ Exception -> 0x023d }
            r2 = r1
            if (r1 == 0) goto L_0x0233
            java.lang.String r1 = ";"
            java.lang.String[] r1 = r2.split(r1)     // Catch:{ Exception -> 0x023d }
            java.util.ArrayList<java.lang.String> r9 = r7.countriesArray     // Catch:{ Exception -> 0x023d }
            r3 = r1[r13]     // Catch:{ Exception -> 0x023d }
            r9.add(r4, r3)     // Catch:{ Exception -> 0x023d }
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r7.countriesMap     // Catch:{ Exception -> 0x023d }
            r9 = r1[r13]     // Catch:{ Exception -> 0x023d }
            r6 = r1[r4]     // Catch:{ Exception -> 0x023d }
            r3.put(r9, r6)     // Catch:{ Exception -> 0x023d }
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r7.codesMap     // Catch:{ Exception -> 0x023d }
            r6 = r1[r4]     // Catch:{ Exception -> 0x023d }
            r9 = r1[r13]     // Catch:{ Exception -> 0x023d }
            r3.put(r6, r9)     // Catch:{ Exception -> 0x023d }
            r3 = r1[r14]     // Catch:{ Exception -> 0x023d }
            r6 = r1[r13]     // Catch:{ Exception -> 0x023d }
            r9 = r25
            r9.put(r3, r6)     // Catch:{ Exception -> 0x022f }
            int r3 = r1.length     // Catch:{ Exception -> 0x022f }
            if (r3 <= r12) goto L_0x021e
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r7.phoneFormatMap     // Catch:{ Exception -> 0x021a }
            r6 = r1[r4]     // Catch:{ Exception -> 0x021a }
            r4 = r1[r12]     // Catch:{ Exception -> 0x021a }
            r3.put(r6, r4)     // Catch:{ Exception -> 0x021a }
            goto L_0x021e
        L_0x021a:
            r0 = move-exception
            r6 = r24
            goto L_0x0242
        L_0x021e:
            r3 = r1[r14]     // Catch:{ Exception -> 0x022f }
            r4 = r1[r13]     // Catch:{ Exception -> 0x022f }
            r6 = r24
            r6.put(r3, r4)     // Catch:{ Exception -> 0x023b }
            r24 = r6
            r25 = r9
            r3 = -2
            r4 = 0
            r6 = -1
            goto L_0x01de
        L_0x022f:
            r0 = move-exception
            r6 = r24
            goto L_0x0242
        L_0x0233:
            r6 = r24
            r9 = r25
            r0.close()     // Catch:{ Exception -> 0x023b }
            goto L_0x0245
        L_0x023b:
            r0 = move-exception
            goto L_0x0242
        L_0x023d:
            r0 = move-exception
            r6 = r24
            r9 = r25
        L_0x0242:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0245:
            java.util.ArrayList<java.lang.String> r0 = r7.countriesArray
            org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6 r1 = org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6.INSTANCE
            java.util.Collections.sort(r0, r1)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r7.inputFields = r0
            r0 = 0
        L_0x0253:
            r1 = 10
            if (r0 >= r1) goto L_0x081d
            if (r0 != 0) goto L_0x028e
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>(r8)
            r3 = 0
            r1[r3] = r2
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            r2 = 2131627074(0x7f0e0CLASSNAME, float:1.8881402E38)
            java.lang.String r4 = "PaymentShippingAddress"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r3]
            r4 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r4)
            r1.addView(r2, r12)
            goto L_0x02dd
        L_0x028e:
            r3 = 0
            r1 = 6
            if (r0 != r1) goto L_0x02dd
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            r2.<init>(r8)
            r1[r3] = r2
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            r2 = r2[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r12)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>(r8)
            r1[r14] = r2
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r14]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r14]
            r2 = 2131627084(0x7f0e0c4c, float:1.8881422E38)
            java.lang.String r3 = "PaymentShippingReceiver"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r14]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r12)
        L_0x02dd:
            r1 = 8
            if (r0 != r1) goto L_0x0305
            android.widget.LinearLayout r1 = new android.widget.LinearLayout
            r1.<init>(r8)
            r2 = 0
            r1.setClipChildren(r2)
            r3 = r1
            android.widget.LinearLayout r3 = (android.widget.LinearLayout) r3
            r3.setOrientation(r2)
            android.widget.LinearLayout r2 = r7.linearLayout2
            r3 = 50
            r4 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r2.addView(r1, r12)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            goto L_0x0379
        L_0x0305:
            r1 = 9
            if (r0 != r1) goto L_0x0316
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r2 = 8
            r1 = r1[r2]
            android.view.ViewParent r1 = r1.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            goto L_0x0379
        L_0x0316:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r2 = 0
            r1.setClipChildren(r2)
            android.widget.LinearLayout r2 = r7.linearLayout2
            r3 = 50
            r4 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r2.addView(r1, r12)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            r2 = 5
            if (r0 == r2) goto L_0x0337
            r2 = 1
            goto L_0x0338
        L_0x0337:
            r2 = 0
        L_0x0338:
            if (r2 == 0) goto L_0x035b
            r3 = 7
            if (r0 != r3) goto L_0x0347
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            boolean r3 = r3.phone_requested
            if (r3 != 0) goto L_0x0347
            r2 = 0
            goto L_0x035b
        L_0x0347:
            r3 = 6
            if (r0 != r3) goto L_0x035b
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            boolean r3 = r3.phone_requested
            if (r3 != 0) goto L_0x035b
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            boolean r3 = r3.email_requested
            if (r3 != 0) goto L_0x035b
            r2 = 0
        L_0x035b:
            if (r2 == 0) goto L_0x0379
            org.telegram.ui.PaymentFormActivity$2 r3 = new org.telegram.ui.PaymentFormActivity$2
            r3.<init>(r8)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            java.util.ArrayList<android.view.View> r4 = r7.dividers
            r4.add(r3)
            android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
            r12 = 83
            r13 = -1
            r4.<init>(r13, r14, r12)
            r1.addView(r3, r4)
        L_0x0379:
            r2 = 9
            if (r0 != r2) goto L_0x0387
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            org.telegram.ui.Components.HintEditText r3 = new org.telegram.ui.Components.HintEditText
            r3.<init>(r8)
            r2[r0] = r3
            goto L_0x0390
        L_0x0387:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r3 = new org.telegram.ui.Components.EditTextBoldCursor
            r3.<init>(r8)
            r2[r0] = r3
        L_0x0390:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            r2.setTag(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 1098907648(0x41800000, float:16.0)
            r2.setTextSize(r14, r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            java.lang.String r3 = "windowBackgroundWhiteHintText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setHintTextColor(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setTextColor(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 0
            r2.setBackgroundDrawable(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setCursorColor(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCursorSize(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 1069547520(0x3fCLASSNAME, float:1.5)
            r2.setCursorWidth(r3)
            r2 = 4
            if (r0 != r2) goto L_0x03fc
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda13 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda13
            r3.<init>(r7)
            r2.setOnTouchListener(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 0
            r2.setInputType(r3)
        L_0x03fc:
            r2 = 9
            if (r0 == r2) goto L_0x041a
            r2 = 8
            if (r0 != r2) goto L_0x0405
            goto L_0x041a
        L_0x0405:
            r2 = 7
            if (r0 != r2) goto L_0x0410
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r2.setInputType(r14)
            goto L_0x0422
        L_0x0410:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 16385(0x4001, float:2.296E-41)
            r2.setInputType(r3)
            goto L_0x0422
        L_0x041a:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 3
            r2.setInputType(r3)
        L_0x0422:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 268435461(0x10000005, float:2.5243564E-29)
            r2.setImeOptions(r3)
            switch(r0) {
                case 0: goto L_0x0587;
                case 1: goto L_0x0559;
                case 2: goto L_0x052b;
                case 3: goto L_0x04fc;
                case 4: goto L_0x04ba;
                case 5: goto L_0x048b;
                case 6: goto L_0x045e;
                case 7: goto L_0x0431;
                default: goto L_0x042f;
            }
        L_0x042f:
            goto L_0x05b4
        L_0x0431:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627079(0x7f0e0CLASSNAME, float:1.8881412E38)
            java.lang.String r4 = "PaymentShippingEmailPlaceholder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            java.lang.String r2 = r2.email
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            java.lang.String r3 = r3.email
            r2.setText(r3)
            goto L_0x05b4
        L_0x045e:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627082(0x7f0e0c4a, float:1.8881418E38)
            java.lang.String r4 = "PaymentShippingName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            java.lang.String r2 = r2.name
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            java.lang.String r3 = r3.name
            r2.setText(r3)
            goto L_0x05b4
        L_0x048b:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627088(0x7f0e0CLASSNAME, float:1.888143E38)
            java.lang.String r4 = "PaymentShippingZipPlaceholder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r3.shipping_address
            java.lang.String r3 = r3.post_code
            r2.setText(r3)
            goto L_0x05b4
        L_0x04ba:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627078(0x7f0e0CLASSNAME, float:1.888141E38)
            java.lang.String r4 = "PaymentShippingCountry"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            java.lang.String r2 = r2.country_iso2
            java.lang.Object r2 = r9.get(r2)
            java.lang.String r2 = (java.lang.String) r2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r3.shipping_address
            java.lang.String r3 = r3.country_iso2
            r7.countryName = r3
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            if (r2 == 0) goto L_0x04f7
            r3 = r2
        L_0x04f7:
            r4.setText(r3)
            goto L_0x05b4
        L_0x04fc:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627087(0x7f0e0c4f, float:1.8881429E38)
            java.lang.String r4 = "PaymentShippingStatePlaceholder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r3.shipping_address
            java.lang.String r3 = r3.state
            r2.setText(r3)
            goto L_0x05b4
        L_0x052b:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627077(0x7f0e0CLASSNAME, float:1.8881408E38)
            java.lang.String r4 = "PaymentShippingCityPlaceholder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r3.shipping_address
            java.lang.String r3 = r3.city
            r2.setText(r3)
            goto L_0x05b4
        L_0x0559:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627076(0x7f0e0CLASSNAME, float:1.8881406E38)
            java.lang.String r4 = "PaymentShippingAddress2Placeholder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r3.shipping_address
            java.lang.String r3 = r3.street_line2
            r2.setText(r3)
            goto L_0x05b4
        L_0x0587:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r4 = "PaymentShippingAddress1Placeholder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setHint(r3)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x05b4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r2 = r2.shipping_address
            if (r2 == 0) goto L_0x05b4
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r3 = r3.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r3.shipping_address
            java.lang.String r3 = r3.street_line1
            r2.setText(r3)
        L_0x05b4:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r3 = r2[r0]
            r2 = r2[r0]
            int r2 = r2.length()
            r3.setSelection(r2)
            r2 = 8
            if (r0 != r2) goto L_0x063c
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r8)
            r7.textView = r2
            java.lang.String r3 = "+"
            r2.setText(r3)
            android.widget.TextView r2 = r7.textView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r7.textView
            r3 = 1098907648(0x41800000, float:16.0)
            r2.setTextSize(r14, r3)
            android.widget.TextView r2 = r7.textView
            r27 = -2
            r28 = -2
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 0
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r1.addView(r2, r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 0
            r2.setPadding(r3, r4, r4, r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 19
            r2.setGravity(r3)
            android.text.InputFilter[] r2 = new android.text.InputFilter[r14]
            android.text.InputFilter$LengthFilter r3 = new android.text.InputFilter$LengthFilter
            r12 = 5
            r3.<init>(r12)
            r2[r4] = r3
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r3.setFilters(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r27 = 55
            r29 = 0
            r31 = 1101529088(0x41a80000, float:21.0)
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r1.addView(r3, r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$3 r4 = new org.telegram.ui.PaymentFormActivity$3
            r4.<init>()
            r3.addTextChangedListener(r4)
            goto L_0x06aa
        L_0x063c:
            r2 = 9
            if (r0 != r2) goto L_0x0675
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 0
            r2.setPadding(r3, r3, r3, r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 19
            r2.setGravity(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r27 = -1
            r28 = -2
            r29 = 0
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r1.addView(r2, r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.ui.PaymentFormActivity$4 r3 = new org.telegram.ui.PaymentFormActivity$4
            r3.<init>()
            r2.addTextChangedListener(r3)
            goto L_0x06aa
        L_0x0675:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 0
            r2.setPadding(r4, r4, r4, r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x068d
            r3 = 5
            goto L_0x068e
        L_0x068d:
            r3 = 3
        L_0x068e:
            r2.setGravity(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r1.addView(r2, r3)
        L_0x06aa:
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17
            r3.<init>(r7)
            r2.setOnEditorActionListener(r3)
            r2 = 9
            if (r0 != r2) goto L_0x0812
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r2 = r2.email_to_provider
            if (r2 != 0) goto L_0x06e7
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r2 = r2.phone_to_provider
            if (r2 == 0) goto L_0x06cb
            goto L_0x06e7
        L_0x06cb:
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r8)
            r2[r14] = r3
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r7.sectionCell
            r3 = r3[r14]
            r4 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r4)
            r2.addView(r3, r13)
            r28 = r15
            goto L_0x07a3
        L_0x06e7:
            r2 = 0
            r3 = 0
        L_0x06e9:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0710
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.users
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC.User) r4
            long r12 = r4.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r14 = r7.paymentForm
            r28 = r15
            long r14 = r14.provider_id
            int r29 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r29 != 0) goto L_0x070a
            r2 = r4
        L_0x070a:
            int r3 = r3 + 1
            r15 = r28
            r14 = 1
            goto L_0x06e9
        L_0x0710:
            r28 = r15
            if (r2 == 0) goto L_0x071d
            java.lang.String r3 = r2.first_name
            java.lang.String r4 = r2.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r4)
            goto L_0x071f
        L_0x071d:
            java.lang.String r3 = ""
        L_0x071f:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r12 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r12.<init>(r8)
            r13 = 1
            r4[r13] = r12
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r13]
            r12 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r12, (java.lang.String) r10)
            r4.setBackgroundDrawable(r14)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r12 = r7.bottomCell
            r12 = r12[r13]
            r13 = -2
            r14 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r13)
            r4.addView(r12, r15)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.email_to_provider
            if (r4 == 0) goto L_0x076d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.phone_to_provider
            if (r4 == 0) goto L_0x076d
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r12 = 1
            r4 = r4[r12]
            r13 = 2131627070(0x7f0e0c3e, float:1.8881394E38)
            java.lang.Object[] r14 = new java.lang.Object[r12]
            r12 = 0
            r14[r12] = r3
            java.lang.String r12 = "PaymentPhoneEmailToProvider"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r14)
            r4.setText(r12)
            goto L_0x07a2
        L_0x076d:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.email_to_provider
            if (r4 == 0) goto L_0x078c
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r12 = 1
            r4 = r4[r12]
            r13 = 2131627057(0x7f0e0CLASSNAME, float:1.8881368E38)
            java.lang.Object[] r14 = new java.lang.Object[r12]
            r15 = 0
            r14[r15] = r3
            java.lang.String r15 = "PaymentEmailToProvider"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r13, r14)
            r4.setText(r13)
            goto L_0x07a2
        L_0x078c:
            r12 = 1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r12]
            r13 = 2131627071(0x7f0e0c3f, float:1.8881396E38)
            java.lang.Object[] r14 = new java.lang.Object[r12]
            r12 = 0
            r14[r12] = r3
            java.lang.String r12 = "PaymentPhoneToProvider"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r14)
            r4.setText(r12)
        L_0x07a2:
        L_0x07a3:
            org.telegram.ui.Cells.TextCheckCell r2 = new org.telegram.ui.Cells.TextCheckCell
            r2.<init>(r8)
            r7.checkCell1 = r2
            r3 = 1
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3)
            r2.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextCheckCell r2 = r7.checkCell1
            r3 = 2131627085(0x7f0e0c4d, float:1.8881424E38)
            java.lang.String r4 = "PaymentShippingSave"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r7.saveShippingInfo
            r12 = 0
            r2.setTextAndCheck(r3, r4, r12)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r3 = r7.checkCell1
            r4 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r4)
            r2.addView(r3, r13)
            org.telegram.ui.Cells.TextCheckCell r2 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6
            r3.<init>(r7)
            r2.setOnClickListener(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r4 = 0
            r2[r4] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r4]
            r3 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r10)
            r2.setBackgroundDrawable(r12)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r4]
            r3 = 2131627086(0x7f0e0c4e, float:1.8881426E38)
            java.lang.String r12 = "PaymentShippingSaveInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r2.setText(r3)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r4]
            r4 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r4)
            r2.addView(r3, r13)
            goto L_0x0814
        L_0x0812:
            r28 = r15
        L_0x0814:
            int r0 = r0 + 1
            r15 = r28
            r12 = 3
            r13 = 2
            r14 = 1
            goto L_0x0253
        L_0x081d:
            r28 = r15
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x0838
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 6
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
            goto L_0x083a
        L_0x0838:
            r1 = 8
        L_0x083a:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x084f
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x084f:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x0867
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 7
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
        L_0x0867:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 == 0) goto L_0x087c
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 9
            r0 = r0[r1]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x08a9
        L_0x087c:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 == 0) goto L_0x0890
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x08a9
        L_0x0890:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x08a1
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 6
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x08a9
        L_0x08a1:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 5
            r0 = r0[r2]
            r0.setImeOptions(r1)
        L_0x08a9:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = 1
            r2 = r0[r1]
            if (r2 == 0) goto L_0x08d3
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.name_requested
            if (r1 != 0) goto L_0x08ce
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.phone_requested
            if (r1 != 0) goto L_0x08ce
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x08cb
            goto L_0x08ce
        L_0x08cb:
            r1 = 8
            goto L_0x08cf
        L_0x08ce:
            r1 = 0
        L_0x08cf:
            r0.setVisibility(r1)
            goto L_0x08fc
        L_0x08d3:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r1 = 1
            r2 = r0[r1]
            if (r2 == 0) goto L_0x08fc
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.name_requested
            if (r1 != 0) goto L_0x08f8
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.phone_requested
            if (r1 != 0) goto L_0x08f8
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x08f5
            goto L_0x08f8
        L_0x08f5:
            r1 = 8
            goto L_0x08f9
        L_0x08f8:
            r1 = 0
        L_0x08f9:
            r0.setVisibility(r1)
        L_0x08fc:
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 1
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.name_requested
            if (r1 != 0) goto L_0x091d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.phone_requested
            if (r1 != 0) goto L_0x091d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x091a
            goto L_0x091d
        L_0x091a:
            r1 = 8
            goto L_0x091e
        L_0x091d:
            r1 = 0
        L_0x091e:
            r0.setVisibility(r1)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x098d
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 0
            r0 = r0[r1]
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r1]
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 1
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 2
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 3
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 4
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 5
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
        L_0x098d:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x09a9
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09a9
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r7.fillNumber(r0)
            goto L_0x09ad
        L_0x09a9:
            r1 = 0
            r7.fillNumber(r1)
        L_0x09ad:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 8
            r0 = r0[r1]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x0a12
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 == 0) goto L_0x0a12
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x09d3
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0a12
        L_0x09d3:
            r1 = 0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x09ea }
            java.lang.String r2 = "phone"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x09ea }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x09ea }
            if (r0 == 0) goto L_0x09e9
            java.lang.String r2 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x09ea }
            java.lang.String r2 = r2.toUpperCase()     // Catch:{ Exception -> 0x09ea }
            r1 = r2
        L_0x09e9:
            goto L_0x09ee
        L_0x09ea:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x09ee:
            if (r1 == 0) goto L_0x0a12
            java.lang.Object r0 = r6.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x0a12
            java.util.ArrayList<java.lang.String> r2 = r7.countriesArray
            int r2 = r2.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x0a12
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r4 = 8
            r3 = r3[r4]
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r7.countriesMap
            java.lang.Object r4 = r4.get(r0)
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            r3.setText(r4)
        L_0x0a12:
            r4 = r5
            goto L_0x1d59
        L_0x0a15:
            r28 = r15
            r1 = 9
            r2 = 2
            if (r0 != r2) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0a50
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0a4c }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm     // Catch:{ Exception -> 0x0a4c }
            org.telegram.tgnet.TLRPC$TL_dataJSON r1 = r1.native_params     // Catch:{ Exception -> 0x0a4c }
            java.lang.String r1 = r1.data     // Catch:{ Exception -> 0x0a4c }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0a4c }
            java.lang.String r1 = "google_pay_public_key"
            java.lang.String r1 = r0.optString(r1)     // Catch:{ Exception -> 0x0a4c }
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0a4c }
            if (r2 != 0) goto L_0x0a3b
            r7.googlePayPublicKey = r1     // Catch:{ Exception -> 0x0a4c }
        L_0x0a3b:
            java.lang.String r2 = "acquirer_bank_country"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ Exception -> 0x0a4c }
            r7.googlePayCountryCode = r2     // Catch:{ Exception -> 0x0a4c }
            java.lang.String r2 = "gpay_parameters"
            org.json.JSONObject r2 = r0.optJSONObject(r2)     // Catch:{ Exception -> 0x0a4c }
            r7.googlePayParameters = r2     // Catch:{ Exception -> 0x0a4c }
            goto L_0x0a50
        L_0x0a4c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a50:
            boolean r0 = r7.isWebView
            if (r0 == 0) goto L_0x0b64
            java.lang.String r0 = r7.googlePayPublicKey
            if (r0 != 0) goto L_0x0a5c
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0a5f
        L_0x0a5c:
            r36.initGooglePay(r37)
        L_0x0a5f:
            r36.createGooglePayButton(r37)
            android.widget.LinearLayout r0 = r7.linearLayout2
            android.widget.FrameLayout r1 = r7.googlePayContainer
            r2 = 50
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            r1 = 1
            r7.webviewLoading = r1
            r7.showEditDoneProgress(r1, r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r1 = 0
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r0.setEnabled(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            android.view.View r0 = r0.getContentView()
            r1 = 4
            r0.setVisibility(r1)
            org.telegram.ui.PaymentFormActivity$5 r0 = new org.telegram.ui.PaymentFormActivity$5
            r0.<init>(r8)
            r7.webView = r0
            android.webkit.WebSettings r0 = r0.getSettings()
            r1 = 1
            r0.setJavaScriptEnabled(r1)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r1)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x0abd
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r1 = 0
            r0.setMixedContentMode(r1)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r1 = r7.webView
            r2 = 1
            r0.setAcceptThirdPartyCookies(r1, r2)
        L_0x0abd:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 17
            if (r0 < r1) goto L_0x0ad0
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r1 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r2 = 0
            r1.<init>()
            java.lang.String r2 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r1, r2)
        L_0x0ad0:
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$6 r1 = new org.telegram.ui.PaymentFormActivity$6
            r1.<init>()
            r0.setWebViewClient(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            android.webkit.WebView r1 = r7.webView
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            r1.<init>(r8)
            r2 = 2
            r0[r2] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r2]
            r2 = -2
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r4)
            org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
            r0.<init>(r8)
            r7.checkCell1 = r0
            r1 = 1
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextCheckCell r0 = r7.checkCell1
            r1 = 2131627041(0x7f0e0CLASSNAME, float:1.8881335E38)
            java.lang.String r2 = "PaymentCardSavePaymentInformation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            boolean r2 = r7.saveCardInfo
            r3 = 0
            r0.setTextAndCheck(r1, r2, r3)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r1 = r7.checkCell1
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r4)
            org.telegram.ui.Cells.TextCheckCell r0 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r2 = 0
            r0[r2] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r2]
            r1 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            r36.updateSavePaymentField()
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r2]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            r4 = r5
            goto L_0x1d59
        L_0x0b64:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0bcf
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0bcb }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm     // Catch:{ Exception -> 0x0bcb }
            org.telegram.tgnet.TLRPC$TL_dataJSON r1 = r1.native_params     // Catch:{ Exception -> 0x0bcb }
            java.lang.String r1 = r1.data     // Catch:{ Exception -> 0x0bcb }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0bcb }
            r1 = r0
            java.lang.String r0 = "need_country"
            boolean r0 = r1.getBoolean(r0)     // Catch:{ Exception -> 0x0b7f }
            r7.need_card_country = r0     // Catch:{ Exception -> 0x0b7f }
            goto L_0x0b83
        L_0x0b7f:
            r0 = move-exception
            r2 = 0
            r7.need_card_country = r2     // Catch:{ Exception -> 0x0bcb }
        L_0x0b83:
            java.lang.String r0 = "need_zip"
            boolean r0 = r1.getBoolean(r0)     // Catch:{ Exception -> 0x0b8c }
            r7.need_card_postcode = r0     // Catch:{ Exception -> 0x0b8c }
            goto L_0x0b90
        L_0x0b8c:
            r0 = move-exception
            r2 = 0
            r7.need_card_postcode = r2     // Catch:{ Exception -> 0x0bcb }
        L_0x0b90:
            java.lang.String r0 = "need_cardholder_name"
            boolean r0 = r1.getBoolean(r0)     // Catch:{ Exception -> 0x0b99 }
            r7.need_card_name = r0     // Catch:{ Exception -> 0x0b99 }
            goto L_0x0b9d
        L_0x0b99:
            r0 = move-exception
            r2 = 0
            r7.need_card_name = r2     // Catch:{ Exception -> 0x0bcb }
        L_0x0b9d:
            java.lang.String r0 = "public_token"
            boolean r0 = r1.has(r0)     // Catch:{ Exception -> 0x0bcb }
            if (r0 == 0) goto L_0x0bae
            java.lang.String r0 = "public_token"
            java.lang.String r0 = r1.getString(r0)     // Catch:{ Exception -> 0x0bcb }
            r7.providerApiKey = r0     // Catch:{ Exception -> 0x0bcb }
            goto L_0x0bbc
        L_0x0bae:
            java.lang.String r0 = "publishable_key"
            java.lang.String r0 = r1.getString(r0)     // Catch:{ Exception -> 0x0bb7 }
            r7.providerApiKey = r0     // Catch:{ Exception -> 0x0bb7 }
            goto L_0x0bbc
        L_0x0bb7:
            r0 = move-exception
            java.lang.String r2 = ""
            r7.providerApiKey = r2     // Catch:{ Exception -> 0x0bcb }
        L_0x0bbc:
            java.lang.String r0 = "google_pay_hidden"
            r2 = 0
            boolean r0 = r1.optBoolean(r0, r2)     // Catch:{ Exception -> 0x0bcb }
            if (r0 != 0) goto L_0x0bc7
            r0 = 1
            goto L_0x0bc8
        L_0x0bc7:
            r0 = 0
        L_0x0bc8:
            r7.initGooglePay = r0     // Catch:{ Exception -> 0x0bcb }
            goto L_0x0bcf
        L_0x0bcb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0bcf:
            boolean r0 = r7.initGooglePay
            if (r0 == 0) goto L_0x0bee
            java.lang.String r0 = r7.providerApiKey
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0be7
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.lang.String r0 = r0.native_provider
            java.lang.String r1 = "stripe"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0beb
        L_0x0be7:
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0bee
        L_0x0beb:
            r36.initGooglePay(r37)
        L_0x0bee:
            r1 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x0bf4:
            if (r0 >= r1) goto L_0x0var_
            if (r0 != 0) goto L_0x0c2d
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>(r8)
            r3 = 0
            r1[r3] = r2
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            r2 = 2131627044(0x7f0e0CLASSNAME, float:1.8881341E38)
            java.lang.String r4 = "PaymentCardTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r6)
            goto L_0x0CLASSNAME
        L_0x0c2d:
            r1 = 4
            if (r0 != r1) goto L_0x0CLASSNAME
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>(r8)
            r3 = 1
            r1[r3] = r2
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            r2 = 2131627035(0x7f0e0c1b, float:1.8881323E38)
            java.lang.String r4 = "PaymentBillingAddress"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r6)
        L_0x0CLASSNAME:
            r1 = 3
            if (r0 == r1) goto L_0x0CLASSNAME
            r1 = 5
            if (r0 == r1) goto L_0x0CLASSNAME
            r1 = 4
            if (r0 != r1) goto L_0x0CLASSNAME
            boolean r1 = r7.need_card_postcode
            if (r1 == 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = 0
        L_0x0CLASSNAME:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r8)
            r3 = 0
            r2.setClipChildren(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r2.setBackgroundColor(r3)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r4 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r3.addView(r2, r9)
            r3 = 0
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r4[r0] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r4.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r9 = 1
            r4.setTextSize(r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r6)
            r4 = 3
            if (r0 != r4) goto L_0x0d23
            r6 = 1
            android.text.InputFilter[] r9 = new android.text.InputFilter[r6]
            android.text.InputFilter$LengthFilter r6 = new android.text.InputFilter$LengthFilter
            r6.<init>(r4)
            r4 = 0
            r9[r4] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.setFilters(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 130(0x82, float:1.82E-43)
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.text.method.PasswordTransformationMethod r6 = android.text.method.PasswordTransformationMethod.getInstance()
            r4.setTransformationMethod(r6)
            goto L_0x0d69
        L_0x0d23:
            if (r0 != 0) goto L_0x0d2e
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 3
            r4.setInputType(r6)
            goto L_0x0d69
        L_0x0d2e:
            r4 = 4
            if (r0 != r4) goto L_0x0d46
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14
            r6.<init>(r7)
            r4.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setInputType(r6)
            goto L_0x0d69
        L_0x0d46:
            r4 = 1
            if (r0 != r4) goto L_0x0d53
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 16386(0x4002, float:2.2962E-41)
            r4.setInputType(r6)
            goto L_0x0d69
        L_0x0d53:
            r4 = 2
            if (r0 != r4) goto L_0x0d60
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 4097(0x1001, float:5.741E-42)
            r4.setInputType(r6)
            goto L_0x0d69
        L_0x0d60:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 16385(0x4001, float:2.296E-41)
            r4.setInputType(r6)
        L_0x0d69:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r6)
            switch(r0) {
                case 0: goto L_0x0dcc;
                case 1: goto L_0x0dbb;
                case 2: goto L_0x0daa;
                case 3: goto L_0x0d99;
                case 4: goto L_0x0d88;
                case 5: goto L_0x0d77;
                default: goto L_0x0d76;
            }
        L_0x0d76:
            goto L_0x0ddd
        L_0x0d77:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627088(0x7f0e0CLASSNAME, float:1.888143E38)
            java.lang.String r9 = "PaymentShippingZipPlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0ddd
        L_0x0d88:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627078(0x7f0e0CLASSNAME, float:1.888141E38)
            java.lang.String r9 = "PaymentShippingCountry"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0ddd
        L_0x0d99:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627036(0x7f0e0c1c, float:1.8881325E38)
            java.lang.String r9 = "PaymentCardCvv"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0ddd
        L_0x0daa:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627039(0x7f0e0c1f, float:1.8881331E38)
            java.lang.String r9 = "PaymentCardName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0ddd
        L_0x0dbb:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627037(0x7f0e0c1d, float:1.8881327E38)
            java.lang.String r9 = "PaymentCardExpireDate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0ddd
        L_0x0dcc:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627040(0x7f0e0CLASSNAME, float:1.8881333E38)
            java.lang.String r9 = "PaymentCardNumber"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
        L_0x0ddd:
            if (r0 != 0) goto L_0x0dec
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$7 r6 = new org.telegram.ui.PaymentFormActivity$7
            r6.<init>()
            r4.addTextChangedListener(r6)
            goto L_0x0dfb
        L_0x0dec:
            r4 = 1
            if (r0 != r4) goto L_0x0dfb
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$8 r6 = new org.telegram.ui.PaymentFormActivity$8
            r6.<init>()
            r4.addTextChangedListener(r6)
        L_0x0dfb:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r4.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0e13
            r6 = 5
            goto L_0x0e14
        L_0x0e13:
            r6 = 3
        L_0x0e14:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1094713344(0x41400000, float:12.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r2.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20
            r6.<init>(r7)
            r4.setOnEditorActionListener(r6)
            r4 = 3
            if (r0 != r4) goto L_0x0e5a
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            r6.<init>(r8)
            r9 = 0
            r4[r9] = r6
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r9]
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r4.addView(r6, r13)
            goto L_0x0efb
        L_0x0e5a:
            r4 = 5
            if (r0 != r4) goto L_0x0ed8
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            r6.<init>(r8)
            r9 = 2
            r4[r9] = r6
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r9]
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r4.addView(r6, r13)
            org.telegram.ui.Cells.TextCheckCell r4 = new org.telegram.ui.Cells.TextCheckCell
            r4.<init>(r8)
            r7.checkCell1 = r4
            r6 = 1
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r4.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            r6 = 2131627041(0x7f0e0CLASSNAME, float:1.8881335E38)
            java.lang.String r9 = "PaymentCardSavePaymentInformation"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            boolean r9 = r7.saveCardInfo
            r12 = 0
            r4.setTextAndCheck(r6, r9, r12)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r6 = r7.checkCell1
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r4.addView(r6, r13)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8
            r6.<init>(r7)
            r4.setOnClickListener(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r6.<init>(r8)
            r9 = 0
            r4[r9] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r9]
            r6 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r4.setBackgroundDrawable(r12)
            r36.updateSavePaymentField()
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r9]
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r4.addView(r6, r13)
            goto L_0x0efb
        L_0x0ed8:
            if (r0 != 0) goto L_0x0efb
            r36.createGooglePayButton(r37)
            android.widget.FrameLayout r4 = r7.googlePayContainer
            r29 = -2
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0ee9
            r6 = 3
            goto L_0x0eea
        L_0x0ee9:
            r6 = 5
        L_0x0eea:
            r31 = r6 | 16
            r32 = 0
            r33 = 0
            r34 = 1082130432(0x40800000, float:4.0)
            r35 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r2.addView(r4, r6)
        L_0x0efb:
            if (r1 == 0) goto L_0x0f1a
            org.telegram.ui.PaymentFormActivity$9 r4 = new org.telegram.ui.PaymentFormActivity$9
            r4.<init>(r8)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r4)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r12 = -1
            r13 = 1
            r6.<init>(r12, r13, r9)
            r2.addView(r4, r6)
        L_0x0f1a:
            r4 = 4
            if (r0 != r4) goto L_0x0var_
            boolean r4 = r7.need_card_country
            if (r4 == 0) goto L_0x0f2f
        L_0x0var_:
            r4 = 5
            if (r0 != r4) goto L_0x0var_
            boolean r4 = r7.need_card_postcode
            if (r4 == 0) goto L_0x0f2f
        L_0x0var_:
            r4 = 2
            if (r0 != r4) goto L_0x0var_
            boolean r4 = r7.need_card_name
            if (r4 != 0) goto L_0x0var_
        L_0x0f2f:
            r4 = 8
            r2.setVisibility(r4)
        L_0x0var_:
            int r0 = r0 + 1
            r1 = 6
            goto L_0x0bf4
        L_0x0var_:
            boolean r0 = r7.need_card_country
            if (r0 != 0) goto L_0x0var_
            boolean r0 = r7.need_card_postcode
            if (r0 != 0) goto L_0x0var_
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 1
            r0 = r0[r1]
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = 0
            r0 = r0[r1]
            r0.setVisibility(r2)
        L_0x0var_:
            boolean r0 = r7.need_card_postcode
            if (r0 == 0) goto L_0x0var_
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 5
            r0 = r0[r1]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            r4 = r5
            goto L_0x1d59
        L_0x0var_:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 3
            r0 = r0[r2]
            r0.setImeOptions(r1)
            r4 = r5
            goto L_0x1d59
        L_0x0var_:
            r2 = 8
            r3 = 1
            if (r0 != r3) goto L_0x101c
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r1 = new org.telegram.ui.Cells.RadioCell[r0]
            r7.radioCells = r1
            r1 = 0
        L_0x0var_:
            if (r1 >= r0) goto L_0x0ff2
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r2 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r2 = r2.shipping_options
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r2
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            org.telegram.ui.Cells.RadioCell r4 = new org.telegram.ui.Cells.RadioCell
            r4.<init>(r8)
            r3[r1] = r4
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)
            r3.setTag(r4)
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            r4 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            r6 = 2
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r6 = r2.prices
            java.lang.String r6 = r7.getTotalPriceString(r6)
            r11 = 0
            r9[r11] = r6
            java.lang.String r6 = r2.title
            r9[r4] = r6
            java.lang.String r4 = "%s - %s"
            java.lang.String r4 = java.lang.String.format(r4, r9)
            if (r1 != 0) goto L_0x0fcf
            r6 = 1
            goto L_0x0fd0
        L_0x0fcf:
            r6 = 0
        L_0x0fd0:
            int r9 = r0 + -1
            if (r1 == r9) goto L_0x0fd6
            r9 = 1
            goto L_0x0fd7
        L_0x0fd6:
            r9 = 0
        L_0x0fd7:
            r3.setText(r4, r6, r9)
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9
            r4.<init>(r7)
            r3.setOnClickListener(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            r4 = r4[r1]
            r3.addView(r4)
            int r1 = r1 + 1
            goto L_0x0var_
        L_0x0ff2:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r2 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r2.<init>(r8)
            r3 = 0
            r1[r3] = r2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r2 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r10)
            r1.setBackgroundDrawable(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r3)
            r4 = r5
            goto L_0x1d59
        L_0x101c:
            r3 = 3
            if (r0 != r3) goto L_0x1271
            r1 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x1025:
            if (r0 >= r1) goto L_0x126e
            if (r0 != 0) goto L_0x105e
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>(r8)
            r3 = 0
            r1[r3] = r2
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r3]
            r2 = 2131627044(0x7f0e0CLASSNAME, float:1.8881341E38)
            java.lang.String r4 = "PaymentCardTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r3]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r2, r9)
            goto L_0x105f
        L_0x105e:
            r3 = 0
        L_0x105f:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r1.setClipChildren(r3)
            android.widget.LinearLayout r2 = r7.linearLayout2
            r3 = 50
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r2.addView(r1, r6)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            r2 = 1
            if (r0 == r2) goto L_0x107f
            r2 = 1
            goto L_0x1080
        L_0x107f:
            r2 = 0
        L_0x1080:
            if (r2 == 0) goto L_0x10a4
            r3 = 7
            if (r0 != r3) goto L_0x108f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.phone_requested
            if (r4 != 0) goto L_0x108f
            r2 = 0
            goto L_0x10a5
        L_0x108f:
            r4 = 6
            if (r0 != r4) goto L_0x10a5
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.phone_requested
            if (r4 != 0) goto L_0x10a5
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r4 = r4.email_requested
            if (r4 != 0) goto L_0x10a5
            r2 = 0
            goto L_0x10a5
        L_0x10a4:
            r3 = 7
        L_0x10a5:
            if (r2 == 0) goto L_0x10c4
            org.telegram.ui.PaymentFormActivity$10 r4 = new org.telegram.ui.PaymentFormActivity$10
            r4.<init>(r8)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r4)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r12 = -1
            r13 = 1
            r6.<init>(r12, r13, r9)
            r1.addView(r4, r6)
        L_0x10c4:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r4[r0] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r4.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r9 = 1
            r4.setTextSize(r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r6)
            if (r0 != 0) goto L_0x1137
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15 r6 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15.INSTANCE
            r4.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setInputType(r6)
            goto L_0x1149
        L_0x1137:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 129(0x81, float:1.81E-43)
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r6)
        L_0x1149:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r6)
            switch(r0) {
                case 0: goto L_0x116f;
                case 1: goto L_0x1157;
                default: goto L_0x1156;
            }
        L_0x1156:
            goto L_0x117d
        L_0x1157:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626203(0x7f0e08db, float:1.8879636E38)
            java.lang.String r9 = "LoginPassword"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.requestFocus()
            goto L_0x117d
        L_0x116f:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r6 = r6.saved_credentials
            java.lang.String r6 = r6.title
            r4.setText(r6)
        L_0x117d:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r4.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1195
            r6 = 5
            goto L_0x1196
        L_0x1195:
            r6 = 3
        L_0x1196:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1094713344(0x41400000, float:12.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r1.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16
            r6.<init>(r7)
            r4.setOnEditorActionListener(r6)
            r4 = 1
            if (r0 != r4) goto L_0x1269
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r9.<init>(r8)
            r12 = 0
            r6[r12] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r12]
            r9 = 2131627054(0x7f0e0c2e, float:1.8881362E38)
            java.lang.Object[] r13 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r4 = r4.saved_credentials
            java.lang.String r4 = r4.title
            r13[r12] = r4
            java.lang.String r4 = "PaymentConfirmationMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r9, r13)
            r6.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r12]
            r6 = 2131165465(0x7var_, float:1.7945148E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r4.setBackgroundDrawable(r6)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r12]
            r9 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r9)
            r4.addView(r6, r14)
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r6 = new org.telegram.ui.Cells.TextSettingsCell
            r6.<init>(r8)
            r4[r12] = r6
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            r4 = r4[r12]
            r6 = 1
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r4.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            r4 = r4[r12]
            r6 = 2131627055(0x7f0e0c2f, float:1.8881364E38)
            java.lang.String r9 = "PaymentConfirmationNewCard"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setText(r6, r12)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r6 = r7.settingsCell
            r6 = r6[r12]
            r9 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r9)
            r4.addView(r6, r14)
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            r4 = r4[r12]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda55 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda55
            r6.<init>(r7)
            r4.setOnClickListener(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r6.<init>(r8)
            r9 = 1
            r4[r9] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r9]
            r6 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r4.setBackgroundDrawable(r12)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r9]
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r4.addView(r6, r13)
        L_0x1269:
            int r0 = r0 + 1
            r1 = 2
            goto L_0x1025
        L_0x126e:
            r4 = r5
            goto L_0x1d59
        L_0x1271:
            r3 = 4
            if (r0 == r3) goto L_0x15de
            r3 = 5
            if (r0 != r3) goto L_0x1279
            goto L_0x15de
        L_0x1279:
            r1 = 6
            if (r0 != r1) goto L_0x15db
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r8)
            r7.codeFieldCell = r0
            r1 = 2131627023(0x7f0e0c0f, float:1.8881299E38)
            java.lang.String r2 = "PasswordCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = ""
            r3 = 0
            r0.setTextAndHint(r2, r1, r3)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r1 = 3
            r0.setInputType(r1)
            r1 = 6
            r0.setImeOptions(r1)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18
            r1.<init>(r7)
            r0.setOnEditorActionListener(r1)
            org.telegram.ui.PaymentFormActivity$20 r1 = new org.telegram.ui.PaymentFormActivity$20
            r1.<init>()
            r0.addTextChangedListener(r1)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r2 = r7.codeFieldCell
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r2 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r2.<init>(r8)
            r3 = 2
            r1[r3] = r2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r2 = 2131165465(0x7var_, float:1.7945148E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r10)
            r1.setBackgroundDrawable(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r6)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r2 = new org.telegram.ui.Cells.TextSettingsCell
            r2.<init>(r8)
            r3 = 1
            r1[r3] = r2
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3)
            r1.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            r1.setTag(r11)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r1.setTextColor(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            r2 = 2131627523(0x7f0e0e03, float:1.8882313E38)
            java.lang.String r4 = "ResendCode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2, r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r3]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r2, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda4
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r2 = new org.telegram.ui.Cells.TextSettingsCell
            r2.<init>(r8)
            r3 = 0
            r1[r3] = r2
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            r2 = 1
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            java.lang.String r2 = "windowBackgroundWhiteRedText3"
            r1.setTag(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            java.lang.String r2 = "windowBackgroundWhiteRedText3"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            r2 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r4 = "AbortPassword"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2, r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r3]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r2, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda5 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda5
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            r1 = 3
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r2
            r2 = 0
        L_0x139c:
            if (r2 >= r1) goto L_0x15d5
            if (r2 != 0) goto L_0x13d5
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r4 = 0
            r1[r4] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r4]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r4]
            r3 = 2131627069(0x7f0e0c3d, float:1.8881392E38)
            java.lang.String r6 = "PaymentPasswordTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r4]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
            goto L_0x140c
        L_0x13d5:
            r1 = 2
            if (r2 != r1) goto L_0x140c
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r4 = 1
            r1[r4] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r4]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r4]
            r3 = 2131627065(0x7f0e0CLASSNAME, float:1.8881384E38)
            java.lang.String r6 = "PaymentPasswordEmailTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r4]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
        L_0x140c:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r3 = 0
            r1.setClipChildren(r3)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r4 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r3.addView(r1, r9)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            if (r2 != 0) goto L_0x1447
            org.telegram.ui.PaymentFormActivity$21 r3 = new org.telegram.ui.PaymentFormActivity$21
            r3.<init>(r8)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r3)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r12 = -1
            r13 = 1
            r6.<init>(r12, r13, r9)
            r1.addView(r3, r6)
        L_0x1447:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r3[r2] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r2)
            r3.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 1098907648(0x41800000, float:16.0)
            r9 = 1
            r3.setTextSize(r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r3.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 0
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r3.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r6)
            if (r2 == 0) goto L_0x14c0
            r3 = 1
            if (r2 != r3) goto L_0x14ac
            goto L_0x14c0
        L_0x14ac:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 33
            r3.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r3.setImeOptions(r6)
            goto L_0x14dc
        L_0x14c0:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 129(0x81, float:1.81E-43)
            r3.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r3.setImeOptions(r6)
        L_0x14dc:
            switch(r2) {
                case 0: goto L_0x1502;
                case 1: goto L_0x14f1;
                case 2: goto L_0x14e0;
                default: goto L_0x14df;
            }
        L_0x14df:
            goto L_0x151a
        L_0x14e0:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 2131627063(0x7f0e0CLASSNAME, float:1.888138E38)
            java.lang.String r9 = "PaymentPasswordEmail"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            goto L_0x151a
        L_0x14f1:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 2131627068(0x7f0e0c3c, float:1.888139E38)
            java.lang.String r9 = "PaymentPasswordReEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            goto L_0x151a
        L_0x1502:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 2131627066(0x7f0e0c3a, float:1.8881386E38)
            java.lang.String r9 = "PaymentPasswordEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r3.requestFocus()
        L_0x151a:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r3.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1532
            r6 = 5
            goto L_0x1533
        L_0x1532:
            r6 = 3
        L_0x1533:
            r3.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1094713344(0x41400000, float:12.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r1.addView(r3, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r2]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19
            r6.<init>(r7)
            r3.setOnEditorActionListener(r6)
            r3 = 1
            if (r2 != r3) goto L_0x1596
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r6.<init>(r8)
            r9 = 0
            r3[r9] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r9]
            r6 = 2131627067(0x7f0e0c3b, float:1.8881388E38)
            java.lang.String r12 = "PaymentPasswordInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r3.setText(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r9]
            r6 = 2131165465(0x7var_, float:1.7945148E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r3.setBackgroundDrawable(r6)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r9]
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r3.addView(r6, r13)
            goto L_0x15d0
        L_0x1596:
            r3 = 2
            if (r2 != r3) goto L_0x15d0
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r6.<init>(r8)
            r9 = 1
            r3[r9] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r9]
            r6 = 2131627064(0x7f0e0CLASSNAME, float:1.8881382E38)
            java.lang.String r12 = "PaymentPasswordEmailInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r3.setText(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r9]
            r6 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r3.setBackgroundDrawable(r12)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r9]
            r9 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r9)
            r3.addView(r6, r13)
        L_0x15d0:
            int r2 = r2 + 1
            r1 = 3
            goto L_0x139c
        L_0x15d5:
            r36.updatePasswordFields()
            r4 = r5
            goto L_0x1d59
        L_0x15db:
            r4 = r5
            goto L_0x1d59
        L_0x15de:
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r8)
            r7.paymentInfoCell = r0
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r3)
            org.telegram.messenger.MessageObject r0 = r7.messageObject
            if (r0 == 0) goto L_0x15fe
            org.telegram.ui.Cells.PaymentInfoCell r3 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r0
            java.lang.String r4 = r7.currentBotName
            r3.setInvoice(r0, r4)
            goto L_0x1609
        L_0x15fe:
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r0 = r7.paymentReceipt
            if (r0 == 0) goto L_0x1609
            org.telegram.ui.Cells.PaymentInfoCell r3 = r7.paymentInfoCell
            java.lang.String r4 = r7.currentBotName
            r3.setReceipt(r0, r4)
        L_0x1609:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r3 = r7.paymentInfoCell
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r9)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r8)
            r9 = 0
            r0[r9] = r3
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r7.sectionCell
            r3 = r3[r9]
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r9)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r3.prices
            r0.<init>(r3)
            r7.prices = r0
            org.telegram.tgnet.TLRPC$TL_shippingOption r3 = r7.shippingOption
            if (r3 == 0) goto L_0x1643
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r3.prices
            r0.addAll(r3)
        L_0x1643:
            r3 = 1
            java.lang.String[] r0 = new java.lang.String[r3]
            r7.totalPrice = r0
            r0 = 0
        L_0x1649:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r7.prices
            int r3 = r3.size()
            if (r0 >= r3) goto L_0x1683
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r7.prices
            java.lang.Object r3 = r3.get(r0)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r3 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r3
            org.telegram.ui.Cells.TextPriceCell r6 = new org.telegram.ui.Cells.TextPriceCell
            r6.<init>(r8)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r6.setBackgroundColor(r9)
            java.lang.String r9 = r3.label
            org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
            long r13 = r3.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r15 = r15.invoice
            java.lang.String r15 = r15.currency
            java.lang.String r12 = r12.formatCurrencyString(r13, r15)
            r13 = 0
            r6.setTextAndValue(r9, r12, r13)
            android.widget.LinearLayout r9 = r7.linearLayout2
            r9.addView(r6)
            int r0 = r0 + 1
            goto L_0x1649
        L_0x1683:
            int r0 = r7.currentStep
            r3 = 5
            if (r0 != r3) goto L_0x16be
            java.lang.Long r0 = r7.tipAmount
            if (r0 == 0) goto L_0x16be
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r3)
            r3 = 2131627092(0x7f0e0CLASSNAME, float:1.8881439E38)
            java.lang.String r6 = "PaymentTip"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.Long r9 = r7.tipAmount
            long r12 = r9.longValue()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r9 = r9.invoice
            java.lang.String r9 = r9.currency
            java.lang.String r6 = r6.formatCurrencyString(r12, r9)
            r9 = 0
            r0.setTextAndValue(r3, r6, r9)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r3.addView(r0)
        L_0x16be:
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            r7.totalCell = r0
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r3)
            java.lang.String[] r0 = r7.totalPrice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r7.prices
            java.lang.String r3 = r7.getTotalPriceString(r3)
            r6 = 0
            r0[r6] = r3
            org.telegram.ui.Cells.TextPriceCell r0 = r7.totalCell
            r3 = 2131627096(0x7f0e0CLASSNAME, float:1.8881447E38)
            java.lang.String r9 = "PaymentTransactionTotal"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r9, r3)
            java.lang.String[] r9 = r7.totalPrice
            r9 = r9[r6]
            r6 = 1
            r0.setTextAndValue(r3, r9, r6)
            int r0 = r7.currentStep
            r3 = 4
            if (r0 != r3) goto L_0x19a4
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            int r0 = r0.flags
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x19a4
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r3 = 0
            r0.setClipChildren(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r3)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            java.util.ArrayList<java.lang.Long> r6 = r6.suggested_tip_amounts
            boolean r6 = r6.isEmpty()
            if (r6 == 0) goto L_0x171a
            r6 = 40
            goto L_0x171c
        L_0x171a:
            r6 = 78
        L_0x171c:
            r9 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r3.addView(r0, r6)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda56 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda56
            r3.<init>(r7)
            r0.setOnClickListener(r3)
            org.telegram.ui.Cells.TextPriceCell r3 = new org.telegram.ui.Cells.TextPriceCell
            r3.<init>(r8)
            r12 = r3
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r12.setBackgroundColor(r3)
            r3 = 2131627093(0x7f0e0CLASSNAME, float:1.888144E38)
            java.lang.String r6 = "PaymentTipOptional"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            java.lang.String r6 = ""
            r13 = 0
            r12.setTextAndValue(r3, r6, r13)
            r0.addView(r12)
            r3 = 1
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r7.inputFields = r6
            org.telegram.ui.Components.EditTextBoldCursor r3 = new org.telegram.ui.Components.EditTextBoldCursor
            r3.<init>(r8)
            r6[r13] = r3
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r13)
            r3.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 1098907648(0x41800000, float:16.0)
            r14 = 1
            r3.setTextSize(r14, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            java.lang.String r6 = "windowBackgroundWhiteGrayText2"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            java.lang.String r6 = "windowBackgroundWhiteGrayText2"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 0
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r3.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 3
            r3.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r3.setImeOptions(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
            r14 = 0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r11 = r11.invoice
            java.lang.String r11 = r11.currency
            java.lang.String r6 = r6.formatCurrencyString(r14, r11)
            r3.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setPadding(r13, r13, r13, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x17ef
            r6 = 3
            goto L_0x17f0
        L_0x17ef:
            r6 = 5
        L_0x17f0:
            r3.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r13]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1091567616(0x41100000, float:9.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r0.addView(r3, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r6 = 0
            r3 = r3[r6]
            org.telegram.ui.PaymentFormActivity$11 r11 = new org.telegram.ui.PaymentFormActivity$11
            r11.<init>()
            r3.addTextChangedListener(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r6]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21 r11 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21.INSTANCE
            r3.setOnEditorActionListener(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r6]
            r3.requestFocus()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<java.lang.Long> r3 = r3.suggested_tip_amounts
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x199f
            android.widget.HorizontalScrollView r3 = new android.widget.HorizontalScrollView
            r3.<init>(r8)
            r11 = r3
            r6 = 0
            r11.setHorizontalScrollBarEnabled(r6)
            r11.setVerticalScrollBarEnabled(r6)
            r11.setClipToPadding(r6)
            r3 = 1101529088(0x41a80000, float:21.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r13 = 1101529088(0x41a80000, float:21.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r11.setPadding(r3, r6, r13, r6)
            r3 = 1
            r11.setFillViewport(r3)
            r29 = -1
            r30 = 1106247680(0x41var_, float:30.0)
            r31 = 51
            r32 = 0
            r33 = 1110441984(0x42300000, float:44.0)
            r34 = 0
            r35 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r0.addView(r11, r3)
            r3 = 1
            int[] r13 = new int[r3]
            int[] r14 = new int[r3]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<java.lang.Long> r3 = r3.suggested_tip_amounts
            int r15 = r3.size()
            org.telegram.ui.PaymentFormActivity$12 r3 = new org.telegram.ui.PaymentFormActivity$12
            r17 = 9
            r1 = r3
            r9 = 8
            r2 = r36
            r4 = r3
            r9 = -2
            r3 = r37
            r9 = r4
            r4 = r15
            r18 = r12
            r12 = r5
            r5 = r13
            r19 = r12
            r12 = 0
            r6 = r14
            r1.<init>(r3, r4, r5, r6)
            r7.tipLayout = r9
            r9.setOrientation(r12)
            android.widget.LinearLayout r1 = r7.tipLayout
            r2 = 30
            r3 = 51
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createScroll(r4, r2, r3)
            r11.addView(r1, r2)
            java.lang.String r1 = "contacts_inviteBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2 = 0
        L_0x18b0:
            if (r2 >= r15) goto L_0x199c
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x18cb
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<java.lang.Long> r3 = r3.suggested_tip_amounts
            int r4 = r15 - r2
            r5 = 1
            int r4 = r4 - r5
            java.lang.Object r3 = r3.get(r4)
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            goto L_0x18db
        L_0x18cb:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<java.lang.Long> r3 = r3.suggested_tip_amounts
            java.lang.Object r3 = r3.get(r2)
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
        L_0x18db:
            org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            java.lang.String r6 = r6.currency
            java.lang.String r5 = r5.formatCurrencyString(r3, r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r8)
            r9 = 1096810496(0x41600000, float:14.0)
            r12 = 1
            r6.setTextSize(r12, r9)
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r6.setTypeface(r9)
            r6.setLines(r12)
            java.lang.Long r9 = java.lang.Long.valueOf(r3)
            r6.setTag(r9)
            r6.setMaxLines(r12)
            r6.setText(r5)
            r9 = 1097859072(0x41700000, float:15.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r12 = 1097859072(0x41700000, float:15.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r20 = r11
            r11 = 0
            r6.setPadding(r9, r11, r12, r11)
            java.lang.String r9 = "chats_secretName"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r9)
            r9 = 1097859072(0x41700000, float:15.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r11 = 536870911(0x1fffffff, float:1.0842021E-19)
            r11 = r11 & r1
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r9, r11)
            r6.setBackground(r9)
            r9 = 1
            r6.setSingleLine(r9)
            r9 = 17
            r6.setGravity(r9)
            android.widget.LinearLayout r9 = r7.tipLayout
            r29 = -2
            r30 = -1
            r31 = 19
            r32 = 0
            r33 = 0
            int r11 = r15 + -1
            if (r2 == r11) goto L_0x1955
            r34 = 9
            goto L_0x1957
        L_0x1955:
            r34 = 0
        L_0x1957:
            r35 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34, (int) r35)
            r9.addView(r6, r11)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10
            r9.<init>(r7, r6, r3)
            r6.setOnClickListener(r9)
            android.text.TextPaint r9 = r6.getPaint()
            float r9 = r9.measureText(r5)
            double r11 = (double) r9
            double r11 = java.lang.Math.ceil(r11)
            int r9 = (int) r11
            r11 = 1106247680(0x41var_, float:30.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r9 = r9 + r11
            r11 = 2131230944(0x7var_e0, float:1.8077955E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
            r6.setTag(r11, r12)
            r11 = 0
            r12 = r13[r11]
            int r12 = java.lang.Math.max(r12, r9)
            r13[r11] = r12
            r12 = r14[r11]
            int r12 = r12 + r9
            r14[r11] = r12
            int r2 = r2 + 1
            r11 = r20
            r12 = 0
            goto L_0x18b0
        L_0x199c:
            r20 = r11
            goto L_0x19a6
        L_0x199f:
            r19 = r5
            r18 = r12
            goto L_0x19a6
        L_0x19a4:
            r19 = r5
        L_0x19a6:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextPriceCell r1 = r7.totalCell
            r0.addView(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            r1.<init>(r8)
            r2 = 2
            r0[r2] = r1
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r2]
            r1 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r3)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r2]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r4)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r2 = 0
            r0[r2] = r1
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r2]
            r1 = 1
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r2]
            java.lang.String r3 = r7.cardName
            if (r3 == 0) goto L_0x1a18
            int r3 = r3.length()
            if (r3 <= r1) goto L_0x1a18
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = r7.cardName
            java.lang.String r4 = r4.substring(r2, r1)
            java.lang.String r2 = r4.toUpperCase()
            r3.append(r2)
            java.lang.String r2 = r7.cardName
            java.lang.String r2 = r2.substring(r1)
            r3.append(r2)
            java.lang.String r1 = r3.toString()
            goto L_0x1a1a
        L_0x1a18:
            java.lang.String r1 = r7.cardName
        L_0x1a1a:
            r2 = 2131627047(0x7f0e0CLASSNAME, float:1.8881347E38)
            java.lang.String r3 = "PaymentCheckoutMethod"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131165947(0x7var_fb, float:1.7946125E38)
            r4 = 1
            r0.setTextAndValueAndIcon(r1, r2, r3, r4)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r2 = 0
            r1 = r1[r2]
            r0.addView(r1)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1a45
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r2]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda57 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda57
            r1.<init>(r7)
            r0.setOnClickListener(r1)
        L_0x1a45:
            r0 = 0
            r1 = 0
        L_0x1a47:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r2.users
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x1a69
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r2.users
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            long r3 = r2.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            long r5 = r5.provider_id
            int r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r9 != 0) goto L_0x1a66
            r0 = r2
        L_0x1a66:
            int r1 = r1 + 1
            goto L_0x1a47
        L_0x1a69:
            if (r0 == 0) goto L_0x1ab8
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r3 = 1
            r1[r3] = r2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r3]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3)
            r1.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r3]
            java.lang.String r2 = r0.first_name
            java.lang.String r3 = r0.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = r2
            r4 = 2131627052(0x7f0e0c2c, float:1.8881358E38)
            java.lang.String r5 = "PaymentCheckoutProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 2131165952(0x7var_, float:1.7946136E38)
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r6 = r7.validateRequest
            if (r6 == 0) goto L_0x1aa9
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            if (r6 != 0) goto L_0x1aa7
            org.telegram.tgnet.TLRPC$TL_shippingOption r6 = r7.shippingOption
            if (r6 == 0) goto L_0x1aa9
        L_0x1aa7:
            r6 = 1
            goto L_0x1aaa
        L_0x1aa9:
            r6 = 0
        L_0x1aaa:
            r1.setTextAndValueAndIcon(r2, r4, r5, r6)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r4 = 1
            r2 = r2[r4]
            r1.addView(r2)
            goto L_0x1aba
        L_0x1ab8:
            java.lang.String r3 = ""
        L_0x1aba:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r1 = r7.validateRequest
            if (r1 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r1 = r1.info
            org.telegram.tgnet.TLRPC$TL_postAddress r1 = r1.shipping_address
            if (r1 == 0) goto L_0x1b00
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 2
            r1[r4] = r2
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r1.addView(r2)
            int r1 = r7.currentStep
            r2 = 4
            if (r1 != r2) goto L_0x1af5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda58 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda58
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1b00
        L_0x1af5:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
        L_0x1b00:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r1 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r1 = r1.info
            java.lang.String r1 = r1.name
            if (r1 == 0) goto L_0x1b44
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 3
            r1[r4] = r2
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r1.addView(r2)
            int r1 = r7.currentStep
            r2 = 4
            if (r1 != r2) goto L_0x1b39
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda1
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1b44
        L_0x1b39:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
        L_0x1b44:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r1 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r1 = r1.info
            java.lang.String r1 = r1.phone
            if (r1 == 0) goto L_0x1b87
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 4
            r1[r4] = r2
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r1.addView(r2)
            int r1 = r7.currentStep
            if (r1 != r4) goto L_0x1b7c
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda2
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1b87
        L_0x1b7c:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
        L_0x1b87:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r1 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r1 = r1.info
            java.lang.String r1 = r1.email
            if (r1 == 0) goto L_0x1bcb
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 5
            r1[r4] = r2
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r1.addView(r2)
            int r1 = r7.currentStep
            r2 = 4
            if (r1 != r2) goto L_0x1bc0
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda3 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda3
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1bcb
        L_0x1bc0:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
        L_0x1bcb:
            org.telegram.tgnet.TLRPC$TL_shippingOption r1 = r7.shippingOption
            if (r1 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 6
            r1[r4] = r2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = r7.shippingOption
            java.lang.String r2 = r2.title
            r5 = 2131627053(0x7f0e0c2d, float:1.888136E38)
            java.lang.String r6 = "PaymentCheckoutShippingMethod"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131165948(0x7var_fc, float:1.7946128E38)
            r9 = 0
            r1.setTextAndValueAndIcon(r2, r5, r6, r9)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r1.addView(r2)
        L_0x1CLASSNAME:
            r36.setAddressFields()
        L_0x1CLASSNAME:
            int r1 = r7.currentStep
            r2 = 4
            if (r1 != r2) goto L_0x1d2f
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.bottomLayout = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r1 < r2) goto L_0x1c2c
            android.widget.FrameLayout r1 = r7.bottomLayout
            java.lang.String r2 = "listSelectorSDK21"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r4 = "contacts_inviteBackground"
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r2, (java.lang.String) r4)
            r1.setBackgroundDrawable(r2)
            goto L_0x1CLASSNAME
        L_0x1c2c:
            android.widget.FrameLayout r1 = r7.bottomLayout
            java.lang.String r2 = "contacts_inviteBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
        L_0x1CLASSNAME:
            android.widget.FrameLayout r1 = r7.bottomLayout
            r2 = 48
            r4 = 80
            r5 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r2, (int) r4)
            r4 = r19
            r4.addView(r1, r2)
            android.widget.FrameLayout r1 = r7.bottomLayout
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12
            r2.<init>(r7, r3)
            r1.setOnClickListener(r2)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r7.payTextView = r1
            java.lang.String r2 = "contacts_inviteText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r7.payTextView
            r2 = 2131627050(0x7f0e0c2a, float:1.8881353E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String[] r9 = r7.totalPrice
            r11 = 0
            r9 = r9[r11]
            r6[r11] = r9
            java.lang.String r9 = "PaymentCheckoutPay"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r9, r2, r6)
            r1.setText(r2)
            android.widget.TextView r1 = r7.payTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r2)
            android.widget.TextView r1 = r7.payTextView
            r2 = 17
            r1.setGravity(r2)
            android.widget.TextView r1 = r7.payTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.FrameLayout r1 = r7.bottomLayout
            android.widget.TextView r2 = r7.payTextView
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r1.addView(r2, r5)
            org.telegram.ui.Components.ContextProgressView r1 = new org.telegram.ui.Components.ContextProgressView
            r2 = 0
            r1.<init>(r8, r2)
            r7.progressViewButton = r1
            r2 = 4
            r1.setVisibility(r2)
            java.lang.String r1 = "contacts_inviteText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            org.telegram.ui.Components.ContextProgressView r2 = r7.progressViewButton
            r5 = 805306367(0x2fffffff, float:4.6566126E-10)
            r5 = r5 & r1
            r2.setColors(r5, r1)
            android.widget.FrameLayout r2 = r7.bottomLayout
            org.telegram.ui.Components.ContextProgressView r5 = r7.progressViewButton
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r2.addView(r5, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r7.doneItem
            r5 = 0
            r2.setEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r7.doneItem
            android.view.View r2 = r2.getContentView()
            r5 = 4
            r2.setVisibility(r5)
            org.telegram.ui.PaymentFormActivity$18 r2 = new org.telegram.ui.PaymentFormActivity$18
            r2.<init>(r8)
            r7.webView = r2
            r5 = -1
            r2.setBackgroundColor(r5)
            android.webkit.WebView r2 = r7.webView
            android.webkit.WebSettings r2 = r2.getSettings()
            r5 = 1
            r2.setJavaScriptEnabled(r5)
            android.webkit.WebView r2 = r7.webView
            android.webkit.WebSettings r2 = r2.getSettings()
            r2.setDomStorageEnabled(r5)
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r2 < r5) goto L_0x1d11
            android.webkit.WebView r2 = r7.webView
            android.webkit.WebSettings r2 = r2.getSettings()
            r5 = 0
            r2.setMixedContentMode(r5)
            android.webkit.CookieManager r2 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r5 = r7.webView
            r6 = 1
            r2.setAcceptThirdPartyCookies(r5, r6)
        L_0x1d11:
            android.webkit.WebView r2 = r7.webView
            org.telegram.ui.PaymentFormActivity$19 r5 = new org.telegram.ui.PaymentFormActivity$19
            r5.<init>()
            r2.setWebViewClient(r5)
            android.webkit.WebView r2 = r7.webView
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r4.addView(r2, r5)
            android.webkit.WebView r2 = r7.webView
            r5 = 8
            r2.setVisibility(r5)
            goto L_0x1d31
        L_0x1d2f:
            r4 = r19
        L_0x1d31:
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            r2.<init>(r8)
            r5 = 1
            r1[r5] = r2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r5]
            r2 = 2131165466(0x7var_a, float:1.794515E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r10)
            r1.setBackgroundDrawable(r2)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            r2 = r2[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r1.addView(r2, r5)
        L_0x1d59:
            android.view.View r0 = r7.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3473lambda$createView$1$orgtelegramuiPaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda52(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3472lambda$createView$0$orgtelegramuiPaymentFormActivity(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
        this.countryName = country.shortname;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3482lambda$createView$2$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView2.getTag()).intValue();
            while (true) {
                int i2 = num + 1;
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i2 < editTextBoldCursorArr.length) {
                    num++;
                    if (num != 4 && ((View) editTextBoldCursorArr[num].getParent()).getVisibility() == 0) {
                        this.inputFields[num].requestFocus();
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

    /* renamed from: lambda$createView$3$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3492lambda$createView$3$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveShippingInfo;
        this.saveShippingInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3493lambda$createView$4$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3495lambda$createView$6$orgtelegramuiPaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda53(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3494lambda$createView$5$orgtelegramuiPaymentFormActivity(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3496lambda$createView$7$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView2.getTag()).intValue();
            while (true) {
                int i2 = num + 1;
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i2 < editTextBoldCursorArr.length) {
                    num++;
                    if (num == 4) {
                        num++;
                    }
                    if (((View) editTextBoldCursorArr[num].getParent()).getVisibility() == 0) {
                        this.inputFields[num].requestFocus();
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

    /* renamed from: lambda$createView$8$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3497lambda$createView$8$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3498lambda$createView$9$orgtelegramuiPaymentFormActivity(View v) {
        int num = ((Integer) v.getTag()).intValue();
        int a1 = 0;
        while (true) {
            RadioCell[] radioCellArr = this.radioCells;
            if (a1 < radioCellArr.length) {
                radioCellArr[a1].setChecked(num == a1, true);
                a1++;
            } else {
                return;
            }
        }
    }

    static /* synthetic */ boolean lambda$createView$10(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3474lambda$createView$11$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3475lambda$createView$12$orgtelegramuiPaymentFormActivity(View v) {
        this.passwordOk = false;
        goToNextStep();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3476lambda$createView$13$orgtelegramuiPaymentFormActivity(View v) {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    static /* synthetic */ boolean lambda$createView$14(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView2);
        return true;
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3477lambda$createView$15$orgtelegramuiPaymentFormActivity(TextView valueTextView, long amount, View v) {
        long amoumt = ((Long) valueTextView.getTag()).longValue();
        Long l = this.tipAmount;
        if (l == null || amoumt != l.longValue()) {
            this.inputFields[0].setText(LocaleController.getInstance().formatCurrencyString(amount, false, true, true, this.paymentForm.invoice.currency));
        } else {
            this.ignoreOnTextChange = true;
            this.inputFields[0].setText("");
            this.ignoreOnTextChange = false;
            this.tipAmount = 0L;
            updateTotalPrice();
        }
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[0].setSelection(editTextBoldCursorArr[0].length());
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3479lambda$createView$17$orgtelegramuiPaymentFormActivity(View v) {
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentCheckoutMethod", NUM), true);
            builder.setItems(new CharSequence[]{this.cardName, LocaleController.getString("PaymentCheckoutMethodNewCard", NUM)}, new int[]{NUM, NUM}, new PaymentFormActivity$$ExternalSyntheticLambda0(this));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3478lambda$createView$16$orgtelegramuiPaymentFormActivity(DialogInterface dialog, int which) {
        if (which == 1) {
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
            paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
                public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                    PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
                }

                public /* synthetic */ void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                    PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tL_payments_validateRequestedInfo);
                }

                public /* synthetic */ void onFragmentDestroyed() {
                    PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
                }

                public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay) {
                    PaymentFormActivity.this.paymentForm.saved_credentials = null;
                    String unused = PaymentFormActivity.this.paymentJson = tokenJson;
                    boolean unused2 = PaymentFormActivity.this.saveCardInfo = saveCard;
                    String unused3 = PaymentFormActivity.this.cardName = card;
                    TLRPC.TL_inputPaymentCredentialsGooglePay unused4 = PaymentFormActivity.this.googlePayCredentials = googlePay;
                    PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", NUM), true);
                    return false;
                }
            });
            presentFragment(paymentFormActivity);
        }
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3480lambda$createView$18$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                TLRPC.TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3481lambda$createView$19$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                TLRPC.TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3483lambda$createView$20$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                TLRPC.TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$21$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3484lambda$createView$21$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        activity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC.TL_account_password tL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo validateRequested) {
                TLRPC.TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = validateRequested;
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$23$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3486lambda$createView$23$orgtelegramuiPaymentFormActivity(String providerName, View v) {
        TLRPC.User user = this.botUser;
        if (user == null || user.verified) {
            showPayAlert(this.totalPrice[0]);
            return;
        }
        String botKey = "payment_warning_" + this.botUser.id;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        if (!preferences.getBoolean(botKey, false)) {
            preferences.edit().putBoolean(botKey, true).commit();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentWarning", NUM));
            builder.setMessage(LocaleController.formatString("PaymentWarningText", NUM, this.currentBotName, providerName));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$ExternalSyntheticLambda11(this));
            showDialog(builder.create());
            return;
        }
        showPayAlert(this.totalPrice[0]);
    }

    /* renamed from: lambda$createView$22$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3485lambda$createView$22$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    /* renamed from: lambda$createView$24$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3487lambda$createView$24$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* renamed from: lambda$createView$26$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3488lambda$createView$26$orgtelegramuiPaymentFormActivity(View v) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), PaymentFormActivity$$ExternalSyntheticLambda50.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    static /* synthetic */ void lambda$createView$25(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$28$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3490lambda$createView$28$orgtelegramuiPaymentFormActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        String text = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(text);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", NUM));
        builder.setPositiveButton(LocaleController.getString("Disable", NUM), new PaymentFormActivity$$ExternalSyntheticLambda22(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$createView$27$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3489lambda$createView$27$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* renamed from: lambda$createView$29$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m3491lambda$createView$29$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 6) {
            this.doneItem.performClick();
            return true;
        } else if (i != 5) {
            return false;
        } else {
            int num = ((Integer) textView2.getTag()).intValue();
            if (num == 0) {
                this.inputFields[1].requestFocus();
                return false;
            } else if (num != 1) {
                return false;
            } else {
                this.inputFields[2].requestFocus();
                return false;
            }
        }
    }

    /* access modifiers changed from: private */
    public void setAddressFields() {
        boolean z = false;
        if (this.validateRequest.info.shipping_address != null) {
            this.detailSettingsCell[2].setTextAndValueAndIcon(String.format("%s %s, %s, %s, %s, %s", new Object[]{this.validateRequest.info.shipping_address.street_line1, this.validateRequest.info.shipping_address.street_line2, this.validateRequest.info.shipping_address.city, this.validateRequest.info.shipping_address.state, this.validateRequest.info.shipping_address.country_iso2, this.validateRequest.info.shipping_address.post_code}), LocaleController.getString("PaymentShippingAddress", NUM), NUM, true);
        }
        if (this.validateRequest.info.name != null) {
            this.detailSettingsCell[3].setTextAndValueAndIcon(this.validateRequest.info.name, LocaleController.getString("PaymentCheckoutName", NUM), NUM, true);
        }
        if (this.validateRequest.info.phone != null) {
            this.detailSettingsCell[4].setTextAndValueAndIcon(PhoneFormat.getInstance().format(this.validateRequest.info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", NUM), NUM, (this.validateRequest.info.email == null && this.shippingOption == null) ? false : true);
        }
        if (this.validateRequest.info.email != null) {
            TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[5];
            String str = this.validateRequest.info.email;
            String string = LocaleController.getString("PaymentCheckoutEmail", NUM);
            if (this.shippingOption != null) {
                z = true;
            }
            textDetailSettingsCell.setTextAndValueAndIcon(str, string, NUM, z);
        }
    }

    /* access modifiers changed from: private */
    public void updateTotalPrice() {
        this.totalPrice[0] = getTotalPriceString(this.prices);
        this.totalCell.setTextAndValue(LocaleController.getString("PaymentTransactionTotal", NUM), this.totalPrice[0], true);
        TextView textView2 = this.payTextView;
        if (textView2 != null) {
            textView2.setText(LocaleController.formatString("PaymentCheckoutPay", NUM, this.totalPrice[0]));
        }
        if (this.tipLayout != null) {
            int color = Theme.getColor("contacts_inviteBackground");
            int N2 = this.tipLayout.getChildCount();
            for (int b = 0; b < N2; b++) {
                TextView child = (TextView) this.tipLayout.getChildAt(b);
                if (child.getTag().equals(this.tipAmount)) {
                    Theme.setDrawableColor(child.getBackground(), color);
                    child.setTextColor(Theme.getColor("contacts_inviteText"));
                } else {
                    Theme.setDrawableColor(child.getBackground(), NUM & color);
                    child.setTextColor(Theme.getColor("chats_secretName"));
                }
                child.invalidate();
            }
        }
    }

    private void createGooglePayButton(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        this.googlePayContainer = frameLayout;
        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.googlePayContainer.setVisibility(8);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.googlePayButton = frameLayout2;
        frameLayout2.setClickable(true);
        this.googlePayButton.setFocusable(true);
        this.googlePayButton.setBackgroundResource(NUM);
        if (this.googlePayPublicKey == null) {
            this.googlePayButton.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f));
        } else {
            this.googlePayButton.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        }
        this.googlePayContainer.addView(this.googlePayButton, LayoutHelper.createFrame(-1, 48.0f));
        this.googlePayButton.setOnClickListener(new PaymentFormActivity$$ExternalSyntheticLambda54(this));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(2.0f);
        linearLayout.setGravity(16);
        linearLayout.setOrientation(1);
        linearLayout.setDuplicateParentStateEnabled(true);
        this.googlePayButton.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setDuplicateParentStateEnabled(true);
        imageView.setImageResource(NUM);
        linearLayout.addView(imageView, LayoutHelper.createLinear(-1, 0, 1.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView2.setDuplicateParentStateEnabled(true);
        imageView2.setImageResource(NUM);
        this.googlePayButton.addView(imageView2, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004b A[Catch:{ JSONException -> 0x00b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x006a A[Catch:{ JSONException -> 0x00b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00a4 A[Catch:{ JSONException -> 0x00b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$createGooglePayButton$30$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3471x2e26daf8(android.view.View r9) {
        /*
            r8 = this;
            android.widget.FrameLayout r0 = r8.googlePayButton
            r1 = 0
            r0.setClickable(r1)
            org.json.JSONObject r0 = r8.getBaseRequest()     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONObject r1 = r8.getBaseCardPaymentMethod()     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r2 = r8.googlePayPublicKey     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r3 = "tokenizationSpecification"
            if (r2 == 0) goto L_0x0021
            org.json.JSONObject r2 = r8.googlePayParameters     // Catch:{ JSONException -> 0x00b4 }
            if (r2 != 0) goto L_0x0021
            org.telegram.ui.PaymentFormActivity$22 r2 = new org.telegram.ui.PaymentFormActivity$22     // Catch:{ JSONException -> 0x00b4 }
            r2.<init>()     // Catch:{ JSONException -> 0x00b4 }
            r1.put(r3, r2)     // Catch:{ JSONException -> 0x00b4 }
            goto L_0x0029
        L_0x0021:
            org.telegram.ui.PaymentFormActivity$23 r2 = new org.telegram.ui.PaymentFormActivity$23     // Catch:{ JSONException -> 0x00b4 }
            r2.<init>()     // Catch:{ JSONException -> 0x00b4 }
            r1.put(r3, r2)     // Catch:{ JSONException -> 0x00b4 }
        L_0x0029:
            java.lang.String r2 = "allowedPaymentMethods"
            org.json.JSONArray r3 = new org.json.JSONArray     // Catch:{ JSONException -> 0x00b4 }
            r3.<init>()     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONArray r3 = r3.put(r1)     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00b4 }
            r2.<init>()     // Catch:{ JSONException -> 0x00b4 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r8.paymentForm     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice     // Catch:{ JSONException -> 0x00b4 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r4 = r4.prices     // Catch:{ JSONException -> 0x00b4 }
            r3.<init>(r4)     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_shippingOption r4 = r8.shippingOption     // Catch:{ JSONException -> 0x00b4 }
            if (r4 == 0) goto L_0x0050
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r4 = r4.prices     // Catch:{ JSONException -> 0x00b4 }
            r3.addAll(r4)     // Catch:{ JSONException -> 0x00b4 }
        L_0x0050:
            java.lang.String r4 = "totalPrice"
            java.lang.String r5 = r8.getTotalPriceDecimalString(r3)     // Catch:{ JSONException -> 0x00b4 }
            r8.totalPriceDecimal = r5     // Catch:{ JSONException -> 0x00b4 }
            r2.put(r4, r5)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r4 = "totalPriceStatus"
            java.lang.String r5 = "FINAL"
            r2.put(r4, r5)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r4 = r8.googlePayCountryCode     // Catch:{ JSONException -> 0x00b4 }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ JSONException -> 0x00b4 }
            if (r4 != 0) goto L_0x0071
            java.lang.String r4 = "countryCode"
            java.lang.String r5 = r8.googlePayCountryCode     // Catch:{ JSONException -> 0x00b4 }
            r2.put(r4, r5)     // Catch:{ JSONException -> 0x00b4 }
        L_0x0071:
            java.lang.String r4 = "currencyCode"
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r8.paymentForm     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r5 = r5.currency     // Catch:{ JSONException -> 0x00b4 }
            r2.put(r4, r5)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r4 = "checkoutOption"
            java.lang.String r5 = "COMPLETE_IMMEDIATE_PURCHASE"
            r2.put(r4, r5)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r4 = "transactionInfo"
            r0.put(r4, r2)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r4 = "merchantInfo"
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00b4 }
            r5.<init>()     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r6 = "merchantName"
            java.lang.String r7 = r8.currentBotName     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONObject r5 = r5.put(r6, r7)     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r4, r5)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r4 = r0.toString()     // Catch:{ JSONException -> 0x00b4 }
            com.google.android.gms.wallet.PaymentDataRequest r4 = com.google.android.gms.wallet.PaymentDataRequest.fromJson(r4)     // Catch:{ JSONException -> 0x00b4 }
            if (r4 == 0) goto L_0x00b3
            com.google.android.gms.wallet.PaymentsClient r5 = r8.paymentsClient     // Catch:{ JSONException -> 0x00b4 }
            com.google.android.gms.tasks.Task r5 = r5.loadPaymentData(r4)     // Catch:{ JSONException -> 0x00b4 }
            android.app.Activity r6 = r8.getParentActivity()     // Catch:{ JSONException -> 0x00b4 }
            r7 = 991(0x3df, float:1.389E-42)
            com.google.android.gms.wallet.AutoResolveHelper.resolveTask(r5, r6, r7)     // Catch:{ JSONException -> 0x00b4 }
        L_0x00b3:
            goto L_0x00b8
        L_0x00b4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.m3471x2e26daf8(android.view.View):void");
    }

    private void updatePasswordFields() {
        if (this.currentStep == 6 && this.bottomCell[2] != null) {
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
                for (int a = 0; a < 3; a++) {
                    ((View) this.inputFields[a].getParent()).setVisibility(8);
                }
                for (int a2 = 0; a2 < this.dividers.size(); a2++) {
                    this.dividers.get(a2).setVisibility(8);
                }
                return;
            }
            showEditDoneProgress(true, false);
            if (this.waitingForEmail) {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                this.bottomCell[2].setVisibility(0);
                this.settingsCell[0].setVisibility(0);
                this.settingsCell[1].setVisibility(0);
                this.codeFieldCell.setVisibility(0);
                this.bottomCell[1].setText("");
                this.headerCell[0].setVisibility(8);
                this.headerCell[1].setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                for (int a3 = 0; a3 < 3; a3++) {
                    ((View) this.inputFields[a3].getParent()).setVisibility(8);
                }
                for (int a4 = 0; a4 < this.dividers.size(); a4++) {
                    this.dividers.get(a4).setVisibility(8);
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
            for (int a5 = 0; a5 < 3; a5++) {
                ((View) this.inputFields[a5].getParent()).setVisibility(0);
            }
            for (int a6 = 0; a6 < this.dividers.size(); a6++) {
                this.dividers.get(a6).setVisibility(0);
            }
        }
    }

    private void loadPasswordInfo() {
        if (!this.loadingPasswordInfo) {
            this.loadingPasswordInfo = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda41(this), 10);
        }
    }

    /* renamed from: lambda$loadPasswordInfo$33$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3502lambda$loadPasswordInfo$33$orgtelegramuiPaymentFormActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda32(this, error, response));
    }

    /* renamed from: lambda$loadPasswordInfo$32$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3501lambda$loadPasswordInfo$32$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject response) {
        this.loadingPasswordInfo = false;
        if (error == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            if (this.paymentForm != null && this.currentPassword.has_password) {
                this.paymentForm.password_missing = false;
                this.paymentForm.can_save_credentials = true;
                updateSavePaymentField();
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            PaymentFormActivity paymentFormActivity = this.passwordFragment;
            if (paymentFormActivity != null) {
                paymentFormActivity.setCurrentPassword(this.currentPassword);
            }
            if (!this.currentPassword.has_password && this.shortPollRunnable == null) {
                PaymentFormActivity$$ExternalSyntheticLambda24 paymentFormActivity$$ExternalSyntheticLambda24 = new PaymentFormActivity$$ExternalSyntheticLambda24(this);
                this.shortPollRunnable = paymentFormActivity$$ExternalSyntheticLambda24;
                AndroidUtilities.runOnUIThread(paymentFormActivity$$ExternalSyntheticLambda24, 5000);
            }
        }
    }

    /* renamed from: lambda$loadPasswordInfo$31$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3500lambda$loadPasswordInfo$31$orgtelegramuiPaymentFormActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo();
            this.shortPollRunnable = null;
        }
    }

    private void showAlertWithText(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void showPayAlert(String totalPrice2) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("PaymentTransactionMessage2", NUM, totalPrice2, this.currentBotName, this.currentItemName)));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$ExternalSyntheticLambda33(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showPayAlert$34$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3520lambda$showPayAlert$34$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        setDonePressed(true);
        sendData();
    }

    private JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    private JSONObject getBaseCardPaymentMethod() throws JSONException {
        List<String> SUPPORTED_NETWORKS = Arrays.asList(new String[]{"AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA"});
        List<String> SUPPORTED_METHODS = Arrays.asList(new String[]{"PAN_ONLY", "CRYPTOGRAM_3DS"});
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", new JSONArray(SUPPORTED_METHODS));
        parameters.put("allowedCardNetworks", new JSONArray(SUPPORTED_NETWORKS));
        cardPaymentMethod.put("parameters", parameters);
        return cardPaymentMethod;
    }

    public Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put("allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));
            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    private void initGooglePay(Context context) {
        IsReadyToPayRequest request;
        if (Build.VERSION.SDK_INT >= 19 && getParentActivity() != null) {
            this.paymentsClient = Wallet.getPaymentsClient(context, new Wallet.WalletOptions.Builder().setEnvironment(this.paymentForm.invoice.test ? 3 : 1).setTheme(1).build());
            Optional<JSONObject> isReadyToPayRequest = getIsReadyToPayRequest();
            if (isReadyToPayRequest.isPresent() && (request = IsReadyToPayRequest.fromJson(isReadyToPayRequest.get().toString())) != null) {
                this.paymentsClient.isReadyToPay(request).addOnCompleteListener(getParentActivity(), new PaymentFormActivity$$ExternalSyntheticLambda23(this));
            }
        }
    }

    /* renamed from: lambda$initGooglePay$35$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3499lambda$initGooglePay$35$orgtelegramuiPaymentFormActivity(Task task1) {
        if (task1.isSuccessful()) {
            FrameLayout frameLayout = this.googlePayContainer;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                return;
            }
            return;
        }
        FileLog.e("isReadyToPay failed", (Throwable) task1.getException());
    }

    private String getTotalPriceString(ArrayList<TLRPC.TL_labeledPrice> prices2) {
        long amount = 0;
        for (int a = 0; a < prices2.size(); a++) {
            amount += prices2.get(a).amount;
        }
        Long l = this.tipAmount;
        if (l != null) {
            amount += l.longValue();
        }
        return LocaleController.getInstance().formatCurrencyString(amount, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TLRPC.TL_labeledPrice> prices2) {
        long amount = 0;
        for (int a = 0; a < prices2.size(); a++) {
            amount += prices2.get(a).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(amount, this.paymentForm.invoice.currency, false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
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
            int i = this.currentStep;
            if ((i == 2 || i == 6) && Build.VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(8192);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        super.onFragmentDestroy();
        this.canceled = true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            WebView webView2 = this.webView;
            if (webView2 == null) {
                int i = this.currentStep;
                if (i == 2) {
                    AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda25(this), 100);
                } else if (i == 3) {
                    this.inputFields[1].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[1]);
                } else if (i == 4) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    if (editTextBoldCursorArr != null) {
                        editTextBoldCursorArr[0].requestFocus();
                    }
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

    /* renamed from: lambda$onTransitionAnimationEnd$36$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3504x9var_e5e() {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.twoStepPasswordChanged) {
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == 991) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda26(this, resultCode, data));
        }
    }

    /* renamed from: lambda$onActivityResultFragment$37$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3503x8avar_e0f(int resultCode, Intent data) {
        String paymentInfo;
        if (resultCode == -1) {
            PaymentData paymentData = PaymentData.getFromIntent(data);
            if (paymentData != null && (paymentInfo = paymentData.toJson()) != null) {
                try {
                    JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
                    JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
                    String string = tokenizationData.getString("type");
                    String token = tokenizationData.getString("token");
                    if (this.googlePayPublicKey == null) {
                        if (this.googlePayParameters == null) {
                            Token t = TokenParser.parseToken(token);
                            this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{t.getType(), t.getId()});
                            Card card = t.getCard();
                            this.cardName = card.getType() + " *" + card.getLast4();
                            goToNextStep();
                        }
                    }
                    TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = new TLRPC.TL_inputPaymentCredentialsGooglePay();
                    this.googlePayCredentials = tL_inputPaymentCredentialsGooglePay;
                    tL_inputPaymentCredentialsGooglePay.payment_token = new TLRPC.TL_dataJSON();
                    this.googlePayCredentials.payment_token.data = tokenizationData.toString();
                    String descriptions = paymentMethodData.optString("description");
                    if (!TextUtils.isEmpty(descriptions)) {
                        this.cardName = descriptions;
                    } else {
                        this.cardName = "Android Pay";
                    }
                    goToNextStep();
                } catch (JSONException e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                return;
            }
        } else if (resultCode == 1) {
            Status status = AutoResolveHelper.getStatusFromIntent(data);
            StringBuilder sb = new StringBuilder();
            sb.append("android pay error ");
            sb.append(status != null ? status.getStatusMessage() : "");
            FileLog.e(sb.toString());
        }
        showEditDoneProgress(true, false);
        setDonePressed(false);
        this.googlePayButton.setClickable(true);
    }

    /* access modifiers changed from: private */
    public void goToNextStep() {
        int nextStep;
        int nextStep2;
        int nextStep3;
        int i = this.currentStep;
        if (i == 0) {
            PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
            if (paymentFormActivityDelegate != null) {
                paymentFormActivityDelegate.didSelectNewAddress(this.validateRequest);
                finishFragment();
                return;
            }
            if (this.paymentForm.invoice.flexible) {
                nextStep3 = 1;
            } else if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                    nextStep3 = 4;
                } else {
                    nextStep3 = 3;
                }
            } else {
                nextStep3 = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep3, this.requestedInfo, (TLRPC.TL_shippingOption) null, (Long) null, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
        } else if (i == 1) {
            if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                    nextStep2 = 4;
                } else {
                    nextStep2 = 3;
                }
            } else {
                nextStep2 = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep2, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
        } else if (i == 2) {
            if (!this.paymentForm.password_missing || !this.saveCardInfo) {
                PaymentFormActivityDelegate paymentFormActivityDelegate2 = this.delegate;
                if (paymentFormActivityDelegate2 != null) {
                    paymentFormActivityDelegate2.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials);
                    finishFragment();
                    return;
                }
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                return;
            }
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
            this.passwordFragment = paymentFormActivity;
            paymentFormActivity.setCurrentPassword(this.currentPassword);
            this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                public /* synthetic */ void didSelectNewAddress(TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo) {
                    PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tL_payments_validateRequestedInfo);
                }

                public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay) {
                    if (PaymentFormActivity.this.delegate != null) {
                        PaymentFormActivity.this.delegate.didSelectNewCard(tokenJson, card, saveCard, googlePay);
                    }
                    if (PaymentFormActivity.this.isWebView) {
                        PaymentFormActivity.this.removeSelfFromStack();
                    }
                    return PaymentFormActivity.this.delegate != null;
                }

                public void onFragmentDestroyed() {
                    PaymentFormActivity unused = PaymentFormActivity.this.passwordFragment = null;
                }

                public void currentPasswordUpdated(TLRPC.TL_account_password password) {
                    TLRPC.TL_account_password unused = PaymentFormActivity.this.currentPassword = password;
                }
            });
            presentFragment(this.passwordFragment, this.isWebView);
        } else if (i == 3) {
            if (this.passwordOk) {
                nextStep = 4;
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
        } else if (i == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (i != 6) {
        } else {
            if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials)) {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
                return;
            }
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public void updateSavePaymentField() {
        if (this.bottomCell[0] != null && this.sectionCell[2] != null) {
            if ((this.paymentForm.password_missing || this.paymentForm.can_save_credentials) && (this.webView == null || !this.webviewLoading)) {
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
                        int index12 = index1 + len;
                        int index22 = index2 + len;
                        this.bottomCell[0].getTextView().setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        text.replace(index22, index22 + 1, "");
                        text.replace(index12, index12 + 1, "");
                        text.setSpan(new LinkSpan(), index12, index22 - 1, 33);
                    }
                }
                this.checkCell1.setEnabled(true);
                this.bottomCell[0].setText(text);
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

    public void fillNumber(String number) {
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            boolean allowCall = true;
            if (number != null || (tm.getSimState() != 1 && tm.getPhoneType() != 0)) {
                if (Build.VERSION.SDK_INT >= 23) {
                    allowCall = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                }
                if (number != null || allowCall) {
                    if (number == null) {
                        number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                    }
                    String textToSet = null;
                    boolean ok = false;
                    if (!TextUtils.isEmpty(number)) {
                        if (number.length() > 4) {
                            int a = 4;
                            while (true) {
                                if (a < 1) {
                                    break;
                                }
                                String sub = number.substring(0, a);
                                if (this.codesMap.get(sub) != null) {
                                    ok = true;
                                    textToSet = number.substring(a);
                                    this.inputFields[8].setText(sub);
                                    break;
                                }
                                a--;
                            }
                            if (!ok) {
                                textToSet = number.substring(1);
                                this.inputFields[8].setText(number.substring(0, 1));
                            }
                        }
                        if (textToSet != null) {
                            this.inputFields[9].setText(textToSet);
                            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                            editTextBoldCursorArr[9].setSelection(editTextBoldCursorArr[9].length());
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void sendSavePassword(boolean clear) {
        String firstPassword;
        String email;
        if (clear || this.codeFieldCell.getVisibility() != 0) {
            TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
            if (clear) {
                this.doneItem.setVisibility(0);
                req.new_settings = new TLRPC.TL_account_passwordInputSettings();
                req.new_settings.flags = 2;
                req.new_settings.email = "";
                req.password = new TLRPC.TL_inputCheckPasswordEmpty();
                email = null;
                firstPassword = null;
            } else {
                String firstPassword2 = this.inputFields[0].getText().toString();
                if (TextUtils.isEmpty(firstPassword2)) {
                    shakeField(0);
                    return;
                } else if (!firstPassword2.equals(this.inputFields[1].getText().toString())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    shakeField(1);
                    return;
                } else {
                    String email2 = this.inputFields[2].getText().toString();
                    if (email2.length() < 3) {
                        shakeField(2);
                        return;
                    }
                    int dot = email2.lastIndexOf(46);
                    int dog = email2.lastIndexOf(64);
                    if (dog < 0 || dot < dog) {
                        shakeField(2);
                        return;
                    }
                    req.password = new TLRPC.TL_inputCheckPasswordEmpty();
                    req.new_settings = new TLRPC.TL_account_passwordInputSettings();
                    req.new_settings.flags |= 1;
                    req.new_settings.hint = "";
                    req.new_settings.new_algo = this.currentPassword.new_algo;
                    TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                    tL_account_passwordInputSettings.flags = 2 | tL_account_passwordInputSettings.flags;
                    req.new_settings.email = email2.trim();
                    email = email2;
                    firstPassword = firstPassword2;
                }
            }
            showEditDoneProgress(true, true);
            Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda39(this, clear, email, firstPassword, req));
            return;
        }
        String code = this.codeFieldCell.getText();
        if (code.length() == 0) {
            shakeView(this.codeFieldCell);
            return;
        }
        showEditDoneProgress(true, true);
        TLRPC.TL_account_confirmPasswordEmail req2 = new TLRPC.TL_account_confirmPasswordEmail();
        req2.code = code;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new PaymentFormActivity$$ExternalSyntheticLambda42(this), 10);
    }

    /* renamed from: lambda$sendSavePassword$39$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3513lambda$sendSavePassword$39$orgtelegramuiPaymentFormActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda31(this, error));
    }

    /* renamed from: lambda$sendSavePassword$38$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3512lambda$sendSavePassword$38$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error) {
        String timeString;
        showEditDoneProgress(true, false);
        if (error == null) {
            if (getParentActivity() != null) {
                Runnable runnable = this.shortPollRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.shortPollRunnable = null;
                }
                goToNextStep();
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            shakeView(this.codeFieldCell);
            this.codeFieldCell.setText("", false);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
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

    /* renamed from: lambda$sendSavePassword$44$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3518lambda$sendSavePassword$44$orgtelegramuiPaymentFormActivity(boolean clear, String email, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda38(this, error, clear, response, email));
    }

    /* renamed from: lambda$sendSavePassword$45$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3519lambda$sendSavePassword$45$orgtelegramuiPaymentFormActivity(boolean clear, String email, String firstPassword, TLRPC.TL_account_updatePasswordSettings req) {
        RequestDelegate requestDelegate = new PaymentFormActivity$$ExternalSyntheticLambda49(this, clear, email);
        if (!clear) {
            byte[] newPasswordBytes = AndroidUtilities.getStringBytes(firstPassword);
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo);
                if (req.new_settings.new_password_hash == null) {
                    TLRPC.TL_error error = new TLRPC.TL_error();
                    error.text = "ALGO_INVALID";
                    requestDelegate.run((TLObject) null, error);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
                return;
            }
            TLRPC.TL_error error2 = new TLRPC.TL_error();
            error2.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run((TLObject) null, error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
    }

    /* renamed from: lambda$sendSavePassword$43$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3517lambda$sendSavePassword$43$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, boolean clear, TLObject response, String email) {
        String timeString;
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            showEditDoneProgress(true, false);
            if (clear) {
                this.currentPassword.has_password = false;
                this.currentPassword.current_algo = null;
                this.delegate.currentPasswordUpdated(this.currentPassword);
                finishFragment();
            } else if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                if (error == null) {
                    return;
                }
                if (error.text.equals("EMAIL_UNCONFIRMED") || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    this.emailCodeLength = Utilities.parseInt(error.text).intValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$ExternalSyntheticLambda43(this, email));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    Dialog dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                    }
                } else if (error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
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
            } else if (getParentActivity() != null) {
                goToNextStep();
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda48(this, clear), 8);
        }
    }

    /* renamed from: lambda$sendSavePassword$41$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3515lambda$sendSavePassword$41$orgtelegramuiPaymentFormActivity(boolean clear, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda36(this, error2, response2, clear));
    }

    /* renamed from: lambda$sendSavePassword$40$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3514lambda$sendSavePassword$40$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            sendSavePassword(clear);
        }
    }

    /* renamed from: lambda$sendSavePassword$42$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3516lambda$sendSavePassword$42$orgtelegramuiPaymentFormActivity(String email, DialogInterface dialogInterface, int i) {
        this.waitingForEmail = true;
        this.currentPassword.email_unconfirmed_pattern = email;
        updatePasswordFields();
    }

    /* access modifiers changed from: private */
    public boolean sendCardData() {
        Integer year;
        Integer month;
        int i;
        String[] args = this.inputFields[1].getText().toString().split("/");
        if (args.length == 2) {
            Integer month2 = Utilities.parseInt(args[0]);
            year = Utilities.parseInt(args[1]);
            month = month2;
        } else {
            year = null;
            month = null;
        }
        final Card card = new Card(this.inputFields[0].getText().toString(), month, year, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), (String) null, (String) null, (String) null, (String) null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), (String) null);
        this.cardName = card.getType() + " *" + card.getLast4();
        if (!card.validateNumber()) {
            shakeField(0);
            return false;
        }
        if (!card.validateExpMonth() || !card.validateExpYear()) {
            i = 1;
        } else if (!card.validateExpiryDate()) {
            i = 1;
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
                if ("stripe".equals(this.paymentForm.native_provider)) {
                    new Stripe(this.providerApiKey).createToken(card, new TokenCallback() {
                        public void onSuccess(Token token) {
                            if (!PaymentFormActivity.this.canceled) {
                                String unused = PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                                AndroidUtilities.runOnUIThread(new PaymentFormActivity$25$$ExternalSyntheticLambda0(this));
                            }
                        }

                        /* renamed from: lambda$onSuccess$0$org-telegram-ui-PaymentFormActivity$25  reason: not valid java name */
                        public /* synthetic */ void m3521lambda$onSuccess$0$orgtelegramuiPaymentFormActivity$25() {
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
                    return true;
                } else if (!"smartglocal".equals(this.paymentForm.native_provider)) {
                    return true;
                } else {
                    new AsyncTask<Object, Object, String>() {
                        /* access modifiers changed from: protected */
                        /* JADX WARNING: Unknown top exception splitter block from list: {B:31:0x0132=Splitter:B:31:0x0132, B:20:0x0106=Splitter:B:20:0x0106} */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public java.lang.String doInBackground(java.lang.Object... r14) {
                            /*
                                r13 = this;
                                java.lang.String r0 = "token"
                                java.lang.String r1 = "card"
                                java.lang.String r2 = ""
                                r3 = 0
                                org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x0135 }
                                r4.<init>()     // Catch:{ Exception -> 0x0135 }
                                org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x0135 }
                                r5.<init>()     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r6 = "number"
                                com.stripe.android.model.Card r7 = r8     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = r7.getNumber()     // Catch:{ Exception -> 0x0135 }
                                r5.put(r6, r7)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r6 = "expiration_month"
                                java.util.Locale r7 = java.util.Locale.US     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r8 = "%02d"
                                r9 = 1
                                java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x0135 }
                                com.stripe.android.model.Card r11 = r8     // Catch:{ Exception -> 0x0135 }
                                java.lang.Integer r11 = r11.getExpMonth()     // Catch:{ Exception -> 0x0135 }
                                r12 = 0
                                r10[r12] = r11     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = java.lang.String.format(r7, r8, r10)     // Catch:{ Exception -> 0x0135 }
                                r5.put(r6, r7)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r6 = "expiration_year"
                                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0135 }
                                r7.<init>()     // Catch:{ Exception -> 0x0135 }
                                r7.append(r2)     // Catch:{ Exception -> 0x0135 }
                                com.stripe.android.model.Card r8 = r8     // Catch:{ Exception -> 0x0135 }
                                java.lang.Integer r8 = r8.getExpYear()     // Catch:{ Exception -> 0x0135 }
                                r7.append(r8)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0135 }
                                r5.put(r6, r7)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r6 = "security_code"
                                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0135 }
                                r7.<init>()     // Catch:{ Exception -> 0x0135 }
                                r7.append(r2)     // Catch:{ Exception -> 0x0135 }
                                com.stripe.android.model.Card r8 = r8     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r8 = r8.getCVC()     // Catch:{ Exception -> 0x0135 }
                                r7.append(r8)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0135 }
                                r5.put(r6, r7)     // Catch:{ Exception -> 0x0135 }
                                r4.put(r1, r5)     // Catch:{ Exception -> 0x0135 }
                                org.telegram.ui.PaymentFormActivity r6 = org.telegram.ui.PaymentFormActivity.this     // Catch:{ Exception -> 0x0135 }
                                org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r6.paymentForm     // Catch:{ Exception -> 0x0135 }
                                org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice     // Catch:{ Exception -> 0x0135 }
                                boolean r6 = r6.test     // Catch:{ Exception -> 0x0135 }
                                if (r6 == 0) goto L_0x0080
                                java.net.URL r6 = new java.net.URL     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = "https://tgb-playground.smart-glocal.com/cds/v1/tokenize/card"
                                r6.<init>(r7)     // Catch:{ Exception -> 0x0135 }
                                goto L_0x0087
                            L_0x0080:
                                java.net.URL r6 = new java.net.URL     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = "https://tgb.smart-glocal.com/cds/v1/tokenize/card"
                                r6.<init>(r7)     // Catch:{ Exception -> 0x0135 }
                            L_0x0087:
                                java.net.URLConnection r7 = r6.openConnection()     // Catch:{ Exception -> 0x0135 }
                                java.net.HttpURLConnection r7 = (java.net.HttpURLConnection) r7     // Catch:{ Exception -> 0x0135 }
                                r3 = r7
                                r7 = 30000(0x7530, float:4.2039E-41)
                                r3.setConnectTimeout(r7)     // Catch:{ Exception -> 0x0135 }
                                r7 = 80000(0x13880, float:1.12104E-40)
                                r3.setReadTimeout(r7)     // Catch:{ Exception -> 0x0135 }
                                r3.setUseCaches(r12)     // Catch:{ Exception -> 0x0135 }
                                r3.setDoOutput(r9)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = "POST"
                                r3.setRequestMethod(r7)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = "Content-Type"
                                java.lang.String r8 = "application/json"
                                r3.setRequestProperty(r7, r8)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r7 = "X-PUBLIC-TOKEN"
                                org.telegram.ui.PaymentFormActivity r8 = org.telegram.ui.PaymentFormActivity.this     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r8 = r8.providerApiKey     // Catch:{ Exception -> 0x0135 }
                                r3.setRequestProperty(r7, r8)     // Catch:{ Exception -> 0x0135 }
                                java.io.OutputStream r7 = r3.getOutputStream()     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r8 = r4.toString()     // Catch:{ all -> 0x012a }
                                java.lang.String r9 = "UTF-8"
                                byte[] r8 = r8.getBytes(r9)     // Catch:{ all -> 0x012a }
                                r7.write(r8)     // Catch:{ all -> 0x012a }
                                if (r7 == 0) goto L_0x00cc
                                r7.close()     // Catch:{ Exception -> 0x0135 }
                            L_0x00cc:
                                int r7 = r3.getResponseCode()     // Catch:{ Exception -> 0x0135 }
                                r8 = 200(0xc8, float:2.8E-43)
                                if (r7 < r8) goto L_0x0106
                                r8 = 300(0x12c, float:4.2E-43)
                                if (r7 >= r8) goto L_0x0106
                                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x0135 }
                                r2.<init>()     // Catch:{ Exception -> 0x0135 }
                                org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ Exception -> 0x0135 }
                                java.io.InputStream r9 = r3.getInputStream()     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r9 = org.telegram.ui.PaymentFormActivity.getResponseBody(r9)     // Catch:{ Exception -> 0x0135 }
                                r8.<init>(r9)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r9 = "data"
                                org.json.JSONObject r9 = r8.getJSONObject(r9)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r9 = r9.getString(r0)     // Catch:{ Exception -> 0x0135 }
                                r2.put(r0, r9)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r0 = "type"
                                r2.put(r0, r1)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x0135 }
                                if (r3 == 0) goto L_0x0105
                                r3.disconnect()
                            L_0x0105:
                                return r0
                            L_0x0106:
                                boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0135 }
                                if (r0 == 0) goto L_0x0124
                                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0135 }
                                r0.<init>()     // Catch:{ Exception -> 0x0135 }
                                r0.append(r2)     // Catch:{ Exception -> 0x0135 }
                                java.io.InputStream r1 = r3.getErrorStream()     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r1 = org.telegram.ui.PaymentFormActivity.getResponseBody(r1)     // Catch:{ Exception -> 0x0135 }
                                r0.append(r1)     // Catch:{ Exception -> 0x0135 }
                                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0135 }
                                org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0135 }
                            L_0x0124:
                                if (r3 == 0) goto L_0x013c
                            L_0x0126:
                                r3.disconnect()
                                goto L_0x013c
                            L_0x012a:
                                r0 = move-exception
                                if (r7 == 0) goto L_0x0132
                                r7.close()     // Catch:{ all -> 0x0131 }
                                goto L_0x0132
                            L_0x0131:
                                r1 = move-exception
                            L_0x0132:
                                throw r0     // Catch:{ Exception -> 0x0135 }
                            L_0x0133:
                                r0 = move-exception
                                goto L_0x013e
                            L_0x0135:
                                r0 = move-exception
                                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0133 }
                                if (r3 == 0) goto L_0x013c
                                goto L_0x0126
                            L_0x013c:
                                r0 = 0
                                return r0
                            L_0x013e:
                                if (r3 == 0) goto L_0x0143
                                r3.disconnect()
                            L_0x0143:
                                goto L_0x0145
                            L_0x0144:
                                throw r0
                            L_0x0145:
                                goto L_0x0144
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.AnonymousClass26.doInBackground(java.lang.Object[]):java.lang.String");
                        }

                        /* access modifiers changed from: protected */
                        public void onPostExecute(String result) {
                            if (!PaymentFormActivity.this.canceled) {
                                if (result == null) {
                                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", NUM));
                                } else {
                                    String unused = PaymentFormActivity.this.paymentJson = result;
                                    PaymentFormActivity.this.goToNextStep();
                                }
                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                PaymentFormActivity.this.setDonePressed(false);
                            }
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[]{null, null, null});
                    return true;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return true;
            }
        } else {
            shakeField(5);
            return false;
        }
        shakeField(i);
        return false;
    }

    /* access modifiers changed from: private */
    public static String getResponseBody(InputStream responseStream) throws IOException {
        String rBody = new Scanner(responseStream, "UTF-8").useDelimiter("\\A").next();
        responseStream.close();
        return rBody;
    }

    /* access modifiers changed from: private */
    public void sendForm() {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = new TLRPC.TL_payments_validateRequestedInfo();
            this.validateRequest = tL_payments_validateRequestedInfo;
            tL_payments_validateRequestedInfo.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
            this.validateRequest.save = this.saveShippingInfo;
            this.validateRequest.msg_id = this.messageObject.getId();
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new PaymentFormActivity$$ExternalSyntheticLambda45(this, this.validateRequest), 2);
        }
    }

    /* renamed from: lambda$sendForm$49$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3511lambda$sendForm$49$orgtelegramuiPaymentFormActivity(TLObject req, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda28(this, response));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda34(this, error, req));
        }
    }

    /* renamed from: lambda$sendForm$47$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3509lambda$sendForm$47$orgtelegramuiPaymentFormActivity(TLObject response) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC.TL_payments_clearSavedInfo req1 = new TLRPC.TL_payments_clearSavedInfo();
            req1.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, PaymentFormActivity$$ExternalSyntheticLambda51.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    static /* synthetic */ void lambda$sendForm$46(TLObject response1, TLRPC.TL_error error1) {
    }

    /* renamed from: lambda$sendForm$48$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3510lambda$sendForm$48$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject req) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (error != null) {
            String str = error.text;
            char c = 65535;
            switch (str.hashCode()) {
                case -2092780146:
                    if (str.equals("ADDRESS_CITY_INVALID")) {
                        c = 4;
                        break;
                    }
                    break;
                case -1623547228:
                    if (str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                        c = 7;
                        break;
                    }
                    break;
                case -1224177757:
                    if (str.equals("ADDRESS_COUNTRY_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1031752045:
                    if (str.equals("REQ_INFO_NAME_INVALID")) {
                        c = 0;
                        break;
                    }
                    break;
                case -274035920:
                    if (str.equals("ADDRESS_POSTCODE_INVALID")) {
                        c = 5;
                        break;
                    }
                    break;
                case 417441502:
                    if (str.equals("ADDRESS_STATE_INVALID")) {
                        c = 6;
                        break;
                    }
                    break;
                case 708423542:
                    if (str.equals("REQ_INFO_PHONE_INVALID")) {
                        c = 1;
                        break;
                    }
                    break;
                case 863965605:
                    if (str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                        c = 8;
                        break;
                    }
                    break;
                case 889106340:
                    if (str.equals("REQ_INFO_EMAIL_INVALID")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    shakeField(6);
                    return;
                case 1:
                    shakeField(9);
                    return;
                case 2:
                    shakeField(7);
                    return;
                case 3:
                    shakeField(4);
                    return;
                case 4:
                    shakeField(2);
                    return;
                case 5:
                    shakeField(5);
                    return;
                case 6:
                    shakeField(3);
                    return;
                case 7:
                    shakeField(0);
                    return;
                case 8:
                    shakeField(1);
                    return;
                default:
                    AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
                    return;
            }
        }
    }

    private void sendData() {
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            TLRPC.TL_payments_sendPaymentForm req = new TLRPC.TL_payments_sendPaymentForm();
            req.msg_id = this.messageObject.getId();
            req.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
            req.form_id = this.paymentForm.form_id;
            if (UserConfig.getInstance(this.currentAccount).tmpPassword == null || this.paymentForm.saved_credentials == null) {
                TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = this.googlePayCredentials;
                if (tL_inputPaymentCredentialsGooglePay != null) {
                    req.credentials = tL_inputPaymentCredentialsGooglePay;
                } else {
                    req.credentials = new TLRPC.TL_inputPaymentCredentials();
                    req.credentials.save = this.saveCardInfo;
                    req.credentials.data = new TLRPC.TL_dataJSON();
                    req.credentials.data.data = this.paymentJson;
                }
            } else {
                req.credentials = new TLRPC.TL_inputPaymentCredentialsSaved();
                req.credentials.id = this.paymentForm.saved_credentials.id;
                req.credentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            }
            TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo = this.requestedInfo;
            if (!(tL_payments_validatedRequestedInfo == null || tL_payments_validatedRequestedInfo.id == null)) {
                req.requested_info_id = this.requestedInfo.id;
                req.flags = 1 | req.flags;
            }
            TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
            if (tL_shippingOption != null) {
                req.shipping_option_id = tL_shippingOption.id;
                req.flags |= 2;
            }
            if ((this.paymentForm.invoice.flags & 256) != 0) {
                Long l = this.tipAmount;
                req.tip_amount = l != null ? l.longValue() : 0;
                req.flags |= 4;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$ExternalSyntheticLambda47(this, req), 2);
        }
    }

    /* renamed from: lambda$sendData$53$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3508lambda$sendData$53$orgtelegramuiPaymentFormActivity(TLRPC.TL_payments_sendPaymentForm req, TLObject response, TLRPC.TL_error error) {
        if (response == null) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda37(this, error, req));
        } else if (response instanceof TLRPC.TL_payments_paymentResult) {
            TLRPC.Updates updates = ((TLRPC.TL_payments_paymentResult) response).updates;
            TLRPC.Message[] message = new TLRPC.Message[1];
            int a = 0;
            int N = updates.updates.size();
            while (true) {
                if (a >= N) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateNewMessage) {
                    message[0] = ((TLRPC.TL_updateNewMessage) update).message;
                    break;
                } else if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                    message[0] = ((TLRPC.TL_updateNewChannelMessage) update).message;
                    break;
                } else {
                    a++;
                }
            }
            getMessagesController().processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda40(this, message));
        } else if (response instanceof TLRPC.TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda27(this, response));
        }
    }

    /* renamed from: lambda$sendData$50$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3505lambda$sendData$50$orgtelegramuiPaymentFormActivity(TLRPC.Message[] message) {
        goToNextStep();
        if (this.parentFragment instanceof ChatActivity) {
            ((ChatActivity) this.parentFragment).getUndoView().showWithAction(0, 77, (Object) AndroidUtilities.replaceTags(LocaleController.formatString("PaymentInfoHint", NUM, this.totalPrice[0], this.currentItemName)), (Object) message[0], (Runnable) null, (Runnable) null);
        }
    }

    /* renamed from: lambda$sendData$51$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3506lambda$sendData$51$orgtelegramuiPaymentFormActivity(TLObject response) {
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
            String str = ((TLRPC.TL_payments_paymentVerificationNeeded) response).url;
            this.webViewUrl = str;
            webView3.loadUrl(str);
        }
    }

    /* renamed from: lambda$sendData$52$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3507lambda$sendData$52$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLRPC.TL_payments_sendPaymentForm req) {
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

    /* access modifiers changed from: private */
    public void setDonePressed(boolean value) {
        this.donePressed = value;
        this.swipeBackEnabled = !value;
        if (this.actionBar != null) {
            this.actionBar.getBackButton().setEnabled(!this.donePressed);
        }
        TextDetailSettingsCell[] textDetailSettingsCellArr = this.detailSettingsCell;
        if (textDetailSettingsCellArr[0] != null) {
            textDetailSettingsCellArr[0].setEnabled(!this.donePressed);
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
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
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
        } else {
            String password = this.inputFields[1].getText().toString();
            showEditDoneProgress(true, true);
            setDonePressed(true);
            TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$ExternalSyntheticLambda44(this, password, req), 2);
        }
    }

    /* renamed from: lambda$checkPassword$58$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3470lambda$checkPassword$58$orgtelegramuiPaymentFormActivity(String password, TLRPC.TL_account_getPassword req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda35(this, error, response, password, req));
    }

    /* renamed from: lambda$checkPassword$57$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3469lambda$checkPassword$57$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject response, String password, TLRPC.TL_account_getPassword req) {
        if (error == null) {
            TLRPC.TL_account_password currentPassword2 = (TLRPC.TL_account_password) response;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword2, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (!currentPassword2.has_password) {
                this.passwordOk = false;
                goToNextStep();
            } else {
                Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda30(this, currentPassword2, AndroidUtilities.getStringBytes(password)));
            }
        } else {
            AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    /* renamed from: lambda$checkPassword$56$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3468lambda$checkPassword$56$orgtelegramuiPaymentFormActivity(TLRPC.TL_account_password currentPassword2, byte[] passwordBytes) {
        byte[] x_bytes;
        if (currentPassword2.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(passwordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword2.current_algo);
        } else {
            x_bytes = null;
        }
        TLRPC.TL_account_getTmpPassword req1 = new TLRPC.TL_account_getTmpPassword();
        req1.period = 1800;
        RequestDelegate requestDelegate = new PaymentFormActivity$$ExternalSyntheticLambda46(this, req1);
        if (currentPassword2.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req1.password = SRPHelper.startCheck(x_bytes, currentPassword2.srp_id, currentPassword2.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword2.current_algo);
            if (req1.password == null) {
                TLRPC.TL_error error2 = new TLRPC.TL_error();
                error2.text = "ALGO_INVALID";
                requestDelegate.run((TLObject) null, error2);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error22 = new TLRPC.TL_error();
        error22.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run((TLObject) null, error22);
    }

    /* renamed from: lambda$checkPassword$55$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3467lambda$checkPassword$55$orgtelegramuiPaymentFormActivity(TLRPC.TL_account_getTmpPassword req1, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda29(this, response1, error1, req1));
    }

    /* renamed from: lambda$checkPassword$54$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m3466lambda$checkPassword$54$orgtelegramuiPaymentFormActivity(TLObject response1, TLRPC.TL_error error1, TLRPC.TL_account_getTmpPassword req1) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (response1 != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TLRPC.TL_account_tmpPassword) response1;
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

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean animateDoneItem, boolean show) {
        final boolean z = show;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animateDoneItem && this.doneItem != null) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.doneItemAnimation = animatorSet2;
            if (z) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
            } else if (this.webView != null) {
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            PaymentFormActivity.this.progressView.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (this.payTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.payTextView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.payTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{1.0f})});
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.payTextView, View.ALPHA, new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            PaymentFormActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.payTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
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
            for (int a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        }
        if (this.radioCells != null) {
            for (int a2 = 0; a2 < this.radioCells.length; a2++) {
                arrayList.add(new ThemeDescription(this.radioCells[a2], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.radioCells[a2], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription((View) this.radioCells[a2], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.radioCells[a2], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
                arrayList.add(new ThemeDescription((View) this.radioCells[a2], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        }
        for (int a3 = 0; a3 < this.headerCell.length; a3++) {
            arrayList.add(new ThemeDescription(this.headerCell[a3], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.headerCell[a3], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        }
        for (ShadowSectionCell themeDescription : this.sectionCell) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        }
        for (int a4 = 0; a4 < this.bottomCell.length; a4++) {
            arrayList.add(new ThemeDescription(this.bottomCell[a4], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.bottomCell[a4], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription((View) this.bottomCell[a4], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        }
        for (int a5 = 0; a5 < this.dividers.size(); a5++) {
            arrayList.add(new ThemeDescription(this.dividers.get(a5), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
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
        for (int a6 = 0; a6 < this.settingsCell.length; a6++) {
            arrayList.add(new ThemeDescription(this.settingsCell[a6], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.settingsCell[a6], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription((View) this.settingsCell[a6], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText6"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        for (int a7 = 1; a7 < this.detailSettingsCell.length; a7++) {
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a7], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.detailSettingsCell[a7], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.detailSettingsCell[a7], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        }
        arrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        return arrayList;
    }
}
