package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda24(ContactsController contactsController, ArrayList arrayList, boolean z, String str) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$deleteContact$54(this.f$1, this.f$2, this.f$3);
    }
}
