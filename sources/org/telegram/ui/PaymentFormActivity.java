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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendForm$46(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$TL_payments_paymentForm.bot_id));
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
    /* JADX WARNING: type inference failed for: r5v0 */
    /* JADX WARNING: type inference failed for: r9v2, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r9v3 */
    /* JADX WARNING: type inference failed for: r9v4 */
    /* JADX WARNING: type inference failed for: r9v6 */
    /* JADX WARNING: type inference failed for: r5v195, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r5v196 */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:315|316|317|318|319) */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0fc8, code lost:
        if (r5.email_requested == false) goto L_0x0fb9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x032f, code lost:
        if (r11.email_requested == false) goto L_0x0320;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Missing exception handler attribute for start block: B:318:0x0ae8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x1344  */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x13f5  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x141d  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x144a  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x144c  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x1477  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x14af  */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r36) {
        /*
            r35 = this;
            r7 = r35
            r8 = r36
            int r0 = r7.currentStep
            r9 = 6
            r10 = 5
            r11 = 4
            r12 = 3
            r13 = 2
            r14 = 1
            if (r0 != 0) goto L_0x001e
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627254(0x7f0e0cf6, float:1.8881767E38)
            java.lang.String r2 = "PaymentShippingInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x001e:
            if (r0 != r14) goto L_0x0030
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627255(0x7f0e0cf7, float:1.888177E38)
            java.lang.String r2 = "PaymentShippingMethod"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0030:
            if (r0 != r13) goto L_0x0042
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627212(0x7f0e0ccc, float:1.8881682E38)
            java.lang.String r2 = "PaymentCardInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x0042:
            if (r0 != r12) goto L_0x0054
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627212(0x7f0e0ccc, float:1.8881682E38)
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
            r2 = 2131627219(0x7f0e0cd3, float:1.8881696E38)
            java.lang.String r3 = "PaymentCheckout"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627219(0x7f0e0cd3, float:1.8881696E38)
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
            r2 = 2131627247(0x7f0e0cef, float:1.8881753E38)
            java.lang.String r3 = "PaymentReceipt"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x00b7:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627247(0x7f0e0cef, float:1.8881753E38)
            java.lang.String r2 = "PaymentReceipt"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x00d6
        L_0x00c6:
            if (r0 != r9) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131627236(0x7f0e0ce4, float:1.888173E38)
            java.lang.String r2 = "PaymentPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x00d6:
            org.telegram.ui.ActionBar.ActionBar r0 = r7.actionBar
            r1 = 2131165503(0x7var_f, float:1.7945225E38)
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
            r1 = 2131165533(0x7var_d, float:1.7945286E38)
            r2 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 2131625429(0x7f0e05d5, float:1.8878066E38)
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
            if (r0 != 0) goto L_0x0953
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x021d }
            java.io.InputStreamReader r11 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x021d }
            android.content.res.Resources r24 = r36.getResources()     // Catch:{ Exception -> 0x021d }
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
            org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6 r4 = org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6.INSTANCE
            java.util.Collections.sort(r0, r4)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r7.inputFields = r0
            r0 = 0
        L_0x022f:
            r4 = 10
            if (r0 >= r4) goto L_0x078a
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
            r9 = 2131627248(0x7f0e0cf0, float:1.8881755E38)
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
            r9 = 2131627258(0x7f0e0cfa, float:1.8881775E38)
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
            r9.<init>(r7, r8)
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
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21 r11 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda21
            r11.<init>(r7)
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
            r11 = 2131627253(0x7f0e0cf5, float:1.8881765E38)
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
            r11 = 2131627256(0x7f0e0cf8, float:1.8881771E38)
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
            r11 = 2131627262(0x7f0e0cfe, float:1.8881783E38)
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
            r11 = 2131627252(0x7f0e0cf4, float:1.8881763E38)
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
            r11 = 2131627261(0x7f0e0cfd, float:1.8881781E38)
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
            r11 = 2131627251(0x7f0e0cf3, float:1.8881761E38)
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
            r11 = 2131627250(0x7f0e0cf2, float:1.888176E38)
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
            r11 = 2131627249(0x7f0e0cf1, float:1.8881757E38)
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
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda24 r9 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda24
            r9.<init>(r7)
            r4.setOnEditorActionListener(r9)
            r4 = 9
            if (r0 != r4) goto L_0x077f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            boolean r9 = r4.email_to_provider
            if (r9 != 0) goto L_0x0664
            boolean r4 = r4.phone_to_provider
            if (r4 == 0) goto L_0x0648
            goto L_0x0664
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
            r27 = r6
            goto L_0x0711
        L_0x0664:
            r4 = 0
            r9 = 0
        L_0x0666:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            int r11 = r11.size()
            if (r9 >= r11) goto L_0x068e
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            java.lang.Object r11 = r11.get(r9)
            org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
            long r12 = r11.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r15 = r7.paymentForm
            r27 = r6
            long r5 = r15.provider_id
            int r15 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r15 != 0) goto L_0x0687
            r4 = r11
        L_0x0687:
            int r9 = r9 + 1
            r6 = r27
            r5 = 0
            r12 = 3
            goto L_0x0666
        L_0x068e:
            r27 = r6
            if (r4 == 0) goto L_0x069b
            java.lang.String r5 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r5, r4)
            goto L_0x069c
        L_0x069b:
            r4 = r3
        L_0x069c:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r6.<init>(r8)
            r5[r14] = r6
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r14]
            r6 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r6, (java.lang.String) r10)
            r5.setBackgroundDrawable(r9)
            android.widget.LinearLayout r5 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r6 = r7.bottomCell
            r6 = r6[r14]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r5.addView(r6, r12)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            boolean r6 = r5.email_to_provider
            if (r6 == 0) goto L_0x06e4
            boolean r5 = r5.phone_to_provider
            if (r5 == 0) goto L_0x06e4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r14]
            r6 = 2131627244(0x7f0e0cec, float:1.8881747E38)
            java.lang.Object[] r9 = new java.lang.Object[r14]
            r11 = 0
            r9[r11] = r4
            java.lang.String r4 = "PaymentPhoneEmailToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r6, r9)
            r5.setText(r4)
            goto L_0x0711
        L_0x06e4:
            if (r6 == 0) goto L_0x06fc
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r14]
            r6 = 2131627231(0x7f0e0cdf, float:1.888172E38)
            java.lang.Object[] r9 = new java.lang.Object[r14]
            r11 = 0
            r9[r11] = r4
            java.lang.String r4 = "PaymentEmailToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r6, r9)
            r5.setText(r4)
            goto L_0x0711
        L_0x06fc:
            r11 = 0
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r14]
            r6 = 2131627245(0x7f0e0ced, float:1.888175E38)
            java.lang.Object[] r9 = new java.lang.Object[r14]
            r9[r11] = r4
            java.lang.String r4 = "PaymentPhoneToProvider"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r6, r9)
            r5.setText(r4)
        L_0x0711:
            org.telegram.ui.Cells.TextCheckCell r4 = new org.telegram.ui.Cells.TextCheckCell
            r4.<init>(r8)
            r7.checkCell1 = r4
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r4.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            r5 = 2131627259(0x7f0e0cfb, float:1.8881777E38)
            java.lang.String r6 = "PaymentShippingSave"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            boolean r6 = r7.saveShippingInfo
            r9 = 0
            r4.setTextAndCheck(r5, r6, r9)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r5 = r7.checkCell1
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r5, r11)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda7
            r5.<init>(r7)
            r4.setOnClickListener(r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r5.<init>(r8)
            r6 = 0
            r4[r6] = r5
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r6]
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r5, (java.lang.String) r10)
            r4.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r6]
            r5 = 2131627260(0x7f0e0cfc, float:1.888178E38)
            java.lang.String r9 = "PaymentShippingSaveInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r4.setText(r5)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r5, r11)
            goto L_0x0781
        L_0x077f:
            r27 = r6
        L_0x0781:
            int r0 = r0 + 1
            r6 = r27
            r5 = 0
            r12 = 3
            r13 = 2
            goto L_0x022f
        L_0x078a:
            r27 = r6
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x07a5
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 6
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 8
            r0.setVisibility(r1)
            goto L_0x07a7
        L_0x07a5:
            r1 = 8
        L_0x07a7:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x07bc
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r0 = r0[r1]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x07bc:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x07d2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r1)
        L_0x07d2:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r1 = r0.phone_requested
            if (r1 == 0) goto L_0x07e7
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r9 = 9
            r0 = r0[r9]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x080c
        L_0x07e7:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            boolean r2 = r0.email_requested
            if (r2 == 0) goto L_0x07f7
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 7
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x080c
        L_0x07f7:
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x0804
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 6
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x080c
        L_0x0804:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 5
            r0 = r0[r2]
            r0.setImeOptions(r1)
        L_0x080c:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r1 = r0[r14]
            if (r1 == 0) goto L_0x082d
            r0 = r0[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0828
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0828
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0825
            goto L_0x0828
        L_0x0825:
            r1 = 8
            goto L_0x0829
        L_0x0828:
            r1 = 0
        L_0x0829:
            r0.setVisibility(r1)
            goto L_0x084d
        L_0x082d:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r1 = r0[r14]
            if (r1 == 0) goto L_0x084d
            r0 = r0[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0849
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0849
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0846
            goto L_0x0849
        L_0x0846:
            r1 = 8
            goto L_0x084a
        L_0x0849:
            r1 = 0
        L_0x084a:
            r0.setVisibility(r1)
        L_0x084d:
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r0 = r0[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            boolean r2 = r1.name_requested
            if (r2 != 0) goto L_0x0865
            boolean r2 = r1.phone_requested
            if (r2 != 0) goto L_0x0865
            boolean r1 = r1.email_requested
            if (r1 == 0) goto L_0x0862
            goto L_0x0865
        L_0x0862:
            r1 = 8
            goto L_0x0866
        L_0x0865:
            r1 = 0
        L_0x0866:
            r0.setVisibility(r1)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x08d4
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
            r0 = r0[r14]
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
        L_0x08d4:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08ec
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08ec
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r7.fillNumber(r0)
            goto L_0x08f0
        L_0x08ec:
            r1 = 0
            r7.fillNumber(r1)
        L_0x08f0:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 8
            r0 = r0[r1]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r0.invoice
            boolean r1 = r1.phone_requested
            if (r1 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x0910
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x1CLASSNAME
        L_0x0910:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0926 }
            java.lang.String r1 = "phone"
            java.lang.Object r0 = r0.getSystemService(r1)     // Catch:{ Exception -> 0x0926 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0926 }
            if (r0 == 0) goto L_0x092a
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0926 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x0926 }
            r2 = r0
            goto L_0x092b
        L_0x0926:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x092a:
            r2 = 0
        L_0x092b:
            if (r2 == 0) goto L_0x1CLASSNAME
            r1 = r27
            java.lang.Object r0 = r1.get(r2)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x1CLASSNAME
            java.util.ArrayList<java.lang.String> r1 = r7.countriesArray
            int r1 = r1.indexOf(r0)
            r2 = -1
            if (r1 == r2) goto L_0x1CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r2 = 8
            r1 = r1[r2]
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r7.countriesMap
            java.lang.Object r0 = r2.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r1.setText(r0)
            goto L_0x1CLASSNAME
        L_0x0953:
            r1 = 2
            r9 = 9
            if (r0 != r1) goto L_0x0ea5
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x098c
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0988 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm     // Catch:{ Exception -> 0x0988 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r1 = r1.native_params     // Catch:{ Exception -> 0x0988 }
            java.lang.String r1 = r1.data     // Catch:{ Exception -> 0x0988 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0988 }
            java.lang.String r1 = "google_pay_public_key"
            java.lang.String r1 = r0.optString(r1)     // Catch:{ Exception -> 0x0988 }
            boolean r4 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0988 }
            if (r4 != 0) goto L_0x0977
            r7.googlePayPublicKey = r1     // Catch:{ Exception -> 0x0988 }
        L_0x0977:
            java.lang.String r1 = "acquirer_bank_country"
            java.lang.String r1 = r0.optString(r1)     // Catch:{ Exception -> 0x0988 }
            r7.googlePayCountryCode = r1     // Catch:{ Exception -> 0x0988 }
            java.lang.String r1 = "gpay_parameters"
            org.json.JSONObject r0 = r0.optJSONObject(r1)     // Catch:{ Exception -> 0x0988 }
            r7.googlePayParameters = r0     // Catch:{ Exception -> 0x0988 }
            goto L_0x098c
        L_0x0988:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x098c:
            boolean r0 = r7.isWebView
            if (r0 == 0) goto L_0x0a99
            java.lang.String r0 = r7.googlePayPublicKey
            if (r0 != 0) goto L_0x0998
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x099b
        L_0x0998:
            r35.initGooglePay(r36)
        L_0x099b:
            r35.createGooglePayButton(r36)
            android.widget.LinearLayout r0 = r7.linearLayout2
            android.widget.FrameLayout r1 = r7.googlePayContainer
            r2 = 50
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            r7.webviewLoading = r14
            r7.showEditDoneProgress(r14, r14)
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
            r0.setJavaScriptEnabled(r14)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r14)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x09f6
            android.webkit.WebView r1 = r7.webView
            android.webkit.WebSettings r1 = r1.getSettings()
            r2 = 0
            r1.setMixedContentMode(r2)
            android.webkit.CookieManager r1 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r2 = r7.webView
            r1.setAcceptThirdPartyCookies(r2, r14)
        L_0x09f6:
            r1 = 17
            if (r0 < r1) goto L_0x0a07
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r1 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r2 = 0
            r1.<init>()
            java.lang.String r2 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r1, r2)
        L_0x0a07:
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
            r1 = 2131627215(0x7f0e0ccf, float:1.8881688E38)
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
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda15
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r2 = 0
            r0[r2] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r2]
            r1 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            r35.updateSavePaymentField()
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r2]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            goto L_0x1CLASSNAME
        L_0x0a99:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0afd
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0af9 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm     // Catch:{ Exception -> 0x0af9 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r1 = r1.native_params     // Catch:{ Exception -> 0x0af9 }
            java.lang.String r1 = r1.data     // Catch:{ Exception -> 0x0af9 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0af9 }
            java.lang.String r1 = "need_country"
            boolean r1 = r0.getBoolean(r1)     // Catch:{ Exception -> 0x0ab3 }
            r7.need_card_country = r1     // Catch:{ Exception -> 0x0ab3 }
            goto L_0x0ab6
        L_0x0ab3:
            r1 = 0
            r7.need_card_country = r1     // Catch:{ Exception -> 0x0af9 }
        L_0x0ab6:
            java.lang.String r1 = "need_zip"
            boolean r1 = r0.getBoolean(r1)     // Catch:{ Exception -> 0x0abf }
            r7.need_card_postcode = r1     // Catch:{ Exception -> 0x0abf }
            goto L_0x0ac2
        L_0x0abf:
            r1 = 0
            r7.need_card_postcode = r1     // Catch:{ Exception -> 0x0af9 }
        L_0x0ac2:
            java.lang.String r1 = "need_cardholder_name"
            boolean r1 = r0.getBoolean(r1)     // Catch:{ Exception -> 0x0acb }
            r7.need_card_name = r1     // Catch:{ Exception -> 0x0acb }
            goto L_0x0ace
        L_0x0acb:
            r1 = 0
            r7.need_card_name = r1     // Catch:{ Exception -> 0x0af9 }
        L_0x0ace:
            java.lang.String r1 = "public_token"
            boolean r1 = r0.has(r1)     // Catch:{ Exception -> 0x0af9 }
            if (r1 == 0) goto L_0x0adf
            java.lang.String r1 = "public_token"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0af9 }
            r7.providerApiKey = r1     // Catch:{ Exception -> 0x0af9 }
            goto L_0x0aea
        L_0x0adf:
            java.lang.String r1 = "publishable_key"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0ae8 }
            r7.providerApiKey = r1     // Catch:{ Exception -> 0x0ae8 }
            goto L_0x0aea
        L_0x0ae8:
            r7.providerApiKey = r3     // Catch:{ Exception -> 0x0af9 }
        L_0x0aea:
            java.lang.String r1 = "google_pay_hidden"
            r3 = 0
            boolean r0 = r0.optBoolean(r1, r3)     // Catch:{ Exception -> 0x0af9 }
            if (r0 != 0) goto L_0x0af5
            r0 = 1
            goto L_0x0af6
        L_0x0af5:
            r0 = 0
        L_0x0af6:
            r7.initGooglePay = r0     // Catch:{ Exception -> 0x0af9 }
            goto L_0x0afd
        L_0x0af9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0afd:
            boolean r0 = r7.initGooglePay
            if (r0 == 0) goto L_0x0b1c
            java.lang.String r0 = r7.providerApiKey
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b15
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            java.lang.String r0 = r0.native_provider
            java.lang.String r1 = "stripe"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0b19
        L_0x0b15:
            org.json.JSONObject r0 = r7.googlePayParameters
            if (r0 == 0) goto L_0x0b1c
        L_0x0b19:
            r35.initGooglePay(r36)
        L_0x0b1c:
            r1 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x0b22:
            if (r0 >= r1) goto L_0x0e6e
            if (r0 != 0) goto L_0x0b5b
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
            r3 = 2131627218(0x7f0e0cd2, float:1.8881694E38)
            java.lang.String r5 = "PaymentCardTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r4]
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r1.addView(r3, r6)
            goto L_0x0b91
        L_0x0b5b:
            r1 = 4
            if (r0 != r1) goto L_0x0b91
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
            r3 = 2131627209(0x7f0e0cc9, float:1.8881676E38)
            java.lang.String r4 = "PaymentBillingAddress"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r14]
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r1.addView(r3, r6)
        L_0x0b91:
            r1 = 3
            if (r0 == r1) goto L_0x0ba0
            r1 = 5
            if (r0 == r1) goto L_0x0ba0
            r1 = 4
            if (r0 != r1) goto L_0x0b9e
            boolean r1 = r7.need_card_postcode
            if (r1 == 0) goto L_0x0ba0
        L_0x0b9e:
            r1 = 1
            goto L_0x0ba1
        L_0x0ba0:
            r1 = 0
        L_0x0ba1:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r8)
            r4 = 0
            r3.setClipChildren(r4)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            android.widget.LinearLayout r4 = r7.linearLayout2
            r5 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r4.addView(r3, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r5 = new org.telegram.ui.Components.EditTextBoldCursor
            r5.<init>(r8)
            r4[r0] = r5
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
            r4.setTag(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r14, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            java.lang.String r5 = "windowBackgroundWhiteHintText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setHintTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4.setTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 0
            r4.setBackgroundDrawable(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4.setCursorColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r5)
            r4 = 3
            if (r0 != r4) goto L_0x0c4d
            android.text.InputFilter[] r5 = new android.text.InputFilter[r14]
            android.text.InputFilter$LengthFilter r6 = new android.text.InputFilter$LengthFilter
            r6.<init>(r4)
            r4 = 0
            r5[r4] = r6
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r4.setFilters(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 130(0x82, float:1.82E-43)
            r4.setInputType(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            android.text.method.PasswordTransformationMethod r5 = android.text.method.PasswordTransformationMethod.getInstance()
            r4.setTransformationMethod(r5)
            goto L_0x0CLASSNAME
        L_0x0c4d:
            if (r0 != 0) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 3
            r4.setInputType(r5)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = 4
            if (r0 != r4) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda22 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda22
            r5.<init>(r7)
            r4.setOnTouchListener(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 0
            r4.setInputType(r5)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r0 != r14) goto L_0x0c7c
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 16386(0x4002, float:2.2962E-41)
            r4.setInputType(r5)
            goto L_0x0CLASSNAME
        L_0x0c7c:
            r4 = 2
            if (r0 != r4) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 4097(0x1001, float:5.741E-42)
            r4.setInputType(r5)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 16385(0x4001, float:2.296E-41)
            r4.setInputType(r5)
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r5)
            if (r0 == 0) goto L_0x0d02
            if (r0 == r14) goto L_0x0cf1
            r4 = 2
            if (r0 == r4) goto L_0x0ce0
            r4 = 3
            if (r0 == r4) goto L_0x0ccf
            r4 = 4
            if (r0 == r4) goto L_0x0cbe
            r4 = 5
            if (r0 == r4) goto L_0x0cad
            goto L_0x0d12
        L_0x0cad:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 2131627262(0x7f0e0cfe, float:1.8881783E38)
            java.lang.String r6 = "PaymentShippingZipPlaceholder"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setHint(r5)
            goto L_0x0d12
        L_0x0cbe:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 2131627252(0x7f0e0cf4, float:1.8881763E38)
            java.lang.String r6 = "PaymentShippingCountry"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setHint(r5)
            goto L_0x0d12
        L_0x0ccf:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 2131627210(0x7f0e0cca, float:1.8881678E38)
            java.lang.String r6 = "PaymentCardCvv"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setHint(r5)
            goto L_0x0d12
        L_0x0ce0:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 2131627213(0x7f0e0ccd, float:1.8881684E38)
            java.lang.String r6 = "PaymentCardName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setHint(r5)
            goto L_0x0d12
        L_0x0cf1:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 2131627211(0x7f0e0ccb, float:1.888168E38)
            java.lang.String r6 = "PaymentCardExpireDate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setHint(r5)
            goto L_0x0d12
        L_0x0d02:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 2131627214(0x7f0e0cce, float:1.8881686E38)
            java.lang.String r6 = "PaymentCardNumber"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setHint(r5)
        L_0x0d12:
            if (r0 != 0) goto L_0x0d21
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$7 r5 = new org.telegram.ui.PaymentFormActivity$7
            r5.<init>()
            r4.addTextChangedListener(r5)
            goto L_0x0d2f
        L_0x0d21:
            if (r0 != r14) goto L_0x0d2f
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$8 r5 = new org.telegram.ui.PaymentFormActivity$8
            r5.<init>()
            r4.addTextChangedListener(r5)
        L_0x0d2f:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            r4.setPadding(r6, r6, r6, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0d47
            r5 = 5
            goto L_0x0d48
        L_0x0d47:
            r5 = 3
        L_0x0d48:
            r4.setGravity(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r7.inputFields
            r4 = r4[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda28 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda28
            r5.<init>(r7)
            r4.setOnEditorActionListener(r5)
            r4 = 3
            if (r0 != r4) goto L_0x0d8e
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            r5.<init>(r8)
            r6 = 0
            r4[r6] = r5
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r5, r11)
            goto L_0x0e2e
        L_0x0d8e:
            r4 = 5
            r6 = -2
            r9 = -1
            if (r0 != r4) goto L_0x0e0b
            org.telegram.ui.Cells.ShadowSectionCell[] r4 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            r5.<init>(r8)
            r11 = 2
            r4[r11] = r5
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r5 = r7.sectionCell
            r5 = r5[r11]
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r5, r11)
            org.telegram.ui.Cells.TextCheckCell r4 = new org.telegram.ui.Cells.TextCheckCell
            r4.<init>(r8)
            r7.checkCell1 = r4
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r4.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            r5 = 2131627215(0x7f0e0ccf, float:1.8881688E38)
            java.lang.String r6 = "PaymentCardSavePaymentInformation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            boolean r6 = r7.saveCardInfo
            r9 = 0
            r4.setTextAndCheck(r5, r6, r9)
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r5 = r7.checkCell1
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r5, r11)
            org.telegram.ui.Cells.TextCheckCell r4 = r7.checkCell1
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17 r5 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda17
            r5.<init>(r7)
            r4.setOnClickListener(r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r5.<init>(r8)
            r6 = 0
            r4[r6] = r5
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r7.bottomCell
            r4 = r4[r6]
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r5, (java.lang.String) r10)
            r4.setBackgroundDrawable(r9)
            r35.updateSavePaymentField()
            android.widget.LinearLayout r4 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r5 = r7.bottomCell
            r5 = r5[r6]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r4.addView(r5, r11)
            goto L_0x0e2e
        L_0x0e0b:
            if (r0 != 0) goto L_0x0e2e
            r35.createGooglePayButton(r36)
            android.widget.FrameLayout r4 = r7.googlePayContainer
            r27 = -2
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0e1c
            r5 = 3
            goto L_0x0e1d
        L_0x0e1c:
            r5 = 5
        L_0x0e1d:
            r29 = r5 | 16
            r30 = 0
            r31 = 0
            r32 = 1082130432(0x40800000, float:4.0)
            r33 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r5)
        L_0x0e2e:
            if (r1 == 0) goto L_0x0e4c
            org.telegram.ui.PaymentFormActivity$9 r1 = new org.telegram.ui.PaymentFormActivity$9
            r1.<init>(r7, r8)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r4)
            java.util.ArrayList<android.view.View> r4 = r7.dividers
            r4.add(r1)
            android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
            r5 = 83
            r6 = -1
            r4.<init>(r6, r14, r5)
            r3.addView(r1, r4)
        L_0x0e4c:
            r1 = 4
            if (r0 != r1) goto L_0x0e57
            boolean r1 = r7.need_card_country
            if (r1 == 0) goto L_0x0e54
            goto L_0x0e57
        L_0x0e54:
            r1 = 8
            goto L_0x0e66
        L_0x0e57:
            r1 = 5
            if (r0 != r1) goto L_0x0e5e
            boolean r1 = r7.need_card_postcode
            if (r1 == 0) goto L_0x0e54
        L_0x0e5e:
            r1 = 2
            if (r0 != r1) goto L_0x0e69
            boolean r1 = r7.need_card_name
            if (r1 != 0) goto L_0x0e69
            goto L_0x0e54
        L_0x0e66:
            r3.setVisibility(r1)
        L_0x0e69:
            int r0 = r0 + 1
            r1 = 6
            goto L_0x0b22
        L_0x0e6e:
            boolean r0 = r7.need_card_country
            if (r0 != 0) goto L_0x0e87
            boolean r0 = r7.need_card_postcode
            if (r0 != 0) goto L_0x0e87
            org.telegram.ui.Cells.HeaderCell[] r0 = r7.headerCell
            r0 = r0[r14]
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r2 = 0
            r0 = r0[r2]
            r0.setVisibility(r1)
        L_0x0e87:
            boolean r0 = r7.need_card_postcode
            if (r0 == 0) goto L_0x0e98
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r1 = 5
            r0 = r0[r1]
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r1)
            goto L_0x1CLASSNAME
        L_0x0e98:
            r1 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r7.inputFields
            r2 = 3
            r0 = r0[r2]
            r0.setImeOptions(r1)
            goto L_0x1CLASSNAME
        L_0x0ea5:
            if (r0 != r14) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r7.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r1 = new org.telegram.ui.Cells.RadioCell[r0]
            r7.radioCells = r1
            r1 = 0
        L_0x0eb4:
            if (r1 >= r0) goto L_0x0var_
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
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r4 = r2.prices
            java.lang.String r4 = r7.getTotalPriceString(r4)
            r6 = 0
            r5[r6] = r4
            java.lang.String r2 = r2.title
            r5[r14] = r2
            java.lang.String r2 = "%s - %s"
            java.lang.String r2 = java.lang.String.format(r2, r5)
            if (r1 != 0) goto L_0x0efd
            r4 = 1
            goto L_0x0efe
        L_0x0efd:
            r4 = 0
        L_0x0efe:
            int r5 = r0 + -1
            if (r1 == r5) goto L_0x0var_
            r5 = 1
            goto L_0x0var_
        L_0x0var_:
            r5 = 0
        L_0x0var_:
            r3.setText(r2, r4, r5)
            org.telegram.ui.Cells.RadioCell[] r2 = r7.radioCells
            r2 = r2[r1]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda10
            r3.<init>(r7)
            r2.setOnClickListener(r3)
            android.widget.LinearLayout r2 = r7.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r3 = r7.radioCells
            r3 = r3[r1]
            r2.addView(r3)
            int r1 = r1 + 1
            goto L_0x0eb4
        L_0x0var_:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r2 = 0
            r0[r2] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r2]
            r1 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r2]
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
            goto L_0x1CLASSNAME
        L_0x0var_:
            r1 = 3
            if (r0 != r1) goto L_0x118f
            r1 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x0var_:
            if (r0 >= r1) goto L_0x1CLASSNAME
            if (r0 != 0) goto L_0x0f8b
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
            r3 = 2131627218(0x7f0e0cd2, float:1.8881694E38)
            java.lang.String r5 = "PaymentCardTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r4]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r1.addView(r3, r9)
            goto L_0x0f8d
        L_0x0f8b:
            r4 = 0
            r6 = -1
        L_0x0f8d:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r1.setClipChildren(r4)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r4 = 50
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r4)
            r3.addView(r1, r5)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            if (r0 == r14) goto L_0x0fab
            r3 = 1
            goto L_0x0fac
        L_0x0fab:
            r3 = 0
        L_0x0fac:
            r4 = 7
            if (r3 == 0) goto L_0x0fcb
            if (r0 != r4) goto L_0x0fbb
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            boolean r5 = r5.phone_requested
            if (r5 != 0) goto L_0x0fbb
        L_0x0fb9:
            r3 = 0
            goto L_0x0fcb
        L_0x0fbb:
            r5 = 6
            if (r0 != r5) goto L_0x0fcb
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            boolean r6 = r5.phone_requested
            if (r6 != 0) goto L_0x0fcb
            boolean r5 = r5.email_requested
            if (r5 != 0) goto L_0x0fcb
            goto L_0x0fb9
        L_0x0fcb:
            if (r3 == 0) goto L_0x0fe9
            org.telegram.ui.PaymentFormActivity$10 r3 = new org.telegram.ui.PaymentFormActivity$10
            r3.<init>(r7, r8)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r5)
            java.util.ArrayList<android.view.View> r5 = r7.dividers
            r5.add(r3)
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            r6 = 83
            r9 = -1
            r5.<init>(r9, r14, r6)
            r1.addView(r3, r5)
        L_0x0fe9:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r5 = new org.telegram.ui.Components.EditTextBoldCursor
            r5.<init>(r8)
            r3[r0] = r5
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
            r3.setTag(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1098907648(0x41800000, float:16.0)
            r3.setTextSize(r14, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.String r5 = "windowBackgroundWhiteHintText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setHintTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 0
            r3.setBackgroundDrawable(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setCursorColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r5)
            if (r0 != 0) goto L_0x105b
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda23 r5 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda23.INSTANCE
            r3.setOnTouchListener(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 0
            r3.setInputType(r5)
            goto L_0x106d
        L_0x105b:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 129(0x81, float:1.81E-43)
            r3.setInputType(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r5)
        L_0x106d:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 268435462(0x10000006, float:2.5243567E-29)
            r3.setImeOptions(r5)
            if (r0 == 0) goto L_0x1094
            if (r0 == r14) goto L_0x107c
            goto L_0x10a1
        L_0x107c:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 2131626350(0x7f0e096e, float:1.8879934E38)
            java.lang.String r6 = "LoginPassword"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setHint(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r3.requestFocus()
            goto L_0x10a1
        L_0x1094:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r5 = r5.saved_credentials
            java.lang.String r5 = r5.title
            r3.setText(r5)
        L_0x10a1:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            r3.setPadding(r6, r6, r6, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x10b9
            r5 = 5
            goto L_0x10ba
        L_0x10b9:
            r5 = 3
        L_0x10ba:
            r3.setGravity(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r1.addView(r3, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda25 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda25
            r3.<init>(r7)
            r1.setOnEditorActionListener(r3)
            if (r0 != r14) goto L_0x118a
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r5 = 0
            r1[r5] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131627228(0x7f0e0cdc, float:1.8881715E38)
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
            r3 = 2131165483(0x7var_b, float:1.7945184E38)
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
            r3 = 2131627229(0x7f0e0cdd, float:1.8881717E38)
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
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda5
            r3.<init>(r7)
            r1.setOnClickListener(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r14]
            r3 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r10)
            r1.setBackgroundDrawable(r5)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r14]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r1.addView(r3, r9)
        L_0x118a:
            int r0 = r0 + 1
            r1 = 2
            goto L_0x0var_
        L_0x118f:
            r1 = 4
            if (r0 == r1) goto L_0x14f6
            r1 = 5
            if (r0 != r1) goto L_0x1197
            goto L_0x14f6
        L_0x1197:
            r1 = 6
            if (r0 != r1) goto L_0x1CLASSNAME
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r8)
            r7.codeFieldCell = r0
            r1 = 2131627195(0x7f0e0cbb, float:1.8881648E38)
            java.lang.String r4 = "PasswordCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r4 = 0
            r0.setTextAndHint(r3, r1, r4)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r7.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r1 = 3
            r0.setInputType(r1)
            r1 = 6
            r0.setImeOptions(r1)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda27 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda27
            r1.<init>(r7)
            r0.setOnEditorActionListener(r1)
            org.telegram.ui.PaymentFormActivity$20 r1 = new org.telegram.ui.PaymentFormActivity$20
            r1.<init>()
            r0.addTextChangedListener(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r1 = r7.codeFieldCell
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r1.<init>(r8)
            r3 = 2
            r0[r3] = r1
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r7.bottomCell
            r0 = r0[r3]
            r1 = 2131165483(0x7var_b, float:1.7945184E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r3]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r5)
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
            r1 = 2131627782(0x7f0e0var_, float:1.8882838E38)
            java.lang.String r3 = "ResendCode"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1, r14)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r14]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r1, r5)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r14]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda16
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r1 = new org.telegram.ui.Cells.TextSettingsCell
            r1.<init>(r8)
            r3 = 0
            r0[r3] = r1
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r3]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r3]
            java.lang.String r1 = "windowBackgroundWhiteRedText3"
            r0.setTag(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r3]
            java.lang.String r1 = "windowBackgroundWhiteRedText3"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r3]
            r1 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r4 = "AbortPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setText(r1, r3)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r1 = r7.settingsCell
            r1 = r1[r3]
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r1, r6)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r7.settingsCell
            r0 = r0[r3]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda14
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            r1 = 3
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r1]
            r7.inputFields = r0
            r0 = 0
        L_0x12b6:
            if (r0 >= r1) goto L_0x14f1
            if (r0 != 0) goto L_0x12ef
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
            r3 = 2131627243(0x7f0e0ceb, float:1.8881745E38)
            java.lang.String r5 = "PaymentPasswordTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r4]
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r1.addView(r3, r6)
            goto L_0x1326
        L_0x12ef:
            r1 = 2
            if (r0 != r1) goto L_0x1326
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
            r3 = 2131627239(0x7f0e0ce7, float:1.8881737E38)
            java.lang.String r4 = "PaymentPasswordEmailTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r3 = r7.headerCell
            r3 = r3[r14]
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r1.addView(r3, r6)
            goto L_0x1327
        L_0x1326:
            r5 = -1
        L_0x1327:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r3 = 0
            r1.setClipChildren(r3)
            android.widget.LinearLayout r3 = r7.linearLayout2
            r4 = 50
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r3.addView(r1, r6)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r3)
            if (r0 != 0) goto L_0x1360
            org.telegram.ui.PaymentFormActivity$21 r3 = new org.telegram.ui.PaymentFormActivity$21
            r3.<init>(r7, r8)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r5)
            java.util.ArrayList<android.view.View> r5 = r7.dividers
            r5.add(r3)
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            r6 = 83
            r9 = -1
            r5.<init>(r9, r14, r6)
            r1.addView(r3, r5)
        L_0x1360:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r5 = new org.telegram.ui.Components.EditTextBoldCursor
            r5.<init>(r8)
            r3[r0] = r5
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
            r3.setTag(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1098907648(0x41800000, float:16.0)
            r3.setTextSize(r14, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            java.lang.String r5 = "windowBackgroundWhiteHintText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setHintTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setTextColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 0
            r3.setBackgroundDrawable(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3.setCursorColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3.setCursorSize(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r5)
            if (r0 == 0) goto L_0x13d7
            if (r0 != r14) goto L_0x13c3
            goto L_0x13d7
        L_0x13c3:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 33
            r3.setInputType(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 268435462(0x10000006, float:2.5243567E-29)
            r3.setImeOptions(r5)
            goto L_0x13f3
        L_0x13d7:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 129(0x81, float:1.81E-43)
            r3.setInputType(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
            r3.setTypeface(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 268435461(0x10000005, float:2.5243564E-29)
            r3.setImeOptions(r5)
        L_0x13f3:
            if (r0 == 0) goto L_0x141d
            if (r0 == r14) goto L_0x140c
            r3 = 2
            if (r0 == r3) goto L_0x13fb
            goto L_0x1434
        L_0x13fb:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 2131627237(0x7f0e0ce5, float:1.8881733E38)
            java.lang.String r6 = "PaymentPasswordEmail"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setHint(r5)
            goto L_0x1434
        L_0x140c:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 2131627242(0x7f0e0cea, float:1.8881743E38)
            java.lang.String r6 = "PaymentPasswordReEnter"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setHint(r5)
            goto L_0x1434
        L_0x141d:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 2131627240(0x7f0e0ce8, float:1.8881739E38)
            java.lang.String r6 = "PaymentPasswordEnter"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setHint(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r3.requestFocus()
        L_0x1434:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            r3.setPadding(r6, r6, r6, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x144c
            r5 = 5
            goto L_0x144d
        L_0x144c:
            r5 = 3
        L_0x144d:
            r3.setGravity(r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r7.inputFields
            r3 = r3[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r1.addView(r3, r5)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r0]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda26 r3 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda26
            r3.<init>(r7)
            r1.setOnEditorActionListener(r3)
            if (r0 != r14) goto L_0x14af
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r5 = 0
            r1[r5] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131627241(0x7f0e0ce9, float:1.888174E38)
            java.lang.String r6 = "PaymentPasswordInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r5]
            r3 = 2131165483(0x7var_b, float:1.7945184E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r3, (java.lang.String) r10)
            r1.setBackgroundDrawable(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r5]
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r1.addView(r3, r9)
            goto L_0x14e9
        L_0x14af:
            r1 = 2
            if (r0 != r1) goto L_0x14e9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r14]
            r3 = 2131627238(0x7f0e0ce6, float:1.8881735E38)
            java.lang.String r5 = "PaymentPasswordEmailInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r1 = r7.bottomCell
            r1 = r1[r14]
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r5, (java.lang.String) r10)
            r1.setBackgroundDrawable(r3)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r7.bottomCell
            r3 = r3[r14]
            r6 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r1.addView(r3, r11)
            goto L_0x14ec
        L_0x14e9:
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
        L_0x14ec:
            int r0 = r0 + 1
            r1 = 3
            goto L_0x12b6
        L_0x14f1:
            r35.updatePasswordFields()
            goto L_0x1CLASSNAME
        L_0x14f6:
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r8)
            r7.paymentInfoCell = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.messenger.MessageObject r0 = r7.messageObject
            if (r0 == 0) goto L_0x1519
            org.telegram.ui.Cells.PaymentInfoCell r1 = r7.paymentInfoCell
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) r0
            java.lang.String r4 = r7.currentBotName
            r1.setInvoice(r0, r4)
            goto L_0x1524
        L_0x1519:
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r0 = r7.paymentReceipt
            if (r0 == 0) goto L_0x1524
            org.telegram.ui.Cells.PaymentInfoCell r1 = r7.paymentInfoCell
            java.lang.String r4 = r7.currentBotName
            r1.setReceipt(r0, r4)
        L_0x1524:
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r1 = r7.paymentInfoCell
            r4 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r4)
            r0.addView(r1, r12)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            r1.<init>(r8)
            r12 = 0
            r0[r12] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r12]
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r4)
            r0.addView(r1, r12)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r1.prices
            r0.<init>(r1)
            r7.prices = r0
            org.telegram.tgnet.TLRPC$TL_shippingOption r1 = r7.shippingOption
            if (r1 == 0) goto L_0x155e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r1.prices
            r0.addAll(r1)
        L_0x155e:
            java.lang.String[] r0 = new java.lang.String[r14]
            r7.totalPrice = r0
            r0 = 0
        L_0x1563:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r7.prices
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x15a1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r7.prices
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r1 = (org.telegram.tgnet.TLRPC$TL_labeledPrice) r1
            org.telegram.ui.Cells.TextPriceCell r11 = new org.telegram.ui.Cells.TextPriceCell
            r11.<init>(r8)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r11.setBackgroundColor(r12)
            java.lang.String r12 = r1.label
            org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
            long r4 = r1.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.lang.String r1 = r1.currency
            java.lang.String r1 = r13.formatCurrencyString(r4, r1)
            r4 = 0
            r11.setTextAndValue(r12, r1, r4)
            android.widget.LinearLayout r1 = r7.linearLayout2
            r1.addView(r11)
            int r0 = r0 + 1
            r4 = -2
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            goto L_0x1563
        L_0x15a1:
            int r0 = r7.currentStep
            r1 = 5
            if (r0 != r1) goto L_0x15dc
            java.lang.Long r0 = r7.tipAmount
            if (r0 == 0) goto L_0x15dc
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            r1 = 2131627266(0x7f0e0d02, float:1.8881792E38)
            java.lang.String r4 = "PaymentTip"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.Long r5 = r7.tipAmount
            long r11 = r5.longValue()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            java.lang.String r5 = r5.currency
            java.lang.String r4 = r4.formatCurrencyString(r11, r5)
            r5 = 0
            r0.setTextAndValue(r1, r4, r5)
            android.widget.LinearLayout r1 = r7.linearLayout2
            r1.addView(r0)
        L_0x15dc:
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r8)
            r7.totalCell = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            java.lang.String[] r0 = r7.totalPrice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r1 = r7.prices
            java.lang.String r1 = r7.getTotalPriceString(r1)
            r4 = 0
            r0[r4] = r1
            org.telegram.ui.Cells.TextPriceCell r0 = r7.totalCell
            r1 = 2131627270(0x7f0e0d06, float:1.88818E38)
            java.lang.String r5 = "PaymentTransactionTotal"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            java.lang.String[] r5 = r7.totalPrice
            r5 = r5[r4]
            r0.setTextAndValue(r1, r5, r14)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x18ad
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            int r0 = r0.flags
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x18ad
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r1 = 0
            r0.setClipChildren(r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r1 = r7.linearLayout2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r4 = r4.invoice
            java.util.ArrayList<java.lang.Long> r4 = r4.suggested_tip_amounts
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x1637
            r4 = 40
            goto L_0x1639
        L_0x1637:
            r4 = 78
        L_0x1639:
            r5 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r1.addView(r0, r4)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda9
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.TextPriceCell r1 = new org.telegram.ui.Cells.TextPriceCell
            r1.<init>(r8)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r1.setBackgroundColor(r4)
            r4 = 2131627267(0x7f0e0d03, float:1.8881794E38)
            java.lang.String r5 = "PaymentTipOptional"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 0
            r1.setTextAndValue(r4, r3, r5)
            r0.addView(r1)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = new org.telegram.ui.Components.EditTextBoldCursor[r14]
            r7.inputFields = r1
            org.telegram.ui.Components.EditTextBoldCursor r4 = new org.telegram.ui.Components.EditTextBoldCursor
            r4.<init>(r8)
            r1[r5] = r4
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r1.setTag(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r4 = 1098907648(0x41800000, float:16.0)
            r1.setTextSize(r14, r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            java.lang.String r4 = "windowBackgroundWhiteGrayText2"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setHintTextColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            java.lang.String r4 = "windowBackgroundWhiteGrayText2"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setTextColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r4 = 0
            r1.setBackgroundDrawable(r4)
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
            r11 = 0
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r13 = r13.invoice
            java.lang.String r13 = r13.currency
            java.lang.String r2 = r2.formatCurrencyString(r11, r13)
            r1.setHint(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r5, r5, r5, r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x1707
            r2 = 3
            goto L_0x1708
        L_0x1707:
            r2 = 5
        L_0x1708:
            r1.setGravity(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1091567616(0x41100000, float:9.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r5 = 0
            r1 = r1[r5]
            org.telegram.ui.PaymentFormActivity$11 r2 = new org.telegram.ui.PaymentFormActivity$11
            r2.<init>()
            r1.addTextChangedListener(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda29 r2 = org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda29.INSTANCE
            r1.setOnEditorActionListener(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r1 = r7.inputFields
            r1 = r1[r5]
            r1.requestFocus()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.util.ArrayList<java.lang.Long> r1 = r1.suggested_tip_amounts
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x18a5
            android.widget.HorizontalScrollView r11 = new android.widget.HorizontalScrollView
            r11.<init>(r8)
            r11.setHorizontalScrollBarEnabled(r5)
            r11.setVerticalScrollBarEnabled(r5)
            r11.setClipToPadding(r5)
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r11.setPadding(r1, r5, r2, r5)
            r11.setFillViewport(r14)
            r25 = -1
            r26 = 1106247680(0x41var_, float:30.0)
            r27 = 51
            r28 = 0
            r29 = 1110441984(0x42300000, float:44.0)
            r30 = 0
            r31 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r11, r1)
            int[] r0 = new int[r14]
            int[] r12 = new int[r14]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r1 = r1.invoice
            java.util.ArrayList<java.lang.Long> r1 = r1.suggested_tip_amounts
            int r13 = r1.size()
            org.telegram.ui.PaymentFormActivity$12 r2 = new org.telegram.ui.PaymentFormActivity$12
            r1 = r2
            r17 = r4
            r9 = 2131165484(0x7var_c, float:1.7945186E38)
            r4 = r2
            r2 = r35
            r18 = r3
            r3 = r36
            r15 = r4
            r4 = r13
            r9 = 0
            r5 = r0
            r34 = r6
            r6 = r12
            r1.<init>(r2, r3, r4, r5, r6)
            r7.tipLayout = r15
            r15.setOrientation(r9)
            android.widget.LinearLayout r1 = r7.tipLayout
            r2 = 30
            r3 = 51
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createScroll(r4, r2, r3)
            r11.addView(r1, r2)
            java.lang.String r1 = "contacts_inviteBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r5 = 0
        L_0x17c3:
            if (r5 >= r13) goto L_0x18b4
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x17dd
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            int r3 = r13 - r5
            int r3 = r3 - r14
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            goto L_0x17ed
        L_0x17dd:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            java.util.ArrayList<java.lang.Long> r2 = r2.suggested_tip_amounts
            java.lang.Object r2 = r2.get(r5)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x17ed:
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r6 = r6.invoice
            java.lang.String r6 = r6.currency
            java.lang.String r4 = r4.formatCurrencyString(r2, r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r8)
            r11 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r14, r11)
            java.lang.String r11 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
            r6.setTypeface(r11)
            r6.setLines(r14)
            java.lang.Long r11 = java.lang.Long.valueOf(r2)
            r6.setTag(r11)
            r6.setMaxLines(r14)
            r6.setText(r4)
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r15 = 1097859072(0x41700000, float:15.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.setPadding(r11, r9, r15, r9)
            java.lang.String r11 = "chats_secretName"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r6.setTextColor(r11)
            r11 = 1097859072(0x41700000, float:15.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r15 = 536870911(0x1fffffff, float:1.0842021E-19)
            r15 = r15 & r1
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r11, r15)
            r6.setBackground(r11)
            r6.setSingleLine(r14)
            r11 = 17
            r6.setGravity(r11)
            android.widget.LinearLayout r11 = r7.tipLayout
            r26 = -2
            r27 = -1
            r28 = 19
            r29 = 0
            r30 = 0
            int r15 = r13 + -1
            if (r5 == r15) goto L_0x1862
            r31 = 9
            goto L_0x1864
        L_0x1862:
            r31 = 0
        L_0x1864:
            r32 = 0
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32)
            r11.addView(r6, r15)
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19 r11 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda19
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
            r3 = 2131230951(0x7var_e7, float:1.807797E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            r6.setTag(r3, r4)
            r3 = r0[r9]
            int r3 = java.lang.Math.max(r3, r2)
            r0[r9] = r3
            r3 = r12[r9]
            int r3 = r3 + r2
            r12[r9] = r3
            int r5 = r5 + 1
            goto L_0x17c3
        L_0x18a5:
            r18 = r3
            r17 = r4
            r34 = r6
            r9 = 0
            goto L_0x18b4
        L_0x18ad:
            r18 = r3
            r34 = r6
            r9 = 0
            r17 = 0
        L_0x18b4:
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
            r1 = 2131165484(0x7var_c, float:1.7945186E38)
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
            r0[r9] = r1
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r9]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r9]
            java.lang.String r1 = r7.cardName
            if (r1 == 0) goto L_0x1924
            int r1 = r1.length()
            if (r1 <= r14) goto L_0x1924
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = r7.cardName
            java.lang.String r3 = r3.substring(r9, r14)
            java.lang.String r3 = r3.toUpperCase()
            r1.append(r3)
            java.lang.String r3 = r7.cardName
            java.lang.String r3 = r3.substring(r14)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            goto L_0x1926
        L_0x1924:
            java.lang.String r1 = r7.cardName
        L_0x1926:
            r3 = 2131627221(0x7f0e0cd5, float:1.88817E38)
            java.lang.String r4 = "PaymentCheckoutMethod"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 2131165992(0x7var_, float:1.7946217E38)
            r0.setTextAndValueAndIcon(r1, r3, r4, r14)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r9]
            r0.addView(r1)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x194f
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r9]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda18
            r1.<init>(r7)
            r0.setOnClickListener(r1)
        L_0x194f:
            r0 = r17
            r5 = 0
        L_0x1952:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r1.users
            int r1 = r1.size()
            if (r5 >= r1) goto L_0x1974
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = r7.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r1.users
            java.lang.Object r1 = r1.get(r5)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            long r3 = r1.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r6 = r7.paymentForm
            long r11 = r6.provider_id
            int r6 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r6 != 0) goto L_0x1971
            r0 = r1
        L_0x1971:
            int r5 = r5 + 1
            goto L_0x1952
        L_0x1974:
            if (r0 == 0) goto L_0x19c0
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r3 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r3.<init>(r8)
            r1[r14] = r3
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r14]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r1.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r14]
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r0 = 2131627226(0x7f0e0cda, float:1.888171E38)
            java.lang.String r4 = "PaymentCheckoutProvider"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r4 = 2131165997(0x7var_d, float:1.7946227E38)
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r5 = r7.validateRequest
            if (r5 == 0) goto L_0x19b2
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.info
            org.telegram.tgnet.TLRPC$TL_postAddress r5 = r5.shipping_address
            if (r5 != 0) goto L_0x19b0
            org.telegram.tgnet.TLRPC$TL_shippingOption r5 = r7.shippingOption
            if (r5 == 0) goto L_0x19b2
        L_0x19b0:
            r5 = 1
            goto L_0x19b3
        L_0x19b2:
            r5 = 0
        L_0x19b3:
            r1.setTextAndValueAndIcon(r3, r0, r4, r5)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r14]
            r0.addView(r1)
            goto L_0x19c2
        L_0x19c0:
            r3 = r18
        L_0x19c2:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            if (r0 == 0) goto L_0x1b0b
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            org.telegram.tgnet.TLRPC$TL_postAddress r0 = r0.shipping_address
            if (r0 == 0) goto L_0x1a07
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r4 = 2
            r0[r4] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r0.addView(r1)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x19fc
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda11
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            goto L_0x1a07
        L_0x19fc:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
        L_0x1a07:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.name
            if (r0 == 0) goto L_0x1a4a
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r4 = 3
            r0[r4] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r0.addView(r1)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1a3f
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda8
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            goto L_0x1a4a
        L_0x1a3f:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
        L_0x1a4a:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.phone
            if (r0 == 0) goto L_0x1a8c
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r4 = 4
            r0[r4] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r0.addView(r1)
            int r0 = r7.currentStep
            if (r0 != r4) goto L_0x1a81
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda12
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            goto L_0x1a8c
        L_0x1a81:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
        L_0x1a8c:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r7.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.email
            if (r0 == 0) goto L_0x1acf
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r4 = 5
            r0[r4] = r1
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r0.addView(r1)
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1ac4
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda6
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            goto L_0x1acf
        L_0x1ac4:
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
        L_0x1acf:
            org.telegram.tgnet.TLRPC$TL_shippingOption r0 = r7.shippingOption
            if (r0 == 0) goto L_0x1b08
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r1 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r1.<init>(r8)
            r4 = 6
            r0[r4] = r1
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r7.detailSettingsCell
            r0 = r0[r4]
            org.telegram.tgnet.TLRPC$TL_shippingOption r1 = r7.shippingOption
            java.lang.String r1 = r1.title
            r5 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r6 = "PaymentCheckoutShippingMethod"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131165993(0x7var_, float:1.7946219E38)
            r0.setTextAndValueAndIcon(r1, r5, r6, r9)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r1 = r7.detailSettingsCell
            r1 = r1[r4]
            r0.addView(r1)
        L_0x1b08:
            r35.setAddressFields()
        L_0x1b0b:
            int r0 = r7.currentStep
            r1 = 4
            if (r0 != r1) goto L_0x1CLASSNAME
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.bottomLayout = r0
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x1b2d
            java.lang.String r4 = "listSelectorSDK21"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r5 = "contacts_inviteBackground"
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r4, (java.lang.String) r5)
            r0.setBackgroundDrawable(r4)
            goto L_0x1b36
        L_0x1b2d:
            java.lang.String r4 = "contacts_inviteBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColor(r4)
        L_0x1b36:
            android.widget.FrameLayout r0 = r7.bottomLayout
            r4 = 48
            r5 = 80
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r4, (int) r5)
            r5 = r34
            r5.addView(r0, r4)
            android.widget.FrameLayout r0 = r7.bottomLayout
            org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20 r4 = new org.telegram.ui.PaymentFormActivity$$ExternalSyntheticLambda20
            r4.<init>(r7, r3)
            r0.setOnClickListener(r4)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.payTextView = r0
            java.lang.String r3 = "contacts_inviteText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r7.payTextView
            r3 = 2131627224(0x7f0e0cd8, float:1.8881706E38)
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
            r0.<init>(r7, r8)
            r7.webView = r0
            r0.setBackgroundColor(r6)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setJavaScriptEnabled(r14)
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r14)
            r0 = 21
            if (r1 < r0) goto L_0x1CLASSNAME
            android.webkit.WebView r0 = r7.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setMixedContentMode(r9)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r1 = r7.webView
            r0.setAcceptThirdPartyCookies(r1, r14)
        L_0x1CLASSNAME:
            android.webkit.WebView r0 = r7.webView
            org.telegram.ui.PaymentFormActivity$19 r1 = new org.telegram.ui.PaymentFormActivity$19
            r1.<init>()
            r0.setWebViewClient(r1)
            android.webkit.WebView r0 = r7.webView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1)
            r5.addView(r0, r1)
            android.webkit.WebView r0 = r7.webView
            r1 = 8
            r0.setVisibility(r1)
        L_0x1CLASSNAME:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
            r1.<init>(r8)
            r0[r14] = r1
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r7.sectionCell
            r0 = r0[r14]
            r1 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r7.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r7.sectionCell
            r1 = r1[r14]
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r2)
        L_0x1CLASSNAME:
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
            countrySelectActivity.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda57(this));
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
            countrySelectActivity.setCountrySelectActivityDelegate(new PaymentFormActivity$$ExternalSyntheticLambda58(this));
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
    public /* synthetic */ void lambda$createView$18(View view) {
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
    public /* synthetic */ void lambda$createView$19(View view) {
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
    public /* synthetic */ void lambda$createView$20(View view) {
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
    public /* synthetic */ void lambda$createView$21(View view) {
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
    public /* synthetic */ void lambda$createView$23(String str, View view) {
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
            builder.setPositiveButton(LocaleController.getString("Continue", NUM), new PaymentFormActivity$$ExternalSyntheticLambda2(this));
            showDialog(builder.create());
            return;
        }
        showPayAlert(this.totalPrice[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$22(DialogInterface dialogInterface, int i) {
        showPayAlert(this.totalPrice[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$24(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$26(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), PaymentFormActivity$$ExternalSyntheticLambda55.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$28(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
        if (this.currentPassword.has_secure_values) {
            string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
        }
        builder.setMessage(string);
        builder.setTitle(LocaleController.getString("TurnPasswordOffQuestionTitle", NUM));
        builder.setPositiveButton(LocaleController.getString("Disable", NUM), new PaymentFormActivity$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView2 = (TextView) create.getButton(-1);
        if (textView2 != null) {
            textView2.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$27(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$29(TextView textView2, int i, KeyEvent keyEvent) {
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
        this.googlePayButton.setOnClickListener(new PaymentFormActivity$$ExternalSyntheticLambda13(this));
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
    public /* synthetic */ void lambda$createGooglePayButton$30(android.view.View r5) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.lambda$createGooglePayButton$30(android.view.View):void");
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda48(this), 10);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$33(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda39(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$32(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
                PaymentFormActivity$$ExternalSyntheticLambda31 paymentFormActivity$$ExternalSyntheticLambda31 = new PaymentFormActivity$$ExternalSyntheticLambda31(this);
                this.shortPollRunnable = paymentFormActivity$$ExternalSyntheticLambda31;
                AndroidUtilities.runOnUIThread(paymentFormActivity$$ExternalSyntheticLambda31, 5000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$31() {
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
        builder.setPositiveButton(LocaleController.getString("Continue", NUM), new PaymentFormActivity$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPayAlert$34(DialogInterface dialogInterface, int i) {
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
                this.paymentsClient.isReadyToPay(fromJson).addOnCompleteListener(getParentActivity(), new PaymentFormActivity$$ExternalSyntheticLambda30(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initGooglePay$35(Task task) {
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
                    AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda32(this), 100);
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
    public /* synthetic */ void lambda$onTransitionAnimationEnd$36() {
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
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda33(this, i2, intent));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResultFragment$37(int i, Intent intent) {
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
            Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda45(this, z, str2, str, tLRPC$TL_account_updatePasswordSettings));
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_confirmPasswordEmail, new PaymentFormActivity$$ExternalSyntheticLambda47(this), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda38(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$38(TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$sendSavePassword$44(boolean z, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda44(this, tLRPC$TL_error, z, tLObject, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$45(boolean z, String str, String str2, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        PaymentFormActivity$$ExternalSyntheticLambda54 paymentFormActivity$$ExternalSyntheticLambda54 = new PaymentFormActivity$$ExternalSyntheticLambda54(this, z, str);
        if (!z) {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str2);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                tLRPC$TL_account_updatePasswordSettings.new_settings.new_password_hash = SRPHelper.getVBytes(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
                if (tLRPC$TL_account_updatePasswordSettings.new_settings.new_password_hash == null) {
                    TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                    tLRPC$TL_error.text = "ALGO_INVALID";
                    paymentFormActivity$$ExternalSyntheticLambda54.run((TLObject) null, tLRPC$TL_error);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, paymentFormActivity$$ExternalSyntheticLambda54, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            paymentFormActivity$$ExternalSyntheticLambda54.run((TLObject) null, tLRPC$TL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, paymentFormActivity$$ExternalSyntheticLambda54, 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$43(TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, String str) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new PaymentFormActivity$$ExternalSyntheticLambda53(this, z), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$41(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda42(this, tLRPC$TL_error, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$40(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            sendSavePassword(z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSavePassword$42(String str, DialogInterface dialogInterface, int i) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new PaymentFormActivity$$ExternalSyntheticLambda50(this, this.validateRequest), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendForm$49(TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject2 instanceof TLRPC$TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda35(this, tLObject2));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda40(this, tLRPC$TL_error, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendForm$47(TLObject tLObject) {
        this.requestedInfo = (TLRPC$TL_payments_validatedRequestedInfo) tLObject;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
            tLRPC$TL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_clearSavedInfo, PaymentFormActivity$$ExternalSyntheticLambda56.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendForm$48(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_sendPaymentForm, new PaymentFormActivity$$ExternalSyntheticLambda52(this, tLRPC$TL_payments_sendPaymentForm), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$53(TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda43(this, tLRPC$TL_error, tLRPC$TL_payments_sendPaymentForm));
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
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda46(this, tLRPC$MessageArr));
        } else if (tLObject instanceof TLRPC$TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda34(this, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$50(TLRPC$Message[] tLRPC$MessageArr) {
        goToNextStep();
        if (this.parentFragment instanceof ChatActivity) {
            ((ChatActivity) this.parentFragment).getUndoView().showWithAction(0, 77, (Object) AndroidUtilities.replaceTags(LocaleController.formatString("PaymentInfoHint", NUM, this.totalPrice[0], this.currentItemName)), (Object) tLRPC$MessageArr[0], (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendData$51(TLObject tLObject) {
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
    public /* synthetic */ void lambda$sendData$52(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPassword, new PaymentFormActivity$$ExternalSyntheticLambda49(this, obj, tLRPC$TL_account_getPassword), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$58(String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda41(this, tLRPC$TL_error, tLObject, str, tLRPC$TL_account_getPassword));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$57(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            } else if (!tLRPC$TL_account_password.has_password) {
                this.passwordOk = false;
                goToNextStep();
            } else {
                Utilities.globalQueue.postRunnable(new PaymentFormActivity$$ExternalSyntheticLambda37(this, tLRPC$TL_account_password, AndroidUtilities.getStringBytes(str)));
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_getPassword, new Object[0]);
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$56(TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr) {
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword = new TLRPC$TL_account_getTmpPassword();
        tLRPC$TL_account_getTmpPassword.period = 1800;
        PaymentFormActivity$$ExternalSyntheticLambda51 paymentFormActivity$$ExternalSyntheticLambda51 = new PaymentFormActivity$$ExternalSyntheticLambda51(this, tLRPC$TL_account_getTmpPassword);
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getTmpPassword.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                paymentFormActivity$$ExternalSyntheticLambda51.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getTmpPassword, paymentFormActivity$$ExternalSyntheticLambda51, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        paymentFormActivity$$ExternalSyntheticLambda51.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$55(TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$ExternalSyntheticLambda36(this, tLObject, tLRPC$TL_error, tLRPC$TL_account_getTmpPassword));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPassword$54(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword) {
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
