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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
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

        private Insets(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    public static float constrain(float amount, float low, float high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    public Switch(Context context) {
        super(context);
        this.mThumbDrawable = context.getResources().getDrawable(R.drawable.switch_thumb);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(this);
        }
        this.mTrackDrawable = context.getResources().getDrawable(R.drawable.switch_track);
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
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchPadding(int pixels) {
        this.mSwitchPadding = pixels;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int pixels) {
        this.mSwitchMinWidth = pixels;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int pixels) {
        this.mThumbTextPadding = pixels;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable track) {
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(null);
        }
        this.mTrackDrawable = track;
        if (track != null) {
            track.setCallback(this);
        }
        requestLayout();
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable thumb) {
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(null);
        }
        this.mThumbDrawable = thumb;
        if (thumb != null) {
            thumb.setCallback(this);
        }
        requestLayout();
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setSplitTrack(boolean splitTrack) {
        this.mSplitTrack = splitTrack;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thumbWidth;
        int thumbHeight;
        Rect padding = this.mTempRect;
        int trackHeight = 0;
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            thumbWidth = (this.mThumbDrawable.getIntrinsicWidth() - padding.left) - padding.right;
            thumbHeight = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            thumbWidth = 0;
            thumbHeight = 0;
        }
        this.mThumbWidth = thumbWidth;
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            trackHeight = this.mTrackDrawable.getIntrinsicHeight();
        } else {
            padding.setEmpty();
        }
        int paddingLeft = padding.left;
        int paddingRight = padding.right;
        if (this.mThumbDrawable != null) {
            Insets inset = Insets.NONE;
            paddingLeft = Math.max(paddingLeft, inset.left);
            paddingRight = Math.max(paddingRight, inset.right);
        }
        int switchWidth = Math.max(this.mSwitchMinWidth, ((2 * this.mThumbWidth) + paddingLeft) + paddingRight);
        int switchHeight = Math.max(trackHeight, thumbHeight);
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < switchHeight) {
            setMeasuredDimension(switchWidth, switchHeight);
        }
    }

    private boolean hitThumb(float x, float y) {
        int thumbOffset = getThumbOffset();
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbLeft = (this.mSwitchLeft + thumbOffset) - this.mTouchSlop;
        return x > ((float) thumbLeft) && x < ((float) ((((this.mThumbWidth + thumbLeft) + this.mTempRect.left) + this.mTempRect.right) + this.mTouchSlop)) && y > ((float) (this.mSwitchTop - this.mTouchSlop)) && y < ((float) (this.mSwitchBottom + this.mTouchSlop));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        float x;
        float y;
        switch (ev.getActionMasked()) {
            case 0:
                x = ev.getX();
                y = ev.getY();
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
                stopDrag(ev);
                super.onTouchEvent(ev);
                return true;
            case 2:
                float y2;
                switch (this.mTouchMode) {
                    case 0:
                        break;
                    case 1:
                        y = ev.getX();
                        y2 = ev.getY();
                        if (Math.abs(y - this.mTouchX) > ((float) this.mTouchSlop) || Math.abs(y2 - this.mTouchY) > ((float) this.mTouchSlop)) {
                            this.mTouchMode = 2;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = y;
                            this.mTouchY = y2;
                            return true;
                        }
                    case 2:
                        x = ev.getX();
                        int thumbScrollRange = getThumbScrollRange();
                        y2 = x - this.mTouchX;
                        float dPos = thumbScrollRange != 0 ? y2 / ((float) thumbScrollRange) : y2 > 0.0f ? 1.0f : -1.0f;
                        if (LocaleController.isRTL) {
                            dPos = -dPos;
                        }
                        float newPos = constrain(this.thumbPosition + dPos, 0.0f, 1.0f);
                        if (newPos != this.thumbPosition) {
                            this.mTouchX = x;
                            setThumbPosition(newPos);
                        }
                        return true;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(3);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void stopDrag(MotionEvent ev) {
        boolean z = false;
        this.mTouchMode = 0;
        boolean commitChange = ev.getAction() == 1 && isEnabled();
        if (commitChange) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xvel = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xvel) > ((float) this.mMinFlingVelocity)) {
                if (!LocaleController.isRTL) {
                    if (xvel > 0.0f) {
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
        cancelSuperTouch(ev);
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        float targetPosition = newCheckedState ? 1.0f : 0.0f;
        this.mPositionAnimator = ObjectAnimator.ofFloat(this, "thumbPosition", new float[]{targetPosition});
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
    private void setThumbPosition(float position) {
        this.thumbPosition = position;
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

    public void setChecked(boolean checked) {
        super.setChecked(checked);
        checked = isChecked();
        if (this.attachedToWindow && this.wasLayout) {
            animateThumbToCheckedState(checked);
        } else {
            cancelPositionAnimator();
            setThumbPosition(checked ? 1.0f : 0.0f);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(checked ? Theme.key_switchTrackChecked : Theme.key_switchTrack), Mode.MULTIPLY));
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(checked ? Theme.key_switchThumbChecked : Theme.key_switchThumb), Mode.MULTIPLY));
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

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchLeft;
        int switchRight;
        int switchBottom;
        super.onLayout(changed, left, top, right, bottom);
        this.wasLayout = true;
        int opticalInsetLeft = 0;
        int opticalInsetRight = 0;
        if (this.mThumbDrawable != null) {
            Rect trackPadding = this.mTempRect;
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.getPadding(trackPadding);
            } else {
                trackPadding.setEmpty();
            }
            Insets insets = Insets.NONE;
            opticalInsetLeft = Math.max(0, insets.left - trackPadding.left);
            opticalInsetRight = Math.max(0, insets.right - trackPadding.right);
        }
        if (LocaleController.isRTL) {
            switchLeft = getPaddingLeft() + opticalInsetLeft;
            switchRight = ((this.mSwitchWidth + switchLeft) - opticalInsetLeft) - opticalInsetRight;
        } else {
            switchRight = (getWidth() - getPaddingRight()) - opticalInsetRight;
            switchLeft = ((switchRight - this.mSwitchWidth) + opticalInsetLeft) + opticalInsetRight;
        }
        int gravity = getGravity() & 112;
        if (gravity == 16) {
            gravity = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2);
            switchBottom = this.mSwitchHeight + gravity;
        } else if (gravity != 80) {
            gravity = getPaddingTop();
            switchBottom = this.mSwitchHeight + gravity;
        } else {
            switchBottom = getHeight() - getPaddingBottom();
            gravity = switchBottom - this.mSwitchHeight;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = gravity;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    public void draw(Canvas c) {
        Insets thumbInsets;
        int trackLeft;
        int trackTop;
        int trackRight;
        Rect padding = this.mTempRect;
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        int thumbInitialLeft = getThumbOffset() + switchLeft;
        if (this.mThumbDrawable != null) {
            thumbInsets = Insets.NONE;
        } else {
            thumbInsets = Insets.NONE;
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            thumbInitialLeft += padding.left;
            trackLeft = switchLeft;
            trackTop = switchTop;
            trackRight = switchRight;
            int trackBottom = switchBottom;
            if (thumbInsets != Insets.NONE) {
                if (thumbInsets.left > padding.left) {
                    trackLeft += thumbInsets.left - padding.left;
                }
                if (thumbInsets.top > padding.top) {
                    trackTop += thumbInsets.top - padding.top;
                }
                if (thumbInsets.right > padding.right) {
                    trackRight -= thumbInsets.right - padding.right;
                }
                if (thumbInsets.bottom > padding.bottom) {
                    trackBottom -= thumbInsets.bottom - padding.bottom;
                }
            }
            this.mTrackDrawable.setBounds(trackLeft, trackTop, trackRight, trackBottom);
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            trackLeft = thumbInitialLeft - padding.left;
            trackTop = (this.mThumbWidth + thumbInitialLeft) + padding.right;
            trackRight = AndroidUtilities.density == 1.5f ? AndroidUtilities.dp(1.0f) : 0;
            this.mThumbDrawable.setBounds(trackLeft, switchTop + trackRight, trackTop, switchBottom + trackRight);
            Drawable background = getBackground();
            if (background != null && VERSION.SDK_INT >= 21) {
                background.setHotspotBounds(trackLeft, switchTop, trackTop, switchBottom);
            }
        }
        super.draw(c);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect padding = this.mTempRect;
        Drawable trackDrawable = this.mTrackDrawable;
        if (trackDrawable != null) {
            trackDrawable.getPadding(padding);
        } else {
            padding.setEmpty();
        }
        int switchTop = this.mSwitchTop;
        int switchBottom = this.mSwitchBottom;
        Drawable thumbDrawable = this.mThumbDrawable;
        if (trackDrawable != null) {
            if (!this.mSplitTrack || thumbDrawable == null) {
                trackDrawable.draw(canvas);
            } else {
                Insets insets = Insets.NONE;
                thumbDrawable.copyBounds(padding);
                padding.left += insets.left;
                padding.right -= insets.right;
                int saveCount = canvas.save();
                canvas.clipRect(padding, Op.DIFFERENCE);
                trackDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }
        int saveCount2 = canvas.save();
        if (thumbDrawable != null) {
            thumbDrawable.draw(canvas);
        }
        canvas.restoreToCount(saveCount2);
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
        float position;
        if (LocaleController.isRTL) {
            position = 1.0f - this.thumbPosition;
        } else {
            position = this.thumbPosition;
        }
        return (int) ((((float) getThumbScrollRange()) * position) + 0.5f);
    }

    private int getThumbScrollRange() {
        if (this.mTrackDrawable == null) {
            return 0;
        }
        Insets insets;
        Rect padding = this.mTempRect;
        this.mTrackDrawable.getPadding(padding);
        if (this.mThumbDrawable != null) {
            insets = Insets.NONE;
        } else {
            insets = Insets.NONE;
        }
        return ((((this.mSwitchWidth - this.mThumbWidth) - padding.left) - padding.right) - insets.left) - insets.right;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] myDrawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(myDrawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(myDrawableState);
        }
        invalidate();
    }

    @SuppressLint({"NewApi"})
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setHotspot(x, y);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setHotspot(x, y);
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        if (!(super.verifyDrawable(who) || who == this.mThumbDrawable)) {
            if (who != this.mTrackDrawable) {
                return false;
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
