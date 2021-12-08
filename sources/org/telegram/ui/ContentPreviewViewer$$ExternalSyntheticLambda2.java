package org.telegram.ui;

import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ContentPreviewViewer$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ RecyclerListView f$0;
    public final /* synthetic */ Object f$1;

    public /* synthetic */ ContentPreviewViewer$$ExternalSyntheticLambda2(RecyclerListView recyclerListView, Object obj) {
        this.f$0 = recyclerListView;
        this.f$1 = obj;
    }

    public final void run() {
        ContentPreviewViewer.lambda$onTouch$0(this.f$0, this.f$1);
    }
}
