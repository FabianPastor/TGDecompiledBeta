package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.LocationActivity.MapOverlayView;
import org.telegram.ui.LocationActivity.VenueLocation;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$MapOverlayView$o5OZdnsBb0Qzhy6B5bQNdu7taGo implements OnClickListener {
    private final /* synthetic */ MapOverlayView f$0;
    private final /* synthetic */ VenueLocation f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$MapOverlayView$o5OZdnsBb0Qzhy6B5bQNdu7taGo(MapOverlayView mapOverlayView, VenueLocation venueLocation) {
        this.f$0 = mapOverlayView;
        this.f$1 = venueLocation;
    }

    public final void onClick(View view) {
        this.f$0.lambda$addInfoView$1$LocationActivity$MapOverlayView(this.f$1, view);
    }
}
