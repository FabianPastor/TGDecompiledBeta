package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.StackLinearViewData;

public class StackLinearChartView<T extends StackLinearViewData> extends BaseChartView<StackLinearChartData, T> {
    Path ovalPath = new Path();
    boolean[] skipPoints;

    public int findMaxValue(int i, int i2) {
        return 100;
    }

    /* access modifiers changed from: protected */
    public float getMinDistance() {
        return 0.1f;
    }

    public StackLinearChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
        this.drawPointOnSelection = false;
    }

    public T createLineViewData(ChartData.Line line) {
        return new StackLinearViewData(line);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0211 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0241  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x025a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawChart(android.graphics.Canvas r25) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            T r2 = r0.chartData
            if (r2 == 0) goto L_0x02d2
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
            if (r5 >= r6) goto L_0x0040
            java.util.ArrayList<L> r6 = r0.lines
            java.lang.Object r6 = r6.get(r5)
            org.telegram.ui.Charts.view_data.StackLinearViewData r6 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r6
            android.graphics.Path r6 = r6.chartPath
            r6.reset()
            java.util.ArrayList<L> r6 = r0.lines
            java.lang.Object r6 = r6.get(r5)
            org.telegram.ui.Charts.view_data.StackLinearViewData r6 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r6
            android.graphics.Path r6 = r6.chartPathPicker
            r6.reset()
            int r5 = r5 + 1
            goto L_0x001b
        L_0x0040:
            r25.save()
            boolean[] r5 = r0.skipPoints
            if (r5 == 0) goto L_0x0054
            int r5 = r5.length
            T r6 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r6 = (org.telegram.ui.Charts.data.StackLinearChartData) r6
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r6 = r6.lines
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x0062
        L_0x0054:
            T r5 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r5 = (org.telegram.ui.Charts.data.StackLinearChartData) r5
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r5 = r5.lines
            int r5 = r5.size()
            boolean[] r5 = new boolean[r5]
            r0.skipPoints = r5
        L_0x0062:
            int r5 = r0.transitionMode
            r6 = 1132396544(0x437var_, float:255.0)
            r8 = 2
            r9 = 1065353216(0x3var_, float:1.0)
            if (r5 != r8) goto L_0x00fa
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float r5 = r5.progress
            float r5 = r9 - r5
            float r5 = r5 * r6
            int r5 = (int) r5
            android.graphics.Path r6 = r0.ovalPath
            r6.reset()
            android.graphics.Rect r6 = r0.chartArea
            int r6 = r6.width()
            android.graphics.Rect r10 = r0.chartArea
            int r10 = r10.height()
            if (r6 <= r10) goto L_0x008e
            android.graphics.Rect r6 = r0.chartArea
            int r6 = r6.width()
            goto L_0x0094
        L_0x008e:
            android.graphics.Rect r6 = r0.chartArea
            int r6 = r6.height()
        L_0x0094:
            android.graphics.Rect r10 = r0.chartArea
            int r10 = r10.width()
            android.graphics.Rect r11 = r0.chartArea
            int r11 = r11.height()
            if (r10 <= r11) goto L_0x00a9
            android.graphics.Rect r10 = r0.chartArea
            int r10 = r10.height()
            goto L_0x00af
        L_0x00a9:
            android.graphics.Rect r10 = r0.chartArea
            int r10 = r10.width()
        L_0x00af:
            float r10 = (float) r10
            r11 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 / r11
            int r10 = (int) r10
            float r11 = (float) r10
            int r6 = r6 - r10
            int r6 = r6 / r8
            float r6 = (float) r6
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.progress
            float r12 = r9 - r10
            float r6 = r6 * r12
            float r11 = r11 + r6
            float r6 = r9 - r10
            float r11 = r11 * r6
            android.graphics.RectF r6 = new android.graphics.RectF
            r6.<init>()
            android.graphics.Rect r10 = r0.chartArea
            int r10 = r10.centerX()
            float r10 = (float) r10
            float r10 = r10 - r11
            android.graphics.Rect r12 = r0.chartArea
            int r12 = r12.centerY()
            float r12 = (float) r12
            float r12 = r12 - r11
            android.graphics.Rect r13 = r0.chartArea
            int r13 = r13.centerX()
            float r13 = (float) r13
            float r13 = r13 + r11
            android.graphics.Rect r14 = r0.chartArea
            int r14 = r14.centerY()
            float r14 = (float) r14
            float r14 = r14 + r11
            r6.set(r10, r12, r13, r14)
            android.graphics.Path r10 = r0.ovalPath
            android.graphics.Path$Direction r12 = android.graphics.Path.Direction.CW
            r10.addRoundRect(r6, r11, r11, r12)
            android.graphics.Path r6 = r0.ovalPath
            r1.clipPath(r6)
            goto L_0x0107
        L_0x00fa:
            r10 = 3
            if (r5 != r10) goto L_0x0105
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float r5 = r5.progress
            float r5 = r5 * r6
            int r5 = (int) r5
            goto L_0x0107
        L_0x0105:
            r5 = 255(0xff, float:3.57E-43)
        L_0x0107:
            T r6 = r0.chartData
            r10 = r6
            org.telegram.ui.Charts.data.StackLinearChartData r10 = (org.telegram.ui.Charts.data.StackLinearChartData) r10
            float[] r10 = r10.xPercentage
            int r10 = r10.length
            r11 = 1
            if (r10 >= r8) goto L_0x0113
            goto L_0x011b
        L_0x0113:
            org.telegram.ui.Charts.data.StackLinearChartData r6 = (org.telegram.ui.Charts.data.StackLinearChartData) r6
            float[] r6 = r6.xPercentage
            r6 = r6[r11]
            float r9 = r6 * r2
        L_0x011b:
            int r6 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r6 = (float) r6
            float r6 = r6 / r9
            int r6 = (int) r6
            int r6 = r6 + r11
            int r8 = r0.startXIndex
            int r8 = r8 - r6
            int r8 = r8 - r11
            int r8 = java.lang.Math.max(r4, r8)
            T r9 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r9 = (org.telegram.ui.Charts.data.StackLinearChartData) r9
            float[] r9 = r9.xPercentage
            int r9 = r9.length
            int r9 = r9 - r11
            int r10 = r0.endXIndex
            int r10 = r10 + r6
            int r10 = r10 + r11
            int r6 = java.lang.Math.min(r9, r10)
            r9 = 0
            r10 = r8
            r12 = 0
            r13 = 0
        L_0x013d:
            if (r10 > r6) goto L_0x028f
            T r14 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r14 = (org.telegram.ui.Charts.data.StackLinearChartData) r14
            float[] r14 = r14.xPercentage
            r14 = r14[r10]
            float r14 = r14 * r2
            float r14 = r14 - r3
            r7 = 0
            r15 = 0
            r16 = 0
        L_0x014e:
            java.util.ArrayList<L> r4 = r0.lines
            int r4 = r4.size()
            if (r15 >= r4) goto L_0x0180
            java.util.ArrayList<L> r4 = r0.lines
            java.lang.Object r4 = r4.get(r15)
            org.telegram.ui.Charts.view_data.LineViewData r4 = (org.telegram.ui.Charts.view_data.LineViewData) r4
            boolean r11 = r4.enabled
            if (r11 != 0) goto L_0x0169
            float r11 = r4.alpha
            int r11 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x0169
            goto L_0x017c
        L_0x0169:
            org.telegram.ui.Charts.data.ChartData$Line r11 = r4.line
            int[] r11 = r11.y
            r17 = r11[r10]
            if (r17 <= 0) goto L_0x017c
            r11 = r11[r10]
            float r11 = (float) r11
            float r4 = r4.alpha
            float r11 = r11 * r4
            float r16 = r16 + r11
            int r7 = r7 + 1
        L_0x017c:
            int r15 = r15 + 1
            r11 = 1
            goto L_0x014e
        L_0x0180:
            r4 = 0
            r11 = 0
        L_0x0182:
            java.util.ArrayList<L> r15 = r0.lines
            int r15 = r15.size()
            if (r4 >= r15) goto L_0x027a
            java.util.ArrayList<L> r15 = r0.lines
            java.lang.Object r15 = r15.get(r4)
            org.telegram.ui.Charts.view_data.LineViewData r15 = (org.telegram.ui.Charts.view_data.LineViewData) r15
            boolean r9 = r15.enabled
            if (r9 != 0) goto L_0x01ac
            float r9 = r15.alpha
            r17 = 0
            int r9 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r9 != 0) goto L_0x01ac
            r18 = r2
            r19 = r3
            r23 = r5
            r20 = r7
            r22 = r8
            r5 = 0
            r7 = 0
            goto L_0x026b
        L_0x01ac:
            org.telegram.ui.Charts.data.ChartData$Line r9 = r15.line
            int[] r9 = r9.y
            r18 = r2
            r2 = 1
            if (r7 != r2) goto L_0x01bf
            r2 = r9[r10]
            if (r2 != 0) goto L_0x01ba
            goto L_0x01c4
        L_0x01ba:
            float r2 = r15.alpha
            r19 = r3
            goto L_0x01d3
        L_0x01bf:
            r2 = 0
            int r19 = (r16 > r2 ? 1 : (r16 == r2 ? 0 : -1))
            if (r19 != 0) goto L_0x01c8
        L_0x01c4:
            r19 = r3
            r2 = 0
            goto L_0x01d3
        L_0x01c8:
            r2 = r9[r10]
            float r2 = (float) r2
            r19 = r3
            float r3 = r15.alpha
            float r2 = r2 * r3
            float r2 = r2 / r16
        L_0x01d3:
            int r3 = r24.getMeasuredHeight()
            r20 = r7
            int r7 = r0.chartBottom
            int r3 = r3 - r7
            int r7 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r3 = r3 - r7
            float r3 = (float) r3
            float r3 = r3 * r2
            int r7 = r24.getMeasuredHeight()
            r21 = r12
            int r12 = r0.chartBottom
            int r7 = r7 - r12
            float r7 = (float) r7
            float r7 = r7 - r3
            float r7 = r7 - r11
            if (r10 != r8) goto L_0x0206
            android.graphics.Path r12 = r15.chartPath
            r22 = r8
            int r8 = r24.getMeasuredHeight()
            float r8 = (float) r8
            r23 = r5
            r5 = 0
            r12.moveTo(r5, r8)
            boolean[] r8 = r0.skipPoints
            r12 = 0
            r8[r4] = r12
            r12 = r14
            goto L_0x020d
        L_0x0206:
            r23 = r5
            r22 = r8
            r5 = 0
            r12 = r21
        L_0x020d:
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 != 0) goto L_0x023b
            if (r10 <= 0) goto L_0x023b
            int r2 = r10 + -1
            r2 = r9[r2]
            if (r2 != 0) goto L_0x023b
            if (r10 >= r6) goto L_0x023b
            int r2 = r10 + 1
            r2 = r9[r2]
            if (r2 != 0) goto L_0x023b
            boolean[] r2 = r0.skipPoints
            boolean r2 = r2[r4]
            if (r2 != 0) goto L_0x0234
            android.graphics.Path r2 = r15.chartPath
            int r7 = r24.getMeasuredHeight()
            int r8 = r0.chartBottom
            int r7 = r7 - r8
            float r7 = (float) r7
            r2.lineTo(r14, r7)
        L_0x0234:
            boolean[] r2 = r0.skipPoints
            r7 = 1
            r2[r4] = r7
            r7 = 0
            goto L_0x0258
        L_0x023b:
            boolean[] r2 = r0.skipPoints
            boolean r2 = r2[r4]
            if (r2 == 0) goto L_0x024e
            android.graphics.Path r2 = r15.chartPath
            int r8 = r24.getMeasuredHeight()
            int r9 = r0.chartBottom
            int r8 = r8 - r9
            float r8 = (float) r8
            r2.lineTo(r14, r8)
        L_0x024e:
            android.graphics.Path r2 = r15.chartPath
            r2.lineTo(r14, r7)
            boolean[] r2 = r0.skipPoints
            r7 = 0
            r2[r4] = r7
        L_0x0258:
            if (r10 != r6) goto L_0x026a
            android.graphics.Path r2 = r15.chartPath
            int r8 = r24.getMeasuredWidth()
            float r8 = (float) r8
            int r9 = r24.getMeasuredHeight()
            float r9 = (float) r9
            r2.lineTo(r8, r9)
            r13 = r14
        L_0x026a:
            float r11 = r11 + r3
        L_0x026b:
            int r4 = r4 + 1
            r2 = r18
            r3 = r19
            r7 = r20
            r8 = r22
            r5 = r23
            r9 = 0
            goto L_0x0182
        L_0x027a:
            r18 = r2
            r19 = r3
            r23 = r5
            r22 = r8
            r21 = r12
            r5 = 0
            r7 = 0
            int r10 = r10 + 1
            r5 = r23
            r4 = 0
            r9 = 0
            r11 = 1
            goto L_0x013d
        L_0x028f:
            r23 = r5
            r25.save()
            int r2 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            float r2 = (float) r2
            int r3 = r24.getMeasuredHeight()
            int r4 = r0.chartBottom
            int r3 = r3 - r4
            float r3 = (float) r3
            r1.clipRect(r12, r2, r13, r3)
            java.util.ArrayList<L> r2 = r0.lines
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
        L_0x02aa:
            if (r2 < 0) goto L_0x02cc
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.Charts.view_data.LineViewData r3 = (org.telegram.ui.Charts.view_data.LineViewData) r3
            android.graphics.Paint r4 = r3.paint
            r5 = r23
            r4.setAlpha(r5)
            android.graphics.Path r4 = r3.chartPath
            android.graphics.Paint r6 = r3.paint
            r1.drawPath(r4, r6)
            android.graphics.Paint r3 = r3.paint
            r4 = 255(0xff, float:3.57E-43)
            r3.setAlpha(r4)
            int r2 = r2 + -1
            goto L_0x02aa
        L_0x02cc:
            r25.restore()
            r25.restore()
        L_0x02d2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawChart(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0142  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawPickerChart(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            T r1 = r0.chartData
            if (r1 == 0) goto L_0x0175
            java.util.ArrayList<L> r1 = r0.lines
            int r1 = r1.size()
            r2 = 0
            r3 = 0
        L_0x000e:
            if (r3 >= r1) goto L_0x0020
            java.util.ArrayList<L> r4 = r0.lines
            java.lang.Object r4 = r4.get(r3)
            org.telegram.ui.Charts.view_data.StackLinearViewData r4 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r4
            android.graphics.Path r4 = r4.chartPathPicker
            r4.reset()
            int r3 = r3 + 1
            goto L_0x000e
        L_0x0020:
            T r1 = r0.chartData
            r3 = r1
            org.telegram.ui.Charts.data.StackLinearChartData r3 = (org.telegram.ui.Charts.data.StackLinearChartData) r3
            int r3 = r3.simplifiedSize
            boolean[] r4 = r0.skipPoints
            if (r4 == 0) goto L_0x0036
            int r4 = r4.length
            org.telegram.ui.Charts.data.StackLinearChartData r1 = (org.telegram.ui.Charts.data.StackLinearChartData) r1
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r1 = r1.lines
            int r1 = r1.size()
            if (r4 >= r1) goto L_0x0044
        L_0x0036:
            T r1 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r1 = (org.telegram.ui.Charts.data.StackLinearChartData) r1
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r1 = r1.lines
            int r1 = r1.size()
            boolean[] r1 = new boolean[r1]
            r0.skipPoints = r1
        L_0x0044:
            r1 = 0
        L_0x0045:
            r4 = 1
            if (r1 >= r3) goto L_0x0157
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
        L_0x004c:
            java.util.ArrayList<L> r9 = r0.lines
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x0087
            java.util.ArrayList<L> r9 = r0.lines
            java.lang.Object r9 = r9.get(r6)
            org.telegram.ui.Charts.view_data.LineViewData r9 = (org.telegram.ui.Charts.view_data.LineViewData) r9
            boolean r10 = r9.enabled
            if (r10 != 0) goto L_0x0067
            float r10 = r9.alpha
            int r10 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r10 != 0) goto L_0x0067
            goto L_0x0084
        L_0x0067:
            T r10 = r0.chartData
            r11 = r10
            org.telegram.ui.Charts.data.StackLinearChartData r11 = (org.telegram.ui.Charts.data.StackLinearChartData) r11
            int[][] r11 = r11.simplifiedY
            r11 = r11[r6]
            r11 = r11[r1]
            if (r11 <= 0) goto L_0x0084
            org.telegram.ui.Charts.data.StackLinearChartData r10 = (org.telegram.ui.Charts.data.StackLinearChartData) r10
            int[][] r10 = r10.simplifiedY
            r10 = r10[r6]
            r10 = r10[r1]
            float r10 = (float) r10
            float r9 = r9.alpha
            float r10 = r10 * r9
            float r7 = r7 + r10
            int r8 = r8 + 1
        L_0x0084:
            int r6 = r6 + 1
            goto L_0x004c
        L_0x0087:
            float r6 = (float) r1
            int r9 = r3 + -1
            float r10 = (float) r9
            float r6 = r6 / r10
            int r10 = r0.pickerWidth
            float r10 = (float) r10
            float r6 = r6 * r10
            r10 = 0
            r11 = 0
        L_0x0093:
            java.util.ArrayList<L> r12 = r0.lines
            int r12 = r12.size()
            if (r10 >= r12) goto L_0x0153
            java.util.ArrayList<L> r12 = r0.lines
            java.lang.Object r12 = r12.get(r10)
            org.telegram.ui.Charts.view_data.LineViewData r12 = (org.telegram.ui.Charts.view_data.LineViewData) r12
            boolean r13 = r12.enabled
            if (r13 != 0) goto L_0x00af
            float r13 = r12.alpha
            int r13 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x00af
            goto L_0x014e
        L_0x00af:
            if (r8 != r4) goto L_0x00c1
            T r13 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r13 = (org.telegram.ui.Charts.data.StackLinearChartData) r13
            int[][] r13 = r13.simplifiedY
            r13 = r13[r10]
            r13 = r13[r1]
            if (r13 != 0) goto L_0x00be
            goto L_0x00c5
        L_0x00be:
            float r13 = r12.alpha
            goto L_0x00d7
        L_0x00c1:
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x00c7
        L_0x00c5:
            r13 = 0
            goto L_0x00d7
        L_0x00c7:
            T r13 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r13 = (org.telegram.ui.Charts.data.StackLinearChartData) r13
            int[][] r13 = r13.simplifiedY
            r13 = r13[r10]
            r13 = r13[r1]
            float r13 = (float) r13
            float r14 = r12.alpha
            float r13 = r13 * r14
            float r13 = r13 / r7
        L_0x00d7:
            int r14 = r0.pikerHeight
            float r15 = (float) r14
            float r13 = r13 * r15
            float r15 = (float) r14
            float r15 = r15 - r13
            float r15 = r15 - r11
            if (r1 != 0) goto L_0x00eb
            android.graphics.Path r4 = r12.chartPathPicker
            float r14 = (float) r14
            r4.moveTo(r5, r14)
            boolean[] r4 = r0.skipPoints
            r4[r10] = r2
        L_0x00eb:
            T r4 = r0.chartData
            r14 = r4
            org.telegram.ui.Charts.data.StackLinearChartData r14 = (org.telegram.ui.Charts.data.StackLinearChartData) r14
            int[][] r14 = r14.simplifiedY
            r14 = r14[r10]
            r14 = r14[r1]
            if (r14 != 0) goto L_0x0129
            if (r1 <= 0) goto L_0x0129
            r14 = r4
            org.telegram.ui.Charts.data.StackLinearChartData r14 = (org.telegram.ui.Charts.data.StackLinearChartData) r14
            int[][] r14 = r14.simplifiedY
            r14 = r14[r10]
            int r16 = r1 + -1
            r14 = r14[r16]
            if (r14 != 0) goto L_0x0129
            if (r1 >= r9) goto L_0x0129
            org.telegram.ui.Charts.data.StackLinearChartData r4 = (org.telegram.ui.Charts.data.StackLinearChartData) r4
            int[][] r4 = r4.simplifiedY
            r4 = r4[r10]
            int r14 = r1 + 1
            r4 = r4[r14]
            if (r4 != 0) goto L_0x0129
            boolean[] r4 = r0.skipPoints
            boolean r4 = r4[r10]
            if (r4 != 0) goto L_0x0123
            android.graphics.Path r4 = r12.chartPathPicker
            int r14 = r0.pikerHeight
            float r14 = (float) r14
            r4.lineTo(r6, r14)
        L_0x0123:
            boolean[] r4 = r0.skipPoints
            r14 = 1
            r4[r10] = r14
            goto L_0x0140
        L_0x0129:
            boolean[] r4 = r0.skipPoints
            boolean r4 = r4[r10]
            if (r4 == 0) goto L_0x0137
            android.graphics.Path r4 = r12.chartPathPicker
            int r14 = r0.pikerHeight
            float r14 = (float) r14
            r4.lineTo(r6, r14)
        L_0x0137:
            android.graphics.Path r4 = r12.chartPathPicker
            r4.lineTo(r6, r15)
            boolean[] r4 = r0.skipPoints
            r4[r10] = r2
        L_0x0140:
            if (r1 != r9) goto L_0x014d
            android.graphics.Path r4 = r12.chartPathPicker
            int r12 = r0.pickerWidth
            float r12 = (float) r12
            int r14 = r0.pikerHeight
            float r14 = (float) r14
            r4.lineTo(r12, r14)
        L_0x014d:
            float r11 = r11 + r13
        L_0x014e:
            int r10 = r10 + 1
            r4 = 1
            goto L_0x0093
        L_0x0153:
            int r1 = r1 + 1
            goto L_0x0045
        L_0x0157:
            java.util.ArrayList<L> r1 = r0.lines
            int r1 = r1.size()
            r2 = 1
            int r1 = r1 - r2
        L_0x015f:
            if (r1 < 0) goto L_0x0175
            java.util.ArrayList<L> r2 = r0.lines
            java.lang.Object r2 = r2.get(r1)
            org.telegram.ui.Charts.view_data.LineViewData r2 = (org.telegram.ui.Charts.view_data.LineViewData) r2
            android.graphics.Path r3 = r2.chartPathPicker
            android.graphics.Paint r2 = r2.paint
            r4 = r18
            r4.drawPath(r3, r2)
            int r1 = r1 + -1
            goto L_0x015f
        L_0x0175:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawPickerChart(android.graphics.Canvas):void");
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
