package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$sAwCUX3IhR8N0C-EEhE4RmQefts  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$sAwCUX3IhR8N0CEEhE4RmQefts implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$sAwCUX3IhR8N0CEEhE4RmQefts INSTANCE = new $$Lambda$MessagesController$sAwCUX3IhR8N0CEEhE4RmQefts();

    private /* synthetic */ $$Lambda$MessagesController$sAwCUX3IhR8N0CEEhE4RmQefts() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$deleteUserPhoto$68(tLObject, tL_error);
    }
}
