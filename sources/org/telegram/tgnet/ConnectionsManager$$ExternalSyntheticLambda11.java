package org.telegram.tgnet;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ RequestDelegate f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ RequestDelegateTimestamp f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda11(RequestDelegate requestDelegate, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, RequestDelegateTimestamp requestDelegateTimestamp, long j) {
        this.f$0 = requestDelegate;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = requestDelegateTimestamp;
        this.f$4 = j;
    }

    public final void run() {
        ConnectionsManager.lambda$sendRequest$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
