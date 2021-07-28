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
    int animationType = -1;
    private int circleColor;
    private String circleColorKey = "chat_goDownButtonCounterBackground";
    Paint circlePaint = new Paint(1);
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
    int gravity = 17;
    public float horizontalPadding;
    int lastH;
    RectF rectF = new RectF();
    private boolean reverseAnimation;
    private int textColor;
    private String textColorKey = "chat_goDownButtonCounter";
    TextPaint textPaint = new TextPaint(1);
    float x;

    public CounterView(Context context) {
        super(context);
        setVisibility(8);
        this.circlePaint.setColor(-16777216);
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredHeight() != this.lastH) {
            int i3 = this.currentCount;
            this.currentCount = -1;
            setCount(i3, this.animationType == 0);
            this.lastH = getMeasuredHeight();
        }
    }

    public void setCount(int i, boolean z) {
        int i2 = i;
        if (i2 != this.currentCount) {
            ValueAnimator valueAnimator = this.countAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z2 = false;
            if (i2 > 0) {
                setVisibility(0);
            }
            boolean z3 = Math.abs(i2 - this.currentCount) > 99 ? false : z;
            if (!z3) {
                this.currentCount = i2;
                if (i2 == 0) {
                    setVisibility(8);
                    return;
                }
                String valueOf = String.valueOf(i);
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(valueOf)));
                this.countLayout = new StaticLayout(valueOf, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                invalidate();
            }
            String valueOf2 = String.valueOf(i);
            if (z3) {
                ValueAnimator valueAnimator2 = this.countAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.countChangeProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.countAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        CounterView.this.lambda$setCount$0$CounterView(valueAnimator);
                    }
                });
                this.countAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        CounterView counterView = CounterView.this;
                        counterView.animationType = -1;
                        float unused = counterView.countChangeProgress = 1.0f;
                        StaticLayout unused2 = CounterView.this.countOldLayout = null;
                        StaticLayout unused3 = CounterView.this.countAnimationStableLayout = null;
                        StaticLayout unused4 = CounterView.this.countAnimationInLayout = null;
                        CounterView counterView2 = CounterView.this;
                        if (counterView2.currentCount == 0) {
                            counterView2.setVisibility(8);
                        }
                        CounterView.this.invalidate();
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
                        int i5 = max;
                        this.countOldLayout = new StaticLayout(spannableStringBuilder, this.textPaint, i5, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationStableLayout = new StaticLayout(spannableStringBuilder3, this.textPaint, i5, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationInLayout = new StaticLayout(spannableStringBuilder2, this.textPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.countOldLayout = this.countLayout;
                    }
                }
                this.countWidthOld = this.countWidth;
                if (i2 > this.currentCount) {
                    z2 = true;
                }
                this.countAnimationIncrement = z2;
                this.countAnimator.start();
            }
            if (i2 > 0) {
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(valueOf2)));
                this.countLayout = new StaticLayout(valueOf2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            }
            this.currentCount = i2;
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setCount$0 */
    public /* synthetic */ void lambda$setCount$0$CounterView(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        super.onDraw(canvas);
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
        float f3 = this.countChangeProgress;
        if (f3 != 1.0f) {
            int i = this.animationType;
            if (i != 0) {
                boolean z = true;
                if (i != 1) {
                    float f4 = f3 * 2.0f;
                    if (f4 > 1.0f) {
                        f4 = 1.0f;
                    }
                    float measuredHeight = ((float) (getMeasuredHeight() - AndroidUtilities.dp(23.0f))) / 2.0f;
                    int i2 = this.countWidth;
                    int i3 = this.countWidthOld;
                    float f5 = i2 == i3 ? (float) i2 : (((float) i2) * f4) + (((float) i3) * (1.0f - f4));
                    updateX(f5);
                    if (this.countAnimationIncrement) {
                        float f6 = this.countChangeProgress;
                        if (f6 <= 0.5f) {
                            f2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(f6 * 2.0f);
                        } else {
                            f2 = CubicBezierInterpolator.EASE_IN.getInterpolation(1.0f - ((f6 - 0.5f) * 2.0f));
                        }
                        f = (f2 * 0.1f) + 1.0f;
                    } else {
                        f = 1.0f;
                    }
                    RectF rectF2 = this.rectF;
                    float f7 = this.x;
                    rectF2.set(f7, measuredHeight, f5 + f7 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + measuredHeight);
                    canvas.save();
                    canvas.scale(f, f, this.rectF.centerX(), this.rectF.centerY());
                    RectF rectF3 = this.rectF;
                    float f8 = AndroidUtilities.density;
                    canvas.drawRoundRect(rectF3, f8 * 11.5f, f8 * 11.5f, this.circlePaint);
                    canvas.clipRect(this.rectF);
                    if (this.reverseAnimation == this.countAnimationIncrement) {
                        z = false;
                    }
                    if (this.countAnimationInLayout != null) {
                        canvas.save();
                        float f9 = this.countLeft;
                        float dp = ((float) AndroidUtilities.dp(4.0f)) + measuredHeight;
                        int dp2 = AndroidUtilities.dp(13.0f);
                        if (!z) {
                            dp2 = -dp2;
                        }
                        canvas.translate(f9, dp + (((float) dp2) * (1.0f - f4)));
                        this.textPaint.setAlpha((int) (f4 * 255.0f));
                        this.countAnimationInLayout.draw(canvas);
                        canvas.restore();
                    } else if (this.countLayout != null) {
                        canvas.save();
                        float var_ = this.countLeft;
                        float dp3 = ((float) AndroidUtilities.dp(4.0f)) + measuredHeight;
                        int dp4 = AndroidUtilities.dp(13.0f);
                        if (!z) {
                            dp4 = -dp4;
                        }
                        canvas.translate(var_, dp3 + (((float) dp4) * (1.0f - f4)));
                        this.textPaint.setAlpha((int) (f4 * 255.0f));
                        this.countLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.countOldLayout != null) {
                        canvas.save();
                        canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(4.0f)) + measuredHeight + (((float) (z ? -AndroidUtilities.dp(13.0f) : AndroidUtilities.dp(13.0f))) * f4));
                        this.textPaint.setAlpha((int) ((1.0f - f4) * 255.0f));
                        this.countOldLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.countAnimationStableLayout != null) {
                        canvas.save();
                        canvas.translate(this.countLeft, measuredHeight + ((float) AndroidUtilities.dp(4.0f)));
                        this.textPaint.setAlpha(255);
                        this.countAnimationStableLayout.draw(canvas);
                        canvas.restore();
                    }
                    this.textPaint.setAlpha(255);
                    canvas.restore();
                    return;
                }
            }
            updateX((float) this.countWidth);
            float var_ = this.countLeft + (((float) this.countWidth) / 2.0f);
            float measuredHeight2 = ((float) getMeasuredHeight()) / 2.0f;
            canvas.save();
            float var_ = this.animationType == 0 ? this.countChangeProgress : 1.0f - this.countChangeProgress;
            canvas.scale(var_, var_, var_, measuredHeight2);
            drawInternal(canvas);
            canvas.restore();
            return;
        }
        drawInternal(canvas);
    }

    private void updateX(float f) {
        int i = this.gravity;
        if (i == 5) {
            float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(5.5f));
            this.countLeft = measuredWidth;
            float f2 = this.horizontalPadding;
            if (f2 != 0.0f) {
                this.countLeft = measuredWidth - Math.max(f2 + (f / 2.0f), f);
            } else {
                this.countLeft = measuredWidth - f;
            }
        } else if (i == 3) {
            this.countLeft = (float) AndroidUtilities.dp(5.5f);
        } else {
            this.countLeft = (float) ((int) ((((float) getMeasuredWidth()) - f) / 2.0f));
        }
        this.x = this.countLeft - ((float) AndroidUtilities.dp(5.5f));
    }

    private void drawInternal(Canvas canvas) {
        float measuredHeight = ((float) (getMeasuredHeight() - AndroidUtilities.dp(23.0f))) / 2.0f;
        updateX((float) this.countWidth);
        RectF rectF2 = this.rectF;
        float f = this.x;
        rectF2.set(f, measuredHeight, ((float) this.countWidth) + f + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + measuredHeight);
        RectF rectF3 = this.rectF;
        float f2 = AndroidUtilities.density;
        canvas.drawRoundRect(rectF3, f2 * 11.5f, f2 * 11.5f, this.circlePaint);
        if (this.countLayout != null) {
            canvas.save();
            canvas.translate(this.countLeft, measuredHeight + ((float) AndroidUtilities.dp(4.0f)));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void setColors(String str, String str2) {
        this.textColorKey = str;
        this.circleColorKey = str2;
    }

    public void setGravity(int i) {
        this.gravity = i;
    }

    public void setReverse(boolean z) {
        this.reverseAnimation = z;
    }
}
