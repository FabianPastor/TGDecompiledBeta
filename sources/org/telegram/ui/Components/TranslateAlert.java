package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class TranslateAlert extends Dialog {
    /* access modifiers changed from: private */
    public Spannable allTexts;
    private FrameLayout allTextsContainer;
    /* access modifiers changed from: private */
    public TextView allTextsView;
    /* access modifiers changed from: private */
    public boolean allowScroll = true;
    private ImageView backButton;
    protected ColorDrawable backDrawable = new ColorDrawable(-16777216) {
        public void setAlpha(int i) {
            super.setAlpha(i);
            TranslateAlert.this.container.invalidate();
        }
    };
    private Rect backRect = new Rect();
    private int blockIndex = 0;
    private Rect buttonRect = new Rect();
    private FrameLayout buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    /* access modifiers changed from: private */
    public FrameLayout container;
    /* access modifiers changed from: private */
    public float containerOpenAnimationT = 0.0f;
    private Rect containerRect = new Rect();
    private FrameLayout contentView;
    /* access modifiers changed from: private */
    public boolean dismissed = false;
    /* access modifiers changed from: private */
    public BaseFragment fragment;
    /* access modifiers changed from: private */
    public String fromLanguage;
    private boolean fromScrollRect = false;
    private float fromScrollViewY = 0.0f;
    private float fromScrollY = 0.0f;
    private boolean fromTranslateMoreView = false;
    private float fromY = 0.0f;
    private FrameLayout header;
    private FrameLayout.LayoutParams headerLayout;
    private FrameLayout headerShadowView;
    private boolean loaded = false;
    private boolean loading = false;
    private boolean maybeScrolling = false;
    /* access modifiers changed from: private */
    public long minFetchingDuration = 1000;
    private ValueAnimator openAnimationToAnimator = null;
    /* access modifiers changed from: private */
    public boolean openAnimationToAnimatorPriority = false;
    private ValueAnimator openingAnimator;
    /* access modifiers changed from: private */
    public boolean openingAnimatorPriority = false;
    /* access modifiers changed from: private */
    public float openingT = 0.0f;
    /* access modifiers changed from: private */
    public ClickableSpan pressedLink;
    private boolean pressedOutside = false;
    private Rect scrollRect = new Rect();
    /* access modifiers changed from: private */
    public NestedScrollView scrollView;
    private FrameLayout.LayoutParams scrollViewLayout;
    private ValueAnimator scrollerToBottom = null;
    private boolean scrolling = false;
    private FrameLayout.LayoutParams subtitleLayout;
    private LoadingTextView subtitleView;
    /* access modifiers changed from: private */
    public ArrayList<CharSequence> textBlocks;
    private Rect textRect = new Rect();
    /* access modifiers changed from: private */
    public FrameLayout textsContainerView;
    private LinearLayout textsView;
    private FrameLayout.LayoutParams titleLayout;
    private TextView titleView;
    /* access modifiers changed from: private */
    public String toLanguage;
    private Rect translateMoreRect = new Rect();
    private TextView translateMoreView;
    /* access modifiers changed from: private */
    public String[] userAgents = {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"};

    public interface OnTranslationFail {
        void run(boolean z);
    }

    public interface OnTranslationSuccess {
        void run(String str, String str2);
    }

    /* access modifiers changed from: private */
    public void openAnimation(float f) {
        float f2 = 1.0f;
        float min = Math.min(Math.max(f, 0.0f), 1.0f);
        this.containerOpenAnimationT = min;
        this.container.forceLayout();
        this.titleView.setScaleX(AndroidUtilities.lerp(1.0f, 0.9473f, min));
        this.titleView.setScaleY(AndroidUtilities.lerp(1.0f, 0.9473f, min));
        this.titleLayout.topMargin = AndroidUtilities.dp(AndroidUtilities.lerp(22.0f, 8.0f, min));
        this.titleLayout.leftMargin = AndroidUtilities.dp(AndroidUtilities.lerp(22.0f, 72.0f, min));
        this.titleView.setLayoutParams(this.titleLayout);
        this.subtitleLayout.topMargin = AndroidUtilities.dp(AndroidUtilities.lerp(47.0f, 30.0f, min)) - LoadingTextView.padVert;
        this.subtitleLayout.leftMargin = AndroidUtilities.dp(AndroidUtilities.lerp(22.0f, 72.0f, min)) - LoadingTextView.padHorz;
        this.subtitleView.setLayoutParams(this.subtitleLayout);
        this.backButton.setAlpha(min);
        float f3 = (0.25f * min) + 0.75f;
        this.backButton.setScaleX(f3);
        this.backButton.setScaleY(f3);
        this.backButton.setClickable(min > 0.5f);
        FrameLayout frameLayout = this.headerShadowView;
        if (this.scrollView.getScrollY() <= 0) {
            f2 = min;
        }
        frameLayout.setAlpha(f2);
        this.headerLayout.height = (int) AndroidUtilities.lerp((float) AndroidUtilities.dp(70.0f), (float) AndroidUtilities.dp(56.0f), min);
        this.header.setLayoutParams(this.headerLayout);
        this.scrollViewLayout.topMargin = (int) AndroidUtilities.lerp((float) AndroidUtilities.dp(70.0f), (float) AndroidUtilities.dp(56.0f), min);
        this.scrollView.setLayoutParams(this.scrollViewLayout);
    }

    private void openAnimationTo(float f, boolean z) {
        if (!this.openAnimationToAnimatorPriority || z) {
            this.openAnimationToAnimatorPriority = z;
            float min = Math.min(Math.max(f, 0.0f), 1.0f);
            ValueAnimator valueAnimator = this.openAnimationToAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.containerOpenAnimationT, min});
            this.openAnimationToAnimator = ofFloat;
            ofFloat.addUpdateListener(new TranslateAlert$$ExternalSyntheticLambda0(this));
            this.openAnimationToAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    boolean unused = TranslateAlert.this.openAnimationToAnimatorPriority = false;
                }

                public void onAnimationCancel(Animator animator) {
                    boolean unused = TranslateAlert.this.openAnimationToAnimatorPriority = false;
                }
            });
            this.openAnimationToAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.openAnimationToAnimator.setDuration(220);
            this.openAnimationToAnimator.start();
            if (((double) min) >= 0.5d && this.blockIndex <= 1) {
                fetchNext();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openAnimationTo$0(ValueAnimator valueAnimator) {
        openAnimation(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public int minHeight() {
        LinearLayout linearLayout = this.textsView;
        return (linearLayout == null ? 0 : linearLayout.getMeasuredHeight()) + AndroidUtilities.dp(147.0f);
    }

    /* access modifiers changed from: private */
    public boolean canExpand() {
        return this.textsView.getChildCount() < this.textBlocks.size() || minHeight() >= Math.min(AndroidUtilities.dp(550.0f), AndroidUtilities.displayMetrics.heightPixels / 2);
    }

    /* access modifiers changed from: private */
    public void updateCanExpand() {
        boolean canExpand = canExpand();
        float f = 0.0f;
        if (this.containerOpenAnimationT > 0.0f && !canExpand) {
            openAnimationTo(0.0f, false);
        }
        ViewPropertyAnimator alpha = this.buttonShadowView.animate().alpha(canExpand ? 1.0f : 0.0f);
        float alpha2 = this.buttonShadowView.getAlpha();
        if (canExpand) {
            f = 1.0f;
        }
        alpha.setDuration((long) (Math.abs(alpha2 - f) * 220.0f)).start();
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TranslateAlert(org.telegram.ui.ActionBar.BaseFragment r30, android.content.Context r31, java.lang.String r32, java.lang.String r33, java.lang.CharSequence r34) {
        /*
            r29 = this;
            r0 = r29
            r1 = r31
            r2 = r32
            r3 = r33
            r4 = 2131689509(0x7f0var_, float:1.9008035E38)
            r0.<init>(r1, r4)
            r4 = 0
            r0.blockIndex = r4
            r5 = 0
            r0.containerOpenAnimationT = r5
            r0.openAnimationToAnimatorPriority = r4
            r6 = 0
            r0.openAnimationToAnimator = r6
            r7 = 1
            r0.allowScroll = r7
            r0.scrollerToBottom = r6
            r0.fromScrollY = r5
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.containerRect = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.textRect = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.translateMoreRect = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.buttonRect = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.backRect = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r0.scrollRect = r6
            r0.fromY = r5
            r0.pressedOutside = r4
            r0.maybeScrolling = r4
            r0.scrolling = r4
            r0.fromScrollRect = r4
            r0.fromTranslateMoreView = r4
            r0.fromScrollViewY = r5
            r0.openingT = r5
            org.telegram.ui.Components.TranslateAlert$9 r6 = new org.telegram.ui.Components.TranslateAlert$9
            r8 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r6.<init>(r8)
            r0.backDrawable = r6
            r0.dismissed = r4
            r0.openingAnimatorPriority = r4
            r0.loading = r4
            r0.loaded = r4
            r6 = 6
            java.lang.String[] r6 = new java.lang.String[r6]
            java.lang.String r8 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
            r6[r4] = r8
            java.lang.String r8 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36"
            r6[r7] = r8
            java.lang.String r8 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0"
            r9 = 2
            r6[r9] = r8
            java.lang.String r8 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0"
            r10 = 3
            r6[r10] = r8
            java.lang.String r8 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"
            r11 = 4
            r6[r11] = r8
            java.lang.String r8 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
            r12 = 5
            r6[r12] = r8
            r0.userAgents = r6
            r13 = 1000(0x3e8, double:4.94E-321)
            r0.minFetchingDuration = r13
            r6 = r30
            r0.fragment = r6
            if (r2 == 0) goto L_0x00a1
            java.lang.String r6 = "und"
            boolean r6 = r2.equals(r6)
            if (r6 == 0) goto L_0x00a1
            java.lang.String r6 = "auto"
            goto L_0x00a2
        L_0x00a1:
            r6 = r2
        L_0x00a2:
            r0.fromLanguage = r6
            r0.toLanguage = r3
            r6 = 1024(0x400, float:1.435E-42)
            r8 = r34
            java.util.ArrayList r6 = r0.cutInBlocks(r8, r6)
            r0.textBlocks = r6
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 21
            r13 = 30
            if (r6 < r13) goto L_0x00c3
            android.view.Window r14 = r29.getWindow()
            r15 = -2147483392(0xfffffffvar_, float:-3.59E-43)
            r14.addFlags(r15)
            goto L_0x00cf
        L_0x00c3:
            if (r6 < r8) goto L_0x00cf
            android.view.Window r14 = r29.getWindow()
            r15 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r14.addFlags(r15)
        L_0x00cf:
            android.widget.FrameLayout r14 = new android.widget.FrameLayout
            r14.<init>(r1)
            r0.contentView = r14
            android.graphics.drawable.ColorDrawable r15 = r0.backDrawable
            r14.setBackground(r15)
            android.widget.FrameLayout r14 = r0.contentView
            r14.setClipChildren(r4)
            android.widget.FrameLayout r14 = r0.contentView
            r14.setClipToPadding(r4)
            if (r6 < r8) goto L_0x00fd
            android.widget.FrameLayout r8 = r0.contentView
            r8.setFitsSystemWindows(r7)
            if (r6 < r13) goto L_0x00f6
            android.widget.FrameLayout r6 = r0.contentView
            r8 = 1792(0x700, float:2.511E-42)
            r6.setSystemUiVisibility(r8)
            goto L_0x00fd
        L_0x00f6:
            android.widget.FrameLayout r6 = r0.contentView
            r8 = 1280(0x500, float:1.794E-42)
            r6.setSystemUiVisibility(r8)
        L_0x00fd:
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>()
            java.lang.String r8 = "dialogBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setColor(r8)
            r8 = 1073741824(0x40000000, float:2.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r13 = -1087834685(0xffffffffbvar_f5c3, float:-0.66)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r14 = 503316480(0x1e000000, float:6.7762636E-21)
            r6.setShadowLayer(r8, r5, r13, r14)
            org.telegram.ui.Components.TranslateAlert$2 r8 = new org.telegram.ui.Components.TranslateAlert$2
            r8.<init>(r1, r6)
            r0.container = r8
            r8.setWillNotDraw(r4)
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r0.header = r6
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.titleView = r6
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x0141
            int r8 = r6.getWidth()
            float r8 = (float) r8
            goto L_0x0142
        L_0x0141:
            r8 = 0
        L_0x0142:
            r6.setPivotX(r8)
            android.widget.TextView r6 = r0.titleView
            r6.setPivotY(r5)
            android.widget.TextView r6 = r0.titleView
            r6.setLines(r7)
            android.widget.TextView r6 = r0.titleView
            r8 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.String r13 = "AutomaticTranslation"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r8)
            r6.setText(r8)
            android.widget.TextView r6 = r0.titleView
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x0165
            r8 = 5
            goto L_0x0166
        L_0x0165:
            r8 = 3
        L_0x0166:
            r6.setGravity(r8)
            android.widget.TextView r6 = r0.titleView
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r6.setTypeface(r13)
            android.widget.TextView r6 = r0.titleView
            java.lang.String r13 = "dialogTextBlack"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6.setTextColor(r14)
            android.widget.TextView r6 = r0.titleView
            r14 = 1100480512(0x41980000, float:19.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r6.setTextSize(r4, r14)
            android.widget.FrameLayout r6 = r0.header
            android.widget.TextView r14 = r0.titleView
            r15 = -1
            r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r17 = 55
            r18 = 1102053376(0x41b00000, float:22.0)
            r19 = 1102053376(0x41b00000, float:22.0)
            r20 = 1102053376(0x41b00000, float:22.0)
            r21 = 0
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r0.titleLayout = r15
            r6.addView(r14, r15)
            android.widget.TextView r6 = r0.titleView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6 r14 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6
            r14.<init>(r0)
            r6.post(r14)
            java.lang.String r2 = r0.languageName(r2)
            java.lang.String r3 = r0.languageName(r3)
            r6 = 2131625808(0x7f0e0750, float:1.8878834E38)
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.String r14 = ""
            if (r2 == 0) goto L_0x01c2
            r15 = r2
            goto L_0x01c3
        L_0x01c2:
            r15 = r14
        L_0x01c3:
            r9[r4] = r15
            if (r3 == 0) goto L_0x01c8
            r14 = r3
        L_0x01c8:
            r9[r7] = r14
            java.lang.String r14 = "FromLanguageToLanguage"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r14, r6, r9)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r9 = new org.telegram.ui.Components.TranslateAlert$LoadingTextView
            r9.<init>(r1, r6, r4)
            r0.subtitleView = r9
            r9.showLoadingText(r4)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r9 = r0.subtitleView
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01e2
            r14 = 5
            goto L_0x01e3
        L_0x01e2:
            r14 = 3
        L_0x01e3:
            r9.setGravity(r14)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r9 = r0.subtitleView
            r9.setLines(r7)
            if (r2 == 0) goto L_0x01f4
            if (r3 == 0) goto L_0x01f4
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r2 = r0.subtitleView
            r2.setText(r6)
        L_0x01f4:
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r2 = r0.subtitleView
            java.lang.String r3 = "player_actionBarSubtitle"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r2 = r0.subtitleView
            r3 = 1096810496(0x41600000, float:14.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            r2.setTextSize(r4, r6)
            android.widget.FrameLayout r2 = r0.header
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r6 = r0.subtitleView
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0218
            r9 = 5
            goto L_0x0219
        L_0x0218:
            r9 = 3
        L_0x0219:
            r16 = r9 | 48
            int r9 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padHorz
            float r9 = (float) r9
            float r17 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r17
            r21 = 1102053376(0x41b00000, float:22.0)
            float r17 = r21 - r9
            r9 = 1111228416(0x423CLASSNAME, float:47.0)
            int r11 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padVert
            float r11 = (float) r11
            float r18 = org.telegram.messenger.AndroidUtilities.density
            float r11 = r11 / r18
            float r18 = r9 - r11
            int r9 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padHorz
            float r9 = (float) r9
            float r11 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r11
            float r19 = r21 - r9
            r20 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r0.subtitleLayout = r9
            r2.addView(r6, r9)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.backButton = r2
            r6 = 2131165487(0x7var_f, float:1.7945193E38)
            r2.setImageResource(r6)
            android.widget.ImageView r2 = r0.backButton
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r9, r11)
            r2.setColorFilter(r6)
            android.widget.ImageView r2 = r0.backButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.FIT_CENTER
            r2.setScaleType(r6)
            android.widget.ImageView r2 = r0.backButton
            r6 = 1098907648(0x41800000, float:16.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.setPadding(r9, r4, r11, r4)
            android.widget.ImageView r2 = r0.backButton
            java.lang.String r9 = "dialogButtonSelector"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9)
            r2.setBackground(r9)
            android.widget.ImageView r2 = r0.backButton
            r2.setClickable(r4)
            android.widget.ImageView r2 = r0.backButton
            r2.setAlpha(r5)
            android.widget.ImageView r2 = r0.backButton
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4 r9 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4
            r9.<init>(r0)
            r2.setOnClickListener(r9)
            android.widget.FrameLayout r2 = r0.header
            android.widget.ImageView r9 = r0.backButton
            r11 = 56
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r11, r10)
            r2.addView(r9, r11)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.headerShadowView = r2
            java.lang.String r9 = "dialogShadowLine"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r2.setBackgroundColor(r11)
            android.widget.FrameLayout r2 = r0.headerShadowView
            r2.setAlpha(r5)
            android.widget.FrameLayout r2 = r0.header
            android.widget.FrameLayout r5 = r0.headerShadowView
            r11 = 87
            r13 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r7, r11)
            r2.addView(r5, r11)
            android.widget.FrameLayout r2 = r0.header
            r2.setClipChildren(r4)
            android.widget.FrameLayout r2 = r0.container
            android.widget.FrameLayout r5 = r0.header
            r11 = 70
            r14 = 55
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r11, r14)
            r0.headerLayout = r11
            r2.addView(r5, r11)
            org.telegram.ui.Components.TranslateAlert$3 r2 = new org.telegram.ui.Components.TranslateAlert$3
            r2.<init>(r1)
            r0.scrollView = r2
            r2.setClipChildren(r7)
            org.telegram.ui.Components.TranslateAlert$4 r2 = new org.telegram.ui.Components.TranslateAlert$4
            r2.<init>(r0, r1)
            r0.textsView = r2
            r2.setOrientation(r7)
            android.widget.LinearLayout r2 = r0.textsView
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r11 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padHorz
            int r5 = r5 - r11
            r11 = 1094713344(0x41400000, float:12.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r15 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padVert
            int r14 = r14 - r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r16 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padHorz
            int r15 = r15 - r16
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r17 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padVert
            int r10 = r16 - r17
            r2.setPadding(r5, r14, r15, r10)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.translateMoreView = r2
            java.lang.String r5 = "dialogTextBlue"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            android.widget.TextView r2 = r0.translateMoreView
            r2.setTextSize(r7, r6)
            android.widget.TextView r2 = r0.translateMoreView
            r5 = 2131628220(0x7f0e10bc, float:1.8883727E38)
            java.lang.String r10 = "TranslateMore"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
            r2.setText(r5)
            android.widget.TextView r2 = r0.translateMoreView
            java.util.ArrayList<java.lang.CharSequence> r5 = r0.textBlocks
            int r5 = r5.size()
            if (r5 <= r7) goto L_0x0348
            r5 = 4
            goto L_0x034a
        L_0x0348:
            r5 = 8
        L_0x034a:
            r2.setVisibility(r5)
            android.widget.TextView r2 = r0.translateMoreView
            android.text.TextPaint r2 = r2.getPaint()
            r2.setAntiAlias(r7)
            android.widget.TextView r2 = r0.translateMoreView
            android.text.TextPaint r2 = r2.getPaint()
            r2.setFlags(r7)
            android.widget.TextView r2 = r0.translateMoreView
            java.lang.String r5 = "dialogLinkSelection"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r10 = 1065353216(0x3var_, float:1.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r5, r14, r15)
            r2.setBackgroundDrawable(r5)
            android.widget.TextView r2 = r0.translateMoreView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3
            r5.<init>(r0)
            r2.setOnClickListener(r5)
            android.widget.TextView r2 = r0.translateMoreView
            int r5 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padHorz
            int r14 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padVert
            int r15 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padHorz
            int r12 = org.telegram.ui.Components.TranslateAlert.LoadingTextView.padVert
            r2.setPadding(r5, r14, r15, r12)
            android.widget.LinearLayout r2 = r0.textsView
            android.widget.TextView r5 = r0.translateMoreView
            r22 = -2
            r23 = -2
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x039e
            r24 = 5
            goto L_0x03a0
        L_0x039e:
            r24 = 3
        L_0x03a0:
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28)
            r2.addView(r5, r12)
            android.graphics.Paint r2 = new android.graphics.Paint
            r2.<init>()
            java.lang.String r5 = "chat_inTextSelectionHighlight"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setColor(r5)
            org.telegram.ui.Components.TranslateAlert$6 r2 = new org.telegram.ui.Components.TranslateAlert$6
            r2.<init>(r1)
            r0.allTextsContainer = r2
            r2.setClipChildren(r4)
            android.widget.FrameLayout r2 = r0.allTextsContainer
            r2.setClipToPadding(r4)
            android.widget.FrameLayout r2 = r0.allTextsContainer
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r2.setPadding(r5, r12, r14, r11)
            org.telegram.ui.Components.TranslateAlert$7 r2 = new org.telegram.ui.Components.TranslateAlert$7
            r2.<init>(r1)
            r0.allTextsView = r2
            r2.setTextColor(r4)
            android.widget.TextView r2 = r0.allTextsView
            r2.setTextSize(r7, r6)
            android.widget.TextView r2 = r0.allTextsView
            r2.setTextIsSelectable(r7)
            android.widget.TextView r2 = r0.allTextsView
            android.text.method.LinkMovementMethod r4 = new android.text.method.LinkMovementMethod
            r4.<init>()
            r2.setMovementMethod(r4)
            android.widget.FrameLayout r2 = r0.allTextsContainer
            android.widget.TextView r4 = r0.allTextsView
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r5)
            r2.addView(r4, r6)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.textsContainerView = r2
            android.widget.FrameLayout r4 = r0.allTextsContainer
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r5)
            r2.addView(r4, r6)
            android.widget.FrameLayout r2 = r0.textsContainerView
            android.widget.LinearLayout r4 = r0.textsView
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r5)
            r2.addView(r4, r5)
            androidx.core.widget.NestedScrollView r2 = r0.scrollView
            android.widget.FrameLayout r4 = r0.textsContainerView
            r5 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r5, (float) r10)
            r2.addView((android.view.View) r4, (android.view.ViewGroup.LayoutParams) r5)
            android.widget.FrameLayout r2 = r0.container
            androidx.core.widget.NestedScrollView r4 = r0.scrollView
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 119(0x77, float:1.67E-43)
            r17 = 0
            r18 = 1116471296(0x428CLASSNAME, float:70.0)
            r19 = 0
            r20 = 1117913088(0x42a20000, float:81.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r0.scrollViewLayout = r5
            r2.addView(r4, r5)
            android.widget.TextView r2 = r0.translateMoreView
            r2.bringToFront()
            r29.fetchNext()
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.buttonShadowView = r2
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r2.setBackgroundColor(r4)
            android.widget.FrameLayout r2 = r0.container
            android.widget.FrameLayout r4 = r0.buttonShadowView
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 87
            r18 = 0
            r20 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r2.addView(r4, r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.buttonTextView = r2
            r2.setLines(r7)
            android.widget.TextView r2 = r0.buttonTextView
            r2.setSingleLine(r7)
            android.widget.TextView r2 = r0.buttonTextView
            r2.setGravity(r7)
            android.widget.TextView r2 = r0.buttonTextView
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r4)
            android.widget.TextView r2 = r0.buttonTextView
            r4 = 17
            r2.setGravity(r4)
            android.widget.TextView r2 = r0.buttonTextView
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
            android.widget.TextView r2 = r0.buttonTextView
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r2.setTypeface(r4)
            android.widget.TextView r2 = r0.buttonTextView
            r2.setTextSize(r7, r3)
            android.widget.TextView r2 = r0.buttonTextView
            r3 = 2131625012(0x7f0e0434, float:1.887722E38)
            java.lang.String r4 = "CloseTranslation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.buttonView = r2
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r3 = "featuredStickers_addButton"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "featuredStickers_addButtonPressed"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r1, r3, r4)
            r2.setBackground(r1)
            android.widget.FrameLayout r1 = r0.buttonView
            android.widget.TextView r2 = r0.buttonTextView
            r1.addView(r2)
            android.widget.FrameLayout r1 = r0.buttonView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda5 r2 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda5
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            android.widget.FrameLayout r1 = r0.container
            android.widget.FrameLayout r2 = r0.buttonView
            r3 = -1
            r4 = 1111490560(0x42400000, float:48.0)
            r5 = 80
            r6 = 1098907648(0x41800000, float:16.0)
            r7 = 1098907648(0x41800000, float:16.0)
            r8 = 1098907648(0x41800000, float:16.0)
            r9 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
            r1.addView(r2, r3)
            android.widget.FrameLayout r1 = r0.contentView
            android.widget.FrameLayout r2 = r0.container
            r3 = -2
            r4 = 81
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r3, r4)
            r1.addView(r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, java.lang.String, java.lang.String, java.lang.CharSequence):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        TextView textView = this.titleView;
        textView.setPivotX(LocaleController.isRTL ? (float) textView.getWidth() : 0.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        this.scrollView.getScrollY();
        this.scrollView.computeVerticalScrollRange();
        this.scrollView.computeVerticalScrollExtent();
        openAnimationTo(1.0f, true);
        fetchNext();
        if (this.containerOpenAnimationT >= 1.0f && canExpand()) {
            ValueAnimator valueAnimator = this.scrollerToBottom;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.scrollerToBottom = null;
            }
            this.allowScroll = false;
            this.scrollView.stopNestedScroll();
            this.scrollerToBottom = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.scrollerToBottom.addUpdateListener(new TranslateAlert$$ExternalSyntheticLambda2(this, this.scrollView.getScrollY()));
            this.scrollerToBottom.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    boolean unused = TranslateAlert.this.allowScroll = true;
                }

                public void onAnimationCancel(Animator animator) {
                    boolean unused = TranslateAlert.this.allowScroll = true;
                }
            });
            this.scrollerToBottom.setDuration(220);
            this.scrollerToBottom.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(int i, ValueAnimator valueAnimator) {
        this.scrollView.setScrollY((int) (((float) i) + (((float) AndroidUtilities.dp(150.0f)) * ((Float) valueAnimator.getAnimatedValue()).floatValue())));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
        dismiss();
    }

    private void setScrollY(float f) {
        openAnimation(f);
        float max = Math.max(Math.min(f + 1.0f, 1.0f), 0.0f);
        this.openingT = max;
        this.backDrawable.setAlpha((int) (max * 51.0f));
        this.container.invalidate();
    }

    private void scrollYTo(float f) {
        openAnimationTo(f, false);
        openTo(f + 1.0f, false);
    }

    private float getScrollY() {
        return Math.max(Math.min(this.containerOpenAnimationT - (1.0f - this.openingT), 1.0f), 0.0f);
    }

    private boolean hasSelection() {
        return this.allTextsView.hasSelection();
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        float f;
        ClickableSpan clickableSpan;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        this.container.invalidate();
        this.container.getGlobalVisibleRect(this.containerRect);
        int i = (int) x;
        int i2 = (int) y;
        if (!this.containerRect.contains(i, i2)) {
            if (motionEvent.getAction() == 0) {
                this.pressedOutside = true;
                return true;
            } else if (motionEvent.getAction() == 1 && this.pressedOutside) {
                this.pressedOutside = false;
                dismiss();
                return true;
            }
        }
        this.allTextsContainer.getGlobalVisibleRect(this.textRect);
        if (this.textRect.contains(i, i2) && !this.scrolling) {
            Layout layout = this.allTextsView.getLayout();
            int lineForVertical = layout.getLineForVertical((int) ((((y - ((float) this.allTextsView.getTop())) - ((float) this.container.getTop())) - ((float) this.scrollView.getTop())) + ((float) this.scrollView.getScrollY())));
            float left = (float) ((int) ((x - ((float) this.allTextsView.getLeft())) - ((float) this.container.getLeft())));
            int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, left);
            float lineLeft = layout.getLineLeft(lineForVertical);
            if (this.allTexts != null && lineLeft <= left && lineLeft + layout.getLineWidth(lineForVertical) >= left) {
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) this.allTexts.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                if (clickableSpanArr != null && clickableSpanArr.length >= 1) {
                    if (motionEvent.getAction() == 1 && (clickableSpan = this.pressedLink) == clickableSpanArr[0]) {
                        clickableSpan.onClick(this.allTextsView);
                        this.pressedLink = null;
                        this.allTextsView.setTextIsSelectable(true);
                    } else if (motionEvent.getAction() == 0) {
                        this.pressedLink = clickableSpanArr[0];
                    }
                    this.allTextsView.invalidate();
                    return true;
                } else if (this.pressedLink != null) {
                    this.allTextsView.invalidate();
                    this.pressedLink = null;
                }
            } else if (this.pressedLink != null) {
                this.allTextsView.invalidate();
                this.pressedLink = null;
            }
        } else if (this.pressedLink != null) {
            this.allTextsView.invalidate();
            this.pressedLink = null;
        }
        this.scrollView.getGlobalVisibleRect(this.scrollRect);
        this.backButton.getGlobalVisibleRect(this.backRect);
        this.buttonView.getGlobalVisibleRect(this.buttonRect);
        this.translateMoreView.getGlobalVisibleRect(this.translateMoreRect);
        boolean contains = this.translateMoreRect.contains(i, i2);
        this.fromTranslateMoreView = contains;
        if (this.pressedLink == null && !contains && !hasSelection()) {
            if (!this.backRect.contains(i, i2) && !this.buttonRect.contains(i, i2) && motionEvent.getAction() == 0) {
                this.fromScrollRect = this.scrollRect.contains(i, i2) && (this.containerOpenAnimationT > 0.0f || !canExpand());
                this.maybeScrolling = true;
                this.scrolling = false;
                this.fromY = y;
                this.fromScrollY = getScrollY();
                this.fromScrollViewY = (float) this.scrollView.getScrollY();
                super.dispatchTouchEvent(motionEvent);
                return true;
            } else if (this.maybeScrolling && (motionEvent.getAction() == 2 || motionEvent.getAction() == 1)) {
                float f2 = this.fromY - y;
                if (this.fromScrollRect) {
                    f2 = -Math.max(0.0f, (-(this.fromScrollViewY + ((float) AndroidUtilities.dp(48.0f)))) - f2);
                    if (f2 < 0.0f) {
                        this.scrolling = true;
                        this.allTextsView.setTextIsSelectable(false);
                    }
                } else if (Math.abs(f2) > ((float) AndroidUtilities.dp(4.0f)) && !this.fromScrollRect) {
                    this.scrolling = true;
                    this.allTextsView.setTextIsSelectable(false);
                    this.scrollView.stopNestedScroll();
                    this.allowScroll = false;
                }
                float f3 = (float) AndroidUtilities.displayMetrics.heightPixels;
                float min = Math.min(f3, Math.min((float) AndroidUtilities.dp(550.0f), 0.5f * f3));
                float f4 = -1.0f;
                float f5 = f3 - min;
                float min2 = ((1.0f - (-Math.min(Math.max(this.fromScrollY, -1.0f), 0.0f))) * min) + (Math.min(1.0f, Math.max(this.fromScrollY, 0.0f)) * f5) + f2;
                float f6 = min2 > min ? (min2 - min) / f5 : -(1.0f - (min2 / min));
                if (!canExpand()) {
                    f6 = Math.min(f6, 0.0f);
                }
                updateCanExpand();
                if (this.scrolling) {
                    setScrollY(f6);
                    if (motionEvent.getAction() == 1) {
                        this.scrolling = false;
                        this.allTextsView.setTextIsSelectable(true);
                        this.maybeScrolling = false;
                        this.allowScroll = true;
                        if (Math.abs(f2) > ((float) AndroidUtilities.dp(16.0f))) {
                            float round = (float) Math.round(this.fromScrollY);
                            float f7 = this.fromScrollY;
                            if (f6 > f7) {
                                f4 = 1.0f;
                            }
                            f = round + (f4 * ((float) Math.ceil((double) Math.abs(f7 - f6))));
                        } else {
                            f = (float) Math.round(this.fromScrollY);
                        }
                        scrollYTo(f);
                    }
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private LoadingTextView addBlock(CharSequence charSequence, boolean z) {
        AnonymousClass8 r0 = new LoadingTextView(getContext(), charSequence, z) {
            /* access modifiers changed from: protected */
            public void onLoadEnd() {
                TranslateAlert.this.scrollView.postDelayed(new TranslateAlert$8$$ExternalSyntheticLambda0(this), TranslateAlert.this.textBlocks.size() > 1 ? 700 : 0);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLoadEnd$0() {
                TranslateAlert.this.allTextsView.setText(TranslateAlert.this.allTexts);
            }
        };
        r0.setLines(0);
        r0.setMaxLines(0);
        r0.setSingleLine(false);
        r0.setEllipsizeNull();
        r0.setTextColor(Theme.getColor("dialogTextBlack"));
        r0.setTextSize(1, 16.0f);
        r0.setTextIsSelectable(false);
        r0.setTranslationY(((float) (this.textsView.getChildCount() - 1)) * ((((float) LoadingTextView.padVert) * -4.0f) + ((float) AndroidUtilities.dp(0.48f))));
        LinearLayout linearLayout = this.textsView;
        linearLayout.addView(r0, linearLayout.getChildCount() - 1, LayoutHelper.createLinear(-1, -1, 0, 0, 0, 0, 0));
        return r0;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.contentView.setPadding(0, 0, 0, 0);
        setContentView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        int i = attributes.flags & -3;
        attributes.flags = i;
        int i2 = i | 131072;
        attributes.flags = i2;
        if (Build.VERSION.SDK_INT >= 21) {
            attributes.flags = i2 | -NUM;
        }
        attributes.flags |= 256;
        attributes.height = -1;
        window.setAttributes(attributes);
        this.container.forceLayout();
    }

    public void show() {
        super.show();
        openAnimation(0.0f);
        openTo(1.0f, true, true);
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            openTo(0.0f, true);
        }
    }

    private void openTo(float f, boolean z) {
        openTo(f, z, false);
    }

    private void openTo(float f, boolean z, final boolean z2) {
        final float min = Math.min(Math.max(f, 0.0f), 1.0f);
        if (!this.openingAnimatorPriority || z) {
            this.openingAnimatorPriority = z;
            ValueAnimator valueAnimator = this.openingAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.openingAnimator = ValueAnimator.ofFloat(new float[]{this.openingT, min});
            this.backDrawable.setAlpha((int) (this.openingT * 51.0f));
            this.openingAnimator.addUpdateListener(new TranslateAlert$$ExternalSyntheticLambda1(this));
            this.openingAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationCancel(Animator animator) {
                    if (min <= 0.0f) {
                        TranslateAlert.this.dismissInternal();
                    } else if (z2) {
                        TranslateAlert.this.allTextsView.setTextIsSelectable(true);
                        TranslateAlert.this.allTextsView.invalidate();
                        TranslateAlert.this.scrollView.stopNestedScroll();
                        TranslateAlert.this.openAnimation(min - 1.0f);
                    }
                    boolean unused = TranslateAlert.this.openingAnimatorPriority = false;
                }

                public void onAnimationEnd(Animator animator) {
                    if (min <= 0.0f) {
                        TranslateAlert.this.dismissInternal();
                    } else if (z2) {
                        TranslateAlert.this.allTextsView.setTextIsSelectable(true);
                        TranslateAlert.this.allTextsView.invalidate();
                        TranslateAlert.this.scrollView.stopNestedScroll();
                        TranslateAlert.this.openAnimation(min - 1.0f);
                    }
                    boolean unused = TranslateAlert.this.openingAnimatorPriority = false;
                }
            });
            this.openingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.openingAnimator.setDuration((long) (Math.abs(this.openingT - min) * ((float) (z2 ? 380 : 200))));
            this.openingAnimator.setStartDelay(z2 ? 60 : 0);
            this.openingAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openTo$6(ValueAnimator valueAnimator) {
        this.openingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.invalidate();
        this.backDrawable.setAlpha((int) (this.openingT * 51.0f));
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public String languageName(String str) {
        String str2;
        LocaleController.LocaleInfo languageByPlural = LocaleController.getInstance().getLanguageByPlural(str);
        if (languageByPlural == null || (str2 = languageByPlural.nameEnglish) == null) {
            return null;
        }
        return str2;
    }

    public void updateSourceLanguage() {
        String languageName = languageName(this.fromLanguage);
        String languageName2 = languageName(this.toLanguage);
        if (languageName != null && languageName2 != null) {
            this.subtitleView.setAlpha(1.0f);
            this.subtitleView.setText(LocaleController.formatString("FromLanguageToLanguage", NUM, languageName, languageName2));
        } else if (this.loaded) {
            this.subtitleView.animate().alpha(0.0f).setDuration(150).start();
        }
    }

    private ArrayList<CharSequence> cutInBlocks(CharSequence charSequence, int i) {
        ArrayList<CharSequence> arrayList = new ArrayList<>();
        if (charSequence == null) {
            return arrayList;
        }
        while (charSequence.length() > i) {
            String charSequence2 = charSequence.subSequence(0, i).toString();
            int lastIndexOf = charSequence2.lastIndexOf("\n\n");
            if (lastIndexOf == -1) {
                lastIndexOf = charSequence2.lastIndexOf("\n");
            }
            if (lastIndexOf == -1) {
                lastIndexOf = charSequence2.lastIndexOf(". ");
            }
            int i2 = lastIndexOf + 1;
            arrayList.add(charSequence.subSequence(0, i2));
            charSequence = charSequence.subSequence(i2, charSequence.length());
        }
        if (charSequence.length() > 0) {
            arrayList.add(charSequence);
        }
        return arrayList;
    }

    public void showTranslateMoreView(boolean z) {
        this.translateMoreView.setClickable(z);
        this.translateMoreView.setVisibility(this.textBlocks.size() > 1 ? 0 : 8);
        float f = 1.0f;
        ViewPropertyAnimator interpolator = this.translateMoreView.animate().alpha(z ? 1.0f : 0.0f).withEndAction(new TranslateAlert$$ExternalSyntheticLambda7(this, z)).setInterpolator(CubicBezierInterpolator.EASE_OUT);
        float alpha = this.translateMoreView.getAlpha();
        if (!z) {
            f = 0.0f;
        }
        interpolator.setDuration((long) (Math.abs(alpha - f) * 85.0f)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showTranslateMoreView$7(boolean z) {
        if (!z) {
            this.translateMoreView.setVisibility(this.textBlocks.size() > 1 ? 4 : 8);
        }
    }

    private void fetchNext() {
        if (!this.loading) {
            boolean z = true;
            this.loading = true;
            showTranslateMoreView(false);
            if (this.blockIndex < this.textBlocks.size()) {
                CharSequence charSequence = this.textBlocks.get(this.blockIndex);
                if (this.blockIndex == 0) {
                    z = false;
                }
                fetchTranslation(charSequence, new TranslateAlert$$ExternalSyntheticLambda9(this, addBlock(charSequence, z)), new TranslateAlert$$ExternalSyntheticLambda8(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNext$8(LoadingTextView loadingTextView, String str, String str2) {
        boolean z = true;
        this.loaded = true;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        AndroidUtilities.addLinks(spannableStringBuilder, 1);
        MessageObject.addUrlsByPattern(false, spannableStringBuilder, false, 0, 0, true);
        URLSpan[] uRLSpanArr = (URLSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);
        for (final URLSpan uRLSpan : uRLSpanArr) {
            int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
            int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
            spannableStringBuilder.removeSpan(uRLSpan);
            spannableStringBuilder.setSpan(new ClickableSpan() {
                public void onClick(View view) {
                    AlertsCreator.showOpenUrlAlert(TranslateAlert.this.fragment, uRLSpan.getURL(), false, false);
                }

                public void updateDrawState(TextPaint textPaint) {
                    int min = Math.min(textPaint.getAlpha(), (textPaint.getColor() >> 24) & 255);
                    textPaint.setUnderlineText(true);
                    textPaint.setColor(Theme.getColor("dialogTextLink"));
                    textPaint.setAlpha(min);
                }
            }, spanStart, spanEnd, 33);
        }
        loadingTextView.setText(spannableStringBuilder);
        this.allTexts = new SpannableStringBuilder(this.allTextsView.getText()).append(this.blockIndex == 0 ? "" : "\n").append(spannableStringBuilder);
        this.fromLanguage = str2;
        updateSourceLanguage();
        int i = this.blockIndex + 1;
        this.blockIndex = i;
        if (i >= this.textBlocks.size()) {
            z = false;
        }
        showTranslateMoreView(z);
        this.loading = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNext$9(boolean z) {
        if (z) {
            Toast.makeText(getContext(), LocaleController.getString("TranslationFailedAlert1", NUM), 0).show();
        } else {
            Toast.makeText(getContext(), LocaleController.getString("TranslationFailedAlert2", NUM), 0).show();
        }
        if (this.blockIndex == 0) {
            dismiss();
        }
    }

    private void fetchTranslation(final CharSequence charSequence, final OnTranslationSuccess onTranslationSuccess, final OnTranslationFail onTranslationFail) {
        new Thread() {
            /* JADX WARNING: Missing exception handler attribute for start block: B:42:0x0183 */
            /* JADX WARNING: Unknown top exception splitter block from list: {B:42:0x0183=Splitter:B:42:0x0183, B:21:0x010d=Splitter:B:21:0x010d} */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r12 = this;
                    long r0 = android.os.SystemClock.elapsedRealtime()
                    r2 = 1
                    r3 = 0
                    r4 = 0
                    java.lang.String r5 = "https://translate.goo"
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
                    r6.<init>()     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = "gleapis.com/transl"
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0186 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
                    r6.<init>()     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = "ate_a"
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0186 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
                    r6.<init>()     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = "/singl"
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0186 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
                    r6.<init>()     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = "e?client=gtx&sl="
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    org.telegram.ui.Components.TranslateAlert r5 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r5.fromLanguage     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = android.net.Uri.encode(r5)     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = "&tl="
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    org.telegram.ui.Components.TranslateAlert r5 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r5.toLanguage     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = android.net.Uri.encode(r5)     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q="
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0186 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
                    r6.<init>()     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.CharSequence r5 = r2     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = android.net.Uri.encode(r5)     // Catch:{ Exception -> 0x0186 }
                    r6.append(r5)     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0186 }
                    java.net.URI r6 = new java.net.URI     // Catch:{ Exception -> 0x0186 }
                    r6.<init>(r5)     // Catch:{ Exception -> 0x0186 }
                    java.net.URL r5 = r6.toURL()     // Catch:{ Exception -> 0x0186 }
                    java.net.URLConnection r5 = r5.openConnection()     // Catch:{ Exception -> 0x0186 }
                    java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5     // Catch:{ Exception -> 0x0186 }
                    java.lang.String r6 = "GET"
                    r5.setRequestMethod(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = "User-Agent"
                    org.telegram.ui.Components.TranslateAlert r7 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0184 }
                    java.lang.String[] r7 = r7.userAgents     // Catch:{ Exception -> 0x0184 }
                    double r8 = java.lang.Math.random()     // Catch:{ Exception -> 0x0184 }
                    org.telegram.ui.Components.TranslateAlert r10 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0184 }
                    java.lang.String[] r10 = r10.userAgents     // Catch:{ Exception -> 0x0184 }
                    int r10 = r10.length     // Catch:{ Exception -> 0x0184 }
                    int r10 = r10 - r2
                    double r10 = (double) r10
                    java.lang.Double.isNaN(r10)
                    double r8 = r8 * r10
                    long r8 = java.lang.Math.round(r8)     // Catch:{ Exception -> 0x0184 }
                    int r9 = (int) r8     // Catch:{ Exception -> 0x0184 }
                    r7 = r7[r9]     // Catch:{ Exception -> 0x0184 }
                    r5.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = "Content-Type"
                    java.lang.String r7 = "application/json"
                    r5.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x0184 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r6.<init>()     // Catch:{ Exception -> 0x0184 }
                    java.io.BufferedReader r7 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0184 }
                    java.io.InputStreamReader r8 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0184 }
                    java.io.InputStream r9 = r5.getInputStream()     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r10 = "UTF-8"
                    java.nio.charset.Charset r10 = java.nio.charset.Charset.forName(r10)     // Catch:{ Exception -> 0x0184 }
                    r8.<init>(r9, r10)     // Catch:{ Exception -> 0x0184 }
                    r7.<init>(r8)     // Catch:{ Exception -> 0x0184 }
                L_0x00e2:
                    int r8 = r7.read()     // Catch:{ all -> 0x017f }
                    r9 = -1
                    if (r8 == r9) goto L_0x00ee
                    char r8 = (char) r8     // Catch:{ all -> 0x017f }
                    r6.append(r8)     // Catch:{ all -> 0x017f }
                    goto L_0x00e2
                L_0x00ee:
                    r7.close()     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0184 }
                    org.json.JSONTokener r7 = new org.json.JSONTokener     // Catch:{ Exception -> 0x0184 }
                    r7.<init>(r6)     // Catch:{ Exception -> 0x0184 }
                    org.json.JSONArray r6 = new org.json.JSONArray     // Catch:{ Exception -> 0x0184 }
                    r6.<init>(r7)     // Catch:{ Exception -> 0x0184 }
                    org.json.JSONArray r7 = r6.getJSONArray(r4)     // Catch:{ Exception -> 0x0184 }
                    r8 = 2
                    java.lang.String r6 = r6.getString(r8)     // Catch:{ Exception -> 0x0109 }
                    goto L_0x010a
                L_0x0109:
                    r6 = r3
                L_0x010a:
                    java.lang.String r8 = ""
                    r9 = 0
                L_0x010d:
                    int r10 = r7.length()     // Catch:{ Exception -> 0x0184 }
                    if (r9 >= r10) goto L_0x0137
                    org.json.JSONArray r10 = r7.getJSONArray(r9)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r10 = r10.getString(r4)     // Catch:{ Exception -> 0x0184 }
                    if (r10 == 0) goto L_0x0134
                    java.lang.String r11 = "null"
                    boolean r11 = r10.equals(r11)     // Catch:{ Exception -> 0x0184 }
                    if (r11 != 0) goto L_0x0134
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r11.<init>()     // Catch:{ Exception -> 0x0184 }
                    r11.append(r8)     // Catch:{ Exception -> 0x0184 }
                    r11.append(r10)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0184 }
                L_0x0134:
                    int r9 = r9 + 1
                    goto L_0x010d
                L_0x0137:
                    java.lang.CharSequence r7 = r2     // Catch:{ Exception -> 0x0184 }
                    int r7 = r7.length()     // Catch:{ Exception -> 0x0184 }
                    if (r7 <= 0) goto L_0x015a
                    java.lang.CharSequence r7 = r2     // Catch:{ Exception -> 0x0184 }
                    char r7 = r7.charAt(r4)     // Catch:{ Exception -> 0x0184 }
                    r9 = 10
                    if (r7 != r9) goto L_0x015a
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r7.<init>()     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r9 = "\n"
                    r7.append(r9)     // Catch:{ Exception -> 0x0184 }
                    r7.append(r8)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r8 = r7.toString()     // Catch:{ Exception -> 0x0184 }
                L_0x015a:
                    long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0184 }
                    long r9 = r9 - r0
                    org.telegram.ui.Components.TranslateAlert r0 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0184 }
                    long r0 = r0.minFetchingDuration     // Catch:{ Exception -> 0x0184 }
                    int r7 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                    if (r7 >= 0) goto L_0x0173
                    org.telegram.ui.Components.TranslateAlert r0 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0184 }
                    long r0 = r0.minFetchingDuration     // Catch:{ Exception -> 0x0184 }
                    long r0 = r0 - r9
                    java.lang.Thread.sleep(r0)     // Catch:{ Exception -> 0x0184 }
                L_0x0173:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationSuccess r0 = r3     // Catch:{ Exception -> 0x0184 }
                    org.telegram.ui.Components.TranslateAlert$12$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.TranslateAlert$12$$ExternalSyntheticLambda2     // Catch:{ Exception -> 0x0184 }
                    r1.<init>(r0, r8, r6)     // Catch:{ Exception -> 0x0184 }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x0184 }
                    goto L_0x01ed
                L_0x017f:
                    r0 = move-exception
                    r7.close()     // Catch:{ all -> 0x0183 }
                L_0x0183:
                    throw r0     // Catch:{ Exception -> 0x0184 }
                L_0x0184:
                    r0 = move-exception
                    goto L_0x0188
                L_0x0186:
                    r0 = move-exception
                    r5 = r3
                L_0x0188:
                    java.lang.String r1 = "translate"
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01b9 }
                    r6.<init>()     // Catch:{ IOException -> 0x01b9 }
                    java.lang.String r7 = "failed to translate a text "
                    r6.append(r7)     // Catch:{ IOException -> 0x01b9 }
                    if (r5 == 0) goto L_0x019f
                    int r7 = r5.getResponseCode()     // Catch:{ IOException -> 0x01b9 }
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ IOException -> 0x01b9 }
                    goto L_0x01a0
                L_0x019f:
                    r7 = r3
                L_0x01a0:
                    r6.append(r7)     // Catch:{ IOException -> 0x01b9 }
                    java.lang.String r7 = " "
                    r6.append(r7)     // Catch:{ IOException -> 0x01b9 }
                    if (r5 == 0) goto L_0x01ae
                    java.lang.String r3 = r5.getResponseMessage()     // Catch:{ IOException -> 0x01b9 }
                L_0x01ae:
                    r6.append(r3)     // Catch:{ IOException -> 0x01b9 }
                    java.lang.String r3 = r6.toString()     // Catch:{ IOException -> 0x01b9 }
                    android.util.Log.e(r1, r3)     // Catch:{ IOException -> 0x01b9 }
                    goto L_0x01bd
                L_0x01b9:
                    r1 = move-exception
                    r1.printStackTrace()
                L_0x01bd:
                    r0.printStackTrace()
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r4
                    if (r0 == 0) goto L_0x01ed
                    org.telegram.ui.Components.TranslateAlert r0 = org.telegram.ui.Components.TranslateAlert.this
                    boolean r0 = r0.dismissed
                    if (r0 != 0) goto L_0x01ed
                    if (r5 == 0) goto L_0x01d7
                    int r0 = r5.getResponseCode()     // Catch:{ Exception -> 0x01e3 }
                    r1 = 429(0x1ad, float:6.01E-43)
                    if (r0 != r1) goto L_0x01d7
                    goto L_0x01d8
                L_0x01d7:
                    r2 = 0
                L_0x01d8:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r4     // Catch:{ Exception -> 0x01e3 }
                    org.telegram.ui.Components.TranslateAlert$12$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.TranslateAlert$12$$ExternalSyntheticLambda1     // Catch:{ Exception -> 0x01e3 }
                    r1.<init>(r0, r2)     // Catch:{ Exception -> 0x01e3 }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x01e3 }
                    goto L_0x01ed
                L_0x01e3:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r4
                    org.telegram.ui.Components.TranslateAlert$12$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.TranslateAlert$12$$ExternalSyntheticLambda0
                    r1.<init>(r0)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
                L_0x01ed:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.AnonymousClass12.run():void");
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$run$0(OnTranslationSuccess onTranslationSuccess, String str, String str2) {
                if (onTranslationSuccess != null) {
                    onTranslationSuccess.run(str, str2);
                }
            }
        }.start();
    }

    public static void showAlert(Context context, BaseFragment baseFragment, String str, String str2, CharSequence charSequence) {
        TranslateAlert translateAlert = new TranslateAlert(baseFragment, context, str, str2, charSequence);
        if (baseFragment == null) {
            translateAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(translateAlert);
        }
    }

    private static class LoadingTextView extends FrameLayout {
        public static int padHorz = AndroidUtilities.dp(6.0f);
        public static int padVert = AndroidUtilities.dp(1.5f);
        private ValueAnimator animator = null;
        private Path fetchPath = new Path() {
            private boolean got = false;

            public void reset() {
                super.reset();
                this.got = false;
            }

            public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
                if (!this.got) {
                    LoadingTextView.this.fetchedPathRect.set(f - ((float) LoadingTextView.padHorz), f2 - ((float) LoadingTextView.padVert), f3 + ((float) LoadingTextView.padHorz), f4 + ((float) LoadingTextView.padVert));
                    this.got = true;
                }
            }
        };
        /* access modifiers changed from: private */
        public RectF fetchedPathRect = new RectF();
        private float gradientWidth = ((float) AndroidUtilities.dp(350.0f));
        private Path inPath = new Path();
        public boolean loaded = false;
        private ValueAnimator loadingAnimator = null;
        private Paint loadingPaint = new Paint();
        private Path loadingPath = new Path();
        private CharSequence loadingString;
        private float loadingT = 0.0f;
        private TextView loadingTextView;
        private RectF rect = new RectF();
        private boolean scaleFromZero = false;
        private long scaleFromZeroStart = 0;
        private Path shadePath = new Path();
        private boolean showLoadingTextValue = true;
        private long start = SystemClock.elapsedRealtime();
        private Path tempPath = new Path();
        public TextView textView;

        private void updateTextLayout() {
        }

        /* access modifiers changed from: protected */
        public void onLoadEnd() {
        }

        public void resize() {
            this.textView.forceLayout();
            this.loadingTextView.forceLayout();
            updateLoadingLayout();
            updateTextLayout();
            updateHeight();
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            resize();
        }

        public LoadingTextView(Context context, CharSequence charSequence, boolean z) {
            super(context);
            new Paint();
            this.scaleFromZero = z;
            this.scaleFromZeroStart = SystemClock.elapsedRealtime();
            int i = padHorz;
            int i2 = padVert;
            setPadding(i, i2, i, i2);
            this.loadingT = 0.0f;
            AnonymousClass2 r15 = new TextView(this, context) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE ? 999999 : View.MeasureSpec.getSize(i2), View.MeasureSpec.getMode(i2)));
                }
            };
            this.loadingTextView = r15;
            CharSequence replaceEmoji = Emoji.replaceEmoji(charSequence, r15.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false);
            TextView textView2 = this.loadingTextView;
            this.loadingString = replaceEmoji;
            textView2.setText(replaceEmoji);
            this.loadingTextView.setVisibility(4);
            this.loadingTextView.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            addView(this.loadingTextView, LayoutHelper.createFrame(-1, -2, 48));
            AnonymousClass3 r14 = new TextView(this, context) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE ? 999999 : View.MeasureSpec.getSize(i2), View.MeasureSpec.getMode(i2)));
                }
            };
            this.textView = r14;
            addView(r14, LayoutHelper.createFrame(-1, -2.0f));
            int color = Theme.getColor("dialogBackground");
            int color2 = Theme.getColor("dialogBackgroundGray");
            this.loadingPaint.setShader(new LinearGradient(0.0f, 0.0f, this.gradientWidth, 0.0f, new int[]{color, color2, color}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT));
            setWillNotDraw(false);
            setClipChildren(false);
            updateLoadingLayout();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateHeight();
        }

        private void updateHeight() {
            int measuredHeight = this.loadingTextView.getMeasuredHeight();
            float f = 1.0f;
            if (this.scaleFromZero) {
                f = Math.max(Math.min(((float) (SystemClock.elapsedRealtime() - this.scaleFromZeroStart)) / 220.0f, 1.0f), 0.0f);
            }
            int measuredHeight2 = (int) ((((float) ((padVert * 2) + measuredHeight)) + (((float) (this.textView.getMeasuredHeight() - measuredHeight)) * this.loadingT)) * f);
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(-1, measuredHeight2);
            }
            layoutParams.height = measuredHeight2;
            setLayoutParams(layoutParams);
        }

        private void updateLoadingLayout() {
            if (((float) this.loadingTextView.getMeasuredWidth()) > 0.0f) {
                Layout layout = this.loadingTextView.getLayout();
                for (int i = 0; i < layout.getLineCount(); i++) {
                    int lineStart = layout.getLineStart(i);
                    int lineEnd = layout.getLineEnd(i);
                    if (lineStart + 1 != lineEnd) {
                        layout.getSelectionPath(lineStart, lineEnd, this.fetchPath);
                        this.loadingPath.addRoundRect(this.fetchedPathRect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                    }
                }
                updateHeight();
            }
            if (!this.loaded && this.loadingAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.loadingAnimator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView$$ExternalSyntheticLambda0(this));
                this.loadingAnimator.setDuration(Long.MAX_VALUE);
                this.loadingAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateLoadingLayout$0(ValueAnimator valueAnimator) {
            this.loadingT = 0.0f;
            if (this.scaleFromZero && SystemClock.elapsedRealtime() < this.scaleFromZeroStart + 220 + 25) {
                updateHeight();
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            resize();
        }

        public void setEllipsizeNull() {
            this.loadingTextView.setEllipsize((TextUtils.TruncateAt) null);
            this.textView.setEllipsize((TextUtils.TruncateAt) null);
        }

        public void setSingleLine(boolean z) {
            this.loadingTextView.setSingleLine(z);
            this.textView.setSingleLine(z);
        }

        public void setLines(int i) {
            this.loadingTextView.setLines(i);
            this.textView.setLines(i);
        }

        public void setGravity(int i) {
            this.loadingTextView.setGravity(i);
            this.textView.setGravity(i);
        }

        public void setMaxLines(int i) {
            this.loadingTextView.setMaxLines(i);
            this.textView.setMaxLines(i);
        }

        public void setTextIsSelectable(boolean z) {
            this.textView.setTextIsSelectable(z);
        }

        public void showLoadingText(boolean z) {
            this.showLoadingTextValue = z;
        }

        public void setTextColor(int i) {
            this.loadingTextView.setTextColor(i);
            this.textView.setTextColor(i);
        }

        public void setTextSize(int i, float f) {
            this.loadingTextView.setTextSize(i, f);
            this.textView.setTextSize(i, f);
            TextView textView2 = this.loadingTextView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(this.loadingString, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false);
            this.loadingString = replaceEmoji;
            textView2.setText(replaceEmoji);
            this.loadingTextView.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            TextView textView3 = this.textView;
            textView3.setText(Emoji.replaceEmoji(textView3.getText(), this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            updateLoadingLayout();
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(Emoji.replaceEmoji(charSequence, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            updateTextLayout();
            if (!this.loaded) {
                this.loaded = true;
                this.loadingT = 0.0f;
                ValueAnimator valueAnimator = this.loadingAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.loadingAnimator = null;
                }
                ValueAnimator valueAnimator2 = this.animator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView$$ExternalSyntheticLambda1(this));
                this.animator.addListener(new Animator.AnimatorListener() {
                    public void onAnimationRepeat(Animator animator) {
                    }

                    public void onAnimationStart(Animator animator) {
                    }

                    public void onAnimationEnd(Animator animator) {
                        LoadingTextView.this.onLoadEnd();
                    }

                    public void onAnimationCancel(Animator animator) {
                        LoadingTextView.this.onLoadEnd();
                    }
                });
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_IN);
                this.animator.setDuration(220);
                this.animator.start();
                return;
            }
            updateHeight();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$1(ValueAnimator valueAnimator) {
            this.loadingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateHeight();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float width = (float) getWidth();
            float height = (float) getHeight();
            float max = LocaleController.isRTL ? Math.max(width / 2.0f, width - 8.0f) : Math.min(width / 2.0f, 8.0f);
            float min = Math.min(height / 2.0f, 8.0f);
            float f = max * max;
            float f2 = min * min;
            float f3 = width - max;
            float f4 = f3 * f3;
            float f5 = height - min;
            float f6 = f5 * f5;
            float sqrt = this.loadingT * ((float) Math.sqrt((double) Math.max(Math.max(f + f2, f2 + f4), Math.max(f + f6, f4 + f6))));
            this.inPath.reset();
            this.inPath.addCircle(max, min, sqrt, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
            this.loadingPaint.setAlpha((int) ((1.0f - this.loadingT) * 255.0f));
            float f7 = this.gradientWidth;
            float f8 = this.gradientWidth;
            float elapsedRealtime = f7 - (((((float) (SystemClock.elapsedRealtime() - this.start)) / 1000.0f) * f8) % f8);
            this.shadePath.reset();
            this.shadePath.addRect(0.0f, 0.0f, width, height, Path.Direction.CW);
            canvas.translate((float) padHorz, (float) padVert);
            canvas.clipPath(this.loadingPath);
            canvas.translate((float) (-padHorz), (float) (-padVert));
            float f9 = 0.0f;
            canvas.translate(-elapsedRealtime, 0.0f);
            this.shadePath.offset(elapsedRealtime, 0.0f, this.tempPath);
            canvas.drawPath(this.tempPath, this.loadingPaint);
            canvas.translate(elapsedRealtime, 0.0f);
            canvas.restore();
            canvas.save();
            this.rect.set(0.0f, 0.0f, width, height);
            canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
            canvas.translate((float) padHorz, (float) padVert);
            canvas.clipPath(this.loadingPath);
            RectF rectF = this.rect;
            if (this.showLoadingTextValue) {
                f9 = 0.08f;
            }
            canvas.saveLayerAlpha(rectF, (int) (f9 * 255.0f), 31);
            this.loadingTextView.draw(canvas);
            canvas.restore();
            canvas.restore();
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view != this.textView) {
                return false;
            }
            canvas.save();
            canvas.clipPath(this.inPath);
            if (this.loadingT < 1.0f) {
                this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                canvas.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (this.loadingT < 1.0f) {
                canvas.restore();
            }
            canvas.restore();
            return drawChild;
        }
    }
}
