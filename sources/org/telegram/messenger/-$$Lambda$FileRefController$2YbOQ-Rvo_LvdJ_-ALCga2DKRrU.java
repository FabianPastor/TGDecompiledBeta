package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$2YbOQ-Rvo_LvdJ_-ALCga2DKRrU implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$FileRefController$2YbOQ-Rvo_LvdJ_-ALCga2DKRrU INSTANCE = new -$$Lambda$FileRefController$2YbOQ-Rvo_LvdJ_-ALCga2DKRrU();

    private /* synthetic */ -$$Lambda$FileRefController$2YbOQ-Rvo_LvdJ_-ALCga2DKRrU() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$20(tLObject, tL_error);
    }
}
