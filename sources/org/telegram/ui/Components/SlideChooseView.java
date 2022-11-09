package org.telegram.ui.Components;

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
/* loaded from: classes3.dex */
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
    private String[] optionsStr;
    private Paint paint;
    private final Theme.ResourcesProvider resourcesProvider;
    private int selectedIndex;
    private AnimatedFloat selectedIndexAnimatedHolder;
    private float selectedIndexTouch;
    private int sideSide;
    private boolean startMoving;
    private int startMovingPreset;
    private TextPaint textPaint;
    private float xTouchDown;
    private float yTouchDown;

    /* loaded from: classes3.dex */
    public interface Callback {

        /* renamed from: org.telegram.ui.Components.SlideChooseView$Callback$-CC  reason: invalid class name */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$onTouchEnd(Callback callback) {
            }
        }

        void onOptionSelected(int i);

        void onTouchEnd();
    }

    public SlideChooseView(Context context) {
        this(context, null);
    }

    public SlideChooseView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.dashedFrom = -1;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        this.selectedIndexAnimatedHolder = new AnimatedFloat(this, 120L, cubicBezierInterpolator);
        this.movingAnimatedHolder = new AnimatedFloat(this, 150L, cubicBezierInterpolator);
        this.resourcesProvider = resourcesProvider;
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        Paint paint = new Paint(1);
        this.linePaint = paint;
        paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.accessibilityDelegate = new IntSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.Components.SlideChooseView.1
            @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
            protected int getProgress() {
                return SlideChooseView.this.selectedIndex;
            }

            @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
            protected void setProgress(int i) {
                SlideChooseView.this.setOption(i);
            }

            @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
            protected int getMaxValue() {
                return SlideChooseView.this.optionsStr.length - 1;
            }

            @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
            /* renamed from: getContentDescription */
            protected CharSequence mo2225getContentDescription(View view) {
                if (SlideChooseView.this.selectedIndex < SlideChooseView.this.optionsStr.length) {
                    return SlideChooseView.this.optionsStr[SlideChooseView.this.selectedIndex];
                }
                return null;
            }
        };
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setOptions(int i, String... strArr) {
        this.optionsStr = strArr;
        this.selectedIndex = i;
        this.optionsSizes = new int[strArr.length];
        int i2 = 0;
        while (true) {
            String[] strArr2 = this.optionsStr;
            if (i2 < strArr2.length) {
                this.optionsSizes[i2] = (int) Math.ceil(this.textPaint.measureText(strArr2[i2]));
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

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float clamp = MathUtils.clamp(((x - this.sideSide) + (this.circleSize / 2.0f)) / ((this.lineSize + (this.gapSize * 2)) + i), 0.0f, this.optionsStr.length - 1);
        boolean z = Math.abs(clamp - ((float) Math.round(clamp))) < 0.35f;
        if (z) {
            clamp = Math.round(clamp);
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
            if (this.startMoving && Math.abs(this.xTouchDown - x) >= AndroidUtilities.dp(2.0f)) {
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
            Callback callback = this.callback;
            if (callback != null) {
                callback.onTouchEnd();
            }
            this.startMoving = false;
            this.moving = false;
            invalidate();
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOption(int i) {
        if (this.selectedIndex != i) {
            try {
                performHapticFeedback(9, 1);
            } catch (Exception unused) {
            }
        }
        this.selectedIndex = i;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onOptionSelected(i);
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
        this.circleSize = AndroidUtilities.dp(6.0f);
        this.gapSize = AndroidUtilities.dp(2.0f);
        this.sideSide = AndroidUtilities.dp(22.0f);
        int measuredWidth = getMeasuredWidth();
        int i3 = this.circleSize;
        String[] strArr = this.optionsStr;
        this.lineSize = (((measuredWidth - (i3 * strArr.length)) - ((this.gapSize * 2) * (strArr.length - 1))) - (this.sideSide * 2)) / (strArr.length - 1);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f;
        int i;
        int dp;
        int dp2;
        float f2;
        int i2;
        float f3 = this.selectedIndexAnimatedHolder.set(this.selectedIndex);
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
            float f7 = i3;
            float f8 = f7 - f3;
            float max = Math.max(f4, f5 - Math.abs(f8));
            int blendARGB = ColorUtils.blendARGB(getThemedColor("switchTrack"), getThemedColor("switchTrackChecked"), MathUtils.clamp((f3 - f7) + f5, f4, f5));
            this.paint.setColor(blendARGB);
            this.linePaint.setColor(blendARGB);
            float f9 = measuredHeight;
            canvas.drawCircle(i7, f9, AndroidUtilities.lerp(this.circleSize / 2, AndroidUtilities.dp(6.0f), max), this.paint);
            if (i3 != 0) {
                int i8 = (i7 - (this.circleSize / 2)) - this.gapSize;
                int i9 = this.lineSize;
                int i10 = i8 - i9;
                int i11 = this.dashedFrom;
                if (i11 != -1 && i3 - 1 >= i11) {
                    int dp3 = i10 + AndroidUtilities.dp(3.0f);
                    int dp4 = (i9 - AndroidUtilities.dp(3.0f)) / AndroidUtilities.dp(13.0f);
                    if (this.lastDash != dp4) {
                        f2 = max;
                        i2 = i7;
                        this.linePaint.setPathEffect(new DashPathEffect(new float[]{AndroidUtilities.dp(6.0f), (dp2 - (AndroidUtilities.dp(8.0f) * dp4)) / (dp4 - 1)}, 0.0f));
                        this.lastDash = dp4;
                    } else {
                        f2 = max;
                        i2 = i7;
                    }
                    f = f2;
                    i = i2;
                    canvas.drawLine(AndroidUtilities.dp(1.0f) + dp3, f9, (dp3 + dp2) - AndroidUtilities.dp(1.0f), f9, this.linePaint);
                } else {
                    f = max;
                    i = i7;
                    float var_ = f8 - 1.0f;
                    float clamp = MathUtils.clamp(1.0f - Math.abs(var_), 0.0f, 1.0f);
                    float clamp2 = MathUtils.clamp(1.0f - Math.min(Math.abs(f8), Math.abs(var_)), 0.0f, 1.0f);
                    canvas.drawRect((int) (i10 + (AndroidUtilities.dp(3.0f) * clamp)), measuredHeight - AndroidUtilities.dp(1.0f), dp + ((int) (i9 - (AndroidUtilities.dp(3.0f) * clamp2))), AndroidUtilities.dp(1.0f) + measuredHeight, this.paint);
                }
            } else {
                f = max;
                i = i7;
            }
            int i12 = this.optionsSizes[i3];
            String str = this.optionsStr[i3];
            this.textPaint.setColor(ColorUtils.blendARGB(getThemedColor("windowBackgroundWhiteGrayText"), getThemedColor("windowBackgroundWhiteBlueText"), f));
            if (i3 == 0) {
                canvas.drawText(str, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(28.0f), this.textPaint);
            } else if (i3 == this.optionsStr.length - 1) {
                canvas.drawText(str, (getMeasuredWidth() - i12) - AndroidUtilities.dp(22.0f), AndroidUtilities.dp(28.0f), this.textPaint);
            } else {
                canvas.drawText(str, i - (i12 / 2), AndroidUtilities.dp(28.0f), this.textPaint);
            }
            i3++;
            f4 = 0.0f;
            f5 = 1.0f;
        }
        int i13 = this.lineSize + (this.gapSize * 2);
        int i14 = this.circleSize;
        float var_ = this.sideSide + ((i13 + i14) * f3) + (i14 / 2);
        this.paint.setColor(ColorUtils.setAlphaComponent(getThemedColor("switchTrackChecked"), 80));
        float var_ = measuredHeight;
        canvas.drawCircle(var_, var_, AndroidUtilities.dp(f6 * 12.0f), this.paint);
        this.paint.setColor(getThemedColor("switchTrackChecked"));
        canvas.drawCircle(var_, var_, AndroidUtilities.dp(6.0f), this.paint);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        return super.performAccessibilityAction(i, bundle) || this.accessibilityDelegate.performAccessibilityActionInternal(this, i, bundle);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
