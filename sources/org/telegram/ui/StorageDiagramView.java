package org.telegram.ui;

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
import org.telegram.ui.StorageDiagramView;

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
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(110.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(110.0f), NUM));
        this.rectF.set((float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(3.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(3.0f)));
        updateDescription();
        this.textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    public void setData(ClearViewData[] clearViewDataArr) {
        this.data = clearViewDataArr;
        invalidate();
        this.drawingPercentage = new float[clearViewDataArr.length];
        this.animateToPercentage = new float[clearViewDataArr.length];
        this.startFromPercentage = new float[clearViewDataArr.length];
        update(false);
        if (this.enabledCount > 1) {
            this.singleProgress = 0.0f;
        } else {
            this.singleProgress = 1.0f;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        Canvas canvas2;
        Canvas canvas3 = canvas;
        int i2 = Build.VERSION.SDK_INT;
        if (this.data != null) {
            float f = 1.0f;
            if (this.enabledCount > 1) {
                float f2 = this.singleProgress;
                if (f2 > 0.0f) {
                    double d = (double) f2;
                    Double.isNaN(d);
                    float f3 = (float) (d - 0.04d);
                    this.singleProgress = f3;
                    if (f3 < 0.0f) {
                        this.singleProgress = 0.0f;
                    }
                }
            } else {
                float f4 = this.singleProgress;
                if (f4 < 1.0f) {
                    double d2 = (double) f4;
                    Double.isNaN(d2);
                    float f5 = (float) (d2 + 0.04d);
                    this.singleProgress = f5;
                    if (f5 > 1.0f) {
                        this.singleProgress = 1.0f;
                    }
                }
            }
            int i3 = 0;
            int i4 = 0;
            float f6 = 0.0f;
            while (true) {
                ClearViewData[] clearViewDataArr = this.data;
                i = 255;
                if (i4 >= clearViewDataArr.length) {
                    break;
                }
                if (clearViewDataArr[i4] != null) {
                    float[] fArr = this.drawingPercentage;
                    if (fArr[i4] != 0.0f) {
                        float f7 = fArr[i4];
                        if (clearViewDataArr[i4].firstDraw) {
                            float f8 = (-360.0f * f7) + ((f - this.singleProgress) * 10.0f);
                            float f9 = f8 > 0.0f ? 0.0f : f8;
                            clearViewDataArr[i4].paint.setColor(Theme.getColor(clearViewDataArr[i4].color));
                            this.data[i4].paint.setAlpha(255);
                            double width = (double) (this.rectF.width() / 2.0f);
                            Double.isNaN(width);
                            double d3 = (double) f9;
                            Double.isNaN(d3);
                            if (Math.abs((float) (d3 * ((3.141592653589793d * width) / 180.0d))) <= f) {
                                float centerX = this.rectF.centerX();
                                double d4 = (double) (-90.0f - (360.0f * f6));
                                double cos = Math.cos(Math.toRadians(d4));
                                Double.isNaN(width);
                                float var_ = centerX + ((float) (width * cos));
                                float centerY = this.rectF.centerY();
                                double sin = Math.sin(Math.toRadians(d4));
                                Double.isNaN(width);
                                float var_ = centerY + ((float) (width * sin));
                                if (i2 >= 21) {
                                    canvas3.drawPoint(var_, var_, this.data[i4].paint);
                                } else {
                                    this.data[i4].paint.setStyle(Paint.Style.FILL);
                                    canvas3.drawCircle(var_, var_, this.data[i4].paint.getStrokeWidth() / 2.0f, this.data[i4].paint);
                                }
                            } else {
                                this.data[i4].paint.setStyle(Paint.Style.STROKE);
                                canvas.drawArc(this.rectF, -90.0f - (360.0f * f6), f9, false, this.data[i4].paint);
                            }
                        }
                        f6 += f7;
                    }
                }
                i4++;
                f = 1.0f;
            }
            float var_ = 0.0f;
            while (true) {
                ClearViewData[] clearViewDataArr2 = this.data;
                if (i3 >= clearViewDataArr2.length) {
                    break;
                }
                if (clearViewDataArr2[i3] != null) {
                    float[] fArr2 = this.drawingPercentage;
                    if (fArr2[i3] != 0.0f) {
                        float var_ = fArr2[i3];
                        if (!clearViewDataArr2[i3].firstDraw) {
                            float var_ = (var_ * -360.0f) + ((1.0f - this.singleProgress) * 10.0f);
                            float var_ = var_ > 0.0f ? 0.0f : var_;
                            clearViewDataArr2[i3].paint.setColor(Theme.getColor(clearViewDataArr2[i3].color));
                            this.data[i3].paint.setAlpha(i);
                            double width2 = (double) (this.rectF.width() / 2.0f);
                            Double.isNaN(width2);
                            double d5 = (double) var_;
                            Double.isNaN(d5);
                            if (Math.abs((float) (((width2 * 3.141592653589793d) / 180.0d) * d5)) <= 1.0f) {
                                float centerX2 = this.rectF.centerX();
                                double d6 = (double) (-90.0f - (var_ * 360.0f));
                                double cos2 = Math.cos(Math.toRadians(d6));
                                Double.isNaN(width2);
                                float var_ = centerX2 + ((float) (cos2 * width2));
                                float centerY2 = this.rectF.centerY();
                                double sin2 = Math.sin(Math.toRadians(d6));
                                Double.isNaN(width2);
                                float var_ = centerY2 + ((float) (width2 * sin2));
                                if (i2 >= 21) {
                                    canvas2 = canvas;
                                    canvas2.drawPoint(var_, var_, this.data[i3].paint);
                                } else {
                                    canvas2 = canvas;
                                    this.data[i3].paint.setStyle(Paint.Style.FILL);
                                    canvas2.drawCircle(var_, var_, this.data[i3].paint.getStrokeWidth() / 2.0f, this.data[i3].paint);
                                }
                            } else {
                                canvas2 = canvas;
                                this.data[i3].paint.setStyle(Paint.Style.STROKE);
                                canvas.drawArc(this.rectF, -90.0f - (var_ * 360.0f), var_, false, this.data[i3].paint);
                                var_ += var_;
                                i3++;
                                canvas3 = canvas2;
                                i = 255;
                            }
                        } else {
                            canvas2 = canvas3;
                        }
                        var_ += var_;
                        i3++;
                        canvas3 = canvas2;
                        i = 255;
                    }
                }
                canvas2 = canvas3;
                i3++;
                canvas3 = canvas2;
                i = 255;
            }
            Canvas canvas4 = canvas3;
            if (this.layout1 != null) {
                canvas.save();
                canvas4.translate((float) ((getMeasuredWidth() - this.layout1.getWidth()) >> 1), (float) ((((getMeasuredHeight() - this.layout1.getHeight()) - this.layout2.getHeight()) >> 1) + AndroidUtilities.dp(2.0f)));
                this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.textPaint2.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.layout1.draw(canvas4);
                canvas4.translate(0.0f, (float) this.layout1.getHeight());
                this.layout2.draw(canvas4);
                canvas.restore();
            }
        }
    }

    public static class ClearViewData {
        boolean clear = true;
        public String color;
        boolean firstDraw = false;
        Paint paint;
        private final StorageDiagramView parentView;
        long size;

        public ClearViewData(StorageDiagramView storageDiagramView) {
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            this.parentView = storageDiagramView;
            paint2.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(5.0f));
            this.paint.setStrokeCap(Paint.Cap.ROUND);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
        }

        public void setClear(boolean z) {
            if (this.clear != z) {
                this.clear = z;
                this.parentView.updateDescription();
                this.firstDraw = true;
                this.parentView.update(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void update(boolean z) {
        final ClearViewData[] clearViewDataArr = this.data;
        if (clearViewDataArr != null) {
            int i = 0;
            long j = 0;
            for (int i2 = 0; i2 < clearViewDataArr.length; i2++) {
                if (clearViewDataArr[i2] != null && clearViewDataArr[i2].clear) {
                    j += clearViewDataArr[i2].size;
                }
            }
            this.enabledCount = 0;
            float f = 0.0f;
            float f2 = 0.0f;
            for (int i3 = 0; i3 < clearViewDataArr.length; i3++) {
                if (clearViewDataArr[i3] != null && clearViewDataArr[i3].clear) {
                    this.enabledCount++;
                }
                if (clearViewDataArr[i3] == null || !clearViewDataArr[i3].clear) {
                    this.animateToPercentage[i3] = 0.0f;
                } else {
                    float f3 = ((float) clearViewDataArr[i3].size) / ((float) j);
                    if (f3 < 0.02777f) {
                        f3 = 0.02777f;
                    }
                    f += f3;
                    if (f3 > f2 && clearViewDataArr[i3].clear) {
                        f2 = f3;
                    }
                    this.animateToPercentage[i3] = f3;
                }
            }
            if (f > 1.0f) {
                float f4 = 1.0f / f;
                for (int i4 = 0; i4 < clearViewDataArr.length; i4++) {
                    if (clearViewDataArr[i4] != null) {
                        float[] fArr = this.animateToPercentage;
                        fArr[i4] = fArr[i4] * f4;
                    }
                }
            }
            if (!z) {
                while (i < clearViewDataArr.length) {
                    this.drawingPercentage[i] = this.animateToPercentage[i];
                    i++;
                }
                return;
            }
            while (i < clearViewDataArr.length) {
                this.startFromPercentage[i] = this.drawingPercentage[i];
                i++;
            }
            ValueAnimator valueAnimator2 = this.valueAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.removeAllListeners();
                this.valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.valueAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(clearViewDataArr) {
                public final /* synthetic */ StorageDiagramView.ClearViewData[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StorageDiagramView.this.lambda$update$0$StorageDiagramView(this.f$1, valueAnimator);
                }
            });
            this.valueAnimator.addListener(new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(Animator animator) {
                    int i = 0;
                    while (true) {
                        ClearViewData[] clearViewDataArr = clearViewDataArr;
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$0 */
    public /* synthetic */ void lambda$update$0$StorageDiagramView(ClearViewData[] clearViewDataArr, ValueAnimator valueAnimator2) {
        float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
        for (int i = 0; i < clearViewDataArr.length; i++) {
            this.drawingPercentage[i] = (this.startFromPercentage[i] * (1.0f - floatValue)) + (this.animateToPercentage[i] * floatValue);
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public void updateDescription() {
        if (this.data != null) {
            long j = 0;
            int i = 0;
            while (true) {
                ClearViewData[] clearViewDataArr = this.data;
                if (i >= clearViewDataArr.length) {
                    break;
                }
                if (clearViewDataArr[i] != null && clearViewDataArr[i].clear) {
                    j += clearViewDataArr[i].size;
                }
                i++;
            }
            String str = " ";
            String[] split = AndroidUtilities.formatFileSize(j).split(str);
            if ((split != null) && (split.length > 1)) {
                this.layout1 = new StaticLayout(j == 0 ? str : split[0], this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (j != 0) {
                    str = split[1];
                }
                this.layout2 = new StaticLayout(str, this.textPaint2, getMeasuredWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            }
        }
    }
}
