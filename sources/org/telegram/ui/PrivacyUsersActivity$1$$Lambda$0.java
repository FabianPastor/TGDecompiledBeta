package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate;
import org.telegram.ui.PrivacyUsersActivity.AnonymousClass1;

final /* synthetic */ class PrivacyUsersActivity$1$$Lambda$0 implements GroupCreateActivityDelegate {
    private final AnonymousClass1 arg$1;

    PrivacyUsersActivity$1$$Lambda$0(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void didSelectUsers(ArrayList arrayList) {
        this.arg$1.lambda$onItemClick$0$PrivacyUsersActivity$1(arrayList);
    }
}
