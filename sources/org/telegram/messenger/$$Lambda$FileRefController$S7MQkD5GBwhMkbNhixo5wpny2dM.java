package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$FileRefController$S7MQkD5GBwhMkbNhixo5wpny2dM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FileRefController$S7MQkD5GBwhMkbNhixo5wpny2dM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FileRefController$S7MQkD5GBwhMkbNhixo5wpny2dM INSTANCE = new $$Lambda$FileRefController$S7MQkD5GBwhMkbNhixo5wpny2dM();

    private /* synthetic */ $$Lambda$FileRefController$S7MQkD5GBwhMkbNhixo5wpny2dM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$24(tLObject, tLRPC$TL_error);
    }
}
