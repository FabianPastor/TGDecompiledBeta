package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$IrwtgwjJOMXYm5EJBFX56NfTh68  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$IrwtgwjJOMXYm5EJBFX56NfTh68 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$IrwtgwjJOMXYm5EJBFX56NfTh68 INSTANCE = new $$Lambda$MessagesController$IrwtgwjJOMXYm5EJBFX56NfTh68();

    private /* synthetic */ $$Lambda$MessagesController$IrwtgwjJOMXYm5EJBFX56NfTh68() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$49(tLObject, tLRPC$TL_error);
    }
}
