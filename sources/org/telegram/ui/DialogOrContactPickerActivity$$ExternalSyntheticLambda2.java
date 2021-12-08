package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ContactsActivity;

public final /* synthetic */ class DialogOrContactPickerActivity$$ExternalSyntheticLambda2 implements ContactsActivity.ContactsActivityDelegate {
    public final /* synthetic */ DialogOrContactPickerActivity f$0;

    public /* synthetic */ DialogOrContactPickerActivity$$ExternalSyntheticLambda2(DialogOrContactPickerActivity dialogOrContactPickerActivity) {
        this.f$0 = dialogOrContactPickerActivity;
    }

    public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
        this.f$0.m2834lambda$new$2$orgtelegramuiDialogOrContactPickerActivity(user, str, contactsActivity);
    }
}
