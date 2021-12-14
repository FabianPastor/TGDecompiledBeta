package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda21 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ HashMap f$2;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda21(ContactsController contactsController, ArrayList arrayList, HashMap hashMap) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = hashMap;
    }

    public final void run() {
        this.f$0.lambda$mergePhonebookAndTelegramContacts$40(this.f$1, this.f$2);
    }
}
