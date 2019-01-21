package org.telegram.ui;

import android.view.View;

final /* synthetic */ class StickerPreviewViewer$$Lambda$1 implements Runnable {
    private final StickerPreviewViewer arg$1;
    private final View arg$2;
    private final int arg$3;

    StickerPreviewViewer$$Lambda$1(StickerPreviewViewer stickerPreviewViewer, View view, int i) {
        this.arg$1 = stickerPreviewViewer;
        this.arg$2 = view;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$onInterceptTouchEvent$1$StickerPreviewViewer(this.arg$2, this.arg$3);
    }
}
