package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;

public class ClippingImageView extends View {
    private static float[] radii = new float[8];
    private float additionalTranslationY;
    private float animationProgress;
    private float[][] animationValues;
    private RectF bitmapRect;
    private BitmapShader bitmapShader;
    private ImageReceiver.BitmapHolder bmp;
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
    private Paint paint;
    private int[] radius = new int[4];
    private Paint roundPaint;
    private Path roundPath = new Path();
    private RectF roundRect;
    private Matrix shaderMatrix;

    public ClippingImageView(Context context) {
        super(context);
        Paint paint2 = new Paint(2);
        this.paint = paint2;
        paint2.setFilterBitmap(true);
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

    public void setAdditionalTranslationY(float f) {
        this.additionalTranslationY = f;
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f + this.additionalTranslationY);
    }

    public float getTranslationY() {
        return super.getTranslationY() - this.additionalTranslationY;
    }

    @Keep
    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float f) {
        this.animationProgress = f;
        float[][] fArr = this.animationValues;
        setScaleX(fArr[0][0] + ((fArr[1][0] - fArr[0][0]) * f));
        float[][] fArr2 = this.animationValues;
        setScaleY(fArr2[0][1] + ((fArr2[1][1] - fArr2[0][1]) * this.animationProgress));
        float[][] fArr3 = this.animationValues;
        setTranslationX(fArr3[0][2] + ((fArr3[1][2] - fArr3[0][2]) * this.animationProgress));
        float[][] fArr4 = this.animationValues;
        setTranslationY(fArr4[0][3] + ((fArr4[1][3] - fArr4[0][3]) * this.animationProgress));
        float[][] fArr5 = this.animationValues;
        setClipHorizontal((int) (fArr5[0][4] + ((fArr5[1][4] - fArr5[0][4]) * this.animationProgress)));
        float[][] fArr6 = this.animationValues;
        setClipTop((int) (fArr6[0][5] + ((fArr6[1][5] - fArr6[0][5]) * this.animationProgress)));
        float[][] fArr7 = this.animationValues;
        setClipBottom((int) (fArr7[0][6] + ((fArr7[1][6] - fArr7[0][6]) * this.animationProgress)));
        int i = 0;
        while (true) {
            int[] iArr = this.radius;
            if (i >= iArr.length) {
                break;
            }
            float[][] fArr8 = this.animationValues;
            int i2 = i + 7;
            iArr[i] = (int) (fArr8[0][i2] + ((fArr8[1][i2] - fArr8[0][i2]) * this.animationProgress));
            setRadius(iArr);
            i++;
        }
        float[][] fArr9 = this.animationValues;
        if (fArr9[0].length > 11) {
            setImageY((int) (fArr9[0][11] + ((fArr9[1][11] - fArr9[0][11]) * this.animationProgress)));
            float[][] fArr10 = this.animationValues;
            setImageX((int) (fArr10[0][12] + ((fArr10[1][12] - fArr10[0][12]) * this.animationProgress)));
        }
        invalidate();
    }

    public void getClippedVisibleRect(RectF rectF) {
        rectF.left = getTranslationX();
        rectF.top = getTranslationY();
        rectF.right = rectF.left + (((float) getMeasuredWidth()) * getScaleX());
        float measuredHeight = rectF.top + (((float) getMeasuredHeight()) * getScaleY());
        rectF.bottom = measuredHeight;
        rectF.left += (float) this.clipLeft;
        rectF.top += (float) this.clipTop;
        rectF.right -= (float) this.clipRight;
        rectF.bottom = measuredHeight - ((float) this.clipBottom);
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

    public int[] getRadius() {
        return this.radius;
    }

    public void onDraw(Canvas canvas) {
        ImageReceiver.BitmapHolder bitmapHolder;
        if (getVisibility() == 0 && (bitmapHolder = this.bmp) != null && !bitmapHolder.isRecycled()) {
            float scaleY = getScaleY();
            canvas.save();
            if (this.needRadius) {
                this.shaderMatrix.reset();
                this.roundRect.set(((float) this.imageX) / scaleY, ((float) this.imageY) / scaleY, ((float) getWidth()) - (((float) this.imageX) / scaleY), ((float) getHeight()) - (((float) this.imageY) / scaleY));
                this.bitmapRect.set(0.0f, 0.0f, (float) this.bmp.getWidth(), (float) this.bmp.getHeight());
                int i = 0;
                AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.orientation, false);
                this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                canvas.clipRect(((float) this.clipLeft) / scaleY, ((float) this.clipTop) / scaleY, ((float) getWidth()) - (((float) this.clipRight) / scaleY), ((float) getHeight()) - (((float) this.clipBottom) / scaleY));
                while (true) {
                    int[] iArr = this.radius;
                    if (i >= iArr.length) {
                        break;
                    }
                    float[] fArr = radii;
                    int i2 = i * 2;
                    fArr[i2] = (float) iArr[i];
                    fArr[i2 + 1] = (float) iArr[i];
                    i++;
                }
                this.roundPath.reset();
                this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                this.roundPath.close();
                canvas.drawPath(this.roundPath, this.roundPaint);
            } else {
                int i3 = this.orientation;
                if (i3 == 90 || i3 == 270) {
                    this.drawRect.set((float) ((-getHeight()) / 2), (float) ((-getWidth()) / 2), (float) (getHeight() / 2), (float) (getWidth() / 2));
                    this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
                    this.matrix.postRotate((float) this.orientation, 0.0f, 0.0f);
                    this.matrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
                } else if (i3 == 180) {
                    this.drawRect.set((float) ((-getWidth()) / 2), (float) ((-getHeight()) / 2), (float) (getWidth() / 2), (float) (getHeight() / 2));
                    this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
                    this.matrix.postRotate((float) this.orientation, 0.0f, 0.0f);
                    this.matrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
                } else {
                    this.drawRect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                    this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
                }
                canvas.clipRect(((float) this.clipLeft) / scaleY, ((float) this.clipTop) / scaleY, ((float) getWidth()) - (((float) this.clipRight) / scaleY), ((float) getHeight()) - (((float) this.clipBottom) / scaleY));
                try {
                    canvas.drawBitmap(this.bmp.bitmap, this.matrix, this.paint);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            canvas.restore();
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

    public void setImageBitmap(ImageReceiver.BitmapHolder bitmapHolder) {
        ImageReceiver.BitmapHolder bitmapHolder2 = this.bmp;
        if (bitmapHolder2 != null) {
            bitmapHolder2.release();
            this.bitmapShader = null;
        }
        this.bmp = bitmapHolder;
        if (!(bitmapHolder == null || bitmapHolder.bitmap == null)) {
            this.bitmapRect.set(0.0f, 0.0f, (float) bitmapHolder.getWidth(), (float) bitmapHolder.getHeight());
            Bitmap bitmap = this.bmp.bitmap;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader2 = new BitmapShader(bitmap, tileMode, tileMode);
            this.bitmapShader = bitmapShader2;
            this.roundPaint.setShader(bitmapShader2);
        }
        invalidate();
    }

    public Bitmap getBitmap() {
        ImageReceiver.BitmapHolder bitmapHolder = this.bmp;
        if (bitmapHolder != null) {
            return bitmapHolder.bitmap;
        }
        return null;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setRadius(int[] iArr) {
        if (iArr == null) {
            this.needRadius = false;
            int i = 0;
            while (true) {
                int[] iArr2 = this.radius;
                if (i < iArr2.length) {
                    iArr2[i] = 0;
                    i++;
                } else {
                    return;
                }
            }
        } else {
            System.arraycopy(iArr, 0, this.radius, 0, iArr.length);
            this.needRadius = false;
            for (int i2 : iArr) {
                if (i2 != 0) {
                    this.needRadius = true;
                    return;
                }
            }
        }
    }
}
