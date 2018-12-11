package org.telegram.p005ui;

import android.view.View;

/* renamed from: org.telegram.ui.StickerPreviewViewer$$Lambda$0 */
final /* synthetic */ class StickerPreviewViewer$$Lambda$0 implements Runnable {
    private final View arg$1;
    private final Object arg$2;

    StickerPreviewViewer$$Lambda$0(View view, Object obj) {
        this.arg$1 = view;
        this.arg$2 = obj;
    }

    public void run() {
        StickerPreviewViewer.lambda$onTouch$0$StickerPreviewViewer(this.arg$1, this.arg$2);
    }
}
