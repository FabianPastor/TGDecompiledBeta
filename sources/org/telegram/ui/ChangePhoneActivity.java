package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
import org.telegram.messenger.SmsReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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
import org.telegram.tgnet.TLRPC.TL_codeSettings;
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

public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    private boolean checkPermissions = true;
    private int currentViewNum = 0;
    private View doneButton;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems = new ArrayList();
    private AlertDialog progressDialog;
    private int scrollHeight;
    private SlideView[] views = new SlideView[5];

    public class LoginActivitySmsView extends SlideView implements NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
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
                    this.blackImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), Mode.MULTIPLY));
                    frameLayout.addView(this.blackImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.blueImageView = new ImageView(context);
                    this.blueImageView.setImageResource(R.drawable.sms_bubble);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionBackground"), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentAppCodeTitle", R.string.SentAppCodeTitle));
                } else {
                    this.blueImageView = new ImageView(context);
                    this.blueImageView.setImageResource(R.drawable.sms_code);
                    this.blueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionBackground"), Mode.MULTIPLY));
                    frameLayout.addView(this.blueImageView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setText(LocaleController.getString("SentSmsCodeTitle", R.string.SentSmsCodeTitle));
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
            this.timeText = new TextView(context, ChangePhoneActivity.this) {
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
            this.problemText = new TextView(context, ChangePhoneActivity.this) {
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.problemText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.problemText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
            this.problemText.setTextSize(1, 15.0f);
            this.problemText.setGravity(49);
            if (this.currentType == 1) {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", R.string.DidNotGetTheCodeSms));
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            }
            addView(this.problemText, LayoutHelper.createLinear(-2, -2, 49));
            this.problemText.setOnClickListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$new$0$ChangePhoneActivity$LoginActivitySmsView(View v) {
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
                        return;
                    } catch (Exception e) {
                        AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                        return;
                    }
                }
                resendCode();
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.currentType != 3 && this.blueImageView != null) {
                int innerHeight = ((this.blueImageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight()) + this.confirmTextView.getMeasuredHeight()) + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (ChangePhoneActivity.this.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else if (ChangePhoneActivity.this.scrollHeight > maxHeight) {
                    setMeasuredDimension(getMeasuredWidth(), maxHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), ChangePhoneActivity.this.scrollHeight);
                }
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
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
            ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$$Lambda$1(this, params, req), 2);
        }

        final /* synthetic */ void lambda$resendCode$3$ChangePhoneActivity$LoginActivitySmsView(Bundle params, TL_auth_resendCode req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$9(this, error, params, response, req));
        }

        final /* synthetic */ void lambda$null$2$ChangePhoneActivity$LoginActivitySmsView(TL_error error, Bundle params, TLObject response, TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                ChangePhoneActivity.this.fillNextCodeParams(params, (TL_auth_sentCode) response);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(ChangePhoneActivity.this.currentAccount, error, ChangePhoneActivity.this, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$10(this));
                }
            }
            ChangePhoneActivity.this.needHideProgress();
        }

        final /* synthetic */ void lambda$null$1$ChangePhoneActivity$LoginActivitySmsView(DialogInterface dialog1, int which) {
            onBackPressed(true);
            ChangePhoneActivity.this.lambda$createView$1$AudioSelectActivity();
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
            if (params != null) {
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
                        Drawable pressedDrawable = getResources().getDrawable(R.drawable.search_dark_activated).mutate();
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
                        this.codeField[a].setOnKeyListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$2(this, num));
                        this.codeField[a].setOnEditorActionListener(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$3(this));
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
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(1), Integer.valueOf(0)));
                        } else if (this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(1), Integer.valueOf(0)));
                        }
                        createTimer();
                    } else if (this.currentType == 2 && (this.nextType == 4 || this.nextType == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(2), Integer.valueOf(0)));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else if (this.currentType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(2), Integer.valueOf(0)));
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

        final /* synthetic */ boolean lambda$setParams$4$ChangePhoneActivity$LoginActivitySmsView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            this.codeField[num - 1].setSelection(this.codeField[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        final /* synthetic */ boolean lambda$setParams$5$ChangePhoneActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
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
                        AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$4$$Lambda$0(this));
                    }

                    final /* synthetic */ void lambda$run$0$ChangePhoneActivity$LoginActivitySmsView$4() {
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
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                this.timeTimer = new Timer();
                this.timeTimer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
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
                                            ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$5$1$$Lambda$0(this), 2);
                                        } else if (LoginActivitySmsView.this.nextType == 3) {
                                            AndroidUtilities.setWaitingForSms(false);
                                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                            LoginActivitySmsView.this.waitingForEvent = false;
                                            LoginActivitySmsView.this.destroyCodeTimer();
                                            LoginActivitySmsView.this.resendCode();
                                        }
                                    }
                                }

                                final /* synthetic */ void lambda$run$1$ChangePhoneActivity$LoginActivitySmsView$5$1(TLObject response, TL_error error) {
                                    if (error != null && error.text != null) {
                                        AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$5$1$$Lambda$1(this, error));
                                    }
                                }

                                final /* synthetic */ void lambda$null$0$ChangePhoneActivity$LoginActivitySmsView$5$1(TL_error error) {
                                    LoginActivitySmsView.this.lastError = error.text;
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
                TL_account_changePhone req = new TL_account_changePhone();
                req.phone_number = this.requestPhone;
                req.phone_code = code;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                ChangePhoneActivity.this.needShowProgress();
                ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$$Lambda$4(this, req), 2);
            }
        }

        final /* synthetic */ void lambda$onNextPressed$7$ChangePhoneActivity$LoginActivitySmsView(TL_account_changePhone req, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$8(this, error, response, req));
        }

        final /* synthetic */ void lambda$null$6$ChangePhoneActivity$LoginActivitySmsView(TL_error error, TLObject response, TL_account_changePhone req) {
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
                ChangePhoneActivity.this.lambda$createView$1$AudioSelectActivity();
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
            if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                for (EditTextBoldCursor text : this.codeField) {
                    text.setText("");
                }
                this.codeField[0].requestFocus();
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                ChangePhoneActivity.this.setPage(0, true, null, true);
            }
        }

        public boolean onBackPressed(boolean force) {
            if (force) {
                TL_auth_cancelCode req = new TL_auth_cancelCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(ChangePhoneActivity.this.currentAccount).sendRequest(req, ChangePhoneActivity$LoginActivitySmsView$$Lambda$6.$instance, 10);
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
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new ChangePhoneActivity$LoginActivitySmsView$$Lambda$5(this));
            ChangePhoneActivity.this.showDialog(builder.create());
            return false;
        }

        final /* synthetic */ void lambda$onBackPressed$8$ChangePhoneActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            ChangePhoneActivity.this.setPage(0, true, null, true);
        }

        static final /* synthetic */ void lambda$onBackPressed$9$ChangePhoneActivity$LoginActivitySmsView(TLObject response, TL_error error) {
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
                AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$Lambda$7(this), 100);
            }
        }

        final /* synthetic */ void lambda$onShow$10$ChangePhoneActivity$LoginActivitySmsView() {
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
                        this.ignoreOnTextChange = true;
                        this.codeField[0].setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed();
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
            this.countryButton.setBackgroundResource(R.drawable.spinner_states);
            addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 0.0f, 0.0f, 14.0f));
            this.countryButton.setOnClickListener(new ChangePhoneActivity$PhoneView$$Lambda$0(this));
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
            this.codeField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(NUM);
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
                                    EditTextBoldCursor access$400 = PhoneView.this.codeField;
                                    text = text.substring(0, 1);
                                    access$400.setText(text);
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
                        String phoneChars = "NUM";
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
                            HintEditText access$600 = PhoneView.this.phoneField;
                            if (start > PhoneView.this.phoneField.length()) {
                                start = PhoneView.this.phoneField.length();
                            }
                            access$600.setSelection(start);
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
            this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.textView2.setTextSize(1, 14.0f);
            this.textView2.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
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
                FileLog.e(e);
            }
            Collections.sort(this.countriesArray, ChangePhoneActivity$PhoneView$$Lambda$4.$instance);
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    country = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Throwable e2) {
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
                SharedPreferences preferences;
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
                boolean allowCall = true;
                if (VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    if (ChangePhoneActivity.this.checkPermissions) {
                        ChangePhoneActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!ChangePhoneActivity.this.permissionsItems.isEmpty()) {
                            preferences = MessagesController.getGlobalMainSettings();
                            if (preferences.getBoolean("firstlogin", true) || ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                Builder builder = new Builder(ChangePhoneActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
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
                    String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    req.phone_number = phone;
                    req.settings = new TL_codeSettings();
                    TL_codeSettings tL_codeSettings = req.settings;
                    boolean z = simcardAvailable && allowCall;
                    tL_codeSettings.allow_flashcall = z;
                    if (VERSION.SDK_INT >= 26) {
                        try {
                            req.settings.app_hash = SmsManager.getDefault().createAppSpecificSmsToken(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, SmsReceiver.class), NUM));
                        } catch (Throwable e) {
                            FileLog.e(e);
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
                            if (TextUtils.isEmpty(number)) {
                                req.settings.current_number = false;
                            } else {
                                tL_codeSettings = req.settings;
                                z = phone.contains(number) || number.contains(phone);
                                tL_codeSettings.current_number = z;
                                if (!req.settings.current_number) {
                                    req.settings.allow_flashcall = false;
                                }
                            }
                        } catch (Throwable e2) {
                            req.settings.allow_flashcall = false;
                            FileLog.e(e2);
                        }
                    }
                    Bundle params = new Bundle();
                    params.putString("phone", "+" + this.codeField.getText() + " " + this.phoneField.getText());
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Throwable e22) {
                        FileLog.e(e22);
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

        protected void onDraw(Canvas canvas) {
            int start = (int) (((float) getMeasuredWidth()) * this.progress);
            canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
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
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
                } else if (id == -1) {
                    ChangePhoneActivity.this.lambda$createView$1$AudioSelectActivity();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        ScrollView scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (ChangePhoneActivity.this.currentViewNum == 1 || ChangePhoneActivity.this.currentViewNum == 2 || ChangePhoneActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                ChangePhoneActivity.this.scrollHeight = MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
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
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog.setCanCacnel(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
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
        int a;
        PhoneView phoneView = this.views[0];
        LoginActivitySmsView smsView1 = this.views[1];
        LoginActivitySmsView smsView2 = this.views[2];
        LoginActivitySmsView smsView3 = this.views[3];
        LoginActivitySmsView smsView4 = this.views[4];
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
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
