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

    private float degreesToRadians(float f) {
        return (f * 3.1415927f) / 180.0f;
    }

    private void setSelected(boolean z, boolean z2) {
    }

    public PhotoFilterBlurControl(Context context) {
        super(context);
        setWillNotDraw(null);
        this.paint.setColor(-1);
        this.arcPaint.setColor(-1);
        this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.arcPaint.setStyle(Style.STROKE);
    }

    public void setType(int i) {
        this.type = i;
        invalidate();
    }

    public void setDelegate(PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate) {
        this.delegate = photoFilterLinearBlurControlDelegate;
    }

    private float getDistance(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() != 2) {
            return null;
        }
        float x = motionEvent.getX(0);
        float y = motionEvent.getY(0);
        x -= motionEvent.getX(1);
        y -= motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        PhotoFilterBlurControl photoFilterBlurControl = this;
        MotionEvent motionEvent2 = motionEvent;
        switch (motionEvent.getActionMasked()) {
            case 0:
            case 5:
                if (motionEvent.getPointerCount() == 1) {
                    if (photoFilterBlurControl.checkForMoving && !photoFilterBlurControl.isMoving) {
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        Point actualCenterPoint = getActualCenterPoint();
                        Point point = new Point(x - actualCenterPoint.f24x, y - actualCenterPoint.f25y);
                        x = (float) Math.sqrt((double) ((point.f24x * point.f24x) + (point.f25y * point.f25y)));
                        y = getActualInnerRadius();
                        float actualOuterRadius = getActualOuterRadius();
                        boolean z = Math.abs(actualOuterRadius - y) < BlurInsetProximity;
                        float f = BlurInsetProximity;
                        float f2 = z ? BlurInsetProximity : BlurViewRadiusInset;
                        if (!z) {
                            f = BlurViewRadiusInset;
                        }
                        if (photoFilterBlurControl.type == 0) {
                            float abs = (float) Math.abs((((double) point.f24x) * Math.cos(((double) degreesToRadians(photoFilterBlurControl.angle)) + 1.5707963267948966d)) + (((double) point.f25y) * Math.sin(((double) degreesToRadians(photoFilterBlurControl.angle)) + 1.5707963267948966d)));
                            if (x < BlurViewCenterInset) {
                                photoFilterBlurControl.isMoving = true;
                            } else if (abs > y - BlurViewRadiusInset && abs < f2 + y) {
                                photoFilterBlurControl.isMoving = true;
                            } else if (abs > actualOuterRadius - f && abs < BlurViewRadiusInset + actualOuterRadius) {
                                photoFilterBlurControl.isMoving = true;
                            } else if (abs <= y - BlurViewRadiusInset || abs >= actualOuterRadius + BlurViewRadiusInset) {
                                photoFilterBlurControl.isMoving = true;
                            }
                        } else if (photoFilterBlurControl.type == 1) {
                            if (x < BlurViewCenterInset) {
                                photoFilterBlurControl.isMoving = true;
                            } else if (x > y - BlurViewRadiusInset && x < y + f2) {
                                photoFilterBlurControl.isMoving = true;
                            } else if (x > actualOuterRadius - f && x < actualOuterRadius + BlurViewRadiusInset) {
                                photoFilterBlurControl.isMoving = true;
                            }
                        }
                        photoFilterBlurControl.checkForMoving = false;
                        if (photoFilterBlurControl.isMoving) {
                            handlePan(1, motionEvent2);
                            break;
                        }
                    }
                }
                if (photoFilterBlurControl.isMoving) {
                    handlePan(3, motionEvent2);
                    photoFilterBlurControl.checkForMoving = true;
                    photoFilterBlurControl.isMoving = false;
                }
                if (motionEvent.getPointerCount() == 2) {
                    if (photoFilterBlurControl.checkForZooming && !photoFilterBlurControl.isZooming) {
                        handlePinch(1, motionEvent2);
                        photoFilterBlurControl.isZooming = true;
                        break;
                    }
                }
                handlePinch(3, motionEvent2);
                photoFilterBlurControl.checkForZooming = true;
                photoFilterBlurControl.isZooming = false;
                break;
                break;
            case 1:
            case 3:
            case 6:
                if (photoFilterBlurControl.isMoving) {
                    handlePan(3, motionEvent2);
                    photoFilterBlurControl.isMoving = false;
                } else if (photoFilterBlurControl.isZooming) {
                    handlePinch(3, motionEvent2);
                    photoFilterBlurControl.isZooming = false;
                }
                photoFilterBlurControl.checkForMoving = true;
                photoFilterBlurControl.checkForZooming = true;
                break;
            case 2:
                if (!photoFilterBlurControl.isMoving) {
                    if (photoFilterBlurControl.isZooming) {
                        handlePinch(2, motionEvent2);
                        break;
                    }
                }
                handlePan(2, motionEvent2);
                break;
                break;
            default:
                break;
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handlePan(int i, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Point actualCenterPoint = getActualCenterPoint();
        Point point = new Point(x - actualCenterPoint.f24x, y - actualCenterPoint.f25y);
        float sqrt = (float) Math.sqrt((double) ((point.f24x * point.f24x) + (point.f25y * point.f25y)));
        float f = this.actualAreaSize.width > this.actualAreaSize.height ? r0.actualAreaSize.height : r0.actualAreaSize.width;
        float f2 = r0.falloff * f;
        float f3 = r0.size * f;
        float abs = (float) Math.abs((((double) point.f24x) * Math.cos(((double) degreesToRadians(r0.angle)) + 1.5707963267948966d)) + (((double) point.f25y) * Math.sin(((double) degreesToRadians(r0.angle)) + 1.5707963267948966d)));
        boolean z = false;
        float f4 = BlurInsetProximity;
        switch (i) {
            case 1:
                r0.pointerStartX = motionEvent.getX();
                r0.pointerStartY = motionEvent.getY();
                if (Math.abs(f3 - f2) < BlurInsetProximity) {
                    z = true;
                }
                x = z ? BlurInsetProximity : BlurViewRadiusInset;
                if (!z) {
                    f4 = BlurViewRadiusInset;
                }
                if (r0.type != 0) {
                    f = f2;
                    y = f3;
                    if (r0.type == 1) {
                        if (sqrt < BlurViewCenterInset) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                            r0.startCenterPoint = actualCenterPoint;
                        } else if (sqrt > f - BlurViewRadiusInset && sqrt < f + x) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            r0.startDistance = sqrt;
                            r0.startRadius = f;
                        } else if (sqrt > y - f4 && sqrt < y + BlurViewRadiusInset) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            r0.startDistance = sqrt;
                            r0.startRadius = y;
                        }
                    }
                } else if (sqrt < BlurViewCenterInset) {
                    r0.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                    r0.startCenterPoint = actualCenterPoint;
                } else if (abs <= f2 - BlurViewRadiusInset || abs >= f2 + x) {
                    f = f2;
                    if (abs <= f3 - f4 || abs >= f3 + BlurViewRadiusInset) {
                        y = f3;
                        if (abs <= f - BlurViewRadiusInset || abs >= y + BlurViewRadiusInset) {
                            r0.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                        }
                    } else {
                        r0.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                        r0.startDistance = abs;
                        r0.startRadius = f3;
                    }
                } else {
                    r0.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                    r0.startDistance = abs;
                    r0.startRadius = f2;
                }
                setSelected(true, true);
                return;
            case 2:
                Rect rect;
                if (r0.type != 0) {
                    if (r0.type == 1) {
                        switch (r0.activeControl) {
                            case BlurViewActiveControlCenter:
                                x -= r0.pointerStartX;
                                y -= r0.pointerStartY;
                                rect = new Rect((((float) getWidth()) - r0.actualAreaSize.width) / 2.0f, (((float) getHeight()) - r0.actualAreaSize.height) / 2.0f, r0.actualAreaSize.width, r0.actualAreaSize.height);
                                point = new Point(Math.max(rect.f26x, Math.min(rect.f26x + rect.width, r0.startCenterPoint.f24x + x)), Math.max(rect.f27y, Math.min(rect.f27y + rect.height, r0.startCenterPoint.f25y + y)));
                                r0.centerPoint = new Point((point.f24x - rect.f26x) / r0.actualAreaSize.width, ((point.f25y - rect.f27y) + ((r0.actualAreaSize.width - r0.actualAreaSize.height) / 2.0f)) / r0.actualAreaSize.width);
                                break;
                            case BlurViewActiveControlInnerRadius:
                                r0.falloff = Math.min(Math.max(0.1f, (r0.startRadius + (sqrt - r0.startDistance)) / f), r0.size - BlurMinimumDifference);
                                break;
                            case BlurViewActiveControlOuterRadius:
                                r0.size = Math.max(r0.falloff + BlurMinimumDifference, (r0.startRadius + (sqrt - r0.startDistance)) / f);
                                break;
                            default:
                                break;
                        }
                    }
                }
                switch (r0.activeControl) {
                    case BlurViewActiveControlCenter:
                        x -= r0.pointerStartX;
                        y -= r0.pointerStartY;
                        rect = new Rect((((float) getWidth()) - r0.actualAreaSize.width) / 2.0f, (((float) getHeight()) - r0.actualAreaSize.height) / 2.0f, r0.actualAreaSize.width, r0.actualAreaSize.height);
                        point = new Point(Math.max(rect.f26x, Math.min(rect.f26x + rect.width, r0.startCenterPoint.f24x + x)), Math.max(rect.f27y, Math.min(rect.f27y + rect.height, r0.startCenterPoint.f25y + y)));
                        r0.centerPoint = new Point((point.f24x - rect.f26x) / r0.actualAreaSize.width, ((point.f25y - rect.f27y) + ((r0.actualAreaSize.width - r0.actualAreaSize.height) / 2.0f)) / r0.actualAreaSize.width);
                        break;
                    case BlurViewActiveControlInnerRadius:
                        r0.falloff = Math.min(Math.max(0.1f, (r0.startRadius + (abs - r0.startDistance)) / f), r0.size - BlurMinimumDifference);
                        break;
                    case BlurViewActiveControlOuterRadius:
                        r0.size = Math.max(r0.falloff + BlurMinimumDifference, (r0.startRadius + (abs - r0.startDistance)) / f);
                        break;
                    case BlurViewActiveControlRotation:
                        abs = x - r0.pointerStartX;
                        sqrt = y - r0.pointerStartY;
                        boolean z2 = x > actualCenterPoint.f24x;
                        boolean z3 = y > actualCenterPoint.f25y;
                        if (z2 || z3) {
                            if (!z2 || z3) {
                                if (z2 && z3) {
                                    if (Math.abs(sqrt) > Math.abs(abs)) {
                                        if (sqrt > BlurInsetProximity) {
                                        }
                                        r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                                        r0.pointerStartX = x;
                                        r0.pointerStartY = y;
                                    } else {
                                        if (abs < BlurInsetProximity) {
                                        }
                                        r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                                        r0.pointerStartX = x;
                                        r0.pointerStartY = y;
                                    }
                                } else if (Math.abs(sqrt) > Math.abs(abs)) {
                                    if (sqrt < BlurInsetProximity) {
                                    }
                                    r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                                    r0.pointerStartX = x;
                                    r0.pointerStartY = y;
                                    break;
                                } else {
                                    if (abs < BlurInsetProximity) {
                                    }
                                    r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                                    r0.pointerStartX = x;
                                    r0.pointerStartY = y;
                                }
                            } else if (Math.abs(sqrt) > Math.abs(abs)) {
                                if (sqrt > BlurInsetProximity) {
                                }
                                r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                                r0.pointerStartX = x;
                                r0.pointerStartY = y;
                            } else {
                                if (abs > BlurInsetProximity) {
                                }
                                r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                                r0.pointerStartX = x;
                                r0.pointerStartY = y;
                            }
                        } else if (Math.abs(sqrt) > Math.abs(abs)) {
                            break;
                        } else {
                            if (abs > BlurInsetProximity) {
                            }
                            r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((r7 * 2) - 1))) / 3.1415927f) / 1.15f;
                            r0.pointerStartX = x;
                            r0.pointerStartY = y;
                        }
                        int i2 = 1;
                        r0.angle += ((((float) Math.sqrt((double) ((abs * abs) + (sqrt * sqrt)))) * ((float) ((i2 * 2) - 1))) / 3.1415927f) / 1.15f;
                        r0.pointerStartX = x;
                        r0.pointerStartY = y;
                        break;
                    default:
                        break;
                }
                invalidate();
                if (r0.delegate != null) {
                    r0.delegate.valueChanged(r0.centerPoint, r0.falloff, r0.size, degreesToRadians(r0.angle) + 1.5707964f);
                    return;
                }
                return;
            case 3:
            case 4:
            case 5:
                r0.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            default:
                return;
        }
    }

    private void handlePinch(int i, MotionEvent motionEvent) {
        switch (i) {
            case 1:
                this.startPointerDistance = getDistance(motionEvent);
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
                setSelected(0, true);
                return;
            default:
                return;
        }
        i = getDistance(motionEvent);
        this.pointerScale += ((i - this.startPointerDistance) / AndroidUtilities.density) * 0.01f;
        this.falloff = Math.max(0.1f, this.falloff * this.pointerScale);
        this.size = Math.max(this.falloff + BlurMinimumDifference, this.size * this.pointerScale);
        this.pointerScale = 1.0f;
        this.startPointerDistance = i;
        invalidate();
        if (this.delegate != 0) {
            this.delegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    public void setActualAreaSize(float f, float f2) {
        this.actualAreaSize.width = f;
        this.actualAreaSize.height = f2;
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        Point actualCenterPoint = getActualCenterPoint();
        float actualInnerRadius = getActualInnerRadius();
        float actualOuterRadius = getActualOuterRadius();
        canvas2.translate(actualCenterPoint.f24x, actualCenterPoint.f25y);
        if (this.type == 0) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            int i;
            Canvas canvas3;
            float f6;
            canvas2.rotate(r0.angle);
            float dp = (float) AndroidUtilities.dp(6.0f);
            float dp2 = (float) AndroidUtilities.dp(12.0f);
            float dp3 = (float) AndroidUtilities.dp(1.5f);
            int i2 = 0;
            while (i2 < 30) {
                f = dp2 + dp;
                f2 = ((float) i2) * f;
                f3 = -actualInnerRadius;
                f4 = f2 + dp2;
                f5 = dp3 - actualInnerRadius;
                float f7 = f3;
                f3 = f5;
                i = i2;
                canvas2.drawRect(f2, f3, f4, f3, r0.paint);
                f = (((float) (-i)) * f) - dp;
                float f8 = f - dp2;
                canvas3 = canvas2;
                canvas3.drawRect(f8, f7, f, f3, r0.paint);
                f6 = actualInnerRadius;
                f3 = dp3 + actualInnerRadius;
                canvas3.drawRect(f2, f6, f4, f3, r0.paint);
                canvas3.drawRect(f8, f6, f, f3, r0.paint);
                i2 = i + 1;
            }
            actualInnerRadius = (float) AndroidUtilities.dp(6.0f);
            for (i = 0; i < 64; i++) {
                float f9 = actualInnerRadius + dp;
                dp2 = ((float) i) * f9;
                float f10 = -actualOuterRadius;
                f = actualInnerRadius + dp2;
                f2 = dp3 - actualOuterRadius;
                f3 = f2;
                f5 = f10;
                canvas2.drawRect(dp2, f10, f, f3, r0.paint);
                f9 = (((float) (-i)) * f9) - dp;
                f4 = f9 - actualInnerRadius;
                canvas3 = canvas2;
                canvas3.drawRect(f4, f5, f9, f3, r0.paint);
                f6 = actualOuterRadius;
                f3 = dp3 + actualOuterRadius;
                canvas3.drawRect(dp2, f6, f, f3, r0.paint);
                canvas3.drawRect(f4, f6, f9, f3, r0.paint);
            }
        } else if (r0.type == 1) {
            int i3;
            float f11 = -actualInnerRadius;
            r0.arcRect.set(f11, f11, actualInnerRadius, actualInnerRadius);
            for (i3 = 0; i3 < 22; i3++) {
                canvas2.drawArc(r0.arcRect, 16.35f * ((float) i3), 10.2f, false, r0.arcPaint);
            }
            f11 = -actualOuterRadius;
            r0.arcRect.set(f11, f11, actualOuterRadius, actualOuterRadius);
            for (i3 = 0; i3 < 64; i3++) {
                canvas2.drawArc(r0.arcRect, 5.62f * ((float) i3), 3.6f, false, r0.arcPaint);
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
