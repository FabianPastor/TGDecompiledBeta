package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$y0viyyzHAD9iq9L5OFqN7-FTVtM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$y0viyyzHAD9iq9L5OFqN7FTVtM implements Runnable {
    public static final /* synthetic */ $$Lambda$NotificationsController$y0viyyzHAD9iq9L5OFqN7FTVtM INSTANCE = new $$Lambda$NotificationsController$y0viyyzHAD9iq9L5OFqN7FTVtM();

    private /* synthetic */ $$Lambda$NotificationsController$y0viyyzHAD9iq9L5OFqN7FTVtM() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
