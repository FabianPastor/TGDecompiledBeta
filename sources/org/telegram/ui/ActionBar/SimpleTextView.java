package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.StaticLayoutEx;

public class SimpleTextView extends View implements Drawable.Callback {
    private boolean buildFullLayout;
    private int currentScrollDelay;
    private int drawablePadding = AndroidUtilities.dp(4.0f);
    private Paint fadePaint;
    private Paint fadePaintBack;
    private float fullAlpha;
    private Layout fullLayout;
    private int gravity = 51;
    private long lastUpdateTime;
    private int lastWidth;
    private Layout layout;
    private Drawable leftDrawable;
    private int leftDrawableTopPadding;
    private int maxLines = 1;
    private int minWidth;
    private int offsetX;
    private int offsetY;
    private Layout partLayout;
    private Drawable rightDrawable;
    private float rightDrawableScale = 1.0f;
    private int rightDrawableTopPadding;
    private boolean scrollNonFitText;
    private float scrollingOffset;
    private CharSequence text;
    private boolean textDoesNotFit;
    private int textHeight;
    private TextPaint textPaint = new TextPaint(1);
    private int textWidth;
    private int totalWidth;
    private boolean wasLayout;
    private Drawable wrapBackgroundDrawable;

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

    /* access modifiers changed from: protected */
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

    public void setBuildFullLayout(boolean z) {
        this.buildFullLayout = z;
    }

    public void setFullAlpha(float f) {
        this.fullAlpha = f;
        invalidate();
    }

    public float getFullAlpha() {
        return this.fullAlpha;
    }

    public void setScrollNonFitText(boolean z) {
        if (this.scrollNonFitText != z) {
            this.scrollNonFitText = z;
            if (z) {
                this.fadePaint = new Paint();
                this.fadePaint.setShader(new LinearGradient(0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), 0.0f, new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
                this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                this.fadePaintBack = new Paint();
                this.fadePaintBack.setShader(new LinearGradient(0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
                this.fadePaintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            }
            requestLayout();
        }
    }

    public void setMaxLines(int i) {
        this.maxLines = i;
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
            i = 0 + drawable.getIntrinsicWidth() + this.drawablePadding;
        }
        Drawable drawable2 = this.rightDrawable;
        return drawable2 != null ? i + ((int) (((float) drawable2.getIntrinsicWidth()) * this.rightDrawableScale)) + this.drawablePadding : i;
    }

    public Paint getPaint() {
        return this.textPaint;
    }

    private void calcOffset(int i) {
        if (this.layout.getLineCount() > 0) {
            boolean z = false;
            this.textWidth = (int) Math.ceil((double) this.layout.getLineWidth(0));
            if (this.fullLayout != null) {
                int lineBottom = this.layout.getLineBottom(0);
                this.textHeight = lineBottom;
                Layout layout2 = this.fullLayout;
                this.textHeight = lineBottom + layout2.getLineBottom(layout2.getLineCount() - 1);
            } else if (this.maxLines <= 1 || this.layout.getLineCount() <= 0) {
                this.textHeight = this.layout.getLineBottom(0);
            } else {
                Layout layout3 = this.layout;
                this.textHeight = layout3.getLineBottom(layout3.getLineCount() - 1);
            }
            int i2 = this.gravity;
            if ((i2 & 7) == 1) {
                this.offsetX = (i - this.textWidth) / 2;
            } else if ((i2 & 7) == 3) {
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

    /* access modifiers changed from: protected */
    public boolean createLayout(int i) {
        CharSequence ellipsize;
        if (this.text != null) {
            try {
                Drawable drawable = this.leftDrawable;
                int intrinsicWidth = drawable != null ? (i - drawable.getIntrinsicWidth()) - this.drawablePadding : i;
                Drawable drawable2 = this.rightDrawable;
                if (drawable2 != null) {
                    intrinsicWidth = (intrinsicWidth - ((int) (((float) drawable2.getIntrinsicWidth()) * this.rightDrawableScale))) - this.drawablePadding;
                }
                if (this.buildFullLayout) {
                    CharSequence ellipsize2 = TextUtils.ellipsize(this.text, this.textPaint, (float) intrinsicWidth, TextUtils.TruncateAt.END);
                    if (!ellipsize2.equals(this.text)) {
                        CharSequence charSequence = this.text;
                        CharSequence charSequence2 = ellipsize2;
                        StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(charSequence, 0, charSequence.length(), this.textPaint, intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, intrinsicWidth, 3, false);
                        this.fullLayout = createStaticLayout;
                        if (createStaticLayout != null) {
                            int lineEnd = createStaticLayout.getLineEnd(0);
                            int lineStart = this.fullLayout.getLineStart(1);
                            CharSequence subSequence = this.text.subSequence(0, lineEnd);
                            CharSequence charSequence3 = this.text;
                            CharSequence subSequence2 = charSequence3.subSequence(lineStart, charSequence3.length());
                            this.layout = new StaticLayout(subSequence, 0, subSequence.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.partLayout = new StaticLayout(charSequence2, lineEnd, charSequence2.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.fullLayout = StaticLayoutEx.createStaticLayout(subSequence2, 0, subSequence2.length(), this.textPaint, intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, intrinsicWidth, 2, false);
                        }
                    } else {
                        CharSequence charSequence4 = ellipsize2;
                        this.layout = new StaticLayout(charSequence4, 0, charSequence4.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        this.fullLayout = null;
                        this.partLayout = null;
                    }
                } else if (this.maxLines > 1) {
                    CharSequence charSequence5 = this.text;
                    this.layout = StaticLayoutEx.createStaticLayout(charSequence5, 0, charSequence5.length(), this.textPaint, intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, intrinsicWidth, this.maxLines, false);
                } else {
                    if (this.scrollNonFitText) {
                        ellipsize = this.text;
                    } else {
                        ellipsize = TextUtils.ellipsize(this.text, this.textPaint, (float) intrinsicWidth, TextUtils.TruncateAt.END);
                    }
                    CharSequence charSequence6 = ellipsize;
                    this.layout = new StaticLayout(charSequence6, 0, charSequence6.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + intrinsicWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                calcOffset(intrinsicWidth);
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int i3 = this.lastWidth;
        int i4 = AndroidUtilities.displaySize.x;
        if (i3 != i4) {
            this.lastWidth = i4;
            this.scrollingOffset = 0.0f;
            this.currentScrollDelay = 500;
        }
        createLayout((size - getPaddingLeft()) - getPaddingRight());
        if (View.MeasureSpec.getMode(i2) != NUM) {
            size2 = this.textHeight;
        }
        setMeasuredDimension(size, size2);
        if ((this.gravity & 112) == 16) {
            this.offsetY = ((getMeasuredHeight() - this.textHeight) / 2) + getPaddingTop();
        } else {
            this.offsetY = getPaddingTop();
        }
    }

    /* access modifiers changed from: protected */
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

    public void setMinWidth(int i) {
        this.minWidth = i;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        if (this.maxLines > 1) {
            super.setBackgroundDrawable(drawable);
        } else {
            this.wrapBackgroundDrawable = drawable;
        }
    }

    public Drawable getBackground() {
        Drawable drawable = this.wrapBackgroundDrawable;
        if (drawable != null) {
            return drawable;
        }
        return super.getBackground();
    }

    public void setLeftDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback((Drawable.Callback) null);
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
                drawable2.setCallback((Drawable.Callback) null);
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

    public void setRightDrawableScale(float f) {
        this.rightDrawableScale = f;
    }

    public void setSideDrawablesColor(int i) {
        Theme.setDrawableColor(this.rightDrawable, i);
        Theme.setDrawableColor(this.leftDrawable, i);
    }

    public boolean setText(CharSequence charSequence) {
        return setText(charSequence, false);
    }

    public boolean setText(CharSequence charSequence, boolean z) {
        CharSequence charSequence2 = this.text;
        if (charSequence2 == null && charSequence == null) {
            return false;
        }
        if (!z && charSequence2 != null && charSequence2.equals(charSequence)) {
            return false;
        }
        this.text = charSequence;
        this.scrollingOffset = 0.0f;
        this.currentScrollDelay = 500;
        recreateLayoutMaybe();
        return true;
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
        if (!this.wasLayout || getMeasuredHeight() == 0 || this.buildFullLayout) {
            requestLayout();
            return true;
        }
        boolean createLayout = createLayout((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        if ((this.gravity & 112) == 16) {
            this.offsetY = ((getMeasuredHeight() - this.textHeight) / 2) + getPaddingTop();
        } else {
            this.offsetY = getPaddingTop();
        }
        return createLayout;
    }

    public CharSequence getText() {
        CharSequence charSequence = this.text;
        return charSequence == null ? "" : charSequence;
    }

    public int getLineCount() {
        Layout layout2 = this.layout;
        int i = 0;
        if (layout2 != null) {
            i = 0 + layout2.getLineCount();
        }
        Layout layout3 = this.fullLayout;
        return layout3 != null ? i + layout3.getLineCount() : i;
    }

    public int getTextStartX() {
        int i = 0;
        if (this.layout == null) {
            return 0;
        }
        Drawable drawable = this.leftDrawable;
        if (drawable != null && (this.gravity & 7) == 3) {
            i = 0 + this.drawablePadding + drawable.getIntrinsicWidth();
        }
        return ((int) getX()) + this.offsetX + i;
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        super.onDraw(canvas);
        boolean z = this.scrollNonFitText && (this.textDoesNotFit || this.scrollingOffset != 0.0f);
        int i4 = Integer.MIN_VALUE;
        if (z) {
            i4 = canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), 255, 31);
        }
        this.totalWidth = this.textWidth;
        Drawable drawable = this.leftDrawable;
        if (drawable != null) {
            int i5 = (int) (-this.scrollingOffset);
            int i6 = this.gravity;
            if ((i6 & 7) == 1) {
                i5 += this.offsetX;
            }
            if ((i6 & 112) == 16) {
                i3 = ((getMeasuredHeight() - this.leftDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            } else {
                i3 = this.leftDrawableTopPadding + ((this.textHeight - drawable.getIntrinsicHeight()) / 2);
            }
            Drawable drawable2 = this.leftDrawable;
            drawable2.setBounds(i5, i3, drawable2.getIntrinsicWidth() + i5, this.leftDrawable.getIntrinsicHeight() + i3);
            this.leftDrawable.draw(canvas);
            int i7 = this.gravity;
            if ((i7 & 7) == 3 || (i7 & 7) == 1) {
                i = this.drawablePadding + this.leftDrawable.getIntrinsicWidth() + 0;
            } else {
                i = 0;
            }
            this.totalWidth += this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
        } else {
            i = 0;
        }
        Drawable drawable3 = this.rightDrawable;
        if (drawable3 != null) {
            int i8 = this.textWidth + i + this.drawablePadding + ((int) (-this.scrollingOffset));
            int i9 = this.gravity;
            if ((i9 & 7) == 1) {
                i2 = this.offsetX;
            } else {
                if ((i9 & 7) == 5) {
                    i2 = this.offsetX;
                }
                int intrinsicWidth = (int) (((float) drawable3.getIntrinsicWidth()) * this.rightDrawableScale);
                int intrinsicHeight = (int) (((float) this.rightDrawable.getIntrinsicHeight()) * this.rightDrawableScale);
                int i10 = ((this.textHeight - intrinsicHeight) / 2) + this.rightDrawableTopPadding;
                this.rightDrawable.setBounds(i8, i10, i8 + intrinsicWidth, intrinsicHeight + i10);
                this.rightDrawable.draw(canvas);
                this.totalWidth += this.drawablePadding + intrinsicWidth;
            }
            i8 += i2;
            int intrinsicWidth2 = (int) (((float) drawable3.getIntrinsicWidth()) * this.rightDrawableScale);
            int intrinsicHeight2 = (int) (((float) this.rightDrawable.getIntrinsicHeight()) * this.rightDrawableScale);
            int i102 = ((this.textHeight - intrinsicHeight2) / 2) + this.rightDrawableTopPadding;
            this.rightDrawable.setBounds(i8, i102, i8 + intrinsicWidth2, intrinsicHeight2 + i102);
            this.rightDrawable.draw(canvas);
            this.totalWidth += this.drawablePadding + intrinsicWidth2;
        }
        int dp = this.totalWidth + AndroidUtilities.dp(16.0f);
        float f = this.scrollingOffset;
        if (f != 0.0f) {
            Drawable drawable4 = this.leftDrawable;
            if (drawable4 != null) {
                int i11 = ((int) (-f)) + dp;
                int intrinsicHeight3 = ((this.textHeight - drawable4.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
                Drawable drawable5 = this.leftDrawable;
                drawable5.setBounds(i11, intrinsicHeight3, drawable5.getIntrinsicWidth() + i11, this.leftDrawable.getIntrinsicHeight() + intrinsicHeight3);
                this.leftDrawable.draw(canvas);
            }
            Drawable drawable6 = this.rightDrawable;
            if (drawable6 != null) {
                int i12 = this.textWidth + i + this.drawablePadding + ((int) (-this.scrollingOffset)) + dp;
                int intrinsicHeight4 = ((this.textHeight - drawable6.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
                Drawable drawable7 = this.rightDrawable;
                drawable7.setBounds(i12, intrinsicHeight4, drawable7.getIntrinsicWidth() + i12, this.rightDrawable.getIntrinsicHeight() + intrinsicHeight4);
                this.rightDrawable.draw(canvas);
            }
        }
        if (this.layout != null) {
            if (this.wrapBackgroundDrawable != null) {
                int i13 = this.textWidth;
                int i14 = ((int) (((float) (this.offsetX + i)) - this.scrollingOffset)) + (i13 / 2);
                int max = Math.max(i13 + getPaddingLeft() + getPaddingRight(), this.minWidth);
                int i15 = i14 - (max / 2);
                this.wrapBackgroundDrawable.setBounds(i15, 0, max + i15, getMeasuredHeight());
                this.wrapBackgroundDrawable.draw(canvas);
            }
            if (!(this.offsetX + i == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.save();
                canvas.translate(((float) (this.offsetX + i)) - this.scrollingOffset, (float) this.offsetY);
            }
            this.layout.draw(canvas);
            if (this.partLayout != null && this.fullAlpha < 1.0f) {
                int alpha = this.textPaint.getAlpha();
                this.textPaint.setAlpha((int) ((1.0f - this.fullAlpha) * 255.0f));
                canvas.save();
                canvas.translate(this.layout.getLineWidth(0), 0.0f);
                this.partLayout.draw(canvas);
                canvas.restore();
                this.textPaint.setAlpha(alpha);
            }
            if (this.fullLayout != null && this.fullAlpha > 0.0f) {
                int alpha2 = this.textPaint.getAlpha();
                this.textPaint.setAlpha((int) (this.fullAlpha * 255.0f));
                canvas.translate(0.0f, (float) this.layout.getLineBottom(0));
                this.fullLayout.draw(canvas);
                this.textPaint.setAlpha(alpha2);
            }
            if (this.scrollingOffset != 0.0f) {
                canvas.translate((float) dp, 0.0f);
                this.layout.draw(canvas);
            }
            if (!(this.offsetX + i == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.restore();
            }
            if (z) {
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(10.0f))) {
                    this.fadePaint.setAlpha((int) ((this.scrollingOffset / ((float) AndroidUtilities.dp(10.0f))) * 255.0f));
                } else if (this.scrollingOffset > ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) {
                    this.fadePaint.setAlpha((int) ((1.0f - ((this.scrollingOffset - ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) / ((float) AndroidUtilities.dp(10.0f)))) * 255.0f));
                } else {
                    this.fadePaint.setAlpha(255);
                }
                canvas.drawRect(0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), (float) getMeasuredHeight(), this.fadePaint);
                canvas.save();
                canvas.translate((float) (getMeasuredWidth() - AndroidUtilities.dp(6.0f)), 0.0f);
                canvas.drawRect(0.0f, 0.0f, (float) AndroidUtilities.dp(6.0f), (float) getMeasuredHeight(), this.fadePaintBack);
                canvas.restore();
            }
            updateScrollAnimation();
        }
        if (z) {
            canvas.restoreToCount(i4);
        }
    }

    private void updateScrollAnimation() {
        if (!this.scrollNonFitText) {
            return;
        }
        if (this.textDoesNotFit || this.scrollingOffset != 0.0f) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            int i = this.currentScrollDelay;
            if (i > 0) {
                this.currentScrollDelay = (int) (((long) i) - j);
            } else {
                int dp = this.totalWidth + AndroidUtilities.dp(16.0f);
                float f = 50.0f;
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(100.0f))) {
                    f = ((this.scrollingOffset / ((float) AndroidUtilities.dp(100.0f))) * 20.0f) + 30.0f;
                } else if (this.scrollingOffset >= ((float) (dp - AndroidUtilities.dp(100.0f)))) {
                    f = 50.0f - (((this.scrollingOffset - ((float) (dp - AndroidUtilities.dp(100.0f)))) / ((float) AndroidUtilities.dp(100.0f))) * 20.0f);
                }
                float dp2 = this.scrollingOffset + ((((float) j) / 1000.0f) * ((float) AndroidUtilities.dp(f)));
                this.scrollingOffset = dp2;
                this.lastUpdateTime = elapsedRealtime;
                if (dp2 > ((float) dp)) {
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
        Drawable drawable3 = this.rightDrawable;
        if (drawable == drawable3) {
            invalidate(drawable3.getBounds());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setVisibleToUser(true);
        accessibilityNodeInfo.setClassName("android.widget.TextView");
        accessibilityNodeInfo.setText(this.text);
    }
}
