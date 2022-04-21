package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ContactsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda91 implements ContactsActivity.ContactsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int[] f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda91(LaunchActivity launchActivity, boolean z, int[] iArr) {
        this.f$0 = launchActivity;
        this.f$1 = z;
        this.f$2 = iArr;
    }

    public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
        this.f$0.m2332lambda$handleIntent$16$orgtelegramuiLaunchActivity(this.f$1, this.f$2, user, str, contactsActivity);
    }
}
