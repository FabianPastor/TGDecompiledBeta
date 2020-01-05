package org.telegram.messenger;

import org.telegram.messenger.TonController.BytesCallback;
import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.WordsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$y4rzsuU0HEOkbZyV__GESg9CTVs implements BytesCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ ErrorCallback f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ String[] f$3;
    private final /* synthetic */ WordsCallback f$4;

    public /* synthetic */ -$$Lambda$TonController$y4rzsuU0HEOkbZyV__GESg9CTVs(TonController tonController, ErrorCallback errorCallback, boolean z, String[] strArr, WordsCallback wordsCallback) {
        this.f$0 = tonController;
        this.f$1 = errorCallback;
        this.f$2 = z;
        this.f$3 = strArr;
        this.f$4 = wordsCallback;
    }

    public final void run(byte[] bArr) {
        this.f$0.lambda$createWallet$13$TonController(this.f$1, this.f$2, this.f$3, this.f$4, bArr);
    }
}
