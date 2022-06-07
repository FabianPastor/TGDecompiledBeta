package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SlideChooseView extends View {
    private final SeekBarAccessibilityDelegate accessibilityDelegate;
    private Callback callback;
    private int circleSize;
    private int dashedFrom;
    private int gapSize;
    private int lastDash;
    private Paint linePaint;
    private int lineSize;
    private boolean moving;
    private AnimatedFloat movingAnimatedHolder;
    private int[] optionsSizes;
    /* access modifiers changed from: private */
    public String[] optionsStr;
    private Paint paint;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int selectedIndex;
    private AnimatedFloat selectedIndexAnimatedHolder;
    private float selectedIndexTouch;
    private int sideSide;
    private boolean startMoving;
    private int startMovingPreset;
    private TextPaint textPaint;
    private float xTouchDown;
    private float yTouchDown;

    public interface Callback {

        /* renamed from: org.telegram.ui.Components.SlideChooseView$Callback$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onTouchEnd(Callback callback) {
            }
        }

        void onOptionSelected(int i);

        void onTouchEnd();
    }

    public SlideChooseView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public SlideChooseView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.dashedFrom = -1;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.selectedIndexAnimatedHolder = new AnimatedFloat((View) this, 120, (TimeInterpolator) cubicBezierInterpolator);
        this.movingAnimatedHolder = new AnimatedFloat((View) this, 150, (TimeInterpolator) cubicBezierInterpolator);
        this.resourcesProvider = resourcesProvider2;
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        Paint paint2 = new Paint(1);
        this.linePaint = paint2;
        paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.accessibilityDelegate = new IntSeekBarAccessibilityDelegate() {
            /* access modifiers changed from: protected */
            public int getProgress() {
                return SlideChooseView.this.selectedIndex;
            }

            /* access modifiers changed from: protected */
            public void setProgress(int i) {
                SlideChooseView.this.setOption(i);
            }

            /* access modifiers changed from: protected */
            public int getMaxValue() {
                return SlideChooseView.this.optionsStr.length - 1;
            }

            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(View view) {
                if (SlideChooseView.this.selectedIndex < SlideChooseView.this.optionsStr.length) {
                    return SlideChooseView.this.optionsStr[SlideChooseView.this.selectedIndex];
                }
                return null;
            }
        };
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public void setOptions(int i, String... strArr) {
        this.optionsStr = strArr;
        this.selectedIndex = i;
        this.optionsSizes = new int[strArr.length];
        int i2 = 0;
        while (true) {
            String[] strArr2 = this.optionsStr;
            if (i2 < strArr2.length) {
                this.optionsSizes[i2] = (int) Math.ceil((double) this.textPaint.measureText(strArr2[i2]));
                i2++;
            } else {
                requestLayout();
                return;
            }
        }
    }

    public void setDashedFrom(int i) {
        this.dashedFrom = i;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int i = this.circleSize;
        float clamp = MathUtils.clamp(((x - ((float) this.sideSide)) + (((float) i) / 2.0f)) / ((float) ((this.lineSize + (this.gapSize * 2)) + i)), 0.0f, (float) (this.optionsStr.length - 1));
        boolean z = Math.abs(clamp - ((float) Math.round(clamp))) < 0.35f;
        if (z) {
            clamp = (float) Math.round(clamp);
        }
        if (motionEvent.getAction() == 0) {
            this.xTouchDown = x;
            this.yTouchDown = y;
            this.selectedIndexTouch = clamp;
            this.startMovingPreset = this.selectedIndex;
            this.startMoving = true;
            invalidate();
        } else if (motionEvent.getAction() == 2) {
            if (!this.moving && Math.abs(this.xTouchDown - x) > Math.abs(this.yTouchDown - y)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.startMoving && Math.abs(this.xTouchDown - x) >= ((float) AndroidUtilities.dp(2.0f))) {
                this.moving = true;
                this.startMoving = false;
            }
            if (this.moving) {
                this.selectedIndexTouch = clamp;
                invalidate();
                if (Math.round(this.selectedIndexTouch) != this.selectedIndex && z) {
                    setOption(Math.round(this.selectedIndexTouch));
                }
            }
            invalidate();
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (!this.moving) {
                this.selectedIndexTouch = clamp;
                if (Math.round(clamp) != this.selectedIndex) {
                    setOption(Math.round(this.selectedIndexTouch));
                }
            } else {
                int i2 = this.selectedIndex;
                if (i2 != this.startMovingPreset) {
                    setOption(i2);
                }
            }
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onTouchEnd();
            }
            this.startMoving = false;
            this.moving = false;
            invalidate();
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void setOption(int i) {
        if (this.selectedIndex != i) {
            try {
                performHapticFeedback(9, 1);
            } catch (Exception unused) {
            }
        }
        this.selectedIndex = i;
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onOptionSelected(i);
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
        this.circleSize = AndroidUtilities.dp(6.0f);
        this.gapSize = AndroidUtilities.dp(2.0f);
        this.sideSide = AndroidUtilities.dp(22.0f);
        int measuredWidth = getMeasuredWidth();
        int i3 = this.circleSize;
        String[] strArr = this.optionsStr;
        this.lineSize = (((measuredWidth - (i3 * strArr.length)) - ((this.gapSize * 2) * (strArr.length - 1))) - (this.sideSide * 2)) / (strArr.length - 1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        float f;
        int i2;
        float f2;
        Canvas canvas2 = canvas;
        float f3 = this.selectedIndexAnimatedHolder.set((float) this.selectedIndex);
        float f4 = 0.0f;
        float f5 = 1.0f;
        float f6 = this.movingAnimatedHolder.set(this.moving ? 1.0f : 0.0f);
        int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
        int i3 = 0;
        while (i3 < this.optionsStr.length) {
            int i4 = this.sideSide;
            int i5 = this.lineSize + (this.gapSize * 2);
            int i6 = this.circleSize;
            int i7 = i4 + ((i5 + i6) * i3) + (i6 / 2);
            float f7 = (float) i3;
            float f8 = f7 - f3;
            float max = Math.max(f4, f5 - Math.abs(f8));
            int blendARGB = ColorUtils.blendARGB(getThemedColor("switchTrack"), getThemedColor("switchTrackChecked"), MathUtils.clamp((f3 - f7) + f5, f4, f5));
            this.paint.setColor(blendARGB);
            this.linePaint.setColor(blendARGB);
            float f9 = (float) measuredHeight;
            canvas2.drawCircle((float) i7, f9, (float) AndroidUtilities.lerp(this.circleSize / 2, AndroidUtilities.dp(6.0f), max), this.paint);
            if (i3 != 0) {
                int i8 = (i7 - (this.circleSize / 2)) - this.gapSize;
                int i9 = this.lineSize;
                int i10 = i8 - i9;
                int i11 = this.dashedFrom;
                if (i11 == -1 || i3 - 1 < i11) {
                    f = max;
                    i = i7;
                    float var_ = f8 - 1.0f;
                    float clamp = MathUtils.clamp(1.0f - Math.abs(var_), 0.0f, 1.0f);
                    float clamp2 = MathUtils.clamp(1.0f - Math.min(Math.abs(f8), Math.abs(var_)), 0.0f, 1.0f);
                    int dp = (int) (((float) i10) + (((float) AndroidUtilities.dp(3.0f)) * clamp));
                    canvas.drawRect((float) dp, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (dp + ((int) (((float) i9) - (((float) AndroidUtilities.dp(3.0f)) * clamp2)))), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
                } else {
                    int dp2 = i10 + AndroidUtilities.dp(3.0f);
                    int dp3 = i9 - AndroidUtilities.dp(3.0f);
                    int dp4 = dp3 / AndroidUtilities.dp(13.0f);
                    if (this.lastDash != dp4) {
                        f2 = max;
                        i2 = i7;
                        this.linePaint.setPathEffect(new DashPathEffect(new float[]{(float) AndroidUtilities.dp(6.0f), ((float) (dp3 - (AndroidUtilities.dp(8.0f) * dp4))) / ((float) (dp4 - 1))}, 0.0f));
                        this.lastDash = dp4;
                    } else {
                        f2 = max;
                        i2 = i7;
                    }
                    float dp5 = (float) ((dp2 + dp3) - AndroidUtilities.dp(1.0f));
                    float var_ = f9;
                    float var_ = dp5;
                    f = f2;
                    float var_ = f9;
                    i = i2;
                    canvas.drawLine((float) (AndroidUtilities.dp(1.0f) + dp2), var_, var_, var_, this.linePaint);
                }
            } else {
                f = max;
                i = i7;
            }
            int i12 = this.optionsSizes[i3];
            String str = this.optionsStr[i3];
            this.textPaint.setColor(ColorUtils.blendARGB(getThemedColor("windowBackgroundWhiteGrayText"), getThemedColor("windowBackgroundWhiteBlueText"), f));
            if (i3 == 0) {
                canvas2.drawText(str, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else if (i3 == this.optionsStr.length - 1) {
                canvas2.drawText(str, (float) ((getMeasuredWidth() - i12) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else {
                canvas2.drawText(str, (float) (i - (i12 / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            }
            i3++;
            f4 = 0.0f;
            f5 = 1.0f;
        }
        int i13 = this.lineSize + (this.gapSize * 2);
        int i14 = this.circleSize;
        float var_ = ((float) this.sideSide) + (((float) (i13 + i14)) * f3) + ((float) (i14 / 2));
        this.paint.setColor(ColorUtils.setAlphaComponent(getThemedColor("switchTrackChecked"), 80));
        float var_ = (float) measuredHeight;
        canvas2.drawCircle(var_, var_, (float) AndroidUtilities.dp(f6 * 12.0f), this.paint);
        this.paint.setColor(getThemedColor("switchTrackChecked"));
        canvas2.drawCircle(var_, var_, (float) AndroidUtilities.dp(6.0f), this.paint);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        return super.performAccessibilityAction(i, bundle) || this.accessibilityDelegate.performAccessibilityActionInternal(this, i, bundle);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
