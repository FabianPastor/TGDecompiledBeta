package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ContactsActivity$$Lambda$2 implements OnClickListener {
    private final ContactsActivity arg$1;
    private final User arg$2;
    private final String arg$3;

    ContactsActivity$$Lambda$2(ContactsActivity contactsActivity, User user, String str) {
        this.arg$1 = contactsActivity;
        this.arg$2 = user;
        this.arg$3 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didSelectResult$3$ContactsActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
