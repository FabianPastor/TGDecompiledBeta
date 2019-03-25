package org.telegram.messenger;

final /* synthetic */ class NotificationsController$$Lambda$14 implements Runnable {
    static final Runnable $instance = new NotificationsController$$Lambda$14();

    private NotificationsController$$Lambda$14() {
    }

    public void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
