package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$lnX7i9eGTDuJovXhCLpz1QuqmLE implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$lnX7i9eGTDuJovXhCLpz1QuqmLE INSTANCE = new -$$Lambda$MessagesController$lnX7i9eGTDuJovXhCLpz1QuqmLE();

    private /* synthetic */ -$$Lambda$MessagesController$lnX7i9eGTDuJovXhCLpz1QuqmLE() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$deleteUserPhoto$58(tLObject, tL_error);
    }
}
