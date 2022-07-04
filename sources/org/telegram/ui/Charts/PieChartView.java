package org.telegram.ui.Charts;

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
        float a;
        float a2;
        int transitionAlpha;
        float sc;
        Canvas canvas2;
        int transitionAlpha2;
        int radius;
        float a3;
        float a4;
        int transitionAlpha3;
        int i;
        int i2;
        Canvas canvas3 = canvas;
        if (this.chartData != null) {
            int transitionAlpha4 = 255;
            if (canvas3 != null) {
                canvas.save();
            }
            if (this.transitionMode == 1) {
                transitionAlpha4 = (int) (this.transitionParams.progress * this.transitionParams.progress * 255.0f);
            }
            float f = 0.0f;
            if (this.isEmpty) {
                float f2 = this.emptyDataAlpha;
                if (f2 != 0.0f) {
                    float f3 = f2 - 0.12f;
                    this.emptyDataAlpha = f3;
                    if (f3 < 0.0f) {
                        this.emptyDataAlpha = 0.0f;
                    }
                    invalidate();
                }
            } else {
                float f4 = this.emptyDataAlpha;
                if (f4 != 1.0f) {
                    float f5 = f4 + 0.12f;
                    this.emptyDataAlpha = f5;
                    if (f5 > 1.0f) {
                        this.emptyDataAlpha = 1.0f;
                    }
                    invalidate();
                }
            }
            float f6 = this.emptyDataAlpha;
            int transitionAlpha5 = (int) (((float) transitionAlpha4) * f6);
            float sc2 = (f6 * 0.6f) + 0.4f;
            if (canvas3 != null) {
                canvas3.scale(sc2, sc2, this.chartArea.centerX(), this.chartArea.centerY());
            }
            int radius2 = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
            this.rectF.set(this.chartArea.centerX() - ((float) radius2), (this.chartArea.centerY() + ((float) AndroidUtilities.dp(16.0f))) - ((float) radius2), this.chartArea.centerX() + ((float) radius2), this.chartArea.centerY() + ((float) AndroidUtilities.dp(16.0f)) + ((float) radius2));
            int n = this.lines.size();
            float localSum = 0.0f;
            for (int i3 = 0; i3 < n; i3++) {
                localSum += ((PieChartViewData) this.lines.get(i3)).drawingPart * ((PieChartViewData) this.lines.get(i3)).alpha;
            }
            if (localSum != 0.0f) {
                float a5 = -90.0f;
                int i4 = 0;
                while (true) {
                    a = 2.0f;
                    a2 = 8.0f;
                    if (i4 >= n) {
                        break;
                    }
                    if (((PieChartViewData) this.lines.get(i4)).alpha > f || ((PieChartViewData) this.lines.get(i4)).enabled) {
                        ((PieChartViewData) this.lines.get(i4)).paint.setAlpha(transitionAlpha5);
                        float currentPercent = (((PieChartViewData) this.lines.get(i4)).drawingPart / localSum) * ((PieChartViewData) this.lines.get(i4)).alpha;
                        this.darawingValuesPercentage[i4] = currentPercent;
                        if (currentPercent != f) {
                            if (canvas3 != null) {
                                canvas.save();
                            }
                            double textAngle = (double) (a5 + ((currentPercent / 2.0f) * 360.0f));
                            if (((PieChartViewData) this.lines.get(i4)).selectionA > f) {
                                float ai = INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i4)).selectionA);
                                if (canvas3 != null) {
                                    double cos = Math.cos(Math.toRadians(textAngle));
                                    transitionAlpha3 = transitionAlpha5;
                                    double dp = (double) AndroidUtilities.dp(8.0f);
                                    Double.isNaN(dp);
                                    double d = cos * dp;
                                    double d2 = (double) ai;
                                    Double.isNaN(d2);
                                    float f7 = (float) (d2 * d);
                                    double sin = Math.sin(Math.toRadians(textAngle));
                                    double dp2 = (double) AndroidUtilities.dp(8.0f);
                                    Double.isNaN(dp2);
                                    double d3 = sin * dp2;
                                    double d4 = (double) ai;
                                    Double.isNaN(d4);
                                    canvas3.translate(f7, (float) (d3 * d4));
                                } else {
                                    transitionAlpha3 = transitionAlpha5;
                                }
                            } else {
                                transitionAlpha3 = transitionAlpha5;
                            }
                            ((PieChartViewData) this.lines.get(i4)).paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            ((PieChartViewData) this.lines.get(i4)).paint.setStrokeWidth(1.0f);
                            ((PieChartViewData) this.lines.get(i4)).paint.setAntiAlias(!USE_LINES);
                            if (canvas3 == null || this.transitionMode == 1) {
                                i = i4;
                                i2 = 255;
                            } else {
                                double d5 = textAngle;
                                i2 = 255;
                                i = i4;
                                canvas.drawArc(this.rectF, a5, currentPercent * 360.0f, true, ((PieChartViewData) this.lines.get(i4)).paint);
                                ((PieChartViewData) this.lines.get(i)).paint.setStyle(Paint.Style.STROKE);
                                canvas.restore();
                            }
                            ((PieChartViewData) this.lines.get(i)).paint.setAlpha(i2);
                            a5 += 360.0f * currentPercent;
                            i4 = i + 1;
                            transitionAlpha5 = transitionAlpha3;
                            f = 0.0f;
                        }
                    }
                    i = i4;
                    transitionAlpha3 = transitionAlpha5;
                    i4 = i + 1;
                    transitionAlpha5 = transitionAlpha3;
                    f = 0.0f;
                }
                int i5 = i4;
                int n2 = transitionAlpha5;
                float a6 = -90.0f;
                if (canvas3 != null) {
                    int i6 = 0;
                    while (i6 < n) {
                        if (((PieChartViewData) this.lines.get(i6)).alpha > 0.0f || ((PieChartViewData) this.lines.get(i6)).enabled) {
                            float currentPercent2 = (((PieChartViewData) this.lines.get(i6)).drawingPart * ((PieChartViewData) this.lines.get(i6)).alpha) / localSum;
                            canvas.save();
                            double textAngle2 = (double) (((currentPercent2 / a) * 360.0f) + a6);
                            if (((PieChartViewData) this.lines.get(i6)).selectionA > 0.0f) {
                                float ai2 = INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i6)).selectionA);
                                double cos2 = Math.cos(Math.toRadians(textAngle2));
                                sc = sc2;
                                double dp3 = (double) AndroidUtilities.dp(a2);
                                Double.isNaN(dp3);
                                double d6 = (double) ai2;
                                Double.isNaN(d6);
                                double sin2 = Math.sin(Math.toRadians(textAngle2));
                                a4 = a6;
                                double dp4 = (double) AndroidUtilities.dp(a2);
                                Double.isNaN(dp4);
                                double d7 = (double) ai2;
                                Double.isNaN(d7);
                                canvas3.translate((float) (d6 * cos2 * dp3), (float) (sin2 * dp4 * d7));
                            } else {
                                a4 = a6;
                                sc = sc2;
                            }
                            int percent = (int) (100.0f * currentPercent2);
                            if (currentPercent2 < 0.02f || percent <= 0 || percent > 100) {
                                transitionAlpha2 = n2;
                                transitionAlpha = n;
                                canvas2 = canvas3;
                                radius = radius2;
                            } else {
                                double width = (double) (this.rectF.width() * 0.42f);
                                double sqrt = Math.sqrt((double) (1.0f - currentPercent2));
                                Double.isNaN(width);
                                float rText = (float) (width * sqrt);
                                this.textPaint.setTextSize(this.MIN_TEXT_SIZE + (this.MAX_TEXT_SIZE * currentPercent2));
                                transitionAlpha2 = n2;
                                this.textPaint.setAlpha((int) (((float) transitionAlpha2) * ((PieChartViewData) this.lines.get(i6)).alpha));
                                String str = this.lookupTable[percent];
                                double centerX = (double) this.rectF.centerX();
                                double d8 = (double) rText;
                                double cos3 = Math.cos(Math.toRadians(textAngle2));
                                Double.isNaN(d8);
                                Double.isNaN(centerX);
                                float f8 = (float) (centerX + (d8 * cos3));
                                double centerY = (double) this.rectF.centerY();
                                radius = radius2;
                                transitionAlpha = n;
                                double d9 = (double) rText;
                                double sin3 = Math.sin(Math.toRadians(textAngle2));
                                Double.isNaN(d9);
                                Double.isNaN(centerY);
                                canvas2 = canvas;
                                canvas2.drawText(str, f8, ((float) (centerY + (d9 * sin3))) - ((this.textPaint.descent() + this.textPaint.ascent()) / 2.0f), this.textPaint);
                            }
                            canvas.restore();
                            ((PieChartViewData) this.lines.get(i6)).paint.setAlpha(255);
                            a3 = a4 + (currentPercent2 * 360.0f);
                        } else {
                            a3 = a6;
                            sc = sc2;
                            transitionAlpha2 = n2;
                            transitionAlpha = n;
                            canvas2 = canvas3;
                            radius = radius2;
                        }
                        i6++;
                        a6 = a3;
                        radius2 = radius;
                        canvas3 = canvas2;
                        sc2 = sc;
                        n = transitionAlpha;
                        a = 2.0f;
                        a2 = 8.0f;
                        n2 = transitionAlpha2;
                    }
                    float f9 = a6;
                    float var_ = sc2;
                    int i7 = n2;
                    int transitionAlpha6 = n;
                    Canvas canvas4 = canvas3;
                    int i8 = radius2;
                    canvas.restore();
                    return;
                }
                int i9 = n2;
                int transitionAlpha7 = n;
                Canvas canvas5 = canvas3;
                int i10 = radius2;
            } else if (canvas3 != null) {
                canvas.restore();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        float f;
        float sum2;
        int n;
        float yPercentage;
        if (this.chartData != null) {
            int n2 = ((StackLinearChartData) this.chartData).xPercentage.length;
            int nl = this.lines.size();
            for (int k = 0; k < this.lines.size(); k++) {
                ((LineViewData) this.lines.get(k)).linesPathBottomSize = 0;
            }
            float p = (1.0f / ((float) ((StackLinearChartData) this.chartData).xPercentage.length)) * this.pickerWidth;
            for (int i = 0; i < n2; i++) {
                float stackOffset = 0.0f;
                float xPoint = (p / 2.0f) + (((StackLinearChartData) this.chartData).xPercentage[i] * (this.pickerWidth - p));
                float sum3 = 0.0f;
                int drawingLinesCount = 0;
                boolean allDisabled = true;
                int k2 = 0;
                while (true) {
                    f = 0.0f;
                    if (k2 >= nl) {
                        break;
                    }
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != 0.0f) {
                        float v = ((float) line.line.y[i]) * line.alpha;
                        sum3 += v;
                        if (v > 0.0f) {
                            drawingLinesCount++;
                            if (line.enabled) {
                                allDisabled = false;
                            }
                        }
                    }
                    k2++;
                }
                int k3 = 0;
                while (k3 < nl) {
                    LineViewData line2 = (LineViewData) this.lines.get(k3);
                    if (line2.enabled || line2.alpha != f) {
                        int[] y = line2.line.y;
                        if (drawingLinesCount == 1) {
                            if (y[i] == 0) {
                                yPercentage = 0.0f;
                            } else {
                                yPercentage = line2.alpha;
                            }
                        } else if (sum3 == f) {
                            yPercentage = 0.0f;
                        } else if (allDisabled) {
                            yPercentage = (((float) y[i]) / sum3) * line2.alpha * line2.alpha;
                        } else {
                            yPercentage = line2.alpha * (((float) y[i]) / sum3);
                        }
                        float yPoint = ((float) this.pikerHeight) * yPercentage;
                        float[] fArr = line2.linesPath;
                        n = n2;
                        int n3 = line2.linesPathBottomSize;
                        sum2 = sum3;
                        line2.linesPathBottomSize = n3 + 1;
                        fArr[n3] = xPoint;
                        float[] fArr2 = line2.linesPath;
                        int i2 = line2.linesPathBottomSize;
                        line2.linesPathBottomSize = i2 + 1;
                        fArr2[i2] = (((float) this.pikerHeight) - yPoint) - stackOffset;
                        float[] fArr3 = line2.linesPath;
                        int i3 = line2.linesPathBottomSize;
                        line2.linesPathBottomSize = i3 + 1;
                        fArr3[i3] = xPoint;
                        float[] fArr4 = line2.linesPath;
                        int i4 = line2.linesPathBottomSize;
                        line2.linesPathBottomSize = i4 + 1;
                        fArr4[i4] = ((float) this.pikerHeight) - stackOffset;
                        stackOffset += yPoint;
                    } else {
                        n = n2;
                        sum2 = sum3;
                    }
                    k3++;
                    n2 = n;
                    sum3 = sum2;
                    f = 0.0f;
                }
                float f2 = sum3;
            }
            for (int k4 = 0; k4 < nl; k4++) {
                LineViewData line3 = (LineViewData) this.lines.get(k4);
                line3.paint.setStrokeWidth(p);
                line3.paint.setAlpha(255);
                line3.paint.setAntiAlias(false);
                canvas.drawLines(line3.linesPath, 0, line3.linesPathBottomSize, line3.paint);
            }
            Canvas canvas2 = canvas;
            return;
        }
        Canvas canvas3 = canvas;
    }

    /* access modifiers changed from: protected */
    public void drawBottomLine(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
    }

    /* access modifiers changed from: protected */
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
    }

    /* access modifiers changed from: package-private */
    public void drawBottomSignature(Canvas canvas) {
    }

    public void setData(StackLinearChartData chartData) {
        super.setData(chartData);
        if (chartData != null) {
            this.values = new float[chartData.lines.size()];
            this.darawingValuesPercentage = new float[chartData.lines.size()];
            onPickerDataChanged(false, true, false);
        }
    }

    public PieChartViewData createLineViewData(ChartData.Line line) {
        return new PieChartViewData(line);
    }

    /* access modifiers changed from: protected */
    public void selectXOnChart(int x, int y) {
        if (this.chartData != null && !this.isEmpty) {
            double theta = Math.atan2((double) ((this.chartArea.centerY() + ((float) AndroidUtilities.dp(16.0f))) - ((float) y)), (double) (this.chartArea.centerX() - ((float) x)));
            float a = (float) (Math.toDegrees(theta) - 90.0d);
            if (a < 0.0f) {
                double d = (double) a;
                Double.isNaN(d);
                a = (float) (d + 360.0d);
            }
            float a2 = a / 360.0f;
            float p = 0.0f;
            int newSelection = -1;
            float selectionStartA = 0.0f;
            float selectionEndA = 0.0f;
            int i = 0;
            while (true) {
                if (i >= this.lines.size()) {
                    break;
                }
                if (((PieChartViewData) this.lines.get(i)).enabled || ((PieChartViewData) this.lines.get(i)).alpha != 0.0f) {
                    if (a2 > p) {
                        float[] fArr = this.darawingValuesPercentage;
                        if (a2 < fArr[i] + p) {
                            newSelection = i;
                            selectionStartA = p;
                            selectionEndA = p + fArr[i];
                            break;
                        }
                    }
                    p += this.darawingValuesPercentage[i];
                }
                i++;
            }
            if (this.currentSelection == newSelection || newSelection < 0) {
                double d2 = theta;
                float f = p;
            } else {
                this.currentSelection = newSelection;
                invalidate();
                this.pieLegendView.setVisibility(0);
                LineViewData l = (LineViewData) this.lines.get(newSelection);
                this.pieLegendView.setData(l.line.name, (int) this.values[this.currentSelection], l.lineColor);
                this.pieLegendView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
                float r = this.rectF.width() / 2.0f;
                double centerX = (double) this.rectF.centerX();
                double d3 = theta;
                double d4 = (double) r;
                float f2 = p;
                double cos = Math.cos(Math.toRadians((double) ((selectionEndA * 360.0f) - 90.0f)));
                Double.isNaN(d4);
                Double.isNaN(centerX);
                double d5 = centerX + (d4 * cos);
                double centerX2 = (double) this.rectF.centerX();
                double d6 = (double) r;
                double cos2 = Math.cos(Math.toRadians((double) ((selectionStartA * 360.0f) - 90.0f)));
                Double.isNaN(d6);
                Double.isNaN(centerX2);
                int xl = (int) Math.min(d5, centerX2 + (d6 * cos2));
                if (xl < 0) {
                    xl = 0;
                }
                if (this.pieLegendView.getMeasuredWidth() + xl > getMeasuredWidth() - AndroidUtilities.dp(16.0f)) {
                    xl -= (this.pieLegendView.getMeasuredWidth() + xl) - (getMeasuredWidth() - AndroidUtilities.dp(16.0f));
                }
                double centerY = (double) this.rectF.centerY();
                double d7 = (double) r;
                double sin = Math.sin(Math.toRadians((double) ((selectionStartA * 360.0f) - 90.0f)));
                Double.isNaN(d7);
                Double.isNaN(centerY);
                double d8 = centerY + (d7 * sin);
                double centerY2 = (double) this.rectF.centerY();
                double d9 = (double) r;
                float f3 = a2;
                float f4 = r;
                double sin2 = Math.sin(Math.toRadians((double) ((360.0f * selectionEndA) - 90.0f)));
                Double.isNaN(d9);
                Double.isNaN(centerY2);
                int yl = ((int) Math.min(this.rectF.centerY(), (float) ((int) Math.min(d8, centerY2 + (d9 * sin2))))) - AndroidUtilities.dp(50.0f);
                this.pieLegendView.setTranslationX((float) xl);
                this.pieLegendView.setTranslationY((float) yl);
                boolean v = false;
                if (Build.VERSION.SDK_INT >= 27) {
                    v = performHapticFeedback(9, 2);
                }
                if (!v) {
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != this.oldW) {
            this.oldW = getMeasuredWidth();
            int r = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
            this.MIN_TEXT_SIZE = (float) (r / 13);
            this.MAX_TEXT_SIZE = (float) (r / 7);
        }
    }

    public void updatePicker(ChartData chartData, long d) {
        float p;
        int n = chartData.x.length;
        long startOfDay = d - (d % 86400000);
        int startIndex = 0;
        for (int i = 0; i < n; i++) {
            if (startOfDay >= chartData.x[i]) {
                startIndex = i;
            }
        }
        if (chartData.xPercentage.length < 2) {
            p = 0.5f;
        } else {
            p = 1.0f / ((float) chartData.x.length);
        }
        if (startIndex == 0) {
            this.pickerDelegate.pickerStart = 0.0f;
            this.pickerDelegate.pickerEnd = p;
        } else if (startIndex >= chartData.x.length - 1) {
            this.pickerDelegate.pickerStart = 1.0f - p;
            this.pickerDelegate.pickerEnd = 1.0f;
        } else {
            this.pickerDelegate.pickerStart = ((float) startIndex) * p;
            this.pickerDelegate.pickerEnd = this.pickerDelegate.pickerStart + p;
            if (this.pickerDelegate.pickerEnd > 1.0f) {
                this.pickerDelegate.pickerEnd = 1.0f;
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

    public void onPickerDataChanged(boolean animated, boolean force, boolean useAnimator) {
        super.onPickerDataChanged(animated, force, useAnimator);
        if (this.chartData != null && ((StackLinearChartData) this.chartData).xPercentage != null) {
            updateCharValues(this.pickerDelegate.pickerStart, this.pickerDelegate.pickerEnd, force);
        }
    }

    private void updateCharValues(float startPercentage, float endPercentage, boolean force) {
        float animateTo;
        if (this.values != null) {
            int n = ((StackLinearChartData) this.chartData).xPercentage.length;
            int nl = this.lines.size();
            int startIndex = -1;
            int endIndex = -1;
            for (int j = 0; j < n; j++) {
                if (((StackLinearChartData) this.chartData).xPercentage[j] >= startPercentage && startIndex == -1) {
                    startIndex = j;
                }
                if (((StackLinearChartData) this.chartData).xPercentage[j] <= endPercentage) {
                    endIndex = j;
                }
            }
            if (endIndex < startIndex) {
                startIndex = endIndex;
            }
            if (force || this.lastEndIndex != endIndex || this.lastStartIndex != startIndex) {
                this.lastEndIndex = endIndex;
                this.lastStartIndex = startIndex;
                this.isEmpty = true;
                this.sum = 0.0f;
                for (int i = 0; i < nl; i++) {
                    this.values[i] = 0.0f;
                }
                for (int j2 = startIndex; j2 <= endIndex; j2++) {
                    for (int i2 = 0; i2 < nl; i2++) {
                        float[] fArr = this.values;
                        fArr[i2] = fArr[i2] + ((float) ((ChartData.Line) ((StackLinearChartData) this.chartData).lines.get(i2)).y[j2]);
                        this.sum += (float) ((ChartData.Line) ((StackLinearChartData) this.chartData).lines.get(i2)).y[j2];
                        if (this.isEmpty && ((PieChartViewData) this.lines.get(i2)).enabled && ((ChartData.Line) ((StackLinearChartData) this.chartData).lines.get(i2)).y[j2] > 0) {
                            this.isEmpty = false;
                        }
                    }
                }
                if (!force) {
                    for (int i3 = 0; i3 < nl; i3++) {
                        PieChartViewData line = (PieChartViewData) this.lines.get(i3);
                        if (line.animator != null) {
                            line.animator.cancel();
                        }
                        float f = this.sum;
                        if (f == 0.0f) {
                            animateTo = 0.0f;
                        } else {
                            animateTo = this.values[i3] / f;
                        }
                        ValueAnimator animator = createAnimator(line.drawingPart, animateTo, new PieChartView$$ExternalSyntheticLambda0(this, line));
                        line.animator = animator;
                        animator.start();
                    }
                    return;
                }
                for (int i4 = 0; i4 < nl; i4++) {
                    if (this.sum == 0.0f) {
                        ((PieChartViewData) this.lines.get(i4)).drawingPart = 0.0f;
                    } else {
                        ((PieChartViewData) this.lines.get(i4)).drawingPart = this.values[i4] / this.sum;
                    }
                }
            }
        }
    }

    /* renamed from: lambda$updateCharValues$0$org-telegram-ui-Charts-PieChartView  reason: not valid java name */
    public /* synthetic */ void m2906lambda$updateCharValues$0$orgtelegramuiChartsPieChartView(PieChartViewData line, ValueAnimator animation) {
        line.drawingPart = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void onPickerJumpTo(float start, float end, boolean force) {
        if (this.chartData != null) {
            if (force) {
                updateCharValues(start, end, false);
                return;
            }
            updateIndexes();
            invalidate();
        }
    }

    public void fillTransitionParams(TransitionParams params) {
        drawChart((Canvas) null);
        float p = 0.0f;
        int i = 0;
        while (true) {
            float[] fArr = this.darawingValuesPercentage;
            if (i < fArr.length) {
                p += fArr[i];
                params.angle[i] = (360.0f * p) - 180.0f;
                i++;
            } else {
                return;
            }
        }
    }
}
