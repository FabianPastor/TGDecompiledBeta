package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$QPO74HqlwjG4J6suVSh5BheLJLM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$QPO74HqlwjG4J6suVSh5BheLJLM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$NotificationsController$QPO74HqlwjG4J6suVSh5BheLJLM INSTANCE = new $$Lambda$NotificationsController$QPO74HqlwjG4J6suVSh5BheLJLM();

    private /* synthetic */ $$Lambda$NotificationsController$QPO74HqlwjG4J6suVSh5BheLJLM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$38(tLObject, tLRPC$TL_error);
    }
}
