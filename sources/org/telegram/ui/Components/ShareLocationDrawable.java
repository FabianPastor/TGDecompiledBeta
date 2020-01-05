package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class ShareLocationDrawable extends Drawable {
    private int currentType;
    private Drawable drawable;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private long lastUpdateTime = 0;
    private float[] progress = new float[]{0.0f, -0.5f};

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public ShareLocationDrawable(Context context, int i) {
        this.currentType = i;
        if (i == 3) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else if (i == 2) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else if (i == 1) {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        } else {
            this.drawable = context.getResources().getDrawable(NUM);
            this.drawableLeft = context.getResources().getDrawable(NUM);
            this.drawableRight = context.getResources().getDrawable(NUM);
        }
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        currentTimeMillis = 16;
        if (j <= 16) {
            currentTimeMillis = j;
        }
        for (int i = 0; i < 2; i++) {
            float[] fArr = this.progress;
            if (fArr[i] >= 1.0f) {
                fArr[i] = 0.0f;
            }
            fArr = this.progress;
            fArr[i] = fArr[i] + (((float) currentTimeMillis) / 1300.0f);
            if (fArr[i] > 1.0f) {
                fArr[i] = 1.0f;
            }
        }
        invalidateSelf();
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x016a  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0166  */
    public void draw(android.graphics.Canvas r17) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = r0.currentType;
        r3 = 3;
        r4 = 1;
        r5 = 2;
        if (r2 != r3) goto L_0x0012;
    L_0x000b:
        r2 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x002a;
    L_0x0012:
        if (r2 != r5) goto L_0x001b;
    L_0x0014:
        r2 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x002a;
    L_0x001b:
        if (r2 != r4) goto L_0x0024;
    L_0x001d:
        r2 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x002a;
    L_0x0024:
        r2 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
    L_0x002a:
        r6 = r16.getBounds();
        r6 = r6.top;
        r7 = r16.getIntrinsicHeight();
        r7 = r7 - r2;
        r7 = r7 / r5;
        r6 = r6 + r7;
        r7 = r16.getBounds();
        r7 = r7.left;
        r8 = r16.getIntrinsicWidth();
        r8 = r8 - r2;
        r8 = r8 / r5;
        r7 = r7 + r8;
        r2 = r0.drawable;
        r8 = r2.getIntrinsicWidth();
        r8 = r8 + r7;
        r9 = r0.drawable;
        r9 = r9.getIntrinsicHeight();
        r9 = r9 + r6;
        r2.setBounds(r7, r6, r8, r9);
        r2 = r0.drawable;
        r2.draw(r1);
        r2 = 0;
    L_0x005b:
        if (r2 >= r5) goto L_0x01a3;
    L_0x005d:
        r8 = r0.progress;
        r9 = r8[r2];
        r10 = 0;
        r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1));
        if (r9 >= 0) goto L_0x0068;
    L_0x0066:
        goto L_0x019e;
    L_0x0068:
        r8 = r8[r2];
        r9 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r8 = r8 * r9;
        r8 = r8 + r9;
        r10 = r0.currentType;
        r11 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r12 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r13 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r14 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r10 != r3) goto L_0x00b5;
    L_0x007d:
        r13 = r13 * r8;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r8 = r8 * r12;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r0.progress;
        r12 = r12[r2];
        r12 = r12 * r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r12 = r12 + r7;
        r12 = r12 - r11;
        r13 = r0.drawable;
        r13 = r13.getIntrinsicHeight();
        r13 = r13 / r5;
        r13 = r13 + r6;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r13 = r13 - r14;
        r14 = r0.drawable;
        r14 = r14.getIntrinsicWidth();
        r14 = r14 + r7;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
    L_0x00b1:
        r14 = r14 - r15;
        r14 = r14 + r11;
        goto L_0x015e;
    L_0x00b5:
        if (r10 != r5) goto L_0x00e7;
    L_0x00b7:
        r13 = r13 * r8;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r8 = r8 * r12;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r0.progress;
        r12 = r12[r2];
        r12 = r12 * r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r12 = r12 + r7;
        r12 = r12 - r11;
        r13 = r0.drawable;
        r13 = r13.getIntrinsicHeight();
        r13 = r13 / r5;
        r13 = r13 + r6;
        r14 = r0.drawable;
        r14 = r14.getIntrinsicWidth();
        r14 = r14 + r7;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x00b1;
    L_0x00e7:
        if (r10 != r4) goto L_0x0122;
    L_0x00e9:
        r10 = NUM; // 0x40200000 float:2.5 double:5.315350785E-315;
        r10 = r10 * r8;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = NUM; // 0x40d00000 float:6.5 double:5.372337977E-315;
        r8 = r8 * r11;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r11 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r12 = r0.progress;
        r12 = r12[r2];
        r12 = r12 * r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r12 = r12 + r7;
        r12 = r12 - r11;
        r13 = r0.drawable;
        r13 = r13.getIntrinsicHeight();
        r13 = r13 / r5;
        r13 = r13 + r6;
        r15 = r0.drawable;
        r15 = r15.getIntrinsicWidth();
        r15 = r15 + r7;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r15 = r15 - r14;
        r14 = r15 + r11;
        goto L_0x015e;
    L_0x0122:
        r13 = r13 * r8;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r8 = r8 * r12;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r0.progress;
        r12 = r12[r2];
        r12 = r12 * r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r13 = r13 + r7;
        r13 = r13 - r11;
        r15 = r0.drawable;
        r15 = r15.getIntrinsicHeight();
        r15 = r15 / r5;
        r15 = r15 + r6;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = r15 - r14;
        r15 = r0.drawable;
        r15 = r15.getIntrinsicWidth();
        r15 = r15 + r7;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r15 = r15 - r12;
        r11 = r11 + r15;
        r12 = r13;
        r13 = r14;
        r14 = r11;
    L_0x015e:
        r11 = r0.progress;
        r15 = r11[r2];
        r15 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1));
        if (r15 >= 0) goto L_0x016a;
    L_0x0166:
        r11 = r11[r2];
        r11 = r11 / r9;
        goto L_0x0172;
    L_0x016a:
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = r11[r2];
        r11 = r11 - r9;
        r11 = r11 / r9;
        r11 = r15 - r11;
    L_0x0172:
        r9 = r0.drawableLeft;
        r15 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r11 = r11 * r15;
        r11 = (int) r11;
        r9.setAlpha(r11);
        r9 = r0.drawableLeft;
        r15 = r12 - r10;
        r3 = r13 - r8;
        r12 = r12 + r10;
        r13 = r13 + r8;
        r9.setBounds(r15, r3, r12, r13);
        r8 = r0.drawableLeft;
        r8.draw(r1);
        r8 = r0.drawableRight;
        r8.setAlpha(r11);
        r8 = r0.drawableRight;
        r9 = r14 - r10;
        r14 = r14 + r10;
        r8.setBounds(r9, r3, r14, r13);
        r3 = r0.drawableRight;
        r3.draw(r1);
    L_0x019e:
        r2 = r2 + 1;
        r3 = 3;
        goto L_0x005b;
    L_0x01a3:
        r16.update();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareLocationDrawable.draw(android.graphics.Canvas):void");
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.drawable.setColorFilter(colorFilter);
        this.drawableLeft.setColorFilter(colorFilter);
        this.drawableRight.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        int i = this.currentType;
        if (i == 3) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i == 2) {
            return AndroidUtilities.dp(74.0f);
        }
        if (i == 1) {
            return AndroidUtilities.dp(40.0f);
        }
        return AndroidUtilities.dp(120.0f);
    }

    public int getIntrinsicHeight() {
        int i = this.currentType;
        if (i == 3) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i == 2) {
            return AndroidUtilities.dp(74.0f);
        }
        if (i == 1) {
            return AndroidUtilities.dp(40.0f);
        }
        return AndroidUtilities.dp(180.0f);
    }
}
