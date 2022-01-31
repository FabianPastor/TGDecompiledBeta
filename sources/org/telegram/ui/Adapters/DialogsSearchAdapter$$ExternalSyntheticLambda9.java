package org.telegram.ui.Adapters;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_searchGlobal;

public final /* synthetic */ class DialogsSearchAdapter$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ DialogsSearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ TLRPC$TL_messages_searchGlobal f$6;
    public final /* synthetic */ ArrayList f$7;

    public /* synthetic */ DialogsSearchAdapter$$ExternalSyntheticLambda9(DialogsSearchAdapter dialogsSearchAdapter, int i, int i2, TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, ArrayList arrayList) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tLRPC$TL_error;
        this.f$4 = str;
        this.f$5 = tLObject;
        this.f$6 = tLRPC$TL_messages_searchGlobal;
        this.f$7 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$searchMessagesInternal$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
