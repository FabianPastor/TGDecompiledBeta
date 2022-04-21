package org.telegram.ui.Components.voip;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;

public class CellFlickerDrawable {
    public float animationSpeedScale;
    public boolean drawFrame;
    private Shader gradientShader;
    private Shader gradientShader2;
    long lastUpdateTime;
    Matrix matrix;
    private Paint paint;
    private Paint paintOutline;
    int parentWidth;
    float progress;
    public boolean repeatEnabled;
    public float repeatProgress;
    int size;

    public CellFlickerDrawable() {
        this(64, 204);
    }

    public CellFlickerDrawable(int a1, int a2) {
        this.paint = new Paint(1);
        this.paintOutline = new Paint(1);
        this.matrix = new Matrix();
        this.repeatEnabled = true;
        this.drawFrame = true;
        this.repeatProgress = 1.2f;
        this.animationSpeedScale = 1.0f;
        this.size = AndroidUtilities.dp(160.0f);
        this.gradientShader = new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(-1, a1), 0}, (float[]) null, Shader.TileMode.CLAMP);
        int[] iArr = {0, ColorUtils.setAlphaComponent(-1, a2), 0};
        this.gradientShader2 = new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, iArr, (float[]) null, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradientShader);
        this.paintOutline.setShader(this.gradientShader2);
        this.paintOutline.setStyle(Paint.Style.STROKE);
        this.paintOutline.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public void setColors(int color) {
        setColors(color, 64, 204);
    }

    public void setColors(int color, int alpha1, int alpha2) {
        this.gradientShader = new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(color, alpha1), 0}, (float[]) null, Shader.TileMode.CLAMP);
        int[] iArr = {0, ColorUtils.setAlphaComponent(color, alpha2), 0};
        this.gradientShader2 = new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, iArr, (float[]) null, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradientShader);
        this.paintOutline.setShader(this.gradientShader2);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress2) {
        this.progress = progress2;
    }

    public void draw(Canvas canvas, RectF rectF, float rad) {
        if (this.progress <= 1.0f || this.repeatEnabled) {
            long currentTime = System.currentTimeMillis();
            long j = this.lastUpdateTime;
            if (j != 0) {
                long dt = currentTime - j;
                if (dt > 10) {
                    float f = this.progress + ((((float) dt) / 1200.0f) * this.animationSpeedScale);
                    this.progress = f;
                    if (f > this.repeatProgress) {
                        this.progress = 0.0f;
                    }
                    this.lastUpdateTime = currentTime;
                }
            } else {
                this.lastUpdateTime = currentTime;
            }
            float f2 = this.progress;
            if (f2 <= 1.0f) {
                int i = this.parentWidth;
                int i2 = this.size;
                this.matrix.setTranslate((((float) (i + (i2 * 2))) * f2) - ((float) i2), 0.0f);
                this.gradientShader.setLocalMatrix(this.matrix);
                this.gradientShader2.setLocalMatrix(this.matrix);
                canvas.drawRoundRect(rectF, rad, rad, this.paint);
                if (this.drawFrame) {
                    canvas.drawRoundRect(rectF, rad, rad, this.paintOutline);
                }
            }
        }
    }

    public void draw(Canvas canvas, GroupCallMiniTextureView view) {
        long currentTime = System.currentTimeMillis();
        long j = this.lastUpdateTime;
        if (j != 0) {
            long dt = currentTime - j;
            if (dt > 10) {
                float f = this.progress + (((float) dt) / 500.0f);
                this.progress = f;
                if (f > 4.0f) {
                    this.progress = 0.0f;
                }
                this.lastUpdateTime = currentTime;
            }
        } else {
            this.lastUpdateTime = currentTime;
        }
        float f2 = this.progress;
        if (f2 <= 1.0f) {
            int i = this.parentWidth;
            int i2 = this.size;
            this.matrix.setTranslate(((((float) (i + (i2 * 2))) * f2) - ((float) i2)) - view.getX(), 0.0f);
            this.gradientShader.setLocalMatrix(this.matrix);
            this.gradientShader2.setLocalMatrix(this.matrix);
            AndroidUtilities.rectTmp.set(view.textureView.currentClipHorizontal, view.textureView.currentClipVertical, ((float) view.textureView.getMeasuredWidth()) - view.textureView.currentClipHorizontal, ((float) view.textureView.getMeasuredHeight()) - view.textureView.currentClipVertical);
            canvas.drawRect(AndroidUtilities.rectTmp, this.paint);
            if (this.drawFrame) {
                canvas.drawRoundRect(AndroidUtilities.rectTmp, view.textureView.roundRadius, view.textureView.roundRadius, this.paintOutline);
            }
        }
    }

    public void setParentWidth(int parentWidth2) {
        this.parentWidth = parentWidth2;
    }
}
