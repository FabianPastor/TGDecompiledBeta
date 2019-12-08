package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.WordsCallback;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$Sp7YTFNvZxWEIiCU697PpZ7K9s8 implements RequestDelegate {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ ErrorCallback f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ String[] f$3;
    private final /* synthetic */ WordsCallback f$4;

    public /* synthetic */ -$$Lambda$TonController$Sp7YTFNvZxWEIiCU697PpZ7K9s8(TonController tonController, ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback) {
        this.f$0 = tonController;
        this.f$1 = errorCallback;
        this.f$2 = z;
        this.f$3 = strArr;
        this.f$4 = wordsCallback;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$createWallet$13$TonController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
