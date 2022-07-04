package org.telegram.tgnet;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda13 implements RequestDelegateInternal {
    public final /* synthetic */ TLObject f$0;
    public final /* synthetic */ RequestDelegate f$1;
    public final /* synthetic */ RequestDelegateTimestamp f$2;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda13(TLObject tLObject, RequestDelegate requestDelegate, RequestDelegateTimestamp requestDelegateTimestamp) {
        this.f$0 = tLObject;
        this.f$1 = requestDelegate;
        this.f$2 = requestDelegateTimestamp;
    }

    public final void run(long j, int i, String str, int i2, long j2) {
        ConnectionsManager.lambda$sendRequest$1(this.f$0, this.f$1, this.f$2, j, i, str, i2, j2);
    }
}
