package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$rKBU1BCAYAmYgXeSuqfz2cb23AY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$rKBU1BCAYAmYgXeSuqfz2cb23AY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$rKBU1BCAYAmYgXeSuqfz2cb23AY INSTANCE = new $$Lambda$MessagesController$rKBU1BCAYAmYgXeSuqfz2cb23AY();

    private /* synthetic */ $$Lambda$MessagesController$rKBU1BCAYAmYgXeSuqfz2cb23AY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$81(tLObject, tLRPC$TL_error);
    }
}