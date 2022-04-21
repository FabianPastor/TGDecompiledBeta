package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class TextSelectionHint extends View {
    Animator a;
    int animateToEnd;
    int animateToStart;
    int currentEnd;
    int currentStart;
    Runnable dismissTunnable = new TextSelectionHint$$ExternalSyntheticLambda5(this);
    int end;
    float endOffsetValue;
    float enterValue;
    private Interpolator interpolator = new OvershootInterpolator();
    int lastW;
    int padding = AndroidUtilities.dp(24.0f);
    Path path = new Path();
    float prepareProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    Paint selectionPaint = new Paint(1);
    private boolean showOnMeasure;
    boolean showing;
    int start;
    float startOffsetValue;
    StaticLayout textLayout;
    TextPaint textPaint = new TextPaint(1);

    public TextSelectionHint(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        int textColor = getThemedColor("undo_infoColor");
        int alpha = Color.alpha(textColor);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        this.textPaint.setColor(textColor);
        this.selectionPaint.setColor(textColor);
        Paint paint = this.selectionPaint;
        double d = (double) alpha;
        Double.isNaN(d);
        paint.setAlpha((int) (d * 0.14d));
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("undo_background")));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != this.lastW || this.textLayout == null) {
            Animator animator = this.a;
            if (animator != null) {
                animator.removeAllListeners();
                this.a.cancel();
            }
            String text = LocaleController.getString("TextSelectionHit", NUM);
            Matcher matcher = Pattern.compile("\\*\\*.*\\*\\*").matcher(text);
            String word = null;
            if (matcher.matches()) {
                word = matcher.group();
            }
            String text2 = text.replace("**", "");
            this.textLayout = new StaticLayout(text2, this.textPaint, getMeasuredWidth() - (this.padding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.start = 0;
            this.end = 0;
            if (word != null) {
                this.start = text2.indexOf(word);
            }
            int i = this.start;
            if (i > 0) {
                this.end = i + word.length();
            } else {
                int k = 0;
                for (int i2 = 0; i2 < text2.length(); i2++) {
                    if (text2.charAt(i2) == ' ') {
                        k++;
                        if (k == 2) {
                            this.start = i2 + 1;
                        }
                        if (k == 3) {
                            this.end = i2 - 1;
                        }
                    }
                }
            }
            if (this.end == 0) {
                this.end = text2.length();
            }
            this.animateToStart = 0;
            StaticLayout staticLayout = this.textLayout;
            int offsetForHorizontal = staticLayout.getOffsetForHorizontal(staticLayout.getLineForOffset(this.end), (float) (this.textLayout.getWidth() - 1));
            this.animateToEnd = offsetForHorizontal;
            this.currentStart = this.start;
            this.currentEnd = this.end;
            if (this.showing) {
                this.prepareProgress = 1.0f;
                this.enterValue = 1.0f;
                this.currentStart = this.animateToStart;
                this.currentEnd = offsetForHorizontal;
                this.startOffsetValue = 0.0f;
                this.endOffsetValue = 0.0f;
            } else if (this.showOnMeasure) {
                show();
            }
            this.showOnMeasure = false;
            this.lastW = getMeasuredWidth();
        }
        int h = this.textLayout.getHeight() + (AndroidUtilities.dp(8.0f) * 2);
        if (h < AndroidUtilities.dp(56.0f)) {
            h = AndroidUtilities.dp(56.0f);
        }
        setMeasuredDimension(getMeasuredWidth(), h);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int topPadding;
        float enterProgress;
        Canvas canvas2 = canvas;
        if (this.textLayout != null) {
            super.onDraw(canvas);
            canvas.save();
            int topPadding2 = (getMeasuredHeight() - this.textLayout.getHeight()) >> 1;
            canvas2.translate((float) this.padding, (float) topPadding2);
            if (this.enterValue != 0.0f) {
                drawSelection(canvas2, this.textLayout, this.currentStart, this.currentEnd);
            }
            this.textLayout.draw(canvas2);
            int handleViewSize = AndroidUtilities.dp(14.0f);
            int line = this.textLayout.getLineForOffset(this.currentEnd);
            float primaryHorizontal = this.textLayout.getPrimaryHorizontal(this.currentEnd);
            int y = this.textLayout.getLineBottom(line);
            int i = this.currentEnd;
            int i2 = this.animateToEnd;
            if (i == i2) {
                int i3 = topPadding2;
                topPadding = y;
                roundedRect(this.path, this.textLayout.getPrimaryHorizontal(i2), (float) this.textLayout.getLineTop(line), this.textLayout.getPrimaryHorizontal(this.animateToEnd) + AndroidUtilities.dpf2(4.0f), (float) this.textLayout.getLineBottom(line), AndroidUtilities.dpf2(4.0f), AndroidUtilities.dpf2(4.0f), false, true);
                canvas2.drawPath(this.path, this.selectionPaint);
            } else {
                topPadding = y;
            }
            float enterProgress2 = this.interpolator.getInterpolation(this.enterValue);
            int xOffset = (int) (this.textLayout.getPrimaryHorizontal(this.animateToEnd) + (AndroidUtilities.dpf2(4.0f) * (1.0f - this.endOffsetValue)) + ((this.textLayout.getPrimaryHorizontal(this.end) - this.textLayout.getPrimaryHorizontal(this.animateToEnd)) * this.endOffsetValue));
            canvas.save();
            canvas2.translate((float) xOffset, (float) topPadding);
            canvas2.scale(enterProgress2, enterProgress2, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f);
            this.path.reset();
            this.path.addCircle(((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
            this.path.addRect(0.0f, 0.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
            canvas2.drawPath(this.path, this.textPaint);
            canvas.restore();
            int line2 = this.textLayout.getLineForOffset(this.currentStart);
            float x = this.textLayout.getPrimaryHorizontal(this.currentStart);
            int y2 = this.textLayout.getLineBottom(line2);
            if (this.currentStart == this.animateToStart) {
                int i4 = xOffset;
                int i5 = line2;
                enterProgress = enterProgress2;
                roundedRect(this.path, (float) (-AndroidUtilities.dp(4.0f)), (float) this.textLayout.getLineTop(line2), 0.0f, (float) this.textLayout.getLineBottom(line2), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), true, false);
                canvas2.drawPath(this.path, this.selectionPaint);
            } else {
                int i6 = line2;
                enterProgress = enterProgress2;
            }
            canvas.save();
            canvas2.translate((float) (((int) ((this.textLayout.getPrimaryHorizontal(this.animateToStart) - (((float) AndroidUtilities.dp(4.0f)) * (1.0f - this.startOffsetValue))) + ((this.textLayout.getPrimaryHorizontal(this.start) - this.textLayout.getPrimaryHorizontal(this.animateToStart)) * this.startOffsetValue))) - handleViewSize), (float) y2);
            canvas2.scale(enterProgress, enterProgress, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f);
            this.path.reset();
            this.path.addCircle(((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
            Path path2 = this.path;
            path2.addRect(((float) handleViewSize) / 2.0f, 0.0f, (float) handleViewSize, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
            canvas2.drawPath(this.path, this.textPaint);
            canvas.restore();
            canvas.restore();
        }
    }

    private void roundedRect(Path path2, float left, float top, float right, float bottom, float rx, float ry, boolean tl, boolean tr) {
        Path path3 = path2;
        float f = right;
        path2.reset();
        float rx2 = rx < 0.0f ? 0.0f : rx;
        float ry2 = ry < 0.0f ? 0.0f : ry;
        float width = f - left;
        float height = bottom - top;
        if (rx2 > width / 2.0f) {
            rx2 = width / 2.0f;
        }
        if (ry2 > height / 2.0f) {
            ry2 = height / 2.0f;
        }
        float widthMinusCorners = width - (rx2 * 2.0f);
        float heightMinusCorners = height - (2.0f * ry2);
        path2.moveTo(f, top + ry2);
        if (tr) {
            path2.rQuadTo(0.0f, -ry2, -rx2, -ry2);
        } else {
            path2.rLineTo(0.0f, -ry2);
            path2.rLineTo(-rx2, 0.0f);
        }
        path2.rLineTo(-widthMinusCorners, 0.0f);
        if (tl) {
            path2.rQuadTo(-rx2, 0.0f, -rx2, ry2);
        } else {
            path2.rLineTo(-rx2, 0.0f);
            path2.rLineTo(0.0f, ry2);
        }
        path2.rLineTo(0.0f, heightMinusCorners);
        path2.rLineTo(0.0f, ry2);
        path2.rLineTo(rx2, 0.0f);
        path2.rLineTo(widthMinusCorners, 0.0f);
        path2.rLineTo(rx2, 0.0f);
        path2.rLineTo(0.0f, -ry2);
        path2.rLineTo(0.0f, -heightMinusCorners);
        path2.close();
    }

    private void drawSelection(Canvas canvas, StaticLayout layout, int selectionStart, int selectionEnd) {
        StaticLayout staticLayout = layout;
        int i = selectionEnd;
        int startLine = layout.getLineForOffset(selectionStart);
        int endLine = staticLayout.getLineForOffset(i);
        int startX = (int) layout.getPrimaryHorizontal(selectionStart);
        int endX = (int) staticLayout.getPrimaryHorizontal(i);
        if (startLine == endLine) {
            canvas.drawRect((float) startX, (float) staticLayout.getLineTop(startLine), (float) endX, (float) staticLayout.getLineBottom(startLine), this.selectionPaint);
            return;
        }
        canvas.drawRect((float) startX, (float) staticLayout.getLineTop(startLine), staticLayout.getLineWidth(startLine), (float) staticLayout.getLineBottom(startLine), this.selectionPaint);
        canvas.drawRect(0.0f, (float) staticLayout.getLineTop(endLine), (float) endX, (float) staticLayout.getLineBottom(endLine), this.selectionPaint);
        for (int i2 = startLine + 1; i2 < endLine; i2++) {
            canvas.drawRect(0.0f, (float) staticLayout.getLineTop(i2), staticLayout.getLineWidth(i2), (float) staticLayout.getLineBottom(i2), this.selectionPaint);
        }
    }

    public void show() {
        AndroidUtilities.cancelRunOnUIThread(this.dismissTunnable);
        Animator animator = this.a;
        if (animator != null) {
            animator.removeAllListeners();
            this.a.cancel();
        }
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            this.showOnMeasure = true;
            return;
        }
        this.showing = true;
        setVisibility(0);
        this.prepareProgress = 0.0f;
        this.enterValue = 0.0f;
        this.currentStart = this.start;
        this.currentEnd = this.end;
        this.startOffsetValue = 1.0f;
        this.endOffsetValue = 1.0f;
        invalidate();
        ValueAnimator prepareAnimation = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        prepareAnimation.addUpdateListener(new TextSelectionHint$$ExternalSyntheticLambda1(this));
        prepareAnimation.setDuration(210);
        prepareAnimation.setInterpolator(new DecelerateInterpolator());
        ValueAnimator enterAnimation = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        enterAnimation.addUpdateListener(new TextSelectionHint$$ExternalSyntheticLambda2(this));
        enterAnimation.setStartDelay(600);
        enterAnimation.setDuration(250);
        ValueAnimator moveStart = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        moveStart.setStartDelay(500);
        moveStart.addUpdateListener(new TextSelectionHint$$ExternalSyntheticLambda3(this));
        moveStart.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        moveStart.setDuration(500);
        ValueAnimator moveEnd = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        moveEnd.setStartDelay(400);
        moveEnd.addUpdateListener(new TextSelectionHint$$ExternalSyntheticLambda4(this));
        moveEnd.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        moveEnd.setDuration(900);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(new Animator[]{prepareAnimation, enterAnimation, moveStart, moveEnd});
        this.a = set;
        set.start();
        AndroidUtilities.runOnUIThread(this.dismissTunnable, 5000);
    }

    /* renamed from: lambda$show$0$org-telegram-ui-Components-TextSelectionHint  reason: not valid java name */
    public /* synthetic */ void m4461lambda$show$0$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        this.prepareProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$show$1$org-telegram-ui-Components-TextSelectionHint  reason: not valid java name */
    public /* synthetic */ void m4462lambda$show$1$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        this.enterValue = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$show$2$org-telegram-ui-Components-TextSelectionHint  reason: not valid java name */
    public /* synthetic */ void m4463lambda$show$2$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
        this.startOffsetValue = floatValue;
        int i = this.animateToStart;
        this.currentStart = (int) (((float) i) + (((float) (this.start - i)) * floatValue));
        invalidate();
    }

    /* renamed from: lambda$show$3$org-telegram-ui-Components-TextSelectionHint  reason: not valid java name */
    public /* synthetic */ void m4464lambda$show$3$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
        this.endOffsetValue = floatValue;
        int i = this.animateToEnd;
        this.currentEnd = i + ((int) Math.ceil((double) (((float) (this.end - i)) * floatValue)));
        invalidate();
    }

    public void hide() {
        AndroidUtilities.cancelRunOnUIThread(this.dismissTunnable);
        hideInternal();
    }

    /* access modifiers changed from: private */
    public void hideInternal() {
        Animator animator = this.a;
        if (animator != null) {
            animator.removeAllListeners();
            this.a.cancel();
        }
        this.showing = false;
        ValueAnimator animator2 = ValueAnimator.ofFloat(new float[]{this.prepareProgress, 0.0f});
        animator2.addUpdateListener(new TextSelectionHint$$ExternalSyntheticLambda0(this));
        animator2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                TextSelectionHint.this.setVisibility(4);
            }
        });
        this.a = animator2;
        animator2.start();
    }

    /* renamed from: lambda$hideInternal$4$org-telegram-ui-Components-TextSelectionHint  reason: not valid java name */
    public /* synthetic */ void m4460x3avar_fdd(ValueAnimator animation) {
        this.prepareProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getPrepareProgress() {
        return this.prepareProgress;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
