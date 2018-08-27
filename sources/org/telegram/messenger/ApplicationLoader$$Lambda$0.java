package org.telegram.messenger;

final /* synthetic */ class ApplicationLoader$$Lambda$0 implements Runnable {
    static final Runnable $instance = new ApplicationLoader$$Lambda$0();

    private ApplicationLoader$$Lambda$0() {
    }

    public void run() {
        ApplicationLoader.startPushService();
    }
}
