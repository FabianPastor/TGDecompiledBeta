package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.text.StaticLayout;

public class LinkPath extends Path {
    private boolean allowReset = true;
    private int baselineShift;
    private StaticLayout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop = -1.0f;
    private int lineHeight;
    private RectF rect;
    private boolean useRoundRect;

    public LinkPath(boolean z) {
        this.useRoundRect = z;
    }

    public void setCurrentLayout(StaticLayout staticLayout, int i, float f) {
        this.currentLayout = staticLayout;
        this.currentLine = staticLayout.getLineForOffset(i);
        this.lastTop = -1.0f;
        this.heightOffset = f;
        if (VERSION.SDK_INT >= 28) {
            i = staticLayout.getLineCount();
            if (i > 0) {
                i--;
                this.lineHeight = staticLayout.getLineBottom(i) - staticLayout.getLineTop(i);
            }
        }
    }

    public void setAllowReset(boolean z) {
        this.allowReset = z;
    }

    public void setUseRoundRect(boolean z) {
        this.useRoundRect = z;
    }

    public boolean isUsingRoundRect() {
        return this.useRoundRect;
    }

    public void setBaselineShift(int i) {
        this.baselineShift = i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ba  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008c  */
    public void addRect(float r10, float r11, float r12, float r13, android.graphics.Path.Direction r14) {
        /*
        r9 = this;
        r0 = r9.heightOffset;
        r11 = r11 + r0;
        r13 = r13 + r0;
        r0 = r9.lastTop;
        r1 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r1 != 0) goto L_0x000f;
    L_0x000c:
        r9.lastTop = r11;
        goto L_0x001b;
    L_0x000f:
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x001b;
    L_0x0013:
        r9.lastTop = r11;
        r0 = r9.currentLine;
        r0 = r0 + 1;
        r9.currentLine = r0;
    L_0x001b:
        r0 = r9.currentLayout;
        r1 = r9.currentLine;
        r0 = r0.getLineRight(r1);
        r1 = r9.currentLayout;
        r2 = r9.currentLine;
        r1 = r1.getLineLeft(r2);
        r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r2 >= 0) goto L_0x00bf;
    L_0x002f:
        r2 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r2 > 0) goto L_0x0039;
    L_0x0033:
        r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1));
        if (r2 > 0) goto L_0x0039;
    L_0x0037:
        goto L_0x00bf;
    L_0x0039:
        r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x003f;
    L_0x003d:
        r6 = r0;
        goto L_0x0040;
    L_0x003f:
        r6 = r12;
    L_0x0040:
        r12 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1));
        if (r12 >= 0) goto L_0x0046;
    L_0x0044:
        r4 = r1;
        goto L_0x0047;
    L_0x0046:
        r4 = r10;
    L_0x0047:
        r10 = android.os.Build.VERSION.SDK_INT;
        r12 = 28;
        r0 = 0;
        if (r10 < r12) goto L_0x0069;
    L_0x004e:
        r10 = r13 - r11;
        r12 = r9.lineHeight;
        r12 = (float) r12;
        r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r10 <= 0) goto L_0x007b;
    L_0x0057:
        r10 = r9.currentLayout;
        r10 = r10.getHeight();
        r10 = (float) r10;
        r10 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r10 == 0) goto L_0x007a;
    L_0x0062:
        r10 = r9.currentLayout;
        r0 = r10.getSpacingAdd();
        goto L_0x007a;
    L_0x0069:
        r10 = r9.currentLayout;
        r10 = r10.getHeight();
        r10 = (float) r10;
        r10 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r10 == 0) goto L_0x007a;
    L_0x0074:
        r10 = r9.currentLayout;
        r0 = r10.getSpacingAdd();
    L_0x007a:
        r13 = r13 - r0;
    L_0x007b:
        r10 = r9.baselineShift;
        if (r10 >= 0) goto L_0x0082;
    L_0x007f:
        r10 = (float) r10;
        r13 = r13 + r10;
        goto L_0x0086;
    L_0x0082:
        if (r10 <= 0) goto L_0x0086;
    L_0x0084:
        r10 = (float) r10;
        r11 = r11 + r10;
    L_0x0086:
        r5 = r11;
        r7 = r13;
        r10 = r9.useRoundRect;
        if (r10 == 0) goto L_0x00ba;
    L_0x008c:
        r10 = r9.rect;
        if (r10 != 0) goto L_0x0097;
    L_0x0090:
        r10 = new android.graphics.RectF;
        r10.<init>();
        r9.rect = r10;
    L_0x0097:
        r10 = r9.rect;
        r11 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r12 = (float) r12;
        r4 = r4 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r12 = (float) r12;
        r6 = r6 + r12;
        r10.set(r4, r5, r6, r7);
        r10 = r9.rect;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r12 = (float) r12;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        super.addRoundRect(r10, r12, r11, r14);
        goto L_0x00bf;
    L_0x00ba:
        r3 = r9;
        r8 = r14;
        super.addRect(r4, r5, r6, r7, r8);
    L_0x00bf:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.LinkPath.addRect(float, float, float, float, android.graphics.Path$Direction):void");
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
        }
    }
}
