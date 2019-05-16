package org.telegram.messenger;

import java.util.Map;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GcmPushListenerService$nHUi11ZwPF6EFepQ1zn-R3cweVE implements Runnable {
    private final /* synthetic */ GcmPushListenerService f$0;
    private final /* synthetic */ Map f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$GcmPushListenerService$nHUi11ZwPF6EFepQ1zn-R3cweVE(GcmPushListenerService gcmPushListenerService, Map map, long j) {
        this.f$0 = gcmPushListenerService;
        this.f$1 = map;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$onMessageReceived$4$GcmPushListenerService(this.f$1, this.f$2);
    }
}
