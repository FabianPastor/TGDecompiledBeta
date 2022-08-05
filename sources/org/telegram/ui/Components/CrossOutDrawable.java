package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;

public class CrossOutDrawable extends Drawable {
    int color;
    String colorKey;
    boolean cross;
    Drawable iconDrawable;
    private float lenOffsetBottom;
    private float lenOffsetTop;
    Paint paint = new Paint(1);
    float progress;
    RectF rectF = new RectF();
    private float xOffset;
    final Paint xRefPaint;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public CrossOutDrawable(Context context, int i, String str) {
        Paint paint2 = new Paint(1);
        this.xRefPaint = paint2;
        this.iconDrawable = ContextCompat.getDrawable(context, i);
        this.colorKey = str;
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        paint2.setColor(-16777216);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(AndroidUtilities.dpf2(2.5f));
    }

    public void setCrossOut(boolean z, boolean z2) {
        if (this.cross != z) {
            this.cross = z;
            float f = 0.0f;
            if (!z2) {
                if (z) {
                    f = 1.0f;
                }
                this.progress = f;
            } else {
                if (!z) {
                    f = 1.0f;
                }
                this.progress = f;
            }
            invalidateSelf();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x003b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0062  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r13) {
        /*
            r12 = this;
            boolean r0 = r12.cross
            r1 = 1037726734(0x3dda740e, float:0.10666667)
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x001f
            float r4 = r12.progress
            int r5 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x001f
            float r4 = r4 + r1
            r12.progress = r4
            r12.invalidateSelf()
            float r0 = r12.progress
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0035
            r12.progress = r3
            goto L_0x0035
        L_0x001f:
            if (r0 != 0) goto L_0x0035
            float r0 = r12.progress
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0035
            float r0 = r0 - r1
            r12.progress = r0
            r12.invalidateSelf()
            float r0 = r12.progress
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0035
            r12.progress = r2
        L_0x0035:
            java.lang.String r0 = r12.colorKey
            if (r0 != 0) goto L_0x003b
            r0 = -1
            goto L_0x003f
        L_0x003b:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
        L_0x003f:
            int r1 = r12.color
            if (r1 == r0) goto L_0x0056
            r12.color = r0
            android.graphics.Paint r1 = r12.paint
            r1.setColor(r0)
            android.graphics.drawable.Drawable r1 = r12.iconDrawable
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r0, r5)
            r1.setColorFilter(r4)
        L_0x0056:
            float r0 = r12.progress
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0062
            android.graphics.drawable.Drawable r0 = r12.iconDrawable
            r0.draw(r13)
            return
        L_0x0062:
            android.graphics.RectF r0 = r12.rectF
            android.graphics.drawable.Drawable r1 = r12.iconDrawable
            android.graphics.Rect r1 = r1.getBounds()
            r0.set(r1)
            android.graphics.RectF r0 = r12.rectF
            r1 = 255(0xff, float:3.57E-43)
            r2 = 31
            r13.saveLayerAlpha(r0, r1, r2)
            android.graphics.drawable.Drawable r0 = r12.iconDrawable
            r0.draw(r13)
            android.graphics.RectF r0 = r12.rectF
            float r0 = r0.left
            r1 = 1083179008(0x40900000, float:4.5)
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
            float r0 = r0 + r2
            float r2 = r12.xOffset
            float r0 = r0 + r2
            float r2 = r12.lenOffsetTop
            float r0 = r0 + r2
            android.graphics.RectF r2 = r12.rectF
            float r2 = r2.top
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r1)
            float r2 = r2 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r1 = (float) r1
            float r2 = r2 - r1
            float r1 = r12.lenOffsetTop
            float r2 = r2 + r1
            android.graphics.RectF r1 = r12.rectF
            float r1 = r1.right
            r4 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            float r1 = r1 - r5
            float r5 = r12.xOffset
            float r1 = r1 + r5
            float r5 = r12.lenOffsetBottom
            float r1 = r1 - r5
            android.graphics.RectF r5 = r12.rectF
            float r5 = r5.bottom
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            float r5 = r5 - r6
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r5 - r4
            float r4 = r12.lenOffsetBottom
            float r5 = r5 - r4
            boolean r4 = r12.cross
            if (r4 == 0) goto L_0x00d2
            float r1 = r1 - r0
            float r4 = r12.progress
            float r1 = r1 * r4
            float r1 = r1 + r0
            float r5 = r5 - r2
            float r5 = r5 * r4
            float r5 = r5 + r2
            goto L_0x00e2
        L_0x00d2:
            float r4 = r1 - r0
            float r6 = r12.progress
            float r7 = r3 - r6
            float r4 = r4 * r7
            float r0 = r0 + r4
            float r4 = r5 - r2
            float r6 = r3 - r6
            float r4 = r4 * r6
            float r2 = r2 + r4
        L_0x00e2:
            android.graphics.Paint r4 = r12.paint
            float r4 = r4.getStrokeWidth()
            float r8 = r2 - r4
            android.graphics.Paint r4 = r12.paint
            float r4 = r4.getStrokeWidth()
            float r10 = r5 - r4
            android.graphics.Paint r11 = r12.xRefPaint
            r6 = r13
            r7 = r0
            r9 = r1
            r6.drawLine(r7, r8, r9, r10, r11)
            android.graphics.Paint r4 = r12.xRefPaint
            float r4 = r4.getStrokeWidth()
            android.graphics.Paint r6 = r12.paint
            float r6 = r6.getStrokeWidth()
            float r4 = r4 - r6
            r6 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r6
            float r4 = r4 + r3
            float r8 = r2 - r4
            float r10 = r5 - r4
            android.graphics.Paint r11 = r12.xRefPaint
            r6 = r13
            r6.drawLine(r7, r8, r9, r10, r11)
            android.graphics.Paint r11 = r12.paint
            r8 = r2
            r10 = r5
            r6.drawLine(r7, r8, r9, r10, r11)
            r13.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CrossOutDrawable.draw(android.graphics.Canvas):void");
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.iconDrawable.setBounds(i, i2, i3, i4);
    }

    public int getIntrinsicHeight() {
        return this.iconDrawable.getIntrinsicHeight();
    }

    public int getIntrinsicWidth() {
        return this.iconDrawable.getIntrinsicWidth();
    }

    public void setOffsets(float f, float f2, float f3) {
        this.xOffset = f;
        this.lenOffsetTop = f2;
        this.lenOffsetBottom = f3;
        invalidateSelf();
    }

    public void setStrokeWidth(float f) {
        this.paint.setStrokeWidth(f);
        this.xRefPaint.setStrokeWidth(f * 1.47f);
    }

    public float getProgress() {
        return this.progress;
    }
}
