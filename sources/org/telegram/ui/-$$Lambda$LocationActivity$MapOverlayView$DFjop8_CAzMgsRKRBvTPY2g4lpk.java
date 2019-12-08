package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.model.Marker;
import org.telegram.ui.LocationActivity.MapOverlayView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$MapOverlayView$DFjop8_CAzMgsRKRBvTPY2g4lpk implements OnClickListener {
    private final /* synthetic */ MapOverlayView f$0;
    private final /* synthetic */ Marker f$1;

    public /* synthetic */ -$$Lambda$LocationActivity$MapOverlayView$DFjop8_CAzMgsRKRBvTPY2g4lpk(MapOverlayView mapOverlayView, Marker marker) {
        this.f$0 = mapOverlayView;
        this.f$1 = marker;
    }

    public final void onClick(View view) {
        this.f$0.lambda$addInfoView$1$LocationActivity$MapOverlayView(this.f$1, view);
    }
}
