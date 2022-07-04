package org.telegram.ui.Delegates;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MemberRequestsDelegate$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ MemberRequestsDelegate f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteImporter f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ TLRPC.TL_messages_hideChatJoinRequest f$4;

    public /* synthetic */ MemberRequestsDelegate$$ExternalSyntheticLambda7(MemberRequestsDelegate memberRequestsDelegate, TLRPC.TL_chatInviteImporter tL_chatInviteImporter, boolean z, TLRPC.User user, TLRPC.TL_messages_hideChatJoinRequest tL_messages_hideChatJoinRequest) {
        this.f$0 = memberRequestsDelegate;
        this.f$1 = tL_chatInviteImporter;
        this.f$2 = z;
        this.f$3 = user;
        this.f$4 = tL_messages_hideChatJoinRequest;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1629xa1bba2bb(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
