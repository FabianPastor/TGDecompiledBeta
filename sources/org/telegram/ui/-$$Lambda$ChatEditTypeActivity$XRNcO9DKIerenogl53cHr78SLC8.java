package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditTypeActivity$XRNcO9DKIerenogl53cHr78SLC8 implements RequestDelegate {
    private final /* synthetic */ ChatEditTypeActivity f$0;

    public /* synthetic */ -$$Lambda$ChatEditTypeActivity$XRNcO9DKIerenogl53cHr78SLC8(ChatEditTypeActivity chatEditTypeActivity) {
        this.f$0 = chatEditTypeActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onFragmentCreate$1$ChatEditTypeActivity(tLObject, tL_error);
    }
}
