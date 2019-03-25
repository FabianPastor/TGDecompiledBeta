package org.telegram.ui;

import org.telegram.ui.Components.RecyclerListView;

final /* synthetic */ class ContentPreviewViewer$$Lambda$1 implements Runnable {
    private final ContentPreviewViewer arg$1;
    private final RecyclerListView arg$2;
    private final int arg$3;
    private final int arg$4;

    ContentPreviewViewer$$Lambda$1(ContentPreviewViewer contentPreviewViewer, RecyclerListView recyclerListView, int i, int i2) {
        this.arg$1 = contentPreviewViewer;
        this.arg$2 = recyclerListView;
        this.arg$3 = i;
        this.arg$4 = i2;
    }

    public void run() {
        this.arg$1.lambda$onInterceptTouchEvent$1$ContentPreviewViewer(this.arg$2, this.arg$3, this.arg$4);
    }
}
