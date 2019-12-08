package org.telegram.ui;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$xaLC8G3FvrqkrgVLJnWcDUqWa-4 implements OnMarkerClickListener {
    private final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ -$$Lambda$LocationActivity$xaLC8G3FvrqkrgVLJnWcDUqWa-4(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final boolean onMarkerClick(Marker marker) {
        return this.f$0.lambda$onMapInit$18$LocationActivity(marker);
    }
}
