package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.StateSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SeekBarView extends FrameLayout {
    private float bufferedProgress;
    private float currentRadius;
    private SeekBarViewDelegate delegate;
    private Drawable hoverDrawable;
    private Paint innerPaint1;
    private long lastUpdateTime;
    private Paint outerPaint1;
    private boolean pressed;
    private int[] pressedState = {16842910, 16842919};
    private float progressToSet;
    private boolean reportChanges;
    private int selectorWidth;
    private int thumbDX;
    private int thumbSize;
    private int thumbX;

    public interface SeekBarViewDelegate {
        void onSeekBarDrag(boolean z, float f);

        void onSeekBarPressed(boolean z);
    }

    public SeekBarView(Context context) {
        super(context);
        setWillNotDraw(false);
        this.innerPaint1 = new Paint(1);
        this.innerPaint1.setColor(Theme.getColor("player_progressBackground"));
        this.outerPaint1 = new Paint(1);
        this.outerPaint1.setColor(Theme.getColor("player_progress"));
        this.selectorWidth = AndroidUtilities.dp(32.0f);
        this.thumbSize = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            int color = Theme.getColor("player_progress");
            this.hoverDrawable = Theme.createSelectorDrawable(Color.argb(40, Color.red(color), Color.green(color), Color.blue(color)), 1, AndroidUtilities.dp(16.0f));
            this.hoverDrawable.setCallback(this);
            this.hoverDrawable.setVisible(true, false);
        }
    }

    public void setColors(int i, int i2) {
        this.innerPaint1.setColor(i);
        this.outerPaint1.setColor(i2);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setDrawableColor(drawable, Color.argb(40, Color.red(i2), Color.green(i2), Color.blue(i2)));
        }
    }

    public void setInnerColor(int i) {
        this.innerPaint1.setColor(i);
    }

    public void setOuterColor(int i) {
        this.outerPaint1.setColor(i);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setDrawableColor(drawable, Color.argb(40, Color.red(i), Color.green(i), Color.blue(i)));
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouch(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return onTouch(motionEvent);
    }

    public void setReportChanges(boolean z) {
        this.reportChanges = z;
    }

    public void setDelegate(SeekBarViewDelegate seekBarViewDelegate) {
        this.delegate = seekBarViewDelegate;
    }

    /* access modifiers changed from: package-private */
    public boolean onTouch(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (motionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
            if (motionEvent.getY() >= 0.0f && motionEvent.getY() <= ((float) getMeasuredHeight())) {
                if (((float) (this.thumbX - measuredHeight)) > motionEvent.getX() || motionEvent.getX() > ((float) (this.thumbX + this.thumbSize + measuredHeight))) {
                    this.thumbX = ((int) motionEvent.getX()) - (this.thumbSize / 2);
                    int i = this.thumbX;
                    if (i < 0) {
                        this.thumbX = 0;
                    } else if (i > getMeasuredWidth() - this.selectorWidth) {
                        this.thumbX = getMeasuredWidth() - this.selectorWidth;
                    }
                }
                this.thumbDX = (int) (motionEvent.getX() - ((float) this.thumbX));
                this.pressed = true;
                this.delegate.onSeekBarPressed(true);
                if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.hoverDrawable) != null) {
                    drawable3.setState(this.pressedState);
                    this.hoverDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressed) {
                if (motionEvent.getAction() == 1) {
                    this.delegate.onSeekBarDrag(true, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
                }
                if (Build.VERSION.SDK_INT >= 21 && (drawable = this.hoverDrawable) != null) {
                    drawable.setState(StateSet.NOTHING);
                }
                this.delegate.onSeekBarPressed(false);
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.pressed) {
            this.thumbX = (int) (motionEvent.getX() - ((float) this.thumbDX));
            int i2 = this.thumbX;
            if (i2 < 0) {
                this.thumbX = 0;
            } else if (i2 > getMeasuredWidth() - this.selectorWidth) {
                this.thumbX = getMeasuredWidth() - this.selectorWidth;
            }
            if (this.reportChanges) {
                this.delegate.onSeekBarDrag(false, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
            }
            if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.hoverDrawable) != null) {
                drawable2.setHotspot(motionEvent.getX(), motionEvent.getY());
            }
            invalidate();
            return true;
        }
        return false;
    }

    public float getProgress() {
        return ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth));
    }

    public void setProgress(float f) {
        if (getMeasuredWidth() == 0) {
            this.progressToSet = f;
            return;
        }
        this.progressToSet = -1.0f;
        int ceil = (int) Math.ceil((double) (((float) (getMeasuredWidth() - this.selectorWidth)) * f));
        if (this.thumbX != ceil) {
            this.thumbX = ceil;
            int i = this.thumbX;
            if (i < 0) {
                this.thumbX = 0;
            } else if (i > getMeasuredWidth() - this.selectorWidth) {
                this.thumbX = getMeasuredWidth() - this.selectorWidth;
            }
            invalidate();
        }
    }

    public void setBufferedProgress(float f) {
        this.bufferedProgress = f;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.progressToSet >= 0.0f && getMeasuredWidth() > 0) {
            setProgress(this.progressToSet);
            this.progressToSet = -1.0f;
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.hoverDrawable;
    }

    public boolean isDragging() {
        return this.pressed;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
        canvas.drawRect((float) (this.selectorWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.selectorWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint1);
        if (this.bufferedProgress > 0.0f) {
            canvas.drawRect((float) (this.selectorWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), ((float) (this.selectorWidth / 2)) + (this.bufferedProgress * ((float) (getMeasuredWidth() - this.selectorWidth))), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint1);
        }
        canvas.drawRect((float) (this.selectorWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) ((this.selectorWidth / 2) + this.thumbX), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint1);
        if (this.hoverDrawable != null) {
            int dp = (this.thumbX + (this.selectorWidth / 2)) - AndroidUtilities.dp(16.0f);
            int dp2 = ((this.thumbSize / 2) + measuredHeight) - AndroidUtilities.dp(16.0f);
            this.hoverDrawable.setBounds(dp, dp2, AndroidUtilities.dp(32.0f) + dp, AndroidUtilities.dp(32.0f) + dp2);
            this.hoverDrawable.draw(canvas);
        }
        float dp3 = (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != dp3) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.lastUpdateTime;
            if (elapsedRealtime > 18) {
                elapsedRealtime = 16;
            }
            float f = this.currentRadius;
            if (f < dp3) {
                this.currentRadius = f + (((float) AndroidUtilities.dp(1.0f)) * (((float) elapsedRealtime) / 60.0f));
                if (this.currentRadius > dp3) {
                    this.currentRadius = dp3;
                }
            } else {
                this.currentRadius = f - (((float) AndroidUtilities.dp(1.0f)) * (((float) elapsedRealtime) / 60.0f));
                if (this.currentRadius < dp3) {
                    this.currentRadius = dp3;
                }
            }
            invalidate();
        }
        canvas.drawCircle((float) (this.thumbX + (this.selectorWidth / 2)), (float) (measuredHeight + (this.thumbSize / 2)), this.currentRadius, this.outerPaint1);
    }
}
