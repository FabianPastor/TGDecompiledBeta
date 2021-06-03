package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$p5rx37KXvar_-UrUIybok9TWy6dM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$p5rx37KXvar_UrUIybok9TWy6dM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$p5rx37KXvar_UrUIybok9TWy6dM INSTANCE = new $$Lambda$MessagesController$p5rx37KXvar_UrUIybok9TWy6dM();

    private /* synthetic */ $$Lambda$MessagesController$p5rx37KXvar_UrUIybok9TWy6dM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$83(tLObject, tLRPC$TL_error);
    }
}
