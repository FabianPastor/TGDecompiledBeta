package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.GroupCreateActivity.GroupCreateActivityDelegate;
import org.telegram.p005ui.PrivacyUsersActivity.C15531;

/* renamed from: org.telegram.ui.PrivacyUsersActivity$1$$Lambda$0 */
final /* synthetic */ class PrivacyUsersActivity$1$$Lambda$0 implements GroupCreateActivityDelegate {
    private final C15531 arg$1;

    PrivacyUsersActivity$1$$Lambda$0(C15531 c15531) {
        this.arg$1 = c15531;
    }

    public void didSelectUsers(ArrayList arrayList) {
        this.arg$1.lambda$onItemClick$0$PrivacyUsersActivity$1(arrayList);
    }
}
