package org.telegram.messenger;

import android.content.SharedPreferences.Editor;
import org.telegram.tgnet.TLObject;

final /* synthetic */ class ContactsController$$Lambda$30 implements Runnable {
    private final ContactsController arg$1;
    private final Editor arg$2;
    private final TLObject arg$3;

    ContactsController$$Lambda$30(ContactsController contactsController, Editor editor, TLObject tLObject) {
        this.arg$1 = contactsController;
        this.arg$2 = editor;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$54$ContactsController(this.arg$2, this.arg$3);
    }
}
