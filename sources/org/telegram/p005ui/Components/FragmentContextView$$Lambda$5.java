package org.telegram.p005ui.Components;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.p005ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;

/* renamed from: org.telegram.ui.Components.FragmentContextView$$Lambda$5 */
final /* synthetic */ class FragmentContextView$$Lambda$5 implements SharingLocationsAlertDelegate {
    private final FragmentContextView arg$1;

    FragmentContextView$$Lambda$5(FragmentContextView fragmentContextView) {
        this.arg$1 = fragmentContextView;
    }

    public void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
        this.arg$1.bridge$lambda$0$FragmentContextView(sharingLocationInfo);
    }
}
