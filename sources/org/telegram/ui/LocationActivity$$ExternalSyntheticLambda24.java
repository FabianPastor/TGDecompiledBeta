package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Adapters.BaseLocationAdapter;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda24 implements BaseLocationAdapter.BaseLocationAdapterDelegate {
    public final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda24(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final void didLoadSearchResult(ArrayList arrayList) {
        this.f$0.updatePlacesMarkers(arrayList);
    }
}
