package org.telegram.ui.Components.voip;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SvgHelper;
/* loaded from: classes3.dex */
public class CellFlickerDrawable {
    public float animationSpeedScale;
    public boolean drawFrame;
    public boolean frameInside;
    private Shader gradientShader;
    private Shader gradientShader2;
    long lastUpdateTime;
    Matrix matrix;
    Runnable onRestartCallback;
    private Paint paint;
    private Paint paintOutline;
    View parentView;
    int parentWidth;
    public float progress;
    public boolean repeatEnabled;
    public float repeatProgress;
    int size;

    public CellFlickerDrawable() {
        this(64, 204);
    }

    public CellFlickerDrawable(int i, int i2) {
        this.paint = new Paint(1);
        this.paintOutline = new Paint(1);
        this.matrix = new Matrix();
        this.repeatEnabled = true;
        this.drawFrame = true;
        this.frameInside = false;
        this.repeatProgress = 1.2f;
        this.animationSpeedScale = 1.0f;
        this.size = AndroidUtilities.dp(160.0f);
        this.gradientShader = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(-1, i), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.gradientShader2 = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(-1, i2), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradientShader);
        this.paintOutline.setShader(this.gradientShader2);
        this.paintOutline.setStyle(Paint.Style.STROKE);
        this.paintOutline.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    public void setColors(int i, int i2, int i3) {
        this.gradientShader = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(i, i2), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.gradientShader2 = new LinearGradient(0.0f, 0.0f, this.size, 0.0f, new int[]{0, ColorUtils.setAlphaComponent(i, i3), 0}, (float[]) null, Shader.TileMode.CLAMP);
        this.paint.setShader(this.gradientShader);
        this.paintOutline.setShader(this.gradientShader2);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float f) {
        this.progress = f;
    }

    public void draw(Canvas canvas, RectF rectF, float f, View view) {
        update(view);
        canvas.drawRoundRect(rectF, f, f, this.paint);
        if (this.drawFrame) {
            if (this.frameInside) {
                rectF.inset(this.paintOutline.getStrokeWidth() / 2.0f, this.paintOutline.getStrokeWidth() / 2.0f);
            }
            canvas.drawRoundRect(rectF, f, f, this.paintOutline);
        }
    }

    public void draw(Canvas canvas, Path path, View view) {
        update(view);
        canvas.drawPath(path, this.paint);
        if (this.drawFrame) {
            canvas.drawPath(path, this.paintOutline);
        }
    }

    private void update(View view) {
        if (this.progress <= 1.0f || this.repeatEnabled) {
            if (view != null) {
                view.invalidate();
            }
            long currentTimeMillis = System.currentTimeMillis();
            long j = this.lastUpdateTime;
            if (j != 0) {
                long j2 = currentTimeMillis - j;
                if (j2 > 10) {
                    float f = this.progress + ((((float) j2) / 1200.0f) * this.animationSpeedScale);
                    this.progress = f;
                    if (f > this.repeatProgress) {
                        this.progress = 0.0f;
                        Runnable runnable = this.onRestartCallback;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                    this.lastUpdateTime = currentTimeMillis;
                }
            } else {
                this.lastUpdateTime = currentTimeMillis;
            }
            int i = this.parentWidth;
            int i2 = this.size;
            float f2 = ((i + (i2 * 2)) * this.progress) - i2;
            this.matrix.reset();
            this.matrix.setTranslate(f2, 0.0f);
            this.gradientShader.setLocalMatrix(this.matrix);
            this.gradientShader2.setLocalMatrix(this.matrix);
        }
    }

    public void draw(Canvas canvas, GroupCallMiniTextureView groupCallMiniTextureView) {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.lastUpdateTime;
        if (j != 0) {
            long j2 = currentTimeMillis - j;
            if (j2 > 10) {
                float f = this.progress + (((float) j2) / 500.0f);
                this.progress = f;
                if (f > 4.0f) {
                    this.progress = 0.0f;
                    Runnable runnable = this.onRestartCallback;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                this.lastUpdateTime = currentTimeMillis;
            }
        } else {
            this.lastUpdateTime = currentTimeMillis;
        }
        float f2 = this.progress;
        if (f2 > 1.0f) {
            return;
        }
        int i = this.parentWidth;
        int i2 = this.size;
        this.matrix.setTranslate((((i + (i2 * 2)) * f2) - i2) - groupCallMiniTextureView.getX(), 0.0f);
        this.gradientShader.setLocalMatrix(this.matrix);
        this.gradientShader2.setLocalMatrix(this.matrix);
        RectF rectF = AndroidUtilities.rectTmp;
        VoIPTextureView voIPTextureView = groupCallMiniTextureView.textureView;
        float f3 = voIPTextureView.currentClipHorizontal;
        float f4 = voIPTextureView.currentClipVertical;
        VoIPTextureView voIPTextureView2 = groupCallMiniTextureView.textureView;
        rectF.set(f3, f4, voIPTextureView.getMeasuredWidth() - voIPTextureView2.currentClipHorizontal, voIPTextureView2.getMeasuredHeight() - groupCallMiniTextureView.textureView.currentClipVertical);
        canvas.drawRect(rectF, this.paint);
        if (!this.drawFrame) {
            return;
        }
        if (this.frameInside) {
            rectF.inset(this.paintOutline.getStrokeWidth() / 2.0f, this.paintOutline.getStrokeWidth() / 2.0f);
        }
        float f5 = groupCallMiniTextureView.textureView.roundRadius;
        canvas.drawRoundRect(rectF, f5, f5, this.paintOutline);
    }

    public void setParentWidth(int i) {
        this.parentWidth = i;
    }

    public DrawableInterface getDrawableInterface(View view, SvgHelper.SvgDrawable svgDrawable) {
        this.parentView = view;
        return new DrawableInterface(svgDrawable);
    }

    public void setOnRestartCallback(Runnable runnable) {
        this.onRestartCallback = runnable;
    }

    /* loaded from: classes3.dex */
    public class DrawableInterface extends Drawable {
        public float radius;
        SvgHelper.SvgDrawable svgDrawable;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public DrawableInterface(SvgHelper.SvgDrawable svgDrawable) {
            this.svgDrawable = svgDrawable;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            CellFlickerDrawable.this.setParentWidth(getBounds().width());
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(getBounds());
            CellFlickerDrawable.this.draw(canvas, rectF, this.radius, null);
            SvgHelper.SvgDrawable svgDrawable = this.svgDrawable;
            if (svgDrawable != null) {
                svgDrawable.setPaint(CellFlickerDrawable.this.paint);
                CellFlickerDrawable cellFlickerDrawable = CellFlickerDrawable.this;
                int i = cellFlickerDrawable.parentWidth;
                int i2 = cellFlickerDrawable.size;
                float f = (((i2 * 2) + i) * cellFlickerDrawable.progress) - i2;
                float scale = this.svgDrawable.getScale(getBounds().width(), getBounds().height());
                CellFlickerDrawable.this.matrix.reset();
                CellFlickerDrawable cellFlickerDrawable2 = CellFlickerDrawable.this;
                cellFlickerDrawable2.matrix.setScale(1.0f / scale, 0.0f, cellFlickerDrawable2.size / 2.0f, 0.0f);
                CellFlickerDrawable.this.matrix.setTranslate((f - this.svgDrawable.getBounds().left) - (CellFlickerDrawable.this.size / scale), 0.0f);
                CellFlickerDrawable.this.gradientShader.setLocalMatrix(CellFlickerDrawable.this.matrix);
                int i3 = ((int) (i * 0.5f)) / 2;
                this.svgDrawable.setBounds(getBounds().centerX() - i3, getBounds().centerY() - i3, getBounds().centerX() + i3, getBounds().centerY() + i3);
                this.svgDrawable.draw(canvas);
            }
            CellFlickerDrawable.this.parentView.invalidate();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            CellFlickerDrawable.this.paint.setAlpha(i);
            CellFlickerDrawable.this.paintOutline.setAlpha(i);
        }
    }
}
