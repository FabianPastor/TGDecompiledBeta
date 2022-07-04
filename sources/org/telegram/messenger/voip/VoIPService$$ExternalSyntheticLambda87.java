package org.telegram.messenger.voip;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda87 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ MessagesStorage f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda87(VoIPService voIPService, MessagesStorage messagesStorage) {
        this.f$0 = voIPService;
        this.f$1 = messagesStorage;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2436x4CLASSNAME(this.f$1, tLObject, tL_error);
    }
}
