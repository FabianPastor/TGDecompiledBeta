package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$BghCany7PLgDuaTecRitmA13mXA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$BghCany7PLgDuaTecRitmA13mXA implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$BghCany7PLgDuaTecRitmA13mXA INSTANCE = new $$Lambda$VoIPService$BghCany7PLgDuaTecRitmA13mXA();

    private /* synthetic */ $$Lambda$VoIPService$BghCany7PLgDuaTecRitmA13mXA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
