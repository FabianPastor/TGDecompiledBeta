package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SettingsActivity$SearchAdapter$$Lambda$81 implements RequestDelegate {
    private final SearchAdapter arg$1;

    SettingsActivity$SearchAdapter$$Lambda$81(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadFaqWebPage$81$SettingsActivity$SearchAdapter(tLObject, tL_error);
    }
}
