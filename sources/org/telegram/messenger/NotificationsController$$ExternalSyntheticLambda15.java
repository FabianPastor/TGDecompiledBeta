package org.telegram.messenger;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda15(NotificationsController notificationsController, int i) {
        this.f$0 = notificationsController;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$removeDeletedMessagesFromNotifications$8(this.f$1);
    }
}