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
import com.google.android.gms.wallet.PaymentDataRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
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
import org.telegram.tgnet.TLRPC$User;
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
    private PaymentsClient paymentsClient;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public ContextProgressView progressViewButton;
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
    /* access modifiers changed from: private */
    public String stripeApiKey;
    private boolean swipeBackEnabled;
    private TextView textView;
    private TLRPC$TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    /* access modifiers changed from: private */
    public String webViewUrl;
    /* access modifiers changed from: private */
    public boolean webviewLoading;

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay);

        void onFragmentDestroyed();
    }

    static /* synthetic */ boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$null$17(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$null$37(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public PaymentFormActivity(MessageObject messageObject2, TLRPC$TL_payments_paymentReceipt tLRPC$TL_payments_paymentReceipt) {
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
        tLRPC$TL_payments_paymentForm.bot_id = tLRPC$TL_payments_paymentReceipt.bot_id;
        tLRPC$TL_payments_paymentForm.invoice = tLRPC$TL_payments_paymentReceipt.invoice;
        tLRPC$TL_payments_paymentForm.provider_id = tLRPC$TL_payments_paymentReceipt.provider_id;
        tLRPC$TL_payments_paymentForm.users = tLRPC$TL_payments_paymentReceipt.users;
        this.shippingOption = tLRPC$TL_payments_paymentReceipt.shipping;
        this.messageObject = messageObject2;
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$TL_payments_paymentReceipt.bot_id));
        this.botUser = user;
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
        }
        this.currentItemName = messageObject2.messageOwner.media.title;
        if (tLRPC$TL_payments_paymentReceipt.info != null) {
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo = new TLRPC$TL_payments_validateRequestedInfo();
            this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
            tLRPC$TL_payments_validateRequestedInfo.info = tLRPC$TL_payments_paymentReceipt.info;
        }
        this.cardName = tLRPC$TL_payments_paymentReceipt.credentials_title;
    }

    public PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2) {
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
        TLRPC$TL_invoice tLRPC$TL_invoice = tLRPC$TL_payments_paymentForm.invoice;
        int i = 0;
        if (!tLRPC$TL_invoice.shipping_address_requested && !tLRPC$TL_invoice.email_requested && !tLRPC$TL_invoice.name_requested && !tLRPC$TL_invoice.phone_requested) {
            if (tLRPC$TL_payments_paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(this.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i = 2;
            }
        }
        init(tLRPC$TL_payments_paymentForm, messageObject2, i, (TLRPC$TL_payments_validatedRequestedInfo) null, (TLRPC$TL_shippingOption) null, (String) null, (String) null, (TLRPC$TL_payments_validateRequestedInfo) null, false, (TLRPC$TL_inputPaymentCredentialsGooglePay) null);
    }

    private PaymentFormActivity(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, int i, TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo, TLRPC$TL_shippingOption tLRPC$TL_shippingOption, String str, String str2, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
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
        init(tLRPC$TL_payments_paymentForm, messageObject2, i, tLRPC$TL_payments_validatedRequestedInfo, tLRPC$TL_shippingOption, str, str2, tLRPC$TL_payments_validateRequestedInfo, z, tLRPC$TL_inputPaymentCredentialsGooglePay);
    }

    private void setCurrentPassword(TLRPC$TL_account_password tLRPC$TL_account_password) {
        if (!tLRPC$TL_account_password.has_password) {
            this.currentPassword = tLRPC$TL_account_password;
            if (tLRPC$TL_account_password != null) {
                this.waitingForEmail = !TextUtils.isEmpty(tLRPC$TL_account_password.email_unconfirmed_pattern);
            }
            updatePasswordFields();
        } else if (getParentActivity() != null) {
            goToNextStep();
        }
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    private void init(TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm, MessageObject messageObject2, int i, TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo, TLRPC$TL_shippingOption tLRPC$TL_shippingOption, String str, String str2, TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo, boolean z, TLRPC$TL_inputPaymentCredentialsGooglePay tLRPC$TL_inputPaymentCredentialsGooglePay) {
        this.currentStep = i;
        this.paymentJson = str;
        this.googlePayCredentials = tLRPC$TL_inputPaymentCredentialsGooglePay;
        this.requestedInfo = tLRPC$TL_payments_validatedRequestedInfo;
        this.paymentForm = tLRPC$TL_payments_paymentForm;
        this.shippingOption = tLRPC$TL_shippingOption;
        this.messageObject = messageObject2;
        this.saveCardInfo = z;
        boolean z2 = true;
        this.isWebView = !"stripe".equals(tLRPC$TL_payments_paymentForm.native_provider);
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$TL_payments_paymentForm.bot_id));
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
            if (this.paymentForm.saved_credentials == null) {
                z2 = false;
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

    /* JADX WARNING: Can't wrap try/catch for region: R(23:284|285|286|287|288|289|290|291|292|293|294|295|296|297|298|299|300|301|302|303|308|(1:310)(1:311)|312) */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0aae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0aaf, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x0var_, code lost:
        if (r9.email_requested == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0321, code lost:
        if (r13.email_requested == false) goto L_0x0312;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:289:0x0a7b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:294:0x0a86 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:299:0x0a91 */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x12c6  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x137b  */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x13a3  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x13cf  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x13d1  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x13fc  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x1433  */
    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r35) {
        /*
            r34 = this;
            r1 = r34
            r2 = r35
            int r0 = r1.currentStep
            r3 = 6
            r4 = 5
            r5 = 4
            r6 = 3
            r7 = 2
            r8 = 1
            if (r0 != 0) goto L_0x001e
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626626(0x7f0e0a82, float:1.8880494E38)
            java.lang.String r10 = "PaymentShippingInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x001e:
            if (r0 != r8) goto L_0x0030
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626627(0x7f0e0a83, float:1.8880496E38)
            java.lang.String r10 = "PaymentShippingMethod"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x0030:
            if (r0 != r7) goto L_0x0042
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            java.lang.String r10 = "PaymentCardInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x0042:
            if (r0 != r6) goto L_0x0054
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
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
            r10 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            java.lang.String r11 = "PaymentCheckout"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626593(0x7f0e0a61, float:1.8880427E38)
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
            r10 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r11 = "PaymentReceipt"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x00b7:
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r10 = "PaymentReceipt"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x00d6
        L_0x00c6:
            if (r0 != r3) goto L_0x00d6
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131626608(0x7f0e0a70, float:1.8880457E38)
            java.lang.String r10 = "PaymentPassword"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
        L_0x00d6:
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            r9 = 2131165466(0x7var_a, float:1.794515E38)
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
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            if (r9 == 0) goto L_0x0104
            if (r9 == r8) goto L_0x0104
            if (r9 == r7) goto L_0x0104
            if (r9 == r6) goto L_0x0104
            if (r9 == r5) goto L_0x0104
            if (r9 != r3) goto L_0x0144
        L_0x0104:
            r9 = 2131165496(0x7var_, float:1.794521E38)
            r12 = 1113587712(0x42600000, float:56.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r13 = 2131625129(0x7f0e04a9, float:1.8877457E38)
            java.lang.String r14 = "Done"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItemWithWidth(r8, r9, r12, r13)
            r1.doneItem = r0
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r2, r8)
            r1.progressView = r0
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
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10)
            r0.addView(r9, r12)
        L_0x0144:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r2)
            r1.fragmentView = r0
            r9 = r0
            android.widget.FrameLayout r9 = (android.widget.FrameLayout) r9
            java.lang.String r12 = "windowBackgroundGray"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r0.setBackgroundColor(r12)
            android.widget.ScrollView r0 = new android.widget.ScrollView
            r0.<init>(r2)
            r1.scrollView = r0
            r0.setFillViewport(r8)
            android.widget.ScrollView r0 = r1.scrollView
            java.lang.String r12 = "actionBarDefault"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor((android.widget.ScrollView) r0, (int) r12)
            android.widget.ScrollView r0 = r1.scrollView
            r12 = -1
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 51
            r15 = 0
            r16 = 0
            r17 = 0
            int r10 = r1.currentStep
            if (r10 != r5) goto L_0x0182
            r10 = 1111490560(0x42400000, float:48.0)
            r18 = 1111490560(0x42400000, float:48.0)
            goto L_0x0185
        L_0x0182:
            r10 = 0
            r18 = 0
        L_0x0185:
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r9.addView(r0, r10)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r2)
            r1.linearLayout2 = r0
            r0.setOrientation(r8)
            android.widget.ScrollView r0 = r1.scrollView
            android.widget.LinearLayout r10 = r1.linearLayout2
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            r13 = -2
            r12.<init>(r11, r13)
            r0.addView(r10, r12)
            int r0 = r1.currentStep
            java.lang.String r5 = "windowBackgroundWhiteBlackText"
            java.lang.String r10 = "windowBackgroundGrayShadow"
            java.lang.String r23 = "windowBackgroundWhite"
            r12 = 0
            if (r0 != 0) goto L_0x0921
            java.util.HashMap r9 = new java.util.HashMap
            r9.<init>()
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0218 }
            java.io.InputStreamReader r15 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0218 }
            android.content.res.Resources r19 = r35.getResources()     // Catch:{ Exception -> 0x0218 }
            android.content.res.AssetManager r14 = r19.getAssets()     // Catch:{ Exception -> 0x0218 }
            java.lang.String r3 = "countries.txt"
            java.io.InputStream r3 = r14.open(r3)     // Catch:{ Exception -> 0x0218 }
            r15.<init>(r3)     // Catch:{ Exception -> 0x0218 }
            r0.<init>(r15)     // Catch:{ Exception -> 0x0218 }
        L_0x01d3:
            java.lang.String r3 = r0.readLine()     // Catch:{ Exception -> 0x0218 }
            if (r3 == 0) goto L_0x0214
            java.lang.String r14 = ";"
            java.lang.String[] r3 = r3.split(r14)     // Catch:{ Exception -> 0x0218 }
            java.util.ArrayList<java.lang.String> r14 = r1.countriesArray     // Catch:{ Exception -> 0x0218 }
            r15 = r3[r7]     // Catch:{ Exception -> 0x0218 }
            r14.add(r12, r15)     // Catch:{ Exception -> 0x0218 }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r1.countriesMap     // Catch:{ Exception -> 0x0218 }
            r15 = r3[r7]     // Catch:{ Exception -> 0x0218 }
            r11 = r3[r12]     // Catch:{ Exception -> 0x0218 }
            r14.put(r15, r11)     // Catch:{ Exception -> 0x0218 }
            java.util.HashMap<java.lang.String, java.lang.String> r11 = r1.codesMap     // Catch:{ Exception -> 0x0218 }
            r14 = r3[r12]     // Catch:{ Exception -> 0x0218 }
            r15 = r3[r7]     // Catch:{ Exception -> 0x0218 }
            r11.put(r14, r15)     // Catch:{ Exception -> 0x0218 }
            r11 = r3[r8]     // Catch:{ Exception -> 0x0218 }
            r14 = r3[r7]     // Catch:{ Exception -> 0x0218 }
            r4.put(r11, r14)     // Catch:{ Exception -> 0x0218 }
            int r11 = r3.length     // Catch:{ Exception -> 0x0218 }
            if (r11 <= r6) goto L_0x020b
            java.util.HashMap<java.lang.String, java.lang.String> r11 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0218 }
            r14 = r3[r12]     // Catch:{ Exception -> 0x0218 }
            r15 = r3[r6]     // Catch:{ Exception -> 0x0218 }
            r11.put(r14, r15)     // Catch:{ Exception -> 0x0218 }
        L_0x020b:
            r11 = r3[r8]     // Catch:{ Exception -> 0x0218 }
            r3 = r3[r7]     // Catch:{ Exception -> 0x0218 }
            r9.put(r11, r3)     // Catch:{ Exception -> 0x0218 }
            r11 = -1
            goto L_0x01d3
        L_0x0214:
            r0.close()     // Catch:{ Exception -> 0x0218 }
            goto L_0x021c
        L_0x0218:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021c:
            java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
            org.telegram.ui.-$$Lambda$Ds7dtVnGrflEw4-LvNOxA0cDT4Y r3 = org.telegram.ui.$$Lambda$Ds7dtVnGrflEw4LvNOxA0cDT4Y.INSTANCE
            java.util.Collections.sort(r0, r3)
            r0 = 10
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r0]
            r1.inputFields = r0
            r0 = 0
        L_0x022a:
            r3 = 10
            r11 = 9
            if (r0 >= r3) goto L_0x0760
            if (r0 != 0) goto L_0x0268
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r14 = new org.telegram.ui.Cells.HeaderCell
            r14.<init>(r2)
            r3[r12] = r14
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r14)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            r14 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            java.lang.String r15 = "PaymentShippingAddress"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r3.setText(r14)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r14 = r1.headerCell
            r14 = r14[r12]
            r15 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r13)
            r3.addView(r14, r7)
            r3 = 8
            r14 = -1
            goto L_0x02b8
        L_0x0268:
            r3 = 6
            r15 = -1
            if (r0 != r3) goto L_0x02b5
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r7 = new org.telegram.ui.Cells.ShadowSectionCell
            r7.<init>(r2)
            r3[r12] = r7
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r7 = r1.sectionCell
            r7 = r7[r12]
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r13)
            r3.addView(r7, r14)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r7 = new org.telegram.ui.Cells.HeaderCell
            r7.<init>(r2)
            r3[r8] = r7
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r7)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            r7 = 2131626630(0x7f0e0a86, float:1.8880502E38)
            java.lang.String r14 = "PaymentShippingReceiver"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r14, r7)
            r3.setText(r7)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r7 = r1.headerCell
            r7 = r7[r8]
            r14 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r13)
            r3.addView(r7, r15)
            goto L_0x02b6
        L_0x02b5:
            r14 = -1
        L_0x02b6:
            r3 = 8
        L_0x02b8:
            if (r0 != r3) goto L_0x02d6
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r2)
            r3.setOrientation(r12)
            android.widget.LinearLayout r7 = r1.linearLayout2
            r15 = 50
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15)
            r7.addView(r3, r13)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r7)
            goto L_0x0342
        L_0x02d6:
            if (r0 != r11) goto L_0x02e5
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r7 = 8
            r3 = r3[r7]
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            goto L_0x0342
        L_0x02e5:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            android.widget.LinearLayout r7 = r1.linearLayout2
            r13 = 50
            r14 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r13)
            r7.addView(r3, r15)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r7)
            r7 = 5
            if (r0 == r7) goto L_0x0304
            if (r0 == r11) goto L_0x0304
            r7 = 1
            goto L_0x0305
        L_0x0304:
            r7 = 0
        L_0x0305:
            if (r7 == 0) goto L_0x0324
            r13 = 7
            if (r0 != r13) goto L_0x0314
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r13 = r13.invoice
            boolean r13 = r13.phone_requested
            if (r13 != 0) goto L_0x0314
        L_0x0312:
            r7 = 0
            goto L_0x0324
        L_0x0314:
            r13 = 6
            if (r0 != r13) goto L_0x0324
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r13 = r13.invoice
            boolean r14 = r13.phone_requested
            if (r14 != 0) goto L_0x0324
            boolean r13 = r13.email_requested
            if (r13 != 0) goto L_0x0324
            goto L_0x0312
        L_0x0324:
            if (r7 == 0) goto L_0x0342
            org.telegram.ui.PaymentFormActivity$2 r7 = new org.telegram.ui.PaymentFormActivity$2
            r7.<init>(r1, r2)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r7.setBackgroundColor(r13)
            java.util.ArrayList<android.view.View> r13 = r1.dividers
            r13.add(r7)
            android.widget.FrameLayout$LayoutParams r13 = new android.widget.FrameLayout$LayoutParams
            r14 = 83
            r15 = -1
            r13.<init>(r15, r8, r14)
            r3.addView(r7, r13)
        L_0x0342:
            if (r0 != r11) goto L_0x034e
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            org.telegram.ui.Components.HintEditText r13 = new org.telegram.ui.Components.HintEditText
            r13.<init>(r2)
            r7[r0] = r13
            goto L_0x0357
        L_0x034e:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r13 = new org.telegram.ui.Components.EditTextBoldCursor
            r13.<init>(r2)
            r7[r0] = r13
        L_0x0357:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)
            r7.setTag(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 1098907648(0x41800000, float:16.0)
            r7.setTextSize(r8, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r13 = "windowBackgroundWhiteHintText"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r7.setHintTextColor(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r7.setTextColor(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 0
            r7.setBackgroundDrawable(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r7.setCursorColor(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 1101004800(0x41a00000, float:20.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.setCursorSize(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 1069547520(0x3fCLASSNAME, float:1.5)
            r7.setCursorWidth(r13)
            r7 = 4
            if (r0 != r7) goto L_0x03c3
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$IfUWsdY6A3YdC7z4Rkp3xdEZ1P8 r13 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$IfUWsdY6A3YdC7z4Rkp3xdEZ1P8
            r13.<init>()
            r7.setOnTouchListener(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setInputType(r12)
        L_0x03c3:
            if (r0 == r11) goto L_0x03df
            r7 = 8
            if (r0 != r7) goto L_0x03ca
            goto L_0x03df
        L_0x03ca:
            r7 = 7
            if (r0 != r7) goto L_0x03d5
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setInputType(r8)
            goto L_0x03e6
        L_0x03d5:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 16385(0x4001, float:2.296E-41)
            r7.setInputType(r13)
            goto L_0x03e6
        L_0x03df:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setInputType(r6)
        L_0x03e6:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 268435461(0x10000005, float:2.5243564E-29)
            r7.setImeOptions(r13)
            switch(r0) {
                case 0: goto L_0x0505;
                case 1: goto L_0x04e1;
                case 2: goto L_0x04bd;
                case 3: goto L_0x0499;
                case 4: goto L_0x0460;
                case 5: goto L_0x043b;
                case 6: goto L_0x0418;
                case 7: goto L_0x03f5;
                default: goto L_0x03f3;
            }
        L_0x03f3:
            goto L_0x0528
        L_0x03f5:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626625(0x7f0e0a81, float:1.8880491E38)
            java.lang.String r14 = "PaymentShippingEmailPlaceholder"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            java.lang.String r7 = r7.email
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            r13.setText(r7)
            goto L_0x0528
        L_0x0418:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            java.lang.String r14 = "PaymentShippingName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            java.lang.String r7 = r7.name
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            r13.setText(r7)
            goto L_0x0528
        L_0x043b:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            java.lang.String r14 = "PaymentShippingZipPlaceholder"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.String r7 = r7.post_code
            r13.setText(r7)
            goto L_0x0528
        L_0x0460:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626624(0x7f0e0a80, float:1.888049E38)
            java.lang.String r14 = "PaymentShippingCountry"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            if (r7 == 0) goto L_0x0528
            java.lang.String r7 = r7.country_iso2
            java.lang.Object r7 = r4.get(r7)
            java.lang.String r7 = (java.lang.String) r7
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r13 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r13 = r13.saved_info
            org.telegram.tgnet.TLRPC$TL_postAddress r13 = r13.shipping_address
            java.lang.String r13 = r13.country_iso2
            r1.countryName = r13
            org.telegram.ui.Components.EditTextBoldCursor[] r14 = r1.inputFields
            r14 = r14[r0]
            if (r7 == 0) goto L_0x0493
            goto L_0x0494
        L_0x0493:
            r7 = r13
        L_0x0494:
            r14.setText(r7)
            goto L_0x0528
        L_0x0499:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626633(0x7f0e0a89, float:1.8880508E38)
            java.lang.String r14 = "PaymentShippingStatePlaceholder"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.String r7 = r7.state
            r13.setText(r7)
            goto L_0x0528
        L_0x04bd:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            java.lang.String r14 = "PaymentShippingCityPlaceholder"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.String r7 = r7.city
            r13.setText(r7)
            goto L_0x0528
        L_0x04e1:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            java.lang.String r14 = "PaymentShippingAddress2Placeholder"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.String r7 = r7.street_line2
            r13.setText(r7)
            goto L_0x0528
        L_0x0505:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            java.lang.String r14 = "PaymentShippingAddress1Placeholder"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.setHint(r13)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r7 = r7.saved_info
            if (r7 == 0) goto L_0x0528
            org.telegram.tgnet.TLRPC$TL_postAddress r7 = r7.shipping_address
            if (r7 == 0) goto L_0x0528
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.String r7 = r7.street_line1
            r13.setText(r7)
        L_0x0528:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r13 = r7[r0]
            r7 = r7[r0]
            int r7 = r7.length()
            r13.setSelection(r7)
            r7 = 8
            if (r0 != r7) goto L_0x05af
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r2)
            r1.textView = r7
            java.lang.String r13 = "+"
            r7.setText(r13)
            android.widget.TextView r7 = r1.textView
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r7.setTextColor(r13)
            android.widget.TextView r7 = r1.textView
            r13 = 1098907648(0x41800000, float:16.0)
            r7.setTextSize(r8, r13)
            android.widget.TextView r7 = r1.textView
            r27 = -2
            r28 = -2
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 0
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r3.addView(r7, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.setPadding(r13, r12, r12, r12)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 19
            r7.setGravity(r13)
            android.text.InputFilter[] r7 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r13 = new android.text.InputFilter$LengthFilter
            r14 = 5
            r13.<init>(r14)
            r7[r12] = r13
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            r13.setFilters(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r27 = 55
            r29 = 0
            r31 = 1101529088(0x41a80000, float:21.0)
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r3.addView(r7, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$3 r7 = new org.telegram.ui.PaymentFormActivity$3
            r7.<init>()
            r3.addTextChangedListener(r7)
            goto L_0x0619
        L_0x05af:
            if (r0 != r11) goto L_0x05e5
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setPadding(r12, r12, r12, r12)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 19
            r7.setGravity(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r27 = -1
            r28 = -2
            r29 = 0
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 1101529088(0x41a80000, float:21.0)
            r32 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r3.addView(r7, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.PaymentFormActivity$4 r7 = new org.telegram.ui.PaymentFormActivity$4
            r7.<init>()
            r3.addTextChangedListener(r7)
            goto L_0x0619
        L_0x05e5:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.setPadding(r12, r12, r12, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x05fc
            r13 = 5
            goto L_0x05fd
        L_0x05fc:
            r13 = 3
        L_0x05fd:
            r7.setGravity(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r7, r13)
        L_0x0619:
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$Olw4mziI4Y7UZ6FQRVf9ETMmZig r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$Olw4mziI4Y7UZ6FQRVf9ETMmZig
            r7.<init>()
            r3.setOnEditorActionListener(r7)
            if (r0 != r11) goto L_0x075a
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            boolean r7 = r3.email_to_provider
            if (r7 != 0) goto L_0x064e
            boolean r3 = r3.phone_to_provider
            if (r3 == 0) goto L_0x0634
            goto L_0x064e
        L_0x0634:
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r7 = new org.telegram.ui.Cells.ShadowSectionCell
            r7.<init>(r2)
            r3[r8] = r7
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r7 = r1.sectionCell
            r7 = r7[r8]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r3.addView(r7, r14)
            goto L_0x06ef
        L_0x064e:
            r3 = 0
            r7 = 0
        L_0x0650:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            int r11 = r11.size()
            if (r7 >= r11) goto L_0x0670
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r11.users
            java.lang.Object r11 = r11.get(r7)
            org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
            int r13 = r11.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r14 = r1.paymentForm
            int r14 = r14.provider_id
            if (r13 != r14) goto L_0x066d
            r3 = r11
        L_0x066d:
            int r7 = r7 + 1
            goto L_0x0650
        L_0x0670:
            if (r3 == 0) goto L_0x067b
            java.lang.String r7 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r7, r3)
            goto L_0x067d
        L_0x067b:
            java.lang.String r3 = ""
        L_0x067d:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r11 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r11.<init>(r2)
            r7[r8] = r11
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r8]
            r11 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r11, (java.lang.String) r10)
            r7.setBackgroundDrawable(r13)
            android.widget.LinearLayout r7 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r11 = r1.bottomCell
            r11 = r11[r8]
            r13 = -2
            r14 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r13)
            r7.addView(r11, r15)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r7 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r7 = r7.invoice
            boolean r11 = r7.email_to_provider
            if (r11 == 0) goto L_0x06c4
            boolean r7 = r7.phone_to_provider
            if (r7 == 0) goto L_0x06c4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r8]
            r11 = 2131626616(0x7f0e0a78, float:1.8880473E38)
            java.lang.Object[] r13 = new java.lang.Object[r8]
            r13[r12] = r3
            java.lang.String r3 = "PaymentPhoneEmailToProvider"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r11, r13)
            r7.setText(r3)
            goto L_0x06ef
        L_0x06c4:
            if (r11 == 0) goto L_0x06db
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r8]
            r11 = 2131626604(0x7f0e0a6c, float:1.8880449E38)
            java.lang.Object[] r13 = new java.lang.Object[r8]
            r13[r12] = r3
            java.lang.String r3 = "PaymentEmailToProvider"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r11, r13)
            r7.setText(r3)
            goto L_0x06ef
        L_0x06db:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r8]
            r11 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.Object[] r13 = new java.lang.Object[r8]
            r13[r12] = r3
            java.lang.String r3 = "PaymentPhoneToProvider"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r11, r13)
            r7.setText(r3)
        L_0x06ef:
            org.telegram.ui.Cells.TextCheckCell r3 = new org.telegram.ui.Cells.TextCheckCell
            r3.<init>(r2)
            r1.checkCell1 = r3
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r3.setBackgroundDrawable(r7)
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            r7 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            java.lang.String r11 = "PaymentShippingSave"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            boolean r11 = r1.saveShippingInfo
            r3.setTextAndCheck(r7, r11, r12)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r7 = r1.checkCell1
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r3.addView(r7, r14)
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$PexWzZmxWLN1J6v8efPEtLRkUHI r7 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$PexWzZmxWLN1J6v8efPEtLRkUHI
            r7.<init>()
            r3.setOnClickListener(r7)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r7.<init>(r2)
            r3[r12] = r7
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r12]
            r7 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r7, (java.lang.String) r10)
            r3.setBackgroundDrawable(r11)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r12]
            r7 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            java.lang.String r11 = "PaymentShippingSaveInfo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r3.setText(r7)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r12]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r3.addView(r7, r14)
        L_0x075a:
            int r0 = r0 + 1
            r7 = 2
            r13 = -2
            goto L_0x022a
        L_0x0760:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.name_requested
            if (r0 != 0) goto L_0x0779
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 6
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r2 = 8
            r0.setVisibility(r2)
            goto L_0x077b
        L_0x0779:
            r2 = 8
        L_0x077b:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.phone_requested
            if (r0 != 0) goto L_0x0790
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r2]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
        L_0x0790:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.email_requested
            if (r0 != 0) goto L_0x07a6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 7
            r0 = r0[r3]
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.setVisibility(r2)
        L_0x07a6:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r2 = r0.phone_requested
            if (r2 == 0) goto L_0x07b9
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r11]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x07de
        L_0x07b9:
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            boolean r3 = r0.email_requested
            if (r3 == 0) goto L_0x07c9
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 7
            r0 = r0[r3]
            r0.setImeOptions(r2)
            goto L_0x07de
        L_0x07c9:
            boolean r0 = r0.name_requested
            if (r0 == 0) goto L_0x07d6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 6
            r0 = r0[r3]
            r0.setImeOptions(r2)
            goto L_0x07de
        L_0x07d6:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r3 = 5
            r0 = r0[r3]
            r0.setImeOptions(r2)
        L_0x07de:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r2 = r0[r8]
            if (r2 == 0) goto L_0x07ff
            r0 = r0[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r3 = r2.name_requested
            if (r3 != 0) goto L_0x07fa
            boolean r3 = r2.phone_requested
            if (r3 != 0) goto L_0x07fa
            boolean r2 = r2.email_requested
            if (r2 == 0) goto L_0x07f7
            goto L_0x07fa
        L_0x07f7:
            r2 = 8
            goto L_0x07fb
        L_0x07fa:
            r2 = 0
        L_0x07fb:
            r0.setVisibility(r2)
            goto L_0x081f
        L_0x07ff:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r2 = r0[r8]
            if (r2 == 0) goto L_0x081f
            r0 = r0[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r3 = r2.name_requested
            if (r3 != 0) goto L_0x081b
            boolean r3 = r2.phone_requested
            if (r3 != 0) goto L_0x081b
            boolean r2 = r2.email_requested
            if (r2 == 0) goto L_0x0818
            goto L_0x081b
        L_0x0818:
            r2 = 8
            goto L_0x081c
        L_0x081b:
            r2 = 0
        L_0x081c:
            r0.setVisibility(r2)
        L_0x081f:
            org.telegram.ui.Cells.HeaderCell[] r0 = r1.headerCell
            r0 = r0[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r2 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r2.invoice
            boolean r3 = r2.name_requested
            if (r3 != 0) goto L_0x0837
            boolean r3 = r2.phone_requested
            if (r3 != 0) goto L_0x0837
            boolean r2 = r2.email_requested
            if (r2 == 0) goto L_0x0834
            goto L_0x0837
        L_0x0834:
            r2 = 8
            goto L_0x0838
        L_0x0837:
            r2 = 0
        L_0x0838:
            r0.setVisibility(r2)
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r0 = r0.invoice
            boolean r0 = r0.shipping_address_requested
            if (r0 != 0) goto L_0x08a4
            org.telegram.ui.Cells.HeaderCell[] r0 = r1.headerCell
            r0 = r0[r12]
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r0 = r0[r12]
            r0.setVisibility(r2)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r12]
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
            r0 = r0[r6]
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
        L_0x08a4:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08bc
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08bc
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            java.lang.String r0 = r0.phone
            r1.fillNumber(r0)
            goto L_0x08c0
        L_0x08bc:
            r2 = 0
            r1.fillNumber(r2)
        L_0x08c0:
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 8
            r0 = r0[r2]
            int r0 = r0.length()
            if (r0 != 0) goto L_0x186f
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r2 = r0.invoice
            boolean r2 = r2.phone_requested
            if (r2 == 0) goto L_0x186f
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.saved_info
            if (r0 == 0) goto L_0x08e0
            java.lang.String r0 = r0.phone
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x186f
        L_0x08e0:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r2 = "phone"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ Exception -> 0x08f6 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x08f6 }
            if (r0 == 0) goto L_0x08fa
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x08f6 }
            r5 = r0
            goto L_0x08fb
        L_0x08f6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08fa:
            r5 = 0
        L_0x08fb:
            if (r5 == 0) goto L_0x186f
            java.lang.Object r0 = r9.get(r5)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x186f
            java.util.ArrayList<java.lang.String> r2 = r1.countriesArray
            int r2 = r2.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x186f
            org.telegram.ui.Components.EditTextBoldCursor[] r2 = r1.inputFields
            r3 = 8
            r2 = r2[r3]
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r1.countriesMap
            java.lang.Object r0 = r3.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r2.setText(r0)
            goto L_0x186f
        L_0x0921:
            r3 = 2
            if (r0 != r3) goto L_0x0e36
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0958
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0954 }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r1.paymentForm     // Catch:{ Exception -> 0x0954 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r3 = r3.native_params     // Catch:{ Exception -> 0x0954 }
            java.lang.String r3 = r3.data     // Catch:{ Exception -> 0x0954 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x0954 }
            java.lang.String r3 = "google_pay_public_key"
            java.lang.String r3 = r0.optString(r3)     // Catch:{ Exception -> 0x0954 }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0954 }
            if (r4 != 0) goto L_0x0943
            r1.googlePayPublicKey = r3     // Catch:{ Exception -> 0x0954 }
        L_0x0943:
            java.lang.String r3 = "acquirer_bank_country"
            java.lang.String r3 = r0.optString(r3)     // Catch:{ Exception -> 0x0954 }
            r1.googlePayCountryCode = r3     // Catch:{ Exception -> 0x0954 }
            java.lang.String r3 = "gpay_parameters"
            org.json.JSONObject r0 = r0.optJSONObject(r3)     // Catch:{ Exception -> 0x0954 }
            r1.googlePayParameters = r0     // Catch:{ Exception -> 0x0954 }
            goto L_0x0958
        L_0x0954:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0958:
            boolean r0 = r1.isWebView
            if (r0 == 0) goto L_0x0a61
            java.lang.String r0 = r1.googlePayPublicKey
            if (r0 != 0) goto L_0x0964
            org.json.JSONObject r0 = r1.googlePayParameters
            if (r0 == 0) goto L_0x0967
        L_0x0964:
            r34.initGooglePay(r35)
        L_0x0967:
            r34.createGooglePayButton(r35)
            android.widget.LinearLayout r0 = r1.linearLayout2
            android.widget.FrameLayout r3 = r1.googlePayContainer
            r4 = 50
            r5 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r4)
            r1.webviewLoading = r8
            r1.showEditDoneProgress(r8, r8)
            org.telegram.ui.Components.ContextProgressView r0 = r1.progressView
            r0.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            r0.setEnabled(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            android.view.View r0 = r0.getContentView()
            r3 = 4
            r0.setVisibility(r3)
            org.telegram.ui.PaymentFormActivity$5 r0 = new org.telegram.ui.PaymentFormActivity$5
            r0.<init>(r2)
            r1.webView = r0
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setJavaScriptEnabled(r8)
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setDomStorageEnabled(r8)
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r0 < r3) goto L_0x09c0
            android.webkit.WebView r3 = r1.webView
            android.webkit.WebSettings r3 = r3.getSettings()
            r3.setMixedContentMode(r12)
            android.webkit.CookieManager r3 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r4 = r1.webView
            r3.setAcceptThirdPartyCookies(r4, r8)
        L_0x09c0:
            r3 = 17
            if (r0 < r3) goto L_0x09d1
            android.webkit.WebView r0 = r1.webView
            org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy r3 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy
            r4 = 0
            r3.<init>()
            java.lang.String r4 = "TelegramWebviewProxy"
            r0.addJavascriptInterface(r3, r4)
        L_0x09d1:
            android.webkit.WebView r0 = r1.webView
            org.telegram.ui.PaymentFormActivity$6 r3 = new org.telegram.ui.PaymentFormActivity$6
            r3.<init>()
            r0.setWebViewClient(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            android.webkit.WebView r3 = r1.webView
            r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
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
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r6)
            org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
            r0.<init>(r2)
            r1.checkCell1 = r0
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextCheckCell r0 = r1.checkCell1
            r3 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            java.lang.String r4 = "PaymentCardSavePaymentInformation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r1.saveCardInfo
            r0.setTextAndCheck(r3, r4, r12)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r3 = r1.checkCell1
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r6)
            org.telegram.ui.Cells.TextCheckCell r0 = r1.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$cvL2PmrtAz-lhXkk9DISaHjZOls r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$cvL2PmrtAz-lhXkk9DISaHjZOls
            r3.<init>()
            r0.setOnClickListener(r3)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r2)
            r0[r12] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r0 = r0[r12]
            r3 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r10)
            r0.setBackgroundDrawable(r2)
            r34.updateSavePaymentField()
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r1.bottomCell
            r2 = r2[r12]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
            goto L_0x186f
        L_0x0a61:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_dataJSON r0 = r0.native_params
            if (r0 == 0) goto L_0x0ab2
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0aae }
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r1.paymentForm     // Catch:{ Exception -> 0x0aae }
            org.telegram.tgnet.TLRPC$TL_dataJSON r3 = r3.native_params     // Catch:{ Exception -> 0x0aae }
            java.lang.String r3 = r3.data     // Catch:{ Exception -> 0x0aae }
            r0.<init>(r3)     // Catch:{ Exception -> 0x0aae }
            java.lang.String r3 = "need_country"
            boolean r3 = r0.getBoolean(r3)     // Catch:{ Exception -> 0x0a7b }
            r1.need_card_country = r3     // Catch:{ Exception -> 0x0a7b }
            goto L_0x0a7d
        L_0x0a7b:
            r1.need_card_country = r12     // Catch:{ Exception -> 0x0aae }
        L_0x0a7d:
            java.lang.String r3 = "need_zip"
            boolean r3 = r0.getBoolean(r3)     // Catch:{ Exception -> 0x0a86 }
            r1.need_card_postcode = r3     // Catch:{ Exception -> 0x0a86 }
            goto L_0x0a88
        L_0x0a86:
            r1.need_card_postcode = r12     // Catch:{ Exception -> 0x0aae }
        L_0x0a88:
            java.lang.String r3 = "need_cardholder_name"
            boolean r3 = r0.getBoolean(r3)     // Catch:{ Exception -> 0x0a91 }
            r1.need_card_name = r3     // Catch:{ Exception -> 0x0a91 }
            goto L_0x0a93
        L_0x0a91:
            r1.need_card_name = r12     // Catch:{ Exception -> 0x0aae }
        L_0x0a93:
            java.lang.String r3 = "publishable_key"
            java.lang.String r3 = r0.getString(r3)     // Catch:{ Exception -> 0x0a9c }
            r1.stripeApiKey = r3     // Catch:{ Exception -> 0x0a9c }
            goto L_0x0aa0
        L_0x0a9c:
            java.lang.String r3 = ""
            r1.stripeApiKey = r3     // Catch:{ Exception -> 0x0aae }
        L_0x0aa0:
            java.lang.String r3 = "google_pay_hidden"
            boolean r0 = r0.optBoolean(r3, r12)     // Catch:{ Exception -> 0x0aae }
            if (r0 != 0) goto L_0x0aaa
            r0 = 1
            goto L_0x0aab
        L_0x0aaa:
            r0 = 0
        L_0x0aab:
            r1.initGooglePay = r0     // Catch:{ Exception -> 0x0aae }
            goto L_0x0ab2
        L_0x0aae:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0ab2:
            boolean r0 = r1.initGooglePay
            if (r0 == 0) goto L_0x0ac5
            java.lang.String r0 = r1.stripeApiKey
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0ac2
            org.json.JSONObject r0 = r1.googlePayParameters
            if (r0 == 0) goto L_0x0ac5
        L_0x0ac2:
            r34.initGooglePay(r35)
        L_0x0ac5:
            r3 = 6
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x0acb:
            if (r0 >= r3) goto L_0x0e01
            if (r0 != 0) goto L_0x0b03
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r12] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            r4 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.String r7 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r12]
            r7 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r3.addView(r4, r11)
            goto L_0x0b39
        L_0x0b03:
            r3 = 4
            if (r0 != r3) goto L_0x0b39
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            r4 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            java.lang.String r7 = "PaymentBillingAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r8]
            r7 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r3.addView(r4, r11)
        L_0x0b39:
            if (r0 == r6) goto L_0x0b47
            r3 = 5
            if (r0 == r3) goto L_0x0b47
            r3 = 4
            if (r0 != r3) goto L_0x0b45
            boolean r3 = r1.need_card_postcode
            if (r3 == 0) goto L_0x0b47
        L_0x0b45:
            r3 = 1
            goto L_0x0b48
        L_0x0b47:
            r3 = 0
        L_0x0b48:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r2)
            android.widget.LinearLayout r7 = r1.linearLayout2
            r9 = 50
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r7.addView(r4, r13)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r2)
            r7[r0] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r7.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 1098907648(0x41800000, float:16.0)
            r7.setTextSize(r8, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r7.setTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 0
            r7.setBackgroundDrawable(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r7.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r7.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r7.setCursorWidth(r9)
            if (r0 != r6) goto L_0x0bef
            android.text.InputFilter[] r7 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r9 = new android.text.InputFilter$LengthFilter
            r9.<init>(r6)
            r7[r12] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r9.setFilters(r7)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 130(0x82, float:1.82E-43)
            r7.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
            r7.setTypeface(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            android.text.method.PasswordTransformationMethod r9 = android.text.method.PasswordTransformationMethod.getInstance()
            r7.setTransformationMethod(r9)
            goto L_0x0CLASSNAME
        L_0x0bef:
            if (r0 != 0) goto L_0x0bf9
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setInputType(r6)
            goto L_0x0CLASSNAME
        L_0x0bf9:
            r7 = 4
            if (r0 != r7) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$ufBXhrNbvJlVLqcHkReQdB857HQ r9 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$ufBXhrNbvJlVLqcHkReQdB857HQ
            r9.<init>()
            r7.setOnTouchListener(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r7.setInputType(r12)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            if (r0 != r8) goto L_0x0c1c
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 16386(0x4002, float:2.2962E-41)
            r7.setInputType(r9)
            goto L_0x0CLASSNAME
        L_0x0c1c:
            r7 = 2
            if (r0 != r7) goto L_0x0CLASSNAME
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 4097(0x1001, float:5.741E-42)
            r7.setInputType(r9)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 16385(0x4001, float:2.296E-41)
            r7.setInputType(r9)
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 268435461(0x10000005, float:2.5243564E-29)
            r7.setImeOptions(r9)
            if (r0 == 0) goto L_0x0ca1
            if (r0 == r8) goto L_0x0CLASSNAME
            r7 = 2
            if (r0 == r7) goto L_0x0c7f
            if (r0 == r6) goto L_0x0c6e
            r7 = 4
            if (r0 == r7) goto L_0x0c5d
            r7 = 5
            if (r0 == r7) goto L_0x0c4c
            goto L_0x0cb1
        L_0x0c4c:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            java.lang.String r11 = "PaymentShippingZipPlaceholder"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r7.setHint(r9)
            goto L_0x0cb1
        L_0x0c5d:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 2131626624(0x7f0e0a80, float:1.888049E38)
            java.lang.String r11 = "PaymentShippingCountry"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r7.setHint(r9)
            goto L_0x0cb1
        L_0x0c6e:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            java.lang.String r11 = "PaymentCardCvv"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r7.setHint(r9)
            goto L_0x0cb1
        L_0x0c7f:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            java.lang.String r11 = "PaymentCardName"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r7.setHint(r9)
            goto L_0x0cb1
        L_0x0CLASSNAME:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 2131626585(0x7f0e0a59, float:1.888041E38)
            java.lang.String r11 = "PaymentCardExpireDate"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r7.setHint(r9)
            goto L_0x0cb1
        L_0x0ca1:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 2131626588(0x7f0e0a5c, float:1.8880416E38)
            java.lang.String r11 = "PaymentCardNumber"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r7.setHint(r9)
        L_0x0cb1:
            if (r0 != 0) goto L_0x0cc0
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            org.telegram.ui.PaymentFormActivity$7 r9 = new org.telegram.ui.PaymentFormActivity$7
            r9.<init>()
            r7.addTextChangedListener(r9)
            goto L_0x0cce
        L_0x0cc0:
            if (r0 != r8) goto L_0x0cce
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            org.telegram.ui.PaymentFormActivity$8 r9 = new org.telegram.ui.PaymentFormActivity$8
            r9.<init>()
            r7.addTextChangedListener(r9)
        L_0x0cce:
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r7.setPadding(r12, r12, r12, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0ce5
            r9 = 5
            goto L_0x0ce6
        L_0x0ce5:
            r9 = 3
        L_0x0ce6:
            r7.setGravity(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r4.addView(r7, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r7 = r1.inputFields
            r7 = r7[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$xR8Gbm-NkgnmgJUOUHnvYiFZMuY r9 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$xR8Gbm-NkgnmgJUOUHnvYiFZMuY
            r9.<init>()
            r7.setOnEditorActionListener(r9)
            if (r0 != r6) goto L_0x0d2a
            org.telegram.ui.Cells.ShadowSectionCell[] r7 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
            r9.<init>(r2)
            r7[r12] = r9
            android.widget.LinearLayout r7 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r9 = r1.sectionCell
            r9 = r9[r12]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r7.addView(r9, r14)
            goto L_0x0dc1
        L_0x0d2a:
            r7 = 5
            r11 = -2
            r13 = -1
            if (r0 != r7) goto L_0x0da5
            org.telegram.ui.Cells.ShadowSectionCell[] r7 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
            r9.<init>(r2)
            r14 = 2
            r7[r14] = r9
            android.widget.LinearLayout r7 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r9 = r1.sectionCell
            r9 = r9[r14]
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r7.addView(r9, r14)
            org.telegram.ui.Cells.TextCheckCell r7 = new org.telegram.ui.Cells.TextCheckCell
            r7.<init>(r2)
            r1.checkCell1 = r7
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r7.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextCheckCell r7 = r1.checkCell1
            r9 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            java.lang.String r11 = "PaymentCardSavePaymentInformation"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            boolean r11 = r1.saveCardInfo
            r7.setTextAndCheck(r9, r11, r12)
            android.widget.LinearLayout r7 = r1.linearLayout2
            org.telegram.ui.Cells.TextCheckCell r9 = r1.checkCell1
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r7.addView(r9, r14)
            org.telegram.ui.Cells.TextCheckCell r7 = r1.checkCell1
            org.telegram.ui.-$$Lambda$PaymentFormActivity$tYBCHRD5qJE9gLUKdrPoOZY19ls r9 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$tYBCHRD5qJE9gLUKdrPoOZY19ls
            r9.<init>()
            r7.setOnClickListener(r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r9.<init>(r2)
            r7[r12] = r9
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r7 = r1.bottomCell
            r7 = r7[r12]
            r9 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r9, (java.lang.String) r10)
            r7.setBackgroundDrawable(r11)
            r34.updateSavePaymentField()
            android.widget.LinearLayout r7 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r9 = r1.bottomCell
            r9 = r9[r12]
            r11 = -2
            r13 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r11)
            r7.addView(r9, r14)
            goto L_0x0dc1
        L_0x0da5:
            if (r0 != 0) goto L_0x0dc1
            r34.createGooglePayButton(r35)
            android.widget.FrameLayout r7 = r1.googlePayContainer
            r27 = -2
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 21
            r30 = 0
            r31 = 0
            r32 = 1082130432(0x40800000, float:4.0)
            r33 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r4.addView(r7, r9)
        L_0x0dc1:
            if (r3 == 0) goto L_0x0ddf
            org.telegram.ui.PaymentFormActivity$9 r3 = new org.telegram.ui.PaymentFormActivity$9
            r3.<init>(r1, r2)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r7)
            java.util.ArrayList<android.view.View> r7 = r1.dividers
            r7.add(r3)
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r11 = -1
            r7.<init>(r11, r8, r9)
            r4.addView(r3, r7)
        L_0x0ddf:
            r3 = 4
            if (r0 != r3) goto L_0x0dea
            boolean r3 = r1.need_card_country
            if (r3 == 0) goto L_0x0de7
            goto L_0x0dea
        L_0x0de7:
            r3 = 8
            goto L_0x0df9
        L_0x0dea:
            r3 = 5
            if (r0 != r3) goto L_0x0df1
            boolean r3 = r1.need_card_postcode
            if (r3 == 0) goto L_0x0de7
        L_0x0df1:
            r3 = 2
            if (r0 != r3) goto L_0x0dfc
            boolean r3 = r1.need_card_name
            if (r3 != 0) goto L_0x0dfc
            goto L_0x0de7
        L_0x0df9:
            r4.setVisibility(r3)
        L_0x0dfc:
            int r0 = r0 + 1
            r3 = 6
            goto L_0x0acb
        L_0x0e01:
            boolean r0 = r1.need_card_country
            if (r0 != 0) goto L_0x0e19
            boolean r0 = r1.need_card_postcode
            if (r0 != 0) goto L_0x0e19
            org.telegram.ui.Cells.HeaderCell[] r0 = r1.headerCell
            r0 = r0[r8]
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r0 = r0[r12]
            r0.setVisibility(r2)
        L_0x0e19:
            boolean r0 = r1.need_card_postcode
            if (r0 == 0) goto L_0x0e2a
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r2 = 5
            r0 = r0[r2]
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            r0.setImeOptions(r2)
            goto L_0x186f
        L_0x0e2a:
            r2 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = r1.inputFields
            r0 = r0[r6]
            r0.setImeOptions(r2)
            goto L_0x186f
        L_0x0e36:
            if (r0 != r8) goto L_0x0ed8
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r0 = r1.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r0 = r0.shipping_options
            int r0 = r0.size()
            org.telegram.ui.Cells.RadioCell[] r3 = new org.telegram.ui.Cells.RadioCell[r0]
            r1.radioCells = r3
            r3 = 0
        L_0x0e45:
            if (r3 >= r0) goto L_0x0eb0
            org.telegram.tgnet.TLRPC$TL_payments_validatedRequestedInfo r4 = r1.requestedInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_shippingOption> r4 = r4.shipping_options
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_shippingOption r4 = (org.telegram.tgnet.TLRPC$TL_shippingOption) r4
            org.telegram.ui.Cells.RadioCell[] r5 = r1.radioCells
            org.telegram.ui.Cells.RadioCell r6 = new org.telegram.ui.Cells.RadioCell
            r6.<init>(r2)
            r5[r3] = r6
            org.telegram.ui.Cells.RadioCell[] r5 = r1.radioCells
            r5 = r5[r3]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            r5.setTag(r6)
            org.telegram.ui.Cells.RadioCell[] r5 = r1.radioCells
            r5 = r5[r3]
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r5.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.RadioCell[] r5 = r1.radioCells
            r5 = r5[r3]
            r6 = 2
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r6 = r4.prices
            java.lang.String r6 = r1.getTotalPriceString(r6)
            r7[r12] = r6
            java.lang.String r4 = r4.title
            r7[r8] = r4
            java.lang.String r4 = "%s - %s"
            java.lang.String r4 = java.lang.String.format(r4, r7)
            if (r3 != 0) goto L_0x0e8d
            r6 = 1
            goto L_0x0e8e
        L_0x0e8d:
            r6 = 0
        L_0x0e8e:
            int r7 = r0 + -1
            if (r3 == r7) goto L_0x0e94
            r7 = 1
            goto L_0x0e95
        L_0x0e94:
            r7 = 0
        L_0x0e95:
            r5.setText(r4, r6, r7)
            org.telegram.ui.Cells.RadioCell[] r4 = r1.radioCells
            r4 = r4[r3]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$XzomBkzVnAnjaV6SN-Js2LQ2of8 r5 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$XzomBkzVnAnjaV6SN-Js2LQ2of8
            r5.<init>()
            r4.setOnClickListener(r5)
            android.widget.LinearLayout r4 = r1.linearLayout2
            org.telegram.ui.Cells.RadioCell[] r5 = r1.radioCells
            r5 = r5[r3]
            r4.addView(r5)
            int r3 = r3 + 1
            goto L_0x0e45
        L_0x0eb0:
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r2)
            r0[r12] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r0 = r0[r12]
            r3 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r10)
            r0.setBackgroundDrawable(r2)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r2 = r1.bottomCell
            r2 = r2[r12]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
            goto L_0x186f
        L_0x0ed8:
            if (r0 != r6) goto L_0x1116
            r3 = 2
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x0ee0:
            if (r0 >= r3) goto L_0x186f
            if (r0 != 0) goto L_0x0var_
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r12] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            r4 = 2131626592(0x7f0e0a60, float:1.8880425E38)
            java.lang.String r7 = "PaymentCardTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r12]
            r7 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r3.addView(r4, r11)
            goto L_0x0var_
        L_0x0var_:
            r9 = -1
        L_0x0var_:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r7 = 50
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r4.addView(r3, r11)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            if (r0 == r8) goto L_0x0var_
            r4 = 1
            goto L_0x0var_
        L_0x0var_:
            r4 = 0
        L_0x0var_:
            r7 = 7
            if (r4 == 0) goto L_0x0var_
            if (r0 != r7) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r9 = r9.invoice
            boolean r9 = r9.phone_requested
            if (r9 != 0) goto L_0x0var_
        L_0x0var_:
            r4 = 0
            goto L_0x0var_
        L_0x0var_:
            r9 = 6
            if (r0 != r9) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r9 = r9.invoice
            boolean r11 = r9.phone_requested
            if (r11 != 0) goto L_0x0var_
            boolean r9 = r9.email_requested
            if (r9 != 0) goto L_0x0var_
            goto L_0x0var_
        L_0x0var_:
            if (r4 == 0) goto L_0x0var_
            org.telegram.ui.PaymentFormActivity$10 r4 = new org.telegram.ui.PaymentFormActivity$10
            r4.<init>(r1, r2)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r9)
            java.util.ArrayList<android.view.View> r9 = r1.dividers
            r9.add(r4)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r11 = 83
            r13 = -1
            r9.<init>(r13, r8, r11)
            r3.addView(r4, r9)
        L_0x0var_:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r2)
            r4[r0] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r4.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r8, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 0
            r4.setBackgroundDrawable(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r9)
            if (r0 != 0) goto L_0x0fe4
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$YMeFnG1zi0SfhsCCk86PBQIPZKI r9 = org.telegram.ui.$$Lambda$PaymentFormActivity$YMeFnG1zi0SfhsCCk86PBQIPZKI.INSTANCE
            r4.setOnTouchListener(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.setInputType(r12)
            goto L_0x0ff6
        L_0x0fe4:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 129(0x81, float:1.81E-43)
            r4.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r9 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r9)
        L_0x0ff6:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r9)
            if (r0 == 0) goto L_0x101d
            if (r0 == r8) goto L_0x1005
            goto L_0x102a
        L_0x1005:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 2131625826(0x7f0e0762, float:1.887887E38)
            java.lang.String r11 = "LoginPassword"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r4.setHint(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.requestFocus()
            goto L_0x102a
        L_0x101d:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r9 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r9 = r9.saved_credentials
            java.lang.String r9 = r9.title
            r4.setText(r9)
        L_0x102a:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setPadding(r12, r12, r12, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x1041
            r9 = 5
            goto L_0x1042
        L_0x1041:
            r9 = 3
        L_0x1042:
            r4.setGravity(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$e9RqJBJ1jQ4PDryfM6w6pSv02Wk r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$e9RqJBJ1jQ4PDryfM6w6pSv02Wk
            r4.<init>()
            r3.setOnEditorActionListener(r4)
            if (r0 != r8) goto L_0x1111
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r12] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r12]
            r4 = 2131626601(0x7f0e0a69, float:1.8880443E38)
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r11 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_paymentSavedCredentialsCard r11 = r11.saved_credentials
            java.lang.String r11 = r11.title
            r9[r12] = r11
            java.lang.String r11 = "PaymentConfirmationMessage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r4, r9)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r12]
            r4 = 2131165446(0x7var_, float:1.794511E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r10)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r12]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r3.addView(r4, r13)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r4 = new org.telegram.ui.Cells.TextSettingsCell
            r4.<init>(r2)
            r3[r12] = r4
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r12]
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r3.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r12]
            r4 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            java.lang.String r9 = "PaymentConfirmationNewCard"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r3.setText(r4, r12)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r4 = r1.settingsCell
            r4 = r4[r12]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r3.addView(r4, r13)
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r12]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$8oiBrYVovzVK-mug2vbZpERQ3mo r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$8oiBrYVovzVK-mug2vbZpERQ3mo
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r8]
            r4 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r10)
            r3.setBackgroundDrawable(r9)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r9 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r9)
            r3.addView(r4, r13)
        L_0x1111:
            int r0 = r0 + 1
            r3 = 2
            goto L_0x0ee0
        L_0x1116:
            r3 = 4
            if (r0 == r3) goto L_0x1475
            r3 = 5
            if (r0 != r3) goto L_0x111e
            goto L_0x1475
        L_0x111e:
            r3 = 6
            if (r0 != r3) goto L_0x186f
            org.telegram.ui.Cells.EditTextSettingsCell r0 = new org.telegram.ui.Cells.EditTextSettingsCell
            r0.<init>(r2)
            r1.codeFieldCell = r0
            r3 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            java.lang.String r4 = "PasswordCode"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = ""
            r0.setTextAndHint(r4, r3, r12)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r1.codeFieldCell
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Cells.EditTextSettingsCell r0 = r1.codeFieldCell
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getTextView()
            r0.setInputType(r6)
            r3 = 6
            r0.setImeOptions(r3)
            org.telegram.ui.-$$Lambda$PaymentFormActivity$LQCYdVcxE1Ytnfv6sifywPW4CZ4 r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$LQCYdVcxE1Ytnfv6sifywPW4CZ4
            r3.<init>()
            r0.setOnEditorActionListener(r3)
            org.telegram.ui.PaymentFormActivity$15 r3 = new org.telegram.ui.PaymentFormActivity$15
            r3.<init>()
            r0.addTextChangedListener(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.EditTextSettingsCell r3 = r1.codeFieldCell
            r4 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r4)
            r0.addView(r3, r9)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r2)
            r4 = 2
            r0[r4] = r3
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r0 = r1.bottomCell
            r0 = r0[r4]
            r3 = 2131165446(0x7var_, float:1.794511E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r10)
            r0.setBackgroundDrawable(r3)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r4]
            r4 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r4)
            r0.addView(r3, r9)
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
            r0.setTag(r5)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setTextColor(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            r3 = 2131626936(0x7f0e0bb8, float:1.8881122E38)
            java.lang.String r4 = "ResendCode"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3, r8)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r8]
            r4 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r4)
            r0.addView(r3, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r8]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$aXAUqIs8LZgIjzhMbkZqaWXHupQ r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$aXAUqIs8LZgIjzhMbkZqaWXHupQ
            r3.<init>()
            r0.setOnClickListener(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            org.telegram.ui.Cells.TextSettingsCell r3 = new org.telegram.ui.Cells.TextSettingsCell
            r3.<init>(r2)
            r0[r12] = r3
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r12]
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r12]
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            r0.setTag(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r12]
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r12]
            r3 = 2131623939(0x7f0e0003, float:1.8875044E38)
            java.lang.String r4 = "AbortPassword"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setText(r3, r12)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell[] r3 = r1.settingsCell
            r3 = r3[r12]
            r4 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r4)
            r0.addView(r3, r9)
            org.telegram.ui.Cells.TextSettingsCell[] r0 = r1.settingsCell
            r0 = r0[r12]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$bJHWj5XWO49_bvTcvyTQyZ9HIbw r3 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$bJHWj5XWO49_bvTcvyTQyZ9HIbw
            r3.<init>()
            r0.setOnClickListener(r3)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r6]
            r1.inputFields = r0
            r0 = 0
        L_0x123d:
            if (r0 >= r6) goto L_0x1470
            if (r0 != 0) goto L_0x1275
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r12] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r12]
            r4 = 2131626615(0x7f0e0a77, float:1.8880471E38)
            java.lang.String r7 = "PaymentPasswordTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r12]
            r7 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r3.addView(r4, r11)
            goto L_0x12ac
        L_0x1275:
            r3 = 2
            if (r0 != r3) goto L_0x12ac
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            org.telegram.ui.Cells.HeaderCell[] r3 = r1.headerCell
            r3 = r3[r8]
            r4 = 2131626611(0x7f0e0a73, float:1.8880463E38)
            java.lang.String r7 = "PaymentPasswordEmailTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell[] r4 = r1.headerCell
            r4 = r4[r8]
            r7 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r3.addView(r4, r11)
            goto L_0x12ad
        L_0x12ac:
            r9 = -1
        L_0x12ad:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r7 = 50
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r7)
            r4.addView(r3, r11)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r3.setBackgroundColor(r4)
            if (r0 != 0) goto L_0x12e2
            org.telegram.ui.PaymentFormActivity$16 r4 = new org.telegram.ui.PaymentFormActivity$16
            r4.<init>(r1, r2)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r4.setBackgroundColor(r9)
            java.util.ArrayList<android.view.View> r9 = r1.dividers
            r9.add(r4)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r11 = 83
            r13 = -1
            r9.<init>(r13, r8, r11)
            r3.addView(r4, r9)
        L_0x12e2:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r9 = new org.telegram.ui.Components.EditTextBoldCursor
            r9.<init>(r2)
            r4[r0] = r9
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r4.setTag(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r9 = 1098907648(0x41800000, float:16.0)
            r4.setTextSize(r8, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            java.lang.String r11 = "windowBackgroundWhiteHintText"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setHintTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r11 = 0
            r4.setBackgroundDrawable(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setCursorColor(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r13 = 1101004800(0x41a00000, float:20.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r4.setCursorSize(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r13 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r13)
            if (r0 == 0) goto L_0x135a
            if (r0 != r8) goto L_0x1346
            goto L_0x135a
        L_0x1346:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r13 = 33
            r4.setInputType(r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r13 = 268435462(0x10000006, float:2.5243567E-29)
            r4.setImeOptions(r13)
            goto L_0x1379
        L_0x135a:
            r13 = 268435462(0x10000006, float:2.5243567E-29)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r14 = 129(0x81, float:1.81E-43)
            r4.setInputType(r14)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            android.graphics.Typeface r14 = android.graphics.Typeface.DEFAULT
            r4.setTypeface(r14)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r14 = 268435461(0x10000005, float:2.5243564E-29)
            r4.setImeOptions(r14)
        L_0x1379:
            if (r0 == 0) goto L_0x13a3
            if (r0 == r8) goto L_0x1392
            r4 = 2
            if (r0 == r4) goto L_0x1381
            goto L_0x13ba
        L_0x1381:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r14 = 2131626609(0x7f0e0a71, float:1.888046E38)
            java.lang.String r15 = "PaymentPasswordEmail"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r4.setHint(r14)
            goto L_0x13ba
        L_0x1392:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r14 = 2131626614(0x7f0e0a76, float:1.888047E38)
            java.lang.String r15 = "PaymentPasswordReEnter"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r4.setHint(r14)
            goto L_0x13ba
        L_0x13a3:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r14 = 2131626612(0x7f0e0a74, float:1.8880465E38)
            java.lang.String r15 = "PaymentPasswordEnter"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r4.setHint(r14)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r4.requestFocus()
        L_0x13ba:
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r4.setPadding(r12, r12, r12, r14)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x13d1
            r14 = 5
            goto L_0x13d2
        L_0x13d1:
            r14 = 3
        L_0x13d2:
            r4.setGravity(r14)
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r1.inputFields
            r4 = r4[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 51
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1094713344(0x41400000, float:12.0)
            r32 = 1101529088(0x41a80000, float:21.0)
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r3.addView(r4, r14)
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$BoYuyJ_EGxzIvZyIClgSYk_2q9Q r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$BoYuyJ_EGxzIvZyIClgSYk_2q9Q
            r4.<init>()
            r3.setOnEditorActionListener(r4)
            if (r0 != r8) goto L_0x1433
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r12] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r12]
            r4 = 2131626613(0x7f0e0a75, float:1.8880467E38)
            java.lang.String r14 = "PaymentPasswordInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r14, r4)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r12]
            r4 = 2131165446(0x7var_, float:1.794511E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r10)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r12]
            r14 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14)
            r3.addView(r4, r7)
            goto L_0x146c
        L_0x1433:
            r3 = 2
            if (r0 != r3) goto L_0x146c
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r4.<init>(r2)
            r3[r8] = r4
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r8]
            r4 = 2131626610(0x7f0e0a72, float:1.8880461E38)
            java.lang.String r7 = "PaymentPasswordEmailInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r3.setText(r4)
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r3 = r1.bottomCell
            r3 = r3[r8]
            r4 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r10)
            r3.setBackgroundDrawable(r7)
            android.widget.LinearLayout r3 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell[] r4 = r1.bottomCell
            r4 = r4[r8]
            r7 = -2
            r14 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r7)
            r3.addView(r4, r15)
        L_0x146c:
            int r0 = r0 + 1
            goto L_0x123d
        L_0x1470:
            r34.updatePasswordFields()
            goto L_0x186f
        L_0x1475:
            r11 = 0
            org.telegram.ui.Cells.PaymentInfoCell r0 = new org.telegram.ui.Cells.PaymentInfoCell
            r0.<init>(r2)
            r1.paymentInfoCell = r0
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r3)
            org.telegram.ui.Cells.PaymentInfoCell r0 = r1.paymentInfoCell
            org.telegram.messenger.MessageObject r3 = r1.messageObject
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) r3
            java.lang.String r4 = r1.currentBotName
            r0.setInvoice(r3, r4)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.PaymentInfoCell r3 = r1.paymentInfoCell
            r4 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r2)
            r0[r12] = r3
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r1.sectionCell
            r3 = r3[r12]
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r4)
            r0.addView(r3, r7)
            java.util.ArrayList r0 = new java.util.ArrayList
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r3 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r3 = r3.invoice
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r3.prices
            r0.<init>(r3)
            org.telegram.tgnet.TLRPC$TL_shippingOption r3 = r1.shippingOption
            if (r3 == 0) goto L_0x14ca
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_labeledPrice> r3 = r3.prices
            r0.addAll(r3)
        L_0x14ca:
            java.lang.String r3 = r1.getTotalPriceString(r0)
            r4 = 0
        L_0x14cf:
            int r5 = r0.size()
            if (r4 >= r5) goto L_0x1509
            java.lang.Object r5 = r0.get(r4)
            org.telegram.tgnet.TLRPC$TL_labeledPrice r5 = (org.telegram.tgnet.TLRPC$TL_labeledPrice) r5
            org.telegram.ui.Cells.TextPriceCell r7 = new org.telegram.ui.Cells.TextPriceCell
            r7.<init>(r2)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r7.setBackgroundColor(r13)
            java.lang.String r13 = r5.label
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
            r17 = r9
            long r8 = r5.amount
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r5 = r1.paymentForm
            org.telegram.tgnet.TLRPC$TL_invoice r5 = r5.invoice
            java.lang.String r5 = r5.currency
            java.lang.String r5 = r14.formatCurrencyString(r8, r5)
            r7.setTextAndValue(r13, r5, r12)
            android.widget.LinearLayout r5 = r1.linearLayout2
            r5.addView(r7)
            int r4 = r4 + 1
            r9 = r17
            r8 = 1
            goto L_0x14cf
        L_0x1509:
            r17 = r9
            org.telegram.ui.Cells.TextPriceCell r0 = new org.telegram.ui.Cells.TextPriceCell
            r0.<init>(r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r4)
            r4 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            java.lang.String r5 = "PaymentTransactionTotal"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 1
            r0.setTextAndValue(r4, r3, r5)
            android.widget.LinearLayout r4 = r1.linearLayout2
            r4.addView(r0)
            org.telegram.ui.PaymentFormActivity$11 r0 = new org.telegram.ui.PaymentFormActivity$11
            r0.<init>(r1, r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r4)
            java.util.ArrayList<android.view.View> r4 = r1.dividers
            r4.add(r0)
            android.widget.LinearLayout r4 = r1.linearLayout2
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            r8 = 83
            r9 = -1
            r7.<init>(r9, r5, r8)
            r4.addView(r0, r7)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r4 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r4.<init>(r2)
            r0[r12] = r4
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r12]
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r0.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r12]
            java.lang.String r4 = r1.cardName
            r7 = 2131626595(0x7f0e0a63, float:1.888043E38)
            java.lang.String r8 = "PaymentCheckoutMethod"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setTextAndValue(r4, r7, r5)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r4 = r1.detailSettingsCell
            r4 = r4[r12]
            r0.addView(r4)
            int r0 = r1.currentStep
            r4 = 4
            if (r0 != r4) goto L_0x1587
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r12]
            org.telegram.ui.-$$Lambda$PaymentFormActivity$VLYtB2G85ebWkqvar_i88M_wSyRU r4 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$VLYtB2G85ebWkqvar_i88M_wSyRU
            r4.<init>()
            r0.setOnClickListener(r4)
        L_0x1587:
            r5 = r11
            r0 = 0
        L_0x1589:
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.users
            int r4 = r4.size()
            if (r0 >= r4) goto L_0x15a9
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r4 = r1.paymentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.users
            java.lang.Object r4 = r4.get(r0)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            int r7 = r4.id
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r8 = r1.paymentForm
            int r8 = r8.provider_id
            if (r7 != r8) goto L_0x15a6
            r5 = r4
        L_0x15a6:
            int r0 = r0 + 1
            goto L_0x1589
        L_0x15a9:
            if (r5 == 0) goto L_0x15e2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r4 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r4.<init>(r2)
            r7 = 1
            r0[r7] = r4
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r7)
            r0.setBackgroundDrawable(r4)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r7]
            java.lang.String r4 = r5.first_name
            java.lang.String r5 = r5.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r5)
            r5 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            java.lang.String r8 = "PaymentCheckoutProvider"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r0.setTextAndValue(r4, r5, r7)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r7]
            r0.addView(r5)
            goto L_0x15e4
        L_0x15e2:
            java.lang.String r4 = ""
        L_0x15e4:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            if (r0 == 0) goto L_0x1737
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            org.telegram.tgnet.TLRPC$TL_postAddress r0 = r0.shipping_address
            if (r0 == 0) goto L_0x1642
            r5 = 6
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r5 = r0.street_line1
            r7[r12] = r5
            java.lang.String r5 = r0.street_line2
            r8 = 1
            r7[r8] = r5
            java.lang.String r5 = r0.city
            r8 = 2
            r7[r8] = r5
            java.lang.String r5 = r0.state
            r7[r6] = r5
            java.lang.String r5 = r0.country_iso2
            r8 = 4
            r7[r8] = r5
            java.lang.String r0 = r0.post_code
            r5 = 5
            r7[r5] = r0
            java.lang.String r0 = "%s %s, %s, %s, %s, %s"
            java.lang.String r0 = java.lang.String.format(r0, r7)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r7.<init>(r2)
            r8 = 2
            r5[r8] = r7
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r8]
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r5.setBackgroundColor(r7)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r8]
            r7 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            java.lang.String r9 = "PaymentShippingAddress"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r9 = 1
            r5.setTextAndValue(r0, r7, r9)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r8]
            r0.addView(r5)
        L_0x1642:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.name
            if (r0 == 0) goto L_0x167e
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r2)
            r0[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r5 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.info
            java.lang.String r5 = r5.name
            r7 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            java.lang.String r8 = "PaymentCheckoutName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r8 = 1
            r0.setTextAndValue(r5, r7, r8)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r6]
            r0.addView(r5)
        L_0x167e:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.phone
            if (r0 == 0) goto L_0x16c4
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r2)
            r6 = 4
            r0[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r6 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r6 = r6.info
            java.lang.String r6 = r6.phone
            java.lang.String r5 = r5.format(r6)
            r6 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            java.lang.String r7 = "PaymentCheckoutPhoneNumber"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 1
            r0.setTextAndValue(r5, r6, r7)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r6 = 4
            r5 = r5[r6]
            r0.addView(r5)
        L_0x16c4:
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r0 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r0 = r0.info
            java.lang.String r0 = r0.email
            if (r0 == 0) goto L_0x1701
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r2)
            r6 = 5
            r0[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo r5 = r1.validateRequest
            org.telegram.tgnet.TLRPC$TL_paymentRequestedInfo r5 = r5.info
            java.lang.String r5 = r5.email
            r7 = 2131626594(0x7f0e0a62, float:1.8880429E38)
            java.lang.String r8 = "PaymentCheckoutEmail"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r8 = 1
            r0.setTextAndValue(r5, r7, r8)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r6]
            r0.addView(r5)
        L_0x1701:
            org.telegram.tgnet.TLRPC$TL_shippingOption r0 = r1.shippingOption
            if (r0 == 0) goto L_0x1737
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            org.telegram.ui.Cells.TextDetailSettingsCell r5 = new org.telegram.ui.Cells.TextDetailSettingsCell
            r5.<init>(r2)
            r6 = 6
            r0[r6] = r5
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r5)
            org.telegram.ui.Cells.TextDetailSettingsCell[] r0 = r1.detailSettingsCell
            r0 = r0[r6]
            org.telegram.tgnet.TLRPC$TL_shippingOption r5 = r1.shippingOption
            java.lang.String r5 = r5.title
            r7 = 2131626600(0x7f0e0a68, float:1.888044E38)
            java.lang.String r8 = "PaymentCheckoutShippingMethod"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setTextAndValue(r5, r7, r12)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextDetailSettingsCell[] r5 = r1.detailSettingsCell
            r5 = r5[r6]
            r0.addView(r5)
        L_0x1737:
            int r0 = r1.currentStep
            r5 = 4
            if (r0 != r5) goto L_0x1848
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r2)
            r1.bottomLayout = r0
            r5 = 1
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r0.setBackgroundDrawable(r6)
            android.widget.FrameLayout r0 = r1.bottomLayout
            r5 = 48
            r6 = 80
            r7 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r5, r6)
            r6 = r17
            r6.addView(r0, r5)
            android.widget.FrameLayout r0 = r1.bottomLayout
            org.telegram.ui.-$$Lambda$PaymentFormActivity$Et7eTNMFl76HF0lZwG_wlSGZMok r5 = new org.telegram.ui.-$$Lambda$PaymentFormActivity$Et7eTNMFl76HF0lZwG_wlSGZMok
            r5.<init>(r4, r3)
            r0.setOnClickListener(r5)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r2)
            r1.payTextView = r0
            java.lang.String r4 = "windowBackgroundWhiteBlueText6"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setTextColor(r4)
            android.widget.TextView r0 = r1.payTextView
            r4 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            r5 = 1
            java.lang.Object[] r7 = new java.lang.Object[r5]
            r7[r12] = r3
            java.lang.String r3 = "PaymentCheckoutPay"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r7)
            r0.setText(r3)
            android.widget.TextView r0 = r1.payTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r5, r3)
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
            r5 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r0.addView(r3, r7)
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r0.<init>(r2, r12)
            r1.progressViewButton = r0
            r3 = 4
            r0.setVisibility(r3)
            android.widget.FrameLayout r0 = r1.bottomLayout
            org.telegram.ui.Components.ContextProgressView r3 = r1.progressViewButton
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r0.addView(r3, r7)
            android.view.View r0 = new android.view.View
            r0.<init>(r2)
            r3 = 2131165464(0x7var_, float:1.7945146E38)
            r0.setBackgroundResource(r3)
            r20 = -1
            r21 = 1077936128(0x40400000, float:3.0)
            r22 = 83
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r6.addView(r0, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            r0.setEnabled(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.doneItem
            android.view.View r0 = r0.getContentView()
            r3 = 4
            r0.setVisibility(r3)
            org.telegram.ui.PaymentFormActivity$13 r0 = new org.telegram.ui.PaymentFormActivity$13
            r0.<init>(r1, r2)
            r1.webView = r0
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
            if (r0 < r4) goto L_0x182b
            android.webkit.WebView r0 = r1.webView
            android.webkit.WebSettings r0 = r0.getSettings()
            r0.setMixedContentMode(r12)
            android.webkit.CookieManager r0 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r4 = r1.webView
            r0.setAcceptThirdPartyCookies(r4, r3)
        L_0x182b:
            android.webkit.WebView r0 = r1.webView
            org.telegram.ui.PaymentFormActivity$14 r3 = new org.telegram.ui.PaymentFormActivity$14
            r3.<init>()
            r0.setWebViewClient(r3)
            android.webkit.WebView r0 = r1.webView
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r6.addView(r0, r3)
            android.webkit.WebView r0 = r1.webView
            r3 = 8
            r0.setVisibility(r3)
        L_0x1848:
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            org.telegram.ui.Cells.ShadowSectionCell r3 = new org.telegram.ui.Cells.ShadowSectionCell
            r3.<init>(r2)
            r4 = 1
            r0[r4] = r3
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r1.sectionCell
            r0 = r0[r4]
            r3 = 2131165447(0x7var_, float:1.7945111E38)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r10)
            r0.setBackgroundDrawable(r2)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell[] r2 = r1.sectionCell
            r2 = r2[r4]
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
        L_0x186f:
            android.view.View r0 = r1.fragmentView
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
                    PaymentFormActivity.this.lambda$null$0$PaymentFormActivity(str, str2);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$PaymentFormActivity(String str, String str2) {
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
                    PaymentFormActivity.this.lambda$null$5$PaymentFormActivity(str, str2);
                }
            });
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$PaymentFormActivity(String str, String str2) {
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
        PaymentFormActivity paymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, (TLRPC$TL_inputPaymentCredentialsGooglePay) null);
        paymentFormActivity.setDelegate(new PaymentFormActivityDelegate() {
            public void currentPasswordUpdated(TLRPC$TL_account_password tLRPC$TL_account_password) {
            }

            public void onFragmentDestroyed() {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$15 */
    public /* synthetic */ void lambda$createView$15$PaymentFormActivity(String str, String str2, View view) {
        TLRPC$User tLRPC$User = this.botUser;
        if (tLRPC$User == null || tLRPC$User.verified) {
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
                public final /* synthetic */ String f$1;

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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$14 */
    public /* synthetic */ void lambda$null$14$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
        showPayAlert(str);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$16 */
    public /* synthetic */ boolean lambda$createView$16$PaymentFormActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        sendSavePassword(false);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$18 */
    public /* synthetic */ void lambda$createView$18$PaymentFormActivity(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), $$Lambda$PaymentFormActivity$GfAnBT_1LLaYF4JkADBwshLg8aA.INSTANCE);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$20 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$19 */
    public /* synthetic */ void lambda$null$19$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$21 */
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
                PaymentFormActivity.this.lambda$createGooglePayButton$22$PaymentFormActivity(view);
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
    /* renamed from: lambda$createGooglePayButton$22 */
    public /* synthetic */ void lambda$createGooglePayButton$22$PaymentFormActivity(View view) {
        this.googlePayButton.setClickable(false);
        try {
            JSONObject baseRequest = getBaseRequest();
            JSONObject baseCardPaymentMethod = getBaseCardPaymentMethod();
            if (this.googlePayPublicKey != null) {
                baseCardPaymentMethod.put("tokenizationSpecification", new JSONObject() {
                    {
                        put("type", "DIRECT");
                        put("parameters", new JSONObject() {
                            {
                                put("protocolVersion", "ECv2");
                                put("publicKey", PaymentFormActivity.this.googlePayPublicKey);
                            }
                        });
                    }
                });
            } else {
                baseCardPaymentMethod.put("tokenizationSpecification", new JSONObject() {
                    {
                        put("type", "PAYMENT_GATEWAY");
                        if (PaymentFormActivity.this.googlePayParameters != null) {
                            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                                Toast.makeText(PaymentFormActivity.this.getParentActivity(), "use params", 0).show();
                            }
                            put("parameters", PaymentFormActivity.this.googlePayParameters);
                            return;
                        }
                        put("parameters", new JSONObject() {
                            {
                                put("gateway", "stripe");
                                put("stripe:publishableKey", PaymentFormActivity.this.stripeApiKey);
                                put("stripe:version", "3.5.0");
                            }
                        });
                    }
                });
            }
            baseRequest.put("allowedPaymentMethods", new JSONArray().put(baseCardPaymentMethod));
            JSONObject jSONObject = new JSONObject();
            ArrayList arrayList = new ArrayList(this.paymentForm.invoice.prices);
            TLRPC$TL_shippingOption tLRPC$TL_shippingOption = this.shippingOption;
            if (tLRPC$TL_shippingOption != null) {
                arrayList.addAll(tLRPC$TL_shippingOption.prices);
            }
            jSONObject.put("totalPrice", getTotalPriceDecimalString(arrayList));
            jSONObject.put("totalPriceStatus", "FINAL");
            if (!TextUtils.isEmpty(this.googlePayCountryCode)) {
                jSONObject.put("countryCode", this.googlePayCountryCode);
            }
            jSONObject.put("currencyCode", this.paymentForm.invoice.currency);
            jSONObject.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");
            baseRequest.put("transactionInfo", jSONObject);
            baseRequest.put("merchantInfo", new JSONObject().put("merchantName", this.currentBotName));
            PaymentDataRequest fromJson = PaymentDataRequest.fromJson(baseRequest.toString());
            if (fromJson != null) {
                AutoResolveHelper.resolveTask(this.paymentsClient.loadPaymentData(fromJson), getParentActivity(), 991);
            }
        } catch (JSONException unused) {
            throw new RuntimeException("The price cannot be deserialized from the JSON object.");
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$loadPasswordInfo$25$PaymentFormActivity(tLObject, tLRPC$TL_error);
                }
            }, 10);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordInfo$25 */
    public /* synthetic */ void lambda$loadPasswordInfo$25$PaymentFormActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$24$PaymentFormActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$24 */
    public /* synthetic */ void lambda$null$24$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
                $$Lambda$PaymentFormActivity$TJjUJS3RKRMSdoe2var_YnfakFA r3 = new Runnable() {
                    public final void run() {
                        PaymentFormActivity.this.lambda$null$23$PaymentFormActivity();
                    }
                };
                this.shortPollRunnable = r3;
                AndroidUtilities.runOnUIThread(r3, 5000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$23 */
    public /* synthetic */ void lambda$null$23$PaymentFormActivity() {
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
                PaymentFormActivity.this.lambda$showPayAlert$26$PaymentFormActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPayAlert$26 */
    public /* synthetic */ void lambda$showPayAlert$26$PaymentFormActivity(DialogInterface dialogInterface, int i) {
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
            Wallet.WalletOptions.Builder builder = new Wallet.WalletOptions.Builder();
            builder.setEnvironment(this.paymentForm.invoice.test ? 3 : 1);
            builder.setTheme(1);
            this.paymentsClient = Wallet.getPaymentsClient(context, builder.build());
            Optional<JSONObject> isReadyToPayRequest = getIsReadyToPayRequest();
            if (isReadyToPayRequest.isPresent() && (fromJson = IsReadyToPayRequest.fromJson(((JSONObject) isReadyToPayRequest.get()).toString())) != null) {
                this.paymentsClient.isReadyToPay(fromJson).addOnCompleteListener(getParentActivity(), new OnCompleteListener() {
                    public final void onComplete(Task task) {
                        PaymentFormActivity.this.lambda$initGooglePay$27$PaymentFormActivity(task);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initGooglePay$27 */
    public /* synthetic */ void lambda$initGooglePay$27$PaymentFormActivity(Task task) {
        if (task.isSuccessful()) {
            FrameLayout frameLayout = this.googlePayContainer;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.googlePayContainer, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(180);
                animatorSet.start();
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
                    PaymentFormActivity.this.lambda$onActivityResultFragment$28$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActivityResultFragment$28 */
    public /* synthetic */ void lambda$onActivityResultFragment$28$PaymentFormActivity(int i, Intent intent) {
        if (i == -1) {
            String json = PaymentData.getFromIntent(intent).toJson();
            if (json != null) {
                try {
                    JSONObject jSONObject = new JSONObject(json).getJSONObject("paymentMethodData");
                    JSONObject jSONObject2 = jSONObject.getJSONObject("tokenizationData");
                    jSONObject2.getString("type");
                    String string = jSONObject2.getString("token");
                    if (this.googlePayPublicKey != null) {
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
                    } else {
                        Token parseToken = TokenParser.parseToken(string);
                        this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{parseToken.getType(), parseToken.getId()});
                        Card card = parseToken.getCard();
                        this.cardName = card.getType() + " *" + card.getLast4();
                    }
                    goToNextStep();
                } catch (JSONException unused) {
                    throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
                }
            } else {
                return;
            }
        } else if (i == 1) {
            Status statusFromIntent = AutoResolveHelper.getStatusFromIntent(intent);
            FileLog.e("android pay error " + statusFromIntent.getStatusMessage());
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
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i2, this.requestedInfo, (TLRPC$TL_shippingOption) null, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials), this.isWebView);
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
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, (String) null, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials), this.isWebView);
        } else if (i3 == 2) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm2 = this.paymentForm;
            if (!tLRPC$TL_payments_paymentForm2.password_missing || !(z = this.saveCardInfo)) {
                PaymentFormActivityDelegate paymentFormActivityDelegate = this.delegate;
                if (paymentFormActivityDelegate != null) {
                    paymentFormActivityDelegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials);
                    finishFragment();
                    return;
                }
                presentFragment(new PaymentFormActivity(tLRPC$TL_payments_paymentForm2, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials), this.isWebView);
                return;
            }
            PaymentFormActivity paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm2, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, z, this.googlePayCredentials);
            this.passwordFragment = paymentFormActivity;
            paymentFormActivity.setCurrentPassword(this.currentPassword);
            this.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
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
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, this.passwordOk ? 4 : 2, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials), !this.passwordOk);
        } else if (i3 == 4) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (i3 != 6) {
        } else {
            if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.googlePayCredentials)) {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.googlePayCredentials), true);
            } else {
                finishFragment();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSavePaymentField() {
        WebView webView2;
        if (this.bottomCell[0] != null && this.sectionCell[2] != null) {
            TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = this.paymentForm;
            if ((tLRPC$TL_payments_paymentForm.password_missing || tLRPC$TL_payments_paymentForm.can_save_credentials) && ((webView2 = this.webView) == null || (webView2 != null && !this.webviewLoading))) {
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
                    tLRPC$TL_account_passwordInputSettings2.flags |= 1;
                    tLRPC$TL_account_passwordInputSettings2.hint = "";
                    tLRPC$TL_account_passwordInputSettings2.new_algo = this.currentPassword.new_algo;
                    if (obj2.length() > 0) {
                        TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings3 = tLRPC$TL_account_updatePasswordSettings.new_settings;
                        tLRPC$TL_account_passwordInputSettings3.flags = 2 | tLRPC$TL_account_passwordInputSettings3.flags;
                        tLRPC$TL_account_passwordInputSettings3.email = obj2.trim();
                    }
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
                    PaymentFormActivity.this.lambda$sendSavePassword$36$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
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
                PaymentFormActivity.this.lambda$sendSavePassword$30$PaymentFormActivity(tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$30 */
    public /* synthetic */ void lambda$sendSavePassword$30$PaymentFormActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PaymentFormActivity.this.lambda$null$29$PaymentFormActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$29 */
    public /* synthetic */ void lambda$null$29$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error) {
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
    /* renamed from: lambda$null$35 */
    public /* synthetic */ void lambda$null$35$PaymentFormActivity(boolean z, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                PaymentFormActivity.this.lambda$null$34$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSavePassword$36 */
    public /* synthetic */ void lambda$sendSavePassword$36$PaymentFormActivity(boolean z, String str, String str2, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        $$Lambda$PaymentFormActivity$tmUJjqfIk2w8_fCZQU_ekNMbUEM r0 = new RequestDelegate(z, str) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PaymentFormActivity.this.lambda$null$35$PaymentFormActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, String str) {
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
                            PaymentFormActivity.this.lambda$null$33$PaymentFormActivity(this.f$1, dialogInterface, i);
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
                    PaymentFormActivity.this.lambda$null$32$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 8);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$32 */
    public /* synthetic */ void lambda$null$32$PaymentFormActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                PaymentFormActivity.this.lambda$null$31$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            sendSavePassword(z);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$33 */
    public /* synthetic */ void lambda$null$33$PaymentFormActivity(String str, DialogInterface dialogInterface, int i) {
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
                                    PaymentFormActivity.AnonymousClass20.this.lambda$onSuccess$0$PaymentFormActivity$20();
                                }
                            });
                        }
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onSuccess$0 */
                    public /* synthetic */ void lambda$onSuccess$0$PaymentFormActivity$20() {
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
            TLRPC$TL_payments_validateRequestedInfo tLRPC$TL_payments_validateRequestedInfo = new TLRPC$TL_payments_validateRequestedInfo();
            this.validateRequest = tLRPC$TL_payments_validateRequestedInfo;
            tLRPC$TL_payments_validateRequestedInfo.save = this.saveShippingInfo;
            tLRPC$TL_payments_validateRequestedInfo.msg_id = this.messageObject.getId();
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
                    PaymentFormActivity.this.lambda$sendForm$40$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendForm$40 */
    public /* synthetic */ void lambda$sendForm$40$PaymentFormActivity(TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject2 instanceof TLRPC$TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject2) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$38$PaymentFormActivity(this.f$1);
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
                    PaymentFormActivity.this.lambda$null$39$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$38 */
    public /* synthetic */ void lambda$null$38$PaymentFormActivity(TLObject tLObject) {
        this.requestedInfo = (TLRPC$TL_payments_validatedRequestedInfo) tLObject;
        if (this.paymentForm.saved_info != null && !this.saveShippingInfo) {
            TLRPC$TL_payments_clearSavedInfo tLRPC$TL_payments_clearSavedInfo = new TLRPC$TL_payments_clearSavedInfo();
            tLRPC$TL_payments_clearSavedInfo.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_clearSavedInfo, $$Lambda$PaymentFormActivity$lUrplA1M0XW4RgHIoe2OwY1lYsU.INSTANCE);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$39 */
    public /* synthetic */ void lambda$null$39$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_payments_sendPaymentForm, new RequestDelegate(tLRPC$TL_payments_sendPaymentForm) {
                public final /* synthetic */ TLRPC$TL_payments_sendPaymentForm f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PaymentFormActivity.this.lambda$sendData$43$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendData$43 */
    public /* synthetic */ void lambda$sendData$43$PaymentFormActivity(TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_payments_sendPaymentForm) {
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ TLRPC$TL_payments_sendPaymentForm f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$42$PaymentFormActivity(this.f$1, this.f$2);
                }
            });
        } else if (tLObject instanceof TLRPC$TL_payments_paymentResult) {
            MessagesController.getInstance(this.currentAccount).processUpdates(((TLRPC$TL_payments_paymentResult) tLObject).updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PaymentFormActivity.this.goToNextStep();
                }
            });
        } else if (tLObject instanceof TLRPC$TL_payments_paymentVerificationNeeded) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PaymentFormActivity.this.lambda$null$41$PaymentFormActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$41 */
    public /* synthetic */ void lambda$null$41$PaymentFormActivity(TLObject tLObject) {
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
    /* renamed from: lambda$null$42 */
    public /* synthetic */ void lambda$null$42$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm) {
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
                    PaymentFormActivity.this.lambda$checkPassword$48$PaymentFormActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkPassword$48 */
    public /* synthetic */ void lambda$checkPassword$48$PaymentFormActivity(String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                PaymentFormActivity.this.lambda$null$47$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$47 */
    public /* synthetic */ void lambda$null$47$PaymentFormActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, TLRPC$TL_account_getPassword tLRPC$TL_account_getPassword) {
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
                        PaymentFormActivity.this.lambda$null$46$PaymentFormActivity(this.f$1, this.f$2);
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
    /* renamed from: lambda$null$46 */
    public /* synthetic */ void lambda$null$46$PaymentFormActivity(TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr) {
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword = new TLRPC$TL_account_getTmpPassword();
        tLRPC$TL_account_getTmpPassword.period = 1800;
        $$Lambda$PaymentFormActivity$kLoWIbPuCReZSjhsG6vvfolZRqo r1 = new RequestDelegate(tLRPC$TL_account_getTmpPassword) {
            public final /* synthetic */ TLRPC$TL_account_getTmpPassword f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                PaymentFormActivity.this.lambda$null$45$PaymentFormActivity(this.f$1, tLObject, tLRPC$TL_error);
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
    /* renamed from: lambda$null$45 */
    public /* synthetic */ void lambda$null$45$PaymentFormActivity(TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                PaymentFormActivity.this.lambda$null$44$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$44 */
    public /* synthetic */ void lambda$null$44$PaymentFormActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword) {
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
