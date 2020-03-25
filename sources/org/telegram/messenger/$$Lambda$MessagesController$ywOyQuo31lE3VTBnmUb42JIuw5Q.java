package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ywOyQuo31lE3VTBnmUb42JIuw5Q  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ywOyQuo31lE3VTBnmUb42JIuw5Q implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ywOyQuo31lE3VTBnmUb42JIuw5Q INSTANCE = new $$Lambda$MessagesController$ywOyQuo31lE3VTBnmUb42JIuw5Q();

    private /* synthetic */ $$Lambda$MessagesController$ywOyQuo31lE3VTBnmUb42JIuw5Q() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePeerSettingsBar$43(tLObject, tLRPC$TL_error);
    }
}
