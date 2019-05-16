package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.ShareAlert.ShareSearchAdapter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ShareAlert$ShareSearchAdapter$HTelFIImAnZsj4Mdi1ZJ7VKMmtA implements Runnable {
    private final /* synthetic */ ShareSearchAdapter f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$ShareAlert$ShareSearchAdapter$HTelFIImAnZsj4Mdi1ZJ7VKMmtA(ShareSearchAdapter shareSearchAdapter, int i, ArrayList arrayList) {
        this.f$0 = shareSearchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
    }
}
