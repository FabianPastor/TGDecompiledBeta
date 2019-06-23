package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController.LocationFetchCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$bOYgJNCZwK3lpg5f5m5MU9x-WWw implements Runnable {
    private final /* synthetic */ LocationFetchCallback f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Location f$3;

    public /* synthetic */ -$$Lambda$LocationController$bOYgJNCZwK3lpg5f5m5MU9x-WWw(LocationFetchCallback locationFetchCallback, String str, String str2, Location location) {
        this.f$0 = locationFetchCallback;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = location;
    }

    public final void run() {
        LocationController.lambda$null$21(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
