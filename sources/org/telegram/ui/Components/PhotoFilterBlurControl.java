package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

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
        this.arcPaint.setStyle(Style.STROKE);
    }

    public void setType(int blurType) {
        this.type = blurType;
        invalidate();
    }

    public void setDelegate(PhotoFilterLinearBlurControlDelegate delegate) {
        this.delegate = delegate;
    }

    private float getDistance(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return BlurInsetProximity;
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(MotionEvent event) {
        PhotoFilterBlurControl photoFilterBlurControl = this;
        MotionEvent motionEvent = event;
        int action = event.getActionMasked();
        switch (action) {
            case 0:
            case 5:
                if (event.getPointerCount() != 1) {
                    if (photoFilterBlurControl.isMoving) {
                        handlePan(3, motionEvent);
                        photoFilterBlurControl.checkForMoving = true;
                        photoFilterBlurControl.isMoving = false;
                    }
                    if (event.getPointerCount() != 2) {
                        handlePinch(3, motionEvent);
                        photoFilterBlurControl.checkForZooming = true;
                        photoFilterBlurControl.isZooming = false;
                        return true;
                    } else if (photoFilterBlurControl.checkForZooming && !photoFilterBlurControl.isZooming) {
                        handlePinch(1, motionEvent);
                        photoFilterBlurControl.isZooming = true;
                        return true;
                    }
                } else if (photoFilterBlurControl.checkForMoving && !photoFilterBlurControl.isMoving) {
                    boolean z;
                    float locationX = event.getX();
                    float locationY = event.getY();
                    Point centerPoint = getActualCenterPoint();
                    Point delta = new Point(locationX - centerPoint.f24x, locationY - centerPoint.f25y);
                    float radialDistance = (float) Math.sqrt((double) ((delta.f24x * delta.f24x) + (delta.f25y * delta.f25y)));
                    float innerRadius = getActualInnerRadius();
                    float outerRadius = getActualOuterRadius();
                    boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                    float outerRadiusInnerInset = BlurInsetProximity;
                    float innerRadiusOuterInset = close ? BlurInsetProximity : BlurViewRadiusInset;
                    if (!close) {
                        outerRadiusInnerInset = BlurViewRadiusInset;
                    }
                    if (photoFilterBlurControl.type == 0) {
                        int i = action;
                        action = (float) Math.abs((((double) delta.f24x) * Math.cos(((double) degreesToRadians(photoFilterBlurControl.angle)) + NUM)) + (((double) delta.f25y) * Math.sin(((double) degreesToRadians(photoFilterBlurControl.angle)) + 1.5707963267948966d)));
                        if (radialDistance < BlurViewCenterInset) {
                            z = true;
                            photoFilterBlurControl.isMoving = true;
                        } else if (action > innerRadius - BlurViewRadiusInset && action < innerRadius + innerRadiusOuterInset) {
                            z = true;
                            photoFilterBlurControl.isMoving = true;
                        } else if (action <= outerRadius - outerRadiusInnerInset || action >= BlurViewRadiusInset + outerRadius) {
                            if (action > innerRadius - BlurViewRadiusInset) {
                                if (action < BlurViewRadiusInset + outerRadius) {
                                    z = true;
                                }
                            }
                            z = true;
                            photoFilterBlurControl.isMoving = true;
                        } else {
                            z = true;
                            photoFilterBlurControl.isMoving = true;
                        }
                    } else {
                        float f = locationX;
                        z = true;
                        Point point = centerPoint;
                        Point point2 = delta;
                        if (photoFilterBlurControl.type == z) {
                            if (radialDistance < BlurViewCenterInset) {
                                photoFilterBlurControl.isMoving = z;
                            } else if (radialDistance <= innerRadius - BlurViewRadiusInset || radialDistance >= innerRadius + innerRadiusOuterInset) {
                                if (radialDistance <= outerRadius - outerRadiusInnerInset || radialDistance >= BlurViewRadiusInset + outerRadius) {
                                    action = 1;
                                } else {
                                    action = 1;
                                    photoFilterBlurControl.isMoving = true;
                                }
                                photoFilterBlurControl.checkForMoving = false;
                                if (photoFilterBlurControl.isMoving) {
                                    handlePan(action, motionEvent);
                                }
                            } else {
                                action = 1;
                                photoFilterBlurControl.isMoving = true;
                                photoFilterBlurControl.checkForMoving = false;
                                if (photoFilterBlurControl.isMoving) {
                                    handlePan(action, motionEvent);
                                }
                            }
                        }
                    }
                    action = z;
                    photoFilterBlurControl.checkForMoving = false;
                    if (photoFilterBlurControl.isMoving) {
                        handlePan(action, motionEvent);
                    }
                }
                return true;
            case 1:
            case 3:
            case 6:
                if (photoFilterBlurControl.isMoving) {
                    handlePan(3, motionEvent);
                    photoFilterBlurControl.isMoving = false;
                } else if (photoFilterBlurControl.isZooming) {
                    handlePinch(3, motionEvent);
                    photoFilterBlurControl.isZooming = false;
                }
                photoFilterBlurControl.checkForMoving = true;
                photoFilterBlurControl.checkForZooming = true;
                break;
            case 2:
                if (!photoFilterBlurControl.isMoving) {
                    if (photoFilterBlurControl.isZooming) {
                        handlePinch(2, motionEvent);
                        break;
                    }
                }
                handlePan(2, motionEvent);
                break;
                break;
        }
    }

    private void handlePan(int state, MotionEvent event) {
        float locationX = event.getX();
        float locationY = event.getY();
        Point actualCenterPoint = getActualCenterPoint();
        Point delta = new Point(locationX - actualCenterPoint.f24x, locationY - actualCenterPoint.f25y);
        float radialDistance = (float) Math.sqrt((double) ((delta.f24x * delta.f24x) + (delta.f25y * delta.f25y)));
        float shorterSide = this.actualAreaSize.width > this.actualAreaSize.height ? r0.actualAreaSize.height : r0.actualAreaSize.width;
        float innerRadius = r0.falloff * shorterSide;
        float outerRadius = r0.size * shorterSide;
        float distance = (float) Math.abs((((double) delta.f24x) * Math.cos(((double) degreesToRadians(r0.angle)) + 1.5707963267948966d)) + (((double) delta.f25y) * Math.sin(((double) degreesToRadians(r0.angle)) + 1.5707963267948966d)));
        float outerRadiusInnerInset = BlurInsetProximity;
        float innerRadiusOuterInset;
        Point point;
        switch (state) {
            case 1:
                r0.pointerStartX = event.getX();
                r0.pointerStartY = event.getY();
                boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                innerRadiusOuterInset = close ? BlurInsetProximity : BlurViewRadiusInset;
                if (!close) {
                    outerRadiusInnerInset = BlurViewRadiusInset;
                }
                float innerRadius2;
                float outerRadius2;
                if (r0.type == 0) {
                    if (radialDistance < BlurViewCenterInset) {
                        r0.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        r0.startCenterPoint = actualCenterPoint;
                    } else if (distance <= innerRadius - BlurViewRadiusInset || distance >= innerRadius + innerRadiusOuterInset) {
                        innerRadius2 = innerRadius;
                        if (distance <= outerRadius - outerRadiusInnerInset || distance >= outerRadius + BlurViewRadiusInset) {
                            outerRadius2 = outerRadius;
                            if (distance <= innerRadius2 - BlurViewRadiusInset || distance >= BlurViewRadiusInset + outerRadius2) {
                                r0.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                            }
                        } else {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            r0.startDistance = distance;
                            r0.startRadius = outerRadius;
                        }
                    } else {
                        r0.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                        r0.startDistance = distance;
                        r0.startRadius = innerRadius;
                    }
                } else {
                    innerRadius2 = innerRadius;
                    outerRadius2 = outerRadius;
                    if (r0.type == 1) {
                        if (radialDistance < BlurViewCenterInset) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                            r0.startCenterPoint = actualCenterPoint;
                        } else if (radialDistance > innerRadius2 - BlurViewRadiusInset && radialDistance < innerRadius2 + innerRadiusOuterInset) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            r0.startDistance = radialDistance;
                            r0.startRadius = innerRadius2;
                        } else if (radialDistance > outerRadius2 - outerRadiusInnerInset && radialDistance < BlurViewRadiusInset + outerRadius2) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            r0.startDistance = radialDistance;
                            r0.startRadius = outerRadius2;
                        }
                    }
                }
                setSelected(true, true);
                return;
            case 2:
                if (r0.type != 0) {
                    if (r0.type == 1) {
                        switch (r0.activeControl) {
                            case BlurViewActiveControlCenter:
                                delta = locationX - r0.pointerStartX;
                                innerRadiusOuterInset = locationY - r0.pointerStartY;
                                Rect actualArea = new Rect((((float) getWidth()) - r0.actualAreaSize.width) / 2.0f, (((float) getHeight()) - r0.actualAreaSize.height) / 2.0f, r0.actualAreaSize.width, r0.actualAreaSize.height);
                                Point newPoint = new Point(Math.max(actualArea.f26x, Math.min(actualArea.f26x + actualArea.width, r0.startCenterPoint.f24x + delta)), Math.max(actualArea.f27y, Math.min(actualArea.f27y + actualArea.height, r0.startCenterPoint.f25y + innerRadiusOuterInset)));
                                r0.centerPoint = new Point((newPoint.f24x - actualArea.f26x) / r0.actualAreaSize.width, ((newPoint.f25y - actualArea.f27y) + ((r0.actualAreaSize.width - r0.actualAreaSize.height) / 2.0f)) / r0.actualAreaSize.width);
                                break;
                            case BlurViewActiveControlInnerRadius:
                                r0.falloff = Math.min(Math.max(0.1f, (r0.startRadius + (radialDistance - r0.startDistance)) / shorterSide), r0.size - BlurMinimumDifference);
                                break;
                            case BlurViewActiveControlOuterRadius:
                                r0.size = Math.max(r0.falloff + BlurMinimumDifference, (r0.startRadius + (radialDistance - r0.startDistance)) / shorterSide);
                                break;
                            default:
                                break;
                        }
                    }
                }
                switch (r0.activeControl) {
                    case BlurViewActiveControlCenter:
                        innerRadiusOuterInset = locationX - r0.pointerStartX;
                        outerRadiusInnerInset = locationY - r0.pointerStartY;
                        Rect actualArea2 = new Rect((((float) getWidth()) - r0.actualAreaSize.width) / 2.0f, (((float) getHeight()) - r0.actualAreaSize.height) / 2.0f, r0.actualAreaSize.width, r0.actualAreaSize.height);
                        Point newPoint2 = new Point(Math.max(actualArea2.f26x, Math.min(actualArea2.f26x + actualArea2.width, r0.startCenterPoint.f24x + innerRadiusOuterInset)), Math.max(actualArea2.f27y, Math.min(actualArea2.f27y + actualArea2.height, r0.startCenterPoint.f25y + outerRadiusInnerInset)));
                        point = delta;
                        r0.centerPoint = new Point((newPoint2.f24x - actualArea2.f26x) / r0.actualAreaSize.width, ((newPoint2.f25y - actualArea2.f27y) + ((r0.actualAreaSize.width - r0.actualAreaSize.height) / 2.0f)) / r0.actualAreaSize.width);
                        break;
                    case BlurViewActiveControlInnerRadius:
                        r0.falloff = Math.min(Math.max(0.1f, (r0.startRadius + (distance - r0.startDistance)) / shorterSide), r0.size - BlurMinimumDifference);
                        break;
                    case BlurViewActiveControlOuterRadius:
                        r0.size = Math.max(r0.falloff + BlurMinimumDifference, (r0.startRadius + (distance - r0.startDistance)) / shorterSide);
                        break;
                    case BlurViewActiveControlRotation:
                        innerRadiusOuterInset = locationX - r0.pointerStartX;
                        float translationY = locationY - r0.pointerStartY;
                        boolean clockwise = false;
                        boolean right = locationX > actualCenterPoint.f24x;
                        boolean bottom = locationY > actualCenterPoint.f25y;
                        if (right || bottom) {
                            if (!right || bottom) {
                                if (right && bottom) {
                                    if (Math.abs(translationY) > Math.abs(innerRadiusOuterInset)) {
                                        if (translationY > BlurInsetProximity) {
                                            clockwise = true;
                                        }
                                    } else if (innerRadiusOuterInset < BlurInsetProximity) {
                                        clockwise = true;
                                    }
                                } else if (Math.abs(translationY) > Math.abs(innerRadiusOuterInset)) {
                                    if (translationY < BlurInsetProximity) {
                                        clockwise = true;
                                    }
                                } else if (innerRadiusOuterInset < BlurInsetProximity) {
                                    clockwise = true;
                                }
                            } else if (Math.abs(translationY) > Math.abs(innerRadiusOuterInset)) {
                                if (translationY > BlurInsetProximity) {
                                    clockwise = true;
                                }
                            } else if (innerRadiusOuterInset > BlurInsetProximity) {
                                clockwise = true;
                            }
                        } else if (Math.abs(translationY) > Math.abs(innerRadiusOuterInset)) {
                            if (translationY < BlurInsetProximity) {
                                clockwise = true;
                            }
                        } else if (innerRadiusOuterInset > BlurInsetProximity) {
                            clockwise = true;
                        }
                        r0.angle += ((((float) (((clockwise ? 1 : 0) * 2) - 1)) * ((float) Math.sqrt((double) ((innerRadiusOuterInset * innerRadiusOuterInset) + (translationY * translationY))))) / 3.1415927f) / 1.15f;
                        r0.pointerStartX = locationX;
                        r0.pointerStartY = locationY;
                        break;
                    default:
                        point = delta;
                        break;
                }
                invalidate();
                if (r0.delegate != null) {
                    r0.delegate.valueChanged(r0.centerPoint, r0.falloff, r0.size, degreesToRadians(r0.angle) + 1.5707964f);
                    break;
                }
                break;
            case 3:
            case 4:
            case 5:
                r0.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                point = delta;
                break;
            default:
                return;
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
        this.pointerScale += ((newDistance - this.startPointerDistance) / AndroidUtilities.density) * 0.01f;
        this.falloff = Math.max(0.1f, this.falloff * this.pointerScale);
        this.size = Math.max(this.falloff + BlurMinimumDifference, this.size * this.pointerScale);
        this.pointerScale = 1.0f;
        this.startPointerDistance = newDistance;
        invalidate();
        if (this.delegate != null) {
            this.delegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    private void setSelected(boolean selected, boolean animated) {
    }

    public void setActualAreaSize(float width, float height) {
        this.actualAreaSize.width = width;
        this.actualAreaSize.height = height;
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        Point centerPoint = getActualCenterPoint();
        float innerRadius = getActualInnerRadius();
        float outerRadius = getActualOuterRadius();
        canvas2.translate(centerPoint.f24x, centerPoint.f25y);
        int i;
        Canvas canvas3;
        int i2;
        if (this.type == 0) {
            float f;
            float f2;
            canvas2.rotate(r0.angle);
            float space = (float) AndroidUtilities.dp(6.0f);
            float length = (float) AndroidUtilities.dp(12.0f);
            float thickness = (float) AndroidUtilities.dp(1.5f);
            i = 0;
            while (true) {
                int i3 = i;
                if (i3 >= 30) {
                    break;
                }
                f = thickness - innerRadius;
                int i4 = i3;
                float f3 = f;
                f = thickness;
                canvas2.drawRect((length + space) * ((float) i3), -innerRadius, (((float) i3) * (length + space)) + length, f3, r0.paint);
                canvas2.drawRect(((((float) (-i4)) * (length + space)) - space) - length, -innerRadius, (((float) (-i4)) * (length + space)) - space, f - innerRadius, r0.paint);
                canvas3 = canvas2;
                f2 = innerRadius;
                canvas3.drawRect((length + space) * ((float) i4), f2, length + (((float) i4) * (length + space)), f + innerRadius, r0.paint);
                canvas2.drawRect(((((float) (-i4)) * (length + space)) - space) - length, innerRadius, (((float) (-i4)) * (length + space)) - space, f + innerRadius, r0.paint);
                i = i4 + 1;
                thickness = f;
            }
            f = thickness;
            float length2 = (float) AndroidUtilities.dp(6.0f);
            i2 = 0;
            while (true) {
                int i5 = i2;
                if (i5 >= 64) {
                    break;
                }
                canvas3 = canvas2;
                canvas3.drawRect((length2 + space) * ((float) i5), -outerRadius, length2 + (((float) i5) * (length2 + space)), f - outerRadius, r0.paint);
                canvas2.drawRect(((((float) (-i5)) * (length2 + space)) - space) - length2, -outerRadius, (((float) (-i5)) * (length2 + space)) - space, f - outerRadius, r0.paint);
                canvas3 = canvas2;
                f2 = outerRadius;
                canvas3.drawRect((length2 + space) * ((float) i5), f2, length2 + (((float) i5) * (length2 + space)), f + outerRadius, r0.paint);
                canvas2.drawRect(((((float) (-i5)) * (length2 + space)) - space) - length2, outerRadius, (((float) (-i5)) * (length2 + space)) - space, f + outerRadius, r0.paint);
                i2 = i5 + 1;
            }
        } else if (r0.type == 1) {
            int i6;
            float f4;
            r0.arcRect.set(-innerRadius, -innerRadius, innerRadius, innerRadius);
            i = 0;
            while (true) {
                i6 = i;
                if (i6 >= 22) {
                    break;
                }
                canvas3 = canvas2;
                f4 = 10.2f;
                canvas3.drawArc(r0.arcRect, (6.15f + 10.2f) * ((float) i6), f4, false, r0.arcPaint);
                i = i6 + 1;
            }
            r0.arcRect.set(-outerRadius, -outerRadius, outerRadius, outerRadius);
            i2 = 0;
            while (true) {
                i6 = i2;
                if (i6 >= 64) {
                    break;
                }
                canvas3 = canvas2;
                f4 = 3.6f;
                canvas3.drawArc(r0.arcRect, (2.02f + 3.6f) * ((float) i6), f4, false, r0.arcPaint);
                i2 = i6 + 1;
            }
        }
        canvas2.drawCircle(BlurInsetProximity, BlurInsetProximity, (float) AndroidUtilities.dp(8.0f), r0.paint);
    }

    private Point getActualCenterPoint() {
        return new Point(((((float) getWidth()) - this.actualAreaSize.width) / 2.0f) + (this.centerPoint.f24x * this.actualAreaSize.width), ((((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + ((((float) getHeight()) - this.actualAreaSize.height) / 2.0f)) - ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) + (this.centerPoint.f25y * this.actualAreaSize.width));
    }

    private float getActualInnerRadius() {
        return (this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width) * this.falloff;
    }

    private float getActualOuterRadius() {
        return (this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width) * this.size;
    }
}
