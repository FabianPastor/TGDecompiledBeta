package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.Paint.UndoStore.UndoStoreDelegate;

/* renamed from: org.telegram.ui.Components.PhotoPaintView$$Lambda$0 */
final /* synthetic */ class PhotoPaintView$$Lambda$0 implements UndoStoreDelegate {
    private final PhotoPaintView arg$1;

    PhotoPaintView$$Lambda$0(PhotoPaintView photoPaintView) {
        this.arg$1 = photoPaintView;
    }

    public void historyChanged() {
        this.arg$1.lambda$new$0$PhotoPaintView();
    }
}
