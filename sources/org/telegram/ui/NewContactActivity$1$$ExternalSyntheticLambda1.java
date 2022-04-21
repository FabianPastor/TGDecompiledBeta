package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.NewContactActivity;

public final /* synthetic */ class NewContactActivity$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ NewContactActivity.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_contacts_importedContacts f$1;
    public final /* synthetic */ TLRPC.TL_inputPhoneContact f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ TLRPC.TL_contacts_importContacts f$4;

    public /* synthetic */ NewContactActivity$1$$ExternalSyntheticLambda1(NewContactActivity.AnonymousClass1 r1, TLRPC.TL_contacts_importedContacts tL_contacts_importedContacts, TLRPC.TL_inputPhoneContact tL_inputPhoneContact, TLRPC.TL_error tL_error, TLRPC.TL_contacts_importContacts tL_contacts_importContacts) {
        this.f$0 = r1;
        this.f$1 = tL_contacts_importedContacts;
        this.f$2 = tL_inputPhoneContact;
        this.f$3 = tL_error;
        this.f$4 = tL_contacts_importContacts;
    }

    public final void run() {
        this.f$0.m2643lambda$onItemClick$1$orgtelegramuiNewContactActivity$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
