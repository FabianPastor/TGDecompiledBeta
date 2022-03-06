package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ HashMap f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ HashMap f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ HashMap f$6;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda33(ContactsController contactsController, HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.f$0 = contactsController;
        this.f$1 = hashMap;
        this.f$2 = hashMap2;
        this.f$3 = z;
        this.f$4 = hashMap3;
        this.f$5 = arrayList;
        this.f$6 = hashMap4;
    }

    public final void run() {
        this.f$0.lambda$performSyncPhoneBook$24(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
