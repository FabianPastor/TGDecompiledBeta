package org.telegram.messenger;

final /* synthetic */ class NotificationsController$$Lambda$3 implements Runnable {
    private final NotificationsController arg$1;
    private final int arg$2;

    NotificationsController$$Lambda$3(NotificationsController notificationsController, int i) {
        this.arg$1 = notificationsController;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$setLastOnlineFromOtherDevice$3$NotificationsController(this.arg$2);
    }
}
