package org.telegram.ui.Adapters;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsSearchAdapter$-rG-J91Hu2opSK8sjEhFEpnZiuA implements Runnable {
    private final /* synthetic */ DialogsSearchAdapter f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;

    public /* synthetic */ -$$Lambda$DialogsSearchAdapter$-rG-J91Hu2opSK8sjEhFEpnZiuA(DialogsSearchAdapter dialogsSearchAdapter, int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$9$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
