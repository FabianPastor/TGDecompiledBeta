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
    Runnable dismissRunnable = new Tooltip$$ExternalSyntheticLambda0(this);
    private boolean showing;

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Tooltip  reason: not valid java name */
    public /* synthetic */ void m2695lambda$new$0$orgtelegramuiComponentsTooltip() {
        ViewPropertyAnimator duration = animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Tooltip.this.setVisibility(8);
            }
        }).setDuration(300);
        this.animator = duration;
        duration.start();
    }

    public Tooltip(Context context, ViewGroup parentView, int backgroundColor, int textColor) {
        super(context);
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), backgroundColor));
        setTextColor(textColor);
        setTextSize(1, 14.0f);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        setGravity(16);
        parentView.addView(this, LayoutHelper.createFrame(-2, -2.0f, 51, 5.0f, 0.0f, 5.0f, 3.0f));
        setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateTooltipPosition();
    }

    /* JADX WARNING: type inference failed for: r4v10, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTooltipPosition() {
        /*
            r7 = this;
            android.view.View r0 = r7.anchor
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r0 = 0
            r1 = 0
            android.view.ViewParent r2 = r7.getParent()
            android.view.View r2 = (android.view.View) r2
            android.view.View r3 = r7.anchor
        L_0x000f:
            if (r3 == r2) goto L_0x0023
            int r4 = r3.getTop()
            int r0 = r0 + r4
            int r4 = r3.getLeft()
            int r1 = r1 + r4
            android.view.ViewParent r4 = r3.getParent()
            r3 = r4
            android.view.View r3 = (android.view.View) r3
            goto L_0x000f
        L_0x0023:
            android.view.View r4 = r7.anchor
            int r4 = r4.getWidth()
            int r4 = r4 / 2
            int r4 = r4 + r1
            int r5 = r7.getMeasuredWidth()
            int r5 = r5 / 2
            int r4 = r4 - r5
            if (r4 >= 0) goto L_0x0037
            r4 = 0
            goto L_0x0053
        L_0x0037:
            int r5 = r7.getMeasuredWidth()
            int r5 = r5 + r4
            int r6 = r2.getMeasuredWidth()
            if (r5 <= r6) goto L_0x0053
            int r5 = r2.getMeasuredWidth()
            int r6 = r7.getMeasuredWidth()
            int r5 = r5 - r6
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r5 - r6
        L_0x0053:
            float r5 = (float) r4
            r7.setTranslationX(r5)
            int r5 = r7.getMeasuredHeight()
            int r5 = r0 - r5
            float r6 = (float) r5
            r7.setTranslationY(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Tooltip.updateTooltipPosition():void");
    }

    public void show(View anchor2) {
        if (anchor2 != null) {
            this.anchor = anchor2;
            updateTooltipPosition();
            this.showing = true;
            AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
            AndroidUtilities.runOnUIThread(this.dismissRunnable, 2000);
            ViewPropertyAnimator viewPropertyAnimator = this.animator;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.setListener((Animator.AnimatorListener) null);
                this.animator.cancel();
                this.animator = null;
            }
            if (getVisibility() != 0) {
                setAlpha(0.0f);
                setVisibility(0);
                ViewPropertyAnimator listener = animate().setDuration(300).alpha(1.0f).setListener((Animator.AnimatorListener) null);
                this.animator = listener;
                listener.start();
            }
        }
    }

    public void hide() {
        if (this.showing) {
            ViewPropertyAnimator viewPropertyAnimator = this.animator;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.setListener((Animator.AnimatorListener) null);
                this.animator.cancel();
                this.animator = null;
            }
            AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
            this.dismissRunnable.run();
        }
        this.showing = false;
    }
}
