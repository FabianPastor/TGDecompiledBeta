package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class BaseLocationAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ BaseLocationAdapter f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ BaseLocationAdapter$$ExternalSyntheticLambda2(BaseLocationAdapter baseLocationAdapter, TLObject tLObject) {
        this.f$0 = baseLocationAdapter;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$searchBotUser$2(this.f$1);
    }
}
