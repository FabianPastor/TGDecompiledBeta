package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class HideViewAfterAnimation extends AnimatorListenerAdapter {
    private final View view;

    public HideViewAfterAnimation(View view2) {
        this.view = view2;
    }

    public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        this.view.setVisibility(8);
    }
}
