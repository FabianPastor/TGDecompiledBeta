package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ContactsActivity$$Lambda$5 */
final /* synthetic */ class ContactsActivity$$Lambda$5 implements OnClickListener {
    private final ContactsActivity arg$1;
    private final String arg$2;

    ContactsActivity$$Lambda$5(ContactsActivity contactsActivity, String str) {
        this.arg$1 = contactsActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$ContactsActivity(this.arg$2, dialogInterface, i);
    }
}
