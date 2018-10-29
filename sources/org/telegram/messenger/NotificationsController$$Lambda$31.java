package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$31 implements Runnable {
    private final NotificationsController arg$1;
    private final ArrayList arg$2;

    NotificationsController$$Lambda$31(NotificationsController notificationsController, ArrayList arrayList) {
        this.arg$1 = notificationsController;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$4$NotificationsController(this.arg$2);
    }
}
