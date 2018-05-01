package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.FrameLayout;
import org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener;

public class EntitiesContainerView extends FrameLayout implements OnScaleGestureListener, OnRotationGestureListener {
    private EntitiesContainerViewDelegate delegate;
    private ScaleGestureDetector gestureDetector;
    private boolean hasTransformed;
    private float previousAngle;
    private float previousScale = 1.0f;
    private RotationGestureDetector rotationGestureDetector;

    public interface EntitiesContainerViewDelegate {
        void onEntityDeselect();

        EntityView onSelectedEntityRequest();

        boolean shouldReceiveTouches();
    }

    public void onRotationEnd(RotationGestureDetector rotationGestureDetector) {
    }

    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }

    public EntitiesContainerView(Context context, EntitiesContainerViewDelegate entitiesContainerViewDelegate) {
        super(context);
        this.gestureDetector = new ScaleGestureDetector(context, this);
        this.rotationGestureDetector = new RotationGestureDetector(this);
        this.delegate = entitiesContainerViewDelegate;
    }

    public int entitiesCount() {
        int i = 0;
        int i2 = 0;
        while (i < getChildCount()) {
            if (getChildAt(i) instanceof EntityView) {
                i2++;
            }
            i++;
        }
        return i2;
    }

    public void bringViewToFront(EntityView entityView) {
        if (indexOfChild(entityView) != getChildCount() - 1) {
            removeView(entityView);
            addView(entityView, getChildCount());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return (motionEvent.getPointerCount() != 2 || this.delegate.shouldReceiveTouches() == null) ? null : true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.delegate.onSelectedEntityRequest() == null) {
            return false;
        }
        if (motionEvent.getPointerCount() == 1) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.hasTransformed = false;
            } else if (actionMasked == 1 || actionMasked == 2) {
                if (this.hasTransformed == null && this.delegate != null) {
                    this.delegate.onEntityDeselect();
                }
                return false;
            }
        }
        this.gestureDetector.onTouchEvent(motionEvent);
        this.rotationGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        scaleGestureDetector = scaleGestureDetector.getScaleFactor();
        this.delegate.onSelectedEntityRequest().scale(scaleGestureDetector / this.previousScale);
        this.previousScale = scaleGestureDetector;
        return null;
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        this.previousScale = 1.0f;
        this.hasTransformed = true;
        return true;
    }

    public void onRotationBegin(RotationGestureDetector rotationGestureDetector) {
        this.previousAngle = rotationGestureDetector.getStartAngle();
        this.hasTransformed = true;
    }

    public void onRotation(RotationGestureDetector rotationGestureDetector) {
        EntityView onSelectedEntityRequest = this.delegate.onSelectedEntityRequest();
        rotationGestureDetector = rotationGestureDetector.getAngle();
        onSelectedEntityRequest.rotate(onSelectedEntityRequest.getRotation() + (this.previousAngle - rotationGestureDetector));
        this.previousAngle = rotationGestureDetector;
    }
}
