package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$Iii6Ysd4L9akc-d1WhGl6DiaJBA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$Iii6Ysd4L9akcd1WhGl6DiaJBA implements Runnable {
    public static final /* synthetic */ $$Lambda$NotificationsController$Iii6Ysd4L9akcd1WhGl6DiaJBA INSTANCE = new $$Lambda$NotificationsController$Iii6Ysd4L9akcd1WhGl6DiaJBA();

    private /* synthetic */ $$Lambda$NotificationsController$Iii6Ysd4L9akcd1WhGl6DiaJBA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
