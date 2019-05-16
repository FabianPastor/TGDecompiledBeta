package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class CallSwipeView extends View {
    private boolean animatingArrows = false;
    private Path arrow = new Path();
    private int[] arrowAlphas = new int[]{64, 64, 64};
    private AnimatorSet arrowAnim;
    private Paint arrowsPaint;
    private boolean canceled = false;
    private boolean dragFromRight;
    private float dragStartX;
    private boolean dragging = false;
    private Listener listener;
    private Paint pullBgPaint;
    private RectF tmpRect = new RectF();
    private View viewToDrag;

    private class ArrowAnimWrapper {
        private int index;

        public ArrowAnimWrapper(int i) {
            this.index = i;
        }

        public int getArrowAlpha() {
            return CallSwipeView.this.arrowAlphas[this.index];
        }

        public void setArrowAlpha(int i) {
            CallSwipeView.this.arrowAlphas[this.index] = i;
        }
    }

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
        this.arrowsPaint = new Paint(1);
        this.arrowsPaint.setColor(-1);
        this.arrowsPaint.setStyle(Style.STROKE);
        this.arrowsPaint.setStrokeWidth((float) AndroidUtilities.dp(2.5f));
        this.pullBgPaint = new Paint(1);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.arrowAlphas.length; i++) {
            ObjectAnimator ofInt = ObjectAnimator.ofInt(new ArrowAnimWrapper(i), "arrowAlpha", new int[]{64, 255, 64});
            ofInt.setDuration(700);
            ofInt.setStartDelay((long) (i * 200));
            arrayList.add(ofInt);
        }
        this.arrowAnim = new AnimatorSet();
        this.arrowAnim.playTogether(arrayList);
        this.arrowAnim.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    if (CallSwipeView.this.arrowAnim != null) {
                        CallSwipeView.this.arrowAnim.start();
                    }
                }
            };
            private long startTime;

            public void onAnimationEnd(Animator animator) {
                if (System.currentTimeMillis() - this.startTime < animator.getDuration() / 4) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("Not repeating animation because previous loop was too fast");
                    }
                    return;
                }
                if (!CallSwipeView.this.canceled && CallSwipeView.this.animatingArrows) {
                    CallSwipeView.this.post(this.restarter);
                }
            }

            public void onAnimationCancel(Animator animator) {
                CallSwipeView.this.canceled = true;
            }

            public void onAnimationStart(Animator animator) {
                this.startTime = System.currentTimeMillis();
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatorSet animatorSet = this.arrowAnim;
        if (animatorSet != null) {
            this.canceled = true;
            animatorSet.cancel();
            this.arrowAnim = null;
        }
    }

    public void setColor(int i) {
        this.pullBgPaint.setColor(i);
        this.pullBgPaint.setAlpha(178);
    }

    public void setViewToDrag(View view, boolean z) {
        this.viewToDrag = view;
        this.dragFromRight = z;
        updateArrowPath();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private int getDraggedViewWidth() {
        return getHeight();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        if (motionEvent.getAction() != 0) {
            float f = 0.0f;
            if (motionEvent.getAction() == 2) {
                View view = this.viewToDrag;
                float f2 = this.dragFromRight ? (float) (-(getWidth() - getDraggedViewWidth())) : 0.0f;
                float x = motionEvent.getX() - this.dragStartX;
                if (!this.dragFromRight) {
                    f = (float) (getWidth() - getDraggedViewWidth());
                }
                view.setTranslationX(Math.max(f2, Math.min(x, f)));
                invalidate();
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (Math.abs(this.viewToDrag.getTranslationX()) < ((float) (getWidth() - getDraggedViewWidth())) || motionEvent.getAction() != 1) {
                    this.listener.onDragCancel();
                    this.viewToDrag.animate().translationX(0.0f).setDuration(200).start();
                    invalidate();
                    startAnimatingArrows();
                    this.dragging = false;
                } else {
                    this.listener.onDragComplete();
                }
            }
        } else if ((!this.dragFromRight && motionEvent.getX() < ((float) getDraggedViewWidth())) || (this.dragFromRight && motionEvent.getX() > ((float) (getWidth() - getDraggedViewWidth())))) {
            this.dragging = true;
            this.dragStartX = motionEvent.getX();
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
        if (!this.animatingArrows) {
            AnimatorSet animatorSet = this.arrowAnim;
            if (animatorSet != null) {
                this.animatingArrows = true;
                if (animatorSet != null) {
                    animatorSet.start();
                }
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

    /* Access modifiers changed, original: protected */
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
        float abs = Math.abs(this.viewToDrag.getTranslationX());
        for (int i = 0; i < 3; i++) {
            float f = 16.0f;
            float f2 = 1.0f;
            if (abs > ((float) AndroidUtilities.dp((float) (i * 16)))) {
                f2 = 1.0f - Math.min(1.0f, Math.max(0.0f, (abs - ((float) (AndroidUtilities.dp(16.0f) * i))) / ((float) AndroidUtilities.dp(16.0f))));
            }
            this.arrowsPaint.setAlpha(Math.round(((float) this.arrowAlphas[i]) * f2));
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
        int dp = AndroidUtilities.dp(6.0f);
        if (this.dragFromRight) {
            float f = (float) dp;
            this.arrow.moveTo(f, (float) (-dp));
            this.arrow.lineTo(0.0f, 0.0f);
            this.arrow.lineTo(f, f);
            return;
        }
        this.arrow.moveTo(0.0f, (float) (-dp));
        float f2 = (float) dp;
        this.arrow.lineTo(f2, 0.0f);
        this.arrow.lineTo(0.0f, f2);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(16);
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (i == 16 && isEnabled()) {
            this.listener.onDragComplete();
        }
        return super.performAccessibilityAction(i, bundle);
    }
}
