package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import org.telegram.messenger.AndroidUtilities;

public class CropGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = -1;
    private int mActivePointerIndex = 0;
    private ScaleGestureDetector mDetector;
    private boolean mIsDragging;
    private float mLastTouchX;
    private float mLastTouchY;
    /* access modifiers changed from: private */
    public CropGestureListener mListener;
    private final float mMinimumVelocity;
    private final float mTouchSlop = ((float) AndroidUtilities.dp(1.0f));
    private VelocityTracker mVelocityTracker;
    private boolean started;
    private long touchTime;

    public interface CropGestureListener {
        void onDrag(float f, float f2);

        void onFling(float f, float f2, float f3, float f4);

        void onScale(float f, float f2, float f3);

        void onTapUp();
    }

    public CropGestureDetector(Context context) {
        this.mMinimumVelocity = (float) ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        this.mDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }
                CropGestureDetector.this.mListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                return true;
            }

            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
    }

    /* access modifiers changed from: package-private */
    public float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(this.mActivePointerIndex);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    /* access modifiers changed from: package-private */
    public float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(this.mActivePointerIndex);
        } catch (Exception e) {
            return ev.getY();
        }
    }

    public void setOnGestureListener(CropGestureListener listener) {
        this.mListener = listener;
    }

    public boolean isScaling() {
        return this.mDetector.isInProgress();
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int newPointerIndex;
        this.mDetector.onTouchEvent(ev);
        int i = 0;
        boolean z = false;
        switch (ev.getAction() & 255) {
            case 0:
                this.mActivePointerId = ev.getPointerId(0);
                this.touchTime = SystemClock.elapsedRealtime();
                break;
            case 1:
            case 3:
                if (this.mIsDragging == 0 && SystemClock.elapsedRealtime() - this.touchTime < 800) {
                    this.mListener.onTapUp();
                }
                this.mActivePointerId = -1;
                break;
            case 6:
                int pointerIndex = (65280 & ev.getAction()) >> 8;
                if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
                    if (pointerIndex == 0) {
                        newPointerIndex = 1;
                    } else {
                        newPointerIndex = 0;
                    }
                    this.mActivePointerId = ev.getPointerId(newPointerIndex);
                    this.mLastTouchX = ev.getX(newPointerIndex);
                    this.mLastTouchY = ev.getY(newPointerIndex);
                    break;
                }
                break;
        }
        if (this.mActivePointerId != -1) {
            i = this.mActivePointerId;
        }
        this.mActivePointerIndex = ev.findPointerIndex(i);
        switch (ev.getAction()) {
            case 0:
            case 2:
                if (this.started) {
                    float x = getActiveX(ev);
                    float y = getActiveY(ev);
                    float dx = x - this.mLastTouchX;
                    float dy = y - this.mLastTouchY;
                    if (!this.mIsDragging) {
                        if (((float) Math.sqrt((double) ((dx * dx) + (dy * dy)))) >= this.mTouchSlop) {
                            z = true;
                        }
                        this.mIsDragging = z;
                    }
                    if (this.mIsDragging) {
                        this.mListener.onDrag(dx, dy);
                        this.mLastTouchX = x;
                        this.mLastTouchY = y;
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        if (velocityTracker != null) {
                            velocityTracker.addMovement(ev);
                            break;
                        }
                    }
                } else {
                    VelocityTracker obtain = VelocityTracker.obtain();
                    this.mVelocityTracker = obtain;
                    if (obtain != null) {
                        obtain.addMovement(ev);
                    }
                    this.mLastTouchX = getActiveX(ev);
                    this.mLastTouchY = getActiveY(ev);
                    this.mIsDragging = false;
                    this.started = true;
                    return true;
                }
                break;
            case 1:
                if (this.mIsDragging) {
                    if (this.mVelocityTracker != null) {
                        this.mLastTouchX = getActiveX(ev);
                        this.mLastTouchY = getActiveY(ev);
                        this.mVelocityTracker.addMovement(ev);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        float vX = this.mVelocityTracker.getXVelocity();
                        float vY = this.mVelocityTracker.getYVelocity();
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= this.mMinimumVelocity) {
                            this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -vX, -vY);
                        }
                    }
                    this.mIsDragging = false;
                }
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.mVelocityTracker = null;
                }
                this.started = false;
                break;
            case 3:
                VelocityTracker velocityTracker3 = this.mVelocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.mVelocityTracker = null;
                }
                this.started = false;
                this.mIsDragging = false;
                break;
        }
        return true;
    }
}
