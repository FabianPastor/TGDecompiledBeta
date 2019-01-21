package org.telegram.ui.Adapters;

import android.util.LongSparseArray;
import java.util.ArrayList;

final /* synthetic */ class DialogsSearchAdapter$$Lambda$10 implements Runnable {
    private final DialogsSearchAdapter arg$1;
    private final ArrayList arg$2;
    private final LongSparseArray arg$3;

    DialogsSearchAdapter$$Lambda$10(DialogsSearchAdapter dialogsSearchAdapter, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.arg$1 = dialogsSearchAdapter;
        this.arg$2 = arrayList;
        this.arg$3 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$null$3$DialogsSearchAdapter(this.arg$2, this.arg$3);
    }
}
