package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class DataSettingsActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ DataSettingsActivity f$0;

    public /* synthetic */ DataSettingsActivity$$ExternalSyntheticLambda5(DataSettingsActivity dataSettingsActivity) {
        this.f$0 = dataSettingsActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$4(tLObject, tLRPC$TL_error);
    }
}
