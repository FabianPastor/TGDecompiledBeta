package org.telegram.ui.Adapters;

import android.location.Location;
import org.telegram.ui.Adapters.BaseLocationAdapter.C05061;

final /* synthetic */ class BaseLocationAdapter$1$$Lambda$0 implements Runnable {
    private final C05061 arg$1;
    private final String arg$2;
    private final Location arg$3;

    BaseLocationAdapter$1$$Lambda$0(C05061 c05061, String str, Location location) {
        this.arg$1 = c05061;
        this.arg$2 = str;
        this.arg$3 = location;
    }

    public void run() {
        this.arg$1.lambda$run$0$BaseLocationAdapter$1(this.arg$2, this.arg$3);
    }
}
