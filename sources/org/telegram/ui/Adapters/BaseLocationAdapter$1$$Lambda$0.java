package org.telegram.ui.Adapters;

import android.location.Location;
import org.telegram.ui.Adapters.BaseLocationAdapter.AnonymousClass1;

final /* synthetic */ class BaseLocationAdapter$1$$Lambda$0 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final String arg$2;
    private final Location arg$3;

    BaseLocationAdapter$1$$Lambda$0(AnonymousClass1 anonymousClass1, String str, Location location) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = str;
        this.arg$3 = location;
    }

    public void run() {
        this.arg$1.lambda$run$0$BaseLocationAdapter$1(this.arg$2, this.arg$3);
    }
}
