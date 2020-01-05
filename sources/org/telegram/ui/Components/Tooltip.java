package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class Tooltip extends TextView {
    private View anchor;
    private ViewPropertyAnimator animator;
    Runnable dismissRunnable = new -$$Lambda$Tooltip$vOoSDdUyB3V3sLP7AXAFWIK3ppI(this);
    private boolean showing;

    public /* synthetic */ void lambda$new$0$Tooltip() {
        this.animator = animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Tooltip.this.setVisibility(8);
            }
        }).setDuration(300);
        this.animator.start();
    }

    public Tooltip(Context context, ViewGroup viewGroup, int i, int i2) {
        super(context);
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), i));
        setTextColor(i2);
        setTextSize(1, 14.0f);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        setGravity(16);
        viewGroup.addView(this, LayoutHelper.createFrame(-2, -2.0f, 51, 5.0f, 0.0f, 5.0f, 3.0f));
        setVisibility(8);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateTooltipPosition();
    }

    private void updateTooltipPosition() {
        if (this.anchor != null) {
            View view = (View) getParent();
            int i = 0;
            int i2 = 0;
            for (View view2 = this.anchor; view2 != view; view2 = (View) view2.getParent()) {
                i2 += view2.getTop();
                i += view2.getLeft();
            }
            int width = (i + (this.anchor.getWidth() / 2)) - (getMeasuredWidth() / 2);
            if (width < 0) {
                width = 0;
            }
            setTranslationX((float) width);
            setTranslationY((float) (i2 - getMeasuredHeight()));
        }
    }

    public void show(View view) {
        if (view != null) {
            this.anchor = view;
            updateTooltipPosition();
            this.showing = true;
            AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
            AndroidUtilities.runOnUIThread(this.dismissRunnable, 2000);
            ViewPropertyAnimator viewPropertyAnimator = this.animator;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.setListener(null);
                this.animator.cancel();
                this.animator = null;
            }
            if (getVisibility() != 0) {
                setAlpha(0.0f);
                setVisibility(0);
                this.animator = animate().setDuration(300).alpha(1.0f).setListener(null);
                this.animator.start();
            }
        }
    }

    public void hide() {
        if (this.showing) {
            ViewPropertyAnimator viewPropertyAnimator = this.animator;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.setListener(null);
                this.animator.cancel();
                this.animator = null;
            }
            AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
            this.dismissRunnable.run();
        }
        this.showing = false;
    }
}
