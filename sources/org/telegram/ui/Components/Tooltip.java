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
/* loaded from: classes3.dex */
public class Tooltip extends TextView {
    private View anchor;
    private ViewPropertyAnimator animator;
    Runnable dismissRunnable;
    private boolean showing;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        ViewPropertyAnimator duration = animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Tooltip.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                Tooltip.this.setVisibility(8);
            }
        }).setDuration(300L);
        this.animator = duration;
        duration.start();
    }

    public Tooltip(Context context, ViewGroup viewGroup, int i, int i2) {
        super(context);
        this.dismissRunnable = new Runnable() { // from class: org.telegram.ui.Components.Tooltip$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Tooltip.this.lambda$new$0();
            }
        };
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), i));
        setTextColor(i2);
        setTextSize(1, 14.0f);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        setGravity(16);
        viewGroup.addView(this, LayoutHelper.createFrame(-2, -2.0f, 51, 5.0f, 0.0f, 5.0f, 3.0f));
        setVisibility(8);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateTooltipPosition();
    }

    private void updateTooltipPosition() {
        if (this.anchor == null) {
            return;
        }
        View view = (View) getParent();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (View view2 = this.anchor; view2 != view; view2 = (View) view2.getParent()) {
            i3 += view2.getTop();
            i2 += view2.getLeft();
        }
        int width = (i2 + (this.anchor.getWidth() / 2)) - (getMeasuredWidth() / 2);
        if (width >= 0) {
            i = getMeasuredWidth() + width > view.getMeasuredWidth() ? (view.getMeasuredWidth() - getMeasuredWidth()) - AndroidUtilities.dp(16.0f) : width;
        }
        setTranslationX(i);
        setTranslationY(i3 - getMeasuredHeight());
    }

    public void show(View view) {
        if (view == null) {
            return;
        }
        this.anchor = view;
        updateTooltipPosition();
        this.showing = true;
        AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
        AndroidUtilities.runOnUIThread(this.dismissRunnable, 2000L);
        ViewPropertyAnimator viewPropertyAnimator = this.animator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.setListener(null);
            this.animator.cancel();
            this.animator = null;
        }
        if (getVisibility() == 0) {
            return;
        }
        setAlpha(0.0f);
        setVisibility(0);
        ViewPropertyAnimator listener = animate().setDuration(300L).alpha(1.0f).setListener(null);
        this.animator = listener;
        listener.start();
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
