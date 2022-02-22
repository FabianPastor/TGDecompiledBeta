package org.telegram.ui;

import org.telegram.messenger.LocationController;
import org.telegram.ui.Components.SharingLocationsAlert;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda82 implements SharingLocationsAlert.SharingLocationsAlertDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int[] f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda82(LaunchActivity launchActivity, int[] iArr) {
        this.f$0 = launchActivity;
        this.f$1 = iArr;
    }

    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
        this.f$0.lambda$handleIntent$12(this.f$1, sharingLocationInfo);
    }
}
