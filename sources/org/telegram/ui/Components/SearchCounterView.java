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
    private static final int ANIMATION_TYPE_REPLACE = 2;
    int animationType = -1;
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() != this.lastH) {
            int count = this.currentCount;
            String str = this.currentString;
            this.currentString = null;
            setCount(str, count, false);
            this.lastH = getMeasuredHeight();
        }
    }

    public void setCount(String newStr, int count, boolean animated) {
        boolean animated2;
        String newStr2 = newStr;
        int i = count;
        String str = this.currentString;
        if (str == null || !str.equals(newStr2)) {
            ValueAnimator valueAnimator = this.countAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.currentCount == 0 || i <= 0 || newStr2 == null || LocaleController.isRTL || TextUtils.isEmpty(newStr)) {
                animated2 = false;
            } else {
                animated2 = animated;
            }
            if (animated2 && newStr2 != null && !newStr2.contains("**")) {
                animated2 = false;
            }
            if (!animated2) {
                if (newStr2 != null) {
                    newStr2 = newStr2.replaceAll("\\*\\*", "");
                }
                this.currentCount = i;
                if (newStr2 == null) {
                    this.countWidth = 0;
                    this.countLayout = null;
                } else {
                    this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(newStr2)));
                    StaticLayout staticLayout = r10;
                    StaticLayout staticLayout2 = new StaticLayout(newStr2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    this.countLayout = staticLayout;
                }
                invalidate();
            }
            this.dx = 0.0f;
            if (animated2) {
                ValueAnimator valueAnimator2 = this.countAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.countChangeProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.countAnimator = ofFloat;
                ofFloat.addUpdateListener(new SearchCounterView$$ExternalSyntheticLambda0(this));
                this.countAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        SearchCounterView.this.animationType = -1;
                        float unused = SearchCounterView.this.countChangeProgress = 1.0f;
                        StaticLayout unused2 = SearchCounterView.this.countOldLayout = null;
                        StaticLayout unused3 = SearchCounterView.this.countAnimationStableLayout = null;
                        StaticLayout unused4 = SearchCounterView.this.countAnimationInLayout = null;
                        SearchCounterView.this.invalidate();
                    }
                });
                this.animationType = 2;
                this.countAnimator.setDuration(200);
                this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                if (this.countLayout != null) {
                    String oldStr = this.currentString;
                    int countStartIndex = newStr2.indexOf("**");
                    if (countStartIndex >= 0) {
                        newStr2 = newStr2.replaceAll("\\*\\*", "");
                    } else {
                        countStartIndex = 0;
                    }
                    SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                    SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr2);
                    SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr2);
                    boolean replaceAllDigits = Integer.toString(this.currentCount).length() != Integer.toString(count).length();
                    int n = Math.min(oldStr.length(), newStr2.length());
                    int cutIndexNew = 0;
                    if (countStartIndex > 0) {
                        oldSpannableStr.setSpan(new EmptyStubSpan(), 0, countStartIndex, 33);
                        newSpannableStr.setSpan(new EmptyStubSpan(), 0, countStartIndex, 33);
                        stableStr.setSpan(new EmptyStubSpan(), 0, countStartIndex, 33);
                    }
                    boolean newEndReached = false;
                    boolean oldEndReached = false;
                    int cutIndexOld = 0;
                    for (int i2 = countStartIndex; i2 < n; i2++) {
                        if (!newEndReached && !oldEndReached) {
                            if (replaceAllDigits) {
                                stableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 33);
                            } else if (oldStr.charAt(i2) == newStr2.charAt(i2)) {
                                oldSpannableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 33);
                                newSpannableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 33);
                            } else {
                                stableStr.setSpan(new EmptyStubSpan(), i2, i2 + 1, 33);
                            }
                        }
                        if (!Character.isDigit(newStr2.charAt(i2))) {
                            newSpannableStr.setSpan(new EmptyStubSpan(), i2, newStr2.length(), 33);
                            newEndReached = true;
                            cutIndexNew = i2;
                        }
                        if (!Character.isDigit(oldStr.charAt(i2))) {
                            oldSpannableStr.setSpan(new EmptyStubSpan(), i2, oldStr.length(), 33);
                            oldEndReached = true;
                            cutIndexOld = i2;
                        }
                    }
                    int countOldWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(oldStr)));
                    int countNewWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(newStr2)));
                    boolean z = animated2;
                    int cutIndexOld2 = cutIndexOld;
                    String str2 = oldStr;
                    int cutIndexNew2 = cutIndexNew;
                    int i3 = n;
                    this.countOldLayout = new StaticLayout(oldSpannableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    this.countAnimationStableLayout = new StaticLayout(stableStr, this.textPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    this.countAnimationInLayout = new StaticLayout(newSpannableStr, this.textPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    if (countStartIndex > 0) {
                        SpannableStringBuilder stableString2 = new SpannableStringBuilder(newStr2);
                        stableString2.setSpan(new EmptyStubSpan(), countStartIndex, newStr2.length(), 0);
                        this.countAnimationStableLayout2 = new StaticLayout(stableString2, this.textPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.countAnimationStableLayout2 = null;
                    }
                    this.dx = this.countOldLayout.getPrimaryHorizontal(cutIndexOld2) - this.countAnimationStableLayout.getPrimaryHorizontal(cutIndexNew2);
                }
                this.countWidthOld = this.countWidth;
                this.countAnimationIncrement = i < this.currentCount;
                this.countAnimator.start();
            }
            if (i > 0) {
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) this.textPaint.measureText(newStr2)));
                this.countLayout = new StaticLayout(newStr2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            }
            this.currentCount = i;
            invalidate();
            this.currentString = newStr2;
        }
    }

    /* renamed from: lambda$setCount$0$org-telegram-ui-Components-SearchCounterView  reason: not valid java name */
    public /* synthetic */ void m1322lambda$setCount$0$orgtelegramuiComponentsSearchCounterView(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float countWidth2;
        super.onDraw(canvas);
        int textColor2 = Theme.getColor(this.textColorKey, this.resourcesProvider);
        if (this.textColor != textColor2) {
            this.textColor = textColor2;
            this.textPaint.setColor(textColor2);
        }
        if (this.countChangeProgress != 1.0f) {
            float countTop = ((float) (getMeasuredHeight() - AndroidUtilities.dp(23.0f))) / 2.0f;
            int i = this.countWidth;
            int i2 = this.countWidthOld;
            if (i == i2) {
                countWidth2 = (float) i;
            } else {
                float f = this.countChangeProgress;
                countWidth2 = (((float) i) * f) + (((float) i2) * (1.0f - f));
            }
            updateX(countWidth2);
            RectF rectF2 = this.rectF;
            float f2 = this.x;
            rectF2.set(f2, countTop, f2 + countWidth2 + ((float) AndroidUtilities.dp(11.0f)), ((float) AndroidUtilities.dp(23.0f)) + countTop);
            boolean increment = this.countAnimationIncrement;
            if (this.countAnimationInLayout != null) {
                canvas.save();
                float f3 = this.countLeft;
                float dp = ((float) AndroidUtilities.dp(2.0f)) + countTop;
                int dp2 = AndroidUtilities.dp(13.0f);
                if (!increment) {
                    dp2 = -dp2;
                }
                canvas.translate(f3, dp + (((float) dp2) * (1.0f - this.countChangeProgress)));
                this.textPaint.setAlpha((int) (this.countChangeProgress * 255.0f));
                this.countAnimationInLayout.draw(canvas);
                canvas.restore();
            } else if (this.countLayout != null) {
                canvas.save();
                float f4 = this.countLeft;
                float dp3 = ((float) AndroidUtilities.dp(2.0f)) + countTop;
                int dp4 = AndroidUtilities.dp(13.0f);
                if (!increment) {
                    dp4 = -dp4;
                }
                canvas.translate(f4, dp3 + (((float) dp4) * (1.0f - this.countChangeProgress)));
                this.textPaint.setAlpha((int) (this.countChangeProgress * 255.0f));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countOldLayout != null) {
                canvas.save();
                float f5 = this.countLeft;
                float dp5 = ((float) AndroidUtilities.dp(2.0f)) + countTop;
                int dp6 = AndroidUtilities.dp(13.0f);
                if (increment) {
                    dp6 = -dp6;
                }
                canvas.translate(f5, dp5 + (((float) dp6) * this.countChangeProgress));
                this.textPaint.setAlpha((int) ((1.0f - this.countChangeProgress) * 255.0f));
                this.countOldLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countAnimationStableLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft + (this.dx * (1.0f - this.countChangeProgress)), ((float) AndroidUtilities.dp(2.0f)) + countTop);
                this.textPaint.setAlpha(255);
                this.countAnimationStableLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countAnimationStableLayout2 != null) {
                canvas.save();
                canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(2.0f)) + countTop);
                this.textPaint.setAlpha(255);
                this.countAnimationStableLayout2.draw(canvas);
                canvas.restore();
            }
            this.textPaint.setAlpha(255);
            return;
        }
        drawInternal(canvas);
    }

    private void updateX(float countWidth2) {
        int i = this.gravity;
        if (i == 5) {
            float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(5.5f));
            this.countLeft = measuredWidth;
            float f = this.horizontalPadding;
            if (f != 0.0f) {
                this.countLeft = measuredWidth - Math.max(f + (countWidth2 / 2.0f), countWidth2);
            } else {
                this.countLeft = measuredWidth - countWidth2;
            }
        } else if (i == 3) {
            this.countLeft = (float) AndroidUtilities.dp(5.5f);
        } else {
            this.countLeft = (float) ((int) ((((float) getMeasuredWidth()) - countWidth2) / 2.0f));
        }
        this.x = this.countLeft - ((float) AndroidUtilities.dp(5.5f));
    }

    private void drawInternal(Canvas canvas) {
        float countTop = ((float) (getMeasuredHeight() - AndroidUtilities.dp(23.0f))) / 2.0f;
        updateX((float) this.countWidth);
        if (this.countLayout != null) {
            canvas.save();
            canvas.translate(this.countLeft, ((float) AndroidUtilities.dp(2.0f)) + countTop);
            this.countLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void setGravity(int gravity2) {
        this.gravity = gravity2;
    }
}
