package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class SwitchCameraButton extends View {
    private boolean isFront;
    private Paint paint = new Paint(1);
    private float progress;
    private Drawable rotateDrawable = getResources().getDrawable(R.drawable.camswitch);
    private Drawable shadowDrawable = getResources().getDrawable(R.drawable.mid_shadow);

    public SwitchCameraButton(Context context) {
        super(context);
        this.paint.setStyle(Style.STROKE);
        this.paint.setColor(-1);
    }

    protected void onDraw(Canvas canvas) {
        int cx = getMeasuredWidth() / 2;
        int cy = getMeasuredHeight() / 2;
        this.shadowDrawable.setBounds(cx - AndroidUtilities.dp(9.0f), cy - AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f) + cx, AndroidUtilities.dp(9.0f) + cy);
        float width = (((float) AndroidUtilities.dp(5.0f)) * this.progress) + ((float) AndroidUtilities.dp(2.5f));
        this.paint.setStrokeWidth(width);
        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), ((float) AndroidUtilities.dp(6.0f)) - (width / 2.0f), this.paint);
        if (this.progress != 0.0f) {
            if (this.isFront) {
                canvas.rotate(this.progress * -180.0f, (float) cx, (float) cy);
            } else {
                canvas.rotate(-180.0f - (BitmapDescriptorFactory.HUE_CYAN * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.progress)), (float) cx, (float) cy);
            }
        }
        this.rotateDrawable.setBounds(cx - AndroidUtilities.dp(24.0f), cy - AndroidUtilities.dp(20.0f), AndroidUtilities.dp(24.0f) + cx, AndroidUtilities.dp(20.0f) + cy);
        this.rotateDrawable.draw(canvas);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float value) {
        this.progress = value;
        invalidate();
    }

    public boolean isFront() {
        return this.isFront;
    }

    public void setFront(boolean value, boolean animated) {
        if (this.isFront != value) {
            this.isFront = value;
            if (animated) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(320);
                animatorSet.setInterpolator(new DecelerateInterpolator(1.1f));
                if (this.isFront) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "progress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "progress", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f})});
                }
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        SwitchCameraButton.this.setClickable(true);
                    }

                    public void onAnimationStart(Animator animator) {
                        SwitchCameraButton.this.setClickable(false);
                    }
                });
                animatorSet.start();
                return;
            }
            this.progress = this.isFront ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
            invalidate();
        }
    }
}
