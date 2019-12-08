package org.telegram.ui;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI implements SharingLocationsAlertDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ int[] f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$3M4gjjYuGyiykNVPshscLAIEPPI(LaunchActivity launchActivity, int[] iArr) {
        this.f$0 = launchActivity;
        this.f$1 = iArr;
    }

    public final void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
        this.f$0.lambda$handleIntent$9$LaunchActivity(this.f$1, sharingLocationInfo);
    }
}
