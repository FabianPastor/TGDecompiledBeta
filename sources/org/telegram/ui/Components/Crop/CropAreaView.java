package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.BubbleActivity;

public class CropAreaView extends View {
    private Control activeControl;
    private RectF actualRect = new RectF();
    /* access modifiers changed from: private */
    public Animator animator;
    private Paint bitmapPaint;
    private RectF bottomEdge = new RectF();
    private RectF bottomLeftCorner = new RectF();
    private float bottomPadding;
    private RectF bottomRightCorner = new RectF();
    private Bitmap circleBitmap;
    private Paint dimPaint;
    private boolean dimVisibile;
    private Paint eraserPaint;
    private float frameAlpha = 1.0f;
    private Paint framePaint;
    private boolean frameVisible;
    private boolean freeform = true;
    /* access modifiers changed from: private */
    public Animator gridAnimator;
    private float gridProgress;
    private GridType gridType;
    private Paint handlePaint;
    private boolean inBubbleMode;
    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private boolean isDragging;
    private long lastUpdateTime;
    private RectF leftEdge = new RectF();
    private Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private float minWidth;
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private RectF rightEdge = new RectF();
    private Paint shadowPaint;
    private float sidePadding;
    private RectF targetRect = new RectF();
    private RectF tempRect = new RectF();
    private RectF topEdge = new RectF();
    private RectF topLeftCorner = new RectF();
    private RectF topRightCorner = new RectF();

    interface AreaViewListener {
        void onAreaChange();

        void onAreaChangeBegan();

        void onAreaChangeEnded();
    }

    private enum Control {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    enum GridType {
        NONE,
        MINOR,
        MAJOR
    }

    public CropAreaView(Context context) {
        super(context);
        this.inBubbleMode = context instanceof BubbleActivity;
        this.frameVisible = true;
        this.dimVisibile = true;
        this.sidePadding = (float) AndroidUtilities.dp(16.0f);
        this.minWidth = (float) AndroidUtilities.dp(32.0f);
        this.gridType = GridType.NONE;
        Paint paint = new Paint();
        this.dimPaint = paint;
        paint.setColor(NUM);
        Paint paint2 = new Paint();
        this.shadowPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.shadowPaint.setColor(NUM);
        this.shadowPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        Paint paint4 = new Paint();
        this.handlePaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        this.handlePaint.setColor(-1);
        Paint paint5 = new Paint();
        this.framePaint = paint5;
        paint5.setStyle(Paint.Style.FILL);
        this.framePaint.setColor(-NUM);
        Paint paint6 = new Paint(1);
        this.eraserPaint = paint6;
        paint6.setColor(0);
        this.eraserPaint.setStyle(Paint.Style.FILL);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint7 = new Paint(2);
        this.bitmapPaint = paint7;
        paint7.setColor(-1);
    }

    public void setIsVideo(boolean value) {
        this.minWidth = (float) AndroidUtilities.dp(value ? 64.0f : 32.0f);
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDimVisibility(boolean visible) {
        this.dimVisibile = visible;
    }

    public void setFrameVisibility(boolean visible, boolean animated) {
        this.frameVisible = visible;
        float f = 1.0f;
        if (visible) {
            if (animated) {
                f = 0.0f;
            }
            this.frameAlpha = f;
            this.lastUpdateTime = SystemClock.elapsedRealtime();
            invalidate();
            return;
        }
        this.frameAlpha = 1.0f;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
    }

    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    public void setListener(AreaViewListener l) {
        this.listener = l;
    }

    public void setBitmap(int w, int h, boolean sideward, boolean fform) {
        float aspectRatio;
        this.freeform = fform;
        if (sideward) {
            aspectRatio = ((float) h) / ((float) w);
        } else {
            aspectRatio = ((float) w) / ((float) h);
        }
        if (!fform) {
            aspectRatio = 1.0f;
            this.lockAspectRatio = 1.0f;
        }
        setActualRect(aspectRatio);
    }

    public void setFreeform(boolean fform) {
        this.freeform = fform;
    }

    public void setActualRect(float aspectRatio) {
        calculateRect(this.actualRect, aspectRatio);
        updateTouchAreas();
        invalidate();
    }

    public void setActualRect(RectF rect) {
        this.actualRect.set(rect);
        updateTouchAreas();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int width;
        int lineThickness;
        int height;
        int handleSize;
        int width2;
        int lineThickness2;
        int height2;
        int handleSize2;
        if (this.freeform) {
            int lineThickness3 = AndroidUtilities.dp(2.0f);
            int handleSize3 = AndroidUtilities.dp(16.0f);
            int handleThickness = AndroidUtilities.dp(3.0f);
            int originX = ((int) this.actualRect.left) - lineThickness3;
            int originY = ((int) this.actualRect.top) - lineThickness3;
            int width3 = ((int) (this.actualRect.right - this.actualRect.left)) + (lineThickness3 * 2);
            int height3 = ((int) (this.actualRect.bottom - this.actualRect.top)) + (lineThickness3 * 2);
            if (this.dimVisibile) {
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) (originY + lineThickness3), this.dimPaint);
                canvas.drawRect(0.0f, (float) (originY + lineThickness3), (float) (originX + lineThickness3), (float) ((originY + height3) - lineThickness3), this.dimPaint);
                canvas.drawRect((float) ((originX + width3) - lineThickness3), (float) (originY + lineThickness3), (float) getWidth(), (float) ((originY + height3) - lineThickness3), this.dimPaint);
                canvas.drawRect(0.0f, (float) ((originY + height3) - lineThickness3), (float) getWidth(), (float) getHeight(), this.dimPaint);
            }
            if (this.frameVisible) {
                int inset = handleThickness - lineThickness3;
                int gridWidth = width3 - (handleThickness * 2);
                int gridHeight = height3 - (handleThickness * 2);
                GridType type = this.gridType;
                if (type == GridType.NONE && this.gridProgress > 0.0f) {
                    type = this.previousGridType;
                }
                this.shadowPaint.setAlpha((int) (this.gridProgress * 26.0f * this.frameAlpha));
                this.linePaint.setAlpha((int) (this.gridProgress * 178.0f * this.frameAlpha));
                this.framePaint.setAlpha((int) (this.frameAlpha * 178.0f));
                this.handlePaint.setAlpha((int) (this.frameAlpha * 255.0f));
                int i = 0;
                while (true) {
                    int i2 = 3;
                    if (i >= 3) {
                        break;
                    }
                    if (type == GridType.MINOR) {
                        int j = 1;
                        while (j < 4) {
                            if (i == 2 && j == i2) {
                                lineThickness2 = lineThickness3;
                                handleSize2 = handleSize3;
                                width2 = width3;
                                height2 = height3;
                            } else {
                                int startX = originX + handleThickness + (((gridWidth / 3) / 3) * j) + ((gridWidth / 3) * i);
                                handleSize2 = handleSize3;
                                height2 = height3;
                                lineThickness2 = lineThickness3;
                                width2 = width3;
                                Canvas canvas2 = canvas;
                                canvas2.drawLine((float) startX, (float) (originY + handleThickness), (float) startX, (float) (originY + handleThickness + gridHeight), this.shadowPaint);
                                canvas2.drawLine((float) startX, (float) (originY + handleThickness), (float) startX, (float) (originY + handleThickness + gridHeight), this.linePaint);
                                int startY = originY + handleThickness + (((gridHeight / 3) / 3) * j) + ((gridHeight / 3) * i);
                                int i3 = startX;
                                canvas2.drawLine((float) (originX + handleThickness), (float) startY, (float) (originX + handleThickness + gridWidth), (float) startY, this.shadowPaint);
                                canvas.drawLine((float) (originX + handleThickness), (float) startY, (float) (originX + handleThickness + gridWidth), (float) startY, this.linePaint);
                            }
                            j++;
                            handleSize3 = handleSize2;
                            height3 = height2;
                            lineThickness3 = lineThickness2;
                            width3 = width2;
                            i2 = 3;
                        }
                        lineThickness = lineThickness3;
                        handleSize = handleSize3;
                        width = width3;
                        height = height3;
                    } else {
                        lineThickness = lineThickness3;
                        handleSize = handleSize3;
                        width = width3;
                        height = height3;
                        if (type == GridType.MAJOR && i > 0) {
                            int startX2 = originX + handleThickness + ((gridWidth / 3) * i);
                            canvas.drawLine((float) startX2, (float) (originY + handleThickness), (float) startX2, (float) (originY + handleThickness + gridHeight), this.shadowPaint);
                            canvas.drawLine((float) startX2, (float) (originY + handleThickness), (float) startX2, (float) (originY + handleThickness + gridHeight), this.linePaint);
                            int startY2 = originY + handleThickness + ((gridHeight / 3) * i);
                            canvas.drawLine((float) (originX + handleThickness), (float) startY2, (float) (originX + handleThickness + gridWidth), (float) startY2, this.shadowPaint);
                            canvas.drawLine((float) (originX + handleThickness), (float) startY2, (float) (originX + handleThickness + gridWidth), (float) startY2, this.linePaint);
                        }
                    }
                    i++;
                    handleSize3 = handleSize;
                    height3 = height;
                    lineThickness3 = lineThickness;
                    width3 = width;
                }
                int lineThickness4 = lineThickness3;
                int handleSize4 = handleSize3;
                int width4 = width3;
                int height4 = height3;
                Canvas canvas3 = canvas;
                canvas3.drawRect((float) (originX + inset), (float) (originY + inset), (float) ((originX + width4) - inset), (float) (originY + inset + lineThickness4), this.framePaint);
                canvas3.drawRect((float) (originX + inset), (float) (originY + inset), (float) (originX + inset + lineThickness4), (float) ((originY + height4) - inset), this.framePaint);
                canvas3.drawRect((float) (originX + inset), (float) (((originY + height4) - inset) - lineThickness4), (float) ((originX + width4) - inset), (float) ((originY + height4) - inset), this.framePaint);
                canvas3.drawRect((float) (((originX + width4) - inset) - lineThickness4), (float) (originY + inset), (float) ((originX + width4) - inset), (float) ((originY + height4) - inset), this.framePaint);
                canvas.drawRect((float) originX, (float) originY, (float) (originX + handleSize4), (float) (originY + handleThickness), this.handlePaint);
                canvas3.drawRect((float) originX, (float) originY, (float) (originX + handleThickness), (float) (originY + handleSize4), this.handlePaint);
                canvas3.drawRect((float) ((originX + width4) - handleSize4), (float) originY, (float) (originX + width4), (float) (originY + handleThickness), this.handlePaint);
                canvas3.drawRect((float) ((originX + width4) - handleThickness), (float) originY, (float) (originX + width4), (float) (originY + handleSize4), this.handlePaint);
                canvas.drawRect((float) originX, (float) ((originY + height4) - handleThickness), (float) (originX + handleSize4), (float) (originY + height4), this.handlePaint);
                canvas3.drawRect((float) originX, (float) ((originY + height4) - handleSize4), (float) (originX + handleThickness), (float) (originY + height4), this.handlePaint);
                canvas3.drawRect((float) ((originX + width4) - handleSize4), (float) ((originY + height4) - handleThickness), (float) (originX + width4), (float) (originY + height4), this.handlePaint);
                canvas3.drawRect((float) ((originX + width4) - handleThickness), (float) ((originY + height4) - handleSize4), (float) (originX + width4), (float) (originY + height4), this.handlePaint);
                Canvas canvas4 = canvas;
            } else {
                return;
            }
        } else {
            float width5 = ((float) getMeasuredWidth()) - (this.sidePadding * 2.0f);
            int i4 = 0;
            float height5 = ((((float) getMeasuredHeight()) - this.bottomPadding) - ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))) - (this.sidePadding * 2.0f);
            int size = (int) Math.min(width5, height5);
            Bitmap bitmap = this.circleBitmap;
            if (bitmap == null || bitmap.getWidth() != size) {
                Bitmap bitmap2 = this.circleBitmap;
                boolean hasBitmap = bitmap2 != null;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                    this.circleBitmap = null;
                }
                try {
                    this.circleBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas circleCanvas = new Canvas(this.circleBitmap);
                    circleCanvas.drawRect(0.0f, 0.0f, (float) size, (float) size, this.dimPaint);
                    circleCanvas.drawCircle((float) (size / 2), (float) (size / 2), (float) (size / 2), this.eraserPaint);
                    circleCanvas.setBitmap((Bitmap) null);
                    if (!hasBitmap) {
                        this.frameAlpha = 0.0f;
                        this.lastUpdateTime = SystemClock.elapsedRealtime();
                    }
                } catch (Throwable th) {
                }
            }
            if (this.circleBitmap != null) {
                this.bitmapPaint.setAlpha((int) (this.frameAlpha * 255.0f));
                this.dimPaint.setAlpha((int) (this.frameAlpha * 127.0f));
                float f = this.sidePadding;
                float left = ((width5 - ((float) size)) / 2.0f) + f;
                float f2 = f + ((height5 - ((float) size)) / 2.0f);
                if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
                    i4 = AndroidUtilities.statusBarHeight;
                }
                float top = f2 + ((float) i4);
                float bottom = ((float) size) + top;
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) ((int) top), this.dimPaint);
                Canvas canvas5 = canvas;
                canvas5.drawRect(0.0f, (float) ((int) top), (float) ((int) left), (float) ((int) bottom), this.dimPaint);
                canvas.drawRect((float) ((int) (((float) size) + left)), (float) ((int) top), (float) getWidth(), (float) ((int) bottom), this.dimPaint);
                canvas5.drawRect(0.0f, (float) ((int) bottom), (float) getWidth(), (float) getHeight(), this.dimPaint);
                canvas.drawBitmap(this.circleBitmap, (float) ((int) left), (float) ((int) top), this.bitmapPaint);
            } else {
                Canvas canvas6 = canvas;
            }
        }
        if (this.frameAlpha < 1.0f) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            float f3 = this.frameAlpha + (((float) dt) / 180.0f);
            this.frameAlpha = f3;
            if (f3 > 1.0f) {
                this.frameAlpha = 1.0f;
            }
            invalidate();
        }
    }

    public void updateTouchAreas() {
        int touchPadding = AndroidUtilities.dp(16.0f);
        this.topLeftCorner.set(this.actualRect.left - ((float) touchPadding), this.actualRect.top - ((float) touchPadding), this.actualRect.left + ((float) touchPadding), this.actualRect.top + ((float) touchPadding));
        this.topRightCorner.set(this.actualRect.right - ((float) touchPadding), this.actualRect.top - ((float) touchPadding), this.actualRect.right + ((float) touchPadding), this.actualRect.top + ((float) touchPadding));
        this.bottomLeftCorner.set(this.actualRect.left - ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding), this.actualRect.left + ((float) touchPadding), this.actualRect.bottom + ((float) touchPadding));
        this.bottomRightCorner.set(this.actualRect.right - ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding), this.actualRect.right + ((float) touchPadding), this.actualRect.bottom + ((float) touchPadding));
        this.topEdge.set(this.actualRect.left + ((float) touchPadding), this.actualRect.top - ((float) touchPadding), this.actualRect.right - ((float) touchPadding), this.actualRect.top + ((float) touchPadding));
        this.leftEdge.set(this.actualRect.left - ((float) touchPadding), this.actualRect.top + ((float) touchPadding), this.actualRect.left + ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding));
        this.rightEdge.set(this.actualRect.right - ((float) touchPadding), this.actualRect.top + ((float) touchPadding), this.actualRect.right + ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding));
        this.bottomEdge.set(this.actualRect.left + ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding), this.actualRect.right - ((float) touchPadding), this.actualRect.bottom + ((float) touchPadding));
    }

    public float getLockAspectRatio() {
        return this.lockAspectRatio;
    }

    public void setLockedAspectRatio(float aspectRatio) {
        this.lockAspectRatio = aspectRatio;
    }

    public void setGridType(GridType type, boolean animated) {
        Animator animator2 = this.gridAnimator;
        if (animator2 != null && (!animated || this.gridType != type)) {
            animator2.cancel();
            this.gridAnimator = null;
        }
        GridType gridType2 = this.gridType;
        if (gridType2 != type) {
            this.previousGridType = gridType2;
            this.gridType = type;
            float targetProgress = type == GridType.NONE ? 0.0f : 1.0f;
            if (!animated) {
                this.gridProgress = targetProgress;
                invalidate();
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "gridProgress", new float[]{this.gridProgress, targetProgress});
            this.gridAnimator = ofFloat;
            ofFloat.setDuration(200);
            this.gridAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Animator unused = CropAreaView.this.gridAnimator = null;
                }
            });
            if (type == GridType.NONE) {
                this.gridAnimator.setStartDelay(200);
            }
            this.gridAnimator.start();
        }
    }

    private void setGridProgress(float value) {
        this.gridProgress = value;
        invalidate();
    }

    private float getGridProgress() {
        return this.gridProgress;
    }

    public float getAspectRatio() {
        return (this.actualRect.right - this.actualRect.left) / (this.actualRect.bottom - this.actualRect.top);
    }

    public void fill(final RectF targetRect2, Animator scaleAnimator, boolean animated) {
        if (animated) {
            Animator animator2 = this.animator;
            if (animator2 != null) {
                animator2.cancel();
                this.animator = null;
            }
            AnimatorSet set = new AnimatorSet();
            this.animator = set;
            set.setDuration(300);
            Animator[] animators = new Animator[5];
            animators[0] = ObjectAnimator.ofFloat(this, "cropLeft", new float[]{targetRect2.left});
            animators[0].setInterpolator(this.interpolator);
            animators[1] = ObjectAnimator.ofFloat(this, "cropTop", new float[]{targetRect2.top});
            animators[1].setInterpolator(this.interpolator);
            animators[2] = ObjectAnimator.ofFloat(this, "cropRight", new float[]{targetRect2.right});
            animators[2].setInterpolator(this.interpolator);
            animators[3] = ObjectAnimator.ofFloat(this, "cropBottom", new float[]{targetRect2.bottom});
            animators[3].setInterpolator(this.interpolator);
            animators[4] = scaleAnimator;
            animators[4].setInterpolator(this.interpolator);
            set.playTogether(animators);
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    CropAreaView.this.setActualRect(targetRect2);
                    Animator unused = CropAreaView.this.animator = null;
                }
            });
            set.start();
            return;
        }
        setActualRect(targetRect2);
    }

    public void resetAnimator() {
        Animator animator2 = this.animator;
        if (animator2 != null) {
            animator2.cancel();
            this.animator = null;
        }
    }

    private void setCropLeft(float value) {
        this.actualRect.left = value;
        invalidate();
    }

    public float getCropLeft() {
        return this.actualRect.left;
    }

    private void setCropTop(float value) {
        this.actualRect.top = value;
        invalidate();
    }

    public float getCropTop() {
        return this.actualRect.top;
    }

    private void setCropRight(float value) {
        this.actualRect.right = value;
        invalidate();
    }

    public float getCropRight() {
        return this.actualRect.right;
    }

    private void setCropBottom(float value) {
        this.actualRect.bottom = value;
        invalidate();
    }

    public float getCropBottom() {
        return this.actualRect.bottom;
    }

    public float getCropCenterX() {
        return (this.actualRect.left + this.actualRect.right) / 2.0f;
    }

    public float getCropCenterY() {
        return (this.actualRect.top + this.actualRect.bottom) / 2.0f;
    }

    public float getCropWidth() {
        return this.actualRect.right - this.actualRect.left;
    }

    public float getCropHeight() {
        return this.actualRect.bottom - this.actualRect.top;
    }

    public RectF getTargetRectToFill() {
        return getTargetRectToFill(getAspectRatio());
    }

    public RectF getTargetRectToFill(float aspectRatio) {
        calculateRect(this.targetRect, aspectRatio);
        return this.targetRect;
    }

    public void calculateRect(RectF rect, float cropAspectRatio) {
        float right;
        float top;
        float left;
        float bottom;
        float statusBarHeight = (float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
        float measuredHeight = (((float) getMeasuredHeight()) - this.bottomPadding) - statusBarHeight;
        float aspectRatio = ((float) getMeasuredWidth()) / measuredHeight;
        float minSide = Math.min((float) getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float f = this.sidePadding;
        float width = ((float) getMeasuredWidth()) - (f * 2.0f);
        float height = measuredHeight - (f * 2.0f);
        float centerX = ((float) getMeasuredWidth()) / 2.0f;
        float centerY = (measuredHeight / 2.0f) + statusBarHeight;
        if (((double) Math.abs(1.0f - cropAspectRatio)) < 1.0E-4d) {
            left = centerX - (minSide / 2.0f);
            top = centerY - (minSide / 2.0f);
            right = (minSide / 2.0f) + centerX;
            bottom = (minSide / 2.0f) + centerY;
        } else if (((double) (cropAspectRatio - aspectRatio)) > 1.0E-4d || height * cropAspectRatio > width) {
            left = centerX - (width / 2.0f);
            top = centerY - ((width / cropAspectRatio) / 2.0f);
            right = (width / 2.0f) + centerX;
            bottom = centerY + ((width / cropAspectRatio) / 2.0f);
        } else {
            left = centerX - ((height * cropAspectRatio) / 2.0f);
            top = centerY - (height / 2.0f);
            right = ((height * cropAspectRatio) / 2.0f) + centerX;
            bottom = (height / 2.0f) + centerY;
        }
        rect.set(left, top, right, bottom);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (event.getY() - ((ViewGroup) getParent()).getY());
        boolean b = false;
        float statusBarHeight = (float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
        int action = event.getActionMasked();
        if (action == 0) {
            if (this.freeform) {
                if (this.topLeftCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.TOP_LEFT;
                } else if (this.topRightCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.TOP_RIGHT;
                } else if (this.bottomLeftCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.BOTTOM_LEFT;
                } else if (this.bottomRightCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.BOTTOM_RIGHT;
                } else if (this.leftEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.LEFT;
                } else if (this.topEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.TOP;
                } else if (this.rightEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.RIGHT;
                } else if (this.bottomEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.BOTTOM;
                } else {
                    this.activeControl = Control.NONE;
                    return false;
                }
                this.previousX = x;
                this.previousY = y;
                setGridType(GridType.MAJOR, false);
                this.isDragging = true;
                AreaViewListener areaViewListener = this.listener;
                if (areaViewListener != null) {
                    areaViewListener.onAreaChangeBegan();
                }
                return true;
            }
            this.activeControl = Control.NONE;
            return false;
        } else if (action == 1 || action == 3) {
            this.isDragging = false;
            if (this.activeControl == Control.NONE) {
                return false;
            }
            this.activeControl = Control.NONE;
            AreaViewListener areaViewListener2 = this.listener;
            if (areaViewListener2 != null) {
                areaViewListener2.onAreaChangeEnded();
            }
            return true;
        } else if (action != 2 || this.activeControl == Control.NONE) {
            return false;
        } else {
            this.tempRect.set(this.actualRect);
            float translationX = (float) (x - this.previousX);
            float translationY = (float) (y - this.previousY);
            this.previousX = x;
            this.previousY = y;
            if (Math.abs(translationX) > Math.abs(translationY)) {
                b = true;
            }
            switch (AnonymousClass3.$SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[this.activeControl.ordinal()]) {
                case 1:
                    this.tempRect.left += translationX;
                    this.tempRect.top += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float w = this.tempRect.width();
                        float h = this.tempRect.height();
                        if (b) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.left -= this.tempRect.width() - w;
                        this.tempRect.top -= this.tempRect.width() - h;
                        break;
                    }
                    break;
                case 2:
                    this.tempRect.right += translationX;
                    this.tempRect.top += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float h2 = this.tempRect.height();
                        if (b) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.top -= this.tempRect.width() - h2;
                        break;
                    }
                    break;
                case 3:
                    this.tempRect.left += translationX;
                    this.tempRect.bottom += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float w2 = this.tempRect.width();
                        if (b) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.left -= this.tempRect.width() - w2;
                        break;
                    }
                    break;
                case 4:
                    this.tempRect.right += translationX;
                    this.tempRect.bottom += translationY;
                    float f = this.lockAspectRatio;
                    if (f > 0.0f) {
                        if (!b) {
                            constrainRectByHeight(this.tempRect, f);
                            break;
                        } else {
                            constrainRectByWidth(this.tempRect, f);
                            break;
                        }
                    }
                    break;
                case 5:
                    this.tempRect.top += translationY;
                    float f2 = this.lockAspectRatio;
                    if (f2 > 0.0f) {
                        constrainRectByHeight(this.tempRect, f2);
                        break;
                    }
                    break;
                case 6:
                    this.tempRect.left += translationX;
                    float f3 = this.lockAspectRatio;
                    if (f3 > 0.0f) {
                        constrainRectByWidth(this.tempRect, f3);
                        break;
                    }
                    break;
                case 7:
                    this.tempRect.right += translationX;
                    float f4 = this.lockAspectRatio;
                    if (f4 > 0.0f) {
                        constrainRectByWidth(this.tempRect, f4);
                        break;
                    }
                    break;
                case 8:
                    this.tempRect.bottom += translationY;
                    float f5 = this.lockAspectRatio;
                    if (f5 > 0.0f) {
                        constrainRectByHeight(this.tempRect, f5);
                        break;
                    }
                    break;
            }
            if (this.tempRect.left < this.sidePadding) {
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF = this.tempRect;
                    rectF.bottom = rectF.top + ((this.tempRect.right - this.sidePadding) / this.lockAspectRatio);
                }
                this.tempRect.left = this.sidePadding;
            } else if (this.tempRect.right > ((float) getWidth()) - this.sidePadding) {
                this.tempRect.right = ((float) getWidth()) - this.sidePadding;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF2 = this.tempRect;
                    rectF2.bottom = rectF2.top + (this.tempRect.width() / this.lockAspectRatio);
                }
            }
            float f6 = this.sidePadding;
            float topPadding = statusBarHeight + f6;
            float finalBottomPadidng = this.bottomPadding + f6;
            if (this.tempRect.top < topPadding) {
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF3 = this.tempRect;
                    rectF3.right = rectF3.left + ((this.tempRect.bottom - topPadding) * this.lockAspectRatio);
                }
                this.tempRect.top = topPadding;
            } else if (this.tempRect.bottom > ((float) getHeight()) - finalBottomPadidng) {
                this.tempRect.bottom = ((float) getHeight()) - finalBottomPadidng;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF4 = this.tempRect;
                    rectF4.right = rectF4.left + (this.tempRect.height() * this.lockAspectRatio);
                }
            }
            if (this.tempRect.width() < this.minWidth) {
                RectF rectF5 = this.tempRect;
                rectF5.right = rectF5.left + this.minWidth;
            }
            if (this.tempRect.height() < this.minWidth) {
                RectF rectF6 = this.tempRect;
                rectF6.bottom = rectF6.top + this.minWidth;
            }
            float f7 = this.lockAspectRatio;
            if (f7 > 0.0f) {
                if (f7 < 1.0f) {
                    if (this.tempRect.width() <= this.minWidth) {
                        RectF rectF7 = this.tempRect;
                        rectF7.right = rectF7.left + this.minWidth;
                        RectF rectF8 = this.tempRect;
                        rectF8.bottom = rectF8.top + (this.tempRect.width() / this.lockAspectRatio);
                    }
                } else if (this.tempRect.height() <= this.minWidth) {
                    RectF rectF9 = this.tempRect;
                    rectF9.bottom = rectF9.top + this.minWidth;
                    RectF rectvar_ = this.tempRect;
                    rectvar_.right = rectvar_.left + (this.tempRect.height() * this.lockAspectRatio);
                }
            }
            setActualRect(this.tempRect);
            AreaViewListener areaViewListener3 = this.listener;
            if (areaViewListener3 != null) {
                areaViewListener3.onAreaChange();
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.Crop.CropAreaView$3  reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;

        static {
            int[] iArr = new int[Control.values().length];
            $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control = iArr;
            try {
                iArr[Control.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.TOP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.LEFT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private void constrainRectByWidth(RectF rect, float aspectRatio) {
        float w = rect.width();
        rect.right = rect.left + w;
        rect.bottom = rect.top + (w / aspectRatio);
    }

    private void constrainRectByHeight(RectF rect, float aspectRatio) {
        float h = rect.height();
        rect.right = rect.left + (h * aspectRatio);
        rect.bottom = rect.top + h;
    }

    public void getCropRect(RectF rect) {
        rect.set(this.actualRect);
    }
}
