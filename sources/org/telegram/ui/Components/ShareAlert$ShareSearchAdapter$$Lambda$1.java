package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.ShareAlert.ShareSearchAdapter;

final /* synthetic */ class ShareAlert$ShareSearchAdapter$$Lambda$1 implements Runnable {
    private final ShareSearchAdapter arg$1;
    private final int arg$2;
    private final ArrayList arg$3;

    ShareAlert$ShareSearchAdapter$$Lambda$1(ShareSearchAdapter shareSearchAdapter, int i, ArrayList arrayList) {
        this.arg$1 = shareSearchAdapter;
        this.arg$2 = i;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(this.arg$2, this.arg$3);
    }
}
