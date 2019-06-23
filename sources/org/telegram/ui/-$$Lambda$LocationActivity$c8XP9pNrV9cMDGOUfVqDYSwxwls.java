package org.telegram.ui;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$c8XP9pNrV9cMDGOUfVqDYSwxwls implements OnMyLocationChangeListener {
    private final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ -$$Lambda$LocationActivity$c8XP9pNrV9cMDGOUfVqDYSwxwls(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final void onMyLocationChange(Location location) {
        this.f$0.lambda$onMapInit$12$LocationActivity(location);
    }
}
