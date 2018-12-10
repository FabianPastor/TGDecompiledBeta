package org.telegram.p005ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.annotation.Keep;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Components.Switch */
public class Switch extends View {
    private boolean attachedToWindow;
    private ObjectAnimator checkAnimator;
    private int drawIconType;
    private ObjectAnimator iconAnimator;
    private float iconProgress = 1.0f;
    private boolean isChecked;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private float progress;
    private RectF rectF = new RectF();
    private String thumbCheckedColorKey = Theme.key_windowBackgroundWhite;
    private String thumbColorKey = Theme.key_windowBackgroundWhite;
    private String trackCheckedColorKey = Theme.key_switch2TrackChecked;
    private String trackColorKey = Theme.key_switch2Track;

    /* renamed from: org.telegram.ui.Components.Switch$1 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            Switch.this.checkAnimator = null;
        }
    }

    /* renamed from: org.telegram.ui.Components.Switch$2 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            Switch.this.iconAnimator = null;
        }
    }

    /* renamed from: org.telegram.ui.Components.Switch$OnCheckedChangeListener */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch switchR, boolean z);
    }

    public Switch(Context context) {
        super(context);
        this.paint2.setStyle(Style.STROKE);
        this.paint2.setStrokeCap(Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
    }

    @Keep
    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    @Keep
    public void setIconProgress(float value) {
        if (this.iconProgress != value) {
            this.iconProgress = value;
            invalidate();
        }
    }

    @Keep
    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        if (this.iconAnimator != null) {
            this.iconAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int type) {
        this.drawIconType = type;
    }

    public void setColors(String track, String trackChecked, String thumb, String thumbChecked) {
        this.trackColorKey = track;
        this.trackCheckedColorKey = trackChecked;
        this.thumbColorKey = thumb;
        this.thumbCheckedColorKey = thumbChecked;
    }

    private void animateToCheckedState(boolean newCheckedState) {
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(250);
        this.checkAnimator.addListener(new CLASSNAME());
        this.checkAnimator.start();
    }

    private void animateIcon(boolean newCheckedState) {
        String str = "iconProgress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.iconAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.iconAnimator.setDuration(250);
        this.iconAnimator.addListener(new CLASSNAME());
        this.iconAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(checked, 0, animated);
    }

    public void setChecked(boolean checked, int iconType, boolean animated) {
        float f = 1.0f;
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (this.attachedToWindow && animated) {
                animateToCheckedState(checked);
            } else {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
            }
            if (this.onCheckedChangeListener != null) {
                this.onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
        if (this.drawIconType != iconType) {
            this.drawIconType = iconType;
            if (this.attachedToWindow && animated) {
                animateIcon(iconType == 0);
                return;
            }
            cancelIconAnimator();
            if (iconType != 0) {
                f = 0.0f;
            }
            setIconProgress(f);
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            int width = AndroidUtilities.m9dp(31.0f);
            int thumb = AndroidUtilities.m9dp(20.0f);
            int x = (getMeasuredWidth() - width) / 2;
            int y = (getMeasuredHeight() - AndroidUtilities.m9dp(14.0f)) / 2;
            int tx = (AndroidUtilities.m9dp(7.0f) + x) + ((int) (((float) AndroidUtilities.m9dp(17.0f)) * this.progress));
            int ty = getMeasuredHeight() / 2;
            int color1 = Theme.getColor(this.trackColorKey);
            int color2 = Theme.getColor(this.trackCheckedColorKey);
            int r1 = Color.red(color1);
            int r2 = Color.red(color2);
            int g1 = Color.green(color1);
            int g2 = Color.green(color2);
            int b1 = Color.blue(color1);
            int b2 = Color.blue(color2);
            int a1 = Color.alpha(color1);
            int color = ((((((int) (((float) a1) + (((float) (Color.alpha(color2) - a1)) * this.progress))) & 255) << 24) | ((((int) (((float) r1) + (((float) (r2 - r1)) * this.progress))) & 255) << 16)) | ((((int) (((float) g1) + (((float) (g2 - g1)) * this.progress))) & 255) << 8)) | (((int) (((float) b1) + (((float) (b2 - b1)) * this.progress))) & 255);
            this.paint.setColor(color);
            this.paint2.setColor(color);
            this.rectF.set((float) x, (float) y, (float) (x + width), (float) (AndroidUtilities.m9dp(14.0f) + y));
            canvas.drawRoundRect(this.rectF, (float) AndroidUtilities.m9dp(7.0f), (float) AndroidUtilities.m9dp(7.0f), this.paint);
            canvas.drawCircle((float) tx, (float) ty, (float) AndroidUtilities.m9dp(10.0f), this.paint);
            color1 = Theme.getColor(this.thumbColorKey);
            color2 = Theme.getColor(this.thumbCheckedColorKey);
            r1 = Color.red(color1);
            r2 = Color.red(color2);
            g1 = Color.green(color1);
            g2 = Color.green(color2);
            b1 = Color.blue(color1);
            b2 = Color.blue(color2);
            a1 = Color.alpha(color1);
            int alpha = (int) (((float) a1) + (((float) (Color.alpha(color2) - a1)) * this.progress));
            this.paint.setColor(((((alpha & 255) << 24) | ((((int) (((float) r1) + (((float) (r2 - r1)) * this.progress))) & 255) << 16)) | ((((int) (((float) g1) + (((float) (g2 - g1)) * this.progress))) & 255) << 8)) | (((int) (((float) b1) + (((float) (b2 - b1)) * this.progress))) & 255));
            canvas.drawCircle((float) tx, (float) ty, (float) AndroidUtilities.m9dp(8.0f), this.paint);
            if (this.drawIconType == 1) {
                tx = (int) (((float) tx) - (((float) AndroidUtilities.m9dp(10.8f)) - (((float) AndroidUtilities.m9dp(1.3f)) * this.progress)));
                ty = (int) (((float) ty) - (((float) AndroidUtilities.m9dp(8.5f)) - (((float) AndroidUtilities.m9dp(0.5f)) * this.progress)));
                int startX2 = ((int) AndroidUtilities.dpf2(4.6f)) + tx;
                int startY2 = (int) (AndroidUtilities.dpf2(9.5f) + ((float) ty));
                int startX = ((int) AndroidUtilities.dpf2(7.5f)) + tx;
                int startY = ((int) AndroidUtilities.dpf2(5.4f)) + ty;
                int endX = startX + AndroidUtilities.m9dp(7.0f);
                int endY = startY + AndroidUtilities.m9dp(7.0f);
                Canvas canvas2 = canvas;
                canvas2.drawLine((float) ((int) (((float) startX) + (((float) (startX2 - startX)) * this.progress))), (float) ((int) (((float) startY) + (((float) (startY2 - startY)) * this.progress))), (float) ((int) (((float) endX) + (((float) ((startX2 + AndroidUtilities.m9dp(2.0f)) - endX)) * this.progress))), (float) ((int) (((float) endY) + (((float) ((startY2 + AndroidUtilities.m9dp(2.0f)) - endY)) * this.progress))), this.paint2);
                startX = ((int) AndroidUtilities.dpf2(7.5f)) + tx;
                startY = ((int) AndroidUtilities.dpf2(12.5f)) + ty;
                canvas2 = canvas;
                canvas2.drawLine((float) startX, (float) startY, (float) (startX + AndroidUtilities.m9dp(7.0f)), (float) (startY - AndroidUtilities.m9dp(7.0f)), this.paint2);
            } else if (this.drawIconType == 2 || this.iconAnimator != null) {
                this.paint2.setAlpha((int) (255.0f * (1.0f - this.iconProgress)));
                canvas.drawLine((float) tx, (float) ty, (float) tx, (float) (ty - AndroidUtilities.m9dp(5.0f)), this.paint2);
                canvas.save();
                canvas.rotate(-90.0f * this.iconProgress, (float) tx, (float) ty);
                canvas.drawLine((float) tx, (float) ty, (float) (AndroidUtilities.m9dp(4.0f) + tx), (float) ty, this.paint2);
                canvas.restore();
            }
        }
    }
}
