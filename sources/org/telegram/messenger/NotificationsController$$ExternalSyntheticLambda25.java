package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda25(NotificationsController notificationsController, LongSparseArray longSparseArray, boolean z, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = longSparseArray;
        this.f$2 = z;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$removeDeletedMessagesFromNotifications$9(this.f$1, this.f$2, this.f$3);
    }
}
