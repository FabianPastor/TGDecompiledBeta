package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class LocationController$$Lambda$10 implements Runnable {
    private final LocationController arg$1;
    private final long arg$2;
    private final TLObject arg$3;

    LocationController$$Lambda$10(LocationController locationController, long j, TLObject tLObject) {
        this.arg$1 = locationController;
        this.arg$2 = j;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$17$LocationController(this.arg$2, this.arg$3);
    }
}
