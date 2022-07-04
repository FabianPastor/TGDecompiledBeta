package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.ChatFull f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda36(GroupCallActivity groupCallActivity, TLObject tLObject, TLRPC.ChatFull chatFull, boolean z) {
        this.f$0 = groupCallActivity;
        this.f$1 = tLObject;
        this.f$2 = chatFull;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m3502lambda$getLink$38$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3);
    }
}
