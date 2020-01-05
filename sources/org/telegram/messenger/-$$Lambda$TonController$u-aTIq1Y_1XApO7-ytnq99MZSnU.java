package org.telegram.messenger;

import javax.crypto.Cipher;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.WordsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$u-aTIq1Y_1XApO7-ytnq99MZSnU implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ Cipher f$2;
    private final /* synthetic */ ErrorCallback f$3;
    private final /* synthetic */ WordsCallback f$4;

    public /* synthetic */ -$$Lambda$TonController$u-aTIq1Y_1XApO7-ytnq99MZSnU(TonController tonController, String str, Cipher cipher, ErrorCallback errorCallback, WordsCallback wordsCallback) {
        this.f$0 = tonController;
        this.f$1 = str;
        this.f$2 = cipher;
        this.f$3 = errorCallback;
        this.f$4 = wordsCallback;
    }

    public final void run() {
        this.f$0.lambda$getSecretWords$30$TonController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
