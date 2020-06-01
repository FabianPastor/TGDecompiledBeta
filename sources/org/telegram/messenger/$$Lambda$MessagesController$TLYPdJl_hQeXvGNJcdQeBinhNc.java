package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$TLYPdJl_hQeXv-GNJcdQeBinhNc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$TLYPdJl_hQeXvGNJcdQeBinhNc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$TLYPdJl_hQeXvGNJcdQeBinhNc INSTANCE = new $$Lambda$MessagesController$TLYPdJl_hQeXvGNJcdQeBinhNc();

    private /* synthetic */ $$Lambda$MessagesController$TLYPdJl_hQeXvGNJcdQeBinhNc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$175(tLObject, tLRPC$TL_error);
    }
}
