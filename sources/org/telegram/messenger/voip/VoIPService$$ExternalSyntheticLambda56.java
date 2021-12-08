package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda56 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda56 INSTANCE = new VoIPService$$ExternalSyntheticLambda56();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda56() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
