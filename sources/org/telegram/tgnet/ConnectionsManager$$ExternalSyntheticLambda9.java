package org.telegram.tgnet;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ConnectionsManager f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ RequestDelegate f$3;
    public final /* synthetic */ RequestDelegateTimestamp f$4;
    public final /* synthetic */ QuickAckDelegate f$5;
    public final /* synthetic */ WriteToSocketDelegate f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda9(ConnectionsManager connectionsManager, TLObject tLObject, int i, RequestDelegate requestDelegate, RequestDelegateTimestamp requestDelegateTimestamp, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z) {
        this.f$0 = connectionsManager;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = requestDelegate;
        this.f$4 = requestDelegateTimestamp;
        this.f$5 = quickAckDelegate;
        this.f$6 = writeToSocketDelegate;
        this.f$7 = i2;
        this.f$8 = i3;
        this.f$9 = i4;
        this.f$10 = z;
    }

    public final void run() {
        this.f$0.lambda$sendRequest$2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
