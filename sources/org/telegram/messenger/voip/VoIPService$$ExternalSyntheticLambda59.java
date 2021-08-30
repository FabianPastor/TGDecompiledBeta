package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda59 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda59 INSTANCE = new VoIPService$$ExternalSyntheticLambda59();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda59() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
