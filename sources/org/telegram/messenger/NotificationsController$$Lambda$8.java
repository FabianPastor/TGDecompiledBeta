package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class NotificationsController$$Lambda$8 implements Runnable {
    private final NotificationsController arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final ArrayList arg$4;
    private final boolean arg$5;

    NotificationsController$$Lambda$8(NotificationsController notificationsController, ArrayList arrayList, boolean z, ArrayList arrayList2, boolean z2) {
        this.arg$1 = notificationsController;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = arrayList2;
        this.arg$5 = z2;
    }

    public void run() {
        this.arg$1.lambda$processNewMessages$16$NotificationsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
