package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$r40aIljP9XXEZHDSJKpMdZBFBYA implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$r40aIljP9XXEZHDSJKpMdZBFBYA INSTANCE = new -$$Lambda$MessagesController$r40aIljP9XXEZHDSJKpMdZBFBYA();

    private /* synthetic */ -$$Lambda$MessagesController$r40aIljP9XXEZHDSJKpMdZBFBYA() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$35(tLObject, tL_error);
    }
}