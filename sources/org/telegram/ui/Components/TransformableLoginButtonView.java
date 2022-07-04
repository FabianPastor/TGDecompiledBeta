package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class TransformableLoginButtonView extends View {
    private static final float ARROW_BACK_SIZE = 9.0f;
    private static final float ARROW_IN = 0.4f;
    private static final float ARROW_PADDING = 21.0f;
    private static final float BUTTON_RADIUS_DP = 6.0f;
    private static final float BUTTON_TEXT_IN = 0.6f;
    private static final float CIRCLE_RADIUS_DP = 32.0f;
    private static final float LEFT_CHECK_LINE = 8.0f;
    private static final float RIGHT_CHECK_LINE = 16.0f;
    public static final int TRANSFORM_ARROW_CHECK = 1;
    public static final int TRANSFORM_OPEN_ARROW = 0;
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

    @Retention(RetentionPolicy.SOURCE)
    private @interface TransformType {
    }

    public TransformableLoginButtonView(Context context) {
        super(context);
        this.backgroundPaint.setColor(Theme.getColor("chats_actionBackground"));
        this.outlinePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setDrawBackground(boolean drawBackground2) {
        this.drawBackground = drawBackground2;
    }

    public void setRippleDrawable(Drawable d) {
        this.rippleDrawable = d;
        invalidate();
    }

    public void setTransformType(int transformType2) {
        this.transformType = transformType2;
        invalidate();
    }

    public void setBackgroundColor(int color) {
        this.backgroundPaint.setColor(color);
        invalidate();
    }

    public void setColor(int color) {
        this.outlinePaint.setColor(color);
        invalidate();
    }

    public void setButtonText(TextPaint textPaint2, String buttonText2) {
        this.textPaint = textPaint2;
        this.buttonText = buttonText2;
        this.outlinePaint.setColor(textPaint2.getColor());
        this.buttonWidth = textPaint2.measureText(buttonText2);
    }

    public void setProgress(float p) {
        this.progress = p;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.drawBackground) {
            float rad = (float) AndroidUtilities.dp(((this.transformType == 0 ? this.progress : 1.0f) * 26.0f) + 6.0f);
            this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            canvas2.drawRoundRect(this.rect, rad, rad, this.backgroundPaint);
        }
        switch (this.transformType) {
            case 0:
                TextPaint textPaint2 = this.textPaint;
                if (!(textPaint2 == null || this.buttonText == null)) {
                    int alpha = textPaint2.getAlpha();
                    this.textPaint.setAlpha((int) (((float) alpha) * (1.0f - (Math.min(0.6f, this.progress) / 0.6f))));
                    canvas2.drawText(this.buttonText, (((float) getWidth()) - this.buttonWidth) / 2.0f, ((((float) getHeight()) / 2.0f) + (this.textPaint.getTextSize() / 2.0f)) - ((float) AndroidUtilities.dp(1.75f)), this.textPaint);
                    this.textPaint.setAlpha(alpha);
                }
                float arrowProgress = (Math.max(0.4f, this.progress) - 0.4f) / 0.6f;
                if (arrowProgress != 0.0f) {
                    float endX = ((float) AndroidUtilities.dp(21.0f)) + (((float) (getWidth() - (AndroidUtilities.dp(21.0f) * 2))) * arrowProgress);
                    float centerY = ((float) getHeight()) / 2.0f;
                    canvas.drawLine((float) AndroidUtilities.dp(21.0f), centerY, endX, centerY, this.outlinePaint);
                    float backSize = ((float) AndroidUtilities.dp(9.0f)) * arrowProgress;
                    double d = (double) endX;
                    double cos = Math.cos(0.7853981633974483d);
                    double d2 = (double) backSize;
                    Double.isNaN(d2);
                    Double.isNaN(d);
                    float backX = (float) (d - (cos * d2));
                    double sin = Math.sin(0.7853981633974483d);
                    double d3 = (double) backSize;
                    Double.isNaN(d3);
                    float backY = (float) (sin * d3);
                    Canvas canvas3 = canvas;
                    float f = endX;
                    float f2 = centerY;
                    float f3 = backX;
                    canvas3.drawLine(f, f2, f3, centerY - backY, this.outlinePaint);
                    canvas3.drawLine(f, f2, f3, centerY + backY, this.outlinePaint);
                    break;
                }
                break;
            case 1:
                float startX = (float) AndroidUtilities.dp(21.0f);
                float endX2 = (float) (getWidth() - AndroidUtilities.dp(21.0f));
                float centerY2 = ((float) getHeight()) / 2.0f;
                canvas.save();
                canvas2.translate(((float) (-AndroidUtilities.dp(2.0f))) * this.progress, 0.0f);
                canvas2.rotate(this.progress * 90.0f, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
                canvas.drawLine(startX + ((endX2 - startX) * this.progress), centerY2, endX2, centerY2, this.outlinePaint);
                int leftSize = AndroidUtilities.dp((this.progress * -1.0f) + 9.0f);
                int rightSize = AndroidUtilities.dp((this.progress * 7.0f) + 9.0f);
                double d4 = (double) endX2;
                double d5 = (double) leftSize;
                double cos2 = Math.cos(0.7853981633974483d);
                Double.isNaN(d5);
                Double.isNaN(d4);
                float f4 = (float) (d4 - (d5 * cos2));
                double d6 = (double) centerY2;
                double d7 = (double) leftSize;
                double sin2 = Math.sin(0.7853981633974483d);
                Double.isNaN(d7);
                Double.isNaN(d6);
                canvas.drawLine(endX2, centerY2, f4, (float) (d6 + (d7 * sin2)), this.outlinePaint);
                double d8 = (double) endX2;
                double d9 = (double) rightSize;
                double cos3 = Math.cos(0.7853981633974483d);
                Double.isNaN(d9);
                Double.isNaN(d8);
                float f5 = (float) (d8 - (d9 * cos3));
                double d10 = (double) centerY2;
                double d11 = (double) rightSize;
                double sin3 = Math.sin(0.7853981633974483d);
                Double.isNaN(d11);
                Double.isNaN(d10);
                canvas.drawLine(endX2, centerY2, f5, (float) (d10 - (d11 * sin3)), this.outlinePaint);
                canvas.restore();
                break;
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

    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (this.rippleDrawable != null && Build.VERSION.SDK_INT >= 21) {
            this.rippleDrawable.setHotspot(x, y);
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
            r0 = 0
            goto L_0x0010
        L_0x000f:
            r0 = 1
        L_0x0010:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TransformableLoginButtonView.verifyDrawable(android.graphics.drawable.Drawable):boolean");
    }
}
