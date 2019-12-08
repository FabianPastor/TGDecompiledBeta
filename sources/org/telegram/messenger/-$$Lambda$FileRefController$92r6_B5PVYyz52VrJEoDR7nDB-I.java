package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$92r6_B5PVYyz52VrJEoDR7nDB-I implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$FileRefController$92r6_B5PVYyz52VrJEoDR7nDB-I INSTANCE = new -$$Lambda$FileRefController$92r6_B5PVYyz52VrJEoDR7nDB-I();

    private /* synthetic */ -$$Lambda$FileRefController$92r6_B5PVYyz52VrJEoDR7nDB-I() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$22(tLObject, tL_error);
    }
}
