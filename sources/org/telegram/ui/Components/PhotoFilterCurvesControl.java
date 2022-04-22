package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.PhotoFilterView;

public class PhotoFilterCurvesControl extends View {
    private int activeSegment = 0;
    private Rect actualArea = new Rect();
    private boolean checkForMoving = true;
    private PhotoFilterView.CurvesToolValue curveValue;
    private PhotoFilterCurvesControlDelegate delegate;
    private boolean isMoving;
    private float lastY;
    private Paint paint = new Paint(1);
    private Paint paintCurve = new Paint(1);
    private Paint paintDash = new Paint(1);
    private Path path = new Path();
    private TextPaint textPaint = new TextPaint(1);

    public interface PhotoFilterCurvesControlDelegate {
        void valueChanged();
    }

    public PhotoFilterCurvesControl(Context context, PhotoFilterView.CurvesToolValue curvesToolValue) {
        super(context);
        setWillNotDraw(false);
        this.curveValue = curvesToolValue;
        this.paint.setColor(-NUM);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paintDash.setColor(-NUM);
        this.paintDash.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintDash.setStyle(Paint.Style.STROKE);
        this.paintCurve.setColor(-1);
        this.paintCurve.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paintCurve.setStyle(Paint.Style.STROKE);
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

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        if (r0 != 6) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getActionMasked()
            r1 = 0
            r2 = 3
            r3 = 1
            if (r0 == 0) goto L_0x002b
            if (r0 == r3) goto L_0x001f
            r4 = 2
            if (r0 == r4) goto L_0x0017
            if (r0 == r2) goto L_0x001f
            r4 = 5
            if (r0 == r4) goto L_0x002b
            r4 = 6
            if (r0 == r4) goto L_0x001f
            goto L_0x0076
        L_0x0017:
            boolean r0 = r7.isMoving
            if (r0 == 0) goto L_0x0076
            r7.handlePan(r4, r8)
            goto L_0x0076
        L_0x001f:
            boolean r0 = r7.isMoving
            if (r0 == 0) goto L_0x0028
            r7.handlePan(r2, r8)
            r7.isMoving = r1
        L_0x0028:
            r7.checkForMoving = r3
            goto L_0x0076
        L_0x002b:
            int r0 = r8.getPointerCount()
            if (r0 != r3) goto L_0x006b
            boolean r0 = r7.checkForMoving
            if (r0 == 0) goto L_0x0076
            boolean r0 = r7.isMoving
            if (r0 != 0) goto L_0x0076
            float r0 = r8.getX()
            float r2 = r8.getY()
            r7.lastY = r2
            org.telegram.ui.Components.Rect r4 = r7.actualArea
            float r5 = r4.x
            int r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r6 < 0) goto L_0x0061
            float r6 = r4.width
            float r5 = r5 + r6
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 > 0) goto L_0x0061
            float r0 = r4.y
            int r5 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r5 < 0) goto L_0x0061
            float r4 = r4.height
            float r0 = r0 + r4
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x0061
            r7.isMoving = r3
        L_0x0061:
            r7.checkForMoving = r1
            boolean r0 = r7.isMoving
            if (r0 == 0) goto L_0x0076
            r7.handlePan(r3, r8)
            goto L_0x0076
        L_0x006b:
            boolean r0 = r7.isMoving
            if (r0 == 0) goto L_0x0076
            r7.handlePan(r2, r8)
            r7.checkForMoving = r3
            r7.isMoving = r1
        L_0x0076:
            return r3
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
            PhotoFilterView.CurvesValue curvesValue = null;
            PhotoFilterView.CurvesToolValue curvesToolValue = this.curveValue;
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

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        String str;
        float f = this.actualArea.width / 5.0f;
        for (int i = 0; i < 4; i++) {
            Rect rect = this.actualArea;
            float f2 = rect.x;
            float f3 = ((float) i) * f;
            float f4 = rect.y;
            canvas.drawLine(f2 + f + f3, f4, f2 + f + f3, f4 + rect.height, this.paint);
        }
        Rect rect2 = this.actualArea;
        float f5 = rect2.x;
        float f6 = rect2.y;
        canvas.drawLine(f5, f6 + rect2.height, f5 + rect2.width, f6, this.paintDash);
        PhotoFilterView.CurvesValue curvesValue = null;
        int i2 = this.curveValue.activeType;
        if (i2 == 0) {
            this.paintCurve.setColor(-1);
            curvesValue = this.curveValue.luminanceCurve;
        } else if (i2 == 1) {
            this.paintCurve.setColor(-1229492);
            curvesValue = this.curveValue.redCurve;
        } else if (i2 == 2) {
            this.paintCurve.setColor(-15667555);
            curvesValue = this.curveValue.greenCurve;
        } else if (i2 == 3) {
            this.paintCurve.setColor(-13404165);
            curvesValue = this.curveValue.blueCurve;
        }
        for (int i3 = 0; i3 < 5; i3++) {
            if (i3 == 0) {
                str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.blacksLevel / 100.0f)});
            } else if (i3 == 1) {
                str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.shadowsLevel / 100.0f)});
            } else if (i3 == 2) {
                str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.midtonesLevel / 100.0f)});
            } else if (i3 == 3) {
                str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.highlightsLevel / 100.0f)});
            } else if (i3 != 4) {
                str = "";
            } else {
                str = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.whitesLevel / 100.0f)});
            }
            float measureText = this.textPaint.measureText(str);
            Rect rect3 = this.actualArea;
            canvas.drawText(str, rect3.x + ((f - measureText) / 2.0f) + (((float) i3) * f), (rect3.y + rect3.height) - ((float) AndroidUtilities.dp(4.0f)), this.textPaint);
        }
        float[] interpolateCurve = curvesValue.interpolateCurve();
        invalidate();
        this.path.reset();
        for (int i4 = 0; i4 < interpolateCurve.length / 2; i4++) {
            if (i4 == 0) {
                Path path2 = this.path;
                Rect rect4 = this.actualArea;
                int i5 = i4 * 2;
                path2.moveTo(rect4.x + (interpolateCurve[i5] * rect4.width), rect4.y + ((1.0f - interpolateCurve[i5 + 1]) * rect4.height));
            } else {
                Path path3 = this.path;
                Rect rect5 = this.actualArea;
                int i6 = i4 * 2;
                path3.lineTo(rect5.x + (interpolateCurve[i6] * rect5.width), rect5.y + ((1.0f - interpolateCurve[i6 + 1]) * rect5.height));
            }
        }
        canvas.drawPath(this.path, this.paintCurve);
    }
}
