package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.ui.Components.Paint.Views.RotationGestureDetector;

public class EntitiesContainerView extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {
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

    public EntitiesContainerView(Context context, EntitiesContainerViewDelegate entitiesContainerViewDelegate) {
        super(context);
        this.gestureDetector = new ScaleGestureDetector(context, this);
        this.rotationGestureDetector = new RotationGestureDetector(this);
        this.delegate = entitiesContainerViewDelegate;
    }

    public int entitiesCount() {
        int count = 0;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) instanceof EntityView) {
                count++;
            }
        }
        return count;
    }

    public void bringViewToFront(EntityView view) {
        if (indexOfChild(view) != getChildCount() - 1) {
            removeView(view);
            addView(view, getChildCount());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return ev.getPointerCount() == 2 && this.delegate.shouldReceiveTouches();
    }

    public boolean onTouchEvent(MotionEvent event) {
        EntitiesContainerViewDelegate entitiesContainerViewDelegate;
        if (this.delegate.onSelectedEntityRequest() == null) {
            return false;
        }
        if (event.getPointerCount() == 1) {
            int action = event.getActionMasked();
            if (action == 0) {
                this.hasTransformed = false;
            } else if (action == 1 || action == 2) {
                if (!this.hasTransformed && (entitiesContainerViewDelegate = this.delegate) != null) {
                    entitiesContainerViewDelegate.onEntityDeselect();
                }
                return false;
            }
        }
        this.gestureDetector.onTouchEvent(event);
        this.rotationGestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean onScale(ScaleGestureDetector detector) {
        float sf = detector.getScaleFactor();
        this.delegate.onSelectedEntityRequest().scale(sf / this.previousScale);
        this.previousScale = sf;
        return false;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        this.previousScale = 1.0f;
        this.hasTransformed = true;
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    public void onRotationBegin(RotationGestureDetector rotationDetector) {
        this.previousAngle = rotationDetector.getStartAngle();
        this.hasTransformed = true;
    }

    public void onRotation(RotationGestureDetector rotationDetector) {
        EntityView view = this.delegate.onSelectedEntityRequest();
        float angle = rotationDetector.getAngle();
        view.rotate(view.getRotation() + (this.previousAngle - angle));
        this.previousAngle = angle;
    }

    public void onRotationEnd(RotationGestureDetector rotationDetector) {
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (child instanceof TextPaintView) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            child.measure(getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed, lp.width), View.MeasureSpec.makeMeasureSpec(0, 0));
            return;
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }
}
