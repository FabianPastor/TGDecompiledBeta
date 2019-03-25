package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.FileProvider;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import javax.crypto.Cipher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SecureDocumentKey;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SmsReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputSecureFile;
import org.telegram.tgnet.TLRPC.PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.SecureFile;
import org.telegram.tgnet.TLRPC.SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC.SecurePlainData;
import org.telegram.tgnet.TLRPC.SecureRequiredType;
import org.telegram.tgnet.TLRPC.SecureValueError;
import org.telegram.tgnet.TLRPC.SecureValueType;
import org.telegram.tgnet.TLRPC.TL_account_acceptAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_deleteSecureValue;
import org.telegram.tgnet.TLRPC.TL_account_getAllSecureValues;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_passwordSettings;
import org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyEmailCode;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC.TL_account_sentEmailCode;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_codeSettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getPassportConfig;
import org.telegram.tgnet.TLRPC.TL_help_passportConfig;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_inputSecureFile;
import org.telegram.tgnet.TLRPC.TL_inputSecureFileUploaded;
import org.telegram.tgnet.TLRPC.TL_inputSecureValue;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC.TL_secureCredentialsEncrypted;
import org.telegram.tgnet.TLRPC.TL_secureData;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoSHA512;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC.TL_securePlainEmail;
import org.telegram.tgnet.TLRPC.TL_securePlainPhone;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf;
import org.telegram.tgnet.TLRPC.TL_secureSecretSettings;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.tgnet.TLRPC.TL_secureValueError;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorData;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorFile;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorFiles;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorFrontSide;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorReverseSide;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorSelfie;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorTranslationFile;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorTranslationFiles;
import org.telegram.tgnet.TLRPC.TL_secureValueHash;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeBankStatement;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeIdentityCard;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeInternalPassport;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePassport;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePassportRegistration;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeRentalAgreement;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeTemporaryRegistration;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeUtilityBill;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PassportActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int FIELD_ADDRESS_COUNT = 6;
    private static final int FIELD_BIRTHDAY = 3;
    private static final int FIELD_CARDNUMBER = 7;
    private static final int FIELD_CITIZENSHIP = 5;
    private static final int FIELD_CITY = 3;
    private static final int FIELD_COUNTRY = 5;
    private static final int FIELD_EMAIL = 0;
    private static final int FIELD_EXPIRE = 8;
    private static final int FIELD_GENDER = 4;
    private static final int FIELD_IDENTITY_COUNT = 9;
    private static final int FIELD_IDENTITY_NODOC_COUNT = 7;
    private static final int FIELD_MIDNAME = 1;
    private static final int FIELD_NAME = 0;
    private static final int FIELD_NATIVE_COUNT = 3;
    private static final int FIELD_NATIVE_MIDNAME = 1;
    private static final int FIELD_NATIVE_NAME = 0;
    private static final int FIELD_NATIVE_SURNAME = 2;
    private static final int FIELD_PASSWORD = 0;
    private static final int FIELD_PHONE = 2;
    private static final int FIELD_PHONECODE = 1;
    private static final int FIELD_PHONECOUNTRY = 0;
    private static final int FIELD_POSTCODE = 2;
    private static final int FIELD_RESIDENCE = 6;
    private static final int FIELD_STATE = 4;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int FIELD_SURNAME = 2;
    public static final int TYPE_ADDRESS = 2;
    public static final int TYPE_EMAIL = 4;
    public static final int TYPE_EMAIL_VERIFICATION = 6;
    public static final int TYPE_IDENTITY = 1;
    public static final int TYPE_MANAGE = 8;
    public static final int TYPE_PASSWORD = 5;
    public static final int TYPE_PHONE = 3;
    public static final int TYPE_PHONE_VERIFICATION = 7;
    public static final int TYPE_REQUEST = 0;
    private static final int UPLOADING_TYPE_DOCUMENTS = 0;
    private static final int UPLOADING_TYPE_FRONT = 2;
    private static final int UPLOADING_TYPE_REVERSE = 3;
    private static final int UPLOADING_TYPE_SELFIE = 1;
    private static final int UPLOADING_TYPE_TRANSLATION = 4;
    private static final int attach_document = 4;
    private static final int attach_gallery = 1;
    private static final int attach_photo = 0;
    private static final int done_button = 2;
    private static final int info_item = 1;
    private TextView acceptTextView;
    private TextSettingsCell addDocumentCell;
    private ShadowSectionCell addDocumentSectionCell;
    private boolean allowNonLatinName;
    private ArrayList<TL_secureRequiredType> availableDocumentTypes;
    private TextInfoPrivacyCell bottomCell;
    private TextInfoPrivacyCell bottomCellTranslation;
    private FrameLayout bottomLayout;
    private boolean callbackCalled;
    private ChatAttachAlert chatAttachAlert;
    private HashMap<String, String> codesMap;
    private ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private int currentActivityType;
    private int currentBotId;
    private String currentCallbackUrl;
    private String currentCitizeship;
    private HashMap<String, String> currentDocumentValues;
    private TL_secureRequiredType currentDocumentsType;
    private TL_secureValue currentDocumentsTypeValue;
    private String currentEmail;
    private int[] currentExpireDate;
    private TL_account_authorizationForm currentForm;
    private String currentGender;
    private String currentNonce;
    private TL_account_password currentPassword;
    private String currentPayload;
    private TL_auth_sentCode currentPhoneVerification;
    private LinearLayout currentPhotoViewerLayout;
    private String currentPicturePath;
    private String currentPublicKey;
    private String currentResidence;
    private String currentScope;
    private TL_secureRequiredType currentType;
    private TL_secureValue currentTypeValue;
    private HashMap<String, String> currentValues;
    private int currentViewNum;
    private PassportActivityDelegate delegate;
    private TextSettingsCell deletePassportCell;
    private ArrayList<View> dividers;
    private boolean documentOnly;
    private ArrayList<SecureDocument> documents;
    private HashMap<SecureDocument, SecureDocumentCell> documentsCells;
    private HashMap<String, String> documentsErrors;
    private LinearLayout documentsLayout;
    private HashMap<TL_secureRequiredType, TL_secureRequiredType> documentsToTypesLink;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private int emailCodeLength;
    private ImageView emptyImageView;
    private LinearLayout emptyLayout;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private TextView emptyTextView3;
    private EmptyTextProgressView emptyView;
    private HashMap<String, HashMap<String, String>> errorsMap;
    private HashMap<String, String> errorsValues;
    private View extraBackgroundView;
    private View extraBackgroundView2;
    private HashMap<String, String> fieldsErrors;
    private SecureDocument frontDocument;
    private LinearLayout frontLayout;
    private HeaderCell headerCell;
    private boolean ignoreOnFailure;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private String initialValues;
    private EditTextBoldCursor[] inputExtraFields;
    private ViewGroup[] inputFieldContainers;
    private EditTextBoldCursor[] inputFields;
    private HashMap<String, String> languageMap;
    private LinearLayout linearLayout2;
    private HashMap<String, String> mainErrorsMap;
    private TextInfoPrivacyCell nativeInfoCell;
    private boolean needActivityResult;
    private CharSequence noAllDocumentsErrorText;
    private CharSequence noAllTranslationErrorText;
    private ImageView noPasswordImageView;
    private TextView noPasswordSetTextView;
    private TextView noPasswordTextView;
    private boolean[] nonLatinNames;
    private FrameLayout passwordAvatarContainer;
    private TextView passwordForgotButton;
    private TextInfoPrivacyCell passwordInfoRequestTextView;
    private TextInfoPrivacyCell passwordRequestTextView;
    private PassportActivityDelegate pendingDelegate;
    private ErrorRunnable pendingErrorRunnable;
    private Runnable pendingFinishRunnable;
    private String pendingPhone;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private HashMap<String, String> phoneFormatMap;
    private TextView plusTextView;
    private PassportActivity presentAfterAnimation;
    private AlertDialog progressDialog;
    private ContextProgressView progressView;
    private ContextProgressView progressViewButton;
    private PhotoViewerProvider provider;
    private SecureDocument reverseDocument;
    private LinearLayout reverseLayout;
    private byte[] saltedPassword;
    private byte[] savedPasswordHash;
    private byte[] savedSaltedPassword;
    private TextSettingsCell scanDocumentCell;
    private int scrollHeight;
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private ShadowSectionCell sectionCell2;
    private byte[] secureSecret;
    private long secureSecretId;
    private SecureDocument selfieDocument;
    private LinearLayout selfieLayout;
    private TextInfoPrivacyCell topErrorCell;
    private ArrayList<SecureDocument> translationDocuments;
    private LinearLayout translationLayout;
    private HashMap<TL_secureRequiredType, HashMap<String, String>> typesValues;
    private HashMap<TL_secureRequiredType, TextDetailSecureCell> typesViews;
    private TextSettingsCell uploadDocumentCell;
    private TextDetailSettingsCell uploadFrontCell;
    private TextDetailSettingsCell uploadReverseCell;
    private TextDetailSettingsCell uploadSelfieCell;
    private TextSettingsCell uploadTranslationCell;
    private HashMap<String, SecureDocument> uploadingDocuments;
    private int uploadingFileType;
    private boolean useCurrentValue;
    private int usingSavedPassword;
    private SlideView[] views;

    private interface ErrorRunnable {
        void onError(String str, String str2);
    }

    private interface PassportActivityDelegate {
        void deleteValue(TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, ArrayList<TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TL_secureFile tL_secureFile);

        void saveValue(TL_secureRequiredType tL_secureRequiredType, String str, String str2, TL_secureRequiredType tL_secureRequiredType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, ArrayList<SecureDocument> arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
    }

    /* renamed from: org.telegram.ui.PassportActivity$1ValueToSend */
    class AnonymousClass1ValueToSend {
        boolean selfie_required;
        boolean translation_required;
        TL_secureValue value;

        public AnonymousClass1ValueToSend(TL_secureValue v, boolean s, boolean t) {
            this.value = v;
            this.selfie_required = s;
            this.translation_required = t;
        }
    }

    private class EncryptionResult {
        byte[] decrypyedFileSecret;
        byte[] encryptedData;
        byte[] fileHash;
        byte[] fileSecret;
        SecureDocumentKey secureDocumentKey;

        public EncryptionResult(byte[] d, byte[] fs, byte[] dfs, byte[] fh, byte[] fk, byte[] fi) {
            this.encryptedData = d;
            this.fileSecret = fs;
            this.fileHash = fh;
            this.decrypyedFileSecret = dfs;
            this.secureDocumentKey = new SecureDocumentKey(fk, fi);
        }
    }

    public class LinkSpan extends ClickableSpan {
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
            ds.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }

        public void onClick(View widget) {
            Browser.openUrl(PassportActivity.this.getParentActivity(), PassportActivity.this.currentForm.privacy_policy_url);
        }
    }

    public class PhoneConfirmationView extends SlideView implements NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        private EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        private int codeTime = 15000;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private boolean ignoreOnTextChange;
        private double lastCodeTime;
        private double lastCurrentTime;
        private String lastError = "";
        private int length;
        private boolean nextPressed;
        private int nextType;
        private int openTime;
        private String pattern = "*";
        private String phone;
        private String phoneHash;
        private TextView problemText;
        private ProgressView progressView;
        private int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private int verificationType;
        private boolean waitingForEvent;

        public PhoneConfirmationView(Context context, int type) {
            super(context);
            this.verificationType = type;
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView = new TextView(context);
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            FrameLayout frameLayout;
            if (this.verificationType == 3) {
                this.confirmTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(NUM);
                if (LocaleController.isRTL) {
                    int i;
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    TextView textView = this.confirmTextView;
                    if (LocaleController.isRTL) {
                        i = 5;
                    } else {
                        i = 3;
                    }
                    frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
            } else {
                this.confirmTextView.setGravity(49);
                frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, 49));
                if (this.verificationType == 1) {
                    this.blackImageView = new ImageView(context);
                    this.blackImageView.setImageResource(NUM);
                    this.blackImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), Mode.MULTIPLY));
                    frameLayout.addView(this.blackImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.blueImageView = new ImageView(context);
                    this.blueImageView.setImageResource(NUM);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionBackground"), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentAppCodeTitle", NUM));
                } else {
                    this.blueImageView = new ImageView(context);
                    this.blueImageView.setImageResource(NUM);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionBackground"), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentSmsCodeTitle", NUM));
                }
                addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 0));
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
            }
            this.codeFieldContainer = new LinearLayout(context);
            this.codeFieldContainer.setOrientation(0);
            addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, 36, 1));
            if (this.verificationType == 3) {
                this.codeFieldContainer.setVisibility(8);
            }
            this.timeText = new TextView(context, PassportActivity.this) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            if (this.verificationType == 3) {
                this.timeText.setTextSize(1, 14.0f);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                this.progressView = new ProgressView(context);
                this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            } else {
                this.timeText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
                this.timeText.setTextSize(1, 15.0f);
                this.timeText.setGravity(49);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, 49));
            }
            this.problemText = new TextView(context, PassportActivity.this) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
            this.problemText.setTextSize(1, 15.0f);
            this.problemText.setGravity(49);
            if (this.verificationType == 1) {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", NUM));
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", NUM));
            }
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49));
            this.problemText.setOnClickListener(new PassportActivity$PhoneConfirmationView$$Lambda$0(this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$0$PassportActivity$PhoneConfirmationView(View v) {
            boolean email = false;
            if (!this.nextPressed) {
                if ((this.nextType == 4 && this.verificationType == 2) || this.nextType == 0) {
                    email = true;
                }
                if (email) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                        Intent mailer = new Intent("android.intent.action.SEND");
                        mailer.setType("message/rfCLASSNAME");
                        mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                        mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.phone);
                        mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.phone + "\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                        getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                        return;
                    } catch (Exception e) {
                        AlertsCreator.showSimpleAlert(PassportActivity.this, LocaleController.getString("NoMailInstalled", NUM));
                        return;
                    }
                }
                resendCode();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.verificationType != 3 && this.blueImageView != null) {
                int innerHeight = ((this.blueImageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight()) + this.confirmTextView.getMeasuredHeight()) + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (PassportActivity.this.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else if (PassportActivity.this.scrollHeight > maxHeight) {
                    setMeasuredDimension(getMeasuredWidth(), maxHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), PassportActivity.this.scrollHeight);
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.verificationType != 3 && this.blueImageView != null) {
                int h;
                int bottom = this.confirmTextView.getBottom();
                int height = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    h = this.problemText.getMeasuredHeight();
                    t = (bottom + height) - h;
                    this.problemText.layout(this.problemText.getLeft(), t, this.problemText.getRight(), t + h);
                } else if (this.timeText.getVisibility() == 0) {
                    h = this.timeText.getMeasuredHeight();
                    t = (bottom + height) - h;
                    this.timeText.layout(this.timeText.getLeft(), t, this.timeText.getRight(), t + h);
                } else {
                    t = bottom + height;
                }
                height = t - bottom;
                h = this.codeFieldContainer.getMeasuredHeight();
                t = ((height - h) / 2) + bottom;
                this.codeFieldContainer.layout(this.codeFieldContainer.getLeft(), t, this.codeFieldContainer.getRight(), t + h);
            }
        }

        private void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            this.nextPressed = true;
            PassportActivity.this.needShowProgress();
            TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new PassportActivity$PhoneConfirmationView$$Lambda$1(this, params, req), 2);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$resendCode$3$PassportActivity$PhoneConfirmationView(Bundle params, TL_auth_resendCode req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$$Lambda$9(this, error, params, response, req));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$2$PassportActivity$PhoneConfirmationView(TL_error error, Bundle params, TLObject response, TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                PassportActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response, true);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new PassportActivity$PhoneConfirmationView$$Lambda$10(this));
                }
            }
            PassportActivity.this.needHideProgress();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$PassportActivity$PhoneConfirmationView(DialogInterface dialog1, int which) {
            onBackPressed(true);
            PassportActivity.this.finishFragment();
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.waitingForEvent = true;
                if (this.verificationType == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.verificationType == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = params;
                this.phone = params.getString("phone");
                this.phoneHash = params.getString("phoneHash");
                int i = params.getInt("timeout");
                this.time = i;
                this.timeout = i;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = params.getInt("nextType");
                this.pattern = params.getString("pattern");
                this.length = params.getInt("length");
                if (this.length == 0) {
                    this.length = 5;
                }
                int a;
                if (this.codeField == null || this.codeField.length != this.length) {
                    this.codeField = new EditTextBoldCursor[this.length];
                    a = 0;
                    while (a < this.length) {
                        final int num = a;
                        this.codeField[a] = new EditTextBoldCursor(getContext());
                        this.codeField[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[a].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[a].setCursorWidth(1.5f);
                        Drawable pressedDrawable = getResources().getDrawable(NUM).mutate();
                        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Mode.MULTIPLY));
                        this.codeField[a].setBackgroundDrawable(pressedDrawable);
                        this.codeField[a].setImeOptions(NUM);
                        this.codeField[a].setTextSize(1, 20.0f);
                        this.codeField[a].setMaxLines(1);
                        this.codeField[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[a].setPadding(0, 0, 0, 0);
                        this.codeField[a].setGravity(49);
                        if (this.verificationType == 3) {
                            this.codeField[a].setEnabled(false);
                            this.codeField[a].setInputType(0);
                            this.codeField[a].setVisibility(8);
                        } else {
                            this.codeField[a].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[a], LayoutHelper.createLinear(34, 36, 1, 0, 0, a != this.length + -1 ? 7 : 0, 0));
                        this.codeField[a].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (!PhoneConfirmationView.this.ignoreOnTextChange) {
                                    int len = s.length();
                                    if (len >= 1) {
                                        if (len > 1) {
                                            String text = s.toString();
                                            PhoneConfirmationView.this.ignoreOnTextChange = true;
                                            for (int a = 0; a < Math.min(PhoneConfirmationView.this.length - num, len); a++) {
                                                if (a == 0) {
                                                    s.replace(0, len, text.substring(a, a + 1));
                                                } else {
                                                    PhoneConfirmationView.this.codeField[num + a].setText(text.substring(a, a + 1));
                                                }
                                            }
                                            PhoneConfirmationView.this.ignoreOnTextChange = false;
                                        }
                                        if (num != PhoneConfirmationView.this.length - 1) {
                                            PhoneConfirmationView.this.codeField[num + 1].setSelection(PhoneConfirmationView.this.codeField[num + 1].length());
                                            PhoneConfirmationView.this.codeField[num + 1].requestFocus();
                                        }
                                        if ((num == PhoneConfirmationView.this.length - 1 || (num == PhoneConfirmationView.this.length - 2 && len >= 2)) && PhoneConfirmationView.this.getCode().length() == PhoneConfirmationView.this.length) {
                                            PhoneConfirmationView.this.onNextPressed();
                                        }
                                    }
                                }
                            }
                        });
                        this.codeField[a].setOnKeyListener(new PassportActivity$PhoneConfirmationView$$Lambda$2(this, num));
                        this.codeField[a].setOnEditorActionListener(new PassportActivity$PhoneConfirmationView$$Lambda$3(this));
                        a++;
                    }
                } else {
                    for (EditTextBoldCursor text : this.codeField) {
                        text.setText("");
                    }
                }
                if (this.progressView != null) {
                    this.progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format("+" + this.phone);
                    CharSequence str = "";
                    if (this.verificationType == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(number)));
                    } else if (this.verificationType == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(number)));
                    } else if (this.verificationType == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.verificationType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.verificationType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(1), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(1), Integer.valueOf(0)));
                        }
                        createTimer();
                    } else if (this.verificationType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(2), Integer.valueOf(0)));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else if (this.verificationType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(2), Integer.valueOf(0)));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$setParams$4$PassportActivity$PhoneConfirmationView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            this.codeField[num - 1].setSelection(this.codeField[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$setParams$5$PassportActivity$PhoneConfirmationView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$4$$Lambda$0(this));
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$run$0$PassportActivity$PhoneConfirmationView$4() {
                        double currentTime = (double) System.currentTimeMillis();
                        double diff = currentTime - PhoneConfirmationView.this.lastCodeTime;
                        PhoneConfirmationView.this.lastCodeTime = currentTime;
                        PhoneConfirmationView.this.codeTime = (int) (((double) PhoneConfirmationView.this.codeTime) - diff);
                        if (PhoneConfirmationView.this.codeTime <= 1000) {
                            PhoneConfirmationView.this.problemText.setVisibility(0);
                            PhoneConfirmationView.this.timeText.setVisibility(8);
                            PhoneConfirmationView.this.destroyCodeTimer();
                        }
                    }
                }, 0, 1000);
            }
        }

        private void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.codeTimer != null) {
                        this.codeTimer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new TimerTask() {
                    public void run() {
                        if (PhoneConfirmationView.this.timeTimer != null) {
                            double currentTime = (double) System.currentTimeMillis();
                            PhoneConfirmationView.this.time = (int) (((double) PhoneConfirmationView.this.time) - (currentTime - PhoneConfirmationView.this.lastCurrentTime));
                            PhoneConfirmationView.this.lastCurrentTime = currentTime;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (PhoneConfirmationView.this.time >= 1000) {
                                        int seconds = (PhoneConfirmationView.this.time / 1000) - (((PhoneConfirmationView.this.time / 1000) / 60) * 60);
                                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3) {
                                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                        } else if (PhoneConfirmationView.this.nextType == 2) {
                                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                        }
                                        if (PhoneConfirmationView.this.progressView != null) {
                                            PhoneConfirmationView.this.progressView.setProgress(1.0f - (((float) PhoneConfirmationView.this.time) / ((float) PhoneConfirmationView.this.timeout)));
                                            return;
                                        }
                                        return;
                                    }
                                    if (PhoneConfirmationView.this.progressView != null) {
                                        PhoneConfirmationView.this.progressView.setProgress(1.0f);
                                    }
                                    PhoneConfirmationView.this.destroyTimer();
                                    if (PhoneConfirmationView.this.verificationType == 3) {
                                        AndroidUtilities.setWaitingForCall(false);
                                        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                                        PhoneConfirmationView.this.waitingForEvent = false;
                                        PhoneConfirmationView.this.destroyCodeTimer();
                                        PhoneConfirmationView.this.resendCode();
                                    } else if (PhoneConfirmationView.this.verificationType != 2 && PhoneConfirmationView.this.verificationType != 4) {
                                    } else {
                                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 2) {
                                            if (PhoneConfirmationView.this.nextType == 4) {
                                                PhoneConfirmationView.this.timeText.setText(LocaleController.getString("Calling", NUM));
                                            } else {
                                                PhoneConfirmationView.this.timeText.setText(LocaleController.getString("SendingSms", NUM));
                                            }
                                            PhoneConfirmationView.this.createCodeTimer();
                                            TL_auth_resendCode req = new TL_auth_resendCode();
                                            req.phone_number = PhoneConfirmationView.this.phone;
                                            req.phone_code_hash = PhoneConfirmationView.this.phoneHash;
                                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new PassportActivity$PhoneConfirmationView$5$1$$Lambda$0(this), 2);
                                        } else if (PhoneConfirmationView.this.nextType == 3) {
                                            AndroidUtilities.setWaitingForSms(false);
                                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                            PhoneConfirmationView.this.waitingForEvent = false;
                                            PhoneConfirmationView.this.destroyCodeTimer();
                                            PhoneConfirmationView.this.resendCode();
                                        }
                                    }
                                }

                                /* Access modifiers changed, original: final|synthetic */
                                public final /* synthetic */ void lambda$run$1$PassportActivity$PhoneConfirmationView$5$1(TLObject response, TL_error error) {
                                    if (error != null && error.text != null) {
                                        AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$5$1$$Lambda$1(this, error));
                                    }
                                }

                                /* Access modifiers changed, original: final|synthetic */
                                public final /* synthetic */ void lambda$null$0$PassportActivity$PhoneConfirmationView$5$1(TL_error error) {
                                    PhoneConfirmationView.this.lastError = error.text;
                                }
                            });
                        }
                    }
                }, 0, 1000);
            }
        }

        private void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.timeTimer != null) {
                        this.timeTimer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private String getCode() {
            if (this.codeField == null) {
                return "";
            }
            StringBuilder codeBuilder = new StringBuilder();
            for (EditTextBoldCursor text : this.codeField) {
                codeBuilder.append(PhoneFormat.stripExceptNumbers(text.getText().toString()));
            }
            return codeBuilder.toString();
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String code = getCode();
                if (TextUtils.isEmpty(code)) {
                    AndroidUtilities.shakeView(this.codeFieldContainer, 2.0f, 0);
                    return;
                }
                this.nextPressed = true;
                if (this.verificationType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.verificationType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                PassportActivity.this.showEditDoneProgress(true, true);
                TL_account_verifyPhone req = new TL_account_verifyPhone();
                req.phone_number = this.phone;
                req.phone_code = code;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                PassportActivity.this.needShowProgress();
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new PassportActivity$PhoneConfirmationView$$Lambda$4(this, req), 2);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$7$PassportActivity$PhoneConfirmationView(TL_account_verifyPhone req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$$Lambda$7(this, error, req));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$6$PassportActivity$PhoneConfirmationView(TL_error error, TL_account_verifyPhone req) {
            PassportActivity.this.needHideProgress();
            this.nextPressed = false;
            if (error == null) {
                destroyTimer();
                destroyCodeTimer();
                PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("phone"), null, null, null, null, null, null, null, null, new PassportActivity$PhoneConfirmationView$$Lambda$8(PassportActivity.this), null);
                return;
            }
            this.lastError = error.text;
            if ((this.verificationType == 3 && (this.nextType == 4 || this.nextType == 2)) || ((this.verificationType == 2 && (this.nextType == 4 || this.nextType == 3)) || (this.verificationType == 4 && this.nextType == 2))) {
                createTimer();
            }
            if (this.verificationType == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.verificationType == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = true;
            if (this.verificationType != 3) {
                AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
            }
            PassportActivity.this.showEditDoneProgress(true, false);
            if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                for (EditTextBoldCursor text : this.codeField) {
                    text.setText("");
                }
                this.codeField[0].requestFocus();
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                PassportActivity.this.setPage(0, true, null);
            }
        }

        public boolean onBackPressed(boolean force) {
            if (force) {
                TL_auth_cancelCode req = new TL_auth_cancelCode();
                req.phone_number = this.phone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, PassportActivity$PhoneConfirmationView$$Lambda$6.$instance, 2);
                destroyTimer();
                destroyCodeTimer();
                this.currentParams = null;
                if (this.verificationType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.verificationType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                return true;
            }
            Builder builder = new Builder(PassportActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopVerification", NUM));
            builder.setPositiveButton(LocaleController.getString("Continue", NUM), null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new PassportActivity$PhoneConfirmationView$$Lambda$5(this));
            PassportActivity.this.showDialog(builder.create());
            return false;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onBackPressed$8$PassportActivity$PhoneConfirmationView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            PassportActivity.this.setPage(0, true, null);
        }

        static final /* synthetic */ void lambda$onBackPressed$9$PassportActivity$PhoneConfirmationView(TLObject response, TL_error error) {
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            if (this.verificationType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.verificationType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        public void onShow() {
            super.onShow();
            if (this.codeFieldContainer != null && this.codeFieldContainer.getVisibility() == 0) {
                int a = this.codeField.length - 1;
                while (a >= 0) {
                    if (a == 0 || this.codeField[a].length() != 0) {
                        this.codeField[a].requestFocus();
                        this.codeField[a].setSelection(this.codeField[a].length());
                        AndroidUtilities.showKeyboard(this.codeField[a]);
                        return;
                    }
                    a--;
                }
            }
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.waitingForEvent && this.codeField != null) {
                if (id == NotificationCenter.didReceiveSmsCode) {
                    this.codeField[0].setText("" + args[0]);
                    onNextPressed();
                } else if (id == NotificationCenter.didReceiveCall) {
                    String num = "" + args[0];
                    if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                        this.ignoreOnTextChange = true;
                        this.codeField[0].setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    }
                }
            }
        }
    }

    private class ProgressView extends View {
        private Paint paint = new Paint();
        private Paint paint2 = new Paint();
        private float progress;

        public ProgressView(Context context) {
            super(context);
            this.paint.setColor(Theme.getColor("login_progressInner"));
            this.paint2.setColor(Theme.getColor("login_progressOuter"));
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int start = (int) (((float) getMeasuredWidth()) * this.progress);
            canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    public class SecureDocumentCell extends FrameLayout implements FileDownloadProgressListener {
        private int TAG;
        private int buttonState;
        private SecureDocument currentSecureDocument;
        private BackupImageView imageView;
        private RadialProgress radialProgress = new RadialProgress(this);
        private TextView textView;
        final /* synthetic */ PassportActivity this$0;
        private TextView valueTextView;

        public SecureDocumentCell(PassportActivity this$0, Context context) {
            int i;
            int i2;
            int i3 = 21;
            int i4 = 5;
            this.this$0 = this$0;
            super(context);
            this.TAG = DownloadController.getInstance(this$0.currentAccount).generateObserverTag();
            this.imageView = new BackupImageView(context);
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 8.0f, 21.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            TextView textView = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 16);
            TextView textView2 = this.textView;
            if (LocaleController.isRTL) {
                i2 = 5;
            } else {
                i2 = 3;
            }
            addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i2 | 48, (float) (LocaleController.isRTL ? 21 : 81), 10.0f, (float) (LocaleController.isRTL ? 81 : 21), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            textView = this.valueTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            textView2 = this.valueTextView;
            if (!LocaleController.isRTL) {
                i4 = 3;
            }
            i2 = i4 | 48;
            float f = (float) (LocaleController.isRTL ? 21 : 81);
            if (LocaleController.isRTL) {
                i3 = 81;
            }
            addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i2, f, 35.0f, (float) i3, 0.0f));
            setWillNotDraw(false);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, NUM));
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int x = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            int y = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
        }

        /* Access modifiers changed, original: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (child == this.imageView) {
                this.radialProgress.draw(canvas);
            }
            return result;
        }

        public void setTextAndValueAndImage(String text, CharSequence value, SecureDocument document) {
            this.textView.setText(text);
            this.valueTextView.setText(value);
            this.imageView.setImage(document, "48_48");
            this.currentSecureDocument = document;
            updateButtonState(false);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        public void updateButtonState(boolean animated) {
            float f = 0.0f;
            String fileName = FileLoader.getAttachFileName(this.currentSecureDocument);
            boolean fileExists = FileLoader.getPathToAttach(this.currentSecureDocument).exists();
            Float progress;
            RadialProgress radialProgress;
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setBackground(null, false, false);
            } else if (this.currentSecureDocument.path != null) {
                if (this.currentSecureDocument.inputFile != null) {
                    DownloadController.getInstance(this.this$0.currentAccount).removeLoadingFileObserver(this);
                    this.radialProgress.setBackground(null, false, animated);
                    this.buttonState = -1;
                    return;
                }
                DownloadController.getInstance(this.this$0.currentAccount).addLoadingFileObserver(this.currentSecureDocument.path, this);
                this.buttonState = 1;
                progress = ImageLoader.getInstance().getFileProgress(this.currentSecureDocument.path);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, animated);
                radialProgress = this.radialProgress;
                if (progress != null) {
                    f = progress.floatValue();
                }
                radialProgress.setProgress(f, false);
                invalidate();
            } else if (fileExists) {
                DownloadController.getInstance(this.this$0.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground(null, false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.this$0.currentAccount).addLoadingFileObserver(fileName, this);
                this.buttonState = 1;
                progress = ImageLoader.getInstance().getFileProgress(fileName);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, animated);
                radialProgress = this.radialProgress;
                if (progress != null) {
                    f = progress.floatValue();
                }
                radialProgress.setProgress(f, animated);
                invalidate();
            }
        }

        public void invalidate() {
            super.invalidate();
            this.textView.invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
            this.radialProgress.setProgress(progress, true);
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        public TextDetailSecureCell(Context context) {
            super(context);
            int padding = PassportActivity.this.currentActivityType == 8 ? 21 : 51;
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? padding : 21), 10.0f, (float) (LocaleController.isRTL ? 21 : padding), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            TextView textView = this.valueTextView;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            float f = (float) (LocaleController.isRTL ? padding : 21);
            if (LocaleController.isRTL) {
                padding = 21;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, f, 35.0f, (float) padding, 0.0f));
            this.checkImageView = new ImageView(context);
            this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
            this.checkImageView.setImageResource(NUM);
            addView(this.checkImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 25.0f, 21.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), NUM));
        }

        public void setTextAndValue(String text, CharSequence value, boolean divider) {
            this.textView.setText(text);
            this.valueTextView.setText(value);
            this.needDivider = divider;
            setWillNotDraw(!divider);
        }

        public void setChecked(boolean checked) {
            this.checkImageView.setVisibility(checked ? 0 : 4);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        public void setNeedDivider(boolean value) {
            this.needDivider = value;
            setWillNotDraw(!this.needDivider);
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public PassportActivity(int type, int botId, String scope, String publicKey, String payload, String nonce, String callbackUrl, TL_account_authorizationForm form, TL_account_password accountPassword) {
        this(type, form, accountPassword, null, null, null, null, null, null);
        this.currentBotId = botId;
        this.currentPayload = payload;
        this.currentNonce = nonce;
        this.currentScope = scope;
        this.currentPublicKey = publicKey;
        this.currentCallbackUrl = callbackUrl;
        if (type == 0 && !form.errors.isEmpty()) {
            try {
                Collections.sort(form.errors, new Comparator<SecureValueError>() {
                    /* Access modifiers changed, original: 0000 */
                    public int getErrorValue(SecureValueError error) {
                        if (error instanceof TL_secureValueError) {
                            return 0;
                        }
                        if (error instanceof TL_secureValueErrorFrontSide) {
                            return 1;
                        }
                        if (error instanceof TL_secureValueErrorReverseSide) {
                            return 2;
                        }
                        if (error instanceof TL_secureValueErrorSelfie) {
                            return 3;
                        }
                        if (error instanceof TL_secureValueErrorTranslationFile) {
                            return 4;
                        }
                        if (error instanceof TL_secureValueErrorTranslationFiles) {
                            return 5;
                        }
                        if (error instanceof TL_secureValueErrorFile) {
                            return 6;
                        }
                        if (error instanceof TL_secureValueErrorFiles) {
                            return 7;
                        }
                        if (!(error instanceof TL_secureValueErrorData)) {
                            return 100;
                        }
                        return PassportActivity.this.getFieldCost(((TL_secureValueErrorData) error).field);
                    }

                    public int compare(SecureValueError e1, SecureValueError e2) {
                        int val1 = getErrorValue(e1);
                        int val2 = getErrorValue(e2);
                        if (val1 < val2) {
                            return -1;
                        }
                        if (val1 > val2) {
                            return 1;
                        }
                        return 0;
                    }
                });
                int size = form.errors.size();
                for (int a = 0; a < size; a++) {
                    String key;
                    String description;
                    String target;
                    String hash;
                    SecureValueError secureValueError = (SecureValueError) form.errors.get(a);
                    String field = null;
                    byte[] file_hash = null;
                    if (secureValueError instanceof TL_secureValueErrorFrontSide) {
                        TL_secureValueErrorFrontSide secureValueErrorFrontSide = (TL_secureValueErrorFrontSide) secureValueError;
                        key = getNameForType(secureValueErrorFrontSide.type);
                        description = secureValueErrorFrontSide.text;
                        file_hash = secureValueErrorFrontSide.file_hash;
                        target = "front";
                    } else if (secureValueError instanceof TL_secureValueErrorReverseSide) {
                        TL_secureValueErrorReverseSide secureValueErrorReverseSide = (TL_secureValueErrorReverseSide) secureValueError;
                        key = getNameForType(secureValueErrorReverseSide.type);
                        description = secureValueErrorReverseSide.text;
                        file_hash = secureValueErrorReverseSide.file_hash;
                        target = "reverse";
                    } else if (secureValueError instanceof TL_secureValueErrorSelfie) {
                        TL_secureValueErrorSelfie secureValueErrorSelfie = (TL_secureValueErrorSelfie) secureValueError;
                        key = getNameForType(secureValueErrorSelfie.type);
                        description = secureValueErrorSelfie.text;
                        file_hash = secureValueErrorSelfie.file_hash;
                        target = "selfie";
                    } else if (secureValueError instanceof TL_secureValueErrorTranslationFile) {
                        TL_secureValueErrorTranslationFile secureValueErrorTranslationFile = (TL_secureValueErrorTranslationFile) secureValueError;
                        key = getNameForType(secureValueErrorTranslationFile.type);
                        description = secureValueErrorTranslationFile.text;
                        file_hash = secureValueErrorTranslationFile.file_hash;
                        target = "translation";
                    } else if (secureValueError instanceof TL_secureValueErrorTranslationFiles) {
                        TL_secureValueErrorTranslationFiles secureValueErrorTranslationFiles = (TL_secureValueErrorTranslationFiles) secureValueError;
                        key = getNameForType(secureValueErrorTranslationFiles.type);
                        description = secureValueErrorTranslationFiles.text;
                        target = "translation";
                    } else if (secureValueError instanceof TL_secureValueErrorFile) {
                        TL_secureValueErrorFile secureValueErrorFile = (TL_secureValueErrorFile) secureValueError;
                        key = getNameForType(secureValueErrorFile.type);
                        description = secureValueErrorFile.text;
                        file_hash = secureValueErrorFile.file_hash;
                        target = "files";
                    } else if (secureValueError instanceof TL_secureValueErrorFiles) {
                        TL_secureValueErrorFiles secureValueErrorFiles = (TL_secureValueErrorFiles) secureValueError;
                        key = getNameForType(secureValueErrorFiles.type);
                        description = secureValueErrorFiles.text;
                        target = "files";
                    } else if (secureValueError instanceof TL_secureValueError) {
                        TL_secureValueError secureValueErrorAll = (TL_secureValueError) secureValueError;
                        key = getNameForType(secureValueErrorAll.type);
                        description = secureValueErrorAll.text;
                        file_hash = secureValueErrorAll.hash;
                        target = "error_all";
                    } else {
                        if (secureValueError instanceof TL_secureValueErrorData) {
                            TL_secureValueErrorData secureValueErrorData = (TL_secureValueErrorData) secureValueError;
                            boolean found = false;
                            for (int b = 0; b < form.values.size(); b++) {
                                TL_secureValue value = (TL_secureValue) form.values.get(b);
                                if (value.data != null && Arrays.equals(value.data.data_hash, secureValueErrorData.data_hash)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                key = getNameForType(secureValueErrorData.type);
                                description = secureValueErrorData.text;
                                field = secureValueErrorData.field;
                                file_hash = secureValueErrorData.data_hash;
                                target = "data";
                            }
                        }
                    }
                    HashMap<String, String> vals = (HashMap) this.errorsMap.get(key);
                    if (vals == null) {
                        vals = new HashMap();
                        this.errorsMap.put(key, vals);
                        this.mainErrorsMap.put(key, description);
                    }
                    if (file_hash != null) {
                        hash = Base64.encodeToString(file_hash, 2);
                    } else {
                        hash = "";
                    }
                    if ("data".equals(target)) {
                        if (field != null) {
                            vals.put(field, description);
                        }
                    } else if ("files".equals(target)) {
                        if (file_hash != null) {
                            vals.put("files" + hash, description);
                        } else {
                            vals.put("files_all", description);
                        }
                    } else if ("selfie".equals(target)) {
                        vals.put("selfie" + hash, description);
                    } else if ("translation".equals(target)) {
                        if (file_hash != null) {
                            vals.put("translation" + hash, description);
                        } else {
                            vals.put("translation_all", description);
                        }
                    } else if ("front".equals(target)) {
                        vals.put("front" + hash, description);
                    } else if ("reverse".equals(target)) {
                        vals.put("reverse" + hash, description);
                    } else if ("error_all".equals(target)) {
                        vals.put("error_all", description);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public PassportActivity(int type, TL_account_authorizationForm form, TL_account_password accountPassword, TL_secureRequiredType secureType, TL_secureValue secureValue, TL_secureRequiredType secureDocumentsType, TL_secureValue secureDocumentsValue, HashMap<String, String> values, HashMap<String, String> documentValues) {
        this.currentCitizeship = "";
        this.currentResidence = "";
        this.currentExpireDate = new int[3];
        this.dividers = new ArrayList();
        this.nonLatinNames = new boolean[3];
        this.allowNonLatinName = true;
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.documents = new ArrayList();
        this.translationDocuments = new ArrayList();
        this.documentsCells = new HashMap();
        this.uploadingDocuments = new HashMap();
        this.typesValues = new HashMap();
        this.typesViews = new HashMap();
        this.documentsToTypesLink = new HashMap();
        this.errorsMap = new HashMap();
        this.mainErrorsMap = new HashMap();
        this.errorsValues = new HashMap();
        this.provider = new EmptyPhotoViewerProvider() {
            public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index, boolean needPreview) {
                int i = 0;
                if (index < 0 || index >= PassportActivity.this.currentPhotoViewerLayout.getChildCount()) {
                    return null;
                }
                SecureDocumentCell cell = (SecureDocumentCell) PassportActivity.this.currentPhotoViewerLayout.getChildAt(index);
                int[] coords = new int[2];
                cell.imageView.getLocationInWindow(coords);
                PlaceProviderObject object = new PlaceProviderObject();
                object.viewX = coords[0];
                int i2 = coords[1];
                if (VERSION.SDK_INT < 21) {
                    i = AndroidUtilities.statusBarHeight;
                }
                object.viewY = i2 - i;
                object.parentView = PassportActivity.this.currentPhotoViewerLayout;
                object.imageReceiver = cell.imageView.getImageReceiver();
                object.thumb = object.imageReceiver.getBitmapSafe();
                return object;
            }

            public void deleteImageAtIndex(int index) {
                SecureDocument document;
                if (PassportActivity.this.uploadingFileType == 1) {
                    document = PassportActivity.this.selfieDocument;
                } else if (PassportActivity.this.uploadingFileType == 4) {
                    document = (SecureDocument) PassportActivity.this.translationDocuments.get(index);
                } else if (PassportActivity.this.uploadingFileType == 2) {
                    document = PassportActivity.this.frontDocument;
                } else if (PassportActivity.this.uploadingFileType == 3) {
                    document = PassportActivity.this.reverseDocument;
                } else {
                    document = (SecureDocument) PassportActivity.this.documents.get(index);
                }
                SecureDocumentCell cell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(document);
                if (cell != null) {
                    String key = null;
                    String hash = PassportActivity.this.getDocumentHash(document);
                    if (PassportActivity.this.uploadingFileType == 1) {
                        PassportActivity.this.selfieDocument = null;
                        key = "selfie" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 4) {
                        key = "translation" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 2) {
                        PassportActivity.this.frontDocument = null;
                        key = "front" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 3) {
                        PassportActivity.this.reverseDocument = null;
                        key = "reverse" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 0) {
                        key = "files" + hash;
                    }
                    if (key != null) {
                        if (PassportActivity.this.documentsErrors != null) {
                            PassportActivity.this.documentsErrors.remove(key);
                        }
                        if (PassportActivity.this.errorsValues != null) {
                            PassportActivity.this.errorsValues.remove(key);
                        }
                    }
                    PassportActivity.this.updateUploadText(PassportActivity.this.uploadingFileType);
                    PassportActivity.this.currentPhotoViewerLayout.removeView(cell);
                }
            }

            public String getDeleteMessageString() {
                if (PassportActivity.this.uploadingFileType == 1) {
                    return LocaleController.formatString("PassportDeleteSelfieAlert", NUM, new Object[0]);
                }
                return LocaleController.formatString("PassportDeleteScanAlert", NUM, new Object[0]);
            }
        };
        this.currentActivityType = type;
        this.currentForm = form;
        this.currentType = secureType;
        if (this.currentType != null) {
            this.allowNonLatinName = this.currentType.native_names;
        }
        this.currentTypeValue = secureValue;
        this.currentDocumentsType = secureDocumentsType;
        this.currentDocumentsTypeValue = secureDocumentsValue;
        this.currentPassword = accountPassword;
        this.currentValues = values;
        this.currentDocumentValues = documentValues;
        if (this.currentActivityType == 3) {
            this.permissionsItems = new ArrayList();
        } else if (this.currentActivityType == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap();
        }
        if (this.currentDocumentValues == null) {
            this.currentDocumentValues = new HashMap();
        }
        if (type == 5) {
            if (!(UserConfig.getInstance(this.currentAccount).savedPasswordHash == null || UserConfig.getInstance(this.currentAccount).savedSaltedPassword == null)) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            if (this.currentPassword == null) {
                loadPasswordInfo();
            } else {
                TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            }
            if (!SharedConfig.isPassportConfigLoaded()) {
                TL_help_getPassportConfig req = new TL_help_getPassportConfig();
                req.hash = SharedConfig.passportConfigHash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, PassportActivity$$Lambda$0.$instance);
            }
        }
    }

    static final /* synthetic */ void lambda$null$0$PassportActivity(TLObject response) {
        if (response instanceof TL_help_passportConfig) {
            TL_help_passportConfig res = (TL_help_passportConfig) response;
            SharedConfig.setPassportConfig(res.countries_langs.data, res.hash);
            return;
        }
        SharedConfig.getCountryLangs();
    }

    public void onResume() {
        super.onResume();
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onResume();
        }
        if (this.currentActivityType == 5 && this.inputFieldContainers != null && this.inputFieldContainers[0] != null && this.inputFieldContainers[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
            AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$1(this), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onResume$2$PassportActivity() {
        if (this.inputFieldContainers != null && this.inputFieldContainers[0] != null && this.inputFieldContainers[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onPause();
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        callCallback(false);
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
        if (this.currentActivityType == 7) {
            for (int a = 0; a < this.views.length; a++) {
                if (this.views[a] != null) {
                    this.views[a].onDestroyActivity();
                }
            }
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.progressDialog = null;
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            private boolean onIdentityDone(Runnable finishRunnable, ErrorRunnable errorRunnable) {
                if (!PassportActivity.this.uploadingDocuments.isEmpty() || PassportActivity.this.checkFieldsForError()) {
                    return false;
                }
                int a;
                if (PassportActivity.this.allowNonLatinName) {
                    PassportActivity.this.allowNonLatinName = false;
                    boolean error = false;
                    for (a = 0; a < PassportActivity.this.nonLatinNames.length; a++) {
                        if (PassportActivity.this.nonLatinNames[a]) {
                            PassportActivity.this.inputFields[a].setErrorText(LocaleController.getString("PassportUseLatinOnly", NUM));
                            if (!error) {
                                String lastName;
                                error = true;
                                String firstName = PassportActivity.this.nonLatinNames[0] ? PassportActivity.this.getTranslitString(PassportActivity.this.inputExtraFields[0].getText().toString()) : PassportActivity.this.inputFields[0].getText().toString();
                                String middleName = PassportActivity.this.nonLatinNames[1] ? PassportActivity.this.getTranslitString(PassportActivity.this.inputExtraFields[1].getText().toString()) : PassportActivity.this.inputFields[1].getText().toString();
                                if (PassportActivity.this.nonLatinNames[2]) {
                                    lastName = PassportActivity.this.getTranslitString(PassportActivity.this.inputExtraFields[2].getText().toString());
                                } else {
                                    lastName = PassportActivity.this.inputFields[2].getText().toString();
                                }
                                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(middleName) || TextUtils.isEmpty(lastName)) {
                                    PassportActivity.this.onFieldError(PassportActivity.this.inputFields[a]);
                                } else {
                                    int num = a;
                                    Builder builder = new Builder(PassportActivity.this.getParentActivity());
                                    builder.setMessage(LocaleController.formatString("PassportNameCheckAlert", NUM, firstName, middleName, lastName));
                                    builder.setTitle(LocaleController.getString("AppName", NUM));
                                    builder.setPositiveButton(LocaleController.getString("Done", NUM), new PassportActivity$3$$Lambda$0(this, firstName, middleName, lastName, finishRunnable, errorRunnable));
                                    builder.setNegativeButton(LocaleController.getString("Edit", NUM), new PassportActivity$3$$Lambda$1(this, num));
                                    PassportActivity.this.showDialog(builder.create());
                                }
                            }
                        }
                    }
                    if (error) {
                        return false;
                    }
                }
                if (PassportActivity.this.isHasNotAnyChanges()) {
                    PassportActivity.this.finishFragment();
                    return false;
                }
                String jSONObject;
                SecureDocument secureDocument;
                JSONObject json = null;
                JSONObject documentsJson = null;
                try {
                    HashMap<String, String> hashMap;
                    ArrayList<String> arrayList;
                    int size;
                    String key;
                    if (!PassportActivity.this.documentOnly) {
                        hashMap = new HashMap(PassportActivity.this.currentValues);
                        if (PassportActivity.this.currentType.native_names) {
                            if (PassportActivity.this.nativeInfoCell.getVisibility() == 0) {
                                hashMap.put("first_name_native", PassportActivity.this.inputExtraFields[0].getText().toString());
                                hashMap.put("middle_name_native", PassportActivity.this.inputExtraFields[1].getText().toString());
                                hashMap.put("last_name_native", PassportActivity.this.inputExtraFields[2].getText().toString());
                            } else {
                                hashMap.put("first_name_native", PassportActivity.this.inputFields[0].getText().toString());
                                hashMap.put("middle_name_native", PassportActivity.this.inputFields[1].getText().toString());
                                hashMap.put("last_name_native", PassportActivity.this.inputFields[2].getText().toString());
                            }
                        }
                        hashMap.put("first_name", PassportActivity.this.inputFields[0].getText().toString());
                        hashMap.put("middle_name", PassportActivity.this.inputFields[1].getText().toString());
                        hashMap.put("last_name", PassportActivity.this.inputFields[2].getText().toString());
                        hashMap.put("birth_date", PassportActivity.this.inputFields[3].getText().toString());
                        hashMap.put("gender", PassportActivity.this.currentGender);
                        hashMap.put("country_code", PassportActivity.this.currentCitizeship);
                        hashMap.put("residence_country_code", PassportActivity.this.currentResidence);
                        JSONObject json2 = new JSONObject();
                        try {
                            arrayList = new ArrayList(hashMap.keySet());
                            Collections.sort(arrayList, new PassportActivity$3$$Lambda$2(this));
                            size = arrayList.size();
                            for (a = 0; a < size; a++) {
                                key = (String) arrayList.get(a);
                                json2.put(key, hashMap.get(key));
                            }
                            json = json2;
                        } catch (Exception e) {
                            json = json2;
                        }
                    }
                    if (PassportActivity.this.currentDocumentsType != null) {
                        hashMap = new HashMap(PassportActivity.this.currentDocumentValues);
                        hashMap.put("document_no", PassportActivity.this.inputFields[7].getText().toString());
                        if (PassportActivity.this.currentExpireDate[0] != 0) {
                            hashMap.put("expiry_date", String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(PassportActivity.this.currentExpireDate[2]), Integer.valueOf(PassportActivity.this.currentExpireDate[1]), Integer.valueOf(PassportActivity.this.currentExpireDate[0])}));
                        } else {
                            hashMap.put("expiry_date", "");
                        }
                        JSONObject documentsJson2 = new JSONObject();
                        try {
                            arrayList = new ArrayList(hashMap.keySet());
                            Collections.sort(arrayList, new PassportActivity$3$$Lambda$3(this));
                            size = arrayList.size();
                            for (a = 0; a < size; a++) {
                                key = (String) arrayList.get(a);
                                documentsJson2.put(key, hashMap.get(key));
                            }
                            documentsJson = documentsJson2;
                        } catch (Exception e2) {
                            documentsJson = documentsJson2;
                        }
                    }
                } catch (Exception e3) {
                }
                if (PassportActivity.this.fieldsErrors != null) {
                    PassportActivity.this.fieldsErrors.clear();
                }
                if (PassportActivity.this.documentsErrors != null) {
                    PassportActivity.this.documentsErrors.clear();
                }
                PassportActivityDelegate access$4200 = PassportActivity.this.delegate;
                TL_secureRequiredType access$3200 = PassportActivity.this.currentType;
                String jSONObject2 = json != null ? json.toString() : null;
                TL_secureRequiredType access$3700 = PassportActivity.this.currentDocumentsType;
                if (documentsJson != null) {
                    jSONObject = documentsJson.toString();
                } else {
                    jSONObject = null;
                }
                SecureDocument access$300 = PassportActivity.this.selfieDocument;
                ArrayList access$400 = PassportActivity.this.translationDocuments;
                SecureDocument access$500 = PassportActivity.this.frontDocument;
                if (PassportActivity.this.reverseLayout == null || PassportActivity.this.reverseLayout.getVisibility() != 0) {
                    secureDocument = null;
                } else {
                    secureDocument = PassportActivity.this.reverseDocument;
                }
                access$4200.saveValue(access$3200, null, jSONObject2, access$3700, jSONObject, null, access$300, access$400, access$500, secureDocument, finishRunnable, errorRunnable);
                return true;
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onIdentityDone$0$PassportActivity$3(String firstName, String middleName, String lastName, Runnable finishRunnable, ErrorRunnable errorRunnable, DialogInterface dialogInterface, int i) {
                PassportActivity.this.inputFields[0].setText(firstName);
                PassportActivity.this.inputFields[1].setText(middleName);
                PassportActivity.this.inputFields[2].setText(lastName);
                PassportActivity.this.showEditDoneProgress(true, true);
                onIdentityDone(finishRunnable, errorRunnable);
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onIdentityDone$1$PassportActivity$3(int num, DialogInterface dialogInterface, int i) {
                PassportActivity.this.onFieldError(PassportActivity.this.inputFields[num]);
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ int lambda$onIdentityDone$2$PassportActivity$3(String key1, String key2) {
                int val1 = PassportActivity.this.getFieldCost(key1);
                int val2 = PassportActivity.this.getFieldCost(key2);
                if (val1 < val2) {
                    return -1;
                }
                if (val1 > val2) {
                    return 1;
                }
                return 0;
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ int lambda$onIdentityDone$3$PassportActivity$3(String key1, String key2) {
                int val1 = PassportActivity.this.getFieldCost(key1);
                int val2 = PassportActivity.this.getFieldCost(key2);
                if (val1 < val2) {
                    return -1;
                }
                if (val1 > val2) {
                    return 1;
                }
                return 0;
            }

            public void onItemClick(int id) {
                if (id == -1) {
                    if (!PassportActivity.this.checkDiscard()) {
                        if (PassportActivity.this.currentActivityType == 0 || PassportActivity.this.currentActivityType == 5) {
                            PassportActivity.this.callCallback(false);
                        }
                        PassportActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    if (PassportActivity.this.getParentActivity() != null) {
                        View textView = new TextView(PassportActivity.this.getParentActivity());
                        String str2 = LocaleController.getString("PassportInfo2", NUM);
                        SpannableStringBuilder spanned = new SpannableStringBuilder(str2);
                        int index1 = str2.indexOf(42);
                        int index2 = str2.lastIndexOf(42);
                        if (!(index1 == -1 || index2 == -1)) {
                            spanned.replace(index2, index2 + 1, "");
                            spanned.replace(index1, index1 + 1, "");
                            spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("PassportInfoUrl", NUM)) {
                                public void onClick(View widget) {
                                    PassportActivity.this.dismissCurrentDialig();
                                    super.onClick(widget);
                                }
                            }, index1, index2 - 1, 33);
                        }
                        textView.setText(spanned);
                        textView.setTextSize(1, 16.0f);
                        textView.setLinkTextColor(Theme.getColor("dialogTextLink"));
                        textView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
                        textView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                        textView.setMovementMethod(new LinkMovementMethodMy());
                        textView.setTextColor(Theme.getColor("dialogTextBlack"));
                        Builder builder = new Builder(PassportActivity.this.getParentActivity());
                        builder.setView(textView);
                        builder.setTitle(LocaleController.getString("PassportInfoTitle", NUM));
                        builder.setNegativeButton(LocaleController.getString("Close", NUM), null);
                        PassportActivity.this.showDialog(builder.create());
                    }
                } else if (id != 2) {
                } else {
                    if (PassportActivity.this.currentActivityType == 5) {
                        PassportActivity.this.onPasswordDone(false);
                    } else if (PassportActivity.this.currentActivityType == 7) {
                        PassportActivity.this.views[PassportActivity.this.currentViewNum].onNextPressed();
                    } else {
                        final Runnable finishRunnable = new PassportActivity$3$$Lambda$4(this);
                        ErrorRunnable errorRunnable = new ErrorRunnable() {
                            public void onError(String error, String text) {
                                if ("PHONE_VERIFICATION_NEEDED".equals(error)) {
                                    PassportActivity.this.startPhoneVerification(true, text, finishRunnable, this, PassportActivity.this.delegate);
                                    return;
                                }
                                PassportActivity.this.showEditDoneProgress(true, false);
                            }
                        };
                        String value;
                        if (PassportActivity.this.currentActivityType == 4) {
                            if (PassportActivity.this.useCurrentValue) {
                                value = PassportActivity.this.currentEmail;
                            } else if (!PassportActivity.this.checkFieldsForError()) {
                                value = PassportActivity.this.inputFields[0].getText().toString();
                            } else {
                                return;
                            }
                            PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value, null, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                        } else if (PassportActivity.this.currentActivityType == 3) {
                            if (PassportActivity.this.useCurrentValue) {
                                value = UserConfig.getInstance(PassportActivity.this.currentAccount).getCurrentUser().phone;
                            } else if (!PassportActivity.this.checkFieldsForError()) {
                                value = PassportActivity.this.inputFields[1].getText().toString() + PassportActivity.this.inputFields[2].getText().toString();
                            } else {
                                return;
                            }
                            PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value, null, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                        } else if (PassportActivity.this.currentActivityType == 2) {
                            if (PassportActivity.this.uploadingDocuments.isEmpty() && !PassportActivity.this.checkFieldsForError()) {
                                if (PassportActivity.this.isHasNotAnyChanges()) {
                                    PassportActivity.this.finishFragment();
                                    return;
                                }
                                JSONObject json = null;
                                try {
                                    if (!PassportActivity.this.documentOnly) {
                                        JSONObject json2 = new JSONObject();
                                        try {
                                            json2.put("street_line1", PassportActivity.this.inputFields[0].getText().toString());
                                            json2.put("street_line2", PassportActivity.this.inputFields[1].getText().toString());
                                            json2.put("post_code", PassportActivity.this.inputFields[2].getText().toString());
                                            json2.put("city", PassportActivity.this.inputFields[3].getText().toString());
                                            json2.put("state", PassportActivity.this.inputFields[4].getText().toString());
                                            json2.put("country_code", PassportActivity.this.currentCitizeship);
                                            json = json2;
                                        } catch (Exception e) {
                                            json = json2;
                                        }
                                    }
                                } catch (Exception e2) {
                                }
                                if (PassportActivity.this.fieldsErrors != null) {
                                    PassportActivity.this.fieldsErrors.clear();
                                }
                                if (PassportActivity.this.documentsErrors != null) {
                                    PassportActivity.this.documentsErrors.clear();
                                }
                                PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, null, json != null ? json.toString() : null, PassportActivity.this.currentDocumentsType, null, PassportActivity.this.documents, PassportActivity.this.selfieDocument, PassportActivity.this.translationDocuments, null, null, finishRunnable, errorRunnable);
                            } else {
                                return;
                            }
                        } else if (PassportActivity.this.currentActivityType == 1) {
                            if (!onIdentityDone(finishRunnable, errorRunnable)) {
                                return;
                            }
                        } else if (PassportActivity.this.currentActivityType == 6) {
                            TLObject req = new TL_account_verifyEmail();
                            req.email = (String) PassportActivity.this.currentValues.get("email");
                            req.code = PassportActivity.this.inputFields[0].getText().toString();
                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new PassportActivity$3$$Lambda$5(this, finishRunnable, errorRunnable, req)), PassportActivity.this.classGuid);
                        }
                        PassportActivity.this.showEditDoneProgress(true, true);
                    }
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$4$PassportActivity$3() {
                PassportActivity.this.finishFragment();
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$6$PassportActivity$3(Runnable finishRunnable, ErrorRunnable errorRunnable, TL_account_verifyEmail req, TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new PassportActivity$3$$Lambda$6(this, error, finishRunnable, errorRunnable, req));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$5$PassportActivity$3(TL_error error, Runnable finishRunnable, ErrorRunnable errorRunnable, TL_account_verifyEmail req) {
                if (error == null) {
                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), null, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                    return;
                }
                AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
                errorRunnable.onError(null, null);
            }
        });
        if (this.currentActivityType == 7) {
            AnonymousClass4 anonymousClass4 = new ScrollView(context) {
                /* Access modifiers changed, original: protected */
                public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    if (PassportActivity.this.currentViewNum == 1 || PassportActivity.this.currentViewNum == 2 || PassportActivity.this.currentViewNum == 4) {
                        rectangle.bottom += AndroidUtilities.dp(40.0f);
                    }
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }

                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    PassportActivity.this.scrollHeight = MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            };
            this.scrollView = anonymousClass4;
            this.fragmentView = anonymousClass4;
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        } else {
            float f;
            this.fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = this.fragmentView;
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.scrollView = new ScrollView(context) {
                /* Access modifiers changed, original: protected */
                public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                    rectangle.top += AndroidUtilities.dp(20.0f);
                    rectangle.bottom += AndroidUtilities.dp(50.0f);
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }
            };
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
            ScrollView scrollView = this.scrollView;
            if (this.currentActivityType == 0) {
                f = 48.0f;
            } else {
                f = 0.0f;
            }
            frameLayout.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, f));
            this.linearLayout2 = new LinearLayout(context);
            this.linearLayout2.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        }
        if (!(this.currentActivityType == 0 || this.currentActivityType == 8)) {
            this.doneItem = this.actionBar.createMenu().addItemWithWidth(2, NUM, AndroidUtilities.dp(56.0f));
            this.progressView = new ContextProgressView(context, 1);
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            if ((this.currentActivityType == 1 || this.currentActivityType == 2) && this.chatAttachAlert != null) {
                try {
                    if (this.chatAttachAlert.isShowing()) {
                        this.chatAttachAlert.dismiss();
                    }
                } catch (Exception e) {
                }
                this.chatAttachAlert.onDestroy();
                this.chatAttachAlert = null;
            }
        }
        if (this.currentActivityType == 5) {
            createPasswordInterface(context);
        } else if (this.currentActivityType == 0) {
            createRequestInterface(context);
        } else if (this.currentActivityType == 1) {
            createIdentityInterface(context);
            fillInitialValues();
        } else if (this.currentActivityType == 2) {
            createAddressInterface(context);
            fillInitialValues();
        } else if (this.currentActivityType == 3) {
            createPhoneInterface(context);
        } else if (this.currentActivityType == 4) {
            createEmailInterface(context);
        } else if (this.currentActivityType == 6) {
            createEmailVerificationInterface(context);
        } else if (this.currentActivityType == 7) {
            createPhoneVerificationInterface(context);
        } else if (this.currentActivityType == 8) {
            createManageInterface(context);
        }
        return this.fragmentView;
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    public void dismissCurrentDialig() {
        if (this.chatAttachAlert == null || this.visibleDialog != this.chatAttachAlert) {
            super.dismissCurrentDialig();
            return;
        }
        this.chatAttachAlert.closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.hideCamera(true);
    }

    private String getTranslitString(String value) {
        return LocaleController.getInstance().getTranslitString(value, true);
    }

    private int getFieldCost(String key) {
        Object obj = -1;
        switch (key.hashCode()) {
            case -2006252145:
                if (key.equals("residence_country_code")) {
                    obj = 9;
                    break;
                }
                break;
            case -1537298398:
                if (key.equals("last_name_native")) {
                    obj = 5;
                    break;
                }
                break;
            case -1249512767:
                if (key.equals("gender")) {
                    obj = 7;
                    break;
                }
                break;
            case -796150911:
                if (key.equals("street_line1")) {
                    obj = 12;
                    break;
                }
                break;
            case -796150910:
                if (key.equals("street_line2")) {
                    obj = 13;
                    break;
                }
                break;
            case -160985414:
                if (key.equals("first_name")) {
                    obj = null;
                    break;
                }
                break;
            case 3053931:
                if (key.equals("city")) {
                    obj = 15;
                    break;
                }
                break;
            case 109757585:
                if (key.equals("state")) {
                    obj = 16;
                    break;
                }
                break;
            case 421072629:
                if (key.equals("middle_name")) {
                    obj = 2;
                    break;
                }
                break;
            case 451516732:
                if (key.equals("first_name_native")) {
                    obj = 1;
                    break;
                }
                break;
            case 475919162:
                if (key.equals("expiry_date")) {
                    obj = 11;
                    break;
                }
                break;
            case 506677093:
                if (key.equals("document_no")) {
                    obj = 10;
                    break;
                }
                break;
            case 1168724782:
                if (key.equals("birth_date")) {
                    obj = 6;
                    break;
                }
                break;
            case 1181577377:
                if (key.equals("middle_name_native")) {
                    obj = 3;
                    break;
                }
                break;
            case 1481071862:
                if (key.equals("country_code")) {
                    obj = 8;
                    break;
                }
                break;
            case 2002465324:
                if (key.equals("post_code")) {
                    obj = 14;
                    break;
                }
                break;
            case 2013122196:
                if (key.equals("last_name")) {
                    obj = 4;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
            case 1:
                return 20;
            case 2:
            case 3:
                return 21;
            case 4:
            case 5:
                return 22;
            case 6:
                return 23;
            case 7:
                return 24;
            case 8:
                return 25;
            case 9:
                return 26;
            case 10:
                return 27;
            case 11:
                return 28;
            case 12:
                return 29;
            case 13:
                return 30;
            case 14:
                return 31;
            case 15:
                return 32;
            case 16:
                return 33;
            default:
                return 100;
        }
    }

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", NUM));
        FrameLayout frameLayout = new FrameLayout(context);
        this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        for (int a = 0; a < 3; a++) {
            float f;
            this.views[a] = new PhoneConfirmationView(context, a + 2);
            this.views[a].setVisibility(8);
            View view = this.views[a];
            if (AndroidUtilities.isTablet()) {
                f = 26.0f;
            } else {
                f = 18.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
        }
        Bundle params = new Bundle();
        params.putString("phone", (String) this.currentValues.get("phone"));
        fillNextCodeParams(params, this.currentPhoneVerification, false);
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new PassportActivity$$Lambda$2(this)), this.classGuid);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadPasswordInfo$4$PassportActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$74(this, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$3$PassportActivity(TLObject response) {
        if (response != null) {
            this.currentPassword = (TL_account_password) response;
            if (TwoStepVerificationActivity.canHandleCurrentPassword(this.currentPassword, false)) {
                TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
                updatePasswordInterface();
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                    return;
                }
                return;
            }
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", NUM));
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
            container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(3);
            this.inputFields[a].setImeOptions(NUM);
            switch (a) {
                case 0:
                    this.inputFields[a].setHint(LocaleController.getString("PassportEmailCode", NUM));
                    break;
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$3(this));
            this.inputFields[a].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!PassportActivity.this.ignoreOnTextChange && PassportActivity.this.emailCodeLength != 0 && PassportActivity.this.inputFields[0].length() == PassportActivity.this.emailCodeLength) {
                        PassportActivity.this.doneItem.callOnClick();
                    }
                }
            });
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.formatString("PassportEmailVerifyInfo", NUM, this.currentValues.get("email")));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createEmailVerificationInterface$5$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    private void createPasswordInterface(Context context) {
        int a;
        Object botUser = null;
        if (this.currentForm != null) {
            for (a = 0; a < this.currentForm.users.size(); a++) {
                User user = (User) this.currentForm.users.get(a);
                if (user.id == this.currentBotId) {
                    botUser = user;
                    break;
                }
            }
        } else {
            botUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        FrameLayout frameLayout = this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", NUM));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.passwordAvatarContainer = new FrameLayout(context);
        this.linearLayout2.addView(this.passwordAvatarContainer, LayoutHelper.createLinear(-1, 100));
        BackupImageView avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(avatarImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        Drawable avatarDrawable = new AvatarDrawable((User) botUser);
        TLObject photo = null;
        if (botUser.photo != null) {
            photo = botUser.photo.photo_small;
        }
        avatarImageView.setImage(photo, "50_50", avatarDrawable, botUser);
        this.passwordRequestTextView = new TextInfoPrivacyCell(context);
        this.passwordRequestTextView.getTextView().setGravity(1);
        if (this.currentBotId == 0) {
            this.passwordRequestTextView.setText(LocaleController.getString("PassportSelfRequest", NUM));
        } else {
            this.passwordRequestTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", NUM, UserObject.getFirstName(botUser))));
        }
        ((LayoutParams) this.passwordRequestTextView.getTextView().getLayoutParams()).gravity = 1;
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        this.noPasswordImageView = new ImageView(context);
        this.noPasswordImageView.setImageResource(NUM);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        this.noPasswordTextView = new TextView(context);
        this.noPasswordTextView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.noPasswordTextView.setText(LocaleController.getString("TelegramPassportCreatePasswordInfo", NUM));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView = new TextView(context);
        this.noPasswordSetTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText5"));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.noPasswordSetTextView.setText(LocaleController.getString("TelegramPassportCreatePassword", NUM));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 9.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new PassportActivity$$Lambda$4(this));
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = new ViewGroup[1];
        for (a = 0; a < 1; a++) {
            this.inputFieldContainers[a] = new FrameLayout(context);
            this.linearLayout2.addView(this.inputFieldContainers[a], LayoutHelper.createLinear(-1, 50));
            this.inputFieldContainers[a].setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(129);
            this.inputFields[a].setMaxLines(1);
            this.inputFields[a].setLines(1);
            this.inputFields[a].setSingleLine(true);
            this.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.inputFields[a].setTypeface(Typeface.DEFAULT);
            this.inputFields[a].setImeOptions(NUM);
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            this.inputFieldContainers[a].addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$5(this));
            this.inputFields[a].setCustomSelectionActionModeCallback(new Callback() {
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
        }
        this.passwordInfoRequestTextView = new TextInfoPrivacyCell(context);
        this.passwordInfoRequestTextView.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", NUM, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        this.passwordForgotButton = new TextView(context);
        this.passwordForgotButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString("ForgotPassword", NUM));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        this.linearLayout2.addView(this.passwordForgotButton, LayoutHelper.createLinear(-2, 30, (LocaleController.isRTL ? 5 : 3) | 48, 21, 0, 21, 0));
        this.passwordForgotButton.setOnClickListener(new PassportActivity$$Lambda$6(this));
        updatePasswordInterface();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createPasswordInterface$6$PassportActivity(View v) {
        TwoStepVerificationActivity activity = new TwoStepVerificationActivity(this.currentAccount, 1);
        activity.setCloseAfterSet(true);
        activity.setCurrentPasswordInfo(new byte[0], this.currentPassword);
        presentFragment(activity);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createPasswordInterface$7$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createPasswordInterface$12$PassportActivity(View v) {
        if (this.currentPassword.has_recovery) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new PassportActivity$$Lambda$70(this), 10), this.classGuid);
        } else if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", NUM), new PassportActivity$$Lambda$71(this));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
            builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", NUM));
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$10$PassportActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$72(this, error, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$9$PassportActivity(TL_error error, TLObject response) {
        needHideProgress();
        if (error == null) {
            TL_auth_passwordRecovery res = (TL_auth_passwordRecovery) response;
            Builder builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, res.email_pattern));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new PassportActivity$$Lambda$73(this, res));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            String timeString;
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$8$PassportActivity(TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
        fragment.setRecoveryParams(this.currentPassword);
        this.currentPassword.email_unconfirmed_pattern = res.email_pattern;
        presentFragment(fragment);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$11$PassportActivity(DialogInterface dialog, int which) {
        Browser.openUrl(getParentActivity(), "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    private void onPasswordDone(boolean saved) {
        String textPassword;
        if (saved) {
            textPassword = null;
        } else {
            textPassword = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(textPassword)) {
                onPasscodeError(false);
                return;
            }
            showEditDoneProgress(true, true);
        }
        Utilities.globalQueue.postRunnable(new PassportActivity$$Lambda$7(this, saved, textPassword));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onPasswordDone$13$PassportActivity(boolean saved, String textPassword) {
        byte[] x_bytes;
        final TL_account_getPasswordSettings req = new TL_account_getPasswordSettings();
        if (saved) {
            x_bytes = this.savedPasswordHash;
        } else if (this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(AndroidUtilities.getStringBytes(textPassword), (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        final boolean z = saved;
        final String str = textPassword;
        RequestDelegate requestDelegate = new RequestDelegate() {
            private void openRequestInterface() {
                if (PassportActivity.this.inputFields != null) {
                    int type;
                    if (!z) {
                        UserConfig.getInstance(PassportActivity.this.currentAccount).savePassword(x_bytes, PassportActivity.this.saltedPassword);
                    }
                    AndroidUtilities.hideKeyboard(PassportActivity.this.inputFields[0]);
                    PassportActivity.this.ignoreOnFailure = true;
                    if (PassportActivity.this.currentBotId == 0) {
                        type = 8;
                    } else {
                        type = 0;
                    }
                    PassportActivity activity = new PassportActivity(type, PassportActivity.this.currentBotId, PassportActivity.this.currentScope, PassportActivity.this.currentPublicKey, PassportActivity.this.currentPayload, PassportActivity.this.currentNonce, PassportActivity.this.currentCallbackUrl, PassportActivity.this.currentForm, PassportActivity.this.currentPassword);
                    activity.currentEmail = PassportActivity.this.currentEmail;
                    activity.currentAccount = PassportActivity.this.currentAccount;
                    activity.saltedPassword = PassportActivity.this.saltedPassword;
                    activity.secureSecret = PassportActivity.this.secureSecret;
                    activity.secureSecretId = PassportActivity.this.secureSecretId;
                    activity.needActivityResult = PassportActivity.this.needActivityResult;
                    if (PassportActivity.this.parentLayout == null || !PassportActivity.this.parentLayout.checkTransitionAnimation()) {
                        PassportActivity.this.presentFragment(activity, true);
                    } else {
                        PassportActivity.this.presentAfterAnimation = activity;
                    }
                }
            }

            private void resetSecret() {
                TL_account_updatePasswordSettings req2 = new TL_account_updatePasswordSettings();
                if (PassportActivity.this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                    req2.password = SRPHelper.startCheck(x_bytes, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, PassportActivity.this.currentPassword.current_algo);
                }
                req2.new_settings = new TL_account_passwordInputSettings();
                req2.new_settings.new_secure_settings = new TL_secureSecretSettings();
                req2.new_settings.new_secure_settings.secure_secret = new byte[0];
                req2.new_settings.new_secure_settings.secure_algo = new TL_securePasswordKdfAlgoUnknown();
                req2.new_settings.new_secure_settings.secure_secret_id = 0;
                TL_account_passwordInputSettings tL_account_passwordInputSettings = req2.new_settings;
                tL_account_passwordInputSettings.flags |= 4;
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new PassportActivity$8$$Lambda$0(this));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$resetSecret$3$PassportActivity$8(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$14(this, error));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$2$PassportActivity$8(TL_error error) {
                if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                    generateNewSecret();
                    return;
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TL_account_getPassword(), new PassportActivity$8$$Lambda$15(this), 8);
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$1$PassportActivity$8(TLObject response2, TL_error error2) {
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$16(this, error2, response2));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$0$PassportActivity$8(TL_error error2, TLObject response2) {
                if (error2 == null) {
                    PassportActivity.this.currentPassword = (TL_account_password) response2;
                    TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                    resetSecret();
                }
            }

            private void generateNewSecret() {
                Utilities.globalQueue.postRunnable(new PassportActivity$8$$Lambda$1(this, x_bytes, str));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$generateNewSecret$8$PassportActivity$8(byte[] x_bytes, String textPassword) {
                Utilities.random.setSeed(PassportActivity.this.currentPassword.secure_random);
                TL_account_updatePasswordSettings req1 = new TL_account_updatePasswordSettings();
                if (PassportActivity.this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                    req1.password = SRPHelper.startCheck(x_bytes, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, PassportActivity.this.currentPassword.current_algo);
                }
                req1.new_settings = new TL_account_passwordInputSettings();
                PassportActivity.this.secureSecret = PassportActivity.this.getRandomSecret();
                PassportActivity.this.secureSecretId = Utilities.bytesToLong(Utilities.computeSHA256(PassportActivity.this.secureSecret));
                if (PassportActivity.this.currentPassword.new_secure_algo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = PassportActivity.this.currentPassword.new_secure_algo;
                    PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), newAlgo.salt);
                    byte[] key = new byte[32];
                    System.arraycopy(PassportActivity.this.saltedPassword, 0, key, 0, 32);
                    byte[] iv = new byte[16];
                    System.arraycopy(PassportActivity.this.saltedPassword, 32, iv, 0, 16);
                    Utilities.aesCbcEncryptionByteArraySafe(PassportActivity.this.secureSecret, key, iv, 0, PassportActivity.this.secureSecret.length, 0, 1);
                    req1.new_settings.new_secure_settings = new TL_secureSecretSettings();
                    req1.new_settings.new_secure_settings.secure_algo = newAlgo;
                    req1.new_settings.new_secure_settings.secure_secret = PassportActivity.this.secureSecret;
                    req1.new_settings.new_secure_settings.secure_secret_id = PassportActivity.this.secureSecretId;
                    TL_account_passwordInputSettings tL_account_passwordInputSettings = req1.new_settings;
                    tL_account_passwordInputSettings.flags |= 4;
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req1, new PassportActivity$8$$Lambda$10(this));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$7$PassportActivity$8(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$11(this, error));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$6$PassportActivity$8(TL_error error) {
                if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                    if (PassportActivity.this.currentForm == null) {
                        PassportActivity.this.currentForm = new TL_account_authorizationForm();
                    }
                    openRequestInterface();
                    return;
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TL_account_getPassword(), new PassportActivity$8$$Lambda$12(this), 8);
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$5$PassportActivity$8(TLObject response2, TL_error error2) {
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$13(this, error2, response2));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$4$PassportActivity$8(TL_error error2, TLObject response2) {
                if (error2 == null) {
                    PassportActivity.this.currentPassword = (TL_account_password) response2;
                    TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                    generateNewSecret();
                }
            }

            public void run(TLObject response, TL_error error) {
                if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TL_account_getPassword(), new PassportActivity$8$$Lambda$2(this, z), 8);
                } else if (error == null) {
                    Utilities.globalQueue.postRunnable(new PassportActivity$8$$Lambda$3(this, response, str, z));
                } else {
                    AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$4(this, z, error));
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$run$10$PassportActivity$8(boolean saved, TLObject response2, TL_error error2) {
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$9(this, error2, response2, saved));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$9$PassportActivity$8(TL_error error2, TLObject response2, boolean saved) {
                if (error2 == null) {
                    PassportActivity.this.currentPassword = (TL_account_password) response2;
                    TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                    PassportActivity.this.onPasswordDone(saved);
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$run$15$PassportActivity$8(TLObject response, String textPassword, boolean saved) {
                byte[] secure_salt;
                TL_account_passwordSettings settings = (TL_account_passwordSettings) response;
                TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo;
                if (settings.secure_settings != null) {
                    PassportActivity.this.secureSecret = settings.secure_settings.secure_secret;
                    PassportActivity.this.secureSecretId = settings.secure_settings.secure_secret_id;
                    if (settings.secure_settings.secure_algo instanceof TL_securePasswordKdfAlgoSHA512) {
                        secure_salt = settings.secure_settings.secure_algo.salt;
                        PassportActivity.this.saltedPassword = Utilities.computeSHA512(secure_salt, AndroidUtilities.getStringBytes(textPassword), secure_salt);
                    } else if (settings.secure_settings.secure_algo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                        algo = settings.secure_settings.secure_algo;
                        secure_salt = algo.salt;
                        PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), algo.salt);
                    } else if (settings.secure_settings.secure_algo instanceof TL_securePasswordKdfAlgoUnknown) {
                        AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$5(this));
                        return;
                    } else {
                        secure_salt = new byte[0];
                    }
                } else {
                    if (PassportActivity.this.currentPassword.new_secure_algo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                        algo = (TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                        secure_salt = algo.salt;
                        PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), algo.salt);
                    } else {
                        secure_salt = new byte[0];
                    }
                    PassportActivity.this.secureSecret = null;
                    PassportActivity.this.secureSecretId = 0;
                }
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$6(this, settings, saved, secure_salt));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$11$PassportActivity$8() {
                AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$14$PassportActivity$8(TL_account_passwordSettings settings, boolean saved, byte[] secure_salt) {
                PassportActivity.this.currentEmail = settings.email;
                if (saved) {
                    PassportActivity.this.saltedPassword = PassportActivity.this.savedSaltedPassword;
                }
                if (!PassportActivity.checkSecret(PassportActivity.this.decryptSecret(PassportActivity.this.secureSecret, PassportActivity.this.saltedPassword), Long.valueOf(PassportActivity.this.secureSecretId)) || secure_salt.length == 0 || PassportActivity.this.secureSecretId == 0) {
                    if (saved) {
                        UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                        PassportActivity.this.usingSavedPassword = 0;
                        PassportActivity.this.updatePasswordInterface();
                        return;
                    }
                    if (PassportActivity.this.currentForm != null) {
                        PassportActivity.this.currentForm.values.clear();
                        PassportActivity.this.currentForm.errors.clear();
                    }
                    if (PassportActivity.this.secureSecret == null || PassportActivity.this.secureSecret.length == 0) {
                        generateNewSecret();
                    } else {
                        resetSecret();
                    }
                } else if (PassportActivity.this.currentBotId == 0) {
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TL_account_getAllSecureValues(), new PassportActivity$8$$Lambda$7(this));
                } else {
                    openRequestInterface();
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$13$PassportActivity$8(TLObject response1, TL_error error1) {
                AndroidUtilities.runOnUIThread(new PassportActivity$8$$Lambda$8(this, response1, error1));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$12$PassportActivity$8(TLObject response1, TL_error error1) {
                if (response1 != null) {
                    PassportActivity.this.currentForm = new TL_account_authorizationForm();
                    Vector vector = (Vector) response1;
                    int size = vector.objects.size();
                    for (int a = 0; a < size; a++) {
                        PassportActivity.this.currentForm.values.add((TL_secureValue) vector.objects.get(a));
                    }
                    openRequestInterface();
                    return;
                }
                if ("APP_VERSION_OUTDATED".equals(error1.text)) {
                    AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                } else {
                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", NUM), error1.text);
                }
                PassportActivity.this.showEditDoneProgress(true, false);
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$run$16$PassportActivity$8(boolean saved, TL_error error) {
                if (saved) {
                    UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                    PassportActivity.this.usingSavedPassword = 0;
                    PassportActivity.this.updatePasswordInterface();
                    if (PassportActivity.this.inputFieldContainers != null && PassportActivity.this.inputFieldContainers[0].getVisibility() == 0) {
                        PassportActivity.this.inputFields[0].requestFocus();
                        AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
                        return;
                    }
                    return;
                }
                PassportActivity.this.showEditDoneProgress(true, false);
                if (error.text.equals("PASSWORD_HASH_INVALID")) {
                    PassportActivity.this.onPasscodeError(true);
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                } else {
                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
                }
            }
        };
        TL_error error;
        if (this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, this.currentPassword.current_algo);
            if (req.password == null) {
                error = new TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run(null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10), this.classGuid);
            return;
        }
        error = new TL_error();
        error.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error);
    }

    private boolean isPersonalDocument(SecureValueType type) {
        return (type instanceof TL_secureValueTypeDriverLicense) || (type instanceof TL_secureValueTypePassport) || (type instanceof TL_secureValueTypeInternalPassport) || (type instanceof TL_secureValueTypeIdentityCard);
    }

    private boolean isAddressDocument(SecureValueType type) {
        return (type instanceof TL_secureValueTypeUtilityBill) || (type instanceof TL_secureValueTypeBankStatement) || (type instanceof TL_secureValueTypePassportRegistration) || (type instanceof TL_secureValueTypeTemporaryRegistration) || (type instanceof TL_secureValueTypeRentalAgreement);
    }

    /* JADX WARNING: Missing block: B:115:0x0377, code skipped:
            if (isPersonalDocument(r6.type) == false) goto L_0x0379;
     */
    private void createRequestInterface(android.content.Context r44) {
        /*
        r43 = this;
        r23 = 0;
        r0 = r43;
        r4 = r0.currentForm;
        if (r4 == 0) goto L_0x0032;
    L_0x0008:
        r16 = 0;
    L_0x000a:
        r0 = r43;
        r4 = r0.currentForm;
        r4 = r4.users;
        r4 = r4.size();
        r0 = r16;
        if (r0 >= r4) goto L_0x0032;
    L_0x0018:
        r0 = r43;
        r4 = r0.currentForm;
        r4 = r4.users;
        r0 = r16;
        r42 = r4.get(r0);
        r42 = (org.telegram.tgnet.TLRPC.User) r42;
        r0 = r42;
        r4 = r0.id;
        r0 = r43;
        r5 = r0.currentBotId;
        if (r4 != r5) goto L_0x01c2;
    L_0x0030:
        r23 = r42;
    L_0x0032:
        r0 = r43;
        r0 = r0.fragmentView;
        r24 = r0;
        r24 = (android.widget.FrameLayout) r24;
        r0 = r43;
        r4 = r0.actionBar;
        r5 = "TelegramPassport";
        r9 = NUM; // 0x7f0CLASSNAME float:1.8613912E38 double:1.053098548E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r9);
        r4.setTitle(r5);
        r0 = r43;
        r4 = r0.actionBar;
        r4 = r4.createMenu();
        r5 = 1;
        r9 = NUM; // 0x7var_cf float:1.7945517E38 double:1.052935732E-314;
        r4.addItem(r5, r9);
        if (r23 == 0) goto L_0x0131;
    L_0x005c:
        r19 = new android.widget.FrameLayout;
        r0 = r19;
        r1 = r44;
        r0.<init>(r1);
        r0 = r43;
        r4 = r0.linearLayout2;
        r5 = -1;
        r9 = 100;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r9);
        r0 = r19;
        r4.addView(r0, r5);
        r21 = new org.telegram.ui.Components.BackupImageView;
        r0 = r21;
        r1 = r44;
        r0.<init>(r1);
        r4 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r21;
        r0.setRoundRadius(r4);
        r4 = 64;
        r5 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r6 = 17;
        r7 = 0;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r9 = 0;
        r10 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10);
        r0 = r19;
        r1 = r21;
        r0.addView(r1, r4);
        r20 = new org.telegram.ui.Components.AvatarDrawable;
        r0 = r20;
        r1 = r23;
        r0.<init>(r1);
        r32 = 0;
        r0 = r23;
        r4 = r0.photo;
        if (r4 == 0) goto L_0x00b8;
    L_0x00b0:
        r0 = r23;
        r4 = r0.photo;
        r0 = r4.photo_small;
        r32 = r0;
    L_0x00b8:
        r4 = "50_50";
        r0 = r21;
        r1 = r32;
        r2 = r20;
        r3 = r23;
        r0.setImage(r1, r4, r2, r3);
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r44;
        r4.<init>(r0);
        r0 = r43;
        r0.bottomCell = r4;
        r0 = r43;
        r4 = r0.bottomCell;
        r5 = NUM; // 0x7var_d float:1.7944864E38 double:1.0529355727E-314;
        r9 = "windowBackgroundGrayShadow";
        r0 = r44;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r9);
        r4.setBackgroundDrawable(r5);
        r0 = r43;
        r4 = r0.bottomCell;
        r5 = "PassportRequest";
        r9 = NUM; // 0x7f0CLASSNAMEfc float:1.8612818E38 double:1.053098282E-314;
        r10 = 1;
        r10 = new java.lang.Object[r10];
        r11 = 0;
        r12 = org.telegram.messenger.UserObject.getFirstName(r23);
        r10[r11] = r12;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r10);
        r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5);
        r4.setText(r5);
        r0 = r43;
        r4 = r0.bottomCell;
        r4 = r4.getTextView();
        r5 = 1;
        r4.setGravity(r5);
        r0 = r43;
        r4 = r0.bottomCell;
        r4 = r4.getTextView();
        r4 = r4.getLayoutParams();
        r4 = (android.widget.FrameLayout.LayoutParams) r4;
        r5 = 1;
        r4.gravity = r5;
        r0 = r43;
        r4 = r0.linearLayout2;
        r0 = r43;
        r5 = r0.bottomCell;
        r9 = -1;
        r10 = -2;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10);
        r4.addView(r5, r9);
    L_0x0131:
        r4 = new org.telegram.ui.Cells.HeaderCell;
        r0 = r44;
        r4.<init>(r0);
        r0 = r43;
        r0.headerCell = r4;
        r0 = r43;
        r4 = r0.headerCell;
        r5 = "PassportRequestedInformation";
        r9 = NUM; // 0x7f0CLASSNAMEfe float:1.8612822E38 double:1.053098283E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r9);
        r4.setText(r5);
        r0 = r43;
        r4 = r0.headerCell;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setBackgroundColor(r5);
        r0 = r43;
        r4 = r0.linearLayout2;
        r0 = r43;
        r5 = r0.headerCell;
        r9 = -1;
        r10 = -2;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10);
        r4.addView(r5, r9);
        r0 = r43;
        r4 = r0.currentForm;
        if (r4 == 0) goto L_0x03e0;
    L_0x0172:
        r0 = r43;
        r4 = r0.currentForm;
        r4 = r4.required_types;
        r38 = r4.size();
        r31 = new java.util.ArrayList;
        r31.<init>();
        r18 = new java.util.ArrayList;
        r18.<init>();
        r30 = 0;
        r17 = 0;
        r26 = 0;
        r25 = 0;
        r16 = 0;
    L_0x0190:
        r0 = r16;
        r1 = r38;
        if (r0 >= r1) goto L_0x028d;
    L_0x0196:
        r0 = r43;
        r4 = r0.currentForm;
        r4 = r4.required_types;
        r0 = r16;
        r34 = r4.get(r0);
        r34 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r34;
        r0 = r34;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 == 0) goto L_0x01ea;
    L_0x01aa:
        r6 = r34;
        r6 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r6;
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isPersonalDocument(r4);
        if (r4 == 0) goto L_0x01c6;
    L_0x01b8:
        r0 = r31;
        r0.add(r6);
        r30 = r30 + 1;
    L_0x01bf:
        r16 = r16 + 1;
        goto L_0x0190;
    L_0x01c2:
        r16 = r16 + 1;
        goto L_0x000a;
    L_0x01c6:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isAddressDocument(r4);
        if (r4 == 0) goto L_0x01d8;
    L_0x01d0:
        r0 = r18;
        r0.add(r6);
        r17 = r17 + 1;
        goto L_0x01bf;
    L_0x01d8:
        r4 = r6.type;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        if (r4 == 0) goto L_0x01e1;
    L_0x01de:
        r26 = 1;
        goto L_0x01bf;
    L_0x01e1:
        r4 = r6.type;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r4 == 0) goto L_0x01bf;
    L_0x01e7:
        r25 = 1;
        goto L_0x01bf;
    L_0x01ea:
        r0 = r34;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf;
        if (r4 == 0) goto L_0x01bf;
    L_0x01f0:
        r33 = r34;
        r33 = (org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r33;
        r0 = r33;
        r4 = r0.types;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x01bf;
    L_0x01fe:
        r0 = r33;
        r4 = r0.types;
        r5 = 0;
        r29 = r4.get(r5);
        r29 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r29;
        r0 = r29;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 == 0) goto L_0x01bf;
    L_0x020f:
        r6 = r29;
        r6 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r6;
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isPersonalDocument(r4);
        if (r4 == 0) goto L_0x0250;
    L_0x021d:
        r22 = 0;
        r0 = r33;
        r4 = r0.types;
        r39 = r4.size();
    L_0x0227:
        r0 = r22;
        r1 = r39;
        if (r0 >= r1) goto L_0x024c;
    L_0x022d:
        r0 = r33;
        r4 = r0.types;
        r0 = r22;
        r29 = r4.get(r0);
        r29 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r29;
        r0 = r29;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 != 0) goto L_0x0242;
    L_0x023f:
        r22 = r22 + 1;
        goto L_0x0227;
    L_0x0242:
        r4 = r29;
        r4 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r4;
        r0 = r31;
        r0.add(r4);
        goto L_0x023f;
    L_0x024c:
        r30 = r30 + 1;
        goto L_0x01bf;
    L_0x0250:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isAddressDocument(r4);
        if (r4 == 0) goto L_0x01bf;
    L_0x025a:
        r22 = 0;
        r0 = r33;
        r4 = r0.types;
        r39 = r4.size();
    L_0x0264:
        r0 = r22;
        r1 = r39;
        if (r0 >= r1) goto L_0x0289;
    L_0x026a:
        r0 = r33;
        r4 = r0.types;
        r0 = r22;
        r29 = r4.get(r0);
        r29 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r29;
        r0 = r29;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 != 0) goto L_0x027f;
    L_0x027c:
        r22 = r22 + 1;
        goto L_0x0264;
    L_0x027f:
        r4 = r29;
        r4 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r4;
        r0 = r18;
        r0.add(r4);
        goto L_0x027c;
    L_0x0289:
        r17 = r17 + 1;
        goto L_0x01bf;
    L_0x028d:
        if (r26 == 0) goto L_0x0294;
    L_0x028f:
        r4 = 1;
        r0 = r30;
        if (r0 <= r4) goto L_0x02de;
    L_0x0294:
        r36 = 1;
    L_0x0296:
        if (r25 == 0) goto L_0x029d;
    L_0x0298:
        r4 = 1;
        r0 = r17;
        if (r0 <= r4) goto L_0x02e1;
    L_0x029d:
        r35 = 1;
    L_0x029f:
        r16 = 0;
    L_0x02a1:
        r0 = r16;
        r1 = r38;
        if (r0 >= r1) goto L_0x03e0;
    L_0x02a7:
        r0 = r43;
        r4 = r0.currentForm;
        r4 = r4.required_types;
        r0 = r16;
        r34 = r4.get(r0);
        r34 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r34;
        r0 = r34;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 == 0) goto L_0x0344;
    L_0x02bb:
        r6 = r34;
        r6 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r6;
        r4 = r6.type;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
        if (r4 != 0) goto L_0x02cb;
    L_0x02c5:
        r4 = r6.type;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
        if (r4 == 0) goto L_0x02e4;
    L_0x02cb:
        r7 = 0;
        r8 = 0;
    L_0x02cd:
        r4 = r38 + -1;
        r0 = r16;
        if (r0 != r4) goto L_0x03dd;
    L_0x02d3:
        r9 = 1;
    L_0x02d4:
        r4 = r43;
        r5 = r44;
        r4.addField(r5, r6, r7, r8, r9);
    L_0x02db:
        r16 = r16 + 1;
        goto L_0x02a1;
    L_0x02de:
        r36 = 0;
        goto L_0x0296;
    L_0x02e1:
        r35 = 0;
        goto L_0x029f;
    L_0x02e4:
        r4 = r6.type;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        if (r4 == 0) goto L_0x02f2;
    L_0x02ea:
        if (r36 == 0) goto L_0x02ef;
    L_0x02ec:
        r7 = 0;
    L_0x02ed:
        r8 = 0;
        goto L_0x02cd;
    L_0x02ef:
        r7 = r31;
        goto L_0x02ed;
    L_0x02f2:
        r4 = r6.type;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r4 == 0) goto L_0x0300;
    L_0x02f8:
        if (r35 == 0) goto L_0x02fd;
    L_0x02fa:
        r7 = 0;
    L_0x02fb:
        r8 = 0;
        goto L_0x02cd;
    L_0x02fd:
        r7 = r18;
        goto L_0x02fb;
    L_0x0300:
        if (r36 == 0) goto L_0x0322;
    L_0x0302:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isPersonalDocument(r4);
        if (r4 == 0) goto L_0x0322;
    L_0x030c:
        r7 = new java.util.ArrayList;
        r7.<init>();
        r7.add(r6);
        r6 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r6.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
        r4.<init>();
        r6.type = r4;
        r8 = 1;
        goto L_0x02cd;
    L_0x0322:
        if (r35 == 0) goto L_0x02db;
    L_0x0324:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isAddressDocument(r4);
        if (r4 == 0) goto L_0x02db;
    L_0x032e:
        r7 = new java.util.ArrayList;
        r7.<init>();
        r7.add(r6);
        r6 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r6.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
        r4.<init>();
        r6.type = r4;
        r8 = 1;
        goto L_0x02cd;
    L_0x0344:
        r0 = r34;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf;
        if (r4 == 0) goto L_0x02db;
    L_0x034a:
        r33 = r34;
        r33 = (org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r33;
        r0 = r33;
        r4 = r0.types;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x02db;
    L_0x0358:
        r0 = r33;
        r4 = r0.types;
        r5 = 0;
        r29 = r4.get(r5);
        r29 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r29;
        r0 = r29;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 == 0) goto L_0x02db;
    L_0x0369:
        r6 = r29;
        r6 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r6;
        if (r36 == 0) goto L_0x0379;
    L_0x036f:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isPersonalDocument(r4);
        if (r4 != 0) goto L_0x0385;
    L_0x0379:
        if (r35 == 0) goto L_0x02db;
    L_0x037b:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isAddressDocument(r4);
        if (r4 == 0) goto L_0x02db;
    L_0x0385:
        r7 = new java.util.ArrayList;
        r7.<init>();
        r22 = 0;
        r0 = r33;
        r4 = r0.types;
        r39 = r4.size();
    L_0x0394:
        r0 = r22;
        r1 = r39;
        if (r0 >= r1) goto L_0x03b7;
    L_0x039a:
        r0 = r33;
        r4 = r0.types;
        r0 = r22;
        r29 = r4.get(r0);
        r29 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r29;
        r0 = r29;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 != 0) goto L_0x03af;
    L_0x03ac:
        r22 = r22 + 1;
        goto L_0x0394;
    L_0x03af:
        r4 = r29;
        r4 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r4;
        r7.add(r4);
        goto L_0x03ac;
    L_0x03b7:
        r4 = r6.type;
        r0 = r43;
        r4 = r0.isPersonalDocument(r4);
        if (r4 == 0) goto L_0x03d0;
    L_0x03c1:
        r6 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r6.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
        r4.<init>();
        r6.type = r4;
    L_0x03cd:
        r8 = 1;
        goto L_0x02cd;
    L_0x03d0:
        r6 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r6.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
        r4.<init>();
        r6.type = r4;
        goto L_0x03cd;
    L_0x03dd:
        r9 = 0;
        goto L_0x02d4;
    L_0x03e0:
        if (r23 == 0) goto L_0x04c6;
    L_0x03e2:
        r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r44;
        r4.<init>(r0);
        r0 = r43;
        r0.bottomCell = r4;
        r0 = r43;
        r4 = r0.bottomCell;
        r5 = NUM; // 0x7var_c float:1.7944862E38 double:1.052935572E-314;
        r9 = "windowBackgroundGrayShadow";
        r0 = r44;
        r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r5, r9);
        r4.setBackgroundDrawable(r5);
        r0 = r43;
        r4 = r0.bottomCell;
        r5 = "windowBackgroundWhiteGrayText4";
        r4.setLinkTextColorKey(r5);
        r0 = r43;
        r4 = r0.currentForm;
        r4 = r4.privacy_policy_url;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x05cf;
    L_0x0416:
        r4 = "PassportPolicy";
        r5 = NUM; // 0x7f0CLASSNAMEf9 float:1.8612812E38 double:1.0530982804E-314;
        r9 = 2;
        r9 = new java.lang.Object[r9];
        r10 = 0;
        r11 = org.telegram.messenger.UserObject.getFirstName(r23);
        r9[r10] = r11;
        r10 = 1;
        r0 = r23;
        r11 = r0.username;
        r9[r10] = r11;
        r40 = org.telegram.messenger.LocaleController.formatString(r4, r5, r9);
        r41 = new android.text.SpannableStringBuilder;
        r0 = r41;
        r1 = r40;
        r0.<init>(r1);
        r4 = 42;
        r0 = r40;
        r27 = r0.indexOf(r4);
        r4 = 42;
        r0 = r40;
        r28 = r0.lastIndexOf(r4);
        r4 = -1;
        r0 = r27;
        if (r0 == r4) goto L_0x048e;
    L_0x044f:
        r4 = -1;
        r0 = r28;
        if (r0 == r4) goto L_0x048e;
    L_0x0454:
        r0 = r43;
        r4 = r0.bottomCell;
        r4 = r4.getTextView();
        r5 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy;
        r5.<init>();
        r4.setMovementMethod(r5);
        r4 = r28 + 1;
        r5 = "";
        r0 = r41;
        r1 = r28;
        r0.replace(r1, r4, r5);
        r4 = r27 + 1;
        r5 = "";
        r0 = r41;
        r1 = r27;
        r0.replace(r1, r4, r5);
        r4 = new org.telegram.ui.PassportActivity$LinkSpan;
        r0 = r43;
        r4.<init>();
        r5 = r28 + -1;
        r9 = 33;
        r0 = r41;
        r1 = r27;
        r0.setSpan(r4, r1, r5, r9);
    L_0x048e:
        r0 = r43;
        r4 = r0.bottomCell;
        r0 = r41;
        r4.setText(r0);
    L_0x0497:
        r0 = r43;
        r4 = r0.bottomCell;
        r4 = r4.getTextView();
        r5 = "windowBackgroundWhiteGrayText4";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setHighlightColor(r5);
        r0 = r43;
        r4 = r0.bottomCell;
        r4 = r4.getTextView();
        r5 = 1;
        r4.setGravity(r5);
        r0 = r43;
        r4 = r0.linearLayout2;
        r0 = r43;
        r5 = r0.bottomCell;
        r9 = -1;
        r10 = -2;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10);
        r4.addView(r5, r9);
    L_0x04c6:
        r4 = new android.widget.FrameLayout;
        r0 = r44;
        r4.<init>(r0);
        r0 = r43;
        r0.bottomLayout = r4;
        r0 = r43;
        r4 = r0.bottomLayout;
        r5 = "passport_authorizeBackground";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r9 = "passport_authorizeBackgroundSelected";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r5 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r5, r9);
        r4.setBackgroundDrawable(r5);
        r0 = r43;
        r4 = r0.bottomLayout;
        r5 = -1;
        r9 = 48;
        r10 = 80;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r9, r10);
        r0 = r24;
        r0.addView(r4, r5);
        r0 = r43;
        r4 = r0.bottomLayout;
        r5 = new org.telegram.ui.PassportActivity$$Lambda$8;
        r0 = r43;
        r5.<init>(r0);
        r4.setOnClickListener(r5);
        r4 = new android.widget.TextView;
        r0 = r44;
        r4.<init>(r0);
        r0 = r43;
        r0.acceptTextView = r4;
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setCompoundDrawablePadding(r5);
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = NUM; // 0x7var_f float:1.794464E38 double:1.0529355183E-314;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r4.setCompoundDrawablesWithIntrinsicBounds(r5, r9, r10, r11);
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = "passport_authorizeText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setTextColor(r5);
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = "PassportAuthorize";
        r9 = NUM; // 0x7f0CLASSNAME float:1.8612573E38 double:1.053098222E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r9);
        r4.setText(r5);
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = 1;
        r9 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4.setTextSize(r5, r9);
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = 17;
        r4.setGravity(r5);
        r0 = r43;
        r4 = r0.acceptTextView;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r4.setTypeface(r5);
        r0 = r43;
        r4 = r0.bottomLayout;
        r0 = r43;
        r5 = r0.acceptTextView;
        r9 = -2;
        r10 = -1;
        r11 = 17;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11);
        r4.addView(r5, r9);
        r4 = new org.telegram.ui.Components.ContextProgressView;
        r5 = 0;
        r0 = r44;
        r4.<init>(r0, r5);
        r0 = r43;
        r0.progressViewButton = r4;
        r0 = r43;
        r4 = r0.progressViewButton;
        r5 = 4;
        r4.setVisibility(r5);
        r0 = r43;
        r4 = r0.bottomLayout;
        r0 = r43;
        r5 = r0.progressViewButton;
        r9 = -1;
        r10 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10);
        r4.addView(r5, r9);
        r37 = new android.view.View;
        r0 = r37;
        r1 = r44;
        r0.<init>(r1);
        r4 = NUM; // 0x7var_ float:1.7944888E38 double:1.0529355786E-314;
        r0 = r37;
        r0.setBackgroundResource(r4);
        r9 = -1;
        r10 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r11 = 83;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r15 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15);
        r0 = r24;
        r1 = r37;
        r0.addView(r1, r4);
        return;
    L_0x05cf:
        r0 = r43;
        r4 = r0.bottomCell;
        r5 = "PassportNoPolicy";
        r9 = NUM; // 0x7f0CLASSNAMEed float:1.8612788E38 double:1.0530982744E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];
        r11 = 0;
        r12 = org.telegram.messenger.UserObject.getFirstName(r23);
        r10[r11] = r12;
        r11 = 1;
        r0 = r23;
        r12 = r0.username;
        r10[r11] = r12;
        r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r10);
        r5 = org.telegram.messenger.AndroidUtilities.replaceTags(r5);
        r4.setText(r5);
        goto L_0x0497;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createRequestInterface(android.content.Context):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createRequestInterface$16$PassportActivity(View view) {
        int size2;
        int b;
        JSONObject result;
        ArrayList<AnonymousClass1ValueToSend> valuesToSend = new ArrayList();
        int a = 0;
        int size = this.currentForm.required_types.size();
        while (a < size) {
            TL_secureRequiredType requiredType;
            SecureRequiredType secureRequiredType = (SecureRequiredType) this.currentForm.required_types.get(a);
            if (secureRequiredType instanceof TL_secureRequiredType) {
                requiredType = (TL_secureRequiredType) secureRequiredType;
            } else {
                if (secureRequiredType instanceof TL_secureRequiredTypeOneOf) {
                    TL_secureRequiredTypeOneOf requiredTypeOneOf = (TL_secureRequiredTypeOneOf) secureRequiredType;
                    if (requiredTypeOneOf.types.isEmpty()) {
                        continue;
                    } else {
                        secureRequiredType = (SecureRequiredType) requiredTypeOneOf.types.get(0);
                        if (secureRequiredType instanceof TL_secureRequiredType) {
                            requiredType = (TL_secureRequiredType) secureRequiredType;
                            size2 = requiredTypeOneOf.types.size();
                            for (b = 0; b < size2; b++) {
                                secureRequiredType = (SecureRequiredType) requiredTypeOneOf.types.get(b);
                                if (secureRequiredType instanceof TL_secureRequiredType) {
                                    TL_secureRequiredType innerType = (TL_secureRequiredType) secureRequiredType;
                                    if (getValueByType(innerType, true) != null) {
                                        requiredType = innerType;
                                        break;
                                    }
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
                a++;
            }
            TL_secureValue value = getValueByType(requiredType, true);
            Vibrator v;
            if (value == null) {
                v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(getViewByType(requiredType), 2.0f, 0);
                return;
            }
            HashMap<String, String> errors = (HashMap) this.errorsMap.get(getNameForType(requiredType.type));
            if (errors == null || errors.isEmpty()) {
                valuesToSend.add(new AnonymousClass1ValueToSend(value, requiredType.selfie_required, requiredType.translation_required));
                a++;
            } else {
                v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(getViewByType(requiredType), 2.0f, 0);
                return;
            }
        }
        showEditDoneProgress(false, true);
        TLObject req = new TL_account_acceptAuthorization();
        req.bot_id = this.currentBotId;
        req.scope = this.currentScope;
        req.public_key = this.currentPublicKey;
        JSONObject jsonObject = new JSONObject();
        size = valuesToSend.size();
        for (a = 0; a < size; a++) {
            AnonymousClass1ValueToSend valueToSend = (AnonymousClass1ValueToSend) valuesToSend.get(a);
            TL_secureValue secureValue = valueToSend.value;
            JSONObject data = new JSONObject();
            if (secureValue.plain_data == null) {
                try {
                    byte[] decryptedSecret;
                    TL_secureFile secureFile;
                    JSONObject file;
                    result = new JSONObject();
                    if (secureValue.data != null) {
                        decryptedSecret = decryptValueSecret(secureValue.data.secret, secureValue.data.data_hash);
                        data.put("data_hash", Base64.encodeToString(secureValue.data.data_hash, 2));
                        data.put("secret", Base64.encodeToString(decryptedSecret, 2));
                        result.put("data", data);
                    }
                    if (!secureValue.files.isEmpty()) {
                        JSONArray files = new JSONArray();
                        size2 = secureValue.files.size();
                        for (b = 0; b < size2; b++) {
                            secureFile = (TL_secureFile) secureValue.files.get(b);
                            decryptedSecret = decryptValueSecret(secureFile.secret, secureFile.file_hash);
                            file = new JSONObject();
                            file.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                            file.put("secret", Base64.encodeToString(decryptedSecret, 2));
                            files.put(file);
                        }
                        result.put("files", files);
                    }
                    if (secureValue.front_side instanceof TL_secureFile) {
                        secureFile = (TL_secureFile) secureValue.front_side;
                        decryptedSecret = decryptValueSecret(secureFile.secret, secureFile.file_hash);
                        JSONObject front = new JSONObject();
                        front.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                        front.put("secret", Base64.encodeToString(decryptedSecret, 2));
                        result.put("front_side", front);
                    }
                    if (secureValue.reverse_side instanceof TL_secureFile) {
                        secureFile = (TL_secureFile) secureValue.reverse_side;
                        decryptedSecret = decryptValueSecret(secureFile.secret, secureFile.file_hash);
                        JSONObject reverse = new JSONObject();
                        reverse.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                        reverse.put("secret", Base64.encodeToString(decryptedSecret, 2));
                        result.put("reverse_side", reverse);
                    }
                    if (valueToSend.selfie_required && (secureValue.selfie instanceof TL_secureFile)) {
                        secureFile = (TL_secureFile) secureValue.selfie;
                        decryptedSecret = decryptValueSecret(secureFile.secret, secureFile.file_hash);
                        JSONObject selfie = new JSONObject();
                        selfie.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                        selfie.put("secret", Base64.encodeToString(decryptedSecret, 2));
                        result.put("selfie", selfie);
                    }
                    if (valueToSend.translation_required && !secureValue.translation.isEmpty()) {
                        JSONArray translation = new JSONArray();
                        size2 = secureValue.translation.size();
                        for (b = 0; b < size2; b++) {
                            secureFile = (TL_secureFile) secureValue.translation.get(b);
                            decryptedSecret = decryptValueSecret(secureFile.secret, secureFile.file_hash);
                            file = new JSONObject();
                            file.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                            file.put("secret", Base64.encodeToString(decryptedSecret, 2));
                            translation.put(file);
                        }
                        result.put("translation", translation);
                    }
                    jsonObject.put(getNameForType(secureValue.type), result);
                } catch (Exception e) {
                }
            } else if (secureValue.plain_data instanceof TL_securePlainEmail) {
                TL_securePlainEmail tL_securePlainEmail = (TL_securePlainEmail) secureValue.plain_data;
            } else if (secureValue.plain_data instanceof TL_securePlainPhone) {
                TL_securePlainPhone tL_securePlainPhone = (TL_securePlainPhone) secureValue.plain_data;
            }
            TL_secureValueHash hash = new TL_secureValueHash();
            hash.type = secureValue.type;
            hash.hash = secureValue.hash;
            req.value_hashes.add(hash);
        }
        result = new JSONObject();
        try {
            result.put("secure_data", jsonObject);
        } catch (Exception e2) {
        }
        if (this.currentPayload != null) {
            try {
                result.put("payload", this.currentPayload);
            } catch (Exception e3) {
            }
        }
        if (this.currentNonce != null) {
            try {
                result.put("nonce", this.currentNonce);
            } catch (Exception e4) {
            }
        }
        EncryptionResult encryptionResult = encryptData(AndroidUtilities.getStringBytes(result.toString()));
        req.credentials = new TL_secureCredentialsEncrypted();
        req.credentials.hash = encryptionResult.fileHash;
        req.credentials.data = encryptionResult.encryptedData;
        try {
            String key = this.currentPublicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(key, 0)));
            Cipher c = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
            c.init(1, pubKey);
            req.credentials.secret = c.doFinal(encryptionResult.decrypyedFileSecret);
        } catch (Exception e5) {
            FileLog.e(e5);
        }
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PassportActivity$$Lambda$68(this));
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$15$PassportActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$69(this, error));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$14$PassportActivity(TL_error error) {
        if (error == null) {
            this.ignoreOnFailure = true;
            callCallback(true);
            finishFragment();
            return;
        }
        showEditDoneProgress(false, false);
        if ("APP_VERSION_OUTDATED".equals(error.text)) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    private void createManageInterface(Context context) {
        FrameLayout frameLayout = this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", NUM));
        this.actionBar.createMenu().addItem(1, NUM);
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportProvidedInformation", NUM));
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.sectionCell = new ShadowSectionCell(context);
        this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        this.addDocumentCell = new TextSettingsCell(context);
        this.addDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.addDocumentCell.setText(LocaleController.getString("PassportNoDocumentsAdd", NUM), true);
        this.linearLayout2.addView(this.addDocumentCell, LayoutHelper.createLinear(-1, -2));
        this.addDocumentCell.setOnClickListener(new PassportActivity$$Lambda$9(this));
        this.deletePassportCell = new TextSettingsCell(context);
        this.deletePassportCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
        this.deletePassportCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.deletePassportCell.setText(LocaleController.getString("TelegramPassportDelete", NUM), false);
        this.linearLayout2.addView(this.deletePassportCell, LayoutHelper.createLinear(-1, -2));
        this.deletePassportCell.setOnClickListener(new PassportActivity$$Lambda$10(this));
        this.addDocumentSectionCell = new ShadowSectionCell(context);
        this.addDocumentSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.linearLayout2.addView(this.addDocumentSectionCell, LayoutHelper.createLinear(-1, -2));
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        if (AndroidUtilities.isTablet()) {
            this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(528.0f) - ActionBar.getCurrentActionBarHeight()));
        } else {
            this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        }
        this.emptyImageView = new ImageView(context);
        this.emptyImageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sessions_devicesImage"), Mode.MULTIPLY));
        this.emptyLayout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTextView1 = new TextView(context);
        this.emptyTextView1.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 15.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView1.setText(LocaleController.getString("PassportNoDocuments", NUM));
        this.emptyLayout.addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        this.emptyTextView2 = new TextView(context);
        this.emptyTextView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.emptyTextView2.setText(LocaleController.getString("PassportNoDocumentsInfo", NUM));
        this.emptyLayout.addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        this.emptyTextView3 = new TextView(context);
        this.emptyTextView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setTextSize(1, 15.0f);
        this.emptyTextView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setText(LocaleController.getString("PassportNoDocumentsAdd", NUM).toUpperCase());
        this.emptyLayout.addView(this.emptyTextView3, LayoutHelper.createLinear(-2, 30, 17, 0, 16, 0, 0));
        this.emptyTextView3.setOnClickListener(new PassportActivity$$Lambda$11(this));
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            ArrayList<TL_secureRequiredType> documentTypes;
            TL_secureRequiredType requiredType;
            boolean documentOnly;
            boolean z;
            TL_secureValue value = (TL_secureValue) this.currentForm.values.get(a);
            if (isPersonalDocument(value.type)) {
                documentTypes = new ArrayList();
                requiredType = new TL_secureRequiredType();
                requiredType.type = value.type;
                requiredType.selfie_required = true;
                requiredType.translation_required = true;
                documentTypes.add(requiredType);
                requiredType = new TL_secureRequiredType();
                requiredType.type = new TL_secureValueTypePersonalDetails();
                documentOnly = true;
            } else if (isAddressDocument(value.type)) {
                documentTypes = new ArrayList();
                requiredType = new TL_secureRequiredType();
                requiredType.type = value.type;
                requiredType.translation_required = true;
                documentTypes.add(requiredType);
                requiredType = new TL_secureRequiredType();
                requiredType.type = new TL_secureValueTypeAddress();
                documentOnly = true;
            } else {
                requiredType = new TL_secureRequiredType();
                requiredType.type = value.type;
                documentTypes = null;
                documentOnly = false;
            }
            if (a == size - 1) {
                z = true;
            } else {
                z = false;
            }
            addField(context, requiredType, documentTypes, documentOnly, z);
        }
        updateManageVisibility();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createManageInterface$17$PassportActivity(View v) {
        openAddDocumentAlert();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createManageInterface$21$PassportActivity(View v) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PassportActivity$$Lambda$65(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("TelegramPassportDeleteAlert", NUM));
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$20$PassportActivity(DialogInterface dialog, int which) {
        TL_account_deleteSecureValue req = new TL_account_deleteSecureValue();
        for (int a = 0; a < this.currentForm.values.size(); a++) {
            req.types.add(((TL_secureValue) this.currentForm.values.get(a)).type);
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PassportActivity$$Lambda$66(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$19$PassportActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$67(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$18$PassportActivity() {
        int a = 0;
        while (a < this.linearLayout2.getChildCount()) {
            View child = this.linearLayout2.getChildAt(a);
            if (child instanceof TextDetailSecureCell) {
                this.linearLayout2.removeView(child);
                a--;
            }
            a++;
        }
        needHideProgress();
        this.typesViews.clear();
        this.typesValues.clear();
        this.currentForm.values.clear();
        updateManageVisibility();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createManageInterface$22$PassportActivity(View v) {
        openAddDocumentAlert();
    }

    private boolean hasNotValueForType(Class<? extends SecureValueType> type) {
        int count = this.currentForm.values.size();
        for (int a = 0; a < count; a++) {
            if (((TL_secureValue) this.currentForm.values.get(a)).type.getClass() == type) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnfilledValues() {
        if (hasNotValueForType(TL_secureValueTypePhone.class) || hasNotValueForType(TL_secureValueTypeEmail.class) || hasNotValueForType(TL_secureValueTypePersonalDetails.class) || hasNotValueForType(TL_secureValueTypePassport.class) || hasNotValueForType(TL_secureValueTypeInternalPassport.class) || hasNotValueForType(TL_secureValueTypeIdentityCard.class) || hasNotValueForType(TL_secureValueTypeDriverLicense.class) || hasNotValueForType(TL_secureValueTypeAddress.class) || hasNotValueForType(TL_secureValueTypeUtilityBill.class) || hasNotValueForType(TL_secureValueTypePassportRegistration.class) || hasNotValueForType(TL_secureValueTypeTemporaryRegistration.class) || hasNotValueForType(TL_secureValueTypeBankStatement.class) || hasNotValueForType(TL_secureValueTypeRentalAgreement.class)) {
            return true;
        }
        return false;
    }

    private void openAddDocumentAlert() {
        ArrayList<CharSequence> values = new ArrayList();
        ArrayList<Class<? extends SecureValueType>> types = new ArrayList();
        if (hasNotValueForType(TL_secureValueTypePhone.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPhone", NUM));
            types.add(TL_secureValueTypePhone.class);
        }
        if (hasNotValueForType(TL_secureValueTypeEmail.class)) {
            values.add(LocaleController.getString("ActionBotDocumentEmail", NUM));
            types.add(TL_secureValueTypeEmail.class);
        }
        if (hasNotValueForType(TL_secureValueTypePersonalDetails.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentity", NUM));
            types.add(TL_secureValueTypePersonalDetails.class);
        }
        if (hasNotValueForType(TL_secureValueTypePassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassport", NUM));
            types.add(TL_secureValueTypePassport.class);
        }
        if (hasNotValueForType(TL_secureValueTypeInternalPassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentInternalPassport", NUM));
            types.add(TL_secureValueTypeInternalPassport.class);
        }
        if (hasNotValueForType(TL_secureValueTypePassportRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
            types.add(TL_secureValueTypePassportRegistration.class);
        }
        if (hasNotValueForType(TL_secureValueTypeTemporaryRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
            types.add(TL_secureValueTypeTemporaryRegistration.class);
        }
        if (hasNotValueForType(TL_secureValueTypeIdentityCard.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentityCard", NUM));
            types.add(TL_secureValueTypeIdentityCard.class);
        }
        if (hasNotValueForType(TL_secureValueTypeDriverLicense.class)) {
            values.add(LocaleController.getString("ActionBotDocumentDriverLicence", NUM));
            types.add(TL_secureValueTypeDriverLicense.class);
        }
        if (hasNotValueForType(TL_secureValueTypeAddress.class)) {
            values.add(LocaleController.getString("ActionBotDocumentAddress", NUM));
            types.add(TL_secureValueTypeAddress.class);
        }
        if (hasNotValueForType(TL_secureValueTypeUtilityBill.class)) {
            values.add(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
            types.add(TL_secureValueTypeUtilityBill.class);
        }
        if (hasNotValueForType(TL_secureValueTypeBankStatement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
            types.add(TL_secureValueTypeBankStatement.class);
        }
        if (hasNotValueForType(TL_secureValueTypeRentalAgreement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
            types.add(TL_secureValueTypeRentalAgreement.class);
        }
        if (getParentActivity() != null && !values.isEmpty()) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PassportNoDocumentsAdd", NUM));
            builder.setItems((CharSequence[]) values.toArray(new CharSequence[values.size()]), new PassportActivity$$Lambda$12(this, types));
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$openAddDocumentAlert$23$PassportActivity(ArrayList types, DialogInterface dialog, int which) {
        TL_secureRequiredType requiredType = null;
        TL_secureRequiredType documentRequiredType = null;
        try {
            TL_secureRequiredType requiredType2 = new TL_secureRequiredType();
            try {
                requiredType2.type = (SecureValueType) ((Class) types.get(which)).newInstance();
                requiredType = requiredType2;
            } catch (Exception e) {
                requiredType = requiredType2;
            }
        } catch (Exception e2) {
        }
        if (isPersonalDocument(requiredType.type)) {
            documentRequiredType = requiredType;
            documentRequiredType.selfie_required = true;
            documentRequiredType.translation_required = true;
            requiredType = new TL_secureRequiredType();
            requiredType.type = new TL_secureValueTypePersonalDetails();
        } else if (isAddressDocument(requiredType.type)) {
            documentRequiredType = requiredType;
            requiredType = new TL_secureRequiredType();
            requiredType.type = new TL_secureValueTypeAddress();
        }
        openTypeActivity(requiredType, documentRequiredType, new ArrayList(), documentRequiredType != null);
    }

    private void updateManageVisibility() {
        if (this.currentForm.values.isEmpty()) {
            this.emptyLayout.setVisibility(0);
            this.sectionCell.setVisibility(8);
            this.headerCell.setVisibility(8);
            this.addDocumentCell.setVisibility(8);
            this.deletePassportCell.setVisibility(8);
            this.addDocumentSectionCell.setVisibility(8);
            return;
        }
        this.emptyLayout.setVisibility(8);
        this.sectionCell.setVisibility(0);
        this.headerCell.setVisibility(0);
        this.deletePassportCell.setVisibility(0);
        this.addDocumentSectionCell.setVisibility(0);
        if (hasUnfilledValues()) {
            this.addDocumentCell.setVisibility(0);
        } else {
            this.addDocumentCell.setVisibility(8);
        }
    }

    private void callCallback(boolean success) {
        if (!this.callbackCalled) {
            if (!TextUtils.isEmpty(this.currentCallbackUrl)) {
                if (success) {
                    Browser.openUrl(getParentActivity(), Uri.parse(this.currentCallbackUrl + "&tg_passport=success"));
                } else if (!this.ignoreOnFailure && (this.currentActivityType == 5 || this.currentActivityType == 0)) {
                    Browser.openUrl(getParentActivity(), Uri.parse(this.currentCallbackUrl + "&tg_passport=cancel"));
                }
                this.callbackCalled = true;
            } else if (this.needActivityResult) {
                if (success || (!this.ignoreOnFailure && (this.currentActivityType == 5 || this.currentActivityType == 0))) {
                    getParentActivity().setResult(success ? -1 : 0);
                }
                this.callbackCalled = true;
            }
        }
    }

    private void createEmailInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", NUM));
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell settingsCell1 = new TextSettingsCell(context);
            settingsCell1.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            settingsCell1.setText(LocaleController.formatString("PassportPhoneUseSame", NUM, this.currentEmail), false);
            this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
            settingsCell1.setOnClickListener(new PassportActivity$$Lambda$13(this));
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameEmailInfo", NUM));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
            container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(33);
            this.inputFields[a].setImeOptions(NUM);
            switch (a) {
                case 0:
                    this.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", NUM));
                    if (this.currentTypeValue != null && (this.currentTypeValue.plain_data instanceof TL_securePlainEmail)) {
                        TL_securePlainEmail securePlainEmail = this.currentTypeValue.plain_data;
                        if (!TextUtils.isEmpty(securePlainEmail.email)) {
                            this.inputFields[a].setText(securePlainEmail.email);
                            break;
                        }
                    }
                    break;
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$14(this));
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.getString("PassportEmailUploadInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createEmailInterface$24$PassportActivity(View v) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createEmailInterface$25$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    private void createPhoneInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", NUM));
        this.languageMap = new HashMap();
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
                if (args.length > 3) {
                    this.phoneFormatMap.put(args[0], args[3]);
                }
                this.languageMap.put(args[1], args[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        Collections.sort(this.countriesArray, PassportActivity$$Lambda$15.$instance);
        String currentPhone = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
        View textSettingsCell = new TextSettingsCell(context);
        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        textSettingsCell.setText(LocaleController.formatString("PassportPhoneUseSame", NUM, PhoneFormat.getInstance().format("+" + currentPhone)), false);
        this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
        textSettingsCell.setOnClickListener(new PassportActivity$$Lambda$16(this));
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportPhoneUseOther", NUM));
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[3];
        for (int a = 0; a < 3; a++) {
            ViewGroup container;
            if (a == 2) {
                this.inputFields[a] = new HintEditText(context);
            } else {
                this.inputFields[a] = new EditTextBoldCursor(context);
            }
            if (a == 1) {
                container = new LinearLayout(context);
                ((LinearLayout) container).setOrientation(0);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
                container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (a == 2) {
                container = (ViewGroup) this.inputFields[1].getParent();
            } else {
                container = new FrameLayout(context);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
                container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            if (a == 0) {
                this.inputFields[a].setOnTouchListener(new PassportActivity$$Lambda$17(this));
                this.inputFields[a].setText(LocaleController.getString("ChooseCountry", NUM));
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(3);
                if (a == 2) {
                    this.inputFields[a].setImeOptions(NUM);
                } else {
                    this.inputFields[a].setImeOptions(NUM);
                }
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            if (a == 1) {
                this.plusTextView = new TextView(context);
                this.plusTextView.setText("+");
                this.plusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.plusTextView.setTextSize(1, 16.0f);
                container.addView(this.plusTextView, LayoutHelper.createLinear(-2, -2, 21.0f, 12.0f, 0.0f, 6.0f));
                this.inputFields[a].setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
                this.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(5)});
                this.inputFields[a].setGravity(19);
                container.addView(this.inputFields[a], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!PassportActivity.this.ignoreOnTextChange) {
                            PassportActivity.this.ignoreOnTextChange = true;
                            String text = PhoneFormat.stripExceptNumbers(PassportActivity.this.inputFields[1].getText().toString());
                            PassportActivity.this.inputFields[1].setText(text);
                            HintEditText phoneField = PassportActivity.this.inputFields[2];
                            if (text.length() == 0) {
                                phoneField.setHintText(null);
                                phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", NUM));
                                PassportActivity.this.inputFields[0].setText(LocaleController.getString("ChooseCountry", NUM));
                            } else {
                                boolean ok = false;
                                String textToSet = null;
                                if (text.length() > 4) {
                                    for (int a = 4; a >= 1; a--) {
                                        String sub = text.substring(0, a);
                                        if (((String) PassportActivity.this.codesMap.get(sub)) != null) {
                                            ok = true;
                                            textToSet = text.substring(a) + PassportActivity.this.inputFields[2].getText().toString();
                                            text = sub;
                                            PassportActivity.this.inputFields[1].setText(sub);
                                            break;
                                        }
                                    }
                                    if (!ok) {
                                        textToSet = text.substring(1) + PassportActivity.this.inputFields[2].getText().toString();
                                        EditTextBoldCursor editTextBoldCursor = PassportActivity.this.inputFields[1];
                                        text = text.substring(0, 1);
                                        editTextBoldCursor.setText(text);
                                    }
                                }
                                String country = (String) PassportActivity.this.codesMap.get(text);
                                boolean set = false;
                                if (country != null) {
                                    int index = PassportActivity.this.countriesArray.indexOf(country);
                                    if (index != -1) {
                                        PassportActivity.this.inputFields[0].setText((CharSequence) PassportActivity.this.countriesArray.get(index));
                                        String hint = (String) PassportActivity.this.phoneFormatMap.get(text);
                                        set = true;
                                        if (hint != null) {
                                            phoneField.setHintText(hint.replace('X', 8211));
                                            phoneField.setHint(null);
                                        }
                                    }
                                }
                                if (!set) {
                                    phoneField.setHintText(null);
                                    phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", NUM));
                                    PassportActivity.this.inputFields[0].setText(LocaleController.getString("WrongCountry", NUM));
                                }
                                if (!ok) {
                                    PassportActivity.this.inputFields[1].setSelection(PassportActivity.this.inputFields[1].getText().length());
                                }
                                if (textToSet != null) {
                                    phoneField.requestFocus();
                                    phoneField.setText(textToSet);
                                    phoneField.setSelection(phoneField.length());
                                }
                            }
                            PassportActivity.this.ignoreOnTextChange = false;
                        }
                    }
                });
            } else if (a == 2) {
                this.inputFields[a].setPadding(0, 0, 0, 0);
                this.inputFields[a].setGravity(19);
                this.inputFields[a].setHintText(null);
                this.inputFields[a].setHint(LocaleController.getString("PaymentShippingPhoneNumber", NUM));
                container.addView(this.inputFields[a], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 21.0f, 6.0f));
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
                        if (!PassportActivity.this.ignoreOnPhoneChange) {
                            int a;
                            HintEditText phoneField = PassportActivity.this.inputFields[2];
                            int start = phoneField.getSelectionStart();
                            String phoneChars = "NUM";
                            String str = phoneField.getText().toString();
                            if (this.characterAction == 3) {
                                str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1);
                                start--;
                            }
                            StringBuilder builder = new StringBuilder(str.length());
                            for (a = 0; a < str.length(); a++) {
                                String ch = str.substring(a, a + 1);
                                if (phoneChars.contains(ch)) {
                                    builder.append(ch);
                                }
                            }
                            PassportActivity.this.ignoreOnPhoneChange = true;
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
                            PassportActivity.this.ignoreOnPhoneChange = false;
                        }
                    }
                });
            } else {
                this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
                container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            }
            this.inputFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$18(this));
            if (a == 2) {
                this.inputFields[a].setOnKeyListener(new PassportActivity$$Lambda$19(this));
            }
            if (a == 0) {
                View divider = new View(context);
                this.dividers.add(divider);
                divider.setBackgroundColor(Theme.getColor("divider"));
                container.addView(divider, new LayoutParams(-1, 1, 83));
            }
        }
        String country = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                country = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (country != null) {
            String countryName = (String) this.languageMap.get(country);
            if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                this.inputFields[1].setText((CharSequence) this.countriesMap.get(countryName));
            }
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUploadInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createPhoneInterface$26$PassportActivity(View v) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createPhoneInterface$29$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PassportActivity$$Lambda$63(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$28$PassportActivity(String name, String shortName) {
        this.inputFields[0].setText(name);
        if (this.countriesArray.indexOf(name) != -1) {
            this.ignoreOnTextChange = true;
            String code = (String) this.countriesMap.get(name);
            this.inputFields[1].setText(code);
            String hint = (String) this.phoneFormatMap.get(code);
            this.inputFields[2].setHintText(hint != null ? hint.replace('X', 8211) : null);
            this.ignoreOnTextChange = false;
        }
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$64(this), 300);
        this.inputFields[2].requestFocus();
        this.inputFields[2].setSelection(this.inputFields[2].length());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$27$PassportActivity() {
        AndroidUtilities.showKeyboard(this.inputFields[2]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createPhoneInterface$30$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.inputFields[2].requestFocus();
            return true;
        } else if (i != 6) {
            return false;
        } else {
            this.doneItem.callOnClick();
            return true;
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createPhoneInterface$31$PassportActivity(View v, int keyCode, KeyEvent event) {
        if (keyCode != 67 || this.inputFields[2].length() != 0) {
            return false;
        }
        this.inputFields[1].requestFocus();
        this.inputFields[1].setSelection(this.inputFields[1].length());
        this.inputFields[1].dispatchKeyEvent(event);
        return true;
    }

    private void createAddressInterface(Context context) {
        this.languageMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                this.languageMap.put(args[1], args[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.topErrorCell = new TextInfoPrivacyCell(context);
        this.topErrorCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        if (this.currentDocumentsType != null) {
            String errorText;
            SpannableStringBuilder spannableStringBuilder;
            if (this.currentDocumentsType.type instanceof TL_secureValueTypeRentalAgreement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeBankStatement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeUtilityBill) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypePassportRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeTemporaryRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
            }
            this.headerCell = new HeaderCell(context);
            this.headerCell.setText(LocaleController.getString("PassportDocuments", NUM));
            this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            this.documentsLayout = new LinearLayout(context);
            this.documentsLayout.setOrientation(1);
            this.linearLayout2.addView(this.documentsLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell = new TextSettingsCell(context);
            this.uploadDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell.setOnClickListener(new PassportActivity$$Lambda$20(this));
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            if (this.currentBotId != 0) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAddressUploadInfo", NUM);
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeRentalAgreement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAgreementInfo", NUM);
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeUtilityBill) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBillInfo", NUM);
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypePassportRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddPassportRegistrationInfo", NUM);
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeTemporaryRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddTemporaryRegistrationInfo", NUM);
            } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeBankStatement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBankInfo", NUM);
            } else {
                this.noAllDocumentsErrorText = "";
            }
            CharSequence text = this.noAllDocumentsErrorText;
            if (this.documentsErrors != null) {
                errorText = (String) this.documentsErrors.get("files_all");
                if (errorText != null) {
                    spannableStringBuilder = new SpannableStringBuilder(errorText);
                    spannableStringBuilder.append("\n\n");
                    spannableStringBuilder.append(this.noAllDocumentsErrorText);
                    text = spannableStringBuilder;
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, errorText.length(), 33);
                    this.errorsValues.put("files_all", "");
                }
            }
            this.bottomCell.setText(text);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                this.headerCell = new HeaderCell(context);
                this.headerCell.setText(LocaleController.getString("PassportTranslation", NUM));
                this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                this.translationLayout = new LinearLayout(context);
                this.translationLayout.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell = new TextSettingsCell(context);
                this.uploadTranslationCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new PassportActivity$$Lambda$21(this));
                this.bottomCellTranslation = new TextInfoPrivacyCell(context);
                this.bottomCellTranslation.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeRentalAgreement) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationAgreementInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeUtilityBill) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBillInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypePassportRegistration) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationPassportRegistrationInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeTemporaryRegistration) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationTemporaryRegistrationInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeBankStatement) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBankInfo", NUM);
                } else {
                    this.noAllTranslationErrorText = "";
                }
                text = this.noAllTranslationErrorText;
                if (this.documentsErrors != null) {
                    errorText = (String) this.documentsErrors.get("translation_all");
                    if (errorText != null) {
                        spannableStringBuilder = new SpannableStringBuilder(errorText);
                        spannableStringBuilder.append("\n\n");
                        spannableStringBuilder.append(this.noAllTranslationErrorText);
                        text = spannableStringBuilder;
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, errorText.length(), 33);
                        this.errorsValues.put("translation_all", "");
                    }
                }
                this.bottomCellTranslation.setText(text);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportAddress", NUM));
        }
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportAddressHeader", NUM));
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[6];
        for (int a = 0; a < 6; a++) {
            String key;
            final EditTextBoldCursor field = new EditTextBoldCursor(context);
            this.inputFields[a] = field;
            ViewGroup container = new FrameLayout(context) {
                private StaticLayout errorLayout;
                float offsetX;

                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                    this.errorLayout = field.getErrorLayout(width);
                    if (this.errorLayout != null) {
                        int lineCount = this.errorLayout.getLineCount();
                        if (lineCount > 1) {
                            heightMeasureSpec = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                        }
                        if (LocaleController.isRTL) {
                            float maxW = 0.0f;
                            for (int a = 0; a < lineCount; a++) {
                                if (this.errorLayout.getLineLeft(a) != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                }
                                maxW = Math.max(maxW, this.errorLayout.getLineWidth(a));
                                if (a == lineCount - 1) {
                                    this.offsetX = ((float) width) - maxW;
                                }
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, field.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, -2));
            container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            if (a == 5) {
                this.extraBackgroundView = new View(context);
                this.extraBackgroundView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.documentOnly && this.currentDocumentsType != null) {
                container.setVisibility(8);
                if (this.extraBackgroundView != null) {
                    this.extraBackgroundView.setVisibility(8);
                }
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setSupportRtlHint(true);
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (a == 5) {
                this.inputFields[a].setOnTouchListener(new PassportActivity$$Lambda$22(this));
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(16385);
                this.inputFields[a].setImeOptions(NUM);
            }
            switch (a) {
                case 0:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportStreet1", NUM));
                    key = "street_line1";
                    break;
                case 1:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportStreet2", NUM));
                    key = "street_line2";
                    break;
                case 2:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportPostcode", NUM));
                    key = "post_code";
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCity", NUM));
                    key = "city";
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportState", NUM));
                    key = "state";
                    break;
                case 5:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCountry", NUM));
                    key = "country_code";
                    break;
                default:
                    break;
            }
            setFieldValues(this.currentValues, this.inputFields[a], key);
            final String str;
            if (a == 2) {
                str = key;
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    private boolean ignore;

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!this.ignore) {
                            this.ignore = true;
                            boolean error = false;
                            for (int a = 0; a < s.length(); a++) {
                                char ch = s.charAt(a);
                                if ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && !((ch >= '0' && ch <= '9') || ch == '-' || ch == ' '))) {
                                    error = true;
                                    break;
                                }
                            }
                            this.ignore = false;
                            if (error) {
                                field.setErrorText(LocaleController.getString("PassportUseLatinOnly", NUM));
                            } else {
                                PassportActivity.this.checkFieldForError(field, str, s, false);
                            }
                        }
                    }
                });
                this.inputFields[a].setFilters(new InputFilter[]{new LengthFilter(10)});
            } else {
                str = key;
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        PassportActivity.this.checkFieldForError(field, str, s, false);
                    }
                });
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, 0);
            this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, 64.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$23(this));
        }
        this.sectionCell = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.documentOnly && this.currentDocumentsType != null) {
            this.headerCell.setVisibility(8);
            this.sectionCell.setVisibility(8);
        }
        if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            if (this.documentOnly && this.currentDocumentsType != null) {
                this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            }
        } else {
            if (this.currentDocumentsTypeValue != null) {
                addDocumentViews(this.currentDocumentsTypeValue.files);
                addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            View textSettingsCell = new TextSettingsCell(context);
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentDocumentsType == null) {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteInfo", NUM), false);
            } else {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteDocument", NUM), false);
            }
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new PassportActivity$$Lambda$24(this));
            this.sectionCell = new ShadowSectionCell(context);
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        }
        updateUploadText(0);
        updateUploadText(4);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createAddressInterface$32$PassportActivity(View v) {
        this.uploadingFileType = 0;
        openAttachMenu();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createAddressInterface$33$PassportActivity(View v) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createAddressInterface$35$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PassportActivity$$Lambda$62(this));
            presentFragment(fragment);
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$34$PassportActivity(String name, String shortName) {
        this.inputFields[5].setText(name);
        this.currentCitizeship = shortName;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createAddressInterface$36$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int num = ((Integer) textView.getTag()).intValue() + 1;
        if (num >= this.inputFields.length) {
            return true;
        }
        if (this.inputFields[num].isFocusable()) {
            this.inputFields[num].requestFocus();
            return true;
        }
        this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
        textView.clearFocus();
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createAddressInterface$37$PassportActivity(View v) {
        createDocumentDeleteAlert();
    }

    private void createDocumentDeleteAlert() {
        boolean[] checks = new boolean[]{true};
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PassportActivity$$Lambda$25(this, checks));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        if (this.documentOnly && this.currentDocumentsType == null && (this.currentType.type instanceof TL_secureValueTypeAddress)) {
            builder.setMessage(LocaleController.getString("PassportDeleteAddressAlert", NUM));
        } else if (this.documentOnly && this.currentDocumentsType == null && (this.currentType.type instanceof TL_secureValueTypePersonalDetails)) {
            builder.setMessage(LocaleController.getString("PassportDeletePersonalAlert", NUM));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteDocumentAlert", NUM));
        }
        if (!(this.documentOnly || this.currentDocumentsType == null)) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentType.type instanceof TL_secureValueTypeAddress) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentAddress", NUM), "", true, false);
            } else if (this.currentType.type instanceof TL_secureValueTypePersonalDetails) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentPersonal", NUM), "", true, false);
            }
            cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48, 51));
            cell.setOnClickListener(new PassportActivity$$Lambda$26(checks));
            builder.setView(frameLayout);
        }
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createDocumentDeleteAlert$38$PassportActivity(boolean[] checks, DialogInterface dialog, int which) {
        if (!this.documentOnly) {
            this.currentValues.clear();
        }
        this.currentDocumentValues.clear();
        this.delegate.deleteValue(this.currentType, this.currentDocumentsType, this.availableDocumentTypes, checks[0], null, null);
        finishFragment();
    }

    static final /* synthetic */ void lambda$createDocumentDeleteAlert$39$PassportActivity(boolean[] checks, View v) {
        if (v.isEnabled()) {
            boolean z;
            CheckBoxCell cell1 = (CheckBoxCell) v;
            if (checks[0]) {
                z = false;
            } else {
                z = true;
            }
            checks[0] = z;
            cell1.setChecked(checks[0], true);
        }
    }

    private void onFieldError(View field) {
        if (field != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
            scrollToField(field);
        }
    }

    private void scrollToField(View field) {
        while (field != null && this.linearLayout2.indexOfChild(field) < 0) {
            field = (View) field.getParent();
        }
        if (field != null) {
            this.scrollView.smoothScrollTo(0, field.getTop() - ((this.scrollView.getMeasuredHeight() - field.getMeasuredHeight()) / 2));
        }
    }

    private String getDocumentHash(SecureDocument document) {
        if (document != null) {
            if (document.secureFile != null && document.secureFile.file_hash != null) {
                return Base64.encodeToString(document.secureFile.file_hash, 2);
            }
            if (document.fileHash != null) {
                return Base64.encodeToString(document.fileHash, 2);
            }
        }
        return "";
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0028  */
    private void checkFieldForError(org.telegram.ui.Components.EditTextBoldCursor r5, java.lang.String r6, android.text.Editable r7, boolean r8) {
        /*
        r4 = this;
        r3 = 0;
        r2 = r4.errorsValues;
        if (r2 == 0) goto L_0x0057;
    L_0x0005:
        r2 = r4.errorsValues;
        r1 = r2.get(r6);
        r1 = (java.lang.String) r1;
        if (r1 == 0) goto L_0x0057;
    L_0x000f:
        r2 = android.text.TextUtils.equals(r1, r7);
        if (r2 == 0) goto L_0x0053;
    L_0x0015:
        r2 = r4.fieldsErrors;
        if (r2 == 0) goto L_0x0041;
    L_0x0019:
        r2 = r4.fieldsErrors;
        r1 = r2.get(r6);
        r1 = (java.lang.String) r1;
        if (r1 == 0) goto L_0x0041;
    L_0x0023:
        r5.setErrorText(r1);
    L_0x0026:
        if (r8 == 0) goto L_0x005b;
    L_0x0028:
        r0 = "error_document_all";
    L_0x002b:
        r2 = r4.errorsValues;
        if (r2 == 0) goto L_0x0040;
    L_0x002f:
        r2 = r4.errorsValues;
        r2 = r2.containsKey(r0);
        if (r2 == 0) goto L_0x0040;
    L_0x0037:
        r2 = r4.errorsValues;
        r2.remove(r0);
        r2 = 0;
        r4.checkTopErrorCell(r2);
    L_0x0040:
        return;
    L_0x0041:
        r2 = r4.documentsErrors;
        if (r2 == 0) goto L_0x0026;
    L_0x0045:
        r2 = r4.documentsErrors;
        r1 = r2.get(r6);
        r1 = (java.lang.String) r1;
        if (r1 == 0) goto L_0x0026;
    L_0x004f:
        r5.setErrorText(r1);
        goto L_0x0026;
    L_0x0053:
        r5.setErrorText(r3);
        goto L_0x0026;
    L_0x0057:
        r5.setErrorText(r3);
        goto L_0x0026;
    L_0x005b:
        r0 = "error_all";
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.checkFieldForError(org.telegram.ui.Components.EditTextBoldCursor, java.lang.String, android.text.Editable, boolean):void");
    }

    private boolean checkFieldsForError() {
        int a;
        String key;
        if (this.currentDocumentsType != null) {
            if (this.errorsValues.containsKey("error_all") || this.errorsValues.containsKey("error_document_all")) {
                onFieldError(this.topErrorCell);
                return true;
            }
            int size;
            SecureDocument document;
            if (this.uploadDocumentCell != null) {
                if (this.documents.isEmpty()) {
                    onFieldError(this.uploadDocumentCell);
                    return true;
                }
                a = 0;
                size = this.documents.size();
                while (a < size) {
                    document = (SecureDocument) this.documents.get(a);
                    key = "files" + getDocumentHash(document);
                    if (key == null || !this.errorsValues.containsKey(key)) {
                        a++;
                    } else {
                        onFieldError((View) this.documentsCells.get(document));
                        return true;
                    }
                }
            }
            if (this.errorsValues.containsKey("files_all") || this.errorsValues.containsKey("translation_all")) {
                onFieldError(this.bottomCell);
                return true;
            }
            if (this.uploadFrontCell != null) {
                if (this.frontDocument == null) {
                    onFieldError(this.uploadFrontCell);
                    return true;
                }
                if (this.errorsValues.containsKey("front" + getDocumentHash(this.frontDocument))) {
                    onFieldError((View) this.documentsCells.get(this.frontDocument));
                    return true;
                }
            }
            if (((this.currentDocumentsType.type instanceof TL_secureValueTypeIdentityCard) || (this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense)) && this.uploadReverseCell != null) {
                if (this.reverseDocument == null) {
                    onFieldError(this.uploadReverseCell);
                    return true;
                }
                if (this.errorsValues.containsKey("reverse" + getDocumentHash(this.reverseDocument))) {
                    onFieldError((View) this.documentsCells.get(this.reverseDocument));
                    return true;
                }
            }
            if (!(this.uploadSelfieCell == null || this.currentBotId == 0)) {
                if (this.selfieDocument == null) {
                    onFieldError(this.uploadSelfieCell);
                    return true;
                }
                if (this.errorsValues.containsKey("selfie" + getDocumentHash(this.selfieDocument))) {
                    onFieldError((View) this.documentsCells.get(this.selfieDocument));
                    return true;
                }
            }
            if (!(this.uploadTranslationCell == null || this.currentBotId == 0)) {
                if (this.translationDocuments.isEmpty()) {
                    onFieldError(this.uploadTranslationCell);
                    return true;
                }
                a = 0;
                size = this.translationDocuments.size();
                while (a < size) {
                    document = (SecureDocument) this.translationDocuments.get(a);
                    key = "translation" + getDocumentHash(document);
                    if (key == null || !this.errorsValues.containsKey(key)) {
                        a++;
                    } else {
                        onFieldError((View) this.documentsCells.get(document));
                        return true;
                    }
                }
            }
        }
        int i = 0;
        while (i < 2) {
            EditTextBoldCursor[] fields = i == 0 ? this.inputFields : (this.nativeInfoCell == null || this.nativeInfoCell.getVisibility() != 0) ? null : this.inputExtraFields;
            if (fields != null) {
                a = 0;
                while (a < fields.length) {
                    boolean error = false;
                    if (fields[a].hasErrorText()) {
                        error = true;
                    }
                    if (!this.errorsValues.isEmpty()) {
                        if (this.currentType.type instanceof TL_secureValueTypePersonalDetails) {
                            if (i != 0) {
                                switch (a) {
                                    case 0:
                                        key = "first_name_native";
                                        break;
                                    case 1:
                                        key = "middle_name_native";
                                        break;
                                    case 2:
                                        key = "last_name_native";
                                        break;
                                    default:
                                        key = null;
                                        break;
                                }
                            }
                            switch (a) {
                                case 0:
                                    key = "first_name";
                                    break;
                                case 1:
                                    key = "middle_name";
                                    break;
                                case 2:
                                    key = "last_name";
                                    break;
                                case 3:
                                    key = "birth_date";
                                    break;
                                case 4:
                                    key = "gender";
                                    break;
                                case 5:
                                    key = "country_code";
                                    break;
                                case 6:
                                    key = "residence_country_code";
                                    break;
                                case 7:
                                    key = "document_no";
                                    break;
                                case 8:
                                    key = "expiry_date";
                                    break;
                                default:
                                    key = null;
                                    break;
                            }
                        } else if (this.currentType.type instanceof TL_secureValueTypeAddress) {
                            switch (a) {
                                case 0:
                                    key = "street_line1";
                                    break;
                                case 1:
                                    key = "street_line2";
                                    break;
                                case 2:
                                    key = "post_code";
                                    break;
                                case 3:
                                    key = "city";
                                    break;
                                case 4:
                                    key = "state";
                                    break;
                                case 5:
                                    key = "country_code";
                                    break;
                                default:
                                    key = null;
                                    break;
                            }
                        } else {
                            key = null;
                        }
                        if (key != null) {
                            String value = (String) this.errorsValues.get(key);
                            if (!TextUtils.isEmpty(value) && value.equals(fields[a].getText().toString())) {
                                error = true;
                            }
                        }
                    }
                    if (!this.documentOnly || this.currentDocumentsType == null || a >= 7) {
                        if (!error) {
                            int len = fields[a].length();
                            boolean allowZeroLength = false;
                            if (this.currentActivityType == 1) {
                                if (a == 8) {
                                    continue;
                                } else if ((i == 0 && (a == 0 || a == 2 || a == 1)) || (i == 1 && (a == 0 || a == 1 || a == 2))) {
                                    if (len > 255) {
                                        error = true;
                                    }
                                    if ((i == 0 && a == 1) || (i == 1 && a == 1)) {
                                        allowZeroLength = true;
                                    }
                                } else if (a == 7 && len > 24) {
                                    error = true;
                                }
                            } else if (this.currentActivityType == 2) {
                                if (a == 1) {
                                    continue;
                                } else if (a == 3) {
                                    if (len < 2) {
                                        error = true;
                                    }
                                } else if (a == 4) {
                                    if (!"US".equals(this.currentCitizeship)) {
                                        continue;
                                    } else if (len < 2) {
                                        error = true;
                                    }
                                } else if (a == 2 && (len < 2 || len > 10)) {
                                    error = true;
                                }
                            }
                            if (!(error || allowZeroLength || len != 0)) {
                                error = true;
                            }
                        }
                        if (error) {
                            onFieldError(fields[a]);
                            return true;
                        }
                    }
                    a++;
                }
                continue;
            }
            i++;
        }
        return false;
    }

    private void createIdentityInterface(Context context) {
        EditTextBoldCursor editTextBoldCursor;
        final EditTextBoldCursor editTextBoldCursor2;
        ViewGroup container;
        String key;
        HashMap<String, String> values;
        final EditTextBoldCursor editTextBoldCursor3;
        final String str;
        this.languageMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                this.languageMap.put(args[1], args[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.topErrorCell = new TextInfoPrivacyCell(context);
        this.topErrorCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        if (this.currentDocumentsType != null) {
            this.headerCell = new HeaderCell(context);
            if (this.documentOnly) {
                this.headerCell.setText(LocaleController.getString("PassportDocuments", NUM));
            } else {
                this.headerCell.setText(LocaleController.getString("PassportRequiredDocuments", NUM));
            }
            this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            this.frontLayout = new LinearLayout(context);
            this.frontLayout.setOrientation(1);
            this.linearLayout2.addView(this.frontLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell = new TextDetailSettingsCell(context);
            this.uploadFrontCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadFrontCell, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell.setOnClickListener(new PassportActivity$$Lambda$27(this));
            this.reverseLayout = new LinearLayout(context);
            this.reverseLayout.setOrientation(1);
            this.linearLayout2.addView(this.reverseLayout, LayoutHelper.createLinear(-1, -2));
            boolean divider = this.currentDocumentsType.selfie_required;
            this.uploadReverseCell = new TextDetailSettingsCell(context);
            this.uploadReverseCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.uploadReverseCell.setTextAndValue(LocaleController.getString("PassportReverseSide", NUM), LocaleController.getString("PassportReverseSideInfo", NUM), divider);
            this.linearLayout2.addView(this.uploadReverseCell, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell.setOnClickListener(new PassportActivity$$Lambda$28(this));
            if (this.currentDocumentsType.selfie_required) {
                this.selfieLayout = new LinearLayout(context);
                this.selfieLayout.setOrientation(1);
                this.linearLayout2.addView(this.selfieLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell = new TextDetailSettingsCell(context);
                this.uploadSelfieCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.uploadSelfieCell.setTextAndValue(LocaleController.getString("PassportSelfie", NUM), LocaleController.getString("PassportSelfieInfo", NUM), this.currentType.translation_required);
                this.linearLayout2.addView(this.uploadSelfieCell, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell.setOnClickListener(new PassportActivity$$Lambda$29(this));
            }
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.bottomCell.setText(LocaleController.getString("PassportPersonalUploadInfo", NUM));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                this.headerCell = new HeaderCell(context);
                this.headerCell.setText(LocaleController.getString("PassportTranslation", NUM));
                this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                this.translationLayout = new LinearLayout(context);
                this.translationLayout.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell = new TextSettingsCell(context);
                this.uploadTranslationCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new PassportActivity$$Lambda$30(this));
                this.bottomCellTranslation = new TextInfoPrivacyCell(context);
                this.bottomCellTranslation.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypePassport) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddPassportInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeInternalPassport) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddInternalPassportInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeIdentityCard) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddIdentityCardInfo", NUM);
                } else if (this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddDriverLicenceInfo", NUM);
                } else {
                    this.noAllTranslationErrorText = "";
                }
                CharSequence text = this.noAllTranslationErrorText;
                if (this.documentsErrors != null) {
                    String errorText = (String) this.documentsErrors.get("translation_all");
                    if (errorText != null) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorText);
                        spannableStringBuilder.append("\n\n");
                        spannableStringBuilder.append(this.noAllTranslationErrorText);
                        text = spannableStringBuilder;
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, errorText.length(), 33);
                        this.errorsValues.put("translation_all", "");
                    }
                }
                this.bottomCellTranslation.setText(text);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else if (VERSION.SDK_INT >= 18) {
            this.scanDocumentCell = new TextSettingsCell(context);
            this.scanDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.scanDocumentCell.setText(LocaleController.getString("PassportScanPassport", NUM), false);
            this.linearLayout2.addView(this.scanDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.scanDocumentCell.setOnClickListener(new PassportActivity$$Lambda$31(this));
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.bottomCell.setText(LocaleController.getString("PassportScanPassportInfo", NUM));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.headerCell = new HeaderCell(context);
        if (this.documentOnly) {
            this.headerCell.setText(LocaleController.getString("PassportDocument", NUM));
        } else {
            this.headerCell.setText(LocaleController.getString("PassportPersonal", NUM));
        }
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        int count = this.currentDocumentsType != null ? 9 : 7;
        this.inputFields = new EditTextBoldCursor[count];
        int a = 0;
        while (a < count) {
            editTextBoldCursor = new EditTextBoldCursor(context);
            this.inputFields[a] = editTextBoldCursor;
            editTextBoldCursor2 = editTextBoldCursor;
            container = new FrameLayout(context) {
                private StaticLayout errorLayout;
                private float offsetX;

                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                    this.errorLayout = editTextBoldCursor2.getErrorLayout(width);
                    if (this.errorLayout != null) {
                        int lineCount = this.errorLayout.getLineCount();
                        if (lineCount > 1) {
                            heightMeasureSpec = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                        }
                        if (LocaleController.isRTL) {
                            float maxW = 0.0f;
                            for (int a = 0; a < lineCount; a++) {
                                if (this.errorLayout.getLineLeft(a) != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                }
                                maxW = Math.max(maxW, this.errorLayout.getLineWidth(a));
                                if (a == lineCount - 1) {
                                    this.offsetX = ((float) width) - maxW;
                                }
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor2.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
            container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            if (a == count - 1) {
                this.extraBackgroundView = new View(context);
                this.extraBackgroundView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.documentOnly && this.currentDocumentsType != null && a < 7) {
                container.setVisibility(8);
                if (this.extraBackgroundView != null) {
                    this.extraBackgroundView.setVisibility(8);
                }
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setSupportRtlHint(true);
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (a == 5 || a == 6) {
                this.inputFields[a].setOnTouchListener(new PassportActivity$$Lambda$32(this));
                this.inputFields[a].setInputType(0);
            } else if (a == 3 || a == 8) {
                this.inputFields[a].setOnTouchListener(new PassportActivity$$Lambda$33(this, context));
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else if (a == 4) {
                this.inputFields[a].setOnTouchListener(new PassportActivity$$Lambda$34(this));
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(16385);
                this.inputFields[a].setImeOptions(NUM);
            }
            switch (a) {
                case 0:
                    if (this.currentType.native_names) {
                        this.inputFields[a].setHintText(LocaleController.getString("PassportNameLatin", NUM));
                    } else {
                        this.inputFields[a].setHintText(LocaleController.getString("PassportName", NUM));
                    }
                    key = "first_name";
                    values = this.currentValues;
                    break;
                case 1:
                    if (this.currentType.native_names) {
                        this.inputFields[a].setHintText(LocaleController.getString("PassportMidnameLatin", NUM));
                    } else {
                        this.inputFields[a].setHintText(LocaleController.getString("PassportMidname", NUM));
                    }
                    key = "middle_name";
                    values = this.currentValues;
                    break;
                case 2:
                    if (this.currentType.native_names) {
                        this.inputFields[a].setHintText(LocaleController.getString("PassportSurnameLatin", NUM));
                    } else {
                        this.inputFields[a].setHintText(LocaleController.getString("PassportSurname", NUM));
                    }
                    key = "last_name";
                    values = this.currentValues;
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportBirthdate", NUM));
                    key = "birth_date";
                    values = this.currentValues;
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportGender", NUM));
                    key = "gender";
                    values = this.currentValues;
                    break;
                case 5:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCitizenship", NUM));
                    key = "country_code";
                    values = this.currentValues;
                    break;
                case 6:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportResidence", NUM));
                    key = "residence_country_code";
                    values = this.currentValues;
                    break;
                case 7:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportDocumentNumber", NUM));
                    key = "document_no";
                    values = this.currentDocumentValues;
                    break;
                case 8:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportExpired", NUM));
                    key = "expiry_date";
                    values = this.currentDocumentValues;
                    break;
                default:
                    break;
            }
            setFieldValues(values, this.inputFields[a], key);
            this.inputFields[a].setSelection(this.inputFields[a].length());
            if (a == 0 || a == 2 || a == 1) {
                editTextBoldCursor3 = editTextBoldCursor;
                str = key;
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    private boolean ignore;

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!this.ignore) {
                            int num = ((Integer) editTextBoldCursor3.getTag()).intValue();
                            boolean error = false;
                            for (int a = 0; a < s.length(); a++) {
                                char ch = s.charAt(a);
                                if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && !((ch >= 'A' && ch <= 'Z') || ch == ' ' || ch == '\'' || ch == ',' || ch == '.' || ch == '&' || ch == '-' || ch == '/'))) {
                                    error = true;
                                    break;
                                }
                            }
                            if (!error || PassportActivity.this.allowNonLatinName) {
                                PassportActivity.this.nonLatinNames[num] = error;
                                PassportActivity.this.checkFieldForError(editTextBoldCursor3, str, s, false);
                                return;
                            }
                            editTextBoldCursor3.setErrorText(LocaleController.getString("PassportUseLatinOnly", NUM));
                        }
                    }
                });
            } else {
                editTextBoldCursor3 = editTextBoldCursor;
                str = key;
                final HashMap<String, String> hashMap = values;
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        PassportActivity.this.checkFieldForError(editTextBoldCursor3, str, s, hashMap == PassportActivity.this.currentDocumentValues);
                        int field12 = ((Integer) editTextBoldCursor3.getTag()).intValue();
                        EditTextBoldCursor editText = PassportActivity.this.inputFields[field12];
                        if (field12 == 6) {
                            PassportActivity.this.checkNativeFields(true);
                        }
                    }
                });
            }
            this.inputFields[a].setPadding(0, 0, 0, 0);
            this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$35(this));
            a++;
        }
        this.sectionCell2 = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell2, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context);
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputExtraFields = new EditTextBoldCursor[3];
        a = 0;
        while (a < 3) {
            editTextBoldCursor = new EditTextBoldCursor(context);
            this.inputExtraFields[a] = editTextBoldCursor;
            editTextBoldCursor2 = editTextBoldCursor;
            container = new FrameLayout(context) {
                private StaticLayout errorLayout;
                private float offsetX;

                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                    this.errorLayout = editTextBoldCursor2.getErrorLayout(width);
                    if (this.errorLayout != null) {
                        int lineCount = this.errorLayout.getLineCount();
                        if (lineCount > 1) {
                            heightMeasureSpec = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                        }
                        if (LocaleController.isRTL) {
                            float maxW = 0.0f;
                            for (int a = 0; a < lineCount; a++) {
                                if (this.errorLayout.getLineLeft(a) != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                }
                                maxW = Math.max(maxW, this.errorLayout.getLineWidth(a));
                                if (a == lineCount - 1) {
                                    this.offsetX = ((float) width) - maxW;
                                }
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor2.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
            container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            if (a == 2) {
                this.extraBackgroundView2 = new View(context);
                this.extraBackgroundView2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.extraBackgroundView2, LayoutHelper.createLinear(-1, 6));
            }
            this.inputExtraFields[a].setTag(Integer.valueOf(a));
            this.inputExtraFields[a].setSupportRtlHint(true);
            this.inputExtraFields[a].setTextSize(1, 16.0f);
            this.inputExtraFields[a].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputExtraFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputExtraFields[a].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputExtraFields[a].setTransformHintToHeader(true);
            this.inputExtraFields[a].setBackgroundDrawable(null);
            this.inputExtraFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputExtraFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputExtraFields[a].setCursorWidth(1.5f);
            this.inputExtraFields[a].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            this.inputExtraFields[a].setInputType(16385);
            this.inputExtraFields[a].setImeOptions(NUM);
            switch (a) {
                case 0:
                    key = "first_name_native";
                    values = this.currentValues;
                    break;
                case 1:
                    key = "middle_name_native";
                    values = this.currentValues;
                    break;
                case 2:
                    key = "last_name_native";
                    values = this.currentValues;
                    break;
                default:
                    break;
            }
            setFieldValues(values, this.inputExtraFields[a], key);
            this.inputExtraFields[a].setSelection(this.inputExtraFields[a].length());
            if (a == 0 || a == 2 || a == 1) {
                editTextBoldCursor3 = editTextBoldCursor;
                str = key;
                this.inputExtraFields[a].addTextChangedListener(new TextWatcher() {
                    private boolean ignore;

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!this.ignore) {
                            PassportActivity.this.checkFieldForError(editTextBoldCursor3, str, s, false);
                        }
                    }
                });
            }
            this.inputExtraFields[a].setPadding(0, 0, 0, 0);
            this.inputExtraFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            container.addView(this.inputExtraFields[a], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputExtraFields[a].setOnEditorActionListener(new PassportActivity$$Lambda$36(this));
            a++;
        }
        this.nativeInfoCell = new TextInfoPrivacyCell(context);
        this.linearLayout2.addView(this.nativeInfoCell, LayoutHelper.createLinear(-1, -2));
        if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
            this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        } else {
            if (this.currentDocumentsTypeValue != null) {
                addDocumentViews(this.currentDocumentsTypeValue.files);
                if (this.currentDocumentsTypeValue.front_side instanceof TL_secureFile) {
                    addDocumentViewInternal((TL_secureFile) this.currentDocumentsTypeValue.front_side, 2);
                }
                if (this.currentDocumentsTypeValue.reverse_side instanceof TL_secureFile) {
                    addDocumentViewInternal((TL_secureFile) this.currentDocumentsTypeValue.reverse_side, 3);
                }
                if (this.currentDocumentsTypeValue.selfie instanceof TL_secureFile) {
                    addDocumentViewInternal((TL_secureFile) this.currentDocumentsTypeValue.selfie, 1);
                }
                addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
            }
            View textSettingsCell = new TextSettingsCell(context);
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentDocumentsType == null) {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteInfo", NUM), false);
            } else {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteDocument", NUM), false);
            }
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new PassportActivity$$Lambda$37(this));
            this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.sectionCell = new ShadowSectionCell(context);
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        }
        updateInterfaceStringsForDocumentType();
        checkNativeFields(false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createIdentityInterface$40$PassportActivity(View v) {
        this.uploadingFileType = 2;
        openAttachMenu();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createIdentityInterface$41$PassportActivity(View v) {
        this.uploadingFileType = 3;
        openAttachMenu();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createIdentityInterface$42$PassportActivity(View v) {
        this.uploadingFileType = 1;
        openAttachMenu();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createIdentityInterface$43$PassportActivity(View v) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createIdentityInterface$45$PassportActivity(View v) {
        if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
            MrzCameraActivity fragment = new MrzCameraActivity();
            fragment.setDelegate(new PassportActivity$$Lambda$61(this));
            presentFragment(fragment);
            return;
        }
        getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 22);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$44$PassportActivity(Result result) {
        String country;
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        if (result.gender != 0) {
            switch (result.gender) {
                case 1:
                    this.currentGender = "male";
                    this.inputFields[4].setText(LocaleController.getString("PassportMale", NUM));
                    break;
                case 2:
                    this.currentGender = "female";
                    this.inputFields[4].setText(LocaleController.getString("PassportFemale", NUM));
                    break;
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            this.currentCitizeship = result.nationality;
            country = (String) this.languageMap.get(this.currentCitizeship);
            if (country != null) {
                this.inputFields[5].setText(country);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            this.currentResidence = result.issuingCountry;
            country = (String) this.languageMap.get(this.currentResidence);
            if (country != null) {
                this.inputFields[6].setText(country);
            }
        }
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createIdentityInterface$47$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new PassportActivity$$Lambda$60(this, v));
            presentFragment(fragment);
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$46$PassportActivity(View v, String name, String shortName) {
        int field12 = ((Integer) v.getTag()).intValue();
        EditTextBoldCursor editText = this.inputFields[field12];
        if (field12 == 5) {
            this.currentCitizeship = shortName;
        } else {
            this.currentResidence = shortName;
        }
        editText.setText(name);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createIdentityInterface$50$PassportActivity(Context context, View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(1);
            int monthOfYear = calendar.get(2);
            int dayOfMonth = calendar.get(5);
            try {
                String title;
                int minYear;
                int maxYear;
                int currentYearDiff;
                EditTextBoldCursor field1 = (EditTextBoldCursor) v;
                int num = ((Integer) field1.getTag()).intValue();
                if (num == 8) {
                    title = LocaleController.getString("PassportSelectExpiredDate", NUM);
                    minYear = 0;
                    maxYear = 20;
                    currentYearDiff = 0;
                } else {
                    title = LocaleController.getString("PassportSelectBithdayDate", NUM);
                    minYear = -120;
                    maxYear = 0;
                    currentYearDiff = -18;
                }
                int selectedDay = -1;
                int selectedMonth = -1;
                int selectedYear = -1;
                String[] args = field1.getText().toString().split("\\.");
                if (args.length == 3) {
                    selectedDay = Utilities.parseInt(args[0]).intValue();
                    selectedMonth = Utilities.parseInt(args[1]).intValue();
                    selectedYear = Utilities.parseInt(args[2]).intValue();
                }
                Builder builder = AlertsCreator.createDatePickerDialog(context, minYear, maxYear, currentYearDiff, selectedDay, selectedMonth, selectedYear, title, num == 8, new PassportActivity$$Lambda$58(this, num, field1));
                if (num == 8) {
                    builder.setNegativeButton(LocaleController.getString("PassportSelectNotExpire", NUM), new PassportActivity$$Lambda$59(this, field1));
                }
                showDialog(builder.create());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$48$PassportActivity(int num, EditTextBoldCursor field1, int year1, int month, int dayOfMonth1) {
        if (num == 8) {
            this.currentExpireDate[0] = year1;
            this.currentExpireDate[1] = month + 1;
            this.currentExpireDate[2] = dayOfMonth1;
        }
        field1.setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(dayOfMonth1), Integer.valueOf(month + 1), Integer.valueOf(year1)}));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$49$PassportActivity(EditTextBoldCursor field1, DialogInterface dialog, int which) {
        int[] iArr = this.currentExpireDate;
        int[] iArr2 = this.currentExpireDate;
        this.currentExpireDate[2] = 0;
        iArr2[1] = 0;
        iArr[0] = 0;
        field1.setText(LocaleController.getString("PassportNoExpireDate", NUM));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createIdentityInterface$52$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PassportSelectGender", NUM));
            builder.setItems(new CharSequence[]{LocaleController.getString("PassportMale", NUM), LocaleController.getString("PassportFemale", NUM)}, new PassportActivity$$Lambda$57(this));
            builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$51$PassportActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.currentGender = "male";
            this.inputFields[4].setText(LocaleController.getString("PassportMale", NUM));
        } else if (i == 1) {
            this.currentGender = "female";
            this.inputFields[4].setText(LocaleController.getString("PassportFemale", NUM));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createIdentityInterface$53$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int num = ((Integer) textView.getTag()).intValue() + 1;
        if (num >= this.inputFields.length) {
            return true;
        }
        if (this.inputFields[num].isFocusable()) {
            this.inputFields[num].requestFocus();
            return true;
        }
        this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
        textView.clearFocus();
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createIdentityInterface$54$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int num = ((Integer) textView.getTag()).intValue() + 1;
        if (num >= this.inputExtraFields.length) {
            return true;
        }
        if (this.inputExtraFields[num].isFocusable()) {
            this.inputExtraFields[num].requestFocus();
            return true;
        }
        this.inputExtraFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
        textView.clearFocus();
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createIdentityInterface$55$PassportActivity(View v) {
        createDocumentDeleteAlert();
    }

    private void updateInterfaceStringsForDocumentType() {
        if (this.currentDocumentsType != null) {
            this.actionBar.setTitle(getTextForType(this.currentDocumentsType.type));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportPersonal", NUM));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
        updateUploadText(4);
    }

    private void updateUploadText(int type) {
        boolean divider = true;
        int i = 8;
        TextDetailSettingsCell textDetailSettingsCell;
        if (type == 0) {
            if (this.uploadDocumentCell != null) {
                if (this.documents.size() >= 1) {
                    this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", NUM), false);
                } else {
                    this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadDocument", NUM), false);
                }
            }
        } else if (type == 1) {
            if (this.uploadSelfieCell != null) {
                textDetailSettingsCell = this.uploadSelfieCell;
                if (this.selfieDocument == null) {
                    i = 0;
                }
                textDetailSettingsCell.setVisibility(i);
            }
        } else if (type == 4) {
            if (this.uploadTranslationCell == null) {
                return;
            }
            if (this.translationDocuments.size() >= 1) {
                this.uploadTranslationCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", NUM), false);
            } else {
                this.uploadTranslationCell.setText(LocaleController.getString("PassportUploadDocument", NUM), false);
            }
        } else if (type == 2) {
            if (this.uploadFrontCell != null) {
                if (this.currentDocumentsType == null || !(this.currentDocumentsType.selfie_required || (this.currentDocumentsType.type instanceof TL_secureValueTypeIdentityCard) || (this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense))) {
                    divider = false;
                }
                if ((this.currentDocumentsType.type instanceof TL_secureValueTypePassport) || (this.currentDocumentsType.type instanceof TL_secureValueTypeInternalPassport)) {
                    this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportMainPage", NUM), LocaleController.getString("PassportMainPageInfo", NUM), divider);
                } else {
                    this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportFrontSide", NUM), LocaleController.getString("PassportFrontSideInfo", NUM), divider);
                }
                textDetailSettingsCell = this.uploadFrontCell;
                if (this.frontDocument == null) {
                    i = 0;
                }
                textDetailSettingsCell.setVisibility(i);
            }
        } else if (type == 3 && this.uploadReverseCell != null) {
            if ((this.currentDocumentsType.type instanceof TL_secureValueTypeIdentityCard) || (this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense)) {
                this.reverseLayout.setVisibility(0);
                textDetailSettingsCell = this.uploadReverseCell;
                if (this.reverseDocument == null) {
                    i = 0;
                }
                textDetailSettingsCell.setVisibility(i);
                return;
            }
            this.reverseLayout.setVisibility(8);
            this.uploadReverseCell.setVisibility(8);
        }
    }

    private void checkTopErrorCell(boolean init) {
        if (this.topErrorCell != null) {
            String errorText;
            SpannableStringBuilder stringBuilder = null;
            if (this.fieldsErrors != null && (init || this.errorsValues.containsKey("error_all"))) {
                errorText = (String) this.fieldsErrors.get("error_all");
                if (errorText != null) {
                    stringBuilder = new SpannableStringBuilder(errorText);
                    if (init) {
                        this.errorsValues.put("error_all", "");
                    }
                }
            }
            if (this.documentsErrors != null && (init || this.errorsValues.containsKey("error_document_all"))) {
                errorText = (String) this.documentsErrors.get("error_all");
                if (errorText != null) {
                    if (stringBuilder == null) {
                        stringBuilder = new SpannableStringBuilder(errorText);
                    } else {
                        stringBuilder.append("\n\n").append(errorText);
                    }
                    if (init) {
                        this.errorsValues.put("error_document_all", "");
                    }
                }
            }
            if (stringBuilder != null) {
                stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, stringBuilder.length(), 33);
                this.topErrorCell.setText(stringBuilder);
                this.topErrorCell.setVisibility(0);
            } else if (this.topErrorCell.getVisibility() != 8) {
                this.topErrorCell.setVisibility(8);
            }
        }
    }

    private void addDocumentViewInternal(TL_secureFile f, int uploadingType) {
        addDocumentView(new SecureDocument(getSecureDocumentKey(f.secret, f.file_hash), f, null, null, null), uploadingType);
    }

    private void addDocumentViews(ArrayList<SecureFile> files) {
        this.documents.clear();
        int size = files.size();
        for (int a = 0; a < size; a++) {
            SecureFile secureFile = (SecureFile) files.get(a);
            if (secureFile instanceof TL_secureFile) {
                addDocumentViewInternal((TL_secureFile) secureFile, 0);
            }
        }
    }

    private void addTranslationDocumentViews(ArrayList<SecureFile> files) {
        this.translationDocuments.clear();
        int size = files.size();
        for (int a = 0; a < size; a++) {
            SecureFile secureFile = (SecureFile) files.get(a);
            if (secureFile instanceof TL_secureFile) {
                addDocumentViewInternal((TL_secureFile) secureFile, 4);
            }
        }
    }

    private void setFieldValues(HashMap<String, String> values, EditTextBoldCursor editText, String key) {
        String value = (String) values.get(key);
        if (value != null) {
            int i = -1;
            switch (key.hashCode()) {
                case -2006252145:
                    if (key.equals("residence_country_code")) {
                        i = 1;
                        break;
                    }
                    break;
                case -1249512767:
                    if (key.equals("gender")) {
                        i = 2;
                        break;
                    }
                    break;
                case 475919162:
                    if (key.equals("expiry_date")) {
                        i = 3;
                        break;
                    }
                    break;
                case 1481071862:
                    if (key.equals("country_code")) {
                        i = 0;
                        break;
                    }
                    break;
            }
            String country;
            switch (i) {
                case 0:
                    this.currentCitizeship = value;
                    country = (String) this.languageMap.get(this.currentCitizeship);
                    if (country != null) {
                        editText.setText(country);
                        break;
                    }
                    break;
                case 1:
                    this.currentResidence = value;
                    country = (String) this.languageMap.get(this.currentResidence);
                    if (country != null) {
                        editText.setText(country);
                        break;
                    }
                    break;
                case 2:
                    if (!"male".equals(value)) {
                        if ("female".equals(value)) {
                            this.currentGender = value;
                            editText.setText(LocaleController.getString("PassportFemale", NUM));
                            break;
                        }
                    }
                    this.currentGender = value;
                    editText.setText(LocaleController.getString("PassportMale", NUM));
                    break;
                    break;
                case 3:
                    boolean ok = false;
                    if (!TextUtils.isEmpty(value)) {
                        String[] args = value.split("\\.");
                        if (args.length == 3) {
                            this.currentExpireDate[0] = Utilities.parseInt(args[2]).intValue();
                            this.currentExpireDate[1] = Utilities.parseInt(args[1]).intValue();
                            this.currentExpireDate[2] = Utilities.parseInt(args[0]).intValue();
                            editText.setText(value);
                            ok = true;
                        }
                    }
                    if (!ok) {
                        int[] iArr = this.currentExpireDate;
                        int[] iArr2 = this.currentExpireDate;
                        this.currentExpireDate[2] = 0;
                        iArr2[1] = 0;
                        iArr[0] = 0;
                        editText.setText(LocaleController.getString("PassportNoExpireDate", NUM));
                        break;
                    }
                    break;
                default:
                    editText.setText(value);
                    break;
            }
        }
        if (this.fieldsErrors != null) {
            value = (String) this.fieldsErrors.get(key);
            if (value != null) {
                editText.setErrorText(value);
                this.errorsValues.put(key, editText.getText().toString());
                return;
            }
        }
        if (this.documentsErrors != null) {
            value = (String) this.documentsErrors.get(key);
            if (value != null) {
                editText.setErrorText(value);
                this.errorsValues.put(key, editText.getText().toString());
            }
        }
    }

    private void addDocumentView(SecureDocument document, int type) {
        if (type == 1) {
            this.selfieDocument = document;
            if (this.selfieLayout == null) {
                return;
            }
        } else if (type == 4) {
            this.translationDocuments.add(document);
            if (this.translationLayout == null) {
                return;
            }
        } else if (type == 2) {
            this.frontDocument = document;
            if (this.frontLayout == null) {
                return;
            }
        } else if (type == 3) {
            this.reverseDocument = document;
            if (this.reverseLayout == null) {
                return;
            }
        } else {
            this.documents.add(document);
            if (this.documentsLayout == null) {
                return;
            }
        }
        if (getParentActivity() != null) {
            String text;
            String key;
            String value;
            SecureDocumentCell cell = new SecureDocumentCell(this, getParentActivity());
            cell.setTag(document);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.documentsCells.put(document, cell);
            String hash = getDocumentHash(document);
            if (type == 1) {
                text = LocaleController.getString("PassportSelfie", NUM);
                this.selfieLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "selfie" + hash;
            } else if (type == 4) {
                text = LocaleController.getString("AttachPhoto", NUM);
                this.translationLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "translation" + hash;
            } else if (type == 2) {
                if ((this.currentDocumentsType.type instanceof TL_secureValueTypePassport) || (this.currentDocumentsType.type instanceof TL_secureValueTypeInternalPassport)) {
                    text = LocaleController.getString("PassportMainPage", NUM);
                } else {
                    text = LocaleController.getString("PassportFrontSide", NUM);
                }
                this.frontLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "front" + hash;
            } else if (type == 3) {
                text = LocaleController.getString("PassportReverseSide", NUM);
                this.reverseLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "reverse" + hash;
            } else {
                text = LocaleController.getString("AttachPhoto", NUM);
                this.documentsLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "files" + hash;
            }
            if (!(key == null || this.documentsErrors == null)) {
                value = (String) this.documentsErrors.get(key);
                if (value != null) {
                    cell.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
                    this.errorsValues.put(key, "");
                    cell.setTextAndValueAndImage(text, value, document);
                    cell.setOnClickListener(new PassportActivity$$Lambda$38(this, type));
                    cell.setOnLongClickListener(new PassportActivity$$Lambda$39(this, type, document, cell, key));
                }
            }
            value = LocaleController.formatDateForBan((long) document.secureFile.date);
            cell.setTextAndValueAndImage(text, value, document);
            cell.setOnClickListener(new PassportActivity$$Lambda$38(this, type));
            cell.setOnLongClickListener(new PassportActivity$$Lambda$39(this, type, document, cell, key));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addDocumentView$56$PassportActivity(int type, View v) {
        this.uploadingFileType = type;
        if (type == 1) {
            this.currentPhotoViewerLayout = this.selfieLayout;
        } else if (type == 4) {
            this.currentPhotoViewerLayout = this.translationLayout;
        } else if (type == 2) {
            this.currentPhotoViewerLayout = this.frontLayout;
        } else if (type == 3) {
            this.currentPhotoViewerLayout = this.reverseLayout;
        } else {
            this.currentPhotoViewerLayout = this.documentsLayout;
        }
        SecureDocument document1 = (SecureDocument) v.getTag();
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        if (type == 0) {
            PhotoViewer.getInstance().openPhoto(this.documents, this.documents.indexOf(document1), this.provider);
        } else {
            PhotoViewer.getInstance().openPhoto(this.translationDocuments, this.translationDocuments.indexOf(document1), this.provider);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$addDocumentView$58$PassportActivity(int type, SecureDocument document, SecureDocumentCell cell, String key, View v) {
        Builder builder = new Builder(getParentActivity());
        if (type == 1) {
            builder.setMessage(LocaleController.getString("PassportDeleteSelfie", NUM));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteScan", NUM));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PassportActivity$$Lambda$56(this, document, type, cell, key));
        showDialog(builder.create());
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$57$PassportActivity(SecureDocument document, int type, SecureDocumentCell cell, String key, DialogInterface dialog, int which) {
        this.documentsCells.remove(document);
        if (type == 1) {
            this.selfieDocument = null;
            this.selfieLayout.removeView(cell);
        } else if (type == 4) {
            this.translationDocuments.remove(document);
            this.translationLayout.removeView(cell);
        } else if (type == 2) {
            this.frontDocument = null;
            this.frontLayout.removeView(cell);
        } else if (type == 3) {
            this.reverseDocument = null;
            this.reverseLayout.removeView(cell);
        } else {
            this.documents.remove(document);
            this.documentsLayout.removeView(cell);
        }
        if (key != null) {
            if (this.documentsErrors != null) {
                this.documentsErrors.remove(key);
            }
            if (this.errorsValues != null) {
                this.errorsValues.remove(key);
            }
        }
        updateUploadText(type);
        if (document.path != null && this.uploadingDocuments.remove(document.path) != null) {
            if (this.uploadingDocuments.isEmpty()) {
                this.doneItem.setEnabled(true);
                this.doneItem.setAlpha(1.0f);
            }
            FileLoader.getInstance(this.currentAccount).cancelUploadFile(document.path, false);
        }
    }

    private String getNameForType(SecureValueType type) {
        if (type instanceof TL_secureValueTypePersonalDetails) {
            return "personal_details";
        }
        if (type instanceof TL_secureValueTypePassport) {
            return "passport";
        }
        if (type instanceof TL_secureValueTypeInternalPassport) {
            return "internal_passport";
        }
        if (type instanceof TL_secureValueTypeDriverLicense) {
            return "driver_license";
        }
        if (type instanceof TL_secureValueTypeIdentityCard) {
            return "identity_card";
        }
        if (type instanceof TL_secureValueTypeUtilityBill) {
            return "utility_bill";
        }
        if (type instanceof TL_secureValueTypeAddress) {
            return "address";
        }
        if (type instanceof TL_secureValueTypeBankStatement) {
            return "bank_statement";
        }
        if (type instanceof TL_secureValueTypeRentalAgreement) {
            return "rental_agreement";
        }
        if (type instanceof TL_secureValueTypeTemporaryRegistration) {
            return "temporary_registration";
        }
        if (type instanceof TL_secureValueTypePassportRegistration) {
            return "passport_registration";
        }
        if (type instanceof TL_secureValueTypeEmail) {
            return "email";
        }
        if (type instanceof TL_secureValueTypePhone) {
            return "phone";
        }
        return "";
    }

    private TextDetailSecureCell getViewByType(TL_secureRequiredType requiredType) {
        TextDetailSecureCell view = (TextDetailSecureCell) this.typesViews.get(requiredType);
        if (view != null) {
            return view;
        }
        requiredType = (TL_secureRequiredType) this.documentsToTypesLink.get(requiredType);
        if (requiredType != null) {
            return (TextDetailSecureCell) this.typesViews.get(requiredType);
        }
        return view;
    }

    private String getTextForType(SecureValueType type) {
        if (type instanceof TL_secureValueTypePassport) {
            return LocaleController.getString("ActionBotDocumentPassport", NUM);
        }
        if (type instanceof TL_secureValueTypeDriverLicense) {
            return LocaleController.getString("ActionBotDocumentDriverLicence", NUM);
        }
        if (type instanceof TL_secureValueTypeIdentityCard) {
            return LocaleController.getString("ActionBotDocumentIdentityCard", NUM);
        }
        if (type instanceof TL_secureValueTypeUtilityBill) {
            return LocaleController.getString("ActionBotDocumentUtilityBill", NUM);
        }
        if (type instanceof TL_secureValueTypeBankStatement) {
            return LocaleController.getString("ActionBotDocumentBankStatement", NUM);
        }
        if (type instanceof TL_secureValueTypeRentalAgreement) {
            return LocaleController.getString("ActionBotDocumentRentalAgreement", NUM);
        }
        if (type instanceof TL_secureValueTypeInternalPassport) {
            return LocaleController.getString("ActionBotDocumentInternalPassport", NUM);
        }
        if (type instanceof TL_secureValueTypePassportRegistration) {
            return LocaleController.getString("ActionBotDocumentPassportRegistration", NUM);
        }
        if (type instanceof TL_secureValueTypeTemporaryRegistration) {
            return LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM);
        }
        if (type instanceof TL_secureValueTypePhone) {
            return LocaleController.getString("ActionBotDocumentPhone", NUM);
        }
        if (type instanceof TL_secureValueTypeEmail) {
            return LocaleController.getString("ActionBotDocumentEmail", NUM);
        }
        return "";
    }

    private void setTypeValue(TL_secureRequiredType requiredType, String text, String json, TL_secureRequiredType documentRequiredType, String documentsJson, boolean documentOnly, int availableDocumentTypesCount) {
        TextDetailSecureCell view = (TextDetailSecureCell) this.typesViews.get(requiredType);
        if (view == null) {
            if (this.currentActivityType == 8) {
                ArrayList<TL_secureRequiredType> documentTypes = new ArrayList();
                if (documentRequiredType != null) {
                    documentTypes.add(documentRequiredType);
                }
                View prev = this.linearLayout2.getChildAt(this.linearLayout2.getChildCount() - 6);
                if (prev instanceof TextDetailSecureCell) {
                    ((TextDetailSecureCell) prev).setNeedDivider(true);
                }
                view = addField(getParentActivity(), requiredType, documentTypes, true, true);
                updateManageVisibility();
            } else {
                return;
            }
        }
        HashMap<String, String> values = (HashMap) this.typesValues.get(requiredType);
        HashMap<String, String> documentValues = documentRequiredType != null ? (HashMap) this.typesValues.get(documentRequiredType) : null;
        TL_secureValue requiredTypeValue = getValueByType(requiredType, true);
        TL_secureValue documentRequiredTypeValue = getValueByType(documentRequiredType, true);
        if (json == null || this.languageMap != null) {
            this.languageMap = null;
        } else {
            this.languageMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt")));
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    this.languageMap.put(args[1], args[2]);
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        String value = null;
        if (text == null) {
            StringBuilder stringBuilder = null;
            if (!(this.currentActivityType == 8 || documentRequiredType == null || (TextUtils.isEmpty(documentsJson) && documentRequiredTypeValue == null))) {
                if (null == null) {
                    stringBuilder = new StringBuilder();
                }
                if (availableDocumentTypesCount > 1) {
                    stringBuilder.append(getTextForType(documentRequiredType.type));
                } else if (TextUtils.isEmpty(documentsJson)) {
                    stringBuilder.append(LocaleController.getString("PassportDocuments", NUM));
                }
            }
            if (!(json == null && documentsJson == null)) {
                if (values != null) {
                    values.clear();
                    String[] keys = null;
                    String[] documentKeys = null;
                    if (requiredType.type instanceof TL_secureValueTypePersonalDetails) {
                        if ((this.currentActivityType == 0 && !documentOnly) || (this.currentActivityType == 8 && documentRequiredType == null)) {
                            keys = new String[]{"first_name", "middle_name", "last_name", "first_name_native", "middle_name_native", "last_name_native", "birth_date", "gender", "country_code", "residence_country_code"};
                        }
                        if (this.currentActivityType == 0 || (this.currentActivityType == 8 && documentRequiredType != null)) {
                            documentKeys = new String[]{"document_no", "expiry_date"};
                        }
                    } else if ((requiredType.type instanceof TL_secureValueTypeAddress) && ((this.currentActivityType == 0 && !documentOnly) || (this.currentActivityType == 8 && documentRequiredType == null))) {
                        keys = new String[]{"street_line1", "street_line2", "post_code", "city", "state", "country_code"};
                    }
                    if (!(keys == null && documentKeys == null)) {
                        JSONObject jsonObject = null;
                        String[] currentKeys = null;
                        int b = 0;
                        while (true) {
                            JSONObject jsonObject2 = jsonObject;
                            if (b < 2) {
                                JSONObject jSONObject;
                                if (b == 0) {
                                    if (json != null) {
                                        try {
                                            jSONObject = new JSONObject(json);
                                            currentKeys = keys;
                                        } catch (Exception e2) {
                                            jsonObject = jsonObject2;
                                        }
                                    }
                                    jsonObject = jsonObject2;
                                } else if (documentValues == null) {
                                    jsonObject = jsonObject2;
                                    b++;
                                } else {
                                    if (documentsJson != null) {
                                        jSONObject = new JSONObject(documentsJson);
                                        currentKeys = documentKeys;
                                    }
                                    jsonObject = jsonObject2;
                                }
                                if (!(currentKeys == null || jsonObject == null)) {
                                    try {
                                        Iterator<String> iter = jsonObject.keys();
                                        while (iter.hasNext()) {
                                            String key = (String) iter.next();
                                            if (b == 0) {
                                                values.put(key, jsonObject.getString(key));
                                            } else {
                                                documentValues.put(key, jsonObject.getString(key));
                                            }
                                        }
                                    } catch (Throwable e3) {
                                        try {
                                            FileLog.e(e3);
                                        } catch (Exception e4) {
                                        }
                                    }
                                    int a = 0;
                                    while (true) {
                                        StringBuilder stringBuilder2;
                                        try {
                                            stringBuilder2 = stringBuilder;
                                            if (a < currentKeys.length) {
                                                if (jsonObject.has(currentKeys[a])) {
                                                    if (stringBuilder2 == null) {
                                                        stringBuilder = new StringBuilder();
                                                    } else {
                                                        stringBuilder = stringBuilder2;
                                                    }
                                                    String jsonValue = jsonObject.getString(currentKeys[a]);
                                                    if (!(jsonValue == null || TextUtils.isEmpty(jsonValue) || "first_name_native".equals(currentKeys[a]) || "middle_name_native".equals(currentKeys[a]) || "last_name_native".equals(currentKeys[a]))) {
                                                        if (stringBuilder.length() > 0) {
                                                            if ("last_name".equals(currentKeys[a]) || "last_name_native".equals(currentKeys[a]) || "middle_name".equals(currentKeys[a]) || "middle_name_native".equals(currentKeys[a])) {
                                                                stringBuilder.append(" ");
                                                            } else {
                                                                stringBuilder.append(", ");
                                                            }
                                                        }
                                                        String str = currentKeys[a];
                                                        Object obj = -1;
                                                        switch (str.hashCode()) {
                                                            case -2006252145:
                                                                if (str.equals("residence_country_code")) {
                                                                    obj = 1;
                                                                    break;
                                                                }
                                                                break;
                                                            case -1249512767:
                                                                if (str.equals("gender")) {
                                                                    obj = 2;
                                                                    break;
                                                                }
                                                                break;
                                                            case 1481071862:
                                                                if (str.equals("country_code")) {
                                                                    obj = null;
                                                                    break;
                                                                }
                                                                break;
                                                        }
                                                        switch (obj) {
                                                            case null:
                                                            case 1:
                                                                String country = (String) this.languageMap.get(jsonValue);
                                                                if (country == null) {
                                                                    break;
                                                                }
                                                                stringBuilder.append(country);
                                                                break;
                                                            case 2:
                                                                if (!"male".equals(jsonValue)) {
                                                                    if (!"female".equals(jsonValue)) {
                                                                        break;
                                                                    }
                                                                    stringBuilder.append(LocaleController.getString("PassportFemale", NUM));
                                                                    break;
                                                                }
                                                                stringBuilder.append(LocaleController.getString("PassportMale", NUM));
                                                                break;
                                                            default:
                                                                stringBuilder.append(jsonValue);
                                                                break;
                                                        }
                                                    }
                                                }
                                                stringBuilder = stringBuilder2;
                                                a++;
                                            } else {
                                                stringBuilder = stringBuilder2;
                                            }
                                        } catch (Exception e5) {
                                            stringBuilder = stringBuilder2;
                                        }
                                    }
                                }
                                b++;
                            }
                        }
                    }
                } else {
                    return;
                }
            }
            if (stringBuilder != null) {
                value = stringBuilder.toString();
            }
        } else if (requiredType.type instanceof TL_secureValueTypePhone) {
            value = PhoneFormat.getInstance().format("+" + text);
        } else if (requiredType.type instanceof TL_secureValueTypeEmail) {
            value = text;
        }
        boolean isError = false;
        HashMap<String, String> errors = !documentOnly ? (HashMap) this.errorsMap.get(getNameForType(requiredType.type)) : null;
        HashMap<String, String> documentsErrors = documentRequiredType != null ? (HashMap) this.errorsMap.get(getNameForType(documentRequiredType.type)) : null;
        if ((errors != null && errors.size() > 0) || (documentsErrors != null && documentsErrors.size() > 0)) {
            value = null;
            if (!documentOnly) {
                value = (String) this.mainErrorsMap.get(getNameForType(requiredType.type));
            }
            if (value == null) {
                value = (String) this.mainErrorsMap.get(getNameForType(documentRequiredType.type));
            }
            isError = true;
        } else if (requiredType.type instanceof TL_secureValueTypePersonalDetails) {
            if (TextUtils.isEmpty(value)) {
                if (documentRequiredType == null) {
                    value = LocaleController.getString("PassportPersonalDetailsInfo", NUM);
                } else if (this.currentActivityType == 8) {
                    value = LocaleController.getString("PassportDocuments", NUM);
                } else if (availableDocumentTypesCount != 1) {
                    value = LocaleController.getString("PassportIdentityDocumentInfo", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypePassport) {
                    value = LocaleController.getString("PassportIdentityPassport", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeInternalPassport) {
                    value = LocaleController.getString("PassportIdentityInternalPassport", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeDriverLicense) {
                    value = LocaleController.getString("PassportIdentityDriverLicence", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeIdentityCard) {
                    value = LocaleController.getString("PassportIdentityID", NUM);
                }
            }
        } else if (requiredType.type instanceof TL_secureValueTypeAddress) {
            if (TextUtils.isEmpty(value)) {
                if (documentRequiredType == null) {
                    value = LocaleController.getString("PassportAddressNoUploadInfo", NUM);
                } else if (this.currentActivityType == 8) {
                    value = LocaleController.getString("PassportDocuments", NUM);
                } else if (availableDocumentTypesCount != 1) {
                    value = LocaleController.getString("PassportAddressInfo", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeRentalAgreement) {
                    value = LocaleController.getString("PassportAddAgreementInfo", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeUtilityBill) {
                    value = LocaleController.getString("PassportAddBillInfo", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypePassportRegistration) {
                    value = LocaleController.getString("PassportAddPassportRegistrationInfo", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeTemporaryRegistration) {
                    value = LocaleController.getString("PassportAddTemporaryRegistrationInfo", NUM);
                } else if (documentRequiredType.type instanceof TL_secureValueTypeBankStatement) {
                    value = LocaleController.getString("PassportAddBankInfo", NUM);
                }
            }
        } else if (requiredType.type instanceof TL_secureValueTypePhone) {
            if (TextUtils.isEmpty(value)) {
                value = LocaleController.getString("PassportPhoneInfo", NUM);
            }
        } else if ((requiredType.type instanceof TL_secureValueTypeEmail) && TextUtils.isEmpty(value)) {
            value = LocaleController.getString("PassportEmailInfo", NUM);
        }
        view.setValue(value);
        view.valueTextView.setTextColor(Theme.getColor(isError ? "windowBackgroundWhiteRedText3" : "windowBackgroundWhiteGrayText2"));
        boolean z = (isError || this.currentActivityType == 8 || (((!documentOnly || documentRequiredType == null) && (documentOnly || requiredTypeValue == null)) || (documentRequiredType != null && documentRequiredTypeValue == null))) ? false : true;
        view.setChecked(z);
    }

    private void checkNativeFields(boolean byEdit) {
        if (this.inputExtraFields != null) {
            String country = (String) this.languageMap.get(this.currentResidence);
            String lang = (String) SharedConfig.getCountryLangs().get(this.currentResidence);
            int a;
            if (this.currentType.native_names && !TextUtils.isEmpty(this.currentResidence) && !"EN".equals(lang)) {
                if (this.nativeInfoCell.getVisibility() != 0) {
                    this.nativeInfoCell.setVisibility(0);
                    this.headerCell.setVisibility(0);
                    this.extraBackgroundView2.setVisibility(0);
                    for (EditTextBoldCursor parent : this.inputExtraFields) {
                        ((View) parent.getParent()).setVisibility(0);
                    }
                    if (this.inputExtraFields[0].length() == 0 && this.inputExtraFields[1].length() == 0 && this.inputExtraFields[2].length() == 0) {
                        for (boolean z : this.nonLatinNames) {
                            if (z) {
                                this.inputExtraFields[0].setText(this.inputFields[0].getText());
                                this.inputExtraFields[1].setText(this.inputFields[1].getText());
                                this.inputExtraFields[2].setText(this.inputFields[2].getText());
                                break;
                            }
                        }
                    }
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, "windowBackgroundGrayShadow"));
                }
                this.nativeInfoCell.setText(LocaleController.formatString("PassportNativeInfo", NUM, country));
                String header = lang != null ? LocaleController.getServerString("PassportLanguage_" + lang) : null;
                if (header != null) {
                    this.headerCell.setText(LocaleController.formatString("PassportNativeHeaderLang", NUM, header));
                } else {
                    this.headerCell.setText(LocaleController.getString("PassportNativeHeader", NUM));
                }
                for (a = 0; a < 3; a++) {
                    switch (a) {
                        case 0:
                            if (header == null) {
                                this.inputExtraFields[a].setHintText(LocaleController.formatString("PassportNameCountry", NUM, country));
                                break;
                            } else {
                                this.inputExtraFields[a].setHintText(LocaleController.getString("PassportName", NUM));
                                break;
                            }
                        case 1:
                            if (header == null) {
                                this.inputExtraFields[a].setHintText(LocaleController.formatString("PassportMidnameCountry", NUM, country));
                                break;
                            } else {
                                this.inputExtraFields[a].setHintText(LocaleController.getString("PassportMidname", NUM));
                                break;
                            }
                        case 2:
                            if (header == null) {
                                this.inputExtraFields[a].setHintText(LocaleController.formatString("PassportSurnameCountry", NUM, country));
                                break;
                            } else {
                                this.inputExtraFields[a].setHintText(LocaleController.getString("PassportSurname", NUM));
                                break;
                            }
                        default:
                            break;
                    }
                }
                if (byEdit) {
                    AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$40(this));
                }
            } else if (this.nativeInfoCell.getVisibility() != 8) {
                this.nativeInfoCell.setVisibility(8);
                this.headerCell.setVisibility(8);
                this.extraBackgroundView2.setVisibility(8);
                for (EditTextBoldCursor parent2 : this.inputExtraFields) {
                    ((View) parent2.getParent()).setVisibility(8);
                }
                if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, "windowBackgroundGrayShadow"));
                } else {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, "windowBackgroundGrayShadow"));
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkNativeFields$59$PassportActivity() {
        if (this.inputExtraFields != null) {
            scrollToField(this.inputExtraFields[0]);
        }
    }

    private String getErrorsString(HashMap<String, String> errors, HashMap<String, String> documentErrors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < 2; a++) {
            HashMap<String, String> hashMap;
            if (a == 0) {
                hashMap = errors;
            } else {
                hashMap = documentErrors;
            }
            if (hashMap != null) {
                for (Entry<String, String> entry : hashMap.entrySet()) {
                    String value = (String) entry.getValue();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                        value = value.toLowerCase();
                    }
                    if (value.endsWith(".")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    stringBuilder.append(value);
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.append('.');
        }
        return stringBuilder.toString();
    }

    private TL_secureValue getValueByType(TL_secureRequiredType requiredType, boolean check) {
        if (requiredType == null) {
            return null;
        }
        int a = 0;
        int size = this.currentForm.values.size();
        while (a < size) {
            TL_secureValue secureValue = (TL_secureValue) this.currentForm.values.get(a);
            if (requiredType.type.getClass() != secureValue.type.getClass()) {
                a++;
            } else if (!check) {
                return secureValue;
            } else {
                if (requiredType.selfie_required && !(secureValue.selfie instanceof TL_secureFile)) {
                    return null;
                }
                if (requiredType.translation_required && secureValue.translation.isEmpty()) {
                    return null;
                }
                if (isAddressDocument(requiredType.type) && secureValue.files.isEmpty()) {
                    return null;
                }
                if (isPersonalDocument(requiredType.type) && !(secureValue.front_side instanceof TL_secureFile)) {
                    return null;
                }
                if (((requiredType.type instanceof TL_secureValueTypeDriverLicense) || (requiredType.type instanceof TL_secureValueTypeIdentityCard)) && !(secureValue.reverse_side instanceof TL_secureFile)) {
                    return null;
                }
                if (!(requiredType.type instanceof TL_secureValueTypePersonalDetails) && !(requiredType.type instanceof TL_secureValueTypeAddress)) {
                    return secureValue;
                }
                String[] keys = requiredType.type instanceof TL_secureValueTypePersonalDetails ? requiredType.native_names ? new String[]{"first_name_native", "last_name_native", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"first_name", "last_name", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"street_line1", "street_line2", "post_code", "city", "state", "country_code"};
                try {
                    JSONObject jsonObject = new JSONObject(decryptData(secureValue.data.data, decryptValueSecret(secureValue.data.secret, secureValue.data.data_hash), secureValue.data.data_hash));
                    int b = 0;
                    while (b < keys.length) {
                        if (!jsonObject.has(keys[b]) || TextUtils.isEmpty(jsonObject.getString(keys[b]))) {
                            return null;
                        }
                        b++;
                    }
                    return secureValue;
                } catch (Throwable th) {
                    return null;
                }
            }
        }
        return null;
    }

    private void openTypeActivity(TL_secureRequiredType requiredType, TL_secureRequiredType documentRequiredType, ArrayList<TL_secureRequiredType> availableDocumentTypes, boolean documentOnly) {
        int activityType = -1;
        final int availableDocumentTypesCount = availableDocumentTypes != null ? availableDocumentTypes.size() : 0;
        SecureValueType type = requiredType.type;
        SecureValueType documentType = documentRequiredType != null ? documentRequiredType.type : null;
        if (type instanceof TL_secureValueTypePersonalDetails) {
            activityType = 1;
        } else if (type instanceof TL_secureValueTypeAddress) {
            activityType = 2;
        } else if (type instanceof TL_secureValueTypePhone) {
            activityType = 3;
        } else if (type instanceof TL_secureValueTypeEmail) {
            activityType = 4;
        }
        if (activityType != -1) {
            HashMap<String, String> errors = !documentOnly ? (HashMap) this.errorsMap.get(getNameForType(type)) : null;
            HashMap<String, String> documentsErrors = (HashMap) this.errorsMap.get(getNameForType(documentType));
            PassportActivity activity = new PassportActivity(activityType, this.currentForm, this.currentPassword, requiredType, getValueByType(requiredType, false), documentRequiredType, getValueByType(documentRequiredType, false), (HashMap) this.typesValues.get(requiredType), documentRequiredType != null ? (HashMap) this.typesValues.get(documentRequiredType) : null);
            final SecureValueType secureValueType = type;
            final boolean z = documentOnly;
            activity.delegate = new PassportActivityDelegate() {
                private InputSecureFile getInputSecureFile(SecureDocument document) {
                    if (document.inputFile != null) {
                        TL_inputSecureFileUploaded inputSecureFileUploaded = new TL_inputSecureFileUploaded();
                        inputSecureFileUploaded.id = document.inputFile.id;
                        inputSecureFileUploaded.parts = document.inputFile.parts;
                        inputSecureFileUploaded.md5_checksum = document.inputFile.md5_checksum;
                        inputSecureFileUploaded.file_hash = document.fileHash;
                        inputSecureFileUploaded.secret = document.fileSecret;
                        return inputSecureFileUploaded;
                    }
                    InputSecureFile inputSecureFile = new TL_inputSecureFile();
                    inputSecureFile.id = document.secureFile.id;
                    inputSecureFile.access_hash = document.secureFile.access_hash;
                    return inputSecureFile;
                }

                private void renameFile(SecureDocument oldDocument, TL_secureFile newSecureFile) {
                    File oldFile = FileLoader.getPathToAttach(oldDocument);
                    String oldKey = oldDocument.secureFile.dc_id + "_" + oldDocument.secureFile.id;
                    File newFile = FileLoader.getPathToAttach(newSecureFile);
                    String newKey = newSecureFile.dc_id + "_" + newSecureFile.id;
                    oldFile.renameTo(newFile);
                    ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, null, false);
                }

                public void saveValue(TL_secureRequiredType requiredType, String text, String json, TL_secureRequiredType documentRequiredType, String documentsJson, ArrayList<SecureDocument> documents, SecureDocument selfie, ArrayList<SecureDocument> translationDocuments, SecureDocument front, SecureDocument reverse, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    EncryptionResult result;
                    TL_inputSecureValue inputSecureValue = null;
                    if (!TextUtils.isEmpty(json)) {
                        inputSecureValue = new TL_inputSecureValue();
                        inputSecureValue.type = requiredType.type;
                        inputSecureValue.flags |= 1;
                        result = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(json));
                        inputSecureValue.data = new TL_secureData();
                        inputSecureValue.data.data = result.encryptedData;
                        inputSecureValue.data.data_hash = result.fileHash;
                        inputSecureValue.data.secret = result.fileSecret;
                    } else if (!TextUtils.isEmpty(text)) {
                        SecurePlainData plainData;
                        if (secureValueType instanceof TL_secureValueTypeEmail) {
                            SecurePlainData securePlainEmail = new TL_securePlainEmail();
                            securePlainEmail.email = text;
                            plainData = securePlainEmail;
                        } else if (secureValueType instanceof TL_secureValueTypePhone) {
                            SecurePlainData securePlainPhone = new TL_securePlainPhone();
                            securePlainPhone.phone = text;
                            plainData = securePlainPhone;
                        } else {
                            return;
                        }
                        inputSecureValue = new TL_inputSecureValue();
                        inputSecureValue.type = requiredType.type;
                        inputSecureValue.flags |= 32;
                        inputSecureValue.plain_data = plainData;
                    }
                    if (z || inputSecureValue != null) {
                        TL_inputSecureValue fileInputSecureValue;
                        if (documentRequiredType != null) {
                            int size;
                            int a;
                            fileInputSecureValue = new TL_inputSecureValue();
                            fileInputSecureValue.type = documentRequiredType.type;
                            if (!TextUtils.isEmpty(documentsJson)) {
                                fileInputSecureValue.flags |= 1;
                                result = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(documentsJson));
                                fileInputSecureValue.data = new TL_secureData();
                                fileInputSecureValue.data.data = result.encryptedData;
                                fileInputSecureValue.data.data_hash = result.fileHash;
                                fileInputSecureValue.data.secret = result.fileSecret;
                            }
                            if (front != null) {
                                fileInputSecureValue.front_side = getInputSecureFile(front);
                                fileInputSecureValue.flags |= 2;
                            }
                            if (reverse != null) {
                                fileInputSecureValue.reverse_side = getInputSecureFile(reverse);
                                fileInputSecureValue.flags |= 4;
                            }
                            if (selfie != null) {
                                fileInputSecureValue.selfie = getInputSecureFile(selfie);
                                fileInputSecureValue.flags |= 8;
                            }
                            if (!(translationDocuments == null || translationDocuments.isEmpty())) {
                                fileInputSecureValue.flags |= 64;
                                size = translationDocuments.size();
                                for (a = 0; a < size; a++) {
                                    fileInputSecureValue.translation.add(getInputSecureFile((SecureDocument) translationDocuments.get(a)));
                                }
                            }
                            if (!(documents == null || documents.isEmpty())) {
                                fileInputSecureValue.flags |= 16;
                                size = documents.size();
                                for (a = 0; a < size; a++) {
                                    fileInputSecureValue.files.add(getInputSecureFile((SecureDocument) documents.get(a)));
                                }
                            }
                            if (z) {
                                inputSecureValue = fileInputSecureValue;
                                fileInputSecureValue = null;
                            }
                        } else {
                            fileInputSecureValue = null;
                        }
                        final TL_inputSecureValue finalFileInputSecureValue = fileInputSecureValue;
                        final TL_account_saveSecureValue req = new TL_account_saveSecureValue();
                        req.value = inputSecureValue;
                        req.secure_secret_id = PassportActivity.this.secureSecretId;
                        final ErrorRunnable errorRunnable2 = errorRunnable;
                        final String str = text;
                        final TL_secureRequiredType tL_secureRequiredType = documentRequiredType;
                        final TL_secureRequiredType tL_secureRequiredType2 = requiredType;
                        final ArrayList<SecureDocument> arrayList = documents;
                        final SecureDocument secureDocument = selfie;
                        final SecureDocument secureDocument2 = front;
                        final SecureDocument secureDocument3 = reverse;
                        final ArrayList<SecureDocument> arrayList2 = translationDocuments;
                        final String str2 = json;
                        final String str3 = documentsJson;
                        final Runnable runnable = finishRunnable;
                        ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                            private void onResult(TL_error error, TL_secureValue newValue, TL_secureValue newPendingValue) {
                                AndroidUtilities.runOnUIThread(new PassportActivity$19$1$$Lambda$0(this, error, errorRunnable2, str, req, z, tL_secureRequiredType, tL_secureRequiredType2, newValue, newPendingValue, arrayList, secureDocument, secureDocument2, secureDocument3, arrayList2, str2, str3, availableDocumentTypesCount, runnable));
                            }

                            /* Access modifiers changed, original: final|synthetic */
                            public final /* synthetic */ void lambda$onResult$0$PassportActivity$19$1(TL_error error, ErrorRunnable errorRunnable, String text, TL_account_saveSecureValue req, boolean documentOnly, TL_secureRequiredType documentRequiredType, TL_secureRequiredType requiredType, TL_secureValue newValue, TL_secureValue newPendingValue, ArrayList documents, SecureDocument selfie, SecureDocument front, SecureDocument reverse, ArrayList translationDocuments, String json, String documentsJson, int availableDocumentTypesCount, Runnable finishRunnable) {
                                if (error != null) {
                                    if (errorRunnable != null) {
                                        errorRunnable.onError(error.text, text);
                                    }
                                    AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, text);
                                    return;
                                }
                                int size;
                                int a;
                                SecureDocument document;
                                int size2;
                                int b;
                                SecureFile file;
                                TL_secureFile secureFile;
                                if (!documentOnly) {
                                    PassportActivity.this.removeValue(requiredType);
                                    PassportActivity.this.removeValue(documentRequiredType);
                                } else if (documentRequiredType != null) {
                                    PassportActivity.this.removeValue(documentRequiredType);
                                } else {
                                    PassportActivity.this.removeValue(requiredType);
                                }
                                if (newValue != null) {
                                    PassportActivity.this.currentForm.values.add(newValue);
                                }
                                if (newPendingValue != null) {
                                    PassportActivity.this.currentForm.values.add(newPendingValue);
                                }
                                if (!(documents == null || documents.isEmpty())) {
                                    size = documents.size();
                                    for (a = 0; a < size; a++) {
                                        document = (SecureDocument) documents.get(a);
                                        if (document.inputFile != null) {
                                            size2 = newValue.files.size();
                                            for (b = 0; b < size2; b++) {
                                                file = (SecureFile) newValue.files.get(b);
                                                if (file instanceof TL_secureFile) {
                                                    secureFile = (TL_secureFile) file;
                                                    if (Utilities.arraysEquals(document.fileSecret, 0, secureFile.secret, 0)) {
                                                        AnonymousClass19.this.renameFile(document, secureFile);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!(selfie == null || selfie.inputFile == null || !(newValue.selfie instanceof TL_secureFile))) {
                                    secureFile = (TL_secureFile) newValue.selfie;
                                    if (Utilities.arraysEquals(selfie.fileSecret, 0, secureFile.secret, 0)) {
                                        AnonymousClass19.this.renameFile(selfie, secureFile);
                                    }
                                }
                                if (!(front == null || front.inputFile == null || !(newValue.front_side instanceof TL_secureFile))) {
                                    secureFile = (TL_secureFile) newValue.front_side;
                                    if (Utilities.arraysEquals(front.fileSecret, 0, secureFile.secret, 0)) {
                                        AnonymousClass19.this.renameFile(front, secureFile);
                                    }
                                }
                                if (!(reverse == null || reverse.inputFile == null || !(newValue.reverse_side instanceof TL_secureFile))) {
                                    secureFile = (TL_secureFile) newValue.reverse_side;
                                    if (Utilities.arraysEquals(reverse.fileSecret, 0, secureFile.secret, 0)) {
                                        AnonymousClass19.this.renameFile(reverse, secureFile);
                                    }
                                }
                                if (!(translationDocuments == null || translationDocuments.isEmpty())) {
                                    size = translationDocuments.size();
                                    for (a = 0; a < size; a++) {
                                        document = (SecureDocument) translationDocuments.get(a);
                                        if (document.inputFile != null) {
                                            size2 = newValue.translation.size();
                                            for (b = 0; b < size2; b++) {
                                                file = (SecureFile) newValue.translation.get(b);
                                                if (file instanceof TL_secureFile) {
                                                    secureFile = (TL_secureFile) file;
                                                    if (Utilities.arraysEquals(document.fileSecret, 0, secureFile.secret, 0)) {
                                                        AnonymousClass19.this.renameFile(document, secureFile);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                PassportActivity.this.setTypeValue(requiredType, text, json, documentRequiredType, documentsJson, documentOnly, availableDocumentTypesCount);
                                if (finishRunnable != null) {
                                    finishRunnable.run();
                                }
                            }

                            public void run(TLObject response, TL_error error) {
                                if (error != null) {
                                    if (error.text.equals("EMAIL_VERIFICATION_NEEDED")) {
                                        TL_account_sendVerifyEmailCode req = new TL_account_sendVerifyEmailCode();
                                        req.email = str;
                                        ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new PassportActivity$19$1$$Lambda$1(this, str, tL_secureRequiredType2, this, errorRunnable2));
                                        return;
                                    } else if (error.text.equals("PHONE_VERIFICATION_NEEDED")) {
                                        AndroidUtilities.runOnUIThread(new PassportActivity$19$1$$Lambda$2(errorRunnable2, error, str));
                                        return;
                                    }
                                }
                                if (error != null || finalFileInputSecureValue == null) {
                                    onResult(error, (TL_secureValue) response, null);
                                    return;
                                }
                                TL_secureValue pendingValue = (TL_secureValue) response;
                                TL_account_saveSecureValue req2 = new TL_account_saveSecureValue();
                                req2.value = finalFileInputSecureValue;
                                req2.secure_secret_id = PassportActivity.this.secureSecretId;
                                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req2, new PassportActivity$19$1$$Lambda$3(this, pendingValue));
                            }

                            /* Access modifiers changed, original: final|synthetic */
                            public final /* synthetic */ void lambda$run$2$PassportActivity$19$1(String text, TL_secureRequiredType requiredType, PassportActivityDelegate currentDelegate, ErrorRunnable errorRunnable, TLObject response1, TL_error error1) {
                                AndroidUtilities.runOnUIThread(new PassportActivity$19$1$$Lambda$4(this, response1, text, requiredType, currentDelegate, error1, errorRunnable));
                            }

                            /* Access modifiers changed, original: final|synthetic */
                            public final /* synthetic */ void lambda$null$1$PassportActivity$19$1(TLObject response1, String text, TL_secureRequiredType requiredType, PassportActivityDelegate currentDelegate, TL_error error1, ErrorRunnable errorRunnable) {
                                if (response1 != null) {
                                    TL_account_sentEmailCode res = (TL_account_sentEmailCode) response1;
                                    HashMap values = new HashMap();
                                    values.put("email", text);
                                    values.put("pattern", res.email_pattern);
                                    PassportActivity activity1 = new PassportActivity(6, PassportActivity.this.currentForm, PassportActivity.this.currentPassword, requiredType, null, null, null, values, null);
                                    activity1.currentAccount = PassportActivity.this.currentAccount;
                                    activity1.emailCodeLength = res.length;
                                    activity1.saltedPassword = PassportActivity.this.saltedPassword;
                                    activity1.secureSecret = PassportActivity.this.secureSecret;
                                    activity1.delegate = currentDelegate;
                                    PassportActivity.this.presentFragment(activity1, true);
                                    return;
                                }
                                PassportActivity.this.showAlertWithText(LocaleController.getString("PassportEmail", NUM), error1.text);
                                if (errorRunnable != null) {
                                    errorRunnable.onError(error1.text, text);
                                }
                            }

                            /* Access modifiers changed, original: final|synthetic */
                            public final /* synthetic */ void lambda$run$4$PassportActivity$19$1(TL_secureValue pendingValue, TLObject response12, TL_error error12) {
                                onResult(error12, (TL_secureValue) response12, pendingValue);
                            }
                        });
                    } else if (errorRunnable != null) {
                        errorRunnable.onError(null, null);
                    }
                }

                public SecureDocument saveFile(TL_secureFile secureFile) {
                    String path = FileLoader.getDirectory(4) + "/" + secureFile.dc_id + "_" + secureFile.id + ".jpg";
                    EncryptionResult result = PassportActivity.this.createSecureDocument(path);
                    return new SecureDocument(result.secureDocumentKey, secureFile, path, result.fileHash, result.fileSecret);
                }

                public void deleteValue(TL_secureRequiredType requiredType, TL_secureRequiredType documentRequiredType, ArrayList<TL_secureRequiredType> documentRequiredTypes, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(requiredType, documentRequiredType, documentRequiredTypes, deleteType, finishRunnable, errorRunnable, z);
                }
            };
            activity.currentAccount = this.currentAccount;
            activity.saltedPassword = this.saltedPassword;
            activity.secureSecret = this.secureSecret;
            activity.currentBotId = this.currentBotId;
            activity.fieldsErrors = errors;
            activity.documentOnly = documentOnly;
            activity.documentsErrors = documentsErrors;
            activity.availableDocumentTypes = availableDocumentTypes;
            if (activityType == 4) {
                activity.currentEmail = this.currentEmail;
            }
            presentFragment(activity);
        }
    }

    private TL_secureValue removeValue(TL_secureRequiredType requiredType) {
        if (requiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            if (requiredType.type.getClass() == ((TL_secureValue) this.currentForm.values.get(a)).type.getClass()) {
                return (TL_secureValue) this.currentForm.values.remove(a);
            }
        }
        return null;
    }

    private void deleteValueInternal(TL_secureRequiredType requiredType, TL_secureRequiredType documentRequiredType, ArrayList<TL_secureRequiredType> documentRequiredTypes, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable, boolean documentOnly) {
        if (requiredType != null) {
            TL_account_deleteSecureValue req = new TL_account_deleteSecureValue();
            if (!documentOnly || documentRequiredType == null) {
                if (deleteType) {
                    req.types.add(requiredType.type);
                }
                if (documentRequiredType != null) {
                    req.types.add(documentRequiredType.type);
                }
            } else {
                req.types.add(documentRequiredType.type);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PassportActivity$$Lambda$41(this, errorRunnable, documentOnly, documentRequiredType, requiredType, deleteType, documentRequiredTypes, finishRunnable));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteValueInternal$61$PassportActivity(ErrorRunnable errorRunnable, boolean documentOnly, TL_secureRequiredType documentRequiredType, TL_secureRequiredType requiredType, boolean deleteType, ArrayList documentRequiredTypes, Runnable finishRunnable, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$55(this, error, errorRunnable, documentOnly, documentRequiredType, requiredType, deleteType, documentRequiredTypes, finishRunnable));
    }

    /* Access modifiers changed, original: final|synthetic */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00d3  */
    public final /* synthetic */ void lambda$null$60$PassportActivity(org.telegram.tgnet.TLRPC.TL_error r18, org.telegram.ui.PassportActivity.ErrorRunnable r19, boolean r20, org.telegram.tgnet.TLRPC.TL_secureRequiredType r21, org.telegram.tgnet.TLRPC.TL_secureRequiredType r22, boolean r23, java.util.ArrayList r24, java.lang.Runnable r25) {
        /*
        r17 = this;
        if (r18 == 0) goto L_0x0022;
    L_0x0002:
        if (r19 == 0) goto L_0x000e;
    L_0x0004:
        r0 = r18;
        r2 = r0.text;
        r3 = 0;
        r0 = r19;
        r0.onError(r2, r3);
    L_0x000e:
        r2 = "AppName";
        r3 = NUM; // 0x7f0CLASSNAMEde float:1.8609642E38 double:1.053097508E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r0 = r18;
        r3 = r0.text;
        r0 = r17;
        r0.showAlertWithText(r2, r3);
    L_0x0021:
        return;
    L_0x0022:
        if (r20 == 0) goto L_0x0079;
    L_0x0024:
        if (r21 == 0) goto L_0x0071;
    L_0x0026:
        r0 = r17;
        r1 = r21;
        r0.removeValue(r1);
    L_0x002d:
        r0 = r17;
        r2 = r0.currentActivityType;
        r3 = 8;
        if (r2 != r3) goto L_0x008a;
    L_0x0035:
        r0 = r17;
        r2 = r0.typesViews;
        r0 = r22;
        r16 = r2.remove(r0);
        r16 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r16;
        if (r16 == 0) goto L_0x0068;
    L_0x0043:
        r0 = r17;
        r2 = r0.linearLayout2;
        r0 = r16;
        r2.removeView(r0);
        r0 = r17;
        r2 = r0.linearLayout2;
        r0 = r17;
        r3 = r0.linearLayout2;
        r3 = r3.getChildCount();
        r3 = r3 + -6;
        r11 = r2.getChildAt(r3);
        r2 = r11 instanceof org.telegram.ui.PassportActivity.TextDetailSecureCell;
        if (r2 == 0) goto L_0x0068;
    L_0x0062:
        r11 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r11;
        r2 = 0;
        r11.setNeedDivider(r2);
    L_0x0068:
        r17.updateManageVisibility();
    L_0x006b:
        if (r25 == 0) goto L_0x0021;
    L_0x006d:
        r25.run();
        goto L_0x0021;
    L_0x0071:
        r0 = r17;
        r1 = r22;
        r0.removeValue(r1);
        goto L_0x002d;
    L_0x0079:
        if (r23 == 0) goto L_0x0082;
    L_0x007b:
        r0 = r17;
        r1 = r22;
        r0.removeValue(r1);
    L_0x0082:
        r0 = r17;
        r1 = r21;
        r0.removeValue(r1);
        goto L_0x002d;
    L_0x008a:
        r7 = 0;
        r6 = r21;
        if (r6 == 0) goto L_0x00dc;
    L_0x008f:
        if (r24 == 0) goto L_0x00dc;
    L_0x0091:
        r2 = r24.size();
        r3 = 1;
        if (r2 <= r3) goto L_0x00dc;
    L_0x0098:
        r10 = 0;
        r12 = r24.size();
    L_0x009d:
        if (r10 >= r12) goto L_0x00d1;
    L_0x009f:
        r0 = r24;
        r13 = r0.get(r10);
        r13 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r13;
        r2 = 0;
        r0 = r17;
        r14 = r0.getValueByType(r13, r2);
        if (r14 == 0) goto L_0x00f1;
    L_0x00b0:
        r2 = r14.data;
        if (r2 == 0) goto L_0x00d0;
    L_0x00b4:
        r2 = r14.data;
        r2 = r2.data;
        r3 = r14.data;
        r3 = r3.secret;
        r4 = r14.data;
        r4 = r4.data_hash;
        r0 = r17;
        r3 = r0.decryptValueSecret(r3, r4);
        r4 = r14.data;
        r4 = r4.data_hash;
        r0 = r17;
        r7 = r0.decryptData(r2, r3, r4);
    L_0x00d0:
        r6 = r13;
    L_0x00d1:
        if (r6 != 0) goto L_0x00dc;
    L_0x00d3:
        r2 = 0;
        r0 = r24;
        r6 = r0.get(r2);
        r6 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r6;
    L_0x00dc:
        if (r23 == 0) goto L_0x00f6;
    L_0x00de:
        r4 = 0;
        r5 = 0;
        if (r24 == 0) goto L_0x00f4;
    L_0x00e2:
        r9 = r24.size();
    L_0x00e6:
        r2 = r17;
        r3 = r22;
        r8 = r20;
        r2.setTypeValue(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x006b;
    L_0x00f1:
        r10 = r10 + 1;
        goto L_0x009d;
    L_0x00f4:
        r9 = 0;
        goto L_0x00e6;
    L_0x00f6:
        r5 = 0;
        r2 = 0;
        r0 = r17;
        r1 = r22;
        r15 = r0.getValueByType(r1, r2);
        if (r15 == 0) goto L_0x0122;
    L_0x0102:
        r2 = r15.data;
        if (r2 == 0) goto L_0x0122;
    L_0x0106:
        r2 = r15.data;
        r2 = r2.data;
        r3 = r15.data;
        r3 = r3.secret;
        r4 = r15.data;
        r4 = r4.data_hash;
        r0 = r17;
        r3 = r0.decryptValueSecret(r3, r4);
        r4 = r15.data;
        r4 = r4.data_hash;
        r0 = r17;
        r5 = r0.decryptData(r2, r3, r4);
    L_0x0122:
        r4 = 0;
        if (r24 == 0) goto L_0x0134;
    L_0x0125:
        r9 = r24.size();
    L_0x0129:
        r2 = r17;
        r3 = r22;
        r8 = r20;
        r2.setTypeValue(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x006b;
    L_0x0134:
        r9 = 0;
        goto L_0x0129;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$null$60$PassportActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.PassportActivity$ErrorRunnable, boolean, org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType, boolean, java.util.ArrayList, java.lang.Runnable):void");
    }

    private TextDetailSecureCell addField(Context context, TL_secureRequiredType requiredType, ArrayList<TL_secureRequiredType> documentRequiredTypes, boolean documentOnly, boolean last) {
        String text;
        int availableDocumentTypesCount = documentRequiredTypes != null ? documentRequiredTypes.size() : 0;
        View textDetailSecureCell = new TextDetailSecureCell(context);
        textDetailSecureCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        String[] strArr;
        String str;
        boolean z;
        if (requiredType.type instanceof TL_secureValueTypePersonalDetails) {
            if (documentRequiredTypes == null || documentRequiredTypes.isEmpty()) {
                text = LocaleController.getString("PassportPersonalDetails", NUM);
            } else if (documentOnly && documentRequiredTypes.size() == 1) {
                text = getTextForType(((TL_secureRequiredType) documentRequiredTypes.get(0)).type);
            } else if (documentOnly && documentRequiredTypes.size() == 2) {
                strArr = new Object[2];
                strArr[0] = getTextForType(((TL_secureRequiredType) documentRequiredTypes.get(0)).type);
                strArr[1] = getTextForType(((TL_secureRequiredType) documentRequiredTypes.get(1)).type);
                text = LocaleController.formatString("PassportTwoDocuments", NUM, strArr);
            } else {
                text = LocaleController.getString("PassportIdentityDocument", NUM);
            }
            str = "";
            if (last) {
                z = false;
            } else {
                z = true;
            }
            textDetailSecureCell.setTextAndValue(text, str, z);
        } else if (requiredType.type instanceof TL_secureValueTypeAddress) {
            if (documentRequiredTypes == null || documentRequiredTypes.isEmpty()) {
                text = LocaleController.getString("PassportAddress", NUM);
            } else if (documentOnly && documentRequiredTypes.size() == 1) {
                text = getTextForType(((TL_secureRequiredType) documentRequiredTypes.get(0)).type);
            } else if (documentOnly && documentRequiredTypes.size() == 2) {
                strArr = new Object[2];
                strArr[0] = getTextForType(((TL_secureRequiredType) documentRequiredTypes.get(0)).type);
                strArr[1] = getTextForType(((TL_secureRequiredType) documentRequiredTypes.get(1)).type);
                text = LocaleController.formatString("PassportTwoDocuments", NUM, strArr);
            } else {
                text = LocaleController.getString("PassportResidentialAddress", NUM);
            }
            str = "";
            if (last) {
                z = false;
            } else {
                z = true;
            }
            textDetailSecureCell.setTextAndValue(text, str, z);
        } else if (requiredType.type instanceof TL_secureValueTypePhone) {
            textDetailSecureCell.setTextAndValue(LocaleController.getString("PassportPhone", NUM), "", !last);
        } else if (requiredType.type instanceof TL_secureValueTypeEmail) {
            textDetailSecureCell.setTextAndValue(LocaleController.getString("PassportEmail", NUM), "", !last);
        }
        if (this.currentActivityType == 8) {
            this.linearLayout2.addView(textDetailSecureCell, this.linearLayout2.getChildCount() - 5, LayoutHelper.createLinear(-1, -2));
        } else {
            this.linearLayout2.addView(textDetailSecureCell, LayoutHelper.createLinear(-1, -2));
        }
        textDetailSecureCell.setOnClickListener(new PassportActivity$$Lambda$42(this, documentRequiredTypes, requiredType, documentOnly));
        this.typesViews.put(requiredType, textDetailSecureCell);
        text = null;
        String json = null;
        String documentJson = null;
        this.typesValues.put(requiredType, new HashMap());
        TL_secureValue value = getValueByType(requiredType, false);
        if (value != null) {
            if (value.plain_data instanceof TL_securePlainEmail) {
                text = ((TL_securePlainEmail) value.plain_data).email;
            } else if (value.plain_data instanceof TL_securePlainPhone) {
                text = ((TL_securePlainPhone) value.plain_data).phone;
            } else if (value.data != null) {
                json = decryptData(value.data.data, decryptValueSecret(value.data.secret, value.data.data_hash), value.data.data_hash);
            }
        }
        TL_secureRequiredType documentsType = null;
        if (!(documentRequiredTypes == null || documentRequiredTypes.isEmpty())) {
            boolean found = false;
            int count = documentRequiredTypes.size();
            for (int a = 0; a < count; a++) {
                TL_secureRequiredType documentType = (TL_secureRequiredType) documentRequiredTypes.get(a);
                this.typesValues.put(documentType, new HashMap());
                this.documentsToTypesLink.put(documentType, requiredType);
                if (!found) {
                    TL_secureValue documentValue = getValueByType(documentType, false);
                    if (documentValue != null) {
                        if (documentValue.data != null) {
                            documentJson = decryptData(documentValue.data.data, decryptValueSecret(documentValue.data.secret, documentValue.data.data_hash), documentValue.data.data_hash);
                        }
                        documentsType = documentType;
                        found = true;
                    }
                }
            }
            if (documentsType == null) {
                documentsType = (TL_secureRequiredType) documentRequiredTypes.get(0);
            }
        }
        setTypeValue(requiredType, text, json, documentsType, documentJson, documentOnly, availableDocumentTypesCount);
        return textDetailSecureCell;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addField$65$PassportActivity(ArrayList documentRequiredTypes, TL_secureRequiredType requiredType, boolean documentOnly, View v) {
        int count;
        int a;
        TL_secureRequiredType documentType;
        TL_secureRequiredType documentsType = null;
        if (documentRequiredTypes != null) {
            count = documentRequiredTypes.size();
            for (a = 0; a < count; a++) {
                documentType = (TL_secureRequiredType) documentRequiredTypes.get(a);
                if (getValueByType(documentType, false) != null || count == 1) {
                    documentsType = documentType;
                    break;
                }
            }
        }
        Builder builder;
        if (!(requiredType.type instanceof TL_secureValueTypePersonalDetails) && !(requiredType.type instanceof TL_secureValueTypeAddress)) {
            boolean phoneField = requiredType.type instanceof TL_secureValueTypePhone;
            if ((phoneField || (requiredType.type instanceof TL_secureValueTypeEmail)) && getValueByType(requiredType, false) != null) {
                builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new PassportActivity$$Lambda$52(this, requiredType, documentOnly));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(phoneField ? LocaleController.getString("PassportDeletePhoneAlert", NUM) : LocaleController.getString("PassportDeleteEmailAlert", NUM));
                showDialog(builder.create());
                return;
            }
        } else if (!(documentsType != null || documentRequiredTypes == null || documentRequiredTypes.isEmpty())) {
            builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
            if (requiredType.type instanceof TL_secureValueTypePersonalDetails) {
                builder.setTitle(LocaleController.getString("PassportIdentityDocument", NUM));
            } else if (requiredType.type instanceof TL_secureValueTypeAddress) {
                builder.setTitle(LocaleController.getString("PassportAddress", NUM));
            }
            ArrayList<String> strings = new ArrayList();
            count = documentRequiredTypes.size();
            for (a = 0; a < count; a++) {
                documentType = (TL_secureRequiredType) documentRequiredTypes.get(a);
                if (documentType.type instanceof TL_secureValueTypeDriverLicense) {
                    strings.add(LocaleController.getString("PassportAddLicence", NUM));
                } else if (documentType.type instanceof TL_secureValueTypePassport) {
                    strings.add(LocaleController.getString("PassportAddPassport", NUM));
                } else if (documentType.type instanceof TL_secureValueTypeInternalPassport) {
                    strings.add(LocaleController.getString("PassportAddInternalPassport", NUM));
                } else if (documentType.type instanceof TL_secureValueTypeIdentityCard) {
                    strings.add(LocaleController.getString("PassportAddCard", NUM));
                } else if (documentType.type instanceof TL_secureValueTypeUtilityBill) {
                    strings.add(LocaleController.getString("PassportAddBill", NUM));
                } else if (documentType.type instanceof TL_secureValueTypeBankStatement) {
                    strings.add(LocaleController.getString("PassportAddBank", NUM));
                } else if (documentType.type instanceof TL_secureValueTypeRentalAgreement) {
                    strings.add(LocaleController.getString("PassportAddAgreement", NUM));
                } else if (documentType.type instanceof TL_secureValueTypeTemporaryRegistration) {
                    strings.add(LocaleController.getString("PassportAddTemporaryRegistration", NUM));
                } else if (documentType.type instanceof TL_secureValueTypePassportRegistration) {
                    strings.add(LocaleController.getString("PassportAddPassportRegistration", NUM));
                }
            }
            builder.setItems((CharSequence[]) strings.toArray(new CharSequence[strings.size()]), new PassportActivity$$Lambda$51(this, requiredType, documentRequiredTypes, documentOnly));
            showDialog(builder.create());
            return;
        }
        openTypeActivity(requiredType, documentsType, documentRequiredTypes, documentOnly);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$62$PassportActivity(TL_secureRequiredType requiredType, ArrayList documentRequiredTypes, boolean documentOnly, DialogInterface dialog, int which) {
        openTypeActivity(requiredType, (TL_secureRequiredType) documentRequiredTypes.get(which), documentRequiredTypes, documentOnly);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$64$PassportActivity(TL_secureRequiredType requiredType, boolean documentOnly, DialogInterface dialog, int which) {
        needShowProgress();
        deleteValueInternal(requiredType, null, null, true, new PassportActivity$$Lambda$53(this), new PassportActivity$$Lambda$54(this), documentOnly);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$63$PassportActivity(String error, String text) {
        needHideProgress();
    }

    private SecureDocumentKey getSecureDocumentKey(byte[] file_secret, byte[] file_hash) {
        byte[] file_secret_hash = Utilities.computeSHA512(decryptValueSecret(file_secret, file_hash), file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        return new SecureDocumentKey(file_key, file_iv);
    }

    private byte[] decryptSecret(byte[] secret, byte[] passwordHash) {
        if (secret == null || secret.length != 32) {
            return null;
        }
        byte[] key = new byte[32];
        System.arraycopy(passwordHash, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(passwordHash, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(secret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        return decryptedSecret;
    }

    private byte[] decryptValueSecret(byte[] encryptedSecureValueSecret, byte[] hash) {
        if (encryptedSecureValueSecret == null || encryptedSecureValueSecret.length != 32 || hash == null || hash.length != 32) {
            return null;
        }
        byte[] key = new byte[32];
        System.arraycopy(this.saltedPassword, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(this.saltedPassword, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(this.secureSecret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        if (!checkSecret(decryptedSecret, null)) {
            return null;
        }
        byte[] secret_hash = Utilities.computeSHA512(decryptedSecret, hash);
        byte[] file_secret_key = new byte[32];
        System.arraycopy(secret_hash, 0, file_secret_key, 0, 32);
        byte[] file_secret_iv = new byte[16];
        System.arraycopy(secret_hash, 32, file_secret_iv, 0, 16);
        byte[] result = new byte[32];
        System.arraycopy(encryptedSecureValueSecret, 0, result, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(result, file_secret_key, file_secret_iv, 0, result.length, 0, 0);
        return result;
    }

    private EncryptionResult createSecureDocument(String path) {
        byte[] b = new byte[((int) new File(path).length())];
        RandomAccessFile f = null;
        try {
            RandomAccessFile f2 = new RandomAccessFile(path, "rws");
            try {
                f2.readFully(b);
                f = f2;
            } catch (Exception e) {
                f = f2;
            }
        } catch (Exception e2) {
        }
        EncryptionResult result = encryptData(b);
        try {
            f.seek(0);
            f.write(result.encryptedData);
            f.close();
        } catch (Exception e3) {
        }
        return result;
    }

    private String decryptData(byte[] data, byte[] file_secret, byte[] file_hash) {
        if (data == null || file_secret == null || file_secret.length != 32 || file_hash == null || file_hash.length != 32) {
            return null;
        }
        byte[] file_secret_hash = Utilities.computeSHA512(file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        byte[] decryptedData = new byte[data.length];
        System.arraycopy(data, 0, decryptedData, 0, data.length);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedData, file_key, file_iv, 0, decryptedData.length, 0, 0);
        if (!Arrays.equals(Utilities.computeSHA256(decryptedData), file_hash)) {
            return null;
        }
        int dataOffset = decryptedData[0] & 255;
        return new String(decryptedData, dataOffset, decryptedData.length - dataOffset);
    }

    public static boolean checkSecret(byte[] secret, Long id) {
        if (secret == null || secret.length != 32) {
            return false;
        }
        int sum = 0;
        for (byte b : secret) {
            sum += b & 255;
        }
        if (sum % 255 != 239) {
            return false;
        }
        if (id == null || Utilities.bytesToLong(Utilities.computeSHA256(secret)) == id.longValue()) {
            return true;
        }
        return false;
    }

    private byte[] getRandomSecret() {
        int a;
        byte[] secret = new byte[32];
        Utilities.random.nextBytes(secret);
        int sum = 0;
        for (byte b : secret) {
            sum += b & 255;
        }
        sum %= 255;
        if (sum != 239) {
            sum = 239 - sum;
            a = Utilities.random.nextInt(32);
            int val = (secret[a] & 255) + sum;
            if (val < 255) {
                val += 255;
            }
            secret[a] = (byte) (val % 255);
        }
        return secret;
    }

    private EncryptionResult encryptData(byte[] data) {
        byte[] file_secret = getRandomSecret();
        int extraLen = Utilities.random.nextInt(208) + 32;
        while ((data.length + extraLen) % 16 != 0) {
            extraLen++;
        }
        Object padding = new byte[extraLen];
        Utilities.random.nextBytes(padding);
        padding[0] = (byte) extraLen;
        byte[] paddedData = new byte[(data.length + extraLen)];
        System.arraycopy(padding, 0, paddedData, 0, extraLen);
        System.arraycopy(data, 0, paddedData, extraLen, data.length);
        byte[] file_hash = Utilities.computeSHA256(paddedData);
        Object file_secret_hash = Utilities.computeSHA512(file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        Utilities.aesCbcEncryptionByteArraySafe(paddedData, file_key, file_iv, 0, paddedData.length, 0, 1);
        byte[] key = new byte[32];
        System.arraycopy(this.saltedPassword, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(this.saltedPassword, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(this.secureSecret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        Object secret_hash = Utilities.computeSHA512(decryptedSecret, file_hash);
        byte[] file_secret_key = new byte[32];
        System.arraycopy(secret_hash, 0, file_secret_key, 0, 32);
        byte[] file_secret_iv = new byte[16];
        System.arraycopy(secret_hash, 32, file_secret_iv, 0, 16);
        byte[] encrypyed_file_secret = new byte[32];
        System.arraycopy(file_secret, 0, encrypyed_file_secret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(encrypyed_file_secret, file_secret_key, file_secret_iv, 0, encrypyed_file_secret.length, 0, 1);
        return new EncryptionResult(paddedData, encrypyed_file_secret, file_secret, file_hash, file_key, file_iv);
    }

    private void showAlertWithText(String title, String text) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setTitle(title);
            builder.setMessage(text);
            showDialog(builder.create());
        }
    }

    private void onPasscodeError(boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                this.inputFields[0].setText("");
            }
            AndroidUtilities.shakeView(this.inputFields[0], 2.0f, 0);
        }
    }

    private void startPhoneVerification(boolean checkPermissions, String phone, Runnable finishRunnable, ErrorRunnable errorRunnable, PassportActivityDelegate delegate) {
        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
        boolean allowCall = true;
        if (getParentActivity() != null && VERSION.SDK_INT >= 23 && simcardAvailable) {
            allowCall = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
            if (checkPermissions) {
                this.permissionsItems.clear();
                if (!allowCall) {
                    this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                }
                if (!this.permissionsItems.isEmpty()) {
                    if (getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                        this.permissionsDialog = showDialog(builder.create());
                    } else {
                        getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
                    }
                    this.pendingPhone = phone;
                    this.pendingErrorRunnable = errorRunnable;
                    this.pendingFinishRunnable = finishRunnable;
                    this.pendingDelegate = delegate;
                    return;
                }
            }
        }
        TL_account_sendVerifyPhoneCode req = new TL_account_sendVerifyPhoneCode();
        req.phone_number = phone;
        req.settings = new TL_codeSettings();
        TL_codeSettings tL_codeSettings = req.settings;
        boolean z = simcardAvailable && allowCall;
        tL_codeSettings.allow_flashcall = z;
        if (VERSION.SDK_INT >= 26) {
            try {
                req.settings.app_hash = SmsManager.getDefault().createAppSpecificSmsToken(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, SmsReceiver.class), NUM));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else {
            req.settings.app_hash = BuildVars.SMS_HASH;
            req.settings.app_hash_persistent = true;
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (TextUtils.isEmpty(req.settings.app_hash)) {
            preferences.edit().remove("sms_hash").commit();
        } else {
            TL_codeSettings tL_codeSettings2 = req.settings;
            tL_codeSettings2.flags |= 8;
            preferences.edit().putString("sms_hash", req.settings.app_hash).commit();
        }
        if (req.settings.allow_flashcall) {
            try {
                String number = tm.getLine1Number();
                if (TextUtils.isEmpty(number)) {
                    req.settings.current_number = false;
                } else {
                    tL_codeSettings = req.settings;
                    z = phone.contains(number) || number.contains(phone);
                    tL_codeSettings.current_number = z;
                    if (!req.settings.current_number) {
                        req.settings.allow_flashcall = false;
                    }
                }
            } catch (Exception e2) {
                req.settings.allow_flashcall = false;
                FileLog.e(e2);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PassportActivity$$Lambda$43(this, phone, delegate, req), 2);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$startPhoneVerification$67$PassportActivity(String phone, PassportActivityDelegate delegate, TL_account_sendVerifyPhoneCode req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$50(this, error, phone, delegate, response, req));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$66$PassportActivity(TL_error error, String phone, PassportActivityDelegate delegate, TLObject response, TL_account_sendVerifyPhoneCode req) {
        if (error == null) {
            HashMap values = new HashMap();
            values.put("phone", phone);
            PassportActivity activity = new PassportActivity(7, this.currentForm, this.currentPassword, this.currentType, null, null, null, values, null);
            activity.currentAccount = this.currentAccount;
            activity.saltedPassword = this.saltedPassword;
            activity.secureSecret = this.secureSecret;
            activity.delegate = delegate;
            activity.currentPhoneVerification = (TL_auth_sentCode) response;
            presentFragment(activity, true);
            return;
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, phone);
    }

    private void updatePasswordInterface() {
        if (this.noPasswordImageView != null) {
            if (this.currentPassword == null || this.usingSavedPassword != 0) {
                this.noPasswordImageView.setVisibility(8);
                this.noPasswordTextView.setVisibility(8);
                this.noPasswordSetTextView.setVisibility(8);
                this.passwordAvatarContainer.setVisibility(8);
                this.inputFieldContainers[0].setVisibility(8);
                this.doneItem.setVisibility(8);
                this.passwordForgotButton.setVisibility(8);
                this.passwordInfoRequestTextView.setVisibility(8);
                this.passwordRequestTextView.setVisibility(8);
                this.emptyView.setVisibility(0);
            } else if (this.currentPassword.has_password) {
                this.passwordRequestTextView.setVisibility(0);
                this.noPasswordImageView.setVisibility(8);
                this.noPasswordTextView.setVisibility(8);
                this.noPasswordSetTextView.setVisibility(8);
                this.emptyView.setVisibility(8);
                this.passwordAvatarContainer.setVisibility(0);
                this.inputFieldContainers[0].setVisibility(0);
                this.doneItem.setVisibility(0);
                this.passwordForgotButton.setVisibility(0);
                this.passwordInfoRequestTextView.setVisibility(0);
                this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
                if (this.inputFields == null) {
                    return;
                }
                if (this.currentPassword == null || TextUtils.isEmpty(this.currentPassword.hint)) {
                    this.inputFields[0].setHint(LocaleController.getString("LoginPassword", NUM));
                } else {
                    this.inputFields[0].setHint(this.currentPassword.hint);
                }
            } else {
                this.passwordRequestTextView.setVisibility(0);
                this.noPasswordImageView.setVisibility(0);
                this.noPasswordTextView.setVisibility(0);
                this.noPasswordSetTextView.setVisibility(0);
                this.passwordAvatarContainer.setVisibility(8);
                this.inputFieldContainers[0].setVisibility(8);
                this.doneItem.setVisibility(8);
                this.passwordForgotButton.setVisibility(8);
                this.passwordInfoRequestTextView.setVisibility(8);
                this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 25.0f, 0.0f, 0.0f));
                this.emptyView.setVisibility(8);
            }
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
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.doneItem.getImageView().setVisibility(0);
                this.doneItem.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.ALPHA, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        if (show) {
                            PassportActivity.this.doneItem.getImageView().setVisibility(4);
                        } else {
                            PassportActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (this.acceptTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (show) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.acceptTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        if (show) {
                            PassportActivity.this.acceptTextView.setVisibility(4);
                        } else {
                            PassportActivity.this.progressViewButton.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.FileDidUpload) {
            String location = args[0];
            SecureDocument document = (SecureDocument) this.uploadingDocuments.get(location);
            if (document != null) {
                document.inputFile = (TL_inputFile) args[1];
                this.uploadingDocuments.remove(location);
                if (this.uploadingDocuments.isEmpty() && this.doneItem != null) {
                    this.doneItem.setEnabled(true);
                    this.doneItem.setAlpha(1.0f);
                }
                if (this.documentsCells != null) {
                    SecureDocumentCell cell = (SecureDocumentCell) this.documentsCells.get(document);
                    if (cell != null) {
                        cell.updateButtonState(true);
                    }
                }
                if (this.errorsValues != null && this.errorsValues.containsKey("error_document_all")) {
                    this.errorsValues.remove("error_document_all");
                    checkTopErrorCell(false);
                }
                if (document.type == 0) {
                    if (!(this.bottomCell == null || TextUtils.isEmpty(this.noAllDocumentsErrorText))) {
                        this.bottomCell.setText(this.noAllDocumentsErrorText);
                    }
                    this.errorsValues.remove("files_all");
                } else if (document.type == 4) {
                    if (!(this.bottomCellTranslation == null || TextUtils.isEmpty(this.noAllTranslationErrorText))) {
                        this.bottomCellTranslation.setText(this.noAllTranslationErrorText);
                    }
                    this.errorsValues.remove("translation_all");
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
        } else {
            if (id == NotificationCenter.didSetTwoStepPassword) {
                if (args == null || args.length <= 0) {
                    this.currentPassword = null;
                    loadPasswordInfo();
                } else {
                    if (!(args[7] == null || this.inputFields[0] == null)) {
                        this.inputFields[0].setText((String) args[7]);
                    }
                    if (args[6] == null) {
                        this.currentPassword = new TL_account_password();
                        this.currentPassword.current_algo = (PasswordKdfAlgo) args[1];
                        this.currentPassword.new_secure_algo = (SecurePasswordKdfAlgo) args[2];
                        this.currentPassword.secure_random = (byte[]) args[3];
                        this.currentPassword.has_recovery = !TextUtils.isEmpty((String) args[4]);
                        this.currentPassword.hint = (String) args[5];
                        this.currentPassword.srp_id = -1;
                        this.currentPassword.srp_B = new byte[256];
                        Utilities.random.nextBytes(this.currentPassword.srp_B);
                        if (this.inputFields[0] != null && this.inputFields[0].length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                }
                updatePasswordInterface();
                return;
            }
            if (id == NotificationCenter.didRemoveTwoStepPassword) {
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$44(this));
        }
        if (this.currentActivityType == 5) {
            if (isOpen) {
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 2) {
                    onPasswordDone(false);
                }
            }
        } else if (this.currentActivityType == 7) {
            if (isOpen) {
                this.views[this.currentViewNum].onShow();
            }
        } else if (this.currentActivityType == 4) {
            if (isOpen) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if (this.currentActivityType == 6) {
            if (isOpen) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if ((this.currentActivityType == 2 || this.currentActivityType == 1) && VERSION.SDK_INT >= 21) {
            createChatAttachView();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onTransitionAnimationEnd$68$PassportActivity() {
        presentFragment(this.presentAfterAnimation, true);
        this.presentAfterAnimation = null;
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 0 || requestCode == 2) {
            createChatAttachView();
            if (this.chatAttachAlert != null) {
                this.chatAttachAlert.onActivityResultFragment(requestCode, data, this.currentPicturePath);
            }
            this.currentPicturePath = null;
        } else if (requestCode != 1) {
        } else {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            ArrayList<SendingMediaInfo> photos = new ArrayList();
            SendingMediaInfo info = new SendingMediaInfo();
            info.uri = data.getData();
            photos.add(info);
            processSelectedFiles(photos);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if ((this.currentActivityType == 1 || this.currentActivityType == 2) && this.chatAttachAlert != null) {
            if (requestCode == 17 && this.chatAttachAlert != null) {
                this.chatAttachAlert.checkCamera(false);
            } else if (requestCode == 21) {
                if (getParentActivity() != null && grantResults != null && grantResults.length != 0 && grantResults[0] != 0) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.getString("PermissionNoAudioVideo", NUM));
                    builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new PassportActivity$$Lambda$45(this));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                    builder.show();
                }
            } else if (requestCode == 19 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
                processSelectedAttach(0);
            } else if (requestCode == 22 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0 && this.scanDocumentCell != null) {
                this.scanDocumentCell.callOnClick();
            }
        } else if (this.currentActivityType == 3 && requestCode == 6) {
            startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onRequestPermissionsResultFragment$69$PassportActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveSelfArgs(Bundle args) {
        if (this.currentPicturePath != null) {
            args.putString("path", this.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.currentPicturePath = args.getString("path");
    }

    public boolean onBackPressed() {
        if (this.currentActivityType == 7) {
            this.views[this.currentViewNum].onBackPressed(true);
            for (int a = 0; a < this.views.length; a++) {
                if (this.views[a] != null) {
                    this.views[a].onDestroyActivity();
                }
            }
            return true;
        } else if (this.currentActivityType == 0 || this.currentActivityType == 5) {
            callCallback(false);
            return true;
        } else if ((this.currentActivityType == 1 || this.currentActivityType == 2) && checkDiscard()) {
            return false;
        } else {
            return true;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType == 3 && VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog.setCanCacnel(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int page, boolean animated, Bundle params) {
        if (page == 3) {
            this.doneItem.setVisibility(8);
        }
        final SlideView outView = this.views[this.currentViewNum];
        final SlideView newView = this.views[page];
        this.currentViewNum = page;
        newView.setParams(params, false);
        newView.onShow();
        if (animated) {
            newView.setTranslationX((float) AndroidUtilities.displaySize.x);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.setDuration(300);
            r3 = new Animator[2];
            r3[0] = ObjectAnimator.ofFloat(outView, "translationX", new float[]{(float) (-AndroidUtilities.displaySize.x)});
            r3[1] = ObjectAnimator.ofFloat(newView, "translationX", new float[]{0.0f});
            animatorSet.playTogether(r3);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    newView.setVisibility(0);
                }

                public void onAnimationEnd(Animator animation) {
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            animatorSet.start();
            return;
        }
        newView.setTranslationX(0.0f);
        newView.setVisibility(0);
        if (outView != newView) {
            outView.setVisibility(8);
        }
    }

    private void fillNextCodeParams(Bundle params, TL_auth_sentCode res, boolean animated) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(2, animated, params);
        } else if (res.type instanceof TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(1, animated, params);
        } else if (res.type instanceof TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(0, animated, params);
        }
    }

    private void openAttachMenu() {
        boolean z = true;
        if (getParentActivity() != null) {
            if (this.uploadingFileType != 0 || this.documents.size() < 20) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (this.uploadingFileType != 1) {
                    z = false;
                }
                chatAttachAlert.setOpenWithFrontFaceCamera(z);
                this.chatAttachAlert.setMaxSelectedPhotos(getMaxSelectedDocuments());
                this.chatAttachAlert.loadGalleryPhotos();
                if (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22) {
                    AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
                }
                this.chatAttachAlert.init();
                showDialog(this.chatAttachAlert);
                return;
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("PassportUploadMaxReached", NUM, LocaleController.formatPluralString("Files", 20)));
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            this.chatAttachAlert = new ChatAttachAlert(getParentActivity(), this);
            this.chatAttachAlert.setDelegate(new ChatAttachViewDelegate() {
                public void didPressedButton(int button) {
                    if (PassportActivity.this.getParentActivity() != null && PassportActivity.this.chatAttachAlert != null) {
                        if (button == 8 || button == 7) {
                            if (button != 8) {
                                PassportActivity.this.chatAttachAlert.dismiss();
                            }
                            HashMap<Object, Object> selectedPhotos = PassportActivity.this.chatAttachAlert.getSelectedPhotos();
                            ArrayList<Object> selectedPhotosOrder = PassportActivity.this.chatAttachAlert.getSelectedPhotosOrder();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList<SendingMediaInfo> photos = new ArrayList();
                                for (int a = 0; a < selectedPhotosOrder.size(); a++) {
                                    PhotoEntry photoEntry = (PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(a));
                                    SendingMediaInfo info = new SendingMediaInfo();
                                    if (photoEntry.imagePath != null) {
                                        info.path = photoEntry.imagePath;
                                    } else if (photoEntry.path != null) {
                                        info.path = photoEntry.path;
                                    }
                                    photos.add(info);
                                    photoEntry.reset();
                                }
                                PassportActivity.this.processSelectedFiles(photos);
                                return;
                            }
                            return;
                        }
                        if (PassportActivity.this.chatAttachAlert != null) {
                            PassportActivity.this.chatAttachAlert.dismissWithButtonClick(button);
                        }
                        PassportActivity.this.processSelectedAttach(button);
                    }
                }

                public View getRevealView() {
                    return null;
                }

                public void didSelectBot(User user) {
                }

                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(PassportActivity.this.fragmentView.findFocus());
                }

                public boolean allowGroupPhotos() {
                    return false;
                }
            });
        }
    }

    private int getMaxSelectedDocuments() {
        if (this.uploadingFileType == 0) {
            return 20 - this.documents.size();
        }
        if (this.uploadingFileType == 4) {
            return 20 - this.translationDocuments.size();
        }
        return 1;
    }

    private void processSelectedAttach(int which) {
        if (which == 0) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", image));
                            takePictureIntent.addFlags(2);
                            takePictureIntent.addFlags(1);
                        } else {
                            takePictureIntent.putExtra("output", Uri.fromFile(image));
                        }
                        this.currentPicturePath = image.getAbsolutePath();
                    }
                    startActivityForResult(takePictureIntent, 0);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
        } else if (which == 1) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(0, false, false, null);
                fragment.setCurrentAccount(this.currentAccount);
                fragment.setMaxSelectedPhotos(getMaxSelectedDocuments());
                fragment.setAllowSearchImages(false);
                fragment.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
                        PassportActivity.this.processSelectedFiles(photos);
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("image/*");
                            PassportActivity.this.startActivityForResult(photoPickerIntent, 1);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                });
                presentFragment(fragment);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        } else if (which != 4) {
        } else {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                DocumentSelectActivity fragment2 = new DocumentSelectActivity(false);
                fragment2.setCurrentAccount(this.currentAccount);
                fragment2.setCanSelectOnlyImageFiles(true);
                fragment2.setMaxSelectedFiles(getMaxSelectedDocuments());
                fragment2.setDelegate(new DocumentSelectActivityDelegate() {
                    public void startMusicSelectActivity(BaseFragment baseFragment) {
                        DocumentSelectActivity$DocumentSelectActivityDelegate$$CC.startMusicSelectActivity(this, baseFragment);
                    }

                    public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
                        activity.finishFragment();
                        ArrayList<SendingMediaInfo> arrayList = new ArrayList();
                        int count = files.size();
                        for (int a = 0; a < count; a++) {
                            SendingMediaInfo info = new SendingMediaInfo();
                            info.path = (String) files.get(a);
                            arrayList.add(info);
                        }
                        PassportActivity.this.processSelectedFiles(arrayList);
                    }

                    public void startDocumentSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                            if (VERSION.SDK_INT >= 18) {
                                photoPickerIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
                            }
                            photoPickerIntent.setType("*/*");
                            PassportActivity.this.startActivityForResult(photoPickerIntent, 21);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                });
                presentFragment(fragment2);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        }
    }

    private void fillInitialValues() {
        if (this.initialValues == null) {
            this.initialValues = getCurrentValues();
        }
    }

    private String getCurrentValues() {
        int a;
        StringBuilder values = new StringBuilder();
        for (EditTextBoldCursor text : this.inputFields) {
            values.append(text.getText()).append(",");
        }
        if (this.inputExtraFields != null) {
            for (EditTextBoldCursor text2 : this.inputExtraFields) {
                values.append(text2.getText()).append(",");
            }
        }
        int count = this.documents.size();
        for (a = 0; a < count; a++) {
            values.append(((SecureDocument) this.documents.get(a)).secureFile.id);
        }
        if (this.frontDocument != null) {
            values.append(this.frontDocument.secureFile.id);
        }
        if (this.reverseDocument != null) {
            values.append(this.reverseDocument.secureFile.id);
        }
        if (this.selfieDocument != null) {
            values.append(this.selfieDocument.secureFile.id);
        }
        count = this.translationDocuments.size();
        for (a = 0; a < count; a++) {
            values.append(((SecureDocument) this.translationDocuments.get(a)).secureFile.id);
        }
        return values.toString();
    }

    private boolean isHasNotAnyChanges() {
        return this.initialValues == null || this.initialValues.equals(getCurrentValues());
    }

    private boolean checkDiscard() {
        if (isHasNotAnyChanges()) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new PassportActivity$$Lambda$46(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("DiscardChanges", NUM));
        builder.setMessage(LocaleController.getString("PassportDiscardChanges", NUM));
        showDialog(builder.create());
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkDiscard$70$PassportActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    private void processSelectedFiles(ArrayList<SendingMediaInfo> photos) {
        if (!photos.isEmpty()) {
            boolean needRecoginze;
            if (this.uploadingFileType == 1 || this.uploadingFileType == 4) {
                needRecoginze = false;
            } else if (this.currentType.type instanceof TL_secureValueTypePersonalDetails) {
                boolean allFieldsAreEmpty = true;
                int a = 0;
                while (a < this.inputFields.length) {
                    if (a != 5 && a != 8 && a != 4 && a != 6 && this.inputFields[a].length() > 0) {
                        allFieldsAreEmpty = false;
                        break;
                    }
                    a++;
                }
                needRecoginze = allFieldsAreEmpty;
            } else {
                needRecoginze = false;
            }
            Utilities.globalQueue.postRunnable(new PassportActivity$$Lambda$47(this, photos, this.uploadingFileType, needRecoginze));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processSelectedFiles$73$PassportActivity(ArrayList photos, int type, boolean needRecoginze) {
        boolean didRecognizeSuccessfully = false;
        int i = (this.uploadingFileType == 0 || this.uploadingFileType == 4) ? 20 : 1;
        int count = Math.min(i, photos.size());
        for (int a = 0; a < count; a++) {
            SendingMediaInfo info = (SendingMediaInfo) photos.get(a);
            Bitmap bitmap = ImageLoader.loadBitmap(info.path, info.uri, 2048.0f, 2048.0f, false);
            if (bitmap != null) {
                PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, 2048.0f, 2048.0f, 89, false, 320, 320);
                if (size != null) {
                    TL_secureFile secureFile = new TL_secureFile();
                    secureFile.dc_id = (int) size.location.volume_id;
                    secureFile.id = (long) size.location.local_id;
                    secureFile.date = (int) (System.currentTimeMillis() / 1000);
                    SecureDocument document = this.delegate.saveFile(secureFile);
                    document.type = type;
                    AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$48(this, document, type));
                    if (needRecoginze && !didRecognizeSuccessfully) {
                        try {
                            Result result = MrzRecognizer.recognize(bitmap, this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense);
                            if (result != null) {
                                didRecognizeSuccessfully = true;
                                AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$49(this, result));
                            }
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                }
            }
        }
        SharedConfig.saveConfig();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$71$PassportActivity(SecureDocument document, int type) {
        SecureDocumentCell cell;
        if (this.uploadingFileType == 1) {
            if (this.selfieDocument != null) {
                cell = (SecureDocumentCell) this.documentsCells.remove(this.selfieDocument);
                if (cell != null) {
                    this.selfieLayout.removeView(cell);
                }
                this.selfieDocument = null;
            }
        } else if (this.uploadingFileType == 4) {
            if (this.translationDocuments.size() >= 20) {
                return;
            }
        } else if (this.uploadingFileType == 2) {
            if (this.frontDocument != null) {
                cell = (SecureDocumentCell) this.documentsCells.remove(this.frontDocument);
                if (cell != null) {
                    this.frontLayout.removeView(cell);
                }
                this.frontDocument = null;
            }
        } else if (this.uploadingFileType == 3) {
            if (this.reverseDocument != null) {
                cell = (SecureDocumentCell) this.documentsCells.remove(this.reverseDocument);
                if (cell != null) {
                    this.reverseLayout.removeView(cell);
                }
                this.reverseDocument = null;
            }
        } else if (this.uploadingFileType == 0 && this.documents.size() >= 20) {
            return;
        }
        this.uploadingDocuments.put(document.path, document);
        this.doneItem.setEnabled(false);
        this.doneItem.setAlpha(0.5f);
        FileLoader.getInstance(this.currentAccount).uploadFile(document.path, false, true, 16777216);
        addDocumentView(document, type);
        updateUploadText(type);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$72$PassportActivity(Result result) {
        String country;
        int count1;
        int a1;
        TL_secureRequiredType requiredType;
        if (result.type == 2) {
            if (!(this.currentDocumentsType.type instanceof TL_secureValueTypeIdentityCard)) {
                count1 = this.availableDocumentTypes.size();
                for (a1 = 0; a1 < count1; a1++) {
                    requiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(a1);
                    if (requiredType.type instanceof TL_secureValueTypeIdentityCard) {
                        this.currentDocumentsType = requiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (result.type == 1) {
            if (!(this.currentDocumentsType.type instanceof TL_secureValueTypePassport)) {
                count1 = this.availableDocumentTypes.size();
                for (a1 = 0; a1 < count1; a1++) {
                    requiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(a1);
                    if (requiredType.type instanceof TL_secureValueTypePassport) {
                        this.currentDocumentsType = requiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (result.type == 3) {
            if (!(this.currentDocumentsType.type instanceof TL_secureValueTypeInternalPassport)) {
                count1 = this.availableDocumentTypes.size();
                for (a1 = 0; a1 < count1; a1++) {
                    requiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(a1);
                    if (requiredType.type instanceof TL_secureValueTypeInternalPassport) {
                        this.currentDocumentsType = requiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (result.type == 4 && !(this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense)) {
            count1 = this.availableDocumentTypes.size();
            for (a1 = 0; a1 < count1; a1++) {
                requiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(a1);
                if (requiredType.type instanceof TL_secureValueTypeDriverLicense) {
                    this.currentDocumentsType = requiredType;
                    updateInterfaceStringsForDocumentType();
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        if (!TextUtils.isEmpty(result.number)) {
            this.inputFields[7].setText(result.number);
        }
        if (result.gender != 0) {
            switch (result.gender) {
                case 1:
                    this.currentGender = "male";
                    this.inputFields[4].setText(LocaleController.getString("PassportMale", NUM));
                    break;
                case 2:
                    this.currentGender = "female";
                    this.inputFields[4].setText(LocaleController.getString("PassportFemale", NUM));
                    break;
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            this.currentCitizeship = result.nationality;
            country = (String) this.languageMap.get(this.currentCitizeship);
            if (country != null) {
                this.inputFields[5].setText(country);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            this.currentResidence = result.issuingCountry;
            country = (String) this.languageMap.get(this.currentResidence);
            if (country != null) {
                this.inputFields[6].setText(country);
            }
        }
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
        if (result.expiryDay <= 0 || result.expiryMonth <= 0 || result.expiryYear <= 0) {
            int[] iArr = this.currentExpireDate;
            int[] iArr2 = this.currentExpireDate;
            this.currentExpireDate[2] = 0;
            iArr2[1] = 0;
            iArr[0] = 0;
            this.inputFields[8].setText(LocaleController.getString("PassportNoExpireDate", NUM));
            return;
        }
        this.currentExpireDate[0] = result.expiryYear;
        this.currentExpireDate[1] = result.expiryMonth;
        this.currentExpireDate[2] = result.expiryDay;
        this.inputFields[8].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.expiryDay), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)}));
    }

    public void setNeedActivityResult(boolean needActivityResult) {
        this.needActivityResult = needActivityResult;
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        ArrayList<ThemeDescription> arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.extraBackgroundView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        if (this.extraBackgroundView2 != null) {
            arrayList.add(new ThemeDescription(this.extraBackgroundView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        }
        for (a = 0; a < this.dividers.size(); a++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"));
        }
        for (Entry<SecureDocument, SecureDocumentCell> entry : this.documentsCells.entrySet()) {
            View cell = (SecureDocumentCell) entry.getValue();
            arrayList.add(new ThemeDescription(cell, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(cell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(cell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        }
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, null, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, null, null, null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4"));
        if (this.inputFields != null) {
            for (a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_CURSORCOLOR | ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteRedText3"));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteRedText3"));
        }
        if (this.inputExtraFields != null) {
            for (a = 0; a < this.inputExtraFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputExtraFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a], ThemeDescription.FLAG_CURSORCOLOR | ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a], ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[a], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteRedText3"));
            }
        }
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.noPasswordImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chat_messagePanelIcons"));
        arrayList.add(new ThemeDescription(this.noPasswordTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.noPasswordSetTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText5"));
        arrayList.add(new ThemeDescription(this.passwordForgotButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.plusTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.acceptTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "passport_authorizeText"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "passport_authorizeBackground"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "passport_authorizeBackgroundSelected"));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, "contextProgressOuter2"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "sessions_devicesImage"));
        arrayList.add(new ThemeDescription(this.emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.emptyTextView3, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
