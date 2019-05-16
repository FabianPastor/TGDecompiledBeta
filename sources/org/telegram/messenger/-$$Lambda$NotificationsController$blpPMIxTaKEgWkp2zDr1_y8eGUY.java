package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$blpPMIxTaKEgWkp2zDr1_y8eGUY implements Runnable {
    private final /* synthetic */ NotificationsController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ CountDownLatch f$5;

    public /* synthetic */ -$$Lambda$NotificationsController$blpPMIxTaKEgWkp2zDr1_y8eGUY(NotificationsController notificationsController, ArrayList arrayList, ArrayList arrayList2, boolean z, boolean z2, CountDownLatch countDownLatch) {
        this.f$0 = notificationsController;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$processNewMessages$16$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
