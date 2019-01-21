package org.telegram.ui.Adapters;

import java.util.ArrayList;

final /* synthetic */ class PhonebookSearchAdapter$$Lambda$1 implements Runnable {
    private final PhonebookSearchAdapter arg$1;
    private final String arg$2;
    private final ArrayList arg$3;
    private final ArrayList arg$4;

    PhonebookSearchAdapter$$Lambda$1(PhonebookSearchAdapter phonebookSearchAdapter, String str, ArrayList arrayList, ArrayList arrayList2) {
        this.arg$1 = phonebookSearchAdapter;
        this.arg$2 = str;
        this.arg$3 = arrayList;
        this.arg$4 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$2$PhonebookSearchAdapter(this.arg$2, this.arg$3, this.arg$4);
    }
}
