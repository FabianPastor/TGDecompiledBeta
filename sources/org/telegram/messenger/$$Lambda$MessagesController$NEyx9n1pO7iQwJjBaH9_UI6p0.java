package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$N-Eyx9n1pO7iQwJj-BaH9_UI6p0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$NEyx9n1pO7iQwJjBaH9_UI6p0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$NEyx9n1pO7iQwJjBaH9_UI6p0 INSTANCE = new $$Lambda$MessagesController$NEyx9n1pO7iQwJjBaH9_UI6p0();

    private /* synthetic */ $$Lambda$MessagesController$NEyx9n1pO7iQwJjBaH9_UI6p0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$48(tLObject, tLRPC$TL_error);
    }
}
