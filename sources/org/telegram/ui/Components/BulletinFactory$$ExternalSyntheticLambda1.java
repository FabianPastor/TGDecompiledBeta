package org.telegram.ui.Components;

import org.telegram.ui.Components.Bulletin;

public final /* synthetic */ class BulletinFactory$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ Bulletin.LottieLayout f$0;

    public /* synthetic */ BulletinFactory$$ExternalSyntheticLambda1(Bulletin.LottieLayout lottieLayout) {
        this.f$0 = lottieLayout;
    }

    public final void run() {
        this.f$0.performHapticFeedback(3, 2);
    }
}
