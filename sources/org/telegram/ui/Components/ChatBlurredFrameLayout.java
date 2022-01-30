package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ChatActivity;

public class ChatBlurredFrameLayout extends FrameLayout {
    public int backgroundColor;
    public int backgroundPaddingBottom;
    public int backgroundPaddingTop;
    protected Paint backgroundPaint;
    ChatActivity chatActivity;
    public boolean drawBlur = true;
    public boolean isTopView = true;

    public ChatBlurredFrameLayout(Context context, ChatActivity chatActivity2) {
        super(context);
        this.chatActivity = chatActivity2;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        SizeNotifierFrameLayout sizeNotifierFrameLayout;
        if (SharedConfig.chatBlurEnabled() && this.chatActivity != null && this.drawBlur) {
            if (this.backgroundPaint == null) {
                this.backgroundPaint = new Paint();
            }
            this.backgroundPaint.setColor(this.backgroundColor);
            AndroidUtilities.rectTmp2.set(0, this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight() - this.backgroundPaddingBottom);
            float f = 0.0f;
            View view = this;
            while (true) {
                sizeNotifierFrameLayout = this.chatActivity.contentView;
                if (view == sizeNotifierFrameLayout) {
                    break;
                }
                f += view.getY();
                view = (View) view.getParent();
            }
            sizeNotifierFrameLayout.drawBlur(canvas, f, AndroidUtilities.rectTmp2, this.backgroundPaint, this.isTopView);
        }
        super.dispatchDraw(canvas);
    }

    public void setBackgroundColor(int i) {
        if (!SharedConfig.chatBlurEnabled() || this.chatActivity == null) {
            super.setBackgroundColor(i);
        } else {
            this.backgroundColor = i;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        ChatActivity chatActivity2;
        if (SharedConfig.chatBlurEnabled() && (chatActivity2 = this.chatActivity) != null) {
            chatActivity2.contentView.blurBehindViews.add(this);
        }
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 != null) {
            chatActivity2.contentView.blurBehindViews.remove(this);
        }
        super.onDetachedFromWindow();
    }
}
