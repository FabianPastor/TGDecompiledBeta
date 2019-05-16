package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import org.telegram.messenger.AndroidUtilities;

public class CropGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = -1;
    private int mActivePointerIndex = 0;
    private ScaleGestureDetector mDetector;
    private boolean mIsDragging;
    float mLastTouchX;
    float mLastTouchY;
    private CropGestureListener mListener;
    final float mMinimumVelocity;
    final float mTouchSlop = ((float) AndroidUtilities.dp(1.0f));
    private VelocityTracker mVelocityTracker;
    private boolean started;

    public interface CropGestureListener {
        void onDrag(float f, float f2);

        void onFling(float f, float f2, float f3, float f4);

        void onScale(float f, float f2, float f3);
    }

    public CropGestureDetector(Context context) {
        this.mMinimumVelocity = (float) ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        this.mDetector = new ScaleGestureDetector(context, new OnScaleGestureListener() {
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            }

            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float scaleFactor = scaleGestureDetector.getScaleFactor();
                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }
                CropGestureDetector.this.mListener.onScale(scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                return true;
            }
        });
    }

    /* Access modifiers changed, original: 0000 */
    public float getActiveX(MotionEvent motionEvent) {
        try {
            motionEvent = motionEvent.getX(this.mActivePointerIndex);
            return motionEvent;
        } catch (Exception unused) {
            return motionEvent.getX();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public float getActiveY(MotionEvent motionEvent) {
        try {
            motionEvent = motionEvent.getY(this.mActivePointerIndex);
            return motionEvent;
        } catch (Exception unused) {
            return motionEvent.getY();
        }
    }

    public void setOnGestureListener(CropGestureListener cropGestureListener) {
        this.mListener = cropGestureListener;
    }

    public boolean isScaling() {
        return this.mDetector.isInProgress();
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float yVelocity;
        this.mDetector.onTouchEvent(motionEvent);
        int action = motionEvent.getAction() & 255;
        boolean z = false;
        if (action == 0) {
            this.mActivePointerId = motionEvent.getPointerId(0);
        } else if (action == 1 || action == 3) {
            this.mActivePointerId = -1;
        } else if (action == 6) {
            action = (65280 & motionEvent.getAction()) >> 8;
            if (motionEvent.getPointerId(action) == this.mActivePointerId) {
                action = action == 0 ? 1 : 0;
                this.mActivePointerId = motionEvent.getPointerId(action);
                this.mLastTouchX = motionEvent.getX(action);
                this.mLastTouchY = motionEvent.getY(action);
            }
        }
        action = this.mActivePointerId;
        if (action == -1) {
            action = 0;
        }
        this.mActivePointerIndex = motionEvent.findPointerIndex(action);
        action = motionEvent.getAction();
        if (action != 0) {
            VelocityTracker velocityTracker;
            if (action == 1) {
                if (this.mIsDragging) {
                    if (this.mVelocityTracker != null) {
                        this.mLastTouchX = getActiveX(motionEvent);
                        this.mLastTouchY = getActiveY(motionEvent);
                        this.mVelocityTracker.addMovement(motionEvent);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        float xVelocity = this.mVelocityTracker.getXVelocity();
                        yVelocity = this.mVelocityTracker.getYVelocity();
                        if (Math.max(Math.abs(xVelocity), Math.abs(yVelocity)) >= this.mMinimumVelocity) {
                            this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -xVelocity, -yVelocity);
                        }
                    }
                    this.mIsDragging = false;
                }
                velocityTracker = this.mVelocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.started = false;
            } else if (action != 2) {
                if (action == 3) {
                    velocityTracker = this.mVelocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.started = false;
                    this.mIsDragging = false;
                }
            }
            return true;
        }
        VelocityTracker velocityTracker2;
        if (this.started) {
            yVelocity = getActiveX(motionEvent);
            float activeY = getActiveY(motionEvent);
            float f = yVelocity - this.mLastTouchX;
            float f2 = activeY - this.mLastTouchY;
            if (!this.mIsDragging) {
                if (((float) Math.sqrt((double) ((f * f) + (f2 * f2)))) >= this.mTouchSlop) {
                    z = true;
                }
                this.mIsDragging = z;
            }
            if (this.mIsDragging) {
                this.mListener.onDrag(f, f2);
                this.mLastTouchX = yVelocity;
                this.mLastTouchY = activeY;
                velocityTracker2 = this.mVelocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.addMovement(motionEvent);
                }
            }
            return true;
        }
        this.mVelocityTracker = VelocityTracker.obtain();
        velocityTracker2 = this.mVelocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.addMovement(motionEvent);
        }
        this.mLastTouchX = getActiveX(motionEvent);
        this.mLastTouchY = getActiveY(motionEvent);
        this.mIsDragging = false;
        this.started = true;
        return true;
    }
}
