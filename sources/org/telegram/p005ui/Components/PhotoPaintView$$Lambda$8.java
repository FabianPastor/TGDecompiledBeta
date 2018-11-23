package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.Paint.Views.EntityView;

/* renamed from: org.telegram.ui.Components.PhotoPaintView$$Lambda$8 */
final /* synthetic */ class PhotoPaintView$$Lambda$8 implements Runnable {
    private final PhotoPaintView arg$1;
    private final EntityView arg$2;

    PhotoPaintView$$Lambda$8(PhotoPaintView photoPaintView, EntityView entityView) {
        this.arg$1 = photoPaintView;
        this.arg$2 = entityView;
    }

    public void run() {
        this.arg$1.lambda$showMenuForEntity$11$PhotoPaintView(this.arg$2);
    }
}
