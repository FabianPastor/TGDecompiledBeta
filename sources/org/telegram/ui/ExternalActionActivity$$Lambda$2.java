package org.telegram.ui;

import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;

final /* synthetic */ class ExternalActionActivity$$Lambda$2 implements PasscodeViewDelegate {
    private final ExternalActionActivity arg$1;

    ExternalActionActivity$$Lambda$2(ExternalActionActivity externalActionActivity) {
        this.arg$1 = externalActionActivity;
    }

    public void didAcceptedPassword() {
        this.arg$1.lambda$showPasscodeActivity$2$ExternalActionActivity();
    }
}
