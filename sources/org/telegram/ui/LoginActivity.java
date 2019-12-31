package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_deleteAccount;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_auth_authorization;
import org.telegram.tgnet.TLRPC.TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC.TL_auth_checkPassword;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_auth_signUp;
import org.telegram.tgnet.TLRPC.TL_codeSettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getNearestDc;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.TL_nearestDc;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC.auth_CodeType;
import org.telegram.tgnet.TLRPC.auth_SentCodeType;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate.-CC;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SlideView;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment {
    private static final int DONE_TYPE_ACTION = 1;
    private static final int DONE_TYPE_FLOATING = 0;
    private static final int done_button = 1;
    private boolean checkPermissions;
    private boolean checkShowPermissions;
    private int currentDoneType;
    private TL_help_termsOfService currentTermsOfService;
    private int currentViewNum;
    private boolean[] doneButtonVisible;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private ContextProgressView doneProgressView;
    private FrameLayout floatingButtonContainer;
    private ImageView floatingButtonIcon;
    private RadialProgressView floatingProgressView;
    private boolean newAccount;
    private AnimatorSet pagesAnimation;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems;
    private int progressRequestId;
    private boolean restoringState;
    private int scrollHeight;
    private AnimatorSet[] showDoneAnimation;
    private boolean syncContacts;
    private SlideView[] views;

    private class ProgressView extends View {
        private boolean animating;
        private final RectF boundsRect = new RectF();
        private long duration;
        private final Paint paint = new Paint(1);
        private final Paint paint2 = new Paint(1);
        private final Path path = new Path();
        private float radius;
        private final RectF rect = new RectF();
        private long startTime;

        public ProgressView(Context context) {
            super(context);
            this.paint.setColor(Theme.getColor("login_progressInner"));
            this.paint2.setColor(Theme.getColor("login_progressOuter"));
        }

        public void startProgressAnimation(long j) {
            this.animating = true;
            this.duration = j;
            this.startTime = System.currentTimeMillis();
            invalidate();
        }

        public void resetProgressAnimation() {
            this.duration = 0;
            this.startTime = 0;
            this.animating = false;
            invalidate();
        }

        public boolean isProgressAnimationRunning() {
            return this.animating;
        }

        /* Access modifiers changed, original: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            this.path.rewind();
            float f = (float) i2;
            this.radius = f / 2.0f;
            this.boundsRect.set(0.0f, 0.0f, (float) i, f);
            this.rect.set(this.boundsRect);
            Path path = this.path;
            RectF rectF = this.boundsRect;
            float f2 = this.radius;
            path.addRoundRect(rectF, f2, f2, Direction.CW);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float min = this.duration > 0 ? Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ((float) this.duration)) : 0.0f;
            canvas.clipPath(this.path);
            RectF rectF = this.boundsRect;
            float f = this.radius;
            canvas.drawRoundRect(rectF, f, f, this.paint);
            rectF = this.rect;
            rectF.right = this.boundsRect.right * min;
            f = this.radius;
            canvas.drawRoundRect(rectF, f, f, this.paint2);
            boolean z = this.animating;
            int i = (this.duration <= 0 || min >= 1.0f) ? 0 : 1;
            int i2 = z & i;
            this.animating = i2;
            if (i2 != 0) {
                postInvalidateOnAnimation();
            }
        }
    }

    public class LoginActivityPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int current_g;
        private byte[] current_p;
        private byte[] current_salt1;
        private byte[] current_salt2;
        private byte[] current_srp_B;
        private long current_srp_id;
        private boolean has_recovery;
        private boolean nextPressed;
        private int passwordType;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;
        final /* synthetic */ LoginActivity this$0;

        public boolean needBackButton() {
            return true;
        }

        public LoginActivityPasswordView(LoginActivity loginActivity, Context context) {
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            String str = "windowBackgroundWhiteGrayText6";
            this.confirmTextView.setTextColor(Theme.getColor(str));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.confirmTextView.setText(LocaleController.getString("LoginPasswordText", NUM));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.codeField = new EditTextBoldCursor(context2);
            String str2 = "windowBackgroundWhiteBlackText";
            this.codeField.setTextColor(Theme.getColor(str2));
            this.codeField.setCursorColor(Theme.getColor(str2));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.codeField.setHint(LocaleController.getString("LoginPassword", NUM));
            this.codeField.setImeOptions(NUM);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setInputType(129);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            this.codeField.setOnEditorActionListener(new -$$Lambda$LoginActivity$LoginActivityPasswordView$TmxPaCI1XFUfKacPC6vKEsUBp8g(this));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.cancelButton.setText(LocaleController.getString("ForgotPassword", NUM));
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(this.cancelButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48));
            this.cancelButton.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivityPasswordView$XuGku6jlA50Wtx4H_NfhHJUyWxg(this));
            this.resetAccountButton = new TextView(context2);
            this.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
            this.resetAccountButton.setVisibility(8);
            this.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", NUM));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(this.resetAccountButton, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 34, 0, 0));
            this.resetAccountButton.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivityPasswordView$K2ROoTSLPIpCLdI8XOROn58qsk4(this));
            this.resetAccountText = new TextView(context2);
            this.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountText.setVisibility(8);
            this.resetAccountText.setTextColor(Theme.getColor(str));
            this.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", NUM));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            TextView textView = this.resetAccountText;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 7, 0, 14));
        }

        public /* synthetic */ boolean lambda$new$0$LoginActivity$LoginActivityPasswordView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ void lambda$new$4$LoginActivity$LoginActivityPasswordView(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                if (this.has_recovery) {
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new -$$Lambda$LoginActivity$LoginActivityPasswordView$oCLNhngYy9ZhYZMHU9IaIdZfqM0(this), 10);
                } else {
                    this.resetAccountText.setVisibility(0);
                    this.resetAccountButton.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    this.this$0.needShowAlert(LocaleController.getString("RestorePasswordNoEitle", NUM), LocaleController.getString("RestorePasswordNoEmailText", NUM));
                }
            }
        }

        public /* synthetic */ void lambda$null$3$LoginActivity$LoginActivityPasswordView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityPasswordView$UcCVsMRXQ-iTPOpCCyDnzwrWa-M(this, tL_error, tLObject));
        }

        public /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityPasswordView(TL_error tL_error, TLObject tLObject) {
            this.this$0.needHideProgress(false);
            if (tL_error == null) {
                TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) tLObject;
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, tL_auth_passwordRecovery.email_pattern));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$LoginActivity$LoginActivityPasswordView$xtZEvWAVK5tRPP93-2SWWR4hemw(this, tL_auth_passwordRecovery));
                Dialog showDialog = this.this$0.showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                    return;
                }
                return;
            }
            String str = "AppName";
            if (tL_error.text.startsWith("FLOOD_WAIT")) {
                String formatPluralString;
                int intValue = Utilities.parseInt(tL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString(str, NUM), tL_error.text);
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityPasswordView(TL_auth_passwordRecovery tL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", tL_auth_passwordRecovery.email_pattern);
            this.this$0.setPage(7, true, bundle, false);
        }

        public /* synthetic */ void lambda$new$8$LoginActivity$LoginActivityPasswordView(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new -$$Lambda$LoginActivity$LoginActivityPasswordView$Dp_mVB_gTl6zcz74Qvp-ffZE3zM(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                this.this$0.showDialog(builder.create());
            }
        }

        public /* synthetic */ void lambda$null$7$LoginActivity$LoginActivityPasswordView(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TL_account_deleteAccount tL_account_deleteAccount = new TL_account_deleteAccount();
            tL_account_deleteAccount.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_deleteAccount, new -$$Lambda$LoginActivity$LoginActivityPasswordView$2nlj3NklnLOo9ydoOLKoDWf5TVE(this), 10);
        }

        public /* synthetic */ void lambda$null$6$LoginActivity$LoginActivityPasswordView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityPasswordView$d6NFC5NiYYxgW-wGlgA6Dyrzfcg(this, tL_error));
        }

        public /* synthetic */ void lambda$null$5$LoginActivity$LoginActivityPasswordView(TL_error tL_error) {
            this.this$0.needHideProgress(false);
            String str = "code";
            String str2 = "phoneHash";
            String str3 = "phoneFormated";
            if (tL_error == null) {
                Bundle bundle = new Bundle();
                bundle.putString(str3, this.requestPhone);
                bundle.putString(str2, this.phoneHash);
                bundle.putString(str, this.phoneCode);
                this.this$0.setPage(5, true, bundle, false);
                return;
            }
            String str4 = "AppName";
            if (tL_error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString(str4, NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
                return;
            }
            String str5 = "2FA_CONFIRM_WAIT_";
            if (tL_error.text.startsWith(str5)) {
                Bundle bundle2 = new Bundle();
                bundle2.putString(str3, this.requestPhone);
                bundle2.putString(str2, this.phoneHash);
                bundle2.putString(str, this.phoneCode);
                bundle2.putInt("startTime", ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime());
                bundle2.putInt("waitTime", Utilities.parseInt(tL_error.text.replace(str5, "")).intValue());
                this.this$0.setPage(8, true, bundle2, false);
                return;
            }
            this.this$0.needShowAlert(LocaleController.getString(str4, NUM), tL_error.text);
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                boolean z2 = false;
                if (bundle.isEmpty()) {
                    this.resetAccountButton.setVisibility(0);
                    this.resetAccountText.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
                this.resetAccountButton.setVisibility(8);
                this.resetAccountText.setVisibility(8);
                this.codeField.setText("");
                this.currentParams = bundle;
                this.current_salt1 = Utilities.hexToBytes(this.currentParams.getString("current_salt1"));
                this.current_salt2 = Utilities.hexToBytes(this.currentParams.getString("current_salt2"));
                this.current_p = Utilities.hexToBytes(this.currentParams.getString("current_p"));
                this.current_g = this.currentParams.getInt("current_g");
                this.current_srp_B = Utilities.hexToBytes(this.currentParams.getString("current_srp_B"));
                this.current_srp_id = this.currentParams.getLong("current_srp_id");
                this.passwordType = this.currentParams.getInt("passwordType");
                String string = this.currentParams.getString("hint");
                if (this.currentParams.getInt("has_recovery") == 1) {
                    z2 = true;
                }
                this.has_recovery = z2;
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                if (string == null || string.length() <= 0) {
                    this.codeField.setHint(LocaleController.getString("LoginPassword", NUM));
                } else {
                    this.codeField.setHint(string);
                }
            }
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                if (z) {
                    this.codeField.setText("");
                }
                this.this$0.onFieldError(this.confirmTextView);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                this.this$0.needShowProgress(0);
                Utilities.globalQueue.postRunnable(new -$$Lambda$LoginActivity$LoginActivityPasswordView$WyzK5f9f0RFfGbTvGPPQ9T_pyfw(this, obj));
            }
        }

        public /* synthetic */ void lambda$onNextPressed$14$LoginActivity$LoginActivityPasswordView(String str) {
            TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
            if (this.passwordType == 1) {
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = new TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow();
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1 = this.current_salt1;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt2 = this.current_salt2;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.g = this.current_g;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p = this.current_p;
            } else {
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = new TL_passwordKdfAlgoUnknown();
            }
            boolean z = tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
            byte[] x = z ? SRPHelper.getX(AndroidUtilities.getStringBytes(str), tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) : null;
            TL_auth_checkPassword tL_auth_checkPassword = new TL_auth_checkPassword();
            -$$Lambda$LoginActivity$LoginActivityPasswordView$-nNvbKKyRiSG6kHR43GIOADaOYo -__lambda_loginactivity_loginactivitypasswordview_-nnvbkkyrisg6khr43gioadaoyo = new -$$Lambda$LoginActivity$LoginActivityPasswordView$-nNvbKKyRiSG6kHR43GIOADaOYo(this);
            if (z) {
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1 = this.current_salt1;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt2 = this.current_salt2;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.g = this.current_g;
                tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p = this.current_p;
                tL_auth_checkPassword.password = SRPHelper.startCheck(x, this.current_srp_id, this.current_srp_B, tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow);
                if (tL_auth_checkPassword.password == null) {
                    TL_error tL_error = new TL_error();
                    tL_error.text = "PASSWORD_HASH_INVALID";
                    -__lambda_loginactivity_loginactivitypasswordview_-nnvbkkyrisg6khr43gioadaoyo.run(null, tL_error);
                    return;
                }
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_checkPassword, -__lambda_loginactivity_loginactivitypasswordview_-nnvbkkyrisg6khr43gioadaoyo, 10);
            }
        }

        public /* synthetic */ void lambda$null$13$LoginActivity$LoginActivityPasswordView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityPasswordView$eenM4mkBWajH3OtWHKAoLdxq3Jw(this, tL_error, tLObject));
        }

        public /* synthetic */ void lambda$null$12$LoginActivity$LoginActivityPasswordView(TL_error tL_error, TLObject tLObject) {
            this.nextPressed = false;
            if (tL_error != null) {
                if ("SRP_ID_INVALID".equals(tL_error.text)) {
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(new TL_account_getPassword(), new -$$Lambda$LoginActivity$LoginActivityPasswordView$K_Ui4U920vN4BxkMQccwYDwvar_(this), 8);
                    return;
                }
            }
            if (tLObject instanceof TL_auth_authorization) {
                this.this$0.showDoneButton(false, true);
                postDelayed(new -$$Lambda$LoginActivity$LoginActivityPasswordView$mK2WrfN60htmQT94pvEcRuuSFxU(this, tLObject), 150);
            } else {
                this.this$0.needHideProgress(false);
                if (tL_error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                } else {
                    String str = "AppName";
                    if (tL_error.text.startsWith("FLOOD_WAIT")) {
                        String formatPluralString;
                        int intValue = Utilities.parseInt(tL_error.text).intValue();
                        if (intValue < 60) {
                            formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                        } else {
                            formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                        }
                        this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
                    } else {
                        this.this$0.needShowAlert(LocaleController.getString(str, NUM), tL_error.text);
                    }
                }
            }
        }

        public /* synthetic */ void lambda$null$10$LoginActivity$LoginActivityPasswordView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityPasswordView$aCM70N9LbmHe_XZ8f0BkvGrFUr4(this, tL_error, tLObject));
        }

        public /* synthetic */ void lambda$null$9$LoginActivity$LoginActivityPasswordView(TL_error tL_error, TLObject tLObject) {
            if (tL_error == null) {
                TL_account_password tL_account_password = (TL_account_password) tLObject;
                this.current_srp_B = tL_account_password.srp_B;
                this.current_srp_id = tL_account_password.srp_id;
                onNextPressed();
            }
        }

        public /* synthetic */ void lambda$null$11$LoginActivity$LoginActivityPasswordView(TLObject tLObject) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.codeField);
            this.this$0.onAuthSuccess((TL_auth_authorization) tLObject);
        }

        public boolean onBackPressed(boolean z) {
            this.nextPressed = false;
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityPasswordView$ehXcdFYIaEaGdKJTe54wQRdaCWQ(this), 100);
        }

        public /* synthetic */ void lambda$onShow$15$LoginActivity$LoginActivityPasswordView() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                editTextBoldCursor = this.codeField;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                AndroidUtilities.showKeyboard(this.codeField);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("passview_code", obj);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("passview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("passview_params");
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("passview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
        }
    }

    public class LoginActivityRecoverView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private boolean nextPressed;
        final /* synthetic */ LoginActivity this$0;

        public boolean needBackButton() {
            return true;
        }

        public LoginActivityRecoverView(LoginActivity loginActivity, Context context) {
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", NUM));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.codeField = new EditTextBoldCursor(context2);
            String str = "windowBackgroundWhiteBlackText";
            this.codeField.setTextColor(Theme.getColor(str));
            this.codeField.setCursorColor(Theme.getColor(str));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.codeField.setHint(LocaleController.getString("PasswordCode", NUM));
            this.codeField.setImeOptions(NUM);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setInputType(3);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            this.codeField.setOnEditorActionListener(new -$$Lambda$LoginActivity$LoginActivityRecoverView$qDtPayVjLCLCB--uwfYE1UHVJU0(this));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            TextView textView = this.cancelButton;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createLinear(-2, -2, i | 80, 0, 0, 0, 14));
            this.cancelButton.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivityRecoverView$T2kwdtaqLpflrB1CM7xmzXbnaj8(this));
        }

        public /* synthetic */ boolean lambda$new$0$LoginActivity$LoginActivityRecoverView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ void lambda$new$2$LoginActivity$LoginActivityRecoverView(View view) {
            Builder builder = new Builder(this.this$0.getParentActivity());
            builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", NUM));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$LoginActivity$LoginActivityRecoverView$GHrdHw8XOYZ2EOzzL9oTvnnduAY(this));
            Dialog showDialog = this.this$0.showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityRecoverView(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(6, true, new Bundle(), true);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.codeField.setText("");
                this.currentParams = bundle;
                String string = this.currentParams.getString("email_unconfirmed_pattern");
                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", NUM, string));
                AndroidUtilities.showKeyboard(this.codeField);
                this.codeField.requestFocus();
            }
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator vibrator = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                if (z) {
                    this.codeField.setText("");
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                if (this.codeField.getText().toString().length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.this$0.needShowProgress(0);
                TL_auth_recoverPassword tL_auth_recoverPassword = new TL_auth_recoverPassword();
                tL_auth_recoverPassword.code = obj;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_recoverPassword, new -$$Lambda$LoginActivity$LoginActivityRecoverView$rgaN2I5iElJnNYqU1iHy0vwcr6w(this), 10);
            }
        }

        public /* synthetic */ void lambda$onNextPressed$5$LoginActivity$LoginActivityRecoverView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityRecoverView$ZsV5PKFT-uJ58EfwgKYtDIlx8iE(this, tLObject, tL_error));
        }

        public /* synthetic */ void lambda$null$4$LoginActivity$LoginActivityRecoverView(TLObject tLObject, TL_error tL_error) {
            this.this$0.needHideProgress(false);
            this.nextPressed = false;
            String str = "AppName";
            if (tLObject instanceof TL_auth_authorization) {
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$LoginActivity$LoginActivityRecoverView$I1bQ5rEP_TTiI07Nb2wIWAeHEMM(this, tLObject));
                builder.setMessage(LocaleController.getString("PasswordReset", NUM));
                builder.setTitle(LocaleController.getString(str, NUM));
                Dialog showDialog = this.this$0.showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                }
            } else if (tL_error.text.startsWith("CODE_INVALID")) {
                onPasscodeError(true);
            } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                String formatPluralString;
                int intValue = Utilities.parseInt(tL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), tL_error.text);
            }
        }

        public /* synthetic */ void lambda$null$3$LoginActivity$LoginActivityRecoverView(TLObject tLObject, DialogInterface dialogInterface, int i) {
            this.this$0.onAuthSuccess((TL_auth_authorization) tLObject);
        }

        public boolean onBackPressed(boolean z) {
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityRecoverView$G4hHYij0gEl198Zvu2E28bVCEaY(this), 100);
        }

        public /* synthetic */ void lambda$onShow$6$LoginActivity$LoginActivityRecoverView() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                editTextBoldCursor = this.codeField;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (!(obj == null || obj.length() == 0)) {
                bundle.putString("recoveryview_code", obj);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("recoveryview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("recoveryview_params");
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("recoveryview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
        }
    }

    public class LoginActivityRegisterView extends SlideView implements ImageUpdaterDelegate {
        private FileLocation avatar;
        private AnimatorSet avatarAnimation;
        private FileLocation avatarBig;
        private AvatarDrawable avatarDrawable;
        private ImageView avatarEditor;
        private BackupImageView avatarImage;
        private View avatarOverlay;
        private RadialProgressView avatarProgressView;
        private boolean createAfterUpload;
        private Bundle currentParams;
        private EditTextBoldCursor firstNameField;
        private ImageUpdater imageUpdater;
        private EditTextBoldCursor lastNameField;
        private boolean nextPressed = false;
        private String phoneHash;
        private TextView privacyView;
        private String requestPhone;
        private TextView textView;
        final /* synthetic */ LoginActivity this$0;
        private InputFile uploadedAvatar;
        private TextView wrongNumber;

        public class LinkSpan extends ClickableSpan {
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(false);
            }

            public void onClick(View view) {
                LoginActivityRegisterView.this.showTermsOfService(false);
            }
        }

        public /* synthetic */ String getInitialSearchString() {
            return -CC.$default$getInitialSearchString(this);
        }

        public boolean needBackButton() {
            return true;
        }

        private void showTermsOfService(boolean z) {
            if (this.this$0.currentTermsOfService != null) {
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("TermsOfService", NUM));
                if (z) {
                    builder.setPositiveButton(LocaleController.getString("Accept", NUM), new -$$Lambda$LoginActivity$LoginActivityRegisterView$1zSGYizDeZnhefZSwKFasIIewOY(this));
                    builder.setNegativeButton(LocaleController.getString("Decline", NUM), new -$$Lambda$LoginActivity$LoginActivityRegisterView$q2tVNOkdhWLHwjQp16kkC5ykcIE(this));
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.this$0.currentTermsOfService.text);
                MessageObject.addEntitiesToText(spannableStringBuilder, this.this$0.currentTermsOfService.entities, false, 0, false, false, false);
                builder.setMessage(spannableStringBuilder);
                this.this$0.showDialog(builder.create());
            }
        }

        public /* synthetic */ void lambda$showTermsOfService$0$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed();
        }

        public /* synthetic */ void lambda$showTermsOfService$3$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
            Builder builder = new Builder(this.this$0.getParentActivity());
            builder.setTitle(LocaleController.getString("TermsOfService", NUM));
            builder.setMessage(LocaleController.getString("TosDecline", NUM));
            builder.setPositiveButton(LocaleController.getString("SignUp", NUM), new -$$Lambda$LoginActivity$LoginActivityRegisterView$kqjdj9pPpsBrHJ9ZRVIehID5ZAc(this));
            builder.setNegativeButton(LocaleController.getString("Decline", NUM), new -$$Lambda$LoginActivity$LoginActivityRegisterView$XXjD9aNfiwvZEhE6clKqUdqJ0uI(this));
            this.this$0.showDialog(builder.create());
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
            this.this$0.currentTermsOfService.popup = false;
            onNextPressed();
        }

        public /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, null, true);
        }

        public LoginActivityRegisterView(LoginActivity loginActivity, Context context) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            setOrientation(1);
            this.imageUpdater = new ImageUpdater();
            this.imageUpdater.setSearchAvailable(false);
            this.imageUpdater.setUploadAfterSelect(false);
            ImageUpdater imageUpdater = this.imageUpdater;
            imageUpdater.parentFragment = loginActivity2;
            imageUpdater.delegate = this;
            this.textView = new TextView(context2);
            this.textView.setText(LocaleController.getString("RegisterText2", NUM));
            String str = "windowBackgroundWhiteGrayText6";
            this.textView.setTextColor(Theme.getColor(str));
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setTextSize(1, 14.0f);
            addView(this.textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 0, 0, 0));
            FrameLayout frameLayout = new FrameLayout(context2);
            addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 21.0f, 0.0f, 0.0f));
            this.avatarDrawable = new AvatarDrawable();
            this.avatarImage = new BackupImageView(context2) {
                public void invalidate() {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                public void invalidate(int i, int i2, int i3, int i4) {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(i, i2, i3, i4);
                }
            };
            this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, null, null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            this.avatarOverlay = new View(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (LoginActivityRegisterView.this.avatarImage != null && LoginActivityRegisterView.this.avatarProgressView.getVisibility() == 0) {
                        paint.setAlpha((int) ((LoginActivityRegisterView.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f) * LoginActivityRegisterView.this.avatarProgressView.getAlpha()));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            frameLayout.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            this.avatarOverlay.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivityRegisterView$UrLK1f4-ouBTRVpCoDl3SfCgewQ(this));
            this.avatarEditor = new ImageView(context2) {
                public void invalidate(int i, int i2, int i3, int i4) {
                    super.invalidate(i, i2, i3, i4);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor.setScaleType(ScaleType.CENTER);
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            this.avatarProgressView = new RadialProgressView(context2) {
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            showAvatarProgress(false, false);
            this.firstNameField = new EditTextBoldCursor(context2);
            String str2 = "windowBackgroundWhiteHintText";
            this.firstNameField.setHintTextColor(Theme.getColor(str2));
            String str3 = "windowBackgroundWhiteBlackText";
            this.firstNameField.setTextColor(Theme.getColor(str3));
            this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.firstNameField.setCursorColor(Theme.getColor(str3));
            this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.firstNameField.setCursorWidth(1.5f);
            this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
            this.firstNameField.setImeOptions(NUM);
            this.firstNameField.setTextSize(1, 17.0f);
            this.firstNameField.setMaxLines(1);
            this.firstNameField.setInputType(8192);
            frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 85.0f, 0.0f, LocaleController.isRTL ? 85.0f : 0.0f, 0.0f));
            this.firstNameField.setOnEditorActionListener(new -$$Lambda$LoginActivity$LoginActivityRegisterView$gWZuHFySSOyrQEK-aMCkRgvER6k(this));
            this.lastNameField = new EditTextBoldCursor(context2);
            this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
            this.lastNameField.setHintTextColor(Theme.getColor(str2));
            this.lastNameField.setTextColor(Theme.getColor(str3));
            this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.lastNameField.setCursorColor(Theme.getColor(str3));
            this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.lastNameField.setCursorWidth(1.5f);
            this.lastNameField.setImeOptions(NUM);
            this.lastNameField.setTextSize(1, 17.0f);
            this.lastNameField.setMaxLines(1);
            this.lastNameField.setInputType(8192);
            frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 85.0f, 51.0f, LocaleController.isRTL ? 85.0f : 0.0f, 0.0f));
            this.lastNameField.setOnEditorActionListener(new -$$Lambda$LoginActivity$LoginActivityRegisterView$EHan9TMOJ2z2Np-xyuMTnMKxl24(this));
            this.wrongNumber = new TextView(context2);
            this.wrongNumber.setText(LocaleController.getString("CancelRegistration", NUM));
            this.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.wrongNumber.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.wrongNumber.setTextSize(1, 14.0f);
            this.wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            this.wrongNumber.setVisibility(8);
            addView(this.wrongNumber, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 20, 0, 0));
            this.wrongNumber.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivityRegisterView$9SHAtLUticbvQmKKGk0ALjL8TaM(this));
            FrameLayout frameLayout2 = new FrameLayout(context2);
            frameLayout2.setClipToPadding(false);
            frameLayout2.setPadding(0, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(100.0f), AndroidUtilities.dp(16.0f));
            addView(frameLayout2, LayoutHelper.createLinear(-1, -1, 83));
            this.privacyView = new TextView(context2);
            this.privacyView.setTextColor(Theme.getColor(str));
            this.privacyView.setMovementMethod(new LinkMovementMethodMy());
            this.privacyView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            this.privacyView.setTextSize(1, 14.0f);
            this.privacyView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            frameLayout2.addView(this.privacyView, LayoutHelper.createFrame(-2, -2, 83));
            String string = LocaleController.getString("TermsOfServiceLogin", NUM);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
            int indexOf = string.indexOf(42);
            int lastIndexOf = string.lastIndexOf(42);
            if (!(indexOf == -1 || lastIndexOf == -1 || indexOf == lastIndexOf)) {
                spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, "");
                spannableStringBuilder.replace(indexOf, indexOf + 1, "");
                spannableStringBuilder.setSpan(new LinkSpan(), indexOf, lastIndexOf - 1, 33);
            }
            this.privacyView.setText(spannableStringBuilder);
        }

        public /* synthetic */ void lambda$new$5$LoginActivity$LoginActivityRegisterView(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new -$$Lambda$LoginActivity$LoginActivityRegisterView$4Yij9Lzn53ZtYUwU1guXgPxIWfc(this));
        }

        public /* synthetic */ void lambda$null$4$LoginActivity$LoginActivityRegisterView() {
            this.avatar = null;
            this.avatarBig = null;
            this.uploadedAvatar = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage(null, null, this.avatarDrawable, null);
            this.avatarEditor.setImageResource(NUM);
        }

        public /* synthetic */ boolean lambda$new$6$LoginActivity$LoginActivityRegisterView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.lastNameField.requestFocus();
            return true;
        }

        public /* synthetic */ boolean lambda$new$7$LoginActivity$LoginActivityRegisterView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ void lambda$new$8$LoginActivity$LoginActivityRegisterView(View view) {
            if (this.this$0.doneProgressView.getTag() == null) {
                onBackPressed(false);
            }
        }

        public void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityRegisterView$29x-JtuV_r7woPg6Q9kl8QOB7qw(this, photoSize2, photoSize));
        }

        public /* synthetic */ void lambda$didUploadPhoto$9$LoginActivity$LoginActivityRegisterView(PhotoSize photoSize, PhotoSize photoSize2) {
            this.avatar = photoSize.location;
            this.avatarBig = photoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, null);
        }

        private void showAvatarProgress(final boolean z, boolean z2) {
            if (this.avatarEditor != null) {
                AnimatorSet animatorSet = this.avatarAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.avatarAnimation = null;
                }
                if (z2) {
                    this.avatarAnimation = new AnimatorSet();
                    AnimatorSet animatorSet2;
                    Animator[] animatorArr;
                    if (z) {
                        this.avatarProgressView.setVisibility(0);
                        animatorSet2 = this.avatarAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                        animatorSet2.playTogether(animatorArr);
                    } else {
                        this.avatarEditor.setVisibility(0);
                        animatorSet2 = this.avatarAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                        animatorSet2.playTogether(animatorArr);
                    }
                    this.avatarAnimation.setDuration(180);
                    this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (LoginActivityRegisterView.this.avatarAnimation != null && LoginActivityRegisterView.this.avatarEditor != null) {
                                if (z) {
                                    LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                                } else {
                                    LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                                }
                                LoginActivityRegisterView.this.avatarAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            LoginActivityRegisterView.this.avatarAnimation = null;
                        }
                    });
                    this.avatarAnimation.start();
                } else if (z) {
                    this.avatarEditor.setAlpha(1.0f);
                    this.avatarEditor.setVisibility(4);
                    this.avatarProgressView.setAlpha(1.0f);
                    this.avatarProgressView.setVisibility(0);
                } else {
                    this.avatarEditor.setAlpha(1.0f);
                    this.avatarEditor.setVisibility(0);
                    this.avatarProgressView.setAlpha(0.0f);
                    this.avatarProgressView.setVisibility(4);
                }
            }
        }

        public boolean onBackPressed(boolean z) {
            if (z) {
                this.this$0.needHideProgress(true);
                this.nextPressed = false;
                this.currentParams = null;
                return true;
            }
            Builder builder = new Builder(this.this$0.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("AreYouSureRegistration", NUM));
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new -$$Lambda$LoginActivity$LoginActivityRegisterView$tgsdp57qGOzyamGoBJL7DAG_UO0(this));
            builder.setPositiveButton(LocaleController.getString("Continue", NUM), null);
            this.this$0.showDialog(builder.create());
            return false;
        }

        public /* synthetic */ void lambda$onBackPressed$10$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, null, true);
            hidePrivacyView();
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onShow() {
            super.onShow();
            if (this.privacyView != null) {
                if (this.this$0.restoringState) {
                    this.privacyView.setAlpha(1.0f);
                } else {
                    this.privacyView.setAlpha(0.0f);
                    this.privacyView.animate().alpha(1.0f).setDuration(200).setStartDelay(300).setInterpolator(AndroidUtilities.decelerateInterpolator).start();
                }
            }
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                editTextBoldCursor = this.firstNameField;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityRegisterView$mhiqEuaO3fJj3ukKBPnIzQ_CLASSNAMEQ(this), 100);
        }

        public /* synthetic */ void lambda$onShow$11$LoginActivity$LoginActivityRegisterView() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                editTextBoldCursor = this.firstNameField;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
            }
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                String str = "";
                this.firstNameField.setText(str);
                this.lastNameField.setText(str);
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.currentParams = bundle;
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                if (this.this$0.currentTermsOfService != null && this.this$0.currentTermsOfService.popup) {
                    showTermsOfService(true);
                } else if (this.firstNameField.length() == 0) {
                    this.this$0.onFieldError(this.firstNameField);
                } else {
                    this.nextPressed = true;
                    TL_auth_signUp tL_auth_signUp = new TL_auth_signUp();
                    tL_auth_signUp.phone_code_hash = this.phoneHash;
                    tL_auth_signUp.phone_number = this.requestPhone;
                    tL_auth_signUp.first_name = this.firstNameField.getText().toString();
                    tL_auth_signUp.last_name = this.lastNameField.getText().toString();
                    this.this$0.needShowProgress(0);
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_signUp, new -$$Lambda$LoginActivity$LoginActivityRegisterView$Y8QUBO4NtlyD_zNIRNXkfwwL7ys(this), 10);
                }
            }
        }

        public /* synthetic */ void lambda$onNextPressed$14$LoginActivity$LoginActivityRegisterView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityRegisterView$bkk0nIeZFEnzFGrxNX-KQWeY7Po(this, tLObject, tL_error));
        }

        public /* synthetic */ void lambda$null$13$LoginActivity$LoginActivityRegisterView(TLObject tLObject, TL_error tL_error) {
            this.nextPressed = false;
            if (tLObject instanceof TL_auth_authorization) {
                hidePrivacyView();
                this.this$0.showDoneButton(false, true);
                postDelayed(new -$$Lambda$LoginActivity$LoginActivityRegisterView$JECz43rHd9EVr-eNfom9CNE-xiI(this, tLObject), 150);
                if (this.avatarBig != null) {
                    MessagesController.getInstance(this.this$0.currentAccount).uploadAndApplyUserAvatar(this.avatarBig);
                    return;
                }
                return;
            }
            this.this$0.needHideProgress(false);
            String str = "AppName";
            if (tL_error.text.contains("PHONE_NUMBER_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if (tL_error.text.contains("PHONE_CODE_EMPTY") || tL_error.text.contains("PHONE_CODE_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.getString("InvalidCode", NUM));
            } else if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.getString("CodeExpired", NUM));
            } else if (tL_error.text.contains("FIRSTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.getString("InvalidFirstName", NUM));
            } else if (tL_error.text.contains("LASTNAME_INVALID")) {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.getString("InvalidLastName", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), tL_error.text);
            }
        }

        public /* synthetic */ void lambda$null$12$LoginActivity$LoginActivityRegisterView(TLObject tLObject) {
            this.this$0.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.this$0.fragmentView.findFocus());
            this.this$0.onAuthSuccess((TL_auth_authorization) tLObject, true);
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.firstNameField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("registerview_first", obj);
            }
            obj = this.lastNameField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("registerview_last", obj);
            }
            if (this.this$0.currentTermsOfService != null) {
                SerializedData serializedData = new SerializedData(this.this$0.currentTermsOfService.getObjectSize());
                this.this$0.currentTermsOfService.serializeToStream(serializedData);
                bundle.putString("terms", Base64.encodeToString(serializedData.toByteArray(), 0));
                serializedData.cleanup();
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("registerview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            String string;
            this.currentParams = bundle.getBundle("registerview_params");
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            try {
                string = bundle.getString("terms");
                if (string != null) {
                    byte[] decode = Base64.decode(string, 0);
                    if (decode != null) {
                        SerializedData serializedData = new SerializedData(decode);
                        this.this$0.currentTermsOfService = TL_help_termsOfService.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        serializedData.cleanup();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            string = bundle.getString("registerview_first");
            if (string != null) {
                this.firstNameField.setText(string);
            }
            String string2 = bundle.getString("registerview_last");
            if (string2 != null) {
                this.lastNameField.setText(string2);
            }
        }

        private void hidePrivacyView() {
            this.privacyView.animate().alpha(0.0f).setDuration(150).setStartDelay(0).setInterpolator(AndroidUtilities.accelerateInterpolator).start();
        }
    }

    public class LoginActivityResetWaitView extends SlideView {
        private TextView confirmTextView;
        private Bundle currentParams;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;
        private TextView resetAccountTime;
        private int startTime;
        final /* synthetic */ LoginActivity this$0;
        private Runnable timeRunnable;
        private int waitTime;

        public boolean needBackButton() {
            return true;
        }

        public LoginActivityResetWaitView(LoginActivity loginActivity, Context context) {
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            String str = "windowBackgroundWhiteGrayText6";
            this.confirmTextView.setTextColor(Theme.getColor(str));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.resetAccountText = new TextView(context2);
            this.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountText.setTextColor(Theme.getColor(str));
            this.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", NUM));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountText, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 24, 0, 0));
            this.resetAccountTime = new TextView(context2);
            this.resetAccountTime.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountTime.setTextColor(Theme.getColor(str));
            this.resetAccountTime.setTextSize(1, 14.0f);
            this.resetAccountTime.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountTime, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 2, 0, 0));
            this.resetAccountButton = new TextView(context2);
            this.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", NUM));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            TextView textView = this.resetAccountButton;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 7, 0, 0));
            this.resetAccountButton.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivityResetWaitView$8W-Hvar_tcDgJINwVsj88SoCtEEI(this));
        }

        public /* synthetic */ void lambda$new$3$LoginActivity$LoginActivityResetWaitView(View view) {
            if (this.this$0.doneProgressView.getTag() == null && Math.abs(ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime) >= this.waitTime) {
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new -$$Lambda$LoginActivity$LoginActivityResetWaitView$06c4N1-a79zd80tl7oqx4H1YDGI(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                this.this$0.showDialog(builder.create());
            }
        }

        public /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityResetWaitView(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TL_account_deleteAccount tL_account_deleteAccount = new TL_account_deleteAccount();
            tL_account_deleteAccount.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_deleteAccount, new -$$Lambda$LoginActivity$LoginActivityResetWaitView$VRRsiuZ_SrcrIpAgJJI-2VPK-uo(this), 10);
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityResetWaitView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivityResetWaitView$ehRWxs7c1MtBNOs2EfgHKXhZShY(this, tL_error));
        }

        public /* synthetic */ void lambda$null$0$LoginActivity$LoginActivityResetWaitView(TL_error tL_error) {
            this.this$0.needHideProgress(false);
            if (tL_error == null) {
                Bundle bundle = new Bundle();
                bundle.putString("phoneFormated", this.requestPhone);
                bundle.putString("phoneHash", this.phoneHash);
                bundle.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, bundle, false);
                return;
            }
            String str = "AppName";
            if (tL_error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString(str, NUM), tL_error.text);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", NUM);
        }

        private void updateTimeText() {
            int max = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int i = max / 86400;
            int i2 = max - (86400 * i);
            int i3 = i2 / 3600;
            i2 = (i2 - (i3 * 3600)) / 60;
            int i4 = max % 60;
            String str = "MinutesBold";
            String str2 = "HoursBold";
            String str3 = " ";
            StringBuilder stringBuilder;
            if (i != 0) {
                TextView textView = this.resetAccountTime;
                stringBuilder = new StringBuilder();
                stringBuilder.append(LocaleController.formatPluralString("DaysBold", i));
                stringBuilder.append(str3);
                stringBuilder.append(LocaleController.formatPluralString(str2, i3));
                stringBuilder.append(str3);
                stringBuilder.append(LocaleController.formatPluralString(str, i2));
                textView.setText(AndroidUtilities.replaceTags(stringBuilder.toString()));
            } else {
                TextView textView2 = this.resetAccountTime;
                stringBuilder = new StringBuilder();
                stringBuilder.append(LocaleController.formatPluralString(str2, i3));
                stringBuilder.append(str3);
                stringBuilder.append(LocaleController.formatPluralString(str, i2));
                stringBuilder.append(str3);
                stringBuilder.append(LocaleController.formatPluralString("SecondsBold", i4));
                textView2.setText(AndroidUtilities.replaceTags(stringBuilder.toString()));
            }
            String str4;
            if (max > 0) {
                str4 = "windowBackgroundWhiteGrayText6";
                this.resetAccountButton.setTag(str4);
                this.resetAccountButton.setTextColor(Theme.getColor(str4));
                return;
            }
            str4 = "windowBackgroundWhiteRedText6";
            this.resetAccountButton.setTag(str4);
            this.resetAccountButton.setTextColor(Theme.getColor(str4));
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.currentParams = bundle;
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                this.startTime = bundle.getInt("startTime");
                this.waitTime = bundle.getInt("waitTime");
                TextView textView = this.confirmTextView;
                Object[] objArr = new Object[1];
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(this.requestPhone);
                objArr[0] = LocaleController.addNbsp(instance.format(stringBuilder.toString()));
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", NUM, objArr)));
                updateTimeText();
                this.timeRunnable = new Runnable() {
                    public void run() {
                        if (LoginActivityResetWaitView.this.timeRunnable == this) {
                            LoginActivityResetWaitView.this.updateTimeText();
                            AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                        }
                    }
                };
                AndroidUtilities.runOnUIThread(this.timeRunnable, 1000);
            }
        }

        public boolean onBackPressed(boolean z) {
            this.this$0.needHideProgress(true);
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
            return true;
        }

        public void saveStateParams(Bundle bundle) {
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("resetview_params", bundle2);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("resetview_params");
            bundle = this.currentParams;
            if (bundle != null) {
                setParams(bundle, true);
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        private String catchedPhone;
        private EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        private int codeTime = 15000;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private String emailPhone;
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
        private String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        private int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private boolean waitingForEvent;

        static /* synthetic */ void lambda$onBackPressed$10(TLObject tLObject, TL_error tL_error) {
        }

        public boolean needBackButton() {
            return true;
        }

        public LoginActivitySmsView(LoginActivity loginActivity, Context context, int i) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            this.currentType = i;
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
            if (this.currentType == 3) {
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
                if (this.currentType == 1) {
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
            if (this.currentType == 3) {
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
            if (this.currentType == 3) {
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
            if (this.currentType == 1) {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", NUM));
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", NUM));
            }
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49));
            this.problemText.setOnClickListener(new -$$Lambda$LoginActivity$LoginActivitySmsView$WDlczSWG-Xmyhu8bi5YJvMI6JBs(this));
        }

        public /* synthetic */ void lambda$new$0$LoginActivity$LoginActivitySmsView(View view) {
            if (!this.nextPressed) {
                Object obj = ((this.nextType == 4 && this.currentType == 2) || this.nextType == 0) ? 1 : null;
                if (obj != null) {
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
                        stringBuilder.append(this.emailPhone);
                        intent.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Phone: ");
                        stringBuilder.append(this.requestPhone);
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
                        this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("NoMailInstalled", NUM));
                    }
                } else if (this.this$0.doneProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (this.currentType != 3) {
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
            if (this.currentType != 3 && this.blueImageView != null) {
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

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TL_auth_resendCode tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.requestPhone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new -$$Lambda$LoginActivity$LoginActivitySmsView$7Coa87SRXNZ2UoSLGVeBv7VQ3Fs(this, bundle), 10);
            this.this$0.needShowProgress(0);
        }

        public /* synthetic */ void lambda$resendCode$2$LoginActivity$LoginActivitySmsView(Bundle bundle, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$PO7r5AWMTSHLIApv2-6O4ar48qM(this, tL_error, bundle, tLObject));
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivitySmsView(TL_error tL_error, Bundle bundle, TLObject tLObject) {
            this.nextPressed = false;
            if (tL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
            } else {
                String str = tL_error.text;
                if (str != null) {
                    String str2 = "AppName";
                    if (str.contains("PHONE_NUMBER_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString(str2, NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                    } else if (tL_error.text.contains("PHONE_CODE_EMPTY") || tL_error.text.contains("PHONE_CODE_INVALID")) {
                        this.this$0.needShowAlert(LocaleController.getString(str2, NUM), LocaleController.getString("InvalidCode", NUM));
                    } else if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                        onBackPressed(true);
                        this.this$0.setPage(0, true, null, true);
                        this.this$0.needShowAlert(LocaleController.getString(str2, NUM), LocaleController.getString("CodeExpired", NUM));
                    } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                        this.this$0.needShowAlert(LocaleController.getString(str2, NUM), LocaleController.getString("FloodWait", NUM));
                    } else if (tL_error.code != -1000) {
                        LoginActivity loginActivity = this.this$0;
                        String string = LocaleController.getString(str2, NUM);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
                        stringBuilder.append("\n");
                        stringBuilder.append(tL_error.text);
                        loginActivity.needShowAlert(string, stringBuilder.toString());
                    }
                }
            }
            this.this$0.needHideProgress(false);
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            Bundle bundle2 = bundle;
            if (bundle2 != null) {
                int i;
                this.waitingForEvent = true;
                int i2 = this.currentType;
                if (i2 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle2;
                this.phone = bundle2.getString("phone");
                this.emailPhone = bundle2.getString("ephone");
                this.requestPhone = bundle2.getString("phoneFormated");
                this.phoneHash = bundle2.getString("phoneHash");
                this.time = bundle2.getInt("timeout");
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
                        if (this.currentType == 3) {
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
                                if (!LoginActivitySmsView.this.ignoreOnTextChange) {
                                    int length = editable.length();
                                    if (length >= 1) {
                                        if (length > 1) {
                                            String obj = editable.toString();
                                            LoginActivitySmsView.this.ignoreOnTextChange = true;
                                            for (int i = 0; i < Math.min(LoginActivitySmsView.this.length - i, length); i++) {
                                                if (i == 0) {
                                                    editable.replace(0, length, obj.substring(i, i + 1));
                                                } else {
                                                    LoginActivitySmsView.this.codeField[i + i].setText(obj.substring(i, i + 1));
                                                }
                                            }
                                            LoginActivitySmsView.this.ignoreOnTextChange = false;
                                        }
                                        if (i != LoginActivitySmsView.this.length - 1) {
                                            LoginActivitySmsView.this.codeField[i + 1].setSelection(LoginActivitySmsView.this.codeField[i + 1].length());
                                            LoginActivitySmsView.this.codeField[i + 1].requestFocus();
                                        }
                                        if ((i == LoginActivitySmsView.this.length - 1 || (i == LoginActivitySmsView.this.length - 2 && length >= 2)) && LoginActivitySmsView.this.getCode().length() == LoginActivitySmsView.this.length) {
                                            LoginActivitySmsView.this.onNextPressed();
                                        }
                                    }
                                }
                            }
                        });
                        this.codeField[i].setOnKeyListener(new -$$Lambda$LoginActivity$LoginActivitySmsView$W4f4bbr6ANrn17O_fi8CZ_C8uvk(this, i));
                        this.codeField[i].setOnEditorActionListener(new -$$Lambda$LoginActivity$LoginActivitySmsView$f1ikQgn9Rce7rxNwD6eW9NGEWc4(this));
                        i++;
                    }
                }
                ProgressView progressView = this.progressView;
                if (progressView != null) {
                    progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String format = PhoneFormat.getInstance().format(this.phone);
                    int i4 = this.currentType;
                    if (i4 == 1) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", NUM));
                    } else if (i4 == 2) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(format)));
                    } else if (i4 == 3) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(format)));
                    } else if (i4 == 4) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(format)));
                    }
                    this.confirmTextView.setText(charSequence);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    i = this.currentType;
                    if (i == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else {
                        TextView textView;
                        String str2 = "SmsText";
                        String str3 = "CallText";
                        CharSequence charSequence2 = null;
                        if (i == 3) {
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
                                if (z) {
                                    charSequence2 = AndroidUtilities.obtainLoginPhoneCall(this.pattern);
                                }
                                if (charSequence2 != null) {
                                    this.ignoreOnTextChange = true;
                                    this.codeField[0].setText(charSequence2);
                                    this.ignoreOnTextChange = false;
                                    onNextPressed();
                                } else {
                                    format = this.catchedPhone;
                                    if (format != null) {
                                        this.ignoreOnTextChange = true;
                                        this.codeField[0].setText(format);
                                        this.ignoreOnTextChange = false;
                                        onNextPressed();
                                    } else {
                                        createTimer();
                                    }
                                }
                            }
                        }
                        if (this.currentType == 2) {
                            i = this.nextType;
                            if (i == 4 || i == 3) {
                                this.timeText.setText(LocaleController.formatString(str3, NUM, Integer.valueOf(2), Integer.valueOf(0)));
                                this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                                textView = this.timeText;
                                if (this.time >= 1000) {
                                    i3 = 0;
                                }
                                textView.setVisibility(i3);
                                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                                String string = sharedPreferences.getString("sms_hash", null);
                                if (!TextUtils.isEmpty(string)) {
                                    format = sharedPreferences.getString("sms_hash_code", null);
                                    if (format != null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(string);
                                        stringBuilder.append("|");
                                        if (format.contains(stringBuilder.toString())) {
                                            charSequence2 = format.substring(format.indexOf(124) + 1);
                                        }
                                    }
                                }
                                if (charSequence2 != null) {
                                    this.codeField[0].setText(charSequence2);
                                    onNextPressed();
                                } else {
                                    createTimer();
                                }
                            }
                        }
                        if (this.currentType == 4 && this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString(str2, NUM, Integer.valueOf(2), Integer.valueOf(0)));
                            this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                            textView = this.timeText;
                            if (this.time >= 1000) {
                                i3 = 0;
                            }
                            textView.setVisibility(i3);
                            createTimer();
                        } else {
                            this.timeText.setVisibility(8);
                            this.problemText.setVisibility(8);
                            createCodeTimer();
                        }
                    }
                }
            }
        }

        public /* synthetic */ boolean lambda$setParams$3$LoginActivity$LoginActivitySmsView(int i, View view, int i2, KeyEvent keyEvent) {
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

        public /* synthetic */ boolean lambda$setParams$4$LoginActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
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
                        AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$4$Fr5toso-Gx7wT-8YsmrVs3ysx4Y(this));
                    }

                    public /* synthetic */ void lambda$run$0$LoginActivity$LoginActivitySmsView$4() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$5200 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTimeMillis);
                        access$5200 = currentTimeMillis - access$5200;
                        LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                        LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                        double access$5300 = (double) loginActivitySmsView.codeTime;
                        Double.isNaN(access$5300);
                        loginActivitySmsView.codeTime = (int) (access$5300 - access$5200);
                        if (LoginActivitySmsView.this.codeTime <= 1000) {
                            LoginActivitySmsView.this.problemText.setVisibility(0);
                            LoginActivitySmsView.this.timeText.setVisibility(8);
                            LoginActivitySmsView.this.destroyCodeTimer();
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
                ProgressView progressView = this.progressView;
                if (progressView != null) {
                    progressView.resetProgressAnimation();
                }
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$5$yhZ0JlUwl5Kkxw-R7O3sYzK3tvc(this));
                        }
                    }

                    public /* synthetic */ void lambda$run$2$LoginActivity$LoginActivitySmsView$5() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$5800 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTimeMillis);
                        access$5800 = currentTimeMillis - access$5800;
                        LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                        LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                        double access$5900 = (double) loginActivitySmsView.time;
                        Double.isNaN(access$5900);
                        loginActivitySmsView.time = (int) (access$5900 - access$5800);
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int access$59002 = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(r0), Integer.valueOf(access$59002)));
                            } else if (LoginActivitySmsView.this.nextType == 2) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(r0), Integer.valueOf(access$59002)));
                            }
                            if (LoginActivitySmsView.this.progressView != null && !LoginActivitySmsView.this.progressView.isProgressAnimationRunning()) {
                                LoginActivitySmsView.this.progressView.startProgressAnimation(((long) LoginActivitySmsView.this.time) - 1000);
                                return;
                            }
                            return;
                        }
                        LoginActivitySmsView.this.destroyTimer();
                        if (LoginActivitySmsView.this.currentType == 3) {
                            AndroidUtilities.setWaitingForCall(false);
                            NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                            LoginActivitySmsView.this.waitingForEvent = false;
                            LoginActivitySmsView.this.destroyCodeTimer();
                            LoginActivitySmsView.this.resendCode();
                        } else if (LoginActivitySmsView.this.currentType != 2 && LoginActivitySmsView.this.currentType != 4) {
                        } else {
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2) {
                                if (LoginActivitySmsView.this.nextType == 4) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", NUM));
                                } else {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.getString("SendingSms", NUM));
                                }
                                LoginActivitySmsView.this.createCodeTimer();
                                TL_auth_resendCode tL_auth_resendCode = new TL_auth_resendCode();
                                tL_auth_resendCode.phone_number = LoginActivitySmsView.this.requestPhone;
                                tL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new -$$Lambda$LoginActivity$LoginActivitySmsView$5$Lug0N7IyxdRqJvwOmLwPEiL6cA0(this), 10);
                            } else if (LoginActivitySmsView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            }
                        }
                    }

                    public /* synthetic */ void lambda$null$1$LoginActivity$LoginActivitySmsView$5(TLObject tLObject, TL_error tL_error) {
                        if (tL_error != null && tL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$5$ch_quU2kHnrVJ_iz4zDaJIXUckM(this, tL_error));
                        }
                    }

                    public /* synthetic */ void lambda$null$0$LoginActivity$LoginActivitySmsView$5(TL_error tL_error) {
                        LoginActivitySmsView.this.lastError = tL_error.text;
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
            if (!this.nextPressed && this.this$0.currentViewNum >= 1 && this.this$0.currentViewNum <= 4) {
                String code = getCode();
                if (TextUtils.isEmpty(code)) {
                    this.this$0.onFieldError(this.codeFieldContainer);
                    return;
                }
                this.nextPressed = true;
                int i = this.currentType;
                if (i == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                TL_auth_signIn tL_auth_signIn = new TL_auth_signIn();
                tL_auth_signIn.phone_number = this.requestPhone;
                tL_auth_signIn.phone_code = code;
                tL_auth_signIn.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_signIn, new -$$Lambda$LoginActivity$LoginActivitySmsView$QuZqrlgFwDiyZUcg7gbDXVsiiUk(this, tL_auth_signIn), 10), false);
                this.this$0.showDoneButton(true, true);
            }
        }

        public /* synthetic */ void lambda$onNextPressed$8$LoginActivity$LoginActivitySmsView(TL_auth_signIn tL_auth_signIn, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$KDcbJgHhkQhO0H1lUrS7lHNHtoc(this, tL_error, tLObject, tL_auth_signIn));
        }

        /* JADX WARNING: Removed duplicated region for block: B:30:0x00b2  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x00a5  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00c6  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x00a5  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x00b2  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00c6  */
        /* JADX WARNING: Missing block: B:16:0x008a, code skipped:
            if (r7 != 2) goto L_0x008c;
     */
        /* JADX WARNING: Missing block: B:21:0x0094, code skipped:
            if (r7 != 3) goto L_0x0096;
     */
        /* JADX WARNING: Missing block: B:25:0x009c, code skipped:
            if (r5.nextType == 2) goto L_0x009e;
     */
        public /* synthetic */ void lambda$null$7$LoginActivity$LoginActivitySmsView(org.telegram.tgnet.TLRPC.TL_error r6, org.telegram.tgnet.TLObject r7, org.telegram.tgnet.TLRPC.TL_auth_signIn r8) {
            /*
            r5 = this;
            r0 = 3;
            r1 = 0;
            r2 = 1;
            if (r6 != 0) goto L_0x004c;
        L_0x0005:
            r5.nextPressed = r1;
            r6 = r5.this$0;
            r6.showDoneButton(r1, r2);
            r5.destroyTimer();
            r5.destroyCodeTimer();
            r6 = r7 instanceof org.telegram.tgnet.TLRPC.TL_auth_authorizationSignUpRequired;
            if (r6 == 0) goto L_0x0043;
        L_0x0016:
            r7 = (org.telegram.tgnet.TLRPC.TL_auth_authorizationSignUpRequired) r7;
            r6 = r7.terms_of_service;
            if (r6 == 0) goto L_0x0021;
        L_0x001c:
            r7 = r5.this$0;
            r7.currentTermsOfService = r6;
        L_0x0021:
            r6 = new android.os.Bundle;
            r6.<init>();
            r7 = r5.requestPhone;
            r3 = "phoneFormated";
            r6.putString(r3, r7);
            r7 = r5.phoneHash;
            r3 = "phoneHash";
            r6.putString(r3, r7);
            r7 = r8.phone_code;
            r8 = "code";
            r6.putString(r8, r7);
            r7 = r5.this$0;
            r8 = 5;
            r7.setPage(r8, r2, r6, r1);
            goto L_0x0192;
        L_0x0043:
            r6 = r5.this$0;
            r7 = (org.telegram.tgnet.TLRPC.TL_auth_authorization) r7;
            r6.onAuthSuccess(r7);
            goto L_0x0192;
        L_0x004c:
            r7 = r6.text;
            r5.lastError = r7;
            r3 = "SESSION_PASSWORD_NEEDED";
            r7 = r7.contains(r3);
            if (r7 == 0) goto L_0x0079;
        L_0x0058:
            r6 = new org.telegram.tgnet.TLRPC$TL_account_getPassword;
            r6.<init>();
            r7 = r5.this$0;
            r7 = r7.currentAccount;
            r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7);
            r1 = new org.telegram.ui.-$$Lambda$LoginActivity$LoginActivitySmsView$ClZInkb3pcX3d_IEp0qMTOZ0dwc;
            r1.<init>(r5, r8);
            r8 = 10;
            r7.sendRequest(r6, r1, r8);
            r5.destroyTimer();
            r5.destroyCodeTimer();
            goto L_0x0192;
        L_0x0079:
            r5.nextPressed = r1;
            r7 = r5.this$0;
            r7.showDoneButton(r1, r2);
            r7 = r5.currentType;
            r8 = 4;
            r3 = 2;
            if (r7 != r0) goto L_0x008c;
        L_0x0086:
            r7 = r5.nextType;
            if (r7 == r8) goto L_0x009e;
        L_0x008a:
            if (r7 == r3) goto L_0x009e;
        L_0x008c:
            r7 = r5.currentType;
            if (r7 != r3) goto L_0x0096;
        L_0x0090:
            r7 = r5.nextType;
            if (r7 == r8) goto L_0x009e;
        L_0x0094:
            if (r7 == r0) goto L_0x009e;
        L_0x0096:
            r7 = r5.currentType;
            if (r7 != r8) goto L_0x00a1;
        L_0x009a:
            r7 = r5.nextType;
            if (r7 != r3) goto L_0x00a1;
        L_0x009e:
            r5.createTimer();
        L_0x00a1:
            r7 = r5.currentType;
            if (r7 != r3) goto L_0x00b2;
        L_0x00a5:
            org.telegram.messenger.AndroidUtilities.setWaitingForSms(r2);
            r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r8 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
            r7.addObserver(r5, r8);
            goto L_0x00c0;
        L_0x00b2:
            if (r7 != r0) goto L_0x00c0;
        L_0x00b4:
            org.telegram.messenger.AndroidUtilities.setWaitingForCall(r2);
            r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r8 = org.telegram.messenger.NotificationCenter.didReceiveCall;
            r7.addObserver(r5, r8);
        L_0x00c0:
            r5.waitingForEvent = r2;
            r7 = r5.currentType;
            if (r7 == r0) goto L_0x0191;
        L_0x00c6:
            r7 = r6.text;
            r8 = "PHONE_NUMBER_INVALID";
            r7 = r7.contains(r8);
            r8 = NUM; // 0x7f0e00f9 float:1.8875543E38 double:1.0531622797E-314;
            r3 = "AppName";
            if (r7 == 0) goto L_0x00e9;
        L_0x00d5:
            r6 = r5.this$0;
            r7 = org.telegram.messenger.LocaleController.getString(r3, r8);
            r8 = NUM; // 0x7f0e0585 float:1.8877903E38 double:1.0531628547E-314;
            r2 = "InvalidPhoneNumber";
            r8 = org.telegram.messenger.LocaleController.getString(r2, r8);
            r6.needShowAlert(r7, r8);
            goto L_0x0191;
        L_0x00e9:
            r7 = r6.text;
            r4 = "PHONE_CODE_EMPTY";
            r7 = r7.contains(r4);
            if (r7 != 0) goto L_0x016a;
        L_0x00f3:
            r7 = r6.text;
            r4 = "PHONE_CODE_INVALID";
            r7 = r7.contains(r4);
            if (r7 == 0) goto L_0x00fe;
        L_0x00fd:
            goto L_0x016a;
        L_0x00fe:
            r7 = r6.text;
            r4 = "PHONE_CODE_EXPIRED";
            r7 = r7.contains(r4);
            if (r7 == 0) goto L_0x0124;
        L_0x0108:
            r5.onBackPressed(r2);
            r6 = r5.this$0;
            r7 = 0;
            r6.setPage(r1, r2, r7, r2);
            r6 = r5.this$0;
            r7 = org.telegram.messenger.LocaleController.getString(r3, r8);
            r8 = NUM; // 0x7f0e0307 float:1.887661E38 double:1.0531625395E-314;
            r2 = "CodeExpired";
            r8 = org.telegram.messenger.LocaleController.getString(r2, r8);
            r6.needShowAlert(r7, r8);
            goto L_0x0191;
        L_0x0124:
            r7 = r6.text;
            r2 = "FLOOD_WAIT";
            r7 = r7.startsWith(r2);
            if (r7 == 0) goto L_0x0141;
        L_0x012e:
            r6 = r5.this$0;
            r7 = org.telegram.messenger.LocaleController.getString(r3, r8);
            r8 = NUM; // 0x7f0e04c2 float:1.8877508E38 double:1.0531627584E-314;
            r2 = "FloodWait";
            r8 = org.telegram.messenger.LocaleController.getString(r2, r8);
            r6.needShowAlert(r7, r8);
            goto L_0x0191;
        L_0x0141:
            r7 = r5.this$0;
            r8 = org.telegram.messenger.LocaleController.getString(r3, r8);
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = NUM; // 0x7f0e0441 float:1.8877246E38 double:1.0531626947E-314;
            r4 = "ErrorOccurred";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r2.append(r3);
            r3 = "\n";
            r2.append(r3);
            r6 = r6.text;
            r2.append(r6);
            r6 = r2.toString();
            r7.needShowAlert(r8, r6);
            goto L_0x0191;
        L_0x016a:
            r6 = r5.this$0;
            r7 = org.telegram.messenger.LocaleController.getString(r3, r8);
            r8 = NUM; // 0x7f0e0582 float:1.8877897E38 double:1.0531628533E-314;
            r2 = "InvalidCode";
            r8 = org.telegram.messenger.LocaleController.getString(r2, r8);
            r6.needShowAlert(r7, r8);
            r6 = 0;
        L_0x017d:
            r7 = r5.codeField;
            r8 = r7.length;
            if (r6 >= r8) goto L_0x018c;
        L_0x0182:
            r7 = r7[r6];
            r8 = "";
            r7.setText(r8);
            r6 = r6 + 1;
            goto L_0x017d;
        L_0x018c:
            r6 = r7[r1];
            r6.requestFocus();
        L_0x0191:
            r2 = 0;
        L_0x0192:
            if (r2 == 0) goto L_0x019b;
        L_0x0194:
            r6 = r5.currentType;
            if (r6 != r0) goto L_0x019b;
        L_0x0198:
            org.telegram.messenger.AndroidUtilities.endIncomingCall();
        L_0x019b:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity$LoginActivitySmsView.lambda$null$7$LoginActivity$LoginActivitySmsView(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_auth_signIn):void");
        }

        public /* synthetic */ void lambda$null$6$LoginActivity$LoginActivitySmsView(TL_auth_signIn tL_auth_signIn, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$u6PnjqH_X1fZ6mUkFF5h0b6RQx0(this, tL_error, tLObject, tL_auth_signIn));
        }

        public /* synthetic */ void lambda$null$5$LoginActivity$LoginActivitySmsView(TL_error tL_error, TLObject tLObject, TL_auth_signIn tL_auth_signIn) {
            this.nextPressed = false;
            this.this$0.showDoneButton(false, true);
            if (tL_error == null) {
                TL_account_password tL_account_password = (TL_account_password) tLObject;
                if (TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, true)) {
                    Bundle bundle = new Bundle();
                    PasswordKdfAlgo passwordKdfAlgo = tL_account_password.current_algo;
                    if (passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                        TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo;
                        bundle.putString("current_salt1", Utilities.bytesToHex(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1));
                        bundle.putString("current_salt2", Utilities.bytesToHex(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt2));
                        bundle.putString("current_p", Utilities.bytesToHex(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p));
                        bundle.putInt("current_g", tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.g);
                        bundle.putString("current_srp_B", Utilities.bytesToHex(tL_account_password.srp_B));
                        bundle.putLong("current_srp_id", tL_account_password.srp_id);
                        bundle.putInt("passwordType", 1);
                    }
                    String str = tL_account_password.hint;
                    String str2 = "";
                    if (str == null) {
                        str = str2;
                    }
                    bundle.putString("hint", str);
                    str = tL_account_password.email_unconfirmed_pattern;
                    if (str == null) {
                        str = str2;
                    }
                    bundle.putString("email_unconfirmed_pattern", str);
                    bundle.putString("phoneFormated", this.requestPhone);
                    bundle.putString("phoneHash", this.phoneHash);
                    bundle.putString("code", tL_auth_signIn.phone_code);
                    bundle.putInt("has_recovery", tL_account_password.has_recovery);
                    this.this$0.setPage(6, true, bundle, false);
                } else {
                    AlertsCreator.showUpdateAppAlert(this.this$0.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                    return;
                }
            }
            this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), tL_error.text);
        }

        public boolean onBackPressed(boolean z) {
            if (z) {
                this.nextPressed = false;
                this.this$0.needHideProgress(true);
                TL_auth_cancelCode tL_auth_cancelCode = new TL_auth_cancelCode();
                tL_auth_cancelCode.phone_number = this.requestPhone;
                tL_auth_cancelCode.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_cancelCode, -$$Lambda$LoginActivity$LoginActivitySmsView$m2HPGKwCNffO48k34_Pfyo6t35w.INSTANCE, 10);
                destroyTimer();
                destroyCodeTimer();
                this.currentParams = null;
                int i = this.currentType;
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
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new -$$Lambda$LoginActivity$LoginActivitySmsView$hHDcDlk50TjqyjTury7-j6-VypU(this));
            this.this$0.showDialog(builder.create());
            return false;
        }

        public /* synthetic */ void lambda$onBackPressed$9$LoginActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, null, true);
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            int i = this.currentType;
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
            if (this.currentType != 3) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$LoginActivitySmsView$Bj-lU3gGH1xepmEk_N-1k9TPJGE(this), 100);
            }
        }

        public /* synthetic */ void lambda$onShow$11$LoginActivity$LoginActivitySmsView() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                int length = editTextBoldCursorArr.length - 1;
                while (length >= 0) {
                    if (length == 0 || this.codeField[length].length() != 0) {
                        this.codeField[length].requestFocus();
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        editTextBoldCursorArr2[length].setSelection(editTextBoldCursorArr2[length].length());
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
                            if (!this.pattern.equals("*")) {
                                this.catchedPhone = stringBuilder3;
                                AndroidUtilities.endIncomingCall();
                            }
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(stringBuilder3);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        }
                    }
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = getCode();
            if (code.length() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("smsview_code_");
                stringBuilder.append(this.currentType);
                bundle.putString(stringBuilder.toString(), code);
            }
            code = this.catchedPhone;
            if (code != null) {
                bundle.putString("catchedPhone", code);
            }
            if (this.currentParams != null) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("smsview_params_");
                stringBuilder2.append(this.currentType);
                bundle.putBundle(stringBuilder2.toString(), this.currentParams);
            }
            int i = this.time;
            if (i != 0) {
                bundle.putInt("time", i);
            }
            i = this.openTime;
            if (i != 0) {
                bundle.putInt("open", i);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("smsview_params_");
            stringBuilder.append(this.currentType);
            this.currentParams = bundle.getBundle(stringBuilder.toString());
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String string = bundle.getString("catchedPhone");
            if (string != null) {
                this.catchedPhone = string;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("smsview_code_");
            stringBuilder.append(this.currentType);
            string = bundle.getString(stringBuilder.toString());
            if (string != null) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr != null) {
                    editTextBoldCursorArr[0].setText(string);
                }
            }
            int i = bundle.getInt("time");
            if (i != 0) {
                this.time = i;
            }
            int i2 = bundle.getInt("open");
            if (i2 != 0) {
                this.openTime = i2;
            }
        }
    }

    public class PhoneView extends SlideView implements OnItemSelectedListener {
        private CheckBoxCell checkBoxCell;
        private EditTextBoldCursor codeField;
        private HashMap<String, String> codesMap = new HashMap();
        private ArrayList<String> countriesArray = new ArrayList();
        private HashMap<String, String> countriesMap = new HashMap();
        private TextView countryButton;
        private int countryState = 0;
        private boolean ignoreOnPhoneChange = false;
        private boolean ignoreOnTextChange = false;
        private boolean ignoreSelection = false;
        private boolean nextPressed = false;
        private HintEditText phoneField;
        private HashMap<String, String> phoneFormatMap = new HashMap();
        private TextView textView;
        private TextView textView2;
        final /* synthetic */ LoginActivity this$0;
        private View view;

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public PhoneView(LoginActivity loginActivity, Context context) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            setOrientation(1);
            this.countryButton = new TextView(context2);
            this.countryButton.setTextSize(1, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
            String str = "windowBackgroundWhiteBlackText";
            this.countryButton.setTextColor(Theme.getColor(str));
            this.countryButton.setMaxLines(1);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.countryButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 7));
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new -$$Lambda$LoginActivity$PhoneView$YKPRtOo2JwvFERoOBV4IqiJYOOU(this));
            this.view = new View(context2);
            this.view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            this.view.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
            addView(this.view, LayoutHelper.createLinear(-1, 1, 4.0f, -17.5f, 4.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            this.textView = new TextView(context2);
            this.textView.setText("+");
            this.textView.setTextColor(Theme.getColor(str));
            this.textView.setTextSize(1, 18.0f);
            linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditTextBoldCursor(context2);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(Theme.getColor(str));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.codeField.setCursorColor(Theme.getColor(str));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(NUM);
            this.codeField.setFilters(new InputFilter[]{new LengthFilter(5)});
            linearLayout.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            this.codeField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!PhoneView.this.ignoreOnTextChange) {
                        PhoneView.this.ignoreOnTextChange = true;
                        String stripExceptNumbers = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                        PhoneView.this.codeField.setText(stripExceptNumbers);
                        String str = null;
                        if (stripExceptNumbers.length() == 0) {
                            PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 1;
                        } else {
                            Object substring;
                            CharSequence charSequence;
                            Object obj;
                            int i = 4;
                            if (stripExceptNumbers.length() > 4) {
                                while (i >= 1) {
                                    substring = stripExceptNumbers.substring(0, i);
                                    if (((String) PhoneView.this.codesMap.get(substring)) != null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(stripExceptNumbers.substring(i));
                                        stringBuilder.append(PhoneView.this.phoneField.getText().toString());
                                        stripExceptNumbers = stringBuilder.toString();
                                        PhoneView.this.codeField.setText(substring);
                                        charSequence = stripExceptNumbers;
                                        obj = 1;
                                        break;
                                    }
                                    i--;
                                }
                                substring = stripExceptNumbers;
                                charSequence = null;
                                obj = null;
                                if (obj == null) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(substring.substring(1));
                                    stringBuilder2.append(PhoneView.this.phoneField.getText().toString());
                                    charSequence = stringBuilder2.toString();
                                    EditTextBoldCursor access$2200 = PhoneView.this.codeField;
                                    substring = substring.substring(0, 1);
                                    access$2200.setText(substring);
                                }
                            } else {
                                substring = stripExceptNumbers;
                                charSequence = null;
                                obj = null;
                            }
                            String str2 = (String) PhoneView.this.codesMap.get(substring);
                            String str3 = "WrongCountry";
                            if (str2 != null) {
                                int indexOf = PhoneView.this.countriesArray.indexOf(str2);
                                if (indexOf != -1) {
                                    PhoneView.this.ignoreSelection = true;
                                    PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(indexOf));
                                    String str4 = (String) PhoneView.this.phoneFormatMap.get(substring);
                                    HintEditText access$2400 = PhoneView.this.phoneField;
                                    if (str4 != null) {
                                        str = str4.replace('X', 8211);
                                    }
                                    access$2400.setHintText(str);
                                    PhoneView.this.countryState = 0;
                                } else {
                                    PhoneView.this.countryButton.setText(LocaleController.getString(str3, NUM));
                                    PhoneView.this.phoneField.setHintText(null);
                                    PhoneView.this.countryState = 2;
                                }
                            } else {
                                PhoneView.this.countryButton.setText(LocaleController.getString(str3, NUM));
                                PhoneView.this.phoneField.setHintText(null);
                                PhoneView.this.countryState = 2;
                            }
                            if (obj == null) {
                                PhoneView.this.codeField.setSelection(PhoneView.this.codeField.getText().length());
                            }
                            if (charSequence != null) {
                                PhoneView.this.phoneField.requestFocus();
                                PhoneView.this.phoneField.setText(charSequence);
                                PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                            }
                        }
                        PhoneView.this.ignoreOnTextChange = false;
                    }
                }
            });
            this.codeField.setOnEditorActionListener(new -$$Lambda$LoginActivity$PhoneView$s6X9t4lGGXuJqcRZlLSeul8QD9A(this));
            this.phoneField = new HintEditText(context2) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(Theme.getColor(str));
            this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.phoneField.setPadding(0, 0, 0, 0);
            this.phoneField.setCursorColor(Theme.getColor(str));
            this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.phoneField.setCursorWidth(1.5f);
            this.phoneField.setTextSize(1, 18.0f);
            this.phoneField.setMaxLines(1);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(NUM);
            linearLayout.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            this.phoneField.addTextChangedListener(new TextWatcher() {
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
                    if (!PhoneView.this.ignoreOnPhoneChange) {
                        StringBuilder stringBuilder;
                        int i;
                        int selectionStart = PhoneView.this.phoneField.getSelectionStart();
                        String obj = PhoneView.this.phoneField.getText().toString();
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
                        PhoneView.this.ignoreOnPhoneChange = true;
                        obj = PhoneView.this.phoneField.getHintText();
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
                        editable.replace(0, editable.length(), stringBuilder);
                        if (selectionStart >= 0) {
                            HintEditText access$2400 = PhoneView.this.phoneField;
                            if (selectionStart > PhoneView.this.phoneField.length()) {
                                selectionStart = PhoneView.this.phoneField.length();
                            }
                            access$2400.setSelection(selectionStart);
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            this.phoneField.setOnEditorActionListener(new -$$Lambda$LoginActivity$PhoneView$TEi0WT_UJWWkmE9ZUo0lhioWQRI(this));
            this.phoneField.setOnKeyListener(new -$$Lambda$LoginActivity$PhoneView$Qv5VLHdHxMblEcKPlwyU2WrV61c(this));
            this.textView2 = new TextView(context2);
            this.textView2.setText(LocaleController.getString("StartText", NUM));
            this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.textView2.setTextSize(1, 14.0f);
            this.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            if (loginActivity.newAccount) {
                this.checkBoxCell = new CheckBoxCell(context2, 2);
                this.checkBoxCell.setText(LocaleController.getString("SyncContacts", NUM), "", loginActivity.syncContacts, false);
                addView(this.checkBoxCell, LayoutHelper.createLinear(-2, -1, 51, 0, 0, 0, 0));
                this.checkBoxCell.setOnClickListener(new OnClickListener() {
                    private Toast visibleToast;

                    public void onClick(View view) {
                        if (PhoneView.this.this$0.getParentActivity() != null) {
                            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                            LoginActivity loginActivity = PhoneView.this.this$0;
                            loginActivity.syncContacts = loginActivity.syncContacts ^ 1;
                            checkBoxCell.setChecked(PhoneView.this.this$0.syncContacts, true);
                            try {
                                if (this.visibleToast != null) {
                                    this.visibleToast.cancel();
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            if (PhoneView.this.this$0.syncContacts) {
                                this.visibleToast = Toast.makeText(PhoneView.this.this$0.getParentActivity(), LocaleController.getString("SyncContactsOn", NUM), 0);
                                this.visibleToast.show();
                            } else {
                                this.visibleToast = Toast.makeText(PhoneView.this.this$0.getParentActivity(), LocaleController.getString("SyncContactsOff", NUM), 0);
                                this.visibleToast.show();
                            }
                        }
                    }
                });
            }
            HashMap hashMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
                while (true) {
                    str = bufferedReader.readLine();
                    if (str == null) {
                        break;
                    }
                    String[] split = str.split(";");
                    this.countriesArray.add(0, split[2]);
                    this.countriesMap.put(split[2], split[0]);
                    this.codesMap.put(split[0], split[2]);
                    if (split.length > 3) {
                        this.phoneFormatMap.put(split[0], split[3]);
                    }
                    hashMap.put(split[1], split[2]);
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            Collections.sort(this.countriesArray, -$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE);
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            loginActivity.getAccountInstance().getConnectionsManager().sendRequest(new TL_help_getNearestDc(), new -$$Lambda$LoginActivity$PhoneView$w7_kYbY4pRrWACLASSNAMExO5d-AXHa18(this, hashMap), 10);
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                this.phoneField.setHintText(null);
                this.countryState = 1;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                HintEditText hintEditText = this.phoneField;
                hintEditText.setSelection(hintEditText.length());
                return;
            }
            this.codeField.requestFocus();
        }

        public /* synthetic */ void lambda$new$2$LoginActivity$PhoneView(View view) {
            CountrySelectActivity countrySelectActivity = new CountrySelectActivity(true);
            countrySelectActivity.setCountrySelectActivityDelegate(new -$$Lambda$LoginActivity$PhoneView$7SKvAGybNL6bKIPOIaonyBBsyEE(this));
            this.this$0.presentFragment(countrySelectActivity);
        }

        public /* synthetic */ void lambda$null$1$LoginActivity$PhoneView(String str, String str2) {
            selectCountry(str, str2);
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$PhoneView$mxS5ICVDzCvqTR0SgGSNeONT9eA(this), 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        public /* synthetic */ void lambda$null$0$LoginActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        public /* synthetic */ boolean lambda$new$3$LoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        public /* synthetic */ boolean lambda$new$4$LoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        public /* synthetic */ boolean lambda$new$5$LoginActivity$PhoneView(View view, int i, KeyEvent keyEvent) {
            if (i != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField.dispatchKeyEvent(keyEvent);
            return true;
        }

        public /* synthetic */ void lambda$new$7$LoginActivity$PhoneView(HashMap hashMap, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$PhoneView$8N-9x5OyTcoij1cAYvw9kO6AGws(this, tLObject, hashMap));
        }

        public /* synthetic */ void lambda$null$6$LoginActivity$PhoneView(TLObject tLObject, HashMap hashMap) {
            if (tLObject != null) {
                TL_nearestDc tL_nearestDc = (TL_nearestDc) tLObject;
                if (this.codeField.length() == 0) {
                    setCountry(hashMap, tL_nearestDc.country.toUpperCase());
                }
            }
        }

        public void selectCountry(String str, String str2) {
            if (this.countriesArray.indexOf(str) != -1) {
                this.ignoreOnTextChange = true;
                str2 = (String) this.countriesMap.get(str);
                this.codeField.setText(str2);
                this.countryButton.setText(str);
                str = (String) this.phoneFormatMap.get(str2);
                this.phoneField.setHintText(str != null ? str.replace('X', 8211) : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }

        private void setCountry(HashMap<String, String> hashMap, String str) {
            String str2 = (String) hashMap.get(str);
            if (str2 != null && this.countriesArray.indexOf(str2) != -1) {
                this.codeField.setText((CharSequence) this.countriesMap.get(str2));
                this.countryState = 0;
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onNextPressed() {
            String str = "ephone";
            if (!(this.this$0.getParentActivity() == null || this.nextPressed)) {
                Object obj;
                Object obj2;
                Object obj3;
                String str2;
                String str3 = "phone";
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService(str3);
                if (BuildVars.DEBUG_VERSION) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("sim status = ");
                    stringBuilder.append(telephonyManager.getSimState());
                    FileLog.d(stringBuilder.toString());
                }
                int simState = telephonyManager.getSimState();
                Object obj4 = (simState == 1 || simState == 0 || telephonyManager.getPhoneType() == 0 || AndroidUtilities.isAirplaneModeOn()) ? null : 1;
                String str4 = "OK";
                String str5 = "AppName";
                if (VERSION.SDK_INT < 23 || obj4 == null) {
                    obj = 1;
                    obj2 = 1;
                    obj3 = 1;
                } else {
                    str2 = "android.permission.READ_PHONE_STATE";
                    obj = this.this$0.getParentActivity().checkSelfPermission(str2) == 0 ? 1 : null;
                    String str6 = "android.permission.CALL_PHONE";
                    obj3 = this.this$0.getParentActivity().checkSelfPermission(str6) == 0 ? 1 : null;
                    String str7 = "android.permission.READ_CALL_LOG";
                    obj2 = (VERSION.SDK_INT < 28 || this.this$0.getParentActivity().checkSelfPermission(str7) == 0) ? 1 : null;
                    if (this.this$0.checkPermissions) {
                        this.this$0.permissionsItems.clear();
                        if (obj == null) {
                            this.this$0.permissionsItems.add(str2);
                        }
                        if (obj3 == null) {
                            this.this$0.permissionsItems.add(str6);
                        }
                        if (obj2 == null) {
                            this.this$0.permissionsItems.add(str7);
                        }
                        if (!this.this$0.permissionsItems.isEmpty()) {
                            Object obj5;
                            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                            if (globalMainSettings.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale(str2) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale(str7)) {
                                globalMainSettings.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString(str5, NUM));
                                builder.setPositiveButton(LocaleController.getString(str4, NUM), null);
                                if (obj == null && (obj3 == null || obj2 == null)) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndLog", NUM));
                                } else if (obj3 == null || obj2 == null) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallLog", NUM));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                                }
                                LoginActivity loginActivity = this.this$0;
                                loginActivity.permissionsDialog = loginActivity.showDialog(builder.create());
                            } else {
                                try {
                                    this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[0]), 6);
                                } catch (Exception unused) {
                                    obj5 = null;
                                }
                            }
                            obj5 = 1;
                            if (obj5 != null) {
                                return;
                            }
                        }
                    }
                }
                int i = this.countryState;
                if (i == 1) {
                    this.this$0.needShowAlert(LocaleController.getString(str5, NUM), LocaleController.getString("ChooseCountry", NUM));
                } else if (i == 2 && !BuildVars.DEBUG_VERSION) {
                    this.this$0.needShowAlert(LocaleController.getString(str5, NUM), LocaleController.getString("WrongCountry", NUM));
                } else if (this.codeField.length() == 0) {
                    this.this$0.needShowAlert(LocaleController.getString(str5, NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else if (this.phoneField.length() == 0) {
                    this.this$0.onFieldError(this.phoneField);
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("");
                    stringBuilder2.append(this.codeField.getText());
                    stringBuilder2.append(this.phoneField.getText());
                    str2 = PhoneFormat.stripExceptNumbers(stringBuilder2.toString());
                    if (this.this$0.getParentActivity() instanceof LaunchActivity) {
                        for (int i2 = 0; i2 < 3; i2++) {
                            UserConfig instance = UserConfig.getInstance(i2);
                            if (instance.isClientActivated() && PhoneNumberUtils.compare(str2, instance.getCurrentUser().phone)) {
                                Builder builder2 = new Builder(this.this$0.getParentActivity());
                                builder2.setTitle(LocaleController.getString(str5, NUM));
                                builder2.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", NUM));
                                builder2.setPositiveButton(LocaleController.getString("AccountSwitch", NUM), new -$$Lambda$LoginActivity$PhoneView$dpjhAtsGXirSFaKNuV4Bo6d-RDQ(this, i2));
                                builder2.setNegativeButton(LocaleController.getString(str4, NUM), null);
                                this.this$0.showDialog(builder2.create());
                                return;
                            }
                        }
                    }
                    ConnectionsManager.getInstance(this.this$0.currentAccount).cleanup(false);
                    TL_auth_sendCode tL_auth_sendCode = new TL_auth_sendCode();
                    tL_auth_sendCode.api_hash = BuildVars.APP_HASH;
                    tL_auth_sendCode.api_id = BuildVars.APP_ID;
                    tL_auth_sendCode.phone_number = str2;
                    tL_auth_sendCode.settings = new TL_codeSettings();
                    TL_codeSettings tL_codeSettings = tL_auth_sendCode.settings;
                    boolean z = (obj4 == null || obj == null || obj3 == null || obj2 == null) ? false : true;
                    tL_codeSettings.allow_flashcall = z;
                    tL_auth_sendCode.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    String str8 = "sms_hash";
                    if (tL_auth_sendCode.settings.allow_app_hash) {
                        sharedPreferences.edit().putString(str8, BuildVars.SMS_HASH).commit();
                    } else {
                        sharedPreferences.edit().remove(str8).commit();
                    }
                    if (tL_auth_sendCode.settings.allow_flashcall) {
                        try {
                            String line1Number = telephonyManager.getLine1Number();
                            if (!TextUtils.isEmpty(line1Number)) {
                                tL_auth_sendCode.settings.current_number = PhoneNumberUtils.compare(str2, line1Number);
                                if (!tL_auth_sendCode.settings.current_number) {
                                    tL_auth_sendCode.settings.allow_flashcall = false;
                                }
                            } else if (UserConfig.getActivatedAccountsCount() > 0) {
                                tL_auth_sendCode.settings.allow_flashcall = false;
                            } else {
                                tL_auth_sendCode.settings.current_number = false;
                            }
                        } catch (Exception e) {
                            tL_auth_sendCode.settings.allow_flashcall = false;
                            FileLog.e(e);
                        }
                    }
                    Bundle bundle = new Bundle();
                    StringBuilder stringBuilder3 = new StringBuilder();
                    String str9 = "+";
                    stringBuilder3.append(str9);
                    stringBuilder3.append(this.codeField.getText());
                    String str10 = " ";
                    stringBuilder3.append(str10);
                    stringBuilder3.append(this.phoneField.getText());
                    bundle.putString(str3, stringBuilder3.toString());
                    try {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(str9);
                        stringBuilder3.append(PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()));
                        stringBuilder3.append(str10);
                        stringBuilder3.append(PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                        bundle.putString(str, stringBuilder3.toString());
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(str9);
                        stringBuilder3.append(str2);
                        bundle.putString(str, stringBuilder3.toString());
                    }
                    bundle.putString("phoneFormated", str2);
                    this.nextPressed = true;
                    this.this$0.needShowProgress(ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_sendCode, new -$$Lambda$LoginActivity$PhoneView$D6rb0YYZK8N7UnvplgUPvar_wHHA(this, bundle, tL_auth_sendCode), 27));
                }
            }
        }

        public /* synthetic */ void lambda$onNextPressed$8$LoginActivity$PhoneView(int i, DialogInterface dialogInterface, int i2) {
            if (UserConfig.selectedAccount != i) {
                ((LaunchActivity) this.this$0.getParentActivity()).switchToAccount(i, false);
            }
            this.this$0.finishFragment();
        }

        public /* synthetic */ void lambda$onNextPressed$10$LoginActivity$PhoneView(Bundle bundle, TL_auth_sendCode tL_auth_sendCode, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$PhoneView$wW0KLTORoTF7Ey-ULvar_AiQdzqo(this, tL_error, bundle, tLObject, tL_auth_sendCode));
        }

        public /* synthetic */ void lambda$null$9$LoginActivity$PhoneView(TL_error tL_error, Bundle bundle, TLObject tLObject, TL_auth_sendCode tL_auth_sendCode) {
            this.nextPressed = false;
            if (tL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
            } else {
                String str = tL_error.text;
                if (str != null) {
                    if (str.contains("PHONE_NUMBER_INVALID")) {
                        this.this$0.needShowInvalidAlert(tL_auth_sendCode.phone_number, false);
                    } else {
                        String str2 = "FloodWait";
                        String str3 = "AppName";
                        if (tL_error.text.contains("PHONE_PASSWORD_FLOOD")) {
                            this.this$0.needShowAlert(LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM));
                        } else if (tL_error.text.contains("PHONE_NUMBER_FLOOD")) {
                            this.this$0.needShowAlert(LocaleController.getString(str3, NUM), LocaleController.getString("PhoneNumberFlood", NUM));
                        } else if (tL_error.text.contains("PHONE_NUMBER_BANNED")) {
                            this.this$0.needShowInvalidAlert(tL_auth_sendCode.phone_number, true);
                        } else if (tL_error.text.contains("PHONE_CODE_EMPTY") || tL_error.text.contains("PHONE_CODE_INVALID")) {
                            this.this$0.needShowAlert(LocaleController.getString(str3, NUM), LocaleController.getString("InvalidCode", NUM));
                        } else if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                            this.this$0.needShowAlert(LocaleController.getString(str3, NUM), LocaleController.getString("CodeExpired", NUM));
                        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                            this.this$0.needShowAlert(LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM));
                        } else if (tL_error.code != -1000) {
                            this.this$0.needShowAlert(LocaleController.getString(str3, NUM), tL_error.text);
                        }
                    }
                }
            }
            this.this$0.needHideProgress(false);
        }

        public void fillNumber() {
            String str = "firstloginshow";
            String str2 = "android.permission.READ_PHONE_STATE";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (!(telephonyManager.getSimState() == 1 || telephonyManager.getPhoneType() == 0)) {
                    CharSequence charSequence = null;
                    Object obj;
                    if (VERSION.SDK_INT >= 23) {
                        obj = this.this$0.getParentActivity().checkSelfPermission(str2) == 0 ? 1 : null;
                        if (this.this$0.checkShowPermissions && obj == null) {
                            this.this$0.permissionsShowItems.clear();
                            if (obj == null) {
                                this.this$0.permissionsShowItems.add(str2);
                            }
                            if (!this.this$0.permissionsShowItems.isEmpty()) {
                                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                                if (!globalMainSettings.getBoolean(str, true)) {
                                    if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale(str2)) {
                                        this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsShowItems.toArray(new String[0]), 7);
                                    }
                                }
                                globalMainSettings.edit().putBoolean(str, false).commit();
                                Builder builder = new Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                                builder.setMessage(LocaleController.getString("AllowFillNumber", NUM));
                                this.this$0.permissionsShowDialog = this.this$0.showDialog(builder.create());
                            }
                            return;
                        }
                    }
                    obj = 1;
                    if (!(this.this$0.newAccount || obj == null)) {
                        str = PhoneFormat.stripExceptNumbers(telephonyManager.getLine1Number());
                        if (!TextUtils.isEmpty(str)) {
                            int i = 4;
                            if (str.length() > 4) {
                                Object obj2;
                                while (i >= 1) {
                                    str2 = str.substring(0, i);
                                    if (((String) this.codesMap.get(str2)) != null) {
                                        String substring = str.substring(i);
                                        this.codeField.setText(str2);
                                        charSequence = substring;
                                        obj2 = 1;
                                        break;
                                    }
                                    i--;
                                }
                                obj2 = null;
                                if (obj2 == null) {
                                    charSequence = str.substring(1);
                                    this.codeField.setText(str.substring(0, 1));
                                }
                            }
                            if (charSequence != null) {
                                this.phoneField.requestFocus();
                                this.phoneField.setText(charSequence);
                                this.phoneField.setSelection(this.phoneField.length());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell = this.checkBoxCell;
            if (checkBoxCell != null) {
                checkBoxCell.setChecked(this.this$0.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$LoginActivity$PhoneView$L2TZSNi6mTJFJwu7mU6H2Fczcd4(this), 100);
        }

        public /* synthetic */ void lambda$onShow$11$LoginActivity$PhoneView() {
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                HintEditText hintEditText = this.phoneField;
                hintEditText.setSelection(hintEditText.length());
                AndroidUtilities.showKeyboard(this.phoneField);
                return;
            }
            this.codeField.requestFocus();
            AndroidUtilities.showKeyboard(this.codeField);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourPhone", NUM);
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("phoneview_code", obj);
            }
            obj = this.phoneField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("phoneview_phone", obj);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            String string = bundle.getString("phoneview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
            String string2 = bundle.getString("phoneview_phone");
            if (string2 != null) {
                this.phoneField.setText(string2);
            }
        }
    }

    public LoginActivity() {
        this.views = new SlideView[9];
        this.permissionsItems = new ArrayList();
        this.permissionsShowItems = new ArrayList();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
    }

    public LoginActivity(int i) {
        this.views = new SlideView[9];
        this.permissionsItems = new ArrayList();
        this.permissionsShowItems = new ArrayList();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.currentAccount = i;
        this.newAccount = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i < slideViewArr.length) {
                if (slideViewArr[i] != null) {
                    slideViewArr[i].onDestroyActivity();
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Missing block: B:105:0x03c5, code skipped:
            if (r1 != 4) goto L_0x03cc;
     */
    public android.view.View createView(android.content.Context r26) {
        /*
        r25 = this;
        r0 = r25;
        r1 = r26;
        r2 = r0.actionBar;
        r3 = "AppName";
        r4 = NUM; // 0x7f0e00f9 float:1.8875543E38 double:1.0531622797E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r2.setTitle(r3);
        r2 = r0.actionBar;
        r3 = new org.telegram.ui.LoginActivity$1;
        r3.<init>();
        r2.setActionBarMenuOnItemClick(r3);
        r2 = 0;
        r0.currentDoneType = r2;
        r3 = r0.doneButtonVisible;
        r4 = 1;
        r3[r2] = r4;
        r3[r4] = r2;
        r3 = r0.actionBar;
        r3 = r3.createMenu();
        r5 = r0.actionBar;
        r5.setAllowOverlayTitle(r4);
        r5 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7 = NUM; // 0x7var_f float:1.7945128E38 double:1.052935637E-314;
        r3 = r3.addItemWithWidth(r4, r7, r6);
        r0.doneItem = r3;
        r3 = new org.telegram.ui.Components.ContextProgressView;
        r3.<init>(r1, r4);
        r0.doneProgressView = r3;
        r3 = r0.doneProgressView;
        r6 = 0;
        r3.setAlpha(r6);
        r3 = r0.doneProgressView;
        r7 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r3.setScaleX(r7);
        r3 = r0.doneProgressView;
        r3.setScaleY(r7);
        r3 = r0.doneProgressView;
        r8 = 4;
        r3.setVisibility(r8);
        r3 = r0.doneItem;
        r3.setAlpha(r6);
        r3 = r0.doneItem;
        r3.setScaleX(r7);
        r3 = r0.doneItem;
        r3.setScaleY(r7);
        r3 = r0.doneItem;
        r9 = r0.doneProgressView;
        r10 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r11 = -1;
        r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10);
        r3.addView(r9, r12);
        r3 = r0.doneItem;
        r9 = NUM; // 0x7f0e03e4 float:1.8877058E38 double:1.0531626487E-314;
        r12 = "Done";
        r13 = org.telegram.messenger.LocaleController.getString(r12, r9);
        r3.setContentDescription(r13);
        r3 = r0.doneItem;
        r13 = r0.doneButtonVisible;
        r13 = r13[r4];
        r14 = 8;
        if (r13 == 0) goto L_0x0097;
    L_0x0095:
        r13 = 0;
        goto L_0x0099;
    L_0x0097:
        r13 = 8;
    L_0x0099:
        r3.setVisibility(r13);
        r3 = new org.telegram.ui.LoginActivity$2;
        r3.<init>(r1);
        r0.fragmentView = r3;
        r13 = new org.telegram.ui.LoginActivity$3;
        r13.<init>(r1);
        r13.setFillViewport(r4);
        r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r10);
        r3.addView(r13, r15);
        r15 = new android.widget.FrameLayout;
        r15.<init>(r1);
        r10 = -2;
        r7 = 51;
        r7 = org.telegram.ui.Components.LayoutHelper.createScroll(r11, r10, r7);
        r13.addView(r15, r7);
        r7 = r0.views;
        r10 = new org.telegram.ui.LoginActivity$PhoneView;
        r10.<init>(r0, r1);
        r7[r2] = r10;
        r7 = r0.views;
        r10 = new org.telegram.ui.LoginActivity$LoginActivitySmsView;
        r10.<init>(r0, r1, r4);
        r7[r4] = r10;
        r7 = r0.views;
        r10 = new org.telegram.ui.LoginActivity$LoginActivitySmsView;
        r13 = 2;
        r10.<init>(r0, r1, r13);
        r7[r13] = r10;
        r7 = r0.views;
        r10 = new org.telegram.ui.LoginActivity$LoginActivitySmsView;
        r11 = 3;
        r10.<init>(r0, r1, r11);
        r7[r11] = r10;
        r7 = r0.views;
        r10 = new org.telegram.ui.LoginActivity$LoginActivitySmsView;
        r10.<init>(r0, r1, r8);
        r7[r8] = r10;
        r7 = r0.views;
        r10 = 5;
        r11 = new org.telegram.ui.LoginActivity$LoginActivityRegisterView;
        r11.<init>(r0, r1);
        r7[r10] = r11;
        r7 = r0.views;
        r10 = new org.telegram.ui.LoginActivity$LoginActivityPasswordView;
        r10.<init>(r0, r1);
        r11 = 6;
        r7[r11] = r10;
        r7 = r0.views;
        r10 = 7;
        r6 = new org.telegram.ui.LoginActivity$LoginActivityRecoverView;
        r6.<init>(r0, r1);
        r7[r10] = r6;
        r6 = r0.views;
        r7 = new org.telegram.ui.LoginActivity$LoginActivityResetWaitView;
        r7.<init>(r0, r1);
        r6[r14] = r7;
        r6 = 0;
    L_0x0118:
        r7 = r0.views;
        r10 = r7.length;
        if (r6 >= r10) goto L_0x015e;
    L_0x011d:
        r7 = r7[r6];
        if (r6 != 0) goto L_0x0123;
    L_0x0121:
        r10 = 0;
        goto L_0x0125;
    L_0x0123:
        r10 = 8;
    L_0x0125:
        r7.setVisibility(r10);
        r7 = r0.views;
        r7 = r7[r6];
        r16 = -1;
        r17 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r18 = 51;
        r10 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r10 == 0) goto L_0x013d;
    L_0x0138:
        r10 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r19 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        goto L_0x0141;
    L_0x013d:
        r10 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r19 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
    L_0x0141:
        r20 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r10 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r10 == 0) goto L_0x014e;
    L_0x0149:
        r10 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r21 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        goto L_0x0152;
    L_0x014e:
        r10 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r21 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
    L_0x0152:
        r22 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r15.addView(r7, r10);
        r6 = r6 + 1;
        goto L_0x0118;
    L_0x015e:
        r6 = r25.loadCurrentState();
        if (r6 == 0) goto L_0x01c5;
    L_0x0164:
        r7 = "currentViewNum";
        r7 = r6.getInt(r7, r2);
        r0.currentViewNum = r7;
        r7 = "syncContacts";
        r7 = r6.getInt(r7, r4);
        if (r7 != r4) goto L_0x0176;
    L_0x0174:
        r7 = 1;
        goto L_0x0177;
    L_0x0176:
        r7 = 0;
    L_0x0177:
        r0.syncContacts = r7;
        r7 = r0.currentViewNum;
        if (r7 < r4) goto L_0x01a3;
    L_0x017d:
        if (r7 > r8) goto L_0x01a3;
    L_0x017f:
        r7 = "open";
        r7 = r6.getInt(r7);
        if (r7 == 0) goto L_0x01c5;
    L_0x0187:
        r10 = java.lang.System.currentTimeMillis();
        r16 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r10 = r10 / r16;
        r14 = (long) r7;
        r10 = r10 - r14;
        r10 = java.lang.Math.abs(r10);
        r14 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
        r7 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1));
        if (r7 < 0) goto L_0x01c5;
    L_0x019c:
        r0.currentViewNum = r2;
        r6 = 0;
        r25.clearCurrentState();
        goto L_0x01c5;
    L_0x01a3:
        r7 = r0.currentViewNum;
        if (r7 != r11) goto L_0x01c5;
    L_0x01a7:
        r7 = r0.views;
        r7 = r7[r11];
        r7 = (org.telegram.ui.LoginActivity.LoginActivityPasswordView) r7;
        r10 = r7.passwordType;
        if (r10 == 0) goto L_0x01bf;
    L_0x01b3:
        r10 = r7.current_salt1;
        if (r10 == 0) goto L_0x01bf;
    L_0x01b9:
        r7 = r7.current_salt2;
        if (r7 != 0) goto L_0x01c5;
    L_0x01bf:
        r0.currentViewNum = r2;
        r6 = 0;
        r25.clearCurrentState();
    L_0x01c5:
        r7 = new android.widget.FrameLayout;
        r7.<init>(r1);
        r0.floatingButtonContainer = r7;
        r7 = r0.floatingButtonContainer;
        r10 = r0.doneButtonVisible;
        r10 = r10[r2];
        if (r10 == 0) goto L_0x01d6;
    L_0x01d4:
        r10 = 0;
        goto L_0x01d8;
    L_0x01d6:
        r10 = 8;
    L_0x01d8:
        r7.setVisibility(r10);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10 = "chats_actionBackground";
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r11 = "chats_actionPressedBackground";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r7 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r7, r10, r11);
        r10 = android.os.Build.VERSION.SDK_INT;
        r11 = 21;
        if (r10 >= r11) goto L_0x0221;
    L_0x01f5:
        r10 = r26.getResources();
        r14 = NUM; // 0x7var_cd float:1.7944994E38 double:1.0529356043E-314;
        r10 = r10.getDrawable(r14);
        r10 = r10.mutate();
        r14 = new android.graphics.PorterDuffColorFilter;
        r15 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r8 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r14.<init>(r15, r8);
        r10.setColorFilter(r14);
        r8 = new org.telegram.ui.Components.CombinedDrawable;
        r8.<init>(r10, r7, r2, r2);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r8.setIconSize(r7, r10);
        r7 = r8;
    L_0x0221:
        r8 = r0.floatingButtonContainer;
        r8.setBackgroundDrawable(r7);
        r7 = android.os.Build.VERSION.SDK_INT;
        if (r7 < r11) goto L_0x0293;
    L_0x022a:
        r7 = new android.animation.StateListAnimator;
        r7.<init>();
        r8 = new int[r4];
        r10 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
        r8[r2] = r10;
        r10 = r0.floatingButtonIcon;
        r14 = new float[r13];
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14[r2] = r15;
        r15 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14[r4] = r15;
        r15 = "translationZ";
        r10 = android.animation.ObjectAnimator.ofFloat(r10, r15, r14);
        r14 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r10 = r10.setDuration(r14);
        r7.addState(r8, r10);
        r8 = new int[r2];
        r10 = r0.floatingButtonIcon;
        r14 = new float[r13];
        r15 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14[r2] = r15;
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14[r4] = r15;
        r15 = "translationZ";
        r10 = android.animation.ObjectAnimator.ofFloat(r10, r15, r14);
        r14 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r10 = r10.setDuration(r14);
        r7.addState(r8, r10);
        r8 = r0.floatingButtonContainer;
        r8.setStateListAnimator(r7);
        r7 = r0.floatingButtonContainer;
        r8 = new org.telegram.ui.LoginActivity$4;
        r8.<init>();
        r7.setOutlineProvider(r8);
    L_0x0293:
        r7 = r0.floatingButtonContainer;
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r11) goto L_0x029e;
    L_0x0299:
        r8 = 56;
        r18 = 56;
        goto L_0x02a2;
    L_0x029e:
        r8 = 60;
        r18 = 60;
    L_0x02a2:
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r11) goto L_0x02a9;
    L_0x02a6:
        r19 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        goto L_0x02ad;
    L_0x02a9:
        r8 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r19 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
    L_0x02ad:
        r20 = 85;
        r21 = 0;
        r22 = 0;
        r23 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r24 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24);
        r3.addView(r7, r8);
        r3 = r0.floatingButtonContainer;
        r7 = new org.telegram.ui.-$$Lambda$LoginActivity$W1F_A-dhM1LzLqQMtvwEBbAYl2w;
        r7.<init>(r0);
        r3.setOnClickListener(r7);
        r3 = new android.widget.ImageView;
        r3.<init>(r1);
        r0.floatingButtonIcon = r3;
        r3 = r0.floatingButtonIcon;
        r7 = android.widget.ImageView.ScaleType.CENTER;
        r3.setScaleType(r7);
        r3 = r0.floatingButtonIcon;
        r7 = new android.graphics.PorterDuffColorFilter;
        r8 = "chats_actionIcon";
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r10 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r7.<init>(r8, r10);
        r3.setColorFilter(r7);
        r3 = r0.floatingButtonIcon;
        r7 = NUM; // 0x7var_e float:1.7944704E38 double:1.0529355337E-314;
        r3.setImageResource(r7);
        r3 = r0.floatingButtonContainer;
        r7 = org.telegram.messenger.LocaleController.getString(r12, r9);
        r3.setContentDescription(r7);
        r3 = r0.floatingButtonContainer;
        r7 = r0.floatingButtonIcon;
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r11) goto L_0x0304;
    L_0x0301:
        r8 = 56;
        goto L_0x0306;
    L_0x0304:
        r8 = 60;
    L_0x0306:
        r9 = android.os.Build.VERSION.SDK_INT;
        if (r9 < r11) goto L_0x030b;
    L_0x030a:
        goto L_0x030d;
    L_0x030b:
        r5 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
    L_0x030d:
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r5);
        r3.addView(r7, r5);
        r3 = new org.telegram.ui.Components.RadialProgressView;
        r3.<init>(r1);
        r0.floatingProgressView = r3;
        r1 = r0.floatingProgressView;
        r3 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.setSize(r3);
        r1 = r0.floatingProgressView;
        r3 = "chats_actionIcon";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setProgressColor(r3);
        r1 = r0.floatingProgressView;
        r3 = 0;
        r1.setAlpha(r3);
        r1 = r0.floatingProgressView;
        r3 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r1.setScaleX(r3);
        r1 = r0.floatingProgressView;
        r1.setScaleY(r3);
        r1 = r0.floatingProgressView;
        r3 = 4;
        r1.setVisibility(r3);
        r1 = r0.floatingButtonContainer;
        r3 = r0.floatingProgressView;
        r5 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r7 = -1;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r5);
        r1.addView(r3, r5);
        if (r6 == 0) goto L_0x035c;
    L_0x035a:
        r0.restoringState = r4;
    L_0x035c:
        r1 = 0;
    L_0x035d:
        r3 = r0.views;
        r5 = r3.length;
        if (r1 >= r5) goto L_0x03dd;
    L_0x0362:
        if (r6 == 0) goto L_0x037a;
    L_0x0364:
        if (r1 < r4) goto L_0x0373;
    L_0x0366:
        r5 = 4;
        if (r1 > r5) goto L_0x0373;
    L_0x0369:
        r5 = r0.currentViewNum;
        if (r1 != r5) goto L_0x037a;
    L_0x036d:
        r3 = r3[r1];
        r3.restoreStateParams(r6);
        goto L_0x037a;
    L_0x0373:
        r3 = r0.views;
        r3 = r3[r1];
        r3.restoreStateParams(r6);
    L_0x037a:
        r3 = r0.currentViewNum;
        if (r3 != r1) goto L_0x03cf;
    L_0x037e:
        r3 = r0.actionBar;
        r5 = r0.views;
        r5 = r5[r1];
        r5 = r5.needBackButton();
        if (r5 != 0) goto L_0x0391;
    L_0x038a:
        r5 = r0.newAccount;
        if (r5 == 0) goto L_0x038f;
    L_0x038e:
        goto L_0x0391;
    L_0x038f:
        r5 = 0;
        goto L_0x0394;
    L_0x0391:
        r5 = NUM; // 0x7var_f2 float:1.7945069E38 double:1.0529356226E-314;
    L_0x0394:
        r3.setBackButtonImage(r5);
        r3 = r0.views;
        r3 = r3[r1];
        r3.setVisibility(r2);
        r3 = r0.views;
        r3 = r3[r1];
        r3.onShow();
        r0.currentDoneType = r2;
        if (r1 == r4) goto L_0x03ba;
    L_0x03a9:
        if (r1 == r13) goto L_0x03ba;
    L_0x03ab:
        r3 = 3;
        if (r1 == r3) goto L_0x03ba;
    L_0x03ae:
        r3 = 4;
        if (r1 == r3) goto L_0x03ba;
    L_0x03b1:
        r3 = 8;
        if (r1 != r3) goto L_0x03b6;
    L_0x03b5:
        goto L_0x03ba;
    L_0x03b6:
        r0.showDoneButton(r4, r2);
        goto L_0x03bd;
    L_0x03ba:
        r0.showDoneButton(r2, r2);
    L_0x03bd:
        if (r1 == r4) goto L_0x03c8;
    L_0x03bf:
        if (r1 == r13) goto L_0x03c8;
    L_0x03c1:
        r5 = 3;
        r7 = 4;
        if (r1 == r5) goto L_0x03ca;
    L_0x03c5:
        if (r1 != r7) goto L_0x03cc;
    L_0x03c7:
        goto L_0x03ca;
    L_0x03c8:
        r5 = 3;
        r7 = 4;
    L_0x03ca:
        r0.currentDoneType = r4;
    L_0x03cc:
        r8 = 8;
        goto L_0x03da;
    L_0x03cf:
        r5 = 3;
        r7 = 4;
        r3 = r0.views;
        r3 = r3[r1];
        r8 = 8;
        r3.setVisibility(r8);
    L_0x03da:
        r1 = r1 + 1;
        goto L_0x035d;
    L_0x03dd:
        r0.restoringState = r2;
        r1 = r0.actionBar;
        r2 = r0.currentViewNum;
        r2 = r3[r2];
        r2 = r2.getHeaderName();
        r1.setTitle(r2);
        r1 = r0.fragmentView;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$0$LoginActivity(View view) {
        onDoneButtonPressed();
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        try {
            if (this.currentViewNum >= 1 && this.currentViewNum <= 4 && (this.views[this.currentViewNum] instanceof LoginActivitySmsView)) {
                int access$1000 = ((LoginActivitySmsView) this.views[this.currentViewNum]).openTime;
                if (access$1000 != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) access$1000)) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed(true);
                    setPage(0, false, null, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 6) {
            this.checkPermissions = false;
            i = this.currentViewNum;
            if (i == 0) {
                this.views[i].onNextPressed();
            }
        } else if (i == 7) {
            this.checkShowPermissions = false;
            i = this.currentViewNum;
            if (i == 0) {
                ((PhoneView) this.views[i]).fillNumber();
            }
        }
    }

    private Bundle loadCurrentState() {
        if (this.newAccount) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            for (Entry entry : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String str = (String) entry.getKey();
                Object value = entry.getValue();
                String[] split = str.split("_\\|_");
                if (split.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(str, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(str, ((Integer) value).intValue());
                    }
                } else if (split.length == 2) {
                    Bundle bundle2 = bundle.getBundle(split[0]);
                    if (bundle2 == null) {
                        bundle2 = new Bundle();
                        bundle.putBundle(split[0], bundle2);
                    }
                    if (value instanceof String) {
                        bundle2.putString(split[1], (String) value);
                    } else if (value instanceof Integer) {
                        bundle2.putInt(split[1], ((Integer) value).intValue());
                    }
                }
            }
            return bundle;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private void clearCurrentState() {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        edit.clear();
        edit.commit();
    }

    private void putBundleToEditor(Bundle bundle, Editor editor, String str) {
        for (String str2 : bundle.keySet()) {
            Object obj = bundle.get(str2);
            String str3 = "_|_";
            StringBuilder stringBuilder;
            if (obj instanceof String) {
                if (str != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(str3);
                    stringBuilder.append(str2);
                    editor.putString(stringBuilder.toString(), (String) obj);
                } else {
                    editor.putString(str2, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (str != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(str3);
                    stringBuilder.append(str2);
                    editor.putInt(stringBuilder.toString(), ((Integer) obj).intValue());
                } else {
                    editor.putInt(str2, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, str2);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT < 23) {
            return;
        }
        if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
            } catch (Exception unused) {
            }
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
        }
    }

    public boolean onBackPressed() {
        int i = this.currentViewNum;
        int i2 = 0;
        if (i == 0) {
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (i2 < slideViewArr.length) {
                    if (slideViewArr[i2] != null) {
                        slideViewArr[i2].onDestroyActivity();
                    }
                    i2++;
                } else {
                    clearCurrentState();
                    return true;
                }
            }
        }
        if (i == 6) {
            this.views[i].onBackPressed(true);
            setPage(0, true, null, true);
        } else if (i == 7 || i == 8) {
            this.views[this.currentViewNum].onBackPressed(true);
            setPage(6, true, null, true);
        } else if (i < 1 || i > 4) {
            i = this.currentViewNum;
            if (i == 5) {
                ((LoginActivityRegisterView) this.views[i]).wrongNumber.callOnClick();
            }
        } else if (this.views[i].onBackPressed(false)) {
            setPage(0, true, null, true);
        }
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) this.views[5];
        if (loginActivityRegisterView != null) {
            loginActivityRegisterView.imageUpdater.onActivityResult(i, i2, intent);
        }
    }

    private void needShowAlert(String str, String str2) {
        if (str2 != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(str);
            builder.setMessage(str2);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    private void onFieldError(View view) {
        try {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
        } catch (Throwable unused) {
        }
        AndroidUtilities.shakeView(view, 2.0f, 0);
    }

    private void needShowInvalidAlert(String str, boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", NUM));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", NUM));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", NUM), new -$$Lambda$LoginActivity$46vRdz1bQM_EHE2DXn2q36kGyZo(this, z, str));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$needShowInvalidAlert$1$LoginActivity(boolean z, String str, DialogInterface dialogInterface, int i) {
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("message/rfCLASSNAME");
            intent.putExtra("android.intent.extra.EMAIL", new String[]{"login@stel.com"});
            String str2 = "\nLocale: ";
            String str3 = "\nDevice Name: ";
            String str4 = "\nOS version: SDK ";
            String str5 = "I'm trying to use my mobile phone number: ";
            String str6 = "android.intent.extra.TEXT";
            String str7 = "android.intent.extra.SUBJECT";
            StringBuilder stringBuilder;
            if (z) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Banned phone number: ");
                stringBuilder.append(str);
                intent.putExtra(str7, stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append(str5);
                stringBuilder.append(str);
                stringBuilder.append("\nBut Telegram says it's banned. Please help.\n\nApp version: ");
                stringBuilder.append(format);
                stringBuilder.append(str4);
                stringBuilder.append(VERSION.SDK_INT);
                stringBuilder.append(str3);
                stringBuilder.append(Build.MANUFACTURER);
                stringBuilder.append(Build.MODEL);
                stringBuilder.append(str2);
                stringBuilder.append(Locale.getDefault());
                intent.putExtra(str6, stringBuilder.toString());
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid phone number: ");
                stringBuilder.append(str);
                intent.putExtra(str7, stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append(str5);
                stringBuilder.append(str);
                stringBuilder.append("\nBut Telegram says it's invalid. Please help.\n\nApp version: ");
                stringBuilder.append(format);
                stringBuilder.append(str4);
                stringBuilder.append(VERSION.SDK_INT);
                stringBuilder.append(str3);
                stringBuilder.append(Build.MANUFACTURER);
                stringBuilder.append(Build.MODEL);
                stringBuilder.append(str2);
                stringBuilder.append(Locale.getDefault());
                intent.putExtra(str6, stringBuilder.toString());
            }
            getParentActivity().startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (Exception unused) {
            needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("NoMailInstalled", NUM));
        }
    }

    private void showDoneButton(final boolean z, boolean z2) {
        final boolean z3 = this.currentDoneType == 0;
        boolean[] zArr = this.doneButtonVisible;
        int i = this.currentDoneType;
        if (zArr[i] != z) {
            AnimatorSet[] animatorSetArr = this.showDoneAnimation;
            if (animatorSetArr[i] != null) {
                animatorSetArr[i].cancel();
            }
            zArr = this.doneButtonVisible;
            i = this.currentDoneType;
            zArr[i] = z;
            if (z2) {
                TimeInterpolator timeInterpolator;
                this.showDoneAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (z) {
                    if (z3) {
                        this.floatingButtonContainer.setVisibility(0);
                        this.showDoneAnimation[this.currentDoneType].play(ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Y, new float[]{0.0f}));
                    } else {
                        this.doneItem.setVisibility(0);
                        animatorSet = this.showDoneAnimation[this.currentDoneType];
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem, View.SCALE_X, new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem, View.SCALE_Y, new float[]{1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem, View.ALPHA, new float[]{1.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                } else if (z3) {
                    this.showDoneAnimation[this.currentDoneType].play(ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Y, new float[]{AndroidUtilities.dpf2(70.0f)}));
                } else {
                    animatorSet = this.showDoneAnimation[this.currentDoneType];
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem, View.SCALE_X, new float[]{0.1f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem, View.SCALE_Y, new float[]{0.1f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (LoginActivity.this.showDoneAnimation[z3 ^ 1] != null && LoginActivity.this.showDoneAnimation[z3 ^ 1].equals(animator) && !z) {
                            if (z3) {
                                LoginActivity.this.floatingButtonContainer.setVisibility(8);
                            } else {
                                LoginActivity.this.doneItem.setVisibility(8);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (LoginActivity.this.showDoneAnimation[z3 ^ 1] != null && LoginActivity.this.showDoneAnimation[z3 ^ 1].equals(animator)) {
                            LoginActivity.this.showDoneAnimation[z3 ^ 1] = null;
                        }
                    }
                });
                int i2 = 150;
                if (!z3) {
                    timeInterpolator = null;
                } else if (z) {
                    i2 = 200;
                    timeInterpolator = AndroidUtilities.decelerateInterpolator;
                } else {
                    timeInterpolator = AndroidUtilities.accelerateInterpolator;
                }
                this.showDoneAnimation[this.currentDoneType].setDuration((long) i2);
                this.showDoneAnimation[this.currentDoneType].setInterpolator(timeInterpolator);
                this.showDoneAnimation[this.currentDoneType].start();
            } else if (z) {
                if (z3) {
                    this.floatingButtonContainer.setVisibility(0);
                    this.floatingButtonContainer.setTranslationY(0.0f);
                } else {
                    this.doneItem.setVisibility(0);
                    this.doneItem.setScaleX(1.0f);
                    this.doneItem.setScaleY(1.0f);
                    this.doneItem.setAlpha(1.0f);
                }
            } else if (z3) {
                this.floatingButtonContainer.setVisibility(8);
                this.floatingButtonContainer.setTranslationY(AndroidUtilities.dpf2(70.0f));
            } else {
                this.doneItem.setVisibility(8);
                this.doneItem.setScaleX(0.1f);
                this.doneItem.setScaleY(0.1f);
                this.doneItem.setAlpha(0.0f);
            }
        }
    }

    private void onDoneButtonPressed() {
        if (this.doneButtonVisible[this.currentDoneType]) {
            if (this.doneProgressView.getTag() == null) {
                this.views[this.currentViewNum].onNextPressed();
            } else if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("StopLoading", NUM));
                builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), null);
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new -$$Lambda$LoginActivity$QDPdXQumalMXrHB2DnwB0raXD7E(this));
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$onDoneButtonPressed$2$LoginActivity(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        final boolean z3 = z;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        final boolean z4 = this.currentDoneType == 0;
        if (z2) {
            this.doneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet2;
            Animator[] animatorArr;
            if (z3) {
                this.doneProgressView.setTag(Integer.valueOf(1));
                if (z4) {
                    this.floatingProgressView.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(false);
                    animatorSet2 = this.doneItemAnimation;
                    Animator[] animatorArr2 = new Animator[6];
                    animatorArr2[0] = ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{0.1f});
                    animatorArr2[1] = ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{0.1f});
                    animatorArr2[2] = ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{0.0f});
                    animatorArr2[3] = ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{1.0f});
                    animatorArr2[4] = ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{1.0f});
                    animatorArr2[5] = ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr2);
                } else {
                    this.doneProgressView.setVisibility(0);
                    animatorSet2 = this.doneItemAnimation;
                    animatorArr = new Animator[6];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{1.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{1.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                }
            } else {
                this.doneProgressView.setTag(null);
                if (z4) {
                    this.floatingButtonIcon.setVisibility(0);
                    this.floatingButtonContainer.setEnabled(true);
                    animatorSet2 = this.doneItemAnimation;
                    animatorArr = new Animator[6];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{0.1f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{0.1f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{1.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{1.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    this.doneItem.getContentView().setVisibility(0);
                    animatorSet2 = this.doneItemAnimation;
                    animatorArr = new Animator[6];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_X, new float[]{0.1f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.doneProgressView, View.SCALE_Y, new float[]{0.1f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.doneProgressView, View.ALPHA, new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                }
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animator)) {
                        if (z4) {
                            if (z3) {
                                LoginActivity.this.floatingButtonIcon.setVisibility(4);
                            } else {
                                LoginActivity.this.floatingProgressView.setVisibility(4);
                            }
                        } else if (z3) {
                            LoginActivity.this.doneItem.getContentView().setVisibility(4);
                        } else {
                            LoginActivity.this.doneProgressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animator)) {
                        LoginActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (!z3) {
            this.doneProgressView.setTag(null);
            if (z4) {
                this.floatingProgressView.setVisibility(4);
                this.floatingButtonIcon.setVisibility(0);
                this.floatingButtonContainer.setEnabled(true);
                this.floatingProgressView.setScaleX(0.1f);
                this.floatingProgressView.setScaleY(0.1f);
                this.floatingProgressView.setAlpha(0.0f);
                this.floatingButtonIcon.setScaleX(1.0f);
                this.floatingButtonIcon.setScaleY(1.0f);
                this.floatingButtonIcon.setAlpha(1.0f);
                return;
            }
            this.doneItem.getContentView().setVisibility(0);
            this.doneProgressView.setVisibility(4);
            this.doneProgressView.setScaleX(0.1f);
            this.doneProgressView.setScaleY(0.1f);
            this.doneProgressView.setAlpha(0.0f);
            this.doneItem.getContentView().setScaleX(1.0f);
            this.doneItem.getContentView().setScaleY(1.0f);
            this.doneItem.getContentView().setAlpha(1.0f);
        } else if (z4) {
            this.floatingProgressView.setVisibility(0);
            this.floatingButtonIcon.setVisibility(4);
            this.floatingButtonContainer.setEnabled(false);
            this.floatingButtonIcon.setScaleX(0.1f);
            this.floatingButtonIcon.setScaleY(0.1f);
            this.floatingButtonIcon.setAlpha(0.0f);
            this.floatingProgressView.setScaleX(1.0f);
            this.floatingProgressView.setScaleY(1.0f);
            this.floatingProgressView.setAlpha(1.0f);
        } else {
            this.doneProgressView.setVisibility(0);
            this.doneItem.getContentView().setVisibility(4);
            this.doneItem.getContentView().setScaleX(0.1f);
            this.doneItem.getContentView().setScaleY(0.1f);
            this.doneItem.getContentView().setAlpha(0.0f);
            this.doneProgressView.setScaleX(1.0f);
            this.doneProgressView.setScaleY(1.0f);
            this.doneProgressView.setAlpha(1.0f);
        }
    }

    private void needShowProgress(int i) {
        needShowProgress(i, true);
    }

    private void needShowProgress(int i, boolean z) {
        this.progressRequestId = i;
        showEditDoneProgress(true, z);
    }

    private void needHideProgress(boolean z) {
        needHideProgress(z, true);
    }

    private void needHideProgress(boolean z, boolean z2) {
        if (this.progressRequestId != 0) {
            if (z) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        showEditDoneProgress(false, z2);
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        final boolean z3 = i == 0 || i == 5 || i == 6 || i == 7;
        if (z3) {
            if (i == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.currentDoneType = 1;
            showDoneButton(false, z);
            this.currentDoneType = 0;
            showEditDoneProgress(false, false);
            if (!z) {
                showDoneButton(true, false);
            }
        } else {
            this.currentDoneType = 0;
            showDoneButton(false, z);
            if (i != 8) {
                this.currentDoneType = 1;
            }
        }
        int i2 = NUM;
        if (z) {
            SlideView[] slideViewArr = this.views;
            final SlideView slideView = slideViewArr[this.currentViewNum];
            SlideView slideView2 = slideViewArr[i];
            this.currentViewNum = i;
            ActionBar actionBar = this.actionBar;
            if (!(slideView2.needBackButton() || this.newAccount)) {
                i2 = 0;
            }
            actionBar.setBackButtonImage(i2);
            slideView2.setParams(bundle, false);
            this.actionBar.setTitle(slideView2.getHeaderName());
            setParentActivityTitle(slideView2.getHeaderName());
            slideView2.onShow();
            i = AndroidUtilities.displaySize.x;
            if (z2) {
                i = -i;
            }
            slideView2.setX((float) i);
            slideView2.setVisibility(0);
            this.pagesAnimation = new AnimatorSet();
            this.pagesAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (LoginActivity.this.currentDoneType == 0 && z3) {
                        LoginActivity.this.showDoneButton(true, true);
                    }
                    slideView.setVisibility(8);
                    slideView.setX(0.0f);
                }
            });
            AnimatorSet animatorSet = this.pagesAnimation;
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = (float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x);
            animatorArr[0] = ObjectAnimator.ofFloat(slideView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(slideView2, View.TRANSLATION_X, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.pagesAnimation.setDuration(300);
            this.pagesAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            this.pagesAnimation.start();
            return;
        }
        ActionBar actionBar2 = this.actionBar;
        if (!(this.views[i].needBackButton() || this.newAccount)) {
            i2 = 0;
        }
        actionBar2.setBackButtonImage(i2);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = i;
        this.views[i].setParams(bundle, false);
        this.views[i].setVisibility(0);
        this.actionBar.setTitle(this.views[i].getHeaderName());
        setParentActivityTitle(this.views[i].getHeaderName());
        this.views[i].onShow();
    }

    public void saveSelfArgs(Bundle bundle) {
        try {
            bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            bundle.putInt("syncContacts", this.syncContacts ? 1 : 0);
            for (int i = 0; i <= this.currentViewNum; i++) {
                SlideView slideView = this.views[i];
                if (slideView != null) {
                    slideView.saveStateParams(bundle);
                }
            }
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            edit.clear();
            putBundleToEditor(bundle, edit, null);
            edit.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void needFinishActivity(boolean z) {
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false);
                finishFragment();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("afterSignup", z);
            presentFragment(new DialogsActivity(bundle), true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    private void onAuthSuccess(TL_auth_authorization tL_auth_authorization) {
        onAuthSuccess(tL_auth_authorization, false);
    }

    private void onAuthSuccess(TL_auth_authorization tL_auth_authorization, boolean z) {
        ConnectionsManager.getInstance(this.currentAccount).setUserId(tL_auth_authorization.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(tL_auth_authorization.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_auth_authorization.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(tL_auth_authorization.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkProxyInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        needFinishActivity(z);
    }

    private void fillNextCodeParams(Bundle bundle, TL_auth_sentCode tL_auth_sentCode) {
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
        String str2 = "length";
        String str3 = "type";
        if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeApp) {
            bundle.putInt(str3, 1);
            bundle.putInt(str2, tL_auth_sentCode.type.length);
            setPage(1, true, bundle, false);
            return;
        }
        if (tL_auth_sentCode.timeout == 0) {
            tL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tL_auth_sentCode.timeout * 1000);
        auth_SentCodeType auth_sentcodetype = tL_auth_sentCode.type;
        if (auth_sentcodetype instanceof TL_auth_sentCodeTypeCall) {
            bundle.putInt(str3, 4);
            bundle.putInt(str2, tL_auth_sentCode.type.length);
            setPage(4, true, bundle, false);
        } else if (auth_sentcodetype instanceof TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt(str3, 3);
            bundle.putString("pattern", tL_auth_sentCode.type.pattern);
            setPage(3, true, bundle, false);
        } else if (auth_sentcodetype instanceof TL_auth_sentCodeTypeSms) {
            bundle.putInt(str3, 2);
            bundle.putInt(str2, tL_auth_sentCode.type.length);
            setPage(2, true, bundle, false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i >= slideViewArr.length) {
                int i2;
                PhoneView phoneView = (PhoneView) slideViewArr[0];
                LoginActivitySmsView loginActivitySmsView = (LoginActivitySmsView) slideViewArr[1];
                LoginActivitySmsView loginActivitySmsView2 = (LoginActivitySmsView) slideViewArr[2];
                LoginActivitySmsView loginActivitySmsView3 = (LoginActivitySmsView) slideViewArr[3];
                LoginActivitySmsView loginActivitySmsView4 = (LoginActivitySmsView) slideViewArr[4];
                LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) slideViewArr[5];
                LoginActivityPasswordView loginActivityPasswordView = (LoginActivityPasswordView) slideViewArr[6];
                LoginActivityRecoverView loginActivityRecoverView = (LoginActivityRecoverView) slideViewArr[7];
                LoginActivityResetWaitView loginActivityResetWaitView = (LoginActivityResetWaitView) slideViewArr[8];
                ArrayList arrayList = new ArrayList();
                ThemeDescription themeDescription = r12;
                ThemeDescription themeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
                arrayList.add(themeDescription);
                arrayList.add(new ThemeDescription(this.floatingButtonIcon, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
                arrayList.add(new ThemeDescription(this.floatingButtonIcon, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(this.floatingButtonIcon, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
                arrayList.add(new ThemeDescription(this.floatingProgressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "chats_actionIcon"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
                arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
                arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhiteGrayLine"));
                arrayList.add(new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteRedText6"));
                arrayList.add(new ThemeDescription(loginActivityPasswordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.privacyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRegisterView.privacyView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, "windowBackgroundWhiteLinkText"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(loginActivityRecoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView.codeField != null) {
                    for (i2 = 0; i2 < loginActivitySmsView.codeField.length; i2++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView.codeField[i2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView.codeField[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                View access$6100 = loginActivitySmsView.progressView;
                Class[] clsArr = new Class[]{ProgressView.class};
                String[] strArr = new String[1];
                strArr[0] = "paint";
                arrayList.add(new ThemeDescription(access$6100, 0, clsArr, strArr, null, null, null, "login_progressInner"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView2.codeField != null) {
                    for (i2 = 0; i2 < loginActivitySmsView2.codeField.length; i2++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView2.codeField[i2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView2.codeField[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{r8}, null, null, null, "login_progressInner"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{r8}, null, null, null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView3.codeField != null) {
                    for (i2 = 0; i2 < loginActivitySmsView3.codeField.length; i2++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView3.codeField[i2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView3.codeField[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{r8}, null, null, null, "login_progressInner"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{r8}, null, null, null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                if (loginActivitySmsView4.codeField != null) {
                    for (i2 = 0; i2 < loginActivitySmsView4.codeField.length; i2++) {
                        arrayList.add(new ThemeDescription(loginActivitySmsView4.codeField[i2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                        arrayList.add(new ThemeDescription(loginActivitySmsView4.codeField[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
                    }
                }
                arrayList.add(new ThemeDescription(loginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{r8}, null, null, null, "login_progressInner"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{r8}, null, null, null, "login_progressOuter"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            } else if (slideViewArr[i] == null) {
                return new ThemeDescription[0];
            } else {
                i++;
            }
        }
    }
}
