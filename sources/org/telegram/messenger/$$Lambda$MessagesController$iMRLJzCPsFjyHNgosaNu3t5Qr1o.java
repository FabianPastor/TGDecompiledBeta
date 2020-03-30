package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$iMRLJzCPsFjyHNgosaNu3t5Qr1o  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$iMRLJzCPsFjyHNgosaNu3t5Qr1o implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$iMRLJzCPsFjyHNgosaNu3t5Qr1o INSTANCE = new $$Lambda$MessagesController$iMRLJzCPsFjyHNgosaNu3t5Qr1o();

    private /* synthetic */ $$Lambda$MessagesController$iMRLJzCPsFjyHNgosaNu3t5Qr1o() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$174(tLObject, tLRPC$TL_error);
    }
}
