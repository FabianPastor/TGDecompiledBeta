package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsActivity$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ContactsActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ ContactsActivity$$ExternalSyntheticLambda3(ContactsActivity contactsActivity, TLRPC.User user, String str) {
        this.f$0 = contactsActivity;
        this.f$1 = user;
        this.f$2 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2035lambda$didSelectResult$3$orgtelegramuiContactsActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
