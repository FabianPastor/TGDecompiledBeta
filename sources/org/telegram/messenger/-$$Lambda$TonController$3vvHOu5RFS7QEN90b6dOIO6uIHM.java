package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.messenger.TonController.TonLibCallback;
import org.telegram.messenger.TonController.WordsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$3vvHOu5RFS7QEN90b6dOIO6uIHM implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ WordsCallback f$1;
    private final /* synthetic */ ErrorCallback f$2;

    public /* synthetic */ -$$Lambda$TonController$3vvHOu5RFS7QEN90b6dOIO6uIHM(TonController tonController, WordsCallback wordsCallback, ErrorCallback errorCallback) {
        this.f$0 = tonController;
        this.f$1 = wordsCallback;
        this.f$2 = errorCallback;
    }

    public final void run(Object obj) {
        this.f$0.lambda$null$27$TonController(this.f$1, this.f$2, obj);
    }
}
