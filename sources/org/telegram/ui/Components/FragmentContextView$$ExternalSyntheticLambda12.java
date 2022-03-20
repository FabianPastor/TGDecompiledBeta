package org.telegram.ui.Components;

import org.telegram.messenger.LocationController;
import org.telegram.ui.Components.SharingLocationsAlert;

public final /* synthetic */ class FragmentContextView$$ExternalSyntheticLambda12 implements SharingLocationsAlert.SharingLocationsAlertDelegate {
    public final /* synthetic */ FragmentContextView f$0;

    public /* synthetic */ FragmentContextView$$ExternalSyntheticLambda12(FragmentContextView fragmentContextView) {
        this.f$0 = fragmentContextView;
    }

    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
        this.f$0.openSharingLocation(sharingLocationInfo);
    }
}
