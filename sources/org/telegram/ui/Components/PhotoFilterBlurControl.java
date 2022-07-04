package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.BubbleActivity;

public class PhotoFilterBlurControl extends FrameLayout {
    private static final float BlurInsetProximity = ((float) AndroidUtilities.dp(20.0f));
    private static final float BlurMinimumDifference = 0.02f;
    private static final float BlurMinimumFalloff = 0.1f;
    private static final float BlurViewCenterInset = ((float) AndroidUtilities.dp(30.0f));
    private static final float BlurViewRadiusInset = ((float) AndroidUtilities.dp(30.0f));
    private final int GestureStateBegan = 1;
    private final int GestureStateCancelled = 4;
    private final int GestureStateChanged = 2;
    private final int GestureStateEnded = 3;
    private final int GestureStateFailed = 5;
    private BlurViewActiveControl activeControl;
    private Size actualAreaSize = new Size();
    private float angle;
    private Paint arcPaint = new Paint(1);
    private RectF arcRect = new RectF();
    private Point centerPoint = new Point(0.5f, 0.5f);
    private boolean checkForMoving = true;
    private boolean checkForZooming;
    private PhotoFilterLinearBlurControlDelegate delegate;
    private float falloff = 0.15f;
    private boolean inBubbleMode;
    private boolean isMoving;
    private boolean isZooming;
    private Paint paint = new Paint(1);
    private float pointerScale = 1.0f;
    private float pointerStartX;
    private float pointerStartY;
    private float size = 0.35f;
    private Point startCenterPoint = new Point();
    private float startDistance;
    private float startPointerDistance;
    private float startRadius;
    private int type;

    private enum BlurViewActiveControl {
        BlurViewActiveControlNone,
        BlurViewActiveControlCenter,
        BlurViewActiveControlInnerRadius,
        BlurViewActiveControlOuterRadius,
        BlurViewActiveControlWholeArea,
        BlurViewActiveControlRotation
    }

    public interface PhotoFilterLinearBlurControlDelegate {
        void valueChanged(Point point, float f, float f2, float f3);
    }

    public PhotoFilterBlurControl(Context context) {
        super(context);
        setWillNotDraw(false);
        this.paint.setColor(-1);
        this.arcPaint.setColor(-1);
        this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.arcPaint.setStyle(Paint.Style.STROKE);
        this.inBubbleMode = context instanceof BubbleActivity;
    }

    public void setType(int blurType) {
        this.type = blurType;
        invalidate();
    }

    public void setDelegate(PhotoFilterLinearBlurControlDelegate delegate2) {
        this.delegate = delegate2;
    }

    private float getDistance(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return 0.0f;
        }
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        return (float) Math.sqrt((double) (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }

    private float degreesToRadians(float degrees) {
        return (3.1415927f * degrees) / 180.0f;
    }

    public boolean onTouchEvent(MotionEvent event) {
        MotionEvent motionEvent = event;
        int action = event.getActionMasked();
        switch (action) {
            case 0:
            case 5:
                if (event.getPointerCount() != 1) {
                    if (this.isMoving != 0) {
                        handlePan(3, motionEvent);
                        this.checkForMoving = true;
                        this.isMoving = false;
                    }
                    if (event.getPointerCount() != 2) {
                        handlePinch(3, motionEvent);
                        this.checkForZooming = true;
                        this.isZooming = false;
                        return true;
                    } else if (!this.checkForZooming || this.isZooming) {
                        return true;
                    } else {
                        handlePinch(1, motionEvent);
                        this.isZooming = true;
                        return true;
                    }
                } else if (!this.checkForMoving || this.isMoving) {
                    return true;
                } else {
                    float locationX = event.getX();
                    float locationY = event.getY();
                    Point centerPoint2 = getActualCenterPoint();
                    Point delta = new Point(locationX - centerPoint2.x, locationY - centerPoint2.y);
                    float radialDistance = (float) Math.sqrt((double) ((delta.x * delta.x) + (delta.y * delta.y)));
                    float innerRadius = getActualInnerRadius();
                    float outerRadius = getActualOuterRadius();
                    boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                    float outerRadiusInnerInset = 0.0f;
                    float innerRadiusOuterInset = close ? 0.0f : BlurViewRadiusInset;
                    if (!close) {
                        outerRadiusInnerInset = BlurViewRadiusInset;
                    }
                    int i = this.type;
                    if (i == 0) {
                        double d = (double) delta.x;
                        int i2 = action;
                        float f = locationX;
                        double degreesToRadians = (double) degreesToRadians(this.angle);
                        Double.isNaN(degreesToRadians);
                        double cos = Math.cos(degreesToRadians + 1.5707963267948966d);
                        Double.isNaN(d);
                        double d2 = d * cos;
                        double d3 = (double) delta.y;
                        Point point = centerPoint2;
                        Point point2 = delta;
                        double degreesToRadians2 = (double) degreesToRadians(this.angle);
                        Double.isNaN(degreesToRadians2);
                        double sin = Math.sin(degreesToRadians2 + 1.5707963267948966d);
                        Double.isNaN(d3);
                        float distance = (float) Math.abs(d2 + (d3 * sin));
                        if (radialDistance < BlurViewCenterInset) {
                            this.isMoving = true;
                        } else {
                            float f2 = BlurViewRadiusInset;
                            if (distance > innerRadius - f2 && distance < innerRadius + innerRadiusOuterInset) {
                                this.isMoving = true;
                            } else if (distance > outerRadius - outerRadiusInnerInset && distance < outerRadius + f2) {
                                this.isMoving = true;
                            } else if (distance <= innerRadius - f2 || distance >= f2 + outerRadius) {
                                this.isMoving = true;
                            }
                        }
                    } else {
                        float f3 = locationX;
                        Point point3 = centerPoint2;
                        Point point4 = delta;
                        if (i == 1) {
                            if (radialDistance < BlurViewCenterInset) {
                                this.isMoving = true;
                            } else {
                                float f4 = BlurViewRadiusInset;
                                if (radialDistance > innerRadius - f4 && radialDistance < innerRadius + innerRadiusOuterInset) {
                                    this.isMoving = true;
                                } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < f4 + outerRadius) {
                                    this.isMoving = true;
                                }
                            }
                        }
                    }
                    this.checkForMoving = false;
                    if (this.isMoving) {
                        handlePan(1, motionEvent);
                    }
                    return true;
                }
            case 1:
            case 3:
            case 6:
                if (this.isMoving) {
                    handlePan(3, motionEvent);
                    this.isMoving = false;
                } else if (this.isZooming) {
                    handlePinch(3, motionEvent);
                    this.isZooming = false;
                }
                this.checkForMoving = true;
                this.checkForZooming = true;
                int i3 = action;
                return true;
            case 2:
                if (this.isMoving) {
                    handlePan(2, motionEvent);
                    int i4 = action;
                    return true;
                } else if (this.isZooming) {
                    handlePinch(2, motionEvent);
                    int i5 = action;
                    return true;
                } else {
                    int i6 = action;
                    return true;
                }
            default:
                int i7 = action;
                return true;
        }
    }

    private void handlePan(int state, MotionEvent event) {
        float locationX = event.getX();
        float locationY = event.getY();
        Point actualCenterPoint = getActualCenterPoint();
        float dx = locationX - actualCenterPoint.x;
        float dy = locationY - actualCenterPoint.y;
        float radialDistance = (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
        float shorterSide = Math.min(this.actualAreaSize.width, this.actualAreaSize.height);
        float innerRadius = this.falloff * shorterSide;
        float outerRadius = this.size * shorterSide;
        double d = (double) dx;
        double degreesToRadians = (double) degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians);
        double cos = Math.cos(degreesToRadians + 1.5707963267948966d);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = (double) dy;
        double degreesToRadians2 = (double) degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians2);
        double sin = Math.sin(degreesToRadians2 + 1.5707963267948966d);
        Double.isNaN(d3);
        float distance = (float) Math.abs(d2 + (d3 * sin));
        switch (state) {
            case 1:
                float f = dx;
                float f2 = dy;
                this.pointerStartX = event.getX();
                this.pointerStartY = event.getY();
                boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                float innerRadiusOuterInset = close ? 0.0f : BlurViewRadiusInset;
                float outerRadiusInnerInset = close ? 0.0f : BlurViewRadiusInset;
                int i = this.type;
                if (i == 0) {
                    if (radialDistance < BlurViewCenterInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        this.startCenterPoint = actualCenterPoint;
                    } else {
                        float f3 = BlurViewRadiusInset;
                        if (distance > innerRadius - f3 && distance < innerRadius + innerRadiusOuterInset) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            this.startDistance = distance;
                            this.startRadius = innerRadius;
                        } else if (distance > outerRadius - outerRadiusInnerInset && distance < outerRadius + f3) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            this.startDistance = distance;
                            this.startRadius = outerRadius;
                        } else if (distance <= innerRadius - f3 || distance >= f3 + outerRadius) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                        }
                    }
                } else if (i == 1) {
                    if (radialDistance < BlurViewCenterInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        this.startCenterPoint = actualCenterPoint;
                    } else {
                        float f4 = BlurViewRadiusInset;
                        if (radialDistance > innerRadius - f4 && radialDistance < innerRadius + innerRadiusOuterInset) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            this.startDistance = radialDistance;
                            this.startRadius = innerRadius;
                        } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < f4 + outerRadius) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            this.startDistance = radialDistance;
                            this.startRadius = outerRadius;
                        }
                    }
                }
                setSelected(true, true);
                return;
            case 2:
                int i2 = this.type;
                if (i2 == 0) {
                    switch (AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()]) {
                        case 1:
                            float f5 = dy;
                            float translationX = locationX - this.pointerStartX;
                            float translationY = locationY - this.pointerStartY;
                            Rect actualArea = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) + ((((float) getHeight()) - this.actualAreaSize.height) / 2.0f), this.actualAreaSize.width, this.actualAreaSize.height);
                            float f6 = translationX;
                            Point newPoint = new Point(Math.max(actualArea.x, Math.min(actualArea.x + actualArea.width, this.startCenterPoint.x + translationX)), Math.max(actualArea.y, Math.min(actualArea.y + actualArea.height, this.startCenterPoint.y + translationY)));
                            Point point = newPoint;
                            this.centerPoint = new Point((newPoint.x - actualArea.x) / this.actualAreaSize.width, ((newPoint.y - actualArea.y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                            float f7 = locationX;
                            break;
                        case 2:
                            float f8 = dy;
                            this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (distance - this.startDistance)) / shorterSide), this.size - 0.02f);
                            float f9 = locationX;
                            break;
                        case 3:
                            float var_ = dy;
                            this.size = Math.max(this.falloff + 0.02f, (this.startRadius + (distance - this.startDistance)) / shorterSide);
                            float var_ = locationX;
                            break;
                        case 4:
                            float translationX2 = locationX - this.pointerStartX;
                            float translationY2 = locationY - this.pointerStartY;
                            boolean clockwise = false;
                            boolean right = locationX > actualCenterPoint.x;
                            boolean bottom = locationY > actualCenterPoint.y;
                            boolean b = Math.abs(translationY2) > Math.abs(translationX2);
                            if (right || bottom) {
                                if (!right || bottom) {
                                    if (!right || !bottom) {
                                        if (b) {
                                            if (translationY2 < 0.0f) {
                                                clockwise = true;
                                            }
                                        } else if (translationX2 < 0.0f) {
                                            clockwise = true;
                                        }
                                    } else if (b) {
                                        if (translationY2 > 0.0f) {
                                            clockwise = true;
                                        }
                                    } else if (translationX2 < 0.0f) {
                                        clockwise = true;
                                    }
                                } else if (b) {
                                    if (translationY2 > 0.0f) {
                                        clockwise = true;
                                    }
                                } else if (translationX2 > 0.0f) {
                                    clockwise = true;
                                }
                            } else if (b) {
                                if (translationY2 < 0.0f) {
                                    clockwise = true;
                                }
                            } else if (translationX2 > 0.0f) {
                                clockwise = true;
                            }
                            float var_ = dx;
                            float var_ = dy;
                            float var_ = translationX2;
                            this.angle += ((((float) ((clockwise * true) - 1)) * ((float) Math.sqrt((double) ((translationX2 * translationX2) + (translationY2 * translationY2))))) / 3.1415927f) / 1.15f;
                            this.pointerStartX = locationX;
                            this.pointerStartY = locationY;
                            float translationX3 = locationX;
                            break;
                        default:
                            float var_ = dx;
                            float var_ = dy;
                            float var_ = locationX;
                            break;
                    }
                } else {
                    float var_ = dy;
                    if (i2 == 1) {
                        switch (AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()]) {
                            case 1:
                                float translationX4 = locationX - this.pointerStartX;
                                float translationY3 = locationY - this.pointerStartY;
                                Rect actualArea2 = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) + ((((float) getHeight()) - this.actualAreaSize.height) / 2.0f), this.actualAreaSize.width, this.actualAreaSize.height);
                                float var_ = locationX;
                                Point newPoint2 = new Point(Math.max(actualArea2.x, Math.min(actualArea2.x + actualArea2.width, this.startCenterPoint.x + translationX4)), Math.max(actualArea2.y, Math.min(actualArea2.y + actualArea2.height, this.startCenterPoint.y + translationY3)));
                                Point point2 = newPoint2;
                                this.centerPoint = new Point((newPoint2.x - actualArea2.x) / this.actualAreaSize.width, ((newPoint2.y - actualArea2.y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                                break;
                            case 2:
                                this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (radialDistance - this.startDistance)) / shorterSide), this.size - 0.02f);
                                float var_ = locationX;
                                break;
                            case 3:
                                this.size = Math.max(this.falloff + 0.02f, (this.startRadius + (radialDistance - this.startDistance)) / shorterSide);
                                float var_ = locationX;
                                break;
                            default:
                                float var_ = locationX;
                                break;
                        }
                    }
                }
                invalidate();
                PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
                if (photoFilterLinearBlurControlDelegate != null) {
                    photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
                    return;
                }
                return;
            case 3:
            case 4:
            case 5:
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                float var_ = locationX;
                float var_ = dx;
                float var_ = dy;
                return;
            default:
                float var_ = locationX;
                float var_ = dx;
                float var_ = dy;
                return;
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterBlurControl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl;

        static {
            int[] iArr = new int[BlurViewActiveControl.values().length];
            $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl = iArr;
            try {
                iArr[BlurViewActiveControl.BlurViewActiveControlCenter.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlInnerRadius.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlOuterRadius.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlRotation.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void handlePinch(int state, MotionEvent event) {
        switch (state) {
            case 1:
                this.startPointerDistance = getDistance(event);
                this.pointerScale = 1.0f;
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlWholeArea;
                setSelected(true, true);
                break;
            case 2:
                break;
            case 3:
            case 4:
            case 5:
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            default:
                return;
        }
        float newDistance = getDistance(event);
        float f = this.pointerScale + (((newDistance - this.startPointerDistance) / AndroidUtilities.density) * 0.01f);
        this.pointerScale = f;
        float max = Math.max(0.1f, this.falloff * f);
        this.falloff = max;
        this.size = Math.max(max + 0.02f, this.size * this.pointerScale);
        this.pointerScale = 1.0f;
        this.startPointerDistance = newDistance;
        invalidate();
        PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
        if (photoFilterLinearBlurControlDelegate != null) {
            photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    private void setSelected(boolean selected, boolean animated) {
    }

    public void setActualAreaSize(float width, float height) {
        this.actualAreaSize.width = width;
        this.actualAreaSize.height = height;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        Point centerPoint2 = getActualCenterPoint();
        float innerRadius = getActualInnerRadius();
        float outerRadius = getActualOuterRadius();
        canvas2.translate(centerPoint2.x, centerPoint2.y);
        int i = this.type;
        if (i == 0) {
            canvas2.rotate(this.angle);
            float space = (float) AndroidUtilities.dp(6.0f);
            float length = (float) AndroidUtilities.dp(12.0f);
            float thickness = (float) AndroidUtilities.dp(1.5f);
            int i2 = 0;
            while (i2 < 30) {
                Canvas canvas3 = canvas;
                int i3 = i2;
                canvas3.drawRect((length + space) * ((float) i2), -innerRadius, (((float) i2) * (length + space)) + length, thickness - innerRadius, this.paint);
                float left = ((((float) (-i3)) * (length + space)) - space) - length;
                float right = (((float) (-i3)) * (length + space)) - space;
                canvas.drawRect(left, -innerRadius, right, thickness - innerRadius, this.paint);
                Canvas canvas4 = canvas;
                float f = innerRadius;
                canvas4.drawRect((length + space) * ((float) i3), f, length + (((float) i3) * (length + space)), thickness + innerRadius, this.paint);
                canvas4.drawRect(left, f, right, thickness + innerRadius, this.paint);
                i2 = i3 + 1;
            }
            int i4 = i2;
            float length2 = (float) AndroidUtilities.dp(6.0f);
            for (int i5 = 0; i5 < 64; i5++) {
                canvas.drawRect((length2 + space) * ((float) i5), -outerRadius, length2 + (((float) i5) * (length2 + space)), thickness - outerRadius, this.paint);
                float left2 = ((((float) (-i5)) * (length2 + space)) - space) - length2;
                float right2 = (((float) (-i5)) * (length2 + space)) - space;
                canvas.drawRect(left2, -outerRadius, right2, thickness - outerRadius, this.paint);
                Canvas canvas5 = canvas;
                float f2 = outerRadius;
                canvas5.drawRect((length2 + space) * ((float) i5), f2, length2 + (((float) i5) * (length2 + space)), thickness + outerRadius, this.paint);
                canvas5.drawRect(left2, f2, right2, thickness + outerRadius, this.paint);
            }
        } else if (i == 1) {
            this.arcRect.set(-innerRadius, -innerRadius, innerRadius, innerRadius);
            for (int i6 = 0; i6 < 22; i6++) {
                canvas.drawArc(this.arcRect, (6.15f + 10.2f) * ((float) i6), 10.2f, false, this.arcPaint);
            }
            this.arcRect.set(-outerRadius, -outerRadius, outerRadius, outerRadius);
            for (int i7 = 0; i7 < 64; i7++) {
                canvas.drawArc(this.arcRect, (2.02f + 3.6f) * ((float) i7), 3.6f, false, this.arcPaint);
            }
        }
        canvas2.drawCircle(0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), this.paint);
    }

    private Point getActualCenterPoint() {
        return new Point(((((float) getWidth()) - this.actualAreaSize.width) / 2.0f) + (this.centerPoint.x * this.actualAreaSize.width), ((((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) + ((((float) getHeight()) - this.actualAreaSize.height) / 2.0f)) - ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) + (this.centerPoint.y * this.actualAreaSize.width));
    }

    private float getActualInnerRadius() {
        return Math.min(this.actualAreaSize.width, this.actualAreaSize.height) * this.falloff;
    }

    private float getActualOuterRadius() {
        return Math.min(this.actualAreaSize.width, this.actualAreaSize.height) * this.size;
    }
}
