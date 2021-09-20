package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.WallpapersListActivity;

public final /* synthetic */ class WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ WallpapersListActivity.SearchAdapter f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda4(WallpapersListActivity.SearchAdapter searchAdapter, int i) {
        this.f$0 = searchAdapter;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchImages$4(this.f$1, tLObject, tLRPC$TL_error);
    }
}
