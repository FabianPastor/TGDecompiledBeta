package org.telegram.ui.Adapters;

import java.util.ArrayList;

public final /* synthetic */ class DialogsSearchAdapter$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ DialogsSearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;

    public /* synthetic */ DialogsSearchAdapter$$ExternalSyntheticLambda8(DialogsSearchAdapter dialogsSearchAdapter, int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$12(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
