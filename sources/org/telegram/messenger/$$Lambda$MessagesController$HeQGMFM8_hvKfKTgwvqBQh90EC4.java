package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$HeQGMFM8_hvKfKTgwvqBQh90EC4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$HeQGMFM8_hvKfKTgwvqBQh90EC4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$HeQGMFM8_hvKfKTgwvqBQh90EC4 INSTANCE = new $$Lambda$MessagesController$HeQGMFM8_hvKfKTgwvqBQh90EC4();

    private /* synthetic */ $$Lambda$MessagesController$HeQGMFM8_hvKfKTgwvqBQh90EC4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$277(tLObject, tLRPC$TL_error);
    }
}
