package org.telegram.ui;

import org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate;

final /* synthetic */ class PhotoViewer$$Lambda$28 implements PhotoCropViewDelegate {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$28(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onChange(boolean z) {
        this.arg$1.lambda$createCropView$34$PhotoViewer(z);
    }
}
