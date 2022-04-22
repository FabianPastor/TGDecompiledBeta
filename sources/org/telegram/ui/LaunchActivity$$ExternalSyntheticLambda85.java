package org.telegram.ui;

import org.telegram.ui.ActionIntroActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda85 implements ActionIntroActivity.ActionIntroQRLoginDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ ActionIntroActivity f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda85(LaunchActivity launchActivity, ActionIntroActivity actionIntroActivity) {
        this.f$0 = launchActivity;
        this.f$1 = actionIntroActivity;
    }

    public final void didFindQRCode(String str) {
        this.f$0.lambda$handleIntent$20(this.f$1, str);
    }
}
