package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Iterator;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.DoubleLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;

public class DoubleLinearChartView extends BaseChartView<DoubleLinearChartData, LineViewData> {
    public DoubleLinearChartView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.useMinHeight = true;
        super.init();
    }

    /* access modifiers changed from: protected */
    public void drawChart(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        Canvas canvas2 = canvas;
        if (this.chartData != null) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            float f5 = chartPickerDelegate.pickerEnd;
            float f6 = chartPickerDelegate.pickerStart;
            float f7 = ((float) this.chartWidth) / (f5 - f6);
            float f8 = (f6 * f7) - ((float) BaseChartView.HORIZONTAL_PADDING);
            canvas.save();
            int i = this.transitionMode;
            int i2 = 2;
            float f9 = 0.0f;
            int i3 = 1;
            if (i == 2) {
                float var_ = this.transitionParams.progress;
                f = var_ > 0.5f ? 0.0f : 1.0f - (var_ * 2.0f);
                TransitionParams transitionParams = this.transitionParams;
                canvas2.scale((transitionParams.progress * 2.0f) + 1.0f, 1.0f, transitionParams.pX, transitionParams.pY);
            } else if (i == 1) {
                float var_ = this.transitionParams.progress;
                if (var_ < 0.3f) {
                    var_ = 0.0f;
                }
                canvas.save();
                TransitionParams transitionParams2 = this.transitionParams;
                float var_ = transitionParams2.progress;
                canvas2.scale(var_, var_, transitionParams2.pX, transitionParams2.pY);
            } else {
                f = i == 3 ? this.transitionParams.progress : 1.0f;
            }
            int i4 = 0;
            int i5 = 0;
            while (i5 < this.lines.size()) {
                LineViewData lineViewData = (LineViewData) this.lines.get(i5);
                if (lineViewData.enabled || lineViewData.alpha != f9) {
                    int[] iArr = lineViewData.line.y;
                    lineViewData.chartPath.reset();
                    T t = this.chartData;
                    if (((DoubleLinearChartData) t).xPercentage.length < i2) {
                        f3 = 1.0f;
                    } else {
                        f3 = ((DoubleLinearChartData) t).xPercentage[i3] * f7;
                    }
                    int i6 = ((int) (((float) BaseChartView.HORIZONTAL_PADDING) / f3)) + i3;
                    int max = Math.max(i4, this.startXIndex - i6);
                    int min = Math.min(((DoubleLinearChartData) this.chartData).xPercentage.length - i3, this.endXIndex + i6);
                    boolean z = true;
                    int i7 = 0;
                    while (max <= min) {
                        if (iArr[max] < 0) {
                            f4 = f7;
                        } else {
                            T t2 = this.chartData;
                            float var_ = (((DoubleLinearChartData) t2).xPercentage[max] * f7) - f8;
                            float var_ = ((float) iArr[max]) * ((DoubleLinearChartData) t2).linesK[i5];
                            float var_ = this.currentMinHeight;
                            float var_ = (var_ - var_) / (this.currentMaxHeight - var_);
                            float strokeWidth = lineViewData.paint.getStrokeWidth() / 2.0f;
                            f4 = f7;
                            float measuredHeight = (((float) (getMeasuredHeight() - this.chartBottom)) - strokeWidth) - (var_ * (((float) ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT)) - strokeWidth));
                            if (BaseChartView.USE_LINES) {
                                if (i7 == 0) {
                                    float[] fArr = lineViewData.linesPath;
                                    int i8 = i7 + 1;
                                    fArr[i7] = var_;
                                    i7 = i8 + 1;
                                    fArr[i8] = measuredHeight;
                                } else {
                                    float[] fArr2 = lineViewData.linesPath;
                                    int i9 = i7 + 1;
                                    fArr2[i7] = var_;
                                    int i10 = i9 + 1;
                                    fArr2[i9] = measuredHeight;
                                    int i11 = i10 + 1;
                                    fArr2[i10] = var_;
                                    i7 = i11 + 1;
                                    fArr2[i11] = measuredHeight;
                                }
                            } else if (z) {
                                lineViewData.chartPath.moveTo(var_, measuredHeight);
                                z = false;
                            } else {
                                lineViewData.chartPath.lineTo(var_, measuredHeight);
                            }
                        }
                        max++;
                        f7 = f4;
                    }
                    f2 = f7;
                    if (this.endXIndex - this.startXIndex > 100) {
                        lineViewData.paint.setStrokeCap(Paint.Cap.SQUARE);
                    } else {
                        lineViewData.paint.setStrokeCap(Paint.Cap.ROUND);
                    }
                    lineViewData.paint.setAlpha((int) (lineViewData.alpha * 255.0f * f));
                    if (!BaseChartView.USE_LINES) {
                        canvas2.drawPath(lineViewData.chartPath, lineViewData.paint);
                    } else {
                        canvas2.drawLines(lineViewData.linesPath, 0, i7, lineViewData.paint);
                        i5++;
                        f7 = f2;
                        i2 = 2;
                        f9 = 0.0f;
                        i3 = 1;
                        i4 = 0;
                    }
                } else {
                    f2 = f7;
                }
                i5++;
                f7 = f2;
                i2 = 2;
                f9 = 0.0f;
                i3 = 1;
                i4 = 0;
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        Canvas canvas2 = canvas;
        int measuredHeight = getMeasuredHeight() - BaseChartView.PICKER_PADDING;
        int measuredHeight2 = (getMeasuredHeight() - this.pikerHeight) - BaseChartView.PICKER_PADDING;
        int size = this.lines.size();
        if (this.chartData != null) {
            for (int i = 0; i < size; i++) {
                LineViewData lineViewData = (LineViewData) this.lines.get(i);
                if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                    lineViewData.bottomLinePath.reset();
                    int length = ((DoubleLinearChartData) this.chartData).xPercentage.length;
                    int[] iArr = lineViewData.line.y;
                    lineViewData.chartPath.reset();
                    int i2 = 0;
                    for (int i3 = 0; i3 < length; i3++) {
                        if (iArr[i3] >= 0) {
                            T t = this.chartData;
                            float f = ((DoubleLinearChartData) t).xPercentage[i3] * ((float) this.pickerWidth);
                            float f2 = (1.0f - ((((float) iArr[i3]) * ((DoubleLinearChartData) this.chartData).linesK[i]) / (BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) ((DoubleLinearChartData) t).maxValue))) * ((float) (measuredHeight - measuredHeight2));
                            if (BaseChartView.USE_LINES) {
                                if (i2 == 0) {
                                    float[] fArr = lineViewData.linesPathBottom;
                                    int i4 = i2 + 1;
                                    fArr[i2] = f;
                                    i2 = i4 + 1;
                                    fArr[i4] = f2;
                                } else {
                                    float[] fArr2 = lineViewData.linesPathBottom;
                                    int i5 = i2 + 1;
                                    fArr2[i2] = f;
                                    int i6 = i5 + 1;
                                    fArr2[i5] = f2;
                                    int i7 = i6 + 1;
                                    fArr2[i6] = f;
                                    i2 = i7 + 1;
                                    fArr2[i7] = f2;
                                }
                            } else if (i3 == 0) {
                                lineViewData.bottomLinePath.moveTo(f, f2);
                            } else {
                                lineViewData.bottomLinePath.lineTo(f, f2);
                            }
                        }
                    }
                    lineViewData.linesPathBottomSize = i2;
                    if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                        lineViewData.bottomLinePaint.setAlpha((int) (lineViewData.alpha * 255.0f));
                        if (BaseChartView.USE_LINES) {
                            canvas2.drawLines(lineViewData.linesPathBottom, 0, lineViewData.linesPathBottomSize, lineViewData.bottomLinePaint);
                        } else {
                            canvas2.drawPath(lineViewData.bottomLinePath, lineViewData.bottomLinePaint);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
        int i = this.selectedIndex;
        if (i >= 0 && this.legendShowing) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            float f = chartPickerDelegate.pickerEnd;
            float f2 = chartPickerDelegate.pickerStart;
            float f3 = ((float) this.chartWidth) / (f - f2);
            float f4 = (((DoubleLinearChartData) this.chartData).xPercentage[i] * f3) - ((f2 * f3) - ((float) BaseChartView.HORIZONTAL_PADDING));
            this.selectedLinePaint.setAlpha((int) (((float) this.chartActiveLineAlpha) * this.selectionA));
            canvas.drawLine(f4, 0.0f, f4, (float) this.chartArea.bottom, this.selectedLinePaint);
            this.tmpN = this.lines.size();
            int i2 = 0;
            while (true) {
                this.tmpI = i2;
                int i3 = this.tmpI;
                if (i3 < this.tmpN) {
                    LineViewData lineViewData = (LineViewData) this.lines.get(i3);
                    if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                        float f5 = ((float) lineViewData.line.y[this.selectedIndex]) * ((DoubleLinearChartData) this.chartData).linesK[this.tmpI];
                        float f6 = this.currentMinHeight;
                        float measuredHeight = ((float) (getMeasuredHeight() - this.chartBottom)) - (((f5 - f6) / (this.currentMaxHeight - f6)) * ((float) ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT)));
                        lineViewData.selectionPaint.setAlpha((int) (lineViewData.alpha * 255.0f * this.selectionA));
                        this.selectionBackgroundPaint.setAlpha((int) (lineViewData.alpha * 255.0f * this.selectionA));
                        canvas.drawPoint(f4, measuredHeight, lineViewData.selectionPaint);
                        canvas.drawPoint(f4, measuredHeight, this.selectionBackgroundPaint);
                    }
                    i2 = this.tmpI + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
        Canvas canvas2 = canvas;
        ChartHorizontalLinesData chartHorizontalLinesData2 = chartHorizontalLinesData;
        int length = chartHorizontalLinesData2.values.length;
        float f = 1.0f;
        int i = ((DoubleLinearChartData) this.chartData).linesK[0] == 1.0f ? 1 : 0;
        int i2 = (i + 1) % 2;
        int i3 = this.transitionMode;
        if (i3 == 2) {
            f = 1.0f - this.transitionParams.progress;
        } else if (i3 == 1) {
            f = this.transitionParams.progress;
        } else if (i3 == 3) {
            f = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (((float) chartHorizontalLinesData2.alpha) * 0.1f * f));
        int measuredHeight = getMeasuredHeight() - this.chartBottom;
        int i4 = BaseChartView.SIGNATURE_TEXT_HEIGHT;
        int i5 = measuredHeight - i4;
        int textSize = (int) (((float) i4) - this.signaturePaint.getTextSize());
        for (int i6 = 0; i6 < length; i6++) {
            float f2 = this.currentMinHeight;
            int measuredHeight2 = (int) (((float) (getMeasuredHeight() - this.chartBottom)) - (((float) i5) * ((((float) chartHorizontalLinesData2.values[i6]) - f2) / (this.currentMaxHeight - f2))));
            if (chartHorizontalLinesData2.valuesStr != null && this.lines.size() > 0) {
                if (chartHorizontalLinesData2.valuesStr2 == null || this.lines.size() < 2) {
                    this.signaturePaint.setColor(Theme.getColor("statisticChartSignature"));
                    this.signaturePaint.setAlpha((int) (((float) chartHorizontalLinesData2.alpha) * this.signaturePaintAlpha * f));
                } else {
                    this.signaturePaint.setColor(((LineViewData) this.lines.get(i2)).lineColor);
                    this.signaturePaint.setAlpha((int) (((float) chartHorizontalLinesData2.alpha) * ((LineViewData) this.lines.get(i2)).alpha * f));
                }
                canvas2.drawText(chartHorizontalLinesData2.valuesStr[i6], (float) BaseChartView.HORIZONTAL_PADDING, (float) (measuredHeight2 - textSize), this.signaturePaint);
            }
            if (chartHorizontalLinesData2.valuesStr2 != null) {
                if (this.lines.size() > 1) {
                    this.signaturePaint2.setColor(((LineViewData) this.lines.get(i)).lineColor);
                    this.signaturePaint2.setAlpha((int) (((float) chartHorizontalLinesData2.alpha) * ((LineViewData) this.lines.get(i)).alpha * f));
                    canvas2.drawText(chartHorizontalLinesData2.valuesStr2[i6], (float) (getMeasuredWidth() - BaseChartView.HORIZONTAL_PADDING), (float) (measuredHeight2 - textSize), this.signaturePaint2);
                }
            }
        }
    }

    public LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }

    public int findMaxValue(int i, int i2) {
        if (this.lines.isEmpty()) {
            return 0;
        }
        int size = this.lines.size();
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            int rMaxQ = ((LineViewData) this.lines.get(i4)).enabled ? (int) (((float) ((DoubleLinearChartData) this.chartData).lines.get(i4).segmentTree.rMaxQ(i, i2)) * ((DoubleLinearChartData) this.chartData).linesK[i4]) : 0;
            if (rMaxQ > i3) {
                i3 = rMaxQ;
            }
        }
        return i3;
    }

    public int findMinValue(int i, int i2) {
        if (this.lines.isEmpty()) {
            return 0;
        }
        int size = this.lines.size();
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < size; i4++) {
            int rMinQ = ((LineViewData) this.lines.get(i4)).enabled ? (int) (((float) ((DoubleLinearChartData) this.chartData).lines.get(i4).segmentTree.rMinQ(i, i2)) * ((DoubleLinearChartData) this.chartData).linesK[i4]) : Integer.MAX_VALUE;
            if (rMinQ < i3) {
                i3 = rMinQ;
            }
        }
        return i3;
    }

    /* access modifiers changed from: protected */
    public void updatePickerMinMaxHeight() {
        int i;
        if (BaseChartView.ANIMATE_PICKER_SIZES) {
            int i2 = 0;
            if (((LineViewData) this.lines.get(0)).enabled) {
                super.updatePickerMinMaxHeight();
                return;
            }
            Iterator<L> it = this.lines.iterator();
            while (it.hasNext()) {
                LineViewData lineViewData = (LineViewData) it.next();
                if (lineViewData.enabled && (i = lineViewData.line.maxValue) > i2) {
                    i2 = i;
                }
            }
            if (this.lines.size() > 1) {
                i2 = (int) (((float) i2) * ((DoubleLinearChartData) this.chartData).linesK[1]);
            }
            if (i2 > 0) {
                float f = (float) i2;
                if (f != this.animatedToPickerMaxHeight) {
                    this.animatedToPickerMaxHeight = f;
                    Animator animator = this.pickerAnimator;
                    if (animator != null) {
                        animator.cancel();
                    }
                    ValueAnimator createAnimator = createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            DoubleLinearChartView.this.pickerMaxHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                            DoubleLinearChartView doubleLinearChartView = DoubleLinearChartView.this;
                            doubleLinearChartView.invalidatePickerChart = true;
                            doubleLinearChartView.invalidate();
                        }
                    });
                    this.pickerAnimator = createAnimator;
                    createAnimator.start();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public ChartHorizontalLinesData createHorizontalLinesData(int i, int i2) {
        T t = this.chartData;
        float f = 1.0f;
        if (((DoubleLinearChartData) t).linesK.length >= 2) {
            char c = 0;
            if (((DoubleLinearChartData) t).linesK[0] == 1.0f) {
                c = 1;
            }
            f = ((DoubleLinearChartData) this.chartData).linesK[c];
        }
        return new ChartHorizontalLinesData(i, i2, this.useMinHeight, f);
    }
}
