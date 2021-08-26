package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Adapters.BaseLocationAdapter;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda23 implements BaseLocationAdapter.BaseLocationAdapterDelegate {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda23(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout) {
        this.f$0 = chatAttachAlertLocationLayout;
    }

    public final void didLoadSearchResult(ArrayList arrayList) {
        this.f$0.updatePlacesMarkers(arrayList);
    }
}
