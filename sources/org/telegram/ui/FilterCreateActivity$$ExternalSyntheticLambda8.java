package org.telegram.ui;

import android.util.LongSparseArray;
import java.util.Comparator;

public final /* synthetic */ class FilterCreateActivity$$ExternalSyntheticLambda8 implements Comparator {
    public final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ FilterCreateActivity$$ExternalSyntheticLambda8(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return FilterCreateActivity.lambda$saveFilterToServer$11(this.f$0, (Integer) obj, (Integer) obj2);
    }
}
