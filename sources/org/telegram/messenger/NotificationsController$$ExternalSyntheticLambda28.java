package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda28(NotificationsController notificationsController, ArrayList arrayList) {
        this.f$0 = notificationsController;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdateRead$19(this.f$1);
    }
}
