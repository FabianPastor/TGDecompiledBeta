package org.telegram.ui.Components;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FragmentContextView$Z72HHKSvAYjXtWgaFaDIC5DAHI8 implements SharingLocationsAlertDelegate {
    private final /* synthetic */ FragmentContextView f$0;

    public /* synthetic */ -$$Lambda$FragmentContextView$Z72HHKSvAYjXtWgaFaDIC5DAHI8(FragmentContextView fragmentContextView) {
        this.f$0 = fragmentContextView;
    }

    public final void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
        this.f$0.openSharingLocation(sharingLocationInfo);
    }
}
