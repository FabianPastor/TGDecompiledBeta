package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;

public class SimpleTextView extends View implements Callback {
    private static final int DIST_BETWEEN_SCROLLING_TEXT = 16;
    private static final int PIXELS_PER_SECOND = 50;
    private static final int PIXELS_PER_SECOND_SLOW = 30;
    private static final int SCROLL_DELAY_MS = 500;
    private static final int SCROLL_SLOWDOWN_PX = 100;
    private int currentScrollDelay;
    private int drawablePadding = AndroidUtilities.dp(4.0f);
    private GradientDrawable fadeDrawable;
    private GradientDrawable fadeDrawableBack;
    private int gravity = 51;
    private long lastUpdateTime;
    private int lastWidth;
    private Layout layout;
    private Drawable leftDrawable;
    private int leftDrawableTopPadding;
    private int offsetX;
    private int offsetY;
    private Drawable rightDrawable;
    private int rightDrawableTopPadding;
    private boolean scrollNonFitText;
    private float scrollingOffset;
    private SpannableStringBuilder spannableStringBuilder;
    private CharSequence text;
    private boolean textDoesNotFit;
    private int textHeight;
    private TextPaint textPaint = new TextPaint(1);
    private int textWidth;
    private int totalWidth;
    private boolean wasLayout;

    public SimpleTextView(Context context) {
        super(context);
    }

    public void setTextColor(int color) {
        this.textPaint.setColor(color);
        invalidate();
    }

    public void setLinkTextColor(int color) {
        this.textPaint.linkColor = color;
        invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setTextSize(int size) {
        int newSize = AndroidUtilities.dp((float) size);
        if (((float) newSize) != this.textPaint.getTextSize()) {
            this.textPaint.setTextSize((float) newSize);
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public void setScrollNonFitText(boolean value) {
        if (this.scrollNonFitText != value) {
            this.scrollNonFitText = value;
            if (this.scrollNonFitText) {
                this.fadeDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{-1, 0});
                this.fadeDrawableBack = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{0, -1});
            }
            requestLayout();
        }
    }

    public void setGravity(int value) {
        this.gravity = value;
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public int getSideDrawablesSize() {
        int size = 0;
        if (this.leftDrawable != null) {
            size = 0 + (this.leftDrawable.getIntrinsicWidth() + this.drawablePadding);
        }
        if (this.rightDrawable != null) {
            return size + (this.rightDrawable.getIntrinsicWidth() + this.drawablePadding);
        }
        return size;
    }

    public Paint getPaint() {
        return this.textPaint;
    }

    private void calcOffset(int width) {
        boolean z = false;
        if (this.layout.getLineCount() > 0) {
            this.textWidth = (int) Math.ceil((double) this.layout.getLineWidth(0));
            this.textHeight = this.layout.getLineBottom(0);
            if ((this.gravity & 112) == 16) {
                this.offsetY = (getMeasuredHeight() - this.textHeight) / 2;
            } else {
                this.offsetY = 0;
            }
            if ((this.gravity & 7) == 3) {
                this.offsetX = -((int) this.layout.getLineLeft(0));
            } else if (this.layout.getLineLeft(0) == 0.0f) {
                this.offsetX = width - this.textWidth;
            } else {
                this.offsetX = -AndroidUtilities.dp(8.0f);
            }
            this.offsetX += getPaddingLeft();
            if (this.textWidth > width) {
                z = true;
            }
            this.textDoesNotFit = z;
        }
    }

    private boolean createLayout(int width) {
        if (this.text != null) {
            try {
                CharSequence string;
                if (this.leftDrawable != null) {
                    width = (width - this.leftDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.rightDrawable != null) {
                    width = (width - this.rightDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.scrollNonFitText) {
                    string = this.text;
                } else {
                    string = TextUtils.ellipsize(this.text, this.textPaint, (float) width, TruncateAt.END);
                }
                this.layout = new StaticLayout(string, 0, string.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                calcOffset(width);
            } catch (Exception e) {
            }
        } else {
            this.layout = null;
            this.textWidth = 0;
            this.textHeight = 0;
        }
        invalidate();
        return true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalHeight;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (this.lastWidth != AndroidUtilities.displaySize.x) {
            this.lastWidth = AndroidUtilities.displaySize.x;
            this.scrollingOffset = 0.0f;
            this.currentScrollDelay = 500;
        }
        createLayout((width - getPaddingLeft()) - getPaddingRight());
        if (MeasureSpec.getMode(heightMeasureSpec) == NUM) {
            finalHeight = height;
        } else {
            finalHeight = this.textHeight;
        }
        setMeasuredDimension(width, finalHeight);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.wasLayout = true;
    }

    public int getTextWidth() {
        return this.textWidth;
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public void setLeftDrawableTopPadding(int value) {
        this.leftDrawableTopPadding = value;
    }

    public void setRightDrawableTopPadding(int value) {
        this.rightDrawableTopPadding = value;
    }

    public void setLeftDrawable(int resId) {
        setLeftDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public void setRightDrawable(int resId) {
        setRightDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public void setLeftDrawable(Drawable drawable) {
        if (this.leftDrawable != drawable) {
            if (this.leftDrawable != null) {
                this.leftDrawable.setCallback(null);
            }
            this.leftDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public void setRightDrawable(Drawable drawable) {
        if (this.rightDrawable != drawable) {
            if (this.rightDrawable != null) {
                this.rightDrawable.setCallback(null);
            }
            this.rightDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public boolean setText(CharSequence value) {
        return setText(value, false);
    }

    public boolean setText(CharSequence value, boolean force) {
        if ((this.text == null && value == null) || (!force && this.text != null && value != null && this.text.equals(value))) {
            return false;
        }
        this.text = value;
        this.scrollingOffset = 0.0f;
        this.currentScrollDelay = 500;
        recreateLayoutMaybe();
        return true;
    }

    public void setDrawablePadding(int value) {
        if (this.drawablePadding != value) {
            this.drawablePadding = value;
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    private boolean recreateLayoutMaybe() {
        if (this.wasLayout && getMeasuredHeight() != 0) {
            return createLayout(getMeasuredWidth());
        }
        requestLayout();
        return true;
    }

    public CharSequence getText() {
        if (this.text == null) {
            return "";
        }
        return this.text;
    }

    public int getTextStartX() {
        if (this.layout == null) {
            return 0;
        }
        int textOffsetX = 0;
        if (this.leftDrawable != null && (this.gravity & 7) == 3) {
            textOffsetX = 0 + (this.drawablePadding + this.leftDrawable.getIntrinsicWidth());
        }
        return (((int) getX()) + this.offsetX) + textOffsetX;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public int getTextStartY() {
        if (this.layout == null) {
            return 0;
        }
        return (int) getY();
    }

    protected void onDraw(Canvas canvas) {
        int x;
        int y;
        int textOffsetX = 0;
        this.totalWidth = this.textWidth;
        if (this.leftDrawable != null) {
            x = (int) (-this.scrollingOffset);
            y = ((this.textHeight - this.leftDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            this.leftDrawable.setBounds(x, y, this.leftDrawable.getIntrinsicWidth() + x, this.leftDrawable.getIntrinsicHeight() + y);
            this.leftDrawable.draw(canvas);
            if ((this.gravity & 7) == 3) {
                textOffsetX = 0 + (this.drawablePadding + this.leftDrawable.getIntrinsicWidth());
            }
            this.totalWidth += this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
        }
        if (this.rightDrawable != null) {
            x = ((this.textWidth + textOffsetX) + this.drawablePadding) + ((int) (-this.scrollingOffset));
            y = ((this.textHeight - this.rightDrawable.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
            this.rightDrawable.setBounds(x, y, this.rightDrawable.getIntrinsicWidth() + x, this.rightDrawable.getIntrinsicHeight() + y);
            this.rightDrawable.draw(canvas);
            this.totalWidth += this.drawablePadding + this.rightDrawable.getIntrinsicWidth();
        }
        int nextScrollX = this.totalWidth + AndroidUtilities.dp(16.0f);
        if (this.scrollingOffset != 0.0f) {
            if (this.leftDrawable != null) {
                x = ((int) (-this.scrollingOffset)) + nextScrollX;
                y = ((this.textHeight - this.leftDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
                this.leftDrawable.setBounds(x, y, this.leftDrawable.getIntrinsicWidth() + x, this.leftDrawable.getIntrinsicHeight() + y);
                this.leftDrawable.draw(canvas);
            }
            if (this.rightDrawable != null) {
                x = (((this.textWidth + textOffsetX) + this.drawablePadding) + ((int) (-this.scrollingOffset))) + nextScrollX;
                y = ((this.textHeight - this.rightDrawable.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
                this.rightDrawable.setBounds(x, y, this.rightDrawable.getIntrinsicWidth() + x, this.rightDrawable.getIntrinsicHeight() + y);
                this.rightDrawable.draw(canvas);
            }
        }
        if (this.layout != null) {
            if (!(this.offsetX + textOffsetX == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.save();
                canvas.translate(((float) (this.offsetX + textOffsetX)) - this.scrollingOffset, (float) this.offsetY);
                if (this.scrollingOffset != 0.0f) {
                }
            }
            this.layout.draw(canvas);
            if (this.scrollingOffset != 0.0f) {
                canvas.translate((float) nextScrollX, 0.0f);
                this.layout.draw(canvas);
            }
            if (!(this.offsetX + textOffsetX == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.restore();
            }
            if (this.scrollNonFitText && this.textDoesNotFit) {
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(10.0f))) {
                    this.fadeDrawable.setAlpha((int) (255.0f * (this.scrollingOffset / ((float) AndroidUtilities.dp(10.0f)))));
                } else if (this.scrollingOffset > ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) {
                    this.fadeDrawable.setAlpha((int) (255.0f * (1.0f - ((this.scrollingOffset - ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) / ((float) AndroidUtilities.dp(10.0f))))));
                } else {
                    this.fadeDrawable.setAlpha(255);
                }
                this.fadeDrawable.setBounds(0, 0, AndroidUtilities.dp(6.0f), getMeasuredHeight());
                this.fadeDrawable.draw(canvas);
                this.fadeDrawableBack.setBounds(getMeasuredWidth() - AndroidUtilities.dp(6.0f), 0, getMeasuredWidth(), getMeasuredHeight());
                this.fadeDrawableBack.draw(canvas);
            }
            updateScrollAnimation();
        }
    }

    public void setBackgroundColor(int color) {
        if (!this.scrollNonFitText) {
            super.setBackgroundColor(color);
        } else if (this.fadeDrawable != null) {
            this.fadeDrawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
            this.fadeDrawableBack.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
        }
    }

    private void updateScrollAnimation() {
        if (this.scrollNonFitText && this.textDoesNotFit) {
            long newUpdateTime = SystemClock.uptimeMillis();
            long dt = newUpdateTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            if (this.currentScrollDelay > 0) {
                this.currentScrollDelay = (int) (((long) this.currentScrollDelay) - dt);
            } else {
                float pixelsPerSecond;
                int totalDistance = this.totalWidth + AndroidUtilities.dp(16.0f);
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(100.0f))) {
                    pixelsPerSecond = 30.0f + ((this.scrollingOffset / ((float) AndroidUtilities.dp(100.0f))) * 20.0f);
                } else if (this.scrollingOffset >= ((float) (totalDistance - AndroidUtilities.dp(100.0f)))) {
                    pixelsPerSecond = 50.0f - (((this.scrollingOffset - ((float) (totalDistance - AndroidUtilities.dp(100.0f)))) / ((float) AndroidUtilities.dp(100.0f))) * 20.0f);
                } else {
                    pixelsPerSecond = 50.0f;
                }
                this.scrollingOffset += (((float) dt) / 1000.0f) * ((float) AndroidUtilities.dp(pixelsPerSecond));
                this.lastUpdateTime = newUpdateTime;
                if (this.scrollingOffset > ((float) totalDistance)) {
                    this.scrollingOffset = 0.0f;
                    this.currentScrollDelay = 500;
                }
            }
            invalidate();
        }
    }

    public void invalidateDrawable(Drawable who) {
        if (who == this.leftDrawable) {
            invalidate(this.leftDrawable.getBounds());
        } else if (who == this.rightDrawable) {
            invalidate(this.rightDrawable.getBounds());
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
