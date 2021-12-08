package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Iterator;
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
            float f5 = this.chartWidth;
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            float f6 = chartPickerDelegate.pickerEnd;
            float f7 = chartPickerDelegate.pickerStart;
            float f8 = f5 / (f6 - f7);
            float f9 = (f7 * f8) - BaseChartView.HORIZONTAL_PADDING;
            canvas.save();
            int i = this.transitionMode;
            int i2 = 2;
            float var_ = 0.0f;
            int i3 = 1;
            if (i == 2) {
                TransitionParams transitionParams = this.transitionParams;
                float var_ = transitionParams.progress;
                f = var_ > 0.5f ? 0.0f : 1.0f - (var_ * 2.0f);
                canvas2.scale((var_ * 2.0f) + 1.0f, 1.0f, transitionParams.pX, transitionParams.pY);
            } else if (i == 1) {
                float var_ = this.transitionParams.progress;
                f = var_ < 0.3f ? 0.0f : var_;
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
                if (lineViewData.enabled || lineViewData.alpha != var_) {
                    int[] iArr = lineViewData.line.y;
                    lineViewData.chartPath.reset();
                    T t = this.chartData;
                    if (((DoubleLinearChartData) t).xPercentage.length < i2) {
                        f3 = 1.0f;
                    } else {
                        f3 = ((DoubleLinearChartData) t).xPercentage[i3] * f8;
                    }
                    int i6 = ((int) (BaseChartView.HORIZONTAL_PADDING / f3)) + i3;
                    int max = Math.max(i4, this.startXIndex - i6);
                    int min = Math.min(((DoubleLinearChartData) this.chartData).xPercentage.length - i3, this.endXIndex + i6);
                    boolean z = true;
                    int i7 = 0;
                    while (max <= min) {
                        if (iArr[max] < 0) {
                            f4 = f8;
                        } else {
                            T t2 = this.chartData;
                            float var_ = (((DoubleLinearChartData) t2).xPercentage[max] * f8) - f9;
                            float var_ = ((float) iArr[max]) * ((DoubleLinearChartData) t2).linesK[i5];
                            float var_ = this.currentMinHeight;
                            float var_ = (var_ - var_) / (this.currentMaxHeight - var_);
                            float strokeWidth = lineViewData.paint.getStrokeWidth() / 2.0f;
                            f4 = f8;
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
                        f8 = f4;
                    }
                    f2 = f8;
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
                        f8 = f2;
                        i4 = 0;
                        i2 = 2;
                        var_ = 0.0f;
                        i3 = 1;
                    }
                } else {
                    f2 = f8;
                }
                i5++;
                f8 = f2;
                i4 = 0;
                i2 = 2;
                var_ = 0.0f;
                i3 = 1;
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
        Canvas canvas2 = canvas;
        int measuredHeight = getMeasuredHeight();
        int i = BaseChartView.PICKER_PADDING;
        int i2 = measuredHeight - i;
        int measuredHeight2 = (getMeasuredHeight() - this.pikerHeight) - i;
        int size = this.lines.size();
        if (this.chartData != null) {
            for (int i3 = 0; i3 < size; i3++) {
                LineViewData lineViewData = (LineViewData) this.lines.get(i3);
                if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                    lineViewData.bottomLinePath.reset();
                    int length = ((DoubleLinearChartData) this.chartData).xPercentage.length;
                    int[] iArr = lineViewData.line.y;
                    lineViewData.chartPath.reset();
                    int i4 = 0;
                    for (int i5 = 0; i5 < length; i5++) {
                        if (iArr[i5] >= 0) {
                            T t = this.chartData;
                            float f = ((DoubleLinearChartData) t).xPercentage[i5] * this.pickerWidth;
                            float f2 = (1.0f - ((((float) iArr[i5]) * ((DoubleLinearChartData) t).linesK[i3]) / (BaseChartView.ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : (float) ((DoubleLinearChartData) t).maxValue))) * ((float) (i2 - measuredHeight2));
                            if (BaseChartView.USE_LINES) {
                                if (i4 == 0) {
                                    float[] fArr = lineViewData.linesPathBottom;
                                    int i6 = i4 + 1;
                                    fArr[i4] = f;
                                    i4 = i6 + 1;
                                    fArr[i6] = f2;
                                } else {
                                    float[] fArr2 = lineViewData.linesPathBottom;
                                    int i7 = i4 + 1;
                                    fArr2[i4] = f;
                                    int i8 = i7 + 1;
                                    fArr2[i7] = f2;
                                    int i9 = i8 + 1;
                                    fArr2[i8] = f;
                                    i4 = i9 + 1;
                                    fArr2[i9] = f2;
                                }
                            } else if (i5 == 0) {
                                lineViewData.bottomLinePath.moveTo(f, f2);
                            } else {
                                lineViewData.bottomLinePath.lineTo(f, f2);
                            }
                        }
                    }
                    lineViewData.linesPathBottomSize = i4;
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
            float f = this.chartWidth;
            ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
            float f2 = chartPickerDelegate.pickerEnd;
            float f3 = chartPickerDelegate.pickerStart;
            float f4 = f / (f2 - f3);
            float f5 = (((DoubleLinearChartData) this.chartData).xPercentage[i] * f4) - ((f3 * f4) - BaseChartView.HORIZONTAL_PADDING);
            this.selectedLinePaint.setAlpha((int) (((float) this.chartActiveLineAlpha) * this.selectionA));
            canvas.drawLine(f5, 0.0f, f5, this.chartArea.bottom, this.selectedLinePaint);
            this.tmpN = this.lines.size();
            int i2 = 0;
            while (true) {
                this.tmpI = i2;
                int i3 = this.tmpI;
                if (i3 < this.tmpN) {
                    LineViewData lineViewData = (LineViewData) this.lines.get(i3);
                    if (lineViewData.enabled || lineViewData.alpha != 0.0f) {
                        float f6 = ((float) lineViewData.line.y[this.selectedIndex]) * ((DoubleLinearChartData) this.chartData).linesK[this.tmpI];
                        float f7 = this.currentMinHeight;
                        float measuredHeight = ((float) (getMeasuredHeight() - this.chartBottom)) - (((f6 - f7) / (this.currentMaxHeight - f7)) * ((float) ((getMeasuredHeight() - this.chartBottom) - BaseChartView.SIGNATURE_TEXT_HEIGHT)));
                        lineViewData.selectionPaint.setAlpha((int) (lineViewData.alpha * 255.0f * this.selectionA));
                        this.selectionBackgroundPaint.setAlpha((int) (lineViewData.alpha * 255.0f * this.selectionA));
                        canvas.drawPoint(f5, measuredHeight, lineViewData.selectionPaint);
                        canvas.drawPoint(f5, measuredHeight, this.selectionBackgroundPaint);
                    }
                    i2 = this.tmpI + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0079  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawSignaturesToHorizontalLines(android.graphics.Canvas r18, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            int[] r3 = r2.values
            int r4 = r3.length
            T r5 = r0.chartData
            org.telegram.ui.Charts.data.DoubleLinearChartData r5 = (org.telegram.ui.Charts.data.DoubleLinearChartData) r5
            float[] r5 = r5.linesK
            r6 = 0
            r5 = r5[r6]
            r7 = 1065353216(0x3var_, float:1.0)
            r8 = 1
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x001b
            r5 = 1
            goto L_0x001c
        L_0x001b:
            r5 = 0
        L_0x001c:
            int r9 = r5 + 1
            r10 = 2
            int r9 = r9 % r10
            r11 = 1036831949(0x3dcccccd, float:0.1)
            if (r4 <= r10) goto L_0x003d
            r12 = r3[r8]
            r3 = r3[r6]
            int r12 = r12 - r3
            float r3 = (float) r12
            float r12 = r0.currentMaxHeight
            float r13 = r0.currentMinHeight
            float r12 = r12 - r13
            float r3 = r3 / r12
            double r12 = (double) r3
            r14 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 >= 0) goto L_0x003d
            float r3 = r3 / r11
            goto L_0x003f
        L_0x003d:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x003f:
            int r12 = r0.transitionMode
            if (r12 != r10) goto L_0x0049
            org.telegram.ui.Charts.view_data.TransitionParams r12 = r0.transitionParams
            float r12 = r12.progress
            float r7 = r7 - r12
            goto L_0x0057
        L_0x0049:
            if (r12 != r8) goto L_0x0050
            org.telegram.ui.Charts.view_data.TransitionParams r7 = r0.transitionParams
            float r7 = r7.progress
            goto L_0x0057
        L_0x0050:
            r13 = 3
            if (r12 != r13) goto L_0x0057
            org.telegram.ui.Charts.view_data.TransitionParams r7 = r0.transitionParams
            float r7 = r7.progress
        L_0x0057:
            android.graphics.Paint r12 = r0.linePaint
            int r13 = r2.alpha
            float r13 = (float) r13
            float r13 = r13 * r11
            float r13 = r13 * r7
            int r11 = (int) r13
            r12.setAlpha(r11)
            int r11 = r17.getMeasuredHeight()
            int r12 = r0.chartBottom
            int r11 = r11 - r12
            int r12 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r11 = r11 - r12
            float r12 = (float) r12
            android.graphics.Paint r13 = r0.signaturePaint
            float r13 = r13.getTextSize()
            float r12 = r12 - r13
            int r12 = (int) r12
        L_0x0077:
            if (r6 >= r4) goto L_0x0150
            int r13 = r17.getMeasuredHeight()
            int r14 = r0.chartBottom
            int r13 = r13 - r14
            float r13 = (float) r13
            float r14 = (float) r11
            int[] r15 = r2.values
            r15 = r15[r6]
            float r15 = (float) r15
            float r8 = r0.currentMinHeight
            float r15 = r15 - r8
            float r10 = r0.currentMaxHeight
            float r10 = r10 - r8
            float r15 = r15 / r10
            float r14 = r14 * r15
            float r13 = r13 - r14
            int r8 = (int) r13
            java.lang.String[] r10 = r2.valuesStr
            if (r10 == 0) goto L_0x0100
            java.util.ArrayList<L> r10 = r0.lines
            int r10 = r10.size()
            if (r10 <= 0) goto L_0x0100
            java.lang.String[] r10 = r2.valuesStr2
            if (r10 == 0) goto L_0x00d5
            java.util.ArrayList<L> r10 = r0.lines
            int r10 = r10.size()
            r13 = 2
            if (r10 >= r13) goto L_0x00ac
            goto L_0x00d6
        L_0x00ac:
            android.graphics.Paint r10 = r0.signaturePaint
            java.util.ArrayList<L> r14 = r0.lines
            java.lang.Object r14 = r14.get(r9)
            org.telegram.ui.Charts.view_data.LineViewData r14 = (org.telegram.ui.Charts.view_data.LineViewData) r14
            int r14 = r14.lineColor
            r10.setColor(r14)
            android.graphics.Paint r10 = r0.signaturePaint
            int r14 = r2.alpha
            float r14 = (float) r14
            java.util.ArrayList<L> r15 = r0.lines
            java.lang.Object r15 = r15.get(r9)
            org.telegram.ui.Charts.view_data.LineViewData r15 = (org.telegram.ui.Charts.view_data.LineViewData) r15
            float r15 = r15.alpha
            float r14 = r14 * r15
            float r14 = r14 * r7
            float r14 = r14 * r3
            int r14 = (int) r14
            r10.setAlpha(r14)
            goto L_0x00f2
        L_0x00d5:
            r13 = 2
        L_0x00d6:
            android.graphics.Paint r10 = r0.signaturePaint
            java.lang.String r14 = "statisticChartSignature"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r10.setColor(r14)
            android.graphics.Paint r10 = r0.signaturePaint
            int r14 = r2.alpha
            float r14 = (float) r14
            float r15 = r0.signaturePaintAlpha
            float r14 = r14 * r15
            float r14 = r14 * r7
            float r14 = r14 * r3
            int r14 = (int) r14
            r10.setAlpha(r14)
        L_0x00f2:
            java.lang.String[] r10 = r2.valuesStr
            r10 = r10[r6]
            float r14 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            int r15 = r8 - r12
            float r15 = (float) r15
            android.graphics.Paint r13 = r0.signaturePaint
            r1.drawText(r10, r14, r15, r13)
        L_0x0100:
            java.lang.String[] r10 = r2.valuesStr2
            if (r10 == 0) goto L_0x0149
            java.util.ArrayList<L> r10 = r0.lines
            int r10 = r10.size()
            r13 = 1
            if (r10 <= r13) goto L_0x014a
            android.graphics.Paint r10 = r0.signaturePaint2
            java.util.ArrayList<L> r14 = r0.lines
            java.lang.Object r14 = r14.get(r5)
            org.telegram.ui.Charts.view_data.LineViewData r14 = (org.telegram.ui.Charts.view_data.LineViewData) r14
            int r14 = r14.lineColor
            r10.setColor(r14)
            android.graphics.Paint r10 = r0.signaturePaint2
            int r14 = r2.alpha
            float r14 = (float) r14
            java.util.ArrayList<L> r15 = r0.lines
            java.lang.Object r15 = r15.get(r5)
            org.telegram.ui.Charts.view_data.LineViewData r15 = (org.telegram.ui.Charts.view_data.LineViewData) r15
            float r15 = r15.alpha
            float r14 = r14 * r15
            float r14 = r14 * r7
            float r14 = r14 * r3
            int r14 = (int) r14
            r10.setAlpha(r14)
            java.lang.String[] r10 = r2.valuesStr2
            r10 = r10[r6]
            int r14 = r17.getMeasuredWidth()
            float r14 = (float) r14
            float r15 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            float r14 = r14 - r15
            int r8 = r8 - r12
            float r8 = (float) r8
            android.graphics.Paint r15 = r0.signaturePaint2
            r1.drawText(r10, r14, r8, r15)
            goto L_0x014a
        L_0x0149:
            r13 = 1
        L_0x014a:
            int r6 = r6 + 1
            r8 = 1
            r10 = 2
            goto L_0x0077
        L_0x0150:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.DoubleLinearChartView.drawSignaturesToHorizontalLines(android.graphics.Canvas, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData):void");
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
            f = ((DoubleLinearChartData) t).linesK[c];
        }
        return new ChartHorizontalLinesData(i, i2, this.useMinHeight, f);
    }
}
