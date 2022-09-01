package org.telegram.ui;

import android.animation.AnimatorSet;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$69$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass69 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AnimatorSet f$2;

    public /* synthetic */ PhotoViewer$69$$ExternalSyntheticLambda1(PhotoViewer.AnonymousClass69 r1, int i, AnimatorSet animatorSet) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = animatorSet;
    }

    public final void run() {
        this.f$0.lambda$onPreDraw$2(this.f$1, this.f$2);
    }
}
