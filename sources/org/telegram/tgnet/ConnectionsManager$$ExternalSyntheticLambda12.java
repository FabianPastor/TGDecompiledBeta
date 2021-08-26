package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda12 implements Runnable {
    public static final /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda12 INSTANCE = new ConnectionsManager$$ExternalSyntheticLambda12();

    private /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda12() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
