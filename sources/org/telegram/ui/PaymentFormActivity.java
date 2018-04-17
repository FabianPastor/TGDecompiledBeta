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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
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
import com.stripe.android.net.TokenParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
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
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
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
import org.telegram.ui.ActionBar.ActionBarMenu;
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
import org.telegram.ui.Components.LayoutHelper;
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
    class C15542 implements Comparator<String> {
        C15542() {
        }

        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$3 */
    class C15653 implements OnTouchListener {

        /* renamed from: org.telegram.ui.PaymentFormActivity$3$1 */
        class C22121 implements CountrySelectActivityDelegate {
            C22121() {
            }

            public void didSelectCountry(String name, String shortName) {
                PaymentFormActivity.this.inputFields[4].setText(name);
                PaymentFormActivity.this.countryName = shortName;
            }
        }

        C15653() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (PaymentFormActivity.this.getParentActivity() == null) {
                return false;
            }
            if (event.getAction() == 1) {
                CountrySelectActivity fragment = new CountrySelectActivity(false);
                fragment.setCountrySelectActivityDelegate(new C22121());
                PaymentFormActivity.this.presentFragment(fragment);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$4 */
    class C15664 implements TextWatcher {
        C15664() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (!PaymentFormActivity.this.ignoreOnTextChange) {
                PaymentFormActivity.this.ignoreOnTextChange = true;
                String text = PhoneFormat.stripExceptNumbers(PaymentFormActivity.this.inputFields[8].getText().toString());
                PaymentFormActivity.this.inputFields[8].setText(text);
                HintEditText phoneField = PaymentFormActivity.this.inputFields[9];
                if (text.length() == 0) {
                    phoneField.setHintText(null);
                    phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                } else {
                    String sub;
                    boolean ok = false;
                    String textToSet = null;
                    int a = 4;
                    if (text.length() > 4) {
                        int a2;
                        while (true) {
                            a2 = a;
                            if (a2 < 1) {
                                break;
                            }
                            sub = text.substring(0, a2);
                            if (((String) PaymentFormActivity.this.codesMap.get(sub)) != null) {
                                break;
                            }
                            a = a2 - 1;
                        }
                        ok = true;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(text.substring(a2, text.length()));
                        stringBuilder.append(PaymentFormActivity.this.inputFields[9].getText().toString());
                        textToSet = stringBuilder.toString();
                        text = sub;
                        PaymentFormActivity.this.inputFields[8].setText(sub);
                        if (!ok) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(text.substring(1, text.length()));
                            stringBuilder2.append(PaymentFormActivity.this.inputFields[9].getText().toString());
                            textToSet = stringBuilder2.toString();
                            EditTextBoldCursor editTextBoldCursor = PaymentFormActivity.this.inputFields[8];
                            CharSequence substring = text.substring(0, 1);
                            text = substring;
                            editTextBoldCursor.setText(substring);
                        }
                    }
                    String country = (String) PaymentFormActivity.this.codesMap.get(text);
                    boolean set = false;
                    if (!(country == null || PaymentFormActivity.this.countriesArray.indexOf(country) == -1)) {
                        sub = (String) PaymentFormActivity.this.phoneFormatMap.get(text);
                        if (sub != null) {
                            set = true;
                            phoneField.setHintText(sub.replace('X', '\u2013'));
                            phoneField.setHint(null);
                        }
                    }
                    if (!set) {
                        phoneField.setHintText(null);
                        phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                    }
                    if (!ok) {
                        PaymentFormActivity.this.inputFields[8].setSelection(PaymentFormActivity.this.inputFields[8].getText().length());
                    }
                    if (textToSet != null) {
                        phoneField.requestFocus();
                        phoneField.setText(textToSet);
                        phoneField.setSelection(phoneField.length());
                    }
                }
                PaymentFormActivity.this.ignoreOnTextChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$5 */
    class C15675 implements TextWatcher {
        private int actionPosition;
        private int characterAction = -1;

        C15675() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count == 0 && after == 1) {
                this.characterAction = 1;
            } else if (count != 1 || after != 0) {
                this.characterAction = -1;
            } else if (s.charAt(start) != ' ' || start <= 0) {
                this.characterAction = 2;
            } else {
                this.characterAction = 3;
                this.actionPosition = start - 1;
            }
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (!PaymentFormActivity.this.ignoreOnPhoneChange) {
                StringBuilder stringBuilder;
                HintEditText phoneField = PaymentFormActivity.this.inputFields[9];
                int start = phoneField.getSelectionStart();
                String phoneChars = "0123456789";
                String str = phoneField.getText().toString();
                if (this.characterAction == 3) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str.substring(0, this.actionPosition));
                    stringBuilder.append(str.substring(this.actionPosition + 1, str.length()));
                    str = stringBuilder.toString();
                    start--;
                }
                stringBuilder = new StringBuilder(str.length());
                for (int a = 0; a < str.length(); a++) {
                    String ch = str.substring(a, a + 1);
                    if (phoneChars.contains(ch)) {
                        stringBuilder.append(ch);
                    }
                }
                PaymentFormActivity.this.ignoreOnPhoneChange = true;
                String hint = phoneField.getHintText();
                if (hint != null) {
                    int start2 = start;
                    start = 0;
                    while (start < stringBuilder.length()) {
                        if (start < hint.length()) {
                            if (hint.charAt(start) == ' ') {
                                stringBuilder.insert(start, ' ');
                                start++;
                                if (!(start2 != start || this.characterAction == 2 || this.characterAction == 3)) {
                                    start2++;
                                }
                            }
                            start++;
                        } else {
                            stringBuilder.insert(start, ' ');
                            if (!(start2 != start + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                start = start2 + 1;
                            }
                            start = start2;
                        }
                    }
                    start = start2;
                }
                phoneField.setText(stringBuilder);
                if (start >= 0) {
                    phoneField.setSelection(start <= phoneField.length() ? start : phoneField.length());
                }
                phoneField.onTextChange();
                PaymentFormActivity.this.ignoreOnPhoneChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$6 */
    class C15686 implements OnEditorActionListener {
        C15686() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                int num = ((Integer) textView.getTag()).intValue();
                while (num + 1 < PaymentFormActivity.this.inputFields.length) {
                    num++;
                    if (num != 4 && ((View) PaymentFormActivity.this.inputFields[num].getParent()).getVisibility() == 0) {
                        PaymentFormActivity.this.inputFields[num].requestFocus();
                        break;
                    }
                }
                return true;
            } else if (i != 6) {
                return false;
            } else {
                PaymentFormActivity.this.doneItem.performClick();
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$7 */
    class C15697 implements OnClickListener {
        C15697() {
        }

        public void onClick(View v) {
            PaymentFormActivity.this.saveShippingInfo = PaymentFormActivity.this.saveShippingInfo ^ 1;
            PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveShippingInfo);
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$9 */
    class C15719 extends WebViewClient {
        C15719() {
        }

        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            PaymentFormActivity.this.webviewLoading = false;
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.updateSavePaymentField();
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return false;
            }
        }
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

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(account_Password account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(final String eventName, final String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (PaymentFormActivity.this.getParentActivity() != null && eventName.equals("payment_form_submit")) {
                        try {
                            JSONObject jsonObject = new JSONObject(eventData);
                            PaymentFormActivity.this.paymentJson = jsonObject.getJSONObject("credentials").toString();
                            PaymentFormActivity.this.cardName = jsonObject.getString("title");
                        } catch (Throwable e) {
                            PaymentFormActivity.this.paymentJson = eventData;
                            FileLog.m3e(e);
                        }
                        PaymentFormActivity.this.goToNextStep();
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$1 */
    class C22101 extends ActionBarMenuOnItemClick {
        C22101() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onItemClick(int id) {
            if (id == -1) {
                if (!PaymentFormActivity.this.donePressed) {
                    PaymentFormActivity.this.finishFragment();
                }
            } else if (id == 1 && !PaymentFormActivity.this.donePressed) {
                if (PaymentFormActivity.this.currentStep != 3) {
                    AndroidUtilities.hideKeyboard(PaymentFormActivity.this.getParentActivity().getCurrentFocus());
                }
                if (PaymentFormActivity.this.currentStep == 0) {
                    PaymentFormActivity.this.setDonePressed(true);
                    PaymentFormActivity.this.sendForm();
                } else {
                    int a = 0;
                    if (PaymentFormActivity.this.currentStep == 1) {
                        while (true) {
                            int a2 = a;
                            if (a2 >= PaymentFormActivity.this.radioCells.length) {
                                break;
                            } else if (PaymentFormActivity.this.radioCells[a2].isChecked()) {
                                break;
                            } else {
                                a = a2 + 1;
                            }
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

    public PaymentFormActivity(MessageObject message, TL_payments_paymentReceipt receipt) {
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
            this.currentBotName = TtmlNode.ANONYMOUS_REGION_ID;
        }
        this.currentItemName = message.messageOwner.media.title;
        if (receipt.info != null) {
            this.validateRequest = new TL_payments_validateRequestedInfo();
            this.validateRequest.info = receipt.info;
        }
        this.cardName = receipt.credentials_title;
    }

    public PaymentFormActivity(TL_payments_paymentForm form, MessageObject message) {
        int i;
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        if (!(form.invoice.shipping_address_requested || form.invoice.email_requested || form.invoice.name_requested)) {
            if (!form.invoice.phone_requested) {
                if (form.saved_credentials != null) {
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null && UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60) {
                        UserConfig.getInstance(this.currentAccount).tmpPassword = null;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    }
                    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
                        i = 4;
                    } else {
                        i = 3;
                    }
                } else {
                    i = 2;
                }
                init(form, message, i, null, null, null, null, null, false, null);
            }
        }
        i = 0;
        init(form, message, i, null, null, null, null, null, false, null);
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
        this.detailSettingsCell = new TextDetailSettingsCell[7];
        init(form, message, step, validatedRequestedInfo, shipping, tokenJson, card, request, saveCard, androidPay);
    }

    private void setCurrentPassword(account_Password password) {
        if (!(password instanceof TL_account_password)) {
            this.currentPassword = password;
            if (this.currentPassword != null) {
                this.waitingForEmail = this.currentPassword.email_unconfirmed_pattern.length() > 0;
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
        this.currentStep = step;
        this.paymentJson = tokenJson;
        this.androidPayCredentials = androidPay;
        this.requestedInfo = validatedRequestedInfo;
        this.paymentForm = form;
        this.shippingOption = shipping;
        this.messageObject = message;
        this.saveCardInfo = saveCard;
        boolean z = true;
        this.isWebView = "stripe".equals(this.paymentForm.native_provider) ^ true;
        this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(form.bot_id));
        if (this.botUser != null) {
            this.currentBotName = this.botUser.first_name;
        } else {
            this.currentBotName = TtmlNode.ANONYMOUS_REGION_ID;
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
                    getParentActivity().getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
                } else if (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
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

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        Exception exception;
        Context context2 = context;
        boolean z = true;
        if (this.currentStep == 0) {
            r1.actionBar.setTitle(LocaleController.getString("PaymentShippingInfo", R.string.PaymentShippingInfo));
        } else if (r1.currentStep == 1) {
            r1.actionBar.setTitle(LocaleController.getString("PaymentShippingMethod", R.string.PaymentShippingMethod));
        } else if (r1.currentStep == 2) {
            r1.actionBar.setTitle(LocaleController.getString("PaymentCardInfo", R.string.PaymentCardInfo));
        } else if (r1.currentStep == 3) {
            r1.actionBar.setTitle(LocaleController.getString("PaymentCardInfo", R.string.PaymentCardInfo));
        } else if (r1.currentStep == 4) {
            if (r1.paymentForm.invoice.test) {
                r3 = r1.actionBar;
                r10 = new StringBuilder();
                r10.append("Test ");
                r10.append(LocaleController.getString("PaymentCheckout", R.string.PaymentCheckout));
                r3.setTitle(r10.toString());
            } else {
                r1.actionBar.setTitle(LocaleController.getString("PaymentCheckout", R.string.PaymentCheckout));
            }
        } else if (r1.currentStep == 5) {
            if (r1.paymentForm.invoice.test) {
                r3 = r1.actionBar;
                r10 = new StringBuilder();
                r10.append("Test ");
                r10.append(LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt));
                r3.setTitle(r10.toString());
            } else {
                r1.actionBar.setTitle(LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt));
            }
        } else if (r1.currentStep == 6) {
            r1.actionBar.setTitle(LocaleController.getString("PaymentPassword", R.string.PaymentPassword));
        }
        r1.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        r1.actionBar.setAllowOverlayTitle(true);
        r1.actionBar.setActionBarMenuOnItemClick(new C22101());
        ActionBarMenu menu = r1.actionBar.createMenu();
        if (r1.currentStep == 0 || r1.currentStep == 1 || r1.currentStep == 2 || r1.currentStep == 3 || r1.currentStep == 4 || r1.currentStep == 6) {
            r1.doneItem = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            r1.progressView = new ContextProgressView(context2, 1);
            r1.doneItem.addView(r1.progressView, LayoutHelper.createFrame(-1, -1.0f));
            r1.progressView.setVisibility(4);
        }
        r1.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = r1.fragmentView;
        r1.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        r1.scrollView = new ScrollView(context2);
        r1.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(r1.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(r1.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, r1.currentStep == 4 ? 48.0f : 0.0f));
        r1.linearLayout2 = new LinearLayout(context2);
        r1.linearLayout2.setOrientation(1);
        r1.scrollView.addView(r1.linearLayout2, new LayoutParams(-1, -2));
        int i = 0;
        String line;
        int a;
        ViewGroup container;
        View divider;
        String value;
        int b;
        User user;
        if (r1.currentStep == 0) {
            String readLine;
            TelephonyManager telephonyManager;
            String countryName;
            HashMap<String, String> languageMap = new HashMap();
            HashMap<String, String> countryMap = new HashMap();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
                while (true) {
                    readLine = reader.readLine();
                    line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    r1.countriesArray.add(i, args[2]);
                    r1.countriesMap.put(args[2], args[i]);
                    r1.codesMap.put(args[i], args[2]);
                    countryMap.put(args[1], args[2]);
                    if (args.length > 3) {
                        r1.phoneFormatMap.put(args[i], args[3]);
                    }
                    languageMap.put(args[1], args[2]);
                    i = 0;
                }
                reader.close();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            Collections.sort(r1.countriesArray, new C15542());
            r1.inputFields = new EditTextBoldCursor[10];
            a = 0;
            while (a < 10) {
                InputFilter[] inputFilters;
                User providerUser;
                String providerName;
                if (a == 0) {
                    r1.headerCell[0] = new HeaderCell(context2);
                    r1.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.headerCell[0].setText(LocaleController.getString("PaymentShippingAddress", R.string.PaymentShippingAddress));
                    r1.linearLayout2.addView(r1.headerCell[0], LayoutHelper.createLinear(-1, -2));
                } else if (a == 6) {
                    r1.sectionCell[0] = new ShadowSectionCell(context2);
                    r1.linearLayout2.addView(r1.sectionCell[0], LayoutHelper.createLinear(-1, -2));
                    r1.headerCell[z] = new HeaderCell(context2);
                    r1.headerCell[z].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.headerCell[z].setText(LocaleController.getString("PaymentShippingReceiver", R.string.PaymentShippingReceiver));
                    r1.linearLayout2.addView(r1.headerCell[z], LayoutHelper.createLinear(-1, -2));
                }
                if (a == 8) {
                    container = new LinearLayout(context2);
                    ((LinearLayout) container).setOrientation(0);
                    r1.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
                    container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                } else if (a == 9) {
                    container = (ViewGroup) r1.inputFields[8].getParent();
                } else {
                    container = new FrameLayout(context2);
                    r1.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
                    container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    boolean allowDivider = (a == 5 || a == 9) ? false : z;
                    if (allowDivider) {
                        if (a == 7 && !r1.paymentForm.invoice.phone_requested) {
                            allowDivider = false;
                        } else if (!(a != 6 || r1.paymentForm.invoice.phone_requested || r1.paymentForm.invoice.email_requested)) {
                            allowDivider = false;
                        }
                    }
                    if (allowDivider) {
                        divider = new View(context2);
                        r1.dividers.add(divider);
                        divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                        container.addView(divider, new LayoutParams(-1, z, 83));
                    }
                }
                if (a == 9) {
                    r1.inputFields[a] = new HintEditText(context2);
                } else {
                    r1.inputFields[a] = new EditTextBoldCursor(context2);
                }
                r1.inputFields[a].setTag(Integer.valueOf(a));
                r1.inputFields[a].setTextSize(z, 16.0f);
                r1.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                r1.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                r1.inputFields[a].setBackgroundDrawable(null);
                r1.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                r1.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                r1.inputFields[a].setCursorWidth(1.5f);
                if (a == 4) {
                    r1.inputFields[a].setOnTouchListener(new C15653());
                    r1.inputFields[a].setInputType(0);
                }
                if (a != 9) {
                    if (a != 8) {
                        if (a == 7) {
                            r1.inputFields[a].setInputType(z);
                        } else {
                            r1.inputFields[a].setInputType(16385);
                        }
                        r1.inputFields[a].setImeOptions(268435461);
                        switch (a) {
                            case 0:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingAddress1Placeholder", R.string.PaymentShippingAddress1Placeholder));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.shipping_address == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.street_line1);
                                    break;
                                }
                            case 1:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingAddress2Placeholder", R.string.PaymentShippingAddress2Placeholder));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.shipping_address == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.street_line2);
                                    break;
                                }
                            case 2:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingCityPlaceholder", R.string.PaymentShippingCityPlaceholder));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.shipping_address == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.city);
                                    break;
                                }
                            case 3:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingStatePlaceholder", R.string.PaymentShippingStatePlaceholder));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.shipping_address == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.state);
                                    break;
                                }
                            case 4:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingCountry", R.string.PaymentShippingCountry));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.shipping_address == null)) {
                                    value = (String) countryMap.get(r1.paymentForm.saved_info.shipping_address.country_iso2);
                                    r1.countryName = r1.paymentForm.saved_info.shipping_address.country_iso2;
                                    r1.inputFields[a].setText(value == null ? value : r1.countryName);
                                    break;
                                }
                            case 5:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingZipPlaceholder", R.string.PaymentShippingZipPlaceholder));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.shipping_address == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.post_code);
                                    break;
                                }
                            case 6:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingName", R.string.PaymentShippingName));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.name == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.name);
                                    break;
                                }
                            case 7:
                                r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
                                if (!(r1.paymentForm.saved_info == null || r1.paymentForm.saved_info.email == null)) {
                                    r1.inputFields[a].setText(r1.paymentForm.saved_info.email);
                                    break;
                                }
                            default:
                                break;
                        }
                        r1.inputFields[a].setSelection(r1.inputFields[a].length());
                        if (a == 8) {
                            r1.textView = new TextView(context2);
                            r1.textView.setText("+");
                            r1.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            r1.textView.setTextSize(z, 16.0f);
                            container.addView(r1.textView, LayoutHelper.createLinear(-2, -2, 17.0f, 12.0f, 0.0f, 6.0f));
                            r1.inputFields[a].setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
                            r1.inputFields[a].setGravity(19);
                            inputFilters = new InputFilter[z];
                            inputFilters[0] = new LengthFilter(5);
                            r1.inputFields[a].setFilters(inputFilters);
                            container.addView(r1.inputFields[a], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                            r1.inputFields[a].addTextChangedListener(new C15664());
                        } else if (a != 9) {
                            r1.inputFields[a].setPadding(0, 0, 0, 0);
                            r1.inputFields[a].setGravity(19);
                            container.addView(r1.inputFields[a], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 17.0f, 6.0f));
                            r1.inputFields[a].addTextChangedListener(new C15675());
                        } else {
                            r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                            r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                            container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                        }
                        r1.inputFields[a].setOnEditorActionListener(new C15686());
                        if (a != 9) {
                            if (!r1.paymentForm.invoice.email_to_provider) {
                                if (r1.paymentForm.invoice.phone_to_provider) {
                                    r1.sectionCell[z] = new ShadowSectionCell(context2);
                                    r1.linearLayout2.addView(r1.sectionCell[z], LayoutHelper.createLinear(-1, -2));
                                    r1.checkCell1 = new TextCheckCell(context2);
                                    r1.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                                    r1.checkCell1.setTextAndCheck(LocaleController.getString("PaymentShippingSave", R.string.PaymentShippingSave), r1.saveShippingInfo, false);
                                    r1.linearLayout2.addView(r1.checkCell1, LayoutHelper.createLinear(-1, -2));
                                    r1.checkCell1.setOnClickListener(new C15697());
                                    r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                                    r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                    r1.bottomCell[0].setText(LocaleController.getString("PaymentShippingSaveInfo", R.string.PaymentShippingSaveInfo));
                                    r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                                }
                            }
                            providerUser = null;
                            for (b = 0; b < r1.paymentForm.users.size(); b++) {
                                user = (User) r1.paymentForm.users.get(b);
                                if (user.id == r1.paymentForm.provider_id) {
                                    providerUser = user;
                                }
                            }
                            if (providerUser == null) {
                                providerName = ContactsController.formatName(providerUser.first_name, providerUser.last_name);
                            } else {
                                providerName = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            r1.bottomCell[z] = new TextInfoPrivacyCell(context2);
                            r1.bottomCell[z].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            r1.linearLayout2.addView(r1.bottomCell[z], LayoutHelper.createLinear(-1, -2));
                            if (!r1.paymentForm.invoice.email_to_provider && r1.paymentForm.invoice.phone_to_provider) {
                                r1.bottomCell[1].setText(LocaleController.formatString("PaymentPhoneEmailToProvider", R.string.PaymentPhoneEmailToProvider, providerName));
                            } else if (r1.paymentForm.invoice.email_to_provider) {
                                r1.bottomCell[1].setText(LocaleController.formatString("PaymentPhoneToProvider", R.string.PaymentPhoneToProvider, providerName));
                            } else {
                                r1.bottomCell[1].setText(LocaleController.formatString("PaymentEmailToProvider", R.string.PaymentEmailToProvider, providerName));
                            }
                            r1.checkCell1 = new TextCheckCell(context2);
                            r1.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                            r1.checkCell1.setTextAndCheck(LocaleController.getString("PaymentShippingSave", R.string.PaymentShippingSave), r1.saveShippingInfo, false);
                            r1.linearLayout2.addView(r1.checkCell1, LayoutHelper.createLinear(-1, -2));
                            r1.checkCell1.setOnClickListener(new C15697());
                            r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                            r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            r1.bottomCell[0].setText(LocaleController.getString("PaymentShippingSaveInfo", R.string.PaymentShippingSaveInfo));
                            r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                        }
                        a++;
                        z = true;
                    }
                }
                r1.inputFields[a].setInputType(3);
                r1.inputFields[a].setImeOptions(268435461);
                switch (a) {
                    case 0:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingAddress1Placeholder", R.string.PaymentShippingAddress1Placeholder));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.street_line1);
                        break;
                    case 1:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingAddress2Placeholder", R.string.PaymentShippingAddress2Placeholder));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.street_line2);
                        break;
                    case 2:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingCityPlaceholder", R.string.PaymentShippingCityPlaceholder));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.city);
                        break;
                    case 3:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingStatePlaceholder", R.string.PaymentShippingStatePlaceholder));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.state);
                        break;
                    case 4:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingCountry", R.string.PaymentShippingCountry));
                        value = (String) countryMap.get(r1.paymentForm.saved_info.shipping_address.country_iso2);
                        r1.countryName = r1.paymentForm.saved_info.shipping_address.country_iso2;
                        if (value == null) {
                        }
                        r1.inputFields[a].setText(value == null ? value : r1.countryName);
                        break;
                    case 5:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingZipPlaceholder", R.string.PaymentShippingZipPlaceholder));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.shipping_address.post_code);
                        break;
                    case 6:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingName", R.string.PaymentShippingName));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.name);
                        break;
                    case 7:
                        r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
                        r1.inputFields[a].setText(r1.paymentForm.saved_info.email);
                        break;
                    default:
                        break;
                }
                r1.inputFields[a].setSelection(r1.inputFields[a].length());
                if (a == 8) {
                    r1.textView = new TextView(context2);
                    r1.textView.setText("+");
                    r1.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    r1.textView.setTextSize(z, 16.0f);
                    container.addView(r1.textView, LayoutHelper.createLinear(-2, -2, 17.0f, 12.0f, 0.0f, 6.0f));
                    r1.inputFields[a].setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
                    r1.inputFields[a].setGravity(19);
                    inputFilters = new InputFilter[z];
                    inputFilters[0] = new LengthFilter(5);
                    r1.inputFields[a].setFilters(inputFilters);
                    container.addView(r1.inputFields[a], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                    r1.inputFields[a].addTextChangedListener(new C15664());
                } else if (a != 9) {
                    r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                    if (LocaleController.isRTL) {
                    }
                    r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                    container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                } else {
                    r1.inputFields[a].setPadding(0, 0, 0, 0);
                    r1.inputFields[a].setGravity(19);
                    container.addView(r1.inputFields[a], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 17.0f, 6.0f));
                    r1.inputFields[a].addTextChangedListener(new C15675());
                }
                r1.inputFields[a].setOnEditorActionListener(new C15686());
                if (a != 9) {
                    if (r1.paymentForm.invoice.email_to_provider) {
                        if (r1.paymentForm.invoice.phone_to_provider) {
                            r1.sectionCell[z] = new ShadowSectionCell(context2);
                            r1.linearLayout2.addView(r1.sectionCell[z], LayoutHelper.createLinear(-1, -2));
                            r1.checkCell1 = new TextCheckCell(context2);
                            r1.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                            r1.checkCell1.setTextAndCheck(LocaleController.getString("PaymentShippingSave", R.string.PaymentShippingSave), r1.saveShippingInfo, false);
                            r1.linearLayout2.addView(r1.checkCell1, LayoutHelper.createLinear(-1, -2));
                            r1.checkCell1.setOnClickListener(new C15697());
                            r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                            r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            r1.bottomCell[0].setText(LocaleController.getString("PaymentShippingSaveInfo", R.string.PaymentShippingSaveInfo));
                            r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                        }
                    }
                    providerUser = null;
                    for (b = 0; b < r1.paymentForm.users.size(); b++) {
                        user = (User) r1.paymentForm.users.get(b);
                        if (user.id == r1.paymentForm.provider_id) {
                            providerUser = user;
                        }
                    }
                    if (providerUser == null) {
                        providerName = TtmlNode.ANONYMOUS_REGION_ID;
                    } else {
                        providerName = ContactsController.formatName(providerUser.first_name, providerUser.last_name);
                    }
                    r1.bottomCell[z] = new TextInfoPrivacyCell(context2);
                    r1.bottomCell[z].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    r1.linearLayout2.addView(r1.bottomCell[z], LayoutHelper.createLinear(-1, -2));
                    if (!r1.paymentForm.invoice.email_to_provider) {
                    }
                    if (r1.paymentForm.invoice.email_to_provider) {
                        r1.bottomCell[1].setText(LocaleController.formatString("PaymentPhoneToProvider", R.string.PaymentPhoneToProvider, providerName));
                    } else {
                        r1.bottomCell[1].setText(LocaleController.formatString("PaymentEmailToProvider", R.string.PaymentEmailToProvider, providerName));
                    }
                    r1.checkCell1 = new TextCheckCell(context2);
                    r1.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    r1.checkCell1.setTextAndCheck(LocaleController.getString("PaymentShippingSave", R.string.PaymentShippingSave), r1.saveShippingInfo, false);
                    r1.linearLayout2.addView(r1.checkCell1, LayoutHelper.createLinear(-1, -2));
                    r1.checkCell1.setOnClickListener(new C15697());
                    r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                    r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    r1.bottomCell[0].setText(LocaleController.getString("PaymentShippingSaveInfo", R.string.PaymentShippingSaveInfo));
                    r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                }
                a++;
                z = true;
            }
            if (r1.paymentForm.invoice.name_requested) {
                i = 8;
            } else {
                i = 8;
                ((ViewGroup) r1.inputFields[6].getParent()).setVisibility(8);
            }
            if (!r1.paymentForm.invoice.phone_requested) {
                ((ViewGroup) r1.inputFields[i].getParent()).setVisibility(i);
            }
            if (!r1.paymentForm.invoice.email_requested) {
                ((ViewGroup) r1.inputFields[7].getParent()).setVisibility(8);
            }
            if (r1.paymentForm.invoice.phone_requested) {
                r1.inputFields[9].setImeOptions(268435462);
            } else if (r1.paymentForm.invoice.email_requested) {
                r1.inputFields[7].setImeOptions(268435462);
            } else if (r1.paymentForm.invoice.name_requested) {
                r1.inputFields[6].setImeOptions(268435462);
            } else {
                r1.inputFields[5].setImeOptions(268435462);
            }
            if (r1.sectionCell[1] != null) {
                ShadowSectionCell shadowSectionCell = r1.sectionCell[1];
                if (!(r1.paymentForm.invoice.name_requested || r1.paymentForm.invoice.phone_requested)) {
                    if (!r1.paymentForm.invoice.email_requested) {
                        i = 8;
                        shadowSectionCell.setVisibility(i);
                    }
                }
                i = 0;
                shadowSectionCell.setVisibility(i);
            } else if (r1.bottomCell[1] != null) {
                TextInfoPrivacyCell textInfoPrivacyCell = r1.bottomCell[1];
                if (!(r1.paymentForm.invoice.name_requested || r1.paymentForm.invoice.phone_requested)) {
                    if (!r1.paymentForm.invoice.email_requested) {
                        i = 8;
                        textInfoPrivacyCell.setVisibility(i);
                    }
                }
                i = 0;
                textInfoPrivacyCell.setVisibility(i);
            }
            HeaderCell headerCell = r1.headerCell[1];
            if (!(r1.paymentForm.invoice.name_requested || r1.paymentForm.invoice.phone_requested)) {
                if (!r1.paymentForm.invoice.email_requested) {
                    i = 8;
                    headerCell.setVisibility(i);
                    if (!r1.paymentForm.invoice.shipping_address_requested) {
                        r1.headerCell[0].setVisibility(8);
                        r1.sectionCell[0].setVisibility(8);
                        ((ViewGroup) r1.inputFields[0].getParent()).setVisibility(8);
                        ((ViewGroup) r1.inputFields[1].getParent()).setVisibility(8);
                        ((ViewGroup) r1.inputFields[2].getParent()).setVisibility(8);
                        ((ViewGroup) r1.inputFields[3].getParent()).setVisibility(8);
                        ((ViewGroup) r1.inputFields[4].getParent()).setVisibility(8);
                        ((ViewGroup) r1.inputFields[5].getParent()).setVisibility(8);
                    }
                    if (r1.paymentForm.saved_info != null || TextUtils.isEmpty(r1.paymentForm.saved_info.phone)) {
                        fillNumber(null);
                    } else {
                        fillNumber(r1.paymentForm.saved_info.phone);
                    }
                    if (r1.inputFields[8].length() == 0 && r1.paymentForm.invoice.phone_requested && (r1.paymentForm.saved_info == null || TextUtils.isEmpty(r1.paymentForm.saved_info.phone))) {
                        readLine = null;
                        telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                        if (telephonyManager != null) {
                            readLine = telephonyManager.getSimCountryIso().toUpperCase();
                        }
                        if (readLine != null) {
                            countryName = (String) languageMap.get(readLine);
                            if (!(countryName == null || r1.countriesArray.indexOf(countryName) == -1)) {
                                r1.inputFields[8].setText((CharSequence) r1.countriesMap.get(countryName));
                            }
                        }
                    }
                }
            }
            i = 0;
            headerCell.setVisibility(i);
            if (r1.paymentForm.invoice.shipping_address_requested) {
                r1.headerCell[0].setVisibility(8);
                r1.sectionCell[0].setVisibility(8);
                ((ViewGroup) r1.inputFields[0].getParent()).setVisibility(8);
                ((ViewGroup) r1.inputFields[1].getParent()).setVisibility(8);
                ((ViewGroup) r1.inputFields[2].getParent()).setVisibility(8);
                ((ViewGroup) r1.inputFields[3].getParent()).setVisibility(8);
                ((ViewGroup) r1.inputFields[4].getParent()).setVisibility(8);
                ((ViewGroup) r1.inputFields[5].getParent()).setVisibility(8);
            }
            if (r1.paymentForm.saved_info != null) {
            }
            fillNumber(null);
            readLine = null;
            try {
                telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    readLine = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            if (readLine != null) {
                countryName = (String) languageMap.get(readLine);
                r1.inputFields[8].setText((CharSequence) r1.countriesMap.get(countryName));
            }
        } else if (r1.currentStep == 2) {
            JSONObject jsonObject;
            if (r1.paymentForm.native_params != null) {
                try {
                    jsonObject = new JSONObject(r1.paymentForm.native_params.data);
                    try {
                        androidPayKey = jsonObject.getString("android_pay_public_key");
                        if (!TextUtils.isEmpty(androidPayKey)) {
                            r1.androidPayPublicKey = androidPayKey;
                        }
                    } catch (Exception e3) {
                        r1.androidPayPublicKey = null;
                    }
                    try {
                        r1.androidPayBackgroundColor = jsonObject.getInt("android_pay_bgcolor") | Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
                    } catch (Exception e4) {
                        exception = e4;
                        r1.androidPayBackgroundColor = -1;
                    }
                    try {
                        r1.androidPayBlackTheme = jsonObject.getBoolean("android_pay_inverse");
                    } catch (Exception e42) {
                        exception = e42;
                        r1.androidPayBlackTheme = false;
                    }
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
            if (r1.isWebView) {
                if (r1.androidPayPublicKey != null) {
                    initAndroidPay(context);
                }
                r1.androidPayContainer = new FrameLayout(context2);
                r1.androidPayContainer.setId(fragment_container_id);
                r1.androidPayContainer.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                r1.androidPayContainer.setVisibility(8);
                r1.linearLayout2.addView(r1.androidPayContainer, LayoutHelper.createLinear(-1, 48));
                r1.webviewLoading = true;
                showEditDoneProgress(true, true);
                r1.progressView.setVisibility(0);
                r1.doneItem.setEnabled(false);
                r1.doneItem.getImageView().setVisibility(4);
                r1.webView = new WebView(context2) {
                    public boolean onTouchEvent(MotionEvent event) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouchEvent(event);
                    }
                };
                r1.webView.getSettings().setJavaScriptEnabled(true);
                r1.webView.getSettings().setDomStorageEnabled(true);
                if (VERSION.SDK_INT >= 21) {
                    r1.webView.getSettings().setMixedContentMode(0);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(r1.webView, true);
                }
                r1.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
                r1.webView.setWebViewClient(new C15719());
                r1.linearLayout2.addView(r1.webView, LayoutHelper.createFrame(-1, -2.0f));
                r1.sectionCell[2] = new ShadowSectionCell(context2);
                r1.linearLayout2.addView(r1.sectionCell[2], LayoutHelper.createLinear(-1, -2));
                r1.checkCell1 = new TextCheckCell(context2);
                r1.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                r1.checkCell1.setTextAndCheck(LocaleController.getString("PaymentCardSavePaymentInformation", R.string.PaymentCardSavePaymentInformation), r1.saveCardInfo, false);
                r1.linearLayout2.addView(r1.checkCell1, LayoutHelper.createLinear(-1, -2));
                r1.checkCell1.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymentFormActivity.this.saveCardInfo = PaymentFormActivity.this.saveCardInfo ^ 1;
                        PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveCardInfo);
                    }
                });
                r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                updateSavePaymentField();
                r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
            } else {
                if (r1.paymentForm.native_params != null) {
                    try {
                        jsonObject = new JSONObject(r1.paymentForm.native_params.data);
                        try {
                            r1.need_card_country = jsonObject.getBoolean("need_country");
                        } catch (Exception e422) {
                            exception = e422;
                            r1.need_card_country = false;
                        }
                        try {
                            r1.need_card_postcode = jsonObject.getBoolean("need_zip");
                        } catch (Exception e4222) {
                            exception = e4222;
                            r1.need_card_postcode = false;
                        }
                        try {
                            r1.need_card_name = jsonObject.getBoolean("need_cardholder_name");
                        } catch (Exception e42222) {
                            exception = e42222;
                            r1.need_card_name = false;
                        }
                        try {
                            r1.stripeApiKey = jsonObject.getString("publishable_key");
                        } catch (Exception e422222) {
                            exception = e422222;
                            r1.stripeApiKey = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                    } catch (Throwable e222) {
                        FileLog.m3e(e222);
                    }
                }
                initAndroidPay(context);
                a = 6;
                r1.inputFields = new EditTextBoldCursor[6];
                a = 0;
                while (a < a) {
                    if (a == 0) {
                        r1.headerCell[0] = new HeaderCell(context2);
                        r1.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        r1.headerCell[0].setText(LocaleController.getString("PaymentCardTitle", R.string.PaymentCardTitle));
                        r1.linearLayout2.addView(r1.headerCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 4) {
                        r1.headerCell[1] = new HeaderCell(context2);
                        r1.headerCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        r1.headerCell[1].setText(LocaleController.getString("PaymentBillingAddress", R.string.PaymentBillingAddress));
                        r1.linearLayout2.addView(r1.headerCell[1], LayoutHelper.createLinear(-1, -2));
                    }
                    boolean allowDivider2 = (a == 3 || a == 5 || (a == 4 && !r1.need_card_postcode)) ? false : true;
                    container = new FrameLayout(context2);
                    r1.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
                    container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.inputFields[a] = new EditTextBoldCursor(context2);
                    r1.inputFields[a].setTag(Integer.valueOf(a));
                    r1.inputFields[a].setTextSize(1, 16.0f);
                    r1.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                    r1.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    r1.inputFields[a].setBackgroundDrawable(null);
                    r1.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    r1.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                    r1.inputFields[a].setCursorWidth(1.5f);
                    if (a == 3) {
                        r1.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(3)});
                        r1.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
                        r1.inputFields[a].setTypeface(Typeface.DEFAULT);
                        r1.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else if (a == 0) {
                        r1.inputFields[a].setInputType(2);
                    } else if (a == 4) {
                        r1.inputFields[a].setOnTouchListener(new OnTouchListener() {

                            /* renamed from: org.telegram.ui.PaymentFormActivity$11$1 */
                            class C22091 implements CountrySelectActivityDelegate {
                                C22091() {
                                }

                                public void didSelectCountry(String name, String shortName) {
                                    PaymentFormActivity.this.inputFields[4].setText(name);
                                }
                            }

                            public boolean onTouch(View v, MotionEvent event) {
                                if (PaymentFormActivity.this.getParentActivity() == null) {
                                    return false;
                                }
                                if (event.getAction() == 1) {
                                    CountrySelectActivity fragment = new CountrySelectActivity(false);
                                    fragment.setCountrySelectActivityDelegate(new C22091());
                                    PaymentFormActivity.this.presentFragment(fragment);
                                }
                                return true;
                            }
                        });
                        r1.inputFields[a].setInputType(0);
                    } else if (a == 1) {
                        r1.inputFields[a].setInputType(16386);
                    } else if (a == 2) {
                        r1.inputFields[a].setInputType(4097);
                    } else {
                        r1.inputFields[a].setInputType(16385);
                    }
                    r1.inputFields[a].setImeOptions(268435461);
                    switch (a) {
                        case 0:
                            r1.inputFields[a].setHint(LocaleController.getString("PaymentCardNumber", R.string.PaymentCardNumber));
                            break;
                        case 1:
                            r1.inputFields[a].setHint(LocaleController.getString("PaymentCardExpireDate", R.string.PaymentCardExpireDate));
                            break;
                        case 2:
                            r1.inputFields[a].setHint(LocaleController.getString("PaymentCardName", R.string.PaymentCardName));
                            break;
                        case 3:
                            r1.inputFields[a].setHint(LocaleController.getString("PaymentCardCvv", R.string.PaymentCardCvv));
                            break;
                        case 4:
                            r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingCountry", R.string.PaymentShippingCountry));
                            break;
                        case 5:
                            r1.inputFields[a].setHint(LocaleController.getString("PaymentShippingZipPlaceholder", R.string.PaymentShippingZipPlaceholder));
                            break;
                        default:
                            break;
                    }
                    if (a == 0) {
                        r1.inputFields[a].addTextChangedListener(new TextWatcher() {
                            public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
                            public static final int MAX_LENGTH_DINERS_CLUB = 14;
                            public static final int MAX_LENGTH_STANDARD = 16;
                            public final String[] PREFIXES_14 = new String[]{"300", "301", "302", "303", "304", "305", "309", "36", "38", "39"};
                            public final String[] PREFIXES_15 = new String[]{"34", "37"};
                            public final String[] PREFIXES_16 = new String[]{"2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55", "4", "60", "62", "64", "65", "35"};
                            private int actionPosition;
                            private int characterAction = -1;

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                if (count == 0 && after == 1) {
                                    this.characterAction = 1;
                                } else if (count != 1 || after != 0) {
                                    this.characterAction = -1;
                                } else if (s.charAt(start) != ' ' || start <= 0) {
                                    this.characterAction = 2;
                                } else {
                                    this.characterAction = 3;
                                    this.actionPosition = start - 1;
                                }
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (!PaymentFormActivity.this.ignoreOnCardChange) {
                                    StringBuilder stringBuilder;
                                    int a;
                                    String ch;
                                    int i;
                                    int i2 = 0;
                                    EditText phoneField = PaymentFormActivity.this.inputFields[0];
                                    int start = phoneField.getSelectionStart();
                                    String phoneChars = "0123456789";
                                    String str = phoneField.getText().toString();
                                    int i3 = 3;
                                    if (r0.characterAction == 3) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str.substring(0, r0.actionPosition));
                                        stringBuilder.append(str.substring(r0.actionPosition + 1, str.length()));
                                        str = stringBuilder.toString();
                                        start--;
                                    }
                                    stringBuilder = new StringBuilder(str.length());
                                    for (a = 0; a < str.length(); a++) {
                                        ch = str.substring(a, a + 1);
                                        if (phoneChars.contains(ch)) {
                                            stringBuilder.append(ch);
                                        }
                                    }
                                    PaymentFormActivity.this.ignoreOnCardChange = true;
                                    String hint = null;
                                    int maxLength = 100;
                                    if (stringBuilder.length() > 0) {
                                        String currentString = stringBuilder.toString();
                                        int maxLength2 = 100;
                                        ch = null;
                                        a = 0;
                                        while (a < i3) {
                                            String[] checkArr;
                                            int resultMaxLength;
                                            String resultHint;
                                            switch (a) {
                                                case 0:
                                                    checkArr = r0.PREFIXES_16;
                                                    resultMaxLength = 16;
                                                    resultHint = "xxxx xxxx xxxx xxxx";
                                                    break;
                                                case 1:
                                                    checkArr = r0.PREFIXES_15;
                                                    resultMaxLength = 15;
                                                    resultHint = "xxxx xxxx xxxx xxx";
                                                    break;
                                                default:
                                                    checkArr = r0.PREFIXES_14;
                                                    resultMaxLength = 14;
                                                    resultHint = "xxxx xxxx xxxx xx";
                                                    break;
                                            }
                                            int b = i2;
                                            while (true) {
                                                i3 = b;
                                                if (i3 < checkArr.length) {
                                                    int maxLength3;
                                                    String prefix = checkArr[i3];
                                                    i = start;
                                                    if (currentString.length() <= prefix.length()) {
                                                        if (prefix.startsWith(currentString) != 0) {
                                                            start = resultHint;
                                                            maxLength3 = resultMaxLength;
                                                        } else {
                                                            b = i3 + 1;
                                                            start = i;
                                                        }
                                                    } else if (currentString.startsWith(prefix) != 0) {
                                                        start = resultHint;
                                                        maxLength3 = resultMaxLength;
                                                    } else {
                                                        b = i3 + 1;
                                                        start = i;
                                                    }
                                                    ch = start;
                                                    maxLength2 = maxLength3;
                                                } else {
                                                    i = start;
                                                }
                                                if (ch != null) {
                                                    hint = ch;
                                                    maxLength = maxLength2;
                                                    if (maxLength != 0 && stringBuilder.length() > maxLength) {
                                                        stringBuilder.setLength(maxLength);
                                                    }
                                                } else {
                                                    a++;
                                                    start = i;
                                                    i2 = 0;
                                                    i3 = 3;
                                                }
                                            }
                                        }
                                        i = start;
                                        hint = ch;
                                        maxLength = maxLength2;
                                        stringBuilder.setLength(maxLength);
                                    } else {
                                        i = start;
                                    }
                                    if (hint != null) {
                                        if (maxLength != 0 && stringBuilder.length() == maxLength) {
                                            PaymentFormActivity.this.inputFields[1].requestFocus();
                                        }
                                        phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                        start = i;
                                        i2 = 0;
                                        while (i2 < stringBuilder.length()) {
                                            if (i2 < hint.length()) {
                                                if (hint.charAt(i2) == ' ') {
                                                    stringBuilder.insert(i2, ' ');
                                                    i2++;
                                                    if (!(start != i2 || r0.characterAction == 2 || r0.characterAction == 3)) {
                                                        start++;
                                                    }
                                                }
                                                i2++;
                                            } else {
                                                stringBuilder.insert(i2, ' ');
                                                if (!(start != i2 + 1 || r0.characterAction == 2 || r0.characterAction == 3)) {
                                                    start++;
                                                }
                                            }
                                        }
                                    } else {
                                        phoneField.setTextColor(Theme.getColor(stringBuilder.length() > 0 ? Theme.key_windowBackgroundWhiteRedText4 : Theme.key_windowBackgroundWhiteBlackText));
                                        start = i;
                                    }
                                    phoneField.setText(stringBuilder);
                                    if (start >= 0) {
                                        phoneField.setSelection(start <= phoneField.length() ? start : phoneField.length());
                                    }
                                    PaymentFormActivity.this.ignoreOnCardChange = false;
                                }
                            }
                        });
                    } else if (a == 1) {
                        r1.inputFields[a].addTextChangedListener(new TextWatcher() {
                            private int actionPosition;
                            private int characterAction = -1;
                            private boolean isYear;

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                boolean z = false;
                                if (count == 0 && after == 1) {
                                    if (TextUtils.indexOf(PaymentFormActivity.this.inputFields[1].getText(), '/') != -1) {
                                        z = true;
                                    }
                                    this.isYear = z;
                                    this.characterAction = 1;
                                } else if (count != 1 || after != 0) {
                                    this.characterAction = -1;
                                } else if (s.charAt(start) != '/' || start <= 0) {
                                    this.characterAction = 2;
                                } else {
                                    this.isYear = false;
                                    this.characterAction = 3;
                                    this.actionPosition = start - 1;
                                }
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (!PaymentFormActivity.this.ignoreOnCardChange) {
                                    StringBuilder stringBuilder;
                                    EditText phoneField = PaymentFormActivity.this.inputFields[1];
                                    int start = phoneField.getSelectionStart();
                                    String phoneChars = "0123456789";
                                    String str = phoneField.getText().toString();
                                    if (r0.characterAction == 3) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str.substring(0, r0.actionPosition));
                                        stringBuilder.append(str.substring(r0.actionPosition + 1, str.length()));
                                        str = stringBuilder.toString();
                                        start--;
                                    }
                                    stringBuilder = new StringBuilder(str.length());
                                    for (int a = 0; a < str.length(); a++) {
                                        String ch = str.substring(a, a + 1);
                                        if (phoneChars.contains(ch)) {
                                            stringBuilder.append(ch);
                                        }
                                    }
                                    PaymentFormActivity.this.ignoreOnCardChange = true;
                                    PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                    if (stringBuilder.length() > 4) {
                                        stringBuilder.setLength(4);
                                    }
                                    if (stringBuilder.length() < 2) {
                                        r0.isYear = false;
                                    }
                                    boolean isError = false;
                                    int currentYear;
                                    if (r0.isYear) {
                                        String[] args = new String[(stringBuilder.length() > 2 ? 2 : 1)];
                                        args[0] = stringBuilder.substring(0, 2);
                                        if (args.length == 2) {
                                            args[1] = stringBuilder.substring(2);
                                        }
                                        if (stringBuilder.length() == 4 && args.length == 2) {
                                            int month = Utilities.parseInt(args[0]).intValue();
                                            int year = Utilities.parseInt(args[1]).intValue() + 2000;
                                            Calendar rightNow = Calendar.getInstance();
                                            currentYear = rightNow.get(1);
                                            int currentMonth = rightNow.get(2) + 1;
                                            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                                                PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                                isError = true;
                                            }
                                        } else {
                                            currentYear = Utilities.parseInt(args[0]).intValue();
                                            if (currentYear > 12 || currentYear == 0) {
                                                PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                                isError = true;
                                            }
                                        }
                                    } else if (stringBuilder.length() == 1) {
                                        currentYear = Utilities.parseInt(stringBuilder.toString()).intValue();
                                        if (!(currentYear == 1 || currentYear == 0)) {
                                            stringBuilder.insert(0, "0");
                                            start++;
                                        }
                                    } else if (stringBuilder.length() == 2) {
                                        currentYear = Utilities.parseInt(stringBuilder.toString()).intValue();
                                        if (currentYear > 12 || currentYear == 0) {
                                            PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                            isError = true;
                                        }
                                        start++;
                                    }
                                    if (!isError && stringBuilder.length() == 4) {
                                        PaymentFormActivity.this.inputFields[PaymentFormActivity.this.need_card_name ? 2 : 3].requestFocus();
                                    }
                                    if (stringBuilder.length() == 2) {
                                        stringBuilder.append('/');
                                        start++;
                                    } else if (stringBuilder.length() > 2 && stringBuilder.charAt(2) != '/') {
                                        stringBuilder.insert(2, '/');
                                        start++;
                                    }
                                    phoneField.setText(stringBuilder);
                                    if (start >= 0) {
                                        phoneField.setSelection(start <= phoneField.length() ? start : phoneField.length());
                                    }
                                    PaymentFormActivity.this.ignoreOnCardChange = false;
                                }
                            }
                        });
                    }
                    r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                    r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                    container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                    r1.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i == 5) {
                                int num = ((Integer) textView.getTag()).intValue();
                                while (num + 1 < PaymentFormActivity.this.inputFields.length) {
                                    num++;
                                    if (num == 4) {
                                        num++;
                                    }
                                    if (((View) PaymentFormActivity.this.inputFields[num].getParent()).getVisibility() == 0) {
                                        PaymentFormActivity.this.inputFields[num].requestFocus();
                                        break;
                                    }
                                }
                                return true;
                            } else if (i != 6) {
                                return false;
                            } else {
                                PaymentFormActivity.this.doneItem.performClick();
                                return true;
                            }
                        }
                    });
                    if (a == 3) {
                        r1.sectionCell[0] = new ShadowSectionCell(context2);
                        r1.linearLayout2.addView(r1.sectionCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 5) {
                        r1.sectionCell[2] = new ShadowSectionCell(context2);
                        r1.linearLayout2.addView(r1.sectionCell[2], LayoutHelper.createLinear(-1, -2));
                        r1.checkCell1 = new TextCheckCell(context2);
                        r1.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        r1.checkCell1.setTextAndCheck(LocaleController.getString("PaymentCardSavePaymentInformation", R.string.PaymentCardSavePaymentInformation), r1.saveCardInfo, false);
                        r1.linearLayout2.addView(r1.checkCell1, LayoutHelper.createLinear(-1, -2));
                        r1.checkCell1.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                PaymentFormActivity.this.saveCardInfo = PaymentFormActivity.this.saveCardInfo ^ 1;
                                PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveCardInfo);
                            }
                        });
                        r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                        r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        updateSavePaymentField();
                        r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 0) {
                        r1.androidPayContainer = new FrameLayout(context2);
                        r1.androidPayContainer.setId(fragment_container_id);
                        r1.androidPayContainer.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        r1.androidPayContainer.setVisibility(8);
                        container.addView(r1.androidPayContainer, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 4.0f, 0.0f));
                    }
                    if (allowDivider2) {
                        View divider2 = new View(context2);
                        r1.dividers.add(divider2);
                        divider2.setBackgroundColor(Theme.getColor(Theme.key_divider));
                        container.addView(divider2, new LayoutParams(-1, 1, 83));
                    }
                    if ((a == 4 && !r1.need_card_country) || ((a == 5 && !r1.need_card_postcode) || (a == 2 && !r1.need_card_name))) {
                        container.setVisibility(8);
                    }
                    a++;
                    a = 6;
                }
                if (!(r1.need_card_country || r1.need_card_postcode)) {
                    r1.headerCell[1].setVisibility(8);
                    r1.sectionCell[0].setVisibility(8);
                }
                if (r1.need_card_postcode) {
                    r1.inputFields[5].setImeOptions(268435462);
                } else {
                    r1.inputFields[3].setImeOptions(268435462);
                }
            }
        } else if (r1.currentStep == 1) {
            a = r1.requestedInfo.shipping_options.size();
            r1.radioCells = new RadioCell[a];
            a = 0;
            while (a < a) {
                TL_shippingOption shippingOption = (TL_shippingOption) r1.requestedInfo.shipping_options.get(a);
                r1.radioCells[a] = new RadioCell(context2);
                r1.radioCells[a].setTag(Integer.valueOf(a));
                r1.radioCells[a].setBackgroundDrawable(Theme.getSelectorDrawable(true));
                r1.radioCells[a].setText(String.format("%s - %s", new Object[]{getTotalPriceString(shippingOption.prices), shippingOption.title}), a == 0, a != a + -1);
                r1.radioCells[a].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        int num = ((Integer) v.getTag()).intValue();
                        int a = 0;
                        while (a < PaymentFormActivity.this.radioCells.length) {
                            PaymentFormActivity.this.radioCells[a].setChecked(num == a, true);
                            a++;
                        }
                    }
                });
                r1.linearLayout2.addView(r1.radioCells[a]);
                a++;
            }
            r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
            r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
        } else if (r1.currentStep == 3) {
            a = 2;
            r1.inputFields = new EditTextBoldCursor[2];
            a = 0;
            while (a < a) {
                if (a == 0) {
                    r1.headerCell[0] = new HeaderCell(context2);
                    r1.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.headerCell[0].setText(LocaleController.getString("PaymentCardTitle", R.string.PaymentCardTitle));
                    b = -1;
                    r1.linearLayout2.addView(r1.headerCell[0], LayoutHelper.createLinear(-1, -2));
                } else {
                    b = -1;
                }
                container = new FrameLayout(context2);
                r1.linearLayout2.addView(container, LayoutHelper.createLinear(b, 48));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                boolean allowDivider3 = a != 1;
                if (allowDivider3) {
                    if (a == 7 && !r1.paymentForm.invoice.phone_requested) {
                        allowDivider3 = false;
                    } else if (!(a != 6 || r1.paymentForm.invoice.phone_requested || r1.paymentForm.invoice.email_requested)) {
                        allowDivider3 = false;
                    }
                }
                if (allowDivider3) {
                    divider = new View(context2);
                    r1.dividers.add(divider);
                    divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                    container.addView(divider, new LayoutParams(-1, 1, 83));
                }
                r1.inputFields[a] = new EditTextBoldCursor(context2);
                r1.inputFields[a].setTag(Integer.valueOf(a));
                r1.inputFields[a].setTextSize(1, 16.0f);
                r1.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                r1.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                r1.inputFields[a].setBackgroundDrawable(null);
                r1.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                r1.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                r1.inputFields[a].setCursorWidth(1.5f);
                if (a == 0) {
                    r1.inputFields[a].setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                    r1.inputFields[a].setInputType(0);
                } else {
                    r1.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                    r1.inputFields[a].setTypeface(Typeface.DEFAULT);
                }
                r1.inputFields[a].setImeOptions(268435462);
                switch (a) {
                    case 0:
                        r1.inputFields[a].setText(r1.paymentForm.saved_credentials.title);
                        break;
                    case 1:
                        r1.inputFields[a].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                        r1.inputFields[a].requestFocus();
                        break;
                    default:
                        break;
                }
                r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                r1.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i != 6) {
                            return false;
                        }
                        PaymentFormActivity.this.doneItem.performClick();
                        return true;
                    }
                });
                if (a == 1) {
                    r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                    r1.bottomCell[0].setText(LocaleController.formatString("PaymentConfirmationMessage", R.string.PaymentConfirmationMessage, r1.paymentForm.saved_credentials.title));
                    r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                    r1.settingsCell1 = new TextSettingsCell(context2);
                    r1.settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    r1.settingsCell1.setText(LocaleController.getString("PaymentConfirmationNewCard", R.string.PaymentConfirmationNewCard), false);
                    r1.linearLayout2.addView(r1.settingsCell1, LayoutHelper.createLinear(-1, -2));
                    r1.settingsCell1.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            PaymentFormActivity.this.passwordOk = false;
                            PaymentFormActivity.this.goToNextStep();
                        }
                    });
                    r1.bottomCell[1] = new TextInfoPrivacyCell(context2);
                    r1.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    r1.linearLayout2.addView(r1.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                }
                a++;
                a = 2;
            }
        } else {
            String providerName2;
            if (r1.currentStep != 4) {
                if (r1.currentStep != 5) {
                    if (r1.currentStep == 6) {
                        r1.bottomCell[2] = new TextInfoPrivacyCell(context2);
                        r1.bottomCell[2].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        r1.linearLayout2.addView(r1.bottomCell[2], LayoutHelper.createLinear(-1, -2));
                        r1.settingsCell1 = new TextSettingsCell(context2);
                        r1.settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        r1.settingsCell1.setTag(Theme.key_windowBackgroundWhiteRedText3);
                        r1.settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                        r1.settingsCell1.setText(LocaleController.getString("AbortPassword", R.string.AbortPassword), false);
                        r1.linearLayout2.addView(r1.settingsCell1, LayoutHelper.createLinear(-1, -2));
                        r1.settingsCell1.setOnClickListener(new OnClickListener() {

                            /* renamed from: org.telegram.ui.PaymentFormActivity$24$1 */
                            class C15511 implements DialogInterface.OnClickListener {
                                C15511() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PaymentFormActivity.this.sendSavePassword(true);
                                }
                            }

                            public void onClick(View v) {
                                Builder builder = new Builder(PaymentFormActivity.this.getParentActivity());
                                builder.setMessage(LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion));
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C15511());
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                PaymentFormActivity.this.showDialog(builder.create());
                            }
                        });
                        a = 3;
                        r1.inputFields = new EditTextBoldCursor[3];
                        a = 0;
                        while (a < a) {
                            View divider3;
                            if (a == 0) {
                                r1.headerCell[0] = new HeaderCell(context2);
                                r1.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                                r1.headerCell[0].setText(LocaleController.getString("PaymentPasswordTitle", R.string.PaymentPasswordTitle));
                                r1.linearLayout2.addView(r1.headerCell[0], LayoutHelper.createLinear(-1, -2));
                            } else if (a == 2) {
                                r1.headerCell[1] = new HeaderCell(context2);
                                r1.headerCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                                r1.headerCell[1].setText(LocaleController.getString("PaymentPasswordEmailTitle", R.string.PaymentPasswordEmailTitle));
                                b = -1;
                                r1.linearLayout2.addView(r1.headerCell[1], LayoutHelper.createLinear(-1, -2));
                                container = new FrameLayout(context2);
                                r1.linearLayout2.addView(container, LayoutHelper.createLinear(b, 48));
                                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                                if (a == 0) {
                                    divider3 = new View(context2);
                                    r1.dividers.add(divider3);
                                    divider3.setBackgroundColor(Theme.getColor(Theme.key_divider));
                                    container.addView(divider3, new LayoutParams(-1, 1, 83));
                                }
                                r1.inputFields[a] = new EditTextBoldCursor(context2);
                                r1.inputFields[a].setTag(Integer.valueOf(a));
                                r1.inputFields[a].setTextSize(1, 16.0f);
                                r1.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                                r1.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                r1.inputFields[a].setBackgroundDrawable(null);
                                r1.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                r1.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                                r1.inputFields[a].setCursorWidth(1.5f);
                                if (a != 0) {
                                    if (a == 1) {
                                        r1.inputFields[a].setImeOptions(268435462);
                                        switch (a) {
                                            case 0:
                                                r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEnter", R.string.PaymentPasswordEnter));
                                                r1.inputFields[a].requestFocus();
                                                break;
                                            case 1:
                                                r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordReEnter", R.string.PaymentPasswordReEnter));
                                                break;
                                            case 2:
                                                r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEmail", R.string.PaymentPasswordEmail));
                                                break;
                                            default:
                                                break;
                                        }
                                        r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                                        r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                                        container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                                        r1.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                                if (i == 6) {
                                                    PaymentFormActivity.this.doneItem.performClick();
                                                    return true;
                                                }
                                                if (i == 5) {
                                                    int num = ((Integer) textView.getTag()).intValue();
                                                    if (num == 0) {
                                                        PaymentFormActivity.this.inputFields[1].requestFocus();
                                                    } else if (num == 1) {
                                                        PaymentFormActivity.this.inputFields[2].requestFocus();
                                                    }
                                                }
                                                return false;
                                            }
                                        });
                                        if (a != 1) {
                                            r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                                            r1.bottomCell[0].setText(LocaleController.getString("PaymentPasswordInfo", R.string.PaymentPasswordInfo));
                                            r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                            r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                                        } else if (a != 2) {
                                            r1.bottomCell[1] = new TextInfoPrivacyCell(context2);
                                            r1.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
                                            r1.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                            r1.linearLayout2.addView(r1.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                                        }
                                        a++;
                                        a = 3;
                                    }
                                }
                                r1.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                                r1.inputFields[a].setTypeface(Typeface.DEFAULT);
                                r1.inputFields[a].setImeOptions(268435461);
                                switch (a) {
                                    case 0:
                                        r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEnter", R.string.PaymentPasswordEnter));
                                        r1.inputFields[a].requestFocus();
                                        break;
                                    case 1:
                                        r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordReEnter", R.string.PaymentPasswordReEnter));
                                        break;
                                    case 2:
                                        r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEmail", R.string.PaymentPasswordEmail));
                                        break;
                                    default:
                                        break;
                                }
                                r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                                if (LocaleController.isRTL) {
                                }
                                r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                                container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                                r1.inputFields[a].setOnEditorActionListener(/* anonymous class already generated */);
                                if (a != 1) {
                                    r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                                    r1.bottomCell[0].setText(LocaleController.getString("PaymentPasswordInfo", R.string.PaymentPasswordInfo));
                                    r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                    r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                                } else if (a != 2) {
                                    r1.bottomCell[1] = new TextInfoPrivacyCell(context2);
                                    r1.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
                                    r1.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                    r1.linearLayout2.addView(r1.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                                }
                                a++;
                                a = 3;
                            }
                            b = -1;
                            container = new FrameLayout(context2);
                            r1.linearLayout2.addView(container, LayoutHelper.createLinear(b, 48));
                            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            if (a == 0) {
                                divider3 = new View(context2);
                                r1.dividers.add(divider3);
                                divider3.setBackgroundColor(Theme.getColor(Theme.key_divider));
                                container.addView(divider3, new LayoutParams(-1, 1, 83));
                            }
                            r1.inputFields[a] = new EditTextBoldCursor(context2);
                            r1.inputFields[a].setTag(Integer.valueOf(a));
                            r1.inputFields[a].setTextSize(1, 16.0f);
                            r1.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                            r1.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            r1.inputFields[a].setBackgroundDrawable(null);
                            r1.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            r1.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                            r1.inputFields[a].setCursorWidth(1.5f);
                            if (a != 0) {
                                if (a == 1) {
                                    r1.inputFields[a].setImeOptions(268435462);
                                    switch (a) {
                                        case 0:
                                            r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEnter", R.string.PaymentPasswordEnter));
                                            r1.inputFields[a].requestFocus();
                                            break;
                                        case 1:
                                            r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordReEnter", R.string.PaymentPasswordReEnter));
                                            break;
                                        case 2:
                                            r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEmail", R.string.PaymentPasswordEmail));
                                            break;
                                        default:
                                            break;
                                    }
                                    r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                                    if (LocaleController.isRTL) {
                                    }
                                    r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                                    container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                                    r1.inputFields[a].setOnEditorActionListener(/* anonymous class already generated */);
                                    if (a != 1) {
                                        r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                                        r1.bottomCell[0].setText(LocaleController.getString("PaymentPasswordInfo", R.string.PaymentPasswordInfo));
                                        r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                        r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                                    } else if (a != 2) {
                                        r1.bottomCell[1] = new TextInfoPrivacyCell(context2);
                                        r1.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
                                        r1.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                        r1.linearLayout2.addView(r1.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                                    }
                                    a++;
                                    a = 3;
                                }
                            }
                            r1.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                            r1.inputFields[a].setTypeface(Typeface.DEFAULT);
                            r1.inputFields[a].setImeOptions(268435461);
                            switch (a) {
                                case 0:
                                    r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEnter", R.string.PaymentPasswordEnter));
                                    r1.inputFields[a].requestFocus();
                                    break;
                                case 1:
                                    r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordReEnter", R.string.PaymentPasswordReEnter));
                                    break;
                                case 2:
                                    r1.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEmail", R.string.PaymentPasswordEmail));
                                    break;
                                default:
                                    break;
                            }
                            r1.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                            if (LocaleController.isRTL) {
                            }
                            r1.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                            container.addView(r1.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                            r1.inputFields[a].setOnEditorActionListener(/* anonymous class already generated */);
                            if (a != 1) {
                                r1.bottomCell[0] = new TextInfoPrivacyCell(context2);
                                r1.bottomCell[0].setText(LocaleController.getString("PaymentPasswordInfo", R.string.PaymentPasswordInfo));
                                r1.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                r1.linearLayout2.addView(r1.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                            } else if (a != 2) {
                                r1.bottomCell[1] = new TextInfoPrivacyCell(context2);
                                r1.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
                                r1.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                r1.linearLayout2.addView(r1.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                            }
                            a++;
                            a = 3;
                        }
                        updatePasswordFields();
                    }
                }
            }
            r1.paymentInfoCell = new PaymentInfoCell(context2);
            r1.paymentInfoCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r1.paymentInfoCell.setInvoice((TL_messageMediaInvoice) r1.messageObject.messageOwner.media, r1.currentBotName);
            r1.linearLayout2.addView(r1.paymentInfoCell, LayoutHelper.createLinear(-1, -2));
            r1.sectionCell[0] = new ShadowSectionCell(context2);
            r1.linearLayout2.addView(r1.sectionCell[0], LayoutHelper.createLinear(-1, -2));
            ArrayList<TL_labeledPrice> arrayList = new ArrayList();
            arrayList.addAll(r1.paymentForm.invoice.prices);
            if (r1.shippingOption != null) {
                arrayList.addAll(r1.shippingOption.prices);
            }
            androidPayKey = getTotalPriceString(arrayList);
            for (i = 0; i < arrayList.size(); i++) {
                TL_labeledPrice price = (TL_labeledPrice) arrayList.get(i);
                TextPriceCell priceCell = new TextPriceCell(context2);
                priceCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                priceCell.setTextAndValue(price.label, LocaleController.getInstance().formatCurrencyString(price.amount, r1.paymentForm.invoice.currency), false);
                r1.linearLayout2.addView(priceCell);
            }
            TextPriceCell priceCell2 = new TextPriceCell(context2);
            priceCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            priceCell2.setTextAndValue(LocaleController.getString("PaymentTransactionTotal", R.string.PaymentTransactionTotal), androidPayKey, true);
            r1.linearLayout2.addView(priceCell2);
            divider = new View(context2);
            r1.dividers.add(divider);
            divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
            r1.linearLayout2.addView(divider, new LayoutParams(-1, 1, 83));
            r1.detailSettingsCell[0] = new TextDetailSettingsCell(context2);
            r1.detailSettingsCell[0].setBackgroundDrawable(Theme.getSelectorDrawable(true));
            r1.detailSettingsCell[0].setTextAndValue(r1.cardName, LocaleController.getString("PaymentCheckoutMethod", R.string.PaymentCheckoutMethod), true);
            r1.linearLayout2.addView(r1.detailSettingsCell[0]);
            if (r1.currentStep == 4) {
                r1.detailSettingsCell[0].setOnClickListener(new OnClickListener() {

                    /* renamed from: org.telegram.ui.PaymentFormActivity$20$1 */
                    class C22111 implements PaymentFormActivityDelegate {
                        C22111() {
                        }

                        public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
                            PaymentFormActivity.this.paymentForm.saved_credentials = null;
                            PaymentFormActivity.this.paymentJson = tokenJson;
                            PaymentFormActivity.this.saveCardInfo = saveCard;
                            PaymentFormActivity.this.cardName = card;
                            PaymentFormActivity.this.androidPayCredentials = androidPay;
                            PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", R.string.PaymentCheckoutMethod), true);
                            return false;
                        }

                        public void onFragmentDestroyed() {
                        }

                        public void currentPasswordUpdated(account_Password password) {
                        }
                    }

                    public void onClick(View v) {
                        PaymentFormActivity activity = new PaymentFormActivity(PaymentFormActivity.this.paymentForm, PaymentFormActivity.this.messageObject, 2, PaymentFormActivity.this.requestedInfo, PaymentFormActivity.this.shippingOption, null, PaymentFormActivity.this.cardName, PaymentFormActivity.this.validateRequest, PaymentFormActivity.this.saveCardInfo, null);
                        activity.setDelegate(new C22111());
                        PaymentFormActivity.this.presentFragment(activity);
                    }
                });
            }
            User providerUser2 = null;
            for (int a2 = 0; a2 < r1.paymentForm.users.size(); a2++) {
                user = (User) r1.paymentForm.users.get(a2);
                if (user.id == r1.paymentForm.provider_id) {
                    providerUser2 = user;
                }
            }
            if (providerUser2 != null) {
                r1.detailSettingsCell[1] = new TextDetailSettingsCell(context2);
                r1.detailSettingsCell[1].setBackgroundDrawable(Theme.getSelectorDrawable(true));
                TextDetailSettingsCell textDetailSettingsCell = r1.detailSettingsCell[1];
                line = ContactsController.formatName(providerUser2.first_name, providerUser2.last_name);
                providerName2 = line;
                textDetailSettingsCell.setTextAndValue(line, LocaleController.getString("PaymentCheckoutProvider", R.string.PaymentCheckoutProvider), true);
                r1.linearLayout2.addView(r1.detailSettingsCell[1]);
            } else {
                providerName2 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            value = providerName2;
            if (r1.validateRequest != null) {
                if (r1.validateRequest.info.shipping_address != null) {
                    line = String.format("%s %s, %s, %s, %s, %s", new Object[]{r1.validateRequest.info.shipping_address.street_line1, r1.validateRequest.info.shipping_address.street_line2, r1.validateRequest.info.shipping_address.city, r1.validateRequest.info.shipping_address.state, r1.validateRequest.info.shipping_address.country_iso2, r1.validateRequest.info.shipping_address.post_code});
                    r1.detailSettingsCell[2] = new TextDetailSettingsCell(context2);
                    r1.detailSettingsCell[2].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.detailSettingsCell[2].setTextAndValue(line, LocaleController.getString("PaymentShippingAddress", R.string.PaymentShippingAddress), true);
                    r1.linearLayout2.addView(r1.detailSettingsCell[2]);
                }
                if (r1.validateRequest.info.name != null) {
                    r1.detailSettingsCell[3] = new TextDetailSettingsCell(context2);
                    r1.detailSettingsCell[3].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.detailSettingsCell[3].setTextAndValue(r1.validateRequest.info.name, LocaleController.getString("PaymentCheckoutName", R.string.PaymentCheckoutName), true);
                    r1.linearLayout2.addView(r1.detailSettingsCell[3]);
                }
                if (r1.validateRequest.info.phone != null) {
                    r1.detailSettingsCell[4] = new TextDetailSettingsCell(context2);
                    r1.detailSettingsCell[4].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.detailSettingsCell[4].setTextAndValue(PhoneFormat.getInstance().format(r1.validateRequest.info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", R.string.PaymentCheckoutPhoneNumber), true);
                    r1.linearLayout2.addView(r1.detailSettingsCell[4]);
                }
                if (r1.validateRequest.info.email != null) {
                    r1.detailSettingsCell[5] = new TextDetailSettingsCell(context2);
                    r1.detailSettingsCell[5].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.detailSettingsCell[5].setTextAndValue(r1.validateRequest.info.email, LocaleController.getString("PaymentCheckoutEmail", R.string.PaymentCheckoutEmail), true);
                    r1.linearLayout2.addView(r1.detailSettingsCell[5]);
                }
                if (r1.shippingOption != null) {
                    r1.detailSettingsCell[6] = new TextDetailSettingsCell(context2);
                    r1.detailSettingsCell[6].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    r1.detailSettingsCell[6].setTextAndValue(r1.shippingOption.title, LocaleController.getString("PaymentCheckoutShippingMethod", R.string.PaymentCheckoutShippingMethod), false);
                    r1.linearLayout2.addView(r1.detailSettingsCell[6]);
                }
            }
            if (r1.currentStep == 4) {
                r1.bottomLayout = new FrameLayout(context2);
                r1.bottomLayout.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                frameLayout.addView(r1.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
                r1.bottomLayout.setOnClickListener(new OnClickListener() {

                    /* renamed from: org.telegram.ui.PaymentFormActivity$21$1 */
                    class C15501 implements DialogInterface.OnClickListener {
                        C15501() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            PaymentFormActivity.this.showPayAlert(androidPayKey);
                        }
                    }

                    public void onClick(View v) {
                        if (PaymentFormActivity.this.botUser == null || PaymentFormActivity.this.botUser.verified) {
                            PaymentFormActivity.this.showPayAlert(androidPayKey);
                            return;
                        }
                        String botKey = new StringBuilder();
                        botKey.append("payment_warning_");
                        botKey.append(PaymentFormActivity.this.botUser.id);
                        botKey = botKey.toString();
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(PaymentFormActivity.this.currentAccount);
                        if (preferences.getBoolean(botKey, false)) {
                            PaymentFormActivity.this.showPayAlert(androidPayKey);
                        } else {
                            preferences.edit().putBoolean(botKey, true).commit();
                            Builder builder = new Builder(PaymentFormActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("PaymentWarning", R.string.PaymentWarning));
                            builder.setMessage(LocaleController.formatString("PaymentWarningText", R.string.PaymentWarningText, PaymentFormActivity.this.currentBotName, value));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C15501());
                            PaymentFormActivity.this.showDialog(builder.create());
                        }
                    }
                });
                r1.payTextView = new TextView(context2);
                r1.payTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText6));
                r1.payTextView.setText(LocaleController.formatString("PaymentCheckoutPay", R.string.PaymentCheckoutPay, androidPayKey));
                r1.payTextView.setTextSize(1, 14.0f);
                r1.payTextView.setGravity(17);
                r1.payTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r1.bottomLayout.addView(r1.payTextView, LayoutHelper.createFrame(-1, -1.0f));
                r1.progressViewButton = new ContextProgressView(context2, 0);
                r1.progressViewButton.setVisibility(4);
                r1.bottomLayout.addView(r1.progressViewButton, LayoutHelper.createFrame(-1, -1.0f));
                divider = new View(context2);
                divider.setBackgroundResource(R.drawable.header_shadow_reverse);
                frameLayout.addView(divider, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                r1.doneItem.setEnabled(false);
                r1.doneItem.getImageView().setVisibility(4);
                r1.webView = new WebView(context2) {
                    public boolean onTouchEvent(MotionEvent event) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouchEvent(event);
                    }
                };
                r1.webView.setBackgroundColor(-1);
                r1.webView.getSettings().setJavaScriptEnabled(true);
                r1.webView.getSettings().setDomStorageEnabled(true);
                if (VERSION.SDK_INT >= 21) {
                    r1.webView.getSettings().setMixedContentMode(0);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(r1.webView, true);
                }
                r1.webView.setWebViewClient(new WebViewClient() {
                    public void onLoadResource(WebView view, String url) {
                        try {
                            if ("t.me".equals(Uri.parse(url).getHost())) {
                                PaymentFormActivity.this.goToNextStep();
                            } else {
                                super.onLoadResource(view, url);
                            }
                        } catch (Exception e) {
                        }
                    }

                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        PaymentFormActivity.this.webviewLoading = false;
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        PaymentFormActivity.this.updateSavePaymentField();
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        try {
                            if (!"t.me".equals(Uri.parse(url).getHost())) {
                                return false;
                            }
                            PaymentFormActivity.this.goToNextStep();
                            return true;
                        } catch (Exception e) {
                        }
                    }
                });
                frameLayout.addView(r1.webView, LayoutHelper.createFrame(-1, -1.0f));
                r1.webView.setVisibility(8);
            }
            r1.sectionCell[1] = new ShadowSectionCell(context2);
            r1.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            r1.linearLayout2.addView(r1.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        }
        return r1.fragmentView;
    }

    private void updatePasswordFields() {
        if (this.currentStep == 6) {
            if (this.bottomCell[2] != null) {
                int a = 0;
                int a2;
                if (this.currentPassword == null) {
                    this.doneItem.setVisibility(0);
                    showEditDoneProgress(true, true);
                    this.bottomCell[2].setVisibility(8);
                    this.settingsCell1.setVisibility(8);
                    this.headerCell[0].setVisibility(8);
                    this.headerCell[1].setVisibility(8);
                    this.bottomCell[0].setVisibility(8);
                    for (a2 = 0; a2 < 3; a2++) {
                        ((View) this.inputFields[a2].getParent()).setVisibility(8);
                    }
                    while (true) {
                        a2 = a;
                        if (a2 >= this.dividers.size()) {
                            break;
                        }
                        ((View) this.dividers.get(a2)).setVisibility(8);
                        a = a2 + 1;
                    }
                } else {
                    showEditDoneProgress(true, false);
                    if (this.waitingForEmail) {
                        if (getParentActivity() != null) {
                            AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                        }
                        this.doneItem.setVisibility(8);
                        this.bottomCell[2].setText(LocaleController.formatString("EmailPasswordConfirmText", R.string.EmailPasswordConfirmText, this.currentPassword.email_unconfirmed_pattern));
                        this.bottomCell[2].setVisibility(0);
                        this.settingsCell1.setVisibility(0);
                        this.bottomCell[1].setText(TtmlNode.ANONYMOUS_REGION_ID);
                        this.headerCell[0].setVisibility(8);
                        this.headerCell[1].setVisibility(8);
                        this.bottomCell[0].setVisibility(8);
                        for (a2 = 0; a2 < 3; a2++) {
                            ((View) this.inputFields[a2].getParent()).setVisibility(8);
                        }
                        while (true) {
                            a2 = a;
                            if (a2 >= this.dividers.size()) {
                                break;
                            }
                            ((View) this.dividers.get(a2)).setVisibility(8);
                            a = a2 + 1;
                        }
                    } else {
                        this.doneItem.setVisibility(0);
                        this.bottomCell[2].setVisibility(8);
                        this.settingsCell1.setVisibility(8);
                        this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
                        this.headerCell[0].setVisibility(0);
                        this.headerCell[1].setVisibility(0);
                        this.bottomCell[0].setVisibility(0);
                        for (a2 = 0; a2 < 3; a2++) {
                            ((View) this.inputFields[a2].getParent()).setVisibility(0);
                        }
                        for (a2 = 0; a2 < this.dividers.size(); a2++) {
                            ((View) this.dividers.get(a2)).setVisibility(0);
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
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.ui.PaymentFormActivity$26$1$1 */
                        class C15521 implements Runnable {
                            C15521() {
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
                            if (error == null) {
                                PaymentFormActivity.this.currentPassword = (account_Password) response;
                                if (PaymentFormActivity.this.paymentForm != null && (PaymentFormActivity.this.currentPassword instanceof TL_account_password)) {
                                    PaymentFormActivity.this.paymentForm.password_missing = false;
                                    PaymentFormActivity.this.paymentForm.can_save_credentials = true;
                                    PaymentFormActivity.this.updateSavePaymentField();
                                }
                                byte[] salt = new byte[(PaymentFormActivity.this.currentPassword.new_salt.length + 8)];
                                Utilities.random.nextBytes(salt);
                                System.arraycopy(PaymentFormActivity.this.currentPassword.new_salt, 0, salt, 0, PaymentFormActivity.this.currentPassword.new_salt.length);
                                PaymentFormActivity.this.currentPassword.new_salt = salt;
                                if (PaymentFormActivity.this.passwordFragment != null) {
                                    PaymentFormActivity.this.passwordFragment.setCurrentPassword(PaymentFormActivity.this.currentPassword);
                                }
                            }
                            if ((response instanceof TL_account_noPassword) && PaymentFormActivity.this.shortPollRunnable == null) {
                                PaymentFormActivity.this.shortPollRunnable = new C15521();
                                AndroidUtilities.runOnUIThread(PaymentFormActivity.this.shortPollRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                            }
                        }
                    });
                }
            }, 10);
        }
    }

    private void showAlertWithText(String title, String text) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void showPayAlert(String totalPrice) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", R.string.PaymentTransactionReview));
        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", R.string.PaymentTransactionMessage, totalPrice, this.currentBotName, this.currentItemName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PaymentFormActivity.this.setDonePressed(true);
                PaymentFormActivity.this.sendData();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void initAndroidPay(Context context) {
        if (VERSION.SDK_INT < 0) {
            int i;
            GoogleApiClient.Builder addOnConnectionFailedListener = new GoogleApiClient.Builder(context).addConnectionCallbacks(new ConnectionCallbacks() {
                public void onConnected(Bundle bundle) {
                }

                public void onConnectionSuspended(int i) {
                }
            }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
                public void onConnectionFailed(ConnectionResult connectionResult) {
                }
            });
            Api api = Wallet.API;
            WalletOptions.Builder builder = new WalletOptions.Builder();
            if (this.paymentForm.invoice.test) {
                i = 3;
            } else {
                i = 1;
            }
            this.googleApiClient = addOnConnectionFailedListener.addApi(api, builder.setEnvironment(i).setTheme(1).build()).build();
            Wallet.Payments.isReadyToPay(this.googleApiClient).setResultCallback(new ResultCallback<BooleanResult>() {
                public void onResult(BooleanResult booleanResult) {
                    if (booleanResult.getStatus().isSuccess() && booleanResult.getValue()) {
                        PaymentFormActivity.this.showAndroidPay();
                    }
                }
            });
            this.googleApiClient.connect();
        }
    }

    private String getTotalPriceString(ArrayList<TL_labeledPrice> prices) {
        long amount = 0;
        int a = 0;
        while (a < prices.size()) {
            a++;
            amount += ((TL_labeledPrice) prices.get(a)).amount;
        }
        return LocaleController.getInstance().formatCurrencyString(amount, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TL_labeledPrice> prices) {
        long amount = 0;
        int a = 0;
        while (a < prices.size()) {
            a++;
            amount += ((TL_labeledPrice) prices.get(a)).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(amount, this.paymentForm.invoice.currency, false);
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

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
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
        } else if (id == NotificationCenter.didRemovedTwoStepPassword) {
            this.paymentForm.password_missing = true;
            this.paymentForm.can_save_credentials = false;
            updateSavePaymentField();
        } else if (id == NotificationCenter.paymentFinished) {
            removeSelfFromStack();
        }
    }

    private void showAndroidPay() {
        if (getParentActivity() != null) {
            if (this.androidPayContainer != null) {
                WalletFragmentStyle buyButtonText;
                PaymentMethodTokenizationParameters parameters;
                WalletFragmentOptions.Builder optionsBuilder = WalletFragmentOptions.newBuilder();
                optionsBuilder.setEnvironment(this.paymentForm.invoice.test ? 3 : 1);
                optionsBuilder.setMode(1);
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
                optionsBuilder.setFragmentStyle(buyButtonText);
                WalletFragment walletFragment = WalletFragment.newInstance(optionsBuilder.build());
                FragmentTransaction fragmentTransaction = getParentActivity().getFragmentManager().beginTransaction();
                fragmentTransaction.replace(fragment_container_id, walletFragment);
                fragmentTransaction.commit();
                ArrayList<TL_labeledPrice> arrayList = new ArrayList();
                arrayList.addAll(this.paymentForm.invoice.prices);
                if (this.shippingOption != null) {
                    arrayList.addAll(this.shippingOption.prices);
                }
                this.totalPriceDecimal = getTotalPriceDecimalString(arrayList);
                if (this.androidPayPublicKey != null) {
                    parameters = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(2).addParameter("publicKey", this.androidPayPublicKey).build();
                } else {
                    parameters = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(1).addParameter("gateway", "stripe").addParameter("stripe:publishableKey", this.stripeApiKey).addParameter("stripe:version", "3.5.0").build();
                }
                walletFragment.initialize(WalletFragmentInitParams.newBuilder().setMaskedWalletRequest(MaskedWalletRequest.newBuilder().setPaymentMethodTokenizationParameters(parameters).setEstimatedTotalPrice(this.totalPriceDecimal).setCurrencyCode(this.paymentForm.invoice.currency).build()).setMaskedWalletRequestCode(LOAD_MASKED_WALLET_REQUEST_CODE).build());
                this.androidPayContainer.setVisibility(0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[]{0.0f, 1.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(180);
                animatorSet.start();
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_MASKED_WALLET_REQUEST_CODE) {
            if (resultCode == -1) {
                showEditDoneProgress(true, true);
                setDonePressed(true);
                MaskedWallet maskedWallet = (MaskedWallet) data.getParcelableExtra("com.google.android.gms.wallet.EXTRA_MASKED_WALLET");
                Cart.Builder cardBuilder = Cart.newBuilder().setCurrencyCode(this.paymentForm.invoice.currency).setTotalPrice(this.totalPriceDecimal);
                ArrayList<TL_labeledPrice> arrayList = new ArrayList();
                arrayList.addAll(this.paymentForm.invoice.prices);
                if (this.shippingOption != null) {
                    arrayList.addAll(this.shippingOption.prices);
                }
                for (int a = 0; a < arrayList.size(); a++) {
                    TL_labeledPrice price = (TL_labeledPrice) arrayList.get(a);
                    String amount = LocaleController.getInstance().formatCurrencyDecimalString(price.amount, this.paymentForm.invoice.currency, false);
                    cardBuilder.addLineItem(LineItem.newBuilder().setCurrencyCode(this.paymentForm.invoice.currency).setQuantity("1").setDescription(price.label).setTotalPrice(amount).setUnitPrice(amount).build());
                }
                Wallet.Payments.loadFullWallet(this.googleApiClient, FullWalletRequest.newBuilder().setCart(cardBuilder.build()).setGoogleTransactionId(maskedWallet.getGoogleTransactionId()).build(), LOAD_FULL_WALLET_REQUEST_CODE);
                return;
            }
            showEditDoneProgress(true, false);
            setDonePressed(false);
        } else if (requestCode != LOAD_FULL_WALLET_REQUEST_CODE) {
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
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(card.getType());
                        stringBuilder.append(" *");
                        stringBuilder.append(card.getLast4());
                        this.cardName = stringBuilder.toString();
                    }
                    goToNextStep();
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                } catch (JSONException e) {
                    showEditDoneProgress(true, false);
                    setDonePressed(false);
                }
                return;
            }
            showEditDoneProgress(true, false);
            setDonePressed(false);
        }
    }

    private void goToNextStep() {
        int i = 2;
        if (this.currentStep == 0) {
            if (r0.paymentForm.invoice.flexible) {
                i = 1;
            } else if (r0.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(r0.currentAccount).tmpPassword != null && UserConfig.getInstance(r0.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(r0.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(r0.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(r0.currentAccount).tmpPassword != null) {
                    i = 4;
                } else {
                    i = 3;
                }
            }
            presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, i, r0.requestedInfo, null, null, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.isWebView);
        } else if (r0.currentStep == 1) {
            if (r0.paymentForm.saved_credentials != null) {
                if (UserConfig.getInstance(r0.currentAccount).tmpPassword != null && UserConfig.getInstance(r0.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime() + 60) {
                    UserConfig.getInstance(r0.currentAccount).tmpPassword = null;
                    UserConfig.getInstance(r0.currentAccount).saveConfig(false);
                }
                if (UserConfig.getInstance(r0.currentAccount).tmpPassword != null) {
                    i = 4;
                } else {
                    i = 3;
                }
            }
            presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, i, r0.requestedInfo, r0.shippingOption, null, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.isWebView);
        } else if (r0.currentStep == 2) {
            if (r0.paymentForm.password_missing && r0.saveCardInfo) {
                r0.passwordFragment = new PaymentFormActivity(r0.paymentForm, r0.messageObject, 6, r0.requestedInfo, r0.shippingOption, r0.paymentJson, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials);
                r0.passwordFragment.setCurrentPassword(r0.currentPassword);
                r0.passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
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

                    public void currentPasswordUpdated(account_Password password) {
                        PaymentFormActivity.this.currentPassword = password;
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
            if (r0.passwordOk) {
                i = 4;
            }
            presentFragment(new PaymentFormActivity(r0.paymentForm, r0.messageObject, i, r0.requestedInfo, r0.shippingOption, r0.paymentJson, r0.cardName, r0.validateRequest, r0.saveCardInfo, r0.androidPayCredentials), r0.passwordOk ^ true);
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
                    SpannableStringBuilder text = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", R.string.PaymentCardSavePaymentInformationInfoLine1));
                    if (this.paymentForm.password_missing) {
                        loadPasswordInfo();
                        text.append("\n");
                        int len = text.length();
                        String str2 = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", R.string.PaymentCardSavePaymentInformationInfoLine2);
                        int index1 = str2.indexOf(42);
                        int index2 = str2.lastIndexOf(42);
                        text.append(str2);
                        if (!(index1 == -1 || index2 == -1)) {
                            index1 += len;
                            index2 += len;
                            this.bottomCell[0].getTextView().setMovementMethod(new LinkMovementMethodMy());
                            text.replace(index2, index2 + 1, TtmlNode.ANONYMOUS_REGION_ID);
                            text.replace(index1, index1 + 1, TtmlNode.ANONYMOUS_REGION_ID);
                            text.setSpan(new LinkSpan(), index1, index2 - 1, 33);
                        }
                    }
                    this.checkCell1.setEnabled(true);
                    this.bottomCell[0].setText(text);
                    this.checkCell1.setVisibility(0);
                    this.bottomCell[0].setVisibility(0);
                    this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
                this.checkCell1.setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"HardwareIds"})
    public void fillNumber(String number) {
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            boolean allowCall = true;
            boolean allowSms = true;
            if (!(number == null && (tm.getSimState() == 1 || tm.getPhoneType() == 0))) {
                if (VERSION.SDK_INT >= 23) {
                    allowCall = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    allowSms = getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                }
                if (number != null || allowCall || allowSms) {
                    if (number == null) {
                        number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                    }
                    String textToSet = null;
                    boolean ok = false;
                    if (!TextUtils.isEmpty(number)) {
                        int a = 4;
                        if (number.length() > 4) {
                            while (true) {
                                int a2 = a;
                                if (a2 < 1) {
                                    break;
                                }
                                String sub = number.substring(0, a2);
                                if (((String) this.codesMap.get(sub)) != null) {
                                    break;
                                }
                                a = a2 - 1;
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void sendSavePassword(final boolean clear) {
        String firstPassword;
        TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
        if (clear) {
            this.doneItem.setVisibility(0);
            firstPassword = null;
            req.new_settings = new TL_account_passwordInputSettings();
            req.new_settings.flags = 2;
            req.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
            req.current_password_hash = new byte[0];
        } else {
            firstPassword = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(firstPassword)) {
                shakeField(0);
                return;
            } else if (firstPassword.equals(this.inputFields[1].getText().toString())) {
                String email = this.inputFields[2].getText().toString();
                if (email.length() < 3) {
                    shakeField(2);
                    return;
                }
                int dot = email.lastIndexOf(46);
                int dog = email.lastIndexOf(64);
                if (dot >= 0 && dog >= 0) {
                    if (dot >= dog) {
                        req.current_password_hash = new byte[0];
                        req.new_settings = new TL_account_passwordInputSettings();
                        byte[] newPasswordBytes = null;
                        try {
                            newPasswordBytes = firstPassword.getBytes(C0539C.UTF8_NAME);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        byte[] new_salt = this.currentPassword.new_salt;
                        byte[] hash = new byte[((new_salt.length * 2) + newPasswordBytes.length)];
                        System.arraycopy(new_salt, 0, hash, 0, new_salt.length);
                        System.arraycopy(newPasswordBytes, 0, hash, new_salt.length, newPasswordBytes.length);
                        System.arraycopy(new_salt, 0, hash, hash.length - new_salt.length, new_salt.length);
                        TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                        tL_account_passwordInputSettings.flags |= 1;
                        req.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
                        req.new_settings.new_password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                        req.new_settings.new_salt = new_salt;
                        if (email.length() > 0) {
                            TL_account_passwordInputSettings tL_account_passwordInputSettings2 = req.new_settings;
                            tL_account_passwordInputSettings2.flags = 2 | tL_account_passwordInputSettings2.flags;
                            req.new_settings.email = email.trim();
                        }
                        firstPassword = email;
                    }
                }
                shakeField(2);
                return;
            } else {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                shakeField(1);
                return;
            }
        }
        showEditDoneProgress(true, true);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.ui.PaymentFormActivity$32$1$1 */
                    class C15551 implements DialogInterface.OnClickListener {
                        C15551() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            PaymentFormActivity.this.waitingForEmail = true;
                            PaymentFormActivity.this.currentPassword.email_unconfirmed_pattern = firstPassword;
                            PaymentFormActivity.this.updatePasswordFields();
                        }
                    }

                    public void run() {
                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                        if (clear) {
                            PaymentFormActivity.this.currentPassword = new TL_account_noPassword();
                            PaymentFormActivity.this.delegate.currentPasswordUpdated(PaymentFormActivity.this.currentPassword);
                            PaymentFormActivity.this.finishFragment();
                        } else if (error == null && (response instanceof TL_boolTrue)) {
                            if (PaymentFormActivity.this.getParentActivity() != null) {
                                PaymentFormActivity.this.goToNextStep();
                            }
                        } else if (error != null) {
                            if (error.text.equals("EMAIL_UNCONFIRMED")) {
                                Builder builder = new Builder(PaymentFormActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C15551());
                                builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", R.string.YourEmailAlmostThereText));
                                builder.setTitle(LocaleController.getString("YourEmailAlmostThere", R.string.YourEmailAlmostThere));
                                Dialog dialog = PaymentFormActivity.this.showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            } else if (error.text.equals("EMAIL_INVALID")) {
                                PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
                            } else if (error.text.startsWith("FLOOD_WAIT")) {
                                String timeString;
                                int time = Utilities.parseInt(error.text).intValue();
                                if (time < 60) {
                                    timeString = LocaleController.formatPluralString("Seconds", time);
                                } else {
                                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                }
                                PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                            } else {
                                PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }
                    }
                });
            }
        }, 10);
    }

    private boolean sendCardData() {
        Integer month;
        Integer parseInt;
        String[] args = this.inputFields[1].getText().toString().split("/");
        if (args.length == 2) {
            month = Utilities.parseInt(args[0]);
            parseInt = Utilities.parseInt(args[1]);
        } else {
            month = null;
            parseInt = null;
        }
        Integer year = parseInt;
        int i = 3;
        Card card = new Card(r1.inputFields[0].getText().toString(), month, year, r1.inputFields[3].getText().toString(), r1.inputFields[2].getText().toString(), null, null, null, null, r1.inputFields[5].getText().toString(), r1.inputFields[4].getText().toString(), null);
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
                                class C15571 implements Runnable {
                                    C15571() {
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
                                        AndroidUtilities.runOnUIThread(new C15571());
                                    }
                                }

                                public void onError(Exception error) {
                                    if (!PaymentFormActivity.this.canceled) {
                                        PaymentFormActivity.this.showEditDoneProgress(true, false);
                                        PaymentFormActivity.this.setDonePressed(false);
                                        if (!(error instanceof APIConnectionException)) {
                                            if (!(error instanceof APIException)) {
                                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
                                            }
                                        }
                                        AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", R.string.PaymentConnectionFailed));
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
            final TLObject req = this.validateRequest;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    if (response instanceof TL_payments_validatedRequestedInfo) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.PaymentFormActivity$34$1$1 */
                            class C22131 implements RequestDelegate {
                                C22131() {
                                }

                                public void run(TLObject response, TL_error error) {
                                }
                            }

                            public void run() {
                                PaymentFormActivity.this.requestedInfo = (TL_payments_validatedRequestedInfo) response;
                                if (!(PaymentFormActivity.this.paymentForm.saved_info == null || PaymentFormActivity.this.saveShippingInfo)) {
                                    TL_payments_clearSavedInfo req = new TL_payments_clearSavedInfo();
                                    req.info = true;
                                    ConnectionsManager.getInstance(PaymentFormActivity.this.currentAccount).sendRequest(req, new C22131());
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
                                if (error != null) {
                                    int i;
                                    String str = error.text;
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
                                            AlertsCreator.processError(PaymentFormActivity.this.currentAccount, error, PaymentFormActivity.this, req, new Object[0]);
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
        TL_paymentRequestedInfo info = new TL_paymentRequestedInfo();
        if (this.paymentForm.invoice.name_requested) {
            info.name = this.inputFields[6].getText().toString();
            info.flags |= 1;
        }
        if (this.paymentForm.invoice.phone_requested) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(this.inputFields[8].getText().toString());
            stringBuilder.append(this.inputFields[9].getText().toString());
            info.phone = stringBuilder.toString();
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
            info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : TtmlNode.ANONYMOUS_REGION_ID;
            info.shipping_address.post_code = this.inputFields[5].getText().toString();
            info.flags |= 8;
        }
        return info;
    }

    private void sendData() {
        if (!this.canceled) {
            showEditDoneProgress(false, true);
            final TL_payments_sendPaymentForm req = new TL_payments_sendPaymentForm();
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
                req.flags = 1 | req.flags;
            }
            if (this.shippingOption != null) {
                req.shipping_option_id = this.shippingOption.id;
                req.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.ui.PaymentFormActivity$35$1 */
                class C15601 implements Runnable {
                    C15601() {
                    }

                    public void run() {
                        PaymentFormActivity.this.goToNextStep();
                    }
                }

                public void run(final TLObject response, final TL_error error) {
                    if (response == null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(PaymentFormActivity.this.currentAccount, error, PaymentFormActivity.this, req, new Object[0]);
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(false, false);
                            }
                        });
                    } else if (response instanceof TL_payments_paymentResult) {
                        MessagesController.getInstance(PaymentFormActivity.this.currentAccount).processUpdates(((TL_payments_paymentResult) response).updates, false);
                        AndroidUtilities.runOnUIThread(new C15601());
                    } else if (response instanceof TL_payments_paymentVerficationNeeded) {
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
                                PaymentFormActivity.this.webView.loadUrl(((TL_payments_paymentVerficationNeeded) response).url);
                            }
                        });
                    }
                }
            }, 2);
        }
    }

    private void shakeField(int field) {
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.inputFields[field], 2.0f, 0);
    }

    private void setDonePressed(boolean value) {
        this.donePressed = value;
        this.swipeBackEnabled = value ^ 1;
        this.actionBar.getBackButton().setEnabled(this.donePressed ^ 1);
        if (this.detailSettingsCell[0] != null) {
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
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
        } else {
            final String password = this.inputFields[1].getText().toString();
            showEditDoneProgress(true, true);
            setDonePressed(true);
            final TL_account_getPassword req = new TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error != null) {
                                AlertsCreator.processError(PaymentFormActivity.this.currentAccount, error, PaymentFormActivity.this, req, new Object[0]);
                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                PaymentFormActivity.this.setDonePressed(false);
                            } else if (response instanceof TL_account_noPassword) {
                                PaymentFormActivity.this.passwordOk = false;
                                PaymentFormActivity.this.goToNextStep();
                            } else {
                                TL_account_password currentPassword = response;
                                byte[] passwordBytes = null;
                                try {
                                    passwordBytes = password.getBytes(C0539C.UTF8_NAME);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                byte[] hash = new byte[((currentPassword.current_salt.length * 2) + passwordBytes.length)];
                                System.arraycopy(currentPassword.current_salt, 0, hash, 0, currentPassword.current_salt.length);
                                System.arraycopy(passwordBytes, 0, hash, currentPassword.current_salt.length, passwordBytes.length);
                                System.arraycopy(currentPassword.current_salt, 0, hash, hash.length - currentPassword.current_salt.length, currentPassword.current_salt.length);
                                final TL_account_getTmpPassword req = new TL_account_getTmpPassword();
                                req.password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                                req.period = 1800;
                                ConnectionsManager.getInstance(PaymentFormActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                    public void run(final TLObject response, final TL_error error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                PaymentFormActivity.this.showEditDoneProgress(true, false);
                                                PaymentFormActivity.this.setDonePressed(false);
                                                if (response != null) {
                                                    PaymentFormActivity.this.passwordOk = true;
                                                    UserConfig.getInstance(PaymentFormActivity.this.currentAccount).tmpPassword = (TL_account_tmpPassword) response;
                                                    UserConfig.getInstance(PaymentFormActivity.this.currentAccount).saveConfig(false);
                                                    PaymentFormActivity.this.goToNextStep();
                                                } else if (error.text.equals("PASSWORD_HASH_INVALID")) {
                                                    Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                                                    if (v != null) {
                                                        v.vibrate(200);
                                                    }
                                                    AndroidUtilities.shakeView(PaymentFormActivity.this.inputFields[1], 2.0f, 0);
                                                    PaymentFormActivity.this.inputFields[1].setText(TtmlNode.ANONYMOUS_REGION_ID);
                                                } else {
                                                    AlertsCreator.processError(PaymentFormActivity.this.currentAccount, error, PaymentFormActivity.this, req, new Object[0]);
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

    private void showEditDoneProgress(boolean animateDoneItem, boolean show) {
        final boolean z = show;
        if (this.doneItemAnimation != null) {
            r0.doneItemAnimation.cancel();
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (animateDoneItem && r0.doneItem != null) {
            r0.doneItemAnimation = new AnimatorSet();
            if (z) {
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
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (z) {
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
            r0.doneItemAnimation.setDuration(150);
            r0.doneItemAnimation.start();
        } else if (r0.payTextView != null) {
            r0.doneItemAnimation = new AnimatorSet();
            if (z) {
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
                public void onAnimationEnd(Animator animation) {
                    if (PaymentFormActivity.this.doneItemAnimation != null && PaymentFormActivity.this.doneItemAnimation.equals(animation)) {
                        if (z) {
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
            r0.doneItemAnimation.setDuration(150);
            r0.doneItemAnimation.start();
        }
    }

    public boolean onBackPressed() {
        return this.donePressed ^ 1;
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        ArrayList<ThemeDescription> arrayList = new ArrayList();
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
            for (a = 0; a < r0.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) r0.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(r0.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(r0.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        if (r0.radioCells != null) {
            for (a = 0; a < r0.radioCells.length; a++) {
                arrayList.add(new ThemeDescription(r0.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(r0.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
                arrayList.add(new ThemeDescription(r0.radioCells[a], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(r0.radioCells[a], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
                arrayList.add(new ThemeDescription(r0.radioCells[a], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
            }
        } else {
            arrayList.add(new ThemeDescription(null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
        }
        for (a = 0; a < r0.headerCell.length; a++) {
            arrayList.add(new ThemeDescription(r0.headerCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(r0.headerCell[a], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        }
        for (View themeDescription : r0.sectionCell) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        for (a = 0; a < r0.bottomCell.length; a++) {
            arrayList.add(new ThemeDescription(r0.bottomCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(r0.bottomCell[a], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(r0.bottomCell[a], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        }
        for (a = 0; a < r0.dividers.size(); a++) {
            arrayList.add(new ThemeDescription((View) r0.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
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
        for (a = 1; a < r0.detailSettingsCell.length; a++) {
            arrayList.add(new ThemeDescription(r0.detailSettingsCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(r0.detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(r0.detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
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
