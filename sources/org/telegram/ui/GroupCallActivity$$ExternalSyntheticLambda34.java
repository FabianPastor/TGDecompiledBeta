package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda34 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ TLRPC.InputPeer f$2;
    public final /* synthetic */ TLRPC.TL_updateGroupCall f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda34(GroupCallActivity groupCallActivity, TLRPC.Chat chat, TLRPC.InputPeer inputPeer, TLRPC.TL_updateGroupCall tL_updateGroupCall) {
        this.f$0 = groupCallActivity;
        this.f$1 = chat;
        this.f$2 = inputPeer;
        this.f$3 = tL_updateGroupCall;
    }

    public final void run() {
        this.f$0.m2974lambda$new$26$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3);
    }
}
