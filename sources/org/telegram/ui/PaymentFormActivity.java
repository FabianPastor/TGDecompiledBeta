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
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$TL_account_confirmPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_resendPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_inputInvoiceMessage;
import org.telegram.tgnet.TLRPC$TL_inputInvoiceSlug;
import org.telegram.tgnet.TLRPC$TL_inputPaymentCredentials;
import org.telegram.tgnet.TLRPC$TL_inputPaymentCredentialsGooglePay;
import org.telegram.tgnet.TLRPC$TL_inputPaymentCredentialsSaved;
import org.telegram.tgnet.TLRPC$TL_invoice;
import org.telegram.tgnet.TLRPC$TL_labeledPrice;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo;
import org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard;
import org.telegram.tgnet.TLRPC$TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC$TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC$TL_payments_paymentResult;
import org.telegram.tgnet.TLRPC$TL_payments_paymentVerificationNeeded;
import org.telegram.tgnet.TLRPC$TL_payments_sendPaymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo;
import org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo;
import org.telegram.tgnet.TLRPC$TL_postAddress;
import org.telegram.tgnet.TLRPC$TL_shippingOption;
import org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewMessage;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
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
    private TLRPC$User botUser;
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
    public TLRPC$TL_account_password currentPassword;
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
    public TLRPC$TL_inputPaymentCredentialsGooglePay googlePayCredentials;
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
    public TLRPC$TL_payments_paymentForm paymentForm;
    private PaymentFormCallback paymentFormCallback;
    private PaymentInfoCell paymentInfoCell;
    /* access modifiers changed from: private */
    public String paymentJson;
    private TLRPC$TL_payments_paymentReceipt paymentReceipt;
    private boolean paymentStatusSent;
    private PaymentsClient paymentsClient;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap;
    private ArrayList<TLRPC$TL_labeledPrice> prices;
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
    public TLRPC$TL_payments_validatedRequestedInfo requestedInfo;
    private Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public boolean saveCardInfo;
    private boolean saveShippingInfo;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell[] settingsCell;
    /* access modifiers changed from: private */
    public TLRPC$TL_shippingOption shippingOption;
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
    /* access modifiers changed from: private */
    public TLRPC$TL_payments_validateRequestedInfo validateRequest;
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

    private interface PaymentFormActivityDelegate {

        /* renamed from: org.telegram.ui.PaymentFormActivity$PaymentFormActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$currentPasswordUpdated(PaymentFormActivityDelegate paymentFormActivityDelegate, TLRPC$TL_account_password tLRPC$TL_account_password) {
            }

            public static void $default$didSelectNewAddress(PaymentFormActivityDelegate paymentFormActivityDelegate, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
            }

            public static boolean $default$didSelectNewCard(PaymentFormActivityDelegate paymentFormActivityDelegate, String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                return false;
            }

            public static void $default$onFragmentDestroyed(PaymentFormActivityDelegate paymentFormActivityDelegate) {
            }
        }

        void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password);

        void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo);

        boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay);

        void onFragmentDestroyed();
    }

    public interface PaymentFormCallback {
        void onInvoiceStatusChanged(InvoiceStatus invoiceStatus);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$27(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendForm$51(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$TelegramWebviewProxy$$ExternalSyntheticLambda0(this, str, str2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$postEvent$0(String str, String str2) {
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
            PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
            paymentFormActivity.presentFragment(new TwoStepVerificationSetupActivity(6, paymentFormActivity.currentPassword));
        }
    }

    public PaymentFormActivity(TLRPC$TL_payments_paymentReceipt tLRPC$TL_payments_paymentReceipt) {
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
        TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = new TLRPC$TL_payments_paymentForm();
        this.paymentForm = tLRPC$TL_payments_paymentForm;
        this.paymentReceipt = tLRPC$TL_payments_paymentReceipt;
        tLRPC$TL_payments_paymentForm.bot_id = tLRPC$TL_payments_paymentReceipt.bot_id;
        tLRPC$TL_payments_paymentForm.invoice = tLRPC$TL_payments_paymentReceipt.invoice;
        tLRPC$TL_payments_paymentForm.provider_id = tLRPC$TL_payments_paymentReceipt.provider_id;
        tLRPC$TL_payments_paymentForm.users = tLRPC$TL_payments_paymentReceipt.users;
        this.shippingOption = tLRPC$TL_payments_paymentReceipt.shipping;
        long j = tLRPC$TL_payments_paymentReceipt.tip_amount;
        if (j != 0) {
            this.tipAmount = Long.valueOf(j);
        }
        TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$TL_payments_paymentReceipt.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = tLRPC$TL_payments_paymentReceipt.title;
        if (tLRPC$TL_payments_paymentReceipt.info != null) {
            this.validateRequest = new TLRPC$TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC$TL_inputInvoiceMessage tLRPC$TL_inputInvoiceMessage = new TLRPC$TL_inputInvoiceMessage();
                tLRPC$TL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(tLRPC$TL_payments_paymentReceipt.bot_id);
                this.validateRequest.invoice = tLRPC$TL_inputInvoiceMessage;
            } else {
                TLRPC$TL_inputInvoiceSlug tLRPC$TL_inputInvoiceSlug = new TLRPC$TL_inputInvoiceSlug();
                tLRPC$TL_inputInvoiceSlug.slug = this.invoiceSlug;
                this.validateRequest.invoice = tLRPC$TL_inputInvoiceSlug;
            }
            this.validateRequest.info = tLRPC$TL_payments_paymentReceipt.info;
        }
        this.cardName = tLRPC$TL_payments_paymentReceipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, String str, BaseFragment baseFragment) {
        this(tLRPC$TL_payments_paymentForm, (MessageObject) null, str, baseFragment);
    }

    public PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, BaseFragment baseFragment) {
        this(tLRPC$TL_payments_paymentForm, messageObject2, (String) null, baseFragment);
    }

    public PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, String str, BaseFragment baseFragment) {
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
        init(tLRPC$TL_payments_paymentForm, messageObject2, str, 4, (TLRPC$TL_payments_validatedRequestedInfo) null, (TLRPC$TL_shippingOption) null, (Long) null, (String) null, (String) null, (TLRPC$TL_payments_validateRequestedInfo) null, false, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, baseFragment);
    }

    private PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, String str, int i, TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo, TLRPC$TL_shippingOption tLRPC$TL_shippingOption, Long l, String str2, String str3, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay, BaseFragment baseFragment) {
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
        init(tLRPC$TL_payments_paymentForm, messageObject2, str, i, tLRPC$TL_payments_validatedRequestedInfo, tLRPC$TL_shippingOption, l, str2, str3, tLRPC$TL_payments_validateRequestedInfo, z, tLRPC$TL_inputPaymentCredentialsGooglePay, baseFragment);
    }

    public void setPaymentFormCallback(PaymentFormCallback paymentFormCallback2) {
        this.paymentFormCallback = paymentFormCallback2;
    }

    private void setCurrentPassword(TLRPC$TL_account_password tLRPC$TL_account_password) {
        if (!tLRPC$TL_account_password.has_password) {
            this.currentPassword = tLRPC$TL_account_password;
            this.waitingForEmail = !TextUtils.isEmpty(tLRPC$TL_account_password.email_unconfirmed_pattern);
            updatePasswordFields();
        } else if (getParentActivity() != null) {
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    public void setResourcesProvider(Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    private void init(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, String str, int i, TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo, TLRPC$TL_shippingOption tLRPC$TL_shippingOption, Long l, String str2, String str3, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay, BaseFragment baseFragment) {
        this.currentStep = i;
        this.parentFragment = baseFragment;
        this.paymentJson = str2;
        this.googlePayCredentials = tLRPC$TL_inputPaymentCredentialsGooglePay;
        this.requestedInfo = tLRPC$TL_payments_validatedRequestedInfo;
        this.paymentForm = tLRPC$TL_payments_paymentForm;
        this.shippingOption = tLRPC$TL_shippingOption;
        this.tipAmount = l;
        this.messageObject = messageObject2;
        this.invoiceSlug = str;
        this.saveCardInfo = z;
        boolean z2 = false;
        this.isWebView = !"stripe".equals(tLRPC$TL_payments_paymentForm.native_provider) && !"smartglocal".equals(this.paymentForm.native_provider);
        TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$TL_payments_paymentForm.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = tLRPC$TL_payments_paymentForm.title;
        this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
        this.saveShippingInfo = true;
        if (z || this.currentStep == 4) {
            this.saveCardInfo = z;
        } else {
            if (this.paymentForm.saved_credentials != null) {
                z2 = true;
            }
            this.saveCardInfo = z2;
        }
        if (str3 == null) {
            TLRPC$TL_paymentSavedCredentialsCard tLRPC$TL_paymentSavedCredentialsCard = tLRPC$TL_payments_paymentForm.saved_credentials;
            if (tLRPC$TL_paymentSavedCredentialsCard != null) {
                this.cardName = tLRPC$TL_paymentSavedCredentialsCard.title;
                return;
            }
            return;
        }
        this.cardName = str3;
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
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: org.telegram.ui.PaymentFormActivity} */
    /* JADX WARNING: type inference failed for: r12v5, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r12v6 */
    /* JADX WARNING: type inference failed for: r12v10 */
    /* JADX WARNING: type inference failed for: r12v12 */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:309|310|311|312|313) */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x1006, code lost:
        if (r6.email_requested == false) goto L_0x0ff7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0331, code lost:
        if (r11.email_requested == false) goto L_0x0322;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Missing exception handler attribute for start block: B:312:0x0b02 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x1399  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x141b  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x142f  */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x144d  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x1476  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x14a3  */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x14a5  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x14d0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x150a  */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r33) {
        /*
            r32 = this;
            r7 = r32
            r8 = r33
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
            goto L_0x00c6
        L_0x000b:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627386(0x7f0e0d7a, float:1.8882035E38)
            java.lang.String r2 = "PaymentPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c6
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
            r2 = 2131627397(0x7f0e0d85, float:1.8882057E38)
            java.lang.String r3 = "PaymentReceipt"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00c6
        L_0x0044:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627397(0x7f0e0d85, float:1.8882057E38)
            java.lang.String r2 = "PaymentReceipt"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c6
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
            r2 = 2131627367(0x7f0e0d67, float:1.8881996E38)
            java.lang.String r3 = "PaymentCheckout"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00c6
        L_0x007c:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627367(0x7f0e0d67, float:1.8881996E38)
            java.lang.String r2 = "PaymentCheckout"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c6
        L_0x008b:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627360(0x7f0e0d60, float:1.8881982E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c6
        L_0x009a:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627360(0x7f0e0d60, float:1.8881982E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c6
        L_0x00a9:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627405(0x7f0e0d8d, float:1.8882074E38)
            java.lang.String r2 = "PaymentShippingMethod"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00c6
        L_0x00b8:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627404(0x7f0e0d8c, float:1.8882071E38)
            java.lang.String r2 = "PaymentShippingInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x00c6:
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
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            int r1 = r7.currentStep
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = 6
            r12 = 4
            r13 = 3
            r14 = 2
            r15 = -1
            if (r1 == 0) goto L_0x00fa
            if (r1 == r9) goto L_0x00fa
            if (r1 == r14) goto L_0x00fa
            if (r1 == r13) goto L_0x00fa
            if (r1 == r12) goto L_0x00fa
            if (r1 == r11) goto L_0x00fa
            goto L_0x013a
        L_0x00fa:
            r1 = 2131165450(0x7var_a, float:1.7945117E38)
            r2 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 2131625518(0x7f0e062e, float:1.8878246E38)
            java.lang.String r4 = "Done"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItemWithWidth((int) r9, (int) r1, (int) r2, (java.lang.CharSequence) r3)
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
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r10)
            r0.addView(r1, r2)
        L_0x013a:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.fragmentView = r0
            r6 = r0
            android.widget.FrameLayout r6 = (android.widget.FrameLayout) r6
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
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            int r1 = r7.currentStep
            if (r1 != r12) goto L_0x0179
            r1 = 1111490560(0x42400000, float:48.0)
            r22 = 1111490560(0x42400000, float:48.0)
            goto L_0x017c
        L_0x0179:
            r1 = 0
            r22 = 0
        L_0x017c:
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r6.addView(r0, r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.linearLayout2 = r0
            r0.setOrientation(r9)
            android.widget.LinearLayout r0 = r7.linearLayout2
            r5 = 0
            r0.setClipChildren(r5)
            android.widget.ScrollView r0 = r7.scrollView
            android.widget.LinearLayout r1 = r7.linearLayout2
            android.widget.FrameLayout$LayoutParams r2 = new android.widget.FrameLayout$LayoutParams
            r4 = -2
            r2.<init>(r15, r4)
            r0.addView(r1, r2)
            int r0 = r7.currentStep
            java.lang.String r3 = ""
            java.lang.String r2 = "windowBackgroundWhiteBlackText"
            java.lang.String r1 = "windowBackgroundGrayShadow"
            java.lang.String r10 = "windowBackgroundWhite"
            if (r0 != 0) goto L_0x0962
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0215 }
            java.io.InputStreamReader r11 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0215 }
            android.content.res.Resources r16 = r33.getResources()     // Catch:{ Exception -> 0x0215 }
            android.content.res.AssetManager r4 = r16.getAssets()     // Catch:{ Exception -> 0x0215 }
            java.lang.String r15 = "countries.txt"
            java.io.InputStream r4 = r4.open(r15)     // Catch:{ Exception -> 0x0215 }
            r11.<init>(r4)     // Catch:{ Exception -> 0x0215 }
            r0.<init>(r11)     // Catch:{ Exception -> 0x0215 }
        L_0x01ce:
            java.lang.String r4 = r0.readLine()     // Catch:{ Exception -> 0x0215 }
            if (r4 == 0) goto L_0x0211
            java.lang.String r11 = ";"
            java.lang.String[] r4 = r4.split(r11)     // Catch:{ Exception -> 0x0215 }
            java.util.ArrayList<java.lang.String> r11 = r7.countriesArray     // Catch:{ Exception -> 0x0215 }
            r15 = r4[r14]     // Catch:{ Exception -> 0x0215 }
            r11.add(r5, r15)     // Catch:{ Exception -> 0x0215 }
            java.util.HashMap<java.lang.String, java.lang.String> r11 = r7.countriesMap     // Catch:{ Exception -> 0x0215 }
            r15 = r4[r14]     // Catch:{ Exception -> 0x0215 }
            r13 = r4[r5]     // Catch:{ Exception -> 0x0215 }
            r11.put(r15, r13)     // Catch:{ Exception -> 0x0215 }
            java.util.HashMap<java.lang.String, java.lang.String> r11 = r7.codesMap     // Catch:{ Exception -> 0x0215 }
            r13 = r4[r5]     // Catch:{ Exception -> 0x0215 }
            r15 = r4[r14]     // Catch:{ Exception -> 0x0215 }
            r11.put(r13, r15)     // Catch:{ Exception -> 0x0215 }
            r11 = r4[r9]     // Catch:{ Exception -> 0x0215 }
            r13 = r4[r14]     // Catch:{ Exception -> 0x0215 }
            r12.put(r11, r13)     // Catch:{ Exception -> 0x0215 }
            int r11 = r4.length     // Catch:{ Exception -> 0x0215 }
            r13 = 3
            if (r11 <= r13) goto L_0x0207
            java.util.HashMap<java.lang.String, java.lang.String> r11 = r7.phoneFormatMap     // Catch:{ Exception -> 0x0215 }
            r15 = r4[r5]     // Catch:{ Exception -> 0x0215 }
            r5 = r4[r13]     // Catch:{ Exception -> 0x0215 }
            r11.put(r15, r5)     // Catch:{ Exception -> 0x0215 }
        L_0x0207:
            r5 = r4[r9]     // Catch:{ Exception -> 0x0215 }
            r4 = r4[r14]     // Catch:{ Exception -> 0x0215 }
            r6.put(r5, r4)     // Catch:{ Exception -> 0x0215 }
            r5 = 0
            r13 = 3
            goto L_0x01ce
        L_0x0211:
            r0.close()     // Catch:{ Exception -> 0x0215 }
            goto L_0x0219
        L_0x0215:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0219:
            java.util.ArrayList<java.lang.String> r0 = r7.countriesArray
            org.telegram.ui.CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0 r4 = org.telegram.ui.CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE
            java.util.Collections.sort(r0, r4)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r7.inputFields = r0
            r0 = 0
        L_0x0227:
            r4 = 10
            if (r0 >= r4) goto L_0x0799
            if (r0 != 0) goto L_0x0267
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r11 = 0
            r4[r11] = r5
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r11]
            int r5 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r5)
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r11]
            r5 = 2131627398(0x7f0e0d86, float:1.888206E38)
            java.lang.String r13 = "PaymentShippingAddress"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
            r4.setText(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r5 = r7.headerCell
            r5 = r5[r11]
            r13 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r13)
            r4.addView(r5, r14)
            r4 = 8
            r13 = -1
            goto L_0x02be
        L_0x0267:
            r4 = 6
            r11 = 0
            if (r0 != r4) goto L_0x02bb
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r13)
            r4[r11] = r5
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            r5 = r5[r11]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r4.addView(r5, r14)
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r4[r9] = r5
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r9]
            int r5 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r5)
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r9]
            r5 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r11 = "PaymentShippingReceiver"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.setText(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r5 = r7.headerCell
            r5 = r5[r9]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r4.addView(r5, r14)
            goto L_0x02bc
        L_0x02bb:
            r13 = -1
        L_0x02bc:
            r4 = 8
        L_0x02be:
            if (r0 != r4) goto L_0x02e2
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r8)
            r5 = 0
            r4.setClipChildren(r5)
            r4.setOrientation(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r11 = 50
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r5.addView(r4, r14)
            int r5 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r5)
        L_0x02de:
            r5 = 9
            goto L_0x0353
        L_0x02e2:
            r4 = 9
            if (r0 != r4) goto L_0x02f3
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r5 = 8
            r4 = r4[r5]
            android.view.ViewParent r4 = r4.getParent()
            android.view.ViewGroup r4 = (android.view.ViewGroup) r4
            goto L_0x02de
        L_0x02f3:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r8)
            r5 = 0
            r4.setClipChildren(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r11 = 50
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r5.addView(r4, r14)
            int r5 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r5)
            r5 = 5
            if (r0 == r5) goto L_0x0314
            r5 = 1
            goto L_0x0315
        L_0x0314:
            r5 = 0
        L_0x0315:
            if (r5 == 0) goto L_0x0334
            r11 = 7
            if (r0 != r11) goto L_0x0324
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r11 = r11.invoice
            boolean r11 = r11.phone_requested
            if (r11 != 0) goto L_0x0324
        L_0x0322:
            r5 = 0
            goto L_0x0334
        L_0x0324:
            r11 = 6
            if (r0 != r11) goto L_0x0334
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r11 = r11.invoice
            boolean r13 = r11.phone_requested
            if (r13 != 0) goto L_0x0334
            boolean r11 = r11.email_requested
            if (r11 != 0) goto L_0x0334
            goto L_0x0322
        L_0x0334:
            if (r5 == 0) goto L_0x02de
            org.telegram.ui.PaymentFormActivity$2 r5 = new org.telegram.ui.PaymentFormActivity$2
            r5.<init>(r7, r8)
            int r11 = r7.getThemedColor(r10)
            r5.setBackgroundColor(r11)
            java.util.ArrayList<android.view.View> r11 = r7.dividers
            r11.add(r5)
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            r13 = 83
            r14 = -1
            r11.<init>(r14, r9, r13)
            r4.addView(r5, r11)
            goto L_0x02de
        L_0x0353:
            if (r0 != r5) goto L_0x035f
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            org.telegram.ui.Components.HintEditText r11 = new org.telegram.ui.Components.HintEditText
            r11.<init>(r8)
            r5[r0] = r11
            goto L_0x0368
        L_0x035f:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r11 = new org.telegram.ui.Components.EditTextBoldCursor
            r11.<init>(r8)
            r5[r0] = r11
        L_0x0368:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r5.setTag(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 1098907648(0x41800000, float:16.0)
            r5.setTextSize(r9, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            java.lang.String r11 = "windowBackgroundWhiteHintText"
            int r11 = r7.getThemedColor(r11)
            r5.setHintTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            int r11 = r7.getThemedColor(r2)
            r5.setTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 0
            r5.setBackgroundDrawable(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            int r11 = r7.getThemedColor(r2)
            r5.setCursorColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 1101004800(0x41a00000, float:20.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r5.setCursorSize(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 1069547520(0x3fCLASSNAME, float:1.5)
            r5.setCursorWidth(r11)
            r5 = 4
            if (r0 != r5) goto L_0x03d4
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda22 r11 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda22
            r11.<init>(r7)
            r5.setOnTouchListener(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 0
            r5.setInputType(r11)
        L_0x03d4:
            r5 = 9
            if (r0 == r5) goto L_0x03f2
            r5 = 8
            if (r0 != r5) goto L_0x03dd
            goto L_0x03f2
        L_0x03dd:
            r5 = 7
            if (r0 != r5) goto L_0x03e8
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r5.setInputType(r9)
            goto L_0x03fa
        L_0x03e8:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 16385(0x4001, float:2.296E-41)
            r5.setInputType(r11)
            goto L_0x03fa
        L_0x03f2:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 3
            r5.setInputType(r11)
        L_0x03fa:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 268435461(0x10000005, float:2.5243564E-29)
            r5.setImeOptions(r11)
            switch(r0) {
                case 0: goto L_0x0519;
                case 1: goto L_0x04f5;
                case 2: goto L_0x04d1;
                case 3: goto L_0x04ad;
                case 4: goto L_0x0474;
                case 5: goto L_0x044f;
                case 6: goto L_0x042c;
                case 7: goto L_0x0409;
                default: goto L_0x0407;
            }
        L_0x0407:
            goto L_0x053c
        L_0x0409:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627403(0x7f0e0d8b, float:1.888207E38)
            java.lang.String r13 = "PaymentShippingEmailPlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            java.lang.String r5 = r5.email
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            r11.setText(r5)
            goto L_0x053c
        L_0x042c:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627406(0x7f0e0d8e, float:1.8882076E38)
            java.lang.String r13 = "PaymentShippingName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            java.lang.String r5 = r5.name
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            r11.setText(r5)
            goto L_0x053c
        L_0x044f:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627412(0x7f0e0d94, float:1.8882088E38)
            java.lang.String r13 = "PaymentShippingZipPlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r5 = r5.post_code
            r11.setText(r5)
            goto L_0x053c
        L_0x0474:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627402(0x7f0e0d8a, float:1.8882067E38)
            java.lang.String r13 = "PaymentShippingCountry"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x053c
            java.lang.String r5 = r5.country_iso2
            java.lang.Object r5 = r12.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r11 = r11.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r11 = r11.shipping_address
            java.lang.String r11 = r11.country_iso2
            r7.countryName = r11
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r7.inputFields
            r13 = r13[r0]
            if (r5 == 0) goto L_0x04a7
            goto L_0x04a8
        L_0x04a7:
            r5 = r11
        L_0x04a8:
            r13.setText(r5)
            goto L_0x053c
        L_0x04ad:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627411(0x7f0e0d93, float:1.8882086E38)
            java.lang.String r13 = "PaymentShippingStatePlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r5 = r5.state
            r11.setText(r5)
            goto L_0x053c
        L_0x04d1:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627401(0x7f0e0d89, float:1.8882065E38)
            java.lang.String r13 = "PaymentShippingCityPlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r5 = r5.city
            r11.setText(r5)
            goto L_0x053c
        L_0x04f5:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627400(0x7f0e0d88, float:1.8882063E38)
            java.lang.String r13 = "PaymentShippingAddress2Placeholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r5 = r5.street_line2
            r11.setText(r5)
            goto L_0x053c
        L_0x0519:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 2131627399(0x7f0e0d87, float:1.8882061E38)
            java.lang.String r13 = "PaymentShippingAddress1Placeholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r5.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.saved_info
            if (r5 == 0) goto L_0x053c
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 == 0) goto L_0x053c
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r5 = r5.street_line1
            r11.setText(r5)
        L_0x053c:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r11 = r5[r0]
            r5 = r5[r0]
            int r5 = r5.length()
            r11.setSelection(r5)
            r5 = 8
            if (r0 != r5) goto L_0x05c4
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r8)
            r7.textView = r5
            java.lang.String r11 = "+"
            r5.setText(r11)
            android.widget.TextView r5 = r7.textView
            int r11 = r7.getThemedColor(r2)
            r5.setTextColor(r11)
            android.widget.TextView r5 = r7.textView
            r11 = 1098907648(0x41800000, float:16.0)
            r5.setTextSize(r9, r11)
            android.widget.TextView r5 = r7.textView
            r23 = -2
            r24 = -2
            r25 = 1101529088(0x41a80000, float:21.0)
            r26 = 1094713344(0x41400000, float:12.0)
            r27 = 0
            r28 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r23, r24, r25, r26, r27, r28)
            r4.addView(r5, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r13 = 0
            r5.setPadding(r11, r13, r13, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 19
            r5.setGravity(r11)
            android.text.InputFilter[] r5 = new android.text.InputFilter[r9]
            android.text.InputFilter$LengthFilter r11 = new android.text.InputFilter$LengthFilter
            r14 = 5
            r11.<init>(r14)
            r5[r13] = r11
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            r11.setFilters(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r23 = 55
            r25 = 0
            r27 = 1101529088(0x41a80000, float:21.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r23, r24, r25, r26, r27, r28)
            r4.addView(r5, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$3 r5 = new org.telegram.ui.PaymentFormActivity$3
            r5.<init>()
            r4.addTextChangedListener(r5)
            goto L_0x0632
        L_0x05c4:
            r5 = 9
            if (r0 != r5) goto L_0x05fd
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 0
            r5.setPadding(r11, r11, r11, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 19
            r5.setGravity(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r23 = -1
            r24 = -2
            r25 = 0
            r26 = 1094713344(0x41400000, float:12.0)
            r27 = 1101529088(0x41a80000, float:21.0)
            r28 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r23, r24, r25, r26, r27, r28)
            r4.addView(r5, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$4 r5 = new org.telegram.ui.PaymentFormActivity$4
            r5.<init>()
            r4.addTextChangedListener(r5)
            goto L_0x0632
        L_0x05fd:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r13 = 0
            r5.setPadding(r13, r13, r13, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x0615
            r11 = 5
            goto L_0x0616
        L_0x0615:
            r11 = 3
        L_0x0616:
            r5.setGravity(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r23 = -1
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1101529088(0x41a80000, float:21.0)
            r27 = 1094713344(0x41400000, float:12.0)
            r28 = 1101529088(0x41a80000, float:21.0)
            r29 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r4.addView(r5, r11)
        L_0x0632:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda25 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda25
            r5.<init>(r7)
            r4.setOnEditorActionListener(r5)
            r4 = 9
            if (r0 != r4) goto L_0x078f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r5 = r4.email_to_provider
            if (r5 != 0) goto L_0x066e
            boolean r4 = r4.phone_to_provider
            if (r4 == 0) goto L_0x064f
            goto L_0x066e
        L_0x064f:
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r4[r9] = r5
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            r5 = r5[r9]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r4.addView(r5, r14)
            r24 = r10
            r10 = 1
            goto L_0x071d
        L_0x066e:
            r4 = 0
            r5 = 0
        L_0x0670:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            int r11 = r11.size()
            if (r5 >= r11) goto L_0x0697
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            java.lang.Object r11 = r11.get(r5)
            org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
            long r13 = r11.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            r24 = r10
            long r9 = r15.provider_id
            int r15 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r15 != 0) goto L_0x0691
            r4 = r11
        L_0x0691:
            int r5 = r5 + 1
            r10 = r24
            r9 = 1
            goto L_0x0670
        L_0x0697:
            r24 = r10
            if (r4 == 0) goto L_0x06a4
            java.lang.String r5 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r5, r4)
            goto L_0x06a5
        L_0x06a4:
            r4 = r3
        L_0x06a5:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r10 = r7.resourcesProvider
            r9.<init>(r8, r10)
            r10 = 1
            r5[r10] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r10]
            r9 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r1)
            r5.setBackgroundDrawable(r11)
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r10]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r5.addView(r9, r14)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            boolean r9 = r5.email_to_provider
            if (r9 == 0) goto L_0x06f0
            boolean r5 = r5.phone_to_provider
            if (r5 == 0) goto L_0x06f0
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r10]
            r9 = 2131627394(0x7f0e0d82, float:1.8882051E38)
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r13 = 0
            r11[r13] = r4
            java.lang.String r4 = "PaymentPhoneEmailToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r9, r11)
            r5.setText(r4)
            goto L_0x071d
        L_0x06f0:
            if (r9 == 0) goto L_0x0708
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r10]
            r9 = 2131627380(0x7f0e0d74, float:1.8882023E38)
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r13 = 0
            r11[r13] = r4
            java.lang.String r4 = "PaymentEmailToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r9, r11)
            r5.setText(r4)
            goto L_0x071d
        L_0x0708:
            r13 = 0
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r10]
            r9 = 2131627395(0x7f0e0d83, float:1.8882053E38)
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r11[r13] = r4
            java.lang.String r4 = "PaymentPhoneToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r9, r11)
            r5.setText(r4)
        L_0x071d:
            org.telegram.ui.Cells.TextCheckCell r4 = new org.telegram.ui.Cells.TextCheckCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r7.checkCell1 = r4
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r4.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            r5 = 2131627409(0x7f0e0d91, float:1.8882082E38)
            java.lang.String r9 = "PaymentShippingSave"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            boolean r9 = r7.saveShippingInfo
            r10 = 0
            r4.setTextAndCheck(r5, r9, r10)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r5 = r7.checkCell1
            r9 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r4.addView(r5, r11)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9
            r5.<init>(r7)
            r4.setOnClickListener(r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r5.<init>(r8, r9)
            r9 = 0
            r4[r9] = r5
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r9]
            r5 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r5, (java.lang.String) r1)
            r4.setBackgroundDrawable(r10)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r9]
            r5 = 2131627410(0x7f0e0d92, float:1.8882084E38)
            java.lang.String r10 = "PaymentShippingSaveInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
            r4.setText(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r9]
            r9 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r4.addView(r5, r11)
            goto L_0x0791
        L_0x078f:
            r24 = r10
        L_0x0791:
            int r0 = r0 + 1
            r10 = r24
            r9 = 1
            r14 = 2
            goto L_0x0227
        L_0x0799:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x07b2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 6
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
            goto L_0x07b4
        L_0x07b2:
            r1 = 8
        L_0x07b4:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x07c9
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x07c9:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x07df
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x07df:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r1 = r0.phone_requested
            if (r1 == 0) goto L_0x07f4
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r9 = 9
            r0 = r0[r9]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x0819
        L_0x07f4:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            boolean r2 = r0.email_requested
            if (r2 == 0) goto L_0x0804
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x0819
        L_0x0804:
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x0811
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 6
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x0819
        L_0x0811:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 5
            r0 = r0[r2]
            r0.setImeOptions(r1)
        L_0x0819:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = 1
            r2 = r0[r1]
            if (r2 == 0) goto L_0x083b
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0836
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0836
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0833
            goto L_0x0836
        L_0x0833:
            r1 = 8
            goto L_0x0837
        L_0x0836:
            r1 = 0
        L_0x0837:
            r0.setVisibility(r1)
            goto L_0x085c
        L_0x083b:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r1 = 1
            r2 = r0[r1]
            if (r2 == 0) goto L_0x085c
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0858
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0858
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0855
            goto L_0x0858
        L_0x0855:
            r1 = 8
            goto L_0x0859
        L_0x0858:
            r1 = 0
        L_0x0859:
            r0.setVisibility(r1)
        L_0x085c:
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 1
            r0 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0875
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0875
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0872
            goto L_0x0875
        L_0x0872:
            r1 = 8
            goto L_0x0876
        L_0x0875:
            r1 = 0
        L_0x0876:
            r0.setVisibility(r1)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x08e5
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
        L_0x08e5:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08fd
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08fd
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r7.fillNumber(r0)
            goto L_0x0901
        L_0x08fd:
            r1 = 0
            r7.fillNumber(r1)
        L_0x0901:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 8
            r0 = r0[r1]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x1e55
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r0.invoice
            boolean r1 = r1.phone_requested
            if (r1 == 0) goto L_0x1e55
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x0921
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x1e55
        L_0x0921:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0937 }
            java.lang.String r1 = "phone"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ Exception -> 0x0937 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0937 }
            if (r0 == 0) goto L_0x093b
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0937 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x0937 }
            r2 = r0
            goto L_0x093c
        L_0x0937:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x093b:
            r2 = 0
        L_0x093c:
            if (r2 == 0) goto L_0x1e55
            java.lang.Object r0 = r6.get(r2)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x1e55
            java.util.ArrayList<java.lang.String> r1 = r7.countriesArray
            int r1 = r1.indexOf(r0)
            r2 = -1
            if (r1 == r2) goto L_0x1e55
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r2 = 8
            r1 = r1[r2]
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r7.countriesMap
            java.lang.Object r0 = r2.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r1.setText(r0)
            goto L_0x1e55
        L_0x0962:
            r24 = r10
            r4 = 2
            r9 = 9
            if (r0 != r4) goto L_0x0eda
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x099d
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0999 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm     // Catch:{ Exception -> 0x0999 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r4 = r4.native_params     // Catch:{ Exception -> 0x0999 }
            java.lang.String r4 = r4.data     // Catch:{ Exception -> 0x0999 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x0999 }
            java.lang.String r4 = "google_pay_public_key"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ Exception -> 0x0999 }
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0999 }
            if (r5 != 0) goto L_0x0988
            r7.googlePayPublicKey = r4     // Catch:{ Exception -> 0x0999 }
        L_0x0988:
            java.lang.String r4 = "acquirer_bank_country"
            java.lang.String r4 = r0.optString(r4)     // Catch:{ Exception -> 0x0999 }
            r7.googlePayCountryCode = r4     // Catch:{ Exception -> 0x0999 }
            java.lang.String r4 = "gpay_parameters"
            org.json.JSONObject r0 = r0.optJSONObject(r4)     // Catch:{ Exception -> 0x0999 }
            r7.googlePayParameters = r0     // Catch:{ Exception -> 0x0999 }
            goto L_0x099d
        L_0x0999:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x099d:
            boolean r0 = r7.isWebView
            if (r0 == 0) goto L_0x0ab3
            java.lang.String r0 = r7.googlePayPublicKey
            if (r0 != 0) goto L_0x09a9
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x09ac
        L_0x09a9:
            r32.initGooglePay(r33)
        L_0x09ac:
            r32.createGooglePayButton(r33)
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
            r3 = 21
            if (r0 < r3) goto L_0x0a09
            android.webkit.WebView r3 = r7.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r4 = 0
            r3.setMixedContentMode(r4)
            android.webkit.CookieManager r3 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r4 = r7.webView
            r3.setAcceptThirdPartyCookies(r4, r2)
        L_0x0a09:
            r2 = 17
            if (r0 < r2) goto L_0x0a1a
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r2 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r3 = 0
            r2.<init>()
            java.lang.String r3 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r2, r3)
        L_0x0a1a:
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
            r2 = 2131627363(0x7f0e0d63, float:1.8881988E38)
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
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r2 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r7.resourcesProvider
            r2.<init>(r8, r3)
            r3 = 0
            r0[r3] = r2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r3]
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r1)
            r0.setBackgroundDrawable(r1)
            r32.updateSavePaymentField()
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            goto L_0x1e55
        L_0x0ab3:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0b17
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0b13 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm     // Catch:{ Exception -> 0x0b13 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r4 = r4.native_params     // Catch:{ Exception -> 0x0b13 }
            java.lang.String r4 = r4.data     // Catch:{ Exception -> 0x0b13 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x0b13 }
            java.lang.String r4 = "need_country"
            boolean r4 = r0.getBoolean(r4)     // Catch:{ Exception -> 0x0acd }
            r7.need_card_country = r4     // Catch:{ Exception -> 0x0acd }
            goto L_0x0ad0
        L_0x0acd:
            r4 = 0
            r7.need_card_country = r4     // Catch:{ Exception -> 0x0b13 }
        L_0x0ad0:
            java.lang.String r4 = "need_zip"
            boolean r4 = r0.getBoolean(r4)     // Catch:{ Exception -> 0x0ad9 }
            r7.need_card_postcode = r4     // Catch:{ Exception -> 0x0ad9 }
            goto L_0x0adc
        L_0x0ad9:
            r4 = 0
            r7.need_card_postcode = r4     // Catch:{ Exception -> 0x0b13 }
        L_0x0adc:
            java.lang.String r4 = "need_cardholder_name"
            boolean r4 = r0.getBoolean(r4)     // Catch:{ Exception -> 0x0ae5 }
            r7.need_card_name = r4     // Catch:{ Exception -> 0x0ae5 }
            goto L_0x0ae8
        L_0x0ae5:
            r4 = 0
            r7.need_card_name = r4     // Catch:{ Exception -> 0x0b13 }
        L_0x0ae8:
            java.lang.String r4 = "public_token"
            boolean r4 = r0.has(r4)     // Catch:{ Exception -> 0x0b13 }
            if (r4 == 0) goto L_0x0af9
            java.lang.String r3 = "public_token"
            java.lang.String r3 = r0.getString(r3)     // Catch:{ Exception -> 0x0b13 }
            r7.providerApiKey = r3     // Catch:{ Exception -> 0x0b13 }
            goto L_0x0b04
        L_0x0af9:
            java.lang.String r4 = "publishable_key"
            java.lang.String r4 = r0.getString(r4)     // Catch:{ Exception -> 0x0b02 }
            r7.providerApiKey = r4     // Catch:{ Exception -> 0x0b02 }
            goto L_0x0b04
        L_0x0b02:
            r7.providerApiKey = r3     // Catch:{ Exception -> 0x0b13 }
        L_0x0b04:
            java.lang.String r3 = "google_pay_hidden"
            r4 = 0
            boolean r0 = r0.optBoolean(r3, r4)     // Catch:{ Exception -> 0x0b13 }
            if (r0 != 0) goto L_0x0b0f
            r0 = 1
            goto L_0x0b10
        L_0x0b0f:
            r0 = 0
        L_0x0b10:
            r7.initGooglePay = r0     // Catch:{ Exception -> 0x0b13 }
            goto L_0x0b17
        L_0x0b13:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0b17:
            boolean r0 = r7.initGooglePay
            if (r0 == 0) goto L_0x0b36
            java.lang.String r0 = r7.providerApiKey
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b2f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.lang.String r0 = r0.native_provider
            java.lang.String r3 = "stripe"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x0b33
        L_0x0b2f:
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0b36
        L_0x0b33:
            r32.initGooglePay(r33)
        L_0x0b36:
            r3 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r7.inputFields = r0
            r0 = 0
        L_0x0b3c:
            if (r0 >= r3) goto L_0x0ea2
            if (r0 != 0) goto L_0x0b79
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r10 = r24
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = 2131627366(0x7f0e0d66, float:1.8881994E38)
            java.lang.String r6 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r4, r9)
            goto L_0x0bb4
        L_0x0b79:
            r10 = r24
            r3 = 4
            if (r0 != r3) goto L_0x0bb4
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 1
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = 2131627357(0x7f0e0d5d, float:1.8881976E38)
            java.lang.String r6 = "PaymentBillingAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r4, r9)
        L_0x0bb4:
            r3 = 3
            if (r0 == r3) goto L_0x0bc3
            r3 = 5
            if (r0 == r3) goto L_0x0bc3
            r3 = 4
            if (r0 != r3) goto L_0x0bc1
            boolean r3 = r7.need_card_postcode
            if (r3 == 0) goto L_0x0bc3
        L_0x0bc1:
            r3 = 1
            goto L_0x0bc4
        L_0x0bc3:
            r3 = 0
        L_0x0bc4:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r8)
            r5 = 0
            r4.setClipChildren(r5)
            int r5 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r5)
            android.widget.LinearLayout r5 = r7.linearLayout2
            r6 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r5.addView(r4, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r5[r0] = r6
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
            int r6 = r7.getThemedColor(r2)
            r5.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 0
            r5.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            int r6 = r7.getThemedColor(r2)
            r5.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r5.setCursorWidth(r6)
            r5 = 3
            if (r0 != r5) goto L_0x0CLASSNAME
            r6 = 1
            android.text.InputFilter[] r9 = new android.text.InputFilter[r6]
            android.text.InputFilter$LengthFilter r6 = new android.text.InputFilter$LengthFilter
            r6.<init>(r5)
            r5 = 0
            r9[r5] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r5.setFilters(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 130(0x82, float:1.82E-43)
            r5.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r5.setTypeface(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            android.text.method.PasswordTransformationMethod r6 = android.text.method.PasswordTransformationMethod.getInstance()
            r5.setTransformationMethod(r6)
            goto L_0x0cb8
        L_0x0CLASSNAME:
            if (r0 != 0) goto L_0x0c7d
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 3
            r5.setInputType(r6)
            goto L_0x0cb8
        L_0x0c7d:
            r5 = 4
            if (r0 != r5) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda23 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda23
            r6.<init>(r7)
            r5.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 0
            r5.setInputType(r6)
            goto L_0x0cb8
        L_0x0CLASSNAME:
            r5 = 1
            if (r0 != r5) goto L_0x0ca2
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 16386(0x4002, float:2.2962E-41)
            r5.setInputType(r6)
            goto L_0x0cb8
        L_0x0ca2:
            r5 = 2
            if (r0 != r5) goto L_0x0caf
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 4097(0x1001, float:5.741E-42)
            r5.setInputType(r6)
            goto L_0x0cb8
        L_0x0caf:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 16385(0x4001, float:2.296E-41)
            r5.setInputType(r6)
        L_0x0cb8:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r5.setImeOptions(r6)
            if (r0 == 0) goto L_0x0d29
            r5 = 1
            if (r0 == r5) goto L_0x0d18
            r5 = 2
            if (r0 == r5) goto L_0x0d07
            r5 = 3
            if (r0 == r5) goto L_0x0cf6
            r5 = 4
            if (r0 == r5) goto L_0x0ce5
            r5 = 5
            if (r0 == r5) goto L_0x0cd4
            goto L_0x0d39
        L_0x0cd4:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627412(0x7f0e0d94, float:1.8882088E38)
            java.lang.String r9 = "PaymentShippingZipPlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            goto L_0x0d39
        L_0x0ce5:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627402(0x7f0e0d8a, float:1.8882067E38)
            java.lang.String r9 = "PaymentShippingCountry"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            goto L_0x0d39
        L_0x0cf6:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627358(0x7f0e0d5e, float:1.8881978E38)
            java.lang.String r9 = "PaymentCardCvv"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            goto L_0x0d39
        L_0x0d07:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627361(0x7f0e0d61, float:1.8881984E38)
            java.lang.String r9 = "PaymentCardName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            goto L_0x0d39
        L_0x0d18:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627359(0x7f0e0d5f, float:1.888198E38)
            java.lang.String r9 = "PaymentCardExpireDate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
            goto L_0x0d39
        L_0x0d29:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 2131627362(0x7f0e0d62, float:1.8881986E38)
            java.lang.String r9 = "PaymentCardNumber"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setHint(r6)
        L_0x0d39:
            if (r0 != 0) goto L_0x0d48
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$7 r6 = new org.telegram.ui.PaymentFormActivity$7
            r6.<init>()
            r5.addTextChangedListener(r6)
            goto L_0x0d57
        L_0x0d48:
            r5 = 1
            if (r0 != r5) goto L_0x0d57
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$8 r6 = new org.telegram.ui.PaymentFormActivity$8
            r6.<init>()
            r5.addTextChangedListener(r6)
        L_0x0d57:
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r5.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0d6f
            r6 = 5
            goto L_0x0d70
        L_0x0d6f:
            r6 = 3
        L_0x0d70:
            r5.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 51
            r27 = 1101529088(0x41a80000, float:21.0)
            r28 = 1094713344(0x41400000, float:12.0)
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r4.addView(r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda29 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda29
            r6.<init>(r7)
            r5.setOnEditorActionListener(r6)
            r5 = 3
            if (r0 != r5) goto L_0x0db8
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r6.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r9 = 0
            r5[r9] = r6
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r9]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r5.addView(r6, r12)
            goto L_0x0e5f
        L_0x0db8:
            r5 = 5
            if (r0 != r5) goto L_0x0e3c
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r6.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)
            r9 = 2
            r5[r9] = r6
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r9]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r5.addView(r6, r12)
            org.telegram.ui.Cells.TextCheckCell r5 = new org.telegram.ui.Cells.TextCheckCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r7.checkCell1 = r5
            r6 = 1
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r5.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextCheckCell r5 = r7.checkCell1
            r6 = 2131627363(0x7f0e0d63, float:1.8881988E38)
            java.lang.String r9 = "PaymentCardSavePaymentInformation"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            boolean r9 = r7.saveCardInfo
            r11 = 0
            r5.setTextAndCheck(r6, r9, r11)
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r6 = r7.checkCell1
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r5.addView(r6, r12)
            org.telegram.ui.Cells.TextCheckCell r5 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18 r6 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18
            r6.<init>(r7)
            r5.setOnClickListener(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r6.<init>(r8, r9)
            r9 = 0
            r5[r9] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r9]
            r6 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r1)
            r5.setBackgroundDrawable(r11)
            r32.updateSavePaymentField()
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r9]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r5.addView(r6, r12)
            goto L_0x0e5f
        L_0x0e3c:
            if (r0 != 0) goto L_0x0e5f
            r32.createGooglePayButton(r33)
            android.widget.FrameLayout r5 = r7.googlePayContainer
            r24 = -2
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0e4d
            r6 = 3
            goto L_0x0e4e
        L_0x0e4d:
            r6 = 5
        L_0x0e4e:
            r26 = r6 | 16
            r27 = 0
            r28 = 0
            r29 = 1082130432(0x40800000, float:4.0)
            r30 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r4.addView(r5, r6)
        L_0x0e5f:
            if (r3 == 0) goto L_0x0e7e
            org.telegram.ui.PaymentFormActivity$9 r3 = new org.telegram.ui.PaymentFormActivity$9
            r3.<init>(r7, r8)
            int r5 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r5)
            java.util.ArrayList<android.view.View> r5 = r7.dividers
            r5.add(r3)
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            r6 = 83
            r9 = -1
            r11 = 1
            r5.<init>(r9, r11, r6)
            r4.addView(r3, r5)
        L_0x0e7e:
            r3 = 4
            if (r0 != r3) goto L_0x0e89
            boolean r3 = r7.need_card_country
            if (r3 == 0) goto L_0x0e86
            goto L_0x0e89
        L_0x0e86:
            r3 = 8
            goto L_0x0e98
        L_0x0e89:
            r3 = 5
            if (r0 != r3) goto L_0x0e90
            boolean r3 = r7.need_card_postcode
            if (r3 == 0) goto L_0x0e86
        L_0x0e90:
            r3 = 2
            if (r0 != r3) goto L_0x0e9b
            boolean r3 = r7.need_card_name
            if (r3 != 0) goto L_0x0e9b
            goto L_0x0e86
        L_0x0e98:
            r4.setVisibility(r3)
        L_0x0e9b:
            int r0 = r0 + 1
            r24 = r10
            r3 = 6
            goto L_0x0b3c
        L_0x0ea2:
            boolean r0 = r7.need_card_country
            if (r0 != 0) goto L_0x0ebc
            boolean r0 = r7.need_card_postcode
            if (r0 != 0) goto L_0x0ebc
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r1 = 1
            r0 = r0[r1]
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r2 = 0
            r0 = r0[r2]
            r0.setVisibility(r1)
        L_0x0ebc:
            boolean r0 = r7.need_card_postcode
            if (r0 == 0) goto L_0x0ecd
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 5
            r0 = r0[r1]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x1e55
        L_0x0ecd:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 3
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x1e55
        L_0x0eda:
            r10 = r24
            r4 = 1
            if (r0 != r4) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r2 = new org.telegram.ui.Cells.RadioCell[r0]
            r7.radioCells = r2
            r2 = 0
        L_0x0eec:
            if (r2 >= r0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r3 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r3 = r3.shipping_options
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$TL_shippingOption r3 = (org.telegram.tgnet.TLRPC$TL_shippingOption) r3
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
            java.lang.String r3 = r3.title
            r9[r5] = r3
            java.lang.String r3 = "%s - %s"
            java.lang.String r3 = java.lang.String.format(r3, r9)
            if (r2 != 0) goto L_0x0var_
            r5 = 1
            goto L_0x0var_
        L_0x0var_:
            r5 = 0
        L_0x0var_:
            int r6 = r0 + -1
            if (r2 == r6) goto L_0x0f3d
            r6 = 1
            goto L_0x0f3e
        L_0x0f3d:
            r6 = 0
        L_0x0f3e:
            r4.setText(r3, r5, r6)
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r2]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda13 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda13
            r4.<init>(r7)
            r3.setOnClickListener(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r4 = r7.radioCells
            r4 = r4[r2]
            r3.addView(r4)
            int r2 = r2 + 1
            goto L_0x0eec
        L_0x0var_:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r2 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r7.resourcesProvider
            r2.<init>(r8, r3)
            r3 = 0
            r0[r3] = r2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r3]
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r2, (java.lang.String) r1)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            goto L_0x1e55
        L_0x0var_:
            r4 = 3
            if (r0 != r4) goto L_0x11d8
            r4 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r4]
            r7.inputFields = r0
            r0 = 0
        L_0x0f8d:
            if (r0 >= r4) goto L_0x1e55
            if (r0 != 0) goto L_0x0fc8
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = 2131627366(0x7f0e0d66, float:1.8881994E38)
            java.lang.String r6 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r3.addView(r4, r11)
            goto L_0x0fca
        L_0x0fc8:
            r5 = 0
            r9 = -1
        L_0x0fca:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r8)
            r3.setClipChildren(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r5 = 50
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r5)
            r4.addView(r3, r6)
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            r4 = 1
            if (r0 == r4) goto L_0x0fe9
            r4 = 1
            goto L_0x0fea
        L_0x0fe9:
            r4 = 0
        L_0x0fea:
            r5 = 7
            if (r4 == 0) goto L_0x1009
            if (r0 != r5) goto L_0x0ff9
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x0ff9
        L_0x0ff7:
            r4 = 0
            goto L_0x1009
        L_0x0ff9:
            r6 = 6
            if (r0 != r6) goto L_0x1009
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r9 = r6.phone_requested
            if (r9 != 0) goto L_0x1009
            boolean r6 = r6.email_requested
            if (r6 != 0) goto L_0x1009
            goto L_0x0ff7
        L_0x1009:
            if (r4 == 0) goto L_0x1028
            org.telegram.ui.PaymentFormActivity$10 r4 = new org.telegram.ui.PaymentFormActivity$10
            r4.<init>(r7, r8)
            int r6 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r4)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r11 = -1
            r12 = 1
            r6.<init>(r11, r12, r9)
            r3.addView(r4, r6)
        L_0x1028:
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
            int r6 = r7.getThemedColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = r7.getThemedColor(r2)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = r7.getThemedColor(r2)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r6)
            if (r0 != 0) goto L_0x109b
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda24 r6 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda24.INSTANCE
            r4.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setInputType(r6)
            goto L_0x10ad
        L_0x109b:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 129(0x81, float:1.81E-43)
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r6)
        L_0x10ad:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r6)
            if (r0 == 0) goto L_0x10d5
            r4 = 1
            if (r0 == r4) goto L_0x10bd
            goto L_0x10e2
        L_0x10bd:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            java.lang.String r9 = "LoginPassword"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.requestFocus()
            goto L_0x10e2
        L_0x10d5:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r6 = r6.saved_credentials
            java.lang.String r6 = r6.title
            r4.setText(r6)
        L_0x10e2:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r4.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x10fa
            r6 = 5
            goto L_0x10fb
        L_0x10fa:
            r6 = 3
        L_0x10fb:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r11 = -1
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 51
            r14 = 1101529088(0x41a80000, float:21.0)
            r15 = 1094713344(0x41400000, float:12.0)
            r16 = 1101529088(0x41a80000, float:21.0)
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r3.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda27 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda27
            r4.<init>(r7)
            r3.setOnEditorActionListener(r4)
            r3 = 1
            if (r0 != r3) goto L_0x11d3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r7.resourcesProvider
            r6.<init>(r8, r9)
            r9 = 0
            r4[r9] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r9]
            r6 = 2131627377(0x7f0e0d71, float:1.8882017E38)
            java.lang.Object[] r11 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r3 = r3.saved_credentials
            java.lang.String r3 = r3.title
            r11[r9] = r3
            java.lang.String r3 = "PaymentConfirmationMessage"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r6, r11)
            r4.setText(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r9]
            r4 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r4, (java.lang.String) r1)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r9]
            r6 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r6)
            r3.addView(r4, r12)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r4 = new org.telegram.ui.Cells.TextSettingsCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r3[r9] = r4
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            r3 = r3[r9]
            r4 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            r3 = r3[r9]
            r4 = 2131627378(0x7f0e0d72, float:1.8882019E38)
            java.lang.String r6 = "PaymentConfirmationNewCard"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4, r9)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r7.settingsCell
            r4 = r4[r9]
            r6 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r6)
            r3.addView(r4, r12)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            r3 = r3[r9]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6
            r4.<init>(r7)
            r3.setOnClickListener(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r4.<init>(r8, r6)
            r6 = 1
            r3[r6] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r6]
            r4 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r4, (java.lang.String) r1)
            r3.setBackgroundDrawable(r9)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r3.addView(r4, r11)
        L_0x11d3:
            int r0 = r0 + 1
            r4 = 2
            goto L_0x0f8d
        L_0x11d8:
            r4 = 4
            if (r0 == r4) goto L_0x1554
            r4 = 5
            if (r0 != r4) goto L_0x11e0
            goto L_0x1554
        L_0x11e0:
            r4 = 6
            if (r0 != r4) goto L_0x1e55
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r8)
            r7.codeFieldCell = r0
            r4 = 2131627343(0x7f0e0d4f, float:1.8881948E38)
            java.lang.String r5 = "PasswordCode"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 0
            r0.setTextAndHint(r3, r4, r5)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            int r3 = r7.getThemedColor(r10)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r3 = 3
            r0.setInputType(r3)
            r3 = 6
            r0.setImeOptions(r3)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda28 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda28
            r3.<init>(r7)
            r0.setOnEditorActionListener(r3)
            org.telegram.ui.PaymentFormActivity$20 r3 = new org.telegram.ui.PaymentFormActivity$20
            r3.<init>()
            r0.addTextChangedListener(r3)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r3 = r7.codeFieldCell
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r7.resourcesProvider
            r3.<init>(r8, r4)
            r4 = 2
            r0[r4] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r4]
            r3 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r1)
            r0.setBackgroundDrawable(r3)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r4]
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r6)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r3 = new org.telegram.ui.Cells.TextSettingsCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r7.resourcesProvider
            r3.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r4 = 1
            r0[r4] = r3
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r4]
            r0.setTag(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r4]
            int r3 = r7.getThemedColor(r2)
            r0.setTextColor(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r4]
            r3 = 2131627965(0x7f0e0fbd, float:1.888321E38)
            java.lang.String r5 = "ResendCode"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r0.setText(r3, r4)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            r3 = r3[r4]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r0.addView(r3, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16
            r3.<init>(r7)
            r0.setOnClickListener(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r3 = new org.telegram.ui.Cells.TextSettingsCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r3.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r0[r5] = r3
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            r0.setTag(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            int r3 = r7.getThemedColor(r3)
            r0.setTextColor(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            r3 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r4 = "AbortPassword"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3, r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            r3 = r3[r5]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r0.addView(r3, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7
            r3.<init>(r7)
            r0.setOnClickListener(r3)
            r3 = 3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r7.inputFields = r0
            r0 = 0
        L_0x1306:
            if (r0 >= r3) goto L_0x154f
            if (r0 != 0) goto L_0x1341
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 0
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = 2131627393(0x7f0e0d81, float:1.888205E38)
            java.lang.String r6 = "PaymentPasswordTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r4, r9)
            goto L_0x137b
        L_0x1341:
            r3 = 2
            if (r0 != r3) goto L_0x137b
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r7.resourcesProvider
            r4.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r5)
            r5 = 1
            r3[r5] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = 2131627389(0x7f0e0d7d, float:1.8882041E38)
            java.lang.String r6 = "PaymentPasswordEmailTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r4, r9)
            goto L_0x137c
        L_0x137b:
            r6 = -1
        L_0x137c:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r8)
            r4 = 0
            r3.setClipChildren(r4)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r5 = 50
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r4.addView(r3, r9)
            int r4 = r7.getThemedColor(r10)
            r3.setBackgroundColor(r4)
            if (r0 != 0) goto L_0x13b6
            org.telegram.ui.PaymentFormActivity$21 r4 = new org.telegram.ui.PaymentFormActivity$21
            r4.<init>(r7, r8)
            int r6 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r4)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r11 = -1
            r12 = 1
            r6.<init>(r11, r12, r9)
            r3.addView(r4, r6)
        L_0x13b6:
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
            int r6 = r7.getThemedColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = r7.getThemedColor(r2)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = r7.getThemedColor(r2)
            r4.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r6)
            if (r0 == 0) goto L_0x142f
            r4 = 1
            if (r0 != r4) goto L_0x141b
            goto L_0x142f
        L_0x141b:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 33
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r6)
            goto L_0x144b
        L_0x142f:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 129(0x81, float:1.81E-43)
            r4.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r6)
        L_0x144b:
            if (r0 == 0) goto L_0x1476
            r4 = 1
            if (r0 == r4) goto L_0x1465
            r4 = 2
            if (r0 == r4) goto L_0x1454
            goto L_0x148d
        L_0x1454:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627387(0x7f0e0d7b, float:1.8882037E38)
            java.lang.String r9 = "PaymentPasswordEmail"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x148d
        L_0x1465:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627392(0x7f0e0d80, float:1.8882047E38)
            java.lang.String r9 = "PaymentPasswordReEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x148d
        L_0x1476:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131627390(0x7f0e0d7e, float:1.8882043E38)
            java.lang.String r9 = "PaymentPasswordEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.requestFocus()
        L_0x148d:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r9 = 0
            r4.setPadding(r9, r9, r9, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x14a5
            r6 = 5
            goto L_0x14a6
        L_0x14a5:
            r6 = 3
        L_0x14a6:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r11 = -1
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 51
            r14 = 1101529088(0x41a80000, float:21.0)
            r15 = 1094713344(0x41400000, float:12.0)
            r16 = 1101529088(0x41a80000, float:21.0)
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r3.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda26 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda26
            r4.<init>(r7)
            r3.setOnEditorActionListener(r4)
            r3 = 1
            if (r0 != r3) goto L_0x150a
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r4.<init>(r8, r6)
            r6 = 0
            r3[r6] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r6]
            r4 = 2131627391(0x7f0e0d7f, float:1.8882045E38)
            java.lang.String r9 = "PaymentPasswordInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r6]
            r4 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r4, (java.lang.String) r1)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r3.addView(r4, r11)
            goto L_0x1547
        L_0x150a:
            r3 = 2
            if (r0 != r3) goto L_0x1547
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r4.<init>(r8, r6)
            r6 = 1
            r3[r6] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r6]
            r4 = 2131627388(0x7f0e0d7c, float:1.888204E38)
            java.lang.String r9 = "PaymentPasswordEmailInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r6]
            r4 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r4, (java.lang.String) r1)
            r3.setBackgroundDrawable(r9)
            android.widget.LinearLayout r3 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r6]
            r6 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r6)
            r3.addView(r9, r12)
            goto L_0x154a
        L_0x1547:
            r4 = 2131165436(0x7var_fc, float:1.794509E38)
        L_0x154a:
            int r0 = r0 + 1
            r3 = 3
            goto L_0x1306
        L_0x154f:
            r32.updatePasswordFields()
            goto L_0x1e55
        L_0x1554:
            r4 = 2131165436(0x7var_fc, float:1.794509E38)
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r8)
            r7.paymentInfoCell = r0
            int r5 = r7.getThemedColor(r10)
            r0.setBackgroundColor(r5)
            org.telegram.messenger.MessageObject r0 = r7.messageObject
            if (r0 == 0) goto L_0x1577
            org.telegram.ui.Cells.PaymentInfoCell r5 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) r0
            java.lang.String r11 = r7.currentBotName
            r5.setInvoice(r0, r11)
            goto L_0x15a2
        L_0x1577:
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r0 = r7.paymentReceipt
            if (r0 == 0) goto L_0x1583
            org.telegram.ui.Cells.PaymentInfoCell r5 = r7.paymentInfoCell
            java.lang.String r11 = r7.currentBotName
            r5.setReceipt(r0, r11)
            goto L_0x15a2
        L_0x1583:
            java.lang.String r0 = r7.invoiceSlug
            if (r0 == 0) goto L_0x15a2
            org.telegram.ui.Cells.PaymentInfoCell r0 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            java.lang.String r11 = r5.title
            java.lang.String r12 = r5.description
            org.telegram.tgnet.TLRPC$WebDocument r13 = r5.photo
            java.lang.String r14 = r7.currentBotName
            r24 = r0
            r25 = r11
            r26 = r12
            r27 = r13
            r28 = r14
            r29 = r5
            r24.setInfo(r25, r26, r27, r28, r29)
        L_0x15a2:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r5 = r7.paymentInfoCell
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r11)
            r0.addView(r5, r13)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r13 = r7.resourcesProvider
            r5.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r13)
            r13 = 0
            r0[r13] = r5
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            r5 = r5[r13]
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r11)
            r0.addView(r5, r13)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r5 = r5.prices
            r0.<init>(r5)
            r7.prices = r0
            org.telegram.tgnet.TLRPC$TL_shippingOption r5 = r7.shippingOption
            if (r5 == 0) goto L_0x15de
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r5 = r5.prices
            r0.addAll(r5)
        L_0x15de:
            r5 = 1
            java.lang.String[] r0 = new java.lang.String[r5]
            r7.totalPrice = r0
            r0 = 0
        L_0x15e4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r5 = r7.prices
            int r5 = r5.size()
            if (r0 >= r5) goto L_0x1621
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r5 = r7.prices
            java.lang.Object r5 = r5.get(r0)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r5 = (org.telegram.tgnet.TLRPC$TL_labeledPrice) r5
            org.telegram.ui.Cells.TextPriceCell r12 = new org.telegram.ui.Cells.TextPriceCell
            r12.<init>(r8)
            int r13 = r7.getThemedColor(r10)
            r12.setBackgroundColor(r13)
            java.lang.String r13 = r5.label
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
            long r4 = r5.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r15 = r15.invoice
            java.lang.String r15 = r15.currency
            java.lang.String r4 = r14.formatCurrencyString(r4, r15)
            r5 = 0
            r12.setTextAndValue(r13, r4, r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r4.addView(r12)
            int r0 = r0 + 1
            r4 = 2131165436(0x7var_fc, float:1.794509E38)
            goto L_0x15e4
        L_0x1621:
            int r0 = r7.currentStep
            r4 = 5
            if (r0 != r4) goto L_0x165c
            java.lang.Long r0 = r7.tipAmount
            if (r0 == 0) goto L_0x165c
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            int r4 = r7.getThemedColor(r10)
            r0.setBackgroundColor(r4)
            r4 = 2131627418(0x7f0e0d9a, float:1.88821E38)
            java.lang.String r5 = "PaymentTip"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.Long r12 = r7.tipAmount
            long r12 = r12.longValue()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r14 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r14 = r14.invoice
            java.lang.String r14 = r14.currency
            java.lang.String r5 = r5.formatCurrencyString(r12, r14)
            r12 = 0
            r0.setTextAndValue(r4, r5, r12)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r4.addView(r0)
        L_0x165c:
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            r7.totalCell = r0
            int r4 = r7.getThemedColor(r10)
            r0.setBackgroundColor(r4)
            java.lang.String[] r0 = r7.totalPrice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r4 = r7.prices
            java.lang.String r4 = r7.getTotalPriceString(r4)
            r5 = 0
            r0[r5] = r4
            org.telegram.ui.Cells.TextPriceCell r0 = r7.totalCell
            r4 = 2131627422(0x7f0e0d9e, float:1.8882108E38)
            java.lang.String r12 = "PaymentTransactionTotal"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            java.lang.String[] r12 = r7.totalPrice
            r12 = r12[r5]
            r5 = 1
            r0.setTextAndValue(r4, r12, r5)
            int r0 = r7.currentStep
            r4 = 4
            if (r0 != r4) goto L_0x1936
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            int r0 = r0.flags
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x1936
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r4 = 0
            r0.setClipChildren(r4)
            int r4 = r7.getThemedColor(r10)
            r0.setBackgroundColor(r4)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            java.util.ArrayList<java.lang.Long> r5 = r5.suggested_tip_amounts
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x16b8
            r5 = 40
            goto L_0x16ba
        L_0x16b8:
            r5 = 78
        L_0x16ba:
            r12 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r5)
            r4.addView(r0, r5)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12
            r4.<init>(r7)
            r0.setOnClickListener(r4)
            org.telegram.ui.Cells.TextPriceCell r4 = new org.telegram.ui.Cells.TextPriceCell
            r4.<init>(r8)
            int r5 = r7.getThemedColor(r10)
            r4.setBackgroundColor(r5)
            r5 = 2131627419(0x7f0e0d9b, float:1.8882102E38)
            java.lang.String r12 = "PaymentTipOptional"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            r12 = 0
            r4.setTextAndValue(r5, r3, r12)
            r0.addView(r4)
            r4 = 1
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = new org.telegram.ui.Components.EditTextBoldCursor[r4]
            r7.inputFields = r5
            org.telegram.ui.Components.EditTextBoldCursor r13 = new org.telegram.ui.Components.EditTextBoldCursor
            r13.<init>(r8)
            r5[r12] = r13
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r12]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
            r5.setTag(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r7.inputFields
            r5 = r5[r12]
            r13 = 1098907648(0x41800000, float:16.0)
            r5.setTextSize(r4, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r12]
            java.lang.String r5 = "windowBackgroundWhiteGrayText2"
            int r5 = r7.getThemedColor(r5)
            r4.setHintTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r12]
            java.lang.String r5 = "windowBackgroundWhiteGrayText2"
            int r5 = r7.getThemedColor(r5)
            r4.setTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r12]
            r5 = 0
            r4.setBackgroundDrawable(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r12]
            int r2 = r7.getThemedColor(r2)
            r4.setCursorColor(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r4 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setCursorSize(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r4 = 1069547520(0x3fCLASSNAME, float:1.5)
            r2.setCursorWidth(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r4 = 3
            r2.setInputType(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r4 = 268435462(0x10000006, float:2.5243567E-29)
            r2.setImeOptions(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            r13 = 0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r15 = r15.invoice
            java.lang.String r15 = r15.currency
            java.lang.String r4 = r4.formatCurrencyString(r13, r15)
            r2.setHint(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r12, r12, r12, r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x1789
            r13 = 3
            goto L_0x178a
        L_0x1789:
            r13 = 5
        L_0x178a:
            r2.setGravity(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 51
            r27 = 1101529088(0x41a80000, float:21.0)
            r28 = 1091567616(0x41100000, float:9.0)
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r2, r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r12 = 0
            r2 = r2[r12]
            org.telegram.ui.PaymentFormActivity$11 r4 = new org.telegram.ui.PaymentFormActivity$11
            r4.<init>()
            r2.addTextChangedListener(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda30 r4 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda30.INSTANCE
            r2.setOnEditorActionListener(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r7.inputFields
            r2 = r2[r12]
            r2.requestFocus()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x192e
            android.widget.HorizontalScrollView r13 = new android.widget.HorizontalScrollView
            r13.<init>(r8)
            r13.setHorizontalScrollBarEnabled(r12)
            r13.setVerticalScrollBarEnabled(r12)
            r13.setClipToPadding(r12)
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = 1101529088(0x41a80000, float:21.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r13.setPadding(r2, r12, r4, r12)
            r2 = 1
            r13.setFillViewport(r2)
            r24 = -1
            r25 = 1106247680(0x41var_, float:30.0)
            r26 = 51
            r27 = 0
            r28 = 1110441984(0x42300000, float:44.0)
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r13, r2)
            r2 = 1
            int[] r0 = new int[r2]
            int[] r14 = new int[r2]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            int r15 = r2.size()
            org.telegram.ui.PaymentFormActivity$12 r4 = new org.telegram.ui.PaymentFormActivity$12
            r2 = r1
            r1 = r4
            r9 = r2
            r18 = r5
            r5 = 2131165436(0x7var_fc, float:1.794509E38)
            r2 = r32
            r19 = r3
            r3 = r33
            r11 = r4
            r12 = -2
            r4 = r15
            r12 = 0
            r5 = r0
            r31 = r6
            r6 = r14
            r1.<init>(r2, r3, r4, r5, r6)
            r7.tipLayout = r11
            r11.setOrientation(r12)
            android.widget.LinearLayout r1 = r7.tipLayout
            r2 = 30
            r3 = 51
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createScroll(r4, r2, r3)
            r13.addView(r1, r2)
            java.lang.String r1 = "contacts_inviteBackground"
            int r1 = r7.getThemedColor(r1)
            r5 = 0
        L_0x1849:
            if (r5 >= r15) goto L_0x193e
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x1864
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            int r3 = r15 - r5
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            goto L_0x1874
        L_0x1864:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            java.lang.Object r2 = r2.get(r5)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x1874:
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            java.lang.String r6 = r6.currency
            java.lang.String r4 = r4.formatCurrencyString(r2, r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r8)
            r11 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r6.setTextSize(r13, r11)
            java.lang.String r11 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r6.setTypeface(r11)
            r6.setLines(r13)
            java.lang.Long r11 = java.lang.Long.valueOf(r2)
            r6.setTag(r11)
            r6.setMaxLines(r13)
            r6.setText(r4)
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r13 = 1097859072(0x41700000, float:15.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r6.setPadding(r11, r12, r13, r12)
            java.lang.String r11 = "chats_secretName"
            int r11 = r7.getThemedColor(r11)
            r6.setTextColor(r11)
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r13 = 536870911(0x1fffffff, float:1.0842021E-19)
            r13 = r13 & r1
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r11, r13)
            r6.setBackground(r11)
            r11 = 1
            r6.setSingleLine(r11)
            r11 = 17
            r6.setGravity(r11)
            android.widget.LinearLayout r11 = r7.tipLayout
            r24 = -2
            r25 = -1
            r26 = 19
            r27 = 0
            r28 = 0
            int r13 = r15 + -1
            if (r5 == r13) goto L_0x18eb
            r29 = 9
            goto L_0x18ed
        L_0x18eb:
            r29 = 0
        L_0x18ed:
            r30 = 0
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
            r11.addView(r6, r13)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20 r11 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20
            r11.<init>(r7, r6, r2)
            r6.setOnClickListener(r11)
            android.text.TextPaint r2 = r6.getPaint()
            float r2 = r2.measureText(r4)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r3 = 1106247680(0x41var_, float:30.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r3 = 2131230952(0x7var_e8, float:1.8077971E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            r6.setTag(r3, r4)
            r3 = r0[r12]
            int r3 = java.lang.Math.max(r3, r2)
            r0[r12] = r3
            r3 = r14[r12]
            int r3 = r3 + r2
            r14[r12] = r3
            int r5 = r5 + 1
            goto L_0x1849
        L_0x192e:
            r9 = r1
            r19 = r3
            r18 = r5
            r31 = r6
            goto L_0x193e
        L_0x1936:
            r9 = r1
            r19 = r3
            r31 = r6
            r12 = 0
            r18 = 0
        L_0x193e:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextPriceCell r1 = r7.totalCell
            r0.addView(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r7.resourcesProvider
            r1.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r2 = 2
            r0[r2] = r1
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r2]
            r1 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r9)
            r0.setBackgroundDrawable(r3)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r7.sectionCell
            r3 = r3[r2]
            r2 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r2)
            r0.addView(r3, r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r0[r12] = r2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r12]
            r2 = 1
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r12]
            java.lang.String r3 = r7.cardName
            if (r3 == 0) goto L_0x19b1
            int r3 = r3.length()
            if (r3 <= r2) goto L_0x19b1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = r7.cardName
            java.lang.String r4 = r4.substring(r12, r2)
            java.lang.String r4 = r4.toUpperCase()
            r3.append(r4)
            java.lang.String r4 = r7.cardName
            java.lang.String r4 = r4.substring(r2)
            r3.append(r4)
            java.lang.String r2 = r3.toString()
            goto L_0x19b3
        L_0x19b1:
            java.lang.String r2 = r7.cardName
        L_0x19b3:
            r3 = 2131627370(0x7f0e0d6a, float:1.8882003E38)
            java.lang.String r4 = "PaymentCheckoutMethod"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 2131165837(0x7var_d, float:1.7945902E38)
            r5 = 1
            r0.setTextAndValueAndIcon(r2, r3, r4, r5)
            boolean r0 = r7.isCheckoutPreview
            if (r0 == 0) goto L_0x19d5
            java.lang.String r0 = r7.cardName
            if (r0 == 0) goto L_0x19d2
            int r0 = r0.length()
            if (r0 <= r5) goto L_0x19d2
            goto L_0x19d5
        L_0x19d2:
            r5 = 8
            goto L_0x19d6
        L_0x19d5:
            r5 = 0
        L_0x19d6:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r12]
            r0.setVisibility(r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r12]
            r0.addView(r2)
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x19f7
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r12]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19
            r2.<init>(r7)
            r0.setOnClickListener(r2)
        L_0x19f7:
            r2 = r18
            r0 = 0
        L_0x19fa:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r3.users
            int r3 = r3.size()
            if (r0 >= r3) goto L_0x1a22
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r3.users
            java.lang.Object r3 = r3.get(r0)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            long r13 = r3.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            r6 = r2
            long r1 = r4.provider_id
            int r4 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r4 != 0) goto L_0x1a1b
            r2 = r3
            goto L_0x1a1c
        L_0x1a1b:
            r2 = r6
        L_0x1a1c:
            int r0 = r0 + 1
            r1 = 2131165436(0x7var_fc, float:1.794509E38)
            goto L_0x19fa
        L_0x1a22:
            r6 = r2
            if (r6 == 0) goto L_0x1a82
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r2 = 1
            r0[r2] = r1
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r2]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r2]
            java.lang.String r1 = r6.first_name
            java.lang.String r2 = r6.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            r1 = 2131627375(0x7f0e0d6f, float:1.8882013E38)
            java.lang.String r2 = "PaymentCheckoutProvider"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131165839(0x7var_f, float:1.7945906E38)
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r4 = r7.validateRequest
            if (r4 == 0) goto L_0x1a60
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.info
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 != 0) goto L_0x1a6a
            org.telegram.tgnet.TLRPC$TL_shippingOption r4 = r7.shippingOption
            if (r4 != 0) goto L_0x1a6a
        L_0x1a60:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r4 = r4.saved_info
            if (r4 == 0) goto L_0x1a6c
            org.telegram.tgnet.TLRPC$TL_postAddress r4 = r4.shipping_address
            if (r4 == 0) goto L_0x1a6c
        L_0x1a6a:
            r4 = 1
            goto L_0x1a6d
        L_0x1a6c:
            r4 = 0
        L_0x1a6d:
            r0.setTextAndValueAndIcon(r3, r1, r2, r4)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r1 = 1
            r0 = r0[r1]
            r0.setVisibility(r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r1]
            r0.addView(r2)
            goto L_0x1a84
        L_0x1a82:
            r3 = r19
        L_0x1a84:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            if (r0 != 0) goto L_0x1a94
            boolean r1 = r7.isCheckoutPreview
            if (r1 == 0) goto L_0x1bd8
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            if (r1 == 0) goto L_0x1bd8
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r1 = r1.saved_info
            if (r1 == 0) goto L_0x1bd8
        L_0x1a94:
            if (r0 == 0) goto L_0x1a99
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            goto L_0x1a9d
        L_0x1a99:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
        L_0x1a9d:
            org.telegram.tgnet.TLRPC$TL_postAddress r1 = r0.shipping_address
            if (r1 == 0) goto L_0x1add
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
            if (r1 != r2) goto L_0x1ad2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1add
        L_0x1ad2:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = r7.getThemedColor(r10)
            r1.setBackgroundColor(r2)
        L_0x1add:
            java.lang.String r1 = r0.name
            if (r1 == 0) goto L_0x1b1d
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
            if (r1 != r2) goto L_0x1b12
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda11
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1b1d
        L_0x1b12:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = r7.getThemedColor(r10)
            r1.setBackgroundColor(r2)
        L_0x1b1d:
            java.lang.String r1 = r0.phone
            if (r1 == 0) goto L_0x1b5c
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
            if (r1 != r4) goto L_0x1b51
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1b5c
        L_0x1b51:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = r7.getThemedColor(r10)
            r1.setBackgroundColor(r2)
        L_0x1b5c:
            java.lang.String r1 = r0.email
            if (r1 == 0) goto L_0x1b9c
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
            if (r1 != r2) goto L_0x1b91
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r2 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r2)
            r1.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            goto L_0x1b9c
        L_0x1b91:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = r7.getThemedColor(r10)
            r1.setBackgroundColor(r2)
        L_0x1b9c:
            org.telegram.tgnet.TLRPC$TL_shippingOption r1 = r7.shippingOption
            if (r1 == 0) goto L_0x1bd5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 6
            r1[r4] = r2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            int r2 = r7.getThemedColor(r10)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = r7.shippingOption
            java.lang.String r2 = r2.title
            r6 = 2131627376(0x7f0e0d70, float:1.8882015E38)
            java.lang.String r11 = "PaymentCheckoutShippingMethod"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r6)
            r11 = 2131165838(0x7var_e, float:1.7945904E38)
            r1.setTextAndValueAndIcon(r2, r6, r11, r12)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r1.addView(r2)
        L_0x1bd5:
            r7.setAddressFields(r0)
        L_0x1bd8:
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1e0f
            boolean r0 = r7.isCheckoutPreview
            r1 = 1
            r0 = r0 ^ r1
            r7.isAcceptTermsChecked = r0
            r7.recurrentAccepted = r0
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r0 = new org.telegram.ui.PaymentFormActivity$BottomFrameLayout
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            r0.<init>(r8, r1)
            r7.bottomLayout = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x1CLASSNAME
            android.view.View r1 = new android.view.View
            r1.<init>(r8)
            java.lang.String r2 = "listSelectorSDK21"
            int r2 = r7.getThemedColor(r2)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r2, (boolean) r12)
            r1.setBackground(r2)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r2 = r7.bottomLayout
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r2.addView(r1, r11)
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r6 = -1
        L_0x1CLASSNAME:
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r1 = r7.bottomLayout
            r2 = 48
            r4 = 80
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r2, (int) r4)
            r4 = r31
            r4.addView(r1, r2)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r1 = r7.bottomLayout
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21 r2 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21
            r2.<init>(r7, r3)
            r1.setOnClickListener(r2)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r7.payTextView = r1
            java.lang.String r2 = "contacts_inviteText"
            int r2 = r7.getThemedColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r7.payTextView
            r2 = 2131627373(0x7f0e0d6d, float:1.8882009E38)
            r3 = 1
            java.lang.Object[] r6 = new java.lang.Object[r3]
            java.lang.String[] r11 = r7.totalPrice
            r11 = r11[r12]
            r6[r12] = r11
            java.lang.String r11 = "PaymentCheckoutPay"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r2, r6)
            r1.setText(r2)
            android.widget.TextView r1 = r7.payTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r3, r2)
            android.widget.TextView r1 = r7.payTextView
            r2 = 17
            r1.setGravity(r2)
            android.widget.TextView r1 = r7.payTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r1 = r7.bottomLayout
            android.widget.TextView r2 = r7.payTextView
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r3)
            r1.addView(r2, r11)
            org.telegram.ui.Components.ContextProgressView r1 = new org.telegram.ui.Components.ContextProgressView
            r1.<init>(r8, r12)
            r7.progressViewButton = r1
            r2 = 4
            r1.setVisibility(r2)
            java.lang.String r1 = "contacts_inviteText"
            int r1 = r7.getThemedColor(r1)
            org.telegram.ui.Components.ContextProgressView r2 = r7.progressViewButton
            r3 = 805306367(0x2fffffff, float:4.6566126E-10)
            r3 = r3 & r1
            r2.setColors(r3, r1)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r1 = r7.bottomLayout
            org.telegram.ui.Components.ContextProgressView r2 = r7.progressViewButton
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r3)
            r1.addView(r2, r11)
            org.telegram.ui.PaymentFormActivity$BottomFrameLayout r1 = r7.bottomLayout
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r2 = r2.recurring
            if (r2 == 0) goto L_0x1cb4
            boolean r2 = r7.isAcceptTermsChecked
            if (r2 == 0) goto L_0x1cb2
            goto L_0x1cb4
        L_0x1cb2:
            r2 = 0
            goto L_0x1cb5
        L_0x1cb4:
            r2 = 1
        L_0x1cb5:
            r1.setChecked(r2)
            android.widget.TextView r1 = r7.payTextView
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r2 = r2.recurring
            if (r2 == 0) goto L_0x1cca
            boolean r2 = r7.isAcceptTermsChecked
            if (r2 != 0) goto L_0x1cca
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            goto L_0x1ccc
        L_0x1cca:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x1ccc:
            r1.setAlpha(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.doneItem
            r1.setEnabled(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.doneItem
            android.view.View r1 = r1.getContentView()
            r2 = 4
            r1.setVisibility(r2)
            org.telegram.ui.PaymentFormActivity$18 r1 = new org.telegram.ui.PaymentFormActivity$18
            r1.<init>(r7, r8)
            r7.webView = r1
            r2 = -1
            r1.setBackgroundColor(r2)
            android.webkit.WebView r1 = r7.webView
            android.webkit.WebSettings r1 = r1.getSettings()
            r2 = 1
            r1.setJavaScriptEnabled(r2)
            android.webkit.WebView r1 = r7.webView
            android.webkit.WebSettings r1 = r1.getSettings()
            r1.setDomStorageEnabled(r2)
            r1 = 21
            if (r0 < r1) goto L_0x1d12
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setMixedContentMode(r12)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r1 = r7.webView
            r0.setAcceptThirdPartyCookies(r1, r2)
        L_0x1d12:
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$19 r1 = new org.telegram.ui.PaymentFormActivity$19
            r1.<init>()
            r0.setWebViewClient(r1)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.recurring
            if (r0 == 0) goto L_0x1dfc
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r0 = new org.telegram.ui.Cells.RecurrentPaymentsAcceptCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r32.getResourceProvider()
            r0.<init>(r8, r1)
            r7.recurrentAcceptCell = r0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r1 = r1.recurring
            if (r1 == 0) goto L_0x1d3d
            boolean r1 = r7.isAcceptTermsChecked
            if (r1 == 0) goto L_0x1d3d
            r1 = 1
            goto L_0x1d3e
        L_0x1d3d:
            r1 = 0
        L_0x1d3e:
            r0.setChecked(r1)
            r0 = 2131627368(0x7f0e0d68, float:1.8881998E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r0)
            r2 = 42
            int r2 = r0.indexOf(r2)
            r3 = 42
            int r3 = r0.lastIndexOf(r3)
            r6 = -1
            if (r2 == r6) goto L_0x1d9c
            if (r3 == r6) goto L_0x1d9c
            android.text.SpannableString r6 = new android.text.SpannableString
            int r11 = r2 + 1
            java.lang.String r11 = r0.substring(r11, r3)
            r6.<init>(r11)
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r13 = r13.invoice
            java.lang.String r13 = r13.recurring_terms_url
            r11.<init>(r13)
            int r13 = r6.length()
            r14 = 33
            r6.setSpan(r11, r12, r13, r14)
            r11 = 1
            int r3 = r3 + r11
            r1.replace(r2, r3, r6)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r2 = r0.substring(r12, r2)
            r11.append(r2)
            r11.append(r6)
            java.lang.String r0 = r0.substring(r3)
            r11.append(r0)
            java.lang.String r0 = r11.toString()
        L_0x1d9c:
            java.lang.String r2 = "%1$s"
            int r0 = r0.indexOf(r2)
            r2 = -1
            if (r0 == r2) goto L_0x1dc3
            int r2 = r0 + 4
            java.lang.String r3 = r7.currentBotName
            r1.replace(r0, r2, r3)
            org.telegram.ui.Components.TypefaceSpan r2 = new org.telegram.ui.Components.TypefaceSpan
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r2.<init>(r3)
            java.lang.String r3 = r7.currentBotName
            int r3 = r3.length()
            int r3 = r3 + r0
            r6 = 33
            r1.setSpan(r2, r0, r3, r6)
        L_0x1dc3:
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r0 = r7.recurrentAcceptCell
            r0.setText(r1)
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r0 = r7.recurrentAcceptCell
            int r1 = r7.getThemedColor(r10)
            java.lang.String r2 = "listSelectorSDK21"
            int r2 = r7.getThemedColor(r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r1, r2)
            r0.setBackground(r1)
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r0 = r7.recurrentAcceptCell
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.RecurrentPaymentsAcceptCell r0 = r7.recurrentAcceptCell
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 80
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r4.addView(r0, r1)
        L_0x1dfc:
            android.webkit.WebView r0 = r7.webView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r4.addView(r0, r1)
            android.webkit.WebView r0 = r7.webView
            r1 = 8
            r0.setVisibility(r1)
        L_0x1e0f:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r7.resourcesProvider
            r1.<init>((android.content.Context) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
            r2 = 1
            r0[r2] = r1
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r2]
            r1 = 2131165436(0x7var_fc, float:1.794509E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r9)
            r0.setBackgroundDrawable(r1)
            if (r5 == 0) goto L_0x1e45
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1e45
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            if (r0 != 0) goto L_0x1e45
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            if (r0 == 0) goto L_0x1e3c
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 != 0) goto L_0x1e45
        L_0x1e3c:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = 1
            r0 = r0[r1]
            r0.setVisibility(r5)
            goto L_0x1e46
        L_0x1e45:
            r1 = 1
        L_0x1e46:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r7.sectionCell
            r1 = r2[r1]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
        L_0x1e55:
            android.view.View r0 = r7.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda62(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
        this.countryName = country.shortname;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(TextView textView2, int i, KeyEvent keyEvent) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        boolean z = !this.saveShippingInfo;
        this.saveShippingInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$6(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda63(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(CountrySelectActivity.Country country) {
        this.inputFields[4].setText(country.name);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$7(TextView textView2, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int intValue = ((Integer) textView2.getTag()).intValue();
            while (true) {
                intValue++;
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (intValue < editTextBoldCursorArr.length) {
                    if (intValue == 4) {
                        intValue++;
                    }
                    if (((View) editTextBoldCursorArr[intValue].getParent()).getVisibility() == 0) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(View view) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$11(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(View view) {
        this.passwordOk = false;
        goToNextStep();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(View view) {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$14(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView2);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(TextView textView2, long j, View view) {
        long longValue = ((Long) textView2.getTag()).longValue();
        Long l = this.tipAmount;
        if (l == null || longValue != l.longValue()) {
            this.inputFields[0].setText(LocaleController.getInstance().formatCurrencyString(j, false, true, true, this.paymentForm.invoice.currency));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$17(View view) {
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentCheckoutMethod", NUM), true);
            builder.setItems(new CharSequence[]{this.cardName, LocaleController.getString("PaymentCheckoutMethodNewCard", NUM)}, new int[]{NUM, NUM}, new PaymentFormActivity$$ExternalSyntheticLambda3(this));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$16(DialogInterface dialogInterface, int i) {
        if (i == 1) {
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 2, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
            paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
                public /* synthetic */ void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
                    PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tLRPC$TL_account_password);
                }

                public /* synthetic */ void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
                    PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tLRPC$TL_payments_validateRequestedInfo);
                }

                public /* synthetic */ void onFragmentDestroyed() {
                    PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
                }

                public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                    PaymentFormActivity.this.paymentForm.saved_credentials = null;
                    String unused = PaymentFormActivity.this.paymentJson = str;
                    boolean unused2 = PaymentFormActivity.this.saveCardInfo = z;
                    String unused3 = PaymentFormActivity.this.cardName = str2;
                    TLRPC$TL_inputPaymentCredentialsGooglePay unused4 = PaymentFormActivity.this.googlePayCredentials = tLRPC$TL_inputPaymentCredentialsGooglePay;
                    PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", NUM), true);
                    return false;
                }
            });
            presentFragment(paymentFormActivity);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$18(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tLRPC$TL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tLRPC$TL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
                TLRPC$TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$19(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tLRPC$TL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tLRPC$TL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
                TLRPC$TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$20(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tLRPC$TL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tLRPC$TL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
                TLRPC$TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$21(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public /* synthetic */ void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
                PaymentFormActivityDelegate.CC.$default$currentPasswordUpdated(this, tLRPC$TL_account_password);
            }

            public /* synthetic */ boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                return PaymentFormActivityDelegate.CC.$default$didSelectNewCard(this, str, str2, z, tLRPC$TL_inputPaymentCredentialsGooglePay);
            }

            public /* synthetic */ void onFragmentDestroyed() {
                PaymentFormActivityDelegate.CC.$default$onFragmentDestroyed(this);
            }

            public void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
                TLRPC$TL_payments_validateRequestedInfo unused = PaymentFormActivity.this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
                PaymentFormActivity paymentFormActivity = PaymentFormActivity.this;
                paymentFormActivity.setAddressFields(paymentFormActivity.validateRequest.info);
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$24(String str, View view) {
        TLRPC$TL_paymentRequestedInfo tLRPC$TL_paymentRequestedInfo;
        TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = this.paymentForm;
        TLRPC$TL_invoice tLRPC$TL_invoice = tLRPC$TL_payments_paymentForm.invoice;
        if (!tLRPC$TL_invoice.recurring || this.recurrentAccepted) {
            boolean z = this.isCheckoutPreview;
            if (!z || tLRPC$TL_payments_paymentForm.saved_info == null || this.validateRequest != null) {
                int i = 0;
                if (!z || (((tLRPC$TL_paymentRequestedInfo = tLRPC$TL_payments_paymentForm.saved_info) != null || (!tLRPC$TL_invoice.shipping_address_requested && !tLRPC$TL_invoice.email_requested && !tLRPC$TL_invoice.name_requested && !tLRPC$TL_invoice.phone_requested)) && tLRPC$TL_payments_paymentForm.saved_credentials != null && (this.shippingOption != null || !tLRPC$TL_invoice.flexible))) {
                    if (!tLRPC$TL_payments_paymentForm.password_missing && tLRPC$TL_payments_paymentForm.saved_credentials != null) {
                        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                            UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                            UserConfig.getInstance(this.currentAccount).saveConfig(false);
                        }
                        if (UserConfig.getInstance(this.currentAccount).tmpPassword == null) {
                            this.needPayAfterTransition = true;
                            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 3, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment));
                            this.needPayAfterTransition = false;
                            return;
                        } else if (this.isCheckoutPreview) {
                            this.isCheckoutPreview = false;
                            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
                        }
                    }
                    TLRPC$User tLRPC$User = this.botUser;
                    if (tLRPC$User == null || tLRPC$User.verified) {
                        showPayAlert(this.totalPrice[0]);
                        return;
                    }
                    String str2 = "payment_warning_" + this.botUser.id;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                    if (!notificationsSettings.getBoolean(str2, false)) {
                        notificationsSettings.edit().putBoolean(str2, true).commit();
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("PaymentWarning", NUM));
                        builder.setMessage(LocaleController.formatString("PaymentWarningText", NUM, this.currentBotName, str));
                        builder.setPositiveButton(LocaleController.getString("Continue", NUM), new PaymentFormActivity$$ExternalSyntheticLambda0(this));
                        showDialog(builder.create());
                        return;
                    }
                    showPayAlert(this.totalPrice[0]);
                    return;
                }
                if (tLRPC$TL_paymentRequestedInfo != null || (!tLRPC$TL_invoice.shipping_address_requested && !tLRPC$TL_invoice.email_requested && !tLRPC$TL_invoice.name_requested && !tLRPC$TL_invoice.phone_requested)) {
                    i = tLRPC$TL_payments_paymentForm.saved_credentials == null ? 2 : 1;
                }
                this.paymentStatusSent = true;
                presentFragment(new PaymentFormActivity(tLRPC$TL_payments_paymentForm, this.messageObject, this.invoiceSlug, i, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment));
                return;
            }
            setDonePressed(true);
            sendSavedForm(new PaymentFormActivity$$ExternalSyntheticLambda35(this, view));
            return;
        }
        AndroidUtilities.shakeViewSpring((View) this.recurrentAcceptCell.getTextView(), 4.5f);
        try {
            this.recurrentAcceptCell.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$22(View view) {
        setDonePressed(false);
        view.callOnClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$23(DialogInterface dialogInterface, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$25(View view) {
        if (!this.donePressed) {
            boolean z = !this.recurrentAccepted;
            this.recurrentAccepted = z;
            this.recurrentAcceptCell.setChecked(z);
            this.bottomLayout.setChecked(this.recurrentAccepted);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$26(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$28(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), PaymentFormActivity$$ExternalSyntheticLambda61.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$30(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(string);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", NUM));
        builder.setPositiveButton(LocaleController.getString("Disable", NUM), new PaymentFormActivity$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView2 = (TextView) create.getButton(-1);
        if (textView2 != null) {
            textView2.setTextColor(getThemedColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$29(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$31(TextView textView2, int i, KeyEvent keyEvent) {
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

    /* access modifiers changed from: private */
    public void setAddressFields(TLRPC$TL_paymentRequestedInfo tLRPC$TL_paymentRequestedInfo) {
        TLRPC$TL_postAddress tLRPC$TL_postAddress = tLRPC$TL_paymentRequestedInfo.shipping_address;
        boolean z = false;
        if (tLRPC$TL_postAddress != null) {
            this.detailSettingsCell[2].setTextAndValueAndIcon(String.format("%s %s, %s, %s, %s, %s", new Object[]{tLRPC$TL_postAddress.street_line1, tLRPC$TL_postAddress.street_line2, tLRPC$TL_postAddress.city, tLRPC$TL_postAddress.state, tLRPC$TL_postAddress.country_iso2, tLRPC$TL_postAddress.post_code}), LocaleController.getString("PaymentShippingAddress", NUM), NUM, true);
        }
        String str = tLRPC$TL_paymentRequestedInfo.name;
        if (str != null) {
            this.detailSettingsCell[3].setTextAndValueAndIcon(str, LocaleController.getString("PaymentCheckoutName", NUM), NUM, true);
        }
        if (tLRPC$TL_paymentRequestedInfo.phone != null) {
            this.detailSettingsCell[4].setTextAndValueAndIcon(PhoneFormat.getInstance().format(tLRPC$TL_paymentRequestedInfo.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", NUM), NUM, (tLRPC$TL_paymentRequestedInfo.email == null && this.shippingOption == null) ? false : true);
        }
        String str2 = tLRPC$TL_paymentRequestedInfo.email;
        if (str2 != null) {
            TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[5];
            String string = LocaleController.getString("PaymentCheckoutEmail", NUM);
            if (this.shippingOption != null) {
                z = true;
            }
            textDetailSettingsCell.setTextAndValueAndIcon(str2, string, NUM, z);
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
            int themedColor = getThemedColor("contacts_inviteBackground");
            int childCount = this.tipLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TextView textView3 = (TextView) this.tipLayout.getChildAt(i);
                if (textView3.getTag().equals(this.tipAmount)) {
                    Theme.setDrawableColor(textView3.getBackground(), themedColor);
                    textView3.setTextColor(getThemedColor("contacts_inviteText"));
                } else {
                    Theme.setDrawableColor(textView3.getBackground(), NUM & themedColor);
                    textView3.setTextColor(getThemedColor("chats_secretName"));
                }
                textView3.invalidate();
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
        this.googlePayButton.setOnClickListener(new PaymentFormActivity$$ExternalSyntheticLambda5(this));
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x004b A[Catch:{ JSONException -> 0x00b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0068 A[Catch:{ JSONException -> 0x00b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00a2 A[Catch:{ JSONException -> 0x00b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createGooglePayButton$32(android.view.View r5) {
        /*
            r4 = this;
            android.widget.FrameLayout r5 = r4.googlePayButton
            r0 = 0
            r5.setClickable(r0)
            org.json.JSONObject r5 = r4.getBaseRequest()     // Catch:{ JSONException -> 0x00b2 }
            org.json.JSONObject r0 = r4.getBaseCardPaymentMethod()     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r1 = r4.googlePayPublicKey     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r2 = "tokenizationSpecification"
            if (r1 == 0) goto L_0x0021
            org.json.JSONObject r1 = r4.googlePayParameters     // Catch:{ JSONException -> 0x00b2 }
            if (r1 != 0) goto L_0x0021
            org.telegram.ui.PaymentFormActivity$22 r1 = new org.telegram.ui.PaymentFormActivity$22     // Catch:{ JSONException -> 0x00b2 }
            r1.<init>()     // Catch:{ JSONException -> 0x00b2 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00b2 }
            goto L_0x0029
        L_0x0021:
            org.telegram.ui.PaymentFormActivity$23 r1 = new org.telegram.ui.PaymentFormActivity$23     // Catch:{ JSONException -> 0x00b2 }
            r1.<init>()     // Catch:{ JSONException -> 0x00b2 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00b2 }
        L_0x0029:
            java.lang.String r1 = "allowedPaymentMethods"
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ JSONException -> 0x00b2 }
            r2.<init>()     // Catch:{ JSONException -> 0x00b2 }
            org.json.JSONArray r0 = r2.put(r0)     // Catch:{ JSONException -> 0x00b2 }
            r5.put(r1, r0)     // Catch:{ JSONException -> 0x00b2 }
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00b2 }
            r0.<init>()     // Catch:{ JSONException -> 0x00b2 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ JSONException -> 0x00b2 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r4.paymentForm     // Catch:{ JSONException -> 0x00b2 }
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice     // Catch:{ JSONException -> 0x00b2 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r2.prices     // Catch:{ JSONException -> 0x00b2 }
            r1.<init>(r2)     // Catch:{ JSONException -> 0x00b2 }
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = r4.shippingOption     // Catch:{ JSONException -> 0x00b2 }
            if (r2 == 0) goto L_0x0050
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r2.prices     // Catch:{ JSONException -> 0x00b2 }
            r1.addAll(r2)     // Catch:{ JSONException -> 0x00b2 }
        L_0x0050:
            java.lang.String r2 = "totalPrice"
            java.lang.String r1 = r4.getTotalPriceDecimalString(r1)     // Catch:{ JSONException -> 0x00b2 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r1 = "totalPriceStatus"
            java.lang.String r2 = "FINAL"
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r1 = r4.googlePayCountryCode     // Catch:{ JSONException -> 0x00b2 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ JSONException -> 0x00b2 }
            if (r1 != 0) goto L_0x006f
            java.lang.String r1 = "countryCode"
            java.lang.String r2 = r4.googlePayCountryCode     // Catch:{ JSONException -> 0x00b2 }
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b2 }
        L_0x006f:
            java.lang.String r1 = "currencyCode"
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r4.paymentForm     // Catch:{ JSONException -> 0x00b2 }
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r2 = r2.currency     // Catch:{ JSONException -> 0x00b2 }
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r1 = "checkoutOption"
            java.lang.String r2 = "COMPLETE_IMMEDIATE_PURCHASE"
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r1 = "transactionInfo"
            r5.put(r1, r0)     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r0 = "merchantInfo"
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00b2 }
            r1.<init>()     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r2 = "merchantName"
            java.lang.String r3 = r4.currentBotName     // Catch:{ JSONException -> 0x00b2 }
            org.json.JSONObject r1 = r1.put(r2, r3)     // Catch:{ JSONException -> 0x00b2 }
            r5.put(r0, r1)     // Catch:{ JSONException -> 0x00b2 }
            java.lang.String r5 = r5.toString()     // Catch:{ JSONException -> 0x00b2 }
            com.google.android.gms.wallet.PaymentDataRequest r5 = com.google.android.gms.wallet.PaymentDataRequest.fromJson(r5)     // Catch:{ JSONException -> 0x00b2 }
            if (r5 == 0) goto L_0x00b6
            com.google.android.gms.wallet.PaymentsClient r0 = r4.paymentsClient     // Catch:{ JSONException -> 0x00b2 }
            com.google.android.gms.tasks.Task r5 = r0.loadPaymentData(r5)     // Catch:{ JSONException -> 0x00b2 }
            android.app.Activity r0 = r4.getParentActivity()     // Catch:{ JSONException -> 0x00b2 }
            r1 = 991(0x3df, float:1.389E-42)
            com.google.android.gms.wallet.AutoResolveHelper.resolveTask(r5, r0, r1)     // Catch:{ JSONException -> 0x00b2 }
            goto L_0x00b6
        L_0x00b2:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.lambda$createGooglePayButton$32(android.view.View):void");
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda52(this), 10);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$35(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda44(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$34(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.loadingPasswordInfo = false;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = this.paymentForm;
            if (tLRPC$TL_payments_paymentForm != null && this.currentPassword.has_password) {
                tLRPC$TL_payments_paymentForm.password_missing = false;
                tLRPC$TL_payments_paymentForm.can_save_credentials = true;
                updateSavePaymentField();
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            PaymentFormActivity paymentFormActivity = this.passwordFragment;
            if (paymentFormActivity != null) {
                paymentFormActivity.setCurrentPassword(this.currentPassword);
            }
            if (!this.currentPassword.has_password && this.shortPollRunnable == null) {
                PaymentFormActivity$$ExternalSyntheticLambda32 paymentFormActivity$$ExternalSyntheticLambda32 = new PaymentFormActivity$$ExternalSyntheticLambda32(this);
                this.shortPollRunnable = paymentFormActivity$$ExternalSyntheticLambda32;
                AndroidUtilities.runOnUIThread(paymentFormActivity$$ExternalSyntheticLambda32, 5000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$33() {
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
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("PaymentTransactionMessage2", NUM, str, this.currentBotName, this.currentItemName)));
        builder.setPositiveButton(LocaleController.getString("Continue", NUM), new PaymentFormActivity$$ExternalSyntheticLambda2(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPayAlert$36(DialogInterface dialogInterface, int i) {
        setDonePressed(true);
        sendData();
    }

    private JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    private JSONObject getBaseCardPaymentMethod() throws JSONException {
        List asList = Arrays.asList(new String[]{"AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA"});
        List asList2 = Arrays.asList(new String[]{"PAN_ONLY", "CRYPTOGRAM_3DS"});
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("type", "CARD");
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("allowedAuthMethods", new JSONArray(asList2));
        jSONObject2.put("allowedCardNetworks", new JSONArray(asList));
        jSONObject.put("parameters", jSONObject2);
        return jSONObject;
    }

    public Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject baseRequest = getBaseRequest();
            baseRequest.put("allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));
            return Optional.of(baseRequest);
        } catch (JSONException unused) {
            return Optional.empty();
        }
    }

    private void initGooglePay(Context context) {
        IsReadyToPayRequest fromJson;
        if (Build.VERSION.SDK_INT >= 19 && getParentActivity() != null) {
            this.paymentsClient = Wallet.getPaymentsClient(context, new Wallet.WalletOptions.Builder().setEnvironment(this.paymentForm.invoice.test ? 3 : 1).setTheme(1).build());
            Optional<JSONObject> isReadyToPayRequest = getIsReadyToPayRequest();
            if (isReadyToPayRequest.isPresent() && (fromJson = IsReadyToPayRequest.fromJson(isReadyToPayRequest.get().toString())) != null) {
                this.paymentsClient.isReadyToPay(fromJson).addOnCompleteListener(getParentActivity(), new PaymentFormActivity$$ExternalSyntheticLambda31(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initGooglePay$37(Task task) {
        if (task.isSuccessful()) {
            FrameLayout frameLayout = this.googlePayContainer;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                return;
            }
            return;
        }
        FileLog.e("isReadyToPay failed", (Throwable) task.getException());
    }

    private String getTotalPriceString(ArrayList<TLRPC$TL_labeledPrice> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += arrayList.get(i).amount;
        }
        Long l = this.tipAmount;
        if (l != null) {
            j += l.longValue();
        }
        return LocaleController.getInstance().formatCurrencyString(j, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TLRPC$TL_labeledPrice> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j += arrayList.get(i).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(j, this.paymentForm.invoice.currency, false);
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
        ArrayList<BaseFragment> arrayList;
        ActionBarLayout actionBarLayout = this.parentLayout;
        int i = 0;
        if (actionBarLayout == null || (arrayList = actionBarLayout.fragmentsStack) == null) {
            return 0;
        }
        int indexOf = arrayList.indexOf(this);
        if (indexOf == -1) {
            indexOf = this.parentLayout.fragmentsStack.size();
        }
        while (true) {
            if (i >= this.parentLayout.fragmentsStack.size()) {
                i = indexOf;
                break;
            } else if (this.parentLayout.fragmentsStack.get(i) instanceof PaymentFormActivity) {
                break;
            } else {
                i++;
            }
        }
        return i - indexOf;
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
        } catch (Throwable th) {
            FileLog.e(th);
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
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            WebView webView2 = this.webView;
            if (webView2 == null) {
                int i = this.currentStep;
                if (i == 2) {
                    AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda33(this), 100);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTransitionAnimationEnd$38() {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.twoStepPasswordChanged) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = this.paymentForm;
            tLRPC$TL_payments_paymentForm.password_missing = false;
            tLRPC$TL_payments_paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (i == NotificationCenter.didRemoveTwoStepPassword) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm2 = this.paymentForm;
            tLRPC$TL_payments_paymentForm2.password_missing = true;
            tLRPC$TL_payments_paymentForm2.can_save_credentials = false;
            updateSavePaymentField();
        } else if (i == NotificationCenter.paymentFinished) {
            this.paymentStatusSent = true;
            removeSelfFromStack();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 991) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda34(this, i2, intent));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResultFragment$39(int i, Intent intent) {
        String json;
        if (i == -1) {
            PaymentData fromIntent = PaymentData.getFromIntent(intent);
            if (fromIntent != null && (json = fromIntent.toJson()) != null) {
                try {
                    JSONObject jSONObject = new JSONObject(json).getJSONObject("paymentMethodData");
                    JSONObject jSONObject2 = jSONObject.getJSONObject("tokenizationData");
                    jSONObject2.getString("type");
                    String string = jSONObject2.getString("token");
                    if (this.googlePayPublicKey == null) {
                        if (this.googlePayParameters == null) {
                            Token parseToken = TokenParser.parseToken(string);
                            this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{parseToken.getType(), parseToken.getId()});
                            Card card = parseToken.getCard();
                            this.cardName = card.getType() + " *" + card.getLast4();
                            goToNextStep();
                        }
                    }
                    TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay = new TLRPC$TL_inputPaymentCredentialsGooglePay();
                    this.googlePayCredentials = tLRPC$TL_inputPaymentCredentialsGooglePay;
                    tLRPC$TL_inputPaymentCredentialsGooglePay.payment_token = new TLRPC$TL_dataJSON();
                    this.googlePayCredentials.payment_token.data = jSONObject2.toString();
                    String optString = jSONObject.optString("description");
                    if (!TextUtils.isEmpty(optString)) {
                        this.cardName = optString;
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
        } else if (i == 1) {
            Status statusFromIntent = AutoResolveHelper.getStatusFromIntent(intent);
            StringBuilder sb = new StringBuilder();
            sb.append("android pay error ");
            sb.append(statusFromIntent != null ? statusFromIntent.getStatusMessage() : "");
            FileLog.e(sb.toString());
        }
        showEditDoneProgress(true, false);
        setDonePressed(false);
        this.googlePayButton.setClickable(true);
    }

    /* access modifiers changed from: private */
    public void goToNextStep() {
        int i;
        int i2;
        boolean z;
        int i3 = this.currentStep;
        if (i3 == 0) {
            PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
            if (paymentFormActivityDelegate != null) {
                paymentFormActivityDelegate.didSelectNewAddress(this.validateRequest);
                finishFragment();
                return;
            }
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = this.paymentForm;
            if (tLRPC$TL_payments_paymentForm.invoice.flexible) {
                i = 1;
            } else if (tLRPC$TL_payments_paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, i, this.requestedInfo, (TLRPC$TL_shippingOption) null, (Long) null, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
        } else if (i3 == 1) {
            if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i2 = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i2 = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, i2, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
        } else if (i3 == 2) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm2 = this.paymentForm;
            if (!tLRPC$TL_payments_paymentForm2.password_missing || !(z = this.saveCardInfo)) {
                PaymentFormActivityDelegate paymentFormActivityDelegate2 = this.delegate;
                if (paymentFormActivityDelegate2 != null) {
                    paymentFormActivityDelegate2.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials);
                    finishFragment();
                    return;
                }
                presentFragment(new PaymentFormActivity(tLRPC$TL_payments_paymentForm2, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                return;
            }
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm2, this.messageObject, this.invoiceSlug, 6, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, z, this.googlePayCredentials, this.parentFragment);
            this.passwordFragment = paymentFormActivity;
            paymentFormActivity.setCurrentPassword(this.currentPassword);
            this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                public /* synthetic */ void didSelectNewAddress(TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo) {
                    PaymentFormActivityDelegate.CC.$default$didSelectNewAddress(this, tLRPC$TL_payments_validateRequestedInfo);
                }

                public boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
                    if (PaymentFormActivity.this.delegate != null) {
                        PaymentFormActivity.this.delegate.didSelectNewCard(str, str2, z, tLRPC$TL_inputPaymentCredentialsGooglePay);
                    }
                    if (PaymentFormActivity.this.isWebView) {
                        PaymentFormActivity.this.removeSelfFromStack();
                    }
                    return PaymentFormActivity.this.delegate != null;
                }

                public void onFragmentDestroyed() {
                    PaymentFormActivity unused = PaymentFormActivity.this.passwordFragment = null;
                }

                public void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
                    TLRPC$TL_account_password unused = PaymentFormActivity.this.currentPassword = tLRPC$TL_account_password;
                }
            });
            presentFragment(this.passwordFragment, this.isWebView);
        } else if (i3 == 3) {
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, this.passwordOk ? 4 : 2, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
        } else if (i3 == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            String str = this.botUser.username;
            if ((str == null || !str.equalsIgnoreCase(getMessagesController().premiumBotUsername)) && !ObjectsCompat$$ExternalSyntheticBackport0.m(this.invoiceSlug, getMessagesController().premiumInvoiceSlug)) {
                finishFragment();
                return;
            }
            Iterator it = new ArrayList(getParentLayout().fragmentsStack).iterator();
            while (it.hasNext()) {
                BaseFragment baseFragment = (BaseFragment) it.next();
                if ((baseFragment instanceof ChatActivity) || (baseFragment instanceof PremiumPreviewFragment)) {
                    baseFragment.removeSelfFromStack();
                }
            }
            presentFragment(new PremiumPreviewFragment().setForcePremium(), true);
            if (getParentActivity() instanceof LaunchActivity) {
                try {
                    this.fragmentView.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                ((LaunchActivity) getParentActivity()).getFireworksOverlay().start();
            }
        } else if (i3 == 6) {
            if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials)) {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.invoiceSlug, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
            } else {
                finishFragment();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSavePaymentField() {
        if (this.bottomCell[0] != null && this.sectionCell[2] != null) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = this.paymentForm;
            if ((tLRPC$TL_payments_paymentForm.password_missing || tLRPC$TL_payments_paymentForm.can_save_credentials) && (this.webView == null || !this.webviewLoading)) {
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

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036 A[Catch:{ Exception -> 0x0099 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045 A[Catch:{ Exception -> 0x0099 }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"HardwareIds"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fillNumber(java.lang.String r8) {
        /*
            r7 = this;
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0099 }
            java.lang.String r1 = "phone"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ Exception -> 0x0099 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0099 }
            r1 = 1
            if (r8 != 0) goto L_0x0019
            int r2 = r0.getSimState()     // Catch:{ Exception -> 0x0099 }
            if (r2 == r1) goto L_0x009d
            int r2 = r0.getPhoneType()     // Catch:{ Exception -> 0x0099 }
            if (r2 == 0) goto L_0x009d
        L_0x0019:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0099 }
            r3 = 23
            r4 = 0
            if (r2 < r3) goto L_0x002f
            android.app.Activity r2 = r7.getParentActivity()     // Catch:{ Exception -> 0x0099 }
            java.lang.String r3 = "android.permission.READ_PHONE_STATE"
            int r2 = r2.checkSelfPermission(r3)     // Catch:{ Exception -> 0x0099 }
            if (r2 != 0) goto L_0x002d
            goto L_0x002f
        L_0x002d:
            r2 = 0
            goto L_0x0030
        L_0x002f:
            r2 = 1
        L_0x0030:
            if (r8 != 0) goto L_0x0034
            if (r2 == 0) goto L_0x009d
        L_0x0034:
            if (r8 != 0) goto L_0x003e
            java.lang.String r8 = r0.getLine1Number()     // Catch:{ Exception -> 0x0099 }
            java.lang.String r8 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r8)     // Catch:{ Exception -> 0x0099 }
        L_0x003e:
            r0 = 0
            boolean r2 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x0099 }
            if (r2 != 0) goto L_0x009d
            int r2 = r8.length()     // Catch:{ Exception -> 0x0099 }
            r3 = 4
            if (r2 <= r3) goto L_0x0080
        L_0x004c:
            r2 = 8
            if (r3 < r1) goto L_0x006e
            java.lang.String r5 = r8.substring(r4, r3)     // Catch:{ Exception -> 0x0099 }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r7.codesMap     // Catch:{ Exception -> 0x0099 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x0099 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x0099 }
            if (r6 == 0) goto L_0x006b
            java.lang.String r0 = r8.substring(r3)     // Catch:{ Exception -> 0x0099 }
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields     // Catch:{ Exception -> 0x0099 }
            r3 = r3[r2]     // Catch:{ Exception -> 0x0099 }
            r3.setText(r5)     // Catch:{ Exception -> 0x0099 }
            r3 = 1
            goto L_0x006f
        L_0x006b:
            int r3 = r3 + -1
            goto L_0x004c
        L_0x006e:
            r3 = 0
        L_0x006f:
            if (r3 != 0) goto L_0x0080
            java.lang.String r0 = r8.substring(r1)     // Catch:{ Exception -> 0x0099 }
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields     // Catch:{ Exception -> 0x0099 }
            r2 = r3[r2]     // Catch:{ Exception -> 0x0099 }
            java.lang.String r8 = r8.substring(r4, r1)     // Catch:{ Exception -> 0x0099 }
            r2.setText(r8)     // Catch:{ Exception -> 0x0099 }
        L_0x0080:
            if (r0 == 0) goto L_0x009d
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r7.inputFields     // Catch:{ Exception -> 0x0099 }
            r1 = 9
            r8 = r8[r1]     // Catch:{ Exception -> 0x0099 }
            r8.setText(r0)     // Catch:{ Exception -> 0x0099 }
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r7.inputFields     // Catch:{ Exception -> 0x0099 }
            r0 = r8[r1]     // Catch:{ Exception -> 0x0099 }
            r8 = r8[r1]     // Catch:{ Exception -> 0x0099 }
            int r8 = r8.length()     // Catch:{ Exception -> 0x0099 }
            r0.setSelection(r8)     // Catch:{ Exception -> 0x0099 }
            goto L_0x009d
        L_0x0099:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x009d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.fillNumber(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void sendSavePassword(boolean z) {
        String str;
        String str2;
        if (z || this.codeFieldCell.getVisibility() != 0) {
            TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = new TLRPC$TL_account_updatePasswordSettings();
            if (z) {
                this.doneItem.setVisibility(0);
                TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = new TLRPC$TL_account_passwordInputSettings();
                tLRPC$TL_account_updatePasswordSettings.new_settings = tLRPC$TL_account_passwordInputSettings;
                tLRPC$TL_account_passwordInputSettings.flags = 2;
                tLRPC$TL_account_passwordInputSettings.email = "";
                tLRPC$TL_account_updatePasswordSettings.password = new TLRPC$TL_inputCheckPasswordEmpty();
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
                    tLRPC$TL_account_updatePasswordSettings.password = new TLRPC$TL_inputCheckPasswordEmpty();
                    TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings2 = new TLRPC$TL_account_passwordInputSettings();
                    tLRPC$TL_account_updatePasswordSettings.new_settings = tLRPC$TL_account_passwordInputSettings2;
                    int i = tLRPC$TL_account_passwordInputSettings2.flags | 1;
                    tLRPC$TL_account_passwordInputSettings2.flags = i;
                    tLRPC$TL_account_passwordInputSettings2.hint = "";
                    tLRPC$TL_account_passwordInputSettings2.new_algo = this.currentPassword.new_algo;
                    tLRPC$TL_account_passwordInputSettings2.flags = i | 2;
                    tLRPC$TL_account_passwordInputSettings2.email = obj2.trim();
                    str = obj;
                    str2 = obj2;
                }
            }
            showEditDoneProgress(true, true);
            Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda49(this, z, str2, str, tLRPC$TL_account_updatePasswordSettings));
            return;
        }
        String text = this.codeFieldCell.getText();
        if (text.length() == 0) {
            shakeView(this.codeFieldCell);
            return;
        }
        showEditDoneProgress(true, true);
        TLRPC$TL_account_confirmPasswordEmail tLRPC$TL_account_confirmPasswordEmail = new TLRPC$TL_account_confirmPasswordEmail();
        tLRPC$TL_account_confirmPasswordEmail.code = text;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_confirmPasswordEmail, new PaymentFormActivity$$ExternalSyntheticLambda51(this), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$41(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda41(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$40(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        showEditDoneProgress(true, false);
        if (tLRPC$TL_error == null) {
            if (getParentActivity() != null) {
                Runnable runnable = this.shortPollRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.shortPollRunnable = null;
                }
                goToNextStep();
            }
        } else if (tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
            shakeView(this.codeFieldCell);
            this.codeFieldCell.setText("", false);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$46(boolean z, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda48(this, tLRPC$TL_error, z, tLObject, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$47(boolean z, String str, String str2, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        PaymentFormActivity$$ExternalSyntheticLambda59 paymentFormActivity$$ExternalSyntheticLambda59 = new PaymentFormActivity$$ExternalSyntheticLambda59(this, z, str);
        if (!z) {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str2);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                tLRPC$TL_account_updatePasswordSettings.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                if (tLRPC$TL_account_updatePasswordSettings.new_settings.new_password_hash == null) {
                    TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                    tLRPC$TL_error.text = "ALGO_INVALID";
                    paymentFormActivity$$ExternalSyntheticLambda59.run((TLObject) null, tLRPC$TL_error);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, paymentFormActivity$$ExternalSyntheticLambda59, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            paymentFormActivity$$ExternalSyntheticLambda59.run((TLObject) null, tLRPC$TL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, paymentFormActivity$$ExternalSyntheticLambda59, 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$45(TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, String str) {
        String str2;
        if (tLRPC$TL_error == null || !"SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            showEditDoneProgress(true, false);
            if (z) {
                TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                tLRPC$TL_account_password.has_password = false;
                tLRPC$TL_account_password.current_algo = null;
                this.delegate.currentPasswordUpdated(tLRPC$TL_account_password);
                finishFragment();
            } else if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                if (tLRPC$TL_error == null) {
                    return;
                }
                if (tLRPC$TL_error.text.equals("EMAIL_UNCONFIRMED") || tLRPC$TL_error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    this.emailCodeLength = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PaymentFormActivity$$ExternalSyntheticLambda4(this, str));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    Dialog showDialog = showDialog(builder.create());
                    if (showDialog != null) {
                        showDialog.setCanceledOnTouchOutside(false);
                        showDialog.setCancelable(false);
                    }
                } else if (tLRPC$TL_error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str2 = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
                    } else {
                        str2 = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
                }
            } else if (getParentActivity() != null) {
                goToNextStep();
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda58(this, z), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$43(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda46(this, tLRPC$TL_error, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$42(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            sendSavePassword(z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$44(String str, DialogInterface dialogInterface, int i) {
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
            Integer parseInt = Utilities.parseInt((CharSequence) split[0]);
            num = Utilities.parseInt((CharSequence) split[1]);
            num2 = parseInt;
        } else {
            num2 = null;
            num = null;
        }
        final Card card = new Card(this.inputFields[0].getText().toString(), num2, num, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), (String) null, (String) null, (String) null, (String) null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), (String) null);
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
                if ("stripe".equals(this.paymentForm.native_provider)) {
                    new Stripe(this.providerApiKey).createToken(card, new TokenCallback() {
                        public void onSuccess(Token token) {
                            if (!PaymentFormActivity.this.canceled) {
                                String unused = PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                                AndroidUtilities.runOnUIThread(new PaymentFormActivity$25$$ExternalSyntheticLambda0(this));
                            }
                        }

                        /* access modifiers changed from: private */
                        public /* synthetic */ void lambda$onSuccess$0() {
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
                } else if (!"smartglocal".equals(this.paymentForm.native_provider)) {
                    return true;
                } else {
                    new AsyncTask<Object, Object, String>() {
                        /* access modifiers changed from: protected */
                        /* JADX WARNING: Code restructure failed: missing block: B:36:0x0130, code lost:
                            if (r4 == null) goto L_0x0135;
                         */
                        /* JADX WARNING: Missing exception handler attribute for start block: B:28:0x0126 */
                        /* JADX WARNING: Removed duplicated region for block: B:42:0x013a  */
                        /* JADX WARNING: Unknown top exception splitter block from list: {B:20:0x0101=Splitter:B:20:0x0101, B:28:0x0126=Splitter:B:28:0x0126} */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public java.lang.String doInBackground(java.lang.Object... r13) {
                            /*
                                r12 = this;
                                java.lang.String r13 = "token"
                                java.lang.String r0 = "card"
                                java.lang.String r1 = ""
                                r2 = 0
                                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r3.<init>()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r4.<init>()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r5 = "number"
                                com.stripe.android.model.Card r6 = r7     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r6 = r6.getNumber()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r4.put(r5, r6)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r5 = "expiration_month"
                                java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r7 = "%02d"
                                r8 = 1
                                java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                com.stripe.android.model.Card r10 = r7     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.Integer r10 = r10.getExpMonth()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r11 = 0
                                r9[r11] = r10     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r6 = java.lang.String.format(r6, r7, r9)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r4.put(r5, r6)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r5 = "expiration_year"
                                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r6.<init>()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r6.append(r1)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                com.stripe.android.model.Card r7 = r7     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.Integer r7 = r7.getExpYear()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r6.append(r7)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r4.put(r5, r6)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r5 = "security_code"
                                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r6.<init>()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r6.append(r1)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                com.stripe.android.model.Card r7 = r7     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r7 = r7.getCVC()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r6.append(r7)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r4.put(r5, r6)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r3.put(r0, r4)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                org.telegram.ui.PaymentFormActivity r4 = org.telegram.ui.PaymentFormActivity.this     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r4.paymentForm     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                boolean r4 = r4.test     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                if (r4 == 0) goto L_0x0080
                                java.net.URL r4 = new java.net.URL     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r5 = "https://tgb-playground.smart-glocal.com/cds/v1/tokenize/card"
                                r4.<init>(r5)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                goto L_0x0087
                            L_0x0080:
                                java.net.URL r4 = new java.net.URL     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.lang.String r5 = "https://tgb.smart-glocal.com/cds/v1/tokenize/card"
                                r4.<init>(r5)     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                            L_0x0087:
                                java.net.URLConnection r4 = r4.openConnection()     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ Exception -> 0x012b, all -> 0x0129 }
                                r5 = 30000(0x7530, float:4.2039E-41)
                                r4.setConnectTimeout(r5)     // Catch:{ Exception -> 0x0127 }
                                r5 = 80000(0x13880, float:1.12104E-40)
                                r4.setReadTimeout(r5)     // Catch:{ Exception -> 0x0127 }
                                r4.setUseCaches(r11)     // Catch:{ Exception -> 0x0127 }
                                r4.setDoOutput(r8)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r5 = "POST"
                                r4.setRequestMethod(r5)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r5 = "Content-Type"
                                java.lang.String r6 = "application/json"
                                r4.setRequestProperty(r5, r6)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r5 = "X-PUBLIC-TOKEN"
                                org.telegram.ui.PaymentFormActivity r6 = org.telegram.ui.PaymentFormActivity.this     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r6 = r6.providerApiKey     // Catch:{ Exception -> 0x0127 }
                                r4.setRequestProperty(r5, r6)     // Catch:{ Exception -> 0x0127 }
                                java.io.OutputStream r5 = r4.getOutputStream()     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0120 }
                                java.lang.String r6 = "UTF-8"
                                byte[] r3 = r3.getBytes(r6)     // Catch:{ all -> 0x0120 }
                                r5.write(r3)     // Catch:{ all -> 0x0120 }
                                r5.close()     // Catch:{ Exception -> 0x0127 }
                                int r3 = r4.getResponseCode()     // Catch:{ Exception -> 0x0127 }
                                r5 = 200(0xc8, float:2.8E-43)
                                if (r3 < r5) goto L_0x0101
                                r5 = 300(0x12c, float:4.2E-43)
                                if (r3 >= r5) goto L_0x0101
                                org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x0127 }
                                r1.<init>()     // Catch:{ Exception -> 0x0127 }
                                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0127 }
                                java.io.InputStream r5 = r4.getInputStream()     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r5 = org.telegram.ui.PaymentFormActivity.getResponseBody(r5)     // Catch:{ Exception -> 0x0127 }
                                r3.<init>(r5)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r5 = "data"
                                org.json.JSONObject r3 = r3.getJSONObject(r5)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r3 = r3.getString(r13)     // Catch:{ Exception -> 0x0127 }
                                r1.put(r13, r3)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r13 = "type"
                                r1.put(r13, r0)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r13 = r1.toString()     // Catch:{ Exception -> 0x0127 }
                                r4.disconnect()
                                return r13
                            L_0x0101:
                                boolean r13 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0127 }
                                if (r13 == 0) goto L_0x0132
                                java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0127 }
                                r13.<init>()     // Catch:{ Exception -> 0x0127 }
                                r13.append(r1)     // Catch:{ Exception -> 0x0127 }
                                java.io.InputStream r0 = r4.getErrorStream()     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r0 = org.telegram.ui.PaymentFormActivity.getResponseBody(r0)     // Catch:{ Exception -> 0x0127 }
                                r13.append(r0)     // Catch:{ Exception -> 0x0127 }
                                java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0127 }
                                org.telegram.messenger.FileLog.e((java.lang.String) r13)     // Catch:{ Exception -> 0x0127 }
                                goto L_0x0132
                            L_0x0120:
                                r13 = move-exception
                                if (r5 == 0) goto L_0x0126
                                r5.close()     // Catch:{ all -> 0x0126 }
                            L_0x0126:
                                throw r13     // Catch:{ Exception -> 0x0127 }
                            L_0x0127:
                                r13 = move-exception
                                goto L_0x012d
                            L_0x0129:
                                r13 = move-exception
                                goto L_0x0138
                            L_0x012b:
                                r13 = move-exception
                                r4 = r2
                            L_0x012d:
                                org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)     // Catch:{ all -> 0x0136 }
                                if (r4 == 0) goto L_0x0135
                            L_0x0132:
                                r4.disconnect()
                            L_0x0135:
                                return r2
                            L_0x0136:
                                r13 = move-exception
                                r2 = r4
                            L_0x0138:
                                if (r2 == 0) goto L_0x013d
                                r2.disconnect()
                            L_0x013d:
                                throw r13
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.AnonymousClass26.doInBackground(java.lang.Object[]):java.lang.String");
                        }

                        /* access modifiers changed from: protected */
                        public void onPostExecute(String str) {
                            if (!PaymentFormActivity.this.canceled) {
                                if (str == null) {
                                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", NUM));
                                } else {
                                    String unused = PaymentFormActivity.this.paymentJson = str;
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
    }

    /* access modifiers changed from: private */
    public static String getResponseBody(InputStream inputStream) throws IOException {
        String next = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        inputStream.close();
        return next;
    }

    private void sendSavedForm(Runnable runnable) {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            this.validateRequest = new TLRPC$TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC$TL_inputInvoiceMessage tLRPC$TL_inputInvoiceMessage = new TLRPC$TL_inputInvoiceMessage();
                tLRPC$TL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                tLRPC$TL_inputInvoiceMessage.msg_id = this.messageObject.getId();
                this.validateRequest.invoice = tLRPC$TL_inputInvoiceMessage;
            } else {
                TLRPC$TL_inputInvoiceSlug tLRPC$TL_inputInvoiceSlug = new TLRPC$TL_inputInvoiceSlug();
                tLRPC$TL_inputInvoiceSlug.slug = this.invoiceSlug;
                this.validateRequest.invoice = tLRPC$TL_inputInvoiceSlug;
            }
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo = this.validateRequest;
            tLRPC$TL_payments_validateRequestedInfo.save = true;
            tLRPC$TL_payments_validateRequestedInfo.info = this.paymentForm.saved_info;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_validateRequestedInfo, new PaymentFormActivity$$ExternalSyntheticLambda53(this, runnable, tLRPC$TL_payments_validateRequestedInfo), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavedForm$50(Runnable runnable, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject2 instanceof TLRPC$TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda38(this, tLObject2, runnable));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda43(this, tLRPC$TL_error, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavedForm$48(TLObject tLObject, Runnable runnable) {
        this.requestedInfo = (TLRPC$TL_payments_validatedRequestedInfo) tLObject;
        runnable.run();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavedForm$49(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (tLRPC$TL_error != null) {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLObject, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public void sendForm() {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            this.validateRequest = new TLRPC$TL_payments_validateRequestedInfo();
            if (this.messageObject != null) {
                TLRPC$TL_inputInvoiceMessage tLRPC$TL_inputInvoiceMessage = new TLRPC$TL_inputInvoiceMessage();
                tLRPC$TL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                tLRPC$TL_inputInvoiceMessage.msg_id = this.messageObject.getId();
                this.validateRequest.invoice = tLRPC$TL_inputInvoiceMessage;
            } else {
                TLRPC$TL_inputInvoiceSlug tLRPC$TL_inputInvoiceSlug = new TLRPC$TL_inputInvoiceSlug();
                tLRPC$TL_inputInvoiceSlug.slug = this.invoiceSlug;
                this.validateRequest.invoice = tLRPC$TL_inputInvoiceSlug;
            }
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo = this.validateRequest;
            tLRPC$TL_payments_validateRequestedInfo.save = this.saveShippingInfo;
            tLRPC$TL_payments_validateRequestedInfo.info = new TLRPC$TL_paymentRequestedInfo();
            if (this.paymentForm.invoice.name_requested) {
                this.validateRequest.info.name = this.inputFields[6].getText().toString();
                this.validateRequest.info.flags |= 1;
            }
            if (this.paymentForm.invoice.phone_requested) {
                this.validateRequest.info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
                TLRPC$TL_paymentRequestedInfo tLRPC$TL_paymentRequestedInfo = this.validateRequest.info;
                tLRPC$TL_paymentRequestedInfo.flags = tLRPC$TL_paymentRequestedInfo.flags | 2;
            }
            if (this.paymentForm.invoice.email_requested) {
                this.validateRequest.info.email = this.inputFields[7].getText().toString().trim();
                this.validateRequest.info.flags |= 4;
            }
            if (this.paymentForm.invoice.shipping_address_requested) {
                this.validateRequest.info.shipping_address = new TLRPC$TL_postAddress();
                this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
                this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
                this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
                this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
                TLRPC$TL_postAddress tLRPC$TL_postAddress = this.validateRequest.info.shipping_address;
                String str = this.countryName;
                if (str == null) {
                    str = "";
                }
                tLRPC$TL_postAddress.country_iso2 = str;
                tLRPC$TL_postAddress.post_code = this.inputFields[5].getText().toString();
                this.validateRequest.info.flags |= 8;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new PaymentFormActivity$$ExternalSyntheticLambda55(this, this.validateRequest), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendForm$54(TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject2 instanceof TLRPC$TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda37(this, tLObject2));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda42(this, tLRPC$TL_error, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendForm$52(TLObject tLObject) {
        this.requestedInfo = (TLRPC$TL_payments_validatedRequestedInfo) tLObject;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
            tLRPC$TL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_clearSavedInfo, PaymentFormActivity$$ExternalSyntheticLambda60.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendForm$53(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        setDonePressed(false);
        showEditDoneProgress(true, false);
        if (tLRPC$TL_error != null) {
            String str = tLRPC$TL_error.text;
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -2092780146:
                    if (str.equals("ADDRESS_CITY_INVALID")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1623547228:
                    if (str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1224177757:
                    if (str.equals("ADDRESS_COUNTRY_INVALID")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1031752045:
                    if (str.equals("REQ_INFO_NAME_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case -274035920:
                    if (str.equals("ADDRESS_POSTCODE_INVALID")) {
                        c = 4;
                        break;
                    }
                    break;
                case 417441502:
                    if (str.equals("ADDRESS_STATE_INVALID")) {
                        c = 5;
                        break;
                    }
                    break;
                case 708423542:
                    if (str.equals("REQ_INFO_PHONE_INVALID")) {
                        c = 6;
                        break;
                    }
                    break;
                case 863965605:
                    if (str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                        c = 7;
                        break;
                    }
                    break;
                case 889106340:
                    if (str.equals("REQ_INFO_EMAIL_INVALID")) {
                        c = 8;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    shakeField(2);
                    return;
                case 1:
                    shakeField(0);
                    return;
                case 2:
                    shakeField(4);
                    return;
                case 3:
                    shakeField(6);
                    return;
                case 4:
                    shakeField(5);
                    return;
                case 5:
                    shakeField(3);
                    return;
                case 6:
                    shakeField(9);
                    return;
                case 7:
                    shakeField(1);
                    return;
                case 8:
                    shakeField(7);
                    return;
                default:
                    AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLObject, new Object[0]);
                    return;
            }
        }
    }

    private void sendData() {
        String str;
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm = new TLRPC$TL_payments_sendPaymentForm();
            if (this.messageObject != null) {
                TLRPC$TL_inputInvoiceMessage tLRPC$TL_inputInvoiceMessage = new TLRPC$TL_inputInvoiceMessage();
                tLRPC$TL_inputInvoiceMessage.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
                tLRPC$TL_inputInvoiceMessage.msg_id = this.messageObject.getId();
                tLRPC$TL_payments_sendPaymentForm.invoice = tLRPC$TL_inputInvoiceMessage;
            } else {
                TLRPC$TL_inputInvoiceSlug tLRPC$TL_inputInvoiceSlug = new TLRPC$TL_inputInvoiceSlug();
                tLRPC$TL_inputInvoiceSlug.slug = this.invoiceSlug;
                tLRPC$TL_payments_sendPaymentForm.invoice = tLRPC$TL_inputInvoiceSlug;
            }
            tLRPC$TL_payments_sendPaymentForm.form_id = this.paymentForm.form_id;
            if (UserConfig.getInstance(this.currentAccount).tmpPassword == null || this.paymentForm.saved_credentials == null) {
                TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay = this.googlePayCredentials;
                if (tLRPC$TL_inputPaymentCredentialsGooglePay != null) {
                    tLRPC$TL_payments_sendPaymentForm.credentials = tLRPC$TL_inputPaymentCredentialsGooglePay;
                } else {
                    TLRPC$TL_inputPaymentCredentials tLRPC$TL_inputPaymentCredentials = new TLRPC$TL_inputPaymentCredentials();
                    tLRPC$TL_payments_sendPaymentForm.credentials = tLRPC$TL_inputPaymentCredentials;
                    tLRPC$TL_inputPaymentCredentials.save = this.saveCardInfo;
                    tLRPC$TL_inputPaymentCredentials.data = new TLRPC$TL_dataJSON();
                    tLRPC$TL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
                }
            } else {
                TLRPC$TL_inputPaymentCredentialsSaved tLRPC$TL_inputPaymentCredentialsSaved = new TLRPC$TL_inputPaymentCredentialsSaved();
                tLRPC$TL_payments_sendPaymentForm.credentials = tLRPC$TL_inputPaymentCredentialsSaved;
                tLRPC$TL_inputPaymentCredentialsSaved.id = this.paymentForm.saved_credentials.id;
                tLRPC$TL_inputPaymentCredentialsSaved.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            }
            TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo = this.requestedInfo;
            if (!(tLRPC$TL_payments_validatedRequestedInfo == null || (str = tLRPC$TL_payments_validatedRequestedInfo.id) == null)) {
                tLRPC$TL_payments_sendPaymentForm.requested_info_id = str;
                tLRPC$TL_payments_sendPaymentForm.flags = 1 | tLRPC$TL_payments_sendPaymentForm.flags;
            }
            TLRPC$TL_shippingOption tLRPC$TL_shippingOption = this.shippingOption;
            if (tLRPC$TL_shippingOption != null) {
                tLRPC$TL_payments_sendPaymentForm.shipping_option_id = tLRPC$TL_shippingOption.id;
                tLRPC$TL_payments_sendPaymentForm.flags |= 2;
            }
            if ((this.paymentForm.invoice.flags & 256) != 0) {
                Long l = this.tipAmount;
                tLRPC$TL_payments_sendPaymentForm.tip_amount = l != null ? l.longValue() : 0;
                tLRPC$TL_payments_sendPaymentForm.flags |= 4;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_sendPaymentForm, new PaymentFormActivity$$ExternalSyntheticLambda57(this, tLRPC$TL_payments_sendPaymentForm), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$58(TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda47(this, tLRPC$TL_error, tLRPC$TL_payments_sendPaymentForm));
        } else if (tLObject instanceof TLRPC$TL_payments_paymentResult) {
            TLRPC$Updates tLRPC$Updates = ((TLRPC$TL_payments_paymentResult) tLObject).updates;
            TLRPC$Message[] tLRPC$MessageArr = new TLRPC$Message[1];
            int size = tLRPC$Updates.updates.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
                    tLRPC$MessageArr[0] = ((TLRPC$TL_updateNewMessage) tLRPC$Update).message;
                    break;
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
                    tLRPC$MessageArr[0] = ((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).message;
                    break;
                } else {
                    i++;
                }
            }
            getMessagesController().processUpdates(tLRPC$Updates, false);
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda50(this, tLRPC$MessageArr));
        } else if (tLObject instanceof TLRPC$TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda36(this, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$55(TLRPC$Message[] tLRPC$MessageArr) {
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback2 = this.paymentFormCallback;
        if (paymentFormCallback2 != null) {
            paymentFormCallback2.onInvoiceStatusChanged(InvoiceStatus.PAID);
        }
        goToNextStep();
        if (this.parentFragment instanceof ChatActivity) {
            ((ChatActivity) this.parentFragment).getUndoView().showWithAction(0, 77, (Object) AndroidUtilities.replaceTags(LocaleController.formatString("PaymentInfoHint", NUM, this.totalPrice[0], this.currentItemName)), (Object) tLRPC$MessageArr[0], (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$56(TLObject tLObject) {
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
            String str = ((TLRPC$TL_payments_paymentVerificationNeeded) tLObject).url;
            this.webViewUrl = str;
            webView3.loadUrl(str);
        }
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback2 = this.paymentFormCallback;
        if (paymentFormCallback2 != null) {
            paymentFormCallback2.onInvoiceStatusChanged(InvoiceStatus.PENDING);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$57(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_payments_sendPaymentForm, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
        this.paymentStatusSent = true;
        PaymentFormCallback paymentFormCallback2 = this.paymentFormCallback;
        if (paymentFormCallback2 != null) {
            paymentFormCallback2.onInvoiceStatusChanged(InvoiceStatus.FAILED);
        }
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
            TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword = new TLRPC$TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPassword, new PaymentFormActivity$$ExternalSyntheticLambda54(this, obj, tLRPC$TL_account_getPassword), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$63(String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda45(this, tLRPC$TL_error, tLObject, str, tLRPC$TL_account_getPassword));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$62(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (!tLRPC$TL_account_password.has_password) {
                this.passwordOk = false;
                goToNextStep();
            } else {
                Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda40(this, tLRPC$TL_account_password, AndroidUtilities.getStringBytes(str)));
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_getPassword, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$61(TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr) {
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword = new TLRPC$TL_account_getTmpPassword();
        tLRPC$TL_account_getTmpPassword.period = 1800;
        PaymentFormActivity$$ExternalSyntheticLambda56 paymentFormActivity$$ExternalSyntheticLambda56 = new PaymentFormActivity$$ExternalSyntheticLambda56(this, tLRPC$TL_account_getTmpPassword);
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getTmpPassword.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                paymentFormActivity$$ExternalSyntheticLambda56.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getTmpPassword, paymentFormActivity$$ExternalSyntheticLambda56, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        paymentFormActivity$$ExternalSyntheticLambda56.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$60(TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda39(this, tLObject, tLRPC$TL_error, tLRPC$TL_account_getTmpPassword));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$59(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword) {
        showEditDoneProgress(true, false);
        setDonePressed(false);
        if (tLObject != null) {
            this.passwordOk = true;
            UserConfig.getInstance(this.currentAccount).tmpPassword = (TLRPC$TL_account_tmpPassword) tLObject;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            goToNextStep();
        } else if (tLRPC$TL_error.text.equals("PASSWORD_HASH_INVALID")) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
            this.inputFields[1].setText("");
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_getTmpPassword, new Object[0]);
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
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.doneItemAnimation = animatorSet2;
            if (z3) {
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
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.payTextView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.payTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{1.0f})});
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.payTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.payTextView, View.ALPHA, new float[]{1.0f})});
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

    public boolean presentFragment(BaseFragment baseFragment) {
        onPresentFragment(baseFragment);
        return super.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        onPresentFragment(baseFragment);
        return super.presentFragment(baseFragment, z);
    }

    private void onPresentFragment(BaseFragment baseFragment) {
        AndroidUtilities.hideKeyboard(this.fragmentView);
        if (baseFragment instanceof PaymentFormActivity) {
            PaymentFormActivity paymentFormActivity = (PaymentFormActivity) baseFragment;
            paymentFormActivity.paymentFormCallback = this.paymentFormCallback;
            paymentFormActivity.resourcesProvider = this.resourcesProvider;
            paymentFormActivity.needPayAfterTransition = this.needPayAfterTransition;
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
            for (int i = 0; i < this.inputFields.length; i++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[i].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        }
        if (this.radioCells != null) {
            for (int i2 = 0; i2 < this.radioCells.length; i2++) {
                arrayList.add(new ThemeDescription(this.radioCells[i2], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.radioCells[i2], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription((View) this.radioCells[i2], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.radioCells[i2], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
                arrayList.add(new ThemeDescription((View) this.radioCells[i2], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        }
        for (int i3 = 0; i3 < this.headerCell.length; i3++) {
            arrayList.add(new ThemeDescription(this.headerCell[i3], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.headerCell[i3], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        }
        for (ShadowSectionCell themeDescription : this.sectionCell) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        }
        for (int i4 = 0; i4 < this.bottomCell.length; i4++) {
            arrayList.add(new ThemeDescription(this.bottomCell[i4], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.bottomCell[i4], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription((View) this.bottomCell[i4], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        }
        for (int i5 = 0; i5 < this.dividers.size(); i5++) {
            arrayList.add(new ThemeDescription(this.dividers.get(i5), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
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
        for (int i6 = 0; i6 < this.settingsCell.length; i6++) {
            arrayList.add(new ThemeDescription(this.settingsCell[i6], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.settingsCell[i6], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription((View) this.settingsCell[i6], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        }
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText6"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        for (int i7 = 1; i7 < this.detailSettingsCell.length; i7++) {
            arrayList.add(new ThemeDescription(this.detailSettingsCell[i7], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.detailSettingsCell[i7], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.detailSettingsCell[i7], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
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

        public BottomFrameLayout(Context context, TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm) {
            super(context);
            this.progress = (!tLRPC$TL_payments_paymentForm.invoice.recurring || PaymentFormActivity.this.isAcceptTermsChecked) ? 1.0f : 0.0f;
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(PaymentFormActivity.this.getThemedColor("switchTrackBlue"));
            this.paint.setColor(PaymentFormActivity.this.getThemedColor("contacts_inviteBackground"));
            canvas.drawCircle((float) (LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(28.0f) : AndroidUtilities.dp(28.0f)), (float) (-AndroidUtilities.dp(28.0f)), ((float) Math.max(getWidth(), getHeight())) * this.progress, this.paint);
        }

        public void setChecked(boolean z) {
            SpringAnimation springAnimation2 = this.springAnimation;
            if (springAnimation2 != null) {
                springAnimation2.cancel();
            }
            float f = z ? 1.0f : 0.0f;
            if (this.progress != f) {
                SpringAnimation spring = new SpringAnimation(new FloatValueHolder(this.progress * 100.0f)).setSpring(new SpringForce(f * 100.0f).setStiffness(z ? 500.0f : 650.0f).setDampingRatio(1.0f));
                this.springAnimation = spring;
                spring.addUpdateListener(new PaymentFormActivity$BottomFrameLayout$$ExternalSyntheticLambda1(this));
                this.springAnimation.addEndListener(new PaymentFormActivity$BottomFrameLayout$$ExternalSyntheticLambda0(this));
                this.springAnimation.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setChecked$0(DynamicAnimation dynamicAnimation, float f, float f2) {
            this.progress = f / 100.0f;
            if (PaymentFormActivity.this.payTextView != null) {
                PaymentFormActivity.this.payTextView.setAlpha((this.progress * 0.2f) + 0.8f);
            }
            invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setChecked$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (dynamicAnimation == this.springAnimation) {
                this.springAnimation = null;
            }
        }
    }
}
