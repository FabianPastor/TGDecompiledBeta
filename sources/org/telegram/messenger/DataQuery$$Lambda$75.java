package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.DataQuery.KeywordResult;

final /* synthetic */ class DataQuery$$Lambda$75 implements Comparator {
    private final ArrayList arg$1;

    DataQuery$$Lambda$75(ArrayList arrayList) {
        this.arg$1 = arrayList;
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$null$120$DataQuery(this.arg$1, (KeywordResult) obj, (KeywordResult) obj2);
    }
}
