package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_searchGlobal;

public final /* synthetic */ class DialogsSearchAdapter$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ DialogsSearchAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC$TL_messages_searchGlobal f$4;

    public /* synthetic */ DialogsSearchAdapter$$ExternalSyntheticLambda21(DialogsSearchAdapter dialogsSearchAdapter, String str, int i, int i2, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = tLRPC$TL_messages_searchGlobal;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchMessagesInternal$1(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
