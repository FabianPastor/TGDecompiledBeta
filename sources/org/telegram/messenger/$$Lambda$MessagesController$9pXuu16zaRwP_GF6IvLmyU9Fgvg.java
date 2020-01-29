package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg INSTANCE = new $$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg();

    private /* synthetic */ $$Lambda$MessagesController$9pXuu16zaRwP_GF6IvLmyU9Fgvg() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$blockUser$47(tLObject, tL_error);
    }
}
