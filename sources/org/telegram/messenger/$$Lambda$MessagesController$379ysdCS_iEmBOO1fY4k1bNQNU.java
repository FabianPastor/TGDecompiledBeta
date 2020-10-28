package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$379ysdCS_iE-mBOO1fY4k1bNQNU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$379ysdCS_iEmBOO1fY4k1bNQNU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$379ysdCS_iEmBOO1fY4k1bNQNU INSTANCE = new $$Lambda$MessagesController$379ysdCS_iEmBOO1fY4k1bNQNU();

    private /* synthetic */ $$Lambda$MessagesController$379ysdCS_iEmBOO1fY4k1bNQNU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$180(tLObject, tLRPC$TL_error);
    }
}
