package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda51 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ TLRPC$InputPeer f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda51(GroupCallActivity groupCallActivity, TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer) {
        this.f$0 = groupCallActivity;
        this.f$1 = tLRPC$Chat;
        this.f$2 = tLRPC$InputPeer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$28(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
