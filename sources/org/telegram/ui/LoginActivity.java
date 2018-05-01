package org.telegram.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
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
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_auth_signUp;
import org.telegram.tgnet.TLRPC.TL_error;
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

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            float measuredWidth = (float) ((int) (((float) getMeasuredWidth()) * this.progress));
            canvas.drawRect(0.0f, 0.0f, measuredWidth, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect(measuredWidth, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    /* renamed from: org.telegram.ui.LoginActivity$1 */
    class C21821 extends ActionBarMenuOnItemClick {
        C21821() {
        }

        public void onItemClick(int i) {
            if (i == 1) {
                LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
            } else if (i == -1) {
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
        class C14805 implements Runnable {
            C14805() {
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
        class C21854 implements RequestDelegate {
            C21854() {
            }

            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        LoginActivityPasswordView.this.this$0.needHideProgress();
                        LoginActivityPasswordView.this.nextPressed = false;
                        if (tL_error == null) {
                            TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) tLObject;
                            ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).setUserId(tL_auth_authorization.user.id);
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).clearConfig();
                            MessagesController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).cleanup();
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).setCurrentUser(tL_auth_authorization.user);
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).syncContacts = LoginActivityPasswordView.this.this$0.syncContacts;
                            UserConfig.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).saveConfig(true);
                            MessagesStorage.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).cleanup(true);
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(tL_auth_authorization.user);
                            MessagesStorage.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).putUsersAndChats(arrayList, null, true, true);
                            MessagesController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).putUser(tL_auth_authorization.user, false);
                            ContactsController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).checkAppAccount();
                            MessagesController.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).getBlockedUsers(true);
                            ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).updateDcSettings();
                            LoginActivityPasswordView.this.this$0.needFinishActivity();
                        } else if (tL_error.text.equals("PASSWORD_HASH_INVALID")) {
                            LoginActivityPasswordView.this.onPasscodeError(true);
                        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                            String formatPluralString;
                            int intValue = Utilities.parseInt(tL_error.text).intValue();
                            if (intValue < 60) {
                                formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                            } else {
                                formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                            }
                            LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                        } else {
                            LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                        }
                    }
                });
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public LoginActivityPasswordView(LoginActivity loginActivity, Context context) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 3;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.confirmTextView.setText(LocaleController.getString("LoginPasswordText", C0446R.string.LoginPasswordText));
            addView(r0.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            r0.codeField = new EditTextBoldCursor(context2);
            r0.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.codeField.setCursorWidth(1.5f);
            r0.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.codeField.setHint(LocaleController.getString("LoginPassword", C0446R.string.LoginPassword));
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
                        return null;
                    }
                    LoginActivityPasswordView.this.onNextPressed();
                    return true;
                }
            });
            r0.cancelButton = new TextView(context2);
            r0.cancelButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.cancelButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.cancelButton.setText(LocaleController.getString("ForgotPassword", C0446R.string.ForgotPassword));
            r0.cancelButton.setTextSize(1, 14.0f);
            r0.cancelButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.cancelButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(r0.cancelButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48));
            r0.cancelButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$2$1 */
                class C21831 implements RequestDelegate {
                    C21831() {
                    }

                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivityPasswordView.this.this$0.needHideProgress();
                                if (tL_error == null) {
                                    final TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) tLObject;
                                    Builder builder = new Builder(LoginActivityPasswordView.this.this$0.getParentActivity());
                                    builder.setMessage(LocaleController.formatString("RestoreEmailSent", C0446R.string.RestoreEmailSent, tL_auth_passwordRecovery.email_pattern));
                                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface = new Bundle();
                                            dialogInterface.putString("email_unconfirmed_pattern", tL_auth_passwordRecovery.email_pattern);
                                            LoginActivityPasswordView.this.this$0.setPage(7, true, dialogInterface, false);
                                        }
                                    });
                                    Dialog showDialog = LoginActivityPasswordView.this.this$0.showDialog(builder.create());
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
                                    LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                                } else {
                                    LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                }
                            }
                        });
                    }
                }

                public void onClick(View view) {
                    if (LoginActivityPasswordView.this.has_recovery != null) {
                        LoginActivityPasswordView.this.this$0.needShowProgress(0);
                        ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new C21831(), 10);
                        return;
                    }
                    LoginActivityPasswordView.this.resetAccountText.setVisibility(0);
                    LoginActivityPasswordView.this.resetAccountButton.setVisibility(0);
                    AndroidUtilities.hideKeyboard(LoginActivityPasswordView.this.codeField);
                    LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("RestorePasswordNoEmailTitle", C0446R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", C0446R.string.RestorePasswordNoEmailText));
                }
            });
            r0.resetAccountButton = new TextView(context2);
            r0.resetAccountButton.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText6));
            r0.resetAccountButton.setVisibility(8);
            r0.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", C0446R.string.ResetMyAccount));
            r0.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.resetAccountButton.setTextSize(1, 14.0f);
            r0.resetAccountButton.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0f), 0, 0);
            addView(r0.resetAccountButton, LayoutHelper.createLinear(-2, -2, 48 | (LocaleController.isRTL ? 5 : 3), 0, 34, 0, 0));
            r0.resetAccountButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$3$1 */
                class C14771 implements DialogInterface.OnClickListener {

                    /* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$3$1$1 */
                    class C21841 implements RequestDelegate {
                        C21841() {
                        }

                        public void run(TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    LoginActivityPasswordView.this.this$0.needHideProgress();
                                    Bundle bundle;
                                    if (tL_error == null) {
                                        bundle = new Bundle();
                                        bundle.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                        bundle.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                        bundle.putString("code", LoginActivityPasswordView.this.phoneCode);
                                        LoginActivityPasswordView.this.this$0.setPage(5, true, bundle, false);
                                    } else if (tL_error.text.equals("2FA_RECENT_CONFIRM")) {
                                        LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", C0446R.string.ResetAccountCancelledAlert));
                                    } else if (tL_error.text.startsWith("2FA_CONFIRM_WAIT_")) {
                                        bundle = new Bundle();
                                        bundle.putString("phoneFormated", LoginActivityPasswordView.this.requestPhone);
                                        bundle.putString("phoneHash", LoginActivityPasswordView.this.phoneHash);
                                        bundle.putString("code", LoginActivityPasswordView.this.phoneCode);
                                        bundle.putInt("startTime", ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).getCurrentTime());
                                        bundle.putInt("waitTime", Utilities.parseInt(tL_error.text.replace("2FA_CONFIRM_WAIT_", TtmlNode.ANONYMOUS_REGION_ID)).intValue());
                                        LoginActivityPasswordView.this.this$0.setPage(8, true, bundle, false);
                                    } else {
                                        LoginActivityPasswordView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                    }
                                }
                            });
                        }
                    }

                    C14771() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityPasswordView.this.this$0.needShowProgress(0);
                        dialogInterface = new TL_account_deleteAccount();
                        dialogInterface.reason = "Forgot password";
                        ConnectionsManager.getInstance(LoginActivityPasswordView.this.this$0.currentAccount).sendRequest(dialogInterface, new C21841(), 10);
                    }
                }

                public void onClick(View view) {
                    view = new Builder(LoginActivityPasswordView.this.this$0.getParentActivity());
                    view.setMessage(LocaleController.getString("ResetMyAccountWarningText", C0446R.string.ResetMyAccountWarningText));
                    view.setTitle(LocaleController.getString("ResetMyAccountWarning", C0446R.string.ResetMyAccountWarning));
                    view.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", C0446R.string.ResetMyAccountWarningReset), new C14771());
                    view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    LoginActivityPasswordView.this.this$0.showDialog(view.create());
                }
            });
            r0.resetAccountText = new TextView(context2);
            r0.resetAccountText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.resetAccountText.setVisibility(8);
            r0.resetAccountText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r0.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", C0446R.string.ResetMyAccountText));
            r0.resetAccountText.setTextSize(1, 14.0f);
            r0.resetAccountText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            View view = r0.resetAccountText;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createLinear(-2, -2, 48 | i, 0, 7, 0, 14));
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", C0446R.string.LoginPassword);
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
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.currentParams = bundle;
                this.current_salt = Utilities.hexToBytes(this.currentParams.getString("current_salt"));
                this.hint = this.currentParams.getString(TrackReferenceTypeBox.TYPE1);
                if (this.currentParams.getInt("has_recovery")) {
                    z2 = true;
                }
                this.has_recovery = z2;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                if (this.hint == null || this.hint.length() <= null) {
                    this.codeField.setHint(LocaleController.getString("LoginPassword", C0446R.string.LoginPassword));
                } else {
                    this.codeField.setHint(this.hint);
                }
            }
        }

        private void onPasscodeError(boolean z) {
            if (this.this$0.getParentActivity() != null) {
                Vibrator vibrator = (Vibrator) this.this$0.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                if (z) {
                    this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                }
                AndroidUtilities.shakeView(this.confirmTextView, 2.0f, 0);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                Object bytes;
                this.nextPressed = true;
                try {
                    bytes = obj.getBytes(C0542C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    bytes = null;
                }
                this.this$0.needShowProgress(0);
                Object obj2 = new byte[((this.current_salt.length * 2) + bytes.length)];
                System.arraycopy(this.current_salt, 0, obj2, 0, this.current_salt.length);
                System.arraycopy(bytes, 0, obj2, this.current_salt.length, bytes.length);
                System.arraycopy(this.current_salt, 0, obj2, obj2.length - this.current_salt.length, this.current_salt.length);
                TLObject tL_auth_checkPassword = new TL_auth_checkPassword();
                tL_auth_checkPassword.password_hash = Utilities.computeSHA256(obj2, 0, obj2.length);
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_checkPassword, new C21854(), 10);
            }
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new C14805(), 100);
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("passview_code", obj);
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
            bundle = bundle.getString("passview_code");
            if (bundle != null) {
                this.codeField.setText(bundle);
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
        class C14854 implements Runnable {
            C14854() {
            }

            public void run() {
                if (LoginActivityRecoverView.this.codeField != null) {
                    LoginActivityRecoverView.this.codeField.requestFocus();
                    LoginActivityRecoverView.this.codeField.setSelection(LoginActivityRecoverView.this.codeField.length());
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRecoverView$3 */
        class C21863 implements RequestDelegate {
            C21863() {
            }

            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        LoginActivityRecoverView.this.this$0.needHideProgress();
                        LoginActivityRecoverView.this.nextPressed = false;
                        if (tL_error == null) {
                            TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) tLObject;
                            ConnectionsManager.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).setUserId(tL_auth_authorization.user.id);
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).clearConfig();
                            MessagesController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).cleanup();
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).setCurrentUser(tL_auth_authorization.user);
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).syncContacts = LoginActivityRecoverView.this.this$0.syncContacts;
                            UserConfig.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).saveConfig(true);
                            MessagesStorage.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).cleanup(true);
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(tL_auth_authorization.user);
                            MessagesStorage.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).putUsersAndChats(arrayList, null, true, true);
                            MessagesController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).putUser(tL_auth_authorization.user, false);
                            ContactsController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).checkAppAccount();
                            MessagesController.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).getBlockedUsers(true);
                            ConnectionsManager.getInstance(LoginActivityRecoverView.this.this$0.currentAccount).updateDcSettings();
                            LoginActivityRecoverView.this.this$0.needFinishActivity();
                        } else if (tL_error.text.startsWith("CODE_INVALID")) {
                            LoginActivityRecoverView.this.onPasscodeError(true);
                        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                            String formatPluralString;
                            int intValue = Utilities.parseInt(tL_error.text).intValue();
                            if (intValue < 60) {
                                formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                            } else {
                                formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                            }
                            LoginActivityRecoverView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                        } else {
                            LoginActivityRecoverView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                        }
                    }
                });
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public LoginActivityRecoverView(LoginActivity loginActivity, Context context) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", C0446R.string.RestoreEmailSentInfo));
            addView(r0.confirmTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
            r0.codeField = new EditTextBoldCursor(context2);
            r0.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.codeField.setCursorWidth(1.5f);
            r0.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.codeField.setHint(LocaleController.getString("PasswordCode", C0446R.string.PasswordCode));
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
                        return null;
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
                class C14821 implements DialogInterface.OnClickListener {
                    C14821() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityRecoverView.this.this$0.setPage(6, true, new Bundle(), true);
                    }
                }

                public void onClick(View view) {
                    view = new Builder(LoginActivityRecoverView.this.this$0.getParentActivity());
                    view.setMessage(LocaleController.getString("RestoreEmailTroubleText", C0446R.string.RestoreEmailTroubleText));
                    view.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", C0446R.string.RestorePasswordNoEmailTitle));
                    view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C14821());
                    view = LoginActivityRecoverView.this.this$0.showDialog(view.create());
                    if (view != null) {
                        view.setCanceledOnTouchOutside(false);
                        view.setCancelable(false);
                    }
                }
            });
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", C0446R.string.LoginPassword);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.currentParams = bundle;
                this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
                this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", C0446R.string.RestoreEmailTrouble, this.email_unconfirmed_pattern));
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
                String obj = this.codeField.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.this$0.needShowProgress(0);
                TLObject tL_auth_recoverPassword = new TL_auth_recoverPassword();
                tL_auth_recoverPassword.code = obj;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_recoverPassword, new C21863(), 10);
            }
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new C14854(), 100);
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (!(obj == null || obj.length() == 0)) {
                bundle.putString("recoveryview_code", obj);
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
            bundle = bundle.getString("recoveryview_code");
            if (bundle != null) {
                this.codeField.setText(bundle);
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
        class C14904 implements Runnable {
            C14904() {
            }

            public void run() {
                if (LoginActivityRegisterView.this.firstNameField != null) {
                    LoginActivityRegisterView.this.firstNameField.requestFocus();
                    LoginActivityRegisterView.this.firstNameField.setSelection(LoginActivityRegisterView.this.firstNameField.length());
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$5 */
        class C21875 implements RequestDelegate {
            C21875() {
            }

            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        LoginActivityRegisterView.this.nextPressed = false;
                        LoginActivityRegisterView.this.this$0.needHideProgress();
                        if (tL_error == null) {
                            TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) tLObject;
                            ConnectionsManager.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).setUserId(tL_auth_authorization.user.id);
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).clearConfig();
                            MessagesController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).cleanup();
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).setCurrentUser(tL_auth_authorization.user);
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).syncContacts = LoginActivityRegisterView.this.this$0.syncContacts;
                            UserConfig.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).saveConfig(true);
                            MessagesStorage.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).cleanup(true);
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(tL_auth_authorization.user);
                            MessagesStorage.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).putUsersAndChats(arrayList, null, true, true);
                            MessagesController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).putUser(tL_auth_authorization.user, false);
                            ContactsController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).checkAppAccount();
                            MessagesController.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).getBlockedUsers(true);
                            ConnectionsManager.getInstance(LoginActivityRegisterView.this.this$0.currentAccount).updateDcSettings();
                            LoginActivityRegisterView.this.this$0.needFinishActivity();
                        } else if (tL_error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                        } else {
                            if (!tL_error.text.contains("PHONE_CODE_EMPTY")) {
                                if (!tL_error.text.contains("PHONE_CODE_INVALID")) {
                                    if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                        return;
                                    } else if (tL_error.text.contains("FIRSTNAME_INVALID")) {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidFirstName", C0446R.string.InvalidFirstName));
                                        return;
                                    } else if (tL_error.text.contains("LASTNAME_INVALID")) {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidLastName", C0446R.string.InvalidLastName));
                                        return;
                                    } else {
                                        LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                        return;
                                    }
                                }
                            }
                            LoginActivityRegisterView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
                        }
                    }
                });
            }
        }

        public LoginActivityRegisterView(LoginActivity loginActivity, Context context) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            setOrientation(1);
            this.textView = new TextView(context2);
            this.textView.setText(LocaleController.getString("RegisterText", C0446R.string.RegisterText));
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
            r0.firstNameField.setHint(LocaleController.getString("FirstName", C0446R.string.FirstName));
            r0.firstNameField.setImeOptions(268435461);
            r0.firstNameField.setTextSize(1, 18.0f);
            r0.firstNameField.setMaxLines(1);
            r0.firstNameField.setInputType(MessagesController.UPDATE_MASK_CHANNEL);
            addView(r0.firstNameField, LayoutHelper.createLinear(-1, 36, 0.0f, 26.0f, 0.0f, 0.0f));
            r0.firstNameField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return null;
                    }
                    LoginActivityRegisterView.this.lastNameField.requestFocus();
                    return true;
                }
            });
            r0.lastNameField = new EditTextBoldCursor(context2);
            r0.lastNameField.setHint(LocaleController.getString("LastName", C0446R.string.LastName));
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
                            return null;
                        }
                    }
                    LoginActivityRegisterView.this.onNextPressed();
                    return true;
                }
            });
            View linearLayout = new LinearLayout(context2);
            linearLayout.setGravity(80);
            addView(linearLayout, LayoutHelper.createLinear(-1, -1));
            r0.wrongNumber = new TextView(context2);
            r0.wrongNumber.setText(LocaleController.getString("CancelRegistration", C0446R.string.CancelRegistration));
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
                class C14881 implements DialogInterface.OnClickListener {
                    C14881() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityRegisterView.this.onBackPressed();
                        LoginActivityRegisterView.this.this$0.setPage(0, true, null, true);
                    }
                }

                public void onClick(View view) {
                    view = new Builder(LoginActivityRegisterView.this.this$0.getParentActivity());
                    view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    view.setMessage(LocaleController.getString("AreYouSureRegistration", C0446R.string.AreYouSureRegistration));
                    view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C14881());
                    view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    LoginActivityRegisterView.this.this$0.showDialog(view.create());
                }
            });
        }

        public void onBackPressed() {
            this.currentParams = null;
        }

        public String getHeaderName() {
            return LocaleController.getString("YourName", C0446R.string.YourName);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new C14904(), 100);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.firstNameField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.lastNameField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                this.currentParams = bundle;
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                this.nextPressed = true;
                TLObject tL_auth_signUp = new TL_auth_signUp();
                tL_auth_signUp.phone_code = this.phoneCode;
                tL_auth_signUp.phone_code_hash = this.phoneHash;
                tL_auth_signUp.phone_number = this.requestPhone;
                tL_auth_signUp.first_name = this.firstNameField.getText().toString();
                tL_auth_signUp.last_name = this.lastNameField.getText().toString();
                this.this$0.needShowProgress(0);
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_signUp, new C21875(), 10);
            }
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
            if (this.currentParams != null) {
                bundle.putBundle("registerview_params", this.currentParams);
            }
        }

        public void restoreStateParams(Bundle bundle) {
            this.currentParams = bundle.getBundle("registerview_params");
            if (this.currentParams != null) {
                setParams(this.currentParams, true);
            }
            CharSequence string = bundle.getString("registerview_first");
            if (string != null) {
                this.firstNameField.setText(string);
            }
            bundle = bundle.getString("registerview_last");
            if (bundle != null) {
                this.lastNameField.setText(bundle);
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
        class C14952 implements Runnable {
            C14952() {
            }

            public void run() {
                if (LoginActivityResetWaitView.this.timeRunnable == this) {
                    LoginActivityResetWaitView.this.updateTimeText();
                    AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000);
                }
            }
        }

        public boolean needBackButton() {
            return true;
        }

        public LoginActivityResetWaitView(LoginActivity loginActivity, Context context) {
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
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
            r0.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", C0446R.string.ResetAccountStatus));
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
            r0.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", C0446R.string.ResetAccountButton));
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
                class C14931 implements DialogInterface.OnClickListener {

                    /* renamed from: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$1$1$1 */
                    class C21881 implements RequestDelegate {
                        C21881() {
                        }

                        public void run(TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    LoginActivityResetWaitView.this.this$0.needHideProgress();
                                    if (tL_error == null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("phoneFormated", LoginActivityResetWaitView.this.requestPhone);
                                        bundle.putString("phoneHash", LoginActivityResetWaitView.this.phoneHash);
                                        bundle.putString("code", LoginActivityResetWaitView.this.phoneCode);
                                        LoginActivityResetWaitView.this.this$0.setPage(5, true, bundle, false);
                                    } else if (tL_error.text.equals("2FA_RECENT_CONFIRM")) {
                                        LoginActivityResetWaitView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("ResetAccountCancelledAlert", C0446R.string.ResetAccountCancelledAlert));
                                    } else {
                                        LoginActivityResetWaitView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                    }
                                }
                            });
                        }
                    }

                    C14931() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivityResetWaitView.this.this$0.needShowProgress(0);
                        dialogInterface = new TL_account_deleteAccount();
                        dialogInterface.reason = "Forgot password";
                        ConnectionsManager.getInstance(LoginActivityResetWaitView.this.this$0.currentAccount).sendRequest(dialogInterface, new C21881(), 10);
                    }
                }

                public void onClick(View view) {
                    if (Math.abs(ConnectionsManager.getInstance(LoginActivityResetWaitView.this.this$0.currentAccount).getCurrentTime() - LoginActivityResetWaitView.this.startTime) >= LoginActivityResetWaitView.this.waitTime) {
                        view = new Builder(LoginActivityResetWaitView.this.this$0.getParentActivity());
                        view.setMessage(LocaleController.getString("ResetMyAccountWarningText", C0446R.string.ResetMyAccountWarningText));
                        view.setTitle(LocaleController.getString("ResetMyAccountWarning", C0446R.string.ResetMyAccountWarning));
                        view.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", C0446R.string.ResetMyAccountWarningReset), new C14931());
                        view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                        LoginActivityResetWaitView.this.this$0.showDialog(view.create());
                    }
                }
            });
        }

        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", C0446R.string.ResetAccount);
        }

        private void updateTimeText() {
            int max = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(this.this$0.currentAccount).getCurrentTime() - this.startTime));
            int i = max / 86400;
            int i2 = max - (86400 * i);
            int i3 = i2 / 3600;
            i2 = (i2 - (i3 * 3600)) / 60;
            int i4 = max % 60;
            StringBuilder stringBuilder;
            if (i != 0) {
                TextView textView = this.resetAccountTime;
                stringBuilder = new StringBuilder();
                stringBuilder.append(LocaleController.formatPluralString("DaysBold", i));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("HoursBold", i3));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("MinutesBold", i2));
                textView.setText(AndroidUtilities.replaceTags(stringBuilder.toString()));
            } else {
                TextView textView2 = this.resetAccountTime;
                stringBuilder = new StringBuilder();
                stringBuilder.append(LocaleController.formatPluralString("HoursBold", i3));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("MinutesBold", i2));
                stringBuilder.append(" ");
                stringBuilder.append(LocaleController.formatPluralString("SecondsBold", i4));
                textView2.setText(AndroidUtilities.replaceTags(stringBuilder.toString()));
            }
            if (max > 0) {
                this.resetAccountButton.setTag(Theme.key_windowBackgroundWhiteGrayText6);
                this.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
                return;
            }
            this.resetAccountButton.setTag(Theme.key_windowBackgroundWhiteRedText6);
            this.resetAccountButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText6));
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.currentParams = bundle;
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                this.phoneCode = bundle.getString("code");
                this.startTime = bundle.getInt("startTime");
                this.waitTime = bundle.getInt("waitTime");
                bundle = this.confirmTextView;
                Object[] objArr = new Object[1];
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(this.requestPhone);
                objArr[0] = LocaleController.addNbsp(instance.format(stringBuilder.toString()));
                bundle.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", C0446R.string.ResetAccountInfo, objArr)));
                updateTimeText();
                this.timeRunnable = new C14952();
                AndroidUtilities.runOnUIThread(this.timeRunnable, 1000);
            }
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
        class C15026 extends TimerTask {

            /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$6$1 */
            class C15011 implements Runnable {
                C15011() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= 1000) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C15026() {
            }

            public void run() {
                double currentTimeMillis = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTimeMillis - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C15011());
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$7 */
        class C15057 extends TimerTask {

            /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$7$1 */
            class C15041 implements Runnable {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$7$1$1 */
                class C21911 implements RequestDelegate {
                    C21911() {
                    }

                    public void run(TLObject tLObject, final TL_error tL_error) {
                        if (tL_error != null && tL_error.text != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    LoginActivitySmsView.this.lastError = tL_error.text;
                                }
                            });
                        }
                    }
                }

                C15041() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= 1000) {
                        int access$5000 = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType != 4) {
                            if (LoginActivitySmsView.this.nextType != 3) {
                                if (LoginActivitySmsView.this.nextType == 2) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0446R.string.SmsText, Integer.valueOf(r0), Integer.valueOf(access$5000)));
                                }
                                if (LoginActivitySmsView.this.progressView != null) {
                                    LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                    return;
                                }
                                return;
                            }
                        }
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0446R.string.CallText, Integer.valueOf(r0), Integer.valueOf(access$5000)));
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
                    } else if (LoginActivitySmsView.this.currentType != 2) {
                    } else {
                        if (LoginActivitySmsView.this.nextType == 4) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", C0446R.string.Calling));
                            LoginActivitySmsView.this.createCodeTimer();
                            TLObject tL_auth_resendCode = new TL_auth_resendCode();
                            tL_auth_resendCode.phone_number = LoginActivitySmsView.this.requestPhone;
                            tL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new C21911(), 10);
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

            C15057() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTimeMillis = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTimeMillis - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                    AndroidUtilities.runOnUIThread(new C15041());
                }
            }
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$9 */
        class C15089 implements Runnable {
            C15089() {
            }

            public void run() {
                if (LoginActivitySmsView.this.codeField != null) {
                    LoginActivitySmsView.this.codeField.requestFocus();
                    LoginActivitySmsView.this.codeField.setSelection(LoginActivitySmsView.this.codeField.length());
                }
            }
        }

        public LoginActivitySmsView(LoginActivity loginActivity, Context context, int i) {
            View frameLayout;
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
            super(context2);
            this.currentType = i;
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            int i2 = 5;
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            if (r0.currentType == 3) {
                frameLayout = new FrameLayout(context2);
                View imageView = new ImageView(context2);
                imageView.setImageResource(C0446R.drawable.phone_activate);
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
            r0.codeField.setHint(LocaleController.getString("Code", C0446R.string.Code));
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (LoginActivitySmsView.this.ignoreOnTextChange == null && LoginActivitySmsView.this.length != null && LoginActivitySmsView.this.codeField.length() == LoginActivitySmsView.this.length) {
                        LoginActivitySmsView.this.onNextPressed();
                    }
                }
            });
            r0.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return null;
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
            r0.problemText.setText(LocaleController.getString("DidNotGetTheCode", C0446R.string.DidNotGetTheCode));
            r0.problemText.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.problemText.setTextSize(1, 14.0f);
            r0.problemText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(12.0f));
            addView(r0.problemText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 20, 0, 0));
            r0.problemText.setOnClickListener(new OnClickListener() {
                public void onClick(android.view.View r6) {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r5 = this;
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;
                    r6 = r6.nextPressed;
                    if (r6 == 0) goto L_0x0009;
                L_0x0008:
                    return;
                L_0x0009:
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;
                    r6 = r6.nextType;
                    if (r6 == 0) goto L_0x0021;
                L_0x0011:
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;
                    r6 = r6.nextType;
                    r0 = 4;
                    if (r6 == r0) goto L_0x0021;
                L_0x001a:
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;
                    r6.resendCode();
                    goto L_0x0105;
                L_0x0021:
                    r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.getPackageManager();	 Catch:{ Exception -> 0x00ec }
                    r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00ec }
                    r0 = r0.getPackageName();	 Catch:{ Exception -> 0x00ec }
                    r1 = 0;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.getPackageInfo(r0, r1);	 Catch:{ Exception -> 0x00ec }
                    r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x00ec }
                    r2 = "%s (%d)";	 Catch:{ Exception -> 0x00ec }
                    r3 = 2;	 Catch:{ Exception -> 0x00ec }
                    r3 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x00ec }
                    r4 = r6.versionName;	 Catch:{ Exception -> 0x00ec }
                    r3[r1] = r4;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.versionCode;	 Catch:{ Exception -> 0x00ec }
                    r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00ec }
                    r4 = 1;	 Catch:{ Exception -> 0x00ec }
                    r3[r4] = r6;	 Catch:{ Exception -> 0x00ec }
                    r6 = java.lang.String.format(r0, r2, r3);	 Catch:{ Exception -> 0x00ec }
                    r0 = new android.content.Intent;	 Catch:{ Exception -> 0x00ec }
                    r2 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x00ec }
                    r0.<init>(r2);	 Catch:{ Exception -> 0x00ec }
                    r2 = "message/rfc822";	 Catch:{ Exception -> 0x00ec }
                    r0.setType(r2);	 Catch:{ Exception -> 0x00ec }
                    r2 = "android.intent.extra.EMAIL";	 Catch:{ Exception -> 0x00ec }
                    r3 = new java.lang.String[r4];	 Catch:{ Exception -> 0x00ec }
                    r4 = "sms@stel.com";	 Catch:{ Exception -> 0x00ec }
                    r3[r1] = r4;	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r2, r3);	 Catch:{ Exception -> 0x00ec }
                    r1 = "android.intent.extra.SUBJECT";	 Catch:{ Exception -> 0x00ec }
                    r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00ec }
                    r2.<init>();	 Catch:{ Exception -> 0x00ec }
                    r3 = "Android registration/login issue ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r3 = " ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r3 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r3 = r3.emailPhone;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r2 = r2.toString();	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x00ec }
                    r1 = "android.intent.extra.TEXT";	 Catch:{ Exception -> 0x00ec }
                    r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00ec }
                    r2.<init>();	 Catch:{ Exception -> 0x00ec }
                    r3 = "Phone: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r3 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r3 = r3.requestPhone;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r3 = "\nApp version: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = "\nOS version: SDK ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = "\nDevice Name: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = "\nLocale: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = java.util.Locale.getDefault();	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = "\nError: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.lastError;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = r2.toString();	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r1, r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.getContext();	 Catch:{ Exception -> 0x00ec }
                    r1 = "Send email...";	 Catch:{ Exception -> 0x00ec }
                    r0 = android.content.Intent.createChooser(r0, r1);	 Catch:{ Exception -> 0x00ec }
                    r6.startActivity(r0);	 Catch:{ Exception -> 0x00ec }
                    goto L_0x0105;
                L_0x00ec:
                    r6 = org.telegram.ui.LoginActivity.LoginActivitySmsView.this;
                    r6 = r6.this$0;
                    r0 = "AppName";
                    r1 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
                    r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                    r1 = "NoMailInstalled";
                    r2 = NUM; // 0x7f0c0402 float:1.8611273E38 double:1.0530979054E-314;
                    r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
                    r6.needShowAlert(r0, r1);
                L_0x0105:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.LoginActivitySmsView.3.onClick(android.view.View):void");
                }
            });
            frameLayout = new LinearLayout(context2);
            frameLayout.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(frameLayout, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3));
            r0.wrongNumber = new TextView(context2);
            r0.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            r0.wrongNumber.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            r0.wrongNumber.setTextSize(1, 14.0f);
            r0.wrongNumber.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            r0.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            View view = r0.wrongNumber;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            frameLayout.addView(view, LayoutHelper.createLinear(-2, -2, 80 | i2, 0, 0, 0, 10));
            r0.wrongNumber.setText(LocaleController.getString("WrongNumber", C0446R.string.WrongNumber));
            r0.wrongNumber.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$4$1 */
                class C21891 implements RequestDelegate {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }

                    C21891() {
                    }
                }

                public void onClick(View view) {
                    view = new TL_auth_cancelCode();
                    view.phone_number = LoginActivitySmsView.this.requestPhone;
                    view.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(view, new C21891(), 10);
                    LoginActivitySmsView.this.onBackPressed();
                    LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                }
            });
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLObject tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.requestPhone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LoginActivitySmsView.this.nextPressed = false;
                            if (tL_error == null) {
                                LoginActivitySmsView.this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
                            } else if (tL_error.text != null) {
                                if (tL_error.text.contains("PHONE_NUMBER_INVALID")) {
                                    LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                                } else {
                                    if (!tL_error.text.contains("PHONE_CODE_EMPTY")) {
                                        if (!tL_error.text.contains("PHONE_CODE_INVALID")) {
                                            if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                                                LoginActivitySmsView.this.onBackPressed();
                                                LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                            } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                            } else if (tL_error.code != C0542C.PRIORITY_DOWNLOAD) {
                                                LoginActivity loginActivity = LoginActivitySmsView.this.this$0;
                                                String string = LocaleController.getString("AppName", C0446R.string.AppName);
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                                stringBuilder.append("\n");
                                                stringBuilder.append(tL_error.text);
                                                loginActivity.needShowAlert(string, stringBuilder.toString());
                                            }
                                        }
                                    }
                                    LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
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
            return LocaleController.getString("YourCode", C0446R.string.YourCode);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                this.isRestored = z;
                this.codeField.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.waitingForEvent = true;
                if (this.currentType == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (this.currentType == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.emailPhone = bundle.getString("ephone");
                this.requestPhone = bundle.getString("phoneFormated");
                this.phoneHash = bundle.getString("phoneHash");
                int i = bundle.getInt("timeout");
                this.time = i;
                this.timeout = i;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                this.length = bundle.getInt("length");
                i = 0;
                if (this.length != null) {
                    this.codeField.setFilters(new InputFilter[]{new LengthFilter(this.length)});
                } else {
                    this.codeField.setFilters(new InputFilter[0]);
                }
                if (this.progressView != null) {
                    this.progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    bundle = PhoneFormat.getInstance().format(this.phone);
                    CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                    if (this.currentType == 1) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", C0446R.string.SentAppCode));
                    } else if (this.currentType == 2) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", C0446R.string.SentSmsCode, LocaleController.addNbsp(bundle)));
                    } else if (this.currentType == 3) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", C0446R.string.SentCallCode, LocaleController.addNbsp(bundle)));
                    } else if (this.currentType == 4) {
                        charSequence = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", C0446R.string.SentCallOnly, LocaleController.addNbsp(bundle)));
                    }
                    this.confirmTextView.setText(charSequence);
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
                            this.timeText.setText(LocaleController.formatString("CallText", C0446R.string.CallText, Integer.valueOf(1), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", C0446R.string.SmsText, Integer.valueOf(1), Integer.valueOf(0)));
                        }
                        bundle = this.isRestored != null ? AndroidUtilities.obtainLoginPhoneCall(this.pattern) : null;
                        if (bundle != null) {
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(bundle);
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
                        this.timeText.setText(LocaleController.formatString("CallText", C0446R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        bundle = this.problemText;
                        if (this.time >= true) {
                            i = 8;
                        }
                        bundle.setVisibility(i);
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
                this.codeTimer.schedule(new C15026(), 0, 1000);
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
                this.timeTimer.schedule(new C15057(), 0, 1000);
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
                final String obj = this.codeField.getText().toString();
                final TLObject tL_auth_signIn = new TL_auth_signIn();
                tL_auth_signIn.phone_number = this.requestPhone;
                tL_auth_signIn.phone_code = obj;
                tL_auth_signIn.phone_code_hash = this.phoneHash;
                destroyTimer();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_signIn, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$8$1$1 */
                            class C21921 implements RequestDelegate {
                                C21921() {
                                }

                                public void run(final TLObject tLObject, final TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            LoginActivitySmsView.this.this$0.needHideProgress();
                                            if (tL_error == null) {
                                                TL_account_password tL_account_password = (TL_account_password) tLObject;
                                                Bundle bundle = new Bundle();
                                                bundle.putString("current_salt", Utilities.bytesToHex(tL_account_password.current_salt));
                                                bundle.putString(TrackReferenceTypeBox.TYPE1, tL_account_password.hint);
                                                bundle.putString("email_unconfirmed_pattern", tL_account_password.email_unconfirmed_pattern);
                                                bundle.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                                bundle.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                                                bundle.putString("code", tL_auth_signIn.phone_code);
                                                bundle.putInt("has_recovery", tL_account_password.has_recovery);
                                                LoginActivitySmsView.this.this$0.setPage(6, true, bundle, false);
                                                return;
                                            }
                                            LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                        }
                                    });
                                }
                            }

                            public void run() {
                                boolean z = false;
                                LoginActivitySmsView.this.nextPressed = false;
                                if (tL_error == null) {
                                    LoginActivitySmsView.this.this$0.needHideProgress();
                                    TL_auth_authorization tL_auth_authorization = (TL_auth_authorization) tLObject;
                                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).setUserId(tL_auth_authorization.user.id);
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).clearConfig();
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).cleanup();
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).syncContacts = LoginActivitySmsView.this.this$0.syncContacts;
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).setCurrentUser(tL_auth_authorization.user);
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).saveConfig(true);
                                    MessagesStorage.getInstance(LoginActivitySmsView.this.this$0.currentAccount).cleanup(true);
                                    ArrayList arrayList = new ArrayList();
                                    arrayList.add(tL_auth_authorization.user);
                                    MessagesStorage.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUsersAndChats(arrayList, null, true, true);
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUser(tL_auth_authorization.user, false);
                                    ContactsController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).checkAppAccount();
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).getBlockedUsers(true);
                                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).updateDcSettings();
                                    LoginActivitySmsView.this.this$0.needFinishActivity();
                                } else {
                                    LoginActivitySmsView.this.lastError = tL_error.text;
                                    if (tL_error.text.contains("PHONE_NUMBER_UNOCCUPIED")) {
                                        LoginActivitySmsView.this.this$0.needHideProgress();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("phoneFormated", LoginActivitySmsView.this.requestPhone);
                                        bundle.putString("phoneHash", LoginActivitySmsView.this.phoneHash);
                                        bundle.putString("code", tL_auth_signIn.phone_code);
                                        LoginActivitySmsView.this.this$0.setPage(5, true, bundle, false);
                                        LoginActivitySmsView.this.destroyTimer();
                                        LoginActivitySmsView.this.destroyCodeTimer();
                                    } else if (tL_error.text.contains("SESSION_PASSWORD_NEEDED")) {
                                        ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(new TL_account_getPassword(), new C21921(), 10);
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
                                            if (tL_error.text.contains("PHONE_NUMBER_INVALID")) {
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                                            } else {
                                                if (!tL_error.text.contains("PHONE_CODE_EMPTY")) {
                                                    if (!tL_error.text.contains("PHONE_CODE_INVALID")) {
                                                        if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                                                            LoginActivitySmsView.this.onBackPressed();
                                                            LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                                                            LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                                        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                                                            LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                                        } else {
                                                            LoginActivity loginActivity = LoginActivitySmsView.this.this$0;
                                                            String string = LocaleController.getString("AppName", C0446R.string.AppName);
                                                            StringBuilder stringBuilder = new StringBuilder();
                                                            stringBuilder.append(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                                            stringBuilder.append("\n");
                                                            stringBuilder.append(tL_error.text);
                                                            loginActivity.needShowAlert(string, stringBuilder.toString());
                                                        }
                                                    }
                                                }
                                                LoginActivitySmsView.this.this$0.needShowAlert(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
                                            }
                                        }
                                        if (z && LoginActivitySmsView.this.currentType == 3) {
                                            AndroidUtilities.endIncomingCall();
                                            AndroidUtilities.removeLoginPhoneCall(obj, true);
                                            return;
                                        }
                                    }
                                }
                                z = true;
                                if (z) {
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
                AndroidUtilities.runOnUIThread(new C15089(), 100);
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (this.waitingForEvent != 0) {
                if (this.codeField != 0) {
                    if (i == NotificationCenter.didReceiveSmsCode) {
                        this.ignoreOnTextChange = true;
                        i = this.codeField;
                        i2 = new StringBuilder();
                        i2.append(TtmlNode.ANONYMOUS_REGION_ID);
                        i2.append(objArr[0]);
                        i.setText(i2.toString());
                        this.ignoreOnTextChange = false;
                        onNextPressed();
                    } else if (i == NotificationCenter.didReceiveCall) {
                        i = new StringBuilder();
                        i.append(TtmlNode.ANONYMOUS_REGION_ID);
                        i.append(objArr[0]);
                        i = i.toString();
                        if (AndroidUtilities.checkPhonePattern(this.pattern, i) != 0) {
                            if (this.pattern.equals("*") == 0) {
                                this.catchedPhone = i;
                                AndroidUtilities.endIncomingCall();
                                AndroidUtilities.removeLoginPhoneCall(i, true);
                            }
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(i);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        }
                    }
                }
            }
        }

        public void saveStateParams(Bundle bundle) {
            String obj = this.codeField.getText().toString();
            if (obj.length() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("smsview_code_");
                stringBuilder.append(this.currentType);
                bundle.putString(stringBuilder.toString(), obj);
            }
            if (this.catchedPhone != null) {
                bundle.putString("catchedPhone", this.catchedPhone);
            }
            if (this.currentParams != null) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("smsview_params_");
                stringBuilder2.append(this.currentType);
                bundle.putBundle(stringBuilder2.toString(), this.currentParams);
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
            String string = bundle.getString("catchedPhone");
            if (string != null) {
                this.catchedPhone = string;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("smsview_code_");
            stringBuilder.append(this.currentType);
            CharSequence string2 = bundle.getString(stringBuilder.toString());
            if (string2 != null) {
                this.codeField.setText(string2);
            }
            int i = bundle.getInt("time");
            if (i != 0) {
                this.time = i;
            }
            bundle = bundle.getInt("open");
            if (bundle != null) {
                this.openTime = bundle;
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
            Object toUpperCase;
            String str;
            final LoginActivity loginActivity2 = loginActivity;
            Context context2 = context;
            this.this$0 = loginActivity2;
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
            r1.countryButton.setBackgroundResource(C0446R.drawable.spinner_states);
            addView(r1.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            r1.countryButton.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.LoginActivity$PhoneView$1$1 */
                class C21941 implements CountrySelectActivityDelegate {

                    /* renamed from: org.telegram.ui.LoginActivity$PhoneView$1$1$1 */
                    class C15091 implements Runnable {
                        C15091() {
                        }

                        public void run() {
                            AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                        }
                    }

                    C21941() {
                    }

                    public void didSelectCountry(String str, String str2) {
                        PhoneView.this.selectCountry(str);
                        AndroidUtilities.runOnUIThread(new C15091(), 300);
                        PhoneView.this.phoneField.requestFocus();
                        PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                    }
                }

                public void onClick(View view) {
                    view = new CountrySelectActivity(true);
                    view.setCountrySelectActivityDelegate(new C21941());
                    PhoneView.this.this$0.presentFragment(view);
                }
            });
            r1.view = new View(context2);
            r1.view.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            r1.view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
            addView(r1.view, LayoutHelper.createLinear(-1, 1, 4.0f, -17.5f, 4.0f, 0.0f));
            View linearLayout = new LinearLayout(context2);
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

                public void afterTextChanged(Editable editable) {
                    if (PhoneView.this.ignoreOnTextChange == null) {
                        PhoneView.this.ignoreOnTextChange = true;
                        editable = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                        PhoneView.this.codeField.setText(editable);
                        String str = null;
                        if (editable.length() == 0) {
                            PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 1;
                        } else {
                            Object substring;
                            CharSequence charSequence;
                            int i = 4;
                            if (editable.length() > 4) {
                                while (i >= 1) {
                                    substring = editable.substring(0, i);
                                    if (((String) PhoneView.this.codesMap.get(substring)) != null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(editable.substring(i, editable.length()));
                                        stringBuilder.append(PhoneView.this.phoneField.getText().toString());
                                        editable = stringBuilder.toString();
                                        PhoneView.this.codeField.setText(substring);
                                        charSequence = editable;
                                        editable = 1;
                                        break;
                                    }
                                    i--;
                                }
                                substring = editable;
                                editable = null;
                                charSequence = null;
                                if (editable == null) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(substring.substring(1, substring.length()));
                                    stringBuilder2.append(PhoneView.this.phoneField.getText().toString());
                                    charSequence = stringBuilder2.toString();
                                    EditTextBoldCursor access$1000 = PhoneView.this.codeField;
                                    substring = substring.substring(0, 1);
                                    access$1000.setText(substring);
                                }
                            } else {
                                substring = editable;
                                editable = null;
                                charSequence = null;
                            }
                            String str2 = (String) PhoneView.this.codesMap.get(substring);
                            if (str2 != null) {
                                int indexOf = PhoneView.this.countriesArray.indexOf(str2);
                                if (indexOf != -1) {
                                    PhoneView.this.ignoreSelection = true;
                                    PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(indexOf));
                                    String str3 = (String) PhoneView.this.phoneFormatMap.get(substring);
                                    HintEditText access$800 = PhoneView.this.phoneField;
                                    if (str3 != null) {
                                        str = str3.replace('X', '\u2013');
                                    }
                                    access$800.setHintText(str);
                                    PhoneView.this.countryState = 0;
                                } else {
                                    PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", C0446R.string.WrongCountry));
                                    PhoneView.this.phoneField.setHintText(null);
                                    PhoneView.this.countryState = 2;
                                }
                            } else {
                                PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", C0446R.string.WrongCountry));
                                PhoneView.this.phoneField.setHintText(null);
                                PhoneView.this.countryState = 2;
                            }
                            if (editable == null) {
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
            r1.codeField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return null;
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

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (i2 == 0 && i3 == 1) {
                        this.characterAction = 1;
                    } else if (i2 != 1 || i3 != 0) {
                        this.characterAction = -1;
                    } else if (charSequence.charAt(i) != 32 || i <= 0) {
                        this.characterAction = 2;
                    } else {
                        this.characterAction = 3;
                        this.actionPosition = i - 1;
                    }
                }

                public void afterTextChanged(Editable editable) {
                    if (PhoneView.this.ignoreOnPhoneChange == null) {
                        editable = PhoneView.this.phoneField.getSelectionStart();
                        String str = "0123456789";
                        String obj = PhoneView.this.phoneField.getText().toString();
                        if (this.characterAction == 3) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(obj.substring(0, this.actionPosition));
                            stringBuilder.append(obj.substring(this.actionPosition + 1, obj.length()));
                            obj = stringBuilder.toString();
                            editable--;
                        }
                        CharSequence stringBuilder2 = new StringBuilder(obj.length());
                        int i = 0;
                        while (i < obj.length()) {
                            int i2 = i + 1;
                            Object substring = obj.substring(i, i2);
                            if (str.contains(substring)) {
                                stringBuilder2.append(substring);
                            }
                            i = i2;
                        }
                        PhoneView.this.ignoreOnPhoneChange = true;
                        str = PhoneView.this.phoneField.getHintText();
                        if (str != null) {
                            int i3 = editable;
                            editable = null;
                            while (editable < stringBuilder2.length()) {
                                if (editable < str.length()) {
                                    if (str.charAt(editable) == ' ') {
                                        stringBuilder2.insert(editable, ' ');
                                        editable++;
                                        if (!(i3 != editable || this.characterAction == 2 || this.characterAction == 3)) {
                                            i3++;
                                        }
                                    }
                                    editable += 1;
                                } else {
                                    stringBuilder2.insert(editable, ' ');
                                    if (!(i3 != editable + 1 || this.characterAction == 2 || this.characterAction == 3)) {
                                        editable = i3 + 1;
                                    }
                                    editable = i3;
                                }
                            }
                            editable = i3;
                        }
                        PhoneView.this.phoneField.setText(stringBuilder2);
                        if (editable >= null) {
                            HintEditText access$800 = PhoneView.this.phoneField;
                            if (editable > PhoneView.this.phoneField.length()) {
                                editable = PhoneView.this.phoneField.length();
                            }
                            access$800.setSelection(editable);
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            r1.phoneField.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 5) {
                        return null;
                    }
                    PhoneView.this.onNextPressed();
                    return true;
                }
            });
            r1.textView2 = new TextView(context2);
            r1.textView2.setText(LocaleController.getString("StartText", C0446R.string.StartText));
            r1.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r1.textView2.setTextSize(1, 14.0f);
            r1.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            r1.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r1.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
            if (loginActivity.newAccount) {
                r1.checkBoxCell = new CheckBoxCell(context2, 2);
                r1.checkBoxCell.setText(LocaleController.getString("SyncContacts", C0446R.string.SyncContacts), TtmlNode.ANONYMOUS_REGION_ID, loginActivity.syncContacts, false);
                addView(r1.checkBoxCell, LayoutHelper.createLinear(-2, -1, 51, 0, 0, 0, 0));
                r1.checkBoxCell.setOnClickListener(new OnClickListener() {
                    private Toast visibleToast;

                    public void onClick(View view) {
                        if (PhoneView.this.this$0.getParentActivity() != null) {
                            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                            PhoneView.this.this$0.syncContacts = PhoneView.this.this$0.syncContacts ^ true;
                            checkBoxCell.setChecked(PhoneView.this.this$0.syncContacts, true);
                            try {
                                if (this.visibleToast != null) {
                                    this.visibleToast.cancel();
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            if (PhoneView.this.this$0.syncContacts != null) {
                                this.visibleToast = Toast.makeText(PhoneView.this.this$0.getParentActivity(), LocaleController.getString("SyncContactsOn", C0446R.string.SyncContactsOn), 0);
                                this.visibleToast.show();
                            } else {
                                this.visibleToast = Toast.makeText(PhoneView.this.this$0.getParentActivity(), LocaleController.getString("SyncContactsOff", C0446R.string.SyncContactsOff), 0);
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
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    String[] split = readLine.split(";");
                    r1.countriesArray.add(0, split[2]);
                    r1.countriesMap.put(split[2], split[0]);
                    r1.codesMap.put(split[0], split[2]);
                    if (split.length > 3) {
                        r1.phoneFormatMap.put(split[0], split[3]);
                    }
                    hashMap.put(split[1], split[2]);
                }
                bufferedReader.close();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            Collections.sort(r1.countriesArray, new Comparator<String>() {
                public int compare(String str, String str2) {
                    return str.compareTo(str2);
                }
            });
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    toUpperCase = telephonyManager.getSimCountryIso().toUpperCase();
                    if (toUpperCase != null) {
                        str = (String) hashMap.get(toUpperCase);
                        if (!(str == null || r1.countriesArray.indexOf(str) == -1)) {
                            r1.codeField.setText((CharSequence) r1.countriesMap.get(str));
                            r1.countryState = 0;
                        }
                    }
                    if (r1.codeField.length() == 0) {
                        r1.countryButton.setText(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
                        r1.phoneField.setHintText(null);
                        r1.countryState = 1;
                    }
                    if (r1.codeField.length() == 0) {
                        r1.phoneField.requestFocus();
                        r1.phoneField.setSelection(r1.phoneField.length());
                    }
                    r1.codeField.requestFocus();
                    return;
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            toUpperCase = null;
            if (toUpperCase != null) {
                str = (String) hashMap.get(toUpperCase);
                r1.codeField.setText((CharSequence) r1.countriesMap.get(str));
                r1.countryState = 0;
            }
            if (r1.codeField.length() == 0) {
                r1.countryButton.setText(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
                r1.phoneField.setHintText(null);
                r1.countryState = 1;
            }
            if (r1.codeField.length() == 0) {
                r1.codeField.requestFocus();
                return;
            }
            r1.phoneField.requestFocus();
            r1.phoneField.setSelection(r1.phoneField.length());
        }

        public void selectCountry(String str) {
            if (this.countriesArray.indexOf(str) != -1) {
                this.ignoreOnTextChange = true;
                String str2 = (String) this.countriesMap.get(str);
                this.codeField.setText(str2);
                this.countryButton.setText(str);
                str = (String) this.phoneFormatMap.get(str2);
                this.phoneField.setHintText(str != null ? str.replace('X', '\u2013') : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection != null) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onNextPressed() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r14 = this;
            r0 = r14.this$0;
            r0 = r0.getParentActivity();
            if (r0 == 0) goto L_0x03ad;
        L_0x0008:
            r0 = r14.nextPressed;
            if (r0 == 0) goto L_0x000e;
        L_0x000c:
            goto L_0x03ad;
        L_0x000e:
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r1 = "phone";
            r0 = r0.getSystemService(r1);
            r0 = (android.telephony.TelephonyManager) r0;
            r1 = r0.getSimState();
            r2 = 0;
            r3 = 1;
            if (r1 == r3) goto L_0x0028;
        L_0x0020:
            r1 = r0.getPhoneType();
            if (r1 == 0) goto L_0x0028;
        L_0x0026:
            r1 = r3;
            goto L_0x0029;
        L_0x0028:
            r1 = r2;
        L_0x0029:
            r4 = android.os.Build.VERSION.SDK_INT;
            r5 = 2;
            r6 = 0;
            r7 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
            r8 = 23;
            r9 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
            if (r4 < r8) goto L_0x01bb;
        L_0x0037:
            if (r1 == 0) goto L_0x01bb;
        L_0x0039:
            r4 = r14.this$0;
            r4 = r4.getParentActivity();
            r10 = "android.permission.READ_PHONE_STATE";
            r4 = r4.checkSelfPermission(r10);
            if (r4 != 0) goto L_0x0049;
        L_0x0047:
            r4 = r3;
            goto L_0x004a;
        L_0x0049:
            r4 = r2;
        L_0x004a:
            r10 = r14.this$0;
            r10 = r10.getParentActivity();
            r11 = "android.permission.RECEIVE_SMS";
            r10 = r10.checkSelfPermission(r11);
            if (r10 != 0) goto L_0x005a;
        L_0x0058:
            r10 = r3;
            goto L_0x005b;
        L_0x005a:
            r10 = r2;
        L_0x005b:
            r11 = r14.this$0;
            r11 = r11.getParentActivity();
            r12 = "android.permission.CALL_PHONE";
            r11 = r11.checkSelfPermission(r12);
            if (r11 != 0) goto L_0x006b;
        L_0x0069:
            r11 = r3;
            goto L_0x006c;
        L_0x006b:
            r11 = r2;
        L_0x006c:
            r12 = r14.this$0;
            r12 = r12.checkPermissions;
            if (r12 == 0) goto L_0x01bc;
        L_0x0074:
            r12 = r14.this$0;
            r12 = r12.permissionsItems;
            r12.clear();
            if (r4 != 0) goto L_0x008a;
        L_0x007f:
            r12 = r14.this$0;
            r12 = r12.permissionsItems;
            r13 = "android.permission.READ_PHONE_STATE";
            r12.add(r13);
        L_0x008a:
            if (r10 != 0) goto L_0x00a6;
        L_0x008c:
            r12 = r14.this$0;
            r12 = r12.permissionsItems;
            r13 = "android.permission.RECEIVE_SMS";
            r12.add(r13);
            r12 = android.os.Build.VERSION.SDK_INT;
            if (r12 < r8) goto L_0x00a6;
        L_0x009b:
            r8 = r14.this$0;
            r8 = r8.permissionsItems;
            r12 = "android.permission.READ_SMS";
            r8.add(r12);
        L_0x00a6:
            if (r11 != 0) goto L_0x00c9;
        L_0x00a8:
            r8 = r14.this$0;
            r8 = r8.permissionsItems;
            r12 = "android.permission.CALL_PHONE";
            r8.add(r12);
            r8 = r14.this$0;
            r8 = r8.permissionsItems;
            r12 = "android.permission.WRITE_CALL_LOG";
            r8.add(r12);
            r8 = r14.this$0;
            r8 = r8.permissionsItems;
            r12 = "android.permission.READ_CALL_LOG";
            r8.add(r12);
        L_0x00c9:
            r8 = r14.this$0;
            r8 = r8.permissionsItems;
            r8 = r8.isEmpty();
            if (r8 != 0) goto L_0x01bc;
        L_0x00d5:
            r8 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
            r12 = 6;
            if (r11 != 0) goto L_0x0101;
        L_0x00dc:
            if (r4 == 0) goto L_0x0101;
        L_0x00de:
            r8 = r14.this$0;
            r8 = r8.getParentActivity();
            r10 = r14.this$0;
            r10 = r10.permissionsItems;
            r11 = r14.this$0;
            r11 = r11.permissionsItems;
            r11 = r11.size();
            r11 = new java.lang.String[r11];
            r10 = r10.toArray(r11);
            r10 = (java.lang.String[]) r10;
            r8.requestPermissions(r10, r12);
            goto L_0x01b7;
        L_0x0101:
            r11 = "firstlogin";
            r11 = r8.getBoolean(r11, r3);
            if (r11 != 0) goto L_0x014a;
        L_0x0109:
            r11 = r14.this$0;
            r11 = r11.getParentActivity();
            r13 = "android.permission.READ_PHONE_STATE";
            r11 = r11.shouldShowRequestPermissionRationale(r13);
            if (r11 != 0) goto L_0x014a;
        L_0x0117:
            r11 = r14.this$0;
            r11 = r11.getParentActivity();
            r13 = "android.permission.RECEIVE_SMS";
            r11 = r11.shouldShowRequestPermissionRationale(r13);
            if (r11 == 0) goto L_0x0126;
        L_0x0125:
            goto L_0x014a;
        L_0x0126:
            r8 = r14.this$0;	 Catch:{ Exception -> 0x0148 }
            r8 = r8.getParentActivity();	 Catch:{ Exception -> 0x0148 }
            r10 = r14.this$0;	 Catch:{ Exception -> 0x0148 }
            r10 = r10.permissionsItems;	 Catch:{ Exception -> 0x0148 }
            r11 = r14.this$0;	 Catch:{ Exception -> 0x0148 }
            r11 = r11.permissionsItems;	 Catch:{ Exception -> 0x0148 }
            r11 = r11.size();	 Catch:{ Exception -> 0x0148 }
            r11 = new java.lang.String[r11];	 Catch:{ Exception -> 0x0148 }
            r10 = r10.toArray(r11);	 Catch:{ Exception -> 0x0148 }
            r10 = (java.lang.String[]) r10;	 Catch:{ Exception -> 0x0148 }
            r8.requestPermissions(r10, r12);	 Catch:{ Exception -> 0x0148 }
            goto L_0x01b7;
        L_0x0148:
            r8 = r2;
            goto L_0x01b8;
        L_0x014a:
            r8 = r8.edit();
            r11 = "firstlogin";
            r8 = r8.putBoolean(r11, r2);
            r8.commit();
            r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
            r11 = r14.this$0;
            r11 = r11.getParentActivity();
            r8.<init>(r11);
            r11 = "AppName";
            r11 = org.telegram.messenger.LocaleController.getString(r11, r9);
            r8.setTitle(r11);
            r11 = "OK";
            r11 = org.telegram.messenger.LocaleController.getString(r11, r7);
            r8.setPositiveButton(r11, r6);
            r11 = r14.this$0;
            r11 = r11.permissionsItems;
            r11 = r11.size();
            if (r11 < r5) goto L_0x018d;
        L_0x0180:
            r10 = "AllowReadCallAndSms";
            r11 = NUM; // 0x7f0c005a float:1.8609374E38 double:1.053097443E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
            r8.setMessage(r10);
            goto L_0x01a8;
        L_0x018d:
            if (r10 != 0) goto L_0x019c;
        L_0x018f:
            r10 = "AllowReadSms";
            r11 = NUM; // 0x7f0c005b float:1.8609377E38 double:1.0530974434E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
            r8.setMessage(r10);
            goto L_0x01a8;
        L_0x019c:
            r10 = "AllowReadCall";
            r11 = NUM; // 0x7f0c0059 float:1.8609372E38 double:1.0530974424E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r11);
            r8.setMessage(r10);
        L_0x01a8:
            r10 = r14.this$0;
            r11 = r14.this$0;
            r8 = r8.create();
            r8 = r11.showDialog(r8);
            r10.permissionsDialog = r8;
        L_0x01b7:
            r8 = r3;
        L_0x01b8:
            if (r8 == 0) goto L_0x01bc;
        L_0x01ba:
            return;
        L_0x01bb:
            r4 = r3;
        L_0x01bc:
            r8 = r14.countryState;
            if (r8 != r3) goto L_0x01d5;
        L_0x01c0:
            r0 = r14.this$0;
            r1 = "AppName";
            r1 = org.telegram.messenger.LocaleController.getString(r1, r9);
            r2 = "ChooseCountry";
            r3 = NUM; // 0x7f0c017e float:1.8609967E38 double:1.053097587E-314;
            r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
            r0.needShowAlert(r1, r2);
            return;
        L_0x01d5:
            r8 = r14.countryState;
            if (r8 != r5) goto L_0x0204;
        L_0x01d9:
            r5 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
            if (r5 != 0) goto L_0x0204;
        L_0x01dd:
            r5 = r14.codeField;
            r5 = r5.getText();
            r5 = r5.toString();
            r8 = "999";
            r5 = r5.equals(r8);
            if (r5 != 0) goto L_0x0204;
        L_0x01ef:
            r0 = r14.this$0;
            r1 = "AppName";
            r1 = org.telegram.messenger.LocaleController.getString(r1, r9);
            r2 = "WrongCountry";
            r3 = NUM; // 0x7f0c06f4 float:1.8612802E38 double:1.053098278E-314;
            r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
            r0.needShowAlert(r1, r2);
            return;
        L_0x0204:
            r5 = r14.codeField;
            r5 = r5.length();
            if (r5 != 0) goto L_0x0221;
        L_0x020c:
            r0 = r14.this$0;
            r1 = "AppName";
            r1 = org.telegram.messenger.LocaleController.getString(r1, r9);
            r2 = "InvalidPhoneNumber";
            r3 = NUM; // 0x7f0c0332 float:1.8610851E38 double:1.0530978026E-314;
            r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
            r0.needShowAlert(r1, r2);
            return;
        L_0x0221:
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r8 = "";
            r5.append(r8);
            r8 = r14.codeField;
            r8 = r8.getText();
            r5.append(r8);
            r8 = r14.phoneField;
            r8 = r8.getText();
            r5.append(r8);
            r5 = r5.toString();
            r5 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r5);
            r8 = r2;
        L_0x0246:
            r10 = 3;
            if (r8 >= r10) goto L_0x02ae;
        L_0x0249:
            r10 = org.telegram.messenger.UserConfig.getInstance(r8);
            r11 = r10.isClientActivated();
            if (r11 != 0) goto L_0x0254;
        L_0x0253:
            goto L_0x0267;
        L_0x0254:
            r10 = r10.getCurrentUser();
            r10 = r10.phone;
            r11 = r10.contains(r5);
            if (r11 != 0) goto L_0x026a;
        L_0x0260:
            r10 = r5.contains(r10);
            if (r10 == 0) goto L_0x0267;
        L_0x0266:
            goto L_0x026a;
        L_0x0267:
            r8 = r8 + 1;
            goto L_0x0246;
        L_0x026a:
            r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
            r1 = r14.this$0;
            r1 = r1.getParentActivity();
            r0.<init>(r1);
            r1 = "AppName";
            r1 = org.telegram.messenger.LocaleController.getString(r1, r9);
            r0.setTitle(r1);
            r1 = "AccountAlreadyLoggedIn";
            r2 = NUM; // 0x7f0c0003 float:1.8609198E38 double:1.0530974E-314;
            r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
            r0.setMessage(r1);
            r1 = "AccountSwitch";
            r2 = NUM; // 0x7f0c0005 float:1.8609202E38 double:1.053097401E-314;
            r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
            r2 = new org.telegram.ui.LoginActivity$PhoneView$8;
            r2.<init>(r8);
            r0.setPositiveButton(r1, r2);
            r1 = "OK";
            r1 = org.telegram.messenger.LocaleController.getString(r1, r7);
            r0.setNegativeButton(r1, r6);
            r1 = r14.this$0;
            r0 = r0.create();
            r1.showDialog(r0);
            return;
        L_0x02ae:
            r6 = r14.this$0;
            r6 = r6.currentAccount;
            r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
            r6.cleanup();
            r6 = new org.telegram.tgnet.TLRPC$TL_auth_sendCode;
            r6.<init>();
            r7 = org.telegram.messenger.BuildVars.APP_HASH;
            r6.api_hash = r7;
            r7 = org.telegram.messenger.BuildVars.APP_ID;
            r6.api_id = r7;
            r6.phone_number = r5;
            if (r1 == 0) goto L_0x02d0;
        L_0x02cc:
            if (r4 == 0) goto L_0x02d0;
        L_0x02ce:
            r1 = r3;
            goto L_0x02d1;
        L_0x02d0:
            r1 = r2;
        L_0x02d1:
            r6.allow_flashcall = r1;
            r1 = r6.allow_flashcall;
            if (r1 == 0) goto L_0x030c;
        L_0x02d7:
            r0 = r0.getLine1Number();	 Catch:{ Exception -> 0x0306 }
            r1 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0306 }
            if (r1 != 0) goto L_0x02fa;	 Catch:{ Exception -> 0x0306 }
        L_0x02e1:
            r1 = r5.contains(r0);	 Catch:{ Exception -> 0x0306 }
            if (r1 != 0) goto L_0x02f0;	 Catch:{ Exception -> 0x0306 }
        L_0x02e7:
            r0 = r0.contains(r5);	 Catch:{ Exception -> 0x0306 }
            if (r0 == 0) goto L_0x02ee;	 Catch:{ Exception -> 0x0306 }
        L_0x02ed:
            goto L_0x02f0;	 Catch:{ Exception -> 0x0306 }
        L_0x02ee:
            r0 = r2;	 Catch:{ Exception -> 0x0306 }
            goto L_0x02f1;	 Catch:{ Exception -> 0x0306 }
        L_0x02f0:
            r0 = r3;	 Catch:{ Exception -> 0x0306 }
        L_0x02f1:
            r6.current_number = r0;	 Catch:{ Exception -> 0x0306 }
            r0 = r6.current_number;	 Catch:{ Exception -> 0x0306 }
            if (r0 != 0) goto L_0x030c;	 Catch:{ Exception -> 0x0306 }
        L_0x02f7:
            r6.allow_flashcall = r2;	 Catch:{ Exception -> 0x0306 }
            goto L_0x030c;	 Catch:{ Exception -> 0x0306 }
        L_0x02fa:
            r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0306 }
            if (r0 <= 0) goto L_0x0303;	 Catch:{ Exception -> 0x0306 }
        L_0x0300:
            r6.allow_flashcall = r2;	 Catch:{ Exception -> 0x0306 }
            goto L_0x030c;	 Catch:{ Exception -> 0x0306 }
        L_0x0303:
            r6.current_number = r2;	 Catch:{ Exception -> 0x0306 }
            goto L_0x030c;
        L_0x0306:
            r0 = move-exception;
            r6.allow_flashcall = r2;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x030c:
            r0 = new android.os.Bundle;
            r0.<init>();
            r1 = "phone";
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "+";
            r2.append(r4);
            r4 = r14.codeField;
            r4 = r4.getText();
            r2.append(r4);
            r4 = r14.phoneField;
            r4 = r4.getText();
            r2.append(r4);
            r2 = r2.toString();
            r0.putString(r1, r2);
            r1 = "ephone";	 Catch:{ Exception -> 0x0371 }
            r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0371 }
            r2.<init>();	 Catch:{ Exception -> 0x0371 }
            r4 = "+";	 Catch:{ Exception -> 0x0371 }
            r2.append(r4);	 Catch:{ Exception -> 0x0371 }
            r4 = r14.codeField;	 Catch:{ Exception -> 0x0371 }
            r4 = r4.getText();	 Catch:{ Exception -> 0x0371 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x0371 }
            r4 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4);	 Catch:{ Exception -> 0x0371 }
            r2.append(r4);	 Catch:{ Exception -> 0x0371 }
            r4 = " ";	 Catch:{ Exception -> 0x0371 }
            r2.append(r4);	 Catch:{ Exception -> 0x0371 }
            r4 = r14.phoneField;	 Catch:{ Exception -> 0x0371 }
            r4 = r4.getText();	 Catch:{ Exception -> 0x0371 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x0371 }
            r4 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r4);	 Catch:{ Exception -> 0x0371 }
            r2.append(r4);	 Catch:{ Exception -> 0x0371 }
            r2 = r2.toString();	 Catch:{ Exception -> 0x0371 }
            r0.putString(r1, r2);	 Catch:{ Exception -> 0x0371 }
            goto L_0x038b;
        L_0x0371:
            r1 = move-exception;
            org.telegram.messenger.FileLog.m3e(r1);
            r1 = "ephone";
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "+";
            r2.append(r4);
            r2.append(r5);
            r2 = r2.toString();
            r0.putString(r1, r2);
        L_0x038b:
            r1 = "phoneFormated";
            r0.putString(r1, r5);
            r14.nextPressed = r3;
            r1 = r14.this$0;
            r1 = r1.currentAccount;
            r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
            r2 = new org.telegram.ui.LoginActivity$PhoneView$9;
            r2.<init>(r0, r6);
            r0 = 27;
            r0 = r1.sendRequest(r6, r2, r0);
            r1 = r14.this$0;
            r1.needShowProgress(r0);
            return;
        L_0x03ad:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.PhoneView.onNextPressed():void");
        }

        public void fillNumber() {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (!(telephonyManager.getSimState() == 1 || telephonyManager.getPhoneType() == 0)) {
                    boolean z;
                    CharSequence charSequence = null;
                    boolean z2;
                    if (VERSION.SDK_INT >= 23) {
                        z = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                        z2 = this.this$0.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                        if (!(!this.this$0.checkShowPermissions || z || z2)) {
                            this.this$0.permissionsShowItems.clear();
                            if (!z) {
                                this.this$0.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!z2) {
                                this.this$0.permissionsShowItems.add("android.permission.RECEIVE_SMS");
                                if (VERSION.SDK_INT >= 23) {
                                    this.this$0.permissionsShowItems.add("android.permission.READ_SMS");
                                }
                            }
                            if (!this.this$0.permissionsShowItems.isEmpty()) {
                                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                                if (!(globalMainSettings.getBoolean("firstloginshow", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE"))) {
                                    if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                        this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsShowItems.toArray(new String[this.this$0.permissionsShowItems.size()]), 7);
                                    }
                                }
                                globalMainSettings.edit().putBoolean("firstloginshow", false).commit();
                                Builder builder = new Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                                builder.setMessage(LocaleController.getString("AllowFillNumber", C0446R.string.AllowFillNumber));
                                this.this$0.permissionsShowDialog = this.this$0.showDialog(builder.create());
                            }
                            return;
                        }
                    }
                    z = true;
                    z2 = z;
                    if (!this.this$0.newAccount && (r1 || r6)) {
                        String stripExceptNumbers = PhoneFormat.stripExceptNumbers(telephonyManager.getLine1Number());
                        if (!TextUtils.isEmpty(stripExceptNumbers)) {
                            int i = 4;
                            if (stripExceptNumbers.length() > 4) {
                                while (i >= 1) {
                                    CharSequence substring = stripExceptNumbers.substring(0, i);
                                    if (((String) this.codesMap.get(substring)) != null) {
                                        charSequence = stripExceptNumbers.substring(i, stripExceptNumbers.length());
                                        this.codeField.setText(substring);
                                        z = true;
                                        break;
                                    }
                                    i--;
                                }
                                z = false;
                                if (!z) {
                                    charSequence = stripExceptNumbers.substring(1, stripExceptNumbers.length());
                                    this.codeField.setText(stripExceptNumbers.substring(0, 1));
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
            return LocaleController.getString("YourPhone", C0446R.string.YourPhone);
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
            CharSequence string = bundle.getString("phoneview_code");
            if (string != null) {
                this.codeField.setText(string);
            }
            bundle = bundle.getString("phoneview_phone");
            if (bundle != null) {
                this.phoneField.setText(bundle);
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

    public LoginActivity(int i) {
        this.views = new SlideView[9];
        this.permissionsItems = new ArrayList();
        this.permissionsShowItems = new ArrayList();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.currentAccount = i;
        this.newAccount = true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        for (int i = 0; i < this.views.length; i++) {
            if (this.views[i] != null) {
                this.views[i].onDestroyActivity();
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
        int i;
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
        this.actionBar.setActionBarMenuOnItemClick(new C21821());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.actionBar.setAllowOverlayTitle(true);
        this.doneButton = createMenu.addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context2);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        View frameLayout = new FrameLayout(context2);
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
        int i2 = 0;
        while (i2 < r0.views.length) {
            r0.views[i2].setVisibility(i2 == 0 ? 0 : 8);
            View view = r0.views[i2];
            float f = i2 == 0 ? -2.0f : -1.0f;
            float f2 = 26.0f;
            float f3 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (!AndroidUtilities.isTablet()) {
                f2 = 18.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-1, f, 51, f3, 30.0f, f2, 0.0f));
            i2++;
        }
        Bundle loadCurrentState = loadCurrentState();
        if (loadCurrentState != null) {
            r0.currentViewNum = loadCurrentState.getInt("currentViewNum", 0);
            r0.syncContacts = loadCurrentState.getInt("syncContacts", 1) == 1;
            if (r0.currentViewNum >= 1 && r0.currentViewNum <= 4) {
                i = loadCurrentState.getInt("open");
                if (i != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400) {
                    r0.currentViewNum = 0;
                    loadCurrentState = null;
                    clearCurrentState();
                }
            }
        }
        r0.actionBar.setTitle(r0.views[r0.currentViewNum].getHeaderName());
        i = 0;
        while (i < r0.views.length) {
            if (loadCurrentState != null) {
                if (i < 1 || i > 4) {
                    r0.views[i].restoreStateParams(loadCurrentState);
                } else if (i == r0.currentViewNum) {
                    r0.views[i].restoreStateParams(loadCurrentState);
                }
            }
            if (r0.currentViewNum == i) {
                int i3;
                ActionBar actionBar = r0.actionBar;
                if (!r0.views[i].needBackButton()) {
                    if (!r0.newAccount) {
                        i3 = 0;
                        actionBar.setBackButtonImage(i3);
                        r0.views[i].setVisibility(0);
                        r0.views[i].onShow();
                        if (i != 3 || i == 8) {
                            r0.doneButton.setVisibility(8);
                        }
                    }
                }
                i3 = C0446R.drawable.ic_ab_back;
                actionBar.setBackButtonImage(i3);
                r0.views[i].setVisibility(0);
                r0.views[i].onShow();
                if (i != 3) {
                }
                r0.doneButton.setVisibility(8);
            } else {
                r0.views[i].setVisibility(8);
            }
            i++;
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
                int access$200 = ((LoginActivitySmsView) this.views[this.currentViewNum]).openTime;
                if (access$200 != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) access$200)) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed();
                    setPage(0, false, null, true);
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 6) {
            this.checkPermissions = false;
            if (this.currentViewNum == 0) {
                this.views[this.currentViewNum].onNextPressed();
            }
        } else if (i == 7) {
            this.checkShowPermissions = false;
            if (this.currentViewNum == 0) {
                ((PhoneView) this.views[this.currentViewNum]).fillNumber();
            }
        }
    }

    private Bundle loadCurrentState() {
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
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            StringBuilder stringBuilder;
            if (obj instanceof String) {
                if (str != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append("_|_");
                    stringBuilder.append(str2);
                    editor.putString(stringBuilder.toString(), (String) obj);
                } else {
                    editor.putString(str2, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (str != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append("_|_");
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

    protected void onDialogDismiss(android.app.Dialog r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 23;
        if (r0 < r1) goto L_0x005b;
    L_0x0006:
        r0 = r2.permissionsDialog;
        if (r3 != r0) goto L_0x0031;
    L_0x000a:
        r0 = r2.permissionsItems;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0031;
    L_0x0012:
        r0 = r2.getParentActivity();
        if (r0 == 0) goto L_0x0031;
    L_0x0018:
        r3 = r2.getParentActivity();	 Catch:{ Exception -> 0x005b }
        r0 = r2.permissionsItems;	 Catch:{ Exception -> 0x005b }
        r1 = r2.permissionsItems;	 Catch:{ Exception -> 0x005b }
        r1 = r1.size();	 Catch:{ Exception -> 0x005b }
        r1 = new java.lang.String[r1];	 Catch:{ Exception -> 0x005b }
        r0 = r0.toArray(r1);	 Catch:{ Exception -> 0x005b }
        r0 = (java.lang.String[]) r0;	 Catch:{ Exception -> 0x005b }
        r1 = 6;	 Catch:{ Exception -> 0x005b }
        r3.requestPermissions(r0, r1);	 Catch:{ Exception -> 0x005b }
        goto L_0x005b;
    L_0x0031:
        r0 = r2.permissionsShowDialog;
        if (r3 != r0) goto L_0x005b;
    L_0x0035:
        r3 = r2.permissionsShowItems;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x005b;
    L_0x003d:
        r3 = r2.getParentActivity();
        if (r3 == 0) goto L_0x005b;
    L_0x0043:
        r3 = r2.getParentActivity();	 Catch:{ Exception -> 0x005b }
        r0 = r2.permissionsShowItems;	 Catch:{ Exception -> 0x005b }
        r1 = r2.permissionsShowItems;	 Catch:{ Exception -> 0x005b }
        r1 = r1.size();	 Catch:{ Exception -> 0x005b }
        r1 = new java.lang.String[r1];	 Catch:{ Exception -> 0x005b }
        r0 = r0.toArray(r1);	 Catch:{ Exception -> 0x005b }
        r0 = (java.lang.String[]) r0;	 Catch:{ Exception -> 0x005b }
        r1 = 7;	 Catch:{ Exception -> 0x005b }
        r3.requestPermissions(r0, r1);	 Catch:{ Exception -> 0x005b }
    L_0x005b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.onDialogDismiss(android.app.Dialog):void");
    }

    public boolean onBackPressed() {
        int i = 0;
        if (this.currentViewNum == 0) {
            while (i < this.views.length) {
                if (this.views[i] != null) {
                    this.views[i].onDestroyActivity();
                }
                i++;
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

    private void needShowAlert(String str, String str2) {
        if (str2 != null) {
            if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(str);
                builder.setMessage(str2);
                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                showDialog(builder.create());
            }
        }
    }

    private void needShowInvalidAlert(final String str, final boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            if (z) {
                builder.setMessage(LocaleController.getString("BannedPhoneNumber", C0446R.string.BannedPhoneNumber));
            } else {
                builder.setMessage(LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
            }
            builder.setNeutralButton(LocaleController.getString("BotHelp", C0446R.string.BotHelp), new DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface r5, int r6) {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r4 = this;
                    r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x010f }
                    r5 = r5.getPackageManager();	 Catch:{ Exception -> 0x010f }
                    r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x010f }
                    r6 = r6.getPackageName();	 Catch:{ Exception -> 0x010f }
                    r0 = 0;	 Catch:{ Exception -> 0x010f }
                    r5 = r5.getPackageInfo(r6, r0);	 Catch:{ Exception -> 0x010f }
                    r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x010f }
                    r1 = "%s (%d)";	 Catch:{ Exception -> 0x010f }
                    r2 = 2;	 Catch:{ Exception -> 0x010f }
                    r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x010f }
                    r3 = r5.versionName;	 Catch:{ Exception -> 0x010f }
                    r2[r0] = r3;	 Catch:{ Exception -> 0x010f }
                    r5 = r5.versionCode;	 Catch:{ Exception -> 0x010f }
                    r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x010f }
                    r3 = 1;	 Catch:{ Exception -> 0x010f }
                    r2[r3] = r5;	 Catch:{ Exception -> 0x010f }
                    r5 = java.lang.String.format(r6, r1, r2);	 Catch:{ Exception -> 0x010f }
                    r6 = new android.content.Intent;	 Catch:{ Exception -> 0x010f }
                    r1 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x010f }
                    r6.<init>(r1);	 Catch:{ Exception -> 0x010f }
                    r1 = "message/rfc822";	 Catch:{ Exception -> 0x010f }
                    r6.setType(r1);	 Catch:{ Exception -> 0x010f }
                    r1 = "android.intent.extra.EMAIL";	 Catch:{ Exception -> 0x010f }
                    r2 = new java.lang.String[r3];	 Catch:{ Exception -> 0x010f }
                    r3 = "login@stel.com";	 Catch:{ Exception -> 0x010f }
                    r2[r0] = r3;	 Catch:{ Exception -> 0x010f }
                    r6.putExtra(r1, r2);	 Catch:{ Exception -> 0x010f }
                    r0 = r5;	 Catch:{ Exception -> 0x010f }
                    if (r0 == 0) goto L_0x00a2;	 Catch:{ Exception -> 0x010f }
                L_0x0044:
                    r0 = "android.intent.extra.SUBJECT";	 Catch:{ Exception -> 0x010f }
                    r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010f }
                    r1.<init>();	 Catch:{ Exception -> 0x010f }
                    r2 = "Banned phone number: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r2 = r4;	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r1 = r1.toString();	 Catch:{ Exception -> 0x010f }
                    r6.putExtra(r0, r1);	 Catch:{ Exception -> 0x010f }
                    r0 = "android.intent.extra.TEXT";	 Catch:{ Exception -> 0x010f }
                    r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010f }
                    r1.<init>();	 Catch:{ Exception -> 0x010f }
                    r2 = "I'm trying to use my mobile phone number: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r2 = r4;	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r2 = "\nBut Telegram says it's banned. Please help.\n\nApp version: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = "\nOS version: SDK ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = "\nDevice Name: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = "\nLocale: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = java.util.Locale.getDefault();	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = r1.toString();	 Catch:{ Exception -> 0x010f }
                    r6.putExtra(r0, r5);	 Catch:{ Exception -> 0x010f }
                    goto L_0x00ff;	 Catch:{ Exception -> 0x010f }
                L_0x00a2:
                    r0 = "android.intent.extra.SUBJECT";	 Catch:{ Exception -> 0x010f }
                    r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010f }
                    r1.<init>();	 Catch:{ Exception -> 0x010f }
                    r2 = "Invalid phone number: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r2 = r4;	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r1 = r1.toString();	 Catch:{ Exception -> 0x010f }
                    r6.putExtra(r0, r1);	 Catch:{ Exception -> 0x010f }
                    r0 = "android.intent.extra.TEXT";	 Catch:{ Exception -> 0x010f }
                    r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010f }
                    r1.<init>();	 Catch:{ Exception -> 0x010f }
                    r2 = "I'm trying to use my mobile phone number: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r2 = r4;	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r2 = "\nBut Telegram says it's invalid. Please help.\n\nApp version: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r2);	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = "\nOS version: SDK ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = "\nDevice Name: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = "\nLocale: ";	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = java.util.Locale.getDefault();	 Catch:{ Exception -> 0x010f }
                    r1.append(r5);	 Catch:{ Exception -> 0x010f }
                    r5 = r1.toString();	 Catch:{ Exception -> 0x010f }
                    r6.putExtra(r0, r5);	 Catch:{ Exception -> 0x010f }
                L_0x00ff:
                    r5 = org.telegram.ui.LoginActivity.this;	 Catch:{ Exception -> 0x010f }
                    r5 = r5.getParentActivity();	 Catch:{ Exception -> 0x010f }
                    r0 = "Send email...";	 Catch:{ Exception -> 0x010f }
                    r6 = android.content.Intent.createChooser(r6, r0);	 Catch:{ Exception -> 0x010f }
                    r5.startActivity(r6);	 Catch:{ Exception -> 0x010f }
                    goto L_0x0126;
                L_0x010f:
                    r5 = org.telegram.ui.LoginActivity.this;
                    r6 = "AppName";
                    r0 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
                    r6 = org.telegram.messenger.LocaleController.getString(r6, r0);
                    r0 = "NoMailInstalled";
                    r1 = NUM; // 0x7f0c0402 float:1.8611273E38 double:1.0530979054E-314;
                    r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                    r5.needShowAlert(r6, r0);
                L_0x0126:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.2.onClick(android.content.DialogInterface, int):void");
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", true), false);
            showDialog(builder.create());
        }
    }

    private void needShowProgress(final int i) {
        if (!(getParentActivity() == null || getParentActivity().isFinishing())) {
            if (this.progressDialog == null) {
                Builder builder = new Builder(getParentActivity(), 1);
                builder.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                if (i != 0) {
                    builder.setPositiveButton(LocaleController.getString("Cancel", C0446R.string.Cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginActivity.this.views[LoginActivity.this.currentViewNum].onCancelPressed();
                            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).cancelRequest(i, true);
                            LoginActivity.this.progressDialog = 0;
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

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        int i2;
        if (i != 3) {
            if (i != 8) {
                if (i == 0) {
                    this.checkPermissions = true;
                    this.checkShowPermissions = true;
                }
                this.doneButton.setVisibility(0);
                i2 = C0446R.drawable.ic_ab_back;
                if (z) {
                    z = this.actionBar;
                    if (!this.views[i].needBackButton()) {
                        if (this.newAccount) {
                            i2 = 0;
                        }
                    }
                    z.setBackButtonImage(i2);
                    this.views[this.currentViewNum].setVisibility(8);
                    this.currentViewNum = i;
                    this.views[i].setParams(bundle, false);
                    this.views[i].setVisibility(0);
                    this.actionBar.setTitle(this.views[i].getHeaderName());
                    this.views[i].onShow();
                    return;
                }
                final SlideView slideView;
                z = this.views[this.currentViewNum];
                slideView = this.views[i];
                this.currentViewNum = i;
                i = this.actionBar;
                if (!slideView.needBackButton()) {
                    if (this.newAccount) {
                        i2 = 0;
                    }
                }
                i.setBackButtonImage(i2);
                slideView.setParams(bundle, false);
                this.actionBar.setTitle(slideView.getHeaderName());
                slideView.onShow();
                slideView.setX((float) (z2 ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
                z.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListener() {
                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }

                    public void onAnimationStart(Animator animator) {
                    }

                    @SuppressLint({"NewApi"})
                    public void onAnimationEnd(Animator animator) {
                        z.setVisibility(8);
                        z.setX(0.0f);
                    }
                }).setDuration(300).translationX((float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x)).start();
                slideView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListener() {
                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationEnd(Animator animator) {
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }

                    public void onAnimationStart(Animator animator) {
                        slideView.setVisibility(0);
                    }
                }).setDuration(300).translationX(false).start();
            }
        }
        this.doneButton.setVisibility(8);
        i2 = C0446R.drawable.ic_ab_back;
        if (z) {
            z = this.actionBar;
            if (this.views[i].needBackButton()) {
                if (this.newAccount) {
                    i2 = 0;
                }
            }
            z.setBackButtonImage(i2);
            this.views[this.currentViewNum].setVisibility(8);
            this.currentViewNum = i;
            this.views[i].setParams(bundle, false);
            this.views[i].setVisibility(0);
            this.actionBar.setTitle(this.views[i].getHeaderName());
            this.views[i].onShow();
            return;
        }
        z = this.views[this.currentViewNum];
        slideView = this.views[i];
        this.currentViewNum = i;
        i = this.actionBar;
        if (slideView.needBackButton()) {
            if (this.newAccount) {
                i2 = 0;
            }
        }
        i.setBackButtonImage(i2);
        slideView.setParams(bundle, false);
        this.actionBar.setTitle(slideView.getHeaderName());
        slideView.onShow();
        if (z2) {
        }
        slideView.setX((float) (z2 ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
        if (z2) {
        }
        z.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(/* anonymous class already generated */).setDuration(300).translationX((float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x)).start();
        slideView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(/* anonymous class already generated */).setDuration(300).translationX(false).start();
    }

    public void saveSelfArgs(Bundle bundle) {
        try {
            bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            bundle.putInt("syncContacts", this.syncContacts);
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

    private void fillNextCodeParams(Bundle bundle, TL_auth_sentCode tL_auth_sentCode) {
        bundle.putString("phoneHash", tL_auth_sentCode.phone_code_hash);
        if (tL_auth_sentCode.next_type instanceof TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (tL_auth_sentCode.next_type instanceof TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (tL_auth_sentCode.next_type instanceof TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        }
        if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeApp) {
            bundle.putInt("type", 1);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(1, true, bundle, false);
            return;
        }
        if (tL_auth_sentCode.timeout == 0) {
            tL_auth_sentCode.timeout = 60;
        }
        bundle.putInt("timeout", tL_auth_sentCode.timeout * 1000);
        if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeCall) {
            bundle.putInt("type", 4);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(4, true, bundle, false);
        } else if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt("type", 3);
            bundle.putString("pattern", tL_auth_sentCode.type.pattern);
            setPage(3, true, bundle, false);
        } else if (tL_auth_sentCode.type instanceof TL_auth_sentCodeTypeSms) {
            bundle.putInt("type", 2);
            bundle.putInt("length", tL_auth_sentCode.type.length);
            setPage(2, true, bundle, false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        PhoneView phoneView = (PhoneView) this.views[0];
        LoginActivitySmsView loginActivitySmsView = (LoginActivitySmsView) this.views[1];
        LoginActivitySmsView loginActivitySmsView2 = (LoginActivitySmsView) this.views[2];
        LoginActivitySmsView loginActivitySmsView3 = (LoginActivitySmsView) this.views[3];
        LoginActivitySmsView loginActivitySmsView4 = (LoginActivitySmsView) this.views[4];
        LoginActivityRegisterView loginActivityRegisterView = (LoginActivityRegisterView) this.views[5];
        LoginActivityPasswordView loginActivityPasswordView = (LoginActivityPasswordView) this.views[6];
        LoginActivityRecoverView loginActivityRecoverView = (LoginActivityRecoverView) this.views[7];
        LoginActivityResetWaitView loginActivityResetWaitView = (LoginActivityResetWaitView) this.views[8];
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
        r12[17] = new ThemeDescription(loginActivityPasswordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[18] = new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[19] = new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[20] = new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[21] = new ThemeDescription(loginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[22] = new ThemeDescription(loginActivityPasswordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[23] = new ThemeDescription(loginActivityPasswordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteRedText6);
        r12[24] = new ThemeDescription(loginActivityPasswordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[25] = new ThemeDescription(loginActivityRegisterView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[26] = new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[27] = new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[28] = new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[29] = new ThemeDescription(loginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[30] = new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[31] = new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[32] = new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[33] = new ThemeDescription(loginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[34] = new ThemeDescription(loginActivityRegisterView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[35] = new ThemeDescription(loginActivityRecoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[36] = new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[37] = new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[38] = new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[39] = new ThemeDescription(loginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[40] = new ThemeDescription(loginActivityRecoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[41] = new ThemeDescription(loginActivityResetWaitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[42] = new ThemeDescription(loginActivityResetWaitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[43] = new ThemeDescription(loginActivityResetWaitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[44] = new ThemeDescription(loginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[45] = new ThemeDescription(loginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText6);
        r12[46] = new ThemeDescription(loginActivitySmsView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[47] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[48] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[49] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[50] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[51] = new ThemeDescription(loginActivitySmsView.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[52] = new ThemeDescription(loginActivitySmsView.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[53] = new ThemeDescription(loginActivitySmsView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[54] = new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[55] = new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r12[56] = new ThemeDescription(loginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[57] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[58] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[59] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[60] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[61] = new ThemeDescription(loginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[62] = new ThemeDescription(loginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[63] = new ThemeDescription(loginActivitySmsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[64] = new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[65] = new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r12[66] = new ThemeDescription(loginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[67] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[68] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[69] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[70] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[71] = new ThemeDescription(loginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[72] = new ThemeDescription(loginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[73] = new ThemeDescription(loginActivitySmsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[74] = new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[75] = new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r12[76] = new ThemeDescription(loginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[77] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r12[78] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r12[79] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r12[80] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r12[81] = new ThemeDescription(loginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r12[82] = new ThemeDescription(loginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[83] = new ThemeDescription(loginActivitySmsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r12[84] = new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r12[85] = new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        return r12;
    }
}
