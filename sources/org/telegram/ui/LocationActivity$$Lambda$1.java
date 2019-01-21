package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;

final /* synthetic */ class LocationActivity$$Lambda$1 implements BaseLocationAdapterDelegate {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$1(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void didLoadedSearchResult(ArrayList arrayList) {
        this.arg$1.lambda$createView$2$LocationActivity(arrayList);
    }
}
