package org.telegram.ui;

import org.telegram.messenger.MrzRecognizer;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ MrzRecognizer.Result f$1;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda54(PassportActivity passportActivity, MrzRecognizer.Result result) {
        this.f$0 = passportActivity;
        this.f$1 = result;
    }

    public final void run() {
        this.f$0.lambda$processSelectedFiles$71(this.f$1);
    }
}