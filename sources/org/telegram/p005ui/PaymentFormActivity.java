package org.telegram.p005ui;

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
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.p003ts.TsExtractor;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.PhoneFormat.C0195PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
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
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0646ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.PaymentInfoCell;
import org.telegram.p005ui.Cells.RadioCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextDetailSettingsCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextPriceCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.ContextProgressView;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.HintEditText;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.C0643xb6caa888;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
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

/* renamed from: org.telegram.ui.PaymentFormActivity */
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
    private TL_account_password currentPassword;
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

    /* renamed from: org.telegram.ui.PaymentFormActivity$10 */
    class C153410 extends WebViewClient {
        C153410() {
        }

        public void onLoadResource(WebView view, String url) {
            try {
                if ("t.me".equals(Uri.parse(url).getHost())) {
                    PaymentFormActivity.this.goToNextStep();
                    return;
                }
            } catch (Exception e) {
            }
            super.onLoadResource(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            PaymentFormActivity.this.webviewLoading = false;
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.updateSavePaymentField();
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if ("t.me".equals(Uri.parse(url).getHost())) {
                    PaymentFormActivity.this.goToNextStep();
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$2 */
    class C15372 implements TextWatcher {
        C15372() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (!PaymentFormActivity.this.ignoreOnTextChange) {
                PaymentFormActivity.this.ignoreOnTextChange = true;
                String text = C0195PhoneFormat.stripExceptNumbers(PaymentFormActivity.this.inputFields[8].getText().toString());
                PaymentFormActivity.this.inputFields[8].setText(text);
                HintEditText phoneField = PaymentFormActivity.this.inputFields[9];
                if (text.length() == 0) {
                    phoneField.setHintText(null);
                    phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                } else {
                    boolean ok = false;
                    String textToSet = null;
                    if (text.length() > 4) {
                        for (int a = 4; a >= 1; a--) {
                            String sub = text.substring(0, a);
                            if (((String) PaymentFormActivity.this.codesMap.get(sub)) != null) {
                                ok = true;
                                textToSet = text.substring(a, text.length()) + PaymentFormActivity.this.inputFields[9].getText().toString();
                                text = sub;
                                PaymentFormActivity.this.inputFields[8].setText(sub);
                                break;
                            }
                        }
                        if (!ok) {
                            textToSet = text.substring(1, text.length()) + PaymentFormActivity.this.inputFields[9].getText().toString();
                            EditTextBoldCursor editTextBoldCursor = PaymentFormActivity.this.inputFields[8];
                            text = text.substring(0, 1);
                            editTextBoldCursor.setText(text);
                        }
                    }
                    String country = (String) PaymentFormActivity.this.codesMap.get(text);
                    boolean set = false;
                    if (!(country == null || PaymentFormActivity.this.countriesArray.indexOf(country) == -1)) {
                        String hint = (String) PaymentFormActivity.this.phoneFormatMap.get(text);
                        if (hint != null) {
                            set = true;
                            phoneField.setHintText(hint.replace('X', 8211));
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

    /* renamed from: org.telegram.ui.PaymentFormActivity$3 */
    class C15383 implements TextWatcher {
        private int actionPosition;
        private int characterAction = -1;

        C15383() {
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
                int a;
                HintEditText phoneField = PaymentFormActivity.this.inputFields[9];
                int start = phoneField.getSelectionStart();
                String phoneChars = "0123456789";
                String str = phoneField.getText().toString();
                if (this.characterAction == 3) {
                    str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1, str.length());
                    start--;
                }
                StringBuilder builder = new StringBuilder(str.length());
                for (a = 0; a < str.length(); a++) {
                    String ch = str.substring(a, a + 1);
                    if (phoneChars.contains(ch)) {
                        builder.append(ch);
                    }
                }
                PaymentFormActivity.this.ignoreOnPhoneChange = true;
                String hint = phoneField.getHintText();
                if (hint != null) {
                    a = 0;
                    while (a < builder.length()) {
                        if (a < hint.length()) {
                            if (hint.charAt(a) == ' ') {
                                builder.insert(a, ' ');
                                a++;
                                if (!(start != a || this.characterAction == 2 || this.characterAction == 3)) {
                                    start++;
                                }
                            }
                            a++;
                        } else {
                            builder.insert(a, ' ');
                            if (!(start != a + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                start++;
                            }
                        }
                    }
                }
                phoneField.setText(builder);
                if (start >= 0) {
                    if (start > phoneField.length()) {
                        start = phoneField.length();
                    }
                    phoneField.setSelection(start);
                }
                phoneField.onTextChange();
                PaymentFormActivity.this.ignoreOnPhoneChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$5 */
    class C15405 extends WebViewClient {
        C15405() {
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

    /* renamed from: org.telegram.ui.PaymentFormActivity$6 */
    class C15416 implements TextWatcher {
        public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
        public static final int MAX_LENGTH_DINERS_CLUB = 14;
        public static final int MAX_LENGTH_STANDARD = 16;
        public final String[] PREFIXES_14 = new String[]{"300", "301", "302", "303", "304", "305", "309", "36", "38", "39"};
        public final String[] PREFIXES_15 = new String[]{"34", "37"};
        public final String[] PREFIXES_16 = new String[]{"2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55", "4", "60", "62", "64", "65", "35"};
        private int actionPosition;
        private int characterAction = -1;

        C15416() {
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

        /* JADX WARNING: Removed duplicated region for block: B:58:0x018f A:{LOOP_END, LOOP:1: B:15:0x00b6->B:58:0x018f} */
        /* JADX WARNING: Removed duplicated region for block: B:81:0x00ea A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:81:0x00ea A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x018f A:{LOOP_END, LOOP:1: B:15:0x00b6->B:58:0x018f} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void afterTextChanged(Editable s) {
            if (!PaymentFormActivity.this.ignoreOnCardChange) {
                int a;
                EditText phoneField = PaymentFormActivity.this.inputFields[0];
                int start = phoneField.getSelectionStart();
                String phoneChars = "0123456789";
                String str = phoneField.getText().toString();
                if (this.characterAction == 3) {
                    str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1, str.length());
                    start--;
                }
                StringBuilder builder = new StringBuilder(str.length());
                for (a = 0; a < str.length(); a++) {
                    String ch = str.substring(a, a + 1);
                    if (phoneChars.contains(ch)) {
                        builder.append(ch);
                    }
                }
                PaymentFormActivity.this.ignoreOnCardChange = true;
                String hint = null;
                int maxLength = 100;
                if (builder.length() > 0) {
                    String currentString = builder.toString();
                    a = 0;
                    while (a < 3) {
                        String[] checkArr;
                        int resultMaxLength;
                        String resultHint;
                        switch (a) {
                            case 0:
                                checkArr = this.PREFIXES_16;
                                resultMaxLength = 16;
                                resultHint = "xxxx xxxx xxxx xxxx";
                                break;
                            case 1:
                                checkArr = this.PREFIXES_15;
                                resultMaxLength = 15;
                                resultHint = "xxxx xxxx xxxx xxx";
                                break;
                            default:
                                checkArr = this.PREFIXES_14;
                                resultMaxLength = 14;
                                resultHint = "xxxx xxxx xxxx xx";
                                break;
                        }
                        for (String prefix : checkArr) {
                            if (currentString.length() <= prefix.length()) {
                                if (prefix.startsWith(currentString)) {
                                    hint = resultHint;
                                    maxLength = resultMaxLength;
                                    if (hint == null) {
                                        a++;
                                    } else if (maxLength != 0 && builder.length() > maxLength) {
                                        builder.setLength(maxLength);
                                    }
                                }
                            } else if (currentString.startsWith(prefix)) {
                                hint = resultHint;
                                maxLength = resultMaxLength;
                                if (hint == null) {
                                }
                            }
                        }
                        if (hint == null) {
                        }
                    }
                    builder.setLength(maxLength);
                }
                if (hint != null) {
                    if (maxLength != 0 && builder.length() == maxLength) {
                        PaymentFormActivity.this.inputFields[1].requestFocus();
                    }
                    phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    a = 0;
                    while (a < builder.length()) {
                        if (a < hint.length()) {
                            if (hint.charAt(a) == ' ') {
                                builder.insert(a, ' ');
                                a++;
                                if (!(start != a || this.characterAction == 2 || this.characterAction == 3)) {
                                    start++;
                                }
                            }
                            a++;
                        } else {
                            builder.insert(a, ' ');
                            if (!(start != a + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                start++;
                            }
                        }
                    }
                } else {
                    phoneField.setTextColor(builder.length() > 0 ? Theme.getColor(Theme.key_windowBackgroundWhiteRedText4) : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                }
                phoneField.setText(builder);
                if (start >= 0) {
                    if (start > phoneField.length()) {
                        start = phoneField.length();
                    }
                    phoneField.setSelection(start);
                }
                PaymentFormActivity.this.ignoreOnCardChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$7 */
    class C15427 implements TextWatcher {
        private int actionPosition;
        private int characterAction = -1;
        private boolean isYear;

        C15427() {
        }

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
                EditText phoneField = PaymentFormActivity.this.inputFields[1];
                int start = phoneField.getSelectionStart();
                String phoneChars = "0123456789";
                String str = phoneField.getText().toString();
                if (this.characterAction == 3) {
                    str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1, str.length());
                    start--;
                }
                StringBuilder builder = new StringBuilder(str.length());
                for (int a = 0; a < str.length(); a++) {
                    String ch = str.substring(a, a + 1);
                    if (phoneChars.contains(ch)) {
                        builder.append(ch);
                    }
                }
                PaymentFormActivity.this.ignoreOnCardChange = true;
                PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                if (builder.length() > 4) {
                    builder.setLength(4);
                }
                if (builder.length() < 2) {
                    this.isYear = false;
                }
                boolean isError = false;
                int value;
                if (this.isYear) {
                    String[] args = new String[(builder.length() > 2 ? 2 : 1)];
                    args[0] = builder.substring(0, 2);
                    if (args.length == 2) {
                        args[1] = builder.substring(2);
                    }
                    if (builder.length() == 4 && args.length == 2) {
                        int month = Utilities.parseInt(args[0]).intValue();
                        int year = Utilities.parseInt(args[1]).intValue() + 2000;
                        Calendar rightNow = Calendar.getInstance();
                        int currentYear = rightNow.get(1);
                        int currentMonth = rightNow.get(2) + 1;
                        if (year < currentYear || (year == currentYear && month < currentMonth)) {
                            PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            isError = true;
                        }
                    } else {
                        value = Utilities.parseInt(args[0]).intValue();
                        if (value > 12 || value == 0) {
                            PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            isError = true;
                        }
                    }
                } else if (builder.length() == 1) {
                    value = Utilities.parseInt(builder.toString()).intValue();
                    if (!(value == 1 || value == 0)) {
                        builder.insert(0, "0");
                        start++;
                    }
                } else if (builder.length() == 2) {
                    value = Utilities.parseInt(builder.toString()).intValue();
                    if (value > 12 || value == 0) {
                        PaymentFormActivity.this.inputFields[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                        isError = true;
                    }
                    start++;
                }
                if (!isError && builder.length() == 4) {
                    PaymentFormActivity.this.inputFields[PaymentFormActivity.this.need_card_name ? 2 : 3].requestFocus();
                }
                if (builder.length() == 2) {
                    builder.append('/');
                    start++;
                } else if (builder.length() > 2 && builder.charAt(2) != '/') {
                    builder.insert(2, '/');
                    start++;
                }
                phoneField.setText(builder);
                if (start >= 0) {
                    if (start > phoneField.length()) {
                        start = phoneField.length();
                    }
                    phoneField.setSelection(start);
                }
                PaymentFormActivity.this.ignoreOnCardChange = false;
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$LinkSpan */
    public class LinkSpan extends ClickableSpan {
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            PaymentFormActivity.this.presentFragment(new TwoStepVerificationActivity(0));
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$PaymentFormActivityDelegate */
    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TL_account_password tL_account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy */
    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        /* synthetic */ TelegramWebviewProxy(PaymentFormActivity x0, C22011 x1) {
            this();
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$TelegramWebviewProxy$$Lambda$0(this, eventName, eventData));
        }

        final /* synthetic */ void lambda$postEvent$0$PaymentFormActivity$TelegramWebviewProxy(String eventName, String eventData) {
            if (PaymentFormActivity.this.getParentActivity() != null && eventName.equals("payment_form_submit")) {
                try {
                    JSONObject jsonObject = new JSONObject(eventData);
                    PaymentFormActivity.this.paymentJson = jsonObject.getJSONObject("credentials").toString();
                    PaymentFormActivity.this.cardName = jsonObject.getString("title");
                } catch (Throwable e) {
                    PaymentFormActivity.this.paymentJson = eventData;
                    FileLog.m14e(e);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$11 */
    class C219911 implements PaymentFormActivityDelegate {
        C219911() {
        }

        public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
            if (PaymentFormActivity.this.delegate != null) {
                PaymentFormActivity.this.delegate.didSelectNewCard(tokenJson, card, saveCard, androidPay);
            }
            if (PaymentFormActivity.this.isWebView) {
                PaymentFormActivity.this.lambda$null$11$ProfileActivity();
            }
            return PaymentFormActivity.this.delegate != null;
        }

        public void onFragmentDestroyed() {
            PaymentFormActivity.this.passwordFragment = null;
        }

        public void currentPasswordUpdated(TL_account_password password) {
            PaymentFormActivity.this.currentPassword = password;
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$12 */
    class C220012 implements TokenCallback {
        C220012() {
        }

        public void onSuccess(Token token) {
            if (!PaymentFormActivity.this.canceled) {
                PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                AndroidUtilities.runOnUIThread(new PaymentFormActivity$12$$Lambda$0(this));
            }
        }

        final /* synthetic */ void lambda$onSuccess$0$PaymentFormActivity$12() {
            PaymentFormActivity.this.goToNextStep();
            PaymentFormActivity.this.showEditDoneProgress(true, false);
            PaymentFormActivity.this.setDonePressed(false);
        }

        public void onError(Exception error) {
            if (!PaymentFormActivity.this.canceled) {
                PaymentFormActivity.this.showEditDoneProgress(true, false);
                PaymentFormActivity.this.setDonePressed(false);
                if ((error instanceof APIConnectionException) || (error instanceof APIException)) {
                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", R.string.PaymentConnectionFailed));
                } else {
                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$1 */
    class C22011 extends ActionBarMenuOnItemClick {
        C22011() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (!PaymentFormActivity.this.donePressed) {
                    PaymentFormActivity.this.lambda$checkDiscard$69$PassportActivity();
                }
            } else if (id == 1 && !PaymentFormActivity.this.donePressed) {
                if (PaymentFormActivity.this.currentStep != 3) {
                    AndroidUtilities.hideKeyboard(PaymentFormActivity.this.getParentActivity().getCurrentFocus());
                }
                if (PaymentFormActivity.this.currentStep == 0) {
                    PaymentFormActivity.this.setDonePressed(true);
                    PaymentFormActivity.this.sendForm();
                } else if (PaymentFormActivity.this.currentStep == 1) {
                    for (int a = 0; a < PaymentFormActivity.this.radioCells.length; a++) {
                        if (PaymentFormActivity.this.radioCells[a].isChecked()) {
                            PaymentFormActivity.this.shippingOption = (TL_shippingOption) PaymentFormActivity.this.requestedInfo.shipping_options.get(a);
                            break;
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

    /* renamed from: org.telegram.ui.PaymentFormActivity$8 */
    class C22028 implements PaymentFormActivityDelegate {
        C22028() {
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

        public void currentPasswordUpdated(TL_account_password password) {
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
        int step;
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[3];
        this.detailSettingsCell = new TextDetailSettingsCell[7];
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
        this.detailSettingsCell = new TextDetailSettingsCell[7];
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
                FileLog.m14e(e);
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
    /* JADX WARNING: Missing block: B:373:0x1153, code:
            r25 = move-exception;
     */
    /* JADX WARNING: Missing block: B:374:0x1154, code:
            org.telegram.messenger.FileLog.m14e(r25);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        if (this.currentStep == 0) {
            this.actionBar.setTitle(LocaleController.getString("PaymentShippingInfo", R.string.PaymentShippingInfo));
        } else if (this.currentStep == 1) {
            this.actionBar.setTitle(LocaleController.getString("PaymentShippingMethod", R.string.PaymentShippingMethod));
        } else if (this.currentStep == 2) {
            this.actionBar.setTitle(LocaleController.getString("PaymentCardInfo", R.string.PaymentCardInfo));
        } else if (this.currentStep == 3) {
            this.actionBar.setTitle(LocaleController.getString("PaymentCardInfo", R.string.PaymentCardInfo));
        } else if (this.currentStep == 4) {
            if (this.paymentForm.invoice.test) {
                this.actionBar.setTitle("Test " + LocaleController.getString("PaymentCheckout", R.string.PaymentCheckout));
            } else {
                this.actionBar.setTitle(LocaleController.getString("PaymentCheckout", R.string.PaymentCheckout));
            }
        } else if (this.currentStep == 5) {
            if (this.paymentForm.invoice.test) {
                this.actionBar.setTitle("Test " + LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt));
            } else {
                this.actionBar.setTitle(LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt));
            }
        } else if (this.currentStep == 6) {
            this.actionBar.setTitle(LocaleController.getString("PaymentPassword", R.string.PaymentPassword));
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C22011());
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.currentStep == 0 || this.currentStep == 1 || this.currentStep == 2 || this.currentStep == 3 || this.currentStep == 4 || this.currentStep == 6) {
            this.doneItem = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m10dp(56.0f));
            this.progressView = new ContextProgressView(context, 1);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setVisibility(4);
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentStep == 4 ? 48.0f : 0.0f));
        this.linearLayout2 = new LinearLayout(context);
        this.linearLayout2.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        int a;
        boolean allowDivider;
        View view;
        User providerUser;
        User user;
        String providerName;
        if (this.currentStep == 0) {
            int i;
            HashMap<String, String> languageMap = new HashMap();
            HashMap<String, String> countryMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    this.countriesArray.add(0, args[2]);
                    this.countriesMap.put(args[2], args[0]);
                    this.codesMap.put(args[0], args[2]);
                    countryMap.put(args[1], args[2]);
                    if (args.length > 3) {
                        this.phoneFormatMap.put(args[0], args[3]);
                    }
                    languageMap.put(args[1], args[2]);
                }
                bufferedReader.close();
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            Collections.sort(this.countriesArray, PaymentFormActivity$$Lambda$0.$instance);
            this.inputFields = new EditTextBoldCursor[10];
            a = 0;
            while (a < 10) {
                ViewGroup container;
                if (a == 0) {
                    this.headerCell[0] = new HeaderCell(context);
                    this.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.headerCell[0].setText(LocaleController.getString("PaymentShippingAddress", R.string.PaymentShippingAddress));
                    this.linearLayout2.addView(this.headerCell[0], LayoutHelper.createLinear(-1, -2));
                } else if (a == 6) {
                    this.sectionCell[0] = new ShadowSectionCell(context);
                    this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
                    this.headerCell[1] = new HeaderCell(context);
                    this.headerCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.headerCell[1].setText(LocaleController.getString("PaymentShippingReceiver", R.string.PaymentShippingReceiver));
                    this.linearLayout2.addView(this.headerCell[1], LayoutHelper.createLinear(-1, -2));
                }
                ViewGroup linearLayout;
                if (a == 8) {
                    linearLayout = new LinearLayout(context);
                    ((LinearLayout) linearLayout).setOrientation(0);
                    this.linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, 48));
                    linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                } else if (a == 9) {
                    container = (ViewGroup) this.inputFields[8].getParent();
                } else {
                    linearLayout = new FrameLayout(context);
                    this.linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, 48));
                    linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    allowDivider = (a == 5 || a == 9) ? false : true;
                    if (allowDivider) {
                        if (a == 7 && !this.paymentForm.invoice.phone_requested) {
                            allowDivider = false;
                        } else if (!(a != 6 || this.paymentForm.invoice.phone_requested || this.paymentForm.invoice.email_requested)) {
                            allowDivider = false;
                        }
                    }
                    if (allowDivider) {
                        view = new View(context);
                        this.dividers.add(view);
                        view.setBackgroundColor(Theme.getColor(Theme.key_divider));
                        linearLayout.addView(view, new LayoutParams(-1, 1, 83));
                    }
                }
                if (a == 9) {
                    this.inputFields[a] = new HintEditText(context);
                } else {
                    this.inputFields[a] = new EditTextBoldCursor(context);
                }
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setBackgroundDrawable(null);
                this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setCursorSize(AndroidUtilities.m10dp(20.0f));
                this.inputFields[a].setCursorWidth(1.5f);
                if (a == 4) {
                    this.inputFields[a].setOnTouchListener(new PaymentFormActivity$$Lambda$1(this));
                    this.inputFields[a].setInputType(0);
                }
                if (a == 9 || a == 8) {
                    this.inputFields[a].setInputType(3);
                } else if (a == 7) {
                    this.inputFields[a].setInputType(1);
                } else {
                    this.inputFields[a].setInputType(16385);
                }
                this.inputFields[a].setImeOptions(268435461);
                switch (a) {
                    case 0:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingAddress1Placeholder", R.string.PaymentShippingAddress1Placeholder));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.shipping_address == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.shipping_address.street_line1);
                            break;
                        }
                    case 1:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingAddress2Placeholder", R.string.PaymentShippingAddress2Placeholder));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.shipping_address == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.shipping_address.street_line2);
                            break;
                        }
                    case 2:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingCityPlaceholder", R.string.PaymentShippingCityPlaceholder));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.shipping_address == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.shipping_address.city);
                            break;
                        }
                    case 3:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingStatePlaceholder", R.string.PaymentShippingStatePlaceholder));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.shipping_address == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.shipping_address.state);
                            break;
                        }
                    case 4:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingCountry", R.string.PaymentShippingCountry));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.shipping_address == null)) {
                            String value = (String) countryMap.get(this.paymentForm.saved_info.shipping_address.country_iso2);
                            this.countryName = this.paymentForm.saved_info.shipping_address.country_iso2;
                            EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
                            if (value == null) {
                                value = this.countryName;
                            }
                            editTextBoldCursor.setText(value);
                            break;
                        }
                    case 5:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingZipPlaceholder", R.string.PaymentShippingZipPlaceholder));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.shipping_address == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.shipping_address.post_code);
                            break;
                        }
                    case 6:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingName", R.string.PaymentShippingName));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.name == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.name);
                            break;
                        }
                    case 7:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
                        if (!(this.paymentForm.saved_info == null || this.paymentForm.saved_info.email == null)) {
                            this.inputFields[a].setText(this.paymentForm.saved_info.email);
                            break;
                        }
                }
                this.inputFields[a].setSelection(this.inputFields[a].length());
                if (a == 8) {
                    this.textView = new TextView(context);
                    this.textView.setText("+");
                    this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.textView.setTextSize(1, 16.0f);
                    container.addView(this.textView, LayoutHelper.createLinear(-2, -2, 17.0f, 12.0f, 0.0f, 6.0f));
                    this.inputFields[a].setPadding(AndroidUtilities.m10dp(10.0f), 0, 0, 0);
                    this.inputFields[a].setGravity(19);
                    this.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(5)});
                    container.addView(this.inputFields[a], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                    this.inputFields[a].addTextChangedListener(new C15372());
                } else if (a == 9) {
                    this.inputFields[a].setPadding(0, 0, 0, 0);
                    this.inputFields[a].setGravity(19);
                    container.addView(this.inputFields[a], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 17.0f, 6.0f));
                    this.inputFields[a].addTextChangedListener(new C15383());
                } else {
                    this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.m10dp(6.0f));
                    this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                    container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                }
                this.inputFields[a].setOnEditorActionListener(new PaymentFormActivity$$Lambda$2(this));
                if (a == 9) {
                    if (this.paymentForm.invoice.email_to_provider || this.paymentForm.invoice.phone_to_provider) {
                        providerUser = null;
                        for (int b = 0; b < this.paymentForm.users.size(); b++) {
                            user = (User) this.paymentForm.users.get(b);
                            if (user.f177id == this.paymentForm.provider_id) {
                                providerUser = user;
                            }
                        }
                        if (providerUser != null) {
                            providerName = ContactsController.formatName(providerUser.first_name, providerUser.last_name);
                        } else {
                            providerName = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        this.bottomCell[1] = new TextInfoPrivacyCell(context);
                        this.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        this.linearLayout2.addView(this.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                        if (this.paymentForm.invoice.email_to_provider && this.paymentForm.invoice.phone_to_provider) {
                            this.bottomCell[1].setText(LocaleController.formatString("PaymentPhoneEmailToProvider", R.string.PaymentPhoneEmailToProvider, providerName));
                        } else if (this.paymentForm.invoice.email_to_provider) {
                            this.bottomCell[1].setText(LocaleController.formatString("PaymentEmailToProvider", R.string.PaymentEmailToProvider, providerName));
                        } else {
                            this.bottomCell[1].setText(LocaleController.formatString("PaymentPhoneToProvider", R.string.PaymentPhoneToProvider, providerName));
                        }
                    } else {
                        this.sectionCell[1] = new ShadowSectionCell(context);
                        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
                    }
                    this.checkCell1 = new TextCheckCell(context);
                    this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    this.checkCell1.setTextAndCheck(LocaleController.getString("PaymentShippingSave", R.string.PaymentShippingSave), this.saveShippingInfo, false);
                    this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
                    this.checkCell1.setOnClickListener(new PaymentFormActivity$$Lambda$3(this));
                    this.bottomCell[0] = new TextInfoPrivacyCell(context);
                    this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.bottomCell[0].setText(LocaleController.getString("PaymentShippingSaveInfo", R.string.PaymentShippingSaveInfo));
                    this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                }
                a++;
            }
            if (!this.paymentForm.invoice.name_requested) {
                ((ViewGroup) this.inputFields[6].getParent()).setVisibility(8);
            }
            if (!this.paymentForm.invoice.phone_requested) {
                ((ViewGroup) this.inputFields[8].getParent()).setVisibility(8);
            }
            if (!this.paymentForm.invoice.email_requested) {
                ((ViewGroup) this.inputFields[7].getParent()).setVisibility(8);
            }
            if (this.paymentForm.invoice.phone_requested) {
                this.inputFields[9].setImeOptions(268435462);
            } else if (this.paymentForm.invoice.email_requested) {
                this.inputFields[7].setImeOptions(268435462);
            } else if (this.paymentForm.invoice.name_requested) {
                this.inputFields[6].setImeOptions(268435462);
            } else {
                this.inputFields[5].setImeOptions(268435462);
            }
            if (this.sectionCell[1] != null) {
                ShadowSectionCell shadowSectionCell = this.sectionCell[1];
                i = (this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested || this.paymentForm.invoice.email_requested) ? 0 : 8;
                shadowSectionCell.setVisibility(i);
            } else if (this.bottomCell[1] != null) {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[1];
                i = (this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested || this.paymentForm.invoice.email_requested) ? 0 : 8;
                textInfoPrivacyCell.setVisibility(i);
            }
            HeaderCell headerCell = this.headerCell[1];
            i = (this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested || this.paymentForm.invoice.email_requested) ? 0 : 8;
            headerCell.setVisibility(i);
            if (!this.paymentForm.invoice.shipping_address_requested) {
                this.headerCell[0].setVisibility(8);
                this.sectionCell[0].setVisibility(8);
                ((ViewGroup) this.inputFields[0].getParent()).setVisibility(8);
                ((ViewGroup) this.inputFields[1].getParent()).setVisibility(8);
                ((ViewGroup) this.inputFields[2].getParent()).setVisibility(8);
                ((ViewGroup) this.inputFields[3].getParent()).setVisibility(8);
                ((ViewGroup) this.inputFields[4].getParent()).setVisibility(8);
                ((ViewGroup) this.inputFields[5].getParent()).setVisibility(8);
            }
            if (this.paymentForm.saved_info == null || TextUtils.isEmpty(this.paymentForm.saved_info.phone)) {
                fillNumber(null);
            } else {
                fillNumber(this.paymentForm.saved_info.phone);
            }
            if (this.inputFields[8].length() == 0 && this.paymentForm.invoice.phone_requested && (this.paymentForm.saved_info == null || TextUtils.isEmpty(this.paymentForm.saved_info.phone))) {
                String country = null;
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager != null) {
                        country = telephonyManager.getSimCountryIso().toUpperCase();
                    }
                } catch (Throwable e2) {
                    FileLog.m14e(e2);
                }
                if (country != null) {
                    String countryName = (String) languageMap.get(country);
                    if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                        this.inputFields[8].setText((CharSequence) this.countriesMap.get(countryName));
                    }
                }
            }
        } else if (this.currentStep == 2) {
            JSONObject jSONObject;
            if (this.paymentForm.native_params != null) {
                try {
                    jSONObject = new JSONObject(this.paymentForm.native_params.data);
                    try {
                        String androidPayKey = jSONObject.getString("android_pay_public_key");
                        if (!TextUtils.isEmpty(androidPayKey)) {
                            this.androidPayPublicKey = androidPayKey;
                        }
                    } catch (Exception e3) {
                        this.androidPayPublicKey = null;
                    }
                    try {
                        this.androidPayBackgroundColor = jSONObject.getInt("android_pay_bgcolor") | Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
                    } catch (Exception e4) {
                        this.androidPayBackgroundColor = -1;
                    }
                    try {
                        this.androidPayBlackTheme = jSONObject.getBoolean("android_pay_inverse");
                    } catch (Exception e5) {
                        this.androidPayBlackTheme = false;
                    }
                } catch (Throwable e22) {
                    FileLog.m14e(e22);
                }
            }
            if (this.isWebView) {
                if (this.androidPayPublicKey != null) {
                    initAndroidPay(context);
                }
                this.androidPayContainer = new FrameLayout(context);
                this.androidPayContainer.setId(fragment_container_id);
                this.androidPayContainer.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.androidPayContainer.setVisibility(8);
                this.linearLayout2.addView(this.androidPayContainer, LayoutHelper.createLinear(-1, 48));
                this.webviewLoading = true;
                showEditDoneProgress(true, true);
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItem.getImageView().setVisibility(4);
                this.webView = new WebView(context) {
                    public boolean onTouchEvent(MotionEvent event) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouchEvent(event);
                    }
                };
                this.webView.getSettings().setJavaScriptEnabled(true);
                this.webView.getSettings().setDomStorageEnabled(true);
                if (VERSION.SDK_INT >= 21) {
                    this.webView.getSettings().setMixedContentMode(0);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
                }
                if (VERSION.SDK_INT >= 17) {
                    this.webView.addJavascriptInterface(new TelegramWebviewProxy(this, null), "TelegramWebviewProxy");
                }
                this.webView.setWebViewClient(new C15405());
                this.linearLayout2.addView(this.webView, LayoutHelper.createFrame(-1, -2.0f));
                this.sectionCell[2] = new ShadowSectionCell(context);
                this.linearLayout2.addView(this.sectionCell[2], LayoutHelper.createLinear(-1, -2));
                this.checkCell1 = new TextCheckCell(context);
                this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.checkCell1.setTextAndCheck(LocaleController.getString("PaymentCardSavePaymentInformation", R.string.PaymentCardSavePaymentInformation), this.saveCardInfo, false);
                this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
                this.checkCell1.setOnClickListener(new PaymentFormActivity$$Lambda$4(this));
                this.bottomCell[0] = new TextInfoPrivacyCell(context);
                this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                updateSavePaymentField();
                this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
            } else {
                if (this.paymentForm.native_params != null) {
                    jSONObject = new JSONObject(this.paymentForm.native_params.data);
                    try {
                        this.need_card_country = jSONObject.getBoolean("need_country");
                    } catch (Exception e6) {
                        this.need_card_country = false;
                    }
                    try {
                        this.need_card_postcode = jSONObject.getBoolean("need_zip");
                    } catch (Exception e7) {
                        this.need_card_postcode = false;
                    }
                    try {
                        this.need_card_name = jSONObject.getBoolean("need_cardholder_name");
                    } catch (Exception e8) {
                        this.need_card_name = false;
                    }
                    try {
                        this.stripeApiKey = jSONObject.getString("publishable_key");
                    } catch (Exception e9) {
                        this.stripeApiKey = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                }
                initAndroidPay(context);
                this.inputFields = new EditTextBoldCursor[6];
                a = 0;
                while (a < 6) {
                    if (a == 0) {
                        this.headerCell[0] = new HeaderCell(context);
                        this.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.headerCell[0].setText(LocaleController.getString("PaymentCardTitle", R.string.PaymentCardTitle));
                        this.linearLayout2.addView(this.headerCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 4) {
                        this.headerCell[1] = new HeaderCell(context);
                        this.headerCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.headerCell[1].setText(LocaleController.getString("PaymentBillingAddress", R.string.PaymentBillingAddress));
                        this.linearLayout2.addView(this.headerCell[1], LayoutHelper.createLinear(-1, -2));
                    }
                    allowDivider = (a == 3 || a == 5 || (a == 4 && !this.need_card_postcode)) ? false : true;
                    view = new FrameLayout(context);
                    this.linearLayout2.addView(view, LayoutHelper.createLinear(-1, 48));
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.inputFields[a] = new EditTextBoldCursor(context);
                    this.inputFields[a].setTag(Integer.valueOf(a));
                    this.inputFields[a].setTextSize(1, 16.0f);
                    this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                    this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputFields[a].setBackgroundDrawable(null);
                    this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputFields[a].setCursorSize(AndroidUtilities.m10dp(20.0f));
                    this.inputFields[a].setCursorWidth(1.5f);
                    if (a == 3) {
                        this.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(3)});
                        this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
                        this.inputFields[a].setTypeface(Typeface.DEFAULT);
                        this.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else if (a == 0) {
                        this.inputFields[a].setInputType(2);
                    } else if (a == 4) {
                        this.inputFields[a].setOnTouchListener(new PaymentFormActivity$$Lambda$5(this));
                        this.inputFields[a].setInputType(0);
                    } else if (a == 1) {
                        this.inputFields[a].setInputType(16386);
                    } else if (a == 2) {
                        this.inputFields[a].setInputType(4097);
                    } else {
                        this.inputFields[a].setInputType(16385);
                    }
                    this.inputFields[a].setImeOptions(268435461);
                    switch (a) {
                        case 0:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardNumber", R.string.PaymentCardNumber));
                            break;
                        case 1:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardExpireDate", R.string.PaymentCardExpireDate));
                            break;
                        case 2:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardName", R.string.PaymentCardName));
                            break;
                        case 3:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardCvv", R.string.PaymentCardCvv));
                            break;
                        case 4:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentShippingCountry", R.string.PaymentShippingCountry));
                            break;
                        case 5:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentShippingZipPlaceholder", R.string.PaymentShippingZipPlaceholder));
                            break;
                    }
                    if (a == 0) {
                        this.inputFields[a].addTextChangedListener(new C15416());
                    } else if (a == 1) {
                        this.inputFields[a].addTextChangedListener(new C15427());
                    }
                    this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.m10dp(6.0f));
                    this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                    view.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                    this.inputFields[a].setOnEditorActionListener(new PaymentFormActivity$$Lambda$6(this));
                    if (a == 3) {
                        this.sectionCell[0] = new ShadowSectionCell(context);
                        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 5) {
                        this.sectionCell[2] = new ShadowSectionCell(context);
                        this.linearLayout2.addView(this.sectionCell[2], LayoutHelper.createLinear(-1, -2));
                        this.checkCell1 = new TextCheckCell(context);
                        this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        this.checkCell1.setTextAndCheck(LocaleController.getString("PaymentCardSavePaymentInformation", R.string.PaymentCardSavePaymentInformation), this.saveCardInfo, false);
                        this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
                        this.checkCell1.setOnClickListener(new PaymentFormActivity$$Lambda$7(this));
                        this.bottomCell[0] = new TextInfoPrivacyCell(context);
                        this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        updateSavePaymentField();
                        this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 0) {
                        this.androidPayContainer = new FrameLayout(context);
                        this.androidPayContainer.setId(fragment_container_id);
                        this.androidPayContainer.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        this.androidPayContainer.setVisibility(8);
                        view.addView(this.androidPayContainer, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 4.0f, 0.0f));
                    }
                    if (allowDivider) {
                        view = new View(context);
                        this.dividers.add(view);
                        view.setBackgroundColor(Theme.getColor(Theme.key_divider));
                        view.addView(view, new LayoutParams(-1, 1, 83));
                    }
                    if ((a == 4 && !this.need_card_country) || ((a == 5 && !this.need_card_postcode) || (a == 2 && !this.need_card_name))) {
                        view.setVisibility(8);
                    }
                    a++;
                }
                if (!(this.need_card_country || this.need_card_postcode)) {
                    this.headerCell[1].setVisibility(8);
                    this.sectionCell[0].setVisibility(8);
                }
                if (this.need_card_postcode) {
                    this.inputFields[5].setImeOptions(268435462);
                } else {
                    this.inputFields[3].setImeOptions(268435462);
                }
            }
        } else if (this.currentStep == 1) {
            int count = this.requestedInfo.shipping_options.size();
            this.radioCells = new RadioCell[count];
            a = 0;
            while (a < count) {
                TL_shippingOption shippingOption = (TL_shippingOption) this.requestedInfo.shipping_options.get(a);
                this.radioCells[a] = new RadioCell(context);
                this.radioCells[a].setTag(Integer.valueOf(a));
                this.radioCells[a].setBackgroundDrawable(Theme.getSelectorDrawable(true));
                RadioCell radioCell = this.radioCells[a];
                r5 = new Object[2];
                r5[0] = getTotalPriceString(shippingOption.prices);
                r5[1] = shippingOption.title;
                radioCell.setText(String.format("%s - %s", r5), a == 0, a != count + -1);
                this.radioCells[a].setOnClickListener(new PaymentFormActivity$$Lambda$8(this));
                this.linearLayout2.addView(this.radioCells[a]);
                a++;
            }
            this.bottomCell[0] = new TextInfoPrivacyCell(context);
            this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
        } else if (this.currentStep == 3) {
            this.inputFields = new EditTextBoldCursor[2];
            a = 0;
            while (a < 2) {
                if (a == 0) {
                    this.headerCell[0] = new HeaderCell(context);
                    this.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.headerCell[0].setText(LocaleController.getString("PaymentCardTitle", R.string.PaymentCardTitle));
                    this.linearLayout2.addView(this.headerCell[0], LayoutHelper.createLinear(-1, -2));
                }
                view = new FrameLayout(context);
                this.linearLayout2.addView(view, LayoutHelper.createLinear(-1, 48));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                allowDivider = a != 1;
                if (allowDivider) {
                    if (a == 7 && !this.paymentForm.invoice.phone_requested) {
                        allowDivider = false;
                    } else if (!(a != 6 || this.paymentForm.invoice.phone_requested || this.paymentForm.invoice.email_requested)) {
                        allowDivider = false;
                    }
                }
                if (allowDivider) {
                    view = new View(context);
                    this.dividers.add(view);
                    view.setBackgroundColor(Theme.getColor(Theme.key_divider));
                    view.addView(view, new LayoutParams(-1, 1, 83));
                }
                this.inputFields[a] = new EditTextBoldCursor(context);
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setBackgroundDrawable(null);
                this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setCursorSize(AndroidUtilities.m10dp(20.0f));
                this.inputFields[a].setCursorWidth(1.5f);
                if (a == 0) {
                    this.inputFields[a].setOnTouchListener(PaymentFormActivity$$Lambda$9.$instance);
                    this.inputFields[a].setInputType(0);
                } else {
                    this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                    this.inputFields[a].setTypeface(Typeface.DEFAULT);
                }
                this.inputFields[a].setImeOptions(268435462);
                switch (a) {
                    case 0:
                        this.inputFields[a].setText(this.paymentForm.saved_credentials.title);
                        break;
                    case 1:
                        this.inputFields[a].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                        this.inputFields[a].requestFocus();
                        break;
                }
                this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.m10dp(6.0f));
                this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                view.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                this.inputFields[a].setOnEditorActionListener(new PaymentFormActivity$$Lambda$10(this));
                if (a == 1) {
                    this.bottomCell[0] = new TextInfoPrivacyCell(context);
                    this.bottomCell[0].setText(LocaleController.formatString("PaymentConfirmationMessage", R.string.PaymentConfirmationMessage, this.paymentForm.saved_credentials.title));
                    this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                    this.settingsCell1 = new TextSettingsCell(context);
                    this.settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    this.settingsCell1.setText(LocaleController.getString("PaymentConfirmationNewCard", R.string.PaymentConfirmationNewCard), false);
                    this.linearLayout2.addView(this.settingsCell1, LayoutHelper.createLinear(-1, -2));
                    this.settingsCell1.setOnClickListener(new PaymentFormActivity$$Lambda$11(this));
                    this.bottomCell[1] = new TextInfoPrivacyCell(context);
                    this.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                }
                a++;
            }
        } else if (this.currentStep == 4 || this.currentStep == 5) {
            this.paymentInfoCell = new PaymentInfoCell(context);
            this.paymentInfoCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.paymentInfoCell.setInvoice((TL_messageMediaInvoice) this.messageObject.messageOwner.media, this.currentBotName);
            this.linearLayout2.addView(this.paymentInfoCell, LayoutHelper.createLinear(-1, -2));
            this.sectionCell[0] = new ShadowSectionCell(context);
            this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
            ArrayList<TL_labeledPrice> arrayList = new ArrayList(this.paymentForm.invoice.prices);
            if (this.shippingOption != null) {
                arrayList.addAll(this.shippingOption.prices);
            }
            String totalPrice = getTotalPriceString(arrayList);
            for (a = 0; a < arrayList.size(); a++) {
                TL_labeledPrice price = (TL_labeledPrice) arrayList.get(a);
                view = new TextPriceCell(context);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view.setTextAndValue(price.label, LocaleController.getInstance().formatCurrencyString(price.amount, this.paymentForm.invoice.currency), false);
                this.linearLayout2.addView(view);
            }
            view = new TextPriceCell(context);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view.setTextAndValue(LocaleController.getString("PaymentTransactionTotal", R.string.PaymentTransactionTotal), totalPrice, true);
            this.linearLayout2.addView(view);
            view = new View(context);
            this.dividers.add(view);
            view.setBackgroundColor(Theme.getColor(Theme.key_divider));
            this.linearLayout2.addView(view, new LayoutParams(-1, 1, 83));
            this.detailSettingsCell[0] = new TextDetailSettingsCell(context);
            this.detailSettingsCell[0].setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.detailSettingsCell[0].setTextAndValue(this.cardName, LocaleController.getString("PaymentCheckoutMethod", R.string.PaymentCheckoutMethod), true);
            this.linearLayout2.addView(this.detailSettingsCell[0]);
            if (this.currentStep == 4) {
                this.detailSettingsCell[0].setOnClickListener(new PaymentFormActivity$$Lambda$12(this));
            }
            providerUser = null;
            for (a = 0; a < this.paymentForm.users.size(); a++) {
                user = (User) this.paymentForm.users.get(a);
                if (user.f177id == this.paymentForm.provider_id) {
                    providerUser = user;
                }
            }
            if (providerUser != null) {
                this.detailSettingsCell[1] = new TextDetailSettingsCell(context);
                this.detailSettingsCell[1].setBackgroundDrawable(Theme.getSelectorDrawable(true));
                TextDetailSettingsCell textDetailSettingsCell = this.detailSettingsCell[1];
                providerName = ContactsController.formatName(providerUser.first_name, providerUser.last_name);
                textDetailSettingsCell.setTextAndValue(providerName, LocaleController.getString("PaymentCheckoutProvider", R.string.PaymentCheckoutProvider), true);
                this.linearLayout2.addView(this.detailSettingsCell[1]);
            } else {
                providerName = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (this.validateRequest != null) {
                if (this.validateRequest.info.shipping_address != null) {
                    String address = String.format("%s %s, %s, %s, %s, %s", new Object[]{this.validateRequest.info.shipping_address.street_line1, this.validateRequest.info.shipping_address.street_line2, this.validateRequest.info.shipping_address.city, this.validateRequest.info.shipping_address.state, this.validateRequest.info.shipping_address.country_iso2, this.validateRequest.info.shipping_address.post_code});
                    this.detailSettingsCell[2] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[2].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[2].setTextAndValue(address, LocaleController.getString("PaymentShippingAddress", R.string.PaymentShippingAddress), true);
                    this.linearLayout2.addView(this.detailSettingsCell[2]);
                }
                if (this.validateRequest.info.name != null) {
                    this.detailSettingsCell[3] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[3].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[3].setTextAndValue(this.validateRequest.info.name, LocaleController.getString("PaymentCheckoutName", R.string.PaymentCheckoutName), true);
                    this.linearLayout2.addView(this.detailSettingsCell[3]);
                }
                if (this.validateRequest.info.phone != null) {
                    this.detailSettingsCell[4] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[4].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[4].setTextAndValue(C0195PhoneFormat.getInstance().format(this.validateRequest.info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", R.string.PaymentCheckoutPhoneNumber), true);
                    this.linearLayout2.addView(this.detailSettingsCell[4]);
                }
                if (this.validateRequest.info.email != null) {
                    this.detailSettingsCell[5] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[5].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[5].setTextAndValue(this.validateRequest.info.email, LocaleController.getString("PaymentCheckoutEmail", R.string.PaymentCheckoutEmail), true);
                    this.linearLayout2.addView(this.detailSettingsCell[5]);
                }
                if (this.shippingOption != null) {
                    this.detailSettingsCell[6] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[6].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[6].setTextAndValue(this.shippingOption.title, LocaleController.getString("PaymentCheckoutShippingMethod", R.string.PaymentCheckoutShippingMethod), false);
                    this.linearLayout2.addView(this.detailSettingsCell[6]);
                }
            }
            if (this.currentStep == 4) {
                this.bottomLayout = new FrameLayout(context);
                this.bottomLayout.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
                this.bottomLayout.setOnClickListener(new PaymentFormActivity$$Lambda$13(this, providerName, totalPrice));
                this.payTextView = new TextView(context);
                this.payTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText6));
                this.payTextView.setText(LocaleController.formatString("PaymentCheckoutPay", R.string.PaymentCheckoutPay, totalPrice));
                this.payTextView.setTextSize(1, 14.0f);
                this.payTextView.setGravity(17);
                this.payTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.bottomLayout.addView(this.payTextView, LayoutHelper.createFrame(-1, -1.0f));
                this.progressViewButton = new ContextProgressView(context, 0);
                this.progressViewButton.setVisibility(4);
                this.bottomLayout.addView(this.progressViewButton, LayoutHelper.createFrame(-1, -1.0f));
                view = new View(context);
                view.setBackgroundResource(R.drawable.header_shadow_reverse);
                frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                this.doneItem.setEnabled(false);
                this.doneItem.getImageView().setVisibility(4);
                this.webView = new WebView(context) {
                    public boolean onTouchEvent(MotionEvent event) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return super.onTouchEvent(event);
                    }
                };
                this.webView.setBackgroundColor(-1);
                this.webView.getSettings().setJavaScriptEnabled(true);
                this.webView.getSettings().setDomStorageEnabled(true);
                if (VERSION.SDK_INT >= 21) {
                    this.webView.getSettings().setMixedContentMode(0);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
                }
                this.webView.setWebViewClient(new C153410());
                frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
                this.webView.setVisibility(8);
            }
            this.sectionCell[1] = new ShadowSectionCell(context);
            this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        } else if (this.currentStep == 6) {
            this.bottomCell[2] = new TextInfoPrivacyCell(context);
            this.bottomCell[2].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.bottomCell[2], LayoutHelper.createLinear(-1, -2));
            this.settingsCell1 = new TextSettingsCell(context);
            this.settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.settingsCell1.setTag(Theme.key_windowBackgroundWhiteRedText3);
            this.settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            this.settingsCell1.setText(LocaleController.getString("AbortPassword", R.string.AbortPassword), false);
            this.linearLayout2.addView(this.settingsCell1, LayoutHelper.createLinear(-1, -2));
            this.settingsCell1.setOnClickListener(new PaymentFormActivity$$Lambda$14(this));
            this.inputFields = new EditTextBoldCursor[3];
            a = 0;
            while (a < 3) {
                if (a == 0) {
                    this.headerCell[0] = new HeaderCell(context);
                    this.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.headerCell[0].setText(LocaleController.getString("PaymentPasswordTitle", R.string.PaymentPasswordTitle));
                    this.linearLayout2.addView(this.headerCell[0], LayoutHelper.createLinear(-1, -2));
                } else if (a == 2) {
                    this.headerCell[1] = new HeaderCell(context);
                    this.headerCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.headerCell[1].setText(LocaleController.getString("PaymentPasswordEmailTitle", R.string.PaymentPasswordEmailTitle));
                    this.linearLayout2.addView(this.headerCell[1], LayoutHelper.createLinear(-1, -2));
                }
                view = new FrameLayout(context);
                this.linearLayout2.addView(view, LayoutHelper.createLinear(-1, 48));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                if (a == 0) {
                    view = new View(context);
                    this.dividers.add(view);
                    view.setBackgroundColor(Theme.getColor(Theme.key_divider));
                    view.addView(view, new LayoutParams(-1, 1, 83));
                }
                this.inputFields[a] = new EditTextBoldCursor(context);
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setBackgroundDrawable(null);
                this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setCursorSize(AndroidUtilities.m10dp(20.0f));
                this.inputFields[a].setCursorWidth(1.5f);
                if (a == 0 || a == 1) {
                    this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                    this.inputFields[a].setTypeface(Typeface.DEFAULT);
                    this.inputFields[a].setImeOptions(268435461);
                } else {
                    this.inputFields[a].setImeOptions(268435462);
                }
                switch (a) {
                    case 0:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEnter", R.string.PaymentPasswordEnter));
                        this.inputFields[a].requestFocus();
                        break;
                    case 1:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentPasswordReEnter", R.string.PaymentPasswordReEnter));
                        break;
                    case 2:
                        this.inputFields[a].setHint(LocaleController.getString("PaymentPasswordEmail", R.string.PaymentPasswordEmail));
                        break;
                }
                this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.m10dp(6.0f));
                this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                view.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                this.inputFields[a].setOnEditorActionListener(new PaymentFormActivity$$Lambda$15(this));
                if (a == 1) {
                    this.bottomCell[0] = new TextInfoPrivacyCell(context);
                    this.bottomCell[0].setText(LocaleController.getString("PaymentPasswordInfo", R.string.PaymentPasswordInfo));
                    this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                } else if (a == 2) {
                    this.bottomCell[1] = new TextInfoPrivacyCell(context);
                    this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
                    this.bottomCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.bottomCell[1], LayoutHelper.createLinear(-1, -2));
                }
                a++;
            }
            updatePasswordFields();
        }
        return this.fragmentView;
    }

    final /* synthetic */ boolean lambda$createView$1$PaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$Lambda$42(this));
            presentFragment(fragment);
        }
        return true;
    }

    final /* synthetic */ void lambda$null$0$PaymentFormActivity(String name, String shortName) {
        this.inputFields[4].setText(name);
        this.countryName = shortName;
    }

    final /* synthetic */ boolean lambda$createView$2$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    final /* synthetic */ void lambda$createView$3$PaymentFormActivity(View v) {
        this.saveShippingInfo = !this.saveShippingInfo;
        this.checkCell1.setChecked(this.saveShippingInfo);
    }

    final /* synthetic */ void lambda$createView$4$PaymentFormActivity(View v) {
        this.saveCardInfo = !this.saveCardInfo;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    final /* synthetic */ boolean lambda$createView$6$PaymentFormActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PaymentFormActivity$$Lambda$41(this));
            presentFragment(fragment);
        }
        return true;
    }

    final /* synthetic */ void lambda$null$5$PaymentFormActivity(String name, String shortName) {
        this.inputFields[4].setText(name);
    }

    final /* synthetic */ boolean lambda$createView$7$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    final /* synthetic */ void lambda$createView$8$PaymentFormActivity(View v) {
        this.saveCardInfo = !this.saveCardInfo;
        this.checkCell1.setChecked(this.saveCardInfo);
    }

    final /* synthetic */ void lambda$createView$9$PaymentFormActivity(View v) {
        int num = ((Integer) v.getTag()).intValue();
        int a1 = 0;
        while (a1 < this.radioCells.length) {
            this.radioCells[a1].setChecked(num == a1, true);
            a1++;
        }
    }

    final /* synthetic */ boolean lambda$createView$11$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneItem.performClick();
        return true;
    }

    final /* synthetic */ void lambda$createView$12$PaymentFormActivity(View v) {
        this.passwordOk = false;
        goToNextStep();
    }

    final /* synthetic */ void lambda$createView$13$PaymentFormActivity(View v) {
        PaymentFormActivity activity = new PaymentFormActivity(this.paymentForm, this.messageObject, 2, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo, null);
        activity.setDelegate(new C22028());
        presentFragment(activity);
    }

    final /* synthetic */ void lambda$createView$15$PaymentFormActivity(String providerName, String totalPrice, View v) {
        if (this.botUser == null || this.botUser.verified) {
            showPayAlert(totalPrice);
            return;
        }
        String botKey = "payment_warning_" + this.botUser.f177id;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        if (preferences.getBoolean(botKey, false)) {
            showPayAlert(totalPrice);
            return;
        }
        preferences.edit().putBoolean(botKey, true).commit();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentWarning", R.string.PaymentWarning));
        builder.setMessage(LocaleController.formatString("PaymentWarningText", R.string.PaymentWarningText, this.currentBotName, providerName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PaymentFormActivity$$Lambda$40(this, totalPrice));
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$createView$17$PaymentFormActivity(View v) {
        Builder builder = new Builder(getParentActivity());
        String text = LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion);
        if (this.currentPassword.has_secure_values) {
            text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", R.string.TurnPasswordOffPassport);
        }
        builder.setMessage(text);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PaymentFormActivity$$Lambda$39(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$16$PaymentFormActivity(DialogInterface dialogInterface, int i) {
        sendSavePassword(true);
    }

    final /* synthetic */ boolean lambda$createView$18$PaymentFormActivity(TextView textView, int i, KeyEvent keyEvent) {
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
            int a;
            if (this.currentPassword == null) {
                this.doneItem.setVisibility(0);
                showEditDoneProgress(true, true);
                this.bottomCell[2].setVisibility(8);
                this.settingsCell1.setVisibility(8);
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
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
                this.doneItem.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell[2];
                String str = "EmailPasswordConfirmText";
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : TtmlNode.ANONYMOUS_REGION_ID;
                textInfoPrivacyCell.setText(LocaleController.formatString(str, R.string.EmailPasswordConfirmText, objArr));
                this.bottomCell[2].setVisibility(0);
                this.settingsCell1.setVisibility(0);
                this.bottomCell[1].setText(TtmlNode.ANONYMOUS_REGION_ID);
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
            this.doneItem.setVisibility(0);
            this.bottomCell[2].setVisibility(8);
            this.settingsCell1.setVisibility(8);
            this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", R.string.PaymentPasswordEmailInfo));
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new PaymentFormActivity$$Lambda$16(this), 10);
        }
    }

    final /* synthetic */ void lambda$loadPasswordInfo$21$PaymentFormActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$37(this, error, response));
    }

    final /* synthetic */ void lambda$null$20$PaymentFormActivity(TL_error error, TLObject response) {
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
                    this.shortPollRunnable = new PaymentFormActivity$$Lambda$38(this);
                    AndroidUtilities.runOnUIThread(this.shortPollRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                    return;
                }
                return;
            }
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
        }
    }

    final /* synthetic */ void lambda$null$19$PaymentFormActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo();
            this.shortPollRunnable = null;
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
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PaymentFormActivity$$Lambda$17(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$showPayAlert$22$PaymentFormActivity(DialogInterface dialogInterface, int i) {
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
                FileLog.m14e(e);
            }
        }
        try {
            if ((this.currentStep == 2 || this.currentStep == 6) && VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
            }
        } catch (Throwable e2) {
            FileLog.m14e(e2);
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
            lambda$null$11$ProfileActivity();
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
            fragmentTransaction.replace(fragment_container_id, walletFragment);
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
            walletFragment.initialize(WalletFragmentInitParams.newBuilder().setMaskedWalletRequest(MaskedWalletRequest.newBuilder().setPaymentMethodTokenizationParameters(parameters).setEstimatedTotalPrice(this.totalPriceDecimal).setCurrencyCode(this.paymentForm.invoice.currency).build()).setMaskedWalletRequestCode(LOAD_MASKED_WALLET_REQUEST_CODE).build());
            this.androidPayContainer.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[]{0.0f, 1.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_MASKED_WALLET_REQUEST_CODE) {
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
                this.passwordFragment.setDelegate(new C219911());
                presentFragment(this.passwordFragment, this.isWebView);
            } else if (this.delegate != null) {
                this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials);
                lambda$checkDiscard$69$PassportActivity();
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
            lambda$checkDiscard$69$PassportActivity();
        } else if (this.currentStep != 6) {
        } else {
            if (this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials)) {
                lambda$checkDiscard$69$PassportActivity();
            } else {
                presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), true);
            }
        }
    }

    private void updateSavePaymentField() {
        if (this.bottomCell[0] != null && this.sectionCell[2] != null) {
            if ((this.paymentForm.password_missing || this.paymentForm.can_save_credentials) && (this.webView == null || !(this.webView == null || this.webviewLoading))) {
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
                return;
            }
            this.checkCell1.setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
    }

    @SuppressLint({"HardwareIds"})
    public void fillNumber(String number) {
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            boolean allowCall = true;
            boolean allowSms = true;
            if (number != null || (tm.getSimState() != 1 && tm.getPhoneType() != 0)) {
                if (VERSION.SDK_INT >= 23) {
                    if (getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                        allowCall = true;
                    } else {
                        allowCall = false;
                    }
                    if (getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0) {
                        allowSms = true;
                    } else {
                        allowSms = false;
                    }
                }
                if (number != null || allowCall || allowSms) {
                    if (number == null) {
                        number = C0195PhoneFormat.stripExceptNumbers(tm.getLine1Number());
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
        } catch (Throwable e) {
            FileLog.m14e(e);
        }
    }

    private void sendSavePassword(boolean clear) {
        String email;
        String firstPassword;
        TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
        if (clear) {
            this.doneItem.setVisibility(0);
            email = null;
            firstPassword = null;
            req.new_settings = new TL_account_passwordInputSettings();
            req.new_settings.flags = 2;
            req.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
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
                req.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
                req.new_settings.new_algo = this.currentPassword.new_algo;
                if (email.length() > 0) {
                    tL_account_passwordInputSettings = req.new_settings;
                    tL_account_passwordInputSettings.flags |= 2;
                    req.new_settings.email = email.trim();
                }
            } else {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
                shakeField(1);
                return;
            }
        }
        showEditDoneProgress(true, true);
        Utilities.globalQueue.postRunnable(new PaymentFormActivity$$Lambda$18(this, clear, email, firstPassword, req));
    }

    final /* synthetic */ void lambda$null$27$PaymentFormActivity(boolean clear, String email, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$33(this, error, clear, response, email));
    }

    final /* synthetic */ void lambda$sendSavePassword$28$PaymentFormActivity(boolean clear, String email, String firstPassword, TL_account_updatePasswordSettings req) {
        RequestDelegate requestDelegate = new PaymentFormActivity$$Lambda$32(this, clear, email);
        if (clear) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        byte[] newPasswordBytes = AndroidUtilities.getStringBytes(firstPassword);
        TL_error error;
        if (this.currentPassword.new_algo instanceof C0643xb6caa888) {
            C0643xb6caa888 algo = this.currentPassword.new_algo;
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

    final /* synthetic */ void lambda$null$26$PaymentFormActivity(TL_error error, boolean clear, TLObject response, String email) {
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            showEditDoneProgress(true, false);
            if (clear) {
                this.currentPassword.has_password = false;
                this.currentPassword.current_algo = null;
                this.delegate.currentPasswordUpdated(this.currentPassword);
                lambda$checkDiscard$69$PassportActivity();
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
                if (error.text.equals("EMAIL_UNCONFIRMED")) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PaymentFormActivity$$Lambda$35(this, email));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", R.string.YourEmailAlmostThereText));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", R.string.YourEmailAlmostThere));
                    Dialog dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else if (error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
                    return;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                    return;
                } else {
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                    return;
                }
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new PaymentFormActivity$$Lambda$34(this, clear), 8);
    }

    final /* synthetic */ void lambda$null$24$PaymentFormActivity(boolean clear, TLObject response2, TL_error error2) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$36(this, error2, response2, clear));
    }

    final /* synthetic */ void lambda$null$23$PaymentFormActivity(TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            this.currentPassword = (TL_account_password) response2;
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            sendSavePassword(clear);
        }
    }

    final /* synthetic */ void lambda$null$25$PaymentFormActivity(String email, DialogInterface dialogInterface, int i) {
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
                new Stripe(this.stripeApiKey).createToken(card, new C220012());
            } catch (Throwable e) {
                FileLog.m14e(e);
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
                this.validateRequest.info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : TtmlNode.ANONYMOUS_REGION_ID;
                this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 8;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new PaymentFormActivity$$Lambda$19(this, this.validateRequest), 2);
        }
    }

    final /* synthetic */ void lambda$sendForm$32$PaymentFormActivity(TLObject req, TLObject response, TL_error error) {
        if (response instanceof TL_payments_validatedRequestedInfo) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$29(this, response));
        } else {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$30(this, error, req));
        }
    }

    final /* synthetic */ void lambda$null$30$PaymentFormActivity(TLObject response) {
        this.requestedInfo = (TL_payments_validatedRequestedInfo) response;
        if (!(this.paymentForm.saved_info == null || this.saveShippingInfo)) {
            TL_payments_clearSavedInfo req1 = new TL_payments_clearSavedInfo();
            req1.info = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, PaymentFormActivity$$Lambda$31.$instance);
        }
        goToNextStep();
        setDonePressed(false);
        showEditDoneProgress(true, false);
    }

    static final /* synthetic */ void lambda$null$29$PaymentFormActivity(TLObject response1, TL_error error1) {
    }

    final /* synthetic */ void lambda$null$31$PaymentFormActivity(TL_error error, TLObject req) {
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
            info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : TtmlNode.ANONYMOUS_REGION_ID;
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
                req.credentials.f101id = this.paymentForm.saved_credentials.f165id;
                req.credentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
            } else if (this.androidPayCredentials != null) {
                req.credentials = this.androidPayCredentials;
            } else {
                req.credentials = new TL_inputPaymentCredentials();
                req.credentials.save = this.saveCardInfo;
                req.credentials.data = new TL_dataJSON();
                req.credentials.data.data = this.paymentJson;
            }
            if (!(this.requestedInfo == null || this.requestedInfo.f166id == null)) {
                req.requested_info_id = this.requestedInfo.f166id;
                req.flags |= 1;
            }
            if (this.shippingOption != null) {
                req.shipping_option_id = this.shippingOption.f172id;
                req.flags |= 2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$Lambda$20(this, req), 2);
        }
    }

    final /* synthetic */ void lambda$sendData$35$PaymentFormActivity(TL_payments_sendPaymentForm req, TLObject response, TL_error error) {
        if (response == null) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$28(this, error, req));
        } else if (response instanceof TL_payments_paymentResult) {
            MessagesController.getInstance(this.currentAccount).processUpdates(((TL_payments_paymentResult) response).updates, false);
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$26(this));
        } else if (response instanceof TL_payments_paymentVerficationNeeded) {
            AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$27(this, response));
        }
    }

    final /* synthetic */ void lambda$null$33$PaymentFormActivity(TLObject response) {
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

    final /* synthetic */ void lambda$null$34$PaymentFormActivity(TL_error error, TL_payments_sendPaymentForm req) {
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
        setDonePressed(false);
        showEditDoneProgress(false, false);
    }

    private void shakeField(int field) {
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.inputFields[field], 2.0f, 0);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PaymentFormActivity$$Lambda$21(this, password, req), 2);
        }
    }

    final /* synthetic */ void lambda$checkPassword$40$PaymentFormActivity(String password, TL_account_getPassword req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$22(this, error, response, password, req));
    }

    final /* synthetic */ void lambda$null$39$PaymentFormActivity(TL_error error, TLObject response, String password, TL_account_getPassword req) {
        if (error == null) {
            TL_account_password currentPassword = (TL_account_password) response;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            } else if (currentPassword.has_password) {
                Utilities.globalQueue.postRunnable(new PaymentFormActivity$$Lambda$23(this, currentPassword, AndroidUtilities.getStringBytes(password)));
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

    final /* synthetic */ void lambda$null$38$PaymentFormActivity(TL_account_password currentPassword, byte[] passwordBytes) {
        byte[] x_bytes;
        if (currentPassword.current_algo instanceof C0643xb6caa888) {
            x_bytes = SRPHelper.getX(passwordBytes, currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        TL_account_getTmpPassword req1 = new TL_account_getTmpPassword();
        req1.period = 1800;
        RequestDelegate requestDelegate = new PaymentFormActivity$$Lambda$24(this, req1);
        TL_error error2;
        if (currentPassword.current_algo instanceof C0643xb6caa888) {
            req1.password = SRPHelper.startCheck(x_bytes, currentPassword.srp_id, currentPassword.srp_B, (C0643xb6caa888) currentPassword.current_algo);
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

    final /* synthetic */ void lambda$null$37$PaymentFormActivity(TL_account_getTmpPassword req1, TLObject response1, TL_error error1) {
        AndroidUtilities.runOnUIThread(new PaymentFormActivity$$Lambda$25(this, response1, error1, req1));
    }

    final /* synthetic */ void lambda$null$36$PaymentFormActivity(TLObject response1, TL_error error1, TL_account_getTmpPassword req1) {
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
            this.inputFields[1].setText(TtmlNode.ANONYMOUS_REGION_ID);
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
            for (a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        if (this.radioCells != null) {
            for (a = 0; a < this.radioCells.length; a++) {
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
                arrayList.add(new ThemeDescription(this.radioCells[a], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
            }
        } else {
            arrayList.add(new ThemeDescription(null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
        }
        for (a = 0; a < this.headerCell.length; a++) {
            arrayList.add(new ThemeDescription(this.headerCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.headerCell[a], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        }
        for (View themeDescription : this.sectionCell) {
            arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        for (a = 0; a < this.bottomCell.length; a++) {
            arrayList.add(new ThemeDescription(this.bottomCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.bottomCell[a], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(this.bottomCell[a], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        }
        for (a = 0; a < this.dividers.size(); a++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        }
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked));
        arrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.settingsCell1, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        for (a = 1; a < this.detailSettingsCell.length; a++) {
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        arrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
