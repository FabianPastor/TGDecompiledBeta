package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$boFYkk1Z7rFDh5xstB4qmGk9lo0 implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$boFYkk1Z7rFDh5xstB4qmGk9lo0(MediaDataController mediaDataController, int i, String str, String str2) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$117$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}