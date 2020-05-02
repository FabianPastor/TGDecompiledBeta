package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$YWR0Cd5bxxVSkVBQgpfwdEYnlyg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$YWR0Cd5bxxVSkVBQgpfwdEYnlyg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$YWR0Cd5bxxVSkVBQgpfwdEYnlyg INSTANCE = new $$Lambda$MessagesController$YWR0Cd5bxxVSkVBQgpfwdEYnlyg();

    private /* synthetic */ $$Lambda$MessagesController$YWR0Cd5bxxVSkVBQgpfwdEYnlyg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$212(tLObject, tLRPC$TL_error);
    }
}
