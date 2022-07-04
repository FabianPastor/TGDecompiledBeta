package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ContactsActivity;

public final /* synthetic */ class CallLogActivity$$ExternalSyntheticLambda1 implements ContactsActivity.ContactsActivityDelegate {
    public final /* synthetic */ CallLogActivity f$0;

    public /* synthetic */ CallLogActivity$$ExternalSyntheticLambda1(CallLogActivity callLogActivity) {
        this.f$0 = callLogActivity;
    }

    public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
        this.f$0.m2745lambda$createView$2$orgtelegramuiCallLogActivity(user, str, contactsActivity);
    }
}
