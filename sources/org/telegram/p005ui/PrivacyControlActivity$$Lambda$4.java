package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.PrivacyUsersActivity.PrivacyActivityDelegate;

/* renamed from: org.telegram.ui.PrivacyControlActivity$$Lambda$4 */
final /* synthetic */ class PrivacyControlActivity$$Lambda$4 implements PrivacyActivityDelegate {
    private final PrivacyControlActivity arg$1;
    private final int arg$2;

    PrivacyControlActivity$$Lambda$4(PrivacyControlActivity privacyControlActivity, int i) {
        this.arg$1 = privacyControlActivity;
        this.arg$2 = i;
    }

    public void didUpdatedUserList(ArrayList arrayList, boolean z) {
        this.arg$1.lambda$null$1$PrivacyControlActivity(this.arg$2, arrayList, z);
    }
}
