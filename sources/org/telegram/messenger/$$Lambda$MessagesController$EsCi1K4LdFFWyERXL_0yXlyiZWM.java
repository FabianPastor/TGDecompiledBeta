package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$EsCi1K4LdFFWyERXL_0yXlyiZWM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$EsCi1K4LdFFWyERXL_0yXlyiZWM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$EsCi1K4LdFFWyERXL_0yXlyiZWM INSTANCE = new $$Lambda$MessagesController$EsCi1K4LdFFWyERXL_0yXlyiZWM();

    private /* synthetic */ $$Lambda$MessagesController$EsCi1K4LdFFWyERXL_0yXlyiZWM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$181(tLObject, tLRPC$TL_error);
    }
}
