package org.telegram.ui.Delegates;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteImporter;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_hideChatJoinRequest;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MemberRequestsDelegate$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ MemberRequestsDelegate f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_chatInviteImporter f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLRPC$User f$5;
    public final /* synthetic */ TLRPC$TL_messages_hideChatJoinRequest f$6;

    public /* synthetic */ MemberRequestsDelegate$$ExternalSyntheticLambda3(MemberRequestsDelegate memberRequestsDelegate, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter, boolean z, TLRPC$User tLRPC$User, TLRPC$TL_messages_hideChatJoinRequest tLRPC$TL_messages_hideChatJoinRequest) {
        this.f$0 = memberRequestsDelegate;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_chatInviteImporter;
        this.f$4 = z;
        this.f$5 = tLRPC$User;
        this.f$6 = tLRPC$TL_messages_hideChatJoinRequest;
    }

    public final void run() {
        this.f$0.lambda$hideChatJoinRequest$6(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
