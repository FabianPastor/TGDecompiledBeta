package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
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

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        SizeNotifierFrameLayout sizeNotifierFrameLayout2;
        if (SharedConfig.chatBlurEnabled() && this.sizeNotifierFrameLayout != null && this.drawBlur && this.backgroundColor != 0) {
            if (this.backgroundPaint == null) {
                this.backgroundPaint = new Paint();
            }
            this.backgroundPaint.setColor(this.backgroundColor);
            AndroidUtilities.rectTmp2.set(0, this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight() - this.backgroundPaddingBottom);
            float f = 0.0f;
            View view = this;
            while (true) {
                sizeNotifierFrameLayout2 = this.sizeNotifierFrameLayout;
                if (view == sizeNotifierFrameLayout2) {
                    break;
                }
                f += view.getY();
                view = (View) view.getParent();
            }
            sizeNotifierFrameLayout2.drawBlurRect(canvas, f, AndroidUtilities.rectTmp2, this.backgroundPaint, this.isTopView);
        }
        super.dispatchDraw(canvas);
    }

    public void setBackgroundColor(int i) {
        if (!SharedConfig.chatBlurEnabled() || this.sizeNotifierFrameLayout == null) {
            super.setBackgroundColor(i);
        } else {
            this.backgroundColor = i;
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
