package org.telegram.ui.Components;

import org.telegram.ui.Components.Paint.Views.EntityView;

final /* synthetic */ class PhotoPaintView$$Lambda$7 implements Runnable {
    private final PhotoPaintView arg$1;
    private final EntityView arg$2;

    PhotoPaintView$$Lambda$7(PhotoPaintView photoPaintView, EntityView entityView) {
        this.arg$1 = photoPaintView;
        this.arg$2 = entityView;
    }

    public void run() {
        this.arg$1.lambda$registerRemovalUndo$7$PhotoPaintView(this.arg$2);
    }
}
