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
import com.google.android.gms.tasks.OnCompleteListener;
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
import org.telegram.ui.PaymentFormActivity;

public class PaymentFormActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TLRPC$User botUser;
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
    public TLRPC$TL_payments_paymentForm paymentForm;
    private PaymentInfoCell paymentInfoCell;
    /* access modifiers changed from: private */
    public String paymentJson;
    private TLRPC$TL_payments_paymentReceipt paymentReceipt;
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
    /* access modifiers changed from: private */
    public TLRPC$TL_payments_validatedRequestedInfo requestedInfo;
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
    private String totalPriceDecimal;
    /* access modifiers changed from: private */
    public TLRPC$TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    /* access modifiers changed from: private */
    public String webViewUrl;
    /* access modifiers changed from: private */
    public boolean webviewLoading;

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

    static /* synthetic */ boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$createView$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$sendForm$46(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new Runnable(str, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.TelegramWebviewProxy.this.lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$postEvent$0 */
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
        TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$TL_payments_paymentReceipt.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = tLRPC$TL_payments_paymentReceipt.title;
        if (tLRPC$TL_payments_paymentReceipt.info != null) {
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo = new TLRPC$TL_payments_validateRequestedInfo();
            this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
            tLRPC$TL_payments_validateRequestedInfo.peer = getMessagesController().getInputPeer(tLRPC$TL_payments_paymentReceipt.bot_id);
            this.validateRequest.info = tLRPC$TL_payments_paymentReceipt.info;
        }
        this.cardName = tLRPC$TL_payments_paymentReceipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, BaseFragment baseFragment) {
        int i;
        TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm2 = tLRPC$TL_payments_paymentForm;
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
        TLRPC$TL_invoice tLRPC$TL_invoice = tLRPC$TL_payments_paymentForm2.invoice;
        if (tLRPC$TL_invoice.shipping_address_requested || tLRPC$TL_invoice.email_requested || tLRPC$TL_invoice.name_requested || tLRPC$TL_invoice.phone_requested) {
            i = 0;
        } else if (tLRPC$TL_payments_paymentForm2.saved_credentials != null) {
            if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
            }
            i = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
        } else {
            i = 2;
        }
        init(tLRPC$TL_payments_paymentForm, messageObject2, i, (TLRPC$TL_payments_validatedRequestedInfo) null, (TLRPC$TL_shippingOption) null, (Long) null, (String) null, (String) null, (TLRPC$TL_payments_validateRequestedInfo) null, false, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, baseFragment);
    }

    private PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, int i, TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo, TLRPC$TL_shippingOption tLRPC$TL_shippingOption, Long l, String str, String str2, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay, BaseFragment baseFragment) {
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
        init(tLRPC$TL_payments_paymentForm, messageObject2, i, tLRPC$TL_payments_validatedRequestedInfo, tLRPC$TL_shippingOption, l, str, str2, tLRPC$TL_payments_validateRequestedInfo, z, tLRPC$TL_inputPaymentCredentialsGooglePay, baseFragment);
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

    private void init(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, int i, TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo, TLRPC$TL_shippingOption tLRPC$TL_shippingOption, Long l, String str, String str2, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay, BaseFragment baseFragment) {
        this.currentStep = i;
        this.parentFragment = baseFragment;
        this.paymentJson = str;
        this.googlePayCredentials = tLRPC$TL_inputPaymentCredentialsGooglePay;
        this.requestedInfo = tLRPC$TL_payments_validatedRequestedInfo;
        this.paymentForm = tLRPC$TL_payments_paymentForm;
        this.shippingOption = tLRPC$TL_shippingOption;
        this.tipAmount = l;
        this.messageObject = messageObject2;
        this.saveCardInfo = z;
        boolean z2 = false;
        this.isWebView = !"stripe".equals(tLRPC$TL_payments_paymentForm.native_provider) && !"smartglocal".equals(this.paymentForm.native_provider);
        TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$TL_payments_paymentForm.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject2.messageOwner.media.title;
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
        if (str2 == null) {
            TLRPC$TL_paymentSavedCredentialsCard tLRPC$TL_paymentSavedCredentialsCard = tLRPC$TL_payments_paymentForm.saved_credentials;
            if (tLRPC$TL_paymentSavedCredentialsCard != null) {
                this.cardName = tLRPC$TL_paymentSavedCredentialsCard.title;
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
    /* JADX WARNING: type inference failed for: r9v5, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r9v6 */
    /* JADX WARNING: type inference failed for: r9v25 */
    /* JADX WARNING: Can't wrap try/catch for region: R(22:287|288|289|290|291|292|293|294|295|296|297|298|299|300|301|302|303|304|(1:306)(5:307|308|309|310|311)|312|(1:314)(1:315)|316) */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0ad5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0ad6, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0var_, code lost:
        if (r6.email_requested == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x032f, code lost:
        if (r11.email_requested == false) goto L_0x0320;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:292:0x0a93 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:297:0x0a9e */
    /* JADX WARNING: Missing exception handler attribute for start block: B:302:0x0aa9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:310:0x0ac5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x130c  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x13bd  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x13e5  */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x1411  */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x1413  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x143e  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x1475  */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x18b6  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x18d6  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x18f5  */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x190d  */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x1925  */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x196f  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x1975  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x1abf  */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r35) {
        /*
            r34 = this;
            r7 = r34
            r8 = r35
            int r0 = r7.currentStep
            r9 = 6
            r10 = 5
            r11 = 4
            r12 = 3
            r13 = 2
            r14 = 1
            if (r0 != 0) goto L_0x001e
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626854(0x7f0e0b66, float:1.8880956E38)
            java.lang.String r2 = "PaymentShippingInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x001e:
            if (r0 != r14) goto L_0x0030
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r2 = "PaymentShippingMethod"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0030:
            if (r0 != r13) goto L_0x0042
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626812(0x7f0e0b3c, float:1.888087E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0042:
            if (r0 != r12) goto L_0x0054
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626812(0x7f0e0b3c, float:1.888087E38)
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
            r2 = 2131626819(0x7f0e0b43, float:1.8880885E38)
            java.lang.String r3 = "PaymentCheckout"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626819(0x7f0e0b43, float:1.8880885E38)
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
            r2 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            java.lang.String r3 = "PaymentReceipt"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x00b7:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            java.lang.String r2 = "PaymentReceipt"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x00c6:
            if (r0 != r9) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131626836(0x7f0e0b54, float:1.888092E38)
            java.lang.String r2 = "PaymentPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x00d6:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131165470(0x7var_e, float:1.7945158E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r0.setAllowOverlayTitle(r14)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.PaymentFormActivity$1 r1 = new org.telegram.ui.PaymentFormActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            int r1 = r7.currentStep
            r15 = -1
            if (r1 == 0) goto L_0x0102
            if (r1 == r14) goto L_0x0102
            if (r1 == r13) goto L_0x0102
            if (r1 == r12) goto L_0x0102
            if (r1 == r11) goto L_0x0102
            if (r1 != r9) goto L_0x0144
        L_0x0102:
            r1 = 2131165500(0x7var_c, float:1.7945219E38)
            r2 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 2131625228(0x7f0e050c, float:1.8877658E38)
            java.lang.String r4 = "Done"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItemWithWidth(r14, r1, r2, r3)
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
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r2)
            r0.addView(r1, r2)
        L_0x0144:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.fragmentView = r0
            r6 = r0
            android.widget.FrameLayout r6 = (android.widget.FrameLayout) r6
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
            if (r1 != r11) goto L_0x0183
            r1 = 1111490560(0x42400000, float:48.0)
            r22 = 1111490560(0x42400000, float:48.0)
            goto L_0x0186
        L_0x0183:
            r1 = 0
            r22 = 0
        L_0x0186:
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r6.addView(r0, r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.linearLayout2 = r0
            r0.setOrientation(r14)
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
            java.lang.String r10 = "windowBackgroundGrayShadow"
            java.lang.String r23 = "windowBackgroundWhite"
            if (r0 != 0) goto L_0x0937
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x021d }
            java.io.InputStreamReader r11 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x021d }
            android.content.res.Resources r24 = r35.getResources()     // Catch:{ Exception -> 0x021d }
            android.content.res.AssetManager r9 = r24.getAssets()     // Catch:{ Exception -> 0x021d }
            java.lang.String r4 = "countries.txt"
            java.io.InputStream r4 = r9.open(r4)     // Catch:{ Exception -> 0x021d }
            r11.<init>(r4)     // Catch:{ Exception -> 0x021d }
            r0.<init>(r11)     // Catch:{ Exception -> 0x021d }
        L_0x01d8:
            java.lang.String r4 = r0.readLine()     // Catch:{ Exception -> 0x021d }
            if (r4 == 0) goto L_0x0219
            java.lang.String r9 = ";"
            java.lang.String[] r4 = r4.split(r9)     // Catch:{ Exception -> 0x021d }
            java.util.ArrayList<java.lang.String> r9 = r7.countriesArray     // Catch:{ Exception -> 0x021d }
            r11 = r4[r13]     // Catch:{ Exception -> 0x021d }
            r9.add(r5, r11)     // Catch:{ Exception -> 0x021d }
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r7.countriesMap     // Catch:{ Exception -> 0x021d }
            r11 = r4[r13]     // Catch:{ Exception -> 0x021d }
            r15 = r4[r5]     // Catch:{ Exception -> 0x021d }
            r9.put(r11, r15)     // Catch:{ Exception -> 0x021d }
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r7.codesMap     // Catch:{ Exception -> 0x021d }
            r11 = r4[r5]     // Catch:{ Exception -> 0x021d }
            r15 = r4[r13]     // Catch:{ Exception -> 0x021d }
            r9.put(r11, r15)     // Catch:{ Exception -> 0x021d }
            r9 = r4[r14]     // Catch:{ Exception -> 0x021d }
            r11 = r4[r13]     // Catch:{ Exception -> 0x021d }
            r1.put(r9, r11)     // Catch:{ Exception -> 0x021d }
            int r9 = r4.length     // Catch:{ Exception -> 0x021d }
            if (r9 <= r12) goto L_0x0210
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r7.phoneFormatMap     // Catch:{ Exception -> 0x021d }
            r11 = r4[r5]     // Catch:{ Exception -> 0x021d }
            r15 = r4[r12]     // Catch:{ Exception -> 0x021d }
            r9.put(r11, r15)     // Catch:{ Exception -> 0x021d }
        L_0x0210:
            r9 = r4[r14]     // Catch:{ Exception -> 0x021d }
            r4 = r4[r13]     // Catch:{ Exception -> 0x021d }
            r6.put(r9, r4)     // Catch:{ Exception -> 0x021d }
            r15 = -1
            goto L_0x01d8
        L_0x0219:
            r0.close()     // Catch:{ Exception -> 0x021d }
            goto L_0x0221
        L_0x021d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0221:
            java.util.ArrayList<java.lang.String> r0 = r7.countriesArray
            org.telegram.ui.-$$Lambda$Ds7dtVnGrflEw4-LvNOxA0cDT4Y r4 = org.telegram.ui.$$Lambda$Ds7dtVnGrflEw4LvNOxA0cDT4Y.INSTANCE
            java.util.Collections.sort(r0, r4)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r7.inputFields = r0
            r0 = 0
        L_0x022f:
            r4 = 10
            if (r0 >= r4) goto L_0x0773
            if (r0 != 0) goto L_0x026c
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r9 = new org.telegram.ui.Cells.HeaderCell
            r9.<init>(r8)
            r4[r5] = r9
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r9)
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r5]
            r9 = 2131626848(0x7f0e0b60, float:1.8880944E38)
            java.lang.String r11 = "PaymentShippingAddress"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r4.setText(r9)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r9 = r7.headerCell
            r9 = r9[r5]
            r11 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r11)
            r4.addView(r9, r13)
            r4 = 8
            r13 = -1
            goto L_0x02be
        L_0x026c:
            r4 = 6
            r11 = -2
            r15 = -1
            if (r0 != r4) goto L_0x02bb
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
            r9.<init>(r8)
            r4[r5] = r9
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r9 = r7.sectionCell
            r9 = r9[r5]
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r11)
            r4.addView(r9, r13)
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r9 = new org.telegram.ui.Cells.HeaderCell
            r9.<init>(r8)
            r4[r14] = r9
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r14]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r9)
            org.telegram.ui.Cells.HeaderCell[] r4 = r7.headerCell
            r4 = r4[r14]
            r9 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
            java.lang.String r11 = "PaymentShippingReceiver"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r4.setText(r9)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r9 = r7.headerCell
            r9 = r9[r14]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r4.addView(r9, r15)
            goto L_0x02bc
        L_0x02bb:
            r13 = -1
        L_0x02bc:
            r4 = 8
        L_0x02be:
            if (r0 != r4) goto L_0x02e1
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r8)
            r4.setClipChildren(r5)
            r4.setOrientation(r5)
            android.widget.LinearLayout r9 = r7.linearLayout2
            r11 = 50
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r9.addView(r4, r15)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r9)
        L_0x02dd:
            r9 = 9
            goto L_0x0351
        L_0x02e1:
            r4 = 9
            if (r0 != r4) goto L_0x02f2
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r9 = 8
            r4 = r4[r9]
            android.view.ViewParent r4 = r4.getParent()
            android.view.ViewGroup r4 = (android.view.ViewGroup) r4
            goto L_0x02dd
        L_0x02f2:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r8)
            r4.setClipChildren(r5)
            android.widget.LinearLayout r9 = r7.linearLayout2
            r11 = 50
            r13 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r9.addView(r4, r15)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r9)
            r9 = 5
            if (r0 == r9) goto L_0x0312
            r9 = 1
            goto L_0x0313
        L_0x0312:
            r9 = 0
        L_0x0313:
            if (r9 == 0) goto L_0x0332
            r11 = 7
            if (r0 != r11) goto L_0x0322
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r11 = r11.invoice
            boolean r11 = r11.phone_requested
            if (r11 != 0) goto L_0x0322
        L_0x0320:
            r9 = 0
            goto L_0x0332
        L_0x0322:
            r11 = 6
            if (r0 != r11) goto L_0x0332
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r11 = r11.invoice
            boolean r13 = r11.phone_requested
            if (r13 != 0) goto L_0x0332
            boolean r11 = r11.email_requested
            if (r11 != 0) goto L_0x0332
            goto L_0x0320
        L_0x0332:
            if (r9 == 0) goto L_0x02dd
            org.telegram.ui.PaymentFormActivity$2 r9 = new org.telegram.ui.PaymentFormActivity$2
            r9.<init>(r8)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r9.setBackgroundColor(r11)
            java.util.ArrayList<android.view.View> r11 = r7.dividers
            r11.add(r9)
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            r13 = 83
            r15 = -1
            r11.<init>(r15, r14, r13)
            r4.addView(r9, r11)
            goto L_0x02dd
        L_0x0351:
            if (r0 != r9) goto L_0x035d
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            org.telegram.ui.Components.HintEditText r11 = new org.telegram.ui.Components.HintEditText
            r11.<init>(r8)
            r9[r0] = r11
            goto L_0x0366
        L_0x035d:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r11 = new org.telegram.ui.Components.EditTextBoldCursor
            r11.<init>(r8)
            r9[r0] = r11
        L_0x0366:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r9.setTag(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 1098907648(0x41800000, float:16.0)
            r9.setTextSize(r14, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            java.lang.String r11 = "windowBackgroundWhiteHintText"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r9.setHintTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r9.setTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 0
            r9.setBackgroundDrawable(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r9.setCursorColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 1101004800(0x41a00000, float:20.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r9.setCursorSize(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 1069547520(0x3fCLASSNAME, float:1.5)
            r9.setCursorWidth(r11)
            r9 = 4
            if (r0 != r9) goto L_0x03d1
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$IfUWsdY6A3YdC7z4Rkp3xdEZ1P8 r11 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$IfUWsdY6A3YdC7z4Rkp3xdEZ1P8
            r11.<init>()
            r9.setOnTouchListener(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r9.setInputType(r5)
        L_0x03d1:
            r9 = 9
            if (r0 == r9) goto L_0x03ef
            r9 = 8
            if (r0 != r9) goto L_0x03da
            goto L_0x03ef
        L_0x03da:
            r9 = 7
            if (r0 != r9) goto L_0x03e5
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r9.setInputType(r14)
            goto L_0x03f6
        L_0x03e5:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 16385(0x4001, float:2.296E-41)
            r9.setInputType(r11)
            goto L_0x03f6
        L_0x03ef:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r9.setInputType(r12)
        L_0x03f6:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 268435461(0x10000005, float:2.5243564E-29)
            r9.setImeOptions(r11)
            switch(r0) {
                case 0: goto L_0x0515;
                case 1: goto L_0x04f1;
                case 2: goto L_0x04cd;
                case 3: goto L_0x04a9;
                case 4: goto L_0x0470;
                case 5: goto L_0x044b;
                case 6: goto L_0x0428;
                case 7: goto L_0x0405;
                default: goto L_0x0403;
            }
        L_0x0403:
            goto L_0x0538
        L_0x0405:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626853(0x7f0e0b65, float:1.8880954E38)
            java.lang.String r13 = "PaymentShippingEmailPlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            java.lang.String r9 = r9.email
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            r11.setText(r9)
            goto L_0x0538
        L_0x0428:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626856(0x7f0e0b68, float:1.888096E38)
            java.lang.String r13 = "PaymentShippingName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            java.lang.String r9 = r9.name
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            r11.setText(r9)
            goto L_0x0538
        L_0x044b:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626862(0x7f0e0b6e, float:1.8880972E38)
            java.lang.String r13 = "PaymentShippingZipPlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r9 = r9.post_code
            r11.setText(r9)
            goto L_0x0538
        L_0x0470:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626852(0x7f0e0b64, float:1.8880952E38)
            java.lang.String r13 = "PaymentShippingCountry"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x0538
            java.lang.String r9 = r9.country_iso2
            java.lang.Object r9 = r1.get(r9)
            java.lang.String r9 = (java.lang.String) r9
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r11 = r11.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r11 = r11.shipping_address
            java.lang.String r11 = r11.country_iso2
            r7.countryName = r11
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r7.inputFields
            r13 = r13[r0]
            if (r9 == 0) goto L_0x04a3
            goto L_0x04a4
        L_0x04a3:
            r9 = r11
        L_0x04a4:
            r13.setText(r9)
            goto L_0x0538
        L_0x04a9:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626861(0x7f0e0b6d, float:1.888097E38)
            java.lang.String r13 = "PaymentShippingStatePlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r9 = r9.state
            r11.setText(r9)
            goto L_0x0538
        L_0x04cd:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626851(0x7f0e0b63, float:1.888095E38)
            java.lang.String r13 = "PaymentShippingCityPlaceholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r9 = r9.city
            r11.setText(r9)
            goto L_0x0538
        L_0x04f1:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626850(0x7f0e0b62, float:1.8880948E38)
            java.lang.String r13 = "PaymentShippingAddress2Placeholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r9 = r9.street_line2
            r11.setText(r9)
            goto L_0x0538
        L_0x0515:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 2131626849(0x7f0e0b61, float:1.8880946E38)
            java.lang.String r13 = "PaymentShippingAddress1Placeholder"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.setHint(r11)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r9 = r9.saved_info
            if (r9 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$TL_postAddress r9 = r9.shipping_address
            if (r9 == 0) goto L_0x0538
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            java.lang.String r9 = r9.street_line1
            r11.setText(r9)
        L_0x0538:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r11 = r9[r0]
            r9 = r9[r0]
            int r9 = r9.length()
            r11.setSelection(r9)
            r9 = 8
            if (r0 != r9) goto L_0x05bf
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r8)
            r7.textView = r9
            java.lang.String r11 = "+"
            r9.setText(r11)
            android.widget.TextView r9 = r7.textView
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r9.setTextColor(r11)
            android.widget.TextView r9 = r7.textView
            r11 = 1098907648(0x41800000, float:16.0)
            r9.setTextSize(r14, r11)
            android.widget.TextView r9 = r7.textView
            r25 = -2
            r26 = -2
            r27 = 1101529088(0x41a80000, float:21.0)
            r28 = 1094713344(0x41400000, float:12.0)
            r29 = 0
            r30 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r25, r26, r27, r28, r29, r30)
            r4.addView(r9, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r9.setPadding(r11, r5, r5, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 19
            r9.setGravity(r11)
            android.text.InputFilter[] r9 = new android.text.InputFilter[r14]
            android.text.InputFilter$LengthFilter r11 = new android.text.InputFilter$LengthFilter
            r13 = 5
            r11.<init>(r13)
            r9[r5] = r11
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r7.inputFields
            r11 = r11[r0]
            r11.setFilters(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r25 = 55
            r27 = 0
            r29 = 1101529088(0x41a80000, float:21.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r25, r26, r27, r28, r29, r30)
            r4.addView(r9, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$3 r9 = new org.telegram.ui.PaymentFormActivity$3
            r9.<init>()
            r4.addTextChangedListener(r9)
            goto L_0x062b
        L_0x05bf:
            r9 = 9
            if (r0 != r9) goto L_0x05f7
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r9.setPadding(r5, r5, r5, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 19
            r9.setGravity(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r25 = -1
            r26 = -2
            r27 = 0
            r28 = 1094713344(0x41400000, float:12.0)
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r25, r26, r27, r28, r29, r30)
            r4.addView(r9, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$4 r9 = new org.telegram.ui.PaymentFormActivity$4
            r9.<init>()
            r4.addTextChangedListener(r9)
            goto L_0x062b
        L_0x05f7:
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r9.setPadding(r5, r5, r5, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x060e
            r11 = 5
            goto L_0x060f
        L_0x060e:
            r11 = 3
        L_0x060f:
            r9.setGravity(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r7.inputFields
            r9 = r9[r0]
            r25 = -1
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 51
            r28 = 1101529088(0x41a80000, float:21.0)
            r29 = 1094713344(0x41400000, float:12.0)
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r4.addView(r9, r11)
        L_0x062b:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$Olw4mziI4Y7UZ6FQRVf9ETMmZig r9 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$Olw4mziI4Y7UZ6FQRVf9ETMmZig
            r9.<init>()
            r4.setOnEditorActionListener(r9)
            r4 = 9
            if (r0 != r4) goto L_0x076d
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r9 = r4.email_to_provider
            if (r9 != 0) goto L_0x0662
            boolean r4 = r4.phone_to_provider
            if (r4 == 0) goto L_0x0648
            goto L_0x0662
        L_0x0648:
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
            r9.<init>(r8)
            r4[r14] = r9
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r9 = r7.sectionCell
            r9 = r9[r14]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r4.addView(r9, r15)
            goto L_0x0702
        L_0x0662:
            r4 = 0
            r9 = 0
        L_0x0664:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            int r11 = r11.size()
            if (r9 >= r11) goto L_0x0684
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            java.lang.Object r11 = r11.get(r9)
            org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
            int r13 = r11.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            int r15 = r15.provider_id
            if (r13 != r15) goto L_0x0681
            r4 = r11
        L_0x0681:
            int r9 = r9 + 1
            goto L_0x0664
        L_0x0684:
            if (r4 == 0) goto L_0x068f
            java.lang.String r9 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r9, r4)
            goto L_0x0690
        L_0x068f:
            r4 = r3
        L_0x0690:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r11 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r11.<init>(r8)
            r9[r14] = r11
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r14]
            r11 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r11, (java.lang.String) r10)
            r9.setBackgroundDrawable(r13)
            android.widget.LinearLayout r9 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r11 = r7.bottomCell
            r11 = r11[r14]
            r13 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r13)
            r9.addView(r11, r12)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r9 = r9.invoice
            boolean r11 = r9.email_to_provider
            if (r11 == 0) goto L_0x06d7
            boolean r9 = r9.phone_to_provider
            if (r9 == 0) goto L_0x06d7
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r14]
            r11 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.Object[] r12 = new java.lang.Object[r14]
            r12[r5] = r4
            java.lang.String r4 = "PaymentPhoneEmailToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r12)
            r9.setText(r4)
            goto L_0x0702
        L_0x06d7:
            if (r11 == 0) goto L_0x06ee
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r14]
            r11 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            java.lang.Object[] r12 = new java.lang.Object[r14]
            r12[r5] = r4
            java.lang.String r4 = "PaymentEmailToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r12)
            r9.setText(r4)
            goto L_0x0702
        L_0x06ee:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r14]
            r11 = 2131626845(0x7f0e0b5d, float:1.8880938E38)
            java.lang.Object[] r12 = new java.lang.Object[r14]
            r12[r5] = r4
            java.lang.String r4 = "PaymentPhoneToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r11, r12)
            r9.setText(r4)
        L_0x0702:
            org.telegram.ui.Cells.TextCheckCell r4 = new org.telegram.ui.Cells.TextCheckCell
            r4.<init>(r8)
            r7.checkCell1 = r4
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r4.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            r9 = 2131626859(0x7f0e0b6b, float:1.8880966E38)
            java.lang.String r11 = "PaymentShippingSave"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            boolean r11 = r7.saveShippingInfo
            r4.setTextAndCheck(r9, r11, r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r9 = r7.checkCell1
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r11)
            r4.addView(r9, r13)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$PexWzZmxWLN1J6v8efPEtLRkUHI r9 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$PexWzZmxWLN1J6v8efPEtLRkUHI
            r9.<init>()
            r4.setOnClickListener(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r9.<init>(r8)
            r4[r5] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r5]
            r9 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r9, (java.lang.String) r10)
            r4.setBackgroundDrawable(r11)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r5]
            r9 = 2131626860(0x7f0e0b6c, float:1.8880968E38)
            java.lang.String r11 = "PaymentShippingSaveInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r4.setText(r9)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r7.bottomCell
            r9 = r9[r5]
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r11)
            r4.addView(r9, r13)
        L_0x076d:
            int r0 = r0 + 1
            r12 = 3
            r13 = 2
            goto L_0x022f
        L_0x0773:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x078c
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 6
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
            goto L_0x078e
        L_0x078c:
            r1 = 8
        L_0x078e:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x07a3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x07a3:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x07b9
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x07b9:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r1 = r0.phone_requested
            if (r1 == 0) goto L_0x07ce
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r9 = 9
            r0 = r0[r9]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x07f3
        L_0x07ce:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            boolean r2 = r0.email_requested
            if (r2 == 0) goto L_0x07de
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x07f3
        L_0x07de:
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x07eb
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 6
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x07f3
        L_0x07eb:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 5
            r0 = r0[r2]
            r0.setImeOptions(r1)
        L_0x07f3:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = r0[r14]
            if (r1 == 0) goto L_0x0814
            r0 = r0[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x080f
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x080f
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x080c
            goto L_0x080f
        L_0x080c:
            r1 = 8
            goto L_0x0810
        L_0x080f:
            r1 = 0
        L_0x0810:
            r0.setVisibility(r1)
            goto L_0x0834
        L_0x0814:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r1 = r0[r14]
            if (r1 == 0) goto L_0x0834
            r0 = r0[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0830
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0830
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x082d
            goto L_0x0830
        L_0x082d:
            r1 = 8
            goto L_0x0831
        L_0x0830:
            r1 = 0
        L_0x0831:
            r0.setVisibility(r1)
        L_0x0834:
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r0 = r0[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x084c
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x084c
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0849
            goto L_0x084c
        L_0x0849:
            r1 = 8
            goto L_0x084d
        L_0x084c:
            r1 = 0
        L_0x084d:
            r0.setVisibility(r1)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x08ba
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r0 = r0[r5]
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r5]
            r0.setVisibility(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r5]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r14]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 2
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 3
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 4
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 5
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x08ba:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08d2
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08d2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r7.fillNumber(r0)
            goto L_0x08d6
        L_0x08d2:
            r1 = 0
            r7.fillNumber(r1)
        L_0x08d6:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 8
            r0 = r0[r1]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x1bf5
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r0.invoice
            boolean r1 = r1.phone_requested
            if (r1 == 0) goto L_0x1bf5
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08f6
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x1bf5
        L_0x08f6:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x090c }
            java.lang.String r1 = "phone"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ Exception -> 0x090c }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x090c }
            if (r0 == 0) goto L_0x0910
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x090c }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x090c }
            r2 = r0
            goto L_0x0911
        L_0x090c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0910:
            r2 = 0
        L_0x0911:
            if (r2 == 0) goto L_0x1bf5
            java.lang.Object r0 = r6.get(r2)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x1bf5
            java.util.ArrayList<java.lang.String> r1 = r7.countriesArray
            int r1 = r1.indexOf(r0)
            r2 = -1
            if (r1 == r2) goto L_0x1bf5
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r2 = 8
            r1 = r1[r2]
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r7.countriesMap
            java.lang.Object r0 = r2.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r1.setText(r0)
            goto L_0x1bf5
        L_0x0937:
            r1 = 2
            r9 = 9
            if (r0 != r1) goto L_0x0e78
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0970
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x096c }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm     // Catch:{ Exception -> 0x096c }
            org.telegram.tgnet.TLRPC$TL_dataJSON r1 = r1.native_params     // Catch:{ Exception -> 0x096c }
            java.lang.String r1 = r1.data     // Catch:{ Exception -> 0x096c }
            r0.<init>(r1)     // Catch:{ Exception -> 0x096c }
            java.lang.String r1 = "google_pay_public_key"
            java.lang.String r1 = r0.optString(r1)     // Catch:{ Exception -> 0x096c }
            boolean r4 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x096c }
            if (r4 != 0) goto L_0x095b
            r7.googlePayPublicKey = r1     // Catch:{ Exception -> 0x096c }
        L_0x095b:
            java.lang.String r1 = "acquirer_bank_country"
            java.lang.String r1 = r0.optString(r1)     // Catch:{ Exception -> 0x096c }
            r7.googlePayCountryCode = r1     // Catch:{ Exception -> 0x096c }
            java.lang.String r1 = "gpay_parameters"
            org.json.JSONObject r0 = r0.optJSONObject(r1)     // Catch:{ Exception -> 0x096c }
            r7.googlePayParameters = r0     // Catch:{ Exception -> 0x096c }
            goto L_0x0970
        L_0x096c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0970:
            boolean r0 = r7.isWebView
            if (r0 == 0) goto L_0x0a79
            java.lang.String r0 = r7.googlePayPublicKey
            if (r0 != 0) goto L_0x097c
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x097f
        L_0x097c:
            r34.initGooglePay(r35)
        L_0x097f:
            r34.createGooglePayButton(r35)
            android.widget.LinearLayout r0 = r7.linearLayout2
            android.widget.FrameLayout r1 = r7.googlePayContainer
            r2 = 50
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            r7.webviewLoading = r14
            r7.showEditDoneProgress(r14, r14)
            org.telegram.ui.Components.ContextProgressView r0 = r7.progressView
            r0.setVisibility(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r0.setEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            android.view.View r0 = r0.getContentView()
            r1 = 4
            r0.setVisibility(r1)
            org.telegram.ui.PaymentFormActivity$5 r0 = new org.telegram.ui.PaymentFormActivity$5
            r0.<init>(r8)
            r7.webView = r0
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setJavaScriptEnabled(r14)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r14)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x09d8
            android.webkit.WebView r1 = r7.webView
            android.webkit.WebSettings r1 = r1.getSettings()
            r1.setMixedContentMode(r5)
            android.webkit.CookieManager r1 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r2 = r7.webView
            r1.setAcceptThirdPartyCookies(r2, r14)
        L_0x09d8:
            r1 = 17
            if (r0 < r1) goto L_0x09e9
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r1 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r2 = 0
            r1.<init>()
            java.lang.String r2 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r1, r2)
        L_0x09e9:
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
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextCheckCell r0 = r7.checkCell1
            r1 = 2131626815(0x7f0e0b3f, float:1.8880877E38)
            java.lang.String r2 = "PaymentCardSavePaymentInformation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            boolean r2 = r7.saveCardInfo
            r0.setTextAndCheck(r1, r2, r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r1 = r7.checkCell1
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r4)
            org.telegram.ui.Cells.TextCheckCell r0 = r7.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$cvL2PmrtAz-lhXkk9DISaHjZOls r1 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$cvL2PmrtAz-lhXkk9DISaHjZOls
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r0[r5] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r5]
            r1 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            r34.updateSavePaymentField()
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            goto L_0x1bf5
        L_0x0a79:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0ad9
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0ad5 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm     // Catch:{ Exception -> 0x0ad5 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r1 = r1.native_params     // Catch:{ Exception -> 0x0ad5 }
            java.lang.String r1 = r1.data     // Catch:{ Exception -> 0x0ad5 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0ad5 }
            java.lang.String r1 = "need_country"
            boolean r1 = r0.getBoolean(r1)     // Catch:{ Exception -> 0x0a93 }
            r7.need_card_country = r1     // Catch:{ Exception -> 0x0a93 }
            goto L_0x0a95
        L_0x0a93:
            r7.need_card_country = r5     // Catch:{ Exception -> 0x0ad5 }
        L_0x0a95:
            java.lang.String r1 = "need_zip"
            boolean r1 = r0.getBoolean(r1)     // Catch:{ Exception -> 0x0a9e }
            r7.need_card_postcode = r1     // Catch:{ Exception -> 0x0a9e }
            goto L_0x0aa0
        L_0x0a9e:
            r7.need_card_postcode = r5     // Catch:{ Exception -> 0x0ad5 }
        L_0x0aa0:
            java.lang.String r1 = "need_cardholder_name"
            boolean r1 = r0.getBoolean(r1)     // Catch:{ Exception -> 0x0aa9 }
            r7.need_card_name = r1     // Catch:{ Exception -> 0x0aa9 }
            goto L_0x0aab
        L_0x0aa9:
            r7.need_card_name = r5     // Catch:{ Exception -> 0x0ad5 }
        L_0x0aab:
            java.lang.String r1 = "public_token"
            boolean r1 = r0.has(r1)     // Catch:{ Exception -> 0x0ad5 }
            if (r1 == 0) goto L_0x0abc
            java.lang.String r1 = "public_token"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0ad5 }
            r7.providerApiKey = r1     // Catch:{ Exception -> 0x0ad5 }
            goto L_0x0ac7
        L_0x0abc:
            java.lang.String r1 = "publishable_key"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0ac5 }
            r7.providerApiKey = r1     // Catch:{ Exception -> 0x0ac5 }
            goto L_0x0ac7
        L_0x0ac5:
            r7.providerApiKey = r3     // Catch:{ Exception -> 0x0ad5 }
        L_0x0ac7:
            java.lang.String r1 = "google_pay_hidden"
            boolean r0 = r0.optBoolean(r1, r5)     // Catch:{ Exception -> 0x0ad5 }
            if (r0 != 0) goto L_0x0ad1
            r0 = 1
            goto L_0x0ad2
        L_0x0ad1:
            r0 = 0
        L_0x0ad2:
            r7.initGooglePay = r0     // Catch:{ Exception -> 0x0ad5 }
            goto L_0x0ad9
        L_0x0ad5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0ad9:
            boolean r0 = r7.initGooglePay
            if (r0 == 0) goto L_0x0af8
            java.lang.String r0 = r7.providerApiKey
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0af1
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.lang.String r0 = r0.native_provider
            java.lang.String r1 = "stripe"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0af5
        L_0x0af1:
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0af8
        L_0x0af5:
            r34.initGooglePay(r35)
        L_0x0af8:
            r1 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x0afe:
            if (r0 >= r1) goto L_0x0e42
            if (r0 != 0) goto L_0x0b36
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r1[r5] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r5]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r5]
            r3 = 2131626818(0x7f0e0b42, float:1.8880883E38)
            java.lang.String r4 = "PaymentCardTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
            goto L_0x0b6c
        L_0x0b36:
            r1 = 4
            if (r0 != r1) goto L_0x0b6c
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r14]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r14]
            r3 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r4 = "PaymentBillingAddress"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r14]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
        L_0x0b6c:
            r1 = 3
            if (r0 == r1) goto L_0x0b7b
            r1 = 5
            if (r0 == r1) goto L_0x0b7b
            r1 = 4
            if (r0 != r1) goto L_0x0b79
            boolean r1 = r7.need_card_postcode
            if (r1 == 0) goto L_0x0b7b
        L_0x0b79:
            r1 = 1
            goto L_0x0b7c
        L_0x0b7b:
            r1 = 0
        L_0x0b7c:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r8)
            r3.setClipChildren(r5)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r6 = 50
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r3, r11)
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
            r4.setTextSize(r14, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 0
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r2)
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
            if (r0 != r4) goto L_0x0CLASSNAME
            android.text.InputFilter[] r6 = new android.text.InputFilter[r14]
            android.text.InputFilter$LengthFilter r9 = new android.text.InputFilter$LengthFilter
            r9.<init>(r4)
            r6[r5] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.setFilters(r6)
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
            goto L_0x0c6a
        L_0x0CLASSNAME:
            if (r0 != 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 3
            r4.setInputType(r6)
            goto L_0x0c6a
        L_0x0CLASSNAME:
            r4 = 4
            if (r0 != r4) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$ufBXhrNbvJlVLqcHkReQdB857HQ r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$ufBXhrNbvJlVLqcHkReQdB857HQ
            r6.<init>()
            r4.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.setInputType(r5)
            goto L_0x0c6a
        L_0x0CLASSNAME:
            if (r0 != r14) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 16386(0x4002, float:2.2962E-41)
            r4.setInputType(r6)
            goto L_0x0c6a
        L_0x0CLASSNAME:
            r4 = 2
            if (r0 != r4) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 4097(0x1001, float:5.741E-42)
            r4.setInputType(r6)
            goto L_0x0c6a
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 16385(0x4001, float:2.296E-41)
            r4.setInputType(r6)
        L_0x0c6a:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r6)
            if (r0 == 0) goto L_0x0cda
            if (r0 == r14) goto L_0x0cc9
            r4 = 2
            if (r0 == r4) goto L_0x0cb8
            r4 = 3
            if (r0 == r4) goto L_0x0ca7
            r4 = 4
            if (r0 == r4) goto L_0x0CLASSNAME
            r4 = 5
            if (r0 == r4) goto L_0x0CLASSNAME
            goto L_0x0cea
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626862(0x7f0e0b6e, float:1.8880972E38)
            java.lang.String r9 = "PaymentShippingZipPlaceholder"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0cea
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626852(0x7f0e0b64, float:1.8880952E38)
            java.lang.String r9 = "PaymentShippingCountry"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0cea
        L_0x0ca7:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
            java.lang.String r9 = "PaymentCardCvv"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0cea
        L_0x0cb8:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626813(0x7f0e0b3d, float:1.8880873E38)
            java.lang.String r9 = "PaymentCardName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0cea
        L_0x0cc9:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626811(0x7f0e0b3b, float:1.8880869E38)
            java.lang.String r9 = "PaymentCardExpireDate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
            goto L_0x0cea
        L_0x0cda:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 2131626814(0x7f0e0b3e, float:1.8880875E38)
            java.lang.String r9 = "PaymentCardNumber"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setHint(r6)
        L_0x0cea:
            if (r0 != 0) goto L_0x0cf9
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$7 r6 = new org.telegram.ui.PaymentFormActivity$7
            r6.<init>()
            r4.addTextChangedListener(r6)
            goto L_0x0d07
        L_0x0cf9:
            if (r0 != r14) goto L_0x0d07
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$8 r6 = new org.telegram.ui.PaymentFormActivity$8
            r6.<init>()
            r4.addTextChangedListener(r6)
        L_0x0d07:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r5, r5, r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0d1e
            r6 = 5
            goto L_0x0d1f
        L_0x0d1e:
            r6 = 3
        L_0x0d1f:
            r4.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r26 = -1
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 51
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r3.addView(r4, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$xR8Gbm-NkgnmgJUOUHnvYiFZMuY r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$xR8Gbm-NkgnmgJUOUHnvYiFZMuY
            r6.<init>()
            r4.setOnEditorActionListener(r6)
            r4 = 3
            if (r0 != r4) goto L_0x0d64
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            r6.<init>(r8)
            r4[r5] = r6
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r5]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r4.addView(r6, r12)
            goto L_0x0e02
        L_0x0d64:
            r4 = 5
            r9 = -2
            r11 = -1
            if (r0 != r4) goto L_0x0ddf
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r6 = new org.telegram.ui.Cells.ShadowSectionCell
            r6.<init>(r8)
            r12 = 2
            r4[r12] = r6
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r6 = r7.sectionCell
            r6 = r6[r12]
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r4.addView(r6, r12)
            org.telegram.ui.Cells.TextCheckCell r4 = new org.telegram.ui.Cells.TextCheckCell
            r4.<init>(r8)
            r7.checkCell1 = r4
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r4.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            r6 = 2131626815(0x7f0e0b3f, float:1.8880877E38)
            java.lang.String r9 = "PaymentCardSavePaymentInformation"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            boolean r9 = r7.saveCardInfo
            r4.setTextAndCheck(r6, r9, r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r6 = r7.checkCell1
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r4.addView(r6, r12)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$tYBCHRD5qJE9gLUKdrPoOZY19ls r6 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$tYBCHRD5qJE9gLUKdrPoOZY19ls
            r6.<init>()
            r4.setOnClickListener(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r6.<init>(r8)
            r4[r5] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r5]
            r6 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r4.setBackgroundDrawable(r9)
            r34.updateSavePaymentField()
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r5]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r4.addView(r6, r12)
            goto L_0x0e02
        L_0x0ddf:
            if (r0 != 0) goto L_0x0e02
            r34.createGooglePayButton(r35)
            android.widget.FrameLayout r4 = r7.googlePayContainer
            r26 = -2
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0df0
            r6 = 3
            goto L_0x0df1
        L_0x0df0:
            r6 = 5
        L_0x0df1:
            r28 = r6 | 16
            r29 = 0
            r30 = 0
            r31 = 1082130432(0x40800000, float:4.0)
            r32 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r3.addView(r4, r6)
        L_0x0e02:
            if (r1 == 0) goto L_0x0e20
            org.telegram.ui.PaymentFormActivity$9 r1 = new org.telegram.ui.PaymentFormActivity$9
            r1.<init>(r8)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r4)
            java.util.ArrayList<android.view.View> r4 = r7.dividers
            r4.add(r1)
            android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
            r6 = 83
            r9 = -1
            r4.<init>(r9, r14, r6)
            r3.addView(r1, r4)
        L_0x0e20:
            r1 = 4
            if (r0 != r1) goto L_0x0e2b
            boolean r1 = r7.need_card_country
            if (r1 == 0) goto L_0x0e28
            goto L_0x0e2b
        L_0x0e28:
            r1 = 8
            goto L_0x0e3a
        L_0x0e2b:
            r1 = 5
            if (r0 != r1) goto L_0x0e32
            boolean r1 = r7.need_card_postcode
            if (r1 == 0) goto L_0x0e28
        L_0x0e32:
            r1 = 2
            if (r0 != r1) goto L_0x0e3d
            boolean r1 = r7.need_card_name
            if (r1 != 0) goto L_0x0e3d
            goto L_0x0e28
        L_0x0e3a:
            r3.setVisibility(r1)
        L_0x0e3d:
            int r0 = r0 + 1
            r1 = 6
            goto L_0x0afe
        L_0x0e42:
            boolean r0 = r7.need_card_country
            if (r0 != 0) goto L_0x0e5a
            boolean r0 = r7.need_card_postcode
            if (r0 != 0) goto L_0x0e5a
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r0 = r0[r14]
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r5]
            r0.setVisibility(r1)
        L_0x0e5a:
            boolean r0 = r7.need_card_postcode
            if (r0 == 0) goto L_0x0e6b
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 5
            r0 = r0[r1]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x1bf5
        L_0x0e6b:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 3
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x1bf5
        L_0x0e78:
            if (r0 != r14) goto L_0x0f1a
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r1 = new org.telegram.ui.Cells.RadioCell[r0]
            r7.radioCells = r1
            r1 = 0
        L_0x0e87:
            if (r1 >= r0) goto L_0x0ef2
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r2 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r2 = r2.shipping_options
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = (org.telegram.tgnet.TLRPC$TL_shippingOption) r2
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
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r3.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            r4 = 2
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r4 = r2.prices
            java.lang.String r4 = r7.getTotalPriceString(r4)
            r6[r5] = r4
            java.lang.String r2 = r2.title
            r6[r14] = r2
            java.lang.String r2 = "%s - %s"
            java.lang.String r2 = java.lang.String.format(r2, r6)
            if (r1 != 0) goto L_0x0ecf
            r4 = 1
            goto L_0x0ed0
        L_0x0ecf:
            r4 = 0
        L_0x0ed0:
            int r6 = r0 + -1
            if (r1 == r6) goto L_0x0ed6
            r6 = 1
            goto L_0x0ed7
        L_0x0ed6:
            r6 = 0
        L_0x0ed7:
            r3.setText(r2, r4, r6)
            org.telegram.ui.Cells.RadioCell[] r2 = r7.radioCells
            r2 = r2[r1]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$XzomBkzVnAnjaV6SN-Js2LQ2of8 r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$XzomBkzVnAnjaV6SN-Js2LQ2of8
            r3.<init>()
            r2.setOnClickListener(r3)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            r2.addView(r3)
            int r1 = r1 + 1
            goto L_0x0e87
        L_0x0ef2:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r0[r5] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r5]
            r1 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            goto L_0x1bf5
        L_0x0f1a:
            r1 = 3
            if (r0 != r1) goto L_0x115b
            r1 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x0var_:
            if (r0 >= r1) goto L_0x1bf5
            if (r0 != 0) goto L_0x0f5b
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r1[r5] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r5]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r5]
            r3 = 2131626818(0x7f0e0b42, float:1.8880883E38)
            java.lang.String r4 = "PaymentCardTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
            goto L_0x0f5c
        L_0x0f5b:
            r6 = -1
        L_0x0f5c:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r1.setClipChildren(r5)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r4 = 50
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r3.addView(r1, r9)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            if (r0 == r14) goto L_0x0f7a
            r3 = 1
            goto L_0x0f7b
        L_0x0f7a:
            r3 = 0
        L_0x0f7b:
            r4 = 7
            if (r3 == 0) goto L_0x0f9a
            if (r0 != r4) goto L_0x0f8a
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r6 = r6.phone_requested
            if (r6 != 0) goto L_0x0f8a
        L_0x0var_:
            r3 = 0
            goto L_0x0f9a
        L_0x0f8a:
            r6 = 6
            if (r0 != r6) goto L_0x0f9a
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            boolean r9 = r6.phone_requested
            if (r9 != 0) goto L_0x0f9a
            boolean r6 = r6.email_requested
            if (r6 != 0) goto L_0x0f9a
            goto L_0x0var_
        L_0x0f9a:
            if (r3 == 0) goto L_0x0fb8
            org.telegram.ui.PaymentFormActivity$10 r3 = new org.telegram.ui.PaymentFormActivity$10
            r3.<init>(r8)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r3)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r11 = -1
            r6.<init>(r11, r14, r9)
            r1.addView(r3, r6)
        L_0x0fb8:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r3[r0] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r3.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r3.setTextSize(r14, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 0
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r6)
            if (r0 != 0) goto L_0x1029
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$YMeFnG1zi0SfhsCCk86PBQIPZKI r6 = org.telegram.ui.$$Lambda$PaymentFormActivity$YMeFnG1zi0SfhsCCk86PBQIPZKI.INSTANCE
            r3.setOnTouchListener(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r3.setInputType(r5)
            goto L_0x103b
        L_0x1029:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 129(0x81, float:1.81E-43)
            r3.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r6)
        L_0x103b:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r3.setImeOptions(r6)
            if (r0 == 0) goto L_0x1062
            if (r0 == r14) goto L_0x104a
            goto L_0x106f
        L_0x104a:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 2131626029(0x7f0e082d, float:1.8879283E38)
            java.lang.String r9 = "LoginPassword"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r3.requestFocus()
            goto L_0x106f
        L_0x1062:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r6 = r6.saved_credentials
            java.lang.String r6 = r6.title
            r3.setText(r6)
        L_0x106f:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setPadding(r5, r5, r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1086
            r6 = 5
            goto L_0x1087
        L_0x1086:
            r6 = 3
        L_0x1087:
            r3.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r26 = -1
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 51
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r1.addView(r3, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$e9RqJBJ1jQ4PDryfM6w6pSv02Wk r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$e9RqJBJ1jQ4PDryfM6w6pSv02Wk
            r3.<init>()
            r1.setOnEditorActionListener(r3)
            if (r0 != r14) goto L_0x1156
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r1[r5] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r9 = r9.saved_credentials
            java.lang.String r9 = r9.title
            r6[r5] = r9
            java.lang.String r9 = "PaymentConfirmationMessage"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r3, r6)
            r1.setText(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131165450(0x7var_a, float:1.7945117E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r10)
            r1.setBackgroundDrawable(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r1.addView(r3, r11)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r3 = new org.telegram.ui.Cells.TextSettingsCell
            r3.<init>(r8)
            r1[r5] = r3
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r5]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r1.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r5]
            r3 = 2131626829(0x7f0e0b4d, float:1.8880905E38)
            java.lang.String r6 = "PaymentConfirmationNewCard"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3, r5)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r7.settingsCell
            r3 = r3[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r1.addView(r3, r11)
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r5]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$8oiBrYVovzVK-mug2vbZpERQ3mo r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$8oiBrYVovzVK-mug2vbZpERQ3mo
            r3.<init>()
            r1.setOnClickListener(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r14]
            r3 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r10)
            r1.setBackgroundDrawable(r6)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r14]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r1.addView(r3, r11)
        L_0x1156:
            int r0 = r0 + 1
            r1 = 2
            goto L_0x0var_
        L_0x115b:
            r1 = 4
            if (r0 == r1) goto L_0x14bc
            r1 = 5
            if (r0 != r1) goto L_0x1163
            goto L_0x14bc
        L_0x1163:
            r1 = 6
            if (r0 != r1) goto L_0x1bf5
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r8)
            r7.codeFieldCell = r0
            r1 = 2131626798(0x7f0e0b2e, float:1.8880842E38)
            java.lang.String r4 = "PasswordCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setTextAndHint(r3, r1, r5)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r1 = 3
            r0.setInputType(r1)
            r1 = 6
            r0.setImeOptions(r1)
            org.telegram.ui.-$$Lambda$PaymentFormActivity$uf_8DJOjk5pz_qHwrCln0ooTjmo r1 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$uf_8DJOjk5pz_qHwrCln0ooTjmo
            r1.<init>()
            r0.setOnEditorActionListener(r1)
            org.telegram.ui.PaymentFormActivity$20 r1 = new org.telegram.ui.PaymentFormActivity$20
            r1.<init>()
            r0.addTextChangedListener(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r1 = r7.codeFieldCell
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r3 = 2
            r0[r3] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r3]
            r1 = 2131165450(0x7var_a, float:1.7945117E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r6)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r1 = new org.telegram.ui.Cells.TextSettingsCell
            r1.<init>(r8)
            r0[r14] = r1
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r14]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r14]
            r0.setTag(r2)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r14]
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r14]
            r1 = 2131627214(0x7f0e0cce, float:1.8881686E38)
            java.lang.String r3 = "ResendCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1, r14)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r14]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r6)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r14]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$dC-bcnHmtg9qNsLheaWD-lpvjyQ r1 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$dC-bcnHmtg9qNsLheaWD-lpvjyQ
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r1 = new org.telegram.ui.Cells.TextSettingsCell
            r1.<init>(r8)
            r0[r5] = r1
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            java.lang.String r1 = "windowBackgroundWhiteRedText3"
            r0.setTag(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            java.lang.String r1 = "windowBackgroundWhiteRedText3"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            r1 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r3 = "AbortPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1, r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r5]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r6)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r5]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$bxxexSfbCJYP0TeH1TDVZzLz8kE r1 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$bxxexSfbCJYP0TeH1TDVZzLz8kE
            r1.<init>()
            r0.setOnClickListener(r1)
            r1 = 3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x1280:
            if (r0 >= r1) goto L_0x14b7
            if (r0 != 0) goto L_0x12b8
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r1[r5] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r5]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r5]
            r3 = 2131626843(0x7f0e0b5b, float:1.8880934E38)
            java.lang.String r4 = "PaymentPasswordTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r5]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
            goto L_0x12ef
        L_0x12b8:
            r1 = 2
            if (r0 != r1) goto L_0x12ef
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r14]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Cells.HeaderCell[] r1 = r7.headerCell
            r1 = r1[r14]
            r3 = 2131626839(0x7f0e0b57, float:1.8880926E38)
            java.lang.String r4 = "PaymentPasswordEmailTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r14]
            r4 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r1.addView(r3, r9)
            goto L_0x12f0
        L_0x12ef:
            r6 = -1
        L_0x12f0:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r1.setClipChildren(r5)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r4 = 50
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r3.addView(r1, r9)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            if (r0 != 0) goto L_0x1328
            org.telegram.ui.PaymentFormActivity$21 r3 = new org.telegram.ui.PaymentFormActivity$21
            r3.<init>(r8)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r6)
            java.util.ArrayList<android.view.View> r6 = r7.dividers
            r6.add(r3)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r11 = -1
            r6.<init>(r11, r14, r9)
            r1.addView(r3, r6)
        L_0x1328:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r8)
            r3[r0] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r3.setTag(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1098907648(0x41800000, float:16.0)
            r3.setTextSize(r14, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.String r6 = "windowBackgroundWhiteHintText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setHintTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setTextColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 0
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setCursorColor(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r6)
            if (r0 == 0) goto L_0x139f
            if (r0 != r14) goto L_0x138b
            goto L_0x139f
        L_0x138b:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 33
            r3.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 268435462(0x10000006, float:2.5243567E-29)
            r3.setImeOptions(r6)
            goto L_0x13bb
        L_0x139f:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 129(0x81, float:1.81E-43)
            r3.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            android.graphics.Typeface r6 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 268435461(0x10000005, float:2.5243564E-29)
            r3.setImeOptions(r6)
        L_0x13bb:
            if (r0 == 0) goto L_0x13e5
            if (r0 == r14) goto L_0x13d4
            r3 = 2
            if (r0 == r3) goto L_0x13c3
            goto L_0x13fc
        L_0x13c3:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 2131626837(0x7f0e0b55, float:1.8880921E38)
            java.lang.String r9 = "PaymentPasswordEmail"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            goto L_0x13fc
        L_0x13d4:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 2131626842(0x7f0e0b5a, float:1.8880932E38)
            java.lang.String r9 = "PaymentPasswordReEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            goto L_0x13fc
        L_0x13e5:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 2131626840(0x7f0e0b58, float:1.8880928E38)
            java.lang.String r9 = "PaymentPasswordEnter"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r3.setHint(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r3.requestFocus()
        L_0x13fc:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setPadding(r5, r5, r5, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x1413
            r6 = 5
            goto L_0x1414
        L_0x1413:
            r6 = 3
        L_0x1414:
            r3.setGravity(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r26 = -1
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 51
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r1.addView(r3, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$hqFUiQmF3m-GRr3sxAL-cBdv52o r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$hqFUiQmF3m-GRr3sxAL-cBdv52o
            r3.<init>()
            r1.setOnEditorActionListener(r3)
            if (r0 != r14) goto L_0x1475
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r1[r5] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131626841(0x7f0e0b59, float:1.888093E38)
            java.lang.String r6 = "PaymentPasswordInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131165450(0x7var_a, float:1.7945117E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r10)
            r1.setBackgroundDrawable(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r5]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r1.addView(r3, r11)
            goto L_0x14af
        L_0x1475:
            r1 = 2
            if (r0 != r1) goto L_0x14af
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r14]
            r3 = 2131626838(0x7f0e0b56, float:1.8880923E38)
            java.lang.String r6 = "PaymentPasswordEmailInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r14]
            r11 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r11, (java.lang.String) r10)
            r1.setBackgroundDrawable(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r14]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r1.addView(r3, r12)
            goto L_0x14b2
        L_0x14af:
            r11 = 2131165451(0x7var_b, float:1.794512E38)
        L_0x14b2:
            int r0 = r0 + 1
            r1 = 3
            goto L_0x1280
        L_0x14b7:
            r34.updatePasswordFields()
            goto L_0x1bf5
        L_0x14bc:
            r11 = 2131165451(0x7var_b, float:1.794512E38)
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r8)
            r7.paymentInfoCell = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.messenger.MessageObject r0 = r7.messageObject
            if (r0 == 0) goto L_0x14df
            org.telegram.ui.Cells.PaymentInfoCell r1 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) r0
            java.lang.String r4 = r7.currentBotName
            r1.setInvoice(r0, r4)
            goto L_0x14ea
        L_0x14df:
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r0 = r7.paymentReceipt
            if (r0 == 0) goto L_0x14ea
            org.telegram.ui.Cells.PaymentInfoCell r1 = r7.paymentInfoCell
            java.lang.String r4 = r7.currentBotName
            r1.setReceipt(r0, r4)
        L_0x14ea:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r1 = r7.paymentInfoCell
            r4 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r4)
            r0.addView(r1, r13)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            r1.<init>(r8)
            r0[r5] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r5]
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r4)
            r0.addView(r1, r13)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r1.prices
            r0.<init>(r1)
            r7.prices = r0
            org.telegram.tgnet.TLRPC$TL_shippingOption r1 = r7.shippingOption
            if (r1 == 0) goto L_0x1523
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r1.prices
            r0.addAll(r1)
        L_0x1523:
            java.lang.String[] r0 = new java.lang.String[r14]
            r7.totalPrice = r0
            r0 = 0
        L_0x1528:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r7.prices
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x1567
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r7.prices
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r1 = (org.telegram.tgnet.TLRPC$TL_labeledPrice) r1
            org.telegram.ui.Cells.TextPriceCell r12 = new org.telegram.ui.Cells.TextPriceCell
            r12.<init>(r8)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r12.setBackgroundColor(r13)
            java.lang.String r13 = r1.label
            org.telegram.messenger.LocaleController r15 = org.telegram.messenger.LocaleController.getInstance()
            r19 = r10
            long r9 = r1.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.lang.String r1 = r1.currency
            java.lang.String r1 = r15.formatCurrencyString(r9, r1)
            r12.setTextAndValue(r13, r1, r5)
            android.widget.LinearLayout r1 = r7.linearLayout2
            r1.addView(r12)
            int r0 = r0 + 1
            r10 = r19
            r9 = 9
            goto L_0x1528
        L_0x1567:
            r19 = r10
            int r0 = r7.currentStep
            r1 = 5
            if (r0 != r1) goto L_0x15a3
            java.lang.Long r0 = r7.tipAmount
            if (r0 == 0) goto L_0x15a3
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            r1 = 2131626866(0x7f0e0b72, float:1.888098E38)
            java.lang.String r9 = "PaymentTip"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            org.telegram.messenger.LocaleController r9 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.Long r10 = r7.tipAmount
            long r12 = r10.longValue()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r10 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r10 = r10.invoice
            java.lang.String r10 = r10.currency
            java.lang.String r9 = r9.formatCurrencyString(r12, r10)
            r0.setTextAndValue(r1, r9, r5)
            android.widget.LinearLayout r1 = r7.linearLayout2
            r1.addView(r0)
        L_0x15a3:
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            r7.totalCell = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            java.lang.String[] r0 = r7.totalPrice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r7.prices
            java.lang.String r1 = r7.getTotalPriceString(r1)
            r0[r5] = r1
            org.telegram.ui.Cells.TextPriceCell r0 = r7.totalCell
            r1 = 2131626871(0x7f0e0b77, float:1.888099E38)
            java.lang.String r9 = "PaymentTransactionTotal"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            java.lang.String[] r9 = r7.totalPrice
            r9 = r9[r5]
            r0.setTextAndValue(r1, r9, r14)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1862
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            int r0 = r0.flags
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x1862
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r0.setClipChildren(r5)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r9 = r9.invoice
            java.util.ArrayList<java.lang.Long> r9 = r9.suggested_tip_amounts
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x15fc
            r9 = 40
            goto L_0x15fe
        L_0x15fc:
            r9 = 78
        L_0x15fe:
            r10 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r9)
            r1.addView(r0, r9)
            org.telegram.ui.-$$Lambda$PaymentFormActivity$VLYtB2G85ebWkqvar_i88M_wSyRU r1 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$VLYtB2G85ebWkqvar_i88M_wSyRU
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextPriceCell r1 = new org.telegram.ui.Cells.TextPriceCell
            r1.<init>(r8)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r9)
            r9 = 2131626867(0x7f0e0b73, float:1.8880982E38)
            java.lang.String r10 = "PaymentTipOptional"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r1.setTextAndValue(r9, r3, r5)
            r0.addView(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = new org.telegram.ui.Components.EditTextBoldCursor[r14]
            r7.inputFields = r1
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r8)
            r1[r5] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r5)
            r1.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r9 = 1098907648(0x41800000, float:16.0)
            r1.setTextSize(r14, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            java.lang.String r9 = "windowBackgroundWhiteGrayText2"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r1.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            java.lang.String r9 = "windowBackgroundWhiteGrayText2"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r1.setTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r9 = 0
            r1.setBackgroundDrawable(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setCursorColor(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setCursorSize(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r2 = 1069547520(0x3fCLASSNAME, float:1.5)
            r1.setCursorWidth(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r2 = 3
            r1.setInputType(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r1.setImeOptions(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            r12 = 0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r10 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r10 = r10.invoice
            java.lang.String r10 = r10.currency
            java.lang.String r2 = r2.formatCurrencyString(r12, r10)
            r1.setHint(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r5, r5, r5, r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x16cb
            r2 = 3
            goto L_0x16cc
        L_0x16cb:
            r2 = 5
        L_0x16cc:
            r1.setGravity(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r26 = -1
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 51
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1091567616(0x41100000, float:9.0)
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            org.telegram.ui.PaymentFormActivity$11 r2 = new org.telegram.ui.PaymentFormActivity$11
            r2.<init>()
            r1.addTextChangedListener(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$nZ4_szZB3J8cd3WhiD7iwhnvYsk r2 = org.telegram.ui.$$Lambda$PaymentFormActivity$nZ4_szZB3J8cd3WhiD7iwhnvYsk.INSTANCE
            r1.setOnEditorActionListener(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r1.requestFocus()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.util.ArrayList<java.lang.Long> r1 = r1.suggested_tip_amounts
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x1862
            android.widget.HorizontalScrollView r10 = new android.widget.HorizontalScrollView
            r10.<init>(r8)
            r10.setHorizontalScrollBarEnabled(r5)
            r10.setVerticalScrollBarEnabled(r5)
            r10.setClipToPadding(r5)
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r10.setPadding(r1, r5, r2, r5)
            r10.setFillViewport(r14)
            r26 = -1
            r27 = 1106247680(0x41var_, float:30.0)
            r28 = 51
            r29 = 0
            r30 = 1110441984(0x42300000, float:44.0)
            r31 = 0
            r32 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r10, r1)
            int[] r0 = new int[r14]
            int[] r12 = new int[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.util.ArrayList<java.lang.Long> r1 = r1.suggested_tip_amounts
            int r13 = r1.size()
            org.telegram.ui.PaymentFormActivity$12 r15 = new org.telegram.ui.PaymentFormActivity$12
            r1 = r15
            r2 = r34
            r17 = r3
            r3 = r35
            r9 = -2
            r4 = r13
            r9 = 0
            r5 = r0
            r33 = r6
            r6 = r12
            r1.<init>(r3, r4, r5, r6)
            r7.tipLayout = r15
            r15.setOrientation(r9)
            android.widget.LinearLayout r1 = r7.tipLayout
            r2 = 30
            r3 = 51
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createScroll(r4, r2, r3)
            r10.addView(r1, r2)
            java.lang.String r1 = "contacts_inviteBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r5 = 0
        L_0x1780:
            if (r5 >= r13) goto L_0x1867
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x179a
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            int r3 = r13 - r5
            int r3 = r3 - r14
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            goto L_0x17aa
        L_0x179a:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            java.lang.Object r2 = r2.get(r5)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x17aa:
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            java.lang.String r6 = r6.currency
            java.lang.String r4 = r4.formatCurrencyString(r2, r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r8)
            r10 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r14, r10)
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r6.setTypeface(r10)
            r6.setLines(r14)
            java.lang.Long r10 = java.lang.Long.valueOf(r2)
            r6.setTag(r10)
            r6.setMaxLines(r14)
            r6.setText(r4)
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r15 = 1097859072(0x41700000, float:15.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.setPadding(r10, r9, r15, r9)
            java.lang.String r10 = "chats_secretName"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setTextColor(r10)
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r15 = 536870911(0x1fffffff, float:1.0842021E-19)
            r15 = r15 & r1
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r10, r15)
            r6.setBackground(r10)
            r6.setSingleLine(r14)
            r10 = 17
            r6.setGravity(r10)
            android.widget.LinearLayout r10 = r7.tipLayout
            r26 = -2
            r27 = -1
            r28 = 19
            r29 = 0
            r30 = 0
            int r15 = r13 + -1
            if (r5 == r15) goto L_0x181f
            r31 = 9
            goto L_0x1821
        L_0x181f:
            r31 = 0
        L_0x1821:
            r32 = 0
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32)
            r10.addView(r6, r15)
            org.telegram.ui.-$$Lambda$PaymentFormActivity$UOoKIkzkLhnzz156prQysEuP_KE r10 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$UOoKIkzkLhnzz156prQysEuP_KE
            r10.<init>(r6, r2)
            r6.setOnClickListener(r10)
            android.text.TextPaint r2 = r6.getPaint()
            float r2 = r2.measureText(r4)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r3 = 1106247680(0x41var_, float:30.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            r3 = 2131230943(0x7var_df, float:1.8077953E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            r6.setTag(r3, r4)
            r3 = r0[r9]
            int r3 = java.lang.Math.max(r3, r2)
            r0[r9] = r3
            r3 = r12[r9]
            int r3 = r3 + r2
            r12[r9] = r3
            int r5 = r5 + 1
            goto L_0x1780
        L_0x1862:
            r17 = r3
            r33 = r6
            r9 = 0
        L_0x1867:
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
            r1 = r19
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r11, (java.lang.String) r1)
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
            r0[r9] = r2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r9]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r9]
            java.lang.String r2 = r7.cardName
            if (r2 == 0) goto L_0x18d6
            int r2 = r2.length()
            if (r2 <= r14) goto L_0x18d6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r7.cardName
            java.lang.String r3 = r3.substring(r9, r14)
            java.lang.String r3 = r3.toUpperCase()
            r2.append(r3)
            java.lang.String r3 = r7.cardName
            java.lang.String r3 = r3.substring(r14)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            goto L_0x18d8
        L_0x18d6:
            java.lang.String r2 = r7.cardName
        L_0x18d8:
            r3 = 2131626821(0x7f0e0b45, float:1.888089E38)
            java.lang.String r4 = "PaymentCheckoutMethod"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 2131165905(0x7var_d1, float:1.794604E38)
            r0.setTextAndValueAndIcon(r2, r3, r4, r14)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r9]
            r0.addView(r2)
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x1901
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r9]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$xX7gPqWdjlYfKTgW27OpQdgiH2c r2 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$xX7gPqWdjlYfKTgW27OpQdgiH2c
            r2.<init>()
            r0.setOnClickListener(r2)
        L_0x1901:
            r2 = 0
            r5 = 0
        L_0x1903:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r0.users
            int r0 = r0.size()
            if (r5 >= r0) goto L_0x1923
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r0.users
            java.lang.Object r0 = r0.get(r5)
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            int r3 = r0.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            int r4 = r4.provider_id
            if (r3 != r4) goto L_0x1920
            r2 = r0
        L_0x1920:
            int r5 = r5 + 1
            goto L_0x1903
        L_0x1923:
            if (r2 == 0) goto L_0x196f
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r3 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r3.<init>(r8)
            r0[r14] = r3
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r14]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r14]
            java.lang.String r3 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r2)
            r2 = 2131626826(0x7f0e0b4a, float:1.88809E38)
            java.lang.String r4 = "PaymentCheckoutProvider"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r4 = 2131165910(0x7var_d6, float:1.794605E38)
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r5 = r7.validateRequest
            if (r5 == 0) goto L_0x1961
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 != 0) goto L_0x195f
            org.telegram.tgnet.TLRPC$TL_shippingOption r5 = r7.shippingOption
            if (r5 == 0) goto L_0x1961
        L_0x195f:
            r5 = 1
            goto L_0x1962
        L_0x1961:
            r5 = 0
        L_0x1962:
            r0.setTextAndValueAndIcon(r3, r2, r4, r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r14]
            r0.addView(r2)
            goto L_0x1971
        L_0x196f:
            r3 = r17
        L_0x1971:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            if (r0 == 0) goto L_0x1aba
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            org.telegram.tgnet.TLRPC$TL_postAddress r0 = r0.shipping_address
            if (r0 == 0) goto L_0x19b6
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 2
            r0[r4] = r2
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r0.addView(r2)
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x19ab
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$aXAUqIs8LZgIjzhMbkZqaWXHupQ r2 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$aXAUqIs8LZgIjzhMbkZqaWXHupQ
            r2.<init>()
            r0.setOnClickListener(r2)
            goto L_0x19b6
        L_0x19ab:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r2)
        L_0x19b6:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.name
            if (r0 == 0) goto L_0x19f9
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 3
            r0[r4] = r2
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r0.addView(r2)
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x19ee
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$TRE2OsVpSnUxqbD-RAp5tffzVyM r2 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$TRE2OsVpSnUxqbD-RAp5tffzVyM
            r2.<init>()
            r0.setOnClickListener(r2)
            goto L_0x19f9
        L_0x19ee:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r2)
        L_0x19f9:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.phone
            if (r0 == 0) goto L_0x1a3b
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 4
            r0[r4] = r2
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r0.addView(r2)
            int r0 = r7.currentStep
            if (r0 != r4) goto L_0x1a30
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$bJHWj5XWO49_bvTcvyTQyZ9HIbw r2 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$bJHWj5XWO49_bvTcvyTQyZ9HIbw
            r2.<init>()
            r0.setOnClickListener(r2)
            goto L_0x1a3b
        L_0x1a30:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r2)
        L_0x1a3b:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.email
            if (r0 == 0) goto L_0x1a7e
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 5
            r0[r4] = r2
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r0.addView(r2)
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x1a73
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$NzNTAiRglUL0O4tFCLASSNAMEN3JOMeC0 r2 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$NzNTAiRglUL0O4tFCLASSNAMEN3JOMeC0
            r2.<init>()
            r0.setOnClickListener(r2)
            goto L_0x1a7e
        L_0x1a73:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r2)
        L_0x1a7e:
            org.telegram.tgnet.TLRPC$TL_shippingOption r0 = r7.shippingOption
            if (r0 == 0) goto L_0x1ab7
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r2 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r2.<init>(r8)
            r4 = 6
            r0[r4] = r2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r2)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = r7.shippingOption
            java.lang.String r2 = r2.title
            r5 = 2131626827(0x7f0e0b4b, float:1.8880901E38)
            java.lang.String r6 = "PaymentCheckoutShippingMethod"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131165906(0x7var_d2, float:1.7946042E38)
            r0.setTextAndValueAndIcon(r2, r5, r6, r9)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r2 = r7.detailSettingsCell
            r2 = r2[r4]
            r0.addView(r2)
        L_0x1ab7:
            r34.setAddressFields()
        L_0x1aba:
            int r0 = r7.currentStep
            r2 = 4
            if (r0 != r2) goto L_0x1bd2
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.bottomLayout = r0
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r2 < r4) goto L_0x1adc
            java.lang.String r4 = "listSelectorSDK21"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r5 = "contacts_inviteBackground"
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r4, (java.lang.String) r5)
            r0.setBackgroundDrawable(r4)
            goto L_0x1ae5
        L_0x1adc:
            java.lang.String r4 = "contacts_inviteBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColor(r4)
        L_0x1ae5:
            android.widget.FrameLayout r0 = r7.bottomLayout
            r4 = 48
            r5 = 80
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4, r5)
            r5 = r33
            r5.addView(r0, r4)
            android.widget.FrameLayout r0 = r7.bottomLayout
            org.telegram.ui.-$$Lambda$PaymentFormActivity$20kIl2YO7mLVnhYjfjMILQmfRgo r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$20kIl2YO7mLVnhYjfjMILQmfRgo
            r4.<init>(r3)
            r0.setOnClickListener(r4)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.payTextView = r0
            java.lang.String r3 = "contacts_inviteText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r7.payTextView
            r3 = 2131626824(0x7f0e0b48, float:1.8880895E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            java.lang.String[] r6 = r7.totalPrice
            r6 = r6[r9]
            r4[r9] = r6
            java.lang.String r6 = "PaymentCheckoutPay"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            r0.setText(r3)
            android.widget.TextView r0 = r7.payTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r14, r3)
            android.widget.TextView r0 = r7.payTextView
            r3 = 17
            r0.setGravity(r3)
            android.widget.TextView r0 = r7.payTextView
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r0.setTypeface(r3)
            android.widget.FrameLayout r0 = r7.bottomLayout
            android.widget.TextView r3 = r7.payTextView
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r0.addView(r3, r4)
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r8, r9)
            r7.progressViewButton = r0
            r3 = 4
            r0.setVisibility(r3)
            java.lang.String r0 = "contacts_inviteText"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            org.telegram.ui.Components.ContextProgressView r3 = r7.progressViewButton
            r4 = 805306367(0x2fffffff, float:4.6566126E-10)
            r4 = r4 & r0
            r3.setColors(r4, r0)
            android.widget.FrameLayout r0 = r7.bottomLayout
            org.telegram.ui.Components.ContextProgressView r3 = r7.progressViewButton
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r0.addView(r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            r0.setEnabled(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.doneItem
            android.view.View r0 = r0.getContentView()
            r3 = 4
            r0.setVisibility(r3)
            org.telegram.ui.PaymentFormActivity$18 r0 = new org.telegram.ui.PaymentFormActivity$18
            r0.<init>(r8)
            r7.webView = r0
            r0.setBackgroundColor(r6)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setJavaScriptEnabled(r14)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r14)
            r0 = 21
            if (r2 < r0) goto L_0x1bb5
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setMixedContentMode(r9)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r2 = r7.webView
            r0.setAcceptThirdPartyCookies(r2, r14)
        L_0x1bb5:
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$19 r2 = new org.telegram.ui.PaymentFormActivity$19
            r2.<init>()
            r0.setWebViewClient(r2)
            android.webkit.WebView r0 = r7.webView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r5.addView(r0, r2)
            android.webkit.WebView r0 = r7.webView
            r2 = 8
            r0.setVisibility(r2)
        L_0x1bd2:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            r2.<init>(r8)
            r0[r14] = r2
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r14]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r11, (java.lang.String) r1)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r14]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
        L_0x1bf5:
            android.view.View r0 = r7.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ boolean lambda$createView$1$PaymentFormActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(String str, String str2) {
                    PaymentFormActivity.this.lambda$createView$0$PaymentFormActivity(str, str2);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$PaymentFormActivity(String str, String str2) {
        this.inputFields[4].setText(str);
        this.countryName = str2;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$PaymentFormActivity(View view) {
        boolean z = !this.saveShippingInfo;
        this.saveShippingInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$PaymentFormActivity(View view) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ boolean lambda$createView$6$PaymentFormActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(String str, String str2) {
                    PaymentFormActivity.this.lambda$createView$5$PaymentFormActivity(str, str2);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$PaymentFormActivity(String str, String str2) {
        this.inputFields[4].setText(str);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ boolean lambda$createView$7$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
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
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$PaymentFormActivity(View view) {
        boolean z = !this.saveCardInfo;
        this.saveCardInfo = z;
        this.checkCell1.setChecked(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ boolean lambda$createView$11$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$12 */
    public /* synthetic */ void lambda$createView$12$PaymentFormActivity(View view) {
        this.passwordOk = false;
        goToNextStep();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$13 */
    public /* synthetic */ void lambda$createView$13$PaymentFormActivity(View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$15 */
    public /* synthetic */ void lambda$createView$15$PaymentFormActivity(TextView textView2, long j, View view) {
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
    /* renamed from: lambda$createView$17 */
    public /* synthetic */ void lambda$createView$17$PaymentFormActivity(View view) {
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PaymentCheckoutMethod", NUM), true);
            builder.setItems(new CharSequence[]{this.cardName, LocaleController.getString("PaymentCheckoutMethodNewCard", NUM)}, new int[]{NUM, NUM}, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PaymentFormActivity.this.lambda$createView$16$PaymentFormActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$16 */
    public /* synthetic */ void lambda$createView$16$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        if (i == 1) {
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
    /* renamed from: lambda$createView$18 */
    public /* synthetic */ void lambda$createView$18$PaymentFormActivity(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$19 */
    public /* synthetic */ void lambda$createView$19$PaymentFormActivity(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$20 */
    public /* synthetic */ void lambda$createView$20$PaymentFormActivity(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$21 */
    public /* synthetic */ void lambda$createView$21$PaymentFormActivity(View view) {
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 0, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null, this.parentFragment);
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
                PaymentFormActivity.this.setAddressFields();
            }
        });
        presentFragment(paymentFormActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$23 */
    public /* synthetic */ void lambda$createView$23$PaymentFormActivity(String str, View view) {
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
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PaymentFormActivity.this.lambda$createView$22$PaymentFormActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return;
        }
        showPayAlert(this.totalPrice[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$22 */
    public /* synthetic */ void lambda$createView$22$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$24 */
    public /* synthetic */ boolean lambda$createView$24$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$26 */
    public /* synthetic */ void lambda$createView$26$PaymentFormActivity(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), $$Lambda$PaymentFormActivity$QXo69tyXDqus2aG4Jgxnv6uoTo.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$28 */
    public /* synthetic */ void lambda$createView$28$PaymentFormActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(string);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", NUM));
        builder.setPositiveButton(LocaleController.getString("Disable", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.lambda$createView$27$PaymentFormActivity(dialogInterface, i);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$27 */
    public /* synthetic */ void lambda$createView$27$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$29 */
    public /* synthetic */ boolean lambda$createView$29$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
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
    public void setAddressFields() {
        TLRPC$TL_postAddress tLRPC$TL_postAddress = this.validateRequest.info.shipping_address;
        boolean z = false;
        if (tLRPC$TL_postAddress != null) {
            this.detailSettingsCell[2].setTextAndValueAndIcon(String.format("%s %s, %s, %s, %s, %s", new Object[]{tLRPC$TL_postAddress.street_line1, tLRPC$TL_postAddress.street_line2, tLRPC$TL_postAddress.city, tLRPC$TL_postAddress.state, tLRPC$TL_postAddress.country_iso2, tLRPC$TL_postAddress.post_code}), LocaleController.getString("PaymentShippingAddress", NUM), NUM, true);
        }
        String str = this.validateRequest.info.name;
        if (str != null) {
            this.detailSettingsCell[3].setTextAndValueAndIcon(str, LocaleController.getString("PaymentCheckoutName", NUM), NUM, true);
        }
        if (this.validateRequest.info.phone != null) {
            this.detailSettingsCell[4].setTextAndValueAndIcon(PhoneFormat.getInstance().format(this.validateRequest.info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", NUM), NUM, (this.validateRequest.info.email == null && this.shippingOption == null) ? false : true);
        }
        String str2 = this.validateRequest.info.email;
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
            int color = Theme.getColor("contacts_inviteBackground");
            int childCount = this.tipLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TextView textView3 = (TextView) this.tipLayout.getChildAt(i);
                if (textView3.getTag().equals(this.tipAmount)) {
                    Theme.setDrawableColor(textView3.getBackground(), color);
                    textView3.setTextColor(Theme.getColor("contacts_inviteText"));
                } else {
                    Theme.setDrawableColor(textView3.getBackground(), NUM & color);
                    textView3.setTextColor(Theme.getColor("chats_secretName"));
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
        this.googlePayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PaymentFormActivity.this.lambda$createGooglePayButton$30$PaymentFormActivity(view);
            }
        });
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
    /* JADX WARNING: Removed duplicated region for block: B:12:0x004b A[Catch:{ JSONException -> 0x00b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x006a A[Catch:{ JSONException -> 0x00b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00a4 A[Catch:{ JSONException -> 0x00b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$createGooglePayButton$30 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createGooglePayButton$30$PaymentFormActivity(android.view.View r5) {
        /*
            r4 = this;
            android.widget.FrameLayout r5 = r4.googlePayButton
            r0 = 0
            r5.setClickable(r0)
            org.json.JSONObject r5 = r4.getBaseRequest()     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONObject r0 = r4.getBaseCardPaymentMethod()     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r1 = r4.googlePayPublicKey     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r2 = "tokenizationSpecification"
            if (r1 == 0) goto L_0x0021
            org.json.JSONObject r1 = r4.googlePayParameters     // Catch:{ JSONException -> 0x00b4 }
            if (r1 != 0) goto L_0x0021
            org.telegram.ui.PaymentFormActivity$22 r1 = new org.telegram.ui.PaymentFormActivity$22     // Catch:{ JSONException -> 0x00b4 }
            r1.<init>()     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00b4 }
            goto L_0x0029
        L_0x0021:
            org.telegram.ui.PaymentFormActivity$23 r1 = new org.telegram.ui.PaymentFormActivity$23     // Catch:{ JSONException -> 0x00b4 }
            r1.<init>()     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00b4 }
        L_0x0029:
            java.lang.String r1 = "allowedPaymentMethods"
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ JSONException -> 0x00b4 }
            r2.<init>()     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONArray r0 = r2.put(r0)     // Catch:{ JSONException -> 0x00b4 }
            r5.put(r1, r0)     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00b4 }
            r0.<init>()     // Catch:{ JSONException -> 0x00b4 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r4.paymentForm     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice     // Catch:{ JSONException -> 0x00b4 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r2.prices     // Catch:{ JSONException -> 0x00b4 }
            r1.<init>(r2)     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_shippingOption r2 = r4.shippingOption     // Catch:{ JSONException -> 0x00b4 }
            if (r2 == 0) goto L_0x0050
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r2 = r2.prices     // Catch:{ JSONException -> 0x00b4 }
            r1.addAll(r2)     // Catch:{ JSONException -> 0x00b4 }
        L_0x0050:
            java.lang.String r2 = "totalPrice"
            java.lang.String r1 = r4.getTotalPriceDecimalString(r1)     // Catch:{ JSONException -> 0x00b4 }
            r4.totalPriceDecimal = r1     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r1 = "totalPriceStatus"
            java.lang.String r2 = "FINAL"
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r1 = r4.googlePayCountryCode     // Catch:{ JSONException -> 0x00b4 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ JSONException -> 0x00b4 }
            if (r1 != 0) goto L_0x0071
            java.lang.String r1 = "countryCode"
            java.lang.String r2 = r4.googlePayCountryCode     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b4 }
        L_0x0071:
            java.lang.String r1 = "currencyCode"
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r4.paymentForm     // Catch:{ JSONException -> 0x00b4 }
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r2 = r2.currency     // Catch:{ JSONException -> 0x00b4 }
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r1 = "checkoutOption"
            java.lang.String r2 = "COMPLETE_IMMEDIATE_PURCHASE"
            r0.put(r1, r2)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r1 = "transactionInfo"
            r5.put(r1, r0)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r0 = "merchantInfo"
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00b4 }
            r1.<init>()     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r2 = "merchantName"
            java.lang.String r3 = r4.currentBotName     // Catch:{ JSONException -> 0x00b4 }
            org.json.JSONObject r1 = r1.put(r2, r3)     // Catch:{ JSONException -> 0x00b4 }
            r5.put(r0, r1)     // Catch:{ JSONException -> 0x00b4 }
            java.lang.String r5 = r5.toString()     // Catch:{ JSONException -> 0x00b4 }
            com.google.android.gms.wallet.PaymentDataRequest r5 = com.google.android.gms.wallet.PaymentDataRequest.fromJson(r5)     // Catch:{ JSONException -> 0x00b4 }
            if (r5 == 0) goto L_0x00b8
            com.google.android.gms.wallet.PaymentsClient r0 = r4.paymentsClient     // Catch:{ JSONException -> 0x00b4 }
            com.google.android.gms.tasks.Task r5 = r0.loadPaymentData(r5)     // Catch:{ JSONException -> 0x00b4 }
            android.app.Activity r0 = r4.getParentActivity()     // Catch:{ JSONException -> 0x00b4 }
            r1 = 991(0x3df, float:1.389E-42)
            com.google.android.gms.wallet.AutoResolveHelper.resolveTask(r5, r0, r1)     // Catch:{ JSONException -> 0x00b4 }
            goto L_0x00b8
        L_0x00b4:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.lambda$createGooglePayButton$30$PaymentFormActivity(android.view.View):void");
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$loadPasswordInfo$33$PaymentFormActivity(tLObject, tLRPC$TL_error);
                }
            }, 10);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordInfo$33 */
    public /* synthetic */ void lambda$loadPasswordInfo$33$PaymentFormActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$loadPasswordInfo$32$PaymentFormActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordInfo$32 */
    public /* synthetic */ void lambda$loadPasswordInfo$32$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
                $$Lambda$PaymentFormActivity$lQ1ldyyjP2GM2PBBIl0I86KOtQ r3 = new Runnable() {
                    public final void run() {
                        PaymentFormActivity.this.lambda$loadPasswordInfo$31$PaymentFormActivity();
                    }
                };
                this.shortPollRunnable = r3;
                AndroidUtilities.runOnUIThread(r3, 5000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordInfo$31 */
    public /* synthetic */ void lambda$loadPasswordInfo$31$PaymentFormActivity() {
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
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.lambda$showPayAlert$34$PaymentFormActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPayAlert$34 */
    public /* synthetic */ void lambda$showPayAlert$34$PaymentFormActivity(DialogInterface dialogInterface, int i) {
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
            if (isReadyToPayRequest.isPresent() && (fromJson = IsReadyToPayRequest.fromJson(((JSONObject) isReadyToPayRequest.get()).toString())) != null) {
                this.paymentsClient.isReadyToPay(fromJson).addOnCompleteListener(getParentActivity(), new OnCompleteListener() {
                    public final void onComplete(Task task) {
                        PaymentFormActivity.this.lambda$initGooglePay$35$PaymentFormActivity(task);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initGooglePay$35 */
    public /* synthetic */ void lambda$initGooglePay$35$PaymentFormActivity(Task task) {
        if (task.isSuccessful()) {
            FrameLayout frameLayout = this.googlePayContainer;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                return;
            }
            return;
        }
        FileLog.e("isReadyToPay failed", task.getException());
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            PaymentFormActivity.this.lambda$onTransitionAnimationEnd$36$PaymentFormActivity();
                        }
                    }, 100);
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
    /* renamed from: lambda$onTransitionAnimationEnd$36 */
    public /* synthetic */ void lambda$onTransitionAnimationEnd$36$PaymentFormActivity() {
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
            removeSelfFromStack();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 991) {
            AndroidUtilities.runOnUIThread(new Runnable(i2, intent) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ Intent f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$onActivityResultFragment$37$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActivityResultFragment$37 */
    public /* synthetic */ void lambda$onActivityResultFragment$37$PaymentFormActivity(int i, Intent intent) {
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
        boolean z;
        int i;
        int i2;
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
                i2 = 1;
            } else if (tLRPC$TL_payments_paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i2 = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i2 = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i2, this.requestedInfo, (TLRPC$TL_shippingOption) null, (Long) null, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
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
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, this.tipAmount, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
        } else if (i3 == 2) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm2 = this.paymentForm;
            if (!tLRPC$TL_payments_paymentForm2.password_missing || !(z = this.saveCardInfo)) {
                PaymentFormActivityDelegate paymentFormActivityDelegate2 = this.delegate;
                if (paymentFormActivityDelegate2 != null) {
                    paymentFormActivityDelegate2.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials);
                    finishFragment();
                    return;
                }
                presentFragment(new PaymentFormActivity(tLRPC$TL_payments_paymentForm2, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), this.isWebView);
                return;
            }
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm2, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, z, this.googlePayCredentials, this.parentFragment);
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
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.passwordOk ? 4 : 2, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
        } else if (i3 == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (i3 != 6) {
        } else {
            if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials)) {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.tipAmount, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials, this.parentFragment), true);
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
            Utilities.globalQueue.postRunnable(new Runnable(z, str2, str, tLRPC$TL_account_updatePasswordSettings) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ String f$3;
                public final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendSavePassword$45$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
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
        TLRPC$TL_account_confirmPasswordEmail tLRPC$TL_account_confirmPasswordEmail = new TLRPC$TL_account_confirmPasswordEmail();
        tLRPC$TL_account_confirmPasswordEmail.code = text;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_confirmPasswordEmail, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PaymentFormActivity.this.lambda$sendSavePassword$39$PaymentFormActivity(tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$39 */
    public /* synthetic */ void lambda$sendSavePassword$39$PaymentFormActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$38$PaymentFormActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$38 */
    public /* synthetic */ void lambda$sendSavePassword$38$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error) {
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
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$44 */
    public /* synthetic */ void lambda$sendSavePassword$44$PaymentFormActivity(boolean z, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, z, tLObject, str) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$43$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$45 */
    public /* synthetic */ void lambda$sendSavePassword$45$PaymentFormActivity(boolean z, String str, String str2, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        $$Lambda$PaymentFormActivity$7mNUMeUEdxZmHoHTkOVwRrHijA r0 = new RequestDelegate(z, str) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PaymentFormActivity.this.lambda$sendSavePassword$44$PaymentFormActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        };
        if (!z) {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str2);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                tLRPC$TL_account_updatePasswordSettings.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                if (tLRPC$TL_account_updatePasswordSettings.new_settings.new_password_hash == null) {
                    TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                    tLRPC$TL_error.text = "ALGO_INVALID";
                    r0.run((TLObject) null, tLRPC$TL_error);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, r0, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            r0.run((TLObject) null, tLRPC$TL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, r0, 10);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$43 */
    public /* synthetic */ void lambda$sendSavePassword$43$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, String str) {
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
                    this.emailCodeLength = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(str) {
                        public final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PaymentFormActivity.this.lambda$sendSavePassword$42$PaymentFormActivity(this.f$1, dialogInterface, i);
                        }
                    });
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
                    int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str2 = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
                }
            } else if (getParentActivity() != null) {
                goToNextStep();
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$sendSavePassword$41$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 8);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$41 */
    public /* synthetic */ void lambda$sendSavePassword$41$PaymentFormActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$sendSavePassword$40$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$40 */
    public /* synthetic */ void lambda$sendSavePassword$40$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            sendSavePassword(z);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$42 */
    public /* synthetic */ void lambda$sendSavePassword$42$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
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
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        PaymentFormActivity.AnonymousClass25.this.lambda$onSuccess$0$PaymentFormActivity$25();
                                    }
                                });
                            }
                        }

                        /* access modifiers changed from: private */
                        /* renamed from: lambda$onSuccess$0 */
                        public /* synthetic */ void lambda$onSuccess$0$PaymentFormActivity$25() {
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

    /* access modifiers changed from: private */
    public void sendForm() {
        if (!this.canceled) {
            showEditDoneProgress(true, true);
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo = new TLRPC$TL_payments_validateRequestedInfo();
            this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
            tLRPC$TL_payments_validateRequestedInfo.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo2 = this.validateRequest;
            tLRPC$TL_payments_validateRequestedInfo2.save = this.saveShippingInfo;
            tLRPC$TL_payments_validateRequestedInfo2.msg_id = this.messageObject.getId();
            this.validateRequest.info = new TLRPC$TL_paymentRequestedInfo();
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate(this.validateRequest) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$sendForm$49$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendForm$49 */
    public /* synthetic */ void lambda$sendForm$49$PaymentFormActivity(TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject2 instanceof TLRPC$TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject2) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendForm$47$PaymentFormActivity(this.f$1);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendForm$48$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendForm$47 */
    public /* synthetic */ void lambda$sendForm$47$PaymentFormActivity(TLObject tLObject) {
        this.requestedInfo = (TLRPC$TL_payments_validatedRequestedInfo) tLObject;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
            tLRPC$TL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_clearSavedInfo, $$Lambda$PaymentFormActivity$xvFYdkNGXcXnX60Q2a878DGgPc.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendForm$48 */
    public /* synthetic */ void lambda$sendForm$48$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
            tLRPC$TL_payments_sendPaymentForm.msg_id = this.messageObject.getId();
            tLRPC$TL_payments_sendPaymentForm.peer = getMessagesController().getInputPeer(this.messageObject.messageOwner.peer_id);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_sendPaymentForm, new RequestDelegate(tLRPC$TL_payments_sendPaymentForm) {
                public final /* synthetic */ TLRPC$TL_payments_sendPaymentForm f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$sendData$53$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendData$53 */
    public /* synthetic */ void lambda$sendData$53$PaymentFormActivity(TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_payments_sendPaymentForm) {
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ TLRPC$TL_payments_sendPaymentForm f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendData$52$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
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
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$MessageArr) {
                public final /* synthetic */ TLRPC$Message[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendData$50$PaymentFormActivity(this.f$1);
                }
            });
        } else if (tLObject instanceof TLRPC$TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$sendData$51$PaymentFormActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendData$50 */
    public /* synthetic */ void lambda$sendData$50$PaymentFormActivity(TLRPC$Message[] tLRPC$MessageArr) {
        goToNextStep();
        if (this.parentFragment instanceof ChatActivity) {
            ((ChatActivity) this.parentFragment).getUndoView().showWithAction(0, 77, AndroidUtilities.replaceTags(LocaleController.formatString("PaymentInfoHint", NUM, this.totalPrice[0], this.currentItemName)), tLRPC$MessageArr[0], (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendData$51 */
    public /* synthetic */ void lambda$sendData$51$PaymentFormActivity(TLObject tLObject) {
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
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendData$52 */
    public /* synthetic */ void lambda$sendData$52$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_payments_sendPaymentForm, new Object[0]);
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
            TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword = new TLRPC$TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPassword, new RequestDelegate(obj, tLRPC$TL_account_getPassword) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ TLRPC$TL_account_getPassword f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$checkPassword$58$PaymentFormActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkPassword$58 */
    public /* synthetic */ void lambda$checkPassword$58$PaymentFormActivity(String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, str, tLRPC$TL_account_getPassword) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ TLRPC$TL_account_getPassword f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$checkPassword$57$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkPassword$57 */
    public /* synthetic */ void lambda$checkPassword$57$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (!tLRPC$TL_account_password.has_password) {
                this.passwordOk = false;
                goToNextStep();
            } else {
                Utilities.globalQueue.postRunnable(new Runnable(tLRPC$TL_account_password, AndroidUtilities.getStringBytes(str)) {
                    public final /* synthetic */ TLRPC$TL_account_password f$1;
                    public final /* synthetic */ byte[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PaymentFormActivity.this.lambda$checkPassword$56$PaymentFormActivity(this.f$1, this.f$2);
                    }
                });
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_getPassword, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkPassword$56 */
    public /* synthetic */ void lambda$checkPassword$56$PaymentFormActivity(TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr) {
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword = new TLRPC$TL_account_getTmpPassword();
        tLRPC$TL_account_getTmpPassword.period = 1800;
        $$Lambda$PaymentFormActivity$F0joIv89L40t5j7MhMWgA8nbow r1 = new RequestDelegate(tLRPC$TL_account_getTmpPassword) {
            public final /* synthetic */ TLRPC$TL_account_getTmpPassword f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PaymentFormActivity.this.lambda$checkPassword$55$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        };
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getTmpPassword.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                r1.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getTmpPassword, r1, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        r1.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkPassword$55 */
    public /* synthetic */ void lambda$checkPassword$55$PaymentFormActivity(TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$TL_error, tLRPC$TL_account_getTmpPassword) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLRPC$TL_account_getTmpPassword f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$checkPassword$54$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkPassword$54 */
    public /* synthetic */ void lambda$checkPassword$54$PaymentFormActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword) {
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
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
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
}
