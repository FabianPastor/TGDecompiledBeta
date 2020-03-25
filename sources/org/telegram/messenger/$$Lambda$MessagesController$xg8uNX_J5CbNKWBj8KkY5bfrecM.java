package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$xg8uNX_J5CbNKWBj8KkY5bfrecM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$xg8uNX_J5CbNKWBj8KkY5bfrecM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$xg8uNX_J5CbNKWBj8KkY5bfrecM INSTANCE = new $$Lambda$MessagesController$xg8uNX_J5CbNKWBj8KkY5bfrecM();

    private /* synthetic */ $$Lambda$MessagesController$xg8uNX_J5CbNKWBj8KkY5bfrecM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$264(tLObject, tLRPC$TL_error);
    }
}
