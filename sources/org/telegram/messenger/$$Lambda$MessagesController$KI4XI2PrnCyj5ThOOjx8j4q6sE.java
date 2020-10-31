package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$KI4XI2PrnCyj5ThOOjx8j4-q6sE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$KI4XI2PrnCyj5ThOOjx8j4q6sE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$KI4XI2PrnCyj5ThOOjx8j4q6sE INSTANCE = new $$Lambda$MessagesController$KI4XI2PrnCyj5ThOOjx8j4q6sE();

    private /* synthetic */ $$Lambda$MessagesController$KI4XI2PrnCyj5ThOOjx8j4q6sE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$273(tLObject, tLRPC$TL_error);
    }
}
