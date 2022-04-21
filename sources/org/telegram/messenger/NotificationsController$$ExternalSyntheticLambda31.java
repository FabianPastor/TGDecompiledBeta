package org.telegram.messenger;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda31 implements Runnable {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda31 INSTANCE = new NotificationsController$$ExternalSyntheticLambda31();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda31() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
