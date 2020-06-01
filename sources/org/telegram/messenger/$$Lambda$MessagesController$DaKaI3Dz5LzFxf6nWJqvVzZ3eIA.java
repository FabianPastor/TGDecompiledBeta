package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$DaKaI3Dz5LzFxf6nWJqvVzZ3eIA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$DaKaI3Dz5LzFxf6nWJqvVzZ3eIA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$DaKaI3Dz5LzFxf6nWJqvVzZ3eIA INSTANCE = new $$Lambda$MessagesController$DaKaI3Dz5LzFxf6nWJqvVzZ3eIA();

    private /* synthetic */ $$Lambda$MessagesController$DaKaI3Dz5LzFxf6nWJqvVzZ3eIA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$266(tLObject, tLRPC$TL_error);
    }
}
