package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$9 implements Runnable {
    private final NotificationsController arg$1;
    private final LongSparseArray arg$2;
    private final ArrayList arg$3;

    NotificationsController$$Lambda$9(NotificationsController notificationsController, LongSparseArray longSparseArray, ArrayList arrayList) {
        this.arg$1 = notificationsController;
        this.arg$2 = longSparseArray;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processDialogsUpdateRead$19$NotificationsController(this.arg$2, this.arg$3);
    }
}
