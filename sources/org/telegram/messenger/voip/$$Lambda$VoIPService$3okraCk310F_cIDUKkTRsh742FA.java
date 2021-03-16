package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$3okraCk310F_cIDUKkTRsh742FA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$3okraCk310F_cIDUKkTRsh742FA implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$3okraCk310F_cIDUKkTRsh742FA INSTANCE = new $$Lambda$VoIPService$3okraCk310F_cIDUKkTRsh742FA();

    private /* synthetic */ $$Lambda$VoIPService$3okraCk310F_cIDUKkTRsh742FA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
