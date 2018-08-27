package org.telegram.messenger;

final /* synthetic */ class GcmInstanceIDListenerService$$Lambda$0 implements Runnable {
    private final String arg$1;

    GcmInstanceIDListenerService$$Lambda$0(String str) {
        this.arg$1 = str;
    }

    public void run() {
        GcmInstanceIDListenerService.lambda$onTokenRefresh$0$GcmInstanceIDListenerService(this.arg$1);
    }
}
