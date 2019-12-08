package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$sAwCUX3IhR8N0C-EEhE4RmQefts implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$sAwCUX3IhR8N0C-EEhE4RmQefts INSTANCE = new -$$Lambda$MessagesController$sAwCUX3IhR8N0C-EEhE4RmQefts();

    private /* synthetic */ -$$Lambda$MessagesController$sAwCUX3IhR8N0C-EEhE4RmQefts() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$deleteUserPhoto$68(tLObject, tL_error);
    }
}
