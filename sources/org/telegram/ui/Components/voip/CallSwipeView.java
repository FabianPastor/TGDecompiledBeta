package org.telegram.ui.Components.voip;

import android.animation.AnimatorSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes3.dex */
public class CallSwipeView extends View {
    private boolean animatingArrows;
    private Path arrow;
    private int[] arrowAlphas;
    private AnimatorSet arrowAnim;
    private Paint arrowsPaint;
    private boolean dragFromRight;
    private float dragStartX;
    private boolean dragging;
    private Listener listener;
    private Paint pullBgPaint;
    private RectF tmpRect;
    private View viewToDrag;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onDragCancel();

        void onDragComplete();

        void onDragStart();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatorSet animatorSet = this.arrowAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.arrowAnim = null;
        }
    }

    public void setColor(int i) {
        this.pullBgPaint.setColor(i);
        this.pullBgPaint.setAlpha(178);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private int getDraggedViewWidth() {
        return getHeight();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (!isEnabled() || accessibilityManager.isTouchExplorationEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            if ((!this.dragFromRight && motionEvent.getX() < getDraggedViewWidth()) || (this.dragFromRight && motionEvent.getX() > getWidth() - getDraggedViewWidth())) {
                this.dragging = true;
                this.dragStartX = motionEvent.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                this.listener.onDragStart();
                stopAnimatingArrows();
            }
        } else {
            float f = 0.0f;
            if (motionEvent.getAction() == 2) {
                View view = this.viewToDrag;
                float f2 = this.dragFromRight ? -(getWidth() - getDraggedViewWidth()) : 0.0f;
                float x = motionEvent.getX() - this.dragStartX;
                if (!this.dragFromRight) {
                    f = getWidth() - getDraggedViewWidth();
                }
                view.setTranslationX(Math.max(f2, Math.min(x, f)));
                invalidate();
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (Math.abs(this.viewToDrag.getTranslationX()) >= getWidth() - getDraggedViewWidth() && motionEvent.getAction() == 1) {
                    this.listener.onDragComplete();
                } else {
                    this.listener.onDragCancel();
                    this.viewToDrag.animate().translationX(0.0f).setDuration(200L).start();
                    invalidate();
                    startAnimatingArrows();
                    this.dragging = false;
                }
            }
        }
        return this.dragging;
    }

    public void stopAnimatingArrows() {
        this.animatingArrows = false;
    }

    public void startAnimatingArrows() {
        AnimatorSet animatorSet;
        if (this.animatingArrows || (animatorSet = this.arrowAnim) == null) {
            return;
        }
        this.animatingArrows = true;
        if (animatorSet == null) {
            return;
        }
        animatorSet.start();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.viewToDrag.getTranslationX() != 0.0f) {
            if (this.dragFromRight) {
                this.tmpRect.set((getWidth() + this.viewToDrag.getTranslationX()) - getDraggedViewWidth(), 0.0f, getWidth(), getHeight());
            } else {
                this.tmpRect.set(0.0f, 0.0f, this.viewToDrag.getTranslationX() + getDraggedViewWidth(), getHeight());
            }
            canvas.drawRoundRect(this.tmpRect, getHeight() / 2, getHeight() / 2, this.pullBgPaint);
        }
        canvas.save();
        if (this.dragFromRight) {
            canvas.translate((getWidth() - getHeight()) - AndroidUtilities.dp(18.0f), getHeight() / 2);
        } else {
            canvas.translate(getHeight() + AndroidUtilities.dp(12.0f), getHeight() / 2);
        }
        float abs = Math.abs(this.viewToDrag.getTranslationX());
        for (int i = 0; i < 3; i++) {
            float f = 16.0f;
            float f2 = 1.0f;
            if (abs > AndroidUtilities.dp(i * 16)) {
                f2 = 1.0f - Math.min(1.0f, Math.max(0.0f, (abs - (AndroidUtilities.dp(16.0f) * i)) / AndroidUtilities.dp(16.0f)));
            }
            this.arrowsPaint.setAlpha(Math.round(this.arrowAlphas[i] * f2));
            canvas.drawPath(this.arrow, this.arrowsPaint);
            if (this.dragFromRight) {
                f = -16.0f;
            }
            canvas.translate(AndroidUtilities.dp(f), 0.0f);
        }
        canvas.restore();
        invalidate();
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (isEnabled() && accessibilityEvent.getEventType() == 1) {
            this.listener.onDragComplete();
        }
        super.onPopulateAccessibilityEvent(accessibilityEvent);
    }

    /* loaded from: classes3.dex */
    private class ArrowAnimWrapper {
        private int index;
        final /* synthetic */ CallSwipeView this$0;

        @Keep
        public int getArrowAlpha() {
            return this.this$0.arrowAlphas[this.index];
        }

        @Keep
        public void setArrowAlpha(int i) {
            this.this$0.arrowAlphas[this.index] = i;
        }
    }
}
