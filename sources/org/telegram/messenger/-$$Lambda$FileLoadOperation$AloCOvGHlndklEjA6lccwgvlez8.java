package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$AloCOvGHlndklEjA6lccwgvlez8 implements RequestDelegate {
    private final /* synthetic */ FileLoadOperation f$0;

    public /* synthetic */ -$$Lambda$FileLoadOperation$AloCOvGHlndklEjA6lccwgvlez8(FileLoadOperation fileLoadOperation) {
        this.f$0 = fileLoadOperation;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$requestFileOffsets$8$FileLoadOperation(tLObject, tL_error);
    }
}
