package org.telegram.p005ui;

import android.animation.AnimatorSet;
import org.telegram.p005ui.PhotoViewer.C109336;

/* renamed from: org.telegram.ui.PhotoViewer$36$$Lambda$1 */
final /* synthetic */ class PhotoViewer$36$$Lambda$1 implements Runnable {
    private final C109336 arg$1;
    private final AnimatorSet arg$2;

    PhotoViewer$36$$Lambda$1(C109336 c109336, AnimatorSet animatorSet) {
        this.arg$1 = c109336;
        this.arg$2 = animatorSet;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$1$PhotoViewer$36(this.arg$2);
    }
}
