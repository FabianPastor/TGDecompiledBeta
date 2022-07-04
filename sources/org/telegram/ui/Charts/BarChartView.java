package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.graphics.ColorUtils;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.BarViewData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;

public class BarChartView extends BaseChartView<ChartData, BarViewData> {
    public BarChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
    }

    /* access modifiers changed from: protected */
    public void drawChart(Canvas canvas) {
        int end;
        float transitionAlpha;
        int k;
        float p;
        int k2;
        Canvas canvas2 = canvas;
        if (this.chartData != null) {
            float fullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset = (this.pickerDelegate.pickerStart * fullWidth) - HORIZONTAL_PADDING;
            char c = 1;
            int start = this.startXIndex - 1;
            if (start < 0) {
                start = 0;
            }
            int start2 = start;
            int end2 = this.endXIndex + 1;
            if (end2 > this.chartData.lines.get(0).y.length - 1) {
                end = this.chartData.lines.get(0).y.length - 1;
            } else {
                end = end2;
            }
            canvas.save();
            float f = 0.0f;
            canvas2.clipRect(this.chartStart, 0.0f, this.chartEnd, (float) (getMeasuredHeight() - this.chartBottom));
            canvas.save();
            float f2 = 2.0f;
            int i = 2;
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
            } else {
                transitionAlpha = 1.0f;
            }
            int k3 = 0;
            while (k3 < this.lines.size()) {
                BarViewData line = (BarViewData) this.lines.get(k3);
                if (line.enabled || line.alpha != f) {
                    if (this.chartData.xPercentage.length < i) {
                        p = 1.0f;
                    } else {
                        p = this.chartData.xPercentage[c] * fullWidth;
                    }
                    int[] y = line.line.y;
                    float selectedY = 0.0f;
                    float selectedY2 = 0.0f;
                    boolean selected = false;
                    float a = line.alpha;
                    int j = 0;
                    int i2 = start2;
                    while (i2 <= end) {
                        float xPoint = ((p / f2) + (this.chartData.xPercentage[i2] * fullWidth)) - offset;
                        int[] y2 = y;
                        float yPoint = ((float) (getMeasuredHeight() - this.chartBottom)) - (((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) * ((((float) y[i2]) / this.currentMaxHeight) * a));
                        if (i2 != this.selectedIndex || !this.legendShowing) {
                            int j2 = j + 1;
                            line.linesPath[j] = xPoint;
                            int j3 = j2 + 1;
                            line.linesPath[j2] = yPoint;
                            int j4 = j3 + 1;
                            line.linesPath[j3] = xPoint;
                            j = j4 + 1;
                            k2 = k3;
                            line.linesPath[j4] = (float) (getMeasuredHeight() - this.chartBottom);
                        } else {
                            selected = true;
                            k2 = k3;
                            selectedY2 = yPoint;
                            selectedY = xPoint;
                        }
                        i2++;
                        y = y2;
                        k3 = k2;
                        f2 = 2.0f;
                    }
                    int[] y3 = y;
                    int k4 = k3;
                    Paint paint = (selected || this.postTransition) ? line.unselectedPaint : line.paint;
                    paint.setStrokeWidth(p);
                    if (selected) {
                        line.unselectedPaint.setColor(ColorUtils.blendARGB(line.lineColor, line.blendColor, 1.0f - this.selectionA));
                    }
                    if (this.postTransition) {
                        line.unselectedPaint.setColor(ColorUtils.blendARGB(line.lineColor, line.blendColor, 0.0f));
                    }
                    paint.setAlpha((int) (transitionAlpha * 255.0f));
                    canvas2.drawLines(line.linesPath, 0, j, paint);
                    if (selected) {
                        line.paint.setStrokeWidth(p);
                        line.paint.setAlpha((int) (255.0f * transitionAlpha));
                        int[] iArr = y3;
                        float f3 = p;
                        k = k4;
                        canvas.drawLine(selectedY, selectedY2, selectedY, (float) (getMeasuredHeight() - this.chartBottom), line.paint);
                        line.paint.setAlpha(255);
                    } else {
                        BarViewData barViewData = line;
                        int[] iArr2 = y3;
                        k = k4;
                    }
                } else {
                    k = k3;
                }
                k3 = k + 1;
                i = 2;
                c = 1;
                f = 0.0f;
                f2 = 2.0f;
            }
            int i3 = k3;
            canvas.restore();
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        int bottom;
        int nl;
        float p;
        int bottom2;
        int nl2;
        int bottom3 = getMeasuredHeight() - PICKER_PADDING;
        int top = (getMeasuredHeight() - this.pikerHeight) - PICKER_PADDING;
        int nl3 = this.lines.size();
        if (this.chartData != null) {
            int k = 0;
            while (k < nl3) {
                BarViewData line = (BarViewData) this.lines.get(k);
                if (line.enabled || line.alpha != 0.0f) {
                    line.bottomLinePath.reset();
                    int n = this.chartData.xPercentage.length;
                    int j = 0;
                    if (this.chartData.xPercentage.length < 2) {
                        p = 1.0f;
                    } else {
                        p = this.chartData.xPercentage[1] * this.pickerWidth;
                    }
                    int[] y = line.line.y;
                    float a = line.alpha;
                    int i = 0;
                    while (i < n) {
                        if (y[i] < 0) {
                            bottom2 = bottom3;
                            nl2 = nl3;
                        } else {
                            float xPoint = this.chartData.xPercentage[i] * this.pickerWidth;
                            nl2 = nl3;
                            float yPoint = (1.0f - ((((float) y[i]) / (ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) this.chartData.maxValue)) * a)) * ((float) (bottom3 - top));
                            int j2 = j + 1;
                            line.linesPath[j] = xPoint;
                            int j3 = j2 + 1;
                            line.linesPath[j2] = yPoint;
                            int j4 = j3 + 1;
                            line.linesPath[j3] = xPoint;
                            j = j4 + 1;
                            bottom2 = bottom3;
                            line.linesPath[j4] = (float) (getMeasuredHeight() - this.chartBottom);
                        }
                        i++;
                        nl3 = nl2;
                        bottom3 = bottom2;
                    }
                    bottom = bottom3;
                    nl = nl3;
                    line.paint.setStrokeWidth(2.0f + p);
                    canvas.drawLines(line.linesPath, 0, j, line.paint);
                } else {
                    Canvas canvas2 = canvas;
                    bottom = bottom3;
                    nl = nl3;
                }
                k++;
                nl3 = nl;
                bottom3 = bottom;
            }
            Canvas canvas3 = canvas;
            int i2 = bottom3;
            int i3 = nl3;
            return;
        }
        Canvas canvas4 = canvas;
        int i4 = bottom3;
        int i5 = nl3;
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
    }

    public BarViewData createLineViewData(ChartData.Line line) {
        return new BarViewData(line);
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
