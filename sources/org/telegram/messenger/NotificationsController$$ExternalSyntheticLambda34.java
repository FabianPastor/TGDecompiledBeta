package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda34 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ LongSparseIntArray f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda34(NotificationsController notificationsController, LongSparseIntArray longSparseIntArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = longSparseIntArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdateRead$21(this.f$1, this.f$2);
    }
}
