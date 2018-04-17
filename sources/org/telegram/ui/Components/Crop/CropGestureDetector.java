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

    /* renamed from: org.telegram.ui.Components.Crop.CropGestureDetector$1 */
    class C11201 implements OnScaleGestureListener {
        C11201() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (!Float.isNaN(scaleFactor)) {
                if (!Float.isInfinite(scaleFactor)) {
                    CropGestureDetector.this.mListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                    return true;
                }
            }
            return false;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    public interface CropGestureListener {
        void onDrag(float f, float f2);

        void onFling(float f, float f2, float f3, float f4);

        void onScale(float f, float f2, float f3);
    }

    public CropGestureDetector(Context context) {
        this.mMinimumVelocity = (float) ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        this.mDetector = new ScaleGestureDetector(context, new C11201());
    }

    float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(this.mActivePointerIndex);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    float getActiveY(MotionEvent ev) {
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
        float x;
        float y;
        float dx;
        float dy;
        this.mDetector.onTouchEvent(ev);
        int i = 0;
        int action = ev.getAction() & 255;
        boolean z = false;
        if (action != 3) {
            if (action != 6) {
                switch (action) {
                    case 0:
                        this.mActivePointerId = ev.getPointerId(0);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
            action = (65280 & ev.getAction()) >> 8;
            if (ev.getPointerId(action) == this.mActivePointerId) {
                int newPointerIndex;
                if (action == 0) {
                    newPointerIndex = 1;
                } else {
                    newPointerIndex = 0;
                }
                this.mActivePointerId = ev.getPointerId(newPointerIndex);
                this.mLastTouchX = ev.getX(newPointerIndex);
                this.mLastTouchY = ev.getY(newPointerIndex);
            }
            if (this.mActivePointerId != -1) {
                i = this.mActivePointerId;
            }
            this.mActivePointerIndex = ev.findPointerIndex(i);
            switch (ev.getAction()) {
                case 0:
                case 2:
                    if (!this.started) {
                        x = getActiveX(ev);
                        y = getActiveY(ev);
                        dx = x - this.mLastTouchX;
                        dy = y - this.mLastTouchY;
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
                            if (this.mVelocityTracker != null) {
                                this.mVelocityTracker.addMovement(ev);
                                break;
                            }
                        }
                    }
                    this.mVelocityTracker = VelocityTracker.obtain();
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.addMovement(ev);
                    }
                    this.mLastTouchX = getActiveX(ev);
                    this.mLastTouchY = getActiveY(ev);
                    this.mIsDragging = false;
                    this.started = true;
                    return true;
                    break;
                case 1:
                    if (this.mIsDragging) {
                        if (this.mVelocityTracker != null) {
                            this.mLastTouchX = getActiveX(ev);
                            this.mLastTouchY = getActiveY(ev);
                            this.mVelocityTracker.addMovement(ev);
                            this.mVelocityTracker.computeCurrentVelocity(1000);
                            x = this.mVelocityTracker.getXVelocity();
                            dx = this.mVelocityTracker.getYVelocity();
                            if (Math.max(Math.abs(x), Math.abs(dx)) >= this.mMinimumVelocity) {
                                this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -x, -dx);
                            }
                        }
                        this.mIsDragging = false;
                    }
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.started = false;
                    break;
                case 3:
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.started = false;
                    this.mIsDragging = false;
                    break;
                default:
                    break;
            }
            return true;
        }
        this.mActivePointerId = -1;
        if (this.mActivePointerId != -1) {
            i = this.mActivePointerId;
        }
        this.mActivePointerIndex = ev.findPointerIndex(i);
        switch (ev.getAction()) {
            case 0:
            case 2:
                if (!this.started) {
                    x = getActiveX(ev);
                    y = getActiveY(ev);
                    dx = x - this.mLastTouchX;
                    dy = y - this.mLastTouchY;
                    if (this.mIsDragging) {
                        if (((float) Math.sqrt((double) ((dx * dx) + (dy * dy)))) >= this.mTouchSlop) {
                            z = true;
                        }
                        this.mIsDragging = z;
                    }
                    if (this.mIsDragging) {
                        this.mListener.onDrag(dx, dy);
                        this.mLastTouchX = x;
                        this.mLastTouchY = y;
                        if (this.mVelocityTracker != null) {
                            this.mVelocityTracker.addMovement(ev);
                            break;
                        }
                    }
                }
                this.mVelocityTracker = VelocityTracker.obtain();
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.addMovement(ev);
                }
                this.mLastTouchX = getActiveX(ev);
                this.mLastTouchY = getActiveY(ev);
                this.mIsDragging = false;
                this.started = true;
                return true;
                break;
            case 1:
                if (this.mIsDragging) {
                    if (this.mVelocityTracker != null) {
                        this.mLastTouchX = getActiveX(ev);
                        this.mLastTouchY = getActiveY(ev);
                        this.mVelocityTracker.addMovement(ev);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        x = this.mVelocityTracker.getXVelocity();
                        dx = this.mVelocityTracker.getYVelocity();
                        if (Math.max(Math.abs(x), Math.abs(dx)) >= this.mMinimumVelocity) {
                            this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -x, -dx);
                        }
                    }
                    this.mIsDragging = false;
                }
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.started = false;
                break;
            case 3:
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.started = false;
                this.mIsDragging = false;
                break;
            default:
                break;
        }
        return true;
    }
}
