package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.widget.FrameLayout;
import org.telegram.messenger.SharedConfig;

public class BlurredFrameLayout extends FrameLayout {
    public int backgroundColor = 0;
    public int backgroundPaddingBottom;
    public int backgroundPaddingTop;
    protected Paint backgroundPaint;
    public boolean drawBlur = true;
    public boolean isTopView = true;
    private final SizeNotifierFrameLayout sizeNotifierFrameLayout;

    public BlurredFrameLayout(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout2) {
        super(context);
        this.sizeNotifierFrameLayout = sizeNotifierFrameLayout2;
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.view.ViewParent] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r9) {
        /*
            r8 = this;
            boolean r0 = org.telegram.messenger.SharedConfig.chatBlurEnabled()
            if (r0 == 0) goto L_0x0055
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r8.sizeNotifierFrameLayout
            if (r0 == 0) goto L_0x0055
            boolean r0 = r8.drawBlur
            if (r0 == 0) goto L_0x0055
            int r0 = r8.backgroundColor
            if (r0 == 0) goto L_0x0055
            android.graphics.Paint r0 = r8.backgroundPaint
            if (r0 != 0) goto L_0x001d
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.backgroundPaint = r0
        L_0x001d:
            android.graphics.Paint r0 = r8.backgroundPaint
            int r1 = r8.backgroundColor
            r0.setColor(r1)
            android.graphics.Rect r0 = org.telegram.messenger.AndroidUtilities.rectTmp2
            r1 = 0
            int r2 = r8.backgroundPaddingTop
            int r3 = r8.getMeasuredWidth()
            int r4 = r8.getMeasuredHeight()
            int r5 = r8.backgroundPaddingBottom
            int r4 = r4 - r5
            r0.set(r1, r2, r3, r4)
            r0 = 0
            r1 = r8
        L_0x0039:
            org.telegram.ui.Components.SizeNotifierFrameLayout r2 = r8.sizeNotifierFrameLayout
            if (r1 == r2) goto L_0x004a
            float r2 = r1.getY()
            float r0 = r0 + r2
            android.view.ViewParent r2 = r1.getParent()
            r1 = r2
            android.view.View r1 = (android.view.View) r1
            goto L_0x0039
        L_0x004a:
            android.graphics.Rect r5 = org.telegram.messenger.AndroidUtilities.rectTmp2
            android.graphics.Paint r6 = r8.backgroundPaint
            boolean r7 = r8.isTopView
            r3 = r9
            r4 = r0
            r2.drawBlurRect(r3, r4, r5, r6, r7)
        L_0x0055:
            super.dispatchDraw(r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BlurredFrameLayout.dispatchDraw(android.graphics.Canvas):void");
    }

    public void setBackgroundColor(int color) {
        if (!SharedConfig.chatBlurEnabled() || this.sizeNotifierFrameLayout == null) {
            super.setBackgroundColor(color);
        } else {
            this.backgroundColor = color;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        SizeNotifierFrameLayout sizeNotifierFrameLayout2;
        if (SharedConfig.chatBlurEnabled() && (sizeNotifierFrameLayout2 = this.sizeNotifierFrameLayout) != null) {
            sizeNotifierFrameLayout2.blurBehindViews.add(this);
        }
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierFrameLayout;
        if (sizeNotifierFrameLayout2 != null) {
            sizeNotifierFrameLayout2.blurBehindViews.remove(this);
        }
        super.onDetachedFromWindow();
    }
}
