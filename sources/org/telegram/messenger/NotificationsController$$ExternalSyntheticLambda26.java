package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ SparseArray f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda26(NotificationsController notificationsController, SparseArray sparseArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = sparseArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$removeDeletedMessagesFromNotifications$9(this.f$1, this.f$2);
    }
}
