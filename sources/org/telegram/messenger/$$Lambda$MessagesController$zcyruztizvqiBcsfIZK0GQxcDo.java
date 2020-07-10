package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$zcyruztizvqiBc-sfIZK0GQxcDo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$zcyruztizvqiBcsfIZK0GQxcDo implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$zcyruztizvqiBcsfIZK0GQxcDo INSTANCE = new $$Lambda$MessagesController$zcyruztizvqiBcsfIZK0GQxcDo();

    private /* synthetic */ $$Lambda$MessagesController$zcyruztizvqiBcsfIZK0GQxcDo() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$215(tLObject, tLRPC$TL_error);
    }
}
