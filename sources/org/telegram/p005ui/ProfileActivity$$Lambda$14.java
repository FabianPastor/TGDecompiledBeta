package org.telegram.p005ui;

import android.animation.AnimatorSet;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$14 */
final /* synthetic */ class ProfileActivity$$Lambda$14 implements Runnable {
    private final AnimatorSet arg$1;

    private ProfileActivity$$Lambda$14(AnimatorSet animatorSet) {
        this.arg$1 = animatorSet;
    }

    static Runnable get$Lambda(AnimatorSet animatorSet) {
        return new ProfileActivity$$Lambda$14(animatorSet);
    }

    public void run() {
        this.arg$1.start();
    }
}
