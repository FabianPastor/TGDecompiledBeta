package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
    private static final int COLLAPSED_HEIGHT;
    private static final int MAX_OPEN_HEIGHT;
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
    public boolean expanded = false;
    /* access modifiers changed from: private */
    public StaticLayout firstThreeLinesLayout;
    private int lastInlineLine = -1;
    private int lastMaxWidth = 0;
    private LinkSpanDrawable.LinkCollector links;
    Runnable longPressedRunnable = new Runnable() {
        public void run() {
            String url;
            if (AboutLinkCell.this.pressedLink != null) {
                if (AboutLinkCell.this.pressedLink.getSpan() instanceof URLSpanNoUnderline) {
                    url = ((URLSpanNoUnderline) AboutLinkCell.this.pressedLink.getSpan()).getURL();
                } else if (AboutLinkCell.this.pressedLink.getSpan() instanceof URLSpan) {
                    url = ((URLSpan) AboutLinkCell.this.pressedLink.getSpan()).getURL();
                } else {
                    url = AboutLinkCell.this.pressedLink.getSpan().toString();
                }
                try {
                    AboutLinkCell.this.performHapticFeedback(0, 2);
                } catch (Exception e) {
                }
                BottomSheet.Builder builder = new BottomSheet.Builder(AboutLinkCell.this.parentFragment.getParentActivity());
                builder.setTitle(url);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new AboutLinkCell$3$$ExternalSyntheticLambda0(this, (ClickableSpan) AboutLinkCell.this.pressedLink.getSpan(), url));
                builder.setOnPreDismissListener(new AboutLinkCell$3$$ExternalSyntheticLambda1(this));
                builder.show();
                LinkSpanDrawable unused = AboutLinkCell.this.pressedLink = null;
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Cells-AboutLinkCell$3  reason: not valid java name */
        public /* synthetic */ void m1484lambda$run$0$orgtelegramuiCellsAboutLinkCell$3(ClickableSpan pressedLinkFinal, String url, DialogInterface dialog, int which) {
            if (which == 0) {
                AboutLinkCell.this.onLinkClick(pressedLinkFinal);
            } else if (which == 1) {
                AndroidUtilities.addToClipboard(url);
                if (Build.VERSION.SDK_INT >= 31) {
                    return;
                }
                if (url.startsWith("@")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("UsernameCopied", NUM)).show();
                } else if (url.startsWith("#") || url.startsWith("$")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("HashtagCopied", NUM)).show();
                } else {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("LinkCopied", NUM)).show();
                }
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-Cells-AboutLinkCell$3  reason: not valid java name */
        public /* synthetic */ void m1485lambda$run$1$orgtelegramuiCellsAboutLinkCell$3(DialogInterface di) {
            AboutLinkCell.this.resetPressedLink();
        }
    };
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
    private float rawCollapseT = 0.0f;
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
    private LinkPath urlPath = new LinkPath(true);
    private Point urlPathOffset = new Point();
    private TextView valueTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AboutLinkCell(Context context, BaseFragment fragment) {
        super(context);
        Context context2 = context;
        this.parentFragment = fragment;
        this.container = new FrameLayout(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                boolean result = false;
                if (!(AboutLinkCell.this.textLayout == null && AboutLinkCell.this.nextLinesLayouts == null)) {
                    if (event.getAction() == 0 || (AboutLinkCell.this.pressedLink != null && event.getAction() == 1)) {
                        if (x >= AboutLinkCell.this.showMoreTextView.getLeft() && x <= AboutLinkCell.this.showMoreTextView.getRight() && y >= AboutLinkCell.this.showMoreTextView.getTop() && y <= AboutLinkCell.this.showMoreTextView.getBottom()) {
                            return super.onTouchEvent(event);
                        }
                        if (getMeasuredWidth() > 0 && x > getMeasuredWidth() - AndroidUtilities.dp(23.0f)) {
                            return super.onTouchEvent(event);
                        }
                        if (event.getAction() == 0) {
                            if (AboutLinkCell.this.firstThreeLinesLayout == null || AboutLinkCell.this.expandT >= 1.0f || !AboutLinkCell.this.shouldExpand) {
                                AboutLinkCell aboutLinkCell = AboutLinkCell.this;
                                if (aboutLinkCell.checkTouchTextLayout(aboutLinkCell.textLayout, AboutLinkCell.this.textX, AboutLinkCell.this.textY, x, y)) {
                                    result = true;
                                }
                            } else {
                                AboutLinkCell aboutLinkCell2 = AboutLinkCell.this;
                                if (aboutLinkCell2.checkTouchTextLayout(aboutLinkCell2.firstThreeLinesLayout, AboutLinkCell.this.textX, AboutLinkCell.this.textY, x, y)) {
                                    result = true;
                                } else if (AboutLinkCell.this.nextLinesLayouts != null) {
                                    int i = 0;
                                    while (true) {
                                        if (i >= AboutLinkCell.this.nextLinesLayouts.length) {
                                            break;
                                        }
                                        AboutLinkCell aboutLinkCell3 = AboutLinkCell.this;
                                        if (aboutLinkCell3.checkTouchTextLayout(aboutLinkCell3.nextLinesLayouts[i], AboutLinkCell.this.nextLinesLayoutsPositions[i].x, AboutLinkCell.this.nextLinesLayoutsPositions[i].y, x, y)) {
                                            result = true;
                                            break;
                                        }
                                        i++;
                                    }
                                }
                            }
                            if (!result) {
                                AboutLinkCell.this.resetPressedLink();
                            }
                        } else if (AboutLinkCell.this.pressedLink != null) {
                            try {
                                AboutLinkCell aboutLinkCell4 = AboutLinkCell.this;
                                aboutLinkCell4.onLinkClick((ClickableSpan) aboutLinkCell4.pressedLink.getSpan());
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                            AboutLinkCell.this.resetPressedLink();
                            result = true;
                        }
                    } else if (event.getAction() == 3) {
                        AboutLinkCell.this.resetPressedLink();
                    }
                }
                if (result || super.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        this.links = new LinkSpanDrawable.LinkCollector(this.container);
        this.container.setClickable(true);
        this.rippleBackground = Theme.createRadSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0, 0);
        TextView textView = new TextView(context2);
        this.valueTextView = textView;
        textView.setVisibility(8);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        int i = 5;
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setImportantForAccessibility(2);
        this.container.addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        this.bottomShadow = new FrameLayout(context2);
        Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.SRC_ATOP));
        this.bottomShadow.setBackground(shadowDrawable);
        addView(this.bottomShadow, LayoutHelper.createFrame(-1, 12.0f, 87, 0.0f, 0.0f, 0.0f, 0.0f));
        addView(this.container, LayoutHelper.createFrame(-1, -1, 55));
        AnonymousClass2 r5 = new TextView(context2) {
            private boolean pressed = false;

            public boolean onTouchEvent(MotionEvent event) {
                boolean wasPressed = this.pressed;
                if (event.getAction() == 0) {
                    this.pressed = true;
                } else if (event.getAction() != 2) {
                    this.pressed = false;
                }
                if (wasPressed != this.pressed) {
                    invalidate();
                }
                return super.onTouchEvent(event);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (this.pressed) {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_urlPaint);
                }
                super.onDraw(canvas);
            }
        };
        this.showMoreTextView = r5;
        r5.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
        this.showMoreTextView.setTextSize(1, 16.0f);
        this.showMoreTextView.setLines(1);
        this.showMoreTextView.setMaxLines(1);
        this.showMoreTextView.setSingleLine(true);
        this.showMoreTextView.setText(LocaleController.getString("DescriptionMore", NUM));
        this.showMoreTextView.setOnClickListener(new AboutLinkCell$$ExternalSyntheticLambda1(this));
        this.showMoreTextView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        this.showMoreTextBackgroundView = new FrameLayout(context2);
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.showMoreBackgroundDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        this.showMoreTextBackgroundView.setBackground(this.showMoreBackgroundDrawable);
        FrameLayout frameLayout = this.showMoreTextBackgroundView;
        frameLayout.setPadding(frameLayout.getPaddingLeft() + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, AndroidUtilities.dp(3.0f));
        this.showMoreTextBackgroundView.addView(this.showMoreTextView, LayoutHelper.createFrame(-2, -2.0f));
        FrameLayout frameLayout2 = this.showMoreTextBackgroundView;
        addView(frameLayout2, LayoutHelper.createFrame(-2, -2.0f, 85, 22.0f - (((float) frameLayout2.getPaddingLeft()) / AndroidUtilities.density), 0.0f, 22.0f - (((float) this.showMoreTextBackgroundView.getPaddingRight()) / AndroidUtilities.density), 6.0f));
        this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        setWillNotDraw(false);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-AboutLinkCell  reason: not valid java name */
    public /* synthetic */ void m1482lambda$new$0$orgtelegramuiCellsAboutLinkCell(View e) {
        updateCollapse(true, true);
    }

    private void setShowMoreMarginBottom(int marginBottom) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.showMoreTextBackgroundView.getLayoutParams();
        if (lp.bottomMargin != marginBottom) {
            lp.bottomMargin = marginBottom;
            this.showMoreTextBackgroundView.setLayoutParams(lp);
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return false;
    }

    public void draw(Canvas canvas) {
        View parent = (View) getParent();
        float alpha = parent == null ? 1.0f : (float) Math.pow((double) parent.getAlpha(), 2.0d);
        drawText(canvas);
        float viewAlpha = this.showMoreTextBackgroundView.getAlpha();
        if (viewAlpha > 0.0f) {
            canvas.save();
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (viewAlpha * 255.0f), 31);
            this.showMoreBackgroundDrawable.setAlpha((int) (alpha * 255.0f));
            canvas.translate((float) this.showMoreTextBackgroundView.getLeft(), (float) this.showMoreTextBackgroundView.getTop());
            this.showMoreTextBackgroundView.draw(canvas);
            canvas.restore();
        }
        float viewAlpha2 = this.bottomShadow.getAlpha();
        if (viewAlpha2 > 0.0f) {
            canvas.save();
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (255.0f * viewAlpha2), 31);
            canvas.translate((float) this.bottomShadow.getLeft(), (float) this.bottomShadow.getTop());
            this.bottomShadow.draw(canvas);
            canvas.restore();
        }
        this.container.draw(canvas);
        super.draw(canvas);
    }

    private void drawText(Canvas canvas) {
        int line;
        StaticLayout layout;
        int c;
        Canvas canvas2 = canvas;
        canvas.save();
        canvas2.clipRect(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(8.0f), getWidth() - AndroidUtilities.dp(23.0f), getHeight());
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        int line2 = 0;
        canvas2.translate((float) dp, 0.0f);
        LinkSpanDrawable.LinkCollector linkCollector = this.links;
        if (linkCollector != null && linkCollector.draw(canvas2)) {
            invalidate();
        }
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas2.translate(0.0f, (float) dp2);
        try {
            StaticLayout staticLayout = this.firstThreeLinesLayout;
            if (staticLayout != null) {
                if (this.shouldExpand) {
                    staticLayout.draw(canvas2);
                    int lastLine = this.firstThreeLinesLayout.getLineCount() - 1;
                    float top = (float) (this.firstThreeLinesLayout.getLineTop(lastLine) + this.firstThreeLinesLayout.getTopPadding());
                    float x = this.firstThreeLinesLayout.getLineRight(lastLine) + (this.needSpace ? this.SPACE : 0.0f);
                    float y = (float) ((this.firstThreeLinesLayout.getLineBottom(lastLine) - this.firstThreeLinesLayout.getLineTop(lastLine)) - this.firstThreeLinesLayout.getBottomPadding());
                    float t = easeInOutCubic(1.0f - ((float) Math.pow((double) this.expandT, 0.25d)));
                    if (this.nextLinesLayouts != null) {
                        float x2 = x;
                        float y2 = y;
                        int line3 = 0;
                        while (true) {
                            StaticLayout[] staticLayoutArr = this.nextLinesLayouts;
                            if (line3 >= staticLayoutArr.length) {
                                break;
                            }
                            StaticLayout layout2 = staticLayoutArr[line3];
                            if (layout2 != null) {
                                int c2 = canvas.save();
                                Point[] pointArr = this.nextLinesLayoutsPositions;
                                if (pointArr[line3] != null) {
                                    pointArr[line3].set((int) (((float) this.textX) + (x2 * t)), (int) (((float) this.textY) + top + ((1.0f - t) * y2)));
                                }
                                int i = this.lastInlineLine;
                                if (i == -1 || i > line3) {
                                    c = c2;
                                    layout = layout2;
                                    line = line3;
                                    canvas2.translate(x2 * t, ((1.0f - t) * y2) + top);
                                } else {
                                    canvas2.translate(line2, top + y2);
                                    c = c2;
                                    layout = layout2;
                                    line = line3;
                                    canvas.saveLayerAlpha(0.0f, 0.0f, (float) layout2.getWidth(), (float) layout2.getHeight(), (int) (this.expandT * 255.0f), 31);
                                }
                                StaticLayout layout3 = layout;
                                layout3.draw(canvas2);
                                canvas2.restoreToCount(c);
                                x2 += layout3.getLineRight(0) + this.SPACE;
                                y2 += (float) (layout3.getLineBottom(0) + layout3.getTopPadding());
                            } else {
                                line = line3;
                            }
                            line3 = line + 1;
                            line2 = 0;
                        }
                        int i2 = line3;
                    }
                    canvas.restore();
                }
            }
            StaticLayout staticLayout2 = this.textLayout;
            if (staticLayout2 != null) {
                staticLayout2.draw(canvas2);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        canvas.restore();
    }

    public void setOnClickListener(View.OnClickListener l) {
        this.container.setOnClickListener(l);
    }

    /* access modifiers changed from: protected */
    public void didPressUrl(String url) {
    }

    /* access modifiers changed from: protected */
    public void didResizeStart() {
    }

    /* access modifiers changed from: protected */
    public void didResizeEnd() {
    }

    /* access modifiers changed from: protected */
    public void didExtend() {
    }

    /* access modifiers changed from: private */
    public void resetPressedLink() {
        this.links.clear();
        this.pressedLink = null;
        AndroidUtilities.cancelRunOnUIThread(this.longPressedRunnable);
        invalidate();
    }

    public void setText(String text, boolean parseLinks) {
        setTextAndValue(text, (String) null, parseLinks);
    }

    public void setTextAndValue(String text, String value, boolean parseLinks) {
        if (!TextUtils.isEmpty(text) && !TextUtils.equals(text, this.oldText)) {
            try {
                this.oldText = AndroidUtilities.getSafeString(text);
            } catch (Throwable th) {
                this.oldText = text;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.oldText);
            this.stringBuilder = spannableStringBuilder;
            MessageObject.addLinks(false, spannableStringBuilder, false, false, !parseLinks);
            Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (this.lastMaxWidth <= 0) {
                this.lastMaxWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(46.0f);
            }
            checkTextLayout(this.lastMaxWidth, true);
            updateHeight();
            int wasValueVisibility = this.valueTextView.getVisibility();
            if (TextUtils.isEmpty(value)) {
                this.valueTextView.setVisibility(8);
            } else {
                this.valueTextView.setText(value);
                this.valueTextView.setVisibility(0);
            }
            if (wasValueVisibility != this.valueTextView.getVisibility()) {
                checkTextLayout(this.lastMaxWidth, true);
            }
            requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public boolean checkTouchTextLayout(StaticLayout textLayout2, int textX2, int textY2, int ex, int ey) {
        StaticLayout staticLayout = textLayout2;
        int i = textY2;
        int i2 = ex;
        int i3 = ey;
        int x = i2 - textX2;
        int y = i3 - i;
        try {
            int line = staticLayout.getLineForVertical(y);
            int off = staticLayout.getOffsetForHorizontal(line, (float) x);
            float left = staticLayout.getLineLeft(line);
            if (left > ((float) x) || staticLayout.getLineWidth(line) + left < ((float) x) || y < 0 || y > textLayout2.getHeight()) {
                int i4 = x;
                return false;
            }
            Spannable buffer = (Spannable) textLayout2.getText();
            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length == 0) {
                return false;
            }
            resetPressedLink();
            int i5 = x;
            LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(link[0], this.parentFragment.getResourceProvider(), (float) i2, (float) i3);
            this.pressedLink = linkSpanDrawable;
            this.links.addLink(linkSpanDrawable);
            int start = buffer.getSpanStart(this.pressedLink.getSpan());
            int end = buffer.getSpanEnd(this.pressedLink.getSpan());
            LinkPath path = this.pressedLink.obtainNewPath();
            path.setCurrentLayout(staticLayout, start, (float) i);
            staticLayout.getSelectionPath(start, end, path);
            int i6 = start;
            AndroidUtilities.runOnUIThread(this.longPressedRunnable, (long) ViewConfiguration.getLongPressTimeout());
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void onLinkClick(ClickableSpan pressedLink2) {
        if (pressedLink2 instanceof URLSpanNoUnderline) {
            String url = ((URLSpanNoUnderline) pressedLink2).getURL();
            if (url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) {
                didPressUrl(url);
            }
        } else if (pressedLink2 instanceof URLSpan) {
            String url2 = ((URLSpan) pressedLink2).getURL();
            if (AndroidUtilities.shouldShowUrlInAlert(url2)) {
                AlertsCreator.showOpenUrlAlert(this.parentFragment, url2, true, true);
            } else {
                Browser.openUrl(getContext(), url2);
            }
        } else {
            pressedLink2.onClick(this);
        }
    }

    static {
        int dp = AndroidUtilities.dp(76.0f);
        COLLAPSED_HEIGHT = dp;
        MAX_OPEN_HEIGHT = dp;
    }

    private class SpringInterpolator {
        public float friction;
        private final float mass = 1.0f;
        private float position = 0.0f;
        public float tension;
        private float velocity = 0.0f;

        public SpringInterpolator(float tension2, float friction2) {
            this.tension = tension2;
            this.friction = friction2;
        }

        public float getValue(float deltaTime) {
            float deltaTime2 = Math.min(deltaTime, 250.0f);
            while (deltaTime2 > 0.0f) {
                float step = Math.min(deltaTime2, 18.0f);
                step(step);
                deltaTime2 -= step;
            }
            return this.position;
        }

        private void step(float delta) {
            float f = this.position;
            float f2 = this.velocity;
            float f3 = f2 + ((((((-this.tension) * 1.0E-6f) * (f - 1.0f)) + (((-this.friction) * 0.001f) * f2)) / 1.0f) * delta);
            this.velocity = f3;
            this.position = f + (f3 * delta);
        }
    }

    public void updateCollapse(boolean value, boolean animated) {
        ValueAnimator valueAnimator = this.collapseAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.collapseAnimator = null;
        }
        float fromValue = this.expandT;
        float toValue = value ? 1.0f : 0.0f;
        if (animated) {
            if (toValue > 0.0f) {
                didExtend();
            }
            float fullHeight = (float) textHeight();
            float collapsedHeight = Math.min((float) COLLAPSED_HEIGHT, fullHeight);
            float abs = Math.abs(AndroidUtilities.lerp(collapsedHeight, fullHeight, toValue) - AndroidUtilities.lerp(collapsedHeight, fullHeight, fromValue));
            this.collapseAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            float duration = Math.abs(fromValue - toValue) * 1250.0f * 2.0f;
            SpringInterpolator spring = new SpringInterpolator(380.0f, 20.17f);
            AtomicReference<Float> lastValue = new AtomicReference<>(Float.valueOf(fromValue));
            ValueAnimator valueAnimator2 = this.collapseAnimator;
            float f = fromValue;
            float f2 = fromValue;
            AboutLinkCell$$ExternalSyntheticLambda0 aboutLinkCell$$ExternalSyntheticLambda0 = r0;
            AboutLinkCell$$ExternalSyntheticLambda0 aboutLinkCell$$ExternalSyntheticLambda02 = new AboutLinkCell$$ExternalSyntheticLambda0(this, lastValue, f, toValue, spring);
            valueAnimator2.addUpdateListener(aboutLinkCell$$ExternalSyntheticLambda0);
            this.collapseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AboutLinkCell.this.didResizeEnd();
                    if (AboutLinkCell.this.container.getBackground() == null) {
                        AboutLinkCell.this.container.setBackground(AboutLinkCell.this.rippleBackground);
                    }
                    boolean unused = AboutLinkCell.this.expanded = true;
                }

                public void onAnimationStart(Animator animation) {
                    AboutLinkCell.this.didResizeStart();
                }
            });
            this.collapseAnimator.setDuration((long) duration);
            this.collapseAnimator.start();
            return;
        }
        this.expandT = toValue;
        forceLayout();
    }

    /* renamed from: lambda$updateCollapse$1$org-telegram-ui-Cells-AboutLinkCell  reason: not valid java name */
    public /* synthetic */ void m1483lambda$updateCollapse$1$orgtelegramuiCellsAboutLinkCell(AtomicReference lastValue, float fromValue, float toValue, SpringInterpolator spring, ValueAnimator a) {
        float now = ((Float) a.getAnimatedValue()).floatValue();
        this.rawCollapseT = AndroidUtilities.lerp(fromValue, toValue, ((Float) a.getAnimatedValue()).floatValue());
        float lerp = AndroidUtilities.lerp(fromValue, toValue, spring.getValue((now - ((Float) lastValue.getAndSet(Float.valueOf(now))).floatValue()) * 1000.0f * 8.0f));
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
        int height = this.shouldExpand ? (int) AndroidUtilities.lerp((float) fromHeight(), (float) textHeight, this.expandT) : textHeight;
        setHeight(height);
        return height;
    }

    private void setHeight(int height) {
        boolean newHeight;
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
        if (lp == null) {
            newHeight = true;
            if (getMinimumHeight() == 0) {
                int height2 = getHeight();
            } else {
                int minimumHeight = getMinimumHeight();
            }
            lp = new RecyclerView.LayoutParams(-1, height);
        } else {
            newHeight = lp.height != height;
            lp.height = height;
        }
        if (newHeight) {
            setLayoutParams(lp);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkTextLayout(View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(46.0f), false);
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(updateHeight(), NUM));
    }

    private StaticLayout makeTextLayout(CharSequence string, int width) {
        if (Build.VERSION.SDK_INT >= 24) {
            return StaticLayout.Builder.obtain(string, 0, string.length(), Theme.profile_aboutTextPaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
        }
        return new StaticLayout(string, Theme.profile_aboutTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    private void checkTextLayout(int maxWidth, boolean force) {
        int i = maxWidth;
        SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
        int i2 = 0;
        if (spannableStringBuilder != null && (i != this.lastMaxWidth || force)) {
            StaticLayout makeTextLayout = makeTextLayout(spannableStringBuilder, i);
            this.textLayout = makeTextLayout;
            this.shouldExpand = makeTextLayout.getLineCount() >= 4;
            if (this.textLayout.getLineCount() >= 3 && this.shouldExpand) {
                int end = Math.max(this.textLayout.getLineStart(2), this.textLayout.getLineEnd(2));
                if (this.stringBuilder.charAt(end - 1) == 10) {
                    end--;
                }
                this.needSpace = (this.stringBuilder.charAt(end + -1) == ' ' || this.stringBuilder.charAt(end + -1) == 10) ? false : true;
                this.firstThreeLinesLayout = makeTextLayout(this.stringBuilder.subSequence(0, end), i);
                this.nextLinesLayouts = new StaticLayout[(this.textLayout.getLineCount() - 3)];
                this.nextLinesLayoutsPositions = new Point[(this.textLayout.getLineCount() - 3)];
                float x = this.firstThreeLinesLayout.getLineRight(this.firstThreeLinesLayout.getLineCount() - 1) + (this.needSpace ? this.SPACE : 0.0f);
                this.lastInlineLine = -1;
                if (this.showMoreTextBackgroundView.getMeasuredWidth() <= 0) {
                    FrameLayout frameLayout = this.showMoreTextBackgroundView;
                    int i3 = MOST_SPEC;
                    frameLayout.measure(i3, i3);
                }
                for (int line = 3; line < this.textLayout.getLineCount(); line++) {
                    int s = this.textLayout.getLineStart(line);
                    int e = this.textLayout.getLineEnd(line);
                    StaticLayout layout = makeTextLayout(this.stringBuilder.subSequence(Math.min(s, e), Math.max(s, e)), i);
                    this.nextLinesLayouts[line - 3] = layout;
                    this.nextLinesLayoutsPositions[line - 3] = new Point();
                    if (this.lastInlineLine == -1 && x > ((float) ((i - this.showMoreTextBackgroundView.getMeasuredWidth()) + this.showMoreTextBackgroundView.getPaddingLeft()))) {
                        this.lastInlineLine = line - 3;
                    }
                    x += layout.getLineRight(0) + this.SPACE;
                }
                if (x < ((float) ((i - this.showMoreTextBackgroundView.getMeasuredWidth()) + this.showMoreTextBackgroundView.getPaddingLeft()))) {
                    this.shouldExpand = false;
                }
            }
            if (this.shouldExpand == 0) {
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
        if (this.valueTextView.getVisibility() == 0) {
            return height + AndroidUtilities.dp(23.0f);
        }
        return height;
    }

    public boolean onClick() {
        if (!this.shouldExpand || this.expandT > 0.0f) {
            return false;
        }
        updateCollapse(true, true);
        return true;
    }

    private float easeInOutCubic(float x) {
        return ((double) x) < 0.5d ? 4.0f * x * x * x : 1.0f - (((float) Math.pow((double) ((-2.0f * x) + 2.0f), 3.0d)) / 2.0f);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            CharSequence text = staticLayout.getText();
            CharSequence valueText = this.valueTextView.getText();
            if (TextUtils.isEmpty(valueText)) {
                info.setText(text);
                return;
            }
            info.setText(valueText + ": " + text);
        }
    }
}
