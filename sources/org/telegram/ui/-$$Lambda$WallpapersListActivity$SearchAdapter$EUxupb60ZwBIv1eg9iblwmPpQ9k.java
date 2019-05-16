package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WallpapersListActivity$SearchAdapter$EUxupb60ZwBIv1eg9iblwmPpQ9k implements RequestDelegate {
    private final /* synthetic */ SearchAdapter f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$WallpapersListActivity$SearchAdapter$EUxupb60ZwBIv1eg9iblwmPpQ9k(SearchAdapter searchAdapter, int i) {
        this.f$0 = searchAdapter;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchImages$4$WallpapersListActivity$SearchAdapter(this.f$1, tLObject, tL_error);
    }
}
