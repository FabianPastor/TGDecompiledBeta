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
import org.telegram.ui.Components.CounterView;

public class CounterView extends View {
    public CounterDrawable counterDrawable = new CounterDrawable(this);

    public CounterView(Context context) {
        super(context);
        setVisibility(8);
        this.counterDrawable.updateVisibility = true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.counterDrawable.setSize(getMeasuredHeight(), getMeasuredWidth());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.counterDrawable.draw(canvas);
    }

    public void setColors(String str, String str2) {
        String unused = this.counterDrawable.textColorKey = str;
        String unused2 = this.counterDrawable.circleColorKey = str2;
    }

    public void setGravity(int i) {
        this.counterDrawable.gravity = i;
    }

    public void setReverse(boolean z) {
        boolean unused = this.counterDrawable.reverseAnimation = z;
    }

    public void setCount(int i, boolean z) {
        this.counterDrawable.setCount(i, z);
    }

    public static class CounterDrawable {
        int animationType = -1;
        private int circleColor;
        /* access modifiers changed from: private */
        public String circleColorKey = "chat_goDownButtonCounterBackground";
        public Paint circlePaint = new Paint(1);
        /* access modifiers changed from: private */
        public StaticLayout countAnimationInLayout;
        private boolean countAnimationIncrement;
        /* access modifiers changed from: private */
        public StaticLayout countAnimationStableLayout;
        private ValueAnimator countAnimator;
        /* access modifiers changed from: private */
        public float countChangeProgress = 1.0f;
        private StaticLayout countLayout;
        float countLeft;
        /* access modifiers changed from: private */
        public StaticLayout countOldLayout;
        private int countWidth;
        private int countWidthOld;
        int currentCount;
        public int gravity = 17;
        public float horizontalPadding;
        int lastH;
        /* access modifiers changed from: private */
        public View parent;
        public RectF rectF = new RectF();
        /* access modifiers changed from: private */
        public boolean reverseAnimation;
        private int textColor;
        /* access modifiers changed from: private */
        public String textColorKey = "chat_goDownButtonCounter";
        public TextPaint textPaint = new TextPaint(1);
        int type = 0;
        boolean updateVisibility;
        int width;
        float x;

        public CounterDrawable(View view) {
            this.parent = view;
            this.circlePaint.setColor(-16777216);
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        }

        public void setSize(int i, int i2) {
            if (i != this.lastH) {
                int i3 = this.currentCount;
                this.currentCount = -1;
                setCount(i3, this.animationType == 0);
                this.lastH = i;
            }
            this.width = i2;
        }

        private void drawInternal(Canvas canvas) {
            float dp = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
            updateX((float) this.countWidth);
            RectF rectF2 = this.rectF;
            float f = this.x;
            rectF2.set(f, dp, ((float) this.countWidth) + f + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + dp);
            RectF rectF3 = this.rectF;
            float f2 = AndroidUtilities.density;
            canvas.drawRoundRect(rectF3, f2 * 11.5f, f2 * 11.5f, this.circlePaint);
            if (Theme.hasGradientService()) {
                RectF rectF4 = this.rectF;
                float f3 = AndroidUtilities.density;
                canvas.drawRoundRect(rectF4, f3 * 11.5f, f3 * 11.5f, Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (this.countLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft, dp + ((float) AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void setCount(int i, boolean z) {
            View view;
            View view2;
            int i2 = i;
            if (i2 != this.currentCount) {
                ValueAnimator valueAnimator = this.countAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (i2 > 0 && this.updateVisibility && (view2 = this.parent) != null) {
                    view2.setVisibility(0);
                }
                boolean z2 = Math.abs(i2 - this.currentCount) > 99 ? false : z;
                if (!z2) {
                    this.currentCount = i2;
                    if (i2 != 0) {
                        String valueOf = String.valueOf(i);
                        this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(valueOf)));
                        this.countLayout = new StaticLayout(valueOf, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
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
                String valueOf2 = String.valueOf(i);
                if (z2) {
                    ValueAnimator valueAnimator2 = this.countAnimator;
                    if (valueAnimator2 != null) {
                        valueAnimator2.cancel();
                    }
                    this.countChangeProgress = 0.0f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    this.countAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            CounterView.CounterDrawable.this.lambda$setCount$0$CounterView$CounterDrawable(valueAnimator);
                        }
                    });
                    this.countAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            CounterDrawable counterDrawable = CounterDrawable.this;
                            counterDrawable.animationType = -1;
                            float unused = counterDrawable.countChangeProgress = 1.0f;
                            StaticLayout unused2 = CounterDrawable.this.countOldLayout = null;
                            StaticLayout unused3 = CounterDrawable.this.countAnimationStableLayout = null;
                            StaticLayout unused4 = CounterDrawable.this.countAnimationInLayout = null;
                            if (CounterDrawable.this.parent != null) {
                                CounterDrawable counterDrawable2 = CounterDrawable.this;
                                if (counterDrawable2.currentCount == 0 && counterDrawable2.updateVisibility) {
                                    counterDrawable2.parent.setVisibility(8);
                                }
                                CounterDrawable.this.parent.invalidate();
                            }
                        }
                    });
                    if (this.currentCount <= 0) {
                        this.animationType = 0;
                        this.countAnimator.setDuration(220);
                        this.countAnimator.setInterpolator(new OvershootInterpolator());
                    } else if (i2 == 0) {
                        this.animationType = 1;
                        this.countAnimator.setDuration(150);
                        this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    } else {
                        this.animationType = 2;
                        this.countAnimator.setDuration(430);
                        this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    }
                    if (this.countLayout != null) {
                        String valueOf3 = String.valueOf(this.currentCount);
                        if (valueOf3.length() == valueOf2.length()) {
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(valueOf3);
                            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(valueOf2);
                            SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(valueOf2);
                            for (int i3 = 0; i3 < valueOf3.length(); i3++) {
                                if (valueOf3.charAt(i3) == valueOf2.charAt(i3)) {
                                    int i4 = i3 + 1;
                                    spannableStringBuilder.setSpan(new EmptyStubSpan(), i3, i4, 0);
                                    spannableStringBuilder2.setSpan(new EmptyStubSpan(), i3, i4, 0);
                                } else {
                                    spannableStringBuilder3.setSpan(new EmptyStubSpan(), i3, i3 + 1, 0);
                                }
                            }
                            int max = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(valueOf3)));
                            StaticLayout staticLayout = r9;
                            StaticLayout staticLayout2 = new StaticLayout(spannableStringBuilder, this.textPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            this.countOldLayout = staticLayout;
                            int i5 = max;
                            this.countAnimationStableLayout = new StaticLayout(spannableStringBuilder3, this.textPaint, i5, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            this.countAnimationInLayout = new StaticLayout(spannableStringBuilder2, this.textPaint, i5, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        } else {
                            this.countOldLayout = this.countLayout;
                        }
                    }
                    this.countWidthOld = this.countWidth;
                    this.countAnimationIncrement = i2 > this.currentCount;
                    this.countAnimator.start();
                }
                if (i2 > 0) {
                    this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(valueOf2)));
                    this.countLayout = new StaticLayout(valueOf2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                }
                this.currentCount = i2;
                View view4 = this.parent;
                if (view4 != null) {
                    view4.invalidate();
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$setCount$0 */
        public /* synthetic */ void lambda$setCount$0$CounterView$CounterDrawable(ValueAnimator valueAnimator) {
            this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            View view = this.parent;
            if (view != null) {
                view.invalidate();
            }
        }

        public void draw(Canvas canvas) {
            float f;
            float f2;
            boolean z = true;
            if (this.type != 1) {
                int color = Theme.getColor(this.textColorKey);
                int color2 = Theme.getColor(this.circleColorKey);
                if (this.textColor != color) {
                    this.textColor = color;
                    this.textPaint.setColor(color);
                }
                if (this.circleColor != color2) {
                    this.circleColor = color2;
                    this.circlePaint.setColor(color2);
                }
            }
            float f3 = this.countChangeProgress;
            if (f3 != 1.0f) {
                int i = this.animationType;
                if (i == 0 || i == 1) {
                    updateX((float) this.countWidth);
                    float f4 = this.countLeft + (((float) this.countWidth) / 2.0f);
                    float f5 = ((float) this.lastH) / 2.0f;
                    canvas.save();
                    float f6 = this.animationType == 0 ? this.countChangeProgress : 1.0f - this.countChangeProgress;
                    canvas.scale(f6, f6, f4, f5);
                    drawInternal(canvas);
                    canvas.restore();
                    return;
                }
                float f7 = f3 * 2.0f;
                if (f7 > 1.0f) {
                    f7 = 1.0f;
                }
                float dp = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
                int i2 = this.countWidth;
                int i3 = this.countWidthOld;
                float f8 = i2 == i3 ? (float) i2 : (((float) i2) * f7) + (((float) i3) * (1.0f - f7));
                updateX(f8);
                if (this.countAnimationIncrement) {
                    float f9 = this.countChangeProgress;
                    if (f9 <= 0.5f) {
                        f2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(f9 * 2.0f);
                    } else {
                        f2 = CubicBezierInterpolator.EASE_IN.getInterpolation(1.0f - ((f9 - 0.5f) * 2.0f));
                    }
                    f = (f2 * 0.1f) + 1.0f;
                } else {
                    f = 1.0f;
                }
                RectF rectF2 = this.rectF;
                float var_ = this.x;
                rectF2.set(var_, dp, f8 + var_ + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + dp);
                canvas.save();
                canvas.scale(f, f, this.rectF.centerX(), this.rectF.centerY());
                RectF rectF3 = this.rectF;
                float var_ = AndroidUtilities.density;
                canvas.drawRoundRect(rectF3, var_ * 11.5f, var_ * 11.5f, this.circlePaint);
                if (Theme.hasGradientService()) {
                    RectF rectF4 = this.rectF;
                    float var_ = AndroidUtilities.density;
                    canvas.drawRoundRect(rectF4, var_ * 11.5f, var_ * 11.5f, Theme.chat_actionBackgroundGradientDarkenPaint);
                }
                canvas.clipRect(this.rectF);
                if (this.reverseAnimation == this.countAnimationIncrement) {
                    z = false;
                }
                if (this.countAnimationInLayout != null) {
                    canvas.save();
                    float var_ = this.countLeft;
                    float dp2 = ((float) AndroidUtilities.dp(4.0f)) + dp;
                    int dp3 = AndroidUtilities.dp(13.0f);
                    if (!z) {
                        dp3 = -dp3;
                    }
                    canvas.translate(var_, dp2 + (((float) dp3) * (1.0f - f7)));
                    this.textPaint.setAlpha((int) (f7 * 255.0f));
                    this.countAnimationInLayout.draw(canvas);
                    canvas.restore();
                } else if (this.countLayout != null) {
                    canvas.save();
                    float var_ = this.countLeft;
                    float dp4 = ((float) AndroidUtilities.dp(4.0f)) + dp;
                    int dp5 = AndroidUtilities.dp(13.0f);
                    if (!z) {
                        dp5 = -dp5;
                    }
                    canvas.translate(var_, dp4 + (((float) dp5) * (1.0f - f7)));
                    this.textPaint.setAlpha((int) (f7 * 255.0f));
                    this.countLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.countOldLayout != null) {
                    canvas.save();
                    canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(4.0f)) + dp + (((float) (z ? -AndroidUtilities.dp(13.0f) : AndroidUtilities.dp(13.0f))) * f7));
                    this.textPaint.setAlpha((int) ((1.0f - f7) * 255.0f));
                    this.countOldLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.countAnimationStableLayout != null) {
                    canvas.save();
                    canvas.translate(this.countLeft, dp + ((float) AndroidUtilities.dp(4.0f)));
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
            float f = this.countChangeProgress;
            if (f != 1.0f) {
                int i = this.animationType;
                if (i == 0 || i == 1) {
                    updateX((float) this.countWidth);
                    float dp = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
                    RectF rectF2 = this.rectF;
                    float f2 = this.x;
                    rectF2.set(f2, dp, ((float) this.countWidth) + f2 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + dp);
                    return;
                }
                float f3 = f * 2.0f;
                if (f3 > 1.0f) {
                    f3 = 1.0f;
                }
                float dp2 = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
                int i2 = this.countWidth;
                int i3 = this.countWidthOld;
                float f4 = i2 == i3 ? (float) i2 : (((float) i2) * f3) + (((float) i3) * (1.0f - f3));
                updateX(f4);
                RectF rectF3 = this.rectF;
                float f5 = this.x;
                rectF3.set(f5, dp2, f4 + f5 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + dp2);
                return;
            }
            updateX((float) this.countWidth);
            float dp3 = ((float) (this.lastH - AndroidUtilities.dp(23.0f))) / 2.0f;
            RectF rectF4 = this.rectF;
            float f6 = this.x;
            rectF4.set(f6, dp3, ((float) this.countWidth) + f6 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + dp3);
        }

        private void updateX(float f) {
            int i = this.gravity;
            if (i == 5) {
                float dp = (float) (this.width - AndroidUtilities.dp(5.5f));
                this.countLeft = dp;
                float f2 = this.horizontalPadding;
                if (f2 != 0.0f) {
                    this.countLeft = dp - Math.max(f2 + (f / 2.0f), f);
                } else {
                    this.countLeft = dp - f;
                }
            } else if (i == 3) {
                this.countLeft = (float) AndroidUtilities.dp(5.5f);
            } else {
                this.countLeft = (float) ((int) ((((float) this.width) - f) / 2.0f));
            }
            this.x = this.countLeft - ((float) AndroidUtilities.dp(5.5f));
        }

        public float getCenterX() {
            updateX((float) this.countWidth);
            return this.countLeft + (((float) this.countWidth) / 2.0f);
        }

        public void setType(int i) {
            this.type = i;
        }

        public void setParent(View view) {
            this.parent = view;
        }
    }
}
