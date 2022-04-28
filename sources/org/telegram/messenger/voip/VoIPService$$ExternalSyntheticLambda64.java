package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda64 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda64 INSTANCE = new VoIPService$$ExternalSyntheticLambda64();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda64() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
