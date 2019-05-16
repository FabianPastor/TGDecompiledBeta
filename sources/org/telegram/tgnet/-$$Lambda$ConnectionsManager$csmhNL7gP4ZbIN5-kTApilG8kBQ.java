package org.telegram.tgnet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$csmhNL7gP4ZbIN5-kTApilG8kBQ implements Runnable {
    private final /* synthetic */ ConnectionsManager f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ RequestDelegate f$3;
    private final /* synthetic */ QuickAckDelegate f$4;
    private final /* synthetic */ WriteToSocketDelegate f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ int f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$ConnectionsManager$csmhNL7gP4ZbIN5-kTApilG8kBQ(ConnectionsManager connectionsManager, TLObject tLObject, int i, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z) {
        this.f$0 = connectionsManager;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = requestDelegate;
        this.f$4 = quickAckDelegate;
        this.f$5 = writeToSocketDelegate;
        this.f$6 = i2;
        this.f$7 = i3;
        this.f$8 = i4;
        this.f$9 = z;
    }

    public final void run() {
        this.f$0.lambda$sendRequest$2$ConnectionsManager(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
