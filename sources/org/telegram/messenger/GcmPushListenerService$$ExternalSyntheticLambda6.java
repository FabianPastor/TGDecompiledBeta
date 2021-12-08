package org.telegram.messenger;

import java.util.Map;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ GcmPushListenerService f$0;
    public final /* synthetic */ Map f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda6(GcmPushListenerService gcmPushListenerService, Map map, long j) {
        this.f$0 = gcmPushListenerService;
        this.f$1 = map;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$onMessageReceived$4(this.f$1, this.f$2);
    }
}
