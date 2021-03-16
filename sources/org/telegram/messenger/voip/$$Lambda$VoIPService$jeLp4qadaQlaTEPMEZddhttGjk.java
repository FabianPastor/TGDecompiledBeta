package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$jeLp4qadaQlaTEPMEZddh-ttGjk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$jeLp4qadaQlaTEPMEZddhttGjk implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$jeLp4qadaQlaTEPMEZddhttGjk INSTANCE = new $$Lambda$VoIPService$jeLp4qadaQlaTEPMEZddhttGjk();

    private /* synthetic */ $$Lambda$VoIPService$jeLp4qadaQlaTEPMEZddhttGjk() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
