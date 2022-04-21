package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda58 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda58 INSTANCE = new VoIPService$$ExternalSyntheticLambda58();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda58() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
