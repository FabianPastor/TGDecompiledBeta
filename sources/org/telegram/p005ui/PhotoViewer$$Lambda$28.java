package org.telegram.p005ui;

import org.telegram.p005ui.Components.PhotoCropView.PhotoCropViewDelegate;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$28 */
final /* synthetic */ class PhotoViewer$$Lambda$28 implements PhotoCropViewDelegate {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$28(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void onChange(boolean z) {
        this.arg$1.lambda$createCropView$34$PhotoViewer(z);
    }
}
