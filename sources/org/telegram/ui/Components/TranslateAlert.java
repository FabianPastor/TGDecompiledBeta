package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_translateText;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;

public class TranslateAlert extends Dialog {
    /* access modifiers changed from: private */
    public static final int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    private Spannable allTexts;
    /* access modifiers changed from: private */
    public TextView allTextsView;
    /* access modifiers changed from: private */
    public boolean allowScroll;
    private ImageView backButton;
    protected ColorDrawable backDrawable;
    private Rect backRect;
    private int blockIndex;
    /* access modifiers changed from: private */
    public FrameLayout bulletinContainer;
    private Rect buttonRect;
    private FrameLayout buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    /* access modifiers changed from: private */
    public FrameLayout container;
    /* access modifiers changed from: private */
    public float containerOpenAnimationT;
    private Rect containerRect;
    private FrameLayout contentView;
    /* access modifiers changed from: private */
    public boolean dismissed;
    /* access modifiers changed from: private */
    public boolean fastHide;
    private int firstMinHeight;
    /* access modifiers changed from: private */
    public BaseFragment fragment;
    /* access modifiers changed from: private */
    public String fromLanguage;
    private boolean fromScrollRect;
    private float fromScrollViewY;
    private float fromScrollY;
    private float fromY;
    private FrameLayout header;
    private FrameLayout.LayoutParams headerLayout;
    private FrameLayout headerShadowView;
    /* access modifiers changed from: private */
    public float heightMaxPercent;
    /* access modifiers changed from: private */
    public LinkSpanDrawable.LinkCollector links;
    private boolean loaded;
    private boolean loading;
    private boolean maybeScrolling;
    /* access modifiers changed from: private */
    public boolean noforwards;
    private Runnable onDismiss;
    /* access modifiers changed from: private */
    public OnLinkPress onLinkPress;
    private ValueAnimator openAnimationToAnimator;
    /* access modifiers changed from: private */
    public boolean openAnimationToAnimatorPriority;
    private ValueAnimator openingAnimator;
    /* access modifiers changed from: private */
    public boolean openingAnimatorPriority;
    /* access modifiers changed from: private */
    public float openingT;
    private LinkSpanDrawable pressedLink;
    private boolean pressedOutside;
    private Rect scrollRect;
    /* access modifiers changed from: private */
    public NestedScrollView scrollView;
    private FrameLayout.LayoutParams scrollViewLayout;
    private boolean scrolling;
    private ImageView subtitleArrowView;
    /* access modifiers changed from: private */
    public InlineLoadingTextView subtitleFromView;
    private FrameLayout.LayoutParams subtitleLayout;
    private TextView subtitleToView;
    private LinearLayout subtitleView;
    private ArrayList<CharSequence> textBlocks;
    private Rect textRect;
    /* access modifiers changed from: private */
    public FrameLayout textsContainerView;
    /* access modifiers changed from: private */
    public TextBlocksLayout textsView;
    private FrameLayout.LayoutParams titleLayout;
    private TextView titleView;
    /* access modifiers changed from: private */
    public String toLanguage;

    public interface OnLinkPress {
        void run(URLSpan uRLSpan);
    }

    public interface OnTranslationFail {
        void run(boolean z);
    }

    public interface OnTranslationSuccess {
        void run(String str, String str2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$translateText$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public void openAnimation(float f) {
        float f2 = 1.0f;
        float min = Math.min(Math.max(f, 0.0f), 1.0f);
        if (this.containerOpenAnimationT != min) {
            this.containerOpenAnimationT = min;
            this.titleView.setScaleX(AndroidUtilities.lerp(1.0f, 0.9473f, min));
            this.titleView.setScaleY(AndroidUtilities.lerp(1.0f, 0.9473f, min));
            FrameLayout.LayoutParams layoutParams = this.titleLayout;
            int dp = AndroidUtilities.dp((float) AndroidUtilities.lerp(22, 72, min));
            int dp2 = AndroidUtilities.dp((float) AndroidUtilities.lerp(22, 8, min));
            FrameLayout.LayoutParams layoutParams2 = this.titleLayout;
            layoutParams.setMargins(dp, dp2, layoutParams2.rightMargin, layoutParams2.bottomMargin);
            this.titleView.setLayoutParams(this.titleLayout);
            FrameLayout.LayoutParams layoutParams3 = this.subtitleLayout;
            int dp3 = AndroidUtilities.dp((float) AndroidUtilities.lerp(22, 72, min)) - LoadingTextView2.paddingHorizontal;
            int dp4 = AndroidUtilities.dp((float) AndroidUtilities.lerp(47, 30, min)) - LoadingTextView2.paddingVertical;
            FrameLayout.LayoutParams layoutParams4 = this.subtitleLayout;
            layoutParams3.setMargins(dp3, dp4, layoutParams4.rightMargin, layoutParams4.bottomMargin);
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
            this.headerLayout.height = AndroidUtilities.lerp(AndroidUtilities.dp(70.0f), AndroidUtilities.dp(56.0f), min);
            this.header.setLayoutParams(this.headerLayout);
            FrameLayout.LayoutParams layoutParams5 = this.scrollViewLayout;
            int i = layoutParams5.leftMargin;
            int lerp = AndroidUtilities.lerp(AndroidUtilities.dp(70.0f), AndroidUtilities.dp(56.0f), min);
            FrameLayout.LayoutParams layoutParams6 = this.scrollViewLayout;
            layoutParams5.setMargins(i, lerp, layoutParams6.rightMargin, layoutParams6.bottomMargin);
            this.scrollView.setLayoutParams(this.scrollViewLayout);
        }
    }

    /* access modifiers changed from: private */
    public void openAnimationTo(float f, boolean z) {
        openAnimationTo(f, z, (Runnable) null);
    }

    private void openAnimationTo(float f, boolean z, final Runnable runnable) {
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
            this.openAnimationToAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = TranslateAlert.this.openAnimationToAnimatorPriority = false;
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        runnable.run();
                    }
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
        return minHeight(false);
    }

    private int minHeight(boolean z) {
        TextBlocksLayout textBlocksLayout = this.textsView;
        int measuredHeight = textBlocksLayout == null ? 0 : textBlocksLayout.getMeasuredHeight();
        int dp = AndroidUtilities.dp(147.0f) + measuredHeight;
        if (this.firstMinHeight < 0 && measuredHeight > 0) {
            this.firstMinHeight = dp;
        }
        return (this.firstMinHeight <= 0 || this.textBlocks.size() <= 1 || z) ? dp : this.firstMinHeight;
    }

    /* access modifiers changed from: private */
    public boolean canExpand() {
        if (this.textsView.getBlocksCount() < this.textBlocks.size() || ((float) minHeight(true)) >= ((float) AndroidUtilities.displayMetrics.heightPixels) * this.heightMaxPercent) {
            return true;
        }
        return false;
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

    public TranslateAlert(BaseFragment baseFragment, Context context, String str, String str2, CharSequence charSequence, boolean z, OnLinkPress onLinkPress2, Runnable runnable) {
        this(baseFragment, context, -1, (TLRPC$InputPeer) null, -1, str, str2, charSequence, z, onLinkPress2, runnable);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TranslateAlert(org.telegram.ui.ActionBar.BaseFragment r33, android.content.Context r34, int r35, org.telegram.tgnet.TLRPC$InputPeer r36, int r37, java.lang.String r38, java.lang.String r39, java.lang.CharSequence r40, boolean r41, org.telegram.ui.Components.TranslateAlert.OnLinkPress r42, java.lang.Runnable r43) {
        /*
            r32 = this;
            r6 = r32
            r7 = r34
            r0 = r36
            r1 = r38
            r8 = r39
            r9 = r41
            r2 = 2131689509(0x7f0var_, float:1.9008035E38)
            r6.<init>(r7, r2)
            r10 = 0
            r6.blockIndex = r10
            r11 = 0
            r6.containerOpenAnimationT = r11
            r6.openAnimationToAnimatorPriority = r10
            r2 = 0
            r6.openAnimationToAnimator = r2
            r12 = -1
            r6.firstMinHeight = r12
            r13 = 1
            r6.allowScroll = r13
            r6.onLinkPress = r2
            r6.onDismiss = r2
            r6.fromScrollY = r11
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r6.containerRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r6.textRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r6.buttonRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r6.backRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r6.scrollRect = r3
            r6.fromY = r11
            r6.pressedOutside = r10
            r6.maybeScrolling = r10
            r6.scrolling = r10
            r6.fromScrollRect = r10
            r6.fromScrollViewY = r11
            r6.allTexts = r2
            r6.openingT = r11
            org.telegram.ui.Components.TranslateAlert$6 r3 = new org.telegram.ui.Components.TranslateAlert$6
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3.<init>(r4)
            r6.backDrawable = r3
            r6.dismissed = r10
            r3 = 1062836634(0x3var_a, float:0.85)
            r6.heightMaxPercent = r3
            r6.fastHide = r10
            r6.openingAnimatorPriority = r10
            r6.loading = r10
            r6.loaded = r10
            java.lang.String r3 = "und"
            if (r0 == 0) goto L_0x0092
            if (r1 == 0) goto L_0x008a
            boolean r4 = r1.equals(r3)
            if (r4 == 0) goto L_0x008a
            r4 = r35
            r5 = r37
            goto L_0x008f
        L_0x008a:
            r4 = r35
            r5 = r37
            r2 = r1
        L_0x008f:
            translateText(r4, r0, r5, r2, r8)
        L_0x0092:
            r0 = r42
            r6.onLinkPress = r0
            r6.noforwards = r9
            r0 = r33
            r6.fragment = r0
            if (r1 == 0) goto L_0x00a7
            boolean r0 = r1.equals(r3)
            if (r0 == 0) goto L_0x00a7
            java.lang.String r0 = "auto"
            goto L_0x00a8
        L_0x00a7:
            r0 = r1
        L_0x00a8:
            r6.fromLanguage = r0
            r6.toLanguage = r8
            r0 = 1024(0x400, float:1.435E-42)
            r2 = r40
            java.util.ArrayList r0 = r6.cutInBlocks(r2, r0)
            r6.textBlocks = r0
            r0 = r43
            r6.onDismiss = r0
            int r14 = android.os.Build.VERSION.SDK_INT
            r0 = 21
            r2 = 30
            if (r14 < r2) goto L_0x00cd
            android.view.Window r3 = r32.getWindow()
            r4 = -2147483392(0xfffffffvar_, float:-3.59E-43)
            r3.addFlags(r4)
            goto L_0x00d9
        L_0x00cd:
            if (r14 < r0) goto L_0x00d9
            android.view.Window r3 = r32.getWindow()
            r4 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r3.addFlags(r4)
        L_0x00d9:
            if (r9 == 0) goto L_0x00e4
            android.view.Window r3 = r32.getWindow()
            r4 = 8192(0x2000, float:1.14794E-41)
            r3.addFlags(r4)
        L_0x00e4:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r7)
            r6.contentView = r3
            android.graphics.drawable.ColorDrawable r4 = r6.backDrawable
            r3.setBackground(r4)
            android.widget.FrameLayout r3 = r6.contentView
            r3.setClipChildren(r10)
            android.widget.FrameLayout r3 = r6.contentView
            r3.setClipToPadding(r10)
            if (r14 < r0) goto L_0x0112
            android.widget.FrameLayout r0 = r6.contentView
            r0.setFitsSystemWindows(r13)
            if (r14 < r2) goto L_0x010b
            android.widget.FrameLayout r0 = r6.contentView
            r2 = 1792(0x700, float:2.511E-42)
            r0.setSystemUiVisibility(r2)
            goto L_0x0112
        L_0x010b:
            android.widget.FrameLayout r0 = r6.contentView
            r2 = 1280(0x500, float:1.794E-42)
            r0.setSystemUiVisibility(r2)
        L_0x0112:
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            java.lang.String r2 = "dialogBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColor(r2)
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r3 = -1087834685(0xffffffffbvar_f5c3, float:-0.66)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r4 = 503316480(0x1e000000, float:6.7762636E-21)
            r0.setShadowLayer(r2, r11, r3, r4)
            org.telegram.ui.Components.TranslateAlert$2 r2 = new org.telegram.ui.Components.TranslateAlert$2
            r2.<init>(r7, r0)
            r6.container = r2
            r2.setWillNotDraw(r10)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.header = r0
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.titleView = r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0156
            int r2 = r0.getWidth()
            float r2 = (float) r2
            goto L_0x0157
        L_0x0156:
            r2 = 0
        L_0x0157:
            r0.setPivotX(r2)
            android.widget.TextView r0 = r6.titleView
            r0.setPivotY(r11)
            android.widget.TextView r0 = r6.titleView
            r0.setLines(r13)
            android.widget.TextView r0 = r6.titleView
            r2 = 2131624568(0x7f0e0278, float:1.887632E38)
            java.lang.String r3 = "AutomaticTranslation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r6.titleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r5 = 3
            if (r2 == 0) goto L_0x017b
            r2 = 5
            goto L_0x017c
        L_0x017b:
            r2 = 3
        L_0x017c:
            r0.setGravity(r2)
            android.widget.TextView r0 = r6.titleView
            java.lang.String r16 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r16)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r6.titleView
            java.lang.String r17 = "dialogTextBlack"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r6.titleView
            r2 = 1100480512(0x41980000, float:19.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r10, r2)
            android.widget.FrameLayout r0 = r6.header
            android.widget.TextView r2 = r6.titleView
            r18 = -1
            r19 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r20 = 55
            r21 = 1102053376(0x41b00000, float:22.0)
            r22 = 1102053376(0x41b00000, float:22.0)
            r23 = 1102053376(0x41b00000, float:22.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r6.titleLayout = r3
            r0.addView(r2, r3)
            android.widget.TextView r0 = r6.titleView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda5 r2 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda5
            r2.<init>(r6)
            r0.post(r2)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r7)
            r6.subtitleView = r0
            r0.setOrientation(r10)
            r4 = 17
            if (r14 < r4) goto L_0x01db
            android.widget.LinearLayout r0 = r6.subtitleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r0.setLayoutDirection(r2)
        L_0x01db:
            android.widget.LinearLayout r0 = r6.subtitleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x01e3
            r2 = 5
            goto L_0x01e4
        L_0x01e3:
            r2 = 3
        L_0x01e4:
            r0.setGravity(r2)
            java.lang.String r3 = r6.languageName(r1)
            org.telegram.ui.Components.TranslateAlert$3 r2 = new org.telegram.ui.Components.TranslateAlert$3
            if (r3 != 0) goto L_0x01f6
            java.lang.String r0 = r6.languageName(r8)
            r18 = r0
            goto L_0x01f8
        L_0x01f6:
            r18 = r3
        L_0x01f8:
            r1 = 1096810496(0x41600000, float:14.0)
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r20 = "player_actionBarSubtitle"
            int r21 = org.telegram.ui.ActionBar.Theme.getColor(r20)
            r0 = r2
            r33 = 1096810496(0x41600000, float:14.0)
            r1 = r32
            r15 = r2
            r2 = r34
            r12 = r3
            r3 = r18
            r4 = r19
            r5 = r21
            r0.<init>(r2, r3, r4, r5)
            r6.subtitleFromView = r15
            r15.showLoadingText = r10
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.subtitleArrowView = r0
            r1 = 2131166102(0x7var_, float:1.794644E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r6.subtitleArrowView
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r20)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0242
            android.widget.ImageView r0 = r6.subtitleArrowView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r0.setScaleX(r1)
        L_0x0242:
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.subtitleToView = r0
            r0.setLines(r13)
            android.widget.TextView r0 = r6.subtitleToView
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r20)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.subtitleToView
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r33)
            float r1 = (float) r1
            r0.setTextSize(r10, r1)
            android.widget.TextView r0 = r6.subtitleToView
            java.lang.String r1 = r6.languageName(r8)
            r0.setText(r1)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            r1 = 16
            r2 = -2
            if (r0 == 0) goto L_0x02aa
            android.widget.LinearLayout r0 = r6.subtitleView
            int r3 = org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.paddingHorizontal
            r0.setPadding(r3, r10, r10, r10)
            android.widget.LinearLayout r0 = r6.subtitleView
            android.widget.TextView r3 = r6.subtitleToView
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r2, (int) r1)
            r0.addView(r3, r1)
            android.widget.LinearLayout r0 = r6.subtitleView
            android.widget.ImageView r1 = r6.subtitleArrowView
            r25 = -2
            r26 = -2
            r27 = 16
            r28 = 3
            r29 = 1
            r30 = 0
            r31 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r0.addView(r1, r3)
            android.widget.LinearLayout r0 = r6.subtitleView
            org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView r1 = r6.subtitleFromView
            r28 = 2
            r29 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r0.addView(r1, r3)
            goto L_0x02e4
        L_0x02aa:
            android.widget.LinearLayout r0 = r6.subtitleView
            int r3 = org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.paddingHorizontal
            r0.setPadding(r10, r10, r3, r10)
            android.widget.LinearLayout r0 = r6.subtitleView
            org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView r3 = r6.subtitleFromView
            r25 = -2
            r26 = -2
            r27 = 16
            r28 = 0
            r29 = 0
            r30 = 2
            r31 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r0.addView(r3, r4)
            android.widget.LinearLayout r0 = r6.subtitleView
            android.widget.ImageView r3 = r6.subtitleArrowView
            r29 = 1
            r30 = 3
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r0.addView(r3, r4)
            android.widget.LinearLayout r0 = r6.subtitleView
            android.widget.TextView r3 = r6.subtitleToView
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r2, (int) r1)
            r0.addView(r3, r1)
        L_0x02e4:
            if (r12 == 0) goto L_0x02eb
            org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView r0 = r6.subtitleFromView
            r0.set(r12)
        L_0x02eb:
            android.widget.FrameLayout r0 = r6.header
            android.widget.LinearLayout r1 = r6.subtitleView
            r25 = -1
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x02f9
            r15 = 5
            goto L_0x02fa
        L_0x02f9:
            r15 = 3
        L_0x02fa:
            r27 = r15 | 48
            int r3 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingHorizontal
            float r4 = (float) r3
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r5
            r5 = 1102053376(0x41b00000, float:22.0)
            float r28 = r5 - r4
            r4 = 1111228416(0x423CLASSNAME, float:47.0)
            int r8 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingVertical
            float r8 = (float) r8
            float r12 = org.telegram.messenger.AndroidUtilities.density
            float r8 = r8 / r12
            float r29 = r4 - r8
            float r3 = (float) r3
            float r3 = r3 / r12
            float r30 = r5 - r3
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r6.subtitleLayout = r3
            r0.addView(r1, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.backButton = r0
            r1 = 2131165503(0x7var_f, float:1.7945225E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r6.backButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r4)
            r0.setColorFilter(r1)
            android.widget.ImageView r0 = r6.backButton
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.FIT_CENTER
            r0.setScaleType(r1)
            android.widget.ImageView r0 = r6.backButton
            r1 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r3, r10, r4, r10)
            android.widget.ImageView r0 = r6.backButton
            java.lang.String r3 = "dialogButtonSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3)
            r0.setBackground(r3)
            android.widget.ImageView r0 = r6.backButton
            r0.setClickable(r10)
            android.widget.ImageView r0 = r6.backButton
            r0.setAlpha(r11)
            android.widget.ImageView r0 = r6.backButton
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda2
            r3.<init>(r6)
            r0.setOnClickListener(r3)
            android.widget.FrameLayout r0 = r6.header
            android.widget.ImageView r3 = r6.backButton
            r4 = 56
            r8 = 3
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r8)
            r0.addView(r3, r4)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.headerShadowView = r0
            java.lang.String r3 = "dialogShadowLine"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBackgroundColor(r4)
            android.widget.FrameLayout r0 = r6.headerShadowView
            r0.setAlpha(r11)
            android.widget.FrameLayout r0 = r6.header
            android.widget.FrameLayout r4 = r6.headerShadowView
            r8 = 87
            r11 = -1
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r13, (int) r8)
            r0.addView(r4, r8)
            android.widget.FrameLayout r0 = r6.header
            r0.setClipChildren(r10)
            android.widget.FrameLayout r0 = r6.container
            android.widget.FrameLayout r4 = r6.header
            r8 = 70
            r12 = 55
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r8, (int) r12)
            r6.headerLayout = r8
            r0.addView(r4, r8)
            org.telegram.ui.Components.TranslateAlert$4 r0 = new org.telegram.ui.Components.TranslateAlert$4
            r0.<init>(r7)
            r6.scrollView = r0
            r0.setClipChildren(r13)
            org.telegram.ui.Components.TranslateAlert$5 r0 = new org.telegram.ui.Components.TranslateAlert$5
            r0.<init>(r7)
            r6.allTextsView = r0
            org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r4 = new org.telegram.ui.Components.LinkSpanDrawable$LinkCollector
            r4.<init>(r0)
            r6.links = r4
            android.widget.TextView r0 = r6.allTextsView
            r0.setTextColor(r10)
            android.widget.TextView r0 = r6.allTextsView
            r0.setTextSize(r13, r1)
            android.widget.TextView r0 = r6.allTextsView
            r4 = r9 ^ 1
            r0.setTextIsSelectable(r4)
            android.widget.TextView r0 = r6.allTextsView
            java.lang.String r4 = "chat_inTextSelectionHighlight"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setHighlightColor(r4)
            java.lang.String r0 = "chat_TextSelectionCursor"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r4 = 29
            if (r14 < r4) goto L_0x041a
            android.widget.TextView r4 = r6.allTextsView     // Catch:{ Exception -> 0x041a }
            android.graphics.drawable.Drawable r4 = r4.getTextSelectHandleLeft()     // Catch:{ Exception -> 0x041a }
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x041a }
            r4.setColorFilter(r0, r8)     // Catch:{ Exception -> 0x041a }
            android.widget.TextView r8 = r6.allTextsView     // Catch:{ Exception -> 0x041a }
            r8.setTextSelectHandleLeft(r4)     // Catch:{ Exception -> 0x041a }
            android.widget.TextView r4 = r6.allTextsView     // Catch:{ Exception -> 0x041a }
            android.graphics.drawable.Drawable r4 = r4.getTextSelectHandleRight()     // Catch:{ Exception -> 0x041a }
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x041a }
            r4.setColorFilter(r0, r8)     // Catch:{ Exception -> 0x041a }
            android.widget.TextView r0 = r6.allTextsView     // Catch:{ Exception -> 0x041a }
            r0.setTextSelectHandleRight(r4)     // Catch:{ Exception -> 0x041a }
        L_0x041a:
            android.widget.TextView r0 = r6.allTextsView
            android.text.method.LinkMovementMethod r4 = new android.text.method.LinkMovementMethod
            r4.<init>()
            r0.setMovementMethod(r4)
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r0 = new org.telegram.ui.Components.TranslateAlert$TextBlocksLayout
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.widget.TextView r8 = r6.allTextsView
            r0.<init>(r7, r1, r4, r8)
            r6.textsView = r0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingHorizontal
            int r1 = r1 - r4
            r8 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r10 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingVertical
            int r9 = r9 - r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = r4 - r10
            r0.setPadding(r1, r9, r5, r4)
            java.util.ArrayList<java.lang.CharSequence> r0 = r6.textBlocks
            java.util.Iterator r0 = r0.iterator()
        L_0x0458:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x046a
            java.lang.Object r1 = r0.next()
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r4 = r6.textsView
            r4.addBlock(r1)
            goto L_0x0458
        L_0x046a:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.textsContainerView = r0
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r1 = r6.textsView
            r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r0.addView(r1, r4)
            androidx.core.widget.NestedScrollView r0 = r6.scrollView
            android.widget.FrameLayout r1 = r6.textsContainerView
            r4 = 1065353216(0x3var_, float:1.0)
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r2, (float) r4)
            r0.addView((android.view.View) r1, (android.view.ViewGroup.LayoutParams) r4)
            android.widget.FrameLayout r0 = r6.container
            androidx.core.widget.NestedScrollView r1 = r6.scrollView
            r4 = -1
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r8 = 119(0x77, float:1.67E-43)
            r9 = 0
            r10 = 1116471296(0x428CLASSNAME, float:70.0)
            r11 = 0
            r12 = 1117913088(0x42a20000, float:81.0)
            r35 = r4
            r36 = r5
            r37 = r8
            r38 = r9
            r39 = r10
            r40 = r11
            r41 = r12
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r35, r36, r37, r38, r39, r40, r41)
            r6.scrollViewLayout = r4
            r0.addView(r1, r4)
            r32.fetchNext()
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.buttonShadowView = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.container
            android.widget.FrameLayout r1 = r6.buttonShadowView
            r3 = -1
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 87
            r8 = 0
            r10 = 0
            r11 = 1117782016(0x42a00000, float:80.0)
            r35 = r3
            r36 = r4
            r37 = r5
            r38 = r8
            r39 = r9
            r40 = r10
            r41 = r11
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r35, r36, r37, r38, r39, r40, r41)
            r0.addView(r1, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.buttonTextView = r0
            r0.setLines(r13)
            android.widget.TextView r0 = r6.buttonTextView
            r0.setSingleLine(r13)
            android.widget.TextView r0 = r6.buttonTextView
            r0.setGravity(r13)
            android.widget.TextView r0 = r6.buttonTextView
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r6.buttonTextView
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.buttonTextView
            java.lang.String r1 = "featuredStickers_buttonText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.buttonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r16)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.buttonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r13, r1)
            android.widget.TextView r0 = r6.buttonTextView
            r1 = 2131625078(0x7f0e0476, float:1.8877354E38)
            java.lang.String r3 = "CloseTranslation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.buttonView = r0
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r3 = "featuredStickers_addButton"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "featuredStickers_addButtonPressed"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r1, r3, r4)
            r0.setBackground(r1)
            android.widget.FrameLayout r0 = r6.buttonView
            android.widget.TextView r1 = r6.buttonTextView
            r0.addView(r1)
            android.widget.FrameLayout r0 = r6.buttonView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3 r1 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            android.widget.FrameLayout r0 = r6.container
            android.widget.FrameLayout r1 = r6.buttonView
            r3 = -1
            r4 = 1111490560(0x42400000, float:48.0)
            r5 = 80
            r8 = 1098907648(0x41800000, float:16.0)
            r9 = 1098907648(0x41800000, float:16.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r11 = 1098907648(0x41800000, float:16.0)
            r35 = r3
            r36 = r4
            r37 = r5
            r38 = r8
            r39 = r9
            r40 = r10
            r41 = r11
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r35, r36, r37, r38, r39, r40, r41)
            r0.addView(r1, r3)
            android.widget.FrameLayout r0 = r6.contentView
            android.widget.FrameLayout r1 = r6.container
            r3 = 81
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r2, (int) r3)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.bulletinContainer = r0
            android.widget.FrameLayout r1 = r6.contentView
            r2 = -1
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = 119(0x77, float:1.67E-43)
            r5 = 0
            r7 = 0
            r8 = 0
            r9 = 1117913088(0x42a20000, float:81.0)
            r33 = r2
            r34 = r3
            r35 = r4
            r36 = r5
            r37 = r7
            r38 = r8
            r39 = r9
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r1.addView(r0, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, int, org.telegram.tgnet.TLRPC$InputPeer, int, java.lang.String, java.lang.String, java.lang.CharSequence, boolean, org.telegram.ui.Components.TranslateAlert$OnLinkPress, java.lang.Runnable):void");
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
    public /* synthetic */ void lambda$new$3(View view) {
        dismiss();
    }

    public void showDim(boolean z) {
        this.contentView.setBackground(z ? this.backDrawable : null);
    }

    private boolean scrollAtBottom() {
        NestedScrollView nestedScrollView = this.scrollView;
        int bottom = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1).getBottom();
        LoadingTextView2 firstUnloadedBlock = this.textsView.getFirstUnloadedBlock();
        if (firstUnloadedBlock != null) {
            bottom = firstUnloadedBlock.getTop();
        }
        if (bottom - (this.scrollView.getHeight() + this.scrollView.getScrollY()) <= this.textsContainerView.getPaddingBottom()) {
            return true;
        }
        return false;
    }

    private void setScrollY(float f) {
        openAnimation(f);
        float max = Math.max(Math.min(f + 1.0f, 1.0f), 0.0f);
        this.openingT = max;
        this.backDrawable.setAlpha((int) (max * 51.0f));
        this.container.invalidate();
        this.bulletinContainer.setTranslationY((1.0f - this.openingT) * Math.min((float) minHeight(), ((float) AndroidUtilities.displayMetrics.heightPixels) * this.heightMaxPercent));
    }

    private void scrollYTo(float f) {
        scrollYTo(f, (Runnable) null);
    }

    private void scrollYTo(float f, Runnable runnable) {
        openAnimationTo(f, false, runnable);
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
        ClickableSpan[] clickableSpanArr;
        try {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            this.container.getGlobalVisibleRect(this.containerRect);
            int i = (int) x;
            int i2 = (int) y;
            boolean z = false;
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
            try {
                this.allTextsView.getGlobalVisibleRect(this.textRect);
                if (this.textRect.contains(i, i2) && !this.maybeScrolling) {
                    Layout layout = this.allTextsView.getLayout();
                    int top = (int) ((((y - ((float) this.allTextsView.getTop())) - ((float) this.container.getTop())) - ((float) this.scrollView.getTop())) + ((float) this.scrollView.getScrollY()));
                    int lineForVertical = layout.getLineForVertical(top);
                    float left = (float) ((int) ((x - ((float) this.allTextsView.getLeft())) - ((float) this.container.getLeft())));
                    int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, left);
                    float lineLeft = layout.getLineLeft(lineForVertical);
                    if ((this.allTexts instanceof Spannable) && lineLeft <= left && lineLeft + layout.getLineWidth(lineForVertical) >= left && (clickableSpanArr = (ClickableSpan[]) this.allTexts.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class)) != null && clickableSpanArr.length >= 1) {
                        if (motionEvent.getAction() == 1 && this.pressedLink.getSpan() == clickableSpanArr[0]) {
                            ((ClickableSpan) this.pressedLink.getSpan()).onClick(this.allTextsView);
                            LinkSpanDrawable.LinkCollector linkCollector = this.links;
                            if (linkCollector != null) {
                                linkCollector.removeLink(this.pressedLink);
                            }
                            this.pressedLink = null;
                            this.allTextsView.setTextIsSelectable(!this.noforwards);
                        } else if (motionEvent.getAction() == 0) {
                            LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(clickableSpanArr[0], this.fragment.getResourceProvider(), left, (float) top, false);
                            this.pressedLink = linkSpanDrawable;
                            LinkSpanDrawable.LinkCollector linkCollector2 = this.links;
                            if (linkCollector2 != null) {
                                linkCollector2.addLink(linkSpanDrawable);
                            }
                            LinkPath obtainNewPath = this.pressedLink.obtainNewPath();
                            int spanStart = this.allTexts.getSpanStart(this.pressedLink.getSpan());
                            int spanEnd = this.allTexts.getSpanEnd(this.pressedLink.getSpan());
                            obtainNewPath.setCurrentLayout(layout, spanStart, 0.0f);
                            layout.getSelectionPath(spanStart, spanEnd, obtainNewPath);
                        }
                        this.allTextsView.invalidate();
                        return true;
                    }
                }
                if (this.pressedLink != null) {
                    LinkSpanDrawable.LinkCollector linkCollector3 = this.links;
                    if (linkCollector3 != null) {
                        linkCollector3.clear();
                    }
                    this.pressedLink = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.scrollView.getGlobalVisibleRect(this.scrollRect);
            this.backButton.getGlobalVisibleRect(this.backRect);
            this.buttonView.getGlobalVisibleRect(this.buttonRect);
            if (this.pressedLink == null && !hasSelection()) {
                if (!this.backRect.contains(i, i2) && !this.buttonRect.contains(i, i2) && motionEvent.getAction() == 0) {
                    this.fromScrollRect = this.scrollRect.contains(i, i2) && (this.containerOpenAnimationT > 0.0f || !canExpand());
                    this.maybeScrolling = true;
                    if (this.scrollRect.contains(i, i2) && this.textsView.getBlocksCount() > 0 && !this.textsView.getBlockAt(0).loaded) {
                        z = true;
                    }
                    this.scrolling = z;
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
                    float min = Math.min((float) minHeight(), this.heightMaxPercent * f3);
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
                            this.allTextsView.setTextIsSelectable(!this.noforwards);
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
                            scrollYTo(f, new TranslateAlert$$ExternalSyntheticLambda4(this));
                        }
                        return true;
                    }
                }
            }
            if (hasSelection() && this.maybeScrolling) {
                this.scrolling = false;
                this.allTextsView.setTextIsSelectable(!this.noforwards);
                this.maybeScrolling = false;
                this.allowScroll = true;
                scrollYTo((float) Math.round(this.fromScrollY));
            }
            return super.dispatchTouchEvent(motionEvent);
        } catch (Exception e2) {
            e2.printStackTrace();
            return super.dispatchTouchEvent(motionEvent);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchTouchEvent$4() {
        this.contentView.post(new TranslateAlert$$ExternalSyntheticLambda7(this));
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
        Runnable runnable;
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
            if (min <= 0.0f && (runnable = this.onDismiss) != null) {
                runnable.run();
            }
            this.openingAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (min <= 0.0f) {
                        TranslateAlert.this.dismissInternal();
                    } else if (z2) {
                        TranslateAlert.this.allTextsView.setTextIsSelectable(!TranslateAlert.this.noforwards);
                        TranslateAlert.this.allTextsView.invalidate();
                        TranslateAlert.this.scrollView.stopNestedScroll();
                        TranslateAlert.this.openAnimation(min - 1.0f);
                    }
                    boolean unused = TranslateAlert.this.openingAnimatorPriority = false;
                }
            });
            this.openingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.openingAnimator.setDuration((long) (Math.abs(this.openingT - min) * ((float) (this.fastHide ? 200 : 380))));
            this.openingAnimator.setStartDelay(z2 ? 60 : 0);
            this.openingAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openTo$5(ValueAnimator valueAnimator) {
        this.openingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.invalidate();
        this.backDrawable.setAlpha((int) (this.openingT * 51.0f));
        this.bulletinContainer.setTranslationY((1.0f - this.openingT) * Math.min((float) minHeight(), ((float) AndroidUtilities.displayMetrics.heightPixels) * this.heightMaxPercent));
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public String languageName(String str) {
        if (str == null || str.equals("und") || str.equals("auto")) {
            return null;
        }
        LocaleController.LocaleInfo builtinLanguageByPlural = LocaleController.getInstance().getBuiltinLanguageByPlural(str);
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        if (builtinLanguageByPlural == null) {
            return null;
        }
        if (currentLocaleInfo != null && "en".equals(currentLocaleInfo.pluralLangCode)) {
            return builtinLanguageByPlural.nameEnglish;
        }
        return builtinLanguageByPlural.name;
    }

    public void updateSourceLanguage() {
        if (languageName(this.fromLanguage) != null) {
            this.subtitleView.setAlpha(1.0f);
            InlineLoadingTextView inlineLoadingTextView = this.subtitleFromView;
            if (!inlineLoadingTextView.loaded) {
                inlineLoadingTextView.loaded(languageName(this.fromLanguage));
            }
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
            if (lastIndexOf == -1) {
                lastIndexOf = Math.min(charSequence2.length(), i);
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

    private boolean fetchNext() {
        if (this.loading) {
            return false;
        }
        this.loading = true;
        if (this.blockIndex >= this.textBlocks.size()) {
            return false;
        }
        fetchTranslation(this.textBlocks.get(this.blockIndex), (long) Math.min((this.blockIndex + 1) * 1000, 3500), new TranslateAlert$$ExternalSyntheticLambda10(this), new TranslateAlert$$ExternalSyntheticLambda9(this));
        return true;
    }

    /* JADX WARNING: type inference failed for: r1v17, types: [android.text.Spannable] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fetchNext$7(java.lang.String r11, java.lang.String r12) {
        /*
            r10 = this;
            r0 = 1
            r10.loaded = r0
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            r7.<init>(r11)
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 1
            r11 = 0
            r2 = r7
            org.telegram.messenger.MessageObject.addUrlsByPattern(r1, r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x008a }
            int r1 = r7.length()     // Catch:{ Exception -> 0x008a }
            java.lang.Class<android.text.style.URLSpan> r2 = android.text.style.URLSpan.class
            java.lang.Object[] r1 = r7.getSpans(r11, r1, r2)     // Catch:{ Exception -> 0x008a }
            android.text.style.URLSpan[] r1 = (android.text.style.URLSpan[]) r1     // Catch:{ Exception -> 0x008a }
            r2 = 0
        L_0x001f:
            int r3 = r1.length     // Catch:{ Exception -> 0x008a }
            r4 = 33
            r5 = -1
            if (r2 >= r3) goto L_0x0042
            r3 = r1[r2]     // Catch:{ Exception -> 0x008a }
            int r6 = r7.getSpanStart(r3)     // Catch:{ Exception -> 0x008a }
            int r8 = r7.getSpanEnd(r3)     // Catch:{ Exception -> 0x008a }
            if (r6 == r5) goto L_0x003f
            if (r8 != r5) goto L_0x0034
            goto L_0x003f
        L_0x0034:
            r7.removeSpan(r3)     // Catch:{ Exception -> 0x008a }
            org.telegram.ui.Components.TranslateAlert$8 r5 = new org.telegram.ui.Components.TranslateAlert$8     // Catch:{ Exception -> 0x008a }
            r5.<init>(r3)     // Catch:{ Exception -> 0x008a }
            r7.setSpan(r5, r6, r8, r4)     // Catch:{ Exception -> 0x008a }
        L_0x003f:
            int r2 = r2 + 1
            goto L_0x001f
        L_0x0042:
            org.telegram.messenger.AndroidUtilities.addLinks(r7, r0)     // Catch:{ Exception -> 0x008a }
            int r1 = r7.length()     // Catch:{ Exception -> 0x008a }
            java.lang.Class<android.text.style.URLSpan> r2 = android.text.style.URLSpan.class
            java.lang.Object[] r1 = r7.getSpans(r11, r1, r2)     // Catch:{ Exception -> 0x008a }
            android.text.style.URLSpan[] r1 = (android.text.style.URLSpan[]) r1     // Catch:{ Exception -> 0x008a }
            r2 = 0
        L_0x0052:
            int r3 = r1.length     // Catch:{ Exception -> 0x008a }
            if (r2 >= r3) goto L_0x0072
            r3 = r1[r2]     // Catch:{ Exception -> 0x008a }
            int r6 = r7.getSpanStart(r3)     // Catch:{ Exception -> 0x008a }
            int r8 = r7.getSpanEnd(r3)     // Catch:{ Exception -> 0x008a }
            if (r6 == r5) goto L_0x006f
            if (r8 != r5) goto L_0x0064
            goto L_0x006f
        L_0x0064:
            r7.removeSpan(r3)     // Catch:{ Exception -> 0x008a }
            org.telegram.ui.Components.TranslateAlert$9 r9 = new org.telegram.ui.Components.TranslateAlert$9     // Catch:{ Exception -> 0x008a }
            r9.<init>(r3)     // Catch:{ Exception -> 0x008a }
            r7.setSpan(r9, r6, r8, r4)     // Catch:{ Exception -> 0x008a }
        L_0x006f:
            int r2 = r2 + 1
            goto L_0x0052
        L_0x0072:
            android.widget.TextView r1 = r10.allTextsView     // Catch:{ Exception -> 0x008a }
            android.text.TextPaint r1 = r1.getPaint()     // Catch:{ Exception -> 0x008a }
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()     // Catch:{ Exception -> 0x008a }
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x008a }
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r7, r1, r2, r11)     // Catch:{ Exception -> 0x008a }
            android.text.Spannable r1 = (android.text.Spannable) r1     // Catch:{ Exception -> 0x008a }
            r7 = r1
            goto L_0x008e
        L_0x008a:
            r1 = move-exception
            r1.printStackTrace()
        L_0x008e:
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            android.text.Spannable r2 = r10.allTexts
            java.lang.String r3 = ""
            if (r2 != 0) goto L_0x0097
            r2 = r3
        L_0x0097:
            r1.<init>(r2)
            int r2 = r10.blockIndex
            if (r2 != 0) goto L_0x009f
            goto L_0x00a1
        L_0x009f:
            java.lang.String r3 = "\n"
        L_0x00a1:
            android.text.SpannableStringBuilder r1 = r1.append(r3)
            android.text.SpannableStringBuilder r1 = r1.append(r7)
            r10.allTexts = r1
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r2 = r10.textsView
            r2.setWholeText(r1)
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r1 = r10.textsView
            int r2 = r10.blockIndex
            org.telegram.ui.Components.TranslateAlert$LoadingTextView2 r1 = r1.getBlockAt(r2)
            if (r1 == 0) goto L_0x00c2
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6 r2 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6
            r2.<init>(r10)
            r1.loaded(r7, r2)
        L_0x00c2:
            if (r12 == 0) goto L_0x00c9
            r10.fromLanguage = r12
            r10.updateSourceLanguage()
        L_0x00c9:
            int r12 = r10.blockIndex
            int r12 = r12 + r0
            r10.blockIndex = r12
            r10.loading = r11
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.lambda$fetchNext$7(java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNext$6() {
        this.contentView.post(new TranslateAlert$$ExternalSyntheticLambda7(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNext$8(boolean z) {
        if (z) {
            Toast.makeText(getContext(), LocaleController.getString("TranslationFailedAlert1", NUM), 0).show();
        } else {
            Toast.makeText(getContext(), LocaleController.getString("TranslationFailedAlert2", NUM), 0).show();
        }
        if (this.blockIndex == 0) {
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public boolean checkForNextLoading() {
        if (!scrollAtBottom()) {
            return false;
        }
        fetchNext();
        return true;
    }

    private void fetchTranslation(CharSequence charSequence, long j, OnTranslationSuccess onTranslationSuccess, OnTranslationFail onTranslationFail) {
        final CharSequence charSequence2 = charSequence;
        final long j2 = j;
        final OnTranslationSuccess onTranslationSuccess2 = onTranslationSuccess;
        final OnTranslationFail onTranslationFail2 = onTranslationFail;
        new Thread() {
            /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x0159 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r11 = this;
                    java.lang.String r0 = "-"
                    long r1 = android.os.SystemClock.elapsedRealtime()
                    r3 = 0
                    r4 = 0
                    java.lang.String r5 = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015c }
                    r6.<init>()     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    org.telegram.ui.Components.TranslateAlert r5 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r5.fromLanguage     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = android.net.Uri.encode(r5)     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x015c }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015c }
                    r6.<init>()     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = "&tl="
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x015c }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015c }
                    r6.<init>()     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    org.telegram.ui.Components.TranslateAlert r5 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r5.toLanguage     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = android.net.Uri.encode(r5)     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x015c }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015c }
                    r6.<init>()     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q="
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x015c }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015c }
                    r6.<init>()     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.CharSequence r5 = r2     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = android.net.Uri.encode(r5)     // Catch:{ Exception -> 0x015c }
                    r6.append(r5)     // Catch:{ Exception -> 0x015c }
                    java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x015c }
                    java.net.URI r6 = new java.net.URI     // Catch:{ Exception -> 0x015c }
                    r6.<init>(r5)     // Catch:{ Exception -> 0x015c }
                    java.net.URL r5 = r6.toURL()     // Catch:{ Exception -> 0x015c }
                    java.net.URLConnection r5 = r5.openConnection()     // Catch:{ Exception -> 0x015c }
                    java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5     // Catch:{ Exception -> 0x015c }
                    java.lang.String r6 = "GET"
                    r5.setRequestMethod(r6)     // Catch:{ Exception -> 0x015a }
                    java.lang.String r6 = "User-Agent"
                    java.lang.String r7 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
                    r5.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x015a }
                    java.lang.String r6 = "Content-Type"
                    java.lang.String r7 = "application/json"
                    r5.setRequestProperty(r6, r7)     // Catch:{ Exception -> 0x015a }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015a }
                    r6.<init>()     // Catch:{ Exception -> 0x015a }
                    java.io.BufferedReader r7 = new java.io.BufferedReader     // Catch:{ Exception -> 0x015a }
                    java.io.InputStreamReader r8 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x015a }
                    java.io.InputStream r9 = r5.getInputStream()     // Catch:{ Exception -> 0x015a }
                    java.lang.String r10 = "UTF-8"
                    java.nio.charset.Charset r10 = java.nio.charset.Charset.forName(r10)     // Catch:{ Exception -> 0x015a }
                    r8.<init>(r9, r10)     // Catch:{ Exception -> 0x015a }
                    r7.<init>(r8)     // Catch:{ Exception -> 0x015a }
                L_0x00b2:
                    int r8 = r7.read()     // Catch:{ all -> 0x0155 }
                    r9 = -1
                    if (r8 == r9) goto L_0x00be
                    char r8 = (char) r8     // Catch:{ all -> 0x0155 }
                    r6.append(r8)     // Catch:{ all -> 0x0155 }
                    goto L_0x00b2
                L_0x00be:
                    r7.close()     // Catch:{ Exception -> 0x015a }
                    java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x015a }
                    org.json.JSONTokener r7 = new org.json.JSONTokener     // Catch:{ Exception -> 0x015a }
                    r7.<init>(r6)     // Catch:{ Exception -> 0x015a }
                    org.json.JSONArray r6 = new org.json.JSONArray     // Catch:{ Exception -> 0x015a }
                    r6.<init>(r7)     // Catch:{ Exception -> 0x015a }
                    org.json.JSONArray r7 = r6.getJSONArray(r4)     // Catch:{ Exception -> 0x015a }
                    r8 = 2
                    java.lang.String r6 = r6.getString(r8)     // Catch:{ Exception -> 0x00d9 }
                    goto L_0x00da
                L_0x00d9:
                    r6 = r3
                L_0x00da:
                    if (r6 == 0) goto L_0x00ea
                    boolean r8 = r6.contains(r0)     // Catch:{ Exception -> 0x015a }
                    if (r8 == 0) goto L_0x00ea
                    int r0 = r6.indexOf(r0)     // Catch:{ Exception -> 0x015a }
                    java.lang.String r6 = r6.substring(r4, r0)     // Catch:{ Exception -> 0x015a }
                L_0x00ea:
                    java.lang.String r0 = ""
                    r8 = 0
                L_0x00ed:
                    int r9 = r7.length()     // Catch:{ Exception -> 0x015a }
                    if (r8 >= r9) goto L_0x0117
                    org.json.JSONArray r9 = r7.getJSONArray(r8)     // Catch:{ Exception -> 0x015a }
                    java.lang.String r9 = r9.getString(r4)     // Catch:{ Exception -> 0x015a }
                    if (r9 == 0) goto L_0x0114
                    java.lang.String r10 = "null"
                    boolean r10 = r9.equals(r10)     // Catch:{ Exception -> 0x015a }
                    if (r10 != 0) goto L_0x0114
                    java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015a }
                    r10.<init>()     // Catch:{ Exception -> 0x015a }
                    r10.append(r0)     // Catch:{ Exception -> 0x015a }
                    r10.append(r9)     // Catch:{ Exception -> 0x015a }
                    java.lang.String r0 = r10.toString()     // Catch:{ Exception -> 0x015a }
                L_0x0114:
                    int r8 = r8 + 1
                    goto L_0x00ed
                L_0x0117:
                    java.lang.CharSequence r7 = r2     // Catch:{ Exception -> 0x015a }
                    int r7 = r7.length()     // Catch:{ Exception -> 0x015a }
                    if (r7 <= 0) goto L_0x013a
                    java.lang.CharSequence r7 = r2     // Catch:{ Exception -> 0x015a }
                    char r7 = r7.charAt(r4)     // Catch:{ Exception -> 0x015a }
                    r8 = 10
                    if (r7 != r8) goto L_0x013a
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x015a }
                    r7.<init>()     // Catch:{ Exception -> 0x015a }
                    java.lang.String r8 = "\n"
                    r7.append(r8)     // Catch:{ Exception -> 0x015a }
                    r7.append(r0)     // Catch:{ Exception -> 0x015a }
                    java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x015a }
                L_0x013a:
                    long r7 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x015a }
                    long r7 = r7 - r1
                    long r1 = r3     // Catch:{ Exception -> 0x015a }
                    int r9 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
                    if (r9 >= 0) goto L_0x0149
                    long r1 = r1 - r7
                    java.lang.Thread.sleep(r1)     // Catch:{ Exception -> 0x015a }
                L_0x0149:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationSuccess r1 = r5     // Catch:{ Exception -> 0x015a }
                    org.telegram.ui.Components.TranslateAlert$10$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.TranslateAlert$10$$ExternalSyntheticLambda2     // Catch:{ Exception -> 0x015a }
                    r2.<init>(r1, r0, r6)     // Catch:{ Exception -> 0x015a }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x015a }
                    goto L_0x01c2
                L_0x0155:
                    r0 = move-exception
                    r7.close()     // Catch:{ all -> 0x0159 }
                L_0x0159:
                    throw r0     // Catch:{ Exception -> 0x015a }
                L_0x015a:
                    r0 = move-exception
                    goto L_0x015e
                L_0x015c:
                    r0 = move-exception
                    r5 = r3
                L_0x015e:
                    java.lang.String r1 = "translate"
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x018f }
                    r2.<init>()     // Catch:{ IOException -> 0x018f }
                    java.lang.String r6 = "failed to translate a text "
                    r2.append(r6)     // Catch:{ IOException -> 0x018f }
                    if (r5 == 0) goto L_0x0175
                    int r6 = r5.getResponseCode()     // Catch:{ IOException -> 0x018f }
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ IOException -> 0x018f }
                    goto L_0x0176
                L_0x0175:
                    r6 = r3
                L_0x0176:
                    r2.append(r6)     // Catch:{ IOException -> 0x018f }
                    java.lang.String r6 = " "
                    r2.append(r6)     // Catch:{ IOException -> 0x018f }
                    if (r5 == 0) goto L_0x0184
                    java.lang.String r3 = r5.getResponseMessage()     // Catch:{ IOException -> 0x018f }
                L_0x0184:
                    r2.append(r3)     // Catch:{ IOException -> 0x018f }
                    java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x018f }
                    android.util.Log.e(r1, r2)     // Catch:{ IOException -> 0x018f }
                    goto L_0x0193
                L_0x018f:
                    r1 = move-exception
                    r1.printStackTrace()
                L_0x0193:
                    r0.printStackTrace()
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r6
                    if (r0 == 0) goto L_0x01c2
                    org.telegram.ui.Components.TranslateAlert r0 = org.telegram.ui.Components.TranslateAlert.this
                    boolean r0 = r0.dismissed
                    if (r0 != 0) goto L_0x01c2
                    if (r5 == 0) goto L_0x01ad
                    int r0 = r5.getResponseCode()     // Catch:{ Exception -> 0x01b8 }
                    r1 = 429(0x1ad, float:6.01E-43)
                    if (r0 != r1) goto L_0x01ad
                    r4 = 1
                L_0x01ad:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r6     // Catch:{ Exception -> 0x01b8 }
                    org.telegram.ui.Components.TranslateAlert$10$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.TranslateAlert$10$$ExternalSyntheticLambda1     // Catch:{ Exception -> 0x01b8 }
                    r1.<init>(r0, r4)     // Catch:{ Exception -> 0x01b8 }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x01b8 }
                    goto L_0x01c2
                L_0x01b8:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r6
                    org.telegram.ui.Components.TranslateAlert$10$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.TranslateAlert$10$$ExternalSyntheticLambda0
                    r1.<init>(r0)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
                L_0x01c2:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.AnonymousClass10.run():void");
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$run$0(OnTranslationSuccess onTranslationSuccess, String str, String str2) {
                if (onTranslationSuccess != null) {
                    onTranslationSuccess.run(str, str2);
                }
            }
        }.start();
    }

    private static void translateText(int i, TLRPC$InputPeer tLRPC$InputPeer, int i2, String str, String str2) {
        TLRPC$TL_messages_translateText tLRPC$TL_messages_translateText = new TLRPC$TL_messages_translateText();
        tLRPC$TL_messages_translateText.peer = tLRPC$InputPeer;
        tLRPC$TL_messages_translateText.msg_id = i2;
        int i3 = tLRPC$TL_messages_translateText.flags | 1;
        tLRPC$TL_messages_translateText.flags = i3;
        if (str != null) {
            tLRPC$TL_messages_translateText.from_lang = str;
            tLRPC$TL_messages_translateText.flags = i3 | 4;
        }
        tLRPC$TL_messages_translateText.to_lang = str2;
        try {
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_translateText, TranslateAlert$$ExternalSyntheticLambda8.INSTANCE);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static TranslateAlert showAlert(Context context, BaseFragment baseFragment, int i, TLRPC$InputPeer tLRPC$InputPeer, int i2, String str, String str2, CharSequence charSequence, boolean z, OnLinkPress onLinkPress2, Runnable runnable) {
        BaseFragment baseFragment2 = baseFragment;
        TranslateAlert translateAlert = new TranslateAlert(baseFragment, context, i, tLRPC$InputPeer, i2, str, str2, charSequence, z, onLinkPress2, runnable);
        if (baseFragment2 == null) {
            translateAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(translateAlert);
        }
        return translateAlert;
    }

    public static TranslateAlert showAlert(Context context, BaseFragment baseFragment, String str, String str2, CharSequence charSequence, boolean z, OnLinkPress onLinkPress2, Runnable runnable) {
        BaseFragment baseFragment2 = baseFragment;
        TranslateAlert translateAlert = new TranslateAlert(baseFragment, context, str, str2, charSequence, z, onLinkPress2, runnable);
        if (baseFragment2 == null) {
            translateAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(translateAlert);
        }
        return translateAlert;
    }

    public static class TextBlocksLayout extends ViewGroup {
        private static final int gap = (((-LoadingTextView2.paddingVertical) * 4) + AndroidUtilities.dp(0.48f));
        private final int fontSize;
        private final int textColor;
        private TextView wholeTextView;

        /* access modifiers changed from: protected */
        public void onHeightUpdated(int i) {
        }

        public TextBlocksLayout(Context context, int i, int i2, TextView textView) {
            super(context);
            this.fontSize = i;
            this.textColor = i2;
            if (textView != null) {
                int i3 = LoadingTextView2.paddingHorizontal;
                int i4 = LoadingTextView2.paddingVertical;
                textView.setPadding(i3, i4, i3, i4);
                this.wholeTextView = textView;
                addView(textView);
            }
        }

        public void setWholeText(CharSequence charSequence) {
            this.wholeTextView.clearFocus();
            this.wholeTextView.setText(charSequence);
        }

        public LoadingTextView2 addBlock(CharSequence charSequence) {
            LoadingTextView2 loadingTextView2 = new LoadingTextView2(getContext(), charSequence, getBlocksCount() > 0, this.fontSize, this.textColor);
            addView(loadingTextView2);
            TextView textView = this.wholeTextView;
            if (textView != null) {
                textView.bringToFront();
            }
            return loadingTextView2;
        }

        public int getBlocksCount() {
            return getChildCount() - (this.wholeTextView != null ? 1 : 0);
        }

        public LoadingTextView2 getBlockAt(int i) {
            View childAt = getChildAt(i);
            if (childAt instanceof LoadingTextView2) {
                return (LoadingTextView2) childAt;
            }
            return null;
        }

        public LoadingTextView2 getFirstUnloadedBlock() {
            int blocksCount = getBlocksCount();
            for (int i = 0; i < blocksCount; i++) {
                LoadingTextView2 blockAt = getBlockAt(i);
                if (blockAt != null && !blockAt.loaded) {
                    return blockAt;
                }
            }
            return null;
        }

        public int height() {
            int blocksCount = getBlocksCount();
            int i = 0;
            for (int i2 = 0; i2 < blocksCount; i2++) {
                i += getBlockAt(i2).height();
            }
            return getPaddingTop() + i + getPaddingBottom();
        }

        public void updateHeight() {
            int height = height();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            boolean z = true;
            if (layoutParams == null) {
                layoutParams = new FrameLayout.LayoutParams(-1, height);
            } else {
                if (layoutParams.height == height) {
                    z = false;
                }
                layoutParams.height = height;
            }
            if (z) {
                setLayoutParams(layoutParams);
                onHeightUpdated(height);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int blocksCount = getBlocksCount();
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight(), View.MeasureSpec.getMode(i));
            for (int i3 = 0; i3 < blocksCount; i3++) {
                getBlockAt(i3).measure(makeMeasureSpec, TranslateAlert.MOST_SPEC);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(height(), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int blocksCount = getBlocksCount();
            int i5 = 0;
            int i6 = 0;
            while (i5 < blocksCount) {
                LoadingTextView2 blockAt = getBlockAt(i5);
                int height = blockAt.height();
                int i7 = i5 > 0 ? gap : 0;
                blockAt.layout(getPaddingLeft(), getPaddingTop() + i6 + i7, (i3 - i) - getPaddingRight(), getPaddingTop() + i6 + height + i7);
                i6 += height;
                if (i5 > 0 && i5 < blocksCount - 1) {
                    i6 += gap;
                }
                i5++;
            }
            int i8 = i3 - i;
            this.wholeTextView.measure(View.MeasureSpec.makeMeasureSpec((i8 - getPaddingLeft()) - getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(((i4 - i2) - getPaddingTop()) - getPaddingBottom(), NUM));
            this.wholeTextView.layout(getPaddingLeft(), getPaddingTop(), i8 - getPaddingRight(), getPaddingTop() + this.wholeTextView.getMeasuredHeight());
        }
    }

    public static class InlineLoadingTextView extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(6.0f);
        private final TextView fromTextView;
        private final float gradientWidth;
        private final Path inPath = new Path();
        public boolean loaded = false;
        private ValueAnimator loadedAnimator = null;
        private final ValueAnimator loadingAnimator;
        private final Paint loadingPaint;
        private final Path loadingPath = new Path();
        public float loadingT = 0.0f;
        private final RectF rect = new RectF();
        private final Path shadePath = new Path();
        public boolean showLoadingText = true;
        private final long start = SystemClock.elapsedRealtime();
        private final Path tempPath = new Path();
        private final TextView toTextView;

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onLoadAnimation(float f) {
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public InlineLoadingTextView(Context context, CharSequence charSequence, int i, int i2) {
            super(context);
            Context context2 = context;
            int i3 = i2;
            Paint paint = new Paint();
            this.loadingPaint = paint;
            float dp = (float) AndroidUtilities.dp(350.0f);
            this.gradientWidth = dp;
            int i4 = paddingHorizontal;
            setPadding(i4, 0, i4, 0);
            setClipChildren(false);
            setWillNotDraw(false);
            AnonymousClass1 r7 = new TextView(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(TranslateAlert.MOST_SPEC, TranslateAlert.MOST_SPEC);
                }
            };
            this.fromTextView = r7;
            float f = (float) i;
            r7.setTextSize(0, f);
            r7.setTextColor(i3);
            r7.setText(charSequence);
            r7.setLines(1);
            r7.setMaxLines(1);
            r7.setSingleLine(true);
            r7.setEllipsize((TextUtils.TruncateAt) null);
            addView(r7);
            AnonymousClass2 r72 = new TextView(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(TranslateAlert.MOST_SPEC, TranslateAlert.MOST_SPEC);
                }
            };
            this.toTextView = r72;
            r72.setTextSize(0, f);
            r72.setTextColor(i3);
            r72.setLines(1);
            r72.setMaxLines(1);
            r72.setSingleLine(true);
            r72.setEllipsize((TextUtils.TruncateAt) null);
            addView(r72);
            int color = Theme.getColor("dialogBackground");
            paint.setShader(new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color, Theme.getColor("dialogBackgroundGray"), color}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.loadingAnimator = ofFloat;
            ofFloat.addUpdateListener(new TranslateAlert$InlineLoadingTextView$$ExternalSyntheticLambda0(this));
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            this.fromTextView.measure(0, 0);
            this.toTextView.measure(0, 0);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(this.fromTextView.getMeasuredWidth(), this.toTextView.getMeasuredWidth(), this.loadingT) + getPaddingLeft() + getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(Math.max(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight()), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.fromTextView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.fromTextView.getMeasuredWidth(), getPaddingTop() + this.fromTextView.getMeasuredHeight());
            this.toTextView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.toTextView.getMeasuredWidth(), getPaddingTop() + this.toTextView.getMeasuredHeight());
            updateWidth();
        }

        private void updateWidth() {
            int lerp = AndroidUtilities.lerp(this.fromTextView.getMeasuredWidth(), this.toTextView.getMeasuredWidth(), this.loadingT) + getPaddingLeft() + getPaddingRight();
            int max = Math.max(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight());
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            boolean z = true;
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(lerp, max);
            } else {
                if (layoutParams.width == lerp && layoutParams.height == max) {
                    z = false;
                }
                layoutParams.width = lerp;
                layoutParams.height = max;
            }
            if (z) {
                setLayoutParams(layoutParams);
            }
        }

        public void loaded(CharSequence charSequence) {
            loaded(charSequence, 350, (Runnable) null);
        }

        public void loaded(CharSequence charSequence, long j, final Runnable runnable) {
            this.loaded = true;
            this.toTextView.setText(charSequence);
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            if (this.loadedAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.loadedAnimator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$InlineLoadingTextView$$ExternalSyntheticLambda1(this));
                this.loadedAnimator.addListener(new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(Animator animator) {
                        Runnable runnable = runnable;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                this.loadedAnimator.setDuration(j);
                this.loadedAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                this.loadedAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loaded$1(ValueAnimator valueAnimator) {
            this.loadingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateWidth();
            invalidate();
            onLoadAnimation(this.loadingT);
        }

        public void set(CharSequence charSequence) {
            this.loaded = true;
            this.toTextView.setText(charSequence);
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            ValueAnimator valueAnimator = this.loadedAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.loadedAnimator = null;
            }
            this.loadingT = 1.0f;
            requestLayout();
            updateWidth();
            invalidate();
            onLoadAnimation(1.0f);
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
            this.loadingPath.reset();
            this.rect.set(0.0f, 0.0f, width, height);
            this.loadingPath.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
            canvas.clipPath(this.loadingPath);
            canvas.translate(-elapsedRealtime, 0.0f);
            this.shadePath.offset(elapsedRealtime, 0.0f, this.tempPath);
            canvas.drawPath(this.tempPath, this.loadingPaint);
            canvas.translate(elapsedRealtime, 0.0f);
            canvas.restore();
            if (this.showLoadingText && this.fromTextView != null) {
                canvas.save();
                this.rect.set(0.0f, 0.0f, width, height);
                canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
                canvas.translate((float) paddingHorizontal, 0.0f);
                canvas.saveLayerAlpha(this.rect, 20, 31);
                this.fromTextView.draw(canvas);
                canvas.restore();
                canvas.restore();
            }
            if (this.toTextView != null) {
                canvas.save();
                canvas.clipPath(this.inPath);
                canvas.translate((float) paddingHorizontal, 0.0f);
                canvas.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.toTextView.draw(canvas);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }
    }

    public static class LoadingTextView2 extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(6.0f);
        public static final int paddingVertical = AndroidUtilities.dp(1.5f);
        /* access modifiers changed from: private */
        public RectF fetchedPathRect = new RectF();
        private final TextView fromTextView;
        private final float gradientWidth;
        private final Path inPath;
        int lastWidth = 0;
        public boolean loaded = false;
        private ValueAnimator loadedAnimator = null;
        private final ValueAnimator loadingAnimator;
        private final Paint loadingPaint;
        private final Path loadingPath;
        private float loadingT = 0.0f;
        private final RectF rect;
        private float scaleT = 1.0f;
        private final Path shadePath;
        public boolean showLoadingText = true;
        private final long start = SystemClock.elapsedRealtime();
        private final Path tempPath;
        private final TextView toTextView;

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return false;
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public LoadingTextView2(Context context, CharSequence charSequence, boolean z, int i, int i2) {
            super(context);
            Context context2 = context;
            boolean z2 = z;
            int i3 = i2;
            new Path() {
                private boolean got = false;

                public void reset() {
                    super.reset();
                    this.got = false;
                }

                public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
                    if (!this.got) {
                        RectF access$2500 = LoadingTextView2.this.fetchedPathRect;
                        int i = LoadingTextView2.paddingHorizontal;
                        int i2 = LoadingTextView2.paddingVertical;
                        access$2500.set(f - ((float) i), f2 - ((float) i2), f3 + ((float) i), f4 + ((float) i2));
                        this.got = true;
                    }
                }
            };
            this.rect = new RectF();
            this.inPath = new Path();
            this.tempPath = new Path();
            this.loadingPath = new Path();
            this.shadePath = new Path();
            Paint paint = new Paint();
            this.loadingPaint = paint;
            float dp = (float) AndroidUtilities.dp(350.0f);
            this.gradientWidth = dp;
            int i4 = paddingHorizontal;
            int i5 = paddingVertical;
            setPadding(i4, i5, i4, i5);
            setClipChildren(false);
            setWillNotDraw(false);
            AnonymousClass1 r9 = new TextView(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, TranslateAlert.MOST_SPEC);
                }
            };
            this.fromTextView = r9;
            float f = (float) i;
            r9.setTextSize(0, f);
            r9.setTextColor(i3);
            r9.setText(charSequence);
            r9.setLines(0);
            r9.setMaxLines(0);
            r9.setSingleLine(false);
            r9.setEllipsize((TextUtils.TruncateAt) null);
            addView(r9);
            AnonymousClass2 r92 = new TextView(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, TranslateAlert.MOST_SPEC);
                }
            };
            this.toTextView = r92;
            r92.setTextSize(0, f);
            r92.setTextColor(i3);
            r92.setLines(0);
            r92.setMaxLines(0);
            r92.setSingleLine(false);
            r92.setEllipsize((TextUtils.TruncateAt) null);
            addView(r92);
            int color = Theme.getColor("dialogBackground");
            paint.setShader(new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color, Theme.getColor("dialogBackgroundGray"), color}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.loadingAnimator = ofFloat;
            if (z2) {
                this.scaleT = 0.0f;
            }
            ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView2$$ExternalSyntheticLambda1(this, z2));
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(boolean z, ValueAnimator valueAnimator) {
            invalidate();
            if (z) {
                boolean z2 = this.scaleT < 1.0f;
                this.scaleT = Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.start)) / 400.0f);
                if (z2) {
                    updateHeight();
                }
            }
        }

        public int innerHeight() {
            return (int) (((float) AndroidUtilities.lerp(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight(), this.loadingT)) * this.scaleT);
        }

        public int height() {
            return getPaddingTop() + innerHeight() + getPaddingBottom();
        }

        private void updateHeight() {
            ViewParent parent = getParent();
            if (parent instanceof TextBlocksLayout) {
                ((TextBlocksLayout) parent).updateHeight();
            }
        }

        public void loaded(CharSequence charSequence, final Runnable runnable) {
            this.loaded = true;
            this.toTextView.setText(charSequence);
            layout();
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            if (this.loadedAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.loadedAnimator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView2$$ExternalSyntheticLambda0(this));
                this.loadedAnimator.addListener(new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(Animator animator) {
                        Runnable runnable = runnable;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                this.loadedAnimator.setDuration(350);
                this.loadedAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                this.loadedAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loaded$1(ValueAnimator valueAnimator) {
            this.loadingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateHeight();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
            if (this.fromTextView.getMeasuredWidth() <= 0 || this.lastWidth != paddingLeft) {
                measureChild(this.fromTextView, paddingLeft);
                updateLoadingPath();
            }
            if (this.toTextView.getMeasuredWidth() <= 0 || this.lastWidth != paddingLeft) {
                measureChild(this.toTextView, paddingLeft);
            }
            this.lastWidth = paddingLeft;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(height(), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            layout(((i3 - i) - getPaddingLeft()) - getPaddingRight(), true);
        }

        private void layout(int i, boolean z) {
            if (this.lastWidth != i || z) {
                this.lastWidth = i;
                layout(i);
            }
        }

        private void layout(int i) {
            measureChild(this.fromTextView, i);
            layoutChild(this.fromTextView, i);
            updateLoadingPath();
            measureChild(this.toTextView, i);
            layoutChild(this.toTextView, i);
            updateHeight();
        }

        private void layout() {
            layout(this.lastWidth);
        }

        private void measureChild(View view, int i) {
            view.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), TranslateAlert.MOST_SPEC);
        }

        private void layoutChild(View view, int i) {
            view.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + i, getPaddingTop() + view.getMeasuredHeight());
        }

        private void updateLoadingPath() {
            boolean z;
            TextView textView = this.fromTextView;
            if (textView != null && textView.getMeasuredWidth() > 0) {
                this.loadingPath.reset();
                Layout layout = this.fromTextView.getLayout();
                if (layout != null) {
                    CharSequence text = layout.getText();
                    int lineCount = layout.getLineCount();
                    for (int i = 0; i < lineCount; i++) {
                        float lineLeft = layout.getLineLeft(i);
                        float lineRight = layout.getLineRight(i);
                        float min = Math.min(lineLeft, lineRight);
                        float max = Math.max(lineLeft, lineRight);
                        int lineStart = layout.getLineStart(i);
                        int lineEnd = layout.getLineEnd(i);
                        while (true) {
                            if (lineStart < lineEnd) {
                                char charAt = text.charAt(lineStart);
                                if (charAt != 10 && charAt != 9 && charAt != ' ') {
                                    z = true;
                                    break;
                                }
                                lineStart++;
                            } else {
                                z = false;
                                break;
                            }
                        }
                        if (z) {
                            RectF rectF = this.fetchedPathRect;
                            int i2 = paddingHorizontal;
                            int lineTop = layout.getLineTop(i);
                            int i3 = paddingVertical;
                            rectF.set(min - ((float) i2), (float) (lineTop - i3), max + ((float) i2), (float) (layout.getLineBottom(i) + i3));
                            this.loadingPath.addRoundRect(this.fetchedPathRect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        }
                    }
                }
            }
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
            int i = paddingHorizontal;
            int i2 = paddingVertical;
            canvas.translate((float) i, (float) i2);
            canvas.clipPath(this.loadingPath);
            canvas.translate((float) (-i), (float) (-i2));
            canvas.translate(-elapsedRealtime, 0.0f);
            this.shadePath.offset(elapsedRealtime, 0.0f, this.tempPath);
            canvas.drawPath(this.tempPath, this.loadingPaint);
            canvas.translate(elapsedRealtime, 0.0f);
            canvas.restore();
            if (this.showLoadingText && this.fromTextView != null) {
                canvas.save();
                this.rect.set(0.0f, 0.0f, width, height);
                canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
                canvas.translate((float) i, (float) i2);
                canvas.saveLayerAlpha(this.rect, 20, 31);
                this.fromTextView.draw(canvas);
                canvas.restore();
                canvas.restore();
            }
            if (this.toTextView != null) {
                canvas.save();
                canvas.clipPath(this.inPath);
                canvas.translate((float) i, (float) i2);
                canvas.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.toTextView.draw(canvas);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }
    }
}
