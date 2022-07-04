package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda63 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda63 INSTANCE = new VoIPService$$ExternalSyntheticLambda63();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda63() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
