package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$gC7iaeGr_Tt3Akk8QzxZYRjn6Jk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$gC7iaeGr_Tt3Akk8QzxZYRjn6Jk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$LocationController$gC7iaeGr_Tt3Akk8QzxZYRjn6Jk INSTANCE = new $$Lambda$LocationController$gC7iaeGr_Tt3Akk8QzxZYRjn6Jk();

    private /* synthetic */ $$Lambda$LocationController$gC7iaeGr_Tt3Akk8QzxZYRjn6Jk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LocationController.lambda$broadcastLastKnownLocation$8(tLObject, tLRPC$TL_error);
    }
}
