package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.NewContactActivity;

public final /* synthetic */ class NewContactActivity$1$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ NewContactActivity.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_inputPhoneContact f$1;
    public final /* synthetic */ TLRPC.TL_contacts_importContacts f$2;

    public /* synthetic */ NewContactActivity$1$$ExternalSyntheticLambda2(NewContactActivity.AnonymousClass1 r1, TLRPC.TL_inputPhoneContact tL_inputPhoneContact, TLRPC.TL_contacts_importContacts tL_contacts_importContacts) {
        this.f$0 = r1;
        this.f$1 = tL_inputPhoneContact;
        this.f$2 = tL_contacts_importContacts;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3322lambda$onItemClick$2$orgtelegramuiNewContactActivity$1(this.f$1, this.f$2, tLObject, tL_error);
    }
}
