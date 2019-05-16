package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.ui.NewContactActivity.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NewContactActivity$1$WRq0Ss-PBCngsAibqDEMoSm52R4 implements RequestDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_inputPhoneContact f$1;
    private final /* synthetic */ TL_contacts_importContacts f$2;

    public /* synthetic */ -$$Lambda$NewContactActivity$1$WRq0Ss-PBCngsAibqDEMoSm52R4(AnonymousClass1 anonymousClass1, TL_inputPhoneContact tL_inputPhoneContact, TL_contacts_importContacts tL_contacts_importContacts) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_inputPhoneContact;
        this.f$2 = tL_contacts_importContacts;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onItemClick$2$NewContactActivity$1(this.f$1, this.f$2, tLObject, tL_error);
    }
}
