package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.WallpapersListActivity;

public final /* synthetic */ class WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ WallpapersListActivity.SearchAdapter f$0;

    public /* synthetic */ WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda3(WallpapersListActivity.SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchBotUser$2(tLObject, tLRPC$TL_error);
    }
}
