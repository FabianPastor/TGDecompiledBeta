package org.telegram.ui.Components;

import org.telegram.ui.Components.Paint.UndoStore.UndoStoreDelegate;

final /* synthetic */ class PhotoPaintView$$Lambda$0 implements UndoStoreDelegate {
    private final PhotoPaintView arg$1;

    PhotoPaintView$$Lambda$0(PhotoPaintView photoPaintView) {
        this.arg$1 = photoPaintView;
    }

    public void historyChanged() {
        this.arg$1.lambda$new$0$PhotoPaintView();
    }
}
