package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class EmptyTextProgressView extends FrameLayout {
    private boolean inLayout;
    private RadialProgressView progressBar;
    private int showAtPos;
    private TextView textView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public EmptyTextProgressView(Context context) {
        super(context);
        this.progressBar = new RadialProgressView(context);
        this.progressBar.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f));
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 20.0f);
        this.textView.setTextColor(Theme.getColor("emptyListPlaceholder"));
        this.textView.setGravity(17);
        this.textView.setVisibility(4);
        this.textView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.textView.setText(LocaleController.getString("NoResult", NUM));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f));
        setOnTouchListener(-$$Lambda$EmptyTextProgressView$AeVTSCBshpCl6wf4siSABV33AKw.INSTANCE);
    }

    public void showProgress() {
        this.textView.setVisibility(4);
        this.progressBar.setVisibility(0);
    }

    public void showTextView() {
        this.textView.setVisibility(0);
        this.progressBar.setVisibility(4);
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setProgressBarColor(int i) {
        this.progressBar.setProgressColor(i);
    }

    public void setTopImage(int i) {
        if (i == 0) {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        Drawable mutate = getContext().getResources().getDrawable(i).mutate();
        if (mutate != null) {
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("emptyListPlaceholder"), Mode.MULTIPLY));
        }
        this.textView.setCompoundDrawablesWithIntrinsicBounds(null, mutate, null, null);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(1.0f));
    }

    public void setTextSize(int i) {
        this.textView.setTextSize(1, (float) i);
    }

    public void setShowAtCenter(boolean z) {
        this.showAtPos = z;
    }

    public void setShowAtTop(boolean z) {
        this.showAtPos = z ? 2 : 0;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        i3 -= i;
        i4 -= i2;
        i = getChildCount();
        for (int i5 = 0; i5 < i; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                int paddingTop;
                int measuredWidth = (i3 - childAt.getMeasuredWidth()) / 2;
                int i6 = this.showAtPos;
                if (i6 == 2) {
                    i6 = (AndroidUtilities.dp(100.0f) - childAt.getMeasuredHeight()) / 2;
                    paddingTop = getPaddingTop();
                } else if (i6 == 1) {
                    i6 = ((i4 / 2) - childAt.getMeasuredHeight()) / 2;
                    paddingTop = getPaddingTop();
                } else {
                    i6 = (i4 - childAt.getMeasuredHeight()) / 2;
                    paddingTop = getPaddingTop();
                }
                i6 += paddingTop;
                childAt.layout(measuredWidth, i6, childAt.getMeasuredWidth() + measuredWidth, childAt.getMeasuredHeight() + i6);
            }
        }
        this.inLayout = false;
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }
}
