package org.telegram.messenger;

final /* synthetic */ class NotificationsController$$Lambda$12 implements Runnable {
    static final Runnable $instance = new NotificationsController$$Lambda$12();

    private NotificationsController$$Lambda$12() {
    }

    public void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
