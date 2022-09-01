package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda15 implements RequestDelegate {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ TextCheckCell f$1;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda15(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = textCheckCell;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$10(this.f$1, tLObject, tLRPC$TL_error);
    }
}
