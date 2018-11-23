package org.telegram.messenger;

import java.util.Map;

final /* synthetic */ class GcmPushListenerService$$Lambda$4 implements Runnable {
    private final GcmPushListenerService arg$1;
    private final Map arg$2;
    private final long arg$3;

    GcmPushListenerService$$Lambda$4(GcmPushListenerService gcmPushListenerService, Map map, long j) {
        this.arg$1 = gcmPushListenerService;
        this.arg$2 = map;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$null$1$GcmPushListenerService(this.arg$2, this.arg$3);
    }
}
