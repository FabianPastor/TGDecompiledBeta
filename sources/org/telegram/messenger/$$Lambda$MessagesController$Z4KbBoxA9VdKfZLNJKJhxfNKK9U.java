package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Z4KbBoxA9VdKfZLNJKJhxfNKK9U  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Z4KbBoxA9VdKfZLNJKJhxfNKK9U implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Z4KbBoxA9VdKfZLNJKJhxfNKK9U INSTANCE = new $$Lambda$MessagesController$Z4KbBoxA9VdKfZLNJKJhxfNKK9U();

    private /* synthetic */ $$Lambda$MessagesController$Z4KbBoxA9VdKfZLNJKJhxfNKK9U() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$186(tLObject, tLRPC$TL_error);
    }
}
