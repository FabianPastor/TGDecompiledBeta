package org.telegram.messenger;

import android.location.Location;
import androidx.core.util.Consumer;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ LocationController f$0;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda0(LocationController locationController) {
        this.f$0 = locationController;
    }

    public final void accept(Object obj) {
        this.f$0.setLastKnownLocation((Location) obj);
    }
}
