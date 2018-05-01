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
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }

        C11201() {
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            if (!Float.isNaN(scaleFactor)) {
                if (!Float.isInfinite(scaleFactor)) {
                    CropGestureDetector.this.mListener.onScale(scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                    return true;
                }
            }
            return null;
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

    float getActiveX(android.view.MotionEvent r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r1 = this;
        r0 = r1.mActivePointerIndex;	 Catch:{ Exception -> 0x0007 }
        r0 = r2.getX(r0);	 Catch:{ Exception -> 0x0007 }
        return r0;
    L_0x0007:
        r2 = r2.getX();
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Crop.CropGestureDetector.getActiveX(android.view.MotionEvent):float");
    }

    float getActiveY(android.view.MotionEvent r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r1 = this;
        r0 = r1.mActivePointerIndex;	 Catch:{ Exception -> 0x0007 }
        r0 = r2.getY(r0);	 Catch:{ Exception -> 0x0007 }
        return r0;
    L_0x0007:
        r2 = r2.getY();
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Crop.CropGestureDetector.getActiveY(android.view.MotionEvent):float");
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
        float activeX;
        float activeY;
        float f;
        float f2;
        this.mDetector.onTouchEvent(motionEvent);
        int action = motionEvent.getAction() & 255;
        boolean z = false;
        if (action != 3) {
            if (action != 6) {
                switch (action) {
                    case 0:
                        this.mActivePointerId = motionEvent.getPointerId(0);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
            action = (65280 & motionEvent.getAction()) >> 8;
            if (motionEvent.getPointerId(action) == this.mActivePointerId) {
                action = action == 0 ? 1 : 0;
                this.mActivePointerId = motionEvent.getPointerId(action);
                this.mLastTouchX = motionEvent.getX(action);
                this.mLastTouchY = motionEvent.getY(action);
            }
            this.mActivePointerIndex = motionEvent.findPointerIndex(this.mActivePointerId == -1 ? this.mActivePointerId : 0);
            switch (motionEvent.getAction()) {
                case 0:
                case 2:
                    if (!this.started) {
                        activeX = getActiveX(motionEvent);
                        activeY = getActiveY(motionEvent);
                        f = activeX - this.mLastTouchX;
                        f2 = activeY - this.mLastTouchY;
                        if (!this.mIsDragging) {
                            if (((float) Math.sqrt((double) ((f * f) + (f2 * f2)))) >= this.mTouchSlop) {
                                z = true;
                            }
                            this.mIsDragging = z;
                        }
                        if (this.mIsDragging) {
                            this.mListener.onDrag(f, f2);
                            this.mLastTouchX = activeX;
                            this.mLastTouchY = activeY;
                            if (this.mVelocityTracker != null) {
                                this.mVelocityTracker.addMovement(motionEvent);
                                break;
                            }
                        }
                    }
                    this.mVelocityTracker = VelocityTracker.obtain();
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.addMovement(motionEvent);
                    }
                    this.mLastTouchX = getActiveX(motionEvent);
                    this.mLastTouchY = getActiveY(motionEvent);
                    this.mIsDragging = false;
                    this.started = true;
                    return true;
                    break;
                case 1:
                    if (this.mIsDragging) {
                        if (this.mVelocityTracker != null) {
                            this.mLastTouchX = getActiveX(motionEvent);
                            this.mLastTouchY = getActiveY(motionEvent);
                            this.mVelocityTracker.addMovement(motionEvent);
                            this.mVelocityTracker.computeCurrentVelocity(1000);
                            motionEvent = this.mVelocityTracker.getXVelocity();
                            activeX = this.mVelocityTracker.getYVelocity();
                            if (Math.max(Math.abs(motionEvent), Math.abs(activeX)) >= this.mMinimumVelocity) {
                                this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -motionEvent, -activeX);
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
        if (this.mActivePointerId == -1) {
        }
        this.mActivePointerIndex = motionEvent.findPointerIndex(this.mActivePointerId == -1 ? this.mActivePointerId : 0);
        switch (motionEvent.getAction()) {
            case 0:
            case 2:
                if (!this.started) {
                    activeX = getActiveX(motionEvent);
                    activeY = getActiveY(motionEvent);
                    f = activeX - this.mLastTouchX;
                    f2 = activeY - this.mLastTouchY;
                    if (this.mIsDragging) {
                        if (((float) Math.sqrt((double) ((f * f) + (f2 * f2)))) >= this.mTouchSlop) {
                            z = true;
                        }
                        this.mIsDragging = z;
                    }
                    if (this.mIsDragging) {
                        this.mListener.onDrag(f, f2);
                        this.mLastTouchX = activeX;
                        this.mLastTouchY = activeY;
                        if (this.mVelocityTracker != null) {
                            this.mVelocityTracker.addMovement(motionEvent);
                            break;
                        }
                    }
                }
                this.mVelocityTracker = VelocityTracker.obtain();
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.addMovement(motionEvent);
                }
                this.mLastTouchX = getActiveX(motionEvent);
                this.mLastTouchY = getActiveY(motionEvent);
                this.mIsDragging = false;
                this.started = true;
                return true;
                break;
            case 1:
                if (this.mIsDragging) {
                    if (this.mVelocityTracker != null) {
                        this.mLastTouchX = getActiveX(motionEvent);
                        this.mLastTouchY = getActiveY(motionEvent);
                        this.mVelocityTracker.addMovement(motionEvent);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        motionEvent = this.mVelocityTracker.getXVelocity();
                        activeX = this.mVelocityTracker.getYVelocity();
                        if (Math.max(Math.abs(motionEvent), Math.abs(activeX)) >= this.mMinimumVelocity) {
                            this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -motionEvent, -activeX);
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
