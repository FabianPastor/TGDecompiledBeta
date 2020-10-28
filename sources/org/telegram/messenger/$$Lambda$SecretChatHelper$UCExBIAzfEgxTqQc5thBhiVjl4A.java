package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$SecretChatHelper$UCExBIAzfEgxTqQc5thBhiVjl4A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SecretChatHelper$UCExBIAzfEgxTqQc5thBhiVjl4A implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$SecretChatHelper$UCExBIAzfEgxTqQc5thBhiVjl4A INSTANCE = new $$Lambda$SecretChatHelper$UCExBIAzfEgxTqQc5thBhiVjl4A();

    private /* synthetic */ $$Lambda$SecretChatHelper$UCExBIAzfEgxTqQc5thBhiVjl4A() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SecretChatHelper.lambda$declineSecretChat$19(tLObject, tLRPC$TL_error);
    }
}
