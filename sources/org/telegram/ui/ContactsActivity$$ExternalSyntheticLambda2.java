package org.telegram.ui;

import android.content.DialogInterface;
import android.widget.EditText;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsActivity$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ContactsActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ EditText f$2;

    public /* synthetic */ ContactsActivity$$ExternalSyntheticLambda2(ContactsActivity contactsActivity, TLRPC.User user, EditText editText) {
        this.f$0 = contactsActivity;
        this.f$1 = user;
        this.f$2 = editText;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3337lambda$didSelectResult$4$orgtelegramuiContactsActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
