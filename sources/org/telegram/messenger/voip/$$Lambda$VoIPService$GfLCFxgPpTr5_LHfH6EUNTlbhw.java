package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$GfLCFxg-PpTr5_LHfH6EUNTlbhw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$GfLCFxgPpTr5_LHfH6EUNTlbhw implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$GfLCFxgPpTr5_LHfH6EUNTlbhw INSTANCE = new $$Lambda$VoIPService$GfLCFxgPpTr5_LHfH6EUNTlbhw();

    private /* synthetic */ $$Lambda$VoIPService$GfLCFxgPpTr5_LHfH6EUNTlbhw() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
