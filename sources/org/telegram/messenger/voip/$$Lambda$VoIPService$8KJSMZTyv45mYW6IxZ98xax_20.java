package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$8KJSMZTyv-45mYW6IxZ98xax_20  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$8KJSMZTyv45mYW6IxZ98xax_20 implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$8KJSMZTyv45mYW6IxZ98xax_20 INSTANCE = new $$Lambda$VoIPService$8KJSMZTyv45mYW6IxZ98xax_20();

    private /* synthetic */ $$Lambda$VoIPService$8KJSMZTyv45mYW6IxZ98xax_20() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
