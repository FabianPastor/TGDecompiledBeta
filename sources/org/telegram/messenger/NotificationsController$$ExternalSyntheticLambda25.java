package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda25(NotificationsController notificationsController, LongSparseArray longSparseArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = longSparseArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdateRead$21(this.f$1, this.f$2);
    }
}
