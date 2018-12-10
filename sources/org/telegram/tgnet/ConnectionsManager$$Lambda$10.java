package org.telegram.tgnet;

final /* synthetic */ class ConnectionsManager$$Lambda$10 implements RequestDelegateInternal {
    private final TLObject arg$1;
    private final RequestDelegate arg$2;

    ConnectionsManager$$Lambda$10(TLObject tLObject, RequestDelegate requestDelegate) {
        this.arg$1 = tLObject;
        this.arg$2 = requestDelegate;
    }

    public void run(long j, int i, String str, int i2) {
        ConnectionsManager.lambda$null$1$ConnectionsManager(this.arg$1, this.arg$2, j, i, str, i2);
    }
}
