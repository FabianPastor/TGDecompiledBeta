package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class FilterCreateActivity$$ExternalSyntheticLambda13 implements Comparator {
    public final /* synthetic */ LongSparseIntArray f$0;

    public /* synthetic */ FilterCreateActivity$$ExternalSyntheticLambda13(LongSparseIntArray longSparseIntArray) {
        this.f$0 = longSparseIntArray;
    }

    public final int compare(Object obj, Object obj2) {
        return FilterCreateActivity.lambda$saveFilterToServer$11(this.f$0, (Long) obj, (Long) obj2);
    }
}
