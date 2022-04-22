package org.telegram.ui;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ContentPreviewViewer$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ContentPreviewViewer f$0;
    public final /* synthetic */ RecyclerListView f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ Theme.ResourcesProvider f$4;

    public /* synthetic */ ContentPreviewViewer$$ExternalSyntheticLambda4(ContentPreviewViewer contentPreviewViewer, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = contentPreviewViewer;
        this.f$1 = recyclerListView;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = resourcesProvider;
    }

    public final void run() {
        this.f$0.lambda$onInterceptTouchEvent$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
