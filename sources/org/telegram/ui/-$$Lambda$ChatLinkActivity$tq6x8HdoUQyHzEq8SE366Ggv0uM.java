package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatLinkActivity$tq6x8HdoUQyHzEq8SE366Ggv0uM implements RequestDelegate {
    private final /* synthetic */ ChatLinkActivity f$0;

    public /* synthetic */ -$$Lambda$ChatLinkActivity$tq6x8HdoUQyHzEq8SE366Ggv0uM(ChatLinkActivity chatLinkActivity) {
        this.f$0 = chatLinkActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChats$15$ChatLinkActivity(tLObject, tL_error);
    }
}
