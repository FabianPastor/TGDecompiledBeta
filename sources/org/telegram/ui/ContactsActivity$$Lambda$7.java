package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ContactsActivity$$Lambda$7 implements OnClickListener {
    private final ContactsActivity arg$1;
    private final String arg$2;

    ContactsActivity$$Lambda$7(ContactsActivity contactsActivity, String str) {
        this.arg$1 = contactsActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$ContactsActivity(this.arg$2, dialogInterface, i);
    }
}
