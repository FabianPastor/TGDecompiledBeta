package org.telegram.messenger;

import com.google.android.gms.common.api.Status;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ Status f$1;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda14(LocationController locationController, Status status) {
        this.f$0 = locationController;
        this.f$1 = status;
    }

    public final void run() {
        this.f$0.lambda$onConnected$1(this.f$1);
    }
}
