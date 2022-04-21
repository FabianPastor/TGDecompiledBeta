package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda56 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda56(ContactsController contactsController, ArrayList arrayList, ArrayList arrayList2, boolean z, String str) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = z;
        this.f$4 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m16x6de2e0d4(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
