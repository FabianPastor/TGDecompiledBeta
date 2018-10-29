package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

final /* synthetic */ class NotificationsController$$Lambda$7 implements Runnable {
    private final NotificationsController arg$1;
    private final SparseLongArray arg$2;
    private final ArrayList arg$3;
    private final long arg$4;
    private final int arg$5;
    private final int arg$6;
    private final boolean arg$7;

    NotificationsController$$Lambda$7(NotificationsController notificationsController, SparseLongArray sparseLongArray, ArrayList arrayList, long j, int i, int i2, boolean z) {
        this.arg$1 = notificationsController;
        this.arg$2 = sparseLongArray;
        this.arg$3 = arrayList;
        this.arg$4 = j;
        this.arg$5 = i;
        this.arg$6 = i2;
        this.arg$7 = z;
    }

    public void run() {
        this.arg$1.lambda$processReadMessages$13$NotificationsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
