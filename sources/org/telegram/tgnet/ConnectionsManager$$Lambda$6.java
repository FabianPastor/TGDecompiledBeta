package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$$Lambda$6 implements Runnable {
    private final int arg$1;
    private final int arg$2;

    ConnectionsManager$$Lambda$6(int i, int i2) {
        this.arg$1 = i;
        this.arg$2 = i2;
    }

    public void run() {
        ConnectionsManager.lambda$onRequestNewServerIpAndPort$8$ConnectionsManager(this.arg$1, this.arg$2);
    }
}
