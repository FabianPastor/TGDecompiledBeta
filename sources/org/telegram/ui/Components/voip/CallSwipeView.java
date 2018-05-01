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
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

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

    /* renamed from: org.telegram.ui.Components.voip.CallSwipeView$1 */
    class C13511 extends AnimatorListenerAdapter {
        private Runnable restarter = new C13501();
        private long startTime;

        /* renamed from: org.telegram.ui.Components.voip.CallSwipeView$1$1 */
        class C13501 implements Runnable {
            C13501() {
            }

            public void run() {
                if (CallSwipeView.this.arrowAnim != null) {
                    CallSwipeView.this.arrowAnim.start();
                }
            }
        }

        C13511() {
        }

        public void onAnimationEnd(Animator animator) {
            if (System.currentTimeMillis() - this.startTime < animator.getDuration() / 4) {
                if (BuildVars.LOGS_ENABLED != null) {
                    FileLog.m4w("Not repeating animation because previous loop was too fast");
                }
                return;
            }
            if (CallSwipeView.this.canceled == null && CallSwipeView.this.animatingArrows != null) {
                CallSwipeView.this.post(this.restarter);
            }
        }

        public void onAnimationCancel(Animator animator) {
            CallSwipeView.this.canceled = true;
        }

        public void onAnimationStart(Animator animator) {
            this.startTime = System.currentTimeMillis();
        }
    }

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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
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
        Collection arrayList = new ArrayList();
        for (int i = 0; i < this.arrowAlphas.length; i++) {
            ObjectAnimator ofInt = ObjectAnimator.ofInt(new ArrowAnimWrapper(i), "arrowAlpha", new int[]{64, 255, 64});
            ofInt.setDuration(700);
            ofInt.setStartDelay((long) (Callback.DEFAULT_DRAG_ANIMATION_DURATION * i));
            arrayList.add(ofInt);
        }
        this.arrowAnim = new AnimatorSet();
        this.arrowAnim.playTogether(arrayList);
        this.arrowAnim.addListener(new C13511());
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.arrowAnim != null) {
            this.canceled = true;
            this.arrowAnim.cancel();
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
                motionEvent = motionEvent.getX() - this.dragStartX;
                if (!this.dragFromRight) {
                    f = (float) (getWidth() - getDraggedViewWidth());
                }
                view.setTranslationX(Math.max(f2, Math.min(motionEvent, f)));
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
            if (this.arrowAnim != null) {
                this.animatingArrows = true;
                if (this.arrowAnim != null) {
                    this.arrowAnim.start();
                }
            }
        }
    }

    public void reset() {
        if (this.arrowAnim != null) {
            if (!this.canceled) {
                this.listener.onDragCancel();
                this.viewToDrag.animate().translationX(0.0f).setDuration(200).start();
                invalidate();
                startAnimatingArrows();
                this.dragging = false;
            }
        }
    }

    protected void onDraw(Canvas canvas) {
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
            if (abs > ((float) AndroidUtilities.dp((float) (16 * i)))) {
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
}
