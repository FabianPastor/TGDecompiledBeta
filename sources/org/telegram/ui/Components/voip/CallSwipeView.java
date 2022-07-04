package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class CallSwipeView extends View {
    /* access modifiers changed from: private */
    public boolean animatingArrows = false;
    private Path arrow = new Path();
    /* access modifiers changed from: private */
    public int[] arrowAlphas = {64, 64, 64};
    /* access modifiers changed from: private */
    public AnimatorSet arrowAnim;
    private Paint arrowsPaint;
    /* access modifiers changed from: private */
    public boolean canceled = false;
    private boolean dragFromRight;
    private float dragStartX;
    private boolean dragging = false;
    private Listener listener;
    private Paint pullBgPaint;
    private RectF tmpRect = new RectF();
    private View viewToDrag;

    public interface Listener {
        void onDragCancel();

        void onDragComplete();

        void onDragStart();
    }

    public CallSwipeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setClickable(true);
        Paint paint = new Paint(1);
        this.arrowsPaint = paint;
        paint.setColor(-1);
        this.arrowsPaint.setStyle(Paint.Style.STROKE);
        this.arrowsPaint.setStrokeWidth((float) AndroidUtilities.dp(2.5f));
        this.pullBgPaint = new Paint(1);
        ArrayList<Animator> anims = new ArrayList<>();
        for (int i = 0; i < this.arrowAlphas.length; i++) {
            ObjectAnimator anim = ObjectAnimator.ofInt(new ArrowAnimWrapper(i), "arrowAlpha", new int[]{64, 255, 64});
            anim.setDuration(700);
            anim.setStartDelay((long) (i * 200));
            anims.add(anim);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.arrowAnim = animatorSet;
        animatorSet.playTogether(anims);
        this.arrowAnim.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    if (CallSwipeView.this.arrowAnim != null) {
                        CallSwipeView.this.arrowAnim.start();
                    }
                }
            };
            private long startTime;

            public void onAnimationEnd(Animator animation) {
                if (System.currentTimeMillis() - this.startTime < animation.getDuration() / 4) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("Not repeating animation because previous loop was too fast");
                    }
                } else if (!CallSwipeView.this.canceled && CallSwipeView.this.animatingArrows) {
                    CallSwipeView.this.post(this.restarter);
                }
            }

            public void onAnimationCancel(Animator animation) {
                boolean unused = CallSwipeView.this.canceled = true;
            }

            public void onAnimationStart(Animator animation) {
                this.startTime = System.currentTimeMillis();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatorSet animatorSet = this.arrowAnim;
        if (animatorSet != null) {
            this.canceled = true;
            animatorSet.cancel();
            this.arrowAnim = null;
        }
    }

    public void setColor(int color) {
        this.pullBgPaint.setColor(color);
        this.pullBgPaint.setAlpha(178);
    }

    public void setViewToDrag(View viewToDrag2, boolean dragFromRight2) {
        this.viewToDrag = viewToDrag2;
        this.dragFromRight = dragFromRight2;
        updateArrowPath();
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    private int getDraggedViewWidth() {
        return getHeight();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (!isEnabled() || am.isTouchExplorationEnabled()) {
            return super.onTouchEvent(ev);
        }
        if (ev.getAction() != 0) {
            float f = 0.0f;
            if (ev.getAction() == 2) {
                View view = this.viewToDrag;
                float f2 = this.dragFromRight ? (float) (-(getWidth() - getDraggedViewWidth())) : 0.0f;
                float x = ev.getX() - this.dragStartX;
                if (!this.dragFromRight) {
                    f = (float) (getWidth() - getDraggedViewWidth());
                }
                view.setTranslationX(Math.max(f2, Math.min(x, f)));
                invalidate();
            } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                if (Math.abs(this.viewToDrag.getTranslationX()) < ((float) (getWidth() - getDraggedViewWidth())) || ev.getAction() != 1) {
                    this.listener.onDragCancel();
                    this.viewToDrag.animate().translationX(0.0f).setDuration(200).start();
                    invalidate();
                    startAnimatingArrows();
                    this.dragging = false;
                } else {
                    this.listener.onDragComplete();
                }
            }
        } else if ((!this.dragFromRight && ev.getX() < ((float) getDraggedViewWidth())) || (this.dragFromRight && ev.getX() > ((float) (getWidth() - getDraggedViewWidth())))) {
            this.dragging = true;
            this.dragStartX = ev.getX();
            getParent().requestDisallowInterceptTouchEvent(true);
            this.listener.onDragStart();
            stopAnimatingArrows();
        }
        return this.dragging;
    }

    public void stopAnimatingArrows() {
        this.animatingArrows = false;
    }

    public void startAnimatingArrows() {
        AnimatorSet animatorSet;
        if (!this.animatingArrows && (animatorSet = this.arrowAnim) != null) {
            this.animatingArrows = true;
            if (animatorSet != null) {
                animatorSet.start();
            }
        }
    }

    public void reset() {
        if (this.arrowAnim != null && !this.canceled) {
            this.listener.onDragCancel();
            this.viewToDrag.animate().translationX(0.0f).setDuration(200).start();
            invalidate();
            startAnimatingArrows();
            this.dragging = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.viewToDrag.getTranslationX() != 0.0f) {
            if (this.dragFromRight) {
                this.tmpRect.set((((float) getWidth()) + this.viewToDrag.getTranslationX()) - ((float) getDraggedViewWidth()), 0.0f, (float) getWidth(), (float) getHeight());
            } else {
                this.tmpRect.set(0.0f, 0.0f, this.viewToDrag.getTranslationX() + ((float) getDraggedViewWidth()), (float) getHeight());
            }
            canvas.drawRoundRect(this.tmpRect, (float) (getHeight() / 2), (float) (getHeight() / 2), this.pullBgPaint);
        }
        canvas.save();
        if (this.dragFromRight) {
            canvas.translate((float) ((getWidth() - getHeight()) - AndroidUtilities.dp(18.0f)), (float) (getHeight() / 2));
        } else {
            canvas.translate((float) (getHeight() + AndroidUtilities.dp(12.0f)), (float) (getHeight() / 2));
        }
        float offsetX = Math.abs(this.viewToDrag.getTranslationX());
        for (int i = 0; i < 3; i++) {
            float masterAlpha = 1.0f;
            float f = 16.0f;
            if (offsetX > ((float) AndroidUtilities.dp((float) (i * 16)))) {
                masterAlpha = 1.0f - Math.min(1.0f, Math.max(0.0f, (offsetX - ((float) (AndroidUtilities.dp(16.0f) * i))) / ((float) AndroidUtilities.dp(16.0f))));
            }
            this.arrowsPaint.setAlpha(Math.round(((float) this.arrowAlphas[i]) * masterAlpha));
            canvas.drawPath(this.arrow, this.arrowsPaint);
            if (this.dragFromRight) {
                f = -16.0f;
            }
            canvas.translate((float) AndroidUtilities.dp(f), 0.0f);
        }
        canvas.restore();
        invalidate();
    }

    private void updateArrowPath() {
        this.arrow.reset();
        int size = AndroidUtilities.dp(6.0f);
        if (this.dragFromRight) {
            this.arrow.moveTo((float) size, (float) (-size));
            this.arrow.lineTo(0.0f, 0.0f);
            this.arrow.lineTo((float) size, (float) size);
            return;
        }
        this.arrow.moveTo(0.0f, (float) (-size));
        this.arrow.lineTo((float) size, 0.0f);
        this.arrow.lineTo(0.0f, (float) size);
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent ev) {
        if (isEnabled() && ev.getEventType() == 1) {
            this.listener.onDragComplete();
        }
        super.onPopulateAccessibilityEvent(ev);
    }

    private class ArrowAnimWrapper {
        private int index;

        public ArrowAnimWrapper(int value) {
            this.index = value;
        }

        public int getArrowAlpha() {
            return CallSwipeView.this.arrowAlphas[this.index];
        }

        public void setArrowAlpha(int value) {
            CallSwipeView.this.arrowAlphas[this.index] = value;
        }
    }
}
