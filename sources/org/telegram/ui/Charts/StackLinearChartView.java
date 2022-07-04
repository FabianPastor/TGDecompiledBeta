package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.StackLinearViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;

public class StackLinearChartView<T extends StackLinearViewData> extends BaseChartView<StackLinearChartData, T> {
    private float[] mapPoints = new float[2];
    private Matrix matrix = new Matrix();
    Path ovalPath = new Path();
    boolean[] skipPoints;
    float[] startFromY;

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
    /* JADX WARNING: Removed duplicated region for block: B:149:0x051e  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x069a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawChart(android.graphics.Canvas r52) {
        /*
            r51 = this;
            r0 = r51
            r1 = r52
            org.telegram.ui.Charts.data.ChartData r2 = r0.chartData
            if (r2 == 0) goto L_0x0743
            float r2 = r0.chartWidth
            org.telegram.ui.Charts.ChartPickerDelegate r3 = r0.pickerDelegate
            float r3 = r3.pickerEnd
            org.telegram.ui.Charts.ChartPickerDelegate r4 = r0.pickerDelegate
            float r4 = r4.pickerStart
            float r3 = r3 - r4
            float r2 = r2 / r3
            org.telegram.ui.Charts.ChartPickerDelegate r3 = r0.pickerDelegate
            float r3 = r3.pickerStart
            float r3 = r3 * r2
            float r4 = HORIZONTAL_PADDING
            float r3 = r3 - r4
            android.graphics.RectF r4 = r0.chartArea
            float r4 = r4.centerX()
            android.graphics.RectF r5 = r0.chartArea
            float r5 = r5.centerY()
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 + r6
            r6 = 0
        L_0x0032:
            java.util.ArrayList r7 = r0.lines
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x0057
            java.util.ArrayList r7 = r0.lines
            java.lang.Object r7 = r7.get(r6)
            org.telegram.ui.Charts.view_data.StackLinearViewData r7 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r7
            android.graphics.Path r7 = r7.chartPath
            r7.reset()
            java.util.ArrayList r7 = r0.lines
            java.lang.Object r7 = r7.get(r6)
            org.telegram.ui.Charts.view_data.StackLinearViewData r7 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r7
            android.graphics.Path r7 = r7.chartPathPicker
            r7.reset()
            int r6 = r6 + 1
            goto L_0x0032
        L_0x0057:
            r52.save()
            boolean[] r6 = r0.skipPoints
            if (r6 == 0) goto L_0x006b
            int r6 = r6.length
            org.telegram.ui.Charts.data.ChartData r7 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r7 = (org.telegram.ui.Charts.data.StackLinearChartData) r7
            java.util.ArrayList r7 = r7.lines
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x0087
        L_0x006b:
            org.telegram.ui.Charts.data.ChartData r6 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r6 = (org.telegram.ui.Charts.data.StackLinearChartData) r6
            java.util.ArrayList r6 = r6.lines
            int r6 = r6.size()
            boolean[] r6 = new boolean[r6]
            r0.skipPoints = r6
            org.telegram.ui.Charts.data.ChartData r6 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r6 = (org.telegram.ui.Charts.data.StackLinearChartData) r6
            java.util.ArrayList r6 = r6.lines
            int r6 = r6.size()
            float[] r6 = new float[r6]
            r0.startFromY = r6
        L_0x0087:
            r6 = 0
            r7 = 255(0xff, float:3.57E-43)
            r8 = 0
            int r9 = r0.transitionMode
            r10 = 2
            r11 = 1065353216(0x3var_, float:1.0)
            if (r9 != r10) goto L_0x0113
            org.telegram.ui.Charts.view_data.TransitionParams r9 = r0.transitionParams
            float r9 = r9.progress
            r12 = 1058642330(0x3var_a, float:0.6)
            float r9 = r9 / r12
            int r8 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r8 <= 0) goto L_0x00a1
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x00a2
        L_0x00a1:
            r8 = r9
        L_0x00a2:
            android.graphics.Path r9 = r0.ovalPath
            r9.reset()
            android.graphics.RectF r9 = r0.chartArea
            float r9 = r9.width()
            android.graphics.RectF r12 = r0.chartArea
            float r12 = r12.height()
            int r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r9 <= 0) goto L_0x00be
            android.graphics.RectF r9 = r0.chartArea
            float r9 = r9.width()
            goto L_0x00c4
        L_0x00be:
            android.graphics.RectF r9 = r0.chartArea
            float r9 = r9.height()
        L_0x00c4:
            android.graphics.RectF r12 = r0.chartArea
            float r12 = r12.width()
            android.graphics.RectF r13 = r0.chartArea
            float r13 = r13.height()
            int r12 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r12 <= 0) goto L_0x00db
            android.graphics.RectF r12 = r0.chartArea
            float r12 = r12.height()
            goto L_0x00e1
        L_0x00db:
            android.graphics.RectF r12 = r0.chartArea
            float r12 = r12.width()
        L_0x00e1:
            r13 = 1055286886(0x3ee66666, float:0.45)
            float r12 = r12 * r13
            float r13 = r9 - r12
            r14 = 1073741824(0x40000000, float:2.0)
            float r13 = r13 / r14
            org.telegram.ui.Charts.view_data.TransitionParams r14 = r0.transitionParams
            float r14 = r14.progress
            float r14 = r11 - r14
            float r13 = r13 * r14
            float r13 = r13 + r12
            android.graphics.RectF r14 = new android.graphics.RectF
            r14.<init>()
            float r15 = r4 - r13
            float r11 = r5 - r13
            float r10 = r4 + r13
            r17 = r6
            float r6 = r5 + r13
            r14.set(r15, r11, r10, r6)
            android.graphics.Path r6 = r0.ovalPath
            android.graphics.Path$Direction r10 = android.graphics.Path.Direction.CW
            r6.addRoundRect(r14, r13, r13, r10)
            android.graphics.Path r6 = r0.ovalPath
            r1.clipPath(r6)
            goto L_0x0124
        L_0x0113:
            r17 = r6
            int r6 = r0.transitionMode
            r9 = 3
            if (r6 != r9) goto L_0x0124
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float r6 = r6.progress
            r9 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r9
            int r7 = (int) r6
            goto L_0x0125
        L_0x0124:
        L_0x0125:
            r6 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            org.telegram.ui.Charts.data.ChartData r12 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r12 = (org.telegram.ui.Charts.data.StackLinearChartData) r12
            float[] r12 = r12.xPercentage
            int r12 = r12.length
            r13 = 1
            r14 = 2
            if (r12 >= r14) goto L_0x0137
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0141
        L_0x0137:
            org.telegram.ui.Charts.data.ChartData r12 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r12 = (org.telegram.ui.Charts.data.StackLinearChartData) r12
            float[] r12 = r12.xPercentage
            r12 = r12[r13]
            float r12 = r12 * r2
        L_0x0141:
            float r14 = HORIZONTAL_PADDING
            float r14 = r14 / r12
            int r14 = (int) r14
            int r14 = r14 + r13
            int r15 = r0.startXIndex
            int r15 = r15 - r14
            int r15 = r15 - r13
            r13 = 0
            int r15 = java.lang.Math.max(r13, r15)
            org.telegram.ui.Charts.data.ChartData r13 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r13 = (org.telegram.ui.Charts.data.StackLinearChartData) r13
            float[] r13 = r13.xPercentage
            int r13 = r13.length
            r18 = 1
            int r13 = r13 + -1
            r20 = r6
            int r6 = r0.endXIndex
            int r6 = r6 + r14
            int r6 = r6 + 1
            int r6 = java.lang.Math.min(r13, r6)
            r13 = 0
            r21 = 0
            r22 = r15
            r48 = r21
            r21 = r9
            r9 = r48
            r49 = r22
            r22 = r10
            r10 = r49
        L_0x0176:
            if (r10 > r6) goto L_0x06db
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r48 = r25
            r25 = r11
            r11 = r48
            r49 = r26
            r26 = r12
            r12 = r49
            r50 = r27
            r27 = r14
            r14 = r50
        L_0x0194:
            r28 = r7
            java.util.ArrayList r7 = r0.lines
            int r7 = r7.size()
            if (r14 >= r7) goto L_0x01dc
            java.util.ArrayList r7 = r0.lines
            java.lang.Object r7 = r7.get(r14)
            org.telegram.ui.Charts.view_data.LineViewData r7 = (org.telegram.ui.Charts.view_data.LineViewData) r7
            boolean r1 = r7.enabled
            if (r1 != 0) goto L_0x01b5
            float r1 = r7.alpha
            r29 = 0
            int r1 = (r1 > r29 ? 1 : (r1 == r29 ? 0 : -1))
            if (r1 != 0) goto L_0x01b5
            r30 = r9
            goto L_0x01d3
        L_0x01b5:
            org.telegram.ui.Charts.data.ChartData$Line r1 = r7.line
            int[] r1 = r1.y
            r1 = r1[r10]
            if (r1 <= 0) goto L_0x01cf
            org.telegram.ui.Charts.data.ChartData$Line r1 = r7.line
            int[] r1 = r1.y
            r1 = r1[r10]
            float r1 = (float) r1
            r30 = r9
            float r9 = r7.alpha
            float r1 = r1 * r9
            float r24 = r24 + r1
            int r12 = r12 + 1
            goto L_0x01d1
        L_0x01cf:
            r30 = r9
        L_0x01d1:
            r1 = r14
            r11 = r1
        L_0x01d3:
            int r14 = r14 + 1
            r1 = r52
            r7 = r28
            r9 = r30
            goto L_0x0194
        L_0x01dc:
            r30 = r9
            r1 = 0
        L_0x01df:
            java.util.ArrayList r7 = r0.lines
            int r7 = r7.size()
            if (r1 >= r7) goto L_0x06bc
            java.util.ArrayList r7 = r0.lines
            java.lang.Object r7 = r7.get(r1)
            org.telegram.ui.Charts.view_data.LineViewData r7 = (org.telegram.ui.Charts.view_data.LineViewData) r7
            boolean r14 = r7.enabled
            if (r14 != 0) goto L_0x020b
            float r14 = r7.alpha
            r29 = 0
            int r14 = (r14 > r29 ? 1 : (r14 == r29 ? 0 : -1))
            if (r14 != 0) goto L_0x020b
            r34 = r2
            r35 = r3
            r46 = r4
            r14 = r6
            r45 = r8
            r31 = r12
            r41 = r15
            r3 = 0
            goto L_0x06ab
        L_0x020b:
            org.telegram.ui.Charts.data.ChartData$Line r14 = r7.line
            int[] r14 = r14.y
            r30 = r9
            r9 = 1
            if (r12 != r9) goto L_0x0221
            r9 = r14[r10]
            if (r9 != 0) goto L_0x021c
            r9 = 0
            r31 = r12
            goto L_0x0235
        L_0x021c:
            float r9 = r7.alpha
            r31 = r12
            goto L_0x0235
        L_0x0221:
            r9 = 0
            int r31 = (r24 > r9 ? 1 : (r24 == r9 ? 0 : -1))
            if (r31 != 0) goto L_0x022a
            r9 = 0
            r31 = r12
            goto L_0x0235
        L_0x022a:
            r9 = r14[r10]
            float r9 = (float) r9
            r31 = r12
            float r12 = r7.alpha
            float r9 = r9 * r12
            float r9 = r9 / r24
        L_0x0235:
            org.telegram.ui.Charts.data.ChartData r12 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r12 = (org.telegram.ui.Charts.data.StackLinearChartData) r12
            float[] r12 = r12.xPercentage
            r12 = r12[r10]
            float r12 = r12 * r2
            float r12 = r12 - r3
            if (r10 != r6) goto L_0x024a
            r32 = r13
            int r13 = r51.getMeasuredWidth()
            float r13 = (float) r13
            goto L_0x0259
        L_0x024a:
            r32 = r13
            org.telegram.ui.Charts.data.ChartData r13 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r13 = (org.telegram.ui.Charts.data.StackLinearChartData) r13
            float[] r13 = r13.xPercentage
            int r33 = r10 + 1
            r13 = r13[r33]
            float r13 = r13 * r2
            float r13 = r13 - r3
        L_0x0259:
            r29 = 0
            int r33 = (r9 > r29 ? 1 : (r9 == r29 ? 0 : -1))
            if (r33 != 0) goto L_0x0263
            if (r1 != r11) goto L_0x0263
            r17 = 1
        L_0x0263:
            int r33 = r51.getMeasuredHeight()
            r34 = r2
            int r2 = r0.chartBottom
            int r33 = r33 - r2
            int r2 = SIGNATURE_TEXT_HEIGHT
            int r2 = r33 - r2
            float r2 = (float) r2
            float r2 = r2 * r9
            int r33 = r51.getMeasuredHeight()
            r35 = r3
            int r3 = r0.chartBottom
            int r3 = r33 - r3
            float r3 = (float) r3
            float r3 = r3 - r2
            float r3 = r3 - r23
            r33 = r2
            float[] r2 = r0.startFromY
            r2[r1] = r3
            r2 = 0
            int r36 = r51.getMeasuredHeight()
            r37 = r2
            int r2 = r0.chartBottom
            int r2 = r36 - r2
            float r2 = (float) r2
            r36 = r12
            if (r10 != r6) goto L_0x029b
            r30 = r12
            goto L_0x029f
        L_0x029b:
            if (r10 != r15) goto L_0x029f
            r32 = r12
        L_0x029f:
            r38 = r6
            int r6 = r0.transitionMode
            r39 = 1119092736(0x42b40000, float:90.0)
            r40 = r14
            r14 = 2
            if (r6 != r14) goto L_0x042f
            if (r1 == r11) goto L_0x042f
            int r6 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x02c1
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r6 = r6.startX
            r6 = r6[r1]
            org.telegram.ui.Charts.view_data.TransitionParams r14 = r0.transitionParams
            float[] r14 = r14.startY
            r14 = r14[r1]
            r22 = r6
            r25 = r14
            goto L_0x02d1
        L_0x02c1:
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r6 = r6.endX
            r6 = r6[r1]
            org.telegram.ui.Charts.view_data.TransitionParams r14 = r0.transitionParams
            float[] r14 = r14.endY
            r14 = r14[r1]
            r22 = r6
            r25 = r14
        L_0x02d1:
            float r20 = r4 - r22
            float r21 = r5 - r25
            float r6 = r12 - r22
            float r6 = r6 * r21
            float r6 = r6 / r20
            float r6 = r6 + r25
            r14 = 1065353216(0x3var_, float:1.0)
            float r16 = r14 - r8
            float r16 = r16 * r3
            float r41 = r6 * r8
            float r3 = r16 + r41
            float r41 = r14 - r8
            float r41 = r41 * r2
            float r14 = r6 * r8
            float r41 = r41 + r14
            float r2 = r21 / r20
            r14 = 0
            int r42 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r42 <= 0) goto L_0x0305
            r42 = r6
            r14 = r7
            double r6 = (double) r2
            double r6 = java.lang.Math.atan(r6)
            double r6 = -r6
            double r6 = java.lang.Math.toDegrees(r6)
            float r6 = (float) r6
            goto L_0x0316
        L_0x0305:
            r42 = r6
            r14 = r7
            float r6 = java.lang.Math.abs(r2)
            double r6 = (double) r6
            double r6 = java.lang.Math.atan(r6)
            double r6 = java.lang.Math.toDegrees(r6)
            float r6 = (float) r6
        L_0x0316:
            float r6 = r6 - r39
            int r7 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r7 < 0) goto L_0x037e
            float[] r7 = r0.mapPoints
            r19 = 0
            r7[r19] = r12
            r18 = 1
            r7[r18] = r3
            android.graphics.Matrix r7 = r0.matrix
            r7.reset()
            android.graphics.Matrix r7 = r0.matrix
            r43 = r2
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r2 = r2.progress
            float r2 = r2 * r6
            r7.postRotate(r2, r4, r5)
            android.graphics.Matrix r2 = r0.matrix
            float[] r7 = r0.mapPoints
            r2.mapPoints(r7)
            float[] r2 = r0.mapPoints
            r7 = 0
            r12 = r2[r7]
            r7 = 1
            r3 = r2[r7]
            int r7 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x034d
            r7 = r4
            r12 = r7
        L_0x034d:
            r7 = 0
            r2[r7] = r36
            r7 = 1
            r2[r7] = r41
            android.graphics.Matrix r2 = r0.matrix
            r2.reset()
            android.graphics.Matrix r2 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r7 = r0.transitionParams
            float r7 = r7.progress
            float r7 = r7 * r6
            r2.postRotate(r7, r4, r5)
            android.graphics.Matrix r2 = r0.matrix
            float[] r7 = r0.mapPoints
            r2.mapPoints(r7)
            float[] r2 = r0.mapPoints
            r7 = 1
            r2 = r2[r7]
            int r7 = (r36 > r4 ? 1 : (r36 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x0375
            r36 = r4
        L_0x0375:
            r7 = r3
            r45 = r8
            r3 = r2
            r2 = r6
            r6 = r36
            goto L_0x0438
        L_0x037e:
            r43 = r2
            int r2 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r2 < 0) goto L_0x03a2
            r2 = 1065353216(0x3var_, float:1.0)
            float r7 = r2 - r8
            float r7 = r7 * r12
            float r16 = r4 * r8
            float r7 = r7 + r16
            r12 = r7
            r36 = r7
            float r7 = r2 - r8
            float r7 = r7 * r3
            float r2 = r5 * r8
            float r7 = r7 + r2
            r3 = r7
            r2 = r7
            r45 = r8
            r3 = r2
            r2 = r6
            r6 = r36
            goto L_0x0438
        L_0x03a2:
            float[] r2 = r0.mapPoints
            r7 = 0
            r2[r7] = r12
            r7 = 1
            r2[r7] = r3
            android.graphics.Matrix r2 = r0.matrix
            r2.reset()
            android.graphics.Matrix r2 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r7 = r0.transitionParams
            float r7 = r7.progress
            float r7 = r7 * r6
            r44 = r3
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r3 = r3.progress
            r45 = r8
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float[] r8 = r8.angle
            r8 = r8[r1]
            float r3 = r3 * r8
            float r7 = r7 + r3
            r2.postRotate(r7, r4, r5)
            android.graphics.Matrix r2 = r0.matrix
            float[] r3 = r0.mapPoints
            r2.mapPoints(r3)
            float[] r2 = r0.mapPoints
            r3 = 0
            r12 = r2[r3]
            r3 = 1
            r7 = r2[r3]
            int r3 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x03f3
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r3 = r3.progress
            r8 = 1065353216(0x3var_, float:1.0)
            float r3 = r8 - r3
            float r3 = r3 * r36
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float r8 = r8.progress
            float r8 = r8 * r4
            float r3 = r3 + r8
            r8 = 0
            r2[r8] = r3
            goto L_0x03f6
        L_0x03f3:
            r8 = 0
            r2[r8] = r36
        L_0x03f6:
            float[] r2 = r0.mapPoints
            r3 = 1
            r2[r3] = r41
            android.graphics.Matrix r2 = r0.matrix
            r2.reset()
            android.graphics.Matrix r2 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r3 = r3.progress
            float r3 = r3 * r6
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float r8 = r8.progress
            r37 = r6
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r6 = r6.angle
            r6 = r6[r1]
            float r8 = r8 * r6
            float r3 = r3 + r8
            r2.postRotate(r3, r4, r5)
            android.graphics.Matrix r2 = r0.matrix
            float[] r3 = r0.mapPoints
            r2.mapPoints(r3)
            float[] r2 = r0.mapPoints
            r3 = 0
            r36 = r2[r3]
            r3 = 1
            r2 = r2[r3]
            r3 = r2
            r6 = r36
            r2 = r37
            goto L_0x0438
        L_0x042f:
            r14 = r7
            r45 = r8
            r7 = r3
            r6 = r36
            r3 = r2
            r2 = r37
        L_0x0438:
            if (r10 != r15) goto L_0x049d
            r8 = 0
            r36 = r13
            int r13 = r51.getMeasuredHeight()
            float r13 = (float) r13
            r37 = r14
            int r14 = r0.transitionMode
            r41 = r15
            r15 = 2
            if (r14 != r15) goto L_0x048a
            if (r1 == r11) goto L_0x048a
            float[] r14 = r0.mapPoints
            float r15 = r8 - r4
            r19 = 0
            r14[r19] = r15
            r15 = 1
            r14[r15] = r13
            android.graphics.Matrix r14 = r0.matrix
            r14.reset()
            android.graphics.Matrix r14 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r15 = r0.transitionParams
            float r15 = r15.progress
            float r15 = r15 * r2
            r42 = r2
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r0.transitionParams
            float r2 = r2.progress
            r43 = r8
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float[] r8 = r8.angle
            r8 = r8[r1]
            float r2 = r2 * r8
            float r15 = r15 + r2
            r14.postRotate(r15, r4, r5)
            android.graphics.Matrix r2 = r0.matrix
            float[] r8 = r0.mapPoints
            r2.mapPoints(r8)
            float[] r2 = r0.mapPoints
            r8 = 0
            r14 = r2[r8]
            r8 = 1
            r13 = r2[r8]
            r8 = r14
            goto L_0x0490
        L_0x048a:
            r42 = r2
            r43 = r8
            r8 = r43
        L_0x0490:
            r2 = r37
            android.graphics.Path r14 = r2.chartPath
            r14.moveTo(r8, r13)
            boolean[] r14 = r0.skipPoints
            r15 = 0
            r14[r1] = r15
            goto L_0x04a4
        L_0x049d:
            r42 = r2
            r36 = r13
            r2 = r14
            r41 = r15
        L_0x04a4:
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            if (r8 != 0) goto L_0x04aa
            r8 = 0
            goto L_0x04ae
        L_0x04aa:
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float r8 = r8.progress
        L_0x04ae:
            r13 = 0
            int r14 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r14 != 0) goto L_0x04e9
            if (r10 <= 0) goto L_0x04e9
            int r13 = r10 + -1
            r13 = r40[r13]
            if (r13 != 0) goto L_0x04e9
            r14 = r38
            if (r10 >= r14) goto L_0x04eb
            int r13 = r10 + 1
            r13 = r40[r13]
            if (r13 != 0) goto L_0x04eb
            int r13 = r0.transitionMode
            r15 = 2
            if (r13 == r15) goto L_0x04eb
            boolean[] r13 = r0.skipPoints
            boolean r13 = r13[r1]
            if (r13 != 0) goto L_0x04e3
            if (r1 != r11) goto L_0x04de
            android.graphics.Path r13 = r2.chartPath
            r15 = 1065353216(0x3var_, float:1.0)
            float r37 = r15 - r8
            float r15 = r3 * r37
            r13.lineTo(r6, r15)
            goto L_0x04e3
        L_0x04de:
            android.graphics.Path r13 = r2.chartPath
            r13.lineTo(r6, r3)
        L_0x04e3:
            boolean[] r13 = r0.skipPoints
            r15 = 1
            r13[r1] = r15
            goto L_0x051c
        L_0x04e9:
            r14 = r38
        L_0x04eb:
            boolean[] r13 = r0.skipPoints
            boolean r13 = r13[r1]
            if (r13 == 0) goto L_0x0504
            if (r1 != r11) goto L_0x04ff
            android.graphics.Path r13 = r2.chartPath
            r15 = 1065353216(0x3var_, float:1.0)
            float r37 = r15 - r8
            float r15 = r3 * r37
            r13.lineTo(r6, r15)
            goto L_0x0504
        L_0x04ff:
            android.graphics.Path r13 = r2.chartPath
            r13.lineTo(r6, r3)
        L_0x0504:
            if (r1 != r11) goto L_0x0512
            android.graphics.Path r13 = r2.chartPath
            r15 = 1065353216(0x3var_, float:1.0)
            float r16 = r15 - r8
            float r15 = r7 * r16
            r13.lineTo(r12, r15)
            goto L_0x0517
        L_0x0512:
            android.graphics.Path r13 = r2.chartPath
            r13.lineTo(r12, r7)
        L_0x0517:
            boolean[] r13 = r0.skipPoints
            r15 = 0
            r13[r1] = r15
        L_0x051c:
            if (r10 != r14) goto L_0x069a
            int r13 = r51.getMeasuredWidth()
            float r13 = (float) r13
            int r15 = r51.getMeasuredHeight()
            float r15 = (float) r15
            r16 = r3
            int r3 = r0.transitionMode
            r38 = r6
            r6 = 2
            if (r3 != r6) goto L_0x0566
            if (r1 == r11) goto L_0x0566
            float[] r3 = r0.mapPoints
            float r6 = r13 + r4
            r19 = 0
            r3[r19] = r6
            r6 = 1
            r3[r6] = r15
            android.graphics.Matrix r3 = r0.matrix
            r3.reset()
            android.graphics.Matrix r3 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float r6 = r6.progress
            r43 = r8
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float[] r8 = r8.angle
            r8 = r8[r1]
            float r6 = r6 * r8
            r3.postRotate(r6, r4, r5)
            android.graphics.Matrix r3 = r0.matrix
            float[] r6 = r0.mapPoints
            r3.mapPoints(r6)
            float[] r3 = r0.mapPoints
            r6 = 0
            r13 = r3[r6]
            r6 = 1
            r15 = r3[r6]
            goto L_0x056d
        L_0x0566:
            r43 = r8
            android.graphics.Path r3 = r2.chartPath
            r3.lineTo(r13, r15)
        L_0x056d:
            int r3 = r0.transitionMode
            r6 = 2
            if (r3 != r6) goto L_0x0694
            if (r1 == r11) goto L_0x0694
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float[] r3 = r3.startX
            r3 = r3[r1]
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r6 = r6.startY
            r6 = r6[r1]
            float r8 = r4 - r3
            float r20 = r5 - r6
            r22 = r3
            float r3 = r20 / r8
            r21 = 0
            int r25 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r25 <= 0) goto L_0x059e
            r25 = r8
            r44 = r9
            double r8 = (double) r3
            double r8 = java.lang.Math.atan(r8)
            double r8 = -r8
            double r8 = java.lang.Math.toDegrees(r8)
            float r8 = (float) r8
            goto L_0x05b0
        L_0x059e:
            r25 = r8
            r44 = r9
            float r8 = java.lang.Math.abs(r3)
            double r8 = (double) r8
            double r8 = java.lang.Math.atan(r8)
            double r8 = java.lang.Math.toDegrees(r8)
            float r8 = (float) r8
        L_0x05b0:
            float r8 = r8 - r39
            org.telegram.ui.Charts.view_data.TransitionParams r9 = r0.transitionParams
            float[] r9 = r9.startX
            r9 = r9[r1]
            org.telegram.ui.Charts.view_data.TransitionParams r13 = r0.transitionParams
            float[] r13 = r13.startY
            r13 = r13[r1]
            float[] r15 = r0.mapPoints
            r19 = 0
            r15[r19] = r9
            r18 = 1
            r15[r18] = r13
            android.graphics.Matrix r15 = r0.matrix
            r15.reset()
            android.graphics.Matrix r15 = r0.matrix
            r21 = r3
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r3 = r3.progress
            float r3 = r3 * r8
            r39 = r6
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float r6 = r6.progress
            r42 = r8
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float[] r8 = r8.angle
            r8 = r8[r1]
            float r6 = r6 * r8
            float r3 = r3 + r6
            r15.postRotate(r3, r4, r5)
            android.graphics.Matrix r3 = r0.matrix
            float[] r6 = r0.mapPoints
            r3.mapPoints(r6)
            float[] r3 = r0.mapPoints
            r6 = 0
            r8 = r3[r6]
            r9 = 1
            r3 = r3[r9]
            float r9 = r12 - r8
            float r9 = java.lang.Math.abs(r9)
            r19 = r7
            double r6 = (double) r9
            r46 = 4562254508917369340(0x3var_dd2f1a9fc, double:0.001)
            int r9 = (r6 > r46 ? 1 : (r6 == r46 ? 0 : -1))
            if (r9 >= 0) goto L_0x0634
            int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r6 >= 0) goto L_0x0614
            int r6 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r6 < 0) goto L_0x061c
        L_0x0614:
            int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x0634
            int r6 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x0634
        L_0x061c:
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r6 = r6.angle
            r6 = r6[r1]
            r7 = -1020002304(0xffffffffCLASSNAME, float:-180.0)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x062e
            r6 = 0
            r7 = 0
            r9 = r7
            r7 = r19
            goto L_0x063e
        L_0x062e:
            r6 = 0
            r7 = 3
            r9 = r7
            r7 = r19
            goto L_0x063e
        L_0x0634:
            r7 = r19
            int r6 = r0.quarterForPoint(r12, r7)
            int r9 = r0.quarterForPoint(r8, r3)
        L_0x063e:
            r13 = r6
        L_0x063f:
            if (r13 > r9) goto L_0x0688
            if (r13 != 0) goto L_0x0654
            android.graphics.Path r15 = r2.chartPath
            r19 = r3
            int r3 = r51.getMeasuredWidth()
            float r3 = (float) r3
            r46 = r4
            r4 = 0
            r15.lineTo(r3, r4)
            r3 = 0
            goto L_0x0681
        L_0x0654:
            r19 = r3
            r46 = r4
            r3 = 1
            if (r13 != r3) goto L_0x066c
            android.graphics.Path r3 = r2.chartPath
            int r4 = r51.getMeasuredWidth()
            float r4 = (float) r4
            int r15 = r51.getMeasuredHeight()
            float r15 = (float) r15
            r3.lineTo(r4, r15)
            r3 = 0
            goto L_0x0681
        L_0x066c:
            r3 = 2
            if (r13 != r3) goto L_0x067b
            android.graphics.Path r4 = r2.chartPath
            int r15 = r51.getMeasuredHeight()
            float r15 = (float) r15
            r3 = 0
            r4.lineTo(r3, r15)
            goto L_0x0681
        L_0x067b:
            r3 = 0
            android.graphics.Path r4 = r2.chartPath
            r4.lineTo(r3, r3)
        L_0x0681:
            int r13 = r13 + 1
            r3 = r19
            r4 = r46
            goto L_0x063f
        L_0x0688:
            r19 = r3
            r46 = r4
            r3 = 0
            r21 = r20
            r20 = r25
            r25 = r39
            goto L_0x06a5
        L_0x0694:
            r46 = r4
            r44 = r9
            r3 = 0
            goto L_0x06a5
        L_0x069a:
            r16 = r3
            r46 = r4
            r38 = r6
            r43 = r8
            r44 = r9
            r3 = 0
        L_0x06a5:
            float r23 = r23 + r33
            r9 = r30
            r13 = r32
        L_0x06ab:
            int r1 = r1 + 1
            r6 = r14
            r12 = r31
            r2 = r34
            r3 = r35
            r15 = r41
            r8 = r45
            r4 = r46
            goto L_0x01df
        L_0x06bc:
            r34 = r2
            r35 = r3
            r46 = r4
            r14 = r6
            r45 = r8
            r30 = r9
            r31 = r12
            r32 = r13
            r41 = r15
            int r10 = r10 + 1
            r1 = r52
            r11 = r25
            r12 = r26
            r14 = r27
            r7 = r28
            goto L_0x0176
        L_0x06db:
            r34 = r2
            r35 = r3
            r46 = r4
            r28 = r7
            r45 = r8
            r30 = r9
            r25 = r11
            r26 = r12
            r27 = r14
            r41 = r15
            r14 = r6
            r52.save()
            int r1 = SIGNATURE_TEXT_HEIGHT
            float r1 = (float) r1
            int r2 = r51.getMeasuredHeight()
            int r3 = r0.chartBottom
            int r2 = r2 - r3
            float r2 = (float) r2
            r3 = r52
            r4 = r30
            r3.clipRect(r13, r1, r4, r2)
            if (r17 == 0) goto L_0x0710
            java.lang.String r1 = "statisticChartLineEmpty"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r3.drawColor(r1)
        L_0x0710:
            java.util.ArrayList r1 = r0.lines
            int r1 = r1.size()
            r2 = 1
            int r1 = r1 - r2
        L_0x0718:
            if (r1 < 0) goto L_0x073a
            java.util.ArrayList r2 = r0.lines
            java.lang.Object r2 = r2.get(r1)
            org.telegram.ui.Charts.view_data.LineViewData r2 = (org.telegram.ui.Charts.view_data.LineViewData) r2
            android.graphics.Paint r6 = r2.paint
            r7 = r28
            r6.setAlpha(r7)
            android.graphics.Path r6 = r2.chartPath
            android.graphics.Paint r8 = r2.paint
            r3.drawPath(r6, r8)
            android.graphics.Paint r6 = r2.paint
            r8 = 255(0xff, float:3.57E-43)
            r6.setAlpha(r8)
            int r1 = r1 + -1
            goto L_0x0718
        L_0x073a:
            r7 = r28
            r52.restore()
            r52.restore()
            goto L_0x0744
        L_0x0743:
            r3 = r1
        L_0x0744:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawChart(android.graphics.Canvas):void");
    }

    private int quarterForPoint(float x, float y) {
        float cX = this.chartArea.centerX();
        float cY = this.chartArea.centerY() + ((float) AndroidUtilities.dp(16.0f));
        if (x >= cX && y <= cY) {
            return 0;
        }
        if (x >= cX && y >= cY) {
            return 1;
        }
        if (x >= cX || y < cY) {
            return 3;
        }
        return 2;
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        float f;
        float sum;
        int nl;
        float yPercentage;
        boolean hasEmptyPoint;
        Canvas canvas2 = canvas;
        if (this.chartData != null) {
            int nl2 = this.lines.size();
            for (int k = 0; k < nl2; k++) {
                ((StackLinearViewData) this.lines.get(k)).chartPathPicker.reset();
            }
            int n = ((StackLinearChartData) this.chartData).simplifiedSize;
            boolean[] zArr = this.skipPoints;
            if (zArr == null || zArr.length < ((StackLinearChartData) this.chartData).lines.size()) {
                this.skipPoints = new boolean[((StackLinearChartData) this.chartData).lines.size()];
            }
            boolean hasEmptyPoint2 = false;
            int i = 0;
            while (true) {
                int i2 = 1;
                if (i >= n) {
                    break;
                }
                float stackOffset = 0.0f;
                float sum2 = 0.0f;
                int lastEnabled = 0;
                int drawingLinesCount = 0;
                int k2 = 0;
                while (true) {
                    f = 0.0f;
                    if (k2 >= this.lines.size()) {
                        break;
                    }
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != 0.0f) {
                        if (((StackLinearChartData) this.chartData).simplifiedY[k2][i] > 0) {
                            sum2 += ((float) ((StackLinearChartData) this.chartData).simplifiedY[k2][i]) * line.alpha;
                            drawingLinesCount++;
                        }
                        lastEnabled = k2;
                    }
                    k2++;
                }
                float xPoint = (((float) i) / ((float) (n - 1))) * this.pickerWidth;
                int k3 = 0;
                while (k3 < this.lines.size()) {
                    LineViewData line2 = (LineViewData) this.lines.get(k3);
                    if (line2.enabled || line2.alpha != f) {
                        if (drawingLinesCount == i2) {
                            if (((StackLinearChartData) this.chartData).simplifiedY[k3][i] == 0) {
                                yPercentage = 0.0f;
                            } else {
                                yPercentage = line2.alpha;
                            }
                        } else if (sum2 == f) {
                            yPercentage = 0.0f;
                        } else {
                            yPercentage = (((float) ((StackLinearChartData) this.chartData).simplifiedY[k3][i]) * line2.alpha) / sum2;
                        }
                        if (yPercentage == f && k3 == lastEnabled) {
                            hasEmptyPoint2 = true;
                        }
                        float height = ((float) this.pikerHeight) * yPercentage;
                        float yPoint = (((float) this.pikerHeight) - height) - stackOffset;
                        if (i == 0) {
                            nl = nl2;
                            hasEmptyPoint = hasEmptyPoint2;
                            sum = sum2;
                            line2.chartPathPicker.moveTo(0.0f, (float) this.pikerHeight);
                            this.skipPoints[k3] = false;
                        } else {
                            nl = nl2;
                            hasEmptyPoint = hasEmptyPoint2;
                            sum = sum2;
                        }
                        if (((StackLinearChartData) this.chartData).simplifiedY[k3][i] == 0 && i > 0 && ((StackLinearChartData) this.chartData).simplifiedY[k3][i - 1] == 0 && i < n - 1 && ((StackLinearChartData) this.chartData).simplifiedY[k3][i + 1] == 0) {
                            if (!this.skipPoints[k3]) {
                                line2.chartPathPicker.lineTo(xPoint, (float) this.pikerHeight);
                            }
                            this.skipPoints[k3] = true;
                        } else {
                            if (this.skipPoints[k3]) {
                                line2.chartPathPicker.lineTo(xPoint, (float) this.pikerHeight);
                            }
                            line2.chartPathPicker.lineTo(xPoint, yPoint);
                            this.skipPoints[k3] = false;
                        }
                        if (i == n - 1) {
                            line2.chartPathPicker.lineTo(this.pickerWidth, (float) this.pikerHeight);
                        }
                        stackOffset += height;
                        hasEmptyPoint2 = hasEmptyPoint;
                    } else {
                        nl = nl2;
                        sum = sum2;
                    }
                    k3++;
                    nl2 = nl;
                    sum2 = sum;
                    i2 = 1;
                    f = 0.0f;
                }
                float f2 = sum2;
                i++;
            }
            if (hasEmptyPoint2) {
                canvas2.drawColor(Theme.getColor("statisticChartLineEmpty"));
            }
            for (int k4 = this.lines.size() - 1; k4 >= 0; k4--) {
                LineViewData line3 = (LineViewData) this.lines.get(k4);
                canvas2.drawPath(line3.chartPathPicker, line3.paint);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        tick();
        drawChart(canvas);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            if (this.tmpI < this.tmpN) {
                drawHorizontalLines(canvas, (ChartHorizontalLinesData) this.horizontalLines.get(this.tmpI));
                drawSignaturesToHorizontalLines(canvas, (ChartHorizontalLinesData) this.horizontalLines.get(this.tmpI));
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

    public int findMaxValue(int startXIndex, int endXIndex) {
        return 100;
    }

    /* access modifiers changed from: protected */
    public float getMinDistance() {
        return 0.1f;
    }

    public void fillTransitionParams(TransitionParams params) {
        float p;
        float offset;
        float fullWidth;
        float p2;
        float yPercentage;
        if (this.chartData != null) {
            float fullWidth2 = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset2 = (this.pickerDelegate.pickerStart * fullWidth2) - HORIZONTAL_PADDING;
            int i = 2;
            int i2 = 1;
            if (((StackLinearChartData) this.chartData).xPercentage.length < 2) {
                p = 1.0f;
            } else {
                p = ((StackLinearChartData) this.chartData).xPercentage[1] * fullWidth2;
            }
            int additionalPoints = ((int) (HORIZONTAL_PADDING / p)) + 1;
            int localStart = Math.max(0, (this.startXIndex - additionalPoints) - 1);
            int localEnd = Math.min(((StackLinearChartData) this.chartData).xPercentage.length - 1, this.endXIndex + additionalPoints + 1);
            this.transitionParams.startX = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.startY = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.endX = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.endY = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.angle = new float[((StackLinearChartData) this.chartData).lines.size()];
            int j = 0;
            while (j < i) {
                int i3 = localStart;
                if (j == i2) {
                    i3 = localEnd;
                }
                int stackOffset = 0;
                float sum = 0.0f;
                int drawingLinesCount = 0;
                for (int k = 0; k < this.lines.size(); k++) {
                    LineViewData line = (LineViewData) this.lines.get(k);
                    if ((line.enabled || line.alpha != 0.0f) && line.line.y[i3] > 0) {
                        sum += ((float) line.line.y[i3]) * line.alpha;
                        drawingLinesCount++;
                    }
                }
                int k2 = 0;
                while (k2 < this.lines.size()) {
                    LineViewData line2 = (LineViewData) this.lines.get(k2);
                    if (line2.enabled || line2.alpha != 0.0f) {
                        int[] y = line2.line.y;
                        if (drawingLinesCount == 1) {
                            if (y[i3] == 0) {
                                p2 = p;
                                yPercentage = 0.0f;
                            } else {
                                yPercentage = line2.alpha;
                                p2 = p;
                            }
                        } else if (sum == 0.0f) {
                            yPercentage = 0.0f;
                            p2 = p;
                        } else {
                            p2 = p;
                            yPercentage = (((float) y[i3]) * line2.alpha) / sum;
                        }
                        float xPoint = (((StackLinearChartData) this.chartData).xPercentage[i3] * fullWidth2) - offset2;
                        fullWidth = fullWidth2;
                        float height = ((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) * yPercentage;
                        offset = offset2;
                        LineViewData lineViewData = line2;
                        float yPoint = (((float) (getMeasuredHeight() - this.chartBottom)) - height) - ((float) stackOffset);
                        int stackOffset2 = (int) (((float) stackOffset) + height);
                        if (j == 0) {
                            this.transitionParams.startX[k2] = xPoint;
                            this.transitionParams.startY[k2] = yPoint;
                        } else {
                            this.transitionParams.endX[k2] = xPoint;
                            this.transitionParams.endY[k2] = yPoint;
                        }
                        stackOffset = stackOffset2;
                    } else {
                        fullWidth = fullWidth2;
                        offset = offset2;
                        p2 = p;
                    }
                    k2++;
                    p = p2;
                    fullWidth2 = fullWidth;
                    offset2 = offset;
                }
                float f = offset2;
                float f2 = p;
                j++;
                i = 2;
                i2 = 1;
            }
        }
    }
}
