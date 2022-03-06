package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ TLRPC$InputPeer f$2;
    public final /* synthetic */ TLRPC$TL_updateGroupCall f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda42(GroupCallActivity groupCallActivity, TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
        this.f$0 = groupCallActivity;
        this.f$1 = tLRPC$Chat;
        this.f$2 = tLRPC$InputPeer;
        this.f$3 = tLRPC$TL_updateGroupCall;
    }

    public final void run() {
        this.f$0.lambda$new$26(this.f$1, this.f$2, this.f$3);
    }
}
