package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.ShareAlert;

public final /* synthetic */ class ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ShareAlert.ShareSearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda1(ShareAlert.ShareSearchAdapter shareSearchAdapter, int i, ArrayList arrayList) {
        this.f$0 = shareSearchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$2(this.f$1, this.f$2);
    }
}
