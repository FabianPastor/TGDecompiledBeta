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
    private int[] optionsSizes;
    /* access modifiers changed from: private */
    public String[] optionsStr;
    private Paint paint;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int selectedIndex;
    private int sideSide;
    private boolean startMoving;
    private int startMovingPreset;
    private float startX;
    private TextPaint textPaint;
    private float xTouchDown;
    private float yTouchDown;

    public SlideChooseView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public SlideChooseView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.dashedFrom = -1;
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
        boolean z = false;
        if (event.getAction() == 0) {
            this.xTouchDown = x;
            this.yTouchDown = y;
            int a = 0;
            while (true) {
                if (a >= this.optionsStr.length) {
                    break;
                }
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                    a++;
                } else {
                    int i4 = this.selectedIndex;
                    if (a == i4) {
                        z = true;
                    }
                    this.startMoving = z;
                    this.startX = x;
                    this.startMovingPreset = i4;
                }
            }
        } else if (event.getAction() == 2) {
            if (!this.moving && Math.abs(this.xTouchDown - x) > Math.abs(this.yTouchDown - y)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.startMoving) {
                if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                    this.moving = true;
                    this.startMoving = false;
                }
            } else if (this.moving) {
                int a2 = 0;
                while (true) {
                    if (a2 >= this.optionsStr.length) {
                        break;
                    }
                    int i5 = this.sideSide;
                    int i6 = this.lineSize;
                    int i7 = this.gapSize;
                    int i8 = this.circleSize;
                    int cx2 = i5 + (((i7 * 2) + i6 + i8) * a2) + (i8 / 2);
                    int diff = (i6 / 2) + (i8 / 2) + i7;
                    if (x <= ((float) (cx2 - diff)) || x >= ((float) (cx2 + diff))) {
                        a2++;
                    } else if (this.selectedIndex != a2) {
                        setOption(a2);
                    }
                }
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (!this.moving) {
                int a3 = 0;
                while (true) {
                    if (a3 >= 5) {
                        break;
                    }
                    int i9 = this.sideSide;
                    int i10 = this.lineSize + (this.gapSize * 2);
                    int i11 = this.circleSize;
                    int cx3 = i9 + ((i10 + i11) * a3) + (i11 / 2);
                    if (x <= ((float) (cx3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx3))) {
                        a3++;
                    } else if (this.selectedIndex != a3) {
                        setOption(a3);
                    }
                }
            } else {
                int i12 = this.selectedIndex;
                if (i12 != this.startMovingPreset) {
                    setOption(i12);
                }
            }
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onTouchEnd();
            }
            this.startMoving = false;
            this.moving = false;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void setOption(int index) {
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
        int width;
        int x;
        Canvas canvas2 = canvas;
        this.textPaint.setColor(getThemedColor("windowBackgroundWhiteGrayText"));
        int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
        int a = 0;
        while (a < this.optionsStr.length) {
            int i = this.sideSide;
            int i2 = this.lineSize + (this.gapSize * 2);
            int i3 = this.circleSize;
            int cx = i + ((i2 + i3) * a) + (i3 / 2);
            int color = getThemedColor(a <= this.selectedIndex ? "switchTrackChecked" : "switchTrack");
            this.paint.setColor(color);
            this.linePaint.setColor(color);
            canvas2.drawCircle((float) cx, (float) cy, (float) (a == this.selectedIndex ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
            if (a != 0) {
                int x2 = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                int width2 = this.lineSize;
                int i4 = this.dashedFrom;
                if (i4 == -1 || a - 1 < i4) {
                    int i5 = this.selectedIndex;
                    if (a == i5 || a == i5 + 1) {
                        width = width2 - AndroidUtilities.dp(3.0f);
                    } else {
                        width = width2;
                    }
                    if (a == this.selectedIndex + 1) {
                        x = x2 + AndroidUtilities.dp(3.0f);
                    } else {
                        x = x2;
                    }
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (x + width), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                } else {
                    int x3 = x2 + AndroidUtilities.dp(3.0f);
                    int width3 = width2 - AndroidUtilities.dp(3.0f);
                    int dash = width3 / AndroidUtilities.dp(13.0f);
                    if (this.lastDash != dash) {
                        this.linePaint.setPathEffect(new DashPathEffect(new float[]{(float) AndroidUtilities.dp(6.0f), ((float) (width3 - (AndroidUtilities.dp(8.0f) * dash))) / ((float) (dash - 1))}, 0.0f));
                        this.lastDash = dash;
                    }
                    int i6 = dash;
                    canvas.drawLine((float) (AndroidUtilities.dp(1.0f) + x3), (float) cy, (float) ((x3 + width3) - AndroidUtilities.dp(1.0f)), (float) cy, this.linePaint);
                }
            }
            int size = this.optionsSizes[a];
            String[] strArr = this.optionsStr;
            String text = strArr[a];
            if (a == 0) {
                canvas2.drawText(text, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else if (a == strArr.length - 1) {
                canvas2.drawText(text, (float) ((getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else {
                canvas2.drawText(text, (float) (cx - (size / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            }
            a++;
        }
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
