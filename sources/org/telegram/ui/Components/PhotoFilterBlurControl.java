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

    /* renamed from: org.telegram.ui.Components.PhotoFilterBlurControl$1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl = new int[BlurViewActiveControl.values().length];

        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        static {
            /*
            r0 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.values();
            r0 = r0.length;
            r0 = new int[r0];
            $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl = r0;
            r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlCenter;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0014 }
            r2 = 1;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0014 }
        L_0x0014:
            r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlInnerRadius;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x001f }
            r2 = 2;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x001f }
        L_0x001f:
            r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlOuterRadius;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x002a }
            r2 = 3;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x002a }
        L_0x002a:
            r0 = $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = org.telegram.ui.Components.PhotoFilterBlurControl.BlurViewActiveControl.BlurViewActiveControlRotation;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0035 }
            r2 = 4;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0035 }
        L_0x0035:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterBlurControl$AnonymousClass1.<clinit>():void");
        }
    }

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
            return 0.0f;
        }
        float x = motionEvent.getX(0);
        float y = motionEvent.getY(0);
        x -= motionEvent.getX(1);
        y -= motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    /* JADX WARNING: Missing block: B:8:0x0018, code skipped:
            if (r2 != 6) goto L_0x016d;
     */
    public boolean onTouchEvent(android.view.MotionEvent r18) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = r18.getActionMasked();
        r3 = 2;
        r4 = 3;
        r5 = 0;
        r6 = 1;
        if (r2 == 0) goto L_0x0047;
    L_0x000e:
        if (r2 == r6) goto L_0x002e;
    L_0x0010:
        if (r2 == r3) goto L_0x001c;
    L_0x0012:
        if (r2 == r4) goto L_0x002e;
    L_0x0014:
        r7 = 5;
        if (r2 == r7) goto L_0x0047;
    L_0x0017:
        r3 = 6;
        if (r2 == r3) goto L_0x002e;
    L_0x001a:
        goto L_0x016d;
    L_0x001c:
        r2 = r0.isMoving;
        if (r2 == 0) goto L_0x0025;
    L_0x0020:
        r0.handlePan(r3, r1);
        goto L_0x016d;
    L_0x0025:
        r2 = r0.isZooming;
        if (r2 == 0) goto L_0x016d;
    L_0x0029:
        r0.handlePinch(r3, r1);
        goto L_0x016d;
    L_0x002e:
        r2 = r0.isMoving;
        if (r2 == 0) goto L_0x0038;
    L_0x0032:
        r0.handlePan(r4, r1);
        r0.isMoving = r5;
        goto L_0x0041;
    L_0x0038:
        r2 = r0.isZooming;
        if (r2 == 0) goto L_0x0041;
    L_0x003c:
        r0.handlePinch(r4, r1);
        r0.isZooming = r5;
    L_0x0041:
        r0.checkForMoving = r6;
        r0.checkForZooming = r6;
        goto L_0x016d;
    L_0x0047:
        r2 = r18.getPointerCount();
        if (r2 != r6) goto L_0x0147;
    L_0x004d:
        r2 = r0.checkForMoving;
        if (r2 == 0) goto L_0x016d;
    L_0x0051:
        r2 = r0.isMoving;
        if (r2 != 0) goto L_0x016d;
    L_0x0055:
        r2 = r18.getX();
        r3 = r18.getY();
        r4 = r17.getActualCenterPoint();
        r7 = new org.telegram.ui.Components.Point;
        r8 = r4.x;
        r2 = r2 - r8;
        r4 = r4.y;
        r3 = r3 - r4;
        r7.<init>(r2, r3);
        r2 = r7.x;
        r2 = r2 * r2;
        r3 = r7.y;
        r3 = r3 * r3;
        r2 = r2 + r3;
        r2 = (double) r2;
        r2 = java.lang.Math.sqrt(r2);
        r2 = (float) r2;
        r3 = r17.getActualInnerRadius();
        r4 = r17.getActualOuterRadius();
        r8 = r4 - r3;
        r8 = java.lang.Math.abs(r8);
        r9 = BlurInsetProximity;
        r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1));
        if (r8 >= 0) goto L_0x0091;
    L_0x008f:
        r8 = 1;
        goto L_0x0092;
    L_0x0091:
        r8 = 0;
    L_0x0092:
        r9 = 0;
        if (r8 == 0) goto L_0x0097;
    L_0x0095:
        r10 = 0;
        goto L_0x0099;
    L_0x0097:
        r10 = BlurViewRadiusInset;
    L_0x0099:
        if (r8 == 0) goto L_0x009c;
    L_0x009b:
        goto L_0x009e;
    L_0x009c:
        r9 = BlurViewRadiusInset;
    L_0x009e:
        r8 = r0.type;
        if (r8 != 0) goto L_0x0113;
    L_0x00a2:
        r8 = r7.x;
        r11 = (double) r8;
        r8 = r0.angle;
        r8 = r0.degreesToRadians(r8);
        r13 = (double) r8;
        r15 = NUM; // 0x3fvar_fb54442d18 float:3.37028055E12 double:1.NUM;
        java.lang.Double.isNaN(r13);
        r13 = r13 + r15;
        r13 = java.lang.Math.cos(r13);
        java.lang.Double.isNaN(r11);
        r11 = r11 * r13;
        r7 = r7.y;
        r7 = (double) r7;
        r13 = r0.angle;
        r13 = r0.degreesToRadians(r13);
        r13 = (double) r13;
        java.lang.Double.isNaN(r13);
        r13 = r13 + r15;
        r13 = java.lang.Math.sin(r13);
        java.lang.Double.isNaN(r7);
        r7 = r7 * r13;
        r11 = r11 + r7;
        r7 = java.lang.Math.abs(r11);
        r7 = (float) r7;
        r8 = BlurViewCenterInset;
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 >= 0) goto L_0x00e4;
    L_0x00e1:
        r0.isMoving = r6;
        goto L_0x013d;
    L_0x00e4:
        r2 = BlurViewRadiusInset;
        r2 = r3 - r2;
        r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r2 <= 0) goto L_0x00f4;
    L_0x00ec:
        r10 = r10 + r3;
        r2 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r2 >= 0) goto L_0x00f4;
    L_0x00f1:
        r0.isMoving = r6;
        goto L_0x013d;
    L_0x00f4:
        r2 = r4 - r9;
        r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r2 <= 0) goto L_0x0104;
    L_0x00fa:
        r2 = BlurViewRadiusInset;
        r2 = r2 + r4;
        r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r2 >= 0) goto L_0x0104;
    L_0x0101:
        r0.isMoving = r6;
        goto L_0x013d;
    L_0x0104:
        r2 = BlurViewRadiusInset;
        r3 = r3 - r2;
        r3 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));
        if (r3 <= 0) goto L_0x0110;
    L_0x010b:
        r4 = r4 + r2;
        r2 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1));
        if (r2 < 0) goto L_0x013d;
    L_0x0110:
        r0.isMoving = r6;
        goto L_0x013d;
    L_0x0113:
        if (r8 != r6) goto L_0x013d;
    L_0x0115:
        r7 = BlurViewCenterInset;
        r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r7 >= 0) goto L_0x011e;
    L_0x011b:
        r0.isMoving = r6;
        goto L_0x013d;
    L_0x011e:
        r7 = BlurViewRadiusInset;
        r7 = r3 - r7;
        r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r7 <= 0) goto L_0x012e;
    L_0x0126:
        r3 = r3 + r10;
        r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r3 >= 0) goto L_0x012e;
    L_0x012b:
        r0.isMoving = r6;
        goto L_0x013d;
    L_0x012e:
        r3 = r4 - r9;
        r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r3 <= 0) goto L_0x013d;
    L_0x0134:
        r3 = BlurViewRadiusInset;
        r4 = r4 + r3;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 >= 0) goto L_0x013d;
    L_0x013b:
        r0.isMoving = r6;
    L_0x013d:
        r0.checkForMoving = r5;
        r2 = r0.isMoving;
        if (r2 == 0) goto L_0x016d;
    L_0x0143:
        r0.handlePan(r6, r1);
        goto L_0x016d;
    L_0x0147:
        r2 = r0.isMoving;
        if (r2 == 0) goto L_0x0152;
    L_0x014b:
        r0.handlePan(r4, r1);
        r0.checkForMoving = r6;
        r0.isMoving = r5;
    L_0x0152:
        r2 = r18.getPointerCount();
        if (r2 != r3) goto L_0x0166;
    L_0x0158:
        r2 = r0.checkForZooming;
        if (r2 == 0) goto L_0x016d;
    L_0x015c:
        r2 = r0.isZooming;
        if (r2 != 0) goto L_0x016d;
    L_0x0160:
        r0.handlePinch(r6, r1);
        r0.isZooming = r6;
        goto L_0x016d;
    L_0x0166:
        r0.handlePinch(r4, r1);
        r0.checkForZooming = r6;
        r0.isZooming = r5;
    L_0x016d:
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterBlurControl.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void handlePan(int i, MotionEvent motionEvent) {
        int i2 = i;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Point actualCenterPoint = getActualCenterPoint();
        Point point = new Point(x - actualCenterPoint.x, y - actualCenterPoint.y);
        float f = point.x;
        f *= f;
        float f2 = point.y;
        f = (float) Math.sqrt((double) (f + (f2 * f2)));
        Size size = this.actualAreaSize;
        float f3 = size.width;
        f2 = size.height;
        if (f3 <= f2) {
            f2 = f3;
        }
        f3 = this.falloff * f2;
        float f4 = this.size * f2;
        double d = (double) point.x;
        double degreesToRadians = (double) degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians);
        degreesToRadians = Math.cos(degreesToRadians + 1.5707963267948966d);
        Double.isNaN(d);
        d *= degreesToRadians;
        degreesToRadians = (double) point.y;
        float f5 = f3;
        float f6 = f4;
        double degreesToRadians2 = (double) degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians2);
        degreesToRadians2 = Math.sin(degreesToRadians2 + 1.5707963267948966d);
        Double.isNaN(degreesToRadians);
        float abs = (float) Math.abs(d + (degreesToRadians * degreesToRadians2));
        boolean z = false;
        f4 = 0.0f;
        float f7;
        if (i2 == 1) {
            this.pointerStartX = motionEvent.getX();
            this.pointerStartY = motionEvent.getY();
            if (Math.abs(f6 - f5) < BlurInsetProximity) {
                z = true;
            }
            if (z) {
                f7 = 0.0f;
            } else {
                f7 = BlurViewRadiusInset;
            }
            if (!z) {
                f4 = BlurViewRadiusInset;
            }
            int i3 = this.type;
            if (i3 != 0) {
                f2 = f5;
                y = f6;
                if (i3 == 1) {
                    if (f < BlurViewCenterInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        this.startCenterPoint = actualCenterPoint;
                    } else if (f > f2 - BlurViewRadiusInset && f < f2 + f7) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                        this.startDistance = f;
                        this.startRadius = f2;
                    } else if (f > y - f4 && f < y + BlurViewRadiusInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                        this.startDistance = f;
                        this.startRadius = y;
                    }
                }
            } else if (f < BlurViewCenterInset) {
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                this.startCenterPoint = actualCenterPoint;
            } else if (abs <= f5 - BlurViewRadiusInset || abs >= f5 + f7) {
                f2 = f5;
                if (abs <= f6 - f4 || abs >= f6 + BlurViewRadiusInset) {
                    y = f6;
                    f7 = BlurViewRadiusInset;
                    if (abs <= f2 - f7 || abs >= y + f7) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                    }
                } else {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                    this.startDistance = abs;
                    this.startRadius = f6;
                }
            } else {
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                this.startDistance = abs;
                this.startRadius = f5;
            }
            setSelected(true, true);
        } else if (i2 == 2) {
            i2 = this.type;
            float width;
            Size size2;
            Rect rect;
            Size size3;
            if (i2 == 0) {
                i2 = AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()];
                if (i2 == 1) {
                    x -= this.pointerStartX;
                    y -= this.pointerStartY;
                    width = (((float) getWidth()) - this.actualAreaSize.width) / 2.0f;
                    abs = (float) getHeight();
                    size2 = this.actualAreaSize;
                    f2 = size2.height;
                    rect = new Rect(width, (abs - f2) / 2.0f, size2.width, f2);
                    abs = rect.x;
                    x = Math.max(abs, Math.min(rect.width + abs, this.startCenterPoint.x + x));
                    abs = rect.y;
                    actualCenterPoint = new Point(x, Math.max(abs, Math.min(rect.height + abs, this.startCenterPoint.y + y)));
                    y = actualCenterPoint.x - rect.x;
                    size3 = this.actualAreaSize;
                    f = size3.width;
                    this.centerPoint = new Point(y / f, ((actualCenterPoint.y - rect.y) + ((f - size3.height) / 2.0f)) / f);
                } else if (i2 == 2) {
                    this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (abs - this.startDistance)) / f2), this.size - 0.02f);
                } else if (i2 == 3) {
                    this.size = Math.max(this.falloff + 0.02f, (this.startRadius + (abs - this.startDistance)) / f2);
                } else if (i2 == 4) {
                    int i4;
                    f7 = x - this.pointerStartX;
                    abs = y - this.pointerStartY;
                    Object obj = x > actualCenterPoint.x ? 1 : null;
                    Object obj2 = y > actualCenterPoint.y ? 1 : null;
                    if (obj == null && obj2 == null ? Math.abs(abs) <= Math.abs(f7) ? f7 <= 0.0f : abs >= 0.0f : obj == null || obj2 != null ? obj == null || obj2 == null ? Math.abs(abs) <= Math.abs(f7) ? f7 >= 0.0f : abs >= 0.0f : Math.abs(abs) <= Math.abs(f7) ? f7 >= 0.0f : abs <= 0.0f : Math.abs(abs) <= Math.abs(f7) ? f7 <= 0.0f : abs <= 0.0f) {
                        i4 = 1;
                    }
                    this.angle += ((((float) Math.sqrt((double) ((f7 * f7) + (abs * abs)))) * ((float) ((i4 * 2) - 1))) / 3.1415927f) / 1.15f;
                    this.pointerStartX = x;
                    this.pointerStartY = y;
                }
            } else if (i2 == 1) {
                i2 = AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()];
                if (i2 == 1) {
                    x -= this.pointerStartX;
                    y -= this.pointerStartY;
                    width = (((float) getWidth()) - this.actualAreaSize.width) / 2.0f;
                    abs = (float) getHeight();
                    size2 = this.actualAreaSize;
                    f2 = size2.height;
                    rect = new Rect(width, (abs - f2) / 2.0f, size2.width, f2);
                    abs = rect.x;
                    x = Math.max(abs, Math.min(rect.width + abs, this.startCenterPoint.x + x));
                    abs = rect.y;
                    actualCenterPoint = new Point(x, Math.max(abs, Math.min(rect.height + abs, this.startCenterPoint.y + y)));
                    y = actualCenterPoint.x - rect.x;
                    size3 = this.actualAreaSize;
                    f = size3.width;
                    this.centerPoint = new Point(y / f, ((actualCenterPoint.y - rect.y) + ((f - size3.height) / 2.0f)) / f);
                } else if (i2 == 2) {
                    this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (f - this.startDistance)) / f2), this.size - 0.02f);
                } else if (i2 == 3) {
                    this.size = Math.max(this.falloff + 0.02f, (this.startRadius + (f - this.startDistance)) / f2);
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
        Size size = this.actualAreaSize;
        size.width = f;
        size.height = f2;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        Point actualCenterPoint = getActualCenterPoint();
        float actualInnerRadius = getActualInnerRadius();
        float actualOuterRadius = getActualOuterRadius();
        canvas2.translate(actualCenterPoint.x, actualCenterPoint.y);
        int i = this.type;
        if (i == 0) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            int i2;
            Canvas canvas3;
            float f6;
            canvas2.rotate(this.angle);
            float dp = (float) AndroidUtilities.dp(6.0f);
            float dp2 = (float) AndroidUtilities.dp(12.0f);
            float dp3 = (float) AndroidUtilities.dp(1.5f);
            int i3 = 0;
            while (i3 < 30) {
                f = dp2 + dp;
                f2 = ((float) i3) * f;
                f3 = -actualInnerRadius;
                f4 = f2 + dp2;
                f5 = dp3 - actualInnerRadius;
                float f7 = f3;
                f3 = f5;
                i2 = i3;
                canvas.drawRect(f2, f3, f4, f3, this.paint);
                f = (((float) (-i2)) * f) - dp;
                float f8 = f - dp2;
                canvas3 = canvas;
                canvas3.drawRect(f8, f7, f, f3, this.paint);
                f6 = actualInnerRadius;
                f3 = dp3 + actualInnerRadius;
                canvas3.drawRect(f2, f6, f4, f3, this.paint);
                canvas3.drawRect(f8, f6, f, f3, this.paint);
                i3 = i2 + 1;
            }
            actualInnerRadius = (float) AndroidUtilities.dp(6.0f);
            for (i2 = 0; i2 < 64; i2++) {
                float f9 = actualInnerRadius + dp;
                dp2 = ((float) i2) * f9;
                float var_ = -actualOuterRadius;
                f = actualInnerRadius + dp2;
                f2 = dp3 - actualOuterRadius;
                f3 = f2;
                f5 = var_;
                canvas.drawRect(dp2, var_, f, f3, this.paint);
                f9 = (((float) (-i2)) * f9) - dp;
                f4 = f9 - actualInnerRadius;
                canvas3 = canvas;
                canvas3.drawRect(f4, f5, f9, f3, this.paint);
                f6 = actualOuterRadius;
                f3 = dp3 + actualOuterRadius;
                canvas3.drawRect(dp2, f6, f, f3, this.paint);
                canvas3.drawRect(f4, f6, f9, f3, this.paint);
            }
        } else if (i == 1) {
            int i4;
            float var_ = -actualInnerRadius;
            this.arcRect.set(var_, var_, actualInnerRadius, actualInnerRadius);
            for (i4 = 0; i4 < 22; i4++) {
                canvas.drawArc(this.arcRect, 16.35f * ((float) i4), 10.2f, false, this.arcPaint);
            }
            var_ = -actualOuterRadius;
            this.arcRect.set(var_, var_, actualOuterRadius, actualOuterRadius);
            for (i4 = 0; i4 < 64; i4++) {
                canvas.drawArc(this.arcRect, 5.62f * ((float) i4), 3.6f, false, this.arcPaint);
            }
        }
        canvas2.drawCircle(0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), this.paint);
    }

    private Point getActualCenterPoint() {
        float width = (float) getWidth();
        float f = this.actualAreaSize.width;
        width = ((width - f) / 2.0f) + (this.centerPoint.x * f);
        f = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float height = (float) getHeight();
        Size size = this.actualAreaSize;
        float f2 = size.height;
        f += (height - f2) / 2.0f;
        height = size.width;
        return new Point(width, (f - ((height - f2) / 2.0f)) + (this.centerPoint.y * height));
    }

    private float getActualInnerRadius() {
        Size size = this.actualAreaSize;
        float f = size.width;
        float f2 = size.height;
        if (f <= f2) {
            f2 = f;
        }
        return f2 * this.falloff;
    }

    private float getActualOuterRadius() {
        Size size = this.actualAreaSize;
        float f = size.width;
        float f2 = size.height;
        if (f <= f2) {
            f2 = f;
        }
        return f2 * this.size;
    }
}
