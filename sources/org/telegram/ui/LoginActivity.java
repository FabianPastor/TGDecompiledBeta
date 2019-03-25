package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SmsReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
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
import org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$$CC;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SlideView;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions;
    private boolean checkShowPermissions;
    private TL_help_termsOfService currentTermsOfService;
    private int currentViewNum;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private ContextProgressView doneProgressView;
    private boolean newAccount;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems;
    private int progressRequestId;
    private int scrollHeight;
    private boolean syncContacts;
    private SlideView[] views;

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
        private String email_unconfirmed_pattern;
        private boolean has_recovery;
        private String hint;
        private boolean nextPressed;
        private int passwordType;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;

        public LoginActivityPasswordView(Context context) {
            super(context);
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.confirmTextView.setText(LocaleController.getString("LoginPasswordText", NUM));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.codeField = new EditTextBoldCursor(context);
            this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
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
            this.codeField.setOnEditorActionListener(new LoginActivity$LoginActivityPasswordView$$Lambda$0(this));
            this.cancelButton = new TextView(context);
            this.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.cancelButton.setText(LocaleController.getString("ForgotPassword", NUM));
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(this.cancelButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48));
            this.cancelButton.setOnClickListener(new LoginActivity$LoginActivityPasswordView$$Lambda$1(this));
            this.resetAccountButton = new TextView(context);
            this.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
            this.resetAccountButton.setVisibility(8);
            this.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", NUM));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(this.resetAccountButton, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 34, 0, 0));
            this.resetAccountButton.setOnClickListener(new LoginActivity$LoginActivityPasswordView$$Lambda$2(this));
            this.resetAccountText = new TextView(context);
            this.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountText.setVisibility(8);
            this.resetAccountText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", NUM));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountText, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 7, 0, 14));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$0$LoginActivity$LoginActivityPasswordView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$4$LoginActivity$LoginActivityPasswordView(View view) {
            if (LoginActivity.this.doneProgressView.getTag() == null) {
                if (this.has_recovery) {
                    LoginActivity.this.needShowProgress(0);
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new LoginActivity$LoginActivityPasswordView$$Lambda$12(this), 10);
                    return;
                }
                this.resetAccountText.setVisibility(0);
                this.resetAccountButton.setVisibility(0);
                AndroidUtilities.hideKeyboard(this.codeField);
                LoginActivity.this.needShowAlert(LocaleController.getString("RestorePasswordNoEitle", NUM), LocaleController.getString("RestorePasswordNoEmailText", NUM));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$3$LoginActivity$LoginActivityPasswordView(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$Lambda$13(this, error, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityPasswordView(TL_error error, TLObject response) {
            LoginActivity.this.needHideProgress(false);
            if (error == null) {
                TL_auth_passwordRecovery res = (TL_auth_passwordRecovery) response;
                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, res.email_pattern));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityPasswordView$$Lambda$14(this, res));
                Dialog dialog = LoginActivity.this.showDialog(builder.create());
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
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
            } else {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityPasswordView(TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
            LoginActivity.this.setPage(7, true, bundle, false);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$8$LoginActivity$LoginActivityPasswordView(View view) {
            if (LoginActivity.this.doneProgressView.getTag() == null) {
                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityPasswordView$$Lambda$9(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                LoginActivity.this.showDialog(builder.create());
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$7$LoginActivity$LoginActivityPasswordView(DialogInterface dialogInterface, int i) {
            LoginActivity.this.needShowProgress(0);
            TL_account_deleteAccount req = new TL_account_deleteAccount();
            req.reason = "Forgot password";
            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new LoginActivity$LoginActivityPasswordView$$Lambda$10(this), 10);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$6$LoginActivity$LoginActivityPasswordView(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$Lambda$11(this, error));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$5$LoginActivity$LoginActivityPasswordView(TL_error error) {
            LoginActivity.this.needHideProgress(false);
            Bundle params;
            if (error == null) {
                params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                LoginActivity.this.setPage(5, true, params, false);
            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                params.putInt("startTime", ConnectionsManager.getInstance(LoginActivity.this.currentAccount).getCurrentTime());
                params.putInt("waitTime", Utilities.parseInt(error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                LoginActivity.this.setPage(8, true, params, false);
            } else {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            boolean z = true;
            if (params != null) {
                if (params.isEmpty()) {
                    this.resetAccountButton.setVisibility(0);
                    this.resetAccountText.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
                this.resetAccountButton.setVisibility(8);
                this.resetAccountText.setVisibility(8);
                this.codeField.setText("");
                this.currentParams = params;
                this.current_salt1 = Utilities.hexToBytes(this.currentParams.getString("current_salt1"));
                this.current_salt2 = Utilities.hexToBytes(this.currentParams.getString("current_salt2"));
                this.current_p = Utilities.hexToBytes(this.currentParams.getString("current_p"));
                this.current_g = this.currentParams.getInt("current_g");
                this.current_srp_B = Utilities.hexToBytes(this.currentParams.getString("current_srp_B"));
                this.current_srp_id = this.currentParams.getLong("current_srp_id");
                this.passwordType = this.currentParams.getInt("passwordType");
                this.hint = this.currentParams.getString("hint");
                if (this.currentParams.getInt("has_recovery") != 1) {
                    z = false;
                }
                this.has_recovery = z;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                if (this.hint == null || this.hint.length() <= 0) {
                    this.codeField.setHint(LocaleController.getString("LoginPassword", NUM));
                } else {
                    this.codeField.setHint(this.hint);
                }
            }
        }

        private void onPasscodeError(boolean clear) {
            if (LoginActivity.this.getParentActivity() != null) {
                Vibrator v = (Vibrator) LoginActivity.this.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                if (clear) {
                    this.codeField.setText("");
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String oldPassword = this.codeField.getText().toString();
                if (oldPassword.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.nextPressed = true;
                LoginActivity.this.needShowProgress(0);
                Utilities.globalQueue.postRunnable(new LoginActivity$LoginActivityPasswordView$$Lambda$3(this, oldPassword));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$13$LoginActivity$LoginActivityPasswordView(String oldPassword) {
            byte[] x_bytes;
            PasswordKdfAlgo current_algo = null;
            if (this.passwordType == 1) {
                PasswordKdfAlgo algo = new TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow();
                algo.salt1 = this.current_salt1;
                algo.salt2 = this.current_salt2;
                algo.g = this.current_g;
                algo.p = this.current_p;
                current_algo = algo;
            }
            if (current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                x_bytes = SRPHelper.getX(AndroidUtilities.getStringBytes(oldPassword), (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo);
            } else {
                x_bytes = null;
            }
            TL_auth_checkPassword req = new TL_auth_checkPassword();
            RequestDelegate requestDelegate = new LoginActivity$LoginActivityPasswordView$$Lambda$5(this);
            if (current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo2 = (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo;
                algo2.salt1 = this.current_salt1;
                algo2.salt2 = this.current_salt2;
                algo2.g = this.current_g;
                algo2.p = this.current_p;
                req.password = SRPHelper.startCheck(x_bytes, this.current_srp_id, this.current_srp_B, algo2);
                if (req.password == null) {
                    TL_error error = new TL_error();
                    error.text = "PASSWORD_HASH_INVALID";
                    requestDelegate.run(null, error);
                    return;
                }
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, requestDelegate, 10);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$12$LoginActivity$LoginActivityPasswordView(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$Lambda$6(this, error, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$11$LoginActivity$LoginActivityPasswordView(TL_error error, TLObject response) {
            this.nextPressed = false;
            if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                LoginActivity.this.needHideProgress(false);
                if (error == null) {
                    LoginActivity.this.onAuthSuccess((TL_auth_authorization) response);
                    return;
                } else if (error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                    return;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                    return;
                } else {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
                    return;
                }
            }
            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(new TL_account_getPassword(), new LoginActivity$LoginActivityPasswordView$$Lambda$7(this), 8);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$10$LoginActivity$LoginActivityPasswordView(TLObject response2, TL_error error2) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$Lambda$8(this, error2, response2));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$9$LoginActivity$LoginActivityPasswordView(TL_error error2, TLObject response2) {
            if (error2 == null) {
                TL_account_password password = (TL_account_password) response2;
                this.current_srp_B = password.srp_B;
                this.current_srp_id = password.srp_id;
                onNextPressed();
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public boolean onBackPressed(boolean force) {
            this.nextPressed = false;
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityPasswordView$$Lambda$4(this), 100);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onShow$14$LoginActivity$LoginActivityPasswordView() {
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
                AndroidUtilities.showKeyboard(this.codeField);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("passview_code", code);
            }
            if (this.currentParams != null) {
                bundle.putBundle("passview_params", this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("passview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
            }
            String code = bundle.getString("passview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
        }
    }

    public class LoginActivityRecoverView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private String email_unconfirmed_pattern;
        private boolean nextPressed;
        final /* synthetic */ LoginActivity this$0;

        public LoginActivityRecoverView(LoginActivity this$0, Context context) {
            int i;
            int i2 = 5;
            this.this$0 = this$0;
            super(context);
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", NUM));
            TextView textView = this.confirmTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(textView, LayoutHelper.createLinear(-2, -2, i));
            this.codeField = new EditTextBoldCursor(context);
            this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
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
            this.codeField.setOnEditorActionListener(new LoginActivity$LoginActivityRecoverView$$Lambda$0(this));
            this.cancelButton = new TextView(context);
            this.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
            this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            TextView textView2 = this.cancelButton;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(textView2, LayoutHelper.createLinear(-2, -2, i2 | 80, 0, 0, 0, 14));
            this.cancelButton.setOnClickListener(new LoginActivity$LoginActivityRecoverView$$Lambda$1(this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$0$LoginActivity$LoginActivityRecoverView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$2$LoginActivity$LoginActivityRecoverView(View view) {
            Builder builder = new Builder(this.this$0.getParentActivity());
            builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", NUM));
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityRecoverView$$Lambda$6(this));
            Dialog dialog = this.this$0.showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityRecoverView(DialogInterface dialogInterface, int i) {
            this.this$0.setPage(6, true, new Bundle(), true);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", NUM);
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.codeField.setText("");
                this.currentParams = params;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", NUM, this.email_unconfirmed_pattern));
                AndroidUtilities.showKeyboard(this.codeField);
                this.codeField.requestFocus();
            }
        }

        private void onPasscodeError(boolean clear) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator v = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                if (clear) {
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
                String code = this.codeField.getText().toString();
                if (code.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.this$0.needShowProgress(0);
                TL_auth_recoverPassword req = new TL_auth_recoverPassword();
                req.code = code;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityRecoverView$$Lambda$2(this), 10);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$5$LoginActivity$LoginActivityRecoverView(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$Lambda$4(this, error, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$4$LoginActivity$LoginActivityRecoverView(TL_error error, TLObject response) {
            this.this$0.needHideProgress(false);
            this.nextPressed = false;
            if (error == null) {
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new LoginActivity$LoginActivityRecoverView$$Lambda$5(this, response));
                builder.setMessage(LocaleController.getString("PasswordReset", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                Dialog dialog = this.this$0.showDialog(builder.create());
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                }
            } else if (error.text.startsWith("CODE_INVALID")) {
                onPasscodeError(true);
            } else if (error.text.startsWith("FLOOD_WAIT")) {
                String timeString;
                int time = Utilities.parseInt(error.text).intValue();
                if (time < 60) {
                    timeString = LocaleController.formatPluralString("Seconds", time);
                } else {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                }
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$3$LoginActivity$LoginActivityRecoverView(TLObject response, DialogInterface dialogInterface, int i) {
            this.this$0.onAuthSuccess((TL_auth_authorization) response);
        }

        public boolean onBackPressed(boolean force) {
            this.this$0.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRecoverView$$Lambda$3(this), 100);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onShow$6$LoginActivity$LoginActivityRecoverView() {
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (!(code == null || code.length() == 0)) {
                bundle.putString("recoveryview_code", code);
            }
            if (this.currentParams != null) {
                bundle.putBundle("recoveryview_params", this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("recoveryview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
            }
            String code = bundle.getString("recoveryview_code");
            if (code != null) {
                this.codeField.setText(code);
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
        private String phoneCode;
        private String phoneHash;
        private TextView privacyView;
        private String requestPhone;
        private TextView textView;
        private InputFile uploadedAvatar;
        private TextView wrongNumber;

        public class LinkSpan extends ClickableSpan {
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            public void onClick(View widget) {
                LoginActivityRegisterView.this.showTermsOfService(false);
            }
        }

        public String getInitialSearchString() {
            return ImageUpdater$ImageUpdaterDelegate$$CC.getInitialSearchString(this);
        }

        private void showTermsOfService(boolean needAccept) {
            if (LoginActivity.this.currentTermsOfService != null) {
                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("TermsOfService", NUM));
                if (needAccept) {
                    builder.setPositiveButton(LocaleController.getString("Accept", NUM), new LoginActivity$LoginActivityRegisterView$$Lambda$0(this));
                    builder.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$Lambda$1(this));
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                }
                SpannableStringBuilder text = new SpannableStringBuilder(LoginActivity.this.currentTermsOfService.text);
                MessageObject.addEntitiesToText(text, LoginActivity.this.currentTermsOfService.entities, false, 0, false, false, false);
                builder.setMessage(text);
                LoginActivity.this.showDialog(builder.create());
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$showTermsOfService$0$LoginActivity$LoginActivityRegisterView(DialogInterface dialog, int which) {
            LoginActivity.this.currentTermsOfService.popup = false;
            onNextPressed();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$showTermsOfService$3$LoginActivity$LoginActivityRegisterView(DialogInterface dialog, int which) {
            Builder builder1 = new Builder(LoginActivity.this.getParentActivity());
            builder1.setTitle(LocaleController.getString("TermsOfService", NUM));
            builder1.setMessage(LocaleController.getString("TosDecline", NUM));
            builder1.setPositiveButton(LocaleController.getString("SignUp", NUM), new LoginActivity$LoginActivityRegisterView$$Lambda$12(this));
            builder1.setNegativeButton(LocaleController.getString("Decline", NUM), new LoginActivity$LoginActivityRegisterView$$Lambda$13(this));
            LoginActivity.this.showDialog(builder1.create());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityRegisterView(DialogInterface dialog1, int which1) {
            LoginActivity.this.currentTermsOfService.popup = false;
            onNextPressed();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityRegisterView(DialogInterface dialog12, int which12) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        public LoginActivityRegisterView(Context context) {
            float f;
            float f2;
            super(context);
            setOrientation(1);
            this.imageUpdater = new ImageUpdater();
            this.imageUpdater.setSearchAvailable(false);
            this.imageUpdater.setUploadAfterSelect(false);
            this.imageUpdater.parentFragment = LoginActivity.this;
            this.imageUpdater.delegate = this;
            this.textView = new TextView(context);
            this.textView.setText(LocaleController.getString("RegisterText2", NUM));
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setTextSize(1, 14.0f);
            addView(this.textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 0, 0, 0));
            FrameLayout editTextContainer = new FrameLayout(context);
            addView(editTextContainer, LayoutHelper.createLinear(-1, -2, 0.0f, 21.0f, 0.0f, 0.0f));
            this.avatarDrawable = new AvatarDrawable();
            final LoginActivity loginActivity = LoginActivity.this;
            this.avatarImage = new BackupImageView(context) {
                public void invalidate() {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                public void invalidate(int l, int t, int r, int b) {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(l, t, r, b);
                }
            };
            this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, null, null, false);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            editTextContainer.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            final LoginActivity loginActivity2 = LoginActivity.this;
            this.avatarOverlay = new View(context) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (LoginActivityRegisterView.this.avatarImage != null && LoginActivityRegisterView.this.avatarProgressView.getVisibility() == 0) {
                        paint.setAlpha((int) ((85.0f * LoginActivityRegisterView.this.avatarImage.getImageReceiver().getCurrentAlpha()) * LoginActivityRegisterView.this.avatarProgressView.getAlpha()));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            editTextContainer.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            this.avatarOverlay.setOnClickListener(new LoginActivity$LoginActivityRegisterView$$Lambda$2(this));
            final LoginActivity loginActivity22 = LoginActivity.this;
            this.avatarEditor = new ImageView(context) {
                public void invalidate(int l, int t, int r, int b) {
                    super.invalidate(l, t, r, b);
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
            editTextContainer.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            final LoginActivity loginActivity222 = LoginActivity.this;
            this.avatarProgressView = new RadialProgressView(context) {
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            editTextContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 16.0f, 0.0f, 0.0f));
            showAvatarProgress(false, false);
            this.firstNameField = new EditTextBoldCursor(context);
            this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.firstNameField.setCursorWidth(1.5f);
            this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
            this.firstNameField.setImeOptions(NUM);
            this.firstNameField.setTextSize(1, 17.0f);
            this.firstNameField.setMaxLines(1);
            this.firstNameField.setInputType(8192);
            View view = this.firstNameField;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = 85.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 85.0f;
            } else {
                f2 = 0.0f;
            }
            editTextContainer.addView(view, LayoutHelper.createFrame(-1, 36.0f, i, f, 0.0f, f2, 0.0f));
            this.firstNameField.setOnEditorActionListener(new LoginActivity$LoginActivityRegisterView$$Lambda$3(this));
            this.lastNameField = new EditTextBoldCursor(context);
            this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
            this.lastNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.lastNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.lastNameField.setCursorWidth(1.5f);
            this.lastNameField.setImeOptions(NUM);
            this.lastNameField.setTextSize(1, 17.0f);
            this.lastNameField.setMaxLines(1);
            this.lastNameField.setInputType(8192);
            view = this.lastNameField;
            i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = 85.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 85.0f;
            } else {
                f2 = 0.0f;
            }
            editTextContainer.addView(view, LayoutHelper.createFrame(-1, 36.0f, i, f, 51.0f, f2, 0.0f));
            this.lastNameField.setOnEditorActionListener(new LoginActivity$LoginActivityRegisterView$$Lambda$4(this));
            this.wrongNumber = new TextView(context);
            this.wrongNumber.setText(LocaleController.getString("CancelRegistration", NUM));
            this.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.wrongNumber.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.wrongNumber.setTextSize(1, 14.0f);
            this.wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            this.wrongNumber.setVisibility(8);
            addView(this.wrongNumber, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 20, 0, 0));
            this.wrongNumber.setOnClickListener(new LoginActivity$LoginActivityRegisterView$$Lambda$5(this));
            this.privacyView = new TextView(context);
            this.privacyView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.privacyView.setMovementMethod(new LinkMovementMethodMy());
            this.privacyView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            this.privacyView.setTextSize(1, 14.0f);
            this.privacyView.setGravity(81);
            this.privacyView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.privacyView, LayoutHelper.createLinear(-2, -1, 81, 0, 28, 0, 16));
            String str = LocaleController.getString("TermsOfServiceLogin", NUM);
            SpannableStringBuilder text = new SpannableStringBuilder(str);
            int index1 = str.indexOf(42);
            int index2 = str.lastIndexOf(42);
            if (!(index1 == -1 || index2 == -1 || index1 == index2)) {
                text.replace(index2, index2 + 1, "");
                text.replace(index1, index1 + 1, "");
                text.setSpan(new LinkSpan(), index1, index2 - 1, 33);
            }
            this.privacyView.setText(text);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$5$LoginActivity$LoginActivityRegisterView(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new LoginActivity$LoginActivityRegisterView$$Lambda$11(this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$4$LoginActivity$LoginActivityRegisterView() {
            this.avatar = null;
            this.avatarBig = null;
            this.uploadedAvatar = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, null);
            this.avatarEditor.setImageResource(NUM);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$6$LoginActivity$LoginActivityRegisterView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.lastNameField.requestFocus();
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$7$LoginActivity$LoginActivityRegisterView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$8$LoginActivity$LoginActivityRegisterView(View view) {
            if (LoginActivity.this.doneProgressView.getTag() == null) {
                onBackPressed(false);
            }
        }

        public void didUploadPhoto(InputFile file, PhotoSize bigSize, PhotoSize smallSize) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$Lambda$6(this, smallSize, bigSize));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$didUploadPhoto$9$LoginActivity$LoginActivityRegisterView(PhotoSize smallSize, PhotoSize bigSize) {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, null);
        }

        private void showAvatarProgress(final boolean show, boolean animated) {
            if (this.avatarEditor != null) {
                if (this.avatarAnimation != null) {
                    this.avatarAnimation.cancel();
                    this.avatarAnimation = null;
                }
                if (animated) {
                    this.avatarAnimation = new AnimatorSet();
                    AnimatorSet animatorSet;
                    Animator[] animatorArr;
                    if (show) {
                        this.avatarProgressView.setVisibility(0);
                        animatorSet = this.avatarAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        this.avatarEditor.setVisibility(0);
                        animatorSet = this.avatarAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                    this.avatarAnimation.setDuration(180);
                    this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (LoginActivityRegisterView.this.avatarAnimation != null && LoginActivityRegisterView.this.avatarEditor != null) {
                                if (show) {
                                    LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                                } else {
                                    LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                                }
                                LoginActivityRegisterView.this.avatarAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            LoginActivityRegisterView.this.avatarAnimation = null;
                        }
                    });
                    this.avatarAnimation.start();
                } else if (show) {
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

        public boolean onBackPressed(boolean force) {
            if (force) {
                LoginActivity.this.needHideProgress(true);
                this.nextPressed = false;
                this.currentParams = null;
                return true;
            }
            Builder builder = new Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("AreYouSureRegistration", NUM));
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$LoginActivityRegisterView$$Lambda$7(this));
            builder.setPositiveButton(LocaleController.getString("Continue", NUM), null);
            LoginActivity.this.showDialog(builder.create());
            return false;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onBackPressed$10$LoginActivity$LoginActivityRegisterView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", NUM);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public boolean needBackButton() {
            return true;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$Lambda$8(this), 100);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onShow$11$LoginActivity$LoginActivityRegisterView() {
            if (this.firstNameField != null) {
                this.firstNameField.requestFocus();
                this.firstNameField.setSelection(this.firstNameField.length());
            }
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.firstNameField.setText("");
                this.lastNameField.setText("");
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                this.currentParams = params;
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                if (LoginActivity.this.currentTermsOfService == null || !LoginActivity.this.currentTermsOfService.popup) {
                    this.nextPressed = true;
                    TL_auth_signUp req = new TL_auth_signUp();
                    req.phone_code = this.phoneCode;
                    req.phone_code_hash = this.phoneHash;
                    req.phone_number = this.requestPhone;
                    req.first_name = this.firstNameField.getText().toString();
                    req.last_name = this.lastNameField.getText().toString();
                    LoginActivity.this.needShowProgress(0);
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new LoginActivity$LoginActivityRegisterView$$Lambda$9(this), 10);
                    return;
                }
                showTermsOfService(true);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$13$LoginActivity$LoginActivityRegisterView(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityRegisterView$$Lambda$10(this, error, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$12$LoginActivity$LoginActivityRegisterView(TL_error error, TLObject response) {
            this.nextPressed = false;
            LoginActivity.this.needHideProgress(false);
            if (error == null) {
                LoginActivity.this.onAuthSuccess((TL_auth_authorization) response);
                if (this.avatarBig != null) {
                    MessagesController.getInstance(LoginActivity.this.currentAccount).uploadAndApplyUserAvatar(this.avatarBig);
                }
            } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
            } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
            } else if (error.text.contains("FIRSTNAME_INVALID")) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidFirstName", NUM));
            } else if (error.text.contains("LASTNAME_INVALID")) {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidLastName", NUM));
            } else {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        public void saveStateParams(Bundle bundle) {
            String first = this.firstNameField.getText().toString();
            if (first.length() != 0) {
                bundle.putString("registerview_first", first);
            }
            String last = this.lastNameField.getText().toString();
            if (last.length() != 0) {
                bundle.putString("registerview_last", last);
            }
            if (LoginActivity.this.currentTermsOfService != null) {
                SerializedData data = new SerializedData(LoginActivity.this.currentTermsOfService.getObjectSize());
                LoginActivity.this.currentTermsOfService.serializeToStream(data);
                bundle.putString("terms", Base64.encodeToString(data.toByteArray(), 0));
                data.cleanup();
            }
            if (this.currentParams != null) {
                bundle.putBundle("registerview_params", this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("registerview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
            }
            try {
                String terms = bundle.getString("terms");
                if (terms != null) {
                    byte[] arr = Base64.decode(terms, 0);
                    if (arr != null) {
                        SerializedData data = new SerializedData(arr);
                        LoginActivity.this.currentTermsOfService = TL_help_termsOfService.TLdeserialize(data, data.readInt32(false), false);
                        data.cleanup();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            String first = bundle.getString("registerview_first");
            if (first != null) {
                this.firstNameField.setText(first);
            }
            String last = bundle.getString("registerview_last");
            if (last != null) {
                this.lastNameField.setText(last);
            }
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

        public LoginActivityResetWaitView(LoginActivity this$0, Context context) {
            int i;
            int i2 = 5;
            this.this$0 = this$0;
            super(context);
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            TextView textView = this.confirmTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(textView, LayoutHelper.createLinear(-2, -2, i));
            this.resetAccountText = new TextView(context);
            textView = this.resetAccountText;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 48);
            this.resetAccountText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", NUM));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            TextView textView2 = this.resetAccountText;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(textView2, LayoutHelper.createLinear(-2, -2, i | 48, 0, 24, 0, 0));
            this.resetAccountTime = new TextView(context);
            textView = this.resetAccountTime;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 48);
            this.resetAccountTime.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.resetAccountTime.setTextSize(1, 14.0f);
            this.resetAccountTime.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            textView2 = this.resetAccountTime;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(textView2, LayoutHelper.createLinear(-2, -2, i | 48, 0, 2, 0, 0));
            this.resetAccountButton = new TextView(context);
            textView = this.resetAccountButton;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 48);
            this.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", NUM));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            textView2 = this.resetAccountButton;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(textView2, LayoutHelper.createLinear(-2, -2, i2 | 48, 0, 7, 0, 0));
            this.resetAccountButton.setOnClickListener(new LoginActivity$LoginActivityResetWaitView$$Lambda$0(this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$3$LoginActivity$LoginActivityResetWaitView(View view) {
            if (this.this$0.doneProgressView.getTag() == null && Math.abs(ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime) >= this.waitTime) {
                Builder builder = new Builder(this.this$0.getParentActivity());
                builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", NUM));
                builder.setTitle(LocaleController.getString("ResetMyAccountWarning", NUM));
                builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", NUM), new LoginActivity$LoginActivityResetWaitView$$Lambda$1(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                this.this$0.showDialog(builder.create());
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$2$LoginActivity$LoginActivityResetWaitView(DialogInterface dialogInterface, int i) {
            this.this$0.needShowProgress(0);
            TL_account_deleteAccount req = new TL_account_deleteAccount();
            req.reason = "Forgot password";
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new LoginActivity$LoginActivityResetWaitView$$Lambda$2(this), 10);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$LoginActivity$LoginActivityResetWaitView(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivityResetWaitView$$Lambda$3(this, error));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$0$LoginActivity$LoginActivityResetWaitView(TL_error error) {
            this.this$0.needHideProgress(false);
            if (error == null) {
                Bundle params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                this.this$0.setPage(5, true, params, false);
            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ResetAccountCancelledAlert", NUM));
            } else {
                this.this$0.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", NUM);
        }

        private void updateTimeText() {
            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int days = timeLeft / 86400;
            int hours = (timeLeft - (days * 86400)) / 3600;
            int minutes = ((timeLeft - (days * 86400)) - (hours * 3600)) / 60;
            int seconds = timeLeft % 60;
            if (days != 0) {
                this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", days) + " " + LocaleController.formatPluralString("HoursBold", hours) + " " + LocaleController.formatPluralString("MinutesBold", minutes)));
            } else {
                this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", hours) + " " + LocaleController.formatPluralString("MinutesBold", minutes) + " " + LocaleController.formatPluralString("SecondsBold", seconds)));
            }
            if (timeLeft > 0) {
                this.resetAccountButton.setTag("windowBackgroundWhiteGrayText6");
                this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                return;
            }
            this.resetAccountButton.setTag("windowBackgroundWhiteRedText6");
            this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.currentParams = params;
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                this.startTime = params.getInt("startTime");
                this.waitTime = params.getInt("waitTime");
                this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", NUM, LocaleController.addNbsp(PhoneFormat.getInstance().format("+" + this.requestPhone)))));
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

        public boolean needBackButton() {
            return true;
        }

        public boolean onBackPressed(boolean force) {
            this.this$0.needHideProgress(true);
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
            return true;
        }

        public void saveStateParams(Bundle bundle) {
            if (this.currentParams != null) {
                bundle.putBundle("resetview_params", this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("resetview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
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
        private boolean isRestored;
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
        private int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private boolean waitingForEvent;

        public LoginActivitySmsView(Context context, int type) {
            super(context);
            this.currentType = type;
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
            if (this.currentType == 3) {
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
                if (this.currentType == 1) {
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
            if (this.currentType == 3) {
                this.codeFieldContainer.setVisibility(8);
            }
            this.timeText = new TextView(context, LoginActivity.this) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            if (this.currentType == 3) {
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
            this.problemText = new TextView(context, LoginActivity.this) {
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
            if (this.currentType == 1) {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", NUM));
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", NUM));
            }
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49));
            this.problemText.setOnClickListener(new LoginActivity$LoginActivitySmsView$$Lambda$0(this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$0$LoginActivity$LoginActivitySmsView(View v) {
            boolean email = false;
            if (!this.nextPressed) {
                if ((this.nextType == 4 && this.currentType == 2) || this.nextType == 0) {
                    email = true;
                }
                if (email) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                        Intent mailer = new Intent("android.intent.action.SEND");
                        mailer.setType("message/rfCLASSNAME");
                        mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                        mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.emailPhone);
                        mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                        getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                    } catch (Exception e) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("NoMailInstalled", NUM));
                    }
                } else if (LoginActivity.this.doneProgressView.getTag() == null) {
                    resendCode();
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.currentType != 3 && this.blueImageView != null) {
                int innerHeight = ((this.blueImageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight()) + this.confirmTextView.getMeasuredHeight()) + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (LoginActivity.this.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else if (LoginActivity.this.scrollHeight > maxHeight) {
                    setMeasuredDimension(getMeasuredWidth(), maxHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), LoginActivity.this.scrollHeight);
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.currentType != 3 && this.blueImageView != null) {
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

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            int reqId = ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$Lambda$1(this, params), 10);
            LoginActivity.this.needShowProgress(0);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$resendCode$2$LoginActivity$LoginActivitySmsView(Bundle params, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$Lambda$11(this, error, params, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$LoginActivity$LoginActivitySmsView(TL_error error, Bundle params, TLObject response) {
            this.nextPressed = false;
            if (error == null) {
                LoginActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    LoginActivity.this.setPage(0, true, null, true);
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.code != -1000) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
                }
            }
            LoginActivity.this.needHideProgress(false);
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", NUM);
        }

        public boolean needBackButton() {
            return true;
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.isRestored = restore;
                this.waitingForEvent = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = params;
                this.phone = params.getString("phone");
                this.emailPhone = params.getString("ephone");
                this.requestPhone = params.getString("phoneFormated");
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
                        if (this.currentType == 3) {
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
                                if (!LoginActivitySmsView.this.ignoreOnTextChange) {
                                    int len = s.length();
                                    if (len >= 1) {
                                        if (len > 1) {
                                            String text = s.toString();
                                            LoginActivitySmsView.this.ignoreOnTextChange = true;
                                            for (int a = 0; a < Math.min(LoginActivitySmsView.this.length - num, len); a++) {
                                                if (a == 0) {
                                                    s.replace(0, len, text.substring(a, a + 1));
                                                } else {
                                                    LoginActivitySmsView.this.codeField[num + a].setText(text.substring(a, a + 1));
                                                }
                                            }
                                            LoginActivitySmsView.this.ignoreOnTextChange = false;
                                        }
                                        if (num != LoginActivitySmsView.this.length - 1) {
                                            LoginActivitySmsView.this.codeField[num + 1].setSelection(LoginActivitySmsView.this.codeField[num + 1].length());
                                            LoginActivitySmsView.this.codeField[num + 1].requestFocus();
                                        }
                                        if ((num == LoginActivitySmsView.this.length - 1 || (num == LoginActivitySmsView.this.length - 2 && len >= 2)) && LoginActivitySmsView.this.getCode().length() == LoginActivitySmsView.this.length) {
                                            LoginActivitySmsView.this.onNextPressed();
                                        }
                                    }
                                }
                            }
                        });
                        this.codeField[a].setOnKeyListener(new LoginActivity$LoginActivitySmsView$$Lambda$2(this, num));
                        this.codeField[a].setOnEditorActionListener(new LoginActivity$LoginActivitySmsView$$Lambda$3(this));
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
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    if (this.currentType == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", NUM));
                    } else if (this.currentType == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(number)));
                    } else if (this.currentType == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(number)));
                    } else if (this.currentType == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.currentType == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (this.currentType == 3 && (this.nextType == 4 || this.nextType == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        if (this.nextType == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(1), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(1), Integer.valueOf(0)));
                        }
                        if (null != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(null);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        } else if (this.catchedPhone != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(this.catchedPhone);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        } else {
                            createTimer();
                        }
                    } else if (this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(2), Integer.valueOf(0)));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        String hash = preferences.getString("sms_hash", null);
                        String savedCode = null;
                        if (!TextUtils.isEmpty(hash)) {
                            savedCode = preferences.getString("sms_hash_code", null);
                            if (savedCode != null) {
                                if (savedCode.contains(hash + "|")) {
                                    savedCode = savedCode.substring(savedCode.indexOf(124) + 1);
                                }
                            }
                            savedCode = null;
                        }
                        if (savedCode != null) {
                            this.codeField[0].setText(savedCode);
                            onNextPressed();
                            return;
                        }
                        createTimer();
                    } else if (this.currentType == 4 && this.nextType == 2) {
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
        public final /* synthetic */ boolean lambda$setParams$3$LoginActivity$LoginActivitySmsView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            this.codeField[num - 1].setSelection(this.codeField[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$setParams$4$LoginActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
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
                        AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$4$$Lambda$0(this));
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$run$0$LoginActivity$LoginActivitySmsView$4() {
                        double currentTime = (double) System.currentTimeMillis();
                        double diff = currentTime - LoginActivitySmsView.this.lastCodeTime;
                        LoginActivitySmsView.this.lastCodeTime = currentTime;
                        LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - diff);
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
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$5$$Lambda$0(this));
                        }
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$run$2$LoginActivity$LoginActivitySmsView$5() {
                        double currentTime = (double) System.currentTimeMillis();
                        double diff = currentTime - LoginActivitySmsView.this.lastCurrentTime;
                        LoginActivitySmsView.this.lastCurrentTime = currentTime;
                        LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - diff);
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int seconds = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                            if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                            } else if (LoginActivitySmsView.this.nextType == 2) {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                            }
                            if (LoginActivitySmsView.this.progressView != null) {
                                LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                return;
                            }
                            return;
                        }
                        if (LoginActivitySmsView.this.progressView != null) {
                            LoginActivitySmsView.this.progressView.setProgress(1.0f);
                        }
                        LoginActivitySmsView.this.destroyTimer();
                        if (LoginActivitySmsView.this.currentType == 3) {
                            AndroidUtilities.setWaitingForCall(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
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
                                TL_auth_resendCode req = new TL_auth_resendCode();
                                req.phone_number = LoginActivitySmsView.this.requestPhone;
                                req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$5$$Lambda$1(this), 10);
                            } else if (LoginActivitySmsView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            }
                        }
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$null$1$LoginActivity$LoginActivitySmsView$5(TLObject response, TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$5$$Lambda$2(this, error));
                        }
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$null$0$LoginActivity$LoginActivitySmsView$5(TL_error error) {
                        LoginActivitySmsView.this.lastError = error.text;
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
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                TL_auth_signIn req = new TL_auth_signIn();
                req.phone_number = this.requestPhone;
                req.phone_code = code;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                LoginActivity.this.needShowProgress(ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new LoginActivity$LoginActivitySmsView$$Lambda$4(this, req), 10));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$8$LoginActivity$LoginActivitySmsView(TL_auth_signIn req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$Lambda$8(this, error, response, req));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$7$LoginActivity$LoginActivitySmsView(TL_error error, TLObject response, TL_auth_signIn req) {
            this.nextPressed = false;
            boolean ok = false;
            if (error == null) {
                ok = true;
                LoginActivity.this.needHideProgress(false);
                destroyTimer();
                destroyCodeTimer();
                LoginActivity.this.onAuthSuccess((TL_auth_authorization) response);
            } else {
                this.lastError = error.text;
                if (error.text.contains("PHONE_NUMBER_UNOCCUPIED")) {
                    ok = true;
                    LoginActivity.this.needHideProgress(false);
                    Bundle params = new Bundle();
                    params.putString("phoneFormated", this.requestPhone);
                    params.putString("phoneHash", this.phoneHash);
                    params.putString("code", req.phone_code);
                    LoginActivity.this.setPage(5, true, params, false);
                    destroyTimer();
                    destroyCodeTimer();
                } else if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    ok = true;
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(new TL_account_getPassword(), new LoginActivity$LoginActivitySmsView$$Lambda$9(this, req), 10);
                    destroyTimer();
                    destroyCodeTimer();
                } else {
                    LoginActivity.this.needHideProgress(false);
                    if ((this.currentType == 3 && (this.nextType == 4 || this.nextType == 2)) || ((this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) || (this.currentType == 4 && this.nextType == 2))) {
                        createTimer();
                    }
                    if (this.currentType == 2) {
                        AndroidUtilities.setWaitingForSms(true);
                        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                    } else if (this.currentType == 3) {
                        AndroidUtilities.setWaitingForCall(true);
                        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                    }
                    this.waitingForEvent = true;
                    if (this.currentType != 3) {
                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                            for (EditTextBoldCursor text : this.codeField) {
                                text.setText("");
                            }
                            this.codeField[0].requestFocus();
                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                            onBackPressed(true);
                            LoginActivity.this.setPage(0, true, null, true);
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                        } else {
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text);
                        }
                    }
                }
            }
            if (ok && this.currentType == 3) {
                AndroidUtilities.endIncomingCall();
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$6$LoginActivity$LoginActivitySmsView(TL_auth_signIn req, TLObject response1, TL_error error1) {
            AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$Lambda$10(this, error1, response1, req));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$5$LoginActivity$LoginActivitySmsView(TL_error error1, TLObject response1, TL_auth_signIn req) {
            LoginActivity.this.needHideProgress(false);
            if (error1 == null) {
                TL_account_password password = (TL_account_password) response1;
                if (TwoStepVerificationActivity.canHandleCurrentPassword(password, true)) {
                    int i;
                    Bundle bundle = new Bundle();
                    if (password.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                        TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = password.current_algo;
                        bundle.putString("current_salt1", Utilities.bytesToHex(algo.salt1));
                        bundle.putString("current_salt2", Utilities.bytesToHex(algo.salt2));
                        bundle.putString("current_p", Utilities.bytesToHex(algo.p));
                        bundle.putInt("current_g", algo.g);
                        bundle.putString("current_srp_B", Utilities.bytesToHex(password.srp_B));
                        bundle.putLong("current_srp_id", password.srp_id);
                        bundle.putInt("passwordType", 1);
                    }
                    bundle.putString("hint", password.hint != null ? password.hint : "");
                    bundle.putString("email_unconfirmed_pattern", password.email_unconfirmed_pattern != null ? password.email_unconfirmed_pattern : "");
                    bundle.putString("phoneFormated", this.requestPhone);
                    bundle.putString("phoneHash", this.phoneHash);
                    bundle.putString("code", req.phone_code);
                    String str = "has_recovery";
                    if (password.has_recovery) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    bundle.putInt(str, i);
                    LoginActivity.this.setPage(6, true, bundle, false);
                    return;
                }
                AlertsCreator.showUpdateAppAlert(LoginActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), error1.text);
        }

        public boolean onBackPressed(boolean force) {
            if (force) {
                this.nextPressed = false;
                LoginActivity.this.needHideProgress(true);
                TL_auth_cancelCode req = new TL_auth_cancelCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, LoginActivity$LoginActivitySmsView$$Lambda$6.$instance, 10);
                destroyTimer();
                destroyCodeTimer();
                this.currentParams = null;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                return true;
            }
            Builder builder = new Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopVerification", NUM));
            builder.setPositiveButton(LocaleController.getString("Continue", NUM), null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$LoginActivitySmsView$$Lambda$5(this));
            LoginActivity.this.showDialog(builder.create());
            return false;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onBackPressed$9$LoginActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        static final /* synthetic */ void lambda$onBackPressed$10$LoginActivity$LoginActivitySmsView(TLObject response, TL_error error) {
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
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
                AndroidUtilities.runOnUIThread(new LoginActivity$LoginActivitySmsView$$Lambda$7(this), 100);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onShow$11$LoginActivity$LoginActivitySmsView() {
            if (this.codeField != null) {
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
                        if (!this.pattern.equals("*")) {
                            this.catchedPhone = num;
                            AndroidUtilities.endIncomingCall();
                        }
                        this.ignoreOnTextChange = true;
                        this.codeField[0].setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    }
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = getCode();
            if (code.length() != 0) {
                bundle.putString("smsview_code_" + this.currentType, code);
            }
            if (this.catchedPhone != null) {
                bundle.putString("catchedPhone", this.catchedPhone);
            }
            if (this.currentParams != null) {
                bundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
            }
            if (this.time != 0) {
                bundle.putInt("time", this.time);
            }
            if (this.openTime != 0) {
                bundle.putInt("open", this.openTime);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("smsview_params_" + this.currentType);
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
            }
            String catched = bundle.getString("catchedPhone");
            if (catched != null) {
                this.catchedPhone = catched;
            }
            String code = bundle.getString("smsview_code_" + this.currentType);
            if (!(code == null || this.codeField == null)) {
                this.codeField[0].setText(code);
            }
            int t = bundle.getInt("time");
            if (t != 0) {
                this.time = t;
            }
            int t2 = bundle.getInt("open");
            if (t2 != 0) {
                this.openTime = t2;
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
        private View view;

        public PhoneView(Context context) {
            super(context);
            setOrientation(1);
            this.countryButton = new TextView(context);
            this.countryButton.setTextSize(1, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), 0);
            this.countryButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.countryButton.setMaxLines(1);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.countryButton.setBackgroundResource(NUM);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new LoginActivity$PhoneView$$Lambda$0(this));
            this.view = new View(context);
            this.view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            this.view.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
            addView(this.view, LayoutHelper.createLinear(-1, 1, 4.0f, -17.5f, 4.0f, 0.0f));
            View linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setText("+");
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 18.0f);
            linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditTextBoldCursor(context);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(NUM);
            this.codeField.setFilters(new InputFilter[]{new LengthFilter(5)});
            linearLayout.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            final LoginActivity loginActivity = LoginActivity.this;
            this.codeField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!PhoneView.this.ignoreOnTextChange) {
                        PhoneView.this.ignoreOnTextChange = true;
                        String text = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                        PhoneView.this.codeField.setText(text);
                        if (text.length() == 0) {
                            PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 1;
                        } else {
                            boolean ok = false;
                            String textToSet = null;
                            if (text.length() > 4) {
                                for (int a = 4; a >= 1; a--) {
                                    String sub = text.substring(0, a);
                                    if (((String) PhoneView.this.codesMap.get(sub)) != null) {
                                        ok = true;
                                        textToSet = text.substring(a) + PhoneView.this.phoneField.getText().toString();
                                        text = sub;
                                        PhoneView.this.codeField.setText(sub);
                                        break;
                                    }
                                }
                                if (!ok) {
                                    textToSet = text.substring(1) + PhoneView.this.phoneField.getText().toString();
                                    EditTextBoldCursor access$1300 = PhoneView.this.codeField;
                                    text = text.substring(0, 1);
                                    access$1300.setText(text);
                                }
                            }
                            String country = (String) PhoneView.this.codesMap.get(text);
                            if (country != null) {
                                int index = PhoneView.this.countriesArray.indexOf(country);
                                if (index != -1) {
                                    PhoneView.this.ignoreSelection = true;
                                    PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(index));
                                    String hint = (String) PhoneView.this.phoneFormatMap.get(text);
                                    PhoneView.this.phoneField.setHintText(hint != null ? hint.replace('X', 8211) : null);
                                    PhoneView.this.countryState = 0;
                                } else {
                                    PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", NUM));
                                    PhoneView.this.phoneField.setHintText(null);
                                    PhoneView.this.countryState = 2;
                                }
                            } else {
                                PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", NUM));
                                PhoneView.this.phoneField.setHintText(null);
                                PhoneView.this.countryState = 2;
                            }
                            if (!ok) {
                                PhoneView.this.codeField.setSelection(PhoneView.this.codeField.getText().length());
                            }
                            if (textToSet != null) {
                                PhoneView.this.phoneField.requestFocus();
                                PhoneView.this.phoneField.setText(textToSet);
                                PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                            }
                        }
                        PhoneView.this.ignoreOnTextChange = false;
                    }
                }
            });
            this.codeField.setOnEditorActionListener(new LoginActivity$PhoneView$$Lambda$1(this));
            final LoginActivity loginActivity2 = LoginActivity.this;
            this.phoneField = new HintEditText(context) {
                public boolean onTouchEvent(MotionEvent event) {
                    if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.phoneField.setPadding(0, 0, 0, 0);
            this.phoneField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.phoneField.setCursorWidth(1.5f);
            this.phoneField.setTextSize(1, 18.0f);
            this.phoneField.setMaxLines(1);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(NUM);
            linearLayout.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            final LoginActivity loginActivity22 = LoginActivity.this;
            this.phoneField.addTextChangedListener(new TextWatcher() {
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
                    if (!PhoneView.this.ignoreOnPhoneChange) {
                        int a;
                        int start = PhoneView.this.phoneField.getSelectionStart();
                        String phoneChars = "NUM";
                        String str = PhoneView.this.phoneField.getText().toString();
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
                        PhoneView.this.ignoreOnPhoneChange = true;
                        String hint = PhoneView.this.phoneField.getHintText();
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
                        s.replace(0, s.length(), builder);
                        if (start >= 0) {
                            HintEditText access$1500 = PhoneView.this.phoneField;
                            if (start > PhoneView.this.phoneField.length()) {
                                start = PhoneView.this.phoneField.length();
                            }
                            access$1500.setSelection(start);
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            this.phoneField.setOnEditorActionListener(new LoginActivity$PhoneView$$Lambda$2(this));
            this.phoneField.setOnKeyListener(new LoginActivity$PhoneView$$Lambda$3(this));
            this.textView2 = new TextView(context);
            this.textView2.setText(LocaleController.getString("StartText", NUM));
            this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.textView2.setTextSize(1, 14.0f);
            this.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            if (LoginActivity.this.newAccount) {
                this.checkBoxCell = new CheckBoxCell(context, 2);
                this.checkBoxCell.setText(LocaleController.getString("SyncContacts", NUM), "", LoginActivity.this.syncContacts, false);
                addView(this.checkBoxCell, LayoutHelper.createLinear(-2, -1, 51, 0, 0, 0, 0));
                final LoginActivity loginActivity222 = LoginActivity.this;
                this.checkBoxCell.setOnClickListener(new OnClickListener() {
                    private Toast visibleToast;

                    public void onClick(View v) {
                        if (LoginActivity.this.getParentActivity() != null) {
                            CheckBoxCell cell = (CheckBoxCell) v;
                            LoginActivity.this.syncContacts = !LoginActivity.this.syncContacts;
                            cell.setChecked(LoginActivity.this.syncContacts, true);
                            try {
                                if (this.visibleToast != null) {
                                    this.visibleToast.cancel();
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            if (LoginActivity.this.syncContacts) {
                                this.visibleToast = Toast.makeText(LoginActivity.this.getParentActivity(), LocaleController.getString("SyncContactsOn", NUM), 0);
                                this.visibleToast.show();
                                return;
                            }
                            this.visibleToast = Toast.makeText(LoginActivity.this.getParentActivity(), LocaleController.getString("SyncContactsOff", NUM), 0);
                            this.visibleToast.show();
                        }
                    }
                });
            }
            HashMap<String, String> languageMap = new HashMap();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
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
                    languageMap.put(args[1], args[2]);
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            Collections.sort(this.countriesArray, LoginActivity$PhoneView$$Lambda$4.$instance);
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
                String countryName = (String) languageMap.get(country);
                if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                    this.codeField.setText((CharSequence) this.countriesMap.get(countryName));
                    this.countryState = 0;
                }
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", NUM));
                this.phoneField.setHintText(null);
                this.countryState = 1;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
                return;
            }
            this.codeField.requestFocus();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$2$LoginActivity$PhoneView(View view) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new LoginActivity$PhoneView$$Lambda$9(this));
            LoginActivity.this.presentFragment(fragment);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$LoginActivity$PhoneView(String name, String shortName) {
            selectCountry(name, shortName);
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$Lambda$10(this), 300);
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$0$LoginActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$3$LoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$4$LoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ boolean lambda$new$5$LoginActivity$PhoneView(View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            this.codeField.setSelection(this.codeField.length());
            this.codeField.dispatchKeyEvent(event);
            return true;
        }

        public void selectCountry(String name, String iso) {
            if (this.countriesArray.indexOf(name) != -1) {
                this.ignoreOnTextChange = true;
                String code = (String) this.countriesMap.get(name);
                this.codeField.setText(code);
                this.countryButton.setText(name);
                String hint = (String) this.phoneFormatMap.get(code);
                this.phoneField.setHintText(hint != null ? hint.replace('X', 8211) : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed() {
            if (LoginActivity.this.getParentActivity() != null && !this.nextPressed) {
                SharedPreferences preferences;
                Builder builder;
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("sim status = " + tm.getSimState());
                }
                int state = tm.getSimState();
                boolean simcardAvailable = (state == 1 || state == 0 || tm.getPhoneType() == 0 || AndroidUtilities.isAirplaneModeOn()) ? false : true;
                boolean allowCall = true;
                if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean allowCancelCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                    if (LoginActivity.this.checkPermissions) {
                        LoginActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowCancelCall) {
                            LoginActivity.this.permissionsItems.add("android.permission.CALL_PHONE");
                        }
                        boolean ok = true;
                        if (!LoginActivity.this.permissionsItems.isEmpty()) {
                            preferences = MessagesController.getGlobalMainSettings();
                            if (!allowCancelCall && allowCall) {
                                LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[0]), 6);
                            } else if (preferences.getBoolean("firstlogin", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                builder = new Builder(LoginActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                                builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                                LoginActivity.this.permissionsDialog = LoginActivity.this.showDialog(builder.create());
                            } else {
                                try {
                                    LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[0]), 6);
                                } catch (Exception e) {
                                    ok = false;
                                }
                            }
                            if (ok) {
                                return;
                            }
                        }
                    }
                }
                if (this.countryState == 1) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("ChooseCountry", NUM));
                } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("WrongCountry", NUM));
                } else if (this.codeField.length() == 0) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidPhoneNumber", NUM));
                } else {
                    String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    if (LoginActivity.this.getParentActivity() instanceof LaunchActivity) {
                        for (int a = 0; a < 3; a++) {
                            UserConfig userConfig = UserConfig.getInstance(a);
                            if (userConfig.isClientActivated()) {
                                String userPhone = userConfig.getCurrentUser().phone;
                                if (userPhone.contains(phone) || phone.contains(userPhone)) {
                                    int num = a;
                                    builder = new Builder(LoginActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", NUM));
                                    builder.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", NUM));
                                    builder.setPositiveButton(LocaleController.getString("AccountSwitch", NUM), new LoginActivity$PhoneView$$Lambda$5(this, num));
                                    builder.setNegativeButton(LocaleController.getString("OK", NUM), null);
                                    LoginActivity.this.showDialog(builder.create());
                                    return;
                                }
                            }
                        }
                    }
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).cleanup(false);
                    TL_auth_sendCode req = new TL_auth_sendCode();
                    req.api_hash = BuildVars.APP_HASH;
                    req.api_id = BuildVars.APP_ID;
                    req.phone_number = phone;
                    req.settings = new TL_codeSettings();
                    TL_codeSettings tL_codeSettings = req.settings;
                    boolean z = simcardAvailable && allowCall;
                    tL_codeSettings.allow_flashcall = z;
                    if (VERSION.SDK_INT >= 26) {
                        try {
                            req.settings.app_hash = SmsManager.getDefault().createAppSpecificSmsToken(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, SmsReceiver.class), NUM));
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                        }
                    } else {
                        req.settings.app_hash = BuildVars.SMS_HASH;
                        req.settings.app_hash_persistent = true;
                    }
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
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
                            if (!TextUtils.isEmpty(number)) {
                                tL_codeSettings = req.settings;
                                z = phone.contains(number) || number.contains(phone);
                                tL_codeSettings.current_number = z;
                                if (!req.settings.current_number) {
                                    req.settings.allow_flashcall = false;
                                }
                            } else if (UserConfig.getActivatedAccountsCount() > 0) {
                                req.settings.allow_flashcall = false;
                            } else {
                                req.settings.current_number = false;
                            }
                        } catch (Exception e22) {
                            req.settings.allow_flashcall = false;
                            FileLog.e(e22);
                        }
                    }
                    Bundle params = new Bundle();
                    params.putString("phone", "+" + this.codeField.getText() + " " + this.phoneField.getText());
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Exception e222) {
                        FileLog.e(e222);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    LoginActivity.this.needShowProgress(ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new LoginActivity$PhoneView$$Lambda$6(this, params, req), 27));
                }
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$6$LoginActivity$PhoneView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) LoginActivity.this.getParentActivity()).switchToAccount(num, false);
            }
            LoginActivity.this.finishFragment();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onNextPressed$8$LoginActivity$PhoneView(Bundle params, TL_auth_sendCode req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$Lambda$8(this, error, params, response, req));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$7$LoginActivity$PhoneView(TL_error error, Bundle params, TLObject response, TL_auth_sendCode req) {
            this.nextPressed = false;
            if (error == null) {
                LoginActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.this.needShowInvalidAlert(req.phone_number, false);
                } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("PhoneNumberFlood", NUM));
                } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                    LoginActivity.this.needShowInvalidAlert(req.phone_number, true);
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("InvalidCode", NUM));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("CodeExpired", NUM));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("FloodWait", NUM));
                } else if (error.code != -1000) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", NUM), error.text);
                }
            }
            LoginActivity.this.needHideProgress(false);
        }

        public void fillNumber() {
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (tm.getSimState() != 1 && tm.getPhoneType() != 0) {
                    boolean allowCall = true;
                    if (VERSION.SDK_INT >= 23) {
                        if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                            allowCall = true;
                        } else {
                            allowCall = false;
                        }
                        if (LoginActivity.this.checkShowPermissions && !allowCall) {
                            LoginActivity.this.permissionsShowItems.clear();
                            if (!allowCall) {
                                LoginActivity.this.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!LoginActivity.this.permissionsShowItems.isEmpty()) {
                                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                if (preferences.getBoolean("firstloginshow", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                                    preferences.edit().putBoolean("firstloginshow", false).commit();
                                    Builder builder = new Builder(LoginActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", NUM));
                                    builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                                    builder.setMessage(LocaleController.getString("AllowFillNumber", NUM));
                                    LoginActivity.this.permissionsShowDialog = LoginActivity.this.showDialog(builder.create());
                                    return;
                                }
                                LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsShowItems.toArray(new String[0]), 7);
                                return;
                            }
                            return;
                        }
                    }
                    if (!LoginActivity.this.newAccount && allowCall) {
                        String number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                        String textToSet = null;
                        boolean ok = false;
                        if (!TextUtils.isEmpty(number)) {
                            if (number.length() > 4) {
                                for (int a = 4; a >= 1; a--) {
                                    String sub = number.substring(0, a);
                                    if (((String) this.codesMap.get(sub)) != null) {
                                        ok = true;
                                        textToSet = number.substring(a);
                                        this.codeField.setText(sub);
                                        break;
                                    }
                                }
                                if (!ok) {
                                    textToSet = number.substring(1);
                                    this.codeField.setText(number.substring(0, 1));
                                }
                            }
                            if (textToSet != null) {
                                this.phoneField.requestFocus();
                                this.phoneField.setText(textToSet);
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
            if (this.checkBoxCell != null) {
                this.checkBoxCell.setChecked(LoginActivity.this.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new LoginActivity$PhoneView$$Lambda$7(this), 100);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onShow$9$LoginActivity$PhoneView() {
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
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
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("phoneview_code", code);
            }
            String phone = this.phoneField.getText().toString();
            if (phone.length() != 0) {
                bundle.putString("phoneview_phone", phone);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            String code = bundle.getString("phoneview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
            String phone = bundle.getString("phoneview_phone");
            if (phone != null) {
                this.phoneField.setText(phone);
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

    public LoginActivity() {
        this.views = new SlideView[9];
        this.permissionsItems = new ArrayList();
        this.permissionsShowItems = new ArrayList();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
    }

    public LoginActivity(int account) {
        this.views = new SlideView[9];
        this.permissionsItems = new ArrayList();
        this.permissionsShowItems = new ArrayList();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.currentAccount = account;
        this.newAccount = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        for (int a = 0; a < this.views.length; a++) {
            if (this.views[a] != null) {
                this.views[a].onDestroyActivity();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    if (LoginActivity.this.doneProgressView.getTag() == null) {
                        LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
                    } else if (LoginActivity.this.getParentActivity() != null) {
                        Builder builder = new Builder(LoginActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("StopLoading", NUM));
                        builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), null);
                        builder.setNegativeButton(LocaleController.getString("Stop", NUM), new LoginActivity$1$$Lambda$0(this));
                        LoginActivity.this.showDialog(builder.create());
                    }
                } else if (id == -1 && LoginActivity.this.onBackPressed()) {
                    LoginActivity.this.finishFragment();
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$0$LoginActivity$1(DialogInterface dialogInterface, int i) {
                LoginActivity.this.views[LoginActivity.this.currentViewNum].onCancelPressed();
                LoginActivity.this.needHideProgress(true);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.actionBar.setAllowOverlayTitle(true);
        this.doneItem = menu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneProgressView = new ContextProgressView(context, 1);
        this.doneProgressView.setAlpha(0.0f);
        this.doneProgressView.setScaleX(0.1f);
        this.doneProgressView.setScaleY(0.1f);
        this.doneProgressView.setVisibility(4);
        this.doneItem.addView(this.doneProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.doneItem.setContentDescription(LocaleController.getString("Done", NUM));
        ScrollView scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (LoginActivity.this.currentViewNum == 1 || LoginActivity.this.currentViewNum == 2 || LoginActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                LoginActivity.this.scrollHeight = MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        scrollView.setFillViewport(true);
        this.fragmentView = scrollView;
        FrameLayout frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(context);
        this.views[1] = new LoginActivitySmsView(context, 1);
        this.views[2] = new LoginActivitySmsView(context, 2);
        this.views[3] = new LoginActivitySmsView(context, 3);
        this.views[4] = new LoginActivitySmsView(context, 4);
        this.views[5] = new LoginActivityRegisterView(context);
        this.views[6] = new LoginActivityPasswordView(context);
        this.views[7] = new LoginActivityRecoverView(this, context);
        this.views[8] = new LoginActivityResetWaitView(this, context);
        int a = 0;
        while (a < this.views.length) {
            this.views[a].setVisibility(a == 0 ? 0 : 8);
            frameLayout.addView(this.views[a], LayoutHelper.createFrame(-1, -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            a++;
        }
        Bundle savedInstanceState = loadCurrentState();
        if (savedInstanceState != null) {
            this.currentViewNum = savedInstanceState.getInt("currentViewNum", 0);
            this.syncContacts = savedInstanceState.getInt("syncContacts", 1) == 1;
            if (this.currentViewNum >= 1 && this.currentViewNum <= 4) {
                int time = savedInstanceState.getInt("open");
                if (time != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                    this.currentViewNum = 0;
                    savedInstanceState = null;
                    clearCurrentState();
                }
            } else if (this.currentViewNum == 6) {
                LoginActivityPasswordView view = this.views[6];
                if (view.passwordType == 0 || view.current_salt1 == null || view.current_salt2 == null) {
                    this.currentViewNum = 0;
                    savedInstanceState = null;
                    clearCurrentState();
                }
            }
        }
        a = 0;
        while (a < this.views.length) {
            if (savedInstanceState != null) {
                if (a < 1 || a > 4) {
                    this.views[a].restoreStateParams(savedInstanceState);
                } else if (a == this.currentViewNum) {
                    this.views[a].restoreStateParams(savedInstanceState);
                }
            }
            if (this.currentViewNum == a) {
                ActionBar actionBar = this.actionBar;
                int i = (this.views[a].needBackButton() || this.newAccount) ? NUM : 0;
                actionBar.setBackButtonImage(i);
                this.views[a].setVisibility(0);
                this.views[a].onShow();
                if (a == 3 || a == 8) {
                    this.doneItem.setVisibility(8);
                }
            } else {
                this.views[a].setVisibility(8);
            }
            a++;
        }
        this.actionBar.setTitle(this.views[this.currentViewNum].getHeaderName());
        return this.fragmentView;
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
                int time = ((LoginActivitySmsView) this.views[this.currentViewNum]).openTime;
                if (time != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed(true);
                    setPage(0, false, null, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            if (this.currentViewNum == 0) {
                this.views[this.currentViewNum].onNextPressed();
            }
        } else if (requestCode == 7) {
            this.checkShowPermissions = false;
            if (this.currentViewNum == 0) {
                ((PhoneView) this.views[this.currentViewNum]).fillNumber();
            }
        }
    }

    private Bundle loadCurrentState() {
        if (this.newAccount) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            for (Entry<String, ?> entry : ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet()) {
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                String[] args = key.split("_\\|_");
                if (args.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, ((Integer) value).intValue());
                    }
                } else if (args.length == 2) {
                    Bundle inner = bundle.getBundle(args[0]);
                    if (inner == null) {
                        inner = new Bundle();
                        bundle.putBundle(args[0], inner);
                    }
                    if (value instanceof String) {
                        inner.putString(args[1], (String) value);
                    } else if (value instanceof Integer) {
                        inner.putInt(args[1], ((Integer) value).intValue());
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
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        editor.clear();
        editor.commit();
    }

    private void putBundleToEditor(Bundle bundle, Editor editor, String prefix) {
        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            if (obj instanceof String) {
                if (prefix != null) {
                    editor.putString(prefix + "_|_" + key, (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    editor.putInt(prefix + "_|_" + key, ((Integer) obj).intValue());
                } else {
                    editor.putInt(key, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
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
            } catch (Exception e) {
            }
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
            } catch (Exception e2) {
            }
        }
    }

    public boolean onBackPressed() {
        if (this.currentViewNum == 0) {
            for (int a = 0; a < this.views.length; a++) {
                if (this.views[a] != null) {
                    this.views[a].onDestroyActivity();
                }
            }
            clearCurrentState();
            return true;
        }
        if (this.currentViewNum == 6) {
            this.views[this.currentViewNum].onBackPressed(true);
            setPage(0, true, null, true);
        } else if (this.currentViewNum == 7 || this.currentViewNum == 8) {
            this.views[this.currentViewNum].onBackPressed(true);
            setPage(6, true, null, true);
        } else if (this.currentViewNum < 1 || this.currentViewNum > 4) {
            if (this.currentViewNum == 5) {
                ((LoginActivityRegisterView) this.views[this.currentViewNum]).wrongNumber.callOnClick();
            }
        } else if (this.views[this.currentViewNum].onBackPressed(false)) {
            setPage(0, true, null, true);
        }
        return false;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        LoginActivityRegisterView registerView = this.views[5];
        if (registerView != null) {
            registerView.imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void needShowAlert(String title, String text) {
        if (text != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    private void needShowInvalidAlert(String phoneNumber, boolean banned) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (banned) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", NUM));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", NUM));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", NUM), new LoginActivity$$Lambda$0(this, banned, phoneNumber));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$needShowInvalidAlert$0$LoginActivity(boolean banned, String phoneNumber, DialogInterface dialog, int which) {
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
            Intent mailer = new Intent("android.intent.action.SEND");
            mailer.setType("message/rfCLASSNAME");
            mailer.putExtra("android.intent.extra.EMAIL", new String[]{"login@stel.com"});
            if (banned) {
                mailer.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            } else {
                mailer.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            getParentActivity().startActivity(Intent.createChooser(mailer, "Send email..."));
        } catch (Exception e) {
            needShowAlert(LocaleController.getString("AppName", NUM), LocaleController.getString("NoMailInstalled", NUM));
        }
    }

    private void showEditDoneProgress(final boolean show) {
        if (this.doneItemAnimation != null) {
            this.doneItemAnimation.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.doneProgressView.setTag(Integer.valueOf(1));
            this.doneProgressView.setVisibility(0);
            animatorSet = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.doneProgressView, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.doneProgressView, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.doneProgressView, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            this.doneProgressView.setTag(null);
            this.doneItem.getImageView().setVisibility(0);
            animatorSet = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneProgressView, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneProgressView, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneProgressView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                    if (show) {
                        LoginActivity.this.doneItem.getImageView().setVisibility(4);
                    } else {
                        LoginActivity.this.doneProgressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                    LoginActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    private void needShowProgress(int reqiestId) {
        this.progressRequestId = reqiestId;
        showEditDoneProgress(true);
    }

    public void needHideProgress(boolean cancel) {
        if (this.progressRequestId != 0) {
            if (cancel) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        showEditDoneProgress(false);
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        int i = NUM;
        if (page == 3 || page == 8) {
            this.doneItem.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.doneItem.setVisibility(0);
        }
        ActionBar actionBar;
        if (animated) {
            float f;
            final SlideView outView = this.views[this.currentViewNum];
            SlideView newView = this.views[page];
            this.currentViewNum = page;
            actionBar = this.actionBar;
            if (!(newView.needBackButton() || this.newAccount)) {
                i = 0;
            }
            actionBar.setBackButtonImage(i);
            newView.setParams(params, false);
            this.actionBar.setTitle(newView.getHeaderName());
            setParentActivityTitle(newView.getHeaderName());
            newView.onShow();
            if (back) {
                f = (float) (-AndroidUtilities.displaySize.x);
            } else {
                f = (float) AndroidUtilities.displaySize.x;
            }
            newView.setX(f);
            newView.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = back ? (float) AndroidUtilities.displaySize.x : (float) (-AndroidUtilities.displaySize.x);
            animatorArr[0] = ObjectAnimator.ofFloat(outView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
            return;
        }
        actionBar = this.actionBar;
        if (!(this.views[page].needBackButton() || this.newAccount)) {
            i = 0;
        }
        actionBar.setBackButtonImage(i);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = page;
        this.views[page].setParams(params, false);
        this.views[page].setVisibility(0);
        this.actionBar.setTitle(this.views[page].getHeaderName());
        setParentActivityTitle(this.views[page].getHeaderName());
        this.views[page].onShow();
    }

    public void saveSelfArgs(Bundle outState) {
        int i = 0;
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            String str = "syncContacts";
            if (this.syncContacts) {
                i = 1;
            }
            bundle.putInt(str, i);
            for (int a = 0; a <= this.currentViewNum; a++) {
                SlideView v = this.views[a];
                if (v != null) {
                    v.saveStateParams(bundle);
                }
            }
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            editor.clear();
            putBundleToEditor(bundle, editor, null);
            editor.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void needFinishActivity() {
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false);
                finishFragment();
                return;
            }
            presentFragment(new DialogsActivity(null), true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    private void onAuthSuccess(TL_auth_authorization res) {
        ConnectionsManager.getInstance(this.currentAccount).setUserId(res.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(res.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList<User> users = new ArrayList();
        users.add(res.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(res.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).getBlockedUsers(true);
        MessagesController.getInstance(this.currentAccount).checkProxyInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        needFinishActivity();
    }

    private void fillNextCodeParams(Bundle params, TL_auth_sentCode res) {
        if (res.terms_of_service != null) {
            this.currentTermsOfService = res.terms_of_service;
        }
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.type instanceof TL_auth_sentCodeTypeApp) {
            params.putInt("type", 1);
            params.putInt("length", res.type.length);
            setPage(1, true, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(4, true, params, false);
        } else if (res.type instanceof TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, true, params, false);
        } else if (res.type instanceof TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(2, true, params, false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        for (SlideView slideView : this.views) {
            if (slideView == null) {
                return new ThemeDescription[0];
            }
        }
        PhoneView phoneView = this.views[0];
        LoginActivitySmsView smsView1 = this.views[1];
        LoginActivitySmsView smsView2 = this.views[2];
        LoginActivitySmsView smsView3 = this.views[3];
        LoginActivitySmsView smsView4 = this.views[4];
        LoginActivityRegisterView registerView = this.views[5];
        LoginActivityPasswordView passwordView = this.views[6];
        LoginActivityRecoverView recoverView = this.views[7];
        LoginActivityResetWaitView waitView = this.views[8];
        ArrayList<ThemeDescription> arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhiteGrayLine"));
        arrayList.add(new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(passwordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(passwordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(passwordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteRedText6"));
        arrayList.add(new ThemeDescription(passwordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(registerView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(registerView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(registerView.privacyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(registerView.privacyView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(recoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(recoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(waitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(waitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(waitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText6"));
        arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView1.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (smsView1.codeField != null) {
            for (a = 0; a < smsView1.codeField.length; a++) {
                arrayList.add(new ThemeDescription(smsView1.codeField[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView1.codeField[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView1.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView1.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (smsView2.codeField != null) {
            for (a = 0; a < smsView2.codeField.length; a++) {
                arrayList.add(new ThemeDescription(smsView2.codeField[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView2.codeField[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (smsView3.codeField != null) {
            for (a = 0; a < smsView3.codeField.length; a++) {
                arrayList.add(new ThemeDescription(smsView3.codeField[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView3.codeField[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (smsView4.codeField != null) {
            for (a = 0; a < smsView4.codeField.length; a++) {
                arrayList.add(new ThemeDescription(smsView4.codeField[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView4.codeField[a], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
