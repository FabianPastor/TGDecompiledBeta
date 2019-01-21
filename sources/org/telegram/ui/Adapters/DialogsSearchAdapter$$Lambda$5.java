package org.telegram.ui.Adapters;

import java.util.ArrayList;

final /* synthetic */ class DialogsSearchAdapter$$Lambda$5 implements Runnable {
    private final DialogsSearchAdapter arg$1;
    private final int arg$2;
    private final ArrayList arg$3;
    private final ArrayList arg$4;
    private final ArrayList arg$5;

    DialogsSearchAdapter$$Lambda$5(DialogsSearchAdapter dialogsSearchAdapter, int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        this.arg$1 = dialogsSearchAdapter;
        this.arg$2 = i;
        this.arg$3 = arrayList;
        this.arg$4 = arrayList2;
        this.arg$5 = arrayList3;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$9$DialogsSearchAdapter(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
