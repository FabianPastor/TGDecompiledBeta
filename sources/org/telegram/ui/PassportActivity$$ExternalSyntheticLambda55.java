package org.telegram.ui;

import org.telegram.messenger.SecureDocument;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ SecureDocument f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda55(PassportActivity passportActivity, SecureDocument secureDocument, int i) {
        this.f$0 = passportActivity;
        this.f$1 = secureDocument;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$processSelectedFiles$70(this.f$1, this.f$2);
    }
}
