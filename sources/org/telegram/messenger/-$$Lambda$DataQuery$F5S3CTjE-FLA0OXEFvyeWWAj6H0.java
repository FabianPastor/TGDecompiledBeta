package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.DataQuery.KeywordResultCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$F5S3CTjE-FLA0OXEFvyeWWAj6H0 implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ String[] f$1;
    private final /* synthetic */ KeywordResultCallback f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$DataQuery$F5S3CTjE-FLA0OXEFvyeWWAj6H0(DataQuery dataQuery, String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        this.f$0 = dataQuery;
        this.f$1 = strArr;
        this.f$2 = keywordResultCallback;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$null$119$DataQuery(this.f$1, this.f$2, this.f$3);
    }
}
