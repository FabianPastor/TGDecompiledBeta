package org.telegram.messenger;

import com.google.android.gms.wearable.MessageEvent;

public final /* synthetic */ class WearDataLayerListenerService$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MessageEvent f$0;

    public /* synthetic */ WearDataLayerListenerService$$ExternalSyntheticLambda2(MessageEvent messageEvent) {
        this.f$0 = messageEvent;
    }

    public final void run() {
        WearDataLayerListenerService.lambda$onMessageReceived$6(this.f$0);
    }
}
