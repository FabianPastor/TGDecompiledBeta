package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Build.VERSION;
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
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_noPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.TL_dataJSON;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPaymentCredentials;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

public class PaymentFormActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int FIELDS_COUNT_ADDRESS = 10;
    private static final int FIELDS_COUNT_CARD = 7;
    private static final int FIELDS_COUNT_SAVEDCARD = 2;
    private static final int FIELD_CARD = 0;
    private static final int FIELD_CARDNAME = 3;
    private static final int FIELD_CARD_COUNTRY = 5;
    private static final int FIELD_CARD_POSTCODE = 6;
    private static final int FIELD_CITY = 2;
    private static final int FIELD_COUNTRY = 4;
    private static final int FIELD_CVV = 4;
    private static final int FIELD_EMAIL = 7;
    private static final int FIELD_EXPIRE_MONTH = 1;
    private static final int FIELD_EXPIRE_YEAR = 2;
    private static final int FIELD_NAME = 6;
    private static final int FIELD_PHONE = 9;
    private static final int FIELD_PHONECODE = 8;
    private static final int FIELD_POSTCODE = 5;
    private static final int FIELD_SAVEDCARD = 0;
    private static final int FIELD_SAVEDPASSWORD = 1;
    private static final int FIELD_STATE = 3;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int done_button = 1;
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
    private int currentStep;
    private PaymentFormActivityDelegate delegate;
    private TextDetailSettingsCell[] detailSettingsCell;
    private ArrayList<View> dividers;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private HeaderCell[] headerCell;
    private boolean ignoreOnCardChange;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private EditText[] inputFields;
    private LinearLayout linearLayout2;
    private MessageObject messageObject;
    private boolean need_card_country;
    private boolean need_card_name;
    private boolean need_card_postcode;
    private boolean passwordOk;
    private TextView payTextView;
    private TL_payments_paymentForm paymentForm;
    private PaymentInfoCell paymentInfoCell;
    private String paymentJson;
    private HashMap<String, String> phoneFormatMap;
    private ContextProgressView progressView;
    private RadioCell[] radioCells;
    private TL_payments_validatedRequestedInfo requestedInfo;
    private boolean saveCardInfo;
    private boolean saveShippingInfo;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell settingsCell1;
    private TL_shippingOption shippingOption;
    private String stripeApiKey;
    private TextView textView;
    private TL_payments_validateRequestedInfo validateRequest;
    private WebView webView;
    private boolean webviewLoading;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() != 1 && event.getAction() != 3) {
                    return result;
                }
                Selection.removeSelection(buffer);
                return result;
            } catch (Throwable e) {
                FileLog.e(e);
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
        void didSelectNewCard(String str, String str2, boolean z);
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
                            FileLog.e(e);
                        }
                        PaymentFormActivity.this.goToNextStep();
                    }
                }
            });
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
        this.bottomCell = new TextInfoPrivacyCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[6];
        this.currentStep = 5;
        this.paymentForm = new TL_payments_paymentForm();
        this.paymentForm.bot_id = receipt.bot_id;
        this.paymentForm.invoice = receipt.invoice;
        this.paymentForm.provider_id = receipt.provider_id;
        this.paymentForm.users = receipt.users;
        this.shippingOption = receipt.shipping;
        this.messageObject = message;
        User user = MessagesController.getInstance().getUser(Integer.valueOf(receipt.bot_id));
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
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
        this.bottomCell = new TextInfoPrivacyCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[6];
        if (form.invoice.shipping_address_requested || form.invoice.email_requested || form.invoice.name_requested || form.invoice.phone_requested) {
            step = 0;
        } else if (form.saved_credentials != null) {
            if (UserConfig.tmpPassword != null && UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60) {
                UserConfig.tmpPassword = null;
                UserConfig.saveConfig(false);
            }
            if (UserConfig.tmpPassword != null) {
                step = 4;
            } else {
                step = 3;
            }
        } else {
            step = 2;
        }
        init(form, message, step, null, null, null, null, null, false);
    }

    private PaymentFormActivity(TL_payments_paymentForm form, MessageObject message, int step, TL_payments_validatedRequestedInfo validatedRequestedInfo, TL_shippingOption shipping, String tokenJson, String card, TL_payments_validateRequestedInfo request, boolean saveCard) {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.headerCell = new HeaderCell[3];
        this.dividers = new ArrayList();
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCell = new TextInfoPrivacyCell[2];
        this.detailSettingsCell = new TextDetailSettingsCell[6];
        init(form, message, step, validatedRequestedInfo, shipping, tokenJson, card, request, saveCard);
    }

    private void setDelegate(PaymentFormActivityDelegate paymentFormActivityDelegate) {
        this.delegate = paymentFormActivityDelegate;
    }

    private void init(TL_payments_paymentForm form, MessageObject message, int step, TL_payments_validatedRequestedInfo validatedRequestedInfo, TL_shippingOption shipping, String tokenJson, String card, TL_payments_validateRequestedInfo request, boolean saveCard) {
        boolean z = true;
        this.currentStep = step;
        this.paymentJson = tokenJson;
        this.requestedInfo = validatedRequestedInfo;
        this.paymentForm = form;
        this.shippingOption = shipping;
        this.messageObject = message;
        this.saveCardInfo = saveCard;
        User user = MessagesController.getInstance().getUser(Integer.valueOf(form.bot_id));
        if (user != null) {
            this.currentBotName = user.first_name;
        } else {
            this.currentBotName = "";
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
                if (this.currentStep == 2) {
                    getParentActivity().getWindow().setFlags(8192, 8192);
                } else if (UserConfig.passcodeHash.length() == 0 || UserConfig.allowScreenCapture) {
                    getParentActivity().getWindow().clearFlags(8192);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void onPause() {
    }

    /* JADX WARNING: inconsistent code. */
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
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!PaymentFormActivity.this.donePressed) {
                        PaymentFormActivity.this.finishFragment();
                    }
                } else if (id == 1 && !PaymentFormActivity.this.donePressed) {
                    AndroidUtilities.hideKeyboard(PaymentFormActivity.this.getParentActivity().getCurrentFocus());
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
                    }
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.currentStep == 0 || this.currentStep == 1 || this.currentStep == 2 || this.currentStep == 3) {
            this.doneItem = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            this.progressView = new ContextProgressView(context, 1);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setVisibility(4);
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentStep == 4 ? 48.0f : 0.0f));
        this.linearLayout2 = new LinearLayout(context);
        this.linearLayout2.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        int a;
        ViewGroup linearLayout;
        ViewGroup container;
        boolean allowDivider;
        View view;
        EditText editText;
        if (this.currentStep == 0) {
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
                FileLog.e(e);
            }
            Collections.sort(this.countriesArray, new Comparator<String>() {
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            this.inputFields = new EditText[10];
            a = 0;
            while (a < 10) {
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
                    this.inputFields[a] = new EditText(context);
                }
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setBackgroundDrawable(null);
                AndroidUtilities.clearCursorDrawable(this.inputFields[a]);
                if (a == 4) {
                    this.inputFields[a].setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            if (PaymentFormActivity.this.getParentActivity() == null) {
                                return false;
                            }
                            if (event.getAction() == 1) {
                                CountrySelectActivity fragment = new CountrySelectActivity(false);
                                fragment.setCountrySelectActivityDelegate(new CountrySelectActivityDelegate() {
                                    public void didSelectCountry(String name, String shortName) {
                                        PaymentFormActivity.this.inputFields[4].setText(name);
                                        PaymentFormActivity.this.countryName = shortName;
                                    }
                                });
                                PaymentFormActivity.this.presentFragment(fragment);
                            }
                            return true;
                        }
                    });
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
                            editText = this.inputFields[a];
                            if (value == null) {
                                value = this.countryName;
                            }
                            editText.setText(value);
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
                    this.inputFields[a].setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
                    this.inputFields[a].setGravity(19);
                    this.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(5)});
                    container.addView(this.inputFields[a], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                    this.inputFields[a].addTextChangedListener(new TextWatcher() {
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
                                            EditText editText = PaymentFormActivity.this.inputFields[8];
                                            text = text.substring(0, 1);
                                            editText.setText(text);
                                        }
                                    }
                                    String country = (String) PaymentFormActivity.this.codesMap.get(text);
                                    boolean set = false;
                                    if (!(country == null || PaymentFormActivity.this.countriesArray.indexOf(country) == -1)) {
                                        String hint = (String) PaymentFormActivity.this.phoneFormatMap.get(text);
                                        if (hint != null) {
                                            set = true;
                                            phoneField.setHintText(hint.replace('X', '–'));
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
                    });
                } else if (a == 9) {
                    this.inputFields[a].setPadding(0, 0, 0, 0);
                    this.inputFields[a].setGravity(19);
                    container.addView(this.inputFields[a], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 17.0f, 6.0f));
                    this.inputFields[a].addTextChangedListener(new TextWatcher() {
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
                    });
                } else {
                    this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                    this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                    container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                }
                this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
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
                });
                if (a == 9) {
                    this.sectionCell[1] = new ShadowSectionCell(context);
                    this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
                    this.checkCell1 = new TextCheckCell(context);
                    this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    this.checkCell1.setTextAndCheck(LocaleController.getString("PaymentShippingSave", R.string.PaymentShippingSave), this.saveShippingInfo, false);
                    this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
                    this.checkCell1.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            PaymentFormActivity.this.saveShippingInfo = !PaymentFormActivity.this.saveShippingInfo;
                            PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveShippingInfo);
                        }
                    });
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
            } else if (this.paymentForm.invoice.name_requested) {
                this.inputFields[6].setImeOptions(268435462);
            } else if (this.paymentForm.invoice.email_requested) {
                this.inputFields[7].setImeOptions(268435462);
            } else {
                this.inputFields[5].setImeOptions(268435462);
            }
            ShadowSectionCell shadowSectionCell = this.sectionCell[1];
            int i = (this.paymentForm.invoice.name_requested || this.paymentForm.invoice.phone_requested || this.paymentForm.invoice.email_requested) ? 0 : 8;
            shadowSectionCell.setVisibility(i);
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
                    FileLog.e(e2);
                }
                if (country != null) {
                    String countryName = (String) languageMap.get(country);
                    if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                        this.inputFields[8].setText((CharSequence) this.countriesMap.get(countryName));
                    }
                }
            }
        } else if (this.currentStep == 2) {
            if ("stripe".equals(this.paymentForm.native_provider)) {
                JSONObject jSONObject = new JSONObject(this.paymentForm.native_params.data);
                try {
                    this.need_card_country = jSONObject.getBoolean("need_country");
                } catch (Exception e3) {
                    this.need_card_country = false;
                }
                try {
                    this.need_card_postcode = jSONObject.getBoolean("need_zip");
                } catch (Exception e4) {
                    this.need_card_postcode = false;
                }
                try {
                    this.need_card_name = jSONObject.getBoolean("need_cardholder_name");
                    try {
                        this.stripeApiKey = jSONObject.getString("publishable_key");
                    } catch (Exception e5) {
                        this.stripeApiKey = "";
                    }
                } catch (Throwable e22) {
                    FileLog.e("tmessages", e22);
                }
                this.inputFields = new EditText[7];
                a = 0;
                while (a < 7) {
                    if (a == 0) {
                        this.headerCell[0] = new HeaderCell(context);
                        this.headerCell[0].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.headerCell[0].setText(LocaleController.getString("PaymentCardTitle", R.string.PaymentCardTitle));
                        this.linearLayout2.addView(this.headerCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 5) {
                        this.headerCell[1] = new HeaderCell(context);
                        this.headerCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.headerCell[1].setText(LocaleController.getString("PaymentBillingAddress", R.string.PaymentBillingAddress));
                        this.linearLayout2.addView(this.headerCell[1], LayoutHelper.createLinear(-1, -2));
                    }
                    allowDivider = (a == 4 || a == 6 || (a == 5 && !this.need_card_postcode)) ? false : true;
                    if (a == 1) {
                        linearLayout = new LinearLayout(context) {
                            protected void onDraw(Canvas canvas) {
                                canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
                            }
                        };
                        ((LinearLayout) linearLayout).setOrientation(0);
                        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, 48));
                        this.dividers.add(linearLayout);
                    } else if (a == 2) {
                        container = (ViewGroup) this.inputFields[1].getParent();
                    } else {
                        linearLayout = new FrameLayout(context);
                        this.linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, 48));
                        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        if (allowDivider) {
                            view = new View(context);
                            this.dividers.add(view);
                            view.setBackgroundColor(Theme.getColor(Theme.key_divider));
                            linearLayout.addView(view, new LayoutParams(-1, 1, 83));
                        }
                    }
                    OnTouchListener onTouchListener = null;
                    this.inputFields[a] = new EditText(context);
                    this.inputFields[a].setTag(Integer.valueOf(a));
                    this.inputFields[a].setTextSize(1, 16.0f);
                    this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                    this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputFields[a].setBackgroundDrawable(null);
                    AndroidUtilities.clearCursorDrawable(this.inputFields[a]);
                    if (a == 4) {
                        this.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(3)});
                        this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
                        this.inputFields[a].setTypeface(Typeface.DEFAULT);
                        this.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else if (a == 0) {
                        this.inputFields[a].setInputType(2);
                    } else if (a == 5) {
                        this.inputFields[a].setOnTouchListener(new OnTouchListener() {
                            public boolean onTouch(View v, MotionEvent event) {
                                if (PaymentFormActivity.this.getParentActivity() == null) {
                                    return false;
                                }
                                if (event.getAction() == 1) {
                                    CountrySelectActivity fragment = new CountrySelectActivity(false);
                                    fragment.setCountrySelectActivityDelegate(new CountrySelectActivityDelegate() {
                                        public void didSelectCountry(String name, String shortName) {
                                            PaymentFormActivity.this.inputFields[5].setText(name);
                                        }
                                    });
                                    PaymentFormActivity.this.presentFragment(fragment);
                                }
                                return true;
                            }
                        });
                    } else if (a == 1 || a == 2) {
                        boolean isMonth = a == 1;
                        editText = this.inputFields[a];
                        final boolean z = isMonth;
                        OnTouchListener anonymousClass12 = new OnTouchListener() {
                            public boolean onTouch(View v, MotionEvent event) {
                                if (PaymentFormActivity.this.getParentActivity() == null) {
                                    return false;
                                }
                                if (event.getAction() != 1) {
                                    return true;
                                }
                                final int[] result = new int[1];
                                View focusedView = PaymentFormActivity.this.getParentActivity().getCurrentFocus();
                                if (focusedView != null) {
                                    AndroidUtilities.hideKeyboard(focusedView);
                                    focusedView.clearFocus();
                                }
                                PaymentFormActivity.this.showDialog(AlertsCreator.createExpireDateAlert(PaymentFormActivity.this.getParentActivity(), z, result, new Runnable() {
                                    public void run() {
                                        int i;
                                        int i2 = 2;
                                        EditText[] access$1200 = PaymentFormActivity.this.inputFields;
                                        if (z) {
                                            i = 1;
                                        } else {
                                            i = 2;
                                        }
                                        access$1200[i].setHint(null);
                                        EditText[] access$12002 = PaymentFormActivity.this.inputFields;
                                        if (z) {
                                            i2 = 1;
                                        }
                                        access$12002[i2].setText(String.format(Locale.US, "%02d", new Object[]{Integer.valueOf(result[0])}));
                                    }
                                }));
                                return true;
                            }
                        };
                        editText.setOnTouchListener(anonymousClass12);
                    } else {
                        this.inputFields[a].setInputType(16385);
                    }
                    this.inputFields[a].setImeOptions((a == 6 ? 6 : 5) | 268435456);
                    switch (a) {
                        case 0:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardNumber", R.string.PaymentCardNumber));
                            break;
                        case 1:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardExpireDateMonth", R.string.PaymentCardExpireDateMonth));
                            break;
                        case 2:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardExpireDateYear", R.string.PaymentCardExpireDateYear));
                            break;
                        case 3:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardName", R.string.PaymentCardName));
                            break;
                        case 4:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentCardCvv", R.string.PaymentCardCvv));
                            break;
                        case 5:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentShippingCountry", R.string.PaymentShippingCountry));
                            break;
                        case 6:
                            this.inputFields[a].setHint(LocaleController.getString("PaymentShippingZipPlaceholder", R.string.PaymentShippingZipPlaceholder));
                            break;
                    }
                    if (a == 0) {
                        this.inputFields[a].addTextChangedListener(new TextWatcher() {
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
                        });
                    }
                    this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                    this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                    if (a == 1) {
                        this.inputFields[a].setPadding(0, AndroidUtilities.dp(12.0f), 0, 0);
                        container.addView(this.inputFields[a], LayoutHelper.createLinear(-2, -2, 51, 17, 0, 8, 0));
                        view = new ImageView(context);
                        view.setScaleType(ScaleType.CENTER);
                        view.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                        view.setImageResource(R.drawable.ic_arrow_drop_down);
                        view.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteHintText), Mode.MULTIPLY));
                        container.addView(view, LayoutHelper.createLinear(-2, -1, 0.0f, 0.0f, 18.0f, 0.0f));
                        view.setOnTouchListener(onTouchListener);
                        this.textView = new TextView(context);
                        this.textView.setText("/");
                        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                        this.textView.setTextSize(1, 16.0f);
                        container.addView(this.textView, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, 0.0f));
                    } else if (a == 2) {
                        this.inputFields[a].setPadding(0, AndroidUtilities.dp(12.0f), 0, 0);
                        container.addView(this.inputFields[a], LayoutHelper.createLinear(-2, -2, 51, 17, 0, 8, 0));
                        view = new ImageView(context);
                        view.setScaleType(ScaleType.CENTER);
                        view.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                        view.setImageResource(R.drawable.ic_arrow_drop_down);
                        view.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteHintText), Mode.MULTIPLY));
                        container.addView(view, LayoutHelper.createLinear(-2, -1));
                        view.setOnTouchListener(onTouchListener);
                    } else {
                        container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                    }
                    this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i == 5) {
                                int num = ((Integer) textView.getTag()).intValue() + 1;
                                if (num == 1) {
                                    num += 2;
                                }
                                if (num == 5) {
                                    num++;
                                }
                                if (num < PaymentFormActivity.this.inputFields.length) {
                                    PaymentFormActivity.this.inputFields[num].requestFocus();
                                    return true;
                                }
                            } else if (i == 6) {
                                PaymentFormActivity.this.doneItem.performClick();
                                return true;
                            }
                            return false;
                        }
                    });
                    if (a == 4) {
                        this.sectionCell[0] = new ShadowSectionCell(context);
                        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
                    } else if (a == 6) {
                        this.sectionCell[2] = new ShadowSectionCell(context);
                        this.linearLayout2.addView(this.sectionCell[2], LayoutHelper.createLinear(-1, -2));
                        this.checkCell1 = new TextCheckCell(context);
                        this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        this.checkCell1.setTextAndCheck(LocaleController.getString("PaymentCardSavePaymentInformation", R.string.PaymentCardSavePaymentInformation), this.saveCardInfo, false);
                        this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
                        this.checkCell1.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                PaymentFormActivity.this.saveCardInfo = !PaymentFormActivity.this.saveCardInfo;
                                PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveCardInfo);
                            }
                        });
                        this.bottomCell[0] = new TextInfoPrivacyCell(context);
                        this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        updateSavePaymentField();
                        this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                    }
                    if ((a == 5 && !this.need_card_country) || ((a == 6 && !this.need_card_postcode) || (a == 3 && !this.need_card_name))) {
                        container.setVisibility(8);
                    }
                    a++;
                }
                if (!(this.need_card_country || this.need_card_postcode)) {
                    this.headerCell[1].setVisibility(8);
                    this.sectionCell[0].setVisibility(8);
                }
            } else {
                this.webviewLoading = true;
                showEditDoneProgress(true);
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItem.getImageView().setVisibility(4);
                this.webView = new WebView(context);
                this.webView.getSettings().setJavaScriptEnabled(true);
                this.webView.getSettings().setDomStorageEnabled(true);
                if (VERSION.SDK_INT >= 21) {
                    this.webView.getSettings().setMixedContentMode(0);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
                    PaymentFormActivity paymentFormActivity = this;
                    this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
                }
                this.webView.setWebViewClient(new WebViewClient() {
                    public void onLoadResource(WebView view, String url) {
                        super.onLoadResource(view, url);
                    }

                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        PaymentFormActivity.this.webviewLoading = false;
                        PaymentFormActivity.this.showEditDoneProgress(false);
                        PaymentFormActivity.this.updateSavePaymentField();
                    }
                });
                this.linearLayout2.addView(this.webView, LayoutHelper.createFrame(-1, -2.0f));
                this.sectionCell[2] = new ShadowSectionCell(context);
                this.linearLayout2.addView(this.sectionCell[2], LayoutHelper.createLinear(-1, -2));
                this.checkCell1 = new TextCheckCell(context);
                this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.checkCell1.setTextAndCheck(LocaleController.getString("PaymentCardSavePaymentInformation", R.string.PaymentCardSavePaymentInformation), this.saveCardInfo, false);
                this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
                this.checkCell1.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymentFormActivity.this.saveCardInfo = !PaymentFormActivity.this.saveCardInfo;
                        PaymentFormActivity.this.checkCell1.setChecked(PaymentFormActivity.this.saveCardInfo);
                    }
                });
                this.bottomCell[0] = new TextInfoPrivacyCell(context);
                this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                updateSavePaymentField();
                this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
            }
        } else if (this.currentStep == 1) {
            int count = this.requestedInfo.shipping_options.size();
            this.radioCells = new RadioCell[count];
            for (a = 0; a < count; a++) {
                boolean z2;
                boolean z3;
                TL_shippingOption shippingOption = (TL_shippingOption) this.requestedInfo.shipping_options.get(a);
                this.radioCells[a] = new RadioCell(context);
                this.radioCells[a].setTag(Integer.valueOf(a));
                this.radioCells[a].setBackgroundDrawable(Theme.getSelectorDrawable(true));
                RadioCell radioCell = this.radioCells[a];
                r5 = new Object[2];
                r5[0] = getTotalPriceString(shippingOption.prices);
                r5[1] = shippingOption.title;
                String format = String.format("%s - %s", r5);
                if (a == 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (a != count - 1) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                radioCell.setText(format, z2, z3);
                this.radioCells[a].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        int num = ((Integer) v.getTag()).intValue();
                        int a = 0;
                        while (a < PaymentFormActivity.this.radioCells.length) {
                            PaymentFormActivity.this.radioCells[a].setChecked(num == a, true);
                            a++;
                        }
                    }
                });
                this.linearLayout2.addView(this.radioCells[a]);
            }
            this.bottomCell[0] = new TextInfoPrivacyCell(context);
            this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
        } else if (this.currentStep == 3) {
            this.inputFields = new EditText[2];
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
                this.inputFields[a] = new EditText(context);
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setBackgroundDrawable(null);
                AndroidUtilities.clearCursorDrawable(this.inputFields[a]);
                if (a == 0) {
                    this.inputFields[a].setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                } else {
                    this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                    this.inputFields[a].setTypeface(Typeface.DEFAULT);
                }
                this.inputFields[a].setImeOptions((a == 6 ? 6 : 5) | 268435456);
                switch (a) {
                    case 0:
                        this.inputFields[a].setText(this.paymentForm.saved_credentials.title);
                        break;
                    case 1:
                        this.inputFields[a].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                        this.inputFields[a].requestFocus();
                        break;
                }
                this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                view.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
                this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i != 6) {
                            return false;
                        }
                        PaymentFormActivity.this.doneItem.performClick();
                        return true;
                    }
                });
                if (a == 1) {
                    this.bottomCell[0] = new TextInfoPrivacyCell(context);
                    this.bottomCell[0].setText(LocaleController.formatString("PaymentConfirmationMessage", R.string.PaymentConfirmationMessage, this.paymentForm.saved_credentials.title));
                    this.bottomCell[0].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.bottomCell[0], LayoutHelper.createLinear(-1, -2));
                    this.settingsCell1 = new TextSettingsCell(context);
                    this.settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    this.settingsCell1.setText(LocaleController.getString("PaymentConfirmationNewCard", R.string.PaymentConfirmationNewCard), false);
                    this.linearLayout2.addView(this.settingsCell1, LayoutHelper.createLinear(-1, -2));
                    this.settingsCell1.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            PaymentFormActivity.this.passwordOk = false;
                            PaymentFormActivity.this.goToNextStep();
                        }
                    });
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
            ArrayList<TL_labeledPrice> arrayList = new ArrayList();
            arrayList.addAll(this.paymentForm.invoice.prices);
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
                this.detailSettingsCell[0].setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymentFormActivity activity = new PaymentFormActivity(PaymentFormActivity.this.paymentForm, PaymentFormActivity.this.messageObject, 2, PaymentFormActivity.this.requestedInfo, PaymentFormActivity.this.shippingOption, null, PaymentFormActivity.this.cardName, PaymentFormActivity.this.validateRequest, PaymentFormActivity.this.saveCardInfo);
                        activity.setDelegate(new PaymentFormActivityDelegate() {
                            public void didSelectNewCard(String tokenJson, String card, boolean saveCard) {
                                PaymentFormActivity.this.paymentJson = tokenJson;
                                PaymentFormActivity.this.saveCardInfo = saveCard;
                                PaymentFormActivity.this.cardName = card;
                                PaymentFormActivity.this.detailSettingsCell[0].setTextAndValue(PaymentFormActivity.this.cardName, LocaleController.getString("PaymentCheckoutMethod", R.string.PaymentCheckoutMethod), true);
                            }
                        });
                        PaymentFormActivity.this.presentFragment(activity);
                    }
                });
            }
            if (this.validateRequest != null) {
                if (this.validateRequest.info.shipping_address != null) {
                    String address = String.format("%s %s, %s, %s, %s, %s", new Object[]{this.validateRequest.info.shipping_address.street_line1, this.validateRequest.info.shipping_address.street_line2, this.validateRequest.info.shipping_address.city, this.validateRequest.info.shipping_address.state, this.validateRequest.info.shipping_address.country_iso2, this.validateRequest.info.shipping_address.post_code});
                    this.detailSettingsCell[1] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[1].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[1].setTextAndValue(address, LocaleController.getString("PaymentShippingAddress", R.string.PaymentShippingAddress), true);
                    this.linearLayout2.addView(this.detailSettingsCell[1]);
                }
                if (this.validateRequest.info.name != null) {
                    this.detailSettingsCell[2] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[2].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[2].setTextAndValue(this.validateRequest.info.name, LocaleController.getString("PaymentCheckoutName", R.string.PaymentCheckoutName), true);
                    this.linearLayout2.addView(this.detailSettingsCell[2]);
                }
                if (this.validateRequest.info.phone != null) {
                    this.detailSettingsCell[3] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[3].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[3].setTextAndValue(PhoneFormat.getInstance().format(this.validateRequest.info.phone), LocaleController.getString("PaymentCheckoutPhoneNumber", R.string.PaymentCheckoutPhoneNumber), true);
                    this.linearLayout2.addView(this.detailSettingsCell[3]);
                }
                if (this.validateRequest.info.email != null) {
                    this.detailSettingsCell[4] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[4].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[4].setTextAndValue(this.validateRequest.info.email, LocaleController.getString("PaymentCheckoutEmail", R.string.PaymentCheckoutEmail), true);
                    this.linearLayout2.addView(this.detailSettingsCell[4]);
                }
                if (this.shippingOption != null) {
                    this.detailSettingsCell[5] = new TextDetailSettingsCell(context);
                    this.detailSettingsCell[5].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.detailSettingsCell[5].setTextAndValue(this.shippingOption.title, LocaleController.getString("PaymentCheckoutShippingMethod", R.string.PaymentCheckoutShippingMethod), false);
                    this.linearLayout2.addView(this.detailSettingsCell[5]);
                }
            }
            if (this.currentStep == 4) {
                this.bottomLayout = new FrameLayout(context);
                this.bottomLayout.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
                final String str = totalPrice;
                this.bottomLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Builder builder = new Builder(PaymentFormActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("PaymentTransactionReview", R.string.PaymentTransactionReview));
                        builder.setMessage(LocaleController.formatString("PaymentTransactionMessage", R.string.PaymentTransactionMessage, str, PaymentFormActivity.this.currentBotName, PaymentFormActivity.this.currentItemName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PaymentFormActivity.this.showEditDoneProgress(true);
                                PaymentFormActivity.this.setDonePressed(true);
                                PaymentFormActivity.this.sendData();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        PaymentFormActivity.this.showDialog(builder.create());
                    }
                });
                this.payTextView = new TextView(context);
                this.payTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText6));
                this.payTextView.setText(LocaleController.formatString("PaymentCheckoutPay", R.string.PaymentCheckoutPay, totalPrice));
                this.payTextView.setTextSize(1, 14.0f);
                this.payTextView.setGravity(17);
                this.payTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.bottomLayout.addView(this.payTextView, LayoutHelper.createFrame(-1, -1.0f));
                this.progressView = new ContextProgressView(context, 0);
                this.progressView.setVisibility(4);
                this.bottomLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
                view = new View(context);
                view.setBackgroundResource(R.drawable.header_shadow_reverse);
                frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            }
            this.sectionCell[1] = new ShadowSectionCell(context);
            this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        }
        return this.fragmentView;
    }

    private String getTotalPriceString(ArrayList<TL_labeledPrice> prices) {
        int amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount = (int) (((TL_labeledPrice) prices.get(a)).amount + ((long) amount));
        }
        return LocaleController.getInstance().formatCurrencyString((long) amount, this.paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TL_labeledPrice> prices) {
        int amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount = (int) (((TL_labeledPrice) prices.get(a)).amount + ((long) amount));
        }
        return LocaleController.getInstance().formatCurrencyDecimalString((long) amount, this.paymentForm.invoice.currency);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didRemovedTwoStepPassword);
        if (this.currentStep != 4) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.paymentFinished);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didRemovedTwoStepPassword);
        if (this.currentStep != 4) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.paymentFinished);
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
                FileLog.e(e);
            }
        }
        try {
            if (this.currentStep == 2 && VERSION.SDK_INT >= 23 && (UserConfig.passcodeHash.length() == 0 || UserConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(8192);
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        super.onFragmentDestroy();
        this.canceled = true;
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            if (this.webView != null) {
                this.webView.loadUrl(this.paymentForm.url);
            } else if (this.currentStep == 2) {
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            } else if (this.currentStep == 3) {
                AndroidUtilities.showKeyboard(this.inputFields[1]);
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            this.paymentForm.password_missing = false;
            updateSavePaymentField();
        } else if (id == NotificationCenter.didRemovedTwoStepPassword) {
            this.paymentForm.password_missing = true;
            updateSavePaymentField();
        } else if (id == NotificationCenter.paymentFinished) {
            removeSelfFromStack();
        }
    }

    private void goToNextStep() {
        int nextStep;
        if (this.currentStep == 0) {
            if (this.paymentForm.invoice.flexible) {
                nextStep = 1;
            } else if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.tmpPassword != null && UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60) {
                    UserConfig.tmpPassword = null;
                    UserConfig.saveConfig(false);
                }
                if (UserConfig.tmpPassword != null) {
                    nextStep = 4;
                } else {
                    nextStep = 3;
                }
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, null, null, this.cardName, this.validateRequest, this.saveCardInfo));
        } else if (this.currentStep == 1) {
            if (this.paymentForm.saved_credentials != null) {
                if (UserConfig.tmpPassword != null && UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60) {
                    UserConfig.tmpPassword = null;
                    UserConfig.saveConfig(false);
                }
                if (UserConfig.tmpPassword != null) {
                    nextStep = 4;
                } else {
                    nextStep = 3;
                }
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo));
        } else if (this.currentStep == 2) {
            if (this.delegate != null) {
                this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo);
                finishFragment();
                return;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo));
        } else if (this.currentStep == 3) {
            if (this.passwordOk) {
                nextStep = 4;
            } else {
                nextStep = 2;
            }
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, nextStep, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo), !this.passwordOk);
        } else if (this.currentStep == 4) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
            finishFragment();
        }
    }

    private void updateSavePaymentField() {
        if (this.bottomCell[0] != null) {
            if (!this.paymentForm.can_save_credentials || (this.webView != null && (this.webView == null || this.webviewLoading))) {
                this.checkCell1.setVisibility(8);
                this.bottomCell[0].setVisibility(8);
                this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                return;
            }
            SpannableStringBuilder text = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", R.string.PaymentCardSavePaymentInformationInfoLine1));
            if (this.paymentForm.password_missing) {
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
                    text.replace(index2, index2 + 1, "");
                    text.replace(index1, index1 + 1, "");
                    text.setSpan(new LinkSpan(), index1, index2 - 1, 33);
                }
                this.checkCell1.setEnabled(false);
            } else {
                this.checkCell1.setEnabled(true);
            }
            this.bottomCell[0].setText(text);
            this.checkCell1.setVisibility(0);
            this.bottomCell[0].setVisibility(0);
            this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
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
            FileLog.e(e);
        }
    }

    private boolean sendCardData() {
        if (!(this.paymentForm.saved_credentials == null || this.saveCardInfo || !this.paymentForm.can_save_credentials)) {
            TL_payments_clearSavedInfo req = new TL_payments_clearSavedInfo();
            req.credentials = true;
            this.paymentForm.saved_credentials = null;
            UserConfig.tmpPassword = null;
            UserConfig.saveConfig(false);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
        Card card = new Card(this.inputFields[0].getText().toString(), Utilities.parseInt(this.inputFields[1].getText().toString()), Utilities.parseInt(this.inputFields[2].getText().toString()), this.inputFields[4].getText().toString(), this.inputFields[3].getText().toString(), null, null, null, null, this.inputFields[6].getText().toString(), this.inputFields[5].getText().toString(), null);
        this.cardName = card.getType() + " *" + card.getLast4();
        if (!card.validateNumber()) {
            shakeField(0);
            return false;
        } else if (!card.validateExpMonth()) {
            shakeField(1);
            return false;
        } else if (!card.validateExpYear()) {
            shakeField(2);
            return false;
        } else if (!card.validateExpiryDate()) {
            shakeField(1);
            return false;
        } else if (this.need_card_name && this.inputFields[3].length() == 0) {
            shakeField(3);
            return false;
        } else if (!card.validateCVC()) {
            shakeField(4);
            return false;
        } else if (this.need_card_country && this.inputFields[5].length() == 0) {
            shakeField(5);
            return false;
        } else if (this.need_card_postcode && this.inputFields[6].length() == 0) {
            shakeField(6);
            return false;
        } else {
            showEditDoneProgress(true);
            try {
                new Stripe(this.stripeApiKey).createToken(card, new TokenCallback() {
                    public void onSuccess(Token token) {
                        if (!PaymentFormActivity.this.canceled) {
                            PaymentFormActivity.this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[]{token.getType(), token.getId()});
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    PaymentFormActivity.this.goToNextStep();
                                    PaymentFormActivity.this.showEditDoneProgress(false);
                                    PaymentFormActivity.this.setDonePressed(false);
                                }
                            });
                        }
                    }

                    public void onError(Exception error) {
                        if (!PaymentFormActivity.this.canceled) {
                            PaymentFormActivity.this.showEditDoneProgress(false);
                            PaymentFormActivity.this.setDonePressed(false);
                            if ((error instanceof APIConnectionException) || (error instanceof APIException)) {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", R.string.PaymentConnectionFailed));
                            } else {
                                AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
                            }
                        }
                    }
                });
            } catch (Throwable e) {
                FileLog.e(e);
            }
            return true;
        }
    }

    private void sendForm() {
        if (!this.canceled) {
            TL_paymentRequestedInfo tL_paymentRequestedInfo;
            showEditDoneProgress(true);
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
                this.validateRequest.info.email = this.inputFields[7].getText().toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 4;
            }
            if (this.paymentForm.invoice.shipping_address_requested) {
                this.validateRequest.info.shipping_address = new TL_postAddress();
                this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
                this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
                this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
                this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
                this.validateRequest.info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : "";
                this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
                tL_paymentRequestedInfo = this.validateRequest.info;
                tL_paymentRequestedInfo.flags |= 8;
            }
            final TLObject req = this.validateRequest;
            ConnectionsManager.getInstance().sendRequest(this.validateRequest, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    if (response instanceof TL_payments_validatedRequestedInfo) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                PaymentFormActivity.this.requestedInfo = (TL_payments_validatedRequestedInfo) response;
                                if (!(PaymentFormActivity.this.paymentForm.saved_info == null || PaymentFormActivity.this.saveShippingInfo)) {
                                    TL_payments_clearSavedInfo req = new TL_payments_clearSavedInfo();
                                    req.info = true;
                                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                        public void run(TLObject response, TL_error error) {
                                        }
                                    });
                                }
                                PaymentFormActivity.this.goToNextStep();
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(false);
                            }
                        });
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(false);
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
                                            PaymentFormActivity.this.shakeField(6);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(9);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(7);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(4);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(2);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(5);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(3);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(0);
                                            return;
                                        case true:
                                            PaymentFormActivity.this.shakeField(1);
                                            return;
                                        default:
                                            AlertsCreator.processError(error, PaymentFormActivity.this, req, new Object[0]);
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
            info.phone = "+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString();
            info.flags |= 2;
        }
        if (this.paymentForm.invoice.email_requested) {
            info.email = this.inputFields[7].getText().toString();
            info.flags |= 4;
        }
        if (this.paymentForm.invoice.shipping_address_requested) {
            info.shipping_address = new TL_postAddress();
            info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
            info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
            info.shipping_address.city = this.inputFields[2].getText().toString();
            info.shipping_address.state = this.inputFields[3].getText().toString();
            info.shipping_address.country_iso2 = this.countryName != null ? this.countryName : "";
            info.shipping_address.post_code = this.inputFields[5].getText().toString();
            info.flags |= 8;
        }
        return info;
    }

    private void sendData() {
        if (!this.canceled) {
            showEditDoneProgress(true);
            final TL_payments_sendPaymentForm req = new TL_payments_sendPaymentForm();
            req.msg_id = this.messageObject.getId();
            if (UserConfig.tmpPassword == null || this.paymentForm.saved_credentials == null) {
                req.credentials = new TL_inputPaymentCredentials();
                req.credentials.save = this.saveCardInfo;
                req.credentials.data = new TL_dataJSON();
                req.credentials.data.data = this.paymentJson;
            } else {
                req.credentials = new TL_inputPaymentCredentialsSaved();
                req.credentials.id = this.paymentForm.saved_credentials.id;
                req.credentials.tmp_password = UserConfig.tmpPassword.tmp_password;
            }
            if (!(this.requestedInfo == null || this.requestedInfo.id == null)) {
                req.requested_info_id = this.requestedInfo.id;
                req.flags |= 1;
            }
            if (this.shippingOption != null) {
                req.shipping_option_id = this.shippingOption.id;
                req.flags |= 2;
            }
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    if (response == null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(error, PaymentFormActivity.this, req, new Object[0]);
                                PaymentFormActivity.this.setDonePressed(false);
                                PaymentFormActivity.this.showEditDoneProgress(false);
                            }
                        });
                    } else if (response instanceof TL_payments_paymentResult) {
                        MessagesController.getInstance().processUpdates(((TL_payments_paymentResult) response).updates, false);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                PaymentFormActivity.this.goToNextStep();
                            }
                        });
                    } else if (response instanceof TL_payments_paymentVerficationNeeded) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                Browser.openUrl(PaymentFormActivity.this.getParentActivity(), ((TL_payments_paymentVerficationNeeded) response).url, false);
                                PaymentFormActivity.this.goToNextStep();
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
        if (UserConfig.tmpPassword != null && UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60) {
            UserConfig.tmpPassword = null;
            UserConfig.saveConfig(false);
        }
        if (UserConfig.tmpPassword != null) {
            sendData();
        } else if (this.inputFields[1].length() == 0) {
            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.inputFields[1], 2.0f, 0);
        } else {
            final String password = this.inputFields[1].getText().toString();
            showEditDoneProgress(true);
            setDonePressed(true);
            final TL_account_getPassword req = new TL_account_getPassword();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error != null) {
                                AlertsCreator.processError(error, PaymentFormActivity.this, req, new Object[0]);
                                PaymentFormActivity.this.showEditDoneProgress(false);
                                PaymentFormActivity.this.setDonePressed(false);
                            } else if (response instanceof TL_account_noPassword) {
                                PaymentFormActivity.this.passwordOk = false;
                                PaymentFormActivity.this.goToNextStep();
                            } else {
                                TL_account_password currentPassword = response;
                                byte[] passwordBytes = null;
                                try {
                                    passwordBytes = password.getBytes("UTF-8");
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                                byte[] hash = new byte[((currentPassword.current_salt.length * 2) + passwordBytes.length)];
                                System.arraycopy(currentPassword.current_salt, 0, hash, 0, currentPassword.current_salt.length);
                                System.arraycopy(passwordBytes, 0, hash, currentPassword.current_salt.length, passwordBytes.length);
                                System.arraycopy(currentPassword.current_salt, 0, hash, hash.length - currentPassword.current_salt.length, currentPassword.current_salt.length);
                                final TL_account_getTmpPassword req = new TL_account_getTmpPassword();
                                req.password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                                req.period = 1800;
                                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                    public void run(final TLObject response, final TL_error error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                PaymentFormActivity.this.showEditDoneProgress(false);
                                                PaymentFormActivity.this.setDonePressed(false);
                                                if (response != null) {
                                                    PaymentFormActivity.this.passwordOk = true;
                                                    UserConfig.tmpPassword = (TL_account_tmpPassword) response;
                                                    UserConfig.saveConfig(false);
                                                    PaymentFormActivity.this.goToNextStep();
                                                } else if (error.text.equals("PASSWORD_HASH_INVALID")) {
                                                    Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                                                    if (v != null) {
                                                        v.vibrate(200);
                                                    }
                                                    AndroidUtilities.shakeView(PaymentFormActivity.this.inputFields[1], 2.0f, 0);
                                                } else {
                                                    AlertsCreator.processError(error, PaymentFormActivity.this, req, new Object[0]);
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

    private void showEditDoneProgress(final boolean show) {
        if (this.doneItemAnimation != null) {
            this.doneItemAnimation.cancel();
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.doneItem != null) {
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
                this.progressView.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.payTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
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
                arrayList.add(new ThemeDescription(this.radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelectorSDK21));
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
        arrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelectorSDK21));
        arrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelectorSDK21));
        arrayList.add(new ThemeDescription(this.settingsCell1, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelectorSDK21));
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
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelectorSDK21));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
