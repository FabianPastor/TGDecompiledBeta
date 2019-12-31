package org.telegram.messenger;

import drinkless.org.ton.TonApi.ExportedKey;
import org.telegram.messenger.TonController.WordsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$oAriRjh11c2Pb8qwcXa_NXDnoD8 implements Runnable {
    private final /* synthetic */ WordsCallback f$0;
    private final /* synthetic */ ExportedKey f$1;

    public /* synthetic */ -$$Lambda$TonController$oAriRjh11c2Pb8qwcXa_NXDnoD8(WordsCallback wordsCallback, ExportedKey exportedKey) {
        this.f$0 = wordsCallback;
        this.f$1 = exportedKey;
    }

    public final void run() {
        this.f$0.run(this.f$1.wordList);
    }
}
