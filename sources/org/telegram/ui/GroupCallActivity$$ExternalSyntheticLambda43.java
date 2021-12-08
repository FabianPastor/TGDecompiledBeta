package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda43 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ TLRPC.InputPeer f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda43(GroupCallActivity groupCallActivity, TLRPC.Chat chat, TLRPC.InputPeer inputPeer) {
        this.f$0 = groupCallActivity;
        this.f$1 = chat;
        this.f$2 = inputPeer;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2976lambda$new$28$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
