package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogOrContactPickerActivity$YMiYnz58wvar_gzOnJojoou4oa4s implements ContactsActivityDelegate {
    private final /* synthetic */ DialogOrContactPickerActivity f$0;

    public /* synthetic */ -$$Lambda$DialogOrContactPickerActivity$YMiYnz58wvar_gzOnJojoou4oa4s(DialogOrContactPickerActivity dialogOrContactPickerActivity) {
        this.f$0 = dialogOrContactPickerActivity;
    }

    public final void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.f$0.lambda$new$2$DialogOrContactPickerActivity(user, str, contactsActivity);
    }
}
