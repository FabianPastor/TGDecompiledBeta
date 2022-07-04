package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsSearchAdapter$$ExternalSyntheticLambda13 implements RequestDelegate {
    public final /* synthetic */ DialogsSearchAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC.TL_messages_searchGlobal f$4;

    public /* synthetic */ DialogsSearchAdapter$$ExternalSyntheticLambda13(DialogsSearchAdapter dialogsSearchAdapter, String str, int i, int i2, TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = tL_messages_searchGlobal;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2623xbfab1706(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
