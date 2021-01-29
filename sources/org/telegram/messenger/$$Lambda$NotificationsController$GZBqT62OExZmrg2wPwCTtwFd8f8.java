package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$GZBqT62OExZmrg2wPwCTtwFd8f8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$GZBqT62OExZmrg2wPwCTtwFd8f8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$NotificationsController$GZBqT62OExZmrg2wPwCTtwFd8f8 INSTANCE = new $$Lambda$NotificationsController$GZBqT62OExZmrg2wPwCTtwFd8f8();

    private /* synthetic */ $$Lambda$NotificationsController$GZBqT62OExZmrg2wPwCTtwFd8f8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$40(tLObject, tLRPC$TL_error);
    }
}
