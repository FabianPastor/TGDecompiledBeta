package org.telegram.ui.Adapters;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsSearchAdapter$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ DialogsSearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ TLRPC.TL_messages_searchGlobal f$6;
    public final /* synthetic */ ArrayList f$7;

    public /* synthetic */ DialogsSearchAdapter$$ExternalSyntheticLambda15(DialogsSearchAdapter dialogsSearchAdapter, int i, int i2, TLRPC.TL_error tL_error, String str, TLObject tLObject, TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal, ArrayList arrayList) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tL_error;
        this.f$4 = str;
        this.f$5 = tLObject;
        this.f$6 = tL_messages_searchGlobal;
        this.f$7 = arrayList;
    }

    public final void run() {
        this.f$0.m1367x4a30f0c5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
