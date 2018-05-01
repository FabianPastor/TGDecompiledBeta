package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;

public class ShutterButton extends View {
    private static final int LONG_PRESS_TIME = 800;
    private ShutterButtonDelegate delegate;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastUpdateTime;
    private Runnable longPressed = new C13041();
    private boolean pressed;
    private boolean processRelease;
    private Paint redPaint;
    private float redProgress;
    private Drawable shadowDrawable = getResources().getDrawable(C0446R.drawable.camera_btn);
    private State state;
    private long totalTime;
    private Paint whitePaint = new Paint(1);

    /* renamed from: org.telegram.ui.Components.ShutterButton$1 */
    class C13041 implements Runnable {
        C13041() {
        }

        public void run() {
            if (ShutterButton.this.delegate != null && !ShutterButton.this.delegate.shutterLongPressed()) {
                ShutterButton.this.processRelease = false;
            }
        }
    }

    public interface ShutterButtonDelegate {
        void shutterCancel();

        boolean shutterLongPressed();

        void shutterReleased();
    }

    public enum State {
        DEFAULT,
        RECORDING
    }

    public ShutterButton(Context context) {
        super(context);
        this.whitePaint.setStyle(Style.FILL);
        this.whitePaint.setColor(-1);
        this.redPaint = new Paint(1);
        this.redPaint.setStyle(Style.FILL);
        this.redPaint.setColor(-3324089);
        this.state = State.DEFAULT;
    }

    public void setDelegate(ShutterButtonDelegate shutterButtonDelegate) {
        this.delegate = shutterButtonDelegate;
    }

    public ShutterButtonDelegate getDelegate() {
        return this.delegate;
    }

    private void setHighlighted(boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (z) {
            z = new Animator[2];
            z[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.06f});
            z[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.06f});
            animatorSet.playTogether(z);
        } else {
            z = new Animator[2];
            z[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.0f});
            z[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.0f});
            animatorSet.playTogether(z);
            animatorSet.setStartDelay(40);
        }
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.start();
    }

    public void setScaleX(float f) {
        super.setScaleX(f);
        invalidate();
    }

    public State getState() {
        return this.state;
    }

    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        this.shadowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(36.0f), measuredHeight - AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f) + measuredWidth, AndroidUtilities.dp(36.0f) + measuredHeight);
        this.shadowDrawable.draw(canvas);
        if (!this.pressed) {
            if (getScaleX() == 1.0f) {
                if (this.redProgress != null) {
                    this.redProgress = 0.0f;
                    return;
                }
                return;
            }
        }
        float scaleX = (getScaleX() - 1.0f) / 0.06f;
        this.whitePaint.setAlpha((int) (255.0f * scaleX));
        float f = (float) measuredWidth;
        float f2 = (float) measuredHeight;
        canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(26.0f), this.whitePaint);
        if (this.state == State.RECORDING) {
            if (this.redProgress != 1.0f) {
                long abs = Math.abs(System.currentTimeMillis() - this.lastUpdateTime);
                if (abs > 17) {
                    abs = 17;
                }
                this.totalTime += abs;
                if (this.totalTime > 120) {
                    this.totalTime = 120;
                }
                this.redProgress = this.interpolator.getInterpolation(((float) this.totalTime) / 120.0f);
                invalidate();
            }
            canvas.drawCircle(f, f2, (((float) AndroidUtilities.dp(26.0f)) * scaleX) * this.redProgress, this.redPaint);
        } else if (this.redProgress != 0.0f) {
            canvas.drawCircle(f, f2, ((float) AndroidUtilities.dp(26.0f)) * scaleX, this.redPaint);
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float x2 = motionEvent.getX();
        switch (motionEvent.getAction()) {
            case null:
                AndroidUtilities.runOnUIThread(this.longPressed, 800);
                this.pressed = true;
                this.processRelease = true;
                setHighlighted(true);
                break;
            case 1:
                setHighlighted(false);
                AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                if (this.processRelease != null && x >= 0.0f && x2 >= 0.0f && x <= ((float) getMeasuredWidth()) && x2 <= ((float) getMeasuredHeight())) {
                    this.delegate.shutterReleased();
                    break;
                }
            case 2:
                if (x < 0.0f || x2 < 0.0f || x > ((float) getMeasuredWidth()) || x2 > ((float) getMeasuredHeight())) {
                    AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                    if (this.state == State.RECORDING) {
                        setHighlighted(false);
                        this.delegate.shutterCancel();
                        setState(State.DEFAULT, true);
                        break;
                    }
                }
                break;
            case 3:
                setHighlighted(false);
                this.pressed = false;
                break;
            default:
                break;
        }
        return true;
    }

    public void setState(State state, boolean z) {
        if (this.state != state) {
            this.state = state;
            if (z) {
                this.lastUpdateTime = System.currentTimeMillis();
                this.totalTime = 0;
                if (this.state != State.RECORDING) {
                    this.redProgress = 0.0f;
                }
            } else if (this.state == State.RECORDING) {
                this.redProgress = 1.0f;
            } else {
                this.redProgress = 0.0f;
            }
            invalidate();
        }
    }
}
