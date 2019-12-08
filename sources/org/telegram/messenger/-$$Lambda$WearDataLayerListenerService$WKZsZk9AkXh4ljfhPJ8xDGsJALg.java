package org.telegram.messenger;

import com.google.android.gms.wearable.MessageEvent;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearDataLayerListenerService$WKZsZk9AkXh4ljfhPJ8xDGsJALg implements Runnable {
    private final /* synthetic */ MessageEvent f$0;

    public /* synthetic */ -$$Lambda$WearDataLayerListenerService$WKZsZk9AkXh4ljfhPJ8xDGsJALg(MessageEvent messageEvent) {
        this.f$0 = messageEvent;
    }

    public final void run() {
        WearDataLayerListenerService.lambda$onMessageReceived$6(this.f$0);
    }
}
