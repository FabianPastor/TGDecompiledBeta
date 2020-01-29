package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
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

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

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
        setOnTouchListener($$Lambda$EmptyTextProgressView$AeVTSCBshpCl6wf4siSABV33AKw.INSTANCE);
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
            this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        Drawable mutate = getContext().getResources().getDrawable(i).mutate();
        if (mutate != null) {
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("emptyListPlaceholder"), PorterDuff.Mode.MULTIPLY));
        }
        this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, mutate, (Drawable) null, (Drawable) null);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(1.0f));
    }

    public void setTextSize(int i) {
        this.textView.setTextSize(1, (float) i);
    }

    public void setShowAtCenter(boolean z) {
        this.showAtPos = z ? 1 : 0;
    }

    public void setShowAtTop(boolean z) {
        this.showAtPos = z ? 2 : 0;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int measuredHeight;
        int paddingTop;
        this.inLayout = true;
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                int measuredWidth = (i5 - childAt.getMeasuredWidth()) / 2;
                int i8 = this.showAtPos;
                if (i8 == 2) {
                    measuredHeight = (AndroidUtilities.dp(100.0f) - childAt.getMeasuredHeight()) / 2;
                    paddingTop = getPaddingTop();
                } else if (i8 == 1) {
                    measuredHeight = ((i6 / 2) - childAt.getMeasuredHeight()) / 2;
                    paddingTop = getPaddingTop();
                } else {
                    measuredHeight = (i6 - childAt.getMeasuredHeight()) / 2;
                    paddingTop = getPaddingTop();
                }
                int i9 = measuredHeight + paddingTop;
                childAt.layout(measuredWidth, i9, childAt.getMeasuredWidth() + measuredWidth, childAt.getMeasuredHeight() + i9);
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
