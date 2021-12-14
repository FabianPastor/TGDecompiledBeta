package org.telegram.messenger;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda38 implements Runnable {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda38 INSTANCE = new NotificationsController$$ExternalSyntheticLambda38();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda38() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
