package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class FlatCheckBox extends View {
    int HEIGHT = AndroidUtilities.dp(36.0f);
    int INNER_PADDING = AndroidUtilities.dp(22.0f);
    int P = AndroidUtilities.dp(2.0f);
    int TRANSLETE_TEXT = AndroidUtilities.dp(8.0f);
    boolean attached;
    ValueAnimator checkAnimator;
    Paint checkPaint = new Paint(1);
    public boolean checked;
    int colorActive;
    int colorInactive;
    int colorTextActive;
    public boolean enabled = true;
    Paint fillPaint = new Paint(1);
    int lastW = 0;
    Paint outLinePaint = new Paint(1);
    float progress = 0.0f;
    RectF rectF = new RectF();
    String text;
    TextPaint textPaint = new TextPaint(1);

    public FlatCheckBox(Context context) {
        super(context);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.outLinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.5f));
        this.outLinePaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public void recolor(int i) {
        this.colorActive = Theme.getColor("windowBackgroundWhite");
        this.colorTextActive = -1;
        this.colorInactive = i;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checked = z;
        float f = 1.0f;
        if (!this.attached || !z2) {
            if (!z) {
                f = 0.0f;
            }
            this.progress = f;
            return;
        }
        ValueAnimator valueAnimator = this.checkAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.checkAnimator.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.progress;
        if (!z) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                FlatCheckBox.this.lambda$setChecked$0$FlatCheckBox(valueAnimator);
            }
        });
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    public /* synthetic */ void lambda$setChecked$0$FlatCheckBox(ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setText(String str) {
        this.text = str;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        String str = this.text;
        setMeasuredDimension((str == null ? 0 : (int) this.textPaint.measureText(str)) + (this.INNER_PADDING << 1) + (this.P * 2), this.HEIGHT + AndroidUtilities.dp(4.0f));
        if (getMeasuredWidth() != this.lastW) {
            this.rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.rectF.inset(((float) this.P) + (this.outLinePaint.getStrokeWidth() / 2.0f), ((float) this.P) + (this.outLinePaint.getStrokeWidth() / 2.0f) + ((float) AndroidUtilities.dp(2.0f)));
        }
    }

    public void draw(Canvas canvas) {
        float f;
        super.draw(canvas);
        float f2 = this.progress;
        if (f2 <= 0.5f) {
            f = f2 / 0.5f;
            this.fillPaint.setColor(Color.rgb(Color.red(this.colorActive) + ((int) (((float) (Color.red(this.colorInactive) - Color.red(this.colorActive))) * f)), Color.green(this.colorActive) + ((int) (((float) (Color.green(this.colorInactive) - Color.green(this.colorActive))) * f)), Color.blue(this.colorActive) + ((int) (((float) (Color.blue(this.colorInactive) - Color.blue(this.colorActive))) * f))));
            this.textPaint.setColor(Color.rgb(Color.red(this.colorInactive) + ((int) (((float) (Color.red(this.colorTextActive) - Color.red(this.colorInactive))) * f)), Color.green(this.colorInactive) + ((int) (((float) (Color.green(this.colorTextActive) - Color.green(this.colorInactive))) * f)), Color.blue(this.colorInactive) + ((int) (((float) (Color.blue(this.colorTextActive) - Color.blue(this.colorInactive))) * f))));
        } else {
            this.textPaint.setColor(this.colorTextActive);
            this.fillPaint.setColor(this.colorInactive);
            f = 1.0f;
        }
        int measuredHeight = getMeasuredHeight() >> 1;
        this.outLinePaint.setColor(this.colorInactive);
        RectF rectF2 = this.rectF;
        int i = this.HEIGHT;
        canvas.drawRoundRect(rectF2, ((float) i) / 2.0f, ((float) i) / 2.0f, this.fillPaint);
        RectF rectF3 = this.rectF;
        int i2 = this.HEIGHT;
        canvas.drawRoundRect(rectF3, ((float) i2) / 2.0f, ((float) i2) / 2.0f, this.outLinePaint);
        String str = this.text;
        if (str != null) {
            canvas.drawText(str, ((float) (getMeasuredWidth() >> 1)) + (f * ((float) this.TRANSLETE_TEXT)), ((float) measuredHeight) + (this.textPaint.getTextSize() * 0.35f), this.textPaint);
        }
        float f3 = 2.0f - (this.progress / 0.5f);
        canvas.save();
        canvas.scale(0.9f, 0.9f, AndroidUtilities.dpf2(7.0f), (float) measuredHeight);
        canvas.translate((float) AndroidUtilities.dp(12.0f), (float) (measuredHeight - AndroidUtilities.dp(9.0f)));
        if (this.progress > 0.5f) {
            this.checkPaint.setColor(this.colorTextActive);
            float f4 = 1.0f - f3;
            Canvas canvas2 = canvas;
            canvas2.drawLine(AndroidUtilities.dpf2(7.0f), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (AndroidUtilities.dpf2(7.0f) - (((float) AndroidUtilities.dp(4.0f)) * f4))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(4.0f)) * f4))), this.checkPaint);
            canvas.drawLine((float) ((int) AndroidUtilities.dpf2(7.0f)), (float) ((int) AndroidUtilities.dpf2(13.0f)), (float) ((int) (AndroidUtilities.dpf2(7.0f) + (((float) AndroidUtilities.dp(8.0f)) * f4))), (float) ((int) (AndroidUtilities.dpf2(13.0f) - (((float) AndroidUtilities.dp(8.0f)) * f4))), this.checkPaint);
        }
        canvas.restore();
    }

    public void denied() {
        AndroidUtilities.shakeView(this, 2.0f, 0);
    }
}
