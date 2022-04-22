package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPhoneContact;
import org.telegram.ui.NewContactActivity;

public final /* synthetic */ class NewContactActivity$1$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ NewContactActivity.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_inputPhoneContact f$1;
    public final /* synthetic */ TLRPC$TL_contacts_importContacts f$2;

    public /* synthetic */ NewContactActivity$1$$ExternalSyntheticLambda2(NewContactActivity.AnonymousClass1 r1, TLRPC$TL_inputPhoneContact tLRPC$TL_inputPhoneContact, TLRPC$TL_contacts_importContacts tLRPC$TL_contacts_importContacts) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_inputPhoneContact;
        this.f$2 = tLRPC$TL_contacts_importContacts;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onItemClick$2(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
