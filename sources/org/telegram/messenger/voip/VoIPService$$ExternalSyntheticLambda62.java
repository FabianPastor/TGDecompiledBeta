package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda62 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda62 INSTANCE = new VoIPService$$ExternalSyntheticLambda62();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda62() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
