package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ContactsActivity$$Lambda$3 implements OnClickListener {
    private final ContactsActivity arg$1;
    private final User arg$2;
    private final EditText arg$3;

    ContactsActivity$$Lambda$3(ContactsActivity contactsActivity, User user, EditText editText) {
        this.arg$1 = contactsActivity;
        this.arg$2 = user;
        this.arg$3 = editText;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didSelectResult$4$ContactsActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
