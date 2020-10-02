package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$JtTpuvKrWXWVjGH3jhd-0AsVCAI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$JtTpuvKrWXWVjGH3jhd0AsVCAI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$NotificationsController$JtTpuvKrWXWVjGH3jhd0AsVCAI INSTANCE = new $$Lambda$NotificationsController$JtTpuvKrWXWVjGH3jhd0AsVCAI();

    private /* synthetic */ $$Lambda$NotificationsController$JtTpuvKrWXWVjGH3jhd0AsVCAI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$39(tLObject, tLRPC$TL_error);
    }
}
