package org.telegram.p005ui;

import android.animation.AnimatorSet;
import org.telegram.p005ui.PhotoViewer.CLASSNAME;

/* renamed from: org.telegram.ui.PhotoViewer$35$$Lambda$1 */
final /* synthetic */ class PhotoViewer$35$$Lambda$1 implements Runnable {
    private final CLASSNAME arg$1;
    private final AnimatorSet arg$2;

    PhotoViewer$35$$Lambda$1(CLASSNAME CLASSNAME, AnimatorSet animatorSet) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = animatorSet;
    }

    public void run() {
        this.arg$1.lambda$onPreDraw$1$PhotoViewer$35(this.arg$2);
    }
}
