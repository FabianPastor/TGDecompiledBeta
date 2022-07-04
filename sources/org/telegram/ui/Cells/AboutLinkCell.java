package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AboutLinkCell extends FrameLayout {
    private static final int COLLAPSED_HEIGHT = AndroidUtilities.dp(76.0f);
    private static final int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    final float SPACE = ((float) AndroidUtilities.dp(3.0f));
    private Paint backgroundPaint = new Paint();
    private FrameLayout bottomShadow;
    private ValueAnimator collapseAnimator;
    /* access modifiers changed from: private */
    public FrameLayout container;
    /* access modifiers changed from: private */
    public float expandT = 0.0f;
    /* access modifiers changed from: private */
    public boolean expanded;
    /* access modifiers changed from: private */
    public StaticLayout firstThreeLinesLayout;
    private int lastInlineLine = -1;
    private int lastMaxWidth = 0;
    private LinkSpanDrawable.LinkCollector links;
    Runnable longPressedRunnable = new Runnable() {
        public void run() {
            String str;
            if (AboutLinkCell.this.pressedLink != null) {
                if (AboutLinkCell.this.pressedLink.getSpan() instanceof URLSpanNoUnderline) {
                    str = ((URLSpanNoUnderline) AboutLinkCell.this.pressedLink.getSpan()).getURL();
                } else if (AboutLinkCell.this.pressedLink.getSpan() instanceof URLSpan) {
                    str = ((URLSpan) AboutLinkCell.this.pressedLink.getSpan()).getURL();
                } else {
                    str = AboutLinkCell.this.pressedLink.getSpan().toString();
                }
                try {
                    AboutLinkCell.this.performHapticFeedback(0, 2);
                } catch (Exception unused) {
                }
                BottomSheet.Builder builder = new BottomSheet.Builder(AboutLinkCell.this.parentFragment.getParentActivity());
                builder.setTitle(str);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new AboutLinkCell$3$$ExternalSyntheticLambda0(this, (ClickableSpan) AboutLinkCell.this.pressedLink.getSpan(), str));
                builder.setOnPreDismissListener(new AboutLinkCell$3$$ExternalSyntheticLambda1(this));
                builder.show();
                LinkSpanDrawable unused2 = AboutLinkCell.this.pressedLink = null;
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(ClickableSpan clickableSpan, String str, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                AboutLinkCell.this.onLinkClick(clickableSpan);
            } else if (i == 1) {
                AndroidUtilities.addToClipboard(str);
                if (Build.VERSION.SDK_INT >= 31) {
                    return;
                }
                if (str.startsWith("@")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("UsernameCopied", NUM)).show();
                } else if (str.startsWith("#") || str.startsWith("$")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("HashtagCopied", NUM)).show();
                } else {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("LinkCopied", NUM)).show();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(DialogInterface dialogInterface) {
            AboutLinkCell.this.resetPressedLink();
        }
    };
    private boolean moreButtonDisabled;
    private boolean needSpace = false;
    /* access modifiers changed from: private */
    public StaticLayout[] nextLinesLayouts = null;
    /* access modifiers changed from: private */
    public Point[] nextLinesLayoutsPositions;
    private String oldText;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public LinkSpanDrawable pressedLink;
    private Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public Drawable rippleBackground;
    /* access modifiers changed from: private */
    public boolean shouldExpand = false;
    private Drawable showMoreBackgroundDrawable;
    private FrameLayout showMoreTextBackgroundView;
    /* access modifiers changed from: private */
    public TextView showMoreTextView;
    private SpannableStringBuilder stringBuilder;
    /* access modifiers changed from: private */
    public StaticLayout textLayout;
    /* access modifiers changed from: private */
    public int textX;
    /* access modifiers changed from: private */
    public int textY;
    private TextView valueTextView;

    /* access modifiers changed from: protected */
    public void didExtend() {
    }

    /* access modifiers changed from: protected */
    public void didPressUrl(String str) {
    }

    /* access modifiers changed from: protected */
    public void didResizeEnd() {
    }

    /* access modifiers changed from: protected */
    public void didResizeStart() {
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AboutLinkCell(Context context, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        new Point();
        new LinkPath(true);
        this.resourcesProvider = resourcesProvider3;
        this.parentFragment = baseFragment;
        AnonymousClass1 r7 = new FrameLayout(context2) {
            /* JADX WARNING: Code restructure failed: missing block: B:48:0x011b, code lost:
                if (org.telegram.ui.Cells.AboutLinkCell.access$900(r1, org.telegram.ui.Cells.AboutLinkCell.access$000(r1), org.telegram.ui.Cells.AboutLinkCell.access$700(r11.this$0), org.telegram.ui.Cells.AboutLinkCell.access$800(r11.this$0), r0, r7) != false) goto L_0x011d;
             */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x0122  */
            /* JADX WARNING: Removed duplicated region for block: B:64:0x0154 A[ORIG_RETURN, RETURN, SYNTHETIC] */
            /* JADX WARNING: Removed duplicated region for block: B:67:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouchEvent(android.view.MotionEvent r12) {
                /*
                    r11 = this;
                    float r0 = r12.getX()
                    int r0 = (int) r0
                    float r1 = r12.getY()
                    int r7 = (int) r1
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout r1 = r1.textLayout
                    r8 = 0
                    r9 = 1
                    if (r1 != 0) goto L_0x001c
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout[] r1 = r1.nextLinesLayouts
                    if (r1 == 0) goto L_0x014b
                L_0x001c:
                    int r1 = r12.getAction()
                    if (r1 == 0) goto L_0x003f
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    org.telegram.ui.Components.LinkSpanDrawable r1 = r1.pressedLink
                    if (r1 == 0) goto L_0x0031
                    int r1 = r12.getAction()
                    if (r1 != r9) goto L_0x0031
                    goto L_0x003f
                L_0x0031:
                    int r0 = r12.getAction()
                    r1 = 3
                    if (r0 != r1) goto L_0x014b
                    org.telegram.ui.Cells.AboutLinkCell r0 = org.telegram.ui.Cells.AboutLinkCell.this
                    r0.resetPressedLink()
                    goto L_0x014b
                L_0x003f:
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.widget.TextView r1 = r1.showMoreTextView
                    int r1 = r1.getLeft()
                    if (r0 < r1) goto L_0x0074
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.widget.TextView r1 = r1.showMoreTextView
                    int r1 = r1.getRight()
                    if (r0 > r1) goto L_0x0074
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.widget.TextView r1 = r1.showMoreTextView
                    int r1 = r1.getTop()
                    if (r7 < r1) goto L_0x0074
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.widget.TextView r1 = r1.showMoreTextView
                    int r1 = r1.getBottom()
                    if (r7 > r1) goto L_0x0074
                    boolean r12 = super.onTouchEvent(r12)
                    return r12
                L_0x0074:
                    int r1 = r11.getMeasuredWidth()
                    if (r1 <= 0) goto L_0x008c
                    int r1 = r11.getMeasuredWidth()
                    r2 = 1102577664(0x41b80000, float:23.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r1 = r1 - r2
                    if (r0 <= r1) goto L_0x008c
                    boolean r12 = super.onTouchEvent(r12)
                    return r12
                L_0x008c:
                    int r1 = r12.getAction()
                    if (r1 != 0) goto L_0x0128
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout r1 = r1.firstThreeLinesLayout
                    if (r1 == 0) goto L_0x0103
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    float r1 = r1.expandT
                    r2 = 1065353216(0x3var_, float:1.0)
                    int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                    if (r1 >= 0) goto L_0x0103
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    boolean r1 = r1.shouldExpand
                    if (r1 == 0) goto L_0x0103
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout r2 = r1.firstThreeLinesLayout
                    org.telegram.ui.Cells.AboutLinkCell r3 = org.telegram.ui.Cells.AboutLinkCell.this
                    int r3 = r3.textX
                    org.telegram.ui.Cells.AboutLinkCell r4 = org.telegram.ui.Cells.AboutLinkCell.this
                    int r4 = r4.textY
                    r5 = r0
                    r6 = r7
                    boolean r1 = r1.checkTouchTextLayout(r2, r3, r4, r5, r6)
                    if (r1 == 0) goto L_0x00c9
                    goto L_0x011d
                L_0x00c9:
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout[] r1 = r1.nextLinesLayouts
                    if (r1 == 0) goto L_0x011f
                    r10 = 0
                L_0x00d2:
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout[] r1 = r1.nextLinesLayouts
                    int r1 = r1.length
                    if (r10 >= r1) goto L_0x011f
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout[] r2 = r1.nextLinesLayouts
                    r2 = r2[r10]
                    org.telegram.ui.Cells.AboutLinkCell r3 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.graphics.Point[] r3 = r3.nextLinesLayoutsPositions
                    r3 = r3[r10]
                    int r3 = r3.x
                    org.telegram.ui.Cells.AboutLinkCell r4 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.graphics.Point[] r4 = r4.nextLinesLayoutsPositions
                    r4 = r4[r10]
                    int r4 = r4.y
                    r5 = r0
                    r6 = r7
                    boolean r1 = r1.checkTouchTextLayout(r2, r3, r4, r5, r6)
                    if (r1 == 0) goto L_0x0100
                    goto L_0x011d
                L_0x0100:
                    int r10 = r10 + 1
                    goto L_0x00d2
                L_0x0103:
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    android.text.StaticLayout r2 = r1.textLayout
                    org.telegram.ui.Cells.AboutLinkCell r3 = org.telegram.ui.Cells.AboutLinkCell.this
                    int r3 = r3.textX
                    org.telegram.ui.Cells.AboutLinkCell r4 = org.telegram.ui.Cells.AboutLinkCell.this
                    int r4 = r4.textY
                    r5 = r0
                    r6 = r7
                    boolean r0 = r1.checkTouchTextLayout(r2, r3, r4, r5, r6)
                    if (r0 == 0) goto L_0x011f
                L_0x011d:
                    r0 = 1
                    goto L_0x0120
                L_0x011f:
                    r0 = 0
                L_0x0120:
                    if (r0 != 0) goto L_0x014c
                    org.telegram.ui.Cells.AboutLinkCell r1 = org.telegram.ui.Cells.AboutLinkCell.this
                    r1.resetPressedLink()
                    goto L_0x014c
                L_0x0128:
                    org.telegram.ui.Cells.AboutLinkCell r0 = org.telegram.ui.Cells.AboutLinkCell.this
                    org.telegram.ui.Components.LinkSpanDrawable r0 = r0.pressedLink
                    if (r0 == 0) goto L_0x014b
                    org.telegram.ui.Cells.AboutLinkCell r0 = org.telegram.ui.Cells.AboutLinkCell.this     // Catch:{ Exception -> 0x0140 }
                    org.telegram.ui.Components.LinkSpanDrawable r1 = r0.pressedLink     // Catch:{ Exception -> 0x0140 }
                    android.text.style.CharacterStyle r1 = r1.getSpan()     // Catch:{ Exception -> 0x0140 }
                    android.text.style.ClickableSpan r1 = (android.text.style.ClickableSpan) r1     // Catch:{ Exception -> 0x0140 }
                    r0.onLinkClick(r1)     // Catch:{ Exception -> 0x0140 }
                    goto L_0x0144
                L_0x0140:
                    r0 = move-exception
                    org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                L_0x0144:
                    org.telegram.ui.Cells.AboutLinkCell r0 = org.telegram.ui.Cells.AboutLinkCell.this
                    r0.resetPressedLink()
                    r0 = 1
                    goto L_0x014c
                L_0x014b:
                    r0 = 0
                L_0x014c:
                    if (r0 != 0) goto L_0x0154
                    boolean r12 = super.onTouchEvent(r12)
                    if (r12 == 0) goto L_0x0155
                L_0x0154:
                    r8 = 1
                L_0x0155:
                    return r8
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AboutLinkCell.AnonymousClass1.onTouchEvent(android.view.MotionEvent):boolean");
            }
        };
        this.container = r7;
        r7.setImportantForAccessibility(2);
        this.links = new LinkSpanDrawable.LinkCollector(this.container);
        this.container.setClickable(true);
        this.rippleBackground = Theme.createRadSelectorDrawable(Theme.getColor("listSelectorSDK21", resourcesProvider3), 0, 0);
        TextView textView = new TextView(context2);
        this.valueTextView = textView;
        textView.setVisibility(8);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider3));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        int i = 5;
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setImportantForAccessibility(2);
        this.valueTextView.setFocusable(false);
        this.container.addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        this.bottomShadow = new FrameLayout(context2);
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite", resourcesProvider3), PorterDuff.Mode.SRC_ATOP));
        this.bottomShadow.setBackground(mutate);
        addView(this.bottomShadow, LayoutHelper.createFrame(-1, 12.0f, 87, 0.0f, 0.0f, 0.0f, 0.0f));
        addView(this.container, LayoutHelper.createFrame(-1, -1, 55));
        AnonymousClass2 r3 = new TextView(this, context2) {
            private boolean pressed = false;

            public boolean onTouchEvent(MotionEvent motionEvent) {
                boolean z = this.pressed;
                if (motionEvent.getAction() == 0) {
                    this.pressed = true;
                } else if (motionEvent.getAction() != 2) {
                    this.pressed = false;
                }
                if (z != this.pressed) {
                    invalidate();
                }
                return super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (this.pressed) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                    canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_urlPaint);
                }
                super.onDraw(canvas);
            }
        };
        this.showMoreTextView = r3;
        r3.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText", resourcesProvider3));
        this.showMoreTextView.setTextSize(1, 16.0f);
        this.showMoreTextView.setLines(1);
        this.showMoreTextView.setMaxLines(1);
        this.showMoreTextView.setSingleLine(true);
        this.showMoreTextView.setText(LocaleController.getString("DescriptionMore", NUM));
        this.showMoreTextView.setOnClickListener(new AboutLinkCell$$ExternalSyntheticLambda1(this));
        this.showMoreTextView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        this.showMoreTextBackgroundView = new FrameLayout(context2);
        Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
        this.showMoreBackgroundDrawable = mutate2;
        mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite", resourcesProvider3), PorterDuff.Mode.MULTIPLY));
        this.showMoreTextBackgroundView.setBackground(this.showMoreBackgroundDrawable);
        FrameLayout frameLayout = this.showMoreTextBackgroundView;
        frameLayout.setPadding(frameLayout.getPaddingLeft() + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, AndroidUtilities.dp(3.0f));
        this.showMoreTextBackgroundView.addView(this.showMoreTextView, LayoutHelper.createFrame(-2, -2.0f));
        FrameLayout frameLayout2 = this.showMoreTextBackgroundView;
        addView(frameLayout2, LayoutHelper.createFrame(-2, -2.0f, 85, 22.0f - (((float) frameLayout2.getPaddingLeft()) / AndroidUtilities.density), 0.0f, 22.0f - (((float) this.showMoreTextBackgroundView.getPaddingRight()) / AndroidUtilities.density), 6.0f));
        this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite", resourcesProvider3));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        updateCollapse(true, true);
    }

    private void setShowMoreMarginBottom(int i) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.showMoreTextBackgroundView.getLayoutParams();
        if (layoutParams.bottomMargin != i) {
            layoutParams.bottomMargin = i;
            this.showMoreTextBackgroundView.setLayoutParams(layoutParams);
        }
    }

    public void draw(Canvas canvas) {
        float f;
        View view = (View) getParent();
        if (view == null) {
            f = 1.0f;
        } else {
            f = (float) Math.pow((double) view.getAlpha(), 2.0d);
        }
        drawText(canvas);
        float alpha = this.showMoreTextBackgroundView.getAlpha();
        if (alpha > 0.0f) {
            canvas.save();
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (alpha * 255.0f), 31);
            this.showMoreBackgroundDrawable.setAlpha((int) (f * 255.0f));
            canvas.translate((float) this.showMoreTextBackgroundView.getLeft(), (float) this.showMoreTextBackgroundView.getTop());
            this.showMoreTextBackgroundView.draw(canvas);
            canvas.restore();
        }
        float alpha2 = this.bottomShadow.getAlpha();
        if (alpha2 > 0.0f) {
            canvas.save();
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (alpha2 * 255.0f), 31);
            canvas.translate((float) this.bottomShadow.getLeft(), (float) this.bottomShadow.getTop());
            this.bottomShadow.draw(canvas);
            canvas.restore();
        }
        this.container.draw(canvas);
        super.draw(canvas);
    }

    private void drawText(Canvas canvas) {
        int i;
        StaticLayout staticLayout;
        int i2;
        Canvas canvas2 = canvas;
        canvas.save();
        canvas2.clipRect(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(8.0f), getWidth() - AndroidUtilities.dp(23.0f), getHeight());
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        float f = 0.0f;
        canvas2.translate((float) dp, 0.0f);
        LinkSpanDrawable.LinkCollector linkCollector = this.links;
        if (linkCollector != null && linkCollector.draw(canvas2)) {
            invalidate();
        }
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas2.translate(0.0f, (float) dp2);
        try {
            StaticLayout staticLayout2 = this.firstThreeLinesLayout;
            if (staticLayout2 != null) {
                if (this.shouldExpand) {
                    staticLayout2.draw(canvas2);
                    int lineCount = this.firstThreeLinesLayout.getLineCount() - 1;
                    float lineTop = (float) (this.firstThreeLinesLayout.getLineTop(lineCount) + this.firstThreeLinesLayout.getTopPadding());
                    float lineRight = this.firstThreeLinesLayout.getLineRight(lineCount) + (this.needSpace ? this.SPACE : 0.0f);
                    float lineBottom = (float) ((this.firstThreeLinesLayout.getLineBottom(lineCount) - this.firstThreeLinesLayout.getLineTop(lineCount)) - this.firstThreeLinesLayout.getBottomPadding());
                    float easeInOutCubic = easeInOutCubic(1.0f - ((float) Math.pow((double) this.expandT, 0.25d)));
                    if (this.nextLinesLayouts != null) {
                        float f2 = lineRight;
                        int i3 = 0;
                        while (true) {
                            StaticLayout[] staticLayoutArr = this.nextLinesLayouts;
                            if (i3 >= staticLayoutArr.length) {
                                break;
                            }
                            StaticLayout staticLayout3 = staticLayoutArr[i3];
                            if (staticLayout3 != null) {
                                int save = canvas.save();
                                Point[] pointArr = this.nextLinesLayoutsPositions;
                                if (pointArr[i3] != null) {
                                    pointArr[i3].set((int) (((float) this.textX) + (f2 * easeInOutCubic)), (int) (((float) this.textY) + lineTop + ((1.0f - easeInOutCubic) * lineBottom)));
                                }
                                int i4 = this.lastInlineLine;
                                if (i4 == -1 || i4 > i3) {
                                    i2 = save;
                                    staticLayout = staticLayout3;
                                    i = i3;
                                    canvas2.translate(f2 * easeInOutCubic, ((1.0f - easeInOutCubic) * lineBottom) + lineTop);
                                } else {
                                    canvas2.translate(f, lineTop + lineBottom);
                                    i2 = save;
                                    staticLayout = staticLayout3;
                                    i = i3;
                                    canvas.saveLayerAlpha(0.0f, 0.0f, (float) staticLayout3.getWidth(), (float) staticLayout3.getHeight(), (int) (this.expandT * 255.0f), 31);
                                }
                                StaticLayout staticLayout4 = staticLayout;
                                staticLayout4.draw(canvas2);
                                canvas2.restoreToCount(i2);
                                f2 += staticLayout4.getLineRight(0) + this.SPACE;
                                lineBottom += (float) (staticLayout4.getLineBottom(0) + staticLayout4.getTopPadding());
                            } else {
                                i = i3;
                            }
                            i3 = i + 1;
                            f = 0.0f;
                        }
                    }
                    canvas.restore();
                }
            }
            StaticLayout staticLayout5 = this.textLayout;
            if (staticLayout5 != null) {
                staticLayout5.draw(canvas2);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        canvas.restore();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.container.setOnClickListener(onClickListener);
    }

    /* access modifiers changed from: private */
    public void resetPressedLink() {
        this.links.clear();
        this.pressedLink = null;
        AndroidUtilities.cancelRunOnUIThread(this.longPressedRunnable);
        invalidate();
    }

    public void setText(String str, boolean z) {
        setTextAndValue(str, (String) null, z);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        if (!TextUtils.isEmpty(str) && !TextUtils.equals(str, this.oldText)) {
            try {
                this.oldText = AndroidUtilities.getSafeString(str);
            } catch (Throwable unused) {
                this.oldText = str;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.oldText);
            this.stringBuilder = spannableStringBuilder;
            MessageObject.addLinks(false, spannableStringBuilder, false, false, !z);
            Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (this.lastMaxWidth <= 0) {
                this.lastMaxWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(46.0f);
            }
            checkTextLayout(this.lastMaxWidth, true);
            updateHeight();
            int visibility = this.valueTextView.getVisibility();
            if (TextUtils.isEmpty(str2)) {
                this.valueTextView.setVisibility(8);
            } else {
                this.valueTextView.setText(str2);
                this.valueTextView.setVisibility(0);
            }
            if (visibility != this.valueTextView.getVisibility()) {
                checkTextLayout(this.lastMaxWidth, true);
            }
            requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public boolean checkTouchTextLayout(StaticLayout staticLayout, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        try {
            int lineForVertical = staticLayout.getLineForVertical(i6);
            float f = (float) i5;
            int offsetForHorizontal = staticLayout.getOffsetForHorizontal(lineForVertical, f);
            float lineLeft = staticLayout.getLineLeft(lineForVertical);
            if (lineLeft <= f && lineLeft + staticLayout.getLineWidth(lineForVertical) >= f && i6 >= 0 && i6 <= staticLayout.getHeight()) {
                Spannable spannable = (Spannable) staticLayout.getText();
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                if (clickableSpanArr.length != 0 && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                    resetPressedLink();
                    LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(clickableSpanArr[0], this.parentFragment.getResourceProvider(), (float) i3, (float) i4);
                    this.pressedLink = linkSpanDrawable;
                    this.links.addLink(linkSpanDrawable);
                    int spanStart = spannable.getSpanStart(this.pressedLink.getSpan());
                    int spanEnd = spannable.getSpanEnd(this.pressedLink.getSpan());
                    LinkPath obtainNewPath = this.pressedLink.obtainNewPath();
                    obtainNewPath.setCurrentLayout(staticLayout, spanStart, (float) i2);
                    staticLayout.getSelectionPath(spanStart, spanEnd, obtainNewPath);
                    AndroidUtilities.runOnUIThread(this.longPressedRunnable, (long) ViewConfiguration.getLongPressTimeout());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void onLinkClick(ClickableSpan clickableSpan) {
        if (clickableSpan instanceof URLSpanNoUnderline) {
            String url = ((URLSpanNoUnderline) clickableSpan).getURL();
            if (url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) {
                didPressUrl(url);
            }
        } else if (clickableSpan instanceof URLSpan) {
            String url2 = ((URLSpan) clickableSpan).getURL();
            if (AndroidUtilities.shouldShowUrlInAlert(url2)) {
                AlertsCreator.showOpenUrlAlert(this.parentFragment, url2, true, true);
            } else {
                Browser.openUrl(getContext(), url2);
            }
        } else {
            clickableSpan.onClick(this);
        }
    }

    public class SpringInterpolator {
        public float friction;
        private float position = 0.0f;
        public float tension;
        private float velocity = 0.0f;

        public SpringInterpolator(AboutLinkCell aboutLinkCell, float f, float f2) {
            this.tension = f;
            this.friction = f2;
        }

        public float getValue(float f) {
            float min = Math.min(f, 250.0f);
            while (min > 0.0f) {
                float min2 = Math.min(min, 18.0f);
                step(min2);
                min -= min2;
            }
            return this.position;
        }

        private void step(float f) {
            float f2 = this.position;
            float f3 = this.velocity;
            float f4 = f3 + ((((((-this.tension) * 1.0E-6f) * (f2 - 1.0f)) + (((-this.friction) * 0.001f) * f3)) / 1.0f) * f);
            this.velocity = f4;
            this.position = f2 + (f4 * f);
        }
    }

    public void updateCollapse(boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.collapseAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.collapseAnimator = null;
        }
        float f = this.expandT;
        float f2 = z ? 1.0f : 0.0f;
        if (z2) {
            if (f2 > 0.0f) {
                didExtend();
            }
            float textHeight = (float) textHeight();
            float min = Math.min((float) COLLAPSED_HEIGHT, textHeight);
            Math.abs(AndroidUtilities.lerp(min, textHeight, f2) - AndroidUtilities.lerp(min, textHeight, f));
            this.collapseAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.collapseAnimator.addUpdateListener(new AboutLinkCell$$ExternalSyntheticLambda0(this, new AtomicReference(Float.valueOf(f)), f, f2, new SpringInterpolator(this, 380.0f, 20.17f)));
            this.collapseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AboutLinkCell.this.didResizeEnd();
                    if (AboutLinkCell.this.container.getBackground() == null) {
                        AboutLinkCell.this.container.setBackground(AboutLinkCell.this.rippleBackground);
                    }
                    boolean unused = AboutLinkCell.this.expanded = true;
                }

                public void onAnimationStart(Animator animator) {
                    AboutLinkCell.this.didResizeStart();
                }
            });
            this.collapseAnimator.setDuration((long) (Math.abs(f - f2) * 1250.0f * 2.0f));
            this.collapseAnimator.start();
            return;
        }
        this.expandT = f2;
        forceLayout();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCollapse$1(AtomicReference atomicReference, float f, float f2, SpringInterpolator springInterpolator, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        AndroidUtilities.lerp(f, f2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        float lerp = AndroidUtilities.lerp(f, f2, springInterpolator.getValue((floatValue - ((Float) atomicReference.getAndSet(Float.valueOf(floatValue))).floatValue()) * 1000.0f * 8.0f));
        this.expandT = lerp;
        if (lerp > 0.8f && this.container.getBackground() == null) {
            this.container.setBackground(this.rippleBackground);
        }
        this.showMoreTextBackgroundView.setAlpha(1.0f - this.expandT);
        this.bottomShadow.setAlpha((float) Math.pow((double) (1.0f - this.expandT), 2.0d));
        updateHeight();
        this.container.invalidate();
    }

    private int fromHeight() {
        return Math.min(COLLAPSED_HEIGHT + (this.valueTextView.getVisibility() == 0 ? AndroidUtilities.dp(20.0f) : 0), textHeight());
    }

    private int updateHeight() {
        int textHeight = textHeight();
        float fromHeight = (float) fromHeight();
        if (this.shouldExpand) {
            textHeight = (int) AndroidUtilities.lerp(fromHeight, (float) textHeight, this.expandT);
        }
        setHeight(textHeight);
        return textHeight;
    }

    private void setHeight(int i) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) getLayoutParams();
        boolean z = true;
        if (layoutParams == null) {
            if (getMinimumHeight() == 0) {
                getHeight();
            } else {
                getMinimumHeight();
            }
            layoutParams = new RecyclerView.LayoutParams(-1, i);
        } else {
            if (layoutParams.height == i) {
                z = false;
            }
            layoutParams.height = i;
        }
        if (z) {
            setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        checkTextLayout(View.MeasureSpec.getSize(i) - AndroidUtilities.dp(46.0f), false);
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(updateHeight(), NUM));
    }

    private StaticLayout makeTextLayout(CharSequence charSequence, int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            return StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), Theme.profile_aboutTextPaint, i).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
        }
        return new StaticLayout(charSequence, Theme.profile_aboutTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    private void checkTextLayout(int i, boolean z) {
        int i2 = 0;
        if (this.moreButtonDisabled) {
            this.shouldExpand = false;
        }
        SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
        if (spannableStringBuilder != null && (i != this.lastMaxWidth || z)) {
            StaticLayout makeTextLayout = makeTextLayout(spannableStringBuilder, i);
            this.textLayout = makeTextLayout;
            this.shouldExpand = makeTextLayout.getLineCount() >= 4;
            if (this.textLayout.getLineCount() >= 3 && this.shouldExpand) {
                int max = Math.max(this.textLayout.getLineStart(2), this.textLayout.getLineEnd(2));
                if (this.stringBuilder.charAt(max - 1) == 10) {
                    max--;
                }
                int i3 = max - 1;
                this.needSpace = (this.stringBuilder.charAt(i3) == ' ' || this.stringBuilder.charAt(i3) == 10) ? false : true;
                this.firstThreeLinesLayout = makeTextLayout(this.stringBuilder.subSequence(0, max), i);
                this.nextLinesLayouts = new StaticLayout[(this.textLayout.getLineCount() - 3)];
                this.nextLinesLayoutsPositions = new Point[(this.textLayout.getLineCount() - 3)];
                float lineRight = this.firstThreeLinesLayout.getLineRight(this.firstThreeLinesLayout.getLineCount() - 1) + (this.needSpace ? this.SPACE : 0.0f);
                this.lastInlineLine = -1;
                if (this.showMoreTextBackgroundView.getMeasuredWidth() <= 0) {
                    FrameLayout frameLayout = this.showMoreTextBackgroundView;
                    int i4 = MOST_SPEC;
                    frameLayout.measure(i4, i4);
                }
                for (int i5 = 3; i5 < this.textLayout.getLineCount(); i5++) {
                    int lineStart = this.textLayout.getLineStart(i5);
                    int lineEnd = this.textLayout.getLineEnd(i5);
                    StaticLayout makeTextLayout2 = makeTextLayout(this.stringBuilder.subSequence(Math.min(lineStart, lineEnd), Math.max(lineStart, lineEnd)), i);
                    int i6 = i5 - 3;
                    this.nextLinesLayouts[i6] = makeTextLayout2;
                    this.nextLinesLayoutsPositions[i6] = new Point();
                    if (this.lastInlineLine == -1 && lineRight > ((float) ((i - this.showMoreTextBackgroundView.getMeasuredWidth()) + this.showMoreTextBackgroundView.getPaddingLeft()))) {
                        this.lastInlineLine = i6;
                    }
                    lineRight += makeTextLayout2.getLineRight(0) + this.SPACE;
                }
                if (lineRight < ((float) ((i - this.showMoreTextBackgroundView.getMeasuredWidth()) + this.showMoreTextBackgroundView.getPaddingLeft()))) {
                    this.shouldExpand = false;
                }
            }
            if (!this.shouldExpand) {
                this.firstThreeLinesLayout = null;
                this.nextLinesLayouts = null;
            }
            this.lastMaxWidth = i;
            this.container.setMinimumHeight(textHeight());
            if (this.shouldExpand && this.firstThreeLinesLayout != null) {
                int fromHeight = fromHeight() - AndroidUtilities.dp(8.0f);
                StaticLayout staticLayout = this.firstThreeLinesLayout;
                setShowMoreMarginBottom((((fromHeight - staticLayout.getLineBottom(staticLayout.getLineCount() - 1)) - this.showMoreTextBackgroundView.getPaddingBottom()) - this.showMoreTextView.getPaddingBottom()) - (this.showMoreTextView.getLayout() == null ? 0 : this.showMoreTextView.getLayout().getHeight() - this.showMoreTextView.getLayout().getLineBottom(this.showMoreTextView.getLineCount() - 1)));
            }
        }
        TextView textView = this.showMoreTextView;
        if (!this.shouldExpand) {
            i2 = 8;
        }
        textView.setVisibility(i2);
        if (!this.shouldExpand && this.container.getBackground() == null) {
            this.container.setBackground(this.rippleBackground);
        }
        if (this.shouldExpand && this.expandT < 1.0f && this.container.getBackground() != null) {
            this.container.setBackground((Drawable) null);
        }
    }

    private int textHeight() {
        StaticLayout staticLayout = this.textLayout;
        int height = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        return this.valueTextView.getVisibility() == 0 ? height + AndroidUtilities.dp(23.0f) : height;
    }

    public boolean onClick() {
        if (!this.shouldExpand || this.expandT > 0.0f) {
            return false;
        }
        updateCollapse(true, true);
        return true;
    }

    private float easeInOutCubic(float f) {
        return ((double) f) < 0.5d ? 4.0f * f * f * f : 1.0f - (((float) Math.pow((double) ((f * -2.0f) + 2.0f), 3.0d)) / 2.0f);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.textLayout != null) {
            SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
            CharSequence text = this.valueTextView.getText();
            accessibilityNodeInfo.setClassName("android.widget.TextView");
            if (TextUtils.isEmpty(text)) {
                accessibilityNodeInfo.setText(spannableStringBuilder);
                return;
            }
            accessibilityNodeInfo.setText(text + ": " + spannableStringBuilder);
        }
    }

    public void setMoreButtonDisabled(boolean z) {
        this.moreButtonDisabled = z;
    }
}
