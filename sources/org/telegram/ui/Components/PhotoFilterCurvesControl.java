package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.PhotoFilterView.CurvesToolValue;
import org.telegram.ui.Components.PhotoFilterView.CurvesValue;

public class PhotoFilterCurvesControl extends View {
    private static final int CurvesSegmentBlacks = 1;
    private static final int CurvesSegmentHighlights = 4;
    private static final int CurvesSegmentMidtones = 3;
    private static final int CurvesSegmentNone = 0;
    private static final int CurvesSegmentShadows = 2;
    private static final int CurvesSegmentWhites = 5;
    private static final int GestureStateBegan = 1;
    private static final int GestureStateCancelled = 4;
    private static final int GestureStateChanged = 2;
    private static final int GestureStateEnded = 3;
    private static final int GestureStateFailed = 5;
    private int activeSegment = 0;
    private Rect actualArea = new Rect();
    private boolean checkForMoving = true;
    private CurvesToolValue curveValue;
    private PhotoFilterCurvesControlDelegate delegate;
    private boolean isMoving;
    private float lastX;
    private float lastY;
    private Paint paint = new Paint(1);
    private Paint paintCurve = new Paint(1);
    private Paint paintDash = new Paint(1);
    private Path path = new Path();
    private TextPaint textPaint = new TextPaint(1);

    public interface PhotoFilterCurvesControlDelegate {
        void valueChanged();
    }

    public PhotoFilterCurvesControl(Context context, CurvesToolValue curvesToolValue) {
        super(context);
        setWillNotDraw(false);
        this.curveValue = curvesToolValue;
        this.paint.setColor(-NUM);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.paint.setStyle(Style.STROKE);
        this.paintDash.setColor(-NUM);
        this.paintDash.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintDash.setStyle(Style.STROKE);
        this.paintCurve.setColor(-1);
        this.paintCurve.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintCurve.setStyle(Style.STROKE);
        this.textPaint.setColor(-4210753);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    public void setDelegate(PhotoFilterCurvesControlDelegate photoFilterCurvesControlDelegate) {
        this.delegate = photoFilterCurvesControlDelegate;
    }

    public void setActualArea(float f, float f2, float f3, float f4) {
        Rect rect = this.actualArea;
        rect.x = f;
        rect.y = f2;
        rect.width = f3;
        rect.height = f4;
    }

    /* JADX WARNING: Missing block: B:9:0x0014, code skipped:
            if (r0 != 6) goto L_0x0078;
     */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r8.getActionMasked();
        r1 = 0;
        r2 = 3;
        r3 = 1;
        if (r0 == 0) goto L_0x002b;
    L_0x0009:
        if (r0 == r3) goto L_0x001f;
    L_0x000b:
        r4 = 2;
        if (r0 == r4) goto L_0x0017;
    L_0x000e:
        if (r0 == r2) goto L_0x001f;
    L_0x0010:
        r4 = 5;
        if (r0 == r4) goto L_0x002b;
    L_0x0013:
        r4 = 6;
        if (r0 == r4) goto L_0x001f;
    L_0x0016:
        goto L_0x0078;
    L_0x0017:
        r0 = r7.isMoving;
        if (r0 == 0) goto L_0x0078;
    L_0x001b:
        r7.handlePan(r4, r8);
        goto L_0x0078;
    L_0x001f:
        r0 = r7.isMoving;
        if (r0 == 0) goto L_0x0028;
    L_0x0023:
        r7.handlePan(r2, r8);
        r7.isMoving = r1;
    L_0x0028:
        r7.checkForMoving = r3;
        goto L_0x0078;
    L_0x002b:
        r0 = r8.getPointerCount();
        if (r0 != r3) goto L_0x006d;
    L_0x0031:
        r0 = r7.checkForMoving;
        if (r0 == 0) goto L_0x0078;
    L_0x0035:
        r0 = r7.isMoving;
        if (r0 != 0) goto L_0x0078;
    L_0x0039:
        r0 = r8.getX();
        r2 = r8.getY();
        r7.lastX = r0;
        r7.lastY = r2;
        r4 = r7.actualArea;
        r5 = r4.x;
        r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r6 < 0) goto L_0x0063;
    L_0x004d:
        r6 = r4.width;
        r5 = r5 + r6;
        r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r0 > 0) goto L_0x0063;
    L_0x0054:
        r0 = r4.y;
        r5 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r5 < 0) goto L_0x0063;
    L_0x005a:
        r4 = r4.height;
        r0 = r0 + r4;
        r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x0063;
    L_0x0061:
        r7.isMoving = r3;
    L_0x0063:
        r7.checkForMoving = r1;
        r0 = r7.isMoving;
        if (r0 == 0) goto L_0x0078;
    L_0x0069:
        r7.handlePan(r3, r8);
        goto L_0x0078;
    L_0x006d:
        r0 = r7.isMoving;
        if (r0 == 0) goto L_0x0078;
    L_0x0071:
        r7.handlePan(r2, r8);
        r7.checkForMoving = r3;
        r7.isMoving = r1;
    L_0x0078:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterCurvesControl.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void handlePan(int i, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (i == 1) {
            selectSegmentWithPoint(x);
        } else if (i == 2) {
            float min = Math.min(2.0f, (this.lastY - y) / 8.0f);
            CurvesValue curvesValue = null;
            CurvesToolValue curvesToolValue = this.curveValue;
            int i2 = curvesToolValue.activeType;
            if (i2 == 0) {
                curvesValue = curvesToolValue.luminanceCurve;
            } else if (i2 == 1) {
                curvesValue = curvesToolValue.redCurve;
            } else if (i2 == 2) {
                curvesValue = curvesToolValue.greenCurve;
            } else if (i2 == 3) {
                curvesValue = curvesToolValue.blueCurve;
            }
            int i3 = this.activeSegment;
            if (i3 == 1) {
                curvesValue.blacksLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.blacksLevel + min));
            } else if (i3 == 2) {
                curvesValue.shadowsLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.shadowsLevel + min));
            } else if (i3 == 3) {
                curvesValue.midtonesLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.midtonesLevel + min));
            } else if (i3 == 4) {
                curvesValue.highlightsLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.highlightsLevel + min));
            } else if (i3 == 5) {
                curvesValue.whitesLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.whitesLevel + min));
            }
            invalidate();
            PhotoFilterCurvesControlDelegate photoFilterCurvesControlDelegate = this.delegate;
            if (photoFilterCurvesControlDelegate != null) {
                photoFilterCurvesControlDelegate.valueChanged();
            }
            this.lastX = x;
            this.lastY = y;
        } else if (i == 3 || i == 4 || i == 5) {
            unselectSegments();
        }
    }

    private void selectSegmentWithPoint(float f) {
        if (this.activeSegment == 0) {
            Rect rect = this.actualArea;
            this.activeSegment = (int) Math.floor((double) (((f - rect.x) / (rect.width / 5.0f)) + 1.0f));
        }
    }

    private void unselectSegments() {
        if (this.activeSegment != 0) {
            this.activeSegment = 0;
        }
    }

    /* Access modifiers changed, original: protected */
    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        float f3 = this.actualArea.width / 5.0f;
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            Rect rect = this.actualArea;
            float f4 = rect.x;
            float f5 = ((float) i2) * f3;
            f = (f4 + f3) + f5;
            f2 = rect.y;
            canvas.drawLine(f, f2, (f4 + f3) + f5, f2 + rect.height, this.paint);
        }
        Rect rect2 = this.actualArea;
        float f6 = rect2.x;
        f = rect2.y;
        canvas.drawLine(f6, f + rect2.height, f6 + rect2.width, f, this.paintDash);
        CurvesValue curvesValue = null;
        int i3 = this.curveValue.activeType;
        if (i3 == 0) {
            this.paintCurve.setColor(-1);
            curvesValue = this.curveValue.luminanceCurve;
        } else if (i3 == 1) {
            this.paintCurve.setColor(-1229492);
            curvesValue = this.curveValue.redCurve;
        } else if (i3 == 2) {
            this.paintCurve.setColor(-15667555);
            curvesValue = this.curveValue.greenCurve;
        } else if (i3 == 3) {
            this.paintCurve.setColor(-13404165);
            curvesValue = this.curveValue.blueCurve;
        }
        for (i3 = 0; i3 < 5; i3++) {
            String format;
            String str = "%.2f";
            if (i3 == 0) {
                format = String.format(Locale.US, str, new Object[]{Float.valueOf(curvesValue.blacksLevel / 100.0f)});
            } else if (i3 == 1) {
                format = String.format(Locale.US, str, new Object[]{Float.valueOf(curvesValue.shadowsLevel / 100.0f)});
            } else if (i3 == 2) {
                format = String.format(Locale.US, str, new Object[]{Float.valueOf(curvesValue.midtonesLevel / 100.0f)});
            } else if (i3 == 3) {
                format = String.format(Locale.US, str, new Object[]{Float.valueOf(curvesValue.highlightsLevel / 100.0f)});
            } else if (i3 != 4) {
                format = "";
            } else {
                format = String.format(Locale.US, str, new Object[]{Float.valueOf(curvesValue.whitesLevel / 100.0f)});
            }
            f2 = this.textPaint.measureText(format);
            Rect rect3 = this.actualArea;
            canvas.drawText(format, (rect3.x + ((f3 - f2) / 2.0f)) + (((float) i3) * f3), (rect3.y + rect3.height) - ((float) AndroidUtilities.dp(4.0f)), this.textPaint);
        }
        float[] interpolateCurve = curvesValue.interpolateCurve();
        invalidate();
        this.path.reset();
        while (i < interpolateCurve.length / 2) {
            Path path;
            Rect rect4;
            int i4;
            if (i == 0) {
                path = this.path;
                rect4 = this.actualArea;
                i4 = i * 2;
                path.moveTo(rect4.x + (interpolateCurve[i4] * rect4.width), rect4.y + ((1.0f - interpolateCurve[i4 + 1]) * rect4.height));
            } else {
                path = this.path;
                rect4 = this.actualArea;
                i4 = i * 2;
                path.lineTo(rect4.x + (interpolateCurve[i4] * rect4.width), rect4.y + ((1.0f - interpolateCurve[i4 + 1]) * rect4.height));
            }
            i++;
        }
        canvas.drawPath(this.path, this.paintCurve);
    }
}
