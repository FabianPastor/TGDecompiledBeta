package org.telegram.ui.Delegates;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteImporter;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_hideChatJoinRequest;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MemberRequestsDelegate$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ MemberRequestsDelegate f$0;
    public final /* synthetic */ TLRPC$TL_chatInviteImporter f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ TLRPC$TL_messages_hideChatJoinRequest f$4;

    public /* synthetic */ MemberRequestsDelegate$$ExternalSyntheticLambda7(MemberRequestsDelegate memberRequestsDelegate, TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter, boolean z, TLRPC$User tLRPC$User, TLRPC$TL_messages_hideChatJoinRequest tLRPC$TL_messages_hideChatJoinRequest) {
        this.f$0 = memberRequestsDelegate;
        this.f$1 = tLRPC$TL_chatInviteImporter;
        this.f$2 = z;
        this.f$3 = tLRPC$User;
        this.f$4 = tLRPC$TL_messages_hideChatJoinRequest;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$hideChatJoinRequest$7(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}