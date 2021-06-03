package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$IKnzf8sxArrwrmzKnrr4TwkiCqw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$IKnzf8sxArrwrmzKnrr4TwkiCqw implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$IKnzf8sxArrwrmzKnrr4TwkiCqw INSTANCE = new $$Lambda$VoIPService$IKnzf8sxArrwrmzKnrr4TwkiCqw();

    private /* synthetic */ $$Lambda$VoIPService$IKnzf8sxArrwrmzKnrr4TwkiCqw() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
