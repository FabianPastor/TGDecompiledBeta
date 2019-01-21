package org.telegram.messenger;

import android.util.SparseIntArray;
import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$6 implements Runnable {
    private final NotificationsController arg$1;
    private final SparseIntArray arg$2;
    private final ArrayList arg$3;

    NotificationsController$$Lambda$6(NotificationsController notificationsController, SparseIntArray sparseIntArray, ArrayList arrayList) {
        this.arg$1 = notificationsController;
        this.arg$2 = sparseIntArray;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(this.arg$2, this.arg$3);
    }
}
