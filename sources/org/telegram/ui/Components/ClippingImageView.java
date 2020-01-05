package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver.BitmapHolder;

public class ClippingImageView extends View {
    private float animationProgress;
    private float[][] animationValues;
    private RectF bitmapRect;
    private BitmapShader bitmapShader;
    private BitmapHolder bmp;
    private int clipBottom;
    private int clipLeft;
    private int clipRight;
    private int clipTop;
    private RectF drawRect;
    private int imageX;
    private int imageY;
    private Matrix matrix;
    private boolean needRadius;
    private int orientation;
    private Paint paint = new Paint(2);
    private int radius;
    private Paint roundPaint;
    private RectF roundRect;
    private Matrix shaderMatrix;

    public ClippingImageView(Context context) {
        super(context);
        this.paint.setFilterBitmap(true);
        this.matrix = new Matrix();
        this.drawRect = new RectF();
        this.bitmapRect = new RectF();
        this.roundPaint = new Paint(3);
        this.roundRect = new RectF();
        this.shaderMatrix = new Matrix();
    }

    public void setAnimationValues(float[][] fArr) {
        this.animationValues = fArr;
    }

    @Keep
    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float f) {
        this.animationProgress = f;
        float[][] fArr = this.animationValues;
        setScaleX(fArr[0][0] + ((fArr[1][0] - fArr[0][0]) * this.animationProgress));
        fArr = this.animationValues;
        setScaleY(fArr[0][1] + ((fArr[1][1] - fArr[0][1]) * this.animationProgress));
        fArr = this.animationValues;
        setTranslationX(fArr[0][2] + ((fArr[1][2] - fArr[0][2]) * this.animationProgress));
        fArr = this.animationValues;
        setTranslationY(fArr[0][3] + ((fArr[1][3] - fArr[0][3]) * this.animationProgress));
        fArr = this.animationValues;
        setClipHorizontal((int) (fArr[0][4] + ((fArr[1][4] - fArr[0][4]) * this.animationProgress)));
        fArr = this.animationValues;
        setClipTop((int) (fArr[0][5] + ((fArr[1][5] - fArr[0][5]) * this.animationProgress)));
        fArr = this.animationValues;
        setClipBottom((int) (fArr[0][6] + ((fArr[1][6] - fArr[0][6]) * this.animationProgress)));
        fArr = this.animationValues;
        setRadius((int) (fArr[0][7] + ((fArr[1][7] - fArr[0][7]) * this.animationProgress)));
        fArr = this.animationValues;
        if (fArr[0].length > 8) {
            setImageY((int) (fArr[0][8] + ((fArr[1][8] - fArr[0][8]) * this.animationProgress)));
            fArr = this.animationValues;
            setImageX((int) (fArr[0][9] + ((fArr[1][9] - fArr[0][9]) * this.animationProgress)));
        }
        invalidate();
    }

    public void getClippedVisibleRect(RectF rectF) {
        rectF.left = getTranslationX();
        rectF.top = getTranslationY();
        rectF.right = rectF.left + (((float) getMeasuredWidth()) * getScaleX());
        rectF.bottom = rectF.top + (((float) getMeasuredHeight()) * getScaleY());
        rectF.left += (float) this.clipLeft;
        rectF.top += (float) this.clipTop;
        rectF.right -= (float) this.clipRight;
        rectF.bottom -= (float) this.clipBottom;
    }

    public int getClipBottom() {
        return this.clipBottom;
    }

    public int getClipHorizontal() {
        return this.clipRight;
    }

    public int getClipLeft() {
        return this.clipLeft;
    }

    public int getClipRight() {
        return this.clipRight;
    }

    public int getClipTop() {
        return this.clipTop;
    }

    public int getRadius() {
        return this.radius;
    }

    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            BitmapHolder bitmapHolder = this.bmp;
            if (!(bitmapHolder == null || bitmapHolder.isRecycled())) {
                float scaleY = getScaleY();
                canvas.save();
                int i;
                if (this.needRadius) {
                    this.shaderMatrix.reset();
                    this.roundRect.set(((float) this.imageX) / scaleY, ((float) this.imageY) / scaleY, ((float) getWidth()) - (((float) this.imageX) / scaleY), ((float) getHeight()) - (((float) this.imageY) / scaleY));
                    this.bitmapRect.set(0.0f, 0.0f, (float) this.bmp.getWidth(), (float) this.bmp.getHeight());
                    AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.orientation, false);
                    this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                    canvas.clipRect(((float) this.clipLeft) / scaleY, ((float) this.clipTop) / scaleY, ((float) getWidth()) - (((float) this.clipRight) / scaleY), ((float) getHeight()) - (((float) this.clipBottom) / scaleY));
                    RectF rectF = this.roundRect;
                    i = this.radius;
                    canvas.drawRoundRect(rectF, (float) i, (float) i, this.roundPaint);
                } else {
                    i = this.orientation;
                    if (i == 90 || i == 270) {
                        this.drawRect.set((float) ((-getHeight()) / 2), (float) ((-getWidth()) / 2), (float) (getHeight() / 2), (float) (getWidth() / 2));
                        this.matrix.setRectToRect(this.bitmapRect, this.drawRect, ScaleToFit.FILL);
                        this.matrix.postRotate((float) this.orientation, 0.0f, 0.0f);
                        this.matrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
                    } else if (i == 180) {
                        this.drawRect.set((float) ((-getWidth()) / 2), (float) ((-getHeight()) / 2), (float) (getWidth() / 2), (float) (getHeight() / 2));
                        this.matrix.setRectToRect(this.bitmapRect, this.drawRect, ScaleToFit.FILL);
                        this.matrix.postRotate((float) this.orientation, 0.0f, 0.0f);
                        this.matrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
                    } else {
                        this.drawRect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                        this.matrix.setRectToRect(this.bitmapRect, this.drawRect, ScaleToFit.FILL);
                    }
                    canvas.clipRect(((float) this.clipLeft) / scaleY, ((float) this.clipTop) / scaleY, ((float) getWidth()) - (((float) this.clipRight) / scaleY), ((float) getHeight()) - (((float) this.clipBottom) / scaleY));
                    try {
                        canvas.drawBitmap(this.bmp.bitmap, this.matrix, this.paint);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                canvas.restore();
            }
        }
    }

    public void setClipBottom(int i) {
        this.clipBottom = i;
        invalidate();
    }

    public void setClipHorizontal(int i) {
        this.clipRight = i;
        this.clipLeft = i;
        invalidate();
    }

    public void setClipLeft(int i) {
        this.clipLeft = i;
        invalidate();
    }

    public void setClipRight(int i) {
        this.clipRight = i;
        invalidate();
    }

    public void setClipTop(int i) {
        this.clipTop = i;
        invalidate();
    }

    public void setClipVertical(int i) {
        this.clipBottom = i;
        this.clipTop = i;
        invalidate();
    }

    public void setImageY(int i) {
        this.imageY = i;
    }

    public void setImageX(int i) {
        this.imageX = i;
    }

    public void setOrientation(int i) {
        this.orientation = i;
    }

    public void setImageBitmap(BitmapHolder bitmapHolder) {
        BitmapHolder bitmapHolder2 = this.bmp;
        if (bitmapHolder2 != null) {
            bitmapHolder2.release();
            this.bitmapShader = null;
        }
        this.bmp = bitmapHolder;
        if (!(bitmapHolder == null || bitmapHolder.bitmap == null)) {
            this.bitmapRect.set(0.0f, 0.0f, (float) bitmapHolder.getWidth(), (float) bitmapHolder.getHeight());
            if (this.needRadius) {
                Bitmap bitmap = this.bmp.bitmap;
                TileMode tileMode = TileMode.CLAMP;
                this.bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
                this.roundPaint.setShader(this.bitmapShader);
            }
        }
        invalidate();
    }

    public Bitmap getBitmap() {
        BitmapHolder bitmapHolder = this.bmp;
        return bitmapHolder != null ? bitmapHolder.bitmap : null;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setNeedRadius(boolean z) {
        this.needRadius = z;
    }

    public void setRadius(int i) {
        this.radius = i;
    }
}
