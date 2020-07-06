package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ayiRvoQqbB0TdI2CLASSNAMEVwNVOZw-U  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ayiRvoQqbB0TdI2CLASSNAMEVwNVOZwU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ayiRvoQqbB0TdI2CLASSNAMEVwNVOZwU INSTANCE = new $$Lambda$MessagesController$ayiRvoQqbB0TdI2CLASSNAMEVwNVOZwU();

    private /* synthetic */ $$Lambda$MessagesController$ayiRvoQqbB0TdI2CLASSNAMEVwNVOZwU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$171(tLObject, tLRPC$TL_error);
    }
}
