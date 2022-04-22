package org.telegram.messenger;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ NotificationsController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ NotificationsController$$ExternalSyntheticLambda16(NotificationsController notificationsController, int i) {
        this.f$0 = notificationsController;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$processLoadedUnreadMessages$22(this.f$1);
    }
}
