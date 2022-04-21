package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.PopupSwipeBackLayout;

public class ChatScrimPopupContainerLayout extends LinearLayout {
    /* access modifiers changed from: private */
    public View bottomView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    private ReactionsContainerLayout reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.reactionsLayout == null || this.popupWindowLayout == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int reactionsLayoutTotalWidth = this.reactionsLayout.getTotalWidth();
        int maxReactionsLayoutWidth = (this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack().getChildAt(0) : this.popupWindowLayout.getChildAt(0)).getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
        if (reactionsLayoutTotalWidth > maxReactionsLayoutWidth) {
            int maxFullCount = ((maxReactionsLayoutWidth - AndroidUtilities.dp(16.0f)) / AndroidUtilities.dp(36.0f)) + 1;
            int newWidth = ((AndroidUtilities.dp(36.0f) * maxFullCount) + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(8.0f);
            if (newWidth > reactionsLayoutTotalWidth || maxFullCount == this.reactionsLayout.getItemsCount()) {
                newWidth = reactionsLayoutTotalWidth;
            }
            this.reactionsLayout.getLayoutParams().width = newWidth;
        } else {
            this.reactionsLayout.getLayoutParams().width = -2;
        }
        int widthDiff = 0;
        if (this.popupWindowLayout.getSwipeBack() != null) {
            widthDiff = this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
        }
        ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = widthDiff;
        if (this.bottomView != null) {
            if (this.popupWindowLayout.getSwipeBack() != null) {
                ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f) + widthDiff;
            } else {
                ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void applyViewBottom(FrameLayout bottomView2) {
        this.bottomView = bottomView2;
    }

    public void setReactionsLayout(ReactionsContainerLayout reactionsLayout2) {
        this.reactionsLayout = reactionsLayout2;
    }

    public void setPopupWindowLayout(final ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout2) {
        this.popupWindowLayout = popupWindowLayout2;
        popupWindowLayout2.setOnSizeChangedListener(new ActionBarPopupWindow.onSizeChangedListener() {
            public void onSizeChanged() {
                if (ChatScrimPopupContainerLayout.this.bottomView != null) {
                    ChatScrimPopupContainerLayout.this.bottomView.setTranslationY((float) (popupWindowLayout2.getVisibleHeight() - popupWindowLayout2.getMeasuredHeight()));
                }
            }
        });
        if (popupWindowLayout2.getSwipeBack() != null) {
            popupWindowLayout2.getSwipeBack().addOnSwipeBackProgressListener(new PopupSwipeBackLayout.OnSwipeBackProgressListener() {
                public void onSwipeBackProgress(PopupSwipeBackLayout layout, float toProgress, float progress) {
                    if (ChatScrimPopupContainerLayout.this.bottomView != null) {
                        ChatScrimPopupContainerLayout.this.bottomView.setAlpha(1.0f - progress);
                    }
                }
            });
        }
    }
}
