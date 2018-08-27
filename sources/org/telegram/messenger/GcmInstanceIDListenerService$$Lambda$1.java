package org.telegram.messenger;

final /* synthetic */ class GcmInstanceIDListenerService$$Lambda$1 implements Runnable {
    private final String arg$1;

    GcmInstanceIDListenerService$$Lambda$1(String str) {
        this.arg$1 = str;
    }

    public void run() {
        GcmInstanceIDListenerService.lambda$sendRegistrationToServer$2$GcmInstanceIDListenerService(this.arg$1);
    }
}
