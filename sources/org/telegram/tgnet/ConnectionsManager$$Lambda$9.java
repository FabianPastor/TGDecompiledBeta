package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$$Lambda$9 implements Runnable {
    private final ConnectionsManager arg$1;
    private final boolean arg$2;

    ConnectionsManager$$Lambda$9(ConnectionsManager connectionsManager, boolean z) {
        this.arg$1 = connectionsManager;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$setIsUpdating$11$ConnectionsManager(this.arg$2);
    }
}
