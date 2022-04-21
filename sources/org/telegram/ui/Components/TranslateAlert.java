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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    private FrameLayout.LayoutParams backLayout;
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
    private float epsilon;
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
    private boolean fromTranslateMoreView;
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
    private float openAnimationT;
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
    private boolean scrollViewScrollable;
    private ValueAnimator scrollerToBottom;
    private boolean scrolling;
    private ImageView subtitleArrowView;
    /* access modifiers changed from: private */
    public InlineLoadingTextView subtitleFromView;
    private FrameLayout.LayoutParams subtitleLayout;
    private TextView subtitleToView;
    private LinearLayout subtitleView;
    private CharSequence text;
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
    private Rect translateMoreRect;

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
    public void openAnimation(float t) {
        float f = 1.0f;
        float t2 = Math.min(Math.max(t, 0.0f), 1.0f);
        if (this.containerOpenAnimationT != t2) {
            this.containerOpenAnimationT = t2;
            this.titleView.setScaleX(AndroidUtilities.lerp(1.0f, 0.9473f, t2));
            this.titleView.setScaleY(AndroidUtilities.lerp(1.0f, 0.9473f, t2));
            this.titleLayout.setMargins(AndroidUtilities.dp((float) AndroidUtilities.lerp(22, 72, t2)), AndroidUtilities.dp((float) AndroidUtilities.lerp(22, 8, t2)), this.titleLayout.rightMargin, this.titleLayout.bottomMargin);
            this.titleView.setLayoutParams(this.titleLayout);
            this.subtitleLayout.setMargins(AndroidUtilities.dp((float) AndroidUtilities.lerp(22, 72, t2)) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp((float) AndroidUtilities.lerp(47, 30, t2)) - LoadingTextView2.paddingVertical, this.subtitleLayout.rightMargin, this.subtitleLayout.bottomMargin);
            this.subtitleView.setLayoutParams(this.subtitleLayout);
            this.backButton.setAlpha(t2);
            this.backButton.setScaleX((t2 * 0.25f) + 0.75f);
            this.backButton.setScaleY((0.25f * t2) + 0.75f);
            this.backButton.setClickable(t2 > 0.5f);
            FrameLayout frameLayout = this.headerShadowView;
            if (this.scrollView.getScrollY() <= 0) {
                f = t2;
            }
            frameLayout.setAlpha(f);
            this.headerLayout.height = AndroidUtilities.lerp(AndroidUtilities.dp(70.0f), AndroidUtilities.dp(56.0f), t2);
            this.header.setLayoutParams(this.headerLayout);
            FrameLayout.LayoutParams layoutParams = this.scrollViewLayout;
            layoutParams.setMargins(layoutParams.leftMargin, AndroidUtilities.lerp(AndroidUtilities.dp(70.0f), AndroidUtilities.dp(56.0f), t2), this.scrollViewLayout.rightMargin, this.scrollViewLayout.bottomMargin);
            this.scrollView.setLayoutParams(this.scrollViewLayout);
        }
    }

    /* access modifiers changed from: private */
    public void openAnimationTo(float to, boolean priority) {
        openAnimationTo(to, priority, (Runnable) null);
    }

    private void openAnimationTo(float to, boolean priority, final Runnable onAnimationEnd) {
        if (!this.openAnimationToAnimatorPriority || priority) {
            this.openAnimationToAnimatorPriority = priority;
            float to2 = Math.min(Math.max(to, 0.0f), 1.0f);
            ValueAnimator valueAnimator = this.openAnimationToAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.containerOpenAnimationT, to2});
            this.openAnimationToAnimator = ofFloat;
            ofFloat.addUpdateListener(new TranslateAlert$$ExternalSyntheticLambda0(this));
            this.openAnimationToAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = TranslateAlert.this.openAnimationToAnimatorPriority = false;
                    Runnable runnable = onAnimationEnd;
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
            if (((double) to2) >= 0.5d && this.blockIndex <= 1) {
                fetchNext();
            }
        }
    }

    /* renamed from: lambda$openAnimationTo$0$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4491xab02aaef(ValueAnimator a) {
        openAnimation(((Float) a.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public int minHeight() {
        return minHeight(false);
    }

    private int minHeight(boolean full) {
        TextBlocksLayout textBlocksLayout = this.textsView;
        int textsViewHeight = textBlocksLayout == null ? 0 : textBlocksLayout.getMeasuredHeight();
        int height = AndroidUtilities.dp(147.0f) + textsViewHeight;
        if (this.firstMinHeight < 0 && textsViewHeight > 0) {
            this.firstMinHeight = height;
        }
        if (this.firstMinHeight <= 0 || this.textBlocks.size() <= 1 || full) {
            return height;
        }
        return this.firstMinHeight;
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

    public TranslateAlert(BaseFragment fragment2, Context context, String fromLanguage2, String toLanguage2, CharSequence text2, boolean noforwards2, OnLinkPress onLinkPress2, Runnable onDismiss2) {
        this(fragment2, context, -1, (TLRPC.InputPeer) null, -1, fromLanguage2, toLanguage2, text2, noforwards2, onLinkPress2, onDismiss2);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TranslateAlert(org.telegram.ui.ActionBar.BaseFragment r39, android.content.Context r40, int r41, org.telegram.tgnet.TLRPC.InputPeer r42, int r43, java.lang.String r44, java.lang.String r45, java.lang.CharSequence r46, boolean r47, org.telegram.ui.Components.TranslateAlert.OnLinkPress r48, java.lang.Runnable r49) {
        /*
            r38 = this;
            r7 = r38
            r8 = r40
            r9 = r42
            r10 = r44
            r11 = r45
            r12 = r46
            r13 = r47
            r0 = 2131689509(0x7f0var_, float:1.9008035E38)
            r7.<init>(r8, r0)
            r0 = 0
            r7.scrollViewScrollable = r0
            r7.blockIndex = r0
            r14 = 0
            r7.containerOpenAnimationT = r14
            r7.openAnimationT = r14
            r1 = 981668463(0x3a83126f, float:0.001)
            r7.epsilon = r1
            r7.openAnimationToAnimatorPriority = r0
            r1 = 0
            r7.openAnimationToAnimator = r1
            r15 = -1
            r7.firstMinHeight = r15
            r6 = 1
            r7.allowScroll = r6
            r7.scrollerToBottom = r1
            r7.onLinkPress = r1
            r7.onDismiss = r1
            r7.fromScrollY = r14
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r7.containerRect = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r7.textRect = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r7.translateMoreRect = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r7.buttonRect = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r7.backRect = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r7.scrollRect = r2
            r7.fromY = r14
            r7.pressedOutside = r0
            r7.maybeScrolling = r0
            r7.scrolling = r0
            r7.fromScrollRect = r0
            r7.fromTranslateMoreView = r0
            r7.fromScrollViewY = r14
            r7.allTexts = r1
            r7.openingT = r14
            org.telegram.ui.Components.TranslateAlert$6 r2 = new org.telegram.ui.Components.TranslateAlert$6
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2.<init>(r3)
            r7.backDrawable = r2
            r7.dismissed = r0
            r2 = 1062836634(0x3var_a, float:0.85)
            r7.heightMaxPercent = r2
            r7.fastHide = r0
            r7.openingAnimatorPriority = r0
            r7.loading = r0
            r7.loaded = r0
            java.lang.String r2 = "und"
            if (r9 == 0) goto L_0x00a0
            if (r10 == 0) goto L_0x0097
            boolean r3 = r10.equals(r2)
            if (r3 == 0) goto L_0x0097
            goto L_0x0098
        L_0x0097:
            r1 = r10
        L_0x0098:
            r5 = r41
            r4 = r43
            translateText(r5, r9, r4, r1, r11)
            goto L_0x00a4
        L_0x00a0:
            r5 = r41
            r4 = r43
        L_0x00a4:
            r3 = r48
            r7.onLinkPress = r3
            r7.noforwards = r13
            r1 = r39
            r7.fragment = r1
            if (r10 == 0) goto L_0x00b9
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x00b9
            java.lang.String r2 = "auto"
            goto L_0x00ba
        L_0x00b9:
            r2 = r10
        L_0x00ba:
            r7.fromLanguage = r2
            r7.toLanguage = r11
            r7.text = r12
            r2 = 1024(0x400, float:1.435E-42)
            java.util.ArrayList r2 = r7.cutInBlocks(r12, r2)
            r7.textBlocks = r2
            r2 = r49
            r7.onDismiss = r2
            int r15 = android.os.Build.VERSION.SDK_INT
            r14 = 21
            r6 = 30
            if (r15 < r6) goto L_0x00df
            android.view.Window r15 = r38.getWindow()
            r6 = -2147483392(0xfffffffvar_, float:-3.59E-43)
            r15.addFlags(r6)
            goto L_0x00ed
        L_0x00df:
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r14) goto L_0x00ed
            android.view.Window r6 = r38.getWindow()
            r15 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r6.addFlags(r15)
        L_0x00ed:
            if (r13 == 0) goto L_0x00f8
            android.view.Window r6 = r38.getWindow()
            r15 = 8192(0x2000, float:1.14794E-41)
            r6.addFlags(r15)
        L_0x00f8:
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r8)
            r7.contentView = r6
            android.graphics.drawable.ColorDrawable r15 = r7.backDrawable
            r6.setBackground(r15)
            android.widget.FrameLayout r6 = r7.contentView
            r6.setClipChildren(r0)
            android.widget.FrameLayout r6 = r7.contentView
            r6.setClipToPadding(r0)
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r14) goto L_0x012d
            android.widget.FrameLayout r6 = r7.contentView
            r14 = 1
            r6.setFitsSystemWindows(r14)
            int r6 = android.os.Build.VERSION.SDK_INT
            r14 = 30
            if (r6 < r14) goto L_0x0126
            android.widget.FrameLayout r6 = r7.contentView
            r14 = 1792(0x700, float:2.511E-42)
            r6.setSystemUiVisibility(r14)
            goto L_0x012d
        L_0x0126:
            android.widget.FrameLayout r6 = r7.contentView
            r14 = 1280(0x500, float:1.794E-42)
            r6.setSystemUiVisibility(r14)
        L_0x012d:
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>()
            r14 = r6
            java.lang.String r6 = "dialogBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r14.setColor(r6)
            r6 = 1073741824(0x40000000, float:2.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r15 = -1087834685(0xffffffffbvar_f5c3, float:-0.66)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r0 = 503316480(0x1e000000, float:6.7762636E-21)
            r1 = 0
            r14.setShadowLayer(r6, r1, r15, r0)
            org.telegram.ui.Components.TranslateAlert$2 r0 = new org.telegram.ui.Components.TranslateAlert$2
            r0.<init>(r8, r14)
            r7.container = r0
            r1 = 0
            r0.setWillNotDraw(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.header = r0
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.titleView = r0
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0176
            android.widget.TextView r1 = r7.titleView
            int r1 = r1.getWidth()
            float r1 = (float) r1
            goto L_0x0177
        L_0x0176:
            r1 = 0
        L_0x0177:
            r0.setPivotX(r1)
            android.widget.TextView r0 = r7.titleView
            r1 = 0
            r0.setPivotY(r1)
            android.widget.TextView r0 = r7.titleView
            r6 = 1
            r0.setLines(r6)
            android.widget.TextView r0 = r7.titleView
            r1 = 2131624568(0x7f0e0278, float:1.887632E38)
            java.lang.String r15 = "AutomaticTranslation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r7.titleView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x019c
            r1 = 5
            goto L_0x019d
        L_0x019c:
            r1 = 3
        L_0x019d:
            r0.setGravity(r1)
            android.widget.TextView r0 = r7.titleView
            java.lang.String r20 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r7.titleView
            java.lang.String r21 = "dialogTextBlack"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r21)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r7.titleView
            r1 = 1100480512(0x41980000, float:19.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r6 = 0
            r0.setTextSize(r6, r1)
            android.widget.FrameLayout r0 = r7.header
            android.widget.TextView r1 = r7.titleView
            r23 = -1
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 55
            r26 = 1102053376(0x41b00000, float:22.0)
            r27 = 1102053376(0x41b00000, float:22.0)
            r28 = 1102053376(0x41b00000, float:22.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r7.titleLayout = r6
            r0.addView(r1, r6)
            android.widget.TextView r0 = r7.titleView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda7
            r1.<init>(r7)
            r0.post(r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r8)
            r7.subtitleView = r0
            r1 = 0
            r0.setOrientation(r1)
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 17
            if (r0 < r6) goto L_0x0200
            android.widget.LinearLayout r0 = r7.subtitleView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r0.setLayoutDirection(r1)
        L_0x0200:
            android.widget.LinearLayout r0 = r7.subtitleView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0208
            r1 = 5
            goto L_0x0209
        L_0x0208:
            r1 = 3
        L_0x0209:
            r0.setGravity(r1)
            java.lang.String r1 = r7.languageName(r10)
            org.telegram.ui.Components.TranslateAlert$3 r0 = new org.telegram.ui.Components.TranslateAlert$3
            if (r1 != 0) goto L_0x0219
            java.lang.String r23 = r7.languageName(r11)
            goto L_0x021b
        L_0x0219:
            r23 = r1
        L_0x021b:
            r15 = 1096810496(0x41600000, float:14.0)
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r15)
            java.lang.String r26 = "player_actionBarSubtitle"
            int r27 = org.telegram.ui.ActionBar.Theme.getColor(r26)
            r30 = r1
            r1 = r0
            r2 = r38
            r3 = r40
            r4 = r23
            r5 = r25
            r15 = 1
            r6 = r27
            r1.<init>(r3, r4, r5, r6)
            r7.subtitleFromView = r0
            r1 = 0
            r0.showLoadingText = r1
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.subtitleArrowView = r0
            r1 = 2131166102(0x7var_, float:1.794644E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r7.subtitleArrowView
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r26)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0265
            android.widget.ImageView r0 = r7.subtitleArrowView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r0.setScaleX(r1)
        L_0x0265:
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.subtitleToView = r0
            r0.setLines(r15)
            android.widget.TextView r0 = r7.subtitleToView
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r26)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r7.subtitleToView
            r1 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r2
            r2 = 0
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r7.subtitleToView
            java.lang.String r1 = r7.languageName(r11)
            r0.setText(r1)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            r1 = 16
            r2 = -2
            if (r0 == 0) goto L_0x02d1
            android.widget.LinearLayout r0 = r7.subtitleView
            int r3 = org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.paddingHorizontal
            r4 = 0
            r0.setPadding(r3, r4, r4, r4)
            android.widget.LinearLayout r0 = r7.subtitleView
            android.widget.TextView r3 = r7.subtitleToView
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r2, (int) r1)
            r0.addView(r3, r1)
            android.widget.LinearLayout r0 = r7.subtitleView
            android.widget.ImageView r1 = r7.subtitleArrowView
            r31 = -2
            r32 = -2
            r33 = 16
            r34 = 3
            r35 = 1
            r36 = 0
            r37 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r31, (int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37)
            r0.addView(r1, r3)
            android.widget.LinearLayout r0 = r7.subtitleView
            org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView r1 = r7.subtitleFromView
            r34 = 2
            r35 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r31, (int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37)
            r0.addView(r1, r3)
            goto L_0x030c
        L_0x02d1:
            android.widget.LinearLayout r0 = r7.subtitleView
            int r3 = org.telegram.ui.Components.TranslateAlert.InlineLoadingTextView.paddingHorizontal
            r4 = 0
            r0.setPadding(r4, r4, r3, r4)
            android.widget.LinearLayout r0 = r7.subtitleView
            org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView r3 = r7.subtitleFromView
            r31 = -2
            r32 = -2
            r33 = 16
            r34 = 0
            r35 = 0
            r36 = 2
            r37 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r31, (int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37)
            r0.addView(r3, r4)
            android.widget.LinearLayout r0 = r7.subtitleView
            android.widget.ImageView r3 = r7.subtitleArrowView
            r35 = 1
            r36 = 3
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r31, (int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37)
            r0.addView(r3, r4)
            android.widget.LinearLayout r0 = r7.subtitleView
            android.widget.TextView r3 = r7.subtitleToView
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r2, (int) r2, (int) r1)
            r0.addView(r3, r1)
        L_0x030c:
            r1 = r30
            if (r1 == 0) goto L_0x0315
            org.telegram.ui.Components.TranslateAlert$InlineLoadingTextView r0 = r7.subtitleFromView
            r0.set(r1)
        L_0x0315:
            android.widget.FrameLayout r0 = r7.header
            android.widget.LinearLayout r3 = r7.subtitleView
            r31 = -1
            r32 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0324
            r18 = 5
            goto L_0x0326
        L_0x0324:
            r18 = 3
        L_0x0326:
            r33 = r18 | 48
            int r4 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingHorizontal
            float r4 = (float) r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r5
            r5 = 1102053376(0x41b00000, float:22.0)
            float r34 = r5 - r4
            r4 = 1111228416(0x423CLASSNAME, float:47.0)
            int r6 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingVertical
            float r6 = (float) r6
            float r18 = org.telegram.messenger.AndroidUtilities.density
            float r6 = r6 / r18
            float r35 = r4 - r6
            int r4 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingHorizontal
            float r4 = (float) r4
            float r6 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r6
            float r36 = r5 - r4
            r37 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r7.subtitleLayout = r4
            r0.addView(r3, r4)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.backButton = r0
            r3 = 2131165503(0x7var_f, float:1.7945225E38)
            r0.setImageResource(r3)
            android.widget.ImageView r0 = r7.backButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r21)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r6)
            r0.setColorFilter(r3)
            android.widget.ImageView r0 = r7.backButton
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.FIT_CENTER
            r0.setScaleType(r3)
            android.widget.ImageView r0 = r7.backButton
            r3 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2 = 0
            r0.setPadding(r4, r2, r6, r2)
            android.widget.ImageView r0 = r7.backButton
            java.lang.String r4 = "dialogButtonSelector"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4)
            r0.setBackground(r4)
            android.widget.ImageView r0 = r7.backButton
            r0.setClickable(r2)
            android.widget.ImageView r0 = r7.backButton
            r2 = 0
            r0.setAlpha(r2)
            android.widget.ImageView r0 = r7.backButton
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3 r2 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            android.widget.FrameLayout r0 = r7.header
            android.widget.ImageView r2 = r7.backButton
            r4 = 56
            r6 = 3
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r6)
            r7.backLayout = r4
            r0.addView(r2, r4)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.headerShadowView = r0
            java.lang.String r2 = "dialogShadowLine"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r4)
            android.widget.FrameLayout r0 = r7.headerShadowView
            r4 = 0
            r0.setAlpha(r4)
            android.widget.FrameLayout r0 = r7.header
            android.widget.FrameLayout r4 = r7.headerShadowView
            r6 = 87
            r5 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r15, (int) r6)
            r0.addView(r4, r6)
            android.widget.FrameLayout r0 = r7.header
            r4 = 0
            r0.setClipChildren(r4)
            android.widget.FrameLayout r0 = r7.container
            android.widget.FrameLayout r4 = r7.header
            r6 = 70
            r3 = 55
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r6, (int) r3)
            r7.headerLayout = r3
            r0.addView(r4, r3)
            org.telegram.ui.Components.TranslateAlert$4 r0 = new org.telegram.ui.Components.TranslateAlert$4
            r0.<init>(r8)
            r7.scrollView = r0
            r0.setClipChildren(r15)
            org.telegram.ui.Components.TranslateAlert$5 r0 = new org.telegram.ui.Components.TranslateAlert$5
            r0.<init>(r8)
            r7.allTextsView = r0
            org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r0 = new org.telegram.ui.Components.LinkSpanDrawable$LinkCollector
            android.widget.TextView r3 = r7.allTextsView
            r0.<init>(r3)
            r7.links = r0
            android.widget.TextView r0 = r7.allTextsView
            r3 = 0
            r0.setTextColor(r3)
            android.widget.TextView r0 = r7.allTextsView
            r3 = 1098907648(0x41800000, float:16.0)
            r0.setTextSize(r15, r3)
            android.widget.TextView r0 = r7.allTextsView
            r3 = r13 ^ 1
            r0.setTextIsSelectable(r3)
            android.widget.TextView r0 = r7.allTextsView
            java.lang.String r3 = "chat_inTextSelectionHighlight"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setHighlightColor(r3)
            java.lang.String r0 = "chat_TextSelectionCursor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0459 }
            r4 = 29
            if (r0 < r4) goto L_0x045a
            android.widget.TextView r0 = r7.allTextsView     // Catch:{ Exception -> 0x0459 }
            android.graphics.drawable.Drawable r0 = r0.getTextSelectHandleLeft()     // Catch:{ Exception -> 0x0459 }
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x0459 }
            r0.setColorFilter(r3, r4)     // Catch:{ Exception -> 0x0459 }
            android.widget.TextView r4 = r7.allTextsView     // Catch:{ Exception -> 0x0459 }
            r4.setTextSelectHandleLeft(r0)     // Catch:{ Exception -> 0x0459 }
            android.widget.TextView r4 = r7.allTextsView     // Catch:{ Exception -> 0x0459 }
            android.graphics.drawable.Drawable r4 = r4.getTextSelectHandleRight()     // Catch:{ Exception -> 0x0459 }
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x0459 }
            r4.setColorFilter(r3, r5)     // Catch:{ Exception -> 0x0459 }
            android.widget.TextView r5 = r7.allTextsView     // Catch:{ Exception -> 0x0459 }
            r5.setTextSelectHandleRight(r4)     // Catch:{ Exception -> 0x0459 }
            goto L_0x045a
        L_0x0459:
            r0 = move-exception
        L_0x045a:
            android.widget.TextView r0 = r7.allTextsView
            android.text.method.LinkMovementMethod r4 = new android.text.method.LinkMovementMethod
            r4.<init>()
            r0.setMovementMethod(r4)
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r0 = new org.telegram.ui.Components.TranslateAlert$TextBlocksLayout
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r21)
            android.widget.TextView r6 = r7.allTextsView
            r0.<init>(r8, r4, r5, r6)
            r7.textsView = r0
            r4 = 1102053376(0x41b00000, float:22.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingHorizontal
            int r5 = r5 - r6
            r6 = 1094713344(0x41400000, float:12.0)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r19 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingVertical
            int r15 = r17 - r19
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r17 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingHorizontal
            int r4 = r4 - r17
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r17 = org.telegram.ui.Components.TranslateAlert.LoadingTextView2.paddingVertical
            int r6 = r6 - r17
            r0.setPadding(r5, r15, r4, r6)
            java.util.ArrayList<java.lang.CharSequence> r0 = r7.textBlocks
            java.util.Iterator r0 = r0.iterator()
        L_0x04a5:
            boolean r4 = r0.hasNext()
            if (r4 == 0) goto L_0x04b7
            java.lang.Object r4 = r0.next()
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r5 = r7.textsView
            r5.addBlock(r4)
            goto L_0x04a5
        L_0x04b7:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.textsContainerView = r0
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r4 = r7.textsView
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r0.addView(r4, r5)
            androidx.core.widget.NestedScrollView r0 = r7.scrollView
            android.widget.FrameLayout r4 = r7.textsContainerView
            r5 = 1065353216(0x3var_, float:1.0)
            r15 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r15, (float) r5)
            r0.addView((android.view.View) r4, (android.view.ViewGroup.LayoutParams) r5)
            android.widget.FrameLayout r0 = r7.container
            androidx.core.widget.NestedScrollView r4 = r7.scrollView
            r23 = -1
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 119(0x77, float:1.67E-43)
            r26 = 0
            r27 = 1116471296(0x428CLASSNAME, float:70.0)
            r28 = 0
            r29 = 1117913088(0x42a20000, float:81.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r7.scrollViewLayout = r5
            r0.addView(r4, r5)
            r38.fetchNext()
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.buttonShadowView = r0
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r2)
            android.widget.FrameLayout r0 = r7.container
            android.widget.FrameLayout r2 = r7.buttonShadowView
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 87
            r27 = 0
            r29 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r2, r4)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.buttonTextView = r0
            r2 = 1
            r0.setLines(r2)
            android.widget.TextView r0 = r7.buttonTextView
            r0.setSingleLine(r2)
            android.widget.TextView r0 = r7.buttonTextView
            r0.setGravity(r2)
            android.widget.TextView r0 = r7.buttonTextView
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r2)
            android.widget.TextView r0 = r7.buttonTextView
            r2 = 17
            r0.setGravity(r2)
            android.widget.TextView r0 = r7.buttonTextView
            java.lang.String r2 = "featuredStickers_buttonText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r7.buttonTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r7.buttonTextView
            r2 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r0.setTextSize(r4, r2)
            android.widget.TextView r0 = r7.buttonTextView
            r2 = 2131625078(0x7f0e0476, float:1.8877354E38)
            java.lang.String r4 = "CloseTranslation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setText(r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.buttonView = r0
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.String r4 = "featuredStickers_addButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r5 = "featuredStickers_addButtonPressed"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r2, r4, r5)
            r0.setBackground(r2)
            android.widget.FrameLayout r0 = r7.buttonView
            android.widget.TextView r2 = r7.buttonTextView
            r0.addView(r2)
            android.widget.FrameLayout r0 = r7.buttonView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4
            r2.<init>(r7)
            r0.setOnClickListener(r2)
            android.widget.FrameLayout r0 = r7.container
            android.widget.FrameLayout r2 = r7.buttonView
            r19 = -1
            r20 = 1111490560(0x42400000, float:48.0)
            r21 = 80
            r22 = 1098907648(0x41800000, float:16.0)
            r23 = 1098907648(0x41800000, float:16.0)
            r24 = 1098907648(0x41800000, float:16.0)
            r25 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r2, r4)
            android.widget.FrameLayout r0 = r7.contentView
            android.widget.FrameLayout r2 = r7.container
            r4 = 81
            r5 = -2
            r6 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r5, (int) r4)
            r0.addView(r2, r4)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r8)
            r7.bulletinContainer = r0
            android.widget.FrameLayout r2 = r7.contentView
            r15 = -1
            r16 = -1082130432(0xffffffffbvar_, float:-1.0)
            r17 = 119(0x77, float:1.67E-43)
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1117913088(0x42a20000, float:81.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r2.addView(r0, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, int, org.telegram.tgnet.TLRPC$InputPeer, int, java.lang.String, java.lang.String, java.lang.CharSequence, boolean, org.telegram.ui.Components.TranslateAlert$OnLinkPress, java.lang.Runnable):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4488lambda$new$1$orgtelegramuiComponentsTranslateAlert() {
        this.titleView.setPivotX(LocaleController.isRTL ? (float) this.titleView.getWidth() : 0.0f);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4489lambda$new$2$orgtelegramuiComponentsTranslateAlert(View e) {
        dismiss();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4490lambda$new$3$orgtelegramuiComponentsTranslateAlert(View e) {
        dismiss();
    }

    public void showDim(boolean enable) {
        this.contentView.setBackground(enable ? this.backDrawable : null);
    }

    private boolean scrollAtBottom() {
        NestedScrollView nestedScrollView = this.scrollView;
        int bottom = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1).getBottom();
        LoadingTextView2 lastUnloadedBlock = this.textsView.getFirstUnloadedBlock();
        if (lastUnloadedBlock != null) {
            bottom = lastUnloadedBlock.getTop();
        }
        if (bottom - (this.scrollView.getHeight() + this.scrollView.getScrollY()) <= this.textsContainerView.getPaddingBottom()) {
            return true;
        }
        return false;
    }

    private void setScrollY(float t) {
        openAnimation(t);
        float max = Math.max(Math.min(t + 1.0f, 1.0f), 0.0f);
        this.openingT = max;
        this.backDrawable.setAlpha((int) (max * 51.0f));
        this.container.invalidate();
        this.bulletinContainer.setTranslationY((1.0f - this.openingT) * Math.min((float) minHeight(), ((float) AndroidUtilities.displayMetrics.heightPixels) * this.heightMaxPercent));
    }

    private void scrollYTo(float t) {
        scrollYTo(t, (Runnable) null);
    }

    private void scrollYTo(float t, Runnable onAnimationEnd) {
        openAnimationTo(t, false, onAnimationEnd);
        openTo(1.0f + t, false);
    }

    private float getScrollY() {
        return Math.max(Math.min(this.containerOpenAnimationT - (1.0f - this.openingT), 1.0f), 0.0f);
    }

    private boolean hasSelection() {
        return this.allTextsView.hasSelection();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        float f;
        ClickableSpan[] linkSpans;
        try {
            float x = event.getX();
            float y = event.getY();
            this.container.getGlobalVisibleRect(this.containerRect);
            if (!this.containerRect.contains((int) x, (int) y)) {
                if (event.getAction() == 0) {
                    this.pressedOutside = true;
                    return true;
                } else if (event.getAction() == 1 && this.pressedOutside) {
                    this.pressedOutside = false;
                    dismiss();
                    return true;
                }
            }
            try {
                this.allTextsView.getGlobalVisibleRect(this.textRect);
                if (this.textRect.contains((int) x, (int) y) && !this.maybeScrolling) {
                    Layout allTextsLayout = this.allTextsView.getLayout();
                    int tx = (int) ((x - ((float) this.allTextsView.getLeft())) - ((float) this.container.getLeft()));
                    int ty = (int) ((((y - ((float) this.allTextsView.getTop())) - ((float) this.container.getTop())) - ((float) this.scrollView.getTop())) + ((float) this.scrollView.getScrollY()));
                    int line = allTextsLayout.getLineForVertical(ty);
                    int off = allTextsLayout.getOffsetForHorizontal(line, (float) tx);
                    float left = allTextsLayout.getLineLeft(line);
                    if ((this.allTexts instanceof Spannable) && left <= ((float) tx) && allTextsLayout.getLineWidth(line) + left >= ((float) tx) && (linkSpans = (ClickableSpan[]) this.allTexts.getSpans(off, off, ClickableSpan.class)) != null && linkSpans.length >= 1) {
                        if (event.getAction() == 1 && this.pressedLink.getSpan() == linkSpans[0]) {
                            ((ClickableSpan) this.pressedLink.getSpan()).onClick(this.allTextsView);
                            LinkSpanDrawable.LinkCollector linkCollector = this.links;
                            if (linkCollector != null) {
                                linkCollector.removeLink(this.pressedLink);
                            }
                            this.pressedLink = null;
                            this.allTextsView.setTextIsSelectable(!this.noforwards);
                        } else if (event.getAction() == 0) {
                            LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(linkSpans[0], this.fragment.getResourceProvider(), (float) tx, (float) ty, false);
                            this.pressedLink = linkSpanDrawable;
                            LinkSpanDrawable.LinkCollector linkCollector2 = this.links;
                            if (linkCollector2 != null) {
                                linkCollector2.addLink(linkSpanDrawable);
                            }
                            LinkPath path = this.pressedLink.obtainNewPath();
                            int start = this.allTexts.getSpanStart(this.pressedLink.getSpan());
                            int end = this.allTexts.getSpanEnd(this.pressedLink.getSpan());
                            path.setCurrentLayout(allTextsLayout, start, 0.0f);
                            allTextsLayout.getSelectionPath(start, end, path);
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.scrollView.getGlobalVisibleRect(this.scrollRect);
            this.backButton.getGlobalVisibleRect(this.backRect);
            this.buttonView.getGlobalVisibleRect(this.buttonRect);
            if (this.pressedLink == null && !hasSelection()) {
                if (!this.backRect.contains((int) x, (int) y) && !this.buttonRect.contains((int) x, (int) y) && event.getAction() == 0) {
                    this.fromScrollRect = this.scrollRect.contains((int) x, (int) y) && (this.containerOpenAnimationT > 0.0f || !canExpand());
                    this.maybeScrolling = true;
                    this.scrolling = this.scrollRect.contains((int) x, (int) y) && this.textsView.getBlocksCount() > 0 && !this.textsView.getBlockAt(0).loaded;
                    this.fromY = y;
                    this.fromScrollY = getScrollY();
                    this.fromScrollViewY = (float) this.scrollView.getScrollY();
                    super.dispatchTouchEvent(event);
                    return true;
                } else if (this.maybeScrolling && (event.getAction() == 2 || event.getAction() == 1)) {
                    float dy = this.fromY - y;
                    if (this.fromScrollRect) {
                        dy = -Math.max(0.0f, (-(this.fromScrollViewY + ((float) AndroidUtilities.dp(48.0f)))) - dy);
                        if (dy < 0.0f) {
                            this.scrolling = true;
                            this.allTextsView.setTextIsSelectable(false);
                        }
                    } else if (Math.abs(dy) > ((float) AndroidUtilities.dp(4.0f)) && !this.fromScrollRect) {
                        this.scrolling = true;
                        this.allTextsView.setTextIsSelectable(false);
                        this.scrollView.stopNestedScroll();
                        this.allowScroll = false;
                    }
                    float fullHeight = (float) AndroidUtilities.displayMetrics.heightPixels;
                    float minHeight = Math.min((float) minHeight(), this.heightMaxPercent * fullHeight);
                    float f2 = -1.0f;
                    float scrollYPx = ((1.0f - (-Math.min(Math.max(this.fromScrollY, -1.0f), 0.0f))) * minHeight) + ((fullHeight - minHeight) * Math.min(1.0f, Math.max(this.fromScrollY, 0.0f))) + dy;
                    float scrollY = scrollYPx > minHeight ? (scrollYPx - minHeight) / (fullHeight - minHeight) : -(1.0f - (scrollYPx / minHeight));
                    if (!canExpand()) {
                        scrollY = Math.min(scrollY, 0.0f);
                    }
                    updateCanExpand();
                    if (this.scrolling) {
                        setScrollY(scrollY);
                        if (event.getAction() == 1) {
                            this.scrolling = false;
                            this.allTextsView.setTextIsSelectable(!this.noforwards);
                            this.maybeScrolling = false;
                            this.allowScroll = true;
                            if (Math.abs(dy) > ((float) AndroidUtilities.dp(16.0f))) {
                                float round = (float) Math.round(this.fromScrollY);
                                float f3 = this.fromScrollY;
                                if (scrollY > f3) {
                                    f2 = 1.0f;
                                }
                                f = round + (f2 * ((float) Math.ceil((double) Math.abs(f3 - scrollY))));
                            } else {
                                f = (float) Math.round(this.fromScrollY);
                            }
                            scrollYTo(f, new TranslateAlert$$ExternalSyntheticLambda5(this));
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
            return super.dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return super.dispatchTouchEvent(event);
        }
    }

    /* renamed from: lambda$dispatchTouchEvent$4$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4484x59187c0d() {
        this.contentView.post(new TranslateAlert$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.contentView.setPadding(0, 0, 0, 0);
        setContentView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
        Window window = getWindow();
        window.setWindowAnimations(NUM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        params.flags |= 131072;
        if (Build.VERSION.SDK_INT >= 21) {
            params.flags |= -NUM;
        }
        params.flags |= 256;
        params.height = -1;
        window.setAttributes(params);
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

    private void openTo(float t, boolean priority) {
        openTo(t, priority, false);
    }

    private void openTo(float t) {
        openTo(t, false);
    }

    private void openTo(float t, boolean priority, final boolean setAfter) {
        Runnable runnable;
        final float T = Math.min(Math.max(t, 0.0f), 1.0f);
        if (!this.openingAnimatorPriority || priority) {
            this.openingAnimatorPriority = priority;
            ValueAnimator valueAnimator = this.openingAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.openingAnimator = ValueAnimator.ofFloat(new float[]{this.openingT, T});
            this.backDrawable.setAlpha((int) (this.openingT * 51.0f));
            this.openingAnimator.addUpdateListener(new TranslateAlert$$ExternalSyntheticLambda2(this));
            if (T <= 0.0f && (runnable = this.onDismiss) != null) {
                runnable.run();
            }
            this.openingAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (T <= 0.0f) {
                        TranslateAlert.this.dismissInternal();
                    } else if (setAfter) {
                        TranslateAlert.this.allTextsView.setTextIsSelectable(!TranslateAlert.this.noforwards);
                        TranslateAlert.this.allTextsView.invalidate();
                        TranslateAlert.this.scrollView.stopNestedScroll();
                        TranslateAlert.this.openAnimation(T - 1.0f);
                    }
                    boolean unused = TranslateAlert.this.openingAnimatorPriority = false;
                }
            });
            this.openingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.openingAnimator.setDuration((long) (Math.abs(this.openingT - T) * ((float) (this.fastHide ? 200 : 380))));
            this.openingAnimator.setStartDelay(setAfter ? 60 : 0);
            this.openingAnimator.start();
        }
    }

    /* renamed from: lambda$openTo$5$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4492lambda$openTo$5$orgtelegramuiComponentsTranslateAlert(ValueAnimator a) {
        this.openingT = ((Float) a.getAnimatedValue()).floatValue();
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

    public String languageName(String locale) {
        if (locale == null || locale.equals("und") || locale.equals("auto")) {
            return null;
        }
        LocaleController.LocaleInfo thisLanguageInfo = LocaleController.getInstance().getBuiltinLanguageByPlural(locale);
        LocaleController.LocaleInfo currentLanguageInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        if (thisLanguageInfo == null) {
            return null;
        }
        if (currentLanguageInfo != null && "en".equals(currentLanguageInfo.pluralLangCode)) {
            return thisLanguageInfo.nameEnglish;
        }
        return thisLanguageInfo.name;
    }

    public void updateSourceLanguage() {
        if (languageName(this.fromLanguage) != null) {
            this.subtitleView.setAlpha(1.0f);
            if (!this.subtitleFromView.loaded) {
                this.subtitleFromView.loaded(languageName(this.fromLanguage));
            }
        } else if (this.loaded) {
            this.subtitleView.animate().alpha(0.0f).setDuration(150).start();
        }
    }

    private ArrayList<CharSequence> cutInBlocks(CharSequence full, int maxBlockSize) {
        ArrayList<CharSequence> blocks = new ArrayList<>();
        if (full == null) {
            return blocks;
        }
        while (full.length() > maxBlockSize) {
            String maxBlockStr = full.subSequence(0, maxBlockSize).toString();
            int n = -1;
            if (-1 == -1) {
                n = maxBlockStr.lastIndexOf("\n\n");
            }
            if (n == -1) {
                n = maxBlockStr.lastIndexOf("\n");
            }
            if (n == -1) {
                n = maxBlockStr.lastIndexOf(". ");
            }
            if (n == -1) {
                n = Math.min(maxBlockStr.length(), maxBlockSize);
            }
            blocks.add(full.subSequence(0, n + 1));
            full = full.subSequence(n + 1, full.length());
        }
        if (full.length() > 0) {
            blocks.add(full);
        }
        return blocks;
    }

    private boolean fetchNext() {
        if (this.loading) {
            return false;
        }
        this.loading = true;
        if (this.blockIndex >= this.textBlocks.size()) {
            return false;
        }
        fetchTranslation(this.textBlocks.get(this.blockIndex), (long) Math.min((this.blockIndex + 1) * 1000, 3500), new TranslateAlert$$ExternalSyntheticLambda1(this), new TranslateAlert$$ExternalSyntheticLambda10(this));
        return true;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$fetchNext$7$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m4486lambda$fetchNext$7$orgtelegramuiComponentsTranslateAlert(java.lang.String r12, java.lang.String r13) {
        /*
            r11 = this;
            r0 = 1
            r11.loaded = r0
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r12)
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 1
            r8 = 0
            r3 = r1
            org.telegram.messenger.MessageObject.addUrlsByPattern(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x008b }
            int r2 = r1.length()     // Catch:{ Exception -> 0x008b }
            java.lang.Class<android.text.style.URLSpan> r3 = android.text.style.URLSpan.class
            java.lang.Object[] r2 = r1.getSpans(r8, r2, r3)     // Catch:{ Exception -> 0x008b }
            android.text.style.URLSpan[] r2 = (android.text.style.URLSpan[]) r2     // Catch:{ Exception -> 0x008b }
            r3 = 0
        L_0x001f:
            int r4 = r2.length     // Catch:{ Exception -> 0x008b }
            r5 = 33
            r6 = -1
            if (r3 >= r4) goto L_0x0042
            r4 = r2[r3]     // Catch:{ Exception -> 0x008b }
            int r7 = r1.getSpanStart(r4)     // Catch:{ Exception -> 0x008b }
            int r9 = r1.getSpanEnd(r4)     // Catch:{ Exception -> 0x008b }
            if (r7 == r6) goto L_0x003f
            if (r9 != r6) goto L_0x0034
            goto L_0x003f
        L_0x0034:
            r1.removeSpan(r4)     // Catch:{ Exception -> 0x008b }
            org.telegram.ui.Components.TranslateAlert$8 r6 = new org.telegram.ui.Components.TranslateAlert$8     // Catch:{ Exception -> 0x008b }
            r6.<init>(r4)     // Catch:{ Exception -> 0x008b }
            r1.setSpan(r6, r7, r9, r5)     // Catch:{ Exception -> 0x008b }
        L_0x003f:
            int r3 = r3 + 1
            goto L_0x001f
        L_0x0042:
            org.telegram.messenger.AndroidUtilities.addLinks(r1, r0)     // Catch:{ Exception -> 0x008b }
            int r3 = r1.length()     // Catch:{ Exception -> 0x008b }
            java.lang.Class<android.text.style.URLSpan> r4 = android.text.style.URLSpan.class
            java.lang.Object[] r3 = r1.getSpans(r8, r3, r4)     // Catch:{ Exception -> 0x008b }
            android.text.style.URLSpan[] r3 = (android.text.style.URLSpan[]) r3     // Catch:{ Exception -> 0x008b }
            r2 = r3
            r3 = 0
        L_0x0053:
            int r4 = r2.length     // Catch:{ Exception -> 0x008b }
            if (r3 >= r4) goto L_0x0073
            r4 = r2[r3]     // Catch:{ Exception -> 0x008b }
            int r7 = r1.getSpanStart(r4)     // Catch:{ Exception -> 0x008b }
            int r9 = r1.getSpanEnd(r4)     // Catch:{ Exception -> 0x008b }
            if (r7 == r6) goto L_0x0070
            if (r9 != r6) goto L_0x0065
            goto L_0x0070
        L_0x0065:
            r1.removeSpan(r4)     // Catch:{ Exception -> 0x008b }
            org.telegram.ui.Components.TranslateAlert$9 r10 = new org.telegram.ui.Components.TranslateAlert$9     // Catch:{ Exception -> 0x008b }
            r10.<init>(r4)     // Catch:{ Exception -> 0x008b }
            r1.setSpan(r10, r7, r9, r5)     // Catch:{ Exception -> 0x008b }
        L_0x0070:
            int r3 = r3 + 1
            goto L_0x0053
        L_0x0073:
            android.widget.TextView r3 = r11.allTextsView     // Catch:{ Exception -> 0x008b }
            android.text.TextPaint r3 = r3.getPaint()     // Catch:{ Exception -> 0x008b }
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()     // Catch:{ Exception -> 0x008b }
            r4 = 1096810496(0x41600000, float:14.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x008b }
            java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r1, r3, r4, r8)     // Catch:{ Exception -> 0x008b }
            android.text.Spannable r3 = (android.text.Spannable) r3     // Catch:{ Exception -> 0x008b }
            r1 = r3
            goto L_0x008f
        L_0x008b:
            r2 = move-exception
            r2.printStackTrace()
        L_0x008f:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            android.text.Spannable r3 = r11.allTexts
            java.lang.String r4 = ""
            if (r3 != 0) goto L_0x0098
            r3 = r4
        L_0x0098:
            r2.<init>(r3)
            int r3 = r11.blockIndex
            if (r3 != 0) goto L_0x00a0
            goto L_0x00a2
        L_0x00a0:
            java.lang.String r4 = "\n"
        L_0x00a2:
            android.text.SpannableStringBuilder r2 = r2.append(r4)
            android.text.SpannableStringBuilder r2 = r2.append(r1)
            r11.allTexts = r2
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r3 = r11.textsView
            r3.setWholeText(r2)
            org.telegram.ui.Components.TranslateAlert$TextBlocksLayout r2 = r11.textsView
            int r3 = r11.blockIndex
            org.telegram.ui.Components.TranslateAlert$LoadingTextView2 r2 = r2.getBlockAt(r3)
            if (r2 == 0) goto L_0x00c3
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda6
            r3.<init>(r11)
            r2.loaded(r1, r3)
        L_0x00c3:
            if (r13 == 0) goto L_0x00ca
            r11.fromLanguage = r13
            r11.updateSourceLanguage()
        L_0x00ca:
            int r3 = r11.blockIndex
            int r3 = r3 + r0
            r11.blockIndex = r3
            r11.loading = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.m4486lambda$fetchNext$7$orgtelegramuiComponentsTranslateAlert(java.lang.String, java.lang.String):void");
    }

    /* renamed from: lambda$fetchNext$6$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4485lambda$fetchNext$6$orgtelegramuiComponentsTranslateAlert() {
        this.contentView.post(new TranslateAlert$$ExternalSyntheticLambda8(this));
    }

    /* renamed from: lambda$fetchNext$8$org-telegram-ui-Components-TranslateAlert  reason: not valid java name */
    public /* synthetic */ void m4487lambda$fetchNext$8$orgtelegramuiComponentsTranslateAlert(boolean rateLimit) {
        if (rateLimit) {
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

    private void fetchTranslation(CharSequence text2, long minDuration, OnTranslationSuccess onSuccess, OnTranslationFail onFail) {
        final CharSequence charSequence = text2;
        final long j = minDuration;
        final OnTranslationSuccess onTranslationSuccess = onSuccess;
        final OnTranslationFail onTranslationFail = onFail;
        new Thread() {
            public void run() {
                boolean rateLimit;
                Throwable th;
                HttpURLConnection connection = null;
                long start = SystemClock.elapsedRealtime();
                try {
                    connection = (HttpURLConnection) new URI((((("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + Uri.encode(TranslateAlert.this.fromLanguage)) + "&tl=") + Uri.encode(TranslateAlert.this.toLanguage)) + "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q=") + Uri.encode(charSequence.toString())).toURL().openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
                    connection.setRequestProperty("Content-Type", "application/json");
                    StringBuilder textBuilder = new StringBuilder();
                    Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    while (true) {
                        try {
                            int read = reader.read();
                            int c = read;
                            if (read == -1) {
                                break;
                            }
                            try {
                                textBuilder.append((char) c);
                            } catch (Throwable th2) {
                                th = th2;
                                StringBuilder sb = textBuilder;
                            }
                        } catch (Throwable th3) {
                            StringBuilder sb2 = textBuilder;
                            th = th3;
                            try {
                                reader.close();
                            } catch (Throwable th4) {
                            }
                            throw th;
                        }
                    }
                    reader.close();
                    JSONArray array = new JSONArray(new JSONTokener(textBuilder.toString()));
                    JSONArray array1 = array.getJSONArray(0);
                    String sourceLanguage = null;
                    try {
                        sourceLanguage = array.getString(2);
                    } catch (Exception e) {
                    }
                    if (sourceLanguage != null) {
                        if (sourceLanguage.contains("-")) {
                            sourceLanguage = sourceLanguage.substring(0, sourceLanguage.indexOf("-"));
                        }
                    }
                    String result = "";
                    for (int i = 0; i < array1.length(); i++) {
                        String blockText = array1.getJSONArray(i).getString(0);
                        if (blockText != null && !blockText.equals("null")) {
                            result = result + blockText;
                        }
                    }
                    if (charSequence.length() > 0 && charSequence.charAt(0) == 10) {
                        result = "\n" + result;
                    }
                    String finalResult = result;
                    String finalSourceLanguage = sourceLanguage;
                    long elapsed = SystemClock.elapsedRealtime() - start;
                    StringBuilder sb3 = textBuilder;
                    long j = j;
                    if (elapsed < j) {
                        sleep(j - elapsed);
                    }
                    AndroidUtilities.runOnUIThread(new TranslateAlert$10$$ExternalSyntheticLambda2(onTranslationSuccess, finalResult, finalSourceLanguage));
                } catch (Exception e2) {
                    Exception e3 = e2;
                    try {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("failed to translate a text ");
                        String str = null;
                        sb4.append(connection != null ? Integer.valueOf(connection.getResponseCode()) : null);
                        sb4.append(" ");
                        if (connection != null) {
                            str = connection.getResponseMessage();
                        }
                        sb4.append(str);
                        Log.e("translate", sb4.toString());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    e3.printStackTrace();
                    if (onTranslationFail != null && !TranslateAlert.this.dismissed) {
                        if (connection != null) {
                            try {
                                if (connection.getResponseCode() == 429) {
                                    rateLimit = true;
                                    AndroidUtilities.runOnUIThread(new TranslateAlert$10$$ExternalSyntheticLambda1(onTranslationFail, rateLimit));
                                }
                            } catch (Exception e4) {
                                AndroidUtilities.runOnUIThread(new TranslateAlert$10$$ExternalSyntheticLambda0(onTranslationFail));
                                return;
                            }
                        }
                        rateLimit = false;
                        AndroidUtilities.runOnUIThread(new TranslateAlert$10$$ExternalSyntheticLambda1(onTranslationFail, rateLimit));
                    }
                }
            }

            static /* synthetic */ void lambda$run$0(OnTranslationSuccess onSuccess, String finalResult, String finalSourceLanguage) {
                if (onSuccess != null) {
                    onSuccess.run(finalResult, finalSourceLanguage);
                }
            }
        }.start();
    }

    private static void translateText(int currentAccount, TLRPC.InputPeer peer, int msg_id, String from_lang, String to_lang) {
        TLRPC.TL_messages_translateText req = new TLRPC.TL_messages_translateText();
        req.peer = peer;
        req.msg_id = msg_id;
        req.flags |= 1;
        if (from_lang != null) {
            req.from_lang = from_lang;
            req.flags |= 4;
        }
        req.to_lang = to_lang;
        try {
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, TranslateAlert$$ExternalSyntheticLambda9.INSTANCE);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$translateText$9(TLObject error, TLRPC.TL_error res) {
    }

    public static TranslateAlert showAlert(Context context, BaseFragment fragment2, int currentAccount, TLRPC.InputPeer peer, int msgId, String fromLanguage2, String toLanguage2, CharSequence text2, boolean noforwards2, OnLinkPress onLinkPress2, Runnable onDismiss2) {
        BaseFragment baseFragment = fragment2;
        TranslateAlert alert = new TranslateAlert(fragment2, context, currentAccount, peer, msgId, fromLanguage2, toLanguage2, text2, noforwards2, onLinkPress2, onDismiss2);
        if (baseFragment == null) {
            alert.show();
        } else if (fragment2.getParentActivity() != null) {
            fragment2.showDialog(alert);
        }
        return alert;
    }

    public static TranslateAlert showAlert(Context context, BaseFragment fragment2, String fromLanguage2, String toLanguage2, CharSequence text2, boolean noforwards2, OnLinkPress onLinkPress2, Runnable onDismiss2) {
        BaseFragment baseFragment = fragment2;
        TranslateAlert alert = new TranslateAlert(fragment2, context, fromLanguage2, toLanguage2, text2, noforwards2, onLinkPress2, onDismiss2);
        if (baseFragment == null) {
            alert.show();
        } else if (fragment2.getParentActivity() != null) {
            fragment2.showDialog(alert);
        }
        return alert;
    }

    public static class TextBlocksLayout extends ViewGroup {
        private static final int gap = (((-LoadingTextView2.paddingVertical) * 4) + AndroidUtilities.dp(0.48f));
        private final int fontSize;
        private final int textColor;
        private TextView wholeTextView;

        public TextBlocksLayout(Context context, int fontSize2, int textColor2, TextView wholeTextView2) {
            super(context);
            this.fontSize = fontSize2;
            this.textColor = textColor2;
            if (wholeTextView2 != null) {
                wholeTextView2.setPadding(LoadingTextView2.paddingHorizontal, LoadingTextView2.paddingVertical, LoadingTextView2.paddingHorizontal, LoadingTextView2.paddingVertical);
                this.wholeTextView = wholeTextView2;
                addView(wholeTextView2);
            }
        }

        public void setWholeText(CharSequence wholeText) {
            this.wholeTextView.clearFocus();
            this.wholeTextView.setText(wholeText);
        }

        public LoadingTextView2 addBlock(CharSequence fromText) {
            LoadingTextView2 textView = new LoadingTextView2(getContext(), fromText, getBlocksCount() > 0, this.fontSize, this.textColor);
            addView(textView);
            TextView textView2 = this.wholeTextView;
            if (textView2 != null) {
                textView2.bringToFront();
            }
            return textView;
        }

        public int getBlocksCount() {
            return getChildCount() - (this.wholeTextView != null ? 1 : 0);
        }

        public LoadingTextView2 getBlockAt(int i) {
            View child = getChildAt(i);
            if (child instanceof LoadingTextView2) {
                return (LoadingTextView2) child;
            }
            return null;
        }

        public LoadingTextView2 getFirstUnloadedBlock() {
            int count = getBlocksCount();
            for (int i = 0; i < count; i++) {
                LoadingTextView2 block = getBlockAt(i);
                if (block != null && !block.loaded) {
                    return block;
                }
            }
            return null;
        }

        public int height() {
            int height = 0;
            int count = getBlocksCount();
            for (int i = 0; i < count; i++) {
                height += getBlockAt(i).height();
            }
            return getPaddingTop() + height + getPaddingBottom();
        }

        /* access modifiers changed from: protected */
        public void onHeightUpdated(int height) {
        }

        public void updateHeight() {
            boolean updated;
            int newHeight = height();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(-1, newHeight);
                updated = true;
            } else {
                updated = lp.height != newHeight;
                lp.height = newHeight;
            }
            if (updated) {
                setLayoutParams(lp);
                onHeightUpdated(newHeight);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int count = getBlocksCount();
            int innerWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), View.MeasureSpec.getMode(widthMeasureSpec));
            for (int i = 0; i < count; i++) {
                getBlockAt(i).measure(innerWidthMeasureSpec, TranslateAlert.MOST_SPEC);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height(), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int y = 0;
            int height = 0;
            int count = getBlocksCount();
            int i = 0;
            while (i < count) {
                LoadingTextView2 block = getBlockAt(i);
                int blockHeight = block.height();
                int translationY = i > 0 ? gap : 0;
                block.layout(getPaddingLeft(), getPaddingTop() + y + translationY, (r - l) - getPaddingRight(), getPaddingTop() + y + blockHeight + translationY);
                y += blockHeight;
                if (i > 0 && i < count - 1) {
                    y += gap;
                }
                height += blockHeight;
                i++;
            }
            this.wholeTextView.measure(View.MeasureSpec.makeMeasureSpec(((r - l) - getPaddingLeft()) - getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(((b - t) - getPaddingTop()) - getPaddingBottom(), NUM));
            this.wholeTextView.layout(getPaddingLeft(), getPaddingTop(), (r - l) - getPaddingRight(), getPaddingTop() + this.wholeTextView.getMeasuredHeight());
        }
    }

    public static class InlineLoadingTextView extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(6.0f);
        public static final int paddingVertical = 0;
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

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public InlineLoadingTextView(Context context, CharSequence fromText, int fontSize, int textColor) {
            super(context);
            Context context2 = context;
            int i = fontSize;
            int i2 = textColor;
            Paint paint = new Paint();
            this.loadingPaint = paint;
            float dp = (float) AndroidUtilities.dp(350.0f);
            this.gradientWidth = dp;
            int i3 = paddingHorizontal;
            setPadding(i3, 0, i3, 0);
            setClipChildren(false);
            setWillNotDraw(false);
            AnonymousClass1 r8 = new TextView(context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(TranslateAlert.MOST_SPEC, TranslateAlert.MOST_SPEC);
                }
            };
            this.fromTextView = r8;
            r8.setTextSize(0, (float) i);
            r8.setTextColor(i2);
            r8.setText(fromText);
            r8.setLines(1);
            r8.setMaxLines(1);
            r8.setSingleLine(true);
            r8.setEllipsize((TextUtils.TruncateAt) null);
            addView(r8);
            AnonymousClass2 r82 = new TextView(context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(TranslateAlert.MOST_SPEC, TranslateAlert.MOST_SPEC);
                }
            };
            this.toTextView = r82;
            r82.setTextSize(0, (float) i);
            r82.setTextColor(i2);
            r82.setLines(1);
            r82.setMaxLines(1);
            r82.setSingleLine(true);
            r82.setEllipsize((TextUtils.TruncateAt) null);
            addView(r82);
            int c1 = Theme.getColor("dialogBackground");
            paint.setShader(new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{c1, Theme.getColor("dialogBackgroundGray"), c1}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.loadingAnimator = ofFloat;
            ofFloat.addUpdateListener(new TranslateAlert$InlineLoadingTextView$$ExternalSyntheticLambda1(this));
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert$InlineLoadingTextView  reason: not valid java name */
        public /* synthetic */ void m4494x9271a5cb(ValueAnimator a) {
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.fromTextView.measure(0, 0);
            this.toTextView.measure(0, 0);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(this.fromTextView.getMeasuredWidth(), this.toTextView.getMeasuredWidth(), this.loadingT) + getPaddingLeft() + getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(Math.max(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight()), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            this.fromTextView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.fromTextView.getMeasuredWidth(), getPaddingTop() + this.fromTextView.getMeasuredHeight());
            this.toTextView.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + this.toTextView.getMeasuredWidth(), getPaddingTop() + this.toTextView.getMeasuredHeight());
            updateWidth();
        }

        private void updateWidth() {
            boolean updated;
            int newWidth = AndroidUtilities.lerp(this.fromTextView.getMeasuredWidth(), this.toTextView.getMeasuredWidth(), this.loadingT) + getPaddingLeft() + getPaddingRight();
            int newHeight = Math.max(this.fromTextView.getMeasuredHeight(), this.toTextView.getMeasuredHeight());
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp == null) {
                lp = new LinearLayout.LayoutParams(newWidth, newHeight);
                updated = true;
            } else {
                updated = (lp.width == newWidth && lp.height == newHeight) ? false : true;
                lp.width = newWidth;
                lp.height = newHeight;
            }
            if (updated) {
                setLayoutParams(lp);
            }
        }

        /* access modifiers changed from: protected */
        public void onLoadAnimation(float t) {
        }

        public void loaded(CharSequence loadedText) {
            loaded(loadedText, 350, (Runnable) null);
        }

        public void loaded(CharSequence loadedText, Runnable onLoadEnd) {
            loaded(loadedText, 350, onLoadEnd);
        }

        public void loaded(CharSequence loadedText, long duration, final Runnable onLoadEnd) {
            this.loaded = true;
            this.toTextView.setText(loadedText);
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            if (this.loadedAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.loadedAnimator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$InlineLoadingTextView$$ExternalSyntheticLambda0(this));
                this.loadedAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Runnable runnable = onLoadEnd;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                this.loadedAnimator.setDuration(duration);
                this.loadedAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                this.loadedAnimator.start();
            }
        }

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert$InlineLoadingTextView  reason: not valid java name */
        public /* synthetic */ void m4493x9a76264b(ValueAnimator a) {
            this.loadingT = ((Float) a.getAnimatedValue()).floatValue();
            updateWidth();
            invalidate();
            onLoadAnimation(this.loadingT);
        }

        public void set(CharSequence loadedText) {
            this.loaded = true;
            this.toTextView.setText(loadedText);
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
            Canvas canvas2 = canvas;
            float w = (float) getWidth();
            float h = (float) getHeight();
            float cx = LocaleController.isRTL ? Math.max(w / 2.0f, w - 8.0f) : Math.min(w / 2.0f, 8.0f);
            float cy = Math.min(h / 2.0f, 8.0f);
            float r = this.loadingT * ((float) Math.sqrt((double) Math.max(Math.max((cx * cx) + (cy * cy), ((w - cx) * (w - cx)) + (cy * cy)), Math.max((cx * cx) + ((h - cy) * (h - cy)), ((w - cx) * (w - cx)) + ((h - cy) * (h - cy))))));
            this.inPath.reset();
            this.inPath.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.save();
            canvas2.clipPath(this.inPath, Region.Op.DIFFERENCE);
            this.loadingPaint.setAlpha((int) ((1.0f - this.loadingT) * 255.0f));
            float f = this.gradientWidth;
            float f2 = this.gradientWidth;
            float dx = f - (((((float) (SystemClock.elapsedRealtime() - this.start)) / 1000.0f) * f2) % f2);
            this.shadePath.reset();
            float dx2 = dx;
            this.shadePath.addRect(0.0f, 0.0f, w, h, Path.Direction.CW);
            this.loadingPath.reset();
            this.rect.set(0.0f, 0.0f, w, h);
            this.loadingPath.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
            canvas2.clipPath(this.loadingPath);
            canvas2.translate(-dx2, 0.0f);
            this.shadePath.offset(dx2, 0.0f, this.tempPath);
            canvas2.drawPath(this.tempPath, this.loadingPaint);
            canvas2.translate(dx2, 0.0f);
            canvas.restore();
            if (this.showLoadingText && this.fromTextView != null) {
                canvas.save();
                this.rect.set(0.0f, 0.0f, w, h);
                canvas2.clipPath(this.inPath, Region.Op.DIFFERENCE);
                canvas2.translate((float) paddingHorizontal, 0.0f);
                canvas2.saveLayerAlpha(this.rect, 20, 31);
                this.fromTextView.draw(canvas2);
                canvas.restore();
                canvas.restore();
            }
            if (this.toTextView != null) {
                canvas.save();
                canvas2.clipPath(this.inPath);
                canvas2.translate((float) paddingHorizontal, 0.0f);
                canvas2.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.toTextView.draw(canvas2);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return false;
        }
    }

    public static class LoadingTextView2 extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(6.0f);
        public static final int paddingVertical = AndroidUtilities.dp(1.5f);
        private Path fetchPath = new Path() {
            private boolean got = false;

            public void reset() {
                super.reset();
                this.got = false;
            }

            public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
                if (!this.got) {
                    LoadingTextView2.this.fetchedPathRect.set(left - ((float) LoadingTextView2.paddingHorizontal), top - ((float) LoadingTextView2.paddingVertical), ((float) LoadingTextView2.paddingHorizontal) + right, ((float) LoadingTextView2.paddingVertical) + bottom);
                    this.got = true;
                }
            }
        };
        /* access modifiers changed from: private */
        public RectF fetchedPathRect = new RectF();
        private final TextView fromTextView;
        private final float gradientWidth;
        private final Path inPath = new Path();
        int lastWidth = 0;
        public boolean loaded = false;
        private ValueAnimator loadedAnimator = null;
        private final ValueAnimator loadingAnimator;
        private final Paint loadingPaint;
        private final Path loadingPath = new Path();
        private float loadingT = 0.0f;
        private final RectF rect = new RectF();
        private final boolean scaleFromZero;
        private float scaleT = 1.0f;
        private final Path shadePath = new Path();
        public boolean showLoadingText = true;
        private final long start = SystemClock.elapsedRealtime();
        private final Path tempPath = new Path();
        private final TextView toTextView;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public LoadingTextView2(Context context, CharSequence fromText, boolean scaleFromZero2, int fontSize, int textColor) {
            super(context);
            Context context2 = context;
            boolean z = scaleFromZero2;
            int i = fontSize;
            int i2 = textColor;
            Paint paint = new Paint();
            this.loadingPaint = paint;
            float dp = (float) AndroidUtilities.dp(350.0f);
            this.gradientWidth = dp;
            int i3 = paddingHorizontal;
            int i4 = paddingVertical;
            setPadding(i3, i4, i3, i4);
            setClipChildren(false);
            setWillNotDraw(false);
            AnonymousClass1 r10 = new TextView(context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, TranslateAlert.MOST_SPEC);
                }
            };
            this.fromTextView = r10;
            r10.setTextSize(0, (float) i);
            r10.setTextColor(i2);
            r10.setText(fromText);
            r10.setLines(0);
            r10.setMaxLines(0);
            r10.setSingleLine(false);
            r10.setEllipsize((TextUtils.TruncateAt) null);
            addView(r10);
            AnonymousClass2 r102 = new TextView(context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, TranslateAlert.MOST_SPEC);
                }
            };
            this.toTextView = r102;
            r102.setTextSize(0, (float) i);
            r102.setTextColor(i2);
            r102.setLines(0);
            r102.setMaxLines(0);
            r102.setSingleLine(false);
            r102.setEllipsize((TextUtils.TruncateAt) null);
            addView(r102);
            int c1 = Theme.getColor("dialogBackground");
            paint.setShader(new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{c1, Theme.getColor("dialogBackgroundGray"), c1}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT));
            this.scaleFromZero = z;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.loadingAnimator = ofFloat;
            if (z) {
                this.scaleT = 0.0f;
            }
            ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView2$$ExternalSyntheticLambda1(this, z));
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert$LoadingTextView2  reason: not valid java name */
        public /* synthetic */ void m4496xfb13368e(boolean scaleFromZero2, ValueAnimator a) {
            invalidate();
            if (scaleFromZero2) {
                boolean scaleTWasNoFull = this.scaleT < 1.0f;
                this.scaleT = Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.start)) / 400.0f);
                if (scaleTWasNoFull) {
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

        public void loaded(CharSequence loadedText, final Runnable onLoadEnd) {
            this.loaded = true;
            this.toTextView.setText(loadedText);
            layout();
            if (this.loadingAnimator.isRunning()) {
                this.loadingAnimator.cancel();
            }
            if (this.loadedAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.loadedAnimator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView2$$ExternalSyntheticLambda0(this));
                this.loadedAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Runnable runnable = onLoadEnd;
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

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert$LoadingTextView2  reason: not valid java name */
        public /* synthetic */ void m4495xe960660e(ValueAnimator a) {
            this.loadingT = ((Float) a.getAnimatedValue()).floatValue();
            updateHeight();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int innerWidth = (width - getPaddingLeft()) - getPaddingRight();
            if (this.fromTextView.getMeasuredWidth() <= 0 || this.lastWidth != innerWidth) {
                measureChild(this.fromTextView, innerWidth);
                updateLoadingPath();
            }
            if (this.toTextView.getMeasuredWidth() <= 0 || this.lastWidth != innerWidth) {
                measureChild(this.toTextView, innerWidth);
            }
            this.lastWidth = innerWidth;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height(), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            layout(((r - l) - getPaddingLeft()) - getPaddingRight(), true);
        }

        private void layout(int width, boolean force) {
            if (this.lastWidth != width || force) {
                this.lastWidth = width;
                layout(width);
            }
        }

        private void layout(int width) {
            measureChild(this.fromTextView, width);
            layoutChild(this.fromTextView, width);
            updateLoadingPath();
            measureChild(this.toTextView, width);
            layoutChild(this.toTextView, width);
            updateHeight();
        }

        private void layout() {
            layout(this.lastWidth);
        }

        private void measureChild(View view, int width) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), TranslateAlert.MOST_SPEC);
        }

        private void layoutChild(View view, int width) {
            view.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + width, getPaddingTop() + view.getMeasuredHeight());
        }

        private void updateLoadingPath() {
            Layout loadingLayout;
            TextView textView = this.fromTextView;
            if (textView != null && textView.getMeasuredWidth() > 0) {
                this.loadingPath.reset();
                Layout loadingLayout2 = this.fromTextView.getLayout();
                if (loadingLayout2 != null) {
                    CharSequence text = loadingLayout2.getText();
                    int lineCount = loadingLayout2.getLineCount();
                    int i = 0;
                    while (i < lineCount) {
                        float s = loadingLayout2.getLineLeft(i);
                        float e = loadingLayout2.getLineRight(i);
                        float l = Math.min(s, e);
                        float r = Math.max(s, e);
                        int start2 = loadingLayout2.getLineStart(i);
                        int end = loadingLayout2.getLineEnd(i);
                        boolean hasNonEmptyChar = false;
                        int j = start2;
                        while (true) {
                            if (j < end) {
                                char c = text.charAt(j);
                                if (c != 10 && c != 9 && c != ' ') {
                                    hasNonEmptyChar = true;
                                    break;
                                }
                                j++;
                            } else {
                                break;
                            }
                        }
                        if (!hasNonEmptyChar) {
                            loadingLayout = loadingLayout2;
                        } else {
                            RectF rectF = this.fetchedPathRect;
                            int i2 = paddingHorizontal;
                            int lineTop = loadingLayout2.getLineTop(i);
                            int i3 = paddingVertical;
                            loadingLayout = loadingLayout2;
                            rectF.set(l - ((float) i2), (float) (lineTop - i3), ((float) i2) + r, (float) (loadingLayout2.getLineBottom(i) + i3));
                            this.loadingPath.addRoundRect(this.fetchedPathRect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        }
                        i++;
                        loadingLayout2 = loadingLayout;
                    }
                    return;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            float w = (float) getWidth();
            float h = (float) getHeight();
            float cx = LocaleController.isRTL ? Math.max(w / 2.0f, w - 8.0f) : Math.min(w / 2.0f, 8.0f);
            float cy = Math.min(h / 2.0f, 8.0f);
            float r = this.loadingT * ((float) Math.sqrt((double) Math.max(Math.max((cx * cx) + (cy * cy), ((w - cx) * (w - cx)) + (cy * cy)), Math.max((cx * cx) + ((h - cy) * (h - cy)), ((w - cx) * (w - cx)) + ((h - cy) * (h - cy))))));
            this.inPath.reset();
            this.inPath.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.save();
            canvas2.clipPath(this.inPath, Region.Op.DIFFERENCE);
            this.loadingPaint.setAlpha((int) ((1.0f - this.loadingT) * 255.0f));
            float f = this.gradientWidth;
            float f2 = this.gradientWidth;
            float dx = f - (((((float) (SystemClock.elapsedRealtime() - this.start)) / 1000.0f) * f2) % f2);
            this.shadePath.reset();
            float dx2 = dx;
            this.shadePath.addRect(0.0f, 0.0f, w, h, Path.Direction.CW);
            int i = paddingHorizontal;
            int i2 = paddingVertical;
            canvas2.translate((float) i, (float) i2);
            canvas2.clipPath(this.loadingPath);
            canvas2.translate((float) (-i), (float) (-i2));
            canvas2.translate(-dx2, 0.0f);
            this.shadePath.offset(dx2, 0.0f, this.tempPath);
            canvas2.drawPath(this.tempPath, this.loadingPaint);
            canvas2.translate(dx2, 0.0f);
            canvas.restore();
            if (this.showLoadingText && this.fromTextView != null) {
                canvas.save();
                this.rect.set(0.0f, 0.0f, w, h);
                canvas2.clipPath(this.inPath, Region.Op.DIFFERENCE);
                canvas2.translate((float) i, (float) i2);
                canvas2.saveLayerAlpha(this.rect, 20, 31);
                this.fromTextView.draw(canvas2);
                canvas.restore();
                canvas.restore();
            }
            if (this.toTextView != null) {
                canvas.save();
                canvas2.clipPath(this.inPath);
                canvas2.translate((float) i, (float) i2);
                canvas2.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.toTextView.draw(canvas2);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return false;
        }
    }
}
