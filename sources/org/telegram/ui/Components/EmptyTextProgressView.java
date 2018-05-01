package org.telegram.ui.Components;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class EmptyTextProgressView extends FrameLayout {
    private boolean inLayout;
    private RadialProgressView progressBar;
    private boolean showAtCenter;
    private TextView textView;

    /* renamed from: org.telegram.ui.Components.EmptyTextProgressView$1 */
    class C11621 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C11621() {
        }
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
        this.textView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.textView.setGravity(17);
        this.textView.setVisibility(4);
        this.textView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.textView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f));
        setOnTouchListener(new C11621());
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

    public void setTextSize(int i) {
        this.textView.setTextSize(1, (float) i);
    }

    public void setShowAtCenter(boolean z) {
        this.showAtCenter = z;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        i3 -= i;
        i4 -= i2;
        z = getChildCount();
        for (boolean z2 = false; z2 < z; z2++) {
            View childAt = getChildAt(z2);
            if (childAt.getVisibility() != 8) {
                int measuredHeight;
                int measuredWidth = (i3 - childAt.getMeasuredWidth()) / 2;
                if (this.showAtCenter) {
                    measuredHeight = ((i4 / 2) - childAt.getMeasuredHeight()) / 2;
                } else {
                    measuredHeight = (i4 - childAt.getMeasuredHeight()) / 2;
                }
                childAt.layout(measuredWidth, measuredHeight, childAt.getMeasuredWidth() + measuredWidth, childAt.getMeasuredHeight() + measuredHeight);
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
