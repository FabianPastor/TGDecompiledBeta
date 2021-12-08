package org.telegram.ui;

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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.LoginActivity;

public class CancelAccountDeletionActivity extends BaseFragment {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int currentViewNum = 0;
    private View doneButton;
    /* access modifiers changed from: private */
    public Dialog errorDialog;
    /* access modifiers changed from: private */
    public String hash;
    /* access modifiers changed from: private */
    public String phone;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int scrollHeight;
    /* access modifiers changed from: private */
    public SlideView[] views = new SlideView[5];

    private static class ProgressView extends View {
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

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int start = (int) (((float) getMeasuredWidth()) * this.progress);
            canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    public CancelAccountDeletionActivity(Bundle args) {
        super(args);
        this.hash = args.getString("hash");
        this.phone = args.getString("phone");
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                break;
            }
            if (slideViewArr[a] != null) {
                slideViewArr[a].onDestroyActivity();
            }
            a++;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    CancelAccountDeletionActivity.this.views[CancelAccountDeletionActivity.this.currentViewNum].onNextPressed((String) null);
                } else if (id == -1) {
                    CancelAccountDeletionActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        this.doneButton = addItemWithWidth;
        addItemWithWidth.setVisibility(8);
        ScrollView scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (CancelAccountDeletionActivity.this.currentViewNum == 1 || CancelAccountDeletionActivity.this.currentViewNum == 2 || CancelAccountDeletionActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int unused = CancelAccountDeletionActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        scrollView.setFillViewport(true);
        this.fragmentView = scrollView;
        FrameLayout frameLayout = new FrameLayout(context2);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(context2);
        this.views[1] = new LoginActivitySmsView(this, context2, 1);
        this.views[2] = new LoginActivitySmsView(this, context2, 2);
        this.views[3] = new LoginActivitySmsView(this, context2, 3);
        this.views[4] = new LoginActivitySmsView(this, context2, 4);
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a < slideViewArr.length) {
                slideViewArr[a].setVisibility(a == 0 ? 0 : 8);
                frameLayout.addView(this.views[a], LayoutHelper.createFrame(-1, a == 0 ? -2.0f : -1.0f, 51, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 30.0f, AndroidUtilities.isTablet() ? 26.0f : 18.0f, 0.0f));
                a++;
            } else {
                this.actionBar.setTitle(this.views[0].getHeaderName());
                return this.fragmentView;
            }
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (dialog == this.errorDialog) {
            finishFragment();
        }
    }

    public boolean onBackPressed() {
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                return true;
            }
            if (slideViewArr[a] != null) {
                slideViewArr[a].onDestroyActivity();
            }
            a++;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.views[this.currentViewNum].onShow();
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCacnel(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (page == 3 || page == 0) {
            this.doneButton.setVisibility(8);
        } else {
            this.doneButton.setVisibility(0);
        }
        SlideView[] slideViewArr = this.views;
        final SlideView outView = slideViewArr[this.currentViewNum];
        final SlideView newView = slideViewArr[page];
        this.currentViewNum = page;
        newView.setParams(params, false);
        this.actionBar.setTitle(newView.getHeaderName());
        newView.onShow();
        int i = AndroidUtilities.displaySize.x;
        if (back) {
            i = -i;
        }
        newView.setX((float) i);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        Animator[] animatorArr = new Animator[2];
        float[] fArr = new float[1];
        int i2 = AndroidUtilities.displaySize.x;
        if (!back) {
            i2 = -i2;
        }
        fArr[0] = (float) i2;
        animatorArr[0] = ObjectAnimator.ofFloat(outView, "translationX", fArr);
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

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TLRPC.TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeApp) {
            params.putInt("type", 1);
            params.putInt("length", res.type.length);
            setPage(1, true, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(4, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, true, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(2, true, params, false);
        }
    }

    public class PhoneView extends SlideView {
        private boolean nextPressed = false;
        /* access modifiers changed from: private */
        public RadialProgressView progressBar;

        public PhoneView(Context context) {
            super(context);
            setOrientation(1);
            FrameLayout frameLayout = new FrameLayout(context);
            addView(frameLayout, LayoutHelper.createLinear(-1, 200));
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.progressBar = radialProgressView;
            frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        public void onNextPressed(String code) {
            if (CancelAccountDeletionActivity.this.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                TLRPC.TL_account_sendConfirmPhoneCode req = new TLRPC.TL_account_sendConfirmPhoneCode();
                req.hash = CancelAccountDeletionActivity.this.hash;
                req.settings = new TLRPC.TL_codeSettings();
                req.settings.allow_flashcall = false;
                req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                if (req.settings.allow_app_hash) {
                    preferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
                } else {
                    preferences.edit().remove("sms_hash").commit();
                }
                if (req.settings.allow_flashcall) {
                    try {
                        String number = tm.getLine1Number();
                        if (!TextUtils.isEmpty(number)) {
                            req.settings.current_number = PhoneNumberUtils.compare(CancelAccountDeletionActivity.this.phone, number);
                            if (!req.settings.current_number) {
                                req.settings.allow_flashcall = false;
                            }
                        } else {
                            req.settings.current_number = false;
                        }
                    } catch (Exception e) {
                        req.settings.allow_flashcall = false;
                        FileLog.e((Throwable) e);
                    }
                }
                Bundle params = new Bundle();
                params.putString("phone", CancelAccountDeletionActivity.this.phone);
                this.nextPressed = true;
                ConnectionsManager.getInstance(CancelAccountDeletionActivity.this.currentAccount).sendRequest(req, new CancelAccountDeletionActivity$PhoneView$$ExternalSyntheticLambda1(this, params, req), 2);
            }
        }

        /* renamed from: lambda$onNextPressed$1$org-telegram-ui-CancelAccountDeletionActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1524xa2cd87c5(Bundle params, TLRPC.TL_account_sendConfirmPhoneCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$PhoneView$$ExternalSyntheticLambda0(this, error, params, response, req));
        }

        /* renamed from: lambda$onNextPressed$0$org-telegram-ui-CancelAccountDeletionActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1523x7d397ec4(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_account_sendConfirmPhoneCode req) {
            this.nextPressed = false;
            if (error == null) {
                CancelAccountDeletionActivity.this.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
                return;
            }
            CancelAccountDeletionActivity cancelAccountDeletionActivity = CancelAccountDeletionActivity.this;
            Dialog unused = cancelAccountDeletionActivity.errorDialog = AlertsCreator.processError(cancelAccountDeletionActivity.currentAccount, error, CancelAccountDeletionActivity.this, req, new Object[0]);
        }

        public String getHeaderName() {
            return LocaleController.getString("CancelAccountReset", NUM);
        }

        public void onShow() {
            super.onShow();
            onNextPressed((String) null);
        }
    }

    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        /* access modifiers changed from: private */
        public ImageView blackImageView;
        /* access modifiers changed from: private */
        public RLottieImageView blueImageView;
        /* access modifiers changed from: private */
        public EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        /* access modifiers changed from: private */
        public int codeTime = 15000;
        private Timer codeTimer;
        /* access modifiers changed from: private */
        public TextView confirmTextView;
        /* access modifiers changed from: private */
        public int currentType;
        RLottieDrawable hintDrawable;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        /* access modifiers changed from: private */
        public String lastError = "";
        /* access modifiers changed from: private */
        public int length;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        private String pattern = "*";
        /* access modifiers changed from: private */
        public String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        final /* synthetic */ CancelAccountDeletionActivity this$0;
        /* access modifiers changed from: private */
        public int time = 60000;
        /* access modifiers changed from: private */
        public TextView timeText;
        /* access modifiers changed from: private */
        public Timer timeTimer;
        /* access modifiers changed from: private */
        public int timeout;
        private final Object timerSync = new Object();
        /* access modifiers changed from: private */
        public TextView titleTextView;
        /* access modifiers changed from: private */
        public boolean waitingForEvent;

        static /* synthetic */ int access$1526(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.codeTime;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$2126(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.time;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.time = i;
            return i;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(org.telegram.ui.CancelAccountDeletionActivity r31, android.content.Context r32, int r33) {
            /*
                r30 = this;
                r0 = r30
                r1 = r31
                r2 = r32
                r0.this$0 = r1
                r0.<init>(r2)
                java.lang.Object r3 = new java.lang.Object
                r3.<init>()
                r0.timerSync = r3
                r3 = 60000(0xea60, float:8.4078E-41)
                r0.time = r3
                r3 = 15000(0x3a98, float:2.102E-41)
                r0.codeTime = r3
                java.lang.String r3 = ""
                r0.lastError = r3
                java.lang.String r3 = "*"
                r0.pattern = r3
                r3 = r33
                r0.currentType = r3
                r4 = 1
                r0.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                java.lang.String r6 = "windowBackgroundWhiteLinkText"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setLinkTextColor(r6)
                android.widget.TextView r5 = r0.confirmTextView
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r7)
                android.widget.TextView r5 = r0.confirmTextView
                java.lang.String r7 = "windowBackgroundWhiteLinkSelection"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r5.setHighlightColor(r7)
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r7)
                android.widget.TextView r5 = r0.confirmTextView
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3var_, float:1.0)
                r5.setLineSpacing(r9, r10)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleTextView = r5
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.titleTextView
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r11)
                android.widget.TextView r5 = r0.titleTextView
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                r13 = 3
                if (r11 == 0) goto L_0x0091
                r11 = 5
                goto L_0x0092
            L_0x0091:
                r11 = 3
            L_0x0092:
                r5.setGravity(r11)
                android.widget.TextView r5 = r0.titleTextView
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 49
                r5.setGravity(r11)
                int r5 = r0.currentType
                r14 = -2
                if (r5 != r13) goto L_0x013f
                android.widget.TextView r5 = r0.confirmTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00b3
                r9 = 5
                goto L_0x00b4
            L_0x00b3:
                r9 = 3
            L_0x00b4:
                r9 = r9 | 48
                r5.setGravity(r9)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00c4
                r9 = 5
                goto L_0x00c5
            L_0x00c4:
                r9 = 3
            L_0x00c5:
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r9)
                r0.addView(r5, r9)
                android.widget.ImageView r9 = new android.widget.ImageView
                r9.<init>(r2)
                r15 = 2131165959(0x7var_, float:1.794615E38)
                r9.setImageResource(r15)
                boolean r15 = org.telegram.messenger.LocaleController.isRTL
                if (r15 == 0) goto L_0x010f
                r16 = 64
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 19
                r19 = 1073741824(0x40000000, float:2.0)
                r20 = 1073741824(0x40000000, float:2.0)
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r9, r15)
                android.widget.TextView r15 = r0.confirmTextView
                r16 = -1
                r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r18 = org.telegram.messenger.LocaleController.isRTL
                if (r18 == 0) goto L_0x00fd
                r18 = 5
                goto L_0x00ff
            L_0x00fd:
                r18 = 3
            L_0x00ff:
                r19 = 1118044160(0x42a40000, float:82.0)
                r20 = 0
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r15, r12)
                goto L_0x013d
            L_0x010f:
                android.widget.TextView r12 = r0.confirmTextView
                r15 = -1
                r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r17 = org.telegram.messenger.LocaleController.isRTL
                if (r17 == 0) goto L_0x011b
                r17 = 5
                goto L_0x011d
            L_0x011b:
                r17 = 3
            L_0x011d:
                r18 = 0
                r19 = 0
                r20 = 1118044160(0x42a40000, float:82.0)
                r21 = 0
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r5.addView(r12, r15)
                r16 = 64
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 21
                r20 = 1073741824(0x40000000, float:2.0)
                r22 = 1073741824(0x40000000, float:2.0)
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r9, r12)
            L_0x013d:
                goto L_0x0244
            L_0x013f:
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r11)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
                int r12 = r0.currentType
                java.lang.String r15 = "chats_actionBackground"
                if (r12 != r4) goto L_0x01bf
                android.widget.ImageView r12 = new android.widget.ImageView
                r12.<init>(r2)
                r0.blackImageView = r12
                r11 = 2131166106(0x7var_a, float:1.7946448E38)
                r12.setImageResource(r11)
                android.widget.ImageView r11 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
                r12.<init>(r9, r7)
                r11.setColorFilter(r12)
                android.widget.ImageView r7 = r0.blackImageView
                r23 = -2
                r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r25 = 51
                r26 = 0
                r27 = 0
                r28 = 0
                r29 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                r9 = 2131166104(0x7var_, float:1.7946444E38)
                r7.setImageResource(r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
                r9.<init>(r11, r12)
                r7.setColorFilter(r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.TextView r7 = r0.titleTextView
                r9 = 2131627744(0x7f0e0ee0, float:1.8882761E38)
                java.lang.String r11 = "SentAppCodeTitle"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r7.setText(r9)
                goto L_0x0222
            L_0x01bf:
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
                r24 = 2131558494(0x7f0d005e, float:1.8742305E38)
                r9 = 1111490560(0x42400000, float:48.0)
                int r26 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r27 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r28 = 1
                r29 = 0
                java.lang.String r25 = "NUM"
                r23 = r7
                r23.<init>(r24, r25, r26, r27, r28, r29)
                r0.hintDrawable = r7
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                java.lang.String r11 = "Bubble.**"
                r7.setLayerColor(r11, r9)
                org.telegram.ui.Components.RLottieDrawable r7 = r0.hintDrawable
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                java.lang.String r11 = "Phone.**"
                r7.setLayerColor(r11, r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                org.telegram.ui.Components.RLottieDrawable r9 = r0.hintDrawable
                r7.setAnimation(r9)
                org.telegram.ui.Components.RLottieImageView r7 = r0.blueImageView
                r23 = 48
                r24 = 1111490560(0x42400000, float:48.0)
                r25 = 51
                r26 = 0
                r27 = 0
                r28 = 0
                r29 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.TextView r7 = r0.titleTextView
                r9 = 2131627748(0x7f0e0ee4, float:1.888277E38)
                java.lang.String r11 = "SentSmsCodeTitle"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r7.setText(r9)
            L_0x0222:
                android.widget.TextView r7 = r0.titleTextView
                r23 = -2
                r24 = -2
                r25 = 49
                r26 = 0
                r27 = 18
                r28 = 0
                r29 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r0.addView(r7, r9)
                android.widget.TextView r7 = r0.confirmTextView
                r27 = 17
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r0.addView(r7, r9)
            L_0x0244:
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r2)
                r0.codeFieldContainer = r5
                r7 = 0
                r5.setOrientation(r7)
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 36
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r9, (int) r4)
                r0.addView(r5, r9)
                int r5 = r0.currentType
                if (r5 != r13) goto L_0x0265
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 8
                r5.setVisibility(r9)
            L_0x0265:
                org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$1 r5 = new org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$1
                r5.<init>(r2, r1)
                r0.timeText = r5
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.timeText
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r6 = (float) r6
                r5.setLineSpacing(r6, r10)
                int r5 = r0.currentType
                r6 = 1097859072(0x41700000, float:15.0)
                r9 = 1092616192(0x41200000, float:10.0)
                if (r5 != r13) goto L_0x02c5
                android.widget.TextView r5 = r0.timeText
                r11 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.timeText
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0294
                r11 = 5
                goto L_0x0295
            L_0x0294:
                r11 = 3
            L_0x0295:
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r11)
                org.telegram.ui.CancelAccountDeletionActivity$ProgressView r5 = new org.telegram.ui.CancelAccountDeletionActivity$ProgressView
                r5.<init>(r2)
                r0.progressView = r5
                android.widget.TextView r5 = r0.timeText
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x02ab
                r12 = 5
                goto L_0x02ac
            L_0x02ab:
                r12 = 3
            L_0x02ac:
                r5.setGravity(r12)
                org.telegram.ui.CancelAccountDeletionActivity$ProgressView r5 = r0.progressView
                r17 = -1
                r18 = 3
                r19 = 0
                r20 = 1094713344(0x41400000, float:12.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
                r0.addView(r5, r11)
                goto L_0x02e7
            L_0x02c5:
                android.widget.TextView r5 = r0.timeText
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r11, r7, r12)
                android.widget.TextView r5 = r0.timeText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.timeText
                r11 = 49
                r5.setGravity(r11)
                android.widget.TextView r5 = r0.timeText
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
            L_0x02e7:
                org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$2 r5 = new org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$2
                r5.<init>(r2, r1)
                r0.problemText = r5
                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.problemText
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                android.widget.TextView r5 = r0.problemText
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r8, r7, r9)
                android.widget.TextView r5 = r0.problemText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.problemText
                r6 = 49
                r5.setGravity(r6)
                int r5 = r0.currentType
                if (r5 != r4) goto L_0x032d
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625277(0x7f0e053d, float:1.8877757E38)
                java.lang.String r6 = "DidNotGetTheCodeSms"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x033b
            L_0x032d:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625275(0x7f0e053b, float:1.8877753E38)
                java.lang.String r6 = "DidNotGetTheCode"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
            L_0x033b:
                android.widget.TextView r4 = r0.problemText
                r5 = 49
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r5)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.problemText
                org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda1
                r5.<init>(r0)
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView.<init>(org.telegram.ui.CancelAccountDeletionActivity, android.content.Context, int):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1510xvar_de3a5(View v) {
            if (!this.nextPressed) {
                int i = this.nextType;
                if (!((i == 4 && this.currentType == 2) || i == 0)) {
                    resendCode();
                    return;
                }
                try {
                    PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                    Intent mailer = new Intent("android.intent.action.SENDTO");
                    mailer.setData(Uri.parse("mailto:"));
                    mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                    mailer.putExtra("android.intent.extra.SUBJECT", "Android cancel account deletion issue " + version + " " + this.phone);
                    mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.phone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                    getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                } catch (Exception e) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("NoMailInstalled", NUM));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            RLottieImageView rLottieImageView;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.currentType != 3 && (rLottieImageView = this.blueImageView) != null) {
                int innerHeight = rLottieImageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                if (this.timeText.getVisibility() == 0) {
                    innerHeight += this.timeText.getMeasuredHeight();
                }
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(340.0f);
                if (this.this$0.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), Math.min(this.this$0.scrollHeight, maxHeight));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int t2;
            super.onLayout(changed, l, t, r, b);
            if (this.currentType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int height = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int h = this.problemText.getMeasuredHeight();
                    t2 = (bottom + height) - h;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), t2, this.problemText.getRight(), t2 + h);
                } else if (this.timeText.getVisibility() == 0) {
                    int h2 = this.timeText.getMeasuredHeight();
                    t2 = (bottom + height) - h2;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), t2, this.timeText.getRight(), t2 + h2);
                } else {
                    t2 = bottom + height;
                }
                int h3 = this.codeFieldContainer.getMeasuredHeight();
                int t3 = (((t2 - bottom) - h3) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), t3, this.codeFieldContainer.getRight(), t3 + h3);
                int height2 = t3;
            }
        }

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda7(this, params, req), 2);
        }

        /* renamed from: lambda$resendCode$3$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1516x85cCLASSNAME(Bundle params, TLRPC.TL_auth_resendCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(this, error, params, response, req));
        }

        /* renamed from: lambda$resendCode$2$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1515xe95e3721(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_resendCode req) {
            AlertDialog dialog;
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (!(error.text == null || (dialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0])) == null || !error.text.contains("PHONE_CODE_EXPIRED"))) {
                dialog.setPositiveButtonListener(new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda0(this));
            }
            this.this$0.needHideProgress();
        }

        /* renamed from: lambda$resendCode$1$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1514x4cvar_ac2(DialogInterface dialog1, int which) {
            onBackPressed(true);
            this.this$0.finishFragment();
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("CancelAccountReset", NUM);
        }

        public boolean needBackButton() {
            return true;
        }

        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            Bundle bundle = params;
            if (bundle != null) {
                this.waitingForEvent = true;
                int i3 = this.currentType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.phone = bundle.getString("phone");
                this.phoneHash = bundle.getString("phoneHash");
                int i4 = bundle.getInt("timeout");
                this.time = i4;
                this.timeout = i4;
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                int i5 = bundle.getInt("length");
                this.length = i5;
                if (i5 == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    int a = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (a >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[a].setText("");
                        a++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    int a2 = 0;
                    while (a2 < this.length) {
                        final int num = a2;
                        this.codeField[a2] = new EditTextBoldCursor(getContext());
                        this.codeField[a2].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[a2].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        this.codeField[a2].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[a2].setCursorWidth(1.5f);
                        Drawable pressedDrawable = getResources().getDrawable(NUM).mutate();
                        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteInputFieldActivated"), PorterDuff.Mode.MULTIPLY));
                        this.codeField[a2].setBackgroundDrawable(pressedDrawable);
                        this.codeField[a2].setImeOptions(NUM);
                        this.codeField[a2].setTextSize(1, 20.0f);
                        this.codeField[a2].setMaxLines(1);
                        this.codeField[a2].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[a2].setPadding(0, 0, 0, 0);
                        this.codeField[a2].setGravity(49);
                        if (this.currentType == 3) {
                            this.codeField[a2].setEnabled(false);
                            this.codeField[a2].setInputType(0);
                            this.codeField[a2].setVisibility(8);
                        } else {
                            this.codeField[a2].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[a2], LayoutHelper.createLinear(34, 36, 1, 0, 0, a2 != this.length - 1 ? 7 : 0, 0));
                        this.codeField[a2].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                int len;
                                if (!LoginActivitySmsView.this.ignoreOnTextChange && (len = s.length()) >= 1) {
                                    if (len > 1) {
                                        String text = s.toString();
                                        boolean unused = LoginActivitySmsView.this.ignoreOnTextChange = true;
                                        for (int a = 0; a < Math.min(LoginActivitySmsView.this.length - num, len); a++) {
                                            if (a == 0) {
                                                s.replace(0, len, text.substring(a, a + 1));
                                            } else {
                                                LoginActivitySmsView.this.codeField[num + a].setText(text.substring(a, a + 1));
                                            }
                                        }
                                        boolean unused2 = LoginActivitySmsView.this.ignoreOnTextChange = false;
                                    }
                                    if (num != LoginActivitySmsView.this.length - 1) {
                                        LoginActivitySmsView.this.codeField[num + 1].setSelection(LoginActivitySmsView.this.codeField[num + 1].length());
                                        LoginActivitySmsView.this.codeField[num + 1].requestFocus();
                                    }
                                    if ((num == LoginActivitySmsView.this.length - 1 || (num == LoginActivitySmsView.this.length - 2 && len >= 2)) && LoginActivitySmsView.this.getCode().length() == LoginActivitySmsView.this.length) {
                                        LoginActivitySmsView.this.onNextPressed((String) null);
                                    }
                                }
                            }
                        });
                        this.codeField[a2].setOnKeyListener(new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(this, num));
                        this.codeField[a2].setOnEditorActionListener(new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(this));
                        a2++;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    PhoneFormat instance = PhoneFormat.getInstance();
                    SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo2", NUM, instance.format("+" + number))));
                    int index1 = TextUtils.indexOf(spanned, '*');
                    int index2 = TextUtils.lastIndexOf(spanned, '*');
                    if (!(index1 == -1 || index2 == -1 || index1 == index2)) {
                        this.confirmTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spanned.replace(index2, index2 + 1, "");
                        spanned.replace(index1, index1 + 1, "");
                        spanned.setSpan(new URLSpanNoUnderline("tg://settings/change_number"), index1, index2 - 1, 33);
                    }
                    this.confirmTextView.setText(spanned);
                    if (this.currentType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    int i6 = this.currentType;
                    if (i6 == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (i6 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i7 = this.nextType;
                        if (i7 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", NUM, 1, 0));
                        } else if (i7 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", NUM, 1, 0));
                        }
                        createTimer();
                    } else if (i6 == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else if (i6 == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", NUM, 2, 0));
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

        /* renamed from: lambda$setParams$4$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ boolean m1517xcCLASSNAMEd79(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        /* renamed from: lambda$setParams$5$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ boolean m1518x690599d8(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* access modifiers changed from: private */
        public void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$LoginActivitySmsView$4$$ExternalSyntheticLambda0(this));
                    }

                    /* renamed from: lambda$run$0$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView$4  reason: not valid java name */
                    public /* synthetic */ void m1519xdvar_baa() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$1400 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTime);
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTime;
                        LoginActivitySmsView.access$1526(LoginActivitySmsView.this, currentTime - access$1400);
                        if (LoginActivitySmsView.this.codeTime <= 1000) {
                            LoginActivitySmsView.this.problemText.setVisibility(0);
                            LoginActivitySmsView.this.timeText.setVisibility(8);
                            LoginActivitySmsView.this.destroyCodeTimer();
                        }
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.codeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                Timer timer = new Timer();
                this.timeTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (LoginActivitySmsView.this.timeTimer != null) {
                            AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0(this));
                        }
                    }

                    /* renamed from: lambda$run$2$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m1522x50var_ce9() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$2000 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTime);
                        LoginActivitySmsView.access$2126(LoginActivitySmsView.this, currentTime - access$2000);
                        double unused = LoginActivitySmsView.this.lastCurrentTime = currentTime;
                        if (LoginActivitySmsView.this.time >= 1000) {
                            int minutes = (LoginActivitySmsView.this.time / 1000) / 60;
                            int seconds = (LoginActivitySmsView.this.time / 1000) - (minutes * 60);
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
                            NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                            boolean unused2 = LoginActivitySmsView.this.waitingForEvent = false;
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
                                TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                                req.phone_number = LoginActivitySmsView.this.phone;
                                req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new CancelAccountDeletionActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda2(this), 2);
                            } else if (LoginActivitySmsView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                boolean unused3 = LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            }
                        }
                    }

                    /* renamed from: lambda$run$1$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m1521x1815ec4a(TLObject response, TLRPC.TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda1(this, error));
                        }
                    }

                    /* renamed from: lambda$run$0$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m1520xdvar_bab(TLRPC.TL_error error) {
                        String unused = LoginActivitySmsView.this.lastError = error.text;
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.timeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        /* access modifiers changed from: private */
        public String getCode() {
            if (this.codeField == null) {
                return "";
            }
            StringBuilder codeBuilder = new StringBuilder();
            int a = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (a >= editTextBoldCursorArr.length) {
                    return codeBuilder.toString();
                }
                codeBuilder.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[a].getText().toString()));
                a++;
            }
        }

        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                if (code == null) {
                    code = getCode();
                }
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
                TLRPC.TL_account_confirmPhone req = new TLRPC.TL_account_confirmPhone();
                req.phone_code = code;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(this, req), 2);
            }
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1512xedd2df4e(TLRPC.TL_account_confirmPhone req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(this, error, req));
        }

        /* renamed from: lambda$onNextPressed$6$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1511x5164e2ef(TLRPC.TL_error error, TLRPC.TL_account_confirmPhone req) {
            int i;
            int i2;
            this.this$0.needHideProgress();
            this.nextPressed = false;
            if (error == null) {
                CancelAccountDeletionActivity cancelAccountDeletionActivity = this.this$0;
                PhoneFormat instance = PhoneFormat.getInstance();
                Dialog unused = cancelAccountDeletionActivity.errorDialog = AlertsCreator.showSimpleAlert(cancelAccountDeletionActivity, LocaleController.formatString("CancelLinkSuccess", NUM, instance.format("+" + this.phone)));
                return;
            }
            this.lastError = error.text;
            int i3 = this.currentType;
            if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                createTimer();
            }
            int i4 = this.currentType;
            if (i4 == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i4 == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = true;
            if (this.currentType != 3) {
                AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0]);
            }
            if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                int a = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                    if (a < editTextBoldCursorArr.length) {
                        editTextBoldCursorArr[a].setText("");
                        a++;
                    } else {
                        editTextBoldCursorArr[0].requestFocus();
                        return;
                    }
                }
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                this.this$0.setPage(0, true, (Bundle) null, true);
            }
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
                RLottieDrawable rLottieDrawable = this.hintDrawable;
                if (rLottieDrawable != null) {
                    rLottieDrawable.setCurrentFrame(0);
                }
                AndroidUtilities.runOnUIThread(new CancelAccountDeletionActivity$LoginActivitySmsView$$ExternalSyntheticLambda4(this), 100);
            }
        }

        /* renamed from: lambda$onShow$8$org-telegram-ui-CancelAccountDeletionActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1513xfb9f5ea7() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                int a = editTextBoldCursorArr.length - 1;
                while (true) {
                    if (a < 0) {
                        break;
                    } else if (a == 0 || this.codeField[a].length() != 0) {
                        this.codeField[a].requestFocus();
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        editTextBoldCursorArr2[a].setSelection(editTextBoldCursorArr2[a].length());
                        AndroidUtilities.showKeyboard(this.codeField[a]);
                    } else {
                        a--;
                    }
                }
                this.codeField[a].requestFocus();
                EditTextBoldCursor[] editTextBoldCursorArr22 = this.codeField;
                editTextBoldCursorArr22[a].setSelection(editTextBoldCursorArr22[a].length());
                AndroidUtilities.showKeyboard(this.codeField[a]);
            }
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.start();
            }
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.waitingForEvent && this.codeField != null) {
                if (id == NotificationCenter.didReceiveSmsCode) {
                    this.codeField[0].setText("" + args[0]);
                    onNextPressed((String) null);
                } else if (id == NotificationCenter.didReceiveCall) {
                    String num = "" + args[0];
                    if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                        this.ignoreOnTextChange = true;
                        this.codeField[0].setText(num);
                        this.ignoreOnTextChange = false;
                        onNextPressed((String) null);
                    }
                }
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        SlideView[] slideViewArr = this.views;
        LoginActivitySmsView smsView1 = (LoginActivitySmsView) slideViewArr[1];
        LoginActivitySmsView smsView2 = (LoginActivitySmsView) slideViewArr[2];
        LoginActivitySmsView smsView3 = (LoginActivitySmsView) slideViewArr[3];
        LoginActivitySmsView smsView4 = (LoginActivitySmsView) slideViewArr[4];
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new CancelAccountDeletionActivity$$ExternalSyntheticLambda0(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(((PhoneView) slideViewArr[0]).progressBar, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(smsView1.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        if (smsView1.codeField != null) {
            for (int a = 0; a < smsView1.codeField.length; a++) {
                arrayList.add(new ThemeDescription(smsView1.codeField[a], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView1.codeField[a], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
        arrayList.add(new ThemeDescription((View) smsView1.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView1.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView1.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView2.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        if (smsView2.codeField != null) {
            for (int a2 = 0; a2 < smsView2.codeField.length; a2++) {
                arrayList.add(new ThemeDescription(smsView2.codeField[a2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView2.codeField[a2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
        arrayList.add(new ThemeDescription((View) smsView2.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView2.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView2.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView3.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        if (smsView3.codeField != null) {
            for (int a3 = 0; a3 < smsView3.codeField.length; a3++) {
                arrayList.add(new ThemeDescription(smsView3.codeField[a3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView3.codeField[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
        arrayList.add(new ThemeDescription((View) smsView3.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView3.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView3.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView4.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        if (smsView4.codeField != null) {
            for (int a4 = 0; a4 < smsView4.codeField.length; a4++) {
                arrayList.add(new ThemeDescription(smsView4.codeField[a4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(smsView4.codeField[a4], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
            }
        }
        arrayList.add(new ThemeDescription(smsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressInner"));
        arrayList.add(new ThemeDescription((View) smsView4.progressView, 0, new Class[]{ProgressView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "login_progressOuter"));
        arrayList.add(new ThemeDescription(smsView4.blackImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(smsView4.blueImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(smsView4.blueImageView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "chats_actionBackground"));
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$0$org-telegram-ui-CancelAccountDeletionActivity  reason: not valid java name */
    public /* synthetic */ void m1509xvar_bc9() {
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i < slideViewArr.length) {
                if (slideViewArr[i] instanceof LoginActivity.LoginActivitySmsView) {
                    LoginActivity.LoginActivitySmsView smsView = (LoginActivity.LoginActivitySmsView) slideViewArr[i];
                    if (smsView.hintDrawable != null) {
                        smsView.hintDrawable.setLayerColor("Bubble.**", Theme.getColor("chats_actionBackground"));
                        smsView.hintDrawable.setLayerColor("Phone.**", Theme.getColor("chats_actionBackground"));
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }
}
