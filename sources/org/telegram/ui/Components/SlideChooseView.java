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
    private boolean touchWasClose;
    private float xTouchDown;
    private float yTouchDown;

    public SlideChooseView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public SlideChooseView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.dashedFrom = -1;
        this.selectedIndexAnimatedHolder = new AnimatedFloat((View) this, 120, (TimeInterpolator) CubicBezierInterpolator.DEFAULT);
        this.movingAnimatedHolder = new AnimatedFloat((View) this, 150, (TimeInterpolator) CubicBezierInterpolator.DEFAULT);
        this.touchWasClose = false;
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
            public void setProgress(int progress) {
                SlideChooseView.this.setOption(progress);
            }

            /* access modifiers changed from: protected */
            public int getMaxValue() {
                return SlideChooseView.this.optionsStr.length - 1;
            }

            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(View host) {
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

    public void setOptions(int selected, String... options) {
        this.optionsStr = options;
        this.selectedIndex = selected;
        this.optionsSizes = new int[options.length];
        int i = 0;
        while (true) {
            String[] strArr = this.optionsStr;
            if (i < strArr.length) {
                this.optionsSizes[i] = (int) Math.ceil((double) this.textPaint.measureText(strArr[i]));
                i++;
            } else {
                requestLayout();
                return;
            }
        }
    }

    public void setDashedFrom(int from) {
        this.dashedFrom = from;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int i = this.circleSize;
        float indexTouch = MathUtils.clamp(((x - ((float) this.sideSide)) + (((float) i) / 2.0f)) / ((float) ((this.lineSize + (this.gapSize * 2)) + i)), 0.0f, (float) (this.optionsStr.length - 1));
        boolean isClose = Math.abs(indexTouch - ((float) Math.round(indexTouch))) < 0.35f;
        if (isClose) {
            indexTouch = (float) Math.round(indexTouch);
        }
        if (event.getAction() == 0) {
            this.xTouchDown = x;
            this.yTouchDown = y;
            this.selectedIndexTouch = indexTouch;
            this.startMovingPreset = this.selectedIndex;
            this.startMoving = true;
            invalidate();
        } else if (event.getAction() == 2) {
            if (!this.moving && Math.abs(this.xTouchDown - x) > Math.abs(this.yTouchDown - y)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.startMoving && Math.abs(this.xTouchDown - x) >= ((float) AndroidUtilities.dp(2.0f))) {
                this.moving = true;
                this.startMoving = false;
            }
            if (this.moving) {
                this.selectedIndexTouch = indexTouch;
                invalidate();
                if (Math.round(this.selectedIndexTouch) != this.selectedIndex && isClose) {
                    setOption(Math.round(this.selectedIndexTouch));
                }
            }
            invalidate();
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (!this.moving) {
                this.selectedIndexTouch = indexTouch;
                if (Math.round(indexTouch) != this.selectedIndex) {
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
    public void setOption(int index) {
        if (this.selectedIndex != index) {
            try {
                performHapticFeedback(9, 1);
            } catch (Exception e) {
            }
        }
        this.selectedIndex = index;
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onOptionSelected(index);
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
        this.circleSize = AndroidUtilities.dp(6.0f);
        this.gapSize = AndroidUtilities.dp(2.0f);
        this.sideSide = AndroidUtilities.dp(22.0f);
        int measuredWidth = getMeasuredWidth();
        int i = this.circleSize;
        String[] strArr = this.optionsStr;
        this.lineSize = (((measuredWidth - (i * strArr.length)) - ((this.gapSize * 2) * (strArr.length - 1))) - (this.sideSide * 2)) / (strArr.length - 1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float t;
        Canvas canvas2 = canvas;
        float selectedIndexAnimated = this.selectedIndexAnimatedHolder.set((float) this.selectedIndex);
        float f = 0.0f;
        float f2 = 1.0f;
        float movingAnimated = this.movingAnimatedHolder.set(this.moving ? 1.0f : 0.0f);
        int i = 2;
        int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
        int a = 0;
        while (a < this.optionsStr.length) {
            int i2 = this.sideSide;
            int i3 = this.lineSize + (this.gapSize * 2);
            int i4 = this.circleSize;
            int cx = i2 + ((i3 + i4) * a) + (i4 / i);
            float t2 = Math.max(f, f2 - Math.abs(((float) a) - selectedIndexAnimated));
            float ut = MathUtils.clamp((selectedIndexAnimated - ((float) a)) + f2, f, f2);
            int color = ColorUtils.blendARGB(getThemedColor("switchTrack"), getThemedColor("switchTrackChecked"), ut);
            this.paint.setColor(color);
            this.linePaint.setColor(color);
            canvas2.drawCircle((float) cx, (float) cy, (float) AndroidUtilities.lerp(this.circleSize / i, AndroidUtilities.dp(6.0f), t2), this.paint);
            if (a != 0) {
                int x = ((cx - (this.circleSize / i)) - this.gapSize) - this.lineSize;
                int width = this.lineSize;
                int i5 = this.dashedFrom;
                if (i5 == -1 || a - 1 < i5) {
                    float f3 = ut;
                    t = t2;
                    float nt = MathUtils.clamp(1.0f - Math.abs((((float) a) - selectedIndexAnimated) - 1.0f), 0.0f, 1.0f);
                    int width2 = (int) (((float) width) - (((float) AndroidUtilities.dp(3.0f)) * MathUtils.clamp(1.0f - Math.min(Math.abs(((float) a) - selectedIndexAnimated), Math.abs((((float) a) - selectedIndexAnimated) - 1.0f)), 0.0f, 1.0f)));
                    int x2 = (int) (((float) x) + (((float) AndroidUtilities.dp(3.0f)) * nt));
                    int i6 = x2;
                    int i7 = width2;
                    canvas.drawRect((float) x2, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (x2 + width2), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                } else {
                    int x3 = AndroidUtilities.dp(3.0f) + x;
                    int width3 = width - AndroidUtilities.dp(3.0f);
                    int dash = width3 / AndroidUtilities.dp(13.0f);
                    if (this.lastDash != dash) {
                        float gap = ((float) (width3 - (AndroidUtilities.dp(8.0f) * dash))) / ((float) (dash - 1));
                        Paint paint2 = this.linePaint;
                        int i8 = color;
                        float f4 = ut;
                        float[] fArr = new float[i];
                        fArr[0] = (float) AndroidUtilities.dp(6.0f);
                        fArr[1] = gap;
                        paint2.setPathEffect(new DashPathEffect(fArr, 0.0f));
                        this.lastDash = dash;
                    } else {
                        float f5 = ut;
                    }
                    int i9 = dash;
                    t = t2;
                    canvas.drawLine((float) (AndroidUtilities.dp(1.0f) + x3), (float) cy, (float) ((x3 + width3) - AndroidUtilities.dp(1.0f)), (float) cy, this.linePaint);
                }
            } else {
                float f6 = ut;
                t = t2;
            }
            int size = this.optionsSizes[a];
            String text = this.optionsStr[a];
            this.textPaint.setColor(ColorUtils.blendARGB(getThemedColor("windowBackgroundWhiteGrayText"), getThemedColor("windowBackgroundWhiteBlueText"), t));
            if (a == 0) {
                canvas2.drawText(text, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else if (a == this.optionsStr.length - 1) {
                canvas2.drawText(text, (float) ((getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else {
                canvas2.drawText(text, (float) (cx - (size / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            }
            a++;
            f = 0.0f;
            f2 = 1.0f;
            i = 2;
        }
        int i10 = this.lineSize + (this.gapSize * 2);
        int i11 = this.circleSize;
        float cx2 = ((float) this.sideSide) + (((float) (i10 + i11)) * selectedIndexAnimated) + ((float) (i11 / 2));
        this.paint.setColor(ColorUtils.setAlphaComponent(getThemedColor("switchTrackChecked"), 80));
        canvas2.drawCircle(cx2, (float) cy, (float) AndroidUtilities.dp(12.0f * movingAnimated), this.paint);
        this.paint.setColor(getThemedColor("switchTrackChecked"));
        canvas2.drawCircle(cx2, (float) cy, (float) AndroidUtilities.dp(6.0f), this.paint);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, info);
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return super.performAccessibilityAction(action, arguments) || this.accessibilityDelegate.performAccessibilityActionInternal(this, action, arguments);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public interface Callback {
        void onOptionSelected(int i);

        void onTouchEnd();

        /* renamed from: org.telegram.ui.Components.SlideChooseView$Callback$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onTouchEnd(Callback _this) {
            }
        }
    }
}
