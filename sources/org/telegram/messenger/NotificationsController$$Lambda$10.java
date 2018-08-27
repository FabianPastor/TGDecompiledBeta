package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$10 implements Runnable {
    private final NotificationsController arg$1;
    private final ArrayList arg$2;
    private final LongSparseArray arg$3;

    NotificationsController$$Lambda$10(NotificationsController notificationsController, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.arg$1 = notificationsController;
        this.arg$2 = arrayList;
        this.arg$3 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$processLoadedUnreadMessages$21$NotificationsController(this.arg$2, this.arg$3);
    }
}
