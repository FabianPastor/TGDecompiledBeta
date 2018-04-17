package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = true;
    private int currentViewNum = 0;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList();
    private AlertDialog progressDialog;
    private SlideView[] views = new SlideView[5];

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
    class C19461 extends ActionBarMenuOnItemClick {
        C19461() {
        }

        public void onItemClick(int id) {
            if (id == 1) {
                ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
            } else if (id == -1) {
                ChangePhoneActivity.this.finishFragment();
            }
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private EditTextBoldCursor codeField;
        private volatile int codeTime = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
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
        final /* synthetic */ ChangePhoneActivity this$0;
        private volatile int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private boolean waitingForEvent;
        private TextView wrongNumber;

        /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$6 */
        class C09196 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$6$1 */
            class C09181 implements Runnable {
                C09181() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= 1000) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C09196() {
            }

            public void run() {
                double currentTime = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTime - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTime;
                AndroidUtilities.runOnUIThread(new C09181());
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$7 */
        class C09227 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$7$1 */
            class C09211 implements Runnable {

                /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$7$1$1 */
                class C19491 implements RequestDelegate {
                    C19491() {
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

                C09211() {
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
                            ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new C19491(), 2);
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

            C09227() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTime = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTime - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTime;
                    AndroidUtilities.runOnUIThread(new C09211());
                }
            }
        }

        public LoginActivitySmsView(ChangePhoneActivity this$0, Context context, int type) {
            final ChangePhoneActivity changePhoneActivity = this$0;
            Context context2 = context;
            this.this$0 = changePhoneActivity;
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
                                AlertsCreator.showSimpleAlert(LoginActivitySmsView.this.this$0, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
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

                /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$4$1 */
                class C19471 implements RequestDelegate {
                    C19471() {
                    }

                    public void run(TLObject response, TL_error error) {
                    }
                }

                public void onClick(View view) {
                    TL_auth_cancelCode req = new TL_auth_cancelCode();
                    req.phone_number = LoginActivitySmsView.this.requestPhone;
                    req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new C19471(), 2);
                    LoginActivitySmsView.this.onBackPressed();
                    LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                }
            });
        }

        private void resendCode() {
            final Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            final TL_auth_resendCode req = new TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LoginActivitySmsView.this.nextPressed = false;
                            if (error == null) {
                                LoginActivitySmsView.this.this$0.fillNextCodeParams(params, (TL_auth_sentCode) response);
                            } else {
                                AlertsCreator.processError(LoginActivitySmsView.this.this$0.currentAccount, error, LoginActivitySmsView.this.this$0, req, new Object[0]);
                                if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                    LoginActivitySmsView.this.onBackPressed();
                                    LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                                }
                            }
                            LoginActivitySmsView.this.this$0.needHideProgress();
                        }
                    });
                }
            }, 2);
        }

        public String getHeaderName() {
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
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
                        createTimer();
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
                this.codeTimer.schedule(new C09196(), 0, 1000);
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
                this.timeTimer.schedule(new C09227(), 0, 1000);
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
                final TL_account_changePhone req = new TL_account_changePhone();
                req.phone_number = this.requestPhone;
                req.phone_code = this.codeField.getText().toString();
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivitySmsView.this.this$0.needHideProgress();
                                LoginActivitySmsView.this.nextPressed = false;
                                if (error == null) {
                                    User user = response;
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).setCurrentUser(user);
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).saveConfig(true);
                                    ArrayList<User> users = new ArrayList();
                                    users.add(user);
                                    MessagesStorage.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUsersAndChats(users, null, true, true);
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUser(user, false);
                                    LoginActivitySmsView.this.this$0.finishFragment();
                                    NotificationCenter.getInstance(LoginActivitySmsView.this.this$0.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                                    return;
                                }
                                LoginActivitySmsView.this.lastError = error.text;
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
                                    AlertsCreator.processError(LoginActivitySmsView.this.this$0.currentAccount, error, LoginActivitySmsView.this.this$0, req, new Object[0]);
                                }
                            }
                        });
                    }
                }, 2);
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
            if (this.codeField != null) {
                this.codeField.requestFocus();
                this.codeField.setSelection(this.codeField.length());
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
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(num);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        }
                    }
                }
            }
        }
    }

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
        final /* synthetic */ ChangePhoneActivity this$0;
        private View view;

        public PhoneView(ChangePhoneActivity this$0, Context context) {
            final ChangePhoneActivity changePhoneActivity = this$0;
            Context context2 = context;
            this.this$0 = changePhoneActivity;
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

                /* renamed from: org.telegram.ui.ChangePhoneActivity$PhoneView$1$1 */
                class C19511 implements CountrySelectActivityDelegate {

                    /* renamed from: org.telegram.ui.ChangePhoneActivity$PhoneView$1$1$1 */
                    class C09241 implements Runnable {
                        C09241() {
                        }

                        public void run() {
                            AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                        }
                    }

                    C19511() {
                    }

                    public void didSelectCountry(String name, String shortName) {
                        PhoneView.this.selectCountry(name);
                        AndroidUtilities.runOnUIThread(new C09241(), 300);
                        PhoneView.this.phoneField.requestFocus();
                        PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                    }
                }

                public void onClick(View view) {
                    CountrySelectActivity fragment = new CountrySelectActivity(true);
                    fragment.setCountrySelectActivityDelegate(new C19511());
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
            r1.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r1.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r1.codeField.setCursorWidth(1.5f);
            r1.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
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
                                int a2;
                                String sub;
                                PhoneView.this.ignoreOnTextChange = true;
                                while (true) {
                                    a2 = a;
                                    if (a2 < 1) {
                                        break;
                                    }
                                    sub = text.substring(0, a2);
                                    if (((String) PhoneView.this.codesMap.get(sub)) != null) {
                                        break;
                                    }
                                    a = a2 - 1;
                                }
                                ok = true;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(text.substring(a2, text.length()));
                                stringBuilder.append(PhoneView.this.phoneField.getText().toString());
                                textToSet = stringBuilder.toString();
                                text = sub;
                                PhoneView.this.codeField.setText(sub);
                                if (!ok) {
                                    PhoneView.this.ignoreOnTextChange = true;
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(text.substring(1, text.length()));
                                    stringBuilder2.append(PhoneView.this.phoneField.getText().toString());
                                    textToSet = stringBuilder2.toString();
                                    EditTextBoldCursor access$400 = PhoneView.this.codeField;
                                    CharSequence substring = text.substring(0, 1);
                                    text = substring;
                                    access$400.setText(substring);
                                }
                            }
                            String country = (String) PhoneView.this.codesMap.get(text);
                            if (country != null) {
                                int index = PhoneView.this.countriesArray.indexOf(country);
                                if (index != -1) {
                                    PhoneView.this.ignoreSelection = true;
                                    PhoneView.this.countryButton.setText((CharSequence) PhoneView.this.countriesArray.get(index));
                                    String hint = (String) PhoneView.this.phoneFormatMap.get(text);
                                    HintEditText access$200 = PhoneView.this.phoneField;
                                    if (hint != null) {
                                        str = hint.replace('X', '\u2013');
                                    }
                                    access$200.setHintText(str);
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
            r1.textView2.setText(LocaleController.getString("ChangePhoneHelp", R.string.ChangePhoneHelp));
            r1.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r1.textView2.setTextSize(1, 14.0f);
            r1.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            r1.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r1.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
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
                AndroidUtilities.showKeyboard(r1.phoneField);
                r1.phoneField.requestFocus();
                r1.phoneField.setSelection(r1.phoneField.length());
                return;
            }
            AndroidUtilities.showKeyboard(r1.codeField);
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
            if (this.this$0.getParentActivity() != null) {
                if (!this.nextPressed) {
                    TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
                    boolean allowCall = true;
                    if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                        allowCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                        boolean allowSms = this.this$0.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                        if (this.this$0.checkPermissions) {
                            this.this$0.permissionsItems.clear();
                            if (!allowCall) {
                                this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!allowSms) {
                                this.this$0.permissionsItems.add("android.permission.RECEIVE_SMS");
                                if (VERSION.SDK_INT >= 23) {
                                    this.this$0.permissionsItems.add("android.permission.READ_SMS");
                                }
                            }
                            if (!this.this$0.permissionsItems.isEmpty()) {
                                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                if (!(preferences.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE"))) {
                                    if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                        this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[this.this$0.permissionsItems.size()]), 6);
                                        return;
                                    }
                                }
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                if (this.this$0.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", R.string.AllowReadCallAndSms));
                                } else if (allowSms) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", R.string.AllowReadSms));
                                }
                                this.this$0.permissionsDialog = this.this$0.showDialog(builder.create());
                                return;
                            }
                        }
                    }
                    if (this.countryState == 1) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                    } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("WrongCountry", R.string.WrongCountry));
                    } else if (this.codeField.length() == 0) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    } else {
                        final TL_account_sendChangePhoneCode req = new TL_account_sendChangePhoneCode();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                        stringBuilder.append(this.codeField.getText());
                        stringBuilder.append(this.phoneField.getText());
                        String phone = PhoneFormat.stripExceptNumbers(stringBuilder.toString());
                        req.phone_number = phone;
                        boolean z = simcardAvailable && allowCall;
                        req.allow_flashcall = z;
                        if (req.allow_flashcall) {
                            try {
                                String number = tm.getLine1Number();
                                if (TextUtils.isEmpty(number)) {
                                    req.current_number = false;
                                } else {
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
                                }
                            } catch (Throwable e) {
                                req.allow_flashcall = false;
                                FileLog.m3e(e);
                            }
                        }
                        final Bundle params = new Bundle();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("+");
                        stringBuilder2.append(this.codeField.getText());
                        stringBuilder2.append(this.phoneField.getText());
                        params.putString("phone", stringBuilder2.toString());
                        try {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("+");
                            stringBuilder2.append(PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()));
                            stringBuilder2.append(" ");
                            stringBuilder2.append(PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                            params.putString("ephone", stringBuilder2.toString());
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("+");
                            stringBuilder3.append(phone);
                            params.putString("ephone", stringBuilder3.toString());
                        }
                        params.putString("phoneFormated", phone);
                        this.nextPressed = true;
                        this.this$0.needShowProgress();
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(final TLObject response, final TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        PhoneView.this.nextPressed = false;
                                        if (error == null) {
                                            PhoneView.this.this$0.fillNextCodeParams(params, (TL_auth_sentCode) response);
                                        } else {
                                            AlertsCreator.processError(PhoneView.this.this$0.currentAccount, error, PhoneView.this.this$0, req, params.getString("phone"));
                                        }
                                        PhoneView.this.this$0.needHideProgress();
                                    }
                                });
                            }
                        }, 2);
                    }
                }
            }
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
                FileLog.m3e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C19461());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
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
        int a = 0;
        while (a < r0.views.length) {
            r0.views[a].setVisibility(a == 0 ? 0 : 8);
            frameLayout.addView(r0.views[a], LayoutHelper.createFrame(-1, a == 0 ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            a++;
        }
        r0.actionBar.setTitle(r0.views[0].getHeaderName());
        return r0.fragmentView;
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
        int a = 0;
        if (this.currentViewNum == 0) {
            while (true) {
                int a2 = a;
                if (a2 >= this.views.length) {
                    return true;
                }
                if (this.views[a2] != null) {
                    this.views[a2].onDestroyActivity();
                }
                a = a2 + 1;
            }
        } else {
            this.views[this.currentViewNum].onBackPressed();
            setPage(0, true, null, true);
            return false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void needShowProgress() {
        if (!(getParentActivity() == null || getParentActivity().isFinishing())) {
            if (this.progressDialog == null) {
                this.progressDialog = new AlertDialog(getParentActivity(), 1);
                this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                this.progressDialog.setCanceledOnTouchOutside(false);
                this.progressDialog.setCancelable(false);
                this.progressDialog.show();
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
        newView.setX((float) (back ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        String str = "translationX";
        float[] fArr = new float[1];
        fArr[0] = (float) (back ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x);
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[57];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[7] = new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhiteGrayLine);
        themeDescriptionArr[8] = new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[9] = new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[10] = new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[11] = new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[12] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[13] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[14] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[15] = new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[16] = new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[17] = new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[18] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[19] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[20] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[21] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[22] = new ThemeDescription(smsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[23] = new ThemeDescription(smsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[24] = new ThemeDescription(smsView1.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[25] = new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[26] = new ThemeDescription(smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[27] = new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[28] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[29] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[30] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[31] = new ThemeDescription(smsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[32] = new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[33] = new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[34] = new ThemeDescription(smsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[35] = new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[36] = new ThemeDescription(smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[37] = new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[38] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[39] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[40] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[41] = new ThemeDescription(smsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[42] = new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[43] = new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[44] = new ThemeDescription(smsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[45] = new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[46] = new ThemeDescription(smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[47] = new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[48] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[49] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[50] = new ThemeDescription(smsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[51] = new ThemeDescription(smsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[52] = new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[53] = new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[54] = new ThemeDescription(smsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[55] = new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[56] = new ThemeDescription(smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        return themeDescriptionArr;
    }
}
