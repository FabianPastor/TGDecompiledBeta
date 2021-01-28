package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$SecretChatHelper$zQzZ1UYItYsNZWpU-qG3pche4Mk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SecretChatHelper$zQzZ1UYItYsNZWpUqG3pche4Mk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$SecretChatHelper$zQzZ1UYItYsNZWpUqG3pche4Mk INSTANCE = new $$Lambda$SecretChatHelper$zQzZ1UYItYsNZWpUqG3pche4Mk();

    private /* synthetic */ $$Lambda$SecretChatHelper$zQzZ1UYItYsNZWpUqG3pche4Mk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SecretChatHelper.lambda$declineSecretChat$20(tLObject, tLRPC$TL_error);
    }
}
