package org.telegram.ui;

import org.telegram.ui.Components.RecyclerListView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContentPreviewViewer$wFY_75sBoPTmhZIpv5QBGVqAeaE implements Runnable {
    private final /* synthetic */ ContentPreviewViewer f$0;
    private final /* synthetic */ RecyclerListView f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$ContentPreviewViewer$wFY_75sBoPTmhZIpv5QBGVqAeaE(ContentPreviewViewer contentPreviewViewer, RecyclerListView recyclerListView, int i, int i2) {
        this.f$0 = contentPreviewViewer;
        this.f$1 = recyclerListView;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.lambda$onInterceptTouchEvent$1$ContentPreviewViewer(this.f$1, this.f$2, this.f$3);
    }
}
