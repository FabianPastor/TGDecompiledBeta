package org.telegram.tgnet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$YsEmHm13cAxaF3h53HNgd07NbfA implements Runnable {
    private final /* synthetic */ ConnectionsManager f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$ConnectionsManager$YsEmHm13cAxaF3h53HNgd07NbfA(ConnectionsManager connectionsManager, boolean z) {
        this.f$0 = connectionsManager;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.lambda$setIsUpdating$12$ConnectionsManager(this.f$1);
    }
}
