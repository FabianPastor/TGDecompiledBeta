package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$bvTG3hiDO9NIY_T8Eh-QGUgNzpE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$bvTG3hiDO9NIY_T8EhQGUgNzpE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$bvTG3hiDO9NIY_T8EhQGUgNzpE INSTANCE = new $$Lambda$MessagesController$bvTG3hiDO9NIY_T8EhQGUgNzpE();

    private /* synthetic */ $$Lambda$MessagesController$bvTG3hiDO9NIY_T8EhQGUgNzpE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$184(tLObject, tLRPC$TL_error);
    }
}
