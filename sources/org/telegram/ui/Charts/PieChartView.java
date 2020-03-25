package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.PieLegendView;

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
    public void drawChart(Canvas canvas) {
        int i;
        float f;
        float f2;
        Canvas canvas2;
        int i2;
        int i3;
        float f3;
        int i4;
        float f4;
        Canvas canvas3 = canvas;
        if (this.chartData != null) {
            canvas.save();
            if (this.transitionMode == 1) {
                float f5 = this.transitionParams.progress;
                i = (int) (f5 * f5 * 255.0f);
                canvas3.scale(f5, f5, (float) this.chartArea.centerX(), (float) this.chartArea.centerY());
            } else {
                i = 255;
            }
            float f6 = 0.0f;
            if (this.isEmpty) {
                float f7 = this.emptyDataAlpha;
                if (f7 != 0.0f) {
                    float f8 = f7 - 0.12f;
                    this.emptyDataAlpha = f8;
                    if (f8 < 0.0f) {
                        this.emptyDataAlpha = 0.0f;
                    }
                    invalidate();
                }
            } else {
                float f9 = this.emptyDataAlpha;
                if (f9 != 1.0f) {
                    float var_ = f9 + 0.12f;
                    this.emptyDataAlpha = var_;
                    if (var_ > 1.0f) {
                        this.emptyDataAlpha = 1.0f;
                    }
                    invalidate();
                }
            }
            float var_ = (float) i;
            float var_ = this.emptyDataAlpha;
            int i5 = (int) (var_ * var_);
            float var_ = (var_ * 0.6f) + 0.4f;
            canvas3.scale(var_, var_, (float) this.chartArea.centerX(), (float) this.chartArea.centerY());
            int height = (int) (((float) (this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width())) * 0.45f);
            this.rectF.set((float) (this.chartArea.centerX() - height), (float) ((this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - height), (float) (this.chartArea.centerX() + height), (float) (this.chartArea.centerY() + AndroidUtilities.dp(16.0f) + height));
            int size = this.lines.size();
            float var_ = 0.0f;
            for (int i6 = 0; i6 < size; i6++) {
                var_ += ((PieChartViewData) this.lines.get(i6)).drawingPart * ((PieChartViewData) this.lines.get(i6)).alpha;
            }
            if (var_ == 0.0f) {
                canvas.restore();
                return;
            }
            float var_ = -90.0f;
            int i7 = 0;
            float var_ = -90.0f;
            while (true) {
                f = 2.0f;
                f2 = 8.0f;
                if (i7 >= size) {
                    break;
                }
                if (((PieChartViewData) this.lines.get(i7)).alpha > f6 || ((PieChartViewData) this.lines.get(i7)).enabled) {
                    ((PieChartViewData) this.lines.get(i7)).paint.setAlpha(i5);
                    float var_ = (((PieChartViewData) this.lines.get(i7)).drawingPart / var_) * ((PieChartViewData) this.lines.get(i7)).alpha;
                    this.darawingValuesPercentage[i7] = var_;
                    if (var_ != f6) {
                        canvas.save();
                        f3 = var_;
                        double d = (double) (var_ + ((var_ / 2.0f) * 360.0f));
                        if (((PieChartViewData) this.lines.get(i7)).selectionA > f6) {
                            float interpolation = BaseChartView.INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i7)).selectionA);
                            double cos = Math.cos(Math.toRadians(d));
                            i3 = i5;
                            double dp = (double) AndroidUtilities.dp(8.0f);
                            Double.isNaN(dp);
                            double d2 = cos * dp;
                            double d3 = (double) interpolation;
                            Double.isNaN(d3);
                            f4 = var_;
                            double sin = Math.sin(Math.toRadians(d));
                            double dp2 = (double) AndroidUtilities.dp(8.0f);
                            Double.isNaN(dp2);
                            Double.isNaN(d3);
                            canvas3.translate((float) (d2 * d3), (float) (sin * dp2 * d3));
                        } else {
                            f4 = var_;
                            i3 = i5;
                        }
                        ((PieChartViewData) this.lines.get(i7)).paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        ((PieChartViewData) this.lines.get(i7)).paint.setStrokeWidth(1.0f);
                        ((PieChartViewData) this.lines.get(i7)).paint.setAntiAlias(!BaseChartView.USE_LINES);
                        float var_ = f4 * 360.0f;
                        i4 = i7;
                        canvas.drawArc(this.rectF, var_, var_, true, ((PieChartViewData) this.lines.get(i7)).paint);
                        ((PieChartViewData) this.lines.get(i4)).paint.setStyle(Paint.Style.STROKE);
                        canvas.restore();
                        ((PieChartViewData) this.lines.get(i4)).paint.setAlpha(255);
                        var_ += var_;
                        i7 = i4 + 1;
                        var_ = f3;
                        i5 = i3;
                        f6 = 0.0f;
                    }
                }
                i4 = i7;
                i3 = i5;
                f3 = var_;
                i7 = i4 + 1;
                var_ = f3;
                i5 = i3;
                f6 = 0.0f;
            }
            int i8 = i5;
            float var_ = var_;
            int i9 = 0;
            while (i9 < size) {
                if (((PieChartViewData) this.lines.get(i9)).alpha > 0.0f || ((PieChartViewData) this.lines.get(i9)).enabled) {
                    float var_ = (((PieChartViewData) this.lines.get(i9)).drawingPart * ((PieChartViewData) this.lines.get(i9)).alpha) / var_;
                    canvas.save();
                    double d4 = (double) (var_ + ((var_ / f) * 360.0f));
                    if (((PieChartViewData) this.lines.get(i9)).selectionA > 0.0f) {
                        float interpolation2 = BaseChartView.INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i9)).selectionA);
                        double cos2 = Math.cos(Math.toRadians(d4));
                        double dp3 = (double) AndroidUtilities.dp(f2);
                        Double.isNaN(dp3);
                        double d5 = (double) interpolation2;
                        Double.isNaN(d5);
                        double sin2 = Math.sin(Math.toRadians(d4));
                        double dp4 = (double) AndroidUtilities.dp(f2);
                        Double.isNaN(dp4);
                        Double.isNaN(d5);
                        canvas3.translate((float) (cos2 * dp3 * d5), (float) (sin2 * dp4 * d5));
                    }
                    int i10 = (int) (100.0f * var_);
                    if (var_ < 0.02f || i10 <= 0 || i10 > 100) {
                        canvas2 = canvas3;
                        i2 = i8;
                    } else {
                        double width = (double) (this.rectF.width() * 0.42f);
                        double sqrt = Math.sqrt((double) (1.0f - var_));
                        Double.isNaN(width);
                        float var_ = (float) (width * sqrt);
                        this.textPaint.setTextSize(this.MIN_TEXT_SIZE + (this.MAX_TEXT_SIZE * var_));
                        i2 = i8;
                        this.textPaint.setAlpha((int) (((float) i2) * ((PieChartViewData) this.lines.get(i9)).alpha));
                        String str = this.lookupTable[i10];
                        double centerX = (double) this.rectF.centerX();
                        double d6 = (double) var_;
                        double cos3 = Math.cos(Math.toRadians(d4));
                        Double.isNaN(d6);
                        Double.isNaN(centerX);
                        float var_ = (float) (centerX + (cos3 * d6));
                        double centerY = (double) this.rectF.centerY();
                        double sin3 = Math.sin(Math.toRadians(d4));
                        Double.isNaN(d6);
                        Double.isNaN(centerY);
                        canvas2 = canvas;
                        canvas2.drawText(str, var_, ((float) (centerY + (d6 * sin3))) - ((this.textPaint.descent() + this.textPaint.ascent()) / 2.0f), this.textPaint);
                    }
                    canvas.restore();
                    ((PieChartViewData) this.lines.get(i9)).paint.setAlpha(255);
                    var_ += var_ * 360.0f;
                } else {
                    canvas2 = canvas3;
                    i2 = i8;
                }
                i9++;
                i8 = i2;
                canvas3 = canvas2;
                f = 2.0f;
                f2 = 8.0f;
            }
            Canvas canvas4 = canvas3;
            canvas.restore();
        }
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
            float length2 = (1.0f / ((float) ((StackLinearChartData) this.chartData).xPercentage.length)) * ((float) this.pickerWidth);
            for (int i3 = 0; i3 < length; i3++) {
                float f3 = (length2 / 2.0f) + (((StackLinearChartData) this.chartData).xPercentage[i3] * (((float) this.pickerWidth) - length2));
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
            onPickerDataChanged(false, true);
        }
    }

    public PieChartViewData createLineViewData(ChartData.Line line) {
        return new PieChartViewData(line);
    }

    /* access modifiers changed from: protected */
    public void selectXOnChart(int i, int i2) {
        if (this.chartData != null && !this.isEmpty) {
            float degrees = (float) (Math.toDegrees(Math.atan2((double) ((this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - i2), (double) (this.chartArea.centerX() - i))) - 90.0d);
            float f = 0.0f;
            if (degrees < 0.0f) {
                double d = (double) degrees;
                Double.isNaN(d);
                degrees = (float) (d + 360.0d);
            }
            float f2 = degrees / 360.0f;
            int i3 = -1;
            int i4 = 0;
            int i5 = 0;
            float f3 = 0.0f;
            while (true) {
                if (i5 >= this.lines.size()) {
                    f3 = 0.0f;
                    break;
                }
                if (f2 > f3) {
                    float[] fArr = this.darawingValuesPercentage;
                    if (f2 < fArr[i5] + f3) {
                        f = f3 + fArr[i5];
                        i3 = i5;
                        break;
                    }
                }
                f3 += this.darawingValuesPercentage[i5];
                i5++;
            }
            if (this.currentSelection != i3 && i3 >= 0) {
                this.currentSelection = i3;
                invalidate();
                this.pieLegendView.setVisibility(0);
                LineViewData lineViewData = (LineViewData) this.lines.get(i3);
                this.pieLegendView.setData(lineViewData.line.name, (int) this.values[this.currentSelection], lineViewData.lineColor);
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
                if (min >= 0) {
                    i4 = min;
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
                this.pieLegendView.setTranslationX((float) i4);
                this.pieLegendView.setTranslationY((float) min2);
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
            int height = (int) (((float) (this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width())) * 0.45f);
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
            onPickerDataChanged(true, true);
        }
    }

    /* access modifiers changed from: protected */
    public LegendSignatureView createLegendView() {
        PieLegendView pieLegendView2 = new PieLegendView(getContext());
        this.pieLegendView = pieLegendView2;
        return pieLegendView2;
    }

    public void onPickerDataChanged(boolean z, boolean z2) {
        super.onPickerDataChanged(z, z2);
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
                            private final /* synthetic */ PieChartViewData f$1;

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
}
