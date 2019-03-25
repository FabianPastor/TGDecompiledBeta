package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.DataQuery.KeywordResultCallback;

final /* synthetic */ class DataQuery$$Lambda$74 implements Runnable {
    private final DataQuery arg$1;
    private final String[] arg$2;
    private final KeywordResultCallback arg$3;
    private final ArrayList arg$4;

    DataQuery$$Lambda$74(DataQuery dataQuery, String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        this.arg$1 = dataQuery;
        this.arg$2 = strArr;
        this.arg$3 = keywordResultCallback;
        this.arg$4 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$119$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
