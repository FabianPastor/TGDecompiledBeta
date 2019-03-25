package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.DataQuery.KeywordResultCallback;

final /* synthetic */ class DataQuery$$Lambda$73 implements Runnable {
    private final DataQuery arg$1;
    private final String arg$2;
    private final KeywordResultCallback arg$3;
    private final String arg$4;
    private final boolean arg$5;
    private final ArrayList arg$6;
    private final CountDownLatch arg$7;

    DataQuery$$Lambda$73(DataQuery dataQuery, String str, KeywordResultCallback keywordResultCallback, String str2, boolean z, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.arg$1 = dataQuery;
        this.arg$2 = str;
        this.arg$3 = keywordResultCallback;
        this.arg$4 = str2;
        this.arg$5 = z;
        this.arg$6 = arrayList;
        this.arg$7 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getEmojiSuggestions$122$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
