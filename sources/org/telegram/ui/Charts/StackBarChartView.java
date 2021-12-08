package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.SegmentTree;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackBarChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.StackBarViewData;

public class StackBarChartView extends BaseChartView<StackBarChartData, StackBarViewData> {
    private int[] yMaxPoints;

    public StackBarChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
    }

    public StackBarViewData createLineViewData(ChartData.Line line) {
        return new StackBarViewData(line);
    }

    /* access modifiers changed from: protected */
    public void drawChart(Canvas canvas) {
        float lineWidth;
        float p;
        float transitionAlpha;
        int localEnd;
        int localStart;
        int additionalPoints;
        Canvas canvas2 = canvas;
        if (this.chartData != null) {
            float fullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset = (this.pickerDelegate.pickerStart * fullWidth) - HORIZONTAL_PADDING;
            boolean z = true;
            if (((StackBarChartData) this.chartData).xPercentage.length < 2) {
                p = 1.0f;
                lineWidth = 1.0f;
            } else {
                float p2 = ((StackBarChartData) this.chartData).xPercentage[1] * fullWidth;
                p = p2;
                lineWidth = ((StackBarChartData) this.chartData).xPercentage[1] * (fullWidth - p2);
            }
            int additionalPoints2 = ((int) (HORIZONTAL_PADDING / p)) + 1;
            int localStart2 = Math.max(0, (this.startXIndex - additionalPoints2) - 2);
            int localEnd2 = Math.min(((StackBarChartData) this.chartData).xPercentage.length - 1, this.endXIndex + additionalPoints2 + 2);
            for (int k = 0; k < this.lines.size(); k++) {
                ((LineViewData) this.lines.get(k)).linesPathBottomSize = 0;
            }
            canvas.save();
            float f = 0.0f;
            if (this.transitionMode == 2) {
                this.postTransition = true;
                this.selectionA = 0.0f;
                float transitionAlpha2 = 1.0f - this.transitionParams.progress;
                canvas2.scale((this.transitionParams.progress * 2.0f) + 1.0f, 1.0f, this.transitionParams.pX, this.transitionParams.pY);
                transitionAlpha = transitionAlpha2;
            } else if (this.transitionMode == 1) {
                float transitionAlpha3 = this.transitionParams.progress;
                canvas2.scale(this.transitionParams.progress, 1.0f, this.transitionParams.pX, this.transitionParams.pY);
                transitionAlpha = transitionAlpha3;
            } else if (this.transitionMode == 3) {
                transitionAlpha = this.transitionParams.progress;
            } else {
                transitionAlpha = 1.0f;
            }
            if (this.selectedIndex < 0 || !this.legendShowing) {
                z = false;
            }
            boolean selected = z;
            int i = localStart2;
            while (i <= localEnd2) {
                float stackOffset = 0.0f;
                if (this.selectedIndex != i || !selected) {
                    int k2 = 0;
                    while (k2 < this.lines.size()) {
                        LineViewData line = (LineViewData) this.lines.get(k2);
                        if (line.enabled || line.alpha != f) {
                            int[] y = line.line.y;
                            float xPoint = ((p / 2.0f) + (((StackBarChartData) this.chartData).xPercentage[i] * (fullWidth - p))) - offset;
                            int[] iArr = y;
                            float yPercentage = ((float) y[i]) / this.currentMaxHeight;
                            additionalPoints = additionalPoints2;
                            float height = ((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) * yPercentage * line.alpha;
                            float f2 = yPercentage;
                            float[] fArr = line.linesPath;
                            localStart = localStart2;
                            int localStart3 = line.linesPathBottomSize;
                            localEnd = localEnd2;
                            line.linesPathBottomSize = localStart3 + 1;
                            fArr[localStart3] = xPoint;
                            float[] fArr2 = line.linesPath;
                            int i2 = line.linesPathBottomSize;
                            line.linesPathBottomSize = i2 + 1;
                            fArr2[i2] = (((float) (getMeasuredHeight() - this.chartBottom)) - height) - stackOffset;
                            float[] fArr3 = line.linesPath;
                            int i3 = line.linesPathBottomSize;
                            line.linesPathBottomSize = i3 + 1;
                            fArr3[i3] = xPoint;
                            float[] fArr4 = line.linesPath;
                            int i4 = line.linesPathBottomSize;
                            line.linesPathBottomSize = i4 + 1;
                            LineViewData lineViewData = line;
                            fArr4[i4] = ((float) (getMeasuredHeight() - this.chartBottom)) - stackOffset;
                            stackOffset += height;
                        } else {
                            additionalPoints = additionalPoints2;
                            localStart = localStart2;
                            localEnd = localEnd2;
                        }
                        k2++;
                        additionalPoints2 = additionalPoints;
                        localStart2 = localStart;
                        localEnd2 = localEnd;
                        f = 0.0f;
                    }
                }
                i++;
                additionalPoints2 = additionalPoints2;
                localStart2 = localStart2;
                localEnd2 = localEnd2;
                f = 0.0f;
            }
            int i5 = localStart2;
            int i6 = localEnd2;
            for (int k3 = 0; k3 < this.lines.size(); k3++) {
                StackBarViewData line2 = (StackBarViewData) this.lines.get(k3);
                Paint paint = (selected || this.postTransition) ? line2.unselectedPaint : line2.paint;
                if (selected) {
                    line2.unselectedPaint.setColor(ColorUtils.blendARGB(line2.lineColor, line2.blendColor, this.selectionA));
                }
                if (this.postTransition) {
                    line2.unselectedPaint.setColor(ColorUtils.blendARGB(line2.lineColor, line2.blendColor, 1.0f));
                }
                paint.setAlpha((int) (255.0f * transitionAlpha));
                paint.setStrokeWidth(lineWidth);
                canvas2.drawLines(line2.linesPath, 0, line2.linesPathBottomSize, paint);
            }
            if (selected) {
                float stackOffset2 = 0.0f;
                for (int k4 = 0; k4 < this.lines.size(); k4++) {
                    LineViewData line3 = (LineViewData) this.lines.get(k4);
                    if (!line3.enabled) {
                        if (line3.alpha == 0.0f) {
                        }
                    }
                    int[] y2 = line3.line.y;
                    float xPoint2 = ((p / 2.0f) + (((StackBarChartData) this.chartData).xPercentage[this.selectedIndex] * (fullWidth - p))) - offset;
                    float height2 = ((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) * (((float) y2[this.selectedIndex]) / this.currentMaxHeight) * line3.alpha;
                    float yPoint = ((float) (getMeasuredHeight() - this.chartBottom)) - height2;
                    line3.paint.setStrokeWidth(lineWidth);
                    line3.paint.setAlpha((int) (transitionAlpha * 255.0f));
                    int[] iArr2 = y2;
                    LineViewData lineViewData2 = line3;
                    canvas.drawLine(xPoint2, yPoint - stackOffset2, xPoint2, ((float) (getMeasuredHeight() - this.chartBottom)) - stackOffset2, line3.paint);
                    stackOffset2 += height2;
                }
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void selectXOnChart(int x, int y) {
        float p;
        if (this.chartData != null) {
            int oldSelectedIndex = this.selectedIndex;
            float offset = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
            if (((StackBarChartData) this.chartData).xPercentage.length < 2) {
                p = 1.0f;
            } else {
                p = ((StackBarChartData) this.chartData).xPercentage[1] * this.chartFullWidth;
            }
            float xP = (((float) x) + offset) / (this.chartFullWidth - p);
            this.selectedCoordinate = xP;
            if (xP < 0.0f) {
                this.selectedIndex = 0;
                this.selectedCoordinate = 0.0f;
            } else if (xP > 1.0f) {
                this.selectedIndex = ((StackBarChartData) this.chartData).x.length - 1;
                this.selectedCoordinate = 1.0f;
            } else {
                this.selectedIndex = ((StackBarChartData) this.chartData).findIndex(this.startXIndex, this.endXIndex, xP);
                if (this.selectedIndex > this.endXIndex) {
                    this.selectedIndex = this.endXIndex;
                }
                if (this.selectedIndex < this.startXIndex) {
                    this.selectedIndex = this.startXIndex;
                }
            }
            if (oldSelectedIndex != this.selectedIndex) {
                this.legendShowing = true;
                animateLegend(true);
                moveLegend(offset);
                if (this.dateSelectionListener != null) {
                    this.dateSelectionListener.onDateSelected(getSelectedDate());
                }
                invalidate();
                runSmoothHaptic();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        float p;
        float f;
        if (this.chartData != null) {
            int n = ((StackBarChartData) this.chartData).xPercentage.length;
            int nl = this.lines.size();
            for (int k = 0; k < this.lines.size(); k++) {
                ((LineViewData) this.lines.get(k)).linesPathBottomSize = 0;
            }
            int step = Math.max(1, Math.round(((float) n) / 200.0f));
            int[] iArr = this.yMaxPoints;
            if (iArr == null || iArr.length < nl) {
                this.yMaxPoints = new int[nl];
            }
            for (int i = 0; i < n; i++) {
                float stackOffset = 0.0f;
                float xPoint = ((StackBarChartData) this.chartData).xPercentage[i] * this.pickerWidth;
                int k2 = 0;
                while (true) {
                    f = 0.0f;
                    if (k2 >= nl) {
                        break;
                    }
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != 0.0f) {
                        int y = line.line.y[i];
                        int[] iArr2 = this.yMaxPoints;
                        if (y > iArr2[k2]) {
                            iArr2[k2] = y;
                        }
                    }
                    k2++;
                }
                if (i % step == 0) {
                    int k3 = 0;
                    while (k3 < nl) {
                        LineViewData line2 = (LineViewData) this.lines.get(k3);
                        if (line2.enabled || line2.alpha != f) {
                            float yPoint = ((float) this.pikerHeight) * (((float) this.yMaxPoints[k3]) / (ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) ((StackBarChartData) this.chartData).maxValue)) * line2.alpha;
                            float[] fArr = line2.linesPath;
                            int i2 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i2 + 1;
                            fArr[i2] = xPoint;
                            float[] fArr2 = line2.linesPath;
                            int i3 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i3 + 1;
                            fArr2[i3] = (((float) this.pikerHeight) - yPoint) - stackOffset;
                            float[] fArr3 = line2.linesPath;
                            int i4 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i4 + 1;
                            fArr3[i4] = xPoint;
                            float[] fArr4 = line2.linesPath;
                            int i5 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i5 + 1;
                            fArr4[i5] = ((float) this.pikerHeight) - stackOffset;
                            stackOffset += yPoint;
                            this.yMaxPoints[k3] = 0;
                        }
                        k3++;
                        f = 0.0f;
                    }
                }
            }
            if (((StackBarChartData) this.chartData).xPercentage.length < 2) {
                p = 1.0f;
            } else {
                p = ((StackBarChartData) this.chartData).xPercentage[1] * this.pickerWidth;
            }
            for (int k4 = 0; k4 < nl; k4++) {
                LineViewData line3 = (LineViewData) this.lines.get(k4);
                line3.paint.setStrokeWidth(((float) step) * p);
                line3.paint.setAlpha(255);
                canvas.drawLines(line3.linesPath, 0, line3.linesPathBottomSize, line3.paint);
            }
            Canvas canvas2 = canvas;
            return;
        }
        Canvas canvas3 = canvas;
    }

    public void onCheckChanged() {
        int n = ((ChartData.Line) ((StackBarChartData) this.chartData).lines.get(0)).y.length;
        int k = ((StackBarChartData) this.chartData).lines.size();
        ((StackBarChartData) this.chartData).ySum = new int[n];
        for (int i = 0; i < n; i++) {
            ((StackBarChartData) this.chartData).ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                if (((StackBarViewData) this.lines.get(j)).enabled) {
                    int[] iArr = ((StackBarChartData) this.chartData).ySum;
                    iArr[i] = iArr[i] + ((ChartData.Line) ((StackBarChartData) this.chartData).lines.get(j)).y[i];
                }
            }
        }
        ((StackBarChartData) this.chartData).ySumSegmentTree = new SegmentTree(((StackBarChartData) this.chartData).ySum);
        super.onCheckChanged();
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
    }

    public int findMaxValue(int startXIndex, int endXIndex) {
        return ((StackBarChartData) this.chartData).findMax(startXIndex, endXIndex);
    }

    /* access modifiers changed from: protected */
    public void updatePickerMinMaxHeight() {
        if (ANIMATE_PICKER_SIZES) {
            int max = 0;
            int n = ((StackBarChartData) this.chartData).x.length;
            int nl = this.lines.size();
            for (int i = 0; i < n; i++) {
                int h = 0;
                for (int k = 0; k < nl; k++) {
                    StackBarViewData l = (StackBarViewData) this.lines.get(k);
                    if (l.enabled) {
                        h += l.line.y[i];
                    }
                }
                if (h > max) {
                    max = h;
                }
            }
            if (max > 0 && ((float) max) != this.animatedToPickerMaxHeight) {
                this.animatedToPickerMaxHeight = (float) max;
                if (this.pickerAnimator != null) {
                    this.pickerAnimator.cancel();
                }
                this.pickerAnimator = createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        StackBarChartView.this.pickerMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
                        StackBarChartView.this.invalidatePickerChart = true;
                        StackBarChartView.this.invalidate();
                    }
                });
                this.pickerAnimator.start();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initPickerMaxHeight() {
        super.initPickerMaxHeight();
        this.pickerMaxHeight = 0.0f;
        int n = ((StackBarChartData) this.chartData).x.length;
        int nl = this.lines.size();
        for (int i = 0; i < n; i++) {
            int h = 0;
            for (int k = 0; k < nl; k++) {
                StackBarViewData l = (StackBarViewData) this.lines.get(k);
                if (l.enabled) {
                    h += l.line.y[i];
                }
            }
            if (((float) h) > this.pickerMaxHeight) {
                this.pickerMaxHeight = (float) h;
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

    /* access modifiers changed from: protected */
    public float getMinDistance() {
        return 0.1f;
    }
}
