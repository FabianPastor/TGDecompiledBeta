package org.telegram.ui.Components;

import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import org.telegram.messenger.AndroidUtilities;

public final class VerticalPositionAutoAnimator {
    private final AnimatorLayoutChangeListener animatorLayoutChangeListener;

    public static VerticalPositionAutoAnimator attach(View floatingButtonView) {
        return attach(floatingButtonView, 350.0f);
    }

    public static VerticalPositionAutoAnimator attach(View floatingButtonView, float springStiffness) {
        AnimatorLayoutChangeListener listener = new AnimatorLayoutChangeListener(floatingButtonView, springStiffness);
        floatingButtonView.addOnLayoutChangeListener(listener);
        return new VerticalPositionAutoAnimator(listener);
    }

    private VerticalPositionAutoAnimator(AnimatorLayoutChangeListener animatorLayoutChangeListener2) {
        this.animatorLayoutChangeListener = animatorLayoutChangeListener2;
    }

    public void ignoreNextLayout() {
        boolean unused = this.animatorLayoutChangeListener.ignoreNextLayout = true;
    }

    private static class AnimatorLayoutChangeListener implements View.OnLayoutChangeListener {
        private final SpringAnimation floatingButtonAnimator;
        /* access modifiers changed from: private */
        public boolean ignoreNextLayout;
        private Boolean orientation;

        public AnimatorLayoutChangeListener(View view, float springStiffness) {
            SpringAnimation springAnimation = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0.0f);
            this.floatingButtonAnimator = springAnimation;
            springAnimation.getSpring().setDampingRatio(1.0f);
            springAnimation.getSpring().setStiffness(springStiffness);
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            checkOrientation();
            if (oldTop == 0 || oldTop == top || this.ignoreNextLayout) {
                this.ignoreNextLayout = false;
                return;
            }
            this.floatingButtonAnimator.cancel();
            v.setTranslationY((float) (oldTop - top));
            this.floatingButtonAnimator.start();
        }

        private void checkOrientation() {
            boolean orientation2 = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
            Boolean bool = this.orientation;
            if (bool == null || bool.booleanValue() != orientation2) {
                this.orientation = Boolean.valueOf(orientation2);
                this.ignoreNextLayout = true;
            }
        }
    }
}
