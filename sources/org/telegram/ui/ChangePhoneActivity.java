package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
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

    /* renamed from: org.telegram.ui.ChangePhoneActivity$1 */
    class C19521 extends ActionBarMenuOnItemClick {
        C19521() {
        }

        public void onItemClick(int i) {
            if (i == 1) {
                ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
            } else if (i == -1) {
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
        class C09256 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$6$1 */
            class C09241 implements Runnable {
                C09241() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= 1000) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C09256() {
            }

            public void run() {
                double currentTimeMillis = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTimeMillis - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C09241());
            }
        }

        /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$7 */
        class C09287 extends TimerTask {

            /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$7$1 */
            class C09271 implements Runnable {

                /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$7$1$1 */
                class C19551 implements RequestDelegate {
                    C19551() {
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

                C09271() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= 1000) {
                        int access$3800 = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType != 4) {
                            if (LoginActivitySmsView.this.nextType != 3) {
                                if (LoginActivitySmsView.this.nextType == 2) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0446R.string.SmsText, Integer.valueOf(r0), Integer.valueOf(access$3800)));
                                }
                                if (LoginActivitySmsView.this.progressView != null) {
                                    LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                    return;
                                }
                                return;
                            }
                        }
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0446R.string.CallText, Integer.valueOf(r0), Integer.valueOf(access$3800)));
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
                            ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new C19551(), 2);
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

            C09287() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTimeMillis = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTimeMillis - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                    AndroidUtilities.runOnUIThread(new C09271());
                }
            }
        }

        public LoginActivitySmsView(ChangePhoneActivity changePhoneActivity, Context context, int i) {
            View frameLayout;
            final ChangePhoneActivity changePhoneActivity2 = changePhoneActivity;
            Context context2 = context;
            this.this$0 = changePhoneActivity2;
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
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;
                    r6 = r6.nextPressed;
                    if (r6 == 0) goto L_0x0009;
                L_0x0008:
                    return;
                L_0x0009:
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;
                    r6 = r6.nextType;
                    if (r6 == 0) goto L_0x0021;
                L_0x0011:
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;
                    r6 = r6.nextType;
                    r0 = 4;
                    if (r6 == r0) goto L_0x0021;
                L_0x001a:
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;
                    r6.resendCode();
                    goto L_0x00fc;
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
                    r3 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r3 = r3.emailPhone;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r2 = r2.toString();	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x00ec }
                    r1 = "android.intent.extra.TEXT";	 Catch:{ Exception -> 0x00ec }
                    r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00ec }
                    r2.<init>();	 Catch:{ Exception -> 0x00ec }
                    r3 = "Phone: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r3 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
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
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.lastError;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = r2.toString();	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r1, r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.getContext();	 Catch:{ Exception -> 0x00ec }
                    r1 = "Send email...";	 Catch:{ Exception -> 0x00ec }
                    r0 = android.content.Intent.createChooser(r0, r1);	 Catch:{ Exception -> 0x00ec }
                    r6.startActivity(r0);	 Catch:{ Exception -> 0x00ec }
                    goto L_0x00fc;
                L_0x00ec:
                    r6 = org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.this;
                    r6 = r6.this$0;
                    r0 = "NoMailInstalled";
                    r1 = NUM; // 0x7f0c0402 float:1.8611273E38 double:1.0530979054E-314;
                    r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                    org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r6, r0);
                L_0x00fc:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.3.onClick(android.view.View):void");
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

                /* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$4$1 */
                class C19531 implements RequestDelegate {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }

                    C19531() {
                    }
                }

                public void onClick(View view) {
                    view = new TL_auth_cancelCode();
                    view.phone_number = LoginActivitySmsView.this.requestPhone;
                    view.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                    ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(view, new C19531(), 2);
                    LoginActivitySmsView.this.onBackPressed();
                    LoginActivitySmsView.this.this$0.setPage(0, true, null, true);
                }
            });
        }

        private void resendCode() {
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            bundle.putString("ephone", this.emailPhone);
            bundle.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            final TLObject tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.requestPhone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LoginActivitySmsView.this.nextPressed = false;
                            if (tL_error == null) {
                                LoginActivitySmsView.this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
                            } else {
                                AlertsCreator.processError(LoginActivitySmsView.this.this$0.currentAccount, tL_error, LoginActivitySmsView.this.this$0, tL_auth_resendCode, new Object[0]);
                                if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
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
            return LocaleController.getString("YourCode", C0446R.string.YourCode);
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
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
                        createTimer();
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
                this.codeTimer.schedule(new C09256(), 0, 1000);
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
                this.timeTimer.schedule(new C09287(), 0, 1000);
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
                final TLObject tL_account_changePhone = new TL_account_changePhone();
                tL_account_changePhone.phone_number = this.requestPhone;
                tL_account_changePhone.phone_code = this.codeField.getText().toString();
                tL_account_changePhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_changePhone, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivitySmsView.this.this$0.needHideProgress();
                                LoginActivitySmsView.this.nextPressed = false;
                                if (tL_error == null) {
                                    User user = (User) tLObject;
                                    LoginActivitySmsView.this.destroyTimer();
                                    LoginActivitySmsView.this.destroyCodeTimer();
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).setCurrentUser(user);
                                    UserConfig.getInstance(LoginActivitySmsView.this.this$0.currentAccount).saveConfig(true);
                                    ArrayList arrayList = new ArrayList();
                                    arrayList.add(user);
                                    MessagesStorage.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUsersAndChats(arrayList, null, true, true);
                                    MessagesController.getInstance(LoginActivitySmsView.this.this$0.currentAccount).putUser(user, false);
                                    LoginActivitySmsView.this.this$0.finishFragment();
                                    NotificationCenter.getInstance(LoginActivitySmsView.this.this$0.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                                    return;
                                }
                                LoginActivitySmsView.this.lastError = tL_error.text;
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
                                    AlertsCreator.processError(LoginActivitySmsView.this.this$0.currentAccount, tL_error, LoginActivitySmsView.this.this$0, tL_account_changePhone, new Object[0]);
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
                            this.ignoreOnTextChange = true;
                            this.codeField.setText(i);
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

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public PhoneView(ChangePhoneActivity changePhoneActivity, Context context) {
            Object toUpperCase;
            String str;
            final ChangePhoneActivity changePhoneActivity2 = changePhoneActivity;
            Context context2 = context;
            this.this$0 = changePhoneActivity2;
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

                /* renamed from: org.telegram.ui.ChangePhoneActivity$PhoneView$1$1 */
                class C19571 implements CountrySelectActivityDelegate {

                    /* renamed from: org.telegram.ui.ChangePhoneActivity$PhoneView$1$1$1 */
                    class C09301 implements Runnable {
                        C09301() {
                        }

                        public void run() {
                            AndroidUtilities.showKeyboard(PhoneView.this.phoneField);
                        }
                    }

                    C19571() {
                    }

                    public void didSelectCountry(String str, String str2) {
                        PhoneView.this.selectCountry(str);
                        AndroidUtilities.runOnUIThread(new C09301(), 300);
                        PhoneView.this.phoneField.requestFocus();
                        PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                    }
                }

                public void onClick(View view) {
                    view = new CountrySelectActivity(true);
                    view.setCountrySelectActivityDelegate(new C19571());
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
                                PhoneView.this.ignoreOnTextChange = true;
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
                                    PhoneView.this.ignoreOnTextChange = true;
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(substring.substring(1, substring.length()));
                                    stringBuilder2.append(PhoneView.this.phoneField.getText().toString());
                                    charSequence = stringBuilder2.toString();
                                    EditTextBoldCursor access$400 = PhoneView.this.codeField;
                                    substring = substring.substring(0, 1);
                                    access$400.setText(substring);
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
                                    HintEditText access$200 = PhoneView.this.phoneField;
                                    if (str3 != null) {
                                        str = str3.replace('X', '\u2013');
                                    }
                                    access$200.setHintText(str);
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
                            HintEditText access$200 = PhoneView.this.phoneField;
                            if (editable > PhoneView.this.phoneField.length()) {
                                editable = PhoneView.this.phoneField.length();
                            }
                            access$200.setSelection(editable);
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
            r1.textView2.setText(LocaleController.getString("ChangePhoneHelp", C0446R.string.ChangePhoneHelp));
            r1.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            r1.textView2.setTextSize(1, 14.0f);
            r1.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            r1.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(r1.textView2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 28, 0, 10));
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
                        AndroidUtilities.showKeyboard(r1.phoneField);
                        r1.phoneField.requestFocus();
                        r1.phoneField.setSelection(r1.phoneField.length());
                    }
                    AndroidUtilities.showKeyboard(r1.codeField);
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
                AndroidUtilities.showKeyboard(r1.codeField);
                r1.codeField.requestFocus();
                return;
            }
            AndroidUtilities.showKeyboard(r1.phoneField);
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

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (this.ignoreSelection != null) {
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

        public void onNextPressed() {
            if (this.this$0.getParentActivity() != null) {
                if (!this.nextPressed) {
                    boolean z;
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    boolean z2 = (telephonyManager.getSimState() == 1 || telephonyManager.getPhoneType() == 0) ? false : true;
                    if (VERSION.SDK_INT < 23 || !z2) {
                        z = true;
                    } else {
                        z = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                        boolean z3 = this.this$0.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
                        if (this.this$0.checkPermissions) {
                            this.this$0.permissionsItems.clear();
                            if (!z) {
                                this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!z3) {
                                this.this$0.permissionsItems.add("android.permission.RECEIVE_SMS");
                                if (VERSION.SDK_INT >= 23) {
                                    this.this$0.permissionsItems.add("android.permission.READ_SMS");
                                }
                            }
                            if (!this.this$0.permissionsItems.isEmpty()) {
                                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                                if (!(globalMainSettings.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE"))) {
                                    if (!this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                                        this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[this.this$0.permissionsItems.size()]), 6);
                                        return;
                                    }
                                }
                                globalMainSettings.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                                if (this.this$0.permissionsItems.size() == 2) {
                                    builder.setMessage(LocaleController.getString("AllowReadCallAndSms", C0446R.string.AllowReadCallAndSms));
                                } else if (z3) {
                                    builder.setMessage(LocaleController.getString("AllowReadCall", C0446R.string.AllowReadCall));
                                } else {
                                    builder.setMessage(LocaleController.getString("AllowReadSms", C0446R.string.AllowReadSms));
                                }
                                this.this$0.permissionsDialog = this.this$0.showDialog(builder.create());
                                return;
                            }
                        }
                    }
                    if (this.countryState == 1) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
                    } else if (this.countryState == 2 && !BuildVars.DEBUG_VERSION) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("WrongCountry", C0446R.string.WrongCountry));
                    } else if (this.codeField.length() == 0) {
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                    } else {
                        final TLObject tL_account_sendChangePhoneCode = new TL_account_sendChangePhoneCode();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                        stringBuilder.append(this.codeField.getText());
                        stringBuilder.append(this.phoneField.getText());
                        String stripExceptNumbers = PhoneFormat.stripExceptNumbers(stringBuilder.toString());
                        tL_account_sendChangePhoneCode.phone_number = stripExceptNumbers;
                        z2 = z2 && z;
                        tL_account_sendChangePhoneCode.allow_flashcall = z2;
                        if (tL_account_sendChangePhoneCode.allow_flashcall) {
                            try {
                                Object line1Number = telephonyManager.getLine1Number();
                                if (TextUtils.isEmpty(line1Number)) {
                                    tL_account_sendChangePhoneCode.current_number = false;
                                } else {
                                    boolean z4;
                                    if (!stripExceptNumbers.contains(line1Number)) {
                                        if (!line1Number.contains(stripExceptNumbers)) {
                                            z4 = false;
                                            tL_account_sendChangePhoneCode.current_number = z4;
                                            if (!tL_account_sendChangePhoneCode.current_number) {
                                                tL_account_sendChangePhoneCode.allow_flashcall = false;
                                            }
                                        }
                                    }
                                    z4 = true;
                                    tL_account_sendChangePhoneCode.current_number = z4;
                                    if (tL_account_sendChangePhoneCode.current_number) {
                                        tL_account_sendChangePhoneCode.allow_flashcall = false;
                                    }
                                }
                            } catch (Throwable e) {
                                tL_account_sendChangePhoneCode.allow_flashcall = false;
                                FileLog.m3e(e);
                            }
                        }
                        final Bundle bundle = new Bundle();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("+");
                        stringBuilder2.append(this.codeField.getText());
                        stringBuilder2.append(this.phoneField.getText());
                        bundle.putString("phone", stringBuilder2.toString());
                        try {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("+");
                            stringBuilder2.append(PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()));
                            stringBuilder2.append(" ");
                            stringBuilder2.append(PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                            bundle.putString("ephone", stringBuilder2.toString());
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("+");
                            stringBuilder2.append(stripExceptNumbers);
                            bundle.putString("ephone", stringBuilder2.toString());
                        }
                        bundle.putString("phoneFormated", stripExceptNumbers);
                        this.nextPressed = true;
                        this.this$0.needShowProgress();
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_sendChangePhoneCode, new RequestDelegate() {
                            public void run(final TLObject tLObject, final TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        PhoneView.this.nextPressed = false;
                                        if (tL_error == null) {
                                            PhoneView.this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
                                        } else {
                                            AlertsCreator.processError(PhoneView.this.this$0.currentAccount, tL_error, PhoneView.this.this$0, tL_account_sendChangePhoneCode, bundle.getString("phone"));
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
            return LocaleController.getString("ChangePhoneNewNumber", C0446R.string.ChangePhoneNewNumber);
        }
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
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C19521());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        View frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(this, context);
        this.views[1] = new LoginActivitySmsView(this, context, 1);
        this.views[2] = new LoginActivitySmsView(this, context, 2);
        this.views[3] = new LoginActivitySmsView(this, context, 3);
        this.views[4] = new LoginActivitySmsView(this, context, 4);
        context = null;
        while (context < this.views.length) {
            this.views[context].setVisibility(context == null ? 0 : 8);
            frameLayout.addView(this.views[context], LayoutHelper.createFrame(-1, context == null ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
            context++;
        }
        this.actionBar.setTitle(this.views[0].getHeaderName());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 6) {
            this.checkPermissions = false;
            if (this.currentViewNum == 0) {
                this.views[this.currentViewNum].onNextPressed();
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && this.permissionsItems.isEmpty() == null) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
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
            return true;
        }
        this.views[this.currentViewNum].onBackPressed();
        setPage(0, true, null, true);
        return false;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void needShowProgress() {
        if (!(getParentActivity() == null || getParentActivity().isFinishing())) {
            if (this.progressDialog == null) {
                this.progressDialog = new AlertDialog(getParentActivity(), 1);
                this.progressDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
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

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        if (i == 3) {
            this.doneButton.setVisibility(8);
        } else {
            if (i == 0) {
                this.checkPermissions = true;
            }
            this.doneButton.setVisibility(0);
        }
        final SlideView slideView = this.views[this.currentViewNum];
        final SlideView slideView2 = this.views[i];
        this.currentViewNum = i;
        slideView2.setParams(bundle, false);
        this.actionBar.setTitle(slideView2.getHeaderName());
        slideView2.onShow();
        slideView2.setX((float) (z2 ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
        i = new AnimatorSet();
        i.setInterpolator(new AccelerateDecelerateInterpolator());
        i.setDuration(300);
        bundle = new Animator[2];
        String str = "translationX";
        float[] fArr = new float[1];
        fArr[0] = (float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x);
        bundle[0] = ObjectAnimator.ofFloat(slideView, str, fArr);
        bundle[1] = ObjectAnimator.ofFloat(slideView2, "translationX", new float[]{0.0f});
        i.playTogether(bundle);
        i.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                slideView2.setVisibility(0);
            }

            public void onAnimationEnd(Animator animator) {
                slideView.setVisibility(8);
                slideView.setX(0.0f);
            }
        });
        i.start();
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
        themeDescriptionArr[17] = new ThemeDescription(loginActivitySmsView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[18] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[19] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[20] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[21] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[22] = new ThemeDescription(loginActivitySmsView.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[23] = new ThemeDescription(loginActivitySmsView.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[24] = new ThemeDescription(loginActivitySmsView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[25] = new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[26] = new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[27] = new ThemeDescription(loginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[28] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[29] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[30] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[31] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[32] = new ThemeDescription(loginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[33] = new ThemeDescription(loginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[34] = new ThemeDescription(loginActivitySmsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[35] = new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[36] = new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[37] = new ThemeDescription(loginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[38] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[39] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[40] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[41] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[42] = new ThemeDescription(loginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[43] = new ThemeDescription(loginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[44] = new ThemeDescription(loginActivitySmsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[45] = new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[46] = new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[47] = new ThemeDescription(loginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[48] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[49] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[50] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[51] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[52] = new ThemeDescription(loginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[53] = new ThemeDescription(loginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[54] = new ThemeDescription(loginActivitySmsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[55] = new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[56] = new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        return themeDescriptionArr;
    }
}
