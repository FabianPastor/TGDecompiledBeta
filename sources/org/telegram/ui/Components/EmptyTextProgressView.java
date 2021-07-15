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
    private View progressView;
    private int showAtPos;
    private TextView textView;

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public EmptyTextProgressView(Context context) {
        this(context, (View) null);
    }

    public EmptyTextProgressView(Context context, View view) {
        super(context);
        if (view == null) {
            view = new RadialProgressView(context);
            addView(view, LayoutHelper.createFrame(-2, -2.0f));
        } else {
            addView(view, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.progressView = view;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 20.0f);
        this.textView.setTextColor(Theme.getColor("emptyListPlaceholder"));
        this.textView.setGravity(17);
        this.textView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.textView.setText(LocaleController.getString("NoResult", NUM));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f));
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, false, 2.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(view, false, 1.0f, false);
        setOnTouchListener($$Lambda$EmptyTextProgressView$8nH8zAnzG_iOQz8u5LEx8EcAeaI.INSTANCE);
    }

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean z) {
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, false, 0.9f, z);
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 1.0f, z);
    }

    public void showTextView() {
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, true, 0.9f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 1.0f, true);
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setProgressBarColor(int i) {
        View view = this.progressView;
        if (view instanceof RadialProgressView) {
            ((RadialProgressView) view).setProgressColor(i);
        }
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
        int i5;
        int i6;
        this.inLayout = true;
        int i7 = i3 - i;
        int i8 = i4 - i2;
        int childCount = getChildCount();
        for (int i9 = 0; i9 < childCount; i9++) {
            View childAt = getChildAt(i9);
            if (childAt.getVisibility() != 8) {
                int measuredWidth = (i7 - childAt.getMeasuredWidth()) / 2;
                View view = this.progressView;
                if (childAt != view || !(view instanceof FlickerLoadingView)) {
                    int i10 = this.showAtPos;
                    if (i10 == 2) {
                        i5 = (AndroidUtilities.dp(100.0f) - childAt.getMeasuredHeight()) / 2;
                        i6 = getPaddingTop();
                    } else if (i10 == 1) {
                        i5 = ((i8 / 2) - childAt.getMeasuredHeight()) / 2;
                        i6 = getPaddingTop();
                    } else {
                        i5 = (i8 - childAt.getMeasuredHeight()) / 2;
                        i6 = getPaddingTop();
                    }
                } else {
                    i5 = (i8 - childAt.getMeasuredHeight()) / 2;
                    i6 = getPaddingTop();
                }
                int i11 = i5 + i6;
                childAt.layout(measuredWidth, i11, childAt.getMeasuredWidth() + measuredWidth, childAt.getMeasuredHeight() + i11);
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
