package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.WallpapersListActivity;

public final /* synthetic */ class WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ WallpapersListActivity.SearchAdapter f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda2(WallpapersListActivity.SearchAdapter searchAdapter, TLObject tLObject) {
        this.f$0 = searchAdapter;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$searchBotUser$1(this.f$1);
    }
}
