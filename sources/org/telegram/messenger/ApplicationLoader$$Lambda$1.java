package org.telegram.messenger;

final /* synthetic */ class ApplicationLoader$$Lambda$1 implements Runnable {
    static final Runnable $instance = new ApplicationLoader$$Lambda$1();

    private ApplicationLoader$$Lambda$1() {
    }

    public void run() {
        ApplicationLoader.startPushService();
    }
}
