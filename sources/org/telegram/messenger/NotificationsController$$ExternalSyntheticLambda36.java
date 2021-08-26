package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ SparseLongArray f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda36(NotificationsController notificationsController, SparseLongArray sparseLongArray, ArrayList arrayList, long j, int i, int i2, boolean z) {
        this.f$0 = notificationsController;
        this.f$1 = sparseLongArray;
        this.f$2 = arrayList;
        this.f$3 = j;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = z;
    }

    public final void run() {
        this.f$0.lambda$processReadMessages$14(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
