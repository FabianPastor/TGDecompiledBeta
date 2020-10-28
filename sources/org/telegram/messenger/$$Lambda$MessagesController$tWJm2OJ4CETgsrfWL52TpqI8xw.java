package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$tWJm2OJ4CETgsrf-WL52TpqI8xw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$tWJm2OJ4CETgsrfWL52TpqI8xw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$tWJm2OJ4CETgsrfWL52TpqI8xw INSTANCE = new $$Lambda$MessagesController$tWJm2OJ4CETgsrfWL52TpqI8xw();

    private /* synthetic */ $$Lambda$MessagesController$tWJm2OJ4CETgsrfWL52TpqI8xw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$272(tLObject, tLRPC$TL_error);
    }
}
