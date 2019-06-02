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
import android.telephony.PhoneNumberUtils;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
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
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyEmailCode;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC.TL_account_sentEmailCode;
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
import org.telegram.tgnet.TLRPC.TL_secureCredentialsEncrypted;
import org.telegram.tgnet.TLRPC.TL_secureData;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.tgnet.TLRPC.TL_securePlainEmail;
import org.telegram.tgnet.TLRPC.TL_securePlainPhone;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf;
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
import org.telegram.tgnet.TLRPC.auth_CodeType;
import org.telegram.tgnet.TLRPC.auth_SentCodeType;
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
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate.-CC;
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

    /* renamed from: org.telegram.ui.PassportActivity$1ValueToSend */
    class AnonymousClass1ValueToSend {
        boolean selfie_required;
        boolean translation_required;
        TL_secureValue value;

        public AnonymousClass1ValueToSend(TL_secureValue tL_secureValue, boolean z, boolean z2) {
            this.value = tL_secureValue;
            this.selfie_required = z;
            this.translation_required = z2;
        }
    }

    private class EncryptionResult {
        byte[] decrypyedFileSecret;
        byte[] encryptedData;
        byte[] fileHash;
        byte[] fileSecret;
        SecureDocumentKey secureDocumentKey;

        public EncryptionResult(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6) {
            this.encryptedData = bArr;
            this.fileSecret = bArr2;
            this.fileHash = bArr4;
            this.decrypyedFileSecret = bArr3;
            this.secureDocumentKey = new SecureDocumentKey(bArr5, bArr6);
        }
    }

    private interface ErrorRunnable {
        void onError(String str, String str2);
    }

    public class LinkSpan extends ClickableSpan {
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(true);
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }

        public void onClick(View view) {
            Browser.openUrl(PassportActivity.this.getParentActivity(), PassportActivity.this.currentForm.privacy_policy_url);
        }
    }

    private interface PassportActivityDelegate {
        void deleteValue(TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, ArrayList<TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TL_secureFile tL_secureFile);

        void saveValue(TL_secureRequiredType tL_secureRequiredType, String str, String str2, TL_secureRequiredType tL_secureRequiredType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, ArrayList<SecureDocument> arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
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

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float measuredWidth = (float) ((int) (((float) getMeasuredWidth()) * this.progress));
            canvas.drawRect(0.0f, 0.0f, measuredWidth, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect(measuredWidth, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        public TextDetailSecureCell(Context context) {
            super(context);
            int i = PassportActivity.this.currentActivityType == 8 ? 21 : 51;
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            int i2 = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? i : 21), 10.0f, (float) (LocaleController.isRTL ? 21 : i), 0.0f));
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
            int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
            float f = (float) (LocaleController.isRTL ? i : 21);
            if (LocaleController.isRTL) {
                i = 21;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, i3, f, 35.0f, (float) i, 0.0f));
            this.checkImageView = new ImageView(context);
            this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
            this.checkImageView.setImageResource(NUM);
            ImageView imageView = this.checkImageView;
            if (LocaleController.isRTL) {
                i2 = 3;
            }
            addView(imageView, LayoutHelper.createFrame(-2, -2.0f, i2 | 48, 21.0f, 25.0f, 21.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
        }

        public void setTextAndValue(String str, CharSequence charSequence, boolean z) {
            this.textView.setText(str);
            this.valueTextView.setText(charSequence);
            this.needDivider = z;
            setWillNotDraw(z ^ 1);
        }

        public void setChecked(boolean z) {
            this.checkImageView.setVisibility(z ? 0 : 4);
        }

        public void setValue(CharSequence charSequence) {
            this.valueTextView.setText(charSequence);
        }

        public void setNeedDivider(boolean z) {
            this.needDivider = z;
            setWillNotDraw(this.needDivider ^ 1);
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
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
        final /* synthetic */ PassportActivity this$0;
        private int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private int verificationType;
        private boolean waitingForEvent;

        static /* synthetic */ void lambda$onBackPressed$9(TLObject tLObject, TL_error tL_error) {
        }

        public boolean needBackButton() {
            return true;
        }

        public PhoneConfirmationView(PassportActivity passportActivity, Context context, int i) {
            final PassportActivity passportActivity2 = passportActivity;
            Context context2 = context;
            this.this$0 = passportActivity2;
            super(context2);
            this.verificationType = i;
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            String str = "windowBackgroundWhiteGrayText6";
            this.confirmTextView.setTextColor(Theme.getColor(str));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView = new TextView(context2);
            String str2 = "windowBackgroundWhiteBlackText";
            this.titleTextView.setTextColor(Theme.getColor(str2));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            int i2 = 3;
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            FrameLayout frameLayout;
            if (this.verificationType == 3) {
                this.confirmTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                frameLayout = new FrameLayout(context2);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                ImageView imageView = new ImageView(context2);
                imageView.setImageResource(NUM);
                boolean z = LocaleController.isRTL;
                if (z) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, z ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
            } else {
                this.confirmTextView.setGravity(49);
                frameLayout = new FrameLayout(context2);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, 49));
                String str3 = "chats_actionBackground";
                if (this.verificationType == 1) {
                    this.blackImageView = new ImageView(context2);
                    this.blackImageView.setImageResource(NUM);
                    this.blackImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
                    frameLayout.addView(this.blackImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.blueImageView = new ImageView(context2);
                    this.blueImageView.setImageResource(NUM);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentAppCodeTitle", NUM));
                } else {
                    this.blueImageView = new ImageView(context2);
                    this.blueImageView.setImageResource(NUM);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentSmsCodeTitle", NUM));
                }
                addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 0));
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
            }
            this.codeFieldContainer = new LinearLayout(context2);
            this.codeFieldContainer.setOrientation(0);
            addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, 36, 1));
            if (this.verificationType == 3) {
                this.codeFieldContainer.setVisibility(8);
            }
            this.timeText = new TextView(context2) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.timeText.setTextColor(Theme.getColor(str));
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            if (this.verificationType == 3) {
                this.timeText.setTextSize(1, 14.0f);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                this.progressView = new ProgressView(context2);
                TextView textView = this.timeText;
                if (LocaleController.isRTL) {
                    i2 = 5;
                }
                textView.setGravity(i2);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            } else {
                this.timeText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
                this.timeText.setTextSize(1, 15.0f);
                this.timeText.setGravity(49);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, 49));
            }
            this.problemText = new TextView(context2) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
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
            this.problemText.setOnClickListener(new -$$Lambda$PassportActivity$PhoneConfirmationView$PSs1EP1O5Wgd6q0a05fTJgjQt4s(this));
        }

        public /* synthetic */ void lambda$new$0$PassportActivity$PhoneConfirmationView(View view) {
            if (!this.nextPressed) {
                Object obj = ((this.nextType == 4 && this.verificationType == 2) || this.nextType == 0) ? 1 : null;
                if (obj == null) {
                    resendCode();
                } else {
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("message/rfCLASSNAME");
                        intent.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Android registration/login issue ");
                        stringBuilder.append(format);
                        stringBuilder.append(" ");
                        stringBuilder.append(this.phone);
                        intent.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Phone: ");
                        stringBuilder.append(this.phone);
                        stringBuilder.append("\nApp version: ");
                        stringBuilder.append(format);
                        stringBuilder.append("\nOS version: SDK ");
                        stringBuilder.append(VERSION.SDK_INT);
                        stringBuilder.append("\nDevice Name: ");
                        stringBuilder.append(Build.MANUFACTURER);
                        stringBuilder.append(Build.MODEL);
                        stringBuilder.append("\nLocale: ");
                        stringBuilder.append(Locale.getDefault());
                        stringBuilder.append("\nError: ");
                        stringBuilder.append(this.lastError);
                        intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        getContext().startActivity(Intent.createChooser(intent, "Send email..."));
                    } catch (Exception unused) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("NoMailInstalled", NUM));
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (this.verificationType != 3) {
                ImageView imageView = this.blueImageView;
                if (imageView != null) {
                    i = ((imageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight()) + this.confirmTextView.getMeasuredHeight()) + AndroidUtilities.dp(35.0f);
                    i2 = AndroidUtilities.dp(80.0f);
                    int dp = AndroidUtilities.dp(291.0f);
                    if (this.this$0.scrollHeight - i < i2) {
                        setMeasuredDimension(getMeasuredWidth(), i + i2);
                    } else if (this.this$0.scrollHeight > dp) {
                        setMeasuredDimension(getMeasuredWidth(), dp);
                    } else {
                        setMeasuredDimension(getMeasuredWidth(), this.this$0.scrollHeight);
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (this.verificationType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                i = getMeasuredHeight() - bottom;
                TextView textView;
                if (this.problemText.getVisibility() == 0) {
                    i2 = this.problemText.getMeasuredHeight();
                    i = (i + bottom) - i2;
                    textView = this.problemText;
                    textView.layout(textView.getLeft(), i, this.problemText.getRight(), i2 + i);
                } else if (this.timeText.getVisibility() == 0) {
                    i2 = this.timeText.getMeasuredHeight();
                    i = (i + bottom) - i2;
                    textView = this.timeText;
                    textView.layout(textView.getLeft(), i, this.timeText.getRight(), i2 + i);
                } else {
                    i += bottom;
                }
                i -= bottom;
                i2 = this.codeFieldContainer.getMeasuredHeight();
                i = ((i - i2) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), i, this.codeFieldContainer.getRight(), i2 + i);
            }
        }

        private void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TL_auth_resendCode tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.phone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new -$$Lambda$PassportActivity$PhoneConfirmationView$1NsJYQaZadbCmSfsr4dWklizb2g(this, bundle, tL_auth_resendCode), 2);
        }

        public /* synthetic */ void lambda$resendCode$3$PassportActivity$PhoneConfirmationView(Bundle bundle, TL_auth_resendCode tL_auth_resendCode, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$PhoneConfirmationView$UHXDno5AXqEJ78FjOAmHbJDCDI4(this, tL_error, bundle, tLObject, tL_auth_resendCode));
        }

        public /* synthetic */ void lambda$null$2$PassportActivity$PhoneConfirmationView(TL_error tL_error, Bundle bundle, TLObject tLObject, TL_auth_resendCode tL_auth_resendCode) {
            this.nextPressed = false;
            if (tL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject, true);
            } else {
                AlertDialog alertDialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, tL_error, this.this$0, tL_auth_resendCode, new Object[0]);
                if (alertDialog != null && tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                    alertDialog.setPositiveButtonListener(new -$$Lambda$PassportActivity$PhoneConfirmationView$igbayS6dw7tSDYDaiFu83_vyLoc(this));
                }
            }
            this.this$0.needHideProgress();
        }

        public /* synthetic */ void lambda$null$1$PassportActivity$PhoneConfirmationView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.finishFragment();
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle bundle, boolean z) {
            Bundle bundle2 = bundle;
            if (bundle2 != null) {
                int i;
                this.waitingForEvent = true;
                int i2 = this.verificationType;
                if (i2 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle2;
                this.phone = bundle2.getString("phone");
                this.phoneHash = bundle2.getString("phoneHash");
                i2 = bundle2.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle2.getInt("nextType");
                this.pattern = bundle2.getString("pattern");
                this.length = bundle2.getInt("length");
                if (this.length == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                CharSequence charSequence = "";
                int i3 = 8;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    i = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (i >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[i].setText(charSequence);
                        i++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    i = 0;
                    while (i < this.length) {
                        this.codeField[i] = new EditTextBoldCursor(getContext());
                        String str = "windowBackgroundWhiteBlackText";
                        this.codeField[i].setTextColor(Theme.getColor(str));
                        this.codeField[i].setCursorColor(Theme.getColor(str));
                        this.codeField[i].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[i].setCursorWidth(1.5f);
                        Drawable mutate = getResources().getDrawable(NUM).mutate();
                        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Mode.MULTIPLY));
                        this.codeField[i].setBackgroundDrawable(mutate);
                        this.codeField[i].setImeOptions(NUM);
                        this.codeField[i].setTextSize(1, 20.0f);
                        this.codeField[i].setMaxLines(1);
                        this.codeField[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[i].setPadding(0, 0, 0, 0);
                        this.codeField[i].setGravity(49);
                        if (this.verificationType == 3) {
                            this.codeField[i].setEnabled(false);
                            this.codeField[i].setInputType(0);
                            this.codeField[i].setVisibility(8);
                        } else {
                            this.codeField[i].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[i], LayoutHelper.createLinear(34, 36, 1, 0, 0, i != this.length - 1 ? 7 : 0, 0));
                        this.codeField[i].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void afterTextChanged(Editable editable) {
                                if (!PhoneConfirmationView.this.ignoreOnTextChange) {
                                    int length = editable.length();
                                    if (length >= 1) {
                                        if (length > 1) {
                                            String obj = editable.toString();
                                            PhoneConfirmationView.this.ignoreOnTextChange = true;
                                            for (int i = 0; i < Math.min(PhoneConfirmationView.this.length - i, length); i++) {
                                                if (i == 0) {
                                                    editable.replace(0, length, obj.substring(i, i + 1));
                                                } else {
                                                    PhoneConfirmationView.this.codeField[i + i].setText(obj.substring(i, i + 1));
                                                }
                                            }
                                            PhoneConfirmationView.this.ignoreOnTextChange = false;
                                        }
                                        if (i != PhoneConfirmationView.this.length - 1) {
                                            PhoneConfirmationView.this.codeField[i + 1].setSelection(PhoneConfirmationView.this.codeField[i + 1].length());
                                            PhoneConfirmationView.this.codeField[i + 1].requestFocus();
                                        }
                                        if ((i == PhoneConfirmationView.this.length - 1 || (i == PhoneConfirmationView.this.length - 2 && length >= 2)) && PhoneConfirmationView.this.getCode().length() == PhoneConfirmationView.this.length) {
                                            PhoneConfirmationView.this.onNextPressed();
                                        }
                                    }
                                }
                            }
                        });
                        this.codeField[i].setOnKeyListener(new -$$Lambda$PassportActivity$PhoneConfirmationView$Hi5vGNsckWBWmshMz8y5BhPDMNw(this, i));
                        this.codeField[i].setOnEditorActionListener(new -$$Lambda$PassportActivity$PhoneConfirmationView$IfZlFmJVSS8IJyDQ8zJhpbvhePs(this));
                        i++;
                    }
                }
                ProgressView progressView = this.progressView;
                if (progressView != null) {
                    progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    TextView textView;
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(this.phone);
                    String format = instance.format(stringBuilder.toString());
                    int i4 = this.verificationType;
                    if (i4 == 2) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(format)));
                    } else if (i4 == 3) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(format)));
                    } else if (i4 == 4) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(format)));
                    }
                    this.confirmTextView.setText(charSequence);
                    if (this.verificationType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    String str2 = "SmsText";
                    String str3 = "CallText";
                    if (this.verificationType == 3) {
                        i = this.nextType;
                        if (i == 4 || i == 2) {
                            this.problemText.setVisibility(8);
                            this.timeText.setVisibility(0);
                            i = this.nextType;
                            if (i == 4) {
                                this.timeText.setText(LocaleController.formatString(str3, NUM, Integer.valueOf(1), Integer.valueOf(0)));
                            } else if (i == 2) {
                                this.timeText.setText(LocaleController.formatString(str2, NUM, Integer.valueOf(1), Integer.valueOf(0)));
                            }
                            createTimer();
                        }
                    }
                    if (this.verificationType == 2) {
                        i = this.nextType;
                        if (i == 4 || i == 3) {
                            this.timeText.setText(LocaleController.formatString(str3, NUM, Integer.valueOf(2), Integer.valueOf(0)));
                            this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                            textView = this.timeText;
                            if (this.time >= 1000) {
                                i3 = 0;
                            }
                            textView.setVisibility(i3);
                            createTimer();
                        }
                    }
                    if (this.verificationType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString(str2, NUM, Integer.valueOf(2), Integer.valueOf(0)));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        textView = this.timeText;
                        if (this.time >= 1000) {
                            i3 = 0;
                        }
                        textView.setVisibility(i3);
                        createTimer();
                    }
                    this.timeText.setVisibility(8);
                    this.problemText.setVisibility(8);
                    createCodeTimer();
                }
            }
        }

        public /* synthetic */ boolean lambda$setParams$4$PassportActivity$PhoneConfirmationView(int i, View view, int i2, KeyEvent keyEvent) {
            if (i2 != 67 || this.codeField[i].length() != 0 || i <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            i--;
            editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            this.codeField[i].requestFocus();
            this.codeField[i].dispatchKeyEvent(keyEvent);
            return true;
        }

        public /* synthetic */ boolean lambda$setParams$5$PassportActivity$PhoneConfirmationView(TextView textView, int i, KeyEvent keyEvent) {
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
                        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$PhoneConfirmationView$4$4TMBClZCqBNuy-tFpKG40rvRb80(this));
                    }

                    public /* synthetic */ void lambda$run$0$PassportActivity$PhoneConfirmationView$4() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$9900 = PhoneConfirmationView.this.lastCodeTime;
                        Double.isNaN(currentTimeMillis);
                        access$9900 = currentTimeMillis - access$9900;
                        PhoneConfirmationView.this.lastCodeTime = currentTimeMillis;
                        PhoneConfirmationView phoneConfirmationView = PhoneConfirmationView.this;
                        double access$10000 = (double) phoneConfirmationView.codeTime;
                        Double.isNaN(access$10000);
                        phoneConfirmationView.codeTime = (int) (access$10000 - access$9900);
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
                            double currentTimeMillis = (double) System.currentTimeMillis();
                            double access$10500 = PhoneConfirmationView.this.lastCurrentTime;
                            Double.isNaN(currentTimeMillis);
                            access$10500 = currentTimeMillis - access$10500;
                            PhoneConfirmationView phoneConfirmationView = PhoneConfirmationView.this;
                            double access$10600 = (double) phoneConfirmationView.time;
                            Double.isNaN(access$10600);
                            phoneConfirmationView.time = (int) (access$10600 - access$10500);
                            PhoneConfirmationView.this.lastCurrentTime = currentTimeMillis;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (PhoneConfirmationView.this.time >= 1000) {
                                        int access$10600 = (PhoneConfirmationView.this.time / 1000) - (((PhoneConfirmationView.this.time / 1000) / 60) * 60);
                                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3) {
                                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(r0), Integer.valueOf(access$10600)));
                                        } else if (PhoneConfirmationView.this.nextType == 2) {
                                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(r0), Integer.valueOf(access$10600)));
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
                                            TL_auth_resendCode tL_auth_resendCode = new TL_auth_resendCode();
                                            tL_auth_resendCode.phone_number = PhoneConfirmationView.this.phone;
                                            tL_auth_resendCode.phone_code_hash = PhoneConfirmationView.this.phoneHash;
                                            ConnectionsManager.getInstance(PhoneConfirmationView.this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new -$$Lambda$PassportActivity$PhoneConfirmationView$5$1$Q-utOxW1QEIU96NoSBjX04lpg8k(this), 2);
                                        } else if (PhoneConfirmationView.this.nextType == 3) {
                                            AndroidUtilities.setWaitingForSms(false);
                                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                            PhoneConfirmationView.this.waitingForEvent = false;
                                            PhoneConfirmationView.this.destroyCodeTimer();
                                            PhoneConfirmationView.this.resendCode();
                                        }
                                    }
                                }

                                public /* synthetic */ void lambda$run$1$PassportActivity$PhoneConfirmationView$5$1(TLObject tLObject, TL_error tL_error) {
                                    if (tL_error != null && tL_error.text != null) {
                                        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$PhoneConfirmationView$5$1$-XxiWYM9U-4LvqFMJJ2CZp8aBO0(this, tL_error));
                                    }
                                }

                                public /* synthetic */ void lambda$null$0$PassportActivity$PhoneConfirmationView$5$1(TL_error tL_error) {
                                    PhoneConfirmationView.this.lastError = tL_error.text;
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
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    return stringBuilder.toString();
                }
                stringBuilder.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[i].getText().toString()));
                i++;
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String code = getCode();
                if (TextUtils.isEmpty(code)) {
                    AndroidUtilities.shakeView(this.codeFieldContainer, 2.0f, 0);
                    return;
                }
                this.nextPressed = true;
                int i = this.verificationType;
                if (i == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                this.this$0.showEditDoneProgress(true, true);
                TL_account_verifyPhone tL_account_verifyPhone = new TL_account_verifyPhone();
                tL_account_verifyPhone.phone_number = this.phone;
                tL_account_verifyPhone.phone_code = code;
                tL_account_verifyPhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_verifyPhone, new -$$Lambda$PassportActivity$PhoneConfirmationView$H7W-zFejxtN_G3EXdQMEm98R9oE(this, tL_account_verifyPhone), 2);
            }
        }

        public /* synthetic */ void lambda$onNextPressed$7$PassportActivity$PhoneConfirmationView(TL_account_verifyPhone tL_account_verifyPhone, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$PhoneConfirmationView$pKHVwoJ6geCuurl3AWwR7b5outU(this, tL_error, tL_account_verifyPhone));
        }

        /* JADX WARNING: Removed duplicated region for block: B:21:0x0078  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x006b  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x008c  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00d7 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00cd A:{LOOP_END, LOOP:0: B:34:0x00c8->B:36:0x00cd} */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x006b  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0078  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x008c  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00a8  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00cd A:{LOOP_END, LOOP:0: B:34:0x00c8->B:36:0x00cd} */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00d7 A:{SYNTHETIC} */
        /* JADX WARNING: Missing block: B:7:0x004f, code skipped:
            if (r1 != 2) goto L_0x0051;
     */
        /* JADX WARNING: Missing block: B:12:0x0059, code skipped:
            if (r1 != 3) goto L_0x005b;
     */
        /* JADX WARNING: Missing block: B:16:0x0061, code skipped:
            if (r13.nextType == 2) goto L_0x0063;
     */
        public /* synthetic */ void lambda$null$6$PassportActivity$PhoneConfirmationView(org.telegram.tgnet.TLRPC.TL_error r14, org.telegram.tgnet.TLRPC.TL_account_verifyPhone r15) {
            /*
            r13 = this;
            r0 = r13.this$0;
            r0.needHideProgress();
            r0 = 0;
            r13.nextPressed = r0;
            if (r14 != 0) goto L_0x0040;
        L_0x000a:
            r13.destroyTimer();
            r13.destroyCodeTimer();
            r14 = r13.this$0;
            r0 = r14.delegate;
            r14 = r13.this$0;
            r1 = r14.currentType;
            r14 = r13.this$0;
            r14 = r14.currentValues;
            r15 = "phone";
            r14 = r14.get(r15);
            r2 = r14;
            r2 = (java.lang.String) r2;
            r3 = 0;
            r4 = 0;
            r5 = 0;
            r6 = 0;
            r7 = 0;
            r8 = 0;
            r9 = 0;
            r10 = 0;
            r14 = r13.this$0;
            r11 = new org.telegram.ui.-$$Lambda$3CKII8dAtyIA-WQAJjP7Ab16CU8;
            r11.<init>(r14);
            r12 = 0;
            r0.saveValue(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);
            goto L_0x00dc;
        L_0x0040:
            r1 = r14.text;
            r13.lastError = r1;
            r1 = r13.verificationType;
            r2 = 4;
            r3 = 2;
            r4 = 3;
            if (r1 != r4) goto L_0x0051;
        L_0x004b:
            r1 = r13.nextType;
            if (r1 == r2) goto L_0x0063;
        L_0x004f:
            if (r1 == r3) goto L_0x0063;
        L_0x0051:
            r1 = r13.verificationType;
            if (r1 != r3) goto L_0x005b;
        L_0x0055:
            r1 = r13.nextType;
            if (r1 == r2) goto L_0x0063;
        L_0x0059:
            if (r1 == r4) goto L_0x0063;
        L_0x005b:
            r1 = r13.verificationType;
            if (r1 != r2) goto L_0x0066;
        L_0x005f:
            r1 = r13.nextType;
            if (r1 != r3) goto L_0x0066;
        L_0x0063:
            r13.createTimer();
        L_0x0066:
            r1 = r13.verificationType;
            r2 = 1;
            if (r1 != r3) goto L_0x0078;
        L_0x006b:
            org.telegram.messenger.AndroidUtilities.setWaitingForSms(r2);
            r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r3 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
            r1.addObserver(r13, r3);
            goto L_0x0086;
        L_0x0078:
            if (r1 != r4) goto L_0x0086;
        L_0x007a:
            org.telegram.messenger.AndroidUtilities.setWaitingForCall(r2);
            r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r3 = org.telegram.messenger.NotificationCenter.didReceiveCall;
            r1.addObserver(r13, r3);
        L_0x0086:
            r13.waitingForEvent = r2;
            r1 = r13.verificationType;
            if (r1 == r4) goto L_0x0099;
        L_0x008c:
            r1 = r13.this$0;
            r1 = r1.currentAccount;
            r3 = r13.this$0;
            r4 = new java.lang.Object[r0];
            org.telegram.ui.Components.AlertsCreator.processError(r1, r14, r3, r15, r4);
        L_0x0099:
            r15 = r13.this$0;
            r15.showEditDoneProgress(r2, r0);
            r15 = r14.text;
            r1 = "PHONE_CODE_EMPTY";
            r15 = r15.contains(r1);
            if (r15 != 0) goto L_0x00c7;
        L_0x00a8:
            r15 = r14.text;
            r1 = "PHONE_CODE_INVALID";
            r15 = r15.contains(r1);
            if (r15 == 0) goto L_0x00b3;
        L_0x00b2:
            goto L_0x00c7;
        L_0x00b3:
            r14 = r14.text;
            r15 = "PHONE_CODE_EXPIRED";
            r14 = r14.contains(r15);
            if (r14 == 0) goto L_0x00dc;
        L_0x00bd:
            r13.onBackPressed(r2);
            r14 = r13.this$0;
            r15 = 0;
            r14.setPage(r0, r2, r15);
            goto L_0x00dc;
        L_0x00c7:
            r14 = 0;
        L_0x00c8:
            r15 = r13.codeField;
            r1 = r15.length;
            if (r14 >= r1) goto L_0x00d7;
        L_0x00cd:
            r15 = r15[r14];
            r1 = "";
            r15.setText(r1);
            r14 = r14 + 1;
            goto L_0x00c8;
        L_0x00d7:
            r14 = r15[r0];
            r14.requestFocus();
        L_0x00dc:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity$PhoneConfirmationView.lambda$null$6$PassportActivity$PhoneConfirmationView(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$TL_account_verifyPhone):void");
        }

        public boolean onBackPressed(boolean z) {
            if (z) {
                TL_auth_cancelCode tL_auth_cancelCode = new TL_auth_cancelCode();
                tL_auth_cancelCode.phone_number = this.phone;
                tL_auth_cancelCode.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_cancelCode, -$$Lambda$PassportActivity$PhoneConfirmationView$V0ORURFAuRbPRy3yre_3sf3Qzvs.INSTANCE, 2);
                destroyTimer();
                destroyCodeTimer();
                this.currentParams = null;
                int i = this.verificationType;
                if (i == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                return true;
            }
            Builder builder = new Builder(this.this$0.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopVerification", NUM));
            builder.setPositiveButton(LocaleController.getString("Continue", NUM), null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new -$$Lambda$PassportActivity$PhoneConfirmationView$WGkFAmcyRqIzKtaNtQuEsZZn98M(this));
            this.this$0.showDialog(builder.create());
            return false;
        }

        public /* synthetic */ void lambda$onBackPressed$8$PassportActivity$PhoneConfirmationView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, null);
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            int i = this.verificationType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        public void onShow() {
            super.onShow();
            LinearLayout linearLayout = this.codeFieldContainer;
            if (linearLayout != null && linearLayout.getVisibility() == 0) {
                int length = this.codeField.length - 1;
                while (length >= 0) {
                    if (length == 0 || this.codeField[length].length() != 0) {
                        this.codeField[length].requestFocus();
                        EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                        editTextBoldCursorArr[length].setSelection(editTextBoldCursorArr[length].length());
                        AndroidUtilities.showKeyboard(this.codeField[length]);
                        return;
                    }
                    length--;
                }
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (this.waitingForEvent) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr != null) {
                    String str = "";
                    if (i == NotificationCenter.didReceiveSmsCode) {
                        EditText editText = editTextBoldCursorArr[0];
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(objArr[0]);
                        editText.setText(stringBuilder.toString());
                        onNextPressed();
                    } else if (i == NotificationCenter.didReceiveCall) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str);
                        stringBuilder2.append(objArr[0]);
                        String stringBuilder3 = stringBuilder2.toString();
                        if (AndroidUtilities.checkPhonePattern(this.pattern, stringBuilder3)) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(stringBuilder3);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        }
                    }
                }
            }
        }
    }

    public class SecureDocumentCell extends FrameLayout implements FileDownloadProgressListener {
        private int TAG;
        private int buttonState;
        private SecureDocument currentSecureDocument;
        private BackupImageView imageView;
        private RadialProgress radialProgress = new RadialProgress(this);
        private TextView textView;
        private TextView valueTextView;

        public SecureDocumentCell(Context context) {
            super(context);
            this.TAG = DownloadController.getInstance(PassportActivity.this.currentAccount).generateObserverTag();
            this.imageView = new BackupImageView(context);
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 8.0f, 21.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            int i2 = 21;
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 21 : 81), 10.0f, (float) (LocaleController.isRTL ? 81 : 21), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            TextView textView = this.valueTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i3 = i | 48;
            float f = (float) (LocaleController.isRTL ? 21 : 81);
            if (LocaleController.isRTL) {
                i2 = 81;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, i3, f, 35.0f, (float) i2, 0.0f));
            setWillNotDraw(false);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, NUM));
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int left = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            i = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(left, i, AndroidUtilities.dp(24.0f) + left, AndroidUtilities.dp(24.0f) + i);
        }

        /* Access modifiers changed, original: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            boolean drawChild = super.drawChild(canvas, view, j);
            if (view == this.imageView) {
                this.radialProgress.draw(canvas);
            }
            return drawChild;
        }

        public void setTextAndValueAndImage(String str, CharSequence charSequence, SecureDocument secureDocument) {
            this.textView.setText(str);
            this.valueTextView.setText(charSequence);
            this.imageView.setImage(secureDocument, "48_48");
            this.currentSecureDocument = secureDocument;
            updateButtonState(false);
        }

        public void setValue(CharSequence charSequence) {
            this.valueTextView.setText(charSequence);
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentSecureDocument);
            boolean exists = FileLoader.getPathToAttach(this.currentSecureDocument).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setBackground(null, false, false);
                return;
            }
            SecureDocument secureDocument = this.currentSecureDocument;
            float f = 0.0f;
            Float fileProgress;
            if (secureDocument.path != null) {
                if (secureDocument.inputFile != null) {
                    DownloadController.getInstance(PassportActivity.this.currentAccount).removeLoadingFileObserver(this);
                    this.radialProgress.setBackground(null, false, z);
                    this.buttonState = -1;
                } else {
                    DownloadController.getInstance(PassportActivity.this.currentAccount).addLoadingFileObserver(this.currentSecureDocument.path, this);
                    this.buttonState = 1;
                    fileProgress = ImageLoader.getInstance().getFileProgress(this.currentSecureDocument.path);
                    this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, z);
                    RadialProgress radialProgress = this.radialProgress;
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                    radialProgress.setProgress(f, false);
                    invalidate();
                }
            } else if (exists) {
                DownloadController.getInstance(PassportActivity.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground(null, false, z);
                invalidate();
            } else {
                DownloadController.getInstance(PassportActivity.this.currentAccount).addLoadingFileObserver(attachFileName, this);
                this.buttonState = 1;
                fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, z);
                RadialProgress radialProgress2 = this.radialProgress;
                if (fileProgress != null) {
                    f = fileProgress.floatValue();
                }
                radialProgress2.setProgress(f, z);
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

        public void onFailedDownload(String str, boolean z) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        public void onProgressUpload(String str, float f, boolean z) {
            this.radialProgress.setProgress(f, true);
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    public PassportActivity(int i, int i2, String str, String str2, String str3, String str4, String str5, TL_account_authorizationForm tL_account_authorizationForm, TL_account_password tL_account_password) {
        TL_account_authorizationForm tL_account_authorizationForm2 = tL_account_authorizationForm;
        this(i, tL_account_authorizationForm, tL_account_password, null, null, null, null, null, null);
        this.currentBotId = i2;
        this.currentPayload = str3;
        this.currentNonce = str4;
        this.currentScope = str;
        this.currentPublicKey = str2;
        this.currentCallbackUrl = str5;
        if (i == 0 && !tL_account_authorizationForm2.errors.isEmpty()) {
            try {
                Collections.sort(tL_account_authorizationForm2.errors, new Comparator<SecureValueError>() {
                    /* Access modifiers changed, original: 0000 */
                    public int getErrorValue(SecureValueError secureValueError) {
                        if (secureValueError instanceof TL_secureValueError) {
                            return 0;
                        }
                        if (secureValueError instanceof TL_secureValueErrorFrontSide) {
                            return 1;
                        }
                        if (secureValueError instanceof TL_secureValueErrorReverseSide) {
                            return 2;
                        }
                        if (secureValueError instanceof TL_secureValueErrorSelfie) {
                            return 3;
                        }
                        if (secureValueError instanceof TL_secureValueErrorTranslationFile) {
                            return 4;
                        }
                        if (secureValueError instanceof TL_secureValueErrorTranslationFiles) {
                            return 5;
                        }
                        if (secureValueError instanceof TL_secureValueErrorFile) {
                            return 6;
                        }
                        if (secureValueError instanceof TL_secureValueErrorFiles) {
                            return 7;
                        }
                        if (!(secureValueError instanceof TL_secureValueErrorData)) {
                            return 100;
                        }
                        return PassportActivity.this.getFieldCost(((TL_secureValueErrorData) secureValueError).field);
                    }

                    public int compare(SecureValueError secureValueError, SecureValueError secureValueError2) {
                        int errorValue = getErrorValue(secureValueError);
                        int errorValue2 = getErrorValue(secureValueError2);
                        if (errorValue < errorValue2) {
                            return -1;
                        }
                        return errorValue > errorValue2 ? 1 : 0;
                    }
                });
                int size = tL_account_authorizationForm2.errors.size();
                int i3 = 0;
                while (i3 < size) {
                    Object nameForType;
                    Object obj;
                    byte[] bArr;
                    Object obj2;
                    SecureValueError secureValueError = (SecureValueError) tL_account_authorizationForm2.errors.get(i3);
                    String str6 = "data";
                    Object obj3 = null;
                    String str7 = "error_all";
                    String str8 = "selfie";
                    String str9 = "reverse";
                    String str10 = "front";
                    String str11 = "files";
                    String str12 = "translation";
                    if (secureValueError instanceof TL_secureValueErrorFrontSide) {
                        TL_secureValueErrorFrontSide tL_secureValueErrorFrontSide = (TL_secureValueErrorFrontSide) secureValueError;
                        nameForType = getNameForType(tL_secureValueErrorFrontSide.type);
                        obj = tL_secureValueErrorFrontSide.text;
                        bArr = tL_secureValueErrorFrontSide.file_hash;
                        i2 = size;
                        obj2 = str10;
                    } else if (secureValueError instanceof TL_secureValueErrorReverseSide) {
                        TL_secureValueErrorReverseSide tL_secureValueErrorReverseSide = (TL_secureValueErrorReverseSide) secureValueError;
                        nameForType = getNameForType(tL_secureValueErrorReverseSide.type);
                        obj = tL_secureValueErrorReverseSide.text;
                        bArr = tL_secureValueErrorReverseSide.file_hash;
                        i2 = size;
                        obj2 = str9;
                    } else if (secureValueError instanceof TL_secureValueErrorSelfie) {
                        TL_secureValueErrorSelfie tL_secureValueErrorSelfie = (TL_secureValueErrorSelfie) secureValueError;
                        nameForType = getNameForType(tL_secureValueErrorSelfie.type);
                        obj = tL_secureValueErrorSelfie.text;
                        bArr = tL_secureValueErrorSelfie.file_hash;
                        i2 = size;
                        obj2 = str8;
                    } else {
                        if (secureValueError instanceof TL_secureValueErrorTranslationFile) {
                            TL_secureValueErrorTranslationFile tL_secureValueErrorTranslationFile = (TL_secureValueErrorTranslationFile) secureValueError;
                            nameForType = getNameForType(tL_secureValueErrorTranslationFile.type);
                            obj = tL_secureValueErrorTranslationFile.text;
                            bArr = tL_secureValueErrorTranslationFile.file_hash;
                            i2 = size;
                        } else if (secureValueError instanceof TL_secureValueErrorTranslationFiles) {
                            TL_secureValueErrorTranslationFiles tL_secureValueErrorTranslationFiles = (TL_secureValueErrorTranslationFiles) secureValueError;
                            nameForType = getNameForType(tL_secureValueErrorTranslationFiles.type);
                            obj = tL_secureValueErrorTranslationFiles.text;
                            i2 = size;
                            bArr = null;
                        } else {
                            if (secureValueError instanceof TL_secureValueErrorFile) {
                                TL_secureValueErrorFile tL_secureValueErrorFile = (TL_secureValueErrorFile) secureValueError;
                                nameForType = getNameForType(tL_secureValueErrorFile.type);
                                obj = tL_secureValueErrorFile.text;
                                bArr = tL_secureValueErrorFile.file_hash;
                                i2 = size;
                            } else if (secureValueError instanceof TL_secureValueErrorFiles) {
                                TL_secureValueErrorFiles tL_secureValueErrorFiles = (TL_secureValueErrorFiles) secureValueError;
                                nameForType = getNameForType(tL_secureValueErrorFiles.type);
                                obj = tL_secureValueErrorFiles.text;
                                i2 = size;
                                bArr = null;
                            } else if (secureValueError instanceof TL_secureValueError) {
                                TL_secureValueError tL_secureValueError = (TL_secureValueError) secureValueError;
                                nameForType = getNameForType(tL_secureValueError.type);
                                obj = tL_secureValueError.text;
                                bArr = tL_secureValueError.hash;
                                i2 = size;
                                obj2 = str7;
                            } else {
                                if (secureValueError instanceof TL_secureValueErrorData) {
                                    TL_secureValueErrorData tL_secureValueErrorData = (TL_secureValueErrorData) secureValueError;
                                    for (int i4 = 0; i4 < tL_account_authorizationForm2.values.size(); i4++) {
                                        TL_secureValue tL_secureValue = (TL_secureValue) tL_account_authorizationForm2.values.get(i4);
                                        if (tL_secureValue.data != null && Arrays.equals(tL_secureValue.data.data_hash, tL_secureValueErrorData.data_hash)) {
                                            nameForType = 1;
                                            break;
                                        }
                                    }
                                    nameForType = null;
                                    if (nameForType != null) {
                                        nameForType = getNameForType(tL_secureValueErrorData.type);
                                        obj = tL_secureValueErrorData.text;
                                        obj3 = tL_secureValueErrorData.field;
                                        bArr = tL_secureValueErrorData.data_hash;
                                        i2 = size;
                                        obj2 = str6;
                                    }
                                }
                                i2 = size;
                                i3++;
                                size = i2;
                                tL_account_authorizationForm2 = tL_account_authorizationForm;
                            }
                            obj2 = str11;
                        }
                        obj2 = str12;
                    }
                    HashMap hashMap = (HashMap) this.errorsMap.get(nameForType);
                    if (hashMap == null) {
                        hashMap = new HashMap();
                        this.errorsMap.put(nameForType, hashMap);
                        this.mainErrorsMap.put(nameForType, obj);
                    }
                    String encodeToString = bArr != null ? Base64.encodeToString(bArr, 2) : "";
                    StringBuilder stringBuilder;
                    if (str6.equals(obj2)) {
                        if (obj3 != null) {
                            hashMap.put(obj3, obj);
                        }
                    } else if (str11.equals(obj2)) {
                        if (bArr != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str11);
                            stringBuilder.append(encodeToString);
                            hashMap.put(stringBuilder.toString(), obj);
                        } else {
                            hashMap.put("files_all", obj);
                        }
                    } else if (str8.equals(obj2)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str8);
                        stringBuilder.append(encodeToString);
                        hashMap.put(stringBuilder.toString(), obj);
                    } else if (str12.equals(obj2)) {
                        if (bArr != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str12);
                            stringBuilder.append(encodeToString);
                            hashMap.put(stringBuilder.toString(), obj);
                        } else {
                            hashMap.put("translation_all", obj);
                        }
                    } else if (str10.equals(obj2)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str10);
                        stringBuilder.append(encodeToString);
                        hashMap.put(stringBuilder.toString(), obj);
                    } else if (str9.equals(obj2)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str9);
                        stringBuilder.append(encodeToString);
                        hashMap.put(stringBuilder.toString(), obj);
                    } else if (str7.equals(obj2)) {
                        hashMap.put(str7, obj);
                    }
                    i3++;
                    size = i2;
                    tL_account_authorizationForm2 = tL_account_authorizationForm;
                }
            } catch (Exception unused) {
            }
        }
    }

    public PassportActivity(int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_password tL_account_password, TL_secureRequiredType tL_secureRequiredType, TL_secureValue tL_secureValue, TL_secureRequiredType tL_secureRequiredType2, TL_secureValue tL_secureValue2, HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
        String str = "";
        this.currentCitizeship = str;
        this.currentResidence = str;
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
            public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i, boolean z) {
                if (i < 0 || i >= PassportActivity.this.currentPhotoViewerLayout.getChildCount()) {
                    return null;
                }
                SecureDocumentCell secureDocumentCell = (SecureDocumentCell) PassportActivity.this.currentPhotoViewerLayout.getChildAt(i);
                int[] iArr = new int[2];
                secureDocumentCell.imageView.getLocationInWindow(iArr);
                PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                int i2 = 0;
                placeProviderObject.viewX = iArr[0];
                int i3 = iArr[1];
                if (VERSION.SDK_INT < 21) {
                    i2 = AndroidUtilities.statusBarHeight;
                }
                placeProviderObject.viewY = i3 - i2;
                placeProviderObject.parentView = PassportActivity.this.currentPhotoViewerLayout;
                placeProviderObject.imageReceiver = secureDocumentCell.imageView.getImageReceiver();
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                return placeProviderObject;
            }

            public void deleteImageAtIndex(int i) {
                Object access$300;
                if (PassportActivity.this.uploadingFileType == 1) {
                    access$300 = PassportActivity.this.selfieDocument;
                } else if (PassportActivity.this.uploadingFileType == 4) {
                    access$300 = (SecureDocument) PassportActivity.this.translationDocuments.get(i);
                } else if (PassportActivity.this.uploadingFileType == 2) {
                    access$300 = PassportActivity.this.frontDocument;
                } else if (PassportActivity.this.uploadingFileType == 3) {
                    access$300 = PassportActivity.this.reverseDocument;
                } else {
                    access$300 = (SecureDocument) PassportActivity.this.documents.get(i);
                }
                SecureDocumentCell secureDocumentCell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(access$300);
                if (secureDocumentCell != null) {
                    String access$900 = PassportActivity.this.getDocumentHash(access$300);
                    Object obj = null;
                    StringBuilder stringBuilder;
                    if (PassportActivity.this.uploadingFileType == 1) {
                        PassportActivity.this.selfieDocument = null;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("selfie");
                        stringBuilder.append(access$900);
                        obj = stringBuilder.toString();
                    } else if (PassportActivity.this.uploadingFileType == 4) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("translation");
                        stringBuilder.append(access$900);
                        obj = stringBuilder.toString();
                    } else if (PassportActivity.this.uploadingFileType == 2) {
                        PassportActivity.this.frontDocument = null;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("front");
                        stringBuilder.append(access$900);
                        obj = stringBuilder.toString();
                    } else if (PassportActivity.this.uploadingFileType == 3) {
                        PassportActivity.this.reverseDocument = null;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("reverse");
                        stringBuilder.append(access$900);
                        obj = stringBuilder.toString();
                    } else if (PassportActivity.this.uploadingFileType == 0) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("files");
                        stringBuilder.append(access$900);
                        obj = stringBuilder.toString();
                    }
                    if (obj != null) {
                        if (PassportActivity.this.documentsErrors != null) {
                            PassportActivity.this.documentsErrors.remove(obj);
                        }
                        if (PassportActivity.this.errorsValues != null) {
                            PassportActivity.this.errorsValues.remove(obj);
                        }
                    }
                    PassportActivity passportActivity = PassportActivity.this;
                    passportActivity.updateUploadText(passportActivity.uploadingFileType);
                    PassportActivity.this.currentPhotoViewerLayout.removeView(secureDocumentCell);
                }
            }

            public String getDeleteMessageString() {
                if (PassportActivity.this.uploadingFileType == 1) {
                    return LocaleController.formatString("PassportDeleteSelfieAlert", NUM, new Object[0]);
                }
                return LocaleController.formatString("PassportDeleteScanAlert", NUM, new Object[0]);
            }
        };
        this.currentActivityType = i;
        this.currentForm = tL_account_authorizationForm;
        this.currentType = tL_secureRequiredType;
        TL_secureRequiredType tL_secureRequiredType3 = this.currentType;
        if (tL_secureRequiredType3 != null) {
            this.allowNonLatinName = tL_secureRequiredType3.native_names;
        }
        this.currentTypeValue = tL_secureValue;
        this.currentDocumentsType = tL_secureRequiredType2;
        this.currentDocumentsTypeValue = tL_secureValue2;
        this.currentPassword = tL_account_password;
        this.currentValues = hashMap;
        this.currentDocumentValues = hashMap2;
        int i2 = this.currentActivityType;
        if (i2 == 3) {
            this.permissionsItems = new ArrayList();
        } else if (i2 == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap();
        }
        if (this.currentDocumentValues == null) {
            this.currentDocumentValues = new HashMap();
        }
        if (i == 5) {
            if (!(UserConfig.getInstance(this.currentAccount).savedPasswordHash == null || UserConfig.getInstance(this.currentAccount).savedSaltedPassword == null)) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            TL_account_password tL_account_password2 = this.currentPassword;
            if (tL_account_password2 == null) {
                loadPasswordInfo();
            } else {
                TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password2);
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            }
            if (!SharedConfig.isPassportConfigLoaded()) {
                TL_help_getPassportConfig tL_help_getPassportConfig = new TL_help_getPassportConfig();
                tL_help_getPassportConfig.hash = SharedConfig.passportConfigHash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_getPassportConfig, -$$Lambda$PassportActivity$pP0eV3Hy639MJNxG0c3lV4tp0MM.INSTANCE);
            }
        }
    }

    static /* synthetic */ void lambda$null$0(TLObject tLObject) {
        if (tLObject instanceof TL_help_passportConfig) {
            TL_help_passportConfig tL_help_passportConfig = (TL_help_passportConfig) tLObject;
            SharedConfig.setPassportConfig(tL_help_passportConfig.countries_langs.data, tL_help_passportConfig.hash);
            return;
        }
        SharedConfig.getCountryLangs();
    }

    public void onResume() {
        super.onResume();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onResume();
        }
        if (this.currentActivityType == 5) {
            ViewGroup[] viewGroupArr = this.inputFieldContainers;
            if (!(viewGroupArr == null || viewGroupArr[0] == null || viewGroupArr[0].getVisibility() != 0)) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
                AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$1boqEPL5RwKnvvajl3SmsZe7IEk(this), 200);
            }
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public /* synthetic */ void lambda$onResume$2$PassportActivity() {
        ViewGroup[] viewGroupArr = this.inputFieldContainers;
        if (viewGroupArr != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public void onPause() {
        super.onPause();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
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
        int i = 0;
        callCallback(false);
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
        if (this.currentActivityType == 7) {
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (i >= slideViewArr.length) {
                    break;
                }
                if (slideViewArr[i] != null) {
                    slideViewArr[i].onDestroyActivity();
                }
                i++;
            }
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
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
            /* JADX WARNING: Removed duplicated region for block: B:80:0x0365  */
            /* JADX WARNING: Removed duplicated region for block: B:83:0x0376  */
            /* JADX WARNING: Removed duplicated region for block: B:87:0x0394  */
            /* JADX WARNING: Removed duplicated region for block: B:86:0x038e  */
            /* JADX WARNING: Removed duplicated region for block: B:91:0x03a4  */
            /* JADX WARNING: Removed duplicated region for block: B:90:0x039d  */
            private boolean onIdentityDone(java.lang.Runnable r26, org.telegram.ui.PassportActivity.ErrorRunnable r27) {
                /*
                r25 = this;
                r7 = r25;
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.uploadingDocuments;
                r0 = r0.isEmpty();
                r8 = 0;
                if (r0 == 0) goto L_0x03de;
            L_0x000f:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.checkFieldsForError();
                if (r0 == 0) goto L_0x0019;
            L_0x0017:
                goto L_0x03de;
            L_0x0019:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.allowNonLatinName;
                r9 = 3;
                r10 = 2;
                r11 = 1;
                if (r0 == 0) goto L_0x0167;
            L_0x0024:
                r0 = org.telegram.ui.PassportActivity.this;
                r0.allowNonLatinName = r8;
                r0 = 0;
                r12 = 0;
            L_0x002b:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.nonLatinNames;
                r1 = r1.length;
                if (r12 >= r1) goto L_0x0164;
            L_0x0034:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.nonLatinNames;
                r1 = r1[r12];
                if (r1 == 0) goto L_0x0160;
            L_0x003e:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.inputFields;
                r1 = r1[r12];
                r2 = NUM; // 0x7f0d0797 float:1.8746056E38 double:1.0531307375E-314;
                r3 = "PassportUseLatinOnly";
                r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
                r1.setErrorText(r2);
                if (r0 != 0) goto L_0x0160;
            L_0x0054:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.nonLatinNames;
                r0 = r0[r8];
                if (r0 == 0) goto L_0x0073;
            L_0x005e:
                r0 = org.telegram.ui.PassportActivity.this;
                r1 = r0.inputExtraFields;
                r1 = r1[r8];
                r1 = r1.getText();
                r1 = r1.toString();
                r0 = r0.getTranslitString(r1);
                goto L_0x0083;
            L_0x0073:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.inputFields;
                r0 = r0[r8];
                r0 = r0.getText();
                r0 = r0.toString();
            L_0x0083:
                r2 = r0;
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.nonLatinNames;
                r0 = r0[r11];
                if (r0 == 0) goto L_0x00a3;
            L_0x008e:
                r0 = org.telegram.ui.PassportActivity.this;
                r1 = r0.inputExtraFields;
                r1 = r1[r11];
                r1 = r1.getText();
                r1 = r1.toString();
                r0 = r0.getTranslitString(r1);
                goto L_0x00b3;
            L_0x00a3:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.inputFields;
                r0 = r0[r11];
                r0 = r0.getText();
                r0 = r0.toString();
            L_0x00b3:
                r3 = r0;
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.nonLatinNames;
                r0 = r0[r10];
                if (r0 == 0) goto L_0x00d3;
            L_0x00be:
                r0 = org.telegram.ui.PassportActivity.this;
                r1 = r0.inputExtraFields;
                r1 = r1[r10];
                r1 = r1.getText();
                r1 = r1.toString();
                r0 = r0.getTranslitString(r1);
                goto L_0x00e3;
            L_0x00d3:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.inputFields;
                r0 = r0[r10];
                r0 = r0.getText();
                r0 = r0.toString();
            L_0x00e3:
                r4 = r0;
                r0 = android.text.TextUtils.isEmpty(r2);
                if (r0 != 0) goto L_0x0154;
            L_0x00ea:
                r0 = android.text.TextUtils.isEmpty(r3);
                if (r0 != 0) goto L_0x0154;
            L_0x00f0:
                r0 = android.text.TextUtils.isEmpty(r4);
                if (r0 != 0) goto L_0x0154;
            L_0x00f6:
                r13 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.getParentActivity();
                r13.<init>(r0);
                r0 = NUM; // 0x7f0d0761 float:1.8745946E38 double:1.053130711E-314;
                r1 = new java.lang.Object[r9];
                r1[r8] = r2;
                r1[r11] = r3;
                r1[r10] = r4;
                r5 = "PassportNameCheckAlert";
                r0 = org.telegram.messenger.LocaleController.formatString(r5, r0, r1);
                r13.setMessage(r0);
                r0 = NUM; // 0x7f0d00eb float:1.8742591E38 double:1.0531298936E-314;
                r1 = "AppName";
                r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
                r13.setTitle(r0);
                r0 = NUM; // 0x7f0d0383 float:1.8743938E38 double:1.0531302217E-314;
                r1 = "Done";
                r14 = org.telegram.messenger.LocaleController.getString(r1, r0);
                r15 = new org.telegram.ui.-$$Lambda$PassportActivity$3$hBvwZ-d4QGDnNuXdFnmSB9952Bs;
                r0 = r15;
                r1 = r25;
                r5 = r26;
                r6 = r27;
                r0.<init>(r1, r2, r3, r4, r5, r6);
                r13.setPositiveButton(r14, r15);
                r0 = NUM; // 0x7f0d0385 float:1.8743942E38 double:1.0531302227E-314;
                r1 = "Edit";
                r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
                r1 = new org.telegram.ui.-$$Lambda$PassportActivity$3$tZIb58L3Zb4a9cJUHmxI2DCu8M8;
                r1.<init>(r7, r12);
                r13.setNegativeButton(r0, r1);
                r0 = org.telegram.ui.PassportActivity.this;
                r1 = r13.create();
                r0.showDialog(r1);
                goto L_0x015f;
            L_0x0154:
                r0 = org.telegram.ui.PassportActivity.this;
                r1 = r0.inputFields;
                r1 = r1[r12];
                r0.onFieldError(r1);
            L_0x015f:
                r0 = 1;
            L_0x0160:
                r12 = r12 + 1;
                goto L_0x002b;
            L_0x0164:
                if (r0 == 0) goto L_0x0167;
            L_0x0166:
                return r8;
            L_0x0167:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.isHasNotAnyChanges();
                if (r0 == 0) goto L_0x0175;
            L_0x016f:
                r0 = org.telegram.ui.PassportActivity.this;
                r0.finishFragment();
                return r8;
            L_0x0175:
                r0 = 0;
                r1 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r1 = r1.documentOnly;	 Catch:{ Exception -> 0x035b }
                if (r1 != 0) goto L_0x02ba;
            L_0x017e:
                r1 = new java.util.HashMap;	 Catch:{ Exception -> 0x035b }
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.currentValues;	 Catch:{ Exception -> 0x035b }
                r1.<init>(r2);	 Catch:{ Exception -> 0x035b }
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.currentType;	 Catch:{ Exception -> 0x035b }
                r2 = r2.native_names;	 Catch:{ Exception -> 0x035b }
                if (r2 == 0) goto L_0x0218;
            L_0x0193:
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.nativeInfoCell;	 Catch:{ Exception -> 0x035b }
                r2 = r2.getVisibility();	 Catch:{ Exception -> 0x035b }
                r3 = "last_name_native";
                r4 = "middle_name_native";
                r5 = "first_name_native";
                if (r2 != 0) goto L_0x01df;
            L_0x01a5:
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.inputExtraFields;	 Catch:{ Exception -> 0x035b }
                r2 = r2[r8];	 Catch:{ Exception -> 0x035b }
                r2 = r2.getText();	 Catch:{ Exception -> 0x035b }
                r2 = r2.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r5, r2);	 Catch:{ Exception -> 0x035b }
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.inputExtraFields;	 Catch:{ Exception -> 0x035b }
                r2 = r2[r11];	 Catch:{ Exception -> 0x035b }
                r2 = r2.getText();	 Catch:{ Exception -> 0x035b }
                r2 = r2.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r4, r2);	 Catch:{ Exception -> 0x035b }
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.inputExtraFields;	 Catch:{ Exception -> 0x035b }
                r2 = r2[r10];	 Catch:{ Exception -> 0x035b }
                r2 = r2.getText();	 Catch:{ Exception -> 0x035b }
                r2 = r2.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r3, r2);	 Catch:{ Exception -> 0x035b }
                goto L_0x0218;
            L_0x01df:
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.inputFields;	 Catch:{ Exception -> 0x035b }
                r2 = r2[r8];	 Catch:{ Exception -> 0x035b }
                r2 = r2.getText();	 Catch:{ Exception -> 0x035b }
                r2 = r2.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r5, r2);	 Catch:{ Exception -> 0x035b }
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.inputFields;	 Catch:{ Exception -> 0x035b }
                r2 = r2[r11];	 Catch:{ Exception -> 0x035b }
                r2 = r2.getText();	 Catch:{ Exception -> 0x035b }
                r2 = r2.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r4, r2);	 Catch:{ Exception -> 0x035b }
                r2 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r2 = r2.inputFields;	 Catch:{ Exception -> 0x035b }
                r2 = r2[r10];	 Catch:{ Exception -> 0x035b }
                r2 = r2.getText();	 Catch:{ Exception -> 0x035b }
                r2 = r2.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r3, r2);	 Catch:{ Exception -> 0x035b }
            L_0x0218:
                r2 = "first_name";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.inputFields;	 Catch:{ Exception -> 0x035b }
                r3 = r3[r8];	 Catch:{ Exception -> 0x035b }
                r3 = r3.getText();	 Catch:{ Exception -> 0x035b }
                r3 = r3.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = "middle_name";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.inputFields;	 Catch:{ Exception -> 0x035b }
                r3 = r3[r11];	 Catch:{ Exception -> 0x035b }
                r3 = r3.getText();	 Catch:{ Exception -> 0x035b }
                r3 = r3.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = "last_name";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.inputFields;	 Catch:{ Exception -> 0x035b }
                r3 = r3[r10];	 Catch:{ Exception -> 0x035b }
                r3 = r3.getText();	 Catch:{ Exception -> 0x035b }
                r3 = r3.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = "birth_date";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.inputFields;	 Catch:{ Exception -> 0x035b }
                r3 = r3[r9];	 Catch:{ Exception -> 0x035b }
                r3 = r3.getText();	 Catch:{ Exception -> 0x035b }
                r3 = r3.toString();	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = "gender";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.currentGender;	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = "country_code";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.currentCitizeship;	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = "residence_country_code";
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x035b }
                r3 = r3.currentResidence;	 Catch:{ Exception -> 0x035b }
                r1.put(r2, r3);	 Catch:{ Exception -> 0x035b }
                r2 = new org.json.JSONObject;	 Catch:{ Exception -> 0x035b }
                r2.<init>();	 Catch:{ Exception -> 0x035b }
                r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0359 }
                r4 = r1.keySet();	 Catch:{ Exception -> 0x0359 }
                r3.<init>(r4);	 Catch:{ Exception -> 0x0359 }
                r4 = new org.telegram.ui.-$$Lambda$PassportActivity$3$mGr-6qgzmzLZw5uyzhfjuEIAkBg;	 Catch:{ Exception -> 0x0359 }
                r4.<init>(r7);	 Catch:{ Exception -> 0x0359 }
                java.util.Collections.sort(r3, r4);	 Catch:{ Exception -> 0x0359 }
                r4 = r3.size();	 Catch:{ Exception -> 0x0359 }
                r5 = 0;
            L_0x02a8:
                if (r5 >= r4) goto L_0x02bb;
            L_0x02aa:
                r6 = r3.get(r5);	 Catch:{ Exception -> 0x0359 }
                r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x0359 }
                r12 = r1.get(r6);	 Catch:{ Exception -> 0x0359 }
                r2.put(r6, r12);	 Catch:{ Exception -> 0x0359 }
                r5 = r5 + 1;
                goto L_0x02a8;
            L_0x02ba:
                r2 = r0;
            L_0x02bb:
                r1 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r1 = r1.currentDocumentsType;	 Catch:{ Exception -> 0x0359 }
                if (r1 == 0) goto L_0x0359;
            L_0x02c3:
                r1 = new java.util.HashMap;	 Catch:{ Exception -> 0x0359 }
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r3 = r3.currentDocumentValues;	 Catch:{ Exception -> 0x0359 }
                r1.<init>(r3);	 Catch:{ Exception -> 0x0359 }
                r3 = "document_no";
                r4 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r4 = r4.inputFields;	 Catch:{ Exception -> 0x0359 }
                r5 = 7;
                r4 = r4[r5];	 Catch:{ Exception -> 0x0359 }
                r4 = r4.getText();	 Catch:{ Exception -> 0x0359 }
                r4 = r4.toString();	 Catch:{ Exception -> 0x0359 }
                r1.put(r3, r4);	 Catch:{ Exception -> 0x0359 }
                r3 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r3 = r3.currentExpireDate;	 Catch:{ Exception -> 0x0359 }
                r3 = r3[r8];	 Catch:{ Exception -> 0x0359 }
                r4 = "expiry_date";
                if (r3 == 0) goto L_0x0328;
            L_0x02f0:
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0359 }
                r5 = "%02d.%02d.%d";
                r6 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0359 }
                r9 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r9 = r9.currentExpireDate;	 Catch:{ Exception -> 0x0359 }
                r9 = r9[r10];	 Catch:{ Exception -> 0x0359 }
                r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0359 }
                r6[r8] = r9;	 Catch:{ Exception -> 0x0359 }
                r9 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r9 = r9.currentExpireDate;	 Catch:{ Exception -> 0x0359 }
                r9 = r9[r11];	 Catch:{ Exception -> 0x0359 }
                r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0359 }
                r6[r11] = r9;	 Catch:{ Exception -> 0x0359 }
                r9 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x0359 }
                r9 = r9.currentExpireDate;	 Catch:{ Exception -> 0x0359 }
                r9 = r9[r8];	 Catch:{ Exception -> 0x0359 }
                r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0359 }
                r6[r10] = r9;	 Catch:{ Exception -> 0x0359 }
                r3 = java.lang.String.format(r3, r5, r6);	 Catch:{ Exception -> 0x0359 }
                r1.put(r4, r3);	 Catch:{ Exception -> 0x0359 }
                goto L_0x032d;
            L_0x0328:
                r3 = "";
                r1.put(r4, r3);	 Catch:{ Exception -> 0x0359 }
            L_0x032d:
                r3 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0359 }
                r3.<init>();	 Catch:{ Exception -> 0x0359 }
                r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x035d }
                r5 = r1.keySet();	 Catch:{ Exception -> 0x035d }
                r4.<init>(r5);	 Catch:{ Exception -> 0x035d }
                r5 = new org.telegram.ui.-$$Lambda$PassportActivity$3$OvzP5ehYJX-5e7BC1WeQR66NW4c;	 Catch:{ Exception -> 0x035d }
                r5.<init>(r7);	 Catch:{ Exception -> 0x035d }
                java.util.Collections.sort(r4, r5);	 Catch:{ Exception -> 0x035d }
                r5 = r4.size();	 Catch:{ Exception -> 0x035d }
            L_0x0347:
                if (r8 >= r5) goto L_0x035d;
            L_0x0349:
                r6 = r4.get(r8);	 Catch:{ Exception -> 0x035d }
                r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x035d }
                r9 = r1.get(r6);	 Catch:{ Exception -> 0x035d }
                r3.put(r6, r9);	 Catch:{ Exception -> 0x035d }
                r8 = r8 + 1;
                goto L_0x0347;
            L_0x0359:
                r3 = r0;
                goto L_0x035d;
            L_0x035b:
                r2 = r0;
                r3 = r2;
            L_0x035d:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.fieldsErrors;
                if (r1 == 0) goto L_0x036e;
            L_0x0365:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.fieldsErrors;
                r1.clear();
            L_0x036e:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.documentsErrors;
                if (r1 == 0) goto L_0x037f;
            L_0x0376:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.documentsErrors;
                r1.clear();
            L_0x037f:
                r1 = org.telegram.ui.PassportActivity.this;
                r12 = r1.delegate;
                r1 = org.telegram.ui.PassportActivity.this;
                r13 = r1.currentType;
                r14 = 0;
                if (r2 == 0) goto L_0x0394;
            L_0x038e:
                r1 = r2.toString();
                r15 = r1;
                goto L_0x0395;
            L_0x0394:
                r15 = r0;
            L_0x0395:
                r1 = org.telegram.ui.PassportActivity.this;
                r16 = r1.currentDocumentsType;
                if (r3 == 0) goto L_0x03a4;
            L_0x039d:
                r1 = r3.toString();
                r17 = r1;
                goto L_0x03a6;
            L_0x03a4:
                r17 = r0;
            L_0x03a6:
                r18 = 0;
                r1 = org.telegram.ui.PassportActivity.this;
                r19 = r1.selfieDocument;
                r1 = org.telegram.ui.PassportActivity.this;
                r20 = r1.translationDocuments;
                r1 = org.telegram.ui.PassportActivity.this;
                r21 = r1.frontDocument;
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.reverseLayout;
                if (r1 == 0) goto L_0x03d4;
            L_0x03c2:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.reverseLayout;
                r1 = r1.getVisibility();
                if (r1 != 0) goto L_0x03d4;
            L_0x03ce:
                r0 = org.telegram.ui.PassportActivity.this;
                r0 = r0.reverseDocument;
            L_0x03d4:
                r22 = r0;
                r23 = r26;
                r24 = r27;
                r12.saveValue(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24);
                return r11;
            L_0x03de:
                return r8;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity$AnonymousClass3.onIdentityDone(java.lang.Runnable, org.telegram.ui.PassportActivity$ErrorRunnable):boolean");
            }

            public /* synthetic */ void lambda$onIdentityDone$0$PassportActivity$3(String str, String str2, String str3, Runnable runnable, ErrorRunnable errorRunnable, DialogInterface dialogInterface, int i) {
                PassportActivity.this.inputFields[0].setText(str);
                PassportActivity.this.inputFields[1].setText(str2);
                PassportActivity.this.inputFields[2].setText(str3);
                PassportActivity.this.showEditDoneProgress(true, true);
                onIdentityDone(runnable, errorRunnable);
            }

            public /* synthetic */ void lambda$onIdentityDone$1$PassportActivity$3(int i, DialogInterface dialogInterface, int i2) {
                PassportActivity passportActivity = PassportActivity.this;
                passportActivity.onFieldError(passportActivity.inputFields[i]);
            }

            public /* synthetic */ int lambda$onIdentityDone$2$PassportActivity$3(String str, String str2) {
                int access$2000 = PassportActivity.this.getFieldCost(str);
                int access$20002 = PassportActivity.this.getFieldCost(str2);
                if (access$2000 < access$20002) {
                    return -1;
                }
                return access$2000 > access$20002 ? 1 : 0;
            }

            public /* synthetic */ int lambda$onIdentityDone$3$PassportActivity$3(String str, String str2) {
                int access$2000 = PassportActivity.this.getFieldCost(str);
                int access$20002 = PassportActivity.this.getFieldCost(str2);
                if (access$2000 < access$20002) {
                    return -1;
                }
                return access$2000 > access$20002 ? 1 : 0;
            }

            /* JADX WARNING: Removed duplicated region for block: B:73:0x02a6  */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x02b7  */
            /* JADX WARNING: Removed duplicated region for block: B:79:0x02cf  */
            public void onItemClick(int r21) {
                /*
                r20 = this;
                r0 = r20;
                r1 = r21;
                r2 = 5;
                r3 = -1;
                r4 = 0;
                if (r1 != r3) goto L_0x002e;
            L_0x0009:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.checkDiscard();
                if (r1 == 0) goto L_0x0012;
            L_0x0011:
                return;
            L_0x0012:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.currentActivityType;
                if (r1 == 0) goto L_0x0022;
            L_0x001a:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.currentActivityType;
                if (r1 != r2) goto L_0x0027;
            L_0x0022:
                r1 = org.telegram.ui.PassportActivity.this;
                r1.callCallback(r4);
            L_0x0027:
                r1 = org.telegram.ui.PassportActivity.this;
                r1.finishFragment();
                goto L_0x0365;
            L_0x002e:
                r5 = 0;
                r6 = 1;
                if (r1 != r6) goto L_0x00eb;
            L_0x0032:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.getParentActivity();
                if (r1 != 0) goto L_0x003b;
            L_0x003a:
                return;
            L_0x003b:
                r1 = new android.widget.TextView;
                r2 = org.telegram.ui.PassportActivity.this;
                r2 = r2.getParentActivity();
                r1.<init>(r2);
                r2 = NUM; // 0x7f0d0729 float:1.8745833E38 double:1.053130683E-314;
                r7 = "PassportInfo2";
                r2 = org.telegram.messenger.LocaleController.getString(r7, r2);
                r7 = new android.text.SpannableStringBuilder;
                r7.<init>(r2);
                r8 = 42;
                r9 = r2.indexOf(r8);
                r2 = r2.lastIndexOf(r8);
                if (r9 == r3) goto L_0x0082;
            L_0x0060:
                if (r2 == r3) goto L_0x0082;
            L_0x0062:
                r3 = r2 + 1;
                r8 = "";
                r7.replace(r2, r3, r8);
                r3 = r9 + 1;
                r7.replace(r9, r3, r8);
                r3 = new org.telegram.ui.PassportActivity$3$1;
                r8 = NUM; // 0x7f0d072b float:1.8745837E38 double:1.053130684E-314;
                r10 = "PassportInfoUrl";
                r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
                r3.<init>(r8);
                r2 = r2 - r6;
                r8 = 33;
                r7.setSpan(r3, r9, r2, r8);
            L_0x0082:
                r1.setText(r7);
                r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
                r1.setTextSize(r6, r2);
                r2 = "dialogTextLink";
                r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r1.setLinkTextColor(r2);
                r2 = "dialogLinkSelection";
                r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r1.setHighlightColor(r2);
                r2 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r1.setPadding(r3, r4, r2, r4);
                r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy;
                r2.<init>();
                r1.setMovementMethod(r2);
                r2 = "dialogTextBlack";
                r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r1.setTextColor(r2);
                r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.getParentActivity();
                r2.<init>(r3);
                r2.setView(r1);
                r1 = NUM; // 0x7f0d072a float:1.8745835E38 double:1.0531306837E-314;
                r3 = "PassportInfoTitle";
                r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
                r2.setTitle(r1);
                r1 = NUM; // 0x7f0d02cd float:1.874357E38 double:1.053130132E-314;
                r3 = "Close";
                r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
                r2.setNegativeButton(r1, r5);
                r1 = org.telegram.ui.PassportActivity.this;
                r2 = r2.create();
                r1.showDialog(r2);
                goto L_0x0365;
            L_0x00eb:
                r3 = 2;
                if (r1 != r3) goto L_0x0365;
            L_0x00ee:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.currentActivityType;
                if (r1 != r2) goto L_0x00fc;
            L_0x00f6:
                r1 = org.telegram.ui.PassportActivity.this;
                r1.onPasswordDone(r4);
                return;
            L_0x00fc:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.currentActivityType;
                r2 = 7;
                if (r1 != r2) goto L_0x0118;
            L_0x0105:
                r1 = org.telegram.ui.PassportActivity.this;
                r1 = r1.views;
                r2 = org.telegram.ui.PassportActivity.this;
                r2 = r2.currentViewNum;
                r1 = r1[r2];
                r1.onNextPressed();
                goto L_0x0365;
            L_0x0118:
                r1 = new org.telegram.ui.-$$Lambda$PassportActivity$3$GdH_U_rnd96VkbnTuTp9EkJj-aw;
                r1.<init>(r0);
                r2 = new org.telegram.ui.PassportActivity$3$2;
                r2.<init>(r1);
                r7 = org.telegram.ui.PassportActivity.this;
                r7 = r7.currentActivityType;
                r8 = 4;
                if (r7 != r8) goto L_0x0174;
            L_0x012b:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.useCurrentValue;
                if (r3 == 0) goto L_0x013b;
            L_0x0133:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.currentEmail;
            L_0x0139:
                r9 = r3;
                goto L_0x0155;
            L_0x013b:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.checkFieldsForError();
                if (r3 == 0) goto L_0x0144;
            L_0x0143:
                return;
            L_0x0144:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.inputFields;
                r3 = r3[r4];
                r3 = r3.getText();
                r3 = r3.toString();
                goto L_0x0139;
            L_0x0155:
                r3 = org.telegram.ui.PassportActivity.this;
                r7 = r3.delegate;
                r3 = org.telegram.ui.PassportActivity.this;
                r8 = r3.currentType;
                r10 = 0;
                r11 = 0;
                r12 = 0;
                r13 = 0;
                r14 = 0;
                r15 = 0;
                r16 = 0;
                r17 = 0;
                r18 = r1;
                r19 = r2;
                r7.saveValue(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);
                goto L_0x0360;
            L_0x0174:
                r7 = org.telegram.ui.PassportActivity.this;
                r7 = r7.currentActivityType;
                r9 = 3;
                if (r7 != r9) goto L_0x01ef;
            L_0x017d:
                r4 = org.telegram.ui.PassportActivity.this;
                r4 = r4.useCurrentValue;
                if (r4 == 0) goto L_0x0197;
            L_0x0185:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.currentAccount;
                r3 = org.telegram.messenger.UserConfig.getInstance(r3);
                r3 = r3.getCurrentUser();
                r3 = r3.phone;
            L_0x0195:
                r9 = r3;
                goto L_0x01d0;
            L_0x0197:
                r4 = org.telegram.ui.PassportActivity.this;
                r4 = r4.checkFieldsForError();
                if (r4 == 0) goto L_0x01a0;
            L_0x019f:
                return;
            L_0x01a0:
                r4 = new java.lang.StringBuilder;
                r4.<init>();
                r5 = org.telegram.ui.PassportActivity.this;
                r5 = r5.inputFields;
                r5 = r5[r6];
                r5 = r5.getText();
                r5 = r5.toString();
                r4.append(r5);
                r5 = org.telegram.ui.PassportActivity.this;
                r5 = r5.inputFields;
                r3 = r5[r3];
                r3 = r3.getText();
                r3 = r3.toString();
                r4.append(r3);
                r3 = r4.toString();
                goto L_0x0195;
            L_0x01d0:
                r3 = org.telegram.ui.PassportActivity.this;
                r7 = r3.delegate;
                r3 = org.telegram.ui.PassportActivity.this;
                r8 = r3.currentType;
                r10 = 0;
                r11 = 0;
                r12 = 0;
                r13 = 0;
                r14 = 0;
                r15 = 0;
                r16 = 0;
                r17 = 0;
                r18 = r1;
                r19 = r2;
                r7.saveValue(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);
                goto L_0x0360;
            L_0x01ef:
                r7 = org.telegram.ui.PassportActivity.this;
                r7 = r7.currentActivityType;
                if (r7 != r3) goto L_0x02fb;
            L_0x01f7:
                r7 = org.telegram.ui.PassportActivity.this;
                r7 = r7.uploadingDocuments;
                r7 = r7.isEmpty();
                if (r7 == 0) goto L_0x02fa;
            L_0x0203:
                r7 = org.telegram.ui.PassportActivity.this;
                r7 = r7.checkFieldsForError();
                if (r7 == 0) goto L_0x020d;
            L_0x020b:
                goto L_0x02fa;
            L_0x020d:
                r7 = org.telegram.ui.PassportActivity.this;
                r7 = r7.isHasNotAnyChanges();
                if (r7 == 0) goto L_0x021b;
            L_0x0215:
                r1 = org.telegram.ui.PassportActivity.this;
                r1.finishFragment();
                return;
            L_0x021b:
                r7 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029d }
                r7 = r7.documentOnly;	 Catch:{ Exception -> 0x029d }
                if (r7 != 0) goto L_0x029d;
            L_0x0223:
                r7 = new org.json.JSONObject;	 Catch:{ Exception -> 0x029d }
                r7.<init>();	 Catch:{ Exception -> 0x029d }
                r10 = "street_line1";
                r11 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029e }
                r11 = r11.inputFields;	 Catch:{ Exception -> 0x029e }
                r4 = r11[r4];	 Catch:{ Exception -> 0x029e }
                r4 = r4.getText();	 Catch:{ Exception -> 0x029e }
                r4 = r4.toString();	 Catch:{ Exception -> 0x029e }
                r7.put(r10, r4);	 Catch:{ Exception -> 0x029e }
                r4 = "street_line2";
                r10 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029e }
                r10 = r10.inputFields;	 Catch:{ Exception -> 0x029e }
                r10 = r10[r6];	 Catch:{ Exception -> 0x029e }
                r10 = r10.getText();	 Catch:{ Exception -> 0x029e }
                r10 = r10.toString();	 Catch:{ Exception -> 0x029e }
                r7.put(r4, r10);	 Catch:{ Exception -> 0x029e }
                r4 = "post_code";
                r10 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029e }
                r10 = r10.inputFields;	 Catch:{ Exception -> 0x029e }
                r3 = r10[r3];	 Catch:{ Exception -> 0x029e }
                r3 = r3.getText();	 Catch:{ Exception -> 0x029e }
                r3 = r3.toString();	 Catch:{ Exception -> 0x029e }
                r7.put(r4, r3);	 Catch:{ Exception -> 0x029e }
                r3 = "city";
                r4 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029e }
                r4 = r4.inputFields;	 Catch:{ Exception -> 0x029e }
                r4 = r4[r9];	 Catch:{ Exception -> 0x029e }
                r4 = r4.getText();	 Catch:{ Exception -> 0x029e }
                r4 = r4.toString();	 Catch:{ Exception -> 0x029e }
                r7.put(r3, r4);	 Catch:{ Exception -> 0x029e }
                r3 = "state";
                r4 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029e }
                r4 = r4.inputFields;	 Catch:{ Exception -> 0x029e }
                r4 = r4[r8];	 Catch:{ Exception -> 0x029e }
                r4 = r4.getText();	 Catch:{ Exception -> 0x029e }
                r4 = r4.toString();	 Catch:{ Exception -> 0x029e }
                r7.put(r3, r4);	 Catch:{ Exception -> 0x029e }
                r3 = "country_code";
                r4 = org.telegram.ui.PassportActivity.this;	 Catch:{ Exception -> 0x029e }
                r4 = r4.currentCitizeship;	 Catch:{ Exception -> 0x029e }
                r7.put(r3, r4);	 Catch:{ Exception -> 0x029e }
                goto L_0x029e;
            L_0x029d:
                r7 = r5;
            L_0x029e:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.fieldsErrors;
                if (r3 == 0) goto L_0x02af;
            L_0x02a6:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.fieldsErrors;
                r3.clear();
            L_0x02af:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.documentsErrors;
                if (r3 == 0) goto L_0x02c0;
            L_0x02b7:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.documentsErrors;
                r3.clear();
            L_0x02c0:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.delegate;
                r4 = org.telegram.ui.PassportActivity.this;
                r8 = r4.currentType;
                r9 = 0;
                if (r7 == 0) goto L_0x02d3;
            L_0x02cf:
                r5 = r7.toString();
            L_0x02d3:
                r10 = r5;
                r4 = org.telegram.ui.PassportActivity.this;
                r11 = r4.currentDocumentsType;
                r12 = 0;
                r4 = org.telegram.ui.PassportActivity.this;
                r13 = r4.documents;
                r4 = org.telegram.ui.PassportActivity.this;
                r14 = r4.selfieDocument;
                r4 = org.telegram.ui.PassportActivity.this;
                r15 = r4.translationDocuments;
                r16 = 0;
                r17 = 0;
                r7 = r3;
                r18 = r1;
                r19 = r2;
                r7.saveValue(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19);
                goto L_0x0360;
            L_0x02fa:
                return;
            L_0x02fb:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.currentActivityType;
                if (r3 != r6) goto L_0x030a;
            L_0x0303:
                r1 = r0.onIdentityDone(r1, r2);
                if (r1 != 0) goto L_0x0360;
            L_0x0309:
                return;
            L_0x030a:
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.currentActivityType;
                r5 = 6;
                if (r3 != r5) goto L_0x0360;
            L_0x0313:
                r3 = new org.telegram.tgnet.TLRPC$TL_account_verifyEmail;
                r3.<init>();
                r5 = org.telegram.ui.PassportActivity.this;
                r5 = r5.currentValues;
                r7 = "email";
                r5 = r5.get(r7);
                r5 = (java.lang.String) r5;
                r3.email = r5;
                r5 = org.telegram.ui.PassportActivity.this;
                r5 = r5.inputFields;
                r4 = r5[r4];
                r4 = r4.getText();
                r4 = r4.toString();
                r3.code = r4;
                r4 = org.telegram.ui.PassportActivity.this;
                r4 = r4.currentAccount;
                r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
                r5 = new org.telegram.ui.-$$Lambda$PassportActivity$3$_Z500k40kXIJwsW8EZFrcODEnk4;
                r5.<init>(r0, r1, r2, r3);
                r1 = r4.sendRequest(r3, r5);
                r2 = org.telegram.ui.PassportActivity.this;
                r2 = r2.currentAccount;
                r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
                r3 = org.telegram.ui.PassportActivity.this;
                r3 = r3.classGuid;
                r2.bindRequestToGuid(r1, r3);
            L_0x0360:
                r1 = org.telegram.ui.PassportActivity.this;
                r1.showEditDoneProgress(r6, r6);
            L_0x0365:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity$AnonymousClass3.onItemClick(int):void");
            }

            public /* synthetic */ void lambda$onItemClick$4$PassportActivity$3() {
                PassportActivity.this.finishFragment();
            }

            public /* synthetic */ void lambda$onItemClick$6$PassportActivity$3(Runnable runnable, ErrorRunnable errorRunnable, TL_account_verifyEmail tL_account_verifyEmail, TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$3$H95RgqCbEw0JF2FdjyTyxP1Zzp8(this, tL_error, runnable, errorRunnable, tL_account_verifyEmail));
            }

            public /* synthetic */ void lambda$null$5$PassportActivity$3(TL_error tL_error, Runnable runnable, ErrorRunnable errorRunnable, TL_account_verifyEmail tL_account_verifyEmail) {
                TL_error tL_error2 = tL_error;
                if (tL_error2 == null) {
                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), null, null, null, null, null, null, null, null, runnable, errorRunnable);
                    return;
                }
                AlertsCreator.processError(PassportActivity.this.currentAccount, tL_error2, PassportActivity.this, tL_account_verifyEmail, new Object[0]);
                errorRunnable.onError(null, null);
            }
        });
        String str = "actionBarDefault";
        if (this.currentActivityType == 7) {
            AnonymousClass4 anonymousClass4 = new ScrollView(context) {
                /* Access modifiers changed, original: protected */
                public boolean onRequestFocusInDescendants(int i, Rect rect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                    if (PassportActivity.this.currentViewNum == 1 || PassportActivity.this.currentViewNum == 2 || PassportActivity.this.currentViewNum == 4) {
                        rect.bottom += AndroidUtilities.dp(40.0f);
                    }
                    return super.requestChildRectangleOnScreen(view, rect, z);
                }

                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    PassportActivity.this.scrollHeight = MeasureSpec.getSize(i2) - AndroidUtilities.dp(30.0f);
                    super.onMeasure(i, i2);
                }
            };
            this.scrollView = anonymousClass4;
            this.fragmentView = anonymousClass4;
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(str));
        } else {
            this.fragmentView = new FrameLayout(context);
            View view = this.fragmentView;
            FrameLayout frameLayout = (FrameLayout) view;
            view.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.scrollView = new ScrollView(context) {
                /* Access modifiers changed, original: protected */
                public boolean onRequestFocusInDescendants(int i, Rect rect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                    rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                    rect.top += AndroidUtilities.dp(20.0f);
                    rect.bottom += AndroidUtilities.dp(50.0f);
                    return super.requestChildRectangleOnScreen(view, rect, z);
                }
            };
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(str));
            frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentActivityType == 0 ? 48.0f : 0.0f));
            this.linearLayout2 = new LinearLayout(context);
            this.linearLayout2.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        }
        int i = this.currentActivityType;
        if (!(i == 0 || i == 8)) {
            this.doneItem = this.actionBar.createMenu().addItemWithWidth(2, NUM, AndroidUtilities.dp(56.0f));
            this.progressView = new ContextProgressView(context, 1);
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            i = this.currentActivityType;
            if (i == 1 || i == 2) {
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    try {
                        if (chatAttachAlert.isShowing()) {
                            this.chatAttachAlert.dismiss();
                        }
                    } catch (Exception unused) {
                    }
                    this.chatAttachAlert.onDestroy();
                    this.chatAttachAlert = null;
                }
            }
        }
        i = this.currentActivityType;
        if (i == 5) {
            createPasswordInterface(context);
        } else if (i == 0) {
            createRequestInterface(context);
        } else if (i == 1) {
            createIdentityInterface(context);
            fillInitialValues();
        } else if (i == 2) {
            createAddressInterface(context);
            fillInitialValues();
        } else if (i == 3) {
            createPhoneInterface(context);
        } else if (i == 4) {
            createEmailInterface(context);
        } else if (i == 6) {
            createEmailVerificationInterface(context);
        } else if (i == 7) {
            createPhoneVerificationInterface(context);
        } else if (i == 8) {
            createManageInterface(context);
        }
        return this.fragmentView;
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    public void dismissCurrentDialig() {
        Dialog dialog = this.chatAttachAlert;
        if (dialog == null || this.visibleDialog != dialog) {
            super.dismissCurrentDialig();
            return;
        }
        dialog.closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.hideCamera(true);
    }

    private String getTranslitString(String str) {
        return LocaleController.getInstance().getTranslitString(str, true);
    }

    private int getFieldCost(java.lang.String r2) {
        /*
        r1 = this;
        r0 = r2.hashCode();
        switch(r0) {
            case -2006252145: goto L_0x00b8;
            case -1537298398: goto L_0x00ae;
            case -1249512767: goto L_0x00a4;
            case -796150911: goto L_0x0099;
            case -796150910: goto L_0x008e;
            case -160985414: goto L_0x0084;
            case 3053931: goto L_0x0079;
            case 109757585: goto L_0x006e;
            case 421072629: goto L_0x0064;
            case 451516732: goto L_0x005a;
            case 475919162: goto L_0x004e;
            case 506677093: goto L_0x0042;
            case 1168724782: goto L_0x0037;
            case 1181577377: goto L_0x002c;
            case 1481071862: goto L_0x0020;
            case 2002465324: goto L_0x0014;
            case 2013122196: goto L_0x0009;
            default: goto L_0x0007;
        };
    L_0x0007:
        goto L_0x00c3;
    L_0x0009:
        r0 = "last_name";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0011:
        r2 = 4;
        goto L_0x00c4;
    L_0x0014:
        r0 = "post_code";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x001c:
        r2 = 14;
        goto L_0x00c4;
    L_0x0020:
        r0 = "country_code";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0028:
        r2 = 8;
        goto L_0x00c4;
    L_0x002c:
        r0 = "middle_name_native";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0034:
        r2 = 3;
        goto L_0x00c4;
    L_0x0037:
        r0 = "birth_date";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x003f:
        r2 = 6;
        goto L_0x00c4;
    L_0x0042:
        r0 = "document_no";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x004a:
        r2 = 10;
        goto L_0x00c4;
    L_0x004e:
        r0 = "expiry_date";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0056:
        r2 = 11;
        goto L_0x00c4;
    L_0x005a:
        r0 = "first_name_native";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0062:
        r2 = 1;
        goto L_0x00c4;
    L_0x0064:
        r0 = "middle_name";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x006c:
        r2 = 2;
        goto L_0x00c4;
    L_0x006e:
        r0 = "state";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0076:
        r2 = 16;
        goto L_0x00c4;
    L_0x0079:
        r0 = "city";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0081:
        r2 = 15;
        goto L_0x00c4;
    L_0x0084:
        r0 = "first_name";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x008c:
        r2 = 0;
        goto L_0x00c4;
    L_0x008e:
        r0 = "street_line2";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x0096:
        r2 = 13;
        goto L_0x00c4;
    L_0x0099:
        r0 = "street_line1";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x00a1:
        r2 = 12;
        goto L_0x00c4;
    L_0x00a4:
        r0 = "gender";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x00ac:
        r2 = 7;
        goto L_0x00c4;
    L_0x00ae:
        r0 = "last_name_native";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x00b6:
        r2 = 5;
        goto L_0x00c4;
    L_0x00b8:
        r0 = "residence_country_code";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x00c3;
    L_0x00c0:
        r2 = 9;
        goto L_0x00c4;
    L_0x00c3:
        r2 = -1;
    L_0x00c4:
        switch(r2) {
            case 0: goto L_0x00f1;
            case 1: goto L_0x00f1;
            case 2: goto L_0x00ee;
            case 3: goto L_0x00ee;
            case 4: goto L_0x00eb;
            case 5: goto L_0x00eb;
            case 6: goto L_0x00e8;
            case 7: goto L_0x00e5;
            case 8: goto L_0x00e2;
            case 9: goto L_0x00df;
            case 10: goto L_0x00dc;
            case 11: goto L_0x00d9;
            case 12: goto L_0x00d6;
            case 13: goto L_0x00d3;
            case 14: goto L_0x00d0;
            case 15: goto L_0x00cd;
            case 16: goto L_0x00ca;
            default: goto L_0x00c7;
        };
    L_0x00c7:
        r2 = 100;
        return r2;
    L_0x00ca:
        r2 = 33;
        return r2;
    L_0x00cd:
        r2 = 32;
        return r2;
    L_0x00d0:
        r2 = 31;
        return r2;
    L_0x00d3:
        r2 = 30;
        return r2;
    L_0x00d6:
        r2 = 29;
        return r2;
    L_0x00d9:
        r2 = 28;
        return r2;
    L_0x00dc:
        r2 = 27;
        return r2;
    L_0x00df:
        r2 = 26;
        return r2;
    L_0x00e2:
        r2 = 25;
        return r2;
    L_0x00e5:
        r2 = 24;
        return r2;
    L_0x00e8:
        r2 = 23;
        return r2;
    L_0x00eb:
        r2 = 22;
        return r2;
    L_0x00ee:
        r2 = 21;
        return r2;
    L_0x00f1:
        r2 = 20;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.getFieldCost(java.lang.String):int");
    }

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", NUM));
        FrameLayout frameLayout = new FrameLayout(context);
        this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        for (int i = 0; i < 3; i++) {
            this.views[i] = new PhoneConfirmationView(this, context, i + 2);
            this.views[i].setVisibility(8);
            View view = this.views[i];
            float f = 18.0f;
            float f2 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (AndroidUtilities.isTablet()) {
                f = 26.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, f2, 30.0f, f, 0.0f));
        }
        Bundle bundle = new Bundle();
        String str = "phone";
        bundle.putString(str, (String) this.currentValues.get(str));
        fillNextCodeParams(bundle, this.currentPhoneVerification, false);
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new -$$Lambda$PassportActivity$phfg_JpT4YdV6dCoTphvWTVrdjI(this)), this.classGuid);
    }

    public /* synthetic */ void lambda$loadPasswordInfo$4$PassportActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$QH-KW6UpOuxsybHXF9jp7bpwNpc(this, tLObject));
    }

    public /* synthetic */ void lambda$null$3$PassportActivity(TLObject tLObject) {
        if (tLObject != null) {
            this.currentPassword = (TL_account_password) tLObject;
            if (TwoStepVerificationActivity.canHandleCurrentPassword(this.currentPassword, false)) {
                TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
                updatePasswordInterface();
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            } else {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
            }
        }
    }

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", NUM));
        this.inputFields = new EditTextBoldCursor[1];
        for (int i = 0; i < 1; i++) {
            FrameLayout frameLayout = new FrameLayout(context);
            this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 50));
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[i] = new EditTextBoldCursor(context);
            this.inputFields[i].setTag(Integer.valueOf(i));
            this.inputFields[i].setTextSize(1, 16.0f);
            this.inputFields[i].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            String str = "windowBackgroundWhiteBlackText";
            this.inputFields[i].setTextColor(Theme.getColor(str));
            this.inputFields[i].setBackgroundDrawable(null);
            this.inputFields[i].setCursorColor(Theme.getColor(str));
            this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i].setCursorWidth(1.5f);
            int i2 = 3;
            this.inputFields[i].setInputType(3);
            this.inputFields[i].setImeOptions(NUM);
            if (i == 0) {
                this.inputFields[i].setHint(LocaleController.getString("PassportEmailCode", NUM));
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            this.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            EditText editText = this.inputFields[i];
            if (LocaleController.isRTL) {
                i2 = 5;
            }
            editText.setGravity(i2);
            frameLayout.addView(this.inputFields[i], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[i].setOnEditorActionListener(new -$$Lambda$PassportActivity$z1QQrtghbex8oLvar_ona_95yF_0(this));
            this.inputFields[i].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!(PassportActivity.this.ignoreOnTextChange || PassportActivity.this.emailCodeLength == 0 || PassportActivity.this.inputFields[0].length() != PassportActivity.this.emailCodeLength)) {
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

    public /* synthetic */ boolean lambda$createEmailVerificationInterface$5$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    private void createPasswordInterface(Context context) {
        int i;
        Object obj;
        Context context2 = context;
        if (this.currentForm != null) {
            for (i = 0; i < this.currentForm.users.size(); i++) {
                obj = (User) this.currentForm.users.get(i);
                if (obj.id == this.currentBotId) {
                    break;
                }
            }
            obj = null;
        } else {
            obj = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", NUM));
        this.emptyView = new EmptyTextProgressView(context2);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.passwordAvatarContainer = new FrameLayout(context2);
        this.linearLayout2.addView(this.passwordAvatarContainer, LayoutHelper.createLinear(-1, 100));
        BackupImageView backupImageView = new BackupImageView(context2);
        backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(backupImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        backupImageView.setImage(ImageLocation.getForUser(obj, false), "50_50", new AvatarDrawable((User) obj), obj);
        this.passwordRequestTextView = new TextInfoPrivacyCell(context2);
        this.passwordRequestTextView.getTextView().setGravity(1);
        if (this.currentBotId == 0) {
            this.passwordRequestTextView.setText(LocaleController.getString("PassportSelfRequest", NUM));
        } else {
            this.passwordRequestTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", NUM, UserObject.getFirstName(obj))));
        }
        ((LayoutParams) this.passwordRequestTextView.getTextView().getLayoutParams()).gravity = 1;
        int i2 = 5;
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        this.noPasswordImageView = new ImageView(context2);
        this.noPasswordImageView.setImageResource(NUM);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        this.noPasswordTextView = new TextView(context2);
        this.noPasswordTextView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.noPasswordTextView.setText(LocaleController.getString("TelegramPassportCreatePasswordInfo", NUM));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView = new TextView(context2);
        this.noPasswordSetTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText5"));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.noPasswordSetTextView.setText(LocaleController.getString("TelegramPassportCreatePassword", NUM));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 9.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new -$$Lambda$PassportActivity$M4nzx3gJGtJxSj4qK5fr27GLS6o(this));
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = new ViewGroup[1];
        for (i = 0; i < 1; i++) {
            this.inputFieldContainers[i] = new FrameLayout(context2);
            this.linearLayout2.addView(this.inputFieldContainers[i], LayoutHelper.createLinear(-1, 50));
            this.inputFieldContainers[i].setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[i] = new EditTextBoldCursor(context2);
            this.inputFields[i].setTag(Integer.valueOf(i));
            this.inputFields[i].setTextSize(1, 16.0f);
            this.inputFields[i].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            String str = "windowBackgroundWhiteBlackText";
            this.inputFields[i].setTextColor(Theme.getColor(str));
            this.inputFields[i].setBackgroundDrawable(null);
            this.inputFields[i].setCursorColor(Theme.getColor(str));
            this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i].setCursorWidth(1.5f);
            this.inputFields[i].setInputType(129);
            this.inputFields[i].setMaxLines(1);
            this.inputFields[i].setLines(1);
            this.inputFields[i].setSingleLine(true);
            this.inputFields[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.inputFields[i].setTypeface(Typeface.DEFAULT);
            this.inputFields[i].setImeOptions(NUM);
            this.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[i].setGravity(LocaleController.isRTL ? 5 : 3);
            this.inputFieldContainers[i].addView(this.inputFields[i], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[i].setOnEditorActionListener(new -$$Lambda$PassportActivity$It4B3VALo5oSVQDPd8g6gcO-bFY(this));
            this.inputFields[i].setCustomSelectionActionModeCallback(new Callback() {
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    return false;
                }

                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode actionMode) {
                }

                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }
            });
        }
        this.passwordInfoRequestTextView = new TextInfoPrivacyCell(context2);
        this.passwordInfoRequestTextView.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", NUM, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        this.passwordForgotButton = new TextView(context2);
        this.passwordForgotButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString("ForgotPassword", NUM));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        LinearLayout linearLayout = this.linearLayout2;
        TextView textView = this.passwordForgotButton;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, 30, i2 | 48, 21, 0, 21, 0));
        this.passwordForgotButton.setOnClickListener(new -$$Lambda$PassportActivity$rCPvENS17XdkP3jlE3Ii__88K9s(this));
        updatePasswordInterface();
    }

    public /* synthetic */ void lambda$createPasswordInterface$6$PassportActivity(View view) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
        twoStepVerificationActivity.setCloseAfterSet(true);
        twoStepVerificationActivity.setCurrentPasswordInfo(new byte[0], this.currentPassword);
        presentFragment(twoStepVerificationActivity);
    }

    public /* synthetic */ boolean lambda$createPasswordInterface$7$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    public /* synthetic */ void lambda$createPasswordInterface$12$PassportActivity(View view) {
        if (this.currentPassword.has_recovery) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new -$$Lambda$PassportActivity$lgU77TEJCVB2ahunM2AyZkIsQ-Q(this), 10), this.classGuid);
        } else if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", NUM), new -$$Lambda$PassportActivity$I-dc4hA-LGNwWgzhXirKJ8kizjk(this));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
            builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", NUM));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$10$PassportActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$6lQsSYr5slO_jvBJxggKac2g49o(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$9$PassportActivity(TL_error tL_error, TLObject tLObject) {
        needHideProgress();
        String str = "AppName";
        if (tL_error == null) {
            TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) tLObject;
            Builder builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, tL_auth_passwordRecovery.email_pattern));
            builder.setTitle(LocaleController.getString(str, NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PassportActivity$hLNmBmiskobIF7R8TJhAQSbqU04(this, tL_auth_passwordRecovery));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
            String formatPluralString;
            int intValue = Utilities.parseInt(tL_error.text).intValue();
            if (intValue < 60) {
                formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
        } else {
            showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
        }
    }

    public /* synthetic */ void lambda$null$8$PassportActivity(TL_auth_passwordRecovery tL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
        twoStepVerificationActivity.setRecoveryParams(this.currentPassword);
        this.currentPassword.email_unconfirmed_pattern = tL_auth_passwordRecovery.email_pattern;
        presentFragment(twoStepVerificationActivity);
    }

    public /* synthetic */ void lambda$null$11$PassportActivity(DialogInterface dialogInterface, int i) {
        Context parentActivity = getParentActivity();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://telegram.org/deactivate?phone=");
        stringBuilder.append(UserConfig.getInstance(this.currentAccount).getClientPhone());
        Browser.openUrl(parentActivity, stringBuilder.toString());
    }

    private void onPasswordDone(boolean z) {
        String str;
        if (z) {
            str = null;
        } else {
            str = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(str)) {
                onPasscodeError(false);
                return;
            }
            showEditDoneProgress(true, true);
        }
        Utilities.globalQueue.postRunnable(new -$$Lambda$PassportActivity$aT2vRId8R9seAyB5eJdDvQVsURw(this, z, str));
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0037  */
    public /* synthetic */ void lambda$onPasswordDone$13$PassportActivity(boolean r11, java.lang.String r12) {
        /*
        r10 = this;
        r6 = new org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings;
        r6.<init>();
        r7 = 0;
        if (r11 == 0) goto L_0x000c;
    L_0x0008:
        r0 = r10.savedPasswordHash;
    L_0x000a:
        r8 = r0;
        goto L_0x0024;
    L_0x000c:
        r0 = r10.currentPassword;
        r0 = r0.current_algo;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
        if (r0 == 0) goto L_0x0023;
    L_0x0014:
        r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r12);
        r1 = r10.currentPassword;
        r1 = r1.current_algo;
        r1 = (org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) r1;
        r0 = org.telegram.messenger.SRPHelper.getX(r0, r1);
        goto L_0x000a;
    L_0x0023:
        r8 = r7;
    L_0x0024:
        r9 = new org.telegram.ui.PassportActivity$8;
        r0 = r9;
        r1 = r10;
        r2 = r11;
        r3 = r8;
        r4 = r6;
        r5 = r12;
        r0.<init>(r2, r3, r4, r5);
        r11 = r10.currentPassword;
        r12 = r11.current_algo;
        r0 = r12 instanceof org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
        if (r0 == 0) goto L_0x006c;
    L_0x0037:
        r12 = (org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) r12;
        r0 = r11.srp_id;
        r11 = r11.srp_B;
        r11 = org.telegram.messenger.SRPHelper.startCheck(r8, r0, r11, r12);
        r6.password = r11;
        r11 = r6.password;
        if (r11 != 0) goto L_0x0054;
    L_0x0047:
        r11 = new org.telegram.tgnet.TLRPC$TL_error;
        r11.<init>();
        r12 = "ALGO_INVALID";
        r11.text = r12;
        r9.run(r7, r11);
        return;
    L_0x0054:
        r11 = r10.currentAccount;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11);
        r12 = 10;
        r11 = r11.sendRequest(r6, r9, r12);
        r12 = r10.currentAccount;
        r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12);
        r0 = r10.classGuid;
        r12.bindRequestToGuid(r11, r0);
        goto L_0x0078;
    L_0x006c:
        r11 = new org.telegram.tgnet.TLRPC$TL_error;
        r11.<init>();
        r12 = "PASSWORD_HASH_INVALID";
        r11.text = r12;
        r9.run(r7, r11);
    L_0x0078:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$onPasswordDone$13$PassportActivity(boolean, java.lang.String):void");
    }

    private boolean isPersonalDocument(SecureValueType secureValueType) {
        return (secureValueType instanceof TL_secureValueTypeDriverLicense) || (secureValueType instanceof TL_secureValueTypePassport) || (secureValueType instanceof TL_secureValueTypeInternalPassport) || (secureValueType instanceof TL_secureValueTypeIdentityCard);
    }

    private boolean isAddressDocument(SecureValueType secureValueType) {
        return (secureValueType instanceof TL_secureValueTypeUtilityBill) || (secureValueType instanceof TL_secureValueTypeBankStatement) || (secureValueType instanceof TL_secureValueTypePassportRegistration) || (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) || (secureValueType instanceof TL_secureValueTypeRentalAgreement);
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x0201  */
    private void createRequestInterface(android.content.Context r24) {
        /*
        r23 = this;
        r6 = r23;
        r7 = r24;
        r0 = r6.currentForm;
        r9 = 0;
        if (r0 == 0) goto L_0x0029;
    L_0x0009:
        r0 = 0;
    L_0x000a:
        r1 = r6.currentForm;
        r1 = r1.users;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x0029;
    L_0x0014:
        r1 = r6.currentForm;
        r1 = r1.users;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r2 = r1.id;
        r3 = r6.currentBotId;
        if (r2 != r3) goto L_0x0026;
    L_0x0024:
        r10 = r1;
        goto L_0x002a;
    L_0x0026:
        r0 = r0 + 1;
        goto L_0x000a;
    L_0x0029:
        r10 = 0;
    L_0x002a:
        r0 = r6.fragmentView;
        r11 = r0;
        r11 = (android.widget.FrameLayout) r11;
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0d09a8 float:1.8747129E38 double:1.053130999E-314;
        r2 = "TelegramPassport";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r0 = r6.actionBar;
        r0 = r0.createMenu();
        r1 = NUM; // 0x7var_d float:1.7945805E38 double:1.052935802E-314;
        r12 = 1;
        r0.addItem(r12, r1);
        r13 = "windowBackgroundGrayShadow";
        r14 = -2;
        r15 = -1;
        if (r10 == 0) goto L_0x00e1;
    L_0x0051:
        r0 = new android.widget.FrameLayout;
        r0.<init>(r7);
        r1 = r6.linearLayout2;
        r2 = 100;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r2);
        r1.addView(r0, r2);
        r1 = new org.telegram.ui.Components.BackupImageView;
        r1.<init>(r7);
        r2 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.setRoundRadius(r2);
        r16 = 64;
        r17 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r18 = 17;
        r19 = 0;
        r20 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r21 = 0;
        r22 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r0.addView(r1, r2);
        r0 = new org.telegram.ui.Components.AvatarDrawable;
        r0.<init>(r10);
        r2 = org.telegram.messenger.ImageLocation.getForUser(r10, r9);
        r3 = "50_50";
        r1.setImage(r2, r3, r0, r10);
        r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0.<init>(r7);
        r6.bottomCell = r0;
        r0 = r6.bottomCell;
        r1 = NUM; // 0x7var_e6 float:1.7945044E38 double:1.0529356167E-314;
        r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r7, r1, r13);
        r0.setBackgroundDrawable(r1);
        r0 = r6.bottomCell;
        r1 = NUM; // 0x7f0d077a float:1.8745997E38 double:1.053130723E-314;
        r2 = new java.lang.Object[r12];
        r3 = org.telegram.messenger.UserObject.getFirstName(r10);
        r2[r9] = r3;
        r3 = "PassportRequest";
        r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r0.setText(r1);
        r0 = r6.bottomCell;
        r0 = r0.getTextView();
        r0.setGravity(r12);
        r0 = r6.bottomCell;
        r0 = r0.getTextView();
        r0 = r0.getLayoutParams();
        r0 = (android.widget.FrameLayout.LayoutParams) r0;
        r0.gravity = r12;
        r0 = r6.linearLayout2;
        r1 = r6.bottomCell;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14);
        r0.addView(r1, r2);
    L_0x00e1:
        r0 = new org.telegram.ui.Cells.HeaderCell;
        r0.<init>(r7);
        r6.headerCell = r0;
        r0 = r6.headerCell;
        r1 = NUM; // 0x7f0d077c float:1.8746E38 double:1.053130724E-314;
        r2 = "PassportRequestedInformation";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        r0 = r6.headerCell;
        r1 = "windowBackgroundWhite";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setBackgroundColor(r1);
        r0 = r6.linearLayout2;
        r1 = r6.headerCell;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14);
        r0.addView(r1, r2);
        r0 = r6.currentForm;
        if (r0 == 0) goto L_0x0316;
    L_0x0111:
        r0 = r0.required_types;
        r5 = r0.size();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r0 = 0;
        r1 = 0;
        r2 = 0;
        r8 = 0;
        r16 = 0;
    L_0x0127:
        if (r0 >= r5) goto L_0x01ea;
    L_0x0129:
        r14 = r6.currentForm;
        r14 = r14.required_types;
        r14 = r14.get(r0);
        r14 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r14;
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r15 == 0) goto L_0x0166;
    L_0x0137:
        r14 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r14;
        r15 = r14.type;
        r15 = r6.isPersonalDocument(r15);
        if (r15 == 0) goto L_0x0146;
    L_0x0141:
        r4.add(r14);
        goto L_0x01af;
    L_0x0146:
        r15 = r14.type;
        r15 = r6.isAddressDocument(r15);
        if (r15 == 0) goto L_0x0154;
    L_0x014e:
        r3.add(r14);
        r8 = r8 + 1;
        goto L_0x0162;
    L_0x0154:
        r14 = r14.type;
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        if (r15 == 0) goto L_0x015c;
    L_0x015a:
        r1 = 1;
        goto L_0x0162;
    L_0x015c:
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r14 == 0) goto L_0x0162;
    L_0x0160:
        r16 = 1;
    L_0x0162:
        r22 = r4;
        goto L_0x01e0;
    L_0x0166:
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf;
        if (r15 == 0) goto L_0x0162;
    L_0x016a:
        r14 = (org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r14;
        r15 = r14.types;
        r15 = r15.isEmpty();
        if (r15 == 0) goto L_0x0175;
    L_0x0174:
        goto L_0x0162;
    L_0x0175:
        r15 = r14.types;
        r15 = r15.get(r9);
        r15 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r15;
        r9 = r15 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r9 != 0) goto L_0x0182;
    L_0x0181:
        goto L_0x0162;
    L_0x0182:
        r15 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r15;
        r9 = r15.type;
        r9 = r6.isPersonalDocument(r9);
        if (r9 == 0) goto L_0x01b2;
    L_0x018c:
        r9 = r14.types;
        r9 = r9.size();
        r15 = 0;
    L_0x0193:
        if (r15 >= r9) goto L_0x01af;
    L_0x0195:
        r12 = r14.types;
        r12 = r12.get(r15);
        r12 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r12;
        r22 = r9;
        r9 = r12 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r9 != 0) goto L_0x01a4;
    L_0x01a3:
        goto L_0x01a9;
    L_0x01a4:
        r12 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r12;
        r4.add(r12);
    L_0x01a9:
        r15 = r15 + 1;
        r9 = r22;
        r12 = 1;
        goto L_0x0193;
    L_0x01af:
        r2 = r2 + 1;
        goto L_0x0162;
    L_0x01b2:
        r9 = r15.type;
        r9 = r6.isAddressDocument(r9);
        if (r9 == 0) goto L_0x0162;
    L_0x01ba:
        r9 = r14.types;
        r9 = r9.size();
        r12 = 0;
    L_0x01c1:
        if (r12 >= r9) goto L_0x01dc;
    L_0x01c3:
        r15 = r14.types;
        r15 = r15.get(r12);
        r15 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r15;
        r22 = r4;
        r4 = r15 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r4 != 0) goto L_0x01d2;
    L_0x01d1:
        goto L_0x01d7;
    L_0x01d2:
        r15 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r15;
        r3.add(r15);
    L_0x01d7:
        r12 = r12 + 1;
        r4 = r22;
        goto L_0x01c1;
    L_0x01dc:
        r22 = r4;
        r8 = r8 + 1;
    L_0x01e0:
        r0 = r0 + 1;
        r4 = r22;
        r9 = 0;
        r12 = 1;
        r14 = -2;
        r15 = -1;
        goto L_0x0127;
    L_0x01ea:
        r22 = r4;
        if (r1 == 0) goto L_0x01f4;
    L_0x01ee:
        r0 = 1;
        if (r2 <= r0) goto L_0x01f2;
    L_0x01f1:
        goto L_0x01f5;
    L_0x01f2:
        r9 = 0;
        goto L_0x01f6;
    L_0x01f4:
        r0 = 1;
    L_0x01f5:
        r9 = 1;
    L_0x01f6:
        if (r16 == 0) goto L_0x01fd;
    L_0x01f8:
        if (r8 <= r0) goto L_0x01fb;
    L_0x01fa:
        goto L_0x01fd;
    L_0x01fb:
        r8 = 0;
        goto L_0x01fe;
    L_0x01fd:
        r8 = 1;
    L_0x01fe:
        r12 = 0;
    L_0x01ff:
        if (r12 >= r5) goto L_0x0316;
    L_0x0201:
        r0 = r6.currentForm;
        r0 = r0.required_types;
        r0 = r0.get(r12);
        r0 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r0;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r1 == 0) goto L_0x0277;
    L_0x020f:
        r0 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r0;
        r1 = r0.type;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
        if (r2 != 0) goto L_0x0272;
    L_0x0217:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
        if (r2 == 0) goto L_0x021c;
    L_0x021b:
        goto L_0x0272;
    L_0x021c:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        if (r2 == 0) goto L_0x0229;
    L_0x0220:
        if (r9 == 0) goto L_0x0224;
    L_0x0222:
        r1 = 0;
        goto L_0x0226;
    L_0x0224:
        r1 = r22;
    L_0x0226:
        r2 = r0;
        r4 = r1;
        goto L_0x0274;
    L_0x0229:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r2 == 0) goto L_0x0232;
    L_0x022d:
        if (r8 == 0) goto L_0x0230;
    L_0x022f:
        goto L_0x0222;
    L_0x0230:
        r1 = r3;
        goto L_0x0226;
    L_0x0232:
        if (r9 == 0) goto L_0x0253;
    L_0x0234:
        r1 = r6.isPersonalDocument(r1);
        if (r1 == 0) goto L_0x0253;
    L_0x023a:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r1.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r0.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
        r2.<init>();
        r0.type = r2;
    L_0x024e:
        r2 = r0;
        r4 = r1;
        r14 = 1;
        goto L_0x02f7;
    L_0x0253:
        if (r8 == 0) goto L_0x030c;
    L_0x0255:
        r1 = r0.type;
        r1 = r6.isAddressDocument(r1);
        if (r1 == 0) goto L_0x030c;
    L_0x025d:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r1.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r0.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
        r2.<init>();
        r0.type = r2;
        goto L_0x024e;
    L_0x0272:
        r2 = r0;
        r4 = 0;
    L_0x0274:
        r14 = 0;
        goto L_0x02f7;
    L_0x0277:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf;
        if (r1 == 0) goto L_0x030c;
    L_0x027b:
        r0 = (org.telegram.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r0;
        r1 = r0.types;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x0287;
    L_0x0285:
        goto L_0x030c;
    L_0x0287:
        r1 = r0.types;
        r2 = 0;
        r1 = r1.get(r2);
        r1 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r1;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r2 != 0) goto L_0x0296;
    L_0x0294:
        goto L_0x030c;
    L_0x0296:
        r1 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r1;
        if (r9 == 0) goto L_0x02a2;
    L_0x029a:
        r2 = r1.type;
        r2 = r6.isPersonalDocument(r2);
        if (r2 != 0) goto L_0x02ac;
    L_0x02a2:
        if (r8 == 0) goto L_0x030c;
    L_0x02a4:
        r2 = r1.type;
        r2 = r6.isAddressDocument(r2);
        if (r2 == 0) goto L_0x030c;
    L_0x02ac:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r4 = r0.types;
        r4 = r4.size();
        r14 = 0;
    L_0x02b8:
        if (r14 >= r4) goto L_0x02d3;
    L_0x02ba:
        r15 = r0.types;
        r15 = r15.get(r14);
        r15 = (org.telegram.tgnet.TLRPC.SecureRequiredType) r15;
        r16 = r0;
        r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_secureRequiredType;
        if (r0 != 0) goto L_0x02c9;
    L_0x02c8:
        goto L_0x02ce;
    L_0x02c9:
        r15 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r15;
        r2.add(r15);
    L_0x02ce:
        r14 = r14 + 1;
        r0 = r16;
        goto L_0x02b8;
    L_0x02d3:
        r0 = r1.type;
        r0 = r6.isPersonalDocument(r0);
        if (r0 == 0) goto L_0x02e8;
    L_0x02db:
        r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r0.<init>();
        r1 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
        r1.<init>();
        r0.type = r1;
        goto L_0x02f4;
    L_0x02e8:
        r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r0.<init>();
        r1 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
        r1.<init>();
        r0.type = r1;
    L_0x02f4:
        r4 = r2;
        r14 = 1;
        r2 = r0;
    L_0x02f7:
        r0 = r5 + -1;
        if (r12 != r0) goto L_0x02fd;
    L_0x02fb:
        r15 = 1;
        goto L_0x02fe;
    L_0x02fd:
        r15 = 0;
    L_0x02fe:
        r0 = r23;
        r1 = r24;
        r16 = r3;
        r3 = r4;
        r4 = r14;
        r14 = r5;
        r5 = r15;
        r0.addField(r1, r2, r3, r4, r5);
        goto L_0x030f;
    L_0x030c:
        r16 = r3;
        r14 = r5;
    L_0x030f:
        r12 = r12 + 1;
        r5 = r14;
        r3 = r16;
        goto L_0x01ff;
    L_0x0316:
        if (r10 == 0) goto L_0x03d9;
    L_0x0318:
        r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0.<init>(r7);
        r6.bottomCell = r0;
        r0 = r6.bottomCell;
        r1 = NUM; // 0x7var_e5 float:1.7945042E38 double:1.052935616E-314;
        r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r7, r1, r13);
        r0.setBackgroundDrawable(r1);
        r0 = r6.bottomCell;
        r1 = "windowBackgroundWhiteGrayText4";
        r0.setLinkTextColorKey(r1);
        r0 = r6.currentForm;
        r0 = r0.privacy_policy_url;
        r0 = android.text.TextUtils.isEmpty(r0);
        r2 = 2;
        if (r0 != 0) goto L_0x0395;
    L_0x033e:
        r0 = NUM; // 0x7f0d0777 float:1.874599E38 double:1.0531307217E-314;
        r2 = new java.lang.Object[r2];
        r3 = org.telegram.messenger.UserObject.getFirstName(r10);
        r4 = 0;
        r2[r4] = r3;
        r3 = r10.username;
        r4 = 1;
        r2[r4] = r3;
        r3 = "PassportPolicy";
        r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2);
        r2 = new android.text.SpannableStringBuilder;
        r2.<init>(r0);
        r3 = 42;
        r4 = r0.indexOf(r3);
        r0 = r0.lastIndexOf(r3);
        r3 = -1;
        if (r4 == r3) goto L_0x038f;
    L_0x0367:
        if (r0 == r3) goto L_0x038f;
    L_0x0369:
        r3 = r6.bottomCell;
        r3 = r3.getTextView();
        r5 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy;
        r5.<init>();
        r3.setMovementMethod(r5);
        r3 = r0 + 1;
        r5 = "";
        r2.replace(r0, r3, r5);
        r3 = r4 + 1;
        r2.replace(r4, r3, r5);
        r3 = new org.telegram.ui.PassportActivity$LinkSpan;
        r3.<init>();
        r5 = 1;
        r0 = r0 - r5;
        r5 = 33;
        r2.setSpan(r3, r4, r0, r5);
    L_0x038f:
        r0 = r6.bottomCell;
        r0.setText(r2);
        goto L_0x03b5;
    L_0x0395:
        r0 = r6.bottomCell;
        r3 = NUM; // 0x7f0d076b float:1.8745966E38 double:1.053130716E-314;
        r2 = new java.lang.Object[r2];
        r4 = org.telegram.messenger.UserObject.getFirstName(r10);
        r5 = 0;
        r2[r5] = r4;
        r4 = r10.username;
        r5 = 1;
        r2[r5] = r4;
        r4 = "PassportNoPolicy";
        r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2);
        r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2);
        r0.setText(r2);
    L_0x03b5:
        r0 = r6.bottomCell;
        r0 = r0.getTextView();
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setHighlightColor(r1);
        r0 = r6.bottomCell;
        r0 = r0.getTextView();
        r1 = 1;
        r0.setGravity(r1);
        r0 = r6.linearLayout2;
        r1 = r6.bottomCell;
        r2 = -2;
        r3 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2);
        r0.addView(r1, r4);
    L_0x03d9:
        r0 = new android.widget.FrameLayout;
        r0.<init>(r7);
        r6.bottomLayout = r0;
        r0 = r6.bottomLayout;
        r1 = "passport_authorizeBackground";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = "passport_authorizeBackgroundSelected";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r1, r2);
        r0.setBackgroundDrawable(r1);
        r0 = r6.bottomLayout;
        r1 = 48;
        r2 = 80;
        r3 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1, r2);
        r11.addView(r0, r1);
        r0 = r6.bottomLayout;
        r1 = new org.telegram.ui.-$$Lambda$PassportActivity$YcmK_1FiSvvv-xWa8i2cDGfFT0Y;
        r1.<init>(r6);
        r0.setOnClickListener(r1);
        r0 = new android.widget.TextView;
        r0.<init>(r7);
        r6.acceptTextView = r0;
        r0 = r6.acceptTextView;
        r1 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.setCompoundDrawablePadding(r1);
        r0 = r6.acceptTextView;
        r1 = NUM; // 0x7var_ float:1.794481E38 double:1.0529355594E-314;
        r2 = 0;
        r0.setCompoundDrawablesWithIntrinsicBounds(r1, r2, r2, r2);
        r0 = r6.acceptTextView;
        r1 = "passport_authorizeText";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        r0 = r6.acceptTextView;
        r1 = NUM; // 0x7f0d0701 float:1.8745751E38 double:1.0531306634E-314;
        r2 = "PassportAuthorize";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        r0 = r6.acceptTextView;
        r1 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = 1;
        r0.setTextSize(r2, r1);
        r0 = r6.acceptTextView;
        r1 = 17;
        r0.setGravity(r1);
        r0 = r6.acceptTextView;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r0.setTypeface(r2);
        r0 = r6.bottomLayout;
        r2 = r6.acceptTextView;
        r3 = -2;
        r4 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r1);
        r0.addView(r2, r1);
        r0 = new org.telegram.ui.Components.ContextProgressView;
        r1 = 0;
        r0.<init>(r7, r1);
        r6.progressViewButton = r0;
        r0 = r6.progressViewButton;
        r1 = 4;
        r0.setVisibility(r1);
        r0 = r6.bottomLayout;
        r1 = r6.progressViewButton;
        r2 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r2);
        r0.addView(r1, r2);
        r0 = new android.view.View;
        r0.<init>(r7);
        r1 = NUM; // 0x7var_f2 float:1.7945069E38 double:1.0529356226E-314;
        r0.setBackgroundResource(r1);
        r12 = -1;
        r13 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r14 = 83;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18);
        r11.addView(r0, r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createRequestInterface(android.content.Context):void");
    }

    public /* synthetic */ void lambda$createRequestInterface$16$PassportActivity(View view) {
        String str = "";
        ArrayList arrayList = new ArrayList();
        int size = this.currentForm.required_types.size();
        int i = 0;
        while (i < size) {
            TL_secureRequiredType tL_secureRequiredType;
            SecureRequiredType secureRequiredType = (SecureRequiredType) this.currentForm.required_types.get(i);
            if (secureRequiredType instanceof TL_secureRequiredType) {
                tL_secureRequiredType = (TL_secureRequiredType) secureRequiredType;
            } else {
                if (secureRequiredType instanceof TL_secureRequiredTypeOneOf) {
                    TL_secureRequiredTypeOneOf tL_secureRequiredTypeOneOf = (TL_secureRequiredTypeOneOf) secureRequiredType;
                    if (tL_secureRequiredTypeOneOf.types.isEmpty()) {
                        continue;
                    } else {
                        SecureRequiredType secureRequiredType2 = (SecureRequiredType) tL_secureRequiredTypeOneOf.types.get(0);
                        if (secureRequiredType2 instanceof TL_secureRequiredType) {
                            TL_secureRequiredType tL_secureRequiredType2 = (TL_secureRequiredType) secureRequiredType2;
                            int size2 = tL_secureRequiredTypeOneOf.types.size();
                            for (int i2 = 0; i2 < size2; i2++) {
                                SecureRequiredType secureRequiredType3 = (SecureRequiredType) tL_secureRequiredTypeOneOf.types.get(i2);
                                if (secureRequiredType3 instanceof TL_secureRequiredType) {
                                    TL_secureRequiredType tL_secureRequiredType3 = (TL_secureRequiredType) secureRequiredType3;
                                    if (getValueByType(tL_secureRequiredType3, true) != null) {
                                        tL_secureRequiredType = tL_secureRequiredType3;
                                        break;
                                    }
                                }
                            }
                            tL_secureRequiredType = tL_secureRequiredType2;
                        } else {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
                i++;
            }
            TL_secureValue valueByType = getValueByType(tL_secureRequiredType, true);
            String str2 = "vibrator";
            Vibrator vibrator;
            if (valueByType == null) {
                vibrator = (Vibrator) getParentActivity().getSystemService(str2);
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(getViewByType(tL_secureRequiredType), 2.0f, 0);
                return;
            }
            HashMap hashMap = (HashMap) this.errorsMap.get(getNameForType(tL_secureRequiredType.type));
            if (hashMap == null || hashMap.isEmpty()) {
                arrayList.add(new AnonymousClass1ValueToSend(valueByType, tL_secureRequiredType.selfie_required, tL_secureRequiredType.translation_required));
                i++;
            } else {
                vibrator = (Vibrator) getParentActivity().getSystemService(str2);
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(getViewByType(tL_secureRequiredType), 2.0f, 0);
                return;
            }
        }
        showEditDoneProgress(false, true);
        TL_account_acceptAuthorization tL_account_acceptAuthorization = new TL_account_acceptAuthorization();
        tL_account_acceptAuthorization.bot_id = this.currentBotId;
        tL_account_acceptAuthorization.scope = this.currentScope;
        tL_account_acceptAuthorization.public_key = this.currentPublicKey;
        JSONObject jSONObject = new JSONObject();
        int size3 = arrayList.size();
        int i3 = 0;
        while (i3 < size3) {
            ArrayList arrayList2;
            int i4;
            TL_secureValueHash tL_secureValueHash;
            AnonymousClass1ValueToSend anonymousClass1ValueToSend = (AnonymousClass1ValueToSend) arrayList.get(i3);
            TL_secureValue tL_secureValue = anonymousClass1ValueToSend.value;
            JSONObject jSONObject2 = new JSONObject();
            SecurePlainData securePlainData = tL_secureValue.plain_data;
            if (securePlainData == null) {
                try {
                    JSONObject jSONObject3;
                    TL_secureFile tL_secureFile;
                    byte[] decryptValueSecret;
                    JSONObject jSONObject4 = new JSONObject();
                    String str3 = "secret";
                    if (tL_secureValue.data != null) {
                        byte[] decryptValueSecret2 = decryptValueSecret(tL_secureValue.data.secret, tL_secureValue.data.data_hash);
                        jSONObject2.put("data_hash", Base64.encodeToString(tL_secureValue.data.data_hash, 2));
                        jSONObject2.put(str3, Base64.encodeToString(decryptValueSecret2, 2));
                        jSONObject4.put("data", jSONObject2);
                    }
                    String str4 = "file_hash";
                    if (tL_secureValue.files.isEmpty()) {
                        arrayList2 = arrayList;
                        i4 = size3;
                    } else {
                        JSONArray jSONArray = new JSONArray();
                        int size4 = tL_secureValue.files.size();
                        int i5 = 0;
                        while (i5 < size4) {
                            TL_secureFile tL_secureFile2 = (TL_secureFile) tL_secureValue.files.get(i5);
                            arrayList2 = arrayList;
                            try {
                                i4 = size3;
                                try {
                                    byte[] decryptValueSecret3 = decryptValueSecret(tL_secureFile2.secret, tL_secureFile2.file_hash);
                                    jSONObject3 = new JSONObject();
                                    int i6 = size4;
                                    jSONObject3.put(str4, Base64.encodeToString(tL_secureFile2.file_hash, 2));
                                    jSONObject3.put(str3, Base64.encodeToString(decryptValueSecret3, 2));
                                    jSONArray.put(jSONObject3);
                                    i5++;
                                    arrayList = arrayList2;
                                    size3 = i4;
                                    size4 = i6;
                                } catch (Exception unused) {
                                }
                            } catch (Exception unused2) {
                            }
                        }
                        arrayList2 = arrayList;
                        i4 = size3;
                        jSONObject4.put("files", jSONArray);
                    }
                    if (tL_secureValue.front_side instanceof TL_secureFile) {
                        tL_secureFile = (TL_secureFile) tL_secureValue.front_side;
                        decryptValueSecret = decryptValueSecret(tL_secureFile.secret, tL_secureFile.file_hash);
                        jSONObject3 = new JSONObject();
                        jSONObject3.put(str4, Base64.encodeToString(tL_secureFile.file_hash, 2));
                        jSONObject3.put(str3, Base64.encodeToString(decryptValueSecret, 2));
                        jSONObject4.put("front_side", jSONObject3);
                    }
                    if (tL_secureValue.reverse_side instanceof TL_secureFile) {
                        tL_secureFile = (TL_secureFile) tL_secureValue.reverse_side;
                        decryptValueSecret = decryptValueSecret(tL_secureFile.secret, tL_secureFile.file_hash);
                        jSONObject3 = new JSONObject();
                        jSONObject3.put(str4, Base64.encodeToString(tL_secureFile.file_hash, 2));
                        jSONObject3.put(str3, Base64.encodeToString(decryptValueSecret, 2));
                        jSONObject4.put("reverse_side", jSONObject3);
                    }
                    if (anonymousClass1ValueToSend.selfie_required && (tL_secureValue.selfie instanceof TL_secureFile)) {
                        tL_secureFile = (TL_secureFile) tL_secureValue.selfie;
                        decryptValueSecret = decryptValueSecret(tL_secureFile.secret, tL_secureFile.file_hash);
                        jSONObject3 = new JSONObject();
                        jSONObject3.put(str4, Base64.encodeToString(tL_secureFile.file_hash, 2));
                        jSONObject3.put(str3, Base64.encodeToString(decryptValueSecret, 2));
                        jSONObject4.put("selfie", jSONObject3);
                    }
                    if (anonymousClass1ValueToSend.translation_required && !tL_secureValue.translation.isEmpty()) {
                        JSONArray jSONArray2 = new JSONArray();
                        int size5 = tL_secureValue.translation.size();
                        for (size3 = 0; size3 < size5; size3++) {
                            TL_secureFile tL_secureFile3 = (TL_secureFile) tL_secureValue.translation.get(size3);
                            byte[] decryptValueSecret4 = decryptValueSecret(tL_secureFile3.secret, tL_secureFile3.file_hash);
                            JSONObject jSONObject5 = new JSONObject();
                            jSONObject5.put(str4, Base64.encodeToString(tL_secureFile3.file_hash, 2));
                            jSONObject5.put(str3, Base64.encodeToString(decryptValueSecret4, 2));
                            jSONArray2.put(jSONObject5);
                        }
                        jSONObject4.put("translation", jSONArray2);
                    }
                    jSONObject.put(getNameForType(tL_secureValue.type), jSONObject4);
                } catch (Exception unused3) {
                }
                tL_secureValueHash = new TL_secureValueHash();
                tL_secureValueHash.type = tL_secureValue.type;
                tL_secureValueHash.hash = tL_secureValue.hash;
                tL_account_acceptAuthorization.value_hashes.add(tL_secureValueHash);
                i3++;
                arrayList = arrayList2;
                size3 = i4;
            } else if (securePlainData instanceof TL_securePlainEmail) {
                TL_securePlainEmail tL_securePlainEmail = (TL_securePlainEmail) securePlainData;
            } else if (securePlainData instanceof TL_securePlainPhone) {
                TL_securePlainPhone tL_securePlainPhone = (TL_securePlainPhone) securePlainData;
            }
            arrayList2 = arrayList;
            i4 = size3;
            tL_secureValueHash = new TL_secureValueHash();
            tL_secureValueHash.type = tL_secureValue.type;
            tL_secureValueHash.hash = tL_secureValue.hash;
            tL_account_acceptAuthorization.value_hashes.add(tL_secureValueHash);
            i3++;
            arrayList = arrayList2;
            size3 = i4;
        }
        JSONObject jSONObject6 = new JSONObject();
        try {
            jSONObject6.put("secure_data", jSONObject);
        } catch (Exception unused4) {
        }
        String str5 = this.currentPayload;
        if (str5 != null) {
            try {
                jSONObject6.put("payload", str5);
            } catch (Exception unused5) {
            }
        }
        str5 = this.currentNonce;
        if (str5 != null) {
            try {
                jSONObject6.put("nonce", str5);
            } catch (Exception unused6) {
            }
        }
        EncryptionResult encryptData = encryptData(AndroidUtilities.getStringBytes(jSONObject6.toString()));
        tL_account_acceptAuthorization.credentials = new TL_secureCredentialsEncrypted();
        TL_secureCredentialsEncrypted tL_secureCredentialsEncrypted = tL_account_acceptAuthorization.credentials;
        tL_secureCredentialsEncrypted.hash = encryptData.fileHash;
        tL_secureCredentialsEncrypted.data = encryptData.encryptedData;
        try {
            RSAPublicKey rSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(this.currentPublicKey.replaceAll("\\n", str).replace("-----BEGIN PUBLIC KEY-----", str).replace("-----END PUBLIC KEY-----", str), 0)));
            Cipher instance = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
            instance.init(1, rSAPublicKey);
            tL_account_acceptAuthorization.credentials.secret = instance.doFinal(encryptData.decrypyedFileSecret);
        } catch (Exception e) {
            FileLog.e(e);
        }
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_acceptAuthorization, new -$$Lambda$PassportActivity$OXx8osKL-PnehH20iIhS6RoDsW4(this)), this.classGuid);
    }

    public /* synthetic */ void lambda$null$15$PassportActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$8rAsiN00Bfu9wX2d3bVxDe5-3UA(this, tL_error));
    }

    public /* synthetic */ void lambda$null$14$PassportActivity(TL_error tL_error) {
        if (tL_error == null) {
            this.ignoreOnFailure = true;
            callCallback(true);
            finishFragment();
            return;
        }
        showEditDoneProgress(false, false);
        if ("APP_VERSION_OUTDATED".equals(tL_error.text)) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tL_error.text);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x02e7  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x02e5  */
    private void createManageInterface(android.content.Context r20) {
        /*
        r19 = this;
        r6 = r19;
        r7 = r20;
        r0 = r6.fragmentView;
        r0 = (android.widget.FrameLayout) r0;
        r0 = r6.actionBar;
        r1 = "TelegramPassport";
        r2 = NUM; // 0x7f0d09a8 float:1.8747129E38 double:1.053130999E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r0.setTitle(r1);
        r0 = r6.actionBar;
        r0 = r0.createMenu();
        r8 = 1;
        r1 = NUM; // 0x7var_d float:1.7945805E38 double:1.052935802E-314;
        r0.addItem(r8, r1);
        r0 = new org.telegram.ui.Cells.HeaderCell;
        r0.<init>(r7);
        r6.headerCell = r0;
        r0 = r6.headerCell;
        r1 = "PassportProvidedInformation";
        r2 = NUM; // 0x7f0d0779 float:1.8745995E38 double:1.0531307227E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r0.setText(r1);
        r0 = r6.headerCell;
        r1 = "windowBackgroundWhite";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setBackgroundColor(r1);
        r0 = r6.linearLayout2;
        r1 = r6.headerCell;
        r2 = -2;
        r3 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2);
        r0.addView(r1, r4);
        r0 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0.<init>(r7);
        r6.sectionCell = r0;
        r0 = r6.sectionCell;
        r1 = "windowBackgroundGrayShadow";
        r4 = NUM; // 0x7var_e4 float:1.794504E38 double:1.0529356157E-314;
        r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r7, r4, r1);
        r0.setBackgroundDrawable(r4);
        r0 = r6.linearLayout2;
        r4 = r6.sectionCell;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2);
        r0.addView(r4, r5);
        r0 = new org.telegram.ui.Cells.TextSettingsCell;
        r0.<init>(r7);
        r6.addDocumentCell = r0;
        r0 = r6.addDocumentCell;
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r0.setBackgroundDrawable(r4);
        r0 = r6.addDocumentCell;
        r4 = NUM; // 0x7f0d0768 float:1.874596E38 double:1.0531307143E-314;
        r5 = "PassportNoDocumentsAdd";
        r9 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r0.setText(r9, r8);
        r0 = r6.linearLayout2;
        r9 = r6.addDocumentCell;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2);
        r0.addView(r9, r10);
        r0 = r6.addDocumentCell;
        r9 = new org.telegram.ui.-$$Lambda$PassportActivity$yv3E0a1LzZcIvpbSmJY3xiJFxqI;
        r9.<init>(r6);
        r0.setOnClickListener(r9);
        r0 = new org.telegram.ui.Cells.TextSettingsCell;
        r0.<init>(r7);
        r6.deletePassportCell = r0;
        r0 = r6.deletePassportCell;
        r9 = "windowBackgroundWhiteRedText3";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r0.setTextColor(r9);
        r0 = r6.deletePassportCell;
        r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r0.setBackgroundDrawable(r9);
        r0 = r6.deletePassportCell;
        r9 = "TelegramPassportDelete";
        r10 = NUM; // 0x7f0d09ab float:1.8747135E38 double:1.0531310004E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);
        r10 = 0;
        r0.setText(r9, r10);
        r0 = r6.linearLayout2;
        r9 = r6.deletePassportCell;
        r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2);
        r0.addView(r9, r11);
        r0 = r6.deletePassportCell;
        r9 = new org.telegram.ui.-$$Lambda$PassportActivity$CCLsFyBhbKH_We0aA4khftAzZjg;
        r9.<init>(r6);
        r0.setOnClickListener(r9);
        r0 = new org.telegram.ui.Cells.ShadowSectionCell;
        r0.<init>(r7);
        r6.addDocumentSectionCell = r0;
        r0 = r6.addDocumentSectionCell;
        r9 = NUM; // 0x7var_e5 float:1.7945042E38 double:1.052935616E-314;
        r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r7, r9, r1);
        r0.setBackgroundDrawable(r11);
        r0 = r6.linearLayout2;
        r11 = r6.addDocumentSectionCell;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2);
        r0.addView(r11, r12);
        r0 = new android.widget.LinearLayout;
        r0.<init>(r7);
        r6.emptyLayout = r0;
        r0 = r6.emptyLayout;
        r0.setOrientation(r8);
        r0 = r6.emptyLayout;
        r11 = 17;
        r0.setGravity(r11);
        r0 = r6.emptyLayout;
        r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r7, r9, r1);
        r0.setBackgroundDrawable(r1);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x013d;
    L_0x0125:
        r0 = r6.linearLayout2;
        r1 = r6.emptyLayout;
        r9 = new android.widget.LinearLayout$LayoutParams;
        r12 = NUM; // 0x44040000 float:528.0 double:5.637846483E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r13 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        r12 = r12 - r13;
        r9.<init>(r3, r12);
        r0.addView(r1, r9);
        goto L_0x0152;
    L_0x013d:
        r0 = r6.linearLayout2;
        r1 = r6.emptyLayout;
        r9 = new android.widget.LinearLayout$LayoutParams;
        r12 = org.telegram.messenger.AndroidUtilities.displaySize;
        r12 = r12.y;
        r13 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        r12 = r12 - r13;
        r9.<init>(r3, r12);
        r0.addView(r1, r9);
    L_0x0152:
        r0 = new android.widget.ImageView;
        r0.<init>(r7);
        r6.emptyImageView = r0;
        r0 = r6.emptyImageView;
        r1 = NUM; // 0x7var_ float:1.7945627E38 double:1.0529357585E-314;
        r0.setImageResource(r1);
        r0 = r6.emptyImageView;
        r1 = new android.graphics.PorterDuffColorFilter;
        r3 = "sessions_devicesImage";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r1.<init>(r3, r9);
        r0.setColorFilter(r1);
        r0 = r6.emptyLayout;
        r1 = r6.emptyImageView;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r2);
        r0.addView(r1, r2);
        r0 = new android.widget.TextView;
        r0.<init>(r7);
        r6.emptyTextView1 = r0;
        r0 = r6.emptyTextView1;
        r1 = "windowBackgroundWhiteGrayText2";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r2);
        r0 = r6.emptyTextView1;
        r0.setGravity(r11);
        r0 = r6.emptyTextView1;
        r2 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r0.setTextSize(r8, r2);
        r0 = r6.emptyTextView1;
        r3 = "fonts/rmedium.ttf";
        r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r0.setTypeface(r9);
        r0 = r6.emptyTextView1;
        r9 = NUM; // 0x7f0d0767 float:1.8745958E38 double:1.053130714E-314;
        r12 = "PassportNoDocuments";
        r9 = org.telegram.messenger.LocaleController.getString(r12, r9);
        r0.setText(r9);
        r0 = r6.emptyLayout;
        r9 = r6.emptyTextView1;
        r12 = -2;
        r13 = -2;
        r14 = 17;
        r15 = 0;
        r16 = 16;
        r17 = 0;
        r18 = 0;
        r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r13, r14, r15, r16, r17, r18);
        r0.addView(r9, r12);
        r0 = new android.widget.TextView;
        r0.<init>(r7);
        r6.emptyTextView2 = r0;
        r0 = r6.emptyTextView2;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        r0 = r6.emptyTextView2;
        r0.setGravity(r11);
        r0 = r6.emptyTextView2;
        r1 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0.setTextSize(r8, r1);
        r0 = r6.emptyTextView2;
        r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.setPadding(r9, r10, r1, r10);
        r0 = r6.emptyTextView2;
        r1 = NUM; // 0x7f0d0769 float:1.8745962E38 double:1.053130715E-314;
        r9 = "PassportNoDocumentsInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r9, r1);
        r0.setText(r1);
        r0 = r6.emptyLayout;
        r1 = r6.emptyTextView2;
        r12 = -2;
        r16 = 14;
        r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r13, r14, r15, r16, r17, r18);
        r0.addView(r1, r9);
        r0 = new android.widget.TextView;
        r0.<init>(r7);
        r6.emptyTextView3 = r0;
        r0 = r6.emptyTextView3;
        r1 = "windowBackgroundWhiteBlueText4";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        r0 = r6.emptyTextView3;
        r0.setGravity(r11);
        r0 = r6.emptyTextView3;
        r0.setTextSize(r8, r2);
        r0 = r6.emptyTextView3;
        r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r0.setTypeface(r1);
        r0 = r6.emptyTextView3;
        r0.setGravity(r11);
        r0 = r6.emptyTextView3;
        r1 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r1 = r1.toUpperCase();
        r0.setText(r1);
        r0 = r6.emptyLayout;
        r1 = r6.emptyTextView3;
        r11 = -2;
        r12 = 30;
        r13 = 17;
        r14 = 0;
        r15 = 16;
        r16 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16, r17);
        r0.addView(r1, r2);
        r0 = r6.emptyTextView3;
        r1 = new org.telegram.ui.-$$Lambda$PassportActivity$7cIXxuOb-yUfSpLF2ezyFpul-wg;
        r1.<init>(r6);
        r0.setOnClickListener(r1);
        r0 = r6.currentForm;
        r0 = r0.values;
        r9 = r0.size();
        r11 = 0;
    L_0x0273:
        if (r11 >= r9) goto L_0x02f2;
    L_0x0275:
        r0 = r6.currentForm;
        r0 = r0.values;
        r0 = r0.get(r11);
        r0 = (org.telegram.tgnet.TLRPC.TL_secureValue) r0;
        r1 = r0.type;
        r1 = r6.isPersonalDocument(r1);
        if (r1 == 0) goto L_0x02ac;
    L_0x0287:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r2.<init>();
        r0 = r0.type;
        r2.type = r0;
        r2.selfie_required = r8;
        r2.translation_required = r8;
        r1.add(r2);
        r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r0.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
        r2.<init>();
        r0.type = r2;
    L_0x02a8:
        r2 = r0;
        r3 = r1;
        r4 = 1;
        goto L_0x02e1;
    L_0x02ac:
        r1 = r0.type;
        r1 = r6.isAddressDocument(r1);
        if (r1 == 0) goto L_0x02d4;
    L_0x02b4:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r2.<init>();
        r0 = r0.type;
        r2.type = r0;
        r2.translation_required = r8;
        r1.add(r2);
        r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r0.<init>();
        r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
        r2.<init>();
        r0.type = r2;
        goto L_0x02a8;
    L_0x02d4:
        r1 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType;
        r1.<init>();
        r0 = r0.type;
        r1.type = r0;
        r0 = 0;
        r3 = r0;
        r2 = r1;
        r4 = 0;
    L_0x02e1:
        r0 = r9 + -1;
        if (r11 != r0) goto L_0x02e7;
    L_0x02e5:
        r5 = 1;
        goto L_0x02e8;
    L_0x02e7:
        r5 = 0;
    L_0x02e8:
        r0 = r19;
        r1 = r20;
        r0.addField(r1, r2, r3, r4, r5);
        r11 = r11 + 1;
        goto L_0x0273;
    L_0x02f2:
        r19.updateManageVisibility();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createManageInterface(android.content.Context):void");
    }

    public /* synthetic */ void lambda$createManageInterface$17$PassportActivity(View view) {
        openAddDocumentAlert();
    }

    public /* synthetic */ void lambda$createManageInterface$21$PassportActivity(View view) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PassportActivity$TerssJhszDCLASSNAMEoVgt9PLJkXFg6k(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("TelegramPassportDeleteAlert", NUM));
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$20$PassportActivity(DialogInterface dialogInterface, int i) {
        TL_account_deleteSecureValue tL_account_deleteSecureValue = new TL_account_deleteSecureValue();
        for (i = 0; i < this.currentForm.values.size(); i++) {
            tL_account_deleteSecureValue.types.add(((TL_secureValue) this.currentForm.values.get(i)).type);
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_deleteSecureValue, new -$$Lambda$PassportActivity$VYlmj64RpYyoUgzz406EWs6DcyI(this));
    }

    public /* synthetic */ void lambda$null$19$PassportActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$1OMOcheofF5mpc6UFu_0Xy9UK6I(this));
    }

    public /* synthetic */ void lambda$null$18$PassportActivity() {
        int i = 0;
        while (i < this.linearLayout2.getChildCount()) {
            View childAt = this.linearLayout2.getChildAt(i);
            if (childAt instanceof TextDetailSecureCell) {
                this.linearLayout2.removeView(childAt);
                i--;
            }
            i++;
        }
        needHideProgress();
        this.typesViews.clear();
        this.typesValues.clear();
        this.currentForm.values.clear();
        updateManageVisibility();
    }

    public /* synthetic */ void lambda$createManageInterface$22$PassportActivity(View view) {
        openAddDocumentAlert();
    }

    private boolean hasNotValueForType(Class<? extends SecureValueType> cls) {
        int size = this.currentForm.values.size();
        for (int i = 0; i < size; i++) {
            if (((TL_secureValue) this.currentForm.values.get(i)).type.getClass() == cls) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnfilledValues() {
        return hasNotValueForType(TL_secureValueTypePhone.class) || hasNotValueForType(TL_secureValueTypeEmail.class) || hasNotValueForType(TL_secureValueTypePersonalDetails.class) || hasNotValueForType(TL_secureValueTypePassport.class) || hasNotValueForType(TL_secureValueTypeInternalPassport.class) || hasNotValueForType(TL_secureValueTypeIdentityCard.class) || hasNotValueForType(TL_secureValueTypeDriverLicense.class) || hasNotValueForType(TL_secureValueTypeAddress.class) || hasNotValueForType(TL_secureValueTypeUtilityBill.class) || hasNotValueForType(TL_secureValueTypePassportRegistration.class) || hasNotValueForType(TL_secureValueTypeTemporaryRegistration.class) || hasNotValueForType(TL_secureValueTypeBankStatement.class) || hasNotValueForType(TL_secureValueTypeRentalAgreement.class);
    }

    private void openAddDocumentAlert() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (hasNotValueForType(TL_secureValueTypePhone.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentPhone", NUM));
            arrayList2.add(TL_secureValueTypePhone.class);
        }
        if (hasNotValueForType(TL_secureValueTypeEmail.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentEmail", NUM));
            arrayList2.add(TL_secureValueTypeEmail.class);
        }
        if (hasNotValueForType(TL_secureValueTypePersonalDetails.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentIdentity", NUM));
            arrayList2.add(TL_secureValueTypePersonalDetails.class);
        }
        if (hasNotValueForType(TL_secureValueTypePassport.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentPassport", NUM));
            arrayList2.add(TL_secureValueTypePassport.class);
        }
        if (hasNotValueForType(TL_secureValueTypeInternalPassport.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentInternalPassport", NUM));
            arrayList2.add(TL_secureValueTypeInternalPassport.class);
        }
        if (hasNotValueForType(TL_secureValueTypePassportRegistration.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
            arrayList2.add(TL_secureValueTypePassportRegistration.class);
        }
        if (hasNotValueForType(TL_secureValueTypeTemporaryRegistration.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
            arrayList2.add(TL_secureValueTypeTemporaryRegistration.class);
        }
        if (hasNotValueForType(TL_secureValueTypeIdentityCard.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentIdentityCard", NUM));
            arrayList2.add(TL_secureValueTypeIdentityCard.class);
        }
        if (hasNotValueForType(TL_secureValueTypeDriverLicense.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentDriverLicence", NUM));
            arrayList2.add(TL_secureValueTypeDriverLicense.class);
        }
        if (hasNotValueForType(TL_secureValueTypeAddress.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentAddress", NUM));
            arrayList2.add(TL_secureValueTypeAddress.class);
        }
        if (hasNotValueForType(TL_secureValueTypeUtilityBill.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
            arrayList2.add(TL_secureValueTypeUtilityBill.class);
        }
        if (hasNotValueForType(TL_secureValueTypeBankStatement.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
            arrayList2.add(TL_secureValueTypeBankStatement.class);
        }
        if (hasNotValueForType(TL_secureValueTypeRentalAgreement.class)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
            arrayList2.add(TL_secureValueTypeRentalAgreement.class);
        }
        if (getParentActivity() != null && !arrayList.isEmpty()) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PassportNoDocumentsAdd", NUM));
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new -$$Lambda$PassportActivity$-OkER2xuwjfE0-6Y_fzH2uLkAhM(this, arrayList2));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$openAddDocumentAlert$23$PassportActivity(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        TL_secureRequiredType tL_secureRequiredType;
        TL_secureRequiredType tL_secureRequiredType2;
        try {
            tL_secureRequiredType = new TL_secureRequiredType();
            try {
                tL_secureRequiredType.type = (SecureValueType) ((Class) arrayList.get(i)).newInstance();
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            tL_secureRequiredType = null;
        }
        boolean z = true;
        if (isPersonalDocument(tL_secureRequiredType.type)) {
            tL_secureRequiredType.selfie_required = true;
            tL_secureRequiredType.translation_required = true;
            tL_secureRequiredType2 = new TL_secureRequiredType();
            tL_secureRequiredType2.type = new TL_secureValueTypePersonalDetails();
        } else if (isAddressDocument(tL_secureRequiredType.type)) {
            tL_secureRequiredType2 = new TL_secureRequiredType();
            tL_secureRequiredType2.type = new TL_secureValueTypeAddress();
        } else {
            tL_secureRequiredType2 = tL_secureRequiredType;
            tL_secureRequiredType = null;
        }
        ArrayList arrayList2 = new ArrayList();
        if (tL_secureRequiredType == null) {
            z = false;
        }
        openTypeActivity(tL_secureRequiredType2, tL_secureRequiredType, arrayList2, z);
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

    /* JADX WARNING: Missing block: B:20:0x0068, code skipped:
            if (r0 != 0) goto L_0x0076;
     */
    private void callCallback(boolean r4) {
        /*
        r3 = this;
        r0 = r3.callbackCalled;
        if (r0 != 0) goto L_0x0078;
    L_0x0004:
        r0 = r3.currentCallbackUrl;
        r0 = android.text.TextUtils.isEmpty(r0);
        r1 = 5;
        r2 = 1;
        if (r0 != 0) goto L_0x005a;
    L_0x000e:
        if (r4 == 0) goto L_0x002f;
    L_0x0010:
        r4 = r3.getParentActivity();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r3.currentCallbackUrl;
        r0.append(r1);
        r1 = "&tg_passport=success";
        r0.append(r1);
        r0 = r0.toString();
        r0 = android.net.Uri.parse(r0);
        org.telegram.messenger.browser.Browser.openUrl(r4, r0);
        goto L_0x0057;
    L_0x002f:
        r4 = r3.ignoreOnFailure;
        if (r4 != 0) goto L_0x0057;
    L_0x0033:
        r4 = r3.currentActivityType;
        if (r4 == r1) goto L_0x0039;
    L_0x0037:
        if (r4 != 0) goto L_0x0057;
    L_0x0039:
        r4 = r3.getParentActivity();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r3.currentCallbackUrl;
        r0.append(r1);
        r1 = "&tg_passport=cancel";
        r0.append(r1);
        r0 = r0.toString();
        r0 = android.net.Uri.parse(r0);
        org.telegram.messenger.browser.Browser.openUrl(r4, r0);
    L_0x0057:
        r3.callbackCalled = r2;
        goto L_0x0078;
    L_0x005a:
        r0 = r3.needActivityResult;
        if (r0 == 0) goto L_0x0078;
    L_0x005e:
        if (r4 != 0) goto L_0x006a;
    L_0x0060:
        r0 = r3.ignoreOnFailure;
        if (r0 != 0) goto L_0x0076;
    L_0x0064:
        r0 = r3.currentActivityType;
        if (r0 == r1) goto L_0x006a;
    L_0x0068:
        if (r0 != 0) goto L_0x0076;
    L_0x006a:
        r0 = r3.getParentActivity();
        if (r4 == 0) goto L_0x0072;
    L_0x0070:
        r4 = -1;
        goto L_0x0073;
    L_0x0072:
        r4 = 0;
    L_0x0073:
        r0.setResult(r4);
    L_0x0076:
        r3.callbackCalled = r2;
    L_0x0078:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.callCallback(boolean):void");
    }

    private void createEmailInterface(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", NUM));
        String str = "windowBackgroundGrayShadow";
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            textSettingsCell.setText(LocaleController.formatString("PassportPhoneUseSame", NUM, this.currentEmail), false);
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new -$$Lambda$PassportActivity$B8yMOg0egHKBmo8qqI-zP3hWZKI(this));
            this.bottomCell = new TextInfoPrivacyCell(context2);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameEmailInfo", NUM));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.inputFields = new EditTextBoldCursor[1];
        for (int i = 0; i < 1; i++) {
            FrameLayout frameLayout = new FrameLayout(context2);
            this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 50));
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[i] = new EditTextBoldCursor(context2);
            this.inputFields[i].setTag(Integer.valueOf(i));
            this.inputFields[i].setTextSize(1, 16.0f);
            this.inputFields[i].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            String str2 = "windowBackgroundWhiteBlackText";
            this.inputFields[i].setTextColor(Theme.getColor(str2));
            this.inputFields[i].setBackgroundDrawable(null);
            this.inputFields[i].setCursorColor(Theme.getColor(str2));
            this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i].setCursorWidth(1.5f);
            this.inputFields[i].setInputType(33);
            this.inputFields[i].setImeOptions(NUM);
            if (i == 0) {
                this.inputFields[i].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", NUM));
                TL_secureValue tL_secureValue = this.currentTypeValue;
                if (tL_secureValue != null) {
                    SecurePlainData securePlainData = tL_secureValue.plain_data;
                    if (securePlainData instanceof TL_securePlainEmail) {
                        TL_securePlainEmail tL_securePlainEmail = (TL_securePlainEmail) securePlainData;
                        if (!TextUtils.isEmpty(tL_securePlainEmail.email)) {
                            this.inputFields[i].setText(tL_securePlainEmail.email);
                        }
                    }
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            this.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[i].setGravity(LocaleController.isRTL ? 5 : 3);
            frameLayout.addView(this.inputFields[i], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[i].setOnEditorActionListener(new -$$Lambda$PassportActivity$lpvHitH1qNbCguTGgQkc2rG8PIQ(this));
        }
        this.bottomCell = new TextInfoPrivacyCell(context2);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
        this.bottomCell.setText(LocaleController.getString("PassportEmailUploadInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    public /* synthetic */ void lambda$createEmailInterface$24$PassportActivity(View view) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    public /* synthetic */ boolean lambda$createEmailInterface$25$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    private void createPhoneInterface(Context context) {
        String readLine;
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", NUM));
        this.languageMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.countriesArray.add(0, split[2]);
                this.countriesMap.put(split[2], split[0]);
                this.codesMap.put(split[0], split[2]);
                if (split.length > 3) {
                    this.phoneFormatMap.put(split[0], split[3]);
                }
                this.languageMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        Collections.sort(this.countriesArray, -$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE);
        String str = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
        TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        Object[] objArr = new Object[1];
        PhoneFormat instance = PhoneFormat.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        String str2 = "+";
        stringBuilder.append(str2);
        stringBuilder.append(str);
        objArr[0] = instance.format(stringBuilder.toString());
        textSettingsCell.setText(LocaleController.formatString("PassportPhoneUseSame", NUM, objArr), false);
        int i = -1;
        this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
        textSettingsCell.setOnClickListener(new -$$Lambda$PassportActivity$ZXxmpnBqGMDCWhhQmZBD-R6-CLASSNAME(this));
        this.bottomCell = new TextInfoPrivacyCell(context2);
        readLine = "windowBackgroundGrayShadow";
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, readLine));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context2);
        this.headerCell.setText(LocaleController.getString("PassportPhoneUseOther", NUM));
        String str3 = "windowBackgroundWhite";
        this.headerCell.setBackgroundColor(Theme.getColor(str3));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[3];
        int i2 = 0;
        while (i2 < 3) {
            ViewGroup linearLayout;
            if (i2 == 2) {
                this.inputFields[i2] = new HintEditText(context2);
            } else {
                this.inputFields[i2] = new EditTextBoldCursor(context2);
            }
            if (i2 == 1) {
                linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(0);
                this.linearLayout2.addView(linearLayout, LayoutHelper.createLinear(i, 50));
                linearLayout.setBackgroundColor(Theme.getColor(str3));
            } else if (i2 == 2) {
                linearLayout = (ViewGroup) this.inputFields[1].getParent();
            } else {
                linearLayout = new FrameLayout(context2);
                this.linearLayout2.addView(linearLayout, LayoutHelper.createLinear(i, 50));
                linearLayout.setBackgroundColor(Theme.getColor(str3));
            }
            this.inputFields[i2].setTag(Integer.valueOf(i2));
            this.inputFields[i2].setTextSize(1, 16.0f);
            this.inputFields[i2].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            String str4 = "windowBackgroundWhiteBlackText";
            this.inputFields[i2].setTextColor(Theme.getColor(str4));
            this.inputFields[i2].setBackgroundDrawable(null);
            this.inputFields[i2].setCursorColor(Theme.getColor(str4));
            this.inputFields[i2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i2].setCursorWidth(1.5f);
            if (i2 == 0) {
                this.inputFields[i2].setOnTouchListener(new -$$Lambda$PassportActivity$m7XOq19_n687jJsXOFpNqm9gDgg(this));
                this.inputFields[i2].setText(LocaleController.getString("ChooseCountry", NUM));
                this.inputFields[i2].setInputType(0);
                this.inputFields[i2].setFocusable(false);
            } else {
                this.inputFields[i2].setInputType(3);
                if (i2 == 2) {
                    this.inputFields[i2].setImeOptions(NUM);
                } else {
                    this.inputFields[i2].setImeOptions(NUM);
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i2].setSelection(editTextBoldCursorArr[i2].length());
            int i3 = 5;
            if (i2 == 1) {
                this.plusTextView = new TextView(context2);
                this.plusTextView.setText(str2);
                this.plusTextView.setTextColor(Theme.getColor(str4));
                this.plusTextView.setTextSize(1, 16.0f);
                linearLayout.addView(this.plusTextView, LayoutHelper.createLinear(-2, -2, 21.0f, 12.0f, 0.0f, 6.0f));
                this.inputFields[i2].setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
                this.inputFields[i2].setFilters(new InputFilter[]{new LengthFilter(5)});
                this.inputFields[i2].setGravity(19);
                linearLayout.addView(this.inputFields[i2], LayoutHelper.createLinear(55, -2, 0.0f, 12.0f, 16.0f, 6.0f));
                this.inputFields[i2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    /* JADX WARNING: Removed duplicated region for block: B:28:0x0145  */
                    /* JADX WARNING: Removed duplicated region for block: B:30:0x0165  */
                    /* JADX WARNING: Removed duplicated region for block: B:32:0x0182  */
                    public void afterTextChanged(android.text.Editable r12) {
                        /*
                        r11 = this;
                        r12 = org.telegram.ui.PassportActivity.this;
                        r12 = r12.ignoreOnTextChange;
                        if (r12 == 0) goto L_0x0009;
                    L_0x0008:
                        return;
                    L_0x0009:
                        r12 = org.telegram.ui.PassportActivity.this;
                        r0 = 1;
                        r12.ignoreOnTextChange = r0;
                        r12 = org.telegram.ui.PassportActivity.this;
                        r12 = r12.inputFields;
                        r12 = r12[r0];
                        r12 = r12.getText();
                        r12 = r12.toString();
                        r12 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r12);
                        r1 = org.telegram.ui.PassportActivity.this;
                        r1 = r1.inputFields;
                        r1 = r1[r0];
                        r1.setText(r12);
                        r1 = org.telegram.ui.PassportActivity.this;
                        r1 = r1.inputFields;
                        r2 = 2;
                        r1 = r1[r2];
                        r1 = (org.telegram.ui.Components.HintEditText) r1;
                        r3 = r12.length();
                        r4 = NUM; // 0x7f0d07ce float:1.8746167E38 double:1.0531307647E-314;
                        r5 = "PaymentShippingPhoneNumber";
                        r6 = 0;
                        r7 = 0;
                        if (r3 != 0) goto L_0x0066;
                    L_0x0046:
                        r1.setHintText(r6);
                        r12 = org.telegram.messenger.LocaleController.getString(r5, r4);
                        r1.setHint(r12);
                        r12 = org.telegram.ui.PassportActivity.this;
                        r12 = r12.inputFields;
                        r12 = r12[r7];
                        r0 = NUM; // 0x7f0d02ae float:1.8743506E38 double:1.0531301165E-314;
                        r1 = "ChooseCountry";
                        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
                        r12.setText(r0);
                        goto L_0x018f;
                    L_0x0066:
                        r3 = r12.length();
                        r8 = 4;
                        if (r3 <= r8) goto L_0x00ef;
                    L_0x006d:
                        if (r8 < r0) goto L_0x00b5;
                    L_0x006f:
                        r3 = r12.substring(r7, r8);
                        r9 = org.telegram.ui.PassportActivity.this;
                        r9 = r9.codesMap;
                        r9 = r9.get(r3);
                        r9 = (java.lang.String) r9;
                        if (r9 == 0) goto L_0x00b2;
                    L_0x0081:
                        r9 = new java.lang.StringBuilder;
                        r9.<init>();
                        r12 = r12.substring(r8);
                        r9.append(r12);
                        r12 = org.telegram.ui.PassportActivity.this;
                        r12 = r12.inputFields;
                        r12 = r12[r2];
                        r12 = r12.getText();
                        r12 = r12.toString();
                        r9.append(r12);
                        r12 = r9.toString();
                        r8 = org.telegram.ui.PassportActivity.this;
                        r8 = r8.inputFields;
                        r8 = r8[r0];
                        r8.setText(r3);
                        r8 = r12;
                        r12 = 1;
                        goto L_0x00b8;
                    L_0x00b2:
                        r8 = r8 + -1;
                        goto L_0x006d;
                    L_0x00b5:
                        r3 = r12;
                        r8 = r6;
                        r12 = 0;
                    L_0x00b8:
                        if (r12 != 0) goto L_0x00ed;
                    L_0x00ba:
                        r8 = new java.lang.StringBuilder;
                        r8.<init>();
                        r9 = r3.substring(r0);
                        r8.append(r9);
                        r9 = org.telegram.ui.PassportActivity.this;
                        r9 = r9.inputFields;
                        r2 = r9[r2];
                        r2 = r2.getText();
                        r2 = r2.toString();
                        r8.append(r2);
                        r2 = r8.toString();
                        r8 = org.telegram.ui.PassportActivity.this;
                        r8 = r8.inputFields;
                        r8 = r8[r0];
                        r3 = r3.substring(r7, r0);
                        r8.setText(r3);
                        goto L_0x00f2;
                    L_0x00ed:
                        r2 = r8;
                        goto L_0x00f2;
                    L_0x00ef:
                        r3 = r12;
                        r2 = r6;
                        r12 = 0;
                    L_0x00f2:
                        r8 = org.telegram.ui.PassportActivity.this;
                        r8 = r8.codesMap;
                        r8 = r8.get(r3);
                        r8 = (java.lang.String) r8;
                        if (r8 == 0) goto L_0x0142;
                    L_0x0100:
                        r9 = org.telegram.ui.PassportActivity.this;
                        r9 = r9.countriesArray;
                        r8 = r9.indexOf(r8);
                        r9 = -1;
                        if (r8 == r9) goto L_0x0142;
                    L_0x010d:
                        r9 = org.telegram.ui.PassportActivity.this;
                        r9 = r9.inputFields;
                        r9 = r9[r7];
                        r10 = org.telegram.ui.PassportActivity.this;
                        r10 = r10.countriesArray;
                        r8 = r10.get(r8);
                        r8 = (java.lang.CharSequence) r8;
                        r9.setText(r8);
                        r8 = org.telegram.ui.PassportActivity.this;
                        r8 = r8.phoneFormatMap;
                        r3 = r8.get(r3);
                        r3 = (java.lang.String) r3;
                        if (r3 == 0) goto L_0x0140;
                    L_0x0132:
                        r8 = 88;
                        r9 = 8211; // 0x2013 float:1.1506E-41 double:4.057E-320;
                        r3 = r3.replace(r8, r9);
                        r1.setHintText(r3);
                        r1.setHint(r6);
                    L_0x0140:
                        r3 = 1;
                        goto L_0x0143;
                    L_0x0142:
                        r3 = 0;
                    L_0x0143:
                        if (r3 != 0) goto L_0x0163;
                    L_0x0145:
                        r1.setHintText(r6);
                        r3 = org.telegram.messenger.LocaleController.getString(r5, r4);
                        r1.setHint(r3);
                        r3 = org.telegram.ui.PassportActivity.this;
                        r3 = r3.inputFields;
                        r3 = r3[r7];
                        r4 = NUM; // 0x7f0d0aa5 float:1.8747642E38 double:1.053131124E-314;
                        r5 = "WrongCountry";
                        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
                        r3.setText(r4);
                    L_0x0163:
                        if (r12 != 0) goto L_0x0180;
                    L_0x0165:
                        r12 = org.telegram.ui.PassportActivity.this;
                        r12 = r12.inputFields;
                        r12 = r12[r0];
                        r3 = org.telegram.ui.PassportActivity.this;
                        r3 = r3.inputFields;
                        r0 = r3[r0];
                        r0 = r0.getText();
                        r0 = r0.length();
                        r12.setSelection(r0);
                    L_0x0180:
                        if (r2 == 0) goto L_0x018f;
                    L_0x0182:
                        r1.requestFocus();
                        r1.setText(r2);
                        r12 = r1.length();
                        r1.setSelection(r12);
                    L_0x018f:
                        r12 = org.telegram.ui.PassportActivity.this;
                        r12.ignoreOnTextChange = r7;
                        return;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity$AnonymousClass9.afterTextChanged(android.text.Editable):void");
                    }
                });
            } else if (i2 == 2) {
                this.inputFields[i2].setPadding(0, 0, 0, 0);
                this.inputFields[i2].setGravity(19);
                this.inputFields[i2].setHintText(null);
                this.inputFields[i2].setHint(LocaleController.getString("PaymentShippingPhoneNumber", NUM));
                linearLayout.addView(this.inputFields[i2], LayoutHelper.createLinear(-1, -2, 0.0f, 12.0f, 21.0f, 6.0f));
                this.inputFields[i2].addTextChangedListener(new TextWatcher() {
                    private int actionPosition;
                    private int characterAction = -1;

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        if (i2 == 0 && i3 == 1) {
                            this.characterAction = 1;
                        } else if (i2 != 1 || i3 != 0) {
                            this.characterAction = -1;
                        } else if (charSequence.charAt(i) != ' ' || i <= 0) {
                            this.characterAction = 2;
                        } else {
                            this.characterAction = 3;
                            this.actionPosition = i - 1;
                        }
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!PassportActivity.this.ignoreOnPhoneChange) {
                            StringBuilder stringBuilder;
                            int i;
                            HintEditText hintEditText = (HintEditText) PassportActivity.this.inputFields[2];
                            int selectionStart = hintEditText.getSelectionStart();
                            String obj = hintEditText.getText().toString();
                            if (this.characterAction == 3) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(obj.substring(0, this.actionPosition));
                                stringBuilder.append(obj.substring(this.actionPosition + 1));
                                obj = stringBuilder.toString();
                                selectionStart--;
                            }
                            stringBuilder = new StringBuilder(obj.length());
                            int i2 = 0;
                            while (i2 < obj.length()) {
                                i = i2 + 1;
                                String substring = obj.substring(i2, i);
                                if ("NUM".contains(substring)) {
                                    stringBuilder.append(substring);
                                }
                                i2 = i;
                            }
                            PassportActivity.this.ignoreOnPhoneChange = true;
                            obj = hintEditText.getHintText();
                            if (obj != null) {
                                i2 = selectionStart;
                                selectionStart = 0;
                                while (selectionStart < stringBuilder.length()) {
                                    if (selectionStart < obj.length()) {
                                        if (obj.charAt(selectionStart) == ' ') {
                                            stringBuilder.insert(selectionStart, ' ');
                                            selectionStart++;
                                            if (i2 == selectionStart) {
                                                i = this.characterAction;
                                                if (!(i == 2 || i == 3)) {
                                                    i2++;
                                                }
                                            }
                                        }
                                        selectionStart++;
                                    } else {
                                        stringBuilder.insert(selectionStart, ' ');
                                        if (i2 == selectionStart + 1) {
                                            selectionStart = this.characterAction;
                                            if (!(selectionStart == 2 || selectionStart == 3)) {
                                                selectionStart = i2 + 1;
                                            }
                                        }
                                        selectionStart = i2;
                                    }
                                }
                                selectionStart = i2;
                            }
                            hintEditText.setText(stringBuilder);
                            if (selectionStart >= 0) {
                                if (selectionStart > hintEditText.length()) {
                                    selectionStart = hintEditText.length();
                                }
                                hintEditText.setSelection(selectionStart);
                            }
                            hintEditText.onTextChange();
                            PassportActivity.this.ignoreOnPhoneChange = false;
                        }
                    }
                });
            } else {
                this.inputFields[i2].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
                EditText editText = this.inputFields[i2];
                if (!LocaleController.isRTL) {
                    i3 = 3;
                }
                editText.setGravity(i3);
                linearLayout.addView(this.inputFields[i2], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            }
            this.inputFields[i2].setOnEditorActionListener(new -$$Lambda$PassportActivity$Ud8QO9mJkulFHffuDeCmjGoUGKM(this));
            if (i2 == 2) {
                this.inputFields[i2].setOnKeyListener(new -$$Lambda$PassportActivity$jS_iy1VRiF9kFI3x6V7-e2W45Zc(this));
            }
            if (i2 == 0) {
                View view = new View(context2);
                this.dividers.add(view);
                view.setBackgroundColor(Theme.getColor("divider"));
                linearLayout.addView(view, new LayoutParams(-1, 1, 83));
            }
            i2++;
            i = -1;
        }
        Object obj = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            obj = telephonyManager != null ? telephonyManager.getSimCountryIso().toUpperCase() : obj;
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (obj != null) {
            str = (String) this.languageMap.get(obj);
            if (!(str == null || this.countriesArray.indexOf(str) == -1)) {
                this.inputFields[1].setText((CharSequence) this.countriesMap.get(str));
            }
        }
        this.bottomCell = new TextInfoPrivacyCell(context2);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, readLine));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUploadInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    public /* synthetic */ void lambda$createPhoneInterface$26$PassportActivity(View view) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    public /* synthetic */ boolean lambda$createPhoneInterface$29$PassportActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$PassportActivity$BwoW6ssGVQlkA38H5aqDej4Yyd0(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$28$PassportActivity(String str, String str2) {
        this.inputFields[0].setText(str);
        if (this.countriesArray.indexOf(str) != -1) {
            this.ignoreOnTextChange = true;
            str = (String) this.countriesMap.get(str);
            this.inputFields[1].setText(str);
            str = (String) this.phoneFormatMap.get(str);
            this.inputFields[2].setHintText(str != null ? str.replace('X', 8211) : null);
            this.ignoreOnTextChange = false;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$YJlZD6OdhwSywpXiGxUfp0RoQoQ(this), 300);
        this.inputFields[2].requestFocus();
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[2].setSelection(editTextBoldCursorArr[2].length());
    }

    public /* synthetic */ void lambda$null$27$PassportActivity() {
        AndroidUtilities.showKeyboard(this.inputFields[2]);
    }

    public /* synthetic */ boolean lambda$createPhoneInterface$30$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    public /* synthetic */ boolean lambda$createPhoneInterface$31$PassportActivity(View view, int i, KeyEvent keyEvent) {
        if (i != 67 || this.inputFields[2].length() != 0) {
            return false;
        }
        this.inputFields[1].requestFocus();
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[1].setSelection(editTextBoldCursorArr[1].length());
        this.inputFields[1].dispatchKeyEvent(keyEvent);
        return true;
    }

    private void createAddressInterface(Context context) {
        String readLine;
        Context context2 = context;
        this.languageMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.languageMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.topErrorCell = new TextInfoPrivacyCell(context2);
        String str = "windowBackgroundGrayShadow";
        this.topErrorCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
        boolean z = false;
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        int i = -2;
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
        String str2 = "windowBackgroundWhiteRedText3";
        String str3 = "windowBackgroundWhite";
        if (tL_secureRequiredType != null) {
            String str4;
            SecureValueType secureValueType = tL_secureRequiredType.type;
            if (secureValueType instanceof TL_secureValueTypeRentalAgreement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
            } else if (secureValueType instanceof TL_secureValueTypeBankStatement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
            } else if (secureValueType instanceof TL_secureValueTypeUtilityBill) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
            } else if (secureValueType instanceof TL_secureValueTypePassportRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
            } else if (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
            }
            this.headerCell = new HeaderCell(context2);
            this.headerCell.setText(LocaleController.getString("PassportDocuments", NUM));
            this.headerCell.setBackgroundColor(Theme.getColor(str3));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            this.documentsLayout = new LinearLayout(context2);
            this.documentsLayout.setOrientation(1);
            this.linearLayout2.addView(this.documentsLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell = new TextSettingsCell(context2);
            this.uploadDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell.setOnClickListener(new -$$Lambda$PassportActivity$iWoQRq4LDUs7-2TnB7mjc6JUANI(this));
            this.bottomCell = new TextInfoPrivacyCell(context2);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            String str5 = "";
            if (this.currentBotId != 0) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAddressUploadInfo", NUM);
            } else {
                secureValueType = this.currentDocumentsType.type;
                if (secureValueType instanceof TL_secureValueTypeRentalAgreement) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAgreementInfo", NUM);
                } else if (secureValueType instanceof TL_secureValueTypeUtilityBill) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBillInfo", NUM);
                } else if (secureValueType instanceof TL_secureValueTypePassportRegistration) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddPassportRegistrationInfo", NUM);
                } else if (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddTemporaryRegistrationInfo", NUM);
                } else if (secureValueType instanceof TL_secureValueTypeBankStatement) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBankInfo", NUM);
                } else {
                    this.noAllDocumentsErrorText = str5;
                }
            }
            CharSequence charSequence = this.noAllDocumentsErrorText;
            HashMap hashMap = this.documentsErrors;
            if (hashMap != null) {
                str4 = (String) hashMap.get("files_all");
                if (str4 != null) {
                    charSequence = new SpannableStringBuilder(str4);
                    charSequence.append("\n\n");
                    charSequence.append(this.noAllDocumentsErrorText);
                    charSequence.setSpan(new ForegroundColorSpan(Theme.getColor(str2)), 0, str4.length(), 33);
                    this.errorsValues.put("files_all", str5);
                }
            }
            this.bottomCell.setText(charSequence);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                this.headerCell = new HeaderCell(context2);
                this.headerCell.setText(LocaleController.getString("PassportTranslation", NUM));
                this.headerCell.setBackgroundColor(Theme.getColor(str3));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                this.translationLayout = new LinearLayout(context2);
                this.translationLayout.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell = new TextSettingsCell(context2);
                this.uploadTranslationCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new -$$Lambda$PassportActivity$zxJkztkjm9AzPkgXb6NmXZ5sri0(this));
                this.bottomCellTranslation = new TextInfoPrivacyCell(context2);
                this.bottomCellTranslation.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", NUM);
                } else {
                    secureValueType = this.currentDocumentsType.type;
                    if (secureValueType instanceof TL_secureValueTypeRentalAgreement) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationAgreementInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypeUtilityBill) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBillInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypePassportRegistration) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationPassportRegistrationInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationTemporaryRegistrationInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypeBankStatement) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBankInfo", NUM);
                    } else {
                        this.noAllTranslationErrorText = str5;
                    }
                }
                charSequence = this.noAllTranslationErrorText;
                hashMap = this.documentsErrors;
                if (hashMap != null) {
                    str4 = (String) hashMap.get("translation_all");
                    if (str4 != null) {
                        charSequence = new SpannableStringBuilder(str4);
                        charSequence.append("\n\n");
                        charSequence.append(this.noAllTranslationErrorText);
                        charSequence.setSpan(new ForegroundColorSpan(Theme.getColor(str2)), 0, str4.length(), 33);
                        this.errorsValues.put("translation_all", str5);
                    }
                }
                this.bottomCellTranslation.setText(charSequence);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportAddress", NUM));
        }
        this.headerCell = new HeaderCell(context2);
        this.headerCell.setText(LocaleController.getString("PassportAddressHeader", NUM));
        this.headerCell.setBackgroundColor(Theme.getColor(str3));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[6];
        int i2 = 0;
        while (i2 < 6) {
            final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.inputFields[i2] = editTextBoldCursor;
            AnonymousClass11 anonymousClass11 = new FrameLayout(context2) {
                private StaticLayout errorLayout;
                float offsetX;

                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    int size = MeasureSpec.getSize(i) - AndroidUtilities.dp(34.0f);
                    this.errorLayout = editTextBoldCursor.getErrorLayout(size);
                    StaticLayout staticLayout = this.errorLayout;
                    if (staticLayout != null) {
                        int lineCount = staticLayout.getLineCount();
                        int i3 = 0;
                        if (lineCount > 1) {
                            i2 = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                        }
                        if (LocaleController.isRTL) {
                            float f = 0.0f;
                            while (i3 < lineCount) {
                                if (this.errorLayout.getLineLeft(i3) != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                }
                                f = Math.max(f, this.errorLayout.getLineWidth(i3));
                                if (i3 == lineCount - 1) {
                                    this.offsetX = ((float) size) - f;
                                }
                                i3++;
                            }
                        }
                    }
                    super.onMeasure(i, i2);
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            anonymousClass11.setWillNotDraw(z);
            this.linearLayout2.addView(anonymousClass11, LayoutHelper.createLinear(-1, i));
            anonymousClass11.setBackgroundColor(Theme.getColor(str3));
            int i3 = 5;
            if (i2 == 5) {
                this.extraBackgroundView = new View(context2);
                this.extraBackgroundView.setBackgroundColor(Theme.getColor(str3));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.documentOnly && this.currentDocumentsType != null) {
                anonymousClass11.setVisibility(8);
                View view = this.extraBackgroundView;
                if (view != null) {
                    view.setVisibility(8);
                }
            }
            this.inputFields[i2].setTag(Integer.valueOf(i2));
            this.inputFields[i2].setSupportRtlHint(true);
            this.inputFields[i2].setTextSize(1, 16.0f);
            this.inputFields[i2].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[i2].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[i2].setTransformHintToHeader(true);
            String str6 = "windowBackgroundWhiteBlackText";
            this.inputFields[i2].setTextColor(Theme.getColor(str6));
            this.inputFields[i2].setBackgroundDrawable(null);
            this.inputFields[i2].setCursorColor(Theme.getColor(str6));
            this.inputFields[i2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i2].setCursorWidth(1.5f);
            this.inputFields[i2].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor(str2));
            if (i2 == 5) {
                this.inputFields[i2].setOnTouchListener(new -$$Lambda$PassportActivity$VjopYxs0FTEpLRu0AyOT8rb-e68(this));
                this.inputFields[i2].setInputType(0);
                this.inputFields[i2].setFocusable(false);
            } else {
                this.inputFields[i2].setInputType(16385);
                this.inputFields[i2].setImeOptions(NUM);
            }
            if (i2 == 0) {
                this.inputFields[i2].setHintText(LocaleController.getString("PassportStreet1", NUM));
                readLine = "street_line1";
            } else if (i2 == 1) {
                this.inputFields[i2].setHintText(LocaleController.getString("PassportStreet2", NUM));
                readLine = "street_line2";
            } else if (i2 == 2) {
                this.inputFields[i2].setHintText(LocaleController.getString("PassportPostcode", NUM));
                readLine = "post_code";
            } else if (i2 == 3) {
                this.inputFields[i2].setHintText(LocaleController.getString("PassportCity", NUM));
                readLine = "city";
            } else if (i2 == 4) {
                this.inputFields[i2].setHintText(LocaleController.getString("PassportState", NUM));
                readLine = "state";
            } else if (i2 != 5) {
                i2++;
                z = false;
                i = -2;
            } else {
                this.inputFields[i2].setHintText(LocaleController.getString("PassportCountry", NUM));
                readLine = "country_code";
            }
            setFieldValues(this.currentValues, this.inputFields[i2], readLine);
            if (i2 == 2) {
                this.inputFields[i2].addTextChangedListener(new TextWatcher() {
                    private boolean ignore;

                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!this.ignore) {
                            boolean z = true;
                            this.ignore = true;
                            for (int i = 0; i < editable.length(); i++) {
                                char charAt = editable.charAt(i);
                                if ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && !((charAt >= '0' && charAt <= '9') || charAt == '-' || charAt == ' '))) {
                                    break;
                                }
                            }
                            z = false;
                            this.ignore = false;
                            if (z) {
                                editTextBoldCursor.setErrorText(LocaleController.getString("PassportUseLatinOnly", NUM));
                            } else {
                                PassportActivity.this.checkFieldForError(editTextBoldCursor, readLine, editable, false);
                            }
                        }
                    }
                });
                this.inputFields[i2].setFilters(new InputFilter[]{new LengthFilter(10)});
            } else {
                this.inputFields[i2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        PassportActivity.this.checkFieldForError(editTextBoldCursor, readLine, editable, false);
                    }
                });
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i2].setSelection(editTextBoldCursorArr[i2].length());
            this.inputFields[i2].setPadding(0, 0, 0, 0);
            EditText editText = this.inputFields[i2];
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            editText.setGravity(i3 | 16);
            anonymousClass11.addView(this.inputFields[i2], LayoutHelper.createFrame(-1, 64.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputFields[i2].setOnEditorActionListener(new -$$Lambda$PassportActivity$AoVUW01QTsr_TGtxsPo-viSuIG8(this));
            i2++;
            z = false;
            i = -2;
        }
        this.sectionCell = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.documentOnly && this.currentDocumentsType != null) {
            this.headerCell.setVisibility(8);
            this.sectionCell.setVisibility(8);
        }
        if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            if (this.documentOnly && this.currentDocumentsType != null) {
                this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            }
        } else {
            TL_secureValue tL_secureValue = this.currentDocumentsTypeValue;
            if (tL_secureValue != null) {
                addDocumentViews(tL_secureValue.files);
                addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            textSettingsCell.setTextColor(Theme.getColor(str2));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentDocumentsType == null) {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteInfo", NUM), false);
            } else {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteDocument", NUM), false);
            }
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new -$$Lambda$PassportActivity$OrTOOfgxc6UhCIwbWsAhFPEaa80(this));
            this.sectionCell = new ShadowSectionCell(context2);
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        }
        updateUploadText(0);
        updateUploadText(4);
    }

    public /* synthetic */ void lambda$createAddressInterface$32$PassportActivity(View view) {
        this.uploadingFileType = 0;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createAddressInterface$33$PassportActivity(View view) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    public /* synthetic */ boolean lambda$createAddressInterface$35$PassportActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$PassportActivity$FVAHEan3uDvz73NCL5GKR6mgp-0(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$34$PassportActivity(String str, String str2) {
        this.inputFields[5].setText(str);
        this.currentCitizeship = str2;
    }

    public /* synthetic */ boolean lambda$createAddressInterface$36$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        i = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (i < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[i].isFocusable()) {
                this.inputFields[i].requestFocus();
            } else {
                this.inputFields[i].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$createAddressInterface$37$PassportActivity(View view) {
        createDocumentDeleteAlert();
    }

    private void createDocumentDeleteAlert() {
        boolean[] zArr = new boolean[]{true};
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PassportActivity$izrGH6tzz_c5ZefftonTyrpURyU(this, zArr));
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
            CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            SecureValueType secureValueType = this.currentType.type;
            String str = "";
            if (secureValueType instanceof TL_secureValueTypeAddress) {
                checkBoxCell.setText(LocaleController.getString("PassportDeleteDocumentAddress", NUM), str, true, false);
            } else if (secureValueType instanceof TL_secureValueTypePersonalDetails) {
                checkBoxCell.setText(LocaleController.getString("PassportDeleteDocumentPersonal", NUM), str, true, false);
            }
            checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48, 51));
            checkBoxCell.setOnClickListener(new -$$Lambda$PassportActivity$nDbjMA2U409g1anoYGcsOusiteI(zArr));
            builder.setView(frameLayout);
        }
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$createDocumentDeleteAlert$38$PassportActivity(boolean[] zArr, DialogInterface dialogInterface, int i) {
        if (!this.documentOnly) {
            this.currentValues.clear();
        }
        this.currentDocumentValues.clear();
        this.delegate.deleteValue(this.currentType, this.currentDocumentsType, this.availableDocumentTypes, zArr[0], null, null);
        finishFragment();
    }

    static /* synthetic */ void lambda$createDocumentDeleteAlert$39(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            zArr[0] = zArr[0] ^ 1;
            checkBoxCell.setChecked(zArr[0], true);
        }
    }

    private void onFieldError(View view) {
        if (view != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(view, 2.0f, 0);
            scrollToField(view);
        }
    }

    private void scrollToField(View view) {
        while (view != null && this.linearLayout2.indexOfChild(view) < 0) {
            view = (View) view.getParent();
        }
        if (view != null) {
            this.scrollView.smoothScrollTo(0, view.getTop() - ((this.scrollView.getMeasuredHeight() - view.getMeasuredHeight()) / 2));
        }
    }

    private String getDocumentHash(SecureDocument secureDocument) {
        if (secureDocument != null) {
            TL_secureFile tL_secureFile = secureDocument.secureFile;
            if (tL_secureFile != null) {
                byte[] bArr = tL_secureFile.file_hash;
                if (bArr != null) {
                    return Base64.encodeToString(bArr, 2);
                }
            }
            byte[] bArr2 = secureDocument.fileHash;
            if (bArr2 != null) {
                return Base64.encodeToString(bArr2, 2);
            }
        }
        return "";
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003c  */
    private void checkFieldForError(org.telegram.ui.Components.EditTextBoldCursor r3, java.lang.String r4, android.text.Editable r5, boolean r6) {
        /*
        r2 = this;
        r0 = r2.errorsValues;
        r1 = 0;
        if (r0 == 0) goto L_0x0037;
    L_0x0005:
        r0 = r0.get(r4);
        r0 = (java.lang.String) r0;
        if (r0 == 0) goto L_0x0037;
    L_0x000d:
        r5 = android.text.TextUtils.equals(r0, r5);
        if (r5 == 0) goto L_0x0033;
    L_0x0013:
        r5 = r2.fieldsErrors;
        if (r5 == 0) goto L_0x0023;
    L_0x0017:
        r5 = r5.get(r4);
        r5 = (java.lang.String) r5;
        if (r5 == 0) goto L_0x0023;
    L_0x001f:
        r3.setErrorText(r5);
        goto L_0x003a;
    L_0x0023:
        r5 = r2.documentsErrors;
        if (r5 == 0) goto L_0x003a;
    L_0x0027:
        r4 = r5.get(r4);
        r4 = (java.lang.String) r4;
        if (r4 == 0) goto L_0x003a;
    L_0x002f:
        r3.setErrorText(r4);
        goto L_0x003a;
    L_0x0033:
        r3.setErrorText(r1);
        goto L_0x003a;
    L_0x0037:
        r3.setErrorText(r1);
    L_0x003a:
        if (r6 == 0) goto L_0x003f;
    L_0x003c:
        r3 = "error_document_all";
        goto L_0x0041;
    L_0x003f:
        r3 = "error_all";
    L_0x0041:
        r4 = r2.errorsValues;
        if (r4 == 0) goto L_0x0054;
    L_0x0045:
        r4 = r4.containsKey(r3);
        if (r4 == 0) goto L_0x0054;
    L_0x004b:
        r4 = r2.errorsValues;
        r4.remove(r3);
        r3 = 0;
        r2.checkTopErrorCell(r3);
    L_0x0054:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.checkFieldForError(org.telegram.ui.Components.EditTextBoldCursor, java.lang.String, android.text.Editable, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:135:0x022e  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x022e  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x022e  */
    /* JADX WARNING: Missing block: B:126:0x0217, code skipped:
            if (r6 != 5) goto L_0x022b;
     */
    /* JADX WARNING: Missing block: B:168:0x028f, code skipped:
            if (r8 > 24) goto L_0x029b;
     */
    /* JADX WARNING: Missing block: B:172:0x0299, code skipped:
            if (r8 < 2) goto L_0x029b;
     */
    /* JADX WARNING: Missing block: B:177:0x02a9, code skipped:
            if (r8 < 2) goto L_0x029b;
     */
    /* JADX WARNING: Missing block: B:181:0x02b2, code skipped:
            if (r8 > 10) goto L_0x029b;
     */
    private boolean checkFieldsForError() {
        /*
        r13 = this;
        r0 = r13.currentDocumentsType;
        r1 = 0;
        r2 = 1;
        if (r0 == 0) goto L_0x019f;
    L_0x0006:
        r0 = r13.errorsValues;
        r3 = "error_all";
        r0 = r0.containsKey(r3);
        if (r0 != 0) goto L_0x0199;
    L_0x0010:
        r0 = r13.errorsValues;
        r3 = "error_document_all";
        r0 = r0.containsKey(r3);
        if (r0 == 0) goto L_0x001c;
    L_0x001a:
        goto L_0x0199;
    L_0x001c:
        r0 = r13.uploadDocumentCell;
        if (r0 == 0) goto L_0x006d;
    L_0x0020:
        r0 = r13.documents;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x002e;
    L_0x0028:
        r0 = r13.uploadDocumentCell;
        r13.onFieldError(r0);
        return r2;
    L_0x002e:
        r0 = r13.documents;
        r0 = r0.size();
        r3 = 0;
    L_0x0035:
        if (r3 >= r0) goto L_0x006d;
    L_0x0037:
        r4 = r13.documents;
        r4 = r4.get(r3);
        r4 = (org.telegram.messenger.SecureDocument) r4;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "files";
        r5.append(r6);
        r6 = r13.getDocumentHash(r4);
        r5.append(r6);
        r5 = r5.toString();
        if (r5 == 0) goto L_0x006a;
    L_0x0056:
        r6 = r13.errorsValues;
        r5 = r6.containsKey(r5);
        if (r5 == 0) goto L_0x006a;
    L_0x005e:
        r0 = r13.documentsCells;
        r0 = r0.get(r4);
        r0 = (android.view.View) r0;
        r13.onFieldError(r0);
        return r2;
    L_0x006a:
        r3 = r3 + 1;
        goto L_0x0035;
    L_0x006d:
        r0 = r13.errorsValues;
        r3 = "files_all";
        r0 = r0.containsKey(r3);
        if (r0 != 0) goto L_0x0193;
    L_0x0077:
        r0 = r13.errorsValues;
        r3 = "translation_all";
        r0 = r0.containsKey(r3);
        if (r0 == 0) goto L_0x0083;
    L_0x0081:
        goto L_0x0193;
    L_0x0083:
        r0 = r13.uploadFrontCell;
        if (r0 == 0) goto L_0x00bc;
    L_0x0087:
        r3 = r13.frontDocument;
        if (r3 != 0) goto L_0x008f;
    L_0x008b:
        r13.onFieldError(r0);
        return r2;
    L_0x008f:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "front";
        r0.append(r3);
        r3 = r13.frontDocument;
        r3 = r13.getDocumentHash(r3);
        r0.append(r3);
        r0 = r0.toString();
        r3 = r13.errorsValues;
        r0 = r3.containsKey(r0);
        if (r0 == 0) goto L_0x00bc;
    L_0x00ae:
        r0 = r13.documentsCells;
        r1 = r13.frontDocument;
        r0 = r0.get(r1);
        r0 = (android.view.View) r0;
        r13.onFieldError(r0);
        return r2;
    L_0x00bc:
        r0 = r13.currentDocumentsType;
        r0 = r0.type;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeIdentityCard;
        if (r3 != 0) goto L_0x00c8;
    L_0x00c4:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense;
        if (r0 == 0) goto L_0x0101;
    L_0x00c8:
        r0 = r13.uploadReverseCell;
        if (r0 == 0) goto L_0x0101;
    L_0x00cc:
        r3 = r13.reverseDocument;
        if (r3 != 0) goto L_0x00d4;
    L_0x00d0:
        r13.onFieldError(r0);
        return r2;
    L_0x00d4:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "reverse";
        r0.append(r3);
        r3 = r13.reverseDocument;
        r3 = r13.getDocumentHash(r3);
        r0.append(r3);
        r0 = r0.toString();
        r3 = r13.errorsValues;
        r0 = r3.containsKey(r0);
        if (r0 == 0) goto L_0x0101;
    L_0x00f3:
        r0 = r13.documentsCells;
        r1 = r13.reverseDocument;
        r0 = r0.get(r1);
        r0 = (android.view.View) r0;
        r13.onFieldError(r0);
        return r2;
    L_0x0101:
        r0 = r13.uploadSelfieCell;
        if (r0 == 0) goto L_0x013e;
    L_0x0105:
        r3 = r13.currentBotId;
        if (r3 == 0) goto L_0x013e;
    L_0x0109:
        r3 = r13.selfieDocument;
        if (r3 != 0) goto L_0x0111;
    L_0x010d:
        r13.onFieldError(r0);
        return r2;
    L_0x0111:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "selfie";
        r0.append(r3);
        r3 = r13.selfieDocument;
        r3 = r13.getDocumentHash(r3);
        r0.append(r3);
        r0 = r0.toString();
        r3 = r13.errorsValues;
        r0 = r3.containsKey(r0);
        if (r0 == 0) goto L_0x013e;
    L_0x0130:
        r0 = r13.documentsCells;
        r1 = r13.selfieDocument;
        r0 = r0.get(r1);
        r0 = (android.view.View) r0;
        r13.onFieldError(r0);
        return r2;
    L_0x013e:
        r0 = r13.uploadTranslationCell;
        if (r0 == 0) goto L_0x019f;
    L_0x0142:
        r0 = r13.currentBotId;
        if (r0 == 0) goto L_0x019f;
    L_0x0146:
        r0 = r13.translationDocuments;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0154;
    L_0x014e:
        r0 = r13.uploadTranslationCell;
        r13.onFieldError(r0);
        return r2;
    L_0x0154:
        r0 = r13.translationDocuments;
        r0 = r0.size();
        r3 = 0;
    L_0x015b:
        if (r3 >= r0) goto L_0x019f;
    L_0x015d:
        r4 = r13.translationDocuments;
        r4 = r4.get(r3);
        r4 = (org.telegram.messenger.SecureDocument) r4;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "translation";
        r5.append(r6);
        r6 = r13.getDocumentHash(r4);
        r5.append(r6);
        r5 = r5.toString();
        if (r5 == 0) goto L_0x0190;
    L_0x017c:
        r6 = r13.errorsValues;
        r5 = r6.containsKey(r5);
        if (r5 == 0) goto L_0x0190;
    L_0x0184:
        r0 = r13.documentsCells;
        r0 = r0.get(r4);
        r0 = (android.view.View) r0;
        r13.onFieldError(r0);
        return r2;
    L_0x0190:
        r3 = r3 + 1;
        goto L_0x015b;
    L_0x0193:
        r0 = r13.bottomCell;
        r13.onFieldError(r0);
        return r2;
    L_0x0199:
        r0 = r13.topErrorCell;
        r13.onFieldError(r0);
        return r2;
    L_0x019f:
        r0 = 0;
    L_0x01a0:
        r3 = 2;
        if (r0 >= r3) goto L_0x02cd;
    L_0x01a3:
        r4 = 0;
        if (r0 != 0) goto L_0x01a9;
    L_0x01a6:
        r5 = r13.inputFields;
        goto L_0x01b7;
    L_0x01a9:
        r5 = r13.nativeInfoCell;
        if (r5 == 0) goto L_0x01b6;
    L_0x01ad:
        r5 = r5.getVisibility();
        if (r5 != 0) goto L_0x01b6;
    L_0x01b3:
        r5 = r13.inputExtraFields;
        goto L_0x01b7;
    L_0x01b6:
        r5 = r4;
    L_0x01b7:
        if (r5 != 0) goto L_0x01bb;
    L_0x01b9:
        goto L_0x02c9;
    L_0x01bb:
        r6 = 0;
    L_0x01bc:
        r7 = r5.length;
        if (r6 >= r7) goto L_0x02c9;
    L_0x01bf:
        r7 = r5[r6];
        r7 = r7.hasErrorText();
        r8 = r13.errorsValues;
        r8 = r8.isEmpty();
        r9 = 4;
        r10 = 3;
        if (r8 != 0) goto L_0x024d;
    L_0x01cf:
        r8 = r13.currentType;
        r8 = r8.type;
        r11 = r8 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        r12 = "country_code";
        if (r11 == 0) goto L_0x0208;
    L_0x01d9:
        if (r0 != 0) goto L_0x01f8;
    L_0x01db:
        switch(r6) {
            case 0: goto L_0x01f5;
            case 1: goto L_0x01f2;
            case 2: goto L_0x01ef;
            case 3: goto L_0x01ec;
            case 4: goto L_0x01e9;
            case 5: goto L_0x021a;
            case 6: goto L_0x01e6;
            case 7: goto L_0x01e3;
            case 8: goto L_0x01e0;
            default: goto L_0x01de;
        };
    L_0x01de:
        goto L_0x022b;
    L_0x01e0:
        r8 = "expiry_date";
        goto L_0x022c;
    L_0x01e3:
        r8 = "document_no";
        goto L_0x022c;
    L_0x01e6:
        r8 = "residence_country_code";
        goto L_0x022c;
    L_0x01e9:
        r8 = "gender";
        goto L_0x022c;
    L_0x01ec:
        r8 = "birth_date";
        goto L_0x022c;
    L_0x01ef:
        r8 = "last_name";
        goto L_0x022c;
    L_0x01f2:
        r8 = "middle_name";
        goto L_0x022c;
    L_0x01f5:
        r8 = "first_name";
        goto L_0x022c;
    L_0x01f8:
        if (r6 == 0) goto L_0x0205;
    L_0x01fa:
        if (r6 == r2) goto L_0x0202;
    L_0x01fc:
        if (r6 == r3) goto L_0x01ff;
    L_0x01fe:
        goto L_0x022b;
    L_0x01ff:
        r8 = "last_name_native";
        goto L_0x022c;
    L_0x0202:
        r8 = "middle_name_native";
        goto L_0x022c;
    L_0x0205:
        r8 = "first_name_native";
        goto L_0x022c;
    L_0x0208:
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r8 == 0) goto L_0x022b;
    L_0x020c:
        if (r6 == 0) goto L_0x0228;
    L_0x020e:
        if (r6 == r2) goto L_0x0225;
    L_0x0210:
        if (r6 == r3) goto L_0x0222;
    L_0x0212:
        if (r6 == r10) goto L_0x021f;
    L_0x0214:
        if (r6 == r9) goto L_0x021c;
    L_0x0216:
        r8 = 5;
        if (r6 == r8) goto L_0x021a;
    L_0x0219:
        goto L_0x022b;
    L_0x021a:
        r8 = r12;
        goto L_0x022c;
    L_0x021c:
        r8 = "state";
        goto L_0x022c;
    L_0x021f:
        r8 = "city";
        goto L_0x022c;
    L_0x0222:
        r8 = "post_code";
        goto L_0x022c;
    L_0x0225:
        r8 = "street_line2";
        goto L_0x022c;
    L_0x0228:
        r8 = "street_line1";
        goto L_0x022c;
    L_0x022b:
        r8 = r4;
    L_0x022c:
        if (r8 == 0) goto L_0x024d;
    L_0x022e:
        r11 = r13.errorsValues;
        r8 = r11.get(r8);
        r8 = (java.lang.String) r8;
        r11 = android.text.TextUtils.isEmpty(r8);
        if (r11 != 0) goto L_0x024d;
    L_0x023c:
        r11 = r5[r6];
        r11 = r11.getText();
        r11 = r11.toString();
        r8 = r8.equals(r11);
        if (r8 == 0) goto L_0x024d;
    L_0x024c:
        r7 = 1;
    L_0x024d:
        r8 = r13.documentOnly;
        r11 = 7;
        if (r8 == 0) goto L_0x025a;
    L_0x0252:
        r8 = r13.currentDocumentsType;
        if (r8 == 0) goto L_0x025a;
    L_0x0256:
        if (r6 >= r11) goto L_0x025a;
    L_0x0258:
        goto L_0x02c5;
    L_0x025a:
        if (r7 != 0) goto L_0x02bd;
    L_0x025c:
        r8 = r5[r6];
        r8 = r8.length();
        r12 = r13.currentActivityType;
        if (r12 != r2) goto L_0x0292;
    L_0x0266:
        r9 = 8;
        if (r6 != r9) goto L_0x026c;
    L_0x026a:
        goto L_0x02c5;
    L_0x026c:
        if (r0 != 0) goto L_0x0274;
    L_0x026e:
        if (r6 == 0) goto L_0x027c;
    L_0x0270:
        if (r6 == r3) goto L_0x027c;
    L_0x0272:
        if (r6 == r2) goto L_0x027c;
    L_0x0274:
        if (r0 != r2) goto L_0x028b;
    L_0x0276:
        if (r6 == 0) goto L_0x027c;
    L_0x0278:
        if (r6 == r2) goto L_0x027c;
    L_0x027a:
        if (r6 != r3) goto L_0x028b;
    L_0x027c:
        r9 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r8 <= r9) goto L_0x0281;
    L_0x0280:
        r7 = 1;
    L_0x0281:
        if (r0 != 0) goto L_0x0285;
    L_0x0283:
        if (r6 == r2) goto L_0x0289;
    L_0x0285:
        if (r0 != r2) goto L_0x02b5;
    L_0x0287:
        if (r6 != r2) goto L_0x02b5;
    L_0x0289:
        r9 = 1;
        goto L_0x02b6;
    L_0x028b:
        if (r6 != r11) goto L_0x02b5;
    L_0x028d:
        r9 = 24;
        if (r8 <= r9) goto L_0x02b5;
    L_0x0291:
        goto L_0x029b;
    L_0x0292:
        if (r12 != r3) goto L_0x02b5;
    L_0x0294:
        if (r6 != r2) goto L_0x0297;
    L_0x0296:
        goto L_0x02c5;
    L_0x0297:
        if (r6 != r10) goto L_0x029d;
    L_0x0299:
        if (r8 >= r3) goto L_0x02b5;
    L_0x029b:
        r7 = 1;
        goto L_0x02b5;
    L_0x029d:
        if (r6 != r9) goto L_0x02ac;
    L_0x029f:
        r9 = r13.currentCitizeship;
        r10 = "US";
        r9 = r10.equals(r9);
        if (r9 == 0) goto L_0x02c5;
    L_0x02a9:
        if (r8 >= r3) goto L_0x02b5;
    L_0x02ab:
        goto L_0x029b;
    L_0x02ac:
        if (r6 != r3) goto L_0x02b5;
    L_0x02ae:
        if (r8 < r3) goto L_0x029b;
    L_0x02b0:
        r9 = 10;
        if (r8 <= r9) goto L_0x02b5;
    L_0x02b4:
        goto L_0x029b;
    L_0x02b5:
        r9 = 0;
    L_0x02b6:
        if (r7 != 0) goto L_0x02bd;
    L_0x02b8:
        if (r9 != 0) goto L_0x02bd;
    L_0x02ba:
        if (r8 != 0) goto L_0x02bd;
    L_0x02bc:
        r7 = 1;
    L_0x02bd:
        if (r7 == 0) goto L_0x02c5;
    L_0x02bf:
        r0 = r5[r6];
        r13.onFieldError(r0);
        return r2;
    L_0x02c5:
        r6 = r6 + 1;
        goto L_0x01bc;
    L_0x02c9:
        r0 = r0 + 1;
        goto L_0x01a0;
    L_0x02cd:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.checkFieldsForError():boolean");
    }

    private void createIdentityInterface(Context context) {
        String readLine;
        Context context2 = context;
        this.languageMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.languageMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.topErrorCell = new TextInfoPrivacyCell(context2);
        String str = "windowBackgroundGrayShadow";
        this.topErrorCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
        boolean z = false;
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        int i = -1;
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        readLine = "windowBackgroundWhiteRedText3";
        String str2 = "windowBackgroundWhite";
        if (this.currentDocumentsType != null) {
            this.headerCell = new HeaderCell(context2);
            if (this.documentOnly) {
                this.headerCell.setText(LocaleController.getString("PassportDocuments", NUM));
            } else {
                this.headerCell.setText(LocaleController.getString("PassportRequiredDocuments", NUM));
            }
            this.headerCell.setBackgroundColor(Theme.getColor(str2));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            this.frontLayout = new LinearLayout(context2);
            this.frontLayout.setOrientation(1);
            this.linearLayout2.addView(this.frontLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell = new TextDetailSettingsCell(context2);
            this.uploadFrontCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadFrontCell, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell.setOnClickListener(new -$$Lambda$PassportActivity$c9kJTe5mHBur0ZKYsiTqGUHyE0o(this));
            this.reverseLayout = new LinearLayout(context2);
            this.reverseLayout.setOrientation(1);
            this.linearLayout2.addView(this.reverseLayout, LayoutHelper.createLinear(-1, -2));
            boolean z2 = this.currentDocumentsType.selfie_required;
            this.uploadReverseCell = new TextDetailSettingsCell(context2);
            this.uploadReverseCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.uploadReverseCell.setTextAndValue(LocaleController.getString("PassportReverseSide", NUM), LocaleController.getString("PassportReverseSideInfo", NUM), z2);
            this.linearLayout2.addView(this.uploadReverseCell, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell.setOnClickListener(new -$$Lambda$PassportActivity$jVyNHBa_n1RAjexJFiTwuhwl3wk(this));
            if (this.currentDocumentsType.selfie_required) {
                this.selfieLayout = new LinearLayout(context2);
                this.selfieLayout.setOrientation(1);
                this.linearLayout2.addView(this.selfieLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell = new TextDetailSettingsCell(context2);
                this.uploadSelfieCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.uploadSelfieCell.setTextAndValue(LocaleController.getString("PassportSelfie", NUM), LocaleController.getString("PassportSelfieInfo", NUM), this.currentType.translation_required);
                this.linearLayout2.addView(this.uploadSelfieCell, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell.setOnClickListener(new -$$Lambda$PassportActivity$80pcXC9JPBzJ3vgi80hMNDT2KC4(this));
            }
            this.bottomCell = new TextInfoPrivacyCell(context2);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            this.bottomCell.setText(LocaleController.getString("PassportPersonalUploadInfo", NUM));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                this.headerCell = new HeaderCell(context2);
                this.headerCell.setText(LocaleController.getString("PassportTranslation", NUM));
                this.headerCell.setBackgroundColor(Theme.getColor(str2));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                this.translationLayout = new LinearLayout(context2);
                this.translationLayout.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell = new TextSettingsCell(context2);
                this.uploadTranslationCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new -$$Lambda$PassportActivity$ijAn79XSwEsHcuejC6hU9kEtOuA(this));
                this.bottomCellTranslation = new TextInfoPrivacyCell(context2);
                this.bottomCellTranslation.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", NUM);
                } else {
                    SecureValueType secureValueType = this.currentDocumentsType.type;
                    if (secureValueType instanceof TL_secureValueTypePassport) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddPassportInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypeInternalPassport) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddInternalPassportInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypeIdentityCard) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddIdentityCardInfo", NUM);
                    } else if (secureValueType instanceof TL_secureValueTypeDriverLicense) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddDriverLicenceInfo", NUM);
                    } else {
                        this.noAllTranslationErrorText = "";
                    }
                }
                CharSequence charSequence = this.noAllTranslationErrorText;
                HashMap hashMap = this.documentsErrors;
                if (hashMap != null) {
                    String str3 = (String) hashMap.get("translation_all");
                    if (str3 != null) {
                        charSequence = new SpannableStringBuilder(str3);
                        charSequence.append("\n\n");
                        charSequence.append(this.noAllTranslationErrorText);
                        charSequence.setSpan(new ForegroundColorSpan(Theme.getColor(readLine)), 0, str3.length(), 33);
                        this.errorsValues.put("translation_all", "");
                    }
                }
                this.bottomCellTranslation.setText(charSequence);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else if (VERSION.SDK_INT >= 18) {
            this.scanDocumentCell = new TextSettingsCell(context2);
            this.scanDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.scanDocumentCell.setText(LocaleController.getString("PassportScanPassport", NUM), false);
            this.linearLayout2.addView(this.scanDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.scanDocumentCell.setOnClickListener(new -$$Lambda$PassportActivity$76809PrGNfIFwFdK4EfyZ7l0wQM(this));
            this.bottomCell = new TextInfoPrivacyCell(context2);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
            this.bottomCell.setText(LocaleController.getString("PassportScanPassportInfo", NUM));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.headerCell = new HeaderCell(context2);
        if (this.documentOnly) {
            this.headerCell.setText(LocaleController.getString("PassportDocument", NUM));
        } else {
            this.headerCell.setText(LocaleController.getString("PassportPersonal", NUM));
        }
        this.headerCell.setBackgroundColor(Theme.getColor(str2));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        int i2 = 7;
        int i3 = this.currentDocumentsType != null ? 9 : 7;
        this.inputFields = new EditTextBoldCursor[i3];
        int i4 = 0;
        while (true) {
            String str4 = "windowBackgroundWhiteBlackText";
            HashMap hashMap2;
            String str5;
            if (i4 < i3) {
                final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
                this.inputFields[i4] = editTextBoldCursor;
                AnonymousClass14 anonymousClass14 = new FrameLayout(context2) {
                    private StaticLayout errorLayout;
                    private float offsetX;

                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        int size = MeasureSpec.getSize(i) - AndroidUtilities.dp(34.0f);
                        this.errorLayout = editTextBoldCursor.getErrorLayout(size);
                        StaticLayout staticLayout = this.errorLayout;
                        if (staticLayout != null) {
                            int lineCount = staticLayout.getLineCount();
                            int i3 = 0;
                            if (lineCount > 1) {
                                i2 = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                            }
                            if (LocaleController.isRTL) {
                                float f = 0.0f;
                                while (i3 < lineCount) {
                                    if (this.errorLayout.getLineLeft(i3) != 0.0f) {
                                        this.offsetX = 0.0f;
                                        break;
                                    }
                                    f = Math.max(f, this.errorLayout.getLineWidth(i3));
                                    if (i3 == lineCount - 1) {
                                        this.offsetX = ((float) size) - f;
                                    }
                                    i3++;
                                }
                            }
                        }
                        super.onMeasure(i, i2);
                    }

                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        if (this.errorLayout != null) {
                            canvas.save();
                            canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                            this.errorLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                };
                anonymousClass14.setWillNotDraw(z);
                this.linearLayout2.addView(anonymousClass14, LayoutHelper.createLinear(i, 64));
                anonymousClass14.setBackgroundColor(Theme.getColor(str2));
                if (i4 == i3 - 1) {
                    this.extraBackgroundView = new View(context2);
                    this.extraBackgroundView.setBackgroundColor(Theme.getColor(str2));
                    this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(i, 6));
                }
                if (this.documentOnly && this.currentDocumentsType != null && i4 < i2) {
                    anonymousClass14.setVisibility(8);
                    View view = this.extraBackgroundView;
                    if (view != null) {
                        view.setVisibility(8);
                    }
                }
                this.inputFields[i4].setTag(Integer.valueOf(i4));
                this.inputFields[i4].setSupportRtlHint(true);
                this.inputFields[i4].setTextSize(1, 16.0f);
                this.inputFields[i4].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.inputFields[i4].setTextColor(Theme.getColor(str4));
                this.inputFields[i4].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                this.inputFields[i4].setTransformHintToHeader(true);
                this.inputFields[i4].setBackgroundDrawable(null);
                this.inputFields[i4].setCursorColor(Theme.getColor(str4));
                this.inputFields[i4].setCursorSize(AndroidUtilities.dp(20.0f));
                this.inputFields[i4].setCursorWidth(1.5f);
                this.inputFields[i4].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor(readLine));
                if (i4 == 5 || i4 == 6) {
                    this.inputFields[i4].setOnTouchListener(new -$$Lambda$PassportActivity$BkWsiGp0SACn1vJ9h8YBMYKkLVM(this));
                    this.inputFields[i4].setInputType(0);
                } else if (i4 == 3 || i4 == 8) {
                    this.inputFields[i4].setOnTouchListener(new -$$Lambda$PassportActivity$TcnlWpKn54098zIjNVHAKGv7mPQ(this, context2));
                    this.inputFields[i4].setInputType(0);
                    this.inputFields[i4].setFocusable(false);
                } else if (i4 == 4) {
                    this.inputFields[i4].setOnTouchListener(new -$$Lambda$PassportActivity$-CPWpKIngk2P0GwmkCG7jNpTBgM(this));
                    this.inputFields[i4].setInputType(0);
                    this.inputFields[i4].setFocusable(false);
                } else {
                    this.inputFields[i4].setInputType(16385);
                    this.inputFields[i4].setImeOptions(NUM);
                }
                switch (i4) {
                    case 0:
                        if (this.currentType.native_names) {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportNameLatin", NUM));
                        } else {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportName", NUM));
                        }
                        hashMap2 = this.currentValues;
                        str5 = "first_name";
                        break;
                    case 1:
                        if (this.currentType.native_names) {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportMidnameLatin", NUM));
                        } else {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportMidname", NUM));
                        }
                        hashMap2 = this.currentValues;
                        str5 = "middle_name";
                        break;
                    case 2:
                        if (this.currentType.native_names) {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportSurnameLatin", NUM));
                        } else {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportSurname", NUM));
                        }
                        hashMap2 = this.currentValues;
                        str5 = "last_name";
                        break;
                    case 3:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportBirthdate", NUM));
                        hashMap2 = this.currentValues;
                        str5 = "birth_date";
                        break;
                    case 4:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportGender", NUM));
                        hashMap2 = this.currentValues;
                        str5 = "gender";
                        break;
                    case 5:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportCitizenship", NUM));
                        hashMap2 = this.currentValues;
                        str5 = "country_code";
                        break;
                    case 6:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportResidence", NUM));
                        hashMap2 = this.currentValues;
                        str5 = "residence_country_code";
                        break;
                    case 7:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportDocumentNumber", NUM));
                        hashMap2 = this.currentDocumentValues;
                        str5 = "document_no";
                        break;
                    case 8:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportExpired", NUM));
                        hashMap2 = this.currentDocumentValues;
                        str5 = "expiry_date";
                        break;
                    default:
                        break;
                }
                setFieldValues(hashMap2, this.inputFields[i4], str5);
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                editTextBoldCursorArr[i4].setSelection(editTextBoldCursorArr[i4].length());
                if (i4 == 0 || i4 == 2 || i4 == 1) {
                    this.inputFields[i4].addTextChangedListener(new TextWatcher() {
                        private boolean ignore;

                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (!this.ignore) {
                                boolean z;
                                int intValue = ((Integer) editTextBoldCursor.getTag()).intValue();
                                for (int i = 0; i < editable.length(); i++) {
                                    char charAt = editable.charAt(i);
                                    if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && !((charAt >= 'A' && charAt <= 'Z') || charAt == ' ' || charAt == '\'' || charAt == ',' || charAt == '.' || charAt == '&' || charAt == '-' || charAt == '/'))) {
                                        z = true;
                                        break;
                                    }
                                }
                                z = false;
                                if (!z || PassportActivity.this.allowNonLatinName) {
                                    PassportActivity.this.nonLatinNames[intValue] = z;
                                    PassportActivity.this.checkFieldForError(editTextBoldCursor, str5, editable, false);
                                } else {
                                    editTextBoldCursor.setErrorText(LocaleController.getString("PassportUseLatinOnly", NUM));
                                }
                            }
                        }
                    });
                } else {
                    this.inputFields[i4].addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            PassportActivity passportActivity = PassportActivity.this;
                            passportActivity.checkFieldForError(editTextBoldCursor, str5, editable, hashMap2 == passportActivity.currentDocumentValues);
                            int intValue = ((Integer) editTextBoldCursor.getTag()).intValue();
                            EditTextBoldCursor editTextBoldCursor = PassportActivity.this.inputFields[intValue];
                            if (intValue == 6) {
                                PassportActivity.this.checkNativeFields(true);
                            }
                        }
                    });
                }
                this.inputFields[i4].setPadding(0, 0, 0, 0);
                this.inputFields[i4].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                anonymousClass14.addView(this.inputFields[i4], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                this.inputFields[i4].setOnEditorActionListener(new -$$Lambda$PassportActivity$IUKnReQZFqjMBZyTSuqCOeb27jI(this));
                i4++;
                z = false;
                i = -1;
                i2 = 7;
            } else {
                this.sectionCell2 = new ShadowSectionCell(context2);
                this.linearLayout2.addView(this.sectionCell2, LayoutHelper.createLinear(-1, -2));
                this.headerCell = new HeaderCell(context2);
                this.headerCell.setBackgroundColor(Theme.getColor(str2));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                i3 = 3;
                this.inputExtraFields = new EditTextBoldCursor[3];
                int i5 = 0;
                while (i5 < i3) {
                    final EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
                    this.inputExtraFields[i5] = editTextBoldCursor2;
                    AnonymousClass17 anonymousClass17 = new FrameLayout(context2) {
                        private StaticLayout errorLayout;
                        private float offsetX;

                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int i, int i2) {
                            int size = MeasureSpec.getSize(i) - AndroidUtilities.dp(34.0f);
                            this.errorLayout = editTextBoldCursor2.getErrorLayout(size);
                            StaticLayout staticLayout = this.errorLayout;
                            if (staticLayout != null) {
                                int lineCount = staticLayout.getLineCount();
                                int i3 = 0;
                                if (lineCount > 1) {
                                    i2 = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                                }
                                if (LocaleController.isRTL) {
                                    float f = 0.0f;
                                    while (i3 < lineCount) {
                                        if (this.errorLayout.getLineLeft(i3) != 0.0f) {
                                            this.offsetX = 0.0f;
                                            break;
                                        }
                                        f = Math.max(f, this.errorLayout.getLineWidth(i3));
                                        if (i3 == lineCount - 1) {
                                            this.offsetX = ((float) size) - f;
                                        }
                                        i3++;
                                    }
                                }
                            }
                            super.onMeasure(i, i2);
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
                    anonymousClass17.setWillNotDraw(false);
                    this.linearLayout2.addView(anonymousClass17, LayoutHelper.createLinear(-1, 64));
                    anonymousClass17.setBackgroundColor(Theme.getColor(str2));
                    if (i5 == 2) {
                        this.extraBackgroundView2 = new View(context2);
                        this.extraBackgroundView2.setBackgroundColor(Theme.getColor(str2));
                        this.linearLayout2.addView(this.extraBackgroundView2, LayoutHelper.createLinear(-1, 6));
                    }
                    this.inputExtraFields[i5].setTag(Integer.valueOf(i5));
                    this.inputExtraFields[i5].setSupportRtlHint(true);
                    this.inputExtraFields[i5].setTextSize(1, 16.0f);
                    this.inputExtraFields[i5].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                    this.inputExtraFields[i5].setTextColor(Theme.getColor(str4));
                    this.inputExtraFields[i5].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                    this.inputExtraFields[i5].setTransformHintToHeader(true);
                    this.inputExtraFields[i5].setBackgroundDrawable(null);
                    this.inputExtraFields[i5].setCursorColor(Theme.getColor(str4));
                    this.inputExtraFields[i5].setCursorSize(AndroidUtilities.dp(20.0f));
                    this.inputExtraFields[i5].setCursorWidth(1.5f);
                    this.inputExtraFields[i5].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor(readLine));
                    this.inputExtraFields[i5].setInputType(16385);
                    this.inputExtraFields[i5].setImeOptions(NUM);
                    if (i5 == 0) {
                        hashMap2 = this.currentValues;
                        str5 = "first_name_native";
                    } else if (i5 == 1) {
                        hashMap2 = this.currentValues;
                        str5 = "middle_name_native";
                    } else if (i5 != 2) {
                        i5++;
                        i3 = 3;
                    } else {
                        hashMap2 = this.currentValues;
                        str5 = "last_name_native";
                    }
                    setFieldValues(hashMap2, this.inputExtraFields[i5], str5);
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    editTextBoldCursorArr2[i5].setSelection(editTextBoldCursorArr2[i5].length());
                    if (i5 == 0 || i5 == 2 || i5 == 1) {
                        this.inputExtraFields[i5].addTextChangedListener(new TextWatcher() {
                            private boolean ignore;

                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void afterTextChanged(Editable editable) {
                                if (!this.ignore) {
                                    PassportActivity.this.checkFieldForError(editTextBoldCursor2, str5, editable, false);
                                }
                            }
                        });
                    }
                    this.inputExtraFields[i5].setPadding(0, 0, 0, 0);
                    this.inputExtraFields[i5].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    anonymousClass17.addView(this.inputExtraFields[i5], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                    this.inputExtraFields[i5].setOnEditorActionListener(new -$$Lambda$PassportActivity$2R-VniY9bMOiViEoeO5veQ3eJ7w(this));
                    i5++;
                    i3 = 3;
                }
                this.nativeInfoCell = new TextInfoPrivacyCell(context2);
                this.linearLayout2.addView(this.nativeInfoCell, LayoutHelper.createLinear(-1, -2));
                if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
                } else {
                    TL_secureValue tL_secureValue = this.currentDocumentsTypeValue;
                    if (tL_secureValue != null) {
                        addDocumentViews(tL_secureValue.files);
                        SecureFile secureFile = this.currentDocumentsTypeValue.front_side;
                        if (secureFile instanceof TL_secureFile) {
                            addDocumentViewInternal((TL_secureFile) secureFile, 2);
                        }
                        secureFile = this.currentDocumentsTypeValue.reverse_side;
                        if (secureFile instanceof TL_secureFile) {
                            addDocumentViewInternal((TL_secureFile) secureFile, 3);
                        }
                        secureFile = this.currentDocumentsTypeValue.selfie;
                        if (secureFile instanceof TL_secureFile) {
                            addDocumentViewInternal((TL_secureFile) secureFile, 1);
                        }
                        addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
                    }
                    TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
                    textSettingsCell.setTextColor(Theme.getColor(readLine));
                    textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    if (this.currentDocumentsType == null) {
                        textSettingsCell.setText(LocaleController.getString("PassportDeleteInfo", NUM), false);
                    } else {
                        textSettingsCell.setText(LocaleController.getString("PassportDeleteDocument", NUM), false);
                    }
                    this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
                    textSettingsCell.setOnClickListener(new -$$Lambda$PassportActivity$gpc1SNZjQfFOYB3jBSNPORk4F_s(this));
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
                    this.sectionCell = new ShadowSectionCell(context2);
                    this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str));
                    this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
                }
                updateInterfaceStringsForDocumentType();
                checkNativeFields(false);
                return;
            }
        }
    }

    public /* synthetic */ void lambda$createIdentityInterface$40$PassportActivity(View view) {
        this.uploadingFileType = 2;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$41$PassportActivity(View view) {
        this.uploadingFileType = 3;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$42$PassportActivity(View view) {
        this.uploadingFileType = 1;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$43$PassportActivity(View view) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$45$PassportActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            if (getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                getParentActivity().requestPermissions(new String[]{r0}, 22);
                return;
            }
        }
        MrzCameraActivity mrzCameraActivity = new MrzCameraActivity();
        mrzCameraActivity.setDelegate(new -$$Lambda$PassportActivity$jIvyCfLzFIjgzntur0HgCfk6_Pc(this));
        presentFragment(mrzCameraActivity);
    }

    public /* synthetic */ void lambda$null$44$PassportActivity(Result result) {
        String str;
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        int i = result.gender;
        if (i != 0) {
            if (i == 1) {
                this.currentGender = "male";
                this.inputFields[4].setText(LocaleController.getString("PassportMale", NUM));
            } else if (i == 2) {
                this.currentGender = "female";
                this.inputFields[4].setText(LocaleController.getString("PassportFemale", NUM));
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            this.currentCitizeship = result.nationality;
            str = (String) this.languageMap.get(this.currentCitizeship);
            if (str != null) {
                this.inputFields[5].setText(str);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            this.currentResidence = result.issuingCountry;
            str = (String) this.languageMap.get(this.currentResidence);
            if (str != null) {
                this.inputFields[6].setText(str);
            }
        }
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(i), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$47$PassportActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$PassportActivity$bEhtGHiukso4FocfB8ZhvxICXI8(this, view));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$46$PassportActivity(View view, String str, String str2) {
        int intValue = ((Integer) view.getTag()).intValue();
        EditText editText = this.inputFields[intValue];
        if (intValue == 5) {
            this.currentCitizeship = str2;
        } else {
            this.currentResidence = str2;
        }
        editText.setText(str);
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$50$PassportActivity(Context context, View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            Calendar instance = Calendar.getInstance();
            instance.get(1);
            instance.get(2);
            instance.get(5);
            try {
                String string;
                int i;
                int i2;
                int i3;
                int intValue;
                int i4;
                int i5;
                EditTextBoldCursor editTextBoldCursor = (EditTextBoldCursor) view;
                int intValue2 = ((Integer) editTextBoldCursor.getTag()).intValue();
                if (intValue2 == 8) {
                    string = LocaleController.getString("PassportSelectExpiredDate", NUM);
                    i = 0;
                    i2 = 20;
                    i3 = 0;
                } else {
                    string = LocaleController.getString("PassportSelectBithdayDate", NUM);
                    i = -120;
                    i2 = 0;
                    i3 = -18;
                }
                String[] split = editTextBoldCursor.getText().toString().split("\\.");
                if (split.length == 3) {
                    int intValue3 = Utilities.parseInt(split[0]).intValue();
                    int intValue4 = Utilities.parseInt(split[1]).intValue();
                    intValue = Utilities.parseInt(split[2]).intValue();
                    i4 = intValue3;
                    i5 = intValue4;
                } else {
                    i4 = -1;
                    i5 = -1;
                    intValue = -1;
                }
                Builder createDatePickerDialog = AlertsCreator.createDatePickerDialog(context, i, i2, i3, i4, i5, intValue, string, intValue2 == 8, new -$$Lambda$PassportActivity$PBoR-i1NCFH_2XahUVDKl9oJw5I(this, intValue2, editTextBoldCursor));
                if (intValue2 == 8) {
                    createDatePickerDialog.setNegativeButton(LocaleController.getString("PassportSelectNotExpire", NUM), new -$$Lambda$PassportActivity$hkQKt9FoeeVbVy2IQ1nJvNtE5mU(this, editTextBoldCursor));
                }
                showDialog(createDatePickerDialog.create());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$null$48$PassportActivity(int i, EditTextBoldCursor editTextBoldCursor, int i2, int i3, int i4) {
        if (i == 8) {
            int[] iArr = this.currentExpireDate;
            iArr[0] = i2;
            iArr[1] = i3 + 1;
            iArr[2] = i4;
        }
        editTextBoldCursor.setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i3 + 1), Integer.valueOf(i2)}));
    }

    public /* synthetic */ void lambda$null$49$PassportActivity(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i) {
        int[] iArr = this.currentExpireDate;
        iArr[2] = 0;
        iArr[1] = 0;
        iArr[0] = 0;
        editTextBoldCursor.setText(LocaleController.getString("PassportNoExpireDate", NUM));
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$52$PassportActivity(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PassportSelectGender", NUM));
            builder.setItems(new CharSequence[]{LocaleController.getString("PassportMale", NUM), LocaleController.getString("PassportFemale", NUM)}, new -$$Lambda$PassportActivity$xyrt_3Facds-cGPGxpzwS2nYi4s(this));
            builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
        return true;
    }

    public /* synthetic */ void lambda$null$51$PassportActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.currentGender = "male";
            this.inputFields[4].setText(LocaleController.getString("PassportMale", NUM));
        } else if (i == 1) {
            this.currentGender = "female";
            this.inputFields[4].setText(LocaleController.getString("PassportFemale", NUM));
        }
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$53$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        i = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (i < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[i].isFocusable()) {
                this.inputFields[i].requestFocus();
            } else {
                this.inputFields[i].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$54$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        i = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (i < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[i].isFocusable()) {
                this.inputExtraFields[i].requestFocus();
            } else {
                this.inputExtraFields[i].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$createIdentityInterface$55$PassportActivity(View view) {
        createDocumentDeleteAlert();
    }

    private void updateInterfaceStringsForDocumentType() {
        TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
        if (tL_secureRequiredType != null) {
            this.actionBar.setTitle(getTextForType(tL_secureRequiredType.type));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportPersonal", NUM));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
        updateUploadText(4);
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c9  */
    /* JADX WARNING: Missing block: B:39:0x0083, code skipped:
            if ((r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense) == false) goto L_0x0086;
     */
    private void updateUploadText(int r9) {
        /*
        r8 = this;
        r0 = NUM; // 0x7f0d0793 float:1.8746047E38 double:1.0531307355E-314;
        r1 = "PassportUploadAdditinalDocument";
        r2 = NUM; // 0x7f0d0794 float:1.874605E38 double:1.053130736E-314;
        r3 = "PassportUploadDocument";
        r4 = 1;
        r5 = 0;
        if (r9 != 0) goto L_0x0031;
    L_0x000e:
        r9 = r8.uploadDocumentCell;
        if (r9 != 0) goto L_0x0013;
    L_0x0012:
        return;
    L_0x0013:
        r9 = r8.documents;
        r9 = r9.size();
        if (r9 < r4) goto L_0x0026;
    L_0x001b:
        r9 = r8.uploadDocumentCell;
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r9.setText(r0, r5);
        goto L_0x00ff;
    L_0x0026:
        r9 = r8.uploadDocumentCell;
        r0 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r9.setText(r0, r5);
        goto L_0x00ff;
    L_0x0031:
        r6 = 8;
        if (r9 != r4) goto L_0x0045;
    L_0x0035:
        r9 = r8.uploadSelfieCell;
        if (r9 != 0) goto L_0x003a;
    L_0x0039:
        return;
    L_0x003a:
        r0 = r8.selfieDocument;
        if (r0 == 0) goto L_0x0040;
    L_0x003e:
        r5 = 8;
    L_0x0040:
        r9.setVisibility(r5);
        goto L_0x00ff;
    L_0x0045:
        r7 = 4;
        if (r9 != r7) goto L_0x006b;
    L_0x0048:
        r9 = r8.uploadTranslationCell;
        if (r9 != 0) goto L_0x004d;
    L_0x004c:
        return;
    L_0x004d:
        r9 = r8.translationDocuments;
        r9 = r9.size();
        if (r9 < r4) goto L_0x0060;
    L_0x0055:
        r9 = r8.uploadTranslationCell;
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r9.setText(r0, r5);
        goto L_0x00ff;
    L_0x0060:
        r9 = r8.uploadTranslationCell;
        r0 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r9.setText(r0, r5);
        goto L_0x00ff;
    L_0x006b:
        r0 = 2;
        if (r9 != r0) goto L_0x00cf;
    L_0x006e:
        r9 = r8.uploadFrontCell;
        if (r9 != 0) goto L_0x0073;
    L_0x0072:
        return;
    L_0x0073:
        r9 = r8.currentDocumentsType;
        if (r9 == 0) goto L_0x0086;
    L_0x0077:
        r0 = r9.selfie_required;
        if (r0 != 0) goto L_0x0087;
    L_0x007b:
        r9 = r9.type;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeIdentityCard;
        if (r0 != 0) goto L_0x0087;
    L_0x0081:
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense;
        if (r9 == 0) goto L_0x0086;
    L_0x0085:
        goto L_0x0087;
    L_0x0086:
        r4 = 0;
    L_0x0087:
        r9 = r8.currentDocumentsType;
        r9 = r9.type;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePassport;
        if (r0 != 0) goto L_0x00ac;
    L_0x008f:
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeInternalPassport;
        if (r9 == 0) goto L_0x0094;
    L_0x0093:
        goto L_0x00ac;
    L_0x0094:
        r9 = r8.uploadFrontCell;
        r0 = NUM; // 0x7f0d0720 float:1.8745814E38 double:1.0531306787E-314;
        r1 = "PassportFrontSide";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r1 = NUM; // 0x7f0d0721 float:1.8745816E38 double:1.053130679E-314;
        r2 = "PassportFrontSideInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r9.setTextAndValue(r0, r1, r4);
        goto L_0x00c3;
    L_0x00ac:
        r9 = r8.uploadFrontCell;
        r0 = NUM; // 0x7f0d075a float:1.8745932E38 double:1.0531307074E-314;
        r1 = "PassportMainPage";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r1 = NUM; // 0x7f0d075b float:1.8745934E38 double:1.053130708E-314;
        r2 = "PassportMainPageInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r9.setTextAndValue(r0, r1, r4);
    L_0x00c3:
        r9 = r8.uploadFrontCell;
        r0 = r8.frontDocument;
        if (r0 == 0) goto L_0x00cb;
    L_0x00c9:
        r5 = 8;
    L_0x00cb:
        r9.setVisibility(r5);
        goto L_0x00ff;
    L_0x00cf:
        r0 = 3;
        if (r9 != r0) goto L_0x00ff;
    L_0x00d2:
        r9 = r8.uploadReverseCell;
        if (r9 != 0) goto L_0x00d7;
    L_0x00d6:
        return;
    L_0x00d7:
        r9 = r8.currentDocumentsType;
        r9 = r9.type;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeIdentityCard;
        if (r0 != 0) goto L_0x00ef;
    L_0x00df:
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense;
        if (r9 == 0) goto L_0x00e4;
    L_0x00e3:
        goto L_0x00ef;
    L_0x00e4:
        r9 = r8.reverseLayout;
        r9.setVisibility(r6);
        r9 = r8.uploadReverseCell;
        r9.setVisibility(r6);
        goto L_0x00ff;
    L_0x00ef:
        r9 = r8.reverseLayout;
        r9.setVisibility(r5);
        r9 = r8.uploadReverseCell;
        r0 = r8.reverseDocument;
        if (r0 == 0) goto L_0x00fc;
    L_0x00fa:
        r5 = 8;
    L_0x00fc:
        r9.setVisibility(r5);
    L_0x00ff:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.updateUploadText(int):void");
    }

    private void checkTopErrorCell(boolean z) {
        if (this.topErrorCell != null) {
            String str;
            SpannableStringBuilder spannableStringBuilder = null;
            String str2 = "";
            String str3 = "error_all";
            if (this.fieldsErrors != null && (z || this.errorsValues.containsKey(str3))) {
                str = (String) this.fieldsErrors.get(str3);
                if (str != null) {
                    spannableStringBuilder = new SpannableStringBuilder(str);
                    if (z) {
                        this.errorsValues.put(str3, str2);
                    }
                }
            }
            if (this.documentsErrors != null) {
                str = "error_document_all";
                if (z || this.errorsValues.containsKey(str)) {
                    str3 = (String) this.documentsErrors.get(str3);
                    if (str3 != null) {
                        if (spannableStringBuilder == null) {
                            spannableStringBuilder = new SpannableStringBuilder(str3);
                        } else {
                            spannableStringBuilder.append("\n\n").append(str3);
                        }
                        if (z) {
                            this.errorsValues.put(str, str2);
                        }
                    }
                }
            }
            if (spannableStringBuilder != null) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, spannableStringBuilder.length(), 33);
                this.topErrorCell.setText(spannableStringBuilder);
                this.topErrorCell.setVisibility(0);
            } else if (this.topErrorCell.getVisibility() != 8) {
                this.topErrorCell.setVisibility(8);
            }
        }
    }

    private void addDocumentViewInternal(TL_secureFile tL_secureFile, int i) {
        addDocumentView(new SecureDocument(getSecureDocumentKey(tL_secureFile.secret, tL_secureFile.file_hash), tL_secureFile, null, null, null), i);
    }

    private void addDocumentViews(ArrayList<SecureFile> arrayList) {
        this.documents.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            SecureFile secureFile = (SecureFile) arrayList.get(i);
            if (secureFile instanceof TL_secureFile) {
                addDocumentViewInternal((TL_secureFile) secureFile, 0);
            }
        }
    }

    private void addTranslationDocumentViews(ArrayList<SecureFile> arrayList) {
        this.translationDocuments.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            SecureFile secureFile = (SecureFile) arrayList.get(i);
            if (secureFile instanceof TL_secureFile) {
                addDocumentViewInternal((TL_secureFile) secureFile, 4);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x008a  */
    private void setFieldValues(java.util.HashMap<java.lang.String, java.lang.String> r7, org.telegram.ui.Components.EditTextBoldCursor r8, java.lang.String r9) {
        /*
        r6 = this;
        r7 = r7.get(r9);
        r7 = (java.lang.String) r7;
        if (r7 == 0) goto L_0x00f0;
    L_0x0008:
        r0 = -1;
        r1 = r9.hashCode();
        r2 = 3;
        r3 = 2;
        r4 = 0;
        r5 = 1;
        switch(r1) {
            case -2006252145: goto L_0x0033;
            case -1249512767: goto L_0x0029;
            case 475919162: goto L_0x001f;
            case 1481071862: goto L_0x0015;
            default: goto L_0x0014;
        };
    L_0x0014:
        goto L_0x003c;
    L_0x0015:
        r1 = "country_code";
        r1 = r9.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x001d:
        r0 = 0;
        goto L_0x003c;
    L_0x001f:
        r1 = "expiry_date";
        r1 = r9.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x0027:
        r0 = 3;
        goto L_0x003c;
    L_0x0029:
        r1 = "gender";
        r1 = r9.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x0031:
        r0 = 2;
        goto L_0x003c;
    L_0x0033:
        r1 = "residence_country_code";
        r1 = r9.equals(r1);
        if (r1 == 0) goto L_0x003c;
    L_0x003b:
        r0 = 1;
    L_0x003c:
        if (r0 == 0) goto L_0x00df;
    L_0x003e:
        if (r0 == r5) goto L_0x00cd;
    L_0x0040:
        if (r0 == r3) goto L_0x009f;
    L_0x0042:
        if (r0 == r2) goto L_0x0049;
    L_0x0044:
        r8.setText(r7);
        goto L_0x00f0;
    L_0x0049:
        r0 = android.text.TextUtils.isEmpty(r7);
        if (r0 != 0) goto L_0x0087;
    L_0x004f:
        r0 = "\\.";
        r0 = r7.split(r0);
        r1 = r0.length;
        if (r1 != r2) goto L_0x0087;
    L_0x0058:
        r1 = r6.currentExpireDate;
        r2 = r0[r3];
        r2 = org.telegram.messenger.Utilities.parseInt(r2);
        r2 = r2.intValue();
        r1[r4] = r2;
        r1 = r6.currentExpireDate;
        r2 = r0[r5];
        r2 = org.telegram.messenger.Utilities.parseInt(r2);
        r2 = r2.intValue();
        r1[r5] = r2;
        r1 = r6.currentExpireDate;
        r0 = r0[r4];
        r0 = org.telegram.messenger.Utilities.parseInt(r0);
        r0 = r0.intValue();
        r1[r3] = r0;
        r8.setText(r7);
        r7 = 1;
        goto L_0x0088;
    L_0x0087:
        r7 = 0;
    L_0x0088:
        if (r7 != 0) goto L_0x00f0;
    L_0x008a:
        r7 = r6.currentExpireDate;
        r7[r3] = r4;
        r7[r5] = r4;
        r7[r4] = r4;
        r7 = NUM; // 0x7f0d076a float:1.8745964E38 double:1.0531307153E-314;
        r0 = "PassportNoExpireDate";
        r7 = org.telegram.messenger.LocaleController.getString(r0, r7);
        r8.setText(r7);
        goto L_0x00f0;
    L_0x009f:
        r0 = "male";
        r0 = r0.equals(r7);
        if (r0 == 0) goto L_0x00b6;
    L_0x00a7:
        r6.currentGender = r7;
        r7 = NUM; // 0x7f0d075c float:1.8745936E38 double:1.0531307084E-314;
        r0 = "PassportMale";
        r7 = org.telegram.messenger.LocaleController.getString(r0, r7);
        r8.setText(r7);
        goto L_0x00f0;
    L_0x00b6:
        r0 = "female";
        r0 = r0.equals(r7);
        if (r0 == 0) goto L_0x00f0;
    L_0x00be:
        r6.currentGender = r7;
        r7 = NUM; // 0x7f0d071f float:1.8745812E38 double:1.053130678E-314;
        r0 = "PassportFemale";
        r7 = org.telegram.messenger.LocaleController.getString(r0, r7);
        r8.setText(r7);
        goto L_0x00f0;
    L_0x00cd:
        r6.currentResidence = r7;
        r7 = r6.languageMap;
        r0 = r6.currentResidence;
        r7 = r7.get(r0);
        r7 = (java.lang.String) r7;
        if (r7 == 0) goto L_0x00f0;
    L_0x00db:
        r8.setText(r7);
        goto L_0x00f0;
    L_0x00df:
        r6.currentCitizeship = r7;
        r7 = r6.languageMap;
        r0 = r6.currentCitizeship;
        r7 = r7.get(r0);
        r7 = (java.lang.String) r7;
        if (r7 == 0) goto L_0x00f0;
    L_0x00ed:
        r8.setText(r7);
    L_0x00f0:
        r7 = r6.fieldsErrors;
        if (r7 == 0) goto L_0x010d;
    L_0x00f4:
        r7 = r7.get(r9);
        r7 = (java.lang.String) r7;
        if (r7 == 0) goto L_0x010d;
    L_0x00fc:
        r8.setErrorText(r7);
        r7 = r6.errorsValues;
        r8 = r8.getText();
        r8 = r8.toString();
        r7.put(r9, r8);
        goto L_0x0129;
    L_0x010d:
        r7 = r6.documentsErrors;
        if (r7 == 0) goto L_0x0129;
    L_0x0111:
        r7 = r7.get(r9);
        r7 = (java.lang.String) r7;
        if (r7 == 0) goto L_0x0129;
    L_0x0119:
        r8.setErrorText(r7);
        r7 = r6.errorsValues;
        r8 = r8.getText();
        r8 = r8.toString();
        r7.put(r9, r8);
    L_0x0129:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setFieldValues(java.util.HashMap, org.telegram.ui.Components.EditTextBoldCursor, java.lang.String):void");
    }

    private void addDocumentView(SecureDocument secureDocument, int i) {
        if (i == 1) {
            this.selfieDocument = secureDocument;
            if (this.selfieLayout == null) {
                return;
            }
        } else if (i == 4) {
            this.translationDocuments.add(secureDocument);
            if (this.translationLayout == null) {
                return;
            }
        } else if (i == 2) {
            this.frontDocument = secureDocument;
            if (this.frontLayout == null) {
                return;
            }
        } else if (i == 3) {
            this.reverseDocument = secureDocument;
            if (this.reverseLayout == null) {
                return;
            }
        } else {
            this.documents.add(secureDocument);
            if (this.documentsLayout == null) {
                return;
            }
        }
        if (getParentActivity() != null) {
            String string;
            String stringBuilder;
            CharSequence charSequence;
            SecureDocumentCell secureDocumentCell = new SecureDocumentCell(getParentActivity());
            secureDocumentCell.setTag(secureDocument);
            secureDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.documentsCells.put(secureDocument, secureDocumentCell);
            String documentHash = getDocumentHash(secureDocument);
            StringBuilder stringBuilder2;
            if (i == 1) {
                string = LocaleController.getString("PassportSelfie", NUM);
                this.selfieLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("selfie");
                stringBuilder2.append(documentHash);
                stringBuilder = stringBuilder2.toString();
            } else {
                String str = "AttachPhoto";
                if (i == 4) {
                    string = LocaleController.getString(str, NUM);
                    this.translationLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("translation");
                    stringBuilder2.append(documentHash);
                    stringBuilder = stringBuilder2.toString();
                } else if (i == 2) {
                    SecureValueType secureValueType = this.currentDocumentsType.type;
                    if ((secureValueType instanceof TL_secureValueTypePassport) || (secureValueType instanceof TL_secureValueTypeInternalPassport)) {
                        string = LocaleController.getString("PassportMainPage", NUM);
                    } else {
                        string = LocaleController.getString("PassportFrontSide", NUM);
                    }
                    this.frontLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("front");
                    stringBuilder2.append(documentHash);
                    stringBuilder = stringBuilder2.toString();
                } else if (i == 3) {
                    string = LocaleController.getString("PassportReverseSide", NUM);
                    this.reverseLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("reverse");
                    stringBuilder2.append(documentHash);
                    stringBuilder = stringBuilder2.toString();
                } else {
                    string = LocaleController.getString(str, NUM);
                    this.documentsLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("files");
                    stringBuilder2.append(documentHash);
                    stringBuilder = stringBuilder2.toString();
                }
            }
            String str2 = stringBuilder;
            if (str2 != null) {
                HashMap hashMap = this.documentsErrors;
                if (hashMap != null) {
                    charSequence = (String) hashMap.get(str2);
                    if (charSequence != null) {
                        secureDocumentCell.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
                        this.errorsValues.put(str2, "");
                        secureDocumentCell.setTextAndValueAndImage(string, charSequence, secureDocument);
                        secureDocumentCell.setOnClickListener(new -$$Lambda$PassportActivity$c_SivFsgu6h4BCXz5VeKlb47xZU(this, i));
                        secureDocumentCell.setOnLongClickListener(new -$$Lambda$PassportActivity$eMgihqVNJqkr7oPmUY-yq5VUzi4(this, i, secureDocument, secureDocumentCell, str2));
                    }
                }
            }
            charSequence = LocaleController.formatDateForBan((long) secureDocument.secureFile.date);
            secureDocumentCell.setTextAndValueAndImage(string, charSequence, secureDocument);
            secureDocumentCell.setOnClickListener(new -$$Lambda$PassportActivity$c_SivFsgu6h4BCXz5VeKlb47xZU(this, i));
            secureDocumentCell.setOnLongClickListener(new -$$Lambda$PassportActivity$eMgihqVNJqkr7oPmUY-yq5VUzi4(this, i, secureDocument, secureDocumentCell, str2));
        }
    }

    public /* synthetic */ void lambda$addDocumentView$56$PassportActivity(int i, View view) {
        this.uploadingFileType = i;
        if (i == 1) {
            this.currentPhotoViewerLayout = this.selfieLayout;
        } else if (i == 4) {
            this.currentPhotoViewerLayout = this.translationLayout;
        } else if (i == 2) {
            this.currentPhotoViewerLayout = this.frontLayout;
        } else if (i == 3) {
            this.currentPhotoViewerLayout = this.reverseLayout;
        } else {
            this.currentPhotoViewerLayout = this.documentsLayout;
        }
        SecureDocument secureDocument = (SecureDocument) view.getTag();
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        PhotoViewer instance;
        ArrayList arrayList;
        if (i == 0) {
            instance = PhotoViewer.getInstance();
            arrayList = this.documents;
            instance.openPhoto(arrayList, arrayList.indexOf(secureDocument), this.provider);
            return;
        }
        instance = PhotoViewer.getInstance();
        arrayList = this.translationDocuments;
        instance.openPhoto(arrayList, arrayList.indexOf(secureDocument), this.provider);
    }

    public /* synthetic */ boolean lambda$addDocumentView$58$PassportActivity(int i, SecureDocument secureDocument, SecureDocumentCell secureDocumentCell, String str, View view) {
        Builder builder = new Builder(getParentActivity());
        if (i == 1) {
            builder.setMessage(LocaleController.getString("PassportDeleteSelfie", NUM));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteScan", NUM));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PassportActivity$Aj-Wenld4quF9ly9Ap8983X-MQo(this, secureDocument, i, secureDocumentCell, str));
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$57$PassportActivity(SecureDocument secureDocument, int i, SecureDocumentCell secureDocumentCell, String str, DialogInterface dialogInterface, int i2) {
        this.documentsCells.remove(secureDocument);
        if (i == 1) {
            this.selfieDocument = null;
            this.selfieLayout.removeView(secureDocumentCell);
        } else if (i == 4) {
            this.translationDocuments.remove(secureDocument);
            this.translationLayout.removeView(secureDocumentCell);
        } else if (i == 2) {
            this.frontDocument = null;
            this.frontLayout.removeView(secureDocumentCell);
        } else if (i == 3) {
            this.reverseDocument = null;
            this.reverseLayout.removeView(secureDocumentCell);
        } else {
            this.documents.remove(secureDocument);
            this.documentsLayout.removeView(secureDocumentCell);
        }
        if (str != null) {
            HashMap hashMap = this.documentsErrors;
            if (hashMap != null) {
                hashMap.remove(str);
            }
            hashMap = this.errorsValues;
            if (hashMap != null) {
                hashMap.remove(str);
            }
        }
        updateUploadText(i);
        String str2 = secureDocument.path;
        if (str2 != null && this.uploadingDocuments.remove(str2) != null) {
            if (this.uploadingDocuments.isEmpty()) {
                this.doneItem.setEnabled(true);
                this.doneItem.setAlpha(1.0f);
            }
            FileLoader.getInstance(this.currentAccount).cancelUploadFile(secureDocument.path, false);
        }
    }

    private String getNameForType(SecureValueType secureValueType) {
        if (secureValueType instanceof TL_secureValueTypePersonalDetails) {
            return "personal_details";
        }
        if (secureValueType instanceof TL_secureValueTypePassport) {
            return "passport";
        }
        if (secureValueType instanceof TL_secureValueTypeInternalPassport) {
            return "internal_passport";
        }
        if (secureValueType instanceof TL_secureValueTypeDriverLicense) {
            return "driver_license";
        }
        if (secureValueType instanceof TL_secureValueTypeIdentityCard) {
            return "identity_card";
        }
        if (secureValueType instanceof TL_secureValueTypeUtilityBill) {
            return "utility_bill";
        }
        if (secureValueType instanceof TL_secureValueTypeAddress) {
            return "address";
        }
        if (secureValueType instanceof TL_secureValueTypeBankStatement) {
            return "bank_statement";
        }
        if (secureValueType instanceof TL_secureValueTypeRentalAgreement) {
            return "rental_agreement";
        }
        if (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) {
            return "temporary_registration";
        }
        if (secureValueType instanceof TL_secureValueTypePassportRegistration) {
            return "passport_registration";
        }
        if (secureValueType instanceof TL_secureValueTypeEmail) {
            return "email";
        }
        return secureValueType instanceof TL_secureValueTypePhone ? "phone" : "";
    }

    private TextDetailSecureCell getViewByType(TL_secureRequiredType tL_secureRequiredType) {
        TextDetailSecureCell textDetailSecureCell = (TextDetailSecureCell) this.typesViews.get(tL_secureRequiredType);
        if (textDetailSecureCell != null) {
            return textDetailSecureCell;
        }
        tL_secureRequiredType = (TL_secureRequiredType) this.documentsToTypesLink.get(tL_secureRequiredType);
        return tL_secureRequiredType != null ? (TextDetailSecureCell) this.typesViews.get(tL_secureRequiredType) : textDetailSecureCell;
    }

    private String getTextForType(SecureValueType secureValueType) {
        if (secureValueType instanceof TL_secureValueTypePassport) {
            return LocaleController.getString("ActionBotDocumentPassport", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeDriverLicense) {
            return LocaleController.getString("ActionBotDocumentDriverLicence", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeIdentityCard) {
            return LocaleController.getString("ActionBotDocumentIdentityCard", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeUtilityBill) {
            return LocaleController.getString("ActionBotDocumentUtilityBill", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeBankStatement) {
            return LocaleController.getString("ActionBotDocumentBankStatement", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeRentalAgreement) {
            return LocaleController.getString("ActionBotDocumentRentalAgreement", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeInternalPassport) {
            return LocaleController.getString("ActionBotDocumentInternalPassport", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypePassportRegistration) {
            return LocaleController.getString("ActionBotDocumentPassportRegistration", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) {
            return LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM);
        }
        if (secureValueType instanceof TL_secureValueTypePhone) {
            return LocaleController.getString("ActionBotDocumentPhone", NUM);
        }
        return secureValueType instanceof TL_secureValueTypeEmail ? LocaleController.getString("ActionBotDocumentEmail", NUM) : "";
    }

    /* JADX WARNING: Removed duplicated region for block: B:189:0x0371 A:{Catch:{ Exception -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0332 A:{Catch:{ Exception -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0371 A:{Catch:{ Exception -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0332 A:{Catch:{ Exception -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0371 A:{Catch:{ Exception -> 0x037f }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x025d A:{Catch:{ Throwable -> 0x027d }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0285 A:{Catch:{ Exception -> 0x03b3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x025d A:{Catch:{ Throwable -> 0x027d }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0285 A:{Catch:{ Exception -> 0x03b3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03bf  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0552  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x054e  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03b7  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03bf  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x03e4  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x054e  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0552  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03bf  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x03e4  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0552  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x054e  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03bf  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x03e4  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x054e  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0552  */
    private void setTypeValue(org.telegram.tgnet.TLRPC.TL_secureRequiredType r31, java.lang.String r32, java.lang.String r33, org.telegram.tgnet.TLRPC.TL_secureRequiredType r34, java.lang.String r35, boolean r36, int r37) {
        /*
        r30 = this;
        r7 = r30;
        r8 = r31;
        r9 = r32;
        r10 = r33;
        r11 = r34;
        r12 = r35;
        r13 = r37;
        r0 = r7.typesViews;
        r0 = r0.get(r8);
        r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0;
        r14 = 6;
        r15 = 8;
        r6 = 1;
        if (r0 != 0) goto L_0x0053;
    L_0x001c:
        r0 = r7.currentActivityType;
        if (r0 != r15) goto L_0x0052;
    L_0x0020:
        r4 = new java.util.ArrayList;
        r4.<init>();
        if (r11 == 0) goto L_0x002a;
    L_0x0027:
        r4.add(r11);
    L_0x002a:
        r0 = r7.linearLayout2;
        r1 = r0.getChildCount();
        r1 = r1 - r14;
        r0 = r0.getChildAt(r1);
        r1 = r0 instanceof org.telegram.ui.PassportActivity.TextDetailSecureCell;
        if (r1 == 0) goto L_0x003e;
    L_0x0039:
        r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0;
        r0.setNeedDivider(r6);
    L_0x003e:
        r2 = r30.getParentActivity();
        r5 = 1;
        r0 = 1;
        r1 = r30;
        r3 = r31;
        r14 = 1;
        r6 = r0;
        r0 = r1.addField(r2, r3, r4, r5, r6);
        r30.updateManageVisibility();
        goto L_0x0054;
    L_0x0052:
        return;
    L_0x0053:
        r14 = 1;
    L_0x0054:
        r1 = r0;
        r0 = r7.typesValues;
        r0 = r0.get(r8);
        r2 = r0;
        r2 = (java.util.HashMap) r2;
        if (r11 == 0) goto L_0x006a;
    L_0x0060:
        r0 = r7.typesValues;
        r0 = r0.get(r11);
        r0 = (java.util.HashMap) r0;
        r4 = r0;
        goto L_0x006b;
    L_0x006a:
        r4 = 0;
    L_0x006b:
        r5 = r7.getValueByType(r8, r14);
        r6 = r7.getValueByType(r11, r14);
        if (r10 == 0) goto L_0x00c6;
    L_0x0075:
        r0 = r7.languageMap;
        if (r0 != 0) goto L_0x00c6;
    L_0x0079:
        r0 = new java.util.HashMap;
        r0.<init>();
        r7.languageMap = r0;
        r0 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x00be }
        r3 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x00be }
        r19 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00be }
        r19 = r19.getResources();	 Catch:{ Exception -> 0x00be }
        r15 = r19.getAssets();	 Catch:{ Exception -> 0x00be }
        r14 = "countries.txt";
        r14 = r15.open(r14);	 Catch:{ Exception -> 0x00be }
        r3.<init>(r14);	 Catch:{ Exception -> 0x00be }
        r0.<init>(r3);	 Catch:{ Exception -> 0x00be }
    L_0x009a:
        r3 = r0.readLine();	 Catch:{ Exception -> 0x00be }
        if (r3 == 0) goto L_0x00b6;
    L_0x00a0:
        r14 = ";";
        r3 = r3.split(r14);	 Catch:{ Exception -> 0x00be }
        r14 = r7.languageMap;	 Catch:{ Exception -> 0x00be }
        r20 = r5;
        r15 = 1;
        r5 = r3[r15];	 Catch:{ Exception -> 0x00bc }
        r15 = 2;
        r3 = r3[r15];	 Catch:{ Exception -> 0x00bc }
        r14.put(r5, r3);	 Catch:{ Exception -> 0x00bc }
        r5 = r20;
        goto L_0x009a;
    L_0x00b6:
        r20 = r5;
        r0.close();	 Catch:{ Exception -> 0x00bc }
        goto L_0x00c4;
    L_0x00bc:
        r0 = move-exception;
        goto L_0x00c1;
    L_0x00be:
        r0 = move-exception;
        r20 = r5;
    L_0x00c1:
        org.telegram.messenger.FileLog.e(r0);
    L_0x00c4:
        r3 = 0;
        goto L_0x00cb;
    L_0x00c6:
        r20 = r5;
        r3 = 0;
        r7.languageMap = r3;
    L_0x00cb:
        r5 = NUM; // 0x7f0d0718 float:1.8745798E38 double:1.053130675E-314;
        r14 = "PassportDocuments";
        if (r9 == 0) goto L_0x010f;
    L_0x00d2:
        r0 = r8.type;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
        if (r2 == 0) goto L_0x00fb;
    L_0x00d8:
        r0 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "+";
        r2.append(r4);
        r2.append(r9);
        r2 = r2.toString();
        r0 = r0.format(r2);
        r24 = r1;
        r23 = r6;
    L_0x00f5:
        r25 = r14;
        r22 = 0;
        goto L_0x03bd;
    L_0x00fb:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
        if (r0 == 0) goto L_0x0105;
    L_0x00ff:
        r24 = r1;
        r23 = r6;
        r0 = r9;
        goto L_0x00f5;
    L_0x0105:
        r24 = r1;
        r23 = r6;
        r25 = r14;
        r22 = 0;
        goto L_0x03bc;
    L_0x010f:
        r0 = r7.currentActivityType;
        r9 = 8;
        if (r0 == r9) goto L_0x013f;
    L_0x0115:
        if (r11 == 0) goto L_0x013f;
    L_0x0117:
        r0 = android.text.TextUtils.isEmpty(r35);
        if (r0 == 0) goto L_0x011f;
    L_0x011d:
        if (r6 == 0) goto L_0x013f;
    L_0x011f:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r9 = 1;
        if (r13 <= r9) goto L_0x0131;
    L_0x0127:
        r9 = r11.type;
        r9 = r7.getTextForType(r9);
        r0.append(r9);
        goto L_0x0140;
    L_0x0131:
        r9 = android.text.TextUtils.isEmpty(r35);
        if (r9 == 0) goto L_0x0140;
    L_0x0137:
        r9 = org.telegram.messenger.LocaleController.getString(r14, r5);
        r0.append(r9);
        goto L_0x0140;
    L_0x013f:
        r0 = r3;
    L_0x0140:
        if (r10 != 0) goto L_0x0151;
    L_0x0142:
        if (r12 == 0) goto L_0x0145;
    L_0x0144:
        goto L_0x0151;
    L_0x0145:
        r32 = r0;
        r24 = r1;
        r23 = r6;
        r25 = r14;
        r22 = 0;
        goto L_0x0212;
    L_0x0151:
        if (r2 != 0) goto L_0x0154;
    L_0x0153:
        return;
    L_0x0154:
        r2.clear();
        r9 = r8.type;
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        r5 = "first_name_native";
        r21 = 3;
        r15 = "last_name";
        r23 = r6;
        r6 = "middle_name";
        r24 = r1;
        r1 = "country_code";
        r13 = "last_name_native";
        r25 = r14;
        r14 = "middle_name_native";
        if (r3 == 0) goto L_0x01d2;
    L_0x0171:
        r3 = r7.currentActivityType;
        if (r3 != 0) goto L_0x0177;
    L_0x0175:
        if (r36 == 0) goto L_0x017f;
    L_0x0177:
        r3 = r7.currentActivityType;
        r9 = 8;
        if (r3 != r9) goto L_0x01b1;
    L_0x017d:
        if (r11 != 0) goto L_0x01af;
    L_0x017f:
        r3 = 10;
        r3 = new java.lang.String[r3];
        r9 = "first_name";
        r22 = 0;
        r3[r22] = r9;
        r9 = 1;
        r3[r9] = r6;
        r9 = 2;
        r3[r9] = r15;
        r3[r21] = r5;
        r9 = 4;
        r3[r9] = r14;
        r9 = 5;
        r3[r9] = r13;
        r9 = "birth_date";
        r16 = 6;
        r3[r16] = r9;
        r9 = 7;
        r16 = "gender";
        r3[r9] = r16;
        r9 = 8;
        r3[r9] = r1;
        r16 = 9;
        r17 = "residence_country_code";
        r3[r16] = r17;
        r32 = r0;
        goto L_0x01b4;
    L_0x01af:
        r9 = 8;
    L_0x01b1:
        r32 = r0;
        r3 = 0;
    L_0x01b4:
        r0 = r7.currentActivityType;
        if (r0 == 0) goto L_0x01bf;
    L_0x01b8:
        if (r0 != r9) goto L_0x01bd;
    L_0x01ba:
        if (r11 == 0) goto L_0x01bd;
    L_0x01bc:
        goto L_0x01bf;
    L_0x01bd:
        r9 = 0;
        goto L_0x01cf;
    L_0x01bf:
        r9 = 2;
        r0 = new java.lang.String[r9];
        r9 = "document_no";
        r16 = 0;
        r0[r16] = r9;
        r9 = "expiry_date";
        r16 = 1;
        r0[r16] = r9;
        r9 = r0;
    L_0x01cf:
        r22 = 0;
        goto L_0x020d;
    L_0x01d2:
        r32 = r0;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r0 == 0) goto L_0x0209;
    L_0x01d8:
        r0 = r7.currentActivityType;
        if (r0 != 0) goto L_0x01e1;
    L_0x01dc:
        if (r36 == 0) goto L_0x01df;
    L_0x01de:
        goto L_0x01e1;
    L_0x01df:
        r3 = 6;
        goto L_0x01ea;
    L_0x01e1:
        r0 = r7.currentActivityType;
        r3 = 8;
        if (r0 != r3) goto L_0x0209;
    L_0x01e7:
        if (r11 != 0) goto L_0x0209;
    L_0x01e9:
        goto L_0x01df;
    L_0x01ea:
        r3 = new java.lang.String[r3];
        r0 = "street_line1";
        r22 = 0;
        r3[r22] = r0;
        r0 = "street_line2";
        r9 = 1;
        r3[r9] = r0;
        r0 = "post_code";
        r9 = 2;
        r3[r9] = r0;
        r0 = "city";
        r3[r21] = r0;
        r0 = 4;
        r9 = "state";
        r3[r0] = r9;
        r0 = 5;
        r3[r0] = r1;
        goto L_0x020c;
    L_0x0209:
        r22 = 0;
        r3 = 0;
    L_0x020c:
        r9 = 0;
    L_0x020d:
        if (r3 != 0) goto L_0x0216;
    L_0x020f:
        if (r9 == 0) goto L_0x0212;
    L_0x0211:
        goto L_0x0216;
    L_0x0212:
        r0 = r32;
        goto L_0x03b5;
    L_0x0216:
        r21 = r32;
        r16 = r3;
        r32 = r9;
        r0 = 0;
        r3 = 0;
        r9 = 2;
        r26 = 0;
    L_0x0221:
        if (r3 >= r9) goto L_0x03b3;
    L_0x0223:
        if (r3 != 0) goto L_0x0230;
    L_0x0225:
        if (r10 == 0) goto L_0x024a;
    L_0x0227:
        r0 = new org.json.JSONObject;	 Catch:{ Exception -> 0x03b3 }
        r0.<init>(r10);	 Catch:{ Exception -> 0x03b3 }
        r10 = r0;
        r9 = r16;
        goto L_0x024d;
    L_0x0230:
        if (r4 != 0) goto L_0x023f;
    L_0x0232:
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r9 = r26;
        r5 = 2;
        r26 = r2;
        goto L_0x03a0;
    L_0x023f:
        if (r12 == 0) goto L_0x024a;
    L_0x0241:
        r0 = new org.json.JSONObject;	 Catch:{ Exception -> 0x03b3 }
        r0.<init>(r12);	 Catch:{ Exception -> 0x03b3 }
        r9 = r32;
        r10 = r0;
        goto L_0x024d;
    L_0x024a:
        r10 = r0;
        r9 = r26;
    L_0x024d:
        if (r9 == 0) goto L_0x0396;
    L_0x024f:
        if (r10 != 0) goto L_0x0253;
    L_0x0251:
        goto L_0x0396;
    L_0x0253:
        r0 = r10.keys();	 Catch:{ Throwable -> 0x027d }
    L_0x0257:
        r26 = r0.hasNext();	 Catch:{ Throwable -> 0x027d }
        if (r26 == 0) goto L_0x0281;
    L_0x025d:
        r26 = r0.next();	 Catch:{ Throwable -> 0x027d }
        r27 = r0;
        r0 = r26;
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x027d }
        if (r3 != 0) goto L_0x0271;
    L_0x0269:
        r12 = r10.getString(r0);	 Catch:{ Throwable -> 0x027d }
        r2.put(r0, r12);	 Catch:{ Throwable -> 0x027d }
        goto L_0x0278;
    L_0x0271:
        r12 = r10.getString(r0);	 Catch:{ Throwable -> 0x027d }
        r4.put(r0, r12);	 Catch:{ Throwable -> 0x027d }
    L_0x0278:
        r12 = r35;
        r0 = r27;
        goto L_0x0257;
    L_0x027d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x03b3 }
    L_0x0281:
        r0 = 0;
    L_0x0282:
        r12 = r9.length;	 Catch:{ Exception -> 0x03b3 }
        if (r0 >= r12) goto L_0x0396;
    L_0x0285:
        r12 = r9[r0];	 Catch:{ Exception -> 0x03b3 }
        r12 = r10.has(r12);	 Catch:{ Exception -> 0x03b3 }
        if (r12 == 0) goto L_0x0381;
    L_0x028d:
        if (r21 != 0) goto L_0x0297;
    L_0x028f:
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03b3 }
        r12.<init>();	 Catch:{ Exception -> 0x03b3 }
        r26 = r2;
        goto L_0x029b;
    L_0x0297:
        r26 = r2;
        r12 = r21;
    L_0x029b:
        r2 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r2 = r10.getString(r2);	 Catch:{ Exception -> 0x037f }
        if (r2 == 0) goto L_0x0375;
    L_0x02a3:
        r21 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x037f }
        if (r21 != 0) goto L_0x0375;
    L_0x02a9:
        r27 = r4;
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r5.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 != 0) goto L_0x0377;
    L_0x02b3:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r14.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 != 0) goto L_0x0377;
    L_0x02bb:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r13.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 == 0) goto L_0x02c5;
    L_0x02c3:
        goto L_0x0377;
    L_0x02c5:
        r4 = r12.length();	 Catch:{ Exception -> 0x037f }
        if (r4 <= 0) goto L_0x02f7;
    L_0x02cb:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r15.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 != 0) goto L_0x02f2;
    L_0x02d3:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r13.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 != 0) goto L_0x02f2;
    L_0x02db:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r6.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 != 0) goto L_0x02f2;
    L_0x02e3:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r4 = r14.equals(r4);	 Catch:{ Exception -> 0x037f }
        if (r4 == 0) goto L_0x02ec;
    L_0x02eb:
        goto L_0x02f2;
    L_0x02ec:
        r4 = ", ";
        r12.append(r4);	 Catch:{ Exception -> 0x037f }
        goto L_0x02f7;
    L_0x02f2:
        r4 = " ";
        r12.append(r4);	 Catch:{ Exception -> 0x037f }
    L_0x02f7:
        r4 = r9[r0];	 Catch:{ Exception -> 0x037f }
        r21 = -1;
        r28 = r5;
        r5 = r4.hashCode();	 Catch:{ Exception -> 0x037f }
        r29 = r6;
        r6 = -NUM; // 0xfffffffvar_b058f float:-7.0724274E-34 double:NaN;
        if (r5 == r6) goto L_0x0325;
    L_0x0308:
        r6 = -NUM; // 0xffffffffb585f2c1 float:-9.979923E-7 double:NaN;
        if (r5 == r6) goto L_0x031b;
    L_0x030d:
        r6 = NUM; // 0x58475cf6 float:8.7680831E14 double:7.31746726E-315;
        if (r5 == r6) goto L_0x0313;
    L_0x0312:
        goto L_0x032f;
    L_0x0313:
        r4 = r4.equals(r1);	 Catch:{ Exception -> 0x037f }
        if (r4 == 0) goto L_0x032f;
    L_0x0319:
        r4 = 0;
        goto L_0x0330;
    L_0x031b:
        r5 = "gender";
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x037f }
        if (r4 == 0) goto L_0x032f;
    L_0x0323:
        r4 = 2;
        goto L_0x0330;
    L_0x0325:
        r5 = "residence_country_code";
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x037f }
        if (r4 == 0) goto L_0x032f;
    L_0x032d:
        r4 = 1;
        goto L_0x0330;
    L_0x032f:
        r4 = -1;
    L_0x0330:
        if (r4 == 0) goto L_0x0366;
    L_0x0332:
        r5 = 1;
        if (r4 == r5) goto L_0x0366;
    L_0x0335:
        r5 = 2;
        if (r4 == r5) goto L_0x033c;
    L_0x0338:
        r12.append(r2);	 Catch:{ Exception -> 0x037f }
        goto L_0x037c;
    L_0x033c:
        r4 = "male";
        r4 = r4.equals(r2);	 Catch:{ Exception -> 0x037f }
        if (r4 == 0) goto L_0x0351;
    L_0x0344:
        r2 = "PassportMale";
        r4 = NUM; // 0x7f0d075c float:1.8745936E38 double:1.0531307084E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r4);	 Catch:{ Exception -> 0x037f }
        r12.append(r2);	 Catch:{ Exception -> 0x037f }
        goto L_0x037c;
    L_0x0351:
        r4 = "female";
        r2 = r4.equals(r2);	 Catch:{ Exception -> 0x037f }
        if (r2 == 0) goto L_0x037c;
    L_0x0359:
        r2 = "PassportFemale";
        r4 = NUM; // 0x7f0d071f float:1.8745812E38 double:1.053130678E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r4);	 Catch:{ Exception -> 0x037f }
        r12.append(r2);	 Catch:{ Exception -> 0x037f }
        goto L_0x037c;
    L_0x0366:
        r5 = 2;
        r4 = r7.languageMap;	 Catch:{ Exception -> 0x037f }
        r2 = r4.get(r2);	 Catch:{ Exception -> 0x037f }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x037f }
        if (r2 == 0) goto L_0x037c;
    L_0x0371:
        r12.append(r2);	 Catch:{ Exception -> 0x037f }
        goto L_0x037c;
    L_0x0375:
        r27 = r4;
    L_0x0377:
        r28 = r5;
        r29 = r6;
        r5 = 2;
    L_0x037c:
        r21 = r12;
        goto L_0x038a;
    L_0x037f:
        r0 = r12;
        goto L_0x03b5;
    L_0x0381:
        r26 = r2;
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r5 = 2;
    L_0x038a:
        r0 = r0 + 1;
        r2 = r26;
        r4 = r27;
        r5 = r28;
        r6 = r29;
        goto L_0x0282;
    L_0x0396:
        r26 = r2;
        r27 = r4;
        r28 = r5;
        r29 = r6;
        r5 = 2;
        r0 = r10;
    L_0x03a0:
        r3 = r3 + 1;
        r10 = r33;
        r12 = r35;
        r2 = r26;
        r4 = r27;
        r5 = r28;
        r6 = r29;
        r26 = r9;
        r9 = 2;
        goto L_0x0221;
    L_0x03b3:
        r0 = r21;
    L_0x03b5:
        if (r0 == 0) goto L_0x03bc;
    L_0x03b7:
        r0 = r0.toString();
        goto L_0x03bd;
    L_0x03bc:
        r0 = 0;
    L_0x03bd:
        if (r36 != 0) goto L_0x03cf;
    L_0x03bf:
        r1 = r7.errorsMap;
        r2 = r8.type;
        r2 = r7.getNameForType(r2);
        r1 = r1.get(r2);
        r3 = r1;
        r3 = (java.util.HashMap) r3;
        goto L_0x03d0;
    L_0x03cf:
        r3 = 0;
    L_0x03d0:
        if (r11 == 0) goto L_0x03e1;
    L_0x03d2:
        r1 = r7.errorsMap;
        r2 = r11.type;
        r2 = r7.getNameForType(r2);
        r1 = r1.get(r2);
        r1 = (java.util.HashMap) r1;
        goto L_0x03e2;
    L_0x03e1:
        r1 = 0;
    L_0x03e2:
        if (r3 == 0) goto L_0x03ea;
    L_0x03e4:
        r2 = r3.size();
        if (r2 > 0) goto L_0x03f2;
    L_0x03ea:
        if (r1 == 0) goto L_0x0422;
    L_0x03ec:
        r1 = r1.size();
        if (r1 <= 0) goto L_0x0422;
    L_0x03f2:
        if (r36 != 0) goto L_0x0406;
    L_0x03f4:
        r0 = r7.mainErrorsMap;
        r1 = r8.type;
        r1 = r7.getNameForType(r1);
        r0 = r0.get(r1);
        r3 = r0;
        r3 = (java.lang.String) r3;
        r18 = r3;
        goto L_0x0408;
    L_0x0406:
        r18 = 0;
    L_0x0408:
        if (r18 != 0) goto L_0x0419;
    L_0x040a:
        r0 = r7.mainErrorsMap;
        r1 = r11.type;
        r1 = r7.getNameForType(r1);
        r0 = r0.get(r1);
        r0 = (java.lang.String) r0;
        goto L_0x041b;
    L_0x0419:
        r0 = r18;
    L_0x041b:
        r1 = r24;
        r15 = 1;
        r19 = 1;
        goto L_0x0545;
    L_0x0422:
        r1 = r8.type;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        if (r2 == 0) goto L_0x049a;
    L_0x0428:
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x0497;
    L_0x042e:
        if (r11 != 0) goto L_0x043e;
    L_0x0430:
        r0 = NUM; // 0x7f0d076e float:1.8745972E38 double:1.0531307173E-314;
        r1 = "PassportPersonalDetailsInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
    L_0x0439:
        r1 = r24;
        r15 = 1;
        goto L_0x0543;
    L_0x043e:
        r1 = r7.currentActivityType;
        r2 = 8;
        if (r1 != r2) goto L_0x044e;
    L_0x0444:
        r2 = r25;
        r1 = NUM; // 0x7f0d0718 float:1.8745798E38 double:1.053130675E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r2, r1);
        goto L_0x0439;
    L_0x044e:
        r3 = r37;
        r1 = 1;
        if (r3 != r1) goto L_0x048d;
    L_0x0453:
        r1 = r11.type;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePassport;
        if (r2 == 0) goto L_0x0463;
    L_0x0459:
        r0 = NUM; // 0x7f0d0728 float:1.874583E38 double:1.0531306827E-314;
        r1 = "PassportIdentityPassport";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0439;
    L_0x0463:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeInternalPassport;
        if (r2 == 0) goto L_0x0471;
    L_0x0467:
        r0 = NUM; // 0x7f0d0727 float:1.8745828E38 double:1.053130682E-314;
        r1 = "PassportIdentityInternalPassport";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0439;
    L_0x0471:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense;
        if (r2 == 0) goto L_0x047f;
    L_0x0475:
        r0 = NUM; // 0x7f0d0725 float:1.8745824E38 double:1.053130681E-314;
        r1 = "PassportIdentityDriverLicence";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0439;
    L_0x047f:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeIdentityCard;
        if (r1 == 0) goto L_0x0497;
    L_0x0483:
        r0 = NUM; // 0x7f0d0726 float:1.8745826E38 double:1.0531306817E-314;
        r1 = "PassportIdentityID";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0439;
    L_0x048d:
        r0 = NUM; // 0x7f0d0724 float:1.8745822E38 double:1.0531306807E-314;
        r1 = "PassportIdentityDocumentInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0439;
    L_0x0497:
        r15 = 1;
        goto L_0x0541;
    L_0x049a:
        r3 = r37;
        r2 = r25;
        r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r4 == 0) goto L_0x0519;
    L_0x04a2:
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x0497;
    L_0x04a8:
        if (r11 != 0) goto L_0x04b4;
    L_0x04aa:
        r0 = NUM; // 0x7f0d0700 float:1.874575E38 double:1.053130663E-314;
        r1 = "PassportAddressNoUploadInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0439;
    L_0x04b4:
        r1 = r7.currentActivityType;
        r4 = 8;
        if (r1 != r4) goto L_0x04c3;
    L_0x04ba:
        r1 = NUM; // 0x7f0d0718 float:1.8745798E38 double:1.053130675E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r2, r1);
        goto L_0x0439;
    L_0x04c3:
        r15 = 1;
        if (r3 != r15) goto L_0x050f;
    L_0x04c6:
        r1 = r11.type;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeRentalAgreement;
        if (r2 == 0) goto L_0x04d7;
    L_0x04cc:
        r0 = NUM; // 0x7f0d06e6 float:1.8745697E38 double:1.05313065E-314;
        r1 = "PassportAddAgreementInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x04d7:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeUtilityBill;
        if (r2 == 0) goto L_0x04e5;
    L_0x04db:
        r0 = NUM; // 0x7f0d06ea float:1.8745705E38 double:1.053130652E-314;
        r1 = "PassportAddBillInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x04e5:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePassportRegistration;
        if (r2 == 0) goto L_0x04f3;
    L_0x04e9:
        r0 = NUM; // 0x7f0d06f4 float:1.8745725E38 double:1.053130657E-314;
        r1 = "PassportAddPassportRegistrationInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x04f3:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeTemporaryRegistration;
        if (r2 == 0) goto L_0x0501;
    L_0x04f7:
        r0 = NUM; // 0x7f0d06f6 float:1.874573E38 double:1.053130658E-314;
        r1 = "PassportAddTemporaryRegistrationInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x0501:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeBankStatement;
        if (r1 == 0) goto L_0x0541;
    L_0x0505:
        r0 = NUM; // 0x7f0d06e8 float:1.87457E38 double:1.053130651E-314;
        r1 = "PassportAddBankInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x050f:
        r0 = NUM; // 0x7f0d06ff float:1.8745747E38 double:1.0531306624E-314;
        r1 = "PassportAddressInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x0519:
        r15 = 1;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
        if (r2 == 0) goto L_0x052e;
    L_0x051e:
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x0541;
    L_0x0524:
        r0 = NUM; // 0x7f0d0771 float:1.8745979E38 double:1.0531307187E-314;
        r1 = "PassportPhoneInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        goto L_0x0541;
    L_0x052e:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
        if (r1 == 0) goto L_0x0541;
    L_0x0532:
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x0541;
    L_0x0538:
        r0 = NUM; // 0x7f0d071b float:1.8745804E38 double:1.0531306762E-314;
        r1 = "PassportEmailInfo";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
    L_0x0541:
        r1 = r24;
    L_0x0543:
        r19 = 0;
    L_0x0545:
        r1.setValue(r0);
        r0 = r1.valueTextView;
        if (r19 == 0) goto L_0x0552;
    L_0x054e:
        r2 = "windowBackgroundWhiteRedText3";
        goto L_0x0555;
    L_0x0552:
        r2 = "windowBackgroundWhiteGrayText2";
    L_0x0555:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setTextColor(r2);
        if (r19 != 0) goto L_0x0571;
    L_0x055e:
        r0 = r7.currentActivityType;
        r2 = 8;
        if (r0 == r2) goto L_0x0571;
    L_0x0564:
        if (r36 == 0) goto L_0x0568;
    L_0x0566:
        if (r11 != 0) goto L_0x056c;
    L_0x0568:
        if (r36 != 0) goto L_0x0571;
    L_0x056a:
        if (r20 == 0) goto L_0x0571;
    L_0x056c:
        if (r11 == 0) goto L_0x0572;
    L_0x056e:
        if (r23 == 0) goto L_0x0571;
    L_0x0570:
        goto L_0x0572;
    L_0x0571:
        r15 = 0;
    L_0x0572:
        r1.setChecked(r15);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setTypeValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, boolean, int):void");
    }

    private void checkNativeFields(boolean z) {
        if (this.inputExtraFields != null) {
            String str = (String) this.languageMap.get(this.currentResidence);
            String str2 = (String) SharedConfig.getCountryLangs().get(this.currentResidence);
            String str3 = "windowBackgroundGrayShadow";
            int i = 0;
            if (this.currentType.native_names && !TextUtils.isEmpty(this.currentResidence) && !"EN".equals(str2)) {
                int i2;
                if (this.nativeInfoCell.getVisibility() != 0) {
                    EditTextBoldCursor[] editTextBoldCursorArr;
                    this.nativeInfoCell.setVisibility(0);
                    this.headerCell.setVisibility(0);
                    this.extraBackgroundView2.setVisibility(0);
                    i2 = 0;
                    while (true) {
                        editTextBoldCursorArr = this.inputExtraFields;
                        if (i2 >= editTextBoldCursorArr.length) {
                            break;
                        }
                        ((View) editTextBoldCursorArr[i2].getParent()).setVisibility(0);
                        i2++;
                    }
                    if (editTextBoldCursorArr[0].length() == 0 && this.inputExtraFields[1].length() == 0 && this.inputExtraFields[2].length() == 0) {
                        i2 = 0;
                        while (true) {
                            boolean[] zArr = this.nonLatinNames;
                            if (i2 >= zArr.length) {
                                break;
                            } else if (zArr[i2]) {
                                this.inputExtraFields[0].setText(this.inputFields[0].getText());
                                this.inputExtraFields[1].setText(this.inputFields[1].getText());
                                this.inputExtraFields[2].setText(this.inputFields[2].getText());
                                break;
                            } else {
                                i2++;
                            }
                        }
                    }
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, str3));
                }
                this.nativeInfoCell.setText(LocaleController.formatString("PassportNativeInfo", NUM, str));
                if (str2 != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("PassportLanguage_");
                    stringBuilder.append(str2);
                    str2 = LocaleController.getServerString(stringBuilder.toString());
                } else {
                    str2 = null;
                }
                if (str2 != null) {
                    this.headerCell.setText(LocaleController.formatString("PassportNativeHeaderLang", NUM, str2));
                } else {
                    this.headerCell.setText(LocaleController.getString("PassportNativeHeader", NUM));
                }
                for (i2 = 0; i2 < 3; i2++) {
                    if (i2 != 0) {
                        if (i2 != 1) {
                            if (i2 == 2) {
                                if (str2 != null) {
                                    this.inputExtraFields[i2].setHintText(LocaleController.getString("PassportSurname", NUM));
                                } else {
                                    this.inputExtraFields[i2].setHintText(LocaleController.formatString("PassportSurnameCountry", NUM, str));
                                }
                            }
                        } else if (str2 != null) {
                            this.inputExtraFields[i2].setHintText(LocaleController.getString("PassportMidname", NUM));
                        } else {
                            this.inputExtraFields[i2].setHintText(LocaleController.formatString("PassportMidnameCountry", NUM, str));
                        }
                    } else if (str2 != null) {
                        this.inputExtraFields[i2].setHintText(LocaleController.getString("PassportName", NUM));
                    } else {
                        this.inputExtraFields[i2].setHintText(LocaleController.formatString("PassportNameCountry", NUM, str));
                    }
                }
                if (z) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$HspRKy4cKNBUybT-ED_XvHWEg3g(this));
                }
            } else if (this.nativeInfoCell.getVisibility() != 8) {
                this.nativeInfoCell.setVisibility(8);
                this.headerCell.setVisibility(8);
                this.extraBackgroundView2.setVisibility(8);
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    if (i >= editTextBoldCursorArr2.length) {
                        break;
                    }
                    ((View) editTextBoldCursorArr2[i].getParent()).setVisibility(8);
                    i++;
                }
                if (((this.currentBotId == 0 && this.currentDocumentsType != null) || this.currentTypeValue == null || this.documentOnly) && this.currentDocumentsTypeValue == null) {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, str3));
                } else {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, str3));
                }
            }
        }
    }

    public /* synthetic */ void lambda$checkNativeFields$59$PassportActivity() {
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (editTextBoldCursorArr != null) {
            scrollToField(editTextBoldCursorArr[0]);
        }
    }

    private String getErrorsString(HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < 2) {
            HashMap hashMap3 = i == 0 ? hashMap : hashMap2;
            if (hashMap3 != null) {
                for (Entry value : hashMap3.entrySet()) {
                    String str = (String) value.getValue();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                        str = str.toLowerCase();
                    }
                    if (str.endsWith(".")) {
                        str = str.substring(0, str.length() - 1);
                    }
                    stringBuilder.append(str);
                }
            }
            i++;
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.append('.');
        }
        return stringBuilder.toString();
    }

    private TL_secureValue getValueByType(TL_secureRequiredType tL_secureRequiredType, boolean z) {
        if (tL_secureRequiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TL_secureValue tL_secureValue = (TL_secureValue) this.currentForm.values.get(i2);
            if (tL_secureRequiredType.type.getClass() == tL_secureValue.type.getClass()) {
                if (z) {
                    if (tL_secureRequiredType.selfie_required && !(tL_secureValue.selfie instanceof TL_secureFile)) {
                        return null;
                    }
                    if (tL_secureRequiredType.translation_required && tL_secureValue.translation.isEmpty()) {
                        return null;
                    }
                    if (isAddressDocument(tL_secureRequiredType.type) && tL_secureValue.files.isEmpty()) {
                        return null;
                    }
                    if (isPersonalDocument(tL_secureRequiredType.type) && !(tL_secureValue.front_side instanceof TL_secureFile)) {
                        return null;
                    }
                    SecureValueType secureValueType = tL_secureRequiredType.type;
                    if (((secureValueType instanceof TL_secureValueTypeDriverLicense) || (secureValueType instanceof TL_secureValueTypeIdentityCard)) && !(tL_secureValue.reverse_side instanceof TL_secureFile)) {
                        return null;
                    }
                    secureValueType = tL_secureRequiredType.type;
                    if ((secureValueType instanceof TL_secureValueTypePersonalDetails) || (secureValueType instanceof TL_secureValueTypeAddress)) {
                        String[] strArr;
                        String str = "country_code";
                        if (tL_secureRequiredType.type instanceof TL_secureValueTypePersonalDetails) {
                            String str2 = "residence_country_code";
                            String str3 = "gender";
                            String str4 = "birth_date";
                            strArr = tL_secureRequiredType.native_names ? new String[]{"first_name_native", "last_name_native", str4, str3, str, str2} : new String[]{"first_name", "last_name", str4, str3, str, str2};
                        } else {
                            strArr = new String[]{"street_line1", "street_line2", "post_code", "city", "state", str};
                        }
                        try {
                            JSONObject jSONObject = new JSONObject(decryptData(tL_secureValue.data.data, decryptValueSecret(tL_secureValue.data.secret, tL_secureValue.data.data_hash), tL_secureValue.data.data_hash));
                            while (i < strArr.length) {
                                if (jSONObject.has(strArr[i]) && !TextUtils.isEmpty(jSONObject.getString(strArr[i]))) {
                                    i++;
                                }
                                return null;
                            }
                        } catch (Throwable unused) {
                        }
                    }
                }
                return tL_secureValue;
            }
        }
        return null;
    }

    private void openTypeActivity(TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, ArrayList<TL_secureRequiredType> arrayList, boolean z) {
        TL_secureRequiredType tL_secureRequiredType3 = tL_secureRequiredType;
        TL_secureRequiredType tL_secureRequiredType4 = tL_secureRequiredType2;
        final boolean z2 = z;
        final int size = arrayList != null ? arrayList.size() : 0;
        final SecureValueType secureValueType = tL_secureRequiredType3.type;
        SecureValueType secureValueType2 = tL_secureRequiredType4 != null ? tL_secureRequiredType4.type : null;
        int i = secureValueType instanceof TL_secureValueTypePersonalDetails ? 1 : secureValueType instanceof TL_secureValueTypeAddress ? 2 : secureValueType instanceof TL_secureValueTypePhone ? 3 : secureValueType instanceof TL_secureValueTypeEmail ? 4 : -1;
        if (i != -1) {
            TL_account_password tL_account_password;
            HashMap hashMap;
            HashMap hashMap2 = !z2 ? (HashMap) this.errorsMap.get(getNameForType(secureValueType)) : null;
            HashMap hashMap3 = (HashMap) this.errorsMap.get(getNameForType(secureValueType2));
            TL_secureValue valueByType = getValueByType(tL_secureRequiredType3, false);
            TL_secureValue valueByType2 = getValueByType(tL_secureRequiredType4, false);
            TL_account_authorizationForm tL_account_authorizationForm = this.currentForm;
            TL_account_password tL_account_password2 = this.currentPassword;
            HashMap hashMap4 = (HashMap) this.typesValues.get(tL_secureRequiredType3);
            if (tL_secureRequiredType4 != null) {
                tL_account_password = tL_account_password2;
                hashMap = (HashMap) this.typesValues.get(tL_secureRequiredType4);
            } else {
                tL_account_password = tL_account_password2;
                hashMap = null;
            }
            PassportActivity passportActivity = r1;
            HashMap hashMap5 = hashMap3;
            TL_secureValue tL_secureValue = valueByType2;
            HashMap hashMap6 = hashMap5;
            hashMap5 = hashMap2;
            int i2 = i;
            PassportActivity passportActivity2 = new PassportActivity(i, tL_account_authorizationForm, tL_account_password, tL_secureRequiredType, valueByType, tL_secureRequiredType2, tL_secureValue, hashMap4, hashMap);
            passportActivity.delegate = new PassportActivityDelegate() {
                private InputSecureFile getInputSecureFile(SecureDocument secureDocument) {
                    if (secureDocument.inputFile != null) {
                        TL_inputSecureFileUploaded tL_inputSecureFileUploaded = new TL_inputSecureFileUploaded();
                        TL_inputFile tL_inputFile = secureDocument.inputFile;
                        tL_inputSecureFileUploaded.id = tL_inputFile.id;
                        tL_inputSecureFileUploaded.parts = tL_inputFile.parts;
                        tL_inputSecureFileUploaded.md5_checksum = tL_inputFile.md5_checksum;
                        tL_inputSecureFileUploaded.file_hash = secureDocument.fileHash;
                        tL_inputSecureFileUploaded.secret = secureDocument.fileSecret;
                        return tL_inputSecureFileUploaded;
                    }
                    TL_inputSecureFile tL_inputSecureFile = new TL_inputSecureFile();
                    TL_secureFile tL_secureFile = secureDocument.secureFile;
                    tL_inputSecureFile.id = tL_secureFile.id;
                    tL_inputSecureFile.access_hash = tL_secureFile.access_hash;
                    return tL_inputSecureFile;
                }

                private void renameFile(SecureDocument secureDocument, TL_secureFile tL_secureFile) {
                    File pathToAttach = FileLoader.getPathToAttach(secureDocument);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(secureDocument.secureFile.dc_id);
                    String str = "_";
                    stringBuilder.append(str);
                    stringBuilder.append(secureDocument.secureFile.id);
                    String stringBuilder2 = stringBuilder.toString();
                    File pathToAttach2 = FileLoader.getPathToAttach(tL_secureFile);
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(tL_secureFile.dc_id);
                    stringBuilder3.append(str);
                    stringBuilder3.append(tL_secureFile.id);
                    String stringBuilder4 = stringBuilder3.toString();
                    pathToAttach.renameTo(pathToAttach2);
                    ImageLoader.getInstance().replaceImageInCache(stringBuilder2, stringBuilder4, null, false);
                }

                public void saveValue(TL_secureRequiredType tL_secureRequiredType, String str, String str2, TL_secureRequiredType tL_secureRequiredType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, ArrayList<SecureDocument> arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable) {
                    TL_inputSecureValue tL_inputSecureValue;
                    TL_inputSecureValue tL_inputSecureValue2;
                    TL_secureRequiredType tL_secureRequiredType3 = tL_secureRequiredType;
                    String str4 = str;
                    TL_secureRequiredType tL_secureRequiredType4 = tL_secureRequiredType2;
                    ArrayList<SecureDocument> arrayList3 = arrayList;
                    SecureDocument secureDocument4 = secureDocument;
                    ArrayList<SecureDocument> arrayList4 = arrayList2;
                    SecureDocument secureDocument5 = secureDocument2;
                    SecureDocument secureDocument6 = secureDocument3;
                    ErrorRunnable errorRunnable2 = errorRunnable;
                    if (!TextUtils.isEmpty(str2)) {
                        tL_inputSecureValue = new TL_inputSecureValue();
                        tL_inputSecureValue.type = tL_secureRequiredType3.type;
                        tL_inputSecureValue.flags |= 1;
                        EncryptionResult access$6900 = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(str2));
                        tL_inputSecureValue.data = new TL_secureData();
                        TL_secureData tL_secureData = tL_inputSecureValue.data;
                        tL_secureData.data = access$6900.encryptedData;
                        tL_secureData.data_hash = access$6900.fileHash;
                        tL_secureData.secret = access$6900.fileSecret;
                        tL_inputSecureValue2 = tL_inputSecureValue;
                    } else if (TextUtils.isEmpty(str)) {
                        tL_inputSecureValue2 = null;
                    } else {
                        SecurePlainData tL_securePlainEmail;
                        SecureValueType secureValueType = secureValueType;
                        if (secureValueType instanceof TL_secureValueTypeEmail) {
                            tL_securePlainEmail = new TL_securePlainEmail();
                            tL_securePlainEmail.email = str4;
                        } else if (secureValueType instanceof TL_secureValueTypePhone) {
                            tL_securePlainEmail = new TL_securePlainPhone();
                            tL_securePlainEmail.phone = str4;
                        } else {
                            return;
                        }
                        tL_inputSecureValue2 = new TL_inputSecureValue();
                        tL_inputSecureValue2.type = tL_secureRequiredType3.type;
                        tL_inputSecureValue2.flags |= 32;
                        tL_inputSecureValue2.plain_data = tL_securePlainEmail;
                    }
                    if (z2 || tL_inputSecureValue2 != null) {
                        TL_inputSecureValue tL_inputSecureValue3;
                        final TLObject tLObject;
                        TLObject tL_account_saveSecureValue;
                        AnonymousClass1 anonymousClass1;
                        AnonymousClass1 anonymousClass12;
                        final String str5;
                        ConnectionsManager instance;
                        final String str6;
                        TLObject tLObject2;
                        final Runnable runnable2;
                        if (tL_secureRequiredType4 != null) {
                            tL_inputSecureValue = new TL_inputSecureValue();
                            tL_inputSecureValue.type = tL_secureRequiredType4.type;
                            if (!TextUtils.isEmpty(str3)) {
                                tL_inputSecureValue.flags |= 1;
                                EncryptionResult access$69002 = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(str3));
                                tL_inputSecureValue.data = new TL_secureData();
                                TL_secureData tL_secureData2 = tL_inputSecureValue.data;
                                tL_secureData2.data = access$69002.encryptedData;
                                tL_secureData2.data_hash = access$69002.fileHash;
                                tL_secureData2.secret = access$69002.fileSecret;
                            }
                            if (secureDocument5 != null) {
                                tL_inputSecureValue.front_side = getInputSecureFile(secureDocument5);
                                tL_inputSecureValue.flags |= 2;
                            }
                            if (secureDocument6 != null) {
                                tL_inputSecureValue.reverse_side = getInputSecureFile(secureDocument6);
                                tL_inputSecureValue.flags |= 4;
                            }
                            if (secureDocument4 != null) {
                                tL_inputSecureValue.selfie = getInputSecureFile(secureDocument4);
                                tL_inputSecureValue.flags |= 8;
                            }
                            if (!(arrayList4 == null || arrayList2.isEmpty())) {
                                tL_inputSecureValue.flags |= 64;
                                int size = arrayList2.size();
                                for (int i = 0; i < size; i++) {
                                    tL_inputSecureValue.translation.add(getInputSecureFile((SecureDocument) arrayList4.get(i)));
                                }
                            }
                            if (!(arrayList3 == null || arrayList.isEmpty())) {
                                tL_inputSecureValue.flags |= 16;
                                int size2 = arrayList.size();
                                for (int i2 = 0; i2 < size2; i2++) {
                                    tL_inputSecureValue.files.add(getInputSecureFile((SecureDocument) arrayList3.get(i2)));
                                }
                            }
                            if (!z2) {
                                tL_inputSecureValue3 = tL_inputSecureValue;
                                tL_inputSecureValue = tL_inputSecureValue2;
                                tLObject = tL_account_saveSecureValue;
                                tL_account_saveSecureValue = new TL_account_saveSecureValue();
                                tL_account_saveSecureValue.value = tL_inputSecureValue;
                                tL_account_saveSecureValue.secure_secret_id = PassportActivity.this.secureSecretId;
                                errorRunnable2 = errorRunnable;
                                str4 = str;
                                tL_secureRequiredType4 = tL_secureRequiredType2;
                                tL_secureRequiredType3 = tL_secureRequiredType;
                                arrayList3 = arrayList;
                                secureDocument4 = secureDocument;
                                secureDocument5 = secureDocument2;
                                secureDocument6 = secureDocument3;
                                arrayList4 = arrayList2;
                                anonymousClass1 = anonymousClass12;
                                str5 = str2;
                                instance = ConnectionsManager.getInstance(PassportActivity.this.currentAccount);
                                str6 = str3;
                                tLObject2 = tL_account_saveSecureValue;
                                runnable2 = runnable;
                                anonymousClass12 = new RequestDelegate(this) {
                                    final /* synthetic */ AnonymousClass19 this$1;

                                    private void onResult(TL_error tL_error, TL_secureValue tL_secureValue, TL_secureValue tL_secureValue2) {
                                        TL_error tL_error2 = tL_error;
                                        TL_secureValue tL_secureValue3 = tL_secureValue;
                                        TL_secureValue tL_secureValue4 = tL_secureValue2;
                                        ErrorRunnable errorRunnable = errorRunnable2;
                                        String str = str4;
                                        TL_account_saveSecureValue tL_account_saveSecureValue = tLObject;
                                        AnonymousClass19 anonymousClass19 = this.this$1;
                                        AnonymousClass19 anonymousClass192 = anonymousClass19;
                                        AnonymousClass19 anonymousClass193 = anonymousClass192;
                                        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$19$1$0kRj7ir3NwvnbVVMIq70mwI_7cA(this, tL_error, errorRunnable, str, tL_account_saveSecureValue, z2, tL_secureRequiredType4, tL_secureRequiredType3, tL_secureValue3, tL_secureValue4, arrayList3, secureDocument4, secureDocument5, secureDocument6, arrayList4, str5, str6, size, runnable2));
                                    }

                                    public /* synthetic */ void lambda$onResult$0$PassportActivity$19$1(TL_error tL_error, ErrorRunnable errorRunnable, String str, TL_account_saveSecureValue tL_account_saveSecureValue, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, TL_secureValue tL_secureValue, TL_secureValue tL_secureValue2, ArrayList arrayList, SecureDocument secureDocument, SecureDocument secureDocument2, SecureDocument secureDocument3, ArrayList arrayList2, String str2, String str3, int i, Runnable runnable) {
                                        TL_error tL_error2 = tL_error;
                                        ErrorRunnable errorRunnable2 = errorRunnable;
                                        String str4 = str;
                                        TL_secureRequiredType tL_secureRequiredType3 = tL_secureRequiredType;
                                        TL_secureRequiredType tL_secureRequiredType4 = tL_secureRequiredType2;
                                        TL_secureValue tL_secureValue3 = tL_secureValue;
                                        TL_secureValue tL_secureValue4 = tL_secureValue2;
                                        ArrayList arrayList3 = arrayList;
                                        SecureDocument secureDocument4 = secureDocument;
                                        SecureDocument secureDocument5 = secureDocument2;
                                        SecureDocument secureDocument6 = secureDocument3;
                                        ArrayList arrayList4 = arrayList2;
                                        if (tL_error2 != null) {
                                            if (errorRunnable2 != null) {
                                                errorRunnable2.onError(tL_error2.text, str4);
                                            }
                                            AlertsCreator.processError(PassportActivity.this.currentAccount, tL_error2, PassportActivity.this, tL_account_saveSecureValue, str4);
                                            return;
                                        }
                                        int size;
                                        int i2;
                                        SecureFile secureFile;
                                        TL_secureFile tL_secureFile;
                                        if (!z) {
                                            PassportActivity.this.removeValue(tL_secureRequiredType4);
                                            PassportActivity.this.removeValue(tL_secureRequiredType3);
                                        } else if (tL_secureRequiredType3 != null) {
                                            PassportActivity.this.removeValue(tL_secureRequiredType3);
                                        } else {
                                            PassportActivity.this.removeValue(tL_secureRequiredType4);
                                        }
                                        if (tL_secureValue3 != null) {
                                            PassportActivity.this.currentForm.values.add(tL_secureValue3);
                                        }
                                        if (tL_secureValue4 != null) {
                                            PassportActivity.this.currentForm.values.add(tL_secureValue4);
                                        }
                                        if (!(arrayList3 == null || arrayList.isEmpty())) {
                                            size = arrayList.size();
                                            i2 = 0;
                                            while (i2 < size) {
                                                int i3;
                                                SecureDocument secureDocument7 = (SecureDocument) arrayList3.get(i2);
                                                if (secureDocument7.inputFile != null) {
                                                    int size2 = tL_secureValue3.files.size();
                                                    int i4 = 0;
                                                    while (i4 < size2) {
                                                        SecureFile secureFile2 = (SecureFile) tL_secureValue3.files.get(i4);
                                                        i3 = size;
                                                        if (secureFile2 instanceof TL_secureFile) {
                                                            TL_secureFile tL_secureFile2 = (TL_secureFile) secureFile2;
                                                            if (Utilities.arraysEquals(secureDocument7.fileSecret, 0, tL_secureFile2.secret, 0)) {
                                                                this.this$1.renameFile(secureDocument7, tL_secureFile2);
                                                                break;
                                                            }
                                                        }
                                                        i4++;
                                                        size = i3;
                                                        str4 = str;
                                                        tL_secureRequiredType4 = tL_secureRequiredType2;
                                                    }
                                                }
                                                i3 = size;
                                                i2++;
                                                size = i3;
                                                str4 = str;
                                                tL_secureRequiredType4 = tL_secureRequiredType2;
                                            }
                                        }
                                        if (!(secureDocument4 == null || secureDocument4.inputFile == null)) {
                                            secureFile = tL_secureValue3.selfie;
                                            if (secureFile instanceof TL_secureFile) {
                                                tL_secureFile = (TL_secureFile) secureFile;
                                                if (Utilities.arraysEquals(secureDocument4.fileSecret, 0, tL_secureFile.secret, 0)) {
                                                    this.this$1.renameFile(secureDocument4, tL_secureFile);
                                                }
                                            }
                                        }
                                        if (!(secureDocument5 == null || secureDocument5.inputFile == null)) {
                                            secureFile = tL_secureValue3.front_side;
                                            if (secureFile instanceof TL_secureFile) {
                                                tL_secureFile = (TL_secureFile) secureFile;
                                                if (Utilities.arraysEquals(secureDocument5.fileSecret, 0, tL_secureFile.secret, 0)) {
                                                    this.this$1.renameFile(secureDocument5, tL_secureFile);
                                                }
                                            }
                                        }
                                        if (!(secureDocument6 == null || secureDocument6.inputFile == null)) {
                                            secureFile = tL_secureValue3.reverse_side;
                                            if (secureFile instanceof TL_secureFile) {
                                                tL_secureFile = (TL_secureFile) secureFile;
                                                if (Utilities.arraysEquals(secureDocument6.fileSecret, 0, tL_secureFile.secret, 0)) {
                                                    this.this$1.renameFile(secureDocument6, tL_secureFile);
                                                }
                                            }
                                        }
                                        if (!(arrayList4 == null || arrayList2.isEmpty())) {
                                            size = arrayList2.size();
                                            for (i2 = 0; i2 < size; i2++) {
                                                SecureDocument secureDocument8 = (SecureDocument) arrayList4.get(i2);
                                                if (secureDocument8.inputFile != null) {
                                                    int size3 = tL_secureValue3.translation.size();
                                                    for (int i5 = 0; i5 < size3; i5++) {
                                                        SecureFile secureFile3 = (SecureFile) tL_secureValue3.translation.get(i5);
                                                        if (secureFile3 instanceof TL_secureFile) {
                                                            TL_secureFile tL_secureFile3 = (TL_secureFile) secureFile3;
                                                            if (Utilities.arraysEquals(secureDocument8.fileSecret, 0, tL_secureFile3.secret, 0)) {
                                                                this.this$1.renameFile(secureDocument8, tL_secureFile3);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        PassportActivity.this.setTypeValue(tL_secureRequiredType2, str, str2, tL_secureRequiredType, str3, z, i);
                                        if (runnable != null) {
                                            runnable.run();
                                        }
                                    }

                                    public void run(TLObject tLObject, TL_error tL_error) {
                                        if (tL_error != null) {
                                            if (tL_error.text.equals("EMAIL_VERIFICATION_NEEDED")) {
                                                TL_account_sendVerifyEmailCode tL_account_sendVerifyEmailCode = new TL_account_sendVerifyEmailCode();
                                                tL_account_sendVerifyEmailCode.email = str4;
                                                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(tL_account_sendVerifyEmailCode, new -$$Lambda$PassportActivity$19$1$z2A8tnBlw-sIzEUvPsIbORU04FQ(this, str4, tL_secureRequiredType3, this, errorRunnable2));
                                                return;
                                            } else if (tL_error.text.equals("PHONE_VERIFICATION_NEEDED")) {
                                                AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$19$1$PRLUYjV8ZrO0HoPOz4GspIpM-8g(errorRunnable2, tL_error, str4));
                                                return;
                                            }
                                        }
                                        if (tL_error != null || tL_inputSecureValue3 == null) {
                                            onResult(tL_error, (TL_secureValue) tLObject, null);
                                        } else {
                                            TL_secureValue tL_secureValue = (TL_secureValue) tLObject;
                                            TL_account_saveSecureValue tL_account_saveSecureValue = new TL_account_saveSecureValue();
                                            tL_account_saveSecureValue.value = tL_inputSecureValue3;
                                            tL_account_saveSecureValue.secure_secret_id = PassportActivity.this.secureSecretId;
                                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(tL_account_saveSecureValue, new -$$Lambda$PassportActivity$19$1$seC_3uNPyiW1J2ZXyiE5PktGlCs(this, tL_secureValue));
                                        }
                                    }

                                    public /* synthetic */ void lambda$run$2$PassportActivity$19$1(String str, TL_secureRequiredType tL_secureRequiredType, PassportActivityDelegate passportActivityDelegate, ErrorRunnable errorRunnable, TLObject tLObject, TL_error tL_error) {
                                        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$19$1$20EqXlc4T1xja88z5UaATcwnhKs(this, tLObject, str, tL_secureRequiredType, passportActivityDelegate, tL_error, errorRunnable));
                                    }

                                    public /* synthetic */ void lambda$null$1$PassportActivity$19$1(TLObject tLObject, String str, TL_secureRequiredType tL_secureRequiredType, PassportActivityDelegate passportActivityDelegate, TL_error tL_error, ErrorRunnable errorRunnable) {
                                        String str2 = str;
                                        TL_error tL_error2 = tL_error;
                                        ErrorRunnable errorRunnable2 = errorRunnable;
                                        if (tLObject != null) {
                                            TL_account_sentEmailCode tL_account_sentEmailCode = (TL_account_sentEmailCode) tLObject;
                                            HashMap hashMap = new HashMap();
                                            hashMap.put("email", str);
                                            hashMap.put("pattern", tL_account_sentEmailCode.email_pattern);
                                            PassportActivity passportActivity = new PassportActivity(6, PassportActivity.this.currentForm, PassportActivity.this.currentPassword, tL_secureRequiredType, null, null, null, hashMap, null);
                                            passportActivity.currentAccount = PassportActivity.this.currentAccount;
                                            passportActivity.emailCodeLength = tL_account_sentEmailCode.length;
                                            passportActivity.saltedPassword = PassportActivity.this.saltedPassword;
                                            passportActivity.secureSecret = PassportActivity.this.secureSecret;
                                            passportActivity.delegate = passportActivityDelegate;
                                            PassportActivity.this.presentFragment(passportActivity, true);
                                            return;
                                        }
                                        PassportActivity.this.showAlertWithText(LocaleController.getString("PassportEmail", NUM), tL_error2.text);
                                        if (errorRunnable2 != null) {
                                            errorRunnable2.onError(tL_error2.text, str);
                                        }
                                    }

                                    public /* synthetic */ void lambda$run$4$PassportActivity$19$1(TL_secureValue tL_secureValue, TLObject tLObject, TL_error tL_error) {
                                        onResult(tL_error, (TL_secureValue) tLObject, tL_secureValue);
                                    }
                                };
                                instance.sendRequest(tLObject2, anonymousClass1);
                                return;
                            }
                        }
                        tL_inputSecureValue = tL_inputSecureValue2;
                        tL_inputSecureValue3 = null;
                        tLObject = tL_account_saveSecureValue;
                        tL_account_saveSecureValue = new TL_account_saveSecureValue();
                        tL_account_saveSecureValue.value = tL_inputSecureValue;
                        tL_account_saveSecureValue.secure_secret_id = PassportActivity.this.secureSecretId;
                        errorRunnable2 = errorRunnable;
                        str4 = str;
                        tL_secureRequiredType4 = tL_secureRequiredType2;
                        tL_secureRequiredType3 = tL_secureRequiredType;
                        arrayList3 = arrayList;
                        secureDocument4 = secureDocument;
                        secureDocument5 = secureDocument2;
                        secureDocument6 = secureDocument3;
                        arrayList4 = arrayList2;
                        anonymousClass1 = anonymousClass12;
                        str5 = str2;
                        instance = ConnectionsManager.getInstance(PassportActivity.this.currentAccount);
                        str6 = str3;
                        tLObject2 = tL_account_saveSecureValue;
                        runnable2 = runnable;
                        anonymousClass12 = /* anonymous class already generated */;
                        instance.sendRequest(tLObject2, anonymousClass1);
                        return;
                    }
                    if (errorRunnable2 != null) {
                        errorRunnable2.onError(null, null);
                    }
                }

                public SecureDocument saveFile(TL_secureFile tL_secureFile) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(FileLoader.getDirectory(4));
                    stringBuilder.append("/");
                    stringBuilder.append(tL_secureFile.dc_id);
                    stringBuilder.append("_");
                    stringBuilder.append(tL_secureFile.id);
                    stringBuilder.append(".jpg");
                    String stringBuilder2 = stringBuilder.toString();
                    EncryptionResult access$8400 = PassportActivity.this.createSecureDocument(stringBuilder2);
                    return new SecureDocument(access$8400.secureDocumentKey, tL_secureFile, stringBuilder2, access$8400.fileHash, access$8400.fileSecret);
                }

                public void deleteValue(TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, ArrayList<TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(tL_secureRequiredType, tL_secureRequiredType2, arrayList, z, runnable, errorRunnable, z2);
                }
            };
            passportActivity.currentAccount = this.currentAccount;
            passportActivity.saltedPassword = this.saltedPassword;
            passportActivity.secureSecret = this.secureSecret;
            passportActivity.currentBotId = this.currentBotId;
            passportActivity.fieldsErrors = hashMap5;
            passportActivity.documentOnly = z2;
            passportActivity.documentsErrors = hashMap6;
            passportActivity.availableDocumentTypes = arrayList;
            if (i2 == 4) {
                passportActivity.currentEmail = this.currentEmail;
            }
            presentFragment(passportActivity);
        }
    }

    private TL_secureValue removeValue(TL_secureRequiredType tL_secureRequiredType) {
        if (tL_secureRequiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int i = 0; i < size; i++) {
            if (tL_secureRequiredType.type.getClass() == ((TL_secureValue) this.currentForm.values.get(i)).type.getClass()) {
                return (TL_secureValue) this.currentForm.values.remove(i);
            }
        }
        return null;
    }

    private void deleteValueInternal(TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, ArrayList<TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable, boolean z2) {
        TL_secureRequiredType tL_secureRequiredType3 = tL_secureRequiredType;
        TL_secureRequiredType tL_secureRequiredType4 = tL_secureRequiredType2;
        if (tL_secureRequiredType3 != null) {
            TL_account_deleteSecureValue tL_account_deleteSecureValue = new TL_account_deleteSecureValue();
            if (!z2 || tL_secureRequiredType4 == null) {
                if (z) {
                    tL_account_deleteSecureValue.types.add(tL_secureRequiredType3.type);
                }
                if (tL_secureRequiredType4 != null) {
                    tL_account_deleteSecureValue.types.add(tL_secureRequiredType4.type);
                }
            } else {
                tL_account_deleteSecureValue.types.add(tL_secureRequiredType4.type);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_deleteSecureValue, new -$$Lambda$PassportActivity$yqjIwJxQHVRYAcsusKtM70xYOHo(this, errorRunnable, z2, tL_secureRequiredType2, tL_secureRequiredType, z, arrayList, runnable));
        }
    }

    public /* synthetic */ void lambda$deleteValueInternal$61$PassportActivity(ErrorRunnable errorRunnable, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$b8qjtHw-SwhVCQIFBk7EnKFy_9c(this, tL_error, errorRunnable, z, tL_secureRequiredType, tL_secureRequiredType2, z2, arrayList, runnable));
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ab  */
    public /* synthetic */ void lambda$null$60$PassportActivity(org.telegram.tgnet.TLRPC.TL_error r12, org.telegram.ui.PassportActivity.ErrorRunnable r13, boolean r14, org.telegram.tgnet.TLRPC.TL_secureRequiredType r15, org.telegram.tgnet.TLRPC.TL_secureRequiredType r16, boolean r17, java.util.ArrayList r18, java.lang.Runnable r19) {
        /*
        r11 = this;
        r8 = r11;
        r0 = r12;
        r1 = r13;
        r2 = r15;
        r3 = r16;
        r4 = r18;
        r5 = 0;
        if (r0 == 0) goto L_0x0022;
    L_0x000b:
        if (r1 == 0) goto L_0x0012;
    L_0x000d:
        r2 = r0.text;
        r13.onError(r2, r5);
    L_0x0012:
        r1 = NUM; // 0x7f0d00eb float:1.8742591E38 double:1.0531298936E-314;
        r2 = "AppName";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0 = r0.text;
        r11.showAlertWithText(r1, r0);
        goto L_0x00ff;
    L_0x0022:
        if (r14 == 0) goto L_0x002e;
    L_0x0024:
        if (r2 == 0) goto L_0x002a;
    L_0x0026:
        r11.removeValue(r15);
        goto L_0x0036;
    L_0x002a:
        r11.removeValue(r3);
        goto L_0x0036;
    L_0x002e:
        if (r17 == 0) goto L_0x0033;
    L_0x0030:
        r11.removeValue(r3);
    L_0x0033:
        r11.removeValue(r15);
    L_0x0036:
        r0 = r8.currentActivityType;
        r1 = 8;
        r6 = 0;
        if (r0 != r1) goto L_0x0066;
    L_0x003d:
        r0 = r8.typesViews;
        r0 = r0.remove(r3);
        r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0;
        if (r0 == 0) goto L_0x0061;
    L_0x0047:
        r1 = r8.linearLayout2;
        r1.removeView(r0);
        r0 = r8.linearLayout2;
        r1 = r0.getChildCount();
        r1 = r1 + -6;
        r0 = r0.getChildAt(r1);
        r1 = r0 instanceof org.telegram.ui.PassportActivity.TextDetailSecureCell;
        if (r1 == 0) goto L_0x0061;
    L_0x005c:
        r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0;
        r0.setNeedDivider(r6);
    L_0x0061:
        r11.updateManageVisibility();
        goto L_0x00fa;
    L_0x0066:
        if (r2 == 0) goto L_0x00ad;
    L_0x0068:
        if (r4 == 0) goto L_0x00ad;
    L_0x006a:
        r0 = r18.size();
        r1 = 1;
        if (r0 <= r1) goto L_0x00ad;
    L_0x0071:
        r0 = r18.size();
        r1 = 0;
    L_0x0076:
        if (r1 >= r0) goto L_0x009e;
    L_0x0078:
        r7 = r4.get(r1);
        r7 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r7;
        r9 = r11.getValueByType(r7, r6);
        if (r9 == 0) goto L_0x009b;
    L_0x0084:
        r0 = r9.data;
        if (r0 == 0) goto L_0x009f;
    L_0x0088:
        r1 = r0.data;
        r2 = r0.secret;
        r0 = r0.data_hash;
        r0 = r11.decryptValueSecret(r2, r0);
        r2 = r9.data;
        r2 = r2.data_hash;
        r0 = r11.decryptData(r1, r0, r2);
        goto L_0x00a0;
    L_0x009b:
        r1 = r1 + 1;
        goto L_0x0076;
    L_0x009e:
        r7 = r2;
    L_0x009f:
        r0 = r5;
    L_0x00a0:
        if (r7 != 0) goto L_0x00ab;
    L_0x00a2:
        r1 = r4.get(r6);
        r1 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r1;
        r9 = r0;
        r7 = r1;
        goto L_0x00af;
    L_0x00ab:
        r9 = r0;
        goto L_0x00af;
    L_0x00ad:
        r7 = r2;
        r9 = r5;
    L_0x00af:
        if (r17 == 0) goto L_0x00c8;
    L_0x00b1:
        r2 = 0;
        r5 = 0;
        if (r4 == 0) goto L_0x00bb;
    L_0x00b5:
        r0 = r18.size();
        r10 = r0;
        goto L_0x00bc;
    L_0x00bb:
        r10 = 0;
    L_0x00bc:
        r0 = r11;
        r1 = r16;
        r3 = r5;
        r4 = r7;
        r5 = r9;
        r6 = r14;
        r7 = r10;
        r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7);
        goto L_0x00fa;
    L_0x00c8:
        r0 = r11.getValueByType(r3, r6);
        if (r0 == 0) goto L_0x00e5;
    L_0x00ce:
        r1 = r0.data;
        if (r1 == 0) goto L_0x00e5;
    L_0x00d2:
        r2 = r1.data;
        r5 = r1.secret;
        r1 = r1.data_hash;
        r1 = r11.decryptValueSecret(r5, r1);
        r0 = r0.data;
        r0 = r0.data_hash;
        r0 = r11.decryptData(r2, r1, r0);
        r5 = r0;
    L_0x00e5:
        r2 = 0;
        if (r4 == 0) goto L_0x00ee;
    L_0x00e8:
        r0 = r18.size();
        r10 = r0;
        goto L_0x00ef;
    L_0x00ee:
        r10 = 0;
    L_0x00ef:
        r0 = r11;
        r1 = r16;
        r3 = r5;
        r4 = r7;
        r5 = r9;
        r6 = r14;
        r7 = r10;
        r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7);
    L_0x00fa:
        if (r19 == 0) goto L_0x00ff;
    L_0x00fc:
        r19.run();
    L_0x00ff:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$null$60$PassportActivity(org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.PassportActivity$ErrorRunnable, boolean, org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType, boolean, java.util.ArrayList, java.lang.Runnable):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x018e  */
    private org.telegram.ui.PassportActivity.TextDetailSecureCell addField(android.content.Context r17, org.telegram.tgnet.TLRPC.TL_secureRequiredType r18, java.util.ArrayList<org.telegram.tgnet.TLRPC.TL_secureRequiredType> r19, boolean r20, boolean r21) {
        /*
        r16 = this;
        r8 = r16;
        r1 = r18;
        r0 = r19;
        r6 = r20;
        r2 = 0;
        if (r0 == 0) goto L_0x0011;
    L_0x000b:
        r3 = r19.size();
        r7 = r3;
        goto L_0x0012;
    L_0x0011:
        r7 = 0;
    L_0x0012:
        r9 = new org.telegram.ui.PassportActivity$TextDetailSecureCell;
        r3 = r17;
        r9.<init>(r3);
        r3 = 1;
        r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3);
        r9.setBackgroundDrawable(r4);
        r4 = r1.type;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
        r10 = NUM; // 0x7f0d0792 float:1.8746045E38 double:1.053130735E-314;
        r11 = "PassportTwoDocuments";
        r12 = 2;
        r13 = "";
        if (r5 == 0) goto L_0x0092;
    L_0x002f:
        if (r0 == 0) goto L_0x0082;
    L_0x0031:
        r4 = r19.isEmpty();
        if (r4 == 0) goto L_0x0038;
    L_0x0037:
        goto L_0x0082;
    L_0x0038:
        if (r6 == 0) goto L_0x004d;
    L_0x003a:
        r4 = r19.size();
        if (r4 != r3) goto L_0x004d;
    L_0x0040:
        r4 = r0.get(r2);
        r4 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r4;
        r4 = r4.type;
        r4 = r8.getTextForType(r4);
        goto L_0x008b;
    L_0x004d:
        if (r6 == 0) goto L_0x0078;
    L_0x004f:
        r4 = r19.size();
        if (r4 != r12) goto L_0x0078;
    L_0x0055:
        r4 = new java.lang.Object[r12];
        r5 = r0.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r5;
        r5 = r5.type;
        r5 = r8.getTextForType(r5);
        r4[r2] = r5;
        r5 = r0.get(r3);
        r5 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r5;
        r5 = r5.type;
        r5 = r8.getTextForType(r5);
        r4[r3] = r5;
        r4 = org.telegram.messenger.LocaleController.formatString(r11, r10, r4);
        goto L_0x008b;
    L_0x0078:
        r4 = NUM; // 0x7f0d0723 float:1.874582E38 double:1.05313068E-314;
        r5 = "PassportIdentityDocument";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x008b;
    L_0x0082:
        r4 = NUM; // 0x7f0d076d float:1.874597E38 double:1.053130717E-314;
        r5 = "PassportPersonalDetails";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
    L_0x008b:
        r5 = r21 ^ 1;
        r9.setTextAndValue(r4, r13, r5);
        goto L_0x011d;
    L_0x0092:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
        if (r5 == 0) goto L_0x00f8;
    L_0x0096:
        if (r0 == 0) goto L_0x00e9;
    L_0x0098:
        r4 = r19.isEmpty();
        if (r4 == 0) goto L_0x009f;
    L_0x009e:
        goto L_0x00e9;
    L_0x009f:
        if (r6 == 0) goto L_0x00b4;
    L_0x00a1:
        r4 = r19.size();
        if (r4 != r3) goto L_0x00b4;
    L_0x00a7:
        r4 = r0.get(r2);
        r4 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r4;
        r4 = r4.type;
        r4 = r8.getTextForType(r4);
        goto L_0x00f2;
    L_0x00b4:
        if (r6 == 0) goto L_0x00df;
    L_0x00b6:
        r4 = r19.size();
        if (r4 != r12) goto L_0x00df;
    L_0x00bc:
        r4 = new java.lang.Object[r12];
        r5 = r0.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r5;
        r5 = r5.type;
        r5 = r8.getTextForType(r5);
        r4[r2] = r5;
        r5 = r0.get(r3);
        r5 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r5;
        r5 = r5.type;
        r5 = r8.getTextForType(r5);
        r4[r3] = r5;
        r4 = org.telegram.messenger.LocaleController.formatString(r11, r10, r4);
        goto L_0x00f2;
    L_0x00df:
        r4 = NUM; // 0x7f0d077f float:1.8746007E38 double:1.0531307257E-314;
        r5 = "PassportResidentialAddress";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x00f2;
    L_0x00e9:
        r4 = NUM; // 0x7f0d06fd float:1.8745743E38 double:1.0531306614E-314;
        r5 = "PassportAddress";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
    L_0x00f2:
        r5 = r21 ^ 1;
        r9.setTextAndValue(r4, r13, r5);
        goto L_0x011d;
    L_0x00f8:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
        if (r5 == 0) goto L_0x010b;
    L_0x00fc:
        r4 = NUM; // 0x7f0d0770 float:1.8745977E38 double:1.053130718E-314;
        r5 = "PassportPhone";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = r21 ^ 1;
        r9.setTextAndValue(r4, r13, r5);
        goto L_0x011d;
    L_0x010b:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
        if (r4 == 0) goto L_0x011d;
    L_0x010f:
        r4 = NUM; // 0x7f0d0719 float:1.87458E38 double:1.0531306753E-314;
        r5 = "PassportEmail";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = r21 ^ 1;
        r9.setTextAndValue(r4, r13, r5);
    L_0x011d:
        r4 = r8.currentActivityType;
        r5 = 8;
        r10 = -2;
        r11 = -1;
        if (r4 != r5) goto L_0x0135;
    L_0x0125:
        r4 = r8.linearLayout2;
        r5 = r4.getChildCount();
        r5 = r5 + -5;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10);
        r4.addView(r9, r5, r10);
        goto L_0x013e;
    L_0x0135:
        r4 = r8.linearLayout2;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10);
        r4.addView(r9, r5);
    L_0x013e:
        r4 = new org.telegram.ui.-$$Lambda$PassportActivity$5Sry1zhVbDTBEYl5VpIUjvovUNY;
        r4.<init>(r8, r0, r1, r6);
        r9.setOnClickListener(r4);
        r4 = r8.typesViews;
        r4.put(r1, r9);
        r4 = r8.typesValues;
        r5 = new java.util.HashMap;
        r5.<init>();
        r4.put(r1, r5);
        r4 = r8.getValueByType(r1, r2);
        r5 = 0;
        if (r4 == 0) goto L_0x018a;
    L_0x015c:
        r10 = r4.plain_data;
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_securePlainEmail;
        if (r11 == 0) goto L_0x0168;
    L_0x0162:
        r10 = (org.telegram.tgnet.TLRPC.TL_securePlainEmail) r10;
        r4 = r10.email;
    L_0x0166:
        r10 = r5;
        goto L_0x018c;
    L_0x0168:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_securePlainPhone;
        if (r11 == 0) goto L_0x0171;
    L_0x016c:
        r10 = (org.telegram.tgnet.TLRPC.TL_securePlainPhone) r10;
        r4 = r10.phone;
        goto L_0x0166;
    L_0x0171:
        r10 = r4.data;
        if (r10 == 0) goto L_0x018a;
    L_0x0175:
        r11 = r10.data;
        r12 = r10.secret;
        r10 = r10.data_hash;
        r10 = r8.decryptValueSecret(r12, r10);
        r4 = r4.data;
        r4 = r4.data_hash;
        r4 = r8.decryptData(r11, r10, r4);
        r10 = r4;
        r4 = r5;
        goto L_0x018c;
    L_0x018a:
        r4 = r5;
        r10 = r4;
    L_0x018c:
        if (r0 == 0) goto L_0x01e5;
    L_0x018e:
        r11 = r19.isEmpty();
        if (r11 != 0) goto L_0x01e5;
    L_0x0194:
        r11 = r19.size();
        r12 = r5;
        r14 = r12;
        r5 = 0;
        r13 = 0;
    L_0x019c:
        if (r5 >= r11) goto L_0x01d9;
    L_0x019e:
        r15 = r0.get(r5);
        r15 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r15;
        r3 = r8.typesValues;
        r2 = new java.util.HashMap;
        r2.<init>();
        r3.put(r15, r2);
        r2 = r8.documentsToTypesLink;
        r2.put(r15, r1);
        if (r13 != 0) goto L_0x01d4;
    L_0x01b5:
        r2 = 0;
        r3 = r8.getValueByType(r15, r2);
        if (r3 == 0) goto L_0x01d4;
    L_0x01bc:
        r2 = r3.data;
        if (r2 == 0) goto L_0x01d2;
    L_0x01c0:
        r12 = r2.data;
        r13 = r2.secret;
        r2 = r2.data_hash;
        r2 = r8.decryptValueSecret(r13, r2);
        r3 = r3.data;
        r3 = r3.data_hash;
        r14 = r8.decryptData(r12, r2, r3);
    L_0x01d2:
        r12 = r15;
        r13 = 1;
    L_0x01d4:
        r5 = r5 + 1;
        r2 = 0;
        r3 = 1;
        goto L_0x019c;
    L_0x01d9:
        if (r12 != 0) goto L_0x01e3;
    L_0x01db:
        r2 = 0;
        r0 = r0.get(r2);
        r0 = (org.telegram.tgnet.TLRPC.TL_secureRequiredType) r0;
        r12 = r0;
    L_0x01e3:
        r5 = r14;
        goto L_0x01e6;
    L_0x01e5:
        r12 = r5;
    L_0x01e6:
        r0 = r16;
        r1 = r18;
        r2 = r4;
        r3 = r10;
        r4 = r12;
        r6 = r20;
        r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7);
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.addField(android.content.Context, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.ArrayList, boolean, boolean):org.telegram.ui.PassportActivity$TextDetailSecureCell");
    }

    public /* synthetic */ void lambda$addField$65$PassportActivity(ArrayList arrayList, TL_secureRequiredType tL_secureRequiredType, boolean z, View view) {
        int i;
        TL_secureRequiredType tL_secureRequiredType2;
        if (arrayList != null) {
            int size = arrayList.size();
            for (i = 0; i < size; i++) {
                tL_secureRequiredType2 = (TL_secureRequiredType) arrayList.get(i);
                if (getValueByType(tL_secureRequiredType2, false) != null || size == 1) {
                    break;
                }
            }
        }
        tL_secureRequiredType2 = null;
        SecureValueType secureValueType = tL_secureRequiredType.type;
        String str = "Cancel";
        if (!(secureValueType instanceof TL_secureValueTypePersonalDetails) && !(secureValueType instanceof TL_secureValueTypeAddress)) {
            boolean z2 = secureValueType instanceof TL_secureValueTypePhone;
            if ((z2 || (secureValueType instanceof TL_secureValueTypeEmail)) && getValueByType(tL_secureRequiredType, false) != null) {
                int i2;
                String str2;
                Builder builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PassportActivity$SBPx7fdXp-MbcirnzsQkO7Z6gg0(this, tL_secureRequiredType, z));
                builder.setNegativeButton(LocaleController.getString(str, NUM), null);
                builder.setTitle(LocaleController.getString("AppName", NUM));
                if (z2) {
                    i2 = NUM;
                    str2 = "PassportDeletePhoneAlert";
                } else {
                    i2 = NUM;
                    str2 = "PassportDeleteEmailAlert";
                }
                builder.setMessage(LocaleController.getString(str2, i2));
                showDialog(builder.create());
                return;
            }
        } else if (!(tL_secureRequiredType2 != null || arrayList == null || arrayList.isEmpty())) {
            Builder builder2 = new Builder(getParentActivity());
            builder2.setPositiveButton(LocaleController.getString(str, NUM), null);
            SecureValueType secureValueType2 = tL_secureRequiredType.type;
            if (secureValueType2 instanceof TL_secureValueTypePersonalDetails) {
                builder2.setTitle(LocaleController.getString("PassportIdentityDocument", NUM));
            } else if (secureValueType2 instanceof TL_secureValueTypeAddress) {
                builder2.setTitle(LocaleController.getString("PassportAddress", NUM));
            }
            ArrayList arrayList2 = new ArrayList();
            i = arrayList.size();
            for (int i3 = 0; i3 < i; i3++) {
                SecureValueType secureValueType3 = ((TL_secureRequiredType) arrayList.get(i3)).type;
                if (secureValueType3 instanceof TL_secureValueTypeDriverLicense) {
                    arrayList2.add(LocaleController.getString("PassportAddLicence", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypePassport) {
                    arrayList2.add(LocaleController.getString("PassportAddPassport", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypeInternalPassport) {
                    arrayList2.add(LocaleController.getString("PassportAddInternalPassport", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypeIdentityCard) {
                    arrayList2.add(LocaleController.getString("PassportAddCard", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypeUtilityBill) {
                    arrayList2.add(LocaleController.getString("PassportAddBill", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypeBankStatement) {
                    arrayList2.add(LocaleController.getString("PassportAddBank", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypeRentalAgreement) {
                    arrayList2.add(LocaleController.getString("PassportAddAgreement", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypeTemporaryRegistration) {
                    arrayList2.add(LocaleController.getString("PassportAddTemporaryRegistration", NUM));
                } else if (secureValueType3 instanceof TL_secureValueTypePassportRegistration) {
                    arrayList2.add(LocaleController.getString("PassportAddPassportRegistration", NUM));
                }
            }
            builder2.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[0]), new -$$Lambda$PassportActivity$oX34r1oAXwJXLUOfYXGW2rjRrMs(this, tL_secureRequiredType, arrayList, z));
            showDialog(builder2.create());
            return;
        }
        openTypeActivity(tL_secureRequiredType, tL_secureRequiredType2, arrayList, z);
    }

    public /* synthetic */ void lambda$null$62$PassportActivity(TL_secureRequiredType tL_secureRequiredType, ArrayList arrayList, boolean z, DialogInterface dialogInterface, int i) {
        openTypeActivity(tL_secureRequiredType, (TL_secureRequiredType) arrayList.get(i), arrayList, z);
    }

    public /* synthetic */ void lambda$null$64$PassportActivity(TL_secureRequiredType tL_secureRequiredType, boolean z, DialogInterface dialogInterface, int i) {
        needShowProgress();
        deleteValueInternal(tL_secureRequiredType, null, null, true, new -$$Lambda$7J3LPgacD3LbZtGqatVXuN86YjY(this), new -$$Lambda$PassportActivity$3vC5Cf8HrMsDxrDg3Ea7D_w0Ct0(this), z);
    }

    public /* synthetic */ void lambda$null$63$PassportActivity(String str, String str2) {
        needHideProgress();
    }

    private SecureDocumentKey getSecureDocumentKey(byte[] bArr, byte[] bArr2) {
        bArr = Utilities.computeSHA512(decryptValueSecret(bArr, bArr2), bArr2);
        byte[] bArr3 = new byte[32];
        System.arraycopy(bArr, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(bArr, 32, bArr4, 0, 16);
        return new SecureDocumentKey(bArr3, bArr4);
    }

    private byte[] decryptSecret(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr.length != 32) {
            return null;
        }
        byte[] bArr3 = new byte[32];
        System.arraycopy(bArr2, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(bArr2, 32, bArr4, 0, 16);
        bArr2 = new byte[32];
        System.arraycopy(bArr, 0, bArr2, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr2, bArr3, bArr4, 0, bArr2.length, 0, 0);
        return bArr2;
    }

    private byte[] decryptValueSecret(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr.length != 32 || bArr2 == null || bArr2.length != 32) {
            return null;
        }
        byte[] bArr3 = new byte[32];
        System.arraycopy(this.saltedPassword, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(this.saltedPassword, 32, bArr4, 0, 16);
        byte[] bArr5 = new byte[32];
        System.arraycopy(this.secureSecret, 0, bArr5, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, bArr5.length, 0, 0);
        if (!checkSecret(bArr5, null)) {
            return null;
        }
        bArr2 = Utilities.computeSHA512(bArr5, bArr2);
        bArr3 = new byte[32];
        System.arraycopy(bArr2, 0, bArr3, 0, 32);
        bArr4 = new byte[16];
        System.arraycopy(bArr2, 32, bArr4, 0, 16);
        bArr2 = new byte[32];
        System.arraycopy(bArr, 0, bArr2, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr2, bArr3, bArr4, 0, bArr2.length, 0, 0);
        return bArr2;
    }

    private EncryptionResult createSecureDocument(String str) {
        RandomAccessFile randomAccessFile;
        byte[] bArr = new byte[((int) new File(str).length())];
        try {
            randomAccessFile = new RandomAccessFile(str, "rws");
            try {
                randomAccessFile.readFully(bArr);
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            randomAccessFile = null;
        }
        EncryptionResult encryptData = encryptData(bArr);
        try {
            randomAccessFile.seek(0);
            randomAccessFile.write(encryptData.encryptedData);
            randomAccessFile.close();
        } catch (Exception unused3) {
        }
        return encryptData;
    }

    private String decryptData(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (bArr == null || bArr2 == null || bArr2.length != 32 || bArr3 == null || bArr3.length != 32) {
            return null;
        }
        bArr2 = Utilities.computeSHA512(bArr2, bArr3);
        byte[] bArr4 = new byte[32];
        System.arraycopy(bArr2, 0, bArr4, 0, 32);
        byte[] bArr5 = new byte[16];
        System.arraycopy(bArr2, 32, bArr5, 0, 16);
        bArr2 = new byte[bArr.length];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        Utilities.aesCbcEncryptionByteArraySafe(bArr2, bArr4, bArr5, 0, bArr2.length, 0, 0);
        if (!Arrays.equals(Utilities.computeSHA256(bArr2), bArr3)) {
            return null;
        }
        int i = bArr2[0] & 255;
        return new String(bArr2, i, bArr2.length - i);
    }

    public static boolean checkSecret(byte[] bArr, Long l) {
        if (bArr == null || bArr.length != 32) {
            return false;
        }
        int i = 0;
        for (byte b : bArr) {
            i += b & 255;
        }
        if (i % 255 != 239) {
            return false;
        }
        if (l == null || Utilities.bytesToLong(Utilities.computeSHA256(bArr)) == l.longValue()) {
            return true;
        }
        return false;
    }

    private byte[] getRandomSecret() {
        int i;
        byte[] bArr = new byte[32];
        Utilities.random.nextBytes(bArr);
        int i2 = 0;
        for (byte b : bArr) {
            i2 += b & 255;
        }
        i2 %= 255;
        if (i2 != 239) {
            i = 239 - i2;
            int nextInt = Utilities.random.nextInt(32);
            i2 = (bArr[nextInt] & 255) + i;
            if (i2 < 255) {
                i2 += 255;
            }
            bArr[nextInt] = (byte) (i2 % 255);
        }
        return bArr;
    }

    private EncryptionResult encryptData(byte[] bArr) {
        Object obj = bArr;
        byte[] randomSecret = getRandomSecret();
        int nextInt = Utilities.random.nextInt(208) + 32;
        while ((obj.length + nextInt) % 16 != 0) {
            nextInt++;
        }
        byte[] bArr2 = new byte[nextInt];
        Utilities.random.nextBytes(bArr2);
        bArr2[0] = (byte) nextInt;
        byte[] bArr3 = new byte[(obj.length + nextInt)];
        System.arraycopy(bArr2, 0, bArr3, 0, nextInt);
        System.arraycopy(obj, 0, bArr3, nextInt, obj.length);
        bArr2 = Utilities.computeSHA256(bArr3);
        byte[] computeSHA512 = Utilities.computeSHA512(randomSecret, bArr2);
        byte[] bArr4 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr4, 0, 32);
        byte[] bArr5 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr5, 0, 16);
        byte[] bArr6 = bArr5;
        Utilities.aesCbcEncryptionByteArraySafe(bArr3, bArr4, bArr5, 0, bArr3.length, 0, 1);
        computeSHA512 = new byte[32];
        System.arraycopy(this.saltedPassword, 0, computeSHA512, 0, 32);
        byte[] bArr7 = new byte[16];
        System.arraycopy(this.saltedPassword, 32, bArr7, 0, 16);
        byte[] bArr8 = new byte[32];
        System.arraycopy(this.secureSecret, 0, bArr8, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr8, computeSHA512, bArr7, 0, bArr8.length, 0, 0);
        computeSHA512 = Utilities.computeSHA512(bArr8, bArr2);
        bArr8 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr8, 0, 32);
        byte[] bArr9 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr9, 0, 16);
        byte[] bArr10 = new byte[32];
        System.arraycopy(randomSecret, 0, bArr10, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr10, bArr8, bArr9, 0, bArr10.length, 0, 1);
        return new EncryptionResult(bArr3, bArr10, randomSecret, bArr2, bArr4, bArr6);
    }

    private void showAlertWithText(String str, String str2) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setTitle(str);
            builder.setMessage(str2);
            showDialog(builder.create());
        }
    }

    private void onPasscodeError(boolean z) {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            if (z) {
                this.inputFields[0].setText("");
            }
            AndroidUtilities.shakeView(this.inputFields[0], 2.0f, 0);
        }
    }

    private void startPhoneVerification(boolean z, String str, Runnable runnable, ErrorRunnable errorRunnable, PassportActivityDelegate passportActivityDelegate) {
        Object obj;
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        Object obj2 = (telephonyManager.getSimState() == 1 || telephonyManager.getPhoneType() == 0) ? null : 1;
        if (getParentActivity() == null || VERSION.SDK_INT < 23 || obj2 == null) {
            obj = 1;
        } else {
            String str2 = "android.permission.READ_PHONE_STATE";
            obj = getParentActivity().checkSelfPermission(str2) == 0 ? 1 : null;
            if (z) {
                this.permissionsItems.clear();
                if (obj == null) {
                    this.permissionsItems.add(str2);
                }
                if (!this.permissionsItems.isEmpty()) {
                    if (getParentActivity().shouldShowRequestPermissionRationale(str2)) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                        this.permissionsDialog = showDialog(builder.create());
                    } else {
                        getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
                    }
                    this.pendingPhone = str;
                    this.pendingErrorRunnable = errorRunnable;
                    this.pendingFinishRunnable = runnable;
                    this.pendingDelegate = passportActivityDelegate;
                    return;
                }
            }
        }
        TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode = new TL_account_sendVerifyPhoneCode();
        tL_account_sendVerifyPhoneCode.phone_number = str;
        tL_account_sendVerifyPhoneCode.settings = new TL_codeSettings();
        TL_codeSettings tL_codeSettings = tL_account_sendVerifyPhoneCode.settings;
        boolean z2 = (obj2 == null || obj == null) ? false : true;
        tL_codeSettings.allow_flashcall = z2;
        if (VERSION.SDK_INT >= 26) {
            try {
                tL_account_sendVerifyPhoneCode.settings.app_hash = SmsManager.getDefault().createAppSpecificSmsToken(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, SmsReceiver.class), NUM));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else {
            tL_codeSettings = tL_account_sendVerifyPhoneCode.settings;
            tL_codeSettings.app_hash = BuildVars.SMS_HASH;
            tL_codeSettings.app_hash_persistent = true;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String str3 = "sms_hash";
        if (TextUtils.isEmpty(tL_account_sendVerifyPhoneCode.settings.app_hash)) {
            sharedPreferences.edit().remove(str3).commit();
        } else {
            TL_codeSettings tL_codeSettings2 = tL_account_sendVerifyPhoneCode.settings;
            tL_codeSettings2.flags |= 8;
            sharedPreferences.edit().putString(str3, tL_account_sendVerifyPhoneCode.settings.app_hash).commit();
        }
        if (tL_account_sendVerifyPhoneCode.settings.allow_flashcall) {
            try {
                String line1Number = telephonyManager.getLine1Number();
                if (TextUtils.isEmpty(line1Number)) {
                    tL_account_sendVerifyPhoneCode.settings.current_number = false;
                } else {
                    tL_account_sendVerifyPhoneCode.settings.current_number = PhoneNumberUtils.compare(str, line1Number);
                    if (!tL_account_sendVerifyPhoneCode.settings.current_number) {
                        tL_account_sendVerifyPhoneCode.settings.allow_flashcall = false;
                    }
                }
            } catch (Exception th2) {
                tL_account_sendVerifyPhoneCode.settings.allow_flashcall = false;
                FileLog.e(th2);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_sendVerifyPhoneCode, new -$$Lambda$PassportActivity$ziNY0SahWhkV1q9_2nyx-UlZ-Kc(this, str, passportActivityDelegate, tL_account_sendVerifyPhoneCode), 2);
    }

    public /* synthetic */ void lambda$startPhoneVerification$67$PassportActivity(String str, PassportActivityDelegate passportActivityDelegate, TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$XlPMKsspvXmLOvP8do731SElFP8(this, tL_error, str, passportActivityDelegate, tLObject, tL_account_sendVerifyPhoneCode));
    }

    public /* synthetic */ void lambda$null$66$PassportActivity(TL_error tL_error, String str, PassportActivityDelegate passportActivityDelegate, TLObject tLObject, TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode) {
        String str2 = str;
        if (tL_error == null) {
            HashMap hashMap = new HashMap();
            hashMap.put("phone", str2);
            PassportActivity passportActivity = new PassportActivity(7, this.currentForm, this.currentPassword, this.currentType, null, null, null, hashMap, null);
            passportActivity.currentAccount = this.currentAccount;
            passportActivity.saltedPassword = this.saltedPassword;
            passportActivity.secureSecret = this.secureSecret;
            passportActivity.delegate = passportActivityDelegate;
            passportActivity.currentPhoneVerification = (TL_auth_sentCode) tLObject;
            presentFragment(passportActivity, true);
            return;
        }
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_sendVerifyPhoneCode, str2);
    }

    private void updatePasswordInterface() {
        if (this.noPasswordImageView != null) {
            TL_account_password tL_account_password = this.currentPassword;
            if (tL_account_password == null || this.usingSavedPassword != 0) {
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
            } else if (tL_account_password.has_password) {
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
                if (this.inputFields != null) {
                    tL_account_password = this.currentPassword;
                    if (tL_account_password == null || TextUtils.isEmpty(tL_account_password.hint)) {
                        this.inputFields[0].setHint(LocaleController.getString("LoginPassword", NUM));
                    } else {
                        this.inputFields[0].setHint(this.currentPassword.hint);
                    }
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

    private void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z2;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator[] animatorArr;
        if (z && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                AnimatorSet animatorSet2 = this.doneItemAnimation;
                Animator[] animatorArr2 = new Animator[6];
                animatorArr2[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_X, new float[]{0.1f});
                animatorArr2[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_Y, new float[]{0.1f});
                animatorArr2[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.ALPHA, new float[]{0.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr2);
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
                public void onAnimationEnd(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        if (z3) {
                            PassportActivity.this.doneItem.getImageView().setVisibility(4);
                        } else {
                            PassportActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (this.acceptTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
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
                public void onAnimationEnd(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        if (z3) {
                            PassportActivity.this.acceptTextView.setVisibility(4);
                        } else {
                            PassportActivity.this.progressViewButton.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.FileDidUpload) {
            String str = (String) objArr[0];
            SecureDocument secureDocument = (SecureDocument) this.uploadingDocuments.get(str);
            if (secureDocument != null) {
                secureDocument.inputFile = (TL_inputFile) objArr[1];
                this.uploadingDocuments.remove(str);
                if (this.uploadingDocuments.isEmpty()) {
                    ActionBarMenuItem actionBarMenuItem = this.doneItem;
                    if (actionBarMenuItem != null) {
                        actionBarMenuItem.setEnabled(true);
                        this.doneItem.setAlpha(1.0f);
                    }
                }
                HashMap hashMap = this.documentsCells;
                if (hashMap != null) {
                    SecureDocumentCell secureDocumentCell = (SecureDocumentCell) hashMap.get(secureDocument);
                    if (secureDocumentCell != null) {
                        secureDocumentCell.updateButtonState(true);
                    }
                }
                hashMap = this.errorsValues;
                if (hashMap != null) {
                    String str2 = "error_document_all";
                    if (hashMap.containsKey(str2)) {
                        this.errorsValues.remove(str2);
                        checkTopErrorCell(false);
                    }
                }
                i = secureDocument.type;
                if (i == 0) {
                    if (!(this.bottomCell == null || TextUtils.isEmpty(this.noAllDocumentsErrorText))) {
                        this.bottomCell.setText(this.noAllDocumentsErrorText);
                    }
                    this.errorsValues.remove("files_all");
                } else if (i == 4) {
                    if (!(this.bottomCellTranslation == null || TextUtils.isEmpty(this.noAllTranslationErrorText))) {
                        this.bottomCellTranslation.setText(this.noAllTranslationErrorText);
                    }
                    this.errorsValues.remove("translation_all");
                }
            }
        } else if (i != NotificationCenter.FileDidFailUpload) {
            if (i == NotificationCenter.didSetTwoStepPassword) {
                if (objArr == null || objArr.length <= 0) {
                    this.currentPassword = null;
                    loadPasswordInfo();
                } else {
                    if (objArr[7] != null) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                        if (editTextBoldCursorArr[0] != null) {
                            editTextBoldCursorArr[0].setText((String) objArr[7]);
                        }
                    }
                    if (objArr[6] == null) {
                        this.currentPassword = new TL_account_password();
                        TL_account_password tL_account_password = this.currentPassword;
                        tL_account_password.current_algo = (PasswordKdfAlgo) objArr[1];
                        tL_account_password.new_secure_algo = (SecurePasswordKdfAlgo) objArr[2];
                        tL_account_password.secure_random = (byte[]) objArr[3];
                        tL_account_password.has_recovery = TextUtils.isEmpty((String) objArr[4]) ^ 1;
                        tL_account_password = this.currentPassword;
                        tL_account_password.hint = (String) objArr[5];
                        tL_account_password.srp_id = -1;
                        tL_account_password.srp_B = new byte[256];
                        Utilities.random.nextBytes(tL_account_password.srp_B);
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputFields;
                        if (editTextBoldCursorArr2[0] != null && editTextBoldCursorArr2[0].length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                }
                updatePasswordInterface();
                return;
            }
            i = NotificationCenter.didRemoveTwoStepPassword;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$l67xseKhR4lQtrsyuaJeoxN_jJM(this));
        }
        int i = this.currentActivityType;
        if (i == 5) {
            if (z) {
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 2) {
                    onPasswordDone(false);
                }
            }
        } else if (i == 7) {
            if (z) {
                this.views[this.currentViewNum].onShow();
            }
        } else if (i == 4) {
            if (z) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if (i == 6) {
            if (z) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if ((i == 2 || i == 1) && VERSION.SDK_INT >= 21) {
            createChatAttachView();
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$68$PassportActivity() {
        presentFragment(this.presentAfterAnimation, true);
        this.presentAfterAnimation = null;
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1) {
            if (i == 0 || i == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    chatAttachAlert.onActivityResultFragment(i, intent, this.currentPicturePath);
                }
                this.currentPicturePath = null;
            } else if (i == 1) {
                if (intent == null || intent.getData() == null) {
                    showAttachmentError();
                    return;
                }
                ArrayList arrayList = new ArrayList();
                SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                sendingMediaInfo.uri = intent.getData();
                arrayList.add(sendingMediaInfo);
                processSelectedFiles(arrayList);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        int i2 = this.currentActivityType;
        if (i2 == 1 || i2 == 2) {
            ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
            if (chatAttachAlert != null) {
                if (i != 17 || chatAttachAlert == null) {
                    if (i == 21) {
                        if (!(getParentActivity() == null || iArr == null || iArr.length == 0 || iArr[0] == 0)) {
                            Builder builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setMessage(LocaleController.getString("PermissionNoAudioVideo", NUM));
                            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$PassportActivity$saEsQ-rHD2skXoYwyq-hN_ao-V8(this));
                            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                            builder.show();
                        }
                    } else if (i == 19 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
                        processSelectedAttach(0);
                    } else if (i == 22 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
                        TextSettingsCell textSettingsCell = this.scanDocumentCell;
                        if (textSettingsCell != null) {
                            textSettingsCell.callOnClick();
                        }
                    }
                }
                chatAttachAlert.checkCamera(false);
            }
        }
        if (this.currentActivityType == 3 && i == 6) {
            startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$69$PassportActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            intent.setData(Uri.parse(stringBuilder.toString()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        String str = this.currentPicturePath;
        if (str != null) {
            bundle.putString("path", str);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.currentPicturePath = bundle.getString("path");
    }

    public boolean onBackPressed() {
        int i = this.currentActivityType;
        int i2 = 0;
        if (i == 7) {
            this.views[this.currentViewNum].onBackPressed(true);
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (i2 >= slideViewArr.length) {
                    break;
                }
                if (slideViewArr[i2] != null) {
                    slideViewArr[i2].onDestroyActivity();
                }
                i2++;
            }
        } else if (i == 0 || i == 5) {
            callCallback(false);
        } else if (i == 1 || i == 2) {
            return checkDiscard() ^ 1;
        }
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType == 3 && VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
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
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int i, boolean z, Bundle bundle) {
        if (i == 3) {
            this.doneItem.setVisibility(8);
        }
        SlideView[] slideViewArr = this.views;
        final SlideView slideView = slideViewArr[this.currentViewNum];
        final SlideView slideView2 = slideViewArr[i];
        this.currentViewNum = i;
        slideView2.setParams(bundle, false);
        slideView2.onShow();
        if (z) {
            slideView2.setTranslationX((float) AndroidUtilities.displaySize.x);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.setDuration(300);
            r0 = new Animator[2];
            String str = "translationX";
            r0[0] = ObjectAnimator.ofFloat(slideView, str, new float[]{(float) (-AndroidUtilities.displaySize.x)});
            r0[1] = ObjectAnimator.ofFloat(slideView2, str, new float[]{0.0f});
            animatorSet.playTogether(r0);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    slideView2.setVisibility(0);
                }

                public void onAnimationEnd(Animator animator) {
                    slideView.setVisibility(8);
                    slideView.setX(0.0f);
                }
            });
            animatorSet.start();
            return;
        }
        slideView2.setTranslationX(0.0f);
        slideView2.setVisibility(0);
        if (slideView != slideView2) {
            slideView.setVisibility(8);
        }
    }

    private void fillNextCodeParams(Bundle bundle, TL_auth_sentCode tL_auth_sentCode, boolean z) {
        bundle.putString("phoneHash", tL_auth_sentCode.phone_code_hash);
        auth_CodeType auth_codetype = tL_auth_sentCode.next_type;
        String str = "nextType";
        if (auth_codetype instanceof TL_auth_codeTypeCall) {
            bundle.putInt(str, 4);
        } else if (auth_codetype instanceof TL_auth_codeTypeFlashCall) {
            bundle.putInt(str, 3);
        } else if (auth_codetype instanceof TL_auth_codeTypeSms) {
            bundle.putInt(str, 2);
        }
        if (tL_auth_sentCode.timeout == 0) {
            tL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tL_auth_sentCode.timeout * 1000);
        auth_SentCodeType auth_sentcodetype = tL_auth_sentCode.type;
        str = "length";
        String str2 = "type";
        if (auth_sentcodetype instanceof TL_auth_sentCodeTypeCall) {
            bundle.putInt(str2, 4);
            bundle.putInt(str, tL_auth_sentCode.type.length);
            setPage(2, z, bundle);
        } else if (auth_sentcodetype instanceof TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt(str2, 3);
            bundle.putString("pattern", tL_auth_sentCode.type.pattern);
            setPage(1, z, bundle);
        } else if (auth_sentcodetype instanceof TL_auth_sentCodeTypeSms) {
            bundle.putInt(str2, 2);
            bundle.putInt(str, tL_auth_sentCode.type.length);
            setPage(0, z, bundle);
        }
    }

    private void openAttachMenu() {
        if (getParentActivity() != null) {
            boolean z = false;
            if (this.uploadingFileType != 0 || this.documents.size() < 20) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (this.uploadingFileType == 1) {
                    z = true;
                }
                chatAttachAlert.setOpenWithFrontFaceCamera(z);
                this.chatAttachAlert.setMaxSelectedPhotos(getMaxSelectedDocuments());
                this.chatAttachAlert.loadGalleryPhotos();
                int i = VERSION.SDK_INT;
                if (i == 21 || i == 22) {
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
                public boolean allowGroupPhotos() {
                    return false;
                }

                public void didSelectBot(User user) {
                }

                public View getRevealView() {
                    return null;
                }

                public void didPressedButton(int i) {
                    if (!(PassportActivity.this.getParentActivity() == null || PassportActivity.this.chatAttachAlert == null)) {
                        if (i == 8 || i == 7) {
                            if (i != 8) {
                                PassportActivity.this.chatAttachAlert.dismiss();
                            }
                            HashMap selectedPhotos = PassportActivity.this.chatAttachAlert.getSelectedPhotos();
                            ArrayList selectedPhotosOrder = PassportActivity.this.chatAttachAlert.getSelectedPhotosOrder();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList arrayList = new ArrayList();
                                for (int i2 = 0; i2 < selectedPhotosOrder.size(); i2++) {
                                    PhotoEntry photoEntry = (PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(i2));
                                    SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                                    String str = photoEntry.imagePath;
                                    if (str != null) {
                                        sendingMediaInfo.path = str;
                                    } else {
                                        str = photoEntry.path;
                                        if (str != null) {
                                            sendingMediaInfo.path = str;
                                        }
                                    }
                                    arrayList.add(sendingMediaInfo);
                                    photoEntry.reset();
                                }
                                PassportActivity.this.processSelectedFiles(arrayList);
                            }
                        } else {
                            if (PassportActivity.this.chatAttachAlert != null) {
                                PassportActivity.this.chatAttachAlert.dismissWithButtonClick(i);
                            }
                            PassportActivity.this.processSelectedAttach(i);
                        }
                    }
                }

                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(PassportActivity.this.fragmentView.findFocus());
                }
            });
        }
    }

    private int getMaxSelectedDocuments() {
        int i = this.uploadingFileType;
        if (i == 0) {
            i = this.documents.size();
        } else if (i != 4) {
            return 1;
        } else {
            i = this.translationDocuments.size();
        }
        return 20 - i;
    }

    private void processSelectedAttach(int i) {
        if (i == 0) {
            if (VERSION.SDK_INT >= 23) {
                if (getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    getParentActivity().requestPermissions(new String[]{r0}, 19);
                    return;
                }
            }
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File generatePicturePath = AndroidUtilities.generatePicturePath();
                if (generatePicturePath != null) {
                    String str = "output";
                    if (VERSION.SDK_INT >= 24) {
                        intent.putExtra(str, FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", generatePicturePath));
                        intent.addFlags(2);
                        intent.addFlags(1);
                    } else {
                        intent.putExtra(str, Uri.fromFile(generatePicturePath));
                    }
                    this.currentPicturePath = generatePicturePath.getAbsolutePath();
                }
                startActivityForResult(intent, 0);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            String str2 = "android.permission.READ_EXTERNAL_STORAGE";
            if (i == 1) {
                if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission(str2) == 0) {
                    PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(0, false, false, null);
                    photoAlbumPickerActivity.setCurrentAccount(this.currentAccount);
                    photoAlbumPickerActivity.setMaxSelectedPhotos(getMaxSelectedDocuments());
                    photoAlbumPickerActivity.setAllowSearchImages(false);
                    photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                        public void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList) {
                            PassportActivity.this.processSelectedFiles(arrayList);
                        }

                        public void startPhotoSelectActivity() {
                            try {
                                Intent intent = new Intent("android.intent.action.PICK");
                                intent.setType("image/*");
                                PassportActivity.this.startActivityForResult(intent, 1);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    });
                    presentFragment(photoAlbumPickerActivity);
                } else {
                    getParentActivity().requestPermissions(new String[]{str2}, 4);
                }
            } else if (i == 4) {
                if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission(str2) == 0) {
                    DocumentSelectActivity documentSelectActivity = new DocumentSelectActivity(false);
                    documentSelectActivity.setCurrentAccount(this.currentAccount);
                    documentSelectActivity.setCanSelectOnlyImageFiles(true);
                    documentSelectActivity.setMaxSelectedFiles(getMaxSelectedDocuments());
                    documentSelectActivity.setDelegate(new DocumentSelectActivityDelegate() {
                        public /* synthetic */ void startMusicSelectActivity(BaseFragment baseFragment) {
                            -CC.$default$startMusicSelectActivity(this, baseFragment);
                        }

                        public void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList) {
                            documentSelectActivity.finishFragment();
                            ArrayList arrayList2 = new ArrayList();
                            int size = arrayList.size();
                            for (int i = 0; i < size; i++) {
                                SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                                sendingMediaInfo.path = (String) arrayList.get(i);
                                arrayList2.add(sendingMediaInfo);
                            }
                            PassportActivity.this.processSelectedFiles(arrayList2);
                        }

                        public void startDocumentSelectActivity() {
                            try {
                                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                                if (VERSION.SDK_INT >= 18) {
                                    intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
                                }
                                intent.setType("*/*");
                                PassportActivity.this.startActivityForResult(intent, 21);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    });
                    presentFragment(documentSelectActivity);
                } else {
                    getParentActivity().requestPermissions(new String[]{str2}, 4);
                }
            }
        }
    }

    private void fillInitialValues() {
        if (this.initialValues == null) {
            this.initialValues = getCurrentValues();
        }
    }

    private String getCurrentValues() {
        EditTextBoldCursor[] editTextBoldCursorArr;
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (true) {
            editTextBoldCursorArr = this.inputFields;
            str = ",";
            if (i >= editTextBoldCursorArr.length) {
                break;
            }
            stringBuilder.append(editTextBoldCursorArr[i].getText());
            stringBuilder.append(str);
            i++;
        }
        if (this.inputExtraFields != null) {
            i = 0;
            while (true) {
                editTextBoldCursorArr = this.inputExtraFields;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                stringBuilder.append(editTextBoldCursorArr[i].getText());
                stringBuilder.append(str);
                i++;
            }
        }
        i = this.documents.size();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append(((SecureDocument) this.documents.get(i2)).secureFile.id);
        }
        SecureDocument secureDocument = this.frontDocument;
        if (secureDocument != null) {
            stringBuilder.append(secureDocument.secureFile.id);
        }
        secureDocument = this.reverseDocument;
        if (secureDocument != null) {
            stringBuilder.append(secureDocument.secureFile.id);
        }
        secureDocument = this.selfieDocument;
        if (secureDocument != null) {
            stringBuilder.append(secureDocument.secureFile.id);
        }
        i = this.translationDocuments.size();
        for (int i3 = 0; i3 < i; i3++) {
            stringBuilder.append(((SecureDocument) this.translationDocuments.get(i3)).secureFile.id);
        }
        return stringBuilder.toString();
    }

    private boolean isHasNotAnyChanges() {
        String str = this.initialValues;
        return str == null || str.equals(getCurrentValues());
    }

    private boolean checkDiscard() {
        if (isHasNotAnyChanges()) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new -$$Lambda$PassportActivity$Z4N0XgwepebvvcdAUdqH_Rebz4Y(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("DiscardChanges", NUM));
        builder.setMessage(LocaleController.getString("PassportDiscardChanges", NUM));
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$checkDiscard$70$PassportActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private void processSelectedFiles(ArrayList<SendingMediaInfo> arrayList) {
        if (!arrayList.isEmpty()) {
            int i = this.uploadingFileType;
            boolean z = false;
            if (i != 1 && i != 4 && (this.currentType.type instanceof TL_secureValueTypePersonalDetails)) {
                i = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    if (i < editTextBoldCursorArr.length) {
                        if (i != 5 && i != 8 && i != 4 && i != 6 && editTextBoldCursorArr[i].length() > 0) {
                            break;
                        }
                        i++;
                    } else {
                        z = true;
                        break;
                    }
                }
            }
            Utilities.globalQueue.postRunnable(new -$$Lambda$PassportActivity$fufT8Jcf6nimD7xnbctUJqRi2PE(this, arrayList, this.uploadingFileType, z));
        }
    }

    public /* synthetic */ void lambda$processSelectedFiles$73$PassportActivity(ArrayList arrayList, int i, boolean z) {
        Throwable th;
        int i2 = i;
        int i3 = this.uploadingFileType;
        i3 = (i3 == 0 || i3 == 4) ? 20 : 1;
        int min = Math.min(i3, arrayList.size());
        Object obj = null;
        for (int i4 = 0; i4 < min; i4++) {
            SendingMediaInfo sendingMediaInfo = (SendingMediaInfo) arrayList.get(i4);
            Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, sendingMediaInfo.uri, 2048.0f, 2048.0f, false);
            if (loadBitmap != null) {
                PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 2048.0f, 2048.0f, 89, false, 320, 320);
                if (scaleAndSaveImage != null) {
                    TL_secureFile tL_secureFile = new TL_secureFile();
                    FileLocation fileLocation = scaleAndSaveImage.location;
                    tL_secureFile.dc_id = (int) fileLocation.volume_id;
                    tL_secureFile.id = (long) fileLocation.local_id;
                    tL_secureFile.date = (int) (System.currentTimeMillis() / 1000);
                    SecureDocument saveFile = this.delegate.saveFile(tL_secureFile);
                    saveFile.type = i2;
                    AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$2i-R1Bni7j2ywjhyD3jD3ME3dwM(this, saveFile, i2));
                    if (z && obj == null) {
                        try {
                            Result recognize = MrzRecognizer.recognize(loadBitmap, this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense);
                            if (recognize != null) {
                                try {
                                    AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$BRwn9uK4ua32ZIGkCWaMIr8OwBQ(this, recognize));
                                    obj = 1;
                                } catch (Throwable th2) {
                                    th = th2;
                                    obj = 1;
                                    FileLog.e(th);
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            FileLog.e(th);
                        }
                    }
                }
            }
        }
        SharedConfig.saveConfig();
    }

    public /* synthetic */ void lambda$null$71$PassportActivity(SecureDocument secureDocument, int i) {
        int i2 = this.uploadingFileType;
        SecureDocument secureDocument2;
        SecureDocumentCell secureDocumentCell;
        if (i2 == 1) {
            secureDocument2 = this.selfieDocument;
            if (secureDocument2 != null) {
                secureDocumentCell = (SecureDocumentCell) this.documentsCells.remove(secureDocument2);
                if (secureDocumentCell != null) {
                    this.selfieLayout.removeView(secureDocumentCell);
                }
                this.selfieDocument = null;
            }
        } else if (i2 == 4) {
            if (this.translationDocuments.size() >= 20) {
                return;
            }
        } else if (i2 == 2) {
            secureDocument2 = this.frontDocument;
            if (secureDocument2 != null) {
                secureDocumentCell = (SecureDocumentCell) this.documentsCells.remove(secureDocument2);
                if (secureDocumentCell != null) {
                    this.frontLayout.removeView(secureDocumentCell);
                }
                this.frontDocument = null;
            }
        } else if (i2 == 3) {
            secureDocument2 = this.reverseDocument;
            if (secureDocument2 != null) {
                secureDocumentCell = (SecureDocumentCell) this.documentsCells.remove(secureDocument2);
                if (secureDocumentCell != null) {
                    this.reverseLayout.removeView(secureDocumentCell);
                }
                this.reverseDocument = null;
            }
        } else if (i2 == 0 && this.documents.size() >= 20) {
            return;
        }
        this.uploadingDocuments.put(secureDocument.path, secureDocument);
        this.doneItem.setEnabled(false);
        this.doneItem.setAlpha(0.5f);
        FileLoader.getInstance(this.currentAccount).uploadFile(secureDocument.path, false, true, 16777216);
        addDocumentView(secureDocument, i);
        updateUploadText(i);
    }

    public /* synthetic */ void lambda$null$72$PassportActivity(Result result) {
        String str;
        int i = result.type;
        int i2;
        TL_secureRequiredType tL_secureRequiredType;
        if (i == 2) {
            if (!(this.currentDocumentsType.type instanceof TL_secureValueTypeIdentityCard)) {
                i = this.availableDocumentTypes.size();
                for (i2 = 0; i2 < i; i2++) {
                    tL_secureRequiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(i2);
                    if (tL_secureRequiredType.type instanceof TL_secureValueTypeIdentityCard) {
                        this.currentDocumentsType = tL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (i == 1) {
            if (!(this.currentDocumentsType.type instanceof TL_secureValueTypePassport)) {
                i = this.availableDocumentTypes.size();
                for (i2 = 0; i2 < i; i2++) {
                    tL_secureRequiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(i2);
                    if (tL_secureRequiredType.type instanceof TL_secureValueTypePassport) {
                        this.currentDocumentsType = tL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (i == 3) {
            if (!(this.currentDocumentsType.type instanceof TL_secureValueTypeInternalPassport)) {
                i = this.availableDocumentTypes.size();
                for (i2 = 0; i2 < i; i2++) {
                    tL_secureRequiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(i2);
                    if (tL_secureRequiredType.type instanceof TL_secureValueTypeInternalPassport) {
                        this.currentDocumentsType = tL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                }
            }
        } else if (i == 4 && !(this.currentDocumentsType.type instanceof TL_secureValueTypeDriverLicense)) {
            i = this.availableDocumentTypes.size();
            for (i2 = 0; i2 < i; i2++) {
                tL_secureRequiredType = (TL_secureRequiredType) this.availableDocumentTypes.get(i2);
                if (tL_secureRequiredType.type instanceof TL_secureValueTypeDriverLicense) {
                    this.currentDocumentsType = tL_secureRequiredType;
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
        i = result.gender;
        if (i != 0) {
            if (i == 1) {
                this.currentGender = "male";
                this.inputFields[4].setText(LocaleController.getString("PassportMale", NUM));
            } else if (i == 2) {
                this.currentGender = "female";
                this.inputFields[4].setText(LocaleController.getString("PassportFemale", NUM));
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            this.currentCitizeship = result.nationality;
            str = (String) this.languageMap.get(this.currentCitizeship);
            if (str != null) {
                this.inputFields[5].setText(str);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            this.currentResidence = result.issuingCountry;
            str = (String) this.languageMap.get(this.currentResidence);
            if (str != null) {
                this.inputFields[6].setText(str);
            }
        }
        String str2 = "%02d.%02d.%d";
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, str2, new Object[]{Integer.valueOf(i), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
        i = result.expiryDay;
        if (i > 0) {
            int i3 = result.expiryMonth;
            if (i3 > 0) {
                int i4 = result.expiryYear;
                if (i4 > 0) {
                    int[] iArr = this.currentExpireDate;
                    iArr[0] = i4;
                    iArr[1] = i3;
                    iArr[2] = i;
                    this.inputFields[8].setText(String.format(Locale.US, str2, new Object[]{Integer.valueOf(i), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)}));
                    return;
                }
            }
        }
        int[] iArr2 = this.currentExpireDate;
        iArr2[2] = 0;
        iArr2[1] = 0;
        iArr2[0] = 0;
        this.inputFields[8].setText(LocaleController.getString("PassportNoExpireDate", NUM));
    }

    public void setNeedActivityResult(boolean z) {
        this.needActivityResult = z;
    }

    public ThemeDescription[] getThemeDescriptions() {
        int i;
        String str;
        String str2;
        View view;
        EditTextBoldCursor[] editTextBoldCursorArr;
        ArrayList arrayList = new ArrayList();
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
        View view2 = this.extraBackgroundView2;
        if (view2 != null) {
            arrayList.add(new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        }
        for (i = 0; i < this.dividers.size(); i++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"));
        }
        Iterator it = this.documentsCells.entrySet().iterator();
        while (true) {
            str = "valueTextView";
            str2 = "textView";
            if (!it.hasNext()) {
                break;
            }
            view = (SecureDocumentCell) ((Entry) it.next()).getValue();
            View view3 = view;
            arrayList.add(new ThemeDescription(view3, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(view3, 0, new Class[]{SecureDocumentCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(view3, 0, new Class[]{SecureDocumentCell.class}, new String[]{str}, null, null, null, "windowBackgroundWhiteGrayText2"));
        }
        view = this.linearLayout2;
        View view4 = view;
        arrayList.add(new ThemeDescription(view4, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{str}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{str}, null, null, null, "windowBackgroundWhiteValueText"));
        view = this.linearLayout2;
        view4 = view;
        arrayList.add(new ThemeDescription(view4, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, null, null, null, "windowBackgroundWhite"));
        view = this.linearLayout2;
        View view5 = view;
        arrayList.add(new ThemeDescription(view5, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, null, null, null, "divider"));
        view = this.linearLayout2;
        view5 = view;
        arrayList.add(new ThemeDescription(view5, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{str}, null, null, null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, null, null, null, "featuredStickers_addedIcon"));
        view = this.linearLayout2;
        view4 = view;
        arrayList.add(new ThemeDescription(view4, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        view = this.linearLayout2;
        view4 = view;
        arrayList.add(new ThemeDescription(view4, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{str2}, null, null, null, "windowBackgroundWhiteGrayText4"));
        if (this.inputFields != null) {
            i = 0;
            while (true) {
                editTextBoldCursorArr = this.inputFields;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteRedText3"));
                i++;
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteRedText3"));
        }
        if (this.inputExtraFields != null) {
            i = 0;
            while (true) {
                editTextBoldCursorArr = this.inputExtraFields;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteRedText3"));
                i++;
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
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
