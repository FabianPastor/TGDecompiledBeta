package org.telegram.ui;

import org.telegram.tgnet.TLRPC$account_Password;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ TLRPC$account_Password f$1;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda10(PrivacySettingsActivity privacySettingsActivity, TLRPC$account_Password tLRPC$account_Password) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = tLRPC$account_Password;
    }

    public final void run() {
        this.f$0.lambda$loadPasswordSettings$18(this.f$1);
    }
}
