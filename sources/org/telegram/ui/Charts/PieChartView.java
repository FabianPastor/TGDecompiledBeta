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
/* loaded from: classes3.dex */
public class PieChartView extends StackLinearChartView<PieChartViewData> {
    float MAX_TEXT_SIZE;
    float MIN_TEXT_SIZE;
    int currentSelection;
    float[] darawingValuesPercentage;
    float emptyDataAlpha;
    boolean isEmpty;
    int lastEndIndex;
    int lastStartIndex;
    String[] lookupTable;
    int oldW;
    PieLegendView pieLegendView;
    RectF rectF;
    float sum;
    TextPaint textPaint;
    float[] values;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawBottomLine(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawBottomSignature(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawSelection(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData chartHorizontalLinesData) {
    }

    public PieChartView(Context context) {
        super(context);
        this.currentSelection = -1;
        this.rectF = new RectF();
        this.MIN_TEXT_SIZE = AndroidUtilities.dp(9.0f);
        this.MAX_TEXT_SIZE = AndroidUtilities.dp(13.0f);
        this.lookupTable = new String[101];
        this.emptyDataAlpha = 1.0f;
        this.oldW = 0;
        this.lastStartIndex = -1;
        this.lastEndIndex = -1;
        for (int i = 1; i <= 100; i++) {
            String[] strArr = this.lookupTable;
            strArr[i] = i + "%";
        }
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setColor(-1);
        this.textPaint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.canCaptureChartSelection = true;
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    protected void drawChart(Canvas canvas) {
        int i;
        float f;
        float f2;
        float f3;
        Canvas canvas2;
        int i2;
        int i3;
        float f4;
        int i4;
        Canvas canvas3 = canvas;
        if (this.chartData == 0) {
            return;
        }
        if (canvas3 != null) {
            canvas.save();
        }
        if (this.transitionMode == 1) {
            float f5 = this.transitionParams.progress;
            i = (int) (f5 * f5 * 255.0f);
        } else {
            i = 255;
        }
        float f6 = 1.0f;
        float f7 = 0.0f;
        if (this.isEmpty) {
            float f8 = this.emptyDataAlpha;
            if (f8 != 0.0f) {
                float f9 = f8 - 0.12f;
                this.emptyDataAlpha = f9;
                if (f9 < 0.0f) {
                    this.emptyDataAlpha = 0.0f;
                }
                invalidate();
            }
        } else {
            float var_ = this.emptyDataAlpha;
            if (var_ != 1.0f) {
                float var_ = var_ + 0.12f;
                this.emptyDataAlpha = var_;
                if (var_ > 1.0f) {
                    this.emptyDataAlpha = 1.0f;
                }
                invalidate();
            }
        }
        float var_ = this.emptyDataAlpha;
        int i5 = (int) (i * var_);
        float var_ = (var_ * 0.6f) + 0.4f;
        if (canvas3 != null) {
            canvas3.scale(var_, var_, this.chartArea.centerX(), this.chartArea.centerY());
        }
        float height = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
        this.rectF.set(this.chartArea.centerX() - height, (this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - height, this.chartArea.centerX() + height, this.chartArea.centerY() + AndroidUtilities.dp(16.0f) + height);
        int size = this.lines.size();
        float var_ = 0.0f;
        for (int i6 = 0; i6 < size; i6++) {
            var_ += ((PieChartViewData) this.lines.get(i6)).drawingPart * ((PieChartViewData) this.lines.get(i6)).alpha;
        }
        if (var_ == 0.0f) {
            if (canvas3 == null) {
                return;
            }
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
            if (((PieChartViewData) this.lines.get(i7)).alpha > f7 || ((PieChartViewData) this.lines.get(i7)).enabled) {
                ((PieChartViewData) this.lines.get(i7)).paint.setAlpha(i5);
                float var_ = (((PieChartViewData) this.lines.get(i7)).drawingPart / var_) * ((PieChartViewData) this.lines.get(i7)).alpha;
                this.darawingValuesPercentage[i7] = var_;
                if (var_ != f7) {
                    if (canvas3 != null) {
                        canvas.save();
                    }
                    double d = var_ + ((var_ / 2.0f) * 360.0f);
                    if (((PieChartViewData) this.lines.get(i7)).selectionA > f7) {
                        float interpolation = BaseChartView.INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i7)).selectionA);
                        if (canvas3 != null) {
                            double cos = Math.cos(Math.toRadians(d));
                            f4 = var_;
                            double dp = AndroidUtilities.dp(8.0f);
                            Double.isNaN(dp);
                            double d2 = cos * dp;
                            double d3 = interpolation;
                            Double.isNaN(d3);
                            i3 = i5;
                            double sin = Math.sin(Math.toRadians(d));
                            double dp2 = AndroidUtilities.dp(8.0f);
                            Double.isNaN(dp2);
                            Double.isNaN(d3);
                            canvas3.translate((float) (d2 * d3), (float) (sin * dp2 * d3));
                            ((PieChartViewData) this.lines.get(i7)).paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            ((PieChartViewData) this.lines.get(i7)).paint.setStrokeWidth(1.0f);
                            ((PieChartViewData) this.lines.get(i7)).paint.setAntiAlias(!BaseChartView.USE_LINES);
                            if (canvas3 != null || this.transitionMode == 1) {
                                i4 = i7;
                            } else {
                                i4 = i7;
                                canvas.drawArc(this.rectF, var_, var_ * 360.0f, true, ((PieChartViewData) this.lines.get(i7)).paint);
                                ((PieChartViewData) this.lines.get(i4)).paint.setStyle(Paint.Style.STROKE);
                                canvas.restore();
                            }
                            ((PieChartViewData) this.lines.get(i4)).paint.setAlpha(255);
                            var_ += var_ * 360.0f;
                            i7 = i4 + 1;
                            var_ = f4;
                            i5 = i3;
                            f7 = 0.0f;
                        }
                    }
                    i3 = i5;
                    f4 = var_;
                    ((PieChartViewData) this.lines.get(i7)).paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    ((PieChartViewData) this.lines.get(i7)).paint.setStrokeWidth(1.0f);
                    ((PieChartViewData) this.lines.get(i7)).paint.setAntiAlias(!BaseChartView.USE_LINES);
                    if (canvas3 != null) {
                    }
                    i4 = i7;
                    ((PieChartViewData) this.lines.get(i4)).paint.setAlpha(255);
                    var_ += var_ * 360.0f;
                    i7 = i4 + 1;
                    var_ = f4;
                    i5 = i3;
                    f7 = 0.0f;
                }
            }
            i4 = i7;
            i3 = i5;
            f4 = var_;
            i7 = i4 + 1;
            var_ = f4;
            i5 = i3;
            f7 = 0.0f;
        }
        int i8 = i5;
        float var_ = var_;
        if (canvas3 == null) {
            return;
        }
        int i9 = 0;
        while (i9 < size) {
            if (((PieChartViewData) this.lines.get(i9)).alpha > 0.0f || ((PieChartViewData) this.lines.get(i9)).enabled) {
                float var_ = (((PieChartViewData) this.lines.get(i9)).drawingPart * ((PieChartViewData) this.lines.get(i9)).alpha) / var_;
                canvas.save();
                double d4 = var_ + ((var_ / f) * 360.0f);
                if (((PieChartViewData) this.lines.get(i9)).selectionA > 0.0f) {
                    float interpolation2 = BaseChartView.INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i9)).selectionA);
                    double cos2 = Math.cos(Math.toRadians(d4));
                    double dp3 = AndroidUtilities.dp(f2);
                    Double.isNaN(dp3);
                    double d5 = cos2 * dp3;
                    double d6 = interpolation2;
                    Double.isNaN(d6);
                    float var_ = (float) (d5 * d6);
                    double sin2 = Math.sin(Math.toRadians(d4));
                    double dp4 = AndroidUtilities.dp(f2);
                    Double.isNaN(dp4);
                    Double.isNaN(d6);
                    canvas3.translate(var_, (float) (sin2 * dp4 * d6));
                }
                int i10 = (int) (100.0f * var_);
                if (var_ < 0.02f || i10 <= 0 || i10 > 100) {
                    f3 = var_;
                    canvas2 = canvas3;
                    i2 = i8;
                } else {
                    double width = this.rectF.width() * 0.42f;
                    double sqrt = Math.sqrt(f6 - var_);
                    Double.isNaN(width);
                    float var_ = (float) (width * sqrt);
                    this.textPaint.setTextSize(this.MIN_TEXT_SIZE + (this.MAX_TEXT_SIZE * var_));
                    i2 = i8;
                    this.textPaint.setAlpha((int) (i2 * ((PieChartViewData) this.lines.get(i9)).alpha));
                    String str = this.lookupTable[i10];
                    double centerX = this.rectF.centerX();
                    double d7 = var_;
                    double cos3 = Math.cos(Math.toRadians(d4));
                    Double.isNaN(d7);
                    Double.isNaN(centerX);
                    f3 = var_;
                    double centerY = this.rectF.centerY();
                    double sin3 = Math.sin(Math.toRadians(d4));
                    Double.isNaN(d7);
                    Double.isNaN(centerY);
                    canvas2 = canvas;
                    canvas2.drawText(str, (float) (centerX + (cos3 * d7)), ((float) (centerY + (d7 * sin3))) - ((this.textPaint.descent() + this.textPaint.ascent()) / 2.0f), this.textPaint);
                }
                canvas.restore();
                ((PieChartViewData) this.lines.get(i9)).paint.setAlpha(255);
                var_ += f3 * 360.0f;
            } else {
                canvas2 = canvas3;
                i2 = i8;
            }
            i9++;
            canvas3 = canvas2;
            i8 = i2;
            f = 2.0f;
            f2 = 8.0f;
            f6 = 1.0f;
        }
        canvas.restore();
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    protected void drawPickerChart(Canvas canvas) {
        float f;
        int i;
        float f2;
        T t = this.chartData;
        if (t != 0) {
            int length = ((StackLinearChartData) t).xPercentage.length;
            int size = this.lines.size();
            for (int i2 = 0; i2 < this.lines.size(); i2++) {
                ((LineViewData) this.lines.get(i2)).linesPathBottomSize = 0;
            }
            float length2 = (1.0f / ((StackLinearChartData) this.chartData).xPercentage.length) * this.pickerWidth;
            for (int i3 = 0; i3 < length; i3++) {
                float f3 = (length2 / 2.0f) + (((StackLinearChartData) this.chartData).xPercentage[i3] * (this.pickerWidth - length2));
                float f4 = 0.0f;
                int i4 = 1;
                float f5 = 0.0f;
                int i5 = 0;
                boolean z = true;
                for (int i6 = 0; i6 < size; i6++) {
                    LineViewData lineViewData = (LineViewData) this.lines.get(i6);
                    boolean z2 = lineViewData.enabled;
                    if (z2 || lineViewData.alpha != 0.0f) {
                        float f6 = lineViewData.line.y[i3] * lineViewData.alpha;
                        f5 += f6;
                        if (f6 > 0.0f) {
                            i5++;
                            if (z2) {
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
                                f = lineViewData2.alpha;
                                int i8 = this.pikerHeight;
                                float f8 = f * i8;
                                float[] fArr = lineViewData2.linesPath;
                                int i9 = lineViewData2.linesPathBottomSize;
                                i = length;
                                int i10 = i9 + 1;
                                lineViewData2.linesPathBottomSize = i10;
                                fArr[i9] = f3;
                                int i11 = i10 + 1;
                                lineViewData2.linesPathBottomSize = i11;
                                f2 = f5;
                                fArr[i10] = (i8 - f8) - f7;
                                int i12 = i11 + 1;
                                lineViewData2.linesPathBottomSize = i12;
                                fArr[i11] = f3;
                                lineViewData2.linesPathBottomSize = i12 + 1;
                                fArr[i12] = i8 - f7;
                                f7 += f8;
                            }
                            f = 0.0f;
                            int i82 = this.pikerHeight;
                            float var_ = f * i82;
                            float[] fArr2 = lineViewData2.linesPath;
                            int i92 = lineViewData2.linesPathBottomSize;
                            i = length;
                            int i102 = i92 + 1;
                            lineViewData2.linesPathBottomSize = i102;
                            fArr2[i92] = f3;
                            int i112 = i102 + 1;
                            lineViewData2.linesPathBottomSize = i112;
                            f2 = f5;
                            fArr2[i102] = (i82 - var_) - f7;
                            int i122 = i112 + 1;
                            lineViewData2.linesPathBottomSize = i122;
                            fArr2[i112] = f3;
                            lineViewData2.linesPathBottomSize = i122 + 1;
                            fArr2[i122] = i82 - f7;
                            f7 += var_;
                        } else {
                            if (f5 != f4) {
                                if (z) {
                                    float f9 = lineViewData2.alpha;
                                    f = (iArr[i3] / f5) * f9 * f9;
                                } else {
                                    f = lineViewData2.alpha * (iArr[i3] / f5);
                                }
                                int i822 = this.pikerHeight;
                                float var_ = f * i822;
                                float[] fArr22 = lineViewData2.linesPath;
                                int i922 = lineViewData2.linesPathBottomSize;
                                i = length;
                                int i1022 = i922 + 1;
                                lineViewData2.linesPathBottomSize = i1022;
                                fArr22[i922] = f3;
                                int i1122 = i1022 + 1;
                                lineViewData2.linesPathBottomSize = i1122;
                                f2 = f5;
                                fArr22[i1022] = (i822 - var_) - f7;
                                int i1222 = i1122 + 1;
                                lineViewData2.linesPathBottomSize = i1222;
                                fArr22[i1122] = f3;
                                lineViewData2.linesPathBottomSize = i1222 + 1;
                                fArr22[i1222] = i822 - f7;
                                f7 += var_;
                            }
                            f = 0.0f;
                            int i8222 = this.pikerHeight;
                            float var_ = f * i8222;
                            float[] fArr222 = lineViewData2.linesPath;
                            int i9222 = lineViewData2.linesPathBottomSize;
                            i = length;
                            int i10222 = i9222 + 1;
                            lineViewData2.linesPathBottomSize = i10222;
                            fArr222[i9222] = f3;
                            int i11222 = i10222 + 1;
                            lineViewData2.linesPathBottomSize = i11222;
                            f2 = f5;
                            fArr222[i10222] = (i8222 - var_) - f7;
                            int i12222 = i11222 + 1;
                            lineViewData2.linesPathBottomSize = i12222;
                            fArr222[i11222] = f3;
                            lineViewData2.linesPathBottomSize = i12222 + 1;
                            fArr222[i12222] = i8222 - f7;
                            f7 += var_;
                        }
                    } else {
                        i = length;
                        f2 = f5;
                    }
                    i7++;
                    length = i;
                    f5 = f2;
                    f4 = 0.0f;
                    i4 = 1;
                }
            }
            for (int i13 = 0; i13 < size; i13++) {
                LineViewData lineViewData3 = (LineViewData) this.lines.get(i13);
                lineViewData3.paint.setStrokeWidth(length2);
                lineViewData3.paint.setAlpha(255);
                lineViewData3.paint.setAntiAlias(false);
                canvas.drawLines(lineViewData3.linesPath, 0, lineViewData3.linesPathBottomSize, lineViewData3.paint);
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void setData(StackLinearChartData stackLinearChartData) {
        super.setData((PieChartView) stackLinearChartData);
        if (stackLinearChartData != null) {
            this.values = new float[stackLinearChartData.lines.size()];
            this.darawingValuesPercentage = new float[stackLinearChartData.lines.size()];
            onPickerDataChanged(false, true, false);
        }
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    /* renamed from: createLineViewData */
    public PieChartViewData mo1019createLineViewData(ChartData.Line line) {
        return new PieChartViewData(line);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void selectXOnChart(int i, int i2) {
        if (this.chartData == 0 || this.isEmpty) {
            return;
        }
        float degrees = (float) (Math.toDegrees(Math.atan2((this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - i2, this.chartArea.centerX() - i)) - 90.0d);
        float f = 0.0f;
        if (degrees < 0.0f) {
            double d = degrees;
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
            double centerX = this.rectF.centerX();
            double width = this.rectF.width() / 2.0f;
            double d2 = (f * 360.0f) - 90.0f;
            double cos = Math.cos(Math.toRadians(d2));
            Double.isNaN(width);
            Double.isNaN(centerX);
            double d3 = centerX + (cos * width);
            double centerX2 = this.rectF.centerX();
            double d4 = (f3 * 360.0f) - 90.0f;
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
            double centerY = this.rectF.centerY();
            double sin = Math.sin(Math.toRadians(d4));
            Double.isNaN(width);
            Double.isNaN(centerY);
            double d5 = centerY + (sin * width);
            double centerY2 = this.rectF.centerY();
            double sin2 = Math.sin(Math.toRadians(d2));
            Double.isNaN(width);
            Double.isNaN(centerY2);
            int min2 = ((int) Math.min(this.rectF.centerY(), (int) Math.min(d5, centerY2 + (width * sin2)))) - AndroidUtilities.dp(50.0f);
            this.pieLegendView.setTranslationX(min);
            this.pieLegendView.setTranslationY(min2);
            if (!(Build.VERSION.SDK_INT >= 27 ? performHapticFeedback(9, 2) : false)) {
                performHapticFeedback(3, 2);
            }
        }
        moveLegend();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView, android.view.View
    public void onDraw(Canvas canvas) {
        if (this.chartData != 0) {
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

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void onActionUp() {
        this.currentSelection = -1;
        this.pieLegendView.setVisibility(8);
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Charts.BaseChartView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredWidth() != this.oldW) {
            this.oldW = getMeasuredWidth();
            int height = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
            this.MIN_TEXT_SIZE = height / 13;
            this.MAX_TEXT_SIZE = height / 7;
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void updatePicker(ChartData chartData, long j) {
        int length = chartData.x.length;
        long j2 = j - (j % 86400000);
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (j2 >= chartData.x[i2]) {
                i = i2;
            }
        }
        float length2 = chartData.xPercentage.length < 2 ? 0.5f : 1.0f / chartData.x.length;
        if (i == 0) {
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            chartPickerDelegate.pickerStart = 0.0f;
            chartPickerDelegate.pickerEnd = length2;
        } else if (i >= chartData.x.length - 1) {
            ChartPickerDelegate chartPickerDelegate2 = this.pickerDelegate;
            chartPickerDelegate2.pickerStart = 1.0f - length2;
            chartPickerDelegate2.pickerEnd = 1.0f;
        } else {
            ChartPickerDelegate chartPickerDelegate3 = this.pickerDelegate;
            float f = i * length2;
            chartPickerDelegate3.pickerStart = f;
            float f2 = f + length2;
            chartPickerDelegate3.pickerEnd = f2;
            if (f2 > 1.0f) {
                chartPickerDelegate3.pickerEnd = 1.0f;
            }
            onPickerDataChanged(true, true, false);
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected LegendSignatureView createLegendView() {
        PieLegendView pieLegendView = new PieLegendView(getContext());
        this.pieLegendView = pieLegendView;
        return pieLegendView;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void onPickerDataChanged(boolean z, boolean z2, boolean z3) {
        super.onPickerDataChanged(z, z2, z3);
        T t = this.chartData;
        if (t == 0 || ((StackLinearChartData) t).xPercentage == null) {
            return;
        }
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        updateCharValues(chartPickerDelegate.pickerStart, chartPickerDelegate.pickerEnd, z2);
    }

    private void updateCharValues(float f, float f2, boolean z) {
        if (this.values == null) {
            return;
        }
        int length = ((StackLinearChartData) this.chartData).xPercentage.length;
        int size = this.lines.size();
        int i = 0;
        int i2 = -1;
        int i3 = -1;
        for (int i4 = 0; i4 < length; i4++) {
            T t = this.chartData;
            if (((StackLinearChartData) t).xPercentage[i4] >= f && i3 == -1) {
                i3 = i4;
            }
            if (((StackLinearChartData) t).xPercentage[i4] <= f2) {
                i2 = i4;
            }
        }
        if (i2 < i3) {
            i3 = i2;
        }
        if (!z && this.lastEndIndex == i2 && this.lastStartIndex == i3) {
            return;
        }
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
                fArr[i6] = fArr[i6] + ((StackLinearChartData) this.chartData).lines.get(i6).y[i3];
                this.sum += ((StackLinearChartData) this.chartData).lines.get(i6).y[i3];
                if (this.isEmpty && ((PieChartViewData) this.lines.get(i6)).enabled && ((StackLinearChartData) this.chartData).lines.get(i6).y[i3] > 0) {
                    this.isEmpty = false;
                }
            }
            i3++;
        }
        if (z) {
            while (i < size) {
                if (this.sum == 0.0f) {
                    ((PieChartViewData) this.lines.get(i)).drawingPart = 0.0f;
                } else {
                    ((PieChartViewData) this.lines.get(i)).drawingPart = this.values[i] / this.sum;
                }
                i++;
            }
            return;
        }
        while (i < size) {
            final PieChartViewData pieChartViewData = (PieChartViewData) this.lines.get(i);
            Animator animator = pieChartViewData.animator;
            if (animator != null) {
                animator.cancel();
            }
            float f3 = this.sum;
            ValueAnimator createAnimator = createAnimator(pieChartViewData.drawingPart, f3 == 0.0f ? 0.0f : this.values[i] / f3, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.PieChartView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PieChartView.this.lambda$updateCharValues$0(pieChartViewData, valueAnimator);
                }
            });
            pieChartViewData.animator = createAnimator;
            createAnimator.start();
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCharValues$0(PieChartViewData pieChartViewData, ValueAnimator valueAnimator) {
        pieChartViewData.drawingPart = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // org.telegram.ui.Charts.BaseChartView, org.telegram.ui.Charts.ChartPickerDelegate.Listener
    public void onPickerJumpTo(float f, float f2, boolean z) {
        if (this.chartData == 0) {
            return;
        }
        if (z) {
            updateCharValues(f, f2, false);
            return;
        }
        updateIndexes();
        invalidate();
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    public void fillTransitionParams(TransitionParams transitionParams) {
        drawChart(null);
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
