package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController.LocationFetchCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$f_wQSf0Yb70kecs4vFip2jNqUfk implements Runnable {
    private final /* synthetic */ LocationFetchCallback f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Location f$3;

    public /* synthetic */ -$$Lambda$LocationController$f_wQSf0Yb70kecs4vFip2jNqUfk(LocationFetchCallback locationFetchCallback, String str, String str2, Location location) {
        this.f$0 = locationFetchCallback;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = location;
    }

    public final void run() {
        LocationController.lambda$null$26(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
