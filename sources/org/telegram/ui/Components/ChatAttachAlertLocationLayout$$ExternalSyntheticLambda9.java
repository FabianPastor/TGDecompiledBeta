package org.telegram.ui.Components;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda9 implements GoogleMap.OnMarkerClickListener {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda9(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout) {
        this.f$0 = chatAttachAlertLocationLayout;
    }

    public final boolean onMarkerClick(Marker marker) {
        return this.f$0.lambda$onMapInit$19(marker);
    }
}
