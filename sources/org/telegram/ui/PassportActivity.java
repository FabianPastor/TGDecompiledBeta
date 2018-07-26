package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SecureDocumentKey;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputSecureFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.SecureFile;
import org.telegram.tgnet.TLRPC.SecurePlainData;
import org.telegram.tgnet.TLRPC.SecureValueError;
import org.telegram.tgnet.TLRPC.SecureValueType;
import org.telegram.tgnet.TLRPC.TL_account_acceptAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_deleteSecureValue;
import org.telegram.tgnet.TLRPC.TL_account_getAllSecureValues;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_noPassword;
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
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_inputSecureFile;
import org.telegram.tgnet.TLRPC.TL_inputSecureFileUploaded;
import org.telegram.tgnet.TLRPC.TL_inputSecureValue;
import org.telegram.tgnet.TLRPC.TL_secureCredentialsEncrypted;
import org.telegram.tgnet.TLRPC.TL_secureData;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.tgnet.TLRPC.TL_securePlainEmail;
import org.telegram.tgnet.TLRPC.TL_securePlainPhone;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorData;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorFile;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorFiles;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorFrontSide;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorReverseSide;
import org.telegram.tgnet.TLRPC.TL_secureValueErrorSelfie;
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
import org.telegram.tgnet.TLRPC.account_Password;
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
import org.telegram.ui.Components.AlertsCreator.DatePickerDelegate;
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
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;
import org.telegram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import org.telegram.ui.MrzCameraActivity.MrzCameraActivityDelegate;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class PassportActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int FIELD_ADDRESS_COUNT = 6;
    private static final int FIELD_BIRTHDAY = 2;
    private static final int FIELD_CARDNUMBER = 6;
    private static final int FIELD_CITIZENSHIP = 4;
    private static final int FIELD_CITY = 3;
    private static final int FIELD_COUNTRY = 5;
    private static final int FIELD_EMAIL = 0;
    private static final int FIELD_EXPIRE = 7;
    private static final int FIELD_GENDER = 3;
    private static final int FIELD_IDENTITY_COUNT = 8;
    private static final int FIELD_IDENTITY_NODOC_COUNT = 6;
    private static final int FIELD_NAME = 0;
    private static final int FIELD_PASSWORD = 0;
    private static final int FIELD_PHONE = 2;
    private static final int FIELD_PHONECODE = 1;
    private static final int FIELD_PHONECOUNTRY = 0;
    private static final int FIELD_POSTCODE = 2;
    private static final int FIELD_RESIDENCE = 5;
    private static final int FIELD_STATE = 4;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int FIELD_SURNAME = 1;
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
    private static final int attach_document = 4;
    private static final int attach_gallery = 1;
    private static final int attach_photo = 0;
    private static final int done_button = 2;
    private static final int info_item = 1;
    private TextView acceptTextView;
    private TextSettingsCell addDocumentCell;
    private ShadowSectionCell addDocumentSectionCell;
    private ArrayList<SecureValueType> availableDocumentTypes;
    private TextInfoPrivacyCell bottomCell;
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
    private SecureValueType currentDocumentsType;
    private TL_secureValue currentDocumentsTypeValue;
    private String currentEmail;
    private int[] currentExpireDate;
    private TL_account_authorizationForm currentForm;
    private String currentGender;
    private account_Password currentPassword;
    private String currentPayload;
    private TL_auth_sentCode currentPhoneVerification;
    private LinearLayout currentPhotoViewerLayout;
    private String currentPicturePath;
    private String currentPublicKey;
    private String currentResidence;
    private String currentScope;
    private SecureValueType currentType;
    private TL_secureValue currentTypeValue;
    private HashMap<String, String> currentValues;
    private int currentViewNum;
    private PassportActivityDelegate delegate;
    private TextSettingsCell deletePassportCell;
    private ArrayList<View> dividers;
    private ArrayList<SecureDocument> documents;
    private HashMap<SecureDocument, SecureDocumentCell> documentsCells;
    private HashMap<String, String> documentsErrors;
    private LinearLayout documentsLayout;
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
    private HashMap<String, String> fieldsErrors;
    private SecureDocument frontDocument;
    private LinearLayout frontLayout;
    private HeaderCell headerCell;
    private boolean ignoreOnFailure;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private String initialValues;
    private ViewGroup[] inputFieldContainers;
    private EditTextBoldCursor[] inputFields;
    private HashMap<String, String> languageMap;
    private LinearLayout linearLayout2;
    private boolean needActivityResult;
    private CharSequence noAllDocumentsErrorText;
    private ImageView noPasswordImageView;
    private TextView noPasswordSetTextView;
    private TextView noPasswordTextView;
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
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private byte[] secureSecret;
    private long secureSecretId;
    private SecureDocument selfieDocument;
    private LinearLayout selfieLayout;
    private HashMap<SecureValueType, HashMap<String, String>> typesValues;
    private HashMap<SecureValueType, TextDetailSecureCell> typesViews;
    private TextSettingsCell uploadDocumentCell;
    private TextDetailSettingsCell uploadFrontCell;
    private TextDetailSettingsCell uploadReverseCell;
    private TextDetailSettingsCell uploadSelfieCell;
    private HashMap<String, SecureDocument> uploadingDocuments;
    private int uploadingFileType;
    private boolean useCurrentValue;
    private int usingSavedPassword;
    private SlideView[] views;

    /* renamed from: org.telegram.ui.PassportActivity$3 */
    class C16603 implements Runnable {
        C16603() {
        }

        public void run() {
            if (PassportActivity.this.inputFieldContainers != null && PassportActivity.this.inputFieldContainers[0] != null && PassportActivity.this.inputFieldContainers[0].getVisibility() == 0) {
                PassportActivity.this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
            }
        }
    }

    /* renamed from: org.telegram.ui.PassportActivity$7 */
    class C16787 implements OnEditorActionListener {
        C16787() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            PassportActivity.this.doneItem.callOnClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PassportActivity$8 */
    class C16798 implements TextWatcher {
        C16798() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (!PassportActivity.this.ignoreOnTextChange && PassportActivity.this.emailCodeLength != 0 && PassportActivity.this.inputFields[0].length() == PassportActivity.this.emailCodeLength) {
                PassportActivity.this.doneItem.callOnClick();
            }
        }
    }

    /* renamed from: org.telegram.ui.PassportActivity$9 */
    class C16809 implements OnClickListener {
        C16809() {
        }

        public void onClick(View v) {
            TwoStepVerificationActivity activity = new TwoStepVerificationActivity(PassportActivity.this.currentAccount, 1);
            activity.setCloseAfterSet(true);
            activity.setCurrentPasswordInfo(new byte[0], PassportActivity.this.currentPassword);
            PassportActivity.this.presentFragment(activity);
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

    private interface ErrorRunnable {
        void onError(String str, String str2);
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

    private interface PassportActivityDelegate {
        void deleteValue(SecureValueType secureValueType, SecureValueType secureValueType2, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TL_secureFile tL_secureFile);

        void saveValue(SecureValueType secureValueType, String str, String str2, SecureValueType secureValueType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
    }

    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        public TextDetailSecureCell(Context context) {
            super(context);
            int padding = PassportActivity.this.currentActivityType == 8 ? 17 : 47;
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? padding : 17), 10.0f, (float) (LocaleController.isRTL ? 17 : padding), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            View view = this.valueTextView;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            float f = (float) (LocaleController.isRTL ? padding : 17);
            if (LocaleController.isRTL) {
                padding = 17;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f, 35.0f, (float) padding, 0.0f));
            this.checkImageView = new ImageView(context);
            this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
            this.checkImageView.setImageResource(R.drawable.sticker_added);
            addView(this.checkImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 17.0f, 25.0f, 17.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    /* renamed from: org.telegram.ui.PassportActivity$2 */
    class C23992 implements RequestDelegate {
        C23992() {
        }

        public void run(final TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (response != null) {
                        PassportActivity.this.currentPassword = (account_Password) response;
                        byte[] salt = new byte[(PassportActivity.this.currentPassword.new_salt.length + 8)];
                        Utilities.random.nextBytes(salt);
                        System.arraycopy(PassportActivity.this.currentPassword.new_salt, 0, salt, 0, PassportActivity.this.currentPassword.new_salt.length);
                        PassportActivity.this.currentPassword.new_salt = salt;
                        PassportActivity.this.updatePasswordInterface();
                        if (PassportActivity.this.inputFieldContainers[0].getVisibility() == 0) {
                            PassportActivity.this.inputFields[0].requestFocus();
                            AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
                        }
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.PassportActivity$4 */
    class C24064 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.PassportActivity$4$2 */
        class C16612 implements Runnable {
            C16612() {
            }

            public void run() {
                PassportActivity.this.finishFragment();
            }
        }

        C24064() {
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
                    Spannable spannableString = new SpannableString(AndroidUtilities.replaceTags(LocaleController.getString("PassportInfo", R.string.PassportInfo)));
                    URLSpan[] spans = (URLSpan[]) spannableString.getSpans(0, spannableString.length(), URLSpan.class);
                    for (URLSpan span : spans) {
                        int start = spannableString.getSpanStart(span);
                        int end = spannableString.getSpanEnd(span);
                        spannableString.removeSpan(span);
                        spannableString.setSpan(new URLSpanNoUnderline(span.getURL()) {
                            public void onClick(View widget) {
                                PassportActivity.this.dismissCurrentDialig();
                                super.onClick(widget);
                            }
                        }, start, end, 0);
                    }
                    textView.setText(spannableString);
                    textView.setTextSize(1, 16.0f);
                    textView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                    textView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                    textView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                    textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    Builder builder = new Builder(PassportActivity.this.getParentActivity());
                    builder.setView(textView);
                    builder.setTitle(LocaleController.getString("PassportInfoTitle", R.string.PassportInfoTitle));
                    builder.setNegativeButton(LocaleController.getString("Close", R.string.Close), null);
                    PassportActivity.this.showDialog(builder.create());
                }
            } else if (id != 2) {
            } else {
                if (PassportActivity.this.currentActivityType == 5) {
                    PassportActivity.this.onPasswordDone(false);
                    return;
                }
                final Runnable finishRunnable = new C16612();
                final ErrorRunnable errorRunnable = new ErrorRunnable() {
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
                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                } else if (PassportActivity.this.currentActivityType == 3) {
                    if (PassportActivity.this.useCurrentValue) {
                        value = UserConfig.getInstance(PassportActivity.this.currentAccount).getCurrentUser().phone;
                    } else if (!PassportActivity.this.checkFieldsForError()) {
                        value = PassportActivity.this.inputFields[1].getText().toString() + PassportActivity.this.inputFields[2].getText().toString();
                    } else {
                        return;
                    }
                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value, null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                } else if (PassportActivity.this.currentActivityType == 2) {
                    if (PassportActivity.this.uploadingDocuments.isEmpty() && !PassportActivity.this.checkFieldsForError()) {
                        if (PassportActivity.this.isHasNotAnyChanges()) {
                            PassportActivity.this.finishFragment();
                            return;
                        }
                        json = null;
                        try {
                            json = new JSONObject();
                            try {
                                json.put("street_line1", PassportActivity.this.inputFields[0].getText().toString());
                                json.put("street_line2", PassportActivity.this.inputFields[1].getText().toString());
                                json.put("post_code", PassportActivity.this.inputFields[2].getText().toString());
                                json.put("city", PassportActivity.this.inputFields[3].getText().toString());
                                json.put("state", PassportActivity.this.inputFields[4].getText().toString());
                                json.put("country_code", PassportActivity.this.currentCitizeship);
                                json = json;
                            } catch (Exception e) {
                                json = json;
                            }
                        } catch (Exception e2) {
                        }
                        if (PassportActivity.this.fieldsErrors != null) {
                            PassportActivity.this.fieldsErrors.clear();
                        }
                        if (PassportActivity.this.documentsErrors != null) {
                            PassportActivity.this.documentsErrors.clear();
                        }
                        PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, null, json.toString(), PassportActivity.this.currentDocumentsType, null, PassportActivity.this.documents, PassportActivity.this.selfieDocument, null, null, finishRunnable, errorRunnable);
                    } else {
                        return;
                    }
                } else if (PassportActivity.this.currentActivityType == 1) {
                    if (PassportActivity.this.uploadingDocuments.isEmpty() && !PassportActivity.this.checkFieldsForError()) {
                        if (PassportActivity.this.isHasNotAnyChanges()) {
                            PassportActivity.this.finishFragment();
                            return;
                        }
                        String jSONObject;
                        SecureDocument secureDocument;
                        json = null;
                        JSONObject documentsJson = null;
                        try {
                            json = new JSONObject();
                            try {
                                json.put("first_name", PassportActivity.this.inputFields[0].getText().toString());
                                json.put("last_name", PassportActivity.this.inputFields[1].getText().toString());
                                json.put("birth_date", PassportActivity.this.inputFields[2].getText().toString());
                                json.put("gender", PassportActivity.this.currentGender);
                                json.put("country_code", PassportActivity.this.currentCitizeship);
                                json.put("residence_country_code", PassportActivity.this.currentResidence);
                                if (PassportActivity.this.currentDocumentsType != null) {
                                    JSONObject documentsJson2 = new JSONObject();
                                    try {
                                        documentsJson2.put("document_no", PassportActivity.this.inputFields[6].getText().toString());
                                        if (PassportActivity.this.currentExpireDate[0] != 0) {
                                            documentsJson2.put("expiry_date", String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(PassportActivity.this.currentExpireDate[2]), Integer.valueOf(PassportActivity.this.currentExpireDate[1]), Integer.valueOf(PassportActivity.this.currentExpireDate[0])}));
                                            documentsJson = documentsJson2;
                                        } else {
                                            documentsJson2.put("expiry_date", TtmlNode.ANONYMOUS_REGION_ID);
                                            documentsJson = documentsJson2;
                                        }
                                    } catch (Exception e3) {
                                        documentsJson = documentsJson2;
                                        json = json;
                                    }
                                }
                                json = json;
                            } catch (Exception e4) {
                                json = json;
                            }
                        } catch (Exception e5) {
                        }
                        if (PassportActivity.this.fieldsErrors != null) {
                            PassportActivity.this.fieldsErrors.clear();
                        }
                        if (PassportActivity.this.documentsErrors != null) {
                            PassportActivity.this.documentsErrors.clear();
                        }
                        PassportActivityDelegate access$2600 = PassportActivity.this.delegate;
                        SecureValueType access$3200 = PassportActivity.this.currentType;
                        String jSONObject2 = json.toString();
                        SecureValueType access$3800 = PassportActivity.this.currentDocumentsType;
                        if (documentsJson != null) {
                            jSONObject = documentsJson.toString();
                        } else {
                            jSONObject = null;
                        }
                        SecureDocument access$300 = PassportActivity.this.selfieDocument;
                        SecureDocument access$400 = PassportActivity.this.frontDocument;
                        if (PassportActivity.this.reverseLayout == null || PassportActivity.this.reverseLayout.getVisibility() != 0) {
                            secureDocument = null;
                        } else {
                            secureDocument = PassportActivity.this.reverseDocument;
                        }
                        access$2600.saveValue(access$3200, null, jSONObject2, access$3800, jSONObject, null, access$300, access$400, secureDocument, finishRunnable, errorRunnable);
                    } else {
                        return;
                    }
                } else if (PassportActivity.this.currentActivityType == 6) {
                    TLObject req = new TL_account_verifyEmail();
                    req.email = (String) PassportActivity.this.currentValues.get("email");
                    req.code = PassportActivity.this.inputFields[0].getText().toString();
                    final TLObject tLObject = req;
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), null, null, null, null, null, null, null, finishRunnable, errorRunnable);
                                        return;
                                    }
                                    AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, tLObject, new Object[0]);
                                    errorRunnable.onError(null, null);
                                }
                            });
                        }
                    }), PassportActivity.this.classGuid);
                } else if (PassportActivity.this.currentActivityType == 7) {
                    PassportActivity.this.views[PassportActivity.this.currentViewNum].onNextPressed();
                }
                PassportActivity.this.showEditDoneProgress(true, true);
            }
        }
    }

    public class PhoneConfirmationView extends SlideView implements NotificationCenterDelegate {
        private EditTextBoldCursor codeField;
        private int codeTime = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private boolean ignoreOnTextChange;
        private double lastCodeTime;
        private double lastCurrentTime;
        private String lastError = TtmlNode.ANONYMOUS_REGION_ID;
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
        private int verificationType;
        private boolean waitingForEvent;

        /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$5 */
        class C16875 extends TimerTask {

            /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$5$1 */
            class C16861 implements Runnable {
                C16861() {
                }

                public void run() {
                    if (PhoneConfirmationView.this.codeTime <= 1000) {
                        PhoneConfirmationView.this.problemText.setVisibility(0);
                        PhoneConfirmationView.this.destroyCodeTimer();
                    }
                }
            }

            C16875() {
            }

            public void run() {
                double currentTime = (double) System.currentTimeMillis();
                PhoneConfirmationView.this.codeTime = (int) (((double) PhoneConfirmationView.this.codeTime) - (currentTime - PhoneConfirmationView.this.lastCodeTime));
                PhoneConfirmationView.this.lastCodeTime = currentTime;
                AndroidUtilities.runOnUIThread(new C16861());
            }
        }

        /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$6 */
        class C16906 extends TimerTask {

            /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$6$1 */
            class C16891 implements Runnable {

                /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$6$1$1 */
                class C24121 implements RequestDelegate {
                    C24121() {
                    }

                    public void run(TLObject response, final TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    PhoneConfirmationView.this.lastError = error.text;
                                }
                            });
                        }
                    }
                }

                C16891() {
                }

                public void run() {
                    if (PhoneConfirmationView.this.time >= 1000) {
                        int seconds = (PhoneConfirmationView.this.time / 1000) - (((PhoneConfirmationView.this.time / 1000) / 60) * 60);
                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3) {
                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                        } else if (PhoneConfirmationView.this.nextType == 2) {
                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
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
                    } else if (PhoneConfirmationView.this.verificationType != 2) {
                    } else {
                        if (PhoneConfirmationView.this.nextType == 4) {
                            PhoneConfirmationView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                            PhoneConfirmationView.this.createCodeTimer();
                            TL_auth_resendCode req = new TL_auth_resendCode();
                            req.phone_number = PhoneConfirmationView.this.phone;
                            req.phone_code_hash = PhoneConfirmationView.this.phoneHash;
                            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C24121(), 2);
                        } else if (PhoneConfirmationView.this.nextType == 3) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            PhoneConfirmationView.this.waitingForEvent = false;
                            PhoneConfirmationView.this.destroyCodeTimer();
                            PhoneConfirmationView.this.resendCode();
                        }
                    }
                }
            }

            C16906() {
            }

            public void run() {
                if (PhoneConfirmationView.this.timeTimer != null) {
                    double currentTime = (double) System.currentTimeMillis();
                    PhoneConfirmationView.this.time = (int) (((double) PhoneConfirmationView.this.time) - (currentTime - PhoneConfirmationView.this.lastCurrentTime));
                    PhoneConfirmationView.this.lastCurrentTime = currentTime;
                    AndroidUtilities.runOnUIThread(new C16891());
                }
            }
        }

        private class ProgressView extends View {
            private Paint paint = new Paint();
            private Paint paint2 = new Paint();
            private float progress;

            public ProgressView(Context context) {
                super(context);
                this.paint.setColor(Theme.getColor(Theme.key_login_progressInner));
                this.paint2.setColor(Theme.getColor(Theme.key_login_progressOuter));
            }

            public void setProgress(float value) {
                this.progress = value;
                invalidate();
            }

            protected void onDraw(Canvas canvas) {
                int start = (int) (((float) getMeasuredWidth()) * this.progress);
                canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
                canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
            }
        }

        /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$8 */
        class C24148 implements RequestDelegate {
            C24148() {
            }

            public void run(TLObject response, TL_error error) {
            }
        }

        public PhoneConfirmationView(Context context, int type) {
            super(context);
            this.verificationType = type;
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            if (this.verificationType == 3) {
                FrameLayout frameLayout = new FrameLayout(context);
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.phone_activate);
                if (LocaleController.isRTL) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            } else {
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            }
            this.codeField = new EditTextBoldCursor(context);
            this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setHint(LocaleController.getString("Code", R.string.Code));
            this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setInputType(3);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            this.codeField.addTextChangedListener(new TextWatcher(PassportActivity.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!PhoneConfirmationView.this.ignoreOnTextChange && PhoneConfirmationView.this.length != 0 && PhoneConfirmationView.this.codeField.length() == PhoneConfirmationView.this.length) {
                        PhoneConfirmationView.this.onNextPressed();
                    }
                }
            });
            this.codeField.setOnEditorActionListener(new OnEditorActionListener(PassportActivity.this) {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    PhoneConfirmationView.this.onNextPressed();
                    return true;
                }
            });
            if (this.verificationType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextSize(1, 14.0f);
            this.timeText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 30, 0, 0));
            if (this.verificationType == 3) {
                this.progressView = new ProgressView(context);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            this.problemText = new TextView(context);
            this.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            this.problemText.setGravity(LocaleController.isRTL ? 5 : 3);
            this.problemText.setTextSize(1, 14.0f);
            this.problemText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            this.problemText.setOnClickListener(new OnClickListener(PassportActivity.this) {
                public void onClick(View v) {
                    if (!PhoneConfirmationView.this.nextPressed) {
                        if (PhoneConfirmationView.this.nextType == 0 || PhoneConfirmationView.this.nextType == 4) {
                            try {
                                PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                                Intent mailer = new Intent("android.intent.action.SEND");
                                mailer.setType("message/rfc822");
                                mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                                mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + PhoneConfirmationView.this.phone);
                                mailer.putExtra("android.intent.extra.TEXT", "Phone: " + PhoneConfirmationView.this.phone + "\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + PhoneConfirmationView.this.lastError);
                                PhoneConfirmationView.this.getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                                return;
                            } catch (Exception e) {
                                AlertsCreator.showSimpleAlert(PassportActivity.this, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                                return;
                            }
                        }
                        PhoneConfirmationView.this.resendCode();
                    }
                }
            });
        }

        private void resendCode() {
            final Bundle params = new Bundle();
            params.putString("phone", this.phone);
            this.nextPressed = true;
            PassportActivity.this.needShowProgress();
            final TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$4$1$1 */
                        class C16841 implements DialogInterface.OnClickListener {
                            C16841() {
                            }

                            public void onClick(DialogInterface dialog, int which) {
                                PhoneConfirmationView.this.onBackPressed();
                                PassportActivity.this.finishFragment();
                            }
                        }

                        public void run() {
                            PhoneConfirmationView.this.nextPressed = false;
                            if (error == null) {
                                PassportActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response, true);
                            } else {
                                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
                                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                                    dialog.setPositiveButtonListener(new C16841());
                                }
                            }
                            PassportActivity.this.needHideProgress();
                        }
                    });
                }
            }, 2);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            int i = 0;
            if (params != null) {
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
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
                int i2 = params.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = params.getInt("nextType");
                this.pattern = params.getString("pattern");
                this.length = params.getInt("length");
                if (this.length != 0) {
                    this.codeField.setFilters(new InputFilter[]{new LengthFilter(this.length)});
                } else {
                    this.codeField.setFilters(new InputFilter[0]);
                }
                if (this.progressView != null) {
                    ProgressView progressView = this.progressView;
                    if (this.nextType != 0) {
                        i2 = 0;
                    } else {
                        i2 = 8;
                    }
                    progressView.setVisibility(i2);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format("+" + this.phone);
                    CharSequence str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (this.verificationType == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                    } else if (this.verificationType == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                    } else if (this.verificationType == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.verificationType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField);
                        this.codeField.requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.verificationType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(1), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(1), Integer.valueOf(0)));
                        }
                        createTimer();
                    } else if (this.verificationType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setVisibility(0);
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        TextView textView = this.problemText;
                        if (this.time >= 1000) {
                            i = 8;
                        }
                        textView.setVisibility(i);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        private void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new C16875(), 0, 1000);
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new C16906(), 0, 1000);
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                if (this.verificationType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.verificationType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                final TL_account_verifyPhone req = new TL_account_verifyPhone();
                req.phone_number = this.phone;
                req.phone_code = this.codeField.getText().toString();
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                PassportActivity.this.needShowProgress();
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$7$1$1 */
                            class C16911 implements Runnable {
                                C16911() {
                                }

                                public void run() {
                                    PassportActivity.this.finishFragment();
                                }
                            }

                            public void run() {
                                PassportActivity.this.needHideProgress();
                                PhoneConfirmationView.this.nextPressed = false;
                                if (error == null) {
                                    PhoneConfirmationView.this.destroyTimer();
                                    PhoneConfirmationView.this.destroyCodeTimer();
                                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("phone"), null, null, null, null, null, null, null, new C16911(), null);
                                    return;
                                }
                                PhoneConfirmationView.this.lastError = error.text;
                                if ((PhoneConfirmationView.this.verificationType == 3 && (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 2)) || (PhoneConfirmationView.this.verificationType == 2 && (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3))) {
                                    PhoneConfirmationView.this.createTimer();
                                }
                                if (PhoneConfirmationView.this.verificationType == 2) {
                                    AndroidUtilities.setWaitingForSms(true);
                                    NotificationCenter.getGlobalInstance().addObserver(PhoneConfirmationView.this, NotificationCenter.didReceiveSmsCode);
                                } else if (PhoneConfirmationView.this.verificationType == 3) {
                                    AndroidUtilities.setWaitingForCall(true);
                                    NotificationCenter.getGlobalInstance().addObserver(PhoneConfirmationView.this, NotificationCenter.didReceiveCall);
                                }
                                PhoneConfirmationView.this.waitingForEvent = true;
                                if (PhoneConfirmationView.this.verificationType != 3) {
                                    AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, new Object[0]);
                                }
                            }
                        });
                    }
                }, 2);
            }
        }

        public void onBackPressed() {
            TL_auth_cancelCode req = new TL_auth_cancelCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C24148(), 2);
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
            if (this.codeField != null && this.codeField.getVisibility() == 0) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
                AndroidUtilities.showKeyboard(this.codeField);
            }
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.waitingForEvent && this.codeField != null) {
                if (id == NotificationCenter.didReceiveSmsCode) {
                    this.ignoreOnTextChange = true;
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID + args[0]);
                    this.ignoreOnTextChange = false;
                    onNextPressed();
                } else if (id == NotificationCenter.didReceiveCall) {
                    String num = TtmlNode.ANONYMOUS_REGION_ID + args[0];
                    if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                        this.ignoreOnTextChange = true;
                        this.codeField.setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
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
        final /* synthetic */ PassportActivity this$0;
        private TextView valueTextView;

        public SecureDocumentCell(PassportActivity this$0, Context context) {
            int i;
            int i2;
            int i3 = 17;
            int i4 = 5;
            this.this$0 = this$0;
            super(context);
            this.TAG = DownloadController.getInstance(this$0.currentAccount).generateObserverTag();
            this.imageView = new BackupImageView(context);
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 8.0f, 17.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
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
            View view = this.textView;
            if (LocaleController.isRTL) {
                i2 = 5;
            } else {
                i2 = 3;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i2 | 48, (float) (LocaleController.isRTL ? 17 : 77), 10.0f, (float) (LocaleController.isRTL ? 77 : 17), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
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
            view = this.valueTextView;
            if (!LocaleController.isRTL) {
                i4 = 3;
            }
            i2 = i4 | 48;
            float f = (float) (LocaleController.isRTL ? 17 : 77);
            if (LocaleController.isRTL) {
                i3 = 77;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i2, f, 35.0f, (float) i3, 0.0f));
            setWillNotDraw(false);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, NUM));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int x = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            int y = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
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
                r5 = this.radialProgress;
                if (progress != null) {
                    f = progress.floatValue();
                }
                r5.setProgress(f, false);
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
                r5 = this.radialProgress;
                if (progress != null) {
                    f = progress.floatValue();
                }
                r5.setProgress(f, animated);
                invalidate();
            }
        }

        public void invalidate() {
            super.invalidate();
            this.textView.invalidate();
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }

        public void onFailedDownload(String fileName) {
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

    /* renamed from: org.telegram.ui.PassportActivity$1 */
    class C25701 extends EmptyPhotoViewerProvider {
        C25701() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
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
                return LocaleController.formatString("PassportDeleteSelfieAlert", R.string.PassportDeleteSelfieAlert, new Object[0]);
            }
            return LocaleController.formatString("PassportDeleteScanAlert", R.string.PassportDeleteScanAlert, new Object[0]);
        }
    }

    public PassportActivity(int type, int botId, String scope, String publicKey, String payload, String callbackUrl, TL_account_authorizationForm form, account_Password accountPassword) {
        this(type, form, accountPassword, null, null, null, null, null);
        this.currentBotId = botId;
        this.currentPayload = payload;
        this.currentScope = scope;
        this.currentPublicKey = publicKey;
        this.currentCallbackUrl = callbackUrl;
        if (type == 0 && !form.errors.isEmpty()) {
            try {
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
                                target = DataSchemeDataSource.SCHEME_DATA;
                            }
                        }
                    }
                    HashMap<String, String> vals = (HashMap) this.errorsMap.get(key);
                    if (vals == null) {
                        vals = new HashMap();
                        this.errorsMap.put(key, vals);
                    }
                    if (file_hash != null) {
                        hash = Base64.encodeToString(file_hash, 2);
                    } else {
                        hash = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (DataSchemeDataSource.SCHEME_DATA.equals(target)) {
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
                    } else if ("front".equals(target)) {
                        vals.put("front" + hash, description);
                    } else if ("reverse".equals(target)) {
                        vals.put("reverse" + hash, description);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public PassportActivity(int type, TL_account_authorizationForm form, account_Password accountPassword, SecureValueType secureType, TL_secureValue secureValue, SecureValueType secureDocumentsType, TL_secureValue secureDocumentsValue, HashMap<String, String> values) {
        this.currentCitizeship = TtmlNode.ANONYMOUS_REGION_ID;
        this.currentResidence = TtmlNode.ANONYMOUS_REGION_ID;
        this.currentExpireDate = new int[3];
        this.dividers = new ArrayList();
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
        this.documents = new ArrayList();
        this.documentsCells = new HashMap();
        this.uploadingDocuments = new HashMap();
        this.typesValues = new HashMap();
        this.typesViews = new HashMap();
        this.errorsMap = new HashMap();
        this.errorsValues = new HashMap();
        this.provider = new C25701();
        this.currentActivityType = type;
        this.currentForm = form;
        this.currentType = secureType;
        this.currentTypeValue = secureValue;
        this.currentDocumentsType = secureDocumentsType;
        this.currentDocumentsTypeValue = secureDocumentsValue;
        this.currentPassword = accountPassword;
        this.currentValues = values;
        if (this.currentActivityType == 3) {
            this.permissionsItems = new ArrayList();
        } else if (this.currentActivityType == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap();
        }
        if (type == 5) {
            if (!(UserConfig.getInstance(this.currentAccount).savedPasswordHash == null || UserConfig.getInstance(this.currentAccount).savedSaltedPassword == null)) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            if (this.currentPassword == null) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new C23992()), this.classGuid);
            } else {
                byte[] salt = new byte[(this.currentPassword.new_salt.length + 8)];
                Utilities.random.nextBytes(salt);
                System.arraycopy(this.currentPassword.new_salt, 0, salt, 0, this.currentPassword.new_salt.length);
                this.currentPassword.new_salt = salt;
            }
            if (this.usingSavedPassword == 1) {
                onPasswordDone(true);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onResume();
        }
        if (this.currentActivityType == 5 && this.inputFieldContainers != null && this.inputFieldContainers[0] != null && this.inputFieldContainers[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
            AndroidUtilities.runOnUIThread(new C16603(), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemovedTwoStepPassword);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemovedTwoStepPassword);
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
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.progressDialog = null;
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C24064());
        if (this.currentActivityType == 7) {
            View c16745 = new ScrollView(context) {
                protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }
            };
            this.scrollView = c16745;
            this.fragmentView = c16745;
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        } else {
            float f;
            this.fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = this.fragmentView;
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.scrollView = new ScrollView(context) {
                protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
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
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
            View view = this.scrollView;
            if (this.currentActivityType == 0) {
                f = 48.0f;
            } else {
                f = 0.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, f));
            this.linearLayout2 = new LinearLayout(context);
            this.linearLayout2.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        }
        if (!(this.currentActivityType == 0 || this.currentActivityType == 8)) {
            this.doneItem = this.actionBar.createMenu().addItemWithWidth(2, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            this.progressView = new ContextProgressView(context, 1);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setVisibility(4);
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

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", R.string.PassportPhone));
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

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(3);
            this.inputFields[a].setImeOptions(268435462);
            switch (a) {
                case 0:
                    this.inputFields[a].setHint(LocaleController.getString("PassportEmailCode", R.string.PassportEmailCode));
                    break;
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new C16787());
            this.inputFields[a].addTextChangedListener(new C16798());
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.formatString("PassportEmailVerifyInfo", R.string.PassportEmailVerifyInfo, this.currentValues.get("email")));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    private void createPasswordInterface(Context context) {
        int a;
        User botUser = null;
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
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", R.string.TelegramPassport));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.passwordAvatarContainer = new FrameLayout(context);
        this.linearLayout2.addView(this.passwordAvatarContainer, LayoutHelper.createLinear(-1, 100));
        BackupImageView avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(avatarImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        Drawable avatarDrawable = new AvatarDrawable(botUser);
        TLObject photo = null;
        if (botUser.photo != null) {
            photo = botUser.photo.photo_small;
        }
        avatarImageView.setImage(photo, "50_50", avatarDrawable);
        this.passwordRequestTextView = new TextInfoPrivacyCell(context);
        this.passwordRequestTextView.getTextView().setGravity(1);
        this.passwordRequestTextView.setText(LocaleController.getString("PassportSelfRequest", R.string.PassportSelfRequest));
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.noPasswordImageView = new ImageView(context);
        this.noPasswordImageView.setImageResource(R.drawable.no_password);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        this.noPasswordTextView = new TextView(context);
        this.noPasswordTextView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.noPasswordTextView.setText(LocaleController.getString("TelegramPassportCreatePasswordInfo", R.string.TelegramPassportCreatePasswordInfo));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 10.0f, 17.0f, 0.0f));
        this.noPasswordSetTextView = new TextView(context);
        this.noPasswordSetTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.noPasswordSetTextView.setText(LocaleController.getString("TelegramPassportCreatePassword", R.string.TelegramPassportCreatePassword));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 9.0f, 17.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new C16809());
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = new ViewGroup[1];
        for (a = 0; a < 1; a++) {
            this.inputFieldContainers[a] = new FrameLayout(context);
            this.linearLayout2.addView(this.inputFieldContainers[a], LayoutHelper.createLinear(-1, 48));
            this.inputFieldContainers[a].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
            this.inputFields[a].setMaxLines(1);
            this.inputFields[a].setLines(1);
            this.inputFields[a].setSingleLine(true);
            this.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.inputFields[a].setTypeface(Typeface.DEFAULT);
            this.inputFields[a].setImeOptions(268435462);
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            this.inputFieldContainers[a].addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5 && i != 6) {
                        return false;
                    }
                    PassportActivity.this.doneItem.callOnClick();
                    return true;
                }
            });
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
        this.passwordInfoRequestTextView.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", R.string.PassportRequestPasswordInfo, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        this.passwordForgotButton = new TextView(context);
        this.passwordForgotButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        this.linearLayout2.addView(this.passwordForgotButton, LayoutHelper.createLinear(-2, 30, (LocaleController.isRTL ? 5 : 3) | 48, 17, 0, 17, 0));
        this.passwordForgotButton.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.PassportActivity$12$2 */
            class C16502 implements DialogInterface.OnClickListener {
                C16502() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    Browser.openUrl(PassportActivity.this.getParentActivity(), "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(PassportActivity.this.currentAccount).getClientPhone());
                }
            }

            /* renamed from: org.telegram.ui.PassportActivity$12$1 */
            class C23911 implements RequestDelegate {
                C23911() {
                }

                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            PassportActivity.this.needHideProgress();
                            if (error == null) {
                                final TL_auth_passwordRecovery res = response;
                                Builder builder = new Builder(PassportActivity.this.getParentActivity());
                                builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(PassportActivity.this.currentAccount, 1);
                                        fragment.setRecoveryParams(PassportActivity.this.currentPassword);
                                        PassportActivity.this.currentPassword.email_unconfirmed_pattern = res.email_pattern;
                                        PassportActivity.this.presentFragment(fragment);
                                    }
                                });
                                Dialog dialog = PassportActivity.this.showDialog(builder.create());
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
                                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                            } else {
                                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }
                    });
                }
            }

            public void onClick(View v) {
                if (PassportActivity.this.currentPassword.has_recovery) {
                    PassportActivity.this.needShowProgress();
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new C23911(), 10), PassportActivity.this.classGuid);
                } else if (PassportActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(PassportActivity.this.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", R.string.RestorePasswordResetAccount), new C16502());
                    builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                    builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                    PassportActivity.this.showDialog(builder.create());
                }
            }
        });
        updatePasswordInterface();
    }

    private void onPasswordDone(final boolean saved) {
        byte[] currentPasswordHash;
        String textPassword;
        if (saved) {
            currentPasswordHash = this.savedPasswordHash;
            textPassword = null;
        } else {
            textPassword = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(textPassword)) {
                onPasscodeError(false);
                return;
            }
            showEditDoneProgress(true, true);
            byte[] passwordBytes = AndroidUtilities.getStringBytes(textPassword);
            byte[] hash = new byte[((this.currentPassword.current_salt.length * 2) + passwordBytes.length)];
            System.arraycopy(this.currentPassword.current_salt, 0, hash, 0, this.currentPassword.current_salt.length);
            System.arraycopy(passwordBytes, 0, hash, this.currentPassword.current_salt.length, passwordBytes.length);
            System.arraycopy(this.currentPassword.current_salt, 0, hash, hash.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
            currentPasswordHash = Utilities.computeSHA256(hash, 0, hash.length);
        }
        RequestDelegate requestDelegate = new RequestDelegate() {

            /* renamed from: org.telegram.ui.PassportActivity$13$1 */
            class C23921 implements RequestDelegate {

                /* renamed from: org.telegram.ui.PassportActivity$13$1$1 */
                class C16511 implements Runnable {
                    C16511() {
                    }

                    public void run() {
                        AnonymousClass13.this.generateNewSecret();
                    }
                }

                C23921() {
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C16511());
                }
            }

            /* renamed from: org.telegram.ui.PassportActivity$13$2 */
            class C23932 implements RequestDelegate {

                /* renamed from: org.telegram.ui.PassportActivity$13$2$1 */
                class C16521 implements Runnable {
                    C16521() {
                    }

                    public void run() {
                        if (PassportActivity.this.currentForm == null) {
                            PassportActivity.this.currentForm = new TL_account_authorizationForm();
                            PassportActivity.this.currentForm.selfie_required = true;
                        }
                        AnonymousClass13.this.openRequestInterface();
                    }
                }

                C23932() {
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C16521());
                }
            }

            private void openRequestInterface() {
                int type;
                if (!saved) {
                    UserConfig.getInstance(PassportActivity.this.currentAccount).savePassword(currentPasswordHash, PassportActivity.this.saltedPassword);
                }
                AndroidUtilities.hideKeyboard(PassportActivity.this.inputFields[0]);
                PassportActivity.this.ignoreOnFailure = true;
                if (PassportActivity.this.currentBotId == 0) {
                    type = 8;
                } else {
                    type = 0;
                }
                PassportActivity activity = new PassportActivity(type, PassportActivity.this.currentBotId, PassportActivity.this.currentScope, PassportActivity.this.currentPublicKey, PassportActivity.this.currentPayload, PassportActivity.this.currentCallbackUrl, PassportActivity.this.currentForm, PassportActivity.this.currentPassword);
                activity.currentEmail = PassportActivity.this.currentEmail;
                activity.currentAccount = PassportActivity.this.currentAccount;
                activity.saltedPassword = PassportActivity.this.saltedPassword;
                activity.secureSecret = PassportActivity.this.secureSecret;
                activity.secureSecretId = PassportActivity.this.secureSecretId;
                activity.needActivityResult = PassportActivity.this.needActivityResult;
                if (PassportActivity.this.parentLayout.checkTransitionAnimation()) {
                    PassportActivity.this.presentAfterAnimation = activity;
                } else {
                    PassportActivity.this.presentFragment(activity, true);
                }
            }

            private void resetSecret() {
                TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
                req.current_password_hash = currentPasswordHash;
                req.new_settings = new TL_account_passwordInputSettings();
                req.new_settings.new_secure_secret = new byte[0];
                req.new_settings.new_secure_salt = new byte[0];
                req.new_settings.new_secure_secret_id = 0;
                TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 4;
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C23921());
            }

            private void generateNewSecret() {
                Utilities.random.setSeed(PassportActivity.this.currentPassword.secure_random);
                byte[] secureSalt = new byte[(PassportActivity.this.currentPassword.new_secure_salt.length + 8)];
                Utilities.random.nextBytes(secureSalt);
                System.arraycopy(PassportActivity.this.currentPassword.new_secure_salt, 0, secureSalt, 0, PassportActivity.this.currentPassword.new_secure_salt.length);
                PassportActivity.this.saltedPassword = Utilities.computeSHA512(secureSalt, AndroidUtilities.getStringBytes(textPassword), secureSalt);
                byte[] key = new byte[32];
                System.arraycopy(PassportActivity.this.saltedPassword, 0, key, 0, 32);
                byte[] iv = new byte[16];
                System.arraycopy(PassportActivity.this.saltedPassword, 32, iv, 0, 16);
                PassportActivity.this.secureSecret = PassportActivity.this.getRandomSecret();
                PassportActivity.this.secureSecretId = Utilities.bytesToLong(Utilities.computeSHA256(PassportActivity.this.secureSecret));
                Utilities.aesCbcEncryptionByteArraySafe(PassportActivity.this.secureSecret, key, iv, 0, PassportActivity.this.secureSecret.length, 0, 1);
                TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
                req.current_password_hash = currentPasswordHash;
                req.new_settings = new TL_account_passwordInputSettings();
                req.new_settings.new_secure_secret = PassportActivity.this.secureSecret;
                req.new_settings.new_secure_salt = secureSalt;
                req.new_settings.new_secure_secret_id = PassportActivity.this.secureSecretId;
                TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 4;
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C23932());
            }

            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.ui.PassportActivity$13$3$1 */
                    class C23941 implements RequestDelegate {
                        C23941() {
                        }

                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (response != null) {
                                        PassportActivity.this.currentForm = new TL_account_authorizationForm();
                                        PassportActivity.this.currentForm.selfie_required = true;
                                        Vector vector = response;
                                        int size = vector.objects.size();
                                        for (int a = 0; a < size; a++) {
                                            PassportActivity.this.currentForm.values.add((TL_secureValue) vector.objects.get(a));
                                        }
                                        AnonymousClass13.this.openRequestInterface();
                                        return;
                                    }
                                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            });
                        }
                    }

                    public void run() {
                        if (error == null) {
                            TL_account_passwordSettings settings = response;
                            PassportActivity.this.secureSecret = settings.secure_secret;
                            PassportActivity.this.secureSecretId = settings.secure_secret_id;
                            PassportActivity.this.currentEmail = settings.email;
                            if (saved) {
                                PassportActivity.this.saltedPassword = PassportActivity.this.savedSaltedPassword;
                            } else {
                                PassportActivity.this.saltedPassword = Utilities.computeSHA512(settings.secure_salt, AndroidUtilities.getStringBytes(textPassword), settings.secure_salt);
                            }
                            if (!PassportActivity.this.checkSecret(PassportActivity.this.decryptSecret(PassportActivity.this.secureSecret, PassportActivity.this.saltedPassword), Long.valueOf(PassportActivity.this.secureSecretId)) || settings.secure_salt.length == 0 || PassportActivity.this.secureSecretId == 0) {
                                if (saved) {
                                    UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                                    PassportActivity.this.usingSavedPassword = 0;
                                    PassportActivity.this.updatePasswordInterface();
                                } else if (PassportActivity.this.secureSecret == null || PassportActivity.this.secureSecret.length == 0) {
                                    AnonymousClass13.this.generateNewSecret();
                                } else {
                                    AnonymousClass13.this.resetSecret();
                                }
                            } else if (PassportActivity.this.currentBotId == 0) {
                                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TL_account_getAllSecureValues(), new C23941());
                            } else {
                                AnonymousClass13.this.openRequestInterface();
                            }
                        } else if (saved) {
                            UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                            PassportActivity.this.usingSavedPassword = 0;
                            PassportActivity.this.updatePasswordInterface();
                            if (PassportActivity.this.inputFieldContainers[0].getVisibility() == 0) {
                                PassportActivity.this.inputFields[0].requestFocus();
                                AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
                            }
                        } else {
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
                                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                            } else {
                                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }
                    }
                });
            }
        };
        TL_account_getPasswordSettings req = new TL_account_getPasswordSettings();
        req.current_password_hash = currentPasswordHash;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10), this.classGuid);
    }

    private void createRequestInterface(Context context) {
        int a;
        User botUser = null;
        if (this.currentForm != null) {
            for (a = 0; a < this.currentForm.users.size(); a++) {
                User user = (User) this.currentForm.users.get(a);
                if (user.id == this.currentBotId) {
                    botUser = user;
                    break;
                }
            }
        }
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", R.string.TelegramPassport));
        this.actionBar.createMenu().addItem(1, (int) R.drawable.profile_info);
        if (botUser != null) {
            FrameLayout avatarContainer = new FrameLayout(context);
            this.linearLayout2.addView(avatarContainer, LayoutHelper.createLinear(-1, 100));
            BackupImageView avatarImageView = new BackupImageView(context);
            avatarImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            avatarContainer.addView(avatarImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
            Drawable avatarDrawable = new AvatarDrawable(botUser);
            TLObject photo = null;
            if (botUser.photo != null) {
                photo = botUser.photo.photo_small;
            }
            avatarImageView.setImage(photo, "50_50", avatarDrawable);
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", R.string.PassportRequest, UserObject.getFirstName(botUser))));
            this.bottomCell.getTextView().setGravity(1);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportRequestedInformation", R.string.PassportRequestedInformation));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        if (this.currentForm != null) {
            int size = this.currentForm.required_types.size();
            for (a = 0; a < size; a++) {
                ArrayList<SecureValueType> documentTypes;
                boolean z;
                SecureValueType type = (SecureValueType) this.currentForm.required_types.get(a);
                if ((type instanceof TL_secureValueTypePhone) || (type instanceof TL_secureValueTypeEmail)) {
                    documentTypes = null;
                } else if (type instanceof TL_secureValueTypePersonalDetails) {
                    documentTypes = new ArrayList();
                    for (b = 0; b < size; b++) {
                        innerType = (SecureValueType) this.currentForm.required_types.get(b);
                        if ((innerType instanceof TL_secureValueTypeDriverLicense) || (innerType instanceof TL_secureValueTypePassport) || (innerType instanceof TL_secureValueTypeInternalPassport) || (innerType instanceof TL_secureValueTypeIdentityCard)) {
                            documentTypes.add(innerType);
                        }
                    }
                } else if (type instanceof TL_secureValueTypeAddress) {
                    documentTypes = new ArrayList();
                    for (b = 0; b < size; b++) {
                        innerType = (SecureValueType) this.currentForm.required_types.get(b);
                        if ((innerType instanceof TL_secureValueTypeUtilityBill) || (innerType instanceof TL_secureValueTypeBankStatement) || (innerType instanceof TL_secureValueTypePassportRegistration) || (innerType instanceof TL_secureValueTypeTemporaryRegistration) || (innerType instanceof TL_secureValueTypeRentalAgreement)) {
                            documentTypes.add(innerType);
                        }
                    }
                } else {
                }
                SecureValueType secureValueType = (SecureValueType) this.currentForm.required_types.get(a);
                if (a == size - 1) {
                    z = true;
                } else {
                    z = false;
                }
                addField(context, secureValueType, documentTypes, z);
            }
        }
        if (botUser != null) {
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setLinkTextColorKey(Theme.key_windowBackgroundWhiteGrayText4);
            if (TextUtils.isEmpty(this.currentForm.privacy_policy_url)) {
                this.bottomCell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportNoPolicy", R.string.PassportNoPolicy, UserObject.getFirstName(botUser), botUser.username)));
            } else {
                String str2 = LocaleController.formatString("PassportPolicy", R.string.PassportPolicy, UserObject.getFirstName(botUser), botUser.username);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str2);
                int index1 = str2.indexOf(42);
                int index2 = str2.lastIndexOf(42);
                if (!(index1 == -1 || index2 == -1)) {
                    this.bottomCell.getTextView().setMovementMethod(new LinkMovementMethodMy());
                    spannableStringBuilder.replace(index2, index2 + 1, TtmlNode.ANONYMOUS_REGION_ID);
                    spannableStringBuilder.replace(index1, index1 + 1, TtmlNode.ANONYMOUS_REGION_ID);
                    spannableStringBuilder.setSpan(new LinkSpan(), index1, index2 - 1, 33);
                }
                this.bottomCell.setText(spannableStringBuilder);
            }
            this.bottomCell.getTextView().setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            this.bottomCell.getTextView().setGravity(1);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.bottomLayout = new FrameLayout(context);
        this.bottomLayout.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(Theme.key_passport_authorizeBackground), Theme.getColor(Theme.key_passport_authorizeBackgroundSelected)));
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.PassportActivity$14$1 */
            class C23951 implements RequestDelegate {
                C23951() {
                }

                public void run(TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                PassportActivity.this.ignoreOnFailure = true;
                                PassportActivity.this.callCallback(true);
                                PassportActivity.this.finishFragment();
                                return;
                            }
                            PassportActivity.this.showEditDoneProgress(false, false);
                            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                                AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                            } else {
                                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }
                    });
                }
            }

            public void onClick(View view) {
                JSONObject result;
                ArrayList<TL_secureValue> valuesToSend = new ArrayList();
                int a = 0;
                int size = PassportActivity.this.currentForm.required_types.size();
                while (a < size) {
                    int b;
                    SecureValueType type = (SecureValueType) PassportActivity.this.currentForm.required_types.get(a);
                    SecureValueType innerType = null;
                    boolean needDocuments = false;
                    TL_secureValue documentValue = null;
                    if ((type instanceof TL_secureValueTypePhone) || (type instanceof TL_secureValueTypeEmail)) {
                        needDocuments = false;
                    } else if (type instanceof TL_secureValueTypePersonalDetails) {
                        for (b = 0; b < size; b++) {
                            innerType = (SecureValueType) PassportActivity.this.currentForm.required_types.get(b);
                            if ((innerType instanceof TL_secureValueTypeDriverLicense) || (innerType instanceof TL_secureValueTypePassport) || (innerType instanceof TL_secureValueTypeInternalPassport) || (innerType instanceof TL_secureValueTypeIdentityCard)) {
                                needDocuments = true;
                                documentValue = PassportActivity.this.getValueByType(innerType, true);
                                if (documentValue != null) {
                                    break;
                                }
                            }
                        }
                    } else if (type instanceof TL_secureValueTypeAddress) {
                        for (b = 0; b < size; b++) {
                            innerType = (SecureValueType) PassportActivity.this.currentForm.required_types.get(b);
                            if ((innerType instanceof TL_secureValueTypeUtilityBill) || (innerType instanceof TL_secureValueTypeBankStatement) || (innerType instanceof TL_secureValueTypePassportRegistration) || (innerType instanceof TL_secureValueTypeTemporaryRegistration) || (innerType instanceof TL_secureValueTypeRentalAgreement)) {
                                needDocuments = true;
                                documentValue = PassportActivity.this.getValueByType(innerType, true);
                                if (documentValue != null) {
                                    break;
                                }
                            }
                        }
                    } else {
                        continue;
                        a++;
                    }
                    TL_secureValue value = PassportActivity.this.getValueByType(type, true);
                    Vibrator v;
                    if (value == null || (documentValue == null && needDocuments)) {
                        v = (Vibrator) PassportActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200);
                        }
                        AndroidUtilities.shakeView((View) PassportActivity.this.typesViews.get(type), 2.0f, 0);
                        return;
                    }
                    String key = PassportActivity.this.getNameForType(type);
                    String key2 = innerType != null ? PassportActivity.this.getNameForType(innerType) : null;
                    HashMap<String, String> errors = (HashMap) PassportActivity.this.errorsMap.get(key);
                    HashMap<String, String> errors2 = key2 != null ? (HashMap) PassportActivity.this.errorsMap.get(key2) : null;
                    if ((errors == null || errors.isEmpty()) && (errors2 == null || errors2.isEmpty())) {
                        valuesToSend.add(value);
                        if (documentValue != null) {
                            valuesToSend.add(documentValue);
                        }
                        a++;
                    } else {
                        v = (Vibrator) PassportActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200);
                        }
                        AndroidUtilities.shakeView((View) PassportActivity.this.typesViews.get(type), 2.0f, 0);
                        return;
                    }
                }
                PassportActivity.this.showEditDoneProgress(false, true);
                TLObject req = new TL_account_acceptAuthorization();
                req.bot_id = PassportActivity.this.currentBotId;
                req.scope = PassportActivity.this.currentScope;
                req.public_key = PassportActivity.this.currentPublicKey;
                JSONObject jsonObject = new JSONObject();
                size = valuesToSend.size();
                for (a = 0; a < size; a++) {
                    TL_secureValue secureValue = (TL_secureValue) valuesToSend.get(a);
                    JSONObject data = new JSONObject();
                    if (secureValue.plain_data == null) {
                        try {
                            byte[] decryptedSecret;
                            TL_secureFile secureFile;
                            result = new JSONObject();
                            if (secureValue.data != null) {
                                decryptedSecret = PassportActivity.this.decryptValueSecret(secureValue.data.secret, secureValue.data.data_hash);
                                data.put("data_hash", Base64.encodeToString(secureValue.data.data_hash, 2));
                                data.put("secret", Base64.encodeToString(decryptedSecret, 2));
                                result.put(DataSchemeDataSource.SCHEME_DATA, data);
                            }
                            if (!secureValue.files.isEmpty()) {
                                JSONArray files = new JSONArray();
                                int size2 = secureValue.files.size();
                                for (b = 0; b < size2; b++) {
                                    secureFile = (TL_secureFile) secureValue.files.get(b);
                                    decryptedSecret = PassportActivity.this.decryptValueSecret(secureFile.secret, secureFile.file_hash);
                                    JSONObject file = new JSONObject();
                                    file.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                                    file.put("secret", Base64.encodeToString(decryptedSecret, 2));
                                    files.put(file);
                                }
                                result.put("files", files);
                            }
                            if (secureValue.front_side instanceof TL_secureFile) {
                                secureFile = (TL_secureFile) secureValue.front_side;
                                decryptedSecret = PassportActivity.this.decryptValueSecret(secureFile.secret, secureFile.file_hash);
                                JSONObject front = new JSONObject();
                                front.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                                front.put("secret", Base64.encodeToString(decryptedSecret, 2));
                                result.put("front_side", front);
                            }
                            if (secureValue.reverse_side instanceof TL_secureFile) {
                                secureFile = (TL_secureFile) secureValue.reverse_side;
                                decryptedSecret = PassportActivity.this.decryptValueSecret(secureFile.secret, secureFile.file_hash);
                                JSONObject reverse = new JSONObject();
                                reverse.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                                reverse.put("secret", Base64.encodeToString(decryptedSecret, 2));
                                result.put("reverse_side", reverse);
                            }
                            if (PassportActivity.this.currentForm.selfie_required && (secureValue.selfie instanceof TL_secureFile)) {
                                secureFile = (TL_secureFile) secureValue.selfie;
                                decryptedSecret = PassportActivity.this.decryptValueSecret(secureFile.secret, secureFile.file_hash);
                                JSONObject selfie = new JSONObject();
                                selfie.put("file_hash", Base64.encodeToString(secureFile.file_hash, 2));
                                selfie.put("secret", Base64.encodeToString(decryptedSecret, 2));
                                result.put("selfie", selfie);
                            }
                            jsonObject.put(PassportActivity.this.getNameForType(secureValue.type), result);
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
                if (PassportActivity.this.currentPayload != null) {
                    try {
                        result.put("payload", PassportActivity.this.currentPayload);
                    } catch (Exception e3) {
                    }
                }
                EncryptionResult encryptionResult = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(result.toString()));
                req.credentials = new TL_secureCredentialsEncrypted();
                req.credentials.hash = encryptionResult.fileHash;
                req.credentials.data = encryptionResult.encryptedData;
                try {
                    RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(PassportActivity.this.currentPublicKey.replaceAll("\\n", TtmlNode.ANONYMOUS_REGION_ID).replace("-----BEGIN PUBLIC KEY-----", TtmlNode.ANONYMOUS_REGION_ID).replace("-----END PUBLIC KEY-----", TtmlNode.ANONYMOUS_REGION_ID), 0)));
                    Cipher c = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
                    c.init(1, pubKey);
                    req.credentials.secret = c.doFinal(encryptionResult.decrypyedFileSecret);
                } catch (Throwable e4) {
                    FileLog.m3e(e4);
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C23951()), PassportActivity.this.classGuid);
            }
        });
        this.acceptTextView = new TextView(context);
        this.acceptTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.acceptTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.authorize, 0, 0, 0);
        this.acceptTextView.setTextColor(Theme.getColor(Theme.key_passport_authorizeText));
        this.acceptTextView.setText(LocaleController.getString("PassportAuthorize", R.string.PassportAuthorize));
        this.acceptTextView.setTextSize(1, 14.0f);
        this.acceptTextView.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomLayout.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -1, 17));
        this.progressViewButton = new ContextProgressView(context, 0);
        this.progressViewButton.setVisibility(4);
        this.bottomLayout.addView(this.progressViewButton, LayoutHelper.createFrame(-1, -1.0f));
        View view = new View(context);
        view.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
    }

    private void createManageInterface(Context context) {
        FrameLayout frameLayout = this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", R.string.TelegramPassport));
        this.actionBar.createMenu().addItem(1, (int) R.drawable.profile_info);
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportProvidedInformation", R.string.PassportProvidedInformation));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.sectionCell = new ShadowSectionCell(context);
        this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        this.addDocumentCell = new TextSettingsCell(context);
        this.addDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.addDocumentCell.setText(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd), true);
        this.linearLayout2.addView(this.addDocumentCell, LayoutHelper.createLinear(-1, -2));
        this.addDocumentCell.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PassportActivity.this.openAddDocumentAlert();
            }
        });
        this.deletePassportCell = new TextSettingsCell(context);
        this.deletePassportCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
        this.deletePassportCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.deletePassportCell.setText(LocaleController.getString("TelegramPassportDelete", R.string.TelegramPassportDelete), false);
        this.linearLayout2.addView(this.deletePassportCell, LayoutHelper.createLinear(-1, -2));
        this.deletePassportCell.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.PassportActivity$16$1 */
            class C16571 implements DialogInterface.OnClickListener {

                /* renamed from: org.telegram.ui.PassportActivity$16$1$1 */
                class C23961 implements RequestDelegate {

                    /* renamed from: org.telegram.ui.PassportActivity$16$1$1$1 */
                    class C16561 implements Runnable {
                        C16561() {
                        }

                        public void run() {
                            int a = 0;
                            while (a < PassportActivity.this.linearLayout2.getChildCount()) {
                                View child = PassportActivity.this.linearLayout2.getChildAt(a);
                                if (child instanceof TextDetailSecureCell) {
                                    PassportActivity.this.linearLayout2.removeView(child);
                                    a--;
                                }
                                a++;
                            }
                            PassportActivity.this.needHideProgress();
                            PassportActivity.this.typesViews.clear();
                            PassportActivity.this.typesValues.clear();
                            PassportActivity.this.currentForm.values.clear();
                            PassportActivity.this.updateManageVisibility();
                        }
                    }

                    C23961() {
                    }

                    public void run(TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new C16561());
                    }
                }

                C16571() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    TL_account_deleteSecureValue req = new TL_account_deleteSecureValue();
                    for (int a = 0; a < PassportActivity.this.currentForm.values.size(); a++) {
                        req.types.add(((TL_secureValue) PassportActivity.this.currentForm.values.get(a)).type);
                    }
                    PassportActivity.this.needShowProgress();
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C23961());
                }
            }

            public void onClick(View v) {
                Builder builder = new Builder(PassportActivity.this.getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16571());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("TelegramPassportDeleteAlert", R.string.TelegramPassportDeleteAlert));
                PassportActivity.this.showDialog(builder.create());
            }
        });
        this.addDocumentSectionCell = new ShadowSectionCell(context);
        this.addDocumentSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.addDocumentSectionCell, LayoutHelper.createLinear(-1, -2));
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        this.emptyImageView = new ImageView(context);
        this.emptyImageView.setImageResource(R.drawable.no_passport);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), Mode.MULTIPLY));
        this.emptyLayout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTextView1 = new TextView(context);
        this.emptyTextView1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 15.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView1.setText(LocaleController.getString("PassportNoDocuments", R.string.PassportNoDocuments));
        this.emptyLayout.addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        this.emptyTextView2 = new TextView(context);
        this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.emptyTextView2.setText(LocaleController.getString("PassportNoDocumentsInfo", R.string.PassportNoDocumentsInfo));
        this.emptyLayout.addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        this.emptyTextView3 = new TextView(context);
        this.emptyTextView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setTextSize(1, 15.0f);
        this.emptyTextView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setText(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd).toUpperCase());
        this.emptyLayout.addView(this.emptyTextView3, LayoutHelper.createLinear(-2, 30, 17, 0, 16, 0, 0));
        this.emptyTextView3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PassportActivity.this.openAddDocumentAlert();
            }
        });
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            SecureValueType type;
            ArrayList<SecureValueType> documentTypes;
            boolean z;
            TL_secureValue value = (TL_secureValue) this.currentForm.values.get(a);
            if ((value.type instanceof TL_secureValueTypeDriverLicense) || (value.type instanceof TL_secureValueTypePassport) || (value.type instanceof TL_secureValueTypeInternalPassport) || (value.type instanceof TL_secureValueTypeIdentityCard)) {
                type = new TL_secureValueTypePersonalDetails();
                documentTypes = new ArrayList();
                documentTypes.add(value.type);
            } else if ((value.type instanceof TL_secureValueTypeUtilityBill) || (value.type instanceof TL_secureValueTypeBankStatement) || (value.type instanceof TL_secureValueTypePassportRegistration) || (value.type instanceof TL_secureValueTypeTemporaryRegistration) || (value.type instanceof TL_secureValueTypeRentalAgreement)) {
                type = new TL_secureValueTypeAddress();
                documentTypes = new ArrayList();
                documentTypes.add(value.type);
            } else {
                type = value.type;
                documentTypes = null;
            }
            if (a == size - 1) {
                z = true;
            } else {
                z = false;
            }
            addField(context, type, documentTypes, z);
        }
        updateManageVisibility();
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
        final ArrayList<Class<? extends SecureValueType>> types = new ArrayList();
        if (hasNotValueForType(TL_secureValueTypePhone.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone));
            types.add(TL_secureValueTypePhone.class);
        }
        if (hasNotValueForType(TL_secureValueTypeEmail.class)) {
            values.add(LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail));
            types.add(TL_secureValueTypeEmail.class);
        }
        if (hasNotValueForType(TL_secureValueTypePersonalDetails.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentity", R.string.ActionBotDocumentIdentity));
            types.add(TL_secureValueTypePersonalDetails.class);
        }
        if (hasNotValueForType(TL_secureValueTypePassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport));
            types.add(TL_secureValueTypePassport.class);
        }
        if (hasNotValueForType(TL_secureValueTypeInternalPassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport));
            types.add(TL_secureValueTypeInternalPassport.class);
        }
        if (hasNotValueForType(TL_secureValueTypePassportRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            types.add(TL_secureValueTypePassportRegistration.class);
        }
        if (hasNotValueForType(TL_secureValueTypeTemporaryRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            types.add(TL_secureValueTypeTemporaryRegistration.class);
        }
        if (hasNotValueForType(TL_secureValueTypeIdentityCard.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard));
            types.add(TL_secureValueTypeIdentityCard.class);
        }
        if (hasNotValueForType(TL_secureValueTypeDriverLicense.class)) {
            values.add(LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence));
            types.add(TL_secureValueTypeDriverLicense.class);
        }
        if (hasNotValueForType(TL_secureValueTypeAddress.class)) {
            values.add(LocaleController.getString("ActionBotDocumentAddress", R.string.ActionBotDocumentAddress));
            types.add(TL_secureValueTypeAddress.class);
        }
        if (hasNotValueForType(TL_secureValueTypeUtilityBill.class)) {
            values.add(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            types.add(TL_secureValueTypeUtilityBill.class);
        }
        if (hasNotValueForType(TL_secureValueTypeBankStatement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            types.add(TL_secureValueTypeBankStatement.class);
        }
        if (hasNotValueForType(TL_secureValueTypeRentalAgreement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            types.add(TL_secureValueTypeRentalAgreement.class);
        }
        if (getParentActivity() != null && !values.isEmpty()) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd));
            builder.setItems((CharSequence[]) values.toArray(new CharSequence[values.size()]), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SecureValueType type = null;
                    SecureValueType documentType = null;
                    try {
                        type = (SecureValueType) ((Class) types.get(which)).newInstance();
                    } catch (Exception e) {
                    }
                    if ((type instanceof TL_secureValueTypeDriverLicense) || (type instanceof TL_secureValueTypePassport) || (type instanceof TL_secureValueTypeInternalPassport) || (type instanceof TL_secureValueTypeIdentityCard)) {
                        documentType = type;
                        type = new TL_secureValueTypePersonalDetails();
                    } else if ((type instanceof TL_secureValueTypeUtilityBill) || (type instanceof TL_secureValueTypeBankStatement) || (type instanceof TL_secureValueTypePassportRegistration) || (type instanceof TL_secureValueTypeTemporaryRegistration) || (type instanceof TL_secureValueTypeRentalAgreement)) {
                        documentType = type;
                        type = new TL_secureValueTypeAddress();
                    }
                    PassportActivity.this.openTypeActivity(type, documentType, new ArrayList());
                }
            });
            showDialog(builder.create());
        }
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
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell settingsCell1 = new TextSettingsCell(context);
            settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            settingsCell1.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, this.currentEmail), false);
            this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
            settingsCell1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PassportActivity.this.useCurrentValue = true;
                    PassportActivity.this.doneItem.callOnClick();
                    PassportActivity.this.useCurrentValue = false;
                }
            });
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameEmailInfo", R.string.PassportPhoneUseSameEmailInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(33);
            this.inputFields[a].setImeOptions(268435462);
            switch (a) {
                case 0:
                    this.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
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
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 6 && i != 5) {
                        return false;
                    }
                    PassportActivity.this.doneItem.callOnClick();
                    return true;
                }
            });
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportEmailUploadInfo", R.string.PassportEmailUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    private void createPhoneInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", R.string.PassportPhone));
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        Collections.sort(this.countriesArray, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        String currentPhone = UserConfig.getInstance(this.currentAccount).getCurrentUser().phone;
        View textSettingsCell = new TextSettingsCell(context);
        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        textSettingsCell.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, PhoneFormat.getInstance().format("+" + currentPhone)), false);
        this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
        textSettingsCell.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PassportActivity.this.useCurrentValue = true;
                PassportActivity.this.doneItem.callOnClick();
                PassportActivity.this.useCurrentValue = false;
            }
        });
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameInfo", R.string.PassportPhoneUseSameInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportPhoneUseOther", R.string.PassportPhoneUseOther));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
                this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (a == 2) {
                container = (ViewGroup) this.inputFields[1].getParent();
            } else {
                container = new FrameLayout(context);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            if (a == 0) {
                this.inputFields[a].setOnTouchListener(new OnTouchListener() {

                    /* renamed from: org.telegram.ui.PassportActivity$23$1 */
                    class C23971 implements CountrySelectActivityDelegate {

                        /* renamed from: org.telegram.ui.PassportActivity$23$1$1 */
                        class C16591 implements Runnable {
                            C16591() {
                            }

                            public void run() {
                                AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[2]);
                            }
                        }

                        C23971() {
                        }

                        public void didSelectCountry(String name, String shortName) {
                            PassportActivity.this.inputFields[0].setText(name);
                            if (PassportActivity.this.countriesArray.indexOf(name) != -1) {
                                PassportActivity.this.ignoreOnTextChange = true;
                                String code = (String) PassportActivity.this.countriesMap.get(name);
                                PassportActivity.this.inputFields[1].setText(code);
                                String hint = (String) PassportActivity.this.phoneFormatMap.get(code);
                                PassportActivity.this.inputFields[2].setHintText(hint != null ? hint.replace('X', '\u2013') : null);
                                PassportActivity.this.ignoreOnTextChange = false;
                            }
                            AndroidUtilities.runOnUIThread(new C16591(), 300);
                            PassportActivity.this.inputFields[2].requestFocus();
                            PassportActivity.this.inputFields[2].setSelection(PassportActivity.this.inputFields[2].length());
                        }
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        if (PassportActivity.this.getParentActivity() == null) {
                            return false;
                        }
                        if (event.getAction() == 1) {
                            CountrySelectActivity fragment = new CountrySelectActivity(false);
                            fragment.setCountrySelectActivityDelegate(new C23971());
                            PassportActivity.this.presentFragment(fragment);
                        }
                        return true;
                    }
                });
                this.inputFields[a].setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(3);
                if (a == 2) {
                    this.inputFields[a].setImeOptions(268435462);
                } else {
                    this.inputFields[a].setImeOptions(268435461);
                }
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            if (a == 1) {
                this.plusTextView = new TextView(context);
                this.plusTextView.setText("+");
                this.plusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.plusTextView.setTextSize(1, 16.0f);
                container.addView(this.plusTextView, LayoutHelper.createLinear(-2, -2, 17.0f, 12.0f, 0.0f, 6.0f));
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
                                phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                                PassportActivity.this.inputFields[0].setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                            } else {
                                boolean ok = false;
                                String textToSet = null;
                                if (text.length() > 4) {
                                    for (int a = 4; a >= 1; a--) {
                                        String sub = text.substring(0, a);
                                        if (((String) PassportActivity.this.codesMap.get(sub)) != null) {
                                            ok = true;
                                            textToSet = text.substring(a, text.length()) + PassportActivity.this.inputFields[2].getText().toString();
                                            text = sub;
                                            PassportActivity.this.inputFields[1].setText(sub);
                                            break;
                                        }
                                    }
                                    if (!ok) {
                                        textToSet = text.substring(1, text.length()) + PassportActivity.this.inputFields[2].getText().toString();
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
                                            phoneField.setHintText(hint.replace('X', '\u2013'));
                                            phoneField.setHint(null);
                                        }
                                    }
                                }
                                if (!set) {
                                    phoneField.setHintText(null);
                                    phoneField.setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
                                    PassportActivity.this.inputFields[0].setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
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
                this.inputFields[a].setHint(LocaleController.getString("PaymentShippingPhoneNumber", R.string.PaymentShippingPhoneNumber));
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
                        if (!PassportActivity.this.ignoreOnPhoneChange) {
                            int a;
                            HintEditText phoneField = PassportActivity.this.inputFields[2];
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
                container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
            }
            this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == 5) {
                        PassportActivity.this.inputFields[2].requestFocus();
                        return true;
                    } else if (i != 6) {
                        return false;
                    } else {
                        PassportActivity.this.doneItem.callOnClick();
                        return true;
                    }
                }
            });
            if (a == 0) {
                View divider = new View(context);
                this.dividers.add(divider);
                divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                container.addView(divider, new LayoutParams(-1, 1, 83));
            }
        }
        String country = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                country = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        if (country != null) {
            String countryName = (String) this.languageMap.get(country);
            if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                this.inputFields[1].setText((CharSequence) this.countriesMap.get(countryName));
            }
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportPhoneUploadInfo", R.string.PassportPhoneUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        if (this.currentDocumentsType instanceof TL_secureValueTypeRentalAgreement) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypeBankStatement) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypeUtilityBill) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypePassportRegistration) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypeTemporaryRegistration) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
        }
        if (this.currentDocumentsType != null) {
            this.headerCell = new HeaderCell(context);
            this.headerCell.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            this.documentsLayout = new LinearLayout(context);
            this.documentsLayout.setOrientation(1);
            this.linearLayout2.addView(this.documentsLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell = new TextSettingsCell(context);
            this.uploadDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PassportActivity.this.uploadingFileType = 0;
                    PassportActivity.this.openAttachMenu();
                }
            });
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            if (this.currentBotId != 0) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAddressUploadInfo", R.string.PassportAddAddressUploadInfo);
            } else if (this.currentDocumentsType instanceof TL_secureValueTypeRentalAgreement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAgreementInfo", R.string.PassportAddAgreementInfo);
            } else if (this.currentDocumentsType instanceof TL_secureValueTypeUtilityBill) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBillInfo", R.string.PassportAddBillInfo);
            } else if (this.currentDocumentsType instanceof TL_secureValueTypePassportRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddPassportRegistrationInfo", R.string.PassportAddPassportRegistrationInfo);
            } else if (this.currentDocumentsType instanceof TL_secureValueTypeTemporaryRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddTemporaryRegistrationInfo", R.string.PassportAddTemporaryRegistrationInfo);
            } else if (this.currentDocumentsType instanceof TL_secureValueTypeBankStatement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBankInfo", R.string.PassportAddBankInfo);
            } else {
                this.noAllDocumentsErrorText = TtmlNode.ANONYMOUS_REGION_ID;
            }
            CharSequence text = this.noAllDocumentsErrorText;
            if (this.documentsErrors != null) {
                String errorText = (String) this.documentsErrors.get("files_all");
                if (errorText != null) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorText);
                    spannableStringBuilder.append("\n\n");
                    spannableStringBuilder.append(this.noAllDocumentsErrorText);
                    text = spannableStringBuilder;
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText.length(), 33);
                    this.errorsValues.put("files_all", TtmlNode.ANONYMOUS_REGION_ID);
                }
            }
            this.bottomCell.setText(text);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportAddressHeader", R.string.PassportAddressHeader));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[6];
        for (int a = 0; a < 6; a++) {
            String key;
            final EditTextBoldCursor field = new EditTextBoldCursor(context);
            this.inputFields[a] = field;
            ViewGroup container = new FrameLayout(context) {
                private StaticLayout errorLayout;
                float offsetX;

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

                protected void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(17.0f)) + this.offsetX, field.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, -2));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            if (a == 5) {
                this.extraBackgroundView = new View(context);
                this.extraBackgroundView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.currentBotId == 0 && this.currentDocumentsType != null) {
                container.setVisibility(8);
                if (this.extraBackgroundView != null) {
                    this.extraBackgroundView.setVisibility(8);
                }
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setSupportRtlHint(true);
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a == 5) {
                this.inputFields[a].setOnTouchListener(new OnTouchListener() {

                    /* renamed from: org.telegram.ui.PassportActivity$29$1 */
                    class C23981 implements CountrySelectActivityDelegate {
                        C23981() {
                        }

                        public void didSelectCountry(String name, String shortName) {
                            PassportActivity.this.inputFields[5].setText(name);
                            PassportActivity.this.currentCitizeship = shortName;
                        }
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        if (PassportActivity.this.getParentActivity() == null) {
                            return false;
                        }
                        if (event.getAction() == 1) {
                            CountrySelectActivity fragment = new CountrySelectActivity(false);
                            fragment.setCountrySelectActivityDelegate(new C23981());
                            PassportActivity.this.presentFragment(fragment);
                        }
                        return true;
                    }
                });
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(16385);
                this.inputFields[a].setImeOptions(268435461);
            }
            switch (a) {
                case 0:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportStreet1", R.string.PassportStreet1));
                    key = "street_line1";
                    break;
                case 1:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportStreet2", R.string.PassportStreet2));
                    key = "street_line2";
                    break;
                case 2:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportPostcode", R.string.PassportPostcode));
                    key = "post_code";
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCity", R.string.PassportCity));
                    key = "city";
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportState", R.string.PassportState));
                    key = "state";
                    break;
                case 5:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCountry", R.string.PassportCountry));
                    key = "country_code";
                    break;
                default:
                    break;
            }
            setFieldValues(this.inputFields[a], key);
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
                                field.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            } else {
                                PassportActivity.this.checkFieldForError(field, str, s);
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
                        PassportActivity.this.checkFieldForError(field, str, s);
                    }
                });
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, 0);
            this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, 64.0f, 51, 17.0f, 0.0f, 17.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    int num = ((Integer) textView.getTag()).intValue() + 1;
                    if (num >= PassportActivity.this.inputFields.length) {
                        return true;
                    }
                    if (PassportActivity.this.inputFields[num].isFocusable()) {
                        PassportActivity.this.inputFields[num].requestFocus();
                        return true;
                    }
                    PassportActivity.this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                    return true;
                }
            });
        }
        this.sectionCell = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.currentBotId == 0 && this.currentDocumentsType != null) {
            this.headerCell.setVisibility(8);
            this.sectionCell.setVisibility(8);
        }
        if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null) || this.currentDocumentsTypeValue != null) {
            if (this.currentDocumentsTypeValue != null) {
                addDocumentViews(this.currentDocumentsTypeValue.files);
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            View textSettingsCell = new TextSettingsCell(context);
            textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentBotId == 0 && this.currentDocumentsType == null) {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
            } else {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
            }
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PassportActivity.this.createDocumentDeleteAlert();
                }
            });
            this.sectionCell = new ShadowSectionCell(context);
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            if (this.currentBotId == 0 && this.currentDocumentsType != null) {
                this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }
        updateUploadText(0);
    }

    private void createDocumentDeleteAlert() {
        final boolean[] checks = new boolean[]{true};
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PassportActivity.this.currentValues.clear();
                PassportActivity.this.delegate.deleteValue(PassportActivity.this.currentType, PassportActivity.this.currentDocumentsType, checks[0], null, null);
                PassportActivity.this.finishFragment();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (this.currentBotId == 0 && this.currentDocumentsType == null && (this.currentType instanceof TL_secureValueTypeAddress)) {
            builder.setMessage(LocaleController.getString("PassportDeleteAddressAlert", R.string.PassportDeleteAddressAlert));
        } else if (this.currentBotId == 0 && this.currentDocumentsType == null && (this.currentType instanceof TL_secureValueTypePersonalDetails)) {
            builder.setMessage(LocaleController.getString("PassportDeletePersonalAlert", R.string.PassportDeletePersonalAlert));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteDocumentAlert", R.string.PassportDeleteDocumentAlert));
        }
        if (!(this.currentBotId == 0 || this.currentDocumentsType == null)) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentType instanceof TL_secureValueTypeAddress) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentAddress", R.string.PassportDeleteDocumentAddress), TtmlNode.ANONYMOUS_REGION_ID, true, false);
            } else if (this.currentType instanceof TL_secureValueTypePersonalDetails) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentPersonal", R.string.PassportDeleteDocumentPersonal), TtmlNode.ANONYMOUS_REGION_ID, true, false);
            }
            cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48, 51));
            cell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (v.isEnabled()) {
                        boolean z;
                        CheckBoxCell cell = (CheckBoxCell) v;
                        boolean[] zArr = checks;
                        if (checks[0]) {
                            z = false;
                        } else {
                            z = true;
                        }
                        zArr[0] = z;
                        cell.setChecked(checks[0], true);
                    }
                }
            });
            builder.setView(frameLayout);
        }
        showDialog(builder.create());
    }

    private void onFieldError(View field) {
        if (field != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
            while (field != null && this.linearLayout2.indexOfChild(field) < 0) {
                field = (View) field.getParent();
            }
            if (field != null) {
                this.scrollView.smoothScrollTo(0, field.getTop() - ((this.scrollView.getMeasuredHeight() - field.getMeasuredHeight()) / 2));
            }
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
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    private void checkFieldForError(EditTextBoldCursor field, String key, Editable s) {
        if (this.errorsValues != null) {
            String value = (String) this.errorsValues.get(key);
            if (value != null) {
                if (TextUtils.equals(value, s)) {
                    if (this.fieldsErrors != null) {
                        value = (String) this.fieldsErrors.get(key);
                        if (value != null) {
                            field.setErrorText(value);
                            return;
                        }
                    }
                    if (this.documentsErrors != null) {
                        value = (String) this.documentsErrors.get(key);
                        if (value != null) {
                            field.setErrorText(value);
                            return;
                        }
                        return;
                    }
                    return;
                }
                field.setErrorText(null);
                return;
            }
        }
        field.setErrorText(null);
    }

    private boolean checkFieldsForError() {
        int a;
        String key;
        if (this.currentDocumentsType != null) {
            if (this.uploadDocumentCell != null) {
                if (this.documents.isEmpty()) {
                    onFieldError(this.uploadDocumentCell);
                    return true;
                }
                a = 0;
                int size = this.documents.size();
                while (a < size) {
                    SecureDocument document = (SecureDocument) this.documents.get(a);
                    key = "files" + getDocumentHash(document);
                    if (key == null || !this.errorsValues.containsKey(key)) {
                        a++;
                    } else {
                        onFieldError((View) this.documentsCells.get(document));
                        return true;
                    }
                }
            }
            if (this.errorsValues.containsKey("files_all")) {
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
            if (((this.currentDocumentsType instanceof TL_secureValueTypeIdentityCard) || (this.currentDocumentsType instanceof TL_secureValueTypeDriverLicense)) && this.uploadReverseCell != null) {
                if (this.reverseDocument == null) {
                    onFieldError(this.uploadReverseCell);
                    return true;
                }
                if (this.errorsValues.containsKey("reverse" + getDocumentHash(this.reverseDocument))) {
                    onFieldError((View) this.documentsCells.get(this.reverseDocument));
                    return true;
                }
            }
            if (this.uploadSelfieCell != null) {
                if (this.selfieDocument == null) {
                    onFieldError(this.uploadSelfieCell);
                    return true;
                }
                if (this.errorsValues.containsKey("selfie" + getDocumentHash(this.selfieDocument))) {
                    onFieldError((View) this.documentsCells.get(this.selfieDocument));
                    return true;
                }
            }
        }
        a = 0;
        while (a < this.inputFields.length) {
            boolean error = false;
            if (this.inputFields[a].hasErrorText()) {
                error = true;
            }
            if (!this.errorsValues.isEmpty()) {
                if (this.currentType instanceof TL_secureValueTypePersonalDetails) {
                    switch (a) {
                        case 0:
                            key = "first_name";
                            break;
                        case 1:
                            key = "last_name";
                            break;
                        case 2:
                            key = "birth_date";
                            break;
                        case 3:
                            key = "gender";
                            break;
                        case 4:
                            key = "country_code";
                            break;
                        case 5:
                            key = "residence_country_code";
                            break;
                        case 6:
                            key = "document_no";
                            break;
                        case 7:
                            key = "expiry_date";
                            break;
                        default:
                            key = null;
                            break;
                    }
                } else if (this.currentType instanceof TL_secureValueTypeAddress) {
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
                    if (!TextUtils.isEmpty(value) && value.equals(this.inputFields[a].getText().toString())) {
                        error = true;
                    }
                }
            }
            if (this.currentBotId != 0 || this.currentDocumentsType == null || a >= 6) {
                if (!error) {
                    int len = this.inputFields[a].length();
                    if (this.currentActivityType == 1) {
                        if (a == 7) {
                            continue;
                        } else if (a == 0 || a == 1) {
                            if (len > 255) {
                                error = true;
                            }
                        } else if (a == 6 && len > 24) {
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
                    if (!error && len == 0) {
                        error = true;
                    }
                }
                if (error) {
                    onFieldError(this.inputFields[a]);
                    return true;
                }
            }
            a++;
        }
        return false;
    }

    private void createIdentityInterface(Context context) {
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        if (this.currentDocumentsType != null) {
            this.headerCell = new HeaderCell(context);
            if (this.currentBotId == 0) {
                this.headerCell.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            } else {
                this.headerCell.setText(LocaleController.getString("PassportRequiredDocuments", R.string.PassportRequiredDocuments));
            }
            this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            this.frontLayout = new LinearLayout(context);
            this.frontLayout.setOrientation(1);
            this.linearLayout2.addView(this.frontLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell = new TextDetailSettingsCell(context);
            this.uploadFrontCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadFrontCell, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PassportActivity.this.uploadingFileType = 2;
                    PassportActivity.this.openAttachMenu();
                }
            });
            this.reverseLayout = new LinearLayout(context);
            this.reverseLayout.setOrientation(1);
            this.linearLayout2.addView(this.reverseLayout, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell = new TextDetailSettingsCell(context);
            this.uploadReverseCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.uploadReverseCell.setTextAndValue(LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide), LocaleController.getString("PassportReverseSideInfo", R.string.PassportReverseSideInfo), this.currentForm.selfie_required);
            this.linearLayout2.addView(this.uploadReverseCell, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PassportActivity.this.uploadingFileType = 3;
                    PassportActivity.this.openAttachMenu();
                }
            });
            if (this.currentForm.selfie_required) {
                this.selfieLayout = new LinearLayout(context);
                this.selfieLayout.setOrientation(1);
                this.linearLayout2.addView(this.selfieLayout, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell = new TextDetailSettingsCell(context);
                this.uploadSelfieCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.uploadSelfieCell.setTextAndValue(LocaleController.getString("PassportSelfie", R.string.PassportSelfie), LocaleController.getString("PassportSelfieInfo", R.string.PassportSelfieInfo), false);
                this.linearLayout2.addView(this.uploadSelfieCell, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PassportActivity.this.uploadingFileType = 1;
                        PassportActivity.this.openAttachMenu();
                    }
                });
            }
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportPersonalUploadInfo", R.string.PassportPersonalUploadInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        } else if (VERSION.SDK_INT >= 18) {
            View textSettingsCell = new TextSettingsCell(context);
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            textSettingsCell.setText(LocaleController.getString("PassportScanPassport", R.string.PassportScanPassport), false);
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.PassportActivity$39$1 */
                class C24001 implements MrzCameraActivityDelegate {
                    C24001() {
                    }

                    public void didFindMrzInfo(Result result) {
                        String country;
                        if (!TextUtils.isEmpty(result.firstName)) {
                            PassportActivity.this.inputFields[0].setText(result.firstName);
                        }
                        if (!TextUtils.isEmpty(result.lastName)) {
                            PassportActivity.this.inputFields[1].setText(result.lastName);
                        }
                        if (result.gender != 0) {
                            switch (result.gender) {
                                case 1:
                                    PassportActivity.this.currentGender = "male";
                                    PassportActivity.this.inputFields[3].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                                    break;
                                case 2:
                                    PassportActivity.this.currentGender = "female";
                                    PassportActivity.this.inputFields[3].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                                    break;
                            }
                        }
                        if (!TextUtils.isEmpty(result.nationality)) {
                            PassportActivity.this.currentCitizeship = result.nationality;
                            country = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentCitizeship);
                            if (country != null) {
                                PassportActivity.this.inputFields[4].setText(country);
                            }
                        }
                        if (!TextUtils.isEmpty(result.issuingCountry)) {
                            PassportActivity.this.currentResidence = result.issuingCountry;
                            country = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentResidence);
                            if (country != null) {
                                PassportActivity.this.inputFields[5].setText(country);
                            }
                        }
                        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
                            PassportActivity.this.inputFields[2].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
                        }
                    }
                }

                public void onClick(View v) {
                    MrzCameraActivity fragment = new MrzCameraActivity();
                    fragment.setDelegate(new C24001());
                    PassportActivity.this.presentFragment(fragment);
                }
            });
            this.bottomCell = new TextInfoPrivacyCell(context);
            this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportScanPassportInfo", R.string.PassportScanPassportInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.headerCell = new HeaderCell(context);
        this.headerCell.setText(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        int count = this.currentDocumentsType != null ? 8 : 6;
        this.inputFields = new EditTextBoldCursor[count];
        int a = 0;
        while (a < count) {
            String key;
            final EditTextBoldCursor field = new EditTextBoldCursor(context);
            this.inputFields[a] = field;
            ViewGroup container = new FrameLayout(context) {
                private StaticLayout errorLayout;
                private float offsetX;

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

                protected void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(17.0f)) + this.offsetX, field.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            if (a == count - 1) {
                this.extraBackgroundView = new View(context);
                this.extraBackgroundView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.currentBotId == 0 && this.currentDocumentsType != null && a < 6) {
                container.setVisibility(8);
                if (this.extraBackgroundView != null) {
                    this.extraBackgroundView.setVisibility(8);
                }
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setSupportRtlHint(true);
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a == 4 || a == 5) {
                this.inputFields[a].setOnTouchListener(new OnTouchListener() {
                    public boolean onTouch(final View v, MotionEvent event) {
                        if (PassportActivity.this.getParentActivity() == null) {
                            return false;
                        }
                        if (event.getAction() == 1) {
                            CountrySelectActivity fragment = new CountrySelectActivity(false);
                            fragment.setCountrySelectActivityDelegate(new CountrySelectActivityDelegate() {
                                public void didSelectCountry(String name, String shortName) {
                                    int field = ((Integer) v.getTag()).intValue();
                                    PassportActivity.this.inputFields[field].setText(name);
                                    if (field == 4) {
                                        PassportActivity.this.currentCitizeship = shortName;
                                    } else {
                                        PassportActivity.this.currentResidence = shortName;
                                    }
                                }
                            });
                            PassportActivity.this.presentFragment(fragment);
                        }
                        return true;
                    }
                });
                this.inputFields[a].setInputType(0);
            } else if (a == 2 || a == 7) {
                final Context context2 = context;
                this.inputFields[a].setOnTouchListener(new OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (PassportActivity.this.getParentActivity() == null) {
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
                                final EditTextBoldCursor field = (EditTextBoldCursor) v;
                                int num = ((Integer) field.getTag()).intValue();
                                if (num == 7) {
                                    title = LocaleController.getString("PassportSelectExpiredDate", R.string.PassportSelectExpiredDate);
                                    minYear = 0;
                                    maxYear = 20;
                                } else {
                                    title = LocaleController.getString("PassportSelectBithdayDate", R.string.PassportSelectBithdayDate);
                                    minYear = -120;
                                    maxYear = 0;
                                }
                                Builder builder = AlertsCreator.createDatePickerDialog(context2, minYear, maxYear, title, num == 7, new DatePickerDelegate() {
                                    public void didSelectDate(int year, int month, int dayOfMonth) {
                                        PassportActivity.this.currentExpireDate[0] = year;
                                        PassportActivity.this.currentExpireDate[1] = month + 1;
                                        PassportActivity.this.currentExpireDate[2] = dayOfMonth;
                                        field.setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(dayOfMonth), Integer.valueOf(month + 1), Integer.valueOf(year)}));
                                    }
                                });
                                if (num == 7) {
                                    builder.setNegativeButton(LocaleController.getString("PassportSelectNotExpire", R.string.PassportSelectNotExpire), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            int[] access$4100 = PassportActivity.this.currentExpireDate;
                                            int[] access$41002 = PassportActivity.this.currentExpireDate;
                                            PassportActivity.this.currentExpireDate[2] = 0;
                                            access$41002[1] = 0;
                                            access$4100[0] = 0;
                                            field.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
                                        }
                                    });
                                }
                                PassportActivity.this.showDialog(builder.create());
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        return true;
                    }
                });
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else if (a == 3) {
                this.inputFields[a].setOnTouchListener(new OnTouchListener() {

                    /* renamed from: org.telegram.ui.PassportActivity$43$1 */
                    class C16641 implements DialogInterface.OnClickListener {
                        C16641() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                PassportActivity.this.currentGender = "male";
                                PassportActivity.this.inputFields[3].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                            } else if (i == 1) {
                                PassportActivity.this.currentGender = "female";
                                PassportActivity.this.inputFields[3].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                            }
                        }
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        if (PassportActivity.this.getParentActivity() == null) {
                            return false;
                        }
                        if (event.getAction() == 1) {
                            Builder builder = new Builder(PassportActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("PassportSelectGender", R.string.PassportSelectGender));
                            builder.setItems(new CharSequence[]{LocaleController.getString("PassportMale", R.string.PassportMale), LocaleController.getString("PassportFemale", R.string.PassportFemale)}, new C16641());
                            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            PassportActivity.this.showDialog(builder.create());
                        }
                        return true;
                    }
                });
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(16385);
                this.inputFields[a].setImeOptions(268435461);
            }
            switch (a) {
                case 0:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                    key = "first_name";
                    break;
                case 1:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                    key = "last_name";
                    break;
                case 2:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportBirthdate", R.string.PassportBirthdate));
                    key = "birth_date";
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportGender", R.string.PassportGender));
                    key = "gender";
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportCitizenship", R.string.PassportCitizenship));
                    key = "country_code";
                    break;
                case 5:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportResidence", R.string.PassportResidence));
                    key = "residence_country_code";
                    break;
                case 6:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportDocumentNumber", R.string.PassportDocumentNumber));
                    key = "document_no";
                    break;
                case 7:
                    this.inputFields[a].setHintText(LocaleController.getString("PassportExpired", R.string.PassportExpired));
                    key = "expiry_date";
                    break;
                default:
                    break;
            }
            setFieldValues(this.inputFields[a], key);
            this.inputFields[a].setSelection(this.inputFields[a].length());
            if (a == 0 || a == 1) {
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
                                if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && !((ch >= 'A' && ch <= 'Z') || ch == ' ' || ch == '\'' || ch == ',' || ch == '.' || ch == '&' || ch == '-' || ch == '/'))) {
                                    error = true;
                                    break;
                                }
                            }
                            this.ignore = false;
                            if (error) {
                                field.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            } else {
                                PassportActivity.this.checkFieldForError(field, key, s);
                            }
                        }
                    }
                });
            } else {
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        PassportActivity.this.checkFieldForError(field, key, s);
                    }
                });
            }
            this.inputFields[a].setPadding(0, 0, 0, 0);
            this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, 0.0f, 17.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    int num = ((Integer) textView.getTag()).intValue() + 1;
                    if (num >= PassportActivity.this.inputFields.length) {
                        return true;
                    }
                    if (PassportActivity.this.inputFields[num].isFocusable()) {
                        PassportActivity.this.inputFields[num].requestFocus();
                        return true;
                    }
                    PassportActivity.this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                    return true;
                }
            });
            a++;
        }
        this.sectionCell = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null) || this.currentDocumentsTypeValue != null) {
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
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            textSettingsCell = new TextSettingsCell(context);
            textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentBotId == 0 && this.currentDocumentsType == null) {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
            } else {
                textSettingsCell.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
            }
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PassportActivity.this.createDocumentDeleteAlert();
                }
            });
            this.sectionCell = new ShadowSectionCell(context);
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
        updateInterfaceStringsForDocumentType();
    }

    private void updateInterfaceStringsForDocumentType() {
        if (this.currentDocumentsType instanceof TL_secureValueTypeIdentityCard) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypePassport) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypeInternalPassport) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport));
        } else if (this.currentDocumentsType instanceof TL_secureValueTypeDriverLicense) {
            this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
    }

    private void updateUploadText(int type) {
        boolean divider = true;
        int i = 8;
        if (type == 0) {
            if (this.uploadDocumentCell != null) {
                if (this.documents.size() >= 1) {
                    this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", R.string.PassportUploadAdditinalDocument), false);
                } else {
                    this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadDocument", R.string.PassportUploadDocument), false);
                }
            }
        } else if (type == 1) {
            if (this.uploadSelfieCell != null) {
                r3 = this.uploadSelfieCell;
                if (this.selfieDocument == null) {
                    i = 0;
                }
                r3.setVisibility(i);
            }
        } else if (type == 2) {
            if (this.uploadFrontCell != null) {
                if (!(this.currentForm.selfie_required || (this.currentDocumentsType instanceof TL_secureValueTypeIdentityCard) || (this.currentDocumentsType instanceof TL_secureValueTypeDriverLicense))) {
                    divider = false;
                }
                if ((this.currentDocumentsType instanceof TL_secureValueTypePassport) || (this.currentDocumentsType instanceof TL_secureValueTypeInternalPassport)) {
                    this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportMainPage", R.string.PassportMainPage), LocaleController.getString("PassportMainPageInfo", R.string.PassportMainPageInfo), divider);
                } else {
                    this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide), LocaleController.getString("PassportFrontSideInfo", R.string.PassportFrontSideInfo), divider);
                }
                r3 = this.uploadFrontCell;
                if (this.frontDocument == null) {
                    i = 0;
                }
                r3.setVisibility(i);
            }
        } else if (type == 3 && this.uploadReverseCell != null) {
            if ((this.currentDocumentsType instanceof TL_secureValueTypeIdentityCard) || (this.currentDocumentsType instanceof TL_secureValueTypeDriverLicense)) {
                this.reverseLayout.setVisibility(0);
                r3 = this.uploadReverseCell;
                if (this.reverseDocument == null) {
                    i = 0;
                }
                r3.setVisibility(i);
                return;
            }
            this.reverseLayout.setVisibility(8);
            this.uploadReverseCell.setVisibility(8);
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

    private void setFieldValues(EditTextBoldCursor editText, String key) {
        String value = (String) this.currentValues.get(key);
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
                            editText.setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                            break;
                        }
                    }
                    this.currentGender = value;
                    editText.setText(LocaleController.getString("PassportMale", R.string.PassportMale));
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
                        editText.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
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

    private void addDocumentView(SecureDocument document, final int type) {
        if (type == 1) {
            this.selfieDocument = document;
            if (this.selfieLayout == null) {
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
            final int i;
            final SecureDocument secureDocument;
            final SecureDocumentCell cell = new SecureDocumentCell(this, getParentActivity());
            cell.setTag(document);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.documentsCells.put(document, cell);
            String hash = getDocumentHash(document);
            if (type == 1) {
                text = LocaleController.getString("PassportSelfie", R.string.PassportSelfie);
                this.selfieLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "selfie" + hash;
            } else if (type == 2) {
                if ((this.currentDocumentsType instanceof TL_secureValueTypePassport) || (this.currentDocumentsType instanceof TL_secureValueTypeInternalPassport)) {
                    text = LocaleController.getString("PassportMainPage", R.string.PassportMainPage);
                } else {
                    text = LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide);
                }
                this.frontLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "front" + hash;
            } else if (type == 3) {
                text = LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide);
                this.reverseLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "reverse" + hash;
            } else {
                text = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                this.documentsLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "files" + hash;
            }
            if (!(key == null || this.documentsErrors == null)) {
                value = (String) this.documentsErrors.get(key);
                if (value != null) {
                    cell.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    this.errorsValues.put(key, TtmlNode.ANONYMOUS_REGION_ID);
                    cell.setTextAndValueAndImage(text, value, document);
                    cell.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            PassportActivity.this.uploadingFileType = type;
                            if (type == 1) {
                                PassportActivity.this.currentPhotoViewerLayout = PassportActivity.this.selfieLayout;
                            } else if (type == 2) {
                                PassportActivity.this.currentPhotoViewerLayout = PassportActivity.this.frontLayout;
                            } else if (type == 3) {
                                PassportActivity.this.currentPhotoViewerLayout = PassportActivity.this.reverseLayout;
                            } else {
                                PassportActivity.this.currentPhotoViewerLayout = PassportActivity.this.documentsLayout;
                            }
                            SecureDocument document = (SecureDocument) v.getTag();
                            PhotoViewer.getInstance().setParentActivity(PassportActivity.this.getParentActivity());
                            if (type == 0) {
                                PhotoViewer.getInstance().openPhoto(PassportActivity.this.documents, PassportActivity.this.documents.indexOf(document), PassportActivity.this.provider);
                                return;
                            }
                            ArrayList<SecureDocument> arrayList = new ArrayList();
                            arrayList.add(document);
                            PhotoViewer.getInstance().openPhoto(arrayList, 0, PassportActivity.this.provider);
                        }
                    });
                    i = type;
                    secureDocument = document;
                    cell.setOnLongClickListener(new OnLongClickListener() {

                        /* renamed from: org.telegram.ui.PassportActivity$49$1 */
                        class C16651 implements DialogInterface.OnClickListener {
                            C16651() {
                            }

                            public void onClick(DialogInterface dialog, int which) {
                                PassportActivity.this.documentsCells.remove(secureDocument);
                                if (i == 1) {
                                    PassportActivity.this.selfieDocument = null;
                                    PassportActivity.this.selfieLayout.removeView(cell);
                                } else if (i == 2) {
                                    PassportActivity.this.frontDocument = null;
                                    PassportActivity.this.frontLayout.removeView(cell);
                                } else if (i == 3) {
                                    PassportActivity.this.reverseDocument = null;
                                    PassportActivity.this.reverseLayout.removeView(cell);
                                } else {
                                    PassportActivity.this.documents.remove(secureDocument);
                                    PassportActivity.this.documentsLayout.removeView(cell);
                                }
                                if (key != null) {
                                    if (PassportActivity.this.documentsErrors != null) {
                                        PassportActivity.this.documentsErrors.remove(key);
                                    }
                                    if (PassportActivity.this.errorsValues != null) {
                                        PassportActivity.this.errorsValues.remove(key);
                                    }
                                }
                                PassportActivity.this.updateUploadText(i);
                                if (secureDocument.path != null && PassportActivity.this.uploadingDocuments.remove(secureDocument.path) != null) {
                                    if (PassportActivity.this.uploadingDocuments.isEmpty()) {
                                        PassportActivity.this.doneItem.setEnabled(true);
                                        PassportActivity.this.doneItem.setAlpha(1.0f);
                                    }
                                    FileLoader.getInstance(PassportActivity.this.currentAccount).cancelUploadFile(secureDocument.path, false);
                                }
                            }
                        }

                        public boolean onLongClick(View v) {
                            Builder builder = new Builder(PassportActivity.this.getParentActivity());
                            if (i == 1) {
                                builder.setMessage(LocaleController.getString("PassportDeleteSelfie", R.string.PassportDeleteSelfie));
                            } else {
                                builder.setMessage(LocaleController.getString("PassportDeleteScan", R.string.PassportDeleteScan));
                            }
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16651());
                            PassportActivity.this.showDialog(builder.create());
                            return true;
                        }
                    });
                }
            }
            value = LocaleController.formatDateForBan((long) document.secureFile.date);
            cell.setTextAndValueAndImage(text, value, document);
            cell.setOnClickListener(/* anonymous class already generated */);
            i = type;
            secureDocument = document;
            cell.setOnLongClickListener(/* anonymous class already generated */);
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
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    private void setTypeValue(SecureValueType type, String text, String json, SecureValueType documentType, String documentsJson) {
        boolean z;
        TextDetailSecureCell view = (TextDetailSecureCell) this.typesViews.get(type);
        if (view == null) {
            if (this.currentActivityType == 8) {
                ArrayList<SecureValueType> documentTypes = new ArrayList();
                if (documentType != null) {
                    documentTypes.add(documentType);
                }
                View prev = this.linearLayout2.getChildAt(this.linearLayout2.getChildCount() - 6);
                if (prev instanceof TextDetailSecureCell) {
                    ((TextDetailSecureCell) prev).setNeedDivider(true);
                }
                view = addField(getParentActivity(), type, documentTypes, true);
                updateManageVisibility();
            } else {
                return;
            }
        }
        HashMap<String, String> values = (HashMap) this.typesValues.get(type);
        if (json != null) {
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        } else {
            this.languageMap = null;
        }
        String value = null;
        if (json == null && documentsJson == null) {
            if (text != null) {
                if (type instanceof TL_secureValueTypePhone) {
                    value = PhoneFormat.getInstance().format("+" + text);
                } else if (type instanceof TL_secureValueTypeEmail) {
                    value = text;
                }
            }
        } else if (values != null) {
            values.clear();
            String[] keys = null;
            String[] documentKeys = null;
            if (type instanceof TL_secureValueTypePersonalDetails) {
                if (this.currentActivityType == 0 || (this.currentActivityType == 8 && documentType == null)) {
                    keys = new String[]{"first_name", "last_name", "birth_date", "gender", "country_code", "residence_country_code"};
                }
                if (this.currentActivityType == 0 || (this.currentActivityType == 8 && documentType != null)) {
                    documentKeys = new String[]{"document_no", "expiry_date"};
                }
            } else if ((type instanceof TL_secureValueTypeAddress) && (this.currentActivityType == 0 || (this.currentActivityType == 8 && documentType == null))) {
                keys = new String[]{"street_line1", "street_line2", "post_code", "city", "state", "country_code"};
            }
            if (!(keys == null && documentKeys == null)) {
                String[] currentKeys = null;
                int b = 0;
                JSONObject jsonObject = null;
                StringBuilder stringBuilder = null;
                while (b < 2) {
                    JSONObject jsonObject2;
                    StringBuilder stringBuilder2;
                    if (b == 0) {
                        if (json != null) {
                            jsonObject2 = new JSONObject(json);
                            currentKeys = keys;
                        }
                        jsonObject2 = jsonObject;
                    } else {
                        if (documentsJson != null) {
                            try {
                                jsonObject2 = new JSONObject(documentsJson);
                                currentKeys = documentKeys;
                            } catch (Exception e2) {
                                jsonObject2 = jsonObject;
                                stringBuilder2 = stringBuilder;
                            }
                        }
                        jsonObject2 = jsonObject;
                    }
                    if (currentKeys == null) {
                        stringBuilder2 = stringBuilder;
                    } else {
                        try {
                            if (this.currentActivityType == 8 || b != 0 || documentType == null || TextUtils.isEmpty(documentsJson)) {
                                stringBuilder2 = stringBuilder;
                            } else {
                                if (stringBuilder == null) {
                                    stringBuilder2 = new StringBuilder();
                                } else {
                                    stringBuilder2 = stringBuilder;
                                }
                                if (documentType instanceof TL_secureValueTypePassport) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport));
                                } else if (documentType instanceof TL_secureValueTypeDriverLicense) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence));
                                } else if (documentType instanceof TL_secureValueTypeIdentityCard) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard));
                                } else if (documentType instanceof TL_secureValueTypeUtilityBill) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
                                } else if (documentType instanceof TL_secureValueTypeBankStatement) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
                                } else if (documentType instanceof TL_secureValueTypeRentalAgreement) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
                                } else if (hasNotValueForType(TL_secureValueTypeInternalPassport.class)) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport));
                                } else if (hasNotValueForType(TL_secureValueTypePassportRegistration.class)) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
                                } else if (hasNotValueForType(TL_secureValueTypeTemporaryRegistration.class)) {
                                    stringBuilder2.append(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
                                }
                            }
                            int a = 0;
                            stringBuilder = stringBuilder2;
                            while (a < currentKeys.length) {
                                if (jsonObject2.has(currentKeys[a])) {
                                    if (stringBuilder == null) {
                                        stringBuilder2 = new StringBuilder();
                                    } else {
                                        stringBuilder2 = stringBuilder;
                                    }
                                    try {
                                        String jsonValue = jsonObject2.getString(currentKeys[a]);
                                        if (jsonValue != null) {
                                            values.put(currentKeys[a], jsonValue);
                                            if (!TextUtils.isEmpty(jsonValue)) {
                                                if (stringBuilder2.length() > 0) {
                                                    if ("last_name".equals(currentKeys[a])) {
                                                        stringBuilder2.append(" ");
                                                    } else {
                                                        stringBuilder2.append(", ");
                                                    }
                                                }
                                                String str = currentKeys[a];
                                                Object obj = -1;
                                                switch (str.hashCode()) {
                                                    case -1249512767:
                                                        if (str.equals("gender")) {
                                                            obj = 1;
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
                                                        String country = (String) this.languageMap.get(jsonValue);
                                                        if (country == null) {
                                                            break;
                                                        }
                                                        stringBuilder2.append(country);
                                                        break;
                                                    case 1:
                                                        if (!"male".equals(jsonValue)) {
                                                            if (!"female".equals(jsonValue)) {
                                                                break;
                                                            }
                                                            stringBuilder2.append(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                                                            break;
                                                        }
                                                        stringBuilder2.append(LocaleController.getString("PassportMale", R.string.PassportMale));
                                                        break;
                                                    default:
                                                        stringBuilder2.append(jsonValue);
                                                        break;
                                                }
                                            }
                                        }
                                    } catch (Exception e3) {
                                    }
                                } else {
                                    stringBuilder2 = stringBuilder;
                                }
                                a++;
                                stringBuilder = stringBuilder2;
                            }
                            stringBuilder2 = stringBuilder;
                        } catch (Exception e4) {
                            stringBuilder2 = stringBuilder;
                        }
                    }
                    b++;
                    jsonObject = jsonObject2;
                    stringBuilder = stringBuilder2;
                }
                if (stringBuilder != null) {
                    value = stringBuilder.toString();
                }
            }
        } else {
            return;
        }
        boolean isError = false;
        HashMap<String, String> errors = (HashMap) this.errorsMap.get(getNameForType(type));
        HashMap<String, String> documentsErrors = (HashMap) this.errorsMap.get(getNameForType(documentType));
        if ((errors != null && errors.size() > 0) || (documentsErrors != null && documentsErrors.size() > 0)) {
            value = LocaleController.getString("PassportCorrectErrors", R.string.PassportCorrectErrors);
            isError = true;
        } else if (type instanceof TL_secureValueTypePersonalDetails) {
            if (TextUtils.isEmpty(value)) {
                if (documentType == null) {
                    value = LocaleController.getString("PassportPersonalDetailsInfo", R.string.PassportPersonalDetailsInfo);
                } else if (this.currentActivityType == 8) {
                    value = LocaleController.getString("PassportDocuments", R.string.PassportDocuments);
                } else {
                    value = LocaleController.getString("PassportIdentityDocumentInfo", R.string.PassportIdentityDocumentInfo);
                }
            }
        } else if (type instanceof TL_secureValueTypeAddress) {
            if (TextUtils.isEmpty(value)) {
                if (documentType == null) {
                    value = LocaleController.getString("PassportAddressNoUploadInfo", R.string.PassportAddressNoUploadInfo);
                } else if (this.currentActivityType == 8) {
                    value = LocaleController.getString("PassportDocuments", R.string.PassportDocuments);
                } else {
                    value = LocaleController.getString("PassportAddressInfo", R.string.PassportAddressInfo);
                }
            }
        } else if (type instanceof TL_secureValueTypePhone) {
            if (TextUtils.isEmpty(value)) {
                value = LocaleController.getString("PassportPhoneInfo", R.string.PassportPhoneInfo);
            }
        } else if ((type instanceof TL_secureValueTypeEmail) && TextUtils.isEmpty(value)) {
            value = LocaleController.getString("PassportEmailInfo", R.string.PassportEmailInfo);
        }
        view.setValue(value);
        view.valueTextView.setTextColor(Theme.getColor(isError ? Theme.key_windowBackgroundWhiteRedText3 : Theme.key_windowBackgroundWhiteGrayText2));
        if (isError || this.currentActivityType == 8 || getValueByType(type, true) == null || (documentType != null && getValueByType(documentType, true) == null)) {
            z = false;
        } else {
            z = true;
        }
        view.setChecked(z);
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

    private TL_secureValue getValueByType(SecureValueType type, boolean check) {
        if (type == null) {
            return null;
        }
        int a = 0;
        int size = this.currentForm.values.size();
        while (a < size) {
            TL_secureValue secureValue = (TL_secureValue) this.currentForm.values.get(a);
            if (type.getClass() != secureValue.type.getClass()) {
                a++;
            } else if (!check) {
                return secureValue;
            } else {
                if (this.currentForm.selfie_required && (((type instanceof TL_secureValueTypeDriverLicense) || (type instanceof TL_secureValueTypePassport) || (type instanceof TL_secureValueTypeInternalPassport) || (type instanceof TL_secureValueTypeIdentityCard)) && !(secureValue.selfie instanceof TL_secureFile))) {
                    return null;
                }
                if (((type instanceof TL_secureValueTypeUtilityBill) || (type instanceof TL_secureValueTypeBankStatement) || (type instanceof TL_secureValueTypePassportRegistration) || (type instanceof TL_secureValueTypeTemporaryRegistration) || (type instanceof TL_secureValueTypeRentalAgreement)) && secureValue.files.isEmpty()) {
                    return null;
                }
                if (((type instanceof TL_secureValueTypeDriverLicense) || (type instanceof TL_secureValueTypePassport) || (type instanceof TL_secureValueTypeInternalPassport) || (type instanceof TL_secureValueTypeIdentityCard)) && !(secureValue.front_side instanceof TL_secureFile)) {
                    return null;
                }
                if (((type instanceof TL_secureValueTypeDriverLicense) || (type instanceof TL_secureValueTypeIdentityCard)) && !(secureValue.reverse_side instanceof TL_secureFile)) {
                    return null;
                }
                return secureValue;
            }
        }
        return null;
    }

    private void openTypeActivity(SecureValueType type, SecureValueType documentsType, ArrayList<SecureValueType> availableDocumentTypes) {
        int activityType = -1;
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
            HashMap<String, String> errors = (HashMap) this.errorsMap.get(getNameForType(type));
            HashMap<String, String> documentsErrors = (HashMap) this.errorsMap.get(getNameForType(documentsType));
            SecureValueType secureValueType = type;
            SecureValueType secureValueType2 = documentsType;
            PassportActivity activity = new PassportActivity(activityType, this.currentForm, this.currentPassword, secureValueType, getValueByType(type, false), secureValueType2, getValueByType(documentsType, false), (HashMap) this.typesValues.get(type));
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

                public void saveValue(SecureValueType type, String text, String json, SecureValueType documentsType, String documentsJson, ArrayList<SecureDocument> documents, SecureDocument selfie, SecureDocument front, SecureDocument reverse, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    TL_inputSecureValue inputSecureValue = null;
                    if (!TextUtils.isEmpty(json)) {
                        inputSecureValue = new TL_inputSecureValue();
                        inputSecureValue.type = type;
                        inputSecureValue.flags |= 1;
                        EncryptionResult result = PassportActivity.this.encryptData(AndroidUtilities.getStringBytes(json));
                        inputSecureValue.data = new TL_secureData();
                        inputSecureValue.data.data = result.encryptedData;
                        inputSecureValue.data.data_hash = result.fileHash;
                        inputSecureValue.data.secret = result.fileSecret;
                    } else if (!TextUtils.isEmpty(text)) {
                        SecurePlainData plainData;
                        if (type instanceof TL_secureValueTypeEmail) {
                            SecurePlainData securePlainEmail = new TL_securePlainEmail();
                            securePlainEmail.email = text;
                            plainData = securePlainEmail;
                        } else if (type instanceof TL_secureValueTypePhone) {
                            SecurePlainData securePlainPhone = new TL_securePlainPhone();
                            securePlainPhone.phone = text;
                            plainData = securePlainPhone;
                        } else {
                            return;
                        }
                        inputSecureValue = new TL_inputSecureValue();
                        inputSecureValue.type = type;
                        inputSecureValue.flags |= 32;
                        inputSecureValue.plain_data = plainData;
                    }
                    if (inputSecureValue != null) {
                        TL_inputSecureValue fileInputSecureValue;
                        if (documentsType != null) {
                            fileInputSecureValue = new TL_inputSecureValue();
                            fileInputSecureValue.type = documentsType;
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
                            if (!(documents == null || documents.isEmpty())) {
                                fileInputSecureValue.flags |= 16;
                                int size = documents.size();
                                for (int a = 0; a < size; a++) {
                                    fileInputSecureValue.files.add(getInputSecureFile((SecureDocument) documents.get(a)));
                                }
                            }
                            if (PassportActivity.this.currentActivityType == 8) {
                                inputSecureValue = fileInputSecureValue;
                                fileInputSecureValue = null;
                            }
                        } else {
                            fileInputSecureValue = null;
                        }
                        final AnonymousClass50 currentDelegate = this;
                        final TL_inputSecureValue finalFileInputSecureValue = fileInputSecureValue;
                        final TL_account_saveSecureValue req = new TL_account_saveSecureValue();
                        req.value = inputSecureValue;
                        req.secure_secret_id = PassportActivity.this.secureSecretId;
                        final ErrorRunnable errorRunnable2 = errorRunnable;
                        final String str = text;
                        final SecureValueType secureValueType = documentsType;
                        final SecureValueType secureValueType2 = type;
                        final ArrayList<SecureDocument> arrayList = documents;
                        final SecureDocument secureDocument = selfie;
                        final SecureDocument secureDocument2 = front;
                        final SecureDocument secureDocument3 = reverse;
                        final String str2 = json;
                        final String str3 = documentsJson;
                        final Runnable runnable = finishRunnable;
                        ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {

                            /* renamed from: org.telegram.ui.PassportActivity$50$1$2 */
                            class C24072 implements RequestDelegate {
                                C24072() {
                                }

                                public void run(final TLObject response, final TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            if (response != null) {
                                                TL_account_sentEmailCode res = response;
                                                HashMap values = new HashMap();
                                                values.put("email", str);
                                                values.put("pattern", res.email_pattern);
                                                PassportActivity activity = new PassportActivity(6, PassportActivity.this.currentForm, PassportActivity.this.currentPassword, secureValueType2, null, null, null, values);
                                                activity.currentAccount = PassportActivity.this.currentAccount;
                                                activity.emailCodeLength = res.length;
                                                activity.saltedPassword = PassportActivity.this.saltedPassword;
                                                activity.secureSecret = PassportActivity.this.secureSecret;
                                                activity.delegate = currentDelegate;
                                                PassportActivity.this.presentFragment(activity, true);
                                                return;
                                            }
                                            PassportActivity.this.showAlertWithText(LocaleController.getString("PassportEmail", R.string.PassportEmail), error.text);
                                            if (errorRunnable2 != null) {
                                                errorRunnable2.onError(error.text, str);
                                            }
                                        }
                                    });
                                }
                            }

                            private void onResult(final TL_error error, final TL_secureValue newValue, final TL_secureValue newPendingValue) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (error != null) {
                                            if (errorRunnable2 != null) {
                                                errorRunnable2.onError(error.text, str);
                                            }
                                            AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, str);
                                            return;
                                        }
                                        TL_secureFile secureFile;
                                        if (PassportActivity.this.currentActivityType != 8) {
                                            PassportActivity.this.removeValue(secureValueType2);
                                            PassportActivity.this.removeValue(secureValueType);
                                        } else if (secureValueType != null) {
                                            PassportActivity.this.removeValue(secureValueType);
                                        } else {
                                            PassportActivity.this.removeValue(secureValueType2);
                                        }
                                        if (newValue != null) {
                                            PassportActivity.this.currentForm.values.add(newValue);
                                        }
                                        if (newPendingValue != null) {
                                            PassportActivity.this.currentForm.values.add(newPendingValue);
                                        }
                                        if (!(arrayList == null || arrayList.isEmpty())) {
                                            int size = arrayList.size();
                                            for (int a = 0; a < size; a++) {
                                                SecureDocument document = (SecureDocument) arrayList.get(a);
                                                if (document.inputFile != null) {
                                                    int size2 = newValue.files.size();
                                                    for (int b = 0; b < size2; b++) {
                                                        SecureFile file = (SecureFile) newValue.files.get(b);
                                                        if (file instanceof TL_secureFile) {
                                                            secureFile = (TL_secureFile) file;
                                                            if (Utilities.arraysEquals(document.fileSecret, 0, secureFile.secret, 0)) {
                                                                AnonymousClass50.this.renameFile(document, secureFile);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (!(secureDocument == null || secureDocument.inputFile == null || !(newValue.selfie instanceof TL_secureFile))) {
                                            secureFile = (TL_secureFile) newValue.selfie;
                                            if (Utilities.arraysEquals(secureDocument.fileSecret, 0, secureFile.secret, 0)) {
                                                AnonymousClass50.this.renameFile(secureDocument, secureFile);
                                            }
                                        }
                                        if (!(secureDocument2 == null || secureDocument2.inputFile == null || !(newValue.front_side instanceof TL_secureFile))) {
                                            secureFile = (TL_secureFile) newValue.front_side;
                                            if (Utilities.arraysEquals(secureDocument2.fileSecret, 0, secureFile.secret, 0)) {
                                                AnonymousClass50.this.renameFile(secureDocument2, secureFile);
                                            }
                                        }
                                        if (!(secureDocument3 == null || secureDocument3.inputFile == null || !(newValue.reverse_side instanceof TL_secureFile))) {
                                            secureFile = (TL_secureFile) newValue.reverse_side;
                                            if (Utilities.arraysEquals(secureDocument3.fileSecret, 0, secureFile.secret, 0)) {
                                                AnonymousClass50.this.renameFile(secureDocument3, secureFile);
                                            }
                                        }
                                        PassportActivity.this.setTypeValue(secureValueType2, str, str2, secureValueType, str3);
                                        if (runnable != null) {
                                            runnable.run();
                                        }
                                    }
                                });
                            }

                            public void run(TLObject response, final TL_error error) {
                                if (error != null) {
                                    if (error.text.equals("EMAIL_VERIFICATION_NEEDED")) {
                                        TL_account_sendVerifyEmailCode req = new TL_account_sendVerifyEmailCode();
                                        req.email = str;
                                        ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new C24072());
                                        return;
                                    } else if (error.text.equals("PHONE_VERIFICATION_NEEDED")) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                errorRunnable2.onError(error.text, str);
                                            }
                                        });
                                        return;
                                    }
                                }
                                if (error != null || finalFileInputSecureValue == null) {
                                    onResult(error, (TL_secureValue) response, null);
                                    return;
                                }
                                final TL_secureValue pendingValue = (TL_secureValue) response;
                                TL_account_saveSecureValue req2 = new TL_account_saveSecureValue();
                                req2.value = finalFileInputSecureValue;
                                req2.secure_secret_id = PassportActivity.this.secureSecretId;
                                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() {
                                    public void run(TLObject response, TL_error error) {
                                        C24091.this.onResult(error, (TL_secureValue) response, pendingValue);
                                    }
                                });
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

                public void deleteValue(SecureValueType type, SecureValueType documentsType, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(type, documentsType, deleteType, finishRunnable, errorRunnable);
                }
            };
            activity.currentAccount = this.currentAccount;
            activity.saltedPassword = this.saltedPassword;
            activity.secureSecret = this.secureSecret;
            activity.currentBotId = this.currentBotId;
            activity.fieldsErrors = errors;
            activity.documentsErrors = documentsErrors;
            activity.availableDocumentTypes = availableDocumentTypes;
            if (activityType == 4) {
                activity.currentEmail = this.currentEmail;
            }
            presentFragment(activity);
        }
    }

    private TL_secureValue removeValue(SecureValueType type) {
        if (type == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            if (type.getClass() == ((TL_secureValue) this.currentForm.values.get(a)).type.getClass()) {
                return (TL_secureValue) this.currentForm.values.remove(a);
            }
        }
        return null;
    }

    private void deleteValueInternal(SecureValueType type, SecureValueType documentsType, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable) {
        if (type != null) {
            TL_account_deleteSecureValue req = new TL_account_deleteSecureValue();
            if (this.currentActivityType != 8 || documentsType == null) {
                if (deleteType) {
                    req.types.add(type);
                }
                if (documentsType != null) {
                    req.types.add(documentsType);
                }
            } else {
                req.types.add(documentsType);
            }
            final ErrorRunnable errorRunnable2 = errorRunnable;
            final SecureValueType secureValueType = documentsType;
            final SecureValueType secureValueType2 = type;
            final boolean z = deleteType;
            final Runnable runnable = finishRunnable;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error != null) {
                                if (errorRunnable2 != null) {
                                    errorRunnable2.onError(error.text, null);
                                }
                                PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                                return;
                            }
                            if (PassportActivity.this.currentActivityType != 8) {
                                if (z) {
                                    PassportActivity.this.removeValue(secureValueType2);
                                }
                                PassportActivity.this.removeValue(secureValueType);
                            } else if (secureValueType != null) {
                                PassportActivity.this.removeValue(secureValueType);
                            } else {
                                PassportActivity.this.removeValue(secureValueType2);
                            }
                            if (PassportActivity.this.currentActivityType == 8) {
                                TextDetailSecureCell view = (TextDetailSecureCell) PassportActivity.this.typesViews.remove(secureValueType2);
                                if (view != null) {
                                    PassportActivity.this.linearLayout2.removeView(view);
                                    View child = PassportActivity.this.linearLayout2.getChildAt(PassportActivity.this.linearLayout2.getChildCount() - 6);
                                    if (child instanceof TextDetailSecureCell) {
                                        ((TextDetailSecureCell) child).setNeedDivider(false);
                                    }
                                }
                                PassportActivity.this.updateManageVisibility();
                            } else if (z) {
                                PassportActivity.this.setTypeValue(secureValueType2, null, null, secureValueType, null);
                            } else {
                                String json = null;
                                TL_secureValue value = PassportActivity.this.getValueByType(secureValueType2, false);
                                if (!(value == null || value.data == null)) {
                                    json = PassportActivity.this.decryptData(value.data.data, PassportActivity.this.decryptValueSecret(value.data.secret, value.data.data_hash), value.data.data_hash);
                                }
                                PassportActivity.this.setTypeValue(secureValueType2, null, json, secureValueType, null);
                            }
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
            });
        }
    }

    private TextDetailSecureCell addField(Context context, SecureValueType type, ArrayList<SecureValueType> documentTypes, boolean last) {
        String text;
        SecureValueType documentType;
        TextDetailSecureCell view = new TextDetailSecureCell(context);
        view.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        CharSequence charSequence;
        boolean z;
        if (type instanceof TL_secureValueTypePersonalDetails) {
            if (documentTypes == null || documentTypes.isEmpty()) {
                text = LocaleController.getString("PassportPersonalDetails", R.string.PassportPersonalDetails);
            } else if (documentTypes.size() == 1) {
                documentType = (SecureValueType) documentTypes.get(0);
                if (documentType instanceof TL_secureValueTypePassport) {
                    text = LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport);
                } else if (documentType instanceof TL_secureValueTypeDriverLicense) {
                    text = LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence);
                } else if (documentType instanceof TL_secureValueTypeIdentityCard) {
                    text = LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard);
                } else if (documentType instanceof TL_secureValueTypeInternalPassport) {
                    text = LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport);
                } else {
                    text = "LOC_ERR: NO NAME FOR ID TYPE";
                }
            } else {
                text = LocaleController.getString("PassportIdentityDocument", R.string.PassportIdentityDocument);
            }
            charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            if (last) {
                z = false;
            } else {
                z = true;
            }
            view.setTextAndValue(text, charSequence, z);
        } else if (type instanceof TL_secureValueTypeAddress) {
            if (documentTypes == null || documentTypes.isEmpty()) {
                text = LocaleController.getString("PassportAddress", R.string.PassportAddress);
            } else if (documentTypes.size() == 1) {
                documentType = (SecureValueType) documentTypes.get(0);
                if (documentType instanceof TL_secureValueTypeUtilityBill) {
                    text = LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill);
                } else if (documentType instanceof TL_secureValueTypeBankStatement) {
                    text = LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement);
                } else if (documentType instanceof TL_secureValueTypeRentalAgreement) {
                    text = LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement);
                } else if (documentType instanceof TL_secureValueTypePassportRegistration) {
                    text = LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration);
                } else if (documentType instanceof TL_secureValueTypeTemporaryRegistration) {
                    text = LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration);
                } else {
                    text = "LOC_ERR: NO NAME FOR ADDRESS TYPE";
                }
            } else {
                text = LocaleController.getString("PassportResidentialAddress", R.string.PassportResidentialAddress);
            }
            charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            if (last) {
                z = false;
            } else {
                z = true;
            }
            view.setTextAndValue(text, charSequence, z);
        } else if (type instanceof TL_secureValueTypePhone) {
            view.setTextAndValue(LocaleController.getString("PassportPhone", R.string.PassportPhone), TtmlNode.ANONYMOUS_REGION_ID, !last);
        } else if (type instanceof TL_secureValueTypeEmail) {
            view.setTextAndValue(LocaleController.getString("PassportEmail", R.string.PassportEmail), TtmlNode.ANONYMOUS_REGION_ID, !last);
        }
        if (this.currentActivityType == 8) {
            this.linearLayout2.addView(view, this.linearLayout2.getChildCount() - 5, LayoutHelper.createLinear(-1, -2));
        } else {
            this.linearLayout2.addView(view, LayoutHelper.createLinear(-1, -2));
        }
        final ArrayList<SecureValueType> arrayList = documentTypes;
        final SecureValueType secureValueType = type;
        view.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.PassportActivity$52$1 */
            class C16701 implements DialogInterface.OnClickListener {
                C16701() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    PassportActivity.this.openTypeActivity(secureValueType, (SecureValueType) arrayList.get(which), arrayList);
                }
            }

            /* renamed from: org.telegram.ui.PassportActivity$52$2 */
            class C16722 implements DialogInterface.OnClickListener {

                /* renamed from: org.telegram.ui.PassportActivity$52$2$1 */
                class C16711 implements Runnable {
                    C16711() {
                    }

                    public void run() {
                        PassportActivity.this.needHideProgress();
                    }
                }

                /* renamed from: org.telegram.ui.PassportActivity$52$2$2 */
                class C24102 implements ErrorRunnable {
                    C24102() {
                    }

                    public void onError(String error, String text) {
                        PassportActivity.this.needHideProgress();
                    }
                }

                C16722() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    PassportActivity.this.needShowProgress();
                    PassportActivity.this.deleteValueInternal(secureValueType, null, true, new C16711(), new C24102());
                }
            }

            public void onClick(View v) {
                int count;
                int a;
                SecureValueType documentType;
                SecureValueType documentsType = null;
                if (arrayList != null) {
                    count = arrayList.size();
                    for (a = 0; a < count; a++) {
                        documentType = (SecureValueType) arrayList.get(a);
                        if (PassportActivity.this.getValueByType(documentType, false) != null || count == 1) {
                            documentsType = documentType;
                            break;
                        }
                    }
                }
                Builder builder;
                if (!(secureValueType instanceof TL_secureValueTypePersonalDetails) && !(secureValueType instanceof TL_secureValueTypeAddress)) {
                    boolean phoneField = secureValueType instanceof TL_secureValueTypePhone;
                    if ((phoneField || (secureValueType instanceof TL_secureValueTypeEmail)) && PassportActivity.this.getValueByType(secureValueType, false) != null) {
                        builder = new Builder(PassportActivity.this.getParentActivity());
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16722());
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(phoneField ? LocaleController.getString("PassportDeletePhoneAlert", R.string.PassportDeletePhoneAlert) : LocaleController.getString("PassportDeleteEmailAlert", R.string.PassportDeleteEmailAlert));
                        PassportActivity.this.showDialog(builder.create());
                        return;
                    }
                } else if (!(documentsType != null || arrayList == null || arrayList.isEmpty())) {
                    builder = new Builder(PassportActivity.this.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    if (secureValueType instanceof TL_secureValueTypePersonalDetails) {
                        builder.setTitle(LocaleController.getString("PassportIdentityDocument", R.string.PassportIdentityDocument));
                    } else if (secureValueType instanceof TL_secureValueTypeAddress) {
                        builder.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
                    }
                    ArrayList<String> strings = new ArrayList();
                    count = arrayList.size();
                    for (a = 0; a < count; a++) {
                        documentType = (SecureValueType) arrayList.get(a);
                        if (documentType instanceof TL_secureValueTypeDriverLicense) {
                            strings.add(LocaleController.getString("PassportAddLicence", R.string.PassportAddLicence));
                        } else if (documentType instanceof TL_secureValueTypePassport) {
                            strings.add(LocaleController.getString("PassportAddPassport", R.string.PassportAddPassport));
                        } else if (documentType instanceof TL_secureValueTypeInternalPassport) {
                            strings.add(LocaleController.getString("PassportAddInternalPassport", R.string.PassportAddInternalPassport));
                        } else if (documentType instanceof TL_secureValueTypeIdentityCard) {
                            strings.add(LocaleController.getString("PassportAddCard", R.string.PassportAddCard));
                        } else if (documentType instanceof TL_secureValueTypeUtilityBill) {
                            strings.add(LocaleController.getString("PassportAddBill", R.string.PassportAddBill));
                        } else if (documentType instanceof TL_secureValueTypeBankStatement) {
                            strings.add(LocaleController.getString("PassportAddBank", R.string.PassportAddBank));
                        } else if (documentType instanceof TL_secureValueTypeRentalAgreement) {
                            strings.add(LocaleController.getString("PassportAddAgreement", R.string.PassportAddAgreement));
                        } else if (documentType instanceof TL_secureValueTypeTemporaryRegistration) {
                            strings.add(LocaleController.getString("PassportAddTemporaryRegistration", R.string.PassportAddTemporaryRegistration));
                        } else if (documentType instanceof TL_secureValueTypePassportRegistration) {
                            strings.add(LocaleController.getString("PassportAddPassportRegistration", R.string.PassportAddPassportRegistration));
                        }
                    }
                    builder.setItems((CharSequence[]) strings.toArray(new CharSequence[strings.size()]), new C16701());
                    PassportActivity.this.showDialog(builder.create());
                    return;
                }
                PassportActivity.this.openTypeActivity(secureValueType, documentsType, arrayList);
            }
        });
        this.typesViews.put(type, view);
        text = null;
        String json = null;
        String documentJson = null;
        this.typesValues.put(type, new HashMap());
        TL_secureValue value = getValueByType(type, false);
        if (value != null) {
            if (value.plain_data instanceof TL_securePlainEmail) {
                text = ((TL_securePlainEmail) value.plain_data).email;
            } else if (value.plain_data instanceof TL_securePlainPhone) {
                text = ((TL_securePlainPhone) value.plain_data).phone;
            } else if (value.data != null) {
                json = decryptData(value.data.data, decryptValueSecret(value.data.secret, value.data.data_hash), value.data.data_hash);
            }
        }
        SecureValueType documentsType = null;
        if (!(documentTypes == null || documentTypes.isEmpty())) {
            int a = 0;
            int count = documentTypes.size();
            while (a < count) {
                documentType = (SecureValueType) documentTypes.get(a);
                TL_secureValue documentValue = getValueByType(documentType, false);
                if (documentValue != null) {
                    if (documentValue.data != null) {
                        documentJson = decryptData(documentValue.data.data, decryptValueSecret(documentValue.data.secret, documentValue.data.data_hash), documentValue.data.data_hash);
                    }
                    documentsType = documentType;
                    if (documentsType == null) {
                        documentsType = (SecureValueType) documentTypes.get(0);
                    }
                } else {
                    a++;
                }
            }
            if (documentsType == null) {
                documentsType = (SecureValueType) documentTypes.get(0);
            }
        }
        setTypeValue(type, text, json, documentsType, documentJson);
        return view;
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

    private boolean checkSecret(byte[] secret, Long id) {
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
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
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
                this.inputFields[0].setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            AndroidUtilities.shakeView(this.inputFields[0], 2.0f, 0);
        }
    }

    private void startPhoneVerification(boolean checkPermissions, final String phone, Runnable finishRunnable, ErrorRunnable errorRunnable, PassportActivityDelegate delegate) {
        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
        boolean allowCall = true;
        if (getParentActivity() != null && VERSION.SDK_INT >= 23 && simcardAvailable) {
            allowCall = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
            boolean allowSms = getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
            if (checkPermissions) {
                this.permissionsItems.clear();
                if (!allowCall) {
                    this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                }
                if (!allowSms) {
                    this.permissionsItems.add("android.permission.RECEIVE_SMS");
                    if (VERSION.SDK_INT >= 23) {
                        this.permissionsItems.add("android.permission.READ_SMS");
                    }
                }
                if (!this.permissionsItems.isEmpty()) {
                    if (getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        if (this.permissionsItems.size() == 2) {
                            builder.setMessage(LocaleController.getString("AllowReadCallAndSms", R.string.AllowReadCallAndSms));
                        } else if (allowSms) {
                            builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                        } else {
                            builder.setMessage(LocaleController.getString("AllowReadSms", R.string.AllowReadSms));
                        }
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
        final TL_account_sendVerifyPhoneCode req = new TL_account_sendVerifyPhoneCode();
        req.phone_number = phone;
        boolean z = simcardAvailable && allowCall;
        req.allow_flashcall = z;
        if (req.allow_flashcall) {
            try {
                String number = tm.getLine1Number();
                if (TextUtils.isEmpty(number)) {
                    req.current_number = false;
                } else {
                    z = phone.contains(number) || number.contains(phone);
                    req.current_number = z;
                    if (!req.current_number) {
                        req.allow_flashcall = false;
                    }
                }
            } catch (Throwable e) {
                req.allow_flashcall = false;
                FileLog.m3e(e);
            }
        }
        final PassportActivityDelegate passportActivityDelegate = delegate;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (error == null) {
                            HashMap values = new HashMap();
                            values.put("phone", phone);
                            PassportActivity activity = new PassportActivity(7, PassportActivity.this.currentForm, PassportActivity.this.currentPassword, PassportActivity.this.currentType, null, null, null, values);
                            activity.currentAccount = PassportActivity.this.currentAccount;
                            activity.saltedPassword = PassportActivity.this.saltedPassword;
                            activity.secureSecret = PassportActivity.this.secureSecret;
                            activity.delegate = passportActivityDelegate;
                            activity.currentPhoneVerification = (TL_auth_sentCode) response;
                            PassportActivity.this.presentFragment(activity, true);
                            return;
                        }
                        AlertsCreator.processError(PassportActivity.this.currentAccount, error, PassportActivity.this, req, phone);
                    }
                });
            }
        }, 2);
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
            } else if (this.currentPassword instanceof TL_account_noPassword) {
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
            } else {
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
                    this.inputFields[0].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                } else {
                    this.inputFields[0].setHint(this.currentPassword.hint);
                }
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
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
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
                animatorArr[0] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.acceptTextView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.acceptTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressViewButton, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressViewButton, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.acceptTextView, "alpha", new float[]{1.0f});
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
                this.errorsValues.remove("files_all");
                if (this.bottomCell != null && !TextUtils.isEmpty(this.noAllDocumentsErrorText)) {
                    this.bottomCell.setText(this.noAllDocumentsErrorText);
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
        } else {
            if (id == NotificationCenter.didSetTwoStepPassword) {
                if (args == null || args.length <= 0) {
                    this.currentPassword = new TL_account_noPassword();
                } else {
                    if (!(args[7] == null || this.inputFields[0] == null)) {
                        this.inputFields[0].setText((String) args[7]);
                    }
                    if (args[6] == null) {
                        this.currentPassword = new TL_account_password();
                        this.currentPassword.current_salt = (byte[]) args[1];
                        this.currentPassword.new_secure_salt = (byte[]) args[2];
                        this.currentPassword.secure_random = (byte[]) args[3];
                        this.currentPassword.has_recovery = !TextUtils.isEmpty((String) args[4]);
                        this.currentPassword.hint = (String) args[5];
                        if (this.inputFields[0] != null && this.inputFields[0].length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                }
                updatePasswordInterface();
            } else if (id != NotificationCenter.didRemovedTwoStepPassword) {
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    PassportActivity.this.presentFragment(PassportActivity.this.presentAfterAnimation, true);
                    PassportActivity.this.presentAfterAnimation = null;
                }
            });
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

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
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
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("PermissionNoAudioVideo", R.string.PermissionNoAudioVideo));
                    builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                        @TargetApi(9)
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                                PassportActivity.this.getParentActivity().startActivity(intent);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    });
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.show();
                }
            } else if (requestCode == 19 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
                processSelectedAttach(0);
            }
        } else if (this.currentActivityType == 3 && requestCode == 6) {
            startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
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
            this.views[this.currentViewNum].onBackPressed();
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

    protected void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType == 3 && VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new AlertDialog(getParentActivity(), 1);
            this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m3e(e);
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
        boolean z = false;
        int i = 1;
        if (getParentActivity() != null) {
            if (this.uploadingFileType != 0 || this.documents.size() < 20) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (this.uploadingFileType == 1) {
                    z = true;
                }
                chatAttachAlert.setOpenWithFrontFaceCamera(z);
                ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
                if (this.uploadingFileType == 0) {
                    i = 20 - this.documents.size();
                }
                chatAttachAlert2.setMaxSelectedPhotos(i);
                this.chatAttachAlert.loadGalleryPhotos();
                if (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22) {
                    AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
                }
                this.chatAttachAlert.init();
                showDialog(this.chatAttachAlert);
                return;
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", 20)));
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

    private void processSelectedAttach(int which) {
        int i = 1;
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
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return;
                }
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
        } else if (which == 1) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(false, false, false, null);
                fragment.setCurrentAccount(this.currentAccount);
                if (this.uploadingFileType == 0) {
                    i = 20 - this.documents.size();
                }
                fragment.setMaxSelectedPhotos(i);
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
                        } catch (Throwable e) {
                            FileLog.m3e(e);
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
                DocumentSelectActivity fragment2 = new DocumentSelectActivity();
                fragment2.setCurrentAccount(this.currentAccount);
                fragment2.setCanSelectOnlyImageFiles(true);
                if (this.uploadingFileType == 0) {
                    i = 20 - this.documents.size();
                }
                fragment2.setMaxSelectedFiles(i);
                fragment2.setDelegate(new DocumentSelectActivityDelegate() {
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
                        } catch (Throwable e) {
                            FileLog.m3e(e);
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
        int count = this.documents.size();
        for (a = 0; a < count; a++) {
            values.append(((SecureDocument) this.documents.get(a)).secureFile.id);
        }
        if (this.selfieDocument != null) {
            values.append(this.selfieDocument.secureFile.id);
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
        builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PassportActivity.this.finishFragment();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
        builder.setMessage(LocaleController.getString("PassportDiscardChanges", R.string.PassportDiscardChanges));
        showDialog(builder.create());
        return true;
    }

    private void processSelectedFiles(final ArrayList<SendingMediaInfo> photos) {
        if (!photos.isEmpty()) {
            boolean needRecoginze;
            if (this.uploadingFileType == 1) {
                needRecoginze = false;
            } else if (this.currentType instanceof TL_secureValueTypePersonalDetails) {
                boolean allFieldsAreEmpty = true;
                int a = 0;
                while (a < this.inputFields.length) {
                    if (a != 4 && a != 7 && a != 3 && a != 5 && this.inputFields[a].length() > 0) {
                        allFieldsAreEmpty = false;
                        break;
                    }
                    a++;
                }
                needRecoginze = allFieldsAreEmpty;
            } else {
                needRecoginze = false;
            }
            final int type = this.uploadingFileType;
            Utilities.globalQueue.postRunnable(new Runnable() {
                public void run() {
                    boolean didRecognizeSuccessfully = false;
                    int count = Math.min(PassportActivity.this.uploadingFileType == 0 ? 20 : 1, photos.size());
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
                                if (needRecoginze && !didRecognizeSuccessfully) {
                                    try {
                                        final Result result = MrzRecognizer.recognize(bitmap);
                                        if (result != null) {
                                            didRecognizeSuccessfully = true;
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    String country;
                                                    int count;
                                                    int a;
                                                    SecureValueType type;
                                                    if (result.type == 2) {
                                                        if (!(PassportActivity.this.currentDocumentsType instanceof TL_secureValueTypeIdentityCard)) {
                                                            count = PassportActivity.this.availableDocumentTypes.size();
                                                            for (a = 0; a < count; a++) {
                                                                type = (SecureValueType) PassportActivity.this.availableDocumentTypes.get(a);
                                                                if (type instanceof TL_secureValueTypeIdentityCard) {
                                                                    PassportActivity.this.currentDocumentsType = type;
                                                                    PassportActivity.this.updateInterfaceStringsForDocumentType();
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    } else if (result.type == 1) {
                                                        if (!(PassportActivity.this.currentDocumentsType instanceof TL_secureValueTypePassport)) {
                                                            count = PassportActivity.this.availableDocumentTypes.size();
                                                            for (a = 0; a < count; a++) {
                                                                type = (SecureValueType) PassportActivity.this.availableDocumentTypes.get(a);
                                                                if (type instanceof TL_secureValueTypePassport) {
                                                                    PassportActivity.this.currentDocumentsType = type;
                                                                    PassportActivity.this.updateInterfaceStringsForDocumentType();
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    } else if (result.type == 3 && !(PassportActivity.this.currentDocumentsType instanceof TL_secureValueTypeInternalPassport)) {
                                                        count = PassportActivity.this.availableDocumentTypes.size();
                                                        for (a = 0; a < count; a++) {
                                                            type = (SecureValueType) PassportActivity.this.availableDocumentTypes.get(a);
                                                            if (type instanceof TL_secureValueTypeInternalPassport) {
                                                                PassportActivity.this.currentDocumentsType = type;
                                                                PassportActivity.this.updateInterfaceStringsForDocumentType();
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (!TextUtils.isEmpty(result.firstName)) {
                                                        PassportActivity.this.inputFields[0].setText(result.firstName);
                                                    }
                                                    if (!TextUtils.isEmpty(result.lastName)) {
                                                        PassportActivity.this.inputFields[1].setText(result.lastName);
                                                    }
                                                    if (!TextUtils.isEmpty(result.number)) {
                                                        PassportActivity.this.inputFields[6].setText(result.number);
                                                    }
                                                    if (result.gender != 0) {
                                                        switch (result.gender) {
                                                            case 1:
                                                                PassportActivity.this.currentGender = "male";
                                                                PassportActivity.this.inputFields[3].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                                                                break;
                                                            case 2:
                                                                PassportActivity.this.currentGender = "female";
                                                                PassportActivity.this.inputFields[3].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                                                                break;
                                                        }
                                                    }
                                                    if (!TextUtils.isEmpty(result.nationality)) {
                                                        PassportActivity.this.currentCitizeship = result.nationality;
                                                        country = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentCitizeship);
                                                        if (country != null) {
                                                            PassportActivity.this.inputFields[4].setText(country);
                                                        }
                                                    }
                                                    if (!TextUtils.isEmpty(result.issuingCountry)) {
                                                        PassportActivity.this.currentResidence = result.issuingCountry;
                                                        country = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentResidence);
                                                        if (country != null) {
                                                            PassportActivity.this.inputFields[5].setText(country);
                                                        }
                                                    }
                                                    if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
                                                        PassportActivity.this.inputFields[2].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
                                                    }
                                                    if (result.expiryDay <= 0 || result.expiryMonth <= 0 || result.expiryYear <= 0) {
                                                        int[] access$4100 = PassportActivity.this.currentExpireDate;
                                                        int[] access$41002 = PassportActivity.this.currentExpireDate;
                                                        PassportActivity.this.currentExpireDate[2] = 0;
                                                        access$41002[1] = 0;
                                                        access$4100[0] = 0;
                                                        PassportActivity.this.inputFields[7].setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
                                                        return;
                                                    }
                                                    PassportActivity.this.currentExpireDate[0] = result.expiryYear;
                                                    PassportActivity.this.currentExpireDate[1] = result.expiryMonth;
                                                    PassportActivity.this.currentExpireDate[2] = result.expiryDay;
                                                    PassportActivity.this.inputFields[7].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.expiryDay), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)}));
                                                }
                                            });
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                }
                                final SecureDocument document = PassportActivity.this.delegate.saveFile(secureFile);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        SecureDocumentCell cell;
                                        if (PassportActivity.this.uploadingFileType == 1) {
                                            if (PassportActivity.this.selfieDocument != null) {
                                                cell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(PassportActivity.this.selfieDocument);
                                                if (cell != null) {
                                                    PassportActivity.this.selfieLayout.removeView(cell);
                                                }
                                                PassportActivity.this.selfieDocument = null;
                                            }
                                        } else if (PassportActivity.this.uploadingFileType == 2) {
                                            if (PassportActivity.this.frontDocument != null) {
                                                cell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(PassportActivity.this.frontDocument);
                                                if (cell != null) {
                                                    PassportActivity.this.frontLayout.removeView(cell);
                                                }
                                                PassportActivity.this.frontDocument = null;
                                            }
                                        } else if (PassportActivity.this.uploadingFileType == 3) {
                                            if (PassportActivity.this.reverseDocument != null) {
                                                cell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(PassportActivity.this.reverseDocument);
                                                if (cell != null) {
                                                    PassportActivity.this.reverseLayout.removeView(cell);
                                                }
                                                PassportActivity.this.reverseDocument = null;
                                            }
                                        } else if (PassportActivity.this.uploadingFileType == 0 && PassportActivity.this.documents.size() >= 20) {
                                            return;
                                        }
                                        PassportActivity.this.uploadingDocuments.put(document.path, document);
                                        PassportActivity.this.doneItem.setEnabled(false);
                                        PassportActivity.this.doneItem.setAlpha(0.5f);
                                        FileLoader.getInstance(PassportActivity.this.currentAccount).uploadFile(document.path, false, true, 16777216);
                                        PassportActivity.this.addDocumentView(document, type);
                                        PassportActivity.this.updateUploadText(type);
                                    }
                                });
                            }
                        }
                    }
                    SharedConfig.saveConfig();
                }
            });
        }
    }

    public void setNeedActivityResult(boolean needActivityResult) {
        this.needActivityResult = needActivityResult;
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
        arrayList.add(new ThemeDescription(this.extraBackgroundView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        for (a = 0; a < this.dividers.size(); a++) {
            arrayList.add(new ThemeDescription((View) this.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        }
        for (Entry<SecureDocument, SecureDocumentCell> entry : this.documentsCells.entrySet()) {
            View cell = (SecureDocumentCell) entry.getValue();
            arrayList.add(new ThemeDescription(cell, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(cell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(cell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        }
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, null, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, null, null, null, Theme.key_featuredStickers_addedIcon));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        if (this.inputFields != null) {
            for (a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_CURSORCOLOR | ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteRedText3));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteRedText3));
        }
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.noPasswordImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_messagePanelIcons));
        arrayList.add(new ThemeDescription(this.noPasswordTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.noPasswordSetTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText5));
        arrayList.add(new ThemeDescription(this.passwordForgotButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.plusTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.acceptTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_passport_authorizeText));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackground));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackgroundSelected));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_sessions_devicesImage));
        arrayList.add(new ThemeDescription(this.emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.emptyTextView3, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
