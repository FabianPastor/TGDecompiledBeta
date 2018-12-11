package org.telegram.messenger;

import android.content.SharedPreferences.Editor;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$25 implements RequestDelegate {
    private final ContactsController arg$1;
    private final Editor arg$2;

    ContactsController$$Lambda$25(ContactsController contactsController, Editor editor) {
        this.arg$1 = contactsController;
        this.arg$2 = editor;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$reloadContactsStatuses$55$ContactsController(this.arg$2, tLObject, tL_error);
    }
}
