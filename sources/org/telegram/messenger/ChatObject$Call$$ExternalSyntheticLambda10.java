package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_phone_getGroupParticipants f$3;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda10(ChatObject.Call call, boolean z, TLObject tLObject, TLRPC.TL_phone_getGroupParticipants tL_phone_getGroupParticipants) {
        this.f$0 = call;
        this.f$1 = z;
        this.f$2 = tLObject;
        this.f$3 = tL_phone_getGroupParticipants;
    }

    public final void run() {
        this.f$0.m603lambda$loadMembers$1$orgtelegrammessengerChatObject$Call(this.f$1, this.f$2, this.f$3);
    }
}
