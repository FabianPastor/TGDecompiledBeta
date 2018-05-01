package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;

public class SimpleTextView extends View implements Callback {
    private int drawablePadding = AndroidUtilities.dp(4.0f);
    private int gravity = 51;
    private Layout layout;
    private Drawable leftDrawable;
    private int leftDrawableTopPadding;
    private int offsetX;
    private Drawable rightDrawable;
    private int rightDrawableTopPadding;
    private SpannableStringBuilder spannableStringBuilder;
    private CharSequence text;
    private int textHeight;
    private TextPaint textPaint = new TextPaint(1);
    private int textWidth;
    private boolean wasLayout;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public SimpleTextView(Context context) {
        super(context);
    }

    public void setTextColor(int i) {
        this.textPaint.setColor(i);
        invalidate();
    }

    public void setLinkTextColor(int i) {
        this.textPaint.linkColor = i;
        invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setTextSize(int i) {
        i = (float) AndroidUtilities.dp((float) i);
        if (i != this.textPaint.getTextSize()) {
            this.textPaint.setTextSize(i);
            if (recreateLayoutMaybe() == 0) {
                invalidate();
            }
        }
    }

    public void setGravity(int i) {
        this.gravity = i;
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public int getSideDrawablesSize() {
        int i = 0;
        if (this.leftDrawable != null) {
            i = 0 + (this.leftDrawable.getIntrinsicWidth() + this.drawablePadding);
        }
        return this.rightDrawable != null ? i + (this.rightDrawable.getIntrinsicWidth() + this.drawablePadding) : i;
    }

    public Paint getPaint() {
        return this.textPaint;
    }

    private void calcOffset(int i) {
        if (this.layout.getLineCount() > 0) {
            this.textWidth = (int) Math.ceil((double) this.layout.getLineWidth(0));
            this.textHeight = this.layout.getLineBottom(0);
            if ((this.gravity & 7) == 3) {
                this.offsetX = -((int) this.layout.getLineLeft(0));
            } else if (this.layout.getLineLeft(0) == 0.0f) {
                this.offsetX = i - this.textWidth;
            } else {
                this.offsetX = -AndroidUtilities.dp(NUM);
            }
            this.offsetX += getPaddingLeft();
        }
    }

    private boolean createLayout(int r15) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r14 = this;
        r0 = r14.text;
        if (r0 == 0) goto L_0x004c;
    L_0x0004:
        r0 = r14.leftDrawable;	 Catch:{ Exception -> 0x0054 }
        if (r0 == 0) goto L_0x0012;	 Catch:{ Exception -> 0x0054 }
    L_0x0008:
        r0 = r14.leftDrawable;	 Catch:{ Exception -> 0x0054 }
        r0 = r0.getIntrinsicWidth();	 Catch:{ Exception -> 0x0054 }
        r15 = r15 - r0;	 Catch:{ Exception -> 0x0054 }
        r0 = r14.drawablePadding;	 Catch:{ Exception -> 0x0054 }
        r15 = r15 - r0;	 Catch:{ Exception -> 0x0054 }
    L_0x0012:
        r0 = r14.rightDrawable;	 Catch:{ Exception -> 0x0054 }
        if (r0 == 0) goto L_0x0020;	 Catch:{ Exception -> 0x0054 }
    L_0x0016:
        r0 = r14.rightDrawable;	 Catch:{ Exception -> 0x0054 }
        r0 = r0.getIntrinsicWidth();	 Catch:{ Exception -> 0x0054 }
        r15 = r15 - r0;	 Catch:{ Exception -> 0x0054 }
        r0 = r14.drawablePadding;	 Catch:{ Exception -> 0x0054 }
        r15 = r15 - r0;	 Catch:{ Exception -> 0x0054 }
    L_0x0020:
        r0 = r14.text;	 Catch:{ Exception -> 0x0054 }
        r1 = r14.textPaint;	 Catch:{ Exception -> 0x0054 }
        r2 = (float) r15;	 Catch:{ Exception -> 0x0054 }
        r3 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0054 }
        r5 = android.text.TextUtils.ellipsize(r0, r1, r2, r3);	 Catch:{ Exception -> 0x0054 }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0054 }
        r6 = 0;	 Catch:{ Exception -> 0x0054 }
        r7 = r5.length();	 Catch:{ Exception -> 0x0054 }
        r8 = r14.textPaint;	 Catch:{ Exception -> 0x0054 }
        r1 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;	 Catch:{ Exception -> 0x0054 }
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);	 Catch:{ Exception -> 0x0054 }
        r9 = r15 + r1;	 Catch:{ Exception -> 0x0054 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0054 }
        r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0054 }
        r12 = 0;	 Catch:{ Exception -> 0x0054 }
        r13 = 0;	 Catch:{ Exception -> 0x0054 }
        r4 = r0;	 Catch:{ Exception -> 0x0054 }
        r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0054 }
        r14.layout = r0;	 Catch:{ Exception -> 0x0054 }
        r14.calcOffset(r15);	 Catch:{ Exception -> 0x0054 }
        goto L_0x0054;
    L_0x004c:
        r15 = 0;
        r14.layout = r15;
        r15 = 0;
        r14.textWidth = r15;
        r14.textHeight = r15;
    L_0x0054:
        r14.invalidate();
        r15 = 1;
        return r15;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.SimpleTextView.createLayout(int):boolean");
    }

    protected void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        int size = MeasureSpec.getSize(i2);
        createLayout((i - getPaddingLeft()) - getPaddingRight());
        if (MeasureSpec.getMode(i2) != NUM) {
            size = this.textHeight;
        }
        setMeasuredDimension(i, size);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
        setLeftDrawable(i == 0 ? 0 : getContext().getResources().getDrawable(i));
    }

    public void setRightDrawable(int i) {
        setRightDrawable(i == 0 ? 0 : getContext().getResources().getDrawable(i));
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
            if (recreateLayoutMaybe() == null) {
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
            if (recreateLayoutMaybe() == null) {
                invalidate();
            }
        }
    }

    public void setText(CharSequence charSequence) {
        setText(charSequence, false);
    }

    public void setText(CharSequence charSequence, boolean z) {
        if (!(this.text == null && charSequence == null) && (z || !this.text || charSequence == null || !this.text.equals(charSequence))) {
            this.text = charSequence;
            recreateLayoutMaybe();
        }
    }

    public void setDrawablePadding(int i) {
        if (this.drawablePadding != i) {
            this.drawablePadding = i;
            if (recreateLayoutMaybe() == 0) {
                invalidate();
            }
        }
    }

    private boolean recreateLayoutMaybe() {
        if (this.wasLayout) {
            return createLayout(getMeasuredWidth());
        }
        requestLayout();
        return true;
    }

    public CharSequence getText() {
        if (this.text == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        return this.text;
    }

    public int getTextStartX() {
        int i = 0;
        if (this.layout == null) {
            return 0;
        }
        if (this.leftDrawable != null && (this.gravity & 7) == 3) {
            i = 0 + (this.drawablePadding + this.leftDrawable.getIntrinsicWidth());
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

    protected void onDraw(Canvas canvas) {
        int i = 0;
        if (this.leftDrawable != null) {
            int intrinsicHeight = ((this.textHeight - this.leftDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            this.leftDrawable.setBounds(0, intrinsicHeight, this.leftDrawable.getIntrinsicWidth(), this.leftDrawable.getIntrinsicHeight() + intrinsicHeight);
            this.leftDrawable.draw(canvas);
            if ((this.gravity & 7) == 3) {
                i = 0 + (this.drawablePadding + this.leftDrawable.getIntrinsicWidth());
            }
        }
        if (this.rightDrawable != null) {
            intrinsicHeight = (this.textWidth + i) + this.drawablePadding;
            int intrinsicHeight2 = ((this.textHeight - this.rightDrawable.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
            this.rightDrawable.setBounds(intrinsicHeight, intrinsicHeight2, this.rightDrawable.getIntrinsicWidth() + intrinsicHeight, this.rightDrawable.getIntrinsicHeight() + intrinsicHeight2);
            this.rightDrawable.draw(canvas);
        }
        if (this.layout != null) {
            if (this.offsetX + i != 0) {
                canvas.save();
                canvas.translate((float) (this.offsetX + i), 0.0f);
            }
            this.layout.draw(canvas);
            if (this.offsetX + i != 0) {
                canvas.restore();
            }
        }
    }

    public void invalidateDrawable(Drawable drawable) {
        if (drawable == this.leftDrawable) {
            invalidate(this.leftDrawable.getBounds());
        } else if (drawable == this.rightDrawable) {
            invalidate(this.rightDrawable.getBounds());
        }
    }
}
