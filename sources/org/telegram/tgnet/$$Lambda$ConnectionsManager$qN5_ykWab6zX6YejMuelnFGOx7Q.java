package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.tgnet.-$$Lambda$ConnectionsManager$qN5_ykWab6zX6YejMuelnFGOx7Q  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ConnectionsManager$qN5_ykWab6zX6YejMuelnFGOx7Q implements Runnable {
    public static final /* synthetic */ $$Lambda$ConnectionsManager$qN5_ykWab6zX6YejMuelnFGOx7Q INSTANCE = new $$Lambda$ConnectionsManager$qN5_ykWab6zX6YejMuelnFGOx7Q();

    private /* synthetic */ $$Lambda$ConnectionsManager$qN5_ykWab6zX6YejMuelnFGOx7Q() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
