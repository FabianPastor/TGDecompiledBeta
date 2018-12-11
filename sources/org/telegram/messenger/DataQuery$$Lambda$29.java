package org.telegram.messenger;

import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$29 implements RequestDelegate {
    private final DataQuery arg$1;
    private final int arg$2;
    private final int arg$3;
    private final BaseFragment arg$4;
    private final boolean arg$5;

    DataQuery$$Lambda$29(DataQuery dataQuery, int i, int i2, BaseFragment baseFragment, boolean z) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = baseFragment;
        this.arg$5 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$removeStickersSet$44$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
