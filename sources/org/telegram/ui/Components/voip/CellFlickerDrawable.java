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
    public boolean drawFrame;
    private final Shader gradientShader;
    private final Shader gradientShader2;
    long lastUpdateTime;
    Matrix matrix;
    private final Paint paint;
    private final Paint paintOutline;
    int parentWidth;
    float progress;
    public float repeatProgress;
    int size;

    public CellFlickerDrawable() {
        this(64, 204);
    }

    public CellFlickerDrawable(int a1, int a2) {
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        Paint paint3 = new Paint(1);
        this.paintOutline = paint3;
        this.matrix = new Matrix();
        this.drawFrame = true;
        this.repeatProgress = 1.2f;
        this.size = AndroidUtilities.dp(160.0f);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(-1, a1), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.gradientShader = linearGradient;
        int[] iArr = {0, ColorUtils.setAlphaComponent(-1, a2), 0};
        LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, iArr, (float[]) null, Shader.TileMode.CLAMP);
        this.gradientShader2 = linearGradient2;
        paint2.setShader(linearGradient);
        paint3.setShader(linearGradient2);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public void draw(Canvas canvas, RectF rectF, float rad) {
        long currentTime = System.currentTimeMillis();
        long j = this.lastUpdateTime;
        if (j != 0) {
            long dt = currentTime - j;
            if (dt > 10) {
                float f = this.progress + (((float) dt) / 1200.0f);
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
