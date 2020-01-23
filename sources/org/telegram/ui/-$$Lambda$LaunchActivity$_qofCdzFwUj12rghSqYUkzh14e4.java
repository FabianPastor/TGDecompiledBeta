package org.telegram.ui;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$_qofCdzFwUj12rghSqYUkzh14e4 implements SharingLocationsAlertDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ int[] f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$_qofCdzFwUj12rghSqYUkzh14e4(LaunchActivity launchActivity, int[] iArr) {
        this.f$0 = launchActivity;
        this.f$1 = iArr;
    }

    public final void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
        this.f$0.lambda$handleIntent$7$LaunchActivity(this.f$1, sharingLocationInfo);
    }
}
