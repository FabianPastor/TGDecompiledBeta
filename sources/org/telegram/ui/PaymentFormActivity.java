package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.Wallet.WalletOptions;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_noPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_dataJSON;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentials;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentialsAndroidPay;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentialsSaved;
import org.telegram.tgnet.TLRPC.TL_labeledPrice;
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
import org.telegram.tgnet.TLRPC.account_Password;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
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
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

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
    private HashMap<String, String> codesMap;
    private ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private String countryName;
    private String currentBotName;
    private String currentItemName;
    private account_Password currentPassword;
    private int currentStep;
    private PaymentFormActivityDelegate delegate;
    private TextDetailSettingsCell[] detailSettingsCell;
    private ArrayList<View> dividers;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
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
    private TextSettingsCell settingsCell1;
    private TL_shippingOption shippingOption;
    private Runnable shortPollRunnable;
    private String stripeApiKey;
    private TextView textView;
    private String totalPriceDecimal;
    private TL_payments_validateRequestedInfo validateRequest;
    private boolean waitingForEmail;
    private WebView webView;
    private boolean webviewLoading;

    /* renamed from: org.telegram.ui.PaymentFormActivity$2 */
    class C15602 implements Comparator<String> {
        C15602() {
        }

        public int compare(String str, String str2) {
            return str.compareTo(str2);
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$3 */
    class C15713 implements OnTouchListener {

        /* renamed from: org.telegram.ui.PaymentFormActivity$3$1 */
        class C22181 implements CountrySelectActivityDelegate {
            C22181() {
            }

            public void didSelectCountry(String str, String str2) {
                PaymentFormActivity.this.inputFields[4].setText(str);
                PaymentFormActivity.this.countryName = str2;
            }
        }

        C15713() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (PaymentFormActivity.this.getParentActivity() == null) {
                return false;
            }
            if (motionEvent.getAction() == 1) {
                view = new CountrySelectActivity(false);
                view.setCountrySelectActivityDelegate(new C22181());
                PaymentFormActivity.this.presentFragment(view);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$4 */
    class C15724 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C15724() {
        }

        public void afterTextChanged(Editable editable) {
            if (PaymentFormActivity.this.ignoreOnTextChange == null) {
                boolean z = true;
                PaymentFormActivity.this.ignoreOnTextChange = true;
                editable = PhoneFormat.stripExceptNumbers(PaymentFormActivity.this.inputFields[8].getText().toString());
                PaymentFormActivity.this.inputFields[8].setText(editable);
                HintEditText hintEditText = (HintEditText) PaymentFormActivity.this.inputFields[9];
                if (editable.length() == 0) {
                    hintEditText.setHintText(null);
                    hintEditText.setHint(LocaleController.getString("PaymentShippingPhoneNumber", C0446R.string.PaymentShippingPhoneNumber));
                } else {
                    Object substring;
                    CharSequence stringBuilder;
                    int i = 4;
                    if (editable.length() > 4) {
                        Editable editable2;
                        while (i >= 1) {
                            substring = editable.substring(0, i);
                            if (((String) PaymentFormActivity.this.codesMap.get(substring)) != null) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(editable.substring(i, editable.length()));
                                stringBuilder2.append(PaymentFormActivity.this.inputFields[9].getText().toString());
                                editable = stringBuilder2.toString();
                                PaymentFormActivity.this.inputFields[8].setText(substring);
                                editable2 = editable;
                                editable = 1;
                                break;
                            }
                            i--;
                        }
                        substring = editable;
                        editable2 = null;
                        editable = null;
                        if (editable == null) {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(substring.substring(1, substring.length()));
                            stringBuilder3.append(PaymentFormActivity.this.inputFields[9].getText().toString());
                            stringBuilder = stringBuilder3.toString();
                            EditTextBoldCursor editTextBoldCursor = PaymentFormActivity.this.inputFields[8];
                            substring = substring.substring(0, 1);
                            editTextBoldCursor.setText(substring);
                        } else {
                            stringBuilder = editable2;
                        }
                    } else {
                        substring = editable;
                        stringBuilder = null;
                        editable = null;
                    }
                    String str = (String) PaymentFormActivity.this.codesMap.get(substring);
                    if (!(str == null || PaymentFormActivity.this.countriesArray.indexOf(str) == -1)) {
                        String str2 = (String) PaymentFormActivity.this.phoneFormatMap.get(substring);
                        if (str2 != null) {
                            hintEditText.setHintText(str2.replace('X', '\u2013'));
                            hintEditText.setHint(null);
                            if (!z) {
                                hintEditText.setHintText(null);
                                hintEditText.setHint(LocaleController.getString("PaymentShippingPhoneNumber", C0446R.string.PaymentShippingPhoneNumber));
                            }
                            if (editable == null) {
                                PaymentFormActivity.this.inputFields[8].setSelection(PaymentFormActivity.this.inputFields[8].getText().length());
                            }
                            if (stringBuilder != null) {
                                hintEditText.requestFocus();
                                hintEditText.setText(stringBuilder);
                                hintEditText.setSelection(hintEditText.length());
                            }
                        }
                    }
                    z = false;
                    if (z) {
                        hintEditText.setHintText(null);
                        hintEditText.setHint(LocaleController.getString("PaymentShippingPhoneNumber", C0446R.string.PaymentShippingPhoneNumber));
                    }
                    if (editable == null) {
                        PaymentFormActivity.this.inputFields[8].setSelection(PaymentFormActivity.this.inputFields[8].getText().length());
                    }
                    if (stringBuilder != null) {
                        hintEditText.requestFocus();
                        hintEditText.setText(stringBuilder);
                        hintEditText.setSelection(hintEditText.length());
                    }
                }
                PaymentFormActivity.this.ignoreOnTextChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$5 */
    class C15735 implements TextWatcher {
        private int actionPosition;
        private int characterAction = -1;

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C15735() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (i2 == 0 && i3 == 1) {
                this.characterAction = 1;
            } else if (i2 != 1 || i3 != 0) {
                this.characterAction = -1;
            } else if (charSequence.charAt(i) != 32 || i <= 0) {
                this.characterAction = 2;
            } else {
                this.characterAction = 3;
                this.actionPosition = i - 1;
            }
        }

        public void afterTextChanged(Editable editable) {
            if (PaymentFormActivity.this.ignoreOnPhoneChange == null) {
                HintEditText hintEditText = (HintEditText) PaymentFormActivity.this.inputFields[9];
                int selectionStart = hintEditText.getSelectionStart();
                String str = "0123456789";
                String obj = hintEditText.getText().toString();
                if (this.characterAction == 3) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(obj.substring(0, this.actionPosition));
                    stringBuilder.append(obj.substring(this.actionPosition + 1, obj.length()));
                    obj = stringBuilder.toString();
                    selectionStart--;
                }
                CharSequence stringBuilder2 = new StringBuilder(obj.length());
                int i = 0;
                while (i < obj.length()) {
                    int i2 = i + 1;
                    Object substring = obj.substring(i, i2);
                    if (str.contains(substring)) {
                        stringBuilder2.append(substring);
                    }
                    i = i2;
                }
                PaymentFormActivity.this.ignoreOnPhoneChange = true;
                str = hintEditText.getHintText();
                if (str != null) {
                    int i3 = selectionStart;
                    selectionStart = 0;
                    while (selectionStart < stringBuilder2.length()) {
                        if (selectionStart < str.length()) {
                            if (str.charAt(selectionStart) == ' ') {
                                stringBuilder2.insert(selectionStart, ' ');
                                selectionStart++;
                                if (!(i3 != selectionStart || this.characterAction == 2 || this.characterAction == 3)) {
                                    i3++;
                                }
                            }
                            selectionStart++;
                        } else {
                            stringBuilder2.insert(selectionStart, ' ');
                            if (!(i3 != selectionStart + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                selectionStart = i3 + 1;
                            }
                            selectionStart = i3;
                        }
                    }
                    selectionStart = i3;
                }
                hintEditText.setText(stringBuilder2);
                if (selectionStart >= 0) {
                    if (selectionStart > hintEditText.length()) {
                        selectionStart = hintEditText.length();
                    }
                    hintEditText.setSelection(selectionStart);
                }
                hintEditText.onTextChange();
                PaymentFormActivity.this.ignoreOnPhoneChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$6 */
    class C15746 implements OnEditorActionListener {
        C15746() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                textView = ((Integer) textView.getTag()).intValue();
                while (true) {
                    textView += 1;
                    if (textView < PaymentFormActivity.this.inputFields.length) {
                        if (textView != 4 && ((View) PaymentFormActivity.this.inputFields[textView].getParent()).getVisibility() == 0) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                PaymentFormActivity.this.inputFields[textView].requestFocus();
                return true;
            } else if (i != 6) {
                return null;
            } else {
                PaymentFormActivity.this.doneItem.performClick();
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$7 */
    class C15757 implements OnClickListener {
        C15757() {
        }

        public void onClick(View view) {
            PaymentFormActivity.this.saveShippingInfo = PaymentFormActivity.this.saveShippingInfo ^ 1;
            PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveShippingInfo);
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$9 */
    class C15779 extends WebViewClient {
        C15779() {
        }

        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            PaymentFormActivity.this.webviewLoading = false;
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.updateSavePaymentField();
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                textView = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return textView;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }
    }

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
        void currentPasswordUpdated(account_Password account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(final String str, final String str2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (PaymentFormActivity.this.getParentActivity() != null && str.equals("payment_form_submit")) {
                        try {
                            JSONObject jSONObject = new JSONObject(str2);
                            PaymentFormActivity.this.paymentJson = jSONObject.getJSONObject("credentials").toString();
                            PaymentFormActivity.this.cardName = jSONObject.getString("title");
                        } catch (Throwable th) {
                            PaymentFormActivity.this.paymentJson = str2;
                            FileLog.m3e(th);
                        }
                        PaymentFormActivity.this.goToNextStep();
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$1 */
    class C22161 extends ActionBarMenuOnItemClick {
        C22161() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                if (PaymentFormActivity.this.donePressed == 0) {
                    PaymentFormActivity.this.finishFragment();
                }
            } else if (i == 1 && PaymentFormActivity.this.donePressed == 0) {
                if (PaymentFormActivity.this.currentStep != 3) {
                    AndroidUtilities.hideKeyboard(PaymentFormActivity.this.getParentActivity().getCurrentFocus());
                }
                if (PaymentFormActivity.this.currentStep == 0) {
                    PaymentFormActivity.this.setDonePressed(true);
                    PaymentFormActivity.this.sendForm();
                } else {
                    int i2 = 0;
                    if (PaymentFormActivity.this.currentStep == 1) {
                        while (i2 < PaymentFormActivity.this.radioCells.length) {
                            if (PaymentFormActivity.this.radioCells[i2].isChecked() != 0) {
                                PaymentFormActivity.this.shippingOption = (TL_shippingOption) PaymentFormActivity.this.requestedInfo.shipping_options.get(i2);
                                break;
                            }
                            i2++;
                        }
                        PaymentFormActivity.this.goToNextStep();
                    } else if (PaymentFormActivity.this.currentStep == 2) {
                        PaymentFormActivity.this.sendCardData();
                    } else if (PaymentFormActivity.this.currentStep == 3) {
                        PaymentFormActivity.this.checkPassword();
                    } else if (PaymentFormActivity.this.currentStep == 6) {
                        PaymentFormActivity.this.sendSavePassword(false);
                    }
                }
            }
        }
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
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        this.currentStep = 5;
        this.paymentForm = new TL_payments_paymentForm();
        this.paymentForm.bot_id = tL_payments_paymentReceipt.bot_id;
        this.paymentForm.invoice = tL_payments_paymentReceipt.invoice;
        this.paymentForm.provider_id = tL_payments_paymentReceipt.provider_id;
        this.paymentForm.users = tL_payments_paymentReceipt.users;
        this.shippingOption = tL_payments_paymentReceipt.shipping;
        this.messageObject = messageObject;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_payments_paymentReceipt.bot_id));
        if (this.botUser != null) {
            this.currentBotName = this.botUser.first_name;
        } else {
            this.currentBotName = TtmlNode.ANONYMOUS_REGION_ID;
        }
        this.currentItemName = messageObject.messageOwner.media.title;
        if (tL_payments_paymentReceipt.info != null) {
            this.validateRequest = new TL_payments_validateRequestedInfo();
            this.validateRequest.info = tL_payments_paymentReceipt.info;
        }
        this.cardName = tL_payments_paymentReceipt.credentials_title;
    }

    public PaymentFormActivity(TL_payments_paymentForm tL_payments_paymentForm, MessageObject messageObject) {
        int i;
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        int i2 = 3;
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        if (!(tL_payments_paymentForm.invoice.shipping_address_requested || tL_payments_paymentForm.invoice.email_requested || tL_payments_paymentForm.invoice.name_requested)) {
            if (!tL_payments_paymentForm.invoice.phone_requested) {
                if (tL_payments_paymentForm.saved_credentials != null) {
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    }
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                        i2 = 4;
                    }
                } else {
                    i2 = 2;
                }
                i = i2;
                init(tL_payments_paymentForm, messageObject, i, null, null, null, null, null, false, null);
            }
        }
        i = 0;
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
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        init(tL_payments_paymentForm, messageObject, i, tL_payments_validatedRequestedInfo, tL_shippingOption, str, str2, tL_payments_validateRequestedInfo, z, tL_inputPaymentCredentialsAndroidPay);
    }

    private void setCurrentPassword(account_Password account_password) {
        if (!(account_password instanceof TL_account_password)) {
            this.currentPassword = account_password;
            if (this.currentPassword != null) {
                this.waitingForEmail = this.currentPassword.email_unconfirmed_pattern.length() > null ? true : null;
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
        tL_payments_validatedRequestedInfo = true;
        this.isWebView = "stripe".equals(this.paymentForm.native_provider) ^ 1;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_payments_paymentForm.bot_id));
        if (this.botUser != 0) {
            this.currentBotName = this.botUser.first_name;
        } else {
            this.currentBotName = TtmlNode.ANONYMOUS_REGION_ID;
        }
        this.currentItemName = messageObject.messageOwner.media.title;
        this.validateRequest = tL_payments_validateRequestedInfo;
        this.saveShippingInfo = true;
        if (z) {
            this.saveCardInfo = z;
        } else {
            if (this.paymentForm.saved_credentials == null) {
                tL_payments_validatedRequestedInfo = null;
            }
            this.saveCardInfo = tL_payments_validatedRequestedInfo;
        }
        if (str2 != null) {
            this.cardName = str2;
        } else if (tL_payments_paymentForm.saved_credentials != null) {
            this.cardName = tL_payments_paymentForm.saved_credentials.title;
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (VERSION.SDK_INT >= 23) {
            try {
                if ((this.currentStep == 2 || this.currentStep == 6) && !this.paymentForm.invoice.test) {
                    getParentActivity().getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
                } else if (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                }
            } catch (Throwable th) {
                FileLog.m3e(th);
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

    @android.annotation.SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public android.view.View createView(android.content.Context r30) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r29 = this;
        r1 = r29;
        r2 = r30;
        r3 = r1.currentStep;
        r4 = 6;
        r5 = 5;
        r6 = 4;
        r7 = 3;
        r8 = 2;
        r9 = 1;
        if (r3 != 0) goto L_0x001e;
    L_0x000e:
        r3 = r1.actionBar;
        r10 = "PaymentShippingInfo";
        r11 = NUM; // 0x7f0c04ea float:1.8611743E38 double:1.05309802E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x001e:
        r3 = r1.currentStep;
        if (r3 != r9) goto L_0x0032;
    L_0x0022:
        r3 = r1.actionBar;
        r10 = "PaymentShippingMethod";
        r11 = NUM; // 0x7f0c04eb float:1.8611745E38 double:1.0530980205E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x0032:
        r3 = r1.currentStep;
        if (r3 != r8) goto L_0x0046;
    L_0x0036:
        r3 = r1.actionBar;
        r10 = "PaymentCardInfo";
        r11 = NUM; // 0x7f0c04c2 float:1.8611662E38 double:1.053098E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x0046:
        r3 = r1.currentStep;
        if (r3 != r7) goto L_0x005a;
    L_0x004a:
        r3 = r1.actionBar;
        r10 = "PaymentCardInfo";
        r11 = NUM; // 0x7f0c04c2 float:1.8611662E38 double:1.053098E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x005a:
        r3 = r1.currentStep;
        if (r3 != r6) goto L_0x0095;
    L_0x005e:
        r3 = r1.paymentForm;
        r3 = r3.invoice;
        r3 = r3.test;
        if (r3 == 0) goto L_0x0086;
    L_0x0066:
        r3 = r1.actionBar;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "Test ";
        r10.append(r11);
        r11 = "PaymentCheckout";
        r12 = NUM; // 0x7f0c04c9 float:1.8611677E38 double:1.0530980037E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
        r10.append(r11);
        r10 = r10.toString();
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x0086:
        r3 = r1.actionBar;
        r10 = "PaymentCheckout";
        r11 = NUM; // 0x7f0c04c9 float:1.8611677E38 double:1.0530980037E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x0095:
        r3 = r1.currentStep;
        if (r3 != r5) goto L_0x00d0;
    L_0x0099:
        r3 = r1.paymentForm;
        r3 = r3.invoice;
        r3 = r3.test;
        if (r3 == 0) goto L_0x00c1;
    L_0x00a1:
        r3 = r1.actionBar;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "Test ";
        r10.append(r11);
        r11 = "PaymentReceipt";
        r12 = NUM; // 0x7f0c04e3 float:1.861173E38 double:1.0530980165E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
        r10.append(r11);
        r10 = r10.toString();
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x00c1:
        r3 = r1.actionBar;
        r10 = "PaymentReceipt";
        r11 = NUM; // 0x7f0c04e3 float:1.861173E38 double:1.0530980165E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
        goto L_0x00e2;
    L_0x00d0:
        r3 = r1.currentStep;
        if (r3 != r4) goto L_0x00e2;
    L_0x00d4:
        r3 = r1.actionBar;
        r10 = "PaymentPassword";
        r11 = NUM; // 0x7f0c04d8 float:1.8611707E38 double:1.053098011E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
        r3.setTitle(r10);
    L_0x00e2:
        r3 = r1.actionBar;
        r10 = NUM; // 0x7f0700a2 float:1.7944907E38 double:1.052935583E-314;
        r3.setBackButtonImage(r10);
        r3 = r1.actionBar;
        r3.setAllowOverlayTitle(r9);
        r3 = r1.actionBar;
        r10 = new org.telegram.ui.PaymentFormActivity$1;
        r10.<init>();
        r3.setActionBarMenuOnItemClick(r10);
        r3 = r1.actionBar;
        r3 = r3.createMenu();
        r10 = r1.currentStep;
        r11 = -1;
        if (r10 == 0) goto L_0x0118;
    L_0x0104:
        r10 = r1.currentStep;
        if (r10 == r9) goto L_0x0118;
    L_0x0108:
        r10 = r1.currentStep;
        if (r10 == r8) goto L_0x0118;
    L_0x010c:
        r10 = r1.currentStep;
        if (r10 == r7) goto L_0x0118;
    L_0x0110:
        r10 = r1.currentStep;
        if (r10 == r6) goto L_0x0118;
    L_0x0114:
        r10 = r1.currentStep;
        if (r10 != r4) goto L_0x0140;
    L_0x0118:
        r10 = NUM; // 0x7f0700c0 float:1.7944967E38 double:1.052935598E-314;
        r12 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r3 = r3.addItemWithWidth(r9, r10, r12);
        r1.doneItem = r3;
        r3 = new org.telegram.ui.Components.ContextProgressView;
        r3.<init>(r2, r9);
        r1.progressView = r3;
        r3 = r1.doneItem;
        r10 = r1.progressView;
        r12 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12);
        r3.addView(r10, r12);
        r3 = r1.progressView;
        r3.setVisibility(r6);
    L_0x0140:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r2);
        r1.fragmentView = r3;
        r3 = r1.fragmentView;
        r3 = (android.widget.FrameLayout) r3;
        r10 = r1.fragmentView;
        r12 = "windowBackgroundGray";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r10.setBackgroundColor(r12);
        r10 = new android.widget.ScrollView;
        r10.<init>(r2);
        r1.scrollView = r10;
        r10 = r1.scrollView;
        r10.setFillViewport(r9);
        r10 = r1.scrollView;
        r12 = "actionBarDefault";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor(r10, r12);
        r10 = r1.scrollView;
        r12 = -1;
        r13 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r14 = 51;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r5 = r1.currentStep;
        if (r5 != r6) goto L_0x0182;
    L_0x017d:
        r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x017f:
        r18 = r5;
        goto L_0x0184;
    L_0x0182:
        r5 = 0;
        goto L_0x017f;
    L_0x0184:
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18);
        r3.addView(r10, r5);
        r5 = new android.widget.LinearLayout;
        r5.<init>(r2);
        r1.linearLayout2 = r5;
        r5 = r1.linearLayout2;
        r5.setOrientation(r9);
        r5 = r1.scrollView;
        r10 = r1.linearLayout2;
        r12 = new android.widget.FrameLayout$LayoutParams;
        r13 = -2;
        r12.<init>(r11, r13);
        r5.addView(r10, r12);
        r5 = r1.currentStep;
        r14 = 0;
        if (r5 != 0) goto L_0x09bb;
    L_0x01a9:
        r3 = new java.util.HashMap;
        r3.<init>();
        r5 = new java.util.HashMap;
        r5.<init>();
        r6 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0210 }
        r15 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x0210 }
        r10 = r30.getResources();	 Catch:{ Exception -> 0x0210 }
        r10 = r10.getAssets();	 Catch:{ Exception -> 0x0210 }
        r12 = "countries.txt";	 Catch:{ Exception -> 0x0210 }
        r10 = r10.open(r12);	 Catch:{ Exception -> 0x0210 }
        r15.<init>(r10);	 Catch:{ Exception -> 0x0210 }
        r6.<init>(r15);	 Catch:{ Exception -> 0x0210 }
    L_0x01cb:
        r10 = r6.readLine();	 Catch:{ Exception -> 0x0210 }
        if (r10 == 0) goto L_0x020c;	 Catch:{ Exception -> 0x0210 }
    L_0x01d1:
        r12 = ";";	 Catch:{ Exception -> 0x0210 }
        r10 = r10.split(r12);	 Catch:{ Exception -> 0x0210 }
        r12 = r1.countriesArray;	 Catch:{ Exception -> 0x0210 }
        r15 = r10[r8];	 Catch:{ Exception -> 0x0210 }
        r12.add(r14, r15);	 Catch:{ Exception -> 0x0210 }
        r12 = r1.countriesMap;	 Catch:{ Exception -> 0x0210 }
        r15 = r10[r8];	 Catch:{ Exception -> 0x0210 }
        r4 = r10[r14];	 Catch:{ Exception -> 0x0210 }
        r12.put(r15, r4);	 Catch:{ Exception -> 0x0210 }
        r4 = r1.codesMap;	 Catch:{ Exception -> 0x0210 }
        r12 = r10[r14];	 Catch:{ Exception -> 0x0210 }
        r15 = r10[r8];	 Catch:{ Exception -> 0x0210 }
        r4.put(r12, r15);	 Catch:{ Exception -> 0x0210 }
        r4 = r10[r9];	 Catch:{ Exception -> 0x0210 }
        r12 = r10[r8];	 Catch:{ Exception -> 0x0210 }
        r5.put(r4, r12);	 Catch:{ Exception -> 0x0210 }
        r4 = r10.length;	 Catch:{ Exception -> 0x0210 }
        if (r4 <= r7) goto L_0x0203;	 Catch:{ Exception -> 0x0210 }
    L_0x01fa:
        r4 = r1.phoneFormatMap;	 Catch:{ Exception -> 0x0210 }
        r12 = r10[r14];	 Catch:{ Exception -> 0x0210 }
        r15 = r10[r7];	 Catch:{ Exception -> 0x0210 }
        r4.put(r12, r15);	 Catch:{ Exception -> 0x0210 }
    L_0x0203:
        r4 = r10[r9];	 Catch:{ Exception -> 0x0210 }
        r10 = r10[r8];	 Catch:{ Exception -> 0x0210 }
        r3.put(r4, r10);	 Catch:{ Exception -> 0x0210 }
        r4 = 6;	 Catch:{ Exception -> 0x0210 }
        goto L_0x01cb;	 Catch:{ Exception -> 0x0210 }
    L_0x020c:
        r6.close();	 Catch:{ Exception -> 0x0210 }
        goto L_0x0215;
    L_0x0210:
        r0 = move-exception;
        r4 = r0;
        org.telegram.messenger.FileLog.m3e(r4);
    L_0x0215:
        r4 = r1.countriesArray;
        r6 = new org.telegram.ui.PaymentFormActivity$2;
        r6.<init>();
        java.util.Collections.sort(r4, r6);
        r4 = 10;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r1.inputFields = r4;
        r4 = r14;
    L_0x0226:
        r6 = 10;
        r10 = 9;
        if (r4 >= r6) goto L_0x07ca;
    L_0x022c:
        if (r4 != 0) goto L_0x0264;
    L_0x022e:
        r6 = r1.headerCell;
        r12 = new org.telegram.ui.Cells.HeaderCell;
        r12.<init>(r2);
        r6[r14] = r12;
        r6 = r1.headerCell;
        r6 = r6[r14];
        r12 = "windowBackgroundWhite";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r6.setBackgroundColor(r12);
        r6 = r1.headerCell;
        r6 = r6[r14];
        r12 = "PaymentShippingAddress";
        r15 = NUM; // 0x7f0c04e4 float:1.8611731E38 double:1.053098017E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r6.setText(r12);
        r6 = r1.linearLayout2;
        r12 = r1.headerCell;
        r12 = r12[r14];
        r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r12, r15);
    L_0x0261:
        r6 = 8;
        goto L_0x02b1;
    L_0x0264:
        r6 = 6;
        if (r4 != r6) goto L_0x0261;
    L_0x0267:
        r6 = r1.sectionCell;
        r12 = new org.telegram.ui.Cells.ShadowSectionCell;
        r12.<init>(r2);
        r6[r14] = r12;
        r6 = r1.linearLayout2;
        r12 = r1.sectionCell;
        r12 = r12[r14];
        r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r12, r15);
        r6 = r1.headerCell;
        r12 = new org.telegram.ui.Cells.HeaderCell;
        r12.<init>(r2);
        r6[r9] = r12;
        r6 = r1.headerCell;
        r6 = r6[r9];
        r12 = "windowBackgroundWhite";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r6.setBackgroundColor(r12);
        r6 = r1.headerCell;
        r6 = r6[r9];
        r12 = "PaymentShippingReceiver";
        r15 = NUM; // 0x7f0c04ee float:1.8611752E38 double:1.053098022E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r6.setText(r12);
        r6 = r1.linearLayout2;
        r12 = r1.headerCell;
        r12 = r12[r9];
        r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r12, r15);
        goto L_0x0261;
    L_0x02b1:
        if (r4 != r6) goto L_0x02d4;
    L_0x02b3:
        r6 = new android.widget.LinearLayout;
        r6.<init>(r2);
        r12 = r6;
        r12 = (android.widget.LinearLayout) r12;
        r12.setOrientation(r14);
        r12 = r1.linearLayout2;
        r15 = 48;
        r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r15);
        r12.addView(r6, r8);
        r8 = "windowBackgroundWhite";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r6.setBackgroundColor(r8);
        goto L_0x0346;
    L_0x02d4:
        if (r4 != r10) goto L_0x02e3;
    L_0x02d6:
        r6 = r1.inputFields;
        r8 = 8;
        r6 = r6[r8];
        r6 = r6.getParent();
        r6 = (android.view.ViewGroup) r6;
        goto L_0x0346;
    L_0x02e3:
        r6 = new android.widget.FrameLayout;
        r6.<init>(r2);
        r8 = r1.linearLayout2;
        r12 = 48;
        r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12);
        r8.addView(r6, r15);
        r8 = "windowBackgroundWhite";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r6.setBackgroundColor(r8);
        r8 = 5;
        if (r4 == r8) goto L_0x0303;
    L_0x02ff:
        if (r4 == r10) goto L_0x0303;
    L_0x0301:
        r8 = r9;
        goto L_0x0304;
    L_0x0303:
        r8 = r14;
    L_0x0304:
        if (r8 == 0) goto L_0x0327;
    L_0x0306:
        r12 = 7;
        if (r4 != r12) goto L_0x0313;
    L_0x0309:
        r12 = r1.paymentForm;
        r12 = r12.invoice;
        r12 = r12.phone_requested;
        if (r12 != 0) goto L_0x0313;
    L_0x0311:
        r8 = r14;
        goto L_0x0327;
    L_0x0313:
        r12 = 6;
        if (r4 != r12) goto L_0x0327;
    L_0x0316:
        r12 = r1.paymentForm;
        r12 = r12.invoice;
        r12 = r12.phone_requested;
        if (r12 != 0) goto L_0x0327;
    L_0x031e:
        r12 = r1.paymentForm;
        r12 = r12.invoice;
        r12 = r12.email_requested;
        if (r12 != 0) goto L_0x0327;
    L_0x0326:
        goto L_0x0311;
    L_0x0327:
        if (r8 == 0) goto L_0x0346;
    L_0x0329:
        r8 = new android.view.View;
        r8.<init>(r2);
        r12 = r1.dividers;
        r12.add(r8);
        r12 = "divider";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r8.setBackgroundColor(r12);
        r12 = new android.widget.FrameLayout$LayoutParams;
        r15 = 83;
        r12.<init>(r11, r9, r15);
        r6.addView(r8, r12);
    L_0x0346:
        if (r4 != r10) goto L_0x0352;
    L_0x0348:
        r8 = r1.inputFields;
        r12 = new org.telegram.ui.Components.HintEditText;
        r12.<init>(r2);
        r8[r4] = r12;
        goto L_0x035b;
    L_0x0352:
        r8 = r1.inputFields;
        r12 = new org.telegram.ui.Components.EditTextBoldCursor;
        r12.<init>(r2);
        r8[r4] = r12;
    L_0x035b:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = java.lang.Integer.valueOf(r4);
        r8.setTag(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8.setTextSize(r9, r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "windowBackgroundWhiteHintText";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r8.setHintTextColor(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "windowBackgroundWhiteBlackText";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r8.setTextColor(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = 0;
        r8.setBackgroundDrawable(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "windowBackgroundWhiteBlackText";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r8.setCursorColor(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r8.setCursorSize(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r8.setCursorWidth(r12);
        r8 = 4;
        if (r4 != r8) goto L_0x03ca;
    L_0x03b7:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = new org.telegram.ui.PaymentFormActivity$3;
        r12.<init>();
        r8.setOnTouchListener(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r8.setInputType(r14);
    L_0x03ca:
        if (r4 == r10) goto L_0x03e6;
    L_0x03cc:
        r8 = 8;
        if (r4 != r8) goto L_0x03d1;
    L_0x03d0:
        goto L_0x03e6;
    L_0x03d1:
        r8 = 7;
        if (r4 != r8) goto L_0x03dc;
    L_0x03d4:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r8.setInputType(r9);
        goto L_0x03ed;
    L_0x03dc:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r8.setInputType(r12);
        goto L_0x03ed;
    L_0x03e6:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r8.setInputType(r7);
    L_0x03ed:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = 268435461; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r8.setImeOptions(r12);
        switch(r4) {
            case 0: goto L_0x0554;
            case 1: goto L_0x0526;
            case 2: goto L_0x04f8;
            case 3: goto L_0x04c9;
            case 4: goto L_0x0485;
            case 5: goto L_0x0456;
            case 6: goto L_0x0429;
            case 7: goto L_0x03fc;
            default: goto L_0x03fa;
        };
    L_0x03fa:
        goto L_0x0581;
    L_0x03fc:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingEmailPlaceholder";
        r15 = NUM; // 0x7f0c04e9 float:1.8611741E38 double:1.0530980195E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x0412:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.email;
        if (r8 == 0) goto L_0x0581;
    L_0x041a:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.email;
        r8.setText(r12);
        goto L_0x0581;
    L_0x0429:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingName";
        r15 = NUM; // 0x7f0c04ec float:1.8611748E38 double:1.053098021E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x043f:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.name;
        if (r8 == 0) goto L_0x0581;
    L_0x0447:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.name;
        r8.setText(r12);
        goto L_0x0581;
    L_0x0456:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingZipPlaceholder";
        r15 = NUM; // 0x7f0c04f2 float:1.861176E38 double:1.053098024E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x046c:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        if (r8 == 0) goto L_0x0581;
    L_0x0474:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.shipping_address;
        r12 = r12.post_code;
        r8.setText(r12);
        goto L_0x0581;
    L_0x0485:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingCountry";
        r15 = NUM; // 0x7f0c04e8 float:1.861174E38 double:1.053098019E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x049b:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        if (r8 == 0) goto L_0x0581;
    L_0x04a3:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        r8 = r8.country_iso2;
        r8 = r5.get(r8);
        r8 = (java.lang.String) r8;
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.shipping_address;
        r12 = r12.country_iso2;
        r1.countryName = r12;
        r12 = r1.inputFields;
        r12 = r12[r4];
        if (r8 == 0) goto L_0x04c2;
    L_0x04c1:
        goto L_0x04c4;
    L_0x04c2:
        r8 = r1.countryName;
    L_0x04c4:
        r12.setText(r8);
        goto L_0x0581;
    L_0x04c9:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingStatePlaceholder";
        r15 = NUM; // 0x7f0c04f1 float:1.8611758E38 double:1.0530980235E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x04df:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        if (r8 == 0) goto L_0x0581;
    L_0x04e7:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.shipping_address;
        r12 = r12.state;
        r8.setText(r12);
        goto L_0x0581;
    L_0x04f8:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingCityPlaceholder";
        r15 = NUM; // 0x7f0c04e7 float:1.8611737E38 double:1.0530980185E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x050e:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        if (r8 == 0) goto L_0x0581;
    L_0x0516:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.shipping_address;
        r12 = r12.city;
        r8.setText(r12);
        goto L_0x0581;
    L_0x0526:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingAddress2Placeholder";
        r15 = NUM; // 0x7f0c04e6 float:1.8611735E38 double:1.053098018E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x053c:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        if (r8 == 0) goto L_0x0581;
    L_0x0544:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.shipping_address;
        r12 = r12.street_line2;
        r8.setText(r12);
        goto L_0x0581;
    L_0x0554:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = "PaymentShippingAddress1Placeholder";
        r15 = NUM; // 0x7f0c04e5 float:1.8611733E38 double:1.0530980175E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r8.setHint(r12);
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        if (r8 == 0) goto L_0x0581;
    L_0x056a:
        r8 = r1.paymentForm;
        r8 = r8.saved_info;
        r8 = r8.shipping_address;
        if (r8 == 0) goto L_0x0581;
    L_0x0572:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.paymentForm;
        r12 = r12.saved_info;
        r12 = r12.shipping_address;
        r12 = r12.street_line1;
        r8.setText(r12);
    L_0x0581:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = r1.inputFields;
        r12 = r12[r4];
        r12 = r12.length();
        r8.setSelection(r12);
        r8 = 8;
        if (r4 != r8) goto L_0x060e;
    L_0x0594:
        r8 = new android.widget.TextView;
        r8.<init>(r2);
        r1.textView = r8;
        r8 = r1.textView;
        r12 = "+";
        r8.setText(r12);
        r8 = r1.textView;
        r12 = "windowBackgroundWhiteBlackText";
        r12 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r8.setTextColor(r12);
        r8 = r1.textView;
        r12 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8.setTextSize(r9, r12);
        r8 = r1.textView;
        r21 = -2;
        r22 = -2;
        r23 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r24 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r25 = 0;
        r26 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r21, r22, r23, r24, r25, r26);
        r6.addView(r8, r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r8.setPadding(r12, r14, r14, r14);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = 19;
        r8.setGravity(r12);
        r8 = new android.text.InputFilter[r9];
        r12 = new android.text.InputFilter$LengthFilter;
        r15 = 5;
        r12.<init>(r15);
        r8[r14] = r12;
        r12 = r1.inputFields;
        r12 = r12[r4];
        r12.setFilters(r8);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r21 = 55;
        r23 = 0;
        r25 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r21, r22, r23, r24, r25, r26);
        r6.addView(r8, r12);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$4;
        r8.<init>();
        r6.addTextChangedListener(r8);
        goto L_0x0678;
    L_0x060e:
        if (r4 != r10) goto L_0x0644;
    L_0x0610:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r8.setPadding(r14, r14, r14, r14);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = 19;
        r8.setGravity(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r21 = -1;
        r22 = -2;
        r23 = 0;
        r24 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r25 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r26 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r21, r22, r23, r24, r25, r26);
        r6.addView(r8, r12);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$5;
        r8.<init>();
        r6.addTextChangedListener(r8);
        goto L_0x0678;
    L_0x0644:
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r8.setPadding(r14, r14, r14, r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r12 = org.telegram.messenger.LocaleController.isRTL;
        if (r12 == 0) goto L_0x065b;
    L_0x0659:
        r12 = 5;
        goto L_0x065c;
    L_0x065b:
        r12 = r7;
    L_0x065c:
        r8.setGravity(r12);
        r8 = r1.inputFields;
        r8 = r8[r4];
        r21 = -1;
        r22 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r27 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r6.addView(r8, r12);
    L_0x0678:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$6;
        r8.<init>();
        r6.setOnEditorActionListener(r8);
        if (r4 != r10) goto L_0x07c5;
    L_0x0686:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.email_to_provider;
        if (r6 != 0) goto L_0x06af;
    L_0x068e:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.phone_to_provider;
        if (r6 == 0) goto L_0x0697;
    L_0x0696:
        goto L_0x06af;
    L_0x0697:
        r6 = r1.sectionCell;
        r8 = new org.telegram.ui.Cells.ShadowSectionCell;
        r8.<init>(r2);
        r6[r9] = r8;
        r6 = r1.linearLayout2;
        r8 = r1.sectionCell;
        r8 = r8[r9];
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
        goto L_0x075a;
    L_0x06af:
        r6 = r14;
        r8 = 0;
    L_0x06b1:
        r10 = r1.paymentForm;
        r10 = r10.users;
        r10 = r10.size();
        if (r6 >= r10) goto L_0x06d1;
    L_0x06bb:
        r10 = r1.paymentForm;
        r10 = r10.users;
        r10 = r10.get(r6);
        r10 = (org.telegram.tgnet.TLRPC.User) r10;
        r12 = r10.id;
        r15 = r1.paymentForm;
        r15 = r15.provider_id;
        if (r12 != r15) goto L_0x06ce;
    L_0x06cd:
        r8 = r10;
    L_0x06ce:
        r6 = r6 + 1;
        goto L_0x06b1;
    L_0x06d1:
        if (r8 == 0) goto L_0x06dc;
    L_0x06d3:
        r6 = r8.first_name;
        r8 = r8.last_name;
        r6 = org.telegram.messenger.ContactsController.formatName(r6, r8);
        goto L_0x06de;
    L_0x06dc:
        r6 = "";
    L_0x06de:
        r8 = r1.bottomCell;
        r10 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r10.<init>(r2);
        r8[r9] = r10;
        r8 = r1.bottomCell;
        r8 = r8[r9];
        r10 = "windowBackgroundGrayShadow";
        r12 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r12, r10);
        r8.setBackgroundDrawable(r10);
        r8 = r1.linearLayout2;
        r10 = r1.bottomCell;
        r10 = r10[r9];
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r8.addView(r10, r12);
        r8 = r1.paymentForm;
        r8 = r8.invoice;
        r8 = r8.email_to_provider;
        if (r8 == 0) goto L_0x0729;
    L_0x070c:
        r8 = r1.paymentForm;
        r8 = r8.invoice;
        r8 = r8.phone_to_provider;
        if (r8 == 0) goto L_0x0729;
    L_0x0714:
        r8 = r1.bottomCell;
        r8 = r8[r9];
        r10 = "PaymentPhoneEmailToProvider";
        r12 = NUM; // 0x7f0c04e0 float:1.8611723E38 double:1.053098015E-314;
        r15 = new java.lang.Object[r9];
        r15[r14] = r6;
        r6 = org.telegram.messenger.LocaleController.formatString(r10, r12, r15);
        r8.setText(r6);
        goto L_0x075a;
    L_0x0729:
        r8 = r1.paymentForm;
        r8 = r8.invoice;
        r8 = r8.email_to_provider;
        if (r8 == 0) goto L_0x0746;
    L_0x0731:
        r8 = r1.bottomCell;
        r8 = r8[r9];
        r10 = "PaymentEmailToProvider";
        r12 = NUM; // 0x7f0c04d4 float:1.8611699E38 double:1.053098009E-314;
        r15 = new java.lang.Object[r9];
        r15[r14] = r6;
        r6 = org.telegram.messenger.LocaleController.formatString(r10, r12, r15);
        r8.setText(r6);
        goto L_0x075a;
    L_0x0746:
        r8 = r1.bottomCell;
        r8 = r8[r9];
        r10 = "PaymentPhoneToProvider";
        r12 = NUM; // 0x7f0c04e1 float:1.8611725E38 double:1.0530980155E-314;
        r15 = new java.lang.Object[r9];
        r15[r14] = r6;
        r6 = org.telegram.messenger.LocaleController.formatString(r10, r12, r15);
        r8.setText(r6);
    L_0x075a:
        r6 = new org.telegram.ui.Cells.TextCheckCell;
        r6.<init>(r2);
        r1.checkCell1 = r6;
        r6 = r1.checkCell1;
        r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r6.setBackgroundDrawable(r8);
        r6 = r1.checkCell1;
        r8 = "PaymentShippingSave";
        r10 = NUM; // 0x7f0c04ef float:1.8611754E38 double:1.0530980225E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r10 = r1.saveShippingInfo;
        r6.setTextAndCheck(r8, r10, r14);
        r6 = r1.linearLayout2;
        r8 = r1.checkCell1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
        r6 = r1.checkCell1;
        r8 = new org.telegram.ui.PaymentFormActivity$7;
        r8.<init>();
        r6.setOnClickListener(r8);
        r6 = r1.bottomCell;
        r8 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r8.<init>(r2);
        r6[r14] = r8;
        r6 = r1.bottomCell;
        r6 = r6[r14];
        r8 = "windowBackgroundGrayShadow";
        r10 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r10, r8);
        r6.setBackgroundDrawable(r8);
        r6 = r1.bottomCell;
        r6 = r6[r14];
        r8 = "PaymentShippingSaveInfo";
        r10 = NUM; // 0x7f0c04f0 float:1.8611756E38 double:1.053098023E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setText(r8);
        r6 = r1.linearLayout2;
        r8 = r1.bottomCell;
        r8 = r8[r14];
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
    L_0x07c5:
        r4 = r4 + 1;
        r8 = 2;
        goto L_0x0226;
    L_0x07ca:
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.name_requested;
        if (r2 != 0) goto L_0x07e3;
    L_0x07d2:
        r2 = r1.inputFields;
        r4 = 6;
        r2 = r2[r4];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r4 = 8;
        r2.setVisibility(r4);
        goto L_0x07e5;
    L_0x07e3:
        r4 = 8;
    L_0x07e5:
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.phone_requested;
        if (r2 != 0) goto L_0x07fa;
    L_0x07ed:
        r2 = r1.inputFields;
        r2 = r2[r4];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
    L_0x07fa:
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.email_requested;
        if (r2 != 0) goto L_0x0812;
    L_0x0802:
        r2 = r1.inputFields;
        r4 = 7;
        r2 = r2[r4];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r4 = 8;
        r2.setVisibility(r4);
    L_0x0812:
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.phone_requested;
        if (r2 == 0) goto L_0x0825;
    L_0x081a:
        r2 = r1.inputFields;
        r2 = r2[r10];
        r4 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r2.setImeOptions(r4);
        goto L_0x0852;
    L_0x0825:
        r4 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.email_requested;
        if (r2 == 0) goto L_0x0839;
    L_0x0830:
        r2 = r1.inputFields;
        r5 = 7;
        r2 = r2[r5];
        r2.setImeOptions(r4);
        goto L_0x0852;
    L_0x0839:
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.name_requested;
        if (r2 == 0) goto L_0x084a;
    L_0x0841:
        r2 = r1.inputFields;
        r5 = 6;
        r2 = r2[r5];
        r2.setImeOptions(r4);
        goto L_0x0852;
    L_0x084a:
        r2 = r1.inputFields;
        r5 = 5;
        r2 = r2[r5];
        r2.setImeOptions(r4);
    L_0x0852:
        r2 = r1.sectionCell;
        r2 = r2[r9];
        if (r2 == 0) goto L_0x087d;
    L_0x0858:
        r2 = r1.sectionCell;
        r2 = r2[r9];
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0878;
    L_0x0864:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0878;
    L_0x086c:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0875;
    L_0x0874:
        goto L_0x0878;
    L_0x0875:
        r4 = 8;
        goto L_0x0879;
    L_0x0878:
        r4 = r14;
    L_0x0879:
        r2.setVisibility(r4);
        goto L_0x08a7;
    L_0x087d:
        r2 = r1.bottomCell;
        r2 = r2[r9];
        if (r2 == 0) goto L_0x08a7;
    L_0x0883:
        r2 = r1.bottomCell;
        r2 = r2[r9];
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x08a3;
    L_0x088f:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x08a3;
    L_0x0897:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x08a0;
    L_0x089f:
        goto L_0x08a3;
    L_0x08a0:
        r4 = 8;
        goto L_0x08a4;
    L_0x08a3:
        r4 = r14;
    L_0x08a4:
        r2.setVisibility(r4);
    L_0x08a7:
        r2 = r1.headerCell;
        r2 = r2[r9];
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x08c7;
    L_0x08b3:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x08c7;
    L_0x08bb:
        r4 = r1.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x08c4;
    L_0x08c3:
        goto L_0x08c7;
    L_0x08c4:
        r4 = 8;
        goto L_0x08c8;
    L_0x08c7:
        r4 = r14;
    L_0x08c8:
        r2.setVisibility(r4);
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.shipping_address_requested;
        if (r2 != 0) goto L_0x0934;
    L_0x08d3:
        r2 = r1.headerCell;
        r2 = r2[r14];
        r4 = 8;
        r2.setVisibility(r4);
        r2 = r1.sectionCell;
        r2 = r2[r14];
        r2.setVisibility(r4);
        r2 = r1.inputFields;
        r2 = r2[r14];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
        r2 = r1.inputFields;
        r2 = r2[r9];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
        r2 = r1.inputFields;
        r5 = 2;
        r2 = r2[r5];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
        r2 = r1.inputFields;
        r2 = r2[r7];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
        r2 = r1.inputFields;
        r5 = 4;
        r2 = r2[r5];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
        r2 = r1.inputFields;
        r5 = 5;
        r2 = r2[r5];
        r2 = r2.getParent();
        r2 = (android.view.ViewGroup) r2;
        r2.setVisibility(r4);
    L_0x0934:
        r2 = r1.paymentForm;
        r2 = r2.saved_info;
        if (r2 == 0) goto L_0x0950;
    L_0x093a:
        r2 = r1.paymentForm;
        r2 = r2.saved_info;
        r2 = r2.phone;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0950;
    L_0x0946:
        r2 = r1.paymentForm;
        r2 = r2.saved_info;
        r2 = r2.phone;
        r1.fillNumber(r2);
        goto L_0x0954;
    L_0x0950:
        r2 = 0;
        r1.fillNumber(r2);
    L_0x0954:
        r2 = r1.inputFields;
        r4 = 8;
        r2 = r2[r4];
        r2 = r2.length();
        if (r2 != 0) goto L_0x18a7;
    L_0x0960:
        r2 = r1.paymentForm;
        r2 = r2.invoice;
        r2 = r2.phone_requested;
        if (r2 == 0) goto L_0x18a7;
    L_0x0968:
        r2 = r1.paymentForm;
        r2 = r2.saved_info;
        if (r2 == 0) goto L_0x097a;
    L_0x096e:
        r2 = r1.paymentForm;
        r2 = r2.saved_info;
        r2 = r2.phone;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x18a7;
    L_0x097a:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0990 }
        r4 = "phone";	 Catch:{ Exception -> 0x0990 }
        r2 = r2.getSystemService(r4);	 Catch:{ Exception -> 0x0990 }
        r2 = (android.telephony.TelephonyManager) r2;	 Catch:{ Exception -> 0x0990 }
        if (r2 == 0) goto L_0x0995;	 Catch:{ Exception -> 0x0990 }
    L_0x0986:
        r2 = r2.getSimCountryIso();	 Catch:{ Exception -> 0x0990 }
        r2 = r2.toUpperCase();	 Catch:{ Exception -> 0x0990 }
        r15 = r2;
        goto L_0x0996;
    L_0x0990:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
    L_0x0995:
        r15 = 0;
    L_0x0996:
        if (r15 == 0) goto L_0x18a7;
    L_0x0998:
        r2 = r3.get(r15);
        r2 = (java.lang.String) r2;
        if (r2 == 0) goto L_0x18a7;
    L_0x09a0:
        r3 = r1.countriesArray;
        r3 = r3.indexOf(r2);
        if (r3 == r11) goto L_0x18a7;
    L_0x09a8:
        r3 = r1.inputFields;
        r4 = 8;
        r3 = r3[r4];
        r4 = r1.countriesMap;
        r2 = r4.get(r2);
        r2 = (java.lang.CharSequence) r2;
        r3.setText(r2);
        goto L_0x18a7;
    L_0x09bb:
        r4 = r1.currentStep;
        r5 = 2;
        if (r4 != r5) goto L_0x0ee7;
    L_0x09c0:
        r3 = r1.paymentForm;
        r3 = r3.native_params;
        if (r3 == 0) goto L_0x0a02;
    L_0x09c6:
        r3 = new org.json.JSONObject;	 Catch:{ Exception -> 0x09fd }
        r4 = r1.paymentForm;	 Catch:{ Exception -> 0x09fd }
        r4 = r4.native_params;	 Catch:{ Exception -> 0x09fd }
        r4 = r4.data;	 Catch:{ Exception -> 0x09fd }
        r3.<init>(r4);	 Catch:{ Exception -> 0x09fd }
        r4 = "android_pay_public_key";	 Catch:{ Exception -> 0x09e0 }
        r4 = r3.getString(r4);	 Catch:{ Exception -> 0x09e0 }
        r5 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x09e0 }
        if (r5 != 0) goto L_0x09e3;	 Catch:{ Exception -> 0x09e0 }
    L_0x09dd:
        r1.androidPayPublicKey = r4;	 Catch:{ Exception -> 0x09e0 }
        goto L_0x09e3;
    L_0x09e0:
        r4 = 0;
        r1.androidPayPublicKey = r4;	 Catch:{ Exception -> 0x09fd }
    L_0x09e3:
        r4 = "android_pay_bgcolor";	 Catch:{ Exception -> 0x09ef }
        r4 = r3.getInt(r4);	 Catch:{ Exception -> 0x09ef }
        r5 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;	 Catch:{ Exception -> 0x09ef }
        r4 = r4 | r5;	 Catch:{ Exception -> 0x09ef }
        r1.androidPayBackgroundColor = r4;	 Catch:{ Exception -> 0x09ef }
        goto L_0x09f1;
    L_0x09ef:
        r1.androidPayBackgroundColor = r11;	 Catch:{ Exception -> 0x09fd }
    L_0x09f1:
        r4 = "android_pay_inverse";	 Catch:{ Exception -> 0x09fa }
        r3 = r3.getBoolean(r4);	 Catch:{ Exception -> 0x09fa }
        r1.androidPayBlackTheme = r3;	 Catch:{ Exception -> 0x09fa }
        goto L_0x0a02;
    L_0x09fa:
        r1.androidPayBlackTheme = r14;	 Catch:{ Exception -> 0x09fd }
        goto L_0x0a02;
    L_0x09fd:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.m3e(r3);
    L_0x0a02:
        r3 = r1.isWebView;
        if (r3 == 0) goto L_0x0b1d;
    L_0x0a06:
        r3 = r1.androidPayPublicKey;
        if (r3 == 0) goto L_0x0a0d;
    L_0x0a0a:
        r29.initAndroidPay(r30);
    L_0x0a0d:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r2);
        r1.androidPayContainer = r3;
        r3 = r1.androidPayContainer;
        r4 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r3.setId(r4);
        r3 = r1.androidPayContainer;
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r3.setBackgroundDrawable(r4);
        r3 = r1.androidPayContainer;
        r4 = 8;
        r3.setVisibility(r4);
        r3 = r1.linearLayout2;
        r4 = r1.androidPayContainer;
        r5 = 48;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r5);
        r3.addView(r4, r5);
        r1.webviewLoading = r9;
        r1.showEditDoneProgress(r9, r9);
        r3 = r1.progressView;
        r3.setVisibility(r14);
        r3 = r1.doneItem;
        r3.setEnabled(r14);
        r3 = r1.doneItem;
        r3 = r3.getImageView();
        r4 = 4;
        r3.setVisibility(r4);
        r3 = new org.telegram.ui.PaymentFormActivity$8;
        r3.<init>(r2);
        r1.webView = r3;
        r3 = r1.webView;
        r3 = r3.getSettings();
        r3.setJavaScriptEnabled(r9);
        r3 = r1.webView;
        r3 = r3.getSettings();
        r3.setDomStorageEnabled(r9);
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r3 < r4) goto L_0x0a82;
    L_0x0a70:
        r3 = r1.webView;
        r3 = r3.getSettings();
        r3.setMixedContentMode(r14);
        r3 = android.webkit.CookieManager.getInstance();
        r4 = r1.webView;
        r3.setAcceptThirdPartyCookies(r4, r9);
    L_0x0a82:
        r3 = r1.webView;
        r4 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy;
        r5 = 0;
        r4.<init>();
        r5 = "TelegramWebviewProxy";
        r3.addJavascriptInterface(r4, r5);
        r3 = r1.webView;
        r4 = new org.telegram.ui.PaymentFormActivity$9;
        r4.<init>();
        r3.setWebViewClient(r4);
        r3 = r1.linearLayout2;
        r4 = r1.webView;
        r5 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r5);
        r3.addView(r4, r5);
        r3 = r1.sectionCell;
        r4 = new org.telegram.ui.Cells.ShadowSectionCell;
        r4.<init>(r2);
        r5 = 2;
        r3[r5] = r4;
        r3 = r1.linearLayout2;
        r4 = r1.sectionCell;
        r4 = r4[r5];
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r3.addView(r4, r5);
        r3 = new org.telegram.ui.Cells.TextCheckCell;
        r3.<init>(r2);
        r1.checkCell1 = r3;
        r3 = r1.checkCell1;
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r3.setBackgroundDrawable(r4);
        r3 = r1.checkCell1;
        r4 = "PaymentCardSavePaymentInformation";
        r5 = NUM; // 0x7f0c04c5 float:1.8611668E38 double:1.0530980017E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r5 = r1.saveCardInfo;
        r3.setTextAndCheck(r4, r5, r14);
        r3 = r1.linearLayout2;
        r4 = r1.checkCell1;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r3.addView(r4, r5);
        r3 = r1.checkCell1;
        r4 = new org.telegram.ui.PaymentFormActivity$10;
        r4.<init>();
        r3.setOnClickListener(r4);
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r14] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r14];
        r4 = "windowBackgroundGrayShadow";
        r5 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r5, r4);
        r3.setBackgroundDrawable(r2);
        r29.updateSavePaymentField();
        r2 = r1.linearLayout2;
        r3 = r1.bottomCell;
        r3 = r3[r14];
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r2.addView(r3, r4);
        goto L_0x18a7;
    L_0x0b1d:
        r3 = r1.paymentForm;
        r3 = r3.native_params;
        if (r3 == 0) goto L_0x0b62;
    L_0x0b23:
        r3 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0b5d }
        r4 = r1.paymentForm;	 Catch:{ Exception -> 0x0b5d }
        r4 = r4.native_params;	 Catch:{ Exception -> 0x0b5d }
        r4 = r4.data;	 Catch:{ Exception -> 0x0b5d }
        r3.<init>(r4);	 Catch:{ Exception -> 0x0b5d }
        r4 = "need_country";	 Catch:{ Exception -> 0x0b37 }
        r4 = r3.getBoolean(r4);	 Catch:{ Exception -> 0x0b37 }
        r1.need_card_country = r4;	 Catch:{ Exception -> 0x0b37 }
        goto L_0x0b39;
    L_0x0b37:
        r1.need_card_country = r14;	 Catch:{ Exception -> 0x0b5d }
    L_0x0b39:
        r4 = "need_zip";	 Catch:{ Exception -> 0x0b42 }
        r4 = r3.getBoolean(r4);	 Catch:{ Exception -> 0x0b42 }
        r1.need_card_postcode = r4;	 Catch:{ Exception -> 0x0b42 }
        goto L_0x0b44;
    L_0x0b42:
        r1.need_card_postcode = r14;	 Catch:{ Exception -> 0x0b5d }
    L_0x0b44:
        r4 = "need_cardholder_name";	 Catch:{ Exception -> 0x0b4d }
        r4 = r3.getBoolean(r4);	 Catch:{ Exception -> 0x0b4d }
        r1.need_card_name = r4;	 Catch:{ Exception -> 0x0b4d }
        goto L_0x0b4f;
    L_0x0b4d:
        r1.need_card_name = r14;	 Catch:{ Exception -> 0x0b5d }
    L_0x0b4f:
        r4 = "publishable_key";	 Catch:{ Exception -> 0x0b58 }
        r3 = r3.getString(r4);	 Catch:{ Exception -> 0x0b58 }
        r1.stripeApiKey = r3;	 Catch:{ Exception -> 0x0b58 }
        goto L_0x0b62;
    L_0x0b58:
        r3 = "";	 Catch:{ Exception -> 0x0b5d }
        r1.stripeApiKey = r3;	 Catch:{ Exception -> 0x0b5d }
        goto L_0x0b62;
    L_0x0b5d:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.m3e(r3);
    L_0x0b62:
        r29.initAndroidPay(r30);
        r3 = 6;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r3];
        r1.inputFields = r4;
        r4 = r14;
    L_0x0b6b:
        if (r4 >= r3) goto L_0x0eb2;
    L_0x0b6d:
        if (r4 != 0) goto L_0x0ba3;
    L_0x0b6f:
        r3 = r1.headerCell;
        r5 = new org.telegram.ui.Cells.HeaderCell;
        r5.<init>(r2);
        r3[r14] = r5;
        r3 = r1.headerCell;
        r3 = r3[r14];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setBackgroundColor(r5);
        r3 = r1.headerCell;
        r3 = r3[r14];
        r5 = "PaymentCardTitle";
        r6 = NUM; // 0x7f0c04c8 float:1.8611675E38 double:1.053098003E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r3.setText(r5);
        r3 = r1.linearLayout2;
        r5 = r1.headerCell;
        r5 = r5[r14];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r3.addView(r5, r6);
        goto L_0x0bd9;
    L_0x0ba3:
        r3 = 4;
        if (r4 != r3) goto L_0x0bd9;
    L_0x0ba6:
        r3 = r1.headerCell;
        r5 = new org.telegram.ui.Cells.HeaderCell;
        r5.<init>(r2);
        r3[r9] = r5;
        r3 = r1.headerCell;
        r3 = r3[r9];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setBackgroundColor(r5);
        r3 = r1.headerCell;
        r3 = r3[r9];
        r5 = "PaymentBillingAddress";
        r6 = NUM; // 0x7f0c04bf float:1.8611656E38 double:1.0530979987E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r3.setText(r5);
        r3 = r1.linearLayout2;
        r5 = r1.headerCell;
        r5 = r5[r9];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r3.addView(r5, r6);
    L_0x0bd9:
        if (r4 == r7) goto L_0x0be7;
    L_0x0bdb:
        r3 = 5;
        if (r4 == r3) goto L_0x0be7;
    L_0x0bde:
        r3 = 4;
        if (r4 != r3) goto L_0x0be5;
    L_0x0be1:
        r3 = r1.need_card_postcode;
        if (r3 == 0) goto L_0x0be7;
    L_0x0be5:
        r3 = r9;
        goto L_0x0be8;
    L_0x0be7:
        r3 = r14;
    L_0x0be8:
        r5 = new android.widget.FrameLayout;
        r5.<init>(r2);
        r6 = r1.linearLayout2;
        r8 = 48;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r8);
        r6.addView(r5, r10);
        r6 = "windowBackgroundWhite";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setBackgroundColor(r6);
        r6 = r1.inputFields;
        r8 = new org.telegram.ui.Components.EditTextBoldCursor;
        r8.<init>(r2);
        r6[r4] = r8;
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = java.lang.Integer.valueOf(r4);
        r6.setTag(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6.setTextSize(r9, r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "windowBackgroundWhiteHintText";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r6.setHintTextColor(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "windowBackgroundWhiteBlackText";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r6.setTextColor(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 0;
        r6.setBackgroundDrawable(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "windowBackgroundWhiteBlackText";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r6.setCursorColor(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.setCursorSize(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r6.setCursorWidth(r8);
        if (r4 != r7) goto L_0x0c93;
    L_0x0c65:
        r6 = new android.text.InputFilter[r9];
        r8 = new android.text.InputFilter$LengthFilter;
        r8.<init>(r7);
        r6[r14] = r8;
        r8 = r1.inputFields;
        r8 = r8[r4];
        r8.setFilters(r6);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        r6.setInputType(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = android.graphics.Typeface.DEFAULT;
        r6.setTypeface(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = android.text.method.PasswordTransformationMethod.getInstance();
        r6.setTransformationMethod(r8);
        goto L_0x0cd7;
    L_0x0c93:
        if (r4 != 0) goto L_0x0c9e;
    L_0x0c95:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 2;
        r6.setInputType(r8);
        goto L_0x0cd7;
    L_0x0c9e:
        r6 = 4;
        if (r4 != r6) goto L_0x0cb5;
    L_0x0ca1:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$11;
        r8.<init>();
        r6.setOnTouchListener(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r6.setInputType(r14);
        goto L_0x0cd7;
    L_0x0cb5:
        if (r4 != r9) goto L_0x0cc1;
    L_0x0cb7:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 16386; // 0x4002 float:2.2962E-41 double:8.096E-320;
        r6.setInputType(r8);
        goto L_0x0cd7;
    L_0x0cc1:
        r6 = 2;
        if (r4 != r6) goto L_0x0cce;
    L_0x0cc4:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 4097; // 0x1001 float:5.741E-42 double:2.024E-320;
        r6.setInputType(r8);
        goto L_0x0cd7;
    L_0x0cce:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r6.setInputType(r8);
    L_0x0cd7:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = 268435461; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r6.setImeOptions(r8);
        switch(r4) {
            case 0: goto L_0x0d3a;
            case 1: goto L_0x0d29;
            case 2: goto L_0x0d18;
            case 3: goto L_0x0d07;
            case 4: goto L_0x0cf6;
            case 5: goto L_0x0ce5;
            default: goto L_0x0ce4;
        };
    L_0x0ce4:
        goto L_0x0d4a;
    L_0x0ce5:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "PaymentShippingZipPlaceholder";
        r10 = NUM; // 0x7f0c04f2 float:1.861176E38 double:1.053098024E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setHint(r8);
        goto L_0x0d4a;
    L_0x0cf6:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "PaymentShippingCountry";
        r10 = NUM; // 0x7f0c04e8 float:1.861174E38 double:1.053098019E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setHint(r8);
        goto L_0x0d4a;
    L_0x0d07:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "PaymentCardCvv";
        r10 = NUM; // 0x7f0c04c0 float:1.8611658E38 double:1.053097999E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setHint(r8);
        goto L_0x0d4a;
    L_0x0d18:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "PaymentCardName";
        r10 = NUM; // 0x7f0c04c3 float:1.8611664E38 double:1.0530980007E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setHint(r8);
        goto L_0x0d4a;
    L_0x0d29:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "PaymentCardExpireDate";
        r10 = NUM; // 0x7f0c04c1 float:1.861166E38 double:1.0530979997E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setHint(r8);
        goto L_0x0d4a;
    L_0x0d3a:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = "PaymentCardNumber";
        r10 = NUM; // 0x7f0c04c4 float:1.8611666E38 double:1.053098001E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r6.setHint(r8);
    L_0x0d4a:
        if (r4 != 0) goto L_0x0d59;
    L_0x0d4c:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$12;
        r8.<init>();
        r6.addTextChangedListener(r8);
        goto L_0x0d67;
    L_0x0d59:
        if (r4 != r9) goto L_0x0d67;
    L_0x0d5b:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$13;
        r8.<init>();
        r6.addTextChangedListener(r8);
    L_0x0d67:
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.setPadding(r14, r14, r14, r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x0d7e;
    L_0x0d7c:
        r8 = 5;
        goto L_0x0d7f;
    L_0x0d7e:
        r8 = r7;
    L_0x0d7f:
        r6.setGravity(r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r21 = -1;
        r22 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r27 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r5.addView(r6, r8);
        r6 = r1.inputFields;
        r6 = r6[r4];
        r8 = new org.telegram.ui.PaymentFormActivity$14;
        r8.<init>();
        r6.setOnEditorActionListener(r8);
        if (r4 != r7) goto L_0x0dc1;
    L_0x0da9:
        r6 = r1.sectionCell;
        r8 = new org.telegram.ui.Cells.ShadowSectionCell;
        r8.<init>(r2);
        r6[r14] = r8;
        r6 = r1.linearLayout2;
        r8 = r1.sectionCell;
        r8 = r8[r14];
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
        goto L_0x0e71;
    L_0x0dc1:
        r6 = 5;
        if (r4 != r6) goto L_0x0e3a;
    L_0x0dc4:
        r6 = r1.sectionCell;
        r8 = new org.telegram.ui.Cells.ShadowSectionCell;
        r8.<init>(r2);
        r10 = 2;
        r6[r10] = r8;
        r6 = r1.linearLayout2;
        r8 = r1.sectionCell;
        r8 = r8[r10];
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
        r6 = new org.telegram.ui.Cells.TextCheckCell;
        r6.<init>(r2);
        r1.checkCell1 = r6;
        r6 = r1.checkCell1;
        r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r6.setBackgroundDrawable(r8);
        r6 = r1.checkCell1;
        r8 = "PaymentCardSavePaymentInformation";
        r10 = NUM; // 0x7f0c04c5 float:1.8611668E38 double:1.0530980017E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r10 = r1.saveCardInfo;
        r6.setTextAndCheck(r8, r10, r14);
        r6 = r1.linearLayout2;
        r8 = r1.checkCell1;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
        r6 = r1.checkCell1;
        r8 = new org.telegram.ui.PaymentFormActivity$15;
        r8.<init>();
        r6.setOnClickListener(r8);
        r6 = r1.bottomCell;
        r8 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r8.<init>(r2);
        r6[r14] = r8;
        r6 = r1.bottomCell;
        r6 = r6[r14];
        r8 = "windowBackgroundGrayShadow";
        r10 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r10, r8);
        r6.setBackgroundDrawable(r8);
        r29.updateSavePaymentField();
        r6 = r1.linearLayout2;
        r8 = r1.bottomCell;
        r8 = r8[r14];
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r6.addView(r8, r10);
        goto L_0x0e71;
    L_0x0e3a:
        if (r4 != 0) goto L_0x0e71;
    L_0x0e3c:
        r6 = new android.widget.FrameLayout;
        r6.<init>(r2);
        r1.androidPayContainer = r6;
        r6 = r1.androidPayContainer;
        r8 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r6.setId(r8);
        r6 = r1.androidPayContainer;
        r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r6.setBackgroundDrawable(r8);
        r6 = r1.androidPayContainer;
        r8 = 8;
        r6.setVisibility(r8);
        r6 = r1.androidPayContainer;
        r21 = -2;
        r22 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r23 = 21;
        r24 = 0;
        r25 = 0;
        r26 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r27 = 0;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r5.addView(r6, r8);
    L_0x0e71:
        if (r3 == 0) goto L_0x0e90;
    L_0x0e73:
        r3 = new android.view.View;
        r3.<init>(r2);
        r6 = r1.dividers;
        r6.add(r3);
        r6 = "divider";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r3.setBackgroundColor(r6);
        r6 = new android.widget.FrameLayout$LayoutParams;
        r8 = 83;
        r6.<init>(r11, r9, r8);
        r5.addView(r3, r6);
    L_0x0e90:
        r3 = 4;
        if (r4 != r3) goto L_0x0e9b;
    L_0x0e93:
        r3 = r1.need_card_country;
        if (r3 == 0) goto L_0x0e98;
    L_0x0e97:
        goto L_0x0e9b;
    L_0x0e98:
        r3 = 8;
        goto L_0x0eaa;
    L_0x0e9b:
        r3 = 5;
        if (r4 != r3) goto L_0x0ea2;
    L_0x0e9e:
        r3 = r1.need_card_postcode;
        if (r3 == 0) goto L_0x0e98;
    L_0x0ea2:
        r3 = 2;
        if (r4 != r3) goto L_0x0ead;
    L_0x0ea5:
        r3 = r1.need_card_name;
        if (r3 != 0) goto L_0x0ead;
    L_0x0ea9:
        goto L_0x0e98;
    L_0x0eaa:
        r5.setVisibility(r3);
    L_0x0ead:
        r4 = r4 + 1;
        r3 = 6;
        goto L_0x0b6b;
    L_0x0eb2:
        r2 = r1.need_card_country;
        if (r2 != 0) goto L_0x0eca;
    L_0x0eb6:
        r2 = r1.need_card_postcode;
        if (r2 != 0) goto L_0x0eca;
    L_0x0eba:
        r2 = r1.headerCell;
        r2 = r2[r9];
        r3 = 8;
        r2.setVisibility(r3);
        r2 = r1.sectionCell;
        r2 = r2[r14];
        r2.setVisibility(r3);
    L_0x0eca:
        r2 = r1.need_card_postcode;
        if (r2 == 0) goto L_0x0edb;
    L_0x0ece:
        r2 = r1.inputFields;
        r3 = 5;
        r2 = r2[r3];
        r3 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r2.setImeOptions(r3);
        goto L_0x18a7;
    L_0x0edb:
        r3 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r2 = r1.inputFields;
        r2 = r2[r7];
        r2.setImeOptions(r3);
        goto L_0x18a7;
    L_0x0ee7:
        r4 = r1.currentStep;
        if (r4 != r9) goto L_0x0f8b;
    L_0x0eeb:
        r3 = r1.requestedInfo;
        r3 = r3.shipping_options;
        r3 = r3.size();
        r4 = new org.telegram.ui.Cells.RadioCell[r3];
        r1.radioCells = r4;
        r4 = r14;
    L_0x0ef8:
        if (r4 >= r3) goto L_0x0f63;
    L_0x0efa:
        r5 = r1.requestedInfo;
        r5 = r5.shipping_options;
        r5 = r5.get(r4);
        r5 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r5;
        r6 = r1.radioCells;
        r7 = new org.telegram.ui.Cells.RadioCell;
        r7.<init>(r2);
        r6[r4] = r7;
        r6 = r1.radioCells;
        r6 = r6[r4];
        r7 = java.lang.Integer.valueOf(r4);
        r6.setTag(r7);
        r6 = r1.radioCells;
        r6 = r6[r4];
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r6.setBackgroundDrawable(r7);
        r6 = r1.radioCells;
        r6 = r6[r4];
        r7 = "%s - %s";
        r8 = 2;
        r10 = new java.lang.Object[r8];
        r8 = r5.prices;
        r8 = r1.getTotalPriceString(r8);
        r10[r14] = r8;
        r5 = r5.title;
        r10[r9] = r5;
        r5 = java.lang.String.format(r7, r10);
        if (r4 != 0) goto L_0x0f40;
    L_0x0f3e:
        r7 = r9;
        goto L_0x0f41;
    L_0x0f40:
        r7 = r14;
    L_0x0f41:
        r8 = r3 + -1;
        if (r4 == r8) goto L_0x0f47;
    L_0x0f45:
        r8 = r9;
        goto L_0x0f48;
    L_0x0f47:
        r8 = r14;
    L_0x0f48:
        r6.setText(r5, r7, r8);
        r5 = r1.radioCells;
        r5 = r5[r4];
        r6 = new org.telegram.ui.PaymentFormActivity$16;
        r6.<init>();
        r5.setOnClickListener(r6);
        r5 = r1.linearLayout2;
        r6 = r1.radioCells;
        r6 = r6[r4];
        r5.addView(r6);
        r4 = r4 + 1;
        goto L_0x0ef8;
    L_0x0f63:
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r3[r14] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r14];
        r4 = "windowBackgroundGrayShadow";
        r5 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r5, r4);
        r3.setBackgroundDrawable(r2);
        r2 = r1.linearLayout2;
        r3 = r1.bottomCell;
        r3 = r3[r14];
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r2.addView(r3, r4);
        goto L_0x18a7;
    L_0x0f8b:
        r4 = r1.currentStep;
        if (r4 != r7) goto L_0x11c9;
    L_0x0f8f:
        r4 = 2;
        r3 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r1.inputFields = r3;
        r3 = r14;
    L_0x0f95:
        if (r3 >= r4) goto L_0x18a7;
    L_0x0f97:
        if (r3 != 0) goto L_0x0fcc;
    L_0x0f99:
        r4 = r1.headerCell;
        r5 = new org.telegram.ui.Cells.HeaderCell;
        r5.<init>(r2);
        r4[r14] = r5;
        r4 = r1.headerCell;
        r4 = r4[r14];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = r1.headerCell;
        r4 = r4[r14];
        r5 = "PaymentCardTitle";
        r6 = NUM; // 0x7f0c04c8 float:1.8611675E38 double:1.053098003E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r4 = r1.linearLayout2;
        r5 = r1.headerCell;
        r5 = r5[r14];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
    L_0x0fcc:
        r4 = new android.widget.FrameLayout;
        r4.<init>(r2);
        r5 = r1.linearLayout2;
        r6 = 48;
        r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r6);
        r5.addView(r4, r8);
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        if (r3 == r9) goto L_0x0fe9;
    L_0x0fe7:
        r5 = r9;
        goto L_0x0fea;
    L_0x0fe9:
        r5 = r14;
    L_0x0fea:
        if (r5 == 0) goto L_0x100d;
    L_0x0fec:
        r6 = 7;
        if (r3 != r6) goto L_0x0ff9;
    L_0x0fef:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.phone_requested;
        if (r6 != 0) goto L_0x0ff9;
    L_0x0ff7:
        r5 = r14;
        goto L_0x100d;
    L_0x0ff9:
        r6 = 6;
        if (r3 != r6) goto L_0x100d;
    L_0x0ffc:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.phone_requested;
        if (r6 != 0) goto L_0x100d;
    L_0x1004:
        r6 = r1.paymentForm;
        r6 = r6.invoice;
        r6 = r6.email_requested;
        if (r6 != 0) goto L_0x100d;
    L_0x100c:
        goto L_0x0ff7;
    L_0x100d:
        if (r5 == 0) goto L_0x102c;
    L_0x100f:
        r5 = new android.view.View;
        r5.<init>(r2);
        r6 = r1.dividers;
        r6.add(r5);
        r6 = "divider";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setBackgroundColor(r6);
        r6 = new android.widget.FrameLayout$LayoutParams;
        r8 = 83;
        r6.<init>(r11, r9, r8);
        r4.addView(r5, r6);
    L_0x102c:
        r5 = r1.inputFields;
        r6 = new org.telegram.ui.Components.EditTextBoldCursor;
        r6.<init>(r2);
        r5[r3] = r6;
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = java.lang.Integer.valueOf(r3);
        r5.setTag(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5.setTextSize(r9, r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "windowBackgroundWhiteHintText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setHintTextColor(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "windowBackgroundWhiteBlackText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setTextColor(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = 0;
        r5.setBackgroundDrawable(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "windowBackgroundWhiteBlackText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setCursorColor(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5.setCursorSize(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r5.setCursorWidth(r6);
        if (r3 != 0) goto L_0x10a4;
    L_0x1090:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = new org.telegram.ui.PaymentFormActivity$17;
        r6.<init>();
        r5.setOnTouchListener(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r5.setInputType(r14);
        goto L_0x10b6;
    L_0x10a4:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r5.setInputType(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = android.graphics.Typeface.DEFAULT;
        r5.setTypeface(r6);
    L_0x10b6:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r5.setImeOptions(r6);
        switch(r3) {
            case 0: goto L_0x10dc;
            case 1: goto L_0x10c4;
            default: goto L_0x10c3;
        };
    L_0x10c3:
        goto L_0x10e9;
    L_0x10c4:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "LoginPassword";
        r8 = NUM; // 0x7f0c0391 float:1.8611044E38 double:1.0530978495E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r8);
        r5.setHint(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r5.requestFocus();
        goto L_0x10e9;
    L_0x10dc:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = r1.paymentForm;
        r6 = r6.saved_credentials;
        r6 = r6.title;
        r5.setText(r6);
    L_0x10e9:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5.setPadding(r14, r14, r14, r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x1100;
    L_0x10fe:
        r6 = 5;
        goto L_0x1101;
    L_0x1100:
        r6 = r7;
    L_0x1101:
        r5.setGravity(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r21 = -1;
        r22 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r27 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r4.addView(r5, r6);
        r4 = r1.inputFields;
        r4 = r4[r3];
        r5 = new org.telegram.ui.PaymentFormActivity$18;
        r5.<init>();
        r4.setOnEditorActionListener(r5);
        if (r3 != r9) goto L_0x11c4;
    L_0x112b:
        r4 = r1.bottomCell;
        r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r5.<init>(r2);
        r4[r14] = r5;
        r4 = r1.bottomCell;
        r4 = r4[r14];
        r5 = "PaymentConfirmationMessage";
        r6 = NUM; // 0x7f0c04d1 float:1.8611693E38 double:1.0530980076E-314;
        r8 = new java.lang.Object[r9];
        r10 = r1.paymentForm;
        r10 = r10.saved_credentials;
        r10 = r10.title;
        r8[r14] = r10;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r8);
        r4.setText(r5);
        r4 = r1.bottomCell;
        r4 = r4[r14];
        r5 = NUM; // 0x7f070093 float:1.7944876E38 double:1.0529355757E-314;
        r6 = "windowBackgroundGrayShadow";
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r5, r6);
        r4.setBackgroundDrawable(r5);
        r4 = r1.linearLayout2;
        r5 = r1.bottomCell;
        r5 = r5[r14];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextSettingsCell;
        r4.<init>(r2);
        r1.settingsCell1 = r4;
        r4 = r1.settingsCell1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r4.setBackgroundDrawable(r5);
        r4 = r1.settingsCell1;
        r5 = "PaymentConfirmationNewCard";
        r6 = NUM; // 0x7f0c04d2 float:1.8611695E38 double:1.053098008E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5, r14);
        r4 = r1.linearLayout2;
        r5 = r1.settingsCell1;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
        r4 = r1.settingsCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$19;
        r5.<init>();
        r4.setOnClickListener(r5);
        r4 = r1.bottomCell;
        r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r5.<init>(r2);
        r4[r9] = r5;
        r4 = r1.bottomCell;
        r4 = r4[r9];
        r5 = "windowBackgroundGrayShadow";
        r6 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r6, r5);
        r4.setBackgroundDrawable(r5);
        r4 = r1.linearLayout2;
        r5 = r1.bottomCell;
        r5 = r5[r9];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
    L_0x11c4:
        r3 = r3 + 1;
        r4 = 2;
        goto L_0x0f95;
    L_0x11c9:
        r4 = r1.currentStep;
        r5 = 4;
        if (r4 == r5) goto L_0x147a;
    L_0x11ce:
        r4 = r1.currentStep;
        r5 = 5;
        if (r4 != r5) goto L_0x11d5;
    L_0x11d3:
        goto L_0x147a;
    L_0x11d5:
        r3 = r1.currentStep;
        r4 = 6;
        if (r3 != r4) goto L_0x18a7;
    L_0x11da:
        r3 = r1.bottomCell;
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r4.<init>(r2);
        r5 = 2;
        r3[r5] = r4;
        r3 = r1.bottomCell;
        r3 = r3[r5];
        r4 = NUM; // 0x7f070093 float:1.7944876E38 double:1.0529355757E-314;
        r6 = "windowBackgroundGrayShadow";
        r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r4, r6);
        r3.setBackgroundDrawable(r4);
        r3 = r1.linearLayout2;
        r4 = r1.bottomCell;
        r4 = r4[r5];
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r3.addView(r4, r5);
        r3 = new org.telegram.ui.Cells.TextSettingsCell;
        r3.<init>(r2);
        r1.settingsCell1 = r3;
        r3 = r1.settingsCell1;
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r3.setBackgroundDrawable(r4);
        r3 = r1.settingsCell1;
        r4 = "windowBackgroundWhiteRedText3";
        r3.setTag(r4);
        r3 = r1.settingsCell1;
        r4 = "windowBackgroundWhiteRedText3";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setTextColor(r4);
        r3 = r1.settingsCell1;
        r4 = "AbortPassword";
        r5 = NUM; // 0x7f0c0001 float:1.8609194E38 double:1.053097399E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r3.setText(r4, r14);
        r3 = r1.linearLayout2;
        r4 = r1.settingsCell1;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r3.addView(r4, r5);
        r3 = r1.settingsCell1;
        r4 = new org.telegram.ui.PaymentFormActivity$24;
        r4.<init>();
        r3.setOnClickListener(r4);
        r3 = new org.telegram.ui.Components.EditTextBoldCursor[r7];
        r1.inputFields = r3;
        r3 = r14;
    L_0x124b:
        if (r3 >= r7) goto L_0x1475;
    L_0x124d:
        if (r3 != 0) goto L_0x1283;
    L_0x124f:
        r4 = r1.headerCell;
        r5 = new org.telegram.ui.Cells.HeaderCell;
        r5.<init>(r2);
        r4[r14] = r5;
        r4 = r1.headerCell;
        r4 = r4[r14];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = r1.headerCell;
        r4 = r4[r14];
        r5 = "PaymentPasswordTitle";
        r6 = NUM; // 0x7f0c04df float:1.8611721E38 double:1.0530980146E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r4 = r1.linearLayout2;
        r5 = r1.headerCell;
        r5 = r5[r14];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
        goto L_0x12b9;
    L_0x1283:
        r4 = 2;
        if (r3 != r4) goto L_0x12b9;
    L_0x1286:
        r4 = r1.headerCell;
        r5 = new org.telegram.ui.Cells.HeaderCell;
        r5.<init>(r2);
        r4[r9] = r5;
        r4 = r1.headerCell;
        r4 = r4[r9];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = r1.headerCell;
        r4 = r4[r9];
        r5 = "PaymentPasswordEmailTitle";
        r6 = NUM; // 0x7f0c04db float:1.8611713E38 double:1.0530980126E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r4 = r1.linearLayout2;
        r5 = r1.headerCell;
        r5 = r5[r9];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
    L_0x12b9:
        r4 = new android.widget.FrameLayout;
        r4.<init>(r2);
        r5 = r1.linearLayout2;
        r6 = 48;
        r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r6);
        r5.addView(r4, r8);
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        if (r3 != 0) goto L_0x12f1;
    L_0x12d4:
        r5 = new android.view.View;
        r5.<init>(r2);
        r6 = r1.dividers;
        r6.add(r5);
        r6 = "divider";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setBackgroundColor(r6);
        r6 = new android.widget.FrameLayout$LayoutParams;
        r8 = 83;
        r6.<init>(r11, r9, r8);
        r4.addView(r5, r6);
    L_0x12f1:
        r5 = r1.inputFields;
        r6 = new org.telegram.ui.Components.EditTextBoldCursor;
        r6.<init>(r2);
        r5[r3] = r6;
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = java.lang.Integer.valueOf(r3);
        r5.setTag(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5.setTextSize(r9, r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "windowBackgroundWhiteHintText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setHintTextColor(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "windowBackgroundWhiteBlackText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setTextColor(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r12 = 0;
        r5.setBackgroundDrawable(r12);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = "windowBackgroundWhiteBlackText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setCursorColor(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5.setCursorSize(r6);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r5.setCursorWidth(r6);
        if (r3 == 0) goto L_0x1363;
    L_0x1355:
        if (r3 != r9) goto L_0x1358;
    L_0x1357:
        goto L_0x1363;
    L_0x1358:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r6 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r5.setImeOptions(r6);
        goto L_0x1382;
    L_0x1363:
        r6 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r5.setInputType(r8);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = android.graphics.Typeface.DEFAULT;
        r5.setTypeface(r8);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = 268435461; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r5.setImeOptions(r8);
    L_0x1382:
        switch(r3) {
            case 0: goto L_0x13a8;
            case 1: goto L_0x1397;
            case 2: goto L_0x1386;
            default: goto L_0x1385;
        };
    L_0x1385:
        goto L_0x13bf;
    L_0x1386:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = "PaymentPasswordEmail";
        r10 = NUM; // 0x7f0c04d9 float:1.861171E38 double:1.0530980116E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r5.setHint(r8);
        goto L_0x13bf;
    L_0x1397:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = "PaymentPasswordReEnter";
        r10 = NUM; // 0x7f0c04de float:1.861172E38 double:1.053098014E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r5.setHint(r8);
        goto L_0x13bf;
    L_0x13a8:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = "PaymentPasswordEnter";
        r10 = NUM; // 0x7f0c04dc float:1.8611715E38 double:1.053098013E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r5.setHint(r8);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r5.requestFocus();
    L_0x13bf:
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r5.setPadding(r14, r14, r14, r8);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x13d6;
    L_0x13d4:
        r8 = 5;
        goto L_0x13d7;
    L_0x13d6:
        r8 = r7;
    L_0x13d7:
        r5.setGravity(r8);
        r5 = r1.inputFields;
        r5 = r5[r3];
        r21 = -1;
        r22 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r23 = 51;
        r24 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r27 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r4.addView(r5, r8);
        r4 = r1.inputFields;
        r4 = r4[r3];
        r5 = new org.telegram.ui.PaymentFormActivity$25;
        r5.<init>();
        r4.setOnEditorActionListener(r5);
        if (r3 != r9) goto L_0x1438;
    L_0x1401:
        r4 = r1.bottomCell;
        r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r5.<init>(r2);
        r4[r14] = r5;
        r4 = r1.bottomCell;
        r4 = r4[r14];
        r5 = "PaymentPasswordInfo";
        r8 = NUM; // 0x7f0c04dd float:1.8611717E38 double:1.0530980136E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r8);
        r4.setText(r5);
        r4 = r1.bottomCell;
        r4 = r4[r14];
        r5 = NUM; // 0x7f070093 float:1.7944876E38 double:1.0529355757E-314;
        r8 = "windowBackgroundGrayShadow";
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r5, r8);
        r4.setBackgroundDrawable(r5);
        r4 = r1.linearLayout2;
        r5 = r1.bottomCell;
        r5 = r5[r14];
        r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r8);
        goto L_0x1471;
    L_0x1438:
        r4 = 2;
        if (r3 != r4) goto L_0x1471;
    L_0x143b:
        r4 = r1.bottomCell;
        r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r5.<init>(r2);
        r4[r9] = r5;
        r4 = r1.bottomCell;
        r4 = r4[r9];
        r5 = "PaymentPasswordEmailInfo";
        r8 = NUM; // 0x7f0c04da float:1.8611711E38 double:1.053098012E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r8);
        r4.setText(r5);
        r4 = r1.bottomCell;
        r4 = r4[r9];
        r5 = "windowBackgroundGrayShadow";
        r8 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r8, r5);
        r4.setBackgroundDrawable(r5);
        r4 = r1.linearLayout2;
        r5 = r1.bottomCell;
        r5 = r5[r9];
        r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r8);
    L_0x1471:
        r3 = r3 + 1;
        goto L_0x124b;
    L_0x1475:
        r29.updatePasswordFields();
        goto L_0x18a7;
    L_0x147a:
        r12 = 0;
        r4 = new org.telegram.ui.Cells.PaymentInfoCell;
        r4.<init>(r2);
        r1.paymentInfoCell = r4;
        r4 = r1.paymentInfoCell;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = r1.paymentInfoCell;
        r5 = r1.messageObject;
        r5 = r5.messageOwner;
        r5 = r5.media;
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r5;
        r6 = r1.currentBotName;
        r4.setInvoice(r5, r6);
        r4 = r1.linearLayout2;
        r5 = r1.paymentInfoCell;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
        r4 = r1.sectionCell;
        r5 = new org.telegram.ui.Cells.ShadowSectionCell;
        r5.<init>(r2);
        r4[r14] = r5;
        r4 = r1.linearLayout2;
        r5 = r1.sectionCell;
        r5 = r5[r14];
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13);
        r4.addView(r5, r6);
        r4 = new java.util.ArrayList;
        r4.<init>();
        r5 = r1.paymentForm;
        r5 = r5.invoice;
        r5 = r5.prices;
        r4.addAll(r5);
        r5 = r1.shippingOption;
        if (r5 == 0) goto L_0x14d6;
    L_0x14cf:
        r5 = r1.shippingOption;
        r5 = r5.prices;
        r4.addAll(r5);
    L_0x14d6:
        r5 = r1.getTotalPriceString(r4);
        r6 = r14;
    L_0x14db:
        r8 = r4.size();
        if (r6 >= r8) goto L_0x1515;
    L_0x14e1:
        r8 = r4.get(r6);
        r8 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r8;
        r10 = new org.telegram.ui.Cells.TextPriceCell;
        r10.<init>(r2);
        r15 = "windowBackgroundWhite";
        r15 = org.telegram.ui.ActionBar.Theme.getColor(r15);
        r10.setBackgroundColor(r15);
        r15 = r8.label;
        r12 = org.telegram.messenger.LocaleController.getInstance();
        r7 = r8.amount;
        r13 = r1.paymentForm;
        r13 = r13.invoice;
        r13 = r13.currency;
        r7 = r12.formatCurrencyString(r7, r13);
        r10.setTextAndValue(r15, r7, r14);
        r7 = r1.linearLayout2;
        r7.addView(r10);
        r6 = r6 + 1;
        r7 = 3;
        r12 = 0;
        r13 = -2;
        goto L_0x14db;
    L_0x1515:
        r4 = new org.telegram.ui.Cells.TextPriceCell;
        r4.<init>(r2);
        r6 = "windowBackgroundWhite";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r4.setBackgroundColor(r6);
        r6 = "PaymentTransactionTotal";
        r7 = NUM; // 0x7f0c04f8 float:1.8611772E38 double:1.053098027E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r4.setTextAndValue(r6, r5, r9);
        r6 = r1.linearLayout2;
        r6.addView(r4);
        r4 = new android.view.View;
        r4.<init>(r2);
        r6 = r1.dividers;
        r6.add(r4);
        r6 = "divider";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r4.setBackgroundColor(r6);
        r6 = r1.linearLayout2;
        r7 = new android.widget.FrameLayout$LayoutParams;
        r8 = 83;
        r7.<init>(r11, r9, r8);
        r6.addView(r4, r7);
        r4 = r1.detailSettingsCell;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r6.<init>(r2);
        r4[r14] = r6;
        r4 = r1.detailSettingsCell;
        r4 = r4[r14];
        r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r4.setBackgroundDrawable(r6);
        r4 = r1.detailSettingsCell;
        r4 = r4[r14];
        r6 = r1.cardName;
        r7 = "PaymentCheckoutMethod";
        r8 = NUM; // 0x7f0c04cb float:1.861168E38 double:1.0530980047E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);
        r4.setTextAndValue(r6, r7, r9);
        r4 = r1.linearLayout2;
        r6 = r1.detailSettingsCell;
        r6 = r6[r14];
        r4.addView(r6);
        r4 = r1.currentStep;
        r6 = 4;
        if (r4 != r6) goto L_0x1593;
    L_0x1587:
        r4 = r1.detailSettingsCell;
        r4 = r4[r14];
        r6 = new org.telegram.ui.PaymentFormActivity$20;
        r6.<init>();
        r4.setOnClickListener(r6);
    L_0x1593:
        r4 = r14;
        r6 = 0;
    L_0x1595:
        r7 = r1.paymentForm;
        r7 = r7.users;
        r7 = r7.size();
        if (r4 >= r7) goto L_0x15b5;
    L_0x159f:
        r7 = r1.paymentForm;
        r7 = r7.users;
        r7 = r7.get(r4);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
        r8 = r7.id;
        r10 = r1.paymentForm;
        r10 = r10.provider_id;
        if (r8 != r10) goto L_0x15b2;
    L_0x15b1:
        r6 = r7;
    L_0x15b2:
        r4 = r4 + 1;
        goto L_0x1595;
    L_0x15b5:
        if (r6 == 0) goto L_0x15ed;
    L_0x15b7:
        r4 = r1.detailSettingsCell;
        r7 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r7.<init>(r2);
        r4[r9] = r7;
        r4 = r1.detailSettingsCell;
        r4 = r4[r9];
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r4.setBackgroundDrawable(r7);
        r4 = r1.detailSettingsCell;
        r4 = r4[r9];
        r7 = r6.first_name;
        r6 = r6.last_name;
        r6 = org.telegram.messenger.ContactsController.formatName(r7, r6);
        r7 = "PaymentCheckoutProvider";
        r8 = NUM; // 0x7f0c04cf float:1.8611689E38 double:1.0530980067E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);
        r4.setTextAndValue(r6, r7, r9);
        r4 = r1.linearLayout2;
        r7 = r1.detailSettingsCell;
        r7 = r7[r9];
        r4.addView(r7);
        goto L_0x15ef;
    L_0x15ed:
        r6 = "";
    L_0x15ef:
        r4 = r1.validateRequest;
        if (r4 == 0) goto L_0x176f;
    L_0x15f3:
        r4 = r1.validateRequest;
        r4 = r4.info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x1674;
    L_0x15fb:
        r4 = "%s %s, %s, %s, %s, %s";
        r7 = 6;
        r8 = new java.lang.Object[r7];
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.street_line1;
        r8[r14] = r7;
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.street_line2;
        r8[r9] = r7;
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.city;
        r10 = 2;
        r8[r10] = r7;
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.state;
        r10 = 3;
        r8[r10] = r7;
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.country_iso2;
        r10 = 4;
        r8[r10] = r7;
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.post_code;
        r10 = 5;
        r8[r10] = r7;
        r4 = java.lang.String.format(r4, r8);
        r7 = r1.detailSettingsCell;
        r8 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r8.<init>(r2);
        r10 = 2;
        r7[r10] = r8;
        r7 = r1.detailSettingsCell;
        r7 = r7[r10];
        r8 = "windowBackgroundWhite";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.setBackgroundColor(r8);
        r7 = r1.detailSettingsCell;
        r7 = r7[r10];
        r8 = "PaymentShippingAddress";
        r12 = NUM; // 0x7f0c04e4 float:1.8611731E38 double:1.053098017E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r12);
        r7.setTextAndValue(r4, r8, r9);
        r4 = r1.linearLayout2;
        r7 = r1.detailSettingsCell;
        r7 = r7[r10];
        r4.addView(r7);
    L_0x1674:
        r4 = r1.validateRequest;
        r4 = r4.info;
        r4 = r4.name;
        if (r4 == 0) goto L_0x16b2;
    L_0x167c:
        r4 = r1.detailSettingsCell;
        r7 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r7.<init>(r2);
        r8 = 3;
        r4[r8] = r7;
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = "windowBackgroundWhite";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r4.setBackgroundColor(r7);
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.name;
        r10 = "PaymentCheckoutName";
        r12 = NUM; // 0x7f0c04cc float:1.8611683E38 double:1.053098005E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r12);
        r4.setTextAndValue(r7, r10, r9);
        r4 = r1.linearLayout2;
        r7 = r1.detailSettingsCell;
        r7 = r7[r8];
        r4.addView(r7);
    L_0x16b2:
        r4 = r1.validateRequest;
        r4 = r4.info;
        r4 = r4.phone;
        if (r4 == 0) goto L_0x16f9;
    L_0x16ba:
        r4 = r1.detailSettingsCell;
        r7 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r7.<init>(r2);
        r8 = 4;
        r4[r8] = r7;
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = "windowBackgroundWhite";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r4.setBackgroundColor(r7);
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r8 = r1.validateRequest;
        r8 = r8.info;
        r8 = r8.phone;
        r7 = r7.format(r8);
        r8 = "PaymentCheckoutPhoneNumber";
        r10 = NUM; // 0x7f0c04ce float:1.8611687E38 double:1.053098006E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);
        r4.setTextAndValue(r7, r8, r9);
        r4 = r1.linearLayout2;
        r7 = r1.detailSettingsCell;
        r8 = 4;
        r7 = r7[r8];
        r4.addView(r7);
    L_0x16f9:
        r4 = r1.validateRequest;
        r4 = r4.info;
        r4 = r4.email;
        if (r4 == 0) goto L_0x1737;
    L_0x1701:
        r4 = r1.detailSettingsCell;
        r7 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r7.<init>(r2);
        r8 = 5;
        r4[r8] = r7;
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = "windowBackgroundWhite";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r4.setBackgroundColor(r7);
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = r1.validateRequest;
        r7 = r7.info;
        r7 = r7.email;
        r10 = "PaymentCheckoutEmail";
        r12 = NUM; // 0x7f0c04ca float:1.8611679E38 double:1.053098004E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r12);
        r4.setTextAndValue(r7, r10, r9);
        r4 = r1.linearLayout2;
        r7 = r1.detailSettingsCell;
        r7 = r7[r8];
        r4.addView(r7);
    L_0x1737:
        r4 = r1.shippingOption;
        if (r4 == 0) goto L_0x176f;
    L_0x173b:
        r4 = r1.detailSettingsCell;
        r7 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r7.<init>(r2);
        r8 = 6;
        r4[r8] = r7;
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = "windowBackgroundWhite";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r4.setBackgroundColor(r7);
        r4 = r1.detailSettingsCell;
        r4 = r4[r8];
        r7 = r1.shippingOption;
        r7 = r7.title;
        r10 = "PaymentCheckoutShippingMethod";
        r12 = NUM; // 0x7f0c04d0 float:1.861169E38 double:1.053098007E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r10, r12);
        r4.setTextAndValue(r7, r10, r14);
        r4 = r1.linearLayout2;
        r7 = r1.detailSettingsCell;
        r7 = r7[r8];
        r4.addView(r7);
    L_0x176f:
        r4 = r1.currentStep;
        r7 = 4;
        if (r4 != r7) goto L_0x1880;
    L_0x1774:
        r4 = new android.widget.FrameLayout;
        r4.<init>(r2);
        r1.bottomLayout = r4;
        r4 = r1.bottomLayout;
        r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9);
        r4.setBackgroundDrawable(r7);
        r4 = r1.bottomLayout;
        r7 = 80;
        r8 = 48;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r8, r7);
        r3.addView(r4, r7);
        r4 = r1.bottomLayout;
        r7 = new org.telegram.ui.PaymentFormActivity$21;
        r7.<init>(r6, r5);
        r4.setOnClickListener(r7);
        r4 = new android.widget.TextView;
        r4.<init>(r2);
        r1.payTextView = r4;
        r4 = r1.payTextView;
        r6 = "windowBackgroundWhiteBlueText6";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r4.setTextColor(r6);
        r4 = r1.payTextView;
        r6 = "PaymentCheckoutPay";
        r7 = NUM; // 0x7f0c04cd float:1.8611685E38 double:1.0530980057E-314;
        r8 = new java.lang.Object[r9];
        r8[r14] = r5;
        r5 = org.telegram.messenger.LocaleController.formatString(r6, r7, r8);
        r4.setText(r5);
        r4 = r1.payTextView;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4.setTextSize(r9, r5);
        r4 = r1.payTextView;
        r5 = 17;
        r4.setGravity(r5);
        r4 = r1.payTextView;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r4.setTypeface(r5);
        r4 = r1.bottomLayout;
        r5 = r1.payTextView;
        r6 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Components.ContextProgressView;
        r4.<init>(r2, r14);
        r1.progressViewButton = r4;
        r4 = r1.progressViewButton;
        r5 = 4;
        r4.setVisibility(r5);
        r4 = r1.bottomLayout;
        r5 = r1.progressViewButton;
        r6 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6);
        r4.addView(r5, r6);
        r4 = new android.view.View;
        r4.<init>(r2);
        r5 = NUM; // 0x7f07009f float:1.79449E38 double:1.0529355816E-314;
        r4.setBackgroundResource(r5);
        r19 = -1;
        r20 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r21 = 83;
        r22 = 0;
        r23 = 0;
        r24 = 0;
        r25 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r3.addView(r4, r5);
        r4 = r1.doneItem;
        r4.setEnabled(r14);
        r4 = r1.doneItem;
        r4 = r4.getImageView();
        r5 = 4;
        r4.setVisibility(r5);
        r4 = new org.telegram.ui.PaymentFormActivity$22;
        r4.<init>(r2);
        r1.webView = r4;
        r4 = r1.webView;
        r4.setBackgroundColor(r11);
        r4 = r1.webView;
        r4 = r4.getSettings();
        r4.setJavaScriptEnabled(r9);
        r4 = r1.webView;
        r4 = r4.getSettings();
        r4.setDomStorageEnabled(r9);
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x1864;
    L_0x1852:
        r4 = r1.webView;
        r4 = r4.getSettings();
        r4.setMixedContentMode(r14);
        r4 = android.webkit.CookieManager.getInstance();
        r5 = r1.webView;
        r4.setAcceptThirdPartyCookies(r5, r9);
    L_0x1864:
        r4 = r1.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$23;
        r5.<init>();
        r4.setWebViewClient(r5);
        r4 = r1.webView;
        r5 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r5);
        r3.addView(r4, r5);
        r3 = r1.webView;
        r4 = 8;
        r3.setVisibility(r4);
    L_0x1880:
        r3 = r1.sectionCell;
        r4 = new org.telegram.ui.Cells.ShadowSectionCell;
        r4.<init>(r2);
        r3[r9] = r4;
        r3 = r1.sectionCell;
        r3 = r3[r9];
        r4 = "windowBackgroundGrayShadow";
        r5 = NUM; // 0x7f070094 float:1.7944878E38 double:1.052935576E-314;
        r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r5, r4);
        r3.setBackgroundDrawable(r2);
        r2 = r1.linearLayout2;
        r3 = r1.sectionCell;
        r3 = r3[r9];
        r4 = -2;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r4);
        r2.addView(r3, r4);
    L_0x18a7:
        r2 = r1.fragmentView;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
    }

    private void updatePasswordFields() {
        if (this.currentStep == 6) {
            if (this.bottomCell[2] != null) {
                int i = 0;
                int i2;
                if (this.currentPassword == null) {
                    this.doneItem.setVisibility(0);
                    showEditDoneProgress(true, true);
                    this.bottomCell[2].setVisibility(8);
                    this.settingsCell1.setVisibility(8);
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
                } else {
                    showEditDoneProgress(true, false);
                    if (this.waitingForEmail) {
                        if (getParentActivity() != null) {
                            AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                        }
                        this.doneItem.setVisibility(8);
                        this.bottomCell[2].setText(LocaleController.formatString("EmailPasswordConfirmText", C0446R.string.EmailPasswordConfirmText, this.currentPassword.email_unconfirmed_pattern));
                        this.bottomCell[2].setVisibility(0);
                        this.settingsCell1.setVisibility(0);
                        this.bottomCell[1].setText(TtmlNode.ANONYMOUS_REGION_ID);
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
                    } else {
                        this.doneItem.setVisibility(0);
                        this.bottomCell[2].setVisibility(8);
                        this.settingsCell1.setVisibility(8);
                        this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", C0446R.string.PaymentPasswordEmailInfo));
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
            }
        }
    }

    private void loadPasswordInfo() {
        if (!this.loadingPasswordInfo) {
            this.loadingPasswordInfo = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.ui.PaymentFormActivity$26$1$1 */
                        class C15581 implements Runnable {
                            C15581() {
                            }

                            public void run() {
                                if (PaymentFormActivity.this.shortPollRunnable != null) {
                                    PaymentFormActivity.this.loadPasswordInfo();
                                    PaymentFormActivity.this.shortPollRunnable = null;
                                }
                            }
                        }

                        public void run() {
                            PaymentFormActivity.this.loadingPasswordInfo = false;
                            if (tL_error == null) {
                                PaymentFormActivity.this.currentPassword = (account_Password) tLObject;
                                if (PaymentFormActivity.this.paymentForm != null && (PaymentFormActivity.this.currentPassword instanceof TL_account_password)) {
                                    PaymentFormActivity.this.paymentForm.password_missing = false;
                                    PaymentFormActivity.this.paymentForm.can_save_credentials = true;
                                    PaymentFormActivity.this.updateSavePaymentField();
                                }
                                Object obj = new byte[(PaymentFormActivity.this.currentPassword.new_salt.length + 8)];
                                Utilities.random.nextBytes(obj);
                                System.arraycopy(PaymentFormActivity.this.currentPassword.new_salt, 0, obj, 0, PaymentFormActivity.this.currentPassword.new_salt.length);
                                PaymentFormActivity.this.currentPassword.new_salt = obj;
                                if (PaymentFormActivity.this.passwordFragment != null) {
                                    PaymentFormActivity.this.passwordFragment.setCurrentPassword(PaymentFormActivity.this.currentPassword);
                                }
                            }
                            if ((tLObject instanceof TL_account_noPassword) && PaymentFormActivity.this.shortPollRunnable == null) {
                                PaymentFormActivity.this.shortPollRunnable = new C15581();
                                AndroidUtilities.runOnUIThread(PaymentFormActivity.this.shortPollRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                            }
                        }
                    });
                }
            }, 10);
        }
    }

    private void showAlertWithText(String str, String str2) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void showPayAlert(String str) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", C0446R.string.PaymentTransactionReview));
        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", C0446R.string.PaymentTransactionMessage, str, this.currentBotName, this.currentItemName));
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.setDonePressed(1);
                PaymentFormActivity.this.sendData();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void initAndroidPay(Context context) {
        if (VERSION.SDK_INT < 0) {
            this.googleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(new ConnectionCallbacks() {
                public void onConnected(Bundle bundle) {
                }

                public void onConnectionSuspended(int i) {
                }
            }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
                public void onConnectionFailed(ConnectionResult connectionResult) {
                }
            }).addApi(Wallet.API, new WalletOptions.Builder().setEnvironment(this.paymentForm.invoice.test ? 3 : 1).setTheme(1).build()).build();
            Wallet.Payments.isReadyToPay(this.googleApiClient).setResultCallback(new ResultCallback<BooleanResult>() {
                public void onResult(BooleanResult booleanResult) {
                    if (booleanResult.getStatus().isSuccess() && booleanResult.getValue() != null) {
                        PaymentFormActivity.this.showAndroidPay();
                    }
                }
            });
            this.googleApiClient.connect();
        }
    }

    private String getTotalPriceString(ArrayList<TL_labeledPrice> arrayList) {
        long j = 0;
        int i = 0;
        while (i < arrayList.size()) {
            i++;
            j += ((TL_labeledPrice) arrayList.get(i)).amount;
        }
        return LocaleController.getInstance().formatCurrencyString(j, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TL_labeledPrice> arrayList) {
        long j = 0;
        int i = 0;
        while (i < arrayList.size()) {
            i++;
            j += ((TL_labeledPrice) arrayList.get(i)).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(j, this.paymentForm.invoice.currency, false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemovedTwoStepPassword);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemovedTwoStepPassword);
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        try {
            if ((this.currentStep == 2 || this.currentStep == 6) && VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        super.onFragmentDestroy();
        this.canceled = true;
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            if (this.webView) {
                if (!this.currentStep) {
                    this.webView.loadUrl(this.paymentForm.url);
                }
            } else if (this.currentStep) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            } else if (this.currentStep) {
                this.inputFields[1].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[1]);
            } else if (this.currentStep && !this.waitingForEmail) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetTwoStepPassword) {
            this.paymentForm.password_missing = false;
            this.paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (i == NotificationCenter.didRemovedTwoStepPassword) {
            this.paymentForm.password_missing = true;
            this.paymentForm.can_save_credentials = false;
            updateSavePaymentField();
        } else if (i == NotificationCenter.paymentFinished) {
            removeSelfFromStack();
        }
    }

    private void showAndroidPay() {
        if (getParentActivity() != null) {
            if (this.androidPayContainer != null) {
                WalletFragmentStyle buyButtonText;
                PaymentMethodTokenizationParameters build;
                WalletFragmentOptions.Builder newBuilder = WalletFragmentOptions.newBuilder();
                newBuilder.setEnvironment(this.paymentForm.invoice.test ? 3 : 1);
                newBuilder.setMode(1);
                int i = 6;
                if (this.androidPayPublicKey != null) {
                    this.androidPayContainer.setBackgroundColor(this.androidPayBackgroundColor);
                    buyButtonText = new WalletFragmentStyle().setBuyButtonText(5);
                    if (!this.androidPayBlackTheme) {
                        i = 4;
                    }
                    buyButtonText = buyButtonText.setBuyButtonAppearance(i).setBuyButtonWidth(-1);
                } else {
                    buyButtonText = new WalletFragmentStyle().setBuyButtonText(6).setBuyButtonAppearance(6).setBuyButtonWidth(-2);
                }
                newBuilder.setFragmentStyle(buyButtonText);
                Fragment newInstance = WalletFragment.newInstance(newBuilder.build());
                FragmentTransaction beginTransaction = getParentActivity().getFragmentManager().beginTransaction();
                beginTransaction.replace(fragment_container_id, newInstance);
                beginTransaction.commit();
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.paymentForm.invoice.prices);
                if (this.shippingOption != null) {
                    arrayList.addAll(this.shippingOption.prices);
                }
                this.totalPriceDecimal = getTotalPriceDecimalString(arrayList);
                if (this.androidPayPublicKey != null) {
                    build = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(2).addParameter("publicKey", this.androidPayPublicKey).build();
                } else {
                    build = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(1).addParameter("gateway", "stripe").addParameter("stripe:publishableKey", this.stripeApiKey).addParameter("stripe:version", "3.5.0").build();
                }
                newInstance.initialize(WalletFragmentInitParams.newBuilder().setMaskedWalletRequest(MaskedWalletRequest.newBuilder().setPaymentMethodTokenizationParameters(build).setEstimatedTotalPrice(this.totalPriceDecimal).setCurrencyCode(this.paymentForm.invoice.currency).build()).setMaskedWalletRequestCode(LOAD_MASKED_WALLET_REQUEST_CODE).build());
                this.androidPayContainer.setVisibility(0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[]{0.0f, 1.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(180);
                animatorSet.start();
            }
        }
    }

    public void onActivityResultFragment(int r9, int r10, android.content.Intent r11) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r8 = this;
        r0 = 1001; // 0x3e9 float:1.403E-42 double:4.946E-321;
        r1 = -1;
        r2 = 1;
        r3 = 0;
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r9 != r4) goto L_0x00b8;
    L_0x0009:
        if (r10 != r1) goto L_0x00b0;
    L_0x000b:
        r8.showEditDoneProgress(r2, r2);
        r8.setDonePressed(r2);
        r9 = "com.google.android.gms.wallet.EXTRA_MASKED_WALLET";
        r9 = r11.getParcelableExtra(r9);
        r9 = (com.google.android.gms.wallet.MaskedWallet) r9;
        r10 = com.google.android.gms.wallet.Cart.newBuilder();
        r11 = r8.paymentForm;
        r11 = r11.invoice;
        r11 = r11.currency;
        r10 = r10.setCurrencyCode(r11);
        r11 = r8.totalPriceDecimal;
        r10 = r10.setTotalPrice(r11);
        r11 = new java.util.ArrayList;
        r11.<init>();
        r1 = r8.paymentForm;
        r1 = r1.invoice;
        r1 = r1.prices;
        r11.addAll(r1);
        r1 = r8.shippingOption;
        if (r1 == 0) goto L_0x0046;
    L_0x003f:
        r1 = r8.shippingOption;
        r1 = r1.prices;
        r11.addAll(r1);
    L_0x0046:
        r1 = r3;
    L_0x0047:
        r2 = r11.size();
        if (r1 >= r2) goto L_0x008f;
    L_0x004d:
        r2 = r11.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r2;
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r2.amount;
        r7 = r8.paymentForm;
        r7 = r7.invoice;
        r7 = r7.currency;
        r4 = r4.formatCurrencyDecimalString(r5, r7, r3);
        r5 = com.google.android.gms.wallet.LineItem.newBuilder();
        r6 = r8.paymentForm;
        r6 = r6.invoice;
        r6 = r6.currency;
        r5 = r5.setCurrencyCode(r6);
        r6 = "1";
        r5 = r5.setQuantity(r6);
        r2 = r2.label;
        r2 = r5.setDescription(r2);
        r2 = r2.setTotalPrice(r4);
        r2 = r2.setUnitPrice(r4);
        r2 = r2.build();
        r10.addLineItem(r2);
        r1 = r1 + 1;
        goto L_0x0047;
    L_0x008f:
        r11 = com.google.android.gms.wallet.FullWalletRequest.newBuilder();
        r10 = r10.build();
        r10 = r11.setCart(r10);
        r9 = r9.getGoogleTransactionId();
        r9 = r10.setGoogleTransactionId(r9);
        r9 = r9.build();
        r10 = com.google.android.gms.wallet.Wallet.Payments;
        r11 = r8.googleApiClient;
        r10.loadFullWallet(r11, r9, r0);
        goto L_0x0155;
    L_0x00b0:
        r8.showEditDoneProgress(r2, r3);
        r8.setDonePressed(r3);
        goto L_0x0155;
    L_0x00b8:
        if (r9 != r0) goto L_0x0155;
    L_0x00ba:
        if (r10 != r1) goto L_0x014f;
    L_0x00bc:
        r9 = "com.google.android.gms.wallet.EXTRA_FULL_WALLET";
        r9 = r11.getParcelableExtra(r9);
        r9 = (com.google.android.gms.wallet.FullWallet) r9;
        r10 = r9.getPaymentMethodToken();
        r10 = r10.getToken();
        r11 = r8.androidPayPublicKey;	 Catch:{ JSONException -> 0x0148 }
        if (r11 == 0) goto L_0x00ff;	 Catch:{ JSONException -> 0x0148 }
    L_0x00d0:
        r11 = new org.telegram.tgnet.TLRPC$TL_inputPaymentCredentialsAndroidPay;	 Catch:{ JSONException -> 0x0148 }
        r11.<init>();	 Catch:{ JSONException -> 0x0148 }
        r8.androidPayCredentials = r11;	 Catch:{ JSONException -> 0x0148 }
        r11 = r8.androidPayCredentials;	 Catch:{ JSONException -> 0x0148 }
        r0 = new org.telegram.tgnet.TLRPC$TL_dataJSON;	 Catch:{ JSONException -> 0x0148 }
        r0.<init>();	 Catch:{ JSONException -> 0x0148 }
        r11.payment_token = r0;	 Catch:{ JSONException -> 0x0148 }
        r11 = r8.androidPayCredentials;	 Catch:{ JSONException -> 0x0148 }
        r11 = r11.payment_token;	 Catch:{ JSONException -> 0x0148 }
        r11.data = r10;	 Catch:{ JSONException -> 0x0148 }
        r10 = r8.androidPayCredentials;	 Catch:{ JSONException -> 0x0148 }
        r11 = r9.getGoogleTransactionId();	 Catch:{ JSONException -> 0x0148 }
        r10.google_transaction_id = r11;	 Catch:{ JSONException -> 0x0148 }
        r9 = r9.getPaymentDescriptions();	 Catch:{ JSONException -> 0x0148 }
        r10 = r9.length;	 Catch:{ JSONException -> 0x0148 }
        if (r10 <= 0) goto L_0x00fa;	 Catch:{ JSONException -> 0x0148 }
    L_0x00f5:
        r9 = r9[r3];	 Catch:{ JSONException -> 0x0148 }
        r8.cardName = r9;	 Catch:{ JSONException -> 0x0148 }
        goto L_0x013e;	 Catch:{ JSONException -> 0x0148 }
    L_0x00fa:
        r9 = "Android Pay";	 Catch:{ JSONException -> 0x0148 }
        r8.cardName = r9;	 Catch:{ JSONException -> 0x0148 }
        goto L_0x013e;	 Catch:{ JSONException -> 0x0148 }
    L_0x00ff:
        r9 = com.stripe.android.net.TokenParser.parseToken(r10);	 Catch:{ JSONException -> 0x0148 }
        r10 = java.util.Locale.US;	 Catch:{ JSONException -> 0x0148 }
        r11 = "{\"type\":\"%1$s\", \"id\":\"%2$s\"}";	 Catch:{ JSONException -> 0x0148 }
        r0 = 2;	 Catch:{ JSONException -> 0x0148 }
        r0 = new java.lang.Object[r0];	 Catch:{ JSONException -> 0x0148 }
        r1 = r9.getType();	 Catch:{ JSONException -> 0x0148 }
        r0[r3] = r1;	 Catch:{ JSONException -> 0x0148 }
        r1 = r9.getId();	 Catch:{ JSONException -> 0x0148 }
        r0[r2] = r1;	 Catch:{ JSONException -> 0x0148 }
        r10 = java.lang.String.format(r10, r11, r0);	 Catch:{ JSONException -> 0x0148 }
        r8.paymentJson = r10;	 Catch:{ JSONException -> 0x0148 }
        r9 = r9.getCard();	 Catch:{ JSONException -> 0x0148 }
        r10 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0148 }
        r10.<init>();	 Catch:{ JSONException -> 0x0148 }
        r11 = r9.getType();	 Catch:{ JSONException -> 0x0148 }
        r10.append(r11);	 Catch:{ JSONException -> 0x0148 }
        r11 = " *";	 Catch:{ JSONException -> 0x0148 }
        r10.append(r11);	 Catch:{ JSONException -> 0x0148 }
        r9 = r9.getLast4();	 Catch:{ JSONException -> 0x0148 }
        r10.append(r9);	 Catch:{ JSONException -> 0x0148 }
        r9 = r10.toString();	 Catch:{ JSONException -> 0x0148 }
        r8.cardName = r9;	 Catch:{ JSONException -> 0x0148 }
    L_0x013e:
        r8.goToNextStep();	 Catch:{ JSONException -> 0x0148 }
        r8.showEditDoneProgress(r2, r3);	 Catch:{ JSONException -> 0x0148 }
        r8.setDonePressed(r3);	 Catch:{ JSONException -> 0x0148 }
        goto L_0x0155;
    L_0x0148:
        r8.showEditDoneProgress(r2, r3);
        r8.setDonePressed(r3);
        goto L_0x0155;
    L_0x014f:
        r8.showEditDoneProgress(r2, r3);
        r8.setDonePressed(r3);
    L_0x0155:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.onActivityResultFragment(int, int, android.content.Intent):void");
    }

    private void goToNextStep() {
        int i;
        if (this.currentStep == 0) {
            if (r0.paymentForm.invoice.flexible) {
                i = 1;
            } else if (r0.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(r0.currentAccount).tmpPassword != null && UserConfig.getInstance(r0.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(r0.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(r0.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(r0.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i = 2;
            }
            presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, i, r0.requestedInfo, null, null, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.isWebView);
        } else if (r0.currentStep == 1) {
            if (r0.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(r0.currentAccount).tmpPassword != null && UserConfig.getInstance(r0.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(r0.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(r0.currentAccount).saveConfig(false);
                }
                i = UserConfig.getInstance(r0.currentAccount).tmpPassword != null ? 4 : 3;
            } else {
                i = 2;
            }
            presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, i, r0.requestedInfo, r0.shippingOption, null, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.isWebView);
        } else if (r0.currentStep == 2) {
            if (r0.paymentForm.password_missing && r0.saveCardInfo) {
                r0.passwordFragment = new PaymentFormActivity(r0.paymentForm, r0.messageObject, 6, r0.requestedInfo, r0.shippingOption, r0.paymentJson, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials);
                r0.passwordFragment.setCurrentPassword(r0.currentPassword);
                r0.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                    public boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay) {
                        if (PaymentFormActivity.this.delegate != null) {
                            PaymentFormActivity.this.delegate.didSelectNewCard(str, str2, z, tL_inputPaymentCredentialsAndroidPay);
                        }
                        if (PaymentFormActivity.this.isWebView != null) {
                            PaymentFormActivity.this.removeSelfFromStack();
                        }
                        return PaymentFormActivity.this.delegate != null ? true : null;
                    }

                    public void onFragmentDestroyed() {
                        PaymentFormActivity.this.passwordFragment = null;
                    }

                    public void currentPasswordUpdated(account_Password account_password) {
                        PaymentFormActivity.this.currentPassword = account_password;
                    }
                });
                presentFragment(r0.passwordFragment, r0.isWebView);
            } else if (r0.delegate != null) {
                r0.delegate.didSelectNewCard(r0.paymentJson, r0.cardName, r0.saveCardInfo, r0.androidPayCredentials);
                finishFragment();
            } else {
                presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, 4, r0.requestedInfo, r0.shippingOption, r0.paymentJson, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.isWebView);
            }
        } else if (r0.currentStep == 3) {
            presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, r0.passwordOk ? 4 : 2, r0.requestedInfo, r0.shippingOption, r0.paymentJson, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.passwordOk ^ true);
        } else if (r0.currentStep == 4) {
            NotificationCenter.getInstance(r0.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        } else if (r0.currentStep != 6) {
        } else {
            if (r0.delegate.didSelectNewCard(r0.paymentJson, r0.cardName, r0.saveCardInfo, r0.androidPayCredentials)) {
                finishFragment();
            } else {
                presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, 4, r0.requestedInfo, r0.shippingOption, r0.paymentJson, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), true);
            }
        }
    }

    private void updateSavePaymentField() {
        if (this.bottomCell[0] != null) {
            if (this.sectionCell[2] != null) {
                if (this.paymentForm.password_missing || this.paymentForm.can_save_credentials) {
                    if (this.webView != null) {
                        if (!(this.webView == null || this.webviewLoading)) {
                        }
                    }
                    CharSequence spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", C0446R.string.PaymentCardSavePaymentInformationInfoLine1));
                    if (this.paymentForm.password_missing) {
                        loadPasswordInfo();
                        spannableStringBuilder.append("\n");
                        int length = spannableStringBuilder.length();
                        CharSequence string = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", C0446R.string.PaymentCardSavePaymentInformationInfoLine2);
                        int indexOf = string.indexOf(42);
                        int lastIndexOf = string.lastIndexOf(42);
                        spannableStringBuilder.append(string);
                        if (!(indexOf == -1 || lastIndexOf == -1)) {
                            indexOf += length;
                            lastIndexOf += length;
                            this.bottomCell[0].getTextView().setMovementMethod(new LinkMovementMethodMy());
                            spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, TtmlNode.ANONYMOUS_REGION_ID);
                            spannableStringBuilder.replace(indexOf, indexOf + 1, TtmlNode.ANONYMOUS_REGION_ID);
                            spannableStringBuilder.setSpan(new LinkSpan(), indexOf, lastIndexOf - 1, 33);
                        }
                    }
                    this.checkCell1.setEnabled(true);
                    this.bottomCell[0].setText(spannableStringBuilder);
                    this.checkCell1.setVisibility(0);
                    this.bottomCell[0].setVisibility(0);
                    this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
                this.checkCell1.setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }
    }

    @SuppressLint({"HardwareIds"})
    public void fillNumber(String str) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (str != null || (telephonyManager.getSimState() != 1 && telephonyManager.getPhoneType() != 0)) {
                int i;
                int i2;
                if (VERSION.SDK_INT >= 23) {
                    i2 = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0 ? 1 : 0;
                    i = getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0 ? 1 : 0;
                } else {
                    i2 = 1;
                    i = i2;
                }
                if (str != null || r2 != 0 || r3 != 0) {
                    if (str == null) {
                        str = PhoneFormat.stripExceptNumbers(telephonyManager.getLine1Number());
                    }
                    CharSequence charSequence = null;
                    if (!TextUtils.isEmpty(str)) {
                        i = 4;
                        if (str.length() > 4) {
                            String str2;
                            int i3;
                            while (i >= 1) {
                                CharSequence substring = str.substring(0, i);
                                if (((String) this.codesMap.get(substring)) != null) {
                                    String substring2 = str.substring(i, str.length());
                                    this.inputFields[8].setText(substring);
                                    str2 = substring2;
                                    i3 = 1;
                                    break;
                                }
                                i--;
                            }
                            str2 = null;
                            i3 = 0;
                            if (i3 == 0) {
                                charSequence = str.substring(1, str.length());
                                this.inputFields[8].setText(str.substring(0, 1));
                            } else {
                                charSequence = str2;
                            }
                        }
                        if (charSequence != null) {
                            this.inputFields[9].setText(charSequence);
                            this.inputFields[9].setSelection(this.inputFields[9].length());
                        }
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void sendSavePassword(final boolean z) {
        TLObject tL_account_updatePasswordSettings = new TL_account_updatePasswordSettings();
        String str = null;
        if (z) {
            this.doneItem.setVisibility(0);
            tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
            tL_account_updatePasswordSettings.new_settings.flags = 2;
            tL_account_updatePasswordSettings.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
            tL_account_updatePasswordSettings.current_password_hash = new byte[0];
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
                if (lastIndexOf >= 0 && lastIndexOf2 >= 0) {
                    if (lastIndexOf >= lastIndexOf2) {
                        Object bytes;
                        tL_account_updatePasswordSettings.current_password_hash = new byte[0];
                        tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
                        try {
                            bytes = obj.getBytes(C0542C.UTF8_NAME);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        Object obj3 = this.currentPassword.new_salt;
                        Object obj4 = new byte[((obj3.length * 2) + bytes.length)];
                        System.arraycopy(obj3, 0, obj4, 0, obj3.length);
                        System.arraycopy(bytes, 0, obj4, obj3.length, bytes.length);
                        System.arraycopy(obj3, 0, obj4, obj4.length - obj3.length, obj3.length);
                        TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                        tL_account_passwordInputSettings.flags |= 1;
                        tL_account_updatePasswordSettings.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
                        tL_account_updatePasswordSettings.new_settings.new_password_hash = Utilities.computeSHA256(obj4, 0, obj4.length);
                        tL_account_updatePasswordSettings.new_settings.new_salt = obj3;
                        if (obj2.length() > 0) {
                            tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                            tL_account_passwordInputSettings.flags = 2 | tL_account_passwordInputSettings.flags;
                            tL_account_updatePasswordSettings.new_settings.email = obj2.trim();
                        }
                        str = obj2;
                    }
                }
                shakeField(2);
                return;
            } else {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", C0446R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                shakeField(1);
                return;
            }
        }
        showEditDoneProgress(true, true);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.ui.PaymentFormActivity$32$1$1 */
                    class C15611 implements DialogInterface.OnClickListener {
                        C15611() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            PaymentFormActivity.this.waitingForEmail = 1;
                            PaymentFormActivity.this.currentPassword.email_unconfirmed_pattern = str;
                            PaymentFormActivity.this.updatePasswordFields();
                        }
                    }

                    public void run() {
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        if (z) {
                            PaymentFormActivity.this.currentPassword = new TL_account_noPassword();
                            PaymentFormActivity.this.delegate.currentPasswordUpdated(PaymentFormActivity.this.currentPassword);
                            PaymentFormActivity.this.finishFragment();
                        } else if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                            if (PaymentFormActivity.this.getParentActivity() != null) {
                                PaymentFormActivity.this.goToNextStep();
                            }
                        } else if (tL_error != null) {
                            if (tL_error.text.equals("EMAIL_UNCONFIRMED")) {
                                Builder builder = new Builder(PaymentFormActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C15611());
                                builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", C0446R.string.YourEmailAlmostThereText));
                                builder.setTitle(LocaleController.getString("YourEmailAlmostThere", C0446R.string.YourEmailAlmostThere));
                                Dialog showDialog = PaymentFormActivity.this.showDialog(builder.create());
                                if (showDialog != null) {
                                    showDialog.setCanceledOnTouchOutside(false);
                                    showDialog.setCancelable(false);
                                }
                            } else if (tL_error.text.equals("EMAIL_INVALID")) {
                                PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("PasswordEmailInvalid", C0446R.string.PasswordEmailInvalid));
                            } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                                String formatPluralString;
                                int intValue = Utilities.parseInt(tL_error.text).intValue();
                                if (intValue < 60) {
                                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                                } else {
                                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                                }
                                PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                            } else {
                                PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                            }
                        }
                    }
                });
            }
        }, true);
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
        int i = 3;
        Card card = new Card(r1.inputFields[0].getText().toString(), num, parseInt, r1.inputFields[3].getText().toString(), r1.inputFields[2].getText().toString(), null, null, null, null, r1.inputFields[5].getText().toString(), r1.inputFields[4].getText().toString(), null);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(card.getType());
        stringBuilder.append(" *");
        stringBuilder.append(card.getLast4());
        r1.cardName = stringBuilder.toString();
        if (card.validateNumber()) {
            if (card.validateExpMonth() && card.validateExpYear()) {
                if (card.validateExpiryDate()) {
                    if (r1.need_card_name && r1.inputFields[2].length() == 0) {
                        shakeField(2);
                        return false;
                    } else if (!card.validateCVC()) {
                        shakeField(i);
                        return false;
                    } else if (r1.need_card_country && r1.inputFields[4].length() == 0) {
                        shakeField(4);
                        return false;
                    } else if (r1.need_card_postcode && r1.inputFields[5].length() == 0) {
                        shakeField(5);
                        return false;
                    } else {
                        showEditDoneProgress(true, true);
                        try {
                            new Stripe(r1.stripeApiKey).createToken(card, new TokenCallback() {

                                /* renamed from: org.telegram.ui.PaymentFormActivity$33$1 */
                                class C15631 implements Runnable {
                                    C15631() {
                                    }

                                    public void run() {
                                        PaymentFormActivity.this.goToNextStep();
                                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                                        PaymentFormActivity.this.setDonePressed(false);
                                    }
                                }

                                public void onSuccess(Token token) {
                                    if (!PaymentFormActivity.this.canceled) {
                                        PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                                        AndroidUtilities.runOnUIThread(new C15631());
                                    }
                                }

                                public void onError(Exception exception) {
                                    if (!PaymentFormActivity.this.canceled) {
                                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                                        PaymentFormActivity.this.setDonePressed(false);
                                        if (!(exception instanceof APIConnectionException)) {
                                            if (!(exception instanceof APIException)) {
                                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, exception.getMessage());
                                            }
                                        }
                                        AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", C0446R.string.PaymentConnectionFailed));
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        return true;
                    }
                }
            }
            shakeField(1);
            return false;
        }
        shakeField(0);
        return false;
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
                this.validateRequest.info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : TtmlNode.ANONYMOUS_REGION_ID;
                this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
                TL_paymentRequestedInfo tL_paymentRequestedInfo2 = this.validateRequest.info;
                tL_paymentRequestedInfo2.flags |= 8;
            }
            final TLObject tLObject = this.validateRequest;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    if (tLObject instanceof TL_payments_validatedRequestedInfo) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.PaymentFormActivity$34$1$1 */
                            class C22191 implements RequestDelegate {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                }

                                C22191() {
                                }
                            }

                            public void run() {
                                PaymentFormActivity.this.requestedInfo = (TL_payments_validatedRequestedInfo) tLObject;
                                if (!(PaymentFormActivity.this.paymentForm.saved_info == null || PaymentFormActivity.this.saveShippingInfo)) {
                                    TLObject tL_payments_clearSavedInfo = new TL_payments_clearSavedInfo();
                                    tL_payments_clearSavedInfo.info = true;
                                    ConnectionsManager.getInstance(PaymentFormActivity.this.currentAccount).sendRequest(tL_payments_clearSavedInfo, new C22191());
                                }
                                PaymentFormActivity.this.goToNextStep();
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                            }
                        });
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            /* JADX WARNING: inconsistent code. */
                            /* Code decompiled incorrectly, please refer to instructions dump. */
                            public void run() {
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                if (tL_error != null) {
                                    int i;
                                    String str = tL_error.text;
                                    switch (str.hashCode()) {
                                        case -2092780146:
                                            if (str.equals("ADDRESS_CITY_INVALID")) {
                                                i = 4;
                                                break;
                                            }
                                        case -1623547228:
                                            if (str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                                                i = 7;
                                                break;
                                            }
                                        case -1224177757:
                                            if (str.equals("ADDRESS_COUNTRY_INVALID")) {
                                                i = 3;
                                                break;
                                            }
                                        case -1031752045:
                                            if (str.equals("REQ_INFO_NAME_INVALID")) {
                                                i = false;
                                                break;
                                            }
                                        case -274035920:
                                            if (str.equals("ADDRESS_POSTCODE_INVALID")) {
                                                i = 5;
                                                break;
                                            }
                                        case 417441502:
                                            if (str.equals("ADDRESS_STATE_INVALID")) {
                                                i = 6;
                                                break;
                                            }
                                        case 708423542:
                                            if (str.equals("REQ_INFO_PHONE_INVALID")) {
                                                i = true;
                                                break;
                                            }
                                        case 863965605:
                                            if (str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                                                i = 8;
                                                break;
                                            }
                                        case 889106340:
                                            if (str.equals("REQ_INFO_EMAIL_INVALID")) {
                                                i = 2;
                                                break;
                                            }
                                        default:
                                    }
                                    i = -1;
                                    switch (i) {
                                        case 0:
                                            PaymentFormActivity.this.shakeField(6);
                                            return;
                                        case 1:
                                            PaymentFormActivity.this.shakeField(9);
                                            return;
                                        case 2:
                                            PaymentFormActivity.this.shakeField(7);
                                            return;
                                        case 3:
                                            PaymentFormActivity.this.shakeField(4);
                                            return;
                                        case 4:
                                            PaymentFormActivity.this.shakeField(2);
                                            return;
                                        case 5:
                                            PaymentFormActivity.this.shakeField(5);
                                            return;
                                        case 6:
                                            PaymentFormActivity.this.shakeField(3);
                                            return;
                                        case 7:
                                            PaymentFormActivity.this.shakeField(0);
                                            return;
                                        case 8:
                                            PaymentFormActivity.this.shakeField(1);
                                            return;
                                        default:
                                            AlertsCreator.processError(PaymentFormActivity.this.currentAccount, tL_error, PaymentFormActivity.this, tLObject, new Object[0]);
                                            return;
                                    }
                                }
                            }
                        });
                    }
                }
            }, 2);
        }
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
            tL_paymentRequestedInfo.shipping_address.country_iso2 = this.countryName != null ? this.countryName : TtmlNode.ANONYMOUS_REGION_ID;
            tL_paymentRequestedInfo.shipping_address.post_code = this.inputFields[5].getText().toString();
            tL_paymentRequestedInfo.flags |= 8;
        }
        return tL_paymentRequestedInfo;
    }

    private void sendData() {
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            final TLObject tL_payments_sendPaymentForm = new TL_payments_sendPaymentForm();
            tL_payments_sendPaymentForm.msg_id = this.messageObject.getId();
            if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && this.paymentForm.saved_credentials != null) {
                tL_payments_sendPaymentForm.credentials = new TL_inputPaymentCredentialsSaved();
                tL_payments_sendPaymentForm.credentials.id = this.paymentForm.saved_credentials.id;
                tL_payments_sendPaymentForm.credentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            } else if (this.androidPayCredentials != null) {
                tL_payments_sendPaymentForm.credentials = this.androidPayCredentials;
            } else {
                tL_payments_sendPaymentForm.credentials = new TL_inputPaymentCredentials();
                tL_payments_sendPaymentForm.credentials.save = this.saveCardInfo;
                tL_payments_sendPaymentForm.credentials.data = new TL_dataJSON();
                tL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
            }
            if (!(this.requestedInfo == null || this.requestedInfo.id == null)) {
                tL_payments_sendPaymentForm.requested_info_id = this.requestedInfo.id;
                tL_payments_sendPaymentForm.flags = 1 | tL_payments_sendPaymentForm.flags;
            }
            if (this.shippingOption != null) {
                tL_payments_sendPaymentForm.shipping_option_id = this.shippingOption.id;
                tL_payments_sendPaymentForm.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendPaymentForm, new RequestDelegate() {

                /* renamed from: org.telegram.ui.PaymentFormActivity$35$1 */
                class C15661 implements Runnable {
                    C15661() {
                    }

                    public void run() {
                        PaymentFormActivity.this.goToNextStep();
                    }
                }

                public void run(final TLObject tLObject, final TL_error tL_error) {
                    if (tLObject == null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(PaymentFormActivity.this.currentAccount, tL_error, PaymentFormActivity.this, tL_payments_sendPaymentForm, new Object[0]);
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(false, false);
                            }
                        });
                    } else if ((tLObject instanceof TL_payments_paymentResult) != null) {
                        MessagesController.getInstance(PaymentFormActivity.this.currentAccount).processUpdates(((TL_payments_paymentResult) tLObject).updates, false);
                        AndroidUtilities.runOnUIThread(new C15661());
                    } else if ((tLObject instanceof TL_payments_paymentVerficationNeeded) != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(PaymentFormActivity.this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.webView.setVisibility(0);
                                PaymentFormActivity.this.webviewLoading = true;
                                PaymentFormActivity.this.showEditDoneProgress(true, true);
                                PaymentFormActivity.this.progressView.setVisibility(0);
                                PaymentFormActivity.this.doneItem.setEnabled(false);
                                PaymentFormActivity.this.doneItem.getImageView().setVisibility(4);
                                PaymentFormActivity.this.webView.loadUrl(((TL_payments_paymentVerficationNeeded) tLObject).url);
                            }
                        });
                    }
                }
            }, 2);
        }
    }

    private void shakeField(int i) {
        Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.inputFields[i], 2.0f, 0);
    }

    private void setDonePressed(boolean z) {
        this.donePressed = z;
        this.swipeBackEnabled = z ^ 1;
        this.actionBar.getBackButton().setEnabled(this.donePressed ^ 1);
        if (this.detailSettingsCell[0]) {
            this.detailSettingsCell[0].setEnabled(this.donePressed ^ 1);
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
            final String obj = this.inputFields[1].getText().toString();
            showEditDoneProgress(true, true);
            setDonePressed(true);
            final TLObject tL_account_getPassword = new TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPassword, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tL_error != null) {
                                AlertsCreator.processError(PaymentFormActivity.this.currentAccount, tL_error, PaymentFormActivity.this, tL_account_getPassword, new Object[0]);
                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                PaymentFormActivity.this.setDonePressed(false);
                            } else if (tLObject instanceof TL_account_noPassword) {
                                PaymentFormActivity.this.passwordOk = false;
                                PaymentFormActivity.this.goToNextStep();
                            } else {
                                TL_account_password tL_account_password = (TL_account_password) tLObject;
                                Object obj = null;
                                try {
                                    obj = obj.getBytes(C0542C.UTF8_NAME);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                Object obj2 = new byte[((tL_account_password.current_salt.length * 2) + obj.length)];
                                System.arraycopy(tL_account_password.current_salt, 0, obj2, 0, tL_account_password.current_salt.length);
                                System.arraycopy(obj, 0, obj2, tL_account_password.current_salt.length, obj.length);
                                System.arraycopy(tL_account_password.current_salt, 0, obj2, obj2.length - tL_account_password.current_salt.length, tL_account_password.current_salt.length);
                                final TLObject tL_account_getTmpPassword = new TL_account_getTmpPassword();
                                tL_account_getTmpPassword.password_hash = Utilities.computeSHA256(obj2, 0, obj2.length);
                                tL_account_getTmpPassword.period = 1800;
                                ConnectionsManager.getInstance(PaymentFormActivity.this.currentAccount).sendRequest(tL_account_getTmpPassword, new RequestDelegate() {
                                    public void run(final TLObject tLObject, final TL_error tL_error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                                PaymentFormActivity.this.setDonePressed(false);
                                                if (tLObject != null) {
                                                    PaymentFormActivity.this.passwordOk = true;
                                                    UserConfig.getInstance(PaymentFormActivity.this.currentAccount).tmpPassword = (TL_account_tmpPassword) tLObject;
                                                    UserConfig.getInstance(PaymentFormActivity.this.currentAccount).saveConfig(false);
                                                    PaymentFormActivity.this.goToNextStep();
                                                } else if (tL_error.text.equals("PASSWORD_HASH_INVALID")) {
                                                    Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                                                    if (vibrator != null) {
                                                        vibrator.vibrate(200);
                                                    }
                                                    AndroidUtilities.shakeView(PaymentFormActivity.this.inputFields[1], 2.0f, 0);
                                                    PaymentFormActivity.this.inputFields[1].setText(TtmlNode.ANONYMOUS_REGION_ID);
                                                } else {
                                                    AlertsCreator.processError(PaymentFormActivity.this.currentAccount, tL_error, PaymentFormActivity.this, tL_account_getTmpPassword, new Object[0]);
                                                }
                                            }
                                        });
                                    }
                                }, 2);
                            }
                        }
                    });
                }
            }, 2);
        }
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z2;
        if (this.doneItemAnimation != null) {
            r0.doneItemAnimation.cancel();
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (z && r0.doneItem != null) {
            r0.doneItemAnimation = new AnimatorSet();
            if (z3) {
                r0.progressView.setVisibility(0);
                r0.doneItem.setEnabled(false);
                AnimatorSet animatorSet2 = r0.doneItemAnimation;
                r6 = new Animator[6];
                r6[0] = ObjectAnimator.ofFloat(r0.doneItem.getImageView(), "scaleX", new float[]{0.1f});
                r6[1] = ObjectAnimator.ofFloat(r0.doneItem.getImageView(), "scaleY", new float[]{0.1f});
                r6[2] = ObjectAnimator.ofFloat(r0.doneItem.getImageView(), "alpha", new float[]{0.0f});
                r6[3] = ObjectAnimator.ofFloat(r0.progressView, "scaleX", new float[]{1.0f});
                r6[4] = ObjectAnimator.ofFloat(r0.progressView, "scaleY", new float[]{1.0f});
                r6[5] = ObjectAnimator.ofFloat(r0.progressView, "alpha", new float[]{1.0f});
                animatorSet2.playTogether(r6);
            } else if (r0.webView != null) {
                animatorSet = r0.doneItemAnimation;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.progressView, "alpha", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                r0.doneItem.getImageView().setVisibility(0);
                r0.doneItem.setEnabled(true);
                animatorSet = r0.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.progressView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.doneItem.getImageView(), "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.doneItem.getImageView(), "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.doneItem.getImageView(), "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            r0.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator) != null) {
                        if (z3 == null) {
                            PaymentFormActivity.this.progressView.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.doneItem.getImageView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator) != null) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            r0.doneItemAnimation.setDuration(150);
            r0.doneItemAnimation.start();
        } else if (r0.payTextView != null) {
            r0.doneItemAnimation = new AnimatorSet();
            if (z3) {
                r0.progressViewButton.setVisibility(0);
                r0.bottomLayout.setEnabled(false);
                animatorSet = r0.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.payTextView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.payTextView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.payTextView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.progressViewButton, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.progressViewButton, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.progressViewButton, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                r0.payTextView.setVisibility(0);
                r0.bottomLayout.setEnabled(true);
                animatorSet = r0.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.progressViewButton, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.progressViewButton, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.progressViewButton, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.payTextView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.payTextView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.payTextView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            r0.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator) != null) {
                        if (z3 == null) {
                            PaymentFormActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PaymentFormActivity.this.payTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animator) != null) {
                        PaymentFormActivity.this.doneItemAnimation = null;
                    }
                }
            });
            r0.doneItemAnimation.setDuration(150);
            r0.doneItemAnimation.start();
        }
    }

    public boolean onBackPressed() {
        return this.donePressed ^ 1;
    }

    public ThemeDescription[] getThemeDescriptions() {
        int i;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        if (this.inputFields != null) {
            for (i = 0; i < r0.inputFields.length; i++) {
                arrayList.add(new ThemeDescription((View) r0.inputFields[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(r0.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(r0.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        if (r0.radioCells != null) {
            for (i = 0; i < r0.radioCells.length; i++) {
                arrayList.add(new ThemeDescription(r0.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(r0.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
                arrayList.add(new ThemeDescription(r0.radioCells[i], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(r0.radioCells[i], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
                arrayList.add(new ThemeDescription(r0.radioCells[i], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
            }
        } else {
            arrayList.add(new ThemeDescription(null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
        }
        for (i = 0; i < r0.headerCell.length; i++) {
            arrayList.add(new ThemeDescription(r0.headerCell[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(r0.headerCell[i], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        }
        for (View themeDescription : r0.sectionCell) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        for (i = 0; i < r0.bottomCell.length; i++) {
            arrayList.add(new ThemeDescription(r0.bottomCell[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(r0.bottomCell[i], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(r0.bottomCell[i], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        }
        for (i = 0; i < r0.dividers.size(); i++) {
            arrayList.add(new ThemeDescription((View) r0.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        }
        arrayList.add(new ThemeDescription(r0.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(r0.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(r0.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(r0.settingsCell1, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));
        arrayList.add(new ThemeDescription(r0.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(r0.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(r0.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        for (i = 1; i < r0.detailSettingsCell.length; i++) {
            arrayList.add(new ThemeDescription(r0.detailSettingsCell[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(r0.detailSettingsCell[i], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(r0.detailSettingsCell[i], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        arrayList.add(new ThemeDescription(r0.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(r0.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
