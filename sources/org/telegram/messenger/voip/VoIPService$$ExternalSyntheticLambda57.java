package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda57 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda57 INSTANCE = new VoIPService$$ExternalSyntheticLambda57();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda57() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
