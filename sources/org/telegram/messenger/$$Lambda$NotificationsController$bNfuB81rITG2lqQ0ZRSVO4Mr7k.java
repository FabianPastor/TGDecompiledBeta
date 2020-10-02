package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$-bNfuB81rITG2lqQ0ZRSVO4Mr7k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$bNfuB81rITG2lqQ0ZRSVO4Mr7k implements Runnable {
    public static final /* synthetic */ $$Lambda$NotificationsController$bNfuB81rITG2lqQ0ZRSVO4Mr7k INSTANCE = new $$Lambda$NotificationsController$bNfuB81rITG2lqQ0ZRSVO4Mr7k();

    private /* synthetic */ $$Lambda$NotificationsController$bNfuB81rITG2lqQ0ZRSVO4Mr7k() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
