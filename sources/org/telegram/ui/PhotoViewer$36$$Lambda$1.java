package org.telegram.ui;

import android.animation.AnimatorSet;
import org.telegram.ui.PhotoViewer.AnonymousClass36;

final /* synthetic */ class PhotoViewer$36$$Lambda$1 implements Runnable {
    private final AnonymousClass36 arg$1;
    private final AnimatorSet arg$2;

    PhotoViewer$36$$Lambda$1(AnonymousClass36 anonymousClass36, AnimatorSet animatorSet) {
        this.arg$1 = anonymousClass36;
        this.arg$2 = animatorSet;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$1$PhotoViewer$36(this.arg$2);
    }
}
