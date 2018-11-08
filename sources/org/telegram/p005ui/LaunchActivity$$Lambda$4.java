package org.telegram.p005ui;

import org.telegram.p005ui.Components.PasscodeView.PasscodeViewDelegate;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$4 */
final /* synthetic */ class LaunchActivity$$Lambda$4 implements PasscodeViewDelegate {
    private final LaunchActivity arg$1;

    LaunchActivity$$Lambda$4(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void didAcceptedPassword() {
        this.arg$1.lambda$showPasscodeActivity$4$LaunchActivity();
    }
}
