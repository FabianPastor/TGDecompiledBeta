package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$R1awVKLbB9o598GSUGqGt3-Snls  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$R1awVKLbB9o598GSUGqGt3Snls implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$R1awVKLbB9o598GSUGqGt3Snls INSTANCE = new $$Lambda$MessagesController$R1awVKLbB9o598GSUGqGt3Snls();

    private /* synthetic */ $$Lambda$MessagesController$R1awVKLbB9o598GSUGqGt3Snls() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$177(tLObject, tLRPC$TL_error);
    }
}
