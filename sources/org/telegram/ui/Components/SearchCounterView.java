package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class SearchCounterView extends View {
    int animationType;
    /* access modifiers changed from: private */
    public StaticLayout countAnimationInLayout;
    private boolean countAnimationIncrement;
    /* access modifiers changed from: private */
    public StaticLayout countAnimationStableLayout;
    private StaticLayout countAnimationStableLayout2;
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
    String currentString;
    float dx = 0.0f;
    int gravity = 17;
    public float horizontalPadding;
    int lastH;
    RectF rectF = new RectF();
    private final Theme.ResourcesProvider resourcesProvider;
    private int textColor;
    private String textColorKey = "chat_searchPanelText";
    TextPaint textPaint = new TextPaint(1);
    float x;

    public SearchCounterView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredHeight() != this.lastH) {
            int i3 = this.currentCount;
            String str = this.currentString;
            this.currentString = null;
            setCount(str, i3, false);
            this.lastH = getMeasuredHeight();
        }
    }

    public void setCount(String str, int i, boolean z) {
        String str2 = str;
        int i2 = i;
        String str3 = this.currentString;
        if (str3 == null || !str3.equals(str2)) {
            ValueAnimator valueAnimator = this.countAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z2 = (this.currentCount == 0 || i2 <= 0 || str2 == null || LocaleController.isRTL || TextUtils.isEmpty(str)) ? false : z;
            if (z2 && str2 != null && !str2.contains("**")) {
                z2 = false;
            }
            if (!z2) {
                if (str2 != null) {
                    str2 = str2.replaceAll("\\*\\*", "");
                }
                this.currentCount = i2;
                if (str2 == null) {
                    this.countWidth = 0;
                    this.countLayout = null;
                } else {
                    this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(str2)));
                    StaticLayout staticLayout = r10;
                    StaticLayout staticLayout2 = new StaticLayout(str2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    this.countLayout = staticLayout;
                }
                invalidate();
            }
            this.dx = 0.0f;
            if (z2) {
                ValueAnimator valueAnimator2 = this.countAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.countChangeProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.countAnimator = ofFloat;
                ofFloat.addUpdateListener(new SearchCounterView$$ExternalSyntheticLambda0(this));
                this.countAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        SearchCounterView searchCounterView = SearchCounterView.this;
                        searchCounterView.animationType = -1;
                        float unused = searchCounterView.countChangeProgress = 1.0f;
                        StaticLayout unused2 = SearchCounterView.this.countOldLayout = null;
                        StaticLayout unused3 = SearchCounterView.this.countAnimationStableLayout = null;
                        StaticLayout unused4 = SearchCounterView.this.countAnimationInLayout = null;
                        SearchCounterView.this.invalidate();
                    }
                });
                this.countAnimator.setDuration(200);
                this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                if (this.countLayout != null) {
                    String str4 = this.currentString;
                    int indexOf = str2.indexOf("**");
                    if (indexOf >= 0) {
                        str2 = str2.replaceAll("\\*\\*", "");
                    } else {
                        indexOf = 0;
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str4);
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(str2);
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(str2);
                    boolean z3 = Integer.toString(this.currentCount).length() != Integer.toString(i).length();
                    int min = Math.min(str4.length(), str2.length());
                    if (indexOf > 0) {
                        spannableStringBuilder.setSpan(new EmptyStubSpan(), 0, indexOf, 33);
                        spannableStringBuilder2.setSpan(new EmptyStubSpan(), 0, indexOf, 33);
                        spannableStringBuilder3.setSpan(new EmptyStubSpan(), 0, indexOf, 33);
                    }
                    int i3 = 0;
                    boolean z4 = false;
                    boolean z5 = false;
                    int i4 = 0;
                    for (int i5 = indexOf; i5 < min; i5++) {
                        if (!z4 && !z5) {
                            if (z3) {
                                spannableStringBuilder3.setSpan(new EmptyStubSpan(), i5, i5 + 1, 33);
                            } else if (str4.charAt(i5) == str2.charAt(i5)) {
                                int i6 = i5 + 1;
                                spannableStringBuilder.setSpan(new EmptyStubSpan(), i5, i6, 33);
                                spannableStringBuilder2.setSpan(new EmptyStubSpan(), i5, i6, 33);
                            } else {
                                spannableStringBuilder3.setSpan(new EmptyStubSpan(), i5, i5 + 1, 33);
                            }
                        }
                        if (!Character.isDigit(str2.charAt(i5))) {
                            spannableStringBuilder2.setSpan(new EmptyStubSpan(), i5, str2.length(), 33);
                            i4 = i5;
                            z4 = true;
                        }
                        if (!Character.isDigit(str4.charAt(i5))) {
                            spannableStringBuilder.setSpan(new EmptyStubSpan(), i5, str4.length(), 33);
                            i3 = i5;
                            z5 = true;
                        }
                    }
                    int max = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(str4)));
                    int max2 = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(str2)));
                    this.countOldLayout = new StaticLayout(spannableStringBuilder, this.textPaint, max, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    int i7 = max2;
                    this.countAnimationStableLayout = new StaticLayout(spannableStringBuilder3, this.textPaint, i7, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    this.countAnimationInLayout = new StaticLayout(spannableStringBuilder2, this.textPaint, i7, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    if (indexOf > 0) {
                        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(str2);
                        spannableStringBuilder4.setSpan(new EmptyStubSpan(), indexOf, str2.length(), 0);
                        this.countAnimationStableLayout2 = new StaticLayout(spannableStringBuilder4, this.textPaint, max2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.countAnimationStableLayout2 = null;
                    }
                    this.dx = this.countOldLayout.getPrimaryHorizontal(i3) - this.countAnimationStableLayout.getPrimaryHorizontal(i4);
                }
                this.countWidthOld = this.countWidth;
                this.countAnimationIncrement = i2 < this.currentCount;
                this.countAnimator.start();
            }
            if (i2 > 0) {
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(str2)));
                this.countLayout = new StaticLayout(str2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            }
            this.currentCount = i2;
            invalidate();
            this.currentString = str2;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setCount$0(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        super.onDraw(canvas);
        int color = Theme.getColor(this.textColorKey, this.resourcesProvider);
        if (this.textColor != color) {
            this.textColor = color;
            this.textPaint.setColor(color);
        }
        if (this.countChangeProgress != 1.0f) {
            float measuredHeight = ((float) (getMeasuredHeight() - AndroidUtilities.dp(23.0f))) / 2.0f;
            int i = this.countWidth;
            int i2 = this.countWidthOld;
            if (i == i2) {
                f = (float) i;
            } else {
                float f2 = this.countChangeProgress;
                f = (((float) i) * f2) + (((float) i2) * (1.0f - f2));
            }
            updateX(f);
            RectF rectF2 = this.rectF;
            float f3 = this.x;
            rectF2.set(f3, measuredHeight, f + f3 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + measuredHeight);
            boolean z = this.countAnimationIncrement;
            if (this.countAnimationInLayout != null) {
                canvas.save();
                float f4 = this.countLeft;
                float dp = ((float) AndroidUtilities.dp(2.0f)) + measuredHeight;
                int dp2 = AndroidUtilities.dp(13.0f);
                if (!z) {
                    dp2 = -dp2;
                }
                canvas.translate(f4, dp + (((float) dp2) * (1.0f - this.countChangeProgress)));
                this.textPaint.setAlpha((int) (this.countChangeProgress * 255.0f));
                this.countAnimationInLayout.draw(canvas);
                canvas.restore();
            } else if (this.countLayout != null) {
                canvas.save();
                float f5 = this.countLeft;
                float dp3 = ((float) AndroidUtilities.dp(2.0f)) + measuredHeight;
                int dp4 = AndroidUtilities.dp(13.0f);
                if (!z) {
                    dp4 = -dp4;
                }
                canvas.translate(f5, dp3 + (((float) dp4) * (1.0f - this.countChangeProgress)));
                this.textPaint.setAlpha((int) (this.countChangeProgress * 255.0f));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countOldLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(2.0f)) + measuredHeight + (((float) (z ? -AndroidUtilities.dp(13.0f) : AndroidUtilities.dp(13.0f))) * this.countChangeProgress));
                this.textPaint.setAlpha((int) ((1.0f - this.countChangeProgress) * 255.0f));
                this.countOldLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countAnimationStableLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft + (this.dx * (1.0f - this.countChangeProgress)), ((float) AndroidUtilities.dp(2.0f)) + measuredHeight);
                this.textPaint.setAlpha(255);
                this.countAnimationStableLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countAnimationStableLayout2 != null) {
                canvas.save();
                canvas.translate(this.countLeft, measuredHeight + ((float) AndroidUtilities.dp(2.0f)));
                this.textPaint.setAlpha(255);
                this.countAnimationStableLayout2.draw(canvas);
                canvas.restore();
            }
            this.textPaint.setAlpha(255);
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
        if (this.countLayout != null) {
            canvas.save();
            canvas.translate(this.countLeft, measuredHeight + ((float) AndroidUtilities.dp(2.0f)));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void setGravity(int i) {
        this.gravity = i;
    }
}
