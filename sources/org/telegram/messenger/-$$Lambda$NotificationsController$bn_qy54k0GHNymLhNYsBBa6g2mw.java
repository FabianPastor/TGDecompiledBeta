package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$bn_qy54k0GHNymLhNYsBBa6g2mw implements Runnable {
    private final /* synthetic */ NotificationsController f$0;
    private final /* synthetic */ SparseLongArray f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ boolean f$6;

    public /* synthetic */ -$$Lambda$NotificationsController$bn_qy54k0GHNymLhNYsBBa6g2mw(NotificationsController notificationsController, SparseLongArray sparseLongArray, ArrayList arrayList, long j, int i, int i2, boolean z) {
        this.f$0 = notificationsController;
        this.f$1 = sparseLongArray;
        this.f$2 = arrayList;
        this.f$3 = j;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = z;
    }

    public final void run() {
        this.f$0.lambda$processReadMessages$13$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
