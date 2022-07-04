package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RecyclerListView;

public class TranslateAlert2 extends BottomSheet {
    /* access modifiers changed from: private */
    public static final int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    public static volatile DispatchQueue translateQueue = new DispatchQueue("translateQueue", false);
    private Spannable allTexts = null;
    private TextView allTextsView;
    /* access modifiers changed from: private */
    public boolean allowScroll = true;
    /* access modifiers changed from: private */
    public ImageView backButton;
    protected ColorDrawable backDrawable = new ColorDrawable(-16777216) {
        public void setAlpha(int alpha) {
            super.setAlpha(alpha);
        }
    };
    private Rect backRect = new Rect();
    private int blockIndex = 0;
    /* access modifiers changed from: private */
    public FrameLayout bulletinContainer;
    private FrameLayout buttonContainerView;
    private Rect buttonRect = new Rect();
    private FrameLayout buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    private Rect containerRect = new Rect();
    /* access modifiers changed from: private */
    public BaseFragment fragment;
    private String fromLanguage;
    private boolean fromScrollRect = false;
    private float fromScrollViewY = 0.0f;
    private boolean fromTranslateMoreView = false;
    private float fromY = 0.0f;
    /* access modifiers changed from: private */
    public HeaderView header;
    /* access modifiers changed from: private */
    public FrameLayout headerShadowView;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public LinkSpanDrawable.LinkCollector links;
    public RecyclerListView listView;
    private boolean loaded = false;
    private boolean loading = false;
    private boolean maybeScrolling = false;
    private boolean noforwards;
    private Runnable onDismiss;
    /* access modifiers changed from: private */
    public OnLinkPress onLinkPress;
    /* access modifiers changed from: private */
    public View paddingView;
    private LinkSpanDrawable pressedLink;
    private boolean pressedOutside = false;
    private Rect scrollRect = new Rect();
    private boolean scrolling = false;
    private ImageView subtitleArrowView;
    /* access modifiers changed from: private */
    public InlineLoadingTextView subtitleFromView;
    private TextView subtitleToView;
    /* access modifiers changed from: private */
    public LinearLayout subtitleView;
    private CharSequence text;
    private ArrayList<CharSequence> textBlocks;
    private Rect textRect = new Rect();
    /* access modifiers changed from: private */
    public TextBlocksLayout textsView;
    /* access modifiers changed from: private */
    public TextView titleView;
    private String toLanguage;
    private Rect translateMoreRect = new Rect();

    public interface OnLinkPress {
        boolean run(URLSpan uRLSpan);
    }

    public interface OnTranslationFail {
        void run(boolean z);
    }

    public interface OnTranslationSuccess {
        void run(String str, String str2);
    }

    public void onBackPressed() {
        dismiss();
    }

    private class HeaderView extends FrameLayout {
        /* access modifiers changed from: private */
        public float expandedT = 0.0f;

        public HeaderView(Context context) {
            super(context);
        }

        public void setExpandedT(float value) {
            int flags;
            TranslateAlert2.this.backButton.setAlpha(value);
            TranslateAlert2.this.headerShadowView.setAlpha(value);
            if (Math.abs(this.expandedT - value) > 0.01f) {
                if (Build.VERSION.SDK_INT >= 23) {
                    boolean z = true;
                    boolean z2 = this.expandedT > 0.5f;
                    if (value <= 0.5f) {
                        z = false;
                    }
                    if (z2 != z) {
                        int flags2 = TranslateAlert2.this.containerView.getSystemUiVisibility();
                        if (value > 0.5f) {
                            flags = flags2 | 8192;
                        } else {
                            flags = flags2 & -8193;
                        }
                        TranslateAlert2.this.containerView.setSystemUiVisibility(flags);
                    }
                }
                this.expandedT = value;
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            TranslateAlert2.this.titleView.layout(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f), (right - left) - AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f) + TranslateAlert2.this.titleView.getMeasuredHeight());
            TranslateAlert2.this.subtitleView.layout(AndroidUtilities.dp(22.0f) - LoadingTextView2.paddingHorizontal, AndroidUtilities.dp(47.0f) - LoadingTextView2.paddingVertical, ((right - left) - AndroidUtilities.dp(22.0f)) - LoadingTextView2.paddingHorizontal, (AndroidUtilities.dp(47.0f) - LoadingTextView2.paddingVertical) + TranslateAlert2.this.subtitleView.getMeasuredHeight());
            TranslateAlert2.this.backButton.layout(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            TranslateAlert2.this.headerShadowView.layout(0, AndroidUtilities.dp(55.0f), right - left, AndroidUtilities.dp(56.0f));
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == TranslateAlert2.this.titleView) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(this.expandedT * 50.0f), (float) AndroidUtilities.dp(this.expandedT * -14.0f));
                float f = this.expandedT;
                canvas.scale(1.0f - (f * 0.111f), 1.0f - (f * 0.111f), child.getX(), child.getY() + ((float) (child.getMeasuredHeight() / 2)));
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result;
            } else if (child != TranslateAlert2.this.subtitleView) {
                return super.drawChild(canvas, child, drawingTime);
            } else {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(this.expandedT * 50.0f), (float) AndroidUtilities.dp(this.expandedT * -17.0f));
                boolean result2 = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result2;
            }
        }

        /* access modifiers changed from: protected */
        public void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
            if (child == TranslateAlert2.this.backButton) {
                TranslateAlert2.this.backButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
            } else {
                super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
            }
        }
    }

    public void updateCanExpand() {
        boolean z = true;
        if (!this.listView.canScrollVertically(1) && !this.listView.canScrollVertically(-1)) {
            z = false;
        }
        boolean canExpand = z;
        this.buttonShadowView.animate().alpha(canExpand ? 1.0f : 0.0f).setDuration(200).start();
        this.allowScroll = canExpand;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TranslateAlert2(org.telegram.ui.ActionBar.BaseFragment r40, android.content.Context r41, java.lang.String r42, java.lang.String r43, java.lang.CharSequence r44, boolean r45, org.telegram.ui.Components.TranslateAlert2.OnLinkPress r46, java.lang.Runnable r47) {
        /*
            r39 = this;
            r7 = r39
            r8 = r41
            r9 = r42
            r10 = r43
            r11 = r44
            r12 = r45
            r13 = 0
            r7.<init>(r8, r13)
            r7.blockIndex = r13
            r14 = 1
            r7.allowScroll = r14
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.containerRect = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.textRect = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.translateMoreRect = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.buttonRect = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.backRect = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.scrollRect = r0
            r15 = 0
            r7.fromY = r15
            r7.pressedOutside = r13
            r7.maybeScrolling = r13
            r7.scrolling = r13
            r7.fromScrollRect = r13
            r7.fromTranslateMoreView = r13
            r7.fromScrollViewY = r15
            r0 = 0
            r7.allTexts = r0
            org.telegram.ui.Components.TranslateAlert2$10 r0 = new org.telegram.ui.Components.TranslateAlert2$10
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0.<init>(r1)
            r7.backDrawable = r0
            r7.loading = r13
            r7.loaded = r13
            r39.fixNavigationBar()
            r6 = r46
            r7.onLinkPress = r6
            r7.noforwards = r12
            r5 = r40
            r7.fragment = r5
            if (r9 == 0) goto L_0x0078
            java.lang.String r0 = "und"
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x0078
            java.lang.String r0 = "auto"
            goto L_0x0079
        L_0x0078:
            r0 = r9
        L_0x0079:
            r7.fromLanguage = r0
            r7.toLanguage = r10
            r7.text = r11
            r0 = 1024(0x400, float:1.435E-42)
            java.util.ArrayList r0 = r7.cutInBlocks(r11, r0)
            r7.textBlocks = r0
            r4 = r47
            r7.onDismiss = r4
            if (r12 == 0) goto L_0x0096
            android.view.Window r0 = r39.getWindow()
            r1 = 8192(0x2000, float:1.14794E-41)
            r0.addFlags(r1)
        L_0x0096:
            org.telegram.ui.Components.TranslateAlert2$1 r0 = new org.telegram.ui.Components.TranslateAlert2$1
            r0.<init>(r8)
            r7.allTextsView = r0
            org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r0 = new org.telegram.ui.Components.LinkSpanDrawable$LinkCollector
            android.widget.TextView r1 = r7.allTextsView
            r0.<init>(r1)
            r7.links = r0
            android.widget.TextView r0 = r7.allTextsView
            r0.setTextColor(r13)
            android.widget.TextView r0 = r7.allTextsView
            r3 = 1098907648(0x41800000, float:16.0)
            r0.setTextSize(r14, r3)
            android.widget.TextView r0 = r7.allTextsView
            r1 = r12 ^ 1
            r0.setTextIsSelectable(r1)
            android.widget.TextView r0 = r7.allTextsView
            java.lang.String r1 = "chat_inTextSelectionHighlight"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setHighlightColor(r1)
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00f7 }
            r1 = 29
            if (r0 < r1) goto L_0x00f8
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isMIUI()     // Catch:{ Exception -> 0x00f7 }
            if (r0 != 0) goto L_0x00f8
            java.lang.String r0 = "chat_TextSelectionCursor"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)     // Catch:{ Exception -> 0x00f7 }
            android.widget.TextView r1 = r7.allTextsView     // Catch:{ Exception -> 0x00f7 }
            android.graphics.drawable.Drawable r1 = r1.getTextSelectHandleLeft()     // Catch:{ Exception -> 0x00f7 }
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x00f7 }
            r1.setColorFilter(r0, r2)     // Catch:{ Exception -> 0x00f7 }
            android.widget.TextView r2 = r7.allTextsView     // Catch:{ Exception -> 0x00f7 }
            r2.setTextSelectHandleLeft(r1)     // Catch:{ Exception -> 0x00f7 }
            android.widget.TextView r2 = r7.allTextsView     // Catch:{ Exception -> 0x00f7 }
            android.graphics.drawable.Drawable r2 = r2.getTextSelectHandleRight()     // Catch:{ Exception -> 0x00f7 }
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.SRC_IN     // Catch:{ Exception -> 0x00f7 }
            r2.setColorFilter(r0, r14)     // Catch:{ Exception -> 0x00f7 }
            android.widget.TextView r14 = r7.allTextsView     // Catch:{ Exception -> 0x00f7 }
            r14.setTextSelectHandleRight(r2)     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00f8
        L_0x00f7:
            r0 = move-exception
        L_0x00f8:
            android.widget.TextView r0 = r7.allTextsView
            android.text.method.LinkMovementMethod r1 = new android.text.method.LinkMovementMethod
            r1.<init>()
            r0.setMovementMethod(r1)
            org.telegram.ui.Components.TranslateAlert2$2 r0 = new org.telegram.ui.Components.TranslateAlert2$2
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.String r17 = "dialogTextBlack"
            int r18 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.widget.TextView r2 = r7.allTextsView
            r1 = r0
            r19 = r2
            r2 = r39
            r20 = 1098907648(0x41800000, float:16.0)
            r3 = r41
            r4 = r14
            r5 = r18
            r6 = r19
            r1.<init>(r3, r4, r5, r6)
            r7.textsView = r0
            r14 = 1102053376(0x41b00000, float:22.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r2 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingHorizontal
            int r1 = r1 - r2
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingVertical
            int r2 = r2 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r4 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingHorizontal
            int r3 = r3 - r4
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingVertical
            int r4 = r4 - r5
            r0.setPadding(r1, r2, r3, r4)
            java.util.ArrayList<java.lang.CharSequence> r0 = r7.textBlocks
            java.util.Iterator r0 = r0.iterator()
        L_0x0150:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0162
            java.lang.Object r1 = r0.next()
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            org.telegram.ui.Components.TranslateAlert2$TextBlocksLayout r2 = r7.textsView
            r2.addBlock(r1)
            goto L_0x0150
        L_0x0162:
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            java.lang.String r1 = "dialogBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r2 = -1087834685(0xffffffffbvar_f5c3, float:-0.66)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r3 = 503316480(0x1e000000, float:6.7762636E-21)
            r0.setShadowLayer(r1, r15, r2, r3)
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>()
            r6 = r1
            java.lang.String r1 = "dialogBackgroundGray"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r6.setColor(r1)
            org.telegram.ui.Components.TranslateAlert2$3 r1 = new org.telegram.ui.Components.TranslateAlert2$3
            r1.<init>(r8, r6, r0)
            r7.containerView = r1
            android.view.ViewGroup r1 = r7.containerView
            int r2 = r7.backgroundPaddingLeft
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r4 = r7.backgroundPaddingLeft
            r1.setPadding(r2, r3, r4, r13)
            android.view.ViewGroup r1 = r7.containerView
            r1.setClipChildren(r13)
            android.view.ViewGroup r1 = r7.containerView
            r1.setClipToPadding(r13)
            android.view.ViewGroup r1 = r7.containerView
            r1.setWillNotDraw(r13)
            org.telegram.ui.Components.TranslateAlert2$4 r1 = new org.telegram.ui.Components.TranslateAlert2$4
            r1.<init>(r8)
            r7.listView = r1
            r2 = 1
            r1.setClipChildren(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            r1.setClipToPadding(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            r2 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 1117782016(0x42a00000, float:80.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r13, r2, r13, r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            org.telegram.ui.Components.TranslateAlert2$5 r2 = new org.telegram.ui.Components.TranslateAlert2$5
            r2.<init>(r8)
            r7.layoutManager = r2
            r1.setLayoutManager(r2)
            androidx.recyclerview.widget.LinearLayoutManager r1 = r7.layoutManager
            r2 = 1
            r1.setOrientation(r2)
            androidx.recyclerview.widget.LinearLayoutManager r1 = r7.layoutManager
            r1.setReverseLayout(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            org.telegram.ui.Components.TranslateAlert2$6 r2 = new org.telegram.ui.Components.TranslateAlert2$6
            r2.<init>()
            r1.setAdapter(r2)
            org.telegram.ui.Components.TranslateAlert2$7 r1 = new org.telegram.ui.Components.TranslateAlert2$7
            r1.<init>(r8)
            r5 = r1
            r1 = -65536(0xfffffffffffvar_, float:NaN)
            r5.setBackgroundColor(r1)
            android.view.ViewGroup r1 = r7.containerView
            r1.addView(r5)
            android.view.ViewGroup r1 = r7.containerView
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            r4 = -1
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r1.addView(r2, r14)
            org.telegram.ui.Components.TranslateAlert2$HeaderView r1 = new org.telegram.ui.Components.TranslateAlert2$HeaderView
            r1.<init>(r8)
            r7.header = r1
            androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r14 = -2
            r2.<init>((int) r4, (int) r14)
            r1.setLayoutParams(r2)
            android.view.ViewGroup r1 = r7.containerView
            org.telegram.ui.Components.TranslateAlert2$HeaderView r2 = r7.header
            r13 = 55
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r14, (int) r13)
            r1.addView(r2, r3)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r7.titleView = r1
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0244
            android.widget.TextView r2 = r7.titleView
            int r2 = r2.getWidth()
            float r2 = (float) r2
            goto L_0x0245
        L_0x0244:
            r2 = 0
        L_0x0245:
            r1.setPivotX(r2)
            android.widget.TextView r1 = r7.titleView
            r1.setPivotY(r15)
            android.widget.TextView r1 = r7.titleView
            r2 = 1
            r1.setLines(r2)
            android.widget.TextView r1 = r7.titleView
            r2 = 2131624627(0x7f0e02b3, float:1.887644E38)
            java.lang.String r3 = "AutomaticTranslation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r7.titleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r22 = 5
            if (r2 == 0) goto L_0x026b
            r2 = 5
            goto L_0x026c
        L_0x026b:
            r2 = 3
        L_0x026c:
            r1.setGravity(r2)
            android.widget.TextView r1 = r7.titleView
            java.lang.String r23 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r23)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r7.titleView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r7.titleView
            r2 = 1099956224(0x41900000, float:18.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            org.telegram.ui.Components.TranslateAlert2$HeaderView r1 = r7.header
            android.widget.TextView r2 = r7.titleView
            r25 = -1
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 55
            r28 = 1102053376(0x41b00000, float:22.0)
            r29 = 1102053376(0x41b00000, float:22.0)
            r30 = 1102053376(0x41b00000, float:22.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r2, r3)
            android.widget.TextView r1 = r7.titleView
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda8
            r2.<init>(r7)
            r1.post(r2)
            android.widget.LinearLayout r1 = new android.widget.LinearLayout
            r1.<init>(r8)
            r7.subtitleView = r1
            r2 = 0
            r1.setOrientation(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 17
            if (r1 < r3) goto L_0x02c6
            android.widget.LinearLayout r1 = r7.subtitleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r1.setLayoutDirection(r2)
        L_0x02c6:
            android.widget.LinearLayout r1 = r7.subtitleView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x02ce
            r2 = 5
            goto L_0x02cf
        L_0x02ce:
            r2 = 3
        L_0x02cf:
            r1.setGravity(r2)
            java.lang.String r2 = r7.languageName(r9)
            org.telegram.ui.Components.TranslateAlert2$8 r1 = new org.telegram.ui.Components.TranslateAlert2$8
            if (r2 != 0) goto L_0x02df
            java.lang.String r25 = r7.languageName(r10)
            goto L_0x02e1
        L_0x02df:
            r25 = r2
        L_0x02e1:
            r13 = 1096810496(0x41600000, float:14.0)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r13)
            java.lang.String r28 = "player_actionBarSubtitle"
            int r29 = org.telegram.ui.ActionBar.Theme.getColor(r28)
            r30 = r1
            r15 = r2
            r2 = r39
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = r41
            r4 = r25
            r24 = r5
            r5 = r27
            r25 = r6
            r6 = r29
            r1.<init>(r3, r4, r5, r6)
            r7.subtitleFromView = r1
            r2 = 0
            r1.showLoadingText = r2
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r8)
            r7.subtitleArrowView = r1
            r2 = 2131166120(0x7var_a8, float:1.7946476E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r7.subtitleArrowView
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r28)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r4)
            r1.setColorFilter(r2)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x032e
            android.widget.ImageView r1 = r7.subtitleArrowView
            r1.setScaleX(r14)
        L_0x032e:
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r7.subtitleToView = r1
            r2 = 1
            r1.setLines(r2)
            android.widget.TextView r1 = r7.subtitleToView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r28)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r7.subtitleToView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r2 = (float) r2
            r3 = 0
            r1.setTextSize(r3, r2)
            android.widget.TextView r1 = r7.subtitleToView
            java.lang.String r2 = r7.languageName(r10)
            r1.setText(r2)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r2 = 16
            if (r1 == 0) goto L_0x039b
            android.widget.LinearLayout r1 = r7.subtitleView
            int r3 = org.telegram.ui.Components.TranslateAlert2.InlineLoadingTextView.paddingHorizontal
            r4 = 0
            r1.setPadding(r3, r4, r4, r4)
            android.widget.LinearLayout r1 = r7.subtitleView
            android.widget.TextView r3 = r7.subtitleToView
            r4 = -2
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r4, (int) r2)
            r1.addView(r3, r2)
            android.widget.LinearLayout r1 = r7.subtitleView
            android.widget.ImageView r2 = r7.subtitleArrowView
            r32 = -2
            r33 = -2
            r34 = 16
            r35 = 3
            r36 = 1
            r37 = 3
            r38 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37, (int) r38)
            r1.addView(r2, r3)
            android.widget.LinearLayout r1 = r7.subtitleView
            org.telegram.ui.Components.TranslateAlert2$InlineLoadingTextView r2 = r7.subtitleFromView
            r35 = 0
            r36 = 0
            r37 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37, (int) r38)
            r1.addView(r2, r3)
            goto L_0x03d9
        L_0x039b:
            android.widget.LinearLayout r1 = r7.subtitleView
            int r3 = org.telegram.ui.Components.TranslateAlert2.InlineLoadingTextView.paddingHorizontal
            r4 = 0
            r1.setPadding(r4, r4, r3, r4)
            android.widget.LinearLayout r1 = r7.subtitleView
            org.telegram.ui.Components.TranslateAlert2$InlineLoadingTextView r3 = r7.subtitleFromView
            r32 = -2
            r33 = -2
            r34 = 16
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37, (int) r38)
            r1.addView(r3, r4)
            android.widget.LinearLayout r1 = r7.subtitleView
            android.widget.ImageView r3 = r7.subtitleArrowView
            r35 = 3
            r36 = 1
            r37 = 3
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r32, (int) r33, (int) r34, (int) r35, (int) r36, (int) r37, (int) r38)
            r1.addView(r3, r4)
            android.widget.LinearLayout r1 = r7.subtitleView
            android.widget.TextView r3 = r7.subtitleToView
            r4 = -2
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r4, (int) r2)
            r1.addView(r3, r2)
        L_0x03d9:
            if (r15 == 0) goto L_0x03e0
            org.telegram.ui.Components.TranslateAlert2$InlineLoadingTextView r1 = r7.subtitleFromView
            r1.set(r15)
        L_0x03e0:
            org.telegram.ui.Components.TranslateAlert2$HeaderView r1 = r7.header
            android.widget.LinearLayout r2 = r7.subtitleView
            r32 = -1
            r33 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x03ed
            goto L_0x03ef
        L_0x03ed:
            r22 = 3
        L_0x03ef:
            r34 = r22 | 48
            int r3 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingHorizontal
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            r4 = 1102053376(0x41b00000, float:22.0)
            float r35 = r4 - r3
            r3 = 1111228416(0x423CLASSNAME, float:47.0)
            int r5 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingVertical
            float r5 = (float) r5
            float r6 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 / r6
            float r36 = r3 - r5
            int r3 = org.telegram.ui.Components.TranslateAlert2.LoadingTextView2.paddingHorizontal
            float r3 = (float) r3
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r5
            float r37 = r4 - r3
            r38 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r1.addView(r2, r3)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r8)
            r7.backButton = r1
            r2 = 2131165449(0x7var_, float:1.7945115E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r7.backButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r4)
            r1.setColorFilter(r2)
            android.widget.ImageView r1 = r7.backButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.FIT_CENTER
            r1.setScaleType(r2)
            android.widget.ImageView r1 = r7.backButton
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r20)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r4 = 0
            r1.setPadding(r2, r4, r3, r4)
            android.widget.ImageView r1 = r7.backButton
            java.lang.String r2 = "dialogButtonSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2)
            r1.setBackground(r2)
            android.widget.ImageView r1 = r7.backButton
            r1.setClickable(r4)
            android.widget.ImageView r1 = r7.backButton
            r2 = 0
            r1.setAlpha(r2)
            android.widget.ImageView r1 = r7.backButton
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda0
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            org.telegram.ui.Components.TranslateAlert2$HeaderView r1 = r7.header
            android.widget.ImageView r2 = r7.backButton
            r3 = 56
            r4 = 3
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r4)
            r1.addView(r2, r3)
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.headerShadowView = r1
            java.lang.String r2 = "dialogShadowLine"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setBackgroundColor(r3)
            android.widget.FrameLayout r1 = r7.headerShadowView
            r3 = 0
            r1.setAlpha(r3)
            org.telegram.ui.Components.TranslateAlert2$HeaderView r1 = r7.header
            android.widget.FrameLayout r3 = r7.headerShadowView
            r4 = 87
            r5 = -1
            r6 = 1
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r6, (int) r4)
            r1.addView(r3, r14)
            org.telegram.ui.Components.TranslateAlert2$9 r1 = new org.telegram.ui.Components.TranslateAlert2$9
            r1.<init>(r8)
            r7.paddingView = r1
            r39.fetchNext()
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r7.buttonTextView = r1
            r3 = 1
            r1.setLines(r3)
            android.widget.TextView r1 = r7.buttonTextView
            r1.setSingleLine(r3)
            android.widget.TextView r1 = r7.buttonTextView
            r1.setGravity(r3)
            android.widget.TextView r1 = r7.buttonTextView
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r3)
            android.widget.TextView r1 = r7.buttonTextView
            r3 = 17
            r1.setGravity(r3)
            android.widget.TextView r1 = r7.buttonTextView
            java.lang.String r3 = "featuredStickers_buttonText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r3)
            android.widget.TextView r1 = r7.buttonTextView
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r23)
            r1.setTypeface(r3)
            android.widget.TextView r1 = r7.buttonTextView
            r3 = 1
            r1.setTextSize(r3, r13)
            android.widget.TextView r1 = r7.buttonTextView
            r3 = 2131625169(0x7f0e04d1, float:1.8877538E38)
            java.lang.String r6 = "CloseTranslation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.setText(r3)
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.buttonView = r1
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.String r6 = "featuredStickers_addButton"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r13 = "featuredStickers_addButtonPressed"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r3, r6, r13)
            r1.setBackground(r3)
            android.widget.FrameLayout r1 = r7.buttonView
            android.widget.TextView r3 = r7.buttonTextView
            r1.addView(r3)
            android.widget.FrameLayout r1 = r7.buttonView
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda3 r3 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda3
            r3.<init>(r7)
            r1.setOnClickListener(r3)
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.buttonContainerView = r1
            android.widget.FrameLayout r3 = r7.buttonView
            r17 = -1
            r18 = -1082130432(0xffffffffbvar_, float:-1.0)
            r19 = 119(0x77, float:1.67E-43)
            r20 = 1098907648(0x41800000, float:16.0)
            r21 = 1098907648(0x41800000, float:16.0)
            r22 = 1098907648(0x41800000, float:16.0)
            r23 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r1.addView(r3, r6)
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.buttonShadowView = r1
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            android.widget.FrameLayout r1 = r7.buttonShadowView
            r2 = 0
            r1.setAlpha(r2)
            android.widget.FrameLayout r1 = r7.buttonContainerView
            android.widget.FrameLayout r2 = r7.buttonShadowView
            r3 = 55
            r6 = 1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r6, (int) r3)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r7.containerView
            android.widget.FrameLayout r2 = r7.buttonContainerView
            r3 = 80
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r3, (int) r4)
            r1.addView(r2, r3)
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r8)
            r7.bulletinContainer = r1
            android.view.ViewGroup r1 = r7.containerView
            android.widget.FrameLayout r2 = r7.bulletinContainer
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 119(0x77, float:1.67E-43)
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 1117913088(0x42a20000, float:81.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert2.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, java.lang.String, java.lang.String, java.lang.CharSequence, boolean, org.telegram.ui.Components.TranslateAlert2$OnLinkPress, java.lang.Runnable):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    public /* synthetic */ void m1522lambda$new$0$orgtelegramuiComponentsTranslateAlert2() {
        this.titleView.setPivotX(LocaleController.isRTL ? (float) this.titleView.getWidth() : 0.0f);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    public /* synthetic */ void m1523lambda$new$1$orgtelegramuiComponentsTranslateAlert2(View e) {
        if (this.backButton.getAlpha() > 0.5f) {
            dismiss();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    public /* synthetic */ void m1524lambda$new$2$orgtelegramuiComponentsTranslateAlert2(View e) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public float getCurrentItemTop() {
        RecyclerListView.Holder holder;
        View child = this.listView.getChildAt(0);
        if (child == null || (holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child)) == null || holder.getAdapterPosition() != 0) {
            return 0.0f;
        }
        return Math.max(0.0f, child.getY() - ((float) this.header.getMeasuredHeight()));
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return !this.listView.canScrollVertically(-1);
    }

    private boolean hasSelection() {
        return this.allTextsView.hasSelection();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.containerView.setPadding(this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, this.backgroundPaddingLeft, 0);
    }

    public String languageName(String locale) {
        LocaleController.LocaleInfo thisLanguageInfo;
        if (locale == null || locale.equals("und") || locale.equals("auto") || (thisLanguageInfo = LocaleController.getInstance().getBuiltinLanguageByPlural(locale)) == null) {
            return null;
        }
        LocaleController.LocaleInfo currentLanguageInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        if (currentLanguageInfo != null && "en".equals(currentLanguageInfo.pluralLangCode)) {
            return thisLanguageInfo.nameEnglish;
        }
        return thisLanguageInfo.name;
    }

    public void updateSourceLanguage() {
        String fromLanguageName = languageName(this.fromLanguage);
        if (fromLanguageName != null) {
            this.subtitleView.setAlpha(1.0f);
            if (!this.subtitleFromView.loaded) {
                this.subtitleFromView.loaded(fromLanguageName);
            }
        } else if (this.loaded) {
            this.subtitleView.animate().alpha(0.0f).setDuration(150).start();
            this.titleView.animate().scaleX(1.2f).scaleY(1.2f).translationY((float) AndroidUtilities.dp(5.0f)).setDuration(150).start();
        }
    }

    private ArrayList<CharSequence> cutInBlocks(CharSequence full, int maxBlockSize) {
        ArrayList<CharSequence> blocks = new ArrayList<>();
        if (full == null) {
            return blocks;
        }
        while (full.length() > maxBlockSize) {
            String maxBlockStr = full.subSequence(0, maxBlockSize).toString();
            int n = maxBlockStr.lastIndexOf("\n\n");
            if (n == -1) {
                n = maxBlockStr.lastIndexOf("\n");
            }
            if (n == -1) {
                n = maxBlockStr.lastIndexOf(". ");
            }
            if (n == -1) {
                n = maxBlockStr.length();
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
        fetchTranslation(this.textBlocks.get(this.blockIndex), (long) Math.min((this.blockIndex + 1) * 1000, 3500), new TranslateAlert2$$ExternalSyntheticLambda2(this), new TranslateAlert2$$ExternalSyntheticLambda1(this));
        return true;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$fetchNext$4$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1519lambda$fetchNext$4$orgtelegramuiComponentsTranslateAlert2(java.lang.String r12, java.lang.String r13) {
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
            org.telegram.ui.Components.TranslateAlert2$11 r6 = new org.telegram.ui.Components.TranslateAlert2$11     // Catch:{ Exception -> 0x008b }
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
            org.telegram.ui.Components.TranslateAlert2$12 r10 = new org.telegram.ui.Components.TranslateAlert2$12     // Catch:{ Exception -> 0x008b }
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
            if (r3 != 0) goto L_0x0097
            java.lang.String r3 = ""
        L_0x0097:
            r2.<init>(r3)
            int r3 = r11.blockIndex
            if (r3 == 0) goto L_0x00a3
            java.lang.String r3 = "\n"
            r2.append(r3)
        L_0x00a3:
            r2.append(r1)
            r11.allTexts = r2
            org.telegram.ui.Components.TranslateAlert2$TextBlocksLayout r3 = r11.textsView
            r3.setWholeText(r2)
            org.telegram.ui.Components.TranslateAlert2$TextBlocksLayout r3 = r11.textsView
            int r4 = r11.blockIndex
            org.telegram.ui.Components.TranslateAlert2$LoadingTextView2 r3 = r3.getBlockAt(r4)
            if (r3 == 0) goto L_0x00bf
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda7 r4 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda7
            r4.<init>(r11)
            r3.loaded(r1, r4)
        L_0x00bf:
            if (r13 == 0) goto L_0x00c6
            r11.fromLanguage = r13
            r11.updateSourceLanguage()
        L_0x00c6:
            int r4 = r11.blockIndex
            int r4 = r4 + r0
            r11.blockIndex = r4
            r11.loading = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert2.m1519lambda$fetchNext$4$orgtelegramuiComponentsTranslateAlert2(java.lang.String, java.lang.String):void");
    }

    /* renamed from: lambda$fetchNext$3$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    public /* synthetic */ void m1518lambda$fetchNext$3$orgtelegramuiComponentsTranslateAlert2() {
        AndroidUtilities.runOnUIThread(new TranslateAlert2$$ExternalSyntheticLambda9(this));
    }

    /* renamed from: lambda$fetchNext$5$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    public /* synthetic */ void m1520lambda$fetchNext$5$orgtelegramuiComponentsTranslateAlert2(boolean rateLimit) {
        String str;
        Context context = getContext();
        if (rateLimit) {
            str = LocaleController.getString("TranslationFailedAlert1", NUM);
        } else {
            str = LocaleController.getString("TranslationFailedAlert2", NUM);
        }
        Toast.makeText(context, str, 0).show();
        if (this.blockIndex == 0) {
            dismiss();
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        Runnable runnable = this.onDismiss;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public void checkForNextLoading() {
        if (!this.listView.canScrollVertically(-1)) {
            fetchNext();
        }
    }

    private void fetchTranslation(CharSequence text2, long minDuration, OnTranslationSuccess onSuccess, OnTranslationFail onFail) {
        if (!translateQueue.isAlive()) {
            translateQueue.start();
        }
        translateQueue.postRunnable(new TranslateAlert2$$ExternalSyntheticLambda10(this, text2, onSuccess, minDuration, onFail));
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x018d A[Catch:{ IOException -> 0x01b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0196 A[Catch:{ IOException -> 0x01b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01a1 A[Catch:{ IOException -> 0x01b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:97:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$fetchTranslation$9$org-telegram-ui-Components-TranslateAlert2  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1521x3CLASSNAMEfbe(java.lang.CharSequence r23, org.telegram.ui.Components.TranslateAlert2.OnTranslationSuccess r24, long r25, org.telegram.ui.Components.TranslateAlert2.OnTranslationFail r27) {
        /*
            r22 = this;
            r1 = r22
            r2 = r27
            java.lang.String r3 = "-"
            java.lang.String r4 = ""
            r5 = 0
            long r6 = android.os.SystemClock.elapsedRealtime()
            r8 = 0
            java.lang.String r0 = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
            r4 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            r0.append(r4)     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = r1.fromLanguage     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = android.net.Uri.encode(r9)     // Catch:{ Exception -> 0x017c }
            r0.append(r9)     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            r4 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            r0.append(r4)     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = "&tl="
            r0.append(r9)     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            r4 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            r0.append(r4)     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = r1.toLanguage     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = android.net.Uri.encode(r9)     // Catch:{ Exception -> 0x017c }
            r0.append(r9)     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            r4 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            r0.append(r4)     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q="
            r0.append(r9)     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            r4 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            r0.append(r4)     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = r23.toString()     // Catch:{ Exception -> 0x017c }
            java.lang.String r9 = android.net.Uri.encode(r9)     // Catch:{ Exception -> 0x017c }
            r0.append(r9)     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            r4 = r0
            java.net.URI r0 = new java.net.URI     // Catch:{ Exception -> 0x0178 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x0178 }
            java.net.URL r0 = r0.toURL()     // Catch:{ Exception -> 0x0178 }
            java.net.URLConnection r0 = r0.openConnection()     // Catch:{ Exception -> 0x0178 }
            java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x0178 }
            r5 = r0
            java.lang.String r0 = "GET"
            r5.setRequestMethod(r0)     // Catch:{ Exception -> 0x0178 }
            java.lang.String r0 = "User-Agent"
            java.lang.String r9 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"
            r5.setRequestProperty(r0, r9)     // Catch:{ Exception -> 0x0178 }
            java.lang.String r0 = "Content-Type"
            java.lang.String r9 = "application/json"
            r5.setRequestProperty(r0, r9)     // Catch:{ Exception -> 0x0178 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0178 }
            r0.<init>()     // Catch:{ Exception -> 0x0178 }
            r9 = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0178 }
            java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0178 }
            java.io.InputStream r11 = r5.getInputStream()     // Catch:{ Exception -> 0x0178 }
            java.lang.String r12 = "UTF-8"
            java.nio.charset.Charset r12 = java.nio.charset.Charset.forName(r12)     // Catch:{ Exception -> 0x0178 }
            r10.<init>(r11, r12)     // Catch:{ Exception -> 0x0178 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x0178 }
            r10 = r0
        L_0x00b7:
            int r0 = r10.read()     // Catch:{ all -> 0x016a }
            r11 = r0
            r12 = -1
            if (r0 == r12) goto L_0x00ca
            char r0 = (char) r11
            r9.append(r0)     // Catch:{ all -> 0x00c4 }
            goto L_0x00b7
        L_0x00c4:
            r0 = move-exception
            r1 = r0
            r20 = r4
            goto L_0x016e
        L_0x00ca:
            r10.close()     // Catch:{ Exception -> 0x0178 }
            java.lang.String r0 = r9.toString()     // Catch:{ Exception -> 0x0178 }
            r10 = r0
            org.json.JSONTokener r0 = new org.json.JSONTokener     // Catch:{ Exception -> 0x0178 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x0178 }
            r11 = r0
            org.json.JSONArray r0 = new org.json.JSONArray     // Catch:{ Exception -> 0x0178 }
            r0.<init>(r11)     // Catch:{ Exception -> 0x0178 }
            r12 = r0
            org.json.JSONArray r0 = r12.getJSONArray(r8)     // Catch:{ Exception -> 0x0178 }
            r13 = r0
            r14 = 0
            r0 = 2
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Exception -> 0x00eb }
            r14 = r0
            goto L_0x00ec
        L_0x00eb:
            r0 = move-exception
        L_0x00ec:
            if (r14 == 0) goto L_0x00fe
            boolean r0 = r14.contains(r3)     // Catch:{ Exception -> 0x017c }
            if (r0 == 0) goto L_0x00fe
            int r0 = r14.indexOf(r3)     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r14.substring(r8, r0)     // Catch:{ Exception -> 0x017c }
            r14 = r0
        L_0x00fe:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0178 }
            r0.<init>()     // Catch:{ Exception -> 0x0178 }
            r3 = 0
        L_0x0104:
            int r15 = r13.length()     // Catch:{ Exception -> 0x0178 }
            if (r3 >= r15) goto L_0x0123
            org.json.JSONArray r15 = r13.getJSONArray(r3)     // Catch:{ Exception -> 0x017c }
            java.lang.String r15 = r15.getString(r8)     // Catch:{ Exception -> 0x017c }
            if (r15 == 0) goto L_0x011f
            java.lang.String r8 = "null"
            boolean r8 = r15.equals(r8)     // Catch:{ Exception -> 0x017c }
            if (r8 != 0) goto L_0x011f
            r0.append(r15)     // Catch:{ Exception -> 0x017c }
        L_0x011f:
            int r3 = r3 + 1
            r8 = 0
            goto L_0x0104
        L_0x0123:
            int r3 = r23.length()     // Catch:{ Exception -> 0x0178 }
            if (r3 <= 0) goto L_0x013b
            r3 = r23
            r8 = 0
            char r15 = r3.charAt(r8)     // Catch:{ Exception -> 0x017c }
            r8 = 10
            if (r15 != r8) goto L_0x013d
            java.lang.String r8 = "\n"
            r15 = 0
            r0.insert(r15, r8)     // Catch:{ Exception -> 0x017c }
            goto L_0x013e
        L_0x013b:
            r3 = r23
        L_0x013d:
            r15 = 0
        L_0x013e:
            java.lang.String r8 = r0.toString()     // Catch:{ Exception -> 0x0178 }
            r16 = r14
            long r17 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0178 }
            long r17 = r17 - r6
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda6 r15 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda6     // Catch:{ Exception -> 0x0178 }
            r1 = r24
            r21 = r16
            r16 = r0
            r0 = r21
            r15.<init>(r1, r8, r0)     // Catch:{ Exception -> 0x0178 }
            r19 = r0
            r0 = 0
            r20 = r4
            long r3 = r25 - r17
            long r0 = java.lang.Math.max(r0, r3)     // Catch:{ Exception -> 0x0174 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15, r0)     // Catch:{ Exception -> 0x0174 }
            r4 = r20
            goto L_0x01da
        L_0x016a:
            r0 = move-exception
            r20 = r4
            r1 = r0
        L_0x016e:
            r10.close()     // Catch:{ all -> 0x0172 }
            goto L_0x0173
        L_0x0172:
            r0 = move-exception
        L_0x0173:
            throw r1     // Catch:{ Exception -> 0x0174 }
        L_0x0174:
            r0 = move-exception
            r4 = r20
            goto L_0x017d
        L_0x0178:
            r0 = move-exception
            r20 = r4
            goto L_0x017d
        L_0x017c:
            r0 = move-exception
        L_0x017d:
            r1 = r0
            java.lang.String r0 = "translate"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01b0 }
            r3.<init>()     // Catch:{ IOException -> 0x01b0 }
            java.lang.String r8 = "failed to translate a text "
            r3.append(r8)     // Catch:{ IOException -> 0x01b0 }
            r8 = 0
            if (r5 == 0) goto L_0x0196
            int r9 = r5.getResponseCode()     // Catch:{ IOException -> 0x01b0 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ IOException -> 0x01b0 }
            goto L_0x0197
        L_0x0196:
            r9 = r8
        L_0x0197:
            r3.append(r9)     // Catch:{ IOException -> 0x01b0 }
            java.lang.String r9 = " "
            r3.append(r9)     // Catch:{ IOException -> 0x01b0 }
            if (r5 == 0) goto L_0x01a5
            java.lang.String r8 = r5.getResponseMessage()     // Catch:{ IOException -> 0x01b0 }
        L_0x01a5:
            r3.append(r8)     // Catch:{ IOException -> 0x01b0 }
            java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x01b0 }
            android.util.Log.e(r0, r3)     // Catch:{ IOException -> 0x01b0 }
            goto L_0x01b4
        L_0x01b0:
            r0 = move-exception
            r0.printStackTrace()
        L_0x01b4:
            r1.printStackTrace()
            if (r2 == 0) goto L_0x01da
            if (r5 == 0) goto L_0x01c7
            int r0 = r5.getResponseCode()     // Catch:{ Exception -> 0x01c5 }
            r3 = 429(0x1ad, float:6.01E-43)
            if (r0 != r3) goto L_0x01c7
            r8 = 1
            goto L_0x01c8
        L_0x01c5:
            r0 = move-exception
            goto L_0x01d2
        L_0x01c7:
            r8 = 0
        L_0x01c8:
            r0 = r8
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda5     // Catch:{ Exception -> 0x01c5 }
            r3.<init>(r2, r0)     // Catch:{ Exception -> 0x01c5 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x01c5 }
            goto L_0x01da
        L_0x01d2:
            org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda4 r3 = new org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda4
            r3.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
        L_0x01da:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranslateAlert2.m1521x3CLASSNAMEfbe(java.lang.CharSequence, org.telegram.ui.Components.TranslateAlert2$OnTranslationSuccess, long, org.telegram.ui.Components.TranslateAlert2$OnTranslationFail):void");
    }

    static /* synthetic */ void lambda$fetchTranslation$6(OnTranslationSuccess onSuccess, String finalResult, String finalSourceLanguage) {
        if (onSuccess != null) {
            onSuccess.run(finalResult, finalSourceLanguage);
        }
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
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, TranslateAlert2$$ExternalSyntheticLambda11.INSTANCE);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$translateText$10(TLObject error, TLRPC.TL_error res) {
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment fragment2, int currentAccount, TLRPC.InputPeer peer, int msgId, String fromLanguage2, String toLanguage2, CharSequence text2, boolean noforwards2, OnLinkPress onLinkPress2, Runnable onDismiss2) {
        BaseFragment baseFragment = fragment2;
        TLRPC.InputPeer inputPeer = peer;
        String str = fromLanguage2;
        if (inputPeer != null) {
            translateText(currentAccount, inputPeer, msgId, (str == null || !str.equals("und")) ? str : null, toLanguage2);
        } else {
            int i = currentAccount;
            int i2 = msgId;
            String str2 = toLanguage2;
        }
        TranslateAlert2 alert = new TranslateAlert2(fragment2, context, fromLanguage2, toLanguage2, text2, noforwards2, onLinkPress2, onDismiss2);
        if (baseFragment == null) {
            alert.show();
        } else if (fragment2.getParentActivity() != null) {
            baseFragment.showDialog(alert);
        }
        return alert;
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment fragment2, String fromLanguage2, String toLanguage2, CharSequence text2, boolean noforwards2, OnLinkPress onLinkPress2, Runnable onDismiss2) {
        BaseFragment baseFragment = fragment2;
        TranslateAlert2 alert = new TranslateAlert2(fragment2, context, fromLanguage2, toLanguage2, text2, noforwards2, onLinkPress2, onDismiss2);
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
        public void onHeightUpdated(int height, int dy) {
        }

        public void updateHeight() {
            boolean updated;
            int newHeight = height();
            int dy = 0;
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
            if (lp == null) {
                lp = new RecyclerView.LayoutParams(-1, newHeight);
                updated = true;
            } else {
                updated = lp.height != newHeight;
                dy = newHeight - lp.height;
                lp.height = newHeight;
            }
            if (updated) {
                setLayoutParams(lp);
                onHeightUpdated(newHeight, dy);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int count = getBlocksCount();
            int innerWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), View.MeasureSpec.getMode(widthMeasureSpec));
            for (int i = 0; i < count; i++) {
                getBlockAt(i).measure(innerWidthMeasureSpec, TranslateAlert2.MOST_SPEC);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height(), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int y = 0;
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
                i++;
            }
            this.wholeTextView.measure(View.MeasureSpec.makeMeasureSpec(((r - l) - getPaddingLeft()) - getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(((b - t) - getPaddingTop()) - getPaddingBottom(), NUM));
            this.wholeTextView.layout(getPaddingLeft(), getPaddingTop(), (r - l) - getPaddingRight(), getPaddingTop() + this.wholeTextView.getMeasuredHeight());
        }
    }

    public static class InlineLoadingTextView extends ViewGroup {
        public static final int paddingHorizontal = AndroidUtilities.dp(4.0f);
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
                    super.onMeasure(TranslateAlert2.MOST_SPEC, TranslateAlert2.MOST_SPEC);
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
                    super.onMeasure(TranslateAlert2.MOST_SPEC, TranslateAlert2.MOST_SPEC);
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
            ofFloat.addUpdateListener(new TranslateAlert2$InlineLoadingTextView$$ExternalSyntheticLambda1(this));
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert2$InlineLoadingTextView  reason: not valid java name */
        public /* synthetic */ void m1526xe0d11689(ValueAnimator a) {
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
                ofFloat.addUpdateListener(new TranslateAlert2$InlineLoadingTextView$$ExternalSyntheticLambda0(this));
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

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert2$InlineLoadingTextView  reason: not valid java name */
        public /* synthetic */ void m1525xd95ca609(ValueAnimator a) {
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
        public static final int paddingHorizontal = AndroidUtilities.dp(4.0f);
        public static final int paddingVertical = AndroidUtilities.dp(1.5f);
        private RectF fetchedPathRect = new RectF();
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
                    super.onMeasure(widthMeasureSpec, TranslateAlert2.MOST_SPEC);
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
                    super.onMeasure(widthMeasureSpec, TranslateAlert2.MOST_SPEC);
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
            ofFloat.addUpdateListener(new TranslateAlert2$LoadingTextView2$$ExternalSyntheticLambda1(this, z));
            ofFloat.setDuration(Long.MAX_VALUE);
            ofFloat.start();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-TranslateAlert2$LoadingTextView2  reason: not valid java name */
        public /* synthetic */ void m1528xd663CLASSNAME(boolean scaleFromZero2, ValueAnimator a) {
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
                ofFloat.addUpdateListener(new TranslateAlert2$LoadingTextView2$$ExternalSyntheticLambda0(this));
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

        /* renamed from: lambda$loaded$1$org-telegram-ui-Components-TranslateAlert2$LoadingTextView2  reason: not valid java name */
        public /* synthetic */ void m1527xb1bCLASSNAME(ValueAnimator a) {
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
            view.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), TranslateAlert2.MOST_SPEC);
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
