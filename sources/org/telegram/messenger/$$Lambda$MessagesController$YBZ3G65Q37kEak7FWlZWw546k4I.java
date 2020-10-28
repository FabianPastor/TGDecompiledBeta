package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$YBZ3G65Q37kEak7FWlZWw546k4I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$YBZ3G65Q37kEak7FWlZWw546k4I implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$YBZ3G65Q37kEak7FWlZWw546k4I INSTANCE = new $$Lambda$MessagesController$YBZ3G65Q37kEak7FWlZWw546k4I();

    private /* synthetic */ $$Lambda$MessagesController$YBZ3G65Q37kEak7FWlZWw546k4I() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$178(tLObject, tLRPC$TL_error);
    }
}
