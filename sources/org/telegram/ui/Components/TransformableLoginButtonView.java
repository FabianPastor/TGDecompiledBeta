package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class TransformableLoginButtonView extends View {
    private Paint backgroundPaint = new Paint(1);
    private String buttonText;
    private float buttonWidth;
    private boolean drawBackground = true;
    private Paint outlinePaint = new Paint(1);
    private float progress;
    private RectF rect = new RectF();
    private Drawable rippleDrawable;
    private TextPaint textPaint;
    private int transformType = 0;

    public TransformableLoginButtonView(Context context) {
        super(context);
        this.backgroundPaint.setColor(Theme.getColor("chats_actionBackground"));
        this.outlinePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setRippleDrawable(Drawable drawable) {
        this.rippleDrawable = drawable;
        invalidate();
    }

    public void setTransformType(int i) {
        this.transformType = i;
        invalidate();
    }

    public void setBackgroundColor(int i) {
        this.backgroundPaint.setColor(i);
        invalidate();
    }

    public void setColor(int i) {
        this.outlinePaint.setColor(i);
        invalidate();
    }

    public void setButtonText(TextPaint textPaint2, String str) {
        this.textPaint = textPaint2;
        this.buttonText = str;
        this.outlinePaint.setColor(textPaint2.getColor());
        this.buttonWidth = textPaint2.measureText(str);
    }

    public void setProgress(float f) {
        this.progress = f;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.drawBackground) {
            float dp = (float) AndroidUtilities.dp(((this.transformType == 0 ? this.progress : 1.0f) * 26.0f) + 6.0f);
            this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            canvas2.drawRoundRect(this.rect, dp, dp, this.backgroundPaint);
        }
        int i = this.transformType;
        if (i == 0) {
            TextPaint textPaint2 = this.textPaint;
            if (!(textPaint2 == null || this.buttonText == null)) {
                int alpha = textPaint2.getAlpha();
                this.textPaint.setAlpha((int) (((float) alpha) * (1.0f - (Math.min(0.6f, this.progress) / 0.6f))));
                canvas2.drawText(this.buttonText, (((float) getWidth()) - this.buttonWidth) / 2.0f, ((((float) getHeight()) / 2.0f) + (this.textPaint.getTextSize() / 2.0f)) - ((float) AndroidUtilities.dp(1.75f)), this.textPaint);
                this.textPaint.setAlpha(alpha);
            }
            float max = (Math.max(0.4f, this.progress) - 0.4f) / 0.6f;
            if (max != 0.0f) {
                float dp2 = ((float) AndroidUtilities.dp(21.0f)) + (((float) (getWidth() - (AndroidUtilities.dp(21.0f) * 2))) * max);
                float height = ((float) getHeight()) / 2.0f;
                canvas.drawLine((float) AndroidUtilities.dp(21.0f), height, dp2, height, this.outlinePaint);
                double d = (double) dp2;
                double cos = Math.cos(0.7853981633974483d);
                double dp3 = (double) (((float) AndroidUtilities.dp(9.0f)) * max);
                Double.isNaN(dp3);
                Double.isNaN(d);
                float f = (float) (d - (cos * dp3));
                double sin = Math.sin(0.7853981633974483d);
                Double.isNaN(dp3);
                float f2 = (float) (sin * dp3);
                Canvas canvas3 = canvas;
                float f3 = dp2;
                float f4 = height;
                float f5 = f;
                canvas3.drawLine(f3, f4, f5, height - f2, this.outlinePaint);
                canvas3.drawLine(f3, f4, f5, height + f2, this.outlinePaint);
            }
        } else if (i == 1) {
            float dp4 = (float) AndroidUtilities.dp(21.0f);
            float width = (float) (getWidth() - AndroidUtilities.dp(21.0f));
            float height2 = ((float) getHeight()) / 2.0f;
            canvas.save();
            canvas2.translate(((float) (-AndroidUtilities.dp(2.0f))) * this.progress, 0.0f);
            canvas2.rotate(this.progress * 90.0f, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
            canvas.drawLine(((width - dp4) * this.progress) + dp4, height2, width, height2, this.outlinePaint);
            int dp5 = AndroidUtilities.dp((this.progress * -1.0f) + 9.0f);
            int dp6 = AndroidUtilities.dp((this.progress * 7.0f) + 9.0f);
            double d2 = (double) width;
            double d3 = (double) dp5;
            double cos2 = Math.cos(0.7853981633974483d);
            Double.isNaN(d3);
            Double.isNaN(d2);
            double d4 = (double) height2;
            double sin2 = Math.sin(0.7853981633974483d);
            Double.isNaN(d3);
            Double.isNaN(d4);
            double d5 = d4;
            canvas.drawLine(width, height2, (float) (d2 - (cos2 * d3)), (float) ((d3 * sin2) + d4), this.outlinePaint);
            double d6 = (double) dp6;
            double cos3 = Math.cos(0.7853981633974483d);
            Double.isNaN(d6);
            Double.isNaN(d2);
            double sin3 = Math.sin(0.7853981633974483d);
            Double.isNaN(d6);
            Double.isNaN(d5);
            canvas.drawLine(width, height2, (float) (d2 - (cos3 * d6)), (float) (d5 - (d6 * sin3)), this.outlinePaint);
            canvas.restore();
        }
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
            if (Build.VERSION.SDK_INT >= 21) {
                this.rippleDrawable.setHotspotBounds(0, 0, getWidth(), getHeight());
            }
            this.rippleDrawable.draw(canvas2);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.setState(getDrawableState());
            invalidate();
        }
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        Drawable drawable = this.rippleDrawable;
        if (drawable != null && Build.VERSION.SDK_INT >= 21) {
            drawable.setHotspot(f, f2);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.rippleDrawable;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean verifyDrawable(android.graphics.drawable.Drawable r2) {
        /*
            r1 = this;
            boolean r0 = super.verifyDrawable(r2)
            if (r0 != 0) goto L_0x000f
            android.graphics.drawable.Drawable r0 = r1.rippleDrawable
            if (r0 == 0) goto L_0x000d
            if (r2 != r0) goto L_0x000d
            goto L_0x000f
        L_0x000d:
            r2 = 0
            goto L_0x0010
        L_0x000f:
            r2 = 1
        L_0x0010:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TransformableLoginButtonView.verifyDrawable(android.graphics.drawable.Drawable):boolean");
    }
}
