package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

final /* synthetic */ class ConnectionsManager$$Lambda$7 implements Runnable {
    static final Runnable $instance = new ConnectionsManager$$Lambda$7();

    private ConnectionsManager$$Lambda$7() {
    }

    public void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(3));
    }
}
