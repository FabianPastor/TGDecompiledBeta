package org.telegram.messenger;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda0 implements ResultCallback {
    public final /* synthetic */ LocationController f$0;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda0(LocationController locationController) {
        this.f$0 = locationController;
    }

    public final void onResult(Result result) {
        this.f$0.lambda$onConnected$4((LocationSettingsResult) result);
    }
}
