package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPBaseService$_qecwLP1rF2teuQ2aqNnaKwsrZA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPBaseService$_qecwLP1rF2teuQ2aqNnaKwsrZA implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPBaseService$_qecwLP1rF2teuQ2aqNnaKwsrZA INSTANCE = new $$Lambda$VoIPBaseService$_qecwLP1rF2teuQ2aqNnaKwsrZA();

    private /* synthetic */ $$Lambda$VoIPBaseService$_qecwLP1rF2teuQ2aqNnaKwsrZA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
