package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class StickerPreviewViewer$$Lambda$2 implements OnTouchListener {
    private final StickerPreviewViewer arg$1;

    StickerPreviewViewer$$Lambda$2(StickerPreviewViewer stickerPreviewViewer) {
        this.arg$1 = stickerPreviewViewer;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$setParentActivity$2$StickerPreviewViewer(view, motionEvent);
    }
}