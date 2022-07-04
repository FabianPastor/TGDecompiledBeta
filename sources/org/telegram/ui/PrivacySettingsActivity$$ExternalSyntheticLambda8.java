package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_password;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ TLRPC$TL_account_password f$1;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda8(PrivacySettingsActivity privacySettingsActivity, TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = tLRPC$TL_account_password;
    }

    public final void run() {
        this.f$0.lambda$loadPasswordSettings$16(this.f$1);
    }
}
