package org.telegram.ui.Components;

import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import org.telegram.messenger.AndroidUtilities;

public final class VerticalPositionAutoAnimator {
    private final AnimatorLayoutChangeListener animatorLayoutChangeListener;
    /* access modifiers changed from: private */
    public SpringAnimation floatingButtonAnimator;
    private View floatingButtonView;
    /* access modifiers changed from: private */
    public float offsetY;

    public static VerticalPositionAutoAnimator attach(View floatingButtonView2) {
        return attach(floatingButtonView2, 350.0f);
    }

    public static VerticalPositionAutoAnimator attach(View floatingButtonView2, float springStiffness) {
        return new VerticalPositionAutoAnimator(floatingButtonView2, springStiffness);
    }

    public void addUpdateListener(DynamicAnimation.OnAnimationUpdateListener onAnimationUpdateListener) {
        this.floatingButtonAnimator.addUpdateListener(onAnimationUpdateListener);
    }

    public void setOffsetY(float offsetY2) {
        this.offsetY = offsetY2;
        if (this.floatingButtonAnimator.isRunning()) {
            this.floatingButtonAnimator.getSpring().setFinalPosition(offsetY2);
        } else {
            this.floatingButtonView.setTranslationY(offsetY2);
        }
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    private VerticalPositionAutoAnimator(View floatingButtonView2, float springStiffness) {
        this.floatingButtonView = floatingButtonView2;
        AnimatorLayoutChangeListener animatorLayoutChangeListener2 = new AnimatorLayoutChangeListener(floatingButtonView2, springStiffness);
        this.animatorLayoutChangeListener = animatorLayoutChangeListener2;
        floatingButtonView2.addOnLayoutChangeListener(animatorLayoutChangeListener2);
    }

    public void ignoreNextLayout() {
        boolean unused = this.animatorLayoutChangeListener.ignoreNextLayout = true;
    }

    private class AnimatorLayoutChangeListener implements View.OnLayoutChangeListener {
        /* access modifiers changed from: private */
        public boolean ignoreNextLayout;
        private Boolean orientation;

        public AnimatorLayoutChangeListener(View view, float springStiffness) {
            SpringAnimation unused = VerticalPositionAutoAnimator.this.floatingButtonAnimator = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, VerticalPositionAutoAnimator.this.offsetY);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setDampingRatio(1.0f);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setStiffness(springStiffness);
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            checkOrientation();
            if (oldTop == 0 || oldTop == top || this.ignoreNextLayout) {
                this.ignoreNextLayout = false;
                return;
            }
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.cancel();
            if (v.getVisibility() != 0) {
                v.setTranslationY(VerticalPositionAutoAnimator.this.offsetY);
                return;
            }
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setFinalPosition(VerticalPositionAutoAnimator.this.offsetY);
            v.setTranslationY(((float) (oldTop - top)) + VerticalPositionAutoAnimator.this.offsetY);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.start();
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
