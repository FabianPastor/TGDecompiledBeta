package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$EEUfZv3JIlFZfbu9HnSCeH3AbKU implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$EEUfZv3JIlFZfbu9HnSCeH3AbKU INSTANCE = new -$$Lambda$MessagesController$EEUfZv3JIlFZfbu9HnSCeH3AbKU();

    private /* synthetic */ -$$Lambda$MessagesController$EEUfZv3JIlFZfbu9HnSCeH3AbKU() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$255(tLObject, tL_error);
    }
}
