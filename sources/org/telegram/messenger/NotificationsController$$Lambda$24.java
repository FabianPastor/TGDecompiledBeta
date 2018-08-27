package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$24 implements Runnable {
    private final NotificationsController arg$1;
    private final ArrayList arg$2;
    private final int arg$3;

    NotificationsController$$Lambda$24(NotificationsController notificationsController, ArrayList arrayList, int i) {
        this.arg$1 = notificationsController;
        this.arg$2 = arrayList;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$14$NotificationsController(this.arg$2, this.arg$3);
    }
}
