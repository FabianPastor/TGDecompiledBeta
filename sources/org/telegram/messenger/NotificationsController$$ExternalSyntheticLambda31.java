package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda31 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda31(NotificationsController notificationsController, ArrayList arrayList, int i) {
        this.f$0 = notificationsController;
        this.f$1 = arrayList;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$processNewMessages$16(this.f$1, this.f$2);
    }
}