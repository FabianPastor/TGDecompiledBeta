package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$29 implements Runnable {
    private final NotificationsController arg$1;
    private final ArrayList arg$2;

    NotificationsController$$Lambda$29(NotificationsController notificationsController, ArrayList arrayList) {
        this.arg$1 = notificationsController;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$6$NotificationsController(this.arg$2);
    }
}
