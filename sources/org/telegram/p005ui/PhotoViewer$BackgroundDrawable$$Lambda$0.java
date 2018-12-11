package org.telegram.p005ui;

import org.telegram.p005ui.PhotoViewer.BackgroundDrawable;

/* renamed from: org.telegram.ui.PhotoViewer$BackgroundDrawable$$Lambda$0 */
final /* synthetic */ class PhotoViewer$BackgroundDrawable$$Lambda$0 implements Runnable {
    private final BackgroundDrawable arg$1;

    PhotoViewer$BackgroundDrawable$$Lambda$0(BackgroundDrawable backgroundDrawable) {
        this.arg$1 = backgroundDrawable;
    }

    public void run() {
        this.arg$1.lambda$setAlpha$0$PhotoViewer$BackgroundDrawable();
    }
}
