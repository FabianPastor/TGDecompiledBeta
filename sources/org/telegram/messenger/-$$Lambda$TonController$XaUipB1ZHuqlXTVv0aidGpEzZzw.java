package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.WordsCallback;
import org.telegram.tgnet.TLObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$XaUipB1ZHuqlXTVv0aidGpEzZzw implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ ErrorCallback f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ String[] f$4;
    private final /* synthetic */ WordsCallback f$5;

    public /* synthetic */ -$$Lambda$TonController$XaUipB1ZHuqlXTVv0aidGpEzZzw(TonController tonController, TLObject tLObject, ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback) {
        this.f$0 = tonController;
        this.f$1 = tLObject;
        this.f$2 = errorCallback;
        this.f$3 = z;
        this.f$4 = strArr;
        this.f$5 = wordsCallback;
    }

    public final void run() {
        this.f$0.lambda$null$11$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
