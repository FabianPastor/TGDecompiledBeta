package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$pdj01wjZw5EQ-CTc6CLASSNAMEcEwFJiY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$pdj01wjZw5EQCTc6CLASSNAMEcEwFJiY implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$pdj01wjZw5EQCTc6CLASSNAMEcEwFJiY INSTANCE = new $$Lambda$VoIPService$pdj01wjZw5EQCTc6CLASSNAMEcEwFJiY();

    private /* synthetic */ $$Lambda$VoIPService$pdj01wjZw5EQCTc6CLASSNAMEcEwFJiY() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
