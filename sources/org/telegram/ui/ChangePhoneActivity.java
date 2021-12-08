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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
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
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintEditText;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.LoginActivity;

public class ChangePhoneActivity extends BaseFragment {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public boolean checkPermissions = true;
    /* access modifiers changed from: private */
    public int currentViewNum = 0;
    private View doneButton;
    /* access modifiers changed from: private */
    public Dialog permissionsDialog;
    /* access modifiers changed from: private */
    public ArrayList<String> permissionsItems = new ArrayList<>();
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
                    ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed((String) null);
                } else if (id == -1) {
                    ChangePhoneActivity.this.finishFragment();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        ScrollView scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (ChangePhoneActivity.this.currentViewNum == 1 || ChangePhoneActivity.this.currentViewNum == 2 || ChangePhoneActivity.this.currentViewNum == 4) {
                    rectangle.bottom += AndroidUtilities.dp(40.0f);
                }
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int unused = ChangePhoneActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        scrollView.setFillViewport(true);
        this.fragmentView = scrollView;
        FrameLayout frameLayout = new FrameLayout(context2);
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.views[0] = new PhoneView(this, context2);
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

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 6) {
            this.checkPermissions = false;
            int i = this.currentViewNum;
            if (i == 0) {
                this.views[i].onNextPressed((String) null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
        }
    }

    public boolean onBackPressed() {
        int i = this.currentViewNum;
        if (i == 0) {
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
        } else {
            if (this.views[i].onBackPressed(false)) {
                setPage(0, true, (Bundle) null, true);
            }
            return false;
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
        if (page == 3) {
            this.doneButton.setVisibility(8);
        } else {
            if (page == 0) {
                this.checkPermissions = true;
            }
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

    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener {
        /* access modifiers changed from: private */
        public EditTextBoldCursor codeField;
        /* access modifiers changed from: private */
        public HashMap<String, String> codesMap = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<String> countriesArray = new ArrayList<>();
        private HashMap<String, String> countriesMap = new HashMap<>();
        /* access modifiers changed from: private */
        public TextView countryButton;
        /* access modifiers changed from: private */
        public int countryState = 0;
        /* access modifiers changed from: private */
        public boolean ignoreOnPhoneChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange = false;
        /* access modifiers changed from: private */
        public boolean ignoreSelection = false;
        private boolean nextPressed = false;
        /* access modifiers changed from: private */
        public HintEditText phoneField;
        /* access modifiers changed from: private */
        public HashMap<String, String> phoneFormatMap = new HashMap<>();
        /* access modifiers changed from: private */
        public TextView textView;
        /* access modifiers changed from: private */
        public TextView textView2;
        final /* synthetic */ ChangePhoneActivity this$0;
        /* access modifiers changed from: private */
        public View view;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneView(org.telegram.ui.ChangePhoneActivity r30, android.content.Context r31) {
            /*
                r29 = this;
                r1 = r29
                r2 = r30
                r3 = r31
                r1.this$0 = r2
                r1.<init>(r3)
                r4 = 0
                r1.countryState = r4
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r1.countriesArray = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.countriesMap = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.codesMap = r0
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.phoneFormatMap = r0
                r1.ignoreSelection = r4
                r1.ignoreOnTextChange = r4
                r1.ignoreOnPhoneChange = r4
                r1.nextPressed = r4
                r5 = 1
                r1.setOrientation(r5)
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r3)
                r1.countryButton = r0
                r6 = 1099956224(0x41900000, float:18.0)
                r0.setTextSize(r5, r6)
                android.widget.TextView r0 = r1.countryButton
                r7 = 1082130432(0x40800000, float:4.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r0.setPadding(r8, r9, r10, r7)
                android.widget.TextView r0 = r1.countryButton
                java.lang.String r7 = "windowBackgroundWhiteBlackText"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r0.setTextColor(r8)
                android.widget.TextView r0 = r1.countryButton
                r0.setMaxLines(r5)
                android.widget.TextView r0 = r1.countryButton
                r0.setSingleLine(r5)
                android.widget.TextView r0 = r1.countryButton
                android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
                r0.setEllipsize(r8)
                android.widget.TextView r0 = r1.countryButton
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                r9 = 5
                r10 = 3
                if (r8 == 0) goto L_0x007f
                r8 = 5
                goto L_0x0080
            L_0x007f:
                r8 = 3
            L_0x0080:
                r8 = r8 | r5
                r0.setGravity(r8)
                android.widget.TextView r0 = r1.countryButton
                java.lang.String r8 = "listSelectorSDK21"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r11 = 7
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r11)
                r0.setBackground(r8)
                android.widget.TextView r0 = r1.countryButton
                r11 = -1
                r12 = 36
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 1096810496(0x41600000, float:14.0)
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r0, r8)
                android.widget.TextView r0 = r1.countryButton
                org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda0 r8 = new org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda0
                r8.<init>(r1)
                r0.setOnClickListener(r8)
                android.view.View r0 = new android.view.View
                r0.<init>(r3)
                r1.view = r0
                r8 = 1094713344(0x41400000, float:12.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r0.setPadding(r11, r4, r8, r4)
                android.view.View r0 = r1.view
                java.lang.String r8 = "windowBackgroundWhiteGrayLine"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r0.setBackgroundColor(r8)
                android.view.View r0 = r1.view
                r11 = -1
                r12 = 1
                r13 = 1082130432(0x40800000, float:4.0)
                r14 = -1047789568(0xffffffffCLASSNAMECLASSNAME, float:-17.5)
                r15 = 1082130432(0x40800000, float:4.0)
                r16 = 0
                android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r0, r8)
                android.widget.LinearLayout r0 = new android.widget.LinearLayout
                r0.<init>(r3)
                r8 = r0
                r8.setOrientation(r4)
                r12 = -2
                r13 = 0
                r14 = 1101004800(0x41a00000, float:20.0)
                r15 = 0
                android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                r1.addView(r8, r0)
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r3)
                r1.textView = r0
                java.lang.String r11 = "+"
                r0.setText(r11)
                android.widget.TextView r0 = r1.textView
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r0.setTextColor(r11)
                android.widget.TextView r0 = r1.textView
                r0.setTextSize(r5, r6)
                android.widget.TextView r0 = r1.textView
                r11 = -2
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
                r8.addView(r0, r11)
                org.telegram.ui.Components.EditTextBoldCursor r0 = new org.telegram.ui.Components.EditTextBoldCursor
                r0.<init>(r3)
                r1.codeField = r0
                r0.setInputType(r10)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r0.setTextColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r0.setCursorColor(r11)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r11 = 1101004800(0x41a00000, float:20.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r0.setCursorSize(r12)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r12 = 1069547520(0x3fCLASSNAME, float:1.5)
                r0.setCursorWidth(r12)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r3, r4)
                r0.setBackgroundDrawable(r13)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r13 = 1092616192(0x41200000, float:10.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r0.setPadding(r13, r4, r4, r4)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r0.setTextSize(r5, r6)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r0.setMaxLines(r5)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r13 = 19
                r0.setGravity(r13)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r14 = 268435461(0x10000005, float:2.5243564E-29)
                r0.setImeOptions(r14)
                android.text.InputFilter[] r15 = new android.text.InputFilter[r5]
                android.text.InputFilter$LengthFilter r0 = new android.text.InputFilter$LengthFilter
                r0.<init>(r9)
                r15[r4] = r0
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r0.setFilters(r15)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r16 = 55
                r17 = 36
                r18 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
                r19 = 0
                r20 = 1098907648(0x41800000, float:16.0)
                r21 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
                r8.addView(r0, r9)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                org.telegram.ui.ChangePhoneActivity$PhoneView$1 r9 = new org.telegram.ui.ChangePhoneActivity$PhoneView$1
                r9.<init>(r2)
                r0.addTextChangedListener(r9)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda2 r9 = new org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda2
                r9.<init>(r1)
                r0.setOnEditorActionListener(r9)
                org.telegram.ui.Components.HintEditText r0 = new org.telegram.ui.Components.HintEditText
                r0.<init>(r3)
                r1.phoneField = r0
                r0.setInputType(r10)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r0.setTextColor(r9)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                java.lang.String r9 = "windowBackgroundWhiteHintText"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r0.setHintTextColor(r9)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r3, r4)
                r0.setBackgroundDrawable(r9)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.setPadding(r4, r4, r4, r4)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r0.setCursorColor(r7)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r0.setCursorSize(r7)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.setCursorWidth(r12)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.setTextSize(r5, r6)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.setMaxLines(r5)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.setGravity(r13)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.setImeOptions(r14)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r6 = 1108344832(0x42100000, float:36.0)
                r7 = -1
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6)
                r8.addView(r0, r6)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                org.telegram.ui.ChangePhoneActivity$PhoneView$2 r6 = new org.telegram.ui.ChangePhoneActivity$PhoneView$2
                r6.<init>(r2)
                r0.addTextChangedListener(r6)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda3
                r6.<init>(r1)
                r0.setOnEditorActionListener(r6)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda1
                r6.<init>(r1)
                r0.setOnKeyListener(r6)
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r3)
                r1.textView2 = r0
                r6 = 2131624726(0x7f0e0316, float:1.887664E38)
                java.lang.String r9 = "ChangePhoneHelp"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
                r0.setText(r6)
                android.widget.TextView r0 = r1.textView2
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r0.setTextColor(r6)
                android.widget.TextView r0 = r1.textView2
                r6 = 1096810496(0x41600000, float:14.0)
                r0.setTextSize(r5, r6)
                android.widget.TextView r0 = r1.textView2
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x025a
                r6 = 5
                goto L_0x025b
            L_0x025a:
                r6 = 3
            L_0x025b:
                r0.setGravity(r6)
                android.widget.TextView r0 = r1.textView2
                r6 = 1073741824(0x40000000, float:2.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                r9 = 1065353216(0x3var_, float:1.0)
                r0.setLineSpacing(r6, r9)
                android.widget.TextView r0 = r1.textView2
                r22 = -2
                r23 = -2
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0279
                r24 = 5
                goto L_0x027b
            L_0x0279:
                r24 = 3
            L_0x027b:
                r25 = 0
                r26 = 28
                r27 = 0
                r28 = 10
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
                r1.addView(r0, r6)
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r6 = r0
                java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x02e8 }
                java.io.InputStreamReader r9 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x02e8 }
                android.content.res.Resources r11 = r29.getResources()     // Catch:{ Exception -> 0x02e8 }
                android.content.res.AssetManager r11 = r11.getAssets()     // Catch:{ Exception -> 0x02e8 }
                java.lang.String r12 = "countries.txt"
                java.io.InputStream r11 = r11.open(r12)     // Catch:{ Exception -> 0x02e8 }
                r9.<init>(r11)     // Catch:{ Exception -> 0x02e8 }
                r0.<init>(r9)     // Catch:{ Exception -> 0x02e8 }
            L_0x02a8:
                java.lang.String r9 = r0.readLine()     // Catch:{ Exception -> 0x02e8 }
                r11 = r9
                if (r9 == 0) goto L_0x02e4
                java.lang.String r9 = ";"
                java.lang.String[] r9 = r11.split(r9)     // Catch:{ Exception -> 0x02e8 }
                java.util.ArrayList<java.lang.String> r12 = r1.countriesArray     // Catch:{ Exception -> 0x02e8 }
                r13 = 2
                r14 = r9[r13]     // Catch:{ Exception -> 0x02e8 }
                r12.add(r4, r14)     // Catch:{ Exception -> 0x02e8 }
                java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.countriesMap     // Catch:{ Exception -> 0x02e8 }
                r14 = r9[r13]     // Catch:{ Exception -> 0x02e8 }
                r7 = r9[r4]     // Catch:{ Exception -> 0x02e8 }
                r12.put(r14, r7)     // Catch:{ Exception -> 0x02e8 }
                java.util.HashMap<java.lang.String, java.lang.String> r7 = r1.codesMap     // Catch:{ Exception -> 0x02e8 }
                r12 = r9[r4]     // Catch:{ Exception -> 0x02e8 }
                r14 = r9[r13]     // Catch:{ Exception -> 0x02e8 }
                r7.put(r12, r14)     // Catch:{ Exception -> 0x02e8 }
                int r7 = r9.length     // Catch:{ Exception -> 0x02e8 }
                if (r7 <= r10) goto L_0x02db
                java.util.HashMap<java.lang.String, java.lang.String> r7 = r1.phoneFormatMap     // Catch:{ Exception -> 0x02e8 }
                r12 = r9[r4]     // Catch:{ Exception -> 0x02e8 }
                r14 = r9[r10]     // Catch:{ Exception -> 0x02e8 }
                r7.put(r12, r14)     // Catch:{ Exception -> 0x02e8 }
            L_0x02db:
                r7 = r9[r5]     // Catch:{ Exception -> 0x02e8 }
                r12 = r9[r13]     // Catch:{ Exception -> 0x02e8 }
                r6.put(r7, r12)     // Catch:{ Exception -> 0x02e8 }
                r7 = -1
                goto L_0x02a8
            L_0x02e4:
                r0.close()     // Catch:{ Exception -> 0x02e8 }
                goto L_0x02ec
            L_0x02e8:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x02ec:
                java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
                org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6 r7 = org.telegram.ui.ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6.INSTANCE
                java.util.Collections.sort(r0, r7)
                r7 = 0
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x030a }
                java.lang.String r9 = "phone"
                java.lang.Object r0 = r0.getSystemService(r9)     // Catch:{ Exception -> 0x030a }
                android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x030a }
                if (r0 == 0) goto L_0x0309
                java.lang.String r9 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x030a }
                java.lang.String r9 = r9.toUpperCase()     // Catch:{ Exception -> 0x030a }
                r7 = r9
            L_0x0309:
                goto L_0x030e
            L_0x030a:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x030e:
                if (r7 == 0) goto L_0x0330
                java.lang.Object r0 = r6.get(r7)
                java.lang.String r0 = (java.lang.String) r0
                if (r0 == 0) goto L_0x0330
                java.util.ArrayList<java.lang.String> r9 = r1.countriesArray
                int r9 = r9.indexOf(r0)
                r10 = -1
                if (r9 == r10) goto L_0x0330
                org.telegram.ui.Components.EditTextBoldCursor r10 = r1.codeField
                java.util.HashMap<java.lang.String, java.lang.String> r11 = r1.countriesMap
                java.lang.Object r11 = r11.get(r0)
                java.lang.CharSequence r11 = (java.lang.CharSequence) r11
                r10.setText(r11)
                r1.countryState = r4
            L_0x0330:
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 != 0) goto L_0x034e
                android.widget.TextView r0 = r1.countryButton
                r4 = 2131624961(0x7f0e0401, float:1.8877116E38)
                java.lang.String r9 = "ChooseCountry"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
                r0.setText(r4)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r4 = 0
                r0.setHintText(r4)
                r1.countryState = r5
            L_0x034e:
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                int r0 = r0.length()
                if (r0 == 0) goto L_0x036a
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                org.telegram.messenger.AndroidUtilities.showKeyboard(r0)
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                r0.requestFocus()
                org.telegram.ui.Components.HintEditText r0 = r1.phoneField
                int r4 = r0.length()
                r0.setSelection(r4)
                goto L_0x0374
            L_0x036a:
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                org.telegram.messenger.AndroidUtilities.showKeyboard(r0)
                org.telegram.ui.Components.EditTextBoldCursor r0 = r1.codeField
                r0.requestFocus()
            L_0x0374:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChangePhoneActivity.PhoneView.<init>(org.telegram.ui.ChangePhoneActivity, android.content.Context):void");
        }

        /* renamed from: lambda$new$2$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1593lambda$new$2$orgtelegramuiChangePhoneActivity$PhoneView(View view2) {
            CountrySelectActivity fragment = new CountrySelectActivity(true);
            fragment.setCountrySelectActivityDelegate(new ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda8(this));
            this.this$0.presentFragment(fragment);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1592lambda$new$1$orgtelegramuiChangePhoneActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry(country.name);
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda4(this), 300);
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1591lambda$new$0$orgtelegramuiChangePhoneActivity$PhoneView() {
            AndroidUtilities.showKeyboard(this.phoneField);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m1594lambda$new$3$orgtelegramuiChangePhoneActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.phoneField.requestFocus();
            HintEditText hintEditText = this.phoneField;
            hintEditText.setSelection(hintEditText.length());
            return true;
        }

        /* renamed from: lambda$new$4$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m1595lambda$new$4$orgtelegramuiChangePhoneActivity$PhoneView(TextView textView3, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed((String) null);
            return true;
        }

        /* renamed from: lambda$new$5$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ boolean m1596lambda$new$5$orgtelegramuiChangePhoneActivity$PhoneView(View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.phoneField.length() != 0) {
                return false;
            }
            this.codeField.requestFocus();
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.codeField.dispatchKeyEvent(event);
            return true;
        }

        public void selectCountry(String name) {
            if (this.countriesArray.indexOf(name) != -1) {
                this.ignoreOnTextChange = true;
                String code = this.countriesMap.get(name);
                this.codeField.setText(code);
                this.countryButton.setText(name);
                String hint = this.phoneFormatMap.get(code);
                this.phoneField.setHintText(hint != null ? hint.replace('X', 8211) : null);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        }

        public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            this.codeField.setText(this.countriesMap.get(this.countriesArray.get(i)));
            this.ignoreOnTextChange = false;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        public void onNextPressed(String code) {
            if (this.this$0.getParentActivity() != null && !this.nextPressed) {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                boolean simcardAvailable = (tm.getSimState() == 1 || tm.getPhoneType() == 0) ? false : true;
                boolean allowCall = true;
                if (Build.VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = this.this$0.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    if (this.this$0.checkPermissions) {
                        this.this$0.permissionsItems.clear();
                        if (!allowCall) {
                            this.this$0.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!this.this$0.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (preferences.getBoolean("firstlogin", true) || this.this$0.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                                preferences.edit().putBoolean("firstlogin", false).commit();
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                builder.setMessage(LocaleController.getString("AllowReadCall", NUM));
                                ChangePhoneActivity changePhoneActivity = this.this$0;
                                Dialog unused = changePhoneActivity.permissionsDialog = changePhoneActivity.showDialog(builder.create());
                                return;
                            }
                            this.this$0.getParentActivity().requestPermissions((String[]) this.this$0.permissionsItems.toArray(new String[0]), 6);
                            return;
                        }
                    }
                }
                if (this.countryState == 1) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("ChooseCountry", NUM));
                } else if (this.codeField.length() == 0) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("InvalidPhoneNumber", NUM));
                } else {
                    TLRPC.TL_account_sendChangePhoneCode req = new TLRPC.TL_account_sendChangePhoneCode();
                    String phone = PhoneFormat.stripExceptNumbers("" + this.codeField.getText() + this.phoneField.getText());
                    req.phone_number = phone;
                    req.settings = new TLRPC.TL_codeSettings();
                    req.settings.allow_flashcall = simcardAvailable && allowCall;
                    req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                    SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (req.settings.allow_app_hash) {
                        preferences2.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
                    } else {
                        preferences2.edit().remove("sms_hash").commit();
                    }
                    if (req.settings.allow_flashcall) {
                        try {
                            String number = tm.getLine1Number();
                            if (!TextUtils.isEmpty(number)) {
                                req.settings.current_number = PhoneNumberUtils.compare(phone, number);
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
                    params.putString("phone", "+" + this.codeField.getText() + " " + this.phoneField.getText());
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    this.this$0.needShowProgress();
                    ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda7(this, params, req), 2);
                }
            }
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1598x83fce3e8(Bundle params, TLRPC.TL_account_sendChangePhoneCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda5(this, error, params, response, req));
        }

        /* renamed from: lambda$onNextPressed$6$org-telegram-ui-ChangePhoneActivity$PhoneView  reason: not valid java name */
        public /* synthetic */ void m1597xe82bda7(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_account_sendChangePhoneCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else {
                AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, params.getString("phone"));
            }
            this.this$0.needHideProgress();
        }

        public void onShow() {
            super.onShow();
            if (this.phoneField == null) {
                return;
            }
            if (this.codeField.length() != 0) {
                AndroidUtilities.showKeyboard(this.phoneField);
                this.phoneField.requestFocus();
                HintEditText hintEditText = this.phoneField;
                hintEditText.setSelection(hintEditText.length());
                return;
            }
            AndroidUtilities.showKeyboard(this.codeField);
            this.codeField.requestFocus();
        }

        public String getHeaderName() {
            return LocaleController.getString("ChangePhoneNewNumber", NUM);
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
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public int currentType;
        private String emailPhone;
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
        private String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        /* access modifiers changed from: private */
        public String requestPhone;
        final /* synthetic */ ChangePhoneActivity this$0;
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

        static /* synthetic */ int access$2526(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.codeTime;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$3126(LoginActivitySmsView x0, double x1) {
            double d = (double) x0.time;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.time = i;
            return i;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoginActivitySmsView(org.telegram.ui.ChangePhoneActivity r31, android.content.Context r32, int r33) {
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
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r5.setTextColor(r7)
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
                if (r11 == 0) goto L_0x007b
                r11 = 5
                goto L_0x007c
            L_0x007b:
                r11 = 3
            L_0x007c:
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
                if (r5 != r13) goto L_0x0129
                android.widget.TextView r5 = r0.confirmTextView
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x009d
                r9 = 5
                goto L_0x009e
            L_0x009d:
                r9 = 3
            L_0x009e:
                r9 = r9 | 48
                r5.setGravity(r9)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00ae
                r9 = 5
                goto L_0x00af
            L_0x00ae:
                r9 = 3
            L_0x00af:
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r9)
                r0.addView(r5, r9)
                android.widget.ImageView r9 = new android.widget.ImageView
                r9.<init>(r2)
                r15 = 2131165959(0x7var_, float:1.794615E38)
                r9.setImageResource(r15)
                boolean r15 = org.telegram.messenger.LocaleController.isRTL
                if (r15 == 0) goto L_0x00f9
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
                if (r18 == 0) goto L_0x00e7
                r18 = 5
                goto L_0x00e9
            L_0x00e7:
                r18 = 3
            L_0x00e9:
                r19 = 1118044160(0x42a40000, float:82.0)
                r20 = 0
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r15, r12)
                goto L_0x0127
            L_0x00f9:
                android.widget.TextView r12 = r0.confirmTextView
                r15 = -1
                r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r17 = org.telegram.messenger.LocaleController.isRTL
                if (r17 == 0) goto L_0x0105
                r17 = 5
                goto L_0x0107
            L_0x0105:
                r17 = 3
            L_0x0107:
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
            L_0x0127:
                goto L_0x022e
            L_0x0129:
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r11)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
                int r12 = r0.currentType
                java.lang.String r15 = "chats_actionBackground"
                if (r12 != r4) goto L_0x01a9
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
                goto L_0x020c
            L_0x01a9:
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
                r24 = 2131558494(0x7f0d005e, float:1.8742305E38)
                r9 = 1115684864(0x42800000, float:64.0)
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
                r23 = -2
                r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
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
            L_0x020c:
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
            L_0x022e:
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
                if (r5 != r13) goto L_0x024f
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 8
                r5.setVisibility(r9)
            L_0x024f:
                org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$1 r5 = new org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$1
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
                if (r5 != r13) goto L_0x02af
                android.widget.TextView r5 = r0.timeText
                r11 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.timeText
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x027e
                r11 = 5
                goto L_0x027f
            L_0x027e:
                r11 = 3
            L_0x027f:
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r11)
                org.telegram.ui.ChangePhoneActivity$ProgressView r5 = new org.telegram.ui.ChangePhoneActivity$ProgressView
                r5.<init>(r2)
                r0.progressView = r5
                android.widget.TextView r5 = r0.timeText
                boolean r11 = org.telegram.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0295
                r12 = 5
                goto L_0x0296
            L_0x0295:
                r12 = 3
            L_0x0296:
                r5.setGravity(r12)
                org.telegram.ui.ChangePhoneActivity$ProgressView r5 = r0.progressView
                r17 = -1
                r18 = 3
                r19 = 0
                r20 = 1094713344(0x41400000, float:12.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
                r0.addView(r5, r11)
                goto L_0x02d1
            L_0x02af:
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
            L_0x02d1:
                org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$2 r5 = new org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$2
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
                if (r5 != r4) goto L_0x0317
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625277(0x7f0e053d, float:1.8877757E38)
                java.lang.String r6 = "DidNotGetTheCodeSms"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x0325
            L_0x0317:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131625275(0x7f0e053b, float:1.8877753E38)
                java.lang.String r6 = "DidNotGetTheCode"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
            L_0x0325:
                android.widget.TextView r4 = r0.problemText
                r5 = 49
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r5)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.problemText
                org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda3
                r5.<init>(r0)
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView.<init>(org.telegram.ui.ChangePhoneActivity, android.content.Context, int):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1577x8d84ea28(View v) {
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
                    mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.emailPhone);
                    mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
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
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
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
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda9(this, params, req), 2);
        }

        /* renamed from: lambda$resendCode$3$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1584x7413fc3(Bundle params, TLRPC.TL_auth_resendCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda7(this, error, params, response, req));
        }

        /* renamed from: lambda$resendCode$2$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1583x7a5428a4(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda2(this));
                }
            }
            this.this$0.needHideProgress();
        }

        /* renamed from: lambda$resendCode$1$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1582xed671185(DialogInterface dialog1, int which) {
            onBackPressed(true);
            this.this$0.finishFragment();
        }

        public String getHeaderName() {
            if (this.currentType == 1) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", NUM);
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
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
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.emailPhone = bundle.getString("ephone");
                this.requestPhone = bundle.getString("phoneFormated");
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
                int i6 = 8;
                int i7 = 0;
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
                            this.codeField[a2].setVisibility(i6);
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
                        this.codeField[a2].setOnKeyListener(new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda4(this, num));
                        this.codeField[a2].setOnEditorActionListener(new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(this));
                        a2++;
                        i6 = 8;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    String number = PhoneFormat.getInstance().format(this.phone);
                    CharSequence str = "";
                    int i8 = this.currentType;
                    if (i8 == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", NUM));
                    } else if (i8 == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", NUM, LocaleController.addNbsp(number)));
                    } else if (i8 == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", NUM, LocaleController.addNbsp(number)));
                    } else if (i8 == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", NUM, LocaleController.addNbsp(number)));
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
                    int i9 = this.currentType;
                    if (i9 == 1) {
                        this.problemText.setVisibility(0);
                        this.timeText.setVisibility(8);
                    } else if (i9 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i10 = this.nextType;
                        if (i10 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", NUM, 1, 0));
                        } else if (i10 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", NUM, 1, 0));
                        }
                        createTimer();
                    } else if (i9 == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        textView.setVisibility(i7);
                        createTimer();
                    } else if (i9 == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", NUM, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView2 = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        textView2.setVisibility(i7);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        /* renamed from: lambda$setParams$4$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ boolean m1585x8var_cfc(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        /* renamed from: lambda$setParams$5$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ boolean m1586x1c7e341b(TextView textView, int i, KeyEvent keyEvent) {
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
                        AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$4$$ExternalSyntheticLambda0(this));
                    }

                    /* renamed from: lambda$run$0$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView$4  reason: not valid java name */
                    public /* synthetic */ void m1587x3c3d6bad() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$2400 = LoginActivitySmsView.this.lastCodeTime;
                        Double.isNaN(currentTime);
                        double unused = LoginActivitySmsView.this.lastCodeTime = currentTime;
                        LoginActivitySmsView.access$2526(LoginActivitySmsView.this, currentTime - access$2400);
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
                            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0(this));
                        }
                    }

                    /* renamed from: lambda$run$2$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m1590x4845026c() {
                        double currentTime = (double) System.currentTimeMillis();
                        double access$3000 = LoginActivitySmsView.this.lastCurrentTime;
                        Double.isNaN(currentTime);
                        LoginActivitySmsView.access$3126(LoginActivitySmsView.this, currentTime - access$3000);
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
                                req.phone_number = LoginActivitySmsView.this.requestPhone;
                                req.phone_code_hash = LoginActivitySmsView.this.phoneHash;
                                ConnectionsManager.getInstance(LoginActivitySmsView.this.this$0.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda2(this), 2);
                            } else if (LoginActivitySmsView.this.nextType == 3) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                                boolean unused3 = LoginActivitySmsView.this.waitingForEvent = false;
                                LoginActivitySmsView.this.destroyCodeTimer();
                                LoginActivitySmsView.this.resendCode();
                            }
                        }
                    }

                    /* renamed from: lambda$run$1$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m1589x4241370d(TLObject response, TLRPC.TL_error error) {
                        if (error != null && error.text != null) {
                            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda1(this, error));
                        }
                    }

                    /* renamed from: lambda$run$0$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView$5  reason: not valid java name */
                    public /* synthetic */ void m1588x3c3d6bae(TLRPC.TL_error error) {
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
                String code2 = getCode();
                if (TextUtils.isEmpty(code2)) {
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
                TLRPC.TL_account_changePhone req = new TLRPC.TL_account_changePhone();
                req.phone_number = this.requestPhone;
                req.phone_code = code2;
                req.phone_code_hash = this.phoneHash;
                destroyTimer();
                this.this$0.needShowProgress();
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10(this, req), 2);
            }
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1580x367b511(TLRPC.TL_account_changePhone req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(this, error, response, req));
        }

        /* renamed from: lambda$onNextPressed$6$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1579x767a9df2(TLRPC.TL_error error, TLObject response, TLRPC.TL_account_changePhone req) {
            int i;
            int i2;
            this.this$0.needHideProgress();
            this.nextPressed = false;
            if (error == null) {
                TLRPC.User user = (TLRPC.User) response;
                destroyTimer();
                destroyCodeTimer();
                UserConfig.getInstance(this.this$0.currentAccount).setCurrentUser(user);
                UserConfig.getInstance(this.this$0.currentAccount).saveConfig(true);
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user);
                MessagesStorage.getInstance(this.this$0.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
                MessagesController.getInstance(this.this$0.currentAccount).putUser(user, false);
                this.this$0.finishFragment();
                NotificationCenter.getInstance(this.this$0.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                this.this$0.getMessagesController().removeSuggestion(0, "VALIDATE_PHONE_NUMBER");
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

        public boolean onBackPressed(boolean force) {
            if (!force) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("StopVerification", NUM));
                builder.setPositiveButton(LocaleController.getString("Continue", NUM), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("Stop", NUM), new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda0(this));
                this.this$0.showDialog(builder.create());
                return false;
            }
            TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda1.INSTANCE, 10);
            destroyTimer();
            destroyCodeTimer();
            this.currentParams = null;
            int i = this.currentType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            return true;
        }

        /* renamed from: lambda$onBackPressed$8$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1578x91b6var_(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            this.this$0.setPage(0, true, (Bundle) null, true);
        }

        static /* synthetic */ void lambda$onBackPressed$9(TLObject response, TLRPC.TL_error error) {
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
                AndroidUtilities.runOnUIThread(new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(this), 100);
            }
        }

        /* renamed from: lambda$onShow$10$org-telegram-ui-ChangePhoneActivity$LoginActivitySmsView  reason: not valid java name */
        public /* synthetic */ void m1581xfcda23ed() {
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
        PhoneView phoneView = (PhoneView) slideViewArr[0];
        LoginActivitySmsView smsView1 = (LoginActivitySmsView) slideViewArr[1];
        LoginActivitySmsView smsView2 = (LoginActivitySmsView) slideViewArr[2];
        LoginActivitySmsView smsView3 = (LoginActivitySmsView) slideViewArr[3];
        LoginActivitySmsView smsView4 = (LoginActivitySmsView) slideViewArr[4];
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ChangePhoneActivity$$ExternalSyntheticLambda0(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.countryButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(phoneView.view, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayLine"));
        arrayList.add(new ThemeDescription(phoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(phoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(phoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(phoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(smsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
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

    /* renamed from: lambda$getThemeDescriptions$0$org-telegram-ui-ChangePhoneActivity  reason: not valid java name */
    public /* synthetic */ void m1576xCLASSNAME() {
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
