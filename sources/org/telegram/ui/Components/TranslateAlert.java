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
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_translateText;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class TranslateAlert extends Dialog {
    /* access modifiers changed from: private */
    public Spannable allTexts;
    /* access modifiers changed from: private */
    public FrameLayout allTextsContainer;
    /* access modifiers changed from: private */
    public TextView allTextsView;
    /* access modifiers changed from: private */
    public boolean allowScroll;
    private ImageView backButton;
    protected ColorDrawable backDrawable;
    private Rect backRect;
    private int blockIndex = 0;
    /* access modifiers changed from: private */
    public FrameLayout bulletinContainer;
    private Rect buttonRect;
    private FrameLayout buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    /* access modifiers changed from: private */
    public FrameLayout container;
    /* access modifiers changed from: private */
    public float containerOpenAnimationT = 0.0f;
    private Rect containerRect;
    /* access modifiers changed from: private */
    public FrameLayout contentView;
    /* access modifiers changed from: private */
    public boolean dismissed;
    /* access modifiers changed from: private */
    public boolean fastHide;
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
    private LoadingTextView lastLoadingBlock;
    private boolean loaded;
    private boolean loading;
    private boolean maybeScrolling;
    /* access modifiers changed from: private */
    public long minFetchingDuration;
    /* access modifiers changed from: private */
    public boolean noforwards;
    /* access modifiers changed from: private */
    public OnLinkPress onLinkPress;
    private ValueAnimator openAnimationToAnimator;
    /* access modifiers changed from: private */
    public boolean openAnimationToAnimatorPriority = false;
    private ValueAnimator openingAnimator;
    /* access modifiers changed from: private */
    public boolean openingAnimatorPriority;
    /* access modifiers changed from: private */
    public float openingT;
    /* access modifiers changed from: private */
    public ClickableSpan pressedLink;
    private boolean pressedOutside;
    private Rect scrollRect;
    /* access modifiers changed from: private */
    public NestedScrollView scrollView;
    private FrameLayout.LayoutParams scrollViewLayout;
    private boolean scrolling;
    private ImageView subtitleArrowView;
    /* access modifiers changed from: private */
    public LoadingTextView subtitleFromView;
    private FrameLayout.LayoutParams subtitleLayout;
    private TextView subtitleToView;
    private LinearLayout subtitleView;
    private ArrayList<CharSequence> textBlocks;
    /* access modifiers changed from: private */
    public int textPadHorz;
    /* access modifiers changed from: private */
    public int textPadVert;
    private Rect textRect;
    /* access modifiers changed from: private */
    public FrameLayout textsContainerView;
    /* access modifiers changed from: private */
    public LinearLayout textsView;
    private FrameLayout.LayoutParams titleLayout;
    private TextView titleView;
    /* access modifiers changed from: private */
    public String toLanguage;
    /* access modifiers changed from: private */
    public String[] userAgents;

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
    public static /* synthetic */ void lambda$translateText$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public void showTranslateMoreView(boolean z) {
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
            int dp = AndroidUtilities.dp(AndroidUtilities.lerp(22.0f, 72.0f, min));
            int dp2 = AndroidUtilities.dp(AndroidUtilities.lerp(22.0f, 8.0f, min));
            FrameLayout.LayoutParams layoutParams2 = this.titleLayout;
            layoutParams.setMargins(dp, dp2, layoutParams2.rightMargin, layoutParams2.bottomMargin);
            this.titleView.setLayoutParams(this.titleLayout);
            FrameLayout.LayoutParams layoutParams3 = this.subtitleLayout;
            int dp3 = AndroidUtilities.dp(AndroidUtilities.lerp(22.0f, 72.0f, min)) - this.subtitleFromView.padHorz;
            int dp4 = AndroidUtilities.dp(AndroidUtilities.lerp(47.0f, 30.0f, min)) - this.subtitleFromView.padVert;
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
            this.headerLayout.height = (int) AndroidUtilities.lerp((float) AndroidUtilities.dp(70.0f), (float) AndroidUtilities.dp(56.0f), min);
            this.header.setLayoutParams(this.headerLayout);
            FrameLayout.LayoutParams layoutParams5 = this.scrollViewLayout;
            FrameLayout.LayoutParams layoutParams6 = this.scrollViewLayout;
            layoutParams5.setMargins(layoutParams5.leftMargin, (int) AndroidUtilities.lerp((float) AndroidUtilities.dp(70.0f), (float) AndroidUtilities.dp(56.0f), min), layoutParams6.rightMargin, layoutParams6.bottomMargin);
            this.scrollView.setLayoutParams(this.scrollViewLayout);
            this.container.requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public void openAnimationTo(float f, boolean z) {
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
        if ((this.textsView.getChildCount() < this.textBlocks.size() || ((float) minHeight()) >= ((float) AndroidUtilities.displayMetrics.heightPixels) * this.heightMaxPercent) && this.textsView.getChildCount() > 0 && ((LoadingTextView) this.textsView.getChildAt(0)).loaded) {
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

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TranslateAlert(org.telegram.ui.ActionBar.BaseFragment r35, android.content.Context r36, int r37, org.telegram.tgnet.TLRPC$InputPeer r38, int r39, java.lang.String r40, java.lang.String r41, java.lang.CharSequence r42, boolean r43, org.telegram.ui.Components.TranslateAlert.OnLinkPress r44) {
        /*
            r34 = this;
            r8 = r34
            r9 = r36
            r0 = r38
            r1 = r40
            r10 = r41
            r11 = r43
            r2 = 2131689509(0x7f0var_, float:1.9008035E38)
            r8.<init>(r9, r2)
            r12 = 0
            r8.blockIndex = r12
            r13 = 0
            r8.containerOpenAnimationT = r13
            r8.openAnimationToAnimatorPriority = r12
            r2 = 0
            r8.openAnimationToAnimator = r2
            r14 = 1
            r8.allowScroll = r14
            r8.onLinkPress = r2
            r8.fromScrollY = r13
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r8.containerRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r8.textRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r8.buttonRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r8.backRect = r3
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r8.scrollRect = r3
            r8.fromY = r13
            r8.pressedOutside = r12
            r8.maybeScrolling = r12
            r8.scrolling = r12
            r8.fromScrollRect = r12
            r8.fromTranslateMoreView = r12
            r8.fromScrollViewY = r13
            r8.allTexts = r2
            r8.openingT = r13
            org.telegram.ui.Components.TranslateAlert$9 r3 = new org.telegram.ui.Components.TranslateAlert$9
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3.<init>(r4)
            r8.backDrawable = r3
            r8.dismissed = r12
            r3 = 1062836634(0x3var_a, float:0.85)
            r8.heightMaxPercent = r3
            r8.fastHide = r12
            r8.openingAnimatorPriority = r12
            r8.loading = r12
            r8.loaded = r12
            r8.lastLoadingBlock = r2
            r3 = 6
            java.lang.String[] r3 = new java.lang.String[r3]
            java.lang.String r4 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
            r3[r12] = r4
            java.lang.String r4 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36"
            r3[r14] = r4
            r4 = 2
            java.lang.String r5 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0"
            r3[r4] = r5
            java.lang.String r4 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0"
            r15 = 3
            r3[r15] = r4
            r4 = 4
            java.lang.String r5 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"
            r3[r4] = r5
            java.lang.String r4 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
            r16 = 5
            r3[r16] = r4
            r8.userAgents = r3
            r3 = 1000(0x3e8, double:4.94E-321)
            r8.minFetchingDuration = r3
            java.lang.String r3 = "und"
            if (r0 == 0) goto L_0x00b7
            if (r1 == 0) goto L_0x00af
            boolean r4 = r1.equals(r3)
            if (r4 == 0) goto L_0x00af
            r4 = r37
            r5 = r39
            goto L_0x00b4
        L_0x00af:
            r4 = r37
            r5 = r39
            r2 = r1
        L_0x00b4:
            translateText(r4, r0, r5, r2, r10)
        L_0x00b7:
            r0 = r44
            r8.onLinkPress = r0
            r8.noforwards = r11
            r0 = r35
            r8.fragment = r0
            if (r1 == 0) goto L_0x00cc
            boolean r0 = r1.equals(r3)
            if (r0 == 0) goto L_0x00cc
            java.lang.String r0 = "auto"
            goto L_0x00cd
        L_0x00cc:
            r0 = r1
        L_0x00cd:
            r8.fromLanguage = r0
            r8.toLanguage = r10
            r0 = 1024(0x400, float:1.435E-42)
            r2 = r42
            java.util.ArrayList r0 = r8.cutInBlocks(r2, r0)
            r8.textBlocks = r0
            int r7 = android.os.Build.VERSION.SDK_INT
            r0 = 21
            r2 = 30
            if (r7 < r2) goto L_0x00ee
            android.view.Window r3 = r34.getWindow()
            r4 = -2147483392(0xfffffffvar_, float:-3.59E-43)
            r3.addFlags(r4)
            goto L_0x00fa
        L_0x00ee:
            if (r7 < r0) goto L_0x00fa
            android.view.Window r3 = r34.getWindow()
            r4 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r3.addFlags(r4)
        L_0x00fa:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r9)
            r8.contentView = r3
            android.graphics.drawable.ColorDrawable r4 = r8.backDrawable
            r3.setBackground(r4)
            android.widget.FrameLayout r3 = r8.contentView
            r3.setClipChildren(r12)
            android.widget.FrameLayout r3 = r8.contentView
            r3.setClipToPadding(r12)
            if (r7 < r0) goto L_0x0128
            android.widget.FrameLayout r0 = r8.contentView
            r0.setFitsSystemWindows(r14)
            if (r7 < r2) goto L_0x0121
            android.widget.FrameLayout r0 = r8.contentView
            r2 = 1792(0x700, float:2.511E-42)
            r0.setSystemUiVisibility(r2)
            goto L_0x0128
        L_0x0121:
            android.widget.FrameLayout r0 = r8.contentView
            r2 = 1280(0x500, float:1.794E-42)
            r0.setSystemUiVisibility(r2)
        L_0x0128:
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
            r0.setShadowLayer(r2, r13, r3, r4)
            org.telegram.ui.Components.TranslateAlert$2 r2 = new org.telegram.ui.Components.TranslateAlert$2
            r2.<init>(r9, r0)
            r8.container = r2
            r2.setWillNotDraw(r12)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r9)
            r8.header = r0
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.titleView = r0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x016c
            int r2 = r0.getWidth()
            float r2 = (float) r2
            goto L_0x016d
        L_0x016c:
            r2 = 0
        L_0x016d:
            r0.setPivotX(r2)
            android.widget.TextView r0 = r8.titleView
            r0.setPivotY(r13)
            android.widget.TextView r0 = r8.titleView
            r0.setLines(r14)
            android.widget.TextView r0 = r8.titleView
            r2 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.String r3 = "AutomaticTranslation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r8.titleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0190
            r2 = 5
            goto L_0x0191
        L_0x0190:
            r2 = 3
        L_0x0191:
            r0.setGravity(r2)
            android.widget.TextView r0 = r8.titleView
            java.lang.String r17 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r8.titleView
            java.lang.String r18 = "dialogTextBlack"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r8.titleView
            r2 = 1100480512(0x41980000, float:19.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r12, r2)
            android.widget.FrameLayout r0 = r8.header
            android.widget.TextView r2 = r8.titleView
            r19 = -1
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r21 = 55
            r22 = 1102053376(0x41b00000, float:22.0)
            r23 = 1102053376(0x41b00000, float:22.0)
            r24 = 1102053376(0x41b00000, float:22.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r8.titleLayout = r3
            r0.addView(r2, r3)
            android.widget.TextView r0 = r8.titleView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda4
            r2.<init>(r8)
            r0.post(r2)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r9)
            r8.subtitleView = r0
            r0.setOrientation(r12)
            r6 = 17
            if (r7 < r6) goto L_0x01f0
            android.widget.LinearLayout r0 = r8.subtitleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r0.setLayoutDirection(r2)
        L_0x01f0:
            android.widget.LinearLayout r0 = r8.subtitleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x01f8
            r2 = 5
            goto L_0x01f9
        L_0x01f8:
            r2 = 3
        L_0x01f9:
            r0.setGravity(r2)
            r0 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r8.textPadHorz = r2
            r2 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r8.textPadVert = r3
            java.lang.String r5 = r8.languageName(r1)
            org.telegram.ui.Components.TranslateAlert$3 r4 = new org.telegram.ui.Components.TranslateAlert$3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r5 != 0) goto L_0x0223
            java.lang.String r0 = r8.languageName(r10)
            r20 = r0
            goto L_0x0225
        L_0x0223:
            r20 = r5
        L_0x0225:
            r21 = 0
            r22 = 1
            r0 = r4
            r1 = r34
            r2 = r36
            r15 = r4
            r4 = r19
            r13 = r5
            r5 = r20
            r6 = r21
            r26 = r7
            r7 = r22
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.subtitleFromView = r15
            r15.showLoadingText(r12)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r0 = r8.subtitleFromView
            r0.setLines(r14)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r0 = r8.subtitleFromView
            java.lang.String r1 = "player_actionBarSubtitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r2)
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r0 = r8.subtitleFromView
            r2 = 1096810496(0x41600000, float:14.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r3)
            if (r13 == 0) goto L_0x0264
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r0 = r8.subtitleFromView
            r0.setText(r13)
        L_0x0264:
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.subtitleArrowView = r0
            r3 = 2131166056(0x7var_, float:1.7946347E38)
            r0.setImageResource(r3)
            android.widget.ImageView r0 = r8.subtitleArrowView
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r0.setColorFilter(r3)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r0 == 0) goto L_0x028c
            android.widget.ImageView r0 = r8.subtitleArrowView
            r0.setScaleX(r3)
        L_0x028c:
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.subtitleToView = r0
            r0.setLines(r14)
            android.widget.TextView r0 = r8.subtitleToView
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r8.subtitleToView
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            r0.setTextSize(r12, r1)
            android.widget.TextView r0 = r8.subtitleToView
            java.lang.String r1 = r8.languageName(r10)
            r0.setText(r1)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            r1 = -2
            if (r0 == 0) goto L_0x02fb
            android.widget.LinearLayout r0 = r8.subtitleView
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r4 = r8.subtitleFromView
            int r4 = r4.padHorz
            int r5 = r8.textPadHorz
            int r5 = r5 - r4
            r0.setPadding(r4, r12, r5, r12)
            android.widget.LinearLayout r0 = r8.subtitleView
            android.widget.TextView r4 = r8.subtitleToView
            r5 = 16
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r1, (int) r1, (int) r5)
            r0.addView(r4, r5)
            android.widget.LinearLayout r0 = r8.subtitleView
            android.widget.ImageView r4 = r8.subtitleArrowView
            r27 = -2
            r28 = -2
            r29 = 16
            r30 = 3
            r31 = 1
            r32 = 0
            r33 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r4, r5)
            android.widget.LinearLayout r0 = r8.subtitleView
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r4 = r8.subtitleFromView
            r27 = 0
            r30 = 2
            r31 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r4, r5)
            goto L_0x033e
        L_0x02fb:
            android.widget.LinearLayout r0 = r8.subtitleView
            int r4 = r8.textPadHorz
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r5 = r8.subtitleFromView
            int r5 = r5.padHorz
            int r4 = r4 - r5
            r0.setPadding(r4, r12, r5, r12)
            android.widget.LinearLayout r0 = r8.subtitleView
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r4 = r8.subtitleFromView
            r27 = 0
            r28 = -2
            r29 = 16
            r30 = 0
            r31 = 0
            r32 = 2
            r33 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r4, r5)
            android.widget.LinearLayout r0 = r8.subtitleView
            android.widget.ImageView r4 = r8.subtitleArrowView
            r27 = -2
            r31 = 1
            r32 = 3
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r4, r5)
            android.widget.LinearLayout r0 = r8.subtitleView
            android.widget.TextView r4 = r8.subtitleToView
            r5 = 16
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r1, (int) r1, (int) r5)
            r0.addView(r4, r5)
        L_0x033e:
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r0 = r8.subtitleFromView
            r0.updateHeight()
            android.widget.FrameLayout r0 = r8.header
            android.widget.LinearLayout r4 = r8.subtitleView
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0350
            goto L_0x0352
        L_0x0350:
            r16 = 3
        L_0x0352:
            r29 = r16 | 48
            int r5 = r8.textPadHorz
            float r6 = (float) r5
            float r7 = org.telegram.messenger.AndroidUtilities.density
            float r6 = r6 / r7
            r10 = 1102053376(0x41b00000, float:22.0)
            float r30 = r10 - r6
            r6 = 1111228416(0x423CLASSNAME, float:47.0)
            int r13 = r8.textPadVert
            float r13 = (float) r13
            float r13 = r13 / r7
            float r31 = r6 - r13
            float r5 = (float) r5
            float r5 = r5 / r7
            float r32 = r10 - r5
            r33 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r8.subtitleLayout = r5
            r0.addView(r4, r5)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.backButton = r0
            r4 = 2131165487(0x7var_f, float:1.7945193E38)
            r0.setImageResource(r4)
            android.widget.ImageView r0 = r8.backButton
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r6)
            r0.setColorFilter(r4)
            android.widget.ImageView r0 = r8.backButton
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.FIT_CENTER
            r0.setScaleType(r4)
            android.widget.ImageView r0 = r8.backButton
            r4 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setPadding(r5, r12, r6, r12)
            android.widget.ImageView r0 = r8.backButton
            java.lang.String r5 = "dialogButtonSelector"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5)
            r0.setBackground(r5)
            android.widget.ImageView r0 = r8.backButton
            r0.setClickable(r12)
            android.widget.ImageView r0 = r8.backButton
            r5 = 0
            r0.setAlpha(r5)
            android.widget.ImageView r0 = r8.backButton
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda2
            r5.<init>(r8)
            r0.setOnClickListener(r5)
            android.widget.FrameLayout r0 = r8.header
            android.widget.ImageView r5 = r8.backButton
            r6 = 56
            r7 = 56
            r13 = 3
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r13)
            r0.addView(r5, r6)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r9)
            r8.headerShadowView = r0
            java.lang.String r5 = "dialogShadowLine"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setBackgroundColor(r5)
            android.widget.FrameLayout r0 = r8.headerShadowView
            r5 = 0
            r0.setAlpha(r5)
            android.widget.FrameLayout r0 = r8.header
            android.widget.FrameLayout r5 = r8.headerShadowView
            r6 = 87
            r7 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r14, r6)
            r0.addView(r5, r6)
            android.widget.FrameLayout r0 = r8.header
            r0.setClipChildren(r12)
            android.widget.FrameLayout r0 = r8.container
            android.widget.FrameLayout r5 = r8.header
            r6 = 70
            r13 = 55
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6, r13)
            r8.headerLayout = r6
            r0.addView(r5, r6)
            org.telegram.ui.Components.TranslateAlert$4 r0 = new org.telegram.ui.Components.TranslateAlert$4
            r0.<init>(r9)
            r8.scrollView = r0
            r0.setClipChildren(r14)
            org.telegram.ui.Components.TranslateAlert$5 r0 = new org.telegram.ui.Components.TranslateAlert$5
            r0.<init>(r8, r9)
            r8.textsView = r0
            r0.setOrientation(r14)
            android.widget.LinearLayout r0 = r8.textsView
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r6 = r8.textPadHorz
            int r5 = r5 - r6
            r6 = 1094713344(0x41400000, float:12.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r15 = r8.textPadVert
            int r13 = r13 - r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = r8.textPadHorz
            int r15 = r15 - r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r8.textPadVert
            int r2 = r2 - r1
            r0.setPadding(r5, r13, r15, r2)
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            java.lang.String r1 = "chat_inTextSelectionHighlight"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            org.telegram.ui.Components.TranslateAlert$6 r0 = new org.telegram.ui.Components.TranslateAlert$6
            r0.<init>(r9)
            r8.allTextsContainer = r0
            r0.setClipChildren(r12)
            android.widget.FrameLayout r0 = r8.allTextsContainer
            r0.setClipToPadding(r12)
            android.widget.FrameLayout r0 = r8.allTextsContainer
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r0.setPadding(r1, r2, r5, r6)
            org.telegram.ui.Components.TranslateAlert$7 r0 = new org.telegram.ui.Components.TranslateAlert$7
            r0.<init>(r9)
            r8.allTextsView = r0
            r0.setTextColor(r12)
            android.widget.TextView r0 = r8.allTextsView
            r0.setTextSize(r14, r4)
            android.widget.TextView r0 = r8.allTextsView
            r1 = r11 ^ 1
            r0.setTextIsSelectable(r1)
            android.widget.TextView r0 = r8.allTextsView
            java.lang.String r1 = "chat_inTextSelectionHighlight"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setHighlightColor(r1)
            java.lang.String r0 = "chat_TextSelectionCursor"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r1 = 29
            r2 = r26
            if (r2 < r1) goto L_0x04cc
            android.widget.TextView r1 = r8.allTextsView     // Catch:{ Exception -> 0x04cc }
            android.graphics.drawable.Drawable r1 = r1.getTextSelectHandleLeft()     // Catch:{ Exception -> 0x04cc }
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x04cc }
            r1.setColorFilter(r0, r2)     // Catch:{ Exception -> 0x04cc }
            android.widget.TextView r2 = r8.allTextsView     // Catch:{ Exception -> 0x04cc }
            r2.setTextSelectHandleLeft(r1)     // Catch:{ Exception -> 0x04cc }
            android.widget.TextView r1 = r8.allTextsView     // Catch:{ Exception -> 0x04cc }
            android.graphics.drawable.Drawable r1 = r1.getTextSelectHandleRight()     // Catch:{ Exception -> 0x04cc }
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x04cc }
            r1.setColorFilter(r0, r2)     // Catch:{ Exception -> 0x04cc }
            android.widget.TextView r0 = r8.allTextsView     // Catch:{ Exception -> 0x04cc }
            r0.setTextSelectHandleRight(r1)     // Catch:{ Exception -> 0x04cc }
        L_0x04cc:
            android.widget.TextView r0 = r8.allTextsView
            android.text.method.LinkMovementMethod r1 = new android.text.method.LinkMovementMethod
            r1.<init>()
            r0.setMovementMethod(r1)
            android.widget.FrameLayout r0 = r8.allTextsContainer
            android.widget.TextView r1 = r8.allTextsView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r3)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r9)
            r8.textsContainerView = r0
            android.widget.FrameLayout r1 = r8.allTextsContainer
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r3)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r8.textsContainerView
            android.widget.LinearLayout r1 = r8.textsView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r3)
            r0.addView(r1, r2)
            androidx.core.widget.NestedScrollView r0 = r8.scrollView
            android.widget.FrameLayout r1 = r8.textsContainerView
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = -2
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r3, (float) r2)
            r0.addView((android.view.View) r1, (android.view.ViewGroup.LayoutParams) r2)
            android.widget.FrameLayout r0 = r8.container
            androidx.core.widget.NestedScrollView r1 = r8.scrollView
            r2 = -1
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = 119(0x77, float:1.67E-43)
            r5 = 0
            r6 = 1116471296(0x428CLASSNAME, float:70.0)
            r10 = 0
            r11 = 1117913088(0x42a20000, float:81.0)
            r38 = r2
            r39 = r3
            r40 = r4
            r41 = r5
            r42 = r6
            r43 = r10
            r44 = r11
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r38, r39, r40, r41, r42, r43, r44)
            r8.scrollViewLayout = r2
            r0.addView(r1, r2)
            r34.fetchNext()
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r9)
            r8.buttonShadowView = r0
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r8.container
            android.widget.FrameLayout r1 = r8.buttonShadowView
            r2 = -1
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 87
            r6 = 0
            r11 = 1117782016(0x42a00000, float:80.0)
            r38 = r2
            r39 = r3
            r40 = r4
            r42 = r6
            r44 = r11
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r38, r39, r40, r41, r42, r43, r44)
            r0.addView(r1, r2)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.buttonTextView = r0
            r0.setLines(r14)
            android.widget.TextView r0 = r8.buttonTextView
            r0.setSingleLine(r14)
            android.widget.TextView r0 = r8.buttonTextView
            r0.setGravity(r14)
            android.widget.TextView r0 = r8.buttonTextView
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r8.buttonTextView
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r8.buttonTextView
            java.lang.String r1 = "featuredStickers_buttonText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r8.buttonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r8.buttonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r14, r1)
            android.widget.TextView r0 = r8.buttonTextView
            r1 = 2131625012(0x7f0e0434, float:1.887722E38)
            java.lang.String r2 = "CloseTranslation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r9)
            r8.buttonView = r0
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r2 = "featuredStickers_addButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r3 = "featuredStickers_addButtonPressed"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r1, r2, r3)
            r0.setBackground(r1)
            android.widget.FrameLayout r0 = r8.buttonView
            android.widget.TextView r1 = r8.buttonTextView
            r0.addView(r1)
            android.widget.FrameLayout r0 = r8.buttonView
            org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3 r1 = new org.telegram.ui.Components.TranslateAlert$$ExternalSyntheticLambda3
            r1.<init>(r8)
            r0.setOnClickListener(r1)
            android.widget.FrameLayout r0 = r8.container
            android.widget.FrameLayout r1 = r8.buttonView
            r2 = -1
            r3 = 1111490560(0x42400000, float:48.0)
            r4 = 80
            r5 = 1098907648(0x41800000, float:16.0)
            r6 = 1098907648(0x41800000, float:16.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r11 = 1098907648(0x41800000, float:16.0)
            r38 = r2
            r39 = r3
            r40 = r4
            r41 = r5
            r42 = r6
            r43 = r10
            r44 = r11
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r38, r39, r40, r41, r42, r43, r44)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r8.contentView
            android.widget.FrameLayout r1 = r8.container
            r2 = 81
            r3 = -2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r3, r2)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r9)
            r8.bulletinContainer = r0
            android.widget.FrameLayout r1 = r8.contentView
            r2 = -1
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = 119(0x77, float:1.67E-43)
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 1117913088(0x42a20000, float:81.0)
            r35 = r2
            r36 = r3
            r37 = r4
            r38 = r5
            r39 = r6
            r40 = r7
            r41 = r9
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r35, r36, r37, r38, r39, r40, r41)
            r1.addView(r0, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, int, org.telegram.tgnet.TLRPC$InputPeer, int, java.lang.String, java.lang.String, java.lang.CharSequence, boolean, org.telegram.ui.Components.TranslateAlert$OnLinkPress):void");
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

    /* access modifiers changed from: private */
    public boolean scrollAtBottom() {
        NestedScrollView nestedScrollView = this.scrollView;
        int bottom = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1).getBottom();
        if (this.textsView.getChildCount() > 0) {
            LinearLayout linearLayout = this.textsView;
            View childAt = linearLayout.getChildAt(linearLayout.getChildCount() - 1);
            if ((childAt instanceof LoadingTextView) && !((LoadingTextView) childAt).loaded) {
                bottom = childAt.getTop();
            }
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
        openAnimationTo(f, false);
        openTo(f + 1.0f, false);
    }

    private float getScrollY() {
        return Math.max(Math.min(this.containerOpenAnimationT - (1.0f - this.openingT), 1.0f), 0.0f);
    }

    private boolean hasSelection() {
        return this.allTextsView.hasSelection();
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0142 A[Catch:{ Exception -> 0x02b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0191 A[Catch:{ Exception -> 0x02b0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchTouchEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            float r0 = r13.getX()     // Catch:{ Exception -> 0x02b0 }
            float r1 = r13.getY()     // Catch:{ Exception -> 0x02b0 }
            android.widget.FrameLayout r2 = r12.container     // Catch:{ Exception -> 0x02b0 }
            android.graphics.Rect r3 = r12.containerRect     // Catch:{ Exception -> 0x02b0 }
            r2.getGlobalVisibleRect(r3)     // Catch:{ Exception -> 0x02b0 }
            android.graphics.Rect r2 = r12.containerRect     // Catch:{ Exception -> 0x02b0 }
            int r3 = (int) r0     // Catch:{ Exception -> 0x02b0 }
            int r4 = (int) r1     // Catch:{ Exception -> 0x02b0 }
            boolean r2 = r2.contains(r3, r4)     // Catch:{ Exception -> 0x02b0 }
            r5 = 0
            r6 = 1
            if (r2 != 0) goto L_0x0034
            int r2 = r13.getAction()     // Catch:{ Exception -> 0x02b0 }
            if (r2 != 0) goto L_0x0024
            r12.pressedOutside = r6     // Catch:{ Exception -> 0x02b0 }
            return r6
        L_0x0024:
            int r2 = r13.getAction()     // Catch:{ Exception -> 0x02b0 }
            if (r2 != r6) goto L_0x0034
            boolean r2 = r12.pressedOutside     // Catch:{ Exception -> 0x02b0 }
            if (r2 == 0) goto L_0x0034
            r12.pressedOutside = r5     // Catch:{ Exception -> 0x02b0 }
            r12.dismiss()     // Catch:{ Exception -> 0x02b0 }
            return r6
        L_0x0034:
            android.widget.FrameLayout r2 = r12.allTextsContainer     // Catch:{ Exception -> 0x0106 }
            android.graphics.Rect r7 = r12.textRect     // Catch:{ Exception -> 0x0106 }
            r2.getGlobalVisibleRect(r7)     // Catch:{ Exception -> 0x0106 }
            android.graphics.Rect r2 = r12.textRect     // Catch:{ Exception -> 0x0106 }
            boolean r2 = r2.contains(r3, r4)     // Catch:{ Exception -> 0x0106 }
            r7 = 0
            if (r2 == 0) goto L_0x00fa
            boolean r2 = r12.scrolling     // Catch:{ Exception -> 0x0106 }
            if (r2 != 0) goto L_0x00fa
            android.widget.TextView r2 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            android.text.Layout r2 = r2.getLayout()     // Catch:{ Exception -> 0x0106 }
            android.widget.TextView r8 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            int r8 = r8.getLeft()     // Catch:{ Exception -> 0x0106 }
            float r8 = (float) r8     // Catch:{ Exception -> 0x0106 }
            float r0 = r0 - r8
            android.widget.FrameLayout r8 = r12.container     // Catch:{ Exception -> 0x0106 }
            int r8 = r8.getLeft()     // Catch:{ Exception -> 0x0106 }
            float r8 = (float) r8     // Catch:{ Exception -> 0x0106 }
            float r0 = r0 - r8
            int r0 = (int) r0     // Catch:{ Exception -> 0x0106 }
            android.widget.TextView r8 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            int r8 = r8.getTop()     // Catch:{ Exception -> 0x0106 }
            float r8 = (float) r8     // Catch:{ Exception -> 0x0106 }
            float r8 = r1 - r8
            android.widget.FrameLayout r9 = r12.container     // Catch:{ Exception -> 0x0106 }
            int r9 = r9.getTop()     // Catch:{ Exception -> 0x0106 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x0106 }
            float r8 = r8 - r9
            androidx.core.widget.NestedScrollView r9 = r12.scrollView     // Catch:{ Exception -> 0x0106 }
            int r9 = r9.getTop()     // Catch:{ Exception -> 0x0106 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x0106 }
            float r8 = r8 - r9
            androidx.core.widget.NestedScrollView r9 = r12.scrollView     // Catch:{ Exception -> 0x0106 }
            int r9 = r9.getScrollY()     // Catch:{ Exception -> 0x0106 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x0106 }
            float r8 = r8 + r9
            int r8 = (int) r8     // Catch:{ Exception -> 0x0106 }
            int r8 = r2.getLineForVertical(r8)     // Catch:{ Exception -> 0x0106 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0106 }
            int r9 = r2.getOffsetForHorizontal(r8, r0)     // Catch:{ Exception -> 0x0106 }
            float r10 = r2.getLineLeft(r8)     // Catch:{ Exception -> 0x0106 }
            android.text.Spannable r11 = r12.allTexts     // Catch:{ Exception -> 0x0106 }
            if (r11 == 0) goto L_0x00ee
            boolean r11 = r11 instanceof android.text.Spannable     // Catch:{ Exception -> 0x0106 }
            if (r11 == 0) goto L_0x00ee
            int r11 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r11 > 0) goto L_0x00ee
            float r2 = r2.getLineWidth(r8)     // Catch:{ Exception -> 0x0106 }
            float r10 = r10 + r2
            int r0 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x00ee
            android.text.Spannable r0 = r12.allTexts     // Catch:{ Exception -> 0x0106 }
            java.lang.Class<android.text.style.ClickableSpan> r2 = android.text.style.ClickableSpan.class
            java.lang.Object[] r0 = r0.getSpans(r9, r9, r2)     // Catch:{ Exception -> 0x0106 }
            android.text.style.ClickableSpan[] r0 = (android.text.style.ClickableSpan[]) r0     // Catch:{ Exception -> 0x0106 }
            if (r0 == 0) goto L_0x00e2
            int r2 = r0.length     // Catch:{ Exception -> 0x0106 }
            if (r2 < r6) goto L_0x00e2
            int r2 = r13.getAction()     // Catch:{ Exception -> 0x0106 }
            if (r2 != r6) goto L_0x00d2
            android.text.style.ClickableSpan r2 = r12.pressedLink     // Catch:{ Exception -> 0x0106 }
            r8 = r0[r5]     // Catch:{ Exception -> 0x0106 }
            if (r2 != r8) goto L_0x00d2
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            r2.onClick(r0)     // Catch:{ Exception -> 0x0106 }
            r12.pressedLink = r7     // Catch:{ Exception -> 0x0106 }
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            boolean r2 = r12.noforwards     // Catch:{ Exception -> 0x0106 }
            if (r2 != 0) goto L_0x00cd
            r2 = 1
            goto L_0x00ce
        L_0x00cd:
            r2 = 0
        L_0x00ce:
            r0.setTextIsSelectable(r2)     // Catch:{ Exception -> 0x0106 }
            goto L_0x00dc
        L_0x00d2:
            int r2 = r13.getAction()     // Catch:{ Exception -> 0x0106 }
            if (r2 != 0) goto L_0x00dc
            r0 = r0[r5]     // Catch:{ Exception -> 0x0106 }
            r12.pressedLink = r0     // Catch:{ Exception -> 0x0106 }
        L_0x00dc:
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            r0.invalidate()     // Catch:{ Exception -> 0x0106 }
            return r6
        L_0x00e2:
            android.text.style.ClickableSpan r0 = r12.pressedLink     // Catch:{ Exception -> 0x0106 }
            if (r0 == 0) goto L_0x010a
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            r0.invalidate()     // Catch:{ Exception -> 0x0106 }
            r12.pressedLink = r7     // Catch:{ Exception -> 0x0106 }
            goto L_0x010a
        L_0x00ee:
            android.text.style.ClickableSpan r0 = r12.pressedLink     // Catch:{ Exception -> 0x0106 }
            if (r0 == 0) goto L_0x010a
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            r0.invalidate()     // Catch:{ Exception -> 0x0106 }
            r12.pressedLink = r7     // Catch:{ Exception -> 0x0106 }
            goto L_0x010a
        L_0x00fa:
            android.text.style.ClickableSpan r0 = r12.pressedLink     // Catch:{ Exception -> 0x0106 }
            if (r0 == 0) goto L_0x010a
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x0106 }
            r0.invalidate()     // Catch:{ Exception -> 0x0106 }
            r12.pressedLink = r7     // Catch:{ Exception -> 0x0106 }
            goto L_0x010a
        L_0x0106:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ Exception -> 0x02b0 }
        L_0x010a:
            androidx.core.widget.NestedScrollView r0 = r12.scrollView     // Catch:{ Exception -> 0x02b0 }
            android.graphics.Rect r2 = r12.scrollRect     // Catch:{ Exception -> 0x02b0 }
            r0.getGlobalVisibleRect(r2)     // Catch:{ Exception -> 0x02b0 }
            android.widget.ImageView r0 = r12.backButton     // Catch:{ Exception -> 0x02b0 }
            android.graphics.Rect r2 = r12.backRect     // Catch:{ Exception -> 0x02b0 }
            r0.getGlobalVisibleRect(r2)     // Catch:{ Exception -> 0x02b0 }
            android.widget.FrameLayout r0 = r12.buttonView     // Catch:{ Exception -> 0x02b0 }
            android.graphics.Rect r2 = r12.buttonRect     // Catch:{ Exception -> 0x02b0 }
            r0.getGlobalVisibleRect(r2)     // Catch:{ Exception -> 0x02b0 }
            r12.fromTranslateMoreView = r5     // Catch:{ Exception -> 0x02b0 }
            android.text.style.ClickableSpan r0 = r12.pressedLink     // Catch:{ Exception -> 0x02b0 }
            if (r0 != 0) goto L_0x0285
            boolean r0 = r12.hasSelection()     // Catch:{ Exception -> 0x02b0 }
            if (r0 != 0) goto L_0x0285
            android.graphics.Rect r0 = r12.backRect     // Catch:{ Exception -> 0x02b0 }
            boolean r0 = r0.contains(r3, r4)     // Catch:{ Exception -> 0x02b0 }
            r2 = 0
            if (r0 != 0) goto L_0x0191
            android.graphics.Rect r0 = r12.buttonRect     // Catch:{ Exception -> 0x02b0 }
            boolean r0 = r0.contains(r3, r4)     // Catch:{ Exception -> 0x02b0 }
            if (r0 != 0) goto L_0x0191
            int r0 = r13.getAction()     // Catch:{ Exception -> 0x02b0 }
            if (r0 != 0) goto L_0x0191
            android.graphics.Rect r0 = r12.scrollRect     // Catch:{ Exception -> 0x02b0 }
            boolean r0 = r0.contains(r3, r4)     // Catch:{ Exception -> 0x02b0 }
            if (r0 == 0) goto L_0x0158
            float r0 = r12.containerOpenAnimationT     // Catch:{ Exception -> 0x02b0 }
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 > 0) goto L_0x0156
            boolean r0 = r12.canExpand()     // Catch:{ Exception -> 0x02b0 }
            if (r0 != 0) goto L_0x0158
        L_0x0156:
            r0 = 1
            goto L_0x0159
        L_0x0158:
            r0 = 0
        L_0x0159:
            r12.fromScrollRect = r0     // Catch:{ Exception -> 0x02b0 }
            r12.maybeScrolling = r6     // Catch:{ Exception -> 0x02b0 }
            android.graphics.Rect r0 = r12.scrollRect     // Catch:{ Exception -> 0x02b0 }
            boolean r0 = r0.contains(r3, r4)     // Catch:{ Exception -> 0x02b0 }
            if (r0 == 0) goto L_0x017a
            android.widget.LinearLayout r0 = r12.textsView     // Catch:{ Exception -> 0x02b0 }
            int r0 = r0.getChildCount()     // Catch:{ Exception -> 0x02b0 }
            if (r0 <= 0) goto L_0x017a
            android.widget.LinearLayout r0 = r12.textsView     // Catch:{ Exception -> 0x02b0 }
            android.view.View r0 = r0.getChildAt(r5)     // Catch:{ Exception -> 0x02b0 }
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r0 = (org.telegram.ui.Components.TranslateAlert.LoadingTextView) r0     // Catch:{ Exception -> 0x02b0 }
            boolean r0 = r0.loaded     // Catch:{ Exception -> 0x02b0 }
            if (r0 != 0) goto L_0x017a
            r5 = 1
        L_0x017a:
            r12.scrolling = r5     // Catch:{ Exception -> 0x02b0 }
            r12.fromY = r1     // Catch:{ Exception -> 0x02b0 }
            float r0 = r12.getScrollY()     // Catch:{ Exception -> 0x02b0 }
            r12.fromScrollY = r0     // Catch:{ Exception -> 0x02b0 }
            androidx.core.widget.NestedScrollView r0 = r12.scrollView     // Catch:{ Exception -> 0x02b0 }
            int r0 = r0.getScrollY()     // Catch:{ Exception -> 0x02b0 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x02b0 }
            r12.fromScrollViewY = r0     // Catch:{ Exception -> 0x02b0 }
            super.dispatchTouchEvent(r13)     // Catch:{ Exception -> 0x02b0 }
            return r6
        L_0x0191:
            boolean r0 = r12.maybeScrolling     // Catch:{ Exception -> 0x02b0 }
            if (r0 == 0) goto L_0x0285
            int r0 = r13.getAction()     // Catch:{ Exception -> 0x02b0 }
            r3 = 2
            if (r0 == r3) goto L_0x01a2
            int r0 = r13.getAction()     // Catch:{ Exception -> 0x02b0 }
            if (r0 != r6) goto L_0x0285
        L_0x01a2:
            float r0 = r12.fromY     // Catch:{ Exception -> 0x02b0 }
            float r0 = r0 - r1
            boolean r1 = r12.fromScrollRect     // Catch:{ Exception -> 0x02b0 }
            if (r1 == 0) goto L_0x01c6
            float r1 = r12.fromScrollViewY     // Catch:{ Exception -> 0x02b0 }
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x02b0 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x02b0 }
            float r1 = r1 + r3
            float r1 = -r1
            float r1 = r1 - r0
            float r0 = java.lang.Math.max(r2, r1)     // Catch:{ Exception -> 0x02b0 }
            float r0 = -r0
            int r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x01e7
            r12.scrolling = r6     // Catch:{ Exception -> 0x02b0 }
            android.widget.TextView r1 = r12.allTextsView     // Catch:{ Exception -> 0x02b0 }
            r1.setTextIsSelectable(r5)     // Catch:{ Exception -> 0x02b0 }
            goto L_0x01e7
        L_0x01c6:
            float r1 = java.lang.Math.abs(r0)     // Catch:{ Exception -> 0x02b0 }
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x02b0 }
            float r3 = (float) r3     // Catch:{ Exception -> 0x02b0 }
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x01e7
            boolean r1 = r12.fromScrollRect     // Catch:{ Exception -> 0x02b0 }
            if (r1 != 0) goto L_0x01e7
            r12.scrolling = r6     // Catch:{ Exception -> 0x02b0 }
            android.widget.TextView r1 = r12.allTextsView     // Catch:{ Exception -> 0x02b0 }
            r1.setTextIsSelectable(r5)     // Catch:{ Exception -> 0x02b0 }
            androidx.core.widget.NestedScrollView r1 = r12.scrollView     // Catch:{ Exception -> 0x02b0 }
            r1.stopNestedScroll()     // Catch:{ Exception -> 0x02b0 }
            r12.allowScroll = r5     // Catch:{ Exception -> 0x02b0 }
        L_0x01e7:
            android.util.DisplayMetrics r1 = org.telegram.messenger.AndroidUtilities.displayMetrics     // Catch:{ Exception -> 0x02b0 }
            int r1 = r1.heightPixels     // Catch:{ Exception -> 0x02b0 }
            float r1 = (float) r1     // Catch:{ Exception -> 0x02b0 }
            float r3 = r12.heightMaxPercent     // Catch:{ Exception -> 0x02b0 }
            float r3 = r3 * r1
            float r3 = java.lang.Math.min(r1, r3)     // Catch:{ Exception -> 0x02b0 }
            float r4 = r12.fromScrollY     // Catch:{ Exception -> 0x02b0 }
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            float r4 = java.lang.Math.max(r4, r7)     // Catch:{ Exception -> 0x02b0 }
            float r4 = java.lang.Math.min(r4, r2)     // Catch:{ Exception -> 0x02b0 }
            float r4 = -r4
            r8 = 1065353216(0x3var_, float:1.0)
            float r4 = r8 - r4
            float r4 = r4 * r3
            float r1 = r1 - r3
            float r9 = r12.fromScrollY     // Catch:{ Exception -> 0x02b0 }
            float r9 = java.lang.Math.max(r9, r2)     // Catch:{ Exception -> 0x02b0 }
            float r9 = java.lang.Math.min(r8, r9)     // Catch:{ Exception -> 0x02b0 }
            float r9 = r9 * r1
            float r4 = r4 + r9
            float r4 = r4 + r0
            int r9 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r9 <= 0) goto L_0x021d
            float r4 = r4 - r3
            float r4 = r4 / r1
            goto L_0x0221
        L_0x021d:
            float r4 = r4 / r3
            float r1 = r8 - r4
            float r4 = -r1
        L_0x0221:
            boolean r1 = r12.canExpand()     // Catch:{ Exception -> 0x02b0 }
            if (r1 != 0) goto L_0x022b
            float r4 = java.lang.Math.min(r4, r2)     // Catch:{ Exception -> 0x02b0 }
        L_0x022b:
            r12.updateCanExpand()     // Catch:{ Exception -> 0x02b0 }
            boolean r1 = r12.scrolling     // Catch:{ Exception -> 0x02b0 }
            if (r1 == 0) goto L_0x0285
            r12.setScrollY(r4)     // Catch:{ Exception -> 0x02b0 }
            int r1 = r13.getAction()     // Catch:{ Exception -> 0x02b0 }
            if (r1 != r6) goto L_0x0284
            r12.scrolling = r5     // Catch:{ Exception -> 0x02b0 }
            android.widget.TextView r1 = r12.allTextsView     // Catch:{ Exception -> 0x02b0 }
            boolean r2 = r12.noforwards     // Catch:{ Exception -> 0x02b0 }
            if (r2 != 0) goto L_0x0245
            r2 = 1
            goto L_0x0246
        L_0x0245:
            r2 = 0
        L_0x0246:
            r1.setTextIsSelectable(r2)     // Catch:{ Exception -> 0x02b0 }
            r12.maybeScrolling = r5     // Catch:{ Exception -> 0x02b0 }
            r12.allowScroll = r6     // Catch:{ Exception -> 0x02b0 }
            float r0 = java.lang.Math.abs(r0)     // Catch:{ Exception -> 0x02b0 }
            r1 = 1098907648(0x41800000, float:16.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)     // Catch:{ Exception -> 0x02b0 }
            float r1 = (float) r1     // Catch:{ Exception -> 0x02b0 }
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x027a
            float r0 = r12.fromScrollY     // Catch:{ Exception -> 0x02b0 }
            int r0 = java.lang.Math.round(r0)     // Catch:{ Exception -> 0x02b0 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x02b0 }
            float r1 = r12.fromScrollY     // Catch:{ Exception -> 0x02b0 }
            int r2 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x026b
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x026b:
            float r1 = r1 - r4
            float r1 = java.lang.Math.abs(r1)     // Catch:{ Exception -> 0x02b0 }
            double r1 = (double) r1     // Catch:{ Exception -> 0x02b0 }
            double r1 = java.lang.Math.ceil(r1)     // Catch:{ Exception -> 0x02b0 }
            float r1 = (float) r1     // Catch:{ Exception -> 0x02b0 }
            float r7 = r7 * r1
            float r0 = r0 + r7
            goto L_0x0281
        L_0x027a:
            float r0 = r12.fromScrollY     // Catch:{ Exception -> 0x02b0 }
            int r0 = java.lang.Math.round(r0)     // Catch:{ Exception -> 0x02b0 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x02b0 }
        L_0x0281:
            r12.scrollYTo(r0)     // Catch:{ Exception -> 0x02b0 }
        L_0x0284:
            return r6
        L_0x0285:
            boolean r0 = r12.hasSelection()     // Catch:{ Exception -> 0x02b0 }
            if (r0 == 0) goto L_0x02ab
            boolean r0 = r12.maybeScrolling     // Catch:{ Exception -> 0x02b0 }
            if (r0 == 0) goto L_0x02ab
            r12.scrolling = r5     // Catch:{ Exception -> 0x02b0 }
            android.widget.TextView r0 = r12.allTextsView     // Catch:{ Exception -> 0x02b0 }
            boolean r1 = r12.noforwards     // Catch:{ Exception -> 0x02b0 }
            if (r1 != 0) goto L_0x0299
            r1 = 1
            goto L_0x029a
        L_0x0299:
            r1 = 0
        L_0x029a:
            r0.setTextIsSelectable(r1)     // Catch:{ Exception -> 0x02b0 }
            r12.maybeScrolling = r5     // Catch:{ Exception -> 0x02b0 }
            r12.allowScroll = r6     // Catch:{ Exception -> 0x02b0 }
            float r0 = r12.fromScrollY     // Catch:{ Exception -> 0x02b0 }
            int r0 = java.lang.Math.round(r0)     // Catch:{ Exception -> 0x02b0 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x02b0 }
            r12.scrollYTo(r0)     // Catch:{ Exception -> 0x02b0 }
        L_0x02ab:
            boolean r13 = super.dispatchTouchEvent(r13)     // Catch:{ Exception -> 0x02b0 }
            return r13
        L_0x02b0:
            r0 = move-exception
            r0.printStackTrace()
            boolean r13 = super.dispatchTouchEvent(r13)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    private LoadingTextView addBlock(CharSequence charSequence, boolean z) {
        AnonymousClass8 r0 = new LoadingTextView(getContext(), this.textPadHorz, this.textPadVert, charSequence, z, false) {
            /* access modifiers changed from: protected */
            public void onLoadStart() {
                TranslateAlert.this.allTextsView.clearFocus();
            }

            /* access modifiers changed from: protected */
            public void onLoadEnd() {
                TranslateAlert.this.scrollView.post(new TranslateAlert$8$$ExternalSyntheticLambda0(this));
                TranslateAlert.this.contentView.post(new TranslateAlert$8$$ExternalSyntheticLambda1(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLoadEnd$0() {
                TranslateAlert.this.allTextsView.setText(TranslateAlert.this.allTexts);
                TranslateAlert.this.allTextsView.measure(View.MeasureSpec.makeMeasureSpec((TranslateAlert.this.allTextsContainer.getWidth() - TranslateAlert.this.allTextsContainer.getPaddingLeft()) - TranslateAlert.this.allTextsContainer.getPaddingRight(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(TranslateAlert.this.textsView.getHeight(), Integer.MIN_VALUE));
                TranslateAlert.this.allTextsView.layout(TranslateAlert.this.allTextsContainer.getLeft() + TranslateAlert.this.allTextsContainer.getPaddingLeft(), TranslateAlert.this.allTextsContainer.getTop() + TranslateAlert.this.allTextsContainer.getPaddingTop(), TranslateAlert.this.allTextsContainer.getLeft() + TranslateAlert.this.allTextsContainer.getPaddingLeft() + TranslateAlert.this.allTextsView.getMeasuredWidth(), TranslateAlert.this.allTextsContainer.getTop() + TranslateAlert.this.allTextsContainer.getPaddingTop() + TranslateAlert.this.allTextsView.getMeasuredHeight());
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLoadEnd$1() {
                if (TranslateAlert.this.scrollAtBottom()) {
                    boolean unused = TranslateAlert.this.fetchNext();
                }
            }
        };
        r0.setTextColor(Theme.getColor("dialogTextBlack"));
        r0.setTextSize(AndroidUtilities.dp(16.0f));
        r0.setTranslationY(((float) this.textsView.getChildCount()) * ((((float) this.textPadVert) * -4.0f) + ((float) AndroidUtilities.dp(0.48f))));
        LinearLayout linearLayout = this.textsView;
        linearLayout.addView(r0, linearLayout.getChildCount(), LayoutHelper.createLinear(-1, -1, 0, 0, 0, 0, 0));
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
                        TranslateAlert.this.allTextsView.setTextIsSelectable(!TranslateAlert.this.noforwards);
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
    public /* synthetic */ void lambda$openTo$4(ValueAnimator valueAnimator) {
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
        if (str != null && !str.equals("und") && !str.equals("auto")) {
            LocaleController.LocaleInfo builtinLanguageByPlural = LocaleController.getInstance().getBuiltinLanguageByPlural(str);
            boolean z = false;
            try {
                z = LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode.equals("en");
            } catch (Exception unused) {
            }
            if (builtinLanguageByPlural != null && ((z && builtinLanguageByPlural.nameEnglish != null) || (!z && builtinLanguageByPlural.name != null))) {
                return z ? builtinLanguageByPlural.nameEnglish : builtinLanguageByPlural.name;
            }
        }
        return null;
    }

    public void updateSourceLanguage() {
        if (languageName(this.fromLanguage) != null) {
            this.subtitleView.setAlpha(1.0f);
            LoadingTextView loadingTextView = this.subtitleFromView;
            if (!loadingTextView.loaded) {
                loadingTextView.setText(languageName(this.fromLanguage));
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
            int i2 = lastIndexOf + 1;
            arrayList.add(charSequence.subSequence(0, i2));
            charSequence = charSequence.subSequence(i2, charSequence.length());
        }
        if (charSequence.length() > 0) {
            arrayList.add(charSequence);
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public boolean fetchNext() {
        boolean z = false;
        if (this.loading) {
            return false;
        }
        this.loading = true;
        showTranslateMoreView(false);
        if (this.blockIndex >= this.textBlocks.size()) {
            return false;
        }
        CharSequence charSequence = this.textBlocks.get(this.blockIndex);
        LoadingTextView loadingTextView = this.lastLoadingBlock;
        if (loadingTextView == null) {
            if (this.blockIndex != 0) {
                z = true;
            }
            loadingTextView = addBlock(charSequence, z);
        }
        this.lastLoadingBlock = loadingTextView;
        loadingTextView.loading = true;
        fetchTranslation(charSequence, new TranslateAlert$$ExternalSyntheticLambda7(this), new TranslateAlert$$ExternalSyntheticLambda6(this));
        return true;
    }

    /* JADX WARNING: type inference failed for: r1v22, types: [android.text.Spannable] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fetchNext$5(java.lang.String r11, java.lang.String r12) {
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
            org.telegram.ui.Components.TranslateAlert$11 r5 = new org.telegram.ui.Components.TranslateAlert$11     // Catch:{ Exception -> 0x008a }
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
            org.telegram.ui.Components.TranslateAlert$12 r9 = new org.telegram.ui.Components.TranslateAlert$12     // Catch:{ Exception -> 0x008a }
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
            android.widget.TextView r2 = r10.allTextsView
            java.lang.CharSequence r2 = r2.getText()
            r1.<init>(r2)
            int r2 = r10.blockIndex
            if (r2 != 0) goto L_0x00a0
            java.lang.String r2 = ""
            goto L_0x00a2
        L_0x00a0:
            java.lang.String r2 = "\n"
        L_0x00a2:
            android.text.SpannableStringBuilder r1 = r1.append(r2)
            android.text.SpannableStringBuilder r1 = r1.append(r7)
            r10.allTexts = r1
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r1 = r10.lastLoadingBlock
            if (r1 == 0) goto L_0x00b6
            r1.setText(r7)
            r1 = 0
            r10.lastLoadingBlock = r1
        L_0x00b6:
            r10.fromLanguage = r12
            r10.updateSourceLanguage()
            int r12 = r10.blockIndex
            int r12 = r12 + r0
            r10.blockIndex = r12
            java.util.ArrayList<java.lang.CharSequence> r1 = r10.textBlocks
            int r1 = r1.size()
            if (r12 >= r1) goto L_0x00ca
            r12 = 1
            goto L_0x00cb
        L_0x00ca:
            r12 = 0
        L_0x00cb:
            r10.showTranslateMoreView(r12)
            r10.loading = r11
            int r12 = r10.blockIndex
            java.util.ArrayList<java.lang.CharSequence> r1 = r10.textBlocks
            int r1 = r1.size()
            if (r12 >= r1) goto L_0x00ec
            java.util.ArrayList<java.lang.CharSequence> r12 = r10.textBlocks
            int r1 = r10.blockIndex
            java.lang.Object r12 = r12.get(r1)
            java.lang.CharSequence r12 = (java.lang.CharSequence) r12
            org.telegram.ui.Components.TranslateAlert$LoadingTextView r12 = r10.addBlock(r12, r0)
            r10.lastLoadingBlock = r12
            r12.loading = r11
        L_0x00ec:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.lambda$fetchNext$5(java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNext$6(boolean z) {
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
            /* JADX WARNING: Missing exception handler attribute for start block: B:46:0x0181 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r13 = this;
                    java.lang.String r0 = "-"
                    long r1 = android.os.SystemClock.elapsedRealtime()
                    r3 = 1
                    r4 = 0
                    r5 = 0
                    java.lang.String r6 = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r7.<init>()     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    org.telegram.ui.Components.TranslateAlert r6 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r6.fromLanguage     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = android.net.Uri.encode(r6)     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0184 }
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r7.<init>()     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = "&tl="
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0184 }
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r7.<init>()     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    org.telegram.ui.Components.TranslateAlert r6 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r6.toLanguage     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = android.net.Uri.encode(r6)     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0184 }
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r7.<init>()     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q="
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0184 }
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
                    r7.<init>()     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.CharSequence r6 = r2     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = android.net.Uri.encode(r6)     // Catch:{ Exception -> 0x0184 }
                    r7.append(r6)     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r6 = r7.toString()     // Catch:{ Exception -> 0x0184 }
                    java.net.URI r7 = new java.net.URI     // Catch:{ Exception -> 0x0184 }
                    r7.<init>(r6)     // Catch:{ Exception -> 0x0184 }
                    java.net.URL r6 = r7.toURL()     // Catch:{ Exception -> 0x0184 }
                    java.net.URLConnection r6 = r6.openConnection()     // Catch:{ Exception -> 0x0184 }
                    java.net.HttpURLConnection r6 = (java.net.HttpURLConnection) r6     // Catch:{ Exception -> 0x0184 }
                    java.lang.String r7 = "GET"
                    r6.setRequestMethod(r7)     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r7 = "User-Agent"
                    org.telegram.ui.Components.TranslateAlert r8 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0182 }
                    java.lang.String[] r8 = r8.userAgents     // Catch:{ Exception -> 0x0182 }
                    double r9 = java.lang.Math.random()     // Catch:{ Exception -> 0x0182 }
                    org.telegram.ui.Components.TranslateAlert r11 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0182 }
                    java.lang.String[] r11 = r11.userAgents     // Catch:{ Exception -> 0x0182 }
                    int r11 = r11.length     // Catch:{ Exception -> 0x0182 }
                    int r11 = r11 - r3
                    double r11 = (double) r11
                    java.lang.Double.isNaN(r11)
                    double r9 = r9 * r11
                    long r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x0182 }
                    int r10 = (int) r9     // Catch:{ Exception -> 0x0182 }
                    r8 = r8[r10]     // Catch:{ Exception -> 0x0182 }
                    r6.setRequestProperty(r7, r8)     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r7 = "Content-Type"
                    java.lang.String r8 = "application/json"
                    r6.setRequestProperty(r7, r8)     // Catch:{ Exception -> 0x0182 }
                    java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0182 }
                    r7.<init>()     // Catch:{ Exception -> 0x0182 }
                    java.io.BufferedReader r8 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0182 }
                    java.io.InputStreamReader r9 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0182 }
                    java.io.InputStream r10 = r6.getInputStream()     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r11 = "UTF-8"
                    java.nio.charset.Charset r11 = java.nio.charset.Charset.forName(r11)     // Catch:{ Exception -> 0x0182 }
                    r9.<init>(r10, r11)     // Catch:{ Exception -> 0x0182 }
                    r8.<init>(r9)     // Catch:{ Exception -> 0x0182 }
                L_0x00d0:
                    int r9 = r8.read()     // Catch:{ all -> 0x017d }
                    r10 = -1
                    if (r9 == r10) goto L_0x00dc
                    char r9 = (char) r9     // Catch:{ all -> 0x017d }
                    r7.append(r9)     // Catch:{ all -> 0x017d }
                    goto L_0x00d0
                L_0x00dc:
                    r8.close()     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0182 }
                    org.json.JSONTokener r8 = new org.json.JSONTokener     // Catch:{ Exception -> 0x0182 }
                    r8.<init>(r7)     // Catch:{ Exception -> 0x0182 }
                    org.json.JSONArray r7 = new org.json.JSONArray     // Catch:{ Exception -> 0x0182 }
                    r7.<init>(r8)     // Catch:{ Exception -> 0x0182 }
                    org.json.JSONArray r8 = r7.getJSONArray(r5)     // Catch:{ Exception -> 0x0182 }
                    r9 = 2
                    java.lang.String r7 = r7.getString(r9)     // Catch:{ Exception -> 0x00f7 }
                    goto L_0x00f8
                L_0x00f7:
                    r7 = r4
                L_0x00f8:
                    if (r7 == 0) goto L_0x0108
                    boolean r9 = r7.contains(r0)     // Catch:{ Exception -> 0x0182 }
                    if (r9 == 0) goto L_0x0108
                    int r0 = r7.indexOf(r0)     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r7 = r7.substring(r5, r0)     // Catch:{ Exception -> 0x0182 }
                L_0x0108:
                    java.lang.String r0 = ""
                    r9 = 0
                L_0x010b:
                    int r10 = r8.length()     // Catch:{ Exception -> 0x0182 }
                    if (r9 >= r10) goto L_0x0135
                    org.json.JSONArray r10 = r8.getJSONArray(r9)     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r10 = r10.getString(r5)     // Catch:{ Exception -> 0x0182 }
                    if (r10 == 0) goto L_0x0132
                    java.lang.String r11 = "null"
                    boolean r11 = r10.equals(r11)     // Catch:{ Exception -> 0x0182 }
                    if (r11 != 0) goto L_0x0132
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0182 }
                    r11.<init>()     // Catch:{ Exception -> 0x0182 }
                    r11.append(r0)     // Catch:{ Exception -> 0x0182 }
                    r11.append(r10)     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r0 = r11.toString()     // Catch:{ Exception -> 0x0182 }
                L_0x0132:
                    int r9 = r9 + 1
                    goto L_0x010b
                L_0x0135:
                    java.lang.CharSequence r8 = r2     // Catch:{ Exception -> 0x0182 }
                    int r8 = r8.length()     // Catch:{ Exception -> 0x0182 }
                    if (r8 <= 0) goto L_0x0158
                    java.lang.CharSequence r8 = r2     // Catch:{ Exception -> 0x0182 }
                    char r8 = r8.charAt(r5)     // Catch:{ Exception -> 0x0182 }
                    r9 = 10
                    if (r8 != r9) goto L_0x0158
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0182 }
                    r8.<init>()     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r9 = "\n"
                    r8.append(r9)     // Catch:{ Exception -> 0x0182 }
                    r8.append(r0)     // Catch:{ Exception -> 0x0182 }
                    java.lang.String r0 = r8.toString()     // Catch:{ Exception -> 0x0182 }
                L_0x0158:
                    long r8 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0182 }
                    long r8 = r8 - r1
                    org.telegram.ui.Components.TranslateAlert r1 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0182 }
                    long r1 = r1.minFetchingDuration     // Catch:{ Exception -> 0x0182 }
                    int r10 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
                    if (r10 >= 0) goto L_0x0171
                    org.telegram.ui.Components.TranslateAlert r1 = org.telegram.ui.Components.TranslateAlert.this     // Catch:{ Exception -> 0x0182 }
                    long r1 = r1.minFetchingDuration     // Catch:{ Exception -> 0x0182 }
                    long r1 = r1 - r8
                    java.lang.Thread.sleep(r1)     // Catch:{ Exception -> 0x0182 }
                L_0x0171:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationSuccess r1 = r3     // Catch:{ Exception -> 0x0182 }
                    org.telegram.ui.Components.TranslateAlert$13$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.TranslateAlert$13$$ExternalSyntheticLambda2     // Catch:{ Exception -> 0x0182 }
                    r2.<init>(r1, r0, r7)     // Catch:{ Exception -> 0x0182 }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x0182 }
                    goto L_0x01eb
                L_0x017d:
                    r0 = move-exception
                    r8.close()     // Catch:{ all -> 0x0181 }
                L_0x0181:
                    throw r0     // Catch:{ Exception -> 0x0182 }
                L_0x0182:
                    r0 = move-exception
                    goto L_0x0186
                L_0x0184:
                    r0 = move-exception
                    r6 = r4
                L_0x0186:
                    java.lang.String r1 = "translate"
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01b7 }
                    r2.<init>()     // Catch:{ IOException -> 0x01b7 }
                    java.lang.String r7 = "failed to translate a text "
                    r2.append(r7)     // Catch:{ IOException -> 0x01b7 }
                    if (r6 == 0) goto L_0x019d
                    int r7 = r6.getResponseCode()     // Catch:{ IOException -> 0x01b7 }
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ IOException -> 0x01b7 }
                    goto L_0x019e
                L_0x019d:
                    r7 = r4
                L_0x019e:
                    r2.append(r7)     // Catch:{ IOException -> 0x01b7 }
                    java.lang.String r7 = " "
                    r2.append(r7)     // Catch:{ IOException -> 0x01b7 }
                    if (r6 == 0) goto L_0x01ac
                    java.lang.String r4 = r6.getResponseMessage()     // Catch:{ IOException -> 0x01b7 }
                L_0x01ac:
                    r2.append(r4)     // Catch:{ IOException -> 0x01b7 }
                    java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x01b7 }
                    android.util.Log.e(r1, r2)     // Catch:{ IOException -> 0x01b7 }
                    goto L_0x01bb
                L_0x01b7:
                    r1 = move-exception
                    r1.printStackTrace()
                L_0x01bb:
                    r0.printStackTrace()
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r4
                    if (r0 == 0) goto L_0x01eb
                    org.telegram.ui.Components.TranslateAlert r0 = org.telegram.ui.Components.TranslateAlert.this
                    boolean r0 = r0.dismissed
                    if (r0 != 0) goto L_0x01eb
                    if (r6 == 0) goto L_0x01d5
                    int r0 = r6.getResponseCode()     // Catch:{ Exception -> 0x01e1 }
                    r1 = 429(0x1ad, float:6.01E-43)
                    if (r0 != r1) goto L_0x01d5
                    goto L_0x01d6
                L_0x01d5:
                    r3 = 0
                L_0x01d6:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r4     // Catch:{ Exception -> 0x01e1 }
                    org.telegram.ui.Components.TranslateAlert$13$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.TranslateAlert$13$$ExternalSyntheticLambda1     // Catch:{ Exception -> 0x01e1 }
                    r1.<init>(r0, r3)     // Catch:{ Exception -> 0x01e1 }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x01e1 }
                    goto L_0x01eb
                L_0x01e1:
                    org.telegram.ui.Components.TranslateAlert$OnTranslationFail r0 = r4
                    org.telegram.ui.Components.TranslateAlert$13$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.TranslateAlert$13$$ExternalSyntheticLambda0
                    r1.<init>(r0)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
                L_0x01eb:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert.AnonymousClass13.run():void");
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
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_translateText, TranslateAlert$$ExternalSyntheticLambda5.INSTANCE);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void showAlert(Context context, BaseFragment baseFragment, int i, TLRPC$InputPeer tLRPC$InputPeer, int i2, String str, String str2, CharSequence charSequence, boolean z, OnLinkPress onLinkPress2) {
        BaseFragment baseFragment2 = baseFragment;
        TranslateAlert translateAlert = new TranslateAlert(baseFragment, context, i, tLRPC$InputPeer, i2, str, str2, charSequence, z, onLinkPress2);
        if (baseFragment2 == null) {
            translateAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(translateAlert);
        }
    }

    private static class LoadingTextView extends FrameLayout {
        private boolean animateWidth = false;
        private ValueAnimator animator;
        private Path fetchPath = new Path() {
            private boolean got = false;

            public void reset() {
                super.reset();
                this.got = false;
            }

            public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
                if (!this.got) {
                    RectF access$3300 = LoadingTextView.this.fetchedPathRect;
                    LoadingTextView loadingTextView = LoadingTextView.this;
                    int i = loadingTextView.padHorz;
                    int i2 = loadingTextView.padVert;
                    access$3300.set(f - ((float) i), f2 - ((float) i2), f3 + ((float) i), f4 + ((float) i2));
                    this.got = true;
                }
            }
        };
        /* access modifiers changed from: private */
        public RectF fetchedPathRect = new RectF();
        private float gradientWidth = ((float) AndroidUtilities.dp(350.0f));
        private Path inPath;
        public boolean loaded;
        public boolean loading;
        private ValueAnimator loadingAnimator;
        private Paint loadingIdlePaint = new Paint();
        private Paint loadingPaint = new Paint();
        private Path loadingPath = new Path();
        private CharSequence loadingString;
        private float loadingT;
        private TextView loadingTextView;
        public int padHorz = AndroidUtilities.dp(6.0f);
        public int padVert = AndroidUtilities.dp(1.5f);
        private RectF rect;
        private boolean scaleFromZero = false;
        private long scaleFromZeroStart = 0;
        private Path shadePath;
        private boolean showLoadingTextValue;
        private long start;
        private Path tempPath;
        public TextView textView = null;

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onLoadAnimation(float f) {
        }

        /* access modifiers changed from: protected */
        public void onLoadEnd() {
        }

        /* access modifiers changed from: protected */
        public void onLoadStart() {
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public LoadingTextView(Context context, int i, int i2, CharSequence charSequence, boolean z, boolean z2) {
            super(context);
            Context context2 = context;
            int i3 = i;
            int i4 = i2;
            CharSequence charSequence2 = charSequence;
            final boolean z3 = z2;
            new TextPaint();
            this.loading = true;
            this.loaded = false;
            this.loadingT = 0.0f;
            this.loadingAnimator = null;
            this.showLoadingTextValue = true;
            this.animator = null;
            this.start = SystemClock.elapsedRealtime();
            this.shadePath = new Path();
            this.tempPath = new Path();
            this.inPath = new Path();
            this.rect = new RectF();
            this.animateWidth = z3;
            this.scaleFromZero = z;
            this.scaleFromZeroStart = SystemClock.elapsedRealtime();
            this.padHorz = i3;
            this.padVert = i4;
            setPadding(i3, i4, i3, i4);
            this.loadingT = 0.0f;
            AnonymousClass2 r2 = new TextView(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3 = 999999;
                    if (z3) {
                        i = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
                    }
                    if (View.MeasureSpec.getMode(i2) != Integer.MIN_VALUE) {
                        i3 = View.MeasureSpec.getSize(i2);
                    }
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3, View.MeasureSpec.getMode(i2)));
                }
            };
            this.loadingTextView = r2;
            this.loadingString = charSequence2;
            r2.setText(charSequence2);
            this.loadingTextView.setVisibility(4);
            int i5 = 999999;
            this.loadingTextView.measure(View.MeasureSpec.makeMeasureSpec(z3 ? 999999 : getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            addView(this.loadingTextView, LayoutHelper.createFrame(-1, -2, 48));
            AnonymousClass3 r22 = new TextView(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3 = 999999;
                    if (z3) {
                        i = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
                    }
                    if (View.MeasureSpec.getMode(i2) != Integer.MIN_VALUE) {
                        i3 = View.MeasureSpec.getSize(i2);
                    }
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3, View.MeasureSpec.getMode(i2)));
                }
            };
            this.textView = r22;
            r22.setText("");
            this.textView.setVisibility(4);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(!z3 ? getWidth() : i5, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            addView(this.textView, LayoutHelper.createFrame(-1, -2, 48));
            int color = Theme.getColor("dialogBackground");
            int color2 = Theme.getColor("dialogBackgroundGray");
            this.loadingPaint.setShader(new LinearGradient(0.0f, 0.0f, this.gradientWidth, 0.0f, new int[]{color, color2, color}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT));
            this.loadingIdlePaint.setColor(color2);
            setWillNotDraw(false);
            setClipChildren(false);
            updateLoadingLayout();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateHeight();
        }

        /* access modifiers changed from: private */
        public void updateHeight() {
            int measuredHeight = this.loadingTextView.getMeasuredHeight();
            TextView textView2 = this.textView;
            int measuredHeight2 = textView2 == null ? measuredHeight : textView2.getMeasuredHeight();
            float f = 1.0f;
            if (this.scaleFromZero) {
                f = Math.max(Math.min(((float) (SystemClock.elapsedRealtime() - this.scaleFromZeroStart)) / 220.0f, 1.0f), 0.0f);
            }
            int i = (int) ((((float) ((this.padVert * 2) + measuredHeight)) + (((float) (measuredHeight2 - measuredHeight)) * this.loadingT)) * f);
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(-1, i);
            }
            layoutParams.height = i;
            if (this.animateWidth) {
                int measuredWidth = this.loadingTextView.getMeasuredWidth() + (this.padHorz * 2);
                TextView textView3 = this.textView;
                layoutParams.width = (int) ((((float) measuredWidth) + (((float) ((((textView3 == null || textView3.getMeasuredWidth() <= 0) ? this.loadingTextView : this.textView).getMeasuredWidth() + (this.padHorz * 2)) - measuredWidth)) * this.loadingT)) * f);
            }
            setLayoutParams(layoutParams);
        }

        private void updateLoadingLayout() {
            Layout layout;
            if (((float) this.loadingTextView.getMeasuredWidth()) > 0.0f) {
                TextView textView2 = this.loadingTextView;
                if (!(textView2 == null || (layout = textView2.getLayout()) == null)) {
                    for (int i = 0; i < layout.getLineCount(); i++) {
                        int lineStart = layout.getLineStart(i);
                        int lineEnd = layout.getLineEnd(i);
                        if (lineStart + 1 != lineEnd) {
                            layout.getSelectionPath(lineStart, lineEnd, this.fetchPath);
                            this.loadingPath.addRoundRect(this.fetchedPathRect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        }
                    }
                }
                updateHeight();
            }
            if (!this.loaded && this.loadingAnimator == null) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.loadingAnimator = ofFloat;
                ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView$$ExternalSyntheticLambda1(this));
                this.loadingAnimator.setDuration(Long.MAX_VALUE);
                this.loadingAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateLoadingLayout$1(ValueAnimator valueAnimator) {
            this.loadingT = 0.0f;
            if (this.scaleFromZero && SystemClock.elapsedRealtime() < this.scaleFromZeroStart + 220 + 25) {
                updateHeight();
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            updateLoadingLayout();
        }

        public void setLines(int i) {
            this.loadingTextView.setLines(i);
            TextView textView2 = this.textView;
            if (textView2 != null) {
                textView2.setLines(i);
            }
        }

        public void showLoadingText(boolean z) {
            this.showLoadingTextValue = z;
        }

        public void setTextColor(int i) {
            this.loadingTextView.setTextColor(i);
            TextView textView2 = this.textView;
            if (textView2 != null) {
                textView2.setTextColor(i);
            }
        }

        public void setTextSize(int i) {
            float f = (float) i;
            this.loadingTextView.setTextSize(0, f);
            TextView textView2 = this.textView;
            if (textView2 != null) {
                textView2.setTextSize(0, f);
            }
            TextView textView3 = this.loadingTextView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(this.loadingString, textView3.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false);
            this.loadingString = replaceEmoji;
            textView3.setText(replaceEmoji);
            int i2 = 999999;
            this.loadingTextView.measure(View.MeasureSpec.makeMeasureSpec(this.animateWidth ? 999999 : getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            TextView textView4 = this.textView;
            if (textView4 != null) {
                textView4.setText(Emoji.replaceEmoji(textView4.getText(), this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                TextView textView5 = this.textView;
                if (!this.animateWidth) {
                    i2 = getWidth();
                }
                textView5.measure(View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            }
            updateLoadingLayout();
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(this.animateWidth ? 999999 : getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            this.textView.layout(getLeft() + this.padHorz, getTop() + this.padVert, getLeft() + this.padHorz + this.textView.getMeasuredWidth(), getTop() + this.padVert + this.textView.getMeasuredHeight());
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
                ofFloat.addUpdateListener(new TranslateAlert$LoadingTextView$$ExternalSyntheticLambda0(this));
                onLoadStart();
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
                this.animator.setDuration(300);
                this.animator.start();
                return;
            }
            updateHeight();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$2(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.loadingT = floatValue;
            onLoadAnimation(floatValue);
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
            canvas.translate((float) this.padHorz, (float) this.padVert);
            canvas.clipPath(this.loadingPath);
            canvas.translate((float) (-this.padHorz), (float) (-this.padVert));
            float f9 = 0.0f;
            canvas.translate(-elapsedRealtime, 0.0f);
            this.shadePath.offset(elapsedRealtime, 0.0f, this.tempPath);
            canvas.drawPath(this.tempPath, this.loading ? this.loadingPaint : this.loadingIdlePaint);
            canvas.translate(elapsedRealtime, 0.0f);
            canvas.restore();
            canvas.save();
            this.rect.set(0.0f, 0.0f, width, height);
            canvas.clipPath(this.inPath, Region.Op.DIFFERENCE);
            canvas.translate((float) this.padHorz, (float) this.padVert);
            canvas.clipPath(this.loadingPath);
            RectF rectF = this.rect;
            if (this.showLoadingTextValue) {
                f9 = 0.08f;
            }
            canvas.saveLayerAlpha(rectF, (int) (f9 * 255.0f), 31);
            this.loadingTextView.draw(canvas);
            canvas.restore();
            canvas.restore();
            if (this.textView != null) {
                canvas.save();
                canvas.clipPath(this.inPath);
                canvas.translate((float) this.padHorz, (float) this.padVert);
                canvas.saveLayerAlpha(this.rect, (int) (this.loadingT * 255.0f), 31);
                this.textView.draw(canvas);
                if (this.loadingT < 1.0f) {
                    canvas.restore();
                }
                canvas.restore();
            }
        }
    }
}
