package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MentionsAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ MentionsAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ MentionsAdapter$$ExternalSyntheticLambda3(MentionsAdapter mentionsAdapter, String str, TLObject tLObject) {
        this.f$0 = mentionsAdapter;
        this.f$1 = str;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$searchServerStickers$0(this.f$1, this.f$2);
    }
}
