package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class CounterView extends View {
    public CounterDrawable counterDrawable;
    private final Theme.ResourcesProvider resourcesProvider;

    public CounterView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        setVisibility(8);
        CounterDrawable counterDrawable2 = new CounterDrawable(this, true, resourcesProvider2);
        this.counterDrawable = counterDrawable2;
        counterDrawable2.updateVisibility = true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.counterDrawable.setSize(getMeasuredHeight(), getMeasuredWidth());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.counterDrawable.draw(canvas);
    }

    public void setColors(String textKey, String circleKey) {
        String unused = this.counterDrawable.textColorKey = textKey;
        String unused2 = this.counterDrawable.circleColorKey = circleKey;
    }

    public void setGravity(int gravity) {
        this.counterDrawable.gravity = gravity;
    }

    public void setReverse(boolean b) {
        boolean unused = this.counterDrawable.reverseAnimation = b;
    }

    public void setCount(int count, boolean animated) {
        this.counterDrawable.setCount(count, animated);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public static class CounterDrawable {
        private static final int ANIMATION_TYPE_IN = 0;
        private static final int ANIMATION_TYPE_OUT = 1;
        private static final int ANIMATION_TYPE_REPLACE = 2;
        public static final int TYPE_CHAT_PULLING_DOWN = 1;
        public static final int TYPE_CHAT_REACTIONS = 2;
        public static final int TYPE_DEFAULT = 0;
        public boolean addServiceGradient;
        int animationType = -1;
        private int circleColor;
        /* access modifiers changed from: private */
        public String circleColorKey = "chat_goDownButtonCounterBackground";
        public Paint circlePaint;
        /* access modifiers changed from: private */
        public StaticLayout countAnimationInLayout;
        private boolean countAnimationIncrement;
        /* access modifiers changed from: private */
        public StaticLayout countAnimationStableLayout;
        private ValueAnimator countAnimator;
        public float countChangeProgress = 1.0f;
        private StaticLayout countLayout;
        float countLeft;
        /* access modifiers changed from: private */
        public StaticLayout countOldLayout;
        private int countWidth;
        private int countWidthOld;
        int currentCount;
        private boolean drawBackground = true;
        public int gravity = 17;
        public float horizontalPadding;
        int lastH;
        /* access modifiers changed from: private */
        public View parent;
        public RectF rectF = new RectF();
        private final Theme.ResourcesProvider resourcesProvider;
        /* access modifiers changed from: private */
        public boolean reverseAnimation;
        public boolean shortFormat;
        private int textColor;
        /* access modifiers changed from: private */
        public String textColorKey = "chat_goDownButtonCounter";
        public TextPaint textPaint = new TextPaint(1);
        int type = 0;
        public boolean updateVisibility;
        int width;
        float x;

        public CounterDrawable(View parent2, boolean drawBackground2, Theme.ResourcesProvider resourcesProvider2) {
            this.parent = parent2;
            this.resourcesProvider = resourcesProvider2;
            this.drawBackground = drawBackground2;
            if (drawBackground2) {
                Paint paint = new Paint(1);
                this.circlePaint = paint;
                paint.setColor(-16777216);
            }
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        }

        public void setSize(int h, int w) {
            if (h != this.lastH) {
                int count = this.currentCount;
                this.currentCount = -1;
                setCount(count, this.animationType == 0);
                this.lastH = h;
            }
            this.width = w;
        }

        private void drawInternal(Canvas canvas) {
            float countTop = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
            updateX((float) this.countWidth);
            RectF rectF2 = this.rectF;
            float f = this.x;
            rectF2.set(f, countTop, ((float) this.countWidth) + f + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + countTop);
            if (this.circlePaint != null && this.drawBackground) {
                canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, this.circlePaint);
                if (this.addServiceGradient && Theme.hasGradientService()) {
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.chat_actionBackgroundGradientDarkenPaint);
                }
            }
            if (this.countLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(4.0f)) + countTop);
                this.countLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void setCount(int count, boolean animated) {
            boolean animated2;
            View view;
            View view2;
            int i = count;
            if (i != this.currentCount) {
                ValueAnimator valueAnimator = this.countAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (i > 0 && this.updateVisibility && (view2 = this.parent) != null) {
                    view2.setVisibility(0);
                }
                if (Math.abs(i - this.currentCount) > 99) {
                    animated2 = false;
                } else {
                    animated2 = animated;
                }
                if (!animated2) {
                    this.currentCount = i;
                    if (i != 0) {
                        String newStr = getStringOfCCount(count);
                        this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(newStr)));
                        this.countLayout = new StaticLayout(newStr, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        View view3 = this.parent;
                        if (view3 != null) {
                            view3.invalidate();
                        }
                    } else if (this.updateVisibility && (view = this.parent) != null) {
                        view.setVisibility(8);
                        return;
                    } else {
                        return;
                    }
                }
                String newStr2 = getStringOfCCount(count);
                if (animated2) {
                    ValueAnimator valueAnimator2 = this.countAnimator;
                    if (valueAnimator2 != null) {
                        valueAnimator2.cancel();
                    }
                    this.countChangeProgress = 0.0f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    this.countAnimator = ofFloat;
                    ofFloat.addUpdateListener(new CounterView$CounterDrawable$$ExternalSyntheticLambda0(this));
                    this.countAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            CounterDrawable.this.countChangeProgress = 1.0f;
                            StaticLayout unused = CounterDrawable.this.countOldLayout = null;
                            StaticLayout unused2 = CounterDrawable.this.countAnimationStableLayout = null;
                            StaticLayout unused3 = CounterDrawable.this.countAnimationInLayout = null;
                            if (CounterDrawable.this.parent != null) {
                                if (CounterDrawable.this.currentCount == 0 && CounterDrawable.this.updateVisibility) {
                                    CounterDrawable.this.parent.setVisibility(8);
                                }
                                CounterDrawable.this.parent.invalidate();
                            }
                            CounterDrawable.this.animationType = -1;
                        }
                    });
                    if (this.currentCount <= 0) {
                        this.animationType = 0;
                        this.countAnimator.setDuration(220);
                        this.countAnimator.setInterpolator(new OvershootInterpolator());
                    } else if (i == 0) {
                        this.animationType = 1;
                        this.countAnimator.setDuration(150);
                        this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    } else {
                        this.animationType = 2;
                        this.countAnimator.setDuration(430);
                        this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    }
                    if (this.countLayout != null) {
                        String oldStr = getStringOfCCount(this.currentCount);
                        if (oldStr.length() == newStr2.length()) {
                            SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                            SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr2);
                            SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr2);
                            for (int i2 = 0; i2 < oldStr.length(); i2++) {
                                if (oldStr.charAt(i2) == newStr2.charAt(i2)) {
                                    oldSpannableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                                    newSpannableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                                } else {
                                    stableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                                }
                            }
                            int countOldWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(oldStr)));
                            StaticLayout staticLayout = r9;
                            StaticLayout staticLayout2 = new StaticLayout(oldSpannableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            this.countOldLayout = staticLayout;
                            this.countAnimationStableLayout = new StaticLayout(stableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            this.countAnimationInLayout = new StaticLayout(newSpannableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        } else {
                            this.countOldLayout = this.countLayout;
                        }
                    }
                    this.countWidthOld = this.countWidth;
                    this.countAnimationIncrement = i > this.currentCount;
                    this.countAnimator.start();
                }
                if (i > 0) {
                    this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(newStr2)));
                    this.countLayout = new StaticLayout(newStr2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                }
                this.currentCount = i;
                View view4 = this.parent;
                if (view4 != null) {
                    view4.invalidate();
                }
            }
        }

        /* renamed from: lambda$setCount$0$org-telegram-ui-Components-CounterView$CounterDrawable  reason: not valid java name */
        public /* synthetic */ void m893x2var_de4(ValueAnimator valueAnimator) {
            this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            View view = this.parent;
            if (view != null) {
                view.invalidate();
            }
        }

        private String getStringOfCCount(int count) {
            if (this.shortFormat) {
                return AndroidUtilities.formatWholeNumber(count, 0);
            }
            return String.valueOf(count);
        }

        public void draw(Canvas canvas) {
            float countWidth2;
            int i = this.type;
            boolean increment = true;
            if (!(i == 1 || i == 2)) {
                int textColor2 = getThemedColor(this.textColorKey);
                int circleColor2 = getThemedColor(this.circleColorKey);
                if (this.textColor != textColor2) {
                    this.textColor = textColor2;
                    this.textPaint.setColor(textColor2);
                }
                Paint paint = this.circlePaint;
                if (!(paint == null || this.circleColor == circleColor2)) {
                    this.circleColor = circleColor2;
                    paint.setColor(circleColor2);
                }
            }
            float f = this.countChangeProgress;
            if (f != 1.0f) {
                int i2 = this.animationType;
                if (i2 == 0 || i2 == 1) {
                    updateX((float) this.countWidth);
                    float cx = this.countLeft + (((float) this.countWidth) / 2.0f);
                    float cy = ((float) this.lastH) / 2.0f;
                    canvas.save();
                    float progress = this.animationType == 0 ? this.countChangeProgress : 1.0f - this.countChangeProgress;
                    canvas.scale(progress, progress, cx, cy);
                    drawInternal(canvas);
                    canvas.restore();
                    return;
                }
                float progressHalf = f * 2.0f;
                if (progressHalf > 1.0f) {
                    progressHalf = 1.0f;
                }
                float countTop = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
                int i3 = this.countWidth;
                int i4 = this.countWidthOld;
                if (i3 == i4) {
                    countWidth2 = (float) i3;
                } else {
                    countWidth2 = (((float) i3) * progressHalf) + (((float) i4) * (1.0f - progressHalf));
                }
                updateX(countWidth2);
                float scale = 1.0f;
                if (this.countAnimationIncrement) {
                    if (this.countChangeProgress <= 0.5f) {
                        scale = 1.0f + (CubicBezierInterpolator.EASE_OUT.getInterpolation(this.countChangeProgress * 2.0f) * 0.1f);
                    } else {
                        scale = 1.0f + (CubicBezierInterpolator.EASE_IN.getInterpolation(1.0f - ((this.countChangeProgress - 0.5f) * 2.0f)) * 0.1f);
                    }
                }
                RectF rectF2 = this.rectF;
                float f2 = this.x;
                rectF2.set(f2, countTop, f2 + countWidth2 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + countTop);
                canvas.save();
                canvas.scale(scale, scale, this.rectF.centerX(), this.rectF.centerY());
                if (this.drawBackground && this.circlePaint != null) {
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, this.circlePaint);
                    if (this.addServiceGradient && Theme.hasGradientService()) {
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.chat_actionBackgroundGradientDarkenPaint);
                    }
                }
                canvas.clipRect(this.rectF);
                if (this.reverseAnimation == this.countAnimationIncrement) {
                    increment = false;
                }
                if (this.countAnimationInLayout != null) {
                    canvas.save();
                    float f3 = this.countLeft;
                    float dp = ((float) AndroidUtilities.dp(4.0f)) + countTop;
                    int dp2 = AndroidUtilities.dp(13.0f);
                    if (!increment) {
                        dp2 = -dp2;
                    }
                    canvas.translate(f3, dp + (((float) dp2) * (1.0f - progressHalf)));
                    this.textPaint.setAlpha((int) (progressHalf * 255.0f));
                    this.countAnimationInLayout.draw(canvas);
                    canvas.restore();
                } else if (this.countLayout != null) {
                    canvas.save();
                    float f4 = this.countLeft;
                    float dp3 = ((float) AndroidUtilities.dp(4.0f)) + countTop;
                    int dp4 = AndroidUtilities.dp(13.0f);
                    if (!increment) {
                        dp4 = -dp4;
                    }
                    canvas.translate(f4, dp3 + (((float) dp4) * (1.0f - progressHalf)));
                    this.textPaint.setAlpha((int) (progressHalf * 255.0f));
                    this.countLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.countOldLayout != null) {
                    canvas.save();
                    float f5 = this.countLeft;
                    float dp5 = ((float) AndroidUtilities.dp(4.0f)) + countTop;
                    int dp6 = AndroidUtilities.dp(13.0f);
                    if (increment) {
                        dp6 = -dp6;
                    }
                    canvas.translate(f5, dp5 + (((float) dp6) * progressHalf));
                    this.textPaint.setAlpha((int) ((1.0f - progressHalf) * 255.0f));
                    this.countOldLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.countAnimationStableLayout != null) {
                    canvas.save();
                    canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(4.0f)) + countTop);
                    this.textPaint.setAlpha(255);
                    this.countAnimationStableLayout.draw(canvas);
                    canvas.restore();
                }
                this.textPaint.setAlpha(255);
                canvas.restore();
                return;
            }
            drawInternal(canvas);
        }

        public void updateBackgroundRect() {
            float countWidth2;
            float f = this.countChangeProgress;
            if (f != 1.0f) {
                int i = this.animationType;
                if (i == 0 || i == 1) {
                    updateX((float) this.countWidth);
                    float countTop = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
                    RectF rectF2 = this.rectF;
                    float f2 = this.x;
                    rectF2.set(f2, countTop, ((float) this.countWidth) + f2 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + countTop);
                    return;
                }
                float progressHalf = f * 2.0f;
                if (progressHalf > 1.0f) {
                    progressHalf = 1.0f;
                }
                float countTop2 = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
                int i2 = this.countWidth;
                int i3 = this.countWidthOld;
                if (i2 == i3) {
                    countWidth2 = (float) i2;
                } else {
                    countWidth2 = (((float) i2) * progressHalf) + (((float) i3) * (1.0f - progressHalf));
                }
                updateX(countWidth2);
                RectF rectF3 = this.rectF;
                float f3 = this.x;
                rectF3.set(f3, countTop2, f3 + countWidth2 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + countTop2);
                return;
            }
            updateX((float) this.countWidth);
            float countTop3 = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
            RectF rectF4 = this.rectF;
            float f4 = this.x;
            rectF4.set(f4, countTop3, ((float) this.countWidth) + f4 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + countTop3);
        }

        private void updateX(float countWidth2) {
            float padding = this.drawBackground ? (float) AndroidUtilities.dp(5.5f) : 0.0f;
            int i = this.gravity;
            if (i == 5) {
                float f = ((float) this.width) - padding;
                this.countLeft = f;
                float f2 = this.horizontalPadding;
                if (f2 != 0.0f) {
                    this.countLeft = f - Math.max(f2 + (countWidth2 / 2.0f), countWidth2);
                } else {
                    this.countLeft = f - countWidth2;
                }
            } else if (i == 3) {
                this.countLeft = padding;
            } else {
                this.countLeft = (float) ((int) ((((float) this.width) - countWidth2) / 2.0f));
            }
            this.x = this.countLeft - padding;
        }

        public float getCenterX() {
            updateX((float) this.countWidth);
            return this.countLeft + (((float) this.countWidth) / 2.0f);
        }

        public void setType(int type2) {
            this.type = type2;
        }

        public void setParent(View parent2) {
            this.parent = parent2;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public float getEnterProgress() {
        if (this.counterDrawable.countChangeProgress == 1.0f || !(this.counterDrawable.animationType == 0 || this.counterDrawable.animationType == 1)) {
            if (this.counterDrawable.currentCount == 0) {
                return 0.0f;
            }
            return 1.0f;
        } else if (this.counterDrawable.animationType == 0) {
            return this.counterDrawable.countChangeProgress;
        } else {
            return 1.0f - this.counterDrawable.countChangeProgress;
        }
    }

    public boolean isInOutAnimation() {
        return this.counterDrawable.animationType == 0 || this.counterDrawable.animationType == 1;
    }
}
