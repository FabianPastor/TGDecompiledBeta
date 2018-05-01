package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.net.Uri;
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
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.Cart.Builder;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.FullWalletRequest.Builder;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.LineItem.Builder;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.MaskedWalletRequest.Builder;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters.Builder;
import com.google.android.gms.wallet.Payments;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.Wallet.WalletOptions.Builder;
import com.google.android.gms.wallet.fragment.WalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams.Builder;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions.Builder;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.TokenParser;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPaymentCredentials;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
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
import org.telegram.tgnet.TLRPC.TL_invoice;
import org.telegram.tgnet.TLRPC.TL_labeledPrice;
import org.telegram.tgnet.TLRPC.TL_paymentRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_paymentSavedCredentialsCard;
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
import org.telegram.ui.ActionBar.ActionBar;
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

public class PaymentFormActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
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
  private TLRPC.TL_inputPaymentCredentialsAndroidPay androidPayCredentials;
  private String androidPayPublicKey;
  private TLRPC.User botUser;
  private TextInfoPrivacyCell[] bottomCell = new TextInfoPrivacyCell[3];
  private FrameLayout bottomLayout;
  private boolean canceled;
  private String cardName;
  private TextCheckCell checkCell1;
  private HashMap<String, String> codesMap = new HashMap();
  private ArrayList<String> countriesArray = new ArrayList();
  private HashMap<String, String> countriesMap = new HashMap();
  private String countryName;
  private String currentBotName;
  private String currentItemName;
  private TLRPC.account_Password currentPassword;
  private int currentStep;
  private PaymentFormActivityDelegate delegate;
  private TextDetailSettingsCell[] detailSettingsCell = new TextDetailSettingsCell[7];
  private ArrayList<View> dividers = new ArrayList();
  private ActionBarMenuItem doneItem;
  private AnimatorSet doneItemAnimation;
  private boolean donePressed;
  private GoogleApiClient googleApiClient;
  private HeaderCell[] headerCell = new HeaderCell[3];
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
  private TLRPC.TL_payments_paymentForm paymentForm;
  private PaymentInfoCell paymentInfoCell;
  private String paymentJson;
  private HashMap<String, String> phoneFormatMap = new HashMap();
  private ContextProgressView progressView;
  private ContextProgressView progressViewButton;
  private RadioCell[] radioCells;
  private TLRPC.TL_payments_validatedRequestedInfo requestedInfo;
  private boolean saveCardInfo;
  private boolean saveShippingInfo;
  private ScrollView scrollView;
  private ShadowSectionCell[] sectionCell = new ShadowSectionCell[3];
  private TextSettingsCell settingsCell1;
  private TLRPC.TL_shippingOption shippingOption;
  private Runnable shortPollRunnable;
  private String stripeApiKey;
  private TextView textView;
  private String totalPriceDecimal;
  private TLRPC.TL_payments_validateRequestedInfo validateRequest;
  private boolean waitingForEmail;
  private WebView webView;
  private boolean webviewLoading;
  
  public PaymentFormActivity(MessageObject paramMessageObject, TLRPC.TL_payments_paymentReceipt paramTL_payments_paymentReceipt)
  {
    this.currentStep = 5;
    this.paymentForm = new TLRPC.TL_payments_paymentForm();
    this.paymentForm.bot_id = paramTL_payments_paymentReceipt.bot_id;
    this.paymentForm.invoice = paramTL_payments_paymentReceipt.invoice;
    this.paymentForm.provider_id = paramTL_payments_paymentReceipt.provider_id;
    this.paymentForm.users = paramTL_payments_paymentReceipt.users;
    this.shippingOption = paramTL_payments_paymentReceipt.shipping;
    this.messageObject = paramMessageObject;
    this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramTL_payments_paymentReceipt.bot_id));
    if (this.botUser != null) {}
    for (this.currentBotName = this.botUser.first_name;; this.currentBotName = "")
    {
      this.currentItemName = paramMessageObject.messageOwner.media.title;
      if (paramTL_payments_paymentReceipt.info != null)
      {
        this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
        this.validateRequest.info = paramTL_payments_paymentReceipt.info;
      }
      this.cardName = paramTL_payments_paymentReceipt.credentials_title;
      return;
    }
  }
  
  public PaymentFormActivity(TLRPC.TL_payments_paymentForm paramTL_payments_paymentForm, MessageObject paramMessageObject)
  {
    int i;
    if ((paramTL_payments_paymentForm.invoice.shipping_address_requested) || (paramTL_payments_paymentForm.invoice.email_requested) || (paramTL_payments_paymentForm.invoice.name_requested) || (paramTL_payments_paymentForm.invoice.phone_requested)) {
      i = 0;
    }
    for (;;)
    {
      init(paramTL_payments_paymentForm, paramMessageObject, i, null, null, null, null, null, false, null);
      return;
      if (paramTL_payments_paymentForm.saved_credentials != null)
      {
        if ((UserConfig.getInstance(this.currentAccount).tmpPassword != null) && (UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60))
        {
          UserConfig.getInstance(this.currentAccount).tmpPassword = null;
          UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
          i = 4;
        } else {
          i = 3;
        }
      }
      else
      {
        i = 2;
      }
    }
  }
  
  private PaymentFormActivity(TLRPC.TL_payments_paymentForm paramTL_payments_paymentForm, MessageObject paramMessageObject, int paramInt, TLRPC.TL_payments_validatedRequestedInfo paramTL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption paramTL_shippingOption, String paramString1, String paramString2, TLRPC.TL_payments_validateRequestedInfo paramTL_payments_validateRequestedInfo, boolean paramBoolean, TLRPC.TL_inputPaymentCredentialsAndroidPay paramTL_inputPaymentCredentialsAndroidPay)
  {
    init(paramTL_payments_paymentForm, paramMessageObject, paramInt, paramTL_payments_validatedRequestedInfo, paramTL_shippingOption, paramString1, paramString2, paramTL_payments_validateRequestedInfo, paramBoolean, paramTL_inputPaymentCredentialsAndroidPay);
  }
  
  private void checkPassword()
  {
    if ((UserConfig.getInstance(this.currentAccount).tmpPassword != null) && (UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60))
    {
      UserConfig.getInstance(this.currentAccount).tmpPassword = null;
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }
    if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
      sendData();
    }
    for (;;)
    {
      return;
      final Object localObject;
      if (this.inputFields[1].length() == 0)
      {
        localObject = (Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator");
        if (localObject != null) {
          ((Vibrator)localObject).vibrate(200L);
        }
        AndroidUtilities.shakeView(this.inputFields[1], 2.0F, 0);
      }
      else
      {
        localObject = this.inputFields[1].getText().toString();
        showEditDoneProgress(true, true);
        setDonePressed(true);
        final TLRPC.TL_account_getPassword localTL_account_getPassword = new TLRPC.TL_account_getPassword();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_getPassword, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (paramAnonymousTL_error == null) {
                  if ((paramAnonymousTLObject instanceof TLRPC.TL_account_noPassword))
                  {
                    PaymentFormActivity.access$3002(PaymentFormActivity.this, false);
                    PaymentFormActivity.this.goToNextStep();
                  }
                }
                for (;;)
                {
                  return;
                  TLRPC.TL_account_password localTL_account_password = (TLRPC.TL_account_password)paramAnonymousTLObject;
                  final Object localObject = null;
                  try
                  {
                    arrayOfByte = PaymentFormActivity.36.this.val$password.getBytes("UTF-8");
                    localObject = arrayOfByte;
                  }
                  catch (Exception localException)
                  {
                    for (;;)
                    {
                      byte[] arrayOfByte;
                      FileLog.e(localException);
                    }
                  }
                  arrayOfByte = new byte[localTL_account_password.current_salt.length * 2 + localObject.length];
                  System.arraycopy(localTL_account_password.current_salt, 0, arrayOfByte, 0, localTL_account_password.current_salt.length);
                  System.arraycopy(localObject, 0, arrayOfByte, localTL_account_password.current_salt.length, localObject.length);
                  System.arraycopy(localTL_account_password.current_salt, 0, arrayOfByte, arrayOfByte.length - localTL_account_password.current_salt.length, localTL_account_password.current_salt.length);
                  localObject = new TLRPC.TL_account_getTmpPassword();
                  ((TLRPC.TL_account_getTmpPassword)localObject).password_hash = Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length);
                  ((TLRPC.TL_account_getTmpPassword)localObject).period = 1800;
                  ConnectionsManager.getInstance(PaymentFormActivity.this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
                  {
                    public void run(final TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          PaymentFormActivity.this.showEditDoneProgress(true, false);
                          PaymentFormActivity.this.setDonePressed(false);
                          if (paramAnonymous3TLObject != null)
                          {
                            PaymentFormActivity.access$3002(PaymentFormActivity.this, true);
                            UserConfig.getInstance(PaymentFormActivity.this.currentAccount).tmpPassword = ((TLRPC.TL_account_tmpPassword)paramAnonymous3TLObject);
                            UserConfig.getInstance(PaymentFormActivity.this.currentAccount).saveConfig(false);
                            PaymentFormActivity.this.goToNextStep();
                          }
                          for (;;)
                          {
                            return;
                            if (paramAnonymous3TL_error.text.equals("PASSWORD_HASH_INVALID"))
                            {
                              Vibrator localVibrator = (Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator");
                              if (localVibrator != null) {
                                localVibrator.vibrate(200L);
                              }
                              AndroidUtilities.shakeView(PaymentFormActivity.this.inputFields[1], 2.0F, 0);
                              PaymentFormActivity.this.inputFields[1].setText("");
                            }
                            else
                            {
                              AlertsCreator.processError(PaymentFormActivity.this.currentAccount, paramAnonymous3TL_error, PaymentFormActivity.this, PaymentFormActivity.36.1.1.this.val$req, new Object[0]);
                            }
                          }
                        }
                      });
                    }
                  }, 2);
                  continue;
                  AlertsCreator.processError(PaymentFormActivity.this.currentAccount, paramAnonymousTL_error, PaymentFormActivity.this, PaymentFormActivity.36.this.val$req, new Object[0]);
                  PaymentFormActivity.this.showEditDoneProgress(true, false);
                  PaymentFormActivity.this.setDonePressed(false);
                }
              }
            });
          }
        }, 2);
      }
    }
  }
  
  private TLRPC.TL_paymentRequestedInfo getRequestInfo()
  {
    TLRPC.TL_paymentRequestedInfo localTL_paymentRequestedInfo = new TLRPC.TL_paymentRequestedInfo();
    if (this.paymentForm.invoice.name_requested)
    {
      localTL_paymentRequestedInfo.name = this.inputFields[6].getText().toString();
      localTL_paymentRequestedInfo.flags |= 0x1;
    }
    if (this.paymentForm.invoice.phone_requested)
    {
      localTL_paymentRequestedInfo.phone = ("+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString());
      localTL_paymentRequestedInfo.flags |= 0x2;
    }
    if (this.paymentForm.invoice.email_requested)
    {
      localTL_paymentRequestedInfo.email = this.inputFields[7].getText().toString().trim();
      localTL_paymentRequestedInfo.flags |= 0x4;
    }
    TLRPC.TL_postAddress localTL_postAddress;
    if (this.paymentForm.invoice.shipping_address_requested)
    {
      localTL_paymentRequestedInfo.shipping_address = new TLRPC.TL_postAddress();
      localTL_paymentRequestedInfo.shipping_address.street_line1 = this.inputFields[0].getText().toString();
      localTL_paymentRequestedInfo.shipping_address.street_line2 = this.inputFields[1].getText().toString();
      localTL_paymentRequestedInfo.shipping_address.city = this.inputFields[2].getText().toString();
      localTL_paymentRequestedInfo.shipping_address.state = this.inputFields[3].getText().toString();
      localTL_postAddress = localTL_paymentRequestedInfo.shipping_address;
      if (this.countryName == null) {
        break label320;
      }
    }
    label320:
    for (String str = this.countryName;; str = "")
    {
      localTL_postAddress.country_iso2 = str;
      localTL_paymentRequestedInfo.shipping_address.post_code = this.inputFields[5].getText().toString();
      localTL_paymentRequestedInfo.flags |= 0x8;
      return localTL_paymentRequestedInfo;
    }
  }
  
  private String getTotalPriceDecimalString(ArrayList<TLRPC.TL_labeledPrice> paramArrayList)
  {
    long l = 0L;
    for (int i = 0; i < paramArrayList.size(); i++) {
      l += ((TLRPC.TL_labeledPrice)paramArrayList.get(i)).amount;
    }
    return LocaleController.getInstance().formatCurrencyDecimalString(l, this.paymentForm.invoice.currency, false);
  }
  
  private String getTotalPriceString(ArrayList<TLRPC.TL_labeledPrice> paramArrayList)
  {
    long l = 0L;
    for (int i = 0; i < paramArrayList.size(); i++) {
      l += ((TLRPC.TL_labeledPrice)paramArrayList.get(i)).amount;
    }
    return LocaleController.getInstance().formatCurrencyString(l, this.paymentForm.invoice.currency);
  }
  
  private void goToNextStep()
  {
    int i;
    if (this.currentStep == 0) {
      if (this.paymentForm.invoice.flexible)
      {
        i = 1;
        presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, null, null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
      }
    }
    for (;;)
    {
      return;
      if (this.paymentForm.saved_credentials != null)
      {
        if ((UserConfig.getInstance(this.currentAccount).tmpPassword != null) && (UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60))
        {
          UserConfig.getInstance(this.currentAccount).tmpPassword = null;
          UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        if (UserConfig.getInstance(this.currentAccount).tmpPassword != null)
        {
          i = 4;
          break;
        }
        i = 3;
        break;
      }
      i = 2;
      break;
      if (this.currentStep == 1)
      {
        if (this.paymentForm.saved_credentials != null)
        {
          if ((UserConfig.getInstance(this.currentAccount).tmpPassword != null) && (UserConfig.getInstance(this.currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 60))
          {
            UserConfig.getInstance(this.currentAccount).tmpPassword = null;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
          }
          if (UserConfig.getInstance(this.currentAccount).tmpPassword != null) {
            i = 4;
          }
        }
        for (;;)
        {
          presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
          break;
          i = 3;
          continue;
          i = 2;
        }
      }
      if (this.currentStep == 2)
      {
        if ((this.paymentForm.password_missing) && (this.saveCardInfo))
        {
          this.passwordFragment = new PaymentFormActivity(this.paymentForm, this.messageObject, 6, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials);
          this.passwordFragment.setCurrentPassword(this.currentPassword);
          this.passwordFragment.setDelegate(new PaymentFormActivityDelegate()
          {
            public void currentPasswordUpdated(TLRPC.account_Password paramAnonymousaccount_Password)
            {
              PaymentFormActivity.access$4302(PaymentFormActivity.this, paramAnonymousaccount_Password);
            }
            
            public boolean didSelectNewCard(String paramAnonymousString1, String paramAnonymousString2, boolean paramAnonymousBoolean, TLRPC.TL_inputPaymentCredentialsAndroidPay paramAnonymousTL_inputPaymentCredentialsAndroidPay)
            {
              if (PaymentFormActivity.this.delegate != null) {
                PaymentFormActivity.this.delegate.didSelectNewCard(paramAnonymousString1, paramAnonymousString2, paramAnonymousBoolean, paramAnonymousTL_inputPaymentCredentialsAndroidPay);
              }
              if (PaymentFormActivity.this.isWebView) {
                PaymentFormActivity.this.removeSelfFromStack();
              }
              if (PaymentFormActivity.this.delegate != null) {}
              for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false) {
                return paramAnonymousBoolean;
              }
            }
            
            public void onFragmentDestroyed()
            {
              PaymentFormActivity.access$4402(PaymentFormActivity.this, null);
            }
          });
          presentFragment(this.passwordFragment, this.isWebView);
        }
        else if (this.delegate != null)
        {
          this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials);
          finishFragment();
        }
        else
        {
          presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), this.isWebView);
        }
      }
      else
      {
        if (this.currentStep == 3)
        {
          label561:
          PaymentFormActivity localPaymentFormActivity;
          if (this.passwordOk)
          {
            i = 4;
            localPaymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials);
            if (this.passwordOk) {
              break label630;
            }
          }
          label630:
          for (boolean bool = true;; bool = false)
          {
            presentFragment(localPaymentFormActivity, bool);
            break;
            i = 2;
            break label561;
          }
        }
        if (this.currentStep == 4)
        {
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
          finishFragment();
        }
        else if (this.currentStep == 6)
        {
          if (!this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo, this.androidPayCredentials)) {
            presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo, this.androidPayCredentials), true);
          } else {
            finishFragment();
          }
        }
      }
    }
  }
  
  private void init(TLRPC.TL_payments_paymentForm paramTL_payments_paymentForm, MessageObject paramMessageObject, int paramInt, TLRPC.TL_payments_validatedRequestedInfo paramTL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption paramTL_shippingOption, String paramString1, String paramString2, TLRPC.TL_payments_validateRequestedInfo paramTL_payments_validateRequestedInfo, boolean paramBoolean, TLRPC.TL_inputPaymentCredentialsAndroidPay paramTL_inputPaymentCredentialsAndroidPay)
  {
    boolean bool1 = true;
    this.currentStep = paramInt;
    this.paymentJson = paramString1;
    this.androidPayCredentials = paramTL_inputPaymentCredentialsAndroidPay;
    this.requestedInfo = paramTL_payments_validatedRequestedInfo;
    this.paymentForm = paramTL_payments_paymentForm;
    this.shippingOption = paramTL_shippingOption;
    this.messageObject = paramMessageObject;
    this.saveCardInfo = paramBoolean;
    boolean bool2;
    if (!"stripe".equals(this.paymentForm.native_provider))
    {
      bool2 = true;
      this.isWebView = bool2;
      this.botUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramTL_payments_paymentForm.bot_id));
      if (this.botUser == null) {
        break label178;
      }
      this.currentBotName = this.botUser.first_name;
      label112:
      this.currentItemName = paramMessageObject.messageOwner.media.title;
      this.validateRequest = paramTL_payments_validateRequestedInfo;
      this.saveShippingInfo = true;
      if (!paramBoolean) {
        break label188;
      }
      this.saveCardInfo = paramBoolean;
      if (paramString2 != null) {
        break label217;
      }
      if (paramTL_payments_paymentForm.saved_credentials == null) {}
    }
    label178:
    label188:
    label217:
    for (this.cardName = paramTL_payments_paymentForm.saved_credentials.title;; this.cardName = paramString2)
    {
      return;
      bool2 = false;
      break;
      this.currentBotName = "";
      break label112;
      if (this.paymentForm.saved_credentials != null) {}
      for (paramBoolean = bool1;; paramBoolean = false)
      {
        this.saveCardInfo = paramBoolean;
        break;
      }
    }
  }
  
  private void initAndroidPay(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 0) {
      return;
    }
    GoogleApiClient.Builder localBuilder = new GoogleApiClient.Builder(paramContext).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
    {
      public void onConnected(Bundle paramAnonymousBundle) {}
      
      public void onConnectionSuspended(int paramAnonymousInt) {}
    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
    {
      public void onConnectionFailed(ConnectionResult paramAnonymousConnectionResult) {}
    });
    paramContext = Wallet.API;
    Wallet.WalletOptions.Builder localBuilder1 = new Wallet.WalletOptions.Builder();
    if (this.paymentForm.invoice.test) {}
    for (int i = 3;; i = 1)
    {
      this.googleApiClient = localBuilder.addApi(paramContext, localBuilder1.setEnvironment(i).setTheme(1).build()).build();
      Wallet.Payments.isReadyToPay(this.googleApiClient).setResultCallback(new ResultCallback()
      {
        public void onResult(BooleanResult paramAnonymousBooleanResult)
        {
          if ((paramAnonymousBooleanResult.getStatus().isSuccess()) && (paramAnonymousBooleanResult.getValue())) {
            PaymentFormActivity.this.showAndroidPay();
          }
        }
      });
      this.googleApiClient.connect();
      break;
    }
  }
  
  private void loadPasswordInfo()
  {
    if (this.loadingPasswordInfo) {}
    for (;;)
    {
      return;
      this.loadingPasswordInfo = true;
      TLRPC.TL_account_getPassword localTL_account_getPassword = new TLRPC.TL_account_getPassword();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_getPassword, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              PaymentFormActivity.access$4202(PaymentFormActivity.this, false);
              if (paramAnonymousTL_error == null)
              {
                PaymentFormActivity.access$4302(PaymentFormActivity.this, (TLRPC.account_Password)paramAnonymousTLObject);
                if ((PaymentFormActivity.this.paymentForm != null) && ((PaymentFormActivity.this.currentPassword instanceof TLRPC.TL_account_password)))
                {
                  PaymentFormActivity.this.paymentForm.password_missing = false;
                  PaymentFormActivity.this.paymentForm.can_save_credentials = true;
                  PaymentFormActivity.this.updateSavePaymentField();
                }
                byte[] arrayOfByte = new byte[PaymentFormActivity.this.currentPassword.new_salt.length + 8];
                Utilities.random.nextBytes(arrayOfByte);
                System.arraycopy(PaymentFormActivity.this.currentPassword.new_salt, 0, arrayOfByte, 0, PaymentFormActivity.this.currentPassword.new_salt.length);
                PaymentFormActivity.this.currentPassword.new_salt = arrayOfByte;
                if (PaymentFormActivity.this.passwordFragment != null) {
                  PaymentFormActivity.this.passwordFragment.setCurrentPassword(PaymentFormActivity.this.currentPassword);
                }
              }
              if (((paramAnonymousTLObject instanceof TLRPC.TL_account_noPassword)) && (PaymentFormActivity.this.shortPollRunnable == null))
              {
                PaymentFormActivity.access$4602(PaymentFormActivity.this, new Runnable()
                {
                  public void run()
                  {
                    if (PaymentFormActivity.this.shortPollRunnable == null) {}
                    for (;;)
                    {
                      return;
                      PaymentFormActivity.this.loadPasswordInfo();
                      PaymentFormActivity.access$4602(PaymentFormActivity.this, null);
                    }
                  }
                });
                AndroidUtilities.runOnUIThread(PaymentFormActivity.this.shortPollRunnable, 5000L);
              }
            }
          });
        }
      }, 10);
    }
  }
  
  private boolean sendCardData()
  {
    Object localObject1 = this.inputFields[1].getText().toString().split("/");
    Object localObject2;
    boolean bool;
    if (localObject1.length == 2)
    {
      localObject2 = Utilities.parseInt(localObject1[0]);
      localObject1 = Utilities.parseInt(localObject1[1]);
      localObject2 = new Card(this.inputFields[0].getText().toString(), (Integer)localObject2, (Integer)localObject1, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), null, null, null, null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), null);
      this.cardName = (((Card)localObject2).getType() + " *" + ((Card)localObject2).getLast4());
      if (((Card)localObject2).validateNumber()) {
        break label171;
      }
      shakeField(0);
      bool = false;
    }
    for (;;)
    {
      return bool;
      localObject2 = null;
      localObject1 = null;
      break;
      label171:
      if ((!((Card)localObject2).validateExpMonth()) || (!((Card)localObject2).validateExpYear()) || (!((Card)localObject2).validateExpiryDate()))
      {
        shakeField(1);
        bool = false;
        continue;
      }
      if ((this.need_card_name) && (this.inputFields[2].length() == 0))
      {
        shakeField(2);
        bool = false;
        continue;
      }
      if (!((Card)localObject2).validateCVC())
      {
        shakeField(3);
        bool = false;
        continue;
      }
      if ((this.need_card_country) && (this.inputFields[4].length() == 0))
      {
        shakeField(4);
        bool = false;
        continue;
      }
      if ((this.need_card_postcode) && (this.inputFields[5].length() == 0))
      {
        shakeField(5);
        bool = false;
        continue;
      }
      showEditDoneProgress(true, true);
      try
      {
        localObject1 = new com/stripe/android/Stripe;
        ((Stripe)localObject1).<init>(this.stripeApiKey);
        TokenCallback local33 = new org/telegram/ui/PaymentFormActivity$33;
        local33.<init>(this);
        ((Stripe)localObject1).createToken((Card)localObject2, local33);
        bool = true;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  private void sendData()
  {
    if (this.canceled) {
      return;
    }
    showEditDoneProgress(false, true);
    final TLRPC.TL_payments_sendPaymentForm localTL_payments_sendPaymentForm = new TLRPC.TL_payments_sendPaymentForm();
    localTL_payments_sendPaymentForm.msg_id = this.messageObject.getId();
    if ((UserConfig.getInstance(this.currentAccount).tmpPassword != null) && (this.paymentForm.saved_credentials != null))
    {
      localTL_payments_sendPaymentForm.credentials = new TLRPC.TL_inputPaymentCredentialsSaved();
      localTL_payments_sendPaymentForm.credentials.id = this.paymentForm.saved_credentials.id;
      localTL_payments_sendPaymentForm.credentials.tmp_password = UserConfig.getInstance(this.currentAccount).tmpPassword.tmp_password;
    }
    for (;;)
    {
      if ((this.requestedInfo != null) && (this.requestedInfo.id != null))
      {
        localTL_payments_sendPaymentForm.requested_info_id = this.requestedInfo.id;
        localTL_payments_sendPaymentForm.flags |= 0x1;
      }
      if (this.shippingOption != null)
      {
        localTL_payments_sendPaymentForm.shipping_option_id = this.shippingOption.id;
        localTL_payments_sendPaymentForm.flags |= 0x2;
      }
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_payments_sendPaymentForm, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null) {
            if ((paramAnonymousTLObject instanceof TLRPC.TL_payments_paymentResult))
            {
              MessagesController.getInstance(PaymentFormActivity.this.currentAccount).processUpdates(((TLRPC.TL_payments_paymentResult)paramAnonymousTLObject).updates, false);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  PaymentFormActivity.this.goToNextStep();
                }
              });
            }
          }
          for (;;)
          {
            return;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_payments_paymentVerficationNeeded))
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance(PaymentFormActivity.this.currentAccount).postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
                  PaymentFormActivity.this.setDonePressed(false);
                  PaymentFormActivity.this.webView.setVisibility(0);
                  PaymentFormActivity.access$2402(PaymentFormActivity.this, true);
                  PaymentFormActivity.this.showEditDoneProgress(true, true);
                  PaymentFormActivity.this.progressView.setVisibility(0);
                  PaymentFormActivity.this.doneItem.setEnabled(false);
                  PaymentFormActivity.this.doneItem.getImageView().setVisibility(4);
                  PaymentFormActivity.this.webView.loadUrl(((TLRPC.TL_payments_paymentVerficationNeeded)paramAnonymousTLObject).url);
                }
              });
              continue;
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  AlertsCreator.processError(PaymentFormActivity.this.currentAccount, paramAnonymousTL_error, PaymentFormActivity.this, PaymentFormActivity.35.this.val$req, new Object[0]);
                  PaymentFormActivity.this.setDonePressed(false);
                  PaymentFormActivity.this.showEditDoneProgress(false, false);
                }
              });
            }
          }
        }
      }, 2);
      break;
      if (this.androidPayCredentials != null)
      {
        localTL_payments_sendPaymentForm.credentials = this.androidPayCredentials;
      }
      else
      {
        localTL_payments_sendPaymentForm.credentials = new TLRPC.TL_inputPaymentCredentials();
        localTL_payments_sendPaymentForm.credentials.save = this.saveCardInfo;
        localTL_payments_sendPaymentForm.credentials.data = new TLRPC.TL_dataJSON();
        localTL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
      }
    }
  }
  
  private void sendForm()
  {
    if (this.canceled) {
      return;
    }
    showEditDoneProgress(true, true);
    this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
    this.validateRequest.save = this.saveShippingInfo;
    this.validateRequest.msg_id = this.messageObject.getId();
    this.validateRequest.info = new TLRPC.TL_paymentRequestedInfo();
    if (this.paymentForm.invoice.name_requested)
    {
      this.validateRequest.info.name = this.inputFields[6].getText().toString();
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 0x1;
    }
    if (this.paymentForm.invoice.phone_requested)
    {
      this.validateRequest.info.phone = ("+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString());
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 0x2;
    }
    if (this.paymentForm.invoice.email_requested)
    {
      this.validateRequest.info.email = this.inputFields[7].getText().toString().trim();
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 0x4;
    }
    TLRPC.TL_postAddress localTL_postAddress;
    if (this.paymentForm.invoice.shipping_address_requested)
    {
      this.validateRequest.info.shipping_address = new TLRPC.TL_postAddress();
      this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
      this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
      this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
      this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
      localTL_postAddress = this.validateRequest.info.shipping_address;
      if (this.countryName == null) {
        break label499;
      }
    }
    label499:
    for (final Object localObject = this.countryName;; localObject = "")
    {
      localTL_postAddress.country_iso2 = ((String)localObject);
      this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 0x8;
      localObject = this.validateRequest;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(this.validateRequest, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if ((paramAnonymousTLObject instanceof TLRPC.TL_payments_validatedRequestedInfo)) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                PaymentFormActivity.access$902(PaymentFormActivity.this, (TLRPC.TL_payments_validatedRequestedInfo)paramAnonymousTLObject);
                if ((PaymentFormActivity.this.paymentForm.saved_info != null) && (!PaymentFormActivity.this.saveShippingInfo))
                {
                  TLRPC.TL_payments_clearSavedInfo localTL_payments_clearSavedInfo = new TLRPC.TL_payments_clearSavedInfo();
                  localTL_payments_clearSavedInfo.info = true;
                  ConnectionsManager.getInstance(PaymentFormActivity.this.currentAccount).sendRequest(localTL_payments_clearSavedInfo, new RequestDelegate()
                  {
                    public void run(TLObject paramAnonymous3TLObject, TLRPC.TL_error paramAnonymous3TL_error) {}
                  });
                }
                PaymentFormActivity.this.goToNextStep();
                PaymentFormActivity.this.setDonePressed(false);
                PaymentFormActivity.this.showEditDoneProgress(true, false);
              }
            });
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                PaymentFormActivity.this.setDonePressed(false);
                PaymentFormActivity.this.showEditDoneProgress(true, false);
                String str;
                int i;
                if (paramAnonymousTL_error != null)
                {
                  str = paramAnonymousTL_error.text;
                  i = -1;
                  switch (str.hashCode())
                  {
                  default: 
                    switch (i)
                    {
                    default: 
                      AlertsCreator.processError(PaymentFormActivity.this.currentAccount, paramAnonymousTL_error, PaymentFormActivity.this, PaymentFormActivity.34.this.val$req, new Object[0]);
                    }
                    break;
                  }
                }
                for (;;)
                {
                  return;
                  if (!str.equals("REQ_INFO_NAME_INVALID")) {
                    break;
                  }
                  i = 0;
                  break;
                  if (!str.equals("REQ_INFO_PHONE_INVALID")) {
                    break;
                  }
                  i = 1;
                  break;
                  if (!str.equals("REQ_INFO_EMAIL_INVALID")) {
                    break;
                  }
                  i = 2;
                  break;
                  if (!str.equals("ADDRESS_COUNTRY_INVALID")) {
                    break;
                  }
                  i = 3;
                  break;
                  if (!str.equals("ADDRESS_CITY_INVALID")) {
                    break;
                  }
                  i = 4;
                  break;
                  if (!str.equals("ADDRESS_POSTCODE_INVALID")) {
                    break;
                  }
                  i = 5;
                  break;
                  if (!str.equals("ADDRESS_STATE_INVALID")) {
                    break;
                  }
                  i = 6;
                  break;
                  if (!str.equals("ADDRESS_STREET_LINE1_INVALID")) {
                    break;
                  }
                  i = 7;
                  break;
                  if (!str.equals("ADDRESS_STREET_LINE2_INVALID")) {
                    break;
                  }
                  i = 8;
                  break;
                  PaymentFormActivity.this.shakeField(6);
                  continue;
                  PaymentFormActivity.this.shakeField(9);
                  continue;
                  PaymentFormActivity.this.shakeField(7);
                  continue;
                  PaymentFormActivity.this.shakeField(4);
                  continue;
                  PaymentFormActivity.this.shakeField(2);
                  continue;
                  PaymentFormActivity.this.shakeField(5);
                  continue;
                  PaymentFormActivity.this.shakeField(3);
                  continue;
                  PaymentFormActivity.this.shakeField(0);
                  continue;
                  PaymentFormActivity.this.shakeField(1);
                }
              }
            });
          }
        }
      }, 2);
      break;
    }
  }
  
  private void sendSavePassword(final boolean paramBoolean)
  {
    TLRPC.TL_account_updatePasswordSettings localTL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
    final String str1;
    if (paramBoolean)
    {
      this.doneItem.setVisibility(0);
      str1 = null;
      localTL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
      localTL_account_updatePasswordSettings.new_settings.flags = 2;
      localTL_account_updatePasswordSettings.new_settings.email = "";
      localTL_account_updatePasswordSettings.current_password_hash = new byte[0];
    }
    for (;;)
    {
      showEditDoneProgress(true, true);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_updatePasswordSettings, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              PaymentFormActivity.this.showEditDoneProgress(true, false);
              if (PaymentFormActivity.32.this.val$clear)
              {
                PaymentFormActivity.access$4302(PaymentFormActivity.this, new TLRPC.TL_account_noPassword());
                PaymentFormActivity.this.delegate.currentPasswordUpdated(PaymentFormActivity.this.currentPassword);
                PaymentFormActivity.this.finishFragment();
              }
              for (;;)
              {
                return;
                if ((paramAnonymousTL_error == null) && ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue)))
                {
                  if (PaymentFormActivity.this.getParentActivity() != null) {
                    PaymentFormActivity.this.goToNextStep();
                  }
                }
                else if (paramAnonymousTL_error != null)
                {
                  Object localObject;
                  if (paramAnonymousTL_error.text.equals("EMAIL_UNCONFIRMED"))
                  {
                    localObject = new AlertDialog.Builder(PaymentFormActivity.this.getParentActivity());
                    ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                      {
                        PaymentFormActivity.access$5302(PaymentFormActivity.this, true);
                        PaymentFormActivity.this.currentPassword.email_unconfirmed_pattern = PaymentFormActivity.32.this.val$email;
                        PaymentFormActivity.this.updatePasswordFields();
                      }
                    });
                    ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    localObject = PaymentFormActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
                    if (localObject != null)
                    {
                      ((Dialog)localObject).setCanceledOnTouchOutside(false);
                      ((Dialog)localObject).setCancelable(false);
                    }
                  }
                  else if (paramAnonymousTL_error.text.equals("EMAIL_INVALID"))
                  {
                    PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                  }
                  else
                  {
                    if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                    {
                      int i = Utilities.parseInt(paramAnonymousTL_error.text).intValue();
                      if (i < 60) {}
                      for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                      {
                        PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, new Object[] { localObject }));
                        break;
                      }
                    }
                    PaymentFormActivity.this.showAlertWithText(LocaleController.getString("AppName", NUM), paramAnonymousTL_error.text);
                  }
                }
              }
            }
          });
        }
      }, 10);
      String str2;
      for (;;)
      {
        return;
        localObject2 = this.inputFields[0].getText().toString();
        if (TextUtils.isEmpty((CharSequence)localObject2))
        {
          shakeField(0);
        }
        else if (!((String)localObject2).equals(this.inputFields[1].getText().toString()))
        {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
            shakeField(1);
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              FileLog.e(localException1);
            }
          }
        }
        else
        {
          str2 = this.inputFields[2].getText().toString();
          if (str2.length() < 3)
          {
            shakeField(2);
          }
          else
          {
            int i = str2.lastIndexOf('.');
            int j = str2.lastIndexOf('@');
            if ((i >= 0) && (j >= 0) && (i >= j)) {
              break;
            }
            shakeField(2);
          }
        }
      }
      localTL_account_updatePasswordSettings.current_password_hash = new byte[0];
      localTL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
      Object localObject1 = null;
      try
      {
        localObject2 = ((String)localObject2).getBytes("UTF-8");
        localObject1 = localObject2;
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          byte[] arrayOfByte;
          FileLog.e(localException2);
        }
      }
      Object localObject2 = this.currentPassword.new_salt;
      arrayOfByte = new byte[localObject2.length * 2 + localObject1.length];
      System.arraycopy(localObject2, 0, arrayOfByte, 0, localObject2.length);
      System.arraycopy(localObject1, 0, arrayOfByte, localObject2.length, localObject1.length);
      System.arraycopy(localObject2, 0, arrayOfByte, arrayOfByte.length - localObject2.length, localObject2.length);
      localObject1 = localTL_account_updatePasswordSettings.new_settings;
      ((TLRPC.TL_account_passwordInputSettings)localObject1).flags |= 0x1;
      localTL_account_updatePasswordSettings.new_settings.hint = "";
      localTL_account_updatePasswordSettings.new_settings.new_password_hash = Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length);
      localTL_account_updatePasswordSettings.new_settings.new_salt = ((byte[])localObject2);
      localObject1 = str2;
      if (str2.length() > 0)
      {
        localObject1 = localTL_account_updatePasswordSettings.new_settings;
        ((TLRPC.TL_account_passwordInputSettings)localObject1).flags |= 0x2;
        localTL_account_updatePasswordSettings.new_settings.email = str2.trim();
        localObject1 = str2;
      }
    }
  }
  
  private void setCurrentPassword(TLRPC.account_Password paramaccount_Password)
  {
    if ((paramaccount_Password instanceof TLRPC.TL_account_password))
    {
      if (getParentActivity() == null) {}
      for (;;)
      {
        return;
        goToNextStep();
      }
    }
    this.currentPassword = paramaccount_Password;
    if (this.currentPassword != null) {
      if (this.currentPassword.email_unconfirmed_pattern.length() <= 0) {
        break label61;
      }
    }
    label61:
    for (boolean bool = true;; bool = false)
    {
      this.waitingForEmail = bool;
      updatePasswordFields();
      break;
    }
  }
  
  private void setDelegate(PaymentFormActivityDelegate paramPaymentFormActivityDelegate)
  {
    this.delegate = paramPaymentFormActivityDelegate;
  }
  
  private void setDonePressed(boolean paramBoolean)
  {
    boolean bool = true;
    this.donePressed = paramBoolean;
    Object localObject;
    if (!paramBoolean)
    {
      paramBoolean = true;
      this.swipeBackEnabled = paramBoolean;
      localObject = this.actionBar.getBackButton();
      if (this.donePressed) {
        break label76;
      }
      paramBoolean = true;
      label35:
      ((View)localObject).setEnabled(paramBoolean);
      if (this.detailSettingsCell[0] != null)
      {
        localObject = this.detailSettingsCell[0];
        if (this.donePressed) {
          break label81;
        }
      }
    }
    label76:
    label81:
    for (paramBoolean = bool;; paramBoolean = false)
    {
      ((TextDetailSettingsCell)localObject).setEnabled(paramBoolean);
      return;
      paramBoolean = false;
      break;
      paramBoolean = false;
      break label35;
    }
  }
  
  private void shakeField(int paramInt)
  {
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    AndroidUtilities.shakeView(this.inputFields[paramInt], 2.0F, 0);
  }
  
  private void showAlertWithText(String paramString1, String paramString2)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    showDialog(localBuilder.create());
  }
  
  private void showAndroidPay()
  {
    if ((getParentActivity() == null) || (this.androidPayContainer == null)) {
      return;
    }
    Object localObject1 = WalletFragmentOptions.newBuilder();
    int i;
    if (this.paymentForm.invoice.test)
    {
      i = 3;
      label34:
      ((WalletFragmentOptions.Builder)localObject1).setEnvironment(i);
      ((WalletFragmentOptions.Builder)localObject1).setMode(1);
      if (this.androidPayPublicKey == null) {
        break label349;
      }
      this.androidPayContainer.setBackgroundColor(this.androidPayBackgroundColor);
      localObject2 = new WalletFragmentStyle().setBuyButtonText(5);
      if (!this.androidPayBlackTheme) {
        break label344;
      }
      i = 6;
      label86:
      localObject2 = ((WalletFragmentStyle)localObject2).setBuyButtonAppearance(i).setBuyButtonWidth(-1);
      label96:
      ((WalletFragmentOptions.Builder)localObject1).setFragmentStyle((WalletFragmentStyle)localObject2);
      localObject1 = WalletFragment.newInstance(((WalletFragmentOptions.Builder)localObject1).build());
      localObject2 = getParentActivity().getFragmentManager().beginTransaction();
      ((FragmentTransaction)localObject2).replace(4000, (Fragment)localObject1);
      ((FragmentTransaction)localObject2).commit();
      localObject2 = new ArrayList();
      ((ArrayList)localObject2).addAll(this.paymentForm.invoice.prices);
      if (this.shippingOption != null) {
        ((ArrayList)localObject2).addAll(this.shippingOption.prices);
      }
      this.totalPriceDecimal = getTotalPriceDecimalString((ArrayList)localObject2);
      if (this.androidPayPublicKey == null) {
        break label375;
      }
    }
    label344:
    label349:
    label375:
    for (Object localObject2 = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(2).addParameter("publicKey", this.androidPayPublicKey).build();; localObject2 = PaymentMethodTokenizationParameters.newBuilder().setPaymentMethodTokenizationType(1).addParameter("gateway", "stripe").addParameter("stripe:publishableKey", this.stripeApiKey).addParameter("stripe:version", "3.5.0").build())
    {
      localObject2 = MaskedWalletRequest.newBuilder().setPaymentMethodTokenizationParameters((PaymentMethodTokenizationParameters)localObject2).setEstimatedTotalPrice(this.totalPriceDecimal).setCurrencyCode(this.paymentForm.invoice.currency).build();
      ((WalletFragment)localObject1).initialize(WalletFragmentInitParams.newBuilder().setMaskedWalletRequest((MaskedWalletRequest)localObject2).setMaskedWalletRequestCode(1000).build());
      this.androidPayContainer.setVisibility(0);
      localObject2 = new AnimatorSet();
      ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.androidPayContainer, "alpha", new float[] { 0.0F, 1.0F }) });
      ((AnimatorSet)localObject2).setInterpolator(new DecelerateInterpolator());
      ((AnimatorSet)localObject2).setDuration(180L);
      ((AnimatorSet)localObject2).start();
      break;
      i = 1;
      break label34;
      i = 4;
      break label86;
      localObject2 = new WalletFragmentStyle().setBuyButtonText(6).setBuyButtonAppearance(6).setBuyButtonWidth(-2);
      break label96;
    }
  }
  
  private void showEditDoneProgress(boolean paramBoolean1, final boolean paramBoolean2)
  {
    if (this.doneItemAnimation != null) {
      this.doneItemAnimation.cancel();
    }
    if ((paramBoolean1) && (this.doneItem != null))
    {
      this.doneItemAnimation = new AnimatorSet();
      if (paramBoolean2)
      {
        this.progressView.setVisibility(0);
        this.doneItem.setEnabled(false);
        this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 1.0F }) });
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnonymousAnimator))) {
              PaymentFormActivity.access$7002(PaymentFormActivity.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnonymousAnimator)))
            {
              if (paramBoolean2) {
                break label43;
              }
              PaymentFormActivity.this.progressView.setVisibility(4);
            }
            for (;;)
            {
              return;
              label43:
              PaymentFormActivity.this.doneItem.getImageView().setVisibility(4);
            }
          }
        });
        this.doneItemAnimation.setDuration(150L);
        this.doneItemAnimation.start();
      }
    }
    while (this.payTextView == null) {
      for (;;)
      {
        return;
        if (this.webView != null)
        {
          this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }) });
        }
        else
        {
          this.doneItem.getImageView().setVisibility(0);
          this.doneItem.setEnabled(true);
          this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 1.0F }) });
        }
      }
    }
    this.doneItemAnimation = new AnimatorSet();
    if (paramBoolean2)
    {
      this.progressViewButton.setVisibility(0);
      this.bottomLayout.setEnabled(false);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[] { 1.0F }) });
    }
    for (;;)
    {
      this.doneItemAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnonymousAnimator))) {
            PaymentFormActivity.access$7002(PaymentFormActivity.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnonymousAnimator)))
          {
            if (paramBoolean2) {
              break label43;
            }
            PaymentFormActivity.this.progressViewButton.setVisibility(4);
          }
          for (;;)
          {
            return;
            label43:
            PaymentFormActivity.this.payTextView.setVisibility(4);
          }
        }
      });
      this.doneItemAnimation.setDuration(150L);
      this.doneItemAnimation.start();
      break;
      this.payTextView.setVisibility(0);
      this.bottomLayout.setEnabled(true);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[] { 1.0F }) });
    }
  }
  
  private void showPayAlert(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("PaymentTransactionReview", NUM));
    localBuilder.setMessage(LocaleController.formatString("PaymentTransactionMessage", NUM, new Object[] { paramString, this.currentBotName, this.currentItemName }));
    localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        PaymentFormActivity.this.setDonePressed(true);
        PaymentFormActivity.this.sendData();
      }
    });
    localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
    showDialog(localBuilder.create());
  }
  
  private void updatePasswordFields()
  {
    if ((this.currentStep != 6) || (this.bottomCell[2] == null)) {}
    for (;;)
    {
      return;
      int i;
      if (this.currentPassword == null)
      {
        this.doneItem.setVisibility(0);
        showEditDoneProgress(true, true);
        this.bottomCell[2].setVisibility(8);
        this.settingsCell1.setVisibility(8);
        this.headerCell[0].setVisibility(8);
        this.headerCell[1].setVisibility(8);
        this.bottomCell[0].setVisibility(8);
        for (i = 0; i < 3; i++) {
          ((View)this.inputFields[i].getParent()).setVisibility(8);
        }
        for (i = 0; i < this.dividers.size(); i++) {
          ((View)this.dividers.get(i)).setVisibility(8);
        }
      }
      else
      {
        showEditDoneProgress(true, false);
        if (this.waitingForEmail)
        {
          if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
          }
          this.doneItem.setVisibility(8);
          this.bottomCell[2].setText(LocaleController.formatString("EmailPasswordConfirmText", NUM, new Object[] { this.currentPassword.email_unconfirmed_pattern }));
          this.bottomCell[2].setVisibility(0);
          this.settingsCell1.setVisibility(0);
          this.bottomCell[1].setText("");
          this.headerCell[0].setVisibility(8);
          this.headerCell[1].setVisibility(8);
          this.bottomCell[0].setVisibility(8);
          for (i = 0; i < 3; i++) {
            ((View)this.inputFields[i].getParent()).setVisibility(8);
          }
          for (i = 0; i < this.dividers.size(); i++) {
            ((View)this.dividers.get(i)).setVisibility(8);
          }
        }
        else
        {
          this.doneItem.setVisibility(0);
          this.bottomCell[2].setVisibility(8);
          this.settingsCell1.setVisibility(8);
          this.bottomCell[1].setText(LocaleController.getString("PaymentPasswordEmailInfo", NUM));
          this.headerCell[0].setVisibility(0);
          this.headerCell[1].setVisibility(0);
          this.bottomCell[0].setVisibility(0);
          for (i = 0; i < 3; i++) {
            ((View)this.inputFields[i].getParent()).setVisibility(0);
          }
          for (i = 0; i < this.dividers.size(); i++) {
            ((View)this.dividers.get(i)).setVisibility(0);
          }
        }
      }
    }
  }
  
  private void updateSavePaymentField()
  {
    if ((this.bottomCell[0] == null) || (this.sectionCell[2] == null)) {}
    for (;;)
    {
      return;
      if (((this.paymentForm.password_missing) || (this.paymentForm.can_save_credentials)) && ((this.webView == null) || ((this.webView != null) && (!this.webviewLoading))))
      {
        SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", NUM));
        if (this.paymentForm.password_missing)
        {
          loadPasswordInfo();
          localSpannableStringBuilder.append("\n");
          int i = localSpannableStringBuilder.length();
          String str = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", NUM);
          int j = str.indexOf('*');
          int k = str.lastIndexOf('*');
          localSpannableStringBuilder.append(str);
          if ((j != -1) && (k != -1))
          {
            j += i;
            i = k + i;
            this.bottomCell[0].getTextView().setMovementMethod(new LinkMovementMethodMy(null));
            localSpannableStringBuilder.replace(i, i + 1, "");
            localSpannableStringBuilder.replace(j, j + 1, "");
            localSpannableStringBuilder.setSpan(new LinkSpan(), j, i - 1, 33);
          }
        }
        this.checkCell1.setEnabled(true);
        this.bottomCell[0].setText(localSpannableStringBuilder);
        this.checkCell1.setVisibility(0);
        this.bottomCell[0].setVisibility(0);
        this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), NUM, "windowBackgroundGrayShadow"));
      }
      else
      {
        this.checkCell1.setVisibility(8);
        this.bottomCell[0].setVisibility(8);
        this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), NUM, "windowBackgroundGrayShadow"));
      }
    }
  }
  
  /* Error */
  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public View createView(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   4: ifne +1220 -> 1224
    //   7: aload_0
    //   8: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   11: ldc_w 1569
    //   14: ldc_w 1570
    //   17: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   20: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   23: aload_0
    //   24: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   27: ldc_w 1573
    //   30: invokevirtual 1576	org/telegram/ui/ActionBar/ActionBar:setBackButtonImage	(I)V
    //   33: aload_0
    //   34: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   37: iconst_1
    //   38: invokevirtual 1579	org/telegram/ui/ActionBar/ActionBar:setAllowOverlayTitle	(Z)V
    //   41: aload_0
    //   42: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   45: new 8	org/telegram/ui/PaymentFormActivity$1
    //   48: dup
    //   49: aload_0
    //   50: invokespecial 1580	org/telegram/ui/PaymentFormActivity$1:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   53: invokevirtual 1584	org/telegram/ui/ActionBar/ActionBar:setActionBarMenuOnItemClick	(Lorg/telegram/ui/ActionBar/ActionBar$ActionBarMenuOnItemClick;)V
    //   56: aload_0
    //   57: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   60: invokevirtual 1588	org/telegram/ui/ActionBar/ActionBar:createMenu	()Lorg/telegram/ui/ActionBar/ActionBarMenu;
    //   63: astore_2
    //   64: aload_0
    //   65: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   68: ifeq +44 -> 112
    //   71: aload_0
    //   72: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   75: iconst_1
    //   76: if_icmpeq +36 -> 112
    //   79: aload_0
    //   80: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   83: iconst_2
    //   84: if_icmpeq +28 -> 112
    //   87: aload_0
    //   88: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   91: iconst_3
    //   92: if_icmpeq +20 -> 112
    //   95: aload_0
    //   96: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   99: iconst_4
    //   100: if_icmpeq +12 -> 112
    //   103: aload_0
    //   104: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   107: bipush 6
    //   109: if_icmpne +60 -> 169
    //   112: aload_0
    //   113: aload_2
    //   114: iconst_1
    //   115: ldc_w 1589
    //   118: ldc_w 1590
    //   121: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   124: invokevirtual 1600	org/telegram/ui/ActionBar/ActionBarMenu:addItemWithWidth	(III)Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   127: putfield 517	org/telegram/ui/PaymentFormActivity:doneItem	Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   130: aload_0
    //   131: new 1423	org/telegram/ui/Components/ContextProgressView
    //   134: dup
    //   135: aload_1
    //   136: iconst_1
    //   137: invokespecial 1603	org/telegram/ui/Components/ContextProgressView:<init>	(Landroid/content/Context;I)V
    //   140: putfield 673	org/telegram/ui/PaymentFormActivity:progressView	Lorg/telegram/ui/Components/ContextProgressView;
    //   143: aload_0
    //   144: getfield 517	org/telegram/ui/PaymentFormActivity:doneItem	Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   147: aload_0
    //   148: getfield 673	org/telegram/ui/PaymentFormActivity:progressView	Lorg/telegram/ui/Components/ContextProgressView;
    //   151: iconst_m1
    //   152: ldc_w 1604
    //   155: invokestatic 1610	org/telegram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   158: invokevirtual 1614	org/telegram/ui/ActionBar/ActionBarMenuItem:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   161: aload_0
    //   162: getfield 673	org/telegram/ui/PaymentFormActivity:progressView	Lorg/telegram/ui/Components/ContextProgressView;
    //   165: iconst_4
    //   166: invokevirtual 1424	org/telegram/ui/Components/ContextProgressView:setVisibility	(I)V
    //   169: aload_0
    //   170: new 1251	android/widget/FrameLayout
    //   173: dup
    //   174: aload_1
    //   175: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   178: putfield 1619	org/telegram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   181: aload_0
    //   182: getfield 1619	org/telegram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   185: checkcast 1251	android/widget/FrameLayout
    //   188: astore_3
    //   189: aload_0
    //   190: getfield 1619	org/telegram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   193: ldc_w 1621
    //   196: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   199: invokevirtual 1626	android/view/View:setBackgroundColor	(I)V
    //   202: aload_0
    //   203: new 1628	android/widget/ScrollView
    //   206: dup
    //   207: aload_1
    //   208: invokespecial 1629	android/widget/ScrollView:<init>	(Landroid/content/Context;)V
    //   211: putfield 1631	org/telegram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   214: aload_0
    //   215: getfield 1631	org/telegram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   218: iconst_1
    //   219: invokevirtual 1634	android/widget/ScrollView:setFillViewport	(Z)V
    //   222: aload_0
    //   223: getfield 1631	org/telegram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   226: ldc_w 1636
    //   229: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   232: invokestatic 1640	org/telegram/messenger/AndroidUtilities:setScrollViewEdgeEffectColor	(Landroid/widget/ScrollView;I)V
    //   235: aload_0
    //   236: getfield 1631	org/telegram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   239: astore_2
    //   240: aload_0
    //   241: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   244: iconst_4
    //   245: if_icmpne +1244 -> 1489
    //   248: ldc_w 1641
    //   251: fstore 4
    //   253: aload_3
    //   254: aload_2
    //   255: iconst_m1
    //   256: ldc_w 1604
    //   259: bipush 51
    //   261: fconst_0
    //   262: fconst_0
    //   263: fconst_0
    //   264: fload 4
    //   266: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   269: invokevirtual 1645	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   272: aload_0
    //   273: new 1647	android/widget/LinearLayout
    //   276: dup
    //   277: aload_1
    //   278: invokespecial 1648	android/widget/LinearLayout:<init>	(Landroid/content/Context;)V
    //   281: putfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   284: aload_0
    //   285: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   288: iconst_1
    //   289: invokevirtual 1653	android/widget/LinearLayout:setOrientation	(I)V
    //   292: aload_0
    //   293: getfield 1631	org/telegram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   296: aload_0
    //   297: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   300: new 1655	android/widget/FrameLayout$LayoutParams
    //   303: dup
    //   304: iconst_m1
    //   305: bipush -2
    //   307: invokespecial 1658	android/widget/FrameLayout$LayoutParams:<init>	(II)V
    //   310: invokevirtual 1659	android/widget/ScrollView:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   313: aload_0
    //   314: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   317: ifne +3443 -> 3760
    //   320: new 289	java/util/HashMap
    //   323: dup
    //   324: invokespecial 290	java/util/HashMap:<init>	()V
    //   327: astore_3
    //   328: new 289	java/util/HashMap
    //   331: dup
    //   332: invokespecial 290	java/util/HashMap:<init>	()V
    //   335: astore 5
    //   337: new 1661	java/io/BufferedReader
    //   340: astore_2
    //   341: new 1663	java/io/InputStreamReader
    //   344: astore 6
    //   346: aload 6
    //   348: aload_1
    //   349: invokevirtual 1667	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   352: invokevirtual 1673	android/content/res/Resources:getAssets	()Landroid/content/res/AssetManager;
    //   355: ldc_w 1675
    //   358: invokevirtual 1681	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   361: invokespecial 1684	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   364: aload_2
    //   365: aload 6
    //   367: invokespecial 1687	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   370: aload_2
    //   371: invokevirtual 1690	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   374: astore 6
    //   376: aload 6
    //   378: ifnull +1117 -> 1495
    //   381: aload 6
    //   383: ldc_w 1692
    //   386: invokevirtual 977	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   389: astore 6
    //   391: aload_0
    //   392: getfield 287	org/telegram/ui/PaymentFormActivity:countriesArray	Ljava/util/ArrayList;
    //   395: iconst_0
    //   396: aload 6
    //   398: iconst_2
    //   399: aaload
    //   400: invokevirtual 1696	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   403: aload_0
    //   404: getfield 292	org/telegram/ui/PaymentFormActivity:countriesMap	Ljava/util/HashMap;
    //   407: aload 6
    //   409: iconst_2
    //   410: aaload
    //   411: aload 6
    //   413: iconst_0
    //   414: aaload
    //   415: invokevirtual 1700	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   418: pop
    //   419: aload_0
    //   420: getfield 294	org/telegram/ui/PaymentFormActivity:codesMap	Ljava/util/HashMap;
    //   423: aload 6
    //   425: iconst_0
    //   426: aaload
    //   427: aload 6
    //   429: iconst_2
    //   430: aaload
    //   431: invokevirtual 1700	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   434: pop
    //   435: aload 5
    //   437: aload 6
    //   439: iconst_1
    //   440: aaload
    //   441: aload 6
    //   443: iconst_2
    //   444: aaload
    //   445: invokevirtual 1700	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   448: pop
    //   449: aload 6
    //   451: arraylength
    //   452: iconst_3
    //   453: if_icmple +19 -> 472
    //   456: aload_0
    //   457: getfield 296	org/telegram/ui/PaymentFormActivity:phoneFormatMap	Ljava/util/HashMap;
    //   460: aload 6
    //   462: iconst_0
    //   463: aaload
    //   464: aload 6
    //   466: iconst_3
    //   467: aaload
    //   468: invokevirtual 1700	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   471: pop
    //   472: aload_3
    //   473: aload 6
    //   475: iconst_1
    //   476: aaload
    //   477: aload 6
    //   479: iconst_2
    //   480: aaload
    //   481: invokevirtual 1700	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   484: pop
    //   485: goto -115 -> 370
    //   488: astore_2
    //   489: aload_2
    //   490: invokestatic 1032	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   493: aload_0
    //   494: getfield 287	org/telegram/ui/PaymentFormActivity:countriesArray	Ljava/util/ArrayList;
    //   497: new 32	org/telegram/ui/PaymentFormActivity$2
    //   500: dup
    //   501: aload_0
    //   502: invokespecial 1701	org/telegram/ui/PaymentFormActivity$2:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   505: invokestatic 1707	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   508: aload_0
    //   509: bipush 10
    //   511: anewarray 708	org/telegram/ui/Components/EditTextBoldCursor
    //   514: putfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   517: iconst_0
    //   518: istore 7
    //   520: iload 7
    //   522: bipush 10
    //   524: if_icmpge +2503 -> 3027
    //   527: iload 7
    //   529: ifne +973 -> 1502
    //   532: aload_0
    //   533: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   536: iconst_0
    //   537: new 298	org/telegram/ui/Cells/HeaderCell
    //   540: dup
    //   541: aload_1
    //   542: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   545: aastore
    //   546: aload_0
    //   547: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   550: iconst_0
    //   551: aaload
    //   552: ldc_w 1710
    //   555: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   558: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   561: aload_0
    //   562: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   565: iconst_0
    //   566: aaload
    //   567: ldc_w 1713
    //   570: ldc_w 1714
    //   573: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   576: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   579: aload_0
    //   580: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   583: aload_0
    //   584: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   587: iconst_0
    //   588: aaload
    //   589: iconst_m1
    //   590: bipush -2
    //   592: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   595: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   598: iload 7
    //   600: bipush 8
    //   602: if_icmpne +1009 -> 1611
    //   605: new 1647	android/widget/LinearLayout
    //   608: dup
    //   609: aload_1
    //   610: invokespecial 1648	android/widget/LinearLayout:<init>	(Landroid/content/Context;)V
    //   613: astore_2
    //   614: aload_2
    //   615: checkcast 1647	android/widget/LinearLayout
    //   618: iconst_0
    //   619: invokevirtual 1653	android/widget/LinearLayout:setOrientation	(I)V
    //   622: aload_0
    //   623: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   626: aload_2
    //   627: iconst_m1
    //   628: bipush 48
    //   630: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   633: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   636: aload_2
    //   637: ldc_w 1710
    //   640: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   643: invokevirtual 1724	android/view/ViewGroup:setBackgroundColor	(I)V
    //   646: iload 7
    //   648: bipush 9
    //   650: if_icmpne +1185 -> 1835
    //   653: aload_0
    //   654: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   657: iload 7
    //   659: new 1726	org/telegram/ui/Components/HintEditText
    //   662: dup
    //   663: aload_1
    //   664: invokespecial 1727	org/telegram/ui/Components/HintEditText:<init>	(Landroid/content/Context;)V
    //   667: aastore
    //   668: aload_0
    //   669: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   672: iload 7
    //   674: aaload
    //   675: iload 7
    //   677: invokestatic 362	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   680: invokevirtual 1731	org/telegram/ui/Components/EditTextBoldCursor:setTag	(Ljava/lang/Object;)V
    //   683: aload_0
    //   684: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   687: iload 7
    //   689: aaload
    //   690: iconst_1
    //   691: ldc_w 1732
    //   694: invokevirtual 1736	org/telegram/ui/Components/EditTextBoldCursor:setTextSize	(IF)V
    //   697: aload_0
    //   698: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   701: iload 7
    //   703: aaload
    //   704: ldc_w 1738
    //   707: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   710: invokevirtual 1741	org/telegram/ui/Components/EditTextBoldCursor:setHintTextColor	(I)V
    //   713: aload_0
    //   714: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   717: iload 7
    //   719: aaload
    //   720: ldc_w 1743
    //   723: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   726: invokevirtual 1746	org/telegram/ui/Components/EditTextBoldCursor:setTextColor	(I)V
    //   729: aload_0
    //   730: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   733: iload 7
    //   735: aaload
    //   736: aconst_null
    //   737: invokevirtual 1747	org/telegram/ui/Components/EditTextBoldCursor:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   740: aload_0
    //   741: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   744: iload 7
    //   746: aaload
    //   747: ldc_w 1743
    //   750: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   753: invokevirtual 1750	org/telegram/ui/Components/EditTextBoldCursor:setCursorColor	(I)V
    //   756: aload_0
    //   757: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   760: iload 7
    //   762: aaload
    //   763: ldc_w 1751
    //   766: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   769: invokevirtual 1754	org/telegram/ui/Components/EditTextBoldCursor:setCursorSize	(I)V
    //   772: aload_0
    //   773: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   776: iload 7
    //   778: aaload
    //   779: ldc_w 1755
    //   782: invokevirtual 1759	org/telegram/ui/Components/EditTextBoldCursor:setCursorWidth	(F)V
    //   785: iload 7
    //   787: iconst_4
    //   788: if_icmpne +32 -> 820
    //   791: aload_0
    //   792: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   795: iload 7
    //   797: aaload
    //   798: new 64	org/telegram/ui/PaymentFormActivity$3
    //   801: dup
    //   802: aload_0
    //   803: invokespecial 1760	org/telegram/ui/PaymentFormActivity$3:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   806: invokevirtual 1764	org/telegram/ui/Components/EditTextBoldCursor:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   809: aload_0
    //   810: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   813: iload 7
    //   815: aaload
    //   816: iconst_0
    //   817: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   820: iload 7
    //   822: bipush 9
    //   824: if_icmpeq +10 -> 834
    //   827: iload 7
    //   829: bipush 8
    //   831: if_icmpne +1022 -> 1853
    //   834: aload_0
    //   835: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   838: iload 7
    //   840: aaload
    //   841: iconst_3
    //   842: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   845: aload_0
    //   846: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   849: iload 7
    //   851: aaload
    //   852: ldc_w 1768
    //   855: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   858: iload 7
    //   860: tableswitch	default:+48->908, 0:+1160->2020, 1:+1228->2088, 2:+1296->2156, 3:+1364->2224, 4:+1432->2292, 5:+1547->2407, 6:+1030->1890, 7:+1095->1955
    //   908: aload_0
    //   909: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   912: iload 7
    //   914: aaload
    //   915: aload_0
    //   916: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   919: iload 7
    //   921: aaload
    //   922: invokevirtual 711	org/telegram/ui/Components/EditTextBoldCursor:length	()I
    //   925: invokevirtual 1774	org/telegram/ui/Components/EditTextBoldCursor:setSelection	(I)V
    //   928: iload 7
    //   930: bipush 8
    //   932: if_icmpne +1543 -> 2475
    //   935: aload_0
    //   936: new 1451	android/widget/TextView
    //   939: dup
    //   940: aload_1
    //   941: invokespecial 1775	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   944: putfield 1777	org/telegram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   947: aload_0
    //   948: getfield 1777	org/telegram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   951: ldc_w 775
    //   954: invokevirtual 1778	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   957: aload_0
    //   958: getfield 1777	org/telegram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   961: ldc_w 1743
    //   964: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   967: invokevirtual 1779	android/widget/TextView:setTextColor	(I)V
    //   970: aload_0
    //   971: getfield 1777	org/telegram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   974: iconst_1
    //   975: ldc_w 1732
    //   978: invokevirtual 1780	android/widget/TextView:setTextSize	(IF)V
    //   981: aload_2
    //   982: aload_0
    //   983: getfield 1777	org/telegram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   986: bipush -2
    //   988: bipush -2
    //   990: ldc_w 1781
    //   993: ldc_w 1782
    //   996: fconst_0
    //   997: ldc_w 1783
    //   1000: invokestatic 1786	org/telegram/ui/Components/LayoutHelper:createLinear	(IIFFFF)Landroid/widget/LinearLayout$LayoutParams;
    //   1003: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1006: aload_0
    //   1007: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1010: iload 7
    //   1012: aaload
    //   1013: ldc_w 1788
    //   1016: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1019: iconst_0
    //   1020: iconst_0
    //   1021: iconst_0
    //   1022: invokevirtual 1792	org/telegram/ui/Components/EditTextBoldCursor:setPadding	(IIII)V
    //   1025: aload_0
    //   1026: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1029: iload 7
    //   1031: aaload
    //   1032: bipush 19
    //   1034: invokevirtual 1795	org/telegram/ui/Components/EditTextBoldCursor:setGravity	(I)V
    //   1037: new 1797	android/text/InputFilter$LengthFilter
    //   1040: dup
    //   1041: iconst_5
    //   1042: invokespecial 1799	android/text/InputFilter$LengthFilter:<init>	(I)V
    //   1045: astore 6
    //   1047: aload_0
    //   1048: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1051: iload 7
    //   1053: aaload
    //   1054: iconst_1
    //   1055: anewarray 1801	android/text/InputFilter
    //   1058: dup
    //   1059: iconst_0
    //   1060: aload 6
    //   1062: aastore
    //   1063: invokevirtual 1805	org/telegram/ui/Components/EditTextBoldCursor:setFilters	([Landroid/text/InputFilter;)V
    //   1066: aload_2
    //   1067: aload_0
    //   1068: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1071: iload 7
    //   1073: aaload
    //   1074: bipush 55
    //   1076: bipush -2
    //   1078: fconst_0
    //   1079: ldc_w 1782
    //   1082: ldc_w 1732
    //   1085: ldc_w 1783
    //   1088: invokestatic 1786	org/telegram/ui/Components/LayoutHelper:createLinear	(IIFFFF)Landroid/widget/LinearLayout$LayoutParams;
    //   1091: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1094: aload_0
    //   1095: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1098: iload 7
    //   1100: aaload
    //   1101: new 110	org/telegram/ui/PaymentFormActivity$4
    //   1104: dup
    //   1105: aload_0
    //   1106: invokespecial 1806	org/telegram/ui/PaymentFormActivity$4:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   1109: invokevirtual 1810	org/telegram/ui/Components/EditTextBoldCursor:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   1112: aload_0
    //   1113: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1116: iload 7
    //   1118: aaload
    //   1119: new 114	org/telegram/ui/PaymentFormActivity$6
    //   1122: dup
    //   1123: aload_0
    //   1124: invokespecial 1811	org/telegram/ui/PaymentFormActivity$6:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   1127: invokevirtual 1815	org/telegram/ui/Components/EditTextBoldCursor:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   1130: iload 7
    //   1132: bipush 9
    //   1134: if_icmpne +1773 -> 2907
    //   1137: aload_0
    //   1138: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1141: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1144: getfield 1818	org/telegram/tgnet/TLRPC$TL_invoice:email_to_provider	Z
    //   1147: ifne +16 -> 1163
    //   1150: aload_0
    //   1151: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1154: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1157: getfield 1821	org/telegram/tgnet/TLRPC$TL_invoice:phone_to_provider	Z
    //   1160: ifeq +1831 -> 2991
    //   1163: aconst_null
    //   1164: astore_2
    //   1165: iconst_0
    //   1166: istore 8
    //   1168: iload 8
    //   1170: aload_0
    //   1171: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1174: getfield 340	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:users	Ljava/util/ArrayList;
    //   1177: invokevirtual 821	java/util/ArrayList:size	()I
    //   1180: if_icmpge +1461 -> 2641
    //   1183: aload_0
    //   1184: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1187: getfield 340	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:users	Ljava/util/ArrayList;
    //   1190: iload 8
    //   1192: invokevirtual 825	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1195: checkcast 370	org/telegram/tgnet/TLRPC$User
    //   1198: astore 6
    //   1200: aload 6
    //   1202: getfield 1823	org/telegram/tgnet/TLRPC$User:id	I
    //   1205: aload_0
    //   1206: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1209: getfield 336	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:provider_id	I
    //   1212: if_icmpne +6 -> 1218
    //   1215: aload 6
    //   1217: astore_2
    //   1218: iinc 8 1
    //   1221: goto -53 -> 1168
    //   1224: aload_0
    //   1225: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   1228: iconst_1
    //   1229: if_icmpne +22 -> 1251
    //   1232: aload_0
    //   1233: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1236: ldc_w 1825
    //   1239: ldc_w 1826
    //   1242: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1245: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1248: goto -1225 -> 23
    //   1251: aload_0
    //   1252: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   1255: iconst_2
    //   1256: if_icmpne +22 -> 1278
    //   1259: aload_0
    //   1260: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1263: ldc_w 1828
    //   1266: ldc_w 1829
    //   1269: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1272: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1275: goto -1252 -> 23
    //   1278: aload_0
    //   1279: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   1282: iconst_3
    //   1283: if_icmpne +22 -> 1305
    //   1286: aload_0
    //   1287: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1290: ldc_w 1828
    //   1293: ldc_w 1829
    //   1296: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1299: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1302: goto -1279 -> 23
    //   1305: aload_0
    //   1306: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   1309: iconst_4
    //   1310: if_icmpne +73 -> 1383
    //   1313: aload_0
    //   1314: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1317: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1320: getfield 926	org/telegram/tgnet/TLRPC$TL_invoice:test	Z
    //   1323: ifeq +41 -> 1364
    //   1326: aload_0
    //   1327: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1330: new 772	java/lang/StringBuilder
    //   1333: dup
    //   1334: invokespecial 773	java/lang/StringBuilder:<init>	()V
    //   1337: ldc_w 1831
    //   1340: invokevirtual 779	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1343: ldc_w 1833
    //   1346: ldc_w 1834
    //   1349: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1352: invokevirtual 779	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1355: invokevirtual 780	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1358: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1361: goto -1338 -> 23
    //   1364: aload_0
    //   1365: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1368: ldc_w 1833
    //   1371: ldc_w 1834
    //   1374: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1377: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1380: goto -1357 -> 23
    //   1383: aload_0
    //   1384: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   1387: iconst_5
    //   1388: if_icmpne +73 -> 1461
    //   1391: aload_0
    //   1392: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1395: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1398: getfield 926	org/telegram/tgnet/TLRPC$TL_invoice:test	Z
    //   1401: ifeq +41 -> 1442
    //   1404: aload_0
    //   1405: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1408: new 772	java/lang/StringBuilder
    //   1411: dup
    //   1412: invokespecial 773	java/lang/StringBuilder:<init>	()V
    //   1415: ldc_w 1831
    //   1418: invokevirtual 779	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1421: ldc_w 1836
    //   1424: ldc_w 1837
    //   1427: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1430: invokevirtual 779	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1433: invokevirtual 780	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1436: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1439: goto -1416 -> 23
    //   1442: aload_0
    //   1443: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1446: ldc_w 1836
    //   1449: ldc_w 1837
    //   1452: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1455: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1458: goto -1435 -> 23
    //   1461: aload_0
    //   1462: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   1465: bipush 6
    //   1467: if_icmpne -1444 -> 23
    //   1470: aload_0
    //   1471: getfield 1189	org/telegram/ui/PaymentFormActivity:actionBar	Lorg/telegram/ui/ActionBar/ActionBar;
    //   1474: ldc_w 1839
    //   1477: ldc_w 1840
    //   1480: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1483: invokevirtual 1572	org/telegram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1486: goto -1463 -> 23
    //   1489: fconst_0
    //   1490: fstore 4
    //   1492: goto -1239 -> 253
    //   1495: aload_2
    //   1496: invokevirtual 1843	java/io/BufferedReader:close	()V
    //   1499: goto -1006 -> 493
    //   1502: iload 7
    //   1504: bipush 6
    //   1506: if_icmpne -908 -> 598
    //   1509: aload_0
    //   1510: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   1513: iconst_0
    //   1514: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   1517: dup
    //   1518: aload_1
    //   1519: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   1522: aastore
    //   1523: aload_0
    //   1524: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1527: aload_0
    //   1528: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   1531: iconst_0
    //   1532: aaload
    //   1533: iconst_m1
    //   1534: bipush -2
    //   1536: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1539: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1542: aload_0
    //   1543: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   1546: iconst_1
    //   1547: new 298	org/telegram/ui/Cells/HeaderCell
    //   1550: dup
    //   1551: aload_1
    //   1552: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   1555: aastore
    //   1556: aload_0
    //   1557: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   1560: iconst_1
    //   1561: aaload
    //   1562: ldc_w 1710
    //   1565: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1568: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   1571: aload_0
    //   1572: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   1575: iconst_1
    //   1576: aaload
    //   1577: ldc_w 1846
    //   1580: ldc_w 1847
    //   1583: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1586: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   1589: aload_0
    //   1590: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1593: aload_0
    //   1594: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   1597: iconst_1
    //   1598: aaload
    //   1599: iconst_m1
    //   1600: bipush -2
    //   1602: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1605: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1608: goto -1010 -> 598
    //   1611: iload 7
    //   1613: bipush 9
    //   1615: if_icmpne +20 -> 1635
    //   1618: aload_0
    //   1619: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1622: bipush 8
    //   1624: aaload
    //   1625: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   1628: checkcast 1723	android/view/ViewGroup
    //   1631: astore_2
    //   1632: goto -986 -> 646
    //   1635: new 1251	android/widget/FrameLayout
    //   1638: dup
    //   1639: aload_1
    //   1640: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   1643: astore 6
    //   1645: aload_0
    //   1646: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1649: aload 6
    //   1651: iconst_m1
    //   1652: bipush 48
    //   1654: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1657: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1660: aload 6
    //   1662: ldc_w 1710
    //   1665: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1668: invokevirtual 1724	android/view/ViewGroup:setBackgroundColor	(I)V
    //   1671: iload 7
    //   1673: iconst_5
    //   1674: if_icmpeq +104 -> 1778
    //   1677: iload 7
    //   1679: bipush 9
    //   1681: if_icmpeq +97 -> 1778
    //   1684: iconst_1
    //   1685: istore 9
    //   1687: iload 9
    //   1689: istore 8
    //   1691: iload 9
    //   1693: ifeq +26 -> 1719
    //   1696: iload 7
    //   1698: bipush 7
    //   1700: if_icmpne +84 -> 1784
    //   1703: aload_0
    //   1704: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1707: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1710: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   1713: ifne +71 -> 1784
    //   1716: iconst_0
    //   1717: istore 8
    //   1719: aload 6
    //   1721: astore_2
    //   1722: iload 8
    //   1724: ifeq -1078 -> 646
    //   1727: new 1197	android/view/View
    //   1730: dup
    //   1731: aload_1
    //   1732: invokespecial 1848	android/view/View:<init>	(Landroid/content/Context;)V
    //   1735: astore_2
    //   1736: aload_0
    //   1737: getfield 302	org/telegram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   1740: aload_2
    //   1741: invokevirtual 1850	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1744: pop
    //   1745: aload_2
    //   1746: ldc_w 1852
    //   1749: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1752: invokevirtual 1626	android/view/View:setBackgroundColor	(I)V
    //   1755: aload 6
    //   1757: aload_2
    //   1758: new 1655	android/widget/FrameLayout$LayoutParams
    //   1761: dup
    //   1762: iconst_m1
    //   1763: iconst_1
    //   1764: bipush 83
    //   1766: invokespecial 1855	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   1769: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1772: aload 6
    //   1774: astore_2
    //   1775: goto -1129 -> 646
    //   1778: iconst_0
    //   1779: istore 9
    //   1781: goto -94 -> 1687
    //   1784: iload 9
    //   1786: istore 8
    //   1788: iload 7
    //   1790: bipush 6
    //   1792: if_icmpne -73 -> 1719
    //   1795: iload 9
    //   1797: istore 8
    //   1799: aload_0
    //   1800: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1803: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1806: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   1809: ifne -90 -> 1719
    //   1812: iload 9
    //   1814: istore 8
    //   1816: aload_0
    //   1817: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1820: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   1823: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   1826: ifne -107 -> 1719
    //   1829: iconst_0
    //   1830: istore 8
    //   1832: goto -113 -> 1719
    //   1835: aload_0
    //   1836: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1839: iload 7
    //   1841: new 708	org/telegram/ui/Components/EditTextBoldCursor
    //   1844: dup
    //   1845: aload_1
    //   1846: invokespecial 1856	org/telegram/ui/Components/EditTextBoldCursor:<init>	(Landroid/content/Context;)V
    //   1849: aastore
    //   1850: goto -1182 -> 668
    //   1853: iload 7
    //   1855: bipush 7
    //   1857: if_icmpne +17 -> 1874
    //   1860: aload_0
    //   1861: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1864: iload 7
    //   1866: aaload
    //   1867: iconst_1
    //   1868: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   1871: goto -1026 -> 845
    //   1874: aload_0
    //   1875: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1878: iload 7
    //   1880: aaload
    //   1881: sipush 16385
    //   1884: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   1887: goto -1042 -> 845
    //   1890: aload_0
    //   1891: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1894: iload 7
    //   1896: aaload
    //   1897: ldc_w 1858
    //   1900: ldc_w 1859
    //   1903: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1906: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   1909: aload_0
    //   1910: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1913: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1916: ifnull -1008 -> 908
    //   1919: aload_0
    //   1920: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1923: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1926: getfield 767	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   1929: ifnull -1021 -> 908
    //   1932: aload_0
    //   1933: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1936: iload 7
    //   1938: aaload
    //   1939: aload_0
    //   1940: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1943: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1946: getfield 767	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   1949: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   1952: goto -1044 -> 908
    //   1955: aload_0
    //   1956: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   1959: iload 7
    //   1961: aaload
    //   1962: ldc_w 1868
    //   1965: ldc_w 1869
    //   1968: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1971: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   1974: aload_0
    //   1975: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1978: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1981: ifnull -1073 -> 908
    //   1984: aload_0
    //   1985: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1988: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1991: getfield 791	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   1994: ifnull -1086 -> 908
    //   1997: aload_0
    //   1998: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2001: iload 7
    //   2003: aaload
    //   2004: aload_0
    //   2005: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2008: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2011: getfield 791	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   2014: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2017: goto -1109 -> 908
    //   2020: aload_0
    //   2021: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2024: iload 7
    //   2026: aaload
    //   2027: ldc_w 1871
    //   2030: ldc_w 1872
    //   2033: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2036: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   2039: aload_0
    //   2040: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2043: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2046: ifnull -1138 -> 908
    //   2049: aload_0
    //   2050: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2053: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2056: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2059: ifnull -1151 -> 908
    //   2062: aload_0
    //   2063: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2066: iload 7
    //   2068: aaload
    //   2069: aload_0
    //   2070: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2073: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2076: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2079: getfield 801	org/telegram/tgnet/TLRPC$TL_postAddress:street_line1	Ljava/lang/String;
    //   2082: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2085: goto -1177 -> 908
    //   2088: aload_0
    //   2089: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2092: iload 7
    //   2094: aaload
    //   2095: ldc_w 1874
    //   2098: ldc_w 1875
    //   2101: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2104: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   2107: aload_0
    //   2108: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2111: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2114: ifnull -1206 -> 908
    //   2117: aload_0
    //   2118: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2121: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2124: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2127: ifnull -1219 -> 908
    //   2130: aload_0
    //   2131: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2134: iload 7
    //   2136: aaload
    //   2137: aload_0
    //   2138: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2141: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2144: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2147: getfield 804	org/telegram/tgnet/TLRPC$TL_postAddress:street_line2	Ljava/lang/String;
    //   2150: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2153: goto -1245 -> 908
    //   2156: aload_0
    //   2157: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2160: iload 7
    //   2162: aaload
    //   2163: ldc_w 1877
    //   2166: ldc_w 1878
    //   2169: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2172: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   2175: aload_0
    //   2176: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2179: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2182: ifnull -1274 -> 908
    //   2185: aload_0
    //   2186: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2189: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2192: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2195: ifnull -1287 -> 908
    //   2198: aload_0
    //   2199: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2202: iload 7
    //   2204: aaload
    //   2205: aload_0
    //   2206: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2209: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2212: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2215: getfield 807	org/telegram/tgnet/TLRPC$TL_postAddress:city	Ljava/lang/String;
    //   2218: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2221: goto -1313 -> 908
    //   2224: aload_0
    //   2225: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2228: iload 7
    //   2230: aaload
    //   2231: ldc_w 1880
    //   2234: ldc_w 1881
    //   2237: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2240: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   2243: aload_0
    //   2244: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2247: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2250: ifnull -1342 -> 908
    //   2253: aload_0
    //   2254: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2257: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2260: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2263: ifnull -1355 -> 908
    //   2266: aload_0
    //   2267: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2270: iload 7
    //   2272: aaload
    //   2273: aload_0
    //   2274: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2277: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2280: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2283: getfield 810	org/telegram/tgnet/TLRPC$TL_postAddress:state	Ljava/lang/String;
    //   2286: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2289: goto -1381 -> 908
    //   2292: aload_0
    //   2293: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2296: iload 7
    //   2298: aaload
    //   2299: ldc_w 1883
    //   2302: ldc_w 1884
    //   2305: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2308: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   2311: aload_0
    //   2312: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2315: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2318: ifnull -1410 -> 908
    //   2321: aload_0
    //   2322: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2325: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2328: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2331: ifnull -1423 -> 908
    //   2334: aload 5
    //   2336: aload_0
    //   2337: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2340: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2343: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2346: getfield 813	org/telegram/tgnet/TLRPC$TL_postAddress:country_iso2	Ljava/lang/String;
    //   2349: invokevirtual 1887	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2352: checkcast 785	java/lang/String
    //   2355: astore 6
    //   2357: aload_0
    //   2358: aload_0
    //   2359: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2362: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2365: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2368: getfield 813	org/telegram/tgnet/TLRPC$TL_postAddress:country_iso2	Ljava/lang/String;
    //   2371: putfield 495	org/telegram/ui/PaymentFormActivity:countryName	Ljava/lang/String;
    //   2374: aload_0
    //   2375: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2378: iload 7
    //   2380: aaload
    //   2381: astore 10
    //   2383: aload 6
    //   2385: ifnull +13 -> 2398
    //   2388: aload 10
    //   2390: aload 6
    //   2392: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2395: goto -1487 -> 908
    //   2398: aload_0
    //   2399: getfield 495	org/telegram/ui/PaymentFormActivity:countryName	Ljava/lang/String;
    //   2402: astore 6
    //   2404: goto -16 -> 2388
    //   2407: aload_0
    //   2408: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2411: iload 7
    //   2413: aaload
    //   2414: ldc_w 1889
    //   2417: ldc_w 1890
    //   2420: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2423: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   2426: aload_0
    //   2427: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2430: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2433: ifnull -1525 -> 908
    //   2436: aload_0
    //   2437: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2440: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2443: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2446: ifnull -1538 -> 908
    //   2449: aload_0
    //   2450: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2453: iload 7
    //   2455: aaload
    //   2456: aload_0
    //   2457: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2460: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2463: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   2466: getfield 816	org/telegram/tgnet/TLRPC$TL_postAddress:post_code	Ljava/lang/String;
    //   2469: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   2472: goto -1564 -> 908
    //   2475: iload 7
    //   2477: bipush 9
    //   2479: if_icmpne +77 -> 2556
    //   2482: aload_0
    //   2483: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2486: iload 7
    //   2488: aaload
    //   2489: iconst_0
    //   2490: iconst_0
    //   2491: iconst_0
    //   2492: iconst_0
    //   2493: invokevirtual 1792	org/telegram/ui/Components/EditTextBoldCursor:setPadding	(IIII)V
    //   2496: aload_0
    //   2497: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2500: iload 7
    //   2502: aaload
    //   2503: bipush 19
    //   2505: invokevirtual 1795	org/telegram/ui/Components/EditTextBoldCursor:setGravity	(I)V
    //   2508: aload_2
    //   2509: aload_0
    //   2510: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2513: iload 7
    //   2515: aaload
    //   2516: iconst_m1
    //   2517: bipush -2
    //   2519: fconst_0
    //   2520: ldc_w 1782
    //   2523: ldc_w 1781
    //   2526: ldc_w 1783
    //   2529: invokestatic 1786	org/telegram/ui/Components/LayoutHelper:createLinear	(IIFFFF)Landroid/widget/LinearLayout$LayoutParams;
    //   2532: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2535: aload_0
    //   2536: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2539: iload 7
    //   2541: aaload
    //   2542: new 112	org/telegram/ui/PaymentFormActivity$5
    //   2545: dup
    //   2546: aload_0
    //   2547: invokespecial 1891	org/telegram/ui/PaymentFormActivity$5:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   2550: invokevirtual 1810	org/telegram/ui/Components/EditTextBoldCursor:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   2553: goto -1441 -> 1112
    //   2556: aload_0
    //   2557: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2560: iload 7
    //   2562: aaload
    //   2563: iconst_0
    //   2564: iconst_0
    //   2565: iconst_0
    //   2566: ldc_w 1783
    //   2569: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2572: invokevirtual 1792	org/telegram/ui/Components/EditTextBoldCursor:setPadding	(IIII)V
    //   2575: aload_0
    //   2576: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2579: iload 7
    //   2581: aaload
    //   2582: astore 6
    //   2584: getstatic 1894	org/telegram/messenger/LocaleController:isRTL	Z
    //   2587: ifeq +48 -> 2635
    //   2590: iconst_5
    //   2591: istore 8
    //   2593: aload 6
    //   2595: iload 8
    //   2597: invokevirtual 1795	org/telegram/ui/Components/EditTextBoldCursor:setGravity	(I)V
    //   2600: aload_2
    //   2601: aload_0
    //   2602: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   2605: iload 7
    //   2607: aaload
    //   2608: iconst_m1
    //   2609: ldc_w 1895
    //   2612: bipush 51
    //   2614: ldc_w 1781
    //   2617: ldc_w 1782
    //   2620: ldc_w 1781
    //   2623: ldc_w 1783
    //   2626: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   2629: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2632: goto -1520 -> 1112
    //   2635: iconst_3
    //   2636: istore 8
    //   2638: goto -45 -> 2593
    //   2641: aload_2
    //   2642: ifnull +271 -> 2913
    //   2645: aload_2
    //   2646: getfield 373	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2649: aload_2
    //   2650: getfield 1898	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   2653: invokestatic 1904	org/telegram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   2656: astore_2
    //   2657: aload_0
    //   2658: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2661: iconst_1
    //   2662: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   2665: dup
    //   2666: aload_1
    //   2667: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   2670: aastore
    //   2671: aload_0
    //   2672: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2675: iconst_1
    //   2676: aaload
    //   2677: aload_1
    //   2678: ldc_w 1561
    //   2681: ldc_w 1550
    //   2684: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   2687: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   2690: aload_0
    //   2691: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   2694: aload_0
    //   2695: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2698: iconst_1
    //   2699: aaload
    //   2700: iconst_m1
    //   2701: bipush -2
    //   2703: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   2706: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2709: aload_0
    //   2710: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2713: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   2716: getfield 1818	org/telegram/tgnet/TLRPC$TL_invoice:email_to_provider	Z
    //   2719: ifeq +201 -> 2920
    //   2722: aload_0
    //   2723: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2726: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   2729: getfield 1821	org/telegram/tgnet/TLRPC$TL_invoice:phone_to_provider	Z
    //   2732: ifeq +188 -> 2920
    //   2735: aload_0
    //   2736: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2739: iconst_1
    //   2740: aaload
    //   2741: ldc_w 1908
    //   2744: ldc_w 1909
    //   2747: iconst_1
    //   2748: anewarray 745	java/lang/Object
    //   2751: dup
    //   2752: iconst_0
    //   2753: aload_2
    //   2754: aastore
    //   2755: invokestatic 1462	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   2758: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   2761: aload_0
    //   2762: new 1541	org/telegram/ui/Cells/TextCheckCell
    //   2765: dup
    //   2766: aload_1
    //   2767: invokespecial 1910	org/telegram/ui/Cells/TextCheckCell:<init>	(Landroid/content/Context;)V
    //   2770: putfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   2773: aload_0
    //   2774: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   2777: iconst_1
    //   2778: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   2781: invokevirtual 1915	org/telegram/ui/Cells/TextCheckCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   2784: aload_0
    //   2785: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   2788: ldc_w 1917
    //   2791: ldc_w 1918
    //   2794: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2797: aload_0
    //   2798: getfield 520	org/telegram/ui/PaymentFormActivity:saveShippingInfo	Z
    //   2801: iconst_0
    //   2802: invokevirtual 1922	org/telegram/ui/Cells/TextCheckCell:setTextAndCheck	(Ljava/lang/String;ZZ)V
    //   2805: aload_0
    //   2806: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   2809: aload_0
    //   2810: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   2813: iconst_m1
    //   2814: bipush -2
    //   2816: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   2819: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2822: aload_0
    //   2823: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   2826: new 116	org/telegram/ui/PaymentFormActivity$7
    //   2829: dup
    //   2830: aload_0
    //   2831: invokespecial 1923	org/telegram/ui/PaymentFormActivity$7:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   2834: invokevirtual 1927	org/telegram/ui/Cells/TextCheckCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   2837: aload_0
    //   2838: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2841: iconst_0
    //   2842: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   2845: dup
    //   2846: aload_1
    //   2847: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   2850: aastore
    //   2851: aload_0
    //   2852: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2855: iconst_0
    //   2856: aaload
    //   2857: aload_1
    //   2858: ldc_w 1561
    //   2861: ldc_w 1550
    //   2864: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   2867: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   2870: aload_0
    //   2871: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2874: iconst_0
    //   2875: aaload
    //   2876: ldc_w 1929
    //   2879: ldc_w 1930
    //   2882: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2885: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   2888: aload_0
    //   2889: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   2892: aload_0
    //   2893: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2896: iconst_0
    //   2897: aaload
    //   2898: iconst_m1
    //   2899: bipush -2
    //   2901: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   2904: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2907: iinc 7 1
    //   2910: goto -2390 -> 520
    //   2913: ldc_w 411
    //   2916: astore_2
    //   2917: goto -260 -> 2657
    //   2920: aload_0
    //   2921: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2924: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   2927: getfield 1818	org/telegram/tgnet/TLRPC$TL_invoice:email_to_provider	Z
    //   2930: ifeq +32 -> 2962
    //   2933: aload_0
    //   2934: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2937: iconst_1
    //   2938: aaload
    //   2939: ldc_w 1932
    //   2942: ldc_w 1933
    //   2945: iconst_1
    //   2946: anewarray 745	java/lang/Object
    //   2949: dup
    //   2950: iconst_0
    //   2951: aload_2
    //   2952: aastore
    //   2953: invokestatic 1462	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   2956: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   2959: goto -198 -> 2761
    //   2962: aload_0
    //   2963: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   2966: iconst_1
    //   2967: aaload
    //   2968: ldc_w 1935
    //   2971: ldc_w 1936
    //   2974: iconst_1
    //   2975: anewarray 745	java/lang/Object
    //   2978: dup
    //   2979: iconst_0
    //   2980: aload_2
    //   2981: aastore
    //   2982: invokestatic 1462	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   2985: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   2988: goto -227 -> 2761
    //   2991: aload_0
    //   2992: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   2995: iconst_1
    //   2996: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   2999: dup
    //   3000: aload_1
    //   3001: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   3004: aastore
    //   3005: aload_0
    //   3006: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3009: aload_0
    //   3010: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   3013: iconst_1
    //   3014: aaload
    //   3015: iconst_m1
    //   3016: bipush -2
    //   3018: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3021: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3024: goto -263 -> 2761
    //   3027: aload_0
    //   3028: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3031: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3034: getfield 424	org/telegram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   3037: ifne +21 -> 3058
    //   3040: aload_0
    //   3041: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3044: bipush 6
    //   3046: aaload
    //   3047: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3050: checkcast 1723	android/view/ViewGroup
    //   3053: bipush 8
    //   3055: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3058: aload_0
    //   3059: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3062: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3065: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3068: ifne +21 -> 3089
    //   3071: aload_0
    //   3072: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3075: bipush 8
    //   3077: aaload
    //   3078: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3081: checkcast 1723	android/view/ViewGroup
    //   3084: bipush 8
    //   3086: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3089: aload_0
    //   3090: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3093: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3096: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   3099: ifne +21 -> 3120
    //   3102: aload_0
    //   3103: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3106: bipush 7
    //   3108: aaload
    //   3109: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3112: checkcast 1723	android/view/ViewGroup
    //   3115: bipush 8
    //   3117: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3120: aload_0
    //   3121: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3124: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3127: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3130: ifeq +451 -> 3581
    //   3133: aload_0
    //   3134: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3137: bipush 9
    //   3139: aaload
    //   3140: ldc_w 1938
    //   3143: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   3146: aload_0
    //   3147: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   3150: iconst_1
    //   3151: aaload
    //   3152: ifnull +509 -> 3661
    //   3155: aload_0
    //   3156: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   3159: iconst_1
    //   3160: aaload
    //   3161: astore_1
    //   3162: aload_0
    //   3163: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3166: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3169: getfield 424	org/telegram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   3172: ifne +29 -> 3201
    //   3175: aload_0
    //   3176: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3179: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3182: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3185: ifne +16 -> 3201
    //   3188: aload_0
    //   3189: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3192: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3195: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   3198: ifeq +456 -> 3654
    //   3201: iconst_0
    //   3202: istore 8
    //   3204: aload_1
    //   3205: iload 8
    //   3207: invokevirtual 1939	org/telegram/ui/Cells/ShadowSectionCell:setVisibility	(I)V
    //   3210: aload_0
    //   3211: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   3214: iconst_1
    //   3215: aaload
    //   3216: astore_1
    //   3217: aload_0
    //   3218: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3221: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3224: getfield 424	org/telegram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   3227: ifne +29 -> 3256
    //   3230: aload_0
    //   3231: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3234: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3237: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3240: ifne +16 -> 3256
    //   3243: aload_0
    //   3244: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3247: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3250: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   3253: ifeq +482 -> 3735
    //   3256: iconst_0
    //   3257: istore 8
    //   3259: aload_1
    //   3260: iload 8
    //   3262: invokevirtual 1476	org/telegram/ui/Cells/HeaderCell:setVisibility	(I)V
    //   3265: aload_0
    //   3266: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3269: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3272: getfield 418	org/telegram/tgnet/TLRPC$TL_invoice:shipping_address_requested	Z
    //   3275: ifne +127 -> 3402
    //   3278: aload_0
    //   3279: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   3282: iconst_0
    //   3283: aaload
    //   3284: bipush 8
    //   3286: invokevirtual 1476	org/telegram/ui/Cells/HeaderCell:setVisibility	(I)V
    //   3289: aload_0
    //   3290: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   3293: iconst_0
    //   3294: aaload
    //   3295: bipush 8
    //   3297: invokevirtual 1939	org/telegram/ui/Cells/ShadowSectionCell:setVisibility	(I)V
    //   3300: aload_0
    //   3301: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3304: iconst_0
    //   3305: aaload
    //   3306: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3309: checkcast 1723	android/view/ViewGroup
    //   3312: bipush 8
    //   3314: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3317: aload_0
    //   3318: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3321: iconst_1
    //   3322: aaload
    //   3323: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3326: checkcast 1723	android/view/ViewGroup
    //   3329: bipush 8
    //   3331: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3334: aload_0
    //   3335: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3338: iconst_2
    //   3339: aaload
    //   3340: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3343: checkcast 1723	android/view/ViewGroup
    //   3346: bipush 8
    //   3348: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3351: aload_0
    //   3352: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3355: iconst_3
    //   3356: aaload
    //   3357: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3360: checkcast 1723	android/view/ViewGroup
    //   3363: bipush 8
    //   3365: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3368: aload_0
    //   3369: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3372: iconst_4
    //   3373: aaload
    //   3374: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3377: checkcast 1723	android/view/ViewGroup
    //   3380: bipush 8
    //   3382: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3385: aload_0
    //   3386: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3389: iconst_5
    //   3390: aaload
    //   3391: invokevirtual 1480	org/telegram/ui/Components/EditTextBoldCursor:getParent	()Landroid/view/ViewParent;
    //   3394: checkcast 1723	android/view/ViewGroup
    //   3397: bipush 8
    //   3399: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   3402: aload_0
    //   3403: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3406: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3409: ifnull +333 -> 3742
    //   3412: aload_0
    //   3413: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3416: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3419: getfield 783	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   3422: invokestatic 1124	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3425: ifne +317 -> 3742
    //   3428: aload_0
    //   3429: aload_0
    //   3430: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3433: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3436: getfield 783	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   3439: invokevirtual 1942	org/telegram/ui/PaymentFormActivity:fillNumber	(Ljava/lang/String;)V
    //   3442: aload_0
    //   3443: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3446: bipush 8
    //   3448: aaload
    //   3449: invokevirtual 711	org/telegram/ui/Components/EditTextBoldCursor:length	()I
    //   3452: ifne +124 -> 3576
    //   3455: aload_0
    //   3456: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3459: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3462: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3465: ifeq +111 -> 3576
    //   3468: aload_0
    //   3469: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3472: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3475: ifnull +19 -> 3494
    //   3478: aload_0
    //   3479: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3482: getfield 1865	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3485: getfield 783	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   3488: invokestatic 1124	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3491: ifeq +85 -> 3576
    //   3494: aconst_null
    //   3495: astore_2
    //   3496: getstatic 717	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   3499: ldc_w 1943
    //   3502: invokevirtual 725	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   3505: checkcast 1945	android/telephony/TelephonyManager
    //   3508: astore 6
    //   3510: aload_2
    //   3511: astore_1
    //   3512: aload 6
    //   3514: ifnull +12 -> 3526
    //   3517: aload 6
    //   3519: invokevirtual 1948	android/telephony/TelephonyManager:getSimCountryIso	()Ljava/lang/String;
    //   3522: invokevirtual 1951	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   3525: astore_1
    //   3526: aload_1
    //   3527: ifnull +49 -> 3576
    //   3530: aload_3
    //   3531: aload_1
    //   3532: invokevirtual 1887	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3535: checkcast 785	java/lang/String
    //   3538: astore_1
    //   3539: aload_1
    //   3540: ifnull +36 -> 3576
    //   3543: aload_0
    //   3544: getfield 287	org/telegram/ui/PaymentFormActivity:countriesArray	Ljava/util/ArrayList;
    //   3547: aload_1
    //   3548: invokevirtual 1954	java/util/ArrayList:indexOf	(Ljava/lang/Object;)I
    //   3551: iconst_m1
    //   3552: if_icmpeq +24 -> 3576
    //   3555: aload_0
    //   3556: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3559: bipush 8
    //   3561: aaload
    //   3562: aload_0
    //   3563: getfield 292	org/telegram/ui/PaymentFormActivity:countriesMap	Ljava/util/HashMap;
    //   3566: aload_1
    //   3567: invokevirtual 1887	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3570: checkcast 1956	java/lang/CharSequence
    //   3573: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   3576: aload_0
    //   3577: getfield 1619	org/telegram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   3580: areturn
    //   3581: aload_0
    //   3582: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3585: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3588: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   3591: ifeq +19 -> 3610
    //   3594: aload_0
    //   3595: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3598: bipush 7
    //   3600: aaload
    //   3601: ldc_w 1938
    //   3604: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   3607: goto -461 -> 3146
    //   3610: aload_0
    //   3611: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3614: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3617: getfield 424	org/telegram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   3620: ifeq +19 -> 3639
    //   3623: aload_0
    //   3624: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3627: bipush 6
    //   3629: aaload
    //   3630: ldc_w 1938
    //   3633: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   3636: goto -490 -> 3146
    //   3639: aload_0
    //   3640: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   3643: iconst_5
    //   3644: aaload
    //   3645: ldc_w 1938
    //   3648: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   3651: goto -505 -> 3146
    //   3654: bipush 8
    //   3656: istore 8
    //   3658: goto -454 -> 3204
    //   3661: aload_0
    //   3662: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   3665: iconst_1
    //   3666: aaload
    //   3667: ifnull -457 -> 3210
    //   3670: aload_0
    //   3671: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   3674: iconst_1
    //   3675: aaload
    //   3676: astore_1
    //   3677: aload_0
    //   3678: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3681: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3684: getfield 424	org/telegram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   3687: ifne +29 -> 3716
    //   3690: aload_0
    //   3691: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3694: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3697: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3700: ifne +16 -> 3716
    //   3703: aload_0
    //   3704: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3707: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   3710: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   3713: ifeq +15 -> 3728
    //   3716: iconst_0
    //   3717: istore 8
    //   3719: aload_1
    //   3720: iload 8
    //   3722: invokevirtual 1470	org/telegram/ui/Cells/TextInfoPrivacyCell:setVisibility	(I)V
    //   3725: goto -515 -> 3210
    //   3728: bipush 8
    //   3730: istore 8
    //   3732: goto -13 -> 3719
    //   3735: bipush 8
    //   3737: istore 8
    //   3739: goto -480 -> 3259
    //   3742: aload_0
    //   3743: aconst_null
    //   3744: invokevirtual 1942	org/telegram/ui/PaymentFormActivity:fillNumber	(Ljava/lang/String;)V
    //   3747: goto -305 -> 3442
    //   3750: astore_1
    //   3751: aload_1
    //   3752: invokestatic 1032	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3755: aload_2
    //   3756: astore_1
    //   3757: goto -231 -> 3526
    //   3760: aload_0
    //   3761: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   3764: iconst_2
    //   3765: if_icmpne +2000 -> 5765
    //   3768: aload_0
    //   3769: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3772: getfield 1959	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:native_params	Lorg/telegram/tgnet/TLRPC$TL_dataJSON;
    //   3775: ifnull +70 -> 3845
    //   3778: new 1961	org/json/JSONObject
    //   3781: astore_2
    //   3782: aload_2
    //   3783: aload_0
    //   3784: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3787: getfield 1959	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:native_params	Lorg/telegram/tgnet/TLRPC$TL_dataJSON;
    //   3790: getfield 1090	org/telegram/tgnet/TLRPC$TL_dataJSON:data	Ljava/lang/String;
    //   3793: invokespecial 1962	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   3796: aload_2
    //   3797: ldc_w 1964
    //   3800: invokevirtual 1967	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   3803: astore 6
    //   3805: aload 6
    //   3807: invokestatic 1124	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3810: ifne +9 -> 3819
    //   3813: aload_0
    //   3814: aload 6
    //   3816: putfield 1247	org/telegram/ui/PaymentFormActivity:androidPayPublicKey	Ljava/lang/String;
    //   3819: aload_0
    //   3820: aload_2
    //   3821: ldc_w 1969
    //   3824: invokevirtual 1972	org/json/JSONObject:getInt	(Ljava/lang/String;)I
    //   3827: ldc_w 1973
    //   3830: ior
    //   3831: putfield 1249	org/telegram/ui/PaymentFormActivity:androidPayBackgroundColor	I
    //   3834: aload_0
    //   3835: aload_2
    //   3836: ldc_w 1975
    //   3839: invokevirtual 1979	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   3842: putfield 1263	org/telegram/ui/PaymentFormActivity:androidPayBlackTheme	Z
    //   3845: aload_0
    //   3846: getfield 635	org/telegram/ui/PaymentFormActivity:isWebView	Z
    //   3849: ifeq +434 -> 4283
    //   3852: aload_0
    //   3853: getfield 1247	org/telegram/ui/PaymentFormActivity:androidPayPublicKey	Ljava/lang/String;
    //   3856: ifnull +8 -> 3864
    //   3859: aload_0
    //   3860: aload_1
    //   3861: invokespecial 1981	org/telegram/ui/PaymentFormActivity:initAndroidPay	(Landroid/content/Context;)V
    //   3864: aload_0
    //   3865: new 1251	android/widget/FrameLayout
    //   3868: dup
    //   3869: aload_1
    //   3870: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   3873: putfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   3876: aload_0
    //   3877: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   3880: sipush 4000
    //   3883: invokevirtual 1984	android/widget/FrameLayout:setId	(I)V
    //   3886: aload_0
    //   3887: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   3890: iconst_1
    //   3891: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   3894: invokevirtual 1985	android/widget/FrameLayout:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   3897: aload_0
    //   3898: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   3901: bipush 8
    //   3903: invokevirtual 1377	android/widget/FrameLayout:setVisibility	(I)V
    //   3906: aload_0
    //   3907: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3910: aload_0
    //   3911: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   3914: iconst_m1
    //   3915: bipush 48
    //   3917: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3920: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3923: aload_0
    //   3924: iconst_1
    //   3925: putfield 528	org/telegram/ui/PaymentFormActivity:webviewLoading	Z
    //   3928: aload_0
    //   3929: iconst_1
    //   3930: iconst_1
    //   3931: invokespecial 534	org/telegram/ui/PaymentFormActivity:showEditDoneProgress	(ZZ)V
    //   3934: aload_0
    //   3935: getfield 673	org/telegram/ui/PaymentFormActivity:progressView	Lorg/telegram/ui/Components/ContextProgressView;
    //   3938: iconst_0
    //   3939: invokevirtual 1424	org/telegram/ui/Components/ContextProgressView:setVisibility	(I)V
    //   3942: aload_0
    //   3943: getfield 517	org/telegram/ui/PaymentFormActivity:doneItem	Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   3946: iconst_0
    //   3947: invokevirtual 1425	org/telegram/ui/ActionBar/ActionBarMenuItem:setEnabled	(Z)V
    //   3950: aload_0
    //   3951: getfield 517	org/telegram/ui/PaymentFormActivity:doneItem	Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   3954: invokevirtual 1429	org/telegram/ui/ActionBar/ActionBarMenuItem:getImageView	()Landroid/widget/ImageView;
    //   3957: iconst_4
    //   3958: invokevirtual 1445	android/widget/ImageView:setVisibility	(I)V
    //   3961: aload_0
    //   3962: new 118	org/telegram/ui/PaymentFormActivity$8
    //   3965: dup
    //   3966: aload_0
    //   3967: aload_1
    //   3968: invokespecial 1988	org/telegram/ui/PaymentFormActivity$8:<init>	(Lorg/telegram/ui/PaymentFormActivity;Landroid/content/Context;)V
    //   3971: putfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3974: aload_0
    //   3975: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3978: invokevirtual 1994	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   3981: iconst_1
    //   3982: invokevirtual 1999	android/webkit/WebSettings:setJavaScriptEnabled	(Z)V
    //   3985: aload_0
    //   3986: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3989: invokevirtual 1994	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   3992: iconst_1
    //   3993: invokevirtual 2002	android/webkit/WebSettings:setDomStorageEnabled	(Z)V
    //   3996: getstatic 900	android/os/Build$VERSION:SDK_INT	I
    //   3999: bipush 21
    //   4001: if_icmplt +25 -> 4026
    //   4004: aload_0
    //   4005: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   4008: invokevirtual 1994	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   4011: iconst_0
    //   4012: invokevirtual 2005	android/webkit/WebSettings:setMixedContentMode	(I)V
    //   4015: invokestatic 2010	android/webkit/CookieManager:getInstance	()Landroid/webkit/CookieManager;
    //   4018: aload_0
    //   4019: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   4022: iconst_1
    //   4023: invokevirtual 2014	android/webkit/CookieManager:setAcceptThirdPartyCookies	(Landroid/webkit/WebView;Z)V
    //   4026: aload_0
    //   4027: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   4030: new 131	org/telegram/ui/PaymentFormActivity$TelegramWebviewProxy
    //   4033: dup
    //   4034: aload_0
    //   4035: aconst_null
    //   4036: invokespecial 2017	org/telegram/ui/PaymentFormActivity$TelegramWebviewProxy:<init>	(Lorg/telegram/ui/PaymentFormActivity;Lorg/telegram/ui/PaymentFormActivity$1;)V
    //   4039: ldc_w 2018
    //   4042: invokevirtual 2022	android/webkit/WebView:addJavascriptInterface	(Ljava/lang/Object;Ljava/lang/String;)V
    //   4045: aload_0
    //   4046: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   4049: new 120	org/telegram/ui/PaymentFormActivity$9
    //   4052: dup
    //   4053: aload_0
    //   4054: invokespecial 2023	org/telegram/ui/PaymentFormActivity$9:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   4057: invokevirtual 2027	android/webkit/WebView:setWebViewClient	(Landroid/webkit/WebViewClient;)V
    //   4060: aload_0
    //   4061: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4064: aload_0
    //   4065: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   4068: iconst_m1
    //   4069: ldc_w 1895
    //   4072: invokestatic 1610	org/telegram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   4075: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4078: aload_0
    //   4079: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   4082: iconst_2
    //   4083: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   4086: dup
    //   4087: aload_1
    //   4088: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   4091: aastore
    //   4092: aload_0
    //   4093: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4096: aload_0
    //   4097: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   4100: iconst_2
    //   4101: aaload
    //   4102: iconst_m1
    //   4103: bipush -2
    //   4105: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4108: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4111: aload_0
    //   4112: new 1541	org/telegram/ui/Cells/TextCheckCell
    //   4115: dup
    //   4116: aload_1
    //   4117: invokespecial 1910	org/telegram/ui/Cells/TextCheckCell:<init>	(Landroid/content/Context;)V
    //   4120: putfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   4123: aload_0
    //   4124: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   4127: iconst_1
    //   4128: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   4131: invokevirtual 1915	org/telegram/ui/Cells/TextCheckCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   4134: aload_0
    //   4135: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   4138: ldc_w 2029
    //   4141: ldc_w 2030
    //   4144: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4147: aload_0
    //   4148: getfield 541	org/telegram/ui/PaymentFormActivity:saveCardInfo	Z
    //   4151: iconst_0
    //   4152: invokevirtual 1922	org/telegram/ui/Cells/TextCheckCell:setTextAndCheck	(Ljava/lang/String;ZZ)V
    //   4155: aload_0
    //   4156: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4159: aload_0
    //   4160: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   4163: iconst_m1
    //   4164: bipush -2
    //   4166: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4169: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4172: aload_0
    //   4173: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   4176: new 10	org/telegram/ui/PaymentFormActivity$10
    //   4179: dup
    //   4180: aload_0
    //   4181: invokespecial 2031	org/telegram/ui/PaymentFormActivity$10:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   4184: invokevirtual 1927	org/telegram/ui/Cells/TextCheckCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   4187: aload_0
    //   4188: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   4191: iconst_0
    //   4192: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   4195: dup
    //   4196: aload_1
    //   4197: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   4200: aastore
    //   4201: aload_0
    //   4202: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   4205: iconst_0
    //   4206: aaload
    //   4207: aload_1
    //   4208: ldc_w 1561
    //   4211: ldc_w 1550
    //   4214: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   4217: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   4220: aload_0
    //   4221: invokespecial 538	org/telegram/ui/PaymentFormActivity:updateSavePaymentField	()V
    //   4224: aload_0
    //   4225: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4228: aload_0
    //   4229: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   4232: iconst_0
    //   4233: aaload
    //   4234: iconst_m1
    //   4235: bipush -2
    //   4237: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4240: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4243: goto -667 -> 3576
    //   4246: astore 6
    //   4248: aload_0
    //   4249: aconst_null
    //   4250: putfield 1247	org/telegram/ui/PaymentFormActivity:androidPayPublicKey	Ljava/lang/String;
    //   4253: goto -434 -> 3819
    //   4256: astore_2
    //   4257: aload_2
    //   4258: invokestatic 1032	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   4261: goto -416 -> 3845
    //   4264: astore 6
    //   4266: aload_0
    //   4267: iconst_m1
    //   4268: putfield 1249	org/telegram/ui/PaymentFormActivity:androidPayBackgroundColor	I
    //   4271: goto -437 -> 3834
    //   4274: astore_2
    //   4275: aload_0
    //   4276: iconst_0
    //   4277: putfield 1263	org/telegram/ui/PaymentFormActivity:androidPayBlackTheme	Z
    //   4280: goto -435 -> 3845
    //   4283: aload_0
    //   4284: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   4287: getfield 1959	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:native_params	Lorg/telegram/tgnet/TLRPC$TL_dataJSON;
    //   4290: ifnull +65 -> 4355
    //   4293: new 1961	org/json/JSONObject
    //   4296: astore_2
    //   4297: aload_2
    //   4298: aload_0
    //   4299: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   4302: getfield 1959	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:native_params	Lorg/telegram/tgnet/TLRPC$TL_dataJSON;
    //   4305: getfield 1090	org/telegram/tgnet/TLRPC$TL_dataJSON:data	Ljava/lang/String;
    //   4308: invokespecial 1962	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   4311: aload_0
    //   4312: aload_2
    //   4313: ldc_w 2033
    //   4316: invokevirtual 1979	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   4319: putfield 1013	org/telegram/ui/PaymentFormActivity:need_card_country	Z
    //   4322: aload_0
    //   4323: aload_2
    //   4324: ldc_w 2035
    //   4327: invokevirtual 1979	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   4330: putfield 1015	org/telegram/ui/PaymentFormActivity:need_card_postcode	Z
    //   4333: aload_0
    //   4334: aload_2
    //   4335: ldc_w 2037
    //   4338: invokevirtual 1979	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   4341: putfield 549	org/telegram/ui/PaymentFormActivity:need_card_name	Z
    //   4344: aload_0
    //   4345: aload_2
    //   4346: ldc_w 2039
    //   4349: invokevirtual 1967	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   4352: putfield 1019	org/telegram/ui/PaymentFormActivity:stripeApiKey	Ljava/lang/String;
    //   4355: aload_0
    //   4356: aload_1
    //   4357: invokespecial 1981	org/telegram/ui/PaymentFormActivity:initAndroidPay	(Landroid/content/Context;)V
    //   4360: aload_0
    //   4361: bipush 6
    //   4363: anewarray 708	org/telegram/ui/Components/EditTextBoldCursor
    //   4366: putfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4369: iconst_0
    //   4370: istore 8
    //   4372: iload 8
    //   4374: bipush 6
    //   4376: if_icmpge +1316 -> 5692
    //   4379: iload 8
    //   4381: ifne +700 -> 5081
    //   4384: aload_0
    //   4385: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   4388: iconst_0
    //   4389: new 298	org/telegram/ui/Cells/HeaderCell
    //   4392: dup
    //   4393: aload_1
    //   4394: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   4397: aastore
    //   4398: aload_0
    //   4399: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   4402: iconst_0
    //   4403: aaload
    //   4404: ldc_w 1710
    //   4407: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4410: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   4413: aload_0
    //   4414: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   4417: iconst_0
    //   4418: aaload
    //   4419: ldc_w 2041
    //   4422: ldc_w 2042
    //   4425: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4428: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   4431: aload_0
    //   4432: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4435: aload_0
    //   4436: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   4439: iconst_0
    //   4440: aaload
    //   4441: iconst_m1
    //   4442: bipush -2
    //   4444: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4447: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4450: iload 8
    //   4452: iconst_3
    //   4453: if_icmpeq +703 -> 5156
    //   4456: iload 8
    //   4458: iconst_5
    //   4459: if_icmpeq +697 -> 5156
    //   4462: iload 8
    //   4464: iconst_4
    //   4465: if_icmpne +10 -> 4475
    //   4468: aload_0
    //   4469: getfield 1015	org/telegram/ui/PaymentFormActivity:need_card_postcode	Z
    //   4472: ifeq +684 -> 5156
    //   4475: iconst_1
    //   4476: istore 9
    //   4478: new 1251	android/widget/FrameLayout
    //   4481: dup
    //   4482: aload_1
    //   4483: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   4486: astore_2
    //   4487: aload_0
    //   4488: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4491: aload_2
    //   4492: iconst_m1
    //   4493: bipush 48
    //   4495: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4498: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4501: aload_2
    //   4502: ldc_w 1710
    //   4505: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4508: invokevirtual 1724	android/view/ViewGroup:setBackgroundColor	(I)V
    //   4511: aload_0
    //   4512: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4515: iload 8
    //   4517: new 708	org/telegram/ui/Components/EditTextBoldCursor
    //   4520: dup
    //   4521: aload_1
    //   4522: invokespecial 1856	org/telegram/ui/Components/EditTextBoldCursor:<init>	(Landroid/content/Context;)V
    //   4525: aastore
    //   4526: aload_0
    //   4527: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4530: iload 8
    //   4532: aaload
    //   4533: iload 8
    //   4535: invokestatic 362	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4538: invokevirtual 1731	org/telegram/ui/Components/EditTextBoldCursor:setTag	(Ljava/lang/Object;)V
    //   4541: aload_0
    //   4542: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4545: iload 8
    //   4547: aaload
    //   4548: iconst_1
    //   4549: ldc_w 1732
    //   4552: invokevirtual 1736	org/telegram/ui/Components/EditTextBoldCursor:setTextSize	(IF)V
    //   4555: aload_0
    //   4556: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4559: iload 8
    //   4561: aaload
    //   4562: ldc_w 1738
    //   4565: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4568: invokevirtual 1741	org/telegram/ui/Components/EditTextBoldCursor:setHintTextColor	(I)V
    //   4571: aload_0
    //   4572: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4575: iload 8
    //   4577: aaload
    //   4578: ldc_w 1743
    //   4581: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4584: invokevirtual 1746	org/telegram/ui/Components/EditTextBoldCursor:setTextColor	(I)V
    //   4587: aload_0
    //   4588: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4591: iload 8
    //   4593: aaload
    //   4594: aconst_null
    //   4595: invokevirtual 1747	org/telegram/ui/Components/EditTextBoldCursor:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   4598: aload_0
    //   4599: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4602: iload 8
    //   4604: aaload
    //   4605: ldc_w 1743
    //   4608: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4611: invokevirtual 1750	org/telegram/ui/Components/EditTextBoldCursor:setCursorColor	(I)V
    //   4614: aload_0
    //   4615: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4618: iload 8
    //   4620: aaload
    //   4621: ldc_w 1751
    //   4624: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4627: invokevirtual 1754	org/telegram/ui/Components/EditTextBoldCursor:setCursorSize	(I)V
    //   4630: aload_0
    //   4631: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4634: iload 8
    //   4636: aaload
    //   4637: ldc_w 1755
    //   4640: invokevirtual 1759	org/telegram/ui/Components/EditTextBoldCursor:setCursorWidth	(F)V
    //   4643: iload 8
    //   4645: iconst_3
    //   4646: if_icmpne +516 -> 5162
    //   4649: new 1797	android/text/InputFilter$LengthFilter
    //   4652: dup
    //   4653: iconst_3
    //   4654: invokespecial 1799	android/text/InputFilter$LengthFilter:<init>	(I)V
    //   4657: astore 6
    //   4659: aload_0
    //   4660: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4663: iload 8
    //   4665: aaload
    //   4666: iconst_1
    //   4667: anewarray 1801	android/text/InputFilter
    //   4670: dup
    //   4671: iconst_0
    //   4672: aload 6
    //   4674: aastore
    //   4675: invokevirtual 1805	org/telegram/ui/Components/EditTextBoldCursor:setFilters	([Landroid/text/InputFilter;)V
    //   4678: aload_0
    //   4679: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4682: iload 8
    //   4684: aaload
    //   4685: sipush 130
    //   4688: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   4691: aload_0
    //   4692: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4695: iload 8
    //   4697: aaload
    //   4698: getstatic 2048	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   4701: invokevirtual 2052	org/telegram/ui/Components/EditTextBoldCursor:setTypeface	(Landroid/graphics/Typeface;)V
    //   4704: aload_0
    //   4705: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4708: iload 8
    //   4710: aaload
    //   4711: invokestatic 2057	android/text/method/PasswordTransformationMethod:getInstance	()Landroid/text/method/PasswordTransformationMethod;
    //   4714: invokevirtual 2061	org/telegram/ui/Components/EditTextBoldCursor:setTransformationMethod	(Landroid/text/method/TransformationMethod;)V
    //   4717: aload_0
    //   4718: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4721: iload 8
    //   4723: aaload
    //   4724: ldc_w 1768
    //   4727: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   4730: iload 8
    //   4732: tableswitch	default:+40->4772, 0:+547->5279, 1:+591->5323, 2:+613->5345, 3:+569->5301, 4:+657->5389, 5:+635->5367
    //   4772: iload 8
    //   4774: ifne +637 -> 5411
    //   4777: aload_0
    //   4778: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4781: iload 8
    //   4783: aaload
    //   4784: new 16	org/telegram/ui/PaymentFormActivity$12
    //   4787: dup
    //   4788: aload_0
    //   4789: invokespecial 2062	org/telegram/ui/PaymentFormActivity$12:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   4792: invokevirtual 1810	org/telegram/ui/Components/EditTextBoldCursor:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   4795: aload_0
    //   4796: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4799: iload 8
    //   4801: aaload
    //   4802: iconst_0
    //   4803: iconst_0
    //   4804: iconst_0
    //   4805: ldc_w 1783
    //   4808: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4811: invokevirtual 1792	org/telegram/ui/Components/EditTextBoldCursor:setPadding	(IIII)V
    //   4814: aload_0
    //   4815: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4818: iload 8
    //   4820: aaload
    //   4821: astore 6
    //   4823: getstatic 1894	org/telegram/messenger/LocaleController:isRTL	Z
    //   4826: ifeq +612 -> 5438
    //   4829: iconst_5
    //   4830: istore 7
    //   4832: aload 6
    //   4834: iload 7
    //   4836: invokevirtual 1795	org/telegram/ui/Components/EditTextBoldCursor:setGravity	(I)V
    //   4839: aload_2
    //   4840: aload_0
    //   4841: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4844: iload 8
    //   4846: aaload
    //   4847: iconst_m1
    //   4848: ldc_w 1895
    //   4851: bipush 51
    //   4853: ldc_w 1781
    //   4856: ldc_w 1782
    //   4859: ldc_w 1781
    //   4862: ldc_w 1783
    //   4865: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   4868: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4871: aload_0
    //   4872: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   4875: iload 8
    //   4877: aaload
    //   4878: new 20	org/telegram/ui/PaymentFormActivity$14
    //   4881: dup
    //   4882: aload_0
    //   4883: invokespecial 2063	org/telegram/ui/PaymentFormActivity$14:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   4886: invokevirtual 1815	org/telegram/ui/Components/EditTextBoldCursor:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   4889: iload 8
    //   4891: iconst_3
    //   4892: if_icmpne +552 -> 5444
    //   4895: aload_0
    //   4896: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   4899: iconst_0
    //   4900: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   4903: dup
    //   4904: aload_1
    //   4905: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   4908: aastore
    //   4909: aload_0
    //   4910: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4913: aload_0
    //   4914: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   4917: iconst_0
    //   4918: aaload
    //   4919: iconst_m1
    //   4920: bipush -2
    //   4922: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4925: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4928: iload 9
    //   4930: ifeq +51 -> 4981
    //   4933: new 1197	android/view/View
    //   4936: dup
    //   4937: aload_1
    //   4938: invokespecial 1848	android/view/View:<init>	(Landroid/content/Context;)V
    //   4941: astore 6
    //   4943: aload_0
    //   4944: getfield 302	org/telegram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   4947: aload 6
    //   4949: invokevirtual 1850	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4952: pop
    //   4953: aload 6
    //   4955: ldc_w 1852
    //   4958: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4961: invokevirtual 1626	android/view/View:setBackgroundColor	(I)V
    //   4964: aload_2
    //   4965: aload 6
    //   4967: new 1655	android/widget/FrameLayout$LayoutParams
    //   4970: dup
    //   4971: iconst_m1
    //   4972: iconst_1
    //   4973: bipush 83
    //   4975: invokespecial 1855	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   4978: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4981: iload 8
    //   4983: iconst_4
    //   4984: if_icmpne +10 -> 4994
    //   4987: aload_0
    //   4988: getfield 1013	org/telegram/ui/PaymentFormActivity:need_card_country	Z
    //   4991: ifeq +29 -> 5020
    //   4994: iload 8
    //   4996: iconst_5
    //   4997: if_icmpne +10 -> 5007
    //   5000: aload_0
    //   5001: getfield 1015	org/telegram/ui/PaymentFormActivity:need_card_postcode	Z
    //   5004: ifeq +16 -> 5020
    //   5007: iload 8
    //   5009: iconst_2
    //   5010: if_icmpne +16 -> 5026
    //   5013: aload_0
    //   5014: getfield 549	org/telegram/ui/PaymentFormActivity:need_card_name	Z
    //   5017: ifne +9 -> 5026
    //   5020: aload_2
    //   5021: bipush 8
    //   5023: invokevirtual 1937	android/view/ViewGroup:setVisibility	(I)V
    //   5026: iinc 8 1
    //   5029: goto -657 -> 4372
    //   5032: astore 6
    //   5034: aload_0
    //   5035: iconst_0
    //   5036: putfield 1013	org/telegram/ui/PaymentFormActivity:need_card_country	Z
    //   5039: goto -717 -> 4322
    //   5042: astore_2
    //   5043: aload_2
    //   5044: invokestatic 1032	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   5047: goto -692 -> 4355
    //   5050: astore 6
    //   5052: aload_0
    //   5053: iconst_0
    //   5054: putfield 1015	org/telegram/ui/PaymentFormActivity:need_card_postcode	Z
    //   5057: goto -724 -> 4333
    //   5060: astore 6
    //   5062: aload_0
    //   5063: iconst_0
    //   5064: putfield 549	org/telegram/ui/PaymentFormActivity:need_card_name	Z
    //   5067: goto -723 -> 4344
    //   5070: astore_2
    //   5071: aload_0
    //   5072: ldc_w 411
    //   5075: putfield 1019	org/telegram/ui/PaymentFormActivity:stripeApiKey	Ljava/lang/String;
    //   5078: goto -723 -> 4355
    //   5081: iload 8
    //   5083: iconst_4
    //   5084: if_icmpne -634 -> 4450
    //   5087: aload_0
    //   5088: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   5091: iconst_1
    //   5092: new 298	org/telegram/ui/Cells/HeaderCell
    //   5095: dup
    //   5096: aload_1
    //   5097: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   5100: aastore
    //   5101: aload_0
    //   5102: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   5105: iconst_1
    //   5106: aaload
    //   5107: ldc_w 1710
    //   5110: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   5113: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   5116: aload_0
    //   5117: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   5120: iconst_1
    //   5121: aaload
    //   5122: ldc_w 2065
    //   5125: ldc_w 2066
    //   5128: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5131: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   5134: aload_0
    //   5135: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5138: aload_0
    //   5139: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   5142: iconst_1
    //   5143: aaload
    //   5144: iconst_m1
    //   5145: bipush -2
    //   5147: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5150: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5153: goto -703 -> 4450
    //   5156: iconst_0
    //   5157: istore 9
    //   5159: goto -681 -> 4478
    //   5162: iload 8
    //   5164: ifne +17 -> 5181
    //   5167: aload_0
    //   5168: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5171: iload 8
    //   5173: aaload
    //   5174: iconst_2
    //   5175: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   5178: goto -461 -> 4717
    //   5181: iload 8
    //   5183: iconst_4
    //   5184: if_icmpne +35 -> 5219
    //   5187: aload_0
    //   5188: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5191: iload 8
    //   5193: aaload
    //   5194: new 12	org/telegram/ui/PaymentFormActivity$11
    //   5197: dup
    //   5198: aload_0
    //   5199: invokespecial 2067	org/telegram/ui/PaymentFormActivity$11:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   5202: invokevirtual 1764	org/telegram/ui/Components/EditTextBoldCursor:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   5205: aload_0
    //   5206: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5209: iload 8
    //   5211: aaload
    //   5212: iconst_0
    //   5213: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   5216: goto -499 -> 4717
    //   5219: iload 8
    //   5221: iconst_1
    //   5222: if_icmpne +19 -> 5241
    //   5225: aload_0
    //   5226: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5229: iload 8
    //   5231: aaload
    //   5232: sipush 16386
    //   5235: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   5238: goto -521 -> 4717
    //   5241: iload 8
    //   5243: iconst_2
    //   5244: if_icmpne +19 -> 5263
    //   5247: aload_0
    //   5248: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5251: iload 8
    //   5253: aaload
    //   5254: sipush 4097
    //   5257: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   5260: goto -543 -> 4717
    //   5263: aload_0
    //   5264: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5267: iload 8
    //   5269: aaload
    //   5270: sipush 16385
    //   5273: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   5276: goto -559 -> 4717
    //   5279: aload_0
    //   5280: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5283: iload 8
    //   5285: aaload
    //   5286: ldc_w 2069
    //   5289: ldc_w 2070
    //   5292: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5295: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   5298: goto -526 -> 4772
    //   5301: aload_0
    //   5302: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5305: iload 8
    //   5307: aaload
    //   5308: ldc_w 2072
    //   5311: ldc_w 2073
    //   5314: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5317: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   5320: goto -548 -> 4772
    //   5323: aload_0
    //   5324: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5327: iload 8
    //   5329: aaload
    //   5330: ldc_w 2075
    //   5333: ldc_w 2076
    //   5336: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5339: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   5342: goto -570 -> 4772
    //   5345: aload_0
    //   5346: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5349: iload 8
    //   5351: aaload
    //   5352: ldc_w 2078
    //   5355: ldc_w 2079
    //   5358: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5361: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   5364: goto -592 -> 4772
    //   5367: aload_0
    //   5368: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5371: iload 8
    //   5373: aaload
    //   5374: ldc_w 1889
    //   5377: ldc_w 1890
    //   5380: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5383: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   5386: goto -614 -> 4772
    //   5389: aload_0
    //   5390: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5393: iload 8
    //   5395: aaload
    //   5396: ldc_w 1883
    //   5399: ldc_w 1884
    //   5402: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5405: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   5408: goto -636 -> 4772
    //   5411: iload 8
    //   5413: iconst_1
    //   5414: if_icmpne -619 -> 4795
    //   5417: aload_0
    //   5418: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5421: iload 8
    //   5423: aaload
    //   5424: new 18	org/telegram/ui/PaymentFormActivity$13
    //   5427: dup
    //   5428: aload_0
    //   5429: invokespecial 2080	org/telegram/ui/PaymentFormActivity$13:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   5432: invokevirtual 1810	org/telegram/ui/Components/EditTextBoldCursor:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   5435: goto -640 -> 4795
    //   5438: iconst_3
    //   5439: istore 7
    //   5441: goto -609 -> 4832
    //   5444: iload 8
    //   5446: iconst_5
    //   5447: if_icmpne +171 -> 5618
    //   5450: aload_0
    //   5451: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   5454: iconst_2
    //   5455: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   5458: dup
    //   5459: aload_1
    //   5460: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   5463: aastore
    //   5464: aload_0
    //   5465: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5468: aload_0
    //   5469: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   5472: iconst_2
    //   5473: aaload
    //   5474: iconst_m1
    //   5475: bipush -2
    //   5477: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5480: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5483: aload_0
    //   5484: new 1541	org/telegram/ui/Cells/TextCheckCell
    //   5487: dup
    //   5488: aload_1
    //   5489: invokespecial 1910	org/telegram/ui/Cells/TextCheckCell:<init>	(Landroid/content/Context;)V
    //   5492: putfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   5495: aload_0
    //   5496: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   5499: iconst_1
    //   5500: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   5503: invokevirtual 1915	org/telegram/ui/Cells/TextCheckCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5506: aload_0
    //   5507: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   5510: ldc_w 2029
    //   5513: ldc_w 2030
    //   5516: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5519: aload_0
    //   5520: getfield 541	org/telegram/ui/PaymentFormActivity:saveCardInfo	Z
    //   5523: iconst_0
    //   5524: invokevirtual 1922	org/telegram/ui/Cells/TextCheckCell:setTextAndCheck	(Ljava/lang/String;ZZ)V
    //   5527: aload_0
    //   5528: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5531: aload_0
    //   5532: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   5535: iconst_m1
    //   5536: bipush -2
    //   5538: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5541: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5544: aload_0
    //   5545: getfield 525	org/telegram/ui/PaymentFormActivity:checkCell1	Lorg/telegram/ui/Cells/TextCheckCell;
    //   5548: new 22	org/telegram/ui/PaymentFormActivity$15
    //   5551: dup
    //   5552: aload_0
    //   5553: invokespecial 2081	org/telegram/ui/PaymentFormActivity$15:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   5556: invokevirtual 1927	org/telegram/ui/Cells/TextCheckCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   5559: aload_0
    //   5560: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   5563: iconst_0
    //   5564: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   5567: dup
    //   5568: aload_1
    //   5569: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   5572: aastore
    //   5573: aload_0
    //   5574: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   5577: iconst_0
    //   5578: aaload
    //   5579: aload_1
    //   5580: ldc_w 1561
    //   5583: ldc_w 1550
    //   5586: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   5589: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5592: aload_0
    //   5593: invokespecial 538	org/telegram/ui/PaymentFormActivity:updateSavePaymentField	()V
    //   5596: aload_0
    //   5597: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5600: aload_0
    //   5601: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   5604: iconst_0
    //   5605: aaload
    //   5606: iconst_m1
    //   5607: bipush -2
    //   5609: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5612: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5615: goto -687 -> 4928
    //   5618: iload 8
    //   5620: ifne -692 -> 4928
    //   5623: aload_0
    //   5624: new 1251	android/widget/FrameLayout
    //   5627: dup
    //   5628: aload_1
    //   5629: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   5632: putfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   5635: aload_0
    //   5636: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   5639: sipush 4000
    //   5642: invokevirtual 1984	android/widget/FrameLayout:setId	(I)V
    //   5645: aload_0
    //   5646: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   5649: iconst_1
    //   5650: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   5653: invokevirtual 1985	android/widget/FrameLayout:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5656: aload_0
    //   5657: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   5660: bipush 8
    //   5662: invokevirtual 1377	android/widget/FrameLayout:setVisibility	(I)V
    //   5665: aload_2
    //   5666: aload_0
    //   5667: getfield 1231	org/telegram/ui/PaymentFormActivity:androidPayContainer	Landroid/widget/FrameLayout;
    //   5670: bipush -2
    //   5672: ldc_w 1895
    //   5675: bipush 21
    //   5677: fconst_0
    //   5678: fconst_0
    //   5679: ldc_w 2082
    //   5682: fconst_0
    //   5683: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   5686: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5689: goto -761 -> 4928
    //   5692: aload_0
    //   5693: getfield 1013	org/telegram/ui/PaymentFormActivity:need_card_country	Z
    //   5696: ifne +32 -> 5728
    //   5699: aload_0
    //   5700: getfield 1015	org/telegram/ui/PaymentFormActivity:need_card_postcode	Z
    //   5703: ifne +25 -> 5728
    //   5706: aload_0
    //   5707: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   5710: iconst_1
    //   5711: aaload
    //   5712: bipush 8
    //   5714: invokevirtual 1476	org/telegram/ui/Cells/HeaderCell:setVisibility	(I)V
    //   5717: aload_0
    //   5718: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   5721: iconst_0
    //   5722: aaload
    //   5723: bipush 8
    //   5725: invokevirtual 1939	org/telegram/ui/Cells/ShadowSectionCell:setVisibility	(I)V
    //   5728: aload_0
    //   5729: getfield 1015	org/telegram/ui/PaymentFormActivity:need_card_postcode	Z
    //   5732: ifeq +18 -> 5750
    //   5735: aload_0
    //   5736: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5739: iconst_5
    //   5740: aaload
    //   5741: ldc_w 1938
    //   5744: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   5747: goto -2171 -> 3576
    //   5750: aload_0
    //   5751: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   5754: iconst_3
    //   5755: aaload
    //   5756: ldc_w 1938
    //   5759: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   5762: goto -2186 -> 3576
    //   5765: aload_0
    //   5766: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   5769: iconst_1
    //   5770: if_icmpne +270 -> 6040
    //   5773: aload_0
    //   5774: getfield 704	org/telegram/ui/PaymentFormActivity:requestedInfo	Lorg/telegram/tgnet/TLRPC$TL_payments_validatedRequestedInfo;
    //   5777: getfield 2085	org/telegram/tgnet/TLRPC$TL_payments_validatedRequestedInfo:shipping_options	Ljava/util/ArrayList;
    //   5780: invokevirtual 821	java/util/ArrayList:size	()I
    //   5783: istore 9
    //   5785: aload_0
    //   5786: iload 9
    //   5788: anewarray 2087	org/telegram/ui/Cells/RadioCell
    //   5791: putfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5794: iconst_0
    //   5795: istore 8
    //   5797: iload 8
    //   5799: iload 9
    //   5801: if_icmpge +184 -> 5985
    //   5804: aload_0
    //   5805: getfield 704	org/telegram/ui/PaymentFormActivity:requestedInfo	Lorg/telegram/tgnet/TLRPC$TL_payments_validatedRequestedInfo;
    //   5808: getfield 2085	org/telegram/tgnet/TLRPC$TL_payments_validatedRequestedInfo:shipping_options	Ljava/util/ArrayList;
    //   5811: iload 8
    //   5813: invokevirtual 825	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5816: checkcast 1068	org/telegram/tgnet/TLRPC$TL_shippingOption
    //   5819: astore 6
    //   5821: aload_0
    //   5822: getfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5825: iload 8
    //   5827: new 2087	org/telegram/ui/Cells/RadioCell
    //   5830: dup
    //   5831: aload_1
    //   5832: invokespecial 2088	org/telegram/ui/Cells/RadioCell:<init>	(Landroid/content/Context;)V
    //   5835: aastore
    //   5836: aload_0
    //   5837: getfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5840: iload 8
    //   5842: aaload
    //   5843: iload 8
    //   5845: invokestatic 362	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5848: invokevirtual 2089	org/telegram/ui/Cells/RadioCell:setTag	(Ljava/lang/Object;)V
    //   5851: aload_0
    //   5852: getfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5855: iload 8
    //   5857: aaload
    //   5858: iconst_1
    //   5859: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   5862: invokevirtual 2090	org/telegram/ui/Cells/RadioCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5865: aload_0
    //   5866: getfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5869: iload 8
    //   5871: aaload
    //   5872: astore_2
    //   5873: ldc_w 2092
    //   5876: iconst_2
    //   5877: anewarray 745	java/lang/Object
    //   5880: dup
    //   5881: iconst_0
    //   5882: aload_0
    //   5883: aload 6
    //   5885: getfield 1309	org/telegram/tgnet/TLRPC$TL_shippingOption:prices	Ljava/util/ArrayList;
    //   5888: invokespecial 2094	org/telegram/ui/PaymentFormActivity:getTotalPriceString	(Ljava/util/ArrayList;)Ljava/lang/String;
    //   5891: aastore
    //   5892: dup
    //   5893: iconst_1
    //   5894: aload 6
    //   5896: getfield 2095	org/telegram/tgnet/TLRPC$TL_shippingOption:title	Ljava/lang/String;
    //   5899: aastore
    //   5900: invokestatic 2099	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   5903: astore 6
    //   5905: iload 8
    //   5907: ifne +66 -> 5973
    //   5910: iconst_1
    //   5911: istore 11
    //   5913: iload 8
    //   5915: iload 9
    //   5917: iconst_1
    //   5918: isub
    //   5919: if_icmpeq +60 -> 5979
    //   5922: iconst_1
    //   5923: istore 12
    //   5925: aload_2
    //   5926: aload 6
    //   5928: iload 11
    //   5930: iload 12
    //   5932: invokevirtual 2101	org/telegram/ui/Cells/RadioCell:setText	(Ljava/lang/String;ZZ)V
    //   5935: aload_0
    //   5936: getfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5939: iload 8
    //   5941: aaload
    //   5942: new 24	org/telegram/ui/PaymentFormActivity$16
    //   5945: dup
    //   5946: aload_0
    //   5947: invokespecial 2102	org/telegram/ui/PaymentFormActivity$16:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   5950: invokevirtual 2103	org/telegram/ui/Cells/RadioCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   5953: aload_0
    //   5954: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5957: aload_0
    //   5958: getfield 683	org/telegram/ui/PaymentFormActivity:radioCells	[Lorg/telegram/ui/Cells/RadioCell;
    //   5961: iload 8
    //   5963: aaload
    //   5964: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   5967: iinc 8 1
    //   5970: goto -173 -> 5797
    //   5973: iconst_0
    //   5974: istore 11
    //   5976: goto -63 -> 5913
    //   5979: iconst_0
    //   5980: istore 12
    //   5982: goto -57 -> 5925
    //   5985: aload_0
    //   5986: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   5989: iconst_0
    //   5990: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   5993: dup
    //   5994: aload_1
    //   5995: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   5998: aastore
    //   5999: aload_0
    //   6000: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6003: iconst_0
    //   6004: aaload
    //   6005: aload_1
    //   6006: ldc_w 1561
    //   6009: ldc_w 1550
    //   6012: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   6015: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   6018: aload_0
    //   6019: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6022: aload_0
    //   6023: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6026: iconst_0
    //   6027: aaload
    //   6028: iconst_m1
    //   6029: bipush -2
    //   6031: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6034: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6037: goto -2461 -> 3576
    //   6040: aload_0
    //   6041: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   6044: iconst_3
    //   6045: if_icmpne +888 -> 6933
    //   6048: aload_0
    //   6049: iconst_2
    //   6050: anewarray 708	org/telegram/ui/Components/EditTextBoldCursor
    //   6053: putfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6056: iconst_0
    //   6057: istore 7
    //   6059: iload 7
    //   6061: iconst_2
    //   6062: if_icmpge -2486 -> 3576
    //   6065: iload 7
    //   6067: ifne +69 -> 6136
    //   6070: aload_0
    //   6071: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   6074: iconst_0
    //   6075: new 298	org/telegram/ui/Cells/HeaderCell
    //   6078: dup
    //   6079: aload_1
    //   6080: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   6083: aastore
    //   6084: aload_0
    //   6085: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   6088: iconst_0
    //   6089: aaload
    //   6090: ldc_w 1710
    //   6093: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6096: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   6099: aload_0
    //   6100: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   6103: iconst_0
    //   6104: aaload
    //   6105: ldc_w 2041
    //   6108: ldc_w 2042
    //   6111: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6114: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   6117: aload_0
    //   6118: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6121: aload_0
    //   6122: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   6125: iconst_0
    //   6126: aaload
    //   6127: iconst_m1
    //   6128: bipush -2
    //   6130: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6133: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6136: new 1251	android/widget/FrameLayout
    //   6139: dup
    //   6140: aload_1
    //   6141: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   6144: astore_2
    //   6145: aload_0
    //   6146: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6149: aload_2
    //   6150: iconst_m1
    //   6151: bipush 48
    //   6153: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6156: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6159: aload_2
    //   6160: ldc_w 1710
    //   6163: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6166: invokevirtual 1724	android/view/ViewGroup:setBackgroundColor	(I)V
    //   6169: iload 7
    //   6171: iconst_1
    //   6172: if_icmpeq +613 -> 6785
    //   6175: iconst_1
    //   6176: istore 8
    //   6178: iload 8
    //   6180: istore 9
    //   6182: iload 8
    //   6184: ifeq +26 -> 6210
    //   6187: iload 7
    //   6189: bipush 7
    //   6191: if_icmpne +600 -> 6791
    //   6194: aload_0
    //   6195: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6198: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   6201: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   6204: ifne +587 -> 6791
    //   6207: iconst_0
    //   6208: istore 9
    //   6210: iload 9
    //   6212: ifeq +51 -> 6263
    //   6215: new 1197	android/view/View
    //   6218: dup
    //   6219: aload_1
    //   6220: invokespecial 1848	android/view/View:<init>	(Landroid/content/Context;)V
    //   6223: astore 6
    //   6225: aload_0
    //   6226: getfield 302	org/telegram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   6229: aload 6
    //   6231: invokevirtual 1850	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   6234: pop
    //   6235: aload 6
    //   6237: ldc_w 1852
    //   6240: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6243: invokevirtual 1626	android/view/View:setBackgroundColor	(I)V
    //   6246: aload_2
    //   6247: aload 6
    //   6249: new 1655	android/widget/FrameLayout$LayoutParams
    //   6252: dup
    //   6253: iconst_m1
    //   6254: iconst_1
    //   6255: bipush 83
    //   6257: invokespecial 1855	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   6260: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6263: aload_0
    //   6264: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6267: iload 7
    //   6269: new 708	org/telegram/ui/Components/EditTextBoldCursor
    //   6272: dup
    //   6273: aload_1
    //   6274: invokespecial 1856	org/telegram/ui/Components/EditTextBoldCursor:<init>	(Landroid/content/Context;)V
    //   6277: aastore
    //   6278: aload_0
    //   6279: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6282: iload 7
    //   6284: aaload
    //   6285: iload 7
    //   6287: invokestatic 362	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6290: invokevirtual 1731	org/telegram/ui/Components/EditTextBoldCursor:setTag	(Ljava/lang/Object;)V
    //   6293: aload_0
    //   6294: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6297: iload 7
    //   6299: aaload
    //   6300: iconst_1
    //   6301: ldc_w 1732
    //   6304: invokevirtual 1736	org/telegram/ui/Components/EditTextBoldCursor:setTextSize	(IF)V
    //   6307: aload_0
    //   6308: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6311: iload 7
    //   6313: aaload
    //   6314: ldc_w 1738
    //   6317: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6320: invokevirtual 1741	org/telegram/ui/Components/EditTextBoldCursor:setHintTextColor	(I)V
    //   6323: aload_0
    //   6324: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6327: iload 7
    //   6329: aaload
    //   6330: ldc_w 1743
    //   6333: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6336: invokevirtual 1746	org/telegram/ui/Components/EditTextBoldCursor:setTextColor	(I)V
    //   6339: aload_0
    //   6340: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6343: iload 7
    //   6345: aaload
    //   6346: aconst_null
    //   6347: invokevirtual 1747	org/telegram/ui/Components/EditTextBoldCursor:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   6350: aload_0
    //   6351: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6354: iload 7
    //   6356: aaload
    //   6357: ldc_w 1743
    //   6360: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6363: invokevirtual 1750	org/telegram/ui/Components/EditTextBoldCursor:setCursorColor	(I)V
    //   6366: aload_0
    //   6367: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6370: iload 7
    //   6372: aaload
    //   6373: ldc_w 1751
    //   6376: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6379: invokevirtual 1754	org/telegram/ui/Components/EditTextBoldCursor:setCursorSize	(I)V
    //   6382: aload_0
    //   6383: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6386: iload 7
    //   6388: aaload
    //   6389: ldc_w 1755
    //   6392: invokevirtual 1759	org/telegram/ui/Components/EditTextBoldCursor:setCursorWidth	(F)V
    //   6395: iload 7
    //   6397: ifne +445 -> 6842
    //   6400: aload_0
    //   6401: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6404: iload 7
    //   6406: aaload
    //   6407: new 26	org/telegram/ui/PaymentFormActivity$17
    //   6410: dup
    //   6411: aload_0
    //   6412: invokespecial 2106	org/telegram/ui/PaymentFormActivity$17:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   6415: invokevirtual 1764	org/telegram/ui/Components/EditTextBoldCursor:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   6418: aload_0
    //   6419: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6422: iload 7
    //   6424: aaload
    //   6425: iconst_0
    //   6426: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   6429: aload_0
    //   6430: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6433: iload 7
    //   6435: aaload
    //   6436: ldc_w 1938
    //   6439: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   6442: iload 7
    //   6444: tableswitch	default:+24->6468, 0:+427->6871, 1:+450->6894
    //   6468: aload_0
    //   6469: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6472: iload 7
    //   6474: aaload
    //   6475: iconst_0
    //   6476: iconst_0
    //   6477: iconst_0
    //   6478: ldc_w 1783
    //   6481: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6484: invokevirtual 1792	org/telegram/ui/Components/EditTextBoldCursor:setPadding	(IIII)V
    //   6487: aload_0
    //   6488: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6491: iload 7
    //   6493: aaload
    //   6494: astore 6
    //   6496: getstatic 1894	org/telegram/messenger/LocaleController:isRTL	Z
    //   6499: ifeq +428 -> 6927
    //   6502: iconst_5
    //   6503: istore 8
    //   6505: aload 6
    //   6507: iload 8
    //   6509: invokevirtual 1795	org/telegram/ui/Components/EditTextBoldCursor:setGravity	(I)V
    //   6512: aload_2
    //   6513: aload_0
    //   6514: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6517: iload 7
    //   6519: aaload
    //   6520: iconst_m1
    //   6521: ldc_w 1895
    //   6524: bipush 51
    //   6526: ldc_w 1781
    //   6529: ldc_w 1782
    //   6532: ldc_w 1781
    //   6535: ldc_w 1783
    //   6538: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   6541: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6544: aload_0
    //   6545: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6548: iload 7
    //   6550: aaload
    //   6551: new 28	org/telegram/ui/PaymentFormActivity$18
    //   6554: dup
    //   6555: aload_0
    //   6556: invokespecial 2107	org/telegram/ui/PaymentFormActivity$18:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   6559: invokevirtual 1815	org/telegram/ui/Components/EditTextBoldCursor:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   6562: iload 7
    //   6564: iconst_1
    //   6565: if_icmpne +214 -> 6779
    //   6568: aload_0
    //   6569: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6572: iconst_0
    //   6573: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   6576: dup
    //   6577: aload_1
    //   6578: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   6581: aastore
    //   6582: aload_0
    //   6583: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6586: iconst_0
    //   6587: aaload
    //   6588: ldc_w 2109
    //   6591: ldc_w 2110
    //   6594: iconst_1
    //   6595: anewarray 745	java/lang/Object
    //   6598: dup
    //   6599: iconst_0
    //   6600: aload_0
    //   6601: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6604: getfield 435	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_credentials	Lorg/telegram/tgnet/TLRPC$TL_paymentSavedCredentialsCard;
    //   6607: getfield 893	org/telegram/tgnet/TLRPC$TL_paymentSavedCredentialsCard:title	Ljava/lang/String;
    //   6610: aastore
    //   6611: invokestatic 1462	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   6614: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   6617: aload_0
    //   6618: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6621: iconst_0
    //   6622: aaload
    //   6623: aload_1
    //   6624: ldc_w 1548
    //   6627: ldc_w 1550
    //   6630: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   6633: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   6636: aload_0
    //   6637: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6640: aload_0
    //   6641: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6644: iconst_0
    //   6645: aaload
    //   6646: iconst_m1
    //   6647: bipush -2
    //   6649: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6652: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6655: aload_0
    //   6656: new 1474	org/telegram/ui/Cells/TextSettingsCell
    //   6659: dup
    //   6660: aload_1
    //   6661: invokespecial 2111	org/telegram/ui/Cells/TextSettingsCell:<init>	(Landroid/content/Context;)V
    //   6664: putfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   6667: aload_0
    //   6668: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   6671: iconst_1
    //   6672: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   6675: invokevirtual 2112	org/telegram/ui/Cells/TextSettingsCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   6678: aload_0
    //   6679: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   6682: ldc_w 2114
    //   6685: ldc_w 2115
    //   6688: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6691: iconst_0
    //   6692: invokevirtual 2118	org/telegram/ui/Cells/TextSettingsCell:setText	(Ljava/lang/String;Z)V
    //   6695: aload_0
    //   6696: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6699: aload_0
    //   6700: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   6703: iconst_m1
    //   6704: bipush -2
    //   6706: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6709: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6712: aload_0
    //   6713: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   6716: new 30	org/telegram/ui/PaymentFormActivity$19
    //   6719: dup
    //   6720: aload_0
    //   6721: invokespecial 2119	org/telegram/ui/PaymentFormActivity$19:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   6724: invokevirtual 2120	org/telegram/ui/Cells/TextSettingsCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   6727: aload_0
    //   6728: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6731: iconst_1
    //   6732: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   6735: dup
    //   6736: aload_1
    //   6737: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   6740: aastore
    //   6741: aload_0
    //   6742: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6745: iconst_1
    //   6746: aaload
    //   6747: aload_1
    //   6748: ldc_w 1561
    //   6751: ldc_w 1550
    //   6754: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   6757: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   6760: aload_0
    //   6761: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6764: aload_0
    //   6765: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   6768: iconst_1
    //   6769: aaload
    //   6770: iconst_m1
    //   6771: bipush -2
    //   6773: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6776: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6779: iinc 7 1
    //   6782: goto -723 -> 6059
    //   6785: iconst_0
    //   6786: istore 8
    //   6788: goto -610 -> 6178
    //   6791: iload 8
    //   6793: istore 9
    //   6795: iload 7
    //   6797: bipush 6
    //   6799: if_icmpne -589 -> 6210
    //   6802: iload 8
    //   6804: istore 9
    //   6806: aload_0
    //   6807: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6810: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   6813: getfield 427	org/telegram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   6816: ifne -606 -> 6210
    //   6819: iload 8
    //   6821: istore 9
    //   6823: aload_0
    //   6824: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6827: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   6830: getfield 421	org/telegram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   6833: ifne -623 -> 6210
    //   6836: iconst_0
    //   6837: istore 9
    //   6839: goto -629 -> 6210
    //   6842: aload_0
    //   6843: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6846: iload 7
    //   6848: aaload
    //   6849: sipush 129
    //   6852: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   6855: aload_0
    //   6856: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6859: iload 7
    //   6861: aaload
    //   6862: getstatic 2048	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   6865: invokevirtual 2052	org/telegram/ui/Components/EditTextBoldCursor:setTypeface	(Landroid/graphics/Typeface;)V
    //   6868: goto -439 -> 6429
    //   6871: aload_0
    //   6872: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6875: iload 7
    //   6877: aaload
    //   6878: aload_0
    //   6879: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6882: getfield 435	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:saved_credentials	Lorg/telegram/tgnet/TLRPC$TL_paymentSavedCredentialsCard;
    //   6885: getfield 893	org/telegram/tgnet/TLRPC$TL_paymentSavedCredentialsCard:title	Ljava/lang/String;
    //   6888: invokevirtual 1866	org/telegram/ui/Components/EditTextBoldCursor:setText	(Ljava/lang/CharSequence;)V
    //   6891: goto -423 -> 6468
    //   6894: aload_0
    //   6895: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6898: iload 7
    //   6900: aaload
    //   6901: ldc_w 2122
    //   6904: ldc_w 2123
    //   6907: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6910: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   6913: aload_0
    //   6914: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   6917: iload 7
    //   6919: aaload
    //   6920: invokevirtual 2126	org/telegram/ui/Components/EditTextBoldCursor:requestFocus	()Z
    //   6923: pop
    //   6924: goto -456 -> 6468
    //   6927: iconst_3
    //   6928: istore 8
    //   6930: goto -425 -> 6505
    //   6933: aload_0
    //   6934: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   6937: iconst_4
    //   6938: if_icmpeq +11 -> 6949
    //   6941: aload_0
    //   6942: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   6945: iconst_5
    //   6946: if_icmpne +1524 -> 8470
    //   6949: aload_0
    //   6950: new 2128	org/telegram/ui/Cells/PaymentInfoCell
    //   6953: dup
    //   6954: aload_1
    //   6955: invokespecial 2129	org/telegram/ui/Cells/PaymentInfoCell:<init>	(Landroid/content/Context;)V
    //   6958: putfield 2131	org/telegram/ui/PaymentFormActivity:paymentInfoCell	Lorg/telegram/ui/Cells/PaymentInfoCell;
    //   6961: aload_0
    //   6962: getfield 2131	org/telegram/ui/PaymentFormActivity:paymentInfoCell	Lorg/telegram/ui/Cells/PaymentInfoCell;
    //   6965: ldc_w 1710
    //   6968: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6971: invokevirtual 2132	org/telegram/ui/Cells/PaymentInfoCell:setBackgroundColor	(I)V
    //   6974: aload_0
    //   6975: getfield 2131	org/telegram/ui/PaymentFormActivity:paymentInfoCell	Lorg/telegram/ui/Cells/PaymentInfoCell;
    //   6978: aload_0
    //   6979: getfield 347	org/telegram/ui/PaymentFormActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   6982: getfield 381	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6985: getfield 387	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6988: checkcast 2134	org/telegram/tgnet/TLRPC$TL_messageMediaInvoice
    //   6991: aload_0
    //   6992: getfield 375	org/telegram/ui/PaymentFormActivity:currentBotName	Ljava/lang/String;
    //   6995: invokevirtual 2138	org/telegram/ui/Cells/PaymentInfoCell:setInvoice	(Lorg/telegram/tgnet/TLRPC$TL_messageMediaInvoice;Ljava/lang/String;)V
    //   6998: aload_0
    //   6999: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7002: aload_0
    //   7003: getfield 2131	org/telegram/ui/PaymentFormActivity:paymentInfoCell	Lorg/telegram/ui/Cells/PaymentInfoCell;
    //   7006: iconst_m1
    //   7007: bipush -2
    //   7009: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   7012: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7015: aload_0
    //   7016: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   7019: iconst_0
    //   7020: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   7023: dup
    //   7024: aload_1
    //   7025: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   7028: aastore
    //   7029: aload_0
    //   7030: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7033: aload_0
    //   7034: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   7037: iconst_0
    //   7038: aaload
    //   7039: iconst_m1
    //   7040: bipush -2
    //   7042: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   7045: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7048: new 284	java/util/ArrayList
    //   7051: dup
    //   7052: invokespecial 285	java/util/ArrayList:<init>	()V
    //   7055: astore_2
    //   7056: aload_2
    //   7057: aload_0
    //   7058: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   7061: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   7064: getfield 1304	org/telegram/tgnet/TLRPC$TL_invoice:prices	Ljava/util/ArrayList;
    //   7067: invokevirtual 1308	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   7070: pop
    //   7071: aload_0
    //   7072: getfield 345	org/telegram/ui/PaymentFormActivity:shippingOption	Lorg/telegram/tgnet/TLRPC$TL_shippingOption;
    //   7075: ifnull +15 -> 7090
    //   7078: aload_2
    //   7079: aload_0
    //   7080: getfield 345	org/telegram/ui/PaymentFormActivity:shippingOption	Lorg/telegram/tgnet/TLRPC$TL_shippingOption;
    //   7083: getfield 1309	org/telegram/tgnet/TLRPC$TL_shippingOption:prices	Ljava/util/ArrayList;
    //   7086: invokevirtual 1308	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   7089: pop
    //   7090: aload_0
    //   7091: aload_2
    //   7092: invokespecial 2094	org/telegram/ui/PaymentFormActivity:getTotalPriceString	(Ljava/util/ArrayList;)Ljava/lang/String;
    //   7095: astore 5
    //   7097: iconst_0
    //   7098: istore 8
    //   7100: iload 8
    //   7102: aload_2
    //   7103: invokevirtual 821	java/util/ArrayList:size	()I
    //   7106: if_icmpge +82 -> 7188
    //   7109: aload_2
    //   7110: iload 8
    //   7112: invokevirtual 825	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   7115: checkcast 827	org/telegram/tgnet/TLRPC$TL_labeledPrice
    //   7118: astore 6
    //   7120: new 2140	org/telegram/ui/Cells/TextPriceCell
    //   7123: dup
    //   7124: aload_1
    //   7125: invokespecial 2141	org/telegram/ui/Cells/TextPriceCell:<init>	(Landroid/content/Context;)V
    //   7128: astore 10
    //   7130: aload 10
    //   7132: ldc_w 1710
    //   7135: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7138: invokevirtual 2142	org/telegram/ui/Cells/TextPriceCell:setBackgroundColor	(I)V
    //   7141: aload 10
    //   7143: aload 6
    //   7145: getfield 2145	org/telegram/tgnet/TLRPC$TL_labeledPrice:label	Ljava/lang/String;
    //   7148: invokestatic 836	org/telegram/messenger/LocaleController:getInstance	()Lorg/telegram/messenger/LocaleController;
    //   7151: aload 6
    //   7153: getfield 831	org/telegram/tgnet/TLRPC$TL_labeledPrice:amount	J
    //   7156: aload_0
    //   7157: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   7160: getfield 332	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/telegram/tgnet/TLRPC$TL_invoice;
    //   7163: getfield 839	org/telegram/tgnet/TLRPC$TL_invoice:currency	Ljava/lang/String;
    //   7166: invokevirtual 850	org/telegram/messenger/LocaleController:formatCurrencyString	(JLjava/lang/String;)Ljava/lang/String;
    //   7169: iconst_0
    //   7170: invokevirtual 2149	org/telegram/ui/Cells/TextPriceCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   7173: aload_0
    //   7174: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7177: aload 10
    //   7179: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7182: iinc 8 1
    //   7185: goto -85 -> 7100
    //   7188: new 2140	org/telegram/ui/Cells/TextPriceCell
    //   7191: dup
    //   7192: aload_1
    //   7193: invokespecial 2141	org/telegram/ui/Cells/TextPriceCell:<init>	(Landroid/content/Context;)V
    //   7196: astore_2
    //   7197: aload_2
    //   7198: ldc_w 1710
    //   7201: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7204: invokevirtual 2142	org/telegram/ui/Cells/TextPriceCell:setBackgroundColor	(I)V
    //   7207: aload_2
    //   7208: ldc_w 2151
    //   7211: ldc_w 2152
    //   7214: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7217: aload 5
    //   7219: iconst_1
    //   7220: invokevirtual 2149	org/telegram/ui/Cells/TextPriceCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   7223: aload_0
    //   7224: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7227: aload_2
    //   7228: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7231: new 1197	android/view/View
    //   7234: dup
    //   7235: aload_1
    //   7236: invokespecial 1848	android/view/View:<init>	(Landroid/content/Context;)V
    //   7239: astore_2
    //   7240: aload_0
    //   7241: getfield 302	org/telegram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   7244: aload_2
    //   7245: invokevirtual 1850	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   7248: pop
    //   7249: aload_2
    //   7250: ldc_w 1852
    //   7253: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7256: invokevirtual 1626	android/view/View:setBackgroundColor	(I)V
    //   7259: aload_0
    //   7260: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7263: aload_2
    //   7264: new 1655	android/widget/FrameLayout$LayoutParams
    //   7267: dup
    //   7268: iconst_m1
    //   7269: iconst_1
    //   7270: bipush 83
    //   7272: invokespecial 1855	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   7275: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7278: aload_0
    //   7279: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7282: iconst_0
    //   7283: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7286: dup
    //   7287: aload_1
    //   7288: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7291: aastore
    //   7292: aload_0
    //   7293: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7296: iconst_0
    //   7297: aaload
    //   7298: iconst_1
    //   7299: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   7302: invokevirtual 2154	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   7305: aload_0
    //   7306: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7309: iconst_0
    //   7310: aaload
    //   7311: aload_0
    //   7312: getfield 409	org/telegram/ui/PaymentFormActivity:cardName	Ljava/lang/String;
    //   7315: ldc_w 2156
    //   7318: ldc_w 2157
    //   7321: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7324: iconst_1
    //   7325: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   7328: aload_0
    //   7329: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7332: aload_0
    //   7333: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7336: iconst_0
    //   7337: aaload
    //   7338: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7341: aload_0
    //   7342: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   7345: iconst_4
    //   7346: if_icmpne +20 -> 7366
    //   7349: aload_0
    //   7350: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7353: iconst_0
    //   7354: aaload
    //   7355: new 34	org/telegram/ui/PaymentFormActivity$20
    //   7358: dup
    //   7359: aload_0
    //   7360: invokespecial 2161	org/telegram/ui/PaymentFormActivity$20:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   7363: invokevirtual 2162	org/telegram/ui/Cells/TextDetailSettingsCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   7366: aconst_null
    //   7367: astore_2
    //   7368: iconst_0
    //   7369: istore 8
    //   7371: iload 8
    //   7373: aload_0
    //   7374: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   7377: getfield 340	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:users	Ljava/util/ArrayList;
    //   7380: invokevirtual 821	java/util/ArrayList:size	()I
    //   7383: if_icmpge +44 -> 7427
    //   7386: aload_0
    //   7387: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   7390: getfield 340	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:users	Ljava/util/ArrayList;
    //   7393: iload 8
    //   7395: invokevirtual 825	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   7398: checkcast 370	org/telegram/tgnet/TLRPC$User
    //   7401: astore 6
    //   7403: aload 6
    //   7405: getfield 1823	org/telegram/tgnet/TLRPC$User:id	I
    //   7408: aload_0
    //   7409: getfield 321	org/telegram/ui/PaymentFormActivity:paymentForm	Lorg/telegram/tgnet/TLRPC$TL_payments_paymentForm;
    //   7412: getfield 336	org/telegram/tgnet/TLRPC$TL_payments_paymentForm:provider_id	I
    //   7415: if_icmpne +6 -> 7421
    //   7418: aload 6
    //   7420: astore_2
    //   7421: iinc 8 1
    //   7424: goto -53 -> 7371
    //   7427: aload_2
    //   7428: ifnull +1035 -> 8463
    //   7431: aload_0
    //   7432: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7435: iconst_1
    //   7436: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7439: dup
    //   7440: aload_1
    //   7441: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7444: aastore
    //   7445: aload_0
    //   7446: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7449: iconst_1
    //   7450: aaload
    //   7451: iconst_1
    //   7452: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   7455: invokevirtual 2154	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   7458: aload_0
    //   7459: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7462: iconst_1
    //   7463: aaload
    //   7464: astore 6
    //   7466: aload_2
    //   7467: getfield 373	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   7470: aload_2
    //   7471: getfield 1898	org/telegram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   7474: invokestatic 1904	org/telegram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   7477: astore_2
    //   7478: aload 6
    //   7480: aload_2
    //   7481: ldc_w 2164
    //   7484: ldc_w 2165
    //   7487: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7490: iconst_1
    //   7491: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   7494: aload_0
    //   7495: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7498: aload_0
    //   7499: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7502: iconst_1
    //   7503: aaload
    //   7504: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7507: aload_0
    //   7508: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7511: ifnull +525 -> 8036
    //   7514: aload_0
    //   7515: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7518: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7521: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7524: ifnull +175 -> 7699
    //   7527: ldc_w 2167
    //   7530: bipush 6
    //   7532: anewarray 745	java/lang/Object
    //   7535: dup
    //   7536: iconst_0
    //   7537: aload_0
    //   7538: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7541: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7544: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7547: getfield 801	org/telegram/tgnet/TLRPC$TL_postAddress:street_line1	Ljava/lang/String;
    //   7550: aastore
    //   7551: dup
    //   7552: iconst_1
    //   7553: aload_0
    //   7554: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7557: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7560: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7563: getfield 804	org/telegram/tgnet/TLRPC$TL_postAddress:street_line2	Ljava/lang/String;
    //   7566: aastore
    //   7567: dup
    //   7568: iconst_2
    //   7569: aload_0
    //   7570: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7573: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7576: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7579: getfield 807	org/telegram/tgnet/TLRPC$TL_postAddress:city	Ljava/lang/String;
    //   7582: aastore
    //   7583: dup
    //   7584: iconst_3
    //   7585: aload_0
    //   7586: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7589: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7592: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7595: getfield 810	org/telegram/tgnet/TLRPC$TL_postAddress:state	Ljava/lang/String;
    //   7598: aastore
    //   7599: dup
    //   7600: iconst_4
    //   7601: aload_0
    //   7602: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7605: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7608: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7611: getfield 813	org/telegram/tgnet/TLRPC$TL_postAddress:country_iso2	Ljava/lang/String;
    //   7614: aastore
    //   7615: dup
    //   7616: iconst_5
    //   7617: aload_0
    //   7618: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7621: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7624: getfield 798	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/telegram/tgnet/TLRPC$TL_postAddress;
    //   7627: getfield 816	org/telegram/tgnet/TLRPC$TL_postAddress:post_code	Ljava/lang/String;
    //   7630: aastore
    //   7631: invokestatic 2099	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   7634: astore 6
    //   7636: aload_0
    //   7637: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7640: iconst_2
    //   7641: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7644: dup
    //   7645: aload_1
    //   7646: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7649: aastore
    //   7650: aload_0
    //   7651: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7654: iconst_2
    //   7655: aaload
    //   7656: ldc_w 1710
    //   7659: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7662: invokevirtual 2168	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   7665: aload_0
    //   7666: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7669: iconst_2
    //   7670: aaload
    //   7671: aload 6
    //   7673: ldc_w 1713
    //   7676: ldc_w 1714
    //   7679: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7682: iconst_1
    //   7683: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   7686: aload_0
    //   7687: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7690: aload_0
    //   7691: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7694: iconst_2
    //   7695: aaload
    //   7696: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7699: aload_0
    //   7700: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7703: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7706: getfield 767	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   7709: ifnull +74 -> 7783
    //   7712: aload_0
    //   7713: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7716: iconst_3
    //   7717: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7720: dup
    //   7721: aload_1
    //   7722: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7725: aastore
    //   7726: aload_0
    //   7727: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7730: iconst_3
    //   7731: aaload
    //   7732: ldc_w 1710
    //   7735: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7738: invokevirtual 2168	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   7741: aload_0
    //   7742: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7745: iconst_3
    //   7746: aaload
    //   7747: aload_0
    //   7748: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7751: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7754: getfield 767	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   7757: ldc_w 2170
    //   7760: ldc_w 2171
    //   7763: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7766: iconst_1
    //   7767: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   7770: aload_0
    //   7771: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7774: aload_0
    //   7775: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7778: iconst_3
    //   7779: aaload
    //   7780: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7783: aload_0
    //   7784: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7787: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7790: getfield 783	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   7793: ifnull +80 -> 7873
    //   7796: aload_0
    //   7797: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7800: iconst_4
    //   7801: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7804: dup
    //   7805: aload_1
    //   7806: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7809: aastore
    //   7810: aload_0
    //   7811: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7814: iconst_4
    //   7815: aaload
    //   7816: ldc_w 1710
    //   7819: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7822: invokevirtual 2168	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   7825: aload_0
    //   7826: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7829: iconst_4
    //   7830: aaload
    //   7831: invokestatic 2176	org/telegram/PhoneFormat/PhoneFormat:getInstance	()Lorg/telegram/PhoneFormat/PhoneFormat;
    //   7834: aload_0
    //   7835: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7838: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7841: getfield 783	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   7844: invokevirtual 2178	org/telegram/PhoneFormat/PhoneFormat:format	(Ljava/lang/String;)Ljava/lang/String;
    //   7847: ldc_w 2180
    //   7850: ldc_w 2181
    //   7853: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7856: iconst_1
    //   7857: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   7860: aload_0
    //   7861: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7864: aload_0
    //   7865: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7868: iconst_4
    //   7869: aaload
    //   7870: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7873: aload_0
    //   7874: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7877: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7880: getfield 791	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   7883: ifnull +74 -> 7957
    //   7886: aload_0
    //   7887: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7890: iconst_5
    //   7891: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7894: dup
    //   7895: aload_1
    //   7896: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7899: aastore
    //   7900: aload_0
    //   7901: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7904: iconst_5
    //   7905: aaload
    //   7906: ldc_w 1710
    //   7909: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7912: invokevirtual 2168	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   7915: aload_0
    //   7916: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7919: iconst_5
    //   7920: aaload
    //   7921: aload_0
    //   7922: getfield 403	org/telegram/ui/PaymentFormActivity:validateRequest	Lorg/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   7925: getfield 404	org/telegram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/telegram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   7928: getfield 791	org/telegram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   7931: ldc_w 2183
    //   7934: ldc_w 2184
    //   7937: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7940: iconst_1
    //   7941: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   7944: aload_0
    //   7945: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7948: aload_0
    //   7949: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7952: iconst_5
    //   7953: aaload
    //   7954: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7957: aload_0
    //   7958: getfield 345	org/telegram/ui/PaymentFormActivity:shippingOption	Lorg/telegram/tgnet/TLRPC$TL_shippingOption;
    //   7961: ifnull +75 -> 8036
    //   7964: aload_0
    //   7965: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7968: bipush 6
    //   7970: new 312	org/telegram/ui/Cells/TextDetailSettingsCell
    //   7973: dup
    //   7974: aload_1
    //   7975: invokespecial 2153	org/telegram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7978: aastore
    //   7979: aload_0
    //   7980: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7983: bipush 6
    //   7985: aaload
    //   7986: ldc_w 1710
    //   7989: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7992: invokevirtual 2168	org/telegram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   7995: aload_0
    //   7996: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   7999: bipush 6
    //   8001: aaload
    //   8002: aload_0
    //   8003: getfield 345	org/telegram/ui/PaymentFormActivity:shippingOption	Lorg/telegram/tgnet/TLRPC$TL_shippingOption;
    //   8006: getfield 2095	org/telegram/tgnet/TLRPC$TL_shippingOption:title	Ljava/lang/String;
    //   8009: ldc_w 2186
    //   8012: ldc_w 2187
    //   8015: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   8018: iconst_0
    //   8019: invokevirtual 2160	org/telegram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/CharSequence;Z)V
    //   8022: aload_0
    //   8023: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   8026: aload_0
    //   8027: getfield 314	org/telegram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/telegram/ui/Cells/TextDetailSettingsCell;
    //   8030: bipush 6
    //   8032: aaload
    //   8033: invokevirtual 2105	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   8036: aload_0
    //   8037: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   8040: iconst_4
    //   8041: if_icmpne +367 -> 8408
    //   8044: aload_0
    //   8045: new 1251	android/widget/FrameLayout
    //   8048: dup
    //   8049: aload_1
    //   8050: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   8053: putfield 1447	org/telegram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   8056: aload_0
    //   8057: getfield 1447	org/telegram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   8060: iconst_1
    //   8061: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   8064: invokevirtual 1985	android/widget/FrameLayout:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   8067: aload_3
    //   8068: aload_0
    //   8069: getfield 1447	org/telegram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   8072: iconst_m1
    //   8073: bipush 48
    //   8075: bipush 80
    //   8077: invokestatic 2190	org/telegram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   8080: invokevirtual 1645	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8083: aload_0
    //   8084: getfield 1447	org/telegram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   8087: new 38	org/telegram/ui/PaymentFormActivity$21
    //   8090: dup
    //   8091: aload_0
    //   8092: aload_2
    //   8093: aload 5
    //   8095: invokespecial 2192	org/telegram/ui/PaymentFormActivity$21:<init>	(Lorg/telegram/ui/PaymentFormActivity;Ljava/lang/String;Ljava/lang/String;)V
    //   8098: invokevirtual 2193	android/widget/FrameLayout:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   8101: aload_0
    //   8102: new 1451	android/widget/TextView
    //   8105: dup
    //   8106: aload_1
    //   8107: invokespecial 1775	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   8110: putfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8113: aload_0
    //   8114: getfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8117: ldc_w 2195
    //   8120: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8123: invokevirtual 1779	android/widget/TextView:setTextColor	(I)V
    //   8126: aload_0
    //   8127: getfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8130: ldc_w 2197
    //   8133: ldc_w 2198
    //   8136: iconst_1
    //   8137: anewarray 745	java/lang/Object
    //   8140: dup
    //   8141: iconst_0
    //   8142: aload 5
    //   8144: aastore
    //   8145: invokestatic 1462	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   8148: invokevirtual 1778	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   8151: aload_0
    //   8152: getfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8155: iconst_1
    //   8156: ldc_w 2199
    //   8159: invokevirtual 1780	android/widget/TextView:setTextSize	(IF)V
    //   8162: aload_0
    //   8163: getfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8166: bipush 17
    //   8168: invokevirtual 2200	android/widget/TextView:setGravity	(I)V
    //   8171: aload_0
    //   8172: getfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8175: ldc_w 2202
    //   8178: invokestatic 2206	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   8181: invokevirtual 2207	android/widget/TextView:setTypeface	(Landroid/graphics/Typeface;)V
    //   8184: aload_0
    //   8185: getfield 1447	org/telegram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   8188: aload_0
    //   8189: getfield 696	org/telegram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   8192: iconst_m1
    //   8193: ldc_w 1604
    //   8196: invokestatic 1610	org/telegram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   8199: invokevirtual 1645	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8202: aload_0
    //   8203: new 1423	org/telegram/ui/Components/ContextProgressView
    //   8206: dup
    //   8207: aload_1
    //   8208: iconst_0
    //   8209: invokespecial 1603	org/telegram/ui/Components/ContextProgressView:<init>	(Landroid/content/Context;I)V
    //   8212: putfield 692	org/telegram/ui/PaymentFormActivity:progressViewButton	Lorg/telegram/ui/Components/ContextProgressView;
    //   8215: aload_0
    //   8216: getfield 692	org/telegram/ui/PaymentFormActivity:progressViewButton	Lorg/telegram/ui/Components/ContextProgressView;
    //   8219: iconst_4
    //   8220: invokevirtual 1424	org/telegram/ui/Components/ContextProgressView:setVisibility	(I)V
    //   8223: aload_0
    //   8224: getfield 1447	org/telegram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   8227: aload_0
    //   8228: getfield 692	org/telegram/ui/PaymentFormActivity:progressViewButton	Lorg/telegram/ui/Components/ContextProgressView;
    //   8231: iconst_m1
    //   8232: ldc_w 1604
    //   8235: invokestatic 1610	org/telegram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   8238: invokevirtual 1645	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8241: new 1197	android/view/View
    //   8244: dup
    //   8245: aload_1
    //   8246: invokespecial 1848	android/view/View:<init>	(Landroid/content/Context;)V
    //   8249: astore_2
    //   8250: aload_2
    //   8251: ldc_w 2208
    //   8254: invokevirtual 2211	android/view/View:setBackgroundResource	(I)V
    //   8257: aload_3
    //   8258: aload_2
    //   8259: iconst_m1
    //   8260: ldc_w 2212
    //   8263: bipush 83
    //   8265: fconst_0
    //   8266: fconst_0
    //   8267: fconst_0
    //   8268: ldc_w 1641
    //   8271: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   8274: invokevirtual 1645	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8277: aload_0
    //   8278: getfield 517	org/telegram/ui/PaymentFormActivity:doneItem	Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   8281: iconst_0
    //   8282: invokevirtual 1425	org/telegram/ui/ActionBar/ActionBarMenuItem:setEnabled	(Z)V
    //   8285: aload_0
    //   8286: getfield 517	org/telegram/ui/PaymentFormActivity:doneItem	Lorg/telegram/ui/ActionBar/ActionBarMenuItem;
    //   8289: invokevirtual 1429	org/telegram/ui/ActionBar/ActionBarMenuItem:getImageView	()Landroid/widget/ImageView;
    //   8292: iconst_4
    //   8293: invokevirtual 1445	android/widget/ImageView:setVisibility	(I)V
    //   8296: aload_0
    //   8297: new 42	org/telegram/ui/PaymentFormActivity$22
    //   8300: dup
    //   8301: aload_0
    //   8302: aload_1
    //   8303: invokespecial 2213	org/telegram/ui/PaymentFormActivity$22:<init>	(Lorg/telegram/ui/PaymentFormActivity;Landroid/content/Context;)V
    //   8306: putfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8309: aload_0
    //   8310: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8313: iconst_m1
    //   8314: invokevirtual 2214	android/webkit/WebView:setBackgroundColor	(I)V
    //   8317: aload_0
    //   8318: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8321: invokevirtual 1994	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   8324: iconst_1
    //   8325: invokevirtual 1999	android/webkit/WebSettings:setJavaScriptEnabled	(Z)V
    //   8328: aload_0
    //   8329: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8332: invokevirtual 1994	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   8335: iconst_1
    //   8336: invokevirtual 2002	android/webkit/WebSettings:setDomStorageEnabled	(Z)V
    //   8339: getstatic 900	android/os/Build$VERSION:SDK_INT	I
    //   8342: bipush 21
    //   8344: if_icmplt +25 -> 8369
    //   8347: aload_0
    //   8348: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8351: invokevirtual 1994	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   8354: iconst_0
    //   8355: invokevirtual 2005	android/webkit/WebSettings:setMixedContentMode	(I)V
    //   8358: invokestatic 2010	android/webkit/CookieManager:getInstance	()Landroid/webkit/CookieManager;
    //   8361: aload_0
    //   8362: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8365: iconst_1
    //   8366: invokevirtual 2014	android/webkit/CookieManager:setAcceptThirdPartyCookies	(Landroid/webkit/WebView;Z)V
    //   8369: aload_0
    //   8370: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8373: new 44	org/telegram/ui/PaymentFormActivity$23
    //   8376: dup
    //   8377: aload_0
    //   8378: invokespecial 2215	org/telegram/ui/PaymentFormActivity$23:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   8381: invokevirtual 2027	android/webkit/WebView:setWebViewClient	(Landroid/webkit/WebViewClient;)V
    //   8384: aload_3
    //   8385: aload_0
    //   8386: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8389: iconst_m1
    //   8390: ldc_w 1604
    //   8393: invokestatic 1610	org/telegram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   8396: invokevirtual 1645	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8399: aload_0
    //   8400: getfield 669	org/telegram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   8403: bipush 8
    //   8405: invokevirtual 2216	android/webkit/WebView:setVisibility	(I)V
    //   8408: aload_0
    //   8409: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   8412: iconst_1
    //   8413: new 304	org/telegram/ui/Cells/ShadowSectionCell
    //   8416: dup
    //   8417: aload_1
    //   8418: invokespecial 1844	org/telegram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   8421: aastore
    //   8422: aload_0
    //   8423: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   8426: iconst_1
    //   8427: aaload
    //   8428: aload_1
    //   8429: ldc_w 1561
    //   8432: ldc_w 1550
    //   8435: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   8438: invokevirtual 1560	org/telegram/ui/Cells/ShadowSectionCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   8441: aload_0
    //   8442: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   8445: aload_0
    //   8446: getfield 306	org/telegram/ui/PaymentFormActivity:sectionCell	[Lorg/telegram/ui/Cells/ShadowSectionCell;
    //   8449: iconst_1
    //   8450: aaload
    //   8451: iconst_m1
    //   8452: bipush -2
    //   8454: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   8457: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8460: goto -4884 -> 3576
    //   8463: ldc_w 411
    //   8466: astore_2
    //   8467: goto -960 -> 7507
    //   8470: aload_0
    //   8471: getfield 316	org/telegram/ui/PaymentFormActivity:currentStep	I
    //   8474: bipush 6
    //   8476: if_icmpne -4900 -> 3576
    //   8479: aload_0
    //   8480: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   8483: iconst_2
    //   8484: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   8487: dup
    //   8488: aload_1
    //   8489: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   8492: aastore
    //   8493: aload_0
    //   8494: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   8497: iconst_2
    //   8498: aaload
    //   8499: aload_1
    //   8500: ldc_w 1548
    //   8503: ldc_w 1550
    //   8506: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   8509: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   8512: aload_0
    //   8513: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   8516: aload_0
    //   8517: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   8520: iconst_2
    //   8521: aaload
    //   8522: iconst_m1
    //   8523: bipush -2
    //   8525: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   8528: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8531: aload_0
    //   8532: new 1474	org/telegram/ui/Cells/TextSettingsCell
    //   8535: dup
    //   8536: aload_1
    //   8537: invokespecial 2111	org/telegram/ui/Cells/TextSettingsCell:<init>	(Landroid/content/Context;)V
    //   8540: putfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8543: aload_0
    //   8544: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8547: iconst_1
    //   8548: invokestatic 1914	org/telegram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   8551: invokevirtual 2112	org/telegram/ui/Cells/TextSettingsCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   8554: aload_0
    //   8555: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8558: ldc_w 2218
    //   8561: invokevirtual 2219	org/telegram/ui/Cells/TextSettingsCell:setTag	(Ljava/lang/Object;)V
    //   8564: aload_0
    //   8565: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8568: ldc_w 2218
    //   8571: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8574: invokevirtual 2220	org/telegram/ui/Cells/TextSettingsCell:setTextColor	(I)V
    //   8577: aload_0
    //   8578: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8581: ldc_w 2222
    //   8584: ldc_w 2223
    //   8587: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   8590: iconst_0
    //   8591: invokevirtual 2118	org/telegram/ui/Cells/TextSettingsCell:setText	(Ljava/lang/String;Z)V
    //   8594: aload_0
    //   8595: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   8598: aload_0
    //   8599: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8602: iconst_m1
    //   8603: bipush -2
    //   8605: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   8608: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8611: aload_0
    //   8612: getfield 1472	org/telegram/ui/PaymentFormActivity:settingsCell1	Lorg/telegram/ui/Cells/TextSettingsCell;
    //   8615: new 46	org/telegram/ui/PaymentFormActivity$24
    //   8618: dup
    //   8619: aload_0
    //   8620: invokespecial 2224	org/telegram/ui/PaymentFormActivity$24:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   8623: invokevirtual 2120	org/telegram/ui/Cells/TextSettingsCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   8626: aload_0
    //   8627: iconst_3
    //   8628: anewarray 708	org/telegram/ui/Components/EditTextBoldCursor
    //   8631: putfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8634: iconst_0
    //   8635: istore 8
    //   8637: iload 8
    //   8639: iconst_3
    //   8640: if_icmpge +801 -> 9441
    //   8643: iload 8
    //   8645: ifne +543 -> 9188
    //   8648: aload_0
    //   8649: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   8652: iconst_0
    //   8653: new 298	org/telegram/ui/Cells/HeaderCell
    //   8656: dup
    //   8657: aload_1
    //   8658: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   8661: aastore
    //   8662: aload_0
    //   8663: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   8666: iconst_0
    //   8667: aaload
    //   8668: ldc_w 1710
    //   8671: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8674: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   8677: aload_0
    //   8678: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   8681: iconst_0
    //   8682: aaload
    //   8683: ldc_w 2226
    //   8686: ldc_w 2227
    //   8689: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   8692: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   8695: aload_0
    //   8696: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   8699: aload_0
    //   8700: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   8703: iconst_0
    //   8704: aaload
    //   8705: iconst_m1
    //   8706: bipush -2
    //   8708: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   8711: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8714: new 1251	android/widget/FrameLayout
    //   8717: dup
    //   8718: aload_1
    //   8719: invokespecial 1615	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   8722: astore_2
    //   8723: aload_0
    //   8724: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   8727: aload_2
    //   8728: iconst_m1
    //   8729: bipush 48
    //   8731: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   8734: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8737: aload_2
    //   8738: ldc_w 1710
    //   8741: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8744: invokevirtual 1724	android/view/ViewGroup:setBackgroundColor	(I)V
    //   8747: iload 8
    //   8749: ifne +51 -> 8800
    //   8752: new 1197	android/view/View
    //   8755: dup
    //   8756: aload_1
    //   8757: invokespecial 1848	android/view/View:<init>	(Landroid/content/Context;)V
    //   8760: astore 6
    //   8762: aload_0
    //   8763: getfield 302	org/telegram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   8766: aload 6
    //   8768: invokevirtual 1850	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   8771: pop
    //   8772: aload 6
    //   8774: ldc_w 1852
    //   8777: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8780: invokevirtual 1626	android/view/View:setBackgroundColor	(I)V
    //   8783: aload_2
    //   8784: aload 6
    //   8786: new 1655	android/widget/FrameLayout$LayoutParams
    //   8789: dup
    //   8790: iconst_m1
    //   8791: iconst_1
    //   8792: bipush 83
    //   8794: invokespecial 1855	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   8797: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   8800: aload_0
    //   8801: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8804: iload 8
    //   8806: new 708	org/telegram/ui/Components/EditTextBoldCursor
    //   8809: dup
    //   8810: aload_1
    //   8811: invokespecial 1856	org/telegram/ui/Components/EditTextBoldCursor:<init>	(Landroid/content/Context;)V
    //   8814: aastore
    //   8815: aload_0
    //   8816: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8819: iload 8
    //   8821: aaload
    //   8822: iload 8
    //   8824: invokestatic 362	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8827: invokevirtual 1731	org/telegram/ui/Components/EditTextBoldCursor:setTag	(Ljava/lang/Object;)V
    //   8830: aload_0
    //   8831: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8834: iload 8
    //   8836: aaload
    //   8837: iconst_1
    //   8838: ldc_w 1732
    //   8841: invokevirtual 1736	org/telegram/ui/Components/EditTextBoldCursor:setTextSize	(IF)V
    //   8844: aload_0
    //   8845: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8848: iload 8
    //   8850: aaload
    //   8851: ldc_w 1738
    //   8854: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8857: invokevirtual 1741	org/telegram/ui/Components/EditTextBoldCursor:setHintTextColor	(I)V
    //   8860: aload_0
    //   8861: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8864: iload 8
    //   8866: aaload
    //   8867: ldc_w 1743
    //   8870: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8873: invokevirtual 1746	org/telegram/ui/Components/EditTextBoldCursor:setTextColor	(I)V
    //   8876: aload_0
    //   8877: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8880: iload 8
    //   8882: aaload
    //   8883: aconst_null
    //   8884: invokevirtual 1747	org/telegram/ui/Components/EditTextBoldCursor:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   8887: aload_0
    //   8888: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8891: iload 8
    //   8893: aaload
    //   8894: ldc_w 1743
    //   8897: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   8900: invokevirtual 1750	org/telegram/ui/Components/EditTextBoldCursor:setCursorColor	(I)V
    //   8903: aload_0
    //   8904: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8907: iload 8
    //   8909: aaload
    //   8910: ldc_w 1751
    //   8913: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8916: invokevirtual 1754	org/telegram/ui/Components/EditTextBoldCursor:setCursorSize	(I)V
    //   8919: aload_0
    //   8920: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8923: iload 8
    //   8925: aaload
    //   8926: ldc_w 1755
    //   8929: invokevirtual 1759	org/telegram/ui/Components/EditTextBoldCursor:setCursorWidth	(F)V
    //   8932: iload 8
    //   8934: ifeq +9 -> 8943
    //   8937: iload 8
    //   8939: iconst_1
    //   8940: if_icmpne +323 -> 9263
    //   8943: aload_0
    //   8944: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8947: iload 8
    //   8949: aaload
    //   8950: sipush 129
    //   8953: invokevirtual 1767	org/telegram/ui/Components/EditTextBoldCursor:setInputType	(I)V
    //   8956: aload_0
    //   8957: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8960: iload 8
    //   8962: aaload
    //   8963: getstatic 2048	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   8966: invokevirtual 2052	org/telegram/ui/Components/EditTextBoldCursor:setTypeface	(Landroid/graphics/Typeface;)V
    //   8969: aload_0
    //   8970: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   8973: iload 8
    //   8975: aaload
    //   8976: ldc_w 1768
    //   8979: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   8982: iload 8
    //   8984: tableswitch	default:+28->9012, 0:+295->9279, 1:+328->9312, 2:+350->9334
    //   9012: aload_0
    //   9013: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9016: iload 8
    //   9018: aaload
    //   9019: iconst_0
    //   9020: iconst_0
    //   9021: iconst_0
    //   9022: ldc_w 1783
    //   9025: invokestatic 1594	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9028: invokevirtual 1792	org/telegram/ui/Components/EditTextBoldCursor:setPadding	(IIII)V
    //   9031: aload_0
    //   9032: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9035: iload 8
    //   9037: aaload
    //   9038: astore 6
    //   9040: getstatic 1894	org/telegram/messenger/LocaleController:isRTL	Z
    //   9043: ifeq +313 -> 9356
    //   9046: iconst_5
    //   9047: istore 9
    //   9049: aload 6
    //   9051: iload 9
    //   9053: invokevirtual 1795	org/telegram/ui/Components/EditTextBoldCursor:setGravity	(I)V
    //   9056: aload_2
    //   9057: aload_0
    //   9058: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9061: iload 8
    //   9063: aaload
    //   9064: iconst_m1
    //   9065: ldc_w 1895
    //   9068: bipush 51
    //   9070: ldc_w 1781
    //   9073: ldc_w 1782
    //   9076: ldc_w 1781
    //   9079: ldc_w 1783
    //   9082: invokestatic 1644	org/telegram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   9085: invokevirtual 1787	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   9088: aload_0
    //   9089: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9092: iload 8
    //   9094: aaload
    //   9095: new 50	org/telegram/ui/PaymentFormActivity$25
    //   9098: dup
    //   9099: aload_0
    //   9100: invokespecial 2228	org/telegram/ui/PaymentFormActivity$25:<init>	(Lorg/telegram/ui/PaymentFormActivity;)V
    //   9103: invokevirtual 1815	org/telegram/ui/Components/EditTextBoldCursor:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   9106: iload 8
    //   9108: iconst_1
    //   9109: if_icmpne +253 -> 9362
    //   9112: aload_0
    //   9113: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9116: iconst_0
    //   9117: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   9120: dup
    //   9121: aload_1
    //   9122: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   9125: aastore
    //   9126: aload_0
    //   9127: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9130: iconst_0
    //   9131: aaload
    //   9132: ldc_w 2230
    //   9135: ldc_w 2231
    //   9138: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9141: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   9144: aload_0
    //   9145: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9148: iconst_0
    //   9149: aaload
    //   9150: aload_1
    //   9151: ldc_w 1548
    //   9154: ldc_w 1550
    //   9157: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   9160: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   9163: aload_0
    //   9164: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   9167: aload_0
    //   9168: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9171: iconst_0
    //   9172: aaload
    //   9173: iconst_m1
    //   9174: bipush -2
    //   9176: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   9179: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   9182: iinc 8 1
    //   9185: goto -548 -> 8637
    //   9188: iload 8
    //   9190: iconst_2
    //   9191: if_icmpne -477 -> 8714
    //   9194: aload_0
    //   9195: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   9198: iconst_1
    //   9199: new 298	org/telegram/ui/Cells/HeaderCell
    //   9202: dup
    //   9203: aload_1
    //   9204: invokespecial 1708	org/telegram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   9207: aastore
    //   9208: aload_0
    //   9209: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   9212: iconst_1
    //   9213: aaload
    //   9214: ldc_w 1710
    //   9217: invokestatic 1625	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   9220: invokevirtual 1711	org/telegram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   9223: aload_0
    //   9224: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   9227: iconst_1
    //   9228: aaload
    //   9229: ldc_w 2233
    //   9232: ldc_w 2234
    //   9235: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9238: invokevirtual 1716	org/telegram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   9241: aload_0
    //   9242: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   9245: aload_0
    //   9246: getfield 300	org/telegram/ui/PaymentFormActivity:headerCell	[Lorg/telegram/ui/Cells/HeaderCell;
    //   9249: iconst_1
    //   9250: aaload
    //   9251: iconst_m1
    //   9252: bipush -2
    //   9254: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   9257: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   9260: goto -546 -> 8714
    //   9263: aload_0
    //   9264: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9267: iload 8
    //   9269: aaload
    //   9270: ldc_w 1938
    //   9273: invokevirtual 1771	org/telegram/ui/Components/EditTextBoldCursor:setImeOptions	(I)V
    //   9276: goto -294 -> 8982
    //   9279: aload_0
    //   9280: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9283: iload 8
    //   9285: aaload
    //   9286: ldc_w 2236
    //   9289: ldc_w 2237
    //   9292: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9295: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   9298: aload_0
    //   9299: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9302: iload 8
    //   9304: aaload
    //   9305: invokevirtual 2126	org/telegram/ui/Components/EditTextBoldCursor:requestFocus	()Z
    //   9308: pop
    //   9309: goto -297 -> 9012
    //   9312: aload_0
    //   9313: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9316: iload 8
    //   9318: aaload
    //   9319: ldc_w 2239
    //   9322: ldc_w 2240
    //   9325: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9328: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   9331: goto -319 -> 9012
    //   9334: aload_0
    //   9335: getfield 492	org/telegram/ui/PaymentFormActivity:inputFields	[Lorg/telegram/ui/Components/EditTextBoldCursor;
    //   9338: iload 8
    //   9340: aaload
    //   9341: ldc_w 2242
    //   9344: ldc_w 2243
    //   9347: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9350: invokevirtual 1862	org/telegram/ui/Components/EditTextBoldCursor:setHint	(Ljava/lang/CharSequence;)V
    //   9353: goto -341 -> 9012
    //   9356: iconst_3
    //   9357: istore 9
    //   9359: goto -310 -> 9049
    //   9362: iload 8
    //   9364: iconst_2
    //   9365: if_icmpne -183 -> 9182
    //   9368: aload_0
    //   9369: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9372: iconst_1
    //   9373: new 308	org/telegram/ui/Cells/TextInfoPrivacyCell
    //   9376: dup
    //   9377: aload_1
    //   9378: invokespecial 1905	org/telegram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   9381: aastore
    //   9382: aload_0
    //   9383: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9386: iconst_1
    //   9387: aaload
    //   9388: ldc_w 1497
    //   9391: ldc_w 1498
    //   9394: invokestatic 1135	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9397: invokevirtual 1495	org/telegram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   9400: aload_0
    //   9401: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9404: iconst_1
    //   9405: aaload
    //   9406: aload_1
    //   9407: ldc_w 1561
    //   9410: ldc_w 1550
    //   9413: invokestatic 1556	org/telegram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   9416: invokevirtual 1906	org/telegram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   9419: aload_0
    //   9420: getfield 1650	org/telegram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   9423: aload_0
    //   9424: getfield 310	org/telegram/ui/PaymentFormActivity:bottomCell	[Lorg/telegram/ui/Cells/TextInfoPrivacyCell;
    //   9427: iconst_1
    //   9428: aaload
    //   9429: iconst_m1
    //   9430: bipush -2
    //   9432: invokestatic 1720	org/telegram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   9435: invokevirtual 1721	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   9438: goto -256 -> 9182
    //   9441: aload_0
    //   9442: invokespecial 642	org/telegram/ui/PaymentFormActivity:updatePasswordFields	()V
    //   9445: goto -5869 -> 3576
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	9448	0	this	PaymentFormActivity
    //   0	9448	1	paramContext	Context
    //   63	308	2	localObject1	Object
    //   488	2	2	localException1	Exception
    //   613	3223	2	localObject2	Object
    //   4256	2	2	localException2	Exception
    //   4274	1	2	localException3	Exception
    //   4296	725	2	localObject3	Object
    //   5042	2	2	localException4	Exception
    //   5070	596	2	localException5	Exception
    //   5872	3185	2	localObject4	Object
    //   188	8197	3	localObject5	Object
    //   251	1240	4	f	float
    //   335	7808	5	localObject6	Object
    //   344	3471	6	localObject7	Object
    //   4246	1	6	localException6	Exception
    //   4264	1	6	localException7	Exception
    //   4657	309	6	localObject8	Object
    //   5032	1	6	localException8	Exception
    //   5050	1	6	localException9	Exception
    //   5060	1	6	localException10	Exception
    //   5819	3231	6	localObject9	Object
    //   518	6400	7	i	int
    //   1166	8200	8	j	int
    //   1685	7673	9	k	int
    //   2381	4797	10	localObject10	Object
    //   5911	64	11	bool1	boolean
    //   5923	58	12	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   337	370	488	java/lang/Exception
    //   370	376	488	java/lang/Exception
    //   381	472	488	java/lang/Exception
    //   472	485	488	java/lang/Exception
    //   1495	1499	488	java/lang/Exception
    //   3496	3510	3750	java/lang/Exception
    //   3517	3526	3750	java/lang/Exception
    //   3796	3819	4246	java/lang/Exception
    //   3778	3796	4256	java/lang/Exception
    //   4248	4253	4256	java/lang/Exception
    //   4266	4271	4256	java/lang/Exception
    //   4275	4280	4256	java/lang/Exception
    //   3819	3834	4264	java/lang/Exception
    //   3834	3845	4274	java/lang/Exception
    //   4311	4322	5032	java/lang/Exception
    //   4293	4311	5042	java/lang/Exception
    //   5034	5039	5042	java/lang/Exception
    //   5052	5057	5042	java/lang/Exception
    //   5062	5067	5042	java/lang/Exception
    //   5071	5078	5042	java/lang/Exception
    //   4322	4333	5050	java/lang/Exception
    //   4333	4344	5060	java/lang/Exception
    //   4344	4355	5070	java/lang/Exception
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.didSetTwoStepPassword)
    {
      this.paymentForm.password_missing = false;
      this.paymentForm.can_save_credentials = true;
      updateSavePaymentField();
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.didRemovedTwoStepPassword)
      {
        this.paymentForm.password_missing = true;
        this.paymentForm.can_save_credentials = false;
        updateSavePaymentField();
      }
      else if (paramInt1 == NotificationCenter.paymentFinished)
      {
        removeSelfFromStack();
      }
    }
  }
  
  @SuppressLint({"HardwareIds"})
  public void fillNumber(String paramString)
  {
    for (;;)
    {
      try
      {
        TelephonyManager localTelephonyManager = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
        i = 1;
        j = 1;
        if ((paramString != null) || ((localTelephonyManager.getSimState() != 1) && (localTelephonyManager.getPhoneType() != 0)))
        {
          if (Build.VERSION.SDK_INT >= 23)
          {
            if (getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0) {
              continue;
            }
            i = 1;
            if (getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0) {
              continue;
            }
            j = 1;
          }
          if ((paramString != null) || (i != 0) || (j != 0))
          {
            String str1 = paramString;
            if (paramString == null) {
              str1 = PhoneFormat.stripExceptNumbers(localTelephonyManager.getLine1Number());
            }
            paramString = null;
            localTelephonyManager = null;
            int k = 0;
            if (!TextUtils.isEmpty(str1))
            {
              if (str1.length() > 4)
              {
                i = 4;
                j = k;
                paramString = localTelephonyManager;
                if (i >= 1)
                {
                  String str2 = str1.substring(0, i);
                  if ((String)this.codesMap.get(str2) == null) {
                    continue;
                  }
                  j = 1;
                  paramString = str1.substring(i, str1.length());
                  this.inputFields[8].setText(str2);
                }
                if (j == 0)
                {
                  paramString = str1.substring(1, str1.length());
                  this.inputFields[8].setText(str1.substring(0, 1));
                }
              }
              if (paramString != null)
              {
                this.inputFields[9].setText(paramString);
                this.inputFields[9].setSelection(this.inputFields[9].length());
              }
            }
          }
        }
        return;
      }
      catch (Exception paramString)
      {
        int i;
        int j;
        FileLog.e(paramString);
        continue;
      }
      i = 0;
      continue;
      j = 0;
      continue;
      i--;
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
    localArrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
    LinearLayout localLinearLayout = this.linearLayout2;
    Paint localPaint = Theme.dividerPaint;
    localArrayList.add(new ThemeDescription(localLinearLayout, 0, new Class[] { View.class }, localPaint, null, null, "divider"));
    localArrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"));
    localArrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2"));
    localArrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, "contextProgressInner2"));
    localArrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, "contextProgressOuter2"));
    if (this.inputFields != null) {
      for (i = 0; i < this.inputFields.length; i++)
      {
        localArrayList.add(new ThemeDescription((View)this.inputFields[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        localArrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        localArrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
      }
    }
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
    if (this.radioCells != null) {
      for (i = 0; i < this.radioCells.length; i++)
      {
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"));
      }
    }
    localArrayList.add(new ThemeDescription(null, 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"));
    for (int i = 0; i < this.headerCell.length; i++)
    {
      localArrayList.add(new ThemeDescription(this.headerCell[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
      localArrayList.add(new ThemeDescription(this.headerCell[i], 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"));
    }
    for (i = 0; i < this.sectionCell.length; i++) {
      localArrayList.add(new ThemeDescription(this.sectionCell[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"));
    }
    for (i = 0; i < this.bottomCell.length; i++)
    {
      localArrayList.add(new ThemeDescription(this.bottomCell[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"));
      localArrayList.add(new ThemeDescription(this.bottomCell[i], 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"));
      localArrayList.add(new ThemeDescription(this.bottomCell[i], ThemeDescription.FLAG_LINKCOLOR, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteLinkText"));
    }
    for (i = 0; i < this.dividers.size(); i++) {
      localArrayList.add(new ThemeDescription((View)this.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"));
    }
    localArrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"));
    localArrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    localArrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    localArrayList.add(new ThemeDescription(this.settingsCell1, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText6"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextPriceCell.class }, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    localArrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    for (i = 1; i < this.detailSettingsCell.length; i++)
    {
      localArrayList.add(new ThemeDescription(this.detailSettingsCell[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
      localArrayList.add(new ThemeDescription(this.detailSettingsCell[i], 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
      localArrayList.add(new ThemeDescription(this.detailSettingsCell[i], 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    }
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[] { PaymentInfoCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[] { PaymentInfoCell.class }, new String[] { "detailTextView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[] { PaymentInfoCell.class }, new String[] { "detailExTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    localArrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    return (ThemeDescription[])localArrayList.toArray(new ThemeDescription[localArrayList.size()]);
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (paramInt1 == 1000) {
      if (paramInt2 == -1)
      {
        showEditDoneProgress(true, true);
        setDonePressed(true);
        localObject1 = (MaskedWallet)paramIntent.getParcelableExtra("com.google.android.gms.wallet.EXTRA_MASKED_WALLET");
        Cart.Builder localBuilder = Cart.newBuilder().setCurrencyCode(this.paymentForm.invoice.currency).setTotalPrice(this.totalPriceDecimal);
        paramIntent = new ArrayList();
        paramIntent.addAll(this.paymentForm.invoice.prices);
        if (this.shippingOption != null) {
          paramIntent.addAll(this.shippingOption.prices);
        }
        for (paramInt1 = 0; paramInt1 < paramIntent.size(); paramInt1++)
        {
          localObject2 = (TLRPC.TL_labeledPrice)paramIntent.get(paramInt1);
          localObject3 = LocaleController.getInstance().formatCurrencyDecimalString(((TLRPC.TL_labeledPrice)localObject2).amount, this.paymentForm.invoice.currency, false);
          localBuilder.addLineItem(LineItem.newBuilder().setCurrencyCode(this.paymentForm.invoice.currency).setQuantity("1").setDescription(((TLRPC.TL_labeledPrice)localObject2).label).setTotalPrice((String)localObject3).setUnitPrice((String)localObject3).build());
        }
        paramIntent = FullWalletRequest.newBuilder().setCart(localBuilder.build()).setGoogleTransactionId(((MaskedWallet)localObject1).getGoogleTransactionId()).build();
        Wallet.Payments.loadFullWallet(this.googleApiClient, paramIntent, 1001);
      }
    }
    for (;;)
    {
      return;
      showEditDoneProgress(true, false);
      setDonePressed(false);
      continue;
      if (paramInt1 == 1001)
      {
        if (paramInt2 == -1)
        {
          paramIntent = (FullWallet)paramIntent.getParcelableExtra("com.google.android.gms.wallet.EXTRA_FULL_WALLET");
          localObject3 = paramIntent.getPaymentMethodToken().getToken();
          for (;;)
          {
            try
            {
              if (this.androidPayPublicKey == null) {
                break label416;
              }
              localObject1 = new org/telegram/tgnet/TLRPC$TL_inputPaymentCredentialsAndroidPay;
              ((TLRPC.TL_inputPaymentCredentialsAndroidPay)localObject1).<init>();
              this.androidPayCredentials = ((TLRPC.TL_inputPaymentCredentialsAndroidPay)localObject1);
              localObject1 = this.androidPayCredentials;
              localObject2 = new org/telegram/tgnet/TLRPC$TL_dataJSON;
              ((TLRPC.TL_dataJSON)localObject2).<init>();
              ((TLRPC.TL_inputPaymentCredentialsAndroidPay)localObject1).payment_token = ((TLRPC.TL_dataJSON)localObject2);
              this.androidPayCredentials.payment_token.data = ((String)localObject3);
              this.androidPayCredentials.google_transaction_id = paramIntent.getGoogleTransactionId();
              paramIntent = paramIntent.getPaymentDescriptions();
              if (paramIntent.length <= 0) {
                break label406;
              }
              this.cardName = paramIntent[0];
              goToNextStep();
              showEditDoneProgress(true, false);
              setDonePressed(false);
            }
            catch (JSONException paramIntent)
            {
              showEditDoneProgress(true, false);
              setDonePressed(false);
            }
            break;
            label406:
            this.cardName = "Android Pay";
            continue;
            label416:
            paramIntent = TokenParser.parseToken((String)localObject3);
            this.paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[] { paramIntent.getType(), paramIntent.getId() });
            localObject3 = paramIntent.getCard();
            paramIntent = new java/lang/StringBuilder;
            paramIntent.<init>();
            this.cardName = (((Card)localObject3).getType() + " *" + ((Card)localObject3).getLast4());
          }
        }
        showEditDoneProgress(true, false);
        setDonePressed(false);
      }
    }
  }
  
  public boolean onBackPressed()
  {
    if (!this.donePressed) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemovedTwoStepPassword);
    if (this.currentStep != 4) {
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentFinished);
    }
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    if (this.delegate != null) {
      this.delegate.onFragmentDestroyed();
    }
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemovedTwoStepPassword);
    if (this.currentStep != 4) {
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
    }
    if (this.webView != null) {}
    for (;;)
    {
      try
      {
        ViewParent localViewParent = this.webView.getParent();
        if (localViewParent != null) {
          ((FrameLayout)localViewParent).removeView(this.webView);
        }
        this.webView.stopLoading();
        this.webView.loadUrl("about:blank");
        this.webView.destroy();
        this.webView = null;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        continue;
      }
      try
      {
        if (((this.currentStep == 2) || (this.currentStep == 6)) && (Build.VERSION.SDK_INT >= 23) && ((SharedConfig.passcodeHash.length() == 0) || (SharedConfig.allowScreenCapture))) {
          getParentActivity().getWindow().clearFlags(8192);
        }
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
      }
    }
    super.onFragmentDestroy();
    this.canceled = true;
  }
  
  public void onPause()
  {
    if (this.googleApiClient != null) {
      this.googleApiClient.disconnect();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    if (Build.VERSION.SDK_INT >= 23) {}
    for (;;)
    {
      try
      {
        if (((this.currentStep != 2) && (this.currentStep != 6)) || (this.paymentForm.invoice.test)) {
          continue;
        }
        getParentActivity().getWindow().setFlags(8192, 8192);
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
        continue;
      }
      if (this.googleApiClient != null) {
        this.googleApiClient.connect();
      }
      return;
      if ((SharedConfig.passcodeHash.length() == 0) || (SharedConfig.allowScreenCapture)) {
        getParentActivity().getWindow().clearFlags(8192);
      }
    }
  }
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2))
    {
      if (this.webView == null) {
        break label38;
      }
      if (this.currentStep != 4) {
        this.webView.loadUrl(this.paymentForm.url);
      }
    }
    for (;;)
    {
      return;
      label38:
      if (this.currentStep == 2)
      {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
      }
      else if (this.currentStep == 3)
      {
        this.inputFields[1].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[1]);
      }
      else if ((this.currentStep == 6) && (!this.waitingForEmail))
      {
        this.inputFields[0].requestFocus();
        AndroidUtilities.showKeyboard(this.inputFields[0]);
      }
    }
  }
  
  private static class LinkMovementMethodMy
    extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool1 = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        if (paramMotionEvent.getAction() != 1)
        {
          bool2 = bool1;
          if (paramMotionEvent.getAction() != 3) {}
        }
        else
        {
          Selection.removeSelection(paramSpannable);
          bool2 = bool1;
        }
      }
      catch (Exception paramTextView)
      {
        for (;;)
        {
          FileLog.e(paramTextView);
          boolean bool2 = false;
        }
      }
      return bool2;
    }
  }
  
  public class LinkSpan
    extends ClickableSpan
  {
    public LinkSpan() {}
    
    public void onClick(View paramView)
    {
      PaymentFormActivity.this.presentFragment(new TwoStepVerificationActivity(0));
    }
    
    public void updateDrawState(TextPaint paramTextPaint)
    {
      super.updateDrawState(paramTextPaint);
      paramTextPaint.setUnderlineText(false);
    }
  }
  
  private static abstract interface PaymentFormActivityDelegate
  {
    public abstract void currentPasswordUpdated(TLRPC.account_Password paramaccount_Password);
    
    public abstract boolean didSelectNewCard(String paramString1, String paramString2, boolean paramBoolean, TLRPC.TL_inputPaymentCredentialsAndroidPay paramTL_inputPaymentCredentialsAndroidPay);
    
    public abstract void onFragmentDestroyed();
  }
  
  private class TelegramWebviewProxy
  {
    private TelegramWebviewProxy() {}
    
    @JavascriptInterface
    public void postEvent(final String paramString1, final String paramString2)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (PaymentFormActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            if (!paramString1.equals("payment_form_submit")) {
              continue;
            }
            try
            {
              JSONObject localJSONObject1 = new org/json/JSONObject;
              localJSONObject1.<init>(paramString2);
              JSONObject localJSONObject2 = localJSONObject1.getJSONObject("credentials");
              PaymentFormActivity.access$002(PaymentFormActivity.this, localJSONObject2.toString());
              PaymentFormActivity.access$102(PaymentFormActivity.this, localJSONObject1.getString("title"));
              PaymentFormActivity.this.goToNextStep();
            }
            catch (Throwable localThrowable)
            {
              for (;;)
              {
                PaymentFormActivity.access$002(PaymentFormActivity.this, paramString2);
                FileLog.e(localThrowable);
              }
            }
          }
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PaymentFormActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */