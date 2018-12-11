package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$$Lambda$0 implements Runnable {
    private final ConnectionsManager arg$1;
    private final boolean arg$10;
    private final TLObject arg$2;
    private final int arg$3;
    private final RequestDelegate arg$4;
    private final QuickAckDelegate arg$5;
    private final WriteToSocketDelegate arg$6;
    private final int arg$7;
    private final int arg$8;
    private final int arg$9;

    ConnectionsManager$$Lambda$0(ConnectionsManager connectionsManager, TLObject tLObject, int i, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z) {
        this.arg$1 = connectionsManager;
        this.arg$2 = tLObject;
        this.arg$3 = i;
        this.arg$4 = requestDelegate;
        this.arg$5 = quickAckDelegate;
        this.arg$6 = writeToSocketDelegate;
        this.arg$7 = i2;
        this.arg$8 = i3;
        this.arg$9 = i4;
        this.arg$10 = z;
    }

    public void run() {
        this.arg$1.lambda$sendRequest$2$ConnectionsManager(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10);
    }
}
