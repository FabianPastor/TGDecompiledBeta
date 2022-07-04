package org.telegram.ui.Charts;

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
        float offset;
        float fullWidth;
        float p;
        int localEnd;
        int localStart;
        float offset2;
        float fullWidth2;
        Canvas canvas2 = canvas;
        if (this.chartData != null) {
            float fullWidth3 = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset3 = (this.pickerDelegate.pickerStart * fullWidth3) - HORIZONTAL_PADDING;
            canvas.save();
            float transitionAlpha = 1.0f;
            int i = 2;
            float f = 0.0f;
            int i2 = 1;
            if (this.transitionMode == 2) {
                transitionAlpha = this.transitionParams.progress > 0.5f ? 0.0f : 1.0f - (this.transitionParams.progress * 2.0f);
                canvas2.scale((this.transitionParams.progress * 2.0f) + 1.0f, 1.0f, this.transitionParams.pX, this.transitionParams.pY);
            } else if (this.transitionMode == 1) {
                transitionAlpha = this.transitionParams.progress < 0.3f ? 0.0f : this.transitionParams.progress;
                canvas.save();
                canvas2.scale(this.transitionParams.progress, this.transitionParams.progress, this.transitionParams.pX, this.transitionParams.pY);
            } else if (this.transitionMode == 3) {
                transitionAlpha = this.transitionParams.progress;
            }
            int k = 0;
            while (k < this.lines.size()) {
                LineViewData line = (LineViewData) this.lines.get(k);
                if (line.enabled || line.alpha != f) {
                    int j = 0;
                    int[] y = line.line.y;
                    line.chartPath.reset();
                    boolean first = true;
                    if (((DoubleLinearChartData) this.chartData).xPercentage.length < i) {
                        p = 1.0f;
                    } else {
                        p = ((DoubleLinearChartData) this.chartData).xPercentage[i2] * fullWidth3;
                    }
                    int additionalPoints = ((int) (HORIZONTAL_PADDING / p)) + i2;
                    int localStart2 = Math.max(0, this.startXIndex - additionalPoints);
                    int localEnd2 = Math.min(((DoubleLinearChartData) this.chartData).xPercentage.length - i2, this.endXIndex + additionalPoints);
                    int i3 = localStart2;
                    while (i3 <= localEnd2) {
                        if (y[i3] < 0) {
                            fullWidth2 = fullWidth3;
                            offset2 = offset3;
                            localStart = localStart2;
                            localEnd = localEnd2;
                        } else {
                            float xPoint = (((DoubleLinearChartData) this.chartData).xPercentage[i3] * fullWidth3) - offset3;
                            fullWidth2 = fullWidth3;
                            offset2 = offset3;
                            localStart = localStart2;
                            float yPercentage = ((((float) y[i3]) * ((DoubleLinearChartData) this.chartData).linesK[k]) - this.currentMinHeight) / (this.currentMaxHeight - this.currentMinHeight);
                            float padding = line.paint.getStrokeWidth() / 2.0f;
                            localEnd = localEnd2;
                            float yPoint = (((float) (getMeasuredHeight() - this.chartBottom)) - padding) - ((((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) - padding) * yPercentage);
                            if (USE_LINES) {
                                if (j == 0) {
                                    int j2 = j + 1;
                                    line.linesPath[j] = xPoint;
                                    j = j2 + 1;
                                    line.linesPath[j2] = yPoint;
                                } else {
                                    int j3 = j + 1;
                                    line.linesPath[j] = xPoint;
                                    int j4 = j3 + 1;
                                    line.linesPath[j3] = yPoint;
                                    int j5 = j4 + 1;
                                    line.linesPath[j4] = xPoint;
                                    j = j5 + 1;
                                    line.linesPath[j5] = yPoint;
                                }
                            } else if (first) {
                                line.chartPath.moveTo(xPoint, yPoint);
                                first = false;
                            } else {
                                line.chartPath.lineTo(xPoint, yPoint);
                            }
                        }
                        i3++;
                        fullWidth3 = fullWidth2;
                        offset3 = offset2;
                        localStart2 = localStart;
                        localEnd2 = localEnd;
                    }
                    fullWidth = fullWidth3;
                    offset = offset3;
                    int i4 = localStart2;
                    int i5 = localEnd2;
                    if (this.endXIndex - this.startXIndex > 100) {
                        line.paint.setStrokeCap(Paint.Cap.SQUARE);
                    } else {
                        line.paint.setStrokeCap(Paint.Cap.ROUND);
                    }
                    line.paint.setAlpha((int) (line.alpha * 255.0f * transitionAlpha));
                    if (!USE_LINES) {
                        canvas2.drawPath(line.chartPath, line.paint);
                    } else {
                        canvas2.drawLines(line.linesPath, 0, j, line.paint);
                    }
                } else {
                    fullWidth = fullWidth3;
                    offset = offset3;
                }
                k++;
                fullWidth3 = fullWidth;
                offset3 = offset;
                i = 2;
                f = 0.0f;
                i2 = 1;
            }
            float f2 = offset3;
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        Canvas canvas2 = canvas;
        int bottom = getMeasuredHeight() - PICKER_PADDING;
        int top = (getMeasuredHeight() - this.pikerHeight) - PICKER_PADDING;
        int nl = this.lines.size();
        if (this.chartData != null) {
            for (int k = 0; k < nl; k++) {
                LineViewData line = (LineViewData) this.lines.get(k);
                if (line.enabled || line.alpha != 0.0f) {
                    line.bottomLinePath.reset();
                    int n = ((DoubleLinearChartData) this.chartData).xPercentage.length;
                    int j = 0;
                    int[] y = line.line.y;
                    line.chartPath.reset();
                    for (int i = 0; i < n; i++) {
                        if (y[i] >= 0) {
                            float xPoint = ((DoubleLinearChartData) this.chartData).xPercentage[i] * this.pickerWidth;
                            float yPoint = (1.0f - ((((float) y[i]) * ((DoubleLinearChartData) this.chartData).linesK[k]) / (ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) ((DoubleLinearChartData) this.chartData).maxValue))) * ((float) (bottom - top));
                            if (USE_LINES) {
                                if (j == 0) {
                                    int j2 = j + 1;
                                    line.linesPathBottom[j] = xPoint;
                                    j = j2 + 1;
                                    line.linesPathBottom[j2] = yPoint;
                                } else {
                                    int j3 = j + 1;
                                    line.linesPathBottom[j] = xPoint;
                                    int j4 = j3 + 1;
                                    line.linesPathBottom[j3] = yPoint;
                                    int j5 = j4 + 1;
                                    line.linesPathBottom[j4] = xPoint;
                                    j = j5 + 1;
                                    line.linesPathBottom[j5] = yPoint;
                                }
                            } else if (i == 0) {
                                line.bottomLinePath.moveTo(xPoint, yPoint);
                            } else {
                                line.bottomLinePath.lineTo(xPoint, yPoint);
                            }
                        }
                    }
                    line.linesPathBottomSize = j;
                    if (line.enabled || line.alpha != 0.0f) {
                        line.bottomLinePaint.setAlpha((int) (line.alpha * 255.0f));
                        if (USE_LINES) {
                            canvas2.drawLines(line.linesPathBottom, 0, line.linesPathBottomSize, line.bottomLinePaint);
                        } else {
                            canvas2.drawPath(line.bottomLinePath, line.bottomLinePaint);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
        if (this.selectedIndex >= 0 && this.legendShowing) {
            float fullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float xPoint = (((DoubleLinearChartData) this.chartData).xPercentage[this.selectedIndex] * fullWidth) - ((this.pickerDelegate.pickerStart * fullWidth) - HORIZONTAL_PADDING);
            this.selectedLinePaint.setAlpha((int) (((float) this.chartActiveLineAlpha) * this.selectionA));
            canvas.drawLine(xPoint, 0.0f, xPoint, this.chartArea.bottom, this.selectedLinePaint);
            this.tmpN = this.lines.size();
            int i = 0;
            while (true) {
                this.tmpI = i;
                if (this.tmpI < this.tmpN) {
                    LineViewData line = (LineViewData) this.lines.get(this.tmpI);
                    if (line.enabled || line.alpha != 0.0f) {
                        float yPoint = ((float) (getMeasuredHeight() - this.chartBottom)) - (((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) * (((((float) line.line.y[this.selectedIndex]) * ((DoubleLinearChartData) this.chartData).linesK[this.tmpI]) - this.currentMinHeight) / (this.currentMaxHeight - this.currentMinHeight)));
                        line.selectionPaint.setAlpha((int) (line.alpha * 255.0f * this.selectionA));
                        this.selectionBackgroundPaint.setAlpha((int) (line.alpha * 255.0f * this.selectionA));
                        canvas.drawPoint(xPoint, yPoint, line.selectionPaint);
                        canvas.drawPoint(xPoint, yPoint, this.selectionBackgroundPaint);
                    }
                    i = this.tmpI + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        Canvas canvas2 = canvas;
        ChartHorizontalLinesData chartHorizontalLinesData = a;
        int n = chartHorizontalLinesData.values.length;
        int rightIndex = ((DoubleLinearChartData) this.chartData).linesK[0] == 1.0f ? 1 : 0;
        int leftIndex = (rightIndex + 1) % 2;
        float additionalOutAlpha = 1.0f;
        if (n > 2) {
            float v = ((float) (chartHorizontalLinesData.values[1] - chartHorizontalLinesData.values[0])) / (this.currentMaxHeight - this.currentMinHeight);
            if (((double) v) < 0.1d) {
                additionalOutAlpha = v / 0.1f;
            }
        }
        float transitionAlpha = 1.0f;
        if (this.transitionMode == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (this.transitionMode == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (this.transitionMode == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (((float) chartHorizontalLinesData.alpha) * 0.1f * transitionAlpha));
        int chartHeight = (getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT;
        int textOffset = (int) (((float) SIGNATURE_TEXT_HEIGHT) - this.signaturePaint.getTextSize());
        for (int i = 0; i < n; i++) {
            int y = (int) (((float) (getMeasuredHeight() - this.chartBottom)) - (((float) chartHeight) * ((((float) chartHorizontalLinesData.values[i]) - this.currentMinHeight) / (this.currentMaxHeight - this.currentMinHeight))));
            if (chartHorizontalLinesData.valuesStr != null && this.lines.size() > 0) {
                if (chartHorizontalLinesData.valuesStr2 != null) {
                    if (this.lines.size() >= 2) {
                        this.signaturePaint.setColor(((LineViewData) this.lines.get(leftIndex)).lineColor);
                        this.signaturePaint.setAlpha((int) (((float) chartHorizontalLinesData.alpha) * ((LineViewData) this.lines.get(leftIndex)).alpha * transitionAlpha * additionalOutAlpha));
                        canvas2.drawText(chartHorizontalLinesData.valuesStr[i], HORIZONTAL_PADDING, (float) (y - textOffset), this.signaturePaint);
                    }
                }
                this.signaturePaint.setColor(Theme.getColor("statisticChartSignature"));
                this.signaturePaint.setAlpha((int) (((float) chartHorizontalLinesData.alpha) * this.signaturePaintAlpha * transitionAlpha * additionalOutAlpha));
                canvas2.drawText(chartHorizontalLinesData.valuesStr[i], HORIZONTAL_PADDING, (float) (y - textOffset), this.signaturePaint);
            }
            if (chartHorizontalLinesData.valuesStr2 != null && this.lines.size() > 1) {
                this.signaturePaint2.setColor(((LineViewData) this.lines.get(rightIndex)).lineColor);
                this.signaturePaint2.setAlpha((int) (((float) chartHorizontalLinesData.alpha) * ((LineViewData) this.lines.get(rightIndex)).alpha * transitionAlpha * additionalOutAlpha));
                canvas2.drawText(chartHorizontalLinesData.valuesStr2[i], ((float) getMeasuredWidth()) - HORIZONTAL_PADDING, (float) (y - textOffset), this.signaturePaint2);
            }
        }
    }

    public LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }

    public int findMaxValue(int startXIndex, int endXIndex) {
        if (this.lines.isEmpty()) {
            return 0;
        }
        int n = this.lines.size();
        int max = 0;
        for (int i = 0; i < n; i++) {
            int localMax = ((LineViewData) this.lines.get(i)).enabled ? (int) (((float) ((ChartData.Line) ((DoubleLinearChartData) this.chartData).lines.get(i)).segmentTree.rMaxQ(startXIndex, endXIndex)) * ((DoubleLinearChartData) this.chartData).linesK[i]) : 0;
            if (localMax > max) {
                max = localMax;
            }
        }
        return max;
    }

    public int findMinValue(int startXIndex, int endXIndex) {
        if (this.lines.isEmpty()) {
            return 0;
        }
        int n = this.lines.size();
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int localMin = ((LineViewData) this.lines.get(i)).enabled ? (int) (((float) ((ChartData.Line) ((DoubleLinearChartData) this.chartData).lines.get(i)).segmentTree.rMinQ(startXIndex, endXIndex)) * ((DoubleLinearChartData) this.chartData).linesK[i]) : Integer.MAX_VALUE;
            if (localMin < min) {
                min = localMin;
            }
        }
        return min;
    }

    /* access modifiers changed from: protected */
    public void updatePickerMinMaxHeight() {
        if (ANIMATE_PICKER_SIZES) {
            if (((LineViewData) this.lines.get(0)).enabled) {
                super.updatePickerMinMaxHeight();
                return;
            }
            int max = 0;
            Iterator it = this.lines.iterator();
            while (it.hasNext()) {
                LineViewData l = (LineViewData) it.next();
                if (l.enabled && l.line.maxValue > max) {
                    max = l.line.maxValue;
                }
            }
            if (this.lines.size() > 1) {
                max = (int) (((float) max) * ((DoubleLinearChartData) this.chartData).linesK[1]);
            }
            if (max > 0 && ((float) max) != this.animatedToPickerMaxHeight) {
                this.animatedToPickerMaxHeight = (float) max;
                if (this.pickerAnimator != null) {
                    this.pickerAnimator.cancel();
                }
                this.pickerAnimator = createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        DoubleLinearChartView.this.pickerMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
                        DoubleLinearChartView.this.invalidatePickerChart = true;
                        DoubleLinearChartView.this.invalidate();
                    }
                });
                this.pickerAnimator.start();
            }
        }
    }

    /* access modifiers changed from: protected */
    public ChartHorizontalLinesData createHorizontalLinesData(int newMaxHeight, int newMinHeight) {
        float k;
        if (((DoubleLinearChartData) this.chartData).linesK.length < 2) {
            k = 1.0f;
        } else {
            boolean rightIndex = false;
            if (((DoubleLinearChartData) this.chartData).linesK[0] == 1.0f) {
                rightIndex = true;
            }
            k = ((DoubleLinearChartData) this.chartData).linesK[rightIndex];
        }
        return new ChartHorizontalLinesData(newMaxHeight, newMinHeight, this.useMinHeight, k);
    }
}
