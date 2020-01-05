package org.telegram.messenger;

import drinkless.org.ton.TonApi.Key;
import org.telegram.messenger.TonController.WordsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$uMjPBMNMREKI79Tm2gV7lkxaL0k implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ Key f$1;
    private final /* synthetic */ byte[] f$2;
    private final /* synthetic */ String[] f$3;
    private final /* synthetic */ WordsCallback f$4;

    public /* synthetic */ -$$Lambda$TonController$uMjPBMNMREKI79Tm2gV7lkxaL0k(TonController tonController, Key key, byte[] bArr, String[] strArr, WordsCallback wordsCallback) {
        this.f$0 = tonController;
        this.f$1 = key;
        this.f$2 = bArr;
        this.f$3 = strArr;
        this.f$4 = wordsCallback;
    }

    public final void run() {
        this.f$0.lambda$onFinishWalletCreate$1$TonController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
