package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$f7p8vB7s3kY9elcXh5IjeVeDan4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$f7p8vB7s3kY9elcXh5IjeVeDan4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$f7p8vB7s3kY9elcXh5IjeVeDan4 INSTANCE = new $$Lambda$MessagesController$f7p8vB7s3kY9elcXh5IjeVeDan4();

    private /* synthetic */ $$Lambda$MessagesController$f7p8vB7s3kY9elcXh5IjeVeDan4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$172(tLObject, tLRPC$TL_error);
    }
}
