package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$IPQvEgGc0E_OyZ6XMjHW9Pf7Ywc implements BaseLocationAdapterDelegate {
    private final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ -$$Lambda$LocationActivity$IPQvEgGc0E_OyZ6XMjHW9Pf7Ywc(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final void didLoadSearchResult(ArrayList arrayList) {
        this.f$0.updatePlacesMarkers(arrayList);
    }
}
