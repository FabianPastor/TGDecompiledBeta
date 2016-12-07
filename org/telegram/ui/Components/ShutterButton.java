package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class ShutterButton extends View {
    private static final int LONG_PRESS_TIME = 220;
    private final float STEP_SIZE = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) (this.VALUES.length - 1)));
    private final float[] VALUES = new float[]{0.0f, 2.0E-4f, 9.0E-4f, 0.0019f, 0.0036f, 0.0059f, 0.0086f, 0.0119f, 0.0157f, 0.0209f, 0.0257f, 0.0321f, 0.0392f, 0.0469f, 0.0566f, 0.0656f, 0.0768f, 0.0887f, 0.1033f, 0.1186f, 0.1349f, 0.1519f, 0.1696f, 0.1928f, 0.2121f, 0.237f, 0.2627f, 0.2892f, 0.3109f, 0.3386f, 0.3667f, 0.3952f, 0.4241f, 0.4474f, 0.4766f, 0.5f, 0.5234f, 0.5468f, 0.5701f, 0.5933f, 0.6134f, 0.6333f, 0.6531f, 0.6698f, 0.6891f, 0.7054f, 0.7214f, 0.7346f, 0.7502f, 0.763f, 0.7756f, 0.7879f, DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, 0.8107f, 0.8212f, 0.8326f, 0.8415f, 0.8503f, 0.8588f, 0.8672f, 0.8754f, 0.8833f, 0.8911f, 0.8977f, 0.9041f, 0.9113f, 0.9165f, 0.9232f, 0.9281f, 0.9328f, 0.9382f, 0.9434f, 0.9476f, 0.9518f, 0.9557f, 0.9596f, 0.9632f, 0.9662f, 0.9695f, 0.9722f, 0.9753f, 0.9777f, 0.9805f, 0.9826f, 0.9847f, 0.9866f, 0.9884f, 0.9901f, 0.9917f, 0.9931f, 0.9944f, 0.9955f, 0.9964f, 0.9973f, 0.9981f, 0.9986f, 0.9992f, 0.9995f, 0.9998f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT};
    private Paint addWhitePaint;
    private boolean animatingStateChange;
    private float animationProgress;
    private Paint bluePaint;
    private boolean clickEnabled = true;
    public ShutterButtonDelegate delegate;
    boolean fadingAway;
    private final Gusterpolator guesterpolator = new Gusterpolator();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private Paint lightBluePaint;
    private boolean longPress;
    Runnable longPressed = new Runnable() {
        public void run() {
            if (ShutterButton.this.delegate != null) {
                ShutterButton.this.delegate.shutterLongPressed();
            }
        }
    };
    private State previousState;
    private RectF rect;
    private RectF rectF = new RectF();
    private RectF rectF11 = new RectF();
    private RectF rectF12 = new RectF();
    private RectF rectF2 = new RectF();
    private RectF rectF3 = new RectF();
    private RectF rectF4 = new RectF();
    private RectF rectF5 = new RectF();
    private RectF rectF6 = new RectF();
    private RectF rectF7 = new RectF();
    private RectF rectF8 = new RectF();
    private RectF rectF9 = new RectF();
    private Paint redPaint;
    private Drawable shadowDrawable = getResources().getDrawable(R.drawable.shutter_shadow);
    private State state;
    private Paint whitePaint = new Paint(1);

    private class Gusterpolator implements TimeInterpolator {
        private Gusterpolator() {
        }

        public float getInterpolation(float f) {
            if (f >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                return DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            if (f <= 0.0f) {
                return 0.0f;
            }
            int min = Math.min((int) (((float) (ShutterButton.this.VALUES.length - 1)) * f), ShutterButton.this.VALUES.length - 2);
            return ((ShutterButton.this.VALUES[min + 1] - ShutterButton.this.VALUES[min]) * ((f - (((float) min) * ShutterButton.this.STEP_SIZE)) / ShutterButton.this.STEP_SIZE)) + ShutterButton.this.VALUES[min];
        }
    }

    public interface ShutterButtonDelegate {
        void shutterCancel();

        void shutterLongPressed();

        void shutterReleased();
    }

    public enum State {
        DEFAULT,
        VIDEO,
        RECORDING
    }

    public ShutterButton(Context context) {
        super(context);
        this.whitePaint.setStyle(Style.FILL);
        this.whitePaint.setColor(-1);
        this.bluePaint = new Paint(1);
        this.bluePaint.setStyle(Style.FILL);
        this.bluePaint.setColor(-12740893);
        this.lightBluePaint = new Paint(1);
        this.lightBluePaint.setStyle(Style.FILL);
        this.lightBluePaint.setColor(-11491083);
        this.redPaint = new Paint(1);
        this.redPaint.setStyle(Style.FILL);
        this.redPaint.setColor(-3324089);
        this.addWhitePaint = new Paint(1);
        this.addWhitePaint.setStyle(Style.FILL);
        this.addWhitePaint.setColor(-1);
        this.state = State.DEFAULT;
    }

    public void setDelegate(ShutterButtonDelegate shutterButtonDelegate) {
        this.delegate = shutterButtonDelegate;
    }

    private void setHighlighted(boolean value) {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr;
        if (value) {
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.06f});
            animatorArr[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.06f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(60);
        } else {
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorArr[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorSet.playTogether(animatorArr);
            animatorSet.setStartDelay(40);
            animatorSet.setDuration(120);
        }
        animatorSet.start();
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public State getState() {
        return this.state;
    }

    private RectF interpolateRect(RectF rectF, RectF rectF2, float f) {
        return new RectF(rectF.left + ((rectF2.left - rectF.left) * f), rectF.top + ((rectF2.top - rectF.top) * f), rectF.right + ((rectF2.right - rectF.right) * f), rectF.bottom + ((rectF2.bottom - rectF.bottom) * f));
    }

    private int interpolateInt(int i, int i2, float f) {
        return (int) (((float) i) + (((float) (i2 - i)) * f));
    }

    private RectF scaleRect(RectF rectF, float f) {
        float width = rectF.width();
        float width2 = rectF.width() * f;
        float height2 = rectF.height() * f;
        width = (rectF.left + (width / 2.0f)) - (width2 / 2.0f);
        float height = ((rectF.height() / 2.0f) + rectF.top) - (height2 / 2.0f);
        return new RectF(width, height, width2 + width, height2 + height);
    }

    protected void onDraw(Canvas canvas) {
        canvas.translate(0.0f, (float) AndroidUtilities.dp(8.0f));
        if (this.animatingStateChange) {
            float f;
            int dp;
            int dp2;
            int dp3;
            float f2;
            int dp4;
            int dp5;
            int i;
            int interpolateInt;
            int interpolateInt2;
            if (this.previousState == State.DEFAULT && this.state == State.VIDEO) {
                float f3;
                float f4;
                float f5;
                float f6;
                this.rectF2.set((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(68.0f));
                this.rectF3.set((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(72.0f), (float) AndroidUtilities.dp(64.0f));
                this.rectF4.set((float) AndroidUtilities.dp(23.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(61.0f), (float) AndroidUtilities.dp(53.0f));
                f = this.animationProgress;
                dp = AndroidUtilities.dp(34.0f);
                dp2 = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                dp3 = AndroidUtilities.dp(19.0f);
                if (this.state == State.VIDEO) {
                    f2 = this.animationProgress / DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD;
                    if (f2 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    }
                    this.bluePaint.setAlpha((int) (255.0f - (255.0f * f2)));
                    this.lightBluePaint.setAlpha((int) (255.0f - (255.0f * f2)));
                    f = (this.animationProgress - DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) / DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                    if (f < 0.0f) {
                        f = 0.0f;
                    }
                    this.rectF5.set(0.0f, 0.0f, (float) AndroidUtilities.dp(84.0f), (float) AndroidUtilities.dp(68.0f));
                    this.rectF6.set((float) AndroidUtilities.dp(4.0f), this.rectF3.top, (float) AndroidUtilities.dp(80.0f), this.rectF3.bottom);
                    this.rectF7.set((float) AndroidUtilities.dp(36.0f), (float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(48.0f), (float) AndroidUtilities.dp(40.0f));
                    dp4 = AndroidUtilities.dp(6.0f);
                    f3 = f2;
                    f4 = f;
                    dp5 = AndroidUtilities.dp(8.0f);
                    this.rectF8 = this.rectF6;
                    f5 = f2;
                    RectF rectF10 = this.rectF7;
                    f6 = f2;
                    i = dp4;
                    this.rectF9 = this.rectF5;
                    this.rectF5 = rectF10;
                } else {
                    this.rectF9 = this.rectF2;
                    this.rectF8 = this.rectF3;
                    this.rectF5 = this.rectF4;
                    f5 = f;
                    f6 = f;
                    f3 = f;
                    f4 = f;
                    dp5 = 0;
                    i = dp3;
                }
                this.rectF9 = interpolateRect(this.rectF2, this.rectF9, f6);
                interpolateInt = interpolateInt(dp, dp, f6);
                this.rectF8 = interpolateRect(this.rectF3, this.rectF8, f5);
                interpolateInt2 = interpolateInt(dp2, dp2, f5);
                this.rectF5 = interpolateRect(this.rectF4, this.rectF5, f3);
                i = interpolateInt(dp3, i, f3);
                canvas.drawRoundRect(this.rectF9, (float) interpolateInt, (float) interpolateInt, this.whitePaint);
                canvas.drawRoundRect(this.rectF8, (float) interpolateInt2, (float) interpolateInt2, this.bluePaint);
                canvas.drawRoundRect(this.rectF5, (float) i, (float) i, this.lightBluePaint);
                i = interpolateInt(0, dp5, f4);
                if (dp5 > 0 && i > 0) {
                    canvas.drawCircle((float) (AndroidUtilities.dp(84.0f) / 2), (float) (AndroidUtilities.dp(68.0f) / 2), (float) i, this.redPaint);
                }
            }
            int width;
            int dp6;
            RectF interpolateRect;
            if (this.previousState == State.DEFAULT && this.state == State.RECORDING) {
                f2 = this.animationProgress;
                this.rectF.set((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(68.0f));
                this.rectF9 = scaleRect(this.rectF, 0.25f);
                dp3 = AndroidUtilities.dp(34.0f);
                width = (int) (this.rectF9.width() / 2.0f);
                this.rectF5.set((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(72.0f), (float) AndroidUtilities.dp(64.0f));
                this.rectF2 = scaleRect(this.rectF5, 0.25f);
                interpolateInt2 = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                int width2 = (int) (this.rectF2.width() / 2.0f);
                this.rectF7.set((float) AndroidUtilities.dp(23.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(61.0f), (float) AndroidUtilities.dp(53.0f));
                this.rectF4 = scaleRect(this.rectF7, 0.25f);
                dp6 = AndroidUtilities.dp(19.0f);
                int width3 = (int) (this.rectF4.width() / 2.0f);
                dp = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                dp2 = AndroidUtilities.dp(34.0f);
                this.rectF11.set((float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(56.0f), (float) AndroidUtilities.dp(48.0f));
                RectF scaleRect = scaleRect(this.rectF11, 0.25f);
                int dp7 = AndroidUtilities.dp(5.0f);
                this.rectF = interpolateRect(this.rectF, this.rectF9, f2);
                dp4 = interpolateInt(dp3, width, f2);
                interpolateRect = interpolateRect(this.rectF5, this.rectF2, f2);
                width = interpolateInt(interpolateInt2, width2, f2);
                this.rectF5 = interpolateRect(this.rectF7, this.rectF4, f2);
                interpolateInt = interpolateInt(dp6, width3, f2);
                canvas.drawRoundRect(this.rectF, (float) dp4, (float) dp4, this.whitePaint);
                canvas.drawRoundRect(interpolateRect, (float) width, (float) width, this.bluePaint);
                canvas.drawRoundRect(this.rectF5, (float) interpolateInt, (float) interpolateInt, this.lightBluePaint);
                dp5 = interpolateInt(dp, dp2, f2);
                if (dp2 > 0 && dp5 > 0) {
                    canvas.drawCircle((float) (AndroidUtilities.dp(84.0f) / 2), (float) (AndroidUtilities.dp(68.0f) / 2), (float) dp5, this.redPaint);
                }
                canvas.drawRoundRect(interpolateRect(scaleRect, this.rectF11, f2), (float) dp7, (float) dp7, this.addWhitePaint);
                return;
            }
            if (this.previousState == State.VIDEO) {
                int dp8;
                if (this.state == State.RECORDING) {
                    f2 = this.animationProgress;
                    f = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Math.max(0.0f, (f2 - 0.7f) / 0.3f));
                    float min = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Math.max(0.0f, (f2 - 0.6f) / 0.4f));
                    float min2 = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Math.max(0.0f, f2));
                    this.rectF8.set(0.0f, 0.0f, (float) AndroidUtilities.dp(84.0f), (float) AndroidUtilities.dp(68.0f));
                    int dp9 = AndroidUtilities.dp(34.0f);
                    interpolateInt = AndroidUtilities.dp(8.0f);
                    interpolateInt2 = AndroidUtilities.dp(34.0f);
                    this.rectF3.set((float) AndroidUtilities.dp(36.0f), (float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(48.0f), (float) AndroidUtilities.dp(40.0f));
                    this.rectF7.set((float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(56.0f), (float) AndroidUtilities.dp(48.0f));
                    dp8 = AndroidUtilities.dp(5.0f);
                    this.rectF8 = interpolateRect(this.rectF8, this.rectF8, f);
                    dp9 = interpolateInt(dp9, dp9, f);
                    this.whitePaint.setAlpha((int) (255.0f - (255.0f * f)));
                    i = interpolateInt(interpolateInt, interpolateInt2, f2);
                    this.rectF = interpolateRect(this.rectF3, this.rectF7, min);
                    dp4 = interpolateInt(0, dp8, min);
                    this.addWhitePaint.setAlpha((int) (255.0f * min2));
                    canvas.drawRoundRect(this.rectF8, (float) dp9, (float) dp9, this.whitePaint);
                    this.rectF8.set((float) ((AndroidUtilities.dp(84.0f) / 2) - i), (float) ((AndroidUtilities.dp(68.0f) / 2) - i), (float) ((AndroidUtilities.dp(84.0f) / 2) + i), (float) ((AndroidUtilities.dp(68.0f) / 2) + i));
                    this.rectF8.inset((float) AndroidUtilities.dp(-4.0f), (float) AndroidUtilities.dp(-4.0f));
                    this.shadowDrawable.setAlpha((int) (255.0f * min2));
                    this.shadowDrawable.setBounds((int) this.rectF8.left, (int) this.rectF8.top, (int) this.rectF8.right, (int) this.rectF8.bottom);
                    this.shadowDrawable.draw(canvas);
                    canvas.drawCircle((float) (AndroidUtilities.dp(84.0f) / 2), (float) (AndroidUtilities.dp(68.0f) / 2), (float) i, this.redPaint);
                    canvas.drawRoundRect(this.rectF, (float) dp4, (float) dp4, this.addWhitePaint);
                    return;
                }
                if (this.state == State.DEFAULT) {
                    f2 = this.animationProgress;
                    this.rectF.set(0.0f, 0.0f, (float) AndroidUtilities.dp(84.0f), (float) AndroidUtilities.dp(68.0f));
                    this.rectF9.set((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(68.0f));
                    dp3 = AndroidUtilities.dp(34.0f);
                    this.rectF8.set((float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(80.0f), (float) AndroidUtilities.dp(64.0f));
                    this.rectF5.set((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(72.0f), (float) AndroidUtilities.dp(64.0f));
                    interpolateInt = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                    this.rectF6.set((float) AndroidUtilities.dp(36.0f), (float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(48.0f), (float) AndroidUtilities.dp(40.0f));
                    this.rectF3.set((float) AndroidUtilities.dp(23.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(61.0f), (float) AndroidUtilities.dp(53.0f));
                    int dp10 = AndroidUtilities.dp(6.0f);
                    dp8 = AndroidUtilities.dp(19.0f);
                    dp6 = AndroidUtilities.dp(8.0f);
                    this.bluePaint.setAlpha((int) (255.0f * f2));
                    this.lightBluePaint.setAlpha((int) (255.0f * f2));
                    this.redPaint.setAlpha(255 - ((int) (255.0f * f2)));
                    this.rectF = interpolateRect(this.rectF, this.rectF9, f2);
                    dp4 = interpolateInt(dp3, dp3, f2);
                    interpolateRect = interpolateRect(this.rectF8, this.rectF5, f2);
                    width = interpolateInt(interpolateInt, interpolateInt, f2);
                    this.rectF5 = interpolateRect(this.rectF6, this.rectF3, f2);
                    interpolateInt = interpolateInt(dp10, dp8, f2);
                    canvas.drawRoundRect(this.rectF, (float) dp4, (float) dp4, this.whitePaint);
                    canvas.drawRoundRect(interpolateRect, (float) width, (float) width, this.bluePaint);
                    canvas.drawRoundRect(this.rectF5, (float) interpolateInt, (float) interpolateInt, this.lightBluePaint);
                    i = interpolateInt(dp6, 0, f2);
                    if (i > 0) {
                        canvas.drawCircle((float) (AndroidUtilities.dp(84.0f) / 2), (float) (AndroidUtilities.dp(68.0f) / 2), (float) i, this.redPaint);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        switch (this.state) {
            case DEFAULT:
                this.rectF12.set((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(68.0f));
                canvas.drawRoundRect(this.rectF12, (float) AndroidUtilities.dp(34.0f), (float) AndroidUtilities.dp(34.0f), this.whitePaint);
                this.rectF12.set((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(72.0f), (float) AndroidUtilities.dp(64.0f));
                canvas.drawRoundRect(this.rectF12, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), this.bluePaint);
                this.rectF12.set((float) AndroidUtilities.dp(23.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(61.0f), (float) AndroidUtilities.dp(53.0f));
                canvas.drawRoundRect(this.rectF12, (float) AndroidUtilities.dp(19.0f), (float) AndroidUtilities.dp(19.0f), this.lightBluePaint);
                return;
            case VIDEO:
                this.rectF12.set(0.0f, 0.0f, (float) AndroidUtilities.dp(84.0f), (float) AndroidUtilities.dp(68.0f));
                canvas.drawRoundRect(this.rectF12, (float) AndroidUtilities.dp(34.0f), (float) AndroidUtilities.dp(34.0f), this.whitePaint);
                canvas.drawCircle((float) (AndroidUtilities.dp(84.0f) / 2), (float) (AndroidUtilities.dp(68.0f) / 2), (float) AndroidUtilities.dp(8.0f), this.redPaint);
                return;
            case RECORDING:
                this.rectF12.set((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(68.0f));
                this.rectF.set(this.rectF12);
                this.rectF.inset((float) AndroidUtilities.dp(-4.0f), (float) AndroidUtilities.dp(-4.0f));
                this.shadowDrawable.setBounds((int) this.rectF.left, (int) this.rectF.top, (int) this.rectF.right, (int) this.rectF.bottom);
                this.shadowDrawable.draw(canvas);
                canvas.drawRoundRect(this.rectF12, (float) AndroidUtilities.dp(34.0f), (float) AndroidUtilities.dp(34.0f), this.redPaint);
                this.rectF12.set((float) AndroidUtilities.dp(28.0f), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(56.0f), (float) AndroidUtilities.dp(48.0f));
                canvas.drawRoundRect(this.rectF12, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), this.addWhitePaint);
                return;
            default:
                return;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.fadingAway = false;
                AndroidUtilities.runOnUIThread(this.longPressed, 220);
                setHighlighted(true);
                this.rect = new RectF((float) getLeft(), (float) getTop(), (float) getRight(), (float) getBottom());
                break;
            case 1:
                AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                if (!(this.fadingAway || !this.rect.contains((float) (getLeft() + ((int) motionEvent.getX())), (float) (getTop() + ((int) motionEvent.getY()))) || this.delegate == null)) {
                    this.delegate.shutterReleased();
                    break;
                }
            case 2:
                if (!(this.fadingAway || this.rect.contains((float) (getLeft() + ((int) motionEvent.getX())), (float) (getTop() + ((int) motionEvent.getY()))))) {
                    AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                    this.fadingAway = true;
                    setHighlighted(false);
                    if (this.state == State.RECORDING) {
                        this.delegate.shutterCancel();
                        setState(State.DEFAULT, true);
                        break;
                    }
                }
                break;
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            setHighlighted(false);
        }
        return true;
    }

    public void setAnimationProgress(float f) {
        this.animationProgress = f;
        invalidate();
    }

    public void setClickEnabled(boolean z) {
        this.clickEnabled = z;
    }

    public void setState(State value, boolean animated) {
        if (this.state != value) {
            this.previousState = this.state;
            this.state = value;
            if (animated) {
                this.animatingStateChange = true;
                Animator animator = ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                if (this.previousState == State.DEFAULT && this.state == State.VIDEO) {
                    this.redPaint.setAlpha(255);
                    animator.setInterpolator(this.guesterpolator);
                    animator.setDuration(300);
                } else if (this.previousState == State.DEFAULT && this.state == State.RECORDING) {
                    this.redPaint.setAlpha(255);
                    this.addWhitePaint.setAlpha(255);
                    animator.setInterpolator(new OvershootInterpolator(1.11f));
                    animator.setDuration(350);
                } else if (this.previousState == State.VIDEO && this.state == State.RECORDING) {
                    animator.setInterpolator(new OvershootInterpolator(1.11f));
                    animator.setDuration(350);
                }
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ShutterButton.this.animatingStateChange = false;
                        ShutterButton.this.setClickEnabled(true);
                    }

                    public void onAnimationStart(Animator animator) {
                        ShutterButton.this.setClickEnabled(false);
                    }
                });
                animator.start();
            } else if (this.state == State.DEFAULT) {
                this.whitePaint.setAlpha(255);
                this.bluePaint.setAlpha(255);
                this.lightBluePaint.setAlpha(255);
            } else if (this.state == State.VIDEO) {
                this.whitePaint.setAlpha(255);
                this.redPaint.setAlpha(255);
            }
            invalidate();
        }
    }
}
