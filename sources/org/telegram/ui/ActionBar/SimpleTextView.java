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
import android.view.accessibility.AccessibilityNodeInfo;
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    public SimpleTextView(Context context) {
        super(context);
        setImportantForAccessibility(1);
    }

    public void setTextColor(int i) {
        this.textPaint.setColor(i);
        invalidate();
    }

    public void setLinkTextColor(int i) {
        this.textPaint.linkColor = i;
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setTextSize(int i) {
        float dp = (float) AndroidUtilities.dp((float) i);
        if (dp != this.textPaint.getTextSize()) {
            this.textPaint.setTextSize(dp);
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public void setScrollNonFitText(boolean z) {
        if (this.scrollNonFitText != z) {
            this.scrollNonFitText = z;
            if (this.scrollNonFitText) {
                this.fadeDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{-1, 0});
                this.fadeDrawableBack = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{0, -1});
            }
            requestLayout();
        }
    }

    public void setGravity(int i) {
        this.gravity = i;
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public int getSideDrawablesSize() {
        Drawable drawable = this.leftDrawable;
        int i = 0;
        if (drawable != null) {
            i = 0 + (drawable.getIntrinsicWidth() + this.drawablePadding);
        }
        drawable = this.rightDrawable;
        return drawable != null ? i + (drawable.getIntrinsicWidth() + this.drawablePadding) : i;
    }

    public Paint getPaint() {
        return this.textPaint;
    }

    private void calcOffset(int i) {
        if (this.layout.getLineCount() > 0) {
            boolean z = false;
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
                this.offsetX = i - this.textWidth;
            } else {
                this.offsetX = -AndroidUtilities.dp(8.0f);
            }
            this.offsetX += getPaddingLeft();
            if (this.textWidth > i) {
                z = true;
            }
            this.textDoesNotFit = z;
        }
    }

    private boolean createLayout(int i) {
        if (this.text != null) {
            try {
                CharSequence charSequence;
                if (this.leftDrawable != null) {
                    i = (i - this.leftDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.rightDrawable != null) {
                    i = (i - this.rightDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.scrollNonFitText) {
                    charSequence = this.text;
                } else {
                    charSequence = TextUtils.ellipsize(this.text, this.textPaint, (float) i, TruncateAt.END);
                }
                CharSequence charSequence2 = charSequence;
                this.layout = new StaticLayout(charSequence2, 0, charSequence2.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + i, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                calcOffset(i);
            } catch (Exception unused) {
            }
        } else {
            this.layout = null;
            this.textWidth = 0;
            this.textHeight = 0;
        }
        invalidate();
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int size = MeasureSpec.getSize(i2);
        int i3 = this.lastWidth;
        int i4 = AndroidUtilities.displaySize.x;
        if (i3 != i4) {
            this.lastWidth = i4;
            this.scrollingOffset = 0.0f;
            this.currentScrollDelay = 500;
        }
        createLayout((i - getPaddingLeft()) - getPaddingRight());
        if (MeasureSpec.getMode(i2) != NUM) {
            size = this.textHeight;
        }
        setMeasuredDimension(i, size);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.wasLayout = true;
    }

    public int getTextWidth() {
        return this.textWidth;
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public void setLeftDrawableTopPadding(int i) {
        this.leftDrawableTopPadding = i;
    }

    public void setRightDrawableTopPadding(int i) {
        this.rightDrawableTopPadding = i;
    }

    public void setLeftDrawable(int i) {
        setLeftDrawable(i == 0 ? null : getContext().getResources().getDrawable(i));
    }

    public void setRightDrawable(int i) {
        setRightDrawable(i == 0 ? null : getContext().getResources().getDrawable(i));
    }

    public void setLeftDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
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

    public Drawable getRightDrawable() {
        return this.rightDrawable;
    }

    public void setRightDrawable(Drawable drawable) {
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
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

    public void setSideDrawablesColor(int i) {
        Theme.setDrawableColor(this.rightDrawable, i);
        Theme.setDrawableColor(this.leftDrawable, i);
    }

    public boolean setText(CharSequence charSequence) {
        return setText(charSequence, false);
    }

    /* JADX WARNING: Missing block: B:7:0x0010, code skipped:
            if (r3.equals(r2) != false) goto L_0x0012;
     */
    public boolean setText(java.lang.CharSequence r2, boolean r3) {
        /*
        r1 = this;
        r0 = r1.text;
        if (r0 != 0) goto L_0x0006;
    L_0x0004:
        if (r2 == 0) goto L_0x0012;
    L_0x0006:
        if (r3 != 0) goto L_0x0014;
    L_0x0008:
        r3 = r1.text;
        if (r3 == 0) goto L_0x0014;
    L_0x000c:
        r3 = r3.equals(r2);
        if (r3 == 0) goto L_0x0014;
    L_0x0012:
        r2 = 0;
        return r2;
    L_0x0014:
        r1.text = r2;
        r2 = 0;
        r1.scrollingOffset = r2;
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r1.currentScrollDelay = r2;
        r1.recreateLayoutMaybe();
        r2 = 1;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.SimpleTextView.setText(java.lang.CharSequence, boolean):boolean");
    }

    public void setDrawablePadding(int i) {
        if (this.drawablePadding != i) {
            this.drawablePadding = i;
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
        CharSequence charSequence = this.text;
        return charSequence == null ? "" : charSequence;
    }

    public int getTextStartX() {
        int i = 0;
        if (this.layout == null) {
            return 0;
        }
        Drawable drawable = this.leftDrawable;
        if (drawable != null && (this.gravity & 7) == 3) {
            i = 0 + (this.drawablePadding + drawable.getIntrinsicWidth());
        }
        return (((int) getX()) + this.offsetX) + i;
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int intrinsicHeight;
        int intrinsicWidth;
        int intrinsicHeight2;
        this.totalWidth = this.textWidth;
        Drawable drawable = this.leftDrawable;
        if (drawable != null) {
            i = (int) (-this.scrollingOffset);
            intrinsicHeight = ((this.textHeight - drawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            drawable = this.leftDrawable;
            drawable.setBounds(i, intrinsicHeight, drawable.getIntrinsicWidth() + i, this.leftDrawable.getIntrinsicHeight() + intrinsicHeight);
            this.leftDrawable.draw(canvas);
            intrinsicWidth = (this.gravity & 7) == 3 ? (this.drawablePadding + this.leftDrawable.getIntrinsicWidth()) + 0 : 0;
            this.totalWidth += this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
        } else {
            intrinsicWidth = 0;
        }
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 != null) {
            intrinsicHeight = ((this.textWidth + intrinsicWidth) + this.drawablePadding) + ((int) (-this.scrollingOffset));
            intrinsicHeight2 = ((this.textHeight - drawable2.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
            drawable2 = this.rightDrawable;
            drawable2.setBounds(intrinsicHeight, intrinsicHeight2, drawable2.getIntrinsicWidth() + intrinsicHeight, this.rightDrawable.getIntrinsicHeight() + intrinsicHeight2);
            this.rightDrawable.draw(canvas);
            this.totalWidth += this.drawablePadding + this.rightDrawable.getIntrinsicWidth();
        }
        i = this.totalWidth + AndroidUtilities.dp(16.0f);
        float f = this.scrollingOffset;
        if (f != 0.0f) {
            int intrinsicHeight3;
            Drawable drawable3 = this.leftDrawable;
            if (drawable3 != null) {
                intrinsicHeight2 = ((int) (-f)) + i;
                intrinsicHeight3 = ((this.textHeight - drawable3.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
                drawable3 = this.leftDrawable;
                drawable3.setBounds(intrinsicHeight2, intrinsicHeight3, drawable3.getIntrinsicWidth() + intrinsicHeight2, this.leftDrawable.getIntrinsicHeight() + intrinsicHeight3);
                this.leftDrawable.draw(canvas);
            }
            Drawable drawable4 = this.rightDrawable;
            if (drawable4 != null) {
                int i2 = (((this.textWidth + intrinsicWidth) + this.drawablePadding) + ((int) (-this.scrollingOffset))) + i;
                intrinsicHeight3 = ((this.textHeight - drawable4.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
                drawable4 = this.rightDrawable;
                drawable4.setBounds(i2, intrinsicHeight3, drawable4.getIntrinsicWidth() + i2, this.rightDrawable.getIntrinsicHeight() + intrinsicHeight3);
                this.rightDrawable.draw(canvas);
            }
        }
        if (this.layout != null) {
            if (!(this.offsetX + intrinsicWidth == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.save();
                canvas.translate(((float) (this.offsetX + intrinsicWidth)) - this.scrollingOffset, (float) this.offsetY);
                f = this.scrollingOffset;
            }
            this.layout.draw(canvas);
            if (this.scrollingOffset != 0.0f) {
                canvas.translate((float) i, 0.0f);
                this.layout.draw(canvas);
            }
            if (!(this.offsetX + intrinsicWidth == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.restore();
            }
            if (this.scrollNonFitText && (this.textDoesNotFit || this.scrollingOffset != 0.0f)) {
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(10.0f))) {
                    this.fadeDrawable.setAlpha((int) ((this.scrollingOffset / ((float) AndroidUtilities.dp(10.0f))) * 255.0f));
                } else if (this.scrollingOffset > ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) {
                    this.fadeDrawable.setAlpha((int) ((1.0f - ((this.scrollingOffset - ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) / ((float) AndroidUtilities.dp(10.0f)))) * 255.0f));
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

    public void setBackgroundColor(int i) {
        if (this.scrollNonFitText) {
            GradientDrawable gradientDrawable = this.fadeDrawable;
            if (gradientDrawable != null) {
                gradientDrawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                this.fadeDrawableBack.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                return;
            }
            return;
        }
        super.setBackgroundColor(i);
    }

    private void updateScrollAnimation() {
        if (!this.scrollNonFitText) {
            return;
        }
        if (this.textDoesNotFit || this.scrollingOffset != 0.0f) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = uptimeMillis - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            int i = this.currentScrollDelay;
            if (i > 0) {
                this.currentScrollDelay = (int) (((long) i) - j);
            } else {
                i = this.totalWidth + AndroidUtilities.dp(16.0f);
                float f = 50.0f;
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(100.0f))) {
                    f = ((this.scrollingOffset / ((float) AndroidUtilities.dp(100.0f))) * 20.0f) + 30.0f;
                } else if (this.scrollingOffset >= ((float) (i - AndroidUtilities.dp(100.0f)))) {
                    f = 50.0f - (((this.scrollingOffset - ((float) (i - AndroidUtilities.dp(100.0f)))) / ((float) AndroidUtilities.dp(100.0f))) * 20.0f);
                }
                this.scrollingOffset += (((float) j) / 1000.0f) * ((float) AndroidUtilities.dp(f));
                this.lastUpdateTime = uptimeMillis;
                if (this.scrollingOffset > ((float) i)) {
                    this.scrollingOffset = 0.0f;
                    this.currentScrollDelay = 500;
                }
            }
            invalidate();
        }
    }

    public void invalidateDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable == drawable2) {
            invalidate(drawable2.getBounds());
            return;
        }
        drawable2 = this.rightDrawable;
        if (drawable == drawable2) {
            invalidate(drawable2.getBounds());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setVisibleToUser(true);
        accessibilityNodeInfo.setClassName("android.widget.TextView");
        accessibilityNodeInfo.setText(this.text);
    }
}
