package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_phone_getGroupParticipants;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_phone_getGroupParticipants f$3;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda6(ChatObject.Call call, boolean z, TLObject tLObject, TLRPC$TL_phone_getGroupParticipants tLRPC$TL_phone_getGroupParticipants) {
        this.f$0 = call;
        this.f$1 = z;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_phone_getGroupParticipants;
    }

    public final void run() {
        this.f$0.lambda$loadMembers$1(this.f$1, this.f$2, this.f$3);
    }
}
