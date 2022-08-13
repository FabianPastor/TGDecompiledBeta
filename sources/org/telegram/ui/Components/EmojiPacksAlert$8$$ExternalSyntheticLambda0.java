package org.telegram.ui.Components;

import androidx.collection.LongSparseArray;
import org.telegram.ui.Components.EmojiPacksAlert;

public final /* synthetic */ class EmojiPacksAlert$8$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ EmojiPacksAlert.AnonymousClass8 f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ EmojiPacksAlert$8$$ExternalSyntheticLambda0(EmojiPacksAlert.AnonymousClass8 r1, LongSparseArray longSparseArray, int i) {
        this.f$0 = r1;
        this.f$1 = longSparseArray;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onSend$0(this.f$1, this.f$2);
    }
}
