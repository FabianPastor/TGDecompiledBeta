package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.StackLinearViewData;

public class StackLinearChartView<T extends StackLinearViewData> extends BaseChartView<StackLinearChartData, T> {
    private float[] mapPoints = new float[2];
    private Matrix matrix = new Matrix();
    Path ovalPath = new Path();
    boolean[] skipPoints;
    float[] startFromY;

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
    /* JADX WARNING: Removed duplicated region for block: B:109:0x03aa  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x03f9  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x040a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0460  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0464  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x047a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01fd  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0203  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0251  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0254  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0261 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawChart(android.graphics.Canvas r36) {
        /*
            r35 = this;
            r0 = r35
            r1 = r36
            T r2 = r0.chartData
            if (r2 == 0) goto L_0x062b
            float r2 = r0.chartWidth
            org.telegram.ui.Charts.ChartPickerDelegate r3 = r0.pickerDelegate
            float r4 = r3.pickerEnd
            float r3 = r3.pickerStart
            float r4 = r4 - r3
            float r2 = r2 / r4
            float r3 = r3 * r2
            float r4 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
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
            r7 = 0
        L_0x002d:
            java.util.ArrayList<L> r8 = r0.lines
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x0052
            java.util.ArrayList<L> r8 = r0.lines
            java.lang.Object r8 = r8.get(r7)
            org.telegram.ui.Charts.view_data.StackLinearViewData r8 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r8
            android.graphics.Path r8 = r8.chartPath
            r8.reset()
            java.util.ArrayList<L> r8 = r0.lines
            java.lang.Object r8 = r8.get(r7)
            org.telegram.ui.Charts.view_data.StackLinearViewData r8 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r8
            android.graphics.Path r8 = r8.chartPathPicker
            r8.reset()
            int r7 = r7 + 1
            goto L_0x002d
        L_0x0052:
            r36.save()
            boolean[] r7 = r0.skipPoints
            if (r7 == 0) goto L_0x0066
            int r7 = r7.length
            T r8 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r8 = (org.telegram.ui.Charts.data.StackLinearChartData) r8
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r8 = r8.lines
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x0082
        L_0x0066:
            T r7 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r7 = (org.telegram.ui.Charts.data.StackLinearChartData) r7
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r7 = r7.lines
            int r7 = r7.size()
            boolean[] r7 = new boolean[r7]
            r0.skipPoints = r7
            T r7 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r7 = (org.telegram.ui.Charts.data.StackLinearChartData) r7
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r7 = r7.lines
            int r7 = r7.size()
            float[] r7 = new float[r7]
            r0.startFromY = r7
        L_0x0082:
            int r7 = r0.transitionMode
            r8 = 3
            r10 = 2
            r11 = 1065353216(0x3var_, float:1.0)
            if (r7 != r10) goto L_0x0109
            org.telegram.ui.Charts.view_data.TransitionParams r7 = r0.transitionParams
            float r7 = r7.progress
            r13 = 1058642330(0x3var_a, float:0.6)
            float r7 = r7 / r13
            int r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r13 <= 0) goto L_0x0098
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x0098:
            android.graphics.Path r13 = r0.ovalPath
            r13.reset()
            android.graphics.RectF r13 = r0.chartArea
            float r13 = r13.width()
            android.graphics.RectF r14 = r0.chartArea
            float r14 = r14.height()
            int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r13 <= 0) goto L_0x00b4
            android.graphics.RectF r13 = r0.chartArea
            float r13 = r13.width()
            goto L_0x00ba
        L_0x00b4:
            android.graphics.RectF r13 = r0.chartArea
            float r13 = r13.height()
        L_0x00ba:
            android.graphics.RectF r14 = r0.chartArea
            float r14 = r14.width()
            android.graphics.RectF r15 = r0.chartArea
            float r15 = r15.height()
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 <= 0) goto L_0x00d1
            android.graphics.RectF r14 = r0.chartArea
            float r14 = r14.height()
            goto L_0x00d7
        L_0x00d1:
            android.graphics.RectF r14 = r0.chartArea
            float r14 = r14.width()
        L_0x00d7:
            r15 = 1055286886(0x3ee66666, float:0.45)
            float r14 = r14 * r15
            float r13 = r13 - r14
            r15 = 1073741824(0x40000000, float:2.0)
            float r13 = r13 / r15
            org.telegram.ui.Charts.view_data.TransitionParams r15 = r0.transitionParams
            float r15 = r15.progress
            float r15 = r11 - r15
            float r13 = r13 * r15
            float r14 = r14 + r13
            android.graphics.RectF r13 = new android.graphics.RectF
            r13.<init>()
            float r15 = r4 - r14
            float r9 = r5 - r14
            float r11 = r4 + r14
            float r12 = r5 + r14
            r13.set(r15, r9, r11, r12)
            android.graphics.Path r9 = r0.ovalPath
            android.graphics.Path$Direction r11 = android.graphics.Path.Direction.CW
            r9.addRoundRect(r13, r14, r14, r11)
            android.graphics.Path r9 = r0.ovalPath
            r1.clipPath(r9)
            r9 = r7
            r7 = 255(0xff, float:3.57E-43)
            goto L_0x0118
        L_0x0109:
            if (r7 != r8) goto L_0x0115
            org.telegram.ui.Charts.view_data.TransitionParams r7 = r0.transitionParams
            float r7 = r7.progress
            r9 = 1132396544(0x437var_, float:255.0)
            float r7 = r7 * r9
            int r7 = (int) r7
            goto L_0x0117
        L_0x0115:
            r7 = 255(0xff, float:3.57E-43)
        L_0x0117:
            r9 = 0
        L_0x0118:
            T r11 = r0.chartData
            r12 = r11
            org.telegram.ui.Charts.data.StackLinearChartData r12 = (org.telegram.ui.Charts.data.StackLinearChartData) r12
            float[] r12 = r12.xPercentage
            int r12 = r12.length
            r13 = 1
            if (r12 >= r10) goto L_0x0126
            r11 = 1065353216(0x3var_, float:1.0)
            goto L_0x012e
        L_0x0126:
            org.telegram.ui.Charts.data.StackLinearChartData r11 = (org.telegram.ui.Charts.data.StackLinearChartData) r11
            float[] r11 = r11.xPercentage
            r11 = r11[r13]
            float r11 = r11 * r2
        L_0x012e:
            float r12 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r12 = r12 / r11
            int r11 = (int) r12
            int r11 = r11 + r13
            int r12 = r0.startXIndex
            int r12 = r12 - r11
            int r12 = r12 - r13
            int r12 = java.lang.Math.max(r6, r12)
            T r14 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r14 = (org.telegram.ui.Charts.data.StackLinearChartData) r14
            float[] r14 = r14.xPercentage
            int r14 = r14.length
            int r14 = r14 - r13
            int r15 = r0.endXIndex
            int r15 = r15 + r11
            int r15 = r15 + r13
            int r11 = java.lang.Math.min(r14, r15)
            r14 = r12
            r8 = 0
            r15 = 0
            r18 = 0
        L_0x0150:
            if (r14 > r11) goto L_0x05db
            r10 = 0
            r20 = 0
            r21 = 0
        L_0x0157:
            java.util.ArrayList<L> r13 = r0.lines
            int r13 = r13.size()
            if (r6 >= r13) goto L_0x0190
            java.util.ArrayList<L> r13 = r0.lines
            java.lang.Object r13 = r13.get(r6)
            org.telegram.ui.Charts.view_data.LineViewData r13 = (org.telegram.ui.Charts.view_data.LineViewData) r13
            r22 = r7
            boolean r7 = r13.enabled
            if (r7 != 0) goto L_0x0176
            float r7 = r13.alpha
            r17 = 0
            int r7 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1))
            if (r7 != 0) goto L_0x0176
            goto L_0x018b
        L_0x0176:
            org.telegram.ui.Charts.data.ChartData$Line r7 = r13.line
            int[] r7 = r7.y
            r21 = r7[r14]
            if (r21 <= 0) goto L_0x0189
            r7 = r7[r14]
            float r7 = (float) r7
            float r13 = r13.alpha
            float r7 = r7 * r13
            float r20 = r20 + r7
            int r10 = r10 + 1
        L_0x0189:
            r21 = r6
        L_0x018b:
            int r6 = r6 + 1
            r7 = r22
            goto L_0x0157
        L_0x0190:
            r22 = r7
            r6 = 0
            r7 = 0
        L_0x0194:
            java.util.ArrayList<L> r13 = r0.lines
            int r13 = r13.size()
            if (r7 >= r13) goto L_0x05c0
            java.util.ArrayList<L> r13 = r0.lines
            java.lang.Object r13 = r13.get(r7)
            org.telegram.ui.Charts.view_data.LineViewData r13 = (org.telegram.ui.Charts.view_data.LineViewData) r13
            r23 = r8
            boolean r8 = r13.enabled
            if (r8 != 0) goto L_0x01c7
            float r8 = r13.alpha
            r17 = 0
            int r8 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1))
            if (r8 != 0) goto L_0x01c7
            r26 = r2
            r27 = r3
            r1 = r6
            r32 = r9
            r25 = r10
            r9 = r11
            r33 = r12
            r2 = r21
            r8 = r23
            r6 = 0
            r10 = 2
            r13 = 0
            goto L_0x05ac
        L_0x01c7:
            org.telegram.ui.Charts.data.ChartData$Line r8 = r13.line
            int[] r8 = r8.y
            r24 = r15
            r15 = 1
            if (r10 != r15) goto L_0x01da
            r15 = r8[r14]
            if (r15 != 0) goto L_0x01d5
            goto L_0x01df
        L_0x01d5:
            float r15 = r13.alpha
            r25 = r10
            goto L_0x01ef
        L_0x01da:
            r15 = 0
            int r25 = (r20 > r15 ? 1 : (r20 == r15 ? 0 : -1))
            if (r25 != 0) goto L_0x01e3
        L_0x01df:
            r25 = r10
            r15 = 0
            goto L_0x01ef
        L_0x01e3:
            r15 = r8[r14]
            float r15 = (float) r15
            r25 = r10
            float r10 = r13.alpha
            float r15 = r15 * r10
            float r10 = r15 / r20
            r15 = r10
        L_0x01ef:
            T r10 = r0.chartData
            r1 = r10
            org.telegram.ui.Charts.data.StackLinearChartData r1 = (org.telegram.ui.Charts.data.StackLinearChartData) r1
            float[] r1 = r1.xPercentage
            r1 = r1[r14]
            float r1 = r1 * r2
            float r1 = r1 - r3
            if (r14 != r11) goto L_0x0203
            int r10 = r35.getMeasuredWidth()
            float r10 = (float) r10
            goto L_0x020e
        L_0x0203:
            org.telegram.ui.Charts.data.StackLinearChartData r10 = (org.telegram.ui.Charts.data.StackLinearChartData) r10
            float[] r10 = r10.xPercentage
            int r26 = r14 + 1
            r10 = r10[r26]
            float r10 = r10 * r2
            float r10 = r10 - r3
        L_0x020e:
            r17 = 0
            int r26 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1))
            if (r26 != 0) goto L_0x021d
            r26 = r2
            r2 = r21
            if (r7 != r2) goto L_0x0221
            r18 = 1
            goto L_0x0221
        L_0x021d:
            r26 = r2
            r2 = r21
        L_0x0221:
            int r21 = r35.getMeasuredHeight()
            r27 = r3
            int r3 = r0.chartBottom
            int r21 = r21 - r3
            int r3 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r3 = r21 - r3
            float r3 = (float) r3
            float r3 = r3 * r15
            int r21 = r35.getMeasuredHeight()
            r28 = r8
            int r8 = r0.chartBottom
            int r8 = r21 - r8
            float r8 = (float) r8
            float r8 = r8 - r3
            float r8 = r8 - r6
            r21 = r3
            float[] r3 = r0.startFromY
            r3[r7] = r8
            int r3 = r35.getMeasuredHeight()
            r29 = r6
            int r6 = r0.chartBottom
            int r3 = r3 - r6
            float r3 = (float) r3
            if (r14 != r11) goto L_0x0254
            r23 = r1
            goto L_0x0258
        L_0x0254:
            if (r14 != r12) goto L_0x0258
            r24 = r1
        L_0x0258:
            int r6 = r0.transitionMode
            r30 = 1119092736(0x42b40000, float:90.0)
            r31 = r11
            r11 = 2
            if (r6 != r11) goto L_0x03a2
            if (r7 == r2) goto L_0x03a2
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x0272
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r11 = r6.startX
            r11 = r11[r7]
            float[] r6 = r6.startY
            r6 = r6[r7]
            goto L_0x027c
        L_0x0272:
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r11 = r6.endX
            r11 = r11[r7]
            float[] r6 = r6.endY
            r6 = r6[r7]
        L_0x027c:
            float r32 = r4 - r11
            float r33 = r5 - r6
            float r11 = r1 - r11
            float r11 = r11 * r33
            float r11 = r11 / r32
            float r11 = r11 + r6
            r6 = 1065353216(0x3var_, float:1.0)
            float r34 = r6 - r9
            float r8 = r8 * r34
            float r11 = r11 * r9
            float r8 = r8 + r11
            float r3 = r3 * r34
            float r3 = r3 + r11
            float r6 = r33 / r32
            r11 = 0
            int r32 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r32 <= 0) goto L_0x02a7
            r32 = r12
            double r11 = (double) r6
            double r11 = java.lang.Math.atan(r11)
            double r11 = -r11
            double r11 = java.lang.Math.toDegrees(r11)
            goto L_0x02b6
        L_0x02a7:
            r32 = r12
            float r6 = java.lang.Math.abs(r6)
            double r11 = (double) r6
            double r11 = java.lang.Math.atan(r11)
            double r11 = java.lang.Math.toDegrees(r11)
        L_0x02b6:
            float r6 = (float) r11
            float r6 = r6 - r30
            int r11 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r11 < 0) goto L_0x0318
            float[] r10 = r0.mapPoints
            r11 = 0
            r10[r11] = r1
            r11 = 1
            r10[r11] = r8
            android.graphics.Matrix r8 = r0.matrix
            r8.reset()
            android.graphics.Matrix r8 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.progress
            float r10 = r10 * r6
            r8.postRotate(r10, r4, r5)
            android.graphics.Matrix r8 = r0.matrix
            float[] r10 = r0.mapPoints
            r8.mapPoints(r10)
            float[] r8 = r0.mapPoints
            r10 = 0
            r11 = r8[r10]
            r12 = 1
            r8 = r8[r12]
            int r19 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r19 >= 0) goto L_0x02e9
            r11 = r4
        L_0x02e9:
            r33 = r8
            float[] r8 = r0.mapPoints
            r8[r10] = r1
            r8[r12] = r3
            android.graphics.Matrix r3 = r0.matrix
            r3.reset()
            android.graphics.Matrix r3 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            float r8 = r8.progress
            float r8 = r8 * r6
            r3.postRotate(r8, r4, r5)
            android.graphics.Matrix r3 = r0.matrix
            float[] r8 = r0.mapPoints
            r3.mapPoints(r8)
            float[] r3 = r0.mapPoints
            r8 = 1
            r3 = r3[r8]
            int r8 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r8 >= 0) goto L_0x0312
            r1 = r4
        L_0x0312:
            r10 = r32
            r8 = r33
            goto L_0x03a8
        L_0x0318:
            int r11 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r11 < 0) goto L_0x032c
            float r1 = r1 * r34
            float r3 = r4 * r9
            float r1 = r1 + r3
            float r8 = r8 * r34
            float r3 = r5 * r9
            float r8 = r8 + r3
            r11 = r1
            r3 = r8
            r10 = r32
            goto L_0x03a8
        L_0x032c:
            float[] r11 = r0.mapPoints
            r12 = 0
            r11[r12] = r1
            r12 = 1
            r11[r12] = r8
            android.graphics.Matrix r8 = r0.matrix
            r8.reset()
            android.graphics.Matrix r8 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r11 = r0.transitionParams
            float r12 = r11.progress
            float r33 = r12 * r6
            float[] r11 = r11.angle
            r11 = r11[r7]
            float r12 = r12 * r11
            float r11 = r33 + r12
            r8.postRotate(r11, r4, r5)
            android.graphics.Matrix r8 = r0.matrix
            float[] r11 = r0.mapPoints
            r8.mapPoints(r11)
            float[] r8 = r0.mapPoints
            r11 = 0
            r12 = r8[r11]
            r19 = 1
            r33 = r8[r19]
            int r10 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r10 < 0) goto L_0x0370
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.progress
            r16 = 1065353216(0x3var_, float:1.0)
            float r19 = r16 - r10
            float r1 = r1 * r19
            float r10 = r10 * r4
            float r1 = r1 + r10
            r8[r11] = r1
            goto L_0x0372
        L_0x0370:
            r8[r11] = r1
        L_0x0372:
            float[] r1 = r0.mapPoints
            r8 = 1
            r1[r8] = r3
            android.graphics.Matrix r1 = r0.matrix
            r1.reset()
            android.graphics.Matrix r1 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r8 = r3.progress
            float r10 = r8 * r6
            float[] r3 = r3.angle
            r3 = r3[r7]
            float r8 = r8 * r3
            float r10 = r10 + r8
            r1.postRotate(r10, r4, r5)
            android.graphics.Matrix r1 = r0.matrix
            float[] r3 = r0.mapPoints
            r1.mapPoints(r3)
            float[] r1 = r0.mapPoints
            r3 = 0
            r8 = r1[r3]
            r3 = 1
            r1 = r1[r3]
            r3 = r1
            r1 = r8
            r11 = r12
            goto L_0x0312
        L_0x03a2:
            r32 = r12
            r11 = r1
            r10 = r32
            r6 = 0
        L_0x03a8:
            if (r14 != r10) goto L_0x03f9
            int r12 = r35.getMeasuredHeight()
            float r12 = (float) r12
            r32 = r9
            int r9 = r0.transitionMode
            r33 = r10
            r10 = 2
            if (r9 != r10) goto L_0x03ed
            if (r7 == r2) goto L_0x03ed
            float[] r9 = r0.mapPoints
            r10 = 0
            float r34 = r10 - r4
            r10 = 0
            r9[r10] = r34
            r10 = 1
            r9[r10] = r12
            android.graphics.Matrix r9 = r0.matrix
            r9.reset()
            android.graphics.Matrix r9 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r12 = r10.progress
            float r6 = r6 * r12
            float[] r10 = r10.angle
            r10 = r10[r7]
            float r12 = r12 * r10
            float r6 = r6 + r12
            r9.postRotate(r6, r4, r5)
            android.graphics.Matrix r6 = r0.matrix
            float[] r9 = r0.mapPoints
            r6.mapPoints(r9)
            float[] r6 = r0.mapPoints
            r9 = 0
            r10 = r6[r9]
            r12 = 1
            r6 = r6[r12]
            r12 = r6
            goto L_0x03ef
        L_0x03ed:
            r9 = 0
            r10 = 0
        L_0x03ef:
            android.graphics.Path r6 = r13.chartPath
            r6.moveTo(r10, r12)
            boolean[] r6 = r0.skipPoints
            r6[r7] = r9
            goto L_0x03fd
        L_0x03f9:
            r32 = r9
            r33 = r10
        L_0x03fd:
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            if (r6 != 0) goto L_0x0403
            r6 = 0
            goto L_0x0405
        L_0x0403:
            float r6 = r6.progress
        L_0x0405:
            r9 = 0
            int r10 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1))
            if (r10 != 0) goto L_0x0442
            if (r14 <= 0) goto L_0x0442
            int r9 = r14 + -1
            r9 = r28[r9]
            if (r9 != 0) goto L_0x0442
            r9 = r31
            if (r14 >= r9) goto L_0x0444
            int r10 = r14 + 1
            r10 = r28[r10]
            if (r10 != 0) goto L_0x0444
            int r10 = r0.transitionMode
            r12 = 2
            if (r10 == r12) goto L_0x0444
            boolean[] r10 = r0.skipPoints
            boolean r10 = r10[r7]
            if (r10 != 0) goto L_0x043a
            if (r7 != r2) goto L_0x0435
            android.graphics.Path r10 = r13.chartPath
            r12 = 1065353216(0x3var_, float:1.0)
            float r6 = r12 - r6
            float r3 = r3 * r6
            r10.lineTo(r1, r3)
            goto L_0x043a
        L_0x0435:
            android.graphics.Path r6 = r13.chartPath
            r6.lineTo(r1, r3)
        L_0x043a:
            boolean[] r1 = r0.skipPoints
            r3 = 1
            r1[r7] = r3
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0478
        L_0x0442:
            r9 = r31
        L_0x0444:
            boolean[] r10 = r0.skipPoints
            boolean r10 = r10[r7]
            if (r10 == 0) goto L_0x0460
            if (r7 != r2) goto L_0x0458
            android.graphics.Path r10 = r13.chartPath
            r12 = 1065353216(0x3var_, float:1.0)
            float r15 = r12 - r6
            float r3 = r3 * r15
            r10.lineTo(r1, r3)
            goto L_0x0462
        L_0x0458:
            r12 = 1065353216(0x3var_, float:1.0)
            android.graphics.Path r10 = r13.chartPath
            r10.lineTo(r1, r3)
            goto L_0x0462
        L_0x0460:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x0462:
            if (r7 != r2) goto L_0x046e
            android.graphics.Path r1 = r13.chartPath
            float r3 = r12 - r6
            float r3 = r3 * r8
            r1.lineTo(r11, r3)
            goto L_0x0473
        L_0x046e:
            android.graphics.Path r1 = r13.chartPath
            r1.lineTo(r11, r8)
        L_0x0473:
            boolean[] r1 = r0.skipPoints
            r3 = 0
            r1[r7] = r3
        L_0x0478:
            if (r14 != r9) goto L_0x05a3
            int r1 = r35.getMeasuredWidth()
            float r1 = (float) r1
            int r3 = r35.getMeasuredHeight()
            float r3 = (float) r3
            int r6 = r0.transitionMode
            r10 = 2
            if (r6 != r10) goto L_0x04b8
            if (r7 == r2) goto L_0x04b8
            float[] r6 = r0.mapPoints
            float r1 = r1 + r4
            r10 = 0
            r6[r10] = r1
            r1 = 1
            r6[r1] = r3
            android.graphics.Matrix r1 = r0.matrix
            r1.reset()
            android.graphics.Matrix r1 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r6 = r3.progress
            float[] r3 = r3.angle
            r3 = r3[r7]
            float r6 = r6 * r3
            r1.postRotate(r6, r4, r5)
            android.graphics.Matrix r1 = r0.matrix
            float[] r3 = r0.mapPoints
            r1.mapPoints(r3)
            float[] r1 = r0.mapPoints
            r3 = 0
            r6 = r1[r3]
            r3 = 1
            r1 = r1[r3]
            goto L_0x04bd
        L_0x04b8:
            android.graphics.Path r6 = r13.chartPath
            r6.lineTo(r1, r3)
        L_0x04bd:
            int r1 = r0.transitionMode
            r3 = 2
            if (r1 != r3) goto L_0x05a3
            if (r7 == r2) goto L_0x05a3
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float[] r3 = r1.startX
            r3 = r3[r7]
            float[] r1 = r1.startY
            r1 = r1[r7]
            float r3 = r4 - r3
            float r1 = r5 - r1
            float r1 = r1 / r3
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 <= 0) goto L_0x04e4
            r3 = r13
            double r12 = (double) r1
            double r12 = java.lang.Math.atan(r12)
            double r12 = -r12
            double r12 = java.lang.Math.toDegrees(r12)
            goto L_0x04f2
        L_0x04e4:
            r3 = r13
            float r1 = java.lang.Math.abs(r1)
            double r12 = (double) r1
            double r12 = java.lang.Math.atan(r12)
            double r12 = java.lang.Math.toDegrees(r12)
        L_0x04f2:
            float r1 = (float) r12
            float r1 = r1 - r30
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float[] r10 = r6.startX
            r10 = r10[r7]
            float[] r6 = r6.startY
            r6 = r6[r7]
            float[] r12 = r0.mapPoints
            r13 = 0
            r12[r13] = r10
            r10 = 1
            r12[r10] = r6
            android.graphics.Matrix r6 = r0.matrix
            r6.reset()
            android.graphics.Matrix r6 = r0.matrix
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r12 = r10.progress
            float r1 = r1 * r12
            float[] r10 = r10.angle
            r10 = r10[r7]
            float r12 = r12 * r10
            float r1 = r1 + r12
            r6.postRotate(r1, r4, r5)
            android.graphics.Matrix r1 = r0.matrix
            float[] r6 = r0.mapPoints
            r1.mapPoints(r6)
            float[] r1 = r0.mapPoints
            r6 = 0
            r10 = r1[r6]
            r12 = 1
            r1 = r1[r12]
            float r12 = r11 - r10
            float r12 = java.lang.Math.abs(r12)
            double r12 = (double) r12
            r30 = 4562254508917369340(0x3var_dd2f1a9fc, double:0.001)
            int r15 = (r12 > r30 ? 1 : (r12 == r30 ? 0 : -1))
            if (r15 >= 0) goto L_0x055e
            int r12 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r12 >= 0) goto L_0x0545
            int r12 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r12 < 0) goto L_0x054d
        L_0x0545:
            int r12 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r12 <= 0) goto L_0x055e
            int r12 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r12 <= 0) goto L_0x055e
        L_0x054d:
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float[] r1 = r1.angle
            r1 = r1[r7]
            r8 = -1020002304(0xffffffffCLASSNAME, float:-180.0)
            int r1 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x055b
            r1 = 0
            goto L_0x055c
        L_0x055b:
            r1 = 3
        L_0x055c:
            r11 = 0
            goto L_0x0566
        L_0x055e:
            int r11 = r0.quarterForPoint(r11, r8)
            int r1 = r0.quarterForPoint(r10, r1)
        L_0x0566:
            if (r11 > r1) goto L_0x05a4
            if (r11 != 0) goto L_0x0578
            android.graphics.Path r8 = r3.chartPath
            int r10 = r35.getMeasuredWidth()
            float r10 = (float) r10
            r12 = 0
            r8.lineTo(r10, r12)
        L_0x0575:
            r10 = 2
            r13 = 0
            goto L_0x05a0
        L_0x0578:
            r8 = 1
            if (r11 != r8) goto L_0x058b
            android.graphics.Path r8 = r3.chartPath
            int r10 = r35.getMeasuredWidth()
            float r10 = (float) r10
            int r12 = r35.getMeasuredHeight()
            float r12 = (float) r12
            r8.lineTo(r10, r12)
            goto L_0x0575
        L_0x058b:
            r10 = 2
            if (r11 != r10) goto L_0x059a
            android.graphics.Path r8 = r3.chartPath
            int r12 = r35.getMeasuredHeight()
            float r12 = (float) r12
            r13 = 0
            r8.lineTo(r13, r12)
            goto L_0x05a0
        L_0x059a:
            r13 = 0
            android.graphics.Path r8 = r3.chartPath
            r8.lineTo(r13, r13)
        L_0x05a0:
            int r11 = r11 + 1
            goto L_0x0566
        L_0x05a3:
            r6 = 0
        L_0x05a4:
            r10 = 2
            r13 = 0
            float r1 = r29 + r21
            r8 = r23
            r15 = r24
        L_0x05ac:
            int r7 = r7 + 1
            r6 = r1
            r21 = r2
            r11 = r9
            r10 = r25
            r2 = r26
            r3 = r27
            r9 = r32
            r12 = r33
            r1 = r36
            goto L_0x0194
        L_0x05c0:
            r26 = r2
            r27 = r3
            r23 = r8
            r32 = r9
            r9 = r11
            r33 = r12
            r24 = r15
            r6 = 0
            r10 = 2
            r13 = 0
            int r14 = r14 + 1
            r1 = r36
            r7 = r22
            r9 = r32
            r13 = 1
            goto L_0x0150
        L_0x05db:
            r22 = r7
            r36.save()
            int r1 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            float r1 = (float) r1
            int r2 = r35.getMeasuredHeight()
            int r3 = r0.chartBottom
            int r2 = r2 - r3
            float r2 = (float) r2
            r3 = r36
            r3.clipRect(r15, r1, r8, r2)
            if (r18 == 0) goto L_0x05fb
            java.lang.String r1 = "statisticChartLineEmpty"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r3.drawColor(r1)
        L_0x05fb:
            java.util.ArrayList<L> r1 = r0.lines
            int r1 = r1.size()
            r2 = 1
            int r1 = r1 - r2
        L_0x0603:
            if (r1 < 0) goto L_0x0625
            java.util.ArrayList<L> r2 = r0.lines
            java.lang.Object r2 = r2.get(r1)
            org.telegram.ui.Charts.view_data.LineViewData r2 = (org.telegram.ui.Charts.view_data.LineViewData) r2
            android.graphics.Paint r4 = r2.paint
            r7 = r22
            r4.setAlpha(r7)
            android.graphics.Path r4 = r2.chartPath
            android.graphics.Paint r5 = r2.paint
            r3.drawPath(r4, r5)
            android.graphics.Paint r2 = r2.paint
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            int r1 = r1 + -1
            goto L_0x0603
        L_0x0625:
            r36.restore()
            r36.restore()
        L_0x062b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawChart(android.graphics.Canvas):void");
    }

    private int quarterForPoint(float f, float f2) {
        float centerX = this.chartArea.centerX();
        float centerY = this.chartArea.centerY() + ((float) AndroidUtilities.dp(16.0f));
        if (f >= centerX && f2 <= centerY) {
            return 0;
        }
        if (f < centerX || f2 < centerY) {
            return (f >= centerX || f2 < centerY) ? 3 : 2;
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0112 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x015e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawPickerChart(android.graphics.Canvas r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            T r2 = r0.chartData
            if (r2 == 0) goto L_0x01a3
            java.util.ArrayList<L> r2 = r0.lines
            int r2 = r2.size()
            r4 = 0
        L_0x000f:
            if (r4 >= r2) goto L_0x0021
            java.util.ArrayList<L> r5 = r0.lines
            java.lang.Object r5 = r5.get(r4)
            org.telegram.ui.Charts.view_data.StackLinearViewData r5 = (org.telegram.ui.Charts.view_data.StackLinearViewData) r5
            android.graphics.Path r5 = r5.chartPathPicker
            r5.reset()
            int r4 = r4 + 1
            goto L_0x000f
        L_0x0021:
            T r2 = r0.chartData
            r4 = r2
            org.telegram.ui.Charts.data.StackLinearChartData r4 = (org.telegram.ui.Charts.data.StackLinearChartData) r4
            int r4 = r4.simplifiedSize
            boolean[] r5 = r0.skipPoints
            if (r5 == 0) goto L_0x0037
            int r5 = r5.length
            org.telegram.ui.Charts.data.StackLinearChartData r2 = (org.telegram.ui.Charts.data.StackLinearChartData) r2
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r2 = r2.lines
            int r2 = r2.size()
            if (r5 >= r2) goto L_0x0045
        L_0x0037:
            T r2 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r2 = (org.telegram.ui.Charts.data.StackLinearChartData) r2
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r2 = r2.lines
            int r2 = r2.size()
            boolean[] r2 = new boolean[r2]
            r0.skipPoints = r2
        L_0x0045:
            r2 = 0
            r5 = 0
        L_0x0047:
            r6 = 1
            if (r2 >= r4) goto L_0x017c
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
        L_0x004f:
            java.util.ArrayList<L> r12 = r0.lines
            int r12 = r12.size()
            if (r8 >= r12) goto L_0x008b
            java.util.ArrayList<L> r12 = r0.lines
            java.lang.Object r12 = r12.get(r8)
            org.telegram.ui.Charts.view_data.LineViewData r12 = (org.telegram.ui.Charts.view_data.LineViewData) r12
            boolean r13 = r12.enabled
            if (r13 != 0) goto L_0x006a
            float r13 = r12.alpha
            int r13 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x006a
            goto L_0x0088
        L_0x006a:
            T r11 = r0.chartData
            r13 = r11
            org.telegram.ui.Charts.data.StackLinearChartData r13 = (org.telegram.ui.Charts.data.StackLinearChartData) r13
            int[][] r13 = r13.simplifiedY
            r13 = r13[r8]
            r13 = r13[r2]
            if (r13 <= 0) goto L_0x0087
            org.telegram.ui.Charts.data.StackLinearChartData r11 = (org.telegram.ui.Charts.data.StackLinearChartData) r11
            int[][] r11 = r11.simplifiedY
            r11 = r11[r8]
            r11 = r11[r2]
            float r11 = (float) r11
            float r12 = r12.alpha
            float r11 = r11 * r12
            float r9 = r9 + r11
            int r10 = r10 + 1
        L_0x0087:
            r11 = r8
        L_0x0088:
            int r8 = r8 + 1
            goto L_0x004f
        L_0x008b:
            float r8 = (float) r2
            int r12 = r4 + -1
            float r13 = (float) r12
            float r8 = r8 / r13
            float r13 = r0.pickerWidth
            float r8 = r8 * r13
            r13 = 0
            r14 = 0
        L_0x0096:
            java.util.ArrayList<L> r15 = r0.lines
            int r15 = r15.size()
            if (r13 >= r15) goto L_0x0173
            java.util.ArrayList<L> r15 = r0.lines
            java.lang.Object r15 = r15.get(r13)
            org.telegram.ui.Charts.view_data.LineViewData r15 = (org.telegram.ui.Charts.view_data.LineViewData) r15
            boolean r3 = r15.enabled
            if (r3 != 0) goto L_0x00b8
            float r3 = r15.alpha
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 != 0) goto L_0x00b8
            r17 = r4
            r18 = r5
            r5 = 0
            r6 = 0
            goto L_0x0169
        L_0x00b8:
            if (r10 != r6) goto L_0x00ca
            T r3 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r3 = (org.telegram.ui.Charts.data.StackLinearChartData) r3
            int[][] r3 = r3.simplifiedY
            r3 = r3[r13]
            r3 = r3[r2]
            if (r3 != 0) goto L_0x00c7
            goto L_0x00ce
        L_0x00c7:
            float r3 = r15.alpha
            goto L_0x00e0
        L_0x00ca:
            int r3 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r3 != 0) goto L_0x00d0
        L_0x00ce:
            r3 = 0
            goto L_0x00e0
        L_0x00d0:
            T r3 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r3 = (org.telegram.ui.Charts.data.StackLinearChartData) r3
            int[][] r3 = r3.simplifiedY
            r3 = r3[r13]
            r3 = r3[r2]
            float r3 = (float) r3
            float r6 = r15.alpha
            float r3 = r3 * r6
            float r3 = r3 / r9
        L_0x00e0:
            int r6 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x00e7
            if (r13 != r11) goto L_0x00e7
            r5 = 1
        L_0x00e7:
            int r6 = r0.pikerHeight
            float r7 = (float) r6
            float r3 = r3 * r7
            float r7 = (float) r6
            float r7 = r7 - r3
            float r7 = r7 - r14
            r17 = r4
            if (r2 != 0) goto L_0x0102
            android.graphics.Path r4 = r15.chartPathPicker
            float r6 = (float) r6
            r18 = r5
            r5 = 0
            r4.moveTo(r5, r6)
            boolean[] r4 = r0.skipPoints
            r6 = 0
            r4[r13] = r6
            goto L_0x0105
        L_0x0102:
            r18 = r5
            r5 = 0
        L_0x0105:
            T r4 = r0.chartData
            r6 = r4
            org.telegram.ui.Charts.data.StackLinearChartData r6 = (org.telegram.ui.Charts.data.StackLinearChartData) r6
            int[][] r6 = r6.simplifiedY
            r6 = r6[r13]
            r6 = r6[r2]
            if (r6 != 0) goto L_0x0144
            if (r2 <= 0) goto L_0x0144
            r6 = r4
            org.telegram.ui.Charts.data.StackLinearChartData r6 = (org.telegram.ui.Charts.data.StackLinearChartData) r6
            int[][] r6 = r6.simplifiedY
            r6 = r6[r13]
            int r16 = r2 + -1
            r6 = r6[r16]
            if (r6 != 0) goto L_0x0144
            if (r2 >= r12) goto L_0x0144
            org.telegram.ui.Charts.data.StackLinearChartData r4 = (org.telegram.ui.Charts.data.StackLinearChartData) r4
            int[][] r4 = r4.simplifiedY
            r4 = r4[r13]
            int r6 = r2 + 1
            r4 = r4[r6]
            if (r4 != 0) goto L_0x0144
            boolean[] r4 = r0.skipPoints
            boolean r4 = r4[r13]
            if (r4 != 0) goto L_0x013d
            android.graphics.Path r4 = r15.chartPathPicker
            int r6 = r0.pikerHeight
            float r6 = (float) r6
            r4.lineTo(r8, r6)
        L_0x013d:
            boolean[] r4 = r0.skipPoints
            r6 = 1
            r4[r13] = r6
            r6 = 0
            goto L_0x015c
        L_0x0144:
            boolean[] r4 = r0.skipPoints
            boolean r4 = r4[r13]
            if (r4 == 0) goto L_0x0152
            android.graphics.Path r4 = r15.chartPathPicker
            int r6 = r0.pikerHeight
            float r6 = (float) r6
            r4.lineTo(r8, r6)
        L_0x0152:
            android.graphics.Path r4 = r15.chartPathPicker
            r4.lineTo(r8, r7)
            boolean[] r4 = r0.skipPoints
            r6 = 0
            r4[r13] = r6
        L_0x015c:
            if (r2 != r12) goto L_0x0168
            android.graphics.Path r4 = r15.chartPathPicker
            float r7 = r0.pickerWidth
            int r15 = r0.pikerHeight
            float r15 = (float) r15
            r4.lineTo(r7, r15)
        L_0x0168:
            float r14 = r14 + r3
        L_0x0169:
            int r13 = r13 + 1
            r4 = r17
            r5 = r18
            r6 = 1
            r7 = 0
            goto L_0x0096
        L_0x0173:
            r17 = r4
            r18 = r5
            r6 = 0
            int r2 = r2 + 1
            goto L_0x0047
        L_0x017c:
            if (r5 == 0) goto L_0x0187
            java.lang.String r2 = "statisticChartLineEmpty"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.drawColor(r2)
        L_0x0187:
            java.util.ArrayList<L> r2 = r0.lines
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
        L_0x018f:
            if (r2 < 0) goto L_0x01a3
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.Charts.view_data.LineViewData r3 = (org.telegram.ui.Charts.view_data.LineViewData) r3
            android.graphics.Path r4 = r3.chartPathPicker
            android.graphics.Paint r3 = r3.paint
            r1.drawPath(r4, r3)
            int r2 = r2 + -1
            goto L_0x018f
        L_0x01a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawPickerChart(android.graphics.Canvas):void");
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

    /* JADX WARNING: Removed duplicated region for block: B:41:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0142  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fillTransitionParams(org.telegram.ui.Charts.view_data.TransitionParams r18) {
        /*
            r17 = this;
            r0 = r17
            T r1 = r0.chartData
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            float r2 = r0.chartWidth
            org.telegram.ui.Charts.ChartPickerDelegate r3 = r0.pickerDelegate
            float r4 = r3.pickerEnd
            float r3 = r3.pickerStart
            float r4 = r4 - r3
            float r2 = r2 / r4
            float r3 = r3 * r2
            float r4 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r3 = r3 - r4
            r4 = r1
            org.telegram.ui.Charts.data.StackLinearChartData r4 = (org.telegram.ui.Charts.data.StackLinearChartData) r4
            float[] r4 = r4.xPercentage
            int r4 = r4.length
            r5 = 2
            r6 = 1
            if (r4 >= r5) goto L_0x0023
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x002b
        L_0x0023:
            org.telegram.ui.Charts.data.StackLinearChartData r1 = (org.telegram.ui.Charts.data.StackLinearChartData) r1
            float[] r1 = r1.xPercentage
            r1 = r1[r6]
            float r1 = r1 * r2
        L_0x002b:
            float r4 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r4 = r4 / r1
            int r1 = (int) r4
            int r1 = r1 + r6
            int r4 = r0.startXIndex
            int r4 = r4 - r1
            int r4 = r4 - r6
            r7 = 0
            int r4 = java.lang.Math.max(r7, r4)
            T r8 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r8 = (org.telegram.ui.Charts.data.StackLinearChartData) r8
            float[] r8 = r8.xPercentage
            int r8 = r8.length
            int r8 = r8 - r6
            int r9 = r0.endXIndex
            int r9 = r9 + r1
            int r9 = r9 + r6
            int r1 = java.lang.Math.min(r8, r9)
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            T r9 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r9 = (org.telegram.ui.Charts.data.StackLinearChartData) r9
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r9 = r9.lines
            int r9 = r9.size()
            float[] r9 = new float[r9]
            r8.startX = r9
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            T r9 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r9 = (org.telegram.ui.Charts.data.StackLinearChartData) r9
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r9 = r9.lines
            int r9 = r9.size()
            float[] r9 = new float[r9]
            r8.startY = r9
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            T r9 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r9 = (org.telegram.ui.Charts.data.StackLinearChartData) r9
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r9 = r9.lines
            int r9 = r9.size()
            float[] r9 = new float[r9]
            r8.endX = r9
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            T r9 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r9 = (org.telegram.ui.Charts.data.StackLinearChartData) r9
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r9 = r9.lines
            int r9 = r9.size()
            float[] r9 = new float[r9]
            r8.endY = r9
            org.telegram.ui.Charts.view_data.TransitionParams r8 = r0.transitionParams
            T r9 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r9 = (org.telegram.ui.Charts.data.StackLinearChartData) r9
            java.util.ArrayList<org.telegram.ui.Charts.data.ChartData$Line> r9 = r9.lines
            int r9 = r9.size()
            float[] r9 = new float[r9]
            r8.angle = r9
            r8 = 0
        L_0x009a:
            if (r8 >= r5) goto L_0x0159
            if (r8 != r6) goto L_0x00a0
            r9 = r1
            goto L_0x00a1
        L_0x00a0:
            r9 = r4
        L_0x00a1:
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
        L_0x00a5:
            java.util.ArrayList<L> r14 = r0.lines
            int r14 = r14.size()
            if (r11 >= r14) goto L_0x00d5
            java.util.ArrayList<L> r14 = r0.lines
            java.lang.Object r14 = r14.get(r11)
            org.telegram.ui.Charts.view_data.LineViewData r14 = (org.telegram.ui.Charts.view_data.LineViewData) r14
            boolean r15 = r14.enabled
            if (r15 != 0) goto L_0x00c0
            float r15 = r14.alpha
            int r15 = (r15 > r10 ? 1 : (r15 == r10 ? 0 : -1))
            if (r15 != 0) goto L_0x00c0
            goto L_0x00d2
        L_0x00c0:
            org.telegram.ui.Charts.data.ChartData$Line r15 = r14.line
            int[] r15 = r15.y
            r16 = r15[r9]
            if (r16 <= 0) goto L_0x00d2
            r15 = r15[r9]
            float r15 = (float) r15
            float r14 = r14.alpha
            float r15 = r15 * r14
            float r12 = r12 + r15
            int r13 = r13 + 1
        L_0x00d2:
            int r11 = r11 + 1
            goto L_0x00a5
        L_0x00d5:
            r11 = 0
            r14 = 0
        L_0x00d7:
            java.util.ArrayList<L> r15 = r0.lines
            int r15 = r15.size()
            if (r11 >= r15) goto L_0x0152
            java.util.ArrayList<L> r15 = r0.lines
            java.lang.Object r15 = r15.get(r11)
            org.telegram.ui.Charts.view_data.LineViewData r15 = (org.telegram.ui.Charts.view_data.LineViewData) r15
            boolean r5 = r15.enabled
            if (r5 != 0) goto L_0x00f2
            float r5 = r15.alpha
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x00f2
            goto L_0x014c
        L_0x00f2:
            org.telegram.ui.Charts.data.ChartData$Line r5 = r15.line
            int[] r5 = r5.y
            if (r13 != r6) goto L_0x0100
            r5 = r5[r9]
            if (r5 != 0) goto L_0x00fd
            goto L_0x0104
        L_0x00fd:
            float r5 = r15.alpha
            goto L_0x010e
        L_0x0100:
            int r16 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r16 != 0) goto L_0x0106
        L_0x0104:
            r5 = 0
            goto L_0x010e
        L_0x0106:
            r5 = r5[r9]
            float r5 = (float) r5
            float r15 = r15.alpha
            float r5 = r5 * r15
            float r5 = r5 / r12
        L_0x010e:
            T r15 = r0.chartData
            org.telegram.ui.Charts.data.StackLinearChartData r15 = (org.telegram.ui.Charts.data.StackLinearChartData) r15
            float[] r15 = r15.xPercentage
            r15 = r15[r9]
            float r15 = r15 * r2
            float r15 = r15 - r3
            int r16 = r17.getMeasuredHeight()
            int r6 = r0.chartBottom
            int r16 = r16 - r6
            int r6 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r6 = r16 - r6
            float r6 = (float) r6
            float r5 = r5 * r6
            int r6 = r17.getMeasuredHeight()
            int r7 = r0.chartBottom
            int r6 = r6 - r7
            float r6 = (float) r6
            float r6 = r6 - r5
            float r7 = (float) r14
            float r6 = r6 - r7
            float r7 = r7 + r5
            int r14 = (int) r7
            if (r8 != 0) goto L_0x0142
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float[] r7 = r5.startX
            r7[r11] = r15
            float[] r5 = r5.startY
            r5[r11] = r6
            goto L_0x014c
        L_0x0142:
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float[] r7 = r5.endX
            r7[r11] = r15
            float[] r5 = r5.endY
            r5[r11] = r6
        L_0x014c:
            int r11 = r11 + 1
            r5 = 2
            r6 = 1
            r7 = 0
            goto L_0x00d7
        L_0x0152:
            int r8 = r8 + 1
            r5 = 2
            r6 = 1
            r7 = 0
            goto L_0x009a
        L_0x0159:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.fillTransitionParams(org.telegram.ui.Charts.view_data.TransitionParams):void");
    }
}
