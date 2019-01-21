package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate;

final /* synthetic */ class PrivacyControlActivity$$Lambda$3 implements GroupCreateActivityDelegate {
    private final PrivacyControlActivity arg$1;
    private final int arg$2;

    PrivacyControlActivity$$Lambda$3(PrivacyControlActivity privacyControlActivity, int i) {
        this.arg$1 = privacyControlActivity;
        this.arg$2 = i;
    }

    public void didSelectUsers(ArrayList arrayList) {
        this.arg$1.lambda$null$0$PrivacyControlActivity(this.arg$2, arrayList);
    }
}
