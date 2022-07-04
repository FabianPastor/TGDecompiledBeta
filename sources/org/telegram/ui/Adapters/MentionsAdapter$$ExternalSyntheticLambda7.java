package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MentionsAdapter$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ MentionsAdapter f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ MentionsAdapter$$ExternalSyntheticLambda7(MentionsAdapter mentionsAdapter, String str) {
        this.f$0 = mentionsAdapter;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchServerStickers$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
