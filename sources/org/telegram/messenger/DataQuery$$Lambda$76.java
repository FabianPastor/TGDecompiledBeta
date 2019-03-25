package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.DataQuery.KeywordResultCallback;

final /* synthetic */ class DataQuery$$Lambda$76 implements Runnable {
    private final KeywordResultCallback arg$1;
    private final ArrayList arg$2;
    private final String arg$3;

    DataQuery$$Lambda$76(KeywordResultCallback keywordResultCallback, ArrayList arrayList, String str) {
        this.arg$1 = keywordResultCallback;
        this.arg$2 = arrayList;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.run(this.arg$2, this.arg$3);
    }
}
