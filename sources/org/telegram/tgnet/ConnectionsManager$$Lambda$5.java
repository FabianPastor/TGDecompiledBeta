package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$$Lambda$5 implements Runnable {
    private final int arg$1;

    ConnectionsManager$$Lambda$5(int i) {
        this.arg$1 = i;
    }

    public void run() {
        ConnectionsManager.lambda$onLogout$7$ConnectionsManager(this.arg$1);
    }
}
