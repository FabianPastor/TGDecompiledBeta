package org.telegram.p005ui.Adapters;

import android.location.Location;
import org.telegram.p005ui.Adapters.BaseLocationAdapter.C07431;

/* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$1$$Lambda$0 */
final /* synthetic */ class BaseLocationAdapter$1$$Lambda$0 implements Runnable {
    private final C07431 arg$1;
    private final String arg$2;
    private final Location arg$3;

    BaseLocationAdapter$1$$Lambda$0(C07431 c07431, String str, Location location) {
        this.arg$1 = c07431;
        this.arg$2 = str;
        this.arg$3 = location;
    }

    public void run() {
        this.arg$1.lambda$run$0$BaseLocationAdapter$1(this.arg$2, this.arg$3);
    }
}
