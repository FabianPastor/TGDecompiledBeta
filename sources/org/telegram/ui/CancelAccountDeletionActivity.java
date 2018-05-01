package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SlideView;

public class CancelAccountDeletionActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = false;
    private int currentViewNum = 0;
    private View doneButton;
    private Dialog errorDialog;
    private String hash;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList();
    private String phone;
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

    /* renamed from: org.telegram.ui.CancelAccountDeletionActivity$1 */
    class C19361 extends ActionBarMenuOnItemClick {
        C19361() {
        }

        public void onItemClick(int i) {
            if (i == 1) {
                CancelAccountDeletionActivity.this.views[CancelAccountDeletionActivity.this.currentViewNum].onNextPressed();
            } else if (i == -1) {
                CancelAccountDeletionActivity.this.finishFragment();
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
        final /* synthetic */ CancelAccountDeletionActivity this$0;
        private volatile int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private boolean waitingForEvent;

        /* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$5 */
        class C08625 extends TimerTask {

            /* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$5$1 */
            class C08611 implements Runnable {
                C08611() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.codeTime <= 1000) {
                        LoginActivitySmsView.this.problemText.setVisibility(0);
                        LoginActivitySmsView.this.destroyCodeTimer();
                    }
                }
            }

            C08625() {
            }

            public void run() {
                double currentTimeMillis = (double) System.currentTimeMillis();
                LoginActivitySmsView.this.codeTime = (int) (((double) LoginActivitySmsView.this.codeTime) - (currentTimeMillis - LoginActivitySmsView.this.lastCodeTime));
                LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C08611());
            }
        }

        /* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$6 */
        class C08656 extends TimerTask {

            /* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$6$1 */
            class C08641 implements Runnable {

                /* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$6$1$1 */
                class C19381 implements RequestDelegate {
                    C19381() {
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

                C08641() {
                }

                public void run() {
                    if (LoginActivitySmsView.this.time >= 1000) {
                        int access$2500 = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                        if (LoginActivitySmsView.this.nextType != 4) {
                            if (LoginActivitySmsView.this.nextType != 3) {
                                if (LoginActivitySmsView.this.nextType == 2) {
                                    LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", C0446R.string.SmsText, Integer.valueOf(r0), Integer.valueOf(access$2500)));
                                }
                                if (LoginActivitySmsView.this.progressView != null) {
                                    LoginActivitySmsView.this.progressView.setProgress(1.0f - (((float) LoginActivitySmsView.this.time) / ((float) LoginActivitySmsView.this.timeout)));
                                    return;
                                }
                                return;
                            }
                        }
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", C0446R.string.CallText, Integer.valueOf(r0), Integer.valueOf(access$2500)));
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
                            tL_auth_resendCode.phone_number = LoginActivitySmsView.this.phone;
                            tL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                            ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new C19381(), 2);
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

            C08656() {
            }

            public void run() {
                if (LoginActivitySmsView.this.timeTimer != null) {
                    double currentTimeMillis = (double) System.currentTimeMillis();
                    LoginActivitySmsView.this.time = (int) (((double) LoginActivitySmsView.this.time) - (currentTimeMillis - LoginActivitySmsView.this.lastCurrentTime));
                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                    AndroidUtilities.runOnUIThread(new C08641());
                }
            }
        }

        public LoginActivitySmsView(CancelAccountDeletionActivity cancelAccountDeletionActivity, Context context, int i) {
            final CancelAccountDeletionActivity cancelAccountDeletionActivity2 = cancelAccountDeletionActivity;
            Context context2 = context;
            this.this$0 = cancelAccountDeletionActivity2;
            super(context2);
            this.currentType = i;
            setOrientation(1);
            this.confirmTextView = new TextView(context2);
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.confirmTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            if (r0.currentType == 3) {
                View frameLayout = new FrameLayout(context2);
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
            r0.codeField.setCursorWidth(1.5f);
            r0.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.codeField.setImeOptions(268435461);
            r0.codeField.setTextSize(1, 18.0f);
            r0.codeField.setInputType(3);
            r0.codeField.setMaxLines(1);
            r0.codeField.setPadding(0, 0, 0, 0);
            r0.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
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
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;
                    r6 = r6.nextPressed;
                    if (r6 == 0) goto L_0x0009;
                L_0x0008:
                    return;
                L_0x0009:
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;
                    r6 = r6.nextType;
                    if (r6 == 0) goto L_0x0021;
                L_0x0011:
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;
                    r6 = r6.nextType;
                    r0 = 4;
                    if (r6 == r0) goto L_0x0021;
                L_0x001a:
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;
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
                    r3 = "Android cancel account deletion issue ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r3 = " ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r3 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r3 = r3.phone;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r2 = r2.toString();	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x00ec }
                    r1 = "android.intent.extra.TEXT";	 Catch:{ Exception -> 0x00ec }
                    r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00ec }
                    r2.<init>();	 Catch:{ Exception -> 0x00ec }
                    r3 = "Phone: ";	 Catch:{ Exception -> 0x00ec }
                    r2.append(r3);	 Catch:{ Exception -> 0x00ec }
                    r3 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r3 = r3.phone;	 Catch:{ Exception -> 0x00ec }
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
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.lastError;	 Catch:{ Exception -> 0x00ec }
                    r2.append(r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = r2.toString();	 Catch:{ Exception -> 0x00ec }
                    r0.putExtra(r1, r6);	 Catch:{ Exception -> 0x00ec }
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;	 Catch:{ Exception -> 0x00ec }
                    r6 = r6.getContext();	 Catch:{ Exception -> 0x00ec }
                    r1 = "Send email...";	 Catch:{ Exception -> 0x00ec }
                    r0 = android.content.Intent.createChooser(r0, r1);	 Catch:{ Exception -> 0x00ec }
                    r6.startActivity(r0);	 Catch:{ Exception -> 0x00ec }
                    goto L_0x00fc;
                L_0x00ec:
                    r6 = org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.this;
                    r6 = r6.this$0;
                    r0 = "NoMailInstalled";
                    r1 = NUM; // 0x7f0c0402 float:1.8611273E38 double:1.0530979054E-314;
                    r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                    org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r6, r0);
                L_0x00fc:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.3.onClick(android.view.View):void");
                }
            });
        }

        private void resendCode() {
            final Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            final TLObject tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.phone;
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
                            }
                            LoginActivitySmsView.this.this$0.needHideProgress();
                        }
                    });
                }
            }, 2);
        }

        public String getHeaderName() {
            return LocaleController.getString("CancelAccountReset", C0446R.string.CancelAccountReset);
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
                    Object[] objArr = new Object[1];
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(bundle);
                    objArr[0] = instance.format(stringBuilder.toString());
                    this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo", C0446R.string.CancelAccountResetInfo, objArr)));
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
                this.codeTimer.schedule(new C08625(), 0, 1000);
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
                this.timeTimer.schedule(new C08656(), 0, 1000);
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
                final TLObject tL_account_confirmPhone = new TL_account_confirmPhone();
                tL_account_confirmPhone.phone_code = this.codeField.getText().toString();
                tL_account_confirmPhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_confirmPhone, new RequestDelegate() {
                    public void run(TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LoginActivitySmsView.this.this$0.needHideProgress();
                                LoginActivitySmsView.this.nextPressed = false;
                                if (tL_error == null) {
                                    CancelAccountDeletionActivity cancelAccountDeletionActivity = LoginActivitySmsView.this.this$0;
                                    BaseFragment baseFragment = LoginActivitySmsView.this.this$0;
                                    Object[] objArr = new Object[1];
                                    PhoneFormat instance = PhoneFormat.getInstance();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("+");
                                    stringBuilder.append(LoginActivitySmsView.this.phone);
                                    objArr[0] = instance.format(stringBuilder.toString());
                                    cancelAccountDeletionActivity.errorDialog = AlertsCreator.showSimpleAlert(baseFragment, LocaleController.formatString("CancelLinkSuccess", C0446R.string.CancelLinkSuccess, objArr));
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
                                    AlertsCreator.processError(LoginActivitySmsView.this.this$0.currentAccount, tL_error, LoginActivitySmsView.this.this$0, tL_account_confirmPhone, new Object[0]);
                                }
                            }
                        });
                    }
                }, 2);
            }
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

    public class PhoneView extends SlideView {
        private boolean nextPressed = null;
        private RadialProgressView progressBar;

        public PhoneView(Context context) {
            super(context);
            setOrientation(1);
            CancelAccountDeletionActivity frameLayout = new FrameLayout(context);
            addView(frameLayout, LayoutHelper.createLinear(-1, Callback.DEFAULT_DRAG_ANIMATION_DURATION));
            this.progressBar = new RadialProgressView(context);
            frameLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }

        public void onNextPressed() {
            if (CancelAccountDeletionActivity.this.getParentActivity() != null) {
                if (!this.nextPressed) {
                    int phoneType;
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager.getSimState() != 1) {
                        phoneType = telephonyManager.getPhoneType();
                    }
                    phoneType = VERSION.SDK_INT;
                    final TLObject tL_account_sendConfirmPhoneCode = new TL_account_sendConfirmPhoneCode();
                    tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                    tL_account_sendConfirmPhoneCode.hash = CancelAccountDeletionActivity.this.hash;
                    if (tL_account_sendConfirmPhoneCode.allow_flashcall) {
                        try {
                            Object line1Number = telephonyManager.getLine1Number();
                            if (TextUtils.isEmpty(line1Number)) {
                                tL_account_sendConfirmPhoneCode.current_number = false;
                            } else {
                                boolean z;
                                if (!CancelAccountDeletionActivity.this.phone.contains(line1Number)) {
                                    if (!line1Number.contains(CancelAccountDeletionActivity.this.phone)) {
                                        z = false;
                                        tL_account_sendConfirmPhoneCode.current_number = z;
                                        if (!tL_account_sendConfirmPhoneCode.current_number) {
                                            tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                                        }
                                    }
                                }
                                z = true;
                                tL_account_sendConfirmPhoneCode.current_number = z;
                                if (tL_account_sendConfirmPhoneCode.current_number) {
                                    tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                                }
                            }
                        } catch (Throwable e) {
                            tL_account_sendConfirmPhoneCode.allow_flashcall = false;
                            FileLog.m3e(e);
                        }
                    }
                    final Bundle bundle = new Bundle();
                    bundle.putString("phone", CancelAccountDeletionActivity.this.phone);
                    this.nextPressed = true;
                    ConnectionsManager.getInstance(CancelAccountDeletionActivity.this.currentAccount).sendRequest(tL_account_sendConfirmPhoneCode, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    PhoneView.this.nextPressed = false;
                                    if (tL_error == null) {
                                        CancelAccountDeletionActivity.this.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
                                    } else {
                                        CancelAccountDeletionActivity.this.errorDialog = AlertsCreator.processError(CancelAccountDeletionActivity.this.currentAccount, tL_error, CancelAccountDeletionActivity.this, tL_account_sendConfirmPhoneCode, new Object[0]);
                                    }
                                }
                            });
                        }
                    }, 2);
                }
            }
        }

        public String getHeaderName() {
            return LocaleController.getString("CancelAccountReset", C0446R.string.CancelAccountReset);
        }

        public void onShow() {
            super.onShow();
            onNextPressed();
        }
    }

    public CancelAccountDeletionActivity(Bundle bundle) {
        super(bundle);
        this.hash = bundle.getString("hash");
        this.phone = bundle.getString("phone");
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
        this.actionBar.setActionBarMenuOnItemClick(new C19361());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(8);
        this.fragmentView = new ScrollView(context);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        View frameLayout = new FrameLayout(context);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(context);
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
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
        }
        if (dialog == this.errorDialog) {
            finishFragment();
        }
    }

    public boolean onBackPressed() {
        for (int i = 0; i < this.views.length; i++) {
            if (this.views[i] != null) {
                this.views[i].onDestroyActivity();
            }
        }
        return true;
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
        final SlideView slideView;
        final SlideView slideView2;
        String str;
        float[] fArr;
        if (i != 3) {
            if (i != 0) {
                this.doneButton.setVisibility(0);
                slideView = this.views[this.currentViewNum];
                slideView2 = this.views[i];
                this.currentViewNum = i;
                slideView2.setParams(bundle, false);
                this.actionBar.setTitle(slideView2.getHeaderName());
                slideView2.onShow();
                slideView2.setX((float) (z2 ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
                i = new AnimatorSet();
                i.setInterpolator(new AccelerateDecelerateInterpolator());
                i.setDuration(300);
                bundle = new Animator[2];
                str = "translationX";
                fArr = new float[1];
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
        }
        this.doneButton.setVisibility(8);
        slideView = this.views[this.currentViewNum];
        slideView2 = this.views[i];
        this.currentViewNum = i;
        slideView2.setParams(bundle, false);
        this.actionBar.setTitle(slideView2.getHeaderName());
        slideView2.onShow();
        if (z2) {
        }
        slideView2.setX((float) (z2 ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
        i = new AnimatorSet();
        i.setInterpolator(new AccelerateDecelerateInterpolator());
        i.setDuration(300);
        bundle = new Animator[2];
        str = "translationX";
        fArr = new float[1];
        if (z2) {
        }
        fArr[0] = (float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x);
        bundle[0] = ObjectAnimator.ofFloat(slideView, str, fArr);
        bundle[1] = ObjectAnimator.ofFloat(slideView2, "translationX", new float[]{0.0f});
        i.playTogether(bundle);
        i.addListener(/* anonymous class already generated */);
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[43];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(phoneView.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[7] = new ThemeDescription(loginActivitySmsView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[8] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[9] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[10] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[11] = new ThemeDescription(loginActivitySmsView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[12] = new ThemeDescription(loginActivitySmsView.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[13] = new ThemeDescription(loginActivitySmsView.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[14] = new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[15] = new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[16] = new ThemeDescription(loginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[17] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[18] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[19] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[20] = new ThemeDescription(loginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[21] = new ThemeDescription(loginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[22] = new ThemeDescription(loginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[23] = new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[24] = new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[25] = new ThemeDescription(loginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[26] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[27] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[28] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[29] = new ThemeDescription(loginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[30] = new ThemeDescription(loginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[31] = new ThemeDescription(loginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[32] = new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[33] = new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        themeDescriptionArr[34] = new ThemeDescription(loginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[35] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[36] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[37] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[38] = new ThemeDescription(loginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[39] = new ThemeDescription(loginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[40] = new ThemeDescription(loginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[41] = new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressInner);
        themeDescriptionArr[42] = new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, Theme.key_login_progressOuter);
        return themeDescriptionArr;
    }
}
