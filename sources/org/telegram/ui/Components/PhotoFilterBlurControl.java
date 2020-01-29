package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
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
        setWillNotDraw(false);
        this.paint.setColor(-1);
        this.arcPaint.setColor(-1);
        this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.arcPaint.setStyle(Paint.Style.STROKE);
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
            return 0.0f;
        }
        float x = motionEvent.getX(0);
        float y = motionEvent.getY(0);
        float x2 = x - motionEvent.getX(1);
        float y2 = y - motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x2 * x2) + (y2 * y2)));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0018, code lost:
        if (r2 != 6) goto L_0x016d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            int r2 = r18.getActionMasked()
            r3 = 2
            r4 = 3
            r5 = 0
            r6 = 1
            if (r2 == 0) goto L_0x0047
            if (r2 == r6) goto L_0x002e
            if (r2 == r3) goto L_0x001c
            if (r2 == r4) goto L_0x002e
            r7 = 5
            if (r2 == r7) goto L_0x0047
            r3 = 6
            if (r2 == r3) goto L_0x002e
            goto L_0x016d
        L_0x001c:
            boolean r2 = r0.isMoving
            if (r2 == 0) goto L_0x0025
            r0.handlePan(r3, r1)
            goto L_0x016d
        L_0x0025:
            boolean r2 = r0.isZooming
            if (r2 == 0) goto L_0x016d
            r0.handlePinch(r3, r1)
            goto L_0x016d
        L_0x002e:
            boolean r2 = r0.isMoving
            if (r2 == 0) goto L_0x0038
            r0.handlePan(r4, r1)
            r0.isMoving = r5
            goto L_0x0041
        L_0x0038:
            boolean r2 = r0.isZooming
            if (r2 == 0) goto L_0x0041
            r0.handlePinch(r4, r1)
            r0.isZooming = r5
        L_0x0041:
            r0.checkForMoving = r6
            r0.checkForZooming = r6
            goto L_0x016d
        L_0x0047:
            int r2 = r18.getPointerCount()
            if (r2 != r6) goto L_0x0147
            boolean r2 = r0.checkForMoving
            if (r2 == 0) goto L_0x016d
            boolean r2 = r0.isMoving
            if (r2 != 0) goto L_0x016d
            float r2 = r18.getX()
            float r3 = r18.getY()
            org.telegram.ui.Components.Point r4 = r17.getActualCenterPoint()
            org.telegram.ui.Components.Point r7 = new org.telegram.ui.Components.Point
            float r8 = r4.x
            float r2 = r2 - r8
            float r4 = r4.y
            float r3 = r3 - r4
            r7.<init>(r2, r3)
            float r2 = r7.x
            float r2 = r2 * r2
            float r3 = r7.y
            float r3 = r3 * r3
            float r2 = r2 + r3
            double r2 = (double) r2
            double r2 = java.lang.Math.sqrt(r2)
            float r2 = (float) r2
            float r3 = r17.getActualInnerRadius()
            float r4 = r17.getActualOuterRadius()
            float r8 = r4 - r3
            float r8 = java.lang.Math.abs(r8)
            float r9 = BlurInsetProximity
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 >= 0) goto L_0x0091
            r8 = 1
            goto L_0x0092
        L_0x0091:
            r8 = 0
        L_0x0092:
            r9 = 0
            if (r8 == 0) goto L_0x0097
            r10 = 0
            goto L_0x0099
        L_0x0097:
            float r10 = BlurViewRadiusInset
        L_0x0099:
            if (r8 == 0) goto L_0x009c
            goto L_0x009e
        L_0x009c:
            float r9 = BlurViewRadiusInset
        L_0x009e:
            int r8 = r0.type
            if (r8 != 0) goto L_0x0113
            float r8 = r7.x
            double r11 = (double) r8
            float r8 = r0.angle
            float r8 = r0.degreesToRadians(r8)
            double r13 = (double) r8
            r15 = 4609753056924675352(0x3fvar_fb54442d18, double:1.NUM)
            java.lang.Double.isNaN(r13)
            double r13 = r13 + r15
            double r13 = java.lang.Math.cos(r13)
            java.lang.Double.isNaN(r11)
            double r11 = r11 * r13
            float r7 = r7.y
            double r7 = (double) r7
            float r13 = r0.angle
            float r13 = r0.degreesToRadians(r13)
            double r13 = (double) r13
            java.lang.Double.isNaN(r13)
            double r13 = r13 + r15
            double r13 = java.lang.Math.sin(r13)
            java.lang.Double.isNaN(r7)
            double r7 = r7 * r13
            double r11 = r11 + r7
            double r7 = java.lang.Math.abs(r11)
            float r7 = (float) r7
            float r8 = BlurViewCenterInset
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 >= 0) goto L_0x00e4
            r0.isMoving = r6
            goto L_0x013d
        L_0x00e4:
            float r2 = BlurViewRadiusInset
            float r2 = r3 - r2
            int r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x00f4
            float r10 = r10 + r3
            int r2 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r2 >= 0) goto L_0x00f4
            r0.isMoving = r6
            goto L_0x013d
        L_0x00f4:
            float r2 = r4 - r9
            int r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0104
            float r2 = BlurViewRadiusInset
            float r2 = r2 + r4
            int r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0104
            r0.isMoving = r6
            goto L_0x013d
        L_0x0104:
            float r2 = BlurViewRadiusInset
            float r3 = r3 - r2
            int r3 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0110
            float r4 = r4 + r2
            int r2 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r2 < 0) goto L_0x013d
        L_0x0110:
            r0.isMoving = r6
            goto L_0x013d
        L_0x0113:
            if (r8 != r6) goto L_0x013d
            float r7 = BlurViewCenterInset
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x011e
            r0.isMoving = r6
            goto L_0x013d
        L_0x011e:
            float r7 = BlurViewRadiusInset
            float r7 = r3 - r7
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x012e
            float r3 = r3 + r10
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x012e
            r0.isMoving = r6
            goto L_0x013d
        L_0x012e:
            float r3 = r4 - r9
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x013d
            float r3 = BlurViewRadiusInset
            float r4 = r4 + r3
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x013d
            r0.isMoving = r6
        L_0x013d:
            r0.checkForMoving = r5
            boolean r2 = r0.isMoving
            if (r2 == 0) goto L_0x016d
            r0.handlePan(r6, r1)
            goto L_0x016d
        L_0x0147:
            boolean r2 = r0.isMoving
            if (r2 == 0) goto L_0x0152
            r0.handlePan(r4, r1)
            r0.checkForMoving = r6
            r0.isMoving = r5
        L_0x0152:
            int r2 = r18.getPointerCount()
            if (r2 != r3) goto L_0x0166
            boolean r2 = r0.checkForZooming
            if (r2 == 0) goto L_0x016d
            boolean r2 = r0.isZooming
            if (r2 != 0) goto L_0x016d
            r0.handlePinch(r6, r1)
            r0.isZooming = r6
            goto L_0x016d
        L_0x0166:
            r0.handlePinch(r4, r1)
            r0.checkForZooming = r6
            r0.isZooming = r5
        L_0x016d:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterBlurControl.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void handlePan(int i, MotionEvent motionEvent) {
        float f;
        int i2 = i;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Point actualCenterPoint = getActualCenterPoint();
        Point point = new Point(x - actualCenterPoint.x, y - actualCenterPoint.y);
        float f2 = point.x;
        float f3 = point.y;
        float sqrt = (float) Math.sqrt((double) ((f2 * f2) + (f3 * f3)));
        Size size2 = this.actualAreaSize;
        float f4 = size2.width;
        float f5 = size2.height;
        if (f4 <= f5) {
            f5 = f4;
        }
        double d = (double) point.x;
        double degreesToRadians = (double) degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians);
        double cos = Math.cos(degreesToRadians + 1.5707963267948966d);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = (double) point.y;
        float f6 = this.falloff * f5;
        float f7 = this.size * f5;
        double degreesToRadians2 = (double) degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians2);
        double sin = Math.sin(degreesToRadians2 + 1.5707963267948966d);
        Double.isNaN(d3);
        float abs = (float) Math.abs(d2 + (d3 * sin));
        int i3 = 0;
        float f8 = 0.0f;
        if (i2 == 1) {
            this.pointerStartX = motionEvent.getX();
            this.pointerStartY = motionEvent.getY();
            if (Math.abs(f7 - f6) < BlurInsetProximity) {
                i3 = 1;
            }
            if (i3 != 0) {
                f = 0.0f;
            } else {
                f = BlurViewRadiusInset;
            }
            if (i3 == 0) {
                f8 = BlurViewRadiusInset;
            }
            int i4 = this.type;
            if (i4 != 0) {
                float f9 = f6;
                float var_ = f7;
                if (i4 == 1) {
                    if (sqrt < BlurViewCenterInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        this.startCenterPoint = actualCenterPoint;
                    } else if (sqrt > f9 - BlurViewRadiusInset && sqrt < f9 + f) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                        this.startDistance = sqrt;
                        this.startRadius = f9;
                    } else if (sqrt > var_ - f8 && sqrt < var_ + BlurViewRadiusInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                        this.startDistance = sqrt;
                        this.startRadius = var_;
                    }
                }
            } else if (sqrt < BlurViewCenterInset) {
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                this.startCenterPoint = actualCenterPoint;
            } else if (abs <= f6 - BlurViewRadiusInset || abs >= f6 + f) {
                float var_ = f6;
                if (abs <= f7 - f8 || abs >= f7 + BlurViewRadiusInset) {
                    float var_ = f7;
                    float var_ = BlurViewRadiusInset;
                    if (abs <= var_ - var_ || abs >= var_ + var_) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                    }
                } else {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                    this.startDistance = abs;
                    this.startRadius = f7;
                }
            } else {
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                this.startDistance = abs;
                this.startRadius = f6;
            }
            setSelected(true, true);
        } else if (i2 == 2) {
            int i5 = this.type;
            if (i5 == 0) {
                int i6 = AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()];
                if (i6 == 1) {
                    float var_ = x - this.pointerStartX;
                    float var_ = y - this.pointerStartY;
                    Size size3 = this.actualAreaSize;
                    float var_ = size3.height;
                    Rect rect = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, (((float) getHeight()) - var_) / 2.0f, size3.width, var_);
                    float var_ = rect.x;
                    float max = Math.max(var_, Math.min(rect.width + var_, this.startCenterPoint.x + var_));
                    float var_ = rect.y;
                    Point point2 = new Point(max, Math.max(var_, Math.min(rect.height + var_, this.startCenterPoint.y + var_)));
                    float var_ = point2.x - rect.x;
                    Size size4 = this.actualAreaSize;
                    float var_ = size4.width;
                    this.centerPoint = new Point(var_ / var_, ((point2.y - rect.y) + ((var_ - size4.height) / 2.0f)) / var_);
                } else if (i6 == 2) {
                    this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (abs - this.startDistance)) / f5), this.size - 0.02f);
                } else if (i6 == 3) {
                    this.size = Math.max(this.falloff + 0.02f, (this.startRadius + (abs - this.startDistance)) / f5);
                } else if (i6 == 4) {
                    float var_ = x - this.pointerStartX;
                    float var_ = y - this.pointerStartY;
                    boolean z = x > actualCenterPoint.x;
                    boolean z2 = y > actualCenterPoint.y;
                    if (z || z2 ? !(!z || z2 ? !z || !z2 ? Math.abs(var_) <= Math.abs(var_) ? var_ >= 0.0f : var_ >= 0.0f : Math.abs(var_) <= Math.abs(var_) ? var_ >= 0.0f : var_ <= 0.0f : Math.abs(var_) <= Math.abs(var_) ? var_ <= 0.0f : var_ <= 0.0f) : !(Math.abs(var_) <= Math.abs(var_) ? var_ <= 0.0f : var_ >= 0.0f)) {
                        i3 = 1;
                    }
                    this.angle += ((((float) Math.sqrt((double) ((var_ * var_) + (var_ * var_)))) * ((float) ((i3 * 2) - 1))) / 3.1415927f) / 1.15f;
                    this.pointerStartX = x;
                    this.pointerStartY = y;
                }
            } else if (i5 == 1) {
                int i7 = AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()];
                if (i7 == 1) {
                    float var_ = x - this.pointerStartX;
                    float var_ = y - this.pointerStartY;
                    Size size5 = this.actualAreaSize;
                    float var_ = size5.height;
                    Rect rect2 = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, (((float) getHeight()) - var_) / 2.0f, size5.width, var_);
                    float var_ = rect2.x;
                    float max2 = Math.max(var_, Math.min(rect2.width + var_, this.startCenterPoint.x + var_));
                    float var_ = rect2.y;
                    Point point3 = new Point(max2, Math.max(var_, Math.min(rect2.height + var_, this.startCenterPoint.y + var_)));
                    float var_ = point3.x - rect2.x;
                    Size size6 = this.actualAreaSize;
                    float var_ = size6.width;
                    this.centerPoint = new Point(var_ / var_, ((point3.y - rect2.y) + ((var_ - size6.height) / 2.0f)) / var_);
                } else if (i7 == 2) {
                    this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (sqrt - this.startDistance)) / f5), this.size - 0.02f);
                } else if (i7 == 3) {
                    this.size = Math.max(this.falloff + 0.02f, (this.startRadius + (sqrt - this.startDistance)) / f5);
                }
            }
            invalidate();
            PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
            if (photoFilterLinearBlurControlDelegate != null) {
                photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
            }
        } else if (i2 == 3 || i2 == 4 || i2 == 5) {
            this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
            setSelected(false, true);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterBlurControl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl = new int[BlurViewActiveControl.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        static {
            /*
                org.telegram.ui.Components.PhotoFilterBlurControl$BlurViewActiveControl[] r0 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl = r0
                int[] r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl     // Catch:{ NoSuchFieldError -> 0x0014 }
                org.telegram.ui.Components.PhotoFilterBlurControl$BlurViewActiveControl r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlCenter     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl     // Catch:{ NoSuchFieldError -> 0x001f }
                org.telegram.ui.Components.PhotoFilterBlurControl$BlurViewActiveControl r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlInnerRadius     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl     // Catch:{ NoSuchFieldError -> 0x002a }
                org.telegram.ui.Components.PhotoFilterBlurControl$BlurViewActiveControl r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlOuterRadius     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl     // Catch:{ NoSuchFieldError -> 0x0035 }
                org.telegram.ui.Components.PhotoFilterBlurControl$BlurViewActiveControl r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlRotation     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterBlurControl.AnonymousClass1.<clinit>():void");
        }
    }

    private void handlePinch(int i, MotionEvent motionEvent) {
        if (i == 1) {
            this.startPointerDistance = getDistance(motionEvent);
            this.pointerScale = 1.0f;
            this.activeControl = BlurViewActiveControl.BlurViewActiveControlWholeArea;
            setSelected(true, true);
        } else if (i != 2) {
            if (i == 3 || i == 4 || i == 5) {
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            }
            return;
        }
        float distance = getDistance(motionEvent);
        this.pointerScale += ((distance - this.startPointerDistance) / AndroidUtilities.density) * 0.01f;
        this.falloff = Math.max(0.1f, this.falloff * this.pointerScale);
        this.size = Math.max(this.falloff + 0.02f, this.size * this.pointerScale);
        this.pointerScale = 1.0f;
        this.startPointerDistance = distance;
        invalidate();
        PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
        if (photoFilterLinearBlurControlDelegate != null) {
            photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    public void setActualAreaSize(float f, float f2) {
        Size size2 = this.actualAreaSize;
        size2.width = f;
        size2.height = f2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        Point actualCenterPoint = getActualCenterPoint();
        float actualInnerRadius = getActualInnerRadius();
        float actualOuterRadius = getActualOuterRadius();
        canvas2.translate(actualCenterPoint.x, actualCenterPoint.y);
        int i = this.type;
        if (i == 0) {
            canvas2.rotate(this.angle);
            float dp = (float) AndroidUtilities.dp(6.0f);
            float dp2 = (float) AndroidUtilities.dp(12.0f);
            float dp3 = (float) AndroidUtilities.dp(1.5f);
            int i2 = 0;
            while (i2 < 30) {
                float f = dp2 + dp;
                float f2 = ((float) i2) * f;
                float f3 = -actualInnerRadius;
                float f4 = f2 + dp2;
                float f5 = f3;
                float f6 = dp3 - actualInnerRadius;
                int i3 = i2;
                canvas.drawRect(f2, f3, f4, f6, this.paint);
                float f7 = (((float) (-i3)) * f) - dp;
                float f8 = f7 - dp2;
                Canvas canvas3 = canvas;
                canvas3.drawRect(f8, f5, f7, f6, this.paint);
                float f9 = actualInnerRadius;
                float var_ = dp3 + actualInnerRadius;
                canvas3.drawRect(f2, f9, f4, var_, this.paint);
                canvas3.drawRect(f8, f9, f7, var_, this.paint);
                i2 = i3 + 1;
            }
            float dp4 = (float) AndroidUtilities.dp(6.0f);
            for (int i4 = 0; i4 < 64; i4++) {
                float var_ = dp4 + dp;
                float var_ = ((float) i4) * var_;
                float var_ = -actualOuterRadius;
                float var_ = dp4 + var_;
                float var_ = dp3 - actualOuterRadius;
                canvas.drawRect(var_, var_, var_, var_, this.paint);
                float var_ = (((float) (-i4)) * var_) - dp;
                float var_ = var_ - dp4;
                Canvas canvas4 = canvas;
                canvas4.drawRect(var_, var_, var_, var_, this.paint);
                float var_ = actualOuterRadius;
                float var_ = dp3 + actualOuterRadius;
                canvas4.drawRect(var_, var_, var_, var_, this.paint);
                canvas4.drawRect(var_, var_, var_, var_, this.paint);
            }
        } else if (i == 1) {
            float var_ = -actualInnerRadius;
            this.arcRect.set(var_, var_, actualInnerRadius, actualInnerRadius);
            for (int i5 = 0; i5 < 22; i5++) {
                canvas.drawArc(this.arcRect, 16.35f * ((float) i5), 10.2f, false, this.arcPaint);
            }
            float var_ = -actualOuterRadius;
            this.arcRect.set(var_, var_, actualOuterRadius, actualOuterRadius);
            for (int i6 = 0; i6 < 64; i6++) {
                canvas.drawArc(this.arcRect, 5.62f * ((float) i6), 3.6f, false, this.arcPaint);
            }
        }
        canvas2.drawCircle(0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), this.paint);
    }

    private Point getActualCenterPoint() {
        float f = this.actualAreaSize.width;
        float width = ((((float) getWidth()) - f) / 2.0f) + (this.centerPoint.x * f);
        int i = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        Size size2 = this.actualAreaSize;
        float f2 = size2.height;
        float height = ((float) i) + ((((float) getHeight()) - f2) / 2.0f);
        float f3 = size2.width;
        return new Point(width, (height - ((f3 - f2) / 2.0f)) + (this.centerPoint.y * f3));
    }

    private float getActualInnerRadius() {
        Size size2 = this.actualAreaSize;
        float f = size2.width;
        float f2 = size2.height;
        if (f <= f2) {
            f2 = f;
        }
        return f2 * this.falloff;
    }

    private float getActualOuterRadius() {
        Size size2 = this.actualAreaSize;
        float f = size2.width;
        float f2 = size2.height;
        if (f <= f2) {
            f2 = f;
        }
        return f2 * this.size;
    }
}
