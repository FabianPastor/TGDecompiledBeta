package org.telegram.messenger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import org.telegram.messenger.ILocationServiceProvider;

public final /* synthetic */ class GoogleLocationProvider$$ExternalSyntheticLambda0 implements GoogleApiClient.OnConnectionFailedListener {
    public final /* synthetic */ ILocationServiceProvider.IAPIOnConnectionFailedListener f$0;

    public /* synthetic */ GoogleLocationProvider$$ExternalSyntheticLambda0(ILocationServiceProvider.IAPIOnConnectionFailedListener iAPIOnConnectionFailedListener) {
        this.f$0 = iAPIOnConnectionFailedListener;
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        this.f$0.onConnectionFailed();
    }
}
