package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$zok0yI-wqeFLTw16KNL9oBwLj7k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$zok0yIwqeFLTw16KNL9oBwLj7k implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$zok0yIwqeFLTw16KNL9oBwLj7k INSTANCE = new $$Lambda$MessagesController$zok0yIwqeFLTw16KNL9oBwLj7k();

    private /* synthetic */ $$Lambda$MessagesController$zok0yIwqeFLTw16KNL9oBwLj7k() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$50(tLObject, tLRPC$TL_error);
    }
}
