package org.telegram.tgnet;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda13 implements RequestDelegateInternal {
    public final /* synthetic */ ConnectionsManager f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ RequestDelegate f$2;
    public final /* synthetic */ RequestDelegateTimestamp f$3;
    public final /* synthetic */ QuickAckDelegate f$4;
    public final /* synthetic */ WriteToSocketDelegate f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda13(ConnectionsManager connectionsManager, TLObject tLObject, RequestDelegate requestDelegate, RequestDelegateTimestamp requestDelegateTimestamp, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i, int i2, int i3, boolean z) {
        this.f$0 = connectionsManager;
        this.f$1 = tLObject;
        this.f$2 = requestDelegate;
        this.f$3 = requestDelegateTimestamp;
        this.f$4 = quickAckDelegate;
        this.f$5 = writeToSocketDelegate;
        this.f$6 = i;
        this.f$7 = i2;
        this.f$8 = i3;
        this.f$9 = z;
    }

    public final void run(long j, int i, String str, int i2, long j2) {
        this.f$0.lambda$sendRequest$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, j, i, str, i2, j2);
    }
}
