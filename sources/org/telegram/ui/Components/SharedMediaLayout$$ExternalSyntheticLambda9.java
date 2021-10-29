package org.telegram.ui.Components;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ RecyclerListView f$2;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda9(SharedMediaLayout sharedMediaLayout, int i, RecyclerListView recyclerListView) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = i;
        this.f$2 = recyclerListView;
    }

    public final void run() {
        this.f$0.lambda$checkLoadMoreScroll$10(this.f$1, this.f$2);
    }
}
