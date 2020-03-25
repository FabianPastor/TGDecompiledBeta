package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.SegmentTree;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackBarChartData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.StackBarViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;

public class StackBarChartView extends BaseChartView<StackBarChartData, StackBarViewData> {
    private int[] yMaxPoints;

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public float getMinDistance() {
        return 0.1f;
    }

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
        float f;
        float f2;
        float f3;
        float f4;
        int i;
        Canvas canvas2 = canvas;
        T t = this.chartData;
        if (t != null) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            float f5 = chartPickerDelegate.pickerEnd;
            float f6 = chartPickerDelegate.pickerStart;
            float f7 = ((float) this.chartWidth) / (f5 - f6);
            float f8 = (f6 * f7) - ((float) BaseChartView.HORIZONTAL_PADDING);
            boolean z = true;
            if (((StackBarChartData) t).xPercentage.length < 2) {
                f2 = 1.0f;
                f = 1.0f;
            } else {
                float f9 = ((StackBarChartData) t).xPercentage[1] * f7;
                f2 = ((StackBarChartData) t).xPercentage[1] * (f7 - f9);
                f = f9;
            }
            int i2 = ((int) (((float) BaseChartView.HORIZONTAL_PADDING) / f)) + 1;
            int max = Math.max(0, (this.startXIndex - i2) - 2);
            int min = Math.min(((StackBarChartData) this.chartData).xPercentage.length - 1, this.endXIndex + i2 + 2);
            for (int i3 = 0; i3 < this.lines.size(); i3++) {
                ((LineViewData) this.lines.get(i3)).linesPathBottomSize = 0;
            }
            canvas.save();
            int i4 = this.transitionMode;
            float var_ = 2.0f;
            float var_ = 0.0f;
            if (i4 == 2) {
                this.postTransition = true;
                this.selectionA = 0.0f;
                TransitionParams transitionParams = this.transitionParams;
                float var_ = transitionParams.progress;
                f3 = 1.0f - var_;
                canvas2.scale((var_ * 2.0f) + 1.0f, 1.0f, transitionParams.pX, transitionParams.pY);
            } else if (i4 == 1) {
                TransitionParams transitionParams2 = this.transitionParams;
                f3 = transitionParams2.progress;
                canvas2.scale(f3, 1.0f, transitionParams2.pX, transitionParams2.pY);
            } else {
                f3 = i4 == 3 ? this.transitionParams.progress : 1.0f;
            }
            if (this.selectedIndex < 0 || !this.legendShowing) {
                z = false;
            }
            while (max <= min) {
                if (this.selectedIndex != max || !z) {
                    int i5 = 0;
                    float var_ = 0.0f;
                    while (i5 < this.lines.size()) {
                        LineViewData lineViewData = (LineViewData) this.lines.get(i5);
                        if (lineViewData.enabled || lineViewData.alpha != var_) {
                            int[] iArr = lineViewData.line.y;
                            float var_ = ((f / var_) + (((StackBarChartData) this.chartData).xPercentage[max] * (f7 - f))) - f8;
                            float measuredHeight = (((float) iArr[max]) / this.currentMaxHeight) * ((float) ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT)) * lineViewData.alpha;
                            float[] fArr = lineViewData.linesPath;
                            i = min;
                            int i6 = lineViewData.linesPathBottomSize;
                            f4 = f8;
                            int i7 = i6 + 1;
                            lineViewData.linesPathBottomSize = i7;
                            fArr[i6] = var_;
                            int i8 = i7 + 1;
                            lineViewData.linesPathBottomSize = i8;
                            fArr[i7] = (((float) (getMeasuredHeight() - this.chartBottom)) - measuredHeight) - var_;
                            int i9 = i8 + 1;
                            lineViewData.linesPathBottomSize = i9;
                            fArr[i8] = var_;
                            lineViewData.linesPathBottomSize = i9 + 1;
                            fArr[i9] = ((float) (getMeasuredHeight() - this.chartBottom)) - var_;
                            var_ += measuredHeight;
                        } else {
                            i = min;
                            f4 = f8;
                        }
                        i5++;
                        min = i;
                        f8 = f4;
                        var_ = 2.0f;
                        var_ = 0.0f;
                    }
                }
                max++;
                min = min;
                f8 = f8;
                var_ = 2.0f;
                var_ = 0.0f;
            }
            float var_ = f8;
            for (int i10 = 0; i10 < this.lines.size(); i10++) {
                StackBarViewData stackBarViewData = (StackBarViewData) this.lines.get(i10);
                Paint paint = (z || this.postTransition) ? stackBarViewData.unselectedPaint : stackBarViewData.paint;
                if (z) {
                    stackBarViewData.unselectedPaint.setColor(ColorUtils.blendARGB(stackBarViewData.lineColor, stackBarViewData.blendColor, this.selectionA));
                }
                if (this.postTransition) {
                    stackBarViewData.unselectedPaint.setColor(ColorUtils.blendARGB(stackBarViewData.lineColor, stackBarViewData.blendColor, 1.0f));
                }
                paint.setAlpha((int) (255.0f * f3));
                paint.setStrokeWidth(f2);
                canvas2.drawLines(stackBarViewData.linesPath, 0, stackBarViewData.linesPathBottomSize, paint);
            }
            if (z) {
                float var_ = 0.0f;
                for (int i11 = 0; i11 < this.lines.size(); i11++) {
                    LineViewData lineViewData2 = (LineViewData) this.lines.get(i11);
                    if (!lineViewData2.enabled) {
                        if (lineViewData2.alpha == 0.0f) {
                        }
                    }
                    int[] iArr2 = lineViewData2.line.y;
                    float[] fArr2 = ((StackBarChartData) this.chartData).xPercentage;
                    int i12 = this.selectedIndex;
                    float var_ = ((f / 2.0f) + (fArr2[i12] * (f7 - f))) - var_;
                    float measuredHeight2 = (((float) iArr2[i12]) / this.currentMaxHeight) * ((float) ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT)) * lineViewData2.alpha;
                    lineViewData2.paint.setStrokeWidth(f2);
                    lineViewData2.paint.setAlpha((int) (f3 * 255.0f));
                    canvas.drawLine(var_, (((float) (getMeasuredHeight() - this.chartBottom)) - measuredHeight2) - var_, var_, ((float) (getMeasuredHeight() - this.chartBottom)) - var_, lineViewData2.paint);
                    var_ += measuredHeight2;
                }
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void selectXOnChart(int i, int i2) {
        float f;
        T t = this.chartData;
        if (t != null) {
            float f2 = this.chartFullWidth;
            float f3 = (this.pickerDelegate.pickerStart * f2) - ((float) BaseChartView.HORIZONTAL_PADDING);
            if (((StackBarChartData) t).xPercentage.length < 2) {
                f = 1.0f;
            } else {
                f = ((StackBarChartData) t).xPercentage[1] * f2;
            }
            float f4 = (((float) i) + f3) / (this.chartFullWidth - f);
            if (f4 < 0.0f) {
                this.selectedIndex = 0;
            } else if (f4 > 1.0f) {
                this.selectedIndex = ((StackBarChartData) this.chartData).x.length - 1;
            } else {
                int findIndex = ((StackBarChartData) this.chartData).findIndex(this.startXIndex, this.endXIndex, f4);
                this.selectedIndex = findIndex;
                int i3 = findIndex + 1;
                T t2 = this.chartData;
                if (i3 < ((StackBarChartData) t2).xPercentage.length) {
                    if (Math.abs(((StackBarChartData) this.chartData).xPercentage[this.selectedIndex + 1] - f4) < Math.abs(((StackBarChartData) t2).xPercentage[findIndex] - f4)) {
                        this.selectedIndex++;
                    }
                }
                int i4 = this.selectedIndex;
                int i5 = this.endXIndex;
                if (i4 > i5) {
                    this.selectedIndex = i5;
                }
                int i6 = this.selectedIndex;
                int i7 = this.startXIndex;
                if (i6 < i7) {
                    this.selectedIndex = i7;
                }
            }
            this.legendShowing = true;
            animateLegend(true);
            moveLegend(f3);
            BaseChartView.DateSelectionListener dateSelectionListener = this.dateSelectionListener;
            if (dateSelectionListener != null) {
                dateSelectionListener.onDateSelected(getSelectedDate());
            }
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        float f;
        float f2;
        T t = this.chartData;
        if (t != null) {
            int length = ((StackBarChartData) t).xPercentage.length;
            int size = this.lines.size();
            for (int i = 0; i < this.lines.size(); i++) {
                ((LineViewData) this.lines.get(i)).linesPathBottomSize = 0;
            }
            int max = Math.max(1, Math.round(((float) length) / 200.0f));
            int[] iArr = this.yMaxPoints;
            if (iArr == null || iArr.length < size) {
                this.yMaxPoints = new int[size];
            }
            for (int i2 = 0; i2 < length; i2++) {
                float f3 = ((StackBarChartData) this.chartData).xPercentage[i2] * ((float) this.pickerWidth);
                int i3 = 0;
                while (true) {
                    f2 = 0.0f;
                    if (i3 >= size) {
                        break;
                    }
                    LineViewData lineViewData = (LineViewData) this.lines.get(i3);
                    if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                        int i4 = lineViewData.line.y[i2];
                        int[] iArr2 = this.yMaxPoints;
                        if (i4 > iArr2[i3]) {
                            iArr2[i3] = i4;
                        }
                    }
                    i3++;
                }
                if (i2 % max == 0) {
                    int i5 = 0;
                    float f4 = 0.0f;
                    while (i5 < size) {
                        LineViewData lineViewData2 = (LineViewData) this.lines.get(i5);
                        if (lineViewData2.enabled || lineViewData2.alpha != f2) {
                            float f5 = BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) ((StackBarChartData) this.chartData).maxValue;
                            int[] iArr3 = this.yMaxPoints;
                            float f6 = (((float) iArr3[i5]) / f5) * lineViewData2.alpha;
                            int i6 = this.pikerHeight;
                            float f7 = f6 * ((float) i6);
                            float[] fArr = lineViewData2.linesPath;
                            int i7 = lineViewData2.linesPathBottomSize;
                            int i8 = i7 + 1;
                            lineViewData2.linesPathBottomSize = i8;
                            fArr[i7] = f3;
                            int i9 = i8 + 1;
                            lineViewData2.linesPathBottomSize = i9;
                            fArr[i8] = (((float) i6) - f7) - f4;
                            int i10 = i9 + 1;
                            lineViewData2.linesPathBottomSize = i10;
                            fArr[i9] = f3;
                            lineViewData2.linesPathBottomSize = i10 + 1;
                            fArr[i10] = ((float) i6) - f4;
                            f4 += f7;
                            iArr3[i5] = 0;
                        }
                        i5++;
                        f2 = 0.0f;
                    }
                }
            }
            T t2 = this.chartData;
            if (((StackBarChartData) t2).xPercentage.length < 2) {
                f = 1.0f;
            } else {
                f = ((StackBarChartData) t2).xPercentage[1] * ((float) this.pickerWidth);
            }
            for (int i11 = 0; i11 < size; i11++) {
                LineViewData lineViewData3 = (LineViewData) this.lines.get(i11);
                lineViewData3.paint.setStrokeWidth(((float) max) * f);
                lineViewData3.paint.setAlpha(255);
                canvas.drawLines(lineViewData3.linesPath, 0, lineViewData3.linesPathBottomSize, lineViewData3.paint);
            }
        }
    }

    public void onCheckChanged() {
        int length = ((StackBarChartData) this.chartData).lines.get(0).y.length;
        int size = ((StackBarChartData) this.chartData).lines.size();
        ((StackBarChartData) this.chartData).ySum = new int[length];
        for (int i = 0; i < length; i++) {
            ((StackBarChartData) this.chartData).ySum[i] = 0;
            for (int i2 = 0; i2 < size; i2++) {
                if (((StackBarViewData) this.lines.get(i2)).enabled) {
                    T t = this.chartData;
                    int[] iArr = ((StackBarChartData) t).ySum;
                    iArr[i] = iArr[i] + ((StackBarChartData) t).lines.get(i2).y[i];
                }
            }
        }
        T t2 = this.chartData;
        ((StackBarChartData) t2).ySumSegmentTree = new SegmentTree(((StackBarChartData) t2).ySum);
        super.onCheckChanged();
    }

    public int findMaxValue(int i, int i2) {
        return ((StackBarChartData) this.chartData).findMax(i, i2);
    }

    /* access modifiers changed from: protected */
    public void updatePickerMinMaxHeight() {
        if (BaseChartView.ANIMATE_PICKER_SIZES) {
            int length = ((StackBarChartData) this.chartData).x.length;
            int size = this.lines.size();
            int i = 0;
            for (int i2 = 0; i2 < length; i2++) {
                int i3 = 0;
                for (int i4 = 0; i4 < size; i4++) {
                    StackBarViewData stackBarViewData = (StackBarViewData) this.lines.get(i4);
                    if (stackBarViewData.enabled) {
                        i3 += stackBarViewData.line.y[i2];
                    }
                }
                if (i3 > i) {
                    i = i3;
                }
            }
            if (i > 0) {
                float f = (float) i;
                if (f != this.animatedToPickerMaxHeight) {
                    this.animatedToPickerMaxHeight = f;
                    Animator animator = this.pickerAnimator;
                    if (animator != null) {
                        animator.cancel();
                    }
                    ValueAnimator createAnimator = createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            StackBarChartView.this.pickerMaxHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                            StackBarChartView stackBarChartView = StackBarChartView.this;
                            stackBarChartView.invalidatePickerChart = true;
                            stackBarChartView.invalidate();
                        }
                    });
                    this.pickerAnimator = createAnimator;
                    createAnimator.start();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initPickerMaxHeight() {
        super.initPickerMaxHeight();
        this.pickerMaxHeight = 0.0f;
        int length = ((StackBarChartData) this.chartData).x.length;
        int size = this.lines.size();
        for (int i = 0; i < length; i++) {
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                StackBarViewData stackBarViewData = (StackBarViewData) this.lines.get(i3);
                if (stackBarViewData.enabled) {
                    i2 += stackBarViewData.line.y[i];
                }
            }
            float f = (float) i2;
            if (f > this.pickerMaxHeight) {
                this.pickerMaxHeight = f;
            }
        }
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