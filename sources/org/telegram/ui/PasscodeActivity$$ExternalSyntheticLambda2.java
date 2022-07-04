package org.telegram.ui;

public final /* synthetic */ class PasscodeActivity$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CodeNumberField f$0;

    public /* synthetic */ PasscodeActivity$$ExternalSyntheticLambda2(CodeNumberField codeNumberField) {
        this.f$0 = codeNumberField;
    }

    public final void run() {
        this.f$0.animateSuccessProgress(1.0f);
    }
}
