package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$5 implements Runnable {
    private final NotificationsController arg$1;
    private final SparseArray arg$2;
    private final ArrayList arg$3;

    NotificationsController$$Lambda$5(NotificationsController notificationsController, SparseArray sparseArray, ArrayList arrayList) {
        this.arg$1 = notificationsController;
        this.arg$2 = sparseArray;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(this.arg$2, this.arg$3);
    }
}
