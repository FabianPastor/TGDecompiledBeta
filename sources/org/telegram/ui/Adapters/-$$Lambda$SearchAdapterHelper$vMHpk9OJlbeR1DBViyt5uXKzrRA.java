package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SearchAdapterHelper$vMHpk9OJlbeR1DBViyt5uXKzrRA implements RequestDelegate {
    private final /* synthetic */ SearchAdapterHelper f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ String f$6;

    public /* synthetic */ -$$Lambda$SearchAdapterHelper$vMHpk9OJlbeR1DBViyt5uXKzrRA(SearchAdapterHelper searchAdapterHelper, int i, int i2, boolean z, boolean z2, boolean z3, String str) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
        this.f$6 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$queryServerSearch$3$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
