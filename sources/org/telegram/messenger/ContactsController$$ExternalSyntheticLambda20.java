package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda20(ContactsController contactsController, ArrayList arrayList, int i, ArrayList arrayList2) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedContacts$38(this.f$1, this.f$2, this.f$3);
    }
}
