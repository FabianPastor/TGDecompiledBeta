package org.telegram.messenger;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ConcurrentHashMap f$2;
    public final /* synthetic */ HashMap f$3;
    public final /* synthetic */ HashMap f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ boolean f$8;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda23(ContactsController contactsController, ArrayList arrayList, ConcurrentHashMap concurrentHashMap, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2, ArrayList arrayList3, int i, boolean z) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = concurrentHashMap;
        this.f$3 = hashMap;
        this.f$4 = hashMap2;
        this.f$5 = arrayList2;
        this.f$6 = arrayList3;
        this.f$7 = i;
        this.f$8 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedContacts$33(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
