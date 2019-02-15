package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class WallpapersListActivity$SearchAdapter$$Lambda$2 implements RequestDelegate {
    private final SearchAdapter arg$1;
    private final int arg$2;

    WallpapersListActivity$SearchAdapter$$Lambda$2(SearchAdapter searchAdapter, int i) {
        this.arg$1 = searchAdapter;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchImages$4$WallpapersListActivity$SearchAdapter(this.arg$2, tLObject, tL_error);
    }
}
