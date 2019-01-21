package org.telegram.ui;

import android.animation.AnimatorSet;

final /* synthetic */ class ProfileActivity$$Lambda$15 implements Runnable {
    private final AnimatorSet arg$1;

    private ProfileActivity$$Lambda$15(AnimatorSet animatorSet) {
        this.arg$1 = animatorSet;
    }

    static Runnable get$Lambda(AnimatorSet animatorSet) {
        return new ProfileActivity$$Lambda$15(animatorSet);
    }

    public void run() {
        this.arg$1.start();
    }
}
