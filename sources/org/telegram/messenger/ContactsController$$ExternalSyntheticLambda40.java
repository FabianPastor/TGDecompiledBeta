package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_help_inviteText;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC$TL_help_inviteText f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda40(ContactsController contactsController, TLRPC$TL_help_inviteText tLRPC$TL_help_inviteText) {
        this.f$0 = contactsController;
        this.f$1 = tLRPC$TL_help_inviteText;
    }

    public final void run() {
        this.f$0.lambda$checkInviteText$2(this.f$1);
    }
}
