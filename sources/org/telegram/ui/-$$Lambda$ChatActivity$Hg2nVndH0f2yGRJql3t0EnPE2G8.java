package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$Hg2nVndH0f2yGRJql3t0EnPE2G8 implements RequestDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$Hg2nVndH0f2yGRJql3t0EnPE2G8(ChatActivity chatActivity, String str) {
        this.f$0 = chatActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$89$ChatActivity(this.f$1, tLObject, tL_error);
    }
}
