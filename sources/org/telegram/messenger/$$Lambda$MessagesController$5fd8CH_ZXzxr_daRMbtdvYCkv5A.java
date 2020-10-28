package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$5fd8CH_ZXzxr_daRMbtdvYCkv5A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$5fd8CH_ZXzxr_daRMbtdvYCkv5A implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$5fd8CH_ZXzxr_daRMbtdvYCkv5A INSTANCE = new $$Lambda$MessagesController$5fd8CH_ZXzxr_daRMbtdvYCkv5A();

    private /* synthetic */ $$Lambda$MessagesController$5fd8CH_ZXzxr_daRMbtdvYCkv5A() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockPeer$58(tLObject, tLRPC$TL_error);
    }
}
