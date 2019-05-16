package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.DataQuery.KeywordResultCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$WcOlD4X1lYU5wXzTVWls1TBaRSM implements Runnable {
    private final /* synthetic */ KeywordResultCallback f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$DataQuery$WcOlD4X1lYU5wXzTVWls1TBaRSM(KeywordResultCallback keywordResultCallback, ArrayList arrayList, String str) {
        this.f$0 = keywordResultCallback;
        this.f$1 = arrayList;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.run(this.f$1, this.f$2);
    }
}
