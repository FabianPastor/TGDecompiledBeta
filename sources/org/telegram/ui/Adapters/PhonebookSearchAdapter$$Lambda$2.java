package org.telegram.ui.Adapters;

import java.util.ArrayList;

final /* synthetic */ class PhonebookSearchAdapter$$Lambda$2 implements Runnable {
    private final PhonebookSearchAdapter arg$1;
    private final String arg$2;
    private final ArrayList arg$3;
    private final ArrayList arg$4;
    private final int arg$5;

    PhonebookSearchAdapter$$Lambda$2(PhonebookSearchAdapter phonebookSearchAdapter, String str, ArrayList arrayList, ArrayList arrayList2, int i) {
        this.arg$1 = phonebookSearchAdapter;
        this.arg$2 = str;
        this.arg$3 = arrayList;
        this.arg$4 = arrayList2;
        this.arg$5 = i;
    }

    public void run() {
        this.arg$1.lambda$null$0$PhonebookSearchAdapter(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
