package org.telegram.p005ui.Adapters;

import android.location.Location;
import org.telegram.p005ui.Adapters.BaseLocationAdapter.CLASSNAME;

/* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$1$$Lambda$0 */
final /* synthetic */ class BaseLocationAdapter$1$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final String arg$2;
    private final Location arg$3;

    BaseLocationAdapter$1$$Lambda$0(CLASSNAME CLASSNAME, String str, Location location) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = str;
        this.arg$3 = location;
    }

    public void run() {
        this.arg$1.lambda$run$0$BaseLocationAdapter$1(this.arg$2, this.arg$3);
    }
}
