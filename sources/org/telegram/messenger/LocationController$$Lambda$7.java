package org.telegram.messenger;

final /* synthetic */ class LocationController$$Lambda$7 implements Runnable {
    private final LocationController arg$1;
    private final long arg$2;

    LocationController$$Lambda$7(LocationController locationController, long j) {
        this.arg$1 = locationController;
        this.arg$2 = j;
    }

    public void run() {
        this.arg$1.lambda$removeSharingLocation$13$LocationController(this.arg$2);
    }
}
