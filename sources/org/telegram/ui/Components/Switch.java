package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class Switch extends CompoundButton {
    private static final int THUMB_ANIMATION_DURATION = 250;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private boolean attachedToWindow;
    private int mMinFlingVelocity;
    private ObjectAnimator mPositionAnimator;
    private boolean mSplitTrack;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private int mSwitchWidth;
    private final Rect mTempRect = new Rect();
    private Drawable mThumbDrawable;
    private int mThumbTextPadding;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private float thumbPosition;
    private boolean wasLayout;

    public static class Insets {
        public static final Insets NONE = new Insets(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        public final int bottom;
        public final int left;
        public final int right;
        public final int top;

        private Insets(int i, int i2, int i3, int i4) {
            this.left = i;
            this.top = i2;
            this.right = i3;
            this.bottom = i4;
        }
    }

    public static float constrain(float f, float f2, float f3) {
        return f < f2 ? f2 : f > f3 ? f3 : f;
    }

    public Switch(Context context) {
        super(context);
        this.mThumbDrawable = context.getResources().getDrawable(C0446R.drawable.switch_thumb);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(this);
        }
        this.mTrackDrawable = context.getResources().getDrawable(C0446R.drawable.switch_track);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(this);
        }
        if (AndroidUtilities.density < 1.0f) {
            this.mSwitchMinWidth = AndroidUtilities.dp(30.0f);
        } else {
            this.mSwitchMinWidth = 0;
        }
        this.mSwitchPadding = 0;
        this.mSplitTrack = false;
        context = ViewConfiguration.get(context);
        this.mTouchSlop = context.getScaledTouchSlop();
        this.mMinFlingVelocity = context.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchPadding(int i) {
        this.mSwitchPadding = i;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int i) {
        this.mSwitchMinWidth = i;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int i) {
        this.mThumbTextPadding = i;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable drawable) {
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(null);
        }
        this.mTrackDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        requestLayout();
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable drawable) {
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(null);
        }
        this.mThumbDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        requestLayout();
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setSplitTrack(boolean z) {
        this.mSplitTrack = z;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public void onMeasure(int i, int i2) {
        int intrinsicWidth;
        int intrinsicHeight;
        Rect rect = this.mTempRect;
        int i3 = 0;
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(rect);
            intrinsicWidth = (this.mThumbDrawable.getIntrinsicWidth() - rect.left) - rect.right;
            intrinsicHeight = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            intrinsicWidth = 0;
            intrinsicHeight = intrinsicWidth;
        }
        this.mThumbWidth = intrinsicWidth;
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(rect);
            i3 = this.mTrackDrawable.getIntrinsicHeight();
        } else {
            rect.setEmpty();
        }
        intrinsicWidth = rect.left;
        int i4 = rect.right;
        if (this.mThumbDrawable != null) {
            Insets insets = Insets.NONE;
            intrinsicWidth = Math.max(intrinsicWidth, insets.left);
            i4 = Math.max(i4, insets.right);
        }
        i4 = Math.max(this.mSwitchMinWidth, ((2 * this.mThumbWidth) + intrinsicWidth) + i4);
        intrinsicWidth = Math.max(i3, intrinsicHeight);
        this.mSwitchWidth = i4;
        this.mSwitchHeight = intrinsicWidth;
        super.onMeasure(i, i2);
        if (getMeasuredHeight() < intrinsicWidth) {
            setMeasuredDimension(i4, intrinsicWidth);
        }
    }

    private boolean hitThumb(float f, float f2) {
        int thumbOffset = getThumbOffset();
        this.mThumbDrawable.getPadding(this.mTempRect);
        int i = (this.mSwitchLeft + thumbOffset) - this.mTouchSlop;
        return f > ((float) i) && f < ((float) ((((this.mThumbWidth + i) + this.mTempRect.left) + this.mTempRect.right) + this.mTouchSlop)) && f2 > ((float) (this.mSwitchTop - this.mTouchSlop)) && f2 < ((float) (this.mSwitchBottom + this.mTouchSlop));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mVelocityTracker.addMovement(motionEvent);
        float x;
        float y;
        switch (motionEvent.getActionMasked()) {
            case 0:
                x = motionEvent.getX();
                y = motionEvent.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = 1;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
            case 1:
            case 3:
                if (this.mTouchMode != 2) {
                    this.mTouchMode = 0;
                    this.mVelocityTracker.clear();
                    break;
                }
                stopDrag(motionEvent);
                super.onTouchEvent(motionEvent);
                return true;
            case 2:
                switch (this.mTouchMode) {
                    case 0:
                        break;
                    case 1:
                        x = motionEvent.getX();
                        float y2 = motionEvent.getY();
                        if (Math.abs(x - this.mTouchX) > ((float) this.mTouchSlop) || Math.abs(y2 - this.mTouchY) > ((float) this.mTouchSlop)) {
                            this.mTouchMode = 2;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x;
                            this.mTouchY = y2;
                            return true;
                        }
                    case 2:
                        motionEvent = motionEvent.getX();
                        int thumbScrollRange = getThumbScrollRange();
                        y = motionEvent - this.mTouchX;
                        y = thumbScrollRange != 0 ? y / ((float) thumbScrollRange) : y > 0.0f ? 1.0f : -1.0f;
                        if (LocaleController.isRTL) {
                            y = -y;
                        }
                        x = constrain(this.thumbPosition + y, 0.0f, 1.0f);
                        if (x != this.thumbPosition) {
                            this.mTouchX = motionEvent;
                            setThumbPosition(x);
                        }
                        return true;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void cancelSuperTouch(MotionEvent motionEvent) {
        motionEvent = MotionEvent.obtain(motionEvent);
        motionEvent.setAction(3);
        super.onTouchEvent(motionEvent);
        motionEvent.recycle();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void stopDrag(MotionEvent motionEvent) {
        boolean z = false;
        this.mTouchMode = 0;
        boolean z2 = motionEvent.getAction() == 1 && isEnabled();
        if (z2) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > ((float) this.mMinFlingVelocity)) {
                if (!LocaleController.isRTL) {
                    if (xVelocity > 0.0f) {
                    }
                }
                z = true;
            } else {
                z = getTargetCheckedState();
            }
        } else {
            z = isChecked();
        }
        setChecked(z);
        cancelSuperTouch(motionEvent);
    }

    private void animateThumbToCheckedState(boolean z) {
        z = z ? true : false;
        this.mPositionAnimator = ObjectAnimator.ofFloat(this, "thumbPosition", new float[]{z});
        this.mPositionAnimator.setDuration(250);
        this.mPositionAnimator.start();
    }

    private void cancelPositionAnimator() {
        if (this.mPositionAnimator != null) {
            this.mPositionAnimator.cancel();
        }
    }

    private boolean getTargetCheckedState() {
        return this.thumbPosition > 0.5f;
    }

    @Keep
    private void setThumbPosition(float f) {
        this.thumbPosition = f;
        invalidate();
    }

    public float getThumbPosition() {
        return this.thumbPosition;
    }

    public void toggle() {
        setChecked(isChecked() ^ 1);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        requestLayout();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        this.wasLayout = false;
    }

    public void resetLayout() {
        this.wasLayout = false;
    }

    public void setChecked(boolean z) {
        super.setChecked(z);
        z = isChecked();
        if (this.attachedToWindow && this.wasLayout) {
            animateThumbToCheckedState(z);
        } else {
            cancelPositionAnimator();
            setThumbPosition(z ? 1.0f : 0.0f);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? Theme.key_switchTrackChecked : Theme.key_switchTrack), Mode.MULTIPLY));
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? Theme.key_switchThumbChecked : Theme.key_switchThumb), Mode.MULTIPLY));
        }
    }

    public void checkColorFilters() {
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(isChecked() ? Theme.key_switchTrackChecked : Theme.key_switchTrack), Mode.MULTIPLY));
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(isChecked() ? Theme.key_switchThumbChecked : Theme.key_switchThumb), Mode.MULTIPLY));
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.wasLayout = true;
        i = 0;
        if (this.mThumbDrawable) {
            z = this.mTempRect;
            if (this.mTrackDrawable != 0) {
                this.mTrackDrawable.getPadding(z);
            } else {
                z.setEmpty();
            }
            i2 = Insets.NONE;
            i3 = Math.max(0, i2.left - z.left);
            z = Math.max(0, i2.right - z.right);
            i = i3;
        } else {
            z = false;
        }
        if (LocaleController.isRTL != 0) {
            i2 = getPaddingLeft() + i;
            i3 = ((this.mSwitchWidth + i2) - i) - z;
        } else {
            i3 = (getWidth() - getPaddingRight()) - z;
            i2 = ((i3 - this.mSwitchWidth) + i) + z;
        }
        z = getGravity() & 112;
        if (z) {
            z = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2);
            i = this.mSwitchHeight + z;
        } else if (!z) {
            z = getPaddingTop();
            i = this.mSwitchHeight + z;
        } else {
            i = getHeight() - getPaddingBottom();
            z = i - this.mSwitchHeight;
        }
        this.mSwitchLeft = i2;
        this.mSwitchTop = z;
        this.mSwitchBottom = i;
        this.mSwitchRight = i3;
    }

    public void draw(Canvas canvas) {
        Insets insets;
        Rect rect = this.mTempRect;
        int i = this.mSwitchLeft;
        int i2 = this.mSwitchTop;
        int i3 = this.mSwitchRight;
        int i4 = this.mSwitchBottom;
        int thumbOffset = getThumbOffset() + i;
        if (this.mThumbDrawable != null) {
            insets = Insets.NONE;
        } else {
            insets = Insets.NONE;
        }
        if (this.mTrackDrawable != null) {
            int i5;
            int i6;
            this.mTrackDrawable.getPadding(rect);
            thumbOffset += rect.left;
            if (insets != Insets.NONE) {
                if (insets.left > rect.left) {
                    i += insets.left - rect.left;
                }
                i5 = insets.top > rect.top ? (insets.top - rect.top) + i2 : i2;
                if (insets.right > rect.right) {
                    i3 -= insets.right - rect.right;
                }
                if (insets.bottom > rect.bottom) {
                    i6 = i4 - (insets.bottom - rect.bottom);
                    this.mTrackDrawable.setBounds(i, i5, i3, i6);
                }
            } else {
                i5 = i2;
            }
            i6 = i4;
            this.mTrackDrawable.setBounds(i, i5, i3, i6);
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(rect);
            i = thumbOffset - rect.left;
            thumbOffset = (thumbOffset + this.mThumbWidth) + rect.right;
            int dp = AndroidUtilities.density == 1.5f ? AndroidUtilities.dp(1.0f) : 0;
            this.mThumbDrawable.setBounds(i, i2 + dp, thumbOffset, dp + i4);
            Drawable background = getBackground();
            if (background != null && VERSION.SDK_INT >= 21) {
                background.setHotspotBounds(i, i2, thumbOffset, i4);
            }
        }
        super.draw(canvas);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = this.mTempRect;
        Drawable drawable = this.mTrackDrawable;
        if (drawable != null) {
            drawable.getPadding(rect);
        } else {
            rect.setEmpty();
        }
        int i = this.mSwitchTop;
        i = this.mSwitchBottom;
        Drawable drawable2 = this.mThumbDrawable;
        if (drawable != null) {
            if (!this.mSplitTrack || drawable2 == null) {
                drawable.draw(canvas);
            } else {
                Insets insets = Insets.NONE;
                drawable2.copyBounds(rect);
                rect.left += insets.left;
                rect.right -= insets.right;
                int save = canvas.save();
                canvas.clipRect(rect, Op.DIFFERENCE);
                drawable.draw(canvas);
                canvas.restoreToCount(save);
            }
        }
        int save2 = canvas.save();
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
        canvas.restoreToCount(save2);
    }

    public int getCompoundPaddingLeft() {
        if (LocaleController.isRTL) {
            return super.getCompoundPaddingLeft() + this.mSwitchWidth;
        }
        return super.getCompoundPaddingLeft();
    }

    public int getCompoundPaddingRight() {
        if (LocaleController.isRTL) {
            return super.getCompoundPaddingRight();
        }
        return super.getCompoundPaddingRight() + this.mSwitchWidth;
    }

    private int getThumbOffset() {
        float f;
        if (LocaleController.isRTL) {
            f = 1.0f - this.thumbPosition;
        } else {
            f = this.thumbPosition;
        }
        return (int) ((f * ((float) getThumbScrollRange())) + 0.5f);
    }

    private int getThumbScrollRange() {
        if (this.mTrackDrawable == null) {
            return 0;
        }
        Insets insets;
        Rect rect = this.mTempRect;
        this.mTrackDrawable.getPadding(rect);
        if (this.mThumbDrawable != null) {
            insets = Insets.NONE;
        } else {
            insets = Insets.NONE;
        }
        return ((((this.mSwitchWidth - this.mThumbWidth) - rect.left) - rect.right) - insets.left) - insets.right;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(drawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(drawableState);
        }
        invalidate();
    }

    @SuppressLint({"NewApi"})
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setHotspot(f, f2);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setHotspot(f, f2);
        }
    }

    protected boolean verifyDrawable(Drawable drawable) {
        if (!(super.verifyDrawable(drawable) || drawable == this.mThumbDrawable)) {
            if (drawable != this.mTrackDrawable) {
                return null;
            }
        }
        return true;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.jumpToCurrentState();
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.jumpToCurrentState();
        }
        if (this.mPositionAnimator != null && this.mPositionAnimator.isRunning()) {
            this.mPositionAnimator.end();
            this.mPositionAnimator = null;
        }
    }
}
