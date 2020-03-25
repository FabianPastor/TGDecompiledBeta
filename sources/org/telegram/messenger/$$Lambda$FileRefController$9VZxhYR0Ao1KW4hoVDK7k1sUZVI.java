package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI INSTANCE = new $$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI();

    private /* synthetic */ $$Lambda$FileRefController$9VZxhYR0Ao1KW4hoVDK7k1sUZVI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$24(tLObject, tLRPC$TL_error);
    }
}
