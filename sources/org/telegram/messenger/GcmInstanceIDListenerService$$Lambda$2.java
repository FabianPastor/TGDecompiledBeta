package org.telegram.messenger;

final /* synthetic */ class GcmInstanceIDListenerService$$Lambda$2 implements Runnable {
    private final int arg$1;
    private final String arg$2;

    GcmInstanceIDListenerService$$Lambda$2(int i, String str) {
        this.arg$1 = i;
        this.arg$2 = str;
    }

    public void run() {
        MessagesController.getInstance(this.arg$1).registerForPush(this.arg$2);
    }
}
