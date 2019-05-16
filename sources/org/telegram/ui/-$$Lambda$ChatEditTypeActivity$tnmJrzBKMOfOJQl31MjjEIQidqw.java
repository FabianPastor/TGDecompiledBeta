package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditTypeActivity$tnmJrzBKMOfOJQl31MjjEIQidqw implements RequestDelegate {
    private final /* synthetic */ ChatEditTypeActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ChatEditTypeActivity$tnmJrzBKMOfOJQl31MjjEIQidqw(ChatEditTypeActivity chatEditTypeActivity, String str) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$18$ChatEditTypeActivity(this.f$1, tLObject, tL_error);
    }
}
