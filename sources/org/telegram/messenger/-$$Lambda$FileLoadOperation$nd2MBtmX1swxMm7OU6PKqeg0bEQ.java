package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$nd2MBtmX1swxMm7OU6PKqeg0bEQ implements RequestDelegate {
    private final /* synthetic */ FileLoadOperation f$0;

    public /* synthetic */ -$$Lambda$FileLoadOperation$nd2MBtmX1swxMm7OU6PKqeg0bEQ(FileLoadOperation fileLoadOperation) {
        this.f$0 = fileLoadOperation;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$requestFileOffsets$9$FileLoadOperation(tLObject, tL_error);
    }
}
