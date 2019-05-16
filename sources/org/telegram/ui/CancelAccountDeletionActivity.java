package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC.auth_CodeType;
import org.telegram.tgnet.TLRPC.auth_SentCodeType;
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
    private int scrollHeight;
    private SlideView[] views = new SlideView[5];

    private class ProgressView extends View {
        private Paint paint = new Paint();
        private Paint paint2 = new Paint();
        private float progress;

        public ProgressView(Context context) {
            super(context);
            this.paint.setColor(Theme.getColor("login_progressInner"));
            this.paint2.setColor(Theme.getColor("login_progressOuter"));
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float measuredWidth = (float) ((int) (((float) getMeasuredWidth()) * this.progress));
            canvas.drawRect(0.0f, 0.0f, measuredWidth, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect(measuredWidth, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

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
        final /* synthetic */ CancelAccountDeletionActivity this$0;
        private int time = 60000;
        private TextView timeText;
        private Timer timeTimer;
        private int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        private boolean waitingForEvent;

        public boolean needBackButton() {
            return true;
        }

        public LoginActivitySmsView(CancelAccountDeletionActivity cancelAccountDeletionActivity, Context context, int i) {
            final CancelAccountDeletionActivity cancelAccountDeletionActivity2 = cancelAccountDeletionActivity;
            Context context2 = context;
            this.this$0 = cancelAccountDeletionActivity2;
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
            this.problemText.setOnClickListener(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$72NXgFF_13BImj3ROsBvF0l8AmQ(this));
        }

        public /* synthetic */ void lambda$new$0$CancelAccountDeletionActivity$LoginActivitySmsView(View view) {
            if (!this.nextPressed) {
                Object obj = ((this.nextType == 4 && this.currentType == 2) || this.nextType == 0) ? 1 : null;
                if (obj == null) {
                    resendCode();
                } else {
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        String format = String.format(Locale.US, "%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("message/rfCLASSNAME");
                        intent.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Android cancel account deletion issue ");
                        stringBuilder.append(format);
                        stringBuilder.append(" ");
                        stringBuilder.append(this.phone);
                        intent.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Phone: ");
                        stringBuilder.append(this.phone);
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
                        AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("NoMailInstalled", NUM));
                    }
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

        private void resendCode() {
            Bundle bundle = new Bundle();
            bundle.putString("phone", this.phone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TL_auth_resendCode tL_auth_resendCode = new TL_auth_resendCode();
            tL_auth_resendCode.phone_number = this.phone;
            tL_auth_resendCode.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$ymkWVQFIhlznIih0Xq953phiTOo(this, bundle, tL_auth_resendCode), 2);
        }

        public /* synthetic */ void lambda$resendCode$3$CancelAccountDeletionActivity$LoginActivitySmsView(Bundle bundle, TL_auth_resendCode tL_auth_resendCode, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$6BL0ZFjSY7z5vTmWlXT9OO5buPM(this, tL_error, bundle, tLObject, tL_auth_resendCode));
        }

        public /* synthetic */ void lambda$null$2$CancelAccountDeletionActivity$LoginActivitySmsView(TL_error tL_error, Bundle bundle, TLObject tLObject, TL_auth_resendCode tL_auth_resendCode) {
            this.nextPressed = false;
            if (tL_error == null) {
                this.this$0.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
            } else if (tL_error.text != null) {
                AlertDialog alertDialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, tL_error, this.this$0, tL_auth_resendCode, new Object[0]);
                if (alertDialog != null && tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                    alertDialog.setPositiveButtonListener(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$4p7iOR1S-omGhM3JTRpI2lxpfeQ(this));
                }
            }
            this.this$0.needHideProgress();
        }

        public /* synthetic */ void lambda$null$1$CancelAccountDeletionActivity$LoginActivitySmsView(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.finishFragment();
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("CancelAccountReset", NUM);
        }

        public void setParams(Bundle bundle, boolean z) {
            if (bundle != null) {
                int i;
                String str;
                this.waitingForEvent = true;
                int i2 = this.currentType;
                if (i2 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.phoneHash = bundle.getString("phoneHash");
                i2 = bundle.getInt("timeout");
                this.time = i2;
                this.timeout = i2;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                this.length = bundle.getInt("length");
                if (this.length == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                i2 = 8;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    i = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (i >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[i].setText("");
                        i++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    i = 0;
                    while (i < this.length) {
                        this.codeField[i] = new EditTextBoldCursor(getContext());
                        str = "windowBackgroundWhiteBlackText";
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
                        this.codeField[i].setOnKeyListener(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$ZA7-mAFfTFMlQCzBbBXS9uV3KIM(this, i));
                        this.codeField[i].setOnEditorActionListener(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$jgzzj4-t91kdP6uXqtjWCi0O3d8(this));
                        i++;
                    }
                }
                ProgressView progressView = this.progressView;
                if (progressView != null) {
                    progressView.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String format = PhoneFormat.getInstance().format(this.phone);
                    Object[] objArr = new Object[1];
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(format);
                    objArr[0] = instance.format(stringBuilder.toString());
                    this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo", NUM, objArr)));
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
                        str = "SmsText";
                        String str2 = "CallText";
                        if (i == 3) {
                            i = this.nextType;
                            if (i == 4 || i == 2) {
                                this.problemText.setVisibility(8);
                                this.timeText.setVisibility(0);
                                i = this.nextType;
                                if (i == 4) {
                                    this.timeText.setText(LocaleController.formatString(str2, NUM, Integer.valueOf(1), Integer.valueOf(0)));
                                } else if (i == 2) {
                                    this.timeText.setText(LocaleController.formatString(str, NUM, Integer.valueOf(1), Integer.valueOf(0)));
                                }
                                createTimer();
                            }
                        }
                        if (this.currentType == 2) {
                            i = this.nextType;
                            if (i == 4 || i == 3) {
                                this.timeText.setText(LocaleController.formatString(str2, NUM, Integer.valueOf(2), Integer.valueOf(0)));
                                this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                                textView = this.timeText;
                                if (this.time >= 1000) {
                                    i2 = 0;
                                }
                                textView.setVisibility(i2);
                                createTimer();
                            }
                        }
                        if (this.currentType == 4 && this.nextType == 2) {
                            this.timeText.setText(LocaleController.formatString(str, NUM, Integer.valueOf(2), Integer.valueOf(0)));
                            this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                            textView = this.timeText;
                            if (this.time >= 1000) {
                                i2 = 0;
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
        }

        public /* synthetic */ boolean lambda$setParams$4$CancelAccountDeletionActivity$LoginActivitySmsView(int i, View view, int i2, KeyEvent keyEvent) {
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

        public /* synthetic */ boolean lambda$setParams$5$CancelAccountDeletionActivity$LoginActivitySmsView(TextView textView, int i, KeyEvent keyEvent) {
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
                        AndroidUtilities.runOnUIThread(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$4$30Wvb2875vVECuq8Lqz3EDACKYg(this));
                    }

                    public /* synthetic */ void lambda$run$0$CancelAccountDeletionActivity$LoginActivitySmsView$4() {
                        double currentTimeMillis = (double) System.currentTimeMillis();
                        double access$1400 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTimeMillis);
                        access$1400 = currentTimeMillis - access$1400;
                        LoginActivitySmsView.this.lastCodeTime = currentTimeMillis;
                        LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                        double access$1500 = (double) loginActivitySmsView.codeTime;
                        Double.isNaN(access$1500);
                        loginActivitySmsView.codeTime = (int) (access$1500 - access$1400);
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
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    double currentTimeMillis = (double) System.currentTimeMillis();
                                    double access$2000 = LoginActivitySmsView.this.lastCurrentTime;
                                    Double.isNaN(currentTimeMillis);
                                    access$2000 = currentTimeMillis - access$2000;
                                    LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                                    double access$2100 = (double) loginActivitySmsView.time;
                                    Double.isNaN(access$2100);
                                    loginActivitySmsView.time = (int) (access$2100 - access$2000);
                                    LoginActivitySmsView.this.lastCurrentTime = currentTimeMillis;
                                    if (LoginActivitySmsView.this.time >= 1000) {
                                        int access$21002 = (LoginActivitySmsView.this.time / 1000) - (((LoginActivitySmsView.this.time / 1000) / 60) * 60);
                                        if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3) {
                                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", NUM, Integer.valueOf(r0), Integer.valueOf(access$21002)));
                                        } else if (LoginActivitySmsView.this.nextType == 2) {
                                            LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", NUM, Integer.valueOf(r0), Integer.valueOf(access$21002)));
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
                                            TL_auth_resendCode tL_auth_resendCode = new TL_auth_resendCode();
                                            tL_auth_resendCode.phone_number = LoginActivitySmsView.this.phone;
                                            tL_auth_resendCode.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                            ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(tL_auth_resendCode, new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$5$1$TKosr_VcccEYaoxCJjoMXxLoY3g(this), 2);
                                        } else if (LoginActivitySmsView.this.nextType == 3) {
                                            AndroidUtilities.setWaitingForSms(false);
                                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                                            LoginActivitySmsView.this.waitingForEvent = false;
                                            LoginActivitySmsView.this.destroyCodeTimer();
                                            LoginActivitySmsView.this.resendCode();
                                        }
                                    }
                                }

                                public /* synthetic */ void lambda$run$1$CancelAccountDeletionActivity$LoginActivitySmsView$5$1(TLObject tLObject, TL_error tL_error) {
                                    if (tL_error != null && tL_error.text != null) {
                                        AndroidUtilities.runOnUIThread(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$5$1$z9uNBVal4-U1kKMzrr3JL-AbWI4(this, tL_error));
                                    }
                                }

                                public /* synthetic */ void lambda$null$0$CancelAccountDeletionActivity$LoginActivitySmsView$5$1(TL_error tL_error) {
                                    LoginActivitySmsView.this.lastError = tL_error.text;
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
            if (!this.nextPressed) {
                String code = getCode();
                if (TextUtils.isEmpty(code)) {
                    AndroidUtilities.shakeView(this.codeFieldContainer, 2.0f, 0);
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
                TL_account_confirmPhone tL_account_confirmPhone = new TL_account_confirmPhone();
                tL_account_confirmPhone.phone_code = code;
                tL_account_confirmPhone.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_account_confirmPhone, new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$1W4kZzPjzRDtXCpazovyaTPi9gk(this, tL_account_confirmPhone), 2);
            }
        }

        public /* synthetic */ void lambda$onNextPressed$7$CancelAccountDeletionActivity$LoginActivitySmsView(TL_account_confirmPhone tL_account_confirmPhone, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$iU1Eg1dGYY6sRGDfzMe2rfeuHuA(this, tL_error, tL_account_confirmPhone));
        }

        /* JADX WARNING: Removed duplicated region for block: B:21:0x0075  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0068  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00cf A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5 A:{LOOP_END, LOOP:0: B:34:0x00c0->B:36:0x00c5} */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0068  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0075  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00a0  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5 A:{LOOP_END, LOOP:0: B:34:0x00c0->B:36:0x00c5} */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00cf A:{SYNTHETIC} */
        /* JADX WARNING: Missing block: B:7:0x004d, code skipped:
            if (r2 != 2) goto L_0x004f;
     */
        /* JADX WARNING: Missing block: B:12:0x0057, code skipped:
            if (r2 != 3) goto L_0x0059;
     */
        /* JADX WARNING: Missing block: B:16:0x005f, code skipped:
            if (r6.nextType == 2) goto L_0x0061;
     */
        public /* synthetic */ void lambda$null$6$CancelAccountDeletionActivity$LoginActivitySmsView(org.telegram.tgnet.TLRPC.TL_error r7, org.telegram.tgnet.TLRPC.TL_account_confirmPhone r8) {
            /*
            r6 = this;
            r0 = r6.this$0;
            r0.needHideProgress();
            r0 = 0;
            r6.nextPressed = r0;
            r1 = 1;
            if (r7 != 0) goto L_0x003e;
        L_0x000b:
            r7 = r6.this$0;
            r8 = NUM; // 0x7f0d01e2 float:1.8743092E38 double:1.0531300157E-314;
            r1 = new java.lang.Object[r1];
            r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "+";
            r3.append(r4);
            r4 = r6.phone;
            r3.append(r4);
            r3 = r3.toString();
            r2 = r2.format(r3);
            r1[r0] = r2;
            r0 = "CancelLinkSuccess";
            r8 = org.telegram.messenger.LocaleController.formatString(r0, r8, r1);
            r8 = org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r7, r8);
            r7.errorDialog = r8;
            goto L_0x00d4;
        L_0x003e:
            r2 = r7.text;
            r6.lastError = r2;
            r2 = r6.currentType;
            r3 = 4;
            r4 = 2;
            r5 = 3;
            if (r2 != r5) goto L_0x004f;
        L_0x0049:
            r2 = r6.nextType;
            if (r2 == r3) goto L_0x0061;
        L_0x004d:
            if (r2 == r4) goto L_0x0061;
        L_0x004f:
            r2 = r6.currentType;
            if (r2 != r4) goto L_0x0059;
        L_0x0053:
            r2 = r6.nextType;
            if (r2 == r3) goto L_0x0061;
        L_0x0057:
            if (r2 == r5) goto L_0x0061;
        L_0x0059:
            r2 = r6.currentType;
            if (r2 != r3) goto L_0x0064;
        L_0x005d:
            r2 = r6.nextType;
            if (r2 != r4) goto L_0x0064;
        L_0x0061:
            r6.createTimer();
        L_0x0064:
            r2 = r6.currentType;
            if (r2 != r4) goto L_0x0075;
        L_0x0068:
            org.telegram.messenger.AndroidUtilities.setWaitingForSms(r1);
            r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r3 = org.telegram.messenger.NotificationCenter.didReceiveSmsCode;
            r2.addObserver(r6, r3);
            goto L_0x0083;
        L_0x0075:
            if (r2 != r5) goto L_0x0083;
        L_0x0077:
            org.telegram.messenger.AndroidUtilities.setWaitingForCall(r1);
            r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r3 = org.telegram.messenger.NotificationCenter.didReceiveCall;
            r2.addObserver(r6, r3);
        L_0x0083:
            r6.waitingForEvent = r1;
            r2 = r6.currentType;
            if (r2 == r5) goto L_0x0096;
        L_0x0089:
            r2 = r6.this$0;
            r2 = r2.currentAccount;
            r3 = r6.this$0;
            r4 = new java.lang.Object[r0];
            org.telegram.ui.Components.AlertsCreator.processError(r2, r7, r3, r8, r4);
        L_0x0096:
            r8 = r7.text;
            r2 = "PHONE_CODE_EMPTY";
            r8 = r8.contains(r2);
            if (r8 != 0) goto L_0x00bf;
        L_0x00a0:
            r8 = r7.text;
            r2 = "PHONE_CODE_INVALID";
            r8 = r8.contains(r2);
            if (r8 == 0) goto L_0x00ab;
        L_0x00aa:
            goto L_0x00bf;
        L_0x00ab:
            r7 = r7.text;
            r8 = "PHONE_CODE_EXPIRED";
            r7 = r7.contains(r8);
            if (r7 == 0) goto L_0x00d4;
        L_0x00b5:
            r6.onBackPressed(r1);
            r7 = r6.this$0;
            r8 = 0;
            r7.setPage(r0, r1, r8, r1);
            goto L_0x00d4;
        L_0x00bf:
            r7 = 0;
        L_0x00c0:
            r8 = r6.codeField;
            r1 = r8.length;
            if (r7 >= r1) goto L_0x00cf;
        L_0x00c5:
            r8 = r8[r7];
            r1 = "";
            r8.setText(r1);
            r7 = r7 + 1;
            goto L_0x00c0;
        L_0x00cf:
            r7 = r8[r0];
            r7.requestFocus();
        L_0x00d4:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView.lambda$null$6$CancelAccountDeletionActivity$LoginActivitySmsView(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$TL_account_confirmPhone):void");
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$dLIqV278x4RUePN-aKF0rcdGRXU(this), 100);
            }
        }

        public /* synthetic */ void lambda$onShow$8$CancelAccountDeletionActivity$LoginActivitySmsView() {
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
                            this.ignoreOnTextChange = true;
                            this.codeField[0].setText(stringBuilder3);
                            this.ignoreOnTextChange = false;
                            onNextPressed();
                        }
                    }
                }
            }
        }
    }

    public class PhoneView extends SlideView {
        private boolean nextPressed = false;
        private RadialProgressView progressBar;

        public PhoneView(Context context) {
            super(context);
            setOrientation(1);
            FrameLayout frameLayout = new FrameLayout(context);
            addView(frameLayout, LayoutHelper.createLinear(-1, 200));
            this.progressBar = new RadialProgressView(context);
            frameLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }

        /* JADX WARNING: Removed duplicated region for block: B:32:0x00da A:{Catch:{ Exception -> 0x00e4 }} */
        public void onNextPressed() {
            /*
            r11 = this;
            r0 = org.telegram.ui.CancelAccountDeletionActivity.this;
            r0 = r0.getParentActivity();
            if (r0 == 0) goto L_0x010f;
        L_0x0008:
            r0 = r11.nextPressed;
            if (r0 == 0) goto L_0x000e;
        L_0x000c:
            goto L_0x010f;
        L_0x000e:
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r1 = "phone";
            r0 = r0.getSystemService(r1);
            r0 = (android.telephony.TelephonyManager) r0;
            r2 = r0.getSimState();
            r3 = 1;
            if (r2 == r3) goto L_0x0023;
        L_0x001f:
            r2 = r0.getPhoneType();
        L_0x0023:
            r2 = android.os.Build.VERSION.SDK_INT;
            r4 = 23;
            r2 = new org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode;
            r2.<init>();
            r4 = org.telegram.ui.CancelAccountDeletionActivity.this;
            r4 = r4.hash;
            r2.hash = r4;
            r4 = new org.telegram.tgnet.TLRPC$TL_codeSettings;
            r4.<init>();
            r2.settings = r4;
            r4 = r2.settings;
            r5 = 0;
            r4.allow_flashcall = r5;
            r6 = android.os.Build.VERSION.SDK_INT;
            r7 = 26;
            if (r6 < r7) goto L_0x0067;
        L_0x0046:
            r6 = android.telephony.SmsManager.getDefault();	 Catch:{ Throwable -> 0x0062 }
            r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0062 }
            r8 = new android.content.Intent;	 Catch:{ Throwable -> 0x0062 }
            r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0062 }
            r10 = org.telegram.messenger.SmsReceiver.class;
            r8.<init>(r9, r10);	 Catch:{ Throwable -> 0x0062 }
            r9 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
            r7 = android.app.PendingIntent.getBroadcast(r7, r5, r8, r9);	 Catch:{ Throwable -> 0x0062 }
            r6 = r6.createAppSpecificSmsToken(r7);	 Catch:{ Throwable -> 0x0062 }
            r4.app_hash = r6;	 Catch:{ Throwable -> 0x0062 }
            goto L_0x006d;
        L_0x0062:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
            goto L_0x006d;
        L_0x0067:
            r6 = org.telegram.messenger.BuildVars.SMS_HASH;
            r4.app_hash = r6;
            r4.app_hash_persistent = r3;
        L_0x006d:
            r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r6 = "mainconfig";
            r4 = r4.getSharedPreferences(r6, r5);
            r6 = r2.settings;
            r6 = r6.app_hash;
            r6 = android.text.TextUtils.isEmpty(r6);
            r7 = "sms_hash";
            if (r6 != 0) goto L_0x0099;
        L_0x0081:
            r6 = r2.settings;
            r8 = r6.flags;
            r8 = r8 | 8;
            r6.flags = r8;
            r4 = r4.edit();
            r6 = r2.settings;
            r6 = r6.app_hash;
            r4 = r4.putString(r7, r6);
            r4.commit();
            goto L_0x00a4;
        L_0x0099:
            r4 = r4.edit();
            r4 = r4.remove(r7);
            r4.commit();
        L_0x00a4:
            r4 = r2.settings;
            r4 = r4.allow_flashcall;
            if (r4 == 0) goto L_0x00ec;
        L_0x00aa:
            r0 = r0.getLine1Number();	 Catch:{ Exception -> 0x00e4 }
            r4 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x00e4 }
            if (r4 != 0) goto L_0x00df;
        L_0x00b4:
            r4 = r2.settings;	 Catch:{ Exception -> 0x00e4 }
            r6 = org.telegram.ui.CancelAccountDeletionActivity.this;	 Catch:{ Exception -> 0x00e4 }
            r6 = r6.phone;	 Catch:{ Exception -> 0x00e4 }
            r6 = r6.contains(r0);	 Catch:{ Exception -> 0x00e4 }
            if (r6 != 0) goto L_0x00d1;
        L_0x00c2:
            r6 = org.telegram.ui.CancelAccountDeletionActivity.this;	 Catch:{ Exception -> 0x00e4 }
            r6 = r6.phone;	 Catch:{ Exception -> 0x00e4 }
            r0 = r0.contains(r6);	 Catch:{ Exception -> 0x00e4 }
            if (r0 == 0) goto L_0x00cf;
        L_0x00ce:
            goto L_0x00d1;
        L_0x00cf:
            r0 = 0;
            goto L_0x00d2;
        L_0x00d1:
            r0 = 1;
        L_0x00d2:
            r4.current_number = r0;	 Catch:{ Exception -> 0x00e4 }
            r0 = r2.settings;	 Catch:{ Exception -> 0x00e4 }
            r0 = r0.current_number;	 Catch:{ Exception -> 0x00e4 }
            if (r0 != 0) goto L_0x00ec;
        L_0x00da:
            r0 = r2.settings;	 Catch:{ Exception -> 0x00e4 }
            r0.allow_flashcall = r5;	 Catch:{ Exception -> 0x00e4 }
            goto L_0x00ec;
        L_0x00df:
            r0 = r2.settings;	 Catch:{ Exception -> 0x00e4 }
            r0.current_number = r5;	 Catch:{ Exception -> 0x00e4 }
            goto L_0x00ec;
        L_0x00e4:
            r0 = move-exception;
            r4 = r2.settings;
            r4.allow_flashcall = r5;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00ec:
            r0 = new android.os.Bundle;
            r0.<init>();
            r4 = org.telegram.ui.CancelAccountDeletionActivity.this;
            r4 = r4.phone;
            r0.putString(r1, r4);
            r11.nextPressed = r3;
            r1 = org.telegram.ui.CancelAccountDeletionActivity.this;
            r1 = r1.currentAccount;
            r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
            r3 = new org.telegram.ui.-$$Lambda$CancelAccountDeletionActivity$PhoneView$k7vfL3HDSHw4EqLEYwRz3mk5u4I;
            r3.<init>(r11, r0, r2);
            r0 = 2;
            r1.sendRequest(r2, r3, r0);
        L_0x010f:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CancelAccountDeletionActivity$PhoneView.onNextPressed():void");
        }

        public /* synthetic */ void lambda$onNextPressed$1$CancelAccountDeletionActivity$PhoneView(Bundle bundle, TL_account_sendConfirmPhoneCode tL_account_sendConfirmPhoneCode, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$CancelAccountDeletionActivity$PhoneView$P6GwpG9TiT0cGhXBWpHiOCU7n94(this, tL_error, bundle, tLObject, tL_account_sendConfirmPhoneCode));
        }

        public /* synthetic */ void lambda$null$0$CancelAccountDeletionActivity$PhoneView(TL_error tL_error, Bundle bundle, TLObject tLObject, TL_account_sendConfirmPhoneCode tL_account_sendConfirmPhoneCode) {
            this.nextPressed = false;
            if (tL_error == null) {
                CancelAccountDeletionActivity.this.fillNextCodeParams(bundle, (TL_auth_sentCode) tLObject);
                return;
            }
            CancelAccountDeletionActivity cancelAccountDeletionActivity = CancelAccountDeletionActivity.this;
            cancelAccountDeletionActivity.errorDialog = AlertsCreator.processError(cancelAccountDeletionActivity.currentAccount, tL_error, CancelAccountDeletionActivity.this, tL_account_sendConfirmPhoneCode, new Object[0]);
        }

        public String getHeaderName() {
            return LocaleController.getString("CancelAccountReset", NUM);
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
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i >= slideViewArr.length) {
                break;
            }
            if (slideViewArr[i] != null) {
                slideViewArr[i].onDestroyActivity();
            }
            i++;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == 1) {
                    CancelAccountDeletionActivity.this.views[CancelAccountDeletionActivity.this.currentViewNum].onNextPressed();
                } else if (i == -1) {
                    CancelAccountDeletionActivity.this.finishFragment();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneButton.setVisibility(8);
        AnonymousClass2 anonymousClass2 = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (CancelAccountDeletionActivity.this.currentViewNum == 1 || CancelAccountDeletionActivity.this.currentViewNum == 2 || CancelAccountDeletionActivity.this.currentViewNum == 4) {
                    rect.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(view, rect, z);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                CancelAccountDeletionActivity.this.scrollHeight = MeasureSpec.getSize(i2) - AndroidUtilities.dp(30.0f);
                super.onMeasure(i, i2);
            }
        };
        anonymousClass2.setFillViewport(true);
        this.fragmentView = anonymousClass2;
        FrameLayout frameLayout = new FrameLayout(context);
        anonymousClass2.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(context);
        this.views[1] = new LoginActivitySmsView(this, context, 1);
        this.views[2] = new LoginActivitySmsView(this, context, 2);
        this.views[3] = new LoginActivitySmsView(this, context, 3);
        this.views[4] = new LoginActivitySmsView(this, context, 4);
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i < slideViewArr.length) {
                slideViewArr[i].setVisibility(i == 0 ? 0 : 8);
                frameLayout.addView(this.views[i], LayoutHelper.createFrame(-1, i == 0 ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
                i++;
            } else {
                this.actionBar.setTitle(slideViewArr[0].getHeaderName());
                return this.fragmentView;
            }
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 6) {
            this.checkPermissions = false;
            i = this.currentViewNum;
            if (i == 0) {
                this.views[i].onNextPressed();
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
        }
        if (dialog == this.errorDialog) {
            finishFragment();
        }
    }

    public boolean onBackPressed() {
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i >= slideViewArr.length) {
                return true;
            }
            if (slideViewArr[i] != null) {
                slideViewArr[i].onDestroyActivity();
            }
            i++;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
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
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int i, boolean z, Bundle bundle, boolean z2) {
        if (i == 3 || i == 0) {
            this.doneButton.setVisibility(8);
        } else {
            this.doneButton.setVisibility(0);
        }
        SlideView[] slideViewArr = this.views;
        final Object obj = slideViewArr[this.currentViewNum];
        final SlideView slideView = slideViewArr[i];
        this.currentViewNum = i;
        slideView.setParams(bundle, false);
        this.actionBar.setTitle(slideView.getHeaderName());
        slideView.onShow();
        slideView.setX((float) (z2 ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        float[] fArr = new float[1];
        fArr[0] = (float) (z2 ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x);
        String str = "translationX";
        animatorArr[0] = ObjectAnimator.ofFloat(obj, str, fArr);
        animatorArr[1] = ObjectAnimator.ofFloat(slideView, str, new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                slideView.setVisibility(0);
            }

            public void onAnimationEnd(Animator animator) {
                obj.setVisibility(8);
                obj.setX(0.0f);
            }
        });
        animatorSet.start();
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
        int i;
        SlideView[] slideViewArr = this.views;
        PhoneView phoneView = (PhoneView) slideViewArr[0];
        LoginActivitySmsView loginActivitySmsView = (LoginActivitySmsView) slideViewArr[1];
        LoginActivitySmsView loginActivitySmsView2 = (LoginActivitySmsView) slideViewArr[2];
        LoginActivitySmsView loginActivitySmsView3 = (LoginActivitySmsView) slideViewArr[3];
        LoginActivitySmsView loginActivitySmsView4 = (LoginActivitySmsView) slideViewArr[4];
        ArrayList arrayList = new ArrayList();
        ThemeDescription themeDescription = r9;
        ThemeDescription themeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        arrayList.add(themeDescription);
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(phoneView.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
        arrayList.add(new ThemeDescription(loginActivitySmsView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (loginActivitySmsView.codeField != null) {
            for (i = 0; i < loginActivitySmsView.codeField.length; i++) {
                arrayList.add(new ThemeDescription(loginActivitySmsView.codeField[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView.codeField[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(loginActivitySmsView.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        View access$2300 = loginActivitySmsView.progressView;
        Class[] clsArr = new Class[]{ProgressView.class};
        String[] strArr = new String[1];
        strArr[0] = "paint";
        arrayList.add(new ThemeDescription(access$2300, 0, clsArr, strArr, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(loginActivitySmsView.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(loginActivitySmsView.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(loginActivitySmsView.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (loginActivitySmsView2.codeField != null) {
            for (i = 0; i < loginActivitySmsView2.codeField.length; i++) {
                arrayList.add(new ThemeDescription(loginActivitySmsView2.codeField[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView2.codeField[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(loginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{r3}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{r3}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(loginActivitySmsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (loginActivitySmsView3.codeField != null) {
            for (i = 0; i < loginActivitySmsView3.codeField.length; i++) {
                arrayList.add(new ThemeDescription(loginActivitySmsView3.codeField[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView3.codeField[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(loginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{r3}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{r3}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(loginActivitySmsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        if (loginActivitySmsView4.codeField != null) {
            for (i = 0; i < loginActivitySmsView4.codeField.length; i++) {
                arrayList.add(new ThemeDescription(loginActivitySmsView4.codeField[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(loginActivitySmsView4.codeField[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(loginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{r3}, null, null, null, "login_progressInner"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{r3}, null, null, null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(loginActivitySmsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionBackground"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
