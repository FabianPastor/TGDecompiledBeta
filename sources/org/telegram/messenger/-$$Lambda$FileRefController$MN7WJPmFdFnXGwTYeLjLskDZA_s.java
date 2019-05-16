package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$MN7WJPmFdFnXGwTYeLjLskDZA_s implements RequestDelegate {
    private final /* synthetic */ FileRefController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$FileRefController$MN7WJPmFdFnXGwTYeLjLskDZA_s(FileRefController fileRefController, String str, String str2) {
        this.f$0 = fileRefController;
        this.f$1 = str;
        this.f$2 = str2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$requestReferenceFromServer$6$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
