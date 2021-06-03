package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$bs26FKV-nUEyVDxZwVUmDJidFKk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$bs26FKVnUEyVDxZwVUmDJidFKk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$bs26FKVnUEyVDxZwVUmDJidFKk INSTANCE = new $$Lambda$MessagesController$bs26FKVnUEyVDxZwVUmDJidFKk();

    private /* synthetic */ $$Lambda$MessagesController$bs26FKVnUEyVDxZwVUmDJidFKk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePeerSettingsBar$47(tLObject, tLRPC$TL_error);
    }
}
