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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultRenderersFactory;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0431R;
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
import org.telegram.tgnet.TLRPC.C0579xb6caa888;
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

    private interface PaymentFormActivityDelegate {
        void currentPasswordUpdated(TL_account_password tL_account_password);

        boolean didSelectNewCard(String str, String str2, boolean z, TL_inputPaymentCredentialsAndroidPay tL_inputPaymentCredentialsAndroidPay);

        void onFragmentDestroyed();
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$1 */
    class C15351 extends ActionBarMenuOnItemClick {
        C15351() {
        }

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

    /* renamed from: org.telegram.ui.PaymentFormActivity$2 */
    class C15362 implements TextWatcher {
        C15362() {
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
                    phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", C0431R.string.PaymentShippingPhoneNumber));
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
                            phoneField.setHintText(hint.replace('X', '\u2013'));
                            phoneField.setHint(null);
                        }
                    }
                    if (!set) {
                        phoneField.setHintText(null);
                        phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", C0431R.string.PaymentShippingPhoneNumber));
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
    class C15373 implements TextWatcher {
        private int actionPosition;
        private int characterAction = -1;

        C15373() {
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

    /* renamed from: org.telegram.ui.PaymentFormActivity$4 */
    class C15384 extends WebView {
        C15384(Context x0) {
            super(x0);
        }

        public boolean onTouchEvent(MotionEvent event) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.onTouchEvent(event);
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$5 */
    class C15395 extends WebViewClient {
        C15395() {
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
    class C15406 implements TextWatcher {
        public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
        public static final int MAX_LENGTH_DINERS_CLUB = 14;
        public static final int MAX_LENGTH_STANDARD = 16;
        public final String[] PREFIXES_14 = new String[]{"300", "301", "302", "303", "304", "305", "309", "36", "38", "39"};
        public final String[] PREFIXES_15 = new String[]{"34", "37"};
        public final String[] PREFIXES_16 = new String[]{"2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55", "4", "60", "62", "64", "65", "35"};
        private int actionPosition;
        private int characterAction = -1;

        C15406() {
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
                                    a++;
                                } else {
                                    builder.setLength(maxLength);
                                }
                            }
                        }
                        if (hint == null) {
                            builder.setLength(maxLength);
                        } else {
                            a++;
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
    class C15417 implements TextWatcher {
        private int actionPosition;
        private int characterAction = -1;
        private boolean isYear;

        C15417() {
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

    /* renamed from: org.telegram.ui.PaymentFormActivity$8 */
    class C15428 implements PaymentFormActivityDelegate {
        C15428() {
        }

        public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TL_inputPaymentCredentialsAndroidPay androidPay) {
            PaymentFormActivity.this.paymentForm.saved_credentials = null;
            PaymentFormActivity.this.paymentJson = tokenJson;
            PaymentFormActivity.this.saveCardInfo = saveCard;
            PaymentFormActivity.this.cardName = card;
            PaymentFormActivity.this.androidPayCredentials = androidPay;
            PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", C0431R.string.PaymentCheckoutMethod), true);
            return false;
        }

        public void onFragmentDestroyed() {
        }

        public void currentPasswordUpdated(TL_account_password password) {
        }
    }

    /* renamed from: org.telegram.ui.PaymentFormActivity$9 */
    class C15439 extends WebView {
        C15439(Context x0) {
            super(x0);
        }

        public boolean onTouchEvent(MotionEvent event) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.onTouchEvent(event);
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

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
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
                    FileLog.m8e(e);
                }
                PaymentFormActivity.this.goToNextStep();
            }
        }
    }

    final /* bridge */ /* synthetic */ void bridge$lambda$0$PaymentFormActivity() {
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
                FileLog.m8e(e);
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
    public android.view.View createView(android.content.Context r48) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r18_0 'container' android.view.ViewGroup) in PHI: PHI: (r18_1 'container' android.view.ViewGroup) = (r18_0 'container' android.view.ViewGroup), (r18_3 'container' android.view.ViewGroup), (r18_4 'container' android.view.View), (r18_4 'container' android.view.View) binds: {(r18_0 'container' android.view.ViewGroup)=B:40:0x021c, (r18_3 'container' android.view.ViewGroup)=B:103:0x058f, (r18_4 'container' android.view.View)=B:115:0x05dc, (r18_4 'container' android.view.View)=B:116:0x05de}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r47 = this;
        r0 = r47;
        r4 = r0.currentStep;
        if (r4 != 0) goto L_0x0417;
    L_0x0006:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentShippingInfo";
        r6 = NUM; // 0x7f0c05de float:1.8612238E38 double:1.0530981405E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
    L_0x0017:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = NUM; // 0x7f07008e float:1.7944866E38 double:1.052935573E-314;
        r4.setBackButtonImage(r5);
        r0 = r47;
        r4 = r0.actionBar;
        r5 = 1;
        r4.setAllowOverlayTitle(r5);
        r0 = r47;
        r4 = r0.actionBar;
        r5 = new org.telegram.ui.PaymentFormActivity$1;
        r0 = r47;
        r5.<init>();
        r4.setActionBarMenuOnItemClick(r5);
        r0 = r47;
        r4 = r0.actionBar;
        r32 = r4.createMenu();
        r0 = r47;
        r4 = r0.currentStep;
        if (r4 == 0) goto L_0x0068;
    L_0x0045:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 1;
        if (r4 == r5) goto L_0x0068;
    L_0x004c:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 2;
        if (r4 == r5) goto L_0x0068;
    L_0x0053:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 3;
        if (r4 == r5) goto L_0x0068;
    L_0x005a:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 == r5) goto L_0x0068;
    L_0x0061:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 6;
        if (r4 != r5) goto L_0x00a2;
    L_0x0068:
        r4 = 1;
        r5 = NUM; // 0x7f0700ac float:1.7944927E38 double:1.052935588E-314;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r32;
        r4 = r0.addItemWithWidth(r4, r5, r6);
        r0 = r47;
        r0.doneItem = r4;
        r4 = new org.telegram.ui.Components.ContextProgressView;
        r5 = 1;
        r0 = r48;
        r4.<init>(r0, r5);
        r0 = r47;
        r0.progressView = r4;
        r0 = r47;
        r4 = r0.doneItem;
        r0 = r47;
        r5 = r0.progressView;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
    L_0x00a2:
        r4 = new android.widget.FrameLayout;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.fragmentView = r4;
        r0 = r47;
        r0 = r0.fragmentView;
        r26 = r0;
        r26 = (android.widget.FrameLayout) r26;
        r0 = r47;
        r4 = r0.fragmentView;
        r5 = "windowBackgroundGray";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r4 = new android.widget.ScrollView;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.scrollView = r4;
        r0 = r47;
        r4 = r0.scrollView;
        r5 = 1;
        r4.setFillViewport(r5);
        r0 = r47;
        r4 = r0.scrollView;
        r5 = "actionBarDefault";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor(r4, r5);
        r0 = r47;
        r0 = r0.scrollView;
        r45 = r0;
        r4 = -1;
        r5 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = 51;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r0 = r47;
        r10 = r0.currentStep;
        r46 = 4;
        r0 = r46;
        if (r10 != r0) goto L_0x0515;
    L_0x00fc:
        r10 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x00fe:
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r26;
        r1 = r45;
        r0.addView(r1, r4);
        r4 = new android.widget.LinearLayout;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.linearLayout2 = r4;
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = 1;
        r4.setOrientation(r5);
        r0 = r47;
        r4 = r0.scrollView;
        r0 = r47;
        r5 = r0.linearLayout2;
        r6 = new android.widget.FrameLayout$LayoutParams;
        r7 = -1;
        r8 = -2;
        r6.<init>(r7, r8);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.currentStep;
        if (r4 != 0) goto L_0x0cc9;
    L_0x0134:
        r30 = new java.util.HashMap;
        r30.<init>();
        r22 = new java.util.HashMap;
        r22.<init>();
        r38 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x01b5 }
        r4 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x01b5 }
        r5 = r48.getResources();	 Catch:{ Exception -> 0x01b5 }
        r5 = r5.getAssets();	 Catch:{ Exception -> 0x01b5 }
        r6 = "countries.txt";	 Catch:{ Exception -> 0x01b5 }
        r5 = r5.open(r6);	 Catch:{ Exception -> 0x01b5 }
        r4.<init>(r5);	 Catch:{ Exception -> 0x01b5 }
        r0 = r38;	 Catch:{ Exception -> 0x01b5 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x01b5 }
    L_0x0159:
        r31 = r38.readLine();	 Catch:{ Exception -> 0x01b5 }
        if (r31 == 0) goto L_0x0518;	 Catch:{ Exception -> 0x01b5 }
    L_0x015f:
        r4 = ";";	 Catch:{ Exception -> 0x01b5 }
        r0 = r31;	 Catch:{ Exception -> 0x01b5 }
        r15 = r0.split(r4);	 Catch:{ Exception -> 0x01b5 }
        r0 = r47;	 Catch:{ Exception -> 0x01b5 }
        r4 = r0.countriesArray;	 Catch:{ Exception -> 0x01b5 }
        r5 = 0;	 Catch:{ Exception -> 0x01b5 }
        r6 = 2;	 Catch:{ Exception -> 0x01b5 }
        r6 = r15[r6];	 Catch:{ Exception -> 0x01b5 }
        r4.add(r5, r6);	 Catch:{ Exception -> 0x01b5 }
        r0 = r47;	 Catch:{ Exception -> 0x01b5 }
        r4 = r0.countriesMap;	 Catch:{ Exception -> 0x01b5 }
        r5 = 2;	 Catch:{ Exception -> 0x01b5 }
        r5 = r15[r5];	 Catch:{ Exception -> 0x01b5 }
        r6 = 0;	 Catch:{ Exception -> 0x01b5 }
        r6 = r15[r6];	 Catch:{ Exception -> 0x01b5 }
        r4.put(r5, r6);	 Catch:{ Exception -> 0x01b5 }
        r0 = r47;	 Catch:{ Exception -> 0x01b5 }
        r4 = r0.codesMap;	 Catch:{ Exception -> 0x01b5 }
        r5 = 0;	 Catch:{ Exception -> 0x01b5 }
        r5 = r15[r5];	 Catch:{ Exception -> 0x01b5 }
        r6 = 2;	 Catch:{ Exception -> 0x01b5 }
        r6 = r15[r6];	 Catch:{ Exception -> 0x01b5 }
        r4.put(r5, r6);	 Catch:{ Exception -> 0x01b5 }
        r4 = 1;	 Catch:{ Exception -> 0x01b5 }
        r4 = r15[r4];	 Catch:{ Exception -> 0x01b5 }
        r5 = 2;	 Catch:{ Exception -> 0x01b5 }
        r5 = r15[r5];	 Catch:{ Exception -> 0x01b5 }
        r0 = r22;	 Catch:{ Exception -> 0x01b5 }
        r0.put(r4, r5);	 Catch:{ Exception -> 0x01b5 }
        r4 = r15.length;	 Catch:{ Exception -> 0x01b5 }
        r5 = 3;	 Catch:{ Exception -> 0x01b5 }
        if (r4 <= r5) goto L_0x01a9;	 Catch:{ Exception -> 0x01b5 }
    L_0x019c:
        r0 = r47;	 Catch:{ Exception -> 0x01b5 }
        r4 = r0.phoneFormatMap;	 Catch:{ Exception -> 0x01b5 }
        r5 = 0;	 Catch:{ Exception -> 0x01b5 }
        r5 = r15[r5];	 Catch:{ Exception -> 0x01b5 }
        r6 = 3;	 Catch:{ Exception -> 0x01b5 }
        r6 = r15[r6];	 Catch:{ Exception -> 0x01b5 }
        r4.put(r5, r6);	 Catch:{ Exception -> 0x01b5 }
    L_0x01a9:
        r4 = 1;	 Catch:{ Exception -> 0x01b5 }
        r4 = r15[r4];	 Catch:{ Exception -> 0x01b5 }
        r5 = 2;	 Catch:{ Exception -> 0x01b5 }
        r5 = r15[r5];	 Catch:{ Exception -> 0x01b5 }
        r0 = r30;	 Catch:{ Exception -> 0x01b5 }
        r0.put(r4, r5);	 Catch:{ Exception -> 0x01b5 }
        goto L_0x0159;
    L_0x01b5:
        r25 = move-exception;
        org.telegram.messenger.FileLog.m8e(r25);
    L_0x01b9:
        r0 = r47;
        r4 = r0.countriesArray;
        r5 = org.telegram.ui.PaymentFormActivity$$Lambda$0.$instance;
        java.util.Collections.sort(r4, r5);
        r4 = 10;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r47;
        r0.inputFields = r4;
        r11 = 0;
    L_0x01cb:
        r4 = 10;
        if (r11 >= r4) goto L_0x0a44;
    L_0x01cf:
        if (r11 != 0) goto L_0x051d;
    L_0x01d1:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentShippingAddress";
        r6 = NUM; // 0x7f0c05d8 float:1.8612226E38 double:1.0530981376E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x0218:
        r4 = 8;
        if (r11 != r4) goto L_0x058b;
    L_0x021c:
        r18 = new android.widget.LinearLayout;
        r0 = r18;
        r1 = r48;
        r0.<init>(r1);
        r4 = r18;
        r4 = (android.widget.LinearLayout) r4;
        r5 = 0;
        r4.setOrientation(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 48;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
    L_0x0249:
        r4 = 9;
        if (r11 != r4) goto L_0x0629;
    L_0x024d:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.HintEditText;
        r0 = r48;
        r5.<init>(r0);
        r4[r11] = r5;
    L_0x025a:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        r4 = 4;
        if (r11 != r4) goto L_0x02e4;
    L_0x02ca:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$1;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnTouchListener(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setInputType(r5);
    L_0x02e4:
        r4 = 9;
        if (r11 == r4) goto L_0x02ec;
    L_0x02e8:
        r4 = 8;
        if (r11 != r4) goto L_0x0638;
    L_0x02ec:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 3;
        r4.setInputType(r5);
    L_0x02f6:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 268435461; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r5);
        switch(r11) {
            case 0: goto L_0x06c4;
            case 1: goto L_0x06fe;
            case 2: goto L_0x0738;
            case 3: goto L_0x0772;
            case 4: goto L_0x07ac;
            case 5: goto L_0x0807;
            case 6: goto L_0x0654;
            case 7: goto L_0x068c;
            default: goto L_0x0305;
        };
    L_0x0305:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.inputFields;
        r5 = r5[r11];
        r5 = r5.length();
        r4.setSelection(r5);
        r4 = 8;
        if (r11 != r4) goto L_0x0841;
    L_0x031c:
        r4 = new android.widget.TextView;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.textView = r4;
        r0 = r47;
        r4 = r0.textView;
        r5 = "+";
        r4.setText(r5);
        r0 = r47;
        r4 = r0.textView;
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.textView;
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r47;
        r10 = r0.textView;
        r4 = -2;
        r5 = -2;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = 0;
        r9 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5, r6, r7, r8, r9);
        r0 = r18;
        r0.addView(r10, r4);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r4.setPadding(r5, r6, r7, r8);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 19;
        r4.setGravity(r5);
        r4 = 1;
        r0 = new android.text.InputFilter[r4];
        r28 = r0;
        r4 = 0;
        r5 = new android.text.InputFilter$LengthFilter;
        r6 = 5;
        r5.<init>(r6);
        r28[r4] = r5;
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r28;
        r4.setFilters(r0);
        r0 = r47;
        r4 = r0.inputFields;
        r10 = r4[r11];
        r4 = 55;
        r5 = -2;
        r6 = 0;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r9 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5, r6, r7, r8, r9);
        r0 = r18;
        r0.addView(r10, r4);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$2;
        r0 = r47;
        r5.<init>();
        r4.addTextChangedListener(r5);
    L_0x03be:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$2;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 9;
        if (r11 != r4) goto L_0x09d5;
    L_0x03d2:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 != 0) goto L_0x03e6;
    L_0x03dc:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_to_provider;
        if (r4 == 0) goto L_0x0a20;
    L_0x03e6:
        r37 = 0;
        r17 = 0;
    L_0x03ea:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r4 = r4.size();
        r0 = r17;
        if (r0 >= r4) goto L_0x08c9;
    L_0x03f8:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r0 = r17;
        r43 = r4.get(r0);
        r43 = (org.telegram.tgnet.TLRPC.User) r43;
        r0 = r43;
        r4 = r0.id;
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.provider_id;
        if (r4 != r5) goto L_0x0414;
    L_0x0412:
        r37 = r43;
    L_0x0414:
        r17 = r17 + 1;
        goto L_0x03ea;
    L_0x0417:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 1;
        if (r4 != r5) goto L_0x0431;
    L_0x041e:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentShippingMethod";
        r6 = NUM; // 0x7f0c05df float:1.861224E38 double:1.053098141E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0431:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 2;
        if (r4 != r5) goto L_0x044b;
    L_0x0438:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentCardInfo";
        r6 = NUM; // 0x7f0c05b6 float:1.8612157E38 double:1.053098121E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x044b:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 3;
        if (r4 != r5) goto L_0x0465;
    L_0x0452:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentCardInfo";
        r6 = NUM; // 0x7f0c05b6 float:1.8612157E38 double:1.053098121E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0465:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 != r5) goto L_0x04b0;
    L_0x046c:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.test;
        if (r4 == 0) goto L_0x049d;
    L_0x0476:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Test ";
        r5 = r5.append(r6);
        r6 = "PaymentCheckout";
        r7 = NUM; // 0x7f0c05bd float:1.8612171E38 double:1.053098124E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x049d:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentCheckout";
        r6 = NUM; // 0x7f0c05bd float:1.8612171E38 double:1.053098124E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x04b0:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 5;
        if (r4 != r5) goto L_0x04fb;
    L_0x04b7:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.test;
        if (r4 == 0) goto L_0x04e8;
    L_0x04c1:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Test ";
        r5 = r5.append(r6);
        r6 = "PaymentReceipt";
        r7 = NUM; // 0x7f0c05d7 float:1.8612224E38 double:1.053098137E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x04e8:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentReceipt";
        r6 = NUM; // 0x7f0c05d7 float:1.8612224E38 double:1.053098137E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x04fb:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 6;
        if (r4 != r5) goto L_0x0017;
    L_0x0502:
        r0 = r47;
        r4 = r0.actionBar;
        r5 = "PaymentPassword";
        r6 = NUM; // 0x7f0c05cc float:1.8612202E38 double:1.0530981317E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setTitle(r5);
        goto L_0x0017;
    L_0x0515:
        r10 = 0;
        goto L_0x00fe;
    L_0x0518:
        r38.close();	 Catch:{ Exception -> 0x01b5 }
        goto L_0x01b9;
    L_0x051d:
        r4 = 6;
        if (r11 != r4) goto L_0x0218;
    L_0x0520:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentShippingReceiver";
        r6 = NUM; // 0x7f0c05e2 float:1.8612246E38 double:1.0530981425E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0218;
    L_0x058b:
        r4 = 9;
        if (r11 != r4) goto L_0x059f;
    L_0x058f:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 8;
        r4 = r4[r5];
        r18 = r4.getParent();
        r18 = (android.view.ViewGroup) r18;
        goto L_0x0249;
    L_0x059f:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 48;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        r4 = 5;
        if (r11 == r4) goto L_0x060e;
    L_0x05c7:
        r4 = 9;
        if (r11 == r4) goto L_0x060e;
    L_0x05cb:
        r13 = 1;
    L_0x05cc:
        if (r13 == 0) goto L_0x05dc;
    L_0x05ce:
        r4 = 7;
        if (r11 != r4) goto L_0x0610;
    L_0x05d1:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0610;
    L_0x05db:
        r13 = 0;
    L_0x05dc:
        if (r13 == 0) goto L_0x0249;
    L_0x05de:
        r24 = new android.view.View;
        r0 = r24;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = "divider";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
        goto L_0x0249;
    L_0x060e:
        r13 = 0;
        goto L_0x05cc;
    L_0x0610:
        r4 = 6;
        if (r11 != r4) goto L_0x05dc;
    L_0x0613:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x05dc;
    L_0x061d:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 != 0) goto L_0x05dc;
    L_0x0627:
        r13 = 0;
        goto L_0x05dc;
    L_0x0629:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r48;
        r5.<init>(r0);
        r4[r11] = r5;
        goto L_0x025a;
    L_0x0638:
        r4 = 7;
        if (r11 != r4) goto L_0x0647;
    L_0x063b:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r4.setInputType(r5);
        goto L_0x02f6;
    L_0x0647:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r4.setInputType(r5);
        goto L_0x02f6;
    L_0x0654:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingName";
        r6 = NUM; // 0x7f0c05e0 float:1.8612242E38 double:1.0530981415E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x066f:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.name;
        if (r4 == 0) goto L_0x0305;
    L_0x0679:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.name;
        r4.setText(r5);
        goto L_0x0305;
    L_0x068c:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingEmailPlaceholder";
        r6 = NUM; // 0x7f0c05dd float:1.8612236E38 double:1.05309814E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x06a7:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.email;
        if (r4 == 0) goto L_0x0305;
    L_0x06b1:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.email;
        r4.setText(r5);
        goto L_0x0305;
    L_0x06c4:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingAddress1Placeholder";
        r6 = NUM; // 0x7f0c05d9 float:1.8612228E38 double:1.053098138E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x06df:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0305;
    L_0x06e9:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.street_line1;
        r4.setText(r5);
        goto L_0x0305;
    L_0x06fe:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingAddress2Placeholder";
        r6 = NUM; // 0x7f0c05da float:1.861223E38 double:1.0530981386E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x0719:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0305;
    L_0x0723:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.street_line2;
        r4.setText(r5);
        goto L_0x0305;
    L_0x0738:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingCityPlaceholder";
        r6 = NUM; // 0x7f0c05db float:1.8612232E38 double:1.053098139E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x0753:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0305;
    L_0x075d:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.city;
        r4.setText(r5);
        goto L_0x0305;
    L_0x0772:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingStatePlaceholder";
        r6 = NUM; // 0x7f0c05e5 float:1.8612253E38 double:1.053098144E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x078d:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0305;
    L_0x0797:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.state;
        r4.setText(r5);
        goto L_0x0305;
    L_0x07ac:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingCountry";
        r6 = NUM; // 0x7f0c05dc float:1.8612234E38 double:1.0530981396E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x07c7:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0305;
    L_0x07d1:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        r4 = r4.country_iso2;
        r0 = r22;
        r44 = r0.get(r4);
        r44 = (java.lang.String) r44;
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        r4 = r4.country_iso2;
        r0 = r47;
        r0.countryName = r4;
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        if (r44 == 0) goto L_0x0800;
    L_0x07f9:
        r0 = r44;
        r4.setText(r0);
        goto L_0x0305;
    L_0x0800:
        r0 = r47;
        r0 = r0.countryName;
        r44 = r0;
        goto L_0x07f9;
    L_0x0807:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingZipPlaceholder";
        r6 = NUM; // 0x7f0c05e6 float:1.8612255E38 double:1.0530981445E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0305;
    L_0x0822:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x0305;
    L_0x082c:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_info;
        r5 = r5.shipping_address;
        r5 = r5.post_code;
        r4.setText(r5);
        goto L_0x0305;
    L_0x0841:
        r4 = 9;
        if (r11 != r4) goto L_0x0887;
    L_0x0845:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r4.setPadding(r5, r6, r7, r8);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 19;
        r4.setGravity(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r10 = r4[r11];
        r4 = -1;
        r5 = -2;
        r6 = 0;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r9 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5, r6, r7, r8, r9);
        r0 = r18;
        r0.addView(r10, r4);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$3;
        r0 = r47;
        r5.<init>();
        r4.addTextChangedListener(r5);
        goto L_0x03be;
    L_0x0887:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x08c7;
    L_0x08a3:
        r4 = 5;
    L_0x08a4:
        r5.setGravity(r4);
        r0 = r47;
        r4 = r0.inputFields;
        r45 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r45;
        r0.addView(r1, r4);
        goto L_0x03be;
    L_0x08c7:
        r4 = 3;
        goto L_0x08a4;
    L_0x08c9:
        if (r37 == 0) goto L_0x09d9;
    L_0x08cb:
        r0 = r37;
        r4 = r0.first_name;
        r0 = r37;
        r5 = r0.last_name;
        r36 = org.telegram.messenger.ContactsController.formatName(r4, r5);
    L_0x08d7:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 == 0) goto L_0x09de;
    L_0x0919:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_to_provider;
        if (r4 == 0) goto L_0x09de;
    L_0x0923:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPhoneEmailToProvider";
        r6 = NUM; // 0x7f0c05d4 float:1.8612218E38 double:1.0530981356E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r36;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
    L_0x093d:
        r4 = new org.telegram.ui.Cells.TextCheckCell;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.checkCell1 = r4;
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = "PaymentShippingSave";
        r6 = NUM; // 0x7f0c05e3 float:1.8612249E38 double:1.053098143E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r0 = r47;
        r6 = r0.saveShippingInfo;
        r7 = 0;
        r4.setTextAndCheck(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.checkCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$3;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentShippingSaveInfo";
        r6 = NUM; // 0x7f0c05e4 float:1.861225E38 double:1.0530981435E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x09d5:
        r11 = r11 + 1;
        goto L_0x01cb;
    L_0x09d9:
        r36 = "";
        goto L_0x08d7;
    L_0x09de:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_to_provider;
        if (r4 == 0) goto L_0x0a04;
    L_0x09e8:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentEmailToProvider";
        r6 = NUM; // 0x7f0c05c8 float:1.8612194E38 double:1.0530981297E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r36;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        goto L_0x093d;
    L_0x0a04:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPhoneToProvider";
        r6 = NUM; // 0x7f0c05d5 float:1.861222E38 double:1.053098136E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r36;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        goto L_0x093d;
    L_0x0a20:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x093d;
    L_0x0a44:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0a60;
    L_0x0a4e:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 6;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0a60:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0a7d;
    L_0x0a6a:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 8;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0a7d:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 != 0) goto L_0x0a99;
    L_0x0a87:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 7;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0a99:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 == 0) goto L_0x0c3b;
    L_0x0aa3:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 9;
        r4 = r4[r5];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
    L_0x0ab1:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 1;
        r4 = r4[r5];
        if (r4 == 0) goto L_0x0c80;
    L_0x0aba:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 1;
        r5 = r4[r5];
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0adf;
    L_0x0acb:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0adf;
    L_0x0ad5:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0c7c;
    L_0x0adf:
        r4 = 0;
    L_0x0ae0:
        r5.setVisibility(r4);
    L_0x0ae3:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r5 = r4[r5];
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0b08;
    L_0x0af4:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0b08;
    L_0x0afe:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0cb7;
    L_0x0b08:
        r4 = 0;
    L_0x0b09:
        r5.setVisibility(r4);
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.shipping_address_requested;
        if (r4 != 0) goto L_0x0b9a;
    L_0x0b16:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 0;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 1;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 2;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 3;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 4;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 5;
        r4 = r4[r5];
        r4 = r4.getParent();
        r4 = (android.view.ViewGroup) r4;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x0b9a:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0cbb;
    L_0x0ba2:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.phone;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0cbb;
    L_0x0bb0:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.phone;
        r0 = r47;
        r0.fillNumber(r4);
    L_0x0bbd:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 8;
        r4 = r4[r5];
        r4 = r4.length();
        if (r4 != 0) goto L_0x0c36;
    L_0x0bcb:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 == 0) goto L_0x0c36;
    L_0x0bd5:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        if (r4 == 0) goto L_0x0beb;
    L_0x0bdd:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.saved_info;
        r4 = r4.phone;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x0c36;
    L_0x0beb:
        r21 = 0;
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0cc3 }
        r5 = "phone";	 Catch:{ Exception -> 0x0cc3 }
        r41 = r4.getSystemService(r5);	 Catch:{ Exception -> 0x0cc3 }
        r41 = (android.telephony.TelephonyManager) r41;	 Catch:{ Exception -> 0x0cc3 }
        if (r41 == 0) goto L_0x0c02;	 Catch:{ Exception -> 0x0cc3 }
    L_0x0bfa:
        r4 = r41.getSimCountryIso();	 Catch:{ Exception -> 0x0cc3 }
        r21 = r4.toUpperCase();	 Catch:{ Exception -> 0x0cc3 }
    L_0x0c02:
        if (r21 == 0) goto L_0x0c36;
    L_0x0c04:
        r0 = r30;
        r1 = r21;
        r23 = r0.get(r1);
        r23 = (java.lang.String) r23;
        if (r23 == 0) goto L_0x0c36;
    L_0x0c10:
        r0 = r47;
        r4 = r0.countriesArray;
        r0 = r23;
        r27 = r4.indexOf(r0);
        r4 = -1;
        r0 = r27;
        if (r0 == r4) goto L_0x0c36;
    L_0x0c1f:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 8;
        r5 = r4[r5];
        r0 = r47;
        r4 = r0.countriesMap;
        r0 = r23;
        r4 = r4.get(r0);
        r4 = (java.lang.CharSequence) r4;
        r5.setText(r4);
    L_0x0c36:
        r0 = r47;
        r4 = r0.fragmentView;
        return r4;
    L_0x0c3b:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0c54;
    L_0x0c45:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 7;
        r4 = r4[r5];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0ab1;
    L_0x0c54:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 == 0) goto L_0x0c6d;
    L_0x0c5e:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 6;
        r4 = r4[r5];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0ab1;
    L_0x0c6d:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 5;
        r4 = r4[r5];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0ab1;
    L_0x0c7c:
        r4 = 8;
        goto L_0x0ae0;
    L_0x0c80:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        if (r4 == 0) goto L_0x0ae3;
    L_0x0c89:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r5 = r4[r5];
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.name_requested;
        if (r4 != 0) goto L_0x0cae;
    L_0x0c9a:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x0cae;
    L_0x0ca4:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 == 0) goto L_0x0cb4;
    L_0x0cae:
        r4 = 0;
    L_0x0caf:
        r5.setVisibility(r4);
        goto L_0x0ae3;
    L_0x0cb4:
        r4 = 8;
        goto L_0x0caf;
    L_0x0cb7:
        r4 = 8;
        goto L_0x0b09;
    L_0x0cbb:
        r4 = 0;
        r0 = r47;
        r0.fillNumber(r4);
        goto L_0x0bbd;
    L_0x0cc3:
        r25 = move-exception;
        org.telegram.messenger.FileLog.m8e(r25);
        goto L_0x0c02;
    L_0x0cc9:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 2;
        if (r4 != r5) goto L_0x13f0;
    L_0x0cd0:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.native_params;
        if (r4 == 0) goto L_0x0d17;
    L_0x0cd8:
        r29 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0eb9 }
        r0 = r47;	 Catch:{ Exception -> 0x0eb9 }
        r4 = r0.paymentForm;	 Catch:{ Exception -> 0x0eb9 }
        r4 = r4.native_params;	 Catch:{ Exception -> 0x0eb9 }
        r4 = r4.data;	 Catch:{ Exception -> 0x0eb9 }
        r0 = r29;	 Catch:{ Exception -> 0x0eb9 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x0eb9 }
        r4 = "android_pay_public_key";	 Catch:{ Exception -> 0x0eb1 }
        r0 = r29;	 Catch:{ Exception -> 0x0eb1 }
        r14 = r0.getString(r4);	 Catch:{ Exception -> 0x0eb1 }
        r4 = android.text.TextUtils.isEmpty(r14);	 Catch:{ Exception -> 0x0eb1 }
        if (r4 != 0) goto L_0x0cfa;	 Catch:{ Exception -> 0x0eb1 }
    L_0x0cf6:
        r0 = r47;	 Catch:{ Exception -> 0x0eb1 }
        r0.androidPayPublicKey = r14;	 Catch:{ Exception -> 0x0eb1 }
    L_0x0cfa:
        r4 = "android_pay_bgcolor";	 Catch:{ Exception -> 0x0ebf }
        r0 = r29;	 Catch:{ Exception -> 0x0ebf }
        r4 = r0.getInt(r4);	 Catch:{ Exception -> 0x0ebf }
        r5 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;	 Catch:{ Exception -> 0x0ebf }
        r4 = r4 | r5;	 Catch:{ Exception -> 0x0ebf }
        r0 = r47;	 Catch:{ Exception -> 0x0ebf }
        r0.androidPayBackgroundColor = r4;	 Catch:{ Exception -> 0x0ebf }
    L_0x0d0a:
        r4 = "android_pay_inverse";	 Catch:{ Exception -> 0x0ec7 }
        r0 = r29;	 Catch:{ Exception -> 0x0ec7 }
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x0ec7 }
        r0 = r47;	 Catch:{ Exception -> 0x0ec7 }
        r0.androidPayBlackTheme = r4;	 Catch:{ Exception -> 0x0ec7 }
    L_0x0d17:
        r0 = r47;
        r4 = r0.isWebView;
        if (r4 == 0) goto L_0x0ecf;
    L_0x0d1d:
        r0 = r47;
        r4 = r0.androidPayPublicKey;
        if (r4 == 0) goto L_0x0d26;
    L_0x0d23:
        r47.initAndroidPay(r48);
    L_0x0d26:
        r4 = new android.widget.FrameLayout;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.androidPayContainer = r4;
        r0 = r47;
        r4 = r0.androidPayContainer;
        r5 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r4.setId(r5);
        r0 = r47;
        r4 = r0.androidPayContainer;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.androidPayContainer;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.androidPayContainer;
        r6 = -1;
        r7 = 48;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = 1;
        r0 = r47;
        r0.webviewLoading = r4;
        r4 = 1;
        r5 = 1;
        r0 = r47;
        r0.showEditDoneProgress(r4, r5);
        r0 = r47;
        r4 = r0.progressView;
        r5 = 0;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.doneItem;
        r5 = 0;
        r4.setEnabled(r5);
        r0 = r47;
        r4 = r0.doneItem;
        r4 = r4.getImageView();
        r5 = 4;
        r4.setVisibility(r5);
        r4 = new org.telegram.ui.PaymentFormActivity$4;
        r0 = r47;
        r1 = r48;
        r4.<init>(r1);
        r0 = r47;
        r0.webView = r4;
        r0 = r47;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setJavaScriptEnabled(r5);
        r0 = r47;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setDomStorageEnabled(r5);
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x0dce;
    L_0x0db4:
        r0 = r47;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 0;
        r4.setMixedContentMode(r5);
        r19 = android.webkit.CookieManager.getInstance();
        r0 = r47;
        r4 = r0.webView;
        r5 = 1;
        r0 = r19;
        r0.setAcceptThirdPartyCookies(r4, r5);
    L_0x0dce:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 17;
        if (r4 < r5) goto L_0x0de6;
    L_0x0dd4:
        r0 = r47;
        r4 = r0.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$TelegramWebviewProxy;
        r6 = 0;
        r0 = r47;
        r5.<init>();
        r6 = "TelegramWebviewProxy";
        r4.addJavascriptInterface(r5, r6);
    L_0x0de6:
        r0 = r47;
        r4 = r0.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$5;
        r0 = r47;
        r5.<init>();
        r4.setWebViewClient(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.webView;
        r6 = -1;
        r7 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 2;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextCheckCell;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.checkCell1 = r4;
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = "PaymentCardSavePaymentInformation";
        r6 = NUM; // 0x7f0c05b9 float:1.8612163E38 double:1.0530981223E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r0 = r47;
        r6 = r0.saveCardInfo;
        r7 = 0;
        r4.setTextAndCheck(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.checkCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$4;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r47.updateSavePaymentField();
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0c36;
    L_0x0eb1:
        r25 = move-exception;
        r4 = 0;
        r0 = r47;	 Catch:{ Exception -> 0x0eb9 }
        r0.androidPayPublicKey = r4;	 Catch:{ Exception -> 0x0eb9 }
        goto L_0x0cfa;
    L_0x0eb9:
        r25 = move-exception;
        org.telegram.messenger.FileLog.m8e(r25);
        goto L_0x0d17;
    L_0x0ebf:
        r25 = move-exception;
        r4 = -1;
        r0 = r47;	 Catch:{ Exception -> 0x0eb9 }
        r0.androidPayBackgroundColor = r4;	 Catch:{ Exception -> 0x0eb9 }
        goto L_0x0d0a;	 Catch:{ Exception -> 0x0eb9 }
    L_0x0ec7:
        r25 = move-exception;	 Catch:{ Exception -> 0x0eb9 }
        r4 = 0;	 Catch:{ Exception -> 0x0eb9 }
        r0 = r47;	 Catch:{ Exception -> 0x0eb9 }
        r0.androidPayBlackTheme = r4;	 Catch:{ Exception -> 0x0eb9 }
        goto L_0x0d17;
    L_0x0ecf:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.native_params;
        if (r4 == 0) goto L_0x0f1a;
    L_0x0ed7:
        r29 = new org.json.JSONObject;	 Catch:{ Exception -> 0x1153 }
        r0 = r47;	 Catch:{ Exception -> 0x1153 }
        r4 = r0.paymentForm;	 Catch:{ Exception -> 0x1153 }
        r4 = r4.native_params;	 Catch:{ Exception -> 0x1153 }
        r4 = r4.data;	 Catch:{ Exception -> 0x1153 }
        r0 = r29;	 Catch:{ Exception -> 0x1153 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x1153 }
        r4 = "need_country";	 Catch:{ Exception -> 0x114b }
        r0 = r29;	 Catch:{ Exception -> 0x114b }
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x114b }
        r0 = r47;	 Catch:{ Exception -> 0x114b }
        r0.need_card_country = r4;	 Catch:{ Exception -> 0x114b }
    L_0x0ef3:
        r4 = "need_zip";	 Catch:{ Exception -> 0x1159 }
        r0 = r29;	 Catch:{ Exception -> 0x1159 }
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x1159 }
        r0 = r47;	 Catch:{ Exception -> 0x1159 }
        r0.need_card_postcode = r4;	 Catch:{ Exception -> 0x1159 }
    L_0x0f00:
        r4 = "need_cardholder_name";	 Catch:{ Exception -> 0x1161 }
        r0 = r29;	 Catch:{ Exception -> 0x1161 }
        r4 = r0.getBoolean(r4);	 Catch:{ Exception -> 0x1161 }
        r0 = r47;	 Catch:{ Exception -> 0x1161 }
        r0.need_card_name = r4;	 Catch:{ Exception -> 0x1161 }
    L_0x0f0d:
        r4 = "publishable_key";	 Catch:{ Exception -> 0x1169 }
        r0 = r29;	 Catch:{ Exception -> 0x1169 }
        r4 = r0.getString(r4);	 Catch:{ Exception -> 0x1169 }
        r0 = r47;	 Catch:{ Exception -> 0x1169 }
        r0.stripeApiKey = r4;	 Catch:{ Exception -> 0x1169 }
    L_0x0f1a:
        r47.initAndroidPay(r48);
        r4 = 6;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r47;
        r0.inputFields = r4;
        r11 = 0;
    L_0x0f25:
        r4 = 6;
        if (r11 >= r4) goto L_0x13a8;
    L_0x0f28:
        if (r11 != 0) goto L_0x1173;
    L_0x0f2a:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentCardTitle";
        r6 = NUM; // 0x7f0c05bc float:1.861217E38 double:1.0530981237E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x0f71:
        r4 = 3;
        if (r11 == r4) goto L_0x11bf;
    L_0x0f74:
        r4 = 5;
        if (r11 == r4) goto L_0x11bf;
    L_0x0f77:
        r4 = 4;
        if (r11 != r4) goto L_0x0f80;
    L_0x0f7a:
        r0 = r47;
        r4 = r0.need_card_postcode;
        if (r4 == 0) goto L_0x11bf;
    L_0x0f80:
        r13 = 1;
    L_0x0f81:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 48;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        r33 = 0;
        r0 = r47;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r48;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        r4 = 3;
        if (r11 != r4) goto L_0x11c2;
    L_0x1025:
        r4 = 1;
        r0 = new android.text.InputFilter[r4];
        r28 = r0;
        r4 = 0;
        r5 = new android.text.InputFilter$LengthFilter;
        r6 = 3;
        r5.<init>(r6);
        r28[r4] = r5;
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r28;
        r4.setFilters(r0);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        r4.setInputType(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.text.method.PasswordTransformationMethod.getInstance();
        r4.setTransformationMethod(r5);
    L_0x1061:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 268435461; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r5);
        switch(r11) {
            case 0: goto L_0x121c;
            case 1: goto L_0x1246;
            case 2: goto L_0x125b;
            case 3: goto L_0x1231;
            case 4: goto L_0x1285;
            case 5: goto L_0x1270;
            default: goto L_0x1070;
        };
    L_0x1070:
        if (r11 != 0) goto L_0x129a;
    L_0x1072:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$6;
        r0 = r47;
        r5.<init>();
        r4.addTextChangedListener(r5);
    L_0x1082:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x12af;
    L_0x109e:
        r4 = 5;
    L_0x109f:
        r5.setGravity(r4);
        r0 = r47;
        r4 = r0.inputFields;
        r45 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r45;
        r0.addView(r1, r4);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$6;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 3;
        if (r11 != r4) goto L_0x12b2;
    L_0x10d3:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x10f5:
        if (r13 == 0) goto L_0x1125;
    L_0x10f7:
        r24 = new android.view.View;
        r0 = r24;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = "divider";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
    L_0x1125:
        r4 = 4;
        if (r11 != r4) goto L_0x112e;
    L_0x1128:
        r0 = r47;
        r4 = r0.need_card_country;
        if (r4 == 0) goto L_0x1140;
    L_0x112e:
        r4 = 5;
        if (r11 != r4) goto L_0x1137;
    L_0x1131:
        r0 = r47;
        r4 = r0.need_card_postcode;
        if (r4 == 0) goto L_0x1140;
    L_0x1137:
        r4 = 2;
        if (r11 != r4) goto L_0x1147;
    L_0x113a:
        r0 = r47;
        r4 = r0.need_card_name;
        if (r4 != 0) goto L_0x1147;
    L_0x1140:
        r4 = 8;
        r0 = r18;
        r0.setVisibility(r4);
    L_0x1147:
        r11 = r11 + 1;
        goto L_0x0f25;
    L_0x114b:
        r25 = move-exception;
        r4 = 0;
        r0 = r47;	 Catch:{ Exception -> 0x1153 }
        r0.need_card_country = r4;	 Catch:{ Exception -> 0x1153 }
        goto L_0x0ef3;
    L_0x1153:
        r25 = move-exception;
        org.telegram.messenger.FileLog.m8e(r25);
        goto L_0x0f1a;
    L_0x1159:
        r25 = move-exception;
        r4 = 0;
        r0 = r47;	 Catch:{ Exception -> 0x1153 }
        r0.need_card_postcode = r4;	 Catch:{ Exception -> 0x1153 }
        goto L_0x0f00;	 Catch:{ Exception -> 0x1153 }
    L_0x1161:
        r25 = move-exception;	 Catch:{ Exception -> 0x1153 }
        r4 = 0;	 Catch:{ Exception -> 0x1153 }
        r0 = r47;	 Catch:{ Exception -> 0x1153 }
        r0.need_card_name = r4;	 Catch:{ Exception -> 0x1153 }
        goto L_0x0f0d;	 Catch:{ Exception -> 0x1153 }
    L_0x1169:
        r25 = move-exception;	 Catch:{ Exception -> 0x1153 }
        r4 = "";	 Catch:{ Exception -> 0x1153 }
        r0 = r47;	 Catch:{ Exception -> 0x1153 }
        r0.stripeApiKey = r4;	 Catch:{ Exception -> 0x1153 }
        goto L_0x0f1a;
    L_0x1173:
        r4 = 4;
        if (r11 != r4) goto L_0x0f71;
    L_0x1176:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentBillingAddress";
        r6 = NUM; // 0x7f0c05b3 float:1.8612151E38 double:1.0530981193E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0f71;
    L_0x11bf:
        r13 = 0;
        goto L_0x0f81;
    L_0x11c2:
        if (r11 != 0) goto L_0x11d0;
    L_0x11c4:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 2;
        r4.setInputType(r5);
        goto L_0x1061;
    L_0x11d0:
        r4 = 4;
        if (r11 != r4) goto L_0x11ef;
    L_0x11d3:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$5;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnTouchListener(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setInputType(r5);
        goto L_0x1061;
    L_0x11ef:
        r4 = 1;
        if (r11 != r4) goto L_0x11ff;
    L_0x11f2:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 16386; // 0x4002 float:2.2962E-41 double:8.096E-320;
        r4.setInputType(r5);
        goto L_0x1061;
    L_0x11ff:
        r4 = 2;
        if (r11 != r4) goto L_0x120f;
    L_0x1202:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 4097; // 0x1001 float:5.741E-42 double:2.024E-320;
        r4.setInputType(r5);
        goto L_0x1061;
    L_0x120f:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 16385; // 0x4001 float:2.296E-41 double:8.0953E-320;
        r4.setInputType(r5);
        goto L_0x1061;
    L_0x121c:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardNumber";
        r6 = NUM; // 0x7f0c05b8 float:1.8612161E38 double:1.053098122E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1070;
    L_0x1231:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardCvv";
        r6 = NUM; // 0x7f0c05b4 float:1.8612153E38 double:1.05309812E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1070;
    L_0x1246:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardExpireDate";
        r6 = NUM; // 0x7f0c05b5 float:1.8612155E38 double:1.0530981203E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1070;
    L_0x125b:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentCardName";
        r6 = NUM; // 0x7f0c05b7 float:1.861216E38 double:1.0530981213E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1070;
    L_0x1270:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingZipPlaceholder";
        r6 = NUM; // 0x7f0c05e6 float:1.8612255E38 double:1.0530981445E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1070;
    L_0x1285:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentShippingCountry";
        r6 = NUM; // 0x7f0c05dc float:1.8612234E38 double:1.0530981396E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1070;
    L_0x129a:
        r4 = 1;
        if (r11 != r4) goto L_0x1082;
    L_0x129d:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$7;
        r0 = r47;
        r5.<init>();
        r4.addTextChangedListener(r5);
        goto L_0x1082;
    L_0x12af:
        r4 = 3;
        goto L_0x109f;
    L_0x12b2:
        r4 = 5;
        if (r11 != r4) goto L_0x1360;
    L_0x12b5:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 2;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextCheckCell;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.checkCell1 = r4;
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = "PaymentCardSavePaymentInformation";
        r6 = NUM; // 0x7f0c05b9 float:1.8612163E38 double:1.0530981223E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r0 = r47;
        r6 = r0.saveCardInfo;
        r7 = 0;
        r4.setTextAndCheck(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.checkCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.checkCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$7;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r47.updateSavePaymentField();
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x10f5;
    L_0x1360:
        if (r11 != 0) goto L_0x10f5;
    L_0x1362:
        r4 = new android.widget.FrameLayout;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.androidPayContainer = r4;
        r0 = r47;
        r4 = r0.androidPayContainer;
        r5 = 4000; // 0xfa0 float:5.605E-42 double:1.9763E-320;
        r4.setId(r5);
        r0 = r47;
        r4 = r0.androidPayContainer;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.androidPayContainer;
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r0 = r0.androidPayContainer;
        r45 = r0;
        r4 = -2;
        r5 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r6 = 21;
        r7 = 0;
        r8 = 0;
        r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r10 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r45;
        r0.addView(r1, r4);
        goto L_0x10f5;
    L_0x13a8:
        r0 = r47;
        r4 = r0.need_card_country;
        if (r4 != 0) goto L_0x13cc;
    L_0x13ae:
        r0 = r47;
        r4 = r0.need_card_postcode;
        if (r4 != 0) goto L_0x13cc;
    L_0x13b4:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 8;
        r4.setVisibility(r5);
    L_0x13cc:
        r0 = r47;
        r4 = r0.need_card_postcode;
        if (r4 == 0) goto L_0x13e1;
    L_0x13d2:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 5;
        r4 = r4[r5];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0c36;
    L_0x13e1:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = 3;
        r4 = r4[r5];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x0c36;
    L_0x13f0:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 1;
        if (r4 != r5) goto L_0x14d2;
    L_0x13f7:
        r0 = r47;
        r4 = r0.requestedInfo;
        r4 = r4.shipping_options;
        r20 = r4.size();
        r0 = r20;
        r4 = new org.telegram.ui.Cells.RadioCell[r0];
        r0 = r47;
        r0.radioCells = r4;
        r11 = 0;
    L_0x140a:
        r0 = r20;
        if (r11 >= r0) goto L_0x1498;
    L_0x140e:
        r0 = r47;
        r4 = r0.requestedInfo;
        r4 = r4.shipping_options;
        r40 = r4.get(r11);
        r40 = (org.telegram.tgnet.TLRPC.TL_shippingOption) r40;
        r0 = r47;
        r4 = r0.radioCells;
        r5 = new org.telegram.ui.Cells.RadioCell;
        r0 = r48;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r47;
        r4 = r0.radioCells;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r47;
        r4 = r0.radioCells;
        r4 = r4[r11];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.radioCells;
        r6 = r4[r11];
        r4 = "%s - %s";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r7 = 0;
        r0 = r40;
        r8 = r0.prices;
        r0 = r47;
        r8 = r0.getTotalPriceString(r8);
        r5[r7] = r8;
        r7 = 1;
        r0 = r40;
        r8 = r0.title;
        r5[r7] = r8;
        r7 = java.lang.String.format(r4, r5);
        if (r11 != 0) goto L_0x1493;
    L_0x1468:
        r4 = 1;
        r5 = r4;
    L_0x146a:
        r4 = r20 + -1;
        if (r11 == r4) goto L_0x1496;
    L_0x146e:
        r4 = 1;
    L_0x146f:
        r6.setText(r7, r5, r4);
        r0 = r47;
        r4 = r0.radioCells;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$8;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.radioCells;
        r5 = r5[r11];
        r4.addView(r5);
        r11 = r11 + 1;
        goto L_0x140a;
    L_0x1493:
        r4 = 0;
        r5 = r4;
        goto L_0x146a;
    L_0x1496:
        r4 = 0;
        goto L_0x146f;
    L_0x1498:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0c36;
    L_0x14d2:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 3;
        if (r4 != r5) goto L_0x17ce;
    L_0x14d9:
        r4 = 2;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r47;
        r0.inputFields = r4;
        r11 = 0;
    L_0x14e1:
        r4 = 2;
        if (r11 >= r4) goto L_0x0c36;
    L_0x14e4:
        if (r11 != 0) goto L_0x152d;
    L_0x14e6:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentCardTitle";
        r6 = NUM; // 0x7f0c05bc float:1.861217E38 double:1.0530981237E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x152d:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 48;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        r4 = 1;
        if (r11 == r4) goto L_0x1765;
    L_0x1555:
        r13 = 1;
    L_0x1556:
        if (r13 == 0) goto L_0x1566;
    L_0x1558:
        r4 = 7;
        if (r11 != r4) goto L_0x1768;
    L_0x155b:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x1768;
    L_0x1565:
        r13 = 0;
    L_0x1566:
        if (r13 == 0) goto L_0x1596;
    L_0x1568:
        r24 = new android.view.View;
        r0 = r24;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = "divider";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
    L_0x1596:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r48;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        if (r11 != 0) goto L_0x1782;
    L_0x1612:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = org.telegram.ui.PaymentFormActivity$$Lambda$9.$instance;
        r4.setOnTouchListener(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setInputType(r5);
    L_0x1627:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        switch(r11) {
            case 0: goto L_0x179a;
            case 1: goto L_0x17ad;
            default: goto L_0x1636;
        };
    L_0x1636:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x17cb;
    L_0x1652:
        r4 = 5;
    L_0x1653:
        r5.setGravity(r4);
        r0 = r47;
        r4 = r0.inputFields;
        r45 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r45;
        r0.addView(r1, r4);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$10;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 1;
        if (r11 != r4) goto L_0x1761;
    L_0x1687:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentConfirmationMessage";
        r6 = NUM; // 0x7f0c05c5 float:1.8612188E38 double:1.053098128E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r0 = r47;
        r9 = r0.paymentForm;
        r9 = r9.saved_credentials;
        r9 = r9.title;
        r7[r8] = r9;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7f07007f float:1.7944836E38 double:1.052935566E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextSettingsCell;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.settingsCell1 = r4;
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = "PaymentConfirmationNewCard";
        r6 = NUM; // 0x7f0c05c6 float:1.861219E38 double:1.0530981287E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 0;
        r4.setText(r5, r6);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.settingsCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$11;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x1761:
        r11 = r11 + 1;
        goto L_0x14e1;
    L_0x1765:
        r13 = 0;
        goto L_0x1556;
    L_0x1768:
        r4 = 6;
        if (r11 != r4) goto L_0x1566;
    L_0x176b:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.phone_requested;
        if (r4 != 0) goto L_0x1566;
    L_0x1775:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.email_requested;
        if (r4 != 0) goto L_0x1566;
    L_0x177f:
        r13 = 0;
        goto L_0x1566;
    L_0x1782:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r4.setInputType(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r5);
        goto L_0x1627;
    L_0x179a:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.saved_credentials;
        r5 = r5.title;
        r4.setText(r5);
        goto L_0x1636;
    L_0x17ad:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "LoginPassword";
        r6 = NUM; // 0x7f0c03c1 float:1.8611141E38 double:1.0530978733E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r4.requestFocus();
        goto L_0x1636;
    L_0x17cb:
        r4 = 3;
        goto L_0x1653;
    L_0x17ce:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 == r5) goto L_0x17dc;
    L_0x17d5:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 5;
        if (r4 != r5) goto L_0x1d9f;
    L_0x17dc:
        r4 = new org.telegram.ui.Cells.PaymentInfoCell;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.paymentInfoCell = r4;
        r0 = r47;
        r4 = r0.paymentInfoCell;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r5 = r0.paymentInfoCell;
        r0 = r47;
        r4 = r0.messageObject;
        r4 = r4.messageOwner;
        r4 = r4.media;
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r4;
        r0 = r47;
        r6 = r0.currentBotName;
        r5.setInvoice(r4, r6);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.paymentInfoCell;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r16 = new java.util.ArrayList;
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.invoice;
        r4 = r4.prices;
        r0 = r16;
        r0.<init>(r4);
        r0 = r47;
        r4 = r0.shippingOption;
        if (r4 == 0) goto L_0x185d;
    L_0x1852:
        r0 = r47;
        r4 = r0.shippingOption;
        r4 = r4.prices;
        r0 = r16;
        r0.addAll(r4);
    L_0x185d:
        r0 = r47;
        r1 = r16;
        r42 = r0.getTotalPriceString(r1);
        r11 = 0;
    L_0x1866:
        r4 = r16.size();
        if (r11 >= r4) goto L_0x18b3;
    L_0x186c:
        r0 = r16;
        r34 = r0.get(r11);
        r34 = (org.telegram.tgnet.TLRPC.TL_labeledPrice) r34;
        r35 = new org.telegram.ui.Cells.TextPriceCell;
        r0 = r35;
        r1 = r48;
        r0.<init>(r1);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r35;
        r0.setBackgroundColor(r4);
        r0 = r34;
        r4 = r0.label;
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r0 = r34;
        r6 = r0.amount;
        r0 = r47;
        r8 = r0.paymentForm;
        r8 = r8.invoice;
        r8 = r8.currency;
        r5 = r5.formatCurrencyString(r6, r8);
        r6 = 0;
        r0 = r35;
        r0.setTextAndValue(r4, r5, r6);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r35;
        r4.addView(r0);
        r11 = r11 + 1;
        goto L_0x1866;
    L_0x18b3:
        r35 = new org.telegram.ui.Cells.TextPriceCell;
        r0 = r35;
        r1 = r48;
        r0.<init>(r1);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r35;
        r0.setBackgroundColor(r4);
        r4 = "PaymentTransactionTotal";
        r5 = NUM; // 0x7f0c05ec float:1.8612267E38 double:1.0530981475E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r5 = 1;
        r0 = r35;
        r1 = r42;
        r0.setTextAndValue(r4, r1, r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r35;
        r4.addView(r0);
        r24 = new android.view.View;
        r0 = r24;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = "divider";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = new android.widget.FrameLayout$LayoutParams;
        r6 = -1;
        r7 = 1;
        r8 = 83;
        r5.<init>(r6, r7, r8);
        r0 = r24;
        r4.addView(r0, r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r4 = r4[r5];
        r0 = r47;
        r5 = r0.cardName;
        r6 = "PaymentCheckoutMethod";
        r7 = NUM; // 0x7f0c05bf float:1.8612175E38 double:1.053098125E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 0;
        r5 = r5[r6];
        r4.addView(r5);
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 != r5) goto L_0x196f;
    L_0x195e:
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$12;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
    L_0x196f:
        r37 = 0;
        r11 = 0;
    L_0x1972:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r4 = r4.size();
        if (r11 >= r4) goto L_0x199b;
    L_0x197e:
        r0 = r47;
        r4 = r0.paymentForm;
        r4 = r4.users;
        r43 = r4.get(r11);
        r43 = (org.telegram.tgnet.TLRPC.User) r43;
        r0 = r43;
        r4 = r0.id;
        r0 = r47;
        r5 = r0.paymentForm;
        r5 = r5.provider_id;
        if (r4 != r5) goto L_0x1998;
    L_0x1996:
        r37 = r43;
    L_0x1998:
        r11 = r11 + 1;
        goto L_0x1972;
    L_0x199b:
        if (r37 == 0) goto L_0x1d9a;
    L_0x199d:
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 1;
        r4 = r4[r5];
        r0 = r37;
        r5 = r0.first_name;
        r0 = r37;
        r6 = r0.last_name;
        r36 = org.telegram.messenger.ContactsController.formatName(r5, r6);
        r5 = "PaymentCheckoutProvider";
        r6 = NUM; // 0x7f0c05c3 float:1.8612184E38 double:1.053098127E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 1;
        r0 = r36;
        r4.setTextAndValue(r0, r5, r6);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 1;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x19eb:
        r0 = r47;
        r4 = r0.validateRequest;
        if (r4 == 0) goto L_0x1be7;
    L_0x19f1:
        r0 = r47;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.shipping_address;
        if (r4 == 0) goto L_0x1a95;
    L_0x19fb:
        r4 = "%s %s, %s, %s, %s, %s";
        r5 = 6;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r0 = r47;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.street_line1;
        r5[r6] = r7;
        r6 = 1;
        r0 = r47;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.street_line2;
        r5[r6] = r7;
        r6 = 2;
        r0 = r47;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.city;
        r5[r6] = r7;
        r6 = 3;
        r0 = r47;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.state;
        r5[r6] = r7;
        r6 = 4;
        r0 = r47;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.country_iso2;
        r5[r6] = r7;
        r6 = 5;
        r0 = r47;
        r7 = r0.validateRequest;
        r7 = r7.info;
        r7 = r7.shipping_address;
        r7 = r7.post_code;
        r5[r6] = r7;
        r12 = java.lang.String.format(r4, r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 2;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 2;
        r4 = r4[r5];
        r5 = "PaymentShippingAddress";
        r6 = NUM; // 0x7f0c05d8 float:1.8612226E38 double:1.0530981376E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 1;
        r4.setTextAndValue(r12, r5, r6);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 2;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1a95:
        r0 = r47;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.name;
        if (r4 == 0) goto L_0x1ae9;
    L_0x1a9f:
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 3;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 3;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 3;
        r4 = r4[r5];
        r0 = r47;
        r5 = r0.validateRequest;
        r5 = r5.info;
        r5 = r5.name;
        r6 = "PaymentCheckoutName";
        r7 = NUM; // 0x7f0c05c0 float:1.8612178E38 double:1.0530981257E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 3;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1ae9:
        r0 = r47;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.phone;
        if (r4 == 0) goto L_0x1b45;
    L_0x1af3:
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 4;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 4;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 4;
        r4 = r4[r5];
        r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r0 = r47;
        r6 = r0.validateRequest;
        r6 = r6.info;
        r6 = r6.phone;
        r5 = r5.format(r6);
        r6 = "PaymentCheckoutPhoneNumber";
        r7 = NUM; // 0x7f0c05c2 float:1.8612182E38 double:1.0530981267E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 4;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1b45:
        r0 = r47;
        r4 = r0.validateRequest;
        r4 = r4.info;
        r4 = r4.email;
        if (r4 == 0) goto L_0x1b99;
    L_0x1b4f:
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 5;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 5;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 5;
        r4 = r4[r5];
        r0 = r47;
        r5 = r0.validateRequest;
        r5 = r5.info;
        r5 = r5.email;
        r6 = "PaymentCheckoutEmail";
        r7 = NUM; // 0x7f0c05be float:1.8612173E38 double:1.0530981247E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 1;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 5;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1b99:
        r0 = r47;
        r4 = r0.shippingOption;
        if (r4 == 0) goto L_0x1be7;
    L_0x1b9f:
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 6;
        r6 = new org.telegram.ui.Cells.TextDetailSettingsCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 6;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.detailSettingsCell;
        r5 = 6;
        r4 = r4[r5];
        r0 = r47;
        r5 = r0.shippingOption;
        r5 = r5.title;
        r6 = "PaymentCheckoutShippingMethod";
        r7 = NUM; // 0x7f0c05c4 float:1.8612186E38 double:1.0530981277E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r7 = 0;
        r4.setTextAndValue(r5, r6, r7);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.detailSettingsCell;
        r6 = 6;
        r5 = r5[r6];
        r4.addView(r5);
    L_0x1be7:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 4;
        if (r4 != r5) goto L_0x1d60;
    L_0x1bee:
        r4 = new android.widget.FrameLayout;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.bottomLayout = r4;
        r0 = r47;
        r4 = r0.bottomLayout;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.bottomLayout;
        r5 = -1;
        r6 = 48;
        r7 = 80;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7);
        r0 = r26;
        r0.addView(r4, r5);
        r0 = r47;
        r4 = r0.bottomLayout;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$13;
        r0 = r47;
        r1 = r36;
        r2 = r42;
        r5.<init>(r0, r1, r2);
        r4.setOnClickListener(r5);
        r4 = new android.widget.TextView;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.payTextView = r4;
        r0 = r47;
        r4 = r0.payTextView;
        r5 = "windowBackgroundWhiteBlueText6";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.payTextView;
        r5 = "PaymentCheckoutPay";
        r6 = NUM; // 0x7f0c05c1 float:1.861218E38 double:1.053098126E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r7[r8] = r42;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.payTextView;
        r5 = 1;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4.setTextSize(r5, r6);
        r0 = r47;
        r4 = r0.payTextView;
        r5 = 17;
        r4.setGravity(r5);
        r0 = r47;
        r4 = r0.payTextView;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r4.setTypeface(r5);
        r0 = r47;
        r4 = r0.bottomLayout;
        r0 = r47;
        r5 = r0.payTextView;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Components.ContextProgressView;
        r5 = 0;
        r0 = r48;
        r4.<init>(r0, r5);
        r0 = r47;
        r0.progressViewButton = r4;
        r0 = r47;
        r4 = r0.progressViewButton;
        r5 = 4;
        r4.setVisibility(r5);
        r0 = r47;
        r4 = r0.bottomLayout;
        r0 = r47;
        r5 = r0.progressViewButton;
        r6 = -1;
        r7 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7);
        r4.addView(r5, r6);
        r39 = new android.view.View;
        r0 = r39;
        r1 = r48;
        r0.<init>(r1);
        r4 = NUM; // 0x7f07008b float:1.794486E38 double:1.0529355717E-314;
        r0 = r39;
        r0.setBackgroundResource(r4);
        r4 = -1;
        r5 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = 83;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r26;
        r1 = r39;
        r0.addView(r1, r4);
        r0 = r47;
        r4 = r0.doneItem;
        r5 = 0;
        r4.setEnabled(r5);
        r0 = r47;
        r4 = r0.doneItem;
        r4 = r4.getImageView();
        r5 = 4;
        r4.setVisibility(r5);
        r4 = new org.telegram.ui.PaymentFormActivity$9;
        r0 = r47;
        r1 = r48;
        r4.<init>(r1);
        r0 = r47;
        r0.webView = r4;
        r0 = r47;
        r4 = r0.webView;
        r5 = -1;
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setJavaScriptEnabled(r5);
        r0 = r47;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 1;
        r4.setDomStorageEnabled(r5);
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x1d39;
    L_0x1d1f:
        r0 = r47;
        r4 = r0.webView;
        r4 = r4.getSettings();
        r5 = 0;
        r4.setMixedContentMode(r5);
        r19 = android.webkit.CookieManager.getInstance();
        r0 = r47;
        r4 = r0.webView;
        r5 = 1;
        r0 = r19;
        r0.setAcceptThirdPartyCookies(r4, r5);
    L_0x1d39:
        r0 = r47;
        r4 = r0.webView;
        r5 = new org.telegram.ui.PaymentFormActivity$10;
        r0 = r47;
        r5.<init>();
        r4.setWebViewClient(r5);
        r0 = r47;
        r4 = r0.webView;
        r5 = -1;
        r6 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6);
        r0 = r26;
        r0.addView(r4, r5);
        r0 = r47;
        r4 = r0.webView;
        r5 = 8;
        r4.setVisibility(r5);
    L_0x1d60:
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.sectionCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.sectionCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x0c36;
    L_0x1d9a:
        r36 = "";
        goto L_0x19eb;
    L_0x1d9f:
        r0 = r47;
        r4 = r0.currentStep;
        r5 = 6;
        if (r4 != r5) goto L_0x0c36;
    L_0x1da6:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 2;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 2;
        r4 = r4[r5];
        r5 = NUM; // 0x7f07007f float:1.7944836E38 double:1.052935566E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 2;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r4 = new org.telegram.ui.Cells.TextSettingsCell;
        r0 = r48;
        r4.<init>(r0);
        r0 = r47;
        r0.settingsCell1 = r4;
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = 1;
        r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = "windowBackgroundWhiteRedText3";
        r4.setTag(r5);
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = "windowBackgroundWhiteRedText3";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = "AbortPassword";
        r6 = NUM; // 0x7f0c0001 float:1.8609194E38 double:1.053097399E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r6 = 0;
        r4.setText(r5, r6);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.settingsCell1;
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        r0 = r47;
        r4 = r0.settingsCell1;
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$14;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r4 = 3;
        r4 = new org.telegram.ui.Components.EditTextBoldCursor[r4];
        r0 = r47;
        r0.inputFields = r4;
        r11 = 0;
    L_0x1e46:
        r4 = 3;
        if (r11 >= r4) goto L_0x2122;
    L_0x1e49:
        if (r11 != 0) goto L_0x202c;
    L_0x1e4b:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentPasswordTitle";
        r6 = NUM; // 0x7f0c05d3 float:1.8612216E38 double:1.053098135E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x1e92:
        r18 = new android.widget.FrameLayout;
        r0 = r18;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.linearLayout2;
        r5 = -1;
        r6 = 48;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6);
        r0 = r18;
        r4.addView(r0, r5);
        r4 = "windowBackgroundWhite";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r18;
        r0.setBackgroundColor(r4);
        if (r11 != 0) goto L_0x1ee7;
    L_0x1eb9:
        r24 = new android.view.View;
        r0 = r24;
        r1 = r48;
        r0.<init>(r1);
        r0 = r47;
        r4 = r0.dividers;
        r0 = r24;
        r4.add(r0);
        r4 = "divider";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0 = r24;
        r0.setBackgroundColor(r4);
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = 1;
        r7 = 83;
        r4.<init>(r5, r6, r7);
        r0 = r18;
        r1 = r24;
        r0.addView(r1, r4);
    L_0x1ee7:
        r0 = r47;
        r4 = r0.inputFields;
        r5 = new org.telegram.ui.Components.EditTextBoldCursor;
        r0 = r48;
        r5.<init>(r0);
        r4[r11] = r5;
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = java.lang.Integer.valueOf(r11);
        r4.setTag(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 1;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r4.setTextSize(r5, r6);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteHintText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHintTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "windowBackgroundWhiteBlackText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setCursorColor(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCursorSize(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = NUM; // 0x3fc00000 float:1.5 double:5.28426686E-315;
        r4.setCursorWidth(r5);
        if (r11 == 0) goto L_0x1f66;
    L_0x1f63:
        r4 = 1;
        if (r11 != r4) goto L_0x2078;
    L_0x1f66:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        r4.setInputType(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = android.graphics.Typeface.DEFAULT;
        r4.setTypeface(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 268435461; // 0x10000005 float:2.5243564E-29 double:1.326247394E-315;
        r4.setImeOptions(r5);
    L_0x1f88:
        switch(r11) {
            case 0: goto L_0x2086;
            case 1: goto L_0x20a4;
            case 2: goto L_0x20b9;
            default: goto L_0x1f8b;
        };
    L_0x1f8b:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setPadding(r5, r6, r7, r8);
        r0 = r47;
        r4 = r0.inputFields;
        r5 = r4[r11];
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x20ce;
    L_0x1fa7:
        r4 = 5;
    L_0x1fa8:
        r5.setGravity(r4);
        r0 = r47;
        r4 = r0.inputFields;
        r45 = r4[r11];
        r4 = -1;
        r5 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r6 = 51;
        r7 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r9 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r18;
        r1 = r45;
        r0.addView(r1, r4);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = new org.telegram.ui.PaymentFormActivity$$Lambda$15;
        r0 = r47;
        r5.<init>(r0);
        r4.setOnEditorActionListener(r5);
        r4 = 1;
        if (r11 != r4) goto L_0x20d1;
    L_0x1fdc:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = "PaymentPasswordInfo";
        r6 = NUM; // 0x7f0c05d1 float:1.8612212E38 double:1.053098134E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 0;
        r4 = r4[r5];
        r5 = NUM; // 0x7f07007f float:1.7944836E38 double:1.052935566E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 0;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
    L_0x2028:
        r11 = r11 + 1;
        goto L_0x1e46;
    L_0x202c:
        r4 = 2;
        if (r11 != r4) goto L_0x1e92;
    L_0x202f:
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r47;
        r4 = r0.headerCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPasswordEmailTitle";
        r6 = NUM; // 0x7f0c05cf float:1.8612208E38 double:1.053098133E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.headerCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x1e92;
    L_0x2078:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = 268435462; // 0x10000006 float:2.5243567E-29 double:1.3262474E-315;
        r4.setImeOptions(r5);
        goto L_0x1f88;
    L_0x2086:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentPasswordEnter";
        r6 = NUM; // 0x7f0c05d0 float:1.861221E38 double:1.0530981336E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r4.requestFocus();
        goto L_0x1f8b;
    L_0x20a4:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentPasswordReEnter";
        r6 = NUM; // 0x7f0c05d2 float:1.8612214E38 double:1.0530981346E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1f8b;
    L_0x20b9:
        r0 = r47;
        r4 = r0.inputFields;
        r4 = r4[r11];
        r5 = "PaymentPasswordEmail";
        r6 = NUM; // 0x7f0c05cd float:1.8612204E38 double:1.053098132E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setHint(r5);
        goto L_0x1f8b;
    L_0x20ce:
        r4 = 3;
        goto L_0x1fa8;
    L_0x20d1:
        r4 = 2;
        if (r11 != r4) goto L_0x2028;
    L_0x20d4:
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r6 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r48;
        r6.<init>(r0);
        r4[r5] = r6;
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = "PaymentPasswordEmailInfo";
        r6 = NUM; // 0x7f0c05ce float:1.8612206E38 double:1.0530981326E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r6);
        r4.setText(r5);
        r0 = r47;
        r4 = r0.bottomCell;
        r5 = 1;
        r4 = r4[r5];
        r5 = NUM; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
        r6 = "windowBackgroundGrayShadow";
        r0 = r48;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r6);
        r4.setBackgroundDrawable(r5);
        r0 = r47;
        r4 = r0.linearLayout2;
        r0 = r47;
        r5 = r0.bottomCell;
        r6 = 1;
        r5 = r5[r6];
        r6 = -1;
        r7 = -2;
        r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7);
        r4.addView(r5, r6);
        goto L_0x2028;
    L_0x2122:
        r47.updatePasswordFields();
        goto L_0x0c36;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PaymentFormActivity.createView(android.content.Context):android.view.View");
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
        activity.setDelegate(new C15428());
        presentFragment(activity);
    }

    final /* synthetic */ void lambda$createView$15$PaymentFormActivity(String providerName, String totalPrice, View v) {
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
        builder.setTitle(LocaleController.getString("PaymentWarning", C0431R.string.PaymentWarning));
        builder.setMessage(LocaleController.formatString("PaymentWarningText", C0431R.string.PaymentWarningText, this.currentBotName, providerName));
        builder.setPositiveButton(LocaleController.getString("OK", C0431R.string.OK), new PaymentFormActivity$$Lambda$40(this, totalPrice));
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$14$PaymentFormActivity(String totalPrice, DialogInterface dialogInterface, int i) {
        showPayAlert(totalPrice);
    }

    final /* synthetic */ void lambda$createView$17$PaymentFormActivity(View v) {
        Builder builder = new Builder(getParentActivity());
        String text = LocaleController.getString("TurnPasswordOffQuestion", C0431R.string.TurnPasswordOffQuestion);
        if (this.currentPassword.has_secure_values) {
            text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", C0431R.string.TurnPasswordOffPassport);
        }
        builder.setMessage(text);
        builder.setTitle(LocaleController.getString("AppName", C0431R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0431R.string.OK), new PaymentFormActivity$$Lambda$39(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0431R.string.Cancel), null);
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
                textInfoPrivacyCell.setText(LocaleController.formatString(str, C0431R.string.EmailPasswordConfirmText, objArr));
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
            this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", C0431R.string.PaymentPasswordEmailInfo));
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
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", C0431R.string.UpdateAppAlert), true);
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
        builder.setPositiveButton(LocaleController.getString("OK", C0431R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void showPayAlert(String totalPrice) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("PaymentTransactionReview", C0431R.string.PaymentTransactionReview));
        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", C0431R.string.PaymentTransactionMessage, totalPrice, this.currentBotName, this.currentItemName));
        builder.setPositiveButton(LocaleController.getString("OK", C0431R.string.OK), new PaymentFormActivity$$Lambda$17(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0431R.string.Cancel), null);
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
                FileLog.m8e(e);
            }
        }
        try {
            if ((this.currentStep == 2 || this.currentStep == 6) && VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
            }
        } catch (Throwable e2) {
            FileLog.m8e(e2);
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
                SpannableStringBuilder text = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", C0431R.string.PaymentCardSavePaymentInformationInfoLine1));
                if (this.paymentForm.password_missing) {
                    loadPasswordInfo();
                    text.append("\n");
                    int len = text.length();
                    String str2 = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", C0431R.string.PaymentCardSavePaymentInformationInfoLine2);
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
                this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), C0431R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                return;
            }
            this.checkCell1.setVisibility(8);
            this.bottomCell[0].setVisibility(8);
            this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), C0431R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
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
        } catch (Throwable e) {
            FileLog.m8e(e);
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
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", C0431R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e) {
                    FileLog.m8e(e);
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
        if (this.currentPassword.new_algo instanceof C0579xb6caa888) {
            C0579xb6caa888 algo = this.currentPassword.new_algo;
            req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo);
            if (req.new_settings.new_password_hash == null) {
                TL_error error = new TL_error();
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
                if (error.text.equals("EMAIL_UNCONFIRMED")) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", C0431R.string.OK), new PaymentFormActivity$$Lambda$35(this, email));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", C0431R.string.YourEmailAlmostThereText));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", C0431R.string.YourEmailAlmostThere));
                    Dialog dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else if (error.text.equals("EMAIL_INVALID")) {
                    showAlertWithText(LocaleController.getString("AppName", C0431R.string.AppName), LocaleController.getString("PasswordEmailInvalid", C0431R.string.PasswordEmailInvalid));
                    return;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", C0431R.string.AppName), LocaleController.formatString("FloodWaitTime", C0431R.string.FloodWaitTime, timeString));
                    return;
                } else {
                    showAlertWithText(LocaleController.getString("AppName", C0431R.string.AppName), error.text);
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
                new Stripe(this.stripeApiKey).createToken(card, new TokenCallback() {
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
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", C0431R.string.PaymentConnectionFailed));
                            } else {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
                            }
                        }
                    }
                });
            } catch (Throwable e) {
                FileLog.m8e(e);
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
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", C0431R.string.UpdateAppAlert), true);
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
        if (currentPassword.current_algo instanceof C0579xb6caa888) {
            x_bytes = SRPHelper.getX(passwordBytes, currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        TL_account_getTmpPassword req1 = new TL_account_getTmpPassword();
        req1.period = 1800;
        RequestDelegate requestDelegate = new PaymentFormActivity$$Lambda$24(this, req1);
        if (currentPassword.current_algo instanceof C0579xb6caa888) {
            req1.password = SRPHelper.startCheck(x_bytes, currentPassword.srp_id, currentPassword.srp_B, (C0579xb6caa888) currentPassword.current_algo);
            if (req1.password == null) {
                TL_error error2 = new TL_error();
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
