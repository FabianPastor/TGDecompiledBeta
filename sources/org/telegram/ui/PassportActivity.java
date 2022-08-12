package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.PushListenerController;
import org.telegram.messenger.R;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SecureDocumentKey;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputSecureFile;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$SecureFile;
import org.telegram.tgnet.TLRPC$SecurePlainData;
import org.telegram.tgnet.TLRPC$SecureRequiredType;
import org.telegram.tgnet.TLRPC$SecureValueType;
import org.telegram.tgnet.TLRPC$TL_account_acceptAuthorization;
import org.telegram.tgnet.TLRPC$TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_deleteSecureValue;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC$TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC$TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC$TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC$TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCode;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC$TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC$TL_codeSettings;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_getPassportConfig;
import org.telegram.tgnet.TLRPC$TL_help_passportConfig;
import org.telegram.tgnet.TLRPC$TL_inputFile;
import org.telegram.tgnet.TLRPC$TL_inputSecureFile;
import org.telegram.tgnet.TLRPC$TL_inputSecureFileUploaded;
import org.telegram.tgnet.TLRPC$TL_secureCredentialsEncrypted;
import org.telegram.tgnet.TLRPC$TL_secureData;
import org.telegram.tgnet.TLRPC$TL_secureFile;
import org.telegram.tgnet.TLRPC$TL_securePlainEmail;
import org.telegram.tgnet.TLRPC$TL_securePlainPhone;
import org.telegram.tgnet.TLRPC$TL_secureRequiredType;
import org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf;
import org.telegram.tgnet.TLRPC$TL_secureValue;
import org.telegram.tgnet.TLRPC$TL_secureValueHash;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeBankStatement;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePassport;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePassportRegistration;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePhone;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeRentalAgreement;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeTemporaryRegistration;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeUtilityBill;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$auth_CodeType;
import org.telegram.tgnet.TLRPC$auth_SentCodeType;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
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
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.PhotoViewer;

public class PassportActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public TextView acceptTextView;
    private TextSettingsCell addDocumentCell;
    private ShadowSectionCell addDocumentSectionCell;
    /* access modifiers changed from: private */
    public boolean allowNonLatinName;
    private ArrayList<TLRPC$TL_secureRequiredType> availableDocumentTypes;
    private TextInfoPrivacyCell bottomCell;
    private TextInfoPrivacyCell bottomCellTranslation;
    private FrameLayout bottomLayout;
    private boolean callbackCalled;
    /* access modifiers changed from: private */
    public ChatAttachAlert chatAttachAlert;
    /* access modifiers changed from: private */
    public HashMap<String, String> codesMap;
    /* access modifiers changed from: private */
    public ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    /* access modifiers changed from: private */
    public int currentActivityType;
    /* access modifiers changed from: private */
    public long currentBotId;
    /* access modifiers changed from: private */
    public String currentCallbackUrl;
    /* access modifiers changed from: private */
    public String currentCitizeship;
    /* access modifiers changed from: private */
    public HashMap<String, String> currentDocumentValues;
    /* access modifiers changed from: private */
    public TLRPC$TL_secureRequiredType currentDocumentsType;
    private TLRPC$TL_secureValue currentDocumentsTypeValue;
    /* access modifiers changed from: private */
    public String currentEmail;
    /* access modifiers changed from: private */
    public int[] currentExpireDate;
    /* access modifiers changed from: private */
    public TLRPC$TL_account_authorizationForm currentForm;
    /* access modifiers changed from: private */
    public String currentGender;
    /* access modifiers changed from: private */
    public String currentNonce;
    /* access modifiers changed from: private */
    public TLRPC$TL_account_password currentPassword;
    /* access modifiers changed from: private */
    public String currentPayload;
    private TLRPC$TL_auth_sentCode currentPhoneVerification;
    /* access modifiers changed from: private */
    public LinearLayout currentPhotoViewerLayout;
    private String currentPicturePath;
    /* access modifiers changed from: private */
    public String currentPublicKey;
    /* access modifiers changed from: private */
    public String currentResidence;
    /* access modifiers changed from: private */
    public String currentScope;
    /* access modifiers changed from: private */
    public TLRPC$TL_secureRequiredType currentType;
    private TLRPC$TL_secureValue currentTypeValue;
    /* access modifiers changed from: private */
    public HashMap<String, String> currentValues;
    /* access modifiers changed from: private */
    public int currentViewNum;
    /* access modifiers changed from: private */
    public PassportActivityDelegate delegate;
    private TextSettingsCell deletePassportCell;
    private ArrayList<View> dividers;
    /* access modifiers changed from: private */
    public boolean documentOnly;
    /* access modifiers changed from: private */
    public ArrayList<SecureDocument> documents;
    /* access modifiers changed from: private */
    public HashMap<SecureDocument, SecureDocumentCell> documentsCells;
    /* access modifiers changed from: private */
    public HashMap<String, String> documentsErrors;
    private LinearLayout documentsLayout;
    private HashMap<TLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType> documentsToTypesLink;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public int emailCodeLength;
    private ImageView emptyImageView;
    private LinearLayout emptyLayout;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private TextView emptyTextView3;
    private EmptyTextProgressView emptyView;
    private HashMap<String, HashMap<String, String>> errorsMap;
    /* access modifiers changed from: private */
    public HashMap<String, String> errorsValues;
    private View extraBackgroundView;
    private View extraBackgroundView2;
    /* access modifiers changed from: private */
    public HashMap<String, String> fieldsErrors;
    /* access modifiers changed from: private */
    public SecureDocument frontDocument;
    private LinearLayout frontLayout;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnFailure;
    /* access modifiers changed from: private */
    public boolean ignoreOnPhoneChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private String initialValues;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputExtraFields;
    /* access modifiers changed from: private */
    public ViewGroup[] inputFieldContainers;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputFields;
    /* access modifiers changed from: private */
    public HashMap<String, String> languageMap;
    private LinearLayout linearLayout2;
    private HashMap<String, String> mainErrorsMap;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell nativeInfoCell;
    /* access modifiers changed from: private */
    public boolean needActivityResult;
    private CharSequence noAllDocumentsErrorText;
    private CharSequence noAllTranslationErrorText;
    private ImageView noPasswordImageView;
    private TextView noPasswordSetTextView;
    private TextView noPasswordTextView;
    /* access modifiers changed from: private */
    public boolean[] nonLatinNames;
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
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap;
    private TextView plusTextView;
    /* access modifiers changed from: private */
    public PassportActivity presentAfterAnimation;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public ContextProgressView progressViewButton;
    private PhotoViewer.PhotoViewerProvider provider;
    /* access modifiers changed from: private */
    public SecureDocument reverseDocument;
    /* access modifiers changed from: private */
    public LinearLayout reverseLayout;
    /* access modifiers changed from: private */
    public byte[] saltedPassword;
    private byte[] savedPasswordHash;
    /* access modifiers changed from: private */
    public byte[] savedSaltedPassword;
    private TextSettingsCell scanDocumentCell;
    /* access modifiers changed from: private */
    public int scrollHeight;
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private ShadowSectionCell sectionCell2;
    /* access modifiers changed from: private */
    public byte[] secureSecret;
    /* access modifiers changed from: private */
    public long secureSecretId;
    /* access modifiers changed from: private */
    public SecureDocument selfieDocument;
    private LinearLayout selfieLayout;
    private TextInfoPrivacyCell topErrorCell;
    /* access modifiers changed from: private */
    public ArrayList<SecureDocument> translationDocuments;
    private LinearLayout translationLayout;
    private HashMap<TLRPC$TL_secureRequiredType, HashMap<String, String>> typesValues;
    private HashMap<TLRPC$TL_secureRequiredType, TextDetailSecureCell> typesViews;
    private TextSettingsCell uploadDocumentCell;
    private TextDetailSettingsCell uploadFrontCell;
    private TextDetailSettingsCell uploadReverseCell;
    private TextDetailSettingsCell uploadSelfieCell;
    private TextSettingsCell uploadTranslationCell;
    /* access modifiers changed from: private */
    public HashMap<String, SecureDocument> uploadingDocuments;
    /* access modifiers changed from: private */
    public int uploadingFileType;
    /* access modifiers changed from: private */
    public boolean useCurrentValue;
    /* access modifiers changed from: private */
    public int usingSavedPassword;
    /* access modifiers changed from: private */
    public SlideView[] views;

    private interface ErrorRunnable {
        void onError(String str, String str2);
    }

    private interface PassportActivityDelegate {
        void deleteValue(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, ArrayList<TLRPC$TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TLRPC$TL_secureFile tLRPC$TL_secureFile);

        void saveValue(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, String str, String str2, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, ArrayList<SecureDocument> arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
    }

    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(true);
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }

        public void onClick(View view) {
            Browser.openUrl((Context) PassportActivity.this.getParentActivity(), PassportActivity.this.currentForm.privacy_policy_url);
        }
    }

    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        /* access modifiers changed from: private */
        public TextView valueTextView;

        public TextDetailSecureCell(PassportActivity passportActivity, Context context) {
            super(context);
            int i = 21;
            int i2 = passportActivity.currentActivityType == 8 ? 21 : 51;
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i3 = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = this.textView;
            boolean z = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 48, (float) (z ? i2 : 21), 10.0f, (float) (z ? 21 : i2), 0.0f));
            TextView textView4 = new TextView(context);
            this.valueTextView = textView4;
            textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            TextView textView5 = this.valueTextView;
            boolean z2 = LocaleController.isRTL;
            addView(textView5, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, (float) (z2 ? i2 : 21), 35.0f, (float) (!z2 ? i2 : i), 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImageView = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
            this.checkImageView.setImageResource(R.drawable.sticker_added);
            addView(this.checkImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i3) | 48, 21.0f, 25.0f, 21.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        public void setTextAndValue(String str, CharSequence charSequence, boolean z) {
            this.textView.setText(str);
            this.valueTextView.setText(charSequence);
            this.needDivider = z;
            setWillNotDraw(!z);
        }

        public void setChecked(boolean z) {
            this.checkImageView.setVisibility(z ? 0 : 4);
        }

        public void setValue(CharSequence charSequence) {
            this.valueTextView.setText(charSequence);
        }

        public void setNeedDivider(boolean z) {
            this.needDivider = z;
            setWillNotDraw(!z);
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public class SecureDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private int buttonState;
        private SecureDocument currentSecureDocument;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        private RadialProgress radialProgress = new RadialProgress(this);
        private TextView textView;
        /* access modifiers changed from: private */
        public TextView valueTextView;

        public SecureDocumentCell(Context context) {
            super(context);
            this.TAG = DownloadController.getInstance(PassportActivity.this.currentAccount).generateObserverTag();
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            int i = 5;
            addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 8.0f, 21.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = this.textView;
            boolean z = LocaleController.isRTL;
            int i2 = 21;
            addView(textView3, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 48, (float) (z ? 21 : 81), 10.0f, (float) (z ? 81 : 21), 0.0f));
            TextView textView4 = new TextView(context);
            this.valueTextView = textView4;
            textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            TextView textView5 = this.valueTextView;
            boolean z2 = LocaleController.isRTL;
            addView(textView5, LayoutHelper.createFrame(-2, -2.0f, (!z2 ? 3 : i) | 48, (float) (z2 ? 21 : 81), 35.0f, (float) (z2 ? 81 : i2), 0.0f));
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int left = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            int top = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(left, top, AndroidUtilities.dp(24.0f) + left, AndroidUtilities.dp(24.0f) + top);
        }

        /* access modifiers changed from: protected */
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
            boolean exists = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(this.currentSecureDocument).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setBackground((Drawable) null, false, false);
                return;
            }
            SecureDocument secureDocument = this.currentSecureDocument;
            float f = 0.0f;
            if (secureDocument.path != null) {
                if (secureDocument.inputFile != null) {
                    DownloadController.getInstance(PassportActivity.this.currentAccount).removeLoadingFileObserver(this);
                    this.radialProgress.setBackground((Drawable) null, false, z);
                    this.buttonState = -1;
                    return;
                }
                DownloadController.getInstance(PassportActivity.this.currentAccount).addLoadingFileObserver(this.currentSecureDocument.path, this);
                this.buttonState = 1;
                Float fileProgress = ImageLoader.getInstance().getFileProgress(this.currentSecureDocument.path);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, z);
                RadialProgress radialProgress2 = this.radialProgress;
                if (fileProgress != null) {
                    f = fileProgress.floatValue();
                }
                radialProgress2.setProgress(f, false);
                invalidate();
            } else if (exists) {
                DownloadController.getInstance(PassportActivity.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground((Drawable) null, false, z);
                invalidate();
            } else {
                DownloadController.getInstance(PassportActivity.this.currentAccount).addLoadingFileObserver(attachFileName, this);
                this.buttonState = 1;
                Float fileProgress2 = ImageLoader.getInstance().getFileProgress(attachFileName);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, z);
                RadialProgress radialProgress3 = this.radialProgress;
                if (fileProgress2 != null) {
                    f = fileProgress2.floatValue();
                }
                radialProgress3.setProgress(f, z);
                invalidate();
            }
        }

        public void invalidate() {
            super.invalidate();
            this.textView.invalidate();
        }

        /* access modifiers changed from: protected */
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

        public void onProgressDownload(String str, long j, long j2) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        public void onProgressUpload(String str, long j, long j2, boolean z) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    /* JADX WARNING: type inference failed for: r6v3, types: [java.lang.String] */
    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0148 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x015a A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x015f A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0164 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0170 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x017a A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0184 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x018e A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0198 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01a2 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01ab A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01b9 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01bb A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01c0 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01d3 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01e6 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0201 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0214 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x022f A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PassportActivity(int r20, long r21, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, org.telegram.tgnet.TLRPC$TL_account_authorizationForm r28, org.telegram.tgnet.TLRPC$TL_account_password r29) {
        /*
            r19 = this;
            r10 = r19
            r11 = r28
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r0 = r19
            r1 = r20
            r2 = r28
            r3 = r29
            r0.<init>((int) r1, (org.telegram.tgnet.TLRPC$TL_account_authorizationForm) r2, (org.telegram.tgnet.TLRPC$TL_account_password) r3, (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r4, (org.telegram.tgnet.TLRPC$TL_secureValue) r5, (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r6, (org.telegram.tgnet.TLRPC$TL_secureValue) r7, (java.util.HashMap<java.lang.String, java.lang.String>) r8, (java.util.HashMap<java.lang.String, java.lang.String>) r9)
            r0 = r21
            r10.currentBotId = r0
            r0 = r25
            r10.currentPayload = r0
            r0 = r26
            r10.currentNonce = r0
            r0 = r23
            r10.currentScope = r0
            r0 = r24
            r10.currentPublicKey = r0
            r0 = r27
            r10.currentCallbackUrl = r0
            if (r20 != 0) goto L_0x023d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueError> r0 = r11.errors
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x023d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueError> r0 = r11.errors     // Catch:{ Exception -> 0x023d }
            org.telegram.ui.PassportActivity$2 r1 = new org.telegram.ui.PassportActivity$2     // Catch:{ Exception -> 0x023d }
            r1.<init>()     // Catch:{ Exception -> 0x023d }
            java.util.Collections.sort(r0, r1)     // Catch:{ Exception -> 0x023d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueError> r0 = r11.errors     // Catch:{ Exception -> 0x023d }
            int r0 = r0.size()     // Catch:{ Exception -> 0x023d }
            r2 = 0
        L_0x0048:
            if (r2 >= r0) goto L_0x023d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueError> r3 = r11.errors     // Catch:{ Exception -> 0x023d }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueError r3 = (org.telegram.tgnet.TLRPC$SecureValueError) r3     // Catch:{ Exception -> 0x023d }
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorFrontSide     // Catch:{ Exception -> 0x023d }
            java.lang.String r5 = "data"
            r6 = 0
            java.lang.String r7 = "error_all"
            java.lang.String r8 = "selfie"
            java.lang.String r9 = "reverse"
            java.lang.String r12 = "front"
            java.lang.String r14 = "files"
            java.lang.String r15 = "translation"
            if (r4 == 0) goto L_0x007a
            org.telegram.tgnet.TLRPC$TL_secureValueErrorFrontSide r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorFrontSide) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r4 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r4)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.file_hash     // Catch:{ Exception -> 0x023d }
            r21 = r12
        L_0x0073:
            r18 = r6
            r6 = r3
            r3 = r18
            goto L_0x013e
        L_0x007a:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorReverseSide     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$TL_secureValueErrorReverseSide r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorReverseSide) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.file_hash     // Catch:{ Exception -> 0x023d }
            r21 = r9
            goto L_0x0073
        L_0x008d:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorSelfie     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x00a0
            org.telegram.tgnet.TLRPC$TL_secureValueErrorSelfie r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorSelfie) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.file_hash     // Catch:{ Exception -> 0x023d }
            r21 = r8
            goto L_0x0073
        L_0x00a0:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorTranslationFile     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x00b3
            org.telegram.tgnet.TLRPC$TL_secureValueErrorTranslationFile r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorTranslationFile) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.file_hash     // Catch:{ Exception -> 0x023d }
            r21 = r15
            goto L_0x0073
        L_0x00b3:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorTranslationFiles     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$TL_secureValueErrorTranslationFiles r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorTranslationFiles) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            r3 = r6
            r21 = r15
            goto L_0x013e
        L_0x00c6:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorFile     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x00d9
            org.telegram.tgnet.TLRPC$TL_secureValueErrorFile r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorFile) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.file_hash     // Catch:{ Exception -> 0x023d }
            r21 = r14
            goto L_0x0073
        L_0x00d9:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorFiles     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x00eb
            org.telegram.tgnet.TLRPC$TL_secureValueErrorFiles r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorFiles) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            r3 = r6
            r21 = r14
            goto L_0x013e
        L_0x00eb:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueError     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x00ff
            org.telegram.tgnet.TLRPC$TL_secureValueError r3 = (org.telegram.tgnet.TLRPC$TL_secureValueError) r3     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.hash     // Catch:{ Exception -> 0x023d }
            r21 = r7
            goto L_0x0073
        L_0x00ff:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueErrorData     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x0235
            org.telegram.tgnet.TLRPC$TL_secureValueErrorData r3 = (org.telegram.tgnet.TLRPC$TL_secureValueErrorData) r3     // Catch:{ Exception -> 0x023d }
            r1 = 0
        L_0x0106:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_secureValue> r4 = r11.values     // Catch:{ Exception -> 0x023d }
            int r4 = r4.size()     // Catch:{ Exception -> 0x023d }
            if (r1 >= r4) goto L_0x0129
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_secureValue> r4 = r11.values     // Catch:{ Exception -> 0x023d }
            java.lang.Object r4 = r4.get(r1)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$TL_secureValue r4 = (org.telegram.tgnet.TLRPC$TL_secureValue) r4     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$TL_secureData r4 = r4.data     // Catch:{ Exception -> 0x023d }
            if (r4 == 0) goto L_0x0126
            byte[] r4 = r4.data_hash     // Catch:{ Exception -> 0x023d }
            byte[] r6 = r3.data_hash     // Catch:{ Exception -> 0x023d }
            boolean r4 = java.util.Arrays.equals(r4, r6)     // Catch:{ Exception -> 0x023d }
            if (r4 == 0) goto L_0x0126
            r1 = 1
            goto L_0x012a
        L_0x0126:
            int r1 = r1 + 1
            goto L_0x0106
        L_0x0129:
            r1 = 0
        L_0x012a:
            if (r1 != 0) goto L_0x012e
            goto L_0x0235
        L_0x012e:
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r3.type     // Catch:{ Exception -> 0x023d }
            java.lang.String r4 = r10.getNameForType(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r3.text     // Catch:{ Exception -> 0x023d }
            java.lang.String r6 = r3.field     // Catch:{ Exception -> 0x023d }
            byte[] r3 = r3.data_hash     // Catch:{ Exception -> 0x023d }
            r21 = r5
            goto L_0x0073
        L_0x013e:
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r13 = r10.errorsMap     // Catch:{ Exception -> 0x023d }
            java.lang.Object r13 = r13.get(r4)     // Catch:{ Exception -> 0x023d }
            java.util.HashMap r13 = (java.util.HashMap) r13     // Catch:{ Exception -> 0x023d }
            if (r13 != 0) goto L_0x015a
            java.util.HashMap r13 = new java.util.HashMap     // Catch:{ Exception -> 0x023d }
            r13.<init>()     // Catch:{ Exception -> 0x023d }
            r23 = r0
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r0 = r10.errorsMap     // Catch:{ Exception -> 0x023d }
            r0.put(r4, r13)     // Catch:{ Exception -> 0x023d }
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r10.mainErrorsMap     // Catch:{ Exception -> 0x023d }
            r0.put(r4, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x015c
        L_0x015a:
            r23 = r0
        L_0x015c:
            r0 = 2
            if (r6 == 0) goto L_0x0164
            java.lang.String r4 = android.util.Base64.encodeToString(r6, r0)     // Catch:{ Exception -> 0x023d }
            goto L_0x0166
        L_0x0164:
            java.lang.String r4 = ""
        L_0x0166:
            r16 = -1
            int r17 = r21.hashCode()     // Catch:{ Exception -> 0x023d }
            switch(r17) {
                case -1840647503: goto L_0x01ab;
                case -906020504: goto L_0x01a2;
                case 3076010: goto L_0x0198;
                case 97434231: goto L_0x018e;
                case 97705513: goto L_0x0184;
                case 329856746: goto L_0x017a;
                case 1099846370: goto L_0x0170;
                default: goto L_0x016f;
            }     // Catch:{ Exception -> 0x023d }
        L_0x016f:
            goto L_0x01b5
        L_0x0170:
            r0 = r21
            boolean r0 = r0.equals(r9)     // Catch:{ Exception -> 0x023d }
            if (r0 == 0) goto L_0x01b5
            r0 = 5
            goto L_0x01b6
        L_0x017a:
            r0 = r21
            boolean r0 = r0.equals(r7)     // Catch:{ Exception -> 0x023d }
            if (r0 == 0) goto L_0x01b5
            r0 = 6
            goto L_0x01b6
        L_0x0184:
            r0 = r21
            boolean r0 = r0.equals(r12)     // Catch:{ Exception -> 0x023d }
            if (r0 == 0) goto L_0x01b5
            r0 = 4
            goto L_0x01b6
        L_0x018e:
            r0 = r21
            boolean r0 = r0.equals(r14)     // Catch:{ Exception -> 0x023d }
            if (r0 == 0) goto L_0x01b5
            r0 = 1
            goto L_0x01b6
        L_0x0198:
            r0 = r21
            boolean r0 = r0.equals(r5)     // Catch:{ Exception -> 0x023d }
            if (r0 == 0) goto L_0x01b5
            r0 = 0
            goto L_0x01b6
        L_0x01a2:
            r5 = r21
            boolean r5 = r5.equals(r8)     // Catch:{ Exception -> 0x023d }
            if (r5 == 0) goto L_0x01b5
            goto L_0x01b6
        L_0x01ab:
            r5 = r21
            boolean r0 = r5.equals(r15)     // Catch:{ Exception -> 0x023d }
            if (r0 == 0) goto L_0x01b5
            r0 = 3
            goto L_0x01b6
        L_0x01b5:
            r0 = -1
        L_0x01b6:
            switch(r0) {
                case 0: goto L_0x022f;
                case 1: goto L_0x0214;
                case 2: goto L_0x0201;
                case 3: goto L_0x01e6;
                case 4: goto L_0x01d3;
                case 5: goto L_0x01c0;
                case 6: goto L_0x01bb;
                default: goto L_0x01b9;
            }     // Catch:{ Exception -> 0x023d }
        L_0x01b9:
            goto L_0x0237
        L_0x01bb:
            r13.put(r7, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x01c0:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023d }
            r0.<init>()     // Catch:{ Exception -> 0x023d }
            r0.append(r9)     // Catch:{ Exception -> 0x023d }
            r0.append(r4)     // Catch:{ Exception -> 0x023d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023d }
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x01d3:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023d }
            r0.<init>()     // Catch:{ Exception -> 0x023d }
            r0.append(r12)     // Catch:{ Exception -> 0x023d }
            r0.append(r4)     // Catch:{ Exception -> 0x023d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023d }
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x01e6:
            if (r6 == 0) goto L_0x01fb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023d }
            r0.<init>()     // Catch:{ Exception -> 0x023d }
            r0.append(r15)     // Catch:{ Exception -> 0x023d }
            r0.append(r4)     // Catch:{ Exception -> 0x023d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023d }
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x01fb:
            java.lang.String r0 = "translation_all"
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x0201:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023d }
            r0.<init>()     // Catch:{ Exception -> 0x023d }
            r0.append(r8)     // Catch:{ Exception -> 0x023d }
            r0.append(r4)     // Catch:{ Exception -> 0x023d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023d }
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x0214:
            if (r6 == 0) goto L_0x0229
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023d }
            r0.<init>()     // Catch:{ Exception -> 0x023d }
            r0.append(r14)     // Catch:{ Exception -> 0x023d }
            r0.append(r4)     // Catch:{ Exception -> 0x023d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023d }
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x0229:
            java.lang.String r0 = "files_all"
            r13.put(r0, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x022f:
            if (r3 == 0) goto L_0x0237
            r13.put(r3, r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x0237
        L_0x0235:
            r23 = r0
        L_0x0237:
            int r2 = r2 + 1
            r0 = r23
            goto L_0x0048
        L_0x023d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.<init>(int, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_account_authorizationForm, org.telegram.tgnet.TLRPC$TL_account_password):void");
    }

    public PassportActivity(int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_password tLRPC$TL_account_password, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureValue tLRPC$TL_secureValue, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, TLRPC$TL_secureValue tLRPC$TL_secureValue2, HashMap<String, String> hashMap, HashMap<String, String> hashMap2) {
        this.currentCitizeship = "";
        this.currentResidence = "";
        this.currentExpireDate = new int[3];
        this.dividers = new ArrayList<>();
        this.nonLatinNames = new boolean[3];
        this.allowNonLatinName = true;
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.documents = new ArrayList<>();
        this.translationDocuments = new ArrayList<>();
        this.documentsCells = new HashMap<>();
        this.uploadingDocuments = new HashMap<>();
        this.typesValues = new HashMap<>();
        this.typesViews = new HashMap<>();
        this.documentsToTypesLink = new HashMap<>();
        this.errorsMap = new HashMap<>();
        this.mainErrorsMap = new HashMap<>();
        this.errorsValues = new HashMap<>();
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
                if (i < 0 || i >= PassportActivity.this.currentPhotoViewerLayout.getChildCount()) {
                    return null;
                }
                SecureDocumentCell secureDocumentCell = (SecureDocumentCell) PassportActivity.this.currentPhotoViewerLayout.getChildAt(i);
                int[] iArr = new int[2];
                secureDocumentCell.imageView.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                int i2 = 0;
                placeProviderObject.viewX = iArr[0];
                int i3 = iArr[1];
                if (Build.VERSION.SDK_INT < 21) {
                    i2 = AndroidUtilities.statusBarHeight;
                }
                placeProviderObject.viewY = i3 - i2;
                placeProviderObject.parentView = PassportActivity.this.currentPhotoViewerLayout;
                ImageReceiver imageReceiver = secureDocumentCell.imageView.getImageReceiver();
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                return placeProviderObject;
            }

            public void deleteImageAtIndex(int i) {
                SecureDocument secureDocument;
                if (PassportActivity.this.uploadingFileType == 1) {
                    secureDocument = PassportActivity.this.selfieDocument;
                } else if (PassportActivity.this.uploadingFileType == 4) {
                    secureDocument = (SecureDocument) PassportActivity.this.translationDocuments.get(i);
                } else if (PassportActivity.this.uploadingFileType == 2) {
                    secureDocument = PassportActivity.this.frontDocument;
                } else if (PassportActivity.this.uploadingFileType == 3) {
                    secureDocument = PassportActivity.this.reverseDocument;
                } else {
                    secureDocument = (SecureDocument) PassportActivity.this.documents.get(i);
                }
                SecureDocumentCell secureDocumentCell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(secureDocument);
                if (secureDocumentCell != null) {
                    String access$900 = PassportActivity.this.getDocumentHash(secureDocument);
                    String str = null;
                    if (PassportActivity.this.uploadingFileType == 1) {
                        SecureDocument unused = PassportActivity.this.selfieDocument = null;
                        str = "selfie" + access$900;
                    } else if (PassportActivity.this.uploadingFileType == 4) {
                        str = "translation" + access$900;
                    } else if (PassportActivity.this.uploadingFileType == 2) {
                        SecureDocument unused2 = PassportActivity.this.frontDocument = null;
                        str = "front" + access$900;
                    } else if (PassportActivity.this.uploadingFileType == 3) {
                        SecureDocument unused3 = PassportActivity.this.reverseDocument = null;
                        str = "reverse" + access$900;
                    } else if (PassportActivity.this.uploadingFileType == 0) {
                        str = "files" + access$900;
                    }
                    if (str != null) {
                        if (PassportActivity.this.documentsErrors != null) {
                            PassportActivity.this.documentsErrors.remove(str);
                        }
                        if (PassportActivity.this.errorsValues != null) {
                            PassportActivity.this.errorsValues.remove(str);
                        }
                    }
                    PassportActivity passportActivity = PassportActivity.this;
                    passportActivity.updateUploadText(passportActivity.uploadingFileType);
                    PassportActivity.this.currentPhotoViewerLayout.removeView(secureDocumentCell);
                }
            }

            public String getDeleteMessageString() {
                if (PassportActivity.this.uploadingFileType == 1) {
                    return LocaleController.formatString("PassportDeleteSelfieAlert", R.string.PassportDeleteSelfieAlert, new Object[0]);
                }
                return LocaleController.formatString("PassportDeleteScanAlert", R.string.PassportDeleteScanAlert, new Object[0]);
            }
        };
        this.currentActivityType = i;
        this.currentForm = tLRPC$TL_account_authorizationForm;
        this.currentType = tLRPC$TL_secureRequiredType;
        if (tLRPC$TL_secureRequiredType != null) {
            this.allowNonLatinName = tLRPC$TL_secureRequiredType.native_names;
        }
        this.currentTypeValue = tLRPC$TL_secureValue;
        this.currentDocumentsType = tLRPC$TL_secureRequiredType2;
        this.currentDocumentsTypeValue = tLRPC$TL_secureValue2;
        this.currentPassword = tLRPC$TL_account_password;
        this.currentValues = hashMap;
        this.currentDocumentValues = hashMap2;
        if (i == 3) {
            this.permissionsItems = new ArrayList<>();
        } else if (i == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap<>();
        }
        if (this.currentDocumentValues == null) {
            this.currentDocumentValues = new HashMap<>();
        }
        if (i == 5) {
            if (!(UserConfig.getInstance(this.currentAccount).savedPasswordHash == null || UserConfig.getInstance(this.currentAccount).savedSaltedPassword == null)) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
            if (tLRPC$TL_account_password2 == null) {
                loadPasswordInfo();
            } else {
                TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password2);
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            }
            if (!SharedConfig.isPassportConfigLoaded()) {
                TLRPC$TL_help_getPassportConfig tLRPC$TL_help_getPassportConfig = new TLRPC$TL_help_getPassportConfig();
                tLRPC$TL_help_getPassportConfig.hash = SharedConfig.passportConfigHash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_help_getPassportConfig, PassportActivity$$ExternalSyntheticLambda68.INSTANCE);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_help_passportConfig) {
            TLRPC$TL_help_passportConfig tLRPC$TL_help_passportConfig = (TLRPC$TL_help_passportConfig) tLObject;
            SharedConfig.setPassportConfig(tLRPC$TL_help_passportConfig.countries_langs.data, tLRPC$TL_help_passportConfig.hash);
            return;
        }
        SharedConfig.getCountryLangs();
    }

    public void onResume() {
        ViewGroup[] viewGroupArr;
        super.onResume();
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onResume();
        }
        if (this.currentActivityType == 5 && (viewGroupArr = this.inputFieldContainers) != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
            AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda50(this), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$2() {
        ViewGroup[] viewGroupArr = this.inputFieldContainers;
        if (viewGroupArr != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public void onPause() {
        super.onPause();
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onPause();
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        int i = 0;
        callCallback(false);
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.dismissInternal();
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
                    FileLog.e((Throwable) e);
                }
                this.progressDialog = null;
            }
        }
    }

    public View createView(Context context) {
        ChatAttachAlert chatAttachAlert2;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            /* JADX WARNING: Removed duplicated region for block: B:80:0x0362  */
            /* JADX WARNING: Removed duplicated region for block: B:83:0x0373  */
            /* JADX WARNING: Removed duplicated region for block: B:86:0x038b  */
            /* JADX WARNING: Removed duplicated region for block: B:87:0x0391  */
            /* JADX WARNING: Removed duplicated region for block: B:90:0x039a  */
            /* JADX WARNING: Removed duplicated region for block: B:91:0x03a1  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private boolean onIdentityDone(java.lang.Runnable r26, org.telegram.ui.PassportActivity.ErrorRunnable r27) {
                /*
                    r25 = this;
                    r7 = r25
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r0 = r0.uploadingDocuments
                    boolean r0 = r0.isEmpty()
                    r8 = 0
                    if (r0 == 0) goto L_0x03db
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean r0 = r0.checkFieldsForError()
                    if (r0 == 0) goto L_0x0019
                    goto L_0x03db
                L_0x0019:
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean r0 = r0.allowNonLatinName
                    r9 = 3
                    r10 = 2
                    r11 = 1
                    if (r0 == 0) goto L_0x0162
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean unused = r0.allowNonLatinName = r8
                    r0 = 0
                    r12 = 0
                L_0x002b:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    boolean[] r1 = r1.nonLatinNames
                    int r1 = r1.length
                    if (r12 >= r1) goto L_0x015f
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    boolean[] r1 = r1.nonLatinNames
                    boolean r1 = r1[r12]
                    if (r1 == 0) goto L_0x015b
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r1 = r1.inputFields
                    r1 = r1[r12]
                    int r2 = org.telegram.messenger.R.string.PassportUseLatinOnly
                    java.lang.String r3 = "PassportUseLatinOnly"
                    java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                    r1.setErrorText(r2)
                    if (r0 != 0) goto L_0x015b
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean[] r0 = r0.nonLatinNames
                    boolean r0 = r0[r8]
                    if (r0 == 0) goto L_0x0072
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r1 = r0.inputExtraFields
                    r1 = r1[r8]
                    android.text.Editable r1 = r1.getText()
                    java.lang.String r1 = r1.toString()
                    java.lang.String r0 = r0.getTranslitString(r1)
                    goto L_0x0082
                L_0x0072:
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r0 = r0.inputFields
                    r0 = r0[r8]
                    android.text.Editable r0 = r0.getText()
                    java.lang.String r0 = r0.toString()
                L_0x0082:
                    r2 = r0
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean[] r0 = r0.nonLatinNames
                    boolean r0 = r0[r11]
                    if (r0 == 0) goto L_0x00a2
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r1 = r0.inputExtraFields
                    r1 = r1[r11]
                    android.text.Editable r1 = r1.getText()
                    java.lang.String r1 = r1.toString()
                    java.lang.String r0 = r0.getTranslitString(r1)
                    goto L_0x00b2
                L_0x00a2:
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r0 = r0.inputFields
                    r0 = r0[r11]
                    android.text.Editable r0 = r0.getText()
                    java.lang.String r0 = r0.toString()
                L_0x00b2:
                    r3 = r0
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean[] r0 = r0.nonLatinNames
                    boolean r0 = r0[r10]
                    if (r0 == 0) goto L_0x00d2
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r1 = r0.inputExtraFields
                    r1 = r1[r10]
                    android.text.Editable r1 = r1.getText()
                    java.lang.String r1 = r1.toString()
                    java.lang.String r0 = r0.getTranslitString(r1)
                    goto L_0x00e2
                L_0x00d2:
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r0 = r0.inputFields
                    r0 = r0[r10]
                    android.text.Editable r0 = r0.getText()
                    java.lang.String r0 = r0.toString()
                L_0x00e2:
                    r4 = r0
                    boolean r0 = android.text.TextUtils.isEmpty(r2)
                    if (r0 != 0) goto L_0x014f
                    boolean r0 = android.text.TextUtils.isEmpty(r3)
                    if (r0 != 0) goto L_0x014f
                    boolean r0 = android.text.TextUtils.isEmpty(r4)
                    if (r0 != 0) goto L_0x014f
                    org.telegram.ui.ActionBar.AlertDialog$Builder r13 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    android.app.Activity r0 = r0.getParentActivity()
                    r13.<init>((android.content.Context) r0)
                    int r0 = org.telegram.messenger.R.string.PassportNameCheckAlert
                    java.lang.Object[] r1 = new java.lang.Object[r9]
                    r1[r8] = r2
                    r1[r11] = r3
                    r1[r10] = r4
                    java.lang.String r5 = "PassportNameCheckAlert"
                    java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r0, r1)
                    r13.setMessage(r0)
                    int r0 = org.telegram.messenger.R.string.AppName
                    java.lang.String r1 = "AppName"
                    java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                    r13.setTitle(r0)
                    int r0 = org.telegram.messenger.R.string.Done
                    java.lang.String r1 = "Done"
                    java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                    org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda1 r15 = new org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda1
                    r0 = r15
                    r1 = r25
                    r5 = r26
                    r6 = r27
                    r0.<init>(r1, r2, r3, r4, r5, r6)
                    r13.setPositiveButton(r14, r15)
                    int r0 = org.telegram.messenger.R.string.Edit
                    java.lang.String r1 = "Edit"
                    java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                    org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda0
                    r1.<init>(r7, r12)
                    r13.setNegativeButton(r0, r1)
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.ActionBar.AlertDialog r1 = r13.create()
                    r0.showDialog(r1)
                    goto L_0x015a
                L_0x014f:
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r1 = r0.inputFields
                    r1 = r1[r12]
                    r0.onFieldError(r1)
                L_0x015a:
                    r0 = 1
                L_0x015b:
                    int r12 = r12 + 1
                    goto L_0x002b
                L_0x015f:
                    if (r0 == 0) goto L_0x0162
                    return r8
                L_0x0162:
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    boolean r0 = r0.isHasNotAnyChanges()
                    if (r0 == 0) goto L_0x0170
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    r0.finishFragment()
                    return r8
                L_0x0170:
                    r0 = 0
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    boolean r1 = r1.documentOnly     // Catch:{ Exception -> 0x0358 }
                    if (r1 != 0) goto L_0x02b5
                    java.util.HashMap r1 = new java.util.HashMap     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    java.util.HashMap r2 = r2.currentValues     // Catch:{ Exception -> 0x0358 }
                    r1.<init>(r2)     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r2 = r2.currentType     // Catch:{ Exception -> 0x0358 }
                    boolean r2 = r2.native_names     // Catch:{ Exception -> 0x0358 }
                    if (r2 == 0) goto L_0x0213
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Cells.TextInfoPrivacyCell r2 = r2.nativeInfoCell     // Catch:{ Exception -> 0x0358 }
                    int r2 = r2.getVisibility()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = "last_name_native"
                    java.lang.String r4 = "middle_name_native"
                    java.lang.String r5 = "first_name_native"
                    if (r2 != 0) goto L_0x01da
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r2.inputExtraFields     // Catch:{ Exception -> 0x0358 }
                    r2 = r2[r8]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r2 = r2.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r5, r2)     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r2.inputExtraFields     // Catch:{ Exception -> 0x0358 }
                    r2 = r2[r11]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r2 = r2.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r4, r2)     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r2.inputExtraFields     // Catch:{ Exception -> 0x0358 }
                    r2 = r2[r10]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r2 = r2.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r3, r2)     // Catch:{ Exception -> 0x0358 }
                    goto L_0x0213
                L_0x01da:
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r2.inputFields     // Catch:{ Exception -> 0x0358 }
                    r2 = r2[r8]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r2 = r2.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r5, r2)     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r2.inputFields     // Catch:{ Exception -> 0x0358 }
                    r2 = r2[r11]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r2 = r2.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r4, r2)     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r2.inputFields     // Catch:{ Exception -> 0x0358 }
                    r2 = r2[r10]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r2 = r2.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r3, r2)     // Catch:{ Exception -> 0x0358 }
                L_0x0213:
                    java.lang.String r2 = "first_name"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r3 = r3.inputFields     // Catch:{ Exception -> 0x0358 }
                    r3 = r3[r8]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r3 = r3.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = "middle_name"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r3 = r3.inputFields     // Catch:{ Exception -> 0x0358 }
                    r3 = r3[r11]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r3 = r3.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = "last_name"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r3 = r3.inputFields     // Catch:{ Exception -> 0x0358 }
                    r3 = r3[r10]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r3 = r3.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = "birth_date"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r3 = r3.inputFields     // Catch:{ Exception -> 0x0358 }
                    r3 = r3[r9]     // Catch:{ Exception -> 0x0358 }
                    android.text.Editable r3 = r3.getText()     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = "gender"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.currentGender     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = "country_code"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.currentCitizeship     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r2 = "residence_country_code"
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0358 }
                    java.lang.String r3 = r3.currentResidence     // Catch:{ Exception -> 0x0358 }
                    r1.put(r2, r3)     // Catch:{ Exception -> 0x0358 }
                    org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x0358 }
                    r2.<init>()     // Catch:{ Exception -> 0x0358 }
                    java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x0356 }
                    java.util.Set r4 = r1.keySet()     // Catch:{ Exception -> 0x0356 }
                    r3.<init>(r4)     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda4 r4 = new org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda4     // Catch:{ Exception -> 0x0356 }
                    r4.<init>(r7)     // Catch:{ Exception -> 0x0356 }
                    java.util.Collections.sort(r3, r4)     // Catch:{ Exception -> 0x0356 }
                    int r4 = r3.size()     // Catch:{ Exception -> 0x0356 }
                    r5 = 0
                L_0x02a3:
                    if (r5 >= r4) goto L_0x02b6
                    java.lang.Object r6 = r3.get(r5)     // Catch:{ Exception -> 0x0356 }
                    java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x0356 }
                    java.lang.Object r12 = r1.get(r6)     // Catch:{ Exception -> 0x0356 }
                    r2.put(r6, r12)     // Catch:{ Exception -> 0x0356 }
                    int r5 = r5 + 1
                    goto L_0x02a3
                L_0x02b5:
                    r2 = r0
                L_0x02b6:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r1 = r1.currentDocumentsType     // Catch:{ Exception -> 0x0356 }
                    if (r1 == 0) goto L_0x0356
                    java.util.HashMap r1 = new java.util.HashMap     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    java.util.HashMap r3 = r3.currentDocumentValues     // Catch:{ Exception -> 0x0356 }
                    r1.<init>(r3)     // Catch:{ Exception -> 0x0356 }
                    java.lang.String r3 = "document_no"
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r4 = r4.inputFields     // Catch:{ Exception -> 0x0356 }
                    r5 = 7
                    r4 = r4[r5]     // Catch:{ Exception -> 0x0356 }
                    android.text.Editable r4 = r4.getText()     // Catch:{ Exception -> 0x0356 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0356 }
                    r1.put(r3, r4)     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    int[] r3 = r3.currentExpireDate     // Catch:{ Exception -> 0x0356 }
                    r3 = r3[r8]     // Catch:{ Exception -> 0x0356 }
                    java.lang.String r4 = "expiry_date"
                    if (r3 == 0) goto L_0x0323
                    java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x0356 }
                    java.lang.String r5 = "%02d.%02d.%d"
                    java.lang.Object[] r6 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.PassportActivity r9 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    int[] r9 = r9.currentExpireDate     // Catch:{ Exception -> 0x0356 }
                    r9 = r9[r10]     // Catch:{ Exception -> 0x0356 }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0356 }
                    r6[r8] = r9     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.PassportActivity r9 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    int[] r9 = r9.currentExpireDate     // Catch:{ Exception -> 0x0356 }
                    r9 = r9[r11]     // Catch:{ Exception -> 0x0356 }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0356 }
                    r6[r11] = r9     // Catch:{ Exception -> 0x0356 }
                    org.telegram.ui.PassportActivity r9 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0356 }
                    int[] r9 = r9.currentExpireDate     // Catch:{ Exception -> 0x0356 }
                    r9 = r9[r8]     // Catch:{ Exception -> 0x0356 }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0356 }
                    r6[r10] = r9     // Catch:{ Exception -> 0x0356 }
                    java.lang.String r3 = java.lang.String.format(r3, r5, r6)     // Catch:{ Exception -> 0x0356 }
                    r1.put(r4, r3)     // Catch:{ Exception -> 0x0356 }
                    goto L_0x0328
                L_0x0323:
                    java.lang.String r3 = ""
                    r1.put(r4, r3)     // Catch:{ Exception -> 0x0356 }
                L_0x0328:
                    org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0356 }
                    r3.<init>()     // Catch:{ Exception -> 0x0356 }
                    java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x0354 }
                    java.util.Set r5 = r1.keySet()     // Catch:{ Exception -> 0x0354 }
                    r4.<init>(r5)     // Catch:{ Exception -> 0x0354 }
                    org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda5 r5 = new org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda5     // Catch:{ Exception -> 0x0354 }
                    r5.<init>(r7)     // Catch:{ Exception -> 0x0354 }
                    java.util.Collections.sort(r4, r5)     // Catch:{ Exception -> 0x0354 }
                    int r5 = r4.size()     // Catch:{ Exception -> 0x0354 }
                L_0x0342:
                    if (r8 >= r5) goto L_0x035a
                    java.lang.Object r6 = r4.get(r8)     // Catch:{ Exception -> 0x0354 }
                    java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x0354 }
                    java.lang.Object r9 = r1.get(r6)     // Catch:{ Exception -> 0x0354 }
                    r3.put(r6, r9)     // Catch:{ Exception -> 0x0354 }
                    int r8 = r8 + 1
                    goto L_0x0342
                L_0x0354:
                    goto L_0x035a
                L_0x0356:
                    r3 = r0
                    goto L_0x035a
                L_0x0358:
                    r2 = r0
                    r3 = r2
                L_0x035a:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r1 = r1.fieldsErrors
                    if (r1 == 0) goto L_0x036b
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r1 = r1.fieldsErrors
                    r1.clear()
                L_0x036b:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r1 = r1.documentsErrors
                    if (r1 == 0) goto L_0x037c
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r1 = r1.documentsErrors
                    r1.clear()
                L_0x037c:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.PassportActivity$PassportActivityDelegate r12 = r1.delegate
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r13 = r1.currentType
                    r14 = 0
                    if (r2 == 0) goto L_0x0391
                    java.lang.String r1 = r2.toString()
                    r15 = r1
                    goto L_0x0392
                L_0x0391:
                    r15 = r0
                L_0x0392:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r16 = r1.currentDocumentsType
                    if (r3 == 0) goto L_0x03a1
                    java.lang.String r1 = r3.toString()
                    r17 = r1
                    goto L_0x03a3
                L_0x03a1:
                    r17 = r0
                L_0x03a3:
                    r18 = 0
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.messenger.SecureDocument r19 = r1.selfieDocument
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    java.util.ArrayList r20 = r1.translationDocuments
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.messenger.SecureDocument r21 = r1.frontDocument
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    android.widget.LinearLayout r1 = r1.reverseLayout
                    if (r1 == 0) goto L_0x03d1
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    android.widget.LinearLayout r1 = r1.reverseLayout
                    int r1 = r1.getVisibility()
                    if (r1 != 0) goto L_0x03d1
                    org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                    org.telegram.messenger.SecureDocument r0 = r0.reverseDocument
                L_0x03d1:
                    r22 = r0
                    r23 = r26
                    r24 = r27
                    r12.saveValue(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24)
                    return r11
                L_0x03db:
                    return r8
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass3.onIdentityDone(java.lang.Runnable, org.telegram.ui.PassportActivity$ErrorRunnable):boolean");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onIdentityDone$0(String str, String str2, String str3, Runnable runnable, ErrorRunnable errorRunnable, DialogInterface dialogInterface, int i) {
                PassportActivity.this.inputFields[0].setText(str);
                PassportActivity.this.inputFields[1].setText(str2);
                PassportActivity.this.inputFields[2].setText(str3);
                PassportActivity.this.showEditDoneProgress(true, true);
                onIdentityDone(runnable, errorRunnable);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onIdentityDone$1(int i, DialogInterface dialogInterface, int i2) {
                PassportActivity passportActivity = PassportActivity.this;
                passportActivity.onFieldError(passportActivity.inputFields[i]);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ int lambda$onIdentityDone$2(String str, String str2) {
                int access$2000 = PassportActivity.this.getFieldCost(str);
                int access$20002 = PassportActivity.this.getFieldCost(str2);
                if (access$2000 < access$20002) {
                    return -1;
                }
                return access$2000 > access$20002 ? 1 : 0;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ int lambda$onIdentityDone$3(String str, String str2) {
                int access$2000 = PassportActivity.this.getFieldCost(str);
                int access$20002 = PassportActivity.this.getFieldCost(str2);
                if (access$2000 < access$20002) {
                    return -1;
                }
                return access$2000 > access$20002 ? 1 : 0;
            }

            /* JADX WARNING: Removed duplicated region for block: B:74:0x02a4  */
            /* JADX WARNING: Removed duplicated region for block: B:77:0x02b5  */
            /* JADX WARNING: Removed duplicated region for block: B:80:0x02cd  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(int r21) {
                /*
                    r20 = this;
                    r0 = r20
                    r1 = r21
                    r2 = 5
                    r3 = -1
                    r4 = 0
                    if (r1 != r3) goto L_0x002e
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    boolean r1 = r1.checkDiscard()
                    if (r1 == 0) goto L_0x0012
                    return
                L_0x0012:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    int r1 = r1.currentActivityType
                    if (r1 == 0) goto L_0x0022
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    int r1 = r1.currentActivityType
                    if (r1 != r2) goto L_0x0027
                L_0x0022:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    r1.callCallback(r4)
                L_0x0027:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    r1.finishFragment()
                    goto L_0x0363
                L_0x002e:
                    r5 = 0
                    r6 = 1
                    if (r1 != r6) goto L_0x00e7
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    android.app.Activity r1 = r1.getParentActivity()
                    if (r1 != 0) goto L_0x003b
                    return
                L_0x003b:
                    android.widget.TextView r1 = new android.widget.TextView
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this
                    android.app.Activity r2 = r2.getParentActivity()
                    r1.<init>(r2)
                    int r2 = org.telegram.messenger.R.string.PassportInfo2
                    java.lang.String r7 = "PassportInfo2"
                    java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
                    android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
                    r7.<init>(r2)
                    r8 = 42
                    int r9 = r2.indexOf(r8)
                    int r2 = r2.lastIndexOf(r8)
                    if (r9 == r3) goto L_0x0080
                    if (r2 == r3) goto L_0x0080
                    int r3 = r2 + 1
                    java.lang.String r8 = ""
                    r7.replace(r2, r3, r8)
                    int r3 = r9 + 1
                    r7.replace(r9, r3, r8)
                    org.telegram.ui.PassportActivity$3$1 r3 = new org.telegram.ui.PassportActivity$3$1
                    int r8 = org.telegram.messenger.R.string.PassportInfoUrl
                    java.lang.String r10 = "PassportInfoUrl"
                    java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                    r3.<init>(r8)
                    int r2 = r2 - r6
                    r8 = 33
                    r7.setSpan(r3, r9, r2, r8)
                L_0x0080:
                    r1.setText(r7)
                    r2 = 1098907648(0x41800000, float:16.0)
                    r1.setTextSize(r6, r2)
                    java.lang.String r2 = "dialogTextLink"
                    int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r1.setLinkTextColor(r2)
                    java.lang.String r2 = "dialogLinkSelection"
                    int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r1.setHighlightColor(r2)
                    r2 = 1102577664(0x41b80000, float:23.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    r1.setPadding(r3, r4, r2, r4)
                    org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r2 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
                    r2.<init>()
                    r1.setMovementMethod(r2)
                    java.lang.String r2 = "dialogTextBlack"
                    int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r1.setTextColor(r2)
                    org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    android.app.Activity r3 = r3.getParentActivity()
                    r2.<init>((android.content.Context) r3)
                    r2.setView(r1)
                    int r1 = org.telegram.messenger.R.string.PassportInfoTitle
                    java.lang.String r3 = "PassportInfoTitle"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                    r2.setTitle(r1)
                    int r1 = org.telegram.messenger.R.string.Close
                    java.lang.String r3 = "Close"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                    r2.setNegativeButton(r1, r5)
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.ActionBar.AlertDialog r2 = r2.create()
                    r1.showDialog(r2)
                    goto L_0x0363
                L_0x00e7:
                    r3 = 2
                    if (r1 != r3) goto L_0x0363
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    int r1 = r1.currentActivityType
                    if (r1 != r2) goto L_0x00f8
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    r1.onPasswordDone(r4)
                    return
                L_0x00f8:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    int r1 = r1.currentActivityType
                    r2 = 7
                    if (r1 != r2) goto L_0x0114
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.SlideView[] r1 = r1.views
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this
                    int r2 = r2.currentViewNum
                    r1 = r1[r2]
                    r1.onNextPressed(r5)
                    goto L_0x0363
                L_0x0114:
                    org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda2
                    r1.<init>(r0)
                    org.telegram.ui.PassportActivity$3$2 r2 = new org.telegram.ui.PassportActivity$3$2
                    r2.<init>(r1)
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this
                    int r7 = r7.currentActivityType
                    r8 = 4
                    if (r7 != r8) goto L_0x0170
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    boolean r3 = r3.useCurrentValue
                    if (r3 == 0) goto L_0x0137
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    java.lang.String r3 = r3.currentEmail
                L_0x0135:
                    r9 = r3
                    goto L_0x0151
                L_0x0137:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    boolean r3 = r3.checkFieldsForError()
                    if (r3 == 0) goto L_0x0140
                    return
                L_0x0140:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r3 = r3.inputFields
                    r3 = r3[r4]
                    android.text.Editable r3 = r3.getText()
                    java.lang.String r3 = r3.toString()
                    goto L_0x0135
                L_0x0151:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.PassportActivity$PassportActivityDelegate r7 = r3.delegate
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r8 = r3.currentType
                    r10 = 0
                    r11 = 0
                    r12 = 0
                    r13 = 0
                    r14 = 0
                    r15 = 0
                    r16 = 0
                    r17 = 0
                    r18 = r1
                    r19 = r2
                    r7.saveValue(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
                    goto L_0x035e
                L_0x0170:
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this
                    int r7 = r7.currentActivityType
                    r9 = 3
                    if (r7 != r9) goto L_0x01eb
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    boolean r4 = r4.useCurrentValue
                    if (r4 == 0) goto L_0x0193
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    int r3 = r3.currentAccount
                    org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                    org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
                    java.lang.String r3 = r3.phone
                L_0x0191:
                    r9 = r3
                    goto L_0x01cc
                L_0x0193:
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    boolean r4 = r4.checkFieldsForError()
                    if (r4 == 0) goto L_0x019c
                    return
                L_0x019c:
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder
                    r4.<init>()
                    org.telegram.ui.PassportActivity r5 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = r5.inputFields
                    r5 = r5[r6]
                    android.text.Editable r5 = r5.getText()
                    java.lang.String r5 = r5.toString()
                    r4.append(r5)
                    org.telegram.ui.PassportActivity r5 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = r5.inputFields
                    r3 = r5[r3]
                    android.text.Editable r3 = r3.getText()
                    java.lang.String r3 = r3.toString()
                    r4.append(r3)
                    java.lang.String r3 = r4.toString()
                    goto L_0x0191
                L_0x01cc:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.PassportActivity$PassportActivityDelegate r7 = r3.delegate
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r8 = r3.currentType
                    r10 = 0
                    r11 = 0
                    r12 = 0
                    r13 = 0
                    r14 = 0
                    r15 = 0
                    r16 = 0
                    r17 = 0
                    r18 = r1
                    r19 = r2
                    r7.saveValue(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
                    goto L_0x035e
                L_0x01eb:
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this
                    int r7 = r7.currentActivityType
                    if (r7 != r3) goto L_0x02f9
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r7 = r7.uploadingDocuments
                    boolean r7 = r7.isEmpty()
                    if (r7 == 0) goto L_0x02f8
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this
                    boolean r7 = r7.checkFieldsForError()
                    if (r7 == 0) goto L_0x0209
                    goto L_0x02f8
                L_0x0209:
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this
                    boolean r7 = r7.isHasNotAnyChanges()
                    if (r7 == 0) goto L_0x0217
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    r1.finishFragment()
                    return
                L_0x0217:
                    org.telegram.ui.PassportActivity r7 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x029b }
                    boolean r7 = r7.documentOnly     // Catch:{ Exception -> 0x029b }
                    if (r7 != 0) goto L_0x029b
                    org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ Exception -> 0x029b }
                    r7.<init>()     // Catch:{ Exception -> 0x029b }
                    java.lang.String r10 = "street_line1"
                    org.telegram.ui.PassportActivity r11 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0299 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r11 = r11.inputFields     // Catch:{ Exception -> 0x0299 }
                    r4 = r11[r4]     // Catch:{ Exception -> 0x0299 }
                    android.text.Editable r4 = r4.getText()     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0299 }
                    r7.put(r10, r4)     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r4 = "street_line2"
                    org.telegram.ui.PassportActivity r10 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0299 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r10 = r10.inputFields     // Catch:{ Exception -> 0x0299 }
                    r10 = r10[r6]     // Catch:{ Exception -> 0x0299 }
                    android.text.Editable r10 = r10.getText()     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0299 }
                    r7.put(r4, r10)     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r4 = "post_code"
                    org.telegram.ui.PassportActivity r10 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0299 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r10 = r10.inputFields     // Catch:{ Exception -> 0x0299 }
                    r3 = r10[r3]     // Catch:{ Exception -> 0x0299 }
                    android.text.Editable r3 = r3.getText()     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0299 }
                    r7.put(r4, r3)     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r3 = "city"
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0299 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r4 = r4.inputFields     // Catch:{ Exception -> 0x0299 }
                    r4 = r4[r9]     // Catch:{ Exception -> 0x0299 }
                    android.text.Editable r4 = r4.getText()     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0299 }
                    r7.put(r3, r4)     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r3 = "state"
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0299 }
                    org.telegram.ui.Components.EditTextBoldCursor[] r4 = r4.inputFields     // Catch:{ Exception -> 0x0299 }
                    r4 = r4[r8]     // Catch:{ Exception -> 0x0299 }
                    android.text.Editable r4 = r4.getText()     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0299 }
                    r7.put(r3, r4)     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r3 = "country_code"
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this     // Catch:{ Exception -> 0x0299 }
                    java.lang.String r4 = r4.currentCitizeship     // Catch:{ Exception -> 0x0299 }
                    r7.put(r3, r4)     // Catch:{ Exception -> 0x0299 }
                    goto L_0x029c
                L_0x0299:
                    goto L_0x029c
                L_0x029b:
                    r7 = r5
                L_0x029c:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r3 = r3.fieldsErrors
                    if (r3 == 0) goto L_0x02ad
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r3 = r3.fieldsErrors
                    r3.clear()
                L_0x02ad:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r3 = r3.documentsErrors
                    if (r3 == 0) goto L_0x02be
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r3 = r3.documentsErrors
                    r3.clear()
                L_0x02be:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.PassportActivity$PassportActivityDelegate r3 = r3.delegate
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r8 = r4.currentType
                    r9 = 0
                    if (r7 == 0) goto L_0x02d1
                    java.lang.String r5 = r7.toString()
                L_0x02d1:
                    r10 = r5
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    org.telegram.tgnet.TLRPC$TL_secureRequiredType r11 = r4.currentDocumentsType
                    r12 = 0
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    java.util.ArrayList r13 = r4.documents
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    org.telegram.messenger.SecureDocument r14 = r4.selfieDocument
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    java.util.ArrayList r15 = r4.translationDocuments
                    r16 = 0
                    r17 = 0
                    r7 = r3
                    r18 = r1
                    r19 = r2
                    r7.saveValue(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
                    goto L_0x035e
                L_0x02f8:
                    return
                L_0x02f9:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    int r3 = r3.currentActivityType
                    if (r3 != r6) goto L_0x0308
                    boolean r1 = r0.onIdentityDone(r1, r2)
                    if (r1 != 0) goto L_0x035e
                    return
                L_0x0308:
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    int r3 = r3.currentActivityType
                    r5 = 6
                    if (r3 != r5) goto L_0x035e
                    org.telegram.tgnet.TLRPC$TL_account_verifyEmail r3 = new org.telegram.tgnet.TLRPC$TL_account_verifyEmail
                    r3.<init>()
                    org.telegram.ui.PassportActivity r5 = org.telegram.ui.PassportActivity.this
                    java.util.HashMap r5 = r5.currentValues
                    java.lang.String r7 = "email"
                    java.lang.Object r5 = r5.get(r7)
                    java.lang.String r5 = (java.lang.String) r5
                    r3.email = r5
                    org.telegram.ui.PassportActivity r5 = org.telegram.ui.PassportActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = r5.inputFields
                    r4 = r5[r4]
                    android.text.Editable r4 = r4.getText()
                    java.lang.String r4 = r4.toString()
                    r3.code = r4
                    org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                    int r4 = r4.currentAccount
                    org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                    org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda6 r5 = new org.telegram.ui.PassportActivity$3$$ExternalSyntheticLambda6
                    r5.<init>(r0, r1, r2, r3)
                    int r1 = r4.sendRequest(r3, r5)
                    org.telegram.ui.PassportActivity r2 = org.telegram.ui.PassportActivity.this
                    int r2 = r2.currentAccount
                    org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
                    org.telegram.ui.PassportActivity r3 = org.telegram.ui.PassportActivity.this
                    int r3 = r3.classGuid
                    r2.bindRequestToGuid(r1, r3)
                L_0x035e:
                    org.telegram.ui.PassportActivity r1 = org.telegram.ui.PassportActivity.this
                    r1.showEditDoneProgress(r6, r6)
                L_0x0363:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass3.onItemClick(int):void");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$4() {
                PassportActivity.this.finishFragment();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$6(Runnable runnable, ErrorRunnable errorRunnable, TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new PassportActivity$3$$ExternalSyntheticLambda3(this, tLRPC$TL_error, runnable, errorRunnable, tLRPC$TL_account_verifyEmail));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$5(TLRPC$TL_error tLRPC$TL_error, Runnable runnable, ErrorRunnable errorRunnable, TLRPC$TL_account_verifyEmail tLRPC$TL_account_verifyEmail) {
                TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
                if (tLRPC$TL_error2 == null) {
                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), (String) null, (TLRPC$TL_secureRequiredType) null, (String) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (SecureDocument) null, runnable, errorRunnable);
                    return;
                }
                AlertsCreator.processError(PassportActivity.this.currentAccount, tLRPC$TL_error2, PassportActivity.this, tLRPC$TL_account_verifyEmail, new Object[0]);
                errorRunnable.onError((String) null, (String) null);
            }
        });
        if (this.currentActivityType == 7) {
            AnonymousClass4 r0 = new ScrollView(context) {
                /* access modifiers changed from: protected */
                public boolean onRequestFocusInDescendants(int i, Rect rect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                    if (PassportActivity.this.currentViewNum == 1 || PassportActivity.this.currentViewNum == 2 || PassportActivity.this.currentViewNum == 4) {
                        rect.bottom += AndroidUtilities.dp(40.0f);
                    }
                    return super.requestChildRectangleOnScreen(view, rect, z);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int unused = PassportActivity.this.scrollHeight = View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(30.0f);
                    super.onMeasure(i, i2);
                }
            };
            this.scrollView = r0;
            this.fragmentView = r0;
            r0.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        } else {
            FrameLayout frameLayout = new FrameLayout(context);
            this.fragmentView = frameLayout;
            FrameLayout frameLayout2 = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            AnonymousClass5 r02 = new ScrollView(this, context) {
                /* access modifiers changed from: protected */
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
            this.scrollView = r02;
            r02.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
            frameLayout2.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentActivityType == 0 ? 48.0f : 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout2 = linearLayout;
            linearLayout.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        }
        int i = this.currentActivityType;
        if (!(i == 0 || i == 8)) {
            this.doneItem = this.actionBar.createMenu().addItemWithWidth(2, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", R.string.Done));
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            contextProgressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            int i2 = this.currentActivityType;
            if ((i2 == 1 || i2 == 2) && (chatAttachAlert2 = this.chatAttachAlert) != null) {
                try {
                    if (chatAttachAlert2.isShowing()) {
                        this.chatAttachAlert.dismiss();
                    }
                } catch (Exception unused) {
                }
                this.chatAttachAlert.onDestroy();
                this.chatAttachAlert = null;
            }
        }
        int i3 = this.currentActivityType;
        if (i3 == 5) {
            createPasswordInterface(context);
        } else if (i3 == 0) {
            createRequestInterface(context);
        } else if (i3 == 1) {
            createIdentityInterface(context);
            fillInitialValues();
        } else if (i3 == 2) {
            createAddressInterface(context);
            fillInitialValues();
        } else if (i3 == 3) {
            createPhoneInterface(context);
        } else if (i3 == 4) {
            createEmailInterface(context);
        } else if (i3 == 6) {
            createEmailVerificationInterface(context);
        } else if (i3 == 7) {
            createPhoneVerificationInterface(context);
        } else if (i3 == 8) {
            createManageInterface(context);
        }
        return this.fragmentView;
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    public void dismissCurrentDialog() {
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 == null || this.visibleDialog != chatAttachAlert2) {
            super.dismissCurrentDialog();
            return;
        }
        chatAttachAlert2.getPhotoLayout().closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.getPhotoLayout().hideCamera(true);
    }

    /* access modifiers changed from: private */
    public String getTranslitString(String str) {
        return LocaleController.getInstance().getTranslitString(str, true);
    }

    /* access modifiers changed from: private */
    public int getFieldCost(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2006252145:
                if (str.equals("residence_country_code")) {
                    c = 0;
                    break;
                }
                break;
            case -1537298398:
                if (str.equals("last_name_native")) {
                    c = 1;
                    break;
                }
                break;
            case -1249512767:
                if (str.equals("gender")) {
                    c = 2;
                    break;
                }
                break;
            case -796150911:
                if (str.equals("street_line1")) {
                    c = 3;
                    break;
                }
                break;
            case -796150910:
                if (str.equals("street_line2")) {
                    c = 4;
                    break;
                }
                break;
            case -160985414:
                if (str.equals("first_name")) {
                    c = 5;
                    break;
                }
                break;
            case 3053931:
                if (str.equals("city")) {
                    c = 6;
                    break;
                }
                break;
            case 109757585:
                if (str.equals("state")) {
                    c = 7;
                    break;
                }
                break;
            case 421072629:
                if (str.equals("middle_name")) {
                    c = 8;
                    break;
                }
                break;
            case 451516732:
                if (str.equals("first_name_native")) {
                    c = 9;
                    break;
                }
                break;
            case 475919162:
                if (str.equals("expiry_date")) {
                    c = 10;
                    break;
                }
                break;
            case 506677093:
                if (str.equals("document_no")) {
                    c = 11;
                    break;
                }
                break;
            case 1168724782:
                if (str.equals("birth_date")) {
                    c = 12;
                    break;
                }
                break;
            case 1181577377:
                if (str.equals("middle_name_native")) {
                    c = 13;
                    break;
                }
                break;
            case 1481071862:
                if (str.equals("country_code")) {
                    c = 14;
                    break;
                }
                break;
            case 2002465324:
                if (str.equals("post_code")) {
                    c = 15;
                    break;
                }
                break;
            case 2013122196:
                if (str.equals("last_name")) {
                    c = 16;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 26;
            case 1:
            case 16:
                return 22;
            case 2:
                return 24;
            case 3:
                return 29;
            case 4:
                return 30;
            case 5:
            case 9:
                return 20;
            case 6:
                return 32;
            case 7:
                return 33;
            case 8:
            case 13:
                return 21;
            case 10:
                return 28;
            case 11:
                return 27;
            case 12:
                return 23;
            case 14:
                return 25;
            case 15:
                return 31;
            default:
                return 100;
        }
    }

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", R.string.PassportPhone));
        FrameLayout frameLayout = new FrameLayout(context);
        this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        for (int i = 0; i < 3; i++) {
            this.views[i] = new PhoneConfirmationView(this, context, i + 2);
            this.views[i].setVisibility(8);
            SlideView slideView = this.views[i];
            float f = 18.0f;
            float f2 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (AndroidUtilities.isTablet()) {
                f = 26.0f;
            }
            frameLayout.addView(slideView, LayoutHelper.createFrame(-1, -1.0f, 51, f2, 30.0f, f, 0.0f));
        }
        Bundle bundle = new Bundle();
        bundle.putString("phone", this.currentValues.get("phone"));
        fillNextCodeParams(bundle, this.currentPhoneVerification, false);
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new PassportActivity$$ExternalSyntheticLambda63(this)), this.classGuid);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda56(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$3(TLObject tLObject) {
        if (tLObject != null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            updatePasswordInterface();
            if (this.inputFieldContainers[0].getVisibility() == 0) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
            if (this.usingSavedPassword == 1) {
                onPasswordDone(true);
            }
        }
    }

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        this.inputFields = new EditTextBoldCursor[1];
        for (int i = 0; i < 1; i++) {
            FrameLayout frameLayout = new FrameLayout(context);
            this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 50));
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[i] = new EditTextBoldCursor(context);
            this.inputFields[i].setTag(Integer.valueOf(i));
            this.inputFields[i].setTextSize(1, 16.0f);
            this.inputFields[i].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[i].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i].setBackgroundDrawable((Drawable) null);
            this.inputFields[i].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i].setCursorWidth(1.5f);
            int i2 = 3;
            this.inputFields[i].setInputType(3);
            this.inputFields[i].setImeOptions(NUM);
            this.inputFields[i].setHint(LocaleController.getString("PassportEmailCode", R.string.PassportEmailCode));
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            this.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            EditTextBoldCursor editTextBoldCursor = this.inputFields[i];
            if (LocaleController.isRTL) {
                i2 = 5;
            }
            editTextBoldCursor.setGravity(i2);
            frameLayout.addView(this.inputFields[i], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[i].setOnEditorActionListener(new PassportActivity$$ExternalSyntheticLambda39(this));
            this.inputFields[i].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!PassportActivity.this.ignoreOnTextChange && PassportActivity.this.emailCodeLength != 0 && PassportActivity.this.inputFields[0].length() == PassportActivity.this.emailCodeLength) {
                        PassportActivity.this.doneItem.callOnClick();
                    }
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.formatString("PassportEmailVerifyInfo", R.string.PassportEmailVerifyInfo, this.currentValues.get("email")));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createEmailVerificationInterface$5(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    private void createPasswordInterface(Context context) {
        TLRPC$User tLRPC$User;
        Context context2 = context;
        if (this.currentForm != null) {
            int i = 0;
            while (true) {
                if (i >= this.currentForm.users.size()) {
                    tLRPC$User = null;
                    break;
                }
                tLRPC$User = this.currentForm.users.get(i);
                if (tLRPC$User.id == this.currentBotId) {
                    break;
                }
                i++;
            }
        } else {
            tLRPC$User = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.actionBar.setTitle(LocaleController.getString("TelegramPassport", R.string.TelegramPassport));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        ((FrameLayout) this.fragmentView).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.passwordAvatarContainer = frameLayout;
        this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 100));
        BackupImageView backupImageView = new BackupImageView(context2);
        backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(backupImageView, LayoutHelper.createFrame(64, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        backupImageView.setForUserOrChat(tLRPC$User, new AvatarDrawable(tLRPC$User));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.passwordRequestTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.getTextView().setGravity(1);
        if (this.currentBotId == 0) {
            this.passwordRequestTextView.setText(LocaleController.getString("PassportSelfRequest", R.string.PassportSelfRequest));
        } else {
            this.passwordRequestTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", R.string.PassportRequest, UserObject.getFirstName(tLRPC$User))));
        }
        ((FrameLayout.LayoutParams) this.passwordRequestTextView.getTextView().getLayoutParams()).gravity = 1;
        int i2 = 5;
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.noPasswordImageView = imageView;
        imageView.setImageResource(R.drawable.no_password);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        TextView textView = new TextView(context2);
        this.noPasswordTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.noPasswordTextView.setText(LocaleController.getString("TelegramPassportCreatePasswordInfo", R.string.TelegramPassportCreatePasswordInfo));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.noPasswordSetTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText5"));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.noPasswordSetTextView.setText(LocaleController.getString("TelegramPassportCreatePassword", R.string.TelegramPassportCreatePassword));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 9.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda15(this));
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = new ViewGroup[1];
        for (int i3 = 0; i3 < 1; i3++) {
            this.inputFieldContainers[i3] = new FrameLayout(context2);
            this.linearLayout2.addView(this.inputFieldContainers[i3], LayoutHelper.createLinear(-1, 50));
            this.inputFieldContainers[i3].setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[i3] = new EditTextBoldCursor(context2);
            this.inputFields[i3].setTag(Integer.valueOf(i3));
            this.inputFields[i3].setTextSize(1, 16.0f);
            this.inputFields[i3].setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[i3].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i3].setBackgroundDrawable((Drawable) null);
            this.inputFields[i3].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i3].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i3].setCursorWidth(1.5f);
            this.inputFields[i3].setInputType(129);
            this.inputFields[i3].setMaxLines(1);
            this.inputFields[i3].setLines(1);
            this.inputFields[i3].setSingleLine(true);
            this.inputFields[i3].setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.inputFields[i3].setTypeface(Typeface.DEFAULT);
            this.inputFields[i3].setImeOptions(NUM);
            this.inputFields[i3].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[i3].setGravity(LocaleController.isRTL ? 5 : 3);
            this.inputFieldContainers[i3].addView(this.inputFields[i3], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[i3].setOnEditorActionListener(new PassportActivity$$ExternalSyntheticLambda40(this));
            this.inputFields[i3].setCustomSelectionActionModeCallback(new ActionMode.Callback(this) {
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
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.passwordInfoRequestTextView = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", R.string.PassportRequestPasswordInfo, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        TextView textView3 = new TextView(context2);
        this.passwordForgotButton = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        LinearLayout linearLayout = this.linearLayout2;
        TextView textView4 = this.passwordForgotButton;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        linearLayout.addView(textView4, LayoutHelper.createLinear(-2, 30, i2 | 48, 21, 0, 21, 0));
        this.passwordForgotButton.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda14(this));
        updatePasswordInterface();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPasswordInterface$6(View view) {
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        twoStepVerificationSetupActivity.setCloseAfterSet(true);
        presentFragment(twoStepVerificationSetupActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createPasswordInterface$7(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPasswordInterface$12(View view) {
        if (this.currentPassword.has_recovery) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new PassportActivity$$ExternalSyntheticLambda62(this), 10), this.classGuid);
        } else if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", R.string.RestorePasswordResetAccount), new PassportActivity$$ExternalSyntheticLambda2(this));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPasswordInterface$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda59(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPasswordInterface$9(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery = (TLRPC$TL_auth_passwordRecovery) tLObject;
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, tLRPC$TL_auth_passwordRecovery.email_pattern));
            builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", R.string.RestoreEmailSentTitle));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PassportActivity$$ExternalSyntheticLambda7(this, tLRPC$TL_auth_passwordRecovery));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPasswordInterface$8(TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        tLRPC$TL_account_password.email_unconfirmed_pattern = tLRPC$TL_auth_passwordRecovery.email_pattern;
        presentFragment(new TwoStepVerificationSetupActivity(this.currentAccount, 4, tLRPC$TL_account_password));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPasswordInterface$11(DialogInterface dialogInterface, int i) {
        Activity parentActivity = getParentActivity();
        Browser.openUrl((Context) parentActivity, "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    /* access modifiers changed from: private */
    public void onPasswordDone(boolean z) {
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
        Utilities.globalQueue.postRunnable(new PassportActivity$$ExternalSyntheticLambda61(this, z, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x006a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onPasswordDone$13(boolean r11, java.lang.String r12) {
        /*
            r10 = this;
            org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings r6 = new org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings
            r6.<init>()
            r7 = 0
            if (r11 == 0) goto L_0x000c
            byte[] r0 = r10.savedPasswordHash
        L_0x000a:
            r8 = r0
            goto L_0x0024
        L_0x000c:
            org.telegram.tgnet.TLRPC$TL_account_password r0 = r10.currentPassword
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r0 = r0.current_algo
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow
            if (r0 == 0) goto L_0x0023
            byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r12)
            org.telegram.tgnet.TLRPC$TL_account_password r1 = r10.currentPassword
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r1 = r1.current_algo
            org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow r1 = (org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) r1
            byte[] r0 = org.telegram.messenger.SRPHelper.getX(r0, r1)
            goto L_0x000a
        L_0x0023:
            r8 = r7
        L_0x0024:
            org.telegram.ui.PassportActivity$8 r9 = new org.telegram.ui.PassportActivity$8
            r0 = r9
            r1 = r10
            r2 = r11
            r3 = r8
            r4 = r6
            r5 = r12
            r0.<init>(r2, r3, r4, r5)
            org.telegram.tgnet.TLRPC$TL_account_password r11 = r10.currentPassword
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r12 = r11.current_algo
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow
            if (r0 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow r12 = (org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) r12
            long r0 = r11.srp_id
            byte[] r11 = r11.srp_B
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP r11 = org.telegram.messenger.SRPHelper.startCheck(r8, r0, r11, r12)
            r6.password = r11
            if (r11 != 0) goto L_0x0052
            org.telegram.tgnet.TLRPC$TL_error r11 = new org.telegram.tgnet.TLRPC$TL_error
            r11.<init>()
            java.lang.String r12 = "ALGO_INVALID"
            r11.text = r12
            r9.run(r7, r11)
            return
        L_0x0052:
            int r11 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11)
            r12 = 10
            int r11 = r11.sendRequest(r6, r9, r12)
            int r12 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            int r0 = r10.classGuid
            r12.bindRequestToGuid(r11, r0)
            goto L_0x0076
        L_0x006a:
            org.telegram.tgnet.TLRPC$TL_error r11 = new org.telegram.tgnet.TLRPC$TL_error
            r11.<init>()
            java.lang.String r12 = "PASSWORD_HASH_INVALID"
            r11.text = r12
            r9.run(r7, r11)
        L_0x0076:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$onPasswordDone$13(boolean, java.lang.String):void");
    }

    private boolean isPersonalDocument(TLRPC$SecureValueType tLRPC$SecureValueType) {
        return (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeDriverLicense) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassport) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeInternalPassport) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeIdentityCard);
    }

    private boolean isAddressDocument(TLRPC$SecureValueType tLRPC$SecureValueType) {
        return (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeUtilityBill) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeBankStatement) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassportRegistration) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeRentalAgreement);
    }

    /* JADX WARNING: Removed duplicated region for block: B:127:0x0308  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0102  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createRequestInterface(android.content.Context r24) {
        /*
            r23 = this;
            r6 = r23
            r7 = r24
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            r9 = 0
            if (r0 == 0) goto L_0x002b
            r0 = 0
        L_0x000a:
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r1 = r6.currentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r1.users
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x002b
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r1 = r6.currentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r1.users
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            long r2 = r1.id
            long r4 = r6.currentBotId
            int r10 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r10 != 0) goto L_0x0028
            r10 = r1
            goto L_0x002c
        L_0x0028:
            int r0 = r0 + 1
            goto L_0x000a
        L_0x002b:
            r10 = 0
        L_0x002c:
            android.view.View r0 = r6.fragmentView
            r11 = r0
            android.widget.FrameLayout r11 = (android.widget.FrameLayout) r11
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            int r1 = org.telegram.messenger.R.string.TelegramPassport
            java.lang.String r2 = "TelegramPassport"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            int r1 = org.telegram.messenger.R.drawable.msg_info
            r12 = 1
            r0.addItem((int) r12, (int) r1)
            java.lang.String r13 = "windowBackgroundGrayShadow"
            r14 = -2
            r15 = -1
            if (r10 == 0) goto L_0x00d6
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            android.widget.LinearLayout r1 = r6.linearLayout2
            r2 = 100
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r2)
            r1.addView(r0, r2)
            org.telegram.ui.Components.BackupImageView r1 = new org.telegram.ui.Components.BackupImageView
            r1.<init>(r7)
            r2 = 1107296256(0x42000000, float:32.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setRoundRadius(r2)
            r16 = 64
            r17 = 1115684864(0x42800000, float:64.0)
            r18 = 17
            r19 = 0
            r20 = 1090519040(0x41000000, float:8.0)
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r1, r2)
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>((org.telegram.tgnet.TLRPC$User) r10)
            r1.setForUserOrChat(r10, r0)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r0.<init>(r7)
            r6.bottomCell = r0
            int r1 = org.telegram.messenger.R.drawable.greydivider_top
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r13)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            int r1 = org.telegram.messenger.R.string.PassportRequest
            java.lang.Object[] r2 = new java.lang.Object[r12]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r10)
            r2[r9] = r3
            java.lang.String r3 = "PassportRequest"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setText(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            android.widget.TextView r0 = r0.getTextView()
            r0.setGravity(r12)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            android.widget.TextView r0 = r0.getTextView()
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r0.gravity = r12
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = r6.bottomCell
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14)
            r0.addView(r1, r2)
        L_0x00d6:
            org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
            r0.<init>(r7)
            r6.headerCell = r0
            int r1 = org.telegram.messenger.R.string.PassportRequestedInformation
            java.lang.String r2 = "PassportRequestedInformation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Cells.HeaderCell r0 = r6.headerCell
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.HeaderCell r1 = r6.headerCell
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14)
            r0.addView(r1, r2)
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            if (r0 == 0) goto L_0x0306
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r0 = r0.required_types
            int r5 = r0.size()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = 0
            r1 = 0
            r2 = 0
            r8 = 0
            r16 = 0
        L_0x0118:
            if (r0 >= r5) goto L_0x01db
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r14 = r6.currentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r14 = r14.required_types
            java.lang.Object r14 = r14.get(r0)
            org.telegram.tgnet.TLRPC$SecureRequiredType r14 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r14
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r15 == 0) goto L_0x0157
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r14 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r14
            org.telegram.tgnet.TLRPC$SecureValueType r15 = r14.type
            boolean r15 = r6.isPersonalDocument(r15)
            if (r15 == 0) goto L_0x0137
            r4.add(r14)
            goto L_0x01a0
        L_0x0137:
            org.telegram.tgnet.TLRPC$SecureValueType r15 = r14.type
            boolean r15 = r6.isAddressDocument(r15)
            if (r15 == 0) goto L_0x0145
            r3.add(r14)
            int r8 = r8 + 1
            goto L_0x0153
        L_0x0145:
            org.telegram.tgnet.TLRPC$SecureValueType r14 = r14.type
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            if (r15 == 0) goto L_0x014d
            r1 = 1
            goto L_0x0153
        L_0x014d:
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r14 == 0) goto L_0x0153
            r16 = 1
        L_0x0153:
            r22 = r4
            goto L_0x01d1
        L_0x0157:
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf
            if (r15 == 0) goto L_0x0153
            org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf r14 = (org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r15 = r14.types
            boolean r15 = r15.isEmpty()
            if (r15 == 0) goto L_0x0166
            goto L_0x0153
        L_0x0166:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r15 = r14.types
            java.lang.Object r15 = r15.get(r9)
            org.telegram.tgnet.TLRPC$SecureRequiredType r15 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r15
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r9 != 0) goto L_0x0173
            goto L_0x0153
        L_0x0173:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r15 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r15
            org.telegram.tgnet.TLRPC$SecureValueType r9 = r15.type
            boolean r9 = r6.isPersonalDocument(r9)
            if (r9 == 0) goto L_0x01a3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r9 = r14.types
            int r9 = r9.size()
            r15 = 0
        L_0x0184:
            if (r15 >= r9) goto L_0x01a0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r12 = r14.types
            java.lang.Object r12 = r12.get(r15)
            org.telegram.tgnet.TLRPC$SecureRequiredType r12 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r12
            r22 = r9
            boolean r9 = r12 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r9 != 0) goto L_0x0195
            goto L_0x019a
        L_0x0195:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r12 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r12
            r4.add(r12)
        L_0x019a:
            int r15 = r15 + 1
            r9 = r22
            r12 = 1
            goto L_0x0184
        L_0x01a0:
            int r2 = r2 + 1
            goto L_0x0153
        L_0x01a3:
            org.telegram.tgnet.TLRPC$SecureValueType r9 = r15.type
            boolean r9 = r6.isAddressDocument(r9)
            if (r9 == 0) goto L_0x0153
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r9 = r14.types
            int r9 = r9.size()
            r12 = 0
        L_0x01b2:
            if (r12 >= r9) goto L_0x01cd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r15 = r14.types
            java.lang.Object r15 = r15.get(r12)
            org.telegram.tgnet.TLRPC$SecureRequiredType r15 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r15
            r22 = r4
            boolean r4 = r15 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r4 != 0) goto L_0x01c3
            goto L_0x01c8
        L_0x01c3:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r15 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r15
            r3.add(r15)
        L_0x01c8:
            int r12 = r12 + 1
            r4 = r22
            goto L_0x01b2
        L_0x01cd:
            r22 = r4
            int r8 = r8 + 1
        L_0x01d1:
            int r0 = r0 + 1
            r4 = r22
            r9 = 0
            r12 = 1
            r14 = -2
            r15 = -1
            goto L_0x0118
        L_0x01db:
            r22 = r4
            r0 = 1
            if (r1 == 0) goto L_0x01e5
            if (r2 <= r0) goto L_0x01e3
            goto L_0x01e5
        L_0x01e3:
            r9 = 0
            goto L_0x01e6
        L_0x01e5:
            r9 = 1
        L_0x01e6:
            if (r16 == 0) goto L_0x01ed
            if (r8 <= r0) goto L_0x01eb
            goto L_0x01ed
        L_0x01eb:
            r8 = 0
            goto L_0x01ee
        L_0x01ed:
            r8 = 1
        L_0x01ee:
            r12 = 0
        L_0x01ef:
            if (r12 >= r5) goto L_0x0306
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r0 = r0.required_types
            java.lang.Object r0 = r0.get(r12)
            org.telegram.tgnet.TLRPC$SecureRequiredType r0 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r0
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r1 == 0) goto L_0x0267
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r0
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
            if (r2 != 0) goto L_0x0262
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
            if (r2 == 0) goto L_0x020c
            goto L_0x0262
        L_0x020c:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            if (r2 == 0) goto L_0x0219
            if (r9 == 0) goto L_0x0214
        L_0x0212:
            r1 = 0
            goto L_0x0216
        L_0x0214:
            r1 = r22
        L_0x0216:
            r2 = r0
            r4 = r1
            goto L_0x0264
        L_0x0219:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r2 == 0) goto L_0x0222
            if (r8 == 0) goto L_0x0220
            goto L_0x0212
        L_0x0220:
            r1 = r3
            goto L_0x0216
        L_0x0222:
            if (r9 == 0) goto L_0x0243
            boolean r1 = r6.isPersonalDocument(r1)
            if (r1 == 0) goto L_0x0243
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.add(r0)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            r2.<init>()
            r0.type = r2
        L_0x023e:
            r2 = r0
            r4 = r1
            r14 = 1
            goto L_0x02e7
        L_0x0243:
            if (r8 == 0) goto L_0x02fc
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r6.isAddressDocument(r1)
            if (r1 == 0) goto L_0x02fc
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.add(r0)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            r2.<init>()
            r0.type = r2
            goto L_0x023e
        L_0x0262:
            r2 = r0
            r4 = 0
        L_0x0264:
            r14 = 0
            goto L_0x02e7
        L_0x0267:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf
            if (r1 == 0) goto L_0x02fc
            org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf r0 = (org.telegram.tgnet.TLRPC$TL_secureRequiredTypeOneOf) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r1 = r0.types
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0277
            goto L_0x02fc
        L_0x0277:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r1 = r0.types
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$SecureRequiredType r1 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r1
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r2 != 0) goto L_0x0286
            goto L_0x02fc
        L_0x0286:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r1 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r1
            if (r9 == 0) goto L_0x0292
            org.telegram.tgnet.TLRPC$SecureValueType r2 = r1.type
            boolean r2 = r6.isPersonalDocument(r2)
            if (r2 != 0) goto L_0x029c
        L_0x0292:
            if (r8 == 0) goto L_0x02fc
            org.telegram.tgnet.TLRPC$SecureValueType r2 = r1.type
            boolean r2 = r6.isAddressDocument(r2)
            if (r2 == 0) goto L_0x02fc
        L_0x029c:
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r4 = r0.types
            int r4 = r4.size()
            r14 = 0
        L_0x02a8:
            if (r14 >= r4) goto L_0x02c3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureRequiredType> r15 = r0.types
            java.lang.Object r15 = r15.get(r14)
            org.telegram.tgnet.TLRPC$SecureRequiredType r15 = (org.telegram.tgnet.TLRPC$SecureRequiredType) r15
            r16 = r0
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC$TL_secureRequiredType
            if (r0 != 0) goto L_0x02b9
            goto L_0x02be
        L_0x02b9:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r15 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r15
            r2.add(r15)
        L_0x02be:
            int r14 = r14 + 1
            r0 = r16
            goto L_0x02a8
        L_0x02c3:
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r1.type
            boolean r0 = r6.isPersonalDocument(r0)
            if (r0 == 0) goto L_0x02d8
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails r1 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            r1.<init>()
            r0.type = r1
            goto L_0x02e4
        L_0x02d8:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress r1 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            r1.<init>()
            r0.type = r1
        L_0x02e4:
            r4 = r2
            r14 = 1
            r2 = r0
        L_0x02e7:
            int r0 = r5 + -1
            if (r12 != r0) goto L_0x02ed
            r15 = 1
            goto L_0x02ee
        L_0x02ed:
            r15 = 0
        L_0x02ee:
            r0 = r23
            r1 = r24
            r16 = r3
            r3 = r4
            r4 = r14
            r14 = r5
            r5 = r15
            r0.addField(r1, r2, r3, r4, r5)
            goto L_0x02ff
        L_0x02fc:
            r16 = r3
            r14 = r5
        L_0x02ff:
            int r12 = r12 + 1
            r5 = r14
            r3 = r16
            goto L_0x01ef
        L_0x0306:
            if (r10 == 0) goto L_0x03c3
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r0.<init>(r7)
            r6.bottomCell = r0
            int r1 = org.telegram.messenger.R.drawable.greydivider_bottom
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r13)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            java.lang.String r1 = "windowBackgroundWhiteGrayText4"
            r0.setLinkTextColorKey(r1)
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            java.lang.String r0 = r0.privacy_policy_url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r2 = 2
            if (r0 != 0) goto L_0x0380
            int r0 = org.telegram.messenger.R.string.PassportPolicy
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r10)
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r10.username
            r4 = 1
            r2[r4] = r3
            java.lang.String r3 = "PassportPolicy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r0 = r0.lastIndexOf(r3)
            r3 = -1
            if (r4 == r3) goto L_0x037a
            if (r0 == r3) goto L_0x037a
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = r6.bottomCell
            android.widget.TextView r3 = r3.getTextView()
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r5 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r5.<init>()
            r3.setMovementMethod(r5)
            int r3 = r0 + 1
            java.lang.String r5 = ""
            r2.replace(r0, r3, r5)
            int r3 = r4 + 1
            r2.replace(r4, r3, r5)
            org.telegram.ui.PassportActivity$LinkSpan r3 = new org.telegram.ui.PassportActivity$LinkSpan
            r3.<init>()
            r5 = 1
            int r0 = r0 - r5
            r5 = 33
            r2.setSpan(r3, r4, r0, r5)
        L_0x037a:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            r0.setText(r2)
            goto L_0x039f
        L_0x0380:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            int r3 = org.telegram.messenger.R.string.PassportNoPolicy
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r10)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = r10.username
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "PassportNoPolicy"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r3, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x039f:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            android.widget.TextView r0 = r0.getTextView()
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setHighlightColor(r1)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.bottomCell
            android.widget.TextView r0 = r0.getTextView()
            r1 = 1
            r0.setGravity(r1)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell r1 = r6.bottomCell
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r0.addView(r1, r4)
        L_0x03c3:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.bottomLayout = r0
            java.lang.String r1 = "passport_authorizeBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.String r2 = "passport_authorizeBackgroundSelected"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorWithBackgroundDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.widget.FrameLayout r0 = r6.bottomLayout
            r1 = 48
            r2 = 80
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r1, (int) r2)
            r11.addView(r0, r1)
            android.widget.FrameLayout r0 = r6.bottomLayout
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda28 r1 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda28
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.acceptTextView = r0
            r1 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setCompoundDrawablePadding(r1)
            android.widget.TextView r0 = r6.acceptTextView
            int r1 = org.telegram.messenger.R.drawable.authorize
            r2 = 0
            r0.setCompoundDrawablesWithIntrinsicBounds(r1, r2, r2, r2)
            android.widget.TextView r0 = r6.acceptTextView
            java.lang.String r1 = "passport_authorizeText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.acceptTextView
            int r1 = org.telegram.messenger.R.string.PassportAuthorize
            java.lang.String r2 = "PassportAuthorize"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r6.acceptTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r6.acceptTextView
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.acceptTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            android.widget.FrameLayout r0 = r6.bottomLayout
            android.widget.TextView r2 = r6.acceptTextView
            r3 = -2
            r4 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r4, (int) r1)
            r0.addView(r2, r1)
            org.telegram.ui.Components.ContextProgressView r0 = new org.telegram.ui.Components.ContextProgressView
            r1 = 0
            r0.<init>(r7, r1)
            r6.progressViewButton = r0
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r6.bottomLayout
            org.telegram.ui.Components.ContextProgressView r1 = r6.progressViewButton
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r2)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            int r1 = org.telegram.messenger.R.drawable.header_shadow_reverse
            r0.setBackgroundResource(r1)
            r12 = -1
            r13 = 1077936128(0x40400000, float:3.0)
            r14 = 83
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r11.addView(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createRequestInterface(android.content.Context):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createRequestInterface$16(View view) {
        int i;
        ArrayList arrayList;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType;
        ArrayList arrayList2 = new ArrayList();
        int size = this.currentForm.required_types.size();
        int i2 = 0;
        while (i2 < size) {
            TLRPC$SecureRequiredType tLRPC$SecureRequiredType = this.currentForm.required_types.get(i2);
            if (tLRPC$SecureRequiredType instanceof TLRPC$TL_secureRequiredType) {
                tLRPC$TL_secureRequiredType = (TLRPC$TL_secureRequiredType) tLRPC$SecureRequiredType;
            } else {
                if (tLRPC$SecureRequiredType instanceof TLRPC$TL_secureRequiredTypeOneOf) {
                    TLRPC$TL_secureRequiredTypeOneOf tLRPC$TL_secureRequiredTypeOneOf = (TLRPC$TL_secureRequiredTypeOneOf) tLRPC$SecureRequiredType;
                    if (!tLRPC$TL_secureRequiredTypeOneOf.types.isEmpty()) {
                        TLRPC$SecureRequiredType tLRPC$SecureRequiredType2 = tLRPC$TL_secureRequiredTypeOneOf.types.get(0);
                        if (tLRPC$SecureRequiredType2 instanceof TLRPC$TL_secureRequiredType) {
                            TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2 = (TLRPC$TL_secureRequiredType) tLRPC$SecureRequiredType2;
                            int size2 = tLRPC$TL_secureRequiredTypeOneOf.types.size();
                            int i3 = 0;
                            while (true) {
                                if (i3 >= size2) {
                                    tLRPC$TL_secureRequiredType = tLRPC$TL_secureRequiredType2;
                                    break;
                                }
                                TLRPC$SecureRequiredType tLRPC$SecureRequiredType3 = tLRPC$TL_secureRequiredTypeOneOf.types.get(i3);
                                if (tLRPC$SecureRequiredType3 instanceof TLRPC$TL_secureRequiredType) {
                                    TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType3 = (TLRPC$TL_secureRequiredType) tLRPC$SecureRequiredType3;
                                    if (getValueByType(tLRPC$TL_secureRequiredType3, true) != null) {
                                        tLRPC$TL_secureRequiredType = tLRPC$TL_secureRequiredType3;
                                        break;
                                    }
                                }
                                i3++;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                i2++;
            }
            TLRPC$TL_secureValue valueByType = getValueByType(tLRPC$TL_secureRequiredType, true);
            if (valueByType == null) {
                Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(getViewByType(tLRPC$TL_secureRequiredType), 2.0f, 0);
                return;
            }
            HashMap hashMap = this.errorsMap.get(getNameForType(tLRPC$TL_secureRequiredType.type));
            if (hashMap == null || hashMap.isEmpty()) {
                arrayList2.add(new Object(this, valueByType, tLRPC$TL_secureRequiredType.selfie_required, tLRPC$TL_secureRequiredType.translation_required) {
                    boolean selfie_required;
                    boolean translation_required;
                    TLRPC$TL_secureValue value;

                    {
                        this.value = r2;
                        this.selfie_required = r3;
                        this.translation_required = r4;
                    }
                });
                i2++;
            } else {
                Vibrator vibrator2 = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (vibrator2 != null) {
                    vibrator2.vibrate(200);
                }
                AndroidUtilities.shakeView(getViewByType(tLRPC$TL_secureRequiredType), 2.0f, 0);
                return;
            }
        }
        showEditDoneProgress(false, true);
        TLRPC$TL_account_acceptAuthorization tLRPC$TL_account_acceptAuthorization = new TLRPC$TL_account_acceptAuthorization();
        tLRPC$TL_account_acceptAuthorization.bot_id = this.currentBotId;
        tLRPC$TL_account_acceptAuthorization.scope = this.currentScope;
        tLRPC$TL_account_acceptAuthorization.public_key = this.currentPublicKey;
        JSONObject jSONObject = new JSONObject();
        int size3 = arrayList2.size();
        int i4 = 0;
        while (i4 < size3) {
            AnonymousClass1ValueToSend r9 = (AnonymousClass1ValueToSend) arrayList2.get(i4);
            TLRPC$TL_secureValue tLRPC$TL_secureValue = r9.value;
            JSONObject jSONObject2 = new JSONObject();
            TLRPC$SecurePlainData tLRPC$SecurePlainData = tLRPC$TL_secureValue.plain_data;
            if (tLRPC$SecurePlainData == null) {
                try {
                    JSONObject jSONObject3 = new JSONObject();
                    TLRPC$TL_secureData tLRPC$TL_secureData = tLRPC$TL_secureValue.data;
                    if (tLRPC$TL_secureData != null) {
                        byte[] decryptValueSecret = decryptValueSecret(tLRPC$TL_secureData.secret, tLRPC$TL_secureData.data_hash);
                        jSONObject2.put("data_hash", Base64.encodeToString(tLRPC$TL_secureValue.data.data_hash, 2));
                        jSONObject2.put("secret", Base64.encodeToString(decryptValueSecret, 2));
                        jSONObject3.put("data", jSONObject2);
                    }
                    if (!tLRPC$TL_secureValue.files.isEmpty()) {
                        JSONArray jSONArray = new JSONArray();
                        int size4 = tLRPC$TL_secureValue.files.size();
                        int i5 = 0;
                        while (i5 < size4) {
                            TLRPC$TL_secureFile tLRPC$TL_secureFile = (TLRPC$TL_secureFile) tLRPC$TL_secureValue.files.get(i5);
                            arrayList = arrayList2;
                            try {
                                i = size3;
                                try {
                                    byte[] decryptValueSecret2 = decryptValueSecret(tLRPC$TL_secureFile.secret, tLRPC$TL_secureFile.file_hash);
                                    JSONObject jSONObject4 = new JSONObject();
                                    jSONObject4.put("file_hash", Base64.encodeToString(tLRPC$TL_secureFile.file_hash, 2));
                                    jSONObject4.put("secret", Base64.encodeToString(decryptValueSecret2, 2));
                                    jSONArray.put(jSONObject4);
                                    i5++;
                                    arrayList2 = arrayList;
                                    size3 = i;
                                    size4 = size4;
                                } catch (Exception unused) {
                                }
                            } catch (Exception unused2) {
                            }
                        }
                        arrayList = arrayList2;
                        i = size3;
                        jSONObject3.put("files", jSONArray);
                    } else {
                        arrayList = arrayList2;
                        i = size3;
                    }
                    TLRPC$SecureFile tLRPC$SecureFile = tLRPC$TL_secureValue.front_side;
                    if (tLRPC$SecureFile instanceof TLRPC$TL_secureFile) {
                        TLRPC$TL_secureFile tLRPC$TL_secureFile2 = (TLRPC$TL_secureFile) tLRPC$SecureFile;
                        byte[] decryptValueSecret3 = decryptValueSecret(tLRPC$TL_secureFile2.secret, tLRPC$TL_secureFile2.file_hash);
                        JSONObject jSONObject5 = new JSONObject();
                        jSONObject5.put("file_hash", Base64.encodeToString(tLRPC$TL_secureFile2.file_hash, 2));
                        jSONObject5.put("secret", Base64.encodeToString(decryptValueSecret3, 2));
                        jSONObject3.put("front_side", jSONObject5);
                    }
                    TLRPC$SecureFile tLRPC$SecureFile2 = tLRPC$TL_secureValue.reverse_side;
                    if (tLRPC$SecureFile2 instanceof TLRPC$TL_secureFile) {
                        TLRPC$TL_secureFile tLRPC$TL_secureFile3 = (TLRPC$TL_secureFile) tLRPC$SecureFile2;
                        byte[] decryptValueSecret4 = decryptValueSecret(tLRPC$TL_secureFile3.secret, tLRPC$TL_secureFile3.file_hash);
                        JSONObject jSONObject6 = new JSONObject();
                        jSONObject6.put("file_hash", Base64.encodeToString(tLRPC$TL_secureFile3.file_hash, 2));
                        jSONObject6.put("secret", Base64.encodeToString(decryptValueSecret4, 2));
                        jSONObject3.put("reverse_side", jSONObject6);
                    }
                    if (r9.selfie_required) {
                        TLRPC$SecureFile tLRPC$SecureFile3 = tLRPC$TL_secureValue.selfie;
                        if (tLRPC$SecureFile3 instanceof TLRPC$TL_secureFile) {
                            TLRPC$TL_secureFile tLRPC$TL_secureFile4 = (TLRPC$TL_secureFile) tLRPC$SecureFile3;
                            byte[] decryptValueSecret5 = decryptValueSecret(tLRPC$TL_secureFile4.secret, tLRPC$TL_secureFile4.file_hash);
                            JSONObject jSONObject7 = new JSONObject();
                            jSONObject7.put("file_hash", Base64.encodeToString(tLRPC$TL_secureFile4.file_hash, 2));
                            jSONObject7.put("secret", Base64.encodeToString(decryptValueSecret5, 2));
                            jSONObject3.put("selfie", jSONObject7);
                        }
                    }
                    if (r9.translation_required && !tLRPC$TL_secureValue.translation.isEmpty()) {
                        JSONArray jSONArray2 = new JSONArray();
                        int size5 = tLRPC$TL_secureValue.translation.size();
                        for (int i6 = 0; i6 < size5; i6++) {
                            TLRPC$TL_secureFile tLRPC$TL_secureFile5 = (TLRPC$TL_secureFile) tLRPC$TL_secureValue.translation.get(i6);
                            byte[] decryptValueSecret6 = decryptValueSecret(tLRPC$TL_secureFile5.secret, tLRPC$TL_secureFile5.file_hash);
                            JSONObject jSONObject8 = new JSONObject();
                            jSONObject8.put("file_hash", Base64.encodeToString(tLRPC$TL_secureFile5.file_hash, 2));
                            jSONObject8.put("secret", Base64.encodeToString(decryptValueSecret6, 2));
                            jSONArray2.put(jSONObject8);
                        }
                        jSONObject3.put("translation", jSONArray2);
                    }
                    jSONObject.put(getNameForType(tLRPC$TL_secureValue.type), jSONObject3);
                } catch (Exception unused3) {
                }
                TLRPC$TL_secureValueHash tLRPC$TL_secureValueHash = new TLRPC$TL_secureValueHash();
                tLRPC$TL_secureValueHash.type = tLRPC$TL_secureValue.type;
                tLRPC$TL_secureValueHash.hash = tLRPC$TL_secureValue.hash;
                tLRPC$TL_account_acceptAuthorization.value_hashes.add(tLRPC$TL_secureValueHash);
                i4++;
                arrayList2 = arrayList;
                size3 = i;
            } else if (tLRPC$SecurePlainData instanceof TLRPC$TL_securePlainEmail) {
                TLRPC$TL_securePlainEmail tLRPC$TL_securePlainEmail = (TLRPC$TL_securePlainEmail) tLRPC$SecurePlainData;
            } else if (tLRPC$SecurePlainData instanceof TLRPC$TL_securePlainPhone) {
                TLRPC$TL_securePlainPhone tLRPC$TL_securePlainPhone = (TLRPC$TL_securePlainPhone) tLRPC$SecurePlainData;
            }
            arrayList = arrayList2;
            i = size3;
            TLRPC$TL_secureValueHash tLRPC$TL_secureValueHash2 = new TLRPC$TL_secureValueHash();
            tLRPC$TL_secureValueHash2.type = tLRPC$TL_secureValue.type;
            tLRPC$TL_secureValueHash2.hash = tLRPC$TL_secureValue.hash;
            tLRPC$TL_account_acceptAuthorization.value_hashes.add(tLRPC$TL_secureValueHash2);
            i4++;
            arrayList2 = arrayList;
            size3 = i;
        }
        JSONObject jSONObject9 = new JSONObject();
        try {
            jSONObject9.put("secure_data", jSONObject);
        } catch (Exception unused4) {
        }
        String str = this.currentPayload;
        if (str != null) {
            try {
                jSONObject9.put("payload", str);
            } catch (Exception unused5) {
            }
        }
        String str2 = this.currentNonce;
        if (str2 != null) {
            try {
                jSONObject9.put("nonce", str2);
            } catch (Exception unused6) {
            }
        }
        EncryptionResult encryptData = encryptData(AndroidUtilities.getStringBytes(jSONObject9.toString()));
        TLRPC$TL_secureCredentialsEncrypted tLRPC$TL_secureCredentialsEncrypted = new TLRPC$TL_secureCredentialsEncrypted();
        tLRPC$TL_account_acceptAuthorization.credentials = tLRPC$TL_secureCredentialsEncrypted;
        tLRPC$TL_secureCredentialsEncrypted.hash = encryptData.fileHash;
        tLRPC$TL_secureCredentialsEncrypted.data = encryptData.encryptedData;
        try {
            String replace = this.currentPublicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            Cipher instance = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding");
            instance.init(1, (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(replace, 0))));
            tLRPC$TL_account_acceptAuthorization.credentials.secret = instance.doFinal(encryptData.decrypyedFileSecret);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_acceptAuthorization, new PassportActivity$$ExternalSyntheticLambda65(this)), this.classGuid);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createRequestInterface$15(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda57(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createRequestInterface$14(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.ignoreOnFailure = true;
            callCallback(true);
            finishFragment();
            return;
        }
        showEditDoneProgress(false, false);
        if ("APP_VERSION_OUTDATED".equals(tLRPC$TL_error.text)) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), tLRPC$TL_error.text);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x02c4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createManageInterface(android.content.Context r20) {
        /*
            r19 = this;
            r6 = r19
            r7 = r20
            android.view.View r0 = r6.fragmentView
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            int r1 = org.telegram.messenger.R.string.TelegramPassport
            java.lang.String r2 = "TelegramPassport"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            int r1 = org.telegram.messenger.R.drawable.msg_info
            r8 = 1
            r0.addItem((int) r8, (int) r1)
            org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
            r0.<init>(r7)
            r6.headerCell = r0
            int r1 = org.telegram.messenger.R.string.PassportProvidedInformation
            java.lang.String r2 = "PassportProvidedInformation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Cells.HeaderCell r0 = r6.headerCell
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.HeaderCell r1 = r6.headerCell
            r2 = -1
            r3 = -2
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3)
            r0.addView(r1, r4)
            org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
            r0.<init>(r7)
            r6.sectionCell = r0
            int r1 = org.telegram.messenger.R.drawable.greydivider
            java.lang.String r4 = "windowBackgroundGrayShadow"
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r4)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell r1 = r6.sectionCell
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3)
            r0.addView(r1, r5)
            org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
            r0.<init>(r7)
            r6.addDocumentCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.addDocumentCell
            int r1 = org.telegram.messenger.R.string.PassportNoDocumentsAdd
            java.lang.String r5 = "PassportNoDocumentsAdd"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r0.setText(r9, r8)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell r9 = r6.addDocumentCell
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3)
            r0.addView(r9, r10)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.addDocumentCell
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda17 r9 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda17
            r9.<init>(r6)
            r0.setOnClickListener(r9)
            org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
            r0.<init>(r7)
            r6.deletePassportCell = r0
            java.lang.String r9 = "windowBackgroundWhiteRedText3"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r0.setTextColor(r9)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deletePassportCell
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8)
            r0.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deletePassportCell
            int r9 = org.telegram.messenger.R.string.TelegramPassportDelete
            java.lang.String r10 = "TelegramPassportDelete"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r10 = 0
            r0.setText(r9, r10)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.TextSettingsCell r9 = r6.deletePassportCell
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3)
            r0.addView(r9, r11)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deletePassportCell
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda18 r9 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda18
            r9.<init>(r6)
            r0.setOnClickListener(r9)
            org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
            r0.<init>(r7)
            r6.addDocumentSectionCell = r0
            int r9 = org.telegram.messenger.R.drawable.greydivider_bottom
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r9, (java.lang.String) r4)
            r0.setBackgroundDrawable(r11)
            android.widget.LinearLayout r0 = r6.linearLayout2
            org.telegram.ui.Cells.ShadowSectionCell r11 = r6.addDocumentSectionCell
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3)
            r0.addView(r11, r12)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r7)
            r6.emptyLayout = r0
            r0.setOrientation(r8)
            android.widget.LinearLayout r0 = r6.emptyLayout
            r11 = 17
            r0.setGravity(r11)
            android.widget.LinearLayout r0 = r6.emptyLayout
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r9, (java.lang.String) r4)
            r0.setBackgroundDrawable(r4)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0127
            android.widget.LinearLayout r0 = r6.linearLayout2
            android.widget.LinearLayout r4 = r6.emptyLayout
            android.widget.LinearLayout$LayoutParams r9 = new android.widget.LinearLayout$LayoutParams
            r12 = 1141112832(0x44040000, float:528.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r12 = r12 - r13
            r9.<init>(r2, r12)
            r0.addView(r4, r9)
            goto L_0x013c
        L_0x0127:
            android.widget.LinearLayout r0 = r6.linearLayout2
            android.widget.LinearLayout r4 = r6.emptyLayout
            android.widget.LinearLayout$LayoutParams r9 = new android.widget.LinearLayout$LayoutParams
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
            int r12 = r12.y
            int r13 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r12 = r12 - r13
            r9.<init>(r2, r12)
            r0.addView(r4, r9)
        L_0x013c:
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.emptyImageView = r0
            int r2 = org.telegram.messenger.R.drawable.no_passport
            r0.setImageResource(r2)
            android.widget.ImageView r0 = r6.emptyImageView
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "sessions_devicesImage"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r4, r9)
            r0.setColorFilter(r2)
            android.widget.LinearLayout r0 = r6.emptyLayout
            android.widget.ImageView r2 = r6.emptyImageView
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.emptyTextView1 = r0
            java.lang.String r2 = "windowBackgroundWhiteGrayText2"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r6.emptyTextView1
            r0.setGravity(r11)
            android.widget.TextView r0 = r6.emptyTextView1
            r3 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r3)
            android.widget.TextView r0 = r6.emptyTextView1
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r0.setTypeface(r9)
            android.widget.TextView r0 = r6.emptyTextView1
            int r9 = org.telegram.messenger.R.string.PassportNoDocuments
            java.lang.String r12 = "PassportNoDocuments"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r0.setText(r9)
            android.widget.LinearLayout r0 = r6.emptyLayout
            android.widget.TextView r9 = r6.emptyTextView1
            r12 = -2
            r13 = -2
            r14 = 17
            r15 = 0
            r16 = 16
            r17 = 0
            r18 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r0.addView(r9, r12)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.emptyTextView2 = r0
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r6.emptyTextView2
            r0.setGravity(r11)
            android.widget.TextView r0 = r6.emptyTextView2
            r2 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r2)
            android.widget.TextView r0 = r6.emptyTextView2
            r2 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r10, r2, r10)
            android.widget.TextView r0 = r6.emptyTextView2
            int r2 = org.telegram.messenger.R.string.PassportNoDocumentsInfo
            java.lang.String r9 = "PassportNoDocumentsInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r0.setText(r2)
            android.widget.LinearLayout r0 = r6.emptyLayout
            android.widget.TextView r2 = r6.emptyTextView2
            r12 = -2
            r16 = 14
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r0.addView(r2, r9)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.emptyTextView3 = r0
            java.lang.String r2 = "windowBackgroundWhiteBlueText4"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r6.emptyTextView3
            r0.setGravity(r11)
            android.widget.TextView r0 = r6.emptyTextView3
            r0.setTextSize(r8, r3)
            android.widget.TextView r0 = r6.emptyTextView3
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r6.emptyTextView3
            r0.setGravity(r11)
            android.widget.TextView r0 = r6.emptyTextView3
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.LinearLayout r0 = r6.emptyLayout
            android.widget.TextView r1 = r6.emptyTextView3
            r11 = -2
            r12 = 30
            r13 = 17
            r14 = 0
            r15 = 16
            r16 = 0
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r0.addView(r1, r2)
            android.widget.TextView r0 = r6.emptyTextView3
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda16 r1 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda16
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_secureValue> r0 = r0.values
            int r9 = r0.size()
            r11 = 0
        L_0x0250:
            if (r11 >= r9) goto L_0x02cf
            org.telegram.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_secureValue> r0 = r0.values
            java.lang.Object r0 = r0.get(r11)
            org.telegram.tgnet.TLRPC$TL_secureValue r0 = (org.telegram.tgnet.TLRPC$TL_secureValue) r0
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r6.isPersonalDocument(r1)
            if (r1 == 0) goto L_0x0289
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r2 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r2.<init>()
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r0.type
            r2.type = r0
            r2.selfie_required = r8
            r2.translation_required = r8
            r1.add(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            r2.<init>()
            r0.type = r2
        L_0x0285:
            r2 = r0
            r3 = r1
            r4 = 1
            goto L_0x02be
        L_0x0289:
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r6.isAddressDocument(r1)
            if (r1 == 0) goto L_0x02b1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r2 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r2.<init>()
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r0.type
            r2.type = r0
            r2.translation_required = r8
            r1.add(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress r2 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            r2.<init>()
            r0.type = r2
            goto L_0x0285
        L_0x02b1:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r1 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r1.<init>()
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r0.type
            r1.type = r0
            r0 = 0
            r3 = r0
            r2 = r1
            r4 = 0
        L_0x02be:
            int r0 = r9 + -1
            if (r11 != r0) goto L_0x02c4
            r5 = 1
            goto L_0x02c5
        L_0x02c4:
            r5 = 0
        L_0x02c5:
            r0 = r19
            r1 = r20
            r0.addField(r1, r2, r3, r4, r5)
            int r11 = r11 + 1
            goto L_0x0250
        L_0x02cf:
            r19.updateManageVisibility()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createManageInterface(android.content.Context):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageInterface$17(View view) {
        openAddDocumentAlert();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageInterface$21(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("TelegramPassportDeleteTitle", R.string.TelegramPassportDeleteTitle));
        builder.setMessage(LocaleController.getString("TelegramPassportDeleteAlert", R.string.TelegramPassportDeleteAlert));
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new PassportActivity$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageInterface$20(DialogInterface dialogInterface, int i) {
        TLRPC$TL_account_deleteSecureValue tLRPC$TL_account_deleteSecureValue = new TLRPC$TL_account_deleteSecureValue();
        for (int i2 = 0; i2 < this.currentForm.values.size(); i2++) {
            tLRPC$TL_account_deleteSecureValue.types.add(this.currentForm.values.get(i2).type);
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_deleteSecureValue, new PassportActivity$$ExternalSyntheticLambda64(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageInterface$19(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda51(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageInterface$18() {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createManageInterface$22(View view) {
        openAddDocumentAlert();
    }

    private boolean hasNotValueForType(Class<? extends TLRPC$SecureValueType> cls) {
        int size = this.currentForm.values.size();
        for (int i = 0; i < size; i++) {
            if (this.currentForm.values.get(i).type.getClass() == cls) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnfilledValues() {
        return hasNotValueForType(TLRPC$TL_secureValueTypePhone.class) || hasNotValueForType(TLRPC$TL_secureValueTypeEmail.class) || hasNotValueForType(TLRPC$TL_secureValueTypePersonalDetails.class) || hasNotValueForType(TLRPC$TL_secureValueTypePassport.class) || hasNotValueForType(TLRPC$TL_secureValueTypeInternalPassport.class) || hasNotValueForType(TLRPC$TL_secureValueTypeIdentityCard.class) || hasNotValueForType(TLRPC$TL_secureValueTypeDriverLicense.class) || hasNotValueForType(TLRPC$TL_secureValueTypeAddress.class) || hasNotValueForType(TLRPC$TL_secureValueTypeUtilityBill.class) || hasNotValueForType(TLRPC$TL_secureValueTypePassportRegistration.class) || hasNotValueForType(TLRPC$TL_secureValueTypeTemporaryRegistration.class) || hasNotValueForType(TLRPC$TL_secureValueTypeBankStatement.class) || hasNotValueForType(TLRPC$TL_secureValueTypeRentalAgreement.class);
    }

    private void openAddDocumentAlert() {
        Class<TLRPC$TL_secureValueTypeBankStatement> cls;
        Class<TLRPC$TL_secureValueTypeRentalAgreement> cls2;
        Class<TLRPC$TL_secureValueTypeRentalAgreement> cls3 = TLRPC$TL_secureValueTypeRentalAgreement.class;
        Class<TLRPC$TL_secureValueTypeBankStatement> cls4 = TLRPC$TL_secureValueTypeBankStatement.class;
        Class<TLRPC$TL_secureValueTypeUtilityBill> cls5 = TLRPC$TL_secureValueTypeUtilityBill.class;
        Class<TLRPC$TL_secureValueTypeAddress> cls6 = TLRPC$TL_secureValueTypeAddress.class;
        Class<TLRPC$TL_secureValueTypeDriverLicense> cls7 = TLRPC$TL_secureValueTypeDriverLicense.class;
        Class<TLRPC$TL_secureValueTypeIdentityCard> cls8 = TLRPC$TL_secureValueTypeIdentityCard.class;
        Class<TLRPC$TL_secureValueTypeTemporaryRegistration> cls9 = TLRPC$TL_secureValueTypeTemporaryRegistration.class;
        Class<TLRPC$TL_secureValueTypePassportRegistration> cls10 = TLRPC$TL_secureValueTypePassportRegistration.class;
        Class<TLRPC$TL_secureValueTypeInternalPassport> cls11 = TLRPC$TL_secureValueTypeInternalPassport.class;
        Class<TLRPC$TL_secureValueTypePassport> cls12 = TLRPC$TL_secureValueTypePassport.class;
        Class<TLRPC$TL_secureValueTypePersonalDetails> cls13 = TLRPC$TL_secureValueTypePersonalDetails.class;
        Class<TLRPC$TL_secureValueTypeEmail> cls14 = TLRPC$TL_secureValueTypeEmail.class;
        Class<TLRPC$TL_secureValueTypePhone> cls15 = TLRPC$TL_secureValueTypePhone.class;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (hasNotValueForType(cls15)) {
            cls2 = cls3;
            cls = cls4;
            arrayList.add(LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone));
            arrayList2.add(cls15);
        } else {
            cls2 = cls3;
            cls = cls4;
        }
        if (hasNotValueForType(cls14)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail));
            arrayList2.add(cls14);
        }
        if (hasNotValueForType(cls13)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentIdentity", R.string.ActionBotDocumentIdentity));
            arrayList2.add(cls13);
        }
        if (hasNotValueForType(cls12)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport));
            arrayList2.add(cls12);
        }
        if (hasNotValueForType(cls11)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport));
            arrayList2.add(cls11);
        }
        if (hasNotValueForType(cls10)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            arrayList2.add(cls10);
        }
        if (hasNotValueForType(cls9)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            arrayList2.add(cls9);
        }
        if (hasNotValueForType(cls8)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard));
            arrayList2.add(cls8);
        }
        if (hasNotValueForType(cls7)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence));
            arrayList2.add(cls7);
        }
        if (hasNotValueForType(cls6)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentAddress", R.string.ActionBotDocumentAddress));
            arrayList2.add(cls6);
        }
        if (hasNotValueForType(cls5)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            arrayList2.add(cls5);
        }
        Class<TLRPC$TL_secureValueTypeBankStatement> cls16 = cls;
        if (hasNotValueForType(cls16)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            arrayList2.add(cls16);
        }
        Class<TLRPC$TL_secureValueTypeRentalAgreement> cls17 = cls2;
        if (hasNotValueForType(cls17)) {
            arrayList.add(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            arrayList2.add(cls17);
        }
        if (getParentActivity() != null && !arrayList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd));
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new PassportActivity$$ExternalSyntheticLambda5(this, arrayList2));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0051  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$openAddDocumentAlert$23(java.util.ArrayList r2, android.content.DialogInterface r3, int r4) {
        /*
            r1 = this;
            r3 = 0
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType     // Catch:{ Exception -> 0x0017 }
            r0.<init>()     // Catch:{ Exception -> 0x0017 }
            java.lang.Object r2 = r2.get(r4)     // Catch:{ Exception -> 0x0015 }
            java.lang.Class r2 = (java.lang.Class) r2     // Catch:{ Exception -> 0x0015 }
            java.lang.Object r2 = r2.newInstance()     // Catch:{ Exception -> 0x0015 }
            org.telegram.tgnet.TLRPC$SecureValueType r2 = (org.telegram.tgnet.TLRPC$SecureValueType) r2     // Catch:{ Exception -> 0x0015 }
            r0.type = r2     // Catch:{ Exception -> 0x0015 }
            goto L_0x0018
        L_0x0015:
            goto L_0x0018
        L_0x0017:
            r0 = r3
        L_0x0018:
            org.telegram.tgnet.TLRPC$SecureValueType r2 = r0.type
            boolean r2 = r1.isPersonalDocument(r2)
            r4 = 1
            if (r2 == 0) goto L_0x0034
            r0.selfie_required = r4
            r0.translation_required = r4
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r2 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r2.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails r3 = new org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            r3.<init>()
            r2.type = r3
        L_0x0031:
            r3 = r0
            r0 = r2
            goto L_0x0049
        L_0x0034:
            org.telegram.tgnet.TLRPC$SecureValueType r2 = r0.type
            boolean r2 = r1.isAddressDocument(r2)
            if (r2 == 0) goto L_0x0049
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r2 = new org.telegram.tgnet.TLRPC$TL_secureRequiredType
            r2.<init>()
            org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress r3 = new org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            r3.<init>()
            r2.type = r3
            goto L_0x0031
        L_0x0049:
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            if (r3 == 0) goto L_0x0051
            goto L_0x0052
        L_0x0051:
            r4 = 0
        L_0x0052:
            r1.openTypeActivity(r0, r3, r2, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.lambda$openAddDocumentAlert$23(java.util.ArrayList, android.content.DialogInterface, int):void");
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

    /* access modifiers changed from: private */
    public void callCallback(boolean z) {
        int i;
        int i2;
        if (this.callbackCalled) {
            return;
        }
        if (!TextUtils.isEmpty(this.currentCallbackUrl)) {
            if (z) {
                Activity parentActivity = getParentActivity();
                Browser.openUrl((Context) parentActivity, Uri.parse(this.currentCallbackUrl + "&tg_passport=success"));
            } else if (!this.ignoreOnFailure && ((i2 = this.currentActivityType) == 5 || i2 == 0)) {
                Activity parentActivity2 = getParentActivity();
                Browser.openUrl((Context) parentActivity2, Uri.parse(this.currentCallbackUrl + "&tg_passport=cancel"));
            }
            this.callbackCalled = true;
        } else if (this.needActivityResult) {
            if (z || (!this.ignoreOnFailure && ((i = this.currentActivityType) == 5 || i == 0))) {
                getParentActivity().setResult(z ? -1 : 0);
            }
            this.callbackCalled = true;
        }
    }

    private void createEmailInterface(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            textSettingsCell.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, this.currentEmail), false);
            this.linearLayout2.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
            textSettingsCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda20(this));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameEmailInfo", R.string.PassportPhoneUseSameEmailInfo));
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
            this.inputFields[i].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i].setBackgroundDrawable((Drawable) null);
            this.inputFields[i].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i].setCursorWidth(1.5f);
            this.inputFields[i].setInputType(33);
            this.inputFields[i].setImeOptions(NUM);
            this.inputFields[i].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
            TLRPC$TL_secureValue tLRPC$TL_secureValue = this.currentTypeValue;
            if (tLRPC$TL_secureValue != null) {
                TLRPC$SecurePlainData tLRPC$SecurePlainData = tLRPC$TL_secureValue.plain_data;
                if (tLRPC$SecurePlainData instanceof TLRPC$TL_securePlainEmail) {
                    TLRPC$TL_securePlainEmail tLRPC$TL_securePlainEmail = (TLRPC$TL_securePlainEmail) tLRPC$SecurePlainData;
                    if (!TextUtils.isEmpty(tLRPC$TL_securePlainEmail.email)) {
                        this.inputFields[i].setText(tLRPC$TL_securePlainEmail.email);
                    }
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            this.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[i].setGravity(LocaleController.isRTL ? 5 : 3);
            frameLayout.addView(this.inputFields[i], LayoutHelper.createFrame(-1, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[i].setOnEditorActionListener(new PassportActivity$$ExternalSyntheticLambda44(this));
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.bottomCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.getString("PassportEmailUploadInfo", R.string.PassportEmailUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createEmailInterface$24(View view) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createEmailInterface$25(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v7, resolved type: android.widget.FrameLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: android.widget.LinearLayout} */
    /* JADX WARNING: type inference failed for: r13v19, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createPhoneInterface(android.content.Context r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            org.telegram.ui.ActionBar.ActionBar r0 = r1.actionBar
            int r3 = org.telegram.messenger.R.string.PassportPhone
            java.lang.String r4 = "PassportPhone"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setTitle(r3)
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r1.languageMap = r0
            r3 = 3
            r4 = 2
            r5 = 1
            r6 = 0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0073 }
            java.io.InputStreamReader r7 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0073 }
            android.content.res.Resources r8 = r25.getResources()     // Catch:{ Exception -> 0x0073 }
            android.content.res.AssetManager r8 = r8.getAssets()     // Catch:{ Exception -> 0x0073 }
            java.lang.String r9 = "countries.txt"
            java.io.InputStream r8 = r8.open(r9)     // Catch:{ Exception -> 0x0073 }
            r7.<init>(r8)     // Catch:{ Exception -> 0x0073 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0073 }
        L_0x0034:
            java.lang.String r7 = r0.readLine()     // Catch:{ Exception -> 0x0073 }
            if (r7 == 0) goto L_0x006f
            java.lang.String r8 = ";"
            java.lang.String[] r7 = r7.split(r8)     // Catch:{ Exception -> 0x0073 }
            java.util.ArrayList<java.lang.String> r8 = r1.countriesArray     // Catch:{ Exception -> 0x0073 }
            r9 = r7[r4]     // Catch:{ Exception -> 0x0073 }
            r8.add(r6, r9)     // Catch:{ Exception -> 0x0073 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.countriesMap     // Catch:{ Exception -> 0x0073 }
            r9 = r7[r4]     // Catch:{ Exception -> 0x0073 }
            r10 = r7[r6]     // Catch:{ Exception -> 0x0073 }
            r8.put(r9, r10)     // Catch:{ Exception -> 0x0073 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.codesMap     // Catch:{ Exception -> 0x0073 }
            r9 = r7[r6]     // Catch:{ Exception -> 0x0073 }
            r10 = r7[r4]     // Catch:{ Exception -> 0x0073 }
            r8.put(r9, r10)     // Catch:{ Exception -> 0x0073 }
            int r8 = r7.length     // Catch:{ Exception -> 0x0073 }
            if (r8 <= r3) goto L_0x0065
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0073 }
            r9 = r7[r6]     // Catch:{ Exception -> 0x0073 }
            r10 = r7[r3]     // Catch:{ Exception -> 0x0073 }
            r8.put(r9, r10)     // Catch:{ Exception -> 0x0073 }
        L_0x0065:
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.languageMap     // Catch:{ Exception -> 0x0073 }
            r9 = r7[r5]     // Catch:{ Exception -> 0x0073 }
            r7 = r7[r4]     // Catch:{ Exception -> 0x0073 }
            r8.put(r9, r7)     // Catch:{ Exception -> 0x0073 }
            goto L_0x0034
        L_0x006f:
            r0.close()     // Catch:{ Exception -> 0x0073 }
            goto L_0x0077
        L_0x0073:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0077:
            java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
            org.telegram.ui.CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0 r7 = org.telegram.ui.CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE
            java.util.Collections.sort(r0, r7)
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            java.lang.String r0 = r0.phone
            org.telegram.ui.Cells.TextSettingsCell r7 = new org.telegram.ui.Cells.TextSettingsCell
            r7.<init>(r2)
            java.lang.String r8 = "windowBackgroundWhiteBlueText4"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.setTextColor(r8)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r7.setBackgroundDrawable(r8)
            int r8 = org.telegram.messenger.R.string.PassportPhoneUseSame
            java.lang.Object[] r9 = new java.lang.Object[r5]
            org.telegram.PhoneFormat.PhoneFormat r10 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "+"
            r11.append(r12)
            r11.append(r0)
            java.lang.String r0 = r11.toString()
            java.lang.String r0 = r10.format(r0)
            r9[r6] = r0
            java.lang.String r0 = "PassportPhoneUseSame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r8, r9)
            r7.setText(r0, r6)
            android.widget.LinearLayout r0 = r1.linearLayout2
            r8 = -1
            r9 = -2
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r9)
            r0.addView(r7, r10)
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda25 r0 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda25
            r0.<init>(r1)
            r7.setOnClickListener(r0)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r0.<init>(r2)
            r1.bottomCell = r0
            int r7 = org.telegram.messenger.R.drawable.greydivider_bottom
            java.lang.String r10 = "windowBackgroundGrayShadow"
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r7, (java.lang.String) r10)
            r0.setBackgroundDrawable(r7)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r1.bottomCell
            int r7 = org.telegram.messenger.R.string.PassportPhoneUseSameInfo
            java.lang.String r11 = "PassportPhoneUseSameInfo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r0.setText(r7)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = r1.bottomCell
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r9)
            r0.addView(r7, r11)
            org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
            r0.<init>(r2)
            r1.headerCell = r0
            int r7 = org.telegram.messenger.R.string.PassportPhoneUseOther
            java.lang.String r11 = "PassportPhoneUseOther"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r0.setText(r7)
            org.telegram.ui.Cells.HeaderCell r0 = r1.headerCell
            java.lang.String r7 = "windowBackgroundWhite"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r0.setBackgroundColor(r11)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.HeaderCell r11 = r1.headerCell
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r9)
            r0.addView(r11, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r0 = new org.telegram.ui.Components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x0131:
            r11 = 0
            if (r0 >= r3) goto L_0x0369
            if (r0 != r4) goto L_0x0140
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            org.telegram.ui.Components.HintEditText r14 = new org.telegram.ui.Components.HintEditText
            r14.<init>(r2)
            r13[r0] = r14
            goto L_0x0149
        L_0x0140:
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            org.telegram.ui.Components.EditTextBoldCursor r14 = new org.telegram.ui.Components.EditTextBoldCursor
            r14.<init>(r2)
            r13[r0] = r14
        L_0x0149:
            r13 = 50
            if (r0 != r5) goto L_0x0166
            android.widget.LinearLayout r14 = new android.widget.LinearLayout
            r14.<init>(r2)
            r14.setOrientation(r6)
            android.widget.LinearLayout r15 = r1.linearLayout2
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r13)
            r15.addView(r14, r13)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r14.setBackgroundColor(r13)
            goto L_0x0189
        L_0x0166:
            if (r0 != r4) goto L_0x0174
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r5]
            android.view.ViewParent r13 = r13.getParent()
            r14 = r13
            android.view.ViewGroup r14 = (android.view.ViewGroup) r14
            goto L_0x0189
        L_0x0174:
            android.widget.FrameLayout r14 = new android.widget.FrameLayout
            r14.<init>(r2)
            android.widget.LinearLayout r15 = r1.linearLayout2
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r13)
            r15.addView(r14, r13)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r14.setBackgroundColor(r13)
        L_0x0189:
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r0)
            r13.setTag(r15)
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            r15 = 1098907648(0x41800000, float:16.0)
            r13.setTextSize(r5, r15)
            org.telegram.ui.Components.EditTextBoldCursor[] r13 = r1.inputFields
            r13 = r13[r0]
            java.lang.String r16 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r13.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            java.lang.String r13 = "windowBackgroundWhiteBlackText"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r9.setTextColor(r8)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r8.setBackgroundDrawable(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r8.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r8.setCursorWidth(r9)
            if (r0 != 0) goto L_0x020b
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda35 r9 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda35
            r9.<init>(r1)
            r8.setOnTouchListener(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            int r9 = org.telegram.messenger.R.string.ChooseCountry
            java.lang.String r11 = "ChooseCountry"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r8.setText(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r8.setInputType(r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r8.setFocusable(r6)
            goto L_0x0229
        L_0x020b:
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r8.setInputType(r3)
            if (r0 != r4) goto L_0x021f
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r9 = 268435462(0x10000006, float:2.5243567E-29)
            r8.setImeOptions(r9)
            goto L_0x0229
        L_0x021f:
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r9 = 268435461(0x10000005, float:2.5243564E-29)
            r8.setImeOptions(r9)
        L_0x0229:
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r9 = r8[r0]
            r8 = r8[r0]
            int r8 = r8.length()
            r9.setSelection(r8)
            r8 = 19
            r9 = 5
            if (r0 != r5) goto L_0x02ab
            android.widget.TextView r11 = new android.widget.TextView
            r11.<init>(r2)
            r1.plusTextView = r11
            r11.setText(r12)
            android.widget.TextView r11 = r1.plusTextView
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r11.setTextColor(r13)
            android.widget.TextView r11 = r1.plusTextView
            r11.setTextSize(r5, r15)
            android.widget.TextView r11 = r1.plusTextView
            r17 = -2
            r18 = -2
            r19 = 1101529088(0x41a80000, float:21.0)
            r20 = 1094713344(0x41400000, float:12.0)
            r21 = 0
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r14.addView(r11, r13)
            org.telegram.ui.Components.EditTextBoldCursor[] r11 = r1.inputFields
            r11 = r11[r0]
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r11.setPadding(r13, r6, r6, r6)
            android.text.InputFilter[] r11 = new android.text.InputFilter[r5]
            android.text.InputFilter$LengthFilter r13 = new android.text.InputFilter$LengthFilter
            r13.<init>(r9)
            r11[r6] = r13
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r9.setFilters(r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r9.setGravity(r8)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r17 = 55
            r19 = 0
            r21 = 1098907648(0x41800000, float:16.0)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r14.addView(r8, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            org.telegram.ui.PassportActivity$9 r9 = new org.telegram.ui.PassportActivity$9
            r9.<init>()
            r8.addTextChangedListener(r9)
            goto L_0x0329
        L_0x02ab:
            if (r0 != r4) goto L_0x02f6
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r9.setPadding(r6, r6, r6, r6)
            org.telegram.ui.Components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r9.setGravity(r8)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r9 = 0
            r8.setHintText(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            int r9 = org.telegram.messenger.R.string.PaymentShippingPhoneNumber
            java.lang.String r11 = "PaymentShippingPhoneNumber"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r8.setHint(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r17 = -1
            r18 = -2
            r19 = 0
            r20 = 1094713344(0x41400000, float:12.0)
            r21 = 1101529088(0x41a80000, float:21.0)
            r22 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r14.addView(r8, r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            org.telegram.ui.PassportActivity$10 r9 = new org.telegram.ui.PassportActivity$10
            r9.<init>()
            r8.addTextChangedListener(r9)
            goto L_0x0329
        L_0x02f6:
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r8.setPadding(r6, r6, r6, r11)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x030c
            goto L_0x030d
        L_0x030c:
            r9 = 3
        L_0x030d:
            r8.setGravity(r9)
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            r17 = -1
            r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 51
            r20 = 1101529088(0x41a80000, float:21.0)
            r21 = 1094713344(0x41400000, float:12.0)
            r22 = 1101529088(0x41a80000, float:21.0)
            r23 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r14.addView(r8, r9)
        L_0x0329:
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda45 r9 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda45
            r9.<init>(r1)
            r8.setOnEditorActionListener(r9)
            if (r0 != r4) goto L_0x0343
            org.telegram.ui.Components.EditTextBoldCursor[] r8 = r1.inputFields
            r8 = r8[r0]
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda32 r9 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda32
            r9.<init>(r1)
            r8.setOnKeyListener(r9)
        L_0x0343:
            if (r0 != 0) goto L_0x0363
            android.view.View r8 = new android.view.View
            r8.<init>(r2)
            java.util.ArrayList<android.view.View> r9 = r1.dividers
            r9.add(r8)
            java.lang.String r9 = "divider"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setBackgroundColor(r9)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r11 = 83
            r13 = -1
            r9.<init>(r13, r5, r11)
            r14.addView(r8, r9)
        L_0x0363:
            int r0 = r0 + 1
            r8 = -1
            r9 = -2
            goto L_0x0131
        L_0x0369:
            r9 = r11
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0380 }
            java.lang.String r3 = "phone"
            java.lang.Object r0 = r0.getSystemService(r3)     // Catch:{ Exception -> 0x0380 }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x0380 }
            if (r0 == 0) goto L_0x0384
            java.lang.String r0 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x0380 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ Exception -> 0x0380 }
            r11 = r0
            goto L_0x0385
        L_0x0380:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0384:
            r11 = r9
        L_0x0385:
            if (r11 == 0) goto L_0x03a9
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.languageMap
            java.lang.Object r0 = r0.get(r11)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x03a9
            java.util.ArrayList<java.lang.String> r3 = r1.countriesArray
            int r3 = r3.indexOf(r0)
            r4 = -1
            if (r3 == r4) goto L_0x03a9
            org.telegram.ui.Components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r5]
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r1.countriesMap
            java.lang.Object r0 = r4.get(r0)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r3.setText(r0)
        L_0x03a9:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r0.<init>(r2)
            r1.bottomCell = r0
            int r3 = org.telegram.messenger.R.drawable.greydivider_bottom
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r10)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r1.bottomCell
            int r2 = org.telegram.messenger.R.string.PassportPhoneUploadInfo
            java.lang.String r3 = "PassportPhoneUploadInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            android.widget.LinearLayout r0 = r1.linearLayout2
            org.telegram.ui.Cells.TextInfoPrivacyCell r2 = r1.bottomCell
            r3 = -2
            r4 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r3)
            r0.addView(r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.createPhoneInterface(android.content.Context):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPhoneInterface$26(View view) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createPhoneInterface$29(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new PassportActivity$$ExternalSyntheticLambda71(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPhoneInterface$28(CountrySelectActivity.Country country) {
        this.inputFields[0].setText(country.name);
        if (this.countriesArray.indexOf(country.name) != -1) {
            this.ignoreOnTextChange = true;
            String str = this.countriesMap.get(country.name);
            this.inputFields[1].setText(str);
            String str2 = this.phoneFormatMap.get(str);
            this.inputFields[2].setHintText(str2 != null ? str2.replace('X', 8211) : null);
            this.ignoreOnTextChange = false;
        }
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda48(this), 300);
        this.inputFields[2].requestFocus();
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[2].setSelection(editTextBoldCursorArr[2].length());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPhoneInterface$27() {
        AndroidUtilities.showKeyboard(this.inputFields[2]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createPhoneInterface$30(TextView textView, int i, KeyEvent keyEvent) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createPhoneInterface$31(View view, int i, KeyEvent keyEvent) {
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
        final String str;
        Context context2 = context;
        this.languageMap = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.languageMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.topErrorCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_top, "windowBackgroundGrayShadow"));
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        int i = -2;
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType = this.currentDocumentsType;
        if (tLRPC$TL_secureRequiredType != null) {
            TLRPC$SecureValueType tLRPC$SecureValueType = tLRPC$TL_secureRequiredType.type;
            if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeBankStatement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeUtilityBill) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassportRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            }
            HeaderCell headerCell2 = new HeaderCell(context2);
            this.headerCell = headerCell2;
            headerCell2.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout = new LinearLayout(context2);
            this.documentsLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.linearLayout2.addView(this.documentsLayout, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.uploadDocumentCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda23(this));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell2;
            int i2 = R.drawable.greydivider;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, i2, "windowBackgroundGrayShadow"));
            if (this.currentBotId != 0) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAddressUploadInfo", R.string.PassportAddAddressUploadInfo);
            } else {
                TLRPC$SecureValueType tLRPC$SecureValueType2 = this.currentDocumentsType.type;
                if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAgreementInfo", R.string.PassportAddAgreementInfo);
                } else if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypeUtilityBill) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBillInfo", R.string.PassportAddBillInfo);
                } else if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypePassportRegistration) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddPassportRegistrationInfo", R.string.PassportAddPassportRegistrationInfo);
                } else if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddTemporaryRegistrationInfo", R.string.PassportAddTemporaryRegistrationInfo);
                } else if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypeBankStatement) {
                    this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBankInfo", R.string.PassportAddBankInfo);
                } else {
                    this.noAllDocumentsErrorText = "";
                }
            }
            CharSequence charSequence = this.noAllDocumentsErrorText;
            HashMap<String, String> hashMap = this.documentsErrors;
            SpannableStringBuilder spannableStringBuilder = charSequence;
            if (hashMap != null) {
                String str2 = hashMap.get("files_all");
                spannableStringBuilder = charSequence;
                if (str2 != null) {
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(str2);
                    spannableStringBuilder2.append("\n\n");
                    spannableStringBuilder2.append(this.noAllDocumentsErrorText);
                    spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, str2.length(), 33);
                    this.errorsValues.put("files_all", "");
                    spannableStringBuilder = spannableStringBuilder2;
                }
            }
            this.bottomCell.setText(spannableStringBuilder);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                HeaderCell headerCell3 = new HeaderCell(context2);
                this.headerCell = headerCell3;
                headerCell3.setText(LocaleController.getString("PassportTranslation", R.string.PassportTranslation));
                this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout3 = new LinearLayout(context2);
                this.translationLayout = linearLayout3;
                linearLayout3.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
                this.uploadTranslationCell = textSettingsCell2;
                textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda27(this));
                TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
                this.bottomCellTranslation = textInfoPrivacyCell3;
                textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, i2, "windowBackgroundGrayShadow"));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", R.string.PassportAddTranslationUploadInfo);
                } else {
                    TLRPC$SecureValueType tLRPC$SecureValueType3 = this.currentDocumentsType.type;
                    if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationAgreementInfo", R.string.PassportAddTranslationAgreementInfo);
                    } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeUtilityBill) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBillInfo", R.string.PassportAddTranslationBillInfo);
                    } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypePassportRegistration) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationPassportRegistrationInfo", R.string.PassportAddTranslationPassportRegistrationInfo);
                    } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationTemporaryRegistrationInfo", R.string.PassportAddTranslationTemporaryRegistrationInfo);
                    } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeBankStatement) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBankInfo", R.string.PassportAddTranslationBankInfo);
                    } else {
                        this.noAllTranslationErrorText = "";
                    }
                }
                CharSequence charSequence2 = this.noAllTranslationErrorText;
                HashMap<String, String> hashMap2 = this.documentsErrors;
                SpannableStringBuilder spannableStringBuilder3 = charSequence2;
                if (hashMap2 != null) {
                    String str3 = hashMap2.get("translation_all");
                    spannableStringBuilder3 = charSequence2;
                    if (str3 != null) {
                        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(str3);
                        spannableStringBuilder4.append("\n\n");
                        spannableStringBuilder4.append(this.noAllTranslationErrorText);
                        spannableStringBuilder4.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, str3.length(), 33);
                        this.errorsValues.put("translation_all", "");
                        spannableStringBuilder3 = spannableStringBuilder4;
                    }
                }
                this.bottomCellTranslation.setText(spannableStringBuilder3);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
        }
        HeaderCell headerCell4 = new HeaderCell(context2);
        this.headerCell = headerCell4;
        headerCell4.setText(LocaleController.getString("PassportAddressHeader", R.string.PassportAddressHeader));
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[6];
        int i3 = 0;
        while (i3 < 6) {
            final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.inputFields[i3] = editTextBoldCursor;
            AnonymousClass11 r11 = new FrameLayout(this, context2) {
                private StaticLayout errorLayout;
                float offsetX;

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(34.0f);
                    StaticLayout errorLayout2 = editTextBoldCursor.getErrorLayout(size);
                    this.errorLayout = errorLayout2;
                    if (errorLayout2 != null) {
                        int lineCount = errorLayout2.getLineCount();
                        int i3 = 0;
                        if (lineCount > 1) {
                            i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                        }
                        if (LocaleController.isRTL) {
                            float f = 0.0f;
                            while (true) {
                                if (i3 >= lineCount) {
                                    break;
                                } else if (this.errorLayout.getLineLeft(i3) != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                } else {
                                    f = Math.max(f, this.errorLayout.getLineWidth(i3));
                                    if (i3 == lineCount - 1) {
                                        this.offsetX = ((float) size) - f;
                                    }
                                    i3++;
                                }
                            }
                        }
                    }
                    super.onMeasure(i, i2);
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            r11.setWillNotDraw(false);
            this.linearLayout2.addView(r11, LayoutHelper.createLinear(-1, i));
            r11.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            int i4 = 5;
            if (i3 == 5) {
                View view = new View(context2);
                this.extraBackgroundView = view;
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, 6));
            }
            if (this.documentOnly && this.currentDocumentsType != null) {
                r11.setVisibility(8);
                View view2 = this.extraBackgroundView;
                if (view2 != null) {
                    view2.setVisibility(8);
                }
            }
            this.inputFields[i3].setTag(Integer.valueOf(i3));
            this.inputFields[i3].setSupportRtlHint(true);
            this.inputFields[i3].setTextSize(1, 16.0f);
            this.inputFields[i3].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[i3].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[i3].setTransformHintToHeader(true);
            this.inputFields[i3].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i3].setBackgroundDrawable((Drawable) null);
            this.inputFields[i3].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i3].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i3].setCursorWidth(1.5f);
            this.inputFields[i3].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (i3 == 5) {
                this.inputFields[i3].setOnTouchListener(new PassportActivity$$ExternalSyntheticLambda34(this));
                this.inputFields[i3].setInputType(0);
                this.inputFields[i3].setFocusable(false);
            } else {
                this.inputFields[i3].setInputType(16385);
                this.inputFields[i3].setImeOptions(NUM);
            }
            if (i3 == 0) {
                this.inputFields[i3].setHintText(LocaleController.getString("PassportStreet1", R.string.PassportStreet1));
                str = "street_line1";
            } else if (i3 == 1) {
                this.inputFields[i3].setHintText(LocaleController.getString("PassportStreet2", R.string.PassportStreet2));
                str = "street_line2";
            } else if (i3 == 2) {
                this.inputFields[i3].setHintText(LocaleController.getString("PassportPostcode", R.string.PassportPostcode));
                str = "post_code";
            } else if (i3 == 3) {
                this.inputFields[i3].setHintText(LocaleController.getString("PassportCity", R.string.PassportCity));
                str = "city";
            } else if (i3 == 4) {
                this.inputFields[i3].setHintText(LocaleController.getString("PassportState", R.string.PassportState));
                str = "state";
            } else if (i3 != 5) {
                i3++;
                i = -2;
            } else {
                this.inputFields[i3].setHintText(LocaleController.getString("PassportCountry", R.string.PassportCountry));
                str = "country_code";
            }
            setFieldValues(this.currentValues, this.inputFields[i3], str);
            if (i3 == 2) {
                this.inputFields[i3].addTextChangedListener(new TextWatcher() {
                    private boolean ignore;

                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!this.ignore) {
                            boolean z = true;
                            this.ignore = true;
                            int i = 0;
                            while (true) {
                                if (i >= editable.length()) {
                                    z = false;
                                    break;
                                }
                                char charAt = editable.charAt(i);
                                if ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && !((charAt >= '0' && charAt <= '9') || charAt == '-' || charAt == ' '))) {
                                    break;
                                }
                                i++;
                            }
                            this.ignore = false;
                            if (z) {
                                editTextBoldCursor.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            } else {
                                PassportActivity.this.checkFieldForError(editTextBoldCursor, str, editable, false);
                            }
                        }
                    }
                });
                this.inputFields[i3].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            } else {
                this.inputFields[i3].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        PassportActivity.this.checkFieldForError(editTextBoldCursor, str, editable, false);
                    }
                });
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i3].setSelection(editTextBoldCursorArr[i3].length());
            this.inputFields[i3].setPadding(0, 0, 0, 0);
            EditTextBoldCursor editTextBoldCursor2 = this.inputFields[i3];
            if (!LocaleController.isRTL) {
                i4 = 3;
            }
            editTextBoldCursor2.setGravity(i4 | 16);
            r11.addView(this.inputFields[i3], LayoutHelper.createFrame(-1, 64.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputFields[i3].setOnEditorActionListener(new PassportActivity$$ExternalSyntheticLambda42(this));
            i3++;
            i = -2;
        }
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.sectionCell = shadowSectionCell;
        this.linearLayout2.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.documentOnly && this.currentDocumentsType != null) {
            this.headerCell.setVisibility(8);
            this.sectionCell.setVisibility(8);
        }
        if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
            TLRPC$TL_secureValue tLRPC$TL_secureValue = this.currentDocumentsTypeValue;
            if (tLRPC$TL_secureValue != null) {
                addDocumentViews(tLRPC$TL_secureValue.files);
                addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, "windowBackgroundGrayShadow"));
            TextSettingsCell textSettingsCell3 = new TextSettingsCell(context2);
            textSettingsCell3.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
            textSettingsCell3.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentDocumentsType == null) {
                textSettingsCell3.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
            } else {
                textSettingsCell3.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
            }
            this.linearLayout2.addView(textSettingsCell3, LayoutHelper.createLinear(-1, -2));
            textSettingsCell3.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda13(this));
            ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
            this.sectionCell = shadowSectionCell2;
            shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        } else {
            ShadowSectionCell shadowSectionCell3 = this.sectionCell;
            int i5 = R.drawable.greydivider_bottom;
            shadowSectionCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, i5, "windowBackgroundGrayShadow"));
            if (this.documentOnly && this.currentDocumentsType != null) {
                this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, i5, "windowBackgroundGrayShadow"));
            }
        }
        updateUploadText(0);
        updateUploadText(4);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createAddressInterface$32(View view) {
        this.uploadingFileType = 0;
        openAttachMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createAddressInterface$33(View view) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createAddressInterface$35(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new PassportActivity$$ExternalSyntheticLambda70(this));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createAddressInterface$34(CountrySelectActivity.Country country) {
        this.inputFields[5].setText(country.name);
        this.currentCitizeship = country.shortname;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createAddressInterface$36(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (intValue < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[intValue].isFocusable()) {
                this.inputFields[intValue].requestFocus();
            } else {
                this.inputFields[intValue].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createAddressInterface$37(View view) {
        createDocumentDeleteAlert();
    }

    private void createDocumentDeleteAlert() {
        boolean[] zArr = {true};
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PassportActivity$$ExternalSyntheticLambda11(this, zArr));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        boolean z = this.documentOnly;
        if (z && this.currentDocumentsType == null && (this.currentType.type instanceof TLRPC$TL_secureValueTypeAddress)) {
            builder.setMessage(LocaleController.getString("PassportDeleteAddressAlert", R.string.PassportDeleteAddressAlert));
        } else if (!z || this.currentDocumentsType != null || !(this.currentType.type instanceof TLRPC$TL_secureValueTypePersonalDetails)) {
            builder.setMessage(LocaleController.getString("PassportDeleteDocumentAlert", R.string.PassportDeleteDocumentAlert));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeletePersonalAlert", R.string.PassportDeletePersonalAlert));
        }
        if (!this.documentOnly && this.currentDocumentsType != null) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            TLRPC$SecureValueType tLRPC$SecureValueType = this.currentType.type;
            if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeAddress) {
                checkBoxCell.setText(LocaleController.getString("PassportDeleteDocumentAddress", R.string.PassportDeleteDocumentAddress), "", true, false);
            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails) {
                checkBoxCell.setText(LocaleController.getString("PassportDeleteDocumentPersonal", R.string.PassportDeleteDocumentPersonal), "", true, false);
            }
            checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48, 51));
            checkBoxCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda31(zArr));
            builder.setView(frameLayout);
        }
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createDocumentDeleteAlert$38(boolean[] zArr, DialogInterface dialogInterface, int i) {
        if (!this.documentOnly) {
            this.currentValues.clear();
        }
        this.currentDocumentValues.clear();
        this.delegate.deleteValue(this.currentType, this.currentDocumentsType, this.availableDocumentTypes, zArr[0], (Runnable) null, (ErrorRunnable) null);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDocumentDeleteAlert$39(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            zArr[0] = !zArr[0];
            ((CheckBoxCell) view).setChecked(zArr[0], true);
        }
    }

    /* access modifiers changed from: private */
    public void onFieldError(View view) {
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

    /* access modifiers changed from: private */
    public String getDocumentHash(SecureDocument secureDocument) {
        byte[] bArr;
        if (secureDocument == null) {
            return "";
        }
        TLRPC$TL_secureFile tLRPC$TL_secureFile = secureDocument.secureFile;
        if (tLRPC$TL_secureFile != null && (bArr = tLRPC$TL_secureFile.file_hash) != null) {
            return Base64.encodeToString(bArr, 2);
        }
        byte[] bArr2 = secureDocument.fileHash;
        return bArr2 != null ? Base64.encodeToString(bArr2, 2) : "";
    }

    /* access modifiers changed from: private */
    public void checkFieldForError(EditTextBoldCursor editTextBoldCursor, String str, Editable editable, boolean z) {
        String str2;
        String str3;
        String str4;
        HashMap<String, String> hashMap = this.errorsValues;
        if (hashMap == null || (str2 = hashMap.get(str)) == null) {
            editTextBoldCursor.setErrorText((CharSequence) null);
        } else if (TextUtils.equals(str2, editable)) {
            HashMap<String, String> hashMap2 = this.fieldsErrors;
            if (hashMap2 == null || (str4 = hashMap2.get(str)) == null) {
                HashMap<String, String> hashMap3 = this.documentsErrors;
                if (!(hashMap3 == null || (str3 = hashMap3.get(str)) == null)) {
                    editTextBoldCursor.setErrorText(str3);
                }
            } else {
                editTextBoldCursor.setErrorText(str4);
            }
        } else {
            editTextBoldCursor.setErrorText((CharSequence) null);
        }
        String str5 = z ? "error_document_all" : "error_all";
        HashMap<String, String> hashMap4 = this.errorsValues;
        if (hashMap4 != null && hashMap4.containsKey(str5)) {
            this.errorsValues.remove(str5);
            checkTopErrorCell(false);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x021a, code lost:
        if (r6 != 5) goto L_0x022c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x0290, code lost:
        if (r8 > 24) goto L_0x029c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x029a, code lost:
        if (r8 < 2) goto L_0x029c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02aa, code lost:
        if (r8 < 2) goto L_0x029c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02b3, code lost:
        if (r8 > 10) goto L_0x029c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x022f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkFieldsForError() {
        /*
            r13 = this;
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = r13.currentDocumentsType
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x01a3
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r13.errorsValues
            java.lang.String r3 = "error_all"
            boolean r0 = r0.containsKey(r3)
            if (r0 != 0) goto L_0x019d
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r13.errorsValues
            java.lang.String r3 = "error_document_all"
            boolean r0 = r0.containsKey(r3)
            if (r0 == 0) goto L_0x001c
            goto L_0x019d
        L_0x001c:
            org.telegram.ui.Cells.TextSettingsCell r0 = r13.uploadDocumentCell
            if (r0 == 0) goto L_0x006d
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r0 = r13.documents
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x002e
            org.telegram.ui.Cells.TextSettingsCell r0 = r13.uploadDocumentCell
            r13.onFieldError(r0)
            return r2
        L_0x002e:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r0 = r13.documents
            int r0 = r0.size()
            r3 = 0
        L_0x0035:
            if (r3 >= r0) goto L_0x006d
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r13.documents
            java.lang.Object r4 = r4.get(r3)
            org.telegram.messenger.SecureDocument r4 = (org.telegram.messenger.SecureDocument) r4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "files"
            r5.append(r6)
            java.lang.String r6 = r13.getDocumentHash(r4)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            if (r5 == 0) goto L_0x006a
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r13.errorsValues
            boolean r5 = r6.containsKey(r5)
            if (r5 == 0) goto L_0x006a
            java.util.HashMap<org.telegram.messenger.SecureDocument, org.telegram.ui.PassportActivity$SecureDocumentCell> r0 = r13.documentsCells
            java.lang.Object r0 = r0.get(r4)
            android.view.View r0 = (android.view.View) r0
            r13.onFieldError(r0)
            return r2
        L_0x006a:
            int r3 = r3 + 1
            goto L_0x0035
        L_0x006d:
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r13.errorsValues
            java.lang.String r3 = "files_all"
            boolean r0 = r0.containsKey(r3)
            if (r0 != 0) goto L_0x0197
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r13.errorsValues
            java.lang.String r3 = "translation_all"
            boolean r0 = r0.containsKey(r3)
            if (r0 == 0) goto L_0x0083
            goto L_0x0197
        L_0x0083:
            org.telegram.ui.Cells.TextDetailSettingsCell r0 = r13.uploadFrontCell
            if (r0 == 0) goto L_0x00bc
            org.telegram.messenger.SecureDocument r3 = r13.frontDocument
            if (r3 != 0) goto L_0x008f
            r13.onFieldError(r0)
            return r2
        L_0x008f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "front"
            r0.append(r3)
            org.telegram.messenger.SecureDocument r3 = r13.frontDocument
            java.lang.String r3 = r13.getDocumentHash(r3)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r13.errorsValues
            boolean r0 = r3.containsKey(r0)
            if (r0 == 0) goto L_0x00bc
            java.util.HashMap<org.telegram.messenger.SecureDocument, org.telegram.ui.PassportActivity$SecureDocumentCell> r0 = r13.documentsCells
            org.telegram.messenger.SecureDocument r1 = r13.frontDocument
            java.lang.Object r0 = r0.get(r1)
            android.view.View r0 = (android.view.View) r0
            r13.onFieldError(r0)
            return r2
        L_0x00bc:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = r13.currentDocumentsType
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r0.type
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
            if (r3 != 0) goto L_0x00c8
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
            if (r0 == 0) goto L_0x0101
        L_0x00c8:
            org.telegram.ui.Cells.TextDetailSettingsCell r0 = r13.uploadReverseCell
            if (r0 == 0) goto L_0x0101
            org.telegram.messenger.SecureDocument r3 = r13.reverseDocument
            if (r3 != 0) goto L_0x00d4
            r13.onFieldError(r0)
            return r2
        L_0x00d4:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "reverse"
            r0.append(r3)
            org.telegram.messenger.SecureDocument r3 = r13.reverseDocument
            java.lang.String r3 = r13.getDocumentHash(r3)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r13.errorsValues
            boolean r0 = r3.containsKey(r0)
            if (r0 == 0) goto L_0x0101
            java.util.HashMap<org.telegram.messenger.SecureDocument, org.telegram.ui.PassportActivity$SecureDocumentCell> r0 = r13.documentsCells
            org.telegram.messenger.SecureDocument r1 = r13.reverseDocument
            java.lang.Object r0 = r0.get(r1)
            android.view.View r0 = (android.view.View) r0
            r13.onFieldError(r0)
            return r2
        L_0x0101:
            org.telegram.ui.Cells.TextDetailSettingsCell r0 = r13.uploadSelfieCell
            r3 = 0
            if (r0 == 0) goto L_0x0142
            long r5 = r13.currentBotId
            int r7 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x0142
            org.telegram.messenger.SecureDocument r5 = r13.selfieDocument
            if (r5 != 0) goto L_0x0115
            r13.onFieldError(r0)
            return r2
        L_0x0115:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "selfie"
            r0.append(r5)
            org.telegram.messenger.SecureDocument r5 = r13.selfieDocument
            java.lang.String r5 = r13.getDocumentHash(r5)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.util.HashMap<java.lang.String, java.lang.String> r5 = r13.errorsValues
            boolean r0 = r5.containsKey(r0)
            if (r0 == 0) goto L_0x0142
            java.util.HashMap<org.telegram.messenger.SecureDocument, org.telegram.ui.PassportActivity$SecureDocumentCell> r0 = r13.documentsCells
            org.telegram.messenger.SecureDocument r1 = r13.selfieDocument
            java.lang.Object r0 = r0.get(r1)
            android.view.View r0 = (android.view.View) r0
            r13.onFieldError(r0)
            return r2
        L_0x0142:
            org.telegram.ui.Cells.TextSettingsCell r0 = r13.uploadTranslationCell
            if (r0 == 0) goto L_0x01a3
            long r5 = r13.currentBotId
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x01a3
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r0 = r13.translationDocuments
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x015a
            org.telegram.ui.Cells.TextSettingsCell r0 = r13.uploadTranslationCell
            r13.onFieldError(r0)
            return r2
        L_0x015a:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r0 = r13.translationDocuments
            int r0 = r0.size()
            r3 = 0
        L_0x0161:
            if (r3 >= r0) goto L_0x01a3
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r13.translationDocuments
            java.lang.Object r4 = r4.get(r3)
            org.telegram.messenger.SecureDocument r4 = (org.telegram.messenger.SecureDocument) r4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "translation"
            r5.append(r6)
            java.lang.String r6 = r13.getDocumentHash(r4)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r13.errorsValues
            boolean r5 = r6.containsKey(r5)
            if (r5 == 0) goto L_0x0194
            java.util.HashMap<org.telegram.messenger.SecureDocument, org.telegram.ui.PassportActivity$SecureDocumentCell> r0 = r13.documentsCells
            java.lang.Object r0 = r0.get(r4)
            android.view.View r0 = (android.view.View) r0
            r13.onFieldError(r0)
            return r2
        L_0x0194:
            int r3 = r3 + 1
            goto L_0x0161
        L_0x0197:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r13.bottomCell
            r13.onFieldError(r0)
            return r2
        L_0x019d:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r13.topErrorCell
            r13.onFieldError(r0)
            return r2
        L_0x01a3:
            r0 = 0
        L_0x01a4:
            r3 = 2
            if (r0 >= r3) goto L_0x02ce
            r4 = 0
            if (r0 != 0) goto L_0x01ad
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r13.inputFields
            goto L_0x01bb
        L_0x01ad:
            org.telegram.ui.Cells.TextInfoPrivacyCell r5 = r13.nativeInfoCell
            if (r5 == 0) goto L_0x01ba
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x01ba
            org.telegram.ui.Components.EditTextBoldCursor[] r5 = r13.inputExtraFields
            goto L_0x01bb
        L_0x01ba:
            r5 = r4
        L_0x01bb:
            if (r5 != 0) goto L_0x01bf
            goto L_0x02ca
        L_0x01bf:
            r6 = 0
        L_0x01c0:
            int r7 = r5.length
            if (r6 >= r7) goto L_0x02ca
            r7 = r5[r6]
            boolean r7 = r7.hasErrorText()
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r13.errorsValues
            boolean r8 = r8.isEmpty()
            r9 = 4
            r10 = 3
            if (r8 != 0) goto L_0x024e
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r8 = r13.currentType
            org.telegram.tgnet.TLRPC$SecureValueType r8 = r8.type
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            java.lang.String r12 = "country_code"
            if (r11 == 0) goto L_0x020b
            if (r0 != 0) goto L_0x01fb
            switch(r6) {
                case 0: goto L_0x01f8;
                case 1: goto L_0x01f5;
                case 2: goto L_0x01f2;
                case 3: goto L_0x01ef;
                case 4: goto L_0x01ec;
                case 5: goto L_0x022d;
                case 6: goto L_0x01e9;
                case 7: goto L_0x01e6;
                case 8: goto L_0x01e3;
                default: goto L_0x01e2;
            }
        L_0x01e2:
            goto L_0x022c
        L_0x01e3:
            java.lang.String r12 = "expiry_date"
            goto L_0x022d
        L_0x01e6:
            java.lang.String r12 = "document_no"
            goto L_0x022d
        L_0x01e9:
            java.lang.String r12 = "residence_country_code"
            goto L_0x022d
        L_0x01ec:
            java.lang.String r12 = "gender"
            goto L_0x022d
        L_0x01ef:
            java.lang.String r12 = "birth_date"
            goto L_0x022d
        L_0x01f2:
            java.lang.String r12 = "last_name"
            goto L_0x022d
        L_0x01f5:
            java.lang.String r12 = "middle_name"
            goto L_0x022d
        L_0x01f8:
            java.lang.String r12 = "first_name"
            goto L_0x022d
        L_0x01fb:
            if (r6 == 0) goto L_0x0208
            if (r6 == r2) goto L_0x0205
            if (r6 == r3) goto L_0x0202
            goto L_0x022c
        L_0x0202:
            java.lang.String r12 = "last_name_native"
            goto L_0x022d
        L_0x0205:
            java.lang.String r12 = "middle_name_native"
            goto L_0x022d
        L_0x0208:
            java.lang.String r12 = "first_name_native"
            goto L_0x022d
        L_0x020b:
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r8 == 0) goto L_0x022c
            if (r6 == 0) goto L_0x0229
            if (r6 == r2) goto L_0x0226
            if (r6 == r3) goto L_0x0223
            if (r6 == r10) goto L_0x0220
            if (r6 == r9) goto L_0x021d
            r8 = 5
            if (r6 == r8) goto L_0x022d
            goto L_0x022c
        L_0x021d:
            java.lang.String r12 = "state"
            goto L_0x022d
        L_0x0220:
            java.lang.String r12 = "city"
            goto L_0x022d
        L_0x0223:
            java.lang.String r12 = "post_code"
            goto L_0x022d
        L_0x0226:
            java.lang.String r12 = "street_line2"
            goto L_0x022d
        L_0x0229:
            java.lang.String r12 = "street_line1"
            goto L_0x022d
        L_0x022c:
            r12 = r4
        L_0x022d:
            if (r12 == 0) goto L_0x024e
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r13.errorsValues
            java.lang.Object r8 = r8.get(r12)
            java.lang.String r8 = (java.lang.String) r8
            boolean r11 = android.text.TextUtils.isEmpty(r8)
            if (r11 != 0) goto L_0x024e
            r11 = r5[r6]
            android.text.Editable r11 = r11.getText()
            java.lang.String r11 = r11.toString()
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x024e
            r7 = 1
        L_0x024e:
            boolean r8 = r13.documentOnly
            r11 = 7
            if (r8 == 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r8 = r13.currentDocumentsType
            if (r8 == 0) goto L_0x025b
            if (r6 >= r11) goto L_0x025b
            goto L_0x02c6
        L_0x025b:
            if (r7 != 0) goto L_0x02be
            r8 = r5[r6]
            int r8 = r8.length()
            int r12 = r13.currentActivityType
            if (r12 != r2) goto L_0x0293
            r9 = 8
            if (r6 != r9) goto L_0x026d
            goto L_0x02c6
        L_0x026d:
            if (r0 != 0) goto L_0x0275
            if (r6 == 0) goto L_0x027d
            if (r6 == r3) goto L_0x027d
            if (r6 == r2) goto L_0x027d
        L_0x0275:
            if (r0 != r2) goto L_0x028c
            if (r6 == 0) goto L_0x027d
            if (r6 == r2) goto L_0x027d
            if (r6 != r3) goto L_0x028c
        L_0x027d:
            r9 = 255(0xff, float:3.57E-43)
            if (r8 <= r9) goto L_0x0282
            r7 = 1
        L_0x0282:
            if (r0 != 0) goto L_0x0286
            if (r6 == r2) goto L_0x028a
        L_0x0286:
            if (r0 != r2) goto L_0x02b6
            if (r6 != r2) goto L_0x02b6
        L_0x028a:
            r9 = 1
            goto L_0x02b7
        L_0x028c:
            if (r6 != r11) goto L_0x02b6
            r9 = 24
            if (r8 <= r9) goto L_0x02b6
            goto L_0x029c
        L_0x0293:
            if (r12 != r3) goto L_0x02b6
            if (r6 != r2) goto L_0x0298
            goto L_0x02c6
        L_0x0298:
            if (r6 != r10) goto L_0x029e
            if (r8 >= r3) goto L_0x02b6
        L_0x029c:
            r7 = 1
            goto L_0x02b6
        L_0x029e:
            if (r6 != r9) goto L_0x02ad
            java.lang.String r9 = r13.currentCitizeship
            java.lang.String r10 = "US"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x02c6
            if (r8 >= r3) goto L_0x02b6
            goto L_0x029c
        L_0x02ad:
            if (r6 != r3) goto L_0x02b6
            if (r8 < r3) goto L_0x029c
            r9 = 10
            if (r8 <= r9) goto L_0x02b6
            goto L_0x029c
        L_0x02b6:
            r9 = 0
        L_0x02b7:
            if (r7 != 0) goto L_0x02be
            if (r9 != 0) goto L_0x02be
            if (r8 != 0) goto L_0x02be
            r7 = 1
        L_0x02be:
            if (r7 == 0) goto L_0x02c6
            r0 = r5[r6]
            r13.onFieldError(r0)
            return r2
        L_0x02c6:
            int r6 = r6 + 1
            goto L_0x01c0
        L_0x02ca:
            int r0 = r0 + 1
            goto L_0x01a4
        L_0x02ce:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.checkFieldsForError():boolean");
    }

    private void createIdentityInterface(Context context) {
        final String str;
        HashMap<String, String> hashMap;
        final String str2;
        final HashMap<String, String> hashMap2;
        Context context2 = context;
        this.languageMap = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.languageMap.put(split[1], split[2]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.topErrorCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_top, "windowBackgroundGrayShadow"));
        boolean z = false;
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        int i = -1;
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        if (this.currentDocumentsType != null) {
            HeaderCell headerCell2 = new HeaderCell(context2);
            this.headerCell = headerCell2;
            if (this.documentOnly) {
                headerCell2.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            } else {
                headerCell2.setText(LocaleController.getString("PassportRequiredDocuments", R.string.PassportRequiredDocuments));
            }
            this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout = new LinearLayout(context2);
            this.frontLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.linearLayout2.addView(this.frontLayout, LayoutHelper.createLinear(-1, -2));
            TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(context2);
            this.uploadFrontCell = textDetailSettingsCell;
            textDetailSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadFrontCell, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda22(this));
            LinearLayout linearLayout3 = new LinearLayout(context2);
            this.reverseLayout = linearLayout3;
            linearLayout3.setOrientation(1);
            this.linearLayout2.addView(this.reverseLayout, LayoutHelper.createLinear(-1, -2));
            boolean z2 = this.currentDocumentsType.selfie_required;
            TextDetailSettingsCell textDetailSettingsCell2 = new TextDetailSettingsCell(context2);
            this.uploadReverseCell = textDetailSettingsCell2;
            textDetailSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.uploadReverseCell.setTextAndValue(LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide), LocaleController.getString("PassportReverseSideInfo", R.string.PassportReverseSideInfo), z2);
            this.linearLayout2.addView(this.uploadReverseCell, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda24(this));
            if (this.currentDocumentsType.selfie_required) {
                LinearLayout linearLayout4 = new LinearLayout(context2);
                this.selfieLayout = linearLayout4;
                linearLayout4.setOrientation(1);
                this.linearLayout2.addView(this.selfieLayout, LayoutHelper.createLinear(-1, -2));
                TextDetailSettingsCell textDetailSettingsCell3 = new TextDetailSettingsCell(context2);
                this.uploadSelfieCell = textDetailSettingsCell3;
                textDetailSettingsCell3.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.uploadSelfieCell.setTextAndValue(LocaleController.getString("PassportSelfie", R.string.PassportSelfie), LocaleController.getString("PassportSelfieInfo", R.string.PassportSelfieInfo), this.currentType.translation_required);
                this.linearLayout2.addView(this.uploadSelfieCell, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda21(this));
            }
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell2;
            int i2 = R.drawable.greydivider;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, i2, "windowBackgroundGrayShadow"));
            this.bottomCell.setText(LocaleController.getString("PassportPersonalUploadInfo", R.string.PassportPersonalUploadInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                HeaderCell headerCell3 = new HeaderCell(context2);
                this.headerCell = headerCell3;
                headerCell3.setText(LocaleController.getString("PassportTranslation", R.string.PassportTranslation));
                this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout5 = new LinearLayout(context2);
                this.translationLayout = linearLayout5;
                linearLayout5.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
                this.uploadTranslationCell = textSettingsCell;
                textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda12(this));
                TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
                this.bottomCellTranslation = textInfoPrivacyCell3;
                textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, i2, "windowBackgroundGrayShadow"));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", R.string.PassportAddTranslationUploadInfo);
                } else {
                    TLRPC$SecureValueType tLRPC$SecureValueType = this.currentDocumentsType.type;
                    if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassport) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddPassportInfo", R.string.PassportAddPassportInfo);
                    } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeInternalPassport) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddInternalPassportInfo", R.string.PassportAddInternalPassportInfo);
                    } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeIdentityCard) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddIdentityCardInfo", R.string.PassportAddIdentityCardInfo);
                    } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeDriverLicense) {
                        this.noAllTranslationErrorText = LocaleController.getString("PassportAddDriverLicenceInfo", R.string.PassportAddDriverLicenceInfo);
                    } else {
                        this.noAllTranslationErrorText = "";
                    }
                }
                CharSequence charSequence = this.noAllTranslationErrorText;
                HashMap<String, String> hashMap3 = this.documentsErrors;
                SpannableStringBuilder spannableStringBuilder = charSequence;
                if (hashMap3 != null) {
                    String str3 = hashMap3.get("translation_all");
                    spannableStringBuilder = charSequence;
                    if (str3 != null) {
                        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(str3);
                        spannableStringBuilder2.append("\n\n");
                        spannableStringBuilder2.append(this.noAllTranslationErrorText);
                        spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteRedText3")), 0, str3.length(), 33);
                        this.errorsValues.put("translation_all", "");
                        spannableStringBuilder = spannableStringBuilder2;
                    }
                }
                this.bottomCellTranslation.setText(spannableStringBuilder);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else if (Build.VERSION.SDK_INT >= 18) {
            TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
            this.scanDocumentCell = textSettingsCell2;
            textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.scanDocumentCell.setText(LocaleController.getString("PassportScanPassport", R.string.PassportScanPassport), false);
            this.linearLayout2.addView(this.scanDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.scanDocumentCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda19(this));
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, "windowBackgroundGrayShadow"));
            this.bottomCell.setText(LocaleController.getString("PassportScanPassportInfo", R.string.PassportScanPassportInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        HeaderCell headerCell4 = new HeaderCell(context2);
        this.headerCell = headerCell4;
        if (this.documentOnly) {
            headerCell4.setText(LocaleController.getString("PassportDocument", R.string.PassportDocument));
        } else {
            headerCell4.setText(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        this.headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        int i3 = this.currentDocumentsType != null ? 9 : 7;
        this.inputFields = new EditTextBoldCursor[i3];
        int i4 = 0;
        while (true) {
            int i5 = 64;
            if (i4 < i3) {
                final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
                this.inputFields[i4] = editTextBoldCursor;
                AnonymousClass15 r4 = new FrameLayout(this, context2) {
                    private StaticLayout errorLayout;
                    private float offsetX;

                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(34.0f);
                        StaticLayout errorLayout2 = editTextBoldCursor.getErrorLayout(size);
                        this.errorLayout = errorLayout2;
                        if (errorLayout2 != null) {
                            int lineCount = errorLayout2.getLineCount();
                            int i3 = 0;
                            if (lineCount > 1) {
                                i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                            }
                            if (LocaleController.isRTL) {
                                float f = 0.0f;
                                while (true) {
                                    if (i3 >= lineCount) {
                                        break;
                                    } else if (this.errorLayout.getLineLeft(i3) != 0.0f) {
                                        this.offsetX = 0.0f;
                                        break;
                                    } else {
                                        f = Math.max(f, this.errorLayout.getLineWidth(i3));
                                        if (i3 == lineCount - 1) {
                                            this.offsetX = ((float) size) - f;
                                        }
                                        i3++;
                                    }
                                }
                            }
                        }
                        super.onMeasure(i, i2);
                    }

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        if (this.errorLayout != null) {
                            canvas.save();
                            canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                            this.errorLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                };
                r4.setWillNotDraw(z);
                this.linearLayout2.addView(r4, LayoutHelper.createLinear(i, 64));
                r4.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                if (i4 == i3 - 1) {
                    View view = new View(context2);
                    this.extraBackgroundView = view;
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(i, 6));
                }
                if (this.documentOnly && this.currentDocumentsType != null) {
                    if (i4 < 7) {
                        r4.setVisibility(8);
                        View view2 = this.extraBackgroundView;
                        if (view2 != null) {
                            view2.setVisibility(8);
                        }
                    }
                }
                this.inputFields[i4].setTag(Integer.valueOf(i4));
                this.inputFields[i4].setSupportRtlHint(true);
                this.inputFields[i4].setTextSize(1, 16.0f);
                this.inputFields[i4].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.inputFields[i4].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.inputFields[i4].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                this.inputFields[i4].setTransformHintToHeader(true);
                this.inputFields[i4].setBackgroundDrawable((Drawable) null);
                this.inputFields[i4].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.inputFields[i4].setCursorSize(AndroidUtilities.dp(20.0f));
                this.inputFields[i4].setCursorWidth(1.5f);
                this.inputFields[i4].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
                if (i4 == 5 || i4 == 6) {
                    this.inputFields[i4].setOnTouchListener(new PassportActivity$$ExternalSyntheticLambda36(this));
                    this.inputFields[i4].setInputType(0);
                } else if (i4 == 3 || i4 == 8) {
                    this.inputFields[i4].setOnTouchListener(new PassportActivity$$ExternalSyntheticLambda38(this, context2));
                    this.inputFields[i4].setInputType(0);
                    this.inputFields[i4].setFocusable(false);
                } else if (i4 == 4) {
                    this.inputFields[i4].setOnTouchListener(new PassportActivity$$ExternalSyntheticLambda37(this));
                    this.inputFields[i4].setInputType(0);
                    this.inputFields[i4].setFocusable(false);
                } else {
                    this.inputFields[i4].setInputType(16385);
                    this.inputFields[i4].setImeOptions(NUM);
                }
                switch (i4) {
                    case 0:
                        if (this.currentType.native_names) {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportNameLatin", R.string.PassportNameLatin));
                        } else {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                        }
                        hashMap2 = this.currentValues;
                        str2 = "first_name";
                        break;
                    case 1:
                        if (this.currentType.native_names) {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportMidnameLatin", R.string.PassportMidnameLatin));
                        } else {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportMidname", R.string.PassportMidname));
                        }
                        hashMap2 = this.currentValues;
                        str2 = "middle_name";
                        break;
                    case 2:
                        if (this.currentType.native_names) {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportSurnameLatin", R.string.PassportSurnameLatin));
                        } else {
                            this.inputFields[i4].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                        }
                        hashMap2 = this.currentValues;
                        str2 = "last_name";
                        break;
                    case 3:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportBirthdate", R.string.PassportBirthdate));
                        hashMap2 = this.currentValues;
                        str2 = "birth_date";
                        break;
                    case 4:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportGender", R.string.PassportGender));
                        hashMap2 = this.currentValues;
                        str2 = "gender";
                        break;
                    case 5:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportCitizenship", R.string.PassportCitizenship));
                        hashMap2 = this.currentValues;
                        str2 = "country_code";
                        break;
                    case 6:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportResidence", R.string.PassportResidence));
                        hashMap2 = this.currentValues;
                        str2 = "residence_country_code";
                        break;
                    case 7:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportDocumentNumber", R.string.PassportDocumentNumber));
                        hashMap2 = this.currentDocumentValues;
                        str2 = "document_no";
                        break;
                    case 8:
                        this.inputFields[i4].setHintText(LocaleController.getString("PassportExpired", R.string.PassportExpired));
                        hashMap2 = this.currentDocumentValues;
                        str2 = "expiry_date";
                        break;
                }
                setFieldValues(hashMap2, this.inputFields[i4], str2);
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
                            boolean z;
                            if (!this.ignore) {
                                int intValue = ((Integer) editTextBoldCursor.getTag()).intValue();
                                int i = 0;
                                while (true) {
                                    if (i >= editable.length()) {
                                        z = false;
                                        break;
                                    }
                                    char charAt = editable.charAt(i);
                                    if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && !((charAt >= 'A' && charAt <= 'Z') || charAt == ' ' || charAt == '\'' || charAt == ',' || charAt == '.' || charAt == '&' || charAt == '-' || charAt == '/'))) {
                                        z = true;
                                        break;
                                    }
                                    i++;
                                }
                                if (!z || PassportActivity.this.allowNonLatinName) {
                                    PassportActivity.this.nonLatinNames[intValue] = z;
                                    PassportActivity.this.checkFieldForError(editTextBoldCursor, str2, editable, false);
                                    return;
                                }
                                editTextBoldCursor.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
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
                            passportActivity.checkFieldForError(editTextBoldCursor, str2, editable, hashMap2 == passportActivity.currentDocumentValues);
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
                r4.addView(this.inputFields[i4], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                this.inputFields[i4].setOnEditorActionListener(new PassportActivity$$ExternalSyntheticLambda43(this));
                i4++;
                z = false;
                i = -1;
            } else {
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
                this.sectionCell2 = shadowSectionCell;
                this.linearLayout2.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
                HeaderCell headerCell5 = new HeaderCell(context2);
                this.headerCell = headerCell5;
                headerCell5.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                int i6 = 3;
                this.inputExtraFields = new EditTextBoldCursor[3];
                int i7 = 0;
                while (i7 < i6) {
                    final EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
                    this.inputExtraFields[i7] = editTextBoldCursor2;
                    AnonymousClass18 r7 = new FrameLayout(this, context2) {
                        private StaticLayout errorLayout;
                        private float offsetX;

                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            int size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(34.0f);
                            StaticLayout errorLayout2 = editTextBoldCursor2.getErrorLayout(size);
                            this.errorLayout = errorLayout2;
                            if (errorLayout2 != null) {
                                int lineCount = errorLayout2.getLineCount();
                                int i3 = 0;
                                if (lineCount > 1) {
                                    i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), NUM);
                                }
                                if (LocaleController.isRTL) {
                                    float f = 0.0f;
                                    while (true) {
                                        if (i3 >= lineCount) {
                                            break;
                                        } else if (this.errorLayout.getLineLeft(i3) != 0.0f) {
                                            this.offsetX = 0.0f;
                                            break;
                                        } else {
                                            f = Math.max(f, this.errorLayout.getLineWidth(i3));
                                            if (i3 == lineCount - 1) {
                                                this.offsetX = ((float) size) - f;
                                            }
                                            i3++;
                                        }
                                    }
                                }
                            }
                            super.onMeasure(i, i2);
                        }

                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            if (this.errorLayout != null) {
                                canvas.save();
                                canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, editTextBoldCursor2.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                                this.errorLayout.draw(canvas);
                                canvas.restore();
                            }
                        }
                    };
                    r7.setWillNotDraw(false);
                    this.linearLayout2.addView(r7, LayoutHelper.createLinear(-1, i5));
                    r7.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    if (i7 == 2) {
                        View view3 = new View(context2);
                        this.extraBackgroundView2 = view3;
                        view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                        this.linearLayout2.addView(this.extraBackgroundView2, LayoutHelper.createLinear(-1, 6));
                    }
                    this.inputExtraFields[i7].setTag(Integer.valueOf(i7));
                    this.inputExtraFields[i7].setSupportRtlHint(true);
                    this.inputExtraFields[i7].setTextSize(1, 16.0f);
                    this.inputExtraFields[i7].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                    this.inputExtraFields[i7].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    this.inputExtraFields[i7].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                    this.inputExtraFields[i7].setTransformHintToHeader(true);
                    this.inputExtraFields[i7].setBackgroundDrawable((Drawable) null);
                    this.inputExtraFields[i7].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    this.inputExtraFields[i7].setCursorSize(AndroidUtilities.dp(20.0f));
                    this.inputExtraFields[i7].setCursorWidth(1.5f);
                    this.inputExtraFields[i7].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
                    this.inputExtraFields[i7].setInputType(16385);
                    this.inputExtraFields[i7].setImeOptions(NUM);
                    if (i7 == 0) {
                        hashMap = this.currentValues;
                        str = "first_name_native";
                    } else if (i7 == 1) {
                        hashMap = this.currentValues;
                        str = "middle_name_native";
                    } else if (i7 != 2) {
                        i7++;
                        i6 = 3;
                        i5 = 64;
                    } else {
                        hashMap = this.currentValues;
                        str = "last_name_native";
                    }
                    setFieldValues(hashMap, this.inputExtraFields[i7], str);
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    editTextBoldCursorArr2[i7].setSelection(editTextBoldCursorArr2[i7].length());
                    if (i7 == 0 || i7 == 2 || i7 == 1) {
                        this.inputExtraFields[i7].addTextChangedListener(new TextWatcher() {
                            private boolean ignore;

                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void afterTextChanged(Editable editable) {
                                if (!this.ignore) {
                                    PassportActivity.this.checkFieldForError(editTextBoldCursor2, str, editable, false);
                                }
                            }
                        });
                    }
                    this.inputExtraFields[i7].setPadding(0, 0, 0, 0);
                    this.inputExtraFields[i7].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    r7.addView(this.inputExtraFields[i7], LayoutHelper.createFrame(-1, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                    this.inputExtraFields[i7].setOnEditorActionListener(new PassportActivity$$ExternalSyntheticLambda41(this));
                    i7++;
                    i6 = 3;
                    i5 = 64;
                }
                TextInfoPrivacyCell textInfoPrivacyCell5 = new TextInfoPrivacyCell(context2);
                this.nativeInfoCell = textInfoPrivacyCell5;
                this.linearLayout2.addView(textInfoPrivacyCell5, LayoutHelper.createLinear(-1, -2));
                if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
                    TLRPC$TL_secureValue tLRPC$TL_secureValue = this.currentDocumentsTypeValue;
                    if (tLRPC$TL_secureValue != null) {
                        addDocumentViews(tLRPC$TL_secureValue.files);
                        TLRPC$SecureFile tLRPC$SecureFile = this.currentDocumentsTypeValue.front_side;
                        if (tLRPC$SecureFile instanceof TLRPC$TL_secureFile) {
                            addDocumentViewInternal((TLRPC$TL_secureFile) tLRPC$SecureFile, 2);
                        }
                        TLRPC$SecureFile tLRPC$SecureFile2 = this.currentDocumentsTypeValue.reverse_side;
                        if (tLRPC$SecureFile2 instanceof TLRPC$TL_secureFile) {
                            addDocumentViewInternal((TLRPC$TL_secureFile) tLRPC$SecureFile2, 3);
                        }
                        TLRPC$SecureFile tLRPC$SecureFile3 = this.currentDocumentsTypeValue.selfie;
                        if (tLRPC$SecureFile3 instanceof TLRPC$TL_secureFile) {
                            addDocumentViewInternal((TLRPC$TL_secureFile) tLRPC$SecureFile3, 1);
                        }
                        addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
                    }
                    TextSettingsCell textSettingsCell3 = new TextSettingsCell(context2);
                    textSettingsCell3.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
                    textSettingsCell3.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    if (this.currentDocumentsType == null) {
                        textSettingsCell3.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
                    } else {
                        textSettingsCell3.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
                    }
                    this.linearLayout2.addView(textSettingsCell3, LayoutHelper.createLinear(-1, -2));
                    textSettingsCell3.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda26(this));
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
                    this.sectionCell = shadowSectionCell2;
                    shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
                } else {
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                }
                updateInterfaceStringsForDocumentType();
                checkNativeFields(false);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$40(View view) {
        this.uploadingFileType = 2;
        openAttachMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$41(View view) {
        this.uploadingFileType = 3;
        openAttachMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$42(View view) {
        this.uploadingFileType = 1;
        openAttachMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$43(View view) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$44(View view) {
        if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
            CameraScanActivity cameraScanActivity = new CameraScanActivity(0);
            cameraScanActivity.setDelegate(new CameraScanActivity.CameraScanActivityDelegate() {
                public /* synthetic */ void didFindQr(String str) {
                    CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindQr(this, str);
                }

                public /* synthetic */ boolean processQr(String str, Runnable runnable) {
                    return CameraScanActivity.CameraScanActivityDelegate.CC.$default$processQr(this, str, runnable);
                }

                public void didFindMrzInfo(MrzRecognizer.Result result) {
                    if (!TextUtils.isEmpty(result.firstName)) {
                        PassportActivity.this.inputFields[0].setText(result.firstName);
                    }
                    if (!TextUtils.isEmpty(result.middleName)) {
                        PassportActivity.this.inputFields[1].setText(result.middleName);
                    }
                    if (!TextUtils.isEmpty(result.lastName)) {
                        PassportActivity.this.inputFields[2].setText(result.lastName);
                    }
                    int i = result.gender;
                    if (i != 0) {
                        if (i == 1) {
                            String unused = PassportActivity.this.currentGender = "male";
                            PassportActivity.this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
                        } else if (i == 2) {
                            String unused2 = PassportActivity.this.currentGender = "female";
                            PassportActivity.this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
                        }
                    }
                    if (!TextUtils.isEmpty(result.nationality)) {
                        String unused3 = PassportActivity.this.currentCitizeship = result.nationality;
                        String str = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentCitizeship);
                        if (str != null) {
                            PassportActivity.this.inputFields[5].setText(str);
                        }
                    }
                    if (!TextUtils.isEmpty(result.issuingCountry)) {
                        String unused4 = PassportActivity.this.currentResidence = result.issuingCountry;
                        String str2 = (String) PassportActivity.this.languageMap.get(PassportActivity.this.currentResidence);
                        if (str2 != null) {
                            PassportActivity.this.inputFields[6].setText(str2);
                        }
                    }
                    if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
                        PassportActivity.this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
                    }
                }
            });
            presentFragment(cameraScanActivity);
            return;
        }
        getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 22);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createIdentityInterface$46(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(false);
            countrySelectActivity.setCountrySelectActivityDelegate(new PassportActivity$$ExternalSyntheticLambda72(this, view));
            presentFragment(countrySelectActivity);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$45(View view, CountrySelectActivity.Country country) {
        int intValue = ((Integer) view.getTag()).intValue();
        EditTextBoldCursor editTextBoldCursor = this.inputFields[intValue];
        if (intValue == 5) {
            this.currentCitizeship = country.shortname;
        } else {
            this.currentResidence = country.shortname;
        }
        editTextBoldCursor.setText(country.name);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createIdentityInterface$49(Context context, View view, MotionEvent motionEvent) {
        String str;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            Calendar instance = Calendar.getInstance();
            instance.get(1);
            instance.get(2);
            instance.get(5);
            try {
                EditTextBoldCursor editTextBoldCursor = (EditTextBoldCursor) view;
                int intValue = ((Integer) editTextBoldCursor.getTag()).intValue();
                if (intValue == 8) {
                    str = LocaleController.getString("PassportSelectExpiredDate", R.string.PassportSelectExpiredDate);
                    i3 = 0;
                    i2 = 20;
                    i = 0;
                } else {
                    str = LocaleController.getString("PassportSelectBithdayDate", R.string.PassportSelectBithdayDate);
                    i3 = -120;
                    i2 = 0;
                    i = -18;
                }
                String[] split = editTextBoldCursor.getText().toString().split("\\.");
                if (split.length == 3) {
                    int intValue2 = Utilities.parseInt((CharSequence) split[0]).intValue();
                    int intValue3 = Utilities.parseInt((CharSequence) split[1]).intValue();
                    i4 = Utilities.parseInt((CharSequence) split[2]).intValue();
                    i6 = intValue2;
                    i5 = intValue3;
                } else {
                    i6 = -1;
                    i5 = -1;
                    i4 = -1;
                }
                AlertDialog.Builder createDatePickerDialog = AlertsCreator.createDatePickerDialog(context, i3, i2, i, i6, i5, i4, str, intValue == 8, new PassportActivity$$ExternalSyntheticLambda69(this, intValue, editTextBoldCursor));
                if (intValue == 8) {
                    createDatePickerDialog.setNegativeButton(LocaleController.getString("PassportSelectNotExpire", R.string.PassportSelectNotExpire), new PassportActivity$$ExternalSyntheticLambda10(this, editTextBoldCursor));
                }
                showDialog(createDatePickerDialog.create());
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$47(int i, EditTextBoldCursor editTextBoldCursor, int i2, int i3, int i4) {
        if (i == 8) {
            int[] iArr = this.currentExpireDate;
            iArr[0] = i2;
            iArr[1] = i3 + 1;
            iArr[2] = i4;
        }
        editTextBoldCursor.setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i3 + 1), Integer.valueOf(i2)}));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$48(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i) {
        int[] iArr = this.currentExpireDate;
        iArr[2] = 0;
        iArr[1] = 0;
        iArr[0] = 0;
        editTextBoldCursor.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createIdentityInterface$51(View view, MotionEvent motionEvent) {
        if (getParentActivity() == null) {
            return false;
        }
        if (motionEvent.getAction() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("PassportSelectGender", R.string.PassportSelectGender));
            builder.setItems(new CharSequence[]{LocaleController.getString("PassportMale", R.string.PassportMale), LocaleController.getString("PassportFemale", R.string.PassportFemale)}, new PassportActivity$$ExternalSyntheticLambda3(this));
            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$50(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.currentGender = "male";
            this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
        } else if (i == 1) {
            this.currentGender = "female";
            this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createIdentityInterface$52(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (intValue < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[intValue].isFocusable()) {
                this.inputFields[intValue].requestFocus();
            } else {
                this.inputFields[intValue].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createIdentityInterface$53(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int intValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (intValue < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[intValue].isFocusable()) {
                this.inputExtraFields[intValue].requestFocus();
            } else {
                this.inputExtraFields[intValue].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createIdentityInterface$54(View view) {
        createDocumentDeleteAlert();
    }

    private void updateInterfaceStringsForDocumentType() {
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType = this.currentDocumentsType;
        if (tLRPC$TL_secureRequiredType != null) {
            this.actionBar.setTitle(getTextForType(tLRPC$TL_secureRequiredType.type));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
        updateUploadText(4);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0085, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense) == false) goto L_0x0088;
     */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateUploadText(int r7) {
        /*
            r6 = this;
            java.lang.String r0 = "PassportUploadAdditinalDocument"
            java.lang.String r1 = "PassportUploadDocument"
            r2 = 1
            r3 = 0
            if (r7 != 0) goto L_0x002f
            org.telegram.ui.Cells.TextSettingsCell r7 = r6.uploadDocumentCell
            if (r7 != 0) goto L_0x000d
            return
        L_0x000d:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r7 = r6.documents
            int r7 = r7.size()
            if (r7 < r2) goto L_0x0022
            org.telegram.ui.Cells.TextSettingsCell r7 = r6.uploadDocumentCell
            int r1 = org.telegram.messenger.R.string.PassportUploadAdditinalDocument
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)
            r7.setText(r0, r3)
            goto L_0x00f7
        L_0x0022:
            org.telegram.ui.Cells.TextSettingsCell r7 = r6.uploadDocumentCell
            int r0 = org.telegram.messenger.R.string.PassportUploadDocument
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0, r3)
            goto L_0x00f7
        L_0x002f:
            r4 = 8
            if (r7 != r2) goto L_0x0043
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = r6.uploadSelfieCell
            if (r7 != 0) goto L_0x0038
            return
        L_0x0038:
            org.telegram.messenger.SecureDocument r0 = r6.selfieDocument
            if (r0 == 0) goto L_0x003e
            r3 = 8
        L_0x003e:
            r7.setVisibility(r3)
            goto L_0x00f7
        L_0x0043:
            r5 = 4
            if (r7 != r5) goto L_0x006d
            org.telegram.ui.Cells.TextSettingsCell r7 = r6.uploadTranslationCell
            if (r7 != 0) goto L_0x004b
            return
        L_0x004b:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r7 = r6.translationDocuments
            int r7 = r7.size()
            if (r7 < r2) goto L_0x0060
            org.telegram.ui.Cells.TextSettingsCell r7 = r6.uploadTranslationCell
            int r1 = org.telegram.messenger.R.string.PassportUploadAdditinalDocument
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)
            r7.setText(r0, r3)
            goto L_0x00f7
        L_0x0060:
            org.telegram.ui.Cells.TextSettingsCell r7 = r6.uploadTranslationCell
            int r0 = org.telegram.messenger.R.string.PassportUploadDocument
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0, r3)
            goto L_0x00f7
        L_0x006d:
            r0 = 2
            if (r7 != r0) goto L_0x00c7
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = r6.uploadFrontCell
            if (r7 != 0) goto L_0x0075
            return
        L_0x0075:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = r6.currentDocumentsType
            if (r0 == 0) goto L_0x0088
            boolean r1 = r0.selfie_required
            if (r1 != 0) goto L_0x0089
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
            if (r5 != 0) goto L_0x0089
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
            if (r1 == 0) goto L_0x0088
            goto L_0x0089
        L_0x0088:
            r2 = 0
        L_0x0089:
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r0.type
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassport
            if (r1 != 0) goto L_0x00a8
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport
            if (r0 == 0) goto L_0x0094
            goto L_0x00a8
        L_0x0094:
            int r0 = org.telegram.messenger.R.string.PassportFrontSide
            java.lang.String r1 = "PassportFrontSide"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            int r1 = org.telegram.messenger.R.string.PassportFrontSideInfo
            java.lang.String r5 = "PassportFrontSideInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r7.setTextAndValue(r0, r1, r2)
            goto L_0x00bb
        L_0x00a8:
            int r0 = org.telegram.messenger.R.string.PassportMainPage
            java.lang.String r1 = "PassportMainPage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            int r1 = org.telegram.messenger.R.string.PassportMainPageInfo
            java.lang.String r5 = "PassportMainPageInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r7.setTextAndValue(r0, r1, r2)
        L_0x00bb:
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = r6.uploadFrontCell
            org.telegram.messenger.SecureDocument r0 = r6.frontDocument
            if (r0 == 0) goto L_0x00c3
            r3 = 8
        L_0x00c3:
            r7.setVisibility(r3)
            goto L_0x00f7
        L_0x00c7:
            r0 = 3
            if (r7 != r0) goto L_0x00f7
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = r6.uploadReverseCell
            if (r7 != 0) goto L_0x00cf
            return
        L_0x00cf:
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r7 = r6.currentDocumentsType
            org.telegram.tgnet.TLRPC$SecureValueType r7 = r7.type
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
            if (r0 != 0) goto L_0x00e7
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
            if (r7 == 0) goto L_0x00dc
            goto L_0x00e7
        L_0x00dc:
            android.widget.LinearLayout r7 = r6.reverseLayout
            r7.setVisibility(r4)
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = r6.uploadReverseCell
            r7.setVisibility(r4)
            goto L_0x00f7
        L_0x00e7:
            android.widget.LinearLayout r7 = r6.reverseLayout
            r7.setVisibility(r3)
            org.telegram.ui.Cells.TextDetailSettingsCell r7 = r6.uploadReverseCell
            org.telegram.messenger.SecureDocument r0 = r6.reverseDocument
            if (r0 == 0) goto L_0x00f4
            r3 = 8
        L_0x00f4:
            r7.setVisibility(r3)
        L_0x00f7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.updateUploadText(int):void");
    }

    private void checkTopErrorCell(boolean z) {
        String str;
        String str2;
        if (this.topErrorCell != null) {
            SpannableStringBuilder spannableStringBuilder = null;
            if (this.fieldsErrors != null && ((z || this.errorsValues.containsKey("error_all")) && (str2 = this.fieldsErrors.get("error_all")) != null)) {
                spannableStringBuilder = new SpannableStringBuilder(str2);
                if (z) {
                    this.errorsValues.put("error_all", "");
                }
            }
            if (this.documentsErrors != null && ((z || this.errorsValues.containsKey("error_document_all")) && (str = this.documentsErrors.get("error_all")) != null)) {
                if (spannableStringBuilder == null) {
                    spannableStringBuilder = new SpannableStringBuilder(str);
                } else {
                    spannableStringBuilder.append("\n\n").append(str);
                }
                if (z) {
                    this.errorsValues.put("error_document_all", "");
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

    private void addDocumentViewInternal(TLRPC$TL_secureFile tLRPC$TL_secureFile, int i) {
        addDocumentView(new SecureDocument(getSecureDocumentKey(tLRPC$TL_secureFile.secret, tLRPC$TL_secureFile.file_hash), tLRPC$TL_secureFile, (String) null, (byte[]) null, (byte[]) null), i);
    }

    private void addDocumentViews(ArrayList<TLRPC$SecureFile> arrayList) {
        this.documents.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$SecureFile tLRPC$SecureFile = arrayList.get(i);
            if (tLRPC$SecureFile instanceof TLRPC$TL_secureFile) {
                addDocumentViewInternal((TLRPC$TL_secureFile) tLRPC$SecureFile, 0);
            }
        }
    }

    private void addTranslationDocumentViews(ArrayList<TLRPC$SecureFile> arrayList) {
        this.translationDocuments.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$SecureFile tLRPC$SecureFile = arrayList.get(i);
            if (tLRPC$SecureFile instanceof TLRPC$TL_secureFile) {
                addDocumentViewInternal((TLRPC$TL_secureFile) tLRPC$SecureFile, 4);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x009d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setFieldValues(java.util.HashMap<java.lang.String, java.lang.String> r7, org.telegram.ui.Components.EditTextBoldCursor r8, java.lang.String r9) {
        /*
            r6 = this;
            java.lang.Object r7 = r7.get(r9)
            java.lang.String r7 = (java.lang.String) r7
            if (r7 == 0) goto L_0x00ec
            r9.hashCode()
            r0 = -1
            int r1 = r9.hashCode()
            r2 = 3
            r3 = 2
            r4 = 1
            r5 = 0
            switch(r1) {
                case -2006252145: goto L_0x0039;
                case -1249512767: goto L_0x002e;
                case 475919162: goto L_0x0023;
                case 1481071862: goto L_0x0018;
                default: goto L_0x0017;
            }
        L_0x0017:
            goto L_0x0043
        L_0x0018:
            java.lang.String r1 = "country_code"
            boolean r1 = r9.equals(r1)
            if (r1 != 0) goto L_0x0021
            goto L_0x0043
        L_0x0021:
            r0 = 3
            goto L_0x0043
        L_0x0023:
            java.lang.String r1 = "expiry_date"
            boolean r1 = r9.equals(r1)
            if (r1 != 0) goto L_0x002c
            goto L_0x0043
        L_0x002c:
            r0 = 2
            goto L_0x0043
        L_0x002e:
            java.lang.String r1 = "gender"
            boolean r1 = r9.equals(r1)
            if (r1 != 0) goto L_0x0037
            goto L_0x0043
        L_0x0037:
            r0 = 1
            goto L_0x0043
        L_0x0039:
            java.lang.String r1 = "residence_country_code"
            boolean r1 = r9.equals(r1)
            if (r1 != 0) goto L_0x0042
            goto L_0x0043
        L_0x0042:
            r0 = 0
        L_0x0043:
            switch(r0) {
                case 0: goto L_0x00dd;
                case 1: goto L_0x00b1;
                case 2: goto L_0x005c;
                case 3: goto L_0x004b;
                default: goto L_0x0046;
            }
        L_0x0046:
            r8.setText(r7)
            goto L_0x00ec
        L_0x004b:
            r6.currentCitizeship = r7
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r6.languageMap
            java.lang.Object r7 = r0.get(r7)
            java.lang.String r7 = (java.lang.String) r7
            if (r7 == 0) goto L_0x00ec
            r8.setText(r7)
            goto L_0x00ec
        L_0x005c:
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x009a
            java.lang.String r0 = "\\."
            java.lang.String[] r0 = r7.split(r0)
            int r1 = r0.length
            if (r1 != r2) goto L_0x009a
            int[] r1 = r6.currentExpireDate
            r2 = r0[r3]
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r2 = r2.intValue()
            r1[r5] = r2
            int[] r1 = r6.currentExpireDate
            r2 = r0[r4]
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)
            int r2 = r2.intValue()
            r1[r4] = r2
            int[] r1 = r6.currentExpireDate
            r0 = r0[r5]
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)
            int r0 = r0.intValue()
            r1[r3] = r0
            r8.setText(r7)
            r7 = 1
            goto L_0x009b
        L_0x009a:
            r7 = 0
        L_0x009b:
            if (r7 != 0) goto L_0x00ec
            int[] r7 = r6.currentExpireDate
            r7[r3] = r5
            r7[r4] = r5
            r7[r5] = r5
            int r7 = org.telegram.messenger.R.string.PassportNoExpireDate
            java.lang.String r0 = "PassportNoExpireDate"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
            r8.setText(r7)
            goto L_0x00ec
        L_0x00b1:
            java.lang.String r0 = "male"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00c7
            r6.currentGender = r7
            int r7 = org.telegram.messenger.R.string.PassportMale
            java.lang.String r0 = "PassportMale"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
            r8.setText(r7)
            goto L_0x00ec
        L_0x00c7:
            java.lang.String r0 = "female"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00ec
            r6.currentGender = r7
            int r7 = org.telegram.messenger.R.string.PassportFemale
            java.lang.String r0 = "PassportFemale"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
            r8.setText(r7)
            goto L_0x00ec
        L_0x00dd:
            r6.currentResidence = r7
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r6.languageMap
            java.lang.Object r7 = r0.get(r7)
            java.lang.String r7 = (java.lang.String) r7
            if (r7 == 0) goto L_0x00ec
            r8.setText(r7)
        L_0x00ec:
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r6.fieldsErrors
            if (r7 == 0) goto L_0x0109
            java.lang.Object r7 = r7.get(r9)
            java.lang.String r7 = (java.lang.String) r7
            if (r7 == 0) goto L_0x0109
            r8.setErrorText(r7)
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r6.errorsValues
            android.text.Editable r8 = r8.getText()
            java.lang.String r8 = r8.toString()
            r7.put(r9, r8)
            goto L_0x0125
        L_0x0109:
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r6.documentsErrors
            if (r7 == 0) goto L_0x0125
            java.lang.Object r7 = r7.get(r9)
            java.lang.String r7 = (java.lang.String) r7
            if (r7 == 0) goto L_0x0125
            r8.setErrorText(r7)
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r6.errorsValues
            android.text.Editable r8 = r8.getText()
            java.lang.String r8 = r8.toString()
            r7.put(r9, r8)
        L_0x0125:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setFieldValues(java.util.HashMap, org.telegram.ui.Components.EditTextBoldCursor, java.lang.String):void");
    }

    private void addDocumentView(SecureDocument secureDocument, int i) {
        String string;
        String str;
        String str2;
        HashMap<String, String> hashMap;
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
            SecureDocumentCell secureDocumentCell = new SecureDocumentCell(getParentActivity());
            secureDocumentCell.setTag(secureDocument);
            secureDocumentCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.documentsCells.put(secureDocument, secureDocumentCell);
            String documentHash = getDocumentHash(secureDocument);
            if (i == 1) {
                string = LocaleController.getString("PassportSelfie", R.string.PassportSelfie);
                this.selfieLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                str = "selfie" + documentHash;
            } else if (i == 4) {
                string = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                this.translationLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                str = "translation" + documentHash;
            } else if (i == 2) {
                TLRPC$SecureValueType tLRPC$SecureValueType = this.currentDocumentsType.type;
                if ((tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassport) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeInternalPassport)) {
                    string = LocaleController.getString("PassportMainPage", R.string.PassportMainPage);
                } else {
                    string = LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide);
                }
                this.frontLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                str = "front" + documentHash;
            } else if (i == 3) {
                string = LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide);
                this.reverseLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                str = "reverse" + documentHash;
            } else {
                string = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                this.documentsLayout.addView(secureDocumentCell, LayoutHelper.createLinear(-1, -2));
                str = "files" + documentHash;
            }
            String str3 = str;
            if (str3 == null || (hashMap = this.documentsErrors) == null || (str2 = hashMap.get(str3)) == null) {
                str2 = LocaleController.formatDateForBan((long) secureDocument.secureFile.date);
            } else {
                secureDocumentCell.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
                this.errorsValues.put(str3, "");
            }
            secureDocumentCell.setTextAndValueAndImage(string, str2, secureDocument);
            secureDocumentCell.setOnClickListener(new PassportActivity$$ExternalSyntheticLambda29(this, i));
            secureDocumentCell.setOnLongClickListener(new PassportActivity$$ExternalSyntheticLambda33(this, i, secureDocument, secureDocumentCell, str3));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addDocumentView$55(int i, View view) {
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
        if (i == 1) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.selfieDocument);
            PhotoViewer.getInstance().openPhoto((ArrayList<SecureDocument>) arrayList, 0, this.provider);
        } else if (i == 2) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(this.frontDocument);
            PhotoViewer.getInstance().openPhoto((ArrayList<SecureDocument>) arrayList2, 0, this.provider);
        } else if (i == 3) {
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(this.reverseDocument);
            PhotoViewer.getInstance().openPhoto((ArrayList<SecureDocument>) arrayList3, 0, this.provider);
        } else if (i == 0) {
            PhotoViewer instance = PhotoViewer.getInstance();
            ArrayList<SecureDocument> arrayList4 = this.documents;
            instance.openPhoto(arrayList4, arrayList4.indexOf(secureDocument), this.provider);
        } else {
            PhotoViewer instance2 = PhotoViewer.getInstance();
            ArrayList<SecureDocument> arrayList5 = this.translationDocuments;
            instance2.openPhoto(arrayList5, arrayList5.indexOf(secureDocument), this.provider);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$addDocumentView$57(int i, SecureDocument secureDocument, SecureDocumentCell secureDocumentCell, String str, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (i == 1) {
            builder.setMessage(LocaleController.getString("PassportDeleteSelfie", R.string.PassportDeleteSelfie));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteScan", R.string.PassportDeleteScan));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PassportActivity$$ExternalSyntheticLambda6(this, secureDocument, i, secureDocumentCell, str));
        showDialog(builder.create());
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addDocumentView$56(SecureDocument secureDocument, int i, SecureDocumentCell secureDocumentCell, String str, DialogInterface dialogInterface, int i2) {
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
            HashMap<String, String> hashMap = this.documentsErrors;
            if (hashMap != null) {
                hashMap.remove(str);
            }
            HashMap<String, String> hashMap2 = this.errorsValues;
            if (hashMap2 != null) {
                hashMap2.remove(str);
            }
        }
        updateUploadText(i);
        String str2 = secureDocument.path;
        if (str2 != null && this.uploadingDocuments.remove(str2) != null) {
            if (this.uploadingDocuments.isEmpty()) {
                this.doneItem.setEnabled(true);
                this.doneItem.setAlpha(1.0f);
            }
            FileLoader.getInstance(this.currentAccount).cancelFileUpload(secureDocument.path, false);
        }
    }

    private String getNameForType(TLRPC$SecureValueType tLRPC$SecureValueType) {
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails) {
            return "personal_details";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassport) {
            return "passport";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeInternalPassport) {
            return "internal_passport";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeDriverLicense) {
            return "driver_license";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeIdentityCard) {
            return "identity_card";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeUtilityBill) {
            return "utility_bill";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeAddress) {
            return "address";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeBankStatement) {
            return "bank_statement";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
            return "rental_agreement";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
            return "temporary_registration";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassportRegistration) {
            return "passport_registration";
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeEmail) {
            return "email";
        }
        return tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePhone ? "phone" : "";
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r3 = r2.documentsToTypesLink.get(r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.ui.PassportActivity.TextDetailSecureCell getViewByType(org.telegram.tgnet.TLRPC$TL_secureRequiredType r3) {
        /*
            r2 = this;
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.ui.PassportActivity$TextDetailSecureCell> r0 = r2.typesViews
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.PassportActivity$TextDetailSecureCell r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0
            if (r0 != 0) goto L_0x001d
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType> r1 = r2.documentsToTypesLink
            java.lang.Object r3 = r1.get(r3)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r3 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r3
            if (r3 == 0) goto L_0x001d
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.ui.PassportActivity$TextDetailSecureCell> r0 = r2.typesViews
            java.lang.Object r3 = r0.get(r3)
            r0 = r3
            org.telegram.ui.PassportActivity$TextDetailSecureCell r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0
        L_0x001d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.getViewByType(org.telegram.tgnet.TLRPC$TL_secureRequiredType):org.telegram.ui.PassportActivity$TextDetailSecureCell");
    }

    private String getTextForType(TLRPC$SecureValueType tLRPC$SecureValueType) {
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassport) {
            return LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeDriverLicense) {
            return LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeIdentityCard) {
            return LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeUtilityBill) {
            return LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeBankStatement) {
            return LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
            return LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeInternalPassport) {
            return LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassportRegistration) {
            return LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
            return LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration);
        }
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePhone) {
            return LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone);
        }
        return tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeEmail ? LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail) : "";
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0243 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0290 A[Catch:{ Exception -> 0x03e1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0344 A[Catch:{ Exception -> 0x0393 }] */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0373 A[Catch:{ Exception -> 0x0393 }] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x03fe  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0425  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0435  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0438  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0454  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04bf  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0567  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x056a  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0108  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTypeValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType r31, java.lang.String r32, java.lang.String r33, org.telegram.tgnet.TLRPC$TL_secureRequiredType r34, java.lang.String r35, boolean r36, int r37) {
        /*
            r30 = this;
            r7 = r30
            r8 = r31
            r9 = r32
            r10 = r33
            r11 = r34
            r12 = r35
            r13 = r37
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.ui.PassportActivity$TextDetailSecureCell> r0 = r7.typesViews
            java.lang.Object r0 = r0.get(r8)
            org.telegram.ui.PassportActivity$TextDetailSecureCell r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0
            r14 = 6
            r15 = 8
            r6 = 1
            if (r0 != 0) goto L_0x0053
            int r0 = r7.currentActivityType
            if (r0 != r15) goto L_0x0052
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            if (r11 == 0) goto L_0x002a
            r4.add(r11)
        L_0x002a:
            android.widget.LinearLayout r0 = r7.linearLayout2
            int r1 = r0.getChildCount()
            int r1 = r1 - r14
            android.view.View r0 = r0.getChildAt(r1)
            boolean r1 = r0 instanceof org.telegram.ui.PassportActivity.TextDetailSecureCell
            if (r1 == 0) goto L_0x003e
            org.telegram.ui.PassportActivity$TextDetailSecureCell r0 = (org.telegram.ui.PassportActivity.TextDetailSecureCell) r0
            r0.setNeedDivider(r6)
        L_0x003e:
            android.app.Activity r2 = r30.getParentActivity()
            r5 = 1
            r0 = 1
            r1 = r30
            r3 = r31
            r14 = 1
            r6 = r0
            org.telegram.ui.PassportActivity$TextDetailSecureCell r0 = r1.addField(r2, r3, r4, r5, r6)
            r30.updateManageVisibility()
            goto L_0x0054
        L_0x0052:
            return
        L_0x0053:
            r14 = 1
        L_0x0054:
            r1 = r0
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r0 = r7.typesValues
            java.lang.Object r0 = r0.get(r8)
            r2 = r0
            java.util.HashMap r2 = (java.util.HashMap) r2
            if (r11 == 0) goto L_0x006a
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r0 = r7.typesValues
            java.lang.Object r0 = r0.get(r11)
            java.util.HashMap r0 = (java.util.HashMap) r0
            r4 = r0
            goto L_0x006b
        L_0x006a:
            r4 = 0
        L_0x006b:
            org.telegram.tgnet.TLRPC$TL_secureValue r5 = r7.getValueByType(r8, r14)
            org.telegram.tgnet.TLRPC$TL_secureValue r6 = r7.getValueByType(r11, r14)
            if (r10 == 0) goto L_0x00c6
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r7.languageMap
            if (r0 != 0) goto L_0x00c6
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.languageMap = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00be }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00be }
            android.content.Context r17 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00be }
            android.content.res.Resources r17 = r17.getResources()     // Catch:{ Exception -> 0x00be }
            android.content.res.AssetManager r15 = r17.getAssets()     // Catch:{ Exception -> 0x00be }
            java.lang.String r14 = "countries.txt"
            java.io.InputStream r14 = r15.open(r14)     // Catch:{ Exception -> 0x00be }
            r3.<init>(r14)     // Catch:{ Exception -> 0x00be }
            r0.<init>(r3)     // Catch:{ Exception -> 0x00be }
        L_0x009a:
            java.lang.String r3 = r0.readLine()     // Catch:{ Exception -> 0x00be }
            if (r3 == 0) goto L_0x00b6
            java.lang.String r14 = ";"
            java.lang.String[] r3 = r3.split(r14)     // Catch:{ Exception -> 0x00be }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r7.languageMap     // Catch:{ Exception -> 0x00be }
            r19 = r5
            r15 = 1
            r5 = r3[r15]     // Catch:{ Exception -> 0x00bc }
            r15 = 2
            r3 = r3[r15]     // Catch:{ Exception -> 0x00bc }
            r14.put(r5, r3)     // Catch:{ Exception -> 0x00bc }
            r5 = r19
            goto L_0x009a
        L_0x00b6:
            r19 = r5
            r0.close()     // Catch:{ Exception -> 0x00bc }
            goto L_0x00c4
        L_0x00bc:
            r0 = move-exception
            goto L_0x00c1
        L_0x00be:
            r0 = move-exception
            r19 = r5
        L_0x00c1:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c4:
            r3 = 0
            goto L_0x00cb
        L_0x00c6:
            r19 = r5
            r3 = 0
            r7.languageMap = r3
        L_0x00cb:
            java.lang.String r5 = "PassportDocuments"
            if (r9 == 0) goto L_0x0108
            org.telegram.tgnet.TLRPC$SecureValueType r0 = r8.type
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
            if (r2 == 0) goto L_0x00f8
            org.telegram.PhoneFormat.PhoneFormat r0 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "+"
            r2.append(r4)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            java.lang.String r0 = r0.format(r2)
            r25 = r1
            r26 = r5
            r23 = r6
        L_0x00f4:
            r20 = 0
            goto L_0x03eb
        L_0x00f8:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
            r25 = r1
            r26 = r5
            r23 = r6
            if (r0 == 0) goto L_0x0104
            r0 = r9
            goto L_0x00f4
        L_0x0104:
            r20 = 0
            goto L_0x03ea
        L_0x0108:
            int r0 = r7.currentActivityType
            r9 = 8
            if (r0 == r9) goto L_0x013a
            if (r11 == 0) goto L_0x013a
            boolean r0 = android.text.TextUtils.isEmpty(r35)
            if (r0 == 0) goto L_0x0118
            if (r6 == 0) goto L_0x013a
        L_0x0118:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r9 = 1
            if (r13 <= r9) goto L_0x012a
            org.telegram.tgnet.TLRPC$SecureValueType r9 = r11.type
            java.lang.String r9 = r7.getTextForType(r9)
            r0.append(r9)
            goto L_0x013b
        L_0x012a:
            boolean r9 = android.text.TextUtils.isEmpty(r35)
            if (r9 == 0) goto L_0x013b
            int r9 = org.telegram.messenger.R.string.PassportDocuments
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r0.append(r9)
            goto L_0x013b
        L_0x013a:
            r0 = r3
        L_0x013b:
            if (r10 != 0) goto L_0x014a
            if (r12 == 0) goto L_0x0140
            goto L_0x014a
        L_0x0140:
            r25 = r1
            r26 = r5
            r23 = r6
            r20 = 0
            goto L_0x03e3
        L_0x014a:
            if (r2 != 0) goto L_0x014d
            return
        L_0x014d:
            r2.clear()
            org.telegram.tgnet.TLRPC$SecureValueType r9 = r8.type
            boolean r15 = r9 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            java.lang.String r3 = "residence_country_code"
            java.lang.String r14 = "gender"
            r21 = 5
            r22 = 4
            r23 = r6
            java.lang.String r6 = "first_name_native"
            r24 = 3
            r25 = r1
            java.lang.String r1 = "last_name"
            java.lang.String r13 = "middle_name"
            r26 = r5
            java.lang.String r5 = "country_code"
            java.lang.String r8 = "last_name_native"
            r27 = r2
            java.lang.String r2 = "middle_name_native"
            if (r15 == 0) goto L_0x01cd
            int r9 = r7.currentActivityType
            if (r9 != 0) goto L_0x017a
            if (r36 == 0) goto L_0x0180
        L_0x017a:
            r15 = 8
            if (r9 != r15) goto L_0x01ad
            if (r11 != 0) goto L_0x01ad
        L_0x0180:
            r15 = 10
            java.lang.String[] r15 = new java.lang.String[r15]
            java.lang.String r28 = "first_name"
            r20 = 0
            r15[r20] = r28
            r17 = 1
            r15[r17] = r13
            r18 = 2
            r15[r18] = r1
            r15[r24] = r6
            r15[r22] = r2
            r15[r21] = r8
            java.lang.String r21 = "birth_date"
            r16 = 6
            r15[r16] = r21
            r16 = 7
            r15[r16] = r14
            r32 = r14
            r14 = 8
            r15[r14] = r5
            r16 = 9
            r15[r16] = r3
            goto L_0x01b2
        L_0x01ad:
            r32 = r14
            r14 = 8
            r15 = 0
        L_0x01b2:
            if (r9 == 0) goto L_0x01bb
            if (r9 != r14) goto L_0x01b9
            if (r11 == 0) goto L_0x01b9
            goto L_0x01bb
        L_0x01b9:
            r14 = 0
            goto L_0x01ca
        L_0x01bb:
            r9 = 2
            java.lang.String[] r14 = new java.lang.String[r9]
            java.lang.String r9 = "document_no"
            r16 = 0
            r14[r16] = r9
            java.lang.String r9 = "expiry_date"
            r16 = 1
            r14[r16] = r9
        L_0x01ca:
            r20 = 0
            goto L_0x0205
        L_0x01cd:
            r32 = r14
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r9 == 0) goto L_0x0201
            int r9 = r7.currentActivityType
            if (r9 != 0) goto L_0x01dc
            if (r36 == 0) goto L_0x01da
            goto L_0x01dc
        L_0x01da:
            r9 = 6
            goto L_0x01e3
        L_0x01dc:
            r14 = 8
            if (r9 != r14) goto L_0x0201
            if (r11 != 0) goto L_0x0201
            goto L_0x01da
        L_0x01e3:
            java.lang.String[] r15 = new java.lang.String[r9]
            java.lang.String r9 = "street_line1"
            r20 = 0
            r15[r20] = r9
            java.lang.String r9 = "street_line2"
            r14 = 1
            r15[r14] = r9
            java.lang.String r9 = "post_code"
            r14 = 2
            r15[r14] = r9
            java.lang.String r9 = "city"
            r15[r24] = r9
            java.lang.String r9 = "state"
            r15[r22] = r9
            r15[r21] = r5
            r14 = 0
            goto L_0x0205
        L_0x0201:
            r20 = 0
            r14 = 0
            r15 = 0
        L_0x0205:
            if (r15 != 0) goto L_0x0209
            if (r14 == 0) goto L_0x03e3
        L_0x0209:
            r16 = r0
            r22 = r14
            r0 = 0
            r9 = 0
            r14 = 2
            r21 = 0
        L_0x0212:
            if (r9 >= r14) goto L_0x03e1
            if (r9 != 0) goto L_0x0220
            if (r10 == 0) goto L_0x023e
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x03e1 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x03e1 }
            r14 = r0
            r10 = r15
            goto L_0x0241
        L_0x0220:
            if (r4 != 0) goto L_0x0233
            r28 = r1
            r29 = r2
            r24 = r4
            r10 = r21
            r14 = 2
            r1 = r32
            r21 = r15
            r15 = r27
            goto L_0x03cb
        L_0x0233:
            if (r12 == 0) goto L_0x023e
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x03e1 }
            r0.<init>(r12)     // Catch:{ Exception -> 0x03e1 }
            r14 = r0
            r10 = r22
            goto L_0x0241
        L_0x023e:
            r14 = r0
            r10 = r21
        L_0x0241:
            if (r10 == 0) goto L_0x03ba
            if (r14 != 0) goto L_0x0247
            goto L_0x03ba
        L_0x0247:
            java.util.Iterator r0 = r14.keys()     // Catch:{ all -> 0x0284 }
        L_0x024b:
            boolean r21 = r0.hasNext()     // Catch:{ all -> 0x0284 }
            if (r21 == 0) goto L_0x027f
            java.lang.Object r21 = r0.next()     // Catch:{ all -> 0x0284 }
            r24 = r0
            r0 = r21
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x0284 }
            if (r9 != 0) goto L_0x0269
            java.lang.String r12 = r14.getString(r0)     // Catch:{ all -> 0x0284 }
            r21 = r15
            r15 = r27
            r15.put(r0, r12)     // Catch:{ all -> 0x027d }
            goto L_0x0274
        L_0x0269:
            r21 = r15
            r15 = r27
            java.lang.String r12 = r14.getString(r0)     // Catch:{ all -> 0x027d }
            r4.put(r0, r12)     // Catch:{ all -> 0x027d }
        L_0x0274:
            r12 = r35
            r27 = r15
            r15 = r21
            r0 = r24
            goto L_0x024b
        L_0x027d:
            r0 = move-exception
            goto L_0x0289
        L_0x027f:
            r21 = r15
            r15 = r27
            goto L_0x028c
        L_0x0284:
            r0 = move-exception
            r21 = r15
            r15 = r27
        L_0x0289:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x03e1 }
        L_0x028c:
            r0 = 0
        L_0x028d:
            int r12 = r10.length     // Catch:{ Exception -> 0x03e1 }
            if (r0 >= r12) goto L_0x03ae
            r12 = r10[r0]     // Catch:{ Exception -> 0x03e1 }
            boolean r12 = r14.has(r12)     // Catch:{ Exception -> 0x03e1 }
            if (r12 == 0) goto L_0x0395
            if (r16 != 0) goto L_0x02a2
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03e1 }
            r12.<init>()     // Catch:{ Exception -> 0x03e1 }
            r24 = r4
            goto L_0x02a6
        L_0x02a2:
            r24 = r4
            r12 = r16
        L_0x02a6:
            r4 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            java.lang.String r4 = r14.getString(r4)     // Catch:{ Exception -> 0x0393 }
            if (r4 == 0) goto L_0x0387
            boolean r16 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0393 }
            if (r16 != 0) goto L_0x0387
            r27 = r14
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r6.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 != 0) goto L_0x0382
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r2.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 != 0) goto L_0x0382
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r8.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 == 0) goto L_0x02d0
            goto L_0x0382
        L_0x02d0:
            int r14 = r12.length()     // Catch:{ Exception -> 0x0393 }
            if (r14 <= 0) goto L_0x0302
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r1.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 != 0) goto L_0x02fd
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r8.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 != 0) goto L_0x02fd
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 != 0) goto L_0x02fd
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            boolean r14 = r2.equals(r14)     // Catch:{ Exception -> 0x0393 }
            if (r14 == 0) goto L_0x02f7
            goto L_0x02fd
        L_0x02f7:
            java.lang.String r14 = ", "
            r12.append(r14)     // Catch:{ Exception -> 0x0393 }
            goto L_0x0302
        L_0x02fd:
            java.lang.String r14 = " "
            r12.append(r14)     // Catch:{ Exception -> 0x0393 }
        L_0x0302:
            r14 = r10[r0]     // Catch:{ Exception -> 0x0393 }
            r16 = -1
            r28 = r1
            int r1 = r14.hashCode()     // Catch:{ Exception -> 0x0393 }
            r29 = r2
            r2 = -2006252145(0xfffffffvar_b058f, float:-7.0724274E-34)
            if (r1 == r2) goto L_0x0334
            r2 = -1249512767(0xffffffffb585f2c1, float:-9.979923E-7)
            if (r1 == r2) goto L_0x032a
            r2 = 1481071862(0x58475cf6, float:8.7680831E14)
            if (r1 == r2) goto L_0x0320
        L_0x031d:
            r1 = r32
            goto L_0x033e
        L_0x0320:
            boolean r1 = r14.equals(r5)     // Catch:{ Exception -> 0x0393 }
            if (r1 == 0) goto L_0x031d
            r1 = r32
            r2 = 0
            goto L_0x033f
        L_0x032a:
            r1 = r32
            boolean r2 = r14.equals(r1)     // Catch:{ Exception -> 0x0393 }
            if (r2 == 0) goto L_0x033e
            r2 = 2
            goto L_0x033f
        L_0x0334:
            r1 = r32
            boolean r2 = r14.equals(r3)     // Catch:{ Exception -> 0x0393 }
            if (r2 == 0) goto L_0x033e
            r2 = 1
            goto L_0x033f
        L_0x033e:
            r2 = -1
        L_0x033f:
            if (r2 == 0) goto L_0x0373
            r14 = 1
            if (r2 == r14) goto L_0x0373
            r14 = 2
            if (r2 == r14) goto L_0x034b
            r12.append(r4)     // Catch:{ Exception -> 0x0393 }
            goto L_0x0390
        L_0x034b:
            java.lang.String r2 = "male"
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x0393 }
            if (r2 == 0) goto L_0x035f
            java.lang.String r2 = "PassportMale"
            int r4 = org.telegram.messenger.R.string.PassportMale     // Catch:{ Exception -> 0x0393 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r4)     // Catch:{ Exception -> 0x0393 }
            r12.append(r2)     // Catch:{ Exception -> 0x0393 }
            goto L_0x0390
        L_0x035f:
            java.lang.String r2 = "female"
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x0393 }
            if (r2 == 0) goto L_0x0390
            java.lang.String r2 = "PassportFemale"
            int r4 = org.telegram.messenger.R.string.PassportFemale     // Catch:{ Exception -> 0x0393 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r4)     // Catch:{ Exception -> 0x0393 }
            r12.append(r2)     // Catch:{ Exception -> 0x0393 }
            goto L_0x0390
        L_0x0373:
            r14 = 2
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r7.languageMap     // Catch:{ Exception -> 0x0393 }
            java.lang.Object r2 = r2.get(r4)     // Catch:{ Exception -> 0x0393 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0393 }
            if (r2 == 0) goto L_0x0390
            r12.append(r2)     // Catch:{ Exception -> 0x0393 }
            goto L_0x0390
        L_0x0382:
            r28 = r1
            r29 = r2
            goto L_0x038d
        L_0x0387:
            r28 = r1
            r29 = r2
            r27 = r14
        L_0x038d:
            r14 = 2
            r1 = r32
        L_0x0390:
            r16 = r12
            goto L_0x03a0
        L_0x0393:
            r0 = r12
            goto L_0x03e3
        L_0x0395:
            r28 = r1
            r29 = r2
            r24 = r4
            r27 = r14
            r14 = 2
            r1 = r32
        L_0x03a0:
            int r0 = r0 + 1
            r32 = r1
            r4 = r24
            r14 = r27
            r1 = r28
            r2 = r29
            goto L_0x028d
        L_0x03ae:
            r28 = r1
            r29 = r2
            r24 = r4
            r27 = r14
            r14 = 2
            r1 = r32
            goto L_0x03c9
        L_0x03ba:
            r28 = r1
            r29 = r2
            r24 = r4
            r21 = r15
            r15 = r27
            r1 = r32
            r27 = r14
            r14 = 2
        L_0x03c9:
            r0 = r27
        L_0x03cb:
            int r9 = r9 + 1
            r12 = r35
            r32 = r1
            r27 = r15
            r15 = r21
            r4 = r24
            r1 = r28
            r2 = r29
            r21 = r10
            r10 = r33
            goto L_0x0212
        L_0x03e1:
            r0 = r16
        L_0x03e3:
            if (r0 == 0) goto L_0x03ea
            java.lang.String r0 = r0.toString()
            goto L_0x03eb
        L_0x03ea:
            r0 = 0
        L_0x03eb:
            if (r36 != 0) goto L_0x03fe
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r1 = r7.errorsMap
            r2 = r31
            org.telegram.tgnet.TLRPC$SecureValueType r3 = r2.type
            java.lang.String r3 = r7.getNameForType(r3)
            java.lang.Object r1 = r1.get(r3)
            java.util.HashMap r1 = (java.util.HashMap) r1
            goto L_0x0401
        L_0x03fe:
            r2 = r31
            r1 = 0
        L_0x0401:
            if (r11 == 0) goto L_0x0412
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r3 = r7.errorsMap
            org.telegram.tgnet.TLRPC$SecureValueType r4 = r11.type
            java.lang.String r4 = r7.getNameForType(r4)
            java.lang.Object r3 = r3.get(r4)
            java.util.HashMap r3 = (java.util.HashMap) r3
            goto L_0x0413
        L_0x0412:
            r3 = 0
        L_0x0413:
            if (r1 == 0) goto L_0x041b
            int r1 = r1.size()
            if (r1 > 0) goto L_0x0423
        L_0x041b:
            if (r3 == 0) goto L_0x044e
            int r1 = r3.size()
            if (r1 <= 0) goto L_0x044e
        L_0x0423:
            if (r36 != 0) goto L_0x0435
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r7.mainErrorsMap
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r2.type
            java.lang.String r1 = r7.getNameForType(r1)
            java.lang.Object r0 = r0.get(r1)
            r3 = r0
            java.lang.String r3 = (java.lang.String) r3
            goto L_0x0436
        L_0x0435:
            r3 = 0
        L_0x0436:
            if (r3 != 0) goto L_0x0447
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r7.mainErrorsMap
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r11.type
            java.lang.String r1 = r7.getNameForType(r1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x0448
        L_0x0447:
            r0 = r3
        L_0x0448:
            r1 = r25
            r2 = 1
            r6 = 1
            goto L_0x055e
        L_0x044e:
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r2.type
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            if (r2 == 0) goto L_0x04bf
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x04bc
            if (r11 != 0) goto L_0x0469
            int r0 = org.telegram.messenger.R.string.PassportPersonalDetailsInfo
            java.lang.String r1 = "PassportPersonalDetailsInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x0464:
            r1 = r25
            r2 = 1
            goto L_0x055d
        L_0x0469:
            int r1 = r7.currentActivityType
            r2 = 8
            if (r1 != r2) goto L_0x0478
            int r0 = org.telegram.messenger.R.string.PassportDocuments
            r2 = r26
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0464
        L_0x0478:
            r3 = r37
            r1 = 1
            if (r3 != r1) goto L_0x04b3
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r11.type
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassport
            if (r2 == 0) goto L_0x048c
            int r0 = org.telegram.messenger.R.string.PassportIdentityPassport
            java.lang.String r1 = "PassportIdentityPassport"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0464
        L_0x048c:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport
            if (r2 == 0) goto L_0x0499
            int r0 = org.telegram.messenger.R.string.PassportIdentityInternalPassport
            java.lang.String r1 = "PassportIdentityInternalPassport"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0464
        L_0x0499:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
            if (r2 == 0) goto L_0x04a6
            int r0 = org.telegram.messenger.R.string.PassportIdentityDriverLicence
            java.lang.String r1 = "PassportIdentityDriverLicence"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0464
        L_0x04a6:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
            if (r1 == 0) goto L_0x04bc
            int r0 = org.telegram.messenger.R.string.PassportIdentityID
            java.lang.String r1 = "PassportIdentityID"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0464
        L_0x04b3:
            int r0 = org.telegram.messenger.R.string.PassportIdentityDocumentInfo
            java.lang.String r1 = "PassportIdentityDocumentInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0464
        L_0x04bc:
            r2 = 1
            goto L_0x055b
        L_0x04bf:
            r3 = r37
            r2 = r26
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r4 == 0) goto L_0x0535
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x04bc
            if (r11 != 0) goto L_0x04d8
            int r0 = org.telegram.messenger.R.string.PassportAddressNoUploadInfo
            java.lang.String r1 = "PassportAddressNoUploadInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0464
        L_0x04d8:
            int r1 = r7.currentActivityType
            r4 = 8
            if (r1 != r4) goto L_0x04e6
            int r0 = org.telegram.messenger.R.string.PassportDocuments
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0464
        L_0x04e6:
            r2 = 1
            if (r3 != r2) goto L_0x052c
            org.telegram.tgnet.TLRPC$SecureValueType r1 = r11.type
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeRentalAgreement
            if (r3 == 0) goto L_0x04f8
            int r0 = org.telegram.messenger.R.string.PassportAddAgreementInfo
            java.lang.String r1 = "PassportAddAgreementInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x04f8:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeUtilityBill
            if (r3 == 0) goto L_0x0505
            int r0 = org.telegram.messenger.R.string.PassportAddBillInfo
            java.lang.String r1 = "PassportAddBillInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x0505:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassportRegistration
            if (r3 == 0) goto L_0x0512
            int r0 = org.telegram.messenger.R.string.PassportAddPassportRegistrationInfo
            java.lang.String r1 = "PassportAddPassportRegistrationInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x0512:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeTemporaryRegistration
            if (r3 == 0) goto L_0x051f
            int r0 = org.telegram.messenger.R.string.PassportAddTemporaryRegistrationInfo
            java.lang.String r1 = "PassportAddTemporaryRegistrationInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x051f:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeBankStatement
            if (r1 == 0) goto L_0x055b
            int r0 = org.telegram.messenger.R.string.PassportAddBankInfo
            java.lang.String r1 = "PassportAddBankInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x052c:
            int r0 = org.telegram.messenger.R.string.PassportAddressInfo
            java.lang.String r1 = "PassportAddressInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x0535:
            r2 = 1
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
            if (r3 == 0) goto L_0x0549
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x055b
            int r0 = org.telegram.messenger.R.string.PassportPhoneInfo
            java.lang.String r1 = "PassportPhoneInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x055b
        L_0x0549:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
            if (r1 == 0) goto L_0x055b
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x055b
            int r0 = org.telegram.messenger.R.string.PassportEmailInfo
            java.lang.String r1 = "PassportEmailInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x055b:
            r1 = r25
        L_0x055d:
            r6 = 0
        L_0x055e:
            r1.setValue(r0)
            android.widget.TextView r0 = r1.valueTextView
            if (r6 == 0) goto L_0x056a
            java.lang.String r3 = "windowBackgroundWhiteRedText3"
            goto L_0x056c
        L_0x056a:
            java.lang.String r3 = "windowBackgroundWhiteGrayText2"
        L_0x056c:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
            if (r6 != 0) goto L_0x0589
            int r0 = r7.currentActivityType
            r3 = 8
            if (r0 == r3) goto L_0x0589
            if (r36 == 0) goto L_0x057f
            if (r11 != 0) goto L_0x0583
        L_0x057f:
            if (r36 != 0) goto L_0x0589
            if (r19 == 0) goto L_0x0589
        L_0x0583:
            if (r11 == 0) goto L_0x0587
            if (r23 == 0) goto L_0x0589
        L_0x0587:
            r6 = 1
            goto L_0x058a
        L_0x0589:
            r6 = 0
        L_0x058a:
            r1.setChecked(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.setTypeValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, boolean, int):void");
    }

    /* access modifiers changed from: private */
    public void checkNativeFields(boolean z) {
        String str;
        EditTextBoldCursor[] editTextBoldCursorArr;
        if (this.inputExtraFields != null) {
            String str2 = this.languageMap.get(this.currentResidence);
            String str3 = SharedConfig.getCountryLangs().get(this.currentResidence);
            int i = 0;
            if (this.currentType.native_names && !TextUtils.isEmpty(this.currentResidence) && !"EN".equals(str3)) {
                if (this.nativeInfoCell.getVisibility() != 0) {
                    this.nativeInfoCell.setVisibility(0);
                    this.headerCell.setVisibility(0);
                    this.extraBackgroundView2.setVisibility(0);
                    int i2 = 0;
                    while (true) {
                        editTextBoldCursorArr = this.inputExtraFields;
                        if (i2 >= editTextBoldCursorArr.length) {
                            break;
                        }
                        ((View) editTextBoldCursorArr[i2].getParent()).setVisibility(0);
                        i2++;
                    }
                    if (editTextBoldCursorArr[0].length() == 0 && this.inputExtraFields[1].length() == 0 && this.inputExtraFields[2].length() == 0) {
                        int i3 = 0;
                        while (true) {
                            boolean[] zArr = this.nonLatinNames;
                            if (i3 >= zArr.length) {
                                break;
                            } else if (zArr[i3]) {
                                this.inputExtraFields[0].setText(this.inputFields[0].getText());
                                this.inputExtraFields[1].setText(this.inputFields[1].getText());
                                this.inputExtraFields[2].setText(this.inputFields[2].getText());
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), R.drawable.greydivider, "windowBackgroundGrayShadow"));
                }
                this.nativeInfoCell.setText(LocaleController.formatString("PassportNativeInfo", R.string.PassportNativeInfo, str2));
                if (str3 != null) {
                    str = LocaleController.getServerString("PassportLanguage_" + str3);
                } else {
                    str = null;
                }
                if (str != null) {
                    this.headerCell.setText(LocaleController.formatString("PassportNativeHeaderLang", R.string.PassportNativeHeaderLang, str));
                } else {
                    this.headerCell.setText(LocaleController.getString("PassportNativeHeader", R.string.PassportNativeHeader));
                }
                for (int i4 = 0; i4 < 3; i4++) {
                    if (i4 != 0) {
                        if (i4 != 1) {
                            if (i4 == 2) {
                                if (str != null) {
                                    this.inputExtraFields[i4].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                                } else {
                                    this.inputExtraFields[i4].setHintText(LocaleController.formatString("PassportSurnameCountry", R.string.PassportSurnameCountry, str2));
                                }
                            }
                        } else if (str != null) {
                            this.inputExtraFields[i4].setHintText(LocaleController.getString("PassportMidname", R.string.PassportMidname));
                        } else {
                            this.inputExtraFields[i4].setHintText(LocaleController.formatString("PassportMidnameCountry", R.string.PassportMidnameCountry, str2));
                        }
                    } else if (str != null) {
                        this.inputExtraFields[i4].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                    } else {
                        this.inputExtraFields[i4].setHintText(LocaleController.formatString("PassportNameCountry", R.string.PassportNameCountry, str2));
                    }
                }
                if (z) {
                    AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda52(this));
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
                if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), R.drawable.greydivider, "windowBackgroundGrayShadow"));
                } else {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkNativeFields$58() {
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (editTextBoldCursorArr != null) {
            scrollToField(editTextBoldCursorArr[0]);
        }
    }

    private TLRPC$TL_secureValue getValueByType(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, boolean z) {
        if (tLRPC$TL_secureRequiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$TL_secureValue tLRPC$TL_secureValue = this.currentForm.values.get(i2);
            if (tLRPC$TL_secureRequiredType.type.getClass() == tLRPC$TL_secureValue.type.getClass()) {
                if (z) {
                    if (tLRPC$TL_secureRequiredType.selfie_required && !(tLRPC$TL_secureValue.selfie instanceof TLRPC$TL_secureFile)) {
                        return null;
                    }
                    if (tLRPC$TL_secureRequiredType.translation_required && tLRPC$TL_secureValue.translation.isEmpty()) {
                        return null;
                    }
                    if (isAddressDocument(tLRPC$TL_secureRequiredType.type) && tLRPC$TL_secureValue.files.isEmpty()) {
                        return null;
                    }
                    if (isPersonalDocument(tLRPC$TL_secureRequiredType.type) && !(tLRPC$TL_secureValue.front_side instanceof TLRPC$TL_secureFile)) {
                        return null;
                    }
                    TLRPC$SecureValueType tLRPC$SecureValueType = tLRPC$TL_secureRequiredType.type;
                    if (((tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeDriverLicense) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeIdentityCard)) && !(tLRPC$TL_secureValue.reverse_side instanceof TLRPC$TL_secureFile)) {
                        return null;
                    }
                    if ((tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails) || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeAddress)) {
                        String[] strArr = tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails ? tLRPC$TL_secureRequiredType.native_names ? new String[]{"first_name_native", "last_name_native", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"first_name", "last_name", "birth_date", "gender", "country_code", "residence_country_code"} : new String[]{"street_line1", "street_line2", "post_code", "city", "state", "country_code"};
                        try {
                            TLRPC$TL_secureData tLRPC$TL_secureData = tLRPC$TL_secureValue.data;
                            JSONObject jSONObject = new JSONObject(decryptData(tLRPC$TL_secureData.data, decryptValueSecret(tLRPC$TL_secureData.secret, tLRPC$TL_secureData.data_hash), tLRPC$TL_secureValue.data.data_hash));
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
                return tLRPC$TL_secureValue;
            }
        }
        return null;
    }

    private void openTypeActivity(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, ArrayList<TLRPC$TL_secureRequiredType> arrayList, boolean z) {
        int i;
        TLRPC$TL_account_password tLRPC$TL_account_password;
        HashMap hashMap;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType3 = tLRPC$TL_secureRequiredType;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType4 = tLRPC$TL_secureRequiredType2;
        final boolean z2 = z;
        final int size = arrayList != null ? arrayList.size() : 0;
        final TLRPC$SecureValueType tLRPC$SecureValueType = tLRPC$TL_secureRequiredType3.type;
        TLRPC$SecureValueType tLRPC$SecureValueType2 = tLRPC$TL_secureRequiredType4 != null ? tLRPC$TL_secureRequiredType4.type : null;
        if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails) {
            i = 1;
        } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeAddress) {
            i = 2;
        } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePhone) {
            i = 3;
        } else {
            i = tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeEmail ? 4 : -1;
        }
        if (i != -1) {
            HashMap<String, String> hashMap2 = !z2 ? this.errorsMap.get(getNameForType(tLRPC$SecureValueType)) : null;
            HashMap<String, String> hashMap3 = this.errorsMap.get(getNameForType(tLRPC$SecureValueType2));
            TLRPC$TL_secureValue valueByType = getValueByType(tLRPC$TL_secureRequiredType3, false);
            TLRPC$TL_secureValue valueByType2 = getValueByType(tLRPC$TL_secureRequiredType4, false);
            TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm = this.currentForm;
            TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
            HashMap hashMap4 = this.typesValues.get(tLRPC$TL_secureRequiredType3);
            if (tLRPC$TL_secureRequiredType4 != null) {
                tLRPC$TL_account_password = tLRPC$TL_account_password2;
                hashMap = this.typesValues.get(tLRPC$TL_secureRequiredType4);
            } else {
                tLRPC$TL_account_password = tLRPC$TL_account_password2;
                hashMap = null;
            }
            PassportActivity passportActivity = r1;
            int i2 = i;
            PassportActivity passportActivity2 = new PassportActivity(i, tLRPC$TL_account_authorizationForm, tLRPC$TL_account_password, tLRPC$TL_secureRequiredType, valueByType, tLRPC$TL_secureRequiredType2, valueByType2, (HashMap<String, String>) hashMap4, (HashMap<String, String>) hashMap);
            passportActivity.delegate = new PassportActivityDelegate() {
                private TLRPC$InputSecureFile getInputSecureFile(SecureDocument secureDocument) {
                    if (secureDocument.inputFile != null) {
                        TLRPC$TL_inputSecureFileUploaded tLRPC$TL_inputSecureFileUploaded = new TLRPC$TL_inputSecureFileUploaded();
                        TLRPC$TL_inputFile tLRPC$TL_inputFile = secureDocument.inputFile;
                        tLRPC$TL_inputSecureFileUploaded.id = tLRPC$TL_inputFile.id;
                        tLRPC$TL_inputSecureFileUploaded.parts = tLRPC$TL_inputFile.parts;
                        tLRPC$TL_inputSecureFileUploaded.md5_checksum = tLRPC$TL_inputFile.md5_checksum;
                        tLRPC$TL_inputSecureFileUploaded.file_hash = secureDocument.fileHash;
                        tLRPC$TL_inputSecureFileUploaded.secret = secureDocument.fileSecret;
                        return tLRPC$TL_inputSecureFileUploaded;
                    }
                    TLRPC$TL_inputSecureFile tLRPC$TL_inputSecureFile = new TLRPC$TL_inputSecureFile();
                    TLRPC$TL_secureFile tLRPC$TL_secureFile = secureDocument.secureFile;
                    tLRPC$TL_inputSecureFile.id = tLRPC$TL_secureFile.id;
                    tLRPC$TL_inputSecureFile.access_hash = tLRPC$TL_secureFile.access_hash;
                    return tLRPC$TL_inputSecureFile;
                }

                /* access modifiers changed from: private */
                public void renameFile(SecureDocument secureDocument, TLRPC$TL_secureFile tLRPC$TL_secureFile) {
                    File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(secureDocument);
                    File pathToAttach2 = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tLRPC$TL_secureFile);
                    pathToAttach.renameTo(pathToAttach2);
                    ImageLoader.getInstance().replaceImageInCache(secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id, tLRPC$TL_secureFile.dc_id + "_" + tLRPC$TL_secureFile.id, (ImageLocation) null, false);
                }

                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.tgnet.TLRPC$TL_securePlainPhone} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.telegram.tgnet.TLRPC$TL_securePlainEmail} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: org.telegram.tgnet.TLRPC$TL_securePlainPhone} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: org.telegram.tgnet.TLRPC$TL_securePlainPhone} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void saveValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType r22, java.lang.String r23, java.lang.String r24, org.telegram.tgnet.TLRPC$TL_secureRequiredType r25, java.lang.String r26, java.util.ArrayList<org.telegram.messenger.SecureDocument> r27, org.telegram.messenger.SecureDocument r28, java.util.ArrayList<org.telegram.messenger.SecureDocument> r29, org.telegram.messenger.SecureDocument r30, org.telegram.messenger.SecureDocument r31, java.lang.Runnable r32, org.telegram.ui.PassportActivity.ErrorRunnable r33) {
                    /*
                        r21 = this;
                        r15 = r21
                        r6 = r22
                        r3 = r23
                        r5 = r25
                        r7 = r27
                        r8 = r28
                        r11 = r29
                        r9 = r30
                        r10 = r31
                        r2 = r33
                        boolean r0 = android.text.TextUtils.isEmpty(r24)
                        r1 = 0
                        if (r0 != 0) goto L_0x0048
                        org.telegram.tgnet.TLRPC$TL_inputSecureValue r0 = new org.telegram.tgnet.TLRPC$TL_inputSecureValue
                        r0.<init>()
                        org.telegram.tgnet.TLRPC$SecureValueType r4 = r6.type
                        r0.type = r4
                        int r4 = r0.flags
                        r4 = r4 | 1
                        r0.flags = r4
                        org.telegram.ui.PassportActivity r4 = org.telegram.ui.PassportActivity.this
                        byte[] r12 = org.telegram.messenger.AndroidUtilities.getStringBytes(r24)
                        org.telegram.ui.PassportActivity$EncryptionResult r4 = r4.encryptData(r12)
                        org.telegram.tgnet.TLRPC$TL_secureData r12 = new org.telegram.tgnet.TLRPC$TL_secureData
                        r12.<init>()
                        r0.data = r12
                        byte[] r13 = r4.encryptedData
                        r12.data = r13
                        byte[] r13 = r4.fileHash
                        r12.data_hash = r13
                        byte[] r4 = r4.fileSecret
                        r12.secret = r4
                        goto L_0x007c
                    L_0x0048:
                        boolean r0 = android.text.TextUtils.isEmpty(r23)
                        if (r0 != 0) goto L_0x007b
                        org.telegram.tgnet.TLRPC$SecureValueType r0 = r14
                        boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
                        if (r4 == 0) goto L_0x005c
                        org.telegram.tgnet.TLRPC$TL_securePlainEmail r0 = new org.telegram.tgnet.TLRPC$TL_securePlainEmail
                        r0.<init>()
                        r0.email = r3
                        goto L_0x0067
                    L_0x005c:
                        boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
                        if (r0 == 0) goto L_0x007a
                        org.telegram.tgnet.TLRPC$TL_securePlainPhone r0 = new org.telegram.tgnet.TLRPC$TL_securePlainPhone
                        r0.<init>()
                        r0.phone = r3
                    L_0x0067:
                        org.telegram.tgnet.TLRPC$TL_inputSecureValue r4 = new org.telegram.tgnet.TLRPC$TL_inputSecureValue
                        r4.<init>()
                        org.telegram.tgnet.TLRPC$SecureValueType r12 = r6.type
                        r4.type = r12
                        int r12 = r4.flags
                        r12 = r12 | 32
                        r4.flags = r12
                        r4.plain_data = r0
                        r0 = r4
                        goto L_0x007c
                    L_0x007a:
                        return
                    L_0x007b:
                        r0 = r1
                    L_0x007c:
                        boolean r4 = r12
                        if (r4 != 0) goto L_0x0088
                        if (r0 != 0) goto L_0x0088
                        if (r2 == 0) goto L_0x0087
                        r2.onError(r1, r1)
                    L_0x0087:
                        return
                    L_0x0088:
                        if (r5 == 0) goto L_0x0140
                        org.telegram.tgnet.TLRPC$TL_inputSecureValue r4 = new org.telegram.tgnet.TLRPC$TL_inputSecureValue
                        r4.<init>()
                        org.telegram.tgnet.TLRPC$SecureValueType r12 = r5.type
                        r4.type = r12
                        boolean r12 = android.text.TextUtils.isEmpty(r26)
                        if (r12 != 0) goto L_0x00bc
                        int r12 = r4.flags
                        r12 = r12 | 1
                        r4.flags = r12
                        org.telegram.ui.PassportActivity r12 = org.telegram.ui.PassportActivity.this
                        byte[] r13 = org.telegram.messenger.AndroidUtilities.getStringBytes(r26)
                        org.telegram.ui.PassportActivity$EncryptionResult r12 = r12.encryptData(r13)
                        org.telegram.tgnet.TLRPC$TL_secureData r13 = new org.telegram.tgnet.TLRPC$TL_secureData
                        r13.<init>()
                        r4.data = r13
                        byte[] r14 = r12.encryptedData
                        r13.data = r14
                        byte[] r14 = r12.fileHash
                        r13.data_hash = r14
                        byte[] r12 = r12.fileSecret
                        r13.secret = r12
                    L_0x00bc:
                        if (r9 == 0) goto L_0x00ca
                        org.telegram.tgnet.TLRPC$InputSecureFile r12 = r15.getInputSecureFile(r9)
                        r4.front_side = r12
                        int r12 = r4.flags
                        r12 = r12 | 2
                        r4.flags = r12
                    L_0x00ca:
                        if (r10 == 0) goto L_0x00d8
                        org.telegram.tgnet.TLRPC$InputSecureFile r12 = r15.getInputSecureFile(r10)
                        r4.reverse_side = r12
                        int r12 = r4.flags
                        r12 = r12 | 4
                        r4.flags = r12
                    L_0x00d8:
                        if (r8 == 0) goto L_0x00e6
                        org.telegram.tgnet.TLRPC$InputSecureFile r12 = r15.getInputSecureFile(r8)
                        r4.selfie = r12
                        int r12 = r4.flags
                        r12 = r12 | 8
                        r4.flags = r12
                    L_0x00e6:
                        if (r11 == 0) goto L_0x0110
                        boolean r13 = r29.isEmpty()
                        if (r13 != 0) goto L_0x0110
                        int r13 = r4.flags
                        r13 = r13 | 64
                        r4.flags = r13
                        int r13 = r29.size()
                        r14 = 0
                    L_0x00f9:
                        if (r14 >= r13) goto L_0x0110
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$InputSecureFile> r1 = r4.translation
                        java.lang.Object r17 = r11.get(r14)
                        r12 = r17
                        org.telegram.messenger.SecureDocument r12 = (org.telegram.messenger.SecureDocument) r12
                        org.telegram.tgnet.TLRPC$InputSecureFile r12 = r15.getInputSecureFile(r12)
                        r1.add(r12)
                        int r14 = r14 + 1
                        r1 = 0
                        goto L_0x00f9
                    L_0x0110:
                        if (r7 == 0) goto L_0x0137
                        boolean r1 = r27.isEmpty()
                        if (r1 != 0) goto L_0x0137
                        int r1 = r4.flags
                        r1 = r1 | 16
                        r4.flags = r1
                        int r1 = r27.size()
                        r12 = 0
                    L_0x0123:
                        if (r12 >= r1) goto L_0x0137
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$InputSecureFile> r13 = r4.files
                        java.lang.Object r14 = r7.get(r12)
                        org.telegram.messenger.SecureDocument r14 = (org.telegram.messenger.SecureDocument) r14
                        org.telegram.tgnet.TLRPC$InputSecureFile r14 = r15.getInputSecureFile(r14)
                        r13.add(r14)
                        int r12 = r12 + 1
                        goto L_0x0123
                    L_0x0137:
                        boolean r1 = r12
                        if (r1 == 0) goto L_0x013d
                        r0 = r4
                        goto L_0x0140
                    L_0x013d:
                        r16 = r4
                        goto L_0x0142
                    L_0x0140:
                        r16 = 0
                    L_0x0142:
                        org.telegram.tgnet.TLRPC$TL_account_saveSecureValue r14 = new org.telegram.tgnet.TLRPC$TL_account_saveSecureValue
                        r4 = r14
                        r14.<init>()
                        r14.value = r0
                        org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                        long r0 = r0.secureSecretId
                        r14.secure_secret_id = r0
                        org.telegram.ui.PassportActivity r0 = org.telegram.ui.PassportActivity.this
                        int r0 = r0.currentAccount
                        org.telegram.tgnet.ConnectionsManager r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
                        org.telegram.ui.PassportActivity$20$1 r12 = new org.telegram.ui.PassportActivity$20$1
                        r0 = r12
                        r1 = r21
                        r2 = r33
                        r3 = r23
                        r5 = r25
                        r6 = r22
                        r7 = r27
                        r8 = r28
                        r9 = r30
                        r10 = r31
                        r11 = r29
                        r18 = r12
                        r12 = r24
                        r19 = r13
                        r13 = r26
                        r20 = r14
                        r14 = r32
                        r15 = r21
                        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
                        r2 = r18
                        r1 = r19
                        r0 = r20
                        r1.sendRequest(r0, r2)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.AnonymousClass20.saveValue(org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.util.ArrayList, org.telegram.messenger.SecureDocument, java.util.ArrayList, org.telegram.messenger.SecureDocument, org.telegram.messenger.SecureDocument, java.lang.Runnable, org.telegram.ui.PassportActivity$ErrorRunnable):void");
                }

                public SecureDocument saveFile(TLRPC$TL_secureFile tLRPC$TL_secureFile) {
                    String str = FileLoader.getDirectory(4) + "/" + tLRPC$TL_secureFile.dc_id + "_" + tLRPC$TL_secureFile.id + ".jpg";
                    EncryptionResult access$8400 = PassportActivity.this.createSecureDocument(str);
                    return new SecureDocument(access$8400.secureDocumentKey, tLRPC$TL_secureFile, str, access$8400.fileHash, access$8400.fileSecret);
                }

                public void deleteValue(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, ArrayList<TLRPC$TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(tLRPC$TL_secureRequiredType, tLRPC$TL_secureRequiredType2, arrayList, z, runnable, errorRunnable, z2);
                }
            };
            passportActivity.currentAccount = this.currentAccount;
            passportActivity.saltedPassword = this.saltedPassword;
            passportActivity.secureSecret = this.secureSecret;
            passportActivity.currentBotId = this.currentBotId;
            passportActivity.fieldsErrors = hashMap2;
            passportActivity.documentOnly = z2;
            passportActivity.documentsErrors = hashMap3;
            passportActivity.availableDocumentTypes = arrayList;
            if (i2 == 4) {
                passportActivity.currentEmail = this.currentEmail;
            }
            presentFragment(passportActivity);
        }
    }

    /* access modifiers changed from: private */
    public TLRPC$TL_secureValue removeValue(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType) {
        if (tLRPC$TL_secureRequiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int i = 0; i < size; i++) {
            if (tLRPC$TL_secureRequiredType.type.getClass() == this.currentForm.values.get(i).type.getClass()) {
                return this.currentForm.values.remove(i);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void deleteValueInternal(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, ArrayList<TLRPC$TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable, boolean z2) {
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType3 = tLRPC$TL_secureRequiredType;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType4 = tLRPC$TL_secureRequiredType2;
        if (tLRPC$TL_secureRequiredType3 != null) {
            TLRPC$TL_account_deleteSecureValue tLRPC$TL_account_deleteSecureValue = new TLRPC$TL_account_deleteSecureValue();
            if (!z2 || tLRPC$TL_secureRequiredType4 == null) {
                if (z) {
                    tLRPC$TL_account_deleteSecureValue.types.add(tLRPC$TL_secureRequiredType3.type);
                }
                if (tLRPC$TL_secureRequiredType4 != null) {
                    tLRPC$TL_account_deleteSecureValue.types.add(tLRPC$TL_secureRequiredType4.type);
                }
            } else {
                tLRPC$TL_account_deleteSecureValue.types.add(tLRPC$TL_secureRequiredType4.type);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_deleteSecureValue, new PassportActivity$$ExternalSyntheticLambda67(this, errorRunnable, z2, tLRPC$TL_secureRequiredType2, tLRPC$TL_secureRequiredType, z, arrayList, runnable));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteValueInternal$60(ErrorRunnable errorRunnable, boolean z, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda60(this, tLRPC$TL_error, errorRunnable, z, tLRPC$TL_secureRequiredType, tLRPC$TL_secureRequiredType2, z2, arrayList, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteValueInternal$59(TLRPC$TL_error tLRPC$TL_error, ErrorRunnable errorRunnable, boolean z, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        String str;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType3;
        TLRPC$TL_secureData tLRPC$TL_secureData;
        String str2;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        ErrorRunnable errorRunnable2 = errorRunnable;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType4 = tLRPC$TL_secureRequiredType;
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType5 = tLRPC$TL_secureRequiredType2;
        ArrayList arrayList2 = arrayList;
        String str3 = null;
        if (tLRPC$TL_error2 != null) {
            if (errorRunnable2 != null) {
                errorRunnable.onError(tLRPC$TL_error2.text, (String) null);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), tLRPC$TL_error2.text);
            return;
        }
        if (!z) {
            if (z2) {
                removeValue(tLRPC$TL_secureRequiredType5);
            }
            removeValue(tLRPC$TL_secureRequiredType);
        } else if (tLRPC$TL_secureRequiredType4 != null) {
            removeValue(tLRPC$TL_secureRequiredType);
        } else {
            removeValue(tLRPC$TL_secureRequiredType5);
        }
        if (this.currentActivityType == 8) {
            TextDetailSecureCell remove = this.typesViews.remove(tLRPC$TL_secureRequiredType5);
            if (remove != null) {
                this.linearLayout2.removeView(remove);
                LinearLayout linearLayout = this.linearLayout2;
                View childAt = linearLayout.getChildAt(linearLayout.getChildCount() - 6);
                if (childAt instanceof TextDetailSecureCell) {
                    ((TextDetailSecureCell) childAt).setNeedDivider(false);
                }
            }
            updateManageVisibility();
        } else {
            if (tLRPC$TL_secureRequiredType4 == null || arrayList2 == null || arrayList.size() <= 1) {
                tLRPC$TL_secureRequiredType3 = tLRPC$TL_secureRequiredType4;
                str = null;
            } else {
                int size = arrayList.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        tLRPC$TL_secureRequiredType3 = tLRPC$TL_secureRequiredType4;
                        break;
                    }
                    tLRPC$TL_secureRequiredType3 = (TLRPC$TL_secureRequiredType) arrayList2.get(i);
                    TLRPC$TL_secureValue valueByType = getValueByType(tLRPC$TL_secureRequiredType3, false);
                    if (valueByType != null) {
                        TLRPC$TL_secureData tLRPC$TL_secureData2 = valueByType.data;
                        if (tLRPC$TL_secureData2 != null) {
                            str2 = decryptData(tLRPC$TL_secureData2.data, decryptValueSecret(tLRPC$TL_secureData2.secret, tLRPC$TL_secureData2.data_hash), valueByType.data.data_hash);
                        }
                    } else {
                        i++;
                    }
                }
                str2 = null;
                if (tLRPC$TL_secureRequiredType3 == null) {
                    str = str2;
                    tLRPC$TL_secureRequiredType3 = (TLRPC$TL_secureRequiredType) arrayList2.get(0);
                } else {
                    str = str2;
                }
            }
            if (z2) {
                setTypeValue(tLRPC$TL_secureRequiredType2, (String) null, (String) null, tLRPC$TL_secureRequiredType3, str, z, arrayList2 != null ? arrayList.size() : 0);
            } else {
                TLRPC$TL_secureValue valueByType2 = getValueByType(tLRPC$TL_secureRequiredType5, false);
                if (!(valueByType2 == null || (tLRPC$TL_secureData = valueByType2.data) == null)) {
                    str3 = decryptData(tLRPC$TL_secureData.data, decryptValueSecret(tLRPC$TL_secureData.secret, tLRPC$TL_secureData.data_hash), valueByType2.data.data_hash);
                }
                setTypeValue(tLRPC$TL_secureRequiredType2, (String) null, str3, tLRPC$TL_secureRequiredType3, str, z, arrayList2 != null ? arrayList.size() : 0);
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x018f  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.ui.PassportActivity.TextDetailSecureCell addField(android.content.Context r17, org.telegram.tgnet.TLRPC$TL_secureRequiredType r18, java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_secureRequiredType> r19, boolean r20, boolean r21) {
        /*
            r16 = this;
            r8 = r16
            r1 = r18
            r0 = r19
            r6 = r20
            r2 = 0
            if (r0 == 0) goto L_0x0011
            int r3 = r19.size()
            r7 = r3
            goto L_0x0012
        L_0x0011:
            r7 = 0
        L_0x0012:
            org.telegram.ui.PassportActivity$TextDetailSecureCell r9 = new org.telegram.ui.PassportActivity$TextDetailSecureCell
            r3 = r17
            r9.<init>(r8, r3)
            r3 = 1
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r3)
            r9.setBackgroundDrawable(r4)
            org.telegram.tgnet.TLRPC$SecureValueType r4 = r1.type
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            java.lang.String r10 = "PassportTwoDocuments"
            r11 = 2
            java.lang.String r12 = ""
            if (r5 == 0) goto L_0x008f
            if (r0 == 0) goto L_0x0080
            boolean r4 = r19.isEmpty()
            if (r4 == 0) goto L_0x0035
            goto L_0x0080
        L_0x0035:
            if (r6 == 0) goto L_0x004a
            int r4 = r19.size()
            if (r4 != r3) goto L_0x004a
            java.lang.Object r4 = r0.get(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r4 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r4
            org.telegram.tgnet.TLRPC$SecureValueType r4 = r4.type
            java.lang.String r4 = r8.getTextForType(r4)
            goto L_0x0088
        L_0x004a:
            if (r6 == 0) goto L_0x0077
            int r4 = r19.size()
            if (r4 != r11) goto L_0x0077
            int r4 = org.telegram.messenger.R.string.PassportTwoDocuments
            java.lang.Object[] r5 = new java.lang.Object[r11]
            java.lang.Object r11 = r0.get(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r11 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r11
            org.telegram.tgnet.TLRPC$SecureValueType r11 = r11.type
            java.lang.String r11 = r8.getTextForType(r11)
            r5[r2] = r11
            java.lang.Object r11 = r0.get(r3)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r11 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r11
            org.telegram.tgnet.TLRPC$SecureValueType r11 = r11.type
            java.lang.String r11 = r8.getTextForType(r11)
            r5[r3] = r11
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r10, r4, r5)
            goto L_0x0088
        L_0x0077:
            int r4 = org.telegram.messenger.R.string.PassportIdentityDocument
            java.lang.String r5 = "PassportIdentityDocument"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x0088
        L_0x0080:
            int r4 = org.telegram.messenger.R.string.PassportPersonalDetails
            java.lang.String r5 = "PassportPersonalDetails"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
        L_0x0088:
            r5 = r21 ^ 1
            r9.setTextAndValue(r4, r12, r5)
            goto L_0x0118
        L_0x008f:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r5 == 0) goto L_0x00f5
            if (r0 == 0) goto L_0x00e7
            boolean r4 = r19.isEmpty()
            if (r4 == 0) goto L_0x009c
            goto L_0x00e7
        L_0x009c:
            if (r6 == 0) goto L_0x00b1
            int r4 = r19.size()
            if (r4 != r3) goto L_0x00b1
            java.lang.Object r4 = r0.get(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r4 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r4
            org.telegram.tgnet.TLRPC$SecureValueType r4 = r4.type
            java.lang.String r4 = r8.getTextForType(r4)
            goto L_0x00ef
        L_0x00b1:
            if (r6 == 0) goto L_0x00de
            int r4 = r19.size()
            if (r4 != r11) goto L_0x00de
            int r4 = org.telegram.messenger.R.string.PassportTwoDocuments
            java.lang.Object[] r5 = new java.lang.Object[r11]
            java.lang.Object r11 = r0.get(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r11 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r11
            org.telegram.tgnet.TLRPC$SecureValueType r11 = r11.type
            java.lang.String r11 = r8.getTextForType(r11)
            r5[r2] = r11
            java.lang.Object r11 = r0.get(r3)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r11 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r11
            org.telegram.tgnet.TLRPC$SecureValueType r11 = r11.type
            java.lang.String r11 = r8.getTextForType(r11)
            r5[r3] = r11
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r10, r4, r5)
            goto L_0x00ef
        L_0x00de:
            int r4 = org.telegram.messenger.R.string.PassportResidentialAddress
            java.lang.String r5 = "PassportResidentialAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            goto L_0x00ef
        L_0x00e7:
            int r4 = org.telegram.messenger.R.string.PassportAddress
            java.lang.String r5 = "PassportAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
        L_0x00ef:
            r5 = r21 ^ 1
            r9.setTextAndValue(r4, r12, r5)
            goto L_0x0118
        L_0x00f5:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
            if (r5 == 0) goto L_0x0107
            int r4 = org.telegram.messenger.R.string.PassportPhone
            java.lang.String r5 = "PassportPhone"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = r21 ^ 1
            r9.setTextAndValue(r4, r12, r5)
            goto L_0x0118
        L_0x0107:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
            if (r4 == 0) goto L_0x0118
            int r4 = org.telegram.messenger.R.string.PassportEmail
            java.lang.String r5 = "PassportEmail"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = r21 ^ 1
            r9.setTextAndValue(r4, r12, r5)
        L_0x0118:
            int r4 = r8.currentActivityType
            r5 = 8
            r10 = -2
            r11 = -1
            if (r4 != r5) goto L_0x0130
            android.widget.LinearLayout r4 = r8.linearLayout2
            int r5 = r4.getChildCount()
            int r5 = r5 + -5
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r4.addView(r9, r5, r10)
            goto L_0x0139
        L_0x0130:
            android.widget.LinearLayout r4 = r8.linearLayout2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r4.addView(r9, r5)
        L_0x0139:
            org.telegram.ui.PassportActivity$$ExternalSyntheticLambda30 r4 = new org.telegram.ui.PassportActivity$$ExternalSyntheticLambda30
            r4.<init>(r8, r0, r1, r6)
            r9.setOnClickListener(r4)
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.ui.PassportActivity$TextDetailSecureCell> r4 = r8.typesViews
            r4.put(r1, r9)
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r4 = r8.typesValues
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r4.put(r1, r5)
            org.telegram.tgnet.TLRPC$TL_secureValue r4 = r8.getValueByType(r1, r2)
            r5 = 0
            if (r4 == 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$SecurePlainData r10 = r4.plain_data
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_securePlainEmail
            if (r11 == 0) goto L_0x0163
            org.telegram.tgnet.TLRPC$TL_securePlainEmail r10 = (org.telegram.tgnet.TLRPC$TL_securePlainEmail) r10
            java.lang.String r4 = r10.email
        L_0x0161:
            r10 = r5
            goto L_0x0187
        L_0x0163:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_securePlainPhone
            if (r11 == 0) goto L_0x016c
            org.telegram.tgnet.TLRPC$TL_securePlainPhone r10 = (org.telegram.tgnet.TLRPC$TL_securePlainPhone) r10
            java.lang.String r4 = r10.phone
            goto L_0x0161
        L_0x016c:
            org.telegram.tgnet.TLRPC$TL_secureData r10 = r4.data
            if (r10 == 0) goto L_0x0185
            byte[] r11 = r10.data
            byte[] r12 = r10.secret
            byte[] r10 = r10.data_hash
            byte[] r10 = r8.decryptValueSecret(r12, r10)
            org.telegram.tgnet.TLRPC$TL_secureData r4 = r4.data
            byte[] r4 = r4.data_hash
            java.lang.String r4 = r8.decryptData(r11, r10, r4)
            r10 = r4
            r4 = r5
            goto L_0x0187
        L_0x0185:
            r4 = r5
            r10 = r4
        L_0x0187:
            if (r0 == 0) goto L_0x01de
            boolean r11 = r19.isEmpty()
            if (r11 != 0) goto L_0x01de
            int r11 = r19.size()
            r12 = r5
            r13 = 0
            r14 = 0
        L_0x0196:
            if (r13 >= r11) goto L_0x01d3
            java.lang.Object r15 = r0.get(r13)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r15 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r15
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r3 = r8.typesValues
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r3.put(r15, r2)
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_secureRequiredType, org.telegram.tgnet.TLRPC$TL_secureRequiredType> r2 = r8.documentsToTypesLink
            r2.put(r15, r1)
            if (r14 != 0) goto L_0x01ce
            r2 = 0
            org.telegram.tgnet.TLRPC$TL_secureValue r3 = r8.getValueByType(r15, r2)
            if (r3 == 0) goto L_0x01ce
            org.telegram.tgnet.TLRPC$TL_secureData r2 = r3.data
            if (r2 == 0) goto L_0x01cc
            byte[] r5 = r2.data
            byte[] r12 = r2.secret
            byte[] r2 = r2.data_hash
            byte[] r2 = r8.decryptValueSecret(r12, r2)
            org.telegram.tgnet.TLRPC$TL_secureData r3 = r3.data
            byte[] r3 = r3.data_hash
            java.lang.String r12 = r8.decryptData(r5, r2, r3)
        L_0x01cc:
            r5 = r15
            r14 = 1
        L_0x01ce:
            int r13 = r13 + 1
            r2 = 0
            r3 = 1
            goto L_0x0196
        L_0x01d3:
            if (r5 != 0) goto L_0x01df
            r2 = 0
            java.lang.Object r0 = r0.get(r2)
            org.telegram.tgnet.TLRPC$TL_secureRequiredType r0 = (org.telegram.tgnet.TLRPC$TL_secureRequiredType) r0
            r5 = r0
            goto L_0x01df
        L_0x01de:
            r12 = r5
        L_0x01df:
            r0 = r16
            r1 = r18
            r2 = r4
            r3 = r10
            r4 = r5
            r5 = r12
            r6 = r20
            r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.addField(android.content.Context, org.telegram.tgnet.TLRPC$TL_secureRequiredType, java.util.ArrayList, boolean, boolean):org.telegram.ui.PassportActivity$TextDetailSecureCell");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addField$64(ArrayList arrayList, TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, boolean z, View view) {
        TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2;
        String str;
        int i;
        if (arrayList != null) {
            int size = arrayList.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                }
                tLRPC$TL_secureRequiredType2 = (TLRPC$TL_secureRequiredType) arrayList.get(i2);
                if (getValueByType(tLRPC$TL_secureRequiredType2, false) != null || size == 1) {
                    break;
                }
                i2++;
            }
        }
        tLRPC$TL_secureRequiredType2 = null;
        TLRPC$SecureValueType tLRPC$SecureValueType = tLRPC$TL_secureRequiredType.type;
        if (!(tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails) && !(tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeAddress)) {
            boolean z2 = tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePhone;
            if ((z2 || (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeEmail)) && getValueByType(tLRPC$TL_secureRequiredType, false) != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PassportActivity$$ExternalSyntheticLambda9(this, tLRPC$TL_secureRequiredType, z));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (z2) {
                    i = R.string.PassportDeletePhoneAlert;
                    str = "PassportDeletePhoneAlert";
                } else {
                    i = R.string.PassportDeleteEmailAlert;
                    str = "PassportDeleteEmailAlert";
                }
                builder.setMessage(LocaleController.getString(str, i));
                showDialog(builder.create());
                return;
            }
        } else if (tLRPC$TL_secureRequiredType2 == null && arrayList != null && !arrayList.isEmpty()) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            TLRPC$SecureValueType tLRPC$SecureValueType2 = tLRPC$TL_secureRequiredType.type;
            if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypePersonalDetails) {
                builder2.setTitle(LocaleController.getString("PassportIdentityDocument", R.string.PassportIdentityDocument));
            } else if (tLRPC$SecureValueType2 instanceof TLRPC$TL_secureValueTypeAddress) {
                builder2.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
            }
            ArrayList arrayList2 = new ArrayList();
            int size2 = arrayList.size();
            for (int i3 = 0; i3 < size2; i3++) {
                TLRPC$SecureValueType tLRPC$SecureValueType3 = ((TLRPC$TL_secureRequiredType) arrayList.get(i3)).type;
                if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeDriverLicense) {
                    arrayList2.add(LocaleController.getString("PassportAddLicence", R.string.PassportAddLicence));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypePassport) {
                    arrayList2.add(LocaleController.getString("PassportAddPassport", R.string.PassportAddPassport));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeInternalPassport) {
                    arrayList2.add(LocaleController.getString("PassportAddInternalPassport", R.string.PassportAddInternalPassport));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeIdentityCard) {
                    arrayList2.add(LocaleController.getString("PassportAddCard", R.string.PassportAddCard));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeUtilityBill) {
                    arrayList2.add(LocaleController.getString("PassportAddBill", R.string.PassportAddBill));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeBankStatement) {
                    arrayList2.add(LocaleController.getString("PassportAddBank", R.string.PassportAddBank));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
                    arrayList2.add(LocaleController.getString("PassportAddAgreement", R.string.PassportAddAgreement));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
                    arrayList2.add(LocaleController.getString("PassportAddTemporaryRegistration", R.string.PassportAddTemporaryRegistration));
                } else if (tLRPC$SecureValueType3 instanceof TLRPC$TL_secureValueTypePassportRegistration) {
                    arrayList2.add(LocaleController.getString("PassportAddPassportRegistration", R.string.PassportAddPassportRegistration));
                }
            }
            builder2.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[0]), new PassportActivity$$ExternalSyntheticLambda8(this, tLRPC$TL_secureRequiredType, arrayList, z));
            showDialog(builder2.create());
            return;
        }
        openTypeActivity(tLRPC$TL_secureRequiredType, tLRPC$TL_secureRequiredType2, arrayList, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addField$61(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, ArrayList arrayList, boolean z, DialogInterface dialogInterface, int i) {
        openTypeActivity(tLRPC$TL_secureRequiredType, (TLRPC$TL_secureRequiredType) arrayList.get(i), arrayList, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addField$63(TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType, boolean z, DialogInterface dialogInterface, int i) {
        needShowProgress();
        deleteValueInternal(tLRPC$TL_secureRequiredType, (TLRPC$TL_secureRequiredType) null, (ArrayList<TLRPC$TL_secureRequiredType>) null, true, new PassportActivity$$ExternalSyntheticLambda47(this), new PassportActivity$$ExternalSyntheticLambda73(this), z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addField$62(String str, String str2) {
        needHideProgress();
    }

    private static class EncryptionResult {
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

    private SecureDocumentKey getSecureDocumentKey(byte[] bArr, byte[] bArr2) {
        byte[] computeSHA512 = Utilities.computeSHA512(decryptValueSecret(bArr, bArr2), bArr2);
        byte[] bArr3 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr4, 0, 16);
        return new SecureDocumentKey(bArr3, bArr4);
    }

    /* access modifiers changed from: private */
    public byte[] decryptSecret(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr.length != 32) {
            return null;
        }
        byte[] bArr3 = new byte[32];
        System.arraycopy(bArr2, 0, bArr3, 0, 32);
        byte[] bArr4 = new byte[16];
        System.arraycopy(bArr2, 32, bArr4, 0, 16);
        byte[] bArr5 = new byte[32];
        System.arraycopy(bArr, 0, bArr5, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, 32, 0, 0);
        return bArr5;
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
        Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, 32, 0, 0);
        if (!checkSecret(bArr5, (Long) null)) {
            return null;
        }
        byte[] computeSHA512 = Utilities.computeSHA512(bArr5, bArr2);
        byte[] bArr6 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr6, 0, 32);
        byte[] bArr7 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr7, 0, 16);
        byte[] bArr8 = new byte[32];
        System.arraycopy(bArr, 0, bArr8, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr8, bArr6, bArr7, 0, 32, 0, 0);
        return bArr8;
    }

    /* access modifiers changed from: private */
    public EncryptionResult createSecureDocument(String str) {
        RandomAccessFile randomAccessFile;
        byte[] bArr = new byte[((int) new File(str).length())];
        RandomAccessFile randomAccessFile2 = null;
        try {
            randomAccessFile = new RandomAccessFile(str, "rws");
            try {
                randomAccessFile.readFully(bArr);
            } catch (Exception unused) {
                randomAccessFile2 = randomAccessFile;
            }
        } catch (Exception unused2) {
            randomAccessFile = randomAccessFile2;
            EncryptionResult encryptData = encryptData(bArr);
            randomAccessFile.seek(0);
            randomAccessFile.write(encryptData.encryptedData);
            randomAccessFile.close();
            return encryptData;
        }
        EncryptionResult encryptData2 = encryptData(bArr);
        try {
            randomAccessFile.seek(0);
            randomAccessFile.write(encryptData2.encryptedData);
            randomAccessFile.close();
        } catch (Exception unused3) {
        }
        return encryptData2;
    }

    private String decryptData(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (bArr == null || bArr2 == null || bArr2.length != 32 || bArr3 == null || bArr3.length != 32) {
            return null;
        }
        byte[] computeSHA512 = Utilities.computeSHA512(bArr2, bArr3);
        byte[] bArr4 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr4, 0, 32);
        byte[] bArr5 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr5, 0, 16);
        int length = bArr.length;
        byte[] bArr6 = new byte[length];
        System.arraycopy(bArr, 0, bArr6, 0, bArr.length);
        Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, length, 0, 0);
        if (!Arrays.equals(Utilities.computeSHA256(bArr6), bArr3)) {
            return null;
        }
        byte b = bArr6[0] & 255;
        return new String(bArr6, b, length - b);
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

    /* access modifiers changed from: private */
    public byte[] getRandomSecret() {
        byte[] bArr = new byte[32];
        Utilities.random.nextBytes(bArr);
        int i = 0;
        for (int i2 = 0; i2 < 32; i2++) {
            i += 255 & bArr[i2];
        }
        int i3 = i % 255;
        if (i3 != 239) {
            int nextInt = Utilities.random.nextInt(32);
            int i4 = (bArr[nextInt] & 255) + (239 - i3);
            if (i4 < 255) {
                i4 += 255;
            }
            bArr[nextInt] = (byte) (i4 % 255);
        }
        return bArr;
    }

    /* access modifiers changed from: private */
    public EncryptionResult encryptData(byte[] bArr) {
        byte[] bArr2 = bArr;
        byte[] randomSecret = getRandomSecret();
        int nextInt = Utilities.random.nextInt(208) + 32;
        while ((bArr2.length + nextInt) % 16 != 0) {
            nextInt++;
        }
        byte[] bArr3 = new byte[nextInt];
        Utilities.random.nextBytes(bArr3);
        bArr3[0] = (byte) nextInt;
        int length = nextInt + bArr2.length;
        byte[] bArr4 = new byte[length];
        System.arraycopy(bArr3, 0, bArr4, 0, nextInt);
        System.arraycopy(bArr2, 0, bArr4, nextInt, bArr2.length);
        byte[] computeSHA256 = Utilities.computeSHA256(bArr4);
        byte[] computeSHA512 = Utilities.computeSHA512(randomSecret, computeSHA256);
        byte[] bArr5 = new byte[32];
        System.arraycopy(computeSHA512, 0, bArr5, 0, 32);
        byte[] bArr6 = new byte[16];
        System.arraycopy(computeSHA512, 32, bArr6, 0, 16);
        Utilities.aesCbcEncryptionByteArraySafe(bArr4, bArr5, bArr6, 0, length, 0, 1);
        byte[] bArr7 = new byte[32];
        System.arraycopy(this.saltedPassword, 0, bArr7, 0, 32);
        byte[] bArr8 = new byte[16];
        System.arraycopy(this.saltedPassword, 32, bArr8, 0, 16);
        byte[] bArr9 = new byte[32];
        System.arraycopy(this.secureSecret, 0, bArr9, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr9, bArr7, bArr8, 0, 32, 0, 0);
        byte[] computeSHA5122 = Utilities.computeSHA512(bArr9, computeSHA256);
        byte[] bArr10 = new byte[32];
        System.arraycopy(computeSHA5122, 0, bArr10, 0, 32);
        byte[] bArr11 = new byte[16];
        System.arraycopy(computeSHA5122, 32, bArr11, 0, 16);
        byte[] bArr12 = new byte[32];
        System.arraycopy(randomSecret, 0, bArr12, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(bArr12, bArr10, bArr11, 0, 32, 0, 1);
        return new EncryptionResult(bArr4, bArr12, randomSecret, computeSHA256, bArr5, bArr6);
    }

    /* access modifiers changed from: private */
    public void showAlertWithText(String str, String str2) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder.setTitle(str);
            builder.setMessage(str2);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void onPasscodeError(boolean z) {
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

    /* access modifiers changed from: private */
    public void startPhoneVerification(boolean z, String str, Runnable runnable, ErrorRunnable errorRunnable, PassportActivityDelegate passportActivityDelegate) {
        boolean z2;
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        boolean z3 = true;
        boolean z4 = (telephonyManager.getSimState() == 1 || telephonyManager.getPhoneType() == 0) ? false : true;
        if (getParentActivity() == null || Build.VERSION.SDK_INT < 23 || !z4) {
            z2 = true;
        } else {
            z2 = getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
            if (z) {
                this.permissionsItems.clear();
                if (!z2) {
                    this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                }
                if (!this.permissionsItems.isEmpty()) {
                    if (getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                        builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
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
        TLRPC$TL_account_sendVerifyPhoneCode tLRPC$TL_account_sendVerifyPhoneCode = new TLRPC$TL_account_sendVerifyPhoneCode();
        tLRPC$TL_account_sendVerifyPhoneCode.phone_number = str;
        TLRPC$TL_codeSettings tLRPC$TL_codeSettings = new TLRPC$TL_codeSettings();
        tLRPC$TL_account_sendVerifyPhoneCode.settings = tLRPC$TL_codeSettings;
        if (!z4 || !z2) {
            z3 = false;
        }
        tLRPC$TL_codeSettings.allow_flashcall = z3;
        tLRPC$TL_codeSettings.allow_app_hash = PushListenerController.GooglePushListenerServiceProvider.INSTANCE.hasServices();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (tLRPC$TL_account_sendVerifyPhoneCode.settings.allow_app_hash) {
            sharedPreferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
        } else {
            sharedPreferences.edit().remove("sms_hash").commit();
        }
        if (tLRPC$TL_account_sendVerifyPhoneCode.settings.allow_flashcall) {
            try {
                String line1Number = telephonyManager.getLine1Number();
                if (!TextUtils.isEmpty(line1Number)) {
                    tLRPC$TL_account_sendVerifyPhoneCode.settings.current_number = PhoneNumberUtils.compare(str, line1Number);
                    TLRPC$TL_codeSettings tLRPC$TL_codeSettings2 = tLRPC$TL_account_sendVerifyPhoneCode.settings;
                    if (!tLRPC$TL_codeSettings2.current_number) {
                        tLRPC$TL_codeSettings2.allow_flashcall = false;
                    }
                } else {
                    tLRPC$TL_account_sendVerifyPhoneCode.settings.current_number = false;
                }
            } catch (Exception e) {
                tLRPC$TL_account_sendVerifyPhoneCode.settings.allow_flashcall = false;
                FileLog.e((Throwable) e);
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_sendVerifyPhoneCode, new PassportActivity$$ExternalSyntheticLambda66(this, str, passportActivityDelegate, tLRPC$TL_account_sendVerifyPhoneCode), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startPhoneVerification$66(String str, PassportActivityDelegate passportActivityDelegate, TLRPC$TL_account_sendVerifyPhoneCode tLRPC$TL_account_sendVerifyPhoneCode, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda58(this, tLRPC$TL_error, str, passportActivityDelegate, tLObject, tLRPC$TL_account_sendVerifyPhoneCode));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startPhoneVerification$65(TLRPC$TL_error tLRPC$TL_error, String str, PassportActivityDelegate passportActivityDelegate, TLObject tLObject, TLRPC$TL_account_sendVerifyPhoneCode tLRPC$TL_account_sendVerifyPhoneCode) {
        String str2 = str;
        if (tLRPC$TL_error == null) {
            HashMap hashMap = new HashMap();
            hashMap.put("phone", str2);
            PassportActivity passportActivity = new PassportActivity(7, this.currentForm, this.currentPassword, this.currentType, (TLRPC$TL_secureValue) null, (TLRPC$TL_secureRequiredType) null, (TLRPC$TL_secureValue) null, (HashMap<String, String>) hashMap, (HashMap<String, String>) null);
            passportActivity.currentAccount = this.currentAccount;
            passportActivity.saltedPassword = this.saltedPassword;
            passportActivity.secureSecret = this.secureSecret;
            passportActivity.delegate = passportActivityDelegate;
            passportActivity.currentPhoneVerification = (TLRPC$TL_auth_sentCode) tLObject;
            presentFragment(passportActivity, true);
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_sendVerifyPhoneCode, str2);
    }

    /* access modifiers changed from: private */
    public void updatePasswordInterface() {
        ImageView imageView = this.noPasswordImageView;
        if (imageView != null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
            if (tLRPC$TL_account_password == null || this.usingSavedPassword != 0) {
                imageView.setVisibility(8);
                this.noPasswordTextView.setVisibility(8);
                this.noPasswordSetTextView.setVisibility(8);
                this.passwordAvatarContainer.setVisibility(8);
                this.inputFieldContainers[0].setVisibility(8);
                this.doneItem.setVisibility(8);
                this.passwordForgotButton.setVisibility(8);
                this.passwordInfoRequestTextView.setVisibility(8);
                this.passwordRequestTextView.setVisibility(8);
                this.emptyView.setVisibility(0);
            } else if (!tLRPC$TL_account_password.has_password) {
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
                if (this.inputFields != null) {
                    TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
                    if (tLRPC$TL_account_password2 == null || TextUtils.isEmpty(tLRPC$TL_account_password2.hint)) {
                        this.inputFields[0].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                    } else {
                        this.inputFields[0].setHint(this.currentPassword.hint);
                    }
                }
            }
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
            this.doneItemAnimation = new AnimatorSet();
            if (z3) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        if (!z3) {
                            PassportActivity.this.progressView.setVisibility(4);
                        } else {
                            PassportActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        AnimatorSet unused = PassportActivity.this.doneItemAnimation = null;
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
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{1.0f})});
            } else {
                this.acceptTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        if (!z3) {
                            PassportActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PassportActivity.this.acceptTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animator)) {
                        AnimatorSet unused = PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        SecureDocumentCell secureDocumentCell;
        ActionBarMenuItem actionBarMenuItem;
        if (i == NotificationCenter.fileUploaded) {
            String str = objArr[0];
            SecureDocument secureDocument = this.uploadingDocuments.get(str);
            if (secureDocument != null) {
                secureDocument.inputFile = objArr[1];
                this.uploadingDocuments.remove(str);
                if (this.uploadingDocuments.isEmpty() && (actionBarMenuItem = this.doneItem) != null) {
                    actionBarMenuItem.setEnabled(true);
                    this.doneItem.setAlpha(1.0f);
                }
                HashMap<SecureDocument, SecureDocumentCell> hashMap = this.documentsCells;
                if (!(hashMap == null || (secureDocumentCell = hashMap.get(secureDocument)) == null)) {
                    secureDocumentCell.updateButtonState(true);
                }
                HashMap<String, String> hashMap2 = this.errorsValues;
                if (hashMap2 != null && hashMap2.containsKey("error_document_all")) {
                    this.errorsValues.remove("error_document_all");
                    checkTopErrorCell(false);
                }
                int i3 = secureDocument.type;
                if (i3 == 0) {
                    if (this.bottomCell != null && !TextUtils.isEmpty(this.noAllDocumentsErrorText)) {
                        this.bottomCell.setText(this.noAllDocumentsErrorText);
                    }
                    this.errorsValues.remove("files_all");
                } else if (i3 == 4) {
                    if (this.bottomCellTranslation != null && !TextUtils.isEmpty(this.noAllTranslationErrorText)) {
                        this.bottomCellTranslation.setText(this.noAllTranslationErrorText);
                    }
                    this.errorsValues.remove("translation_all");
                }
            }
        } else if (i != NotificationCenter.fileUploadFailed) {
            if (i == NotificationCenter.twoStepPasswordChanged) {
                if (objArr == null || objArr.length <= 0) {
                    this.currentPassword = null;
                    loadPasswordInfo();
                } else {
                    if (objArr[7] != null) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                        if (editTextBoldCursorArr[0] != null) {
                            editTextBoldCursorArr[0].setText(objArr[7]);
                        }
                    }
                    if (objArr[6] == null) {
                        TLRPC$TL_account_password tLRPC$TL_account_password = new TLRPC$TL_account_password();
                        this.currentPassword = tLRPC$TL_account_password;
                        tLRPC$TL_account_password.current_algo = objArr[1];
                        tLRPC$TL_account_password.new_secure_algo = objArr[2];
                        tLRPC$TL_account_password.secure_random = objArr[3];
                        tLRPC$TL_account_password.has_recovery = !TextUtils.isEmpty(objArr[4]);
                        TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
                        tLRPC$TL_account_password2.hint = objArr[5];
                        tLRPC$TL_account_password2.srp_id = -1;
                        byte[] bArr = new byte[256];
                        tLRPC$TL_account_password2.srp_B = bArr;
                        Utilities.random.nextBytes(bArr);
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputFields;
                        if (editTextBoldCursorArr2[0] != null && editTextBoldCursorArr2[0].length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                }
                updatePasswordInterface();
                return;
            }
            int i4 = NotificationCenter.didRemoveTwoStepPassword;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda49(this));
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
        } else if ((i == 2 || i == 1) && Build.VERSION.SDK_INT >= 21) {
            createChatAttachView();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTransitionAnimationEnd$67() {
        presentFragment(this.presentAfterAnimation, true);
        this.presentAfterAnimation = null;
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 != -1) {
            return;
        }
        if (i == 0 || i == 2) {
            createChatAttachView();
            ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
            if (chatAttachAlert2 != null) {
                chatAttachAlert2.onActivityResultFragment(i, intent, this.currentPicturePath);
            }
            this.currentPicturePath = null;
        } else if (i != 1) {
        } else {
            if (intent == null || intent.getData() == null) {
                showAttachmentError();
                return;
            }
            ArrayList arrayList = new ArrayList();
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
            sendingMediaInfo.uri = intent.getData();
            arrayList.add(sendingMediaInfo);
            processSelectedFiles(arrayList);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlert chatAttachAlert2;
        TextSettingsCell textSettingsCell;
        int i2 = this.currentActivityType;
        if ((i2 == 1 || i2 == 2) && (chatAttachAlert2 = this.chatAttachAlert) != null) {
            if (i == 17) {
                chatAttachAlert2.getPhotoLayout().checkCamera(false);
            } else if (i == 21) {
                if (getParentActivity() != null && iArr != null && iArr.length != 0 && iArr[0] != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("PermissionNoAudioVideoWithHint", R.string.PermissionNoAudioVideoWithHint));
                    builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new PassportActivity$$ExternalSyntheticLambda4(this));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    builder.show();
                }
            } else if (i == 19 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
                processSelectedAttach(0);
            } else if (i == 22 && iArr != null && iArr.length > 0 && iArr[0] == 0 && (textSettingsCell = this.scanDocumentCell) != null) {
                textSettingsCell.callOnClick();
            }
        } else if (i2 == 3 && i == 6) {
            startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$68(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        String str = this.currentPicturePath;
        if (str != null) {
            bundle.putString("path", str);
        }
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
            return !checkDiscard();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType == 3 && Build.VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(slideView, "translationX", new float[]{(float) (-AndroidUtilities.displaySize.x)}), ObjectAnimator.ofFloat(slideView2, "translationX", new float[]{0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter(this) {
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

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle bundle, TLRPC$TL_auth_sentCode tLRPC$TL_auth_sentCode, boolean z) {
        bundle.putString("phoneHash", tLRPC$TL_auth_sentCode.phone_code_hash);
        TLRPC$auth_CodeType tLRPC$auth_CodeType = tLRPC$TL_auth_sentCode.next_type;
        if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (tLRPC$auth_CodeType instanceof TLRPC$TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        }
        if (tLRPC$TL_auth_sentCode.timeout == 0) {
            tLRPC$TL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tLRPC$TL_auth_sentCode.timeout * 1000);
        TLRPC$auth_SentCodeType tLRPC$auth_SentCodeType = tLRPC$TL_auth_sentCode.type;
        if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeCall) {
            bundle.putInt("type", 4);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(2, z, bundle);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt("type", 3);
            bundle.putString("pattern", tLRPC$TL_auth_sentCode.type.pattern);
            setPage(1, z, bundle);
        } else if (tLRPC$auth_SentCodeType instanceof TLRPC$TL_auth_sentCodeTypeSms) {
            bundle.putInt("type", 2);
            bundle.putInt("length", tLRPC$TL_auth_sentCode.type.length);
            setPage(0, z, bundle);
        }
    }

    private void openAttachMenu() {
        if (getParentActivity() != null) {
            boolean z = true;
            if (this.uploadingFileType != 0 || this.documents.size() < 20) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
                if (this.uploadingFileType != 1) {
                    z = false;
                }
                chatAttachAlert2.setOpenWithFrontFaceCamera(z);
                this.chatAttachAlert.setMaxSelectedPhotos(getMaxSelectedDocuments(), false);
                this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
                int i = Build.VERSION.SDK_INT;
                if (i == 21 || i == 22) {
                    AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
                }
                this.chatAttachAlert.init();
                showDialog(this.chatAttachAlert);
                return;
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", 20, new Object[0])));
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert2 = new ChatAttachAlert(getParentActivity(), this, false, false);
            this.chatAttachAlert = chatAttachAlert2;
            chatAttachAlert2.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
                public /* synthetic */ void didSelectBot(TLRPC$User tLRPC$User) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$didSelectBot(this, tLRPC$User);
                }

                public /* synthetic */ void doOnIdle(Runnable runnable) {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$doOnIdle(this, runnable);
                }

                public /* synthetic */ boolean needEnterComment() {
                    return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$needEnterComment(this);
                }

                public /* synthetic */ void openAvatarsSearch() {
                    ChatAttachAlert.ChatAttachViewDelegate.CC.$default$openAvatarsSearch(this);
                }

                public void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3) {
                    if (PassportActivity.this.getParentActivity() != null && PassportActivity.this.chatAttachAlert != null) {
                        if (i == 8 || i == 7) {
                            if (i != 8) {
                                PassportActivity.this.chatAttachAlert.dismiss(true);
                            }
                            HashMap<Object, Object> selectedPhotos = PassportActivity.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                            ArrayList<Object> selectedPhotosOrder = PassportActivity.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList arrayList = new ArrayList();
                                for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(i3));
                                    SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                                    String str = photoEntry.imagePath;
                                    if (str != null) {
                                        sendingMediaInfo.path = str;
                                    } else {
                                        sendingMediaInfo.path = photoEntry.path;
                                    }
                                    arrayList.add(sendingMediaInfo);
                                    photoEntry.reset();
                                }
                                PassportActivity.this.processSelectedFiles(arrayList);
                                return;
                            }
                            return;
                        }
                        if (PassportActivity.this.chatAttachAlert != null) {
                            PassportActivity.this.chatAttachAlert.dismissWithButtonClick(i);
                        }
                        PassportActivity.this.processSelectedAttach(i);
                    }
                }

                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(PassportActivity.this.fragmentView.findFocus());
                }
            });
        }
    }

    private int getMaxSelectedDocuments() {
        int size;
        int i = this.uploadingFileType;
        if (i == 0) {
            size = this.documents.size();
        } else if (i != 4) {
            return 1;
        } else {
            size = this.translationDocuments.size();
        }
        return 20 - size;
    }

    /* access modifiers changed from: private */
    public void processSelectedAttach(int i) {
        if (i == 0) {
            int i2 = Build.VERSION.SDK_INT;
            if (i2 < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        if (i2 >= 24) {
                            Activity parentActivity = getParentActivity();
                            intent.putExtra("output", FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                            intent.addFlags(2);
                            intent.addFlags(1);
                        } else {
                            intent.putExtra("output", Uri.fromFile(generatePicturePath));
                        }
                        this.currentPicturePath = generatePicturePath.getAbsolutePath();
                    }
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            }
        }
    }

    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        processSelectedFiles(arrayList);
    }

    public void startDocumentSelectActivity() {
        try {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            intent.setType("*/*");
            startActivityForResult(intent, 21);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void didSelectFiles(ArrayList<String> arrayList, String str, boolean z, int i) {
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
            sendingMediaInfo.path = arrayList.get(i2);
            arrayList2.add(sendingMediaInfo);
        }
        processSelectedFiles(arrayList2);
    }

    private void fillInitialValues() {
        if (this.initialValues == null) {
            this.initialValues = getCurrentValues();
        }
    }

    private String getCurrentValues() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (i >= editTextBoldCursorArr.length) {
                break;
            }
            sb.append(editTextBoldCursorArr[i].getText());
            sb.append(",");
            i++;
        }
        if (this.inputExtraFields != null) {
            int i2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                if (i2 >= editTextBoldCursorArr2.length) {
                    break;
                }
                sb.append(editTextBoldCursorArr2[i2].getText());
                sb.append(",");
                i2++;
            }
        }
        int size = this.documents.size();
        for (int i3 = 0; i3 < size; i3++) {
            sb.append(this.documents.get(i3).secureFile.id);
        }
        SecureDocument secureDocument = this.frontDocument;
        if (secureDocument != null) {
            sb.append(secureDocument.secureFile.id);
        }
        SecureDocument secureDocument2 = this.reverseDocument;
        if (secureDocument2 != null) {
            sb.append(secureDocument2.secureFile.id);
        }
        SecureDocument secureDocument3 = this.selfieDocument;
        if (secureDocument3 != null) {
            sb.append(secureDocument3.secureFile.id);
        }
        int size2 = this.translationDocuments.size();
        for (int i4 = 0; i4 < size2; i4++) {
            sb.append(this.translationDocuments.get(i4).secureFile.id);
        }
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public boolean isHasNotAnyChanges() {
        String str = this.initialValues;
        return str == null || str.equals(getCurrentValues());
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (isHasNotAnyChanges()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new PassportActivity$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
        builder.setMessage(LocaleController.getString("PassportDiscardChanges", R.string.PassportDiscardChanges));
        showDialog(builder.create());
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$69(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void processSelectedFiles(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList) {
        if (!arrayList.isEmpty()) {
            int i = this.uploadingFileType;
            boolean z = true;
            boolean z2 = false;
            if (!(i == 1 || i == 4 || !(this.currentType.type instanceof TLRPC$TL_secureValueTypePersonalDetails))) {
                int i2 = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    if (i2 < editTextBoldCursorArr.length) {
                        if (i2 != 5 && i2 != 8 && i2 != 4 && i2 != 6 && editTextBoldCursorArr[i2].length() > 0) {
                            z = false;
                            break;
                        }
                        i2++;
                    } else {
                        break;
                    }
                }
                z2 = z;
            }
            Utilities.globalQueue.postRunnable(new PassportActivity$$ExternalSyntheticLambda53(this, arrayList, this.uploadingFileType, z2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processSelectedFiles$72(ArrayList arrayList, int i, boolean z) {
        TLRPC$PhotoSize scaleAndSaveImage;
        int i2 = i;
        int i3 = this.uploadingFileType;
        int min = Math.min((i3 == 0 || i3 == 4) ? 20 : 1, arrayList.size());
        boolean z2 = false;
        for (int i4 = 0; i4 < min; i4++) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = (SendMessagesHelper.SendingMediaInfo) arrayList.get(i4);
            Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, sendingMediaInfo.uri, 2048.0f, 2048.0f, false);
            if (!(loadBitmap == null || (scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 2048.0f, 2048.0f, 89, false, 320, 320)) == null)) {
                TLRPC$TL_secureFile tLRPC$TL_secureFile = new TLRPC$TL_secureFile();
                TLRPC$FileLocation tLRPC$FileLocation = scaleAndSaveImage.location;
                tLRPC$TL_secureFile.dc_id = (int) tLRPC$FileLocation.volume_id;
                tLRPC$TL_secureFile.id = (long) tLRPC$FileLocation.local_id;
                tLRPC$TL_secureFile.date = (int) (System.currentTimeMillis() / 1000);
                SecureDocument saveFile = this.delegate.saveFile(tLRPC$TL_secureFile);
                saveFile.type = i2;
                AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda55(this, saveFile, i2));
                if (z && !z2) {
                    try {
                        MrzRecognizer.Result recognize = MrzRecognizer.recognize(loadBitmap, this.currentDocumentsType.type instanceof TLRPC$TL_secureValueTypeDriverLicense);
                        if (recognize != null) {
                            try {
                                AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda54(this, recognize));
                                z2 = true;
                            } catch (Throwable th) {
                                th = th;
                                z2 = true;
                                FileLog.e(th);
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        FileLog.e(th);
                    }
                }
            }
        }
        SharedConfig.saveConfig();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processSelectedFiles$70(SecureDocument secureDocument, int i) {
        int i2 = this.uploadingFileType;
        if (i2 == 1) {
            SecureDocument secureDocument2 = this.selfieDocument;
            if (secureDocument2 != null) {
                SecureDocumentCell remove = this.documentsCells.remove(secureDocument2);
                if (remove != null) {
                    this.selfieLayout.removeView(remove);
                }
                this.selfieDocument = null;
            }
        } else if (i2 == 4) {
            if (this.translationDocuments.size() >= 20) {
                return;
            }
        } else if (i2 == 2) {
            SecureDocument secureDocument3 = this.frontDocument;
            if (secureDocument3 != null) {
                SecureDocumentCell remove2 = this.documentsCells.remove(secureDocument3);
                if (remove2 != null) {
                    this.frontLayout.removeView(remove2);
                }
                this.frontDocument = null;
            }
        } else if (i2 == 3) {
            SecureDocument secureDocument4 = this.reverseDocument;
            if (secureDocument4 != null) {
                SecureDocumentCell remove3 = this.documentsCells.remove(secureDocument4);
                if (remove3 != null) {
                    this.reverseLayout.removeView(remove3);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processSelectedFiles$71(MrzRecognizer.Result result) {
        int i;
        int i2;
        int i3 = result.type;
        if (i3 == 2) {
            if (!(this.currentDocumentsType.type instanceof TLRPC$TL_secureValueTypeIdentityCard)) {
                int size = this.availableDocumentTypes.size();
                int i4 = 0;
                while (true) {
                    if (i4 >= size) {
                        break;
                    }
                    TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType = this.availableDocumentTypes.get(i4);
                    if (tLRPC$TL_secureRequiredType.type instanceof TLRPC$TL_secureValueTypeIdentityCard) {
                        this.currentDocumentsType = tLRPC$TL_secureRequiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                    i4++;
                }
            }
        } else if (i3 == 1) {
            if (!(this.currentDocumentsType.type instanceof TLRPC$TL_secureValueTypePassport)) {
                int size2 = this.availableDocumentTypes.size();
                int i5 = 0;
                while (true) {
                    if (i5 >= size2) {
                        break;
                    }
                    TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType2 = this.availableDocumentTypes.get(i5);
                    if (tLRPC$TL_secureRequiredType2.type instanceof TLRPC$TL_secureValueTypePassport) {
                        this.currentDocumentsType = tLRPC$TL_secureRequiredType2;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                    i5++;
                }
            }
        } else if (i3 == 3) {
            if (!(this.currentDocumentsType.type instanceof TLRPC$TL_secureValueTypeInternalPassport)) {
                int size3 = this.availableDocumentTypes.size();
                int i6 = 0;
                while (true) {
                    if (i6 >= size3) {
                        break;
                    }
                    TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType3 = this.availableDocumentTypes.get(i6);
                    if (tLRPC$TL_secureRequiredType3.type instanceof TLRPC$TL_secureValueTypeInternalPassport) {
                        this.currentDocumentsType = tLRPC$TL_secureRequiredType3;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                    i6++;
                }
            }
        } else if (i3 == 4 && !(this.currentDocumentsType.type instanceof TLRPC$TL_secureValueTypeDriverLicense)) {
            int size4 = this.availableDocumentTypes.size();
            int i7 = 0;
            while (true) {
                if (i7 >= size4) {
                    break;
                }
                TLRPC$TL_secureRequiredType tLRPC$TL_secureRequiredType4 = this.availableDocumentTypes.get(i7);
                if (tLRPC$TL_secureRequiredType4.type instanceof TLRPC$TL_secureValueTypeDriverLicense) {
                    this.currentDocumentsType = tLRPC$TL_secureRequiredType4;
                    updateInterfaceStringsForDocumentType();
                    break;
                }
                i7++;
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
        int i8 = result.gender;
        if (i8 != 0) {
            if (i8 == 1) {
                this.currentGender = "male";
                this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
            } else if (i8 == 2) {
                this.currentGender = "female";
                this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            String str = result.nationality;
            this.currentCitizeship = str;
            String str2 = this.languageMap.get(str);
            if (str2 != null) {
                this.inputFields[5].setText(str2);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            String str3 = result.issuingCountry;
            this.currentResidence = str3;
            String str4 = this.languageMap.get(str3);
            if (str4 != null) {
                this.inputFields[6].setText(str4);
            }
        }
        int i9 = result.birthDay;
        if (i9 > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(i9), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
        int i10 = result.expiryDay;
        if (i10 <= 0 || (i = result.expiryMonth) <= 0 || (i2 = result.expiryYear) <= 0) {
            int[] iArr = this.currentExpireDate;
            iArr[2] = 0;
            iArr[1] = 0;
            iArr[0] = 0;
            this.inputFields[8].setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
            return;
        }
        int[] iArr2 = this.currentExpireDate;
        iArr2[0] = i2;
        iArr2[1] = i;
        iArr2[2] = i10;
        this.inputFields[8].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(i10), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)}));
    }

    public void setNeedActivityResult(boolean z) {
        this.needActivityResult = z;
    }

    private static class ProgressView extends View {
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

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float measuredWidth = (float) ((int) (((float) getMeasuredWidth()) * this.progress));
            canvas.drawRect(0.0f, 0.0f, measuredWidth, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect(measuredWidth, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    public class PhoneConfirmationView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        /* access modifiers changed from: private */
        public EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        /* access modifiers changed from: private */
        public int codeTime = 15000;
        private Timer codeTimer;
        private TextView confirmTextView;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        /* access modifiers changed from: private */
        public String lastError = "";
        /* access modifiers changed from: private */
        public int length;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        private String pattern = "*";
        /* access modifiers changed from: private */
        public String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        final /* synthetic */ PassportActivity this$0;
        /* access modifiers changed from: private */
        public int time = 60000;
        /* access modifiers changed from: private */
        public TextView timeText;
        /* access modifiers changed from: private */
        public Timer timeTimer;
        /* access modifiers changed from: private */
        public int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        /* access modifiers changed from: private */
        public int verificationType;
        /* access modifiers changed from: private */
        public boolean waitingForEvent;

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onBackPressed$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        public boolean needBackButton() {
            return true;
        }

        static /* synthetic */ int access$10026(PhoneConfirmationView phoneConfirmationView, double d) {
            double d2 = (double) phoneConfirmationView.codeTime;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            phoneConfirmationView.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$10626(PhoneConfirmationView phoneConfirmationView, double d) {
            double d2 = (double) phoneConfirmationView.time;
            Double.isNaN(d2);
            int i = (int) (d2 - d);
            phoneConfirmationView.time = i;
            return i;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneConfirmationView(org.telegram.ui.PassportActivity r25, android.content.Context r26, int r27) {
            /*
                r24 = this;
                r0 = r24
                r1 = r25
                r2 = r26
                r0.this$0 = r1
                r0.<init>(r2)
                java.lang.Object r3 = new java.lang.Object
                r3.<init>()
                r0.timerSync = r3
                r3 = 60000(0xea60, float:8.4078E-41)
                r0.time = r3
                r3 = 15000(0x3a98, float:2.102E-41)
                r0.codeTime = r3
                java.lang.String r3 = ""
                r0.lastError = r3
                java.lang.String r3 = "*"
                r0.pattern = r3
                r3 = r27
                r0.verificationType = r3
                r3 = 1
                r0.setOrientation(r3)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.confirmTextView = r4
                java.lang.String r5 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r6)
                android.widget.TextView r4 = r0.confirmTextView
                r6 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.confirmTextView
                r7 = 1073741824(0x40000000, float:2.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                r9 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r8, r9)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r0.titleTextView = r4
                java.lang.String r8 = "windowBackgroundWhiteBlackText"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r4.setTextColor(r10)
                android.widget.TextView r4 = r0.titleTextView
                r10 = 1099956224(0x41900000, float:18.0)
                r4.setTextSize(r3, r10)
                android.widget.TextView r4 = r0.titleTextView
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r4.setTypeface(r10)
                android.widget.TextView r4 = r0.titleTextView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                r12 = 3
                if (r10 == 0) goto L_0x007b
                r10 = 5
                goto L_0x007c
            L_0x007b:
                r10 = 3
            L_0x007c:
                r4.setGravity(r10)
                android.widget.TextView r4 = r0.titleTextView
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                r4.setLineSpacing(r10, r9)
                android.widget.TextView r4 = r0.titleTextView
                r10 = 49
                r4.setGravity(r10)
                int r4 = r0.verificationType
                r13 = -2
                if (r4 != r12) goto L_0x0129
                android.widget.TextView r4 = r0.confirmTextView
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x009d
                r8 = 5
                goto L_0x009e
            L_0x009d:
                r8 = 3
            L_0x009e:
                r8 = r8 | 48
                r4.setGravity(r8)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x00ae
                r8 = 5
                goto L_0x00af
            L_0x00ae:
                r8 = 3
            L_0x00af:
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r8)
                r0.addView(r4, r8)
                android.widget.ImageView r8 = new android.widget.ImageView
                r8.<init>(r2)
                int r14 = org.telegram.messenger.R.drawable.phone_activate
                r8.setImageResource(r14)
                boolean r14 = org.telegram.messenger.LocaleController.isRTL
                if (r14 == 0) goto L_0x00f8
                r15 = 64
                r16 = 1117257728(0x42980000, float:76.0)
                r17 = 19
                r18 = 1073741824(0x40000000, float:2.0)
                r19 = 1073741824(0x40000000, float:2.0)
                r20 = 0
                r21 = 0
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r4.addView(r8, r14)
                android.widget.TextView r8 = r0.confirmTextView
                r14 = -1
                r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r16 = org.telegram.messenger.LocaleController.isRTL
                if (r16 == 0) goto L_0x00e5
                r16 = 5
                goto L_0x00e7
            L_0x00e5:
                r16 = 3
            L_0x00e7:
                r17 = 1118044160(0x42a40000, float:82.0)
                r18 = 0
                r19 = 0
                r20 = 0
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                r4.addView(r8, r14)
                goto L_0x0208
            L_0x00f8:
                android.widget.TextView r15 = r0.confirmTextView
                r16 = -1
                r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                if (r14 == 0) goto L_0x0103
                r18 = 5
                goto L_0x0105
            L_0x0103:
                r18 = 3
            L_0x0105:
                r19 = 0
                r20 = 0
                r21 = 1118044160(0x42a40000, float:82.0)
                r22 = 0
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r4.addView(r15, r14)
                r16 = 64
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 21
                r20 = 1073741824(0x40000000, float:2.0)
                r21 = 0
                r22 = 1073741824(0x40000000, float:2.0)
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r4.addView(r8, r14)
                goto L_0x0208
            L_0x0129:
                android.widget.TextView r4 = r0.confirmTextView
                r4.setGravity(r10)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r10)
                r0.addView(r4, r14)
                int r14 = r0.verificationType
                java.lang.String r15 = "chats_actionBackground"
                if (r14 != r3) goto L_0x01a6
                android.widget.ImageView r14 = new android.widget.ImageView
                r14.<init>(r2)
                r0.blackImageView = r14
                int r11 = org.telegram.messenger.R.drawable.sms_devices
                r14.setImageResource(r11)
                android.widget.ImageView r11 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r14 = new android.graphics.PorterDuffColorFilter
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
                r14.<init>(r8, r10)
                r11.setColorFilter(r14)
                android.widget.ImageView r8 = r0.blackImageView
                r17 = -2
                r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r19 = 51
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
                r4.addView(r8, r10)
                android.widget.ImageView r8 = new android.widget.ImageView
                r8.<init>(r2)
                r0.blueImageView = r8
                int r10 = org.telegram.messenger.R.drawable.sms_bubble
                r8.setImageResource(r10)
                android.widget.ImageView r8 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
                r10.<init>(r11, r14)
                r8.setColorFilter(r10)
                android.widget.ImageView r8 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
                r4.addView(r8, r10)
                android.widget.TextView r4 = r0.titleTextView
                int r8 = org.telegram.messenger.R.string.SentAppCodeTitle
                java.lang.String r10 = "SentAppCodeTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r4.setText(r8)
                goto L_0x01e6
            L_0x01a6:
                android.widget.ImageView r8 = new android.widget.ImageView
                r8.<init>(r2)
                r0.blueImageView = r8
                int r10 = org.telegram.messenger.R.drawable.sms_code
                r8.setImageResource(r10)
                android.widget.ImageView r8 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
                r10.<init>(r11, r14)
                r8.setColorFilter(r10)
                android.widget.ImageView r8 = r0.blueImageView
                r17 = -2
                r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r19 = 51
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
                r4.addView(r8, r10)
                android.widget.TextView r4 = r0.titleTextView
                int r8 = org.telegram.messenger.R.string.SentSmsCodeTitle
                java.lang.String r10 = "SentSmsCodeTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r4.setText(r8)
            L_0x01e6:
                android.widget.TextView r4 = r0.titleTextView
                r17 = -2
                r18 = -2
                r19 = 49
                r20 = 0
                r21 = 18
                r22 = 0
                r23 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r8)
                android.widget.TextView r4 = r0.confirmTextView
                r21 = 17
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r4, r8)
            L_0x0208:
                android.widget.LinearLayout r4 = new android.widget.LinearLayout
                r4.<init>(r2)
                r0.codeFieldContainer = r4
                r8 = 0
                r4.setOrientation(r8)
                android.widget.LinearLayout r4 = r0.codeFieldContainer
                r10 = 36
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r10, (int) r3)
                r0.addView(r4, r10)
                int r4 = r0.verificationType
                if (r4 != r12) goto L_0x0229
                android.widget.LinearLayout r4 = r0.codeFieldContainer
                r10 = 8
                r4.setVisibility(r10)
            L_0x0229:
                org.telegram.ui.PassportActivity$PhoneConfirmationView$1 r4 = new org.telegram.ui.PassportActivity$PhoneConfirmationView$1
                r4.<init>(r0, r2, r1)
                r0.timeText = r4
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.timeText
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r5 = (float) r5
                r4.setLineSpacing(r5, r9)
                int r4 = r0.verificationType
                r5 = 1097859072(0x41700000, float:15.0)
                r10 = 1092616192(0x41200000, float:10.0)
                if (r4 != r12) goto L_0x0287
                android.widget.TextView r4 = r0.timeText
                r4.setTextSize(r3, r6)
                android.widget.TextView r4 = r0.timeText
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0256
                r6 = 5
                goto L_0x0257
            L_0x0256:
                r6 = 3
            L_0x0257:
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r6)
                r0.addView(r4, r6)
                org.telegram.ui.PassportActivity$ProgressView r4 = new org.telegram.ui.PassportActivity$ProgressView
                r4.<init>(r2)
                r0.progressView = r4
                android.widget.TextView r4 = r0.timeText
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x026d
                r11 = 5
                goto L_0x026e
            L_0x026d:
                r11 = 3
            L_0x026e:
                r4.setGravity(r11)
                org.telegram.ui.PassportActivity$ProgressView r4 = r0.progressView
                r17 = -1
                r18 = 3
                r19 = 0
                r20 = 1094713344(0x41400000, float:12.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
                r0.addView(r4, r6)
                goto L_0x02a9
            L_0x0287:
                android.widget.TextView r4 = r0.timeText
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r4.setPadding(r8, r6, r8, r11)
                android.widget.TextView r4 = r0.timeText
                r4.setTextSize(r3, r5)
                android.widget.TextView r4 = r0.timeText
                r6 = 49
                r4.setGravity(r6)
                android.widget.TextView r4 = r0.timeText
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r6)
                r0.addView(r4, r11)
            L_0x02a9:
                org.telegram.ui.PassportActivity$PhoneConfirmationView$2 r4 = new org.telegram.ui.PassportActivity$PhoneConfirmationView$2
                r4.<init>(r0, r2, r1)
                r0.problemText = r4
                java.lang.String r1 = "windowBackgroundWhiteBlueText4"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r4.setTextColor(r1)
                android.widget.TextView r1 = r0.problemText
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r2 = (float) r2
                r1.setLineSpacing(r2, r9)
                android.widget.TextView r1 = r0.problemText
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r1.setPadding(r8, r2, r8, r4)
                android.widget.TextView r1 = r0.problemText
                r1.setTextSize(r3, r5)
                android.widget.TextView r1 = r0.problemText
                r2 = 49
                r1.setGravity(r2)
                int r1 = r0.verificationType
                if (r1 != r3) goto L_0x02ee
                android.widget.TextView r1 = r0.problemText
                int r2 = org.telegram.messenger.R.string.DidNotGetTheCodeSms
                java.lang.String r3 = "DidNotGetTheCodeSms"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x02fb
            L_0x02ee:
                android.widget.TextView r1 = r0.problemText
                int r2 = org.telegram.messenger.R.string.DidNotGetTheCode
                java.lang.String r3 = "DidNotGetTheCode"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
            L_0x02fb:
                android.widget.TextView r1 = r0.problemText
                r2 = 49
                android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r13, (int) r2)
                r0.addView(r1, r2)
                android.widget.TextView r1 = r0.problemText
                org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda2
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PassportActivity.PhoneConfirmationView.<init>(org.telegram.ui.PassportActivity, android.content.Context, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (!this.nextPressed) {
                int i = this.nextType;
                if (!((i == 4 && this.verificationType == 2) || i == 0)) {
                    resendCode();
                    return;
                }
                try {
                    PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                    Intent intent = new Intent("android.intent.action.SENDTO");
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                    intent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + format + " " + this.phone);
                    intent.putExtra("android.intent.extra.TEXT", "Phone: " + this.phone + "\nApp version: " + format + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                    getContext().startActivity(Intent.createChooser(intent, "Send email..."));
                } catch (Exception unused) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            ImageView imageView;
            super.onMeasure(i, i2);
            if (this.verificationType != 3 && (imageView = this.blueImageView) != null) {
                int measuredHeight = imageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                int dp = AndroidUtilities.dp(80.0f);
                int dp2 = AndroidUtilities.dp(291.0f);
                if (this.this$0.scrollHeight - measuredHeight < dp) {
                    setMeasuredDimension(getMeasuredWidth(), measuredHeight + dp);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), Math.min(this.this$0.scrollHeight, dp2));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            super.onLayout(z, i, i2, i3, i4);
            if (this.verificationType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int measuredHeight = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int measuredHeight2 = this.problemText.getMeasuredHeight();
                    i5 = (measuredHeight + bottom) - measuredHeight2;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), i5, this.problemText.getRight(), measuredHeight2 + i5);
                } else if (this.timeText.getVisibility() == 0) {
                    int measuredHeight3 = this.timeText.getMeasuredHeight();
                    i5 = (measuredHeight + bottom) - measuredHeight3;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), i5, this.timeText.getRight(), measuredHeight3 + i5);
                } else {
                    i5 = measuredHeight + bottom;
                }
                int measuredHeight4 = this.codeFieldContainer.getMeasuredHeight();
                int i6 = (((i5 - bottom) - measuredHeight4) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), i6, this.codeFieldContainer.getRight(), measuredHeight4 + i6);
            }
        }

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
            tLRPC$TL_auth_resendCode.phone_number = this.phone;
            tLRPC$TL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda8(this, bundle, tLRPC$TL_auth_resendCode), 2);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$3(Bundle bundle, TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda5(this, tLRPC$TL_error, bundle, tLObject, tLRPC$TL_auth_resendCode));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$2(TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject, TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode) {
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TLRPC$TL_auth_sentCode) tLObject, true);
            } else {
                AlertDialog alertDialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, tLRPC$TL_error, this.this$0, tLRPC$TL_auth_resendCode, new Object[0]);
                if (alertDialog != null && tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                    alertDialog.setPositiveButtonListener(new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda0(this));
                }
            }
            this.this$0.needHideProgress();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$resendCode$1(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.finishFragment();
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle bundle, boolean z) {
            int i;
            int i2;
            Bundle bundle2 = bundle;
            if (bundle2 != null) {
                this.waitingForEvent = true;
                int i3 = this.verificationType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.phone = bundle2.getString("phone");
                this.phoneHash = bundle2.getString("phoneHash");
                int i4 = bundle2.getInt("timeout");
                this.time = i4;
                this.timeout = i4;
                this.nextType = bundle2.getInt("nextType");
                this.pattern = bundle2.getString("pattern");
                int i5 = bundle2.getInt("length");
                this.length = i5;
                if (i5 == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                CharSequence charSequence = "";
                int i6 = 8;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    int i7 = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (i7 >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[i7].setText(charSequence);
                        i7++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    final int i8 = 0;
                    while (i8 < this.length) {
                        this.codeField[i8] = new EditTextBoldCursor(getContext());
                        this.codeField[i8].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[i8].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[i8].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[i8].setCursorWidth(1.5f);
                        Drawable mutate = getResources().getDrawable(R.drawable.search_dark_activated).mutate();
                        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteInputFieldActivated"), PorterDuff.Mode.MULTIPLY));
                        this.codeField[i8].setBackgroundDrawable(mutate);
                        this.codeField[i8].setImeOptions(NUM);
                        this.codeField[i8].setTextSize(1, 20.0f);
                        this.codeField[i8].setMaxLines(1);
                        this.codeField[i8].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[i8].setPadding(0, 0, 0, 0);
                        this.codeField[i8].setGravity(49);
                        if (this.verificationType == 3) {
                            this.codeField[i8].setEnabled(false);
                            this.codeField[i8].setInputType(0);
                            this.codeField[i8].setVisibility(8);
                        } else {
                            this.codeField[i8].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[i8], LayoutHelper.createLinear(34, 36, 1, 0, 0, i8 != this.length - 1 ? 7 : 0, 0));
                        this.codeField[i8].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                            }

                            public void afterTextChanged(Editable editable) {
                                int length;
                                if (!PhoneConfirmationView.this.ignoreOnTextChange && (length = editable.length()) >= 1) {
                                    if (length > 1) {
                                        String obj = editable.toString();
                                        boolean unused = PhoneConfirmationView.this.ignoreOnTextChange = true;
                                        for (int i = 0; i < Math.min(PhoneConfirmationView.this.length - i8, length); i++) {
                                            if (i == 0) {
                                                editable.replace(0, length, obj.substring(i, i + 1));
                                            } else {
                                                PhoneConfirmationView.this.codeField[i8 + i].setText(obj.substring(i, i + 1));
                                            }
                                        }
                                        boolean unused2 = PhoneConfirmationView.this.ignoreOnTextChange = false;
                                    }
                                    if (i8 != PhoneConfirmationView.this.length - 1) {
                                        PhoneConfirmationView.this.codeField[i8 + 1].setSelection(PhoneConfirmationView.this.codeField[i8 + 1].length());
                                        PhoneConfirmationView.this.codeField[i8 + 1].requestFocus();
                                    }
                                    if ((i8 == PhoneConfirmationView.this.length - 1 || (i8 == PhoneConfirmationView.this.length - 2 && length >= 2)) && PhoneConfirmationView.this.getCode().length() == PhoneConfirmationView.this.length) {
                                        PhoneConfirmationView.this.onNextPressed((String) null);
                                    }
                                }
                            }
                        });
                        this.codeField[i8].setOnKeyListener(new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda3(this, i8));
                        this.codeField[i8].setOnEditorActionListener(new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda4(this));
                        i8++;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    PhoneFormat instance = PhoneFormat.getInstance();
                    String format = instance.format("+" + this.phone);
                    int i9 = this.verificationType;
                    if (i9 == 2) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(format)));
                    } else if (i9 == 3) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(format)));
                    } else if (i9 == 4) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(format)));
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
                    int i10 = this.verificationType;
                    if (i10 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i11 = this.nextType;
                        if (i11 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 1, 0));
                        } else if (i11 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 1, 0));
                        }
                        createTimer();
                    } else if (i10 == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView = this.timeText;
                        if (this.time >= 1000) {
                            i6 = 0;
                        }
                        textView.setVisibility(i6);
                        createTimer();
                    } else if (i10 == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView2 = this.timeText;
                        if (this.time >= 1000) {
                            i6 = 0;
                        }
                        textView2.setVisibility(i6);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setParams$4(int i, View view, int i2, KeyEvent keyEvent) {
            if (i2 != 67 || this.codeField[i].length() != 0 || i <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            int i3 = i - 1;
            editTextBoldCursorArr[i3].setSelection(editTextBoldCursorArr[i3].length());
            this.codeField[i3].requestFocus();
            this.codeField[i3].dispatchKeyEvent(keyEvent);
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setParams$5(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* access modifiers changed from: private */
        public void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$4$$ExternalSyntheticLambda0(this));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$9900 = PhoneConfirmationView.this.lastCodeTime;
                        Double.isNaN(currentTimeMillis);
                        double unused = PhoneConfirmationView.this.lastCodeTime = currentTimeMillis;
                        PhoneConfirmationView.access$10026(PhoneConfirmationView.this, currentTimeMillis - access$9900);
                        if (PhoneConfirmationView.this.codeTime <= 1000) {
                            PhoneConfirmationView.this.problemText.setVisibility(0);
                            PhoneConfirmationView.this.timeText.setVisibility(8);
                            PhoneConfirmationView.this.destroyCodeTimer();
                        }
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.codeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                Timer timer = new Timer();
                this.timeTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (PhoneConfirmationView.this.timeTimer != null) {
                            double currentTimeMillis = (double) System.currentTimeMillis();
                            double access$10500 = PhoneConfirmationView.this.lastCurrentTime;
                            Double.isNaN(currentTimeMillis);
                            PhoneConfirmationView.access$10626(PhoneConfirmationView.this, currentTimeMillis - access$10500);
                            double unused = PhoneConfirmationView.this.lastCurrentTime = currentTimeMillis;
                            AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$5$$ExternalSyntheticLambda0(this));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$2() {
                        if (PhoneConfirmationView.this.time >= 1000) {
                            int access$10600 = (PhoneConfirmationView.this.time / 1000) / 60;
                            int access$106002 = (PhoneConfirmationView.this.time / 1000) - (access$10600 * 60);
                            if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3) {
                                PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(access$10600), Integer.valueOf(access$106002)));
                            } else if (PhoneConfirmationView.this.nextType == 2) {
                                PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(access$10600), Integer.valueOf(access$106002)));
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
                            NotificationCenter.getGlobalInstance().removeObserver(PhoneConfirmationView.this, NotificationCenter.didReceiveCall);
                            boolean unused = PhoneConfirmationView.this.waitingForEvent = false;
                            PhoneConfirmationView.this.destroyCodeTimer();
                            PhoneConfirmationView.this.resendCode();
                        } else if (PhoneConfirmationView.this.verificationType != 2 && PhoneConfirmationView.this.verificationType != 4) {
                        } else {
                            if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 2) {
                                if (PhoneConfirmationView.this.nextType == 4) {
                                    PhoneConfirmationView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                                } else {
                                    PhoneConfirmationView.this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
                                }
                                PhoneConfirmationView.this.createCodeTimer();
                                TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode = new TLRPC$TL_auth_resendCode();
                                tLRPC$TL_auth_resendCode.phone_number = PhoneConfirmationView.this.phone;
                                tLRPC$TL_auth_resendCode.phone_code_hash = PhoneConfirmationView.this.phoneHash;
                                ConnectionsManager.getInstance(PhoneConfirmationView.this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_resendCode, new PassportActivity$PhoneConfirmationView$5$$ExternalSyntheticLambda2(this), 2);
                            } else if (PhoneConfirmationView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(PhoneConfirmationView.this, NotificationCenter.didReceiveSmsCode);
                                boolean unused2 = PhoneConfirmationView.this.waitingForEvent = false;
                                PhoneConfirmationView.this.destroyCodeTimer();
                                PhoneConfirmationView.this.resendCode();
                            }
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        if (tLRPC$TL_error != null && tLRPC$TL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$5$$ExternalSyntheticLambda1(this, tLRPC$TL_error));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0(TLRPC$TL_error tLRPC$TL_error) {
                        String unused = PhoneConfirmationView.this.lastError = tLRPC$TL_error.text;
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.timeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        /* access modifiers changed from: private */
        public String getCode() {
            if (this.codeField == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    return sb.toString();
                }
                sb.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[i].getText().toString()));
                i++;
            }
        }

        public void onNextPressed(String str) {
            if (!this.nextPressed) {
                if (str == null) {
                    str = getCode();
                }
                if (TextUtils.isEmpty(str)) {
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
                TLRPC$TL_account_verifyPhone tLRPC$TL_account_verifyPhone = new TLRPC$TL_account_verifyPhone();
                tLRPC$TL_account_verifyPhone.phone_number = this.phone;
                tLRPC$TL_account_verifyPhone.phone_code = str;
                tLRPC$TL_account_verifyPhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_account_verifyPhone, new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda9(this, tLRPC$TL_account_verifyPhone), 2);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$7(TLRPC$TL_account_verifyPhone tLRPC$TL_account_verifyPhone, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda6(this, tLRPC$TL_error, tLRPC$TL_account_verifyPhone));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onNextPressed$6(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_verifyPhone tLRPC$TL_account_verifyPhone) {
            int i;
            int i2;
            this.this$0.needHideProgress();
            this.nextPressed = false;
            if (tLRPC$TL_error == null) {
                destroyTimer();
                destroyCodeTimer();
                this.this$0.delegate.saveValue(this.this$0.currentType, (String) this.this$0.currentValues.get("phone"), (String) null, (TLRPC$TL_secureRequiredType) null, (String) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (SecureDocument) null, new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda7(this.this$0), (ErrorRunnable) null);
                return;
            }
            this.lastError = tLRPC$TL_error.text;
            int i3 = this.verificationType;
            if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                createTimer();
            }
            int i4 = this.verificationType;
            if (i4 == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i4 == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = true;
            if (this.verificationType != 3) {
                AlertsCreator.processError(this.this$0.currentAccount, tLRPC$TL_error, this.this$0, tLRPC$TL_account_verifyPhone, new Object[0]);
            }
            this.this$0.showEditDoneProgress(true, false);
            if (tLRPC$TL_error.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error.text.contains("PHONE_CODE_INVALID")) {
                int i5 = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                    if (i5 < editTextBoldCursorArr.length) {
                        editTextBoldCursorArr[i5].setText("");
                        i5++;
                    } else {
                        editTextBoldCursorArr[0].requestFocus();
                        return;
                    }
                }
            } else if (tLRPC$TL_error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                this.this$0.setPage(0, true, (Bundle) null);
            }
        }

        public boolean onBackPressed(boolean z) {
            if (!z) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda1(this));
                this.this$0.showDialog(builder.create());
                return false;
            }
            TLRPC$TL_auth_cancelCode tLRPC$TL_auth_cancelCode = new TLRPC$TL_auth_cancelCode();
            tLRPC$TL_auth_cancelCode.phone_number = this.phone;
            tLRPC$TL_auth_cancelCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_auth_cancelCode, PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10.INSTANCE, 2);
            destroyTimer();
            destroyCodeTimer();
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$8(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null);
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
                for (int length2 = this.codeField.length - 1; length2 >= 0; length2--) {
                    if (length2 == 0 || this.codeField[length2].length() != 0) {
                        this.codeField[length2].requestFocus();
                        EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                        editTextBoldCursorArr[length2].setSelection(editTextBoldCursorArr[length2].length());
                        AndroidUtilities.showKeyboard(this.codeField[length2]);
                        return;
                    }
                }
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            EditTextBoldCursor[] editTextBoldCursorArr;
            if (this.waitingForEvent && (editTextBoldCursorArr = this.codeField) != null) {
                if (i == NotificationCenter.didReceiveSmsCode) {
                    editTextBoldCursorArr[0].setText("" + objArr[0]);
                    onNextPressed((String) null);
                } else if (i == NotificationCenter.didReceiveCall) {
                    String str = "" + objArr[0];
                    if (AndroidUtilities.checkPhonePattern(this.pattern, str)) {
                        this.ignoreOnTextChange = true;
                        this.codeField[0].setText(str);
                        this.ignoreOnTextChange = false;
                        onNextPressed((String) null);
                    }
                }
            }
        }
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
        arrayList.add(new ThemeDescription(this.extraBackgroundView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        if (this.extraBackgroundView2 != null) {
            arrayList.add(new ThemeDescription(this.extraBackgroundView2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        }
        for (int i = 0; i < this.dividers.size(); i++) {
            arrayList.add(new ThemeDescription(this.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        }
        for (Map.Entry<SecureDocument, SecureDocumentCell> value : this.documentsCells.entrySet()) {
            SecureDocumentCell secureDocumentCell = (SecureDocumentCell) value.getValue();
            arrayList.add(new ThemeDescription(secureDocumentCell, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) secureDocumentCell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) secureDocumentCell, 0, new Class[]{SecureDocumentCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        }
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        if (this.inputFields != null) {
            for (int i2 = 0; i2 < this.inputFields.length; i2++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[i2].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
        }
        if (this.inputExtraFields != null) {
            for (int i3 = 0; i3 < this.inputExtraFields.length; i3++) {
                arrayList.add(new ThemeDescription((View) this.inputExtraFields[i3].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i3], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i3], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i3], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i3], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(this.inputExtraFields[i3], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
            }
        }
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.noPasswordImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelIcons"));
        arrayList.add(new ThemeDescription(this.noPasswordTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.noPasswordSetTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText5"));
        arrayList.add(new ThemeDescription(this.passwordForgotButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.plusTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.acceptTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "passport_authorizeText"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "passport_authorizeBackground"));
        arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "passport_authorizeBackgroundSelected"));
        arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
        arrayList.add(new ThemeDescription(this.progressViewButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sessions_devicesImage"));
        arrayList.add(new ThemeDescription(this.emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.emptyTextView3, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        return arrayList;
    }
}
