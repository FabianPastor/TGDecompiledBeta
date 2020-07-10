package org.telegram.ui.Components;

import android.graphics.Point;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import org.telegram.messenger.AndroidUtilities;

public final class VerticalPositionAutoAnimator {
    private final AnimatorLayoutChangeListener animatorLayoutChangeListener;

    public static VerticalPositionAutoAnimator attach(View view) {
        return attach(view, 350.0f);
    }

    public static VerticalPositionAutoAnimator attach(View view, float f) {
        AnimatorLayoutChangeListener animatorLayoutChangeListener2 = new AnimatorLayoutChangeListener(view, f);
        view.addOnLayoutChangeListener(animatorLayoutChangeListener2);
        return new VerticalPositionAutoAnimator(animatorLayoutChangeListener2);
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

        public AnimatorLayoutChangeListener(View view, float f) {
            SpringAnimation springAnimation = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0.0f);
            this.floatingButtonAnimator = springAnimation;
            springAnimation.getSpring().setDampingRatio(1.0f);
            this.floatingButtonAnimator.getSpring().setStiffness(f);
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            checkOrientation();
            if (i6 == 0 || i6 == i2 || this.ignoreNextLayout) {
                this.ignoreNextLayout = false;
                return;
            }
            this.floatingButtonAnimator.cancel();
            view.setTranslationY((float) (i6 - i2));
            this.floatingButtonAnimator.start();
        }

        private void checkOrientation() {
            Point point = AndroidUtilities.displaySize;
            boolean z = point.x > point.y;
            Boolean bool = this.orientation;
            if (bool == null || bool.booleanValue() != z) {
                this.orientation = Boolean.valueOf(z);
                this.ignoreNextLayout = true;
            }
        }
    }
}
