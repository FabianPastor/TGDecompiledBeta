package org.telegram.p005ui;

import android.animation.AnimatorSet;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$13 */
final /* synthetic */ class ProfileActivity$$Lambda$13 implements Runnable {
    private final AnimatorSet arg$1;

    private ProfileActivity$$Lambda$13(AnimatorSet animatorSet) {
        this.arg$1 = animatorSet;
    }

    static Runnable get$Lambda(AnimatorSet animatorSet) {
        return new ProfileActivity$$Lambda$13(animatorSet);
    }

    public void run() {
        this.arg$1.start();
    }
}
