package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8 implements Runnable {
    public static final /* synthetic */ $$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8 INSTANCE = new $$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8();

    private /* synthetic */ $$Lambda$NotificationsController$2v2nyML5dTCxIdrQrE6xmPJzze8() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
