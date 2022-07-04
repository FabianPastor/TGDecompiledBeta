package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public class ChatScrimPopupContainerLayout extends LinearLayout {
    private View bottomView;
    private int maxHeight;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    private ReactionsContainerLayout reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = this.maxHeight;
        if (i != 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        }
        if (this.reactionsLayout == null || this.popupWindowLayout == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int reactionsLayoutTotalWidth = this.reactionsLayout.getTotalWidth();
        int maxReactionsLayoutWidth = (this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack().getChildAt(0) : this.popupWindowLayout.getChildAt(0)).getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
        if (maxReactionsLayoutWidth > getMeasuredWidth()) {
            maxReactionsLayoutWidth = getMeasuredWidth();
        }
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
        if (this.reactionsLayout.getLayoutParams().width != -2 && this.reactionsLayout.getLayoutParams().width + widthDiff > getMeasuredWidth() && this.popupWindowLayout.getSwipeBack() != null && this.popupWindowLayout.getSwipeBack().getMeasuredWidth() > getMeasuredWidth()) {
            widthDiff = (getMeasuredWidth() - this.reactionsLayout.getLayoutParams().width) + AndroidUtilities.dp(8.0f);
        }
        if (widthDiff < 0) {
            widthDiff = 0;
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

    public void setPopupWindowLayout(ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout2) {
        this.popupWindowLayout = popupWindowLayout2;
        popupWindowLayout2.setOnSizeChangedListener(new ChatScrimPopupContainerLayout$$ExternalSyntheticLambda0(this, popupWindowLayout2));
        if (popupWindowLayout2.getSwipeBack() != null) {
            popupWindowLayout2.getSwipeBack().addOnSwipeBackProgressListener(new ChatScrimPopupContainerLayout$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$setPopupWindowLayout$0$org-telegram-ui-Components-ChatScrimPopupContainerLayout  reason: not valid java name */
    public /* synthetic */ void m873xaa93e743(ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout2) {
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationY((float) (popupWindowLayout2.getVisibleHeight() - popupWindowLayout2.getMeasuredHeight()));
        }
    }

    /* renamed from: lambda$setPopupWindowLayout$1$org-telegram-ui-Components-ChatScrimPopupContainerLayout  reason: not valid java name */
    public /* synthetic */ void m874x3780fe62(PopupSwipeBackLayout layout, float toProgress, float progress) {
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(1.0f - progress);
        }
    }

    public void setMaxHeight(int maxHeight2) {
        this.maxHeight = maxHeight2;
    }
}
