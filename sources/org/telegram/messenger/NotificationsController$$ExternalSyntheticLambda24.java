package org.telegram.messenger;

import android.util.LongSparseArray;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ LongSparseArray f$1;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda24(NotificationsController notificationsController, LongSparseArray longSparseArray) {
        this.f$0 = notificationsController;
        this.f$1 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$processEditedMessages$15(this.f$1);
    }
}
