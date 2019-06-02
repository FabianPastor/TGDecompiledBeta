package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SearchAdapterHelper$v5cwP_i-1geBZNEja0OzJuPoFoM implements Runnable {
    private final /* synthetic */ SearchAdapterHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ -$$Lambda$SearchAdapterHelper$v5cwP_i-1geBZNEja0OzJuPoFoM(SearchAdapterHelper searchAdapterHelper, int i, TL_error tL_error, TLObject tLObject, String str, boolean z) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = str;
        this.f$5 = z;
    }

    public final void run() {
        this.f$0.lambda$null$0$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
