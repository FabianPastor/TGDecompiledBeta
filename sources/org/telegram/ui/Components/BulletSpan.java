package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

public class BulletSpan implements LeadingMarginSpan {
    private static final int STANDARD_BULLET_RADIUS = 4;
    private static final int STANDARD_COLOR = 0;
    public static final int STANDARD_GAP_WIDTH = 2;
    private final int mBulletRadius;
    private final int mColor;
    private final int mGapWidth;
    private final boolean mWantColor;

    public BulletSpan() {
        this(2, 0, false, 4);
    }

    public BulletSpan(int i) {
        this(i, 0, false, 4);
    }

    public BulletSpan(int i, int i2) {
        this(i, i2, true, 4);
    }

    public BulletSpan(int i, int i2, int i3) {
        this(i, i2, true, i3);
    }

    private BulletSpan(int i, int i2, boolean z, int i3) {
        this.mGapWidth = i;
        this.mBulletRadius = i3;
        this.mColor = i2;
        this.mWantColor = z;
    }

    public BulletSpan(Parcel parcel) {
        this.mGapWidth = parcel.readInt();
        this.mWantColor = parcel.readInt() != 0;
        this.mColor = parcel.readInt();
        this.mBulletRadius = parcel.readInt();
    }

    public int getLeadingMargin(boolean z) {
        return (this.mBulletRadius * 2) + this.mGapWidth;
    }

    public int getGapWidth() {
        return this.mGapWidth;
    }

    public int getBulletRadius() {
        return this.mBulletRadius;
    }

    public int getColor() {
        return this.mColor;
    }

    public void drawLeadingMargin(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5, CharSequence charSequence, int i6, int i7, boolean z, Layout layout) {
        int i8;
        if (((Spanned) charSequence).getSpanStart(this) == i6) {
            Paint.Style style = paint.getStyle();
            int i9 = 0;
            if (this.mWantColor) {
                i8 = paint.getColor();
                paint.setColor(this.mColor);
            } else {
                i8 = 0;
            }
            paint.setStyle(Paint.Style.FILL);
            if (layout != null) {
                if (layout.getLineForOffset(i6) != layout.getLineCount() - 1) {
                    i9 = (int) layout.getSpacingAdd();
                }
                i5 -= i9;
            }
            int i10 = this.mBulletRadius;
            canvas.drawCircle((float) (i + (i2 * i10)), ((float) (i3 + i5)) / 2.0f, (float) i10, paint);
            if (this.mWantColor) {
                paint.setColor(i8);
            }
            paint.setStyle(style);
        }
    }
}
