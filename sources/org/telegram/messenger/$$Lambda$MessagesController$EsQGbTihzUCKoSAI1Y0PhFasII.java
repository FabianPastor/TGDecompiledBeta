package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$EsQGbTih-zUCKoSAI1Y0PhFasII  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$EsQGbTihzUCKoSAI1Y0PhFasII implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$EsQGbTihzUCKoSAI1Y0PhFasII INSTANCE = new $$Lambda$MessagesController$EsQGbTihzUCKoSAI1Y0PhFasII();

    private /* synthetic */ $$Lambda$MessagesController$EsQGbTihzUCKoSAI1Y0PhFasII() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$172(tLObject, tLRPC$TL_error);
    }
}
