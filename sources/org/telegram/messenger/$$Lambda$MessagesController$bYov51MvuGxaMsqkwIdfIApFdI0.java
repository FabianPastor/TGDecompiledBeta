package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0 INSTANCE = new $$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0();

    private /* synthetic */ $$Lambda$MessagesController$bYov51MvuGxaMsqkwIdfIApFdI0() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$completeReadTask$163(tLObject, tL_error);
    }
}
