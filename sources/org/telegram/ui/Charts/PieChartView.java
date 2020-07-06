package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.PieLegendView;
import org.telegram.ui.Charts.view_data.TransitionParams;

public class PieChartView extends StackLinearChartView<PieChartViewData> {
    float MAX_TEXT_SIZE = ((float) AndroidUtilities.dp(13.0f));
    float MIN_TEXT_SIZE = ((float) AndroidUtilities.dp(9.0f));
    int currentSelection = -1;
    float[] darawingValuesPercentage;
    float emptyDataAlpha = 1.0f;
    boolean isEmpty;
    int lastEndIndex = -1;
    int lastStartIndex = -1;
    String[] lookupTable = new String[101];
    int oldW = 0;
    PieLegendView pieLegendView;
    RectF rectF = new RectF();
    float sum;
    TextPaint textPaint;
    float[] values;

    /* access modifiers changed from: protected */
    public void drawBottomLine(Canvas canvas) {
    }

    /* access modifiers changed from: package-private */
    public void drawBottomSignature(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
    }

    public PieChartView(Context context) {
        super(context);
        for (int i = 1; i <= 100; i++) {
            String[] strArr = this.lookupTable;
            strArr[i] = i + "%";
        }
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setColor(-1);
        this.textPaint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.canCaptureChartSelection = true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0214  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawChart(android.graphics.Canvas r27) {
        /*
            r26 = this;
            r0 = r26
            r7 = r27
            T r1 = r0.chartData
            if (r1 != 0) goto L_0x0009
            return
        L_0x0009:
            if (r7 == 0) goto L_0x000e
            r27.save()
        L_0x000e:
            int r1 = r0.transitionMode
            r8 = 255(0xff, float:3.57E-43)
            r9 = 1
            if (r1 != r9) goto L_0x0021
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float r1 = r1.progress
            float r1 = r1 * r1
            r2 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r2
            int r1 = (int) r1
            goto L_0x0023
        L_0x0021:
            r1 = 255(0xff, float:3.57E-43)
        L_0x0023:
            boolean r2 = r0.isEmpty
            r3 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            if (r2 == 0) goto L_0x0040
            float r2 = r0.emptyDataAlpha
            int r4 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r4 == 0) goto L_0x0052
            float r2 = r2 - r3
            r0.emptyDataAlpha = r2
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 >= 0) goto L_0x003c
            r0.emptyDataAlpha = r11
        L_0x003c:
            r26.invalidate()
            goto L_0x0052
        L_0x0040:
            float r2 = r0.emptyDataAlpha
            int r4 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r4 == 0) goto L_0x0052
            float r2 = r2 + r3
            r0.emptyDataAlpha = r2
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x004f
            r0.emptyDataAlpha = r10
        L_0x004f:
            r26.invalidate()
        L_0x0052:
            float r1 = (float) r1
            float r2 = r0.emptyDataAlpha
            float r1 = r1 * r2
            int r12 = (int) r1
            r1 = 1053609165(0x3ecccccd, float:0.4)
            r3 = 1058642330(0x3var_a, float:0.6)
            float r2 = r2 * r3
            float r2 = r2 + r1
            if (r7 == 0) goto L_0x0072
            android.graphics.RectF r1 = r0.chartArea
            float r1 = r1.centerX()
            android.graphics.RectF r3 = r0.chartArea
            float r3 = r3.centerY()
            r7.scale(r2, r2, r1, r3)
        L_0x0072:
            android.graphics.RectF r1 = r0.chartArea
            float r1 = r1.width()
            android.graphics.RectF r2 = r0.chartArea
            float r2 = r2.height()
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0089
            android.graphics.RectF r1 = r0.chartArea
            float r1 = r1.height()
            goto L_0x008f
        L_0x0089:
            android.graphics.RectF r1 = r0.chartArea
            float r1 = r1.width()
        L_0x008f:
            r2 = 1055286886(0x3ee66666, float:0.45)
            float r1 = r1 * r2
            int r1 = (int) r1
            android.graphics.RectF r2 = r0.rectF
            android.graphics.RectF r3 = r0.chartArea
            float r3 = r3.centerX()
            float r1 = (float) r1
            float r3 = r3 - r1
            android.graphics.RectF r4 = r0.chartArea
            float r4 = r4.centerY()
            r5 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            float r4 = r4 + r6
            float r4 = r4 - r1
            android.graphics.RectF r6 = r0.chartArea
            float r6 = r6.centerX()
            float r6 = r6 + r1
            android.graphics.RectF r13 = r0.chartArea
            float r13 = r13.centerY()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r13 = r13 + r5
            float r13 = r13 + r1
            r2.set(r3, r4, r6, r13)
            java.util.ArrayList<L> r1 = r0.lines
            int r13 = r1.size()
            r1 = 0
            r15 = 0
        L_0x00cd:
            if (r1 >= r13) goto L_0x00e9
            java.util.ArrayList<L> r2 = r0.lines
            java.lang.Object r2 = r2.get(r1)
            org.telegram.ui.Charts.PieChartViewData r2 = (org.telegram.ui.Charts.PieChartViewData) r2
            float r2 = r2.drawingPart
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r1)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            float r3 = r3.alpha
            float r2 = r2 * r3
            float r15 = r15 + r2
            int r1 = r1 + 1
            goto L_0x00cd
        L_0x00e9:
            int r1 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x00f3
            if (r7 == 0) goto L_0x00f2
            r27.restore()
        L_0x00f2:
            return
        L_0x00f3:
            r16 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r6 = 0
            r17 = -1028390912(0xffffffffc2b40000, float:-90.0)
        L_0x00f8:
            r1 = 1073741824(0x40000000, float:2.0)
            r2 = 1090519040(0x41000000, float:8.0)
            r18 = 1135869952(0x43b40000, float:360.0)
            if (r6 >= r13) goto L_0x022f
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            float r3 = r3.alpha
            int r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r3 > 0) goto L_0x011b
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            boolean r3 = r3.enabled
            if (r3 != 0) goto L_0x011b
            goto L_0x0147
        L_0x011b:
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            android.graphics.Paint r3 = r3.paint
            r3.setAlpha(r12)
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r6)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            float r3 = r3.drawingPart
            float r3 = r3 / r15
            java.util.ArrayList<L> r4 = r0.lines
            java.lang.Object r4 = r4.get(r6)
            org.telegram.ui.Charts.PieChartViewData r4 = (org.telegram.ui.Charts.PieChartViewData) r4
            float r4 = r4.alpha
            float r19 = r3 * r4
            float[] r3 = r0.darawingValuesPercentage
            r3[r6] = r19
            int r3 = (r19 > r11 ? 1 : (r19 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x014e
        L_0x0147:
            r14 = r6
            r24 = r12
            r23 = r15
            goto L_0x0226
        L_0x014e:
            if (r7 == 0) goto L_0x0153
            r27.save()
        L_0x0153:
            float r1 = r19 / r1
            float r1 = r1 * r18
            float r1 = r17 + r1
            double r3 = (double) r1
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r6)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            float r1 = r1.selectionA
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x01b3
            androidx.interpolator.view.animation.FastOutSlowInInterpolator r1 = org.telegram.ui.Charts.BaseChartView.INTERPOLATOR
            java.util.ArrayList<L> r5 = r0.lines
            java.lang.Object r5 = r5.get(r6)
            org.telegram.ui.Charts.PieChartViewData r5 = (org.telegram.ui.Charts.PieChartViewData) r5
            float r5 = r5.selectionA
            float r1 = r1.getInterpolation(r5)
            if (r7 == 0) goto L_0x01b3
            double r20 = java.lang.Math.toRadians(r3)
            double r20 = java.lang.Math.cos(r20)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r23 = r15
            double r14 = (double) r5
            java.lang.Double.isNaN(r14)
            double r20 = r20 * r14
            double r14 = (double) r1
            java.lang.Double.isNaN(r14)
            r24 = r12
            double r11 = r20 * r14
            float r1 = (float) r11
            double r3 = java.lang.Math.toRadians(r3)
            double r3 = java.lang.Math.sin(r3)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r11 = (double) r2
            java.lang.Double.isNaN(r11)
            double r3 = r3 * r11
            java.lang.Double.isNaN(r14)
            double r3 = r3 * r14
            float r2 = (float) r3
            r7.translate(r1, r2)
            goto L_0x01b7
        L_0x01b3:
            r24 = r12
            r23 = r15
        L_0x01b7:
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r6)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r1 = r1.paint
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.FILL_AND_STROKE
            r1.setStyle(r2)
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r6)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r1 = r1.paint
            r1.setStrokeWidth(r10)
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r6)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r1 = r1.paint
            boolean r2 = org.telegram.ui.Charts.BaseChartView.USE_LINES
            r2 = r2 ^ r9
            r1.setAntiAlias(r2)
            if (r7 == 0) goto L_0x0214
            int r1 = r0.transitionMode
            if (r1 == r9) goto L_0x0214
            android.graphics.RectF r2 = r0.rectF
            float r4 = r19 * r18
            r5 = 1
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r6)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r11 = r1.paint
            r1 = r27
            r3 = r17
            r14 = r6
            r6 = r11
            r1.drawArc(r2, r3, r4, r5, r6)
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r14)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r1 = r1.paint
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.STROKE
            r1.setStyle(r2)
            r27.restore()
            goto L_0x0215
        L_0x0214:
            r14 = r6
        L_0x0215:
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r14)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r1 = r1.paint
            r1.setAlpha(r8)
            float r19 = r19 * r18
            float r17 = r17 + r19
        L_0x0226:
            int r6 = r14 + 1
            r15 = r23
            r12 = r24
            r11 = 0
            goto L_0x00f8
        L_0x022f:
            r24 = r12
            r23 = r15
            if (r7 == 0) goto L_0x03a0
            r14 = 0
        L_0x0236:
            if (r14 >= r13) goto L_0x039c
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r14)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            float r3 = r3.alpha
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 > 0) goto L_0x025c
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r14)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            boolean r3 = r3.enabled
            if (r3 != 0) goto L_0x025c
            r5 = r7
            r8 = r24
            r2 = 255(0xff, float:3.57E-43)
            r4 = 1073741824(0x40000000, float:2.0)
            goto L_0x038d
        L_0x025c:
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r14)
            org.telegram.ui.Charts.PieChartViewData r3 = (org.telegram.ui.Charts.PieChartViewData) r3
            float r3 = r3.drawingPart
            java.util.ArrayList<L> r4 = r0.lines
            java.lang.Object r4 = r4.get(r14)
            org.telegram.ui.Charts.PieChartViewData r4 = (org.telegram.ui.Charts.PieChartViewData) r4
            float r4 = r4.alpha
            float r3 = r3 * r4
            float r3 = r3 / r23
            r27.save()
            float r4 = r3 / r1
            float r4 = r4 * r18
            float r4 = r16 + r4
            double r4 = (double) r4
            java.util.ArrayList<L> r6 = r0.lines
            java.lang.Object r6 = r6.get(r14)
            org.telegram.ui.Charts.PieChartViewData r6 = (org.telegram.ui.Charts.PieChartViewData) r6
            float r6 = r6.selectionA
            r9 = 0
            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x02d1
            androidx.interpolator.view.animation.FastOutSlowInInterpolator r6 = org.telegram.ui.Charts.BaseChartView.INTERPOLATOR
            java.util.ArrayList<L> r11 = r0.lines
            java.lang.Object r11 = r11.get(r14)
            org.telegram.ui.Charts.PieChartViewData r11 = (org.telegram.ui.Charts.PieChartViewData) r11
            float r11 = r11.selectionA
            float r6 = r6.getInterpolation(r11)
            double r11 = java.lang.Math.toRadians(r4)
            double r11 = java.lang.Math.cos(r11)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r8 = (double) r15
            java.lang.Double.isNaN(r8)
            double r11 = r11 * r8
            double r8 = (double) r6
            java.lang.Double.isNaN(r8)
            double r11 = r11 * r8
            float r6 = (float) r11
            double r11 = java.lang.Math.toRadians(r4)
            double r11 = java.lang.Math.sin(r11)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r2)
            double r1 = (double) r15
            java.lang.Double.isNaN(r1)
            double r11 = r11 * r1
            java.lang.Double.isNaN(r8)
            double r11 = r11 * r8
            float r1 = (float) r11
            r7.translate(r6, r1)
        L_0x02d1:
            r1 = 1120403456(0x42CLASSNAME, float:100.0)
            float r1 = r1 * r3
            int r1 = (int) r1
            r2 = 1017370378(0x3ca3d70a, float:0.02)
            int r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0371
            if (r1 <= 0) goto L_0x0371
            r2 = 100
            if (r1 > r2) goto L_0x0371
            android.graphics.RectF r2 = r0.rectF
            float r2 = r2.width()
            r6 = 1054280253(0x3ed70a3d, float:0.42)
            float r2 = r2 * r6
            double r8 = (double) r2
            float r2 = r10 - r3
            double r11 = (double) r2
            double r11 = java.lang.Math.sqrt(r11)
            java.lang.Double.isNaN(r8)
            double r8 = r8 * r11
            float r2 = (float) r8
            android.text.TextPaint r6 = r0.textPaint
            float r8 = r0.MIN_TEXT_SIZE
            float r9 = r0.MAX_TEXT_SIZE
            float r9 = r9 * r3
            float r8 = r8 + r9
            r6.setTextSize(r8)
            android.text.TextPaint r6 = r0.textPaint
            r8 = r24
            float r9 = (float) r8
            java.util.ArrayList<L> r11 = r0.lines
            java.lang.Object r11 = r11.get(r14)
            org.telegram.ui.Charts.PieChartViewData r11 = (org.telegram.ui.Charts.PieChartViewData) r11
            float r11 = r11.alpha
            float r9 = r9 * r11
            int r9 = (int) r9
            r6.setAlpha(r9)
            java.lang.String[] r6 = r0.lookupTable
            r1 = r6[r1]
            android.graphics.RectF r6 = r0.rectF
            float r6 = r6.centerX()
            double r11 = (double) r6
            r21 = r11
            double r10 = (double) r2
            double r24 = java.lang.Math.toRadians(r4)
            double r24 = java.lang.Math.cos(r24)
            java.lang.Double.isNaN(r10)
            double r24 = r24 * r10
            java.lang.Double.isNaN(r21)
            double r6 = r21 + r24
            float r6 = (float) r6
            android.graphics.RectF r7 = r0.rectF
            float r7 = r7.centerY()
            r9 = r3
            double r2 = (double) r7
            double r4 = java.lang.Math.toRadians(r4)
            double r4 = java.lang.Math.sin(r4)
            java.lang.Double.isNaN(r10)
            double r10 = r10 * r4
            java.lang.Double.isNaN(r2)
            double r2 = r2 + r10
            float r2 = (float) r2
            android.text.TextPaint r3 = r0.textPaint
            float r3 = r3.descent()
            android.text.TextPaint r4 = r0.textPaint
            float r4 = r4.ascent()
            float r3 = r3 + r4
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r4
            float r2 = r2 - r3
            android.text.TextPaint r3 = r0.textPaint
            r5 = r27
            r5.drawText(r1, r6, r2, r3)
            goto L_0x0377
        L_0x0371:
            r9 = r3
            r5 = r7
            r8 = r24
            r4 = 1073741824(0x40000000, float:2.0)
        L_0x0377:
            r27.restore()
            java.util.ArrayList<L> r1 = r0.lines
            java.lang.Object r1 = r1.get(r14)
            org.telegram.ui.Charts.PieChartViewData r1 = (org.telegram.ui.Charts.PieChartViewData) r1
            android.graphics.Paint r1 = r1.paint
            r2 = 255(0xff, float:3.57E-43)
            r1.setAlpha(r2)
            float r3 = r9 * r18
            float r16 = r16 + r3
        L_0x038d:
            int r14 = r14 + 1
            r7 = r5
            r24 = r8
            r1 = 1073741824(0x40000000, float:2.0)
            r2 = 1090519040(0x41000000, float:8.0)
            r8 = 255(0xff, float:3.57E-43)
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x0236
        L_0x039c:
            r5 = r7
            r27.restore()
        L_0x03a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.PieChartView.drawChart(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        float f;
        int i;
        float f2;
        T t = this.chartData;
        if (t != null) {
            int length = ((StackLinearChartData) t).xPercentage.length;
            int size = this.lines.size();
            for (int i2 = 0; i2 < this.lines.size(); i2++) {
                ((LineViewData) this.lines.get(i2)).linesPathBottomSize = 0;
            }
            float length2 = (1.0f / ((float) ((StackLinearChartData) this.chartData).xPercentage.length)) * this.pickerWidth;
            for (int i3 = 0; i3 < length; i3++) {
                float f3 = (length2 / 2.0f) + (((StackLinearChartData) this.chartData).xPercentage[i3] * (this.pickerWidth - length2));
                float f4 = 0.0f;
                int i4 = 1;
                float f5 = 0.0f;
                int i5 = 0;
                boolean z = true;
                for (int i6 = 0; i6 < size; i6++) {
                    LineViewData lineViewData = (LineViewData) this.lines.get(i6);
                    if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                        float f6 = ((float) lineViewData.line.y[i3]) * lineViewData.alpha;
                        f5 += f6;
                        if (f6 > 0.0f) {
                            i5++;
                            if (lineViewData.enabled) {
                                z = false;
                            }
                        }
                    }
                }
                int i7 = 0;
                float f7 = 0.0f;
                while (i7 < size) {
                    LineViewData lineViewData2 = (LineViewData) this.lines.get(i7);
                    if (lineViewData2.enabled || lineViewData2.alpha != f4) {
                        int[] iArr = lineViewData2.line.y;
                        if (i5 == i4) {
                            if (iArr[i3] != 0) {
                                f2 = lineViewData2.alpha;
                                int i8 = this.pikerHeight;
                                float f8 = f2 * ((float) i8);
                                float[] fArr = lineViewData2.linesPath;
                                int i9 = lineViewData2.linesPathBottomSize;
                                i = length;
                                int i10 = i9 + 1;
                                lineViewData2.linesPathBottomSize = i10;
                                fArr[i9] = f3;
                                int i11 = i10 + 1;
                                lineViewData2.linesPathBottomSize = i11;
                                f = f5;
                                fArr[i10] = (((float) i8) - f8) - f7;
                                int i12 = i11 + 1;
                                lineViewData2.linesPathBottomSize = i12;
                                fArr[i11] = f3;
                                lineViewData2.linesPathBottomSize = i12 + 1;
                                fArr[i12] = ((float) i8) - f7;
                                f7 += f8;
                            }
                        } else if (f5 != f4) {
                            if (z) {
                                float f9 = lineViewData2.alpha;
                                f2 = (((float) iArr[i3]) / f5) * f9 * f9;
                            } else {
                                f2 = lineViewData2.alpha * (((float) iArr[i3]) / f5);
                            }
                            int i82 = this.pikerHeight;
                            float var_ = f2 * ((float) i82);
                            float[] fArr2 = lineViewData2.linesPath;
                            int i92 = lineViewData2.linesPathBottomSize;
                            i = length;
                            int i102 = i92 + 1;
                            lineViewData2.linesPathBottomSize = i102;
                            fArr2[i92] = f3;
                            int i112 = i102 + 1;
                            lineViewData2.linesPathBottomSize = i112;
                            f = f5;
                            fArr2[i102] = (((float) i82) - var_) - f7;
                            int i122 = i112 + 1;
                            lineViewData2.linesPathBottomSize = i122;
                            fArr2[i112] = f3;
                            lineViewData2.linesPathBottomSize = i122 + 1;
                            fArr2[i122] = ((float) i82) - f7;
                            f7 += var_;
                        }
                        f2 = 0.0f;
                        int i822 = this.pikerHeight;
                        float var_ = f2 * ((float) i822);
                        float[] fArr22 = lineViewData2.linesPath;
                        int i922 = lineViewData2.linesPathBottomSize;
                        i = length;
                        int i1022 = i922 + 1;
                        lineViewData2.linesPathBottomSize = i1022;
                        fArr22[i922] = f3;
                        int i1122 = i1022 + 1;
                        lineViewData2.linesPathBottomSize = i1122;
                        f = f5;
                        fArr22[i1022] = (((float) i822) - var_) - f7;
                        int i1222 = i1122 + 1;
                        lineViewData2.linesPathBottomSize = i1222;
                        fArr22[i1122] = f3;
                        lineViewData2.linesPathBottomSize = i1222 + 1;
                        fArr22[i1222] = ((float) i822) - f7;
                        f7 += var_;
                    } else {
                        i = length;
                        f = f5;
                    }
                    i7++;
                    length = i;
                    f5 = f;
                    f4 = 0.0f;
                    i4 = 1;
                }
                int i13 = length;
            }
            for (int i14 = 0; i14 < size; i14++) {
                LineViewData lineViewData3 = (LineViewData) this.lines.get(i14);
                lineViewData3.paint.setStrokeWidth(length2);
                lineViewData3.paint.setAlpha(255);
                lineViewData3.paint.setAntiAlias(false);
                canvas.drawLines(lineViewData3.linesPath, 0, lineViewData3.linesPathBottomSize, lineViewData3.paint);
            }
        }
    }

    public void setData(StackLinearChartData stackLinearChartData) {
        super.setData(stackLinearChartData);
        if (stackLinearChartData != null) {
            this.values = new float[stackLinearChartData.lines.size()];
            this.darawingValuesPercentage = new float[stackLinearChartData.lines.size()];
            onPickerDataChanged(false, true, false);
        }
    }

    public PieChartViewData createLineViewData(ChartData.Line line) {
        return new PieChartViewData(line);
    }

    /* access modifiers changed from: protected */
    public void selectXOnChart(int i, int i2) {
        if (this.chartData != null && !this.isEmpty) {
            float degrees = (float) (Math.toDegrees(Math.atan2((double) ((this.chartArea.centerY() + ((float) AndroidUtilities.dp(16.0f))) - ((float) i2)), (double) (this.chartArea.centerX() - ((float) i)))) - 90.0d);
            float f = 0.0f;
            if (degrees < 0.0f) {
                double d = (double) degrees;
                Double.isNaN(d);
                degrees = (float) (d + 360.0d);
            }
            float f2 = degrees / 360.0f;
            int i3 = -1;
            int i4 = 0;
            float f3 = 0.0f;
            while (true) {
                if (i4 >= this.lines.size()) {
                    f3 = 0.0f;
                    break;
                }
                if (((PieChartViewData) this.lines.get(i4)).enabled || ((PieChartViewData) this.lines.get(i4)).alpha != 0.0f) {
                    if (f2 > f3) {
                        float[] fArr = this.darawingValuesPercentage;
                        if (f2 < fArr[i4] + f3) {
                            f = f3 + fArr[i4];
                            i3 = i4;
                            break;
                        }
                    }
                    f3 += this.darawingValuesPercentage[i4];
                }
                i4++;
            }
            if (this.currentSelection != i3 && i3 >= 0) {
                this.currentSelection = i3;
                invalidate();
                this.pieLegendView.setVisibility(0);
                LineViewData lineViewData = (LineViewData) this.lines.get(i3);
                this.pieLegendView.setData(lineViewData.line.name, (int) this.values[this.currentSelection], lineViewData.lineColor);
                this.pieLegendView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
                double centerX = (double) this.rectF.centerX();
                double width = (double) (this.rectF.width() / 2.0f);
                double d2 = (double) ((f * 360.0f) - 90.0f);
                double cos = Math.cos(Math.toRadians(d2));
                Double.isNaN(width);
                Double.isNaN(centerX);
                double d3 = centerX + (cos * width);
                double centerX2 = (double) this.rectF.centerX();
                double d4 = (double) ((f3 * 360.0f) - 90.0f);
                double cos2 = Math.cos(Math.toRadians(d4));
                Double.isNaN(width);
                Double.isNaN(centerX2);
                int min = (int) Math.min(d3, centerX2 + (cos2 * width));
                if (min < 0) {
                    min = 0;
                }
                if (this.pieLegendView.getMeasuredWidth() + min > getMeasuredWidth() - AndroidUtilities.dp(16.0f)) {
                    min -= (this.pieLegendView.getMeasuredWidth() + min) - (getMeasuredWidth() - AndroidUtilities.dp(16.0f));
                }
                double centerY = (double) this.rectF.centerY();
                double sin = Math.sin(Math.toRadians(d4));
                Double.isNaN(width);
                Double.isNaN(centerY);
                double d5 = centerY + (sin * width);
                double centerY2 = (double) this.rectF.centerY();
                double sin2 = Math.sin(Math.toRadians(d2));
                Double.isNaN(width);
                Double.isNaN(centerY2);
                int min2 = ((int) Math.min(this.rectF.centerY(), (float) ((int) Math.min(d5, centerY2 + (width * sin2))))) - AndroidUtilities.dp(50.0f);
                this.pieLegendView.setTranslationX((float) min);
                this.pieLegendView.setTranslationY((float) min2);
                if (!(Build.VERSION.SDK_INT >= 27 ? performHapticFeedback(9, 2) : false)) {
                    performHapticFeedback(3, 2);
                }
            }
            moveLegend();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.chartData != null) {
            for (int i = 0; i < this.lines.size(); i++) {
                if (i == this.currentSelection) {
                    if (((PieChartViewData) this.lines.get(i)).selectionA < 1.0f) {
                        ((PieChartViewData) this.lines.get(i)).selectionA += 0.1f;
                        if (((PieChartViewData) this.lines.get(i)).selectionA > 1.0f) {
                            ((PieChartViewData) this.lines.get(i)).selectionA = 1.0f;
                        }
                        invalidate();
                    }
                } else if (((PieChartViewData) this.lines.get(i)).selectionA > 0.0f) {
                    ((PieChartViewData) this.lines.get(i)).selectionA -= 0.1f;
                    if (((PieChartViewData) this.lines.get(i)).selectionA < 0.0f) {
                        ((PieChartViewData) this.lines.get(i)).selectionA = 0.0f;
                    }
                    invalidate();
                }
            }
        }
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onActionUp() {
        this.currentSelection = -1;
        this.pieLegendView.setVisibility(8);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredWidth() != this.oldW) {
            this.oldW = getMeasuredWidth();
            int height = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
            this.MIN_TEXT_SIZE = (float) (height / 13);
            this.MAX_TEXT_SIZE = (float) (height / 7);
        }
    }

    public void updatePicker(ChartData chartData, long j) {
        float f;
        int length = chartData.x.length;
        long j2 = j - (j % 86400000);
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (j2 >= chartData.x[i2]) {
                i = i2;
            }
        }
        if (chartData.xPercentage.length < 2) {
            f = 0.5f;
        } else {
            f = 1.0f / ((float) chartData.x.length);
        }
        if (i == 0) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            chartPickerDelegate.pickerStart = 0.0f;
            chartPickerDelegate.pickerEnd = f;
        } else if (i >= chartData.x.length - 1) {
            ChartPickerDelegate chartPickerDelegate2 = this.pickerDelegate;
            chartPickerDelegate2.pickerStart = 1.0f - f;
            chartPickerDelegate2.pickerEnd = 1.0f;
        } else {
            ChartPickerDelegate chartPickerDelegate3 = this.pickerDelegate;
            float f2 = ((float) i) * f;
            chartPickerDelegate3.pickerStart = f2;
            float f3 = f2 + f;
            chartPickerDelegate3.pickerEnd = f3;
            if (f3 > 1.0f) {
                chartPickerDelegate3.pickerEnd = 1.0f;
            }
            onPickerDataChanged(true, true, false);
        }
    }

    /* access modifiers changed from: protected */
    public LegendSignatureView createLegendView() {
        PieLegendView pieLegendView2 = new PieLegendView(getContext());
        this.pieLegendView = pieLegendView2;
        return pieLegendView2;
    }

    public void onPickerDataChanged(boolean z, boolean z2, boolean z3) {
        super.onPickerDataChanged(z, z2, z3);
        T t = this.chartData;
        if (t != null && ((StackLinearChartData) t).xPercentage != null) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            updateCharValues(chartPickerDelegate.pickerStart, chartPickerDelegate.pickerEnd, z2);
        }
    }

    private void updateCharValues(float f, float f2, boolean z) {
        float f3;
        if (this.values != null) {
            int length = ((StackLinearChartData) this.chartData).xPercentage.length;
            int size = this.lines.size();
            int i = 0;
            int i2 = -1;
            int i3 = -1;
            for (int i4 = 0; i4 < length; i4++) {
                if (((StackLinearChartData) this.chartData).xPercentage[i4] >= f && i3 == -1) {
                    i3 = i4;
                }
                if (((StackLinearChartData) this.chartData).xPercentage[i4] <= f2) {
                    i2 = i4;
                }
            }
            if (i2 < i3) {
                i3 = i2;
            }
            if (z || this.lastEndIndex != i2 || this.lastStartIndex != i3) {
                this.lastEndIndex = i2;
                this.lastStartIndex = i3;
                this.isEmpty = true;
                this.sum = 0.0f;
                for (int i5 = 0; i5 < size; i5++) {
                    this.values[i5] = 0.0f;
                }
                while (i3 <= i2) {
                    for (int i6 = 0; i6 < size; i6++) {
                        float[] fArr = this.values;
                        fArr[i6] = fArr[i6] + ((float) ((StackLinearChartData) this.chartData).lines.get(i6).y[i3]);
                        this.sum += (float) ((StackLinearChartData) this.chartData).lines.get(i6).y[i3];
                        if (this.isEmpty && ((PieChartViewData) this.lines.get(i6)).enabled && ((StackLinearChartData) this.chartData).lines.get(i6).y[i3] > 0) {
                            this.isEmpty = false;
                        }
                    }
                    i3++;
                }
                if (!z) {
                    while (i < size) {
                        PieChartViewData pieChartViewData = (PieChartViewData) this.lines.get(i);
                        Animator animator = pieChartViewData.animator;
                        if (animator != null) {
                            animator.cancel();
                        }
                        float f4 = this.sum;
                        if (f4 == 0.0f) {
                            f3 = 0.0f;
                        } else {
                            f3 = this.values[i] / f4;
                        }
                        ValueAnimator createAnimator = createAnimator(pieChartViewData.drawingPart, f3, new ValueAnimator.AnimatorUpdateListener(pieChartViewData) {
                            public final /* synthetic */ PieChartViewData f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                PieChartView.this.lambda$updateCharValues$0$PieChartView(this.f$1, valueAnimator);
                            }
                        });
                        pieChartViewData.animator = createAnimator;
                        createAnimator.start();
                        i++;
                    }
                    return;
                }
                while (i < size) {
                    if (this.sum == 0.0f) {
                        ((PieChartViewData) this.lines.get(i)).drawingPart = 0.0f;
                    } else {
                        ((PieChartViewData) this.lines.get(i)).drawingPart = this.values[i] / this.sum;
                    }
                    i++;
                }
            }
        }
    }

    public /* synthetic */ void lambda$updateCharValues$0$PieChartView(PieChartViewData pieChartViewData, ValueAnimator valueAnimator) {
        pieChartViewData.drawingPart = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void onPickerJumpTo(float f, float f2, boolean z) {
        if (this.chartData != null) {
            if (z) {
                updateCharValues(f, f2, false);
                return;
            }
            updateIndexes();
            invalidate();
        }
    }

    public void fillTransitionParams(TransitionParams transitionParams) {
        drawChart((Canvas) null);
        float f = 0.0f;
        int i = 0;
        while (true) {
            float[] fArr = this.darawingValuesPercentage;
            if (i < fArr.length) {
                f += fArr[i];
                transitionParams.angle[i] = (360.0f * f) - 180.0f;
                i++;
            } else {
                return;
            }
        }
    }
}
