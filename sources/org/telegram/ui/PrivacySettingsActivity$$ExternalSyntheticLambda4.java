package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ TextCheckCell f$1;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda4(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = textCheckCell;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3675lambda$createView$8$orgtelegramuiPrivacySettingsActivity(this.f$1, tLObject, tL_error);
    }
}
