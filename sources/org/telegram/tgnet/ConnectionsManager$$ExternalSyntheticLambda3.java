package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda3 implements Runnable {
    public static final /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda3 INSTANCE = new ConnectionsManager$$ExternalSyntheticLambda3();

    private /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda3() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
