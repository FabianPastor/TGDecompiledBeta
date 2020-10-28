package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$DfGuf8-D5R_04a1N6iaLvkl6C4s  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$DfGuf8D5R_04a1N6iaLvkl6C4s implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$DfGuf8D5R_04a1N6iaLvkl6C4s INSTANCE = new $$Lambda$VoIPService$DfGuf8D5R_04a1N6iaLvkl6C4s();

    private /* synthetic */ $$Lambda$VoIPService$DfGuf8D5R_04a1N6iaLvkl6C4s() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
