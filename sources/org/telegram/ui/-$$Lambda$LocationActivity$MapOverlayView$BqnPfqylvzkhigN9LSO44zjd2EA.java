package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;
import org.telegram.ui.LocationActivity.MapOverlayView;
import org.telegram.ui.LocationActivity.VenueLocation;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$MapOverlayView$BqnPfqylvzkhigN9LSO44zjd2EA implements ScheduleDatePickerDelegate {
    private final /* synthetic */ MapOverlayView f$0;
    private final /* synthetic */ VenueLocation f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$MapOverlayView$BqnPfqylvzkhigN9LSO44zjd2EA(MapOverlayView mapOverlayView, VenueLocation venueLocation) {
        this.f$0 = mapOverlayView;
        this.f$1 = venueLocation;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$null$0$LocationActivity$MapOverlayView(this.f$1, z, i);
    }
}
