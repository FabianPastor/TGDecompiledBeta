package org.telegram.ui;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import androidx.core.graphics.ColorUtils;

public class RoundVideoProgressShadow {
    int lastSizesHash;
    Matrix matrix = new Matrix();
    RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, 100.0f, new int[]{0, 0, ColorUtils.setAlphaComponent(-16777216, 40)}, new float[]{0.0f, 0.7f, 1.0f}, Shader.TileMode.CLAMP);
    Paint shaderPaint = new Paint();

    public RoundVideoProgressShadow() {
        this.shaderPaint.setShader(this.radialGradient);
    }

    public void draw(Canvas canvas, float cx, float cy, float radius, float alpha) {
        if (((int) cx) + (((int) cy) << 12) + (((int) radius) << 24) != this.lastSizesHash) {
            this.matrix.reset();
            float s = radius / 100.0f;
            this.matrix.setTranslate(cx, cy);
            this.matrix.preScale(s, s);
            this.radialGradient.setLocalMatrix(this.matrix);
        }
        this.shaderPaint.setAlpha((int) (255.0f * alpha));
        canvas.drawCircle(cx, cy, radius, this.shaderPaint);
    }
}
