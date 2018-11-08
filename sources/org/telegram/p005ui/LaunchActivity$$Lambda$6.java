package org.telegram.p005ui;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.p005ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$6 */
final /* synthetic */ class LaunchActivity$$Lambda$6 implements SharingLocationsAlertDelegate {
    private final LaunchActivity arg$1;
    private final int[] arg$2;

    LaunchActivity$$Lambda$6(LaunchActivity launchActivity, int[] iArr) {
        this.arg$1 = launchActivity;
        this.arg$2 = iArr;
    }

    public void didSelectLocation(SharingLocationInfo sharingLocationInfo) {
        this.arg$1.lambda$handleIntent$7$LaunchActivity(this.arg$2, sharingLocationInfo);
    }
}
