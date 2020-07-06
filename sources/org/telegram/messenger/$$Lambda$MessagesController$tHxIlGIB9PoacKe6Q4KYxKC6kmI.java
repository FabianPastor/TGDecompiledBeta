package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$tHxIlGIB9PoacKe6Q4KYxKC6kmI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$tHxIlGIB9PoacKe6Q4KYxKC6kmI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$tHxIlGIB9PoacKe6Q4KYxKC6kmI INSTANCE = new $$Lambda$MessagesController$tHxIlGIB9PoacKe6Q4KYxKC6kmI();

    private /* synthetic */ $$Lambda$MessagesController$tHxIlGIB9PoacKe6Q4KYxKC6kmI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePeerSettingsBar$44(tLObject, tLRPC$TL_error);
    }
}
