package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$vkVpTZUT50qaMcRd27xbNahN3bY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$vkVpTZUT50qaMcRd27xbNahN3bY implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$vkVpTZUT50qaMcRd27xbNahN3bY INSTANCE = new $$Lambda$VoIPService$vkVpTZUT50qaMcRd27xbNahN3bY();

    private /* synthetic */ $$Lambda$VoIPService$vkVpTZUT50qaMcRd27xbNahN3bY() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
