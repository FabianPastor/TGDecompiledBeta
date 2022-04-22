package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda60 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda60 INSTANCE = new VoIPService$$ExternalSyntheticLambda60();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda60() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
