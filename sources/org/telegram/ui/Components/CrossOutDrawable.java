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

    public CrossOutDrawable(Context context, int iconRes, String colorKey2) {
        Paint paint2 = new Paint(1);
        this.xRefPaint = paint2;
        this.iconDrawable = ContextCompat.getDrawable(context, iconRes);
        this.colorKey = colorKey2;
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        paint2.setColor(-16777216);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(AndroidUtilities.dpf2(2.5f));
    }

    public void setCrossOut(boolean cross2, boolean animated) {
        if (this.cross != cross2) {
            this.cross = cross2;
            float f = 0.0f;
            if (!animated) {
                if (cross2) {
                    f = 1.0f;
                }
                this.progress = f;
            } else {
                if (!cross2) {
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
    public void draw(android.graphics.Canvas r14) {
        /*
            r13 = this;
            boolean r0 = r13.cross
            r1 = 1037726734(0x3dda740e, float:0.10666667)
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x001f
            float r4 = r13.progress
            int r5 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x001f
            float r4 = r4 + r1
            r13.progress = r4
            r13.invalidateSelf()
            float r0 = r13.progress
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0035
            r13.progress = r3
            goto L_0x0035
        L_0x001f:
            if (r0 != 0) goto L_0x0035
            float r0 = r13.progress
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0035
            float r0 = r0 - r1
            r13.progress = r0
            r13.invalidateSelf()
            float r0 = r13.progress
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0035
            r13.progress = r2
        L_0x0035:
            java.lang.String r0 = r13.colorKey
            if (r0 != 0) goto L_0x003b
            r0 = -1
            goto L_0x003f
        L_0x003b:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
        L_0x003f:
            int r1 = r13.color
            if (r1 == r0) goto L_0x0056
            r13.color = r0
            android.graphics.Paint r1 = r13.paint
            r1.setColor(r0)
            android.graphics.drawable.Drawable r1 = r13.iconDrawable
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r0, r5)
            r1.setColorFilter(r4)
        L_0x0056:
            float r1 = r13.progress
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x0062
            android.graphics.drawable.Drawable r1 = r13.iconDrawable
            r1.draw(r14)
            return
        L_0x0062:
            android.graphics.RectF r1 = r13.rectF
            android.graphics.drawable.Drawable r2 = r13.iconDrawable
            android.graphics.Rect r2 = r2.getBounds()
            r1.set(r2)
            android.graphics.RectF r1 = r13.rectF
            r2 = 255(0xff, float:3.57E-43)
            r4 = 31
            r14.saveLayerAlpha(r1, r2, r4)
            android.graphics.drawable.Drawable r1 = r13.iconDrawable
            r1.draw(r14)
            android.graphics.RectF r1 = r13.rectF
            float r1 = r1.left
            r2 = 1083179008(0x40900000, float:4.5)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            float r1 = r1 + r4
            float r4 = r13.xOffset
            float r1 = r1 + r4
            float r4 = r13.lenOffsetTop
            float r1 = r1 + r4
            android.graphics.RectF r4 = r13.rectF
            float r4 = r4.top
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            float r4 = r4 + r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r2 = (float) r2
            float r4 = r4 - r2
            float r2 = r13.lenOffsetTop
            float r4 = r4 + r2
            android.graphics.RectF r2 = r13.rectF
            float r2 = r2.right
            r5 = 1077936128(0x40400000, float:3.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            float r2 = r2 - r6
            float r6 = r13.xOffset
            float r2 = r2 + r6
            float r6 = r13.lenOffsetBottom
            float r2 = r2 - r6
            android.graphics.RectF r6 = r13.rectF
            float r6 = r6.bottom
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            float r6 = r6 - r7
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r6 - r5
            float r5 = r13.lenOffsetBottom
            float r6 = r6 - r5
            boolean r5 = r13.cross
            if (r5 == 0) goto L_0x00d6
            float r3 = r2 - r1
            float r5 = r13.progress
            float r3 = r3 * r5
            float r2 = r1 + r3
            float r3 = r6 - r4
            float r3 = r3 * r5
            float r6 = r4 + r3
            goto L_0x00e5
        L_0x00d6:
            float r5 = r2 - r1
            float r7 = r13.progress
            float r8 = r3 - r7
            float r5 = r5 * r8
            float r1 = r1 + r5
            float r5 = r6 - r4
            float r3 = r3 - r7
            float r5 = r5 * r3
            float r4 = r4 + r5
        L_0x00e5:
            android.graphics.Paint r3 = r13.paint
            float r3 = r3.getStrokeWidth()
            float r9 = r4 - r3
            android.graphics.Paint r3 = r13.paint
            float r3 = r3.getStrokeWidth()
            float r11 = r6 - r3
            android.graphics.Paint r12 = r13.xRefPaint
            r7 = r14
            r8 = r1
            r10 = r2
            r7.drawLine(r8, r9, r10, r11, r12)
            android.graphics.Paint r12 = r13.paint
            r9 = r4
            r11 = r6
            r7.drawLine(r8, r9, r10, r11, r12)
            r14.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CrossOutDrawable.draw(android.graphics.Canvas):void");
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.iconDrawable.setBounds(left, top, right, bottom);
    }

    public int getIntrinsicHeight() {
        return this.iconDrawable.getIntrinsicHeight();
    }

    public int getIntrinsicWidth() {
        return this.iconDrawable.getIntrinsicWidth();
    }

    public int getOpacity() {
        return -2;
    }

    public void setColorKey(String colorKey2) {
        this.colorKey = colorKey2;
    }

    public void setOffsets(float xOffset2, float lenOffsetTop2, float lenOffsetBottom2) {
        this.xOffset = xOffset2;
        this.lenOffsetTop = lenOffsetTop2;
        this.lenOffsetBottom = lenOffsetBottom2;
        invalidateSelf();
    }

    public void setStrokeWidth(float w) {
        this.paint.setStrokeWidth(w);
        this.xRefPaint.setStrokeWidth(1.47f * w);
    }

    public float getProgress() {
        return this.progress;
    }
}
