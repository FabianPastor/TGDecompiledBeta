package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

/* compiled from: MessageContainsEmojiButton */
class LoadingDrawable extends Drawable {
    private LinearGradient gradient;
    private int gradientColor1;
    private int gradientColor2;
    private int gradientWidth;
    public Paint paint = new Paint(1);
    private Path path = new Path();
    private RectF[] rects;
    private Theme.ResourcesProvider resourcesProvider;
    private long start = -1;

    public int getOpacity() {
        return -2;
    }

    public LoadingDrawable(Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (getPaintAlpha() > 0) {
            int min = Math.min(AndroidUtilities.dp(400.0f), bounds.width());
            int color = Theme.getColor("dialogBackground", this.resourcesProvider);
            int color2 = Theme.getColor("dialogBackgroundGray", this.resourcesProvider);
            int i = 0;
            if (!(this.gradient != null && min == this.gradientWidth && color == this.gradientColor1 && color2 == this.gradientColor2)) {
                this.gradientWidth = min;
                this.gradientColor1 = color;
                this.gradientColor2 = color2;
                int i2 = this.gradientColor1;
                LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, (float) this.gradientWidth, 0.0f, new int[]{i2, this.gradientColor2, i2}, new float[]{0.0f, 0.67f, 1.0f}, Shader.TileMode.REPEAT);
                this.gradient = linearGradient;
                this.paint.setShader(linearGradient);
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (this.start < 0) {
                this.start = elapsedRealtime;
            }
            int i3 = this.gradientWidth;
            float f = ((float) i3) - (((((float) (elapsedRealtime - this.start)) / 1000.0f) * ((float) i3)) % ((float) i3));
            canvas.save();
            canvas.clipRect(bounds);
            canvas.translate(-f, 0.0f);
            this.path.reset();
            if (this.rects != null) {
                while (true) {
                    RectF[] rectFArr = this.rects;
                    if (i >= rectFArr.length) {
                        break;
                    }
                    RectF rectF = rectFArr[i];
                    if (rectF != null) {
                        this.path.addRect(rectF.left + f, rectF.top, rectF.right + f, rectF.bottom, Path.Direction.CW);
                    }
                    i++;
                }
            } else {
                this.path.addRect(((float) bounds.left) + f, (float) bounds.top, ((float) bounds.right) + f, (float) bounds.bottom, Path.Direction.CW);
            }
            canvas.drawPath(this.path, this.paint);
            canvas.translate(f, 0.0f);
            canvas.restore();
            invalidateSelf();
        }
    }

    public int getPaintAlpha() {
        return this.paint.getAlpha();
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        if (i > 0) {
            invalidateSelf();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }
}
