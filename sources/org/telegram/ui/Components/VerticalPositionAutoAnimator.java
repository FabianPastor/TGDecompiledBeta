package org.telegram.ui.Components;

import android.graphics.Point;
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

    public static VerticalPositionAutoAnimator attach(View view) {
        return attach(view, 350.0f);
    }

    public static VerticalPositionAutoAnimator attach(View view, float f) {
        return new VerticalPositionAutoAnimator(view, f);
    }

    public void addUpdateListener(DynamicAnimation.OnAnimationUpdateListener onAnimationUpdateListener) {
        this.floatingButtonAnimator.addUpdateListener(onAnimationUpdateListener);
    }

    public void setOffsetY(float f) {
        this.offsetY = f;
        if (this.floatingButtonAnimator.isRunning()) {
            this.floatingButtonAnimator.getSpring().setFinalPosition(f);
        } else {
            this.floatingButtonView.setTranslationY(f);
        }
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    private VerticalPositionAutoAnimator(View view, float f) {
        this.floatingButtonView = view;
        AnimatorLayoutChangeListener animatorLayoutChangeListener2 = new AnimatorLayoutChangeListener(view, f);
        this.animatorLayoutChangeListener = animatorLayoutChangeListener2;
        view.addOnLayoutChangeListener(animatorLayoutChangeListener2);
    }

    public void ignoreNextLayout() {
        boolean unused = this.animatorLayoutChangeListener.ignoreNextLayout = true;
    }

    private class AnimatorLayoutChangeListener implements View.OnLayoutChangeListener {
        /* access modifiers changed from: private */
        public boolean ignoreNextLayout;
        private Boolean orientation;

        public AnimatorLayoutChangeListener(View view, float f) {
            SpringAnimation unused = VerticalPositionAutoAnimator.this.floatingButtonAnimator = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, VerticalPositionAutoAnimator.this.offsetY);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setDampingRatio(1.0f);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setStiffness(f);
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            checkOrientation();
            if (i6 == 0 || i6 == i2 || this.ignoreNextLayout) {
                this.ignoreNextLayout = false;
                return;
            }
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.cancel();
            if (view.getVisibility() != 0) {
                view.setTranslationY(VerticalPositionAutoAnimator.this.offsetY);
                return;
            }
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setFinalPosition(VerticalPositionAutoAnimator.this.offsetY);
            view.setTranslationY(((float) (i6 - i2)) + VerticalPositionAutoAnimator.this.offsetY);
            VerticalPositionAutoAnimator.this.floatingButtonAnimator.start();
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
