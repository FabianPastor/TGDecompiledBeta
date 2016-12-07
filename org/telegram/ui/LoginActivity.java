package org.telegram.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v4.app.NotificationManagerCompat;
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
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.volley.DefaultRetryPolicy;
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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = true;
    private boolean checkShowPermissions = true;
    private int currentViewNum = 0;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList();
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems = new ArrayList();
    private ProgressDialog progressDialog;
    private SlideView[] views = new SlideView[9];

    public class LoginActivityPasswordView extends SlideView {
        private EditText codeField;
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

        public LoginActivityPasswordView(Context context) {
            super(context);
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.confirmTextView.setText(LocaleController.getString("LoginPasswordText", R.string.LoginPasswordText));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            this.codeField = new EditText(context);
            this.codeField.setTextColor(-14606047);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            this.codeField.setOnEditorActionListener(new OnEditorActionListener(LoginActivity.this) {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivityPasswordView.this.onNextPressed();
                    return true;
                }
            });
            TextView cancelButton = new TextView(context);
            cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            cancelButton.setTextColor(-11697229);
            cancelButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
            cancelButton.setTextSize(1, 14.0f);
            cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(cancelButton, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48));
            cancelButton.setOnClickListener(new OnClickListener(LoginActivity.this) {
                public void onClick(View view) {
                    if (LoginActivityPasswordView.this.has_recovery) {
                        LoginActivity.this.needShowProgress();
                        ConnectionsManager.getInstance().sendRequest(new TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                            public void run(final TLObject response, final TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        LoginActivity.this.needHideProgress();
                                        if (error == null) {
                                            final TL_auth_passwordRecovery res = response;
                                            Builder builder = new Builder(LoginActivity.this.getParentActivity());
                                            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("email_unconfirmed_pattern", res.email_pattern);
                                                    LoginActivity.this.setPage(7, true, bundle, false);
                                                }
                                            });
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
                                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                        } else {
                                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                        }
                                    }
                                });
                            }
                        }, 10);
                        return;
                    }
                    LoginActivityPasswordView.this.resetAccountText.setVisibility(0);
                    LoginActivityPasswordView.this.resetAccountButton.setVisibility(0);
                    AndroidUtilities.hideKeyboard(LoginActivityPasswordView.this.codeField);
                    LoginActivity.this.needShowAlert(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                }
            });
            this.resetAccountButton = new TextView(context);
            this.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountButton.setTextColor(-39322);
            this.resetAccountButton.setVisibility(8);
            this.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", R.string.ResetMyAccount));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(this.resetAccountButton, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 34, 0, 0));
            this.resetAccountButton.setOnClickListener(new OnClickListener(LoginActivity.this) {
                public void onClick(View view) {
                    Builder builder = new Builder(LoginActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                    builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                    builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginActivity.this.needShowProgress();
                            TL_account_deleteAccount req = new TL_account_deleteAccount();
                            req.reason = "Forgot password";
                            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, final TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            LoginActivity.this.needHideProgress();
                                            Bundle params;
                                            if (error == null) {
                                                params = new Bundle();
                                                params.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                                params.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                                params.putString("code", LoginActivityPasswordView.this.phoneCode);
                                                LoginActivity.this.setPage(5, true, params, false);
                                            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                                                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
                                            } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                                                params = new Bundle();
                                                params.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                                params.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                                params.putString("code", LoginActivityPasswordView.this.phoneCode);
                                                params.putInt("startTime", ConnectionsManager.getInstance().getCurrentTime());
                                                params.putInt("waitTime", Utilities.parseInt(error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                                                LoginActivity.this.setPage(8, true, params, false);
                                            } else {
                                                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                            }
                                        }
                                    });
                                }
                            }, 10);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    LoginActivity.this.showDialog(builder.create());
                }
            });
            this.resetAccountText = new TextView(context);
            this.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.resetAccountText.setVisibility(8);
            this.resetAccountText.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", R.string.ResetMyAccountText));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(this.resetAccountText, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 0, 7, 0, 14));
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        public void setParams(Bundle params) {
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
                this.current_salt = Utilities.hexToBytes(this.currentParams.getString("current_salt"));
                this.hint = this.currentParams.getString(TrackReferenceTypeBox.TYPE1);
                if (this.currentParams.getInt("has_recovery") != 1) {
                    z = false;
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
                byte[] oldPasswordBytes = null;
                try {
                    oldPasswordBytes = oldPassword.getBytes("UTF-8");
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                LoginActivity.this.needShowProgress();
                byte[] hash = new byte[((this.current_salt.length * 2) + oldPasswordBytes.length)];
                System.arraycopy(this.current_salt, 0, hash, 0, this.current_salt.length);
                System.arraycopy(oldPasswordBytes, 0, hash, this.current_salt.length, oldPasswordBytes.length);
                System.arraycopy(this.current_salt, 0, hash, hash.length - this.current_salt.length, this.current_salt.length);
                TL_auth_checkPassword req = new TL_auth_checkPassword();
                req.password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivity.this.needHideProgress();
                                LoginActivityPasswordView.this.nextPressed = false;
                                if (error == null) {
                                    TL_auth_authorization res = response;
                                    ConnectionsManager.getInstance().setUserId(res.user.id);
                                    UserConfig.clearConfig();
                                    MessagesController.getInstance().cleanup();
                                    UserConfig.setCurrentUser(res.user);
                                    UserConfig.saveConfig(true);
                                    MessagesStorage.getInstance().cleanup(true);
                                    ArrayList<User> users = new ArrayList();
                                    users.add(res.user);
                                    MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                                    MessagesController.getInstance().putUser(res.user, false);
                                    ContactsController.getInstance().checkAppAccount();
                                    MessagesController.getInstance().getBlockedUsers(true);
                                    LoginActivity.this.needFinishActivity();
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
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                } else {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            }
                        });
                    }
                }, 10);
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
                setParams(this.currentParams);
            }
            String code = bundle.getString("passview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
        }
    }

    public class LoginActivityRecoverView extends SlideView {
        private TextView cancelButton;
        private EditText codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private String email_unconfirmed_pattern;
        private boolean nextPressed;
        final /* synthetic */ LoginActivity this$0;

        public LoginActivityRecoverView(final LoginActivity this$0, Context context) {
            int i;
            int i2 = 5;
            this.this$0 = this$0;
            super(context);
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", R.string.RestoreEmailSentInfo));
            View view = this.confirmTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, i));
            this.codeField = new EditText(context);
            this.codeField.setTextColor(-14606047);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setHint(LocaleController.getString("PasswordCode", R.string.PasswordCode));
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setInputType(3);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            this.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivityRecoverView.this.onNextPressed();
                    return true;
                }
            });
            this.cancelButton = new TextView(context);
            this.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
            this.cancelButton.setTextColor(-11697229);
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            View view2 = this.cancelButton;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(view2, LayoutHelper.createLinear(-2, -2, i2 | 80, 0, 0, 0, 14));
            this.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Builder builder = new Builder(LoginActivityRecoverView.this.this$0.getParentActivity());
                    builder.setMessage(LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
                    builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginActivityRecoverView.this.this$0.setPage(6, true, new Bundle(), true);
                        }
                    });
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

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        public void setParams(Bundle params) {
            if (params != null) {
                this.codeField.setText("");
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
                this.this$0.needShowProgress();
                TL_auth_recoverPassword req = new TL_auth_recoverPassword();
                req.code = code;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivityRecoverView.this.this$0.needHideProgress();
                                LoginActivityRecoverView.this.nextPressed = false;
                                if (error == null) {
                                    TL_auth_authorization res = response;
                                    ConnectionsManager.getInstance().setUserId(res.user.id);
                                    UserConfig.clearConfig();
                                    MessagesController.getInstance().cleanup();
                                    UserConfig.setCurrentUser(res.user);
                                    UserConfig.saveConfig(true);
                                    MessagesStorage.getInstance().cleanup(true);
                                    ArrayList<User> users = new ArrayList();
                                    users.add(res.user);
                                    MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                                    MessagesController.getInstance().putUser(res.user, false);
                                    ContactsController.getInstance().checkAppAccount();
                                    MessagesController.getInstance().getBlockedUsers(true);
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
                }, 10);
            }
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onShow() {
            super.onShow();
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
                setParams(this.currentParams);
            }
            String code = bundle.getString("recoveryview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
        }
    }

    public class LoginActivityRegisterView extends SlideView {
        private Bundle currentParams;
        private EditText firstNameField;
        private EditText lastNameField;
        private boolean nextPressed = false;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;

        public LoginActivityRegisterView(Context context) {
            super(context);
            setOrientation(1);
            TextView textView = new TextView(context);
            textView.setText(LocaleController.getString("RegisterText", R.string.RegisterText));
            textView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView.setTextSize(1, 14.0f);
            addView(textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            this.firstNameField = new EditText(context);
            this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.firstNameField.setTextColor(-14606047);
            AndroidUtilities.clearCursorDrawable(this.firstNameField);
            this.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
            this.firstNameField.setImeOptions(268435461);
            this.firstNameField.setTextSize(1, 18.0f);
            this.firstNameField.setMaxLines(1);
            this.firstNameField.setInputType(8192);
            addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 26.0f, 0.0f, 0.0f));
            this.firstNameField.setOnEditorActionListener(new OnEditorActionListener(LoginActivity.this) {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivityRegisterView.this.lastNameField.requestFocus();
                    return true;
                }
            });
            this.lastNameField = new EditText(context);
            this.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
            this.lastNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.lastNameField.setTextColor(-14606047);
            AndroidUtilities.clearCursorDrawable(this.lastNameField);
            this.lastNameField.setImeOptions(268435461);
            this.lastNameField.setTextSize(1, 18.0f);
            this.lastNameField.setMaxLines(1);
            this.lastNameField.setInputType(8192);
            addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 10.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(80);
            addView(linearLayout, LayoutHelper.createLinear(-1, -1));
            TextView wrongNumber = new TextView(context);
            wrongNumber.setText(LocaleController.getString("CancelRegistration", R.string.CancelRegistration));
            wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            wrongNumber.setTextColor(-11697229);
            wrongNumber.setTextSize(1, 14.0f);
            wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            linearLayout.addView(wrongNumber, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            wrongNumber.setOnClickListener(new OnClickListener(LoginActivity.this) {
                public void onClick(View view) {
                    Builder builder = new Builder(LoginActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginActivityRegisterView.this.onBackPressed();
                            LoginActivity.this.setPage(0, true, null, true);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    LoginActivity.this.showDialog(builder.create());
                }
            });
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", R.string.YourName);
        }

        public void onShow() {
            super.onShow();
            if (this.firstNameField != null) {
                this.firstNameField.requestFocus();
                this.firstNameField.setSelection(this.firstNameField.length());
            }
        }

        public void setParams(Bundle params) {
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
                this.nextPressed = true;
                TL_auth_signUp req = new TL_auth_signUp();
                req.phone_code = this.phoneCode;
                req.phone_code_hash = this.phoneHash;
                req.phone_number = this.requestPhone;
                req.first_name = this.firstNameField.getText().toString();
                req.last_name = this.lastNameField.getText().toString();
                LoginActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivityRegisterView.this.nextPressed = false;
                                LoginActivity.this.needHideProgress();
                                if (error == null) {
                                    TL_auth_authorization res = response;
                                    ConnectionsManager.getInstance().setUserId(res.user.id);
                                    UserConfig.clearConfig();
                                    MessagesController.getInstance().cleanup();
                                    UserConfig.setCurrentUser(res.user);
                                    UserConfig.saveConfig(true);
                                    MessagesStorage.getInstance().cleanup(true);
                                    ArrayList<User> users = new ArrayList();
                                    users.add(res.user);
                                    MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                                    MessagesController.getInstance().putUser(res.user, false);
                                    ContactsController.getInstance().checkAppAccount();
                                    MessagesController.getInstance().getBlockedUsers(true);
                                    LoginActivity.this.needFinishActivity();
                                } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                } else if (error.text.contains("FIRSTNAME_INVALID")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidFirstName", R.string.InvalidFirstName));
                                } else if (error.text.contains("LASTNAME_INVALID")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidLastName", R.string.InvalidLastName));
                                } else {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            }
                        });
                    }
                }, 10);
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
                setParams(this.currentParams);
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
        private TextView resetAccountTime;
        private int startTime;
        final /* synthetic */ LoginActivity this$0;
        private Runnable timeRunnable;
        private int waitTime;

        public LoginActivityResetWaitView(final LoginActivity this$0, Context context) {
            int i;
            int i2 = 5;
            this.this$0 = this$0;
            super(context);
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(1, 14.0f);
            TextView textView = this.confirmTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            View view = this.confirmTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, i));
            TextView resetAccountText = new TextView(context);
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            resetAccountText.setGravity(i | 48);
            resetAccountText.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            resetAccountText.setText(LocaleController.getString("ResetAccountStatus", R.string.ResetAccountStatus));
            resetAccountText.setTextSize(1, 14.0f);
            resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(resetAccountText, LayoutHelper.createLinear(-2, -2, i | 48, 0, 24, 0, 0));
            this.resetAccountTime = new TextView(context);
            textView = this.resetAccountTime;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 48);
            this.resetAccountTime.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.resetAccountTime.setTextSize(1, 14.0f);
            this.resetAccountTime.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            View view2 = this.resetAccountTime;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(view2, LayoutHelper.createLinear(-2, -2, i | 48, 0, 2, 0, 0));
            this.resetAccountButton = new TextView(context);
            textView = this.resetAccountButton;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 48);
            this.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", R.string.ResetAccountButton));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.resetAccountButton.setTextSize(1, 14.0f);
            this.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            view2 = this.resetAccountButton;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(view2, LayoutHelper.createLinear(-2, -2, i2 | 48, 0, 7, 0, 0));
            this.resetAccountButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (Math.abs(ConnectionsManager.getInstance().getCurrentTime() - LoginActivityResetWaitView.this.startTime) >= LoginActivityResetWaitView.this.waitTime) {
                        Builder builder = new Builder(LoginActivityResetWaitView.this.this$0.getParentActivity());
                        builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
                        builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
                        builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LoginActivityResetWaitView.this.this$0.needShowProgress();
                                TL_account_deleteAccount req = new TL_account_deleteAccount();
                                req.reason = "Forgot password";
                                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
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
                                }, 10);
                            }
                        });
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
            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance().getCurrentTime() - this.startTime));
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
                this.resetAccountButton.setTextColor(-NUM);
            } else {
                this.resetAccountButton.setTextColor(-39322);
            }
        }

        public void setParams(Bundle params) {
            if (params != null) {
                this.currentParams = params;
                this.requestPhone = params.getString("phoneFormated");
                this.phoneHash = params.getString("phoneHash");
                this.phoneCode = params.getString("code");
                this.startTime = params.getInt("startTime");
                this.waitTime = params.getInt("waitTime");
                this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", R.string.ResetAccountInfo, PhoneFormat.getInstance().format("+" + this.requestPhone))));
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
                setParams(this.currentParams);
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private EditText codeField;
        private volatile int codeTime = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
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
        private volatile int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private boolean waitingForEvent;

        private class ProgressView extends View {
            private Paint paint = new Paint();
            private Paint paint2 = new Paint();
            private float progress;

            public ProgressView(Context context) {
                super(context);
                this.paint.setColor(-1971470);
                this.paint2.setColor(-10313520);
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

        public LoginActivitySmsView(Context context, int type) {
            super(context);
            this.currentType = type;
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (this.currentType == 3) {
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
            this.codeField = new EditText(context);
            this.codeField.setTextColor(-14606047);
            this.codeField.setHint(LocaleController.getString("Code", R.string.Code));
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setInputType(3);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
            this.codeField.addTextChangedListener(new TextWatcher(LoginActivity.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!LoginActivitySmsView.this.ignoreOnTextChange && LoginActivitySmsView.this.length != 0 && LoginActivitySmsView.this.codeField.length() == LoginActivitySmsView.this.length) {
                        LoginActivitySmsView.this.onNextPressed();
                    }
                }
            });
            this.codeField.setOnEditorActionListener(new OnEditorActionListener(LoginActivity.this) {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    LoginActivitySmsView.this.onNextPressed();
                    return true;
                }
            });
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextSize(1, 14.0f);
            this.timeText.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.timeText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 30, 0, 0));
            if (this.currentType == 3) {
                this.progressView = new ProgressView(context);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            this.problemText = new TextView(context);
            this.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            this.problemText.setGravity(LocaleController.isRTL ? 5 : 3);
            this.problemText.setTextSize(1, 14.0f);
            this.problemText.setTextColor(-11697229);
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            this.problemText.setOnClickListener(new OnClickListener(LoginActivity.this) {
                public void onClick(View v) {
                    if (!LoginActivitySmsView.this.nextPressed) {
                        if (LoginActivitySmsView.this.nextType == 0 || LoginActivitySmsView.this.nextType == 4) {
                            try {
                                PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                                Intent mailer = new Intent("android.intent.action.SEND");
                                mailer.setType("message/rfc822");
                                mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                                mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + LoginActivitySmsView.this.emailPhone);
                                mailer.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivitySmsView.this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivitySmsView.this.lastError);
                                LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                                return;
                            } catch (Exception e) {
                                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                                return;
                            }
                        }
                        LoginActivitySmsView.this.resendCode();
                    }
                }
            });
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(linearLayout, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            TextView wrongNumber = new TextView(context);
            wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            wrongNumber.setTextColor(-11697229);
            wrongNumber.setTextSize(1, 14.0f);
            wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            linearLayout.addView(wrongNumber, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 80, 0, 0, 0, 10));
            wrongNumber.setText(LocaleController.getString("WrongNumber", R.string.WrongNumber));
            wrongNumber.setOnClickListener(new OnClickListener(LoginActivity.this) {
                public void onClick(View view) {
                    TL_auth_cancelCode req = new TL_auth_cancelCode();
                    req.phone_number = LoginActivitySmsView.this.requestPhone;
                    req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                        }
                    }, 10);
                    LoginActivitySmsView.this.onBackPressed();
                    LoginActivity.this.setPage(0, true, null, true);
                }
            });
        }

        private void resendCode() {
            final Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            LoginActivity.this.needShowProgress();
            TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LoginActivitySmsView.this.nextPressed = false;
                            if (error == null) {
                                LoginActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
                            } else if (error.text != null) {
                                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                    LoginActivitySmsView.this.onBackPressed();
                                    LoginActivity.this.setPage(0, true, null, true);
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                } else if (error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                                }
                            }
                            LoginActivity.this.needHideProgress();
                        }
                    });
                }
            }, 10);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        public void setParams(Bundle params) {
            int i = 0;
            if (params != null) {
                this.codeField.setText("");
                this.waitingForEvent = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = params;
                this.phone = params.getString("phone");
                this.emailPhone = params.getString("ephone");
                this.requestPhone = params.getString("phoneFormated");
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
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    if (this.currentType == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", R.string.SentAppCode));
                    } else if (this.currentType == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, number));
                    } else if (this.currentType == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, number));
                    } else if (this.currentType == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, number));
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
                        createTimer();
                    } else if (this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) {
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
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        double currentTime = (double) System.currentTimeMillis();
                        LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTime - LoginActivitySmsView.this.lastCodeTime));
                        LoginActivitySmsView.this.lastCodeTime = currentTime;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (LoginActivitySmsView.this.codeTime <= 1000) {
                                    LoginActivitySmsView.this.problemText.setVisibility(0);
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                }
                            }
                        });
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
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            double currentTime = (double) System.currentTimeMillis();
                            LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTime - LoginActivitySmsView.this.lastCurrentTime));
                            LoginActivitySmsView.this.lastCurrentTime = currentTime;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (LoginActivitySmsView.this.time >= 1000) {
                                        int seconds = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                        } else if (LoginActivitySmsView.this.nextType == 2) {
                                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                        }
                                        if (LoginActivitySmsView.this.progressView != null) {
                                            LoginActivitySmsView.this.progressView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                            return;
                                        }
                                        return;
                                    }
                                    if (LoginActivitySmsView.this.progressView != null) {
                                        LoginActivitySmsView.this.progressView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                    }
                                    LoginActivitySmsView.this.destroyTimer();
                                    if (LoginActivitySmsView.this.currentType == 3) {
                                        AndroidUtilities.setWaitingForCall(false);
                                        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
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
                                            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                                public void run(TLObject response, final TL_error error) {
                                                    if (error != null && error.text != null) {
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                LoginActivitySmsView.this.lastError = error.text;
                                                            }
                                                        });
                                                    }
                                                }
                                            }, 10);
                                        } else if (LoginActivitySmsView.this.nextType == 3) {
                                            AndroidUtilities.setWaitingForSms(false);
                                            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                            LoginActivitySmsView.this.waitingForEvent = false;
                                            LoginActivitySmsView.this.destroyCodeTimer();
                                            LoginActivitySmsView.this.resendCode();
                                        }
                                    }
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
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                final TL_auth_signIn req = new TL_auth_signIn();
                req.phone_number = this.requestPhone;
                req.phone_code = this.codeField.getText().toString();
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                LoginActivity.this.needShowProgress();
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivitySmsView.this.nextPressed = false;
                                if (error == null) {
                                    LoginActivity.this.needHideProgress();
                                    TL_auth_authorization res = response;
                                    ConnectionsManager.getInstance().setUserId(res.user.id);
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                    UserConfig.clearConfig();
                                    MessagesController.getInstance().cleanup();
                                    UserConfig.setCurrentUser(res.user);
                                    UserConfig.saveConfig(true);
                                    MessagesStorage.getInstance().cleanup(true);
                                    ArrayList<User> users = new ArrayList();
                                    users.add(res.user);
                                    MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                                    MessagesController.getInstance().putUser(res.user, false);
                                    ContactsController.getInstance().checkAppAccount();
                                    MessagesController.getInstance().getBlockedUsers(true);
                                    LoginActivity.this.needFinishActivity();
                                    return;
                                }
                                LoginActivitySmsView.this.lastError = error.text;
                                if (error.text.contains("PHONE_NUMBER_UNOCCUPIED")) {
                                    LoginActivity.this.needHideProgress();
                                    Bundle params = new Bundle();
                                    params.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                    params.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                                    params.putString("code", req.phone_code);
                                    LoginActivity.this.setPage(5, true, params, false);
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                } else if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                                    ConnectionsManager.getInstance().sendRequest(new TL_account_getPassword(), new RequestDelegate() {
                                        public void run(final TLObject response, final TL_error error) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    LoginActivity.this.needHideProgress();
                                                    if (error == null) {
                                                        int i;
                                                        TL_account_password password = response;
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("current_salt", Utilities.bytesToHex(password.current_salt));
                                                        bundle.putString(TrackReferenceTypeBox.TYPE1, password.hint);
                                                        bundle.putString("email_unconfirmed_pattern", password.email_unconfirmed_pattern);
                                                        bundle.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                                        bundle.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
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
                                                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                                }
                                            });
                                        }
                                    }, 10);
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                } else {
                                    LoginActivity.this.needHideProgress();
                                    if ((LoginActivitySmsView.this.currentType == 3 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2)) || (LoginActivitySmsView.this.currentType == 2 && (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3))) {
                                        LoginActivitySmsView.this.createTimer();
                                    }
                                    if (LoginActivitySmsView.this.currentType == 2) {
                                        AndroidUtilities.setWaitingForSms(true);
                                        NotificationCenter.getInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                    } else if (LoginActivitySmsView.this.currentType == 3) {
                                        AndroidUtilities.setWaitingForCall(true);
                                        NotificationCenter.getInstance().addObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                                    }
                                    LoginActivitySmsView.this.waitingForEvent = true;
                                    if (LoginActivitySmsView.this.currentType == 3) {
                                        return;
                                    }
                                    if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                    } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                    } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                        LoginActivitySmsView.this.onBackPressed();
                                        LoginActivity.this.setPage(0, true, null, true);
                                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                    } else {
                                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                                    }
                                }
                            }
                        });
                    }
                }, 10);
            }
        }

        public void onBackPressed() {
            destroyTimer();
            destroyCodeTimer();
            this.currentParams = null;
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
        }

        public void onDestroyActivity() {
            super.onDestroyActivity();
            if (this.currentType == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (this.currentType == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        public void onShow() {
            super.onShow();
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
            }
        }

        public void didReceivedNotification(int id, Object... args) {
            if (this.waitingForEvent && this.codeField != null) {
                if (id == NotificationCenter.didReceiveSmsCode) {
                    this.ignoreOnTextChange = true;
                    this.codeField.setText("" + args[0]);
                    this.ignoreOnTextChange = false;
                    onNextPressed();
                } else if (id == NotificationCenter.didReceiveCall) {
                    String num = "" + args[0];
                    if (this.pattern.equals("*") || num.contains(this.pattern.replace("*", ""))) {
                        this.ignoreOnTextChange = true;
                        this.codeField.setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    }
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("smsview_code_" + this.currentType, code);
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
                setParams(this.currentParams);
            }
            String code = bundle.getString("smsview_code_" + this.currentType);
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
        private EditText codeField;
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

        public PhoneView(Context context) {
            super(context);
            setOrientation(1);
            this.countryButton = new TextView(context);
            this.countryButton.setTextSize(1, 18.0f);
            this.countryButton.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), 0);
            this.countryButton.setTextColor(-14606047);
            this.countryButton.setMaxLines(1);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.countryButton.setBackgroundResource(R.drawable.spinner_states);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            final LoginActivity loginActivity = LoginActivity.this;
            this.countryButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CountrySelectActivity fragment = new CountrySelectActivity();
                    fragment.setCountrySelectActivityDelegate(new CountrySelectActivityDelegate() {
                        public void didSelectCountry(String name) {
                            PhoneView.this.selectCountry(name);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                                }
                            }, 300);
                            PhoneView.this.phoneField.requestFocus();
                            PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                        }
                    });
                    LoginActivity.this.presentFragment(fragment);
                }
            });
            View view = new View(context);
            view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            view.setBackgroundColor(-2368549);
            addView(view, LayoutHelper.createLinear(-1, 1, 4.0f, -17.5f, 4.0f, 0.0f));
            view = new LinearLayout(context);
            view.setOrientation(0);
            addView(view, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            view = new TextView(context);
            view.setText("+");
            view.setTextColor(-14606047);
            view.setTextSize(1, 18.0f);
            view.addView(view, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditText(context);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(-14606047);
            AndroidUtilities.clearCursorDrawable(this.codeField);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(268435461);
            this.codeField.setFilters(new InputFilter[]{new LengthFilter(5)});
            view.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            final LoginActivity loginActivity2 = LoginActivity.this;
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
                            PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
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
                                        textToSet = text.substring(a, text.length()) + PhoneView.this.phoneField.getText().toString();
                                        text = sub;
                                        PhoneView.this.codeField.setText(sub);
                                        break;
                                    }
                                }
                                if (!ok) {
                                    textToSet = text.substring(1, text.length()) + PhoneView.this.phoneField.getText().toString();
                                    EditText access$600 = PhoneView.this.codeField;
                                    text = text.substring(0, 1);
                                    access$600.setText(text);
                                }
                            }
                            String country = (String) PhoneView.this.codesMap.get(text);
                            if (country != null) {
                                int index = PhoneView.this.countriesArray.indexOf(country);
                                if (index != -1) {
                                    PhoneView.this.ignoreSelection = true;
                                    PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(index));
                                    String hint = (String) PhoneView.this.phoneFormatMap.get(text);
                                    PhoneView.this.phoneField.setHintText(hint != null ? hint.replace('X', '–') : null);
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
            final LoginActivity loginActivity22 = LoginActivity.this;
            this.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    PhoneView.this.phoneField.requestFocus();
                    PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                    return true;
                }
            });
            this.phoneField = new HintEditText(context);
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(-14606047);
            this.phoneField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.phoneField.setPadding(0, 0, 0, 0);
            AndroidUtilities.clearCursorDrawable(this.phoneField);
            this.phoneField.setTextSize(1, 18.0f);
            this.phoneField.setMaxLines(1);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(268435461);
            view.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            final LoginActivity loginActivity222 = LoginActivity.this;
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
                        String phoneChars = "0123456789";
                        String str = PhoneView.this.phoneField.getText().toString();
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
                        PhoneView.this.phoneField.setText(builder);
                        if (start >= 0) {
                            HintEditText access$400 = PhoneView.this.phoneField;
                            if (start > PhoneView.this.phoneField.length()) {
                                start = PhoneView.this.phoneField.length();
                            }
                            access$400.setSelection(start);
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            final LoginActivity loginActivity2222 = LoginActivity.this;
            this.phoneField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return false;
                    }
                    PhoneView.this.onNextPressed();
                    return true;
                }
            });
            view = new TextView(context);
            view.setText(LocaleController.getString("StartText", R.string.StartText));
            view.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            view.setTextSize(1, 14.0f);
            view.setGravity(LocaleController.isRTL ? 5 : 3);
            view.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            addView(view, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
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
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            final LoginActivity loginActivity22222 = LoginActivity.this;
            Collections.sort(this.countriesArray, new Comparator<String>() {
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
                FileLog.e("tmessages", e2);
            }
            if (country != null) {
                String countryName = (String) languageMap.get(country);
                if (!(countryName == null || this.countriesArray.indexOf(countryName) == -1)) {
                    this.codeField.setText((CharSequence) this.countriesMap.get(countryName));
                    this.countryState = 0;
                }
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
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

        public void selectCountry(String name) {
            if (this.countriesArray.indexOf(name) != -1) {
                this.ignoreOnTextChange = true;
                String code = (String) this.countriesMap.get(name);
                this.codeField.setText(code);
                this.countryButton.setText(name);
                String hint = (String) this.phoneFormatMap.get(code);
                this.phoneField.setHintText(hint != null ? hint.replace('X', '–') : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
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
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
                boolean allowCall = true;
                if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean allowSms = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                    if (LoginActivity.this.checkPermissions) {
                        LoginActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowSms) {
                            LoginActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
                        }
                        if (!LoginActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            if (preferences.getBoolean("firstlogin", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                if (LoginActivity.this.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", R.string.AllowReadCallAndSms));
                                } else if (allowSms) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", R.string.AllowReadSms));
                                }
                                LoginActivity.this.permissionsDialog = LoginActivity.this.showDialog(builder.create());
                                return;
                            }
                            LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[LoginActivity.this.permissionsItems.size()]), 6);
                            return;
                        }
                    }
                }
                if (this.countryState == 1) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION && !this.codeField.getText().toString().equals("999")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("WrongCountry", R.string.WrongCountry));
                } else if (this.codeField.length() == 0) {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else {
                    ConnectionsManager.getInstance().cleanup();
                    final TL_auth_sendCode req = new TL_auth_sendCode();
                    String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    ConnectionsManager.getInstance().applyCountryPortNumber(phone);
                    req.api_hash = BuildVars.APP_HASH;
                    req.api_id = BuildVars.APP_ID;
                    req.phone_number = phone;
                    boolean z = simcardAvailable && allowCall;
                    req.allow_flashcall = z;
                    if (req.allow_flashcall) {
                        try {
                            String number = tm.getLine1Number();
                            z = (number == null || number.length() == 0 || (!phone.contains(number) && !number.contains(phone))) ? false : true;
                            req.current_number = z;
                        } catch (Throwable e) {
                            req.allow_flashcall = false;
                            FileLog.e("tmessages", e);
                        }
                    }
                    final Bundle params = new Bundle();
                    params.putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Throwable e2) {
                        FileLog.e("tmessages", e2);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    LoginActivity.this.needShowProgress();
                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    PhoneView.this.nextPressed = false;
                                    if (error == null) {
                                        LoginActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
                                    } else if (error.text != null) {
                                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                            LoginActivity.this.needShowInvalidAlert(req.phone_number, false);
                                        } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                                            LoginActivity.this.needShowInvalidAlert(req.phone_number, true);
                                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("FloodWait", R.string.FloodWait));
                                        } else if (error.code != NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
                                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
                                        }
                                    }
                                    LoginActivity.this.needHideProgress();
                                }
                            });
                        }
                    }, 27);
                }
            }
        }

        public void fillNumber() {
            boolean allowSms = false;
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (VERSION.SDK_INT >= 23 && tm.getSimState() != 1 && tm.getPhoneType() != 0) {
                    boolean allowCall;
                    if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                        allowCall = true;
                    } else {
                        allowCall = false;
                    }
                    if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0) {
                        allowSms = true;
                    }
                    if (LoginActivity.this.checkShowPermissions && !allowCall && !allowSms) {
                        LoginActivity.this.permissionsShowItems.clear();
                        if (!allowCall) {
                            LoginActivity.this.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowSms) {
                            LoginActivity.this.permissionsShowItems.add("android.permission.RECEIVE_SMS");
                        }
                        if (!LoginActivity.this.permissionsShowItems.isEmpty()) {
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            if (preferences.getBoolean("firstloginshow", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                preferences.edit().putBoolean("firstloginshow", false).commit();
                                Builder builder = new Builder(LoginActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                builder.setMessage(LocaleController.getString("AllowFillNumber", R.string.AllowFillNumber));
                                LoginActivity.this.permissionsShowDialog = LoginActivity.this.showDialog(builder.create());
                                return;
                            }
                            LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsShowItems.toArray(new String[LoginActivity.this.permissionsShowItems.size()]), 7);
                        }
                    } else if (allowCall || allowSms) {
                        String number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                        String textToSet = null;
                        boolean ok = false;
                        if (!TextUtils.isEmpty(number)) {
                            if (number.length() > 4) {
                                for (int a = 4; a >= 1; a--) {
                                    String sub = number.substring(0, a);
                                    if (((String) this.codesMap.get(sub)) != null) {
                                        ok = true;
                                        textToSet = number.substring(a, number.length());
                                        this.codeField.setText(sub);
                                        break;
                                    }
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
                FileLog.e("tmessages", e);
            }
        }

        public void onShow() {
            super.onShow();
            if (this.phoneField != null) {
                if (this.codeField.length() != 0) {
                    AndroidUtilities.showKeyboard(this.phoneField);
                    this.phoneField.requestFocus();
                    this.phoneField.setSelection(this.phoneField.length());
                } else {
                    AndroidUtilities.showKeyboard(this.codeField);
                    this.codeField.requestFocus();
                }
            }
            fillNumber();
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
                FileLog.e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
                } else if (id == -1) {
                    LoginActivity.this.onBackPressed();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
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
            frameLayout.addView(this.views[a], LayoutHelper.createFrame(-1, a == 0 ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, BitmapDescriptorFactory.HUE_ORANGE, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            a++;
        }
        Bundle savedInstanceState = loadCurrentState();
        if (savedInstanceState != null) {
            this.currentViewNum = savedInstanceState.getInt("currentViewNum", 0);
            if (this.currentViewNum >= 1 && this.currentViewNum <= 4) {
                int time = savedInstanceState.getInt("open");
                if (time != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
                    this.currentViewNum = 0;
                    savedInstanceState = null;
                    clearCurrentState();
                }
            }
        }
        this.actionBar.setTitle(this.views[this.currentViewNum].getHeaderName());
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
                this.actionBar.setBackButtonImage(this.views[a].needBackButton() ? R.drawable.ic_ab_back : 0);
                this.views[a].setVisibility(0);
                this.views[a].onShow();
                if (a == 3 || a == 8) {
                    this.doneButton.setVisibility(8);
                }
            } else {
                this.views[a].setVisibility(8);
            }
            a++;
        }
        return this.fragmentView;
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
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
            FileLog.e("tmessages", e);
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
            FileLog.e("tmessages", e);
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

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT < 23) {
            return;
        }
        if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
            getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[this.permissionsShowItems.size()]), 7);
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
            this.views[this.currentViewNum].onBackPressed();
            setPage(0, true, null, true);
        } else if (this.currentViewNum == 7 || this.currentViewNum == 8) {
            this.views[this.currentViewNum].onBackPressed();
            setPage(6, true, null, true);
        }
        return false;
    }

    private void needShowAlert(String title, String text) {
        if (text != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
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
                        if (banned) {
                            mailer.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + phoneNumber);
                            mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
                        } else {
                            mailer.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + phoneNumber);
                            mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
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

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(getParentActivity());
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
                FileLog.e("tmessages", e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        int i = R.drawable.ic_ab_back;
        if (page == 3 || page == 8) {
            this.doneButton.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        if (animated) {
            float f;
            final SlideView outView = this.views[this.currentViewNum];
            final SlideView newView = this.views[page];
            this.currentViewNum = page;
            ActionBar actionBar = this.actionBar;
            if (!newView.needBackButton()) {
                i = 0;
            }
            actionBar.setBackButtonImage(i);
            newView.setParams(params);
            this.actionBar.setTitle(newView.getHeaderName());
            newView.onShow();
            if (back) {
                f = (float) (-AndroidUtilities.displaySize.x);
            } else {
                f = (float) AndroidUtilities.displaySize.x;
            }
            newView.setX(f);
            ViewPropertyAnimator duration = outView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListener() {
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
            }).setDuration(300);
            if (back) {
                f = (float) AndroidUtilities.displaySize.x;
            } else {
                f = (float) (-AndroidUtilities.displaySize.x);
            }
            duration.translationX(f).start();
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
            return;
        }
        actionBar = this.actionBar;
        if (!this.views[page].needBackButton()) {
            i = 0;
        }
        actionBar.setBackButtonImage(i);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = page;
        this.views[page].setParams(params);
        this.views[page].setVisibility(0);
        this.actionBar.setTitle(this.views[page].getHeaderName());
        this.views[page].onShow();
    }

    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
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
            FileLog.e("tmessages", e);
        }
    }

    private void needFinishActivity() {
        clearCurrentState();
        presentFragment(new DialogsActivity(null), true);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
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
}
