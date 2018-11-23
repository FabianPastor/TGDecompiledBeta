package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.DefaultLoadControl;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.C0194PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0403ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.HintEditText;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.SlideView;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_auth_cancelCode;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCode;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.telegram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChangePhoneActivity */
public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = true;
    private int currentViewNum = 0;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList();
    private AlertDialog progressDialog;
    private SlideView[] views = new SlideView[5];

    /* renamed from: org.telegram.ui.ChangePhoneActivity$ProgressView */
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

    /* renamed from: org.telegram.ui.ChangePhoneActivity$1 */
    class C12741 extends ActionBarMenuOnItemClick {
        C12741() {
        }

        public void onItemClick(int id) {
            if (id == 1) {
                ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
            } else if (id == -1) {
                ChangePhoneActivity.this.lambda$checkDiscard$70$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView */
    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        private EditTextBoldCursor codeField;
        private int codeTime = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private String emailPhone;
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
        private String requestPhone;
        private int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private boolean waitingForEvent;

        /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$2 */
        class C05312 extends TimerTask {
            C05312() {
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$2$$Lambda$0(this));
            }

            final /* synthetic */ void lambda$run$0$ChangePhoneActivity$LoginActivitySmsView$2() {
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
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$3 */
        class C05333 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$3$1 */
            class C05321 implements Runnable {
                C05321() {
                }

                public void run() {
                    double currentTime = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTime - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTime;
                    if (LoginActivitySmsView.this.time >= 1000) {
                        int seconds = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                        } else if (LoginActivitySmsView.this.nextType == 2) {
                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
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
                                LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                            } else {
                                LoginActivitySmsView.this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
                            }
                            LoginActivitySmsView.this.createCodeTimer();
                            TL_auth_resendCode req = new TL_auth_resendCode();
                            req.phone_number = LoginActivitySmsView.this.requestPhone;
                            req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$0(this), 2);
                        } else if (LoginActivitySmsView.this.nextType == 3) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            LoginActivitySmsView.this.waitingForEvent = false;
                            LoginActivitySmsView.this.destroyCodeTimer();
                            LoginActivitySmsView.this.resendCode();
                        }
                    }
                }

                final /* synthetic */ void lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$3$1(TLObject response, TL_error error) {
                    if (error != null && error.text != null) {
                        AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$1(this, error));
                    }
                }

                final /* synthetic */ void lambda$null$0$ChangePhoneActivity$LoginActivitySmsView$3$1(TL_error error) {
                    LoginActivitySmsView.this.lastError = error.text;
                }
            }

            C05333() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    AndroidUtilities.runOnUIThread(new C05321());
                }
            }
        }

        public LoginActivitySmsView(Context context, int type) {
            super(context);
            this.currentType = type;
            setOrientation(1);
            this.confirmTextView = new TextView(context);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
            this.titleTextView = new TextView(context);
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            FrameLayout frameLayout;
            if (this.currentType == 3) {
                this.confirmTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3));
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.phone_activate);
                if (LocaleController.isRTL) {
                    int i;
                    frameLayout.addView(imageView, LayoutHelper.createFrame(64, 76.0f, 19, 2.0f, 2.0f, 0.0f, 0.0f));
                    View view = this.confirmTextView;
                    if (LocaleController.isRTL) {
                        i = 5;
                    } else {
                        i = 3;
                    }
                    frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, i, 82.0f, 0.0f, 0.0f, 0.0f));
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
                    this.blackImageView.setImageResource(R.drawable.sms_devices);
                    this.blackImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Mode.MULTIPLY));
                    frameLayout.addView(this.blackImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.blueImageView = new ImageView(context);
                    this.blueImageView.setImageResource(R.drawable.sms_bubble);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentAppCodeTitle", R.string.SentAppCodeTitle));
                } else if (this.currentType == 2 || this.currentType == 3) {
                    this.blueImageView = new ImageView(context);
                    this.blueImageView.setImageResource(R.drawable.sms_code);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionBackground), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentSmsCodeTitle", R.string.SentSmsCodeTitle));
                }
                addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 0));
                addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
            }
            this.codeField = new EditTextBoldCursor(context);
            this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setHint(LocaleController.getString("Code", R.string.Code));
            this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setCursorSize(AndroidUtilities.m9dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setPadding(0, 0, 0, 0);
            this.codeField.setGravity(49);
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            } else {
                this.codeField.setInputType(3);
            }
            addView(this.codeField, LayoutHelper.createLinear(172, 36, 1, 0, 30, 0, 0));
            this.codeField.addTextChangedListener(new TextWatcher(ChangePhoneActivity.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!LoginActivitySmsView.this.ignoreOnTextChange && LoginActivitySmsView.this.length != 0 && C0194PhoneFormat.stripExceptNumbers(LoginActivitySmsView.this.codeField.getText().toString()).length() == LoginActivitySmsView.this.length) {
                        LoginActivitySmsView.this.onNextPressed();
                    }
                }
            });
            this.codeField.setOnEditorActionListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$0(this));
            if (this.currentType == 3) {
                this.codeField.setEnabled(false);
                this.codeField.setInputType(0);
                this.codeField.setVisibility(8);
            }
            this.timeText = new TextView(context);
            this.timeText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.timeText.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
            if (this.currentType == 3) {
                this.timeText.setTextSize(1, 14.0f);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 30, 0, 0));
                this.progressView = new ProgressView(context);
                this.timeText.setGravity(LocaleController.isRTL ? 5 : 3);
                addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0f, 12.0f, 0.0f, 0.0f));
            } else {
                this.timeText.setPadding(0, AndroidUtilities.m9dp(2.0f), 0, AndroidUtilities.m9dp(10.0f));
                this.timeText.setTextSize(1, 15.0f);
                this.timeText.setGravity(49);
                addView(this.timeText, LayoutHelper.createLinear(-2, -2, 49, 0, 30, 0, 0));
            }
            this.problemText = new TextView(context);
            this.problemText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.problemText.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
            this.problemText.setPadding(0, AndroidUtilities.m9dp(2.0f), 0, AndroidUtilities.m9dp(10.0f));
            this.problemText.setTextSize(1, 15.0f);
            this.problemText.setGravity(49);
            if (this.currentType == 1) {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", R.string.DidNotGetTheCodeSms));
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            }
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49, 0, 40, 0, 0));
            this.problemText.setOnClickListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$1(this));
        }

        final /* synthetic */ boolean lambda$new$0$ChangePhoneActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        final /* synthetic */ void lambda$new$1$ChangePhoneActivity$LoginActivitySmsView(View v) {
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
                        mailer.setType("message/rfc822");
                        mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                        mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.emailPhone);
                        mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                        getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                        return;
                    } catch (Exception e) {
                        AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                        return;
                    }
                }
                resendCode();
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.currentType != 3) {
                int h;
                int bottom = this.confirmTextView.getBottom();
                int height = Math.max(AndroidUtilities.m9dp(120.0f), Math.min(AndroidUtilities.m9dp(291.0f), b - t) - bottom);
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
                h = this.codeField.getMeasuredHeight();
                t = ((height - h) / 2) + bottom;
                this.codeField.layout(this.codeField.getLeft(), t, this.codeField.getRight(), t + h);
            }
        }

        private void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            ChangePhoneActivity.this.needShowProgress();
            TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$$Lambda$2(this, params, req), 2);
        }

        final /* synthetic */ void lambda$resendCode$4$ChangePhoneActivity$LoginActivitySmsView(Bundle params, TL_auth_resendCode req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$8(this, error, params, response, req));
        }

        final /* synthetic */ void lambda$null$3$ChangePhoneActivity$LoginActivitySmsView(TL_error error, Bundle params, TLObject response, TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                ChangePhoneActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, error, ChangePhoneActivity.this, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$9(this));
                }
            }
            ChangePhoneActivity.this.needHideProgress();
        }

        final /* synthetic */ void lambda$null$2$ChangePhoneActivity$LoginActivitySmsView(DialogInterface dialog1, int which) {
            onBackPressed(true);
            ChangePhoneActivity.this.lambda$checkDiscard$70$PassportActivity();
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            int i = 8;
            if (params != null) {
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
                int i2 = params.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = params.getInt("nextType");
                this.pattern = params.getString("pattern");
                this.length = params.getInt("length");
                if (this.progressView != null) {
                    this.progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = C0194PhoneFormat.getInstance().format(this.phone);
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
                    TextView textView;
                    TextView textView2;
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
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        textView = this.problemText;
                        if (this.time < 1000) {
                            i2 = 0;
                        } else {
                            i2 = 8;
                        }
                        textView.setVisibility(i2);
                        textView2 = this.timeText;
                        if (this.time >= 1000) {
                            i = 0;
                        }
                        textView2.setVisibility(i);
                        createTimer();
                    } else if (this.currentType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(2), Integer.valueOf(0)));
                        textView = this.problemText;
                        if (this.time < 1000) {
                            i2 = 0;
                        } else {
                            i2 = 8;
                        }
                        textView.setVisibility(i2);
                        textView2 = this.timeText;
                        if (this.time >= 1000) {
                            i = 0;
                        }
                        textView2.setVisibility(i);
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
                this.codeTimer.schedule(new C05312(), 0, 1000);
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
                FileLog.m13e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new C05333(), 0, 1000);
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
                FileLog.m13e(e);
            }
        }

        public void onNextPressed() {
            if (!this.nextPressed) {
                String code = C0194PhoneFormat.stripExceptNumbers(this.codeField.getText().toString());
                if (TextUtils.isEmpty(code)) {
                    AndroidUtilities.shakeView(this.codeField, 2.0f, 0);
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
                TL_account_changePhone req = new TL_account_changePhone();
                req.phone_number = this.requestPhone;
                req.phone_code = code;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                ChangePhoneActivity.this.needShowProgress();
                ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$$Lambda$3(this, req), 2);
            }
        }

        final /* synthetic */ void lambda$onNextPressed$6$ChangePhoneActivity$LoginActivitySmsView(TL_account_changePhone req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$7(this, error, response, req));
        }

        final /* synthetic */ void lambda$null$5$ChangePhoneActivity$LoginActivitySmsView(TL_error error, TLObject response, TL_account_changePhone req) {
            ChangePhoneActivity.this.needHideProgress();
            this.nextPressed = false;
            if (error == null) {
                User user = (User) response;
                destroyTimer();
                destroyCodeTimer();
                UserConfig.getInstance(ChangePhoneActivity.this.currentAccount).setCurrentUser(user);
                UserConfig.getInstance(ChangePhoneActivity.this.currentAccount).saveConfig(true);
                ArrayList<User> users = new ArrayList();
                users.add(user);
                MessagesStorage.getInstance(ChangePhoneActivity.this.currentAccount).putUsersAndChats(users, null, true, true);
                MessagesController.getInstance(ChangePhoneActivity.this.currentAccount).putUser(user, false);
                ChangePhoneActivity.this.lambda$checkDiscard$70$PassportActivity();
                NotificationCenter.getInstance(ChangePhoneActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                return;
            }
            this.lastError = error.text;
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
                AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, error, ChangePhoneActivity.this, req, new Object[0]);
            }
        }

        public boolean onBackPressed(boolean force) {
            if (force) {
                TL_auth_cancelCode req = new TL_auth_cancelCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, ChangePhoneActivity$LoginActivitySmsView$$Lambda$5.$instance, 10);
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
            Builder builder = new Builder(ChangePhoneActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new ChangePhoneActivity$LoginActivitySmsView$$Lambda$4(this));
            ChangePhoneActivity.this.showDialog(builder.create());
            return false;
        }

        final /* synthetic */ void lambda$onBackPressed$7$ChangePhoneActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            ChangePhoneActivity.this.setPage(0, true, null, true);
        }

        static final /* synthetic */ void lambda$onBackPressed$8$ChangePhoneActivity$LoginActivitySmsView(TLObject response, TL_error error) {
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
                AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$6(this), 100);
            }
        }

        final /* synthetic */ void lambda$onShow$9$ChangePhoneActivity$LoginActivitySmsView() {
            if (this.codeField != null) {
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

    /* renamed from: org.telegram.ui.ChangePhoneActivity$PhoneView */
    public class PhoneView extends SlideView implements OnItemSelectedListener {
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
            this.countryButton.setPadding(AndroidUtilities.m9dp(12.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(12.0f), 0);
            this.countryButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.countryButton.setMaxLines(1);
            this.countryButton.setSingleLine(true);
            this.countryButton.setEllipsize(TruncateAt.END);
            this.countryButton.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.countryButton.setBackgroundResource(R.drawable.spinner_states);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new ChangePhoneActivity$PhoneView$$Lambda$0(this));
            this.view = new View(context);
            this.view.setPadding(AndroidUtilities.m9dp(12.0f), 0, AndroidUtilities.m9dp(12.0f), 0);
            this.view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
            addView(this.view, LayoutHelper.createLinear(-1, 1, 4.0f, -17.5f, 4.0f, 0.0f));
            View linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setText("+");
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 18.0f);
            linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
            this.codeField = new EditTextBoldCursor(context);
            this.codeField.setInputType(3);
            this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setCursorSize(AndroidUtilities.m9dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.codeField.setPadding(AndroidUtilities.m9dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(268435461);
            this.codeField.setFilters(new InputFilter[]{new LengthFilter(5)});
            linearLayout.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
            final ChangePhoneActivity changePhoneActivity = ChangePhoneActivity.this;
            this.codeField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!PhoneView.this.ignoreOnTextChange) {
                        PhoneView.this.ignoreOnTextChange = true;
                        String text = C0194PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                        PhoneView.this.codeField.setText(text);
                        if (text.length() == 0) {
                            PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                            PhoneView.this.phoneField.setHintText(null);
                            PhoneView.this.countryState = 1;
                        } else {
                            boolean ok = false;
                            String textToSet = null;
                            if (text.length() > 4) {
                                PhoneView.this.ignoreOnTextChange = true;
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
                                    PhoneView.this.ignoreOnTextChange = true;
                                    textToSet = text.substring(1, text.length()) + PhoneView.this.phoneField.getText().toString();
                                    EditTextBoldCursor access$300 = PhoneView.this.codeField;
                                    text = text.substring(0, 1);
                                    access$300.setText(text);
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
            this.codeField.setOnEditorActionListener(new ChangePhoneActivity$PhoneView$$Lambda$1(this));
            this.phoneField = new HintEditText(context);
            this.phoneField.setInputType(3);
            this.phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.phoneField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.phoneField.setPadding(0, 0, 0, 0);
            this.phoneField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.phoneField.setCursorSize(AndroidUtilities.m9dp(20.0f));
            this.phoneField.setCursorWidth(1.5f);
            this.phoneField.setTextSize(1, 18.0f);
            this.phoneField.setMaxLines(1);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(268435461);
            linearLayout.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            final ChangePhoneActivity changePhoneActivity2 = ChangePhoneActivity.this;
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
                            HintEditText access$500 = PhoneView.this.phoneField;
                            if (start > PhoneView.this.phoneField.length()) {
                                start = PhoneView.this.phoneField.length();
                            }
                            access$500.setSelection(start);
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            this.phoneField.setOnEditorActionListener(new ChangePhoneActivity$PhoneView$$Lambda$2(this));
            this.phoneField.setOnKeyListener(new ChangePhoneActivity$PhoneView$$Lambda$3(this));
            this.textView2 = new TextView(context);
            this.textView2.setText(LocaleController.getString("ChangePhoneHelp", R.string.ChangePhoneHelp));
            this.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.textView2.setTextSize(1, 14.0f);
            this.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView2.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
            addView(this.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
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
                FileLog.m13e(e);
            }
            Collections.sort(this.countriesArray, ChangePhoneActivity$PhoneView$$Lambda$4.$instance);
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    country = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Throwable e2) {
                FileLog.m13e(e2);
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
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        final /* synthetic */ void lambda$new$2$ChangePhoneActivity$PhoneView(View view) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new ChangePhoneActivity$PhoneView$$Lambda$7(this));
            ChangePhoneActivity.this.presentFragment(fragment);
        }

        final /* synthetic */ void lambda$null$1$ChangePhoneActivity$PhoneView(String name, String shortName) {
            selectCountry(name);
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$PhoneView$$Lambda$8(this), 300);
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
        }

        final /* synthetic */ void lambda$null$0$ChangePhoneActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        final /* synthetic */ boolean lambda$new$3$ChangePhoneActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
            return true;
        }

        final /* synthetic */ boolean lambda$new$4$ChangePhoneActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        final /* synthetic */ boolean lambda$new$5$ChangePhoneActivity$PhoneView(View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            this.codeField.setSelection(this.codeField.length());
            this.codeField.dispatchKeyEvent(event);
            return true;
        }

        public void selectCountry(String name) {
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

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed() {
            if (ChangePhoneActivity.this.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
                boolean allowCall = true;
                if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean allowSms = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                    if (ChangePhoneActivity.this.checkPermissions) {
                        ChangePhoneActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowSms) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
                            if (VERSION.SDK_INT >= 23) {
                                ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_SMS");
                            }
                        }
                        if (!ChangePhoneActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (preferences.getBoolean("firstlogin", true) || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(ChangePhoneActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                if (ChangePhoneActivity.this.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", R.string.AllowReadCallAndSms));
                                } else if (allowSms) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", R.string.AllowReadSms));
                                }
                                ChangePhoneActivity.this.permissionsDialog = ChangePhoneActivity.this.showDialog(builder.create());
                                return;
                            }
                            ChangePhoneActivity.this.getParentActivity().requestPermissions((String[]) ChangePhoneActivity.this.permissionsItems.toArray(new String[ChangePhoneActivity.this.permissionsItems.size()]), 6);
                            return;
                        }
                    }
                }
                if (this.countryState == 1) {
                    AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                } else if (this.codeField.length() == 0) {
                    AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else {
                    TL_account_sendChangePhoneCode req = new TL_account_sendChangePhoneCode();
                    String phone = C0194PhoneFormat.stripExceptNumbers(TtmlNode.ANONYMOUS_REGION_ID + this.codeField.getText() + this.phoneField.getText());
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
                            FileLog.m13e(e);
                        }
                    }
                    Bundle params = new Bundle();
                    params.putString("phone", "+" + this.codeField.getText() + " " + this.phoneField.getText());
                    try {
                        params.putString("ephone", "+" + C0194PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + C0194PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    ChangePhoneActivity.this.needShowProgress();
                    ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$PhoneView$$Lambda$5(this, params, req), 2);
                }
            }
        }

        final /* synthetic */ void lambda$onNextPressed$7$ChangePhoneActivity$PhoneView(Bundle params, TL_account_sendChangePhoneCode req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$PhoneView$$Lambda$6(this, error, params, response, req));
        }

        final /* synthetic */ void lambda$null$6$ChangePhoneActivity$PhoneView(TL_error error, Bundle params, TLObject response, TL_account_sendChangePhoneCode req) {
            this.nextPressed = false;
            if (error == null) {
                ChangePhoneActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
            } else {
                AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, error, ChangePhoneActivity.this, req, params.getString("phone"));
            }
            ChangePhoneActivity.this.needHideProgress();
        }

        public void onShow() {
            super.onShow();
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                this.phoneField.setSelection(this.phoneField.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        public String getHeaderName() {
            return LocaleController.getString("ChangePhoneNewNumber", R.string.ChangePhoneNewNumber);
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
                FileLog.m13e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C12741());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
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
        int a = 0;
        while (a < this.views.length) {
            this.views[a].setVisibility(a == 0 ? 0 : 8);
            frameLayout.addView(this.views[a], LayoutHelper.createFrame(-1, a == 0 ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            a++;
        }
        this.actionBar.setTitle(this.views[0].getHeaderName());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            if (this.currentViewNum == 0) {
                this.views[this.currentViewNum].onNextPressed();
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
    }

    public boolean onBackPressed() {
        if (this.currentViewNum == 0) {
            for (int a = 0; a < this.views.length; a++) {
                if (this.views[a] != null) {
                    this.views[a].onDestroyActivity();
                }
            }
            return true;
        }
        if (this.views[this.currentViewNum].onBackPressed(false)) {
            setPage(0, true, null, true);
        }
        return false;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.views[this.currentViewNum].onShow();
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
                FileLog.m13e(e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (page == 3) {
            this.doneButton.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        final SlideView outView = this.views[this.currentViewNum];
        final SlideView newView = this.views[page];
        this.currentViewNum = page;
        newView.setParams(params, false);
        this.actionBar.setTitle(newView.getHeaderName());
        newView.onShow();
        newView.setX(back ? (float) (-AndroidUtilities.displaySize.x) : (float) AndroidUtilities.displaySize.x);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        String str = "translationX";
        float[] fArr = new float[1];
        fArr[0] = back ? (float) AndroidUtilities.displaySize.x : (float) (-AndroidUtilities.displaySize.x);
        animatorArr[0] = ObjectAnimator.ofFloat(outView, str, fArr);
        animatorArr[1] = ObjectAnimator.ofFloat(newView, "translationX", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
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
        r15 = new ThemeDescription[65];
        r15[25] = new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r15[26] = new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r15[27] = new ThemeDescription(smsView1.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[28] = new ThemeDescription(smsView1.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionBackground);
        r15[29] = new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r15[30] = new ThemeDescription(smsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[31] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[32] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r15[33] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r15[34] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r15[35] = new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r15[36] = new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r15[37] = new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r15[38] = new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r15[39] = new ThemeDescription(smsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[40] = new ThemeDescription(smsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionBackground);
        r15[41] = new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r15[42] = new ThemeDescription(smsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[43] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[44] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r15[45] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r15[46] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r15[47] = new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r15[48] = new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r15[49] = new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r15[50] = new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r15[51] = new ThemeDescription(smsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[52] = new ThemeDescription(smsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionBackground);
        r15[53] = new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r15[54] = new ThemeDescription(smsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[55] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[56] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r15[57] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r15[58] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r15[59] = new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        r15[60] = new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        r15[61] = new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        r15[62] = new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        r15[63] = new ThemeDescription(smsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r15[64] = new ThemeDescription(smsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionBackground);
        return r15;
    }
}
