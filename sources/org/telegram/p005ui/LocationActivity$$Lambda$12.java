package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$12 */
final /* synthetic */ class LocationActivity$$Lambda$12 implements Runnable {
    private final LocationActivity arg$1;
    private final TLObject arg$2;
    private final long arg$3;

    LocationActivity$$Lambda$12(LocationActivity locationActivity, TLObject tLObject, long j) {
        this.arg$1 = locationActivity;
        this.arg$2 = tLObject;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$null$13$LocationActivity(this.arg$2, this.arg$3);
    }
}
