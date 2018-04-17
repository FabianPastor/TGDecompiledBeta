package org.telegram.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
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
import android.graphics.Typeface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions;
    private boolean checkShowPermissions;
    private int currentViewNum;
    private View doneButton;
    private boolean newAccount;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems;
    private AlertDialog progressDialog;
    private boolean syncContacts;
    private SlideView[] views;

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

    /* renamed from: org.telegram.ui.LoginActivity$1 */
    class C21761 extends ActionBarMenuOnItemClick {
        C21761() {
        }

        public void onItemClick(int id) {
            if (id == 1) {
                LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
            } else if (id == -1) {
                LoginActivity.this.onBackPressed();
            }
        }
    }

    public class LoginActivityPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private byte[] current_salt;
        private String email_unconfirmed_pattern;
        private boolean has_recovery;
        private String hint;
        private boolean nextPressed;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;
        final /* synthetic */ LoginActivity this$0;

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$5 */
        class C14745 implements Runnable {
            C14745() {
            }

            public void run() {
                if (LoginActivityPasswordView.this.codeField != null) {
                    LoginActivityPasswordView.this.codeField.requestFocus();
                    LoginActivityPasswordView.this.codeField.setSelection(LoginActivityPasswordView.this.codeField.length());
                    AndroidUtilities.showKeyboard(LoginActivityPasswordView.this.codeField);
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$4 */
        class C21794 implements RequestDelegate {
            C21794() {
            }

            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        LoginActivityPasswordView.this.this$0.needHideProgress();
                        LoginActivityPasswordView.this.nextPressed = false;
                        if (error == null) {
                            TL_auth_authorization res = response;
                            ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).setUserId(res.user.id);
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).clearConfig();
                            MessagesController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).cleanup();
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).setCurrentUser(res.user);
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).syncContacts = LoginActivityPasswordView.this.this$0.syncContacts;
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).saveConfig(true);
                            MessagesStorage.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).cleanup(true);
                            ArrayList<User> users = new ArrayList();
                            users.add(res.user);
                            MessagesStorage.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).putUsersAndChats(users, null, true, true);
                            MessagesController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).putUser(res.user, false);
                            ContactsController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).checkAppAccount();
                            MessagesController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).getBlockedUsers(true);
                            ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).updateDcSettings();
                            LoginActivityPasswordView.this.this$0.needFinishActivity();
                        } else if (error.text.equals("PASSWORD_HASH_INVALID")) {
                            LoginActivityPasswordView.this.onPasscodeError(true);
                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                            String timeString;
                            int time = Utilities.parseInt(error.text).intValue();
                            if (time < 60) {
                                timeString = LocaleController.formatPluralString("Seconds", time);
                            } else {
                                timeString = LocaleController.formatPluralString("Minutes", time / 60);
                            }
                            LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                        } else {
                            LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                        }
                    }
                });
            }
        }

        public LoginActivityPasswordView(LoginActivity this$0, Context context) {
            final LoginActivity loginActivity = this$0;
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 3;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.confirmTextView.setText(LocaleController.getString("LoginPasswordText", R.string.LoginPasswordText));
            addView(r0.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            r0.codeField = new EditTextBoldCursor(context2);
            r0.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.codeField.setCursorWidth(1.5f);
            r0.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.codeField.setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
            r0.codeField.setImeOptions(268435461);
            r0.codeField.setTextSize(1, 18.0f);
            r0.codeField.setMaxLines(1);
            r0.codeField.setPadding(0, 0, 0, 0);
            r0.codeField.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
            r0.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            r0.codeField.setTypeface(Typeface.DEFAULT);
            r0.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(r0.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            r0.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivityPasswordView.this.onNextPressed();
                    return true;
                }
            });
            r0.cancelButton = new TextView(context2);
            r0.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.cancelButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.cancelButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
            r0.cancelButton.setTextSize(1, 14.0f);
            r0.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(r0.cancelButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48));
            r0.cancelButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$2$1 */
                class C21771 implements RequestDelegate {
                    C21771() {
                    }

                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivityPasswordView.this.this$0.needHideProgress();
                                if (error == null) {
                                    final TL_auth_passwordRecovery res = response;
                                    Builder builder = new Builder(LoginActivityPasswordView.this.this$0.getParentActivity());
                                    builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
                                            LoginActivityPasswordView.this.this$0.setPage(7, true, bundle, false);
                                        }
                                    });
                                    Dialog dialog = LoginActivityPasswordView.this.this$0.showDialog(builder.create());
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
                                    LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                } else {
                                    LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            }
                        });
                    }
                }

                public void onClick(View view) {
                    if (LoginActivityPasswordView.this.has_recovery) {
                        LoginActivityPasswordView.this.this$0.needShowProgress(0);
                        ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new C21771(), 10);
                        return;
                    }
                    LoginActivityPasswordView.this.resetAccountText.setVisibility(0);
                    LoginActivityPasswordView.this.resetAccountButton.setVisibility(0);
                    AndroidUtilities.hideKeyboard(LoginActivityPasswordView.this.codeField);
                    LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                }
            });
            r0.resetAccountButton = new TextView(context2);
            r0.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText6));
            r0.resetAccountButton.setVisibility(8);
            r0.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", R.string.ResetMyAccount));
            r0.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.resetAccountButton.setTextSize(1, 14.0f);
            r0.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(r0.resetAccountButton, LayoutHelper.createLinear(-2, -2, 48 | (LocaleController.isRTL ? 5 : 3), 0, 34, 0, 0));
            r0.resetAccountButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$3$1 */
                class C14711 implements DialogInterface.OnClickListener {

                    /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$3$1$1 */
                    class C21781 implements RequestDelegate {
                        C21781() {
                        }

                        public void run(TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    LoginActivityPasswordView.this.this$0.needHideProgress();
                                    Bundle params;
                                    if (error == null) {
                                        params = new Bundle();
                                        params.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                        params.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                        params.putString("code", LoginActivityPasswordView.this.phoneCode);
                                        LoginActivityPasswordView.this.this$0.setPage(5, true, params, false);
                                    } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                                        LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
                                    } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                                        params = new Bundle();
                                        params.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                        params.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                        params.putString("code", LoginActivityPasswordView.this.phoneCode);
                                        params.putInt("startTime", ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).getCurrentTime());
                                        params.putInt("waitTime", Utilities.parseInt(error.text.replace("2FA_CONFIRM_WAIT_", TtmlNode.ANONYMOUS_REGION_ID)).intValue());
                                        LoginActivityPasswordView.this.this$0.setPage(8, true, params, false);
                                    } else {
                                        LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                    }
                                }
                            });
                        }
                    }

                    C14711() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityPasswordView.this.this$0.needShowProgress(0);
                        TL_account_deleteAccount req = new TL_account_deleteAccount();
                        req.reason = "Forgot password";
                        ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).sendRequest(req, new C21781(), 10);
                    }
                }

                public void onClick(View view) {
                    Builder builder = new Builder(LoginActivityPasswordView.this.this$0.getParentActivity());
                    builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                    builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                    builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new C14711());
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    LoginActivityPasswordView.this.this$0.showDialog(builder.create());
                }
            });
            r0.resetAccountText = new TextView(context2);
            r0.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountText.setVisibility(8);
            r0.resetAccountText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r0.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", R.string.ResetMyAccountText));
            r0.resetAccountText.setTextSize(1, 14.0f);
            r0.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            View view = r0.resetAccountText;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, 48 | i, 0, 7, 0, 14));
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                boolean z = false;
                if (params.isEmpty()) {
                    this.resetAccountButton.setVisibility(0);
                    this.resetAccountText.setVisibility(0);
                    AndroidUtilities.hideKeyboard(this.codeField);
                    return;
                }
                this.resetAccountButton.setVisibility(8);
                this.resetAccountText.setVisibility(8);
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.currentParams = params;
                this.current_salt = Utilities.hexToBytes(this.currentParams.getString("current_salt"));
                this.hint = this.currentParams.getString(TrackReferenceTypeBox.TYPE1);
                if (this.currentParams.getInt("has_recovery") == 1) {
                    z = true;
                }
                this.has_recovery = z;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                if (this.hint == null || this.hint.length() <= 0) {
                    this.codeField.setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                } else {
                    this.codeField.setHint(this.hint);
                }
            }
        }

        private void onPasscodeError(boolean clear) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator v = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                if (clear) {
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
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
                byte[] oldPasswordBytes = null;
                try {
                    oldPasswordBytes = oldPassword.getBytes(C0539C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.this$0.needShowProgress(0);
                byte[] hash = new byte[((this.current_salt.length * 2) + oldPasswordBytes.length)];
                System.arraycopy(this.current_salt, 0, hash, 0, this.current_salt.length);
                System.arraycopy(oldPasswordBytes, 0, hash, this.current_salt.length, oldPasswordBytes.length);
                System.arraycopy(this.current_salt, 0, hash, hash.length - this.current_salt.length, this.current_salt.length);
                TL_auth_checkPassword req = new TL_auth_checkPassword();
                req.password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new C21794(), 10);
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new C14745(), 100);
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

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRecoverView$4 */
        class C14794 implements Runnable {
            C14794() {
            }

            public void run() {
                if (LoginActivityRecoverView.this.codeField != null) {
                    LoginActivityRecoverView.this.codeField.requestFocus();
                    LoginActivityRecoverView.this.codeField.setSelection(LoginActivityRecoverView.this.codeField.length());
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRecoverView$3 */
        class C21803 implements RequestDelegate {
            C21803() {
            }

            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        LoginActivityRecoverView.this.this$0.needHideProgress();
                        LoginActivityRecoverView.this.nextPressed = false;
                        if (error == null) {
                            TL_auth_authorization res = response;
                            ConnectionsManager.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).setUserId(res.user.id);
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).clearConfig();
                            MessagesController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).cleanup();
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).setCurrentUser(res.user);
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).syncContacts = LoginActivityRecoverView.this.this$0.syncContacts;
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).saveConfig(true);
                            MessagesStorage.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).cleanup(true);
                            ArrayList<User> users = new ArrayList();
                            users.add(res.user);
                            MessagesStorage.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).putUsersAndChats(users, null, true, true);
                            MessagesController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).putUser(res.user, false);
                            ContactsController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).checkAppAccount();
                            MessagesController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).getBlockedUsers(true);
                            ConnectionsManager.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).updateDcSettings();
                            LoginActivityRecoverView.this.this$0.needFinishActivity();
                        } else if (error.text.startsWith("CODE_INVALID")) {
                            LoginActivityRecoverView.this.onPasscodeError(true);
                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                            String timeString;
                            int time = Utilities.parseInt(error.text).intValue();
                            if (time < 60) {
                                timeString = LocaleController.formatPluralString("Seconds", time);
                            } else {
                                timeString = LocaleController.formatPluralString("Minutes", time / 60);
                            }
                            LoginActivityRecoverView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                        } else {
                            LoginActivityRecoverView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                        }
                    }
                });
            }
        }

        public LoginActivityRecoverView(LoginActivity this$0, Context context) {
            final LoginActivity loginActivity = this$0;
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", R.string.RestoreEmailSentInfo));
            addView(r0.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            r0.codeField = new EditTextBoldCursor(context2);
            r0.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.codeField.setCursorWidth(1.5f);
            r0.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.codeField.setHint(LocaleController.getString("PasswordCode", R.string.PasswordCode));
            r0.codeField.setImeOptions(268435461);
            r0.codeField.setTextSize(1, 18.0f);
            r0.codeField.setMaxLines(1);
            r0.codeField.setPadding(0, 0, 0, 0);
            r0.codeField.setInputType(3);
            r0.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            r0.codeField.setTypeface(Typeface.DEFAULT);
            r0.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(r0.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            r0.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivityRecoverView.this.onNextPressed();
                    return true;
                }
            });
            r0.cancelButton = new TextView(context2);
            r0.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
            r0.cancelButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.cancelButton.setTextSize(1, 14.0f);
            r0.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            View view = r0.cancelButton;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, 80 | i, 0, 0, 0, 14));
            r0.cancelButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRecoverView$2$1 */
                class C14761 implements DialogInterface.OnClickListener {
                    C14761() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityRecoverView.this.this$0.setPage(6, true, new Bundle(), true);
                    }
                }

                public void onClick(View view) {
                    Builder builder = new Builder(LoginActivityRecoverView.this.this$0.getParentActivity());
                    builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
                    builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C14761());
                    Dialog dialog = LoginActivityRecoverView.this.this$0.showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                    }
                }
            });
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.currentParams = params;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", R.string.RestoreEmailTrouble, this.email_unconfirmed_pattern));
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
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
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
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new C21803(), 10);
            }
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new C14794(), 100);
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

    public class LoginActivityRegisterView extends SlideView {
        private Bundle currentParams;
        private EditTextBoldCursor firstNameField;
        private EditTextBoldCursor lastNameField;
        private boolean nextPressed = false;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView textView;
        final /* synthetic */ LoginActivity this$0;
        private TextView wrongNumber;

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4 */
        class C14844 implements Runnable {
            C14844() {
            }

            public void run() {
                if (LoginActivityRegisterView.this.firstNameField != null) {
                    LoginActivityRegisterView.this.firstNameField.requestFocus();
                    LoginActivityRegisterView.this.firstNameField.setSelection(LoginActivityRegisterView.this.firstNameField.length());
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$5 */
        class C21815 implements RequestDelegate {
            C21815() {
            }

            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        LoginActivityRegisterView.this.nextPressed = false;
                        LoginActivityRegisterView.this.this$0.needHideProgress();
                        if (error == null) {
                            TL_auth_authorization res = response;
                            ConnectionsManager.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).setUserId(res.user.id);
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).clearConfig();
                            MessagesController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).cleanup();
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).setCurrentUser(res.user);
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).syncContacts = LoginActivityRegisterView.this.this$0.syncContacts;
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).saveConfig(true);
                            MessagesStorage.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).cleanup(true);
                            ArrayList<User> users = new ArrayList();
                            users.add(res.user);
                            MessagesStorage.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).putUsersAndChats(users, null, true, true);
                            MessagesController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).putUser(res.user, false);
                            ContactsController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).checkAppAccount();
                            MessagesController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).getBlockedUsers(true);
                            ConnectionsManager.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).updateDcSettings();
                            LoginActivityRegisterView.this.this$0.needFinishActivity();
                        } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                        } else {
                            if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                if (!error.text.contains("PHONE_CODE_INVALID")) {
                                    if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                        return;
                                    } else if (error.text.contains("FIRSTNAME_INVALID")) {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidFirstName", R.string.InvalidFirstName));
                                        return;
                                    } else if (error.text.contains("LASTNAME_INVALID")) {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidLastName", R.string.InvalidLastName));
                                        return;
                                    } else {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                        return;
                                    }
                                }
                            }
                            LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                        }
                    }
                });
            }
        }

        public LoginActivityRegisterView(LoginActivity this$0, Context context) {
            final LoginActivity loginActivity = this$0;
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.textView = new TextView(context2);
            this.textView.setText(LocaleController.getString("RegisterText", R.string.RegisterText));
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            int i = 3;
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.textView.setTextSize(1, 14.0f);
            addView(r0.textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            r0.firstNameField = new EditTextBoldCursor(context2);
            r0.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.firstNameField.setCursorWidth(1.5f);
            r0.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
            r0.firstNameField.setImeOptions(268435461);
            r0.firstNameField.setTextSize(1, 18.0f);
            r0.firstNameField.setMaxLines(1);
            r0.firstNameField.setInputType(MessagesController.UPDATE_MASK_CHANNEL);
            addView(r0.firstNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 26.0f, 0.0f, 0.0f));
            r0.firstNameField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivityRegisterView.this.lastNameField.requestFocus();
                    return true;
                }
            });
            r0.lastNameField = new EditTextBoldCursor(context2);
            r0.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
            r0.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.lastNameField.setCursorWidth(1.5f);
            r0.lastNameField.setImeOptions(268435462);
            r0.lastNameField.setTextSize(1, 18.0f);
            r0.lastNameField.setMaxLines(1);
            r0.lastNameField.setInputType(MessagesController.UPDATE_MASK_CHANNEL);
            addView(r0.lastNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 10.0f, 0.0f, 0.0f));
            r0.lastNameField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 6) {
                        if (i != 5) {
                            return false;
                        }
                    }
                    LoginActivityRegisterView.this.onNextPressed();
                    return true;
                }
            });
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setGravity(80);
            addView(linearLayout, LayoutHelper.createLinear(-1, -1));
            r0.wrongNumber = new TextView(context2);
            r0.wrongNumber.setText(LocaleController.getString("CancelRegistration", R.string.CancelRegistration));
            r0.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            r0.wrongNumber.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.wrongNumber.setTextSize(1, 14.0f);
            r0.wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            View view = r0.wrongNumber;
            if (LocaleController.isRTL) {
                i = 5;
            }
            linearLayout.addView(view, LayoutHelper.createLinear(-2, -2, 80 | i, 0, 0, 0, 10));
            r0.wrongNumber.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$3$1 */
                class C14821 implements DialogInterface.OnClickListener {
                    C14821() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityRegisterView.this.onBackPressed();
                        LoginActivityRegisterView.this.this$0.setPage(0, true, null, true);
                    }
                }

                public void onClick(View view) {
                    Builder builder = new Builder(LoginActivityRegisterView.this.this$0.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C14821());
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    LoginActivityRegisterView.this.this$0.showDialog(builder.create());
                }
            });
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", R.string.YourName);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new C14844(), 100);
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.firstNameField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.lastNameField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                this.currentParams = params;
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                TL_auth_signUp req = new TL_auth_signUp();
                req.phone_code = this.phoneCode;
                req.phone_code_hash = this.phoneHash;
                req.phone_number = this.requestPhone;
                req.first_name = this.firstNameField.getText().toString();
                req.last_name = this.lastNameField.getText().toString();
                this.this$0.needShowProgress(0);
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new C21815(), 10);
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
            if (this.currentParams != null) {
                bundle.putBundle("registerview_params", this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("registerview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
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

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$2 */
        class C14892 implements Runnable {
            C14892() {
            }

            public void run() {
                if (LoginActivityResetWaitView.this.timeRunnable == this) {
                    LoginActivityResetWaitView.this.updateTimeText();
                    AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                }
            }
        }

        public LoginActivityResetWaitView(LoginActivity this$0, Context context) {
            final LoginActivity loginActivity = this$0;
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 3;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r0.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            r0.resetAccountText = new TextView(context2);
            r0.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r0.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", R.string.ResetAccountStatus));
            r0.resetAccountText.setTextSize(1, 14.0f);
            r0.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r0.resetAccountText, LayoutHelper.createLinear(-2, -2, 48 | (LocaleController.isRTL ? 5 : 3), 0, 24, 0, 0));
            r0.resetAccountTime = new TextView(context2);
            r0.resetAccountTime.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountTime.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r0.resetAccountTime.setTextSize(1, 14.0f);
            r0.resetAccountTime.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r0.resetAccountTime, LayoutHelper.createLinear(-2, -2, 48 | (LocaleController.isRTL ? 5 : 3), 0, 2, 0, 0));
            r0.resetAccountButton = new TextView(context2);
            r0.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", R.string.ResetAccountButton));
            r0.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.resetAccountButton.setTextSize(1, 14.0f);
            r0.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            View view = r0.resetAccountButton;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, 48 | i, 0, 7, 0, 0));
            r0.resetAccountButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$1$1 */
                class C14871 implements DialogInterface.OnClickListener {

                    /* renamed from: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$1$1$1 */
                    class C21821 implements RequestDelegate {
                        C21821() {
                        }

                        public void run(TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    LoginActivityResetWaitView.this.this$0.needHideProgress();
                                    if (error == null) {
                                        Bundle params = new Bundle();
                                        params.putString("phoneFormated", LoginActivityResetWaitView.this.requestPhone);
                                        params.putString("phoneHash", LoginActivityResetWaitView.this.phoneHash);
                                        params.putString("code", LoginActivityResetWaitView.this.phoneCode);
                                        LoginActivityResetWaitView.this.this$0.setPage(5, true, params, false);
                                    } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                                        LoginActivityResetWaitView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
                                    } else {
                                        LoginActivityResetWaitView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                    }
                                }
                            });
                        }
                    }

                    C14871() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityResetWaitView.this.this$0.needShowProgress(0);
                        TL_account_deleteAccount req = new TL_account_deleteAccount();
                        req.reason = "Forgot password";
                        ConnectionsManager.getInstance(LoginActivityResetWaitView.this.this$0.currentAccount).sendRequest(req, new C21821(), 10);
                    }
                }

                public void onClick(View view) {
                    if (Math.abs(ConnectionsManager.getInstance(LoginActivityResetWaitView.this.this$0.currentAccount).getCurrentTime() - LoginActivityResetWaitView.this.startTime) >= LoginActivityResetWaitView.this.waitTime) {
                        Builder builder = new Builder(LoginActivityResetWaitView.this.this$0.getParentActivity());
                        builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                        builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                        builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new C14871());
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        LoginActivityResetWaitView.this.this$0.showDialog(builder.create());
                    }
                }
            });
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", R.string.ResetAccount);
        }

        private void updateTimeText() {
            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int days = timeLeft / 86400;
            int hours = (timeLeft - (days * 86400)) / 3600;
            int minutes = ((timeLeft - (86400 * days)) - (hours * 3600)) / 60;
            int seconds = timeLeft % 60;
            TextView textView;
            StringBuilder stringBuilder;
            if (days != 0) {
                textView = this.resetAccountTime;
                stringBuilder = new StringBuilder();
                stringBuilder.append(LocaleController.formatPluralString("DaysBold", days));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("HoursBold", hours));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("MinutesBold", minutes));
                textView.setText(AndroidUtilities.replaceTags(stringBuilder.toString()));
            } else {
                textView = this.resetAccountTime;
                stringBuilder = new StringBuilder();
                stringBuilder.append(LocaleController.formatPluralString("HoursBold", hours));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("MinutesBold", minutes));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("SecondsBold", seconds));
                textView.setText(AndroidUtilities.replaceTags(stringBuilder.toString()));
            }
            if (timeLeft > 0) {
                this.resetAccountButton.setTag(Theme.key_windowBackgroundWhiteGrayText6);
                this.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
                return;
            }
            this.resetAccountButton.setTag(Theme.key_windowBackgroundWhiteRedText6);
            this.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText6));
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.currentParams = params;
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                this.startTime = params.getInt("startTime");
                this.waitTime = params.getInt("waitTime");
                TextView textView = this.confirmTextView;
                Object[] objArr = new Object[1];
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(this.requestPhone);
                objArr[0] = LocaleController.addNbsp(instance.format(stringBuilder.toString()));
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", R.string.ResetAccountInfo, objArr)));
                updateTimeText();
                this.timeRunnable = new C14892();
                AndroidUtilities.runOnUIThread(this.timeRunnable, 1000);
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public void onBackPressed() {
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
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
        private String catchedPhone;
        private EditTextBoldCursor codeField;
        private volatile int codeTime = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private String emailPhone;
        private boolean ignoreOnTextChange;
        private boolean isRestored;
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
        private String requestPhone;
        final /* synthetic */ LoginActivity this$0;
        private volatile int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private boolean waitingForEvent;
        private TextView wrongNumber;

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$6 */
        class C14966 extends TimerTask {

            /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$6$1 */
            class C14951 implements Runnable {
                C14951() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= 1000) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C14966() {
            }

            public void run() {
                double currentTime = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTime - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTime;
                AndroidUtilities.runOnUIThread(new C14951());
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$7 */
        class C14997 extends TimerTask {

            /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$7$1 */
            class C14981 implements Runnable {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$7$1$1 */
                class C21851 implements RequestDelegate {
                    C21851() {
                    }

                    public void run(TLObject response, final TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    LoginActivitySmsView.this.lastError = error.text;
                                }
                            });
                        }
                    }
                }

                C14981() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= 1000) {
                        int seconds = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType != 4) {
                            if (LoginActivitySmsView.this.nextType != 3) {
                                if (LoginActivitySmsView.this.nextType == 2) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                }
                                if (LoginActivitySmsView.this.progressView != null) {
                                    LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                }
                                return;
                            }
                        }
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                        if (LoginActivitySmsView.this.progressView != null) {
                            LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
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
                    } else if (LoginActivitySmsView.this.currentType != 2) {
                    } else {
                        if (LoginActivitySmsView.this.nextType == 4) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                            LoginActivitySmsView.this.createCodeTimer();
                            TL_auth_resendCode req = new TL_auth_resendCode();
                            req.phone_number = LoginActivitySmsView.this.requestPhone;
                            req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new C21851(), 10);
                        } else if (LoginActivitySmsView.this.nextType == 3) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            LoginActivitySmsView.this.waitingForEvent = false;
                            LoginActivitySmsView.this.destroyCodeTimer();
                            LoginActivitySmsView.this.resendCode();
                        }
                    }
                }
            }

            C14997() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTime = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTime - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTime;
                    AndroidUtilities.runOnUIThread(new C14981());
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$9 */
        class C15029 implements Runnable {
            C15029() {
            }

            public void run() {
                if (LoginActivitySmsView.this.codeField != null) {
                    LoginActivitySmsView.this.codeField.requestFocus();
                    LoginActivitySmsView.this.codeField.setSelection(LoginActivitySmsView.this.codeField.length());
                }
            }
        }

        public LoginActivitySmsView(LoginActivity this$0, Context context, int type) {
            final LoginActivity loginActivity = this$0;
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            this.currentType = type;
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            if (r0.currentType == 3) {
                FrameLayout frameLayout = new FrameLayout(context2);
                ImageView imageView = new ImageView(context2);
                imageView.setImageResource(R.drawable.phone_activate);
                if (LocaleController.isRTL) {
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    frameLayout.addView(r0.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 82.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    frameLayout.addView(r0.confirmTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 0.0f, 0.0f, 82.0f, 0.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 21, 0.0f, 2.0f, 0.0f, 2.0f));
                }
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            } else {
                addView(r0.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            }
            r0.codeField = new EditTextBoldCursor(context2);
            r0.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setHint(LocaleController.getString("Code", R.string.Code));
            r0.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.codeField.setCursorWidth(1.5f);
            r0.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.codeField.setImeOptions(268435461);
            r0.codeField.setTextSize(1, 18.0f);
            r0.codeField.setInputType(3);
            r0.codeField.setMaxLines(1);
            r0.codeField.setPadding(0, 0, 0, 0);
            addView(r0.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            r0.codeField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!(LoginActivitySmsView.this.ignoreOnTextChange || LoginActivitySmsView.this.length == 0 || LoginActivitySmsView.this.codeField.length() != LoginActivitySmsView.this.length)) {
                        LoginActivitySmsView.this.onNextPressed();
                    }
                }
            });
            r0.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivitySmsView.this.onNextPressed();
                    return true;
                }
            });
            if (r0.currentType == 3) {
                r0.codeField.setEnabled(false);
                r0.codeField.setInputType(0);
                r0.codeField.setVisibility(8);
            }
            r0.timeText = new TextView(context2);
            r0.timeText.setTextSize(1, 14.0f);
            r0.timeText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r0.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(r0.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 30, 0, 0));
            if (r0.currentType == 3) {
                r0.progressView = new ProgressView(context2);
                addView(r0.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            r0.problemText = new TextView(context2);
            r0.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            r0.problemText.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.problemText.setTextSize(1, 14.0f);
            r0.problemText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(r0.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            r0.problemText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!LoginActivitySmsView.this.nextPressed) {
                        if (LoginActivitySmsView.this.nextType == 0 || LoginActivitySmsView.this.nextType == 4) {
                            try {
                                PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                                Intent mailer = new Intent("android.intent.action.SEND");
                                mailer.setType("message/rfc822");
                                mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Android registration/login issue ");
                                stringBuilder.append(version);
                                stringBuilder.append(" ");
                                stringBuilder.append(LoginActivitySmsView.this.emailPhone);
                                mailer.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Phone: ");
                                stringBuilder.append(LoginActivitySmsView.this.requestPhone);
                                stringBuilder.append("\nApp version: ");
                                stringBuilder.append(version);
                                stringBuilder.append("\nOS version: SDK ");
                                stringBuilder.append(VERSION.SDK_INT);
                                stringBuilder.append("\nDevice Name: ");
                                stringBuilder.append(Build.MANUFACTURER);
                                stringBuilder.append(Build.MODEL);
                                stringBuilder.append("\nLocale: ");
                                stringBuilder.append(Locale.getDefault());
                                stringBuilder.append("\nError: ");
                                stringBuilder.append(LoginActivitySmsView.this.lastError);
                                mailer.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                                LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                            } catch (Exception e) {
                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                            }
                        } else {
                            LoginActivitySmsView.this.resendCode();
                        }
                    }
                }
            });
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(linearLayout, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            r0.wrongNumber = new TextView(context2);
            r0.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            r0.wrongNumber.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.wrongNumber.setTextSize(1, 14.0f);
            r0.wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            View view = r0.wrongNumber;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            linearLayout.addView(view, LayoutHelper.createLinear(-2, -2, 80 | i, 0, 0, 0, 10));
            r0.wrongNumber.setText(LocaleController.getString("WrongNumber", R.string.WrongNumber));
            r0.wrongNumber.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$4$1 */
                class C21831 implements RequestDelegate {
                    C21831() {
                    }

                    public void run(TLObject response, TL_error error) {
                    }
                }

                public void onClick(View view) {
                    TL_auth_cancelCode req = new TL_auth_cancelCode();
                    req.phone_number = LoginActivitySmsView.this.requestPhone;
                    req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new C21831(), 10);
                    LoginActivitySmsView.this.onBackPressed();
                    LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                }
            });
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            final Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            int reqId = ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LoginActivitySmsView.this.nextPressed = false;
                            if (error == null) {
                                LoginActivitySmsView.this.this$0.fillNextCodeParams(params, (TL_auth_sentCode) response);
                            } else if (error.text != null) {
                                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                    LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                } else {
                                    if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                        if (!error.text.contains("PHONE_CODE_INVALID")) {
                                            if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                LoginActivitySmsView.this.onBackPressed();
                                                LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                            } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                            } else if (error.code != C0539C.PRIORITY_DOWNLOAD) {
                                                LoginActivity loginActivity = LoginActivitySmsView.this.this$0;
                                                String string = LocaleController.getString("AppName", R.string.AppName);
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                stringBuilder.append("\n");
                                                stringBuilder.append(error.text);
                                                loginActivity.needShowAlert(string, stringBuilder.toString());
                                            }
                                        }
                                    }
                                    LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                }
                            }
                            LoginActivitySmsView.this.this$0.needHideProgress();
                        }
                    });
                }
            }, 10);
            this.this$0.needShowProgress(0);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        public void setParams(Bundle params, boolean restore) {
            if (params != null) {
                this.isRestored = restore;
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
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
                int i2 = 0;
                if (this.length != 0) {
                    this.codeField.setFilters(new InputFilter[]{new LengthFilter(this.length)});
                } else {
                    this.codeField.setFilters(new InputFilter[0]);
                }
                if (this.progressView != null) {
                    this.progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (this.currentType == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", R.string.SentAppCode));
                    } else if (this.currentType == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                    } else if (this.currentType == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                    } else if (this.currentType == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField);
                        this.codeField.requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField);
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
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(1), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(1), Integer.valueOf(0)));
                        }
                        String callLogNumber = this.isRestored ? AndroidUtilities.obtainLoginPhoneCall(this.pattern) : null;
                        if (callLogNumber != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(callLogNumber);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        } else if (this.catchedPhone != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(this.catchedPhone);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        } else {
                            createTimer();
                        }
                    } else if (this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setVisibility(0);
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        TextView textView = this.problemText;
                        if (this.time >= 1000) {
                            i2 = 8;
                        }
                        textView.setVisibility(i2);
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
                this.codeTimer.schedule(new C14966(), 0, 1000);
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
                this.timeTimer.schedule(new C14997(), 0, 1000);
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
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                final String code = this.codeField.getText().toString();
                final TL_auth_signIn req = new TL_auth_signIn();
                req.phone_number = this.requestPhone;
                req.phone_code = code;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                int reqId = ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$8$1$1 */
                            class C21861 implements RequestDelegate {
                                C21861() {
                                }

                                public void run(final TLObject response, final TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            LoginActivitySmsView.this.this$0.needHideProgress();
                                            if (error == null) {
                                                TL_account_password password = response;
                                                Bundle bundle = new Bundle();
                                                bundle.putString("current_salt", Utilities.bytesToHex(password.current_salt));
                                                bundle.putString(TrackReferenceTypeBox.TYPE1, password.hint);
                                                bundle.putString("email_unconfirmed_pattern", password.email_unconfirmed_pattern);
                                                bundle.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                                bundle.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                                                bundle.putString("code", req.phone_code);
                                                bundle.putInt("has_recovery", password.has_recovery);
                                                LoginActivitySmsView.this.this$0.setPage(6, true, bundle, false);
                                                return;
                                            }
                                            LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                        }
                                    });
                                }
                            }

                            public void run() {
                                LoginActivitySmsView.this.nextPressed = false;
                                boolean ok = false;
                                if (error == null) {
                                    ok = true;
                                    LoginActivitySmsView.this.this$0.needHideProgress();
                                    TL_auth_authorization res = response;
                                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).setUserId(res.user.id);
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).clearConfig();
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).cleanup();
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).syncContacts = LoginActivitySmsView.this.this$0.syncContacts;
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).setCurrentUser(res.user);
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).saveConfig(true);
                                    MessagesStorage.getInstance(LoginActivitySmsView.this.this$0.currentAccount).cleanup(true);
                                    ArrayList<User> users = new ArrayList();
                                    users.add(res.user);
                                    MessagesStorage.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUsersAndChats(users, null, true, true);
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUser(res.user, false);
                                    ContactsController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).checkAppAccount();
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).getBlockedUsers(true);
                                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).updateDcSettings();
                                    LoginActivitySmsView.this.this$0.needFinishActivity();
                                } else {
                                    LoginActivitySmsView.this.lastError = error.text;
                                    if (error.text.contains("PHONE_NUMBER_UNOCCUPIED")) {
                                        ok = true;
                                        LoginActivitySmsView.this.this$0.needHideProgress();
                                        Bundle params = new Bundle();
                                        params.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                        params.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                                        params.putString("code", req.phone_code);
                                        LoginActivitySmsView.this.this$0.setPage(5, true, params, false);
                                        LoginActivitySmsView.this.destroyTimer();
                                        LoginActivitySmsView.this.destroyCodeTimer();
                                    } else if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                                        ok = true;
                                        ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(new TL_account_getPassword(), new C21861(), 10);
                                        LoginActivitySmsView.this.destroyTimer();
                                        LoginActivitySmsView.this.destroyCodeTimer();
                                    } else {
                                        LoginActivitySmsView.this.this$0.needHideProgress();
                                        if ((LoginActivitySmsView.this.currentType == 3 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2)) || (LoginActivitySmsView.this.currentType == 2 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3))) {
                                            LoginActivitySmsView.this.createTimer();
                                        }
                                        if (LoginActivitySmsView.this.currentType == 2) {
                                            AndroidUtilities.setWaitingForSms(true);
                                            NotificationCenter.getGlobalInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                        } else if (LoginActivitySmsView.this.currentType == 3) {
                                            AndroidUtilities.setWaitingForCall(true);
                                            NotificationCenter.getGlobalInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                                        }
                                        LoginActivitySmsView.this.waitingForEvent = true;
                                        if (LoginActivitySmsView.this.currentType != 3) {
                                            if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                            } else {
                                                if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                                    if (!error.text.contains("PHONE_CODE_INVALID")) {
                                                        if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                            LoginActivitySmsView.this.onBackPressed();
                                                            LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                                                            LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                            LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                                        } else {
                                                            LoginActivity loginActivity = LoginActivitySmsView.this.this$0;
                                                            String string = LocaleController.getString("AppName", R.string.AppName);
                                                            StringBuilder stringBuilder = new StringBuilder();
                                                            stringBuilder.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                            stringBuilder.append("\n");
                                                            stringBuilder.append(error.text);
                                                            loginActivity.needShowAlert(string, stringBuilder.toString());
                                                        }
                                                    }
                                                }
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                            }
                                        }
                                    }
                                }
                                if (ok && LoginActivitySmsView.this.currentType == 3) {
                                    AndroidUtilities.endIncomingCall();
                                    AndroidUtilities.removeLoginPhoneCall(code, true);
                                }
                            }
                        });
                    }
                }, 10);
                this.this$0.needShowProgress(0);
            }
        }

        public void onBackPressed() {
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
                AndroidUtilities.runOnUIThread(new C15029(), 100);
            }
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.waitingForEvent) {
                if (this.codeField != null) {
                    if (id == NotificationCenter.didReceiveSmsCode) {
                        this.ignoreOnTextChange = true;
                        EditTextBoldCursor editTextBoldCursor = this.codeField;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                        stringBuilder.append(args[0]);
                        editTextBoldCursor.setText(stringBuilder.toString());
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    } else if (id == NotificationCenter.didReceiveCall) {
                        String num = new StringBuilder();
                        num.append(TtmlNode.ANONYMOUS_REGION_ID);
                        num.append(args[0]);
                        num = num.toString();
                        if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                            if (!this.pattern.equals("*")) {
                                this.catchedPhone = num;
                                AndroidUtilities.endIncomingCall();
                                AndroidUtilities.removeLoginPhoneCall(num, true);
                            }
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(num);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        }
                    }
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("smsview_code_");
                stringBuilder.append(this.currentType);
                bundle.putString(stringBuilder.toString(), code);
            }
            if (this.catchedPhone != null) {
                bundle.putString("catchedPhone", this.catchedPhone);
            }
            if (this.currentParams != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("smsview_params_");
                stringBuilder.append(this.currentType);
                bundle.putBundle(stringBuilder.toString(), this.currentParams);
            }
            if (this.time != 0) {
                bundle.putInt("time", this.time);
            }
            if (this.openTime != 0) {
                bundle.putInt("open", this.openTime);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("smsview_params_");
            stringBuilder.append(this.currentType);
            this.currentParams = bundle.getBundle(stringBuilder.toString());
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
            }
            String catched = bundle.getString("catchedPhone");
            if (catched != null) {
                this.catchedPhone = catched;
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("smsview_code_");
            stringBuilder2.append(this.currentType);
            String code = bundle.getString(stringBuilder2.toString());
            if (code != null) {
                this.codeField.setText(code);
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
        final /* synthetic */ LoginActivity this$0;
        private View view;

        public PhoneView(LoginActivity this$0, Context context) {
            final LoginActivity loginActivity = this$0;
            Context context2 = context;
            this.this$0 = loginActivity;
            super(context2);
            setOrientation(1);
            this.countryButton = new TextView(context2);
            this.countryButton.setTextSize(1, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), 0);
            this.countryButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.countryButton.setMaxLines(1);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            r1.countryButton.setBackgroundResource(R.drawable.spinner_states);
            addView(r1.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            r1.countryButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$PhoneView$1$1 */
                class C21881 implements CountrySelectActivityDelegate {

                    /* renamed from: org.telegram.ui.LoginActivity$PhoneView$1$1$1 */
                    class C15031 implements Runnable {
                        C15031() {
                        }

                        public void run() {
                            AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                        }
                    }

                    C21881() {
                    }

                    public void didSelectCountry(String name, String shortName) {
                        PhoneView.this.selectCountry(name);
                        AndroidUtilities.runOnUIThread(new C15031(), 300);
                        PhoneView.this.phoneField.requestFocus();
                        PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                    }
                }

                public void onClick(View view) {
                    CountrySelectActivity fragment = new CountrySelectActivity(true);
                    fragment.setCountrySelectActivityDelegate(new C21881());
                    PhoneView.this.this$0.presentFragment(fragment);
                }
            });
            r1.view = new View(context2);
            r1.view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            r1.view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
            addView(r1.view, LayoutHelper.createLinear(-1, 1, 4.0f, -17.5f, 4.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            r1.textView = new TextView(context2);
            r1.textView.setText("+");
            r1.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r1.textView.setTextSize(1, 18.0f);
            linearLayout.addView(r1.textView, LayoutHelper.createLinear(-2, -2));
            r1.codeField = new EditTextBoldCursor(context2);
            r1.codeField.setInputType(3);
            r1.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r1.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r1.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r1.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r1.codeField.setCursorWidth(1.5f);
            r1.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            r1.codeField.setTextSize(1, 18.0f);
            r1.codeField.setMaxLines(1);
            r1.codeField.setGravity(19);
            r1.codeField.setImeOptions(268435461);
            r1.codeField.setFilters(new InputFilter[]{new LengthFilter(5)});
            linearLayout.addView(r1.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            r1.codeField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void afterTextChanged(Editable editable) {
                    if (!PhoneView.this.ignoreOnTextChange) {
                        PhoneView.this.ignoreOnTextChange = true;
                        String text = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                        PhoneView.this.codeField.setText(text);
                        String str = null;
                        if (text.length() == 0) {
                            PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 1;
                        } else {
                            boolean ok = false;
                            String textToSet = null;
                            int a = 4;
                            if (text.length() > 4) {
                                while (true) {
                                    int a2 = a;
                                    if (a2 < 1) {
                                        break;
                                    }
                                    String sub = text.substring(0, a2);
                                    if (((String) PhoneView.this.codesMap.get(sub)) != null) {
                                        break;
                                    }
                                    a = a2 - 1;
                                }
                                if (!ok) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(text.substring(1, text.length()));
                                    stringBuilder.append(PhoneView.this.phoneField.getText().toString());
                                    textToSet = stringBuilder.toString();
                                    EditTextBoldCursor access$1000 = PhoneView.this.codeField;
                                    CharSequence substring = text.substring(0, 1);
                                    text = substring;
                                    access$1000.setText(substring);
                                }
                            }
                            String country = (String) PhoneView.this.codesMap.get(text);
                            if (country != null) {
                                int index = PhoneView.this.countriesArray.indexOf(country);
                                if (index != -1) {
                                    PhoneView.this.ignoreSelection = true;
                                    PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(index));
                                    String hint = (String) PhoneView.this.phoneFormatMap.get(text);
                                    HintEditText access$800 = PhoneView.this.phoneField;
                                    if (hint != null) {
                                        str = hint.replace('X', '\u2013');
                                    }
                                    access$800.setHintText(str);
                                    PhoneView.this.countryState = 0;
                                } else {
                                    PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                                    PhoneView.this.phoneField.setHintText(null);
                                    PhoneView.this.countryState = 2;
                                }
                            } else {
                                PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
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
            r1.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    PhoneView.this.phoneField.requestFocus();
                    PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                    return true;
                }
            });
            r1.phoneField = new HintEditText(context2);
            r1.phoneField.setInputType(3);
            r1.phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r1.phoneField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r1.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r1.phoneField.setPadding(0, 0, 0, 0);
            r1.phoneField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r1.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
            r1.phoneField.setCursorWidth(1.5f);
            r1.phoneField.setTextSize(1, 18.0f);
            r1.phoneField.setMaxLines(1);
            r1.phoneField.setGravity(19);
            r1.phoneField.setImeOptions(268435461);
            linearLayout.addView(r1.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            r1.phoneField.addTextChangedListener(new TextWatcher() {
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
                        StringBuilder stringBuilder;
                        int start = PhoneView.this.phoneField.getSelectionStart();
                        String phoneChars = "0123456789";
                        String str = PhoneView.this.phoneField.getText().toString();
                        if (this.characterAction == 3) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str.substring(0, this.actionPosition));
                            stringBuilder.append(str.substring(this.actionPosition + 1, str.length()));
                            str = stringBuilder.toString();
                            start--;
                        }
                        stringBuilder = new StringBuilder(str.length());
                        for (int a = 0; a < str.length(); a++) {
                            String ch = str.substring(a, a + 1);
                            if (phoneChars.contains(ch)) {
                                stringBuilder.append(ch);
                            }
                        }
                        PhoneView.this.ignoreOnPhoneChange = true;
                        String hint = PhoneView.this.phoneField.getHintText();
                        if (hint != null) {
                            int start2 = start;
                            start = 0;
                            while (start < stringBuilder.length()) {
                                if (start < hint.length()) {
                                    if (hint.charAt(start) == ' ') {
                                        stringBuilder.insert(start, ' ');
                                        start++;
                                        if (!(start2 != start || this.characterAction == 2 || this.characterAction == 3)) {
                                            start2++;
                                        }
                                    }
                                    start++;
                                } else {
                                    stringBuilder.insert(start, ' ');
                                    if (!(start2 != start + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                        start = start2 + 1;
                                    }
                                    start = start2;
                                }
                            }
                            start = start2;
                        }
                        PhoneView.this.phoneField.setText(stringBuilder);
                        if (start >= 0) {
                            PhoneView.this.phoneField.setSelection(start <= PhoneView.this.phoneField.length() ? start : PhoneView.this.phoneField.length());
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            r1.phoneField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    PhoneView.this.onNextPressed();
                    return true;
                }
            });
            r1.textView2 = new TextView(context2);
            r1.textView2.setText(LocaleController.getString("StartText", R.string.StartText));
            r1.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r1.textView2.setTextSize(1, 14.0f);
            r1.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            r1.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r1.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            if (this$0.newAccount) {
                r1.checkBoxCell = new CheckBoxCell(context2, 2);
                r1.checkBoxCell.setText(LocaleController.getString("SyncContacts", R.string.SyncContacts), TtmlNode.ANONYMOUS_REGION_ID, this$0.syncContacts, false);
                addView(r1.checkBoxCell, LayoutHelper.createLinear(-2, -1, 51, 0, 0, 0, 0));
                r1.checkBoxCell.setOnClickListener(new OnClickListener() {
                    private Toast visibleToast;

                    public void onClick(View v) {
                        if (PhoneView.this.this$0.getParentActivity() != null) {
                            CheckBoxCell cell = (CheckBoxCell) v;
                            PhoneView.this.this$0.syncContacts = PhoneView.this.this$0.syncContacts ^ true;
                            cell.setChecked(PhoneView.this.this$0.syncContacts, true);
                            try {
                                if (this.visibleToast != null) {
                                    this.visibleToast.cancel();
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            if (PhoneView.this.this$0.syncContacts) {
                                this.visibleToast = Toast.makeText(PhoneView.this.this$0.getParentActivity(), LocaleController.getString("SyncContactsOn", R.string.SyncContactsOn), 0);
                                this.visibleToast.show();
                            } else {
                                this.visibleToast = Toast.makeText(PhoneView.this.this$0.getParentActivity(), LocaleController.getString("SyncContactsOff", R.string.SyncContactsOff), 0);
                                this.visibleToast.show();
                            }
                        }
                    }
                });
            }
            HashMap<String, String> languageMap = new HashMap();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
                while (true) {
                    String readLine = reader.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    r1.countriesArray.add(0, args[2]);
                    r1.countriesMap.put(args[2], args[0]);
                    r1.codesMap.put(args[0], args[2]);
                    if (args.length > 3) {
                        r1.phoneFormatMap.put(args[0], args[3]);
                    }
                    languageMap.put(args[1], args[2]);
                }
                reader.close();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            Collections.sort(r1.countriesArray, new Comparator<String>() {
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
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
                String countryName = (String) languageMap.get(country);
                if (!(countryName == null || r1.countriesArray.indexOf(countryName) == -1)) {
                    r1.codeField.setText((CharSequence) r1.countriesMap.get(countryName));
                    r1.countryState = 0;
                }
            }
            if (r1.codeField.length() == 0) {
                r1.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                r1.phoneField.setHintText(null);
                r1.countryState = 1;
            }
            if (r1.codeField.length() != 0) {
                r1.phoneField.requestFocus();
                r1.phoneField.setSelection(r1.phoneField.length());
                return;
            }
            r1.codeField.requestFocus();
        }

        public void selectCountry(String name) {
            if (this.countriesArray.indexOf(name) != -1) {
                this.ignoreOnTextChange = true;
                String code = (String) this.countriesMap.get(name);
                this.codeField.setText(code);
                this.countryButton.setText(name);
                String hint = (String) this.phoneFormatMap.get(code);
                this.phoneField.setHintText(hint != null ? hint.replace('X', '\u2013') : null);
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
            if (this.this$0.getParentActivity() != null) {
                if (!r1.nextPressed) {
                    TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
                    boolean allowCall = true;
                    if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                        allowCall = r1.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                        boolean allowSms = r1.this$0.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                        boolean allowCancelCall = r1.this$0.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                        if (r1.this$0.checkPermissions) {
                            r1.this$0.permissionsItems.clear();
                            if (!allowCall) {
                                r1.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!allowSms) {
                                r1.this$0.permissionsItems.add("android.permission.RECEIVE_SMS");
                                if (VERSION.SDK_INT >= 23) {
                                    r1.this$0.permissionsItems.add("android.permission.READ_SMS");
                                }
                            }
                            if (!allowCancelCall) {
                                r1.this$0.permissionsItems.add("android.permission.CALL_PHONE");
                                r1.this$0.permissionsItems.add("android.permission.WRITE_CALL_LOG");
                                r1.this$0.permissionsItems.add("android.permission.READ_CALL_LOG");
                            }
                            boolean ok = true;
                            if (!r1.this$0.permissionsItems.isEmpty()) {
                                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                if (allowCancelCall || !allowCall) {
                                    if (!(preferences.getBoolean("firstlogin", true) || r1.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE"))) {
                                        if (!r1.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                            try {
                                                r1.this$0.getParentActivity().requestPermissions((String[]) r1.this$0.permissionsItems.toArray(new String[r1.this$0.permissionsItems.size()]), 6);
                                            } catch (Exception e) {
                                                Exception ignore = e;
                                                ok = false;
                                            }
                                        }
                                    }
                                    preferences.edit().putBoolean("firstlogin", false).commit();
                                    Builder builder = new Builder(r1.this$0.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                    if (r1.this$0.permissionsItems.size() >= 2) {
                                        builder.setMessage(LocaleController.getString("AllowReadCallAndSms", R.string.AllowReadCallAndSms));
                                    } else if (allowSms) {
                                        builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                    } else {
                                        builder.setMessage(LocaleController.getString("AllowReadSms", R.string.AllowReadSms));
                                    }
                                    r1.this$0.permissionsDialog = r1.this$0.showDialog(builder.create());
                                } else {
                                    r1.this$0.getParentActivity().requestPermissions((String[]) r1.this$0.permissionsItems.toArray(new String[r1.this$0.permissionsItems.size()]), 6);
                                }
                                if (ok) {
                                    return;
                                }
                            }
                        }
                    }
                    if (r1.countryState == 1) {
                        r1.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                    } else if (r1.countryState == 2 && !BuildVars.DEBUG_VERSION && !r1.codeField.getText().toString().equals("999")) {
                        r1.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("WrongCountry", R.string.WrongCountry));
                    } else if (r1.codeField.length() == 0) {
                        r1.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                        stringBuilder.append(r1.codeField.getText());
                        stringBuilder.append(r1.phoneField.getText());
                        String phone = PhoneFormat.stripExceptNumbers(stringBuilder.toString());
                        for (int a = 0; a < 3; a++) {
                            UserConfig userConfig = UserConfig.getInstance(a);
                            if (userConfig.isClientActivated()) {
                                String userPhone = userConfig.getCurrentUser().phone;
                                if (!userPhone.contains(phone)) {
                                    if (phone.contains(userPhone)) {
                                    }
                                }
                                final int num = a;
                                Builder builder2 = new Builder(r1.this$0.getParentActivity());
                                builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder2.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", R.string.AccountAlreadyLoggedIn));
                                builder2.setPositiveButton(LocaleController.getString("AccountSwitch", R.string.AccountSwitch), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (UserConfig.selectedAccount != num) {
                                            ((LaunchActivity) PhoneView.this.this$0.getParentActivity()).switchToAccount(num, false);
                                        }
                                        PhoneView.this.this$0.finishFragment();
                                    }
                                });
                                builder2.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                                r1.this$0.showDialog(builder2.create());
                                return;
                            }
                        }
                        ConnectionsManager.getInstance(r1.this$0.currentAccount).cleanup();
                        final TL_auth_sendCode req = new TL_auth_sendCode();
                        req.api_hash = BuildVars.APP_HASH;
                        req.api_id = BuildVars.APP_ID;
                        req.phone_number = phone;
                        boolean z = simcardAvailable && allowCall;
                        req.allow_flashcall = z;
                        if (req.allow_flashcall) {
                            try {
                                String number = tm.getLine1Number();
                                if (!TextUtils.isEmpty(number)) {
                                    boolean z2;
                                    if (!phone.contains(number)) {
                                        if (!number.contains(phone)) {
                                            z2 = false;
                                            req.current_number = z2;
                                            if (!req.current_number) {
                                                req.allow_flashcall = false;
                                            }
                                        }
                                    }
                                    z2 = true;
                                    req.current_number = z2;
                                    if (req.current_number) {
                                        req.allow_flashcall = false;
                                    }
                                } else if (UserConfig.getActivatedAccountsCount() > 0) {
                                    req.allow_flashcall = false;
                                } else {
                                    req.current_number = false;
                                }
                            } catch (Throwable e2) {
                                Throwable e3 = e2;
                                req.allow_flashcall = false;
                                FileLog.m3e(e3);
                            }
                        }
                        final Bundle params = new Bundle();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("+");
                        stringBuilder2.append(r1.codeField.getText());
                        stringBuilder2.append(r1.phoneField.getText());
                        params.putString("phone", stringBuilder2.toString());
                        try {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("+");
                            stringBuilder2.append(PhoneFormat.stripExceptNumbers(r1.codeField.getText().toString()));
                            stringBuilder2.append(" ");
                            stringBuilder2.append(PhoneFormat.stripExceptNumbers(r1.phoneField.getText().toString()));
                            params.putString("ephone", stringBuilder2.toString());
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("+");
                            stringBuilder3.append(phone);
                            params.putString("ephone", stringBuilder3.toString());
                        }
                        params.putString("phoneFormated", phone);
                        r1.nextPressed = true;
                        r1.this$0.needShowProgress(ConnectionsManager.getInstance(r1.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(final TLObject response, final TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        PhoneView.this.nextPressed = false;
                                        if (error == null) {
                                            PhoneView.this.this$0.fillNextCodeParams(params, (TL_auth_sentCode) response);
                                        } else if (error.text != null) {
                                            if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                                PhoneView.this.this$0.needShowInvalidAlert(req.phone_number, false);
                                            } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                                                PhoneView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                            } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                                                PhoneView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PhoneNumberFlood", R.string.PhoneNumberFlood));
                                            } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                                                PhoneView.this.this$0.needShowInvalidAlert(req.phone_number, true);
                                            } else {
                                                if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                                    if (!error.text.contains("PHONE_CODE_INVALID")) {
                                                        if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                            PhoneView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                            PhoneView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                                        } else if (error.code != C0539C.PRIORITY_DOWNLOAD) {
                                                            PhoneView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                                        }
                                                    }
                                                }
                                                PhoneView.this.this$0.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                            }
                                        }
                                        PhoneView.this.this$0.needHideProgress();
                                    }
                                });
                            }
                        }, 27));
                    }
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void fillNumber() {
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (!(tm.getSimState() == 1 || tm.getPhoneType() == 0)) {
                    boolean allowCall = true;
                    boolean allowSms = true;
                    if (VERSION.SDK_INT >= 23) {
                        allowCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                        allowSms = this.this$0.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                        if (!(!this.this$0.checkShowPermissions || allowCall || allowSms)) {
                            this.this$0.permissionsShowItems.clear();
                            if (!allowCall) {
                                this.this$0.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!allowSms) {
                                this.this$0.permissionsShowItems.add("android.permission.RECEIVE_SMS");
                                if (VERSION.SDK_INT >= 23) {
                                    this.this$0.permissionsShowItems.add("android.permission.READ_SMS");
                                }
                            }
                            if (!this.this$0.permissionsShowItems.isEmpty()) {
                                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                if (!(preferences.getBoolean("firstloginshow", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE"))) {
                                    if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                        this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsShowItems.toArray(new String[this.this$0.permissionsShowItems.size()]), 7);
                                    }
                                }
                                preferences.edit().putBoolean("firstloginshow", false).commit();
                                Builder builder = new Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                builder.setMessage(LocaleController.getString("AllowFillNumber", R.string.AllowFillNumber));
                                this.this$0.permissionsShowDialog = this.this$0.showDialog(builder.create());
                            }
                            return;
                        }
                    }
                    if (!this.this$0.newAccount && (allowCall || allowSms)) {
                        String number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                        String textToSet = null;
                        boolean ok = false;
                        if (!TextUtils.isEmpty(number)) {
                            int a = 4;
                            if (number.length() > 4) {
                                while (true) {
                                    int a2 = a;
                                    if (a2 < 1) {
                                        break;
                                    }
                                    String sub = number.substring(0, a2);
                                    if (((String) this.codesMap.get(sub)) != null) {
                                        break;
                                    }
                                    a = a2 - 1;
                                }
                                if (!ok) {
                                    textToSet = number.substring(1, number.length());
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void onShow() {
            super.onShow();
            fillNumber();
            if (this.checkBoxCell != null) {
                this.checkBoxCell.setChecked(this.this$0.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (PhoneView.this.phoneField == null) {
                        return;
                    }
                    if (PhoneView.this.codeField.length() != 0) {
                        AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                        PhoneView.this.phoneField.requestFocus();
                        PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                        return;
                    }
                    AndroidUtilities.showKeyboard(PhoneView.this.codeField);
                    PhoneView.this.codeField.requestFocus();
                }
            }, 100);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourPhone", R.string.YourPhone);
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
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            this.progressDialog = null;
        }
    }

    public View createView(Context context) {
        int time;
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setActionBarMenuOnItemClick(new C21761());
        ActionBarMenu menu = this.actionBar.createMenu();
        this.actionBar.setAllowOverlayTitle(true);
        this.doneButton = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context2);
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(context2);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(this, context2);
        this.views[1] = new LoginActivitySmsView(this, context2, 1);
        this.views[2] = new LoginActivitySmsView(this, context2, 2);
        this.views[3] = new LoginActivitySmsView(this, context2, 3);
        this.views[4] = new LoginActivitySmsView(this, context2, 4);
        this.views[5] = new LoginActivityRegisterView(this, context2);
        this.views[6] = new LoginActivityPasswordView(this, context2);
        this.views[7] = new LoginActivityRecoverView(this, context2);
        this.views[8] = new LoginActivityResetWaitView(this, context2);
        int a = 0;
        while (a < r0.views.length) {
            r0.views[a].setVisibility(a == 0 ? 0 : 8);
            View view = r0.views[a];
            float f = a == 0 ? -2.0f : -1.0f;
            float f2 = 26.0f;
            float f3 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (!AndroidUtilities.isTablet()) {
                f2 = 18.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-1, f, 51, f3, 30.0f, f2, 0.0f));
            a++;
        }
        Bundle savedInstanceState = loadCurrentState();
        if (savedInstanceState != null) {
            r0.currentViewNum = savedInstanceState.getInt("currentViewNum", 0);
            r0.syncContacts = savedInstanceState.getInt("syncContacts", 1) == 1;
            if (r0.currentViewNum >= 1 && r0.currentViewNum <= 4) {
                time = savedInstanceState.getInt("open");
                if (time != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                    r0.currentViewNum = 0;
                    savedInstanceState = null;
                    clearCurrentState();
                }
            }
        }
        r0.actionBar.setTitle(r0.views[r0.currentViewNum].getHeaderName());
        time = 0;
        while (time < r0.views.length) {
            if (savedInstanceState != null) {
                if (time >= 1) {
                    if (time <= 4) {
                        if (time == r0.currentViewNum) {
                            r0.views[time].restoreStateParams(savedInstanceState);
                        }
                    }
                }
                r0.views[time].restoreStateParams(savedInstanceState);
            }
            if (r0.currentViewNum == time) {
                int i;
                ActionBar actionBar = r0.actionBar;
                if (!r0.views[time].needBackButton()) {
                    if (!r0.newAccount) {
                        i = 0;
                        actionBar.setBackButtonImage(i);
                        r0.views[time].setVisibility(0);
                        r0.views[time].onShow();
                        if (time != 3 || time == 8) {
                            r0.doneButton.setVisibility(8);
                        }
                    }
                }
                i = R.drawable.ic_ab_back;
                actionBar.setBackButtonImage(i);
                r0.views[time].setVisibility(0);
                r0.views[time].onShow();
                if (time != 3) {
                }
                r0.doneButton.setVisibility(8);
            } else {
                r0.views[time].setVisibility(8);
            }
            time++;
        }
        return r0.fragmentView;
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
                    this.views[this.currentViewNum].onBackPressed();
                    setPage(0, false, null, true);
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
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
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            StringBuilder stringBuilder;
            if (obj instanceof String) {
                if (prefix != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(prefix);
                    stringBuilder.append("_|_");
                    stringBuilder.append(key);
                    editor.putString(stringBuilder.toString(), (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(prefix);
                    stringBuilder.append("_|_");
                    stringBuilder.append(key);
                    editor.putInt(stringBuilder.toString(), ((Integer) obj).intValue());
                } else {
                    editor.putInt(key, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT < 23) {
            return;
        }
        if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
            } catch (Exception e) {
            }
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            try {
                getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[this.permissionsShowItems.size()]), 7);
            } catch (Exception e2) {
            }
        }
    }

    public boolean onBackPressed() {
        int a = 0;
        if (this.currentViewNum == 0) {
            while (true) {
                int a2 = a;
                if (a2 >= this.views.length) {
                    break;
                }
                if (this.views[a2] != null) {
                    this.views[a2].onDestroyActivity();
                }
                a = a2 + 1;
            }
            clearCurrentState();
            if (this.newAccount) {
                finishFragment();
            }
            return true;
        }
        if (this.currentViewNum == 6) {
            this.views[this.currentViewNum].onBackPressed();
            setPage(0, true, null, true);
        } else {
            if (this.currentViewNum != 7) {
                if (this.currentViewNum != 8) {
                    if (this.newAccount) {
                        if (this.currentAccount >= 1 && this.currentAccount <= 4) {
                            ((LoginActivitySmsView) this.views[this.currentAccount]).wrongNumber.callOnClick();
                        } else if (this.currentAccount == 5) {
                            ((LoginActivityRegisterView) this.views[this.currentAccount]).wrongNumber.callOnClick();
                        }
                    }
                }
            }
            this.views[this.currentViewNum].onBackPressed();
            setPage(6, true, null, true);
        }
        return false;
    }

    private void needShowAlert(String title, String text) {
        if (text != null) {
            if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(title);
                builder.setMessage(text);
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
            }
        }
    }

    private void needShowInvalidAlert(final String phoneNumber, final boolean banned) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (banned) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", R.string.BannedPhoneNumber));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", R.string.BotHelp), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                        Intent mailer = new Intent("android.intent.action.SEND");
                        mailer.setType("message/rfc822");
                        mailer.putExtra("android.intent.extra.EMAIL", new String[]{"login@stel.com"});
                        StringBuilder stringBuilder;
                        if (banned) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Banned phone number: ");
                            stringBuilder.append(phoneNumber);
                            mailer.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("I'm trying to use my mobile phone number: ");
                            stringBuilder.append(phoneNumber);
                            stringBuilder.append("\nBut Telegram says it's banned. Please help.\n\nApp version: ");
                            stringBuilder.append(version);
                            stringBuilder.append("\nOS version: SDK ");
                            stringBuilder.append(VERSION.SDK_INT);
                            stringBuilder.append("\nDevice Name: ");
                            stringBuilder.append(Build.MANUFACTURER);
                            stringBuilder.append(Build.MODEL);
                            stringBuilder.append("\nLocale: ");
                            stringBuilder.append(Locale.getDefault());
                            mailer.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Invalid phone number: ");
                            stringBuilder.append(phoneNumber);
                            mailer.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("I'm trying to use my mobile phone number: ");
                            stringBuilder.append(phoneNumber);
                            stringBuilder.append("\nBut Telegram says it's invalid. Please help.\n\nApp version: ");
                            stringBuilder.append(version);
                            stringBuilder.append("\nOS version: SDK ");
                            stringBuilder.append(VERSION.SDK_INT);
                            stringBuilder.append("\nDevice Name: ");
                            stringBuilder.append(Build.MANUFACTURER);
                            stringBuilder.append(Build.MODEL);
                            stringBuilder.append("\nLocale: ");
                            stringBuilder.append(Locale.getDefault());
                            mailer.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        }
                        LoginActivity.this.getParentActivity().startActivity(Intent.createChooser(mailer, "Send email..."));
                    } catch (Exception e) {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                    }
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void needShowProgress(final int reqiestId) {
        if (!(getParentActivity() == null || getParentActivity().isFinishing())) {
            if (this.progressDialog == null) {
                Builder builder = new Builder(getParentActivity(), 1);
                builder.setMessage(LocaleController.getString("Loading", R.string.Loading));
                if (reqiestId != 0) {
                    builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.this.views[LoginActivity.this.currentViewNum].onCancelPressed();
                            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).cancelRequest(reqiestId, true);
                            LoginActivity.this.progressDialog = null;
                        }
                    });
                }
                this.progressDialog = builder.show();
                this.progressDialog.setCanceledOnTouchOutside(false);
                this.progressDialog.setCancelable(false);
            }
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

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        int i;
        if (page != 3) {
            if (page != 8) {
                if (page == 0) {
                    this.checkPermissions = true;
                    this.checkShowPermissions = true;
                }
                this.doneButton.setVisibility(0);
                i = R.drawable.ic_ab_back;
                if (animated) {
                    ActionBar actionBar = this.actionBar;
                    if (!this.views[page].needBackButton()) {
                        if (this.newAccount) {
                            i = 0;
                        }
                    }
                    actionBar.setBackButtonImage(i);
                    this.views[this.currentViewNum].setVisibility(8);
                    this.currentViewNum = page;
                    this.views[page].setParams(params, false);
                    this.views[page].setVisibility(0);
                    this.actionBar.setTitle(this.views[page].getHeaderName());
                    this.views[page].onShow();
                    return;
                }
                final SlideView newView;
                final SlideView outView = this.views[this.currentViewNum];
                newView = this.views[page];
                this.currentViewNum = page;
                ActionBar actionBar2 = this.actionBar;
                if (!newView.needBackButton()) {
                    if (this.newAccount) {
                        i = 0;
                    }
                }
                actionBar2.setBackButtonImage(i);
                newView.setParams(params, false);
                this.actionBar.setTitle(newView.getHeaderName());
                newView.onShow();
                newView.setX((float) (back ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
                outView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListener() {
                    public void onAnimationStart(Animator animator) {
                    }

                    @SuppressLint({"NewApi"})
                    public void onAnimationEnd(Animator animator) {
                        outView.setVisibility(8);
                        outView.setX(0.0f);
                    }

                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }
                }).setDuration(300).translationX((float) (back ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x)).start();
                newView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListener() {
                    public void onAnimationStart(Animator animator) {
                        newView.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animator) {
                    }

                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }
                }).setDuration(300).translationX(0.0f).start();
            }
        }
        this.doneButton.setVisibility(8);
        i = R.drawable.ic_ab_back;
        if (animated) {
            ActionBar actionBar3 = this.actionBar;
            if (this.views[page].needBackButton()) {
                if (this.newAccount) {
                    i = 0;
                }
            }
            actionBar3.setBackButtonImage(i);
            this.views[this.currentViewNum].setVisibility(8);
            this.currentViewNum = page;
            this.views[page].setParams(params, false);
            this.views[page].setVisibility(0);
            this.actionBar.setTitle(this.views[page].getHeaderName());
            this.views[page].onShow();
            return;
        }
        final SlideView outView2 = this.views[this.currentViewNum];
        newView = this.views[page];
        this.currentViewNum = page;
        ActionBar actionBar22 = this.actionBar;
        if (newView.needBackButton()) {
            if (this.newAccount) {
                i = 0;
            }
        }
        actionBar22.setBackButtonImage(i);
        newView.setParams(params, false);
        this.actionBar.setTitle(newView.getHeaderName());
        newView.onShow();
        if (back) {
        }
        newView.setX((float) (back ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
        if (back) {
        }
        outView2.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(/* anonymous class already generated */).setDuration(300).translationX((float) (back ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x)).start();
        newView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(/* anonymous class already generated */).setDuration(300).translationX(0.0f).start();
    }

    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            bundle.putInt("syncContacts", this.syncContacts);
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private void needFinishActivity() {
        clearCurrentState();
        if (!this.newAccount) {
            presentFragment(new DialogsActivity(null), true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() != null) {
            this.newAccount = false;
            ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false);
            finishFragment();
        }
    }

    private void fillNextCodeParams(Bundle params, TL_auth_sentCode res) {
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
        PhoneView phoneView = this.views[0];
        LoginActivitySmsView smsView1 = this.views[1];
        LoginActivitySmsView smsView2 = this.views[2];
        LoginActivitySmsView smsView3 = this.views[3];
        LoginActivitySmsView smsView4 = this.views[4];
        LoginActivityRegisterView registerView = this.views[5];
        LoginActivityPasswordView passwordView = this.views[6];
        LoginActivityRecoverView recoverView = this.views[7];
        LoginActivityResetWaitView waitView = this.views[8];
        r12 = new ThemeDescription[86];
        r12[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r12[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r12[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r12[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r12[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r12[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r12[6] = new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[7] = new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhiteGrayLine);
        r12[8] = new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[9] = new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[10] = new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[11] = new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[12] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[13] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[14] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[15] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[16] = new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[17] = new ThemeDescription(passwordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[18] = new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[19] = new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[20] = new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[21] = new ThemeDescription(passwordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[22] = new ThemeDescription(passwordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[23] = new ThemeDescription(passwordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteRedText6);
        r12[24] = new ThemeDescription(passwordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[25] = new ThemeDescription(registerView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[26] = new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[27] = new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[28] = new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[29] = new ThemeDescription(registerView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[30] = new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[31] = new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[32] = new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[33] = new ThemeDescription(registerView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[34] = new ThemeDescription(registerView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[35] = new ThemeDescription(recoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[36] = new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[37] = new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[38] = new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[39] = new ThemeDescription(recoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[40] = new ThemeDescription(recoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[41] = new ThemeDescription(waitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[42] = new ThemeDescription(waitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[43] = new ThemeDescription(waitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[44] = new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[45] = new ThemeDescription(waitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText6);
        r12[46] = new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[47] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[48] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[49] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[50] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[51] = new ThemeDescription(smsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[52] = new ThemeDescription(smsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[53] = new ThemeDescription(smsView1.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[54] = new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[55] = new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r12[56] = new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[57] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[58] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[59] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[60] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[61] = new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[62] = new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[63] = new ThemeDescription(smsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[64] = new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[65] = new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r12[66] = new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[67] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[68] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[69] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[70] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[71] = new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[72] = new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[73] = new ThemeDescription(smsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[74] = new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[75] = new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r12[76] = new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[77] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[78] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[79] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[80] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[81] = new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[82] = new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[83] = new ThemeDescription(smsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[84] = new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[85] = new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        return r12;
    }
}
