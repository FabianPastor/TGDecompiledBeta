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
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class TextSelectionHint extends View {
    Animator a;
    int animateToEnd;
    int animateToStart;
    int currentEnd;
    int currentStart;
    Runnable dismissTunnable;
    int end;
    float endOffsetValue;
    float enterValue;
    private Interpolator interpolator;
    int lastW;
    int padding;
    Path path;
    float prepareProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    Paint selectionPaint;
    private boolean showOnMeasure;
    boolean showing;
    int start;
    float startOffsetValue;
    StaticLayout textLayout;
    TextPaint textPaint;

    public TextSelectionHint(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.textPaint = new TextPaint(1);
        this.selectionPaint = new Paint(1);
        this.padding = AndroidUtilities.dp(24.0f);
        this.interpolator = new OvershootInterpolator();
        this.dismissTunnable = new Runnable() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TextSelectionHint.this.hideInternal();
            }
        };
        this.path = new Path();
        this.resourcesProvider = resourcesProvider;
        int themedColor = getThemedColor("undo_infoColor");
        int alpha = Color.alpha(themedColor);
        this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
        this.textPaint.setColor(themedColor);
        this.selectionPaint.setColor(themedColor);
        Paint paint = this.selectionPaint;
        double d = alpha;
        Double.isNaN(d);
        paint.setAlpha((int) (d * 0.14d));
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("undo_background")));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredWidth() != this.lastW || this.textLayout == null) {
            Animator animator = this.a;
            if (animator != null) {
                animator.removeAllListeners();
                this.a.cancel();
            }
            String string = LocaleController.getString("TextSelectionHit", R.string.TextSelectionHit);
            Matcher matcher = Pattern.compile("\\*\\*.*\\*\\*").matcher(string);
            String str = null;
            if (matcher.matches()) {
                str = matcher.group();
            }
            String replace = string.replace("**", "");
            this.textLayout = new StaticLayout(replace, this.textPaint, getMeasuredWidth() - (this.padding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.start = 0;
            this.end = 0;
            if (str != null) {
                this.start = replace.indexOf(str);
            }
            int i3 = this.start;
            if (i3 > 0) {
                this.end = i3 + str.length();
            } else {
                int i4 = 0;
                for (int i5 = 0; i5 < replace.length(); i5++) {
                    if (replace.charAt(i5) == ' ') {
                        i4++;
                        if (i4 == 2) {
                            this.start = i5 + 1;
                        }
                        if (i4 == 3) {
                            this.end = i5 - 1;
                        }
                    }
                }
            }
            if (this.end == 0) {
                this.end = replace.length();
            }
            this.animateToStart = 0;
            StaticLayout staticLayout = this.textLayout;
            int offsetForHorizontal = staticLayout.getOffsetForHorizontal(staticLayout.getLineForOffset(this.end), this.textLayout.getWidth() - 1);
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
        int height = this.textLayout.getHeight() + (AndroidUtilities.dp(8.0f) * 2);
        if (height < AndroidUtilities.dp(56.0f)) {
            height = AndroidUtilities.dp(56.0f);
        }
        setMeasuredDimension(getMeasuredWidth(), height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int i;
        float f;
        if (this.textLayout == null) {
            return;
        }
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(this.padding, (getMeasuredHeight() - this.textLayout.getHeight()) >> 1);
        if (this.enterValue != 0.0f) {
            drawSelection(canvas, this.textLayout, this.currentStart, this.currentEnd);
        }
        this.textLayout.draw(canvas);
        int dp = AndroidUtilities.dp(14.0f);
        int lineForOffset = this.textLayout.getLineForOffset(this.currentEnd);
        this.textLayout.getPrimaryHorizontal(this.currentEnd);
        int lineBottom = this.textLayout.getLineBottom(lineForOffset);
        int i2 = this.currentEnd;
        int i3 = this.animateToEnd;
        if (i2 == i3) {
            roundedRect(this.path, this.textLayout.getPrimaryHorizontal(i3), this.textLayout.getLineTop(lineForOffset), this.textLayout.getPrimaryHorizontal(this.animateToEnd) + AndroidUtilities.dpf2(4.0f), this.textLayout.getLineBottom(lineForOffset), AndroidUtilities.dpf2(4.0f), AndroidUtilities.dpf2(4.0f), false, true);
            canvas.drawPath(this.path, this.selectionPaint);
        }
        float interpolation = this.interpolator.getInterpolation(this.enterValue);
        canvas.save();
        canvas.translate((int) (this.textLayout.getPrimaryHorizontal(this.animateToEnd) + (AndroidUtilities.dpf2(4.0f) * (1.0f - this.endOffsetValue)) + ((this.textLayout.getPrimaryHorizontal(this.end) - this.textLayout.getPrimaryHorizontal(this.animateToEnd)) * this.endOffsetValue)), lineBottom);
        float f2 = dp;
        float f3 = f2 / 2.0f;
        canvas.scale(interpolation, interpolation, f3, f3);
        this.path.reset();
        this.path.addCircle(f3, f3, f3, Path.Direction.CCW);
        this.path.addRect(0.0f, 0.0f, f3, f3, Path.Direction.CCW);
        canvas.drawPath(this.path, this.textPaint);
        canvas.restore();
        int lineForOffset2 = this.textLayout.getLineForOffset(this.currentStart);
        this.textLayout.getPrimaryHorizontal(this.currentStart);
        int lineBottom2 = this.textLayout.getLineBottom(lineForOffset2);
        if (this.currentStart == this.animateToStart) {
            i = lineBottom2;
            f = f3;
            roundedRect(this.path, -AndroidUtilities.dp(4.0f), this.textLayout.getLineTop(lineForOffset2), 0.0f, this.textLayout.getLineBottom(lineForOffset2), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), true, false);
            canvas.drawPath(this.path, this.selectionPaint);
        } else {
            i = lineBottom2;
            f = f3;
        }
        canvas.save();
        canvas.translate(((int) ((this.textLayout.getPrimaryHorizontal(this.animateToStart) - (AndroidUtilities.dp(4.0f) * (1.0f - this.startOffsetValue))) + ((this.textLayout.getPrimaryHorizontal(this.start) - this.textLayout.getPrimaryHorizontal(this.animateToStart)) * this.startOffsetValue))) - dp, i);
        float f4 = f;
        canvas.scale(interpolation, interpolation, f4, f4);
        this.path.reset();
        this.path.addCircle(f4, f4, f4, Path.Direction.CCW);
        this.path.addRect(f4, 0.0f, f2, f4, Path.Direction.CCW);
        canvas.drawPath(this.path, this.textPaint);
        canvas.restore();
        canvas.restore();
    }

    private void roundedRect(Path path, float f, float f2, float f3, float f4, float f5, float f6, boolean z, boolean z2) {
        path.reset();
        if (f5 < 0.0f) {
            f5 = 0.0f;
        }
        if (f6 < 0.0f) {
            f6 = 0.0f;
        }
        float f7 = f3 - f;
        float f8 = f4 - f2;
        float f9 = f7 / 2.0f;
        if (f5 > f9) {
            f5 = f9;
        }
        float var_ = f8 / 2.0f;
        if (f6 > var_) {
            f6 = var_;
        }
        float var_ = f7 - (f5 * 2.0f);
        float var_ = f8 - (2.0f * f6);
        path.moveTo(f3, f2 + f6);
        if (z2) {
            float var_ = -f6;
            path.rQuadTo(0.0f, var_, -f5, var_);
        } else {
            path.rLineTo(0.0f, -f6);
            path.rLineTo(-f5, 0.0f);
        }
        path.rLineTo(-var_, 0.0f);
        if (z) {
            float var_ = -f5;
            path.rQuadTo(var_, 0.0f, var_, f6);
        } else {
            path.rLineTo(-f5, 0.0f);
            path.rLineTo(0.0f, f6);
        }
        path.rLineTo(0.0f, var_);
        path.rLineTo(0.0f, f6);
        path.rLineTo(f5, 0.0f);
        path.rLineTo(var_, 0.0f);
        path.rLineTo(f5, 0.0f);
        path.rLineTo(0.0f, -f6);
        path.rLineTo(0.0f, -var_);
        path.close();
    }

    private void drawSelection(Canvas canvas, StaticLayout staticLayout, int i, int i2) {
        int lineForOffset = staticLayout.getLineForOffset(i);
        int lineForOffset2 = staticLayout.getLineForOffset(i2);
        int primaryHorizontal = (int) staticLayout.getPrimaryHorizontal(i);
        int primaryHorizontal2 = (int) staticLayout.getPrimaryHorizontal(i2);
        if (lineForOffset == lineForOffset2) {
            canvas.drawRect(primaryHorizontal, staticLayout.getLineTop(lineForOffset), primaryHorizontal2, staticLayout.getLineBottom(lineForOffset), this.selectionPaint);
            return;
        }
        canvas.drawRect(primaryHorizontal, staticLayout.getLineTop(lineForOffset), staticLayout.getLineWidth(lineForOffset), staticLayout.getLineBottom(lineForOffset), this.selectionPaint);
        canvas.drawRect(0.0f, staticLayout.getLineTop(lineForOffset2), primaryHorizontal2, staticLayout.getLineBottom(lineForOffset2), this.selectionPaint);
        while (true) {
            lineForOffset++;
            if (lineForOffset >= lineForOffset2) {
                return;
            }
            canvas.drawRect(0.0f, staticLayout.getLineTop(lineForOffset), staticLayout.getLineWidth(lineForOffset), staticLayout.getLineBottom(lineForOffset), this.selectionPaint);
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
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.lambda$show$0(valueAnimator);
            }
        });
        ofFloat.setDuration(210L);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.lambda$show$1(valueAnimator);
            }
        });
        ofFloat2.setStartDelay(600L);
        ofFloat2.setDuration(250L);
        ValueAnimator ofFloat3 = ValueAnimator.ofFloat(1.0f, 0.0f);
        ofFloat3.setStartDelay(500L);
        ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.lambda$show$2(valueAnimator);
            }
        });
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
        ofFloat3.setInterpolator(cubicBezierInterpolator);
        ofFloat3.setDuration(500L);
        ValueAnimator ofFloat4 = ValueAnimator.ofFloat(1.0f, 0.0f);
        ofFloat4.setStartDelay(400L);
        ofFloat4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.lambda$show$3(valueAnimator);
            }
        });
        ofFloat4.setInterpolator(cubicBezierInterpolator);
        ofFloat4.setDuration(900L);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(ofFloat, ofFloat2, ofFloat3, ofFloat4);
        this.a = animatorSet;
        animatorSet.start();
        AndroidUtilities.runOnUIThread(this.dismissTunnable, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$0(ValueAnimator valueAnimator) {
        this.prepareProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$1(ValueAnimator valueAnimator) {
        this.enterValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$2(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.startOffsetValue = floatValue;
        int i = this.animateToStart;
        this.currentStart = (int) (i + ((this.start - i) * floatValue));
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$3(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.endOffsetValue = floatValue;
        int i = this.animateToEnd;
        this.currentEnd = i + ((int) Math.ceil((this.end - i) * floatValue));
        invalidate();
    }

    public void hide() {
        AndroidUtilities.cancelRunOnUIThread(this.dismissTunnable);
        hideInternal();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideInternal() {
        Animator animator = this.a;
        if (animator != null) {
            animator.removeAllListeners();
            this.a.cancel();
        }
        this.showing = false;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.prepareProgress, 0.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.lambda$hideInternal$4(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TextSelectionHint.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                TextSelectionHint.this.setVisibility(4);
            }
        });
        this.a = ofFloat;
        ofFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hideInternal$4(ValueAnimator valueAnimator) {
        this.prepareProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getPrepareProgress() {
        return this.prepareProgress;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
