package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$AoAm6B-NWOvi4eKtL3P3L8J-rsU implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$AoAm6B-NWOvi4eKtL3P3L8J-rsU INSTANCE = new -$$Lambda$MessagesController$AoAm6B-NWOvi4eKtL3P3L8J-rsU();

    private /* synthetic */ -$$Lambda$MessagesController$AoAm6B-NWOvi4eKtL3P3L8J-rsU() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$completeReadTask$151(tLObject, tL_error);
    }
}
