package org.telegram.tgnet;

import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ConnectionsManager$$Lambda$11 implements Runnable {
    private final RequestDelegate arg$1;
    private final TLObject arg$2;
    private final TL_error arg$3;

    ConnectionsManager$$Lambda$11(RequestDelegate requestDelegate, TLObject tLObject, TL_error tL_error) {
        this.arg$1 = requestDelegate;
        this.arg$2 = tLObject;
        this.arg$3 = tL_error;
    }

    public void run() {
        ConnectionsManager.lambda$null$0$ConnectionsManager(this.arg$1, this.arg$2, this.arg$3);
    }
}
