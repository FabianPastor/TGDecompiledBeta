package org.telegram.messenger;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$HjK4JENsms0-KVbFHgtgUvgpN7c implements ResultCallback {
    private final /* synthetic */ LocationController f$0;

    public /* synthetic */ -$$Lambda$LocationController$HjK4JENsms0-KVbFHgtgUvgpN7c(LocationController locationController) {
        this.f$0 = locationController;
    }

    public final void onResult(Result result) {
        this.f$0.lambda$onConnected$3$LocationController((LocationSettingsResult) result);
    }
}
