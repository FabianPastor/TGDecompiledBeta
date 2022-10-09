package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.PopupSwipeBackLayout;
/* loaded from: classes3.dex */
public class ChatScrimPopupContainerLayout extends LinearLayout {
    private View bottomView;
    private float bottomViewYOffset;
    private float expandSize;
    private int maxHeight;
    private float popupLayoutLeftOffset;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    private float progressToSwipeBack;
    private ReactionsContainerLayout reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3 = this.maxHeight;
        if (i3 != 0) {
            i2 = View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE);
        }
        ReactionsContainerLayout reactionsContainerLayout = this.reactionsLayout;
        if (reactionsContainerLayout != null && this.popupWindowLayout != null) {
            reactionsContainerLayout.getLayoutParams().width = -2;
            int i4 = 0;
            ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = 0;
            this.popupLayoutLeftOffset = 0.0f;
            super.onMeasure(i, i2);
            int measuredWidth = this.reactionsLayout.getMeasuredWidth();
            if (this.popupWindowLayout.getSwipeBack() != null && this.popupWindowLayout.getSwipeBack().getMeasuredWidth() > measuredWidth) {
                measuredWidth = this.popupWindowLayout.getSwipeBack().getMeasuredWidth();
            }
            if (this.popupWindowLayout.getMeasuredWidth() > measuredWidth) {
                measuredWidth = this.popupWindowLayout.getMeasuredWidth();
            }
            if (this.reactionsLayout.showCustomEmojiReaction()) {
                i = View.MeasureSpec.makeMeasureSpec(measuredWidth, NUM);
            }
            int totalWidth = this.reactionsLayout.getTotalWidth();
            View childAt = (this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack() : this.popupWindowLayout).getChildAt(0);
            int measuredWidth2 = childAt.getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
            if (measuredWidth2 > measuredWidth) {
                measuredWidth2 = measuredWidth;
            }
            this.reactionsLayout.bigCircleOffset = AndroidUtilities.dp(36.0f);
            if (this.reactionsLayout.showCustomEmojiReaction()) {
                this.reactionsLayout.getLayoutParams().width = totalWidth;
                this.reactionsLayout.bigCircleOffset = Math.max((totalWidth - childAt.getMeasuredWidth()) - AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            } else if (totalWidth > measuredWidth2) {
                int dp = ((measuredWidth2 - AndroidUtilities.dp(16.0f)) / AndroidUtilities.dp(36.0f)) + 1;
                int dp2 = ((AndroidUtilities.dp(36.0f) * dp) + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(8.0f);
                if (dp2 <= totalWidth && dp != this.reactionsLayout.getItemsCount()) {
                    totalWidth = dp2;
                }
                this.reactionsLayout.getLayoutParams().width = totalWidth;
            } else {
                this.reactionsLayout.getLayoutParams().width = -2;
            }
            if (this.reactionsLayout.getMeasuredWidth() != measuredWidth || !this.reactionsLayout.showCustomEmojiReaction()) {
                int measuredWidth3 = this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth() : 0;
                if (this.reactionsLayout.getLayoutParams().width != -2 && this.reactionsLayout.getLayoutParams().width + measuredWidth3 > measuredWidth) {
                    measuredWidth3 = (measuredWidth - this.reactionsLayout.getLayoutParams().width) + AndroidUtilities.dp(8.0f);
                }
                if (measuredWidth3 >= 0) {
                    i4 = measuredWidth3;
                }
                ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = i4;
                this.popupLayoutLeftOffset = 0.0f;
                updatePopupTranslation();
            } else {
                float measuredWidth4 = (measuredWidth - childAt.getMeasuredWidth()) * 0.25f;
                this.popupLayoutLeftOffset = measuredWidth4;
                ReactionsContainerLayout reactionsContainerLayout2 = this.reactionsLayout;
                int i5 = (int) (reactionsContainerLayout2.bigCircleOffset - measuredWidth4);
                reactionsContainerLayout2.bigCircleOffset = i5;
                if (i5 < AndroidUtilities.dp(36.0f)) {
                    this.popupLayoutLeftOffset = 0.0f;
                    this.reactionsLayout.bigCircleOffset = AndroidUtilities.dp(36.0f);
                }
                updatePopupTranslation();
            }
            if (this.bottomView != null) {
                if (this.reactionsLayout.showCustomEmojiReaction()) {
                    this.bottomView.getLayoutParams().width = childAt.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                    updatePopupTranslation();
                } else {
                    this.bottomView.getLayoutParams().width = -1;
                }
                if (this.popupWindowLayout.getSwipeBack() != null) {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = i4 + AndroidUtilities.dp(36.0f);
                } else {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f);
                }
            }
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(i, i2);
    }

    private void updatePopupTranslation() {
        float f = (1.0f - this.progressToSwipeBack) * this.popupLayoutLeftOffset;
        this.popupWindowLayout.setTranslationX(f);
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationX(f);
        }
    }

    public void applyViewBottom(FrameLayout frameLayout) {
        this.bottomView = frameLayout;
    }

    public void setReactionsLayout(ReactionsContainerLayout reactionsContainerLayout) {
        this.reactionsLayout = reactionsContainerLayout;
        if (reactionsContainerLayout != null) {
            reactionsContainerLayout.setChatScrimView(this);
        }
    }

    public void setPopupWindowLayout(final ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        this.popupWindowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setOnSizeChangedListener(new ActionBarPopupWindow.onSizeChangedListener() { // from class: org.telegram.ui.Components.ChatScrimPopupContainerLayout$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.onSizeChangedListener
            public final void onSizeChanged() {
                ChatScrimPopupContainerLayout.this.lambda$setPopupWindowLayout$0(actionBarPopupWindowLayout);
            }
        });
        if (actionBarPopupWindowLayout.getSwipeBack() != null) {
            actionBarPopupWindowLayout.getSwipeBack().addOnSwipeBackProgressListener(new PopupSwipeBackLayout.OnSwipeBackProgressListener() { // from class: org.telegram.ui.Components.ChatScrimPopupContainerLayout$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.PopupSwipeBackLayout.OnSwipeBackProgressListener
                public final void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
                    ChatScrimPopupContainerLayout.this.lambda$setPopupWindowLayout$1(popupSwipeBackLayout, f, f2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setPopupWindowLayout$0(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        if (this.bottomView != null) {
            this.bottomViewYOffset = actionBarPopupWindowLayout.getVisibleHeight() - actionBarPopupWindowLayout.getMeasuredHeight();
            updateBottomViewPosition();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setPopupWindowLayout$1(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(1.0f - f2);
        }
        this.progressToSwipeBack = f2;
        updatePopupTranslation();
    }

    private void updateBottomViewPosition() {
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationY(this.bottomViewYOffset + this.expandSize);
        }
    }

    public void setMaxHeight(int i) {
        this.maxHeight = i;
    }

    public void setExpandSize(float f) {
        this.popupWindowLayout.setTranslationY(f);
        this.expandSize = f;
        updateBottomViewPosition();
    }

    public void setPopupAlpha(float f) {
        this.popupWindowLayout.setAlpha(f);
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(f);
        }
    }
}
