package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_help_inviteText;

final /* synthetic */ class ContactsController$$Lambda$59 implements Runnable {
    private final ContactsController arg$1;
    private final TL_help_inviteText arg$2;

    ContactsController$$Lambda$59(ContactsController contactsController, TL_help_inviteText tL_help_inviteText) {
        this.arg$1 = contactsController;
        this.arg$2 = tL_help_inviteText;
    }

    public void run() {
        this.arg$1.lambda$null$2$ContactsController(this.arg$2);
    }
}
