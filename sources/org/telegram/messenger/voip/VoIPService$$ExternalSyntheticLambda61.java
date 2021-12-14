package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda61 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda61 INSTANCE = new VoIPService$$ExternalSyntheticLambda61();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda61() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
