package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.LineViewData;

public class LinearChartView extends BaseChartView<ChartData, LineViewData> {
    public LinearChartView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.useMinHeight = true;
        super.init();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0176  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawChart(android.graphics.Canvas r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            T r2 = r0.chartData
            if (r2 == 0) goto L_0x0188
            int r2 = r0.chartWidth
            float r2 = (float) r2
            org.telegram.ui.Charts.ChartPickerDelegate r3 = r0.pickerDelegate
            float r4 = r3.pickerEnd
            float r3 = r3.pickerStart
            float r4 = r4 - r3
            float r2 = r2 / r4
            float r3 = r3 * r2
            int r4 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r4 = (float) r4
            float r3 = r3 - r4
            r4 = 0
            r5 = 0
        L_0x001b:
            java.util.ArrayList<L> r6 = r0.lines
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x0188
            java.util.ArrayList<L> r6 = r0.lines
            java.lang.Object r6 = r6.get(r5)
            org.telegram.ui.Charts.view_data.LineViewData r6 = (org.telegram.ui.Charts.view_data.LineViewData) r6
            boolean r7 = r6.enabled
            r8 = 0
            if (r7 != 0) goto L_0x003b
            float r7 = r6.alpha
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 != 0) goto L_0x003b
            r17 = r2
            r6 = 0
            goto L_0x0181
        L_0x003b:
            T r7 = r0.chartData
            float[] r7 = r7.xPercentage
            int r9 = r7.length
            r10 = 2
            r11 = 1
            if (r9 >= r10) goto L_0x0046
            r7 = 0
            goto L_0x004a
        L_0x0046:
            r7 = r7[r11]
            float r7 = r7 * r2
        L_0x004a:
            org.telegram.ui.Charts.data.ChartData$Line r9 = r6.line
            int[] r9 = r9.y
            int r12 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r12 = (float) r12
            float r12 = r12 / r7
            int r7 = (int) r12
            int r7 = r7 + r11
            android.graphics.Path r12 = r6.chartPath
            r12.reset()
            int r12 = r0.startXIndex
            int r12 = r12 - r7
            int r12 = java.lang.Math.max(r4, r12)
            T r13 = r0.chartData
            float[] r13 = r13.xPercentage
            int r13 = r13.length
            int r13 = r13 - r11
            int r14 = r0.endXIndex
            int r14 = r14 + r7
            int r7 = java.lang.Math.min(r13, r14)
            r13 = 1
            r14 = 0
        L_0x006f:
            r15 = 1073741824(0x40000000, float:2.0)
            if (r12 > r7) goto L_0x00e9
            r16 = r9[r12]
            if (r16 >= 0) goto L_0x007a
            r17 = r2
            goto L_0x00e0
        L_0x007a:
            T r8 = r0.chartData
            float[] r8 = r8.xPercentage
            r8 = r8[r12]
            float r8 = r8 * r2
            float r8 = r8 - r3
            r4 = r9[r12]
            float r4 = (float) r4
            float r11 = r0.currentMinHeight
            float r4 = r4 - r11
            float r10 = r0.currentMaxHeight
            float r10 = r10 - r11
            float r4 = r4 / r10
            android.graphics.Paint r10 = r6.paint
            float r10 = r10.getStrokeWidth()
            float r10 = r10 / r15
            int r11 = r18.getMeasuredHeight()
            int r15 = r0.chartBottom
            int r11 = r11 - r15
            float r11 = (float) r11
            float r11 = r11 - r10
            int r15 = r18.getMeasuredHeight()
            r17 = r2
            int r2 = r0.chartBottom
            int r15 = r15 - r2
            int r2 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r15 = r15 - r2
            float r2 = (float) r15
            float r2 = r2 - r10
            float r4 = r4 * r2
            float r11 = r11 - r4
            boolean r2 = org.telegram.ui.Charts.BaseChartView.USE_LINES
            if (r2 == 0) goto L_0x00d2
            if (r14 != 0) goto L_0x00bf
            float[] r2 = r6.linesPath
            int r4 = r14 + 1
            r2[r14] = r8
            int r14 = r4 + 1
            r2[r4] = r11
            goto L_0x00e0
        L_0x00bf:
            float[] r2 = r6.linesPath
            int r4 = r14 + 1
            r2[r14] = r8
            int r10 = r4 + 1
            r2[r4] = r11
            int r4 = r10 + 1
            r2[r10] = r8
            int r14 = r4 + 1
            r2[r4] = r11
            goto L_0x00e0
        L_0x00d2:
            if (r13 == 0) goto L_0x00db
            android.graphics.Path r2 = r6.chartPath
            r2.moveTo(r8, r11)
            r13 = 0
            goto L_0x00e0
        L_0x00db:
            android.graphics.Path r2 = r6.chartPath
            r2.lineTo(r8, r11)
        L_0x00e0:
            int r12 = r12 + 1
            r2 = r17
            r4 = 0
            r8 = 0
            r10 = 2
            r11 = 1
            goto L_0x006f
        L_0x00e9:
            r17 = r2
            r19.save()
            int r2 = r0.transitionMode
            r4 = 1065353216(0x3var_, float:1.0)
            r7 = 2
            if (r2 != r7) goto L_0x0115
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r2 = r2.progress
            r7 = 1056964608(0x3var_, float:0.5)
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x0101
            r8 = 0
            goto L_0x0105
        L_0x0101:
            float r2 = r2 * r15
            float r8 = r4 - r2
        L_0x0105:
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r7 = r2.progress
            float r7 = r7 * r15
            float r7 = r7 + r4
            float r9 = r2.pX
            float r2 = r2.pY
            r1.scale(r7, r4, r9, r2)
        L_0x0113:
            r4 = r8
            goto L_0x0143
        L_0x0115:
            r7 = 1
            if (r2 != r7) goto L_0x013c
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r2 = r2.progress
            r7 = 1050253722(0x3e99999a, float:0.3)
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0125
            r8 = 0
            goto L_0x0126
        L_0x0125:
            r8 = r2
        L_0x0126:
            r19.save()
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r7 = r2.progress
            boolean r2 = r2.needScaleY
            if (r2 == 0) goto L_0x0132
            r4 = r7
        L_0x0132:
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r9 = r2.pX
            float r2 = r2.pY
            r1.scale(r7, r4, r9, r2)
            goto L_0x0113
        L_0x013c:
            r7 = 3
            if (r2 != r7) goto L_0x0143
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r4 = r2.progress
        L_0x0143:
            android.graphics.Paint r2 = r6.paint
            r7 = 1132396544(0x437var_, float:255.0)
            float r8 = r6.alpha
            float r8 = r8 * r7
            float r8 = r8 * r4
            int r4 = (int) r8
            r2.setAlpha(r4)
            int r2 = r0.endXIndex
            int r4 = r0.startXIndex
            int r2 = r2 - r4
            r4 = 100
            if (r2 <= r4) goto L_0x0162
            android.graphics.Paint r2 = r6.paint
            android.graphics.Paint$Cap r4 = android.graphics.Paint.Cap.SQUARE
            r2.setStrokeCap(r4)
            goto L_0x0169
        L_0x0162:
            android.graphics.Paint r2 = r6.paint
            android.graphics.Paint$Cap r4 = android.graphics.Paint.Cap.ROUND
            r2.setStrokeCap(r4)
        L_0x0169:
            boolean r2 = org.telegram.ui.Charts.BaseChartView.USE_LINES
            if (r2 != 0) goto L_0x0176
            android.graphics.Path r2 = r6.chartPath
            android.graphics.Paint r4 = r6.paint
            r1.drawPath(r2, r4)
            r6 = 0
            goto L_0x017e
        L_0x0176:
            float[] r2 = r6.linesPath
            android.graphics.Paint r4 = r6.paint
            r6 = 0
            r1.drawLines(r2, r6, r14, r4)
        L_0x017e:
            r19.restore()
        L_0x0181:
            int r5 = r5 + 1
            r2 = r17
            r4 = 0
            goto L_0x001b
        L_0x0188:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.LinearChartView.drawChart(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        getMeasuredHeight();
        getMeasuredHeight();
        int size = this.lines.size();
        if (this.chartData != null) {
            for (int i = 0; i < size; i++) {
                LineViewData lineViewData = (LineViewData) this.lines.get(i);
                if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                    lineViewData.bottomLinePath.reset();
                    int length = this.chartData.xPercentage.length;
                    int[] iArr = lineViewData.line.y;
                    lineViewData.chartPath.reset();
                    int i2 = 0;
                    for (int i3 = 0; i3 < length; i3++) {
                        if (iArr[i3] >= 0) {
                            T t = this.chartData;
                            float f = t.xPercentage[i3] * ((float) this.pickerWidth);
                            float f2 = BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) t.maxValue;
                            float f3 = BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMinHeight : (float) this.chartData.minValue;
                            float f4 = (1.0f - ((((float) iArr[i3]) - f3) / (f2 - f3))) * ((float) this.pikerHeight);
                            if (BaseChartView.USE_LINES) {
                                if (i2 == 0) {
                                    float[] fArr = lineViewData.linesPathBottom;
                                    int i4 = i2 + 1;
                                    fArr[i2] = f;
                                    i2 = i4 + 1;
                                    fArr[i4] = f4;
                                } else {
                                    float[] fArr2 = lineViewData.linesPathBottom;
                                    int i5 = i2 + 1;
                                    fArr2[i2] = f;
                                    int i6 = i5 + 1;
                                    fArr2[i5] = f4;
                                    int i7 = i6 + 1;
                                    fArr2[i6] = f;
                                    i2 = i7 + 1;
                                    fArr2[i7] = f4;
                                }
                            } else if (i3 == 0) {
                                lineViewData.bottomLinePath.moveTo(f, f4);
                            } else {
                                lineViewData.bottomLinePath.lineTo(f, f4);
                            }
                        }
                    }
                    lineViewData.linesPathBottomSize = i2;
                    if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                        lineViewData.bottomLinePaint.setAlpha((int) (lineViewData.alpha * 255.0f));
                        if (BaseChartView.USE_LINES) {
                            canvas.drawLines(lineViewData.linesPathBottom, 0, lineViewData.linesPathBottomSize, lineViewData.bottomLinePaint);
                        } else {
                            canvas.drawPath(lineViewData.bottomLinePath, lineViewData.bottomLinePaint);
                        }
                    }
                }
            }
        }
    }

    public LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }
}
