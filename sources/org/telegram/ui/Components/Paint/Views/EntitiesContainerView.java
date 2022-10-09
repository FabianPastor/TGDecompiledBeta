package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.ui.Components.Paint.Views.RotationGestureDetector;
/* loaded from: classes3.dex */
public class EntitiesContainerView extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {
    private EntitiesContainerViewDelegate delegate;
    private ScaleGestureDetector gestureDetector;
    private boolean hasTransformed;
    private float previousAngle;
    private float previousScale;
    private RotationGestureDetector rotationGestureDetector;

    /* loaded from: classes3.dex */
    public interface EntitiesContainerViewDelegate {
        void onEntityDeselect();

        EntityView onSelectedEntityRequest();

        boolean shouldReceiveTouches();
    }

    @Override // org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener
    public void onRotationEnd(RotationGestureDetector rotationGestureDetector) {
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }

    public EntitiesContainerView(Context context, EntitiesContainerViewDelegate entitiesContainerViewDelegate) {
        super(context);
        this.previousScale = 1.0f;
        this.gestureDetector = new ScaleGestureDetector(context, this);
        this.rotationGestureDetector = new RotationGestureDetector(this);
        this.delegate = entitiesContainerViewDelegate;
    }

    public int entitiesCount() {
        int i = 0;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof EntityView) {
                i++;
            }
        }
        return i;
    }

    public void bringViewToFront(EntityView entityView) {
        if (indexOfChild(entityView) != getChildCount() - 1) {
            removeView(entityView);
            addView(entityView, getChildCount());
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return motionEvent.getPointerCount() == 2 && this.delegate.shouldReceiveTouches();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        EntitiesContainerViewDelegate entitiesContainerViewDelegate;
        if (this.delegate.onSelectedEntityRequest() == null) {
            return false;
        }
        if (motionEvent.getPointerCount() == 1) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.hasTransformed = false;
            } else if (actionMasked == 1 || actionMasked == 2) {
                if (!this.hasTransformed && (entitiesContainerViewDelegate = this.delegate) != null) {
                    entitiesContainerViewDelegate.onEntityDeselect();
                }
                return false;
            }
        }
        this.gestureDetector.onTouchEvent(motionEvent);
        this.rotationGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scaleFactor = scaleGestureDetector.getScaleFactor();
        this.delegate.onSelectedEntityRequest().scale(scaleFactor / this.previousScale);
        this.previousScale = scaleFactor;
        return false;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        this.previousScale = 1.0f;
        this.hasTransformed = true;
        return true;
    }

    @Override // org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener
    public void onRotationBegin(RotationGestureDetector rotationGestureDetector) {
        this.previousAngle = rotationGestureDetector.getStartAngle();
        this.hasTransformed = true;
    }

    @Override // org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener
    public void onRotation(RotationGestureDetector rotationGestureDetector) {
        EntityView onSelectedEntityRequest = this.delegate.onSelectedEntityRequest();
        float angle = rotationGestureDetector.getAngle();
        onSelectedEntityRequest.rotate(onSelectedEntityRequest.getRotation() + (this.previousAngle - angle));
        this.previousAngle = angle;
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (view instanceof TextPaintView) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            view.measure(FrameLayout.getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, marginLayoutParams.width), View.MeasureSpec.makeMeasureSpec(0, 0));
            return;
        }
        super.measureChildWithMargins(view, i, i2, i3, i4);
    }
}
