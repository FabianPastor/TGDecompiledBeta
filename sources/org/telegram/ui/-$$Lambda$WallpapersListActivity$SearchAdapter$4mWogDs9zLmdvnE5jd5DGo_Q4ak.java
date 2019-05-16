package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WallpapersListActivity$SearchAdapter$4mWogDs9zLmdvnE5jd5DGo_Q4ak implements RequestDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$WallpapersListActivity$SearchAdapter$4mWogDs9zLmdvnE5jd5DGo_Q4ak(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(tLObject, tL_error);
    }
}
