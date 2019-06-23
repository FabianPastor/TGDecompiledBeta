package org.telegram.messenger;

import java.util.Map;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GcmPushListenerService$kOADpO1n0oEPuZdWbViq4zvdHPc implements Runnable {
    private final /* synthetic */ GcmPushListenerService f$0;
    private final /* synthetic */ Map f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$GcmPushListenerService$kOADpO1n0oEPuZdWbViq4zvdHPc(GcmPushListenerService gcmPushListenerService, Map map, long j) {
        this.f$0 = gcmPushListenerService;
        this.f$1 = map;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$onMessageReceived$3$GcmPushListenerService(this.f$1, this.f$2);
    }
}
