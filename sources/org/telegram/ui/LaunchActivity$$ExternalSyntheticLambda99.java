package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ContactsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda99 implements ContactsActivity.ContactsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int[] f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda99(LaunchActivity launchActivity, boolean z, int[] iArr) {
        this.f$0 = launchActivity;
        this.f$1 = z;
        this.f$2 = iArr;
    }

    public final void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        this.f$0.lambda$handleIntent$19(this.f$1, this.f$2, tLRPC$User, str, contactsActivity);
    }
}
