package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$66 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final long arg$3;

    DataQuery$$Lambda$66(DataQuery dataQuery, ArrayList arrayList, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$clearBotKeyboard$104$DataQuery(this.arg$2, this.arg$3);
    }
}
