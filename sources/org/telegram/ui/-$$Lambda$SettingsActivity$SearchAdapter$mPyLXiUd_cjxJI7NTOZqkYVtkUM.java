package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SettingsActivity$SearchAdapter$mPyLXiUd_cjxJI7NTOZqkYVtkUM implements RequestDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$SettingsActivity$SearchAdapter$mPyLXiUd_cjxJI7NTOZqkYVtkUM(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadFaqWebPage$82$SettingsActivity$SearchAdapter(tLObject, tL_error);
    }
}
