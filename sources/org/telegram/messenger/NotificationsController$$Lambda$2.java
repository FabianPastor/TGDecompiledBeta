package org.telegram.messenger;

final /* synthetic */ class NotificationsController$$Lambda$2 implements Runnable {
    private final NotificationsController arg$1;
    private final long arg$2;

    NotificationsController$$Lambda$2(NotificationsController notificationsController, long j) {
        this.arg$1 = notificationsController;
        this.arg$2 = j;
    }

    public void run() {
        this.arg$1.lambda$setOpenedDialogId$2$NotificationsController(this.arg$2);
    }
}
