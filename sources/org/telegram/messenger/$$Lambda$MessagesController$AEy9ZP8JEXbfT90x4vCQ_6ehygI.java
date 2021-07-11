package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$AEy9ZP8JEXbfT90x4vCQ_6ehygI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$AEy9ZP8JEXbfT90x4vCQ_6ehygI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$AEy9ZP8JEXbfT90x4vCQ_6ehygI INSTANCE = new $$Lambda$MessagesController$AEy9ZP8JEXbfT90x4vCQ_6ehygI();

    private /* synthetic */ $$Lambda$MessagesController$AEy9ZP8JEXbfT90x4vCQ_6ehygI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteParticipantFromChat$227(tLObject, tLRPC$TL_error);
    }
}
