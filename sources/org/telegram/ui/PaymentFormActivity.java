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
import android.graphics.Canvas;
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
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
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
import java.util.Iterator;
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
import org.telegram.ui.Cells.RecurrentPaymentsAcceptCell;
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
    private static final int STEP_CHECKOUT = 4;
    private static final int STEP_CONFIRM_PASSWORD = 3;
    private static final int STEP_PAYMENT_INFO = 2;
    private static final int STEP_RECEIPT = 5;
    private static final int STEP_SET_PASSWORD_EMAIL = 6;
    private static final int STEP_SHIPPING_INFORMATION = 0;
    private static final int STEP_SHIPPING_METHODS = 1;
    private static final int done_button = 1;
    private TLRPC.User botUser;
    private TextInfoPrivacyCell[] bottomCell;
    private BottomFrameLayout bottomLayout;
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
    private String invoiceSlug;
    /* access modifiers changed from: private */
    public boolean isAcceptTermsChecked;
    private boolean isCheckoutPreview;
    /* access modifiers changed from: private */
    public boolean isWebView;
    private LinearLayout linearLayout2;
    private boolean loadingPasswordInfo;
    private MessageObject messageObject;
    private boolean needPayAfterTransition;
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
    private PaymentFormCallback paymentFormCallback;
    private PaymentInfoCell paymentInfoCell;
    /* access modifiers changed from: private */
    public String paymentJson;
    private TLRPC.TL_payments_paymentReceipt paymentReceipt;
    private boolean paymentStatusSent;
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
    private RecurrentPaymentsAcceptCell recurrentAcceptCell;
    private boolean recurrentAccepted;
    /* access modifiers changed from: private */
    public TLRPC.TL_payments_validatedRequestedInfo requestedInfo;
    private Theme.ResourcesProvider resourcesProvider;
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

    public enum InvoiceStatus {
        PAID,
        CANCELLED,
        PENDING,
        FAILED
    }

    public interface PaymentFormCallback {
        void onInvoiceStatusChanged(InvoiceStatus invoiceStatus);
    }

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
        public /* synthetic */ void m4197x74var_d2(String eventName, String eventData) {
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
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
                inputInvoice.peer = getMessagesController().getInputPeer(receipt.bot_id);
                this.validateRequest.invoice = inputInvoice;
            } else {
                TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
                inputInvoice2.slug = this.invoiceSlug;
                this.validateRequest.invoice = inputInvoice2;
            }
            this.validateRequest.info = receipt.info;
        }
        this.cardName = receipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, String invoiceSlug2, BaseFragment parentFragment2) {
        this(form, (MessageObject) null, invoiceSlug2, parentFragment2);
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, BaseFragment parentFragment2) {
        this(form, message, (String) null, parentFragment2);
    }

    public PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, String invoiceSlug2, BaseFragment parentFragment2) {
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
        this.isCheckoutPreview = true;
        init(form, message, invoiceSlug2, 4, (TLRPC.TL_payments_validatedRequestedInfo) null, (TLRPC.TL_shippingOption) null, (Long) null, (String) null, (String) null, (TLRPC.TL_payments_validateRequestedInfo) null, false, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, parentFragment2);
    }

    private PaymentFormActivity(TLRPC.TL_payments_paymentForm form, MessageObject message, String invoiceSlug2, int step, TLRPC.TL_payments_validatedRequestedInfo validatedRequestedInfo, TLRPC.TL_shippingOption shipping, Long tips, String tokenJson, String card, TLRPC.TL_payments_validateRequestedInfo request, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, BaseFragment parent) {
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
        init(form, message, invoiceSlug2, step, validatedRequestedInfo, shipping, tips, tokenJson, card, request, saveCard, googlePay, parent);
    }

    public void setPaymentFormCallback(PaymentFormCallback callback) {
        this.paymentFormCallback = callback;
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

    public void setResourcesProvider(Theme.ResourcesProvider provider) {
        this.resourcesProvider = provider;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    private void init(TLRPC.TL_payments_paymentForm form, MessageObject message, String slug, int step, TLRPC.TL_payments_validatedRequestedInfo validatedRequestedInfo, TLRPC.TL_shippingOption shipping, Long tips, String tokenJson, String card, TLRPC.TL_payments_validateRequestedInfo request, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, BaseFragment parent) {
        TLRPC.TL_payments_paymentForm tL_payments_paymentForm = form;
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
        this.messageObject = message;
        this.invoiceSlug = slug;
        this.saveCardInfo = z;
        this.isWebView = !"stripe".equals(tL_payments_paymentForm.native_provider) && !"smartglocal".equals(this.paymentForm.native_provider);
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(tL_payments_paymentForm.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = tL_payments_paymentForm.title;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: org.telegram.ui.PaymentFormActivity} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v142 */
    /* JADX WARNING: type inference failed for: r4v148 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x085c  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0866  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x087b  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0893  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x08a0  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x08d4  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x08f7  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x093e  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0941  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x094d  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x09c3  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x09cd  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0a04 A[Catch:{ Exception -> 0x0a0e }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0244  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r37) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            int r0 = r7.currentStep
            switch(r0) {
                case 0: goto L_0x00b8;
                case 1: goto L_0x00a9;
                case 2: goto L_0x009a;
                case 3: goto L_0x008b;
                case 4: goto L_0x0054;
                case 5: goto L_0x001b;
                case 6: goto L_0x000b;
                default: goto L_0x0009;
            }
        L_0x0009:
            goto L_0x00c7
        L_0x000b:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627396(0x7f0e0d84, float:1.8882055E38)
            java.lang.String r2 = "PaymentPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x001b:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.test
            if (r0 == 0) goto L_0x0044
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Test "
            r1.append(r2)
            r2 = 2131627407(0x7f0e0d8f, float:1.8882078E38)
            java.lang.String r3 = "PaymentReceipt"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x0044:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627407(0x7f0e0d8f, float:1.8882078E38)
            java.lang.String r2 = "PaymentReceipt"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x0054:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.test
            if (r0 == 0) goto L_0x007c
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Test "
            r1.append(r2)
            r2 = 2131627377(0x7f0e0d71, float:1.8882017E38)
            java.lang.String r3 = "PaymentCheckout"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x007c:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627377(0x7f0e0d71, float:1.8882017E38)
            java.lang.String r2 = "PaymentCheckout"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x008b:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627370(0x7f0e0d6a, float:1.8882003E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x009a:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627370(0x7f0e0d6a, float:1.8882003E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x00a9:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627415(0x7f0e0d97, float:1.8882094E38)
            java.lang.String r2 = "PaymentShippingMethod"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c7
        L_0x00b8:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627414(0x7f0e0d96, float:1.8882092E38)
            java.lang.String r2 = "PaymentShippingInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x00c7:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131165449(0x7var_, float:1.7945115E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r9 = 1
            r0.setAllowOverlayTitle(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.PaymentFormActivity$1 r1 = new org.telegram.ui.PaymentFormActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r10 = r0.createMenu()
            int r0 = r7.currentStep
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 4
            r13 = -1
            switch(r0) {
                case 0: goto L_0x00ef;
                case 1: goto L_0x00ef;
                case 2: goto L_0x00ef;
                case 3: goto L_0x00ef;
                case 4: goto L_0x00ef;
                case 5: goto L_0x00ee;
                case 6: goto L_0x00ef;
                default: goto L_0x00ee;
            }
        L_0x00ee:
            goto L_0x012f
        L_0x00ef:
            r0 = 2131165450(0x7var_a, float:1.7945117E38)
            r1 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 2131625525(0x7f0e0635, float:1.887826E38)
            java.lang.String r3 = "Done"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.addItemWithWidth((int) r9, (int) r0, (int) r1, (java.lang.CharSequence) r2)
            r7.doneItem = r0
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r8, r9)
            r7.progressView = r0
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r0.setScaleY(r1)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r0.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            org.telegram.ui.Components.ContextProgressView r1 = r7.progressView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r11)
            r0.addView(r1, r2)
        L_0x012f:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.fragmentView = r0
            android.view.View r0 = r7.fragmentView
            r14 = r0
            android.widget.FrameLayout r14 = (android.widget.FrameLayout) r14
            android.view.View r0 = r7.fragmentView
            java.lang.String r1 = "windowBackgroundGray"
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.ScrollView r0 = new android.widget.ScrollView
            r0.<init>(r8)
            r7.scrollView = r0
            r0.setFillViewport(r9)
            android.widget.ScrollView r0 = r7.scrollView
            java.lang.String r1 = "actionBarDefault"
            int r1 = r7.getThemedColor(r1)
            org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor((android.widget.ScrollView) r0, (int) r1)
            android.widget.ScrollView r0 = r7.scrollView
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 51
            r18 = 0
            r19 = 0
            r20 = 0
            int r1 = r7.currentStep
            if (r1 != r12) goto L_0x0171
            r1 = 1111490560(0x42400000, float:48.0)
            r21 = 1111490560(0x42400000, float:48.0)
            goto L_0x0174
        L_0x0171:
            r1 = 0
            r21 = 0
        L_0x0174:
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r14.addView(r0, r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.linearLayout2 = r0
            r0.setOrientation(r9)
            android.widget.LinearLayout r0 = r7.linearLayout2
            r15 = 0
            r0.setClipChildren(r15)
            android.widget.ScrollView r0 = r7.scrollView
            android.widget.LinearLayout r1 = r7.linearLayout2
            android.widget.FrameLayout$LayoutParams r2 = new android.widget.FrameLayout$LayoutParams
            r6 = -2
            r2.<init>(r13, r6)
            r0.addView(r1, r2)
            int r0 = r7.currentStep
            java.lang.String r12 = "windowBackgroundWhiteBlackText"
            java.lang.String r1 = "windowBackgroundGrayShadow"
            java.lang.String r3 = "windowBackgroundWhite"
            r4 = 2
            if (r0 != 0) goto L_0x0a3c
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r16 = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r26 = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x022a }
            java.io.InputStreamReader r5 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x022a }
            android.content.res.Resources r27 = r37.getResources()     // Catch:{ Exception -> 0x022a }
            android.content.res.AssetManager r11 = r27.getAssets()     // Catch:{ Exception -> 0x022a }
            java.lang.String r6 = "countries.txt"
            java.io.InputStream r6 = r11.open(r6)     // Catch:{ Exception -> 0x022a }
            r5.<init>(r6)     // Catch:{ Exception -> 0x022a }
            r0.<init>(r5)     // Catch:{ Exception -> 0x022a }
        L_0x01c9:
            java.lang.String r5 = r0.readLine()     // Catch:{ Exception -> 0x022a }
            r6 = r5
            if (r5 == 0) goto L_0x0220
            java.lang.String r5 = ";"
            java.lang.String[] r5 = r6.split(r5)     // Catch:{ Exception -> 0x022a }
            java.util.ArrayList<java.lang.String> r11 = r7.countriesArray     // Catch:{ Exception -> 0x022a }
            r13 = r5[r4]     // Catch:{ Exception -> 0x022a }
            r11.add(r15, r13)     // Catch:{ Exception -> 0x022a }
            java.util.HashMap<java.lang.String, java.lang.String> r11 = r7.countriesMap     // Catch:{ Exception -> 0x022a }
            r13 = r5[r4]     // Catch:{ Exception -> 0x022a }
            r2 = r5[r15]     // Catch:{ Exception -> 0x022a }
            r11.put(r13, r2)     // Catch:{ Exception -> 0x022a }
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r7.codesMap     // Catch:{ Exception -> 0x022a }
            r11 = r5[r15]     // Catch:{ Exception -> 0x022a }
            r13 = r5[r4]     // Catch:{ Exception -> 0x022a }
            r2.put(r11, r13)     // Catch:{ Exception -> 0x022a }
            r2 = r5[r9]     // Catch:{ Exception -> 0x022a }
            r11 = r5[r4]     // Catch:{ Exception -> 0x022a }
            r13 = r26
            r13.put(r2, r11)     // Catch:{ Exception -> 0x021c }
            int r2 = r5.length     // Catch:{ Exception -> 0x021c }
            r11 = 3
            if (r2 <= r11) goto L_0x020a
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r7.phoneFormatMap     // Catch:{ Exception -> 0x0206 }
            r4 = r5[r15]     // Catch:{ Exception -> 0x0206 }
            r15 = r5[r11]     // Catch:{ Exception -> 0x0206 }
            r2.put(r4, r15)     // Catch:{ Exception -> 0x0206 }
            goto L_0x020a
        L_0x0206:
            r0 = move-exception
            r4 = r16
            goto L_0x022f
        L_0x020a:
            r2 = r5[r9]     // Catch:{ Exception -> 0x021c }
            r4 = 2
            r11 = r5[r4]     // Catch:{ Exception -> 0x021c }
            r4 = r16
            r4.put(r2, r11)     // Catch:{ Exception -> 0x0228 }
            r16 = r4
            r26 = r13
            r4 = 2
            r13 = -1
            r15 = 0
            goto L_0x01c9
        L_0x021c:
            r0 = move-exception
            r4 = r16
            goto L_0x022f
        L_0x0220:
            r4 = r16
            r13 = r26
            r0.close()     // Catch:{ Exception -> 0x0228 }
            goto L_0x0232
        L_0x0228:
            r0 = move-exception
            goto L_0x022f
        L_0x022a:
            r0 = move-exception
            r4 = r16
            r13 = r26
        L_0x022f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0232:
            java.util.ArrayList<java.lang.String> r0 = r7.countriesArray
            org.telegram.ui.CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0 r2 = org.telegram.ui.CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE
            java.util.Collections.sort(r0, r2)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r7.inputFields = r0
            r0 = 0
        L_0x0240:
            r2 = 10
            if (r0 >= r2) goto L_0x083e
            if (r0 != 0) goto L_0x027d
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r6 = 0
            r2[r6] = r5
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            r5 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r11 = "PaymentShippingAddress"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r2.setText(r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r5 = r7.headerCell
            r5 = r5[r6]
            r11 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r11)
            r2.addView(r5, r9)
            goto L_0x02d1
        L_0x027d:
            r6 = 0
            r2 = 6
            if (r0 != r2) goto L_0x02d1
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r2[r6] = r5
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r5, r11)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r6 = 1
            r2[r6] = r5
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            r5 = 2131627418(0x7f0e0d9a, float:1.88821E38)
            java.lang.String r9 = "PaymentShippingReceiver"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r2.setText(r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r5 = r7.headerCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r5, r11)
        L_0x02d1:
            r2 = 8
            if (r0 != r2) goto L_0x02f9
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r8)
            r5 = 0
            r2.setClipChildren(r5)
            r6 = r2
            android.widget.LinearLayout r6 = (android.widget.LinearLayout) r6
            r6.setOrientation(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r6 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r5.addView(r2, r11)
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            goto L_0x0373
        L_0x02f9:
            r2 = 9
            if (r0 != r2) goto L_0x030a
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r5 = 8
            r2 = r2[r5]
            android.view.ViewParent r2 = r2.getParent()
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            goto L_0x0373
        L_0x030a:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r8)
            r5 = 0
            r2.setClipChildren(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r6 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r5.addView(r2, r11)
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            r5 = 5
            if (r0 == r5) goto L_0x032b
            r5 = 1
            goto L_0x032c
        L_0x032b:
            r5 = 0
        L_0x032c:
            if (r5 == 0) goto L_0x034f
            r6 = 7
            if (r0 != r6) goto L_0x033b
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x033b
            r5 = 0
            goto L_0x034f
        L_0x033b:
            r6 = 6
            if (r0 != r6) goto L_0x034f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x034f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.email_requested
            if (r6 != 0) goto L_0x034f
            r5 = 0
        L_0x034f:
            if (r5 == 0) goto L_0x0371
            org.telegram.ui.PaymentFormActivity$2 r6 = new org.telegram.ui.PaymentFormActivity$2
            r6.<init>(r8)
            int r9 = r7.getThemedColor(r3)
            r6.setBackgroundColor(r9)
            java.util.ArrayList<android.view.View> r9 = r7.dividers
            r9.add(r6)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r11 = 83
            r16 = r5
            r5 = 1
            r15 = -1
            r9.<init>(r15, r5, r11)
            r2.addView(r6, r9)
            goto L_0x0373
        L_0x0371:
            r16 = r5
        L_0x0373:
            r5 = 9
            if (r0 != r5) goto L_0x0381
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            org.telegram.ui.Components.HintEditText r6 = new org.telegram.ui.Components.HintEditText
            r6.<init>(r8)
            r5[r0] = r6
            goto L_0x038a
        L_0x0381:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r5[r0] = r6
        L_0x038a:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r5.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r9 = 1
            r5.setTextSize(r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = r7.getThemedColor(r6)
            r5.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            int r6 = r7.getThemedColor(r12)
            r5.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 0
            r5.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            int r6 = r7.getThemedColor(r12)
            r5.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r5.setCursorWidth(r6)
            r5 = 4
            if (r0 != r5) goto L_0x03f7
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14
            r6.<init>(r7)
            r5.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 0
            r5.setInputType(r6)
        L_0x03f7:
            r5 = 9
            if (r0 == r5) goto L_0x0416
            r5 = 8
            if (r0 != r5) goto L_0x0400
            goto L_0x0416
        L_0x0400:
            r5 = 7
            if (r0 != r5) goto L_0x040c
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1
            r5.setInputType(r6)
            goto L_0x041e
        L_0x040c:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 16385(0x4001, float:2.296E-41)
            r5.setInputType(r6)
            goto L_0x041e
        L_0x0416:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 3
            r5.setInputType(r6)
        L_0x041e:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r5.setImeOptions(r6)
            switch(r0) {
                case 0: goto L_0x0583;
                case 1: goto L_0x0555;
                case 2: goto L_0x0527;
                case 3: goto L_0x04f8;
                case 4: goto L_0x04b6;
                case 5: goto L_0x0487;
                case 6: goto L_0x045a;
                case 7: goto L_0x042d;
                default: goto L_0x042b;
            }
        L_0x042b:
            goto L_0x05b0
        L_0x042d:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627413(0x7f0e0d95, float:1.888209E38)
            java.lang.String r9 = "PaymentShippingEmailPlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            java.lang.String r5 = r5.email
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            java.lang.String r6 = r6.email
            r5.setText(r6)
            goto L_0x05b0
        L_0x045a:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627416(0x7f0e0d98, float:1.8882096E38)
            java.lang.String r9 = "PaymentShippingName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            java.lang.String r5 = r5.name
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            java.lang.String r6 = r6.name
            r5.setText(r6)
            goto L_0x05b0
        L_0x0487:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627422(0x7f0e0d9e, float:1.8882108E38)
            java.lang.String r9 = "PaymentShippingZipPlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            java.lang.String r6 = r6.post_code
            r5.setText(r6)
            goto L_0x05b0
        L_0x04b6:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627412(0x7f0e0d94, float:1.8882088E38)
            java.lang.String r9 = "PaymentShippingCountry"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            java.lang.String r5 = r5.country_iso2
            java.lang.Object r5 = r13.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            java.lang.String r6 = r6.country_iso2
            r7.countryName = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            if (r5 == 0) goto L_0x04f3
            r6 = r5
        L_0x04f3:
            r9.setText(r6)
            goto L_0x05b0
        L_0x04f8:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627421(0x7f0e0d9d, float:1.8882106E38)
            java.lang.String r9 = "PaymentShippingStatePlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            java.lang.String r6 = r6.state
            r5.setText(r6)
            goto L_0x05b0
        L_0x0527:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627411(0x7f0e0d93, float:1.8882086E38)
            java.lang.String r9 = "PaymentShippingCityPlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            java.lang.String r6 = r6.city
            r5.setText(r6)
            goto L_0x05b0
        L_0x0555:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627410(0x7f0e0d92, float:1.8882084E38)
            java.lang.String r9 = "PaymentShippingAddress2Placeholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            java.lang.String r6 = r6.street_line2
            r5.setText(r6)
            goto L_0x05b0
        L_0x0583:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627409(0x7f0e0d91, float:1.8882082E38)
            java.lang.String r9 = "PaymentShippingAddress1Placeholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x05b0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x05b0
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r6 = r6.shipping_address
            java.lang.String r6 = r6.street_line1
            r5.setText(r6)
        L_0x05b0:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r6 = r5[r0]
            r5 = r5[r0]
            int r5 = r5.length()
            r6.setSelection(r5)
            r5 = 8
            if (r0 != r5) goto L_0x063a
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r8)
            r7.textView = r5
            java.lang.String r6 = "+"
            r5.setText(r6)
            android.widget.TextView r5 = r7.textView
            int r6 = r7.getThemedColor(r12)
            r5.setTextColor(r6)
            android.widget.TextView r5 = r7.textView
            r6 = 1098907648(0x41800000, float:16.0)
            r9 = 1
            r5.setTextSize(r9, r6)
            android.widget.TextView r5 = r7.textView
            r28 = -2
            r29 = -2
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 0
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r28, r29, r30, r31, r32, r33)
            r2.addView(r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r5.setPadding(r6, r9, r9, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 19
            r5.setGravity(r6)
            r5 = 1
            android.text.InputFilter[] r6 = new android.text.InputFilter[r5]
            android.text.InputFilter$LengthFilter r5 = new android.text.InputFilter$LengthFilter
            r11 = 5
            r5.<init>(r11)
            r6[r9] = r5
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r5.setFilters(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r28 = 55
            r30 = 0
            r32 = 1101529088(0x41a80000, float:21.0)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r28, r29, r30, r31, r32, r33)
            r2.addView(r5, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$3 r9 = new org.telegram.ui.PaymentFormActivity$3
            r9.<init>()
            r5.addTextChangedListener(r9)
            goto L_0x06a8
        L_0x063a:
            r5 = 9
            if (r0 != r5) goto L_0x0673
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 0
            r5.setPadding(r6, r6, r6, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 19
            r5.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r28 = -1
            r29 = -2
            r30 = 0
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r28, r29, r30, r31, r32, r33)
            r2.addView(r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$4 r6 = new org.telegram.ui.PaymentFormActivity$4
            r6.<init>()
            r5.addTextChangedListener(r6)
            goto L_0x06a8
        L_0x0673:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r5.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x068b
            r6 = 5
            goto L_0x068c
        L_0x068b:
            r6 = 3
        L_0x068c:
            r5.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r28 = -1
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r30 = 51
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1094713344(0x41400000, float:12.0)
            r33 = 1101529088(0x41a80000, float:21.0)
            r34 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r5, r6)
        L_0x06a8:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18
            r6.<init>(r7)
            r5.setOnEditorActionListener(r6)
            r5 = 9
            if (r0 != r5) goto L_0x082d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            boolean r5 = r5.email_to_provider
            if (r5 != 0) goto L_0x06ed
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            boolean r5 = r5.phone_to_provider
            if (r5 == 0) goto L_0x06c9
            goto L_0x06ed
        L_0x06c9:
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r6.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r9 = 1
            r5[r9] = r6
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r9]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r5.addView(r6, r15)
            r16 = r2
            r15 = r10
            r29 = r13
            r28 = r14
            goto L_0x07ba
        L_0x06ed:
            r5 = 0
            r6 = 0
        L_0x06ef:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r9.users
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x0720
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r9 = r9.users
            java.lang.Object r9 = r9.get(r6)
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC.User) r9
            r15 = r10
            long r10 = r9.id
            r16 = r2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            r29 = r13
            r28 = r14
            long r13 = r2.provider_id
            int r2 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x0716
            r2 = r9
            r5 = r2
        L_0x0716:
            int r6 = r6 + 1
            r10 = r15
            r2 = r16
            r14 = r28
            r13 = r29
            goto L_0x06ef
        L_0x0720:
            r16 = r2
            r15 = r10
            r29 = r13
            r28 = r14
            if (r5 == 0) goto L_0x0732
            java.lang.String r2 = r5.first_name
            java.lang.String r6 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r6)
            goto L_0x0734
        L_0x0732:
            java.lang.String r2 = ""
        L_0x0734:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r10 = 1
            r6[r10] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r10]
            r9 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r1)
            r6.setBackgroundDrawable(r11)
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r10]
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r6.addView(r9, r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.email_to_provider
            if (r6 == 0) goto L_0x0784
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_to_provider
            if (r6 == 0) goto L_0x0784
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r9 = 1
            r6 = r6[r9]
            r10 = 2131627404(0x7f0e0d8c, float:1.8882071E38)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r9 = 0
            r11[r9] = r2
            java.lang.String r9 = "PaymentPhoneEmailToProvider"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r9, r10, r11)
            r6.setText(r9)
            goto L_0x07b9
        L_0x0784:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.email_to_provider
            if (r6 == 0) goto L_0x07a3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r9 = 1
            r6 = r6[r9]
            r10 = 2131627390(0x7f0e0d7e, float:1.8882043E38)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r13 = 0
            r11[r13] = r2
            java.lang.String r14 = "PaymentEmailToProvider"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r14, r10, r11)
            r6.setText(r10)
            goto L_0x07b9
        L_0x07a3:
            r9 = 1
            r13 = 0
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r9]
            r10 = 2131627405(0x7f0e0d8d, float:1.8882074E38)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r11[r13] = r2
            java.lang.String r9 = "PaymentPhoneToProvider"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r9, r10, r11)
            r6.setText(r9)
        L_0x07b9:
        L_0x07ba:
            org.telegram.ui.Cells.TextCheckCell r2 = new org.telegram.ui.Cells.TextCheckCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r2.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r7.checkCell1 = r2
            r5 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r2.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextCheckCell r2 = r7.checkCell1
            r5 = 2131627419(0x7f0e0d9b, float:1.8882102E38)
            java.lang.String r6 = "PaymentShippingSave"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            boolean r6 = r7.saveShippingInfo
            r9 = 0
            r2.setTextAndCheck(r5, r6, r9)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r5 = r7.checkCell1
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r5, r10)
            org.telegram.ui.Cells.TextCheckCell r2 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6
            r5.<init>(r7)
            r2.setOnClickListener(r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r6 = 0
            r2[r6] = r5
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r6]
            r5 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r5, (java.lang.String) r1)
            r2.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r6]
            r5 = 2131627420(0x7f0e0d9c, float:1.8882104E38)
            java.lang.String r9 = "PaymentShippingSaveInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r2.setText(r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r5, r10)
            goto L_0x0834
        L_0x082d:
            r16 = r2
            r15 = r10
            r29 = r13
            r28 = r14
        L_0x0834:
            int r0 = r0 + 1
            r10 = r15
            r14 = r28
            r13 = r29
            r9 = 1
            goto L_0x0240
        L_0x083e:
            r15 = r10
            r29 = r13
            r28 = r14
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x085c
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 6
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
            goto L_0x085e
        L_0x085c:
            r1 = 8
        L_0x085e:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x0873
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x0873:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x088b
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 7
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
        L_0x088b:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 == 0) goto L_0x08a0
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r5 = 9
            r0 = r0[r5]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x08cd
        L_0x08a0:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 == 0) goto L_0x08b4
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x08cd
        L_0x08b4:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x08c5
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 6
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x08cd
        L_0x08c5:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 5
            r0 = r0[r2]
            r0.setImeOptions(r1)
        L_0x08cd:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = 1
            r2 = r0[r1]
            if (r2 == 0) goto L_0x08f7
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.name_requested
            if (r1 != 0) goto L_0x08f2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.phone_requested
            if (r1 != 0) goto L_0x08f2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x08ef
            goto L_0x08f2
        L_0x08ef:
            r1 = 8
            goto L_0x08f3
        L_0x08f2:
            r1 = 0
        L_0x08f3:
            r0.setVisibility(r1)
            goto L_0x0920
        L_0x08f7:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r1 = 1
            r2 = r0[r1]
            if (r2 == 0) goto L_0x0920
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.name_requested
            if (r1 != 0) goto L_0x091c
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.phone_requested
            if (r1 != 0) goto L_0x091c
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0919
            goto L_0x091c
        L_0x0919:
            r1 = 8
            goto L_0x091d
        L_0x091c:
            r1 = 0
        L_0x091d:
            r0.setVisibility(r1)
        L_0x0920:
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 1
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.name_requested
            if (r1 != 0) goto L_0x0941
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.phone_requested
            if (r1 != 0) goto L_0x0941
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x093e
            goto L_0x0941
        L_0x093e:
            r1 = 8
            goto L_0x0942
        L_0x0941:
            r1 = 0
        L_0x0942:
            r0.setVisibility(r1)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x09b1
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
        L_0x09b1:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x09cd
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09cd
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r7.fillNumber(r0)
            goto L_0x09d1
        L_0x09cd:
            r1 = 0
            r7.fillNumber(r1)
        L_0x09d1:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 8
            r0 = r0[r1]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x0a36
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 == 0) goto L_0x0a36
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x09f7
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0a36
        L_0x09f7:
            r1 = 0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a0e }
            java.lang.String r2 = "phone"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x0a0e }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0a0e }
            if (r0 == 0) goto L_0x0a0d
            java.lang.String r2 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0a0e }
            java.lang.String r2 = r2.toUpperCase()     // Catch:{ Exception -> 0x0a0e }
            r1 = r2
        L_0x0a0d:
            goto L_0x0a12
        L_0x0a0e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a12:
            if (r1 == 0) goto L_0x0a36
            java.lang.Object r0 = r4.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x0a36
            java.util.ArrayList<java.lang.String> r2 = r7.countriesArray
            int r2 = r2.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x0a36
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r5 = 8
            r3 = r3[r5]
            java.util.HashMap<java.lang.String, java.lang.String> r5 = r7.countriesMap
            java.lang.Object r5 = r5.get(r0)
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            r3.setText(r5)
        L_0x0a36:
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x0a3c:
            r15 = r10
            r28 = r14
            r5 = 9
            r2 = 2
            if (r0 != r2) goto L_0x0fb7
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0a78
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0a74 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm     // Catch:{ Exception -> 0x0a74 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r2.native_params     // Catch:{ Exception -> 0x0a74 }
            java.lang.String r2 = r2.data     // Catch:{ Exception -> 0x0a74 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0a74 }
            java.lang.String r2 = "google_pay_public_key"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ Exception -> 0x0a74 }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0a74 }
            if (r4 != 0) goto L_0x0a63
            r7.googlePayPublicKey = r2     // Catch:{ Exception -> 0x0a74 }
        L_0x0a63:
            java.lang.String r4 = "acquirer_bank_country"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ Exception -> 0x0a74 }
            r7.googlePayCountryCode = r4     // Catch:{ Exception -> 0x0a74 }
            java.lang.String r4 = "gpay_parameters"
            org.json.JSONObject r4 = r0.optJSONObject(r4)     // Catch:{ Exception -> 0x0a74 }
            r7.googlePayParameters = r4     // Catch:{ Exception -> 0x0a74 }
            goto L_0x0a78
        L_0x0a74:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a78:
            boolean r0 = r7.isWebView
            if (r0 == 0) goto L_0x0b96
            java.lang.String r0 = r7.googlePayPublicKey
            if (r0 != 0) goto L_0x0a84
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0a87
        L_0x0a84:
            r36.initGooglePay(r37)
        L_0x0a87:
            r36.createGooglePayButton(r37)
            android.widget.LinearLayout r0 = r7.linearLayout2
            android.widget.FrameLayout r2 = r7.googlePayContainer
            r3 = 50
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
            r2 = 1
            r7.webviewLoading = r2
            r7.showEditDoneProgress(r2, r2)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r2 = 0
            r0.setVisibility(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r0.setEnabled(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            android.view.View r0 = r0.getContentView()
            r2 = 4
            r0.setVisibility(r2)
            org.telegram.ui.PaymentFormActivity$5 r0 = new org.telegram.ui.PaymentFormActivity$5
            r0.<init>(r8)
            r7.webView = r0
            android.webkit.WebSettings r0 = r0.getSettings()
            r2 = 1
            r0.setJavaScriptEnabled(r2)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 < r2) goto L_0x0ae5
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r2 = 0
            r0.setMixedContentMode(r2)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r2 = r7.webView
            r3 = 1
            r0.setAcceptThirdPartyCookies(r2, r3)
        L_0x0ae5:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 17
            if (r0 < r2) goto L_0x0af8
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r2 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r3 = 0
            r2.<init>()
            java.lang.String r3 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r2, r3)
        L_0x0af8:
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$6 r2 = new org.telegram.ui.PaymentFormActivity$6
            r2.<init>()
            r0.setWebViewClient(r2)
            android.widget.LinearLayout r0 = r7.linearLayout2
            android.webkit.WebView r2 = r7.webView
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r0.addView(r2, r3)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r7.resourcesProvider
            r2.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r3 = 2
            r0[r3] = r2
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            r2 = r2[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r5)
            org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r7.resourcesProvider
            r0.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r7.checkCell1 = r0
            r2 = 1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextCheckCell r0 = r7.checkCell1
            r2 = 2131627373(0x7f0e0d6d, float:1.8882009E38)
            java.lang.String r3 = "PaymentCardSavePaymentInformation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            boolean r3 = r7.saveCardInfo
            r4 = 0
            r0.setTextAndCheck(r2, r3, r4)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r2 = r7.checkCell1
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r5)
            org.telegram.ui.Cells.TextCheckCell r0 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r2 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r7.resourcesProvider
            r2.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r3 = 0
            r0[r3] = r2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r3]
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r1)
            r0.setBackgroundDrawable(r1)
            r36.updateSavePaymentField()
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x0b96:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0CLASSNAME
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0bfd }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm     // Catch:{ Exception -> 0x0bfd }
            org.telegram.tgnet.TLRPC$TL_dataJSON r2 = r2.native_params     // Catch:{ Exception -> 0x0bfd }
            java.lang.String r2 = r2.data     // Catch:{ Exception -> 0x0bfd }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0bfd }
            r2 = r0
            java.lang.String r0 = "need_country"
            boolean r0 = r2.getBoolean(r0)     // Catch:{ Exception -> 0x0bb1 }
            r7.need_card_country = r0     // Catch:{ Exception -> 0x0bb1 }
            goto L_0x0bb5
        L_0x0bb1:
            r0 = move-exception
            r4 = 0
            r7.need_card_country = r4     // Catch:{ Exception -> 0x0bfd }
        L_0x0bb5:
            java.lang.String r0 = "need_zip"
            boolean r0 = r2.getBoolean(r0)     // Catch:{ Exception -> 0x0bbe }
            r7.need_card_postcode = r0     // Catch:{ Exception -> 0x0bbe }
            goto L_0x0bc2
        L_0x0bbe:
            r0 = move-exception
            r4 = 0
            r7.need_card_postcode = r4     // Catch:{ Exception -> 0x0bfd }
        L_0x0bc2:
            java.lang.String r0 = "need_cardholder_name"
            boolean r0 = r2.getBoolean(r0)     // Catch:{ Exception -> 0x0bcb }
            r7.need_card_name = r0     // Catch:{ Exception -> 0x0bcb }
            goto L_0x0bcf
        L_0x0bcb:
            r0 = move-exception
            r4 = 0
            r7.need_card_name = r4     // Catch:{ Exception -> 0x0bfd }
        L_0x0bcf:
            java.lang.String r0 = "public_token"
            boolean r0 = r2.has(r0)     // Catch:{ Exception -> 0x0bfd }
            if (r0 == 0) goto L_0x0be0
            java.lang.String r0 = "public_token"
            java.lang.String r0 = r2.getString(r0)     // Catch:{ Exception -> 0x0bfd }
            r7.providerApiKey = r0     // Catch:{ Exception -> 0x0bfd }
            goto L_0x0bee
        L_0x0be0:
            java.lang.String r0 = "publishable_key"
            java.lang.String r0 = r2.getString(r0)     // Catch:{ Exception -> 0x0be9 }
            r7.providerApiKey = r0     // Catch:{ Exception -> 0x0be9 }
            goto L_0x0bee
        L_0x0be9:
            r0 = move-exception
            java.lang.String r4 = ""
            r7.providerApiKey = r4     // Catch:{ Exception -> 0x0bfd }
        L_0x0bee:
            java.lang.String r0 = "google_pay_hidden"
            r4 = 0
            boolean r0 = r2.optBoolean(r0, r4)     // Catch:{ Exception -> 0x0bfd }
            if (r0 != 0) goto L_0x0bf9
            r0 = 1
            goto L_0x0bfa
        L_0x0bf9:
            r0 = 0
        L_0x0bfa:
            r7.initGooglePay = r0     // Catch:{ Exception -> 0x0bfd }
            goto L_0x0CLASSNAME
        L_0x0bfd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0CLASSNAME:
            boolean r0 = r7.initGooglePay
            if (r0 == 0) goto L_0x0CLASSNAME
            java.lang.String r0 = r7.providerApiKey
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.lang.String r0 = r0.native_provider
            java.lang.String r2 = "stripe"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0c1d
        L_0x0CLASSNAME:
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0CLASSNAME
        L_0x0c1d:
            r36.initGooglePay(r37)
        L_0x0CLASSNAME:
            r2 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r2]
            r7.inputFields = r0
            r0 = 0
        L_0x0CLASSNAME:
            if (r0 >= r2) goto L_0x0var_
            if (r0 != 0) goto L_0x0CLASSNAME
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r2[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r5]
            int r4 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r5]
            r4 = 2131627376(0x7f0e0d70, float:1.8882015E38)
            java.lang.String r6 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.setText(r4)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r2.addView(r4, r9)
            goto L_0x0c9a
        L_0x0CLASSNAME:
            r2 = 4
            if (r0 != r2) goto L_0x0c9a
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 1
            r2[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r5]
            int r4 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r5]
            r4 = 2131627367(0x7f0e0d67, float:1.8881996E38)
            java.lang.String r6 = "PaymentBillingAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.setText(r4)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r2.addView(r4, r9)
        L_0x0c9a:
            r2 = 3
            if (r0 == r2) goto L_0x0ca9
            r2 = 5
            if (r0 == r2) goto L_0x0ca9
            r2 = 4
            if (r0 != r2) goto L_0x0ca7
            boolean r2 = r7.need_card_postcode
            if (r2 == 0) goto L_0x0ca9
        L_0x0ca7:
            r2 = 1
            goto L_0x0caa
        L_0x0ca9:
            r2 = 0
        L_0x0caa:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r8)
            r5 = 0
            r4.setClipChildren(r5)
            int r5 = r7.getThemedColor(r3)
            r4.setBackgroundColor(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r6 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r5.addView(r4, r10)
            r5 = 0
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r8)
            r6[r0] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r6.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1098907648(0x41800000, float:16.0)
            r10 = 1
            r6.setTextSize(r10, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = r7.getThemedColor(r9)
            r6.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            int r9 = r7.getThemedColor(r12)
            r6.setTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 0
            r6.setBackgroundDrawable(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            int r9 = r7.getThemedColor(r12)
            r6.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1101004800(0x41a00000, float:20.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.setCursorSize(r10)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r6.setCursorWidth(r9)
            r6 = 3
            if (r0 != r6) goto L_0x0d59
            r9 = 1
            android.text.InputFilter[] r10 = new android.text.InputFilter[r9]
            android.text.InputFilter$LengthFilter r9 = new android.text.InputFilter$LengthFilter
            r9.<init>(r6)
            r6 = 0
            r10[r6] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r6.setFilters(r10)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 130(0x82, float:1.82E-43)
            r6.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
            r6.setTypeface(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            android.text.method.PasswordTransformationMethod r9 = android.text.method.PasswordTransformationMethod.getInstance()
            r6.setTransformationMethod(r9)
            goto L_0x0d9f
        L_0x0d59:
            if (r0 != 0) goto L_0x0d64
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 3
            r6.setInputType(r9)
            goto L_0x0d9f
        L_0x0d64:
            r6 = 4
            if (r0 != r6) goto L_0x0d7c
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15
            r9.<init>(r7)
            r6.setOnTouchListener(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 0
            r6.setInputType(r9)
            goto L_0x0d9f
        L_0x0d7c:
            r6 = 1
            if (r0 != r6) goto L_0x0d89
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 16386(0x4002, float:2.2962E-41)
            r6.setInputType(r9)
            goto L_0x0d9f
        L_0x0d89:
            r6 = 2
            if (r0 != r6) goto L_0x0d96
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 4097(0x1001, float:5.741E-42)
            r6.setInputType(r9)
            goto L_0x0d9f
        L_0x0d96:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 16385(0x4001, float:2.296E-41)
            r6.setInputType(r9)
        L_0x0d9f:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 268435461(0x10000005, float:2.5243564E-29)
            r6.setImeOptions(r9)
            switch(r0) {
                case 0: goto L_0x0e02;
                case 1: goto L_0x0df1;
                case 2: goto L_0x0de0;
                case 3: goto L_0x0dcf;
                case 4: goto L_0x0dbe;
                case 5: goto L_0x0dad;
                default: goto L_0x0dac;
            }
        L_0x0dac:
            goto L_0x0e13
        L_0x0dad:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131627422(0x7f0e0d9e, float:1.8882108E38)
            java.lang.String r10 = "PaymentShippingZipPlaceholder"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
            goto L_0x0e13
        L_0x0dbe:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131627412(0x7f0e0d94, float:1.8882088E38)
            java.lang.String r10 = "PaymentShippingCountry"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
            goto L_0x0e13
        L_0x0dcf:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131627368(0x7f0e0d68, float:1.8881998E38)
            java.lang.String r10 = "PaymentCardCvv"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
            goto L_0x0e13
        L_0x0de0:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131627371(0x7f0e0d6b, float:1.8882005E38)
            java.lang.String r10 = "PaymentCardName"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
            goto L_0x0e13
        L_0x0df1:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131627369(0x7f0e0d69, float:1.8882E38)
            java.lang.String r10 = "PaymentCardExpireDate"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
            goto L_0x0e13
        L_0x0e02:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131627372(0x7f0e0d6c, float:1.8882007E38)
            java.lang.String r10 = "PaymentCardNumber"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
        L_0x0e13:
            if (r0 != 0) goto L_0x0e22
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$7 r9 = new org.telegram.ui.PaymentFormActivity$7
            r9.<init>()
            r6.addTextChangedListener(r9)
            goto L_0x0e31
        L_0x0e22:
            r6 = 1
            if (r0 != r6) goto L_0x0e31
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$8 r9 = new org.telegram.ui.PaymentFormActivity$8
            r9.<init>()
            r6.addTextChangedListener(r9)
        L_0x0e31:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 0
            r6.setPadding(r10, r10, r10, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0e49
            r9 = 5
            goto L_0x0e4a
        L_0x0e49:
            r9 = 3
        L_0x0e4a:
            r6.setGravity(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1094713344(0x41400000, float:12.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r4.addView(r6, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda22 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda22
            r9.<init>(r7)
            r6.setOnEditorActionListener(r9)
            r6 = 3
            if (r0 != r6) goto L_0x0e92
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r10 = 0
            r6[r10] = r9
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r9 = r7.sectionCell
            r9 = r9[r10]
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r6.addView(r9, r13)
            goto L_0x0var_
        L_0x0e92:
            r6 = 5
            if (r0 != r6) goto L_0x0var_
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r10 = 2
            r6[r10] = r9
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r9 = r7.sectionCell
            r9 = r9[r10]
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r6.addView(r9, r13)
            org.telegram.ui.Cells.TextCheckCell r6 = new org.telegram.ui.Cells.TextCheckCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r6.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r7.checkCell1 = r6
            r9 = 1
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9)
            r6.setBackgroundDrawable(r10)
            org.telegram.ui.Cells.TextCheckCell r6 = r7.checkCell1
            r9 = 2131627373(0x7f0e0d6d, float:1.8882009E38)
            java.lang.String r10 = "PaymentCardSavePaymentInformation"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            boolean r10 = r7.saveCardInfo
            r11 = 0
            r6.setTextAndCheck(r9, r10, r11)
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r9 = r7.checkCell1
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r6.addView(r9, r13)
            org.telegram.ui.Cells.TextCheckCell r6 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9
            r9.<init>(r7)
            r6.setOnClickListener(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r10 = 0
            r6[r10] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r10]
            r9 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r1)
            r6.setBackgroundDrawable(r11)
            r36.updateSavePaymentField()
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r10]
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r6.addView(r9, r13)
            goto L_0x0var_
        L_0x0var_:
            if (r0 != 0) goto L_0x0var_
            r36.createGooglePayButton(r37)
            android.widget.FrameLayout r6 = r7.googlePayContainer
            r29 = -2
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0var_
            r9 = 3
            goto L_0x0var_
        L_0x0var_:
            r9 = 5
        L_0x0var_:
            r31 = r9 | 16
            r32 = 0
            r33 = 0
            r34 = 1082130432(0x40800000, float:4.0)
            r35 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r4.addView(r6, r9)
        L_0x0var_:
            if (r2 == 0) goto L_0x0var_
            org.telegram.ui.PaymentFormActivity$9 r6 = new org.telegram.ui.PaymentFormActivity$9
            r6.<init>(r8)
            int r9 = r7.getThemedColor(r3)
            r6.setBackgroundColor(r9)
            java.util.ArrayList<android.view.View> r9 = r7.dividers
            r9.add(r6)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r10 = 83
            r11 = -1
            r13 = 1
            r9.<init>(r11, r13, r10)
            r4.addView(r6, r9)
        L_0x0var_:
            r6 = 4
            if (r0 != r6) goto L_0x0f5f
            boolean r6 = r7.need_card_country
            if (r6 == 0) goto L_0x0f6d
        L_0x0f5f:
            r6 = 5
            if (r0 != r6) goto L_0x0var_
            boolean r6 = r7.need_card_postcode
            if (r6 == 0) goto L_0x0f6d
        L_0x0var_:
            r6 = 2
            if (r0 != r6) goto L_0x0var_
            boolean r6 = r7.need_card_name
            if (r6 != 0) goto L_0x0var_
        L_0x0f6d:
            r6 = 8
            r4.setVisibility(r6)
        L_0x0var_:
            int r0 = r0 + 1
            r2 = 6
            goto L_0x0CLASSNAME
        L_0x0var_:
            boolean r0 = r7.need_card_country
            if (r0 != 0) goto L_0x0var_
            boolean r0 = r7.need_card_postcode
            if (r0 != 0) goto L_0x0var_
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 1
            r0 = r0[r1]
            r6 = 8
            r0.setVisibility(r6)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = 0
            r0 = r0[r1]
            r0.setVisibility(r6)
        L_0x0var_:
            boolean r0 = r7.need_card_postcode
            if (r0 == 0) goto L_0x0fa6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 5
            r0 = r0[r1]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x0fa6:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 3
            r0 = r0[r2]
            r0.setImeOptions(r1)
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x0fb7:
            r6 = 8
            r2 = 1
            if (r0 != r2) goto L_0x1065
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r2 = new org.telegram.ui.Cells.RadioCell[r0]
            r7.radioCells = r2
            r2 = 0
        L_0x0fc9:
            if (r2 >= r0) goto L_0x1036
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r3 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r3 = r3.shipping_options
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$TL_shippingOption r3 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r3
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            org.telegram.ui.Cells.RadioCell r5 = new org.telegram.ui.Cells.RadioCell
            r5.<init>(r8)
            r4[r2] = r5
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            r4 = r4[r2]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r2)
            r4.setTag(r5)
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            r4 = r4[r2]
            r5 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            r4 = r4[r2]
            r6 = 2
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r6 = r3.prices
            java.lang.String r6 = r7.getTotalPriceString(r6)
            r10 = 0
            r9[r10] = r6
            java.lang.String r6 = r3.title
            r9[r5] = r6
            java.lang.String r5 = "%s - %s"
            java.lang.String r5 = java.lang.String.format(r5, r9)
            if (r2 != 0) goto L_0x1013
            r6 = 1
            goto L_0x1014
        L_0x1013:
            r6 = 0
        L_0x1014:
            int r9 = r0 + -1
            if (r2 == r9) goto L_0x101a
            r9 = 1
            goto L_0x101b
        L_0x101a:
            r9 = 0
        L_0x101b:
            r4.setText(r5, r6, r9)
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            r4 = r4[r2]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda11 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda11
            r5.<init>(r7)
            r4.setOnClickListener(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r5 = r7.radioCells
            r5 = r5[r2]
            r4.addView(r5)
            int r2 = r2 + 1
            goto L_0x0fc9
        L_0x1036:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r7.resourcesProvider
            r3.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r4 = 0
            r2[r4] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r4]
            r3 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r1)
            r2.setBackgroundDrawable(r1)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r4]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r1.addView(r2, r3)
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x1065:
            r2 = 3
            if (r0 != r2) goto L_0x12c5
            r2 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r2]
            r7.inputFields = r0
            r0 = 0
        L_0x106e:
            if (r0 >= r2) goto L_0x12bf
            if (r0 != 0) goto L_0x10a9
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r2[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r5]
            int r4 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r5]
            r4 = 2131627376(0x7f0e0d70, float:1.8882015E38)
            java.lang.String r6 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.setText(r4)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r4, r10)
            goto L_0x10aa
        L_0x10a9:
            r5 = 0
        L_0x10aa:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r8)
            r2.setClipChildren(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r5 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r4.addView(r2, r9)
            int r4 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r4)
            r4 = 1
            if (r0 == r4) goto L_0x10ca
            r4 = 1
            goto L_0x10cb
        L_0x10ca:
            r4 = 0
        L_0x10cb:
            if (r4 == 0) goto L_0x10ef
            r5 = 7
            if (r0 != r5) goto L_0x10da
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x10da
            r4 = 0
            goto L_0x10f0
        L_0x10da:
            r6 = 6
            if (r0 != r6) goto L_0x10f0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x10f0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.email_requested
            if (r6 != 0) goto L_0x10f0
            r4 = 0
            goto L_0x10f0
        L_0x10ef:
            r5 = 7
        L_0x10f0:
            if (r4 == 0) goto L_0x110f
            org.telegram.ui.PaymentFormActivity$10 r6 = new org.telegram.ui.PaymentFormActivity$10
            r6.<init>(r8)
            int r9 = r7.getThemedColor(r3)
            r6.setBackgroundColor(r9)
            java.util.ArrayList<android.view.View> r9 = r7.dividers
            r9.add(r6)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r10 = 83
            r11 = -1
            r13 = 1
            r9.<init>(r11, r13, r10)
            r2.addView(r6, r9)
        L_0x110f:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r8)
            r6[r0] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r6.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1098907648(0x41800000, float:16.0)
            r10 = 1
            r6.setTextSize(r10, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = r7.getThemedColor(r9)
            r6.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            int r9 = r7.getThemedColor(r12)
            r6.setTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 0
            r6.setBackgroundDrawable(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            int r9 = r7.getThemedColor(r12)
            r6.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1101004800(0x41a00000, float:20.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.setCursorSize(r10)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r6.setCursorWidth(r9)
            if (r0 != 0) goto L_0x1182
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16 r9 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16.INSTANCE
            r6.setOnTouchListener(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 0
            r6.setInputType(r9)
            goto L_0x1194
        L_0x1182:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 129(0x81, float:1.81E-43)
            r6.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
            r6.setTypeface(r9)
        L_0x1194:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 268435462(0x10000006, float:2.5243567E-29)
            r6.setImeOptions(r9)
            switch(r0) {
                case 0: goto L_0x11ba;
                case 1: goto L_0x11a2;
                default: goto L_0x11a1;
            }
        L_0x11a1:
            goto L_0x11c8
        L_0x11a2:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 2131626504(0x7f0e0a08, float:1.8880246E38)
            java.lang.String r10 = "LoginPassword"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setHint(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r6.requestFocus()
            goto L_0x11c8
        L_0x11ba:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r9 = r9.saved_credentials
            java.lang.String r9 = r9.title
            r6.setText(r9)
        L_0x11c8:
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 0
            r6.setPadding(r10, r10, r10, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x11e0
            r9 = 5
            goto L_0x11e1
        L_0x11e0:
            r9 = 3
        L_0x11e1:
            r6.setGravity(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1094713344(0x41400000, float:12.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r2.addView(r6, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r6 = r7.inputFields
            r6 = r6[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17
            r9.<init>(r7)
            r6.setOnEditorActionListener(r9)
            r6 = 1
            if (r0 != r6) goto L_0x12ba
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r10 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r7.resourcesProvider
            r10.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r11 = 0
            r9[r11] = r10
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r11]
            r10 = 2131627387(0x7f0e0d7b, float:1.8882037E38)
            java.lang.Object[] r13 = new java.lang.Object[r6]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r6 = r6.saved_credentials
            java.lang.String r6 = r6.title
            r13[r11] = r6
            java.lang.String r6 = "PaymentConfirmationMessage"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r10, r13)
            r9.setText(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r11]
            r9 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r1)
            r6.setBackgroundDrawable(r9)
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r11]
            r10 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r10)
            r6.addView(r9, r14)
            org.telegram.ui.Cells.TextSettingsCell[] r6 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r9 = new org.telegram.ui.Cells.TextSettingsCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r6[r11] = r9
            org.telegram.ui.Cells.TextSettingsCell[] r6 = r7.settingsCell
            r6 = r6[r11]
            r9 = 1
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9)
            r6.setBackgroundDrawable(r10)
            org.telegram.ui.Cells.TextSettingsCell[] r6 = r7.settingsCell
            r6 = r6[r11]
            r9 = 2131627388(0x7f0e0d7c, float:1.888204E38)
            java.lang.String r10 = "PaymentConfirmationNewCard"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9, r11)
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r9 = r7.settingsCell
            r9 = r9[r11]
            r10 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r10)
            r6.addView(r9, r14)
            org.telegram.ui.Cells.TextSettingsCell[] r6 = r7.settingsCell
            r6 = r6[r11]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda61 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda61
            r9.<init>(r7)
            r6.setOnClickListener(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r10 = 1
            r6[r10] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r10]
            r9 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r1)
            r6.setBackgroundDrawable(r11)
            android.widget.LinearLayout r6 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r10]
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r6.addView(r9, r13)
        L_0x12ba:
            int r0 = r0 + 1
            r2 = 2
            goto L_0x106e
        L_0x12bf:
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x12c5:
            r2 = 4
            if (r0 == r2) goto L_0x1648
            r2 = 5
            if (r0 != r2) goto L_0x12ce
            r9 = 2
            goto L_0x1649
        L_0x12ce:
            r2 = 6
            if (r0 != r2) goto L_0x1642
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r8)
            r7.codeFieldCell = r0
            r2 = 2131627353(0x7f0e0d59, float:1.8881968E38)
            java.lang.String r4 = "PasswordCode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            java.lang.String r4 = ""
            r5 = 0
            r0.setTextAndHint(r4, r2, r5)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            int r2 = r7.getThemedColor(r3)
            r0.setBackgroundColor(r2)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r2 = 3
            r0.setInputType(r2)
            r2 = 6
            r0.setImeOptions(r2)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19
            r2.<init>(r7)
            r0.setOnEditorActionListener(r2)
            org.telegram.ui.PaymentFormActivity$20 r2 = new org.telegram.ui.PaymentFormActivity$20
            r2.<init>()
            r0.addTextChangedListener(r2)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r4 = r7.codeFieldCell
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r2.addView(r4, r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 2
            r2[r5] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r7.bottomCell
            r2 = r2[r5]
            r4 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r4, (java.lang.String) r1)
            r2.setBackgroundDrawable(r4)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r2.addView(r4, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r4 = new org.telegram.ui.Cells.TextSettingsCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 1
            r2[r5] = r4
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r2.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            r2.setTag(r12)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            int r4 = r7.getThemedColor(r12)
            r2.setTextColor(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            r4 = 2131627978(0x7f0e0fca, float:1.8883236E38)
            java.lang.String r6 = "ResendCode"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.setText(r4, r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            r4 = r4[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r4, r10)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda5
            r4.<init>(r7)
            r2.setOnClickListener(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r4 = new org.telegram.ui.Cells.TextSettingsCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r2[r5] = r4
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            r4 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r2.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            java.lang.String r4 = "windowBackgroundWhiteRedText3"
            r2.setTag(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            java.lang.String r4 = "windowBackgroundWhiteRedText3"
            int r4 = r7.getThemedColor(r4)
            r2.setTextColor(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            r4 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r6 = "AbortPassword"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.setText(r4, r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            r4 = r4[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r4, r10)
            org.telegram.ui.Cells.TextSettingsCell[] r2 = r7.settingsCell
            r2 = r2[r5]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7
            r4.<init>(r7)
            r2.setOnClickListener(r4)
            r2 = 3
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = new org.telegram.ui.Components.EditTextBoldCursor[r2]
            r7.inputFields = r4
            r4 = 0
        L_0x13f7:
            if (r4 >= r2) goto L_0x1639
            if (r4 != 0) goto L_0x1432
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r6 = 0
            r2[r6] = r5
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            r5 = 2131627403(0x7f0e0d8b, float:1.888207E38)
            java.lang.String r9 = "PaymentPasswordTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r2.setText(r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r5 = r7.headerCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r5, r10)
            goto L_0x146b
        L_0x1432:
            r2 = 2
            if (r4 != r2) goto L_0x146b
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r6 = 1
            r2[r6] = r5
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            org.telegram.ui.Cells.HeaderCell[] r2 = r7.headerCell
            r2 = r2[r6]
            r5 = 2131627399(0x7f0e0d87, float:1.8882061E38)
            java.lang.String r9 = "PaymentPasswordEmailTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r2.setText(r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r5 = r7.headerCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r2.addView(r5, r10)
        L_0x146b:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r8)
            r5 = 0
            r2.setClipChildren(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r6 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r5.addView(r2, r10)
            int r5 = r7.getThemedColor(r3)
            r2.setBackgroundColor(r5)
            if (r4 != 0) goto L_0x14a6
            org.telegram.ui.PaymentFormActivity$21 r5 = new org.telegram.ui.PaymentFormActivity$21
            r5.<init>(r8)
            int r9 = r7.getThemedColor(r3)
            r5.setBackgroundColor(r9)
            java.util.ArrayList<android.view.View> r9 = r7.dividers
            r9.add(r5)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r10 = 83
            r11 = -1
            r13 = 1
            r9.<init>(r11, r13, r10)
            r2.addView(r5, r9)
        L_0x14a6:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r8)
            r5[r4] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            r5.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 1098907648(0x41800000, float:16.0)
            r10 = 1
            r5.setTextSize(r10, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = r7.getThemedColor(r9)
            r5.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            int r9 = r7.getThemedColor(r12)
            r5.setTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 0
            r5.setBackgroundDrawable(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            int r9 = r7.getThemedColor(r12)
            r5.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 1101004800(0x41a00000, float:20.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r5.setCursorSize(r10)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r5.setCursorWidth(r9)
            if (r4 == 0) goto L_0x151f
            r5 = 1
            if (r4 != r5) goto L_0x150b
            goto L_0x151f
        L_0x150b:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 33
            r5.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 268435462(0x10000006, float:2.5243567E-29)
            r5.setImeOptions(r9)
            goto L_0x153b
        L_0x151f:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 129(0x81, float:1.81E-43)
            r5.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
            r5.setTypeface(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 268435461(0x10000005, float:2.5243564E-29)
            r5.setImeOptions(r9)
        L_0x153b:
            switch(r4) {
                case 0: goto L_0x1561;
                case 1: goto L_0x1550;
                case 2: goto L_0x153f;
                default: goto L_0x153e;
            }
        L_0x153e:
            goto L_0x1579
        L_0x153f:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 2131627397(0x7f0e0d85, float:1.8882057E38)
            java.lang.String r10 = "PaymentPasswordEmail"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setHint(r9)
            goto L_0x1579
        L_0x1550:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 2131627402(0x7f0e0d8a, float:1.8882067E38)
            java.lang.String r10 = "PaymentPasswordReEnter"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setHint(r9)
            goto L_0x1579
        L_0x1561:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 2131627400(0x7f0e0d88, float:1.8882063E38)
            java.lang.String r10 = "PaymentPasswordEnter"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setHint(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r5.requestFocus()
        L_0x1579:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 0
            r5.setPadding(r10, r10, r10, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x1591
            r11 = 5
            goto L_0x1592
        L_0x1591:
            r11 = 3
        L_0x1592:
            r5.setGravity(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1094713344(0x41400000, float:12.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r2.addView(r5, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20
            r9.<init>(r7)
            r5.setOnEditorActionListener(r9)
            r5 = 1
            if (r4 != r5) goto L_0x15f8
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r10)
            r10 = 0
            r5[r10] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r10]
            r9 = 2131627401(0x7f0e0d89, float:1.8882065E38)
            java.lang.String r11 = "PaymentPasswordInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.setText(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r10]
            r9 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r1)
            r5.setBackgroundDrawable(r9)
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r10]
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r5.addView(r9, r13)
            r9 = 2
            goto L_0x1634
        L_0x15f8:
            r9 = 2
            if (r4 != r9) goto L_0x1634
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r10 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r7.resourcesProvider
            r10.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r11 = 1
            r5[r11] = r10
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r11]
            r10 = 2131627398(0x7f0e0d86, float:1.888206E38)
            java.lang.String r13 = "PaymentPasswordEmailInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r5.setText(r10)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r11]
            r10 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r10, (java.lang.String) r1)
            r5.setBackgroundDrawable(r13)
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r10 = r7.bottomCell
            r10 = r10[r11]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r5.addView(r10, r14)
        L_0x1634:
            int r4 = r4 + 1
            r2 = 3
            goto L_0x13f7
        L_0x1639:
            r36.updatePasswordFields()
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x1642:
            r24 = r15
            r5 = r28
            goto L_0x1fd1
        L_0x1648:
            r9 = 2
        L_0x1649:
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r8)
            r7.paymentInfoCell = r0
            int r2 = r7.getThemedColor(r3)
            r0.setBackgroundColor(r2)
            org.telegram.messenger.MessageObject r0 = r7.messageObject
            if (r0 == 0) goto L_0x1669
            org.telegram.ui.Cells.PaymentInfoCell r2 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r0
            java.lang.String r4 = r7.currentBotName
            r2.setInvoice(r0, r4)
            goto L_0x169a
        L_0x1669:
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r0 = r7.paymentReceipt
            if (r0 == 0) goto L_0x1675
            org.telegram.ui.Cells.PaymentInfoCell r2 = r7.paymentInfoCell
            java.lang.String r4 = r7.currentBotName
            r2.setReceipt(r0, r4)
            goto L_0x169a
        L_0x1675:
            java.lang.String r0 = r7.invoiceSlug
            if (r0 == 0) goto L_0x169a
            org.telegram.ui.Cells.PaymentInfoCell r0 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            java.lang.String r2 = r2.title
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            java.lang.String r4 = r4.description
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r10 = r7.paymentForm
            org.telegram.tgnet.TLRPC$WebDocument r10 = r10.photo
            java.lang.String r11 = r7.currentBotName
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r7.paymentForm
            r29 = r0
            r30 = r2
            r31 = r4
            r32 = r10
            r33 = r11
            r34 = r13
            r29.setInfo(r30, r31, r32, r33, r34)
        L_0x169a:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r2 = r7.paymentInfoCell
            r4 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r4)
            r0.addView(r2, r11)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r7.resourcesProvider
            r2.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r4 = 0
            r0[r4] = r2
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            r2 = r2[r4]
            r4 = -1
            r10 = -2
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r10)
            r0.addView(r2, r11)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r2.prices
            r0.<init>(r2)
            r7.prices = r0
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = r7.shippingOption
            if (r2 == 0) goto L_0x16d8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r2.prices
            r0.addAll(r2)
        L_0x16d8:
            r2 = 1
            java.lang.String[] r0 = new java.lang.String[r2]
            r7.totalPrice = r0
            r0 = 0
        L_0x16de:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r7.prices
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x171c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r7.prices
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r2 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r2
            org.telegram.ui.Cells.TextPriceCell r4 = new org.telegram.ui.Cells.TextPriceCell
            r4.<init>(r8)
            int r11 = r7.getThemedColor(r3)
            r4.setBackgroundColor(r11)
            java.lang.String r11 = r2.label
            org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
            long r5 = r2.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r14 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r14 = r14.invoice
            java.lang.String r14 = r14.currency
            java.lang.String r5 = r13.formatCurrencyString(r5, r14)
            r6 = 0
            r4.setTextAndValue(r11, r5, r6)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r5.addView(r4)
            int r0 = r0 + 1
            r5 = 9
            r6 = 8
            goto L_0x16de
        L_0x171c:
            int r0 = r7.currentStep
            r11 = 5
            if (r0 != r11) goto L_0x1757
            java.lang.Long r0 = r7.tipAmount
            if (r0 == 0) goto L_0x1757
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            int r2 = r7.getThemedColor(r3)
            r0.setBackgroundColor(r2)
            r2 = 2131627428(0x7f0e0da4, float:1.888212E38)
            java.lang.String r4 = "PaymentTip"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.Long r5 = r7.tipAmount
            long r5 = r5.longValue()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r13 = r13.invoice
            java.lang.String r13 = r13.currency
            java.lang.String r4 = r4.formatCurrencyString(r5, r13)
            r5 = 0
            r0.setTextAndValue(r2, r4, r5)
            android.widget.LinearLayout r2 = r7.linearLayout2
            r2.addView(r0)
        L_0x1757:
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            r7.totalCell = r0
            int r2 = r7.getThemedColor(r3)
            r0.setBackgroundColor(r2)
            java.lang.String[] r0 = r7.totalPrice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r7.prices
            java.lang.String r2 = r7.getTotalPriceString(r2)
            r4 = 0
            r0[r4] = r2
            org.telegram.ui.Cells.TextPriceCell r0 = r7.totalCell
            r2 = 2131627432(0x7f0e0da8, float:1.8882128E38)
            java.lang.String r5 = "PaymentTransactionTotal"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String[] r5 = r7.totalPrice
            r5 = r5[r4]
            r4 = 1
            r0.setTextAndValue(r2, r5, r4)
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x1a4d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            int r0 = r0.flags
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x1a4d
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r2 = 0
            r0.setClipChildren(r2)
            int r2 = r7.getThemedColor(r3)
            r0.setBackgroundColor(r2)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            java.util.ArrayList<java.lang.Long> r4 = r4.suggested_tip_amounts
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x17b3
            r4 = 40
            goto L_0x17b5
        L_0x17b3:
            r4 = 78
        L_0x17b5:
            r5 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r2.addView(r0, r4)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda62 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda62
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextPriceCell r2 = new org.telegram.ui.Cells.TextPriceCell
            r2.<init>(r8)
            r13 = r2
            int r2 = r7.getThemedColor(r3)
            r13.setBackgroundColor(r2)
            r2 = 2131627429(0x7f0e0da5, float:1.8882122E38)
            java.lang.String r4 = "PaymentTipOptional"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            java.lang.String r4 = ""
            r5 = 0
            r13.setTextAndValue(r2, r4, r5)
            r0.addView(r13)
            r2 = 1
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = new org.telegram.ui.Components.EditTextBoldCursor[r2]
            r7.inputFields = r4
            org.telegram.ui.Components.EditTextBoldCursor r2 = new org.telegram.ui.Components.EditTextBoldCursor
            r2.<init>(r8)
            r4[r5] = r2
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r2.setTag(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r4 = 1098907648(0x41800000, float:16.0)
            r6 = 1
            r2.setTextSize(r6, r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            java.lang.String r4 = "windowBackgroundWhiteGrayText2"
            int r4 = r7.getThemedColor(r4)
            r2.setHintTextColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            java.lang.String r4 = "windowBackgroundWhiteGrayText2"
            int r4 = r7.getThemedColor(r4)
            r2.setTextColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r4 = 0
            r2.setBackgroundDrawable(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            int r4 = r7.getThemedColor(r12)
            r2.setCursorColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r4 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setCursorSize(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r4 = 1069547520(0x3fCLASSNAME, float:1.5)
            r2.setCursorWidth(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r4 = 3
            r2.setInputType(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r2.setImeOptions(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
            r9 = 0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r12 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r12 = r12.invoice
            java.lang.String r12 = r12.currency
            java.lang.String r6 = r6.formatCurrencyString(r9, r12)
            r2.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.setPadding(r5, r5, r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1888
            r6 = 3
            goto L_0x1889
        L_0x1888:
            r6 = 5
        L_0x1889:
            r2.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r31 = 51
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1091567616(0x41100000, float:9.0)
            r34 = 1101529088(0x41a80000, float:21.0)
            r35 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r0.addView(r2, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r5 = 0
            r2 = r2[r5]
            org.telegram.ui.PaymentFormActivity$11 r6 = new org.telegram.ui.PaymentFormActivity$11
            r6.<init>()
            r2.addTextChangedListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda23 r6 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda23.INSTANCE
            r2.setOnEditorActionListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r5]
            r2.requestFocus()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x1a40
            android.widget.HorizontalScrollView r2 = new android.widget.HorizontalScrollView
            r2.<init>(r8)
            r9 = r2
            r2 = 0
            r9.setHorizontalScrollBarEnabled(r2)
            r9.setVerticalScrollBarEnabled(r2)
            r9.setClipToPadding(r2)
            r5 = 1101529088(0x41a80000, float:21.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1101529088(0x41a80000, float:21.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9.setPadding(r5, r2, r6, r2)
            r2 = 1
            r9.setFillViewport(r2)
            r29 = -1
            r30 = 1106247680(0x41var_, float:30.0)
            r31 = 51
            r32 = 0
            r33 = 1110441984(0x42300000, float:44.0)
            r34 = 0
            r35 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r0.addView(r9, r2)
            r2 = 1
            int[] r10 = new int[r2]
            int[] r12 = new int[r2]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            int r14 = r2.size()
            org.telegram.ui.PaymentFormActivity$12 r6 = new org.telegram.ui.PaymentFormActivity$12
            r5 = r1
            r1 = r6
            r18 = 3
            r2 = r36
            r4 = r3
            r3 = r37
            r11 = r4
            r20 = 2
            r4 = r14
            r22 = r13
            r21 = 9
            r13 = r5
            r5 = r10
            r23 = r0
            r0 = r6
            r24 = r15
            r15 = -2
            r6 = r12
            r1.<init>(r3, r4, r5, r6)
            r7.tipLayout = r0
            r1 = 0
            r0.setOrientation(r1)
            android.widget.LinearLayout r0 = r7.tipLayout
            r1 = 30
            r2 = 51
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createScroll(r3, r1, r2)
            r9.addView(r0, r1)
            java.lang.String r0 = "contacts_inviteBackground"
            int r0 = r7.getThemedColor(r0)
            r1 = 0
        L_0x194f:
            if (r1 >= r14) goto L_0x1a3d
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x196a
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            int r3 = r14 - r1
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            goto L_0x197a
        L_0x196a:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            java.lang.Object r2 = r2.get(r1)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x197a:
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            java.lang.String r5 = r5.currency
            java.lang.String r4 = r4.formatCurrencyString(r2, r5)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r8)
            r6 = 1096810496(0x41600000, float:14.0)
            r15 = 1
            r5.setTextSize(r15, r6)
            java.lang.String r6 = "fonts/rmedium.ttf"
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r5.setTypeface(r6)
            r5.setLines(r15)
            java.lang.Long r6 = java.lang.Long.valueOf(r2)
            r5.setTag(r6)
            r5.setMaxLines(r15)
            r5.setText(r4)
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r15 = 1097859072(0x41700000, float:15.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r25 = r9
            r9 = 0
            r5.setPadding(r6, r9, r15, r9)
            java.lang.String r6 = "chats_secretName"
            int r6 = r7.getThemedColor(r6)
            r5.setTextColor(r6)
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 536870911(0x1fffffff, float:1.0842021E-19)
            r9 = r9 & r0
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r6, r9)
            r5.setBackground(r6)
            r6 = 1
            r5.setSingleLine(r6)
            r6 = 17
            r5.setGravity(r6)
            android.widget.LinearLayout r6 = r7.tipLayout
            r29 = -2
            r30 = -1
            r31 = 19
            r32 = 0
            r33 = 0
            int r9 = r14 + -1
            if (r1 == r9) goto L_0x19f4
            r34 = 9
            goto L_0x19f6
        L_0x19f4:
            r34 = 0
        L_0x19f6:
            r35 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34, (int) r35)
            r6.addView(r5, r9)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12
            r6.<init>(r7, r5, r2)
            r5.setOnClickListener(r6)
            android.text.TextPaint r6 = r5.getPaint()
            float r6 = r6.measureText(r4)
            r29 = r2
            double r2 = (double) r6
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r3 = 1106247680(0x41var_, float:30.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r3 = 2131230954(0x7var_ea, float:1.8077975E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r2)
            r5.setTag(r3, r6)
            r3 = 0
            r6 = r10[r3]
            int r6 = java.lang.Math.max(r6, r2)
            r10[r3] = r6
            r6 = r12[r3]
            int r6 = r6 + r2
            r12[r3] = r6
            int r1 = r1 + 1
            r9 = r25
            r15 = -2
            goto L_0x194f
        L_0x1a3d:
            r25 = r9
            goto L_0x1a55
        L_0x1a40:
            r23 = r0
            r11 = r3
            r22 = r13
            r24 = r15
            r18 = 3
            r20 = 2
            r13 = r1
            goto L_0x1a55
        L_0x1a4d:
            r13 = r1
            r11 = r3
            r24 = r15
            r18 = 3
            r20 = 2
        L_0x1a55:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextPriceCell r1 = r7.totalCell
            r0.addView(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r7.resourcesProvider
            r1.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r0[r20] = r1
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r20]
            r1 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r13)
            r0.setBackgroundDrawable(r2)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r20]
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
            if (r3 == 0) goto L_0x1ac8
            int r3 = r3.length()
            if (r3 <= r1) goto L_0x1ac8
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
            goto L_0x1aca
        L_0x1ac8:
            java.lang.String r1 = r7.cardName
        L_0x1aca:
            r2 = 2131627380(0x7f0e0d74, float:1.8882023E38)
            java.lang.String r3 = "PaymentCheckoutMethod"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131165838(0x7var_e, float:1.7945904E38)
            r4 = 1
            r0.setTextAndValueAndIcon(r1, r2, r3, r4)
            r0 = 0
            boolean r1 = r7.isCheckoutPreview
            if (r1 == 0) goto L_0x1aee
            java.lang.String r1 = r7.cardName
            if (r1 == 0) goto L_0x1aeb
            int r1 = r1.length()
            if (r1 <= r4) goto L_0x1aeb
            r5 = 0
            goto L_0x1aed
        L_0x1aeb:
            r5 = 8
        L_0x1aed:
            r0 = r5
        L_0x1aee:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r2 = 0
            r1 = r1[r2]
            r1.setVisibility(r0)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r2]
            r1.addView(r3)
            int r1 = r7.currentStep
            r3 = 4
            if (r1 != r3) goto L_0x1b10
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r2]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda63 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda63
            r2.<init>(r7)
            r1.setOnClickListener(r2)
        L_0x1b10:
            r1 = 0
            r2 = 0
        L_0x1b12:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x1b34
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r3.users
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            long r4 = r3.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            long r9 = r6.provider_id
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x1b31
            r1 = r3
        L_0x1b31:
            int r2 = r2 + 1
            goto L_0x1b12
        L_0x1b34:
            if (r1 == 0) goto L_0x1b98
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r3 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r3.<init>(r8)
            r4 = 1
            r2[r4] = r3
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r2.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            java.lang.String r3 = r1.first_name
            java.lang.String r4 = r1.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r4)
            r4 = r3
            r5 = 2131627385(0x7f0e0d79, float:1.8882033E38)
            java.lang.String r6 = "PaymentCheckoutProvider"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131165840(0x7var_, float:1.7945908E38)
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r9 = r7.validateRequest
            if (r9 == 0) goto L_0x1b72
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.info
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 != 0) goto L_0x1b80
            org.telegram.tgnet.TLRPC$TL_shippingOption r9 = r7.shippingOption
            if (r9 != 0) goto L_0x1b80
        L_0x1b72:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x1b82
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x1b82
        L_0x1b80:
            r9 = 1
            goto L_0x1b83
        L_0x1b82:
            r9 = 0
        L_0x1b83:
            r2.setTextAndValueAndIcon(r3, r5, r6, r9)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r3 = 1
            r2 = r2[r3]
            r2.setVisibility(r0)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r7.detailSettingsCell
            r5 = r5[r3]
            r2.addView(r5)
            goto L_0x1b9a
        L_0x1b98:
            java.lang.String r4 = ""
        L_0x1b9a:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r2 = r7.validateRequest
            if (r2 != 0) goto L_0x1baa
            boolean r2 = r7.isCheckoutPreview
            if (r2 == 0) goto L_0x1d33
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            if (r2 == 0) goto L_0x1d33
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
            if (r2 == 0) goto L_0x1d33
        L_0x1baa:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r2 = r7.validateRequest
            if (r2 == 0) goto L_0x1bb1
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.info
            goto L_0x1bb5
        L_0x1bb1:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r2 = r2.saved_info
        L_0x1bb5:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r8)
            r3[r20] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r20]
            r5 = 8
            r3.setVisibility(r5)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r7.detailSettingsCell
            r5 = r5[r20]
            r3.addView(r5)
            org.telegram.tgnet.TLRPC$TL_postAddress r3 = r2.shipping_address
            if (r3 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r20]
            r5 = 0
            r3.setVisibility(r5)
            int r3 = r7.currentStep
            r5 = 4
            if (r3 != r5) goto L_0x1bfa
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r20]
            r5 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r20]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda0
            r5.<init>(r7)
            r3.setOnClickListener(r5)
            goto L_0x1CLASSNAME
        L_0x1bfa:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r20]
            int r5 = r7.getThemedColor(r11)
            r3.setBackgroundColor(r5)
        L_0x1CLASSNAME:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r8)
            r3[r18] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r18]
            r5 = 8
            r3.setVisibility(r5)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r7.detailSettingsCell
            r5 = r5[r18]
            r3.addView(r5)
            java.lang.String r3 = r2.name
            if (r3 == 0) goto L_0x1CLASSNAME
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r18]
            r5 = 0
            r3.setVisibility(r5)
            int r3 = r7.currentStep
            r5 = 4
            if (r3 != r5) goto L_0x1c4a
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r18]
            r5 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r18]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda1
            r5.<init>(r7)
            r3.setOnClickListener(r5)
            goto L_0x1CLASSNAME
        L_0x1c4a:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r18]
            int r5 = r7.getThemedColor(r11)
            r3.setBackgroundColor(r5)
        L_0x1CLASSNAME:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r8)
            r6 = 4
            r3[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            r5 = 8
            r3.setVisibility(r5)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r7.detailSettingsCell
            r5 = r5[r6]
            r3.addView(r5)
            java.lang.String r3 = r2.phone
            if (r3 == 0) goto L_0x1ca5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            r5 = 0
            r3.setVisibility(r5)
            int r3 = r7.currentStep
            if (r3 != r6) goto L_0x1c9a
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            r5 = 1
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r3.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda2
            r5.<init>(r7)
            r3.setOnClickListener(r5)
            goto L_0x1ca5
        L_0x1c9a:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            int r5 = r7.getThemedColor(r11)
            r3.setBackgroundColor(r5)
        L_0x1ca5:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r8)
            r6 = 5
            r3[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            r5 = 8
            r3.setVisibility(r5)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r7.detailSettingsCell
            r5 = r5[r6]
            r3.addView(r5)
            java.lang.String r3 = r2.email
            if (r3 == 0) goto L_0x1cf6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            r5 = 0
            r3.setVisibility(r5)
            int r3 = r7.currentStep
            r5 = 4
            if (r3 != r5) goto L_0x1ceb
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            r5 = 1
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r3.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda3
            r5.<init>(r7)
            r3.setOnClickListener(r5)
            goto L_0x1cf6
        L_0x1ceb:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            int r5 = r7.getThemedColor(r11)
            r3.setBackgroundColor(r5)
        L_0x1cf6:
            org.telegram.tgnet.TLRPC$TL_shippingOption r3 = r7.shippingOption
            if (r3 == 0) goto L_0x1d30
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r8)
            r6 = 6
            r3[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            int r5 = r7.getThemedColor(r11)
            r3.setBackgroundColor(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r3 = r7.detailSettingsCell
            r3 = r3[r6]
            org.telegram.tgnet.TLRPC$TL_shippingOption r5 = r7.shippingOption
            java.lang.String r5 = r5.title
            r9 = 2131627386(0x7f0e0d7a, float:1.8882035E38)
            java.lang.String r10 = "PaymentCheckoutShippingMethod"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r10 = 2131165839(0x7var_f, float:1.7945906E38)
            r12 = 0
            r3.setTextAndValueAndIcon(r5, r9, r10, r12)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r7.detailSettingsCell
            r5 = r5[r6]
            r3.addView(r5)
        L_0x1d30:
            r7.setAddressFields(r2)
        L_0x1d33:
            int r2 = r7.currentStep
            r3 = 4
            if (r2 != r3) goto L_0x1var_
            boolean r2 = r7.isCheckoutPreview
            r3 = 1
            r2 = r2 ^ r3
            r7.isAcceptTermsChecked = r2
            r7.recurrentAccepted = r2
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r2 = new org.telegram.ui.PaymentFormActivity$BottomFrameLayout
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            r2.<init>(r8, r3)
            r7.bottomLayout = r2
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 < r3) goto L_0x1d6f
            android.view.View r2 = new android.view.View
            r2.<init>(r8)
            java.lang.String r3 = "listSelectorSDK21"
            int r3 = r7.getThemedColor(r3)
            r5 = 0
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r3, (boolean) r5)
            r2.setBackground(r3)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r3 = r7.bottomLayout
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r3.addView(r2, r9)
            goto L_0x1d70
        L_0x1d6f:
            r6 = -1
        L_0x1d70:
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r2 = r7.bottomLayout
            r3 = 48
            r5 = 80
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r3, (int) r5)
            r5 = r28
            r5.addView(r2, r3)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r2 = r7.bottomLayout
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda13 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda13
            r3.<init>(r7, r4)
            r2.setOnClickListener(r3)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r8)
            r7.payTextView = r2
            java.lang.String r3 = "contacts_inviteText"
            int r3 = r7.getThemedColor(r3)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r7.payTextView
            r3 = 2131627383(0x7f0e0d77, float:1.8882029E38)
            r6 = 1
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.lang.String[] r10 = r7.totalPrice
            r12 = 0
            r10 = r10[r12]
            r9[r12] = r10
            java.lang.String r10 = "PaymentCheckoutPay"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r3, r9)
            r2.setText(r3)
            android.widget.TextView r2 = r7.payTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r6, r3)
            android.widget.TextView r2 = r7.payTextView
            r3 = 17
            r2.setGravity(r3)
            android.widget.TextView r2 = r7.payTextView
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r2.setTypeface(r3)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r2 = r7.bottomLayout
            android.widget.TextView r3 = r7.payTextView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9 = -1
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r2.addView(r3, r10)
            org.telegram.ui.Components.ContextProgressView r2 = new org.telegram.ui.Components.ContextProgressView
            r3 = 0
            r2.<init>(r8, r3)
            r7.progressViewButton = r2
            r3 = 4
            r2.setVisibility(r3)
            java.lang.String r2 = "contacts_inviteText"
            int r2 = r7.getThemedColor(r2)
            org.telegram.ui.Components.ContextProgressView r3 = r7.progressViewButton
            r6 = 805306367(0x2fffffff, float:4.6566126E-10)
            r6 = r6 & r2
            r3.setColors(r6, r2)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r3 = r7.bottomLayout
            org.telegram.ui.Components.ContextProgressView r6 = r7.progressViewButton
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)
            r3.addView(r6, r12)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r3 = r7.bottomLayout
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.recurring
            if (r6 == 0) goto L_0x1e12
            boolean r6 = r7.isAcceptTermsChecked
            if (r6 == 0) goto L_0x1e10
            goto L_0x1e12
        L_0x1e10:
            r6 = 0
            goto L_0x1e13
        L_0x1e12:
            r6 = 1
        L_0x1e13:
            r3.setChecked(r6)
            android.widget.TextView r3 = r7.payTextView
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.recurring
            if (r6 == 0) goto L_0x1e28
            boolean r6 = r7.isAcceptTermsChecked
            if (r6 != 0) goto L_0x1e28
            r6 = 1061997773(0x3f4ccccd, float:0.8)
            goto L_0x1e2a
        L_0x1e28:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x1e2a:
            r3.setAlpha(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r7.doneItem
            r6 = 0
            r3.setEnabled(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r7.doneItem
            android.view.View r3 = r3.getContentView()
            r6 = 4
            r3.setVisibility(r6)
            org.telegram.ui.PaymentFormActivity$18 r3 = new org.telegram.ui.PaymentFormActivity$18
            r3.<init>(r8)
            r7.webView = r3
            r6 = -1
            r3.setBackgroundColor(r6)
            android.webkit.WebView r3 = r7.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r6 = 1
            r3.setJavaScriptEnabled(r6)
            android.webkit.WebView r3 = r7.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r3.setDomStorageEnabled(r6)
            int r3 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r3 < r6) goto L_0x1e75
            android.webkit.WebView r3 = r7.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r6 = 0
            r3.setMixedContentMode(r6)
            android.webkit.CookieManager r3 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r6 = r7.webView
            r9 = 1
            r3.setAcceptThirdPartyCookies(r6, r9)
        L_0x1e75:
            android.webkit.WebView r3 = r7.webView
            org.telegram.ui.PaymentFormActivity$19 r6 = new org.telegram.ui.PaymentFormActivity$19
            r6.<init>()
            r3.setWebViewClient(r6)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            boolean r3 = r3.recurring
            if (r3 == 0) goto L_0x1f6f
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r3 = new org.telegram.ui.Cells.RecurrentPaymentsAcceptCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r36.getResourceProvider()
            r3.<init>(r8, r6)
            r7.recurrentAcceptCell = r3
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.recurring
            if (r6 == 0) goto L_0x1ea0
            boolean r6 = r7.isAcceptTermsChecked
            if (r6 == 0) goto L_0x1ea0
            r6 = 1
            goto L_0x1ea1
        L_0x1ea0:
            r6 = 0
        L_0x1ea1:
            r3.setChecked(r6)
            r3 = 2131627378(0x7f0e0d72, float:1.8882019E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString((int) r3)
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r3)
            r9 = 42
            int r9 = r3.indexOf(r9)
            r10 = 42
            int r10 = r3.lastIndexOf(r10)
            r12 = -1
            if (r9 == r12) goto L_0x1var_
            if (r10 == r12) goto L_0x1var_
            android.text.SpannableString r12 = new android.text.SpannableString
            int r14 = r9 + 1
            java.lang.String r14 = r3.substring(r14, r10)
            r12.<init>(r14)
            org.telegram.ui.Components.URLSpanNoUnderline r14 = new org.telegram.ui.Components.URLSpanNoUnderline
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r15 = r15.invoice
            java.lang.String r15 = r15.recurring_terms_url
            r14.<init>(r15)
            int r15 = r12.length()
            r18 = r1
            r1 = 33
            r20 = r2
            r2 = 0
            r12.setSpan(r14, r2, r15, r1)
            int r1 = r10 + 1
            r6.replace(r9, r1, r12)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = r3.substring(r2, r9)
            r1.append(r2)
            r1.append(r12)
            int r2 = r10 + 1
            java.lang.String r2 = r3.substring(r2)
            r1.append(r2)
            java.lang.String r3 = r1.toString()
            goto L_0x1f0b
        L_0x1var_:
            r18 = r1
            r20 = r2
        L_0x1f0b:
            java.lang.String r1 = "%1$s"
            int r2 = r3.indexOf(r1)
            r12 = -1
            if (r2 == r12) goto L_0x1var_
            int r12 = r1.length()
            int r12 = r12 + r2
            java.lang.String r14 = r7.currentBotName
            r6.replace(r2, r12, r14)
            org.telegram.ui.Components.TypefaceSpan r12 = new org.telegram.ui.Components.TypefaceSpan
            java.lang.String r14 = "fonts/rmedium.ttf"
            android.graphics.Typeface r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r12.<init>(r14)
            java.lang.String r14 = r7.currentBotName
            int r14 = r14.length()
            int r14 = r14 + r2
            r15 = 33
            r6.setSpan(r12, r2, r14, r15)
        L_0x1var_:
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r12 = r7.recurrentAcceptCell
            r12.setText(r6)
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r12 = r7.recurrentAcceptCell
            int r11 = r7.getThemedColor(r11)
            java.lang.String r14 = "listSelectorSDK21"
            int r14 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r11, r14)
            r12.setBackground(r11)
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r11 = r7.recurrentAcceptCell
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda4 r12 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda4
            r12.<init>(r7)
            r11.setOnClickListener(r12)
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r11 = r7.recurrentAcceptCell
            r28 = -1
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r30 = 80
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r5.addView(r11, r12)
            goto L_0x1var_
        L_0x1f6f:
            r18 = r1
            r20 = r2
        L_0x1var_:
            android.webkit.WebView r1 = r7.webView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r5.addView(r1, r2)
            android.webkit.WebView r1 = r7.webView
            r2 = 8
            r1.setVisibility(r2)
            goto L_0x1f8b
        L_0x1var_:
            r18 = r1
            r5 = r28
        L_0x1f8b:
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r7.resourcesProvider
            r2.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r3 = 1
            r1[r3] = r2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r3]
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r13)
            r1.setBackgroundDrawable(r2)
            if (r0 == 0) goto L_0x1fc0
            int r1 = r7.currentStep
            r2 = 4
            if (r1 != r2) goto L_0x1fc0
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r1 = r7.validateRequest
            if (r1 != 0) goto L_0x1fc0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            if (r1 == 0) goto L_0x1fb8
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r1 = r1.saved_info
            if (r1 != 0) goto L_0x1fc0
        L_0x1fb8:
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r2 = 1
            r1 = r1[r2]
            r1.setVisibility(r0)
        L_0x1fc0:
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            r3 = 1
            r2 = r2[r3]
            r3 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r3)
            r1.addView(r2, r3)
        L_0x1fd1:
            android.view.View r0 = r7.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m4141lambda$createView$1$orgtelegramuiPaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda58(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4140lambda$createView$0$orgtelegramuiPaymentFormActivity(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
        this.countryName = country.shortname;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m4150lambda$createView$2$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
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
    public /* synthetic */ void m4160lambda$createView$3$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveShippingInfo;
        this.saveShippingInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4163lambda$createView$4$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m4165lambda$createView$6$orgtelegramuiPaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda59(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4164lambda$createView$5$orgtelegramuiPaymentFormActivity(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m4166lambda$createView$7$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
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
    public /* synthetic */ void m4167lambda$createView$8$orgtelegramuiPaymentFormActivity(View v) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4168lambda$createView$9$orgtelegramuiPaymentFormActivity(View v) {
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
    public /* synthetic */ boolean m4142lambda$createView$11$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4143lambda$createView$12$orgtelegramuiPaymentFormActivity(View v) {
        this.passwordOk = false;
        goToNextStep();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4144lambda$createView$13$orgtelegramuiPaymentFormActivity(View v) {
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
    public /* synthetic */ void m4145lambda$createView$15$orgtelegramuiPaymentFormActivity(TextView valueTextView, long amount, View v) {
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
    public /* synthetic */ void m4147lambda$createView$17$orgtelegramuiPaymentFormActivity(View v) {
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentCheckoutMethod", NUM), true);
            builder.setItems(new CharSequence[]{this.cardName, LocaleController.getString("PaymentCheckoutMethodNewCard", NUM)}, new int[]{NUM, NUM}, new PaymentFormActivity$$ExternalSyntheticLambda10(this));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4146lambda$createView$16$orgtelegramuiPaymentFormActivity(DialogInterface dialog, int which) {
        if (which == 1) {
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 2, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
    public /* synthetic */ void m4148lambda$createView$18$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4149lambda$createView$19$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4151lambda$createView$20$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$21$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4152lambda$createView$21$orgtelegramuiPaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$24$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4155lambda$createView$24$orgtelegramuiPaymentFormActivity(String providerName, View v) {
        int step;
        if (this.paymentForm.invoice.recurring && !this.recurrentAccepted) {
            AndroidUtilities.shakeViewSpring((View) this.recurrentAcceptCell.getTextView(), 4.5f);
            try {
                this.recurrentAcceptCell.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
        } else if (!this.isCheckoutPreview || this.paymentForm.saved_info == null || this.validateRequest != null) {
            View view = v;
            if (!this.isCheckoutPreview || ((this.paymentForm.saved_info != null || (!this.paymentForm.invoice.shipping_address_requested && !this.paymentForm.invoice.email_requested && !this.paymentForm.invoice.name_requested && !this.paymentForm.invoice.phone_requested)) && this.paymentForm.saved_credentials != null && (this.shippingOption != null || !this.paymentForm.invoice.flexible))) {
                if (!this.paymentForm.password_missing && this.paymentForm.saved_credentials != null) {
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    }
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword == null) {
                        this.needPayAfterTransition = true;
                        presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 3, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment));
                        this.needPayAfterTransition = false;
                        return;
                    } else if (this.isCheckoutPreview) {
                        this.isCheckoutPreview = false;
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
                    }
                }
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
                    builder.setPositiveButton(LocaleController.getString("Continue", NUM), new PaymentFormActivity$$ExternalSyntheticLambda21(this));
                    showDialog(builder.create());
                    return;
                }
                showPayAlert(this.totalPrice[0]);
                return;
            }
            if (this.paymentForm.saved_info == null && (this.paymentForm.invoice.shipping_address_requested || this.paymentForm.invoice.email_requested || this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested)) {
                step = 0;
            } else if (this.paymentForm.saved_credentials == null) {
                step = 2;
            } else {
                step = 1;
            }
            this.paymentStatusSent = true;
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, step, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC.TL_inputPaymentCredentialsGooglePay) null, this.parentFragment));
        } else {
            setDonePressed(true);
            sendSavedForm(new PaymentFormActivity$$ExternalSyntheticLambda28(this, v));
        }
    }

    /* renamed from: lambda$createView$22$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4153lambda$createView$22$orgtelegramuiPaymentFormActivity(View v) {
        setDonePressed(false);
        v.callOnClick();
    }

    /* renamed from: lambda$createView$23$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4154lambda$createView$23$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    /* renamed from: lambda$createView$25$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4156lambda$createView$25$orgtelegramuiPaymentFormActivity(View v) {
        if (!this.donePressed) {
            boolean z = !this.recurrentAccepted;
            this.recurrentAccepted = z;
            this.recurrentAcceptCell.setChecked(z);
            this.bottomLayout.setChecked(this.recurrentAccepted);
        }
    }

    /* renamed from: lambda$createView$26$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m4157lambda$createView$26$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* renamed from: lambda$createView$28$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4158lambda$createView$28$orgtelegramuiPaymentFormActivity(View v) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), PaymentFormActivity$$ExternalSyntheticLambda56.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    static /* synthetic */ void lambda$createView$27(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$30$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4161lambda$createView$30$orgtelegramuiPaymentFormActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        String text = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(text);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", NUM));
        builder.setPositiveButton(LocaleController.getString("Disable", NUM), new PaymentFormActivity$$ExternalSyntheticLambda32(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(getThemedColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$createView$29$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4159lambda$createView$29$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* renamed from: lambda$createView$31$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ boolean m4162lambda$createView$31$orgtelegramuiPaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
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
    public void setAddressFields(TLRPC.TL_paymentRequestedInfo info) {
        boolean z = true;
        int i = 0;
        if (info.shipping_address != null) {
            this.detailSettingsCell[2].setTextAndValueAndIcon(String.format("%s %s, %s, %s, %s, %s", new Object[]{info.shipping_address.street_line1, info.shipping_address.street_line2, info.shipping_address.city, info.shipping_address.state, info.shipping_address.country_iso2, info.shipping_address.post_code}), LocaleController.getString("PaymentShippingAddress", NUM), NUM, true);
        }
        this.detailSettingsCell[2].setVisibility(info.shipping_address != null ? 0 : 8);
        if (info.name != null) {
            this.detailSettingsCell[3].setTextAndValueAndIcon(info.name, LocaleController.getString("PaymentCheckoutName", NUM), NUM, true);
        }
        this.detailSettingsCell[3].setVisibility(info.name != null ? 0 : 8);
        if (info.phone != null) {
            this.detailSettingsCell[4].setTextAndValueAndIcon(PhoneFormat.getInstance().format(info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", NUM), NUM, (info.email == null && this.shippingOption == null) ? false : true);
        }
        this.detailSettingsCell[4].setVisibility(info.phone != null ? 0 : 8);
        if (info.email != null) {
            TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[5];
            String str = info.email;
            String string = LocaleController.getString("PaymentCheckoutEmail", NUM);
            if (this.shippingOption == null) {
                z = false;
            }
            textDetailSettingsCell.setTextAndValueAndIcon(str, string, NUM, z);
        }
        TextDetailSettingsCell textDetailSettingsCell2 = this.detailSettingsCell[5];
        if (info.email == null) {
            i = 8;
        }
        textDetailSettingsCell2.setVisibility(i);
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
            int color = getThemedColor("contacts_inviteBackground");
            int N2 = this.tipLayout.getChildCount();
            for (int b = 0; b < N2; b++) {
                TextView child = (TextView) this.tipLayout.getChildAt(b);
                if (child.getTag().equals(this.tipAmount)) {
                    Theme.setDrawableColor(child.getBackground(), color);
                    child.setTextColor(getThemedColor("contacts_inviteText"));
                } else {
                    Theme.setDrawableColor(child.getBackground(), NUM & color);
                    child.setTextColor(getThemedColor("chats_secretName"));
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
        this.googlePayButton.setOnClickListener(new PaymentFormActivity$$ExternalSyntheticLambda60(this));
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
    /* renamed from: lambda$createGooglePayButton$32$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m4139x557681fa(android.view.View r9) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.m4139x557681fa(android.view.View):void");
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda46(this), 10);
        }
    }

    /* renamed from: lambda$loadPasswordInfo$35$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4172lambda$loadPasswordInfo$35$orgtelegramuiPaymentFormActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda36(this, error, response));
    }

    /* renamed from: lambda$loadPasswordInfo$34$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4171lambda$loadPasswordInfo$34$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject response) {
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
                PaymentFormActivity$$ExternalSyntheticLambda25 paymentFormActivity$$ExternalSyntheticLambda25 = new PaymentFormActivity$$ExternalSyntheticLambda25(this);
                this.shortPollRunnable = paymentFormActivity$$ExternalSyntheticLambda25;
                AndroidUtilities.runOnUIThread(paymentFormActivity$$ExternalSyntheticLambda25, 5000);
            }
        }
    }

    /* renamed from: lambda$loadPasswordInfo$33$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4170lambda$loadPasswordInfo$33$orgtelegramuiPaymentFormActivity() {
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
        builder.setPositiveButton(LocaleController.getString("Continue", NUM), new PaymentFormActivity$$ExternalSyntheticLambda43(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showPayAlert$36$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4193lambda$showPayAlert$36$orgtelegramuiPaymentFormActivity(DialogInterface dialogInterface, int i) {
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
                this.paymentsClient.isReadyToPay(request).addOnCompleteListener(getParentActivity(), new PaymentFormActivity$$ExternalSyntheticLambda24(this));
            }
        }
    }

    /* renamed from: lambda$initGooglePay$37$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4169lambda$initGooglePay$37$orgtelegramuiPaymentFormActivity(Task task1) {
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
        if (this.currentStep != 4 || this.isCheckoutPreview) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentFinished);
        }
        return super.onFragmentCreate();
    }

    public int getOtherSameFragmentDiff() {
        if (this.parentLayout == null || this.parentLayout.fragmentsStack == null) {
            return 0;
        }
        int cur = this.parentLayout.fragmentsStack.indexOf(this);
        if (cur == -1) {
            cur = this.parentLayout.fragmentsStack.size();
        }
        int i = cur;
        int a = 0;
        while (true) {
            if (a >= this.parentLayout.fragmentsStack.size()) {
                break;
            } else if (this.parentLayout.fragmentsStack.get(a) instanceof PaymentFormActivity) {
                i = a;
                break;
            } else {
                a++;
            }
        }
        return i - cur;
    }

    public void onFragmentDestroy() {
        PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
        if (paymentFormActivityDelegate != null) {
            paymentFormActivityDelegate.onFragmentDestroyed();
        }
        if (!this.paymentStatusSent && this.paymentFormCallback != null && getOtherSameFragmentDiff() == 0) {
            this.paymentFormCallback.onInvoiceStatusChanged(InvoiceStatus.CANCELLED);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (this.currentStep != 4 || this.isCheckoutPreview) {
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
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        if (this.currentStep == 4 && this.needPayAfterTransition) {
            this.needPayAfterTransition = false;
            this.bottomLayout.callOnClick();
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            WebView webView2 = this.webView;
            if (webView2 == null) {
                int i = this.currentStep;
                if (i == 2) {
                    AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda26(this), 100);
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

    /* renamed from: lambda$onTransitionAnimationEnd$38$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4174xCLASSNAME() {
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
            this.paymentStatusSent = true;
            removeSelfFromStack();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == 991) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda27(this, resultCode, data));
        }
    }

    /* renamed from: lambda$onActivityResultFragment$39$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4173xb241CLASSNAME(int resultCode, Intent data) {
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
        switch (this.currentStep) {
            case 0:
                PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
                if (paymentFormActivityDelegate != null) {
                    paymentFormActivityDelegate.didSelectNewAddress(this.validateRequest);
                    finishFragment();
                    return;
                }
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
                int i = nextStep;
                int i2 = nextStep;
                PaymentFormActivity paymentFormActivity = r2;
                PaymentFormActivity paymentFormActivity2 = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, i, this.requestedInfo, (TLRPC.TL_shippingOption) null, (Long) null, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                presentFragment(paymentFormActivity, this.isWebView);
                return;
            case 1:
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
                int i3 = nextStep2;
                int i4 = nextStep2;
                PaymentFormActivity paymentFormActivity3 = r2;
                PaymentFormActivity paymentFormActivity4 = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, i3, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                presentFragment(paymentFormActivity3, this.isWebView);
                return;
            case 2:
                if (!this.paymentForm.password_missing || !this.saveCardInfo) {
                    PaymentFormActivityDelegate paymentFormActivityDelegate2 = this.delegate;
                    if (paymentFormActivityDelegate2 != null) {
                        paymentFormActivityDelegate2.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials);
                        finishFragment();
                        return;
                    }
                    presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                    return;
                }
                PaymentFormActivity paymentFormActivity5 = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 6, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment);
                this.passwordFragment = paymentFormActivity5;
                paymentFormActivity5.setCurrentPassword(this.currentPassword);
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
                return;
            case 3:
                if (this.passwordOk) {
                    nextStep3 = 4;
                } else {
                    nextStep3 = 2;
                }
                TLRPC.TL_payments_paymentForm tL_payments_paymentForm = this.paymentForm;
                MessageObject messageObject2 = this.messageObject;
                String str = this.invoiceSlug;
                TLRPC.TL_payments_validatedRequestedInfo tL_payments_validatedRequestedInfo = this.requestedInfo;
                TLRPC.TL_shippingOption tL_shippingOption = this.shippingOption;
                Long l = this.tipAmount;
                String str2 = this.paymentJson;
                String str3 = this.cardName;
                TLRPC.TL_payments_validateRequestedInfo tL_payments_validateRequestedInfo = this.validateRequest;
                boolean z = this.saveCardInfo;
                TLRPC.TL_inputPaymentCredentialsGooglePay tL_inputPaymentCredentialsGooglePay = this.googlePayCredentials;
                presentFragment(new PaymentFormActivity(tL_payments_paymentForm, messageObject2, str, nextStep3, tL_payments_validatedRequestedInfo, tL_shippingOption, l, str2, str3, tL_payments_validateRequestedInfo, z, tL_inputPaymentCredentialsGooglePay, this.parentFragment), true);
                return;
            case 4:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
                if ((this.botUser.username == null || !this.botUser.username.equalsIgnoreCase(getMessagesController().premiumBotUsername)) && (this.invoiceSlug == null || getMessagesController().premiumInvoiceSlug == null || !ColorUtils$$ExternalSyntheticBackport0.m(this.invoiceSlug, getMessagesController().premiumInvoiceSlug))) {
                    finishFragment();
                    return;
                }
                Iterator it = new ArrayList(getParentLayout().fragmentsStack).iterator();
                while (it.hasNext()) {
                    BaseFragment fragment = (BaseFragment) it.next();
                    if ((fragment instanceof ChatActivity) || (fragment instanceof PremiumPreviewFragment)) {
                        fragment.removeSelfFromStack();
                    }
                }
                presentFragment(new PremiumPreviewFragment((String) null).setForcePremium(), true);
                if (getParentActivity() instanceof LaunchActivity) {
                    try {
                        this.fragmentView.performHapticFeedback(3, 2);
                    } catch (Exception e) {
                    }
                    ((LaunchActivity) getParentActivity()).getFireworksOverlay().start();
                    return;
                }
                return;
            case 6:
                if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials)) {
                    presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
                    return;
                }
                finishFragment();
                return;
            default:
                return;
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
            Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda44(this, clear, email, firstPassword, req));
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new PaymentFormActivity$$ExternalSyntheticLambda47(this), 10);
    }

    /* renamed from: lambda$sendSavePassword$41$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4183lambda$sendSavePassword$41$orgtelegramuiPaymentFormActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda35(this, error));
    }

    /* renamed from: lambda$sendSavePassword$40$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4182lambda$sendSavePassword$40$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error) {
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
            int time = Utilities.parseInt((CharSequence) error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* renamed from: lambda$sendSavePassword$46$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4188lambda$sendSavePassword$46$orgtelegramuiPaymentFormActivity(boolean clear, String email, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda42(this, error, clear, response, email));
    }

    /* renamed from: lambda$sendSavePassword$47$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4189lambda$sendSavePassword$47$orgtelegramuiPaymentFormActivity(boolean clear, String email, String firstPassword, TLRPC.TL_account_updatePasswordSettings req) {
        RequestDelegate requestDelegate = new PaymentFormActivity$$ExternalSyntheticLambda55(this, clear, email);
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

    /* renamed from: lambda$sendSavePassword$45$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4187lambda$sendSavePassword$45$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, boolean clear, TLObject response, String email) {
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
                    this.emailCodeLength = Utilities.parseInt((CharSequence) error.text).intValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$ExternalSyntheticLambda53(this, email));
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
                    int time = Utilities.parseInt((CharSequence) error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
                }
            } else if (getParentActivity() != null) {
                goToNextStep();
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda54(this, clear), 8);
        }
    }

    /* renamed from: lambda$sendSavePassword$43$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4185lambda$sendSavePassword$43$orgtelegramuiPaymentFormActivity(boolean clear, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda40(this, error2, response2, clear));
    }

    /* renamed from: lambda$sendSavePassword$42$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4184lambda$sendSavePassword$42$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            sendSavePassword(clear);
        }
    }

    /* renamed from: lambda$sendSavePassword$44$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4186lambda$sendSavePassword$44$orgtelegramuiPaymentFormActivity(String email, DialogInterface dialogInterface, int i) {
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
            Integer month2 = Utilities.parseInt((CharSequence) args[0]);
            year = Utilities.parseInt((CharSequence) args[1]);
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
                        public /* synthetic */ void m4194lambda$onSuccess$0$orgtelegramuiPaymentFormActivity$25() {
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

    private void sendSavedForm(Runnable callback) {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
                inputInvoice.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                inputInvoice.msg_id = this.messageObject.getId();
                this.validateRequest.invoice = inputInvoice;
            } else {
                TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
                inputInvoice2.slug = this.invoiceSlug;
                this.validateRequest.invoice = inputInvoice2;
            }
            this.validateRequest.save = true;
            this.validateRequest.info = this.paymentForm.saved_info;
            TLObject req = this.validateRequest;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$ExternalSyntheticLambda48(this, callback, req), 2);
        }
    }

    /* renamed from: lambda$sendSavedForm$50$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4192lambda$sendSavedForm$50$orgtelegramuiPaymentFormActivity(Runnable callback, TLObject req, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda31(this, response, callback));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda38(this, error, req));
        }
    }

    /* renamed from: lambda$sendSavedForm$48$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4190lambda$sendSavedForm$48$orgtelegramuiPaymentFormActivity(TLObject response, Runnable callback) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
        callback.run();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* renamed from: lambda$sendSavedForm$49$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4191lambda$sendSavedForm$49$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject req) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public void sendForm() {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
                inputInvoice.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                inputInvoice.msg_id = this.messageObject.getId();
                this.validateRequest.invoice = inputInvoice;
            } else {
                TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
                inputInvoice2.slug = this.invoiceSlug;
                this.validateRequest.invoice = inputInvoice2;
            }
            this.validateRequest.save = this.saveShippingInfo;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new PaymentFormActivity$$ExternalSyntheticLambda50(this, this.validateRequest), 2);
        }
    }

    /* renamed from: lambda$sendForm$54$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4181lambda$sendForm$54$orgtelegramuiPaymentFormActivity(TLObject req, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda30(this, response));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda37(this, error, req));
        }
    }

    /* renamed from: lambda$sendForm$52$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4179lambda$sendForm$52$orgtelegramuiPaymentFormActivity(TLObject response) {
        this.requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC.TL_payments_clearSavedInfo req1 = new TLRPC.TL_payments_clearSavedInfo();
            req1.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, PaymentFormActivity$$ExternalSyntheticLambda57.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    static /* synthetic */ void lambda$sendForm$51(TLObject response1, TLRPC.TL_error error1) {
    }

    /* renamed from: lambda$sendForm$53$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4180lambda$sendForm$53$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject req) {
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
            if (this.messageObject != null) {
                TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
                inputInvoice.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                inputInvoice.msg_id = this.messageObject.getId();
                req.invoice = inputInvoice;
            } else {
                TLRPC.TL_inputInvoiceSlug inputInvoice2 = new TLRPC.TL_inputInvoiceSlug();
                inputInvoice2.slug = this.invoiceSlug;
                req.invoice = inputInvoice2;
            }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$ExternalSyntheticLambda52(this, req), 2);
        }
    }

    /* renamed from: lambda$sendData$58$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4178lambda$sendData$58$orgtelegramuiPaymentFormActivity(TLRPC.TL_payments_sendPaymentForm req, TLObject response, TLRPC.TL_error error) {
        if (response == null) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda41(this, error, req));
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
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda45(this, message));
        } else if (response instanceof TLRPC.TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda29(this, response));
        }
    }

    /* renamed from: lambda$sendData$55$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4175lambda$sendData$55$orgtelegramuiPaymentFormActivity(TLRPC.Message[] message) {
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback2 = this.paymentFormCallback;
        if (paymentFormCallback2 != null) {
            paymentFormCallback2.onInvoiceStatusChanged(InvoiceStatus.PAID);
        }
        goToNextStep();
        if (this.parentFragment instanceof ChatActivity) {
            ((ChatActivity) this.parentFragment).getUndoView().showWithAction(0, 77, (Object) AndroidUtilities.replaceTags(LocaleController.formatString("PaymentInfoHint", NUM, this.totalPrice[0], this.currentItemName)), (Object) message[0], (Runnable) null, (Runnable) null);
        }
    }

    /* renamed from: lambda$sendData$56$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4176lambda$sendData$56$orgtelegramuiPaymentFormActivity(TLObject response) {
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
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback2 = this.paymentFormCallback;
        if (paymentFormCallback2 != null) {
            paymentFormCallback2.onInvoiceStatusChanged(InvoiceStatus.PENDING);
        }
    }

    /* renamed from: lambda$sendData$57$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4177lambda$sendData$57$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLRPC.TL_payments_sendPaymentForm req) {
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback2 = this.paymentFormCallback;
        if (paymentFormCallback2 != null) {
            paymentFormCallback2.onInvoiceStatusChanged(InvoiceStatus.FAILED);
        }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$ExternalSyntheticLambda49(this, password, req), 2);
        }
    }

    /* renamed from: lambda$checkPassword$63$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4138lambda$checkPassword$63$orgtelegramuiPaymentFormActivity(String password, TLRPC.TL_account_getPassword req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda39(this, error, response, password, req));
    }

    /* renamed from: lambda$checkPassword$62$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4137lambda$checkPassword$62$orgtelegramuiPaymentFormActivity(TLRPC.TL_error error, TLObject response, String password, TLRPC.TL_account_getPassword req) {
        if (error == null) {
            TLRPC.TL_account_password currentPassword2 = (TLRPC.TL_account_password) response;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword2, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (!currentPassword2.has_password) {
                this.passwordOk = false;
                goToNextStep();
            } else {
                Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda34(this, currentPassword2, AndroidUtilities.getStringBytes(password)));
            }
        } else {
            AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    /* renamed from: lambda$checkPassword$61$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4136lambda$checkPassword$61$orgtelegramuiPaymentFormActivity(TLRPC.TL_account_password currentPassword2, byte[] passwordBytes) {
        byte[] x_bytes;
        if (currentPassword2.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(passwordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword2.current_algo);
        } else {
            x_bytes = null;
        }
        TLRPC.TL_account_getTmpPassword req1 = new TLRPC.TL_account_getTmpPassword();
        req1.period = 1800;
        RequestDelegate requestDelegate = new PaymentFormActivity$$ExternalSyntheticLambda51(this, req1);
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

    /* renamed from: lambda$checkPassword$60$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4135lambda$checkPassword$60$orgtelegramuiPaymentFormActivity(TLRPC.TL_account_getTmpPassword req1, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda33(this, response1, error1, req1));
    }

    /* renamed from: lambda$checkPassword$59$org-telegram-ui-PaymentFormActivity  reason: not valid java name */
    public /* synthetic */ void m4134lambda$checkPassword$59$orgtelegramuiPaymentFormActivity(TLObject response1, TLRPC.TL_error error1, TLRPC.TL_account_getTmpPassword req1) {
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
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f})});
                if (!isFinishing()) {
                    this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
                }
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

    public boolean presentFragment(BaseFragment fragment) {
        onPresentFragment(fragment);
        return super.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        onPresentFragment(fragment);
        return super.presentFragment(fragment, removeLast);
    }

    private void onPresentFragment(BaseFragment fragment) {
        AndroidUtilities.hideKeyboard(this.fragmentView);
        if (fragment instanceof PaymentFormActivity) {
            ((PaymentFormActivity) fragment).paymentFormCallback = this.paymentFormCallback;
            ((PaymentFormActivity) fragment).resourcesProvider = this.resourcesProvider;
            ((PaymentFormActivity) fragment).needPayAfterTransition = this.needPayAfterTransition;
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

    private class BottomFrameLayout extends FrameLayout {
        Paint paint = new Paint(1);
        float progress;
        SpringAnimation springAnimation;

        public BottomFrameLayout(Context context, TLRPC.TL_payments_paymentForm paymentForm) {
            super(context);
            this.progress = (!paymentForm.invoice.recurring || PaymentFormActivity.this.isAcceptTermsChecked) ? 1.0f : 0.0f;
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(PaymentFormActivity.this.getThemedColor("switchTrackBlue"));
            this.paint.setColor(PaymentFormActivity.this.getThemedColor("contacts_inviteBackground"));
            canvas.drawCircle((float) (LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(28.0f) : AndroidUtilities.dp(28.0f)), (float) (-AndroidUtilities.dp(28.0f)), ((float) Math.max(getWidth(), getHeight())) * this.progress, this.paint);
        }

        public void setChecked(boolean checked) {
            SpringAnimation springAnimation2 = this.springAnimation;
            if (springAnimation2 != null) {
                springAnimation2.cancel();
            }
            float to = checked ? 1.0f : 0.0f;
            if (this.progress != to) {
                SpringAnimation spring = new SpringAnimation(new FloatValueHolder(this.progress * 100.0f)).setSpring(new SpringForce(100.0f * to).setStiffness(checked ? 500.0f : 650.0f).setDampingRatio(1.0f));
                this.springAnimation = spring;
                spring.addUpdateListener(new PaymentFormActivity$BottomFrameLayout$$ExternalSyntheticLambda1(this));
                this.springAnimation.addEndListener(new PaymentFormActivity$BottomFrameLayout$$ExternalSyntheticLambda0(this));
                this.springAnimation.start();
            }
        }

        /* renamed from: lambda$setChecked$0$org-telegram-ui-PaymentFormActivity$BottomFrameLayout  reason: not valid java name */
        public /* synthetic */ void m4195x718bb2b5(DynamicAnimation animation, float value, float velocity) {
            this.progress = value / 100.0f;
            if (PaymentFormActivity.this.payTextView != null) {
                PaymentFormActivity.this.payTextView.setAlpha((this.progress * 0.2f) + 0.8f);
            }
            invalidate();
        }

        /* renamed from: lambda$setChecked$1$org-telegram-ui-PaymentFormActivity$BottomFrameLayout  reason: not valid java name */
        public /* synthetic */ void m4196x9ae007f6(DynamicAnimation animation, boolean canceled1, float value, float velocity) {
            if (animation == this.springAnimation) {
                this.springAnimation = null;
            }
        }
    }
}
