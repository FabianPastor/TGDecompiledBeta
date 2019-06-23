package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$QmWQvfhojhGVYrK_adnpMlcs7ZY implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$QmWQvfhojhGVYrK_adnpMlcs7ZY(MediaDataController mediaDataController, TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$null$29$MediaDataController(this.f$1, this.f$2, this.f$3);
    }
}
