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
/* loaded from: classes3.dex */
public class StackBarChartView extends BaseChartView<StackBarChartData, StackBarViewData> {
    private int[] yMaxPoints;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawSelection(Canvas canvas) {
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected float getMinDistance() {
        return 0.1f;
    }

    public StackBarChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    /* renamed from: createLineViewData  reason: collision with other method in class */
    public StackBarViewData moNUMcreateLineViewData(ChartData.Line line) {
        return new StackBarViewData(line);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawChart(Canvas canvas) {
        float f;
        float f2;
        float f3;
        int i;
        float f4;
        T t = this.chartData;
        if (t == 0) {
            return;
        }
        float f5 = this.chartWidth;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        float f6 = chartPickerDelegate.pickerEnd;
        float f7 = chartPickerDelegate.pickerStart;
        float f8 = f5 / (f6 - f7);
        float f9 = BaseChartView.HORIZONTAL_PADDING;
        float var_ = (f7 * f8) - f9;
        boolean z = true;
        if (((StackBarChartData) t).xPercentage.length < 2) {
            f = 1.0f;
            f2 = 1.0f;
        } else {
            float var_ = ((StackBarChartData) t).xPercentage[1] * f8;
            f = ((StackBarChartData) t).xPercentage[1] * (f8 - var_);
            f2 = var_;
        }
        int i2 = ((int) (f9 / f2)) + 1;
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
            canvas.scale((var_ * 2.0f) + 1.0f, 1.0f, transitionParams.pX, transitionParams.pY);
        } else if (i4 == 1) {
            TransitionParams transitionParams2 = this.transitionParams;
            f3 = transitionParams2.progress;
            canvas.scale(f3, 1.0f, transitionParams2.pX, transitionParams2.pY);
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
                        float var_ = ((f2 / var_) + (((StackBarChartData) this.chartData).xPercentage[max] * (f8 - f2))) - var_;
                        float measuredHeight = (iArr[max] / this.currentMaxHeight) * ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT) * lineViewData.alpha;
                        float[] fArr = lineViewData.linesPath;
                        i = min;
                        int i6 = lineViewData.linesPathBottomSize;
                        f4 = var_;
                        int i7 = i6 + 1;
                        lineViewData.linesPathBottomSize = i7;
                        fArr[i6] = var_;
                        int i8 = i7 + 1;
                        lineViewData.linesPathBottomSize = i8;
                        fArr[i7] = ((getMeasuredHeight() - this.chartBottom) - measuredHeight) - var_;
                        int i9 = i8 + 1;
                        lineViewData.linesPathBottomSize = i9;
                        fArr[i8] = var_;
                        lineViewData.linesPathBottomSize = i9 + 1;
                        fArr[i9] = (getMeasuredHeight() - this.chartBottom) - var_;
                        var_ += measuredHeight;
                    } else {
                        i = min;
                        f4 = var_;
                    }
                    i5++;
                    min = i;
                    var_ = f4;
                    var_ = 2.0f;
                    var_ = 0.0f;
                }
            }
            max++;
            min = min;
            var_ = var_;
            var_ = 2.0f;
            var_ = 0.0f;
        }
        float var_ = var_;
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
            paint.setStrokeWidth(f);
            canvas.drawLines(stackBarViewData.linesPath, 0, stackBarViewData.linesPathBottomSize, paint);
        }
        if (z) {
            float var_ = 0.0f;
            for (int i11 = 0; i11 < this.lines.size(); i11++) {
                LineViewData lineViewData2 = (LineViewData) this.lines.get(i11);
                if (!lineViewData2.enabled && lineViewData2.alpha == 0.0f) {
                }
                int[] iArr2 = lineViewData2.line.y;
                float[] fArr2 = ((StackBarChartData) this.chartData).xPercentage;
                int i12 = this.selectedIndex;
                float var_ = ((f2 / 2.0f) + (fArr2[i12] * (f8 - f2))) - var_;
                float measuredHeight2 = (iArr2[i12] / this.currentMaxHeight) * ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT) * lineViewData2.alpha;
                lineViewData2.paint.setStrokeWidth(f);
                lineViewData2.paint.setAlpha((int) (f3 * 255.0f));
                canvas.drawLine(var_, ((getMeasuredHeight() - this.chartBottom) - measuredHeight2) - var_, var_, (getMeasuredHeight() - this.chartBottom) - var_, lineViewData2.paint);
                var_ += measuredHeight2;
            }
        }
        canvas.restore();
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void selectXOnChart(int i, int i2) {
        T t = this.chartData;
        if (t == 0) {
            return;
        }
        int i3 = this.selectedIndex;
        float f = this.chartFullWidth;
        float f2 = (this.pickerDelegate.pickerStart * f) - BaseChartView.HORIZONTAL_PADDING;
        float f3 = (i + f2) / (f - (((StackBarChartData) t).xPercentage.length < 2 ? 1.0f : ((StackBarChartData) t).xPercentage[1] * f));
        if (f3 < 0.0f) {
            this.selectedIndex = 0;
        } else if (f3 > 1.0f) {
            this.selectedIndex = ((StackBarChartData) t).x.length - 1;
        } else {
            int findIndex = ((StackBarChartData) t).findIndex(this.startXIndex, this.endXIndex, f3);
            this.selectedIndex = findIndex;
            int i4 = this.endXIndex;
            if (findIndex > i4) {
                this.selectedIndex = i4;
            }
            int i5 = this.selectedIndex;
            int i6 = this.startXIndex;
            if (i5 < i6) {
                this.selectedIndex = i6;
            }
        }
        if (i3 == this.selectedIndex) {
            return;
        }
        this.legendShowing = true;
        animateLegend(true);
        moveLegend(f2);
        BaseChartView.DateSelectionListener dateSelectionListener = this.dateSelectionListener;
        if (dateSelectionListener != null) {
            dateSelectionListener.onDateSelected(getSelectedDate());
        }
        invalidate();
        runSmoothHaptic();
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawPickerChart(Canvas canvas) {
        float f;
        T t = this.chartData;
        if (t != 0) {
            int length = ((StackBarChartData) t).xPercentage.length;
            int size = this.lines.size();
            for (int i = 0; i < this.lines.size(); i++) {
                ((LineViewData) this.lines.get(i)).linesPathBottomSize = 0;
            }
            int max = Math.max(1, Math.round(length / 200.0f));
            int[] iArr = this.yMaxPoints;
            if (iArr == null || iArr.length < size) {
                this.yMaxPoints = new int[size];
            }
            for (int i2 = 0; i2 < length; i2++) {
                float f2 = ((StackBarChartData) this.chartData).xPercentage[i2] * this.pickerWidth;
                int i3 = 0;
                while (true) {
                    f = 0.0f;
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
                    float f3 = 0.0f;
                    while (i5 < size) {
                        LineViewData lineViewData2 = (LineViewData) this.lines.get(i5);
                        if (lineViewData2.enabled || lineViewData2.alpha != f) {
                            float f4 = BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : ((StackBarChartData) this.chartData).maxValue;
                            int[] iArr3 = this.yMaxPoints;
                            float f5 = (iArr3[i5] / f4) * lineViewData2.alpha;
                            int i6 = this.pikerHeight;
                            float f6 = f5 * i6;
                            float[] fArr = lineViewData2.linesPath;
                            int i7 = lineViewData2.linesPathBottomSize;
                            int i8 = i7 + 1;
                            lineViewData2.linesPathBottomSize = i8;
                            fArr[i7] = f2;
                            int i9 = i8 + 1;
                            lineViewData2.linesPathBottomSize = i9;
                            fArr[i8] = (i6 - f6) - f3;
                            int i10 = i9 + 1;
                            lineViewData2.linesPathBottomSize = i10;
                            fArr[i9] = f2;
                            lineViewData2.linesPathBottomSize = i10 + 1;
                            fArr[i10] = i6 - f3;
                            f3 += f6;
                            iArr3[i5] = 0;
                        }
                        i5++;
                        f = 0.0f;
                    }
                }
            }
            T t2 = this.chartData;
            float f7 = ((StackBarChartData) t2).xPercentage.length < 2 ? 1.0f : ((StackBarChartData) t2).xPercentage[1] * this.pickerWidth;
            for (int i11 = 0; i11 < size; i11++) {
                LineViewData lineViewData3 = (LineViewData) this.lines.get(i11);
                lineViewData3.paint.setStrokeWidth(max * f7);
                lineViewData3.paint.setAlpha(255);
                canvas.drawLines(lineViewData3.linesPath, 0, lineViewData3.linesPathBottomSize, lineViewData3.paint);
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
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

    @Override // org.telegram.ui.Charts.BaseChartView
    public int findMaxValue(int i, int i2) {
        return ((StackBarChartData) this.chartData).findMax(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void updatePickerMinMaxHeight() {
        if (!BaseChartView.ANIMATE_PICKER_SIZES) {
            return;
        }
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
        if (i <= 0) {
            return;
        }
        float f = i;
        if (f == this.animatedToPickerMaxHeight) {
            return;
        }
        this.animatedToPickerMaxHeight = f;
        Animator animator = this.pickerAnimator;
        if (animator != null) {
            animator.cancel();
        }
        ValueAnimator createAnimator = createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.StackBarChartView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
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
            float f = i2;
            if (f > this.pickerMaxHeight) {
                this.pickerMaxHeight = f;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView, android.view.View
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
}
