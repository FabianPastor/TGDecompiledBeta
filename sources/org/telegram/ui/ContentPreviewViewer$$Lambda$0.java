package org.telegram.ui;

import org.telegram.ui.Components.RecyclerListView;

final /* synthetic */ class ContentPreviewViewer$$Lambda$0 implements Runnable {
    private final RecyclerListView arg$1;
    private final Object arg$2;

    ContentPreviewViewer$$Lambda$0(RecyclerListView recyclerListView, Object obj) {
        this.arg$1 = recyclerListView;
        this.arg$2 = obj;
    }

    public void run() {
        ContentPreviewViewer.lambda$onTouch$0$ContentPreviewViewer(this.arg$1, this.arg$2);
    }
}
