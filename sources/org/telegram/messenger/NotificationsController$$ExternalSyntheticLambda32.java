package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda32(NotificationsController notificationsController, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2) {
        this.f$0 = notificationsController;
        this.f$1 = arrayList;
        this.f$2 = longSparseArray;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedUnreadMessages$23(this.f$1, this.f$2, this.f$3);
    }
}