package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class LocationActivity$MapOverlayView$$ExternalSyntheticLambda1 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ LocationActivity.MapOverlayView f$0;
    public final /* synthetic */ LocationActivity.VenueLocation f$1;

    public /* synthetic */ LocationActivity$MapOverlayView$$ExternalSyntheticLambda1(LocationActivity.MapOverlayView mapOverlayView, LocationActivity.VenueLocation venueLocation) {
        this.f$0 = mapOverlayView;
        this.f$1 = venueLocation;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m3182x40ff7d82(this.f$1, z, i);
    }
}
