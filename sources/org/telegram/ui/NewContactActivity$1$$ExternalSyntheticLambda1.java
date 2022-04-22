package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC$TL_contacts_importedContacts;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPhoneContact;
import org.telegram.ui.NewContactActivity;

public final /* synthetic */ class NewContactActivity$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ NewContactActivity.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_contacts_importedContacts f$1;
    public final /* synthetic */ TLRPC$TL_inputPhoneContact f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;
    public final /* synthetic */ TLRPC$TL_contacts_importContacts f$4;

    public /* synthetic */ NewContactActivity$1$$ExternalSyntheticLambda1(NewContactActivity.AnonymousClass1 r1, TLRPC$TL_contacts_importedContacts tLRPC$TL_contacts_importedContacts, TLRPC$TL_inputPhoneContact tLRPC$TL_inputPhoneContact, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_contacts_importContacts tLRPC$TL_contacts_importContacts) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_contacts_importedContacts;
        this.f$2 = tLRPC$TL_inputPhoneContact;
        this.f$3 = tLRPC$TL_error;
        this.f$4 = tLRPC$TL_contacts_importContacts;
    }

    public final void run() {
        this.f$0.lambda$onItemClick$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
