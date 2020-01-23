package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$SRWTDwvobt7Z1JbdFWo76GStVu8 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$SRWTDwvobt7Z1JbdFWo76GStVu8 INSTANCE = new -$$Lambda$MessagesController$SRWTDwvobt7Z1JbdFWo76GStVu8();

    private /* synthetic */ -$$Lambda$MessagesController$SRWTDwvobt7Z1JbdFWo76GStVu8() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$completeReadTask$164(tLObject, tL_error);
    }
}
