package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class StorageDiagramView extends View {
    private float[] animateToPercentage;
    private ClearViewData[] data;
    private float[] drawingPercentage;
    int enabledCount;
    StaticLayout layout1;
    StaticLayout layout2;
    private RectF rectF = new RectF();
    private float singleProgress = 0.0f;
    private float[] startFromPercentage;
    TextPaint textPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);
    ValueAnimator valueAnimator;

    public StorageDiagramView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(110.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(110.0f), NUM));
        this.rectF.set((float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(3.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(3.0f)));
        updateDescription();
        this.textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    public void setData(ClearViewData[] data2) {
        this.data = data2;
        invalidate();
        this.drawingPercentage = new float[data2.length];
        this.animateToPercentage = new float[data2.length];
        this.startFromPercentage = new float[data2.length];
        update(false);
        if (this.enabledCount > 1) {
            this.singleProgress = 0.0f;
        } else {
            this.singleProgress = 1.0f;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        double d;
        int i;
        float percent;
        float a;
        float a2;
        Canvas canvas2 = canvas;
        if (this.data != null) {
            float f = 1.0f;
            float f2 = 0.0f;
            if (this.enabledCount > 1) {
                float f3 = this.singleProgress;
                if (f3 > 0.0f) {
                    double d2 = (double) f3;
                    Double.isNaN(d2);
                    float f4 = (float) (d2 - 0.04d);
                    this.singleProgress = f4;
                    if (f4 < 0.0f) {
                        this.singleProgress = 0.0f;
                    }
                }
            } else {
                float f5 = this.singleProgress;
                if (f5 < 1.0f) {
                    double d3 = (double) f5;
                    Double.isNaN(d3);
                    float f6 = (float) (d3 + 0.04d);
                    this.singleProgress = f6;
                    if (f6 > 1.0f) {
                        this.singleProgress = 1.0f;
                    }
                }
            }
            float startFrom = 0.0f;
            int i2 = 0;
            while (true) {
                ClearViewData[] clearViewDataArr = this.data;
                d = 180.0d;
                i = 255;
                if (i2 >= clearViewDataArr.length) {
                    break;
                }
                if (clearViewDataArr[i2] != null) {
                    float[] fArr = this.drawingPercentage;
                    if (fArr[i2] != 0.0f) {
                        float percent2 = fArr[i2];
                        if (clearViewDataArr[i2].firstDraw) {
                            float a3 = (-360.0f * percent2) + ((f - this.singleProgress) * 10.0f);
                            if (a3 > 0.0f) {
                                a2 = 0.0f;
                            } else {
                                a2 = a3;
                            }
                            this.data[i2].paint.setColor(Theme.getColor(this.data[i2].color));
                            this.data[i2].paint.setAlpha(255);
                            float r = this.rectF.width() / 2.0f;
                            double d4 = (double) r;
                            Double.isNaN(d4);
                            double d5 = (double) a2;
                            Double.isNaN(d5);
                            if (Math.abs((float) (((d4 * 3.141592653589793d) / 180.0d) * d5)) <= f) {
                                float centerX = this.rectF.centerX();
                                double d6 = (double) r;
                                double cos = Math.cos(Math.toRadians((double) (-90.0f - (startFrom * 360.0f))));
                                Double.isNaN(d6);
                                float x = centerX + ((float) (d6 * cos));
                                float centerY = this.rectF.centerY();
                                double d7 = (double) r;
                                double sin = Math.sin(Math.toRadians((double) (-90.0f - (360.0f * startFrom))));
                                Double.isNaN(d7);
                                float y = centerY + ((float) (d7 * sin));
                                if (Build.VERSION.SDK_INT >= 21) {
                                    canvas2.drawPoint(x, y, this.data[i2].paint);
                                } else {
                                    this.data[i2].paint.setStyle(Paint.Style.FILL);
                                    canvas2.drawCircle(x, y, this.data[i2].paint.getStrokeWidth() / 2.0f, this.data[i2].paint);
                                }
                            } else {
                                this.data[i2].paint.setStyle(Paint.Style.STROKE);
                                float f7 = a2;
                                float f8 = r;
                                canvas.drawArc(this.rectF, -90.0f - (360.0f * startFrom), a2, false, this.data[i2].paint);
                            }
                        }
                        startFrom += percent2;
                    }
                }
                i2++;
                f = 1.0f;
            }
            float startFrom2 = 0.0f;
            int i3 = 0;
            while (true) {
                ClearViewData[] clearViewDataArr2 = this.data;
                if (i3 >= clearViewDataArr2.length) {
                    break;
                }
                if (clearViewDataArr2[i3] != null) {
                    float[] fArr2 = this.drawingPercentage;
                    if (fArr2[i3] != f2) {
                        float percent3 = fArr2[i3];
                        if (!clearViewDataArr2[i3].firstDraw) {
                            float a4 = (percent3 * -360.0f) + ((1.0f - this.singleProgress) * 10.0f);
                            if (a4 > f2) {
                                a = 0.0f;
                            } else {
                                a = a4;
                            }
                            this.data[i3].paint.setColor(Theme.getColor(this.data[i3].color));
                            this.data[i3].paint.setAlpha(i);
                            float r2 = this.rectF.width() / 2.0f;
                            double d8 = (double) r2;
                            Double.isNaN(d8);
                            double d9 = (double) a;
                            Double.isNaN(d9);
                            float len = (float) (((d8 * 3.141592653589793d) / d) * d9);
                            if (Math.abs(len) <= 1.0f) {
                                float centerX2 = this.rectF.centerX();
                                double d10 = (double) r2;
                                double cos2 = Math.cos(Math.toRadians((double) (-90.0f - (startFrom2 * 360.0f))));
                                Double.isNaN(d10);
                                float x2 = centerX2 + ((float) (d10 * cos2));
                                float centerY2 = this.rectF.centerY();
                                double d11 = (double) r2;
                                percent = percent3;
                                double sin2 = Math.sin(Math.toRadians((double) (-90.0f - (startFrom2 * 360.0f))));
                                Double.isNaN(d11);
                                float y2 = centerY2 + ((float) (d11 * sin2));
                                if (Build.VERSION.SDK_INT >= 21) {
                                    canvas2.drawPoint(x2, y2, this.data[i3].paint);
                                } else {
                                    this.data[i3].paint.setStyle(Paint.Style.FILL);
                                    canvas2.drawCircle(x2, y2, this.data[i3].paint.getStrokeWidth() / 2.0f, this.data[i3].paint);
                                }
                            } else {
                                percent = percent3;
                                this.data[i3].paint.setStyle(Paint.Style.STROKE);
                                float f9 = len;
                                float var_ = r2;
                                canvas.drawArc(this.rectF, -90.0f - (startFrom2 * 360.0f), a, false, this.data[i3].paint);
                            }
                        } else {
                            percent = percent3;
                        }
                        startFrom2 += percent;
                    }
                }
                i3++;
                i = 255;
                f2 = 0.0f;
                d = 180.0d;
            }
            if (this.layout1 != null) {
                canvas.save();
                canvas2.translate((float) ((getMeasuredWidth() - this.layout1.getWidth()) >> 1), (float) ((((getMeasuredHeight() - this.layout1.getHeight()) - this.layout2.getHeight()) >> 1) + AndroidUtilities.dp(2.0f)));
                this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.textPaint2.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.layout1.draw(canvas2);
                canvas2.translate(0.0f, (float) this.layout1.getHeight());
                this.layout2.draw(canvas2);
                canvas.restore();
            }
        }
    }

    public static class ClearViewData {
        public boolean clear = true;
        public String color;
        boolean firstDraw = false;
        Paint paint;
        private final StorageDiagramView parentView;
        public long size;

        public ClearViewData(StorageDiagramView parentView2) {
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            this.parentView = parentView2;
            paint2.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(5.0f));
            this.paint.setStrokeCap(Paint.Cap.ROUND);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
        }

        public void setClear(boolean clear2) {
            if (this.clear != clear2) {
                this.clear = clear2;
                this.parentView.updateDescription();
                this.firstDraw = true;
                this.parentView.update(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void update(boolean animate) {
        long total = 0;
        final ClearViewData[] data2 = this.data;
        if (data2 != null) {
            for (int i = 0; i < data2.length; i++) {
                if (data2[i] != null && data2[i].clear) {
                    total += data2[i].size;
                }
            }
            float k = 0.0f;
            float max = 0.0f;
            this.enabledCount = 0;
            for (int i2 = 0; i2 < data2.length; i2++) {
                if (data2[i2] != null && data2[i2].clear) {
                    this.enabledCount++;
                }
                if (data2[i2] == null || !data2[i2].clear) {
                    this.animateToPercentage[i2] = 0.0f;
                } else {
                    float percent = ((float) data2[i2].size) / ((float) total);
                    if (percent < 0.02777f) {
                        percent = 0.02777f;
                    }
                    k += percent;
                    if (percent > max && data2[i2].clear) {
                        max = percent;
                    }
                    this.animateToPercentage[i2] = percent;
                }
            }
            if (k > 1.0f) {
                float l = 1.0f / k;
                for (int i3 = 0; i3 < data2.length; i3++) {
                    if (data2[i3] != null) {
                        float[] fArr = this.animateToPercentage;
                        fArr[i3] = fArr[i3] * l;
                    }
                }
            }
            if (!animate) {
                System.arraycopy(this.animateToPercentage, 0, this.drawingPercentage, 0, data2.length);
                return;
            }
            System.arraycopy(this.drawingPercentage, 0, this.startFromPercentage, 0, data2.length);
            ValueAnimator valueAnimator2 = this.valueAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.removeAllListeners();
                this.valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.valueAnimator = ofFloat;
            ofFloat.addUpdateListener(new StorageDiagramView$$ExternalSyntheticLambda0(this, data2));
            this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    int i = 0;
                    while (true) {
                        ClearViewData[] clearViewDataArr = data2;
                        if (i < clearViewDataArr.length) {
                            if (clearViewDataArr[i] != null) {
                                clearViewDataArr[i].firstDraw = false;
                            }
                            i++;
                        } else {
                            return;
                        }
                    }
                }
            });
            this.valueAnimator.setDuration(450);
            this.valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
            this.valueAnimator.start();
        }
    }

    /* renamed from: lambda$update$0$org-telegram-ui-Components-StorageDiagramView  reason: not valid java name */
    public /* synthetic */ void m1464lambda$update$0$orgtelegramuiComponentsStorageDiagramView(ClearViewData[] data2, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        for (int i = 0; i < data2.length; i++) {
            this.drawingPercentage[i] = (this.startFromPercentage[i] * (1.0f - v)) + (this.animateToPercentage[i] * v);
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public void updateDescription() {
        if (this.data != null) {
            long total = 0;
            int i = 0;
            while (true) {
                ClearViewData[] clearViewDataArr = this.data;
                if (i >= clearViewDataArr.length) {
                    break;
                }
                if (clearViewDataArr[i] != null && clearViewDataArr[i].clear) {
                    total += this.data[i].size;
                }
                i++;
            }
            String str = " ";
            String[] str2 = AndroidUtilities.formatFileSize(total).split(str);
            if (str2.length > 1) {
                this.layout1 = new StaticLayout(total == 0 ? str : str2[0], this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (total != 0) {
                    str = str2[1];
                }
                this.layout2 = new StaticLayout(str, this.textPaint2, getMeasuredWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            }
        }
    }
}
