package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M INSTANCE = new $$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M();

    private /* synthetic */ $$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        SecretChatHelper.lambda$declineSecretChat$19(tLObject, tL_error);
    }
}
