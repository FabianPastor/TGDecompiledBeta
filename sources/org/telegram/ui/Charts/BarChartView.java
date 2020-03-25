package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.BarViewData;

public class BarChartView extends BaseChartView<ChartData, BarViewData> {
    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public float getMinDistance() {
        return 0.1f;
    }

    public BarChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0095  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawChart(android.graphics.Canvas r23) {
        /*
            r22 = this;
            r0 = r22
            r7 = r23
            T r1 = r0.chartData
            if (r1 == 0) goto L_0x01b7
            int r1 = r0.chartWidth
            float r1 = (float) r1
            org.telegram.ui.Charts.ChartPickerDelegate r2 = r0.pickerDelegate
            float r3 = r2.pickerEnd
            float r2 = r2.pickerStart
            float r3 = r3 - r2
            float r8 = r1 / r3
            float r2 = r2 * r8
            int r1 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r1 = (float) r1
            float r9 = r2 - r1
            int r1 = r0.startXIndex
            r10 = 1
            int r1 = r1 - r10
            r11 = 0
            if (r1 >= 0) goto L_0x0024
            r12 = 0
            goto L_0x0025
        L_0x0024:
            r12 = r1
        L_0x0025:
            int r1 = r0.endXIndex
            int r1 = r1 + r10
            T r2 = r0.chartData
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r2 = r2.lines
            java.lang.Object r2 = r2.get(r11)
            org.telegram.ui.Charts.data.ChartData$Line r2 = (org.telegram.ui.Charts.data.ChartData.Line) r2
            int[] r2 = r2.y
            int r2 = r2.length
            int r2 = r2 - r10
            if (r1 <= r2) goto L_0x0046
            T r1 = r0.chartData
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r1 = r1.lines
            java.lang.Object r1 = r1.get(r11)
            org.telegram.ui.Charts.data.ChartData$Line r1 = (org.telegram.ui.Charts.data.ChartData.Line) r1
            int[] r1 = r1.y
            int r1 = r1.length
            int r1 = r1 - r10
        L_0x0046:
            r13 = r1
            r23.save()
            int r1 = r0.chartStart
            int r2 = r0.chartEnd
            int r3 = r22.getMeasuredHeight()
            int r4 = r0.chartBottom
            int r3 = r3 - r4
            r7.clipRect(r1, r11, r2, r3)
            r23.save()
            int r1 = r0.transitionMode
            r14 = 1073741824(0x40000000, float:2.0)
            r15 = 2
            r6 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            if (r1 != r15) goto L_0x007c
            r0.postTransition = r10
            r0.selectionA = r6
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float r2 = r1.progress
            float r3 = r5 - r2
            float r2 = r2 * r14
            float r2 = r2 + r5
            float r4 = r1.pX
            float r1 = r1.pY
            r7.scale(r2, r5, r4, r1)
        L_0x0079:
            r16 = r3
            goto L_0x008c
        L_0x007c:
            if (r1 != r10) goto L_0x008a
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float r3 = r1.progress
            float r2 = r1.pX
            float r1 = r1.pY
            r7.scale(r3, r5, r2, r1)
            goto L_0x0079
        L_0x008a:
            r16 = 1065353216(0x3var_, float:1.0)
        L_0x008c:
            r4 = 0
        L_0x008d:
            java.util.ArrayList<L> r1 = r0.lines
            int r1 = r1.size()
            if (r4 >= r1) goto L_0x01b1
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r4)
            r3 = r1
            org.telegram.ui.Charts.view_data.BarViewData r3 = (org.telegram.ui.Charts.view_data.BarViewData) r3
            boolean r1 = r3.enabled
            if (r1 != 0) goto L_0x00b0
            float r1 = r3.alpha
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00b0
            r17 = r4
            r10 = 0
            r14 = 0
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x01a5
        L_0x00b0:
            T r1 = r0.chartData
            float[] r1 = r1.xPercentage
            int r2 = r1.length
            if (r2 >= r15) goto L_0x00ba
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x00be
        L_0x00ba:
            r1 = r1[r10]
            float r1 = r1 * r8
        L_0x00be:
            org.telegram.ui.Charts.data.ChartData$Line r2 = r3.line
            int[] r2 = r2.y
            float r10 = r3.alpha
            r15 = r12
            r17 = 0
            r18 = 0
            r19 = 0
        L_0x00cb:
            if (r15 > r13) goto L_0x012e
            float r20 = r1 / r14
            T r14 = r0.chartData
            float[] r14 = r14.xPercentage
            r14 = r14[r15]
            float r14 = r14 * r8
            float r20 = r20 + r14
            float r20 = r20 - r9
            r14 = r2[r15]
            float r14 = (float) r14
            float r6 = r0.currentMaxHeight
            float r14 = r14 / r6
            float r14 = r14 * r10
            int r6 = r22.getMeasuredHeight()
            int r5 = r0.chartBottom
            int r6 = r6 - r5
            float r5 = (float) r6
            int r6 = r22.getMeasuredHeight()
            r21 = r2
            int r2 = r0.chartBottom
            int r6 = r6 - r2
            int r2 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r6 = r6 - r2
            float r2 = (float) r6
            float r14 = r14 * r2
            float r5 = r5 - r14
            int r2 = r0.selectedIndex
            if (r15 != r2) goto L_0x010a
            boolean r2 = r0.legendShowing
            if (r2 == 0) goto L_0x010a
            r17 = r5
            r18 = r20
            r19 = 1
            goto L_0x0124
        L_0x010a:
            float[] r2 = r3.linesPath
            int r6 = r11 + 1
            r2[r11] = r20
            int r11 = r6 + 1
            r2[r6] = r5
            int r5 = r11 + 1
            r2[r11] = r20
            int r11 = r5 + 1
            int r6 = r22.getMeasuredHeight()
            int r14 = r0.chartBottom
            int r6 = r6 - r14
            float r6 = (float) r6
            r2[r5] = r6
        L_0x0124:
            int r15 = r15 + 1
            r2 = r21
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r14 = 1073741824(0x40000000, float:2.0)
            goto L_0x00cb
        L_0x012e:
            if (r19 != 0) goto L_0x0138
            boolean r2 = r0.postTransition
            if (r2 == 0) goto L_0x0135
            goto L_0x0138
        L_0x0135:
            android.graphics.Paint r2 = r3.paint
            goto L_0x013a
        L_0x0138:
            android.graphics.Paint r2 = r3.unselectedPaint
        L_0x013a:
            r2.setStrokeWidth(r1)
            if (r19 == 0) goto L_0x0153
            android.graphics.Paint r5 = r3.unselectedPaint
            int r6 = r3.lineColor
            int r10 = r3.blendColor
            float r14 = r0.selectionA
            r15 = 1065353216(0x3var_, float:1.0)
            float r14 = r15 - r14
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r10, r14)
            r5.setColor(r6)
            goto L_0x0155
        L_0x0153:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x0155:
            boolean r5 = r0.postTransition
            if (r5 == 0) goto L_0x0168
            android.graphics.Paint r5 = r3.unselectedPaint
            int r6 = r3.lineColor
            int r10 = r3.blendColor
            r14 = 0
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r10, r14)
            r5.setColor(r6)
            goto L_0x0169
        L_0x0168:
            r14 = 0
        L_0x0169:
            r5 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r16
            int r5 = (int) r5
            r2.setAlpha(r5)
            float[] r6 = r3.linesPath
            r10 = 0
            r7.drawLines(r6, r10, r11, r2)
            if (r19 == 0) goto L_0x01a3
            android.graphics.Paint r2 = r3.paint
            r2.setStrokeWidth(r1)
            android.graphics.Paint r1 = r3.paint
            r1.setAlpha(r5)
            int r1 = r22.getMeasuredHeight()
            int r2 = r0.chartBottom
            int r1 = r1 - r2
            float r5 = (float) r1
            android.graphics.Paint r6 = r3.paint
            r1 = r23
            r2 = r18
            r11 = r3
            r3 = r17
            r17 = r4
            r4 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r11.paint
            r2 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r2)
            goto L_0x01a5
        L_0x01a3:
            r17 = r4
        L_0x01a5:
            int r4 = r17 + 1
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            r10 = 1
            r11 = 0
            r14 = 1073741824(0x40000000, float:2.0)
            r15 = 2
            goto L_0x008d
        L_0x01b1:
            r23.restore()
            r23.restore()
        L_0x01b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BarChartView.drawChart(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        float f;
        int measuredHeight = getMeasuredHeight() - BaseChartView.PICKER_PADDING;
        int measuredHeight2 = (getMeasuredHeight() - this.pikerHeight) - BaseChartView.PICKER_PADDING;
        int size = this.lines.size();
        if (this.chartData != null) {
            for (int i = 0; i < size; i++) {
                BarViewData barViewData = (BarViewData) this.lines.get(i);
                if (barViewData.enabled || barViewData.alpha != 0.0f) {
                    barViewData.bottomLinePath.reset();
                    float[] fArr = this.chartData.xPercentage;
                    int length = fArr.length;
                    float f2 = 1.0f;
                    if (fArr.length < 2) {
                        f = 1.0f;
                    } else {
                        f = fArr[1] * ((float) this.pickerWidth);
                    }
                    int[] iArr = barViewData.line.y;
                    float f3 = barViewData.alpha;
                    int i2 = 0;
                    int i3 = 0;
                    while (i2 < length) {
                        if (iArr[i2] >= 0) {
                            T t = this.chartData;
                            float f4 = t.xPercentage[i2] * ((float) this.pickerWidth);
                            float f5 = (f2 - ((((float) iArr[i2]) / (BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) t.maxValue)) * f3)) * ((float) (measuredHeight - measuredHeight2));
                            float[] fArr2 = barViewData.linesPath;
                            int i4 = i3 + 1;
                            fArr2[i3] = f4;
                            int i5 = i4 + 1;
                            fArr2[i4] = f5;
                            int i6 = i5 + 1;
                            fArr2[i5] = f4;
                            i3 = i6 + 1;
                            fArr2[i6] = (float) (getMeasuredHeight() - this.chartBottom);
                        }
                        i2++;
                        f2 = 1.0f;
                    }
                    barViewData.paint.setStrokeWidth(f + 2.0f);
                    canvas.drawLines(barViewData.linesPath, 0, i3, barViewData.paint);
                } else {
                    Canvas canvas2 = canvas;
                }
            }
        }
    }

    public BarViewData createLineViewData(ChartData.Line line) {
        return new BarViewData(line);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        drawChart(canvas);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            int i2 = this.tmpI;
            if (i2 < this.tmpN) {
                drawHorizontalLines(canvas, this.horizontalLines.get(i2));
                drawSignaturesToHorizontalLines(canvas, this.horizontalLines.get(this.tmpI));
                i = this.tmpI + 1;
            } else {
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
        }
    }
}
