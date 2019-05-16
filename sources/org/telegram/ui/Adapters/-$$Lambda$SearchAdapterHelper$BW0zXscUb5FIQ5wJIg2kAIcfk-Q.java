package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SearchAdapterHelper$BW0zXscUb5FIQ5wJIg2kAIcfk-Q implements Runnable {
    private final /* synthetic */ SearchAdapterHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ String f$7;

    public /* synthetic */ -$$Lambda$SearchAdapterHelper$BW0zXscUb5FIQ5wJIg2kAIcfk-Q(SearchAdapterHelper searchAdapterHelper, int i, TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, String str) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = z;
        this.f$5 = z2;
        this.f$6 = z3;
        this.f$7 = str;
    }

    public final void run() {
        this.f$0.lambda$null$2$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
