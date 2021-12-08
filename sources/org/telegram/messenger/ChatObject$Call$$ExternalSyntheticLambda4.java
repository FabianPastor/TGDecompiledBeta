package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.TL_phone_getGroupParticipants f$2;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda4(ChatObject.Call call, boolean z, TLRPC.TL_phone_getGroupParticipants tL_phone_getGroupParticipants) {
        this.f$0 = call;
        this.f$1 = z;
        this.f$2 = tL_phone_getGroupParticipants;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m604lambda$loadMembers$2$orgtelegrammessengerChatObject$Call(this.f$1, this.f$2, tLObject, tL_error);
    }
}
