package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$hfgOj_OFf7yi5pvQ34yapf-YPwA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$hfgOj_OFf7yi5pvQ34yapfYPwA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$hfgOj_OFf7yi5pvQ34yapfYPwA INSTANCE = new $$Lambda$MessagesController$hfgOj_OFf7yi5pvQ34yapfYPwA();

    private /* synthetic */ $$Lambda$MessagesController$hfgOj_OFf7yi5pvQ34yapfYPwA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$78(tLObject, tLRPC$TL_error);
    }
}
