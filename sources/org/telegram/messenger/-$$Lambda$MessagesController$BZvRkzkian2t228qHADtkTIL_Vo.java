package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$BZvRkzkian2t228qHADtkTIL_Vo implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$BZvRkzkian2t228qHADtkTIL_Vo INSTANCE = new -$$Lambda$MessagesController$BZvRkzkian2t228qHADtkTIL_Vo();

    private /* synthetic */ -$$Lambda$MessagesController$BZvRkzkian2t228qHADtkTIL_Vo() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$142(tLObject, tL_error);
    }
}
