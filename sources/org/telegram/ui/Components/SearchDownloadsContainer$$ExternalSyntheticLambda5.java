package org.telegram.ui.Components;

import java.util.ArrayList;

public final /* synthetic */ class SearchDownloadsContainer$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ SearchDownloadsContainer f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ SearchDownloadsContainer$$ExternalSyntheticLambda5(SearchDownloadsContainer searchDownloadsContainer, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = searchDownloadsContainer;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$checkFilesExist$2(this.f$1, this.f$2);
    }
}
