package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SlideChooseView extends View {
    private final SeekBarAccessibilityDelegate accessibilityDelegate;
    private Callback callback;
    private int circleSize;
    private int gapSize;
    private int lineSize;
    private boolean moving;
    private int[] optionsSizes;
    /* access modifiers changed from: private */
    public String[] optionsStr;
    private Paint paint = new Paint(1);
    int selectedIndex;
    private int sideSide;
    private boolean startMoving;
    private int startMovingPreset;
    private float startX;
    private TextPaint textPaint;

    public interface Callback {
        void onOptionSelected(int i);
    }

    public SlideChooseView(Context context) {
        super(context);
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
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
                SlideChooseView slideChooseView = SlideChooseView.this;
                if (slideChooseView.selectedIndex < slideChooseView.optionsStr.length) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r3v1, types: [int] */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r3v5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            float r0 = r9.getX()
            int r1 = r9.getAction()
            r2 = 1097859072(0x41700000, float:15.0)
            r3 = 0
            r4 = 1
            r5 = 2
            if (r1 != 0) goto L_0x0052
            android.view.ViewParent r9 = r8.getParent()
            r9.requestDisallowInterceptTouchEvent(r4)
            r9 = 0
        L_0x0017:
            java.lang.String[] r1 = r8.optionsStr
            int r1 = r1.length
            if (r9 >= r1) goto L_0x00fd
            int r1 = r8.sideSide
            int r6 = r8.lineSize
            int r7 = r8.gapSize
            int r7 = r7 * 2
            int r6 = r6 + r7
            int r7 = r8.circleSize
            int r6 = r6 + r7
            int r6 = r6 * r9
            int r1 = r1 + r6
            int r7 = r7 / r5
            int r1 = r1 + r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r1 - r6
            float r6 = (float) r6
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x004f
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r6
            float r1 = (float) r1
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x004f
            int r1 = r8.selectedIndex
            if (r9 != r1) goto L_0x0047
            r3 = 1
        L_0x0047:
            r8.startMoving = r3
            r8.startX = r0
            r8.startMovingPreset = r1
            goto L_0x00fd
        L_0x004f:
            int r9 = r9 + 1
            goto L_0x0017
        L_0x0052:
            int r1 = r9.getAction()
            if (r1 != r5) goto L_0x00aa
            boolean r9 = r8.startMoving
            if (r9 == 0) goto L_0x0073
            float r9 = r8.startX
            float r9 = r9 - r0
            float r9 = java.lang.Math.abs(r9)
            r0 = 1056964608(0x3var_, float:0.5)
            float r0 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r0, r4)
            int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r9 < 0) goto L_0x00fd
            r8.moving = r4
            r8.startMoving = r3
            goto L_0x00fd
        L_0x0073:
            boolean r9 = r8.moving
            if (r9 == 0) goto L_0x00fd
        L_0x0077:
            java.lang.String[] r9 = r8.optionsStr
            int r9 = r9.length
            if (r3 >= r9) goto L_0x00fd
            int r9 = r8.sideSide
            int r1 = r8.lineSize
            int r2 = r8.gapSize
            int r6 = r2 * 2
            int r6 = r6 + r1
            int r7 = r8.circleSize
            int r6 = r6 + r7
            int r6 = r6 * r3
            int r9 = r9 + r6
            int r6 = r7 / 2
            int r9 = r9 + r6
            int r1 = r1 / r5
            int r7 = r7 / r5
            int r1 = r1 + r7
            int r1 = r1 + r2
            int r2 = r9 - r1
            float r2 = (float) r2
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x00a7
            int r9 = r9 + r1
            float r9 = (float) r9
            int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x00a7
            int r9 = r8.selectedIndex
            if (r9 == r3) goto L_0x00fd
            r8.setOption(r3)
            goto L_0x00fd
        L_0x00a7:
            int r3 = r3 + 1
            goto L_0x0077
        L_0x00aa:
            int r1 = r9.getAction()
            if (r1 == r4) goto L_0x00b7
            int r9 = r9.getAction()
            r1 = 3
            if (r9 != r1) goto L_0x00fd
        L_0x00b7:
            boolean r9 = r8.moving
            if (r9 != 0) goto L_0x00f0
            r9 = 0
        L_0x00bc:
            r1 = 5
            if (r9 >= r1) goto L_0x00f9
            int r1 = r8.sideSide
            int r6 = r8.lineSize
            int r7 = r8.gapSize
            int r7 = r7 * 2
            int r6 = r6 + r7
            int r7 = r8.circleSize
            int r6 = r6 + r7
            int r6 = r6 * r9
            int r1 = r1 + r6
            int r7 = r7 / r5
            int r1 = r1 + r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r1 - r6
            float r6 = (float) r6
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x00ed
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r6
            float r1 = (float) r1
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x00ed
            int r0 = r8.selectedIndex
            if (r0 == r9) goto L_0x00f9
            r8.setOption(r9)
            goto L_0x00f9
        L_0x00ed:
            int r9 = r9 + 1
            goto L_0x00bc
        L_0x00f0:
            int r9 = r8.selectedIndex
            int r0 = r8.startMovingPreset
            if (r9 == r0) goto L_0x00f9
            r8.setOption(r9)
        L_0x00f9:
            r8.startMoving = r3
            r8.moving = r3
        L_0x00fd:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SlideChooseView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: private */
    public void setOption(int i) {
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
        View.MeasureSpec.getSize(i);
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
        this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
        int i = 0;
        while (i < this.optionsStr.length) {
            int i2 = this.sideSide;
            int i3 = this.lineSize + (this.gapSize * 2);
            int i4 = this.circleSize;
            int i5 = i2 + ((i3 + i4) * i) + (i4 / 2);
            if (i <= this.selectedIndex) {
                this.paint.setColor(Theme.getColor("switchTrackChecked"));
            } else {
                this.paint.setColor(Theme.getColor("switchTrack"));
            }
            canvas.drawCircle((float) i5, (float) measuredHeight, (float) (i == this.selectedIndex ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
            if (i != 0) {
                int i6 = (i5 - (this.circleSize / 2)) - this.gapSize;
                int i7 = this.lineSize;
                int i8 = i6 - i7;
                int i9 = this.selectedIndex;
                if (i == i9 || i == i9 + 1) {
                    i7 -= AndroidUtilities.dp(3.0f);
                }
                if (i == this.selectedIndex + 1) {
                    i8 += AndroidUtilities.dp(3.0f);
                }
                canvas.drawRect((float) i8, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i8 + i7), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
            }
            int i10 = this.optionsSizes[i];
            String[] strArr = this.optionsStr;
            String str = strArr[i];
            if (i == 0) {
                canvas.drawText(str, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else if (i == strArr.length - 1) {
                canvas.drawText(str, (float) ((getMeasuredWidth() - i10) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            } else {
                canvas.drawText(str, (float) (i5 - (i10 / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
            }
            i++;
        }
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
}
