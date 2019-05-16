package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$8lQbr5XMNBt__wC6arYqfGdfeMk implements Runnable {
    private final /* synthetic */ NotificationsController f$0;
    private final /* synthetic */ SparseArray f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$NotificationsController$8lQbr5XMNBt__wC6arYqfGdfeMk(NotificationsController notificationsController, SparseArray sparseArray, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = sparseArray;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(this.f$1, this.f$2);
    }
}
