package org.telegram.ui.Components;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;

final /* synthetic */ class FragmentContextView$$Lambda$5 implements SharingLocationsAlertDelegate {
    private final FragmentContextView arg$1;

    FragmentContextView$$Lambda$5(FragmentContextView fragmentContextView) {
        this.arg$1 = fragmentContextView;
    }

    public void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
        this.arg$1.bridge$lambda$0$FragmentContextView(sharingLocationInfo);
    }
}
