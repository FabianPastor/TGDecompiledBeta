package org.telegram.ui.Components;

import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19 implements IMapsProvider.OnMarkerClickListener {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout) {
        this.f$0 = chatAttachAlertLocationLayout;
    }

    public final boolean onClick(IMapsProvider.IMarker iMarker) {
        return this.f$0.lambda$onMapInit$21(iMarker);
    }
}
