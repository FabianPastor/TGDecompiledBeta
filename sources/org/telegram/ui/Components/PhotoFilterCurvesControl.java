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
        this.actualArea.f26x = f;
        this.actualArea.f27y = f2;
        this.actualArea.width = f3;
        this.actualArea.height = f4;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 0:
            case 5:
                if (motionEvent.getPointerCount() != 1) {
                    if (this.isMoving) {
                        handlePan(3, motionEvent);
                        this.checkForMoving = true;
                        this.isMoving = false;
                        break;
                    }
                } else if (this.checkForMoving && !this.isMoving) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    this.lastX = x;
                    this.lastY = y;
                    if (x >= this.actualArea.f26x && x <= this.actualArea.f26x + this.actualArea.width && y >= this.actualArea.f27y && y <= this.actualArea.f27y + this.actualArea.height) {
                        this.isMoving = true;
                    }
                    this.checkForMoving = false;
                    if (this.isMoving) {
                        handlePan(1, motionEvent);
                        break;
                    }
                }
                break;
            case 1:
            case 3:
            case 6:
                if (this.isMoving) {
                    handlePan(3, motionEvent);
                    this.isMoving = false;
                }
                this.checkForMoving = true;
                break;
            case 2:
                if (this.isMoving) {
                    handlePan(2, motionEvent);
                    break;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void handlePan(int i, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        motionEvent = motionEvent.getY();
        switch (i) {
            case 1:
                selectSegmentWithPoint(x);
                return;
            case 2:
                i = Math.min(NUM, (this.lastY - motionEvent) / 8.0f);
                CurvesValue curvesValue = null;
                switch (this.curveValue.activeType) {
                    case 0:
                        curvesValue = this.curveValue.luminanceCurve;
                        break;
                    case 1:
                        curvesValue = this.curveValue.redCurve;
                        break;
                    case 2:
                        curvesValue = this.curveValue.greenCurve;
                        break;
                    case 3:
                        curvesValue = this.curveValue.blueCurve;
                        break;
                    default:
                        break;
                }
                switch (this.activeSegment) {
                    case 1:
                        curvesValue.blacksLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.blacksLevel + i));
                        break;
                    case 2:
                        curvesValue.shadowsLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.shadowsLevel + i));
                        break;
                    case 3:
                        curvesValue.midtonesLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.midtonesLevel + i));
                        break;
                    case 4:
                        curvesValue.highlightsLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.highlightsLevel + i));
                        break;
                    case 5:
                        curvesValue.whitesLevel = Math.max(0.0f, Math.min(100.0f, curvesValue.whitesLevel + i));
                        break;
                    default:
                        break;
                }
                invalidate();
                if (this.delegate != 0) {
                    this.delegate.valueChanged();
                }
                this.lastX = x;
                this.lastY = motionEvent;
                return;
            case 3:
            case 4:
            case 5:
                unselectSegments();
                return;
            default:
                return;
        }
    }

    private void selectSegmentWithPoint(float f) {
        if (this.activeSegment == 0) {
            this.activeSegment = (int) Math.floor((double) (((f - this.actualArea.f26x) / (this.actualArea.width / 5.0f)) + 1.0f));
        }
    }

    private void unselectSegments() {
        if (this.activeSegment != 0) {
            this.activeSegment = 0;
        }
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        float f = this.actualArea.width / 5.0f;
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            float f2 = ((float) i2) * f;
            canvas.drawLine((this.actualArea.f26x + f) + f2, this.actualArea.f27y, (this.actualArea.f26x + f) + f2, this.actualArea.f27y + this.actualArea.height, this.paint);
        }
        canvas.drawLine(this.actualArea.f26x, this.actualArea.f27y + this.actualArea.height, this.actualArea.f26x + this.actualArea.width, this.actualArea.f27y, this.paintDash);
        CurvesValue curvesValue = null;
        switch (this.curveValue.activeType) {
            case 0:
                this.paintCurve.setColor(-1);
                curvesValue = this.curveValue.luminanceCurve;
                break;
            case 1:
                this.paintCurve.setColor(-1229492);
                curvesValue = this.curveValue.redCurve;
                break;
            case 2:
                this.paintCurve.setColor(-15667555);
                curvesValue = this.curveValue.greenCurve;
                break;
            case 3:
                this.paintCurve.setColor(-13404165);
                curvesValue = this.curveValue.blueCurve;
                break;
            default:
                break;
        }
        for (int i3 = 0; i3 < 5; i3++) {
            String format;
            switch (i3) {
                case 0:
                    format = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.blacksLevel / 100.0f)});
                    break;
                case 1:
                    format = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.shadowsLevel / 100.0f)});
                    break;
                case 2:
                    format = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.midtonesLevel / 100.0f)});
                    break;
                case 3:
                    format = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.highlightsLevel / 100.0f)});
                    break;
                case 4:
                    format = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(curvesValue.whitesLevel / 100.0f)});
                    break;
                default:
                    format = TtmlNode.ANONYMOUS_REGION_ID;
                    break;
            }
            canvas.drawText(format, (this.actualArea.f26x + ((f - this.textPaint.measureText(format)) / 2.0f)) + (((float) i3) * f), (this.actualArea.f27y + this.actualArea.height) - ((float) AndroidUtilities.dp(4.0f)), this.textPaint);
        }
        float[] interpolateCurve = curvesValue.interpolateCurve();
        invalidate();
        this.path.reset();
        while (i < interpolateCurve.length / 2) {
            int i4;
            if (i == 0) {
                i4 = i * 2;
                this.path.moveTo(this.actualArea.f26x + (interpolateCurve[i4] * this.actualArea.width), this.actualArea.f27y + ((1.0f - interpolateCurve[i4 + 1]) * this.actualArea.height));
            } else {
                i4 = i * 2;
                this.path.lineTo(this.actualArea.f26x + (interpolateCurve[i4] * this.actualArea.width), this.actualArea.f27y + ((1.0f - interpolateCurve[i4 + 1]) * this.actualArea.height));
            }
            i++;
        }
        canvas.drawPath(this.path, this.paintCurve);
    }
}
