package org.telegram.ui;

final /* synthetic */ class PhotoViewer$$Lambda$37 implements Runnable {
    private final PhotoViewer arg$1;
    private final int arg$2;

    PhotoViewer$$Lambda$37(PhotoViewer photoViewer, int i) {
        this.arg$1 = photoViewer;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$redraw$45$PhotoViewer(this.arg$2);
    }
}
