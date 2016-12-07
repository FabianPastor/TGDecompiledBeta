package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.ActionBar.Theme;

public class PhotoCropView extends FrameLayout {
    private RectF animationEndValues;
    private Runnable animationRunnable;
    private RectF animationStartValues;
    private float bitmapGlobalScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    private float bitmapGlobalX = 0.0f;
    private float bitmapGlobalY = 0.0f;
    private int bitmapHeight = 1;
    private Bitmap bitmapToEdit;
    private int bitmapWidth = 1;
    private int bitmapX;
    private int bitmapY;
    private Paint circlePaint;
    private PhotoCropViewDelegate delegate;
    private int draggingState = 0;
    private boolean freeformCrop = true;
    private Paint halfPaint;
    private float oldX = 0.0f;
    private float oldY = 0.0f;
    private int orientation;
    private Paint rectPaint = new Paint();
    private float rectSizeX = 600.0f;
    private float rectSizeY = 600.0f;
    private float rectX = -1.0f;
    private float rectY = -1.0f;
    private Paint shadowPaint;

    public interface PhotoCropViewDelegate {
        Bitmap getBitmap();

        void needMoveImageTo(float f, float f2, float f3, boolean z);
    }

    public PhotoCropView(Context context) {
        super(context);
        this.rectPaint.setColor(-NUM);
        this.rectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.rectPaint.setStyle(Style.STROKE);
        this.circlePaint = new Paint();
        this.circlePaint.setColor(-1);
        this.halfPaint = new Paint();
        this.halfPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.shadowPaint = new Paint();
        this.shadowPaint.setColor(436207616);
        setWillNotDraw(false);
    }

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform) {
        this.bitmapToEdit = bitmap;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.draggingState = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.bitmapWidth = 1;
        this.bitmapHeight = 1;
        this.rectX = -1.0f;
        this.rectY = -1.0f;
        this.freeformCrop = freeform;
        this.orientation = rotation;
        requestLayout();
    }

    public void setOrientation(int rotation) {
        this.orientation = rotation;
        this.rectX = -1.0f;
        this.rectY = -1.0f;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.delegate.needMoveImageTo(0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, false);
        requestLayout();
    }

    public boolean onTouch(MotionEvent motionEvent) {
        if (motionEvent == null) {
            this.draggingState = 0;
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int cornerSide = AndroidUtilities.dp(20.0f);
        if (motionEvent.getAction() == 0) {
            if (this.rectX - ((float) cornerSide) < x && this.rectX + ((float) cornerSide) > x && this.rectY - ((float) cornerSide) < y && this.rectY + ((float) cornerSide) > y) {
                this.draggingState = 1;
            } else if ((this.rectX - ((float) cornerSide)) + this.rectSizeX < x && (this.rectX + ((float) cornerSide)) + this.rectSizeX > x && this.rectY - ((float) cornerSide) < y && this.rectY + ((float) cornerSide) > y) {
                this.draggingState = 2;
            } else if (this.rectX - ((float) cornerSide) < x && this.rectX + ((float) cornerSide) > x && (this.rectY - ((float) cornerSide)) + this.rectSizeY < y && (this.rectY + ((float) cornerSide)) + this.rectSizeY > y) {
                this.draggingState = 3;
            } else if ((this.rectX - ((float) cornerSide)) + this.rectSizeX < x && (this.rectX + ((float) cornerSide)) + this.rectSizeX > x && (this.rectY - ((float) cornerSide)) + this.rectSizeY < y && (this.rectY + ((float) cornerSide)) + this.rectSizeY > y) {
                this.draggingState = 4;
            } else if (!this.freeformCrop) {
                this.draggingState = 0;
            } else if (this.rectX + ((float) cornerSide) < x && (this.rectX - ((float) cornerSide)) + this.rectSizeX > x && this.rectY - ((float) cornerSide) < y && this.rectY + ((float) cornerSide) > y) {
                this.draggingState = 5;
            } else if (this.rectY + ((float) cornerSide) < y && (this.rectY - ((float) cornerSide)) + this.rectSizeY > y && (this.rectX - ((float) cornerSide)) + this.rectSizeX < x && (this.rectX + ((float) cornerSide)) + this.rectSizeX > x) {
                this.draggingState = 6;
            } else if (this.rectY + ((float) cornerSide) < y && (this.rectY - ((float) cornerSide)) + this.rectSizeY > y && this.rectX - ((float) cornerSide) < x && this.rectX + ((float) cornerSide) > x) {
                this.draggingState = 7;
            } else if (this.rectX + ((float) cornerSide) < x && (this.rectX - ((float) cornerSide)) + this.rectSizeX > x && (this.rectY - ((float) cornerSide)) + this.rectSizeY < y && (this.rectY + ((float) cornerSide)) + this.rectSizeY > y) {
                this.draggingState = 8;
            }
            if (this.draggingState != 0) {
                cancelAnimationRunnable();
                requestDisallowInterceptTouchEvent(true);
            }
            this.oldX = x;
            this.oldY = y;
        } else if (motionEvent.getAction() == 1) {
            if (this.draggingState != 0) {
                this.draggingState = 0;
                startAnimationRunnable();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.draggingState != 0) {
            float diffX = x - this.oldX;
            float diffY = y - this.oldY;
            float bitmapScaledWidth = ((float) this.bitmapWidth) * this.bitmapGlobalScale;
            float bitmapScaledHeight = ((float) this.bitmapHeight) * this.bitmapGlobalScale;
            float bitmapStartX = ((((float) getWidth()) - bitmapScaledWidth) / 2.0f) + this.bitmapGlobalX;
            float bitmapStartY = (((((float) getHeight()) - bitmapScaledHeight) + ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f) + this.bitmapGlobalY;
            float bitmapEndX = bitmapStartX + bitmapScaledWidth;
            float bitmapEndY = bitmapStartY + bitmapScaledHeight;
            float minSide = AndroidUtilities.getPixelsInCM(0.9f, true);
            if (this.draggingState == 1 || this.draggingState == 5) {
                if (this.draggingState != 5) {
                    if (this.rectSizeX - diffX < minSide) {
                        diffX = this.rectSizeX - minSide;
                    }
                    if (this.rectX + diffX < ((float) this.bitmapX)) {
                        diffX = ((float) this.bitmapX) - this.rectX;
                    }
                    if (this.rectX + diffX < bitmapStartX) {
                        this.bitmapGlobalX -= (bitmapStartX - this.rectX) - diffX;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                }
                if (this.freeformCrop) {
                    if (this.rectSizeY - diffY < minSide) {
                        diffY = this.rectSizeY - minSide;
                    }
                    if (this.rectY + diffY < ((float) this.bitmapY)) {
                        diffY = ((float) this.bitmapY) - this.rectY;
                    }
                    if (this.rectY + diffY < bitmapStartY) {
                        this.bitmapGlobalY -= (bitmapStartY - this.rectY) - diffY;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    if (this.draggingState != 5) {
                        this.rectX += diffX;
                        this.rectSizeX -= diffX;
                    }
                    this.rectY += diffY;
                    this.rectSizeY -= diffY;
                } else {
                    if (this.rectY + diffX < ((float) this.bitmapY)) {
                        diffX = ((float) this.bitmapY) - this.rectY;
                    }
                    if (this.rectY + diffX < bitmapStartY) {
                        this.bitmapGlobalY -= (bitmapStartY - this.rectY) - diffX;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectX += diffX;
                    this.rectY += diffX;
                    this.rectSizeX -= diffX;
                    this.rectSizeY -= diffX;
                }
            } else if (this.draggingState == 2 || this.draggingState == 6) {
                if (this.rectSizeX + diffX < minSide) {
                    diffX = -(this.rectSizeX - minSide);
                }
                if ((this.rectX + this.rectSizeX) + diffX > ((float) (this.bitmapX + this.bitmapWidth))) {
                    diffX = (((float) (this.bitmapX + this.bitmapWidth)) - this.rectX) - this.rectSizeX;
                }
                if ((this.rectX + this.rectSizeX) + diffX > bitmapEndX) {
                    this.bitmapGlobalX -= ((bitmapEndX - this.rectX) - this.rectSizeX) - diffX;
                    this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                }
                if (this.freeformCrop) {
                    if (this.draggingState != 6) {
                        if (this.rectSizeY - diffY < minSide) {
                            diffY = this.rectSizeY - minSide;
                        }
                        if (this.rectY + diffY < ((float) this.bitmapY)) {
                            diffY = ((float) this.bitmapY) - this.rectY;
                        }
                        if (this.rectY + diffY < bitmapStartY) {
                            this.bitmapGlobalY -= (bitmapStartY - this.rectY) - diffY;
                            this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                        }
                        this.rectY += diffY;
                        this.rectSizeY -= diffY;
                    }
                    this.rectSizeX += diffX;
                } else {
                    if (this.rectY - diffX < ((float) this.bitmapY)) {
                        diffX = this.rectY - ((float) this.bitmapY);
                    }
                    if (this.rectY - diffX < bitmapStartY) {
                        this.bitmapGlobalY -= (bitmapStartY - this.rectY) + diffX;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectY -= diffX;
                    this.rectSizeX += diffX;
                    this.rectSizeY += diffX;
                }
            } else if (this.draggingState == 3 || this.draggingState == 7) {
                if (this.rectSizeX - diffX < minSide) {
                    diffX = this.rectSizeX - minSide;
                }
                if (this.rectX + diffX < ((float) this.bitmapX)) {
                    diffX = ((float) this.bitmapX) - this.rectX;
                }
                if (this.rectX + diffX < bitmapStartX) {
                    this.bitmapGlobalX -= (bitmapStartX - this.rectX) - diffX;
                    this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                }
                if (this.freeformCrop) {
                    if (this.draggingState != 7) {
                        if ((this.rectY + this.rectSizeY) + diffY > ((float) (this.bitmapY + this.bitmapHeight))) {
                            diffY = (((float) (this.bitmapY + this.bitmapHeight)) - this.rectY) - this.rectSizeY;
                        }
                        if ((this.rectY + this.rectSizeY) + diffY > bitmapEndY) {
                            this.bitmapGlobalY -= ((bitmapEndY - this.rectY) - this.rectSizeY) - diffY;
                            this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                        }
                        this.rectSizeY += diffY;
                        if (this.rectSizeY < minSide) {
                            this.rectSizeY = minSide;
                        }
                    }
                    this.rectX += diffX;
                    this.rectSizeX -= diffX;
                } else {
                    if ((this.rectY + this.rectSizeX) - diffX > ((float) (this.bitmapY + this.bitmapHeight))) {
                        diffX = ((this.rectY + this.rectSizeX) - ((float) this.bitmapY)) - ((float) this.bitmapHeight);
                    }
                    if ((this.rectY + this.rectSizeX) - diffX > bitmapEndY) {
                        this.bitmapGlobalY -= ((bitmapEndY - this.rectY) - this.rectSizeX) + diffX;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectX += diffX;
                    this.rectSizeX -= diffX;
                    this.rectSizeY -= diffX;
                }
            } else if (this.draggingState == 4 || this.draggingState == 8) {
                if (this.draggingState != 8) {
                    if ((this.rectX + this.rectSizeX) + diffX > ((float) (this.bitmapX + this.bitmapWidth))) {
                        diffX = (((float) (this.bitmapX + this.bitmapWidth)) - this.rectX) - this.rectSizeX;
                    }
                    if ((this.rectX + this.rectSizeX) + diffX > bitmapEndX) {
                        this.bitmapGlobalX -= ((bitmapEndX - this.rectX) - this.rectSizeX) - diffX;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                }
                if (this.freeformCrop) {
                    if ((this.rectY + this.rectSizeY) + diffY > ((float) (this.bitmapY + this.bitmapHeight))) {
                        diffY = (((float) (this.bitmapY + this.bitmapHeight)) - this.rectY) - this.rectSizeY;
                    }
                    if ((this.rectY + this.rectSizeY) + diffY > bitmapEndY) {
                        this.bitmapGlobalY -= ((bitmapEndY - this.rectY) - this.rectSizeY) - diffY;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    if (this.draggingState != 8) {
                        this.rectSizeX += diffX;
                    }
                    this.rectSizeY += diffY;
                } else {
                    if ((this.rectY + this.rectSizeX) + diffX > ((float) (this.bitmapY + this.bitmapHeight))) {
                        diffX = (((float) (this.bitmapY + this.bitmapHeight)) - this.rectY) - this.rectSizeX;
                    }
                    if ((this.rectY + this.rectSizeX) + diffX > bitmapEndY) {
                        this.bitmapGlobalY -= ((bitmapEndY - this.rectY) - this.rectSizeX) - diffX;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectSizeX += diffX;
                    this.rectSizeY += diffX;
                }
                if (this.rectSizeX < minSide) {
                    this.rectSizeX = minSide;
                }
                if (this.rectSizeY < minSide) {
                    this.rectSizeY = minSide;
                }
            }
            this.oldX = x;
            this.oldY = y;
            invalidate();
        }
        if (this.draggingState != 0) {
            return true;
        }
        return false;
    }

    public float getRectX() {
        return this.rectX - ((float) AndroidUtilities.dp(14.0f));
    }

    public float getRectY() {
        return (this.rectY - ((float) AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getRectSizeX() {
        return this.rectSizeX;
    }

    public float getRectSizeY() {
        return this.rectSizeY;
    }

    public float getBitmapX() {
        return (float) (this.bitmapX - AndroidUtilities.dp(14.0f));
    }

    public float getBitmapY() {
        return ((float) (this.bitmapY - AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getLimitX() {
        return this.rectX - Math.max(0.0f, (float) Math.ceil((double) ((((float) getWidth()) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)));
    }

    public float getLimitY() {
        return this.rectY - Math.max(0.0f, (float) Math.ceil((double) (((((float) getHeight()) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) + ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f)));
    }

    public float getLimitWidth() {
        return ((((float) (getWidth() - AndroidUtilities.dp(14.0f))) - this.rectX) - ((float) ((int) Math.max(0.0d, Math.ceil((double) ((((float) (getWidth() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)))))) - this.rectSizeX;
    }

    public float getLimitHeight() {
        float additionalY = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        return (((((float) (getHeight() - AndroidUtilities.dp(14.0f))) - additionalY) - this.rectY) - ((float) ((int) Math.max(0.0d, Math.ceil((double) (((((float) (getHeight() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) - additionalY) / 2.0f)))))) - this.rectSizeY;
    }

    private Bitmap createBitmap(int x, int y, int w, int h) {
        Bitmap newBimap = this.delegate.getBitmap();
        if (newBimap != null) {
            this.bitmapToEdit = newBimap;
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(6);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) ((-this.bitmapToEdit.getWidth()) / 2), (float) ((-this.bitmapToEdit.getHeight()) / 2));
        matrix.postRotate((float) this.orientation);
        if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
            matrix.postTranslate((float) ((this.bitmapToEdit.getHeight() / 2) - x), (float) ((this.bitmapToEdit.getWidth() / 2) - y));
        } else {
            matrix.postTranslate((float) ((this.bitmapToEdit.getWidth() / 2) - x), (float) ((this.bitmapToEdit.getHeight() / 2) - y));
        }
        canvas.drawBitmap(this.bitmapToEdit, matrix, paint);
        try {
            canvas.setBitmap(null);
        } catch (Exception e) {
        }
        return bitmap;
    }

    public Bitmap getBitmap() {
        int width;
        int height;
        Bitmap newBimap = this.delegate.getBitmap();
        if (newBimap != null) {
            this.bitmapToEdit = newBimap;
        }
        float bitmapScaledWidth = ((float) this.bitmapWidth) * this.bitmapGlobalScale;
        float bitmapScaledHeight = ((float) this.bitmapHeight) * this.bitmapGlobalScale;
        float percX = (this.rectX - (((((float) getWidth()) - bitmapScaledWidth) / 2.0f) + this.bitmapGlobalX)) / bitmapScaledWidth;
        float percY = (this.rectY - ((((((float) getHeight()) - bitmapScaledHeight) + ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f) + this.bitmapGlobalY)) / bitmapScaledHeight;
        float percSizeX = this.rectSizeX / bitmapScaledWidth;
        float percSizeY = this.rectSizeY / bitmapScaledHeight;
        if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
            width = this.bitmapToEdit.getHeight();
            height = this.bitmapToEdit.getWidth();
        } else {
            width = this.bitmapToEdit.getWidth();
            height = this.bitmapToEdit.getHeight();
        }
        int x = (int) (((float) width) * percX);
        int y = (int) (((float) height) * percY);
        int sizeX = (int) (((float) width) * percSizeX);
        int sizeY = (int) (((float) height) * percSizeY);
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x + sizeX > width) {
            sizeX = width - x;
        }
        if (y + sizeY > height) {
            sizeY = height - y;
        }
        try {
            return createBitmap(x, y, sizeX, sizeY);
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
            return null;
        }
    }

    protected void onDraw(Canvas canvas) {
        int a;
        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), this.rectY, this.halfPaint);
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, this.rectY, this.rectX, this.rectSizeY + this.rectY, this.halfPaint);
        canvas.drawRect(this.rectX + this.rectSizeX, this.rectY, (float) getWidth(), this.rectY + this.rectSizeY, this.halfPaint);
        canvas2 = canvas;
        canvas2.drawRect(0.0f, this.rectSizeY + this.rectY, (float) getWidth(), (float) getHeight(), this.halfPaint);
        int side = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (side * 2)), this.rectY - ((float) (side * 2)), ((float) AndroidUtilities.dp(20.0f)) + (this.rectX - ((float) (side * 2))), this.rectY, this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (side * 2)), this.rectY - ((float) (side * 2)), this.rectX, ((float) AndroidUtilities.dp(20.0f)) + (this.rectY - ((float) (side * 2))), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(((this.rectX + this.rectSizeX) + ((float) (side * 2))) - ((float) AndroidUtilities.dp(20.0f)), this.rectY - ((float) (side * 2)), ((float) (side * 2)) + (this.rectX + this.rectSizeX), this.rectY, this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectSizeX + this.rectX, this.rectY - ((float) (side * 2)), ((float) (side * 2)) + (this.rectX + this.rectSizeX), ((float) AndroidUtilities.dp(20.0f)) + (this.rectY - ((float) (side * 2))), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (side * 2)), ((this.rectY + this.rectSizeY) + ((float) (side * 2))) - ((float) AndroidUtilities.dp(20.0f)), this.rectX, ((float) (side * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (side * 2)), this.rectSizeY + this.rectY, ((float) AndroidUtilities.dp(20.0f)) + (this.rectX - ((float) (side * 2))), ((float) (side * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(((this.rectX + this.rectSizeX) + ((float) (side * 2))) - ((float) AndroidUtilities.dp(20.0f)), this.rectSizeY + this.rectY, ((float) (side * 2)) + (this.rectX + this.rectSizeX), ((float) (side * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectSizeX + this.rectX, ((this.rectY + this.rectSizeY) + ((float) (side * 2))) - ((float) AndroidUtilities.dp(20.0f)), ((float) (side * 2)) + (this.rectX + this.rectSizeX), ((float) (side * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        for (a = 1; a < 3; a++) {
            canvas2 = canvas;
            canvas2.drawRect((this.rectX + ((this.rectSizeX / 3.0f) * ((float) a))) - ((float) side), this.rectY, ((this.rectSizeX / 3.0f) * ((float) a)) + (this.rectX + ((float) (side * 2))), this.rectSizeY + this.rectY, this.shadowPaint);
            canvas2 = canvas;
            canvas2.drawRect(this.rectX, (this.rectY + ((this.rectSizeY / 3.0f) * ((float) a))) - ((float) side), this.rectSizeX + this.rectX, ((float) (side * 2)) + (this.rectY + ((this.rectSizeY / 3.0f) * ((float) a))), this.shadowPaint);
        }
        for (a = 1; a < 3; a++) {
            canvas2 = canvas;
            canvas2.drawRect(((this.rectSizeX / 3.0f) * ((float) a)) + this.rectX, this.rectY, ((this.rectSizeX / 3.0f) * ((float) a)) + (this.rectX + ((float) side)), this.rectSizeY + this.rectY, this.circlePaint);
            canvas2 = canvas;
            canvas2.drawRect(this.rectX, ((this.rectSizeY / 3.0f) * ((float) a)) + this.rectY, this.rectSizeX + this.rectX, ((float) side) + (this.rectY + ((this.rectSizeY / 3.0f) * ((float) a))), this.circlePaint);
        }
        canvas2 = canvas;
        canvas2.drawRect(this.rectX, this.rectY, this.rectSizeX + this.rectX, this.rectSizeY + this.rectY, this.rectPaint);
    }

    public void setBitmapParams(float scale, float x, float y) {
        this.bitmapGlobalScale = scale;
        this.bitmapGlobalX = x;
        this.bitmapGlobalY = y;
    }

    public void startAnimationRunnable() {
        if (this.animationRunnable == null) {
            this.animationRunnable = new Runnable() {
                public void run() {
                    if (PhotoCropView.this.animationRunnable == this) {
                        PhotoCropView.this.animationRunnable = null;
                        PhotoCropView.this.moveToFill(true);
                    }
                }
            };
            AndroidUtilities.runOnUIThread(this.animationRunnable, 1500);
        }
    }

    public void cancelAnimationRunnable() {
        if (this.animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animationRunnable = null;
            this.animationStartValues = null;
            this.animationEndValues = null;
        }
    }

    public void setAnimationProgress(float animationProgress) {
        if (this.animationStartValues != null) {
            if (animationProgress == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.rectX = this.animationEndValues.left;
                this.rectY = this.animationEndValues.top;
                this.rectSizeX = this.animationEndValues.right;
                this.rectSizeY = this.animationEndValues.bottom;
                this.animationStartValues = null;
                this.animationEndValues = null;
            } else {
                this.rectX = this.animationStartValues.left + ((this.animationEndValues.left - this.animationStartValues.left) * animationProgress);
                this.rectY = this.animationStartValues.top + ((this.animationEndValues.top - this.animationStartValues.top) * animationProgress);
                this.rectSizeX = this.animationStartValues.right + ((this.animationEndValues.right - this.animationStartValues.right) * animationProgress);
                this.rectSizeY = this.animationStartValues.bottom + ((this.animationEndValues.bottom - this.animationStartValues.bottom) * animationProgress);
            }
            invalidate();
        }
    }

    public void moveToFill(boolean animated) {
        float scaleTo;
        float scaleToX = ((float) this.bitmapWidth) / this.rectSizeX;
        float scaleToY = ((float) this.bitmapHeight) / this.rectSizeY;
        if (scaleToX > scaleToY) {
            scaleTo = scaleToY;
        } else {
            scaleTo = scaleToX;
        }
        if (scaleTo > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.bitmapGlobalScale * scaleTo > 3.0f) {
            scaleTo = 3.0f / this.bitmapGlobalScale;
        } else if (scaleTo < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.bitmapGlobalScale * scaleTo < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            scaleTo = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / this.bitmapGlobalScale;
        }
        float newSizeX = this.rectSizeX * scaleTo;
        float newSizeY = this.rectSizeY * scaleTo;
        float additionalY = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float newX = (((float) getWidth()) - newSizeX) / 2.0f;
        float newY = ((((float) getHeight()) - newSizeY) + additionalY) / 2.0f;
        this.animationStartValues = new RectF(this.rectX, this.rectY, this.rectSizeX, this.rectSizeY);
        this.animationEndValues = new RectF(newX, newY, newSizeX, newSizeY);
        this.delegate.needMoveImageTo(((((float) (getWidth() / 2)) * (scaleTo - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + newX) + ((this.bitmapGlobalX - this.rectX) * scaleTo), ((((((float) getHeight()) + additionalY) / 2.0f) * (scaleTo - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + newY) + ((this.bitmapGlobalY - this.rectY) * scaleTo), this.bitmapGlobalScale * scaleTo, animated);
    }

    public void setDelegate(PhotoCropViewDelegate delegate) {
        this.delegate = delegate;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Bitmap newBimap = this.delegate.getBitmap();
        if (newBimap != null) {
            this.bitmapToEdit = newBimap;
        }
        if (this.bitmapToEdit != null) {
            float bitmapW;
            float bitmapH;
            int viewWidth = getWidth() - AndroidUtilities.dp(28.0f);
            int viewHeight = (getHeight() - AndroidUtilities.dp(28.0f)) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                bitmapW = (float) this.bitmapToEdit.getHeight();
                bitmapH = (float) this.bitmapToEdit.getWidth();
            } else {
                bitmapW = (float) this.bitmapToEdit.getWidth();
                bitmapH = (float) this.bitmapToEdit.getHeight();
            }
            float scaleX = ((float) viewWidth) / bitmapW;
            float scaleY = ((float) viewHeight) / bitmapH;
            if (scaleX > scaleY) {
                bitmapH = (float) viewHeight;
                bitmapW = (float) ((int) Math.ceil((double) (bitmapW * scaleY)));
            } else {
                bitmapW = (float) viewWidth;
                bitmapH = (float) ((int) Math.ceil((double) (bitmapH * scaleX)));
            }
            float percX = (this.rectX - ((float) this.bitmapX)) / ((float) this.bitmapWidth);
            float percY = (this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight);
            float percSizeX = this.rectSizeX / ((float) this.bitmapWidth);
            float percSizeY = this.rectSizeY / ((float) this.bitmapHeight);
            this.bitmapWidth = (int) bitmapW;
            this.bitmapHeight = (int) bitmapH;
            this.bitmapX = (int) Math.ceil((double) (((viewWidth - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f)));
            this.bitmapY = (int) Math.ceil((double) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + (AndroidUtilities.dp(14.0f) + ((viewHeight - this.bitmapHeight) / 2))));
            if (this.rectX != -1.0f || this.rectY != -1.0f) {
                this.rectX = (((float) this.bitmapWidth) * percX) + ((float) this.bitmapX);
                this.rectY = (((float) this.bitmapHeight) * percY) + ((float) this.bitmapY);
                this.rectSizeX = ((float) this.bitmapWidth) * percSizeX;
                this.rectSizeY = ((float) this.bitmapHeight) * percSizeY;
            } else if (this.freeformCrop) {
                this.rectY = (float) this.bitmapY;
                this.rectX = (float) this.bitmapX;
                this.rectSizeX = (float) this.bitmapWidth;
                this.rectSizeY = (float) this.bitmapHeight;
            } else if (this.bitmapWidth > this.bitmapHeight) {
                this.rectY = (float) this.bitmapY;
                this.rectX = (float) (((viewWidth - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f));
                this.rectSizeX = (float) this.bitmapHeight;
                this.rectSizeY = (float) this.bitmapHeight;
            } else {
                this.rectX = (float) this.bitmapX;
                this.rectY = (float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + (AndroidUtilities.dp(14.0f) + ((viewHeight - this.bitmapWidth) / 2)));
                this.rectSizeX = (float) this.bitmapWidth;
                this.rectSizeY = (float) this.bitmapWidth;
            }
        }
    }
}
