package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.ui.NewContactActivity.AnonymousClass1;

final /* synthetic */ class NewContactActivity$1$$Lambda$0 implements RequestDelegate {
    private final AnonymousClass1 arg$1;
    private final TL_inputPhoneContact arg$2;
    private final TL_contacts_importContacts arg$3;

    NewContactActivity$1$$Lambda$0(AnonymousClass1 anonymousClass1, TL_inputPhoneContact tL_inputPhoneContact, TL_contacts_importContacts tL_contacts_importContacts) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_inputPhoneContact;
        this.arg$3 = tL_contacts_importContacts;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onItemClick$2$NewContactActivity$1(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
