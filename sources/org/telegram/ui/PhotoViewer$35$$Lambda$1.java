package org.telegram.ui;

import android.animation.AnimatorSet;
import org.telegram.ui.PhotoViewer.AnonymousClass35;

final /* synthetic */ class PhotoViewer$35$$Lambda$1 implements Runnable {
    private final AnonymousClass35 arg$1;
    private final AnimatorSet arg$2;

    PhotoViewer$35$$Lambda$1(AnonymousClass35 anonymousClass35, AnimatorSet animatorSet) {
        this.arg$1 = anonymousClass35;
        this.arg$2 = animatorSet;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$1$PhotoViewer$35(this.arg$2);
    }
}
