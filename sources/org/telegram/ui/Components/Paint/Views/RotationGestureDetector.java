package org.telegram.ui.Components.Paint.Views;

import android.view.MotionEvent;

public class RotationGestureDetector {
    private float angle;
    private float fX;
    private float fY;
    private OnRotationGestureListener mListener;
    private float sX;
    private float sY;
    private float startAngle;

    public interface OnRotationGestureListener {
        void onRotation(RotationGestureDetector rotationGestureDetector);

        void onRotationBegin(RotationGestureDetector rotationGestureDetector);

        void onRotationEnd(RotationGestureDetector rotationGestureDetector);
    }

    public float getAngle() {
        return this.angle;
    }

    public float getStartAngle() {
        return this.startAngle;
    }

    public RotationGestureDetector(OnRotationGestureListener listener) {
        this.mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return false;
        }
        switch (event.getActionMasked()) {
            case 0:
            case 5:
                this.sX = event.getX(0);
                this.sY = event.getY(0);
                this.fX = event.getX(1);
                this.fY = event.getY(1);
                break;
            case 1:
            case 3:
                this.startAngle = Float.NaN;
                break;
            case 2:
                float nsX = event.getX(0);
                float nsY = event.getY(0);
                this.angle = angleBetweenLines(this.fX, this.fY, this.sX, this.sY, event.getX(1), event.getY(1), nsX, nsY);
                if (this.mListener != null) {
                    if (!Float.isNaN(this.startAngle)) {
                        this.mListener.onRotation(this);
                        break;
                    } else {
                        this.startAngle = this.angle;
                        this.mListener.onRotationBegin(this);
                        break;
                    }
                }
                break;
            case 6:
                this.startAngle = Float.NaN;
                OnRotationGestureListener onRotationGestureListener = this.mListener;
                if (onRotationGestureListener != null) {
                    onRotationGestureListener.onRotationEnd(this);
                    break;
                }
                break;
        }
        return true;
    }

    private float angleBetweenLines(float fX2, float fY2, float sX2, float sY2, float nfX, float nfY, float nsX, float nsY) {
        float angle2 = ((float) Math.toDegrees((double) (((float) Math.atan2((double) (fY2 - sY2), (double) (fX2 - sX2))) - ((float) Math.atan2((double) (nfY - nsY), (double) (nfX - nsX)))))) % 360.0f;
        if (angle2 < -180.0f) {
            angle2 += 360.0f;
        }
        if (angle2 > 180.0f) {
            return angle2 - 360.0f;
        }
        return angle2;
    }
}
