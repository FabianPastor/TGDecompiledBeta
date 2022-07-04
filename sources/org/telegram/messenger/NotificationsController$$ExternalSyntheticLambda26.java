package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ CountDownLatch f$5;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda26(NotificationsController notificationsController, ArrayList arrayList, ArrayList arrayList2, boolean z, boolean z2, CountDownLatch countDownLatch) {
        this.f$0 = notificationsController;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = countDownLatch;
    }

    public final void run() {
        this.f$0.m2341xffba819a(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
