package org.telegram.p005ui;

/* renamed from: org.telegram.ui.PhotoViewer$$Lambda$35 */
final /* synthetic */ class PhotoViewer$$Lambda$35 implements Runnable {
    private final PhotoViewer arg$1;
    private final int arg$2;

    PhotoViewer$$Lambda$35(PhotoViewer photoViewer, int i) {
        this.arg$1 = photoViewer;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$redraw$43$PhotoViewer(this.arg$2);
    }
}
