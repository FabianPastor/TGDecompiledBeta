package org.telegram.messenger;

final /* synthetic */ class GcmPushListenerService$$Lambda$2 implements Runnable {
    private final String arg$1;

    GcmPushListenerService$$Lambda$2(String str) {
        this.arg$1 = str;
    }

    public void run() {
        GcmPushListenerService.lambda$sendRegistrationToServer$5$GcmPushListenerService(this.arg$1);
    }
}
